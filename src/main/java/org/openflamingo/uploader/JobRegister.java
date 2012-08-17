package org.openflamingo.uploader;

import org.openflamingo.uploader.el.ELEvaluator;
import org.openflamingo.uploader.el.ELService;
import org.openflamingo.uploader.exception.SystemException;
import org.openflamingo.uploader.jaxb.*;
import org.openflamingo.uploader.jaxb.Job;
import org.openflamingo.uploader.util.*;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Flamingo HDFS File Uploader Job을 정의한 XML 파일을 실제로 동작하기 위해서
 * Scheduler에 등록하는 Job Register.
 *
 * @author Edward KIM
 * @since 0.2
 */
@Component
public class JobRegister implements InitializingBean, ApplicationContextAware {

	/**
	 * SLF4J Logging
	 */
	private Logger logger = LoggerFactory.getLogger(JobRegister.class);

	@Autowired
	private Scheduler scheduler;

	@Autowired
	private ELService elService;

	/**
	 * Spring Framework Application Context
	 */
	private ApplicationContext applicationContext;

	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info("Flamingo HDFS File Uploader Job 등록을 시작합니다.");

		String xml = ResourceUtils.getResourceTextContents("classpath:job.xml");
		logger.debug("Uploader Job이 등록되어 있는 XML 파일을 로딩하였습니다.\n{}", xml);

		Flamingo model = (Flamingo) JaxbUtils.unmarshal("org.openflamingo.uploader.jaxb", xml);
		logger.info("job.xml 파일을 CLASSPATH에서 로딩하였습니다.");

		JobContext jobContext = new JobContextImpl(model, getEvaluator(model, elService));

		if (model.getJob() == null || model.getJob().size() < 1) {
			throw new IllegalArgumentException("job.xml 파일에 Job이 등록되어 있지 않습니다.");
		}

		Iterator<Job> iterator = model.getJob().iterator();
		while (iterator.hasNext()) {
			Job job = iterator.next();
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("model", model);
			dataMap.put("job", job);
			dataMap.put("context", jobContext);
			dataMap.put("spring", applicationContext);
			String cronExpression = job.getSchedule().getCronExpression();
			Date start = this.getStart(jobContext, job.getSchedule().getStart());
			Date end = this.getEnd(jobContext, job.getSchedule().getEnd());

			logger.info("Uploader Job '{}'을 Cron Expression '{}'으로 시작일 '{}', 종료일 '{}'으로 등록합니다.", new Object[]{job.getName(), cronExpression, start, end});

			if (job.getSchedule().getStart() == null || job.getSchedule().getEnd() == null) {
				logger.info("시작 날짜 및 종료 날짜가 설정되어 있지 않으므로 즉시 시작합니다.");
				startJobImmediatly(job.getName(), job.getName(), cronExpression, dataMap);
			} else {
				logger.info("시작 날짜 및 종료 날짜가 설정되어 있습니다.");
				startJob(job.getName(), job.getName(), cronExpression, start, end, dataMap);
			}
			logger.info("스케줄링을 완료하였습니다. 이제부터 정해진 시간에 스케줄링이 진행됩니다.");
		}
	}

	private ELEvaluator getEvaluator(Flamingo model, ELService service) throws Exception {
		ELEvaluator evaluator = service.createEvaluator();
		if (model.getGlobalVariables() != null) {
			List<GlobalVariable> vars = model.getGlobalVariables().getGlobalVariable();
			for (GlobalVariable var : vars) {
				evaluator.setVariable(var.getName(), evaluator.evaluate(var.getValue(), String.class));
			}
		}
		return evaluator;
	}

	private Date getEnd(JobContext jobContext, End end) {
		String date = jobContext.getValue(end.getDate());
		String datePattern = end.getDatePattern();
		return getDate(date, datePattern);
	}

	private Date getStart(JobContext jobContext, Start start) {
		String date = jobContext.getValue(start.getDate());
		String datePattern = start.getDatePattern();
		return getDate(date, datePattern);
	}

	private Date getDate(String date, String datePattern) {
		if (StringUtils.isEmpty(date) || StringUtils.isEmpty(datePattern)) {
			return null;
		}
		try {
			return DateUtils.parseDate(date, new String[]{datePattern});
		} catch (Exception ex) {
			throw new IllegalArgumentException(ExceptionUtils.getMessage("날짜 '{}'을 패턴 '{}'을 적용하여 해석할 수 없습니다.", date, datePattern));
		}
	}

	public JobKey startJob(String jobName, String jobGroupName, String cronExpression, Date start, Date end, Map<String, Object> dataMap) {
		try {
			JobKey jobKey = new JobKey(jobName, jobGroupName);
			JobDetail job = JobBuilder.newJob(QuartzJob.class).withIdentity(jobKey).build();
			job.getJobDataMap().putAll(dataMap);
			logger.info("새로운 배치 작업을 등록하기 위해 배치 작업을 생성하였습니다.");

			TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
				.withIdentity(jobName, jobGroupName)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression));
			if (start != null) triggerBuilder.startAt(start);
			if (end != null) triggerBuilder.endAt(end);
			CronTrigger trigger = triggerBuilder.build();
			logger.info("등록한 배치 작업의 실행 주기를 Cron Expression '{}'으로 등록하였습니다.", cronExpression);

			scheduler.scheduleJob(job, trigger);
			logger.info("Job '{}' Group '{}' 으로 배치 작업 등록이 완료되었습니다. 작업이 등록되면 해당 시간에 즉시 동작하게 됩니다.", jobName, jobGroupName);
			return jobKey;
		} catch (SchedulerException e) {
			String message = MessageFormatter.format("Job '{}' Group '{}' 작업을 스케줄러에 등록할 수 없습니다.", jobName, jobGroupName).getMessage();
			throw new SystemException(message, e);
		}
	}

	public JobKey startJobImmediatly(String jobName, String jobGroupName, String cronExpression, Map<String, Object> dataMap) {
		try {
			JobKey jobKey = new JobKey(jobName, jobGroupName);
			JobDetail job = JobBuilder.newJob(QuartzJob.class).withIdentity(jobKey).build();
			job.getJobDataMap().putAll(dataMap);
			logger.info("새로운 배치 작업을 등록하기 위해 배치 작업을 생성하였습니다.");

			SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
				.withIdentity(jobName, jobGroupName)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.startNow()
				.forJob(jobName, jobGroupName)
				.build();

			logger.info("등록한 배치 작업은 즉시 실행하도록 등록하였습니다.");

			scheduler.scheduleJob(job, trigger);
			logger.info("Job '{}' Group '{}' 으로 배치 작업 등록이 완료되었습니다. 작업이 등록되면 해당 시간에 즉시 동작하게 됩니다.", jobName, jobGroupName);
			return jobKey;
		} catch (SchedulerException e) {
			String message = MessageFormatter.format("Job '{}' Group '{}' 작업을 스케줄러에 등록할 수 없습니다.", jobName, jobGroupName).getMessage();
			throw new SystemException(message, e);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}

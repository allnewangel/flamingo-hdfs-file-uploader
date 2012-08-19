/**
 * Flamingo HDFS File Uploader - a tool to upload from datasource to datasource and schedule jobs
 *
 * Copyright (C) 2011-2012 Cloudine.
 *
 * This file is part of Flamingo HDFS File Uploader.
 *
 * Flamingo HDFS File Uploader is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Flamingo HDFS File Uploader is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openflamingo.uploader;

import org.openflamingo.uploader.el.ELEvaluator;
import org.openflamingo.uploader.el.ELService;
import org.openflamingo.uploader.exception.SystemException;
import org.openflamingo.uploader.jaxb.*;
import org.openflamingo.uploader.jaxb.Job;
import org.openflamingo.uploader.util.DateUtils;
import org.openflamingo.uploader.util.ExceptionUtils;
import org.openflamingo.uploader.util.StringUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @since 0.1
 */
@Component
public class JobRegister implements InitializingBean, ApplicationContextAware {

    /**
     * SLF4J Logging
     */
    private Logger logger = LoggerFactory.getLogger(JobRegister.class);

    /**
     * Quartz Job Scheduler
     */
    @Autowired
    private Scheduler scheduler;

    /**
     * Expression Language(EL) Service
     */
    @Autowired
    private ELService elService;

    /**
     * HDFS File Uploader Job XML의 JAXB ROOT Object
     */
    @Autowired
    private Flamingo model;

    /**
     * Spring Framework Application Context
     */
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("=================================================================================");
        logger.info("Flamingo HDFS File Uploader Job XML 파일에 정의되어 있는 Job을 스케줄러에 등록을 시작합니다.");
        logger.info("=================================================================================");

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
            Date start = job.getSchedule().getStart() == null ? null : this.getStart(jobContext, job.getSchedule().getStart());
            Date end = job.getSchedule().getEnd() == null ? null : this.getEnd(jobContext, job.getSchedule().getEnd());
            String misfireInstruction = job.getSchedule().getMisfireInstructions() == null ? null : job.getSchedule().getMisfireInstructions().getType();
            int triggerPriority = job.getSchedule().getTriggerPriority() == null ? Trigger.DEFAULT_PRIORITY : job.getSchedule().getTriggerPriority().intValue();
            String timezone = job.getSchedule().getTimezone() == null ? null : job.getSchedule().getTimezone();

            logger.info("Job '{}'을 Cron Expression '{}'으로 등록합니다.", new Object[]{job.getName(), cronExpression, start, end});

            startJob(jobContext, job.getName(), job.getName(), cronExpression, start, end, misfireInstruction, triggerPriority, timezone, dataMap);

            logger.info("Job '{}'을 등록하였습니다.", new Object[]{job.getName(), cronExpression, start, end});
        }
        logger.info("=================================================================================");
        logger.info("Flamingo HDFS Uploader Job XML에 정의되어 있는 모든 Job을 스케줄링 하였습니다.");
        logger.info("=================================================================================");
    }

    /**
     * Misfire Instruction을 설정한다. 기본값은 Smart Policy이다.
     *
     * @param scheduleBuilder    Cron Schedule Builder
     * @param misfireInstruction Misfire Instruction 문자열
     */
    private void setMisfireInstruction(CronScheduleBuilder scheduleBuilder, String misfireInstruction) {
        if (StringUtils.isEmpty(misfireInstruction)) {
            logger.info("Cron 스케줄링의 Misfire Instruction이 지정되어 있지 않아서 MISFIRE_INSTRUCTION_SMART_POLICY 정책을 적용했습니다.");
            return;
        }
        if ("MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY".equals(misfireInstruction)) {
            logger.info("Cron 스케줄링의 Misfire Instruction으로 MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY 정책을 적용했습니다.");
            scheduleBuilder.withMisfireHandlingInstructionDoNothing();
        } else if ("MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY".equals(misfireInstruction)) {
            scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
            logger.info("Cron 스케줄링의 Misfire Instruction으로 MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY 정책을 적용했습니다.");
        } else if ("MISFIRE_INSTRUCTION_FIRE_ONCE_NOW".equals(misfireInstruction)) {
            scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
            logger.info("Cron 스케줄링의 Misfire Instruction으로 MISFIRE_INSTRUCTION_FIRE_ONCE_NOW 정책을 적용했습니다.");
        }
    }

    /**
     * 우선순위를 반환한다. 기본 우선순위는 {@link org.quartz.Trigger#DEFAULT_PRIORITY}이며 값이 유효하지 않은 경우 기본값을 적용한다.
     *
     * @param priority 우선순위 (예; {@link org.quartz.Trigger#DEFAULT_PRIORITY})
     * @return 우선순위
     */
    private int getTriggerPriority(int priority) {
        if (priority > 0 && priority < 5) {
            logger.info("Cron 스케줄링의 우선순위는 {} 입니다.", priority);
            return priority;
        }
        logger.info("Cron 스케줄링의 우선순위는 {} 입니다.", Trigger.DEFAULT_PRIORITY);
        return Trigger.DEFAULT_PRIORITY;
    }

    /**
     * 스케줄링 정보에 등록되어 있는 Timezone을 설정한다. 기본 Timezone은 <tt>Asia/Seoul</tt> 이다.
     *
     * @param jobContext      Job Context
     * @param scheduleBuilder Cron Schedule Builder
     * @param timezone        Timezone 문자열
     */
    private void setTimezone(JobContext jobContext, CronScheduleBuilder scheduleBuilder, String timezone) {
        if (StringUtils.isEmpty(timezone)) {
            logger.info("Cron 스케줄링의 Timezone은 {}입니다.", "Asia/Seoul");
            scheduleBuilder.inTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            return;
        }
        String evaluated = null;
        try {
            evaluated = jobContext.getValue(timezone);
            if (StringUtils.isEmpty(timezone)) {
                logger.info("Cron 스케줄링의 Timezone은 {}입니다.", "Asia/Seoul");
                scheduleBuilder.inTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            } else {
                logger.info("Cron 스케줄링의 Timezone은 {}입니다.", evaluated);
                scheduleBuilder.inTimeZone(TimeZone.getTimeZone(evaluated));
            }
        } catch (Exception ex) {
            logger.info("Cron 스케줄링의 Timezone은 {}입니다.", "Asia/Seoul");
            scheduleBuilder.inTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        }
    }

    /**
     * 글로별 변수에 포함되어 있는 EL을 처리하기 위해서 글로별 변수로 정의되어 있는 Key Value를 Expression Language Evaluator에 변수로 설정하고
     * EL을 해석하는 EL Evaluator를 반환한다.
     *
     * @param model   Flamingo HDFS File Uploader JAXB Object
     * @param service EL Service
     * @return EL Evaluator
     * @throws Exception EL Evaluator가 변수를 처리할 수 없는 경우
     */
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

    /**
     * 스케줄링 작업의 시작 시간을 반환한다.
     *
     * @param jobContext Job Context
     * @param start      Schedule 정보의 시작 시간
     * @return {@link java.util.Date} 객체
     */
    private Date getStart(JobContext jobContext, Start start) {
        String date = jobContext.getValue(start.getDate());
        String datePattern = start.getDatePattern();
        return getDate(date, datePattern);
    }

    /**
     * 스케줄링 작업의 종료 시간을 반환한다.
     *
     * @param jobContext Job Context
     * @param end        Schedule 정보의 종료 시간
     * @return {@link java.util.Date} 객체
     */
    private Date getEnd(JobContext jobContext, End end) {
        String date = jobContext.getValue(end.getDate());
        String datePattern = end.getDatePattern();
        return getDate(date, datePattern);
    }

    /**
     * 문자열 날짜와 패턴을 이용하여 {@link java.util.Date}을 생성한다.
     *
     * @param date        문자열 날짜
     * @param datePattern Simple Date Format Pattern
     * @return {@link java.util.Date} 객체
     */
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

    /**
     * Quartz Job을 스케줄링한다.
     *
     * @param jobContext         Job Context
     * @param jobName            Quartz Job Name
     * @param jobGroupName       Quartz Job Group Name
     * @param cronExpression     Cron Expression
     * @param start              Start
     * @param end                End
     * @param misfireInstruction Misfire Instrudction
     * @param triggerPriority    Trigger Priority
     * @param timezone           Timezone
     * @param dataMap            Key Value Parameter Map  @return Job Key
     */
    public JobKey startJob(JobContext jobContext, String jobName, String jobGroupName, String cronExpression, Date start, Date end, String misfireInstruction, int triggerPriority, String timezone, Map<String, Object> dataMap) {
        try {
            JobKey jobKey = new JobKey(jobName, jobGroupName);
            TriggerKey triggerKey = new TriggerKey(jobName, TriggerKey.DEFAULT_GROUP);
            JobDetail job = JobBuilder.newJob(QuartzJob.class).withIdentity(jobKey).build();
            job.getJobDataMap().putAll(dataMap);

            CronScheduleBuilder schedBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            setMisfireInstruction(schedBuilder, misfireInstruction);
            setTimezone(jobContext, schedBuilder, timezone);

            Date jobStartDate = new Date();

            TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .withSchedule(schedBuilder)
                .withPriority(getTriggerPriority(triggerPriority))
                .forJob(jobKey);

            if (start != null) {
                // 시작 시간을 설정한 경우 XML의 시작 시간을 사용한다.
                logger.info("Cron 스케줄링의 시작 시간은 {}입니다.", start);
                triggerBuilder.startAt(start);
            } else {
                // 시작시간이 설정되어 있지 않다면 현재 시간부로 시작한다.
                logger.info("Cron 스케줄링의 시작 시간은 {}입니다.", DateUtils.addSeconds(jobStartDate, 5));
                triggerBuilder.startAt(DateUtils.addSeconds(jobStartDate, 5));
            }

            if (end != null) {
                // 종료 시간을 설정한 경우 XML의 종료 시간을 사용한다.
                if (DateUtils.getDiffSeconds(end, jobStartDate) < 0) {
                    throw new SystemException("종료 시간이 현재 시간보다 과거 시간이므로 스케줄링할 수 없습니다.");
                }

                logger.info("Cron 스케줄링의 종료 시간은 {}입니다.", end);
                triggerBuilder.endAt(end);
            } else {
                logger.info("Cron 스케줄링의 종료 시간이 설정되어 있지 않습니다.");
            }

            CronTrigger trigger = triggerBuilder.build();
            scheduler.scheduleJob(job, trigger);
            return jobKey;
        } catch (SchedulerException e) {
            throw new SystemException(ExceptionUtils.getMessage("Job '{}' 을 스케줄러에 등록할 수 없습니다.", jobName, jobGroupName), e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

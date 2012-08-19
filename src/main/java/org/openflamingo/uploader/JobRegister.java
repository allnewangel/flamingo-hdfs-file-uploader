/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * @since 0.2
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

    @Autowired
    private Flamingo model;

    /**
     * Spring Framework Application Context
     */
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("Flamingo HDFS File Uploader Job 등록을 시작합니다.");

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
            String misfireInstruction = job.getSchedule().getMisfireInstructions() == null ? null : job.getSchedule().getMisfireInstructions().getType();
            int triggerPriority = job.getSchedule().getTriggerPriority().intValue();
            String timezone = job.getSchedule().getTimezone() == null ? null : job.getSchedule().getTimezone();


            logger.info("Uploader Job '{}'을 Cron Expression '{}'으로 시작일 '{}', 종료일 '{}'으로 등록합니다.", new Object[]{job.getName(), cronExpression, start, end});

            if (job.getSchedule().getStart() == null || job.getSchedule().getEnd() == null) {
                logger.info("시작 날짜 및 종료 날짜가 설정되어 있지 않으므로 즉시 시작합니다.");
                startJobImmediatly(jobContext, job.getName(), job.getName(), cronExpression, misfireInstruction, triggerPriority, timezone, dataMap);
            } else {
                logger.info("시작 날짜 및 종료 날짜가 설정되어 있습니다.");
                startJob(jobContext, job.getName(), job.getName(), cronExpression, start, end, misfireInstruction, triggerPriority, timezone, dataMap);
            }
            logger.info("스케줄링을 완료하였습니다. 이제부터 정해진 시간에 스케줄링이 진행됩니다.");
        }
    }

    /**
     * Misfire Instruction을 설정한다. 기본값은 Smart Policy이다.
     *
     * @param scheduleBuilder    Cron Schedule Builder
     * @param misfireInstruction Misfire Instruction 문자열
     */
    private void setMisfireInstruction(CronScheduleBuilder scheduleBuilder, String misfireInstruction) {
        if (StringUtils.isEmpty(misfireInstruction)) {
            return;
        }
        if ("MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY".equals(misfireInstruction)) {
            scheduleBuilder.withMisfireHandlingInstructionDoNothing();
        } else if ("MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY".equals(misfireInstruction)) {
            scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
        } else if ("MISFIRE_INSTRUCTION_FIRE_ONCE_NOW".equals(misfireInstruction)) {
            scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
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
            return priority;
        }
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
            scheduleBuilder.inTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            return;
        }
        String evaluated = null;
        try {
            evaluated = jobContext.getValue(timezone);
            if (StringUtils.isEmpty(timezone)) {
                scheduleBuilder.inTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            } else {
                scheduleBuilder.inTimeZone(TimeZone.getTimeZone(evaluated));
            }
        } catch (Exception ex) {
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
            JobDetail job = JobBuilder.newJob(QuartzJob.class).withIdentity(jobKey).build();
            job.getJobDataMap().putAll(dataMap);
            logger.info("새로운 배치 작업을 등록하기 위해 배치 작업을 생성하였습니다.");

            CronScheduleBuilder schedBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            setMisfireInstruction(schedBuilder, misfireInstruction);
            setTimezone(jobContext, schedBuilder, timezone);

            TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(jobName, jobGroupName)
                .withSchedule(schedBuilder)
                .withPriority(getTriggerPriority(triggerPriority))
                .forJob(jobName, jobGroupName);
            if (start != null) triggerBuilder.startAt(start);
            if (end != null) triggerBuilder.endAt(end);
            CronTrigger trigger = triggerBuilder.build();
            logger.info("등록한 배치 작업의 실행 주기를 Cron Expression '{}'으로 등록하였습니다.", cronExpression);

            scheduler.scheduleJob(job, trigger);
            logger.info("Job '{}' Group '{}' 으로 배치 작업 등록이 완료되었습니다. 작업이 등록되면 해당 시간에 즉시 동작하게 됩니다.", jobName, jobGroupName);
            return jobKey;
        } catch (SchedulerException e) {
            throw new SystemException(ExceptionUtils.getMessage("Job '{}' Group '{}' 작업을 스케줄러에 등록할 수 없습니다.", jobName, jobGroupName), e);
        }
    }

    /**
     * Quartz Job을 스케줄링한다.
     *
     * @param jobContext         Job Context
     * @param jobName            Quartz Job Name
     * @param jobGroupName       Quartz Job Group Name
     * @param cronExpression     Cron Expression
     * @param misfireInstruction Misfire Instrudction
     * @param triggerPriority    Trigger Priority
     * @param timezone           Timezone
     * @param dataMap            Key Value Parameter Map     @return Job Key
     */
    public JobKey startJobImmediatly(JobContext jobContext, String jobName, String jobGroupName, String cronExpression, String misfireInstruction, int triggerPriority, String timezone, Map<String, Object> dataMap) {
        try {
            JobKey jobKey = new JobKey(jobName, jobGroupName);
            JobDetail job = JobBuilder.newJob(QuartzJob.class).withIdentity(jobKey).build();
            job.getJobDataMap().putAll(dataMap);
            logger.info("새로운 배치 작업을 등록하기 위해 배치 작업을 생성하였습니다.");

            CronScheduleBuilder schedBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            setMisfireInstruction(schedBuilder, misfireInstruction);
            setTimezone(jobContext, schedBuilder, timezone);

            SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                .withIdentity(jobName, jobGroupName)
                .withSchedule(schedBuilder)
                .withPriority(getTriggerPriority(triggerPriority))
                .startNow()
                .forJob(jobName, jobGroupName)
                .build();

            logger.info("등록한 배치 작업은 즉시 실행하도록 등록하였습니다.");

            scheduler.scheduleJob(job, trigger);
            logger.info("Job '{}' Group '{}' 으로 배치 작업 등록이 완료되었습니다. 작업이 등록되면 해당 시간에 즉시 동작하게 됩니다.", jobName, jobGroupName);
            return jobKey;
        } catch (SchedulerException e) {
            throw new SystemException(ExceptionUtils.getMessage("Job '{}' Group '{}' 작업을 스케줄러에 등록할 수 없습니다.", jobName, jobGroupName), e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

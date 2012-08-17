package org.openflamingo.uploader;

import org.openflamingo.uploader.jaxb.Flamingo;
import org.openflamingo.uploader.jaxb.Local;
import org.openflamingo.uploader.util.DateUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Quartz Job Scheduler based Flamingo HDFS File Uploader Job.
 *
 * @author Edward KIM
 * @since 0.2
 */
public class QuartzJob implements Job {

    /**
     * SLF4J Logging
     */
    private Logger logger = LoggerFactory.getLogger(QuartzJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();
        Flamingo flamingo = (Flamingo) dataMap.get("model");
        org.openflamingo.uploader.jaxb.Job job = (org.openflamingo.uploader.jaxb.Job) dataMap.get("job");
        JobContext jobContext = (JobContext) dataMap.get("context");

        Date startDate = new Date();
        logger.info("--------------------------------------------");
        logger.info("Job '{}'을 시작합니다", job.getName());
        logger.info("--------------------------------------------");
        if (job.getPolicy().getIngress().getLocal() != null) {
            Local local = job.getPolicy().getIngress().getLocal();
            String completeDirectory = local.getCompleteDirectory();
            String errorDirectory = local.getErrorDirectory();
            String workingDirectory = local.getWorkingDirectory();
            String sourceDirectory = local.getSourceDirectory().getPath();
            String condition = local.getSourceDirectory().getCondition();
            String conditionType = local.getSourceDirectory().getConditionType();
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        Date endDate = new Date();
        logger.info("Job '{}'을 완료하였습니다", job.getName());
        logger.info("Job '{}'을 처리하는데 소요된 총 시간은 {} (시작: {} / 종료: {})입니다.", new String[]{
            job.getName(), DateUtils.formatDiffTime(endDate, startDate), DateUtils.parseDate(startDate, "yyyy-MM-dd HH:mm:ss"), DateUtils.parseDate(endDate, "yyyy-MM-dd HH:mm:ss")
        });
    }

}

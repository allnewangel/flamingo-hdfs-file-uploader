package org.openflamingo.uploader;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.openflamingo.uploader.handler.LocalIngressHandler;
import org.openflamingo.uploader.jaxb.Flamingo;
import org.openflamingo.uploader.jaxb.Local;
import org.openflamingo.uploader.util.DateUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

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

    /**
     * HDFS File Uploader Job XML의 JAXB ROOT Object
     */
    private Flamingo model;

    /**
     * HDFS File Uploader Job Context
     */
    private JobContext jobContext;

    /**
     * HDFS File Uploader Job XML에서 정의되어 있는 현재 처리해야할 노드
     */
    private org.openflamingo.uploader.jaxb.Job job;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        //////////////////////////////////////
        // Quartz Job Scheduler Context에서
        // 필요한 각종 정보를 꺼내오는 부분
        //////////////////////////////////////

        JobDataMap dataMap = context.getMergedJobDataMap();
        model = (Flamingo) dataMap.get("model");
        job = (org.openflamingo.uploader.jaxb.Job) dataMap.get("job");
        jobContext = (JobContext) dataMap.get("context");

        //////////////////////////////////////
        // 핸드러를 이용하여 처리하는 부분
        //////////////////////////////////////

        Date startDate = new Date();
        logger.info("--------------------------------------------");
        logger.info("Job '{}'을 시작합니다", job.getName());
        logger.info("--------------------------------------------");
        if (job.getPolicy().getIngress().getLocal() != null) {
            Local local = job.getPolicy().getIngress().getLocal();
            new LocalIngressHandler(jobContext, job, local).validate().execute();
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

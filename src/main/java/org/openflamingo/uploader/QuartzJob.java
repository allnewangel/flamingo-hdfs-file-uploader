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

import org.apache.commons.lang.StringUtils;
import org.openflamingo.uploader.handler.Handler;
import org.openflamingo.uploader.handler.HttpToLocalHandler;
import org.openflamingo.uploader.handler.LocalToHdfsHandler;
import org.openflamingo.uploader.jaxb.Flamingo;
import org.openflamingo.uploader.jaxb.Http;
import org.openflamingo.uploader.jaxb.Local;
import org.openflamingo.uploader.util.DateUtils;
import org.openflamingo.uploader.util.JVMIDUtils;
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
 * @since 0.1
 */
public class QuartzJob implements Job {

    /**
     * SLF4J Logging
     */
    private Logger logger;

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
        // 핸들러를 이용하여 처리하는 부분
        //////////////////////////////////////

        Date startDate = new Date();
        String jobLoggerName = StringUtils.remove(job.getName(), " ") + "_" + DateUtils.parseDate(startDate, "yyyyMMddHHmm") + "_" + JVMIDUtils.generateUUID();
        logger = LoggerFactory.getLogger(jobLoggerName);

        logger.info("--------------------------------------------");
        logger.info("Job '{}'을 시작합니다", job.getName());
        logger.info("--------------------------------------------");
        Handler handler = null;
        if (job.getPolicy().getIngress().getLocal() != null) {
            Local local = job.getPolicy().getIngress().getLocal();
            handler = new LocalToHdfsHandler(jobContext, job, local, logger);
        } else if (job.getPolicy().getIngress().getHttp() != null) {
            Http http = job.getPolicy().getIngress().getHttp();
            handler = new HttpToLocalHandler(jobContext, job, http, logger);
        }

        try {
            handler.validate();
            handler.execute();
        } catch (Exception ex) {
            String msg = "핸들러를 실행하던 도중 예외가 발생하여 Quartz Job이 실패하였습니다.";
            logger.warn(msg, ex);
            throw new JobExecutionException(msg, ex, false);
        }

        Date endDate = new Date();
        logger.info("Job '{}'을 완료하였습니다", job.getName());
        logger.info("Job '{}'을 처리하는데 소요된 총 시간은 {} (시작: {} / 종료: {})입니다.", new String[]{
            job.getName(), DateUtils.formatDiffTime(endDate, startDate), DateUtils.parseDate(startDate, "yyyy-MM-dd HH:mm:ss"), DateUtils.parseDate(endDate, "yyyy-MM-dd HH:mm:ss")
        });
    }

}

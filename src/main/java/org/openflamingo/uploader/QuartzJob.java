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

		logger.info("Job '{}'을 시작합니다 :: {}", job.getName(), DateUtils.parseDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
		if (job.getPolicy().getIngress().getLocal() != null) {
			Local local = job.getPolicy().getIngress().getLocal();
			String completeDirectory = local.getCompleteDirectory();
			String errorDirectory = local.getErrorDirectory();
			String workingDirectory = local.getWorkingDirectory();
			String sourceDirectory = local.getSourceDirectory().getPath();
			String condition = local.getSourceDirectory().getCondition();
			String conditionType = local.getSourceDirectory().getConditionType();
		}

		logger.info("Job '{}'을 완료하였습니다 :: {}", job.getName(), DateUtils.parseDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
	}

}

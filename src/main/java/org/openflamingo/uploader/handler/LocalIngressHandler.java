package org.openflamingo.uploader.handler;

import org.apache.hadoop.fs.Path;
import org.openflamingo.uploader.JobContext;
import org.openflamingo.uploader.jaxb.Job;
import org.openflamingo.uploader.jaxb.Local;
import org.openflamingo.uploader.util.FileSystemScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.openflamingo.uploader.util.FileSystemUtils.*;

/**
 * Local FileSystem을 이용하여 Ingress 처리를 하는 핸들러.
 *
 * @author Edward KIM
 * @since 0.1
 */
public class LocalIngressHandler implements IngressHandler {

    /**
     * SLF4J Logging
     */
    private Logger logger = LoggerFactory.getLogger(LocalIngressHandler.class);

    /**
     * HDFS File Uploader Job Context
     */
    private JobContext jobContext;

    /**
     * HDFS File Uploder Job
     */
    private Job job;

    /**
     * Job에 정의되어 있는 Ingress 노드
     */
    private Local local;

    public LocalIngressHandler(JobContext jobContext, Job job, Local local) {
        this.jobContext = jobContext;
        this.job = job;
        this.local = local;
    }

    @Override
    public void execute() {

    }

    @Override
    public IngressHandler validate() {
        String sourceDirectory = correctPath(local.getSourceDirectory().getPath());
        String workingDirectory = correctPath(local.getWorkingDirectory());
        String errorDirectory = correctPath(local.getErrorDirectory());
        String completeDirectory = correctPath(local.getCompleteDirectory());

        // Scheme이 맞는지 학인한다.
        checkScheme(sourceDirectory, FileSystemScheme.LOCAL);
        checkScheme(workingDirectory,FileSystemScheme.LOCAL);
        checkScheme(errorDirectory,FileSystemScheme.LOCAL);
        checkScheme(completeDirectory,FileSystemScheme.LOCAL);

        // 동일한 파일 시스템을 사용하는지 확인한다.
        validateSameFileSystem(sourceDirectory, workingDirectory);
        validateSameFileSystem(sourceDirectory, errorDirectory);
        if (completeDirectory != null) {
            validateSameFileSystem(sourceDirectory, completeDirectory);
        }

        // 존재하지 않으면 생성한다.
        testCreateDir(new Path(sourceDirectory));
        testCreateDir(new Path(workingDirectory));
        testCreateDir(new Path(errorDirectory));
        if (local.getCompleteDirectory() != null) {
            testCreateDir(new Path(completeDirectory));
        }
        return this;
    }
/*

    public FileStatus getInboundFile(JobContext jobContext) throws IOException, InterruptedException {
        for (FileStatus fs : config.getSrcFs().listStatus(config.getSrcDir())) {
            if (!fs.isDir()) {
                if (fs.getPath().getName().startsWith(".")) {
                    logger.debug("Ignoring hidden file '" + fs.getPath() + "'");
                    continue;
                }

                // move file into work directory
                //
                Path workPath = new Path(config.getWorkDir(), fs.getPath().getName());
                config.getSrcFs().rename(fs.getPath(), workPath);

                return config.getSrcFs().getFileStatus(workPath);
            }
        }
        return null;
    }

    public boolean fileCopyComplete(FileStatus fs) throws IOException {
        boolean success;
        if (config.isRemove()) {
            logger.info("File copy successful, deleting source " + fs.getPath());
            success = config.getSrcFs().delete(fs.getPath(), false);
            if (!success) {
                logger.info("File deletion unsuccessful");
            }
        } else {
            Path completedPath = new Path(config.getCompleteDir(), fs.getPath().getName());
            logger.info("File copy successful, moving source " + fs.getPath() + " to completed file " + completedPath);
            success = config.getSrcFs().rename(fs.getPath(), completedPath);
            if (!success) {
                logger.info("File move unsuccessful");
            }
        }
        return success;
    }

    public boolean fileCopyError(FileStatus fs) throws IOException, InterruptedException {
        Path errorPath = new Path(config.getErrorDir(), fs.getPath().getName());
        logger.info("Found file in work directory, moving " + fs.getPath() + " to error file " + errorPath);
        return config.getSrcFs().rename(fs.getPath(), errorPath);
    }
*/
}

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
package org.openflamingo.uploader.handler;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.openflamingo.uploader.JobContext;
import org.openflamingo.uploader.jaxb.Job;
import org.openflamingo.uploader.jaxb.Local;
import org.openflamingo.uploader.policy.SelectorPattern;
import org.openflamingo.uploader.policy.SelectorPatternFactory;
import org.openflamingo.uploader.util.FileSystemScheme;
import org.openflamingo.uploader.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.openflamingo.uploader.util.FileSystemUtils.*;

/**
 * Local FileSystem을 이용하여 Ingress 처리를 하는 핸들러.
 *
 * @author Edward KIM
 * @since 0.1
 */
public class LocalHandler implements Handler {

    /**
     * SLF4J Logging
     */
    private Logger logger = LoggerFactory.getLogger(LocalHandler.class);

    /**
     * 작업중인 파일의 확장자. 작업 디렉토리의 파일 중에서 다음의 확장자를 가진 파일은
     * 현재 타 쓰레드가 작업중인 파일이므로 멀티 쓰레드로 처리하는 경우 타 쓰레드 처리를 위해서 처리하지 않는다.
     */
    public static final String PROCESSING_FILE_QUALIFIER = ".processing";

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

    public LocalHandler(JobContext jobContext, Job job, Local local) {
        this.jobContext = jobContext;
        this.job = job;
        this.local = local;
    }

    @Override
    public void execute() throws Exception {
        List<FileStatus> inboundFiles = copyToWorkingDirectory();
        List<FileStatus> files = getFilesFromWorkingDirectory();
        Iterator<FileStatus> iterator = files.iterator();
        while (iterator.hasNext()) {
            FileStatus fileStatus = iterator.next();
            System.out.println(fileStatus.getPath());
        }
    }

    @Override
    public void validate() {
        String sourceDirectory = correctPath(local.getSourceDirectory().getPath());
        String workingDirectory = correctPath(local.getWorkingDirectory());
        String errorDirectory = correctPath(local.getErrorDirectory());
        String completeDirectory = correctPath(local.getCompleteDirectory());

        // Scheme이 맞는지 학인한다.
        checkScheme(sourceDirectory, FileSystemScheme.LOCAL);
        checkScheme(workingDirectory, FileSystemScheme.LOCAL);
        checkScheme(errorDirectory, FileSystemScheme.LOCAL);
        checkScheme(completeDirectory, FileSystemScheme.LOCAL);

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
    }

    /**
     * 처리할 파일을 찾아내어 작업 디렉토리로 이동하고 작업 디렉토리의 파일을 반환한다.
     *
     * @return 작업 디렉토리의 파일(파일이 존재하지 않는 경우 null)
     * @throws IOException 파일 시스템에 접근할 수 없거나, 파일을 이동할 수 없는 경우
     */
    public List<FileStatus> copyToWorkingDirectory() throws IOException {
        SelectorPattern selectorPattern = SelectorPatternFactory.getSelectorPattern(this.local.getSourceDirectory().getConditionType(), jobContext.getValue(this.local.getSourceDirectory().getCondition()), jobContext);
        String sourceDirectory = correctPath(local.getSourceDirectory().getPath());
        String workingDirectory = correctPath(local.getWorkingDirectory());

        FileSystem sourceDirectoryFS = getFileSystem(sourceDirectory);
        List<FileStatus> files = new LinkedList<FileStatus>();
        for (FileStatus fs : sourceDirectoryFS.listStatus(new Path(sourceDirectory))) {
            if (!fs.isDir()) {
                if (fs.getPath().getName().startsWith(".") || fs.getPath().getName().startsWith("_") || fs.getPath().getName().endsWith(".work")) {
                    logger.info("숨김 파일 '{}'은 처리하지 않고 넘어갑니다.", fs.getPath());
                    continue;
                }
                if (selectorPattern.accept(FileUtils.getFilename(fs.getPath().getName()))) {
                    // 파일을 작업 디렉토리로 이동한다.
                    Path workPath = new Path(workingDirectory, fs.getPath().getName());
                    sourceDirectoryFS.rename(fs.getPath(), workPath);
                    logger.info("원본 파일 '{}'을 작업 디렉토리 '{}'으로 이동하였습니다.", fs.getPath(), workPath);
                    files.add(sourceDirectoryFS.getFileStatus(workPath));
                }
            }
        }
        return files;
    }

    /**
     * 작업 디렉토리의 파일을 목록을 반환한다. 이때 작업중인 파일은 제외한다.
     * 작업중인 파일은 확장자가 <tt>.processing</tt>으로 구성된다.
     *
     * @return 작업 디렉토리의 파일 중에서 작업이 진행중인 파일이 아닌 파일에 대한 목록
     * @throws IOException 파일 시스템에 접근할 수 없거나 또는 파일 목록을 얻을 수 없는 경우
     */
    public List<FileStatus> getFilesFromWorkingDirectory() throws IOException {
        String workingDirectory = correctPath(local.getWorkingDirectory());
        FileSystem workingDirectoryFS = getFileSystem(workingDirectory);
        List<FileStatus> files = new LinkedList<FileStatus>();
        for (FileStatus fs : workingDirectoryFS.listStatus(new Path(workingDirectory))) {
            if (!fs.isDir()) {
                if (fs.getPath().getName().endsWith(".processing")) {
                    logger.info("'{}' 파일을 현재 작업중인 파일이므로 무시합니다", fs.getPath());
                    continue;
                }
                files.add(fs);
            }
        }
        return files;
    }

    /**
     * 작업 디렉토리의 파일을 목록을 반환한다. 이때 작업중인 파일만으로 목록을 구성한다.
     * 작업중인 파일은 확장자가 <tt>.processing</tt>으로 구성된다.
     *
     * @return 작업 디렉토리의 파일 중에서 작업이 진행중인 파일에 대한 목록
     * @throws IOException 파일 시스템에 접근할 수 없거나 또는 파일 목록을 얻을 수 없는 경우
     */
    public List<FileStatus> getProcessingFilesFromWorkingDirectory() throws IOException {
        String workingDirectory = correctPath(local.getWorkingDirectory());
        FileSystem workingDirectoryFS = getFileSystem(workingDirectory);
        List<FileStatus> files = new LinkedList<FileStatus>();
        for (FileStatus fs : workingDirectoryFS.listStatus(new Path(workingDirectory))) {
            if (!fs.isDir()) {
                if (fs.getPath().getName().endsWith(PROCESSING_FILE_QUALIFIER)) {
                    files.add(fs);
                }
            }
        }
        return files;
    }

    /**
     * 파일을 완료 디렉토리로 이동한다.
     *
     * @param fs 파일
     * @return 정상적으로 완료되었다면 <tt>true</tt>
     * @throws IOException 파일을 이동할 수 없는 경우
     */
    public boolean copyToCompleteDirectory(FileStatus fs) throws IOException {
        String sourceDirectory = correctPath(local.getSourceDirectory().getPath());
        String completeDirectory = correctPath(local.getCompleteDirectory());
        FileSystem sourceDirectoryFS = getFileSystem(sourceDirectory);

        boolean success;
        if (local.isRemoveAfterCopy()) {
            logger.info("파일 복사를 완료하였습니다. 원본 파일 '{}'을 삭제합니다." + fs.getPath());
            success = sourceDirectoryFS.delete(fs.getPath(), false);
            if (!success) {
                logger.info("원본 파일 '{}'을 삭제하였습니다.", fs.getPath());
            }
        } else {
            Path completedPath = new Path(completeDirectory, fs.getPath().getName());
            logger.info("파일 복사를 완료하였습니다. 원본 파일 '{}'을 '{}'으로 이동하였습니다.", fs.getPath(), completedPath);
            success = sourceDirectoryFS.rename(fs.getPath(), completedPath);
            if (!success) {
                logger.warn("파일 이동이 완료되지 않았습니다.");
            }
        }
        return success;
    }

    /**
     * 파일을 에러 디렉토리로 이동한다.
     *
     * @param fs 파일
     * @return 정상적으로 완료되었다면 <tt>true</tt>
     * @throws IOException 파일을 이동할 수 없는 경우
     */
    public boolean copyToErrorDirectory(FileStatus fs) throws IOException {
        String sourceDirectory = correctPath(local.getSourceDirectory().getPath());
        String errorDirectory = correctPath(local.getErrorDirectory());
        FileSystem sourceDirectoryFS = getFileSystem(sourceDirectory);

        Path errorPath = new Path(errorDirectory, fs.getPath().getName());
        logger.info("작업 디렉토리에서 파일을 찾았습니다. '{}' 파일을 '{}'으로 이동합니다.", fs.getPath(), errorPath);
        return sourceDirectoryFS.rename(fs.getPath(), errorPath);
    }
}

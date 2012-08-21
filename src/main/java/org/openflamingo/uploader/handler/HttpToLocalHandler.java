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
package org.openflamingo.uploader.handler;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.mortbay.jetty.HttpStatus;
import org.openflamingo.uploader.JobContext;
import org.openflamingo.uploader.exception.SystemException;
import org.openflamingo.uploader.jaxb.*;
import org.openflamingo.uploader.util.ExceptionUtils;
import org.openflamingo.uploader.util.FileSystemScheme;
import org.openflamingo.uploader.util.FileSystemUtils;
import org.openflamingo.uploader.util.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

import static org.openflamingo.uploader.util.FileSystemUtils.*;

/**
 * HTTP URL 호출을 통해 얻는 결과물을 파일로 저장하는 핸들러.
 *
 * @author Edward KIM
 * @since 0.1
 */
public class HttpToLocalHandler implements Handler {

    /**
     * Job Logger
     */
    private Logger jobLogger;

    /**
     * 작업중인 파일의 확장자. 작업 디렉토리의 파일 중에서 다음의 확장자를 가진 파일은
     * 현재 타 쓰레드가 작업중인 파일이므로 멀티 쓰레드로 처리하는 경우 타 쓰레드 처리를 위해서 처리하지 않는다.
     */
    public static final String PROCESSING_FILE_QUALIFIER = ".processing"; // FIXME

    /**
     * HDFS URL에 대한 Hadoop Configuration Key
     */
    public final static String HDFS_URL = "fs.default.name";

    /**
     * Job Tracker에 대한 Hadoop Configuration Key
     */
    public final static String JOB_TRACKER = "mapred.job.tracker";

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
    private Http http;

    /**
     * Default HTTP Content Type
     */
    private static final String DEFAULT_CONTENT_TYPE = "plain/text";

    /**
     * Default HTTP Request Body Content Type
     */
    private static final String DEFAULT_CHAR_SET = "UTF-8";

    /**
     * 기본 생성자.
     *
     * @param jobContext Flamingo HDFS File Uploader의 Job Context
     * @param job        Job
     * @param http       Http Ingress
     * @param jobLogger  Job Logger for trace
     */
    public HttpToLocalHandler(JobContext jobContext, Job job, Http http, Logger jobLogger) {
        this.jobContext = jobContext;
        this.job = job;
        this.http = http;
        this.jobLogger = jobLogger;
    }

    @Override
    public void execute() throws Exception {
        String response = getResponse(http);
        String type = jobContext.getValue(http.getTarget().getType());
        String filename = jobContext.getValue(http.getTarget().getFilename());
        String target = jobContext.getValue(http.getTarget().getDirectory());
        if ("LOCAL".equals(type)) {
            jobLogger.info("HTTP");
            String targetPath = correctPath(target);
            FileSystem fs = FileSystemUtils.getFileSystem(targetPath);
            saveResponseToFS(response, fs, targetPath, filename);
        } else {
            String cluster = assertNotEmpty(jobContext.getValue(http.getTarget().getCluster()));
            Configuration configuration = getConfiguration(cluster);
            String targetPath = jobContext.getValue(configuration.get(HDFS_URL) + target);
            FileSystem fs = FileSystemUtils.getFileSystem(targetPath);
            saveResponseToFS(response, fs, targetPath, filename);
        }
    }

    /**
     * HTTP로 해당 URL을 요청한다.
     *
     * @param http HTTP
     * @return HTTP Response String
     */
    private String getResponse(Http http) throws IOException {
        jobLogger.info("HTTP URL을 호출하기 위해 관련 파라미터 정보를 처리합니다.");
        HttpClient httpClient = new HttpClient();
        HttpClientParams params = httpClient.getParams();

        String url = jobContext.getValue(http.getUrl());
        String method = jobContext.getValue(http.getMethod().getType());
        String body = jobContext.getValue(http.getBody());
        String contentType = jobContext.getValue(http.getContentType(), DEFAULT_CONTENT_TYPE);
        String charSet = jobContext.getValue(http.getCharSet(), DEFAULT_CHAR_SET);

        jobLogger.info("HTTP URL Information :");
        jobLogger.info("\tURL = {}", url);
        jobLogger.info("\tMethod = {}", method);
        jobLogger.info("\tContent Type = {}", contentType);
        jobLogger.info("\tCharacter Set = {}", charSet);
        if (!StringUtils.isEmpty(body)) {
            jobLogger.info("\tBody = \n{}", charSet);
        }

        HttpMethod httpMethod = null;
        if ("POST".equals(method)) {
            httpMethod = new PostMethod(url);
            ((PostMethod) httpMethod).setRequestEntity(new StringRequestEntity(body, contentType, charSet));
        } else {
            httpMethod = new GetMethod(url);
        }

        if (http.getHeaders() != null && http.getHeaders().getHeader().size() > 0) {
            List<Header> header = http.getHeaders().getHeader();
            jobLogger.info("HTTP Header :", new String[]{});
            for (Header h : header) {
                String name = h.getName();
                String value = jobContext.getValue(h.getValue());
                httpMethod.addRequestHeader(name, value);
                jobLogger.info("\t{} = {}", name, value);
            }
        }

        if (!StringUtils.isEmpty(charSet)) {
            params.setContentCharset(charSet);
        }

        String responseBodyAsString = null;
        try {
            int status = httpClient.executeMethod(httpMethod);
            responseBodyAsString = httpMethod.getResponseBodyAsString();
            jobLogger.debug("HTTP 요청의 응답 메시지는 다음과 같습니다.\n", responseBodyAsString);
            jobLogger.info("HTTP 요청을 완료하였습니다. 상태 코드는 '{}'입니다.", status);
            if (status != HttpStatus.ORDINAL_200_OK) {
                throw new SystemException(ExceptionUtils.getMessage("HTTP URL 호출에 실패하였습니다. 응답코드가 OK가 아닌 '{}' 코드가 서버에서 수신하였습니다.", status));
            }
        } catch (Exception ex) {
            throw new SystemException(ExceptionUtils.getMessage("HTTP URL 호출에 실패하였습니다. 에러 메시지: {}", ex.getMessage()), ex);
        }
        return responseBodyAsString;
    }

    /**
     * HTTP Response를 파일로 기록한다.
     *
     * @param response        HTTP Response
     * @param fs              FileSystem
     * @param targetDirectory Target Directory
     * @param filename        Filename
     */
    private void saveResponseToFS(String response, FileSystem fs, String targetDirectory, String filename) {
        FSDataOutputStream dos = null;
        Path path = new Path(targetDirectory, filename);
        try {
            dos = fs.create(path);
            IOUtils.write(response.getBytes(), dos);
        } catch (Exception ex) {
            throw new SystemException(ExceptionUtils.getMessage("HTTP 응답을 파일로 '{}'에 기록할 수 없습니다.", path), ex);
        } finally {
            if (dos != null) {
                IOUtils.closeQuietly(dos);
            }
        }
    }

    @Override
    public void validate() {
        assertNotEmpty(jobContext.getValue(http.getUrl()));
        String method = assertNotEmpty(jobContext.getValue(http.getMethod().getType()));
        if ("POST".equals(method)) {
            assertNotEmpty(jobContext.getValue(http.getBody()));
        }

        String type = jobContext.getValue(http.getTarget().getType());
        String directory = assertNotEmpty(jobContext.getValue(http.getTarget().getDirectory()));
        assertNotEmpty(jobContext.getValue(http.getTarget().getFilename()));
        if ("LOCAL".equals(type)) {
            String targetPath = correctPath(directory);
            checkScheme(targetPath, FileSystemScheme.LOCAL);
            testCreateDir(new Path(targetPath));
        } else {
            String cluster = assertNotEmpty(jobContext.getValue(http.getTarget().getCluster()));
            Configuration configuration = getConfiguration(cluster);
            String targetPath = jobContext.getValue(configuration.get(HDFS_URL) + directory);
            checkScheme(targetPath, FileSystemScheme.HDFS);
            testCreateDir(new Path(targetPath));
        }
    }

    /**
     * Hadoop Cluster의 이름으로 Cluster의 Hadoop Configuration을 생성한다.
     *
     * @param clusterName Hadoop Cluster명
     * @return {@link org.apache.hadoop.conf.Configuration}
     */
    public Configuration getConfiguration(String clusterName) {
        org.apache.hadoop.conf.Configuration configuration = new org.apache.hadoop.conf.Configuration();
        List<Cluster> clusters = jobContext.getModel().getClusters().getCluster();
        for (Cluster cluster : clusters) {
            if (clusterName.equals(cluster.getName())) {
                configuration.set(HDFS_URL, cluster.getFsDefaultName());
                configuration.set(JOB_TRACKER, cluster.getMapredJobTracker());

                List<Property> properties = cluster.getProperties().getProperty();
                for (Property property : properties) {
                    configuration.set(property.getName(), property.getValue());
                }
            }
        }
        return configuration;
    }

    /**
     * 지정한 값이 비어있지 않은지 확인한다.
     *
     * @param value 확인할 문자열
     * @return 입력한 확인할 문자열
     * @throws SystemException 빈 값이거나 NULL인 경우
     */
    protected String assertNotEmpty(String value) {
        if (StringUtils.isEmpty(value)) {
            throw new SystemException(ExceptionUtils.getMessage("NULL 값이거나 파라미터의 값이 비어있습니다.", value));
        }
        return value;
    }
}

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
package org.openflamingo.uploader.exception;

/**
 * Flamingo HDFS File Uploader 전체에서 사용하는 런타임 예외.
 *
 * @author Edward KIM
 * @since 0.1
 */
public class ProcessFailureException extends SystemException {

    private static final long serialVersionUID = 1;

    /**
     * 종료 코드
     */
    private final int exitCode;

    /**
     * 로그 메시지
     */
    private final String logSnippet;

    /**
     * 기본 생성자.
     *
     * @param exitCode   종료 코드
     * @param logSnippet 로그 메시지
     */
    public ProcessFailureException(int exitCode, String logSnippet) {
        this.exitCode = exitCode;
        this.logSnippet = logSnippet;
    }

    /**
     * 종료 코드를 반환한다.
     *
     * @return 종료 코드
     */
    public int getExitCode() {
        return exitCode;
    }

    /**
     * 로그 메시지를 반환한다.
     *
     * @return 로그 메시지
     */
    public String getLogSnippet() {
        return this.logSnippet;
    }

}

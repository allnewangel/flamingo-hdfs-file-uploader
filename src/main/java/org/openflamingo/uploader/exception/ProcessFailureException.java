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

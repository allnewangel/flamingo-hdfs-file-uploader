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
 * 파일 시스템을 처리할 수 없는 경우 던지는 예외.
 *
 * @author Edward KIM
 * @since 0.1
 */
public class FileSystemException extends SystemException {

    private static final long serialVersionUID = 1;

    /**
     * 기본 생성자.
     */
    public FileSystemException() {
        super();
    }

    /**
     * 기본 생성자.
     *
     * @param message 예외 메시지
     */
    public FileSystemException(String message) {
        super(message);
    }

    /**
     * 기본 생성자.
     *
     * @param message 예외 메시지
     * @param cause   예외 원인
     */
    public FileSystemException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 기본 생성자.
     *
     * @param cause 예외 원인
     */
    public FileSystemException(Throwable cause) {
        super(cause);
    }

}

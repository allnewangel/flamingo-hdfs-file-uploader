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

/**
 * Ingress 처리를 하는 핸들러 인터페이스.
 *
 * @author Edward KIM
 * @since 0.1
 */
public interface Handler {

    /**
     * 핸들러를 실행한다.
     *
     * @throws Exception 핸들러가 정상적으로 실행할 수 없는 경우
     */
    void execute() throws Exception;

    /**
     * 동작을 위한 기본 조건이 준비되었는지 검증한다.
     */
    void validate();

}

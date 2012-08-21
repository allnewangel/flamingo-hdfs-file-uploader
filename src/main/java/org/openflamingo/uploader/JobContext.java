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

import org.openflamingo.uploader.jaxb.Flamingo;

import java.util.Date;

/**
 * HDFS File Uploader Job Context.
 *
 * @author Edward KIM
 * @since 0.1
 */
public interface JobContext {

    /**
     * 지정한 이름을 가진 값을 반환한다.
     *
     * @param name 꺼내올 값의 이름
     * @return 이름에 대한 값
     */
    String getValue(String name);

    /**
     * 지정한 이름을 가진 값을 반환한다.
     *
     * @param name         꺼내올 값의 이름
     * @param defaultValue 기본값
     * @return 이름에 대한 값
     */
    String getValue(String name, String defaultValue);

    /**
     * HDFS File Uploader XML의 JAXB ROOT Object을 반환한다.
     *
     * @return HDFS File Uploader XML의 JAXB ROOT Object
     */
    Flamingo getModel();

    /**
     * 시작 시간을 반환한다.
     *
     * @return 시작 시간
     */
    Date getStartDate();

}

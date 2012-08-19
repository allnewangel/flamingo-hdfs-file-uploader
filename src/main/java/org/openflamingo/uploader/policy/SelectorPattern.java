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
package org.openflamingo.uploader.policy;

import org.openflamingo.uploader.JobContext;

/**
 * Input File Selection Pattern Interface.
 *
 * @author Edward KIM
 * @since 0.1
 */
public interface SelectorPattern {

    /**
     * 지정한 파일을 사용할지 여부를 결정한다.
     *
     * @param filename 경로를 제외한 순수 파일명
     * @return 파일을 사용하는 경우 <tt>true</tt>
     */
    boolean accept(String filename);

}

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
 * Date Pattern based File Selection Pattern.
 *
 * @author Edward KIM
 * @since 0.1
 */
public class DatePattern implements SelectorPattern {

    /**
     * 파일명이 지정한 문자열로 시작하는지 확인하기 위한 Simple Date Format 패턴
     */
    private String pattern;

    /**
     * HDFS File Uploader Job Context
     */
    private JobContext jobContext;

    /**
     * 기본 생성자.
     *
     * @param pattern 문자열 패턴
     */
    public DatePattern(String pattern, JobContext jobContext) {
        this.pattern = pattern;
        this.jobContext = jobContext;
    }

    @Override
    public boolean accept(String filename) {
        String evaluated = jobContext.getValue(filename);
        String dateFormat = jobContext.getValue(pattern);
        return org.springframework.util.StringUtils.countOccurrencesOf(evaluated, dateFormat) > 0;
    }
}

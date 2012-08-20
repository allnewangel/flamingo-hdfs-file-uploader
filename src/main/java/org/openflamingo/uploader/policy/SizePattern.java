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

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.openflamingo.uploader.JobContext;
import org.openflamingo.uploader.exception.FileSystemException;
import org.openflamingo.uploader.util.ExceptionUtils;
import org.openflamingo.uploader.util.FileSystemUtils;

/**
 * File Size Selection Pattern.
 *
 * @author Edward KIM
 * @since 0.2
 */
public class SizePattern implements SelectorPattern {

    /**
     * HDFS File Uploader Job Context
     */
    private JobContext jobContext;

    /**
     * File Size
     */
    String pattern;

    /**
     * 기본 생성자.
     *
     * @param pattern    파일의 크기
     * @param jobContext Job Context
     */
    public SizePattern(String pattern, JobContext jobContext) {
        this.pattern = pattern;
        this.jobContext = jobContext;
    }

    @Override
    public boolean accept(Path path) {
        try {
            String evaluated = jobContext.getValue(path.getName());
            long size = Long.parseLong(evaluated);
            FileSystem fs = FileSystemUtils.getFileSystem(path);
            FileStatus fileStatus = fs.getFileStatus(path);
            return fileStatus.getLen() > size ? true : false;
        } catch (Exception ex) {
            throw new FileSystemException(ExceptionUtils.getMessage("파일({})의 크기를 확인할 수 없습니다.", path), ex);
        }
    }
}

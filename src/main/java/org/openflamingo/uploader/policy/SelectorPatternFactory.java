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
import org.openflamingo.uploader.exception.SystemException;
import org.openflamingo.uploader.util.ExceptionUtils;
import org.openflamingo.uploader.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Selector Pattern Registry.
 *
 * @author Edward KIM
 * @since 0.2
 */
public class SelectorPatternFactory {

    private static Map<String, Class> patterns = new HashMap<String, Class>();

    static {
        patterns.put("startWith", StartWithPattern.class);
        patterns.put("endWith", EndWithPattern.class);
        patterns.put("antPattern", AntPathPattern.class);
        patterns.put("datePattern", DatePattern.class);
        patterns.put("regEx", RegExPattern.class);
    }

    /**
     * 지정한 유형의 Selector Pattern을 새로 생성하여 반환한다.
     *
     * @param type       Selector Pattern의 식별자(예; startWith, endWith, antPattern, datePattern, regEx)
     * @param pattern    Selector Pattern에서 사용하는 문자열 패턴
     * @param jobContext HDFS File Uploader Job Context
     * @return Selector Pattern
     */
    public static SelectorPattern getSelectorPattern(String type, String pattern, JobContext jobContext) {
        Class clazz = patterns.get(type);
        try {
            Constructor constructor = ReflectionUtils.getConstructorIfAvailable(clazz, new Class[]{String.class, JobContext.class});
            return (SelectorPattern) constructor.newInstance(pattern, jobContext);
        } catch (Exception ex) {
            throw new SystemException(ExceptionUtils.getMessage("Selector Pattern 클래스인 {}을 생성할 수 없습니다.", clazz.getClass().getName()), ex);
        }
    }

}

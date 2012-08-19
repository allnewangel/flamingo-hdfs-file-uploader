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
package org.openflamingo.uploader.util;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * String Utility.
 *
 * @author Edward KIM
 * @since 0.1
 */
public class StringUtils {

    /**
     * 문자열 리스트를 문자열 배열로 변환한다.
     *
     * @param values 문자열 리스트
     * @return 문자열 배열
     */
    public static String[] collectionToStringArray(List<String> values) {
        String[] array = new String[values.size()];
        int index = 0;
        for (String value : values) {
            array[index] = value;
            index++;
        }
        return array;
    }

    /**
     * 지정한 문자열이 비어있는지 확인한다.
     *
     * @param str 테스트할 문자열
     * @return 비어있다면 <tt>true</tt>
     */
    public static boolean isEmpty(String str) {
        return (str == null || str.trim().length() < 1);
    }

    /**
     * 콤마를 구분자로 리스트를 하나의 문자열을 구성한다.
     *
     * @param values 리스트
     * @return 콤마 구분자를 포함한 문자열
     */
    public static String collectionToCommaDelimitedString(List<String> values) {
        return collectionToDelimitedString(values, ",");
    }

    /**
     * 지정한 구분자로 리스트를 하나의 문자열로 구성한다.
     *
     * @param values    리스트
     * @param delimiter 구분자
     * @return 구분자를 포함한 문자열
     */
    public static String collectionToDelimitedString(List<String> values, String delimiter) {
        return Joiner.on(delimiter).join(values);
    }

    /**
     * 문자열 배열을 리스트로 변환한다.
     *
     * @param values 문자열 배열열
     * @return 문자열 리스트
     */
    public static List<String> arrayToCollection(String[] values) {
        List<String> list = new ArrayList<String>(values.length);
        Collections.addAll(list, values);
        return list;
    }

    /**
     * 구분자로 분리 가능한 문자열을 리스트로 변환한다.
     *
     * @param value     구분자를 포함한 리스트
     * @param separator 구분자
     * @return 문자열 리스트
     */
    public static List<String> stringToCollection(String value, String separator) {
        String[] strings = org.apache.commons.lang.StringUtils.splitPreserveAllTokens(value, separator);
        return arrayToCollection(strings);
    }
}

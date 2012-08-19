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

import java.util.UUID;

/**
 * Immutable Universally Unique Identifier (UUID)를 생성하는 Generator.
 * <p>
 * <pre>
 * String uuid = UUIDUtils.generateUUID();
 * </pre>
 * </p>
 *
 * @author Edward KIM
 * @since 0.1
 */
public class UUIDUtils {

    /**
     * 고유한 UUID를 생성한다.
     *
     * @return 생성된 UUID
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

}
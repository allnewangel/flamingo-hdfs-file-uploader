/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openflamingo.uploader.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.openflamingo.uploader.exception.FileSystemException;
import org.openflamingo.uploader.exception.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

/**
 * FileSystem Utility.
 *
 * @author Edward KIM
 * @since 0.1
 */
public class FileSystemUtils {

    /**
     * SLF4J Logging
     */
    private static Logger logger = LoggerFactory.getLogger(FileSystemUtils.class);

    /**
     * 지정한 경로를 보정한다. <tt>hdfs://</tt> 또는 <tt>file:</tt> 으로 시작하지 않는 경우
     * 기본으로 로컬 파일 시스템으로 간주하고 <tt>file:</tt>을 붙인다.
     *
     * @param path 파일 또는 디렉토리의 경로
     * @return 보정한 디렉토리의 경로
     */
    public static String correctPath(String path) {
        if (path == null || path.length() < 1) {
            return null;
        }
        Path filePath = new Path(path);
        String scheme = filePath.toUri().getScheme();
        if (scheme == null) {
            return "file:" + path;
        }
        return path;
    }

    /**
     * 지정한 경로가 파일 시스템의 경로인지 확인한다.
     *
     * @param path   경로
     * @param scheme 파일 시스템의 scheme(예; hdfs:// file:)
     */
    public static void checkScheme(String path, FileSystemScheme scheme) {
        if (path == null || path.length() < 1) {
            return;
        }
        if (!path.startsWith(scheme.getScheme())) {
            throw new SystemException(ExceptionUtils.getMessage("처리할 경로 '{}'은 지정한 파일 시스템({})이 아닙니다.", path, scheme.getScheme()));
        }
    }

    /**
     * 지정한 디렉토리를 테스트하여 존재하지 않으면 생성한다.
     * 다만 존재하면서 디렉토리가 아닌 파일이라면 예외를 발생시킨다.
     *
     * @param path 경로
     * @throws FileSystemException 디렉토리를 생성할 수 없거나, 존재하지만 디렉토리가 아니라 파일인 경우
     */
    public static void testCreateDir(Path path) {
        try {
            Configuration conf = new Configuration();
            FileSystem fs = path.getFileSystem(conf);
            if (fs.exists(path) && !fs.getFileStatus(path).isDir()) {
                throw new FileSystemException(ExceptionUtils.getMessage("지정한 경로가 디렉토리가 아닌 파일입니다: '{}'", path));
            }

            if (!fs.exists(path)) {
                if (!fs.mkdirs(path)) {
                    throw new FileSystemException(ExceptionUtils.getMessage("디렉토리를 생성할 수 없습니다: '{}'", path));
                }
                logger.info("디렉토리를 생성했습니다: {}", path);
            }
        } catch (Exception ex) {
            throw new FileSystemException("파일 시스템 처리를 진행할 수 없습니다.", ex);
        }
    }

    /**
     * 지정한 경로의 파일 시스템을 반환한다.
     *
     * @param path 파일 시스템을 얻을 경로
     * @return 파일 시스템
     */
    public static FileSystem getFileSystem(Path path) {
        try {
            Configuration conf = new Configuration();
            return path.getFileSystem(conf);
        } catch (Exception ex) {
            throw new FileSystemException(ExceptionUtils.getMessage("지정한 경로 '{}'의 파일 시스템을 얻을 수 없습니다.", path), ex);
        }
    }

    /**
     * 지정한 경로의 파일 시스템을 반환한다.
     *
     * @param path 파일 시스템을 얻을 경로
     * @return 파일 시스템
     */
    public static FileSystem getFileSystem(String path) {
        return getFileSystem(new Path(path));
    }

    /**
     * 두 입려 경로의 파일 시스템이 동일한지 확인한다.
     *
     * @param path1 경로1
     * @param path2 경로2
     */
    public static void validateSameFileSystem(String path1, String path2) {
        Path p1 = new Path(correctPath(path1));
        Path p2 = new Path(correctPath(path2));
        FileSystem fs1 = null;
        FileSystem fs2 = null;
        try {
            fs1 = p1.getFileSystem(new Configuration());
            fs2 = p2.getFileSystem(new Configuration());
        } catch (Exception ex) {
            throw new SystemException(ExceptionUtils.getMessage("'{}' 또는 '{}' 파일 시스템에 접근할 수 없습니다.",
                p1, p2), ex);
        }

        if (!compareFs(fs1, fs2)) {
            throw new SystemException(ExceptionUtils.getMessage(
                "지정한 두 경로가 동일한 파일 시스템이 아닙니다. 동작을 위해서는 두 경로 모두 동일한 파일 시스템을 가져야 합니다 : {}, {}", p1, p2
            ));
        }

        if (p1.equals(p2)) {
            throw new SystemException(ExceptionUtils.getMessage("지정한 두 경로가 동일한 경로입니다: {}, {}", p1, p2));
        }
    }

    /**
     * 두 파일 시스템에 대해서 URI의 scheme을 확인하여 동일한지 확인한다.
     *
     * @param fs1 파일시스템1
     * @param fs2 파일시스템2
     * @return 동일하다면 <tt>true</tt>
     */
    private static boolean compareFs(FileSystem fs1, FileSystem fs2) {
        URI uri1 = fs1.getUri();
        URI uri2 = fs2.getUri();
        if (uri1.getScheme() == null) {
            return false;
        }
        if (!uri1.getScheme().equals(uri2.getScheme())) {
            return false;
        }
        String srcHost = uri1.getHost();
        String dstHost = uri2.getHost();
        if ((srcHost != null) && (dstHost != null)) {
            try {
                srcHost = InetAddress.getByName(srcHost).getCanonicalHostName();
                dstHost = InetAddress.getByName(dstHost).getCanonicalHostName();
            } catch (UnknownHostException ue) {
                return false;
            }
            if (!srcHost.equals(dstHost)) {
                return false;
            }
        } else if (srcHost == null && dstHost != null) {
            return false;
        } else if (srcHost != null) {
            return false;
        }
        // 포트 확인
        return uri1.getPort() == uri2.getPort();
    }

}

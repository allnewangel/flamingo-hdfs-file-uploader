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

import org.junit.Assert;
import org.junit.Test;

/**
 * FileSystemUtils의 단위 테스트 케이스.
 *
 * @author Edward KIM
 * @since 0.2
 */
public class FileSystemUtilsTest {

    @Test
    public void correctPath() throws Exception {
        Assert.assertTrue(FileSystemUtils.correctPath("") == null);
        Assert.assertEquals("file:/local", FileSystemUtils.correctPath("/local"));
        Assert.assertEquals("file:/local", FileSystemUtils.correctPath("file:/local"));
        Assert.assertEquals("hdfs://localhost:9000/local", FileSystemUtils.correctPath("hdfs://localhost:9000/local"));
    }

    @Test
    public void checkScheme() throws Exception {
        FileSystemUtils.checkScheme("file:/local", FileSystemScheme.LOCAL);
        FileSystemUtils.checkScheme("hdfs://localhost:9000/local", FileSystemScheme.HDFS);

        try {
            FileSystemUtils.checkScheme("hdfs://localhost:9000/local", FileSystemScheme.LOCAL);
            Assert.assertTrue(false);
        } catch (Exception ex) {
            Assert.assertTrue(true);
        }

        try {
            FileSystemUtils.checkScheme("file:/local", FileSystemScheme.HDFS);
            Assert.assertTrue(false);
        } catch (Exception ex) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void validateSameFileSystem() throws Exception {
        FileSystemUtils.validateSameFileSystem("file:/local", "/local1");
    }

}

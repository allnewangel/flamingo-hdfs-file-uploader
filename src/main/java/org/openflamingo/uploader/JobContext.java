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
package org.openflamingo.uploader;

import org.openflamingo.uploader.jaxb.Flamingo;

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
     * HDFS File Uploader XML의 JAXB ROOT Object을 반환한다.
     *
     * @return HDFS File Uploader XML의 JAXB ROOT Object
     */
    Flamingo getModel();

}

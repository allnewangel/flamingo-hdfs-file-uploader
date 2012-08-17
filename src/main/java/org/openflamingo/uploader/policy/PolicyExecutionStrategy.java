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
package org.openflamingo.uploader.policy;

import org.openflamingo.uploader.JobContext;
import org.openflamingo.uploader.jaxb.Policy;

/**
 * Description.
 *
 * @author Edward KIM
 * @since 0.1
 */
public interface PolicyExecutionStrategy {

    /**
     * Policy를 실행한다.
     *
     * @param policy     Uplaoder Job의 Policy
     * @param jobContext Job Context
     * @throws Exception Job 실행중 에러가 발생하는 경우 또는 실행할 수 없는 경우
     */
    void run(Policy policy, JobContext jobContext) throws Exception;

}

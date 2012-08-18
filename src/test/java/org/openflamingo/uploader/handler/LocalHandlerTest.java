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
package org.openflamingo.uploader.handler;

import org.junit.Test;
import org.openflamingo.uploader.JobContextImpl;
import org.openflamingo.uploader.el.ELEvaluator;
import org.openflamingo.uploader.jaxb.Flamingo;
import org.openflamingo.uploader.jaxb.Job;
import org.openflamingo.uploader.util.JaxbUtils;
import org.openflamingo.uploader.util.ResourceUtils;

/**
 * Local FileSystem Ingress Handler의 단위 테스트 케이스.
 *
 * @author Edward KIM
 * @since 0.2
 */
public class LocalHandlerTest {

    @Test
    public void validate() throws Exception {
        String xml = ResourceUtils.getResourceTextContents("classpath:org/openflamingo/uploader/handler/local.xml");

        ELEvaluator evaluator = new ELEvaluator();
        JobContextImpl jobContext = new JobContextImpl(null, evaluator);
        String evaluated = jobContext.getValue(xml);

        System.out.println(evaluated);

        Flamingo model = (Flamingo) JaxbUtils.unmarshal("org.openflamingo.uploader.jaxb", evaluated);

        Job job = model.getJob().get(0);
        LocalHandler localHandler = new LocalHandler(jobContext, job, job.getPolicy().getIngress().getLocal());
        localHandler.validate();
        localHandler.execute();
    }

}

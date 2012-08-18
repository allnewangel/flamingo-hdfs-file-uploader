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
import org.openflamingo.uploader.util.JaxbUtils;
import org.openflamingo.uploader.util.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * Flamingo Site XML 파일을 로딩하여 JAXB Configuration Object를 생성하는 Factory Bean.
 *
 * @author Edward KIM
 * @since 0.1
 */
@Component
public class ConfigurationFactoryBean implements InitializingBean, FactoryBean<Flamingo> {

    /**
     * SLF4J Logging
     */
    private Logger logger = LoggerFactory.getLogger(ConfigurationFactoryBean.class);

    /**
     * Flamingo Site XML 파일
     */
    private Resource configurationFile;

    /**
     * Flamingo HDFS File Uploader XML의 JAXB Configuration Object
     */
    private Flamingo configuration;

    /**
     * Flamingo Site XML의 JAXB Object 패키지명
     */
    private String JAXB_PACKAGE_NAME = "org.openflamingo.uploader.jaxb";

    @Override
    public void afterPropertiesSet() throws Exception {
        // -Dflamingo.uploader.xml 으로 HDFS Uploader XML 파일의 위치를 지정한 경우 지정한 파일을 먼저 사용한다.
        if (System.getProperty("flamingo.uploader.xml") != null) {
            logger.info("'-Dflamingo.uploader.xml={}'으로 지정한 설정 파일을 사용합니다.", System.getProperty("flamingo.uploader.xml"));
            configurationFile = ResourceUtils.getResource(System.getProperty("flamingo.uploader.xml"));
        } else {
            configurationFile = new ClassPathResource("classpath:job.xml");
        }

        String xml = ResourceUtils.getResourceTextContents(configurationFile);
        configuration = (Flamingo) JaxbUtils.unmarshal(JAXB_PACKAGE_NAME, xml);
        logger.info("Uploader Job이 등록되어 있는 XML 파일({})을 로딩하였습니다.\n{}", xml, configurationFile.getFile().getAbsolutePath());
    }

    @Override
    public Flamingo getObject() throws Exception {
        return configuration;
    }

    @Override
    public Class<?> getObjectType() {
        return Flamingo.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}

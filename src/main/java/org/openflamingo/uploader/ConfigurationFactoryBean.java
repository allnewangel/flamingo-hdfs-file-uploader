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
package org.openflamingo.uploader;

import org.openflamingo.uploader.jaxb.Flamingo;
import org.openflamingo.uploader.util.JaxbUtils;
import org.openflamingo.uploader.util.ResourceUtils;
import org.openflamingo.uploader.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
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

    /**
     * HDFS Uploader Job XML File
     */
    @Value("#{config['uploader.job.xml']}")
    private String jobXml;

    @Override
    public void afterPropertiesSet() throws Exception {
        /*
            설정 파일을 로딩하는 규칙은 다음과 같다.
              1. -Duploader.job.xml 옵션으로 지정한 절대 경로
              2. config.properties 파일의 uploader.job.xml 옵션으로 지정한 절대 경로
              3. 아무것도 없으면 CLASSPATH의 job.xml
         */
        if (System.getProperty("uploader.job.xml") != null) {
            logger.info("'-Duploader.job.xml={}'으로 지정한 설정 파일을 사용합니다.", System.getProperty("uploader.job.xml"));
            configurationFile = ResourceUtils.getResource(System.getProperty("uploader.job.xml"));
        } else if (!StringUtils.isEmpty(jobXml)) {
            logger.info("config.properties 파일에 지정한 uploader.job.xml의 값인 로컬 파일 시스템의 절대 경로({})로 지정한 설정 파일을 사용합니다.", jobXml);
            configurationFile = ResourceUtils.getResource(jobXml);
        } else {
            logger.info("옵션이 별도로 지정되어 있지 않아서 CLASSPATH에서 /job.xml 파일을 기본으로 로딩합니다.", jobXml);
            configurationFile = new ClassPathResource("classpath:/job.xml");
        }

        String xml = ResourceUtils.getResourceTextContents(configurationFile);
        configuration = (Flamingo) JaxbUtils.unmarshal(JAXB_PACKAGE_NAME, xml);
        logger.info("Uploader Job이 등록되어 있는 XML 파일({})을 로딩하였습니다.\n{}", configurationFile.getFile().getAbsolutePath(), xml);
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

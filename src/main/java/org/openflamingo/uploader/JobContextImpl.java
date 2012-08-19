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

import org.apache.hadoop.conf.Configuration;
import org.openflamingo.uploader.el.ELEvaluator;
import org.openflamingo.uploader.exception.ELEvaluationException;
import org.openflamingo.uploader.jaxb.Cluster;
import org.openflamingo.uploader.jaxb.Flamingo;
import org.openflamingo.uploader.jaxb.GlobalVariable;
import org.openflamingo.uploader.jaxb.Property;
import org.openflamingo.uploader.util.ExceptionUtils;
import org.openflamingo.uploader.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Job Context Implementation.
 *
 * @author Edward KIM
 * @since 0.1
 */
public class JobContextImpl implements JobContext {

    /**
     * SLF4J Logging
     */
    private Logger logger = LoggerFactory.getLogger(JobContextImpl.class);

    /**
     * <pre>${var}</pre> 형식의 변수를 찾을 때 사용하는 Regular Expression
     */
    private static Pattern variableRegex = Pattern.compile("\\$\\{[^\\}\\$\u0020]+\\}");

    /**
     * Expression을 가진 변수를 찾을 때 찾아들어가는 최대 깊이
     */
    private static int MAX_DEPTH = 20;

    /**
     * HDFS URL에 대한 Hadoop Configuration Key
     */
    public final static String HDFS_URL = "fs.default.name";

    /**
     * Job Tracker에 대한 Hadoop Configuration Key
     */
    public final static String JOB_TRACKER = "mapred.job.tracker";

    /**
     * HDFS File Uploader XML의 JAXB ROOT Object
     */
    private Flamingo model;

    /**
     * 글로별 변수
     */
    private Properties props;

    /**
     * Expression Language Evaluator
     */
    private ELEvaluator evaluator;

    /**
     * Job의 시작 시간
     */
    private Date startDate;

    /**
     * 기본 생성자.
     *
     * @param model     HDFS File Uploader XML의 JAXB ROOT
     * @param evaluator Expression Language Evaluator
     */
    public JobContextImpl(Flamingo model, ELEvaluator evaluator) {
        this.model = model;
        this.props = this.globalVariablesToProperties();
        this.evaluator = evaluator;
        this.startDate = new Date();
    }

    /**
     * 글로별 변수를 {@link java.util.Properties}로 변환한다.
     *
     * @return 문자열 Key Value의 Properties
     */
    protected Properties globalVariablesToProperties() {
        if (model != null && model.getGlobalVariables() != null) {
            List<GlobalVariable> vars = model.getGlobalVariables().getGlobalVariable();
            Properties props = new Properties();
            for (GlobalVariable var : vars) {
                props.put(var.getName(), var.getValue());
            }
            return props;
        }
        return new Properties();
    }

    /**
     * Hadoop Cluster의 이름으로 Cluster의 Hadoop Configuration을 생성한다.
     *
     * @param clusterName Hadoop Cluster명
     * @return {@link org.apache.hadoop.conf.Configuration}
     */
    public Configuration getConfiguration(String clusterName) {
        Configuration configuration = new Configuration();
        List<Cluster> clusters = model.getClusters().getCluster();
        for (Cluster cluster : clusters) {
            if (clusterName.equals(cluster.getName())) {
                configuration.set(HDFS_URL, cluster.getFsDefaultName());
                configuration.set(JOB_TRACKER, cluster.getMapredJobTracker());

                List<Property> properties = cluster.getProperties().getProperty();
                for (Property property : properties) {
                    configuration.set(property.getName(), property.getValue());
                }
            }
        }
        return configuration;
    }

    /**
     * Uploader에 정의되어 있는 Hadoop Cluster 정보를 Cluster Name을 Key로 하는 Map을 반환한다.
     *
     * @return Cluster Name을 Key로, Configuration을 Value로 하는 Map
     */
    public Map<String, Configuration> getConfigurationMap() {
        Map<String, Configuration> map = new HashMap<String, Configuration>();
        List<Cluster> clusters = model.getClusters().getCluster();
        for (Cluster cluster : clusters) {
            Configuration configuration = getConfiguration(cluster.getName());
            map.put(cluster.getName(), configuration);
        }
        return map;
    }

    /**
     * Properties에서 지정한 정규표현식이 포함되어 있는 Key를 기준으로 값을 꺼내온다.
     *
     * @param props Property
     * @param regex Regular Expression
     * @return Value
     */
    public String substituteVars(Properties props, String regex) {
        if (regex == null) {
            return null;
        }
        Matcher match = variableRegex.matcher("");
        String eval = regex;
        for (int index = 0; index < MAX_DEPTH; index++) {
            match.reset(eval);
            if (!match.find()) {
                return eval;
            }
            String var = match.group();
            var = var.substring(2, var.length() - 1); // ${ .. } 제거
            String val = null;
            try {
                val = System.getProperty(var);
            } catch (SecurityException se) {
                logger.warn("System Properties에서 파라미터에 접근할 수 없습니다.", se);
            }
            if (val == null) {
                val = substituteVars(props, var);
            }
            if (val == null) {
                return eval; // return literal ${var}: var is unbound
            }
            // evaluate Expresion Language
            eval = eval.substring(0, match.start()) + val + eval.substring(match.end());
        }
        throw new ELEvaluationException("Expression Language를 해석할 수 없습니다.", new IllegalArgumentException("변수를 찾아내기 찾아내야 하는 깊이가 너무 깊습니다. " + MAX_DEPTH + " " + regex));
    }

    /**
     * Property의 <code>name</code>에 해당하는 값을 반환한다. 해당 속성값이 존재하지 않으면 <code>null</code>을 반환한다.
     * <code>name</code>에 해당하는 값은 변수의 expression을 처리를 통해서 값을 얻는다.
     *
     * @param name Property 명
     * @return Property의 <code>name</code>에 해당하는 값, 존재하지 않는 경우 <code>null</code>
     */
    public String getValue(String name) {
        return evaluate(substituteVars(props, name));
    }

    @Override
    public Flamingo getModel() {
        return this.model;
    }

    @Override
    public Date getStartDate() {
        return this.startDate;
    }

    /**
     * 주어진 값에 포함되어 있는 EL을 Evaluator를 이용하여 EL과 Function을 해석한다.
     *
     * @param value EL을 포함하는 문자열
     * @return EL과 Function을 해석한 문자열
     */
    public String evaluate(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        try {
            return evaluator.evaluate(value, String.class);
        } catch (Exception e) {
            logger.warn(ExceptionUtils.getMessage("EL이 포함되어 있는 문자열({})을 해석할 수 없습니다.", value), e);
            return value;
        }
    }

    /**
     * Property의 <code>name</code>에 해당하는 값을 반환한다. 해당 속성값이 존재하지 않으면 <code>null</code>을 반환한다.
     *
     * @param props Property
     * @param name  Property 명
     * @return Property의 <code>name</code>에 해당하는 값, 존재하지 않는 경우 <code>null</code>
     */
    public String getRawValue(Properties props, String name) {
        return props.getProperty(name);
    }

}

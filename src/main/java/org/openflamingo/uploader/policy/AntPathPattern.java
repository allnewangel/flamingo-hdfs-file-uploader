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
import org.springframework.util.AntPathMatcher;

/**
 * Start With File Selection Pattern.
 *
 * @author Edward KIM
 * @since 0.1
 */
public class AntPathPattern implements SelectorPattern {

	/**
	 * 파일명이 지정한 문자열로 시작하는지 확인하기 위한 패턴
	 */
	private String pattern;

	/**
	 * HDFS File Uploader Job Context
	 */
	private JobContext jobContext;

	/**
	 * 기본 생성자.
	 *
	 * @param pattern Start With에 적용할 문자열 패턴
	 */
	public AntPathPattern(String pattern, JobContext jobContext) {
		this.pattern = pattern;
		this.jobContext = jobContext;
	}

	@Override
	public boolean accept(String filename) {
		String evaluated = jobContext.getValue(filename);
		return new AntPathMatcher().match(pattern, evaluated);
	}

	@Override
	public JobContext getJobContext() {
		return jobContext;
	}
}

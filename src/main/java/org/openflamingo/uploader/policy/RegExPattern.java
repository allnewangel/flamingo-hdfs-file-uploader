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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Regular Expression File Selection Pattern.
 *
 * @author Edward KIM
 * @since 0.1
 */
public class RegExPattern implements SelectorPattern {

	/**
	 * HDFS File Uploader Job Context
	 */
	private JobContext jobContext;

	/**
	 * Regular Expression Pattern
	 */
	Pattern pattern;

	/**
	 * 기본 생성자.
	 *
	 * @param pattern Start With에 적용할 문자열 패턴
	 */
	public RegExPattern(String pattern, JobContext jobContext) {
		this.pattern = Pattern.compile(pattern);
		this.jobContext = jobContext;
	}

	@Override
	public boolean accept(String filename) {
		String evaluated = jobContext.getValue(filename);
		Matcher matcher = pattern.matcher(evaluated);
		return matcher.find();
	}

	@Override
	public JobContext getJobContext() {
		return jobContext;
	}
}
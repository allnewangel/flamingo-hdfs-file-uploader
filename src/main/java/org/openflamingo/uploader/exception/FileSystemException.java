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
package org.openflamingo.uploader.exception;

/**
 * 파일 시스템을 처리할 수 없는 경우 던지는 예외.
 *
 * @author Edward KIM
 * @since 0.1
 */
public class FileSystemException extends SystemException {

	public FileSystemException() {
		super();
	}

	public FileSystemException(String message) {
		super(message);
	}

	public FileSystemException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public FileSystemException(Throwable throwable) {
		super(throwable);
	}
}

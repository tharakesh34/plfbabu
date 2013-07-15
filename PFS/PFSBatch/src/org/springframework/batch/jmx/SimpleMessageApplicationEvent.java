/*
 * Copyright 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.batch.jmx;

import org.springframework.context.ApplicationEvent;

public class SimpleMessageApplicationEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;
	private String message;

	public SimpleMessageApplicationEvent(Object source, String message) {
		super(source);
		this.message = message;
	}
	
	/* (non-Javadoc)
	 * @see java.util.EventObject#toString()
	 */
	public String toString() {
		return "message=["+message+"], " + super.toString();
	}

}
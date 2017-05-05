/**
 *  Copyright 2009 The Revere Group
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pennant.search;

import java.util.Collection;

/**
 * Utilities for TRG Generic DAO Search
 */
public class InternalUtil {
	private InternalUtil() {
		super();
	}

	/**
	 * Convert Object into String Format
	 * 
	 * @param val
	 *            (Object)
	 * 
	 * @return String
	 */
	public static String paramDisplayString(Object val) {
		if (val == null) {
			return "null";
		} else if (val instanceof String) {
			return "'" + val.toString().replace("'", "''") + "'";
		} else if (val instanceof Collection) {
			StringBuilder sb = new StringBuilder();
			sb.append(val.getClass().getSimpleName());
			sb.append(" {");
			boolean first = true;
			for (Object o : (Collection<?>) val) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append(paramDisplayString(o));
			}
			sb.append("}");
			return sb.toString();
		} else if (val instanceof Object[]) {
			StringBuilder sb = new StringBuilder();
			sb.append(val.getClass().getComponentType().getSimpleName());
			sb.append("[] {");
			boolean first = true;
			for (Object o : (Object[]) val) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append(paramDisplayString(o));
			}
			sb.append("}");
			return sb.toString();
		} else {
			return val.toString();
		}
	}
}

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
package com.pennanttech.pennapps.jdbc.search;

import java.util.List;

/**
 * <code>IMutableSearch</code> is an extension of <code>ISearch</code> that
 * provides setters for all of the properties.
 * 
 */
public interface IMutableSearch extends ISearch {
	IMutableSearch setFirstResult(int firstResult);

	IMutableSearch setMaxResults(int maxResults);

	IMutableSearch setPage(int page);

	IMutableSearch setSearchClass(Class<?> searchClass);

	IMutableSearch setFilters(List<Filter> filters);

	IMutableSearch setDisjunction(boolean disjunction);

	IMutableSearch setSorts(List<Sort> sorts);

	IMutableSearch setFields(List<Field> fields);

	IMutableSearch setDistinct(boolean distinct);

	IMutableSearch setFetches(List<String> fetches);

	IMutableSearch setResultMode(int resultMode);
}

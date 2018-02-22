/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */
package com.pennanttech.pennapps.jdbc.search;

import java.util.List;

/**
 * <code>IMutableSearch</code> is an extension of <code>ISearch</code> that provides setters for all of the properties.
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

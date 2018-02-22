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

import java.io.Serializable;
import java.util.List;

/**
 * This class is used to return the results of <code>searchAndCount()</code> operations. It has just two properties: the
 * results and the search and the total (unpaged) count of the search.
 */
public class SearchResult<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	protected List<T> result;
	protected int totalCount = -1;

	public SearchResult() {
		super();
	}

	/**
	 * The results of the search.
	 * 
	 * @return The List of results
	 */
	public List<T> getResult() {
		return result;
	}

	/**
	 * The results of the search.
	 * 
	 * @param results
	 *            The list of results
	 */
	public void setResult(List<T> results) {
		this.result = results;
	}

	/**
	 * The total number of results that would have been returned if no maxResults had been specified. (-1 means
	 * unspecified.)
	 * 
	 * @return The total number of results
	 */
	public int getTotalCount() {
		return totalCount;
	}

	/**
	 * The total number of results that would have been returned if no maxResults had been specified. (-1 means
	 * unspecified.)
	 * 
	 * @param totalCount
	 *            The total number of results
	 * 
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}

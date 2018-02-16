/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  JdbcSearchSupportImpl.java											*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/
package com.pennant.backend.dao.impl;

import java.io.Serializable;
import java.util.List;

import javax.sql.DataSource;

import com.pennant.backend.dao.JdbcSearchSupport;
import com.pennanttech.pennapps.jdbc.search.ISearch;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.jdbc.search.SearchResult;

public class JdbcSearchSupportImpl implements JdbcSearchSupport, Serializable {
	private static final long	serialVersionUID	= -3473489484821533407L;

	private SearchProcessor	jdbcSearchProcessor;

	public JdbcSearchSupportImpl(DataSource dataSource) {
		super();

		jdbcSearchProcessor = new SearchProcessor(dataSource);
	}

	@Override
	public <T> List<T> search(ISearch search) {
		return jdbcSearchProcessor.getResults(search);
	}

	@Override
	public <T> SearchResult<T> searchAndCount(ISearch search) {
		return jdbcSearchProcessor.getResults(search, true);
	}
}

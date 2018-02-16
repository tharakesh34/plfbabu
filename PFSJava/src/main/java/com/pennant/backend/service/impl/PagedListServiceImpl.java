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
 * FileName    		:  PagedListServiceImpl.java											*                           
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
package com.pennant.backend.service.impl;

import java.io.Serializable;
import java.util.List;

import com.pennant.backend.dao.JdbcSearchSupport;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennanttech.pennapps.jdbc.search.SearchResult;

public class PagedListServiceImpl implements PagedListService, Serializable {
	private static final long	serialVersionUID	= 317035964609683048L;

	private JdbcSearchSupport	jdbcSearchSupport;

	public PagedListServiceImpl() {
		super();
	}

	@Override
	public <T> List<T> getBySearchObject(JdbcSearchObject<T> so) {
		return jdbcSearchSupport.search(so);
	}

	@Override
	public <T> SearchResult<T> getSRBySearchObject(JdbcSearchObject<T> so) {
		return jdbcSearchSupport.searchAndCount(so);
	}

	public void setJdbcSearchSupport(JdbcSearchSupport jdbcSearchSupport) {
		this.jdbcSearchSupport = jdbcSearchSupport;
	}
}

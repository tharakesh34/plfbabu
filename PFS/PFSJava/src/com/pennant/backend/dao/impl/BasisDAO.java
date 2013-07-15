
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
 * FileName    		:  BasisDAO.java														*                           
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
import java.util.Collection;

import org.springframework.dao.DataAccessException;

import com.pennant.backend.dao.NextidviewDAO;
public abstract class BasisDAO<T> {
	private NextidviewDAO nextidviewDAO;
	
	protected BasisDAO() {
	}

	protected void initialize(final Object proxy) throws DataAccessException {
	}

	public long save(T entity,String seqName) throws DataAccessException {
		return (long) save(entity, seqName);
	}

	public void saveOrUpdate(T entity,String seqName) throws DataAccessException {
	}

	public void update(T entity) throws DataAccessException {
	}

	public void delete(T entity) throws DataAccessException {
	}

	protected void deleteAll(Collection<T> entities) throws DataAccessException {
	}

	protected T get(Class<T> entityClass, Serializable id) throws DataAccessException {
		return (T) get(entityClass, id);
	}


	public void setNextidviewDAO(NextidviewDAO nextidviewDAO) {
		this.nextidviewDAO = nextidviewDAO;
	}

	public NextidviewDAO getNextidviewDAO() {
		return nextidviewDAO;
	}

}

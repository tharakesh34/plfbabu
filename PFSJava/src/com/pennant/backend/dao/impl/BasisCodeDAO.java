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
 * FileName    		:  BasicCodeDAO.java													*                           
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

import com.pennant.backend.dao.ErrorDetailsDAO;
import com.pennant.backend.model.ErrorDetails;

public abstract class BasisCodeDAO<T> {
	private ErrorDetailsDAO errorDetailsDAO;
	
	/**
	 * constructor
	 */
	protected BasisCodeDAO() {
	}

	protected void initialize(final Object proxy) throws DataAccessException {
	}

	public void save(T entity) throws DataAccessException {
	}

	public void saveOrUpdate(T entity) throws DataAccessException {
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
	
	public ErrorDetails getErrorDetail (String errorId,String errorLanguage,String[] parameters){
		return getErrorDetailsDAO().getErrorDetail("",errorId, errorLanguage, parameters);
	}

	public ErrorDetails getErrorDetail (String errorField,String errorId,String errorLanguage,String[] parameters){
		return getErrorDetailsDAO().getErrorDetail(errorField,errorId, errorLanguage, parameters);
	}

	public ErrorDetailsDAO getErrorDetailsDAO() {
		return errorDetailsDAO;
	}

	public void setErrorDetailsDAO(ErrorDetailsDAO errorDetailsDAO) {
		this.errorDetailsDAO = errorDetailsDAO;
	}

}

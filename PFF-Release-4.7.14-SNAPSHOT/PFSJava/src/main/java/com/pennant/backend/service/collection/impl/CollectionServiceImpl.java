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
package com.pennant.backend.service.collection.impl;

import java.util.List;

import com.pennant.backend.dao.collection.CollectionDAO;
import com.pennant.backend.model.collection.Collection;
import com.pennant.backend.service.collection.CollectionService;

/**
 * Service declaration for methods that depends on <b>CollectionService</b>.<br>
 * 
 */
public class CollectionServiceImpl implements CollectionService{
	
	private CollectionDAO collectionDAO; 

	public CollectionServiceImpl() {
		super();
	}

	@Override
	public List<Collection> getCollectionTablesList() {
		return collectionDAO.getCollectionTablesList();
	}
	
	@Override
	public int getCollectionExecutionSts(){
		return collectionDAO.getCollectionExecutionSts();
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public CollectionDAO getCollectionDAO() {
		return collectionDAO;
	}
	
	public void setCollectionDAO(CollectionDAO collectionDAO) {
		this.collectionDAO = collectionDAO;
	}
}
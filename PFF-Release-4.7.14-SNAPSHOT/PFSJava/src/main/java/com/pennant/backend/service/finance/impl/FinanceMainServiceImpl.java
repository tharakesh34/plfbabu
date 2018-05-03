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
 *																							*
 * FileName    		:  FinanceMainServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-11-2011    														*
 *                                                                  						*
 * Modified Date    :  15-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-11-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.finance.impl;

import java.util.List;

import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinanceMainService;

/**
 * Service implementation for methods that depends on <b>FinanceMain</b>.
 */
public class FinanceMainServiceImpl extends GenericService<FinanceMain> implements FinanceMainService {
	private FinanceMainDAO	financeMainDAO;

	public FinanceMainServiceImpl() {
		super();
	}

	/**
	 * getFinanceMainById fetch the details by using FinanceMainDAO's getFinanceMainById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceMain
	 */
	@Override
	public FinanceMain getFinanceMainById(String id, boolean isWIF) {
		return financeMainDAO.getFinanceMainById(id, "_View", isWIF);
	}

	/**
	 * Method to get Finance related data.
	 * 
	 * @param financeReference
	 *            (String)
	 * @param isWIF
	 *            (boolean)
	 */
	@Override
	public List<FinanceEnquiry> getFinanceDetailsByCustId(long custId) {
		return financeMainDAO.getFinanceDetailsByCustId(custId);
	}

	/**
	 * Method for fetch number of records from financeMain using reference and mandateId
	 * 
	 * @param finReference
	 * @param mandateID
	 * @return Integer
	 */
	@Override
	public int getFinanceCountById(String finReference, long mandateID) {
		return financeMainDAO.getFinanceCountById(finReference, mandateID);
	}

	/**
	 * Method for do mandate swapping against the finReference.
	 * 
	 * @param finReference
	 * @param newMandateID
	 * @return Integer
	 */
	@Override
	public int loanMandateSwapping(String finReference, long newMandateID) {
		return financeMainDAO.loanMandateSwapping(finReference, newMandateID);
	}

	/**
	 * 
	 */
	@Override
	public int getFinanceCountById(String finReference, boolean isWIF) {
		return financeMainDAO.getFinanceCountById(finReference, "", isWIF);
	}

	@Override
	public int updateFinanceBasicDetails(FinanceMain financeMain) {
		return financeMainDAO.updateFinanceBasicDetails(financeMain, "");
	}

	/**
	 * Method to get Finance related data.
	 * @param custId
	 */
	@Override
	public List<FinanceMain> getFinanceByCustId(long custId) {
		return financeMainDAO.getFinanceByCustId(custId);
	}
	/**
	 * Method to get Finance related data.
	 * @param collateralRef
	 */
	@Override
	public List<FinanceMain> getFinanceByCollateralRef(String collateralRef) {
		return financeMainDAO.getFinanceByCollateralRef(collateralRef);
	}
	
	/**
	 * Method to get FinanceReferences by Given MandateId.
	 * @param mandateId
	 */
	@Override
	public List<String> getFinReferencesByMandateId(long mandateId) {
		return financeMainDAO.getFinReferencesByMandateId(mandateId);
	}
	/**
	 * Method to get FinanceReferences by Given custId with FinActiveStatus.
	 * @param custId
	 * @param finActiveStatus
	 */
	@Override
	public List<String> getFinReferencesByCustID(long custId, String finActiveStatus) {
		return financeMainDAO.getFinReferencesByCustID(custId,finActiveStatus);
	}

	@Override
	public List<String> getFinanceMainbyCustId(long custID) {
		return financeMainDAO.getFinReferencesByCustID(custID);
	}
	/**
	 * @param financeMainDAO
	 *            the financeMainDAO to set
	 */
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}


	
}

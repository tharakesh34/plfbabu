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
 * FileName    		:  SuspenseServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.financemanagement.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.backend.dao.finance.FinanceSuspHeadDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.financemanagement.SuspenseService;

/**
 * Service implementation for methods that depends on <b>FinanceSuspHead</b>.<br>
 * 
 */
public class SuspenseServiceImpl extends GenericService<FinanceSuspHead> implements SuspenseService {
	
	private final static Logger logger = Logger.getLogger(SuspenseServiceImpl.class);
	
	private FinanceSuspHeadDAO financeSuspHeadDAO;
	private PostingsDAO postingsDAO;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public PostingsDAO getPostingsDAO() {
    	return postingsDAO;
    }
	public void setPostingsDAO(PostingsDAO postingsDAO) {
    	this.postingsDAO = postingsDAO;
    }
	
	public FinanceSuspHeadDAO getFinanceSuspHeadDAO() {
    	return financeSuspHeadDAO;
    }
	public void setFinanceSuspHeadDAO(FinanceSuspHeadDAO financeSuspHeadDAO) {
    	this.financeSuspHeadDAO = financeSuspHeadDAO;
    }
	
	@Override
	public FinanceSuspHead getFinanceSuspHead() {
		return getFinanceSuspHeadDAO().getFinanceSuspHead();
	}
	
	@Override
	public FinanceSuspHead getNewFinanceSuspHead() {
		return getFinanceSuspHeadDAO().getNewFinanceSuspHead();
	}
	
	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table FinFinanceSuspHeads/FinFinanceSuspHeads_Temp 
	 * 			by using FinanceSuspHeadDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using FinanceSuspHeadDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtFinFinanceSuspHeads by using auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public void updateSuspense(FinanceSuspHead suspHead) {
		logger.debug("Entering");	
		String tableType="";
		getFinanceSuspHeadDAO().update(suspHead,tableType);
		logger.debug("Leaving");
	}

	/**
	 * getFinanceSuspHeadById fetch the details by using 
	 * FinanceSuspHeadDAO's getFinanceSuspHeadById method.
	 * 
	 * @param finRef
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceSuspHead
	 */
	@Override
	public FinanceSuspHead getFinanceSuspHeadById(String finRef,boolean isEnquiry) {
		FinanceSuspHead suspHead = getFinanceSuspHeadDAO().getFinanceSuspHeadById(finRef,"_View");
		if(suspHead != null && isEnquiry){
			suspHead.setSuspDetailsList(getFinanceSuspHeadDAO().getFinanceSuspDetailsListById(finRef));
			suspHead.setSuspPostingsList(getPostingsDAO().getPostingsByFinRefAndEvent(
					suspHead.getFinReference(), "'M_AMZ','M_NONAMZ'",true));
		}
		return suspHead;
	}
	
	/**
	 * getSuspFinanceList fetch the FinReference details by using 
	 * FinanceSuspHeadDAO's .
	 * 
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceSuspHead
	 */
	@Override
	public List<String> getSuspFinanceList() {
		return getFinanceSuspHeadDAO().getSuspFinanceList();
	}
	
	/**
	 * This method refresh the Record.
	 * @param FinanceSuspHead (suspHead)
 	 * @return suspHead
	 */
	@Override
	public FinanceSuspHead refresh(FinanceSuspHead suspHead) {
		logger.debug("Entering");
		getFinanceSuspHeadDAO().refresh(suspHead);
		getFinanceSuspHeadDAO().initialize(suspHead);
		logger.debug("Leaving");
		return suspHead;
	}


}

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
 * FileName    		:  UploadHeaderServiceImpl.java                                         * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-12-2017    														*
 *                                                                  						*
 * Modified Date    :  17-12-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.backend.dao.finance.FinExpenseDetailsDAO;
import com.pennant.backend.dao.finance.FinExpenseMovementsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.UploadFinExpensesDAO;
import com.pennant.backend.dao.finance.UploadFinTypeExpenseDAO;
import com.pennant.backend.dao.finance.UploadHeaderDAO;
import com.pennant.backend.dao.finance.UploadTaxPercentDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.expenses.FinExpenseDetails;
import com.pennant.backend.model.expenses.FinExpenseMovements;
import com.pennant.backend.model.expenses.UploadFinExpenses;
import com.pennant.backend.model.expenses.UploadFinTypeExpense;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.expenses.UploadTaxPercent;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinTypeExpense;
import com.pennant.backend.service.amtmasters.ExpenseTypeService;
import com.pennant.backend.service.feetype.FeeTypeService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.UploadHeaderService;
import com.pennant.backend.service.rmtmasters.FinTypeExpenseService;

/**
 * Service implementation for methods that depends on <b>FinancePurposeDetail</b>.<br>
 * 
 */
public class UploadHeaderServiceImpl implements UploadHeaderService {
	private static final Logger logger = Logger.getLogger(UploadHeaderServiceImpl.class);

	private UploadHeaderDAO 		uploadHeaderDAO;
	private UploadFinExpensesDAO 	uploadFinExpensesDAO;
	private FinExpenseDetailsDAO 	finExpenseDetailsDAO;
	private FinExpenseMovementsDAO 	finExpenseMovementsDAO;
	private ExpenseTypeService		expenseTypeService;
	private FinanceMainDAO			financeMainDAO;
	private FinanceTypeDAO 			financeTypeDAO;
	private FinTypeExpenseService finTypeExpenseService;
	private FeeTypeService feeTypeService;
	private UploadTaxPercentDAO uploadTaxPercentDAO;
	private FinFeeDetailService finFeeDetailService;
	private UploadFinTypeExpenseDAO uploadFinTypeExpenseDAO;
	
	public UploadHeaderServiceImpl() {
		super();
	}

	@Override
	public UploadHeader getUploadHeader(long uploadId) {
		return this.uploadHeaderDAO.getUploadHeader(uploadId);
	}

	@Override
	public boolean isFileNameExist(String fileName) {
		return this.uploadHeaderDAO.isFileNameExist(fileName);
	}
	
	@Override
	public long save (UploadHeader uploadHeader) {
		return this.uploadHeaderDAO.save(uploadHeader);
	}
	
	@Override
	public void saveUploadFinExpenses(List<UploadFinExpenses> uploadFinExpensesList) {
		logger.debug("Entering");
		
		this.uploadFinExpensesDAO.saveUploadFinExpenses(uploadFinExpensesList);
		
		logger.debug("Leaving");
	}
	
	@Override
	public List<FinanceMain> getFinancesByExpenseType (String finType, Date finApprovalStartDate, Date finApprovalEndDate) {
		return this.financeMainDAO.getFinancesByExpenseType(finType, finApprovalStartDate, finApprovalEndDate);
	}
	
	@Override
	public long saveFinExpenseDetails(FinExpenseDetails finExpenseDetails) {
		return this.finExpenseDetailsDAO.saveFinExpenseDetails(finExpenseDetails);
	}
	
	@Override
	public long saveFinExpenseMovements(FinExpenseMovements finExpenseMovements) {
		return this.finExpenseMovementsDAO.saveFinExpenseMovements(finExpenseMovements);
	}
	
	@Override
	public long getFinExpenseIdByExpType(String expTypeCode) {
		return this.expenseTypeService.getFinExpenseIdByExpType(expTypeCode, "");
	}

	@Override
	public FinExpenseDetails getFinExpenseDetailsByReference(String finReference, long expenseTypeId) {
		return this.finExpenseDetailsDAO.getFinExpenseDetailsByReference(finReference, expenseTypeId);
	}

	@Override
	public FinanceMain getFinancesByFinReference(String finReference) {
		return this.financeMainDAO.getFinanceMainForRpyCancel(finReference);
	}
	
	@Override
	public int getFinTypeCount(String finType) {
		return this.financeTypeDAO.getFinTypeCount(finType, "");
	}
	
	@Override
	public int getFinanceCountById(String finReference) {
		return this.financeMainDAO.getFinanceCountById(finReference, "", false);
	}

	@Override
	public void update(FinExpenseDetails finExpenseDetails) {
		logger.debug("Entering");
		
		this.finExpenseDetailsDAO.update(finExpenseDetails);
		
		logger.debug("Leaving");
	}
	

	@Override
	public void updateRecordCounts(UploadHeader uploadHeader) {
		logger.debug("Entering");
		
		this.uploadHeaderDAO.updateRecordCounts(uploadHeader);
		
		logger.debug("Leaving");
	}
	
	@Override
	public List<FinExpenseDetails> getFinExpenseDetailById(String reference) {
		return this.finExpenseDetailsDAO.getFinExpenseDetailsById(reference);
	}

	@Override
	public List<FinExpenseMovements> getFinExpenseMovementById(String reference,long finExpenseId) {
		return this.finExpenseMovementsDAO.getFinExpenseMovementById(reference, finExpenseId);
	}
	
	@Override
	public FinTypeExpense getFinExpensesByFinType(String finType, long expenseTypeId) {
		return finTypeExpenseService.getFinExpensesByFinType(finType, expenseTypeId);
	}
	
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		
		return finTypeExpenseService.doApprove(auditHeader);
	}
	
	@Override
	public long getFinFeeTypeIdByFeeType(String feeTypeCode) {
	
		return feeTypeService.getFinFeeTypeIdByFeeType(feeTypeCode);
	}
	
	@Override
	public void saveFeeUploadDetails(List<UploadTaxPercent> uploadDetailsList) {
		
		this.uploadTaxPercentDAO.saveUploadDetails(uploadDetailsList);
	}
	
	@Override
	public void saveExpenseUploadDetails(List<UploadFinTypeExpense> uploadDetailsList) {
		this.uploadFinTypeExpenseDAO.saveUploadDetails(uploadDetailsList);
		
	}
	
	@Override
	public List<UploadTaxPercent> getSuccesFailedCountForFactor(long uploadId) {
		return this.uploadTaxPercentDAO.getSuccesFailedCount(uploadId);
	}

	@Override
	public List<UploadFinTypeExpense> getSuccesFailedCountExpense(long uploadId) {

		return this.uploadFinTypeExpenseDAO.getSuccesFailedCount(uploadId);
	}
	
	@Override
	public void updateTaxPercent(UploadTaxPercent taxPercent) {
		
		finFeeDetailService.updateTaxPercent(taxPercent);
	}
	@Override
	public void updateRecord(UploadHeader uploadHeader) {
		this.uploadHeaderDAO.updateRecord(uploadHeader);
		
	}
	
	public void setUploadHeaderDAO(UploadHeaderDAO uploadHeaderDAO) {
		this.uploadHeaderDAO = uploadHeaderDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public void setUploadFinExpensesDAO(UploadFinExpensesDAO uploadFinExpensesDAO) {
		this.uploadFinExpensesDAO = uploadFinExpensesDAO;
	}

	public void setFinExpenseDetailsDAO(FinExpenseDetailsDAO finExpenseDetailsDAO) {
		this.finExpenseDetailsDAO = finExpenseDetailsDAO;
	}

	public void setExpenseTypeService(ExpenseTypeService expenseTypeService) {
		this.expenseTypeService = expenseTypeService;
	}

	public void setFinExpenseMovementsDAO(FinExpenseMovementsDAO finExpenseMovementsDAO) {
		this.finExpenseMovementsDAO = finExpenseMovementsDAO;
	}

	public FinTypeExpenseService getFinTypeExpenseService() {
		return finTypeExpenseService;
	}

	public void setFinTypeExpenseService(FinTypeExpenseService finTypeExpenseService) {
		this.finTypeExpenseService = finTypeExpenseService;
	}

	public FeeTypeService getFeeTypeService() {
		return feeTypeService;
	}

	public void setFeeTypeService(FeeTypeService feeTypeService) {
		this.feeTypeService = feeTypeService;
	}

	public UploadTaxPercentDAO getUploadTaxPercentDAO() {
		return uploadTaxPercentDAO;
	}

	public void setUploadTaxPercentDAO(UploadTaxPercentDAO uploadTaxPercentDAO) {
		this.uploadTaxPercentDAO = uploadTaxPercentDAO;
	}

	public FinFeeDetailService getFinFeeDetailService() {
		return finFeeDetailService;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	public UploadFinTypeExpenseDAO getUploadFinTypeExpenseDAO() {
		return uploadFinTypeExpenseDAO;
	}

	public void setUploadFinTypeExpenseDAO(UploadFinTypeExpenseDAO uploadFinTypeExpenseDAO) {
		this.uploadFinTypeExpenseDAO = uploadFinTypeExpenseDAO;
	}
}
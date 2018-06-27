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
 * FileName    		:  CustomerIncomeServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.customermasters.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.customermasters.CustomerIncomeDAO;
import com.pennant.backend.dao.systemmasters.IncomeTypeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.systemmasters.IncomeType;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerIncomeService;
import com.pennant.backend.service.customermasters.validation.CustomerIncomeValidation;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.dao.customer.income.IncomeDetailDAO;

/**
 * Service implementation for methods that depends on <b>CustomerIncome</b>.<br>
 * 
 */
public class CustomerIncomeServiceImpl extends GenericService<CustomerIncome> implements CustomerIncomeService {

	private static Logger logger = Logger.getLogger(CustomerIncomeServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;	
	private CustomerIncomeDAO customerIncomeDAO;
	@Autowired
	private IncomeDetailDAO incomeDetailDAO;	
	private CustomerIncomeValidation customerIncomeValidation;
	private CustomerDAO customerDAO;
	private IncomeTypeDAO incomeTypeDAO;

	public CustomerIncomeServiceImpl() {
		super();
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public CustomerIncomeDAO getCustomerIncomeDAO() {
		return customerIncomeDAO;
	}
	public void setCustomerIncomeDAO(CustomerIncomeDAO customerIncomeDAO) {
		this.customerIncomeDAO = customerIncomeDAO;
	}

	public CustomerIncomeValidation getCustomerIncomeValidation() {

		if(customerIncomeValidation==null){
			this.customerIncomeValidation = new CustomerIncomeValidation(customerIncomeDAO);
		}
		return this.customerIncomeValidation;
	}

	/**
	 * saveOrUpdate	method method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Do Add or Update the Record 
	 * 		a)	Add new Record for the new record in the DB table CustomerIncomes/CustomerIncomes_Temp 
	 * 			by using CustomerIncomeDAO's save method 
	 * 		b)  Update the Record in the table. based on the module workFlow Configuration.
	 * 			by using CustomerIncomeDAO's update method
	 * 3)	Audit the record in to AuditHeader and AdtCustomerIncomes by using 
	 * 			auditHeaderDAO.addAudit(auditHeader)
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader,"saveOrUpdate");
		logger.debug("Leaving");
		if (!auditHeader.isNextProcess()){
			return auditHeader;
		}

		String tableType="";
		CustomerIncome customerIncome = (CustomerIncome) auditHeader.getAuditDetail().getModelData();
		
		if (customerIncome.isWorkflow()) {
			tableType="_Temp";
		}
		
		if (customerIncome.isNew()) {
			customerIncomeDAO.setLinkId(customerIncome);
			incomeDetailDAO.save(customerIncome, tableType);
			auditHeader.getAuditDetail().setModelData(customerIncome);
		}else{
			incomeDetailDAO.update(customerIncome, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);	
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	delete Record for the DB table CustomerIncomes by using 
	 * 			CustomerIncomeDAO's delete method with type as Blank 
	 * 3)	Audit the record in to AuditHeader and AdtCustomerIncomes by using 
	 * 			auditHeaderDAO.addAudit(auditHeader)    
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader,"delete");
		if (!auditHeader.isNextProcess()){
			return auditHeader;
		}

		CustomerIncome customerIncome = (CustomerIncome) auditHeader.getAuditDetail().getModelData();
		incomeDetailDAO.delete(customerIncome.getId(),"");		
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCustomerIncomeById fetch the details by using CustomerIncomeDAO's getCustomerIncomeById method.
	 * @param id (String)
	 * @param  type (String)
	 * 			""/_Temp/_View          
	 * @return CustomerIncome
	 */
	@Override
	public CustomerIncome 	getCustomerIncomeById(CustomerIncome customerIncome){
		return getCustomerIncomeDAO().getCustomerIncomeById(customerIncome,"_View" ,"customer");
	}
	
/*	@Override
	public BigDecimal getTotalIncomeByCustomer(long custId) {
		return getCustomerIncomeDAO().getTotalIncomeByCustomer(custId);
	}*/

	/**
	 * getApprovedCustomerIncomeById fetch the details by using
	 * CustomerIncomeDAO's getCustomerIncomeById method . with parameter id and
	 * type as blank. it fetches the approved records from the CustomerIncomes.
	 * 
	 * @param id
	 *            (String)
	 * @return CustomerIncome
	 */
	public CustomerIncome getApprovedCustomerIncomeById(CustomerIncome customerIncome) {
		return getCustomerIncomeDAO().getCustomerIncomeById(customerIncome,"_AView", "customer");
	}

	/**
	 * doApprove method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	based on the Record type do following actions
	 * 		a)  DELETE	Delete the record from the main table by using 
	 * 				getCustomerIncomeDAO().delete with parameters customerIncome,""
	 * 		b)  NEW		Add new record in to main table by using 
	 * 				getCustomerIncomeDAO().save with parameters customerIncome,""
	 * 		c)  EDIT	Update record in the main table by using 
	 * 				getCustomerIncomeDAO().update with parameters customerIncome,""
	 * 3)	Delete the record from the workFlow table by using 
	 * 			getCustomerIncomeDAO().delete with parameters customerIncome,"_Temp"
	 * 4)	Audit the record in to AuditHeader and AdtCustomerIncomes by using 
	 * 			auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 5)  	Audit the record in to AuditHeader and AdtCustomerIncomes by using 
	 * 			auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType="";
		auditHeader = businessValidation(auditHeader,"doApprove");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		CustomerIncome customerIncome = new CustomerIncome();
		BeanUtils.copyProperties((CustomerIncome) auditHeader.getAuditDetail().getModelData(), customerIncome);		

		if (customerIncome.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType=PennantConstants.TRAN_DEL;
			incomeDetailDAO.delete(customerIncome.getId(),"");				
		} else {
			customerIncome.setRoleCode("");
			customerIncome.setNextRoleCode("");
			customerIncome.setTaskId("");
			customerIncome.setNextTaskId("");
			customerIncome.setWorkflowId(0);

			if (customerIncome.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {	
				tranType=PennantConstants.TRAN_ADD;
				customerIncome.setRecordType("");
				customerIncomeDAO.setLinkId(customerIncome);
				incomeDetailDAO.save(customerIncome,"");
			} else {
				tranType=PennantConstants.TRAN_UPD;
				customerIncome.setRecordType("");
				incomeDetailDAO.update(customerIncome,"");
			}
		}

		if(!StringUtils.equals(customerIncome.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
		incomeDetailDAO.delete(customerIncome.getId(),"_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(customerIncome);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps.
	 * 1)	Do the Business validation by using businessValidation(auditHeader) method
	 * 		if there is any error or warning message then return the auditHeader.
	 * 2)	Delete the record from the workFlow table by using
	 * 			getCustomerIncomeDAO().delete with parameters customerIncome,"_Temp"
	 * 3)	Audit the record in to AuditHeader and AdtCustomerIncomes by using 
	 * 			auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	public AuditHeader  doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader,"doReject");
		if (!auditHeader.isNextProcess()){
			logger.debug("Leaving");
			return auditHeader;
		}

		CustomerIncome customerIncome= (CustomerIncome) auditHeader.getAuditDetail().getModelData();			
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		incomeDetailDAO.delete(customerIncome.getId(),"_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps.
	 * 1)	get the details from the auditHeader. 
	 * 2)	fetch the details from the tables
	 * 3)	Validate the Record based on the record details. 
	 * 4) 	Validate for any business validation.
	 * 
	 * @param AuditHeader (auditHeader)    
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader,String method) {
		logger.debug("Entering");
		auditHeader= getCustomerIncomeValidation().incomeValidation(auditHeader, method);
		auditHeader=nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}
	
	@Override
    public Map<String, BigDecimal> getCustomerIncomeByCustomer(long custID, boolean isWIF) {
	    List<CustomerIncome> list = getCustomerIncomeDAO().getCustomerIncomeByCustomer(custID, "_AView");
	    Map<String, BigDecimal> map = null;
	    if(list != null && list.size() > 0){
	    	map = new HashMap<String, BigDecimal>(list.size());
	    	for (CustomerIncome customerIncome : list) {
	    		 String key = "I_";
	    		if(PennantConstants.EXPENSE.equals(customerIncome.getIncomeExpense())){
	    			key = "E_";
	    		}
	    		key = key+customerIncome.getCategory()+"_"+customerIncome.getIncomeType();

	    		if(customerIncome.isJointCust()){
	    			key = key+"_S";
	    		}else{
	    			key = key+"_P";
	    		}
	    		map.put(key, customerIncome.getIncome());
            }
	    }
		return map;
    }
	
	@Override
    public List<CustomerIncome> getCustomerIncomes(long custID, boolean isWIF) {
	    return getCustomerIncomeDAO().getCustomerIncomeByCustomer(custID, "_AView");
    }

	@Override
	public AuditDetail doValidations(CustomerIncome customerIncome) {
		AuditDetail auditDetail = new AuditDetail();

		if (customerIncome != null) {
			auditDetail.setErrorDetail(validateMasterCode("BMTIncomeTypes", "IncomeExpense",
					customerIncome.getIncomeExpense()));
			auditDetail.setErrorDetail(validateMasterCode("BMTIncomeTypes", "Category", customerIncome.getCategory()));
			auditDetail.setErrorDetail(validateMasterCode("BMTIncomeTypes", "IncomeTypeCode",
					customerIncome.getIncomeType()));
			auditDetail.setErrorDetail(validateMasterCode("BMTIncomeCategory", "IncomeCategory",
					customerIncome.getCategory()));
			IncomeType incomeType = getIncomeTypeDAO().getIncomeTypeById(customerIncome.getIncomeType(),
					customerIncome.getIncomeExpense(), customerIncome.getCategory(), "_AView");
			if (incomeType == null) {
				ErrorDetail errorDetail = new ErrorDetail();
				String[] valueParm = new String[2];
				valueParm[0] = customerIncome.getCustCif();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90113", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
			}

		}
		return auditDetail;
	}

	/**
	 * Validate code or Id value with available masters in system.
	 * 
	 * @param tableName
	 * @param columnName
	 * @param value
	 * 
	 * @return WSReturnStatus
	 */
	private ErrorDetail validateMasterCode(String tableName, String columnName, String value) {
		logger.debug("Entering");

		ErrorDetail errorDetail = new ErrorDetail();
		// validate Master code with PLF system masters
		int count = getCustomerDAO().getLookupCount(tableName, columnName, value);
		if (count <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = columnName;
			valueParm[1] = value;
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm), "EN");
		}

		logger.debug("Leaving");
		return errorDetail;
	}
	@Override
	public int getVersion(CustomerIncome customerIncome) {
		return getCustomerIncomeDAO().getVersion(customerIncome);
	}
	
	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public IncomeTypeDAO getIncomeTypeDAO() {
		return incomeTypeDAO;
	}

	public void setIncomeTypeDAO(IncomeTypeDAO incomeTypeDAO) {
		this.incomeTypeDAO = incomeTypeDAO;
	}
	
}
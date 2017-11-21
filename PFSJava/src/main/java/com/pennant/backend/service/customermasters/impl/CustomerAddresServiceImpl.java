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
 * FileName    		:  CustomerAddresServiceImpl.java                                                   * 	  
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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.dao.systemmasters.CityDAO;
import com.pennant.backend.dao.systemmasters.ProvinceDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerAddresService;
import com.pennant.backend.service.customermasters.validation.CustomerAddressValidation;
import com.pennant.backend.util.PennantConstants;

/**
 * Service implementation for methods that depends on <b>CustomerAddres</b>.<br>
 * 
 */
public class CustomerAddresServiceImpl extends GenericService<CustomerAddres>
		implements CustomerAddresService {

	private static Logger logger = Logger.getLogger(CustomerAddresServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CustomerAddresDAO customerAddresDAO;
	private CustomerAddressValidation customerAddressValidation;
	private CityDAO cityDAO;
	private ProvinceDAO provinceDAO;

	public CustomerAddresServiceImpl() {
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

	public CustomerAddresDAO getCustomerAddresDAO() {
		return customerAddresDAO;
	}
	public void setCustomerAddresDAO(CustomerAddresDAO customerAddresDAO) {
		this.customerAddresDAO = customerAddresDAO;
	}

	public CustomerAddressValidation getAddressValidation(){
		
		if(customerAddressValidation==null){
			this.customerAddressValidation = new CustomerAddressValidation(customerAddresDAO);
		}
		return this.customerAddressValidation;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * CustomerAddresses/CustomerAddresses_Temp by using CustomerAddresDAO's
	 * save method b) Update the Record in the table. based on the module
	 * workFlow Configuration. by using CustomerAddresDAO's update method 3)
	 * Audit the record in to AuditHeader and AdtCustomerAddresses by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		String tableType = "";
		CustomerAddres customerAddres = (CustomerAddres) auditHeader.getAuditDetail().getModelData();

		if (customerAddres.isWorkflow()) {
			tableType = "_Temp";
		}

		if (customerAddres.isNew()) {
			customerAddres.setId(getCustomerAddresDAO().save(customerAddres, tableType));
			auditHeader.getAuditDetail().setModelData(customerAddres);
		/*	auditHeader.setAuditReference(String.valueOf(customerAddres.getId()));*/
		} else {
			getCustomerAddresDAO().update(customerAddres, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table CustomerAddresses by using CustomerAddresDAO's delete method with
	 * type as Blank 3) Audit the record in to AuditHeader and
	 * AdtCustomerAddresses by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		CustomerAddres customerAddres = (CustomerAddres) auditHeader.getAuditDetail().getModelData();
		getCustomerAddresDAO().delete(customerAddres, "");
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCustomerAddresById fetch the details by using CustomerAddresDAO's
	 * getCustomerAddresById method.
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CustomerAddres
	 */
	@Override
	public CustomerAddres getCustomerAddresById(long id, String addType) {
		return getCustomerAddresDAO().getCustomerAddresById(id, addType, "_View");
	}

	/**
	 * getApprovedCustomerAddresById fetch the details by using
	 * CustomerAddresDAO's getCustomerAddresById method . with parameter id and
	 * type as blank. it fetches the approved records from the
	 * CustomerAddresses.
	 * 
	 * @param id
	 *            (String)
	 * @return CustomerAddres
	 */
	public CustomerAddres getApprovedCustomerAddresById(long id, String addType) {
		return getCustomerAddresDAO().getCustomerAddresById(id, addType, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getCustomerAddresDAO().delete with parameters customerAddres,"" b)
	 * NEW Add new record in to main table by using getCustomerAddresDAO().save
	 * with parameters customerAddres,"" c) EDIT Update record in the main table
	 * by using getCustomerAddresDAO().update with parameters customerAddres,""
	 * 3) Delete the record from the workFlow table by using
	 * getCustomerAddresDAO().delete with parameters customerAddres,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtCustomerAddresses by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in
	 * to AuditHeader and AdtCustomerAddresses by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		CustomerAddres customerAddres = new CustomerAddres();
		BeanUtils.copyProperties((CustomerAddres) auditHeader.getAuditDetail()
				.getModelData(), customerAddres);

		if (customerAddres.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getCustomerAddresDAO().delete(customerAddres, "");

		} else {
			customerAddres.setRoleCode("");
			customerAddres.setNextRoleCode("");
			customerAddres.setTaskId("");
			customerAddres.setNextTaskId("");
			customerAddres.setWorkflowId(0);

			if (customerAddres.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				customerAddres.setRecordType("");
				getCustomerAddresDAO().save(customerAddres, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				customerAddres.setRecordType("");
				getCustomerAddresDAO().update(customerAddres, "");
			}
		}

		if(!StringUtils.equals(customerAddres.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
		getCustomerAddresDAO().delete(customerAddres, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(customerAddres);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getCustomerAddresDAO().delete with parameters
	 * customerAddres,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtCustomerAddresses by using auditHeaderDAO.addAudit(auditHeader) for
	 * Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		CustomerAddres customerAddres = (CustomerAddres) auditHeader
				.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCustomerAddresDAO().delete(customerAddres, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader,
			String method) {
		logger.debug("Entering");
		auditHeader = getAddressValidation().addressValidation(auditHeader, method);
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Fetch current version of the record.
	 * 
	 * @param id
	 * @param addrType
	 * @param tableName
	 * @return Integer
	 */
	@Override
	public int getVersion(long id, String addrType) {
		return getCustomerAddresDAO().getVersion(id,addrType);
		
	}
	@Override
	public List<CustomerAddres> getApprovedCustomerAddresById(long id) {
		return getCustomerAddresDAO().getCustomerAddresByCustomer(id, "_AView");
	}

	@Override
	public AuditDetail doValidations(CustomerAddres customerAddres,String method) {
		AuditDetail auditDetail = new AuditDetail();
		ErrorDetails errorDetail = new ErrorDetails();

		if (StringUtils.equals(method, "Create")) {
			List<CustomerAddres> prvCustomerAddrList = customerAddresDAO
					.getCustomerAddresByCustomer(customerAddres.getCustID(), "");
			if (prvCustomerAddrList != null && !prvCustomerAddrList.isEmpty()) {
				for (CustomerAddres prvcustAddr : prvCustomerAddrList) {
					if (prvcustAddr.getCustAddrPriority() == customerAddres.getCustAddrPriority()) {
						String[] valueParm = new String[2];
						valueParm[0] = "Priority";
						valueParm[1] = String.valueOf(customerAddres.getCustAddrPriority());
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("30702", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
						return auditDetail;
					}
					if (StringUtils.equals(prvcustAddr.getCustAddrType(), customerAddres.getCustAddrType())) {
						String[] valueParm = new String[2];
						valueParm[0] = "AddressType";
						valueParm[1] = customerAddres.getCustAddrType();
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("41001", "", valueParm), "EN");
						auditDetail.setErrorDetail(errorDetail);
						return auditDetail;
					}
				}
			} else {
				if (customerAddres.getCustAddrPriority() != Integer.valueOf(PennantConstants.EMAILPRIORITY_VeryHigh)) {
					String[] valueParm = new String[2];
					valueParm[0] = "Address Details";
					valueParm[1] = "Address";
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90270", "", valueParm), "EN");
					auditDetail.setErrorDetail(errorDetail);
					return auditDetail;
				}
			}
		}
		if (StringUtils.equals(method, "Update")) {
			List<CustomerAddres> prvCustomerAddrList = customerAddresDAO
					.getCustomerAddresByCustomer(customerAddres.getCustID(), "");
			if (prvCustomerAddrList != null && !prvCustomerAddrList.isEmpty()) {
				for (CustomerAddres prvCustAddrr : prvCustomerAddrList) {
					if (StringUtils.equals(prvCustAddrr.getCustAddrType(), customerAddres.getCustAddrType())) {
						if (prvCustAddrr.getCustAddrPriority() == Integer
								.valueOf(PennantConstants.EMAILPRIORITY_VeryHigh)) {
							if (customerAddres.getCustAddrPriority() != Integer
									.valueOf(PennantConstants.EMAILPRIORITY_VeryHigh)) {
								String[] valueParm = new String[2];
								valueParm[0] = "Address Details";
								valueParm[1] = "Address should not update";
								errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90270", "", valueParm), "EN");
								auditDetail.setErrorDetail(errorDetail);
								return auditDetail;
							}
						}
					} else {
						if (prvCustAddrr.getCustAddrPriority() == customerAddres.getCustAddrPriority()) {
							String[] valueParm = new String[2];
							valueParm[0] = "Priority";
							valueParm[1] = String.valueOf(customerAddres.getCustAddrPriority());
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("30702", "", valueParm), "EN");
							auditDetail.setErrorDetail(errorDetail);
							return auditDetail;
						}
					}
				}
			}
		}
		
		// validate Master code with PLF system masters
		int count = getCustomerAddresDAO().getAddrTypeCount(customerAddres.getCustAddrType());
		if (count <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "AddrType";
			valueParm[1] = customerAddres.getCustAddrType();
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90701", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;
		}
		Province province = provinceDAO.getProvinceById(customerAddres.getCustAddrCountry(),
				customerAddres.getCustAddrProvince(), "");
		if (province == null) {
			 errorDetail = new ErrorDetails();
			String[] valueParm = new String[2];
			valueParm[0] = customerAddres.getCustAddrProvince();
			valueParm[1] = customerAddres.getCustAddrCountry();
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90701", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;
		}
		City city = cityDAO.getCityById(customerAddres.getCustAddrCountry(), customerAddres.getCustAddrProvince(),
				customerAddres.getCustAddrCity(), "");
		if (city == null) {
			errorDetail = new ErrorDetails();
			String[] valueParm = new String[2];
			valueParm[0] = customerAddres.getCustAddrCity();
			valueParm[1] = customerAddres.getCustAddrProvince();
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90701", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;
		}
		if(!(customerAddres.getCustAddrPriority()>=1 && customerAddres.getCustAddrPriority()<=5)){
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(customerAddres.getCustAddrPriority());
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("90114", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;
			
		}
		if(StringUtils.isNotBlank(customerAddres.getCustAddrZIP())){
			if(customerAddres.getCustAddrZIP().length()<3 || customerAddres.getCustAddrZIP().length()>6){
				String[] valueParm = new String[3];
				valueParm[0] = "pinCode";
				valueParm[1] = "2 digits";
				valueParm[2] = "7 digits";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetails("65031", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
			}
		}
		return auditDetail;
	}

	
	public void setCityDAO(CityDAO cityDAO) {
		this.cityDAO = cityDAO;
	}

	public void setProvinceDAO(ProvinceDAO provinceDAO) {
		this.provinceDAO = provinceDAO;
	}

	
	

}
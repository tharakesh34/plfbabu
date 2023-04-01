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

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : CustomerAddresServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-05-2011 * *
 * Modified Date : 06-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.customermasters.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.PinCodeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.dao.systemmasters.ProvinceDAO;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.CustomerAddresService;
import com.pennant.backend.service.customermasters.validation.CustomerAddressValidation;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.RequestSource;

/**
 * Service implementation for methods that depends on <b>CustomerAddres</b>.<br>
 * 
 */
public class CustomerAddresServiceImpl extends GenericService<CustomerAddres> implements CustomerAddresService {

	private static Logger logger = LogManager.getLogger(CustomerAddresServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CustomerAddresDAO customerAddresDAO;
	private CustomerAddressValidation customerAddressValidation;
	private ProvinceDAO provinceDAO;
	private PinCodeDAO pinCodeDAO;

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

	public CustomerAddressValidation getAddressValidation() {

		if (customerAddressValidation == null) {
			this.customerAddressValidation = new CustomerAddressValidation(customerAddresDAO);
		}
		return this.customerAddressValidation;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * CustomerAddresses/CustomerAddresses_Temp by using CustomerAddresDAO's save method b) Update the Record in the
	 * table. based on the module workFlow Configuration. by using CustomerAddresDAO's update method 3) Audit the record
	 * in to AuditHeader and AdtCustomerAddresses by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
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

		if (customerAddres.isNewRecord()) {
			customerAddres.setId(getCustomerAddresDAO().save(customerAddres, tableType));
			auditHeader.getAuditDetail().setModelData(customerAddres);
			/* auditHeader.setAuditReference(String.valueOf(customerAddres.getId())); */
		} else {
			getCustomerAddresDAO().update(customerAddres, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * CustomerAddresses by using CustomerAddresDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtCustomerAddresses by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
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
	 * getCustomerAddresById fetch the details by using CustomerAddresDAO's getCustomerAddresById method.
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return CustomerAddres
	 */
	@Override
	public CustomerAddres getCustomerAddresById(long id, String addType) {
		return getCustomerAddresDAO().getCustomerAddresById(id, addType, "_View");
	}

	/**
	 * getApprovedCustomerAddresById fetch the details by using CustomerAddresDAO's getCustomerAddresById method . with
	 * parameter id and type as blank. it fetches the approved records from the CustomerAddresses.
	 * 
	 * @param id (String)
	 * @return CustomerAddres
	 */
	public CustomerAddres getApprovedCustomerAddresById(long id, String addType) {
		return getCustomerAddresDAO().getCustomerAddresById(id, addType, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getCustomerAddresDAO().delete with
	 * parameters customerAddres,"" b) NEW Add new record in to main table by using getCustomerAddresDAO().save with
	 * parameters customerAddres,"" c) EDIT Update record in the main table by using getCustomerAddresDAO().update with
	 * parameters customerAddres,"" 3) Delete the record from the workFlow table by using getCustomerAddresDAO().delete
	 * with parameters customerAddres,"_Temp" 4) Audit the record in to AuditHeader and AdtCustomerAddresses by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtCustomerAddresses
	 * by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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
		BeanUtils.copyProperties((CustomerAddres) auditHeader.getAuditDetail().getModelData(), customerAddres);

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

		if (!(PennantConstants.FINSOURCE_ID_API.equals(customerAddres.getSourceId())
				|| RequestSource.UPLOAD.name().equals(customerAddres.getSourceId()))) {
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
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getCustomerAddresDAO().delete with parameters customerAddres,"_Temp" 3) Audit the record
	 * in to AuditHeader and AdtCustomerAddresses by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		CustomerAddres customerAddres = (CustomerAddres) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCustomerAddresDAO().delete(customerAddres, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
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
		return getCustomerAddresDAO().getVersion(id, addrType);

	}

	@Override
	public List<CustomerAddres> getApprovedCustomerAddresById(long id) {
		return getCustomerAddresDAO().getCustomerAddresByCustomer(id, "_AView");
	}

	@Override
	public CustomerAddres getHighPriorityCustAddr(long custID) {
		return getCustomerAddresDAO().getHighPriorityCustAddr(custID, "");
	}

	@Override
	public AuditDetail doValidations(CustomerAddres ca, String method) {
		AuditDetail auditDetail = new AuditDetail();
		ErrorDetail errorDetail = new ErrorDetail();

		switch (method) {
		case "Create":
			validateOnCreate(ca, auditDetail);
			break;
		case "Update":
			validateOnUpdate(ca, auditDetail);
			break;
		default:
			break;
		}

		String addressType = ca.getCustAddrType();

		// validate Master code with PLF system masters
		if (customerAddresDAO.getAddrTypeCount(addressType) <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "AddrType";
			valueParm[1] = addressType;
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;
		}

		PinCode pincode = null;
		if (ca.getPinCodeId() != null && ca.getPinCodeId() < 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "PinCodeId";
			valueParm[1] = "0";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm));
			auditDetail.setErrorDetail(errorDetail);
		} else {
			if (StringUtils.isNotBlank(ca.getCustAddrZIP()) && (ca.getPinCodeId() != null)) {
				pincode = pinCodeDAO.getPinCodeById(ca.getPinCodeId(), "_AView");
				if (pincode == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "PinCodeId " + String.valueOf(ca.getPinCodeId());
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
				} else if (!pincode.getPinCode().equals(ca.getCustAddrZIP())) {
					String[] valueParm = new String[2];
					valueParm[0] = "PinCode " + ca.getCustAddrZIP();
					valueParm[1] = "PinCodeId " + String.valueOf(ca.getPinCodeId());
					errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("99017", "", valueParm));
					auditDetail.setErrorDetail(errorDetail);
				}
			} else {
				if (StringUtils.isNotBlank(ca.getCustAddrZIP()) && (ca.getPinCodeId() == null)) {
					int pinCodeCount = pinCodeDAO.getPinCodeCount(ca.getCustAddrZIP(), "_AView");
					String[] valueParm = new String[1];
					switch (pinCodeCount) {
					case 0:
						valueParm[0] = "PinCode " + ca.getCustAddrZIP();
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", "", valueParm));
						auditDetail.setErrorDetail(errorDetail);
						break;
					case 1:
						pincode = pinCodeDAO.getPinCode(ca.getCustAddrZIP(), "_AView");
						ca.setPinCodeId(pincode.getPinCodeId());
						break;
					default:
						valueParm[0] = "PinCodeId";
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("51004", "", valueParm));
						auditDetail.setErrorDetail(errorDetail);
					}
				} else if (ca.getPinCodeId() != null && StringUtils.isBlank(ca.getCustAddrZIP())) {
					pincode = pinCodeDAO.getPinCodeById(ca.getPinCodeId(), "_AView");
					if (pincode != null) {
						ca.setCustAddrZIP(pincode.getPinCode());
					} else {
						String[] valueParm = new String[1];
						valueParm[0] = "PinCodeId " + String.valueOf(ca.getPinCodeId());
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", "", valueParm));
						auditDetail.setErrorDetail(errorDetail);
					}
				}
			}
		}
		if (pincode != null) {
			if (StringUtils.isNotBlank(ca.getCustAddrCountry())
					&& !ca.getCustAddrCountry().equalsIgnoreCase(pincode.getpCCountry())) {

				String[] valueParm = new String[2];
				valueParm[0] = ca.getCustAddrCountry();
				valueParm[1] = ca.getCustAddrZIP();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
			} else {
				ca.setCustAddrCountry(pincode.getpCCountry());
			}

			Province province = provinceDAO.getProvinceById(ca.getCustAddrCountry(), pincode.getpCProvince(), "");
			if (province != null && StringUtils.isNotBlank(ca.getCustAddrProvince())
					&& !ca.getCustAddrProvince().equalsIgnoreCase(province.getCPProvince())) {

				String[] valueParm = new String[2];
				valueParm[0] = ca.getCustAddrProvince();
				valueParm[1] = ca.getCustAddrZIP();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
			} else {
				ca.setCustAddrProvince(pincode.getpCProvince());
			}

			if (StringUtils.isNotBlank(ca.getCustAddrCity())
					&& !ca.getCustAddrCity().equalsIgnoreCase(pincode.getCity())) {

				String[] valueParm = new String[2];
				valueParm[0] = ca.getCustAddrCity();
				valueParm[1] = ca.getCustAddrZIP();
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);

			} else {
				ca.setCustAddrCity(pincode.getCity());
			}

		}
		if (!(ca.getCustAddrPriority() >= 1 && ca.getCustAddrPriority() <= 5)) {
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(ca.getCustAddrPriority());
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90114", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;

		}
		if (StringUtils.isNotBlank(ca.getCustAddrZIP())) {
			if (ca.getCustAddrZIP().length() < 3 || ca.getCustAddrZIP().length() > 6) {
				String[] valueParm = new String[3];
				valueParm[0] = "pinCode";
				valueParm[1] = "2 digits";
				valueParm[2] = "7 digits";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("65031", "", valueParm), "EN");
				auditDetail.setErrorDetail(errorDetail);
			}
		}
		return auditDetail;
	}

	private void validateOnUpdate(CustomerAddres ca, AuditDetail auditDetail) {
		List<CustomerAddres> addressList = customerAddresDAO.getCustomerAddresByCustomer(ca.getCustID(), "");

		Integer highPriority = Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH);

		for (CustomerAddres address : addressList) {
			if (StringUtils.equals(address.getCustAddrType(), ca.getCustAddrType())) {
				if (address.getCustAddrPriority() == highPriority && ca.getCustAddrPriority() != highPriority) {
					String[] valueParm = new String[2];
					valueParm[0] = "Address Details";
					valueParm[1] = "Address should not update";
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90270", "", valueParm), "EN"));
					return;
				}
			} else {
				if (ca.getCustAddrPriority() == address.getCustAddrPriority()
						&& Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH) == ca.getCustAddrPriority()) {
					String[] valueParm = new String[2];
					valueParm[0] = "Priority";
					valueParm[1] = String.valueOf(ca.getCustAddrPriority());
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30702", "", valueParm), "EN"));
					return;
				}
			}
		}
	}

	private void validateOnCreate(CustomerAddres ca, AuditDetail auditDetail) {
		List<CustomerAddres> addressList = customerAddresDAO.getCustomerAddresByCustomer(ca.getCustID(), "");

		if (CollectionUtils.isEmpty(addressList)) {
			if (ca.getCustAddrPriority() != Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
				String[] valueParm = new String[2];
				valueParm[0] = "Address Details";
				valueParm[1] = "Address";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90270", "", valueParm), "EN"));
				return;
			}
		}

		String[] valueParm = new String[2];
		for (CustomerAddres addr : addressList) {
			if (addr.getCustAddrPriority() == ca.getCustAddrPriority()) {
				valueParm[0] = "Priority";
				valueParm[1] = String.valueOf(ca.getCustAddrPriority());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30702", "", valueParm), "EN"));
				return;
			}

			if (StringUtils.equals(addr.getCustAddrType(), ca.getCustAddrType())) {
				valueParm[0] = "AddressType";
				valueParm[1] = ca.getCustAddrType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("41001", "", valueParm), "EN"));
				return;
			}
		}
	}

	public void setProvinceDAO(ProvinceDAO provinceDAO) {
		this.provinceDAO = provinceDAO;
	}

	public PinCodeDAO getPinCodeDAO() {
		return pinCodeDAO;
	}

	public void setPinCodeDAO(PinCodeDAO pinCodeDAO) {
		this.pinCodeDAO = pinCodeDAO;
	}

}
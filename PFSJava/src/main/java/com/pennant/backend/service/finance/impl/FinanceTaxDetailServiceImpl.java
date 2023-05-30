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
 * * FileName : FinanceTaxDetailServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-06-2017 * *
 * Modified Date : 17-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 17-06-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.PinCodeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.finance.FinanceWriteoffDAO;
import com.pennant.backend.dao.finance.GuarantorDetailDAO;
import com.pennant.backend.dao.finance.JointAccountDetailDAO;
import com.pennant.backend.dao.smtmasters.CountryDAO;
import com.pennant.backend.dao.systemmasters.CityDAO;
import com.pennant.backend.dao.systemmasters.ProvinceDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinanceTaxDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>FinanceTaxDetail</b>.<br>
 */
public class FinanceTaxDetailServiceImpl extends GenericService<FinanceTaxDetail> implements FinanceTaxDetailService {
	private static final Logger logger = LogManager.getLogger(FinanceTaxDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinanceTaxDetailDAO financeTaxDetailDAO;
	private GuarantorDetailDAO guarantorDetailDAO;
	private JointAccountDetailDAO jointAccountDetailDAO;
	private CustomerDAO customerDAO;
	private ProvinceDAO provinceDAO;
	private FinanceMainDAO financeMainDAO;
	private PinCodeDAO pinCodeDAO;
	private CountryDAO countryDAO;
	private CityDAO cityDAO;
	private CustomerAddresDAO customerAddresDAO;
	private FinanceWriteoffDAO financeWriteoffDAO;

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FinanceTaxDetail ftd = (FinanceTaxDetail) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (ftd.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (ftd.isNewRecord()) {
			financeTaxDetailDAO.save(ftd, tableType);
		} else {
			financeTaxDetailDAO.update(ftd, tableType);
		}

		String rcdMaintainSts = FinServiceEvent.GSTDETAILS;
		financeMainDAO.updateMaintainceStatus(ftd.getFinID(), rcdMaintainSts);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FinanceTaxDetail ftd = (FinanceTaxDetail) auditHeader.getAuditDetail().getModelData();
		financeTaxDetailDAO.delete(ftd, TableType.MAIN_TAB);

		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public FinanceTaxDetail getFinanceTaxDetail(long finID) {
		return financeTaxDetailDAO.getFinanceTaxDetail(finID, "_View");
	}

	public FinanceTaxDetail getApprovedFinanceTaxDetail(long finID) {
		return financeTaxDetailDAO.getFinanceTaxDetail(finID, "_AView");
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FinanceTaxDetail ftd = new FinanceTaxDetail();
		BeanUtils.copyProperties((FinanceTaxDetail) auditHeader.getAuditDetail().getModelData(), ftd);

		if (!StringUtils.equals(ftd.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
			financeTaxDetailDAO.delete(ftd, TableType.TEMP_TAB);
		}

		long finID = ftd.getFinID();
		if (!PennantConstants.RECORD_TYPE_NEW.equals(ftd.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(financeTaxDetailDAO.getFinanceTaxDetail(finID, ""));
		}

		if (ftd.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			financeTaxDetailDAO.delete(ftd, TableType.MAIN_TAB);
		} else {
			ftd.setRoleCode("");
			ftd.setNextRoleCode("");
			ftd.setTaskId("");
			ftd.setNextTaskId("");
			ftd.setWorkflowId(0);

			if (ftd.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				ftd.setRecordType("");
				financeTaxDetailDAO.save(ftd, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				ftd.setRecordType("");
				financeTaxDetailDAO.update(ftd, TableType.MAIN_TAB);
			}
		}

		financeMainDAO.updateMaintainceStatus(finID, "");

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(ftd);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);

		return auditHeader;

	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FinanceTaxDetail ftd = (FinanceTaxDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		financeTaxDetailDAO.delete(ftd, TableType.TEMP_TAB);

		financeMainDAO.updateMaintainceStatus(ftd.getFinID(), "");
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<>());

		FinanceTaxDetail ftd = (FinanceTaxDetail) auditDetail.getModelData();
		String finReference = ftd.getFinReference();
		long finID = ftd.getFinID();

		FinanceTaxDetail tempMandate = null;

		if (ftd.isWorkflow()) {
			tempMandate = financeTaxDetailDAO.getFinanceTaxDetail(finID, "_Temp");
		}
		FinanceTaxDetail befMandate = financeTaxDetailDAO.getFinanceTaxDetail(finID, "");
		FinanceTaxDetail oldMandate = ftd.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(finReference);
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (ftd.isNewRecord()) { // for New record or new record into work flow

			if (!ftd.isWorkflow()) {// With out Work flow only new records
				if (befMandate != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (ftd.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is
																					// new
					if (befMandate != null || tempMandate != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befMandate == null || tempMandate != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!ftd.isWorkflow()) { // With out Work flow for update and delete

				if (befMandate == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldMandate != null && !oldMandate.getLastMntOn().equals(befMandate.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
									usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
									usrLanguage));
						}
					}
				}
			} else {

				if (tempMandate == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (tempMandate != null && oldMandate != null
						&& !oldMandate.getLastMntOn().equals(tempMandate.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail = gstNumbeValidation(auditDetail, ftd);

		// Validate Loan is INPROGRESS in any Other Servicing option or NOT ?
		String rcdMntnSts = financeMainDAO.getFinanceMainByRcdMaintenance(finID);
		if (StringUtils.isNotEmpty(rcdMntnSts) && !FinServiceEvent.FEEPOSTING.equals(rcdMntnSts)
				&& !FinServiceEvent.GSTDETAILS.equals(rcdMntnSts)) {
			String[] valueParm1 = new String[1];
			valueParm1[0] = rcdMntnSts;
			auditDetail.setErrorDetail(new ErrorDetail("LMS001", valueParm1));
		}

		if (financeWriteoffDAO.isWriteoffLoan(finID, "")) {
			String[] valueParm1 = new String[1];
			valueParm1[0] = "";
			auditDetail.setErrorDetail(new ErrorDetail("FWF001", valueParm1));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !ftd.isWorkflow()) {
			auditDetail.setBefImage(befMandate);
		}

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public List<ErrorDetail> doGSTValidations(final FinanceTaxDetail ftd) {

		List<ErrorDetail> errorsList = new ArrayList<>();

		int count = financeMainDAO.getFinanceCountById(ftd.getFinID(), "", false);
		if (count <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "finReference";
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("30556", valueParm)));

			return errorsList;
		}

		Customer customer = customerDAO.getCustomerByCIF(ftd.getCustCIF(), "_View");
		if (customer == null) {
			String[] valueParm = new String[1];
			valueParm[0] = ftd.getCustCIF();
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90304", valueParm)));

			return errorsList;
		}

		if (StringUtils.isEmpty(ftd.getApplicableFor())) {
			String[] valueParm = new String[1];
			valueParm[0] = "ApplicableFor";
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

			return errorsList;
		}

		List<ValueLabel> applicableForValues = PennantStaticListUtil.getTaxApplicableFor();
		boolean applicableForFlag = false;
		for (ValueLabel valueLabel : applicableForValues) {
			if (StringUtils.equals(ftd.getApplicableFor(), valueLabel.getValue())) {
				applicableForFlag = true;
				break;
			}
		}
		if (!applicableForFlag) {
			String[] valueParm = new String[2];
			valueParm[0] = ftd.getApplicableFor();
			valueParm[1] = PennantConstants.TAXAPPLICABLEFOR_PRIMAYCUSTOMER + ", "
					+ PennantConstants.TAXAPPLICABLEFOR_COAPPLICANT;
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90337", valueParm)));

			return errorsList;
		}

		if (StringUtils.isEmpty(ftd.getAddrLine1())) {
			String[] valueParm = new String[1];
			valueParm[0] = "AddrLine1";
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

			return errorsList;
		}

		if (StringUtils.isEmpty(ftd.getProvince())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Province";
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

			return errorsList;
		}

		if (StringUtils.isEmpty(ftd.getCountry())) {
			String[] valueParm = new String[1];
			valueParm[0] = "country";
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

			return errorsList;
		}

		if (StringUtils.isEmpty(ftd.getCity())) {
			String[] valueParm = new String[1];
			valueParm[0] = "city";
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

			return errorsList;
		}

		if (StringUtils.isEmpty(ftd.getPinCode()) && ftd.getPinCodeId() == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "PinCode or PinCodeId";
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

			return errorsList;
		}

		if (StringUtils.isNotBlank(ftd.getTaxNumber()) && (StringUtils.length(ftd.getTaxNumber()) != 15)) {
			String[] valueParm = new String[2];
			valueParm[0] = "gstNumber";
			valueParm[1] = "15";
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90300", valueParm)));

			return errorsList;
		}

		PinCode validPincode = null;
		if (ftd.getPinCodeId() != null && ftd.getPinCodeId() < 0) {
			String[] valueParm = new String[2];
			valueParm[0] = "PinCodeId";
			valueParm[1] = "0";
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm)));
		} else {
			if (StringUtils.isNotBlank(ftd.getPinCode()) && (ftd.getPinCodeId() != null)) {
				validPincode = pinCodeDAO.getPinCodeById(ftd.getPinCodeId(), "_AView");
				if (validPincode == null) {
					String[] valueParm = new String[1];
					valueParm[0] = "PinCodeId " + String.valueOf(ftd.getPinCodeId());
					errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", "", valueParm)));
					return errorsList;
				} else if (!validPincode.getPinCode().equals(ftd.getPinCode())) {
					String[] valueParm = new String[2];
					valueParm[0] = "PinCode " + ftd.getPinCode();
					valueParm[1] = "PinCodeId " + String.valueOf(ftd.getPinCodeId());
					errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("99017", "", valueParm)));
					return errorsList;
				}
			} else {
				if (StringUtils.isNotBlank(ftd.getPinCode()) && (ftd.getPinCodeId() == null)) {
					int pinCodeCount = pinCodeDAO.getPinCodeCount(ftd.getPinCode(), "_AView");
					String[] valueParm = new String[1];
					switch (pinCodeCount) {
					case 0:
						valueParm[0] = "PinCode " + ftd.getPinCode();
						errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", "", valueParm)));
						break;
					case 1:
						validPincode = pinCodeDAO.getPinCode(ftd.getPinCode(), "_AView");
						ftd.setPinCodeId(validPincode.getPinCodeId());
						break;
					default:
						valueParm[0] = "PinCodeId";
						errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("51004", "", valueParm)));
						return errorsList;
					}
				} else if (ftd.getPinCodeId() != null && StringUtils.isBlank(ftd.getPinCode())) {
					validPincode = pinCodeDAO.getPinCodeById(ftd.getPinCodeId(), "_AView");
					if (validPincode != null) {
						ftd.setPinCode(validPincode.getPinCode());
					} else {
						String[] valueParm = new String[1];
						valueParm[0] = "PinCodeId " + String.valueOf(ftd.getPinCodeId());
						errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", "", valueParm)));
						return errorsList;
					}
				}
			}
		}

		if (validPincode != null) {
			/* validate country, state, city */
			Country validCountry = countryDAO.getCountryById(ftd.getCountry(), "");
			if (validCountry != null) {
				if (!StringUtils.equals(validCountry.getCountryCode(), ftd.getCountry())) {
					String[] valueParm = new String[2];
					valueParm[0] = ftd.getCountry();
					valueParm[1] = "Country";
					errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", valueParm)));

					return errorsList;
				}
			} else {
				String[] valueParm = new String[2];
				valueParm[0] = "Country";
				valueParm[1] = ftd.getCountry();
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));

				return errorsList;
			}

			Province validProvince = provinceDAO.getProvinceById(ftd.getCountry(), ftd.getProvince(), "");
			if (validProvince != null) {
				if (!StringUtils.equals(validProvince.getCPProvince(), ftd.getProvince())) {
					String[] valueParm = new String[2];
					valueParm[0] = ftd.getProvince();
					valueParm[1] = "State";
					errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", valueParm)));

					return errorsList;
				}
			} else {
				String[] valueParm = new String[2];
				valueParm[0] = "State";
				valueParm[1] = ftd.getProvince();
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));

				return errorsList;
			}

			City validCity = cityDAO.getCityById(ftd.getCountry(), ftd.getProvince(), ftd.getCity(), "");
			if (validCity != null) {
				if (!StringUtils.equals(validCity.getPCCity(), ftd.getCity())) {
					String[] valueParm = new String[2];
					valueParm[0] = ftd.getCity();
					valueParm[1] = "City";
					errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", valueParm)));

					return errorsList;
				}
			} else {
				String[] valueParm = new String[2];
				valueParm[0] = "City";
				valueParm[1] = ftd.getCity();
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));

				return errorsList;
			}
		} else {
			String[] valueParm = new String[2];
			valueParm[0] = "Pincode";
			valueParm[1] = ftd.getPinCode();
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));

			return errorsList;
		}

		return errorsList;
	}

	public List<ErrorDetail> verifyCoApplicantDetails(FinanceTaxDetail ftd) {

		List<ErrorDetail> errorsList = new ArrayList<>();

		Customer customer = customerDAO.getCustomerByCIF(ftd.getCustCIF(), "");
		if (customer == null) {
			String[] valueParm = new String[1];
			valueParm[0] = ftd.getCustCIF();
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90101", valueParm)));

			return errorsList;
		}

		long finID = ftd.getFinID();
		String finReference = ftd.getFinReference();
		switch (ftd.getApplicableFor()) {
		case PennantConstants.TAXAPPLICABLEFOR_PRIMAYCUSTOMER:
			/* business logic */
			FinanceMain finMain = financeMainDAO.getFinanceDetailsForService(finID, "_AView", false);
			if (finMain.getCustID() != customer.getCustID()) {
				String[] valueParm = new String[2];
				valueParm[0] = "Customer";
				valueParm[1] = "Loan : " + finReference;
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));

				return errorsList;
			}
			ftd.setTaxCustId(customer.getCustID());
			break;
		case PennantConstants.TAXAPPLICABLEFOR_COAPPLICANT:
			JointAccountDetail jointAccountDetail = jointAccountDetailDAO.getJointAccountDetailByRef(finID,
					ftd.getCustCIF(), "_AView");
			if (null == jointAccountDetail) {
				String[] valueParm = new String[1];
				valueParm[0] = ftd.getCustCIF();
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90266", valueParm)));

				return errorsList;
			} else {
				if (customer.getCustID() != jointAccountDetail.getCustID()) {
					String[] valueParm = new String[1];
					valueParm[0] = ftd.getCustCIF();
					errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90102", valueParm)));

					return errorsList;
				}
			}
			ftd.setTaxCustId(customer.getCustID());
			break;
		default:
			String[] valueParm = new String[1];
			valueParm[0] = ftd.getApplicableFor();
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
			break;
		}

		return errorsList;
	}

	@Override
	public int getFinanceTaxDetailsByCount(long finID) {
		return financeTaxDetailDAO.getFinTaxDetailsCount(finID);
	}

	@Override
	public List<GuarantorDetail> getGuarantorDetailByFinRef(long finID, String type) {
		return this.guarantorDetailDAO.getGuarantorDetailByFinRef(finID, type);
	}

	public List<JointAccountDetail> getJointAccountDetailByFinRef(long finID, String type) {
		return this.jointAccountDetailDAO.getJointAccountDetailByFinRef(finID, type);

	}

	@Override
	public Customer getCustomerByID(long id) {
		return this.customerDAO.getCustomerByID(id);
	}

	@Override
	public Long getCustomerIdByFinRef(String finReference) {
		logger.debug(Literal.ENTERING);
		long custID = this.financeMainDAO.getCustomerIdByFinRef(finReference);
		logger.debug(Literal.LEAVING);
		return custID;
	}

	/**
	 * to validate the GST Number
	 */
	@Override
	public AuditDetail gstNumbeValidation(AuditDetail auditDetail, FinanceTaxDetail ftd) {
		logger.debug(Literal.ENTERING);

		long custId = ftd.getTaxCustId();
		String taxNumber = ftd.getTaxNumber();
		String applicableFor = ftd.getApplicableFor();

		if (custId != 0) {
			String panNumber = "";
			String gstStateCode = "";

			if (StringUtils.isNotBlank(taxNumber)) {
				// if GST Number is already exist or not
				List<FinanceTaxDetail> financeTaxDetails = financeTaxDetailDAO.getGSTNumberAndCustCIF(custId, taxNumber,
						"_View");

				if (!financeTaxDetails.isEmpty()) {
					for (FinanceTaxDetail finTaxDetail : financeTaxDetails) {
						if (finTaxDetail.getTaxNumber() != null) {
							String custCIF = customerDAO.getCustomerIdCIF(finTaxDetail.getTaxCustId());

							String[] parameters = new String[4];
							parameters[0] = PennantJavaUtil.getLabel("label_FinanceTaxDetailDialog_TaxNumber.value")
									+ ": ";
							parameters[1] = taxNumber;
							parameters[2] = PennantJavaUtil.getLabel("label_FinTaxDetails_CustCIF") + ": ";
							parameters[3] = custCIF;

							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "GSTD001", parameters, null)));
						}
					}
				}

				Province province = this.provinceDAO.getProvinceById(ftd.getCountry(), ftd.getProvince(), "");
				if (province != null) {
					gstStateCode = province.getTaxStateCode();
				}
				if (auditDetail != null && auditDetail.getModelData() != null) {
					if (PennantConstants.TAXAPPLICABLEFOR_COAPPLICANT.equals(applicableFor)) {
						panNumber = customerDAO.getCustCRCPRById(custId, "");
					} else {
						Object modelObj = auditDetail.getModelData();
						if (modelObj instanceof CustomerDetails) {
							panNumber = ((CustomerDetails) modelObj).getCustomer().getCustCRCPR();
						} else {
							panNumber = customerDAO.getCustCRCPRById(custId, "");
						}
					}
				}
				if (StringUtils.isNotBlank(gstStateCode)) { // if GST State Code is not available
					if (!StringUtils.equalsIgnoreCase(gstStateCode, taxNumber.substring(0, 2))) {
						auditDetail.setErrorDetail(ErrorUtil
								.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "65023", null, null)));
					}
				}

				if (StringUtils.isNotBlank(panNumber)) { // if PAN number is not available in GST Number
					if (!StringUtils.equalsIgnoreCase(panNumber, taxNumber.substring(2, 12))) {
						auditDetail.setErrorDetail(ErrorUtil
								.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "65024", null, null)));
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);

		return auditDetail;
	}

	/**
	 *
	 * Ticket id:127950
	 */
	@Override
	public boolean isFinReferenceExitsinLQ(long finID, TableType tempTab, boolean wif) {
		return this.financeMainDAO.isFinReferenceExitsinLQ(finID, tempTab, wif);
	}

	@Override
	public CustomerAddres getHighPriorityCustAddr(final long id) {
		return customerAddresDAO.getHighPriorityCustAddr(id, "_AView");
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	public void setGuarantorDetailDAO(GuarantorDetailDAO guarantorDetailDAO) {
		this.guarantorDetailDAO = guarantorDetailDAO;
	}

	public void setJointAccountDetailDAO(JointAccountDetailDAO jointAccountDetailDAO) {
		this.jointAccountDetailDAO = jointAccountDetailDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setProvinceDAO(ProvinceDAO provinceDAO) {
		this.provinceDAO = provinceDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setPinCodeDAO(PinCodeDAO pinCodeDAO) {
		this.pinCodeDAO = pinCodeDAO;
	}

	public void setCountryDAO(CountryDAO countryDAO) {
		this.countryDAO = countryDAO;
	}

	public void setCityDAO(CityDAO cityDAO) {
		this.cityDAO = cityDAO;
	}

	public void setCustomerAddresDAO(CustomerAddresDAO customerAddresDAO) {
		this.customerAddresDAO = customerAddresDAO;
	}

	public void setFinanceWriteoffDAO(FinanceWriteoffDAO financeWriteoffDAO) {
		this.financeWriteoffDAO = financeWriteoffDAO;
	}

}
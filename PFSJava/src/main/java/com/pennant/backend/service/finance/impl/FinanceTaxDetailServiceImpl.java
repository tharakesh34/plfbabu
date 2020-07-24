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
 * FileName    		:  FinanceTaxDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-06-2017    														*
 *                                                                  						*
 * Modified Date    :  17-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-06-2017       PENNANT	                 0.1                                            * 
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.PinCodeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.finance.GuarantorDetailDAO;
import com.pennant.backend.dao.finance.JountAccountDetailDAO;
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
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceTaxDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>FinanceTaxDetail</b>.<br>
 */
public class FinanceTaxDetailServiceImpl extends GenericService<FinanceTaxDetail> implements FinanceTaxDetailService {
	private static final Logger logger = Logger.getLogger(FinanceTaxDetailServiceImpl.class);

	private CustomerDetailsService customerDetailsService;

	private AuditHeaderDAO auditHeaderDAO;
	private FinanceTaxDetailDAO financeTaxDetailDAO;
	private GuarantorDetailDAO guarantorDetailDAO;
	private JountAccountDetailDAO jountAccountDetailDAO;
	private CustomerDAO customerDAO;
	private ProvinceDAO provinceDAO;
	private FinanceMainDAO financeMainDAO;
	private PinCodeDAO pinCodeDAO;
	private CountryDAO countryDAO;
	private CityDAO cityDAO;
	private CustomerAddresDAO customerAddresDAO;

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table FinTaxDetail/FinTaxDetail_Temp
	 * by using FinTaxDetailDAO's save method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using FinTaxDetailDAO's update method 3) Audit the record in to AuditHeader and AdtFinTaxDetail
	 * by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FinanceTaxDetail financeTaxDetail = (FinanceTaxDetail) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (financeTaxDetail.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (financeTaxDetail.isNew()) {
			getFinanceTaxDetailDAO().save(financeTaxDetail, tableType);
		} else {
			getFinanceTaxDetailDAO().update(financeTaxDetail, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * FinTaxDetail by using FinTaxDetailDAO's delete method with type as Blank 3) Audit the record in to AuditHeader
	 * and AdtFinTaxDetail by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FinanceTaxDetail financeTaxDetail = (FinanceTaxDetail) auditHeader.getAuditDetail().getModelData();
		getFinanceTaxDetailDAO().delete(financeTaxDetail, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getFinTaxDetail fetch the details by using FinTaxDetailDAO's getFinTaxDetailById method.
	 * 
	 * @param finReference
	 *            finReference of the FinanceTaxDetail.
	 * @return FinTaxDetail
	 */
	@Override
	public FinanceTaxDetail getFinanceTaxDetail(String finReference) {
		return getFinanceTaxDetailDAO().getFinanceTaxDetail(finReference, "_View");
	}

	/**
	 * getApprovedFinTaxDetailById fetch the details by using FinTaxDetailDAO's getFinTaxDetailById method . with
	 * parameter id and type as blank. it fetches the approved records from the FinTaxDetail.
	 * 
	 * @param finReference
	 *            finReference of the FinanceTaxDetail. (String)
	 * @return FinTaxDetail
	 */
	public FinanceTaxDetail getApprovedFinanceTaxDetail(String finReference) {
		return getFinanceTaxDetailDAO().getFinanceTaxDetail(finReference, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getFinanceTaxDetailDAO().delete with
	 * parameters financeTaxDetail,"" b) NEW Add new record in to main table by using getFinanceTaxDetailDAO().save with
	 * parameters financeTaxDetail,"" c) EDIT Update record in the main table by using getFinanceTaxDetailDAO().update
	 * with parameters financeTaxDetail,"" 3) Delete the record from the workFlow table by using
	 * getFinanceTaxDetailDAO().delete with parameters financeTaxDetail,"_Temp" 4) Audit the record in to AuditHeader
	 * and AdtFinTaxDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to
	 * AuditHeader and AdtFinTaxDetail by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FinanceTaxDetail financeTaxDetail = new FinanceTaxDetail();
		BeanUtils.copyProperties((FinanceTaxDetail) auditHeader.getAuditDetail().getModelData(), financeTaxDetail);

		if (!StringUtils.equals(financeTaxDetail.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
			getFinanceTaxDetailDAO().delete(financeTaxDetail, TableType.TEMP_TAB);
		}

		if (!PennantConstants.RECORD_TYPE_NEW.equals(financeTaxDetail.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(financeTaxDetailDAO.getFinanceTaxDetail(financeTaxDetail.getFinReference(), ""));
		}

		if (financeTaxDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getFinanceTaxDetailDAO().delete(financeTaxDetail, TableType.MAIN_TAB);
		} else {
			financeTaxDetail.setRoleCode("");
			financeTaxDetail.setNextRoleCode("");
			financeTaxDetail.setTaskId("");
			financeTaxDetail.setNextTaskId("");
			financeTaxDetail.setWorkflowId(0);

			if (financeTaxDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				financeTaxDetail.setRecordType("");
				getFinanceTaxDetailDAO().save(financeTaxDetail, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				financeTaxDetail.setRecordType("");
				getFinanceTaxDetailDAO().update(financeTaxDetail, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(financeTaxDetail);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);

		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getFinanceTaxDetailDAO().delete with parameters financeTaxDetail,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtFinTaxDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FinanceTaxDetail financeTaxDetail = (FinanceTaxDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFinanceTaxDetailDAO().delete(financeTaxDetail, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getFinanceTaxDetailDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinanceTaxDetail financeTaxDetail = (FinanceTaxDetail) auditDetail.getModelData();

		FinanceTaxDetail tempMandate = null;
		if (financeTaxDetail.isWorkflow()) {
			tempMandate = getFinanceTaxDetailDAO().getFinanceTaxDetail(financeTaxDetail.getId(), "_Temp");
		}
		FinanceTaxDetail befMandate = getFinanceTaxDetailDAO().getFinanceTaxDetail(financeTaxDetail.getId(), "");
		FinanceTaxDetail oldMandate = financeTaxDetail.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(financeTaxDetail.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (financeTaxDetail.isNew()) { // for New record or new record into work flow

			if (!financeTaxDetail.isWorkflow()) {// With out Work flow only new records  
				if (befMandate != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (financeTaxDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
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
			if (!financeTaxDetail.isWorkflow()) { // With out Work flow for update and delete

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

		auditDetail = gstNumbeValidation(auditDetail, financeTaxDetail);

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !financeTaxDetail.isWorkflow()) {
			auditDetail.setBefImage(befMandate);
		}

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public List<ErrorDetail> doGSTValidations(final FinanceTaxDetail financeTaxDetail) {

		List<ErrorDetail> errorsList = new ArrayList<>();

		int count = financeMainDAO.getFinanceCountById(financeTaxDetail.getFinReference(), "", false);
		if (count <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "finReference";
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("30556", valueParm)));

			return errorsList;
		}

		Customer customer = customerDetailsService.getCheckCustomerByCIF(financeTaxDetail.getCustCIF());
		if (customer == null) {
			String[] valueParm = new String[1];
			valueParm[0] = financeTaxDetail.getCustCIF();
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90304", valueParm)));

			return errorsList;
		}

		if (StringUtils.isEmpty(financeTaxDetail.getApplicableFor())) {
			String[] valueParm = new String[1];
			valueParm[0] = "ApplicableFor";
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

			return errorsList;
		}

		List<ValueLabel> applicableForValues = PennantStaticListUtil.getTaxApplicableFor();
		boolean applicableForFlag = false;
		for (ValueLabel valueLabel : applicableForValues) {
			if (StringUtils.equals(financeTaxDetail.getApplicableFor(), valueLabel.getValue())) {
				applicableForFlag = true;
				break;
			}
		}
		if (!applicableForFlag) {
			String[] valueParm = new String[2];
			valueParm[0] = financeTaxDetail.getApplicableFor();
			valueParm[1] = PennantConstants.TAXAPPLICABLEFOR_PRIMAYCUSTOMER + ", "
					+ PennantConstants.TAXAPPLICABLEFOR_COAPPLICANT;
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90337", valueParm)));

			return errorsList;
		}

		if (StringUtils.isEmpty(financeTaxDetail.getAddrLine1())) {
			String[] valueParm = new String[1];
			valueParm[0] = "AddrLine1";
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

			return errorsList;
		}

		if (StringUtils.isEmpty(financeTaxDetail.getProvince())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Province";
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

			return errorsList;
		}

		if (StringUtils.isEmpty(financeTaxDetail.getCountry())) {
			String[] valueParm = new String[1];
			valueParm[0] = "country";
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

			return errorsList;
		}

		if (StringUtils.isEmpty(financeTaxDetail.getCity())) {
			String[] valueParm = new String[1];
			valueParm[0] = "city";
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

			return errorsList;
		}

		if (StringUtils.isEmpty(financeTaxDetail.getPinCode())) {
			String[] valueParm = new String[1];
			valueParm[0] = "pincode";
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", valueParm)));

			return errorsList;
		}

		if (StringUtils.isNotBlank(financeTaxDetail.getTaxNumber())
				&& (!(StringUtils.length(financeTaxDetail.getTaxNumber()) == 15))) {
			String[] valueParm = new String[2];
			valueParm[0] = "gstNumber";
			valueParm[1] = "15";
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90300", valueParm)));

			return errorsList;
		}

		PinCode validPincode = pinCodeDAO.getPinCode(financeTaxDetail.getPinCode(), "_AView");
		if (validPincode != null) {
			/* validate country, state, city */
			Country validCountry = countryDAO.getCountryById(financeTaxDetail.getCountry(), "");
			if (validCountry != null) {
				if (!StringUtils.equals(validCountry.getCountryCode(), financeTaxDetail.getCountry())) {
					String[] valueParm = new String[2];
					valueParm[0] = financeTaxDetail.getCountry();
					valueParm[1] = "Country";
					errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", valueParm)));

					return errorsList;
				}
			} else {
				String[] valueParm = new String[2];
				valueParm[0] = "Country";
				valueParm[1] = financeTaxDetail.getCountry();
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));

				return errorsList;
			}

			Province validProvince = provinceDAO.getProvinceById(financeTaxDetail.getCountry(),
					financeTaxDetail.getProvince(), "");
			if (validProvince != null) {
				if (!StringUtils.equals(validProvince.getCPProvince(), financeTaxDetail.getProvince())) {
					String[] valueParm = new String[2];
					valueParm[0] = financeTaxDetail.getProvince();
					valueParm[1] = "State";
					errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", valueParm)));

					return errorsList;
				}
			} else {
				String[] valueParm = new String[2];
				valueParm[0] = "State";
				valueParm[1] = financeTaxDetail.getProvince();
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));

				return errorsList;
			}

			City validCity = cityDAO.getCityById(financeTaxDetail.getCountry(), financeTaxDetail.getProvince(),
					financeTaxDetail.getCity(), "");
			if (validCity != null) {
				if (!StringUtils.equals(validCity.getPCCity(), financeTaxDetail.getCity())) {
					String[] valueParm = new String[2];
					valueParm[0] = financeTaxDetail.getCity();
					valueParm[1] = "City";
					errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", valueParm)));

					return errorsList;
				}
			} else {
				String[] valueParm = new String[2];
				valueParm[0] = "City";
				valueParm[1] = financeTaxDetail.getCity();
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));

				return errorsList;
			}
		} else {
			String[] valueParm = new String[2];
			valueParm[0] = "Pincode";
			valueParm[1] = financeTaxDetail.getPinCode();
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));

			return errorsList;
		}

		return errorsList;
	}

	public List<ErrorDetail> verifyCoApplicantDetails(FinanceTaxDetail financeTaxDetail) {

		List<ErrorDetail> errorsList = new ArrayList<>();

		Customer customer = customerDetailsService.getCustomerByCIF(financeTaxDetail.getCustCIF());
		if (customer == null) {
			String[] valueParm = new String[1];
			valueParm[0] = financeTaxDetail.getCustCIF();
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90101", valueParm)));

			return errorsList;
		}

		switch (financeTaxDetail.getApplicableFor()) {
		case PennantConstants.TAXAPPLICABLEFOR_PRIMAYCUSTOMER:
			/* business logic */
			FinanceMain finMain = financeMainDAO.getFinanceDetailsForService(financeTaxDetail.getFinReference(),
					"_AView", false);
			if (!(finMain.getCustID() == customer.getCustID())) {
				String[] valueParm = new String[2];
				valueParm[0] = "Customer";
				valueParm[1] = "Loan : " + financeTaxDetail.getFinReference();
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90204", valueParm)));

				return errorsList;
			}
			financeTaxDetail.setTaxCustId(customer.getCustID());
			break;
		case PennantConstants.TAXAPPLICABLEFOR_COAPPLICANT:
			JointAccountDetail jointAccountDetail = jountAccountDetailDAO.getJountAccountDetailByRef(
					financeTaxDetail.getFinReference(), financeTaxDetail.getCustCIF(), "_AView");
			if (null == jointAccountDetail) {
				String[] valueParm = new String[1];
				valueParm[0] = financeTaxDetail.getCustCIF();
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90266", valueParm)));

				return errorsList;
			} else {
				if (!(customer.getCustID() == jointAccountDetail.getCustID())) {
					String[] valueParm = new String[1];
					valueParm[0] = financeTaxDetail.getCustCIF();
					errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90102", valueParm)));

					return errorsList;
				}
			}
			financeTaxDetail.setTaxCustId(customer.getCustID());
			break;
		default:
			String[] valueParm = new String[1];
			valueParm[0] = financeTaxDetail.getApplicableFor();
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("RU0040", valueParm)));
			break;
		}

		return errorsList;
	}

	@Override
	public int getFinanceTaxDetailsByCount(String finReference) {
		return financeTaxDetailDAO.getFinTaxDetailsCount(finReference);
	}

	@Override
	public List<GuarantorDetail> getGuarantorDetailByFinRef(String finReference, String type) {
		logger.debug(Literal.ENTERING);
		logger.debug(Literal.LEAVING);
		return this.guarantorDetailDAO.getGuarantorDetailByFinRef(finReference, type);
	}

	public List<JointAccountDetail> getJountAccountDetailByFinRef(String finReference, String type) {
		logger.debug(Literal.ENTERING);
		logger.debug(Literal.LEAVING);

		return this.jountAccountDetailDAO.getJountAccountDetailByFinRef(finReference, type);

	}

	@Override
	public Customer getCustomerByID(long id) {
		logger.debug(Literal.ENTERING);
		logger.debug(Literal.LEAVING);

		return this.customerDAO.getCustomerByID(id);
	}

	@Override
	public FinanceMain getFinanceDetailsForService(String finReference, String type, boolean isWIF) {
		logger.debug(Literal.ENTERING);
		logger.debug(Literal.LEAVING);

		return this.financeMainDAO.getFinanceDetailsForService(finReference, type, isWIF);
	}

	/**
	 * to validate the GST Number
	 */
	@Override
	public AuditDetail gstNumbeValidation(AuditDetail auditDetail, FinanceTaxDetail financeTaxDetail) {
		logger.debug(Literal.ENTERING);

		long custId = financeTaxDetail.getTaxCustId();
		String taxNumber = financeTaxDetail.getTaxNumber();

		if (custId != 0) {
			String panNumber = "";
			String gstStateCode = "";

			if (StringUtils.isNotBlank(taxNumber)) {
				//if GST Number is already exist or not
				List<FinanceTaxDetail> financeTaxDetails = getFinanceTaxDetailDAO().getGSTNumberAndCustCIF(custId,
						taxNumber, "_View");

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

				Province province = this.provinceDAO.getProvinceById(financeTaxDetail.getCountry(),
						financeTaxDetail.getProvince(), "");
				if (province != null) {
					gstStateCode = province.getTaxStateCode();
				}
				if (auditDetail != null && auditDetail.getModelData() != null) {
					Object modelObj = auditDetail.getModelData();
					if (modelObj instanceof CustomerDetails) {
						panNumber = ((CustomerDetails) modelObj).getCustomer().getCustCRCPR();
					} else {
						panNumber = customerDAO.getCustCRCPRById(custId, "");
					}
				}
				if (StringUtils.isNotBlank(gstStateCode)) { //if GST State Code is not available
					if (!StringUtils.equalsIgnoreCase(gstStateCode, taxNumber.substring(0, 2))) {
						auditDetail.setErrorDetail(ErrorUtil
								.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "65023", null, null)));
					}
				}

				if (StringUtils.isNotBlank(panNumber)) { //if PAN number is not available in GST Number
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
	public boolean isFinReferenceExitsinLQ(String finReference, TableType tempTab, boolean wif) {
		return this.financeMainDAO.isFinReferenceExitsinLQ(finReference, tempTab, wif);
	}

	@Override
	public CustomerAddres getHighPriorityCustAddr(final long id) {
		return customerAddresDAO.getHighPriorityCustAddr(id, "_AView");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO
	 *            the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @return the financeTaxDetailDAO
	 */
	public FinanceTaxDetailDAO getFinanceTaxDetailDAO() {
		return financeTaxDetailDAO;
	}

	/**
	 * @param financeTaxDetailDAO
	 *            the financeTaxDetailDAO to set
	 */
	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	public void setGuarantorDetailDAO(GuarantorDetailDAO guarantorDetailDAO) {
		this.guarantorDetailDAO = guarantorDetailDAO;
	}

	public void setJountAccountDetailDAO(JountAccountDetailDAO jountAccountDetailDAO) {
		this.jountAccountDetailDAO = jountAccountDetailDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public ProvinceDAO getProvinceDAO() {
		return provinceDAO;
	}

	public void setProvinceDAO(ProvinceDAO provinceDAO) {
		this.provinceDAO = provinceDAO;
	}

	public void setPinCodeDAO(PinCodeDAO pinCodeDAO) {
		this.pinCodeDAO = pinCodeDAO;
	}

	public void setCityDAO(CityDAO cityDAO) {
		this.cityDAO = cityDAO;
	}

	public void setCountryDAO(CountryDAO countryDAO) {
		this.countryDAO = countryDAO;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public CustomerAddresDAO getCustomerAddresDAO() {
		return customerAddresDAO;
	}

	public void setCustomerAddresDAO(CustomerAddresDAO customerAddresDAO) {
		this.customerAddresDAO = customerAddresDAO;
	}

}
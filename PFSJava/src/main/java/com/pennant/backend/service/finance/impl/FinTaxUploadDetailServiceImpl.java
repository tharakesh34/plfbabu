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
 * FileName    		:  ManualAdviseServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-04-2017    														*
 *                                                                  						*
 * Modified Date    :  21-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-04-2017       PENNANT	                 0.1                                            * 
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
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.PinCodeDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinTaxUploadDetailDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.systemmasters.ProvinceDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.FinTaxUploadDetail;
import com.pennant.backend.model.FinTaxUploadHeader;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinTaxUploadDetailService;
import com.pennant.backend.service.finance.JointAccountDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>ManualAdvise</b>.<br>
 */
public class FinTaxUploadDetailServiceImpl extends GenericService<FinTaxUploadHeader>
		implements FinTaxUploadDetailService {
	private static final Logger			logger	= Logger.getLogger(FinTaxUploadDetailServiceImpl.class);

	private AuditHeaderDAO				auditHeaderDAO;
	private FinTaxUploadDetailDAO		finTaxUploadDetailDAO;
	private FinanceTaxDetailDAO			financeTaxDetailDAO;
	private PinCodeDAO					PinCodeDAO;
	private FinanceMainDAO				financeMainDAO;
	private CustomerDAO					customerDAO;
	private ProvinceDAO					provinceDAO;
	private JointAccountDetailService	jointAccountDetailService;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public FinTaxUploadDetailDAO getFinTaxUploadDetailDAO() {
		return finTaxUploadDetailDAO;
	}

	public void setFinTaxUploadDetailDAO(FinTaxUploadDetailDAO finTaxUploadDetailDAO) {
		this.finTaxUploadDetailDAO = finTaxUploadDetailDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public FinanceTaxDetailDAO getFinanceTaxDetailDAO() {
		return financeTaxDetailDAO;
	}

	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	@Override
	public List<FinTaxUploadDetail> getFinTaxDetailUploadById(String reference, String type, String status) {
		return finTaxUploadDetailDAO.getFinTaxDetailUploadById(reference, type, status);
	}

	public PinCodeDAO getPinCodeDAO() {
		return PinCodeDAO;
	}

	public void setPinCodeDAO(PinCodeDAO pinCodeDAO) {
		PinCodeDAO = pinCodeDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public void setProvinceDAO(ProvinceDAO provinceDAO) {
		this.provinceDAO = provinceDAO;
	}

	public void setJointAccountDetailService(JointAccountDetailService jointAccountDetailService) {
		this.jointAccountDetailService = jointAccountDetailService;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {

		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String tableType = "";
		FinTaxUploadHeader finTaxUploadHeader = (FinTaxUploadHeader) auditHeader.getAuditDetail().getModelData();

		if (finTaxUploadHeader.isWorkflow()) {
			tableType = "_Temp";
		}

		if (finTaxUploadHeader.isNew()) {
			getFinTaxUploadDetailDAO().save(finTaxUploadHeader, tableType);
		} else {
			getFinTaxUploadDetailDAO().update(finTaxUploadHeader, tableType);
		}

		if (finTaxUploadHeader.getFinTaxUploadDetailList() != null
				&& finTaxUploadHeader.getFinTaxUploadDetailList().size() > 0) {

			for (FinTaxUploadDetail finTaxDetail : finTaxUploadHeader.getFinTaxUploadDetailList()) {
				finTaxDetail.setBatchReference(String.valueOf(finTaxUploadHeader.getBatchReference()));
				finTaxDetail.setNewRecord(finTaxUploadHeader.isNew());

			}
			List<AuditDetail> details = finTaxUploadHeader.getAuditDetailMap().get("TaxDetail");
			details = processTaxDetails(details, tableType);
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * Method For Preparing List of AuditDetails for Customer Ratings
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	private List<AuditDetail> processTaxDetails(List<AuditDetail> auditDetails, String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			FinTaxUploadDetail finTaxUploadDetail = (FinTaxUploadDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				finTaxUploadDetail.setRoleCode("");
				finTaxUploadDetail.setNextRoleCode("");
				finTaxUploadDetail.setTaskId("");
				finTaxUploadDetail.setNextTaskId("");
			}

			if (finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (finTaxUploadDetail.isNewRecord()) {
				saveRecord = true;
				if (finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finTaxUploadDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finTaxUploadDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finTaxUploadDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finTaxUploadDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = finTaxUploadDetail.getRecordType();
				recordStatus = finTaxUploadDetail.getRecordStatus();
				finTaxUploadDetail.setRecordType("");
				finTaxUploadDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				getFinTaxUploadDetailDAO().saveFintaxDetail(finTaxUploadDetail, type);
			}

			if (updateRecord) {
				getFinTaxUploadDetailDAO().updateFintaxDetail(finTaxUploadDetail, type);
			}

			if (deleteRecord) {
				getFinTaxUploadDetailDAO().deleteFintaxDetail(finTaxUploadDetail, type);
			}

			if (approveRec) {
				finTaxUploadDetail.setRecordType(rcdType);
				finTaxUploadDetail.setRecordStatus(recordStatus);
			}

			auditDetails.get(i).setModelData(finTaxUploadDetail);
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinTaxUploadHeader finTaxUploadHeader = (FinTaxUploadHeader) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (finTaxUploadHeader.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		if (finTaxUploadHeader.getFinTaxUploadDetailList() != null
				&& finTaxUploadHeader.getFinTaxUploadDetailList().size() > 0) {
			auditDetailMap.put("TaxDetail", setTaxDetailsData(finTaxUploadHeader, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("TaxDetail"));
		}

		finTaxUploadHeader.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(finTaxUploadHeader);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");
		return auditHeader;
	}

	private List<AuditDetail> setTaxDetailsData(FinTaxUploadHeader finTaxUploadHeader, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinTaxUploadDetail detail = new FinTaxUploadDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(detail, detail.getExcludeFields());

		for (int i = 0; i < finTaxUploadHeader.getFinTaxUploadDetailList().size(); i++) {
			FinTaxUploadDetail finTaxUploadDetail = finTaxUploadHeader.getFinTaxUploadDetailList().get(i);

			if (StringUtils.isEmpty(finTaxUploadDetail.getRecordType())) {
				continue;
			}

			finTaxUploadDetail.setWorkflowId(finTaxUploadDetail.getWorkflowId());

			boolean isRcdType = false;

			if (finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finTaxUploadDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finTaxUploadDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finTaxUploadDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				finTaxUploadDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finTaxUploadDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			finTaxUploadDetail.setRecordStatus(finTaxUploadHeader.getRecordStatus());
			finTaxUploadDetail.setUserDetails(finTaxUploadHeader.getUserDetails());
			finTaxUploadDetail.setLastMntOn(finTaxUploadHeader.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
					finTaxUploadDetail.getBefImage(), finTaxUploadDetail));
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	public AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {

		FinTaxUploadHeader finTaxUploadHeader = (FinTaxUploadHeader) auditDetail.getModelData();
		for (FinTaxUploadDetail taxuploadDetail : finTaxUploadHeader.getFinTaxUploadDetailList()) {
			String[] errParm = new String[3];
			String[] valueParm = new String[1];
			boolean idExist = false;
			errParm[0] = String.valueOf(taxuploadDetail.getAggrementNo());

			//--------Length validations-----------------------------------

			if (StringUtils.isEmpty(taxuploadDetail.getAggrementNo())) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails("99015", null), usrLanguage));
			} else {
				if (taxuploadDetail.getAggrementNo().length() > 20) {
					errParm[1] = PennantJavaUtil.getLabel("listheader_AggrementNo.label");
					errParm[2] = 20 + "";
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "99006", errParm, valueParm), usrLanguage));
				}
				//commented As confirmed by chaitanya Dt:24.07.2017
				/* else {
					FinanceTaxDetail fintaxDetail = getFinanceTaxDetailDAO()
							.getFinanceTaxDetail(taxuploadDetail.getAggrementNo(), "_View");
					if (fintaxDetail != null) {
						String[] errParams = new String[1];
						errParams[0] = taxuploadDetail.getAggrementNo();
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "99016", errParams, valueParm),
								usrLanguage));
					}
				}*/
			}

			if (!StringUtils.isEmpty(taxuploadDetail.getTaxCode()) && taxuploadDetail.getTaxCode().length() > 20) {
				errParm[1] = PennantJavaUtil.getLabel("label_Gstin");
				errParm[2] = 20 + "";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "99006", errParm, valueParm), usrLanguage));
			}
			if (taxuploadDetail.getApplicableFor() != null && taxuploadDetail.getApplicableFor().length() > 1) {
				errParm[1] = PennantJavaUtil.getLabel("listheader_ApplicableFor.label");
				errParm[2] = 1 + "";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "99006", errParm, valueParm), usrLanguage));
			}
			if (taxuploadDetail.getApplicant() != null && taxuploadDetail.getApplicant().length() > 20) {
				errParm[1] = PennantJavaUtil.getLabel("listheader_Applicant.label");
				errParm[2] = 20 + "";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "99006", errParm, valueParm), usrLanguage));
			}
			if (taxuploadDetail.getAddrLine1() != null && taxuploadDetail.getAddrLine1().length() > 100) {
				errParm[1] = PennantJavaUtil.getLabel("listheader_AddressLine1.label");
				errParm[2] = 100 + "";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "99006", errParm, valueParm), usrLanguage));
			}
			if (taxuploadDetail.getAddrLine2() != null && taxuploadDetail.getAddrLine2().length() > 100) {
				errParm[1] = PennantJavaUtil.getLabel("listheader_AddressLine2.label");
				errParm[2] = 100 + "";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "99006", errParm, valueParm), usrLanguage));
			}
			if (taxuploadDetail.getAddrLine3() != null && taxuploadDetail.getAddrLine3().length() > 100) {
				errParm[1] = PennantJavaUtil.getLabel("listheader_AddressLine3.label");
				errParm[2] = 100 + "";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "99006", errParm, valueParm), usrLanguage));
			}
			if (taxuploadDetail.getAddrLine4() != null && taxuploadDetail.getAddrLine1() != null
					&& taxuploadDetail.getAddrLine4().length() > 100) {
				errParm[1] = PennantJavaUtil.getLabel("listheader_AddressLine4.label");
				errParm[2] = 100 + "";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "99006", errParm, valueParm), usrLanguage));
			}
			if (taxuploadDetail.getCountry().length() > 2) {
				errParm[1] = PennantJavaUtil.getLabel("listheader_Country.label");
				errParm[2] = 2 + "";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "99006", errParm, valueParm), usrLanguage));
			}
			if (taxuploadDetail.getProvince().length() > 8) {
				errParm[1] = PennantJavaUtil.getLabel("listheader_Province.label");
				errParm[2] = 8 + "";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "99006", errParm, valueParm), usrLanguage));
			}
			if (taxuploadDetail.getCity().length() > 50) {
				errParm[1] = PennantJavaUtil.getLabel("listheader_City.label");
				errParm[2] = 50 + "";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "99006", errParm, valueParm), usrLanguage));
			}
			if (taxuploadDetail.getPinCode().length() > 10) {
				errParm[1] = PennantJavaUtil.getLabel("listheader_PinCode.label");
				errParm[2] = 10 + "";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "99006", errParm, valueParm), usrLanguage));
			}

			//Validate the GST number
			if (StringUtils.isNotBlank(taxuploadDetail.getAggrementNo())) {

				FinanceMain financeMain = financeMainDAO.getFinanceDetailsForService(taxuploadDetail.getAggrementNo(),
						"_View", false);
				//if Aggrement number alone not valid
				if (financeMain == null) {
					String[] errParams = new String[2];

					errParams[0] = PennantJavaUtil.getLabel("listheader_AggrementNo.label");
					errParams[1] = taxuploadDetail.getAggrementNo();
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "99008", errParams, valueParm), usrLanguage));

				} else {
					//if finance exist with that aggrement number. proceed with primary/co-applicant customer
					if (StringUtils.equals(taxuploadDetail.getApplicableFor(),
							PennantConstants.TAXAPPLICABLEFOR_PRIMAYCUSTOMER)) {
						//get the customer attached to the finance
						Customer customer = customerDAO.getCustomerByID(financeMain.getCustID());
						//check wether excel provided and finance related customer both are same
						if (!StringUtils.equals(customer.getCustCIF(), taxuploadDetail.getApplicant())) {
							String[] errParams = new String[2];
							errParams[0] = PennantJavaUtil.getLabel("listheader_Applicant.label") + ":"
									+ taxuploadDetail.getApplicant();
							errParams[1] = taxuploadDetail.getAggrementNo();
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetails(PennantConstants.KEY_FIELD, "99007", errParams, valueParm),
									usrLanguage));
						} else {
							//if customer validated to true proceed with the GST number validation
							validateGstNumber(auditDetail, usrLanguage, taxuploadDetail, valueParm, financeMain,
									customer);
						}

					} else if (StringUtils.equals(taxuploadDetail.getApplicableFor(),
							PennantConstants.TAXAPPLICABLEFOR_COAPPLICANT)) {
						//get the co-applicants attached to the finance
						List<JointAccountDetail> jountAccountDetailList = jointAccountDetailService
								.getJoinAccountDetail(financeMain.getFinReference(), "_View");
						//chek for one match
						for (JointAccountDetail jointAccountDetail : jountAccountDetailList) {
							if (StringUtils.equals(jointAccountDetail.getCustCIF(), taxuploadDetail.getApplicant())) {
								idExist = true;
								break;
							}
						}
						if (!idExist) { //if Co-Applicant is not available then validate
							errParm[0] = taxuploadDetail.getApplicant();
							errParm[1] = taxuploadDetail.getAggrementNo();
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetails(PennantConstants.KEY_FIELD, "99009", errParm, valueParm),
									usrLanguage));
						} else {
							//if co applicant available then get the customer object related to the co-applicant
							Customer customer = customerDAO.getCustomerByCIF(taxuploadDetail.getApplicant(), "_View");
							financeMain.setCustID(customer.getCustID());
							validateGstNumber(auditDetail, usrLanguage, taxuploadDetail, valueParm, financeMain,
									customer);
						}
					}
				}
			}
			//Validate applicable flag.
			if (!(StringUtils.equals(taxuploadDetail.getApplicableFor(),
					PennantConstants.TAXAPPLICABLEFOR_PRIMAYCUSTOMER)
					|| StringUtils.equals(taxuploadDetail.getApplicableFor(),
							PennantConstants.TAXAPPLICABLEFOR_COAPPLICANT))) {
				String[] errParams = new String[2];
				errParams[0] = PennantJavaUtil.getLabel("listheader_ApplicableFor.label") + ":"
						+ taxuploadDetail.getApplicableFor();
				errParams[1] = taxuploadDetail.getAggrementNo();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "99007", errParams, valueParm), usrLanguage));
			}

			//pincode validations against the system .
			if (taxuploadDetail.getPinCode() != null) {
				PinCode pincode = getPinCodeDAO().getPinCode(taxuploadDetail.getPinCode(), "_View");
				String[] errParams = new String[2];
				if (pincode == null) {
					//if pin code is not  available  then validate
					errParams[0] = PennantJavaUtil.getLabel("listheader_PinCode.label") + ":"
							+ taxuploadDetail.getPinCode();
					errParams[1] = taxuploadDetail.getAggrementNo();
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "99007", errParams, valueParm), usrLanguage));
				} else {
					//if pin code is   available  then validate against city and province and country
					if (!StringUtils.equals(pincode.getCity(), taxuploadDetail.getCity())) {
						errParams[0] = PennantJavaUtil.getLabel("listheader_City.label") + ":"
								+ taxuploadDetail.getCity();
						errParams[1] = taxuploadDetail.getAggrementNo();
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "99007", errParams, valueParm),
								usrLanguage));
					}
					if (!StringUtils.equals(pincode.getPCProvince(), taxuploadDetail.getProvince())) {
						errParams[0] = PennantJavaUtil.getLabel("listheader_Province.label") + ":"
								+ taxuploadDetail.getProvince();
						errParams[1] = taxuploadDetail.getAggrementNo();
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "99007", errParams, valueParm),
								usrLanguage));
					}
					if (!StringUtils.equals(pincode.getpCCountry(), taxuploadDetail.getCountry())) {
						errParams[0] = PennantJavaUtil.getLabel("listheader_Country.label") + ":"
								+ taxuploadDetail.getCountry();
						errParams[1] = taxuploadDetail.getAggrementNo();
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "99007", errParams, valueParm),
								usrLanguage));
					}

				}
			} else {
				//pin code no givne then validate
				String[] errParams = new String[1];
				errParams[0] = PennantJavaUtil.getLabel("listheader_PinCode.label");
				errParams[1] = taxuploadDetail.getAggrementNo();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "99007", errParams, valueParm), usrLanguage));
			}

		}
		return auditDetail;
	}

	private void validateGstNumber(AuditDetail auditDetail, String usrLanguage, FinTaxUploadDetail taxuploadDetail,
			String[] valueParm, FinanceMain financeMain, Customer customer) {

		//if customer cif not equal to the provied customer CIF.
		if (StringUtils.isNotBlank(taxuploadDetail.getTaxCode())) {
			String gstStateCode = null;
			String panNumber = customer.getCustCRCPR();
			//if GST Number is already exist or not
			int count = getFinanceTaxDetailDAO().getGSTNumberCount(financeMain.getCustID(),taxuploadDetail.getTaxCode(), "_View");
			if (count != 0) {
				String[] parameters = new String[2];
				parameters[0] = PennantJavaUtil.getLabel("listheader_TaxNumber.label") + ": ";
				parameters[1] = taxuploadDetail.getTaxCode();
				auditDetail.setErrorDetail(ErrorUtil
						.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", parameters, null)));
			}

			Province province = this.provinceDAO.getProvinceById(taxuploadDetail.getCountry(),
					taxuploadDetail.getProvince(), "");
			if (province != null) {
				gstStateCode = province.getTaxStateCode();
			}

			if (StringUtils.isNotBlank(gstStateCode)) { //if GST State Code is not available
				if (!StringUtils.equalsIgnoreCase(gstStateCode, taxuploadDetail.getTaxCode().substring(0, 2))) {
					String[] errParams = new String[2];
					errParams[0] = taxuploadDetail.getApplicant();
					errParams[1] = taxuploadDetail.getAggrementNo();
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "99010", errParams, valueParm)));
				}
			}

			if (StringUtils.isNotBlank(panNumber)) { //if PAN number is not available in GST Number
				if (!StringUtils.equalsIgnoreCase(panNumber, taxuploadDetail.getTaxCode().substring(2, 12))) {
					String[] errParams = new String[2];
					errParams[0] = taxuploadDetail.getApplicant();
					errParams[1] = taxuploadDetail.getAggrementNo();
					auditDetail.setErrorDetail(ErrorUtil
							.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "99011", errParams, null)));
				}
			}
		}
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinTaxUploadHeader finTaxUploadHeader = (FinTaxUploadHeader) auditHeader.getAuditDetail().getModelData();
		getFinTaxUploadDetailDAO().delete(finTaxUploadHeader, "");
		auditHeader.setAuditDetails(
				getListAuditDetails(listDeletion(finTaxUploadHeader, "", auditHeader.getAuditTranType())));

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Common Method for Customers list validation
	 * 
	 * @param list
	 * @param method
	 * @param userDetails
	 * @param lastMntON
	 * @return
	 */
	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list) {
		logger.debug("Entering");
		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {

				String transType = "";
				String rcdType = "";
				FinTaxUploadDetail uploadDetail = (FinTaxUploadDetail) ((AuditDetail) list.get(i)).getModelData();

				rcdType = uploadDetail.getRecordType();

				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(rcdType)) {
					transType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(rcdType)
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(rcdType)) {
					transType = PennantConstants.TRAN_DEL;
				} else {
					transType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isNotEmpty(transType)) {
					// check and change below line for Complete code
					auditDetailsList.add(new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(),
							uploadDetail.getBefImage(), uploadDetail));
				}
			}
		}

		logger.debug("Leaving");
		return auditDetailsList;
	}

	/**
	 * Method deletion of feeTier list with existing fee type
	 * 
	 * @param finTaxUploadHeader
	 * @param tableType
	 */
	public List<AuditDetail> listDeletion(FinTaxUploadHeader finTaxUploadHeader, String tableType,
			String auditTranType) {
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		if (finTaxUploadHeader.getFinTaxUploadDetailList() != null
				&& finTaxUploadHeader.getFinTaxUploadDetailList().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinTaxUploadDetail());

			for (int i = 0; i < finTaxUploadHeader.getFinTaxUploadDetailList().size(); i++) {
				FinTaxUploadDetail taxDetail = finTaxUploadHeader.getFinTaxUploadDetailList().get(i);
				if (StringUtils.isNotEmpty(taxDetail.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], taxDetail.getBefImage(),
							taxDetail));
				}
				finTaxUploadDetailDAO.deleteFintaxDetail(finTaxUploadHeader.getFinTaxUploadDetailList().get(i),
						tableType);
			}

		}

		return auditList;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) throws InterfaceException {

		logger.debug("Entering");

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "Approve");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinTaxUploadHeader finTaxUploadHeader = new FinTaxUploadHeader();
		BeanUtils.copyProperties((FinTaxUploadHeader) auditHeader.getAuditDetail().getModelData(), finTaxUploadHeader);
		if (finTaxUploadHeader.isTotalSelected()) {
			getFinTaxUploadDetailDAO().delete(finTaxUploadHeader, "_Temp");
		}
		if (!PennantConstants.RECORD_TYPE_NEW.equals(finTaxUploadHeader.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					getFinTaxUploadDetailDAO().getFinTaxUploadHeaderByRef(finTaxUploadHeader.getBatchReference(), ""));
		}

		if (finTaxUploadHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(listDeletion(finTaxUploadHeader, "", auditHeader.getAuditTranType()));
			getFinTaxUploadDetailDAO().delete(finTaxUploadHeader, "");
		} else {
			finTaxUploadHeader.setRoleCode("");
			finTaxUploadHeader.setNextRoleCode("");
			finTaxUploadHeader.setTaskId("");
			finTaxUploadHeader.setNextTaskId("");
			finTaxUploadHeader.setWorkflowId(0);

			if (finTaxUploadHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				finTaxUploadHeader.setRecordType("");
				if (finTaxUploadHeader.isTotalSelected()) {
					getFinTaxUploadDetailDAO().save(finTaxUploadHeader, "");
				}
			} else {
				tranType = PennantConstants.TRAN_UPD;
				finTaxUploadHeader.setRecordType("");
				getFinTaxUploadDetailDAO().update(finTaxUploadHeader, "");
			}

			if (finTaxUploadHeader.getFinTaxUploadDetailList() != null
					&& finTaxUploadHeader.getFinTaxUploadDetailList().size() > 0) {
				List<AuditDetail> details = finTaxUploadHeader.getAuditDetailMap().get("TaxDetail");
				details = processTaxDetails(details, "");
				auditDetails.addAll(details);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(
				getListAuditDetails(listDeletion(finTaxUploadHeader, "_Temp", auditHeader.getAuditTranType())));
		getAuditHeaderDAO().addAudit(auditHeader);

		//update or insert based on availability.

		for (FinTaxUploadDetail finTaxUploadDetail : finTaxUploadHeader.getFinTaxUploadDetailList()) {
			FinanceTaxDetail financeTaxDetail = getFinanceTaxDetailDAO()
					.getFinanceTaxDetail(finTaxUploadDetail.getAggrementNo(), "_View");
			if (financeTaxDetail != null) {
				Customer customer = customerDAO.getCustomerByCIF(finTaxUploadDetail.getApplicant(), "_View");
				financeTaxDetail.setTaxCustId(customer.getCustID());
				preparefinTaxDetail(finTaxUploadDetail, financeTaxDetail);
				getFinanceTaxDetailDAO().update(financeTaxDetail, TableType.MAIN_TAB);
			} else {
				FinanceTaxDetail detail = new FinanceTaxDetail();
				Customer customer = customerDAO.getCustomerByCIF(finTaxUploadDetail.getApplicant(), "_View");
				detail.setTaxCustId(customer.getCustID());
				detail.setNewRecord(true);
				preparefinTaxDetail(finTaxUploadDetail, detail);
				getFinanceTaxDetailDAO().save(detail, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(finTaxUploadHeader);
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	private void preparefinTaxDetail(FinTaxUploadDetail finTaxUploadDetail, FinanceTaxDetail financeTaxDetail) {

		financeTaxDetail.setFinReference(finTaxUploadDetail.getAggrementNo());
		financeTaxDetail.setApplicableFor(finTaxUploadDetail.getApplicableFor());
		financeTaxDetail.setCustCIF(finTaxUploadDetail.getApplicant());
		financeTaxDetail.setTaxNumber(finTaxUploadDetail.getTaxCode());
		financeTaxDetail.setAddrLine1(finTaxUploadDetail.getAddrLine1());
		financeTaxDetail.setAddrLine2(finTaxUploadDetail.getAddrLine2());
		financeTaxDetail.setAddrLine3(finTaxUploadDetail.getAddrLine3());
		financeTaxDetail.setAddrLine4(finTaxUploadDetail.getAddrLine4());
		financeTaxDetail.setCountry(finTaxUploadDetail.getCountry());
		financeTaxDetail.setProvince(finTaxUploadDetail.getProvince());
		financeTaxDetail.setCity(finTaxUploadDetail.getCity());
		financeTaxDetail.setPinCode(finTaxUploadDetail.getPinCode());
		financeTaxDetail.setTaxExempted(finTaxUploadDetail.isTaxExempted());
		financeTaxDetail.setLastMntBy(finTaxUploadDetail.getLastMntBy());
		financeTaxDetail.setLastMntOn(finTaxUploadDetail.getLastMntOn());
		financeTaxDetail.setVersion(financeTaxDetail.isNewRecord() ? 1 : financeTaxDetail.getVersion() + 1);
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");

/*		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}*/

		FinTaxUploadHeader uploadHeader = (FinTaxUploadHeader) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		if (StringUtils.equals(uploadHeader.getRecordStatus(), PennantConstants.RCD_STATUS_CANCELLED)
				|| uploadHeader.isTotalSelected()) {
			getFinTaxUploadDetailDAO().delete(uploadHeader, "_Temp");
		}
		auditHeader.setAuditDetails(
				getListAuditDetails(listDeletion(uploadHeader, "_Temp", auditHeader.getAuditTranType())));

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public FinTaxUploadHeader getFinTaxUploadHeaderByRef(long ref) {
		return finTaxUploadDetailDAO.getFinTaxUploadHeaderByRef(ref, "_View");
	}

}
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
 * * FileName : LimitDetailServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-03-2016 * *
 * Modified Date : 31-03-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-03-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.limit.impl;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.limit.LimitDetailDAO;
import com.pennant.backend.dao.limit.LimitGroupLinesDAO;
import com.pennant.backend.dao.limit.LimitHeaderDAO;
import com.pennant.backend.dao.limit.LimitReferenceMappingDAO;
import com.pennant.backend.dao.limit.LimitStructureDetailDAO;
import com.pennant.backend.dao.limit.LimitTransactionDetailsDAO;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerGroup;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customerqueuing.CustomerGroupQueuing;
import com.pennant.backend.model.customerqueuing.CustomerQueuing;
import com.pennant.backend.model.limit.LimitDetails;
import com.pennant.backend.model.limit.LimitGroupLines;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.limit.LimitReferenceMapping;
import com.pennant.backend.model.limit.LimitStructureDetail;
import com.pennant.backend.model.limit.LimitTransactionDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.CurrencyService;
import com.pennant.backend.service.customermasters.CustomerGroupService;
import com.pennant.backend.service.customermasters.impl.CustomerDataService;
import com.pennant.backend.service.limit.LimitDetailService;
import com.pennant.backend.service.limitservice.LimitRebuild;
import com.pennant.backend.service.limitservice.impl.LimitManagement;
import com.pennant.backend.util.LimitConstants;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.eod.dao.CustomerGroupQueuingDAO;
import com.pennant.eod.dao.CustomerQueuingDAO;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.notifications.service.NotificationService;

/**
 * Service implementation for methods that depends on <b>LimitDetail</b>.<br>
 * 
 */
public class LimitDetailServiceImpl extends GenericService<LimitDetails> implements LimitDetailService {
	private static final Logger logger = LogManager.getLogger(LimitDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private LimitHeaderDAO limitHeaderDAO;
	private LimitDetailDAO limitDetailDAO;
	private LimitTransactionDetailsDAO limitTransactionDetailDAO;
	private LimitStructureDetailDAO limitStructureDetailDAO;
	private CurrencyService currencyService;
	private CustomerGroupService customerGroupService;
	private LimitManagement limitManagement;
	private LimitReferenceMappingDAO limitReferenceMappingDAO;
	private LimitGroupLinesDAO limitGroupLinesDAO;
	private LimitRebuild limitRebuild;
	private FinanceMainDAO financeMainDAO;

	private CustomerQueuingDAO customerQueuingDAO;
	private CustomerGroupQueuingDAO customerGroupQueuingDAO;

	protected long userID;
	private String userLangauge;
	private NotificationService notificationService;
	private CustomerDAO customerDAO;
	private CustomerDataService customerDataService;

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @return the limitDetail
	 */
	@Override
	public LimitHeader getLimitHeader() {
		return getLimitHeaderDAO().getLimitHeader();
	}

	/**
	 * @return the limitDetail for New Record
	 */
	@Override
	public LimitHeader getNewLimitHeader() {
		return getLimitHeaderDAO().getNewLimitHeader();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table LIMIT_DETAILS/LIMIT_DETAILS_Temp
	 * by using LimitDetailDAO's save method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using LimitDetailDAO's update method 3) Audit the record in to AuditHeader and AdtLIMIT_DETAILS
	 * by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		return saveOrUpdate(auditHeader, false);
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table LIMIT_DETAILS/LIMIT_DETAILS_Temp
	 * by using LimitDetailDAO's save method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using LimitDetailDAO's update method 3) Audit the record in to AuditHeader and AdtLIMIT_DETAILS
	 * by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @param boolean     onlineRequest
	 * @return auditHeader
	 */

	private AuditHeader saveOrUpdate(AuditHeader auditHeader, boolean online) {
		logger.debug(Literal.ENTERING);
		auditHeader = businessValidation(auditHeader, "saveOrUpdate", online);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}
		String tableType = "";
		LimitHeader limitHeader = (LimitHeader) auditHeader.getAuditDetail().getModelData();

		if (limitHeader.isWorkflow()) {
			tableType = "_Temp";
		}

		if (limitHeader.isNewRecord()) {
			limitHeader.setId(getLimitHeaderDAO().save(limitHeader, tableType));
			auditHeader.getAuditDetail().setModelData(limitHeader);
			auditHeader.setAuditReference(String.valueOf(limitHeader.getHeaderId()));
		} else {
			getLimitHeaderDAO().update(limitHeader, tableType);
		}

		if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {

			auditHeader.setAuditDetails(processingCustomerLimitDetailsList(auditHeader.getAuditDetails(),
					limitHeader.getHeaderId(), limitHeader.getCustomerId(), tableType));

		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * LIMIT_DETAILS by using LimitDetailDAO's delete method with type as Blank 3) Audit the record in to AuditHeader
	 * and AdtLIMIT_DETAILS by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		auditHeader = businessValidation(auditHeader, "delete", false);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		LimitHeader limitHeader = (LimitHeader) auditHeader.getAuditDetail().getModelData();
		if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
			getLimitDetailDAO().deletebyHeaderId(limitHeader.getHeaderId(), "");
		}
		getLimitHeaderDAO().delete(limitHeader, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getLimitDetailById fetch the details by using LimitDetailDAO's getLimitDetailById method.
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return LimitDetail
	 */

	@Override
	public LimitHeader getCustomerLimits(long limitHeaderId) {
		logger.debug(Literal.ENTERING);

		LimitHeader limitHeader;
		limitHeader = getLimitHeaderDAO().getLimitHeaderById(limitHeaderId, "_View");

		if (limitHeader != null) {
			List<LimitDetails> limitDetailList = getLimitDetailDAO()
					.getLimitDetailsByHeaderId(limitHeader.getHeaderId(), "_View");
			updateLatestLimitExposures(limitHeader.getHeaderId(), limitDetailList);
			limitHeader.setCustomerLimitDetailsList(limitDetailList);
		}

		logger.debug(Literal.LEAVING);
		return limitHeader;
	}

	/**
	 * 
	 * @param limitHeaderId
	 * @param limitDetailList
	 */
	private void updateLatestLimitExposures(long limitHeaderId, List<LimitDetails> limitDetailList) {
		logger.debug(Literal.ENTERING);

		if (limitDetailList != null && !limitDetailList.isEmpty()) {

			// Update with Latest ReservedLimit, UtilisedLimit and NonRvlUtilised
			List<LimitDetails> mainLimits = getLimitDetailDAO().getLatestLimitExposures(limitHeaderId, "");

			if (mainLimits != null && !mainLimits.isEmpty()) {
				for (LimitDetails limitDetail : limitDetailList) {
					for (LimitDetails detail : mainLimits) {
						if (limitDetail.getDetailId() == detail.getDetailId()) {
							limitDetail.setReservedLimit(detail.getReservedLimit());
							limitDetail.setUtilisedLimit(detail.getUtilisedLimit());
							limitDetail.setNonRvlUtilised(detail.getNonRvlUtilised());
							break;
						}
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * getApprovedLimitDetailById fetch the details by using LimitDetailDAO's getLimitDetailById method. with parameter
	 * id and type as blank. it fetches the approved records from the LIMIT_DETAILS.
	 * 
	 * @param id (int)
	 * @return LimitDetail
	 */

	public LimitHeader getApprovedCustomerLimits(long headerId) {
		logger.debug(Literal.ENTERING);

		LimitHeader limitHeader;
		limitHeader = getLimitHeaderDAO().getLimitHeaderById(headerId, "_AView");
		if (limitHeader != null) {
			limitHeader.setCustomerLimitDetailsList(
					getLimitDetailDAO().getLimitDetailsByHeaderId(limitHeader.getHeaderId(), "_AView"));
		}
		logger.debug(Literal.LEAVING);
		return limitHeader;
	}

	/**
	 * Method for fetch LimitHeader details by customer id.
	 * 
	 * @param custId
	 * @return LimitHeader
	 */
	@Override
	public LimitHeader getLimitHeaderByCustomer(long custId) {
		LimitHeader limitHeader;
		limitHeader = getLimitHeaderDAO().getLimitHeaderByCustomerId(custId, "_AView");
		if (limitHeader != null) {
			limitHeader.setCustomerLimitDetailsList(
					getLimitDetailDAO().getLimitDetailsByHeaderId(limitHeader.getHeaderId(), "_AView"));
		}
		return limitHeader;
	}

	/**
	 * Method for fetch LimitHeader details by customer group id.
	 * 
	 * @param custGrpId
	 * @return LimitHeader
	 */
	@Override
	public LimitHeader getLimitHeaderByCustomerGroupCode(long custGrpId) {
		logger.debug(Literal.ENTERING);

		LimitHeader limitHeader;
		limitHeader = getLimitHeaderDAO().getLimitHeaderByCustomerGroupCode(custGrpId, "_AView");

		if (limitHeader != null) {
			limitHeader.setCustomerLimitDetailsList(
					getLimitDetailDAO().getLimitDetailsByHeaderId(limitHeader.getHeaderId(), "_AView"));
		}

		logger.debug(Literal.LEAVING);
		return limitHeader;
	}

	@Override
	public List<LimitReferenceMapping> getLimitReferences(LimitDetails limitItem) {
		return getLimitReferenceMappingDAO().getLimitReferences(limitItem.getLimitHeaderId(), limitItem.getLimitLine());
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getLimitDetailDAO().delete with
	 * parameters limitDetail,"" b) NEW Add new record in to main table by using getLimitDetailDAO().save with
	 * parameters limitDetail,"" c) EDIT Update record in the main table by using getLimitDetailDAO().update with
	 * parameters limitDetail,"" 3) Delete the record from the workFlow table by using getLimitDetailDAO().delete with
	 * parameters limitDetail,"_Temp" 4) Audit the record in to AuditHeader and AdtLIMIT_DETAILS by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtLIMIT_DETAILS by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doApprove(AuditHeader auditHeader, boolean fromScreen) {
		logger.debug(Literal.ENTERING);
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		boolean rebuild = false;

		LimitHeader limitHeader = new LimitHeader();
		BeanUtils.copyProperties((LimitHeader) auditHeader.getAuditDetail().getModelData(), limitHeader);

		if (limitHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;

			getLimitDetailDAO().deletebyHeaderId(limitHeader.getHeaderId(), "");
			getLimitHeaderDAO().delete(limitHeader, "");

		} else {
			limitHeader.setRoleCode("");
			limitHeader.setNextRoleCode("");
			limitHeader.setTaskId("");
			limitHeader.setNextTaskId("");
			limitHeader.setWorkflowId(0);

			if (limitHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				limitHeader.setRecordType("");
				getLimitHeaderDAO().save(limitHeader, "");
				rebuild = true;

			} else {
				tranType = PennantConstants.TRAN_UPD;
				limitHeader.setRecordType("");
				getLimitHeaderDAO().update(limitHeader, "");
			}
			if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
				auditHeader.setAuditDetails(processingCustomerLimitDetailsList(auditHeader.getAuditDetails(),
						limitHeader.getHeaderId(), limitHeader.getCustomerId(), ""));
				if (fromScreen)
					getLimitDetailDAO().deletebyHeaderId(limitHeader.getHeaderId(), "_Temp");

			}
		}

		// rebuild is required for group hear since , first group added to the customer ,
		// then created the set up for the group
		if (rebuild) {
			if (limitHeader.getCustomerGroup() != 0 && limitHeader.getCustomerGroup() != Long.MIN_VALUE) {
				limitRebuild.processCustomerGroupRebuild(limitHeader.getCustomerGroup(), false, true);
			}

			if (limitHeader.getCustomerId() != 0 && limitHeader.getCustomerId() != Long.MIN_VALUE) {
				// check customer active finance before calling the rebuild

				int count = getFinanceMainDAO().getFinCountByCustId(limitHeader.getCustomerId());
				if (count > 0) {
					limitRebuild.processCustomerRebuild(limitHeader.getCustomerId(), false);
				}

			}
		}

		if (fromScreen)
			getLimitHeaderDAO().delete(limitHeader, "_Temp");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(limitHeader);

		getAuditHeaderDAO().addAudit(auditHeader);

		if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_LIMIT_NOTIFICATION)) {
			sendMailNotification(limitHeader, "Email");
			sendSMSNotification(limitHeader, "SMS");
		}
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private void sendSMSNotification(LimitHeader limitHeader, String string) {
		sendMailNotification(limitHeader, string);

	}

	protected void sendMailNotification(LimitHeader imitHeader, String notifType) {
		logger.debug(Literal.ENTERING);
		Notification notification = new Notification();
		notification.setKeyReference(imitHeader.getCustCIF());
		notification.setModule("LOAN");
		notification.setSubModule("ORIGINATION");
		if (notifType == "Email") {
			notification.setTemplateCode(NotificationConstants.LIMITHEADER_SUCCESS_NOTIFICATION);
		} else if (notifType == "SMS") {
			notification.setTemplateCode(NotificationConstants.LIMITHEADER_SUCCESS_NOTIFICATION_SMS);
		}

		CustomerDetails customerDetails = customerDataService.getCustomerChildDetails(imitHeader.getCustomerId(),
				"_AView");
		if (customerDetails == null) {
			return;
		}

		if (customerDetails.getCustomer() == null) {
			return;
		}

		// Customer Email
		List<CustomerEMail> emailList = customerDetails.getCustomerEMailList();
		if (CollectionUtils.isEmpty(emailList)) {
			return;
		}

		String emailId = null;
		for (CustomerEMail email : emailList) {
			if (Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH) == email.getCustEMailPriority()) {
				emailId = email.getCustEMail();
				break;
			}
		}

		if (StringUtils.isEmpty(emailId)) {
			return;
		}

		List<String> emails = new ArrayList<>();
		emails.add(emailId);
		notification.setEmails(emails);

		// Customer Contact Number
		String mobileNumber = null;
		List<CustomerPhoneNumber> customerPhoneNumbers = customerDetails.getCustomerPhoneNumList();
		for (CustomerPhoneNumber customerPhoneNumber : customerPhoneNumbers) {
			if (Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH) == customerPhoneNumber
					.getPhoneTypePriority()) {
				mobileNumber = customerPhoneNumber.getPhoneNumber();
				break;
			}
		}

		List<String> mobileNumberList = new ArrayList<>();
		mobileNumberList.add(mobileNumber);
		notification.setMobileNumbers(mobileNumberList);
		notificationService.sendNotification(notification, imitHeader);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Customer Limit Rebuild process
	 * 
	 * @param custID
	 * @param custGroupID
	 */
	public List<Object> processCustomerRebuild(long custId) {
		logger.debug(Literal.ENTERING);

		List<Object> returnList = new ArrayList<Object>();

		// Validate Customer is in EOD process or not
		int custQueuingCount = this.customerQueuingDAO.getCountByCustId(custId);
		if (custQueuingCount > 0) {
			returnList.add(custQueuingCount);
			returnList.add(true);
			return returnList;
		}
		Date appDate = SysParamUtil.getAppDate();
		try {
			CustomerQueuing custQueuing = prepareCustomerQueue(custId);
			this.customerQueuingDAO.insertCustQueueForRebuild(custQueuing);

			// Customer Rebuild process
			this.limitRebuild.processCustomerRebuild(custId, true);

			this.customerQueuingDAO.updateStatus(custId, EodConstants.PROGRESS_SUCCESS, appDate);
		} catch (Exception e) {

			returnList.add(0);
			returnList.add(false); // Customer Rebuild Failed
			this.customerQueuingDAO.updateStatus(custId, EodConstants.PROGRESS_FAILED, appDate);
			logger.debug(Literal.EXCEPTION, e);

		} finally {
			this.customerQueuingDAO.logCustomerQueuingByCustId(custId);
			this.customerQueuingDAO.deleteByCustId(custId);
		}

		logger.debug(Literal.LEAVING);
		return returnList;
	}

	/**
	 * Customer Group Limit Rebuild process
	 * 
	 * @param custId
	 * @param groupId
	 */
	public List<Object> processCustomerGroupRebuild(long groupId) {
		logger.debug(Literal.ENTERING);

		List<Object> returnList = new ArrayList<Object>();

		// Validate customer group is in EOD process or not
		int custGrpQueuingCount = this.customerGroupQueuingDAO.getCountByGrpId(groupId);
		if (custGrpQueuingCount > 0) {
			returnList.add(custGrpQueuingCount);
			returnList.add(true);
			return returnList;
		}

		try {
			// Prepare and insert
			CustomerGroupQueuing custGrpQueuing = prepareCustomerGroupQueue(groupId);
			this.customerGroupQueuingDAO.insertCustGrpQueueForRebuild(custGrpQueuing);
			this.customerQueuingDAO.insertCustomerQueueing(groupId, false);

			// Customer Group Rebuild
			this.limitRebuild.processCustomerGroupRebuild(groupId, false, false);

			this.customerGroupQueuingDAO.updateStatus(groupId, EodConstants.PROGRESS_SUCCESS);
			this.customerQueuingDAO.updateCustomerQueuingStatus(groupId, EodConstants.PROGRESS_SUCCESS);
		} catch (Exception e) {

			returnList.add(0);
			returnList.add(false); // Customer Group Rebuild Failed
			this.customerGroupQueuingDAO.updateStatus(groupId, EodConstants.PROGRESS_FAILED);
			// this.customerQueuingDAO.updateCustomerQueuingStatus(groupId, EodConstants.PROGRESS_FAILED); // Check
			// Required or Not ?
			logger.debug("Exception: ", e);

		} finally {
			this.customerGroupQueuingDAO.logCustomerGroupQueuingByGrpId(groupId);
			this.customerGroupQueuingDAO.deleteByGrpId(groupId);

			this.customerQueuingDAO.logCustomerQueuingByGrpId(groupId);
			this.customerQueuingDAO.deleteByGroupId(groupId);
		}

		logger.debug(Literal.LEAVING);
		return returnList;
	}

	/**
	 * 
	 * @param custId
	 * @return
	 */
	private CustomerQueuing prepareCustomerQueue(long custId) {
		logger.debug(Literal.ENTERING);

		// check customer active finance before calling the rebuild
		boolean loanExist = false;
		int count = getFinanceMainDAO().getFinCountByCustId(custId);
		if (count > 0) {
			loanExist = true;
		}

		CustomerQueuing customerQueuing = new CustomerQueuing();
		customerQueuing.setCustID(custId);
		customerQueuing.setThreadId(0);
		customerQueuing.setEodDate(SysParamUtil.getAppValueDate());
		customerQueuing.setLoanExist(loanExist);
		customerQueuing.setLimitRebuild(true);
		customerQueuing.setEodProcess(false);

		customerQueuing.setStartTime(DateUtility.getSysDate());
		customerQueuing.setProgress(EodConstants.PROGRESS_IN_PROCESS);

		logger.debug(Literal.LEAVING);
		return customerQueuing;
	}

	/**
	 * 
	 * @param groupId
	 * @return
	 */
	private CustomerGroupQueuing prepareCustomerGroupQueue(long groupId) {
		logger.debug(Literal.ENTERING);

		CustomerGroupQueuing custGrpQueuing = new CustomerGroupQueuing();
		custGrpQueuing.setGroupId(groupId);
		custGrpQueuing.setEodDate(SysParamUtil.getAppValueDate());
		custGrpQueuing.setStartTime(DateUtility.getSysDate());
		custGrpQueuing.setProgress(EodConstants.PROGRESS_IN_PROCESS);
		custGrpQueuing.setEodProcess(false);

		logger.debug(Literal.LEAVING);
		return custGrpQueuing;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getLimitDetailDAO().delete with parameters limitDetail,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtLIMIT_DETAILS by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		auditHeader = businessValidation(auditHeader, "doApprove", false);
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}
		LimitHeader limitHeader = (LimitHeader) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
			getLimitDetailDAO().deletebyHeaderId(limitHeader.getHeaderId(), "_Temp");
		}
		getLimitHeaderDAO().delete(limitHeader, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) validate the audit detail 2) if any error/Warnings then
	 * assign the to auditHeader 3) identify the nextprocess
	 * 
	 * @param AuditHeader (auditHeader)
	 * @param boolean     onlineRequest
	 * @return auditHeader
	 */

	private AuditHeader businessValidation(AuditHeader auditHeader, String method, boolean onlineRequest) {
		logger.debug(Literal.ENTERING);
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method,
				onlineRequest);
		auditHeader.setAuditDetail(auditDetail);

		// AuditDetail auditDetail=auditHeader.getAuditDetail();

		auditHeader = getAuditDetails(auditHeader, method, onlineRequest);

		if (auditHeader.getAuditDetails() != null && !auditHeader.getAuditDetails().isEmpty()) {
			for (AuditDetail detail : auditHeader.getAuditDetails()) {
				auditHeader.setErrorList(detail.getErrorDetails());
			}
		}

		auditDetail.setErrorDetails(
				ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), auditHeader.getUsrLanguage()));

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Common Method for Retrieving AuditDetails List
	 * 
	 * @param auditHeader
	 * @param method
	 * @param onlineRequest
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method, boolean onlineRequest) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		LimitHeader limitDetail = (LimitHeader) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if (method.equals("saveOrUpdate") || method.equals("doApprove") || method.equals("doReject")) {
			if (limitDetail.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		if (limitDetail.getCustomerLimitDetailsList() != null && limitDetail.getCustomerLimitDetailsList().size() > 0) {
			auditDetails.addAll(setCustomerLimitDetailsAuditData(limitDetail, auditTranType, method));
		}

		auditHeader.getAuditDetail().setModelData(limitDetail);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param limitHeader
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setCustomerLimitDetailsAuditData(LimitHeader limitHeader, String auditTranType,
			String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		for (int i = 0; i < limitHeader.getCustomerLimitDetailsList().size(); i++) {

			LimitDetails limitDetails = limitHeader.getCustomerLimitDetailsList().get(i);
			limitDetails.setLimitHeaderId(limitHeader.getHeaderId());
			limitDetails.setLastMntBy(limitHeader.getLastMntBy());
			limitDetails.setRoleCode(limitHeader.getRoleCode());
			limitDetails.setNextRoleCode(limitHeader.getNextRoleCode());
			limitDetails.setTaskId(limitHeader.getTaskId());
			limitDetails.setNextTaskId(limitHeader.getNextTaskId());
			limitDetails.setWorkflowId(limitHeader.getWorkflowId());
			limitDetails.setUserDetails(limitHeader.getUserDetails());
			limitDetails.setCreatedBy(limitHeader.getCreatedBy());

			if (StringUtils.isEmpty(limitDetails.getRecordType())) {
				continue;
			}

			/*
			 * boolean isRcdType = false;
			 * 
			 * if (limitDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
			 * limitDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW); isRcdType = true; } else if
			 * (limitDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
			 * limitDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD); isRcdType = true; } else if
			 * (limitDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
			 * limitDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL); isRcdType = true; }
			 */

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (limitDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (limitDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| limitDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			limitDetails.setRecordStatus(limitHeader.getRecordStatus());
			limitDetails.setLastMntOn(limitHeader.getLastMntOn());
			try {
				limitDetails.setCreatedOn(limitHeader.getCreatedOn());
			} catch (DatatypeConfigurationException e) {
				logger.warn("Exception: ", e);
			}

			/*
			 * if (limitDetails.getCommitmentRefList() != null && limitDetails.getCommitmentRefList() .size() > 0) {
			 * auditDetails.addAll(setCommitmentRefDetailsAuditData(limitDetails, auditTranType, method)); }
			 */

			if (!limitDetails.getRecordType().equals("")) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, limitDetails.getBefImage(), limitDetails));
			}
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	/*
	 * private List<AuditDetail> setCommitmentRefDetailsAuditData( LimitDetails limitDetails, String auditTranType,
	 * String method) { List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
	 * 
	 * for (int i=0;i<limitDetails.getCommitmentRefList().size();i++) {
	 * 
	 * LimitCommitmentReference limitComtReference=limitDetails.getCommitmentRefList().get(i);
	 * limitComtReference.setLimitDetailId(limitDetails.getDetailId());
	 * limitComtReference.setLastMntBy(limitDetails.getLastMntBy());
	 * limitComtReference.setRoleCode(limitDetails.getRoleCode());
	 * limitComtReference.setNextRoleCode(limitDetails.getNextRoleCode());
	 * limitComtReference.setTaskId(limitDetails.getTaskId());
	 * limitComtReference.setNextTaskId(limitDetails.getNextTaskId());
	 * limitComtReference.setWorkflowId(limitDetails.getWorkflowId());
	 * limitComtReference.setUserDetails(limitDetails.getUserDetails());
	 * limitComtReference.setCreatedBy(limitDetails.getCreatedBy());
	 * 
	 * 
	 * boolean isRcdType = false;
	 * 
	 * if (limitComtReference.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
	 * limitComtReference.setRecordType(PennantConstants.RECORD_TYPE_NEW); isRcdType = true; } else if
	 * (limitComtReference.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
	 * limitComtReference.setRecordType(PennantConstants.RECORD_TYPE_UPD); isRcdType = true; } else if
	 * (limitComtReference.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
	 * limitComtReference.setRecordType(PennantConstants.RECORD_TYPE_DEL); isRcdType = true; }
	 * 
	 * if (!auditTranType.equals(PennantConstants.TRAN_WF)) { if (limitComtReference.getRecordType().equalsIgnoreCase(
	 * PennantConstants.RECORD_TYPE_NEW)) { auditTranType = PennantConstants.TRAN_ADD; } else if
	 * (limitComtReference.getRecordType().equalsIgnoreCase( PennantConstants.RECORD_TYPE_DEL) ||
	 * limitComtReference.getRecordType().equalsIgnoreCase( PennantConstants.RECORD_TYPE_CAN)) { auditTranType =
	 * PennantConstants.TRAN_DEL; } else { auditTranType = PennantConstants.TRAN_UPD; } }
	 * 
	 * limitComtReference.setRecordStatus(limitDetails.getRecordStatus());
	 * limitComtReference.setLastMntOn(limitDetails.getLastMntOn()); try {
	 * limitComtReference.setCreatedOn(limitDetails.getCreatedOn()); } catch (DatatypeConfigurationException e) {
	 * logger.error("Exception: ", e); }
	 * 
	 * if (!limitComtReference.getRecordType().equals("")) { auditDetails.add(new AuditDetail(auditTranType, i + 1,
	 * limitComtReference .getBefImage(), limitComtReference)); } }
	 * 
	 * logger.debug(Literal.LEAVING); return auditDetails; }
	 */

	/**
	 * Method For Preparing List of AuditDetails for Business Units
	 * 
	 * @param auditDetails
	 * @param id
	 * @param custId
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingCustomerLimitDetailsList(List<AuditDetail> auditDetails, long id, long custId,
			String type) {

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			if (auditDetails.get(i).getModelData() instanceof LimitDetails) {
				LimitDetails limitDetails = (LimitDetails) auditDetails.get(i).getModelData();
				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = false;
				String rcdType = "";
				String recordStatus = "";
				if (type.equals("")) {
					approveRec = true;
					limitDetails.setRoleCode("");
					limitDetails.setNextRoleCode("");
					limitDetails.setTaskId("");
					limitDetails.setNextTaskId("");
					limitDetails.setWorkflowId(0);
				}
				limitDetails.setLimitHeaderId(id);

				if (limitDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					deleteRecord = true;
				} else if (limitDetails.isNewRecord()) {
					saveRecord = true;
					if (limitDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						limitDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (limitDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
						limitDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (limitDetails.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
						limitDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}

				} else if (limitDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (limitDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
					updateRecord = true;
				} else if (limitDetails.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					if (approveRec) {
						deleteRecord = true;
					} else if (limitDetails.isNewRecord()) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				}
				if (approveRec) {
					rcdType = limitDetails.getRecordType();
					recordStatus = limitDetails.getRecordStatus();
					limitDetails.setRecordType("");
					limitDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				if (saveRecord) {
					getLimitDetailDAO().save(limitDetails, type);
				}

				if (updateRecord) {
					getLimitDetailDAO().update(limitDetails, type);
				}

				if (deleteRecord) {
					getLimitDetailDAO().deletebyHeaderId(limitDetails.getLimitHeaderId(), type);
				}

				if (approveRec) {
					limitDetails.setRecordType(rcdType);
					limitDetails.setRecordStatus(recordStatus);
				}
				auditDetails.get(i).setModelData(limitDetails);

			}
		}
		return auditDetails;

	}

	////////// ####### FOR DEMO #########////////////
	/**
	 * Method to process External finance Detail through Excel file
	 * 
	 * @param finInput
	 */
	public LimitHeader procExternalFinance(InputStream finInput, LoggedInUser user) {
		logger.debug(Literal.ENTERING);
		userID = user.getUserId();
		userLangauge = user.getLanguage();
		LimitHeader headerDetails = new LimitHeader();
		String status = "";
		int rcdCount = 0;
		int successRcdCount = 0;
		status = successRcdCount + " records has been processed out of " + rcdCount;

		try {
			POIFSFileSystem finFileSystem = new POIFSFileSystem(finInput);
			@SuppressWarnings("resource")
			HSSFWorkbook finWorkBook = new HSSFWorkbook(finFileSystem);
			HSSFSheet finSheet = finWorkBook.getSheetAt(0);
			@SuppressWarnings("rawtypes")
			Iterator rowIter = finSheet.rowIterator();

			HSSFRow finRow;

			while (rowIter.hasNext()) {
				finRow = (HSSFRow) rowIter.next();
				if (finRow.getRowNum() < 1) {
					continue;
				}

				// New Object creation on each Record

				if (finRow.getCell(0) != null) {
					rcdCount = rcdCount + 1;

					headerDetails = new LimitHeader();
					headerDetails.setNewRecord(true);
					headerDetails.setActive(true);
					headerDetails.setVersion(1);
					headerDetails.setCreatedBy(user.getUserId());
					headerDetails.setCreatedOn(new Timestamp(System.currentTimeMillis()));
					headerDetails.setLastMntBy(user.getUserId());
					headerDetails.setLastMntOn(new Timestamp(System.currentTimeMillis()));

					headerDetails = prepareExtFinanceData(finRow, headerDetails, user);
					headerDetails = validateExtFinanceData(headerDetails);

					if ("E".equals(headerDetails.getRecordStatus())) {
						logger.error("Please Check the values");
					} else {
						// Save Finance Data into DataBase
						successRcdCount = successRcdCount + 1;
						processFinanceData(user, headerDetails);
					}
				} else {
					status = successRcdCount + " records has been processed out of " + rcdCount;
					headerDetails.setStatus(status);
					break;
				}
			}
			status = successRcdCount + " records has been processed out of " + rcdCount;
			headerDetails.setStatus(status);
		} catch (Exception e) {
			logger.error(e);
			return headerDetails;
		}
		headerDetails.setStatus(status);
		logger.debug(Literal.LEAVING);
		return headerDetails;
	}

	private void processFinanceData(LoggedInUser usrDetails, LimitHeader headerDetails)
			throws DatatypeConfigurationException {
		logger.debug(Literal.ENTERING);
		AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_WF, 1, null, headerDetails);

		AuditHeader auditHeader = new AuditHeader(String.valueOf(headerDetails.getHeaderId()), null, null, null,
				auditDetail, headerDetails.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
		doApprove(auditHeader, false);
		logger.debug(Literal.LEAVING);
	}

	private void setRecordDetails(LimitDetails limitDetails, LimitHeader headerDetails)
			throws DatatypeConfigurationException {
		limitDetails.setNewRecord(true);
		limitDetails.setLimitHeaderId(headerDetails.getHeaderId());
		limitDetails.setExpiryDate(headerDetails.getLimitExpiryDate());
		limitDetails.setLastMntBy(headerDetails.getLastMntBy());
		limitDetails.setLastMntOn(headerDetails.getLastMntOn());
		limitDetails.setCreatedBy(headerDetails.getCreatedBy());
		limitDetails.setCreatedOn(headerDetails.getCreatedOn());
		limitDetails.setVersion(headerDetails.getVersion());
		limitDetails.setRecordType(headerDetails.getRecordType());
		limitDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
	}

	private String getValue(HSSFCell cell) {
		if (cell != null) {
			return StringUtils.trimToEmpty(cell.toString());
		}
		return "";
	}

	private BigDecimal getDecimalValue(HSSFCell cell) {
		String strValue = null;
		if (cell != null) {
			strValue = StringUtils.trimToNull(cell.toString());

			if (strValue == null) {
				return BigDecimal.ZERO;
			} else {
				return new BigDecimal(cell.getNumericCellValue());
			}
		}
		return BigDecimal.ZERO;
	}

	/***
	 * Method to set excel data to external finanace bean.
	 * 
	 * @param finRow
	 * @param headerDetails
	 * @param usrDetails
	 * @throws DatatypeConfigurationException
	 */
	public LimitHeader prepareExtFinanceData(HSSFRow finRow, LimitHeader headerDetails, LoggedInUser usrDetails)
			throws DatatypeConfigurationException {
		logger.debug(Literal.ENTERING);

		// Customer Id
		if (StringUtils.isNotEmpty(getValue(finRow.getCell(0)))) {
			headerDetails.setCustomerId(Long.valueOf(getValue(finRow.getCell(0))));
		}

		// Branch
		if (StringUtils.isNotEmpty(getValue(finRow.getCell(1)))) {
			headerDetails.setResponsibleBranch(String.valueOf(getValue(finRow.getCell(1))));
		}

		// Currency
		if (StringUtils.isNotEmpty(getValue(finRow.getCell(2)))) {
			headerDetails.setLimitCcy(getValue(finRow.getCell(2)));
		}

		// Customer Id
		if (StringUtils.isNotEmpty(getValue(finRow.getCell(3)))) {
			headerDetails.setLimitRvwDate(DateUtility.getDate(getValue(finRow.getCell(3))));
		}
		// Customer Id
		if (StringUtils.isNotEmpty(getValue(finRow.getCell(4)))) {
			headerDetails.setLimitExpiryDate(DateUtility.getDate(getValue(finRow.getCell(4))));
		}
		// Customer Id
		if (StringUtils.isNotEmpty(getValue(finRow.getCell(5)))) {
			headerDetails.setLimitStructureCode(getValue(finRow.getCell(5)));
		}
		headerDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		headerDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		headerDetails.setWorkflowId(0);
		headerDetails.setNewRecord(true);
		headerDetails.setUserDetails(usrDetails);

		List<LimitStructureDetail> structurList = getLimitStructureDetailDAO()
				.getLimitStructureDetailById(headerDetails.getLimitStructureCode(), "");
		BigDecimal total = BigDecimal.ZERO;
		List<LimitDetails> limitDetailsList = new ArrayList<LimitDetails>();

		if (structurList != null)
			for (LimitStructureDetail structure : structurList) {
				LimitDetails detail = new LimitDetails();
				int i = 5;
				i = i + structure.getItemSeq();

				// Limit Amount
				if (StringUtils.isNotEmpty(getValue(finRow.getCell(i)))) {
					detail.setLimitSanctioned(getDecimalValue((finRow.getCell(i))));
					detail.setLimitSanctioned(getDecimalValue((finRow.getCell(i))));
				} else {
					detail.setLimitSanctioned(BigDecimal.ZERO);
					detail.setLimitSanctioned(BigDecimal.ZERO);
				}

				total = total.add(detail.getLimitSanctioned());

				if (structure.getLimitLine() == null) {
					detail.setGroupCode(structure.getGroupCode());
					detail.setLimitSanctioned(total);
					detail.setLimitSanctioned(total);
				} else {
					detail.setLimitLine(structure.getLimitLine());
				}
				detail.setLimitCheck(true);
				detail.setEditable(structure.isEditable());
				detail.setItemSeq(structure.getItemSeq());
				setRecordDetails(detail, headerDetails);

				limitDetailsList.add(detail);
			}
		if (headerDetails.getCustomerLimitDetailsList() == null) {
			headerDetails.setCustomerLimitDetailsList(new ArrayList<LimitDetails>());
		}
		headerDetails.setCustomerLimitDetailsList(limitDetailsList);

		logger.debug(Literal.LEAVING);
		return headerDetails;
	}

	/*
	 * ================================================================================================================
	 * VALDIATE EXTERNAL INPUT DATA
	 * ================================================================================================================
	 */
	public LimitHeader validateExtFinanceData(LimitHeader headerDetails)
			throws IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING);

		// Validate REFERENCE
		if (headerDetails.getCustomerId() == 0) {
			headerDetails.setRecordStatus("E");
			headerDetails.setErrDesc(ErrorUtil
					.getErrorDetail(new ErrorDetail("41002", "", new String[] { "Customer", "" }), userLangauge)
					.getError());
			return headerDetails;
		}

		// Check whether Finance already exists with same reference and return if so

		if (getLimitHeaderDAO().isCustomerExists(headerDetails.getCustomerId(), "")) {

			headerDetails.setRecordStatus("E");
			headerDetails
					.setErrDesc(
							ErrorUtil
									.getErrorDetail(
											new ErrorDetail("30506", "",
													new String[] { "Customer",
															String.valueOf(headerDetails.getCustomerId()) }),
											userLangauge)
									.getError());

			return headerDetails;
		}

		if (headerDetails.getResponsibleBranch() == null || StringUtils.isEmpty(headerDetails.getResponsibleBranch())) {
			headerDetails.setRecordStatus("E");
			headerDetails.setErrDesc(
					ErrorUtil.getErrorDetail(new ErrorDetail("41002", "", new String[] { "Branch", "" }), userLangauge)
							.getError());
			return headerDetails;
		}

		if (headerDetails.getLimitCcy() == null || StringUtils.isEmpty(headerDetails.getLimitCcy())) {
			headerDetails.setRecordStatus("E");
			headerDetails.setErrDesc(ErrorUtil
					.getErrorDetail(new ErrorDetail("41002", "", new String[] { "Currency", "" }), userLangauge)
					.getError());
			return headerDetails;
		}

		if (headerDetails.getLimitStructureCode() == null
				|| StringUtils.isEmpty(headerDetails.getLimitStructureCode())) {
			headerDetails.setRecordStatus("E");
			headerDetails.setErrDesc(
					ErrorUtil.getErrorDetail(new ErrorDetail("41002", "", new String[] { "Limit Structure Code", "" }),
							userLangauge).getError());
			return headerDetails;
		}

		return headerDetails;
	}

	/**
	 * Validation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details from the
	 * tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5) for any
	 * mismatch conditions Fetch the error details from getLimitDetailDAO().getErrorDetail with Error ID and language as
	 * parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader (auditHeader)
	 * @param boolean     onlineRequest
	 * @return auditHeader
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method, boolean onlineRequest) {
		logger.debug(Literal.ENTERING);
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		LimitHeader limitDetail = (LimitHeader) auditDetail.getModelData();

		LimitHeader tempLimitDetail = null;
		if (limitDetail.isWorkflow()) {
			tempLimitDetail = getLimitHeaderDAO().getLimitHeaderById(limitDetail.getId(), "_Temp");
		}
		LimitHeader befLimitDetail = getLimitHeaderDAO().getLimitHeaderById(limitDetail.getId(), "");

		LimitHeader oldLimitDetail = limitDetail.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(limitDetail.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_DetailId") + ":" + valueParm[0];

		if (limitDetail.isNewRecord()) { // for New record or new record into work flow

			// Check the unique keys.
			if (PennantConstants.RECORD_TYPE_NEW.equals(limitDetail.getRecordType())
					&& limitHeaderDAO.isDuplicateKey(limitDetail.getRuleCode(), limitDetail.getLimitStructureCode(),
							limitDetail.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
				String[] parameters = new String[2];

				parameters[0] = PennantJavaUtil.getLabel("label_LimitParmDialog_QueryCode.value") + ":"
						+ limitDetail.getRuleCode();
				parameters[1] = PennantJavaUtil.getLabel("label_StructureCode") + ":"
						+ limitDetail.getLimitStructureCode();

				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41008", parameters, null));
			}

			if (!limitDetail.isWorkflow()) {// With out Work flow only new records
				if (befLimitDetail != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (limitDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befLimitDetail != null || tempLimitDetail != null) { // if records already exists in the main
																				// table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befLimitDetail == null || tempLimitDetail != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!limitDetail.isWorkflow()) { // With out Work flow for update and delete

				if (befLimitDetail == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldLimitDetail != null
							&& !oldLimitDetail.getLastMntOn().equals(befLimitDetail.getLastMntOn())) {
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

				if (tempLimitDetail == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				} else if (oldLimitDetail != null
						&& !oldLimitDetail.getLastMntOn().equals(tempLimitDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !limitDetail.isWorkflow()) {
			auditDetail.setBefImage(befLimitDetail);
		}

		return auditDetail;
	}

	@Override
	public int validationCheck(String lmtGrp, String type) {
		return getLimitDetailDAO().validationCheck(lmtGrp, type);
	}

	@Override
	public int limitItemCheck(String lmtItem, String limitCategory, String type) {
		return getLimitDetailDAO().limitItemCheck(lmtItem, limitCategory, type);
	}

	@Override
	public int limitStructureCheck(String structureCode, String type) {
		return getLimitDetailDAO().limitStructureCheck(structureCode, type);
	}

	@Override
	public List<LimitTransactionDetail> getLimitTranDetails(String code, String ref, long headerId) {
		return getLimitTransactionDetailDAO().getLimitTranDetails(code, ref, headerId);
	}

	/**
	 * Validate limit setup details
	 * 
	 * @param aLimitHeader
	 * @return
	 */
	private List<ErrorDetail> validateLimitSetup(LimitHeader aLimitHeader) {
		logger.debug(Literal.ENTERING);

		String totalGrpCode = "";
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		Map<String, List<String>> groupLineMap = new HashMap<String, List<String>>();
		List<LimitDetails> limitDetails = aLimitHeader.getCustomerLimitDetailsList();

		for (LimitDetails limitDetail : limitDetails) {
			if (!StringUtils.isEmpty(limitDetail.getGroupCode())) {
				if (StringUtils.equals(LimitConstants.LIMIT_ITEM_TOTAL, limitDetail.getGroupCode())) {
					totalGrpCode = String.valueOf(limitDetail.getLimitStructureDetailsID());
					continue;
				}

				if (!groupLineMap.containsKey(limitDetail.getGroupCode())) {
					groupLineMap.put(limitDetail.getGroupCode(), getLinesForGroup(limitDetail.getGroupCode()));
				}
			}
		}

		// validate group and lines
		for (LimitDetails limitDetail : limitDetails) {
			BigDecimal sactioned = limitDetail.getLimitSanctioned();

			if (StringUtils.equals(LimitConstants.LIMIT_ITEM_TOTAL, limitDetail.getGroupCode())) {
				totalGrpCode = String.valueOf(limitDetail.getLimitStructureDetailsID());
				continue;
			}

			if (!StringUtils.isEmpty(limitDetail.getGroupCode())) {
				List<String> validList = groupLineMap.get(limitDetail.getGroupCode());
				// validateChild
				BigDecimal childMaxAmount = getMaxSactionedAmt(validList, limitDetails);

				if (sactioned.compareTo(childMaxAmount) < 0) {
					String message = limitDetail.getGroupName() + "(" + limitDetail.getLimitStructureDetailsID() + ")";
					if (StringUtils.isNotBlank(limitDetail.getLimitLine())) {
						message = limitDetail.getLimitLineDesc() + "(" + limitDetail.getLimitStructureDetailsID() + ")";
					}
					String[] valueParm = new String[1];
					valueParm[0] = message;
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("60315", "", valueParm), "EN"));
				}
			}
		}

		// validate total
		BigDecimal total = BigDecimal.ZERO;
		BigDecimal maxofGroups = BigDecimal.ZERO;
		for (LimitDetails limitDetail : limitDetails) {
			BigDecimal sactioned = limitDetail.getLimitSanctioned();

			if (StringUtils.equals(LimitConstants.LIMIT_ITEM_TOTAL, limitDetail.getGroupCode())) {
				total = sactioned;
				totalGrpCode = String.valueOf(limitDetail.getLimitStructureDetailsID());
				continue;
			}

			Set<String> totalGroups = groupLineMap.keySet();
			if (!StringUtils.isEmpty(limitDetail.getGroupCode()) && totalGroups.contains(limitDetail.getGroupCode())) {
				if (maxofGroups.compareTo(sactioned) <= 0) {
					maxofGroups = sactioned;
				}
			}
			if (StringUtils.equals(limitDetail.getLimitLine(), LimitConstants.LIMIT_ITEM_UNCLSFD)) {
				if (maxofGroups.compareTo(sactioned) <= 0) {
					maxofGroups = sactioned;
				}
			}
		}

		if (total.compareTo(maxofGroups) < 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "Total" + "(" + totalGrpCode + ")";
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("60315", "", valueParm), "EN"));
		}

		logger.debug(" Leaving ");
		return errorDetails;
	}

	private BigDecimal getMaxSactionedAmt(List<String> validList, List<LimitDetails> limitDetails) {
		BigDecimal childMaxAmount = BigDecimal.ZERO;
		for (LimitDetails limitDetail : limitDetails) {
			String code = "";
			if (!StringUtils.isBlank(limitDetail.getGroupCode())) {
				code = limitDetail.getGroupCode();
			}
			if (!StringUtils.isBlank(limitDetail.getLimitLine())) {
				code = limitDetail.getLimitLine();
			}
			if (!validList.contains(code)) {
				continue;
			}

			BigDecimal childAmount = limitDetail.getLimitSanctioned();
			if (childMaxAmount.compareTo(childAmount) <= 0) {
				childMaxAmount = childAmount;
			}
		}
		return childMaxAmount;

	}

	/**
	 * Validate Limit header details
	 * 
	 * @param auditHeader
	 * @return AuditDetail
	 */
	@Override
	public AuditDetail doValidations(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = auditHeader.getAuditDetail();
		LimitHeader limitHeader = (LimitHeader) auditDetail.getModelData();

		// validate limit header id for update
		if (!limitHeader.isNewRecord()) {
			int count = getLimitHeaderCountById(limitHeader.getHeaderId());
			if (count <= 0) {
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(limitHeader.getHeaderId());
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90807", "", valueParm)));
			}
		}

		// validate expiryDate and reviewDate
		if (limitHeader.getLimitRvwDate() != null && limitHeader.getLimitExpiryDate() != null) {
			if (limitHeader.getLimitRvwDate().compareTo(limitHeader.getLimitExpiryDate()) > 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "Review date(" + DateUtility.formatToShortDate(limitHeader.getLimitRvwDate()) + ")";
				valueParm[1] = "Limit expiry date(" + DateUtility.formatToShortDate(limitHeader.getLimitExpiryDate())
						+ ")";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("65029", "", valueParm)));
			}
		}

		// Validate ReviewDate
		Date appDate = SysParamUtil.getAppDate();
		Date nextYear = DateUtility.addYears(appDate, 1);
		if (limitHeader.getLimitRvwDate() != null) {
			if (limitHeader.getLimitRvwDate().before(SysParamUtil.getAppDate())
					|| limitHeader.getLimitRvwDate().after(nextYear)) {
				String[] valueParm = new String[3];
				valueParm[0] = "Review date";
				valueParm[1] = DateUtility.formatToLongDate(SysParamUtil.getAppDate());
				valueParm[2] = DateUtility.formatToLongDate(nextYear);
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm)));
			}
		}

		// Validate ExpiryDate
		if (limitHeader.getLimitExpiryDate() != null) {
			if (limitHeader.getLimitExpiryDate().compareTo(SysParamUtil.getAppDate()) < 0
					|| limitHeader.getLimitExpiryDate().after(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"))) {
				String[] valueParm = new String[3];
				valueParm[0] = "Limit expiry date";
				valueParm[1] = DateUtility.formatToLongDate(SysParamUtil.getAppDate());
				valueParm[2] = DateUtility.formatToLongDate(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"));
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm)));
			}
		}
		// validate Customer and customer group
		String custCIF = limitHeader.getCustCIF();

		// validate customer CIF
		if (StringUtils.isNotBlank(custCIF)) {
			Customer customer = customerDAO.getCustomerByCIF(custCIF, "");
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custCIF;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90101", "", valueParm)));
			} else {
				if (!limitHeader.isNewRecord()) {
					int count = getLimitHeaderDAO().getLimitHeaderAndCustCountById(limitHeader.getHeaderId(),
							customer.getCustID());
					if (count <= 0) {
						String[] valueParm = new String[1];
						valueParm[0] = "Cif :" + custCIF + " And LimitId: " + limitHeader.getHeaderId();
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90266", "", valueParm)));
					}
				}

				if (limitHeader.isNewRecord()) {
					LimitHeader headerDetail = getLimitHeaderDAO().getLimitHeaderByCustomerId(customer.getCustID(),
							"_AView");
					if (headerDetail != null) {
						String[] valueParm = new String[1];
						valueParm[0] = custCIF;
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90806", "", valueParm)));
					}
				}
			}
		}

		// validate customer group code
		String custGrpCode = limitHeader.getCustGrpCode();
		if (StringUtils.isNotBlank(custGrpCode)) {
			CustomerGroup customerGroup = customerGroupService.getCustomerGroupByCode(custGrpCode);
			if (customerGroup == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custGrpCode;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90107", "", valueParm)));
				return auditDetail;
			} else {
				if (!limitHeader.isNewRecord()) {
					int count = getLimitHeaderDAO().getLimitHeaderAndCustGrpCountById(limitHeader.getHeaderId(),
							customerGroup.getCustGrpID());
					if (count <= 0) {
						String[] valueParm = new String[1];
						valueParm[0] = "customerGroup :" + custGrpCode + " And LimitId: " + limitHeader.getHeaderId();
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90266", "", valueParm)));
					}
				}
				if (limitHeader.isNewRecord()) {
					LimitHeader headerDetail = getLimitHeaderDAO()
							.getLimitHeaderByCustomerGroupCode(customerGroup.getCustGrpID(), "_AView");
					if (headerDetail != null) {
						String[] valueParm = new String[1];
						valueParm[0] = custGrpCode;
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90805", "", valueParm)));
					}
				}
			}
		}

		// validate structure code
		String structureCode = limitHeader.getLimitStructureCode();
		int recordCount = getLimitStructureDetailDAO().getLimitStructureCountById(structureCode, "");
		if (recordCount <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = structureCode;
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90801", "", valueParm)));
		}

		// validate currency code
		if (StringUtils.isNotBlank(limitHeader.getLimitCcy())) {
			Currency currency = currencyService.getCurrencyById(limitHeader.getLimitCcy());
			if (currency == null) {
				String[] valueParm = new String[2];
				valueParm[0] = "Limit Currency";
				valueParm[1] = limitHeader.getLimitCcy();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm)));
			}
		}

		List<LimitDetails> limitDetails = limitHeader.getCustomerLimitDetailsList();

		if (CollectionUtils.isEmpty(limitDetails)) {
			auditDetail.setErrorDetail(ErrorUtil.getError("90502", "limitDetails"));
			return auditDetail;
		}

		Date lineMaxExpDate = SysParamUtil.getAppDate();
		Map<Long, Long> structureMap = new HashMap<Long, Long>();

		for (LimitDetails detail : limitDetails) {
			long structureId = detail.getLimitStructureDetailsID();
			if (!structureMap.containsKey(structureId)) {
				structureMap.put(structureId, structureId);
			} else {
				String[] valueParm = new String[1];
				valueParm[0] = limitHeader.getLimitStructureCode();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90811", "", valueParm)));
				return auditDetail;
			}
			// validate structureDetailId from LimitDetails
			int count = getLimitDetailDAO().getLimitDetailByStructureId(structureId, "");
			if (count <= 0) {
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(structureId);
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90803", "", valueParm)));
			}
			if (detail.getExpiryDate() != null) {
				if (detail.getExpiryDate().compareTo(SysParamUtil.getAppDate()) < 0
						|| detail.getExpiryDate().after(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"))) {
					String[] valueParm = new String[3];
					valueParm[0] = "Limit expiry date";
					valueParm[1] = DateUtility.formatToLongDate(SysParamUtil.getAppDate());
					valueParm[2] = DateUtility.formatToLongDate(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"));
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm)));
					return auditDetail;
				}
				if (detail.getExpiryDate() != null && lineMaxExpDate != null) {
					if (detail.getExpiryDate().compareTo(lineMaxExpDate) >= 0) {
						lineMaxExpDate = detail.getExpiryDate();
					}
				}
			}
			if (detail.getLimitSanctioned().compareTo(BigDecimal.ZERO) < 0) {
				String[] valueParm = new String[2];
				valueParm[0] = "limitSanctioned";
				valueParm[1] = "Zero";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90205", "", valueParm)));
				return auditDetail;
			}
			// validate limit check method
			if (!StringUtils.equals(detail.getLimitChkMethod(), LimitConstants.LIMIT_CHECK_ACTUAL)
					&& !StringUtils.equals(detail.getLimitChkMethod(), LimitConstants.LIMIT_CHECK_RESERVED)) {
				String[] valueParm = new String[1];
				valueParm[0] = LimitConstants.LIMIT_CHECK_ACTUAL + "," + LimitConstants.LIMIT_CHECK_RESERVED;
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90809", "", valueParm)));
			}
		}
		if (limitHeader.getLimitExpiryDate() != null) {
			if (limitHeader.getLimitExpiryDate().before(lineMaxExpDate)) {
				String[] valueParm = new String[2];
				valueParm[0] = "Limit expiry date";
				valueParm[1] = DateUtility.formatToLongDate(lineMaxExpDate);
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("91125", "", valueParm)));
			}
		}

		// For Garbage collection
		structureMap = null;

		// validate Limit Structure details
		List<LimitStructureDetail> extgStructDetails = getLimitStructureDetailDAO()
				.getLimitStructureDetailById(limitHeader.getLimitStructureCode(), "_AView");
		if (extgStructDetails != null) {
			for (LimitStructureDetail structDetail : extgStructDetails) {
				boolean limitStrFound = false;
				for (LimitDetails detail : limitDetails) {
					if (structDetail.getLimitStructureDetailsID() == detail.getLimitStructureDetailsID()) {
						limitStrFound = true;
						detail.setGroupCode(structDetail.getGroupCode());
						detail.setGroupName(structDetail.getGroupName());
						detail.setLimitLine(structDetail.getLimitLine());
						detail.setLimitLineDesc(structDetail.getLimitLineDesc());
						continue;
					}
				}

				if (!limitStrFound) {
					String[] valueParm = new String[1];
					valueParm[0] = limitHeader.getLimitStructureCode();
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90811", "", valueParm)));
					return auditDetail;
				}
			}
		}

		// validate groups and line amounts
		List<ErrorDetail> errorDetails = validateLimitSetup(limitHeader);
		for (ErrorDetail error : errorDetails) {
			auditDetail.setErrorDetail(error);
		}

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public LimitHeader getCustomerLimitsById(long headerId) {
		logger.debug(Literal.ENTERING);
		LimitHeader limitHeader;
		limitHeader = getLimitHeaderDAO().getLimitHeaderById(headerId, "");
		logger.debug(Literal.LEAVING);
		return limitHeader;
	}

	/**
	 * 
	 * 
	 */
	@Override
	public LimitHeader getLimitHeaderById(long headerId) {
		logger.debug(Literal.ENTERING);

		LimitHeader limitHeader;
		limitHeader = getLimitHeaderDAO().getLimitHeaderById(headerId, "_AView");
		if (limitHeader != null) {
			limitHeader.setCustomerLimitDetailsList(
					getLimitDetailDAO().getLimitDetailsByHeaderId(limitHeader.getHeaderId(), "_AView"));
		}

		logger.debug(Literal.LEAVING);
		return limitHeader;
	}

	@Override
	public List<String> getLinesForGroup(String groupCode) {
		List<String> listString = new ArrayList<>();
		List<LimitGroupLines> list = getLimitGroupLinesDAO().getAllLimitLinesByGroup(groupCode, "");
		for (LimitGroupLines limitGroupLines : list) {
			if (!StringUtils.isBlank(limitGroupLines.getGroupCode())) {
				listString.add(limitGroupLines.getGroupCode());
			}
			if (!StringUtils.isBlank(limitGroupLines.getLimitLine())) {
				listString.add(limitGroupLines.getLimitLine());
			}
		}
		return listString;
	}

	/**
	 * Method for fetch record count of limitHeader
	 * 
	 * @param headerId
	 * @return Integer
	 */
	@Override
	public int getLimitHeaderCountById(long headerId) {
		logger.debug(Literal.ENTERING);
		logger.debug(Literal.LEAVING);
		return getLimitHeaderDAO().getLimitHeaderCountById(headerId, "");
	}

	@Override
	public int limitLineUtilizationCheck(String lmtline) {
		return getLimitReferenceMappingDAO().isLimitLineExist(lmtline);
	}

	public LimitHeaderDAO getLimitHeaderDAO() {
		return limitHeaderDAO;
	}

	public void setLimitHeaderDAO(LimitHeaderDAO limitHeaderDAO) {
		this.limitHeaderDAO = limitHeaderDAO;
	}

	/**
	 * @return the limitDetailDAO
	 */
	public LimitDetailDAO getLimitDetailDAO() {
		return limitDetailDAO;
	}

	/**
	 * @param limitDetailDAO the limitDetailDAO to set
	 */

	public void setLimitDetailDAO(LimitDetailDAO limitDetailsDAO) {
		this.limitDetailDAO = limitDetailsDAO;
	}

	public LimitTransactionDetailsDAO getLimitTransactionDetailDAO() {
		return limitTransactionDetailDAO;
	}

	public void setLimitTransactionDetailDAO(LimitTransactionDetailsDAO limitTransactionDetailDAO) {
		this.limitTransactionDetailDAO = limitTransactionDetailDAO;
	}

	public LimitManagement getLimitManagement() {
		return limitManagement;
	}

	public void setLimitManagement(LimitManagement limitManagement) {
		this.limitManagement = limitManagement;
	}

	public LimitStructureDetailDAO getLimitStructureDetailDAO() {
		return limitStructureDetailDAO;
	}

	public void setLimitStructureDetailDAO(LimitStructureDetailDAO limitStructureDetailDAO) {
		this.limitStructureDetailDAO = limitStructureDetailDAO;
	}

	public void setCustomerGroupService(CustomerGroupService customerGroupService) {
		this.customerGroupService = customerGroupService;
	}

	public void setCurrencyService(CurrencyService currencyService) {
		this.currencyService = currencyService;
	}

	public LimitReferenceMappingDAO getLimitReferenceMappingDAO() {
		return limitReferenceMappingDAO;
	}

	public void setLimitReferenceMappingDAO(LimitReferenceMappingDAO limitReferenceMappingDAO) {
		this.limitReferenceMappingDAO = limitReferenceMappingDAO;
	}

	public LimitGroupLinesDAO getLimitGroupLinesDAO() {
		return limitGroupLinesDAO;
	}

	public void setLimitGroupLinesDAO(LimitGroupLinesDAO limitGroupLinesDAO) {
		this.limitGroupLinesDAO = limitGroupLinesDAO;
	}

	public LimitRebuild getLimitRebuild() {
		return limitRebuild;
	}

	public void setLimitRebuild(LimitRebuild limitRebuild) {
		this.limitRebuild = limitRebuild;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setCustomerQueuingDAO(CustomerQueuingDAO customerQueuingDAO) {
		this.customerQueuingDAO = customerQueuingDAO;
	}

	public void setCustomerGroupQueuingDAO(CustomerGroupQueuingDAO customerGroupQueuingDAO) {
		this.customerGroupQueuingDAO = customerGroupQueuingDAO;
	}

	public NotificationService getNotificationService() {
		return notificationService;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Autowired
	public void setCustomerDataService(CustomerDataService customerDataService) {
		this.customerDataService = customerDataService;
	}

}
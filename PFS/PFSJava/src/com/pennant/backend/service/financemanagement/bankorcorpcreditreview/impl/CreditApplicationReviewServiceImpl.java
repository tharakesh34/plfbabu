package com.pennant.backend.service.financemanagement.bankorcorpcreditreview.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.financemanagement.bankorcorpcreditreview.CreditApplicationReviewDAO;
import com.pennant.backend.dao.financemanagement.bankorcorpcreditreview.CreditReviewSummaryDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevType;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class CreditApplicationReviewServiceImpl extends GenericService<FinCreditReviewSummary> implements
CreditApplicationReviewService {

	private static Logger logger = Logger.getLogger(CreditApplicationReviewServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CreditApplicationReviewDAO creditApplicationReviewDAO;
	private CreditReviewSummaryDAO creditReviewSummaryDAO;
	private CurrencyDAO currencyDAO;

	private CreditReviewSummaryEntryValidation creditReviewSummaryEntryValidation;


	public void setCreditApplicationReviewDAO(CreditApplicationReviewDAO creditApplicationReviewDAO) {
		this.creditApplicationReviewDAO = creditApplicationReviewDAO;
	}

	public CreditApplicationReviewDAO getCreditApplicationReviewDAO() {
		return creditApplicationReviewDAO;
	}
	public CreditReviewSummaryDAO getCreditReviewSummaryDAO() {
		return creditReviewSummaryDAO;
	}

	public void setCreditReviewSummaryDAO(CreditReviewSummaryDAO creditReviewSummaryDAO) {
		this.creditReviewSummaryDAO = creditReviewSummaryDAO;
	}

	@Override
	public List<FinCreditRevCategory> getCreditRevCategoryByCreditRevCode(String creditRevCode) {

		return this.creditApplicationReviewDAO.getCreditRevCategoryByCreditRevCode(creditRevCode);
	}

	@Override
	public List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByCategoryId(long categoryId) {
		return this.creditApplicationReviewDAO.getFinCreditRevSubCategoryByCategoryId(categoryId);
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}




	@Override
	public FinCreditReviewDetails getCreditReviewDetails() {
		return getCreditApplicationReviewDAO().getCreditReviewDetails();
	}

	@Override
	public FinCreditReviewDetails getNewCreditReviewDetails() {
		return getCreditApplicationReviewDAO().getNewCreditReviewDetails();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * RMTCreditReviewDetails/RMTCreditReviewDetails_Temp by using CreditReviewDetailsDAO's save
	 * method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using CreditReviewDetailsDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtRMTCreditReviewDetails by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		FinCreditReviewDetails creditReviewDetails = (FinCreditReviewDetails) auditHeader.getAuditDetail().getModelData();

		if (creditReviewDetails.isWorkflow()) {
			tableType = "_TEMP";
		}

		if (creditReviewDetails.isNew()) {
			creditReviewDetails.setDetailId(getCreditApplicationReviewDAO().save(creditReviewDetails, tableType));
			auditHeader.getAuditDetail().setModelData(creditReviewDetails);
			auditHeader.setAuditReference(String.valueOf(creditReviewDetails.getDetailId()));
		} else {
			getCreditApplicationReviewDAO().update(creditReviewDetails, tableType);
		}

		if (creditReviewDetails.getCreditReviewSummaryEntries() != null && creditReviewDetails.getCreditReviewSummaryEntries().size() > 0) {
			List<AuditDetail> details = creditReviewDetails.getAuditDetailMap().get("FinCreditReviewSummary");
			details = processCreditReviewSummary(details, creditReviewDetails.getDetailId(), tableType);
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table RMTCreditReviewDetails by using CreditReviewDetailsDAO's delete method with
	 * type as Blank 3) Audit the record in to AuditHeader and
	 * AdtRMTCreditReviewDetails by using auditHeaderDAO.addAudit(auditHeader)
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

		FinCreditReviewDetails creditReviewDetails = (FinCreditReviewDetails) auditHeader.getAuditDetail().getModelData();
		getCreditApplicationReviewDAO().delete(creditReviewDetails, "");

		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(creditReviewDetails, "", auditHeader.getAuditTranType())));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCreditReviewDetailsById fetch the details by using CreditReviewDetailsDAO's
	 * getCreditReviewDetailsById method.
	 * 
	 * @param id
	 *            (int)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinCreditReviewDetails
	 */

	@Override
	public FinCreditReviewDetails getCreditReviewDetailsById(long id) {

		FinCreditReviewDetails creditReviewDetails = getCreditApplicationReviewDAO().getCreditReviewDetailsById(id, "_View");
		creditReviewDetails.setCreditReviewSummaryEntries(getCreditReviewSummaryDAO().getListCreditReviewSummaryById(id,"_View",false));
		return creditReviewDetails;

	}

	/**
	 * getApprovedCreditReviewDetailsById fetch the details by using
	 * CreditReviewDetailsDAO's getCreditReviewDetailsById method . with parameter id and
	 * type as blank. it fetches the approved records from the RMTCreditReviewDetails.
	 * 
	 * @param id
	 *            (int)
	 * @return FinCreditReviewDetails
	 */

	public FinCreditReviewDetails getApprovedCreditReviewDetailsById(long id) {
		FinCreditReviewDetails creditReviewDetails = getCreditApplicationReviewDAO().getCreditReviewDetailsById(id, "_AView");
		creditReviewDetails.setCreditReviewSummaryEntries(getCreditReviewSummaryDAO().getListCreditReviewSummaryById(id,"_AView",false));
		return creditReviewDetails;
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param FinCreditReviewDetails
	 *            (creditReviewDetails)
	 * @return creditReviewDetails
	 */
	@Override
	public FinCreditReviewDetails refresh(FinCreditReviewDetails creditReviewDetails) {
		logger.debug("Entering");
		getCreditApplicationReviewDAO().refresh(creditReviewDetails);
		getCreditApplicationReviewDAO().initialize(creditReviewDetails);
		logger.debug("Leaving");
		return creditReviewDetails;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getCreditApplicationReviewDAO().delete with parameters creditReviewDetails,"" b)
	 * NEW Add new record in to main table by using getCreditApplicationReviewDAO().save
	 * with parameters creditReviewDetails,"" c) EDIT Update record in the main table
	 * by using getCreditApplicationReviewDAO().update with parameters creditReviewDetails,"" 3)
	 * Delete the record from the workFlow table by using
	 * getCreditApplicationReviewDAO().delete with parameters creditReviewDetails,"_Temp" 4)
	 * Audit the record in to AuditHeader and AdtRMTCreditReviewDetails by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in
	 * to AuditHeader and AdtRMTCreditReviewDetails by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinCreditReviewDetails creditReviewDetails = (FinCreditReviewDetails) auditHeader.getAuditDetail().getModelData();

		if (creditReviewDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(listDeletion(creditReviewDetails, "", auditHeader.getAuditTranType()));
			getCreditApplicationReviewDAO().delete(creditReviewDetails, "");
		} else {
			creditReviewDetails.setRoleCode("");
			creditReviewDetails.setNextRoleCode("");
			creditReviewDetails.setTaskId("");
			creditReviewDetails.setNextTaskId("");
			creditReviewDetails.setWorkflowId(0);

			if (creditReviewDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				creditReviewDetails.setRecordType("");
				getCreditApplicationReviewDAO().save(creditReviewDetails, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				creditReviewDetails.setRecordType("");
				getCreditApplicationReviewDAO().update(creditReviewDetails, "");
			}

			if (creditReviewDetails.getCreditReviewSummaryEntries() != null && creditReviewDetails.getCreditReviewSummaryEntries().size() > 0) {
				List<AuditDetail> details = creditReviewDetails.getAuditDetailMap().get("FinCreditReviewSummary");
				details = processCreditReviewSummary(details, creditReviewDetails.getDetailId(), "");
				auditDetails.addAll(details);
			}
		}

		getCreditApplicationReviewDAO().delete(creditReviewDetails, "_TEMP");
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(creditReviewDetails, "_TEMP", auditHeader.getAuditTranType())));
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, creditReviewDetails.getBefImage(), creditReviewDetails));
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(creditReviewDetails);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, creditReviewDetails.getBefImage(), creditReviewDetails));
		auditHeader.setAuditDetails(getListAuditDetails(auditDetails));
		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getCreditApplicationReviewDAO().delete with parameters
	 * creditReviewDetails,"_Temp" 3) Audit the record in to AuditHeader and
	 * AdtRMTCreditReviewDetails by using auditHeaderDAO.addAudit(auditHeader) for
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

		FinCreditReviewDetails creditReviewDetails = (FinCreditReviewDetails) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCreditApplicationReviewDAO().delete(creditReviewDetails, "_TEMP");

		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, creditReviewDetails.getBefImage(), creditReviewDetails));
		auditHeader.setAuditDetails(getListAuditDetails(listDeletion(creditReviewDetails, "_TEMP", auditHeader.getAuditTranType())));

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation. 5) for any mismatch conditions Fetch the error details from
	 * getCreditApplicationReviewDAO().getErrorDetail with Error ID and language as
	 * parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		auditHeader = getAuditDetails(auditHeader, method);

		FinCreditReviewDetails creditReviewDetails = (FinCreditReviewDetails) auditHeader.getAuditDetail().getModelData();
		String usrLanguage = creditReviewDetails.getUserDetails().getUsrLanguage();

		// FeeTier Validation
		if (creditReviewDetails.getCreditReviewSummaryEntries() != null && creditReviewDetails.getCreditReviewSummaryEntries().size() > 0) {
			List<AuditDetail> details = creditReviewDetails.getAuditDetailMap().get("FinCreditReviewSummary");
			details = getCreditReviewSummaryEntryValidation().creditReviewSummaryListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		FinCreditReviewDetails creditReviewDetails = (FinCreditReviewDetails) auditDetail.getModelData();

		FinCreditReviewDetails tempCreditReviewDetails = null;
		if (creditReviewDetails.isWorkflow()) {
			tempCreditReviewDetails = getCreditApplicationReviewDAO().getCreditReviewDetailsById(creditReviewDetails.getDetailId(), "_Temp");
		}
		FinCreditReviewDetails befCreditReviewDetails = getCreditApplicationReviewDAO().getCreditReviewDetailsById(creditReviewDetails.getDetailId(), "");

		FinCreditReviewDetails old_CreditReviewDetails = creditReviewDetails.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(creditReviewDetails.getDetailId());
		errParm[0] = PennantJavaUtil.getLabel("label_DetailId") + ":" + valueParm[0];

		if (creditReviewDetails.isNew()) {
			// for New record or new record into work flow
			if (!creditReviewDetails.isWorkflow()) {
				// With out Work flow only new records
				if (befCreditReviewDetails != null) { 
					// Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (creditReviewDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { 
					// if records type is new
					if (befCreditReviewDetails != null || tempCreditReviewDetails != null) {
						// if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befCreditReviewDetails == null || tempCreditReviewDetails != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!creditReviewDetails.isWorkflow()) { // With out Work flow for update
				// and delete

				if (befCreditReviewDetails == null) { // if records not exists in the
					// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (old_CreditReviewDetails != null && !old_CreditReviewDetails.getLastMntOn().equals(befCreditReviewDetails.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm, valueParm), usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm, valueParm), usrLanguage));
						}
					}
				}
			} else {

				if (tempCreditReviewDetails == null) { // if records not exists in the
					// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (old_CreditReviewDetails != null && !old_CreditReviewDetails.getLastMntOn().equals(tempCreditReviewDetails.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if (StringUtils.trimToEmpty(method).equals("doApprove") || !creditReviewDetails.isWorkflow()) {
			creditReviewDetails.setBefImage(befCreditReviewDetails);
		}

		return auditDetail;
	}

	/**
	 * Common Method for Retrieving AuditDetails List
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinCreditReviewDetails creditReviewDetails = (FinCreditReviewDetails) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if (method.equals("saveOrUpdate") || method.equals("doApprove") || method.equals("doReject")) {
			if (creditReviewDetails.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		if (creditReviewDetails.getCreditReviewSummaryEntries() != null && creditReviewDetails.getCreditReviewSummaryEntries().size() > 0) {
			auditDetailMap.put("FinCreditReviewSummary", setCreditReviewSummaryAuditData(creditReviewDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinCreditReviewSummary"));
		}

		creditReviewDetails.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(creditReviewDetails);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param customerDetails
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setCreditReviewSummaryAuditData(FinCreditReviewDetails creditReviewDetails, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinCreditReviewSummary());

		for (int i = 0; i < creditReviewDetails.getCreditReviewSummaryEntries().size(); i++) {

			FinCreditReviewSummary creditReviewSummaryEntry = creditReviewDetails.getCreditReviewSummaryEntries().get(i);
			creditReviewSummaryEntry.setWorkflowId(creditReviewDetails.getWorkflowId());
			creditReviewSummaryEntry.setDetailId(creditReviewDetails.getDetailId());


			boolean isRcdType = false;
			if(creditReviewSummaryEntry.getRecordType()!=null){
				if (creditReviewSummaryEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					creditReviewSummaryEntry.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					isRcdType = true;
				} else if (creditReviewSummaryEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					creditReviewSummaryEntry.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					isRcdType = true;
				} else if (creditReviewSummaryEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					creditReviewSummaryEntry.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					isRcdType = true;
				}

				if (method.equals("saveOrUpdate") && (isRcdType == true)) {
					creditReviewSummaryEntry.setNewRecord(true);
				}

				if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
					if (creditReviewSummaryEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						auditTranType = PennantConstants.TRAN_ADD;
					} else if (creditReviewSummaryEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
							|| creditReviewSummaryEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						auditTranType = PennantConstants.TRAN_DEL;
					} else {
						auditTranType = PennantConstants.TRAN_UPD;
					}
				}
				creditReviewSummaryEntry.setRecordStatus(creditReviewDetails.getRecordStatus());
				creditReviewSummaryEntry.setUserDetails(creditReviewDetails.getUserDetails());
				creditReviewSummaryEntry.setLastMntOn(creditReviewDetails.getLastMntOn());

				if (!StringUtils.trimToEmpty(creditReviewSummaryEntry.getRecordType()).equals("")) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], creditReviewSummaryEntry.getBefImage(), creditReviewSummaryEntry));
				}
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Customer Ratings
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	private List<AuditDetail> processCreditReviewSummary(List<AuditDetail> auditDetails, long detailId, String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			FinCreditReviewSummary creditReviewSummaryEntry = (FinCreditReviewSummary) auditDetails.get(i).getModelData();
			creditReviewSummaryEntry.setDetailId(detailId);
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (type.equals("")) {
				approveRec = true;
				creditReviewSummaryEntry.setRoleCode("");
				creditReviewSummaryEntry.setNextRoleCode("");
				creditReviewSummaryEntry.setTaskId("");
				creditReviewSummaryEntry.setNextTaskId("");
			}

			creditReviewSummaryEntry.setWorkflowId(0);

			if (creditReviewSummaryEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (creditReviewSummaryEntry.isNewRecord()) {
				saveRecord = true;
				if (creditReviewSummaryEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					creditReviewSummaryEntry.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (creditReviewSummaryEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					creditReviewSummaryEntry.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (creditReviewSummaryEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					creditReviewSummaryEntry.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (creditReviewSummaryEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (creditReviewSummaryEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
					updateRecord = true;
			} else if (creditReviewSummaryEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (creditReviewSummaryEntry.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = creditReviewSummaryEntry.getRecordType();
				recordStatus = creditReviewSummaryEntry.getRecordStatus();
				creditReviewSummaryEntry.setRecordType("");
				creditReviewSummaryEntry.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				creditReviewSummaryDAO.save(creditReviewSummaryEntry, type);
			}

			if (updateRecord) {
				creditReviewSummaryDAO.update(creditReviewSummaryEntry, type);
			}

			if (deleteRecord) {
				creditReviewSummaryDAO.delete(creditReviewSummaryEntry, type);
			}

			if (approveRec) {
				creditReviewSummaryEntry.setRecordType(rcdType);
				creditReviewSummaryEntry.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(creditReviewSummaryEntry);
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	/**
	 * Method deletion of creditReviewSummary list with existing fee type
	 * 
	 * @param creditReviewDetails
	 * @param tableType
	 */
	public List<AuditDetail> listDeletion(FinCreditReviewDetails creditReviewDetails, String tableType, String auditTranType) {

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		if (creditReviewDetails.getCreditReviewSummaryEntries() != null && creditReviewDetails.getCreditReviewSummaryEntries().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinCreditReviewSummary());
			for (int i = 0; i < creditReviewDetails.getCreditReviewSummaryEntries().size(); i++) {
				FinCreditReviewSummary creditReviewSummary = creditReviewDetails.getCreditReviewSummaryEntries().get(i);
				if (!creditReviewSummary.getRecordType().equals("") || tableType.equals("")) {
					auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], creditReviewSummary.getBefImage(), creditReviewSummary));
				}
			}
			getCreditReviewSummaryDAO().deleteByDetailId(creditReviewDetails.getDetailId(), tableType);
		}
		return auditList;
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

		if (list != null & list.size() > 0) {

			for (int i = 0; i < list.size(); i++) {

				String transType = "";
				String rcdType = "";
				FinCreditReviewSummary creditReviewSummaryEntry = (FinCreditReviewSummary) ((AuditDetail) list.get(i)).getModelData();

				rcdType = creditReviewSummaryEntry.getRecordType();

				if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					transType = PennantConstants.TRAN_ADD;
				} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL) || rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					transType = PennantConstants.TRAN_DEL;
				} else {
					transType = PennantConstants.TRAN_UPD;
				}

				if (!(transType.equals(""))) {
					// check and change below line for Complete code
					auditDetailsList.add(new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(), creditReviewSummaryEntry.getBefImage(), creditReviewSummaryEntry));
				}
			}
		}
		logger.debug("Leaving");
		return auditDetailsList;
	}

	public void setCreditReviewSummaryEntryValidation(CreditReviewSummaryEntryValidation creditReviewSummaryEntryValidation) {
		this.creditReviewSummaryEntryValidation = creditReviewSummaryEntryValidation;
	}

	public CreditReviewSummaryEntryValidation getCreditReviewSummaryEntryValidation() {
		return creditReviewSummaryEntryValidation;
	}

	@Override
	public List<FinCreditReviewSummary> getListCreditReviewSummaryById(long id, String type,
			boolean postingProcess) {
		return creditReviewSummaryDAO.getListCreditReviewSummaryById(id, type, postingProcess);
	}

	public FinCreditRevType getFinCreditRevByRevCode(String creditRevCode){
		return creditApplicationReviewDAO.getFinCreditRevByRevCode(creditRevCode);
	}

	public void setCurrencyDAO(CurrencyDAO currencyDAO) {
		this.currencyDAO = currencyDAO;
	}

	public CurrencyDAO getCurrencyDAO() {
		return currencyDAO;
	}

	@Override
	public Currency getCurrencyById(String id) {
		return currencyDAO.getCurrencyById(id,"");
	}

	@Override
	public Map<String, List<FinCreditReviewSummary>> getListCreditReviewSummaryByCustId(long id,
			int noOfYears,int year) {
		logger.debug("Entering");
		Map<String, List<FinCreditReviewSummary>> map = new LinkedHashMap<String, List<FinCreditReviewSummary>>();
		//Calendar calender = Calendar.getInstance();
		//int year =DateUtility.getYear(calender.getTime());
		//int year =DateUtility.getYear(calender.getTime());
		
		for(int i=noOfYears;i>=1;i--){
			map.put(String.valueOf(year-i), this.creditReviewSummaryDAO.getListCreditReviewSummaryByYearAndCustId(id, String.valueOf(year-i)));
		}
		logger.debug("Leaving");
		return map;
	}

	@Override
    public int isCreditSummaryExists(long custID, String auditYear) {
	    return this.creditApplicationReviewDAO.isCreditSummaryExists(custID, auditYear);
    }
	
	@Override
    public List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByCategoryIdAndCalcSeq(long categoryId) {
	    return this.creditApplicationReviewDAO.getFinCreditRevSubCategoryByCategoryIdAndCalcSeq(categoryId);
    }
	
	
}

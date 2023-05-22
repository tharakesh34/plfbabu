package com.pennant.backend.service.financemanagement.bankorcorpcreditreview.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.NotesDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.customermasters.FinCreditRevSubCategoryDAO;
import com.pennant.backend.dao.finance.CreditReviewDetailDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.financemanagement.bankorcorpcreditreview.CreditApplicationReviewDAO;
import com.pennant.backend.dao.financemanagement.bankorcorpcreditreview.CreditReviewSummaryDAO;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.finance.CreditReviewData;
import com.pennant.backend.model.finance.CreditReviewDetails;
import com.pennant.backend.model.finance.ExtBreDetails;
import com.pennant.backend.model.finance.ExtCreditReviewConfig;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevType;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.FinCreditRevSubCategoryService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class CreditApplicationReviewServiceImpl extends GenericService<FinCreditReviewSummary>
		implements CreditApplicationReviewService {
	private static Logger logger = LogManager.getLogger(CreditApplicationReviewServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private CreditApplicationReviewDAO creditApplicationReviewDAO;
	private CreditReviewSummaryDAO creditReviewSummaryDAO;
	private FinCreditRevSubCategoryService finCreditRevSubCategoryService;
	private FinCreditRevSubCategoryDAO finCreditRevSubCategoryDAO;
	private CustomerDocumentDAO customerDocumentDAO;
	private NotesDAO notesDAO;
	private CreditReviewSummaryEntryValidation creditReviewSummaryEntryValidation;
	private CreditReviewDetailDAO creditReviewDetailDAO;
	private FinanceMainDAO financeMainDAO;

	List<CustomerDocument> docsList;
	private String excludeFields = "auditYear,remarks,creditRevCode, ";

	public CreditApplicationReviewServiceImpl() {
		super();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * RMTCreditReviewDetails/RMTCreditReviewDetails_Temp by using CreditReviewDetailsDAO's save method b) Update the
	 * Record in the table. based on the module workFlow Configuration. by using CreditReviewDetailsDAO's update method
	 * 3) Audit the record in to AuditHeader and AdtRMTCreditReviewDetails by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
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
		FinCreditReviewDetails creditReviewDetails = (FinCreditReviewDetails) auditHeader.getAuditDetail()
				.getModelData();

		if (creditReviewDetails.isWorkflow()) {
			tableType = "_Temp";
		}

		if (creditReviewDetails.isNewRecord()) {
			creditReviewDetails.setDetailId(getCreditApplicationReviewDAO().save(creditReviewDetails, tableType));
			auditHeader.getAuditDetail().setModelData(creditReviewDetails);
			auditHeader.setAuditReference(String.valueOf(creditReviewDetails.getDetailId()));
		} else {
			getCreditApplicationReviewDAO().update(creditReviewDetails, tableType);
		}

		if (creditReviewDetails.getLovDescFinCreditRevSubCategory() != null
				&& creditReviewDetails.getLovDescFinCreditRevSubCategory().size() > 0) {
			List<AuditDetail> details = creditReviewDetails.getAuditDetailMap().get("FinCreditReviewSubCategory");
			details = processCreditReviewSubCategory(details, creditReviewDetails.getDetailId(), "");
			if (auditDetails.addAll(details)) {
				finCreditRevSubCategoryDAO.updateSubCategories(creditReviewDetails.getLovDescFinCreditRevSubCategory());
			}
		}
		if (creditReviewDetails.getLovDescCreditReviewSummaryEntries() != null
				&& creditReviewDetails.getLovDescCreditReviewSummaryEntries().size() > 0) {
			List<AuditDetail> details = creditReviewDetails.getAuditDetailMap().get("FinCreditReviewSummaryEntries");
			details = processCreditReviewSummaryEntries(details, creditReviewDetails.getDetailId(), tableType,
					creditReviewDetails);
			auditDetails.addAll(details);
		}

		if (creditReviewDetails.getCustomerDocumentList() != null
				&& creditReviewDetails.getCustomerDocumentList().size() > 0) {
			List<AuditDetail> details = creditReviewDetails.getAuditDetailMap().get("CustomerDocuments");
			details = processCustomerDocuments(details, creditReviewDetails.getDetailId(), "");
			auditDetails.addAll(details);
		}

		if (creditReviewDetails.getNotesList() != null) {
			saveNotes(creditReviewDetails.getNotesList(), creditReviewDetails);
		}
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, creditReviewDetails.getBefImage(),
				creditReviewDetails));

		auditHeader.setAuditDetails(auditDetails);
		// auditHeaderDAO.addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;

	}

	public void saveNotes(List<Notes> notesList, FinCreditReviewDetails creditReviewDetails) {
		for (Notes notes : notesList) {
			notes.setReference(String.valueOf(creditReviewDetails.getDetailId()));
			notesDAO.save(notes);
		}
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * RMTCreditReviewDetails by using CreditReviewDetailsDAO's delete method with type as Blank 3) Audit the record in
	 * to AuditHeader and AdtRMTCreditReviewDetails by using auditHeaderDAO.addAudit(auditHeader)
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

		FinCreditReviewDetails creditReviewDetails = (FinCreditReviewDetails) auditHeader.getAuditDetail()
				.getModelData();
		getCreditApplicationReviewDAO().delete(creditReviewDetails, "");

		for (Notes notes : creditReviewDetails.getNotesList()) {
			notesDAO.deleteAllNotes(notes);
		}

		auditHeader.setAuditDetails(
				getListAuditDetails(listDeletion(creditReviewDetails, "", auditHeader.getAuditTranType())));
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getCreditReviewDetailsById fetch the details by using CreditReviewDetailsDAO's getCreditReviewDetailsById method.
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return FinCreditReviewDetails
	 */

	@Override
	public FinCreditReviewDetails getCreditReviewDetailsById(long id) {

		FinCreditReviewDetails creditReviewDetails = getCreditApplicationReviewDAO().getCreditReviewDetailsById(id,
				"_View");
		creditReviewDetails.setCreditReviewSummaryEntries(
				getCreditReviewSummaryDAO().getListCreditReviewSummaryById(id, "_View", false));
		return creditReviewDetails;

	}

	/**
	 * getApprovedCreditReviewDetailsById fetch the details by using CreditReviewDetailsDAO's getCreditReviewDetailsById
	 * method . with parameter id and type as blank. it fetches the approved records from the RMTCreditReviewDetails.
	 * 
	 * @param id (int)
	 * @return FinCreditReviewDetails
	 */

	public FinCreditReviewDetails getApprovedCreditReviewDetailsById(long id) {
		FinCreditReviewDetails creditReviewDetails = getCreditApplicationReviewDAO().getCreditReviewDetailsById(id,
				"_AView");
		creditReviewDetails.setCreditReviewSummaryEntries(
				getCreditReviewSummaryDAO().getListCreditReviewSummaryById(id, "_AView", false));
		return creditReviewDetails;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getCreditApplicationReviewDAO().delete
	 * with parameters creditReviewDetails,"" b) NEW Add new record in to main table by using
	 * getCreditApplicationReviewDAO().save with parameters creditReviewDetails,"" c) EDIT Update record in the main
	 * table by using getCreditApplicationReviewDAO().update with parameters creditReviewDetails,"" 3) Delete the record
	 * from the workFlow table by using getCreditApplicationReviewDAO().delete with parameters
	 * creditReviewDetails,"_Temp" 4) Audit the record in to AuditHeader and AdtRMTCreditReviewDetails by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and
	 * AdtRMTCreditReviewDetails by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
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

		FinCreditReviewDetails creditReviewDetails = (FinCreditReviewDetails) auditHeader.getAuditDetail()
				.getModelData();

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

			if (creditReviewDetails.getLovDescFinCreditRevSubCategory() != null
					&& creditReviewDetails.getLovDescFinCreditRevSubCategory().size() > 0) {
				List<AuditDetail> details = creditReviewDetails.getAuditDetailMap().get("FinCreditReviewSubCategory");
				details = processCreditReviewSubCategory(details, creditReviewDetails.getDetailId(), "");
				auditDetails.addAll(details);
			}

			if (creditReviewDetails.getLovDescCreditReviewSummaryEntries() != null
					&& creditReviewDetails.getLovDescCreditReviewSummaryEntries().size() > 0) {
				List<AuditDetail> details = creditReviewDetails.getAuditDetailMap()
						.get("FinCreditReviewSummaryEntries");
				details = processCreditReviewSummaryEntries(details, creditReviewDetails.getDetailId(), "",
						creditReviewDetails);
				auditDetails.addAll(details);
			}

			if (creditReviewDetails.getCustomerDocumentList() != null
					&& creditReviewDetails.getCustomerDocumentList().size() > 0) {
				List<AuditDetail> details = creditReviewDetails.getAuditDetailMap().get("CustomerDocuments");
				details = processCustomerDocuments(details, creditReviewDetails.getDetailId(), "");
				auditDetails.addAll(details);
			}
		}
		if (!creditReviewDetails.isNewRecord()) {
			getCreditApplicationReviewDAO().delete(creditReviewDetails, "_Temp");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			auditHeader.setAuditDetails(
					getListAuditDetails(listDeletion(creditReviewDetails, "_Temp", auditHeader.getAuditTranType())));
			auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1,
					creditReviewDetails.getBefImage(), creditReviewDetails));
		}
		// auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(creditReviewDetails);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, creditReviewDetails.getBefImage(),
				creditReviewDetails));
		auditHeader.setAuditDetails(getListAuditDetails(auditDetails));
		// auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getCreditApplicationReviewDAO().delete with parameters creditReviewDetails,"_Temp" 3)
	 * Audit the record in to AuditHeader and AdtRMTCreditReviewDetails by using auditHeaderDAO.addAudit(auditHeader)
	 * for Work flow
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

		FinCreditReviewDetails creditReviewDetails = (FinCreditReviewDetails) auditHeader.getAuditDetail()
				.getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getCreditApplicationReviewDAO().delete(creditReviewDetails, "_Temp");
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, creditReviewDetails.getBefImage(),
				creditReviewDetails));
		auditHeader.setAuditDetails(
				getListAuditDetails(listDeletion(creditReviewDetails, "_Temp", auditHeader.getAuditTranType())));

		// auditHeaderDAO.addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from getCreditApplicationReviewDAO().getErrorDetail with
	 * Error ID and language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		auditHeader = getAuditDetails(auditHeader, method);

		FinCreditReviewDetails creditReviewDetails = (FinCreditReviewDetails) auditHeader.getAuditDetail()
				.getModelData();
		String usrLanguage = creditReviewDetails.getUserDetails().getLanguage();

		// FeeTier Validation
		if (creditReviewDetails.getLovDescFinCreditRevSubCategory() != null
				&& creditReviewDetails.getLovDescFinCreditRevSubCategory().size() > 0) {
			List<AuditDetail> details = creditReviewDetails.getAuditDetailMap().get("FinCreditReviewSubCategory");
			details = finCreditRevSubCategoryService.finCreditRevSubCategoryListValidation(details, method,
					usrLanguage);
			auditDetails.addAll(details);
		}

		if (creditReviewDetails.getLovDescCreditReviewSummaryEntries() != null
				&& creditReviewDetails.getLovDescCreditReviewSummaryEntries().size() > 0) {
			List<AuditDetail> details = creditReviewDetails.getAuditDetailMap().get("FinCreditReviewSummaryEntries");
			auditDetails.addAll(details);
		}

		if (creditReviewDetails.getCustomerDocumentList() != null
				&& creditReviewDetails.getCustomerDocumentList().size() > 0) {
			List<AuditDetail> details = creditReviewDetails.getAuditDetailMap().get("CustomerDocuments");
			details = documentListValidation(details, method, usrLanguage);
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	public List<AuditDetail> documentListValidation(List<AuditDetail> auditDetails, String method, String usrLanguage) {

		if (auditDetails != null && auditDetails.size() > 0) {
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail = validateDocs(auditDetails.get(i), method, usrLanguage);
				details.add(auditDetail);
			}
			return details;
		}
		return new ArrayList<AuditDetail>();
	}

	private AuditDetail validateDocs(AuditDetail auditDetail, String method, String usrLanguage) {
		CustomerDocument customerDocument = (CustomerDocument) auditDetail.getModelData();
		CustomerDocument tempCustomerDocument = null;
		if (customerDocument.isWorkflow()) {
			tempCustomerDocument = customerDocumentDAO.getCustomerDocumentById(customerDocument.getCustID(),
					customerDocument.getCustDocCategory(), "");
		}

		CustomerDocument befCustomerDocument = customerDocumentDAO.getCustomerDocumentById(customerDocument.getCustID(),
				customerDocument.getCustDocCategory(), "");
		CustomerDocument oldCustomerDocument = customerDocument.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(customerDocument.getCustID());
		valueParm[1] = customerDocument.getCustDocCategory();

		errParm[0] = PennantJavaUtil.getLabel("label_CustID") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CustDocType") + ":" + valueParm[1];

		if (customerDocument.isNewRecord()) { // for New record or new record into
			// work flow

			if (!customerDocument.isWorkflow()) {// With out Work flow only new
				// records
				if (befCustomerDocument != null) { // Record Already Exists in
					// the table then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow

				if (customerDocument.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type
					// is new
					if (befCustomerDocument != null || tempCustomerDocument != null) {
						// if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befCustomerDocument == null || tempCustomerDocument != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!customerDocument.isWorkflow()) { // With out Work flow for
				// update and delete

				if (befCustomerDocument == null) { // if records not exists in
					// the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {
					if (oldCustomerDocument != null
							&& !oldCustomerDocument.getLastMntOn().equals(befCustomerDocument.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, null));
						} else {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, null));
						}
					}
				}
			} else {

				if (tempCustomerDocument == null) { // if records not exists in
					// the Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempCustomerDocument != null && oldCustomerDocument != null
						&& !oldCustomerDocument.getLastMntOn().equals(tempCustomerDocument.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !customerDocument.isWorkflow()) {
			customerDocument.setBefImage(befCustomerDocument);
		}
		return auditDetail;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinCreditReviewDetails creditReviewDetails = (FinCreditReviewDetails) auditDetail.getModelData();

		FinCreditReviewDetails tempCreditReviewDetails = null;
		if (creditReviewDetails.isWorkflow()) {
			tempCreditReviewDetails = getCreditApplicationReviewDAO()
					.getCreditReviewDetailsById(creditReviewDetails.getDetailId(), "_Temp");
		}
		FinCreditReviewDetails befCreditReviewDetails = getCreditApplicationReviewDAO()
				.getCreditReviewDetailsById(creditReviewDetails.getDetailId(), "");
		FinCreditReviewDetails oldCreditReviewDetails = creditReviewDetails.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(creditReviewDetails.getDetailId());
		errParm[0] = PennantJavaUtil.getLabel("label_DetailId") + ":" + valueParm[0];

		if (creditReviewDetails.isNewRecord()) {
			// for New record or new record into work flow
			if (!creditReviewDetails.isWorkflow()) {
				// With out Work flow only new records
				if (befCreditReviewDetails != null) {
					// Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (creditReviewDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					// if records type is new
					if (befCreditReviewDetails != null || tempCreditReviewDetails != null) {
						// if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befCreditReviewDetails == null || tempCreditReviewDetails != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
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
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldCreditReviewDetails != null
							&& !oldCreditReviewDetails.getLastMntOn().equals(befCreditReviewDetails.getLastMntOn())) {
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

				if (tempCreditReviewDetails == null) { // if records not exists in the
					// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (oldCreditReviewDetails != null
						&& !oldCreditReviewDetails.getLastMntOn().equals(tempCreditReviewDetails.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !creditReviewDetails.isWorkflow()) {
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
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinCreditReviewDetails creditReviewDetails = (FinCreditReviewDetails) auditHeader.getAuditDetail()
				.getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (creditReviewDetails.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		if (creditReviewDetails.getCreditReviewSummaryEntries() != null
				&& creditReviewDetails.getCreditReviewSummaryEntries().size() > 0) {
			auditDetailMap.put("FinCreditReviewSubCategory",
					setCreditReviewSubCategoryAuditData(creditReviewDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinCreditReviewSubCategory"));
		}

		if (creditReviewDetails.getCreditReviewSummaryEntries() != null
				&& creditReviewDetails.getCreditReviewSummaryEntries().size() > 0) {
			auditDetailMap.put("FinCreditReviewSummary",
					setCreditReviewSummaryAuditData(creditReviewDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinCreditReviewSummary"));
		}

		if (creditReviewDetails.getCreditReviewSummaryEntries() != null
				&& creditReviewDetails.getCreditReviewSummaryEntries().size() > 0) {
			auditDetailMap.put("FinCreditReviewSummaryEntries",
					setCreditReviewSummaryEntriesAuditData(creditReviewDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinCreditReviewSummaryEntries"));
		}

		if (creditReviewDetails.getCustomerDocumentList() != null
				&& creditReviewDetails.getCustomerDocumentList().size() > 0) {
			auditDetailMap.put("CustomerDocuments", setCustomerAuditData(creditReviewDetails, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CustomerDocuments"));
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
	private List<AuditDetail> setCreditReviewSummaryAuditData(FinCreditReviewDetails creditReviewDetails,
			String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinCreditReviewSummary(), excludeFields);

		for (int i = 0; i < creditReviewDetails.getCreditReviewSummaryEntries().size(); i++) {

			FinCreditReviewSummary creditReviewSummaryEntry = creditReviewDetails.getCreditReviewSummaryEntries()
					.get(i);
			// creditReviewSummaryEntry.setWorkflowId(creditReviewDetails.getWorkflowId());
			creditReviewSummaryEntry.setDetailId(creditReviewDetails.getDetailId());

			boolean isRcdType = true;
			if (creditReviewSummaryEntry.getRecordType() != null) {
				if (creditReviewSummaryEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					creditReviewSummaryEntry.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					isRcdType = true;
				} else if (creditReviewSummaryEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)
						&& !creditReviewDetails.isNewRecord()) {
					creditReviewSummaryEntry.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					creditReviewSummaryEntry.setNewRecord(false);
					isRcdType = false;
				} else if (creditReviewSummaryEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					creditReviewSummaryEntry.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					// isRcdType = true;
				}

				if ("saveOrUpdate".equals(method) && isRcdType) {
					creditReviewSummaryEntry.setNewRecord(true);
				}

				if (creditReviewSummaryEntry.getRecordType().isEmpty()) {
					creditReviewSummaryEntry.setWorkflowId(0);
				}

				if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
					if (creditReviewSummaryEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						auditTranType = PennantConstants.TRAN_ADD;
					} else if (creditReviewSummaryEntry.getRecordType()
							.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
							|| creditReviewSummaryEntry.getRecordType()
									.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						auditTranType = PennantConstants.TRAN_DEL;
					} else {
						auditTranType = PennantConstants.TRAN_UPD;
					}
				}
				creditReviewSummaryEntry.setRecordStatus(creditReviewDetails.getRecordStatus());
				creditReviewSummaryEntry.setUserDetails(creditReviewDetails.getUserDetails());
				creditReviewSummaryEntry.setLastMntOn(creditReviewDetails.getLastMntOn());

				if (StringUtils.isNotBlank(creditReviewSummaryEntry.getRecordType())) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							creditReviewSummaryEntry.getBefImage(), creditReviewSummaryEntry));
				}
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param customerDetails
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setCreditReviewSummaryEntriesAuditData(FinCreditReviewDetails creditReviewDetails,
			String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinCreditReviewSummary(), excludeFields);

		for (int i = 0; i < creditReviewDetails.getCreditReviewSummaryEntries().size(); i++) {

			FinCreditReviewSummary creditReviewSummaryEntry = creditReviewDetails.getCreditReviewSummaryEntries()
					.get(i);
			// creditReviewSummaryEntry.setWorkflowId(creditReviewDetails.getWorkflowId());
			creditReviewSummaryEntry.setDetailId(creditReviewDetails.getDetailId());

			boolean isRcdType = false;
			if (creditReviewSummaryEntry.getRecordType() != null) {
				if (creditReviewSummaryEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					creditReviewSummaryEntry.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					isRcdType = true;
				} else if (creditReviewSummaryEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					creditReviewSummaryEntry.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					creditReviewSummaryEntry.setNewRecord(false);
					isRcdType = false;
				} else if (creditReviewSummaryEntry.getRecordType()
						.equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
					creditReviewSummaryEntry.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					isRcdType = true;
				} else if (creditReviewSummaryEntry.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					creditReviewSummaryEntry.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					// isRcdType = true;
				}

				if ("saveOrUpdate".equals(method) && isRcdType) {
					creditReviewSummaryEntry.setNewRecord(true);
				}

				if (creditReviewSummaryEntry.getRecordType().isEmpty()) {
					creditReviewSummaryEntry.setWorkflowId(0);
				}

				if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
					if (creditReviewSummaryEntry.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						auditTranType = PennantConstants.TRAN_ADD;
					} else if (creditReviewSummaryEntry.getRecordType()
							.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
							|| creditReviewSummaryEntry.getRecordType()
									.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						auditTranType = PennantConstants.TRAN_DEL;
					} else {
						auditTranType = PennantConstants.TRAN_UPD;
					}
				}
				creditReviewSummaryEntry.setRecordStatus(creditReviewDetails.getRecordStatus());
				creditReviewSummaryEntry.setUserDetails(creditReviewDetails.getUserDetails());
				creditReviewSummaryEntry.setLastMntOn(creditReviewDetails.getLastMntOn());

				if (StringUtils.isNotBlank(creditReviewSummaryEntry.getRecordType())) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							creditReviewSummaryEntry.getBefImage(), creditReviewSummaryEntry));
				}
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param customerDetails
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setCustomerAuditData(FinCreditReviewDetails creditReviewDetails, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new CustomerDocument(), excludeFields);

		for (int i = 0; i < creditReviewDetails.getCustomerDocumentList().size(); i++) {

			CustomerDocument customerDocument = creditReviewDetails.getCustomerDocumentList().get(i);
			customerDocument.setWorkflowId(creditReviewDetails.getWorkflowId());
			// finCreditRevSubCategory.setDetailId(creditReviewDetails.getDetailId());

			boolean isRcdType = false;
			if (customerDocument.getRecordType() != null) {
				if (customerDocument.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					customerDocument.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					isRcdType = true;
				} else if (customerDocument.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					customerDocument.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					isRcdType = true;
				} else if (customerDocument.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					customerDocument.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					// isRcdType = true;
				}

				if ("saveOrUpdate".equals(method) && isRcdType) {
					customerDocument.setNewRecord(true);
				}

				if (customerDocument.getRecordType().isEmpty()) {
					customerDocument.setWorkflowId(0);
				}

				if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
					if (customerDocument.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						auditTranType = PennantConstants.TRAN_ADD;
					} else if (customerDocument.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
							|| customerDocument.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						auditTranType = PennantConstants.TRAN_DEL;
					} else {
						auditTranType = PennantConstants.TRAN_UPD;
					}
				}
				customerDocument.setRecordStatus(creditReviewDetails.getRecordStatus());
				customerDocument.setUserDetails(creditReviewDetails.getUserDetails());
				customerDocument.setLastMntOn(creditReviewDetails.getLastMntOn());

				if (StringUtils.isNotBlank(customerDocument.getRecordType())) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							customerDocument.getBefImage(), customerDocument));
				}
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param customerDetails
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setCreditReviewSubCategoryAuditData(FinCreditReviewDetails creditReviewDetails,
			String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinCreditRevSubCategory(), excludeFields);

		for (int i = 0; i < creditReviewDetails.getLovDescFinCreditRevSubCategory().size(); i++) {

			FinCreditRevSubCategory finCreditRevSubCategory = creditReviewDetails.getLovDescFinCreditRevSubCategory()
					.get(i);
			finCreditRevSubCategory.setWorkflowId(creditReviewDetails.getWorkflowId());
			// finCreditRevSubCategory.setDetailId(creditReviewDetails.getDetailId());

			boolean isRcdType = false;
			if (finCreditRevSubCategory.getRecordType() != null) {
				if (finCreditRevSubCategory.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finCreditRevSubCategory.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					isRcdType = true;
				} else if (finCreditRevSubCategory.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finCreditRevSubCategory.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					isRcdType = true;
				} else if (finCreditRevSubCategory.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finCreditRevSubCategory.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					// isRcdType = true;
				}

				if ("saveOrUpdate".equals(method) && isRcdType) {
					finCreditRevSubCategory.setNewRecord(true);
				}

				if (finCreditRevSubCategory.getRecordType().isEmpty()) {
					finCreditRevSubCategory.setWorkflowId(0);
				}

				if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
					if (finCreditRevSubCategory.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						auditTranType = PennantConstants.TRAN_ADD;
					} else if (finCreditRevSubCategory.getRecordType()
							.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
							|| finCreditRevSubCategory.getRecordType()
									.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						auditTranType = PennantConstants.TRAN_DEL;
					} else {
						auditTranType = PennantConstants.TRAN_UPD;
					}
				}
				finCreditRevSubCategory.setRecordStatus(creditReviewDetails.getRecordStatus());
				finCreditRevSubCategory.setUserDetails(creditReviewDetails.getUserDetails());
				finCreditRevSubCategory.setLastMntOn(creditReviewDetails.getLastMntOn());

				if (StringUtils.isNotBlank(finCreditRevSubCategory.getRecordType())) {
					auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							finCreditRevSubCategory.getBefImage(), finCreditRevSubCategory));
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
	private List<AuditDetail> processCreditReviewSubCategory(List<AuditDetail> auditDetails, long detailId,
			String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			FinCreditRevSubCategory creditReviewSubCategory = (FinCreditRevSubCategory) auditDetails.get(i)
					.getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				creditReviewSubCategory.setRoleCode("");
				creditReviewSubCategory.setNextRoleCode("");
				creditReviewSubCategory.setTaskId("");
				creditReviewSubCategory.setNextTaskId("");
			}

			if (creditReviewSubCategory.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (creditReviewSubCategory.isNewRecord()) {
				if (approveRec) {
					if (creditReviewSubCategory.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						saveRecord = true;
					} else if (creditReviewSubCategory.getRecordType()
							.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
						deleteRecord = true;
					} else if (creditReviewSubCategory.getRecordType()
							.equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
						updateRecord = true;
					}
				} else {
					saveRecord = true;
					if (creditReviewSubCategory.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						creditReviewSubCategory.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (creditReviewSubCategory.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
						creditReviewSubCategory.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (creditReviewSubCategory.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
						creditReviewSubCategory.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}
				}
			} else if (creditReviewSubCategory.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (creditReviewSubCategory.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (creditReviewSubCategory.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (creditReviewSubCategory.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			creditReviewSubCategory.setWorkflowId(0);

			if (approveRec) {
				rcdType = creditReviewSubCategory.getRecordType();
				recordStatus = creditReviewSubCategory.getRecordStatus();
				creditReviewSubCategory.setRecordType("");
				creditReviewSubCategory.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				finCreditRevSubCategoryDAO.save(creditReviewSubCategory, type);
			}

			if (updateRecord) {
				finCreditRevSubCategoryDAO.update(creditReviewSubCategory, type);
			}

			if (deleteRecord) {
				finCreditRevSubCategoryDAO.delete(creditReviewSubCategory, type);
			}

			if (approveRec) {
				creditReviewSubCategory.setRecordType(rcdType);
				creditReviewSubCategory.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(creditReviewSubCategory);
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
	private List<AuditDetail> processCreditReviewSummaryEntries(List<AuditDetail> auditDetails, long detailId,
			String type, FinCreditReviewDetails creditReviewDetails) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		// FinCreditReviewDetails credReviewDetails=null;
		for (int i = 0; i < auditDetails.size(); i++) {

			FinCreditReviewSummary summary = (FinCreditReviewSummary) auditDetails.get(i).getModelData();
			summary.setDetailId(detailId);
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				summary.setRoleCode("");
				summary.setNextRoleCode("");
				summary.setTaskId("");
				summary.setNextTaskId("");
			}
			if (summary.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (summary.isNewRecord() && creditReviewDetails.isNewRecord()) {
				if (approveRec) {
					if (summary.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						saveRecord = true;
					} else if (summary.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
						deleteRecord = true;
					} else {
						updateRecord = true;
					}
				} else {
					saveRecord = true;
					if (summary.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						summary.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (summary.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
						summary.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (summary.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
						summary.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}
				}
			} else if (summary.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}

			} else if (summary.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (summary.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (summary.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = summary.getRecordType();
				recordStatus = summary.getRecordStatus();
				summary.setRecordType("");
				summary.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				creditReviewSummaryDAO.save(summary, type);

			}

			if (updateRecord) {
				creditReviewSummaryDAO.update(summary, type);
			}

			if (deleteRecord) {
				creditReviewSummaryDAO.delete(summary, type);
			}

			if (approveRec) {
				summary.setRecordType(rcdType);
				summary.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(summary);
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
	private List<AuditDetail> processCustomerDocuments(List<AuditDetail> auditDetails, long detailId, String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			CustomerDocument customerDocument = (CustomerDocument) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				customerDocument.setRoleCode("");
				customerDocument.setNextRoleCode("");
				customerDocument.setTaskId("");
				customerDocument.setNextTaskId("");
			}
			if (customerDocument.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (customerDocument.isNewRecord()) {
				if (approveRec) {
					if (customerDocument.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						saveRecord = true;
					} else if (customerDocument.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
						deleteRecord = true;
					} else if (customerDocument.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
						updateRecord = true;
					}
				} else {
					saveRecord = true;
					if (customerDocument.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						customerDocument.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (customerDocument.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
						customerDocument.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (customerDocument.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
						customerDocument.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}
				}
			} else if (customerDocument.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (customerDocument.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (customerDocument.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (customerDocument.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			customerDocument.setWorkflowId(0);
			if (approveRec) {
				rcdType = customerDocument.getRecordType();
				recordStatus = customerDocument.getRecordStatus();
				customerDocument.setRecordType("");
				customerDocument.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				customerDocumentDAO.save(customerDocument, type);
			}

			if (updateRecord) {
				customerDocumentDAO.update(customerDocument, type);
			}

			if (deleteRecord) {
				customerDocumentDAO.delete(customerDocument, type);
			}

			if (approveRec) {
				customerDocument.setRecordType(rcdType);
				customerDocument.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(customerDocument);
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
	public List<AuditDetail> listDeletion(FinCreditReviewDetails creditReviewDetails, String tableType,
			String auditTranType) {

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		if (creditReviewDetails.getLovDescFinCreditRevSubCategory() != null
				&& creditReviewDetails.getLovDescFinCreditRevSubCategory().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinCreditReviewSummary());
			for (int i = 0; i < creditReviewDetails.getLovDescFinCreditRevSubCategory().size(); i++) {
				FinCreditRevSubCategory finCreditRevSubCategory = creditReviewDetails
						.getLovDescFinCreditRevSubCategory().get(i);
				if (StringUtils.isNotBlank(finCreditRevSubCategory.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							finCreditRevSubCategory.getBefImage(), finCreditRevSubCategory));
				}
				finCreditRevSubCategoryDAO.delete(finCreditRevSubCategory, tableType);
			}
		}

		if (creditReviewDetails.getCreditReviewSummaryEntries() != null
				&& creditReviewDetails.getCreditReviewSummaryEntries().size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinCreditReviewSummary());
			for (int i = 0; i < creditReviewDetails.getCreditReviewSummaryEntries().size(); i++) {
				FinCreditReviewSummary creditReviewSummary = creditReviewDetails.getCreditReviewSummaryEntries().get(i);
				if (StringUtils.isNotBlank(creditReviewSummary.getRecordType()) || StringUtils.isEmpty(tableType)) {
					auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
							creditReviewSummary.getBefImage(), creditReviewSummary));
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

		if (list != null && list.size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinCreditReviewSummary(), excludeFields);
			String[] fields1 = PennantJavaUtil.getFieldDetails(new FinCreditRevSubCategory(), excludeFields);

			for (int i = 0; i < list.size(); i++) {

				String transType = "";
				String rcdType = "";

				if (list.get(i).getModelData() instanceof FinCreditRevSubCategory) {
					FinCreditRevSubCategory finCreditRevSubCategory = (FinCreditRevSubCategory) ((AuditDetail) list
							.get(i)).getModelData();
					rcdType = finCreditRevSubCategory.getRecordType();

					if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						transType = PennantConstants.TRAN_ADD;
					} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
							|| rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						transType = PennantConstants.TRAN_DEL;
					} else {
						transType = PennantConstants.TRAN_UPD;
					}

					if (StringUtils.isNotEmpty(transType)) {
						// check and change below line for Complete code
						auditDetailsList
								.add(new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(), fields1[0],
										fields1[1], finCreditRevSubCategory.getBefImage(), finCreditRevSubCategory));
					}

				} else if (list.get(i).getModelData() instanceof FinCreditReviewSummary) {
					FinCreditReviewSummary creditReviewSummaryEntry = (FinCreditReviewSummary) ((AuditDetail) list
							.get(i)).getModelData();
					rcdType = creditReviewSummaryEntry.getRecordType();

					if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						transType = PennantConstants.TRAN_ADD;
					} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
							|| rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						transType = PennantConstants.TRAN_DEL;
					} else {
						transType = PennantConstants.TRAN_UPD;
					}

					if (StringUtils.isNotEmpty(transType)) {
						// check and change below line for Complete code
						auditDetailsList
								.add(new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(), fields[0],
										fields[1], creditReviewSummaryEntry.getBefImage(), creditReviewSummaryEntry));
					}
				}
			}
		}
		logger.debug("Leaving");
		return auditDetailsList;
	}

	public void setCreditReviewSummaryEntryValidation(
			CreditReviewSummaryEntryValidation creditReviewSummaryEntryValidation) {
		this.creditReviewSummaryEntryValidation = creditReviewSummaryEntryValidation;
	}

	public CreditReviewSummaryEntryValidation getCreditReviewSummaryEntryValidation() {
		return creditReviewSummaryEntryValidation;
	}

	@Override
	public List<FinCreditReviewSummary> getListCreditReviewSummaryById(long id, String type, boolean postingProcess) {
		return creditReviewSummaryDAO.getListCreditReviewSummaryById(id, type, postingProcess);
	}

	public FinCreditRevType getFinCreditRevByRevCode(String creditRevCode) {
		return creditApplicationReviewDAO.getFinCreditRevByRevCode(creditRevCode);
	}

	@Override
	public Map<String, List<FinCreditReviewSummary>> getListCreditReviewSummaryByCustId(long id, int noOfYears,
			int year, String type) {
		logger.debug("Entering");
		Map<String, List<FinCreditReviewSummary>> map = new LinkedHashMap<String, List<FinCreditReviewSummary>>();
		for (int i = noOfYears; i >= 1; i--) {
			map.put(String.valueOf(year - i + 1), this.creditReviewSummaryDAO
					.getListCreditReviewSummaryByYearAndCustId(id, String.valueOf(year - i + 1), type));
		}
		logger.debug("Leaving");
		return map;
	}

	@Override
	public Map<String, FinCreditReviewDetails> getListCreditReviewDetailsByCustId(long id, int noOfYears, int year) {
		logger.debug("Entering");
		Map<String, FinCreditReviewDetails> map = new LinkedHashMap<String, FinCreditReviewDetails>();
		for (int i = noOfYears; i >= 1; i--) {
			map.put(String.valueOf(year - i + 1), this.creditReviewSummaryDAO.getCreditReviewDetailsByYearAndCustId(id,
					String.valueOf(year - i + 1), "_View"));
		}
		logger.debug("Leaving");
		return map;
	}

	@Override
	public Map<String, List<FinCreditReviewSummary>> getListCreditReviewSummaryByCustId2(long id, int noOfYears,
			int year, String category, String type) {
		logger.debug("Entering");
		Map<String, List<FinCreditReviewSummary>> map = new LinkedHashMap<String, List<FinCreditReviewSummary>>();
		// Calendar calender = Calendar.getInstance();
		// int year =DateUtility.getYear(calender.getTime());
		// int year =DateUtility.getYear(calender.getTime());
		List<FinCreditReviewSummary> finCreditReviewSummaries;
		for (int i = 0; i <= noOfYears; i++) {
			finCreditReviewSummaries = this.creditReviewSummaryDAO.getListCreditReviewSummaryByYearAndCustId2(id,
					String.valueOf(year - i), category, type);
			if (finCreditReviewSummaries != null && finCreditReviewSummaries.size() > 0) {
				map.put(String.valueOf(year - i), finCreditReviewSummaries);
			}
			if (i == noOfYears + 1) {
				break;
			}
		}

		/*
		 * for(int i=noOfYears;i>=1;i--){ }
		 */
		logger.debug("Leaving");
		return map;
	}

	@Override
	public Map<String, List<FinCreditReviewSummary>> getListCreditReviewSummaryByCustId2(long id, int noOfYears,
			int year, String category, int auditPeriod, boolean isCurrentYear, String type) {
		logger.debug("Entering");
		Map<String, List<FinCreditReviewSummary>> map = new LinkedHashMap<String, List<FinCreditReviewSummary>>();
		// Calendar calender = Calendar.getInstance();
		// int year =DateUtility.getYear(calender.getTime());
		// int year =DateUtility.getYear(calender.getTime());
		List<FinCreditReviewSummary> finCreditReviewSummaries;
		for (int i = 0; i <= noOfYears; i++) {
			finCreditReviewSummaries = this.creditReviewSummaryDAO.getListCreditReviewSummaryByYearAndCustId2(id,
					String.valueOf(year - i), category, auditPeriod, isCurrentYear, type);
			if (finCreditReviewSummaries != null && finCreditReviewSummaries.size() > 0) {
				map.put(String.valueOf(year - i), finCreditReviewSummaries);
			}
			if (i == noOfYears + 1) {
				break;
			}
		}

		/*
		 * for(int i=noOfYears;i>=1;i--){ }
		 */
		logger.debug("Leaving");
		return map;
	}

	@Override
	public String getMaxAuditYearByCustomerId(long customerId, String type) {
		return getCreditApplicationReviewDAO().getMaxAuditYearByCustomerId(customerId, type);
	}

	public BigDecimal getCcySpotRate(String ccyCode) {
		return this.creditReviewSummaryDAO.getCcySpotRate(ccyCode);
	}

	public int getCreditReviewAuditPeriodByAuditYear(final long customerId, final String auditYear, int auditPeriod,
			boolean isEnquiry, String type) {
		return getCreditApplicationReviewDAO().getCreditReviewAuditPeriodByAuditYear(customerId, auditYear, auditPeriod,
				isEnquiry, type);
	}

	@Override
	public List<FinCreditReviewDetails> getFinCreditRevDetailsByCustomerId(final long customerId, String type) {
		return getCreditApplicationReviewDAO().getFinCreditRevDetailsByCustomerId(customerId, type);
	}

	public List<CustomerDocument> getCustomerDocumentsById(long id, String type) {
		docsList = new ArrayList<CustomerDocument>();
		docsList = customerDocumentDAO.getCustomerDocumentByCustomerId(id);
		return docsList;
	}

	@Override
	public FinCreditReviewDetails getCreditReviewDetailsByCustIdAndYear(final long customerId, String auditYear,
			String type) {
		return this.creditApplicationReviewDAO.getCreditReviewDetailsByCustIdAndYear(customerId, auditYear, type);
	}

	@Override
	public int isCreditSummaryExists(long custID, String auditYear, int auditPeriod) {
		return this.creditApplicationReviewDAO.isCreditSummaryExists(custID, auditYear, auditPeriod);
	}

	@Override
	public List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByCategoryIdAndCalcSeq(long categoryId) {
		return this.creditApplicationReviewDAO.getFinCreditRevSubCategoryByCategoryIdAndCalcSeq(categoryId);
	}

	@Override
	public List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByMainCategory(String category) {
		return this.creditApplicationReviewDAO.getFinCreditRevSubCategoryByMainCategory(category);
	}

	@Override
	public List<FinCreditReviewSummary> getLatestCreditReviewSummaryByCustId(long id) {
		logger.debug("Entering");
		logger.debug("Leaving");
		return this.creditReviewSummaryDAO.getLatestCreditReviewSummaryByYearAndCustId(id);
	}

	@Override
	public List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByCategoryId(long categoryId,
			String subCategoryItemType) {
		return this.creditApplicationReviewDAO.getFinCreditRevSubCategoryByCategoryId(categoryId, subCategoryItemType);
	}

	@Override
	public List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByCustCtg(String custCtgCode, String categorydesc) {
		return this.creditApplicationReviewDAO.getFinCreditRevSubCategoryByCustCtg(custCtgCode, categorydesc);
	}

	@Override
	public CreditReviewDetails getCreditReviewDetailsByLoanType(CreditReviewDetails creditReviewDetail) {
		return creditReviewDetailDAO.getCreditReviewDetailsbyLoanType(creditReviewDetail);
	}

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
	public List<FinCreditRevCategory> getCreditRevCategoryByCreditRevCodeAndEligibilityIds(String creditRevCode,
			List<Long> eligibilityIds) {

		return this.creditApplicationReviewDAO.getCreditRevCategoryByCreditRevCodeAndEligibilityIds(creditRevCode,
				eligibilityIds);
	}

	@Override
	public List<FinCreditReviewDetails> getFinCreditRevDetailIds(long customerId) {
		return this.creditApplicationReviewDAO.getFinCreditRevDetailIds(customerId);
	}

	@Override
	public Map<String, Object> getFinCreditRevSummaryDetails(long id, String auditYear) {
		return this.creditApplicationReviewDAO.getFinCreditRevSummaryDetails(id);
	}

	@Override
	public List<FinCreditRevSubCategory> getFinCreditRevSubCategoryByCategoryId(long categoryId) {
		return this.creditApplicationReviewDAO.getFinCreditRevSubCategoryByCategoryId(categoryId);
	}

	@Override
	public CreditReviewData getCreditReviewDataByRef(long finID, String templateName, int templateVersion) {
		return creditReviewDetailDAO.getCreditReviewData(finID, templateName);
	}

	@Override
	public CreditReviewDetails getCreditReviewDetailsByRef(CreditReviewDetails creditReviewDetail) {
		return creditReviewDetailDAO.getCreditReviewDetails(creditReviewDetail);
	}

	@Override
	public FinCreditReviewDetails getNewCreditReviewDetails() {
		return getCreditApplicationReviewDAO().getNewCreditReviewDetails();
	}

	@Override
	public ExtCreditReviewConfig getExtCreditReviewConfigDetails(ExtCreditReviewConfig extCreditReviewDetail) {
		return creditReviewDetailDAO.getExtCreditReviewConfigDetails(extCreditReviewDetail);
	}

	@Override
	public ExtBreDetails getExtBreDetailsByRef(long finID) {
		return creditReviewDetailDAO.getExtBreDetailsByRef(finID);
	}

	@Override
	public Long getFinID(String finReference) {
		return financeMainDAO.getFinID(finReference);
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setFinCreditRevSubCategoryService(FinCreditRevSubCategoryService finCreditRevSubCategoryService) {
		this.finCreditRevSubCategoryService = finCreditRevSubCategoryService;
	}

	public void setFinCreditRevSubCategoryDAO(FinCreditRevSubCategoryDAO finCreditRevSubCategoryDAO) {
		this.finCreditRevSubCategoryDAO = finCreditRevSubCategoryDAO;
	}

	public void setCustomerDocumentDAO(CustomerDocumentDAO customerDocumentDAO) {
		this.customerDocumentDAO = customerDocumentDAO;
	}

	public void setNotesDAO(NotesDAO notesDAO) {
		this.notesDAO = notesDAO;
	}

	public void setCreditReviewDetailDAO(CreditReviewDetailDAO creditReviewDetailDAO) {
		this.creditReviewDetailDAO = creditReviewDetailDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
}

package com.pennanttech.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.documentdetails.DocumentManagerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.customermasters.CustomerDocumentService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.document.DocumentService;
import com.pennanttech.ws.model.customer.DocumentList;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class DocumentController extends ExtendedTestClass {
	private Logger logger = LogManager.getLogger(DocumentController.class);

	private DocumentService documentService;
	private DocumentDetailsDAO documentDetailsDAO;
	private DocumentManagerDAO documentManagerDAO;
	private CustomerDocumentService customerDocumentService;
	private FinanceMainDAO financeMainDAO;

	public AuditHeader processDocumentDetails(DocumentDetails detail) {
		logger.debug(Literal.ENTERING);

		AuditHeader auditHeader = new AuditHeader();
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		detail.setNewRecord(true);
		detail.setLastMntBy(userDetails.getUserId());
		detail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		detail.setVersion(1);
		detail.setDocRefId(null);
		// set update properties if exists
		String finReference = detail.getReferenceId();
		String docCategory = detail.getDocCategory();
		String module = FinanceConstants.MODULE_NAME;
		String type = TableType.MAIN_TAB.getSuffix();
		DocumentDetails extDocDetail = documentDetailsDAO.getDocumentDetails(finReference, docCategory, module, type);
		if (extDocDetail != null) {
			detail.setDocId(extDocDetail.getDocId());
			detail.setNewRecord(false);
			detail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			detail.setVersion(extDocDetail.getVersion() + 1);
		}

		detail.setDocModule(FinanceConstants.MODULE_NAME);
		detail.setUserDetails(SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser()));
		detail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

		if (StringUtils.equals(detail.getRecordType(), PennantConstants.RECORD_TYPE_UPD)) {
			auditHeader = getAuditHeader(detail, PennantConstants.TRAN_UPD);
		} else {
			auditHeader = getAuditHeader(detail, PennantConstants.TRAN_ADD);
		}
		auditHeader = documentService.saveOrUpdate(auditHeader);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	public WSReturnStatus processFinDocumentDetails(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		FinanceMain finMain = null;
		
		if (StringUtils.isNotBlank(financeDetail.getFinReference())) {
			finMain = financeMainDAO.getFinanceMainByRef(financeDetail.getFinReference(), "_View", false);

			if (finMain == null) {
				String[] valueParm = new String[1];
				valueParm[0] = financeDetail.getFinReference();
				return APIErrorHandlerService.getFailedStatus("90201", valueParm);
			}
		} else {
			return APIErrorHandlerService.getFailedStatus("9999", "FinReference is Mandatory");
		}
		try {
			financeDetail.getFinScheduleData().setFinanceMain(finMain);
			List<DocumentDetails> oldDocList = documentDetailsDAO.getDocumentDetailsByRef(
					financeDetail.getFinReference(), FinanceConstants.MODULE_NAME, "", "_View");
			for (DocumentDetails documentDetail : financeDetail.getDocumentDetailsList()) {
				for (DocumentDetails docdetail : oldDocList) {
					if (docdetail.getDocCategory().equals(documentDetail.getDocCategory())) {
						String[] valueParm = new String[1];
						valueParm[0] = "docCategory : " + docdetail.getDocCategory() + " Is Already Exists";
						return APIErrorHandlerService.getFailedStatus("30550", valueParm);
					}
				}
			}
			
			for (DocumentDetails documentDetail : financeDetail.getDocumentDetailsList()) {
				AuditHeader auditHeader = new AuditHeader();
				LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
				documentDetail.setReferenceId(financeDetail.getFinReference());
				documentDetail.setNewRecord(true);
				documentDetail.setLastMntBy(userDetails.getUserId());
				documentDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				documentDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				documentDetail.setVersion(1);
				documentDetail.setDocRefId(Long.MIN_VALUE);
				documentDetail.setFinEvent(FinServiceEvent.ORG);
				documentDetail.setDocModule(FinanceConstants.MODULE_NAME);
				documentDetail.setUserDetails(SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser()));
				documentDetail.setRecordStatus(PennantConstants.RCD_ADD);
				documentDetail.setWorkflowId(finMain.getWorkflowId());
				documentDetail.setCustId(finMain.getCustID());

				auditHeader = getAuditHeader(documentDetail, PennantConstants.TRAN_ADD);

				documentService.saveOrUpdate(auditHeader);
			}
		} catch (InterfaceException ex) {
			logger.error("InterfaceException", ex);
			return APIErrorHandlerService.getFailedStatus("9999", ex.getMessage());
		} catch (AppException ex) {
			logger.error("AppException", ex);
			return APIErrorHandlerService.getFailedStatus("9999", ex.getMessage());
		} catch (Exception e) {
			logger.error("Exception: ", e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug(Literal.LEAVING);
		return APIErrorHandlerService.getSuccessStatus();
	}

	/**
	 * Method for get the DocumentDetails based on the given parameters.
	 * 
	 * @param finReference
	 * @param docCategory
	 * @param type
	 * @return
	 */
	public DocumentDetails getFinanceDocument(String finReference, String docCategory, String type) {
		logger.debug(Literal.ENTERING);
		DocumentDetails response;
		try {
			DocumentType documentType = documentService.getApprovedDocumentTypeById(docCategory);
			// validate given docCategory is valid or not.
			if (documentType == null) {
				response = new DocumentDetails();
				String[] valueParm = new String[1];
				valueParm[0] = docCategory;
				response.setReferenceId(null);
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90401", valueParm));
				return response;
			}
			response = documentDetailsDAO.getDocumentDetails(finReference, docCategory,
					DocumentCategories.FINANCE.getKey(), type);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			response = new DocumentDetails();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}

		if (response == null) {
			response = new DocumentDetails();
			String[] valueParm = new String[1];
			valueParm[0] = "finreference: " + finReference + " & docCategory: " + docCategory;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90266", valueParm));
			return response;
		}
		// setting byte data.
		if (StringUtils.isBlank(response.getDocUri()) && response.getDocImage() == null) {
			Long docRefId = response.getDocRefId();
			if (docRefId != null) {
				DocumentManager documentManager = documentManagerDAO.getById(docRefId);
				if (documentManager != null && documentManager.getDocImage() != null) {
					response.setDocImage(documentManager.getDocImage());
				}
			}
		}
		// preparing successive response
		response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

		logger.debug(Literal.LEAVING);
		return response;

	}

	public DocumentList getCustAndLoanDocuments(String finReferance, long custID) {
		logger.debug(Literal.ENTERING);

		DocumentList response = new DocumentList();
		try {
			List<CustomerDocument> customerDocumentsList = customerDocumentService
					.getApprovedCustomerDocumentById(custID);
			if (CollectionUtils.isNotEmpty(customerDocumentsList)) {
				for (CustomerDocument documents : customerDocumentsList) {
					byte[] custDocImage = getDocumentImage(documents.getDocRefId());
					documents.setCustDocImage(custDocImage);
				}
				response.setCustomerDocumentsList(customerDocumentsList);

			} else {
				response.setCustomerDocumentsList(customerDocumentsList);

			}

			List<DocumentDetails> documentDetailsList = documentDetailsDAO.getDocumentDetailsByRef(finReferance,
					DocumentCategories.FINANCE.getKey(), "");

			if (CollectionUtils.isNotEmpty(documentDetailsList)) {
				for (DocumentDetails documents : documentDetailsList) {
					byte[] docImage = getDocumentImage(documents.getDocRefId());
					documents.setDocImage(docImage);
				}
				response.setFinanceDocumentsList(documentDetailsList);

			} else {

				response.setFinanceDocumentsList(documentDetailsList);
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}
		// preparing successive response
		response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

		logger.debug(Literal.LEAVING);
		return response;

	}

	private byte[] getDocumentImage(long docID) {
		DocumentManager docImage = documentManagerDAO.getById(docID);
		if (docImage != null) {
			return docImage.getDocImage();
		}
		return null;
	}

	private AuditHeader getAuditHeader(DocumentDetails documentDetails, String transType) {
		AuditDetail auditDetail = new AuditDetail(transType, 1, documentDetails.getBefImage(), documentDetails);
		return new AuditHeader(documentDetails.getReferenceId(), null, null, null, auditDetail,
				documentDetails.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public void setDocumentManagerDAO(DocumentManagerDAO documentManagerDAO) {
		this.documentManagerDAO = documentManagerDAO;
	}

	public void setCustomerDocumentService(CustomerDocumentService customerDocumentService) {
		this.customerDocumentService = customerDocumentService;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

}

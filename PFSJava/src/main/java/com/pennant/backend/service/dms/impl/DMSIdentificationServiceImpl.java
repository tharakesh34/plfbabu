package com.pennant.backend.service.dms.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.collateral.CollateralAssignmentDAO;
import com.pennant.backend.dao.dms.DMSIdentificationDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.systemmasters.DocumentTypeDAO;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.dms.DMSIdentificationService;
import com.pennant.backend.util.FinanceConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.model.LegalVerification;
import com.pennanttech.pennapps.pff.verification.model.RiskContainmentUnit;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pennapps.pff.verification.service.VerificationService;
import com.pennanttech.pff.constants.FinServiceEvent;

public class DMSIdentificationServiceImpl implements DMSIdentificationService {
	private static Logger logger = LogManager.getLogger(DMSIdentificationServiceImpl.class);

	private DMSIdentificationDAO dmsIdentificationDao;
	private CollateralAssignmentDAO collateralAssignmentDAO;
	private CollateralSetupService collateralSetupService;
	private VerificationService verificationService;
	private DocumentDetailsDAO documentDetailsDAO;
	private CustomerDetailsService customerDetailsService;
	private DocumentTypeDAO documentTypeDAO;

	@Override
	public void identifyExternalDocument(AuditHeader auditHeader) {
		logger.debug("Entering");
		List<DocumentDetails> dmsDocumentDetailList = new ArrayList<>();
		List<DocumentDetails> totDocumentDetailsList = new ArrayList<>();

		try {
			if (null != auditHeader) {
				Object modelObj = auditHeader.getAuditDetail().getModelData();
				if (modelObj instanceof FinanceDetail) {
					FinanceDetail financeDetail = (FinanceDetail) modelObj;

					/*
					 * Search search = new Search(DocumentType.class); search.addTabelName("BMTDocumentTypes");
					 */

					List<DocumentType> documentTypeList = documentTypeDAO.getDocumentTypes();

					FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
					String finReference = finScheduleData.getFinReference();

					if (null != financeDetail && null != finScheduleData) {
						long custId = finScheduleData.getFinanceMain().getCustID();

						CustomerDetails customerDetails = customerDetailsService.getCustomerAndCustomerDocsById(custId,
								"_AView");
						if (null != customerDetails
								&& CollectionUtils.isNotEmpty(customerDetails.getCustomerDocumentsList())) {
							for (CustomerDocument customer : customerDetails.getCustomerDocumentsList()) {
								if (docUriNotExist(customer)) {
									DocumentDetails details = new DocumentDetails();
									details.setFinReference(finReference);
									details.setDocModule("CUSTOMER");
									details.setDocRefId(customer.getDocRefId());
									details.setState("Identified");
									details.setStatus("");
									details.setDocId(customer.getCustID());
									details.setDocDesc(customer.getLovDescCustDocCategory());
									details.setDocCategory(customer.getCustDocCategory());
									details.setDocExt(customer.getCustDocType());
									details.setLastMntOn(new Timestamp(Calendar.getInstance().getTimeInMillis()));
									details.setCreatedOn(new Timestamp(Calendar.getInstance().getTimeInMillis()));
									details.setReferenceId(
											financeDetail.getCustomerDetails().getCustomer().getCustCIF());
									if (null != financeDetail && null != financeDetail.getCustomerDetails()
											&& null != financeDetail.getCustomerDetails().getCustomer()) {
										details.setCustomerCif(
												financeDetail.getCustomerDetails().getCustomer().getCustCIF());
									}
									dmsDocumentDetailList.add(details);
								}
							}
						}

						List<DocumentDetails> documentDetailsList = documentDetailsDAO.getDocumentDetailsByRef(
								finReference, FinanceConstants.MODULE_NAME, FinServiceEvent.ORG, "_View");

						if (CollectionUtils.isNotEmpty(documentDetailsList)) {
							totDocumentDetailsList.addAll(documentDetailsList);
						}

						List<CollateralAssignment> collateralAssignmentByFinRef = null;
						if (null != collateralAssignmentDAO) {
							collateralAssignmentByFinRef = collateralAssignmentDAO.getCollateralAssignmentByFinRef(
									finReference, FinanceConstants.MODULE_NAME, "_View");
						}
						List<CollateralSetup> collateralSetupList = null;
						if (null != collateralSetupService
								&& CollectionUtils.isNotEmpty(collateralAssignmentByFinRef)) {
							collateralSetupList = collateralAssignmentByFinRef.stream().map(colAssign -> {
								if (null != colAssign) {
									return collateralSetupService.getCollateralSetupByRef(colAssign.getCollateralRef(),
											"", false);
								}
								return null;
							}).filter(details -> null != details).collect(Collectors.toList());
							if (CollectionUtils.isNotEmpty(collateralSetupList)) {
								for (CollateralSetup collateralSetup : collateralSetupList) {
									if (null != collateralSetup
											&& CollectionUtils.isNotEmpty(collateralSetup.getDocuments())) {
										totDocumentDetailsList.addAll(collateralSetup.getDocuments());
									}
								}
							}
						}

						if (null != verificationService) {
							List<Verification> verifications = verificationService
									.getVerificationsForAggrement(finReference);
							if (CollectionUtils.isNotEmpty(verifications)) {
								for (Verification verification : verifications) {
									if (null != verification) {
										VerificationType type = VerificationType
												.getVerificationType(verification.getVerificationType());

										switch (type) {
										case LV:
											LegalVerification legalVerification = verification.getLegalVerification();
											if (null != legalVerification
													&& CollectionUtils.isNotEmpty(legalVerification.getDocuments())) {
												totDocumentDetailsList.addAll(legalVerification.getDocuments());
											}
											break;
										case RCU:
											RiskContainmentUnit riskContainmentUnit = verification.getRcuVerification();
											if (null != riskContainmentUnit
													&& CollectionUtils.isNotEmpty(riskContainmentUnit.getDocuments())) {
												totDocumentDetailsList.addAll(riskContainmentUnit.getDocuments());
											}
											break;
										default:
											break;
										}
									}
								}
							}
						}
					}

					if (CollectionUtils.isNotEmpty(totDocumentDetailsList)) {
						for (DocumentDetails documentDetails : totDocumentDetailsList) {
							if (null != documentDetails && documentDetails.getDocRefId() != null
									&& documentDetails.getDocRefId() != Long.MIN_VALUE
									&& StringUtils.isEmpty(documentDetails.getDocUri())) {
								DocumentDetails details = new DocumentDetails();
								details.setFinReference(finReference);
								details.setDocModule(documentDetails.getDocModule());
								details.setDocRefId(documentDetails.getDocRefId());
								details.setState("Identified");
								details.setStatus("");
								details.setDocId(documentDetails.getDocId());
								details.setDocCategory(documentDetails.getDocCategory());
								if (null != documentTypeList) {
									for (DocumentType documentType : documentTypeList) {
										if (null != documentType && null != documentType.getDocTypeCode()
												&& null != documentDetails.getDocCategory() && documentType
														.getDocTypeCode().equals(documentDetails.getDocCategory())) {
											details.setDocDesc(documentType.getDocTypeDesc());
											break;
										}
									}
								}
								details.setDocExt(documentDetails.getDoctype());
								details.setLastMntOn(new Timestamp(Calendar.getInstance().getTimeInMillis()));
								details.setCreatedOn(new Timestamp(Calendar.getInstance().getTimeInMillis()));
								details.setReferenceId(documentDetails.getReferenceId());
								if (null != financeDetail && null != financeDetail.getCustomerDetails()
										&& null != financeDetail.getCustomerDetails().getCustomer()) {
									details.setCustomerCif(
											financeDetail.getCustomerDetails().getCustomer().getCustCIF());
								}
								dmsDocumentDetailList.add(details);
							}
						}
					}
				}
			}

			if (CollectionUtils.isNotEmpty(dmsDocumentDetailList)) {
				dmsIdentificationDao.saveDMSDocumentReferences(dmsDocumentDetailList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(Literal.EXCEPTION, e);
		}

		logger.debug("Leaving");
	}

	private boolean docUriNotExist(CustomerDocument customer) {
		return null != customer && customer.getDocRefId() != null && customer.getDocRefId() != Long.MIN_VALUE
				&& StringUtils.isEmpty(customer.getDocUri());
	}

	@Override
	public List<DocumentDetails> getDmsDocumentDetails(long dmsId) {
		logger.debug("Entering");
		List<DocumentDetails> dmsDocumentLogs = null;
		try {
			dmsDocumentLogs = dmsIdentificationDao.retrieveDMSDocumentLogs(dmsId);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug("Leaving");
		return dmsDocumentLogs;
	}

	@Autowired
	public void setDmsIdentificationDao(DMSIdentificationDAO dmsIdentificationDao) {
		this.dmsIdentificationDao = dmsIdentificationDao;
	}

	@Autowired
	public void setCollateralAssignmentDAO(CollateralAssignmentDAO collateralAssignmentDAO) {
		this.collateralAssignmentDAO = collateralAssignmentDAO;
	}

	@Autowired
	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}

	@Autowired
	public void setVerificationService(VerificationService verificationService) {
		this.verificationService = verificationService;
	}

	@Autowired
	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	@Autowired
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	@Autowired
	public void setDocumentTypeDAO(DocumentTypeDAO documentTypeDAO) {
		this.documentTypeDAO = documentTypeDAO;
	}

}

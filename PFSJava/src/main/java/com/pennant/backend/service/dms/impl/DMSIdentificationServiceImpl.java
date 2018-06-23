package com.pennant.backend.service.dms.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.collateral.CollateralAssignmentDAO;
import com.pennant.backend.dao.dms.DMSIdentificationDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.dms.DMSIdentificationService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennanttech.model.dms.DMSDocumentDetails;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.model.LegalVerification;
import com.pennanttech.pennapps.pff.verification.model.RiskContainmentUnit;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pennapps.pff.verification.service.VerificationService;

public class DMSIdentificationServiceImpl implements DMSIdentificationService {
	private static Logger logger = Logger.getLogger(DMSIdentificationServiceImpl.class);
	
	@Autowired
	private DMSIdentificationDAO dmsIdentificationDao;
	@Autowired
	private CollateralAssignmentDAO collateralAssignmentDAO;
	@Autowired
	private CollateralSetupService collateralSetupService;
	@Autowired
	private VerificationService verificationService;
	@Autowired
	private DocumentDetailsDAO documentDetailsDAO;
	@Autowired
	private CustomerDetailsService customerDetailsService;
	@Autowired
	private SearchProcessor searchProcessor;

	@Override
	public void identifyExternalDocument(AuditHeader auditHeader) {
		logger.debug("Entering");
		List<DMSDocumentDetails> dmsDocumentDetailList = new ArrayList<>();
		List<DocumentDetails> totDocumentDetailsList = new ArrayList<>();
		
		try{
			if (null != auditHeader) {
				Object modelObj = auditHeader.getAuditDetail().getModelData();
				if (modelObj instanceof FinanceDetail) {
					FinanceDetail financeDetail = (FinanceDetail) modelObj;

					JdbcSearchObject<DocumentType> searchObject = new JdbcSearchObject<DocumentType>(DocumentType.class);
					searchObject.addTabelName("BMTDocumentTypes");

					List<DocumentType> documentTypeList = searchProcessor.getResults(searchObject);

					if (null != financeDetail && null != financeDetail.getFinScheduleData()) {
						long custId = financeDetail.getFinScheduleData().getFinanceMain().getCustID();

						if (null != customerDetailsService) {
							CustomerDetails customerDetails = customerDetailsService.getCustomerAndCustomerDocsById(custId,
									"_AView");
							if (null != customerDetails
									&& CollectionUtils.isNotEmpty(customerDetails.getCustomerDocumentsList())) {
								for (CustomerDocument customer : customerDetails.getCustomerDocumentsList()) {
									if (null != customer && customer.getDocRefId() != Long.MIN_VALUE
											&& StringUtils.isEmpty(customer.getDocUri())) {
										DMSDocumentDetails details = new DMSDocumentDetails();
										details.setFinReference(financeDetail.getFinScheduleData().getFinReference());
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
											details.setCustomerCif(financeDetail.getCustomerDetails().getCustomer().getCustCIF());
										}
										dmsDocumentDetailList.add(details);
									}
								}
							}
						}

						List<DocumentDetails> documentDetailsList = documentDetailsDAO.getDocumentDetailsByRef(
								financeDetail.getFinScheduleData().getFinReference(), FinanceConstants.MODULE_NAME,
								FinanceConstants.FINSER_EVENT_ORG, "_View");

						if (CollectionUtils.isNotEmpty(documentDetailsList)) {
							totDocumentDetailsList.addAll(documentDetailsList);
						}

						List<CollateralAssignment> collateralAssignmentByFinRef = null;
						if (null != collateralAssignmentDAO) {
							collateralAssignmentByFinRef = collateralAssignmentDAO.getCollateralAssignmentByFinRef(
									financeDetail.getFinScheduleData().getFinReference(), FinanceConstants.MODULE_NAME,
									"_View");
						}
						List<CollateralSetup> collateralSetupList = null;
						if (null != collateralSetupService && CollectionUtils.isNotEmpty(collateralAssignmentByFinRef)) {
							collateralSetupList = collateralAssignmentByFinRef.stream().map(colAssign -> {
								if (null != colAssign) {
									return collateralSetupService.getCollateralSetupByRef(colAssign.getCollateralRef(), "",
											false);
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
									.getVerificationsForAggrement(financeDetail.getFinScheduleData().getFinReference());
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
										}
									}
								}
							}
						}
					}

					if (CollectionUtils.isNotEmpty(totDocumentDetailsList)) {
						for (DocumentDetails documentDetails : totDocumentDetailsList) {
							if (null != documentDetails && documentDetails.getDocRefId() != Long.MIN_VALUE
									&& StringUtils.isEmpty(documentDetails.getDocUri())) {
								DMSDocumentDetails details = new DMSDocumentDetails();
								details.setFinReference(financeDetail.getFinScheduleData().getFinReference());
								details.setDocModule(documentDetails.getDocModule());
								details.setDocRefId(documentDetails.getDocRefId());
								details.setState("Identified");
								details.setStatus("");
								details.setDocId(documentDetails.getDocId());
								details.setDocCategory(documentDetails.getDocCategory());
								if (null != documentTypeList) {
									for (DocumentType documentType : documentTypeList) {
										if (null != documentType && null != documentType.getDocTypeCode()
												&& null != documentDetails.getDocCategory()
												&& documentType.getDocTypeCode().equals(documentDetails.getDocCategory())) {
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
									details.setCustomerCif(financeDetail.getCustomerDetails().getCustomer().getCustCIF());
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
		}catch(Exception e){
			logger.debug(Literal.EXCEPTION, e);
		}
		
		
		logger.debug("Leaving");
	}

}

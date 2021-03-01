package com.pennant.backend.service.collateral.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.document.DocumentCategories;

public class CollateralSetupFetchingService {
	private static final Logger logger = LogManager.getLogger(CollateralSetupFetchingService.class);

	private transient DocumentDetailsDAO documentDetailsDAO;
	private transient CollateralSetupService collateralSetupService;

	/**
	 * Checking if the Collateral Setup created from loan or not, if it is from loan we will take the Collateral Setup
	 * from memory otherwise will take from DB.
	 * 
	 * @param aFinanceDetail
	 * @param addReqFinanceMain
	 * @return
	 */
	public List<CollateralSetup> getCollateralSetupList(FinanceDetail aFinanceDetail, boolean addReqFinanceMain) {
		List<CollateralSetup> collateralSetupList = getCollateralSetupList(aFinanceDetail.getCollateralAssignmentList(),
				aFinanceDetail);
		if (addReqFinanceMain) {
			aFinanceDetail.setCollaterals(collateralSetupList);
		}
		return collateralSetupList;
	}

	/**
	 * Checking if the Collateral Setup created from loan or not, if it is from loan we will take the Collateral Setup
	 * from memory otherwise will take from DB.
	 * 
	 * @param collateralAssignments
	 * @param aFinanceDetail
	 * @return
	 */
	public List<CollateralSetup> getCollateralSetupList(List<CollateralAssignment> collateralAssignments,
			FinanceDetail aFinanceDetail) {
		logger.debug(Literal.ENTERING);

		CollateralSetup collateralSetup;
		List<CollateralSetup> collateralSetupList = aFinanceDetail.getCollaterals();
		List<CollateralSetup> collateralSetupResultList = new ArrayList<>();

		if (CollectionUtils.isEmpty(collateralAssignments)) {
			return collateralSetupResultList;
		}

		for (CollateralAssignment collateralAssignment : collateralAssignments) {

			// If assignment is in deleted state, no need to consider.
			if (isDeleted(collateralAssignment.getRecordType())) {
				continue;
			}

			// Checking Collateral Setup created from loan
			if (CollectionUtils.isNotEmpty(collateralSetupList)) {
				boolean addedRcd = false;
				// Checking if the Collateral Setup created from loan we will take it from memory,other wise we will take it from DB.
				for (CollateralSetup detail : collateralSetupList) {
					if (detail.getCollateralRef().equals(collateralAssignment.getCollateralRef())) {
						collateralSetupResultList.add(detail);
						addedRcd = true;
						break;
					}
				}
				if (!addedRcd) {
					// if the Collateral Setup didn't created from loan, we will take Collateral Setup from DB.
					collateralSetup = getCollateralSetupService()
							.getCollateralSetupByRef(collateralAssignment.getCollateralRef(), "", false);
					if (collateralSetup != null) {
						collateralSetupResultList.add(collateralSetup);
					}
				}
			} else {
				// Getting the Collateral Setup from DB.
				collateralSetup = getCollateralSetupService()
						.getCollateralSetupByRef(collateralAssignment.getCollateralRef(), "", false);
				if (collateralSetup != null) {
					collateralSetupResultList.add(collateralSetup);
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return collateralSetupResultList;
	}

	/**
	 * * Checking if the Collateral Setup created from loan or not, if it is from loan we will take the Collateral
	 * documents from memory otherwise will take from DB.
	 * 
	 * @param collateralAssignmentList
	 * @param collateralSetupList
	 * @param isDeleteCheckReq
	 * @return
	 */
	public List<DocumentDetails> getCollateralDocuments(List<CollateralAssignment> collateralAssignmentList,
			List<CollateralSetup> collateralSetupList, boolean isDeleteCheckReq) {
		logger.debug(Literal.ENTERING);

		List<DocumentDetails> documents = new ArrayList<>();
		long docId = -1;

		for (CollateralAssignment assignment : collateralAssignmentList) {
			List<DocumentDetails> list = getDocumentDetailsDAO().getDocumentDetailsByRef(assignment.getCollateralRef(),
					CollateralConstants.MODULE_NAME, "", "_View");
			if (CollectionUtils.isNotEmpty(list)) {
				if (isDeleteCheckReq) {
					for (DocumentDetails documentDetails : list) {
						if (isDeleted(assignment.getRecordType())) {
							documentDetails.setRecordType(PennantConstants.RECORD_TYPE_DEL);
						}
					}
				}
				documents.addAll(list);
			} else {
				if (CollectionUtils.isNotEmpty(collateralSetupList)) {
					for (CollateralSetup collateralSetup : collateralSetupList) {
						boolean isExists = false;
						if (assignment.getCollateralRef().equals(collateralSetup.getCollateralRef())) {
							isExists = true;
						}

						if (isExists) {
							List<DocumentDetails> documentsList = collateralSetup.getDocuments();
							if (CollectionUtils.isNotEmpty(documentsList)) {
								for (DocumentDetails details : documentsList) {
									if ((DocumentCategories.CUSTOMER.getKey().equals(details.getCategoryCode()))) {
										continue;
									}
									details.setDocId(docId);
									details.setReferenceId(collateralSetup.getCollateralRef());
									details.setDocModule(CollateralConstants.MODULE_NAME);
									docId = docId - 1;
									documents.add(details);
								}
							}
						}
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return documents;
	}

	/**
	 * @param collateralAsssignments
	 * @param collateralSetupList
	 * @return
	 */
	public List<CollateralSetup> getResultantCollateralsList(List<CollateralAssignment> collateralAsssignments,
			List<CollateralSetup> collateralSetupList) {
		logger.debug(Literal.ENTERING);

		List<CollateralSetup> resultantCollateralSetupList = new ArrayList<>();

		if (CollectionUtils.isEmpty(collateralAsssignments) || CollectionUtils.isEmpty(collateralSetupList)) {
			return resultantCollateralSetupList;
		}

		for (CollateralAssignment collateralAssignment : collateralAsssignments) {
			for (CollateralSetup collateralSetup : collateralSetupList) {
				if (collateralAssignment.getCollateralRef().equals(collateralSetup.getCollateralRef())) {
					resultantCollateralSetupList.add(collateralSetup);
					break;
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return resultantCollateralSetupList;
	}

	/**
	 * Checking the record is in deleted state or not
	 * 
	 * @param recordType
	 * @return
	 */
	private boolean isDeleted(String recordType) {
		return (PennantConstants.RECORD_TYPE_DEL.equals(recordType)
				|| PennantConstants.RECORD_TYPE_CAN.equals(recordType));
	}

	//Getters and setters
	public DocumentDetailsDAO getDocumentDetailsDAO() {
		return documentDetailsDAO;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public CollateralSetupService getCollateralSetupService() {
		return collateralSetupService;
	}

	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}

}

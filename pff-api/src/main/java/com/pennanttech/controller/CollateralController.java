package com.pennanttech.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;

import com.pennant.app.util.APIHeader;
import com.pennant.app.util.ReferenceUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.dao.collateral.CoOwnerDetailDAO;
import com.pennant.backend.dao.collateral.CollateralThirdPartyDAO;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CoOwnerDetail;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.collateral.CollateralStructure;
import com.pennant.backend.model.collateral.CollateralThirdParty;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.collateral.CollateralStructureService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerEMailService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.collateral.CollateralDetail;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class CollateralController extends ExtendedTestClass {
	Logger logger = LogManager.getLogger(CollateralController.class);

	private CollateralStructureService collateralStructureService;
	private CollateralSetupService collateralSetupService;
	private CustomerDetailsService customerDetailsService;
	private CollateralThirdPartyDAO collateralThirdPartyDAO;
	private ExtendedFieldRenderDAO extendedFieldRenderDAO;
	private DocumentDetailsDAO documentDetailsDAO;
	private CoOwnerDetailDAO coOwnerDetailDAO;
	private CustomerEMailService customerEMailService;
	private FinanceWorkFlowService financeWorkFlowService;

	private final String PROCESS_TYPE_SAVE = "Save";
	private final String PROCESS_TYPE_UPDATE = "Update";
	private final String PROCESS_TYPE_DELETE = "Delete";

	/**
	 * Method for get Collateral structure based on requested collateral type
	 * 
	 * @param collateralType
	 * @return CollateralStructure
	 */
	public CollateralStructure getCollateralType(String collateralType) {
		logger.debug("Entering");
		CollateralStructure collateralStructure = null;

		try {
			collateralStructure = collateralStructureService.getApprovedCollateralStructureByType(collateralType);
			if (collateralStructure == null) {
				collateralStructure = new CollateralStructure();

				String[] valueParam = new String[1];
				valueParam[0] = collateralType;
				collateralStructure.setReturnStatus(APIErrorHandlerService.getFailedStatus("90901", valueParam));
				return collateralStructure;
			}
		} catch (Exception e) {
			logger.error(e);
			APIErrorHandlerService.logUnhandledException(e);
			collateralStructure = new CollateralStructure();
			collateralStructure.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("Leaving");
		collateralStructure.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		return collateralStructure;
	}

	/**
	 * This method perform below operations.<br>
	 * - do Required data field setting.<br>
	 * - do create collateral action
	 * 
	 * @param collateralSetup
	 * @return CollateralSetup
	 */
	public CollateralSetup createCollateral(CollateralSetup collateralSetup) {
		logger.debug("Entering");

		CollateralSetup response = null;
		try {
			doSetRequiredValues(collateralSetup, PROCESS_TYPE_SAVE);
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);

			AuditHeader auditHeader = getAuditHeader(collateralSetup, "");
			auditHeader.setApiHeader(reqHeaderDetails);

			// call collateral create method
			auditHeader = collateralSetupService.doApprove(auditHeader);
			if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					response = new CollateralSetup();
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError()));
				}
			} else {
				response = (CollateralSetup) auditHeader.getAuditDetail().getModelData();
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			}
		} catch (BadSqlGrammarException badSqlE) {
			logger.error(badSqlE);
			response = new CollateralSetup();
			APIErrorHandlerService.logUnhandledException(badSqlE);
			if (badSqlE.getCause() != null) {
				response.setReturnStatus(
						APIErrorHandlerService.getFailedStatus("9999", badSqlE.getCause().getMessage()));
			} else {
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			}
		} catch (NumberFormatException nfe) {
			logger.error(nfe);
			APIErrorHandlerService.logUnhandledException(nfe);
			response = new CollateralSetup();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90275"));
		} catch (DataIntegrityViolationException e) {
			logger.error(e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new CollateralSetup();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90275"));
		} catch (RuntimeException e) {
			logger.error(e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new CollateralSetup();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		} catch (Exception e) {
			logger.error(e);
			APIErrorHandlerService.logUnhandledException(e);
			response = new CollateralSetup();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * This method perform below operations.<br>
	 * - do Required data field setting.<br>
	 * - do update collateral action
	 * 
	 * @param collateralSetup
	 * @return WSReturnStatus
	 */
	public WSReturnStatus updateCollateral(CollateralSetup collateralSetup) {
		logger.debug("Entering");
		try {
			doSetRequiredValues(collateralSetup, PROCESS_TYPE_UPDATE);

			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(collateralSetup, "");
			auditHeader.setApiHeader(reqHeaderDetails);

			// call collateral create method
			auditHeader = collateralSetupService.doApprove(auditHeader);

			if (auditHeader.getErrorMessage() != null) {
				for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
					return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
				}
			}
		} catch (BadSqlGrammarException badSqlE) {
			logger.error(badSqlE);
			APIErrorHandlerService.logUnhandledException(badSqlE);
			if (badSqlE.getCause() != null) {
				return APIErrorHandlerService.getFailedStatus("9999", badSqlE.getCause().getMessage());
			} else {
				return APIErrorHandlerService.getFailedStatus();
			}
		} catch (NumberFormatException nfe) {
			logger.error(nfe);
			APIErrorHandlerService.logUnhandledException(nfe);
			return APIErrorHandlerService.getFailedStatus("90275");
		} catch (DataIntegrityViolationException e) {
			logger.error(e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus("90275");
		} catch (Exception e) {
			logger.error(e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return APIErrorHandlerService.getSuccessStatus();
	}

	/**
	 * delete the customer collateral when it is not used
	 * 
	 * @param collateralSetup
	 * @return
	 */
	public WSReturnStatus deleteCollateral(CollateralSetup setup) {
		logger.debug("Entering");
		try {
			// fetch collateral details to delete
			CollateralSetup collateralSetup = collateralSetupService.getCollateralSetupByRef(setup.getCollateralRef(),
					"", false);
			if (collateralSetup != null) {
				// set required values
				doSetRequiredValues(collateralSetup, PROCESS_TYPE_DELETE);

				APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
						.get(APIHeader.API_HEADER_KEY);
				AuditHeader auditHeader = getAuditHeader(collateralSetup, "");
				auditHeader.setApiHeader(reqHeaderDetails);
				// call delete method
				auditHeader = collateralSetupService.delete(auditHeader);
				if (auditHeader.getErrorMessage() != null) {
					for (ErrorDetail errorDetail : auditHeader.getErrorMessage()) {
						return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug("Leaving");
		return APIErrorHandlerService.getSuccessStatus();
	}

	/**
	 * Fetch customer collateral list
	 * 
	 * @param custID
	 * @return
	 */
	public CollateralDetail getCollaterals(long custID, String type) {
		logger.debug("Entering");

		CollateralDetail collateralDetail = null;
		List<CollateralSetup> collaterals = null;
		List<CollateralSetup> pendingCollaterals = null;

		// fetch list of customer collaterals
		if (StringUtils.isNotBlank(type)) {
			collaterals = collateralSetupService.getPendingCollateralByCustId(custID, type);
			pendingCollaterals = new ArrayList<>();

			for (CollateralSetup collateralSetup : collaterals) {
				if (!StringUtils.equals(collateralSetup.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
					continue;
				}
				pendingCollaterals.add(collateralSetup);
			}
		} else {
			collaterals = collateralSetupService.getApprovedCollateralByCustId(custID);
		}
		if (CollectionUtils.isNotEmpty(collaterals) || CollectionUtils.isNotEmpty(pendingCollaterals)) {
			collateralDetail = new CollateralDetail();
			if (StringUtils.isNotBlank(type)) {
				collateralDetail.setCollateralSetup(pendingCollaterals);
			} else {
				collateralDetail.setCollateralSetup(collaterals);
			}
		}

		logger.debug("Leaving");
		return collateralDetail;
	}

	private void doSetRequiredValues(CollateralSetup collateralSetup, String procType) {
		doSetRequiredValues(collateralSetup, procType, false);
	}

	/**
	 * Method for prepare required data to process collateral services
	 * 
	 * @param collateralSetup
	 * @param procType
	 */
	private void doSetRequiredValues(CollateralSetup collateralSetup, String procType, boolean isPendding) {
		logger.debug("Entering");

		// user details
		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		collateralSetup.setUserDetails(userDetails);
		collateralSetup.setSourceId(APIConstants.FINSOURCE_ID_API);
		collateralSetup.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		collateralSetup.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		collateralSetup.setLastMntBy(userDetails.getUserId());
		CollateralSetup collateralDetail = null;
		// generate collateral reference in case of empty
		String collateralRef = collateralSetup.getCollateralRef();
		if (StringUtils.isBlank(collateralRef)) {
			collateralRef = ReferenceUtil.generateCollateralRef();
			collateralSetup.setCollateralRef(collateralRef);
			collateralRef = collateralSetup.getCollateralRef();
		}

		Customer customer = customerDetailsService.getCustomerByCIF(collateralSetup.getDepositorCif());
		if (customer != null) {
			collateralSetup.setDepositorId(customer.getCustID());
			collateralSetup.setDepositorCif(customer.getCustCIF());
		}

		if (StringUtils.equals(procType, PROCESS_TYPE_SAVE)) {
			collateralSetup.setNewRecord(true);
			collateralSetup.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		} else if (StringUtils.equals(procType, PROCESS_TYPE_UPDATE)) {
			collateralSetup.setNewRecord(false);
			collateralSetup.setRecordType(PennantConstants.RECORD_TYPE_UPD);

			collateralDetail = collateralSetupService.getCollateralSetupByRef(collateralRef, "", false);
			collateralSetup.setCollateralType(collateralDetail.getCollateralType());
			collateralSetup.setCollateralCcy(collateralDetail.getCollateralCcy());
			collateralSetup.setDepositorId(collateralDetail.getDepositorId());
			collateralSetup.setDepositorCif(collateralDetail.getDepositorCif());
			collateralSetup.setVersion(collateralDetail.getVersion() + 1);
			setSecurityDetails(collateralSetup, collateralDetail, isPendding);
		} else {
			collateralSetup.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		}

		// process third party collaterals
		List<CollateralThirdParty> thirdPartyCollaterals = collateralSetup.getCollateralThirdPartyList();
		if (thirdPartyCollaterals != null) {
			for (CollateralThirdParty thirdPartyColl : thirdPartyCollaterals) {
				thirdPartyColl.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				thirdPartyColl.setLastMntBy(userDetails.getUserId());
				thirdPartyColl.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				thirdPartyColl.setUserDetails(userDetails);

				// fetch customer id from cif
				Customer thrdPartyCustomer = customerDetailsService.getCustomerByCIF(thirdPartyColl.getCustCIF());
				if (thrdPartyCustomer != null) {
					thirdPartyColl.setCustomerId(thrdPartyCustomer.getCustID());
				}

				if (StringUtils.equals(procType, PROCESS_TYPE_SAVE)) {
					thirdPartyColl.setCollateralRef(collateralSetup.getCollateralRef());
					thirdPartyColl.setNewRecord(true);
					thirdPartyColl.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (StringUtils.equals(procType, PROCESS_TYPE_UPDATE)) {
					thirdPartyColl.setNewRecord(false);
					thirdPartyColl.setCollateralRef(collateralRef);
					thirdPartyColl.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					thirdPartyColl.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

					// fetch collateral third party details
					long customerId = thirdPartyColl.getCustomerId();
					CollateralThirdParty collateralThirdParty = collateralThirdPartyDAO
							.getCollThirdPartyDetails(collateralRef, customerId, "");
					if (collateralThirdParty == null) {
						thirdPartyColl.setNewRecord(true);
						thirdPartyColl.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						thirdPartyColl.setVersion(1);
					} else {
						thirdPartyColl.setVersion(collateralThirdParty.getVersion() + 1);
					}
				} else {
					thirdPartyColl.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				}
			}
		}

		// process co-owner details

		List<CoOwnerDetail> coOwnerDetails = collateralSetup.getCoOwnerDetailList();
		if (coOwnerDetails != null) {
			int seqNo = 0;
			for (CoOwnerDetail detail : coOwnerDetails) {
				detail.setCollateralRef(collateralRef);
				detail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				detail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				detail.setUserDetails(userDetails);
				detail.setLastMntBy(userDetails.getUserId());
				if (StringUtils.isNotBlank(detail.getCoOwnerCIF())) {
					Customer coOwnerCustomer = customerDetailsService.getCustomerByCIF(detail.getCoOwnerCIF());
					List<String> emails = customerEMailService.getCustEmailsByCustomer(coOwnerCustomer.getCustID());
					if (coOwnerCustomer != null) {
						if (PennantConstants.PFF_CUSTCTG_CORP.equals(coOwnerCustomer.getCustCtgCode())
								|| PennantConstants.PFF_CUSTCTG_SME.equals(coOwnerCustomer.getCustCtgCode())) {
							detail.setCoOwnerCIF(coOwnerCustomer.getCustCIF());
							detail.setCustomerId(coOwnerCustomer.getCustID());
							detail.setCoOwnerCIFName(coOwnerCustomer.getCustShrtName());
							detail.setCustomerId(coOwnerCustomer.getCustID());
							detail.setMobileNo(coOwnerCustomer.getPhoneNumber());
							if (emails != null && emails.size() > 0) {
								detail.setEmailId(emails.get(0));
							}
						} else {
							detail.setCoOwnerCIF(coOwnerCustomer.getCustCIF());
							detail.setCustomerId(coOwnerCustomer.getCustID());
							detail.setCoOwnerCIFName(coOwnerCustomer.getCustFName());
							detail.setMobileNo(coOwnerCustomer.getPhoneNumber());
							if (emails != null && emails.size() > 0) {
								detail.setEmailId(emails.get(0));
							}
						}
					}
				}
				if (StringUtils.equals(procType, PROCESS_TYPE_SAVE)) {
					detail.setCollateralRef(collateralRef);
					detail.setNewRecord(true);
					detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					detail.setCoOwnerId(++seqNo);
				} else if (StringUtils.equals(procType, PROCESS_TYPE_UPDATE)) {
					detail.setNewRecord(false);
					detail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					detail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

					// fetch collateral third party details
					int coOwnerId = detail.getCoOwnerId();
					CoOwnerDetail coOwnerDetail = coOwnerDetailDAO.getCoOwnerDetailByRef(collateralRef, coOwnerId, "");
					if (coOwnerDetail == null) {
						detail.setNewRecord(true);
						detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						detail.setVersion(1);
					} else {
						if (!detail.isBankCustomer()) {
							if (StringUtils.isBlank(detail.getCoOwnerIDType())) {
								detail.setCoOwnerIDType(coOwnerDetail.getCoOwnerIDType());
							}
							if (StringUtils.isBlank(detail.getCoOwnerIDNumber())) {
								detail.setCoOwnerIDNumber(coOwnerDetail.getCoOwnerIDNumber());
							}
						}
						detail.setVersion(coOwnerDetail.getVersion() + 1);
					}
				} else {
					detail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				}
			}
		}

		// get Collateral structure details
		CollateralStructure collateralStructure = null;
		String collateralType = collateralSetup.getCollateralType();
		if (StringUtils.isNotBlank(collateralType)) {
			collateralStructure = collateralStructureService.getApprovedCollateralStructureByType(collateralType);
		} else if (StringUtils.isNotBlank(collateralRef)) {
			CollateralSetup setup = collateralSetupService.getApprovedCollateralSetupById(collateralRef);
			if (setup != null) {
				collateralStructure = collateralStructureService
						.getApprovedCollateralStructureByType(setup.getCollateralType());
			}
		}
		collateralSetup.setCollateralStructure(collateralStructure);

		// process Extended field details
		int totalUnits = 0;
		BigDecimal totalValue = BigDecimal.ZERO;
		List<ExtendedField> extendedFields = collateralSetup.getExtendedDetails();
		if (StringUtils.equals(procType, PROCESS_TYPE_DELETE)) {
			if (collateralSetup.getExtendedFieldRenderList() != null
					&& !collateralSetup.getExtendedFieldRenderList().isEmpty()) {
				for (ExtendedFieldRender exdFieldRender : collateralSetup.getExtendedFieldRenderList()) {
					exdFieldRender.setReference(collateralRef);
					exdFieldRender.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					exdFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					exdFieldRender.setLastMntBy(userDetails.getUserId());
					exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				}
			}
		}
		if (extendedFields != null) {
			List<ExtendedFieldRender> extendedFieldRenderList = new ArrayList<ExtendedFieldRender>();
			int seqNo = 0;
			for (ExtendedField extendedField : extendedFields) {
				ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
				exdFieldRender.setReference(collateralSetup.getCollateralRef());
				exdFieldRender.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				exdFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				exdFieldRender.setLastMntBy(userDetails.getUserId());

				if (StringUtils.equals(procType, PROCESS_TYPE_SAVE)) {
					exdFieldRender.setSeqNo(++seqNo);
					exdFieldRender.setNewRecord(true);
					exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);

					Map<String, Object> mapValues = new HashMap<String, Object>();
					for (ExtendedFieldData extFieldData : extendedField.getExtendedFieldDataList()) {
						mapValues.put(extFieldData.getFieldName(), extFieldData.getFieldValue());
					}

					int noOfUnits = 0;
					BigDecimal curValue = BigDecimal.ZERO;

					try {
						// Setting Number of units
						if (mapValues.containsKey("NOOFUNITS")
								&& !StringUtils.isEmpty(mapValues.get("NOOFUNITS").toString())) {
							noOfUnits = Integer.parseInt(mapValues.get("NOOFUNITS").toString());
							totalUnits = totalUnits + noOfUnits;
						}

						// Setting Total Value
						if (mapValues.containsKey("UNITPRICE")
								&& !StringUtils.isEmpty(mapValues.get("UNITPRICE").toString())) {
							curValue = new BigDecimal(mapValues.get("UNITPRICE").toString());
							totalValue = totalValue.add(curValue.multiply(new BigDecimal(noOfUnits)));
						}
					} catch (NumberFormatException nfe) {
						APIErrorHandlerService.logUnhandledException(nfe);
						logger.error("Exception", nfe);
						throw nfe;
					}

					exdFieldRender.setMapValues(mapValues);
					extendedFieldRenderList.add(exdFieldRender);
				} else if (StringUtils.equals(procType, PROCESS_TYPE_UPDATE)) {
					boolean isSeqFound = false;
					exdFieldRender.setNewRecord(false);
					exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_UPD);

					Map<String, Object> mapValues = new HashMap<String, Object>();
					for (ExtendedFieldData extFieldData : extendedField.getExtendedFieldDataList()) {
						mapValues.put(extFieldData.getFieldName(), extFieldData.getFieldValue());

						if (StringUtils.equalsIgnoreCase(extFieldData.getFieldName(), "SeqNo")) {
							exdFieldRender
									.setSeqNo(Integer.valueOf(Objects.toString(extFieldData.getFieldValue(), "")));
							isSeqFound = true;
						}
					}
					if (!isSeqFound) {
						ExtendedFieldHeader extendedFieldHeader = collateralSetup.getCollateralStructure()
								.getExtendedFieldHeader();
						StringBuilder tableName = new StringBuilder();
						tableName.append(extendedFieldHeader.getModuleName());
						tableName.append("_");
						tableName.append(extendedFieldHeader.getSubModuleName());
						tableName.append("_ED");

						exdFieldRender.setSeqNo(
								extendedFieldRenderDAO.getMaxSeq(collateralRef, tableName.toString(), "") + (++seqNo));
						exdFieldRender.setNewRecord(true);
						exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);

						int no = extendedFieldRenderDAO.getMaxSeq(collateralRef, tableName.toString(), "");
						if (no != 0) {
							exdFieldRender.setSeqNo(no);
							exdFieldRender.setNewRecord(false);
							exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_UPD);
							exdFieldRender.setWorkflowId(collateralSetup.getWorkflowId());
						} else {
							exdFieldRender.setSeqNo(++seqNo);
							exdFieldRender.setNewRecord(true);
							exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						}
					}
					boolean noUnitCmplted = false;
					int noOfUnits = 0;
					boolean unitPriceCmplted = false;
					BigDecimal curValue = BigDecimal.ZERO;

					try {
						// Setting Number of units
						if (!noUnitCmplted && mapValues.containsKey("NOOFUNITS")) {
							noOfUnits = Integer.parseInt(mapValues.get("NOOFUNITS").toString());
							totalUnits = totalUnits + noOfUnits;
							noUnitCmplted = true;
						}

						// Setting Total Value
						if (!unitPriceCmplted && mapValues.containsKey("UNITPRICE")) {
							curValue = new BigDecimal(mapValues.get("UNITPRICE").toString());
							totalValue = totalValue.add(curValue.multiply(new BigDecimal(noOfUnits)));
							unitPriceCmplted = true;
						}
					} catch (NumberFormatException nfe) {
						APIErrorHandlerService.logUnhandledException(nfe);
						logger.error("Exception", nfe);
						throw nfe;
					}

					exdFieldRender.setMapValues(mapValues);
					extendedFieldRenderList.add(exdFieldRender);
				} else {
					exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				}
			}
			collateralSetup.setCollateralValue(totalValue);
			collateralSetup.setExtendedFieldRenderList(extendedFieldRenderList);
		}

		if (collateralStructure != null) {
			CustomerDetails customerDetails = customerDetailsService.getCustomerById(collateralSetup.getDepositorId());
			if (customerDetails != null) {
				collateralSetup.setCustomerDetails(customerDetails);
			}
			// calculate BankLTV
			if (StringUtils.equals(collateralStructure.getLtvType(), CollateralConstants.FIXED_LTV)) {
				collateralSetup.setBankLTV(collateralStructure.getLtvPercentage());
			} else if (StringUtils.equals(collateralStructure.getLtvType(), CollateralConstants.VARIABLE_LTV)) {
				Object ruleResult = null;
				Map<String, Object> declaredMap = collateralSetup.getCustomerDetails().getCustomer()
						.getDeclaredFieldValues();
				declaredMap.put("collateralType", collateralSetup.getCollateralType());
				declaredMap.put("collateralCcy", collateralSetup.getCollateralCcy());
				try {
					ruleResult = RuleExecutionUtil.executeRule(collateralStructure.getSQLRule(), declaredMap,
							collateralSetup.getCollateralCcy(), RuleReturnType.DECIMAL);
				} catch (Exception e) {
					APIErrorHandlerService.logUnhandledException(e);
					logger.error("Exception: ", e);
					ruleResult = "0";
				}
				collateralSetup
						.setBankLTV(ruleResult == null ? BigDecimal.ZERO : new BigDecimal(ruleResult.toString()));
			}

			// calculate Bank Valuation
			BigDecimal ltvValue = collateralSetup.getBankLTV();
			if (collateralSetup.getSpecialLTV() != null
					&& collateralSetup.getSpecialLTV().compareTo(BigDecimal.ZERO) > 0) {
				ltvValue = collateralSetup.getSpecialLTV();
			}
			if (collateralSetup.getSpecialLTV().compareTo(BigDecimal.ZERO) == 0 && collateralDetail != null) {
				collateralSetup.setSpecialLTV(collateralDetail.getSpecialLTV());
			}
			if (StringUtils.isBlank(collateralSetup.getReviewFrequency()) && collateralDetail != null) {
				collateralSetup.setReviewFrequency(collateralDetail.getReviewFrequency());
			}
			if (collateralSetup.getSpecialLTV().compareTo(BigDecimal.ZERO) == 0 && collateralDetail != null) {
				collateralSetup.setSpecialLTV(collateralDetail.getSpecialLTV());
			}
			if (StringUtils.isBlank(collateralSetup.getReviewFrequency()) && collateralDetail != null) {
				collateralSetup.setReviewFrequency(collateralDetail.getReviewFrequency());
			}
			BigDecimal colValue = collateralSetup.getCollateralValue().multiply(ltvValue).divide(new BigDecimal(100), 0,
					RoundingMode.HALF_DOWN);
			if (collateralSetup.getMaxCollateralValue().compareTo(BigDecimal.ZERO) > 0
					&& colValue.compareTo(collateralSetup.getMaxCollateralValue()) > 0) {
				colValue = collateralSetup.getMaxCollateralValue();
			}
			collateralSetup.setBankValuation(colValue);
			collateralSetup.setCollateralStructure(collateralStructure);
		}

		// process document details
		List<DocumentDetails> documentDetails = collateralSetup.getDocuments();
		if (documentDetails != null) {
			for (DocumentDetails detail : documentDetails) {
				detail.setDocModule(CollateralConstants.MODULE_NAME);
				detail.setUserDetails(collateralSetup.getUserDetails());
				detail.setReferenceId(collateralRef);

				if (StringUtils.equals(procType, PROCESS_TYPE_SAVE)) {
					detail.setNewRecord(true);
					detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					detail.setDocRefId(null);
				} else if (StringUtils.equals(procType, PROCESS_TYPE_UPDATE)) {
					String category = detail.getDocCategory();
					String module = CollateralConstants.MODULE_NAME;

					DocumentDetails docDetails = documentDetailsDAO.getDocumentDetails(collateralRef, category, module,
							"");
					if (docDetails != null) {
						detail.setDocId(docDetails.getDocId());
						detail.setNewRecord(false);
						detail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					} else {
						detail.setNewRecord(true);
						detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					}
				} else {
					detail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				}
			}
		}
		logger.debug("Leaving");
	}

	public WSReturnStatus pendingUpdateCollateral(CollateralSetup collateralSetup) {
		logger.debug(Literal.ENTERING);

		try {
			doSetRequiredValues(collateralSetup, PROCESS_TYPE_UPDATE, true);
			collateralSetup.setRecordType(PennantConstants.RECORD_TYPE_NEW);

			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			AuditHeader auditHeader = getAuditHeader(collateralSetup, "");
			auditHeader.setApiHeader(reqHeaderDetails);

			auditHeader = collateralSetupService.saveOrUpdate(auditHeader);

			if (CollectionUtils.isNotEmpty(auditHeader.getErrorMessage())) {
				ErrorDetail errorDetail = auditHeader.getErrorMessage().get(0);
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		} catch (BadSqlGrammarException badSqlE) {
			logger.error(Literal.EXCEPTION, badSqlE);
			APIErrorHandlerService.logUnhandledException(badSqlE);
			if (badSqlE.getCause() != null) {
				return APIErrorHandlerService.getFailedStatus("9999", badSqlE.getCause().getMessage());
			} else {
				return APIErrorHandlerService.getFailedStatus();
			}
		} catch (NumberFormatException nfe) {
			logger.error(Literal.EXCEPTION, nfe);
			APIErrorHandlerService.logUnhandledException(nfe);
			return APIErrorHandlerService.getFailedStatus("90275");
		} catch (DataIntegrityViolationException e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus("90275");
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			return APIErrorHandlerService.getFailedStatus();
		}

		logger.debug(Literal.LEAVING);
		return APIErrorHandlerService.getSuccessStatus();
	}

	private void setSecurityDetails(CollateralSetup setup, CollateralSetup aSetup, boolean isPendding) {
		setup.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		setup.setWorkflowId(0);

		if (isPendding) {
			setup.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
			if (aSetup.getFinReference() != null && aSetup.getWorkflowId() == 0) {
				FinanceWorkFlow financeWorkFlow = financeWorkFlowService.getApprovedFinanceWorkFlowById(
						aSetup.getCollateralType(), FinServiceEvent.ORG, PennantConstants.WORFLOW_MODULE_COLLATERAL);

				if (financeWorkFlow != null) {
					WorkFlowDetails workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkFlow.getWorkFlowType());
					if (workFlowDetails != null) {
						setup.setWorkflowId(workFlowDetails.getWorkFlowId());
					}
				} else {
					setup.setWorkflowId(aSetup.getWorkflowId());
				}
			} else {
				setup.setWorkflowId(aSetup.getWorkflowId());
			}

			setup.setNextRoleCode(aSetup.getNextRoleCode());
			setup.setNextTaskId(aSetup.getNextTaskId());
			setup.setTaskId(aSetup.getTaskId());
			setup.setRoleCode(aSetup.getRoleCode());
		}
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param collateralSetup
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CollateralSetup collateralSetup, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, collateralSetup.getBefImage(), collateralSetup);
		return new AuditHeader(collateralSetup.getCollateralRef(), collateralSetup.getCollateralRef(), null, null,
				auditDetail, collateralSetup.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	public void setCollateralStructureService(CollateralStructureService collateralStructureService) {
		this.collateralStructureService = collateralStructureService;
	}

	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public void setCollateralThirdPartyDAO(CollateralThirdPartyDAO collateralThirdPartyDAO) {
		this.collateralThirdPartyDAO = collateralThirdPartyDAO;
	}

	public void setCoOwnerDetailDAO(CoOwnerDetailDAO coOwnerDetailDAO) {
		this.coOwnerDetailDAO = coOwnerDetailDAO;
	}

	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	public void setExtendedFieldRenderDAO(ExtendedFieldRenderDAO extendedFieldRenderDAO) {
		this.extendedFieldRenderDAO = extendedFieldRenderDAO;
	}

	public void setCustomerEMailService(CustomerEMailService customerEMailService) {
		this.customerEMailService = customerEMailService;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

}
package com.pennant.webui.financemanagement.insurance;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.zkoss.util.media.Media;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.insurance.InsuranceDetails;
import com.pennant.backend.model.insurance.InsurancePaymentInstructions;
import com.pennant.backend.model.systemmasters.VASProviderAccDetail;
import com.pennant.backend.service.insurance.InsuranceDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.util.PennantAppUtil;
import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.model.DataEngineLog;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class InsuranceFileImportService {
	private static final Logger logger = Logger.getLogger(InsuranceFileImportService.class);

	private DataSource dataSource;
	private InsuranceDetailService insuranceDetailService;
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	//Constants
	private static final String DELIVERED	= "DELIVERED";
	private static final String RTO	= "RTO";
	//private static final String OUTFORDELIVERY	= "OUTFORDELIVERY";

	/******************Processing the Insurance details file Start********************************/
	/**
	 * Processing the uploaded file
	 * @throws Exception 
	 */
	public void processFile(LoggedInUser userDetails, DataEngineStatus status, Media media, long providerId) throws Exception {
		logger.debug(Literal.ENTERING);
		
		String configName = status.getName();
		status.reset();
		status.setFileName(media.getName());
		status.setRemarks("Initiated file reading...");

		DataEngineImport dataEngine;
		dataEngine = new DataEngineImport(getDataSource(), userDetails.getUserId(), App.DATABASE.name(), true, DateUtility.getAppDate(), status);
		dataEngine.setMedia(media);
		dataEngine.importData(configName);

		do {
			if (ExecutionStatus.S.name().equals(status.getStatus()) || ExecutionStatus.F.name().equals(status.getStatus())) {
				processData(userDetails, providerId, status);
				break;
			}
		} while (ExecutionStatus.S.name().equals(status.getStatus()) || ExecutionStatus.F.name().equals(status.getStatus()));

		logger.info(configName + " file processing completed");
		logger.debug(Literal.LEAVING);
	}

	/*
	 * Processing the Imported data
	 */
	private void processData(LoggedInUser userDetails, long providerId, DataEngineStatus status) {
		logger.debug(Literal.ENTERING);

		List<InsuranceDetails> insuranceDetails = getInsFileImportData(status.getId());
		if (CollectionUtils.isEmpty(insuranceDetails)) {
			logger.debug("Insurance Details are empty.");
			return;
		}

		int ccyForamt =  getCcyFormat();
		VASProviderAccDetail providerAccDetail = getVASProviderAccDetByPRoviderId(providerId);
		
		String oldRemarks = status.getRemarks();
		status.setStatus(ExecutionStatus.I.name());
		status.setRemarks("File Reading completed, Initiated the data processing...");
		int totalRecords = insuranceDetails.size();
		int failureRecords = 0;
				
		//Processing the data
		for (InsuranceDetails detailFromFile : insuranceDetails) {
			try {
				status.setStatus(ExecutionStatus.E.name());
				
				boolean isExists = false;
				boolean updateVas = false;
				InsuranceDetails insuranceDetail = new InsuranceDetails();

				String vasReference = detailFromFile.getReference();
				
				if (StringUtils.trimToNull(vasReference) == null) {
					throw new Exception("Reference is mandatory.:" + vasReference);
				}
				
				VASRecording vasRecording = getInsuranceDetailService().getVASRecording(vasReference, VASConsatnts.STATUS_NORMAL);
				if (vasRecording == null) {
					throw new Exception("Insurace details are not available for the Reference :" + vasReference);
				}

				InsuranceDetails existingTempDetails = getInsuranceDetailService().getInsurenceDetailsByRef(vasReference, TableType.TEMP_TAB.getSuffix());
				
				if (existingTempDetails != null) {
					throw new Exception("Record is in maintaince for the Reference :" + vasReference);
				}
				
				InsuranceDetails existingDetails = getInsuranceDetailService().getInsurenceDetailsByRef(vasReference, TableType.MAIN_TAB.getSuffix());
				if (existingDetails != null) {
					isExists = true;
					BeanUtils.copyProperties(existingDetails, insuranceDetail);
				} else if (providerAccDetail == null) {
					throw new Exception("Provider Account details are not available. At the Refence :" + vasReference);
				}
				
				//LoggedInUser details
				insuranceDetail.setUserDetails(userDetails);
				
				//Entity code
				insuranceDetail.setEntityCode(providerAccDetail.getEntityCode());

				//Reference
				insuranceDetail.setReference(vasReference);
				
				//Finreference
				insuranceDetail.setFinReference(detailFromFile.getFinReference());
				
				// Issuance date less than AppDate
				Date issuanceDate = detailFromFile.getIssuanceDate();
				if (insuranceDetail.getIssuanceDate() == null) {
					if (DateUtility.compare(issuanceDate, DateUtility.getAppDate()) > 0) {
						throw new Exception("Issuance date Should not be greater than the application date. At the Refence :" + vasReference);
					}
					insuranceDetail.setIssuanceDate(issuanceDate);
				}  

				// InsuranceStartDate greater than issuance date
				if (insuranceDetail.getStartDate() == null) {
					Date startDate = detailFromFile.getStartDate();
					if (startDate != null && (DateUtility.compare(insuranceDetail.getIssuanceDate(), startDate) > 0)) {
						throw new Exception("Insurance Start date should not be less than issuance date. At the Refence :" + vasReference);
					}
					insuranceDetail.setStartDate(startDate);
				}

				// InsuranceEndDate
				if (insuranceDetail.getEndDate() == null) {
					Date endDate = detailFromFile.getEndDate();
					if (endDate != null && (DateUtility.compare(insuranceDetail.getIssuanceDate(), DateUtility.getAppDate()) > 0)) {
						throw new Exception("Insurance End date should be greater than the application date. At the Refence :" + vasReference);
					}
					insuranceDetail.setEndDate(endDate);
				}
				
				// Insurance Term
				if (insuranceDetail.getTerm() == 0) {
					if (StringUtils.isNotBlank(detailFromFile.getTermF())) {
						insuranceDetail.setTerm(Integer.valueOf(detailFromFile.getTermF()));
					}
				}

				//coverage Amount
				if ((BigDecimal.ZERO.compareTo(insuranceDetail.getCoverageAmount()) == 0) && (BigDecimal.ZERO.compareTo(detailFromFile.getCoverageAmount()) == -1)) {
					insuranceDetail.setCoverageAmount(PennantAppUtil.unFormateAmount(detailFromFile.getCoverageAmount(), ccyForamt));
				}
				
				//Policy Number
				if (StringUtils.trimToNull(insuranceDetail.getPolicyNumber()) == null) {
					insuranceDetail.setPolicyNumber(detailFromFile.getPolicyNumber());
				}
				
				// Issuance status
				String issuanceStatus = detailFromFile.getIssuanceStatus();
				if (InsuranceConstants.PENDING.equals(vasRecording.getStatus())) {
					if (InsuranceConstants.PENDING.equals(issuanceStatus) || InsuranceConstants.DISCREPENT.equals(issuanceStatus) || InsuranceConstants.DECLINE.equals(issuanceStatus) || InsuranceConstants.REJECT.equals(issuanceStatus)) {
						insuranceDetail.setIssuanceStatus(issuanceStatus);
					}
					
					if (InsuranceConstants.ISSUED.equals(issuanceStatus) && (StringUtils.trimToNull(detailFromFile.getPolicyNumber()) == null)) {
						throw new Exception("Policy Number is mandatory if the Issuance status is received as ISSUED. At the Refence :" + vasReference);
					}
					
					if (InsuranceConstants.ISSUED.equals(issuanceStatus)) {
						insuranceDetail.setIssuanceStatus(InsuranceConstants.ISSUED);
						vasRecording.setStatus(InsuranceConstants.ACTIVE);
						updateVas = true;
					}
				}
				if (isExists && InsuranceConstants.CANCEL.equals(issuanceStatus)) {//FIXME to chaitanyaaa
					if (InsuranceConstants.ISSUED.equals(insuranceDetail.getIssuanceStatus()) && InsuranceConstants.ACTIVE.equals(insuranceDetail.getPolicyStatus())) {
						insuranceDetail.setIssuanceStatus(InsuranceConstants.CANCEL);
					}
				}
				
				//Policy Status//GDP
				if (InsuranceConstants.PENDING.equals(vasRecording.getStatus()) 
						&& InsuranceConstants.PENDING.equals(detailFromFile.getIssuanceStatus())) {
					insuranceDetail.setPolicyStatus(InsuranceConstants.ACTIVE);
					insuranceDetail.setIssuanceStatus(InsuranceConstants.ISSUED);
				}
				
				if (InsuranceConstants.ACTIVE.equals(vasRecording.getStatus()) 
						&& InsuranceConstants.ISSUED.equals(detailFromFile.getIssuanceStatus()) && InsuranceConstants.CANCELLED.equals(detailFromFile.getIssuanceStatus())) {
					insuranceDetail.setPolicyStatus(InsuranceConstants.CANCELLED);
					insuranceDetail.setIssuanceStatus(InsuranceConstants.CANCEL);
				}
				
				if ((InsuranceConstants.ACTIVE.equals(detailFromFile.getPolicyStatus())
						&& InsuranceConstants.ISSUED.equals(detailFromFile.getIssuanceStatus()))) {
					insuranceDetail.setPolicyStatus(detailFromFile.getPolicyStatus());
					insuranceDetail.setIssuanceStatus(InsuranceConstants.ISSUED);
				}
				
				
				//Partner received date
				if (insuranceDetail.getPartnerReceivedDate() == null) {
					if (detailFromFile.getPartnerReceivedDate() == null) {
						if(InsuranceConstants.ISSUED.equals(insuranceDetail.getIssuanceStatus())){
							insuranceDetail.setPartnerReceivedDate(insuranceDetail.getIssuanceDate());
						}
					} else {
						if (DateUtility.compare(detailFromFile.getPartnerReceivedDate(), DateUtility.getAppDate()) > 0) {
							throw new Exception("Partner received date Should not be greater than the Application date. At the Refence :" + vasReference);
						} else {
							insuranceDetail.setPartnerReceivedDate(detailFromFile.getPartnerReceivedDate());
						}
					}
				}
				
				//Insurance Partner Premium
				if ((!InsuranceConstants.RECON_STATUS_AUTO.equals(insuranceDetail.getReconStatus())) && (BigDecimal.ZERO.compareTo(detailFromFile.getPartnerPremium()) < 0) && (StringUtils.trimToNull(detailFromFile.getPolicyNumber()) == null)) {
					  throw new Exception("Policy Number is mandatory if the Recon Status is N and Partner Premium amount greater than Zero. At the Refence :" + vasReference);
				} else if ((!InsuranceConstants.RECON_STATUS_AUTO.equals(insuranceDetail.getReconStatus())) && (BigDecimal.ZERO.compareTo(detailFromFile.getPartnerPremium()) == -1)) {
					insuranceDetail.setPartnerPremium(PennantAppUtil.unFormateAmount(detailFromFile.getPartnerPremium(), ccyForamt));
				}
				
				//Dispatch Date Attempts
				Date dispDateAttemt1 = detailFromFile.getDispatchDateAttempt1();
				Date dispDateAttemt2 = detailFromFile.getDispatchDateAttempt2();
				Date dispDateAttemt3 = detailFromFile.getDispatchDateAttempt3();
				
				if (!DELIVERED.equals(detailFromFile.getDispatchStatusF())) {
					//Dispatch Date Attempt1
					if (insuranceDetail.getDispatchDateAttempt1() == null) {
						insuranceDetail.setDispatchDateAttempt1(dispDateAttemt1);
					}

					// Dispatch Date Attempt2
					if (insuranceDetail.getDispatchDateAttempt2() == null) {
						insuranceDetail.setDispatchDateAttempt2(dispDateAttemt2);
					}

					// Dispatch Date Attempt3
					if (dispDateAttemt3 != null) {
						insuranceDetail.setDispatchDateAttempt3(dispDateAttemt3);
					}
				}
			
				// AWB No
				if (StringUtils.trimToNull(detailFromFile.getaWBNoF()) != null) {
					if (dispDateAttemt1 != null && dispDateAttemt2 != null && dispDateAttemt3 != null) {
						insuranceDetail.setaWBNo3(detailFromFile.getaWBNoF());
					}

					if (dispDateAttemt1 != null && dispDateAttemt2 != null && insuranceDetail.getaWBNo2() != null) {
						insuranceDetail.setaWBNo2(detailFromFile.getaWBNoF());
					}

					if (dispDateAttemt1 != null && insuranceDetail.getaWBNo1() != null) {
						insuranceDetail.setaWBNo1(detailFromFile.getaWBNoF());
					}
				}
				
				// Dispatch Status
				String dispStatus = detailFromFile.getDispatchStatusF();
				if (dispDateAttemt1 != null || dispDateAttemt2 != null || dispDateAttemt3 != null) {
					if (StringUtils.trimToNull(dispStatus) == null) {
						throw new Exception("Dispatch Status is mandatory. if all Dispatch Date is provided. At the Refence :" + vasReference);
					}
				}
				if (InsuranceConstants.ACTIVE.equals(vasRecording.getStatus()) && InsuranceConstants.ISSUED.equals(insuranceDetail.getIssuanceStatus())) {
					if (!DELIVERED.equals(dispStatus) && (StringUtils.trimToNull(dispStatus) != null)) {
						if (StringUtils.trimToNull(insuranceDetail.getDispatchStatus1()) == null) {
							insuranceDetail.setDispatchStatus1(dispStatus);
						} else if (StringUtils.trimToNull(insuranceDetail.getDispatchStatus2()) == null) {
							insuranceDetail.setDispatchStatus2(dispStatus);
						} else {
							insuranceDetail.setDispatchStatus3(dispStatus);
						}
					}
				}
				
				// Reason of RTO
				String reasonofRTO = detailFromFile.getReasonOfRTOF();
				if (RTO.equals(dispStatus)) {
					if (StringUtils.trimToNull(reasonofRTO) == null) {
						throw new Exception("Reason of RTO is mandatory if Dispatch Status is RTO. At the Refence :" + vasReference);
					}
				}
				
				if (StringUtils.trimToNull(reasonofRTO) != null) {
					if (dispDateAttemt1 != null && dispDateAttemt2 != null && dispDateAttemt3 != null) {
						insuranceDetail.setReasonOfRTO3(reasonofRTO);
					}

					if (dispDateAttemt1 != null && dispDateAttemt2 != null
							&& insuranceDetail.getReasonOfRTO2() != null) {
						insuranceDetail.setReasonOfRTO2(reasonofRTO);
					}

					if (dispDateAttemt1 != null && insuranceDetail.getReasonOfRTO1() != null) {
						insuranceDetail.setReasonOfRTO1(reasonofRTO);
					}
				}
				
				// Medical Status
				if (PennantConstants.YES.equals(detailFromFile.getMedicalStatusF())) {
					insuranceDetail.setMedicalStatus(true);
				} else if (PennantConstants.NO.equals(detailFromFile.getMedicalStatusF())) {
					insuranceDetail.setMedicalStatus(false);
				}
				
				//Insurance Pendency  Reason Category
				String insStatus = insuranceDetail.getIssuanceStatus();
				String insPendencyCategory = detailFromFile.getPendencyReasonCategory();
				
				if (InsuranceConstants.PENDING.equals(insStatus) || InsuranceConstants.DISCREPENT.equals(insStatus) || InsuranceConstants.DECLINE.equals(insStatus)
						|| InsuranceConstants.REJECT.equals(insStatus) || InsuranceConstants.CANCEL.equals(insStatus)) {
					insuranceDetail.setPendencyReasonCategory(insPendencyCategory);
				}
				
				//Insurance Pendency Reason
				if (StringUtils.trimToNull(insPendencyCategory) != null && StringUtils.trimToNull(detailFromFile.getPendencyReason()) != null) {
					insuranceDetail.setPendencyReason(detailFromFile.getPendencyReason());
				}
				
				//Insurer Pendency Resolution requirement
				if (StringUtils.trimToNull(insPendencyCategory) != null) {
					if (PennantConstants.YES.equals(detailFromFile.getPendencyResReqF())) {
						insuranceDetail.setInsPendencyResReq(true);
					} else if (PennantConstants.NO.equals(detailFromFile.getPendencyResReqF())) {
						insuranceDetail.setInsPendencyResReq(false);
					}
				}
				
				//FPR
				insuranceDetail.setfPR(detailFromFile.getfPR());
				
				//FORM_HANDOVER_DATE
				if ((insuranceDetail.getFormHandoverDate() == null) && (detailFromFile.getFormHandoverDate() != null)) {
					insuranceDetail.setFormHandoverDate(detailFromFile.getFormHandoverDate());
				}
				
				//Manual or Auto reconcile process
				if (!isExists) {
					BigDecimal policyAmt = PennantAppUtil.unFormateAmount(detailFromFile.getPartnerPremium(), ccyForamt);
					BigDecimal differenceAmount = vasRecording.getFee().subtract(policyAmt);
					BigDecimal adjAmount = differenceAmount;
					
					if (differenceAmount.compareTo(BigDecimal.ZERO) < 0) {
						differenceAmount = differenceAmount.negate();
					}
					
					if ((differenceAmount.compareTo(providerAccDetail.getReconciliationAmount()) < 0)) {
						insuranceDetail.setReconStatus(InsuranceConstants.RECON_STATUS_AUTO);
						insuranceDetail.setAdjAmount(adjAmount);
						setWorkFlowDetails(insuranceDetail);
					} else {
						insuranceDetail.setReconStatus(InsuranceConstants.RECON_STATUS_MANUAL);
						setWorkFlowDetails(insuranceDetail);
					}
					insuranceDetail.setTolaranceAmount(providerAccDetail.getReconciliationAmount());
				}
				
				//Save/Updating the insurance data
				insuranceDetail.setvASProviderId(providerId);
				if (isExists) {
					getInsuranceDetailService().updateInsuranceDetails(insuranceDetail,TableType.MAIN_TAB.getSuffix());
				} else {
					if (InsuranceConstants.RECON_STATUS_AUTO.equals(insuranceDetail.getReconStatus())) {
						//Accounting execution
						long linkedTranId = getInsuranceDetailService().executeInsPartnerAccountingProcess(insuranceDetail, vasRecording);
						insuranceDetail.setLinkedTranId(linkedTranId);
						//Data saving
						getInsuranceDetailService().saveInsuranceDetails(insuranceDetail, TableType.MAIN_TAB.getSuffix());
					} else if (InsuranceConstants.RECON_STATUS_MANUAL.equals(insuranceDetail.getReconStatus())) {
						getInsuranceDetailService().saveInsuranceDetails(insuranceDetail, TableType.TEMP_TAB.getSuffix());
					}
				}
				
				//Updating the vas status
				if (updateVas) {
					getInsuranceDetailService().updateVasStatus(vasRecording.getStatus(), vasRecording.getVasReference());
				}
			} catch (Throwable e) {
				logger.debug(Literal.EXCEPTION, e);
				updateLog(status.getId(), detailFromFile.getReference(), "F", e.getMessage());
				failureRecords++;
			}
		}
		
		//Status logging
		StringBuilder remarks = new StringBuilder();
		if (totalRecords > 0) {
			if (failureRecords > 0) {
				remarks.append("Completed with exceptions, total Records: ");
				remarks.append(status.getTotalRecords());
				remarks.append(", Success: ");
				remarks.append(status.getSuccessRecords() - failureRecords);
				remarks.append(", Failure: ");
				remarks.append(status.getFailedRecords() + failureRecords);
				status.setSuccessRecords(status.getSuccessRecords() - failureRecords);
				status.setFailedRecords(status.getFailedRecords() + failureRecords);
				status.setRemarks(remarks.toString());
			} else {
				status.setRemarks(oldRemarks);
			}
			setExceptionLog(status);
			updateStatus(status);
			status.setStatus(ExecutionStatus.S.name());
		} 
		logger.debug(Literal.LEAVING);
	}
	/******************Processing the Insurance details file End********************************/
	
	/******************Processing the Insurance Payment Uploads Start 
	 * @param insPaymentUploadDialogCtrl ***************************/
	public void processPaymentUploadsFile(long userId, DataEngineStatus status, Media media, long providerId, String entityCode, InsPaymentUploadDialogCtrl dialogCtrl) throws Exception {
		logger.debug(Literal.ENTERING);
		
		dialogCtrl.setPaymentInstructionsFromFile(null);
		String configName = status.getName();
		status.reset();
		status.setFileName(media.getName());
		status.setRemarks("Initiated file reading...");

		DataEngineImport dataEngine;
		dataEngine = new DataEngineImport(getDataSource(), userId, App.DATABASE.name(), true, DateUtility.getAppDate(),
				status);
		dataEngine.setMedia(media);
		dataEngine.importData(configName);

		do {
			if ("S".equals(status.getStatus()) || "F".equals(status.getStatus())) {
				processPaymentUploadsData(providerId, entityCode ,status, dialogCtrl);
				break;
			}
		} while ("S".equals(status.getStatus()) || "F".equals(status.getStatus()));

		logger.info(configName + " file processing completed");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Processing the payment upload
	 * @param providerId
	 * @param status
	 * @param dialogCtrl
	 */
	private void processPaymentUploadsData(long providerId,String entityCode ,DataEngineStatus status, InsPaymentUploadDialogCtrl dialogCtrl) {
		logger.debug(Literal.ENTERING);

		List<InsuranceDetails> insuranceDetails = getInsPaymentsFileImportData(status.getId());
		if (CollectionUtils.isEmpty(insuranceDetails)) {
			logger.debug("Insurance Details are empty.");
			return;
		}

		VASProviderAccDetail providerAccDetail = getVASProviderAccDetByPRoviderId(providerId, entityCode);
		
		if (providerAccDetail == null) {
			logger.debug("Provider account Details are empty.");
			return;
		}
		
		String oldRemarks = status.getRemarks();
		status.setStatus(ExecutionStatus.I.name());
		status.setRemarks("File Reading completed, Initiated the data processing...");
		int totalRecords = insuranceDetails.size();
		int failureRecords = 0;
		int sucessRecords = 0;	
		
		BigDecimal totPaybleAmt = BigDecimal.ZERO;
		LinkedHashMap<Long, String> adviseRefMap = new LinkedHashMap<>();
		List<VASRecording> vasRecordingsList = new ArrayList<>();
		
		//Processing the data
		for (InsuranceDetails detailFromFile : insuranceDetails) {
			try {
				status.setStatus(ExecutionStatus.E.name());
				VASRecording vasRecording = getInsuranceDetailService().getVASRecording(detailFromFile.getReference(), VASConsatnts.STATUS_NORMAL);
				
				if (vasRecording == null) {
					throw new Exception("Insurace details are not available for the Reference :" + detailFromFile.getReference());
				}
				
				if (vasRecording.getPaymentInsId() == 0) {
					vasRecording.setPaymentInsId(Long.MIN_VALUE);
				}
				
				if (vasRecording.getPaymentInsId() != Long.MIN_VALUE) {
					throw new Exception("Payment already uploaded for this reference :" + detailFromFile.getReference());
				}
				
				if(!StringUtils.equals(entityCode, vasRecording.getEntityCode())){
					throw new Exception("Payment already uploaded for this reference :" + detailFromFile.getReference());
				}
				
				if (!InsuranceConstants.ACTIVE.equals(vasRecording.getStatus())) {
					throw new Exception("Insurace is in inactive status at the Reference :" + detailFromFile.getReference());
				}
				
				InsuranceDetails insuranceDetail = getInsuranceDetailService().getInsurenceDetailsByRef(vasRecording.getVasReference(), "");
				
				if (insuranceDetail == null) {
					InsuranceDetails tempDetail = getInsuranceDetailService().getInsurenceDetailsByRef(vasRecording.getVasReference(), "_Temp");
					if (tempDetail != null) {
						throw new Exception("The uploaded insurance is not reconciled :" + detailFromFile.getReference());
					} else {
						throw new Exception("Insurace details are not uploaded for the Reference :"+ detailFromFile.getReference());
					}
				}
				
				if (!InsuranceConstants.RECON_STATUS_AUTO.equals(insuranceDetail.getReconStatus())) {
					throw new Exception("Insurace is not Auto Reconciled for the Reference :" + detailFromFile.getReference());
				}
				
				if (providerId != insuranceDetail.getvASProviderId()) {
					throw new Exception("Insurace is not related to selected Company Code for the Reference :" + detailFromFile.getReference());
				}
				
				if (providerAccDetail.isReceivableAdjustment()) {
					VASConfiguration configuration = getInsuranceDetailService().getVASConfigurationByCode(vasRecording.getProductCode());
					adviseRefMap.put(configuration.getFeeType(), detailFromFile.getReference());
				}
				totPaybleAmt = totPaybleAmt.add(insuranceDetail.getPartnerPremium());
				vasRecording.setPartnerPremiumAmt(insuranceDetail.getPartnerPremium());
				vasRecordingsList.add(vasRecording);
				sucessRecords++;
			} catch (Throwable e) {
				logger.debug(Literal.EXCEPTION, e);
				updateLog(status.getId(), detailFromFile.getReference(), "F", e.getMessage());
				failureRecords++;
			}
		}
		
		if (sucessRecords > 0) {
			InsurancePaymentInstructions detail = new InsurancePaymentInstructions();
			detail.setVasRecordindList(vasRecordingsList);
			detail.setAdviseRefMap(adviseRefMap);
			detail.setNoOfInsurances(sucessRecords);
			detail.setNoOfPayments(sucessRecords);
			detail.setNoOfReceivables(0);
			detail.setPayableAmount(totPaybleAmt);
			detail.setReceivableAmount(BigDecimal.ZERO);
			detail.setDataEngineStatusId(status.getId());
			dialogCtrl.setPaymentInstructionsFromFile(detail);
		}
		
		//Status logging
		StringBuilder remarks = new StringBuilder();
		if (totalRecords > 0) {
			if (failureRecords > 0) {
				remarks.append("Completed with exceptions, total Records: ");
				remarks.append(status.getTotalRecords());
				remarks.append(", Success: ");
				remarks.append(status.getSuccessRecords() - failureRecords);
				remarks.append(", Failure: ");
				remarks.append(status.getFailedRecords() + failureRecords);
				status.setSuccessRecords(status.getSuccessRecords() - failureRecords);
				status.setFailedRecords(status.getFailedRecords() + failureRecords);
				status.setRemarks(remarks.toString());
			} else {
				status.setRemarks(oldRemarks);
			}
			setExceptionLog(status);
			updateStatus(status);
			status.setStatus(ExecutionStatus.S.name());
		} 
		logger.debug(Literal.LEAVING);
	}
	
	public void saveInsurancePayments(InsurancePaymentInstructions paymentInstructions) {
		getInsuranceDetailService().saveInsurancePayments(paymentInstructions);
	}
	
	/******************Processing the Insurance Payment Uploads End*****************************/
	
	/*****************Process Manual Advise Start**********************************************/
	public InsurancePaymentInstructions getManualAdvises(InsurancePaymentInstructions instructions) {
		logger.debug(Literal.ENTERING);
		
		BigDecimal receivableAmt = BigDecimal.ZERO;
		BigDecimal paybleAmt = instructions.getPayableAmount();
		int noOfReceivables = 0;

		LinkedHashMap<Long, String> adviseRefMap = instructions.getAdviseRefMap();
		for (Long feeTypeId : adviseRefMap.keySet()) {
			List<ManualAdvise> receivableAdvises = getInsuranceDetailService()
					.getManualAdviseByRefAndFeeId(FinanceConstants.MANUAL_ADVISE_RECEIVABLE, feeTypeId);

			for (ManualAdvise manualAdvise : receivableAdvises) {
				if (manualAdvise.getBalanceAmt().compareTo(BigDecimal.ZERO) > 0) {
					receivableAmt = receivableAmt.add(manualAdvise.getBalanceAmt());
					noOfReceivables++;
					if (receivableAmt.compareTo(paybleAmt) > 0) {
						break;
					}
				}
			}
		}
		if (receivableAmt.compareTo(paybleAmt) > 0) {
			instructions.setReceivableAmount(paybleAmt);
		} else {
			instructions.setReceivableAmount(receivableAmt);
		}
		instructions.setNoOfReceivables(noOfReceivables);
		
		logger.debug(Literal.LEAVING);
		return instructions;
	}
	
	/*****************Process Manual Advise End**********************************************/
	
	/******************Common Methods********************************/

	public VASProviderAccDetail getVASProviderAccDetByPRoviderId(long providerId, String entityCode) {
		return getInsuranceDetailService().getVASProviderAccDetByPRoviderId(providerId, entityCode, TableType.MAIN_TAB.getSuffix());
	}
	public VASProviderAccDetail getVASProviderAccDetByPRoviderId(long providerId) {
		return getInsuranceDetailService().getVASProviderAccDetByPRoviderId(providerId, TableType.MAIN_TAB.getSuffix());
	}
	
	public BankBranch getBankBranchById(long bankBranchID) {
		return getInsuranceDetailService().getBankBranchById(bankBranchID,"_AView");
	}
	
	public VehicleDealer getProviderDetails(long providerId) {
		return getInsuranceDetailService().getProviderDetails(providerId,"_AView");
	}
	
	//Setting the workFlow details
	private void setWorkFlowDetails(InsuranceDetails insuranceDetail) {
		if (InsuranceConstants.RECON_STATUS_AUTO.equals(insuranceDetail.getReconStatus())) {
			insuranceDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			insuranceDetail.setRoleCode("");
			insuranceDetail.setNextRoleCode("");
			insuranceDetail.setTaskId("");
			insuranceDetail.setNextTaskId("");
			insuranceDetail.setWorkflowId(0);
		} else if (InsuranceConstants.RECON_STATUS_MANUAL.equals(insuranceDetail.getReconStatus())) {
			String workflowType = ModuleUtil.getWorkflowType("InsuranceDetails");
			WorkFlowDetails workFlowDetails = WorkFlowUtil.getDetailsByType(workflowType);
			WorkflowEngine engine = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
			insuranceDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			insuranceDetail.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
			insuranceDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			insuranceDetail.setWorkflowId(workFlowDetails.getWorkflowId());
			insuranceDetail.setRoleCode(workFlowDetails.getFirstTaskOwner());
			insuranceDetail.setNextRoleCode(workFlowDetails.getFirstTaskOwner());
			insuranceDetail.setTaskId(engine.getUserTaskId(insuranceDetail.getRoleCode()));
			insuranceDetail.setNextTaskId(engine.getUserTaskId(insuranceDetail.getNextRoleCode()) + ";");
		}

	}

	//Setting the exception log data engine status.
	private void setExceptionLog(DataEngineStatus status) {
		List<DataEngineLog> engineLogs = getExceptions(status.getId());
		if (CollectionUtils.isNotEmpty(engineLogs)) {
			status.setDataEngineLogList(engineLogs);
		}
	}
	
	// Getting the exception log
	public List<DataEngineLog> getExceptions(long batchId) {
		RowMapper<DataEngineLog> rowMapper = null;
		MapSqlParameterSource parameterMap = null;
		StringBuilder sql = null;

		try {
			sql = new StringBuilder("Select * from DATA_ENGINE_LOG where ID = :ID");
			parameterMap = new MapSqlParameterSource();
			parameterMap.addValue("ID", batchId);
			rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DataEngineLog.class);
			return jdbcTemplate.query(sql.toString(), parameterMap, rowMapper);
		} catch (Exception e) {
		} finally {
			rowMapper = null;
			sql = null;
		}
		return null;
	}
	
	// Data Engine log
	private void updateLog(long id, String keyId, String status, String reason) {

		StringBuilder query = null;
		MapSqlParameterSource source = null;

		query = new StringBuilder("Update DATA_ENGINE_LOG");
		query.append(" Set Status = :Status, Reason =:Reason Where Id = :Id and KeyId = :KeyId");

		source = new MapSqlParameterSource();
		source.addValue("Id", id);
		source.addValue("KeyId", keyId);
		source.addValue("Status", status);
		source.addValue("Reason", reason = reason.length() > 2000 ? reason.substring(0, 1995) : reason);

		int count = this.jdbcTemplate.update(query.toString(), source);

		if (count == 0) {
			query = new StringBuilder();
			query.append(" INSERT INTO DATA_ENGINE_LOG");
			query.append(" (Id, KeyId, Status, Reason)");
			query.append(" VALUES(:Id, :KeyId, :Status, :Reason)");
			this.jdbcTemplate.update(query.toString(), source);
		}
		query = null;
		source = null;
	}

	// Data Engine status
	private void updateStatus(DataEngineStatus status) {
		StringBuffer query = new StringBuffer();
		query.append(" UPDATE DATA_ENGINE_STATUS SET Status = :Status, ");
		query.append(" SuccessRecords = :SuccessRecords, FailedRecords = :FailedRecords,  Remarks = :Remarks ");
		query.append(" WHERE Id = :Id");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(status);
		this.jdbcTemplate.update(query.toString(), beanParameters);
	}

	
	/**
	 * Getting the Insurance details File import data for processing
	 * @param l 
	 * @return
	 */
	private List<InsuranceDetails> getInsFileImportData(long batchId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();

		sql.append("Select BatchId, Reference ,FinReference ,StartDate ,EndDate ,TermF ,CoverageAmount ,PolicyNumber");
		sql.append(",IssuanceDate ,IssuanceStatus ,PartnerPremium ,PartnerReceivedDate ,AWBNoF ,DispatchStatusF");
		sql.append(",ReasonOfRTOF ,DispatchDateAttempt1 ,DispatchDateAttempt2 ,DispatchDateAttempt3 ,MedicalStatusF");
		sql.append(",PendencyReasonCategory ,PendencyReason ,PendencyResReqF ,FPR ,PolicyStatus ,FormHandoverDate");
		sql.append(",NomineeName ,NomineeRelation From InsuranceDetails_DataEngine Where BatchId = :BatchId");

		logger.debug("selectSql: " + sql.toString());
		RowMapper<InsuranceDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(InsuranceDetails.class);
		
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("BatchId", batchId);
		
		try {
			return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}
	
	/**
	 * Getting the Insurance Payments File import data for processing
	 * @return
	 */
	private List<InsuranceDetails> getInsPaymentsFileImportData(long batchId) {
		logger.debug(Literal.ENTERING);
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("Select  FinReference ,Reference ,PolicyNumber ");
		sql.append(" From InsurancePayments_Dataengine Where BatchId = :BatchId");
		
		logger.debug("selectSql: " + sql.toString());
		RowMapper<InsuranceDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(InsuranceDetails.class);
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("BatchId", batchId);
		try {
			return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	private int getCcyFormat() {
		return CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
	}
	
	//Getters and setters
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.dataSource = dataSource;
	}

	public InsuranceDetailService getInsuranceDetailService() {
		return insuranceDetailService;
	}

	public void setInsuranceDetailService(InsuranceDetailService insuranceDetailService) {
		this.insuranceDetailService = insuranceDetailService;
	}
	
}

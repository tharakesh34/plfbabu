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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.pennant.backend.model.configuration.VASPremiumCalcDetails;
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
import com.pennant.webui.configuration.vasconfiguration.VASConfigurationDialogCtrl;
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
	private static final Logger logger = LogManager.getLogger(InsuranceFileImportService.class);

	private DataSource dataSource;
	private InsuranceDetailService insuranceDetailService;
	private NamedParameterJdbcTemplate jdbcTemplate;

	//Constants
	private static final String DELIVERED = "DELIVERED";
	private static final String RTO = "RTO";
	//private static final String OUTFORDELIVERY	= "OUTFORDELIVERY";

	/****************** Processing the Insurance details file Start ********************************/
	/**
	 * Processing the uploaded file
	 * 
	 * @throws Exception
	 */
	public void processFile(LoggedInUser userDetails, DataEngineStatus status, Media media, long providerId)
			throws Exception {
		logger.debug(Literal.ENTERING);

		String configName = status.getName();
		status.reset();
		status.setFileName(media.getName());
		status.setRemarks("Initiated file reading...");

		DataEngineImport dataEngine;
		dataEngine = new DataEngineImport(getDataSource(), userDetails.getUserId(), App.DATABASE.name(), true,
				DateUtility.getAppDate(), status);
		dataEngine.setMedia(media);
		dataEngine.importData(configName);

		do {
			if (ExecutionStatus.S.name().equals(status.getStatus())
					|| ExecutionStatus.F.name().equals(status.getStatus())) {
				processData(userDetails, providerId, status);
				break;
			}
		} while (ExecutionStatus.S.name().equals(status.getStatus())
				|| ExecutionStatus.F.name().equals(status.getStatus()));

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

		int ccyForamt = getCcyFormat();
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

				VASRecording vasRecording = getInsuranceDetailService().getVASRecording(vasReference,
						VASConsatnts.STATUS_NORMAL);
				if (vasRecording == null) {
					throw new Exception("Insurace details are not available for the Reference :" + vasReference);
				}

				//VAS Configuration
				VASConfiguration configuration = getInsuranceDetailService()
						.getVASConfigurationByCode(vasRecording.getProductCode());

				if (configuration == null) {
					throw new Exception("Configuration details are not available for the Product code :"
							+ vasRecording.getProductCode() + " for the reference :" + vasReference);
				}

				if (providerId != configuration.getManufacturerId()) {
					throw new Exception("Insurance is not related to selected Company Code for the Reference :"
							+ detailFromFile.getReference());
				}

				InsuranceDetails existingTempDetails = getInsuranceDetailService()
						.getInsurenceDetailsByRef(vasReference, TableType.TEMP_TAB.getSuffix());

				if (existingTempDetails != null) {
					throw new Exception("Record is in maintaince for the Reference :" + vasReference);
				}

				InsuranceDetails existingDetails = getInsuranceDetailService().getInsurenceDetailsByRef(vasReference,
						TableType.MAIN_TAB.getSuffix());
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
						throw new Exception(
								"Issuance date Should not be greater than the application date. At the Refence :"
										+ vasReference);
					}
					insuranceDetail.setIssuanceDate(issuanceDate);
				}

				// InsuranceStartDate greater than issuance date
				if (insuranceDetail.getStartDate() == null) {
					Date startDate = detailFromFile.getStartDate();
					if (startDate != null && (DateUtility.compare(insuranceDetail.getIssuanceDate(), startDate) > 0)) {
						throw new Exception(
								"Insurance Start date should not be less than issuance date. At the Refence :"
										+ vasReference);
					}
					insuranceDetail.setStartDate(startDate);
				}

				// InsuranceEndDate
				if (insuranceDetail.getEndDate() == null) {
					Date endDate = detailFromFile.getEndDate();
					if (endDate != null
							&& (DateUtility.compare(insuranceDetail.getIssuanceDate(), DateUtility.getAppDate()) > 0)) {
						throw new Exception(
								"Insurance End date should be greater than the application date. At the Refence :"
										+ vasReference);
					}
					insuranceDetail.setEndDate(endDate);
				}

				//If medical status is standard/Reject. System should not allow the user to update the loading premium amount. hence, premium and final premium amount will be same
				String medicalStatus = vasRecording.getMedicalStatus();
				if (VASConsatnts.VAS_MEDICALSTATUS_REJECT.equals(medicalStatus)
						|| VASConsatnts.VAS_MEDICALSTATUS_STANDARD.equals(medicalStatus)) {
					//Data saving
					insuranceDetail.setReconStatus(InsuranceConstants.RECON_STATUS_AUTO);
					setWorkFlowDetails(insuranceDetail);
					getInsuranceDetailService().saveInsuranceDetails(insuranceDetail, TableType.MAIN_TAB.getSuffix());
				} else {
					//Manual or Auto reconcile process
					if (!isExists) {
						BigDecimal policyAmt = PennantAppUtil.unFormateAmount(detailFromFile.getPartnerPremium(),
								ccyForamt);
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
						getInsuranceDetailService().updateInsuranceDetails(insuranceDetail,
								TableType.MAIN_TAB.getSuffix());
					} else {
						if (InsuranceConstants.RECON_STATUS_AUTO.equals(insuranceDetail.getReconStatus())) {
							//Accounting execution
							long linkedTranId = getInsuranceDetailService()
									.executeInsPartnerAccountingProcess(insuranceDetail, vasRecording);
							insuranceDetail.setLinkedTranId(linkedTranId);
							//Data saving
							getInsuranceDetailService().saveInsuranceDetails(insuranceDetail,
									TableType.MAIN_TAB.getSuffix());
						} else if (InsuranceConstants.RECON_STATUS_MANUAL.equals(insuranceDetail.getReconStatus())) {
							getInsuranceDetailService().saveInsuranceDetails(insuranceDetail,
									TableType.TEMP_TAB.getSuffix());
						}
					}
				}
				//Updating the vas status
				if (updateVas) {
					getInsuranceDetailService().updateVasStatus(vasRecording.getStatus(),
							vasRecording.getVasReference());
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

	/****************** Processing the Insurance details file End ********************************/

	/******************
	 * Processing the Insurance Payment Uploads Start
	 * 
	 * @param insPaymentUploadDialogCtrl
	 ***************************/
	public void processPaymentUploadsFile(long userId, DataEngineStatus status, Media media, long providerId,
			String entityCode, InsPaymentUploadDialogCtrl dialogCtrl) throws Exception {
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
				processPaymentUploadsData(providerId, entityCode, status, dialogCtrl);
				break;
			}
		} while ("S".equals(status.getStatus()) || "F".equals(status.getStatus()));

		logger.info(configName + " file processing completed");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Processing the payment upload
	 * 
	 * @param providerId
	 * @param status
	 * @param dialogCtrl
	 */
	private void processPaymentUploadsData(long providerId, String entityCode, DataEngineStatus status,
			InsPaymentUploadDialogCtrl dialogCtrl) {
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
				VASRecording vasRecording = getInsuranceDetailService().getVASRecording(detailFromFile.getReference(),
						VASConsatnts.STATUS_NORMAL);

				if (vasRecording == null) {
					throw new Exception(
							"Insurace details are not available for the Reference :" + detailFromFile.getReference());
				}

				//Term insurance lien
				if (vasRecording.isTermInsuranceLien()) {
					throw new Exception("Term Insurance Lien available for the reference.(Plolicy number :"
							+ vasRecording.getPolicyNumber() + ")." + detailFromFile.getReference());
				}

				//Medical Status
				if (VASConsatnts.VAS_MEDICALSTATUS_REJECT.equals(vasRecording.getMedicalStatus())) {
					throw new Exception(
							"Medical Status marked as Reject  for the Reference " + detailFromFile.getReference());
				}

				if (vasRecording.getPaymentInsId() == 0) {
					vasRecording.setPaymentInsId(Long.MIN_VALUE);
				}

				if (vasRecording.getPaymentInsId() != Long.MIN_VALUE) {
					throw new Exception(
							"Payment already uploaded for this reference :" + detailFromFile.getReference());
				}

				if (!StringUtils.equals(entityCode, vasRecording.getEntityCode())) {
					throw new Exception(
							"Payment already uploaded for this reference :" + detailFromFile.getReference());
				}

				if (!InsuranceConstants.ACTIVE.equals(vasRecording.getStatus())) {
					throw new Exception(
							"Insurace is in inactive status at the Reference :" + detailFromFile.getReference());
				}

				InsuranceDetails insuranceDetail = getInsuranceDetailService()
						.getInsurenceDetailsByRef(vasRecording.getVasReference(), "");

				if (insuranceDetail == null) {
					InsuranceDetails tempDetail = getInsuranceDetailService()
							.getInsurenceDetailsByRef(vasRecording.getVasReference(), "_Temp");
					if (tempDetail != null) {
						throw new Exception(
								"The uploaded insurance is not reconciled :" + detailFromFile.getReference());
					} else {
						throw new Exception("Insurace details are not uploaded for the Reference :"
								+ detailFromFile.getReference());
					}
				}

				if (!InsuranceConstants.RECON_STATUS_AUTO.equals(insuranceDetail.getReconStatus())) {
					throw new Exception(
							"Insurace is not Auto Reconciled for the Reference :" + detailFromFile.getReference());
				}

				if (providerId != insuranceDetail.getvASProviderId()) {
					throw new Exception("Insurace is not related to selected Company Code for the Reference :"
							+ detailFromFile.getReference());
				}

				if (providerAccDetail.isReceivableAdjustment()) {
					VASConfiguration configuration = getInsuranceDetailService()
							.getVASConfigurationByCode(vasRecording.getProductCode());
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

	/****************** Processing the Insurance Payment Uploads End *****************************/

	/***************** Process Manual Advise Start **********************************************/
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

	/***************** Process Manual Advise End **********************************************/

	/*****************
	 * Process VASPremium CalcUploadFile Start
	 * 
	 * @param manufacturerName
	 * 
	 * @throws Exception
	 **********************************************/
	public void processVASPremiumCalcUploadFile(long userId, DataEngineStatus status, Media media,
			String manufacturerName, VASConfigurationDialogCtrl dialogCtrl) throws Exception {

		dialogCtrl.setPremiumCalcDetList(null);

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
				processPaymentUploadsData(status, manufacturerName, dialogCtrl);
				break;
			}
		} while ("S".equals(status.getStatus()) || "F".equals(status.getStatus()));

		logger.info(configName + " file processing completed");
		logger.debug(Literal.LEAVING);
	}

	private void processPaymentUploadsData(DataEngineStatus status, String manufacturerName,
			VASConfigurationDialogCtrl dialogCtrl) {
		logger.debug(Literal.ENTERING);

		List<VASPremiumCalcDetails> calcDetails = getVASPremiumCalcDetails(status.getId());

		if (CollectionUtils.isEmpty(calcDetails)) {
			return;
		}

		String oldRemarks = status.getRemarks();
		status.setStatus(ExecutionStatus.I.name());
		status.setRemarks("File Reading completed, Initiated the data validating...");
		int totalRecords = calcDetails.size();
		int failureRecords = 0;
		int sucessRecords = 0;

		VASPremiumCalcDetails premiumCalcDetails = null;
		List<VASPremiumCalcDetails> calcDetailsList = new ArrayList<>();

		boolean isError = false;
		//Processing the data
		for (VASPremiumCalcDetails detailFromFile : calcDetails) {
			try {

				if (isError) {
					continue;
				}

				status.setStatus(ExecutionStatus.E.name());
				if (!StringUtils.equalsIgnoreCase(manufacturerName, detailFromFile.getManufacturerName())) {
					throw new Exception("Manufacturer name not matched with the selected manufacturer.");
				}
				premiumCalcDetails = new VASPremiumCalcDetails();

				//Batch Id
				premiumCalcDetails.setBatchId(detailFromFile.getBatchId());

				//Customer Age
				try {
					if (StringUtils.trimToNull(detailFromFile.getCustomerAgeF()) != null) {
						if (StringUtils.isNumeric(detailFromFile.getCustomerAgeF())) {
							premiumCalcDetails.setCustomerAge(Integer.valueOf(detailFromFile.getCustomerAgeF()));
						} else {
							premiumCalcDetails
									.setCustomerAge(new BigDecimal(detailFromFile.getCustomerAgeF()).intValue());
						}
					}
				} catch (Exception e) {
					throw new Exception("Customer age should be numeric." + detailFromFile.getCustomerAgeF());
				}

				//Gender
				String gender = detailFromFile.getGender();
				if (StringUtils.trimToNull(gender) != null) {
					if ("M".equalsIgnoreCase(gender) || "F".equalsIgnoreCase(gender) || "O".equalsIgnoreCase(gender)) {
						premiumCalcDetails.setGender(detailFromFile.getGender());
					} else {
						throw new Exception("Gender should be either M,F,O." + detailFromFile.getGender());
					}
				}

				//PolicyAge
				try {
					if (StringUtils.trimToNull(detailFromFile.getPolicyAgeF()) != null) {
						if (StringUtils.isNumeric(detailFromFile.getPolicyAgeF())) {
							premiumCalcDetails.setPolicyAge(Integer.valueOf(detailFromFile.getPolicyAgeF()));
						} else {
							premiumCalcDetails.setPolicyAge(new BigDecimal(detailFromFile.getPolicyAgeF()).intValue());
						}
					}
				} catch (Exception e) {
					throw new Exception("Policy Age In Terms should be numeric." + detailFromFile.getPolicyAgeF());
				}

				//PremiumPercentage
				try {
					if (StringUtils.trimToNull(detailFromFile.getPremiumPercentageF()) != null) {
						premiumCalcDetails.setPremiumPercentage(new BigDecimal(detailFromFile.getPremiumPercentageF()));
					}
				} catch (Exception e) {
					throw new Exception(
							"Premium Percentage should be numeric." + detailFromFile.getPremiumPercentageF());
				}

				//MinAmount
				try {
					if (StringUtils.trimToNull(detailFromFile.getMinAmountF()) != null) {
						premiumCalcDetails.setMinAmount(new BigDecimal(detailFromFile.getMinAmountF()));
					}
				} catch (Exception e) {
					throw new Exception("Min Amount should be numeric." + detailFromFile.getMinAmountF());
				}

				//Max Amount
				try {
					if (StringUtils.trimToNull(detailFromFile.getMaxAmountF()) != null) {
						premiumCalcDetails.setMaxAmount(new BigDecimal(detailFromFile.getMaxAmountF()));
					}
				} catch (Exception e) {
					throw new Exception("Max Amount should be numeric." + detailFromFile.getMaxAmountF());
				}

				//Loan Age
				try {
					if (StringUtils.trimToNull(detailFromFile.getLoanAgeF()) != null) {
						if (StringUtils.isNumeric(detailFromFile.getLoanAgeF())) {
							premiumCalcDetails.setLoanAge(Integer.valueOf(detailFromFile.getLoanAgeF()));
						} else {
							premiumCalcDetails.setLoanAge(new BigDecimal(detailFromFile.getLoanAgeF()).intValue());
						}
					}
				} catch (Exception e) {
					throw new Exception("Loan Age In Terms should be numeric." + detailFromFile.getLoanAgeF());
				}

				calcDetailsList.add(premiumCalcDetails);

				sucessRecords++;
			} catch (Throwable e) {
				logger.debug(Literal.EXCEPTION, e);
				updateLog(status.getId(), detailFromFile.getCustomerAgeF(), "F", e.getMessage());
				failureRecords++;
			}
		}

		if (sucessRecords > 0) {
			dialogCtrl.setPremiumCalcDetList(calcDetailsList);
		}

		//Status logging
		StringBuilder remarks = new StringBuilder();
		if (totalRecords > 0) {
			if (failureRecords > 0) {
				remarks.append("Completed with exceptions, total Records: ");
				remarks.append(totalRecords);
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

		logger.debug(Literal.LEAVING);
	}

	/***************** Process VASPremium CalcUploadFile End **********************************************/

	/****************** Common Methods ********************************/

	public VASProviderAccDetail getVASProviderAccDetByPRoviderId(long providerId, String entityCode) {
		return getInsuranceDetailService().getVASProviderAccDetByPRoviderId(providerId, entityCode,
				TableType.MAIN_TAB.getSuffix());
	}

	public VASProviderAccDetail getVASProviderAccDetByPRoviderId(long providerId) {
		return getInsuranceDetailService().getVASProviderAccDetByPRoviderId(providerId, TableType.MAIN_TAB.getSuffix());
	}

	public BankBranch getBankBranchById(long bankBranchID) {
		return getInsuranceDetailService().getBankBranchById(bankBranchID, "_AView");
	}

	public VehicleDealer getProviderDetails(long providerId) {
		return getInsuranceDetailService().getProviderDetails(providerId, "_AView");
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
			sql = new StringBuilder("Select * from DATA_ENGINE_LOG where StatusId = :ID");
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
		query.append(" Set Status = :Status, Reason =:Reason Where StatusId = :Id and KeyId = :KeyId");

		source = new MapSqlParameterSource();
		source.addValue("Id", id);
		source.addValue("KeyId", keyId);
		source.addValue("Status", status);
		source.addValue("Reason", reason = reason.length() > 2000 ? reason.substring(0, 1995) : reason);

		int count = this.jdbcTemplate.update(query.toString(), source);

		if (count == 0) {
			query = new StringBuilder();
			query.append(" INSERT INTO DATA_ENGINE_LOG");
			query.append(" (StatusId, KeyId, Status, Reason)");
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
	 * 
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
	 * 
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

	/*
	 * Getting the VAS premium calculation File import data
	 */
	private List<VASPremiumCalcDetails> getVASPremiumCalcDetails(long batchId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();

		sql.append(" Select  BatchId, ManufacturerName, CustomerAgeF, Gender,");
		sql.append(" PolicyAgeF, PremiumPercentageF, MinAmountF, MaxAmountF, LoanAgeF");
		sql.append(" From VASPremiumCalcDet_DataEngine Where BatchId = :BatchId");
		logger.debug("selectSql: " + sql.toString());
		RowMapper<VASPremiumCalcDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(VASPremiumCalcDetails.class);
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

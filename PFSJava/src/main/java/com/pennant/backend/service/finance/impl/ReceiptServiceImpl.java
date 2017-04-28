package com.pennant.backend.service.finance.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.finance.limits.LimitCheckDetails;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinanceRepayPriorityDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueueTotals;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralMovement;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinExcessAmountReserve;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.limitservice.impl.LimitManagement;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.exception.PFFInterfaceException;
import com.pennanttech.pff.core.TableType;
import com.rits.cloning.Cloner;

public class ReceiptServiceImpl extends GenericFinanceDetailService implements ReceiptService {
	private final static Logger				logger	= Logger.getLogger(ReceiptServiceImpl.class);

	private FinanceRepayPriorityDAO			financeRepayPriorityDAO;
	private RepaymentPostingsUtil			repayPostingUtil;
	private AccountingSetDAO				accountingSetDAO;
	private LimitCheckDetails				limitCheckDetails;
	private FinanceDetailService			financeDetailService;
	private LimitManagement					limitManagement;
	
	private FinFeeDetailDAO					finFeeDetailDAO;
	private FinExcessAmountDAO				finExcessAmountDAO;
	private FinReceiptHeaderDAO				finReceiptHeaderDAO;
	private FinReceiptDetailDAO				finReceiptDetailDAO;
	private ReceiptAllocationDetailDAO		allocationDetailDAO;		
	private ManualAdviseDAO					manualAdviseDAO;		

	public ReceiptServiceImpl() {
		super();
	}

	/**
	 * Method for Fetching FInance Details & Repay Schedule Details
	 * 
	 * @param finReference
	 * @return
	 */
	@Override
	public FinReceiptData getFinReceiptDataById(String finReference, String eventCode, String procEdtEvent, String userRole) {
		logger.debug("Entering");

		//Finance Details
		FinReceiptData receiptData = new FinReceiptData();
		receiptData.setFinReference(finReference);

		//Finance Details
		FinanceDetail financeDetail = new FinanceDetail();
		receiptData.setFinanceDetail(financeDetail);
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(finReference);
		FinanceMain financeMain = getFinanceMainDAO().getFinanceMainById(finReference, "_View", false);
		scheduleData.setFinanceMain(financeMain);

		if (financeMain != null) {

			//Finance Schedule Details
			scheduleData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference,
					"_View", false));
			
			//Finance Disbursement Details
			scheduleData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, "_View" , false));

			//Finance Repayments Instruction Details
			scheduleData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, "_View",
					false));

			//Finance Type Details
			FinanceType financeType = getFinanceTypeDAO().getFinanceTypeByID(financeMain.getFinType(), "_AView");
			scheduleData.setFinanceType(financeType);
			
			// Fee Details
			scheduleData.setFinFeeDetailList(getFinFeeDetailDAO().getFinFeeDetailByFinRef(finReference, false, "_View"));
			
			// Finance Fee Schedule Details
			if (scheduleData.getFinFeeDetailList() != null && !scheduleData.getFinFeeDetailList().isEmpty()) {

				List<Long> feeIDList = new ArrayList<>();
				for (int i = 0; i < scheduleData.getFinFeeDetailList().size(); i++) {
					FinFeeDetail feeDetail = scheduleData.getFinFeeDetailList().get(i);

					if(StringUtils.equals(feeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT) ||
							StringUtils.equals(feeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS) ||
							StringUtils.equals(feeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)){
						feeIDList.add(feeDetail.getFeeID());
					}
				}

				if(!feeIDList.isEmpty()){
					List<FinFeeScheduleDetail> feeScheduleList = getFinFeeScheduleDetailDAO().getFeeScheduleByFinID(feeIDList, false, "");

					if(feeScheduleList != null && !feeScheduleList.isEmpty()){

						HashMap<Long, List<FinFeeScheduleDetail>> schFeeMap = new HashMap<>();                        
						for (int i = 0; i < feeScheduleList.size(); i++) {
							FinFeeScheduleDetail schdFee = feeScheduleList.get(i);

							List<FinFeeScheduleDetail> schList = new ArrayList<>();
							if (schFeeMap.containsKey(schdFee.getFeeID())) {
								schList = schFeeMap.get(schdFee.getFeeID());
								schFeeMap.remove(schdFee.getFeeID());
							}
							schList.add(schdFee);
							schFeeMap.put(schdFee.getFeeID(), schList);

						}

						for (int i = 0; i < scheduleData.getFinFeeDetailList().size(); i++) {
							FinFeeDetail feeDetail = scheduleData.getFinFeeDetailList().get(i);
							if (schFeeMap.containsKey(feeDetail.getFeeID())) {
								feeDetail.setFinFeeScheduleDetailList(schFeeMap.get(feeDetail.getFeeID()));
							}
						}
					}
				}
			}
			
			// Insurance Details
			if(ImplementationConstants.ALLOW_INSURANCE){
				scheduleData.setFinInsuranceList(getFinInsurancesDAO().getFinInsuranceListByRef(finReference, "_AView",false));

				// FinSchFrqInsurance Details
				if (scheduleData.getFinInsuranceList() != null && !scheduleData.getFinInsuranceList().isEmpty()) {

					List<FinSchFrqInsurance> finSchFrqInsurances = getFinInsurancesDAO().getFinSchFrqInsuranceFinRef(finReference, false, "_AView");

					if(finSchFrqInsurances != null && !finSchFrqInsurances.isEmpty()){

						HashMap<Long, List<FinSchFrqInsurance>> schInsMap = new HashMap<>();                        
						for (int i = 0; i < finSchFrqInsurances.size(); i++) {
							FinSchFrqInsurance finSchFrqInsurance = finSchFrqInsurances.get(i);

							List<FinSchFrqInsurance> schList = new ArrayList<>();
							if (schInsMap.containsKey(finSchFrqInsurance.getInsId())) {
								schList = schInsMap.get(finSchFrqInsurance.getInsId());
								schInsMap.remove(finSchFrqInsurance.getInsId());
							}
							schList.add(finSchFrqInsurance);
							schInsMap.put(finSchFrqInsurance.getInsId(), schList);

						}

						for (int i = 0; i < scheduleData.getFinInsuranceList().size(); i++) {
							FinInsurances finInsurance = scheduleData.getFinInsuranceList().get(i);
							if (schInsMap.containsKey(finInsurance.getInsId())) {
								finInsurance.setFinSchFrqInsurances(schInsMap.get(finInsurance.getInsId()));
							}
						}
					}
				}
			}

			//Finance Customer Details			
			if (financeMain.getCustID() != 0 && financeMain.getCustID() != Long.MIN_VALUE) {
				financeDetail.setCustomerDetails(getCustomerDetailsService().getCustomerDetailsById(
						financeMain.getCustID(), true, "_View"));
			}

			//Finance Agreement Details	
			//=======================================
			financeDetail.setAggrementList(getAgreementDetailService().getAggrementDetailList(financeType.getFinType(),
					procEdtEvent, userRole));
			
			// Finance Check List Details 
			//=======================================
			getCheckListDetailService().setFinanceCheckListDetails(receiptData.getFinanceDetail(),
					financeType.getFinType(), procEdtEvent, userRole);

			//Finance Stage Accounting Posting Details 
			//=======================================
			receiptData.getFinanceDetail().setStageTransactionEntries(
					getTransactionEntryDAO().getListTransactionEntryByRefType(financeType.getFinType(),
							StringUtils.isEmpty(procEdtEvent) ? FinanceConstants.FINSER_EVENT_ORG : procEdtEvent,
							FinanceConstants.PROCEDT_STAGEACC, userRole, "_AEView", true));

			if (StringUtils.isNotBlank(financeMain.getRecordType())) {

				// Receipt Header Details
				receiptData.setReceiptHeader(getFinReceiptHeaderDAO().getReceiptHeaderByRef(finReference, "_Temp"));
				
				// Fetch Receipt Detail List
				List<FinReceiptDetail> receiptDetailList = getFinReceiptDetailDAO().getReceiptHeaderByID(receiptData.getReceiptHeader().getReceiptID(), "_Temp");
				
				// Fetch Repay Headers List
				List<FinRepayHeader> rpyHeaderList = getFinanceRepaymentsDAO().getFinRepayHeadersByRef(finReference, "_Temp");
				
				// Fetch List of Repay Schedules
				List<RepayScheduleDetail> rpySchList = getFinanceRepaymentsDAO().getRpySchdList(finReference, "_Temp");
				for (FinRepayHeader finRepayHeader : rpyHeaderList) {
					for (RepayScheduleDetail repaySchd : rpySchList) {
						if(finRepayHeader.getRepayID() == repaySchd.getRepayID()){
							finRepayHeader.getRepayScheduleDetails().add(repaySchd);
						}
					}
				}
				
				// Repay Headers setting to Receipt Details
				for (FinReceiptDetail receiptDetail : receiptDetailList) {
					for (FinRepayHeader finRepayHeader : rpyHeaderList) {
						if(finRepayHeader.getReceiptSeqID() == receiptDetail.getReceiptSeqID()){
							receiptDetail.getRepayHeaders().add(finRepayHeader);
						}
					}
				}
				receiptData.getReceiptHeader().setReceiptDetails(receiptDetailList);
				
				// Fetch Excess Amount Details
				receiptData.getReceiptHeader().setExcessAmounts(getFinExcessAmountDAO().getExcessAmountsByRef(finReference));
				
				// Receipt Allocation Details
				receiptData.getReceiptHeader().setAllocations(getAllocationDetailDAO().getAllocationsByReceiptID(
						receiptData.getReceiptHeader().getReceiptID(), "_Temp"));
				
				//Finance Document Details
				financeDetail.setDocumentDetailsList(getDocumentDetailsDAO().getDocumentDetailsByRef(finReference,
						FinanceConstants.MODULE_NAME,procEdtEvent, "_Temp"));

			} else {

				//Repay Header Details
				FinReceiptHeader receiptHeader = new FinReceiptHeader();
				receiptData.setReceiptHeader(receiptHeader);
				
				// Fetch Excess Amount Details
				receiptData.getReceiptHeader().setExcessAmounts(getFinExcessAmountDAO().getExcessAmountsByRef(finReference));
				
			}
		}

		logger.debug("Leaving");
		return receiptData;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table FinanceMain/FinanceMain_Temp by
	 * using FinanceMainDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using FinanceMainDAO's update method 3) Audit the record in to AuditHeader and AdtFinanceMain by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws PFFInterfaceException, IllegalAccessException,
			InvocationTargetException {
		logger.debug("Entering");

		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FinReceiptData rceiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();

		//Finance Stage Accounting Process
		//=======================================
		if (rceiptData.getFinanceDetail().getStageAccountingList() != null
				&& rceiptData.getFinanceDetail().getStageAccountingList().size() > 0) {

			List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();
			auditHeader = executeStageAccounting(auditHeader, list);
			if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
				return auditHeader;
			}
			list = null;
		}

		FinScheduleData scheduleData = rceiptData.getFinanceDetail().getFinScheduleData();
		FinanceMain financeMain = scheduleData.getFinanceMain();
		FinReceiptHeader receiptHeader = rceiptData.getReceiptHeader();

		String finReference = financeMain.getFinReference();
		TableType tableType = TableType.MAIN_TAB;
		if (financeMain.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}
		financeMain.setRcdMaintainSts(FinanceConstants.FINSER_EVENT_RECEIPT);
		if (tableType == TableType.MAIN_TAB) {
			financeMain.setRcdMaintainSts("");
		}

		// Finance Main Details Save And Update
		//=======================================
		long receiptID = receiptHeader.getReceiptID();
		if (financeMain.isNew()) {
			getFinanceMainDAO().save(financeMain, tableType, false);
			
			// Save Receipt Header
			receiptID = getFinReceiptHeaderDAO().save(receiptHeader, tableType);
			
		} else {
			getFinanceMainDAO().update(financeMain, tableType, false);

			//Save/Update FinRepayHeader Details depends on Workflow
			if (tableType == TableType.TEMP_TAB) {
				
				// Update Receipt Header
				getFinReceiptHeaderDAO().update(receiptHeader, tableType);
				
				// Delete Save Receipt Detail List by Reference
				getFinReceiptDetailDAO().deleteByReceiptID(receiptID, tableType);

				// Delete and Save FinRepayHeader Detail list by Reference
				getFinanceRepaymentsDAO().deleteByRef(finReference, tableType);
				
				// Delete and Save Repayment Schedule details by setting Repay Header ID
				getFinanceRepaymentsDAO().deleteRpySchdList(finReference, tableType.getSuffix());
				
				// Receipt Allocation Details
				getAllocationDetailDAO().deleteByReceiptID(receiptID , tableType);
			}
		}
		
		// Save Receipt Detail List by setting Receipt Header ID
		List<FinReceiptDetail> receiptDetails = receiptHeader.getReceiptDetails();
		for (FinReceiptDetail receiptDetail : receiptDetails) {
			receiptDetail.setReceiptID(receiptID);
			long receiptSeqID = getFinReceiptDetailDAO().save(receiptDetail, tableType);
			
			// Excess Amount Reserve
			if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EXCESS) ||
					StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EMIINADV)){
								
				// Excess Amount make utilization
				FinExcessAmountReserve exReserve = getFinExcessAmountDAO().getExcessReserve(receiptID, receiptDetail.getPayAgainstID());
				if(exReserve == null){
					
					// Update Excess Amount in Reserve
					getFinExcessAmountDAO().updateExcessReserve(receiptDetail.getPayAgainstID(), receiptDetail.getAmount());
					
					// Save Excess Reserve Log Amount
					getFinExcessAmountDAO().saveExcessReserveLog(receiptID, receiptDetail.getPayAgainstID(), receiptDetail.getAmount());
					
				}else{
					if(receiptDetail.getAmount().compareTo(exReserve.getReservedAmt()) != 0){
						BigDecimal diffInReserve = receiptDetail.getAmount().subtract(exReserve.getReservedAmt());
						
						// Update Reserve Amount in FinExcessAmount
						getFinExcessAmountDAO().updateExcessReserve(receiptDetail.getPayAgainstID(), diffInReserve);
						
						// Update Excess Reserve Log
						getFinExcessAmountDAO().updateExcessReserveLog(receiptID, receiptDetail.getPayAgainstID(), diffInReserve);
					}
				}
			}

			List<FinRepayHeader> rpyHeaderList = receiptDetail.getRepayHeaders();
			for (FinRepayHeader rpyHeader : rpyHeaderList) {
				rpyHeader.setReceiptSeqID(receiptSeqID);
				
				//Save Repay Header details
				long repayID = getFinanceRepaymentsDAO().saveFinRepayHeader(rpyHeader, tableType.getSuffix());

				List<RepayScheduleDetail> rpySchdList = rpyHeader.getRepayScheduleDetails();
				for (int i = 0; i < rpySchdList.size(); i++) {
					rpySchdList.get(i).setRepayID(repayID);
					rpySchdList.get(i).setRepaySchID(i+1);
				}
				// Save Repayment Schedule Details
				getFinanceRepaymentsDAO().saveRpySchdList(rpySchdList, tableType.getSuffix());
			}
		}
		
		// Receipt Allocation Details
		if(receiptHeader.getAllocations() != null && !receiptHeader.getAllocations().isEmpty()){
			for (int i = 0; i < receiptHeader.getAllocations().size(); i++) {
				ReceiptAllocationDetail allocation = receiptHeader.getAllocations().get(i);
				allocation.setReceiptID(receiptID);
				allocation.setAllocationID(i+1);
			}
			getAllocationDetailDAO().saveAllocations(receiptHeader.getAllocations() , tableType);
		}

		//Finance Schedule Details
		listDeletion(finReference, tableType.getSuffix());
		listSave(scheduleData, tableType.getSuffix(), 0);
		
		// Receipt Header Audit Details Preparation
		String[] rhFields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), receiptHeader.getExcludeFields());
		auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), 1, rhFields[0], rhFields[1], receiptHeader
				.getBefImage(), receiptHeader));
		
		// Save Document Details
		if (rceiptData.getFinanceDetail().getDocumentDetailsList() != null
				&& rceiptData.getFinanceDetail().getDocumentDetailsList().size() > 0) {
			List<AuditDetail> details = rceiptData.getFinanceDetail().getAuditDetailMap().get("DocumentDetails");
			auditDetails.addAll(processingDocumentDetailsList(details, tableType.getSuffix(), financeMain,
					receiptHeader.getReceiptPurpose()));
		}

		// set Finance Check List audit details to auditDetails
		//=======================================
		if (rceiptData.getFinanceDetail().getFinanceCheckList() != null
				&& !rceiptData.getFinanceDetail().getFinanceCheckList().isEmpty()) {
			auditDetails.addAll(getCheckListDetailService().saveOrUpdate(rceiptData.getFinanceDetail(), tableType.getSuffix()));
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain
				.getBefImage(), financeMain));

		auditHeader.setAuditDetails(auditDetails);
		auditHeader.setAuditModule("FinanceDetail");
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Method to delete schedule, disbursement, deferment header, deferment detail,repay instruction, rate changes
	 * lists.
	 * 
	 * @param finDetail
	 * @param tableType
	 * @param isWIF
	 */
	public void listDeletion(String finReference, String tableType) {
		logger.debug("Entering ");
		getFinanceScheduleDetailDAO().deleteByFinReference(finReference, tableType, false, 0);
		getRepayInstructionDAO().deleteByFinReference(finReference, tableType, false, 0);
		logger.debug("Leaving ");
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getFinanceMainDAO().delete with parameters financeMain,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtFinanceMain by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws PFFInterfaceException
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) throws PFFInterfaceException {
		logger.debug("Entering");

		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tranType = PennantConstants.TRAN_DEL;

		FinReceiptData receiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinScheduleData scheduleData = receiptData.getFinanceDetail().getFinScheduleData();
		FinanceMain financeMain = scheduleData.getFinanceMain();

		// Cancel All Transactions done by Finance Reference
		//=======================================
		cancelStageAccounting(financeMain.getFinReference(), receiptData.getReceiptHeader().getReceiptPurpose());

		// ScheduleDetails deletion
		listDeletion(financeMain.getFinReference(), "_Temp");
		getFinanceMainDAO().delete(financeMain, TableType.TEMP_TAB, false, false);

		// Update Receipt Header
		getFinReceiptHeaderDAO().deleteByReceiptID(receiptData.getReceiptHeader().getReceiptID(), TableType.TEMP_TAB);
		
		// Delete Save Receipt Detail List by Reference
		getFinReceiptDetailDAO().deleteByReceiptID(receiptData.getReceiptHeader().getReceiptID(), TableType.TEMP_TAB);

		// Delete and Save FinRepayHeader Detail list by Reference
		getFinanceRepaymentsDAO().deleteByRef(financeMain.getFinReference(), TableType.TEMP_TAB);
		
		// Delete and Save Repayment Schedule details by setting Repay Header ID
		getFinanceRepaymentsDAO().deleteRpySchdList(financeMain.getFinReference(), TableType.TEMP_TAB.getSuffix());
		
		// Receipt Allocation Details
		getAllocationDetailDAO().deleteByReceiptID(receiptData.getReceiptHeader().getReceiptID(), TableType.TEMP_TAB);
		
		// Receipt Header Audit Details Preparation
		String[] rhFields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), receiptData.getReceiptHeader().getExcludeFields());
		auditHeader.getAuditDetails().add(new AuditDetail(tranType, 1, rhFields[0], rhFields[1], receiptData.getReceiptHeader()
				.getBefImage(), receiptData.getReceiptHeader()));

		// Save Document Details
		if (receiptData.getFinanceDetail().getDocumentDetailsList() != null
				&& receiptData.getFinanceDetail().getDocumentDetailsList().size() > 0) {
			for (DocumentDetails docDetails : receiptData.getFinanceDetail().getDocumentDetailsList()) {
				docDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			List<AuditDetail> details = receiptData.getFinanceDetail().getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, "_Temp", receiptData.getFinanceDetail().getFinScheduleData()
					.getFinanceMain(), receiptData.getReceiptHeader().getReceiptPurpose());
			auditHeader.setAuditDetails(details);
		}

		// Checklist Details delete
		//=======================================
		auditHeader.getAuditDetails().addAll(
				getCheckListDetailService().delete(receiptData.getFinanceDetail(), "_Temp", tranType));

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain
				.getBefImage(), financeMain));
		auditHeader.setAuditModule("FinanceDetail");
		getAuditHeaderDAO().addAudit(auditHeader);

		//Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(receiptData);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getFinanceMainDAO().delete with
	 * parameters financeMain,"" b) NEW Add new record in to main table by using getFinanceMainDAO().save with
	 * parameters financeMain,"" c) EDIT Update record in the main table by using getFinanceMainDAO().update with
	 * parameters financeMain,"" 3) Delete the record from the workFlow table by using getFinanceMainDAO().delete with
	 * parameters financeMain,"_Temp" 4) Audit the record in to AuditHeader and AdtFinanceMain by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtFinanceMain by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) throws PFFInterfaceException, IllegalAccessException,
			InvocationTargetException {
		logger.debug("Entering");

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "doApprove");
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		FinReceiptData rceiptData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinReceiptHeader receiptHeader = rceiptData.getReceiptHeader();

		//Finance Stage Accounting Process
		//=======================================
		if (rceiptData.getFinanceDetail().getStageAccountingList() != null
				&& rceiptData.getFinanceDetail().getStageAccountingList().size() > 0) {

			List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();
			auditHeader = executeStageAccounting(auditHeader, list);
			if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
				return auditHeader;
			}
			list = null;
		}

		//Repayments Postings Details Process Execution
		long linkedTranId = 0;
		FinanceProfitDetail profitDetail = null;

		//Execute Accounting Details Process
		//=======================================
		FinScheduleData scheduleData = rceiptData.getFinanceDetail().getFinScheduleData();
		FinanceMain financeMain = scheduleData.getFinanceMain();
		String finReference = financeMain.getFinReference();

		financeMain.setRcdMaintainSts("");
		financeMain.setRoleCode("");
		financeMain.setNextRoleCode("");
		financeMain.setTaskId("");
		financeMain.setNextTaskId("");
		financeMain.setWorkflowId(0);

		//Repayments Posting Process Execution
		//=====================================
		List<FinReceiptDetail> receiptDetailList = receiptHeader.getReceiptDetails();
		
		profitDetail = getProfitDetailsDAO().getFinPftDetailForBatch(finReference);
		boolean isSchdRegenerated = false;
		Map<Long, List<Object>> returnPostingsMap = new HashMap<>();
		List<FinanceScheduleDetail> schdList = scheduleData.getFinanceScheduleDetails();
		
		for (int i = 0; i < receiptDetailList.size(); i++) {
			
			// Repay Header list process individually based on List existence
			List<FinRepayHeader> repayHeaderList = receiptDetailList.get(i).getRepayHeaders();
			
			for (int j = 0; j < repayHeaderList.size(); j++) {
				
				FinRepayHeader repayHeader = repayHeaderList.get(j);
				List<RepayScheduleDetail> repaySchdList = repayHeader.getRepayScheduleDetails();
				List<Object> returnList = processRepaymentPostings(financeMain, schdList,
						profitDetail, repaySchdList, repayHeader.getFinEvent(), scheduleData.getFinanceType().getFinDivision());

				if (!(Boolean) returnList.get(0)) {
					String errParm = (String) returnList.get(1);
					throw new PFFInterfaceException("9999", errParm);
				}

				//Update Linked Transaction ID
				linkedTranId = (long) returnList.get(1);
				returnPostingsMap.put(linkedTranId, returnList);
				repayHeader.setLinkedTranId(linkedTranId);
				financeMain.setFinRepaymentAmount(financeMain.getFinRepaymentAmount().add(repayHeader.getPriAmount()));
				
				String finAccount = (String) returnList.get(2);
				if (finAccount != null) {
					financeMain.setFinAccount(finAccount);
				}
				schdList = (List<FinanceScheduleDetail>) returnList.get(3);
				
				if(!isSchdRegenerated && (StringUtils.isNotEmpty(repayHeader.getEarlyPayEffMtd()) &&
						!StringUtils.equals(PennantConstants.List_Select, repayHeader.getEarlyPayEffMtd()))){
					isSchdRegenerated = true;
				}
			}
		}

		tranType = PennantConstants.TRAN_UPD;
		financeMain.setRecordType("");

		//Create log entry for Action for Schedule Modification
		FinLogEntryDetail entryDetail = new FinLogEntryDetail();
		entryDetail.setFinReference(finReference);
		entryDetail.setEventAction(receiptHeader.getReceiptPurpose());
		entryDetail.setSchdlRecal(isSchdRegenerated);
		entryDetail.setPostDate(DateUtility.getAppDate());
		entryDetail.setReversalCompleted(false);
		long logKey = getFinLogEntryDetailDAO().save(entryDetail);

		//Save Schedule Details For Future Modifications
		if (isSchdRegenerated) {
			FinScheduleData oldFinSchdData = getFinSchDataByFinRef(finReference, "");
			oldFinSchdData.setFinanceMain(financeMain);
			oldFinSchdData.setFinReference(finReference);
			listSave(oldFinSchdData, "_Log", logKey);
		}
		
		// Update Status Details and Profit Details
		financeMain = getRepayPostingUtil().updateStatus(financeMain, DateUtility.getAppDate(), schdList, profitDetail);

		//Finance Main Updation
		//=======================================
		getFinanceMainDAO().update(financeMain, TableType.MAIN_TAB, false);

		// ScheduleDetails delete and save
		//=======================================
		listDeletion(finReference, "");
		scheduleData.setFinanceScheduleDetails(schdList);
		listSave(scheduleData, "", 0);

		// Save Receipt Header
		receiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_APPROVED);
		long receiptID = getFinReceiptHeaderDAO().save(receiptHeader, TableType.MAIN_TAB);

		// Save Receipt Detail List by setting Receipt Header ID
		List<FinReceiptDetail> receiptDetails = receiptHeader.getReceiptDetails();
		for (FinReceiptDetail receiptDetail : receiptDetails) {
			receiptDetail.setReceiptID(receiptID);
			receiptDetail.setStatus(RepayConstants.PAYSTATUS_APPROVED);
			long receiptSeqID = getFinReceiptDetailDAO().save(receiptDetail, TableType.MAIN_TAB);
			
			if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EXCESS) ||
					StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EMIINADV)){
								
				// Excess Amount make utilization
				getFinExcessAmountDAO().updateUtilise(receiptDetail.getPayAgainstID(), receiptDetail.getAmount());
				
				// Delete Reserved Log against Excess and Receipt ID
				getFinExcessAmountDAO().deleteExcessReserve(receiptID, receiptDetail.getPayAgainstID());

				// Excess Movement Creation
				FinExcessMovement movement = new FinExcessMovement();
				movement.setExcessID(receiptDetail.getPayAgainstID());
				movement.setReceiptID(receiptID);
				movement.setMovementType(RepayConstants.RECEIPTTYPE_RECIPT);
				movement.setTranType(AccountConstants.TRANTYPE_DEBIT);
				movement.setAmount(receiptDetail.getAmount());
				getFinExcessAmountDAO().saveExcessMovement(movement);
				
			}

			List<FinRepayHeader> rpyHeaderList = receiptDetail.getRepayHeaders();
			for (FinRepayHeader rpyHeader : rpyHeaderList) {
				rpyHeader.setReceiptSeqID(receiptSeqID);

				//Save Repay Header details
				long repayID = getFinanceRepaymentsDAO().saveFinRepayHeader(rpyHeader, TableType.MAIN_TAB.getSuffix());

				List<RepayScheduleDetail> rpySchdList = rpyHeader.getRepayScheduleDetails();
				for (int i = 0; i < rpySchdList.size(); i++) {
					rpySchdList.get(i).setRepayID(repayID);
					rpySchdList.get(i).setRepaySchID(i+1);
				}
				// Save Repayment Schedule Details
				getFinanceRepaymentsDAO().saveRpySchdList(rpySchdList, TableType.MAIN_TAB.getSuffix());
			}
		}
		
		// Receipt Allocation Details
		if(receiptHeader.getAllocations() != null && !receiptHeader.getAllocations().isEmpty()){
			
			Map<Long , FinFeeDetail> feeDetailMap = null;
			List<FinFeeScheduleDetail> updateFeeList = new ArrayList<>();
			for (int i = 0; i < receiptHeader.getAllocations().size(); i++) {
				ReceiptAllocationDetail allocation = receiptHeader.getAllocations().get(i);
				allocation.setReceiptID(receiptID);
				allocation.setAllocationID(i+1);
				
				// Insurance Schedule Details updations for paid amounts TODO

				// Fee Schedule Details updation
				if(StringUtils.equals(allocation.getAllocationType(), RepayConstants.ALLOCATION_FEE)){
					if(allocation.getPaidAmount().compareTo(BigDecimal.ZERO) > 0 || 
							allocation.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0){
						// Fetch Fee Details based on Allocated ID
						
						if(feeDetailMap == null){
							feeDetailMap = new HashMap<>();
							for (FinFeeDetail fee : scheduleData.getFinFeeDetailList()) {
								feeDetailMap.put(fee.getFeeID(), fee);
							}
						}
						
						//TODO: What to do with waiver amount, how to adjust to fee Schedule object
						BigDecimal remBalPaidAmount = allocation.getPaidAmount();
						if(feeDetailMap.containsKey(allocation.getAllocationTo())){
							FinFeeDetail fee = feeDetailMap.get(allocation.getAllocationTo());
							for (FinFeeScheduleDetail feeSchd : fee.getFinFeeScheduleDetailList()) {
								
								if(remBalPaidAmount.compareTo(BigDecimal.ZERO) == 0){
									break;
								}
								BigDecimal feeBal = feeSchd.getSchAmount().subtract(feeSchd.getPaidAmount().subtract(feeSchd.getWaiverAmount()));
								if(feeBal.compareTo(remBalPaidAmount) > 0){
									feeBal = remBalPaidAmount;
								}
								
								// Create list of updated objects to save one time
								FinFeeScheduleDetail updFeeSchd = new FinFeeScheduleDetail();
								updFeeSchd.setFeeID(feeSchd.getFeeID());
								updFeeSchd.setPaidAmount(feeBal);
								updateFeeList.add(updFeeSchd);
								
								remBalPaidAmount = remBalPaidAmount.subtract(feeBal);
							}
						}
					}
				}
				
				// Manual Advises updation
				if(StringUtils.equals(allocation.getAllocationType(), RepayConstants.ALLOCATION_MANADV)){
					if(allocation.getPaidAmount().compareTo(BigDecimal.ZERO) > 0 || 
							allocation.getWaivedAmount().compareTo(BigDecimal.ZERO) > 0){
						getManualAdviseDAO().updateAdvPayment(allocation.getAllocationTo(),
								allocation.getPaidAmount(), allocation.getWaivedAmount(), TableType.MAIN_TAB);
						
						// Save Movements for Manual Advise
						ManualAdviseMovements movement = new ManualAdviseMovements();
						movement.setAdviseID(allocation.getAllocationTo());
						movement.setPayAgainstID(receiptID);
						movement.setMovementDate(DateUtility.getAppDate());
						movement.setMovementAmount(allocation.getPaidAmount().add(allocation.getWaivedAmount()));
						movement.setPaidAmount(allocation.getPaidAmount());
						movement.setWaivedAmount(allocation.getWaivedAmount());
						getManualAdviseDAO().saveMovement(movement);
					}
				}
			}
			
			// Fee Schedule Details Updation
			if(!updateFeeList.isEmpty()){
				getFinFeeScheduleDetailDAO().updateFeeSchdPaids(updateFeeList);
			}
			
			getAllocationDetailDAO().saveAllocations(receiptHeader.getAllocations() , TableType.MAIN_TAB);
		}

		if(!StringUtils.equals("API", rceiptData.getSourceId())) {
			// Save Document Details
			if (rceiptData.getFinanceDetail().getDocumentDetailsList() != null
					&& rceiptData.getFinanceDetail().getDocumentDetailsList().size() > 0) {
				List<AuditDetail> details = rceiptData.getFinanceDetail().getAuditDetailMap().get("DocumentDetails");
				details = processingDocumentDetailsList(details, "", rceiptData.getFinanceDetail().getFinScheduleData()
						.getFinanceMain(), receiptHeader.getReceiptPurpose());
				auditDetails.addAll(details);
				listDocDeletion(rceiptData.getFinanceDetail(), "_Temp");
			}
			
			// set Check list details Audit
			//=======================================
			if (rceiptData.getFinanceDetail().getFinanceCheckList() != null
					&& !rceiptData.getFinanceDetail().getFinanceCheckList().isEmpty()) {
				auditDetails.addAll(getCheckListDetailService().doApprove(rceiptData.getFinanceDetail(), ""));
			}
			
			String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
			
			// ScheduleDetails delete
			//=======================================
			listDeletion(finReference, "_Temp");
			
			// Fee charges deletion
			List<AuditDetail> tempAuditDetailList = new ArrayList<AuditDetail>();
			
			// Checklist Details delete
			//=======================================
			tempAuditDetailList.addAll(getCheckListDetailService().delete(rceiptData.getFinanceDetail(), "_Temp", tranType));
			
			// Delete Receipt Header
			getFinReceiptHeaderDAO().deleteByReceiptID(receiptID, TableType.TEMP_TAB);
			
			// Delete Save Receipt Detail List by Reference
			getFinReceiptDetailDAO().deleteByReceiptID(receiptID, TableType.TEMP_TAB);

			// Delete and Save FinRepayHeader Detail list by Reference
			getFinanceRepaymentsDAO().deleteByRef(finReference, TableType.TEMP_TAB);
			
			// Delete and Save Repayments Schedule details by setting Repay Header ID
			getFinanceRepaymentsDAO().deleteRpySchdList(finReference, TableType.TEMP_TAB.getSuffix());
			
			// Receipt Allocation Details
			getAllocationDetailDAO().deleteByReceiptID(receiptID, TableType.TEMP_TAB);
			
			// Finance Main Deletion from temp
			getFinanceMainDAO().delete(financeMain, TableType.TEMP_TAB, false, true);
			
			FinReceiptData tempRepayData = (FinReceiptData) aAuditHeader.getAuditDetail().getModelData();
			FinanceMain tempfinanceMain = tempRepayData.getFinanceDetail().getFinScheduleData().getFinanceMain();
			auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
					tempfinanceMain.getBefImage(), tempfinanceMain));
			
			// Receipt Header Audit Details Preparation
			String[] rhFields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), rceiptData.getReceiptHeader().getExcludeFields());
			tempAuditDetailList.add(new AuditDetail(aAuditHeader.getAuditTranType(), 1, rhFields[0], rhFields[1], rceiptData.getReceiptHeader()
					.getBefImage(), rceiptData.getReceiptHeader()));
			
			// Adding audit as deleted from TEMP table
			auditHeader.setAuditDetails(tempAuditDetailList);
			auditHeader.setAuditModule("FinanceDetail");
			getAuditHeaderDAO().addAudit(auditHeader);
			
			// Receipt Header Audit Details Preparation
			auditDetails.add(new AuditDetail(tranType, 1, rhFields[0], rhFields[1], rceiptData.getReceiptHeader()
					.getBefImage(), rceiptData.getReceiptHeader()));

			auditHeader.setAuditTranType(tranType);
			auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain
					.getBefImage(), financeMain));
			
			// Adding audit as Insert/Update/deleted into main table
			auditHeader.setAuditDetails(auditDetails);
			auditHeader.setAuditModule("FinanceDetail");
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		//Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(rceiptData);

		// send DDA Cancellation Request to Interface
		//===========================================
		//Fetch Total Repayment Amount till Maturity date for Early Settlement
		if (FinanceConstants.CLOSE_STATUS_MATURED.equals(financeMain.getClosingStatus())) {

			// send Collateral DeMark request to Interface
			//==========================================
			if(ImplementationConstants.COLLATERAL_INTERNAL){
				if(ImplementationConstants.COLLATERAL_DELINK_AUTO){
					
					List<CollateralAssignment> colAssignList = getCollateralAssignmentDAO().getCollateralAssignmentByFinRef(
							finReference,FinanceConstants.MODULE_NAME, "");
					if(colAssignList != null && !colAssignList.isEmpty()){
						for (int i = 0; i < colAssignList.size(); i++) {
							CollateralMovement movement = new CollateralMovement();
							movement.setModule(FinanceConstants.MODULE_NAME);
							movement.setCollateralRef(colAssignList.get(i).getCollateralRef());
							movement.setReference(colAssignList.get(i).getReference());
							movement.setAssignPerc(BigDecimal.ZERO);
							movement.setValueDate(DateUtility.getAppDate());
							movement.setProcess(CollateralConstants.PROCESS_AUTO);
							getCollateralAssignmentDAO().save(movement);
						}

						getCollateralAssignmentDAO().deLinkCollateral(financeMain.getFinReference());
					}
				}
			}
		}

		// send Limit Amendment Request to ACP Interface and save log details
		//=======================================
		if (ImplementationConstants.LIMIT_INTERNAL) {
			//getLimitManagement().processLoanRepay(rceiptData, false);
		} else {
			getLimitCheckDetails().doProcessLimits(financeMain,	FinanceConstants.AMENDEMENT);
		}
		
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Method for Repayment Details Posting Process
	 * 
	 * @param financeMain
	 * @param scheduleDetails
	 * @param repaySchdList
	 * @param insRefund
	 * @return
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 * @throws InvocationTargetException
	 */
	public List<Object> processRepaymentPostings(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails,
			FinanceProfitDetail profitDetail, List<RepayScheduleDetail> repaySchdList, String finEvent, String finDivision)
			throws IllegalAccessException, PFFInterfaceException, InvocationTargetException {
		logger.debug("Entering");

		List<Object> returnList = new ArrayList<Object>();
		try {

			List<FinRepayQueue> finRepayQueues = new ArrayList<FinRepayQueue>();
			FinRepayQueue finRepayQueue = null;
			FinRepayQueueTotals repayQueueTotals = new FinRepayQueueTotals();

			for (int i = 0; i < repaySchdList.size(); i++) {

				finRepayQueue = new FinRepayQueue();
				finRepayQueue.setFinReference(financeMain.getFinReference());
				finRepayQueue.setRpyDate(repaySchdList.get(i).getSchDate());
				finRepayQueue.setFinRpyFor(repaySchdList.get(i).getSchdFor());
				finRepayQueue.setRcdNotExist(true);
				finRepayQueue = doWriteDataToBean(finRepayQueue, financeMain, repaySchdList.get(i));

				finRepayQueue.setRefundAmount(repaySchdList.get(i).getRefundReq());
				finRepayQueue.setPenaltyPayNow(repaySchdList.get(i).getPenaltyPayNow());
				finRepayQueue.setWaivedAmount(repaySchdList.get(i).getWaivedAmt());
				finRepayQueue.setPenaltyBal(repaySchdList.get(i).getPenaltyAmt().subtract(repaySchdList.get(i).getPenaltyPayNow()));
				finRepayQueue.setChargeType(repaySchdList.get(i).getChargeType());

				// Total Repayments Calculation for Principal, Profit 
				repayQueueTotals.setPrincipal(repayQueueTotals.getPrincipal().add(repaySchdList.get(i).getPrincipalSchdPayNow()));
				repayQueueTotals.setProfit(repayQueueTotals.getProfit().add(repaySchdList.get(i).getProfitSchdPayNow()));
				repayQueueTotals.setLateProfit(repayQueueTotals.getLateProfit().add(repaySchdList.get(i).getLatePftSchdPayNow()));
				repayQueueTotals.setPenalty(repayQueueTotals.getPenalty().add(repaySchdList.get(i).getPenaltyPayNow()));

				// Fee Details
				repayQueueTotals.setFee(repayQueueTotals.getFee().add(repaySchdList.get(i).getSchdFeePayNow()));
				repayQueueTotals.setInsurance(repayQueueTotals.getInsurance().add(repaySchdList.get(i).getSchdInsPayNow()));
				repayQueueTotals.setSuplRent(repayQueueTotals.getSuplRent().add(repaySchdList.get(i).getSchdSuplRentPayNow()));
				repayQueueTotals.setIncrCost(repayQueueTotals.getIncrCost().add(repaySchdList.get(i).getSchdIncrCostPayNow()));

				finRepayQueues.add(finRepayQueue);
			}

			//Repayments Process For Schedule Repay List	
			repayQueueTotals.setQueueList(finRepayQueues);
			returnList = getRepayPostingUtil().postingProcess(financeMain, scheduleDetails, profitDetail,
					repayQueueTotals, finEvent,finDivision);

		} catch (PFFInterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		} catch (IllegalAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		} catch (InvocationTargetException e) {
			logger.error("Exception: ", e);
			throw e;
		}

		logger.debug("Leaving");
		return returnList;
	}

	/**
	 * Method for prepare RepayQueue data
	 * 
	 * @param resultSet
	 * @return
	 */
	private FinRepayQueue doWriteDataToBean(FinRepayQueue finRepayQueue, FinanceMain financeMain,
			RepayScheduleDetail rsd) {
		logger.debug("Entering");

		finRepayQueue.setBranch(financeMain.getFinBranch());
		finRepayQueue.setFinType(financeMain.getFinType());
		finRepayQueue.setCustomerID(financeMain.getCustID());
		finRepayQueue.setFinPriority(9999);

		finRepayQueue.setSchdPft(rsd.getProfitSchd());
		finRepayQueue.setSchdPri(rsd.getPrincipalSchd());
		finRepayQueue.setSchdPftBal(rsd.getProfitSchd().subtract(rsd.getProfitSchdPaid()));
		finRepayQueue.setSchdPriBal(rsd.getPrincipalSchd().subtract(rsd.getPrincipalSchdPaid()));
		finRepayQueue.setSchdPriPayNow(rsd.getPrincipalSchdPayNow());
		finRepayQueue.setSchdPftPayNow(rsd.getProfitSchdPayNow());
		finRepayQueue.setSchdPriPaid(rsd.getPrincipalSchdPaid());
		finRepayQueue.setSchdPftPaid(rsd.getProfitSchdPaid());

		// Fee Details
		//	1. Schedule Fee Amount
		finRepayQueue.setSchdFee(rsd.getSchdFee());
		finRepayQueue.setSchdFeeBal(rsd.getSchdFeeBal());
		finRepayQueue.setSchdFeePayNow(rsd.getSchdFeePayNow());
		finRepayQueue.setSchdFeePaid(rsd.getSchdFeePaid());

		//	2. Schedule Insurance Amount
		finRepayQueue.setSchdIns(rsd.getSchdIns());
		finRepayQueue.setSchdInsBal(rsd.getSchdInsBal());
		finRepayQueue.setSchdInsPayNow(rsd.getSchdInsPayNow());
		finRepayQueue.setSchdInsPaid(rsd.getSchdInsPaid());

		//	3. Schedule Supplementary Rent Amount
		finRepayQueue.setSchdSuplRent(rsd.getSchdSuplRent());
		finRepayQueue.setSchdSuplRentBal(rsd.getSchdSuplRentBal());
		finRepayQueue.setSchdSuplRentPayNow(rsd.getSchdSuplRentPayNow());
		finRepayQueue.setSchdSuplRentPaid(rsd.getSchdSuplRentPaid());

		//	4. Schedule Fee Amount
		finRepayQueue.setSchdIncrCost(rsd.getSchdIncrCost());
		finRepayQueue.setSchdIncrCostBal(rsd.getSchdIncrCostBal());
		finRepayQueue.setSchdIncrCostPayNow(rsd.getSchdIncrCostPayNow());
		finRepayQueue.setSchdIncrCostPaid(rsd.getSchdIncrCostPaid());

		logger.debug("Leaving");
		return finRepayQueue;
	}

	/**
	 * Method to get Schedule related data.
	 * 
	 * @param finReference
	 *            (String)
	 * @param isWIF
	 *            (boolean)
	 * **/
	public FinScheduleData getFinSchDataByFinRef(String finReference, String type) {
		logger.debug("Entering");
		FinScheduleData finSchData = new FinScheduleData();
		finSchData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type,false));
		finSchData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, type,false));
		finSchData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, false));
		logger.debug("Leaving");
		return finSchData;
	}

	public void listSave(FinScheduleData scheduleData, String tableType, long logKey) {
		logger.debug("Entering ");
		HashMap<Date, Integer> mapDateSeq = new HashMap<Date, Integer>();

		// Finance Schedule Details
		for (int i = 0; i < scheduleData.getFinanceScheduleDetails().size(); i++) {

			FinanceScheduleDetail curSchd = scheduleData.getFinanceScheduleDetails().get(i);
			curSchd.setLastMntBy(scheduleData.getFinanceMain().getLastMntBy());
			curSchd.setFinReference(scheduleData.getFinReference());
			int seqNo = 0;

			if (mapDateSeq.containsKey(curSchd.getSchDate())) {
				seqNo = mapDateSeq.get(curSchd.getSchDate());
				mapDateSeq.remove(curSchd.getSchDate());
			}
			seqNo = seqNo + 1;
			mapDateSeq.put(curSchd.getSchDate(), seqNo);
			curSchd.setSchSeq(seqNo);
			curSchd.setLogKey(logKey);
		}

		getFinanceScheduleDetailDAO().saveList(scheduleData.getFinanceScheduleDetails(), tableType, false);

		if (logKey != 0) {
			// Finance Disbursement Details
			mapDateSeq = new HashMap<Date, Integer>();
			Date curBDay = DateUtility.getAppDate();
			for (int i = 0; i < scheduleData.getDisbursementDetails().size(); i++) {
				scheduleData.getDisbursementDetails().get(i).setFinReference(scheduleData.getFinReference());
				scheduleData.getDisbursementDetails().get(i).setDisbReqDate(curBDay);
				scheduleData.getDisbursementDetails().get(i).setDisbIsActive(true);
				scheduleData.getDisbursementDetails().get(i).setDisbDisbursed(true);
				scheduleData.getDisbursementDetails().get(i).setLogKey(logKey);
			}
			getFinanceDisbursementDAO().saveList(scheduleData.getDisbursementDetails(), tableType, false);

		}

		//Finance Repay Instruction Details
		if (scheduleData.getRepayInstructions() != null) {
			for (int i = 0; i < scheduleData.getRepayInstructions().size(); i++) {
				RepayInstruction curSchd = scheduleData.getRepayInstructions().get(i);

				curSchd.setFinReference(scheduleData.getFinReference());
				curSchd.setLogKey(logKey);
			}
			getRepayInstructionDAO().saveList(scheduleData.getRepayInstructions(), tableType, false);
		}

		logger.debug("Leaving ");
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from getFinanceMainDAO().getErrorDetail with Error ID and
	 * language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);

		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Method for Validate Finance Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @param isWIF
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		FinReceiptData repayData = (FinReceiptData) auditDetail.getModelData();
		FinanceMain financeMain = repayData.getFinanceDetail().getFinScheduleData().getFinanceMain();

		FinanceMain tempFinanceMain = null;
		if (financeMain.isWorkflow()) {
			tempFinanceMain = getFinanceMainDAO().getFinanceMainById(financeMain.getId(), "_Temp", false);
		}
		FinanceMain befFinanceMain = getFinanceMainDAO().getFinanceMainById(financeMain.getId(), "", false);
		FinanceMain oldFinanceMain = financeMain.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = financeMain.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (financeMain.isNew()) { // for New record or new record into work flow

			if (!financeMain.isWorkflow()) {// With out Work flow only new
				// records
				if (befFinanceMain != null) { // Record Already Exists in the
					// table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
					// records type is new
					if (befFinanceMain != null || tempFinanceMain != null) { // if
						// records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befFinanceMain == null || tempFinanceMain != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!financeMain.isWorkflow()) { // With out Work flow for update
				// and delete

				if (befFinanceMain == null) { // if records not exists in the
					// main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinanceMain != null && !oldFinanceMain.getLastMntOn().equals(befFinanceMain.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41003", errParm, valueParm), usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41004", errParm, valueParm), usrLanguage));
						}
					}
				}
			} else {

				if (tempFinanceMain == null) { // if records not exists in the
					// Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}

				if (tempFinanceMain != null && oldFinanceMain != null
						&& !oldFinanceMain.getLastMntOn().equals(tempFinanceMain.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}
			}
		}
		
		// Checking , if Customer is in EOD process or not. if Yes, not allowed to do an action
		int eodProgressCount = getCustomerQueuingDAO().getProgressCountByCust(financeMain.getCustID());

		// If Customer Exists in EOD Processing, Not allowed to Maintenance till completion
		if(eodProgressCount > 0){
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
					PennantConstants.KEY_FIELD, "60203", errParm, valueParm), usrLanguage));
		}

		//Checking For Commitment , Is it In Maintenance Or not
		if (StringUtils.trimToEmpty(financeMain.getRecordType()).equals(PennantConstants.RECORD_TYPE_NEW)
				&& "doApprove".equals(method) && StringUtils.isNotEmpty(financeMain.getFinCommitmentRef())) {

			Commitment tempcommitment = getCommitmentDAO()
					.getCommitmentById(financeMain.getFinCommitmentRef(), "_Temp");
			if (tempcommitment != null && tempcommitment.isRevolving()) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
						"30538", errParm, valueParm), usrLanguage));
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !financeMain.isWorkflow()) {
			financeMain.setBefImage(befFinanceMain);
		}

		return auditDetail;
	}

	/**
	 * Method for prepare AuditHeader
	 * 
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinReceiptData repayData = (FinReceiptData) auditHeader.getAuditDetail().getModelData();
		FinanceDetail financeDetail = repayData.getFinanceDetail();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String auditTranType = "";
		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (financeMain.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		//Finance Document Details
		if (financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList().size() > 0) {
			auditDetailMap.put("DocumentDetails", setDocumentDetailsAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
		}

		//Finance Check List Details 
		//=======================================
		List<FinanceCheckListReference> financeCheckList = financeDetail.getFinanceCheckList();

		if (StringUtils.equals(method, "saveOrUpdate")) {
			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				auditDetails.addAll(getCheckListDetailService().getAuditDetail(auditDetailMap, financeDetail,
						auditTranType, method));
			}
		} else {
			String tableType = "_Temp";
			if (financeDetail.getFinScheduleData().getFinanceMain().getRecordType()
					.equals(PennantConstants.RECORD_TYPE_DEL)) {
				tableType = "";
			}

			String finReference = financeDetail.getFinScheduleData().getFinReference();
			financeCheckList = getCheckListDetailService().getCheckListByFinRef(finReference, tableType);
			financeDetail.setFinanceCheckList(financeCheckList);

			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				auditDetails.addAll(getCheckListDetailService().getAuditDetail(auditDetailMap, financeDetail,
						auditTranType, method));
			}
		}

		financeDetail.setAuditDetailMap(auditDetailMap);
		repayData.setFinanceDetail(financeDetail);
		auditHeader.getAuditDetail().setModelData(repayData);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");

		return auditHeader;
	}

	/**
	 * Method for Fetching Accounting Entries
	 * 
	 * @param financeDetail
	 * @return
	 */
	@Override
	public FinanceDetail getAccountingDetail(FinanceDetail financeDetail, String eventCodeRef) {
		logger.debug("Entering");

		FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();

		Long accountSetId = Long.MIN_VALUE;
		if (AccountEventConstants.ACCEVENT_EARLYSTL.equals(eventCodeRef)) {
			accountSetId = getFinTypeAccountingDAO().getAccountSetID(financeType.getFinType(), AccountEventConstants.ACCEVENT_EARLYSTL, FinanceConstants.MODULEID_FINTYPE);
		} else if (AccountEventConstants.ACCEVENT_EARLYPAY.equals(eventCodeRef)) {
			accountSetId = getFinTypeAccountingDAO().getAccountSetID(financeType.getFinType(), AccountEventConstants.ACCEVENT_EARLYPAY, FinanceConstants.MODULEID_FINTYPE);
		} else {
			accountSetId = getFinTypeAccountingDAO().getAccountSetID(financeType.getFinType(), AccountEventConstants.ACCEVENT_REPAY, FinanceConstants.MODULEID_FINTYPE);
		}

		financeDetail.setTransactionEntries(getTransactionEntryDAO().getListTransactionEntryById(
				accountSetId, "_AEView", true));

		String commitmentRef = financeDetail.getFinScheduleData().getFinanceMain().getFinCommitmentRef();

		if (StringUtils.isEmpty(commitmentRef)) {

			Commitment commitment = getCommitmentDAO().getCommitmentById(commitmentRef, "");
			if (commitment != null && commitment.isRevolving()) {
				long accountingSetId = getAccountingSetDAO().getAccountingSetId(AccountEventConstants.ACCEVENT_CMTRPY,
						AccountEventConstants.ACCEVENT_CMTRPY);
				if (accountingSetId != 0) {
					financeDetail.setCmtFinanceEntries(getTransactionEntryDAO().getListTransactionEntryById(
							accountingSetId, "_AEView", true));
				}
			}
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	@Override
	public FinanceProfitDetail getPftDetailForEarlyStlReport(String finReference) {
		return getProfitDetailsDAO().getPftDetailForEarlyStlReport(finReference);
	}

	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceRepayPriorityDAO getFinanceRepayPriorityDAO() {
		return financeRepayPriorityDAO;
	}
	public void setFinanceRepayPriorityDAO(FinanceRepayPriorityDAO financeRepayPriorityDAO) {
		this.financeRepayPriorityDAO = financeRepayPriorityDAO;
	}

	public RepaymentPostingsUtil getRepayPostingUtil() {
		return repayPostingUtil;
	}
	public void setRepayPostingUtil(RepaymentPostingsUtil repayPostingUtil) {
		this.repayPostingUtil = repayPostingUtil;
	}

	public AccountingSetDAO getAccountingSetDAO() {
		return accountingSetDAO;
	}
	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
	}

	public LimitCheckDetails getLimitCheckDetails() {
		return limitCheckDetails;
	}
	public void setLimitCheckDetails(LimitCheckDetails limitCheckDetails) {
		this.limitCheckDetails = limitCheckDetails;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public LimitManagement getLimitManagement() {
		return limitManagement;
	}
	public void setLimitManagement(LimitManagement limitManagement) {
		this.limitManagement = limitManagement;
	}

	public FinExcessAmountDAO getFinExcessAmountDAO() {
		return finExcessAmountDAO;
	}
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public FinFeeDetailDAO getFinFeeDetailDAO() {
		return finFeeDetailDAO;
	}
	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	public FinReceiptHeaderDAO getFinReceiptHeaderDAO() {
		return finReceiptHeaderDAO;
	}
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public FinReceiptDetailDAO getFinReceiptDetailDAO() {
		return finReceiptDetailDAO;
	}
	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	public ReceiptAllocationDetailDAO getAllocationDetailDAO() {
		return allocationDetailDAO;
	}
	public void setAllocationDetailDAO(ReceiptAllocationDetailDAO allocationDetailDAO) {
		this.allocationDetailDAO = allocationDetailDAO;
	}

	public ManualAdviseDAO getManualAdviseDAO() {
		return manualAdviseDAO;
	}
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}
	
}

package com.pennant.backend.service.finance.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.finance.limits.LimitCheckDetails;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.RepaymentProcessUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinanceRepayPriorityDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.finance.OverdraftScheduleDetailDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralMovement;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinExcessAmountReserve;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ManualAdviseReserve;
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
import com.pennanttech.pff.core.InterfaceException;
import com.pennanttech.pff.core.TableType;
import com.rits.cloning.Cloner;

public class ReceiptServiceImpl extends GenericFinanceDetailService implements ReceiptService {
	private final static Logger				logger	= Logger.getLogger(ReceiptServiceImpl.class);

	private FinanceRepayPriorityDAO			financeRepayPriorityDAO;
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
	private RepaymentProcessUtil			repayProcessUtil;
	private ReceiptCalculator				receiptCalculator;
	private OverdraftScheduleDetailDAO		overdraftScheduleDetailDAO;

	public ReceiptServiceImpl() {
		super();
	}

	/**
	 * Method for Fetching Receipt Details , record is waiting for Realization
	 * 
	 * @param finReference
	 * @return
	 */
	@Override
	public FinReceiptHeader getFinReceiptHeaderById(long receiptID, String type) {
		logger.debug("Entering");

		// Receipt Header Details
		FinReceiptHeader receiptHeader = null;
		receiptHeader = getFinReceiptHeaderDAO().getReceiptHeaderByID(receiptID, "_View");

		// Fetch Receipt Detail List
		if(receiptHeader != null){
			List<FinReceiptDetail> receiptDetailList = getFinReceiptDetailDAO().getReceiptHeaderByID(receiptID, "_AView");
			receiptHeader.setReceiptDetails(receiptDetailList);
		}

		logger.debug("Leaving");
		return receiptHeader;
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
			
			//Overdraft Details
			if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
				scheduleData.setOverdraftScheduleDetails(getOverdraftScheduleDetailDAO().getOverdraftScheduleDetails(
						finReference, "", false));
			}
			
			//Finance Disbursement Details
			scheduleData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, "_View" , false));

			//Finance Repayments Instruction Details
			scheduleData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, "_View",
					false));

			//Finance Type Details
			FinanceType financeType = getFinanceTypeDAO().getFinanceTypeByID(financeMain.getFinType(), "_AView");
			scheduleData.setFinanceType(financeType);
			
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
				receiptData.setReceiptHeader(getFinReceiptHeaderDAO().getReceiptHeaderByRef(finReference, TableType.TEMP_TAB.getSuffix()));
				
				// Fetch Receipt Detail List
				List<FinReceiptDetail> receiptDetailList = getFinReceiptDetailDAO().getReceiptHeaderByID(receiptData.getReceiptHeader().getReceiptID(), "_TView");
				
				// Fetch Repay Headers List
				List<FinRepayHeader> rpyHeaderList = getFinanceRepaymentsDAO().getFinRepayHeadersByRef(finReference, TableType.TEMP_TAB.getSuffix());
				
				// Fetch List of Repay Schedules
				List<RepayScheduleDetail> rpySchList = getFinanceRepaymentsDAO().getRpySchdList(finReference, TableType.TEMP_TAB.getSuffix());
				for (FinRepayHeader finRepayHeader : rpyHeaderList) {
					for (RepayScheduleDetail repaySchd : rpySchList) {
						if(finRepayHeader.getRepayID() == repaySchd.getRepayID()){
							finRepayHeader.getRepayScheduleDetails().add(repaySchd);
						}
					}
				}
				
				// Repay Headers setting to Receipt Details
				List<FinExcessAmountReserve> excessReserves = new ArrayList<>();
				List<ManualAdviseReserve> payableReserves = new ArrayList<>();
				for (FinReceiptDetail receiptDetail : receiptDetailList) {
					for (FinRepayHeader finRepayHeader : rpyHeaderList) {
						if(finRepayHeader.getReceiptSeqID() == receiptDetail.getReceiptSeqID()){
							receiptDetail.getRepayHeaders().add(finRepayHeader);
						}
					}
					
					// Manual Advise Movements
					receiptDetail.setAdvMovements(getManualAdviseDAO().getAdvMovementsByReceiptSeq(receiptDetail.getReceiptID(),receiptDetail.getReceiptSeqID(), TableType.TEMP_TAB.getSuffix()));
					
					// Excess Reserve Amounts
					excessReserves.addAll(getFinExcessAmountDAO().getExcessReserveList(receiptDetail.getReceiptSeqID()));
					
					// Payable Reserve Amounts
					payableReserves.addAll(getManualAdviseDAO().getPayableReserveList(receiptDetail.getReceiptSeqID()));
				}
				receiptData.getReceiptHeader().setExcessReserves(excessReserves);
				receiptData.getReceiptHeader().setPayableReserves(payableReserves);
				
				receiptData.getReceiptHeader().setReceiptDetails(receiptDetailList);
				
				// Fetch Excess Amount Details
				receiptData.getReceiptHeader().setExcessAmounts(getFinExcessAmountDAO().getExcessAmountsByRef(finReference));
				
				// Fetch Payable Advise Amount Details
				receiptData.getReceiptHeader().setPayableAdvises(getManualAdviseDAO().getManualAdviseByRef(finReference, 
						FinanceConstants.MANUAL_ADVISE_PAYABLE, "_AView"));
				
				// Receipt Allocation Details
				receiptData.getReceiptHeader().setAllocations(getAllocationDetailDAO().getAllocationsByReceiptID(
						receiptData.getReceiptHeader().getReceiptID(), TableType.TEMP_TAB.getSuffix()));
				
				//Finance Document Details
				financeDetail.setDocumentDetailsList(getDocumentDetailsDAO().getDocumentDetailsByRef(finReference,
						FinanceConstants.MODULE_NAME,procEdtEvent, TableType.TEMP_TAB.getSuffix()));

			} else {

				//Repay Header Details
				FinReceiptHeader receiptHeader = new FinReceiptHeader();
				receiptData.setReceiptHeader(receiptHeader);
				
				// Fetch Excess Amount Details
				receiptData.getReceiptHeader().setExcessAmounts(getFinExcessAmountDAO().getExcessAmountsByRef(finReference));
				
				// Fetch Payable Advise Amount Details
				receiptData.getReceiptHeader().setPayableAdvises(getManualAdviseDAO().getManualAdviseByRef(finReference, 
						FinanceConstants.MANUAL_ADVISE_PAYABLE, "_AView"));
				
			}
			
			// Fee Details ( Fetch Fee Details on below Cases only)
			// 1. Origination Schedule Fee Details from Main Table , because for schedule payment case there will be no maintenance
			// 2. Get All Schedule Payment Fee details for service action
			// 3. Else get all fee details which are included with Origination Schedule Fees(Maintained) and Service action Fees
			String schTableType = "";
			if (StringUtils.isBlank(financeMain.getRecordType())) {
				scheduleData.setFinFeeDetailList(getFinFeeDetailDAO().getFinScheduleFees(finReference, false, "_View"));
			}else{
				scheduleData.setFinFeeDetailList(getFinFeeDetailDAO().getFinFeeDetailByFinRef(finReference, false, "_TView"));
				schTableType = "_Temp";
			}

			// Finance Fee Schedule Details
			if (scheduleData.getFinFeeDetailList() != null && !scheduleData.getFinFeeDetailList().isEmpty()) {

				List<Long> feeIDList = new ArrayList<>();
				for (int i = 0; i < scheduleData.getFinFeeDetailList().size(); i++) {
					FinFeeDetail feeDetail = scheduleData.getFinFeeDetailList().get(i);
					
					if(feeDetail.isOriginationFee()){
						feeDetail.setRcdVisible(false);
					}

					if(StringUtils.equals(feeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT) ||
							StringUtils.equals(feeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS) ||
							StringUtils.equals(feeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)){
						feeIDList.add(feeDetail.getFeeID());
					}
				}

				if(!feeIDList.isEmpty()){
					List<FinFeeScheduleDetail> feeScheduleList = getFinFeeScheduleDetailDAO().getFeeScheduleByFinID(feeIDList, false, schTableType);

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
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws InterfaceException, IllegalAccessException,
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
		List<FinReceiptDetail> receiptDetails = sortReceiptDetails(receiptHeader.getReceiptDetails());
		
		// Manual Advise Movements
		getManualAdviseDAO().deleteMovementsByReceiptID(receiptID,TableType.TEMP_TAB.getSuffix());
					
		for (FinReceiptDetail receiptDetail : receiptDetails) {
			receiptDetail.setReceiptID(receiptID);
			long receiptSeqID = receiptDetail.getReceiptSeqID();
			if(!receiptDetail.isDelRecord()){
				receiptSeqID = getFinReceiptDetailDAO().save(receiptDetail, tableType);
			}
			
			// Excess Amount Reserve
			if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EXCESS) ||
					StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EMIINADV)){
								
				// Excess Amount make utilization
				FinExcessAmountReserve exReserve = getFinExcessAmountDAO().getExcessReserve(receiptSeqID, receiptDetail.getPayAgainstID());
				if(exReserve == null){
					
					// Update Excess Amount in Reserve
					getFinExcessAmountDAO().updateExcessReserve(receiptDetail.getPayAgainstID(), receiptDetail.getAmount());
					
					// Save Excess Reserve Log Amount
					getFinExcessAmountDAO().saveExcessReserveLog(receiptSeqID, receiptDetail.getPayAgainstID(), receiptDetail.getAmount(), RepayConstants.RECEIPTTYPE_RECIPT);
					
				}else{
					if(receiptDetail.getAmount().compareTo(exReserve.getReservedAmt()) != 0){
						BigDecimal diffInReserve = receiptDetail.getAmount().subtract(exReserve.getReservedAmt());
						
						// Update Reserve Amount in FinExcessAmount
						getFinExcessAmountDAO().updateExcessReserve(receiptDetail.getPayAgainstID(), diffInReserve);
						
						// Update Excess Reserve Log
						getFinExcessAmountDAO().updateExcessReserveLog(receiptSeqID, receiptDetail.getPayAgainstID(), diffInReserve, RepayConstants.RECEIPTTYPE_RECIPT);
					}
				}
			}
			
			// Payable Amount Reserve
			if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_PAYABLE)){

				// Payable Amount make utilization
				ManualAdviseReserve payableReserve = getManualAdviseDAO().getPayableReserve(receiptSeqID, receiptDetail.getPayAgainstID());
				if(payableReserve == null){

					// Update Payable Amount in Reserve
					getManualAdviseDAO().updatePayableReserve(receiptDetail.getPayAgainstID(), receiptDetail.getAmount());

					// Save Payable Reserve Log Amount
					getManualAdviseDAO().savePayableReserveLog(receiptSeqID, receiptDetail.getPayAgainstID(), receiptDetail.getAmount());

				}else{
					if(receiptDetail.getAmount().compareTo(payableReserve.getReservedAmt()) != 0){
						BigDecimal diffInReserve = receiptDetail.getAmount().subtract(payableReserve.getReservedAmt());

						// Update Reserve Amount in Manual Advise
						getManualAdviseDAO().updatePayableReserve(receiptDetail.getPayAgainstID(), diffInReserve);

						// Update Payable Reserve Log
						getManualAdviseDAO().updatePayableReserveLog(receiptSeqID, receiptDetail.getPayAgainstID(), diffInReserve);
					}
				}
			}
			
			// Manual Advise Movements
			for (ManualAdviseMovements movement : receiptDetail.getAdvMovements()) {
				movement.setReceiptID(receiptID);
				movement.setReceiptSeqID(receiptSeqID);
				getManualAdviseDAO().saveMovement(movement, TableType.TEMP_TAB.getSuffix());
			}

			List<FinRepayHeader> rpyHeaderList = receiptDetail.getRepayHeaders();
			for (FinRepayHeader rpyHeader : rpyHeaderList) {
				rpyHeader.setReceiptSeqID(receiptSeqID);
				
				//Save Repay Header details
				long repayID = getFinanceRepaymentsDAO().saveFinRepayHeader(rpyHeader, tableType.getSuffix());

				List<RepayScheduleDetail> rpySchdList = rpyHeader.getRepayScheduleDetails();
				if (rpySchdList != null && !rpySchdList.isEmpty()) {
				
					for (int i = 0; i < rpySchdList.size(); i++) {
						rpySchdList.get(i).setRepayID(repayID);
						rpySchdList.get(i).setRepaySchID(i+1);
					}
					// Save Repayment Schedule Details
					getFinanceRepaymentsDAO().saveRpySchdList(rpySchdList, tableType.getSuffix());
				}
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

		// Finance Fee Details
		// =======================================
		if (rceiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList() != null
				&& !rceiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList().isEmpty()) {
			auditDetails.addAll(getFinFeeDetailService().saveOrUpdate(
					rceiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList(), tableType.getSuffix(),
					auditHeader.getAuditTranType(), false));
		}
		
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
	 * workFlow table by using getFinReceiptHeaderDAO().delete with parameters financeMain,TableType.TEMP_TAB.getSuffix() 3) Audit the record in to
	 * AuditHeader and AdtFinanceMain by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws InterfaceException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException {
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
		List<AuditDetail> auditDetails = new ArrayList<>();

		// Cancel All Transactions done by Finance Reference
		//=======================================
		cancelStageAccounting(financeMain.getFinReference(), receiptData.getReceiptHeader().getReceiptPurpose());

		// ScheduleDetails deletion
		listDeletion(financeMain.getFinReference(), TableType.TEMP_TAB.getSuffix());
		getFinanceMainDAO().delete(financeMain, TableType.TEMP_TAB, false, false);
		
		// Delete and Save Repayment Schedule details by setting Repay Header ID
		getFinanceRepaymentsDAO().deleteRpySchdList(financeMain.getFinReference(), TableType.TEMP_TAB.getSuffix());
		
		// Delete and Save FinRepayHeader Detail list by Reference
		getFinanceRepaymentsDAO().deleteByRef(financeMain.getFinReference(), TableType.TEMP_TAB);
		
		for (FinReceiptDetail receiptDetail : receiptData.getReceiptHeader().getReceiptDetails()) {
			long receiptSeqID = receiptDetail.getReceiptSeqID();

			// Excess Amount Reserve
			if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EXCESS) ||
					StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_EMIINADV)){
								
				// Excess Amount make utilization
				FinExcessAmountReserve exReserve = getFinExcessAmountDAO().getExcessReserve(receiptSeqID, receiptDetail.getPayAgainstID());
				if(exReserve != null){

					// Update Reserve Amount in FinExcessAmount
					getFinExcessAmountDAO().updateExcessReserve(receiptDetail.getPayAgainstID(), exReserve.getReservedAmt().negate());

					// Delete Reserved Log against Excess and Receipt ID
					getFinExcessAmountDAO().deleteExcessReserve(receiptSeqID, receiptDetail.getPayAgainstID(), RepayConstants.RECEIPTTYPE_RECIPT);
				}
			}
			
			// Payable Amount Reserve
			if(StringUtils.equals(receiptDetail.getPaymentType(), RepayConstants.PAYTYPE_PAYABLE)){

				// Payable Amount make utilization
				ManualAdviseReserve payableReserve = getManualAdviseDAO().getPayableReserve(receiptSeqID, receiptDetail.getPayAgainstID());
				if(payableReserve != null){

					// Update Reserve Amount in ManualAdvise
					getManualAdviseDAO().updatePayableReserve(receiptDetail.getPayAgainstID(), payableReserve.getReservedAmt().negate());

					// Delete Reserved Log against Payable Advise ID and Receipt ID
					getManualAdviseDAO().deletePayableReserve(receiptSeqID, receiptDetail.getPayAgainstID());
				}
			}
		}
		
		// Delete Save Receipt Detail List by Reference
		getFinReceiptDetailDAO().deleteByReceiptID(receiptData.getReceiptHeader().getReceiptID(), TableType.TEMP_TAB);
		
		// Receipt Allocation Details
		getAllocationDetailDAO().deleteByReceiptID(receiptData.getReceiptHeader().getReceiptID(), TableType.TEMP_TAB);
		
		// Delete Manual Advise Movements
		getManualAdviseDAO().deleteMovementsByReceiptID(receiptData.getReceiptHeader().getReceiptID(),TableType.TEMP_TAB.getSuffix());
		
		// Delete Receipt Header
		getFinReceiptHeaderDAO().deleteByReceiptID(receiptData.getReceiptHeader().getReceiptID(), TableType.TEMP_TAB);
		
		// Receipt Header Audit Details Preparation
		String[] rhFields = PennantJavaUtil.getFieldDetails(new FinReceiptHeader(), receiptData.getReceiptHeader().getExcludeFields());
		auditDetails.add(new AuditDetail(tranType, 1, rhFields[0], rhFields[1], receiptData.getReceiptHeader()
				.getBefImage(), receiptData.getReceiptHeader()));
		
		// Delete Fee Details
		if (receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList() != null) {
			auditDetails.addAll(getFinFeeDetailService().delete(
					receiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList(), "_Temp",
					auditHeader.getAuditTranType(), false));
		}

		// Delete Document Details
		if (receiptData.getFinanceDetail().getDocumentDetailsList() != null
				&& receiptData.getFinanceDetail().getDocumentDetailsList().size() > 0) {
			for (DocumentDetails docDetails : receiptData.getFinanceDetail().getDocumentDetailsList()) {
				docDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
			List<AuditDetail> details = receiptData.getFinanceDetail().getAuditDetailMap().get("DocumentDetails");
			details = processingDocumentDetailsList(details, TableType.TEMP_TAB.getSuffix(), receiptData.getFinanceDetail().getFinScheduleData()
					.getFinanceMain(), receiptData.getReceiptHeader().getReceiptPurpose());
			auditDetails.addAll(details);
		}

		// Checklist Details delete
		//=======================================
		auditDetails.addAll(
				getCheckListDetailService().delete(receiptData.getFinanceDetail(), TableType.TEMP_TAB.getSuffix(), tranType));

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain
				.getBefImage(), financeMain));
		auditHeader.setAuditModule("FinanceDetail");
		auditHeader.setAuditDetails(auditDetails);
		getAuditHeaderDAO().addAudit(auditHeader);
		
		//Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(receiptData);

		logger.debug("Leaving");
		
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getFinReceiptHeaderDAO().delete with
	 * parameters financeMain,"" b) NEW Add new record in to main table by using getFinReceiptHeaderDAO().save with
	 * parameters financeMain,"" c) EDIT Update record in the main table by using getFinReceiptHeaderDAO().update with
	 * parameters financeMain,"" 3) Delete the record from the workFlow table by using getFinReceiptHeaderDAO().delete with
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
	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader) throws InterfaceException, IllegalAccessException,
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
		receiptHeader.setPostBranch(auditHeader.getAuditBranchCode());

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
		profitDetail = getProfitDetailsDAO().getFinProfitDetailsById(finReference);
		List<FinanceScheduleDetail> schdList = scheduleData.getFinanceScheduleDetails();
		schdList=getRepayProcessUtil().doProcessReceipts(financeMain, schdList, 
				profitDetail, receiptHeader, scheduleData,DateUtility.getAppDate());
		if(schdList == null){
			schdList = scheduleData.getFinanceScheduleDetails();
		}

		tranType = PennantConstants.TRAN_UPD;
		financeMain.setRecordType("");

		// Update Status Details and Profit Details
		financeMain = getRepayProcessUtil().updateStatus(financeMain, DateUtility.getAppDate(), schdList, profitDetail);

		//Finance Main Updation
		//=======================================
		getFinanceMainDAO().update(financeMain, TableType.MAIN_TAB, false);

		// ScheduleDetails delete and save
		//=======================================
		listDeletion(finReference, "");
		scheduleData.setFinanceScheduleDetails(schdList);
		listSave(scheduleData, "", 0);
		
		// Save Receipt Header
		if(StringUtils.equals(receiptHeader.getReceiptPurpose(), FinanceConstants.FINSER_EVENT_EARLYRPY) || 
				StringUtils.equals(receiptHeader.getReceiptPurpose(), FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
			receiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);
		}else{
			receiptHeader.setReceiptModeStatus(RepayConstants.PAYSTATUS_APPROVED);
		}
		receiptHeader.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		receiptHeader.setRecordType("");
		receiptHeader.setRoleCode("");
		receiptHeader.setNextRoleCode("");
		receiptHeader.setTaskId("");
		receiptHeader.setNextTaskId("");
		receiptHeader.setWorkflowId(0);
		
		//save Receipt Details
		repayProcessUtil.doSaveReceipts(receiptHeader, scheduleData.getFinFeeDetailList());
		long receiptID = receiptHeader.getReceiptID();

		// Finance Fee Details
		if (scheduleData.getFinFeeDetailList() != null) {
			getFinFeeDetailService().doApprove(scheduleData.getFinFeeDetailList(), 
					TableType.MAIN_TAB.getSuffix(), tranType, false);
		}
		
		if(!StringUtils.equals(PennantConstants.FINSOURCE_ID_API, rceiptData.getSourceId())) {
			// Save Document Details
			if (rceiptData.getFinanceDetail().getDocumentDetailsList() != null
					&& rceiptData.getFinanceDetail().getDocumentDetailsList().size() > 0) {
				List<AuditDetail> details = rceiptData.getFinanceDetail().getAuditDetailMap().get("DocumentDetails");
				details = processingDocumentDetailsList(details, "", rceiptData.getFinanceDetail().getFinScheduleData()
						.getFinanceMain(), receiptHeader.getReceiptPurpose());
				auditDetails.addAll(details);
				listDocDeletion(rceiptData.getFinanceDetail(), TableType.TEMP_TAB.getSuffix());
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
			listDeletion(finReference, TableType.TEMP_TAB.getSuffix());
			
			// Fee charges deletion
			List<AuditDetail> tempAuditDetailList = new ArrayList<AuditDetail>();
			
			//Fin Fee Details Deletion
			if (rceiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList() != null) {
				auditDetails.addAll(getFinFeeDetailService().delete(
						rceiptData.getFinanceDetail().getFinScheduleData().getFinFeeDetailList(), "_Temp",
						auditHeader.getAuditTranType(), false));
			}
			
			// Checklist Details delete
			//=======================================
			tempAuditDetailList.addAll(getCheckListDetailService().delete(rceiptData.getFinanceDetail(), TableType.TEMP_TAB.getSuffix(), tranType));
			
			// Delete and Save Repayments Schedule details by setting Repay Header ID
			getFinanceRepaymentsDAO().deleteRpySchdList(finReference, TableType.TEMP_TAB.getSuffix());
			
			// Delete and Save FinRepayHeader Detail list by Reference
			getFinanceRepaymentsDAO().deleteByRef(finReference, TableType.TEMP_TAB);
			
			// Delete Save Receipt Detail List by Reference
			getFinReceiptDetailDAO().deleteByReceiptID(receiptID, TableType.TEMP_TAB);
			
			// Receipt Allocation Details
			getAllocationDetailDAO().deleteByReceiptID(receiptID, TableType.TEMP_TAB);
			
			// Delete Manual Advise Movements
			getManualAdviseDAO().deleteMovementsByReceiptID(receiptID, TableType.TEMP_TAB.getSuffix());
			
			// Delete Receipt Header
			getFinReceiptHeaderDAO().deleteByReceiptID(receiptID, TableType.TEMP_TAB);
			
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
			BigDecimal priAmt=BigDecimal.ZERO;
			
			for (FinReceiptDetail finReceiptDetail : receiptHeader.getReceiptDetails()) {
				for (FinRepayHeader header : finReceiptDetail.getRepayHeaders()) {
					priAmt=priAmt.add(header.getPriAmount());
				}
			}
			Customer customer = getCustomerDAO().getCustomerByID(financeMain.getCustID());
			getLimitManagement().processLoanRepay(financeMain,customer,priAmt,
					StringUtils.trimToEmpty(financeMain.getProductCategory()));
		} else {
			getLimitCheckDetails().doProcessLimits(financeMain,	FinanceConstants.AMENDEMENT);
		}
		
		logger.debug("Leaving");
		return auditHeader;
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
	 * for any mismatch conditions Fetch the error details from getFinReceiptHeaderDAO().getErrorDetail with Error ID and
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
			tempFinanceMain = getFinanceMainDAO().getFinanceMainById(financeMain.getId(), TableType.TEMP_TAB.getSuffix(), false);
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
					.getCommitmentById(financeMain.getFinCommitmentRef(), TableType.TEMP_TAB.getSuffix());
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
			String tableType = TableType.TEMP_TAB.getSuffix();
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
		
		// Finance Fee details
		if (!financeDetail.isExtSource()) {
			if (financeDetail.getFinScheduleData().getFinFeeDetailList() != null) {
				auditDetails.addAll(getFinFeeDetailService().validate(
						financeDetail.getFinScheduleData().getFinFeeDetailList(), financeMain.getWorkflowId(),
						method, auditTranType, auditHeader.getUsrLanguage(), false));
			}
		}

		financeDetail.setAuditDetailMap(auditDetailMap);
		repayData.setFinanceDetail(financeDetail);
		auditHeader.getAuditDetail().setModelData(repayData);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");

		return auditHeader;
	}

	@Override
	public FinanceProfitDetail getPftDetailForEarlyStlReport(String finReference) {
		return getProfitDetailsDAO().getPftDetailForEarlyStlReport(finReference);
	}

	/**
	 * Method for Sorting Receipt Details From Receipts
	 * @param receipts
	 * @return
	 */
	private List<FinReceiptDetail> sortReceiptDetails(List<FinReceiptDetail> receipts){

		if (receipts != null && receipts.size() > 0) {
			Collections.sort(receipts, new Comparator<FinReceiptDetail>() {
				@Override
				public int compare(FinReceiptDetail detail1, FinReceiptDetail detail2) {
					if (detail1.getPayOrder() > detail2.getPayOrder()) {
						return 1;
					} else if(detail1.getPayOrder() < detail2.getPayOrder()) {
						return -1;
					} 
					return 0;
				}
			});
		}
		return receipts;
	}
	
	
	/**
	 * Method for Calculate Payment Details based on Entered Receipts
	 * @param financeMain
	 * @param finSchDetails
	 * @param isReCal
	 * @param method
	 * @param valueDate
	 * @return
	 */
	@Override
	public FinReceiptData calculateRepayments(FinReceiptData finReceiptData) {
		logger.debug("Entering");

		finReceiptData.setBuildProcess("R");
		finReceiptData.getRepayMain().setRepayAmountNow(BigDecimal.ZERO);
		finReceiptData.getRepayMain().setPrincipalPayNow(BigDecimal.ZERO);
		finReceiptData.getRepayMain().setProfitPayNow(BigDecimal.ZERO);

		// Prepare Receipt Details Data
		FinReceiptHeader receiptHeader = finReceiptData.getReceiptHeader();
		FinanceDetail financeDetail = finReceiptData.getFinanceDetail();
		receiptHeader.setReceiptAmount(receiptHeader.getReceiptAmount());
		receiptHeader.getAllocations().clear();

		// Receipt Mode case
		FinReceiptDetail receiptDetail = receiptHeader.getReceiptDetails().get(0);
		receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
		receiptDetail.setPaymentType(receiptHeader.getReceiptMode());
		receiptDetail.setPayAgainstID(0);
		receiptDetail.setAmount(receiptHeader.getReceiptAmount());
		receiptDetail.setDelRecord(false);
		receiptDetail.setPayOrder(1);
		receiptDetail.getAdvMovements().clear();
		receiptDetail.getRepayHeaders().clear();

		// Prepare Allocation Details
		List<String> allocateTypes = new ArrayList<>(finReceiptData.getAllocationMap().keySet());
		ReceiptAllocationDetail allocationDetail = null;
		BigDecimal totalPaid = BigDecimal.ZERO;
		for (int i = 0; i < allocateTypes.size(); i++) {
			allocationDetail = new ReceiptAllocationDetail();

			String allocationType = allocateTypes.get(i);
			long allocateTo = 0;
			if(allocateTypes.get(i).contains("_")){
				allocationType = allocateTypes.get(i).substring(0, allocateTypes.get(i).indexOf("_"));
				allocateTo = Long.valueOf(allocateTypes.get(i).substring(allocateTypes.get(i).indexOf("_")+1));
			}

			allocationDetail.setAllocationID(i+1);
			allocationDetail.setAllocationType(allocationType);
			allocationDetail.setAllocationTo(allocateTo);
			allocationDetail.setPaidAmount(finReceiptData.getAllocationMap().get(allocateTypes.get(i)));
			if (allocationDetail.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
				receiptHeader.getAllocations().add(allocationDetail);
				if(!StringUtils.equals(allocationType, RepayConstants.ALLOCATION_TDS)){
					totalPaid = totalPaid.add(allocationDetail.getPaidAmount());
				}else{
					totalPaid = totalPaid.subtract(allocationDetail.getPaidAmount());
				}
			}
		}
		
		// Setting Valid Components to open based upon Remaining Balance
		BigDecimal totReceiptAmount = receiptHeader.getReceiptAmount();
		BigDecimal remBal = totReceiptAmount.subtract(totalPaid);
		if(remBal.compareTo(BigDecimal.ZERO) < 0){
			remBal = BigDecimal.ZERO;
		}
		
		// Setting Extra amount for Partial Settlement case
		if(StringUtils.equals(receiptHeader.getReceiptPurpose(), FinanceConstants.FINSER_EVENT_EARLYRPY)){
			boolean isPriRcdFound = false;
			for (ReceiptAllocationDetail detail : receiptHeader.getAllocations()) {
				if(StringUtils.equals(detail.getAllocationType(), RepayConstants.ALLOCATION_PRI) && 
						remBal.compareTo(BigDecimal.ZERO) > 0){
					detail.setPaidAmount(detail.getPaidAmount().add(remBal));
					isPriRcdFound = true;
					break;
				}
			}
			if(!isPriRcdFound && remBal.compareTo(BigDecimal.ZERO) > 0){
				allocationDetail = new ReceiptAllocationDetail();
				allocationDetail.setAllocationID(receiptHeader.getAllocations().size()+1);
				allocationDetail.setAllocationType(RepayConstants.ALLOCATION_PRI);
				allocationDetail.setAllocationTo(0);
				allocationDetail.setPaidAmount(remBal);
				receiptHeader.getAllocations().add(allocationDetail);
			}
		}

		finReceiptData.setReceiptHeader(receiptHeader);
		finReceiptData = receiptCalculator.initiateReceipt(finReceiptData, financeDetail.getFinScheduleData(),
				receiptHeader.getReceiptPurpose());

		logger.debug("Leaving");
		return finReceiptData;
	}
	
	/**
	 * Method for Schedule Modifications with Effective Schedule Method
	 * 
	 * @param receiptData
	 * @throws InterfaceException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	@Override
	public FinReceiptData recalEarlypaySchdl(FinReceiptData receiptData, FinServiceInstruction finServiceInstruction, 
			String recptPurpose) throws IllegalAccessException, InvocationTargetException, InterfaceException {
		logger.debug("Entering");

		//Schedule Recalculation Depends on Earlypay Effective Schedule method
		FinanceDetail financeDetail = receiptData.getFinanceDetail();
		financeDetail.setFinScheduleData(getFinanceDetailService().getFinSchDataForReceipt(receiptData.getFinReference(), "_AView"));
		FinanceMain aFinanceMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		
		// Setting Effective Recalculation Schedule Method
		String method = null;
		if (StringUtils.equals(recptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)) {
			method = finServiceInstruction.getRecalType();
		} else if (StringUtils.equals(recptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)
				||StringUtils.equals(recptPurpose, FinanceConstants.FINSER_EVENT_EARLYSTLENQ)) {
			method = CalculationConstants.EARLYPAY_ADJMUR;
		}
		
		BigDecimal totalBal = receiptData.getReceiptHeader().getReceiptAmount().subtract( receiptData.getReceiptHeader().getTotFeeAmount());
		if (StringUtils.equals(recptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)) {
			if(receiptData.getAllocationMap() != null && !receiptData.getAllocationMap().isEmpty()){
				List<String> allocationKeys = new ArrayList<>(receiptData.getAllocationMap().keySet());
				for (int i = 0; i < allocationKeys.size(); i++) {
					if(!StringUtils.equals(allocationKeys.get(i), RepayConstants.ALLOCATION_TDS)){
						totalBal = totalBal.subtract(receiptData.getAllocationMap().get(allocationKeys.get(i)));
					}else{
						totalBal = totalBal.add(receiptData.getAllocationMap().get(allocationKeys.get(i)));
					}
				}
			}
		}
		
		// Accrued Profit Calculation
		Date curBussniessDate = DateUtility.getAppDate();
		BigDecimal priBalance = BigDecimal.ZERO;
		boolean isLastTermAdjusted = false;
		if(StringUtils.equals(recptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){

			FinanceScheduleDetail curSchd = null;
			FinanceScheduleDetail prvSchd = null;

			for (int i = 0; i < finScheduleData.getFinanceScheduleDetails().size(); i++) {
				curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
				if(i != 0){
					prvSchd = finScheduleData.getFinanceScheduleDetails().get(i - 1);
				}
				if (DateUtility.compare(curBussniessDate, curSchd.getSchDate()) == 0) {
					if(StringUtils.equals(curSchd.getSchdMethod(), CalculationConstants.SCHMTHD_EQUAL)){
						priBalance = curSchd.getClosingBalance().subtract(curSchd.getCpzAmount()).add(curSchd.getProfitCalc().add(prvSchd.getProfitBalance()));
					}else{
						priBalance = curSchd.getClosingBalance().subtract(curSchd.getCpzAmount());
					}
					isLastTermAdjusted = true;
					
					// Future Disbursements into Early paid Balance
					priBalance = priBalance.add(curSchd.getDisbAmount());
					
				} else if (DateUtility.compare(curBussniessDate, curSchd.getSchDate()) < 0) {
					if(!isLastTermAdjusted){
						priBalance = prvSchd.getClosingBalance();
						isLastTermAdjusted = true;
					}
					// Future Disbursements into Early paid Balance
					priBalance = priBalance.add(curSchd.getDisbAmount());
				}
			}
		}

		// Schedule re-modifications only when Effective Schedule Method modified
		if (!StringUtils.equals(method, CalculationConstants.EARLYPAY_NOEFCT)) {

			if (StringUtils.equals(recptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY)) {
				receiptData.getRepayMain().setEarlyPayAmount(totalBal);
			}else if(StringUtils.equals(recptPurpose, FinanceConstants.FINSER_EVENT_EARLYSETTLE)){
				receiptData.getRepayMain().setEarlyPayAmount(priBalance);
			}

			if (StringUtils.equals(method, CalculationConstants.EARLYPAY_RECPFI)
					|| StringUtils.equals(method, CalculationConstants.EARLYPAY_ADMPFI)) {
				aFinanceMain.setPftIntact(true);
			}

			receiptData.getRepayMain().setEarlyPayOnSchDate(DateUtility.getAppDate());
			boolean isSchdDateFound = false;
			FinanceScheduleDetail prvSchd = null;
			for (FinanceScheduleDetail detail : finScheduleData.getFinanceScheduleDetails()) {
				if (detail.getSchDate().compareTo(receiptData.getRepayMain().getEarlyPayOnSchDate()) == 0) {
					if (StringUtils.equals(method, CalculationConstants.EARLYPAY_RECPFI)) {
						detail.setEarlyPaid(detail.getEarlyPaid().add(receiptData.getRepayMain().getEarlyPayAmount())
								.subtract(detail.getRepayAmount()));
						break;
					} else {
						final BigDecimal earlypaidBal = detail.getEarlyPaidBal();
						receiptData.getRepayMain().setEarlyPayAmount(detail.getPrincipalSchd().add(
								receiptData.getRepayMain().getEarlyPayAmount()).add(earlypaidBal));
					}
					isSchdDateFound = true;
				}
				if (detail.getSchDate().compareTo(receiptData.getRepayMain().getEarlyPayOnSchDate()) >= 0) {
					detail.setEarlyPaid(BigDecimal.ZERO);
					detail.setEarlyPaidBal(BigDecimal.ZERO);
				} else{
					prvSchd = detail;
				}
			}

			finScheduleData.setFinanceScheduleDetails(sortSchdDetails(finScheduleData.getFinanceScheduleDetails()));
			finScheduleData.setFinanceType(finScheduleData.getFinanceType());

			// Finding Next Repay Schedule on date
			Date nextRepaySchDate = receiptData.getRepayMain().getEarlyPayNextSchDate();
			if(!isSchdDateFound){
				FinanceScheduleDetail newSchdlEP = new FinanceScheduleDetail(finScheduleData.getFinanceMain().getFinReference());
				newSchdlEP.setDefSchdDate(DateUtility.getAppDate());
				newSchdlEP.setSchDate(DateUtility.getAppDate());
				newSchdlEP.setSchSeq(1);
				newSchdlEP.setSpecifier(CalculationConstants.SCH_SPECIFIER_REPAY);
				newSchdlEP.setRepayOnSchDate(true);
				newSchdlEP.setPftOnSchDate(true);
				newSchdlEP.setSchdMethod(prvSchd.getSchdMethod());
				newSchdlEP.setBaseRate(prvSchd.getBaseRate());
				newSchdlEP.setSplRate(prvSchd.getSplRate());
				newSchdlEP.setMrgRate(prvSchd.getMrgRate());
				newSchdlEP.setActRate(prvSchd.getActRate());
				newSchdlEP.setCalculatedRate(prvSchd.getCalculatedRate());
				newSchdlEP.setPftDaysBasis(prvSchd.getPftDaysBasis());
				finScheduleData.getFinanceScheduleDetails().add(newSchdlEP);
				sortSchdDetails(finScheduleData.getFinanceScheduleDetails());
			}

			for (FinanceScheduleDetail curSchd : finScheduleData.getFinanceScheduleDetails()) {
				if (DateUtility.compare(curSchd.getSchDate(), receiptData.getRepayMain().getEarlyPayOnSchDate()) <= 0) {
					if(DateUtility.compare(curSchd.getSchDate(), aFinanceMain.getGrcPeriodEndDate()) <= 0){
						if(StringUtils.equals(curSchd.getSchdMethod(), CalculationConstants.SCHMTHD_PFT)){
							finScheduleData.getFinanceMain().setRecalSchdMethod(CalculationConstants.SCHMTHD_PRI_PFT);
						}else{
							finScheduleData.getFinanceMain().setRecalSchdMethod(CalculationConstants.SCHMTHD_PRI);
						}
					}else{
						if(!isSchdDateFound){
							finScheduleData.getFinanceMain().setRecalSchdMethod(CalculationConstants.SCHMTHD_PRI);
						}else{
							finScheduleData.getFinanceMain().setRecalSchdMethod(curSchd.getSchdMethod());
						}
					}

					if(StringUtils.equals(recptPurpose, FinanceConstants.FINSER_EVENT_EARLYRPY) &&
							DateUtility.compare(curSchd.getSchDate(), receiptData.getRepayMain().getEarlyPayOnSchDate()) == 0){
						receiptData.getRepayMain().setEarlyPayAmount(receiptData.getRepayMain().getEarlyPayAmount().add(
								curSchd.getProfitSchd()));
					}
				} else {
					nextRepaySchDate = curSchd.getSchDate();
					break;
				}
			}

			//Calculation of Schedule Changes for Early Payment to change Schedule Effects Depends On Method
			finScheduleData = ScheduleCalculator.recalEarlyPaySchedule(finScheduleData, receiptData.getRepayMain()
					.getEarlyPayOnSchDate(), nextRepaySchDate, receiptData.getRepayMain().getEarlyPayAmount(), method);

			// Validation against Future Disbursements, if Closing balance is becoming zero before future disbursement date
			List<FinanceDisbursement> disbList = finScheduleData.getDisbursementDetails();
			Date actualMaturity = finScheduleData.getFinanceMain().getCalMaturity();
			for (int i = 0; i < disbList.size(); i++) {
				FinanceDisbursement curDisb = disbList.get(i);
				if(curDisb.getDisbDate().compareTo(actualMaturity) >= 0){
					finScheduleData.getErrorDetails().add(ErrorUtil.getErrorDetail(new ErrorDetails("30577", null)));
					logger.debug("Leaving");
					return receiptData;
				}
			}

			financeDetail.setFinScheduleData(finScheduleData);
			aFinanceMain = finScheduleData.getFinanceMain();
			aFinanceMain.setWorkflowId(financeDetail.getFinScheduleData().getFinanceMain().getWorkflowId());
			receiptData.setFinanceDetail(financeDetail);
		}

		//Repayments Calculation
		receiptData = calculateRepayments(receiptData);
		logger.debug("Leaving");
		return receiptData;
	}
	
	public List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtility.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;
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

	public RepaymentProcessUtil getRepayProcessUtil() {
		return repayProcessUtil;
	}
	public void setRepayProcessUtil(RepaymentProcessUtil repayProcessUtil) {
		this.repayProcessUtil = repayProcessUtil;
	}

	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	public OverdraftScheduleDetailDAO getOverdraftScheduleDetailDAO() {
		return overdraftScheduleDetailDAO;
	}

	public void setOverdraftScheduleDetailDAO(OverdraftScheduleDetailDAO overdraftScheduleDetailDAO) {
		this.overdraftScheduleDetailDAO = overdraftScheduleDetailDAO;
	}

	@Override
	public List<FinFeeDetail> getFinFeeDetailById(String finReference, boolean isWIF, String type, String eventCode) {
		return getFinFeeDetailService().getFinFeeDetailById(finReference, isWIF, type, eventCode);
	}

}

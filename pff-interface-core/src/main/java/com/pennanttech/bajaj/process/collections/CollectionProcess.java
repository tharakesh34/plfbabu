package com.pennanttech.bajaj.process.collections;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.repeat.RepeatStatus;
import org.zkoss.util.resource.Labels;

import com.pennanttech.bajaj.process.collections.model.CollectionConstants;
import com.pennanttech.bajaj.process.collections.model.CollectionDAO;
import com.pennanttech.bajaj.process.collections.model.CollectionFinTypeFees;
import com.pennanttech.bajaj.process.collections.model.CollectionFinances;
import com.pennanttech.bajaj.process.collections.model.CollectionReceiptExtraction;
import com.pennanttech.bajaj.process.collections.model.CollectionStoredProcedure;
import com.pennanttech.bajaj.process.collections.model.DataExtractions;
import com.pennanttech.dataengine.util.DateUtil;

public class CollectionProcess implements Runnable {
	private static final Logger logger = Logger.getLogger(CollectionProcess.class);
	
	private DataSource dataSource;
	private CollectionDAO collectionDAO;
	
	protected int curOdDays = 0;
	private String interfaceName = "C";
	private Date appDate;
	private long lastMntBy = 0;
	
	public CollectionProcess() {
		super();		
	}
	
	@Override
	public void run() {
		logger.debug("Entering thread :");
		
		DataExtractions dataExtractions = createDataExtractions();
		long extractionId = dataExtractions.getExtractionId();
		
		this.collectionDAO.updateDataExtractionStatus(extractionId, CollectionConstants.COLLECTION_INPROGRESS);
		
		boolean process = false;
		
		//CUST_PERS_INFO_V
		process = nextProcess(CollectionConstants.SP_CUST_PERS_INFO_V, CollectionConstants.TN_CUST_PERS_INFO_V_TMP, extractionId);
		
		//CUST_ADDRESS_INFO_V
		if (process) {
			process = nextProcess(CollectionConstants.SP_CUST_ADDRESS_INFO_V, CollectionConstants.TN_CUST_ADDRESS_INFO_V_TMP, extractionId);
		}
		
		//CASE_DETAILS_V
		if (process) {
			process = nextProcess(CollectionConstants.SP_CASE_DETAILS_V, CollectionConstants.TN_CASE_DETAILS_V_TMP, extractionId);
		}

		//DISBURSAL_INFO_TMP
		if (process) {
			process = nextProcess(CollectionConstants.SP_DISBURSAL_INFO_V, CollectionConstants.TN_DISBURSAL_INFO_TMP, extractionId);
		}

		//GUARANTOR_DETAILS_V
		if (process) {
			process = nextProcess(CollectionConstants.SP_GUARANTOR_DETAILS_V, CollectionConstants.TN_GUARANTOR_DETAILS_V_TMP, extractionId);
		}
		
		//PAYMENT_DETAILS_V
		if (process) {

			// Receipts Extraction Process
			receiptExtractionProcess(extractionId);

			process = nextProcess(CollectionConstants.SP_PAYMENT_DETAILS_V, CollectionConstants.TN_PAYMENT_DETAILS_V, extractionId);
		}
		
		//REPAYMENT_SCH_V
		if (process) {
			process = nextProcess(CollectionConstants.SP_REPAYMENT_SCH_V, CollectionConstants.TN_REPAYMENT_SCH_V_TMP, extractionId);
		}
		
		//BOUNCE_HISTORY_V
		if (process) {
			process = nextProcess(CollectionConstants.SP_BOUNCE_HISTORY_V, CollectionConstants.TN_BOUNCE_HISTORY_V_TEMP, extractionId);
		}
		
		//FORECLOSURE_DETAILS_V
		if (process) {
			process = nextProcess(CollectionConstants.SP_FORECLOSURE_DETAILS_V, CollectionConstants.TN_FORECLOSURE_DETAILS_V_TMP, extractionId);
		}
	
		//NON_DELINQ_ACCT
		if (process) {
			process = nextProcess(CollectionConstants.SP_NON_DELINQ_ACCT_V, CollectionConstants.TN_NON_DELINQ_BOM_POSITION_ACCT, extractionId);
		}
		
		//PRODUCT_MASTER
		if (process) {
			process = nextProcess(CollectionConstants.SP_PRODUCT_MASTER, CollectionConstants.TN_PRODUCT_MASTER_TMP, extractionId);
		}
		
		if (process) {
			this.collectionDAO.updateDataExtractionStatus(extractionId, CollectionConstants.COLLECTION_SUCCESS);
		}
		
		logger.debug("Leaving thread :");
	}
	
	private boolean nextProcess(String spName, String tableName, long extractionId) {
		logger.debug("Entering");
		
		boolean process = false;
		String result = null;
		
		try {
			result = executeProcedure(spName, extractionId);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			result = "FAILED";
		} finally {
			this.collectionDAO.updateCollection(tableName, result);
		}
		
		if (StringUtils.equals(result, "FAILED")) {
			this.collectionDAO.updateDataExtractionStatus(extractionId, CollectionConstants.COLLECTION_FAILED);
			this.collectionDAO.truncateCollectionTables(false);
		} else {
			process = true;
		}
		
		logger.debug("Leaving");
		
		return process;
	}
	
	/**
	 * execute the procedure and get the Result
	 * @param dataSource
	 * @param spName
	 * @param extractionId
	 * @return result
	 */
	public String executeProcedure(String spName, long extractionId) throws Exception {
		logger.debug("Entering");
		
		String result = null;
		
		try {
			RepeatStatus repeatStatus = RepeatStatus.CONTINUABLE;
			
			// create Stored procedure
			CollectionStoredProcedure collectionStoredProcedure = new CollectionStoredProcedure(dataSource, spName);
			
			//execute the procedure
			repeatStatus = collectionStoredProcedure.execute(extractionId);
			
			if (repeatStatus == null) {
				repeatStatus = RepeatStatus.FINISHED;
			} 
			
			result = repeatStatus.toString();
			
			if (StringUtils.equalsIgnoreCase("FINISHED", repeatStatus.toString())){
				result = "COMPLETED";
			}
			
		} catch (Exception e) {
			logger.error("Exception: ", e);
			result = "FAILED";
		}
		
		logger.debug("Leaving");
		
		return result;
	}

	
	/**
	 * create DataExtracions
	 * @param curOdDays
	 * @param interfaceName
	 * @return
	 */
	private DataExtractions createDataExtractions() {
		logger.debug("Entering");
		
		DataExtractions dataExtractions = new DataExtractions();
		dataExtractions.setExtractionDate(this.appDate);
		dataExtractions.setInterfaceName(this.interfaceName);
		dataExtractions.setProgress(CollectionConstants.COLLECTION_WAIT);
		dataExtractions.setStartTime(DateUtil.getSysDate());	
		dataExtractions.setLastMntBy(this.lastMntBy);	
		
		dataExtractions.setExtractionId(this.collectionDAO.saveDataExtraction(dataExtractions));
		
		//Update status as empty
		this.collectionDAO.updateCollection("", " ");
		
		//Truncate collection tables data
		this.collectionDAO.truncateCollectionTables(true);
		
		
		String sysParamValue = this.collectionDAO.getSystemParameterValue("COL_DIVISIONS");
		List<String> divisions = new ArrayList<String>();
		
		if (StringUtils.isNoneBlank(sysParamValue)) {
			String[] sysValues = sysParamValue.split(","); 
			for(String sysParam : sysValues) {
				divisions.add(sysParam);
			}
		}
		
		//get the collection finances Data
		List<CollectionFinances> list = new ArrayList<CollectionFinances>();
		List<CollectionFinances> collectionODFinList = this.collectionDAO.getCollectionODFinList(this.curOdDays, divisions, this.appDate);
		List<CollectionFinances> collectionFinList = this.collectionDAO.getCollectionFinList(divisions, this.appDate);
		list.addAll(collectionODFinList);
		list.addAll(collectionFinList);
		
		// Calculate ForeClosure Charges
		list = calForeClosureCharges(list);
		
		if (!list.isEmpty()) {

			for (CollectionFinances collectionFinance : list) {
				collectionFinance.setExtractionId(dataExtractions.getExtractionId());
			}
			dataExtractions.setCollectionFinancesList(list);

			//Save the CollectionFinances
			this.collectionDAO.saveCollectionFinancesBatch(list);
		}
		
		logger.debug("Entering");
		
		return dataExtractions;
	}
	
	// ****************************************************************** //
	// ****************** Receipts Extraction Process ******************* //
	// ****************************************************************** //

	/**
	 * Receipts Extraction Process
	 */
	private void receiptExtractionProcess(long extractionId) {
		logger.debug("Entering");

		List<CollectionReceiptExtraction> receiptExtractionlist = new ArrayList<CollectionReceiptExtraction>(1);
		this.collectionDAO.startReceiptProcess(extractionId);
		this.collectionDAO.saveAllocationHeader(extractionId);
		List<Long> receiptIdList = this.collectionDAO.getReceiptIdList(extractionId);

		for (Long receiptSeqID : receiptIdList) {

			// Repay Details
			List<CollectionReceiptExtraction> receiptList = this.collectionDAO.getReceiptDetailList(receiptSeqID);
			CollectionReceiptExtraction receiptExtraction = null;

			BigDecimal principal = BigDecimal.ZERO;
			BigDecimal profit = BigDecimal.ZERO;
			BigDecimal penalty = BigDecimal.ZERO;
			BigDecimal excessAmt = BigDecimal.ZERO;
			BigDecimal bounceAmt = BigDecimal.ZERO;
			BigDecimal adviseAmt = BigDecimal.ZERO;
			Integer instNumber = null;
			Date dueDate = null;

			for (CollectionReceiptExtraction detail : receiptList) {

				// Installment Amount
				if (detail.getPrincipal().add(detail.getProfit()).compareTo(BigDecimal.ZERO) > 0) {
					CollectionReceiptExtraction receipt1 = new CollectionReceiptExtraction();
					receipt1.setExtractionId(extractionId);
					receipt1.setFinReference(detail.getFinReference());
					receipt1.setAllocationType("EMI");
					receipt1.setFeeDesc(Labels.getLabel("label_AllocationType_INSTAMT"));
					receipt1.setSchDate(detail.getSchDate());
					receipt1.setProfit(detail.getProfit());
					receipt1.setPrincipal(detail.getPrincipal());
					receipt1.setAmount(detail.getPrincipal().add(detail.getProfit()));
					receipt1.setReceiptID(detail.getReceiptID());
					receipt1.setReceiptSeqID(detail.getReceiptSeqID());
					receiptExtractionlist.add(receipt1);

					if (dueDate == null || detail.getSchDate().compareTo(dueDate) > 0) {
						dueDate = detail.getSchDate();
						instNumber = detail.getInstNumber();
					}
				}

				// Penalty Amount
				if (detail.getPenalty().compareTo(BigDecimal.ZERO) > 0) {
					CollectionReceiptExtraction receipt2 = new CollectionReceiptExtraction();
					receipt2.setExtractionId(extractionId);
					receipt2.setFinReference(detail.getFinReference());
					receipt2.setAllocationType("LPP");
					receipt2.setFeeDesc(Labels.getLabel("label_AllocationType_ODCharges"));
					receipt2.setSchDate(detail.getSchDate());
					receipt2.setAmount(detail.getPenalty());
					receipt2.setReceiptID(detail.getReceiptID());
					receipt2.setReceiptSeqID(detail.getReceiptSeqID());
					receiptExtractionlist.add(receipt2);

					if (dueDate == null || detail.getSchDate().compareTo(dueDate) > 0) {
						dueDate = detail.getSchDate();
						instNumber = detail.getInstNumber();
					}
				}

				/*CollectionReceiptExtraction receipt3 = new CollectionReceiptExtraction();
 				receipt3.setExtractionId(extractionId);
				receipt3.setFinReference(detail.getFinReference());
				receipt3.setAllocationType("FEES");
				receipt3.setSchDate(detail.getSchDate());
				receipt3.setAmount(detail.getSchdFeePayNow());
				receipt3.setReceiptID(detail.getReceiptID());
				receipt3.setReceiptSeqID(detail.getReceiptSeqID());
 				list.add(receipt3);

 				CollectionReceiptExtraction receipt4 = new CollectionReceiptExtraction();
 				receipt4.setExtractionId(extractionId);
 				receipt4.setFinReference(detail.getFinReference());
 				receipt4.setAllocationType("TDS");
 				receipt4.setSchDate(detail.getSchDate());
 				receipt4.setAmount(detail.getTDSSchdPayNow());
 				receipt4.setReceiptID(detail.getReceiptID());
 				receipt4.setReceiptSeqID(detail.getReceiptSeqID());
 				list.add(receipt4);*/

				principal = principal.add(detail.getPrincipal());
				profit = profit.add(detail.getProfit());
				penalty = penalty.add(detail.getPenalty());
			}

			// Finance Excess Movements
			List<CollectionReceiptExtraction> ExcessMovementList = this.collectionDAO.getFinExcessMovements(receiptSeqID);

			for (CollectionReceiptExtraction excessMvtDetail : ExcessMovementList) {
				if (excessMvtDetail.getAmount().compareTo(BigDecimal.ZERO) > 0) {
					CollectionReceiptExtraction receipt1 = new CollectionReceiptExtraction();
					receipt1.setExtractionId(extractionId);
					receipt1.setAllocationType("OTHER");
					receipt1.setSchDate(excessMvtDetail.getSchDate());
					receipt1.setAmount(excessMvtDetail.getAmount());
					receipt1.setReceiptID(excessMvtDetail.getReceiptID());
					receipt1.setReceiptSeqID(excessMvtDetail.getReceiptSeqID());
					receipt1.setPrimaryId(excessMvtDetail.getPrimaryId());

					if (StringUtils.equals(excessMvtDetail.getAmountType(), CollectionConstants.EXAMOUNTTYPE_EXCESS)) {
						receipt1.setFeeDesc(Labels.getLabel("label_AllocationType_EXCESS"));
					} else {
						receipt1.setFeeDesc(Labels.getLabel("label_AllocationType_EMIADV"));
					}
					receiptExtractionlist.add(receipt1);

					excessAmt = excessAmt.add(excessMvtDetail.getAmount());
					if (instNumber == null && (dueDate == null || excessMvtDetail.getSchDate().compareTo(dueDate) > 0)) {
						dueDate = excessMvtDetail.getSchDate();
					}
				}
			}

			// Manual Advises
			List<CollectionReceiptExtraction> manualAdvisesList = this.collectionDAO.getManualAdvises(receiptSeqID);

			for (CollectionReceiptExtraction manualAdvise : manualAdvisesList) {
				if (manualAdvise.getAmount().compareTo(BigDecimal.ZERO) > 0) {

					CollectionReceiptExtraction receipt1 = new CollectionReceiptExtraction();
					receipt1.setExtractionId(extractionId);
					receipt1.setReceiptID(manualAdvise.getReceiptID());
					receipt1.setReceiptSeqID(manualAdvise.getReceiptSeqID());
					receipt1.setSchDate(manualAdvise.getSchDate());
					receipt1.setFinReference(manualAdvise.getFinReference());
					receipt1.setPrimaryId(manualAdvise.getPrimaryId());
					receipt1.setAmount(manualAdvise.getAmount());
					if (manualAdvise.getBounceId() > 0) {
						receipt1.setAllocationType("BOUNCE");
						receipt1.setFeeDesc(Labels.getLabel("label_AllocationType_Bounce"));
						bounceAmt = bounceAmt.add(manualAdvise.getAmount());
					} else {
						receipt1.setAllocationType("OTHER");
						receipt1.setFeeDesc(manualAdvise.getFeeDesc());
						adviseAmt = adviseAmt.add(manualAdvise.getAmount());
					}
					receiptExtractionlist.add(receipt1);
					
					if (instNumber == null && (dueDate == null || manualAdvise.getSchDate().compareTo(dueDate) > 0)) {
						dueDate = manualAdvise.getSchDate();
 					}
				}
			}

			receiptExtraction = new CollectionReceiptExtraction();
			receiptExtraction.setReceiptSeqID(receiptSeqID);
			receiptExtraction.setPrincipal(principal);
			receiptExtraction.setProfit(profit);
			receiptExtraction.setPenalty(penalty);
			receiptExtraction.setExcessAmt(excessAmt);
			receiptExtraction.setAdviseAmt(adviseAmt);
			receiptExtraction.setBounceAmt(bounceAmt);
			receiptExtraction.setDueDate(dueDate);
			receiptExtraction.setInstNumber(instNumber);

			// update Allocation Header
			this.collectionDAO.updateAllocationHeader(receiptExtraction);

			// save Allocation Details 
			if (!receiptExtractionlist.isEmpty() && receiptExtractionlist.size() >= 500) {
				this.collectionDAO.saveAllocationDetailsBatch(receiptExtractionlist);
				receiptExtractionlist.clear();
			}
		}

		// save Allocation Details 
		if(!receiptExtractionlist.isEmpty()) {
			this.collectionDAO.saveAllocationDetailsBatch(receiptExtractionlist);
			receiptExtractionlist.clear();
		}

		// Update Completed Status
		this.collectionDAO.updateReceiptProcess(extractionId);

		logger.debug("Leaving");
	}
	
	// ****************************************************************** //
	// **************** ForeClosure Charges Calculation ***************** //
	// ****************************************************************** //

	/**
	 *
	 */
	private List<CollectionFinances> calForeClosureCharges(List<CollectionFinances> list) {
		logger.debug("Entering");

		// Get the FinTypeFee for EARLYSTL event
		HashMap<String, List<CollectionFinTypeFees>> map = getFinTypeFeesList();

		for (CollectionFinances collFinance : list) {
			if (map.containsKey(collFinance.getFinType())) {

				BigDecimal totCalAmt = BigDecimal.ZERO;
				for (CollectionFinTypeFees collFinType : map.get(collFinance.getFinType())) {

					if (CollectionConstants.FEE_CALCULATION_TYPE_PERCENTAGE.equals(collFinType.getCalculationType())) {
						BigDecimal calAmt = getCalculatedPercentageFee(collFinance, collFinType);
						totCalAmt = totCalAmt.add(calAmt);
					} else if (CollectionConstants.FEE_CALCULATION_TYPE_FIXEDAMOUNT.equals(collFinType.getCalculationType())) {
						BigDecimal calAmt = collFinType.getAmount();
						totCalAmt = totCalAmt.add(calAmt);
					}
				}
				collFinance.setForeClosureCharges(totCalAmt);
			} else {
				collFinance.setForeClosureCharges(BigDecimal.ZERO);
			}
		}

		logger.debug("Leaving");
		return list;
	}

	/**
	 * Calculate PREPAYPENALTY_DUE field value in FORECLOSURE_DETAILS_V_TMP
	 * table
	 * @return 
	 */
	private HashMap<String, List<CollectionFinTypeFees>> getFinTypeFeesList() {
		logger.debug("Entering");

		HashMap<String, List<CollectionFinTypeFees>> map = new HashMap<String, List<CollectionFinTypeFees>>(1);
		List<CollectionFinTypeFees>  list = new ArrayList<CollectionFinTypeFees>();

		List<CollectionFinTypeFees> finTypeFeeList = this.collectionDAO.getFinTypeFeesList();
		for (CollectionFinTypeFees colFinTypeFee : finTypeFeeList) {

			if (map.containsKey(colFinTypeFee.getFinType())) {
				map.get(colFinTypeFee.getFinType()).add(colFinTypeFee);
			} else {
				list = new ArrayList<CollectionFinTypeFees>();
				list.add(colFinTypeFee);
				map.put(colFinTypeFee.getFinType(), list);
			}
		}

		logger.debug("Leaving");
		return map;
	}

	/**
	 * Method for calculating ForecloserChanges based on Percentage method
	 */	
	private BigDecimal getCalculatedPercentageFee(CollectionFinances collFinance, CollectionFinTypeFees collFinType) {
		logger.debug("Entering");

		BigDecimal calculatedAmt = BigDecimal.ZERO;

		switch (collFinType.getCalculateOn()) {

		case CollectionConstants.FEE_CALCULATEDON_TOTALASSETVALUE:
			calculatedAmt = collFinance.getFinAssetValue();
			break;

		case CollectionConstants.FEE_CALCULATEDON_LOANAMOUNT:
			calculatedAmt = collFinance.getFinAmount().subtract(collFinance.getDownPayment());
			break;

		case CollectionConstants.FEE_CALCULATEDON_OUTSTANDINGPRCINCIPAL:
			calculatedAmt = collFinance.getTotalPriBal();
			break;

		default:
			break;
		}

		calculatedAmt = calculatedAmt.multiply(collFinType.getPercentage()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_DOWN);

		logger.debug("Leaving");
		return calculatedAmt;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setCollectionDAO(CollectionDAO collectionDAO) {
		this.collectionDAO = collectionDAO;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public int getCurOdDays() {
		return curOdDays;
	}

	public void setCurOdDays(int curOdDays) {
		this.curOdDays = curOdDays;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public long getLastMntBy() {
		return lastMntBy;
	}

	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}
}

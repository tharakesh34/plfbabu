/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  FinancePurposeDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.backend.service.finance.impl;



import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinFeeReceiptDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.rulefactory.FinFeeScheduleDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>FinancePurposeDetail</b>.<br>
 * 
 */
public class FinFeeDetailServiceImpl extends GenericService<FinFeeDetail> implements FinFeeDetailService {
	private static final Logger logger = Logger.getLogger(FinFeeDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;

	private FinFeeDetailDAO finFeeDetailDAO;
	private FinFeeReceiptDAO finFeeReceiptDAO;
	private FinFeeScheduleDetailDAO finFeeScheduleDetailDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;

	public FinFeeDetailServiceImpl() {
		super();
	}
	
	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}
	
	public FinFeeDetailDAO getFinFeeDetailDAO() {
		return finFeeDetailDAO;
	}

	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}
	
	@Override
	public List<FinReceiptDetail> getFinReceiptDetais(String finReference) {
		logger.debug("Entering");
		
		List<FinReceiptDetail> finReceiptDetails = getFinReceiptDetailDAO().getFinReceiptDetailByFinRef(finReference);
		
		logger.debug("Leaving");
		
		return finReceiptDetails;
	}

	@Override
	public List<FinFeeReceipt> getFinFeeReceiptsById(List<Long> feeIds, String type) {
		logger.debug("Entering");
		
		List<FinFeeReceipt> finReceiptDetails = getFinFeeReceiptDAO().getFinFeeReceiptByFinRef(feeIds, type);
		
		logger.debug("Leaving");
		
		return finReceiptDetails;
	}
	
	@Override
	public List<FinFeeDetail> getFinFeeDetailById(String finReference, boolean isWIF, String type) {
		logger.debug("Entering");
		List<FinFeeDetail> finFeeDetails = getFinFeeDetailDAO().getFinFeeDetailByFinRef(finReference, isWIF, type);
		// Finance Fee Schedule Details
		if (finFeeDetails != null && !finFeeDetails.isEmpty()) {
			for (FinFeeDetail finFeeDetail : finFeeDetails) {
				finFeeDetail.setFinFeeScheduleDetailList(getFinFeeScheduleDetailDAO().getFeeScheduleByFeeID(finFeeDetail.getFeeID(), isWIF, type));
			}
		}
		logger.debug("Leaving");
		return finFeeDetails;
	}
	
	@Override
	public List<FinFeeDetail> getFinFeeDetailById(String finReference, boolean isWIF, String type, String eventCodeRef) {
		logger.debug("Entering");
		List<FinFeeDetail> finFeeDetails = getFinFeeDetailDAO().getFinFeeDetailByFinRef(finReference, isWIF, type, eventCodeRef);
		// Finance Fee Schedule Details
		if (finFeeDetails != null && !finFeeDetails.isEmpty()) {
			for (FinFeeDetail finFeeDetail : finFeeDetails) {
				finFeeDetail.setFinFeeScheduleDetailList(getFinFeeScheduleDetailDAO().getFeeScheduleByFeeID(finFeeDetail.getFeeID(), isWIF, type));
			}
		}
		logger.debug("Leaving");
		return finFeeDetails;
	}
	
	@Override
	public List<AuditDetail> saveOrUpdate(List<FinFeeDetail> finFeeDetails, String tableType, String auditTranType,boolean isWIF) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditDetails.addAll(processFinFeeDetails(finFeeDetails, tableType, auditTranType, false, isWIF));
		
		logger.debug("Leaving");
		return auditDetails;
	}
	
	@Override
	public List<AuditDetail> saveOrUpdateFinFeeReceipts(List<FinFeeReceipt> finFeeReceipts, String tableType, String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		
		auditDetails.addAll(processFinFeeReceipts(finFeeReceipts, tableType, auditTranType, false));
		
		logger.debug("Leaving");
		return auditDetails;
	}
	
	private  List<AuditDetail> processFinFeeDetails(List<FinFeeDetail>  finFeeDetails, String tableType, String auditTranType,
			boolean isApproveRcd,boolean isWIF){
		logger.debug("Entering");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (finFeeDetails != null && !finFeeDetails.isEmpty()) {
			int i = 0;
			boolean saveRecord = false;
			boolean updateRecord = false;
			boolean deleteRecord = false;
			boolean approveRec = false;

			for (FinFeeDetail finFeeDetail : finFeeDetails) {
				
				if(!isApproveRcd && (finFeeDetail.isRcdVisible() && !finFeeDetail.isDataModified())){
					continue;
				}
				
				if(StringUtils.equals(finFeeDetail.getStatus(), FinanceConstants.FEE_STATUS_CANCEL)){
					continue;
				}
				
				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = isApproveRcd;
				String rcdType = "";
				String recordStatus = "";

				if (StringUtils.isEmpty(tableType) || StringUtils.equals(tableType, PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
					approveRec = true;
					finFeeDetail.setRoleCode("");
					finFeeDetail.setNextRoleCode("");
					finFeeDetail.setTaskId("");
					finFeeDetail.setNextTaskId("");
				}
				
			//	finFeeDetail.setWorkflowId(0);		
				if (finFeeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					deleteRecord = true;
				} else if (finFeeDetail.isNewRecord()) {
					saveRecord = true;
					if (finFeeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (finFeeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
						finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (finFeeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
						finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}

				} else if (finFeeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (finFeeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
					updateRecord = true;
				} else if (finFeeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					if (approveRec) {
						deleteRecord = true;
					} else if (finFeeDetail.isNew()) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				}
				if (approveRec) {
					rcdType = finFeeDetail.getRecordType();
					recordStatus = finFeeDetail.getRecordStatus();
					finFeeDetail.setRecordType("");
					finFeeDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				
				if (saveRecord) {
					if (finFeeDetail.isNewRecord() && !approveRec) {
						finFeeDetail.setFeeSeq(getFinFeeDetailDAO().getFeeSeq(finFeeDetail, isWIF, tableType) + 1);
					}
					
					finFeeDetail.setFeeID(getFinFeeDetailDAO().save(finFeeDetail, isWIF, tableType));
					
					if(!finFeeDetail.getFinFeeScheduleDetailList().isEmpty()) {
						for (FinFeeScheduleDetail finFeeSchDetail : finFeeDetail.getFinFeeScheduleDetailList()) {
							finFeeSchDetail.setFeeID(finFeeDetail.getFeeID());
						}
						getFinFeeScheduleDetailDAO().saveFeeScheduleBatch(finFeeDetail.getFinFeeScheduleDetailList(), isWIF, tableType);
					}
				}

				if (updateRecord) {
					getFinFeeDetailDAO().update(finFeeDetail, isWIF, tableType);
					getFinFeeScheduleDetailDAO().deleteFeeScheduleBatch(finFeeDetail.getFeeID(), isWIF, tableType);
					if(!finFeeDetail.getFinFeeScheduleDetailList().isEmpty()) {
						for (FinFeeScheduleDetail finFeeSchDetail : finFeeDetail.getFinFeeScheduleDetailList()) {
							finFeeSchDetail.setFeeID(finFeeDetail.getFeeID());
						}
						getFinFeeScheduleDetailDAO().saveFeeScheduleBatch(finFeeDetail.getFinFeeScheduleDetailList(), isWIF, tableType);
					}
				}

				if (deleteRecord) {
					getFinFeeScheduleDetailDAO().deleteFeeScheduleBatch(finFeeDetail.getFeeID(), isWIF, tableType);
					getFinFeeDetailDAO().delete(finFeeDetail, isWIF, tableType);
				}

				if (approveRec) {
					finFeeDetail.setRecordType(rcdType);
					finFeeDetail.setRecordStatus(recordStatus);
				}

				String[]  fields = PennantJavaUtil.getFieldDetails(finFeeDetail, finFeeDetail.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, i+1, fields[0], fields[1], finFeeDetail.getBefImage(), finFeeDetail));
				i++;
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}
	
	private  List<AuditDetail> processFinFeeReceipts(List<FinFeeReceipt>  finFeeReceipts, String tableType, String auditTranType,
			boolean isApproveRcd){
		logger.debug("Entering");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		
		if (finFeeReceipts != null && !finFeeReceipts.isEmpty()) {
			int i = 0;
			boolean saveRecord = false;
			boolean updateRecord = false;
			boolean deleteRecord = false;
			boolean approveRec = false;
			
			for (FinFeeReceipt finFeeReceipt : finFeeReceipts) {
				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = isApproveRcd;
				String rcdType = "";
				String recordStatus = "";
				
				if (StringUtils.isEmpty(tableType) || StringUtils.equals(tableType, PennantConstants.PREAPPROVAL_TABLE_TYPE)) {
					approveRec = true;
					finFeeReceipt.setRoleCode("");
					finFeeReceipt.setNextRoleCode("");
					finFeeReceipt.setTaskId("");
					finFeeReceipt.setNextTaskId("");
				}
				
				if (finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					deleteRecord = true;
				} else if (finFeeReceipt.isNewRecord()) {
					saveRecord = true;
					if (finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						finFeeReceipt.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
						finFeeReceipt.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
						finFeeReceipt.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}
				} else if (finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
					updateRecord = true;
				} else if (finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					if (approveRec) {
						deleteRecord = true;
					} else if (finFeeReceipt.isNew()) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				}
				
				if (approveRec) {
					rcdType = finFeeReceipt.getRecordType();
					recordStatus = finFeeReceipt.getRecordStatus();
					finFeeReceipt.setRecordType("");
					finFeeReceipt.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}
				
				if (saveRecord) {
					finFeeReceipt.setId(getFinFeeReceiptDAO().save(finFeeReceipt, tableType));
				}
				
				if (updateRecord) {
					getFinFeeReceiptDAO().update(finFeeReceipt, tableType);
				}
				
				if (deleteRecord) {
					getFinFeeReceiptDAO().delete(finFeeReceipt, tableType);
				}
				
				if (approveRec) {
					finFeeReceipt.setRecordType(rcdType);
					finFeeReceipt.setRecordStatus(recordStatus);
				}
				
				String[]  fields = PennantJavaUtil.getFieldDetails(finFeeReceipt, finFeeReceipt.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, i+1, fields[0], fields[1], finFeeReceipt.getBefImage(), finFeeReceipt));
				i++;
			}
		}
		logger.debug("Leaving");
		
		return auditDetails;
	}
	
	
	@Override
	public List<AuditDetail> doApprove(List<FinFeeDetail> finFeeDetails, String tableType, String auditTranType, boolean isWIF) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		auditDetails.addAll(processFinFeeDetails(finFeeDetails, tableType, auditTranType, true, isWIF));
		
		logger.debug("Leaving");
		return auditDetails;
	}
	
	@Override
	public List<AuditDetail> doApproveFinFeeReceipts(List<FinFeeReceipt> finFeeReceipts, String tableType, String auditTranType, String finReference) {
		logger.debug("Entering");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<Long, FinFeeReceipt> map = new HashMap<Long, FinFeeReceipt>();

		if (!StringUtils.equals(PennantConstants.TRAN_DEL, auditTranType)) {
			
			FinFeeReceipt feeReceipt;
			for (FinFeeReceipt finFeeReceipt : finFeeReceipts) {
				if (!map.containsKey(finFeeReceipt.getReceiptID())) {
					feeReceipt = new FinFeeReceipt();
					feeReceipt.setReceiptID(finFeeReceipt.getReceiptID());
					feeReceipt.setPaidAmount(finFeeReceipt.getPaidAmount());
					feeReceipt.setReceiptAmount(finFeeReceipt.getReceiptAmount());
					feeReceipt.setLastMntBy(finFeeReceipt.getLastMntBy());
					feeReceipt.setAvailableAmount(finFeeReceipt.getReceiptAmount().subtract(finFeeReceipt.getPaidAmount()));
				} else {
					feeReceipt = map.get(finFeeReceipt.getReceiptID());
					feeReceipt.setPaidAmount(feeReceipt.getPaidAmount().add(finFeeReceipt.getPaidAmount()));
					feeReceipt.setAvailableAmount(feeReceipt.getReceiptAmount().subtract(feeReceipt.getPaidAmount()));
				}
				
				map.put(finFeeReceipt.getReceiptID(), feeReceipt);
			}
			
			if (ImplementationConstants.UPFRONT_ADJUST_PAYABLEADVISE) {
				createPayableAdvise(finReference, map);
			}
		}
		
		if (!ImplementationConstants.UPFRONT_ADJUST_PAYABLEADVISE) {
			createExcessAmount(finReference, map);
		}
		
		auditDetails.addAll(processFinFeeReceipts(finFeeReceipts, tableType, auditTranType, true));
		
		logger.debug("Leaving");
		return auditDetails;
	}

	private void createPayableAdvise(String finReference, Map<Long, FinFeeReceipt> map) {
		logger.debug("Entering");

		FinFeeReceipt feeReceipt;
		PFSParameter pfsParameter = SysParamUtil.getSystemParameterObject("MANUALADVISE_FEETYPEID");
		long feeTypeId = Long.valueOf(pfsParameter.getSysParmValue());
		ManualAdvise manualAdvise;

		// FIXME CH Get the latest Receipt details and update the payable.
		for (Long key : map.keySet()) {
			feeReceipt = map.get(key);
			if (feeReceipt.getAvailableAmount().compareTo(BigDecimal.ZERO) != 0) {
				manualAdvise = new ManualAdvise();
				manualAdvise.setAdviseType(2);
				manualAdvise.setFinReference(finReference);
				manualAdvise.setFeeTypeID(feeTypeId);
				manualAdvise.setSequence(0);
				manualAdvise.setAdviseAmount(feeReceipt.getAvailableAmount());
				manualAdvise.setPaidAmount(BigDecimal.ZERO);
				manualAdvise.setWaivedAmount(BigDecimal.ZERO);
				manualAdvise.setRemarks("FeeReceipt Remaining Amount");
				manualAdvise.setBounceID(0);
				manualAdvise.setReceiptID(feeReceipt.getReceiptID());
				manualAdvise.setValueDate(DateUtility.getAppDate());
				manualAdvise.setPostDate(DateUtility.getAppDate());
				manualAdvise.setReservedAmt(BigDecimal.ZERO);
				manualAdvise.setBalanceAmt(BigDecimal.ZERO);

				manualAdvise.setVersion(0);
				manualAdvise.setLastMntBy(feeReceipt.getLastMntBy());
				manualAdvise.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				manualAdvise.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				manualAdvise.setRoleCode("");
				manualAdvise.setNextRoleCode("");
				manualAdvise.setTaskId("");
				manualAdvise.setNextTaskId("");
				manualAdvise.setRecordType("");
				manualAdvise.setWorkflowId(0);

				getManualAdviseDAO().save(manualAdvise, TableType.MAIN_TAB);
			}
		}

		logger.debug("Leaving");
	}
	
	@Override
	public void createExcessAmount(String finReference, Map<Long, FinFeeReceipt> map) {
		logger.debug("Entering");

		FinFeeReceipt feeReceipt;
		FinExcessAmount finExcessAmount;

		List<FinReceiptDetail> finReceiptDetailsList = getFinReceiptDetailDAO().getFinReceiptDetailByFinRef(finReference);
		BigDecimal excessAmount = BigDecimal.ZERO;
		for (FinReceiptDetail finReceiptDetail : finReceiptDetailsList) {
			if (map != null && map.containsKey(finReceiptDetail.getReceiptID())) {
				feeReceipt = map.get(finReceiptDetail.getReceiptID());
				excessAmount = excessAmount.add(finReceiptDetail.getAmount().subtract(feeReceipt.getPaidAmount()));
			} else {
				excessAmount = excessAmount.add(finReceiptDetail.getAmount());
			}
		}

		if (excessAmount.compareTo(BigDecimal.ZERO) > 0) {
			finExcessAmount = new FinExcessAmount();
			finExcessAmount.setFinReference(finReference);
			finExcessAmount.setAmountType(RepayConstants.EXAMOUNTTYPE_EXCESS);
			finExcessAmount.setAmount(excessAmount);
			finExcessAmount.setUtilisedAmt(BigDecimal.ZERO);
			finExcessAmount.setReservedAmt(BigDecimal.ZERO);
			finExcessAmount.setBalanceAmt(excessAmount);
			getFinExcessAmountDAO().saveExcess(finExcessAmount);
		}
	
		logger.debug("Leaving");
	}
	
	@Override
	public List<AuditDetail> delete(List<FinFeeDetail> finFeeDetails, String tableType, String auditTranType, boolean isWIF) {
		logger.debug("Entering");
		
		String[] fields = null;	
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if(finFeeDetails != null && !finFeeDetails.isEmpty()) {
			int auditSeq = 1;
			for (FinFeeDetail finFeeDetail : finFeeDetails) {
				getFinFeeScheduleDetailDAO().deleteFeeScheduleBatch(finFeeDetail.getFeeID(), isWIF, tableType);
				getFinFeeDetailDAO().delete(finFeeDetail, isWIF, tableType);
				fields = PennantJavaUtil.getFieldDetails(finFeeDetail, finFeeDetail.getExcludeFields());	
				auditDetails.add(new AuditDetail(auditTranType,auditSeq, fields[0], fields[1], finFeeDetail.getBefImage(), finFeeDetail));
				auditSeq++;
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	@Override
	public List<AuditDetail> deleteFinFeeReceipts(List<FinFeeReceipt> finFeeReceipts, String tableType, String auditTranType) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = null;	
		
		if(finFeeReceipts != null && !finFeeReceipts.isEmpty()) {
			int auditSeq = 1;
			for (FinFeeReceipt finFeeReceipt : finFeeReceipts) {
				getFinFeeReceiptDAO().delete(finFeeReceipt, tableType);
				fields = PennantJavaUtil.getFieldDetails(finFeeReceipt, finFeeReceipt.getExcludeFields());	
				auditDetails.add(new AuditDetail(auditTranType,auditSeq, fields[0], fields[1], finFeeReceipt.getBefImage(), finFeeReceipt));
				auditSeq++;
			}
		}
		
		logger.debug("Leaving");
		return auditDetails;
	}
	
	private List<AuditDetail> getFinFeeDetailAuditDetail(List<FinFeeDetail> finFeeDetails, String auditTranType, String method, long workFlowId) {
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = null;	
		for (FinFeeDetail finFeeDetail : finFeeDetails) {
			
			if("doApprove".equals(method) && !StringUtils.trimToEmpty(finFeeDetail.getRecordStatus()).equals(PennantConstants.RCD_STATUS_SAVED))  {
				//finFeeDetail.setWorkflowId(0);
				/*if (StringUtils.equals(finFeeDetail.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
					finFeeDetail.setNewRecord(true);
				}*/
			} else {
				finFeeDetail.setWorkflowId(workFlowId);
			}
			
			boolean isRcdType = false;

			if (finFeeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finFeeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (finFeeDetail.getRecordType().equalsIgnoreCase(
					PennantConstants.RCD_DEL)) {
				finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				finFeeDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finFeeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finFeeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finFeeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			fields = PennantJavaUtil.getFieldDetails(finFeeDetail, finFeeDetail.getExcludeFields());
			if (StringUtils.isNotEmpty(finFeeDetail.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size()+1, fields[0], fields[1], finFeeDetail.getBefImage(), finFeeDetail));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}
	
	private List<AuditDetail> getFinFeeReceiptAuditDetail(List<FinFeeReceipt> finFeeReceipts, String auditTranType,
			String method, long workFlowId) {
		logger.debug("Entering");

		String[] fields = null;
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		
		for (FinFeeReceipt finFeeReceipt : finFeeReceipts) {
			if (finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finFeeReceipt.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			} else if (finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finFeeReceipt.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			} else if (finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finFeeReceipt.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finFeeReceipt.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			
			fields = PennantJavaUtil.getFieldDetails(finFeeReceipt, finFeeReceipt.getExcludeFields());
			
			if (StringUtils.isNotEmpty(finFeeReceipt.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, auditDetails.size() + 1, fields[0], fields[1],
						finFeeReceipt.getBefImage(), finFeeReceipt));
			}
		}

		logger.debug("Leaving");

		return auditDetails;
	}
	
	@Override
	public List<AuditDetail> validateFinFeeReceipts(FinanceDetail financeDetail, long workflowId, String method, 
			String auditTranType, String  usrLanguage, List<AuditDetail> auditDetails){
		return doValidationFinFeeReceipts(financeDetail, workflowId, method, auditTranType, usrLanguage, auditDetails);
	}
	
	@Override
	public List<AuditDetail> validate(List<FinFeeDetail> finFeeDetails, long workflowId, String method, 
			String auditTranType, String  usrLanguage,boolean isWIF){
		return doValidation(finFeeDetails, workflowId, method, auditTranType, usrLanguage, isWIF);
	}
	
	private List<AuditDetail> doValidation(List<FinFeeDetail> finFeeDetails, long workflowId, String method, 
			String auditTranType, String usrLanguage, boolean isWIF){
		logger.debug("Entering");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if(finFeeDetails != null && !finFeeDetails.isEmpty()){
			List<AuditDetail> finFeeAuditDetails = getFinFeeDetailAuditDetail(finFeeDetails, auditTranType, method, workflowId);
			for (AuditDetail auditDetail : finFeeAuditDetails) {
				validateFinFeeDetail(auditDetail, method, usrLanguage, isWIF); 
			}
			auditDetails.addAll(finFeeAuditDetails);
		}
		
		logger.debug("Leaving");
		return auditDetails ;
	}
	
	private List<AuditDetail> doValidationFinFeeReceipts(FinanceDetail financeDetail, long workflowId,
			String method, String auditTranType, String usrLanguage, List<AuditDetail> auditDetails) {
		logger.debug("Entering");
		
		List<AuditDetail> finFeeAuditDetails = new ArrayList<AuditDetail>();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		List<FinFeeReceipt> finFeeReceipts = finScheduleData.getFinFeeReceipts();

		if (finFeeReceipts != null) {
			finFeeAuditDetails = getFinFeeReceiptAuditDetail(finFeeReceipts, auditTranType, method, workflowId);
			
			for (AuditDetail auditDetail : finFeeAuditDetails) {
				validateFinFeeReceipts(auditDetail, method, usrLanguage);
			}

			//auditDetails.addAll(finFeeAuditDetails);
			if (!financeDetail.isActionSave()) {
				for (int i = 0; i < finScheduleData.getFinFeeDetailActualList().size(); i++) {
					FinFeeDetail finFeeDetail = finScheduleData.getFinFeeDetailActualList().get(i);
					BigDecimal totalPaidAmount = BigDecimal.ZERO;
					
					for (FinFeeReceipt finFeeReceipt : finFeeReceipts) {
						if (!StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, finFeeReceipt.getRecordType()) && (finFeeDetail.getFeeTypeID() == finFeeReceipt.getFeeTypeId())) {
							totalPaidAmount = totalPaidAmount.add(finFeeReceipt.getPaidAmount());
						}
					}
					
					if (finFeeDetail.getPaidAmount().compareTo(BigDecimal.ZERO) != 0) { 
						if (totalPaidAmount.compareTo(BigDecimal.ZERO) == 0) {
							String[] errParm = new String[1];
							String[] valueParm = new String[1];
							valueParm[0] = String.valueOf(finFeeDetail.getFeeTypeDesc());
							errParm[0] = valueParm[0];
							auditDetails.get(0).setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetails(PennantConstants.KEY_FIELD, "65019", errParm, valueParm), usrLanguage));
							break;
						} else if (finFeeDetail.getPaidAmount().compareTo(totalPaidAmount) != 0) {
							String[] errParm = new String[2];
							String[] valueParm = new String[1];
							valueParm[0] = String.valueOf(finFeeDetail.getFeeTypeDesc());
							errParm[0] = ":" + valueParm[0];
							errParm[1] = ":" + valueParm[0];
							auditDetails.get(0).setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetails(PennantConstants.KEY_FIELD, "65018", errParm, valueParm), usrLanguage));
							break;
						}
					}
				}
			}
		}

		logger.debug("Leaving");
		
		return finFeeAuditDetails;
	}
	
	private AuditDetail validateFinFeeDetail(AuditDetail auditDetail,String usrLanguage,String method, boolean isWIF){
		logger.debug("Entering");
		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());			
		FinFeeDetail finFeeDetail = (FinFeeDetail) auditDetail.getModelData();
		FinFeeDetail tempFinFinDetail= null;
		if (finFeeDetail.isWorkflow()){
			tempFinFinDetail = getFinFeeDetailDAO().getFinFeeDetailById(finFeeDetail, isWIF, "_Temp");
		}
		FinFeeDetail befFinFeeDetail = getFinFeeDetailDAO().getFinFeeDetailById(finFeeDetail, isWIF, "");
		FinFeeDetail oldFinFeeDetail= finFeeDetail.getBefImage();

		String[] errParm= new String[1];
		String[] valueParm= new String[1];
		valueParm[0]=String.valueOf(finFeeDetail.getFeeTypeDesc());
		errParm[0]=PennantJavaUtil.getLabel("FeeType")+":"+valueParm[0];

		if (finFeeDetail.isNew()){ // for New record or new record into work flow

			if (!finFeeDetail.isWorkflow()){// With out Work flow only new records  
				if (befFinFeeDetail !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (finFeeDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befFinFeeDetail != null || tempFinFinDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befFinFeeDetail ==null || tempFinFinDetail!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finFeeDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befFinFeeDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldFinFeeDetail!=null && !oldFinFeeDetail.getLastMntOn().equals(befFinFeeDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempFinFinDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (tempFinFinDetail!=null && oldFinFeeDetail!=null && !oldFinFeeDetail.getLastMntOn().equals(tempFinFinDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !finFeeDetail.isWorkflow()){
			auditDetail.setBefImage(befFinFeeDetail);	
		}
		return auditDetail;
	}

	private AuditDetail validateFinFeeReceipts(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
		FinFeeReceipt finFeeReceipt = (FinFeeReceipt) auditDetail.getModelData();
		FinFeeReceipt tempFinFeeReceipt = null;

		if (finFeeReceipt.isWorkflow()) {
			tempFinFeeReceipt = getFinFeeReceiptDAO().getFinFeeReceiptById(finFeeReceipt, "_Temp");
		}

		FinFeeReceipt befFinFeeReceipt = getFinFeeReceiptDAO().getFinFeeReceiptById(finFeeReceipt, "");
		FinFeeReceipt oldFinFeeReceipt = finFeeReceipt.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(finFeeReceipt.getFeeType());
		errParm[0] = PennantJavaUtil.getLabel("FeeType") + ":" + valueParm[0];

		if (finFeeReceipt.isNew()) { // for New record or new record into work flow
			if (!finFeeReceipt.isWorkflow()) {// With out Work flow only new records
				if (befFinFeeReceipt != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (finFeeReceipt.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befFinFeeReceipt != null || tempFinFeeReceipt != null) { // if records already exists in the
																					// main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befFinFeeReceipt == null || tempFinFeeReceipt != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finFeeReceipt.isWorkflow()) { // With out Work flow for update and delete
				if (befFinFeeReceipt == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinFeeReceipt != null
							&& !oldFinFeeReceipt.getLastMntOn().equals(befFinFeeReceipt.getLastMntOn())) {
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
				if (tempFinFeeReceipt == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}
				if (tempFinFeeReceipt != null && oldFinFeeReceipt != null
						&& !oldFinFeeReceipt.getLastMntOn().equals(tempFinFeeReceipt.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !finFeeReceipt.isWorkflow()) {
			auditDetail.setBefImage(befFinFeeReceipt);
		}
		
		return auditDetail;
	}
	
	public FinFeeScheduleDetailDAO getFinFeeScheduleDetailDAO() {
		return finFeeScheduleDetailDAO;
	}

	public void setFinFeeScheduleDetailDAO(FinFeeScheduleDetailDAO finFeeScheduleDetailDAO) {
		this.finFeeScheduleDetailDAO = finFeeScheduleDetailDAO;
	}

	public FinFeeReceiptDAO getFinFeeReceiptDAO() {
		return finFeeReceiptDAO;
	}

	public void setFinFeeReceiptDAO(FinFeeReceiptDAO finFeeReceiptDAO) {
		this.finFeeReceiptDAO = finFeeReceiptDAO;
	}

	public ManualAdviseDAO getManualAdviseDAO() {
		return manualAdviseDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	public FinExcessAmountDAO getFinExcessAmountDAO() {
		return finExcessAmountDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public FinReceiptDetailDAO getFinReceiptDetailDAO() {
		return finReceiptDetailDAO;
	}

	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}


}
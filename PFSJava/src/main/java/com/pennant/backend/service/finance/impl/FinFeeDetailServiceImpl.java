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
 * FileName    		:  FinancePurposeDetailServiceImpl.java                                 * 	  
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
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinFeeReceiptDAO;
import com.pennant.backend.dao.finance.FinTaxDetailsDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.rulefactory.FinFeeScheduleDetailDAO;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.expenses.UploadTaxPercent;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinTaxDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.BranchService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.service.systemmasters.ProvinceService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
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
	
	private FinTaxDetailsDAO finTaxDetailsDAO;
	
	//Newly added
	private RuleExecutionUtil							ruleExecutionUtil;
	private RuleService									ruleService;
	private BranchService								branchService;
	private transient ProvinceService 					provinceService;

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
				finFeeDetail.setFinTaxDetails(getFinTaxDetailsDAO().getFinTaxByFeeID(finFeeDetail.getFeeID(), type));
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
				finFeeDetail.setFinTaxDetails(getFinTaxDetailsDAO().getFinTaxByFeeID(finFeeDetail.getFeeID(), type));
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
	
	private List<AuditDetail> processFinFeeDetails(List<FinFeeDetail> finFeeDetails, String tableType,
			String auditTranType, boolean isApproveRcd, boolean isWIF) {
		logger.debug("Entering");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (CollectionUtils.isNotEmpty(finFeeDetails)) {
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
				FinTaxDetails finTaxDetails = finFeeDetail.getFinTaxDetails();

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
					finTaxDetails.setFeeID(finFeeDetail.getFeeID());
					
					getFinTaxDetailsDAO().save(finTaxDetails, tableType);

					if(!finFeeDetail.getFinFeeScheduleDetailList().isEmpty()) {
						for (FinFeeScheduleDetail finFeeSchDetail : finFeeDetail.getFinFeeScheduleDetailList()) {
							finFeeSchDetail.setFeeID(finFeeDetail.getFeeID());
						}
						getFinFeeScheduleDetailDAO().saveFeeScheduleBatch(finFeeDetail.getFinFeeScheduleDetailList(), isWIF, tableType);
					}
				}

				if (updateRecord) {
					getFinFeeDetailDAO().update(finFeeDetail, isWIF, tableType);
					
					if (finTaxDetails.getFinTaxID() != 0 && finTaxDetails.getFinTaxID() != Long.MIN_VALUE) {
						getFinTaxDetailsDAO().update(finTaxDetails, tableType);
					}
					
					getFinFeeScheduleDetailDAO().deleteFeeScheduleBatch(finFeeDetail.getFeeID(), isWIF, tableType);
					if(!finFeeDetail.getFinFeeScheduleDetailList().isEmpty()) {
						for (FinFeeScheduleDetail finFeeSchDetail : finFeeDetail.getFinFeeScheduleDetailList()) {
							finFeeSchDetail.setFeeID(finFeeDetail.getFeeID());
						}
						getFinFeeScheduleDetailDAO().saveFeeScheduleBatch(finFeeDetail.getFinFeeScheduleDetailList(), isWIF, tableType);
					}
				}

				if (deleteRecord) {
					getFinTaxDetailsDAO().deleteByFeeID(finFeeDetail.getFeeID(), tableType);
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
		
		if (CollectionUtils.isNotEmpty(finFeeReceipts)) {
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
					finFeeReceipt.setWorkflowId(0);
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
	public List<AuditDetail> delete(List<FinFeeDetail> finFeeDetails, String tableType, String auditTranType,
			boolean isWIF) {
		logger.debug("Entering");

		String[] fields = null;
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		if (CollectionUtils.isNotEmpty(finFeeDetails)) {
			int auditSeq = 1;
			for (FinFeeDetail finFeeDetail : finFeeDetails) {
				getFinFeeScheduleDetailDAO().deleteFeeScheduleBatch(finFeeDetail.getFeeID(), isWIF, tableType);
				getFinFeeDetailDAO().delete(finFeeDetail, isWIF, tableType);
				fields = PennantJavaUtil.getFieldDetails(finFeeDetail, finFeeDetail.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, auditSeq, fields[0], fields[1], finFeeDetail.getBefImage(), finFeeDetail));
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
		
		if (CollectionUtils.isNotEmpty(finFeeReceipts)) {
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

			if (PennantConstants.RCD_ADD.equalsIgnoreCase(finFeeDetail.getRecordType())) {
				finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(finFeeDetail.getRecordType())) {
				finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(finFeeDetail.getRecordType())) {
				finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				finFeeDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(finFeeDetail.getRecordType())) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(finFeeDetail.getRecordType())
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(finFeeDetail.getRecordType())) {
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
		AuditDetail detail = new AuditDetail();
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
						
						if (!StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, finFeeReceipt.getRecordType())) {
							if (finFeeDetail.getFeeTypeID() == 0) {
								if (StringUtils.equals(finFeeReceipt.getFeeTypeCode(), finFeeDetail.getVasReference())) {
									totalPaidAmount = totalPaidAmount.add(finFeeReceipt.getPaidAmount());
								}
							} else {
								if (finFeeDetail.getFeeTypeID() == finFeeReceipt.getFeeTypeId()) {
									totalPaidAmount = totalPaidAmount.add(finFeeReceipt.getPaidAmount());
								}
							}
						}
					}
					
					if (finFeeDetail.getPaidAmount().compareTo(BigDecimal.ZERO) != 0) { 
						String feeTypeDesc = finFeeDetail.getFeeTypeDesc();
						if (StringUtils.isBlank(feeTypeDesc)) {
							feeTypeDesc = finFeeDetail.getVasReference();
						}
						if (totalPaidAmount.compareTo(BigDecimal.ZERO) == 0) {
							String[] errParm = new String[1];
							String[] valueParm = new String[1];
							valueParm[0] = feeTypeDesc;
							errParm[0] = valueParm[0];
							detail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "65019", errParm, valueParm), usrLanguage));
							auditDetails.add(detail);
							break;
						} else if (finFeeDetail.getPaidAmount().compareTo(totalPaidAmount) != 0) {
							String[] errParm = new String[2];
							String[] valueParm = new String[1];
							valueParm[0] = feeTypeDesc;
							errParm[0] = ":" + valueParm[0];
							errParm[1] = ":" + valueParm[0];
							detail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "65018", errParm, valueParm), usrLanguage));
							auditDetails.add(detail);
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
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
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

		if (finFeeDetail.isNew()) { // for New record or new record into work flow

			if (!finFeeDetail.isWorkflow()) {// With out Work flow only new records  
				if (befFinFeeDetail !=null) {	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (finFeeDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befFinFeeDetail != null || tempFinFinDetail!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befFinFeeDetail ==null || tempFinFinDetail!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finFeeDetail.isWorkflow()){	// With out Work flow for update and delete

				if (befFinFeeDetail ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldFinFeeDetail!=null && !oldFinFeeDetail.getLastMntOn().equals(befFinFeeDetail.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempFinFinDetail==null ){ // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
				}

				if (tempFinFinDetail!=null && oldFinFeeDetail!=null && !oldFinFeeDetail.getLastMntOn().equals(tempFinFinDetail.getLastMntOn())){ 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,valueParm), usrLanguage));
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

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinFeeReceipt finFeeReceipt = (FinFeeReceipt) auditDetail.getModelData();
		FinFeeReceipt tempFinFeeReceipt = null;

		if (finFeeReceipt.isWorkflow()) {
			tempFinFeeReceipt = getFinFeeReceiptDAO().getFinFeeReceiptById(finFeeReceipt, "_Temp");
		}

		FinFeeReceipt befFinFeeReceipt = getFinFeeReceiptDAO().getFinFeeReceiptById(finFeeReceipt, "");
		FinFeeReceipt oldFinFeeReceipt = finFeeReceipt.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(finFeeReceipt.getFeeTypeDesc());
		errParm[0] = PennantJavaUtil.getLabel("FeeType") + ":" + valueParm[0];

		if (finFeeReceipt.isNew()) { // for New record or new record into work flow
			if (!finFeeReceipt.isWorkflow()) {// With out Work flow only new records
				if (befFinFeeReceipt != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (finFeeReceipt.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befFinFeeReceipt != null || tempFinFeeReceipt != null) { // if records already exists in the
																					// main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befFinFeeReceipt == null || tempFinFeeReceipt != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!finFeeReceipt.isWorkflow()) { // With out Work flow for update and delete
				if (befFinFeeReceipt == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinFeeReceipt != null
							&& !oldFinFeeReceipt.getLastMntOn().equals(befFinFeeReceipt.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41003", errParm, valueParm), usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41004", errParm, valueParm), usrLanguage));
						}
					}
				}
			} else {
				if (tempFinFeeReceipt == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}
				if (tempFinFeeReceipt != null && oldFinFeeReceipt != null
						&& !oldFinFeeReceipt.getLastMntOn().equals(tempFinFeeReceipt.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
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
	
	@Override
	public void updateTaxPercent(UploadTaxPercent taxPercent) {
		this.finFeeDetailDAO.updateTaxPercent(taxPercent);
	}
	
	@Override
	public void convertGSTFinTypeFees(FinFeeDetail finFeeDetail, FinTypeFees finTypeFee, FinanceDetail financeDetail, HashMap<String, Object> gstExecutionMap) {
		
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String finCcy = financeMain.getFinCcy();
		BigDecimal gstPercentage = actualGSTFees(finFeeDetail, finCcy, gstExecutionMap);
		BigDecimal gstActual = BigDecimal.ZERO;
		BigDecimal gstNetOriginal = BigDecimal.ZERO;
		
		if (StringUtils.equals(finTypeFee.getFeeScheduleMethod(), CalculationConstants.REMFEE_WAIVED_BY_BANK)) {
			finFeeDetail.setWaivedAmount(finTypeFee.getAmount());
		}
		
		if (StringUtils.equals(FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE, finTypeFee.getTaxComponent())) {
			gstActual = calculatePercentage(finTypeFee.getAmount(), gstPercentage, financeMain);
			gstActual = CalculationUtil.roundAmount(gstActual, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
			
			finFeeDetail.setActualAmountOriginal(finTypeFee.getAmount());
			finFeeDetail.setActualAmountGST(gstActual);
			finFeeDetail.setActualAmount(gstActual.add(finTypeFee.getAmount()));

			if (StringUtils.equals(finTypeFee.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
				finFeeDetail.setPaidAmount(finTypeFee.getAmount().add(gstActual));
				finFeeDetail.setPaidAmountOriginal(finTypeFee.getAmount());
				finFeeDetail.setPaidAmountGST(gstActual);
			}
			
		} else if (StringUtils.equals(FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE, finTypeFee.getTaxComponent())) {
			gstNetOriginal = calculateInclusivePercentage(finTypeFee.getAmount(), finFeeDetail.getCgst(),finFeeDetail.getSgst(),finFeeDetail.getUgst(),finFeeDetail.getIgst(), financeMain);
			gstNetOriginal = CalculationUtil.roundAmount(gstNetOriginal, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
			
			finFeeDetail.setNetAmount(finTypeFee.getAmount());
			finFeeDetail.setNetAmountOriginal(gstNetOriginal);
			finFeeDetail.setNetAmountGST(finTypeFee.getAmount().subtract(gstNetOriginal));
			
			finFeeDetail.setActualAmountOriginal(gstNetOriginal.add(finFeeDetail.getWaivedAmount()));
			
			BigDecimal actualGst = calculatePercentage(gstNetOriginal.subtract(finFeeDetail.getWaivedAmount()), gstPercentage, financeMain);
			actualGst = CalculationUtil.roundAmount(actualGst, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
			
			finFeeDetail.setActualAmountGST(actualGst);
			finFeeDetail.setActualAmount(finFeeDetail.getActualAmountOriginal().add(actualGst));
			
			if (StringUtils.equals(finTypeFee.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
				finFeeDetail.setPaidAmount(finTypeFee.getAmount());
			}
		}
	}
	
	@Override
	public void processGSTCalForRule(FinFeeDetail finFeeDetail, BigDecimal feeResult, FinanceDetail financeDetail, String branchCode) {
		logger.debug(Literal.ENTERING);
		
		 FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String finCcy = financeMain.getFinCcy();
		int formatter = CurrencyUtil.getFormat(finCcy);
		
		HashMap<String, Object> gstExecutionMap = prepareGstMappingDetails(financeDetail, branchCode);
		BigDecimal gstPercentage = actualGSTFees(finFeeDetail, finCcy, gstExecutionMap);
		BigDecimal gstActual = BigDecimal.ZERO;
		
		if (StringUtils.equals(FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE, finFeeDetail.getTaxComponent())) {
			gstActual = feeResult.multiply(gstPercentage.divide(BigDecimal.valueOf(100), formatter, RoundingMode.HALF_DOWN));
			
			if (!finFeeDetail.isFeeModified()) {
				finFeeDetail.setActualAmountOriginal(feeResult);
				finFeeDetail.setActualAmountGST(gstActual);
				finFeeDetail.setActualAmount(gstActual.add(feeResult));
				
				if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
					finFeeDetail.setPaidAmount(gstActual.add(feeResult));
					finFeeDetail.setPaidAmountOriginal(feeResult);
					finFeeDetail.setPaidAmountGST(gstActual);
				}
			}
			
		} else {
			BigDecimal gstNetOriginal =  calculateInclusivePercentage(feeResult, finFeeDetail.getCgst(),finFeeDetail.getSgst(),finFeeDetail.getUgst(),finFeeDetail.getIgst(), financeMain);
			
			finFeeDetail.setNetAmount(feeResult);
			finFeeDetail.setNetAmountOriginal(gstNetOriginal);
			finFeeDetail.setNetAmountGST(feeResult.subtract(gstNetOriginal));
			
			finFeeDetail.setActualAmountOriginal(gstNetOriginal.add(finFeeDetail.getWaivedAmount()));
			
			BigDecimal actualGst = calculatePercentage(gstNetOriginal.subtract(finFeeDetail.getWaivedAmount()), gstPercentage, financeMain);
			finFeeDetail.setActualAmountGST(actualGst);
			finFeeDetail.setActualAmount(finFeeDetail.getActualAmountOriginal().add(actualGst));

			if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
				finFeeDetail.setPaidAmount(finFeeDetail.getActualAmountOriginal().add(actualGst));
				finFeeDetail.setPaidAmountOriginal(gstNetOriginal.add(finFeeDetail.getWaivedAmount()));
				finFeeDetail.setPaidAmountGST(actualGst);
			} 
		}
		
		logger.debug(Literal.LEAVING);
	}
	
	@Override
	public void processGSTCalForPercentage(FinFeeDetail finFeeDetail, BigDecimal calPercentageFee, FinanceDetail financeDetail, String branchCode) {
		logger.debug(Literal.ENTERING);
		
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String finCcy = financeMain.getFinCcy();
		HashMap<String, Object> gstExecutionMap = prepareGstMappingDetails(financeDetail, branchCode);
		BigDecimal gstPercentage = actualGSTFees(finFeeDetail, finCcy, gstExecutionMap);
		BigDecimal gstActual = BigDecimal.ZERO;
		int formatter = CurrencyUtil.getFormat(finCcy);
		
		if (StringUtils.equals(FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE, finFeeDetail.getTaxComponent())) {
			gstActual = calPercentageFee.multiply(gstPercentage.divide(BigDecimal.valueOf(100), formatter, RoundingMode.HALF_DOWN));
			
			if (!finFeeDetail.isFeeModified()) {
				finFeeDetail.setActualAmountOriginal(calPercentageFee);
				finFeeDetail.setActualAmountGST(gstActual);
				finFeeDetail.setActualAmount(gstActual.add(calPercentageFee));
				
				if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
					finFeeDetail.setPaidAmount(gstActual.add(calPercentageFee));
					finFeeDetail.setPaidAmountOriginal(calPercentageFee);
					finFeeDetail.setPaidAmountGST(gstActual);
				}
			}
		} else {
			BigDecimal gstNetOriginal =  calculateInclusivePercentage(calPercentageFee, finFeeDetail.getCgst(),finFeeDetail.getSgst(),finFeeDetail.getUgst(),finFeeDetail.getIgst(), financeMain);
			
			finFeeDetail.setNetAmount(calPercentageFee);
			finFeeDetail.setNetAmountOriginal(gstNetOriginal);
			finFeeDetail.setNetAmountGST(calPercentageFee.subtract(gstNetOriginal));
			
			finFeeDetail.setActualAmountOriginal(gstNetOriginal.add(finFeeDetail.getWaivedAmount()));
			
			BigDecimal actualGst = calculatePercentage(gstNetOriginal.subtract(finFeeDetail.getWaivedAmount()), gstPercentage, financeMain);
			finFeeDetail.setActualAmountGST(actualGst);
			finFeeDetail.setActualAmount(finFeeDetail.getActualAmountOriginal().add(actualGst));

			if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(), CalculationConstants.REMFEE_PAID_BY_CUSTOMER)) {
				finFeeDetail.setPaidAmount(finFeeDetail.getActualAmountOriginal().add(actualGst));
				finFeeDetail.setPaidAmountOriginal(gstNetOriginal.add(finFeeDetail.getWaivedAmount()));
				finFeeDetail.setPaidAmountGST(actualGst);
			} 
		}
		
		logger.debug(Literal.LEAVING);
	}
	
	@Override
	public BigDecimal actualGSTFees(FinFeeDetail finFeeDetail, String finCcy, HashMap<String, Object> gstExecutionMap) {
		logger.debug(Literal.ENTERING);
	
		BigDecimal cgstPercentage = BigDecimal.ZERO;
		BigDecimal igstPercentage = BigDecimal.ZERO;
		BigDecimal ugstPercentage = BigDecimal.ZERO;
		BigDecimal sgstPercentage = BigDecimal.ZERO;
		BigDecimal tgstPercentage = BigDecimal.ZERO;
		
		List<Rule> rules = ruleService.getGSTRuleDetails(RuleConstants.MODULE_GSTRULE, "");
		
		for (Rule rule : rules) {
			if (StringUtils.equals(RuleConstants.CODE_CGST, rule.getRuleCode())) {
				cgstPercentage = getFeeResult(rule.getSQLRule(), gstExecutionMap, finCcy);
				tgstPercentage = tgstPercentage.add(cgstPercentage);
			} else if (StringUtils.equals(RuleConstants.CODE_IGST, rule.getRuleCode())) {
				igstPercentage = getFeeResult(rule.getSQLRule(), gstExecutionMap, finCcy);
				tgstPercentage = tgstPercentage.add(igstPercentage);
			} else if (StringUtils.equals(RuleConstants.CODE_SGST, rule.getRuleCode())) {
				sgstPercentage = getFeeResult(rule.getSQLRule(), gstExecutionMap, finCcy);
				tgstPercentage = tgstPercentage.add(sgstPercentage);
			} else if (StringUtils.equals(RuleConstants.CODE_UGST, rule.getRuleCode())) {
				ugstPercentage = getFeeResult(rule.getSQLRule(), gstExecutionMap, finCcy);
				tgstPercentage = tgstPercentage.add(ugstPercentage);
			}
		}
		
		finFeeDetail.setCgst(cgstPercentage);
		finFeeDetail.setIgst(igstPercentage);
		finFeeDetail.setSgst(sgstPercentage);
		finFeeDetail.setUgst(ugstPercentage);
		
		logger.debug("Leaving");
		
		return tgstPercentage;
	}
	
	private BigDecimal calculateInclusivePercentage(BigDecimal amount, BigDecimal cgstPerc, BigDecimal sgstPerc, 
			BigDecimal ugstPerc, BigDecimal igstPerc, FinanceMain financeMain) {
		logger.debug(Literal.ENTERING);
		
		if(amount.compareTo(BigDecimal.ZERO) == 0){
			return BigDecimal.ZERO;
		}
		
		BigDecimal totalGSTPerc = cgstPerc.add(sgstPerc).add(ugstPerc).add(igstPerc);
		BigDecimal percentage = (totalGSTPerc.add(new BigDecimal(100))).divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_DOWN);
		BigDecimal actualAmt = amount.divide(percentage, 9, RoundingMode.HALF_DOWN);
		actualAmt = CalculationUtil.roundAmount(actualAmt, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
		BigDecimal actTaxAmount = amount.subtract(actualAmt);
		
		BigDecimal gstAmount = BigDecimal.ZERO;
		if(cgstPerc.compareTo(BigDecimal.ZERO) > 0){
			BigDecimal cgst = (actTaxAmount.multiply(cgstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
			cgst = CalculationUtil.roundAmount(cgst, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
			gstAmount = gstAmount.add(cgst);
		}
		if(sgstPerc.compareTo(BigDecimal.ZERO) > 0){
			BigDecimal sgst = (actTaxAmount.multiply(sgstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
			sgst = CalculationUtil.roundAmount(sgst, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
			gstAmount = gstAmount.add(sgst);
		}
		if(ugstPerc.compareTo(BigDecimal.ZERO) > 0){
			BigDecimal ugst = (actTaxAmount.multiply(ugstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
			ugst = CalculationUtil.roundAmount(ugst, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
			gstAmount = gstAmount.add(ugst);
		}
		if(igstPerc.compareTo(BigDecimal.ZERO) > 0){
			BigDecimal igst = (actTaxAmount.multiply(igstPerc)).divide(totalGSTPerc, 9, RoundingMode.HALF_DOWN);
			igst = CalculationUtil.roundAmount(igst, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
			gstAmount = gstAmount.add(igst);
		}
		
		logger.debug(Literal.LEAVING);
		return amount.subtract(gstAmount);
	}
	
	/**
	 * Method for Processing of SQL Rule and get Executed Result
	 * 
	 * @return
	 */
	@Override
	public BigDecimal getFeeResult(String sqlRule, HashMap<String, Object> executionMap,String finCcy) {
		logger.debug("Entering");
		
		BigDecimal result = BigDecimal.ZERO;
		try {
			Object exereslut = this.ruleExecutionUtil.executeRule(sqlRule, executionMap, finCcy, RuleReturnType.DECIMAL);
			if (exereslut == null || StringUtils.isEmpty(exereslut.toString())) {
				result = BigDecimal.ZERO;
			} else {
				result = new BigDecimal(exereslut.toString());
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		
		logger.debug("Leaving");
		
		return result;
	}
	
	@Override
	public void calculateGSTFees(FinFeeDetail finFeeDetail, FinanceMain financeMain, HashMap<String, Object> gstExecutionMap) {
		logger.debug(Literal.ENTERING);
		
		BigDecimal netAmountOriginal = finFeeDetail.getActualAmountOriginal().subtract(finFeeDetail.getWaivedAmount());
		BigDecimal paidAmountOriginal = finFeeDetail.getPaidAmountOriginal();
		BigDecimal remainingAmountOriginal = finFeeDetail.getRemainingFeeOriginal();
		String finCcy = financeMain.getFinCcy();
		
		FinTaxDetails finTaxDetails = null;
		if (finFeeDetail.getFinTaxDetails() == null) {
			finTaxDetails = new FinTaxDetails();
		} else {
			finTaxDetails = finFeeDetail.getFinTaxDetails();
		}
		
		if (finFeeDetail.isTaxApplicable()) {
			
			BigDecimal tgstPercentage = actualGSTFees(finFeeDetail, finCcy, gstExecutionMap);
			BigDecimal cgstPercentage = finFeeDetail.getCgst();
			BigDecimal igstPercentage = finFeeDetail.getIgst();
			BigDecimal ugstPercentage = finFeeDetail.getUgst();
			BigDecimal sgstPercentage = finFeeDetail.getSgst();

			if (StringUtils.equals(FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE, finFeeDetail.getTaxComponent())) {
				
				//Actual Amounts
				BigDecimal actualOriginal = finFeeDetail.getActualAmountOriginal();
				
				finTaxDetails.setActualCGST(calculatePercentage(actualOriginal, cgstPercentage, financeMain));
				finTaxDetails.setActualIGST(calculatePercentage(actualOriginal, igstPercentage, financeMain));
				finTaxDetails.setActualSGST(calculatePercentage(actualOriginal, sgstPercentage, financeMain));
				finTaxDetails.setActualUGST(calculatePercentage(actualOriginal, ugstPercentage, financeMain));
				
				BigDecimal actualTGST = finTaxDetails.getActualCGST().add(finTaxDetails.getActualIGST()).add(finTaxDetails.getActualUGST()).add(finTaxDetails.getActualSGST());
				actualTGST = CalculationUtil.roundAmount(actualTGST, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
				finFeeDetail.setActualAmountGST(actualTGST);
				finFeeDetail.setActualAmount(actualOriginal.add(actualTGST));
				
				//Paid Amounts
				finTaxDetails.setPaidCGST(calculatePercentage(paidAmountOriginal, cgstPercentage, financeMain));
				finTaxDetails.setPaidIGST(calculatePercentage(paidAmountOriginal, igstPercentage, financeMain));
				finTaxDetails.setPaidSGST(calculatePercentage(paidAmountOriginal, sgstPercentage, financeMain));
				finTaxDetails.setPaidUGST(calculatePercentage(paidAmountOriginal, ugstPercentage, financeMain));
				
				//Net Amounts
				finTaxDetails.setNetCGST(calculatePercentage(netAmountOriginal, cgstPercentage, financeMain));
				finTaxDetails.setNetIGST(calculatePercentage(netAmountOriginal, igstPercentage, financeMain));
				finTaxDetails.setNetSGST(calculatePercentage(netAmountOriginal, sgstPercentage, financeMain));
				finTaxDetails.setNetUGST(calculatePercentage(netAmountOriginal, ugstPercentage, financeMain));

				//Remaining Fee
				finTaxDetails.setRemFeeSGST(calculatePercentage(remainingAmountOriginal, cgstPercentage, financeMain));
				finTaxDetails.setRemFeeIGST(calculatePercentage(remainingAmountOriginal, igstPercentage, financeMain));
				finTaxDetails.setRemFeeCGST(calculatePercentage(remainingAmountOriginal, sgstPercentage, financeMain));
				finTaxDetails.setRemFeeUGST(calculatePercentage(remainingAmountOriginal, ugstPercentage, financeMain));

				// Total Paid GST
				BigDecimal paidTGST = finTaxDetails.getPaidCGST().add(finTaxDetails.getPaidIGST()).add(finTaxDetails.getPaidUGST()).add(finTaxDetails.getPaidSGST());
				paidTGST = CalculationUtil.roundAmount(paidTGST, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
				
				finTaxDetails.setPaidTGST(paidTGST);
				finFeeDetail.setPaidAmountGST(paidTGST);
				finFeeDetail.setPaidAmount(paidAmountOriginal.add(paidTGST));
				
				// Total Net GST
				BigDecimal netTGST = finTaxDetails.getNetCGST().add(finTaxDetails.getNetIGST()).add(finTaxDetails.getNetUGST()).add(finTaxDetails.getNetSGST());
				netTGST = CalculationUtil.roundAmount(netTGST, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
				
				finTaxDetails.setNetTGST(netTGST);
				finFeeDetail.setNetAmountGST(netTGST);
				finFeeDetail.setNetAmountOriginal(netAmountOriginal);
				finFeeDetail.setNetAmount(netAmountOriginal.add(netTGST));
				
				// Total Rem Fee Gst
				BigDecimal remTGST = finFeeDetail.getNetAmountGST().subtract(finFeeDetail.getPaidAmountGST());
				remTGST = CalculationUtil.roundAmount(remTGST, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
				
				finTaxDetails.setRemFeeTGST(remTGST);
				finFeeDetail.setRemainingFeeGST(remTGST);
				finFeeDetail.setRemainingFeeOriginal(finFeeDetail.getActualAmountOriginal().subtract(finFeeDetail.getPaidAmountOriginal()).subtract(finFeeDetail.getWaivedAmount()));
				finFeeDetail.setRemainingFee(finFeeDetail.getNetAmount().subtract(finFeeDetail.getPaidAmount()));
			
			} else if (StringUtils.equals(FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE, finFeeDetail.getTaxComponent())) {
				
				//Net Amount
				BigDecimal totalNetFee = finFeeDetail.getNetAmount().subtract(finFeeDetail.getWaivedAmount());
				BigDecimal netFeeOriginal = calculateInclusivePercentage(totalNetFee, cgstPercentage,sgstPercentage, ugstPercentage, igstPercentage, financeMain);
				
				finTaxDetails.setNetCGST(calculatePercentage(netFeeOriginal, cgstPercentage, financeMain));
				finTaxDetails.setNetIGST(calculatePercentage(netFeeOriginal, igstPercentage, financeMain));
				finTaxDetails.setNetSGST(calculatePercentage(netFeeOriginal, sgstPercentage, financeMain));
				finTaxDetails.setNetUGST(calculatePercentage(netFeeOriginal, ugstPercentage, financeMain));
				
				BigDecimal netTGST = finTaxDetails.getNetCGST().add(finTaxDetails.getNetIGST()).add(finTaxDetails.getNetUGST()).add(finTaxDetails.getNetSGST());
				netTGST = CalculationUtil.roundAmount(netTGST, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
				
				//finFeeDetail.setNetAmountOriginal(netFeeOriginal);
				finFeeDetail.setNetAmountOriginal(totalNetFee.subtract(netTGST));
				finFeeDetail.setNetAmountGST(netTGST);
				finFeeDetail.setNetAmount(totalNetFee);
				finTaxDetails.setNetTGST(netTGST);
				
				//Actual Amounts
				//BigDecimal actualOriginal = netFeeOriginal.add(finFeeDetail.getWaivedAmount());
				BigDecimal actualGst = calculatePercentage(netFeeOriginal.subtract(finFeeDetail.getWaivedAmount()), tgstPercentage, financeMain);
				actualGst = CalculationUtil.roundAmount(actualGst, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
				
				//finFeeDetail.setActualAmountOriginal(actualOriginal);
				finFeeDetail.setActualAmountOriginal(totalNetFee.subtract(netTGST));
				finFeeDetail.setActualAmountGST(actualGst);
				finFeeDetail.setActualAmount(finFeeDetail.getActualAmountOriginal().add(actualGst));
				
				finTaxDetails.setActualCGST(calculatePercentage(netFeeOriginal.subtract(finFeeDetail.getWaivedAmount()), cgstPercentage, financeMain));
				finTaxDetails.setActualIGST(calculatePercentage(netFeeOriginal.subtract(finFeeDetail.getWaivedAmount()), igstPercentage, financeMain));
				finTaxDetails.setActualSGST(calculatePercentage(netFeeOriginal.subtract(finFeeDetail.getWaivedAmount()), sgstPercentage, financeMain));
				finTaxDetails.setActualUGST(calculatePercentage(netFeeOriginal.subtract(finFeeDetail.getWaivedAmount()), ugstPercentage, financeMain));
				finTaxDetails.setActualTGST(actualGst);
			
				//Paid Amounts
				BigDecimal totalPaidFee = finFeeDetail.getPaidAmount();
				BigDecimal paidFeeOriginal = calculateInclusivePercentage(totalPaidFee, cgstPercentage,sgstPercentage,ugstPercentage,igstPercentage, financeMain);
				
				finTaxDetails.setPaidCGST(calculatePercentage(paidFeeOriginal, cgstPercentage, financeMain));
				finTaxDetails.setPaidIGST(calculatePercentage(paidFeeOriginal, igstPercentage, financeMain));
				finTaxDetails.setPaidSGST(calculatePercentage(paidFeeOriginal, sgstPercentage, financeMain));
				finTaxDetails.setPaidUGST(calculatePercentage(paidFeeOriginal, ugstPercentage, financeMain));
				
				BigDecimal paidTGST = finTaxDetails.getPaidCGST().add(finTaxDetails.getPaidIGST()).add(finTaxDetails.getPaidUGST()).add(finTaxDetails.getPaidSGST());
				paidTGST = CalculationUtil.roundAmount(paidTGST, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
				//finFeeDetail.setPaidAmountOriginal(paidFeeOriginal);
				finFeeDetail.setPaidAmountOriginal(totalPaidFee.subtract(paidTGST));
				finFeeDetail.setPaidAmountGST(paidTGST);
				finTaxDetails.setPaidTGST(paidTGST);
				
				//Remaining Amount
				BigDecimal totalRemainingFee = totalNetFee.subtract(totalPaidFee);
				BigDecimal remainingFeeOriginal = calculateInclusivePercentage(totalRemainingFee,cgstPercentage,sgstPercentage,ugstPercentage,igstPercentage, financeMain);
				
				finTaxDetails.setRemFeeCGST(calculatePercentage(remainingFeeOriginal, cgstPercentage, financeMain));
				finTaxDetails.setRemFeeIGST(calculatePercentage(remainingFeeOriginal, igstPercentage, financeMain));
				finTaxDetails.setRemFeeSGST(calculatePercentage(remainingFeeOriginal, sgstPercentage, financeMain));
				finTaxDetails.setRemFeeUGST(calculatePercentage(remainingFeeOriginal, ugstPercentage, financeMain));
				
				BigDecimal remFeeTGST = finTaxDetails.getRemFeeCGST().add(finTaxDetails.getRemFeeIGST()).add(finTaxDetails.getRemFeeUGST()).add(finTaxDetails.getRemFeeSGST());
				remFeeTGST = CalculationUtil.roundAmount(remFeeTGST, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
				
				//finFeeDetail.setRemainingFeeOriginal(remainingFeeOriginal);
				finFeeDetail.setRemainingFeeOriginal(totalRemainingFee.subtract(remFeeTGST));
				finFeeDetail.setRemainingFee(totalRemainingFee);
				finFeeDetail.setRemainingFeeGST(remFeeTGST);
				finTaxDetails.setRemFeeTGST(remFeeTGST);
			} 
		} else {
			
			//Net Amount
			finFeeDetail.setNetAmountOriginal(netAmountOriginal);
			finFeeDetail.setNetAmountGST(BigDecimal.ZERO);
			finFeeDetail.setNetAmount(netAmountOriginal);
			
			//Remaining Amount
			finFeeDetail.setRemainingFeeOriginal(finFeeDetail.getActualAmountOriginal().subtract(finFeeDetail.getWaivedAmount()).subtract(finFeeDetail.getPaidAmount()));
			finFeeDetail.setRemainingFeeGST(BigDecimal.ZERO);
			finFeeDetail.setRemainingFee(finFeeDetail.getActualAmount().subtract(finFeeDetail.getWaivedAmount()).subtract(finFeeDetail.getPaidAmount()));
			
			//Paid Amount
			finFeeDetail.setPaidAmountOriginal(finFeeDetail.getPaidAmount());
			finFeeDetail.setPaidAmountGST(BigDecimal.ZERO);

			//Actual Fee
			finTaxDetails.setActualCGST(BigDecimal.ZERO);
			finTaxDetails.setActualIGST(BigDecimal.ZERO);
			finTaxDetails.setActualSGST(BigDecimal.ZERO);
			finTaxDetails.setActualUGST(BigDecimal.ZERO);
			finTaxDetails.setActualTGST(BigDecimal.ZERO);
			
			//Paid Fee
			finTaxDetails.setPaidCGST(BigDecimal.ZERO);
			finTaxDetails.setPaidIGST(BigDecimal.ZERO);
			finTaxDetails.setPaidSGST(BigDecimal.ZERO);
			finTaxDetails.setPaidUGST(BigDecimal.ZERO);
			finTaxDetails.setPaidTGST(BigDecimal.ZERO);
			
			//Net Fee
			finTaxDetails.setNetCGST(BigDecimal.ZERO);
			finTaxDetails.setNetIGST(BigDecimal.ZERO);
			finTaxDetails.setNetSGST(BigDecimal.ZERO);
			finTaxDetails.setNetUGST(BigDecimal.ZERO);
			finTaxDetails.setNetTGST(BigDecimal.ZERO);
			
			//Remaining Fee
			finTaxDetails.setRemFeeCGST(BigDecimal.ZERO);
			finTaxDetails.setRemFeeIGST(BigDecimal.ZERO);
			finTaxDetails.setRemFeeSGST(BigDecimal.ZERO);
			finTaxDetails.setRemFeeUGST(BigDecimal.ZERO);
			finTaxDetails.setRemFeeTGST(BigDecimal.ZERO);
		}
		
		finFeeDetail.setFinTaxDetails(finTaxDetails);
		
		logger.debug(Literal.LEAVING);
	}
	
	@Override
	public BigDecimal calculatePercentage (BigDecimal amount, BigDecimal gstPercentage, FinanceMain financeMain) {
		logger.debug(Literal.ENTERING);
		
		BigDecimal result = BigDecimal.ZERO;
		int formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());
		
		result = amount.multiply(gstPercentage.divide(BigDecimal.valueOf(100), formatter, RoundingMode.HALF_DOWN));
		
		result = CalculationUtil.roundAmount(result, financeMain.getCalRoundingMode(), financeMain.getRoundingTarget());
		
		logger.debug(Literal.LEAVING);
		return result;
	}
	
	@Override
	public HashMap<String, Object> prepareGstMappingDetails(FinanceDetail financeDetail, String branchCode) {
		
		HashMap<String, Object> gstExecutionMap = new HashMap<>();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String fromBranchCode = financeMain.getFinBranch();
		boolean gstExempted = false;
		
		if (financeDetail.getCustomerDetails() != null) {
			if (fromBranchCode == null) {
				fromBranchCode = financeDetail.getCustomerDetails().getCustomer().getCustDftBranch();
			}
			
			Branch fromBranch = this.branchService.getApprovedBranchById(fromBranchCode);
			Province fromState = this.provinceService.getApprovedProvinceById(fromBranch.getBranchCountry(), fromBranch.getBranchProvince());
			
			if (fromState != null) {
				gstExecutionMap.put("fromState", fromState.getCPProvince());
				gstExecutionMap.put("fromUnionTerritory", fromState.isUnionTerritory());
				gstExecutionMap.put("fromStateGstExempted", fromState.isTaxExempted());
			}
			
			String toStateCode = "";
			String toCountryCode = "";
			FinanceTaxDetail finTaxDetail = financeDetail.getFinanceTaxDetails();
			
			if (finTaxDetail != null && StringUtils.isNotBlank(finTaxDetail.getApplicableFor()) 
					&& !PennantConstants.List_Select.equals(finTaxDetail.getApplicableFor())
					&& StringUtils.isNotBlank(finTaxDetail.getProvince())
					&& StringUtils.isNotBlank(finTaxDetail.getCountry())) {
				toStateCode = finTaxDetail.getProvince();
				toCountryCode = finTaxDetail.getCountry();
				gstExempted = finTaxDetail.isTaxExempted();
			} else {
				List<CustomerAddres> addressList = financeDetail.getCustomerDetails().getAddressList();
				if (CollectionUtils.isNotEmpty(addressList)) {
					for (CustomerAddres customerAddres : addressList) {
						if (customerAddres.getCustAddrPriority() == Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
							toStateCode = customerAddres.getCustAddrProvince();
							toCountryCode = customerAddres.getCustAddrCountry();
							break;
						}
					}
				}
			}
			
			if (StringUtils.isBlank(toCountryCode) || StringUtils.isBlank(toStateCode)) {	// if toCountry is not available 
				gstExecutionMap.put("toState", "");
				gstExecutionMap.put("toUnionTerritory", 2);
				gstExecutionMap.put("toStateGstExempted", "");
			} else {
				Province toState = this.provinceService.getApprovedProvinceById(toCountryCode, toStateCode);
				gstExecutionMap.put("toState", toState.getCPProvince());
				gstExecutionMap.put("toUnionTerritory", toState.isUnionTerritory());
				gstExecutionMap.put("toStateGstExempted", toState.isTaxExempted());
			}
			
			gstExecutionMap.put("gstExempted", gstExempted);
			
		} else if (StringUtils.isNotBlank(branchCode)) {
			Branch fromBranch = this.branchService.getApprovedBranchById(branchCode);
			Province fromState = this.provinceService.getApprovedProvinceById(fromBranch.getBranchCountry(), fromBranch.getBranchProvince());
			
			if (fromState != null) {
				gstExecutionMap.put("fromState", fromState.getCPProvince());
				gstExecutionMap.put("fromUnionTerritory", fromState.isUnionTerritory());
				gstExecutionMap.put("fromStateGstExempted", fromState.isTaxExempted());
			}
			
			String toStateCode = "";
			String toCountryCode = "";
			FinanceTaxDetail finTaxDetail = financeDetail.getFinanceTaxDetails();
			
			if (finTaxDetail != null && StringUtils.isNotBlank(finTaxDetail.getApplicableFor()) 
					&& !PennantConstants.List_Select.equals(finTaxDetail.getApplicableFor())
					&& StringUtils.isNotBlank(finTaxDetail.getProvince())
					&& StringUtils.isNotBlank(finTaxDetail.getCountry())) {
				toStateCode = finTaxDetail.getProvince();
				toCountryCode = finTaxDetail.getCountry();
				gstExempted = finTaxDetail.isTaxExempted();
			}
			
			if (StringUtils.isBlank(toCountryCode) || StringUtils.isBlank(toStateCode)) {
				gstExecutionMap.put("toState", fromState.getCPProvince());
				gstExecutionMap.put("toUnionTerritory", fromState.isUnionTerritory());
				gstExecutionMap.put("toStateGstExempted", fromState.isTaxExempted());
			} else {
				Province toState = this.provinceService.getApprovedProvinceById(toCountryCode, toStateCode);
				gstExecutionMap.put("toState", toState.getCPProvince());
				gstExecutionMap.put("toUnionTerritory", toState.isUnionTerritory());
				gstExecutionMap.put("toStateGstExempted", toState.isTaxExempted());
			}
			
			gstExecutionMap.put("gstExempted", gstExempted);
			
		}
		
		return gstExecutionMap;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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
	
	public FinTaxDetailsDAO getFinTaxDetailsDAO() {
		return finTaxDetailsDAO;
	}

	public void setFinTaxDetailsDAO(FinTaxDetailsDAO finTaxDetailsDAO) {
		this.finTaxDetailsDAO = finTaxDetailsDAO;
	}
	
	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}
	
	public void setProvinceService(ProvinceService provinceService) {
		this.provinceService = provinceService;
	}
}
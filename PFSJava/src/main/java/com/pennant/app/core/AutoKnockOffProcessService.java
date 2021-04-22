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
 *
 * FileName    		:  FinMaturityService.java												*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  24-12-2017															*
 *                                                                  
 * Modified Date    :  24-12-2017															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-12-2017       Pennant	                 0.1                                            * 
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
package com.pennant.app.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.RepaymentProcessUtil;
import com.pennant.backend.dao.applicationmaster.EntityDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.receipts.ReceiptAllocationDetailDAO;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.AutoKnockOffData;
import com.pennant.backend.model.finance.AutoKnockOffFeeMapping;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.XcessPayables;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.pennapps.core.resource.Literal;

public class AutoKnockOffProcessService extends ServiceHelper {
	private static final long serialVersionUID = 1L;

	private transient RepaymentProcessUtil repaymentProcessUtil;
	private transient ReceiptCalculator receiptCalculator;
	private transient FinReceiptHeaderDAO finReceiptHeaderDAO;
	private transient ReceiptAllocationDetailDAO receiptAllocationDetailDAO;
	private transient FinODPenaltyRateDAO finODPenaltyRateDAO;
	private transient FinanceProfitDetailDAO profitDetailsDAO;
	private transient EntityDAO entityDAO;
	private transient ManualAdviseDAO manualAdviseDAO;

	public void processAutoKnockOff(AutoKnockOffData knockOffData) throws Exception {
		String finreference = knockOffData.getFinReference();
		List<AutoKnockOffFeeMapping> feeMappingList = knockOffData.getFeeMappingList();

		FinReceiptData receiptData = getFinReceiptDataById(finreference, AccountEventConstants.ACCEVENT_REPAY);

		BigDecimal receiptAmount = BigDecimal.ZERO;
		BigDecimal availableAmount = knockOffData.getBalAmount();
		BigDecimal emiAmount = BigDecimal.ZERO;

		FinReceiptHeader header = receiptData.getReceiptHeader();

		header.setReference(finreference);
		header.setReceiptDate(knockOffData.getValueDate());
		header.setValueDate(knockOffData.getValueDate());
		header.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		header.setRecAgainst(RepayConstants.RECEIPTTO_FINANCE);
		header.setKnockOffType(RepayConstants.KNOCKOFF_TYPE_AUTO);
		//header.setPayAgainstId(knockOffData.getPayableId());
		header.setReceiptPurpose(FinanceConstants.FINSER_EVENT_SCHDRPY);
		header.setExcessAdjustTo(RepayConstants.EXCESSADJUSTTO_EXCESS);
		header.setAllocationType(RepayConstants.ALLOCATIONTYPE_AUTO);
		header.setEffectSchdMethod(PennantConstants.List_Select);
		header.setActFinReceipt(true);
		header.setReceiptMode(getPaymentType(knockOffData.getPayableType()));
		header.setReceiptModeStatus(RepayConstants.PAYSTATUS_REALIZED);
		header.setRealizationDate(knockOffData.getValueDate());
		header.setLogSchInPresentment(true);
		header.setPostBranch("EOD");
		header.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

		Date valueDate = header.getValueDate();
		receiptData.setBuildProcess("I");
		receiptData.setAllocList(header.getAllocations());
		receiptData.setValueDate(valueDate);
		header.setValueDate(null);
		FinanceMain fm = receiptData.getFinanceDetail().getFinScheduleData().getFinanceMain();
		if (fm != null) {
			fm.setEventProperties(knockOffData.getEventProperties());
		}
		receiptData = receiptCalculator.initiateReceipt(receiptData, false);

		header = receiptData.getReceiptHeader();
		BigDecimal pastDues = header.getTotalPastDues().getTotalDue();
		BigDecimal totalBounces = header.getTotalBounces().getTotalDue();
		BigDecimal totalRcvAdvises = header.getTotalRcvAdvises().getTotalDue();
		BigDecimal totalDues = pastDues.add(totalBounces).add(totalRcvAdvises);

		if (totalDues.compareTo(BigDecimal.ZERO) <= 0) {
			knockOffData.setReason("No dues to knock off");
			return;
		}

		List<ReceiptAllocationDetail> allocationDtls = header.getAllocations();

		for (AutoKnockOffFeeMapping feeMapping : feeMappingList) {
			if (availableAmount.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}

			for (ReceiptAllocationDetail rad : allocationDtls) {
				String alloctaionTo = "";

				if (StringUtils.isEmpty(rad.getFeeTypeCode())) {
					alloctaionTo = rad.getAllocationType();
				} else {
					alloctaionTo = rad.getFeeTypeCode();
				}

				String feeMap = "";
				if (RepayConstants.ALLOCATION_KOEMI.equalsIgnoreCase(feeMapping.getFeeTypeCode())) {
					feeMap = RepayConstants.ALLOCATION_EMI;
				} else {
					feeMap = feeMapping.getFeeTypeCode();
				}

				if (alloctaionTo.equalsIgnoreCase(feeMap)) {
					BigDecimal totDue = rad.getTotalDue();
					BigDecimal paidAmount = rad.getTotalPaid();
					BigDecimal bal = totDue.subtract(paidAmount);
					BigDecimal paidNow = null;

					if (bal.compareTo(BigDecimal.ZERO) > 0) {
						if (bal.compareTo(availableAmount) >= 0) {
							paidNow = availableAmount;
							availableAmount = BigDecimal.ZERO;
						} else {
							paidNow = bal;
							availableAmount = availableAmount.subtract(bal);
						}
						rad.setTotalPaid(rad.getTotalPaid().add(paidNow));
						rad.setPaidAmount(rad.getPaidAmount().add(paidNow));
						receiptAmount = receiptAmount.add(paidNow);
						if (RepayConstants.ALLOCATION_EMI.equalsIgnoreCase(feeMap)) {
							emiAmount = emiAmount.add(paidNow);
						}
					}
				}
			}

			if (emiAmount.compareTo(BigDecimal.ZERO) > 0) {
				setPaidAmounts(receiptData, emiAmount, allocationDtls);
			}
		}

		List<FinReceiptDetail> receiptDetails = new ArrayList<>();

		FinReceiptDetail receiptDetail = new FinReceiptDetail();
		receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
		receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
		receiptDetail.setPaymentType(getPaymentType(knockOffData.getPayableType()));
		receiptDetail.setPayAgainstID(knockOffData.getPayableId());
		receiptDetail.setAmount(receiptAmount);
		receiptDetail.setDueAmount(receiptAmount);
		receiptDetail.setStatus(RepayConstants.PAYSTATUS_REALIZED);
		receiptDetail.setValueDate(knockOffData.getValueDate());
		receiptDetail.setReceivedDate(knockOffData.getValueDate());
		receiptDetail.setNoReserve(true);

		receiptDetails.add(receiptDetail);

		header.setReceiptDetails(receiptDetails);

		if (receiptAmount.compareTo(BigDecimal.ZERO) <= 0) {
			knockOffData.setReason("No dues to knock off");
			return;
		}

		XcessPayables xcessPayable = new XcessPayables();
		xcessPayable.setPayableType(knockOffData.getPayableType());
		xcessPayable.setAmount(receiptAmount);
		xcessPayable.setTotPaidNow(receiptAmount);
		xcessPayable.setPayableID(knockOffData.getPayableId());

		if (RepayConstants.PAYTYPE_PAYABLE.equals(receiptDetail.getPaymentType())) {
			ManualAdvise advise = manualAdviseDAO.getManualAdviseById(receiptDetail.getPayAgainstID(), "_View");
			xcessPayable.setFeeTypeCode(advise.getFeeTypeCode());
		}
		header.setReceiptAmount(receiptAmount);

		header.getXcessPayables().add(xcessPayable);

		FinScheduleData fsd = receiptData.getFinanceDetail().getFinScheduleData();

		List<FinanceScheduleDetail> schedules = new ArrayList<>();
		for (FinanceScheduleDetail schd : fsd.getFinanceScheduleDetails()) {
			schedules.add(schd.copyEntity());
		}

		receiptData.setBuildProcess("R");
		for (ReceiptAllocationDetail allocate : allocationDtls) {
			allocate.setPaidAvailable(allocate.getPaidAmount());
			allocate.setWaivedAvailable(allocate.getWaivedAmount());
			allocate.setPaidAmount(BigDecimal.ZERO);
			allocate.setPaidGST(BigDecimal.ZERO);
			allocate.setTotalPaid(BigDecimal.ZERO);
			allocate.setBalance(allocate.getTotalDue());
			allocate.setWaivedAmount(BigDecimal.ZERO);
			allocate.setWaivedGST(BigDecimal.ZERO);
		}

		receiptCalculator.initiateReceipt(receiptData, false);
		fsd.setFinanceScheduleDetails(schedules);
		repaymentProcessUtil.processAutoKnockOff(receiptData);
		knockOffData.setUtilzedAmount(knockOffData.getUtilzedAmount().add(receiptAmount));
		knockOffData.setReceiptId(receiptData.getReceiptHeader().getReceiptID());
	}

	private void setPaidAmounts(FinReceiptData receiptData, BigDecimal emiAmount,
			List<ReceiptAllocationDetail> allocations) {
		BigDecimal[] emiSplit = receiptCalculator.getEmiSplit(receiptData, emiAmount);

		for (ReceiptAllocationDetail allocation : allocations) {
			String allocationType = allocation.getAllocationType();
			BigDecimal totalDue = allocation.getTotalDue();
			BigDecimal waivedAmount = allocation.getWaivedAmount();

			BigDecimal balanceAmount = totalDue.subtract(waivedAmount);
			switch (allocationType) {
			case RepayConstants.ALLOCATION_PFT:
				if (emiSplit[1].compareTo(balanceAmount) > 0) {
					emiSplit[1] = balanceAmount;
				}
				allocation.setTotalPaid(emiSplit[1]);
				allocation.setPaidAmount(emiSplit[1]);
				break;
			case RepayConstants.ALLOCATION_NPFT:
				if (emiSplit[2].compareTo(balanceAmount) > 0) {
					emiSplit[2] = balanceAmount;
				}
				allocation.setTotalPaid(emiSplit[2]);
				allocation.setPaidAmount(emiSplit[2]);
				break;
			case RepayConstants.ALLOCATION_PRI:
				if (emiSplit[0].compareTo(balanceAmount) > 0) {
					emiSplit[0] = balanceAmount;
				}
				allocation.setTotalPaid(emiSplit[0]);
				allocation.setPaidAmount(emiSplit[0]);
				break;
			case RepayConstants.ALLOCATION_TDS:
				allocation.setTotalPaid(emiSplit[1].subtract(emiSplit[2]));
				allocation.setPaidAmount(emiSplit[1].subtract(emiSplit[2]));
				break;

			default:
				break;
			}

		}
	}

	private FinReceiptData getInProcessReceiptData(FinReceiptData receiptData) {
		String finReference = receiptData.getReceiptHeader().getReference();
		// Multi Receipts: Get In Process Receipts
		long curReceiptID = 0;
		if (receiptData.getReceiptHeader() != null) {
			curReceiptID = receiptData.getReceiptHeader().getReceiptID();
		}

		List<ReceiptAllocationDetail> radList = null;
		List<FinReceiptHeader> rchList = finReceiptHeaderDAO.getInProcessReceipts(finReference);

		if (rchList != null) {
			receiptData.setInProcRchList(rchList);
			radList = receiptAllocationDetailDAO.getManualAllocationsByRef(finReference, curReceiptID);

			if (radList != null) {
				receiptData.setInProcRadList(radList);
			}
		}
		return receiptData;
	}

	private FinReceiptData getFinReceiptDataById(String finReference, String eventCode) {
		logger.debug(Literal.ENTERING);

		// All the data should be from main tables OR views only.
		FinReceiptData receiptData = new FinReceiptData();
		receiptData.setFinReference(finReference);

		FinanceDetail financeDetail = new FinanceDetail();
		receiptData.setFinanceDetail(financeDetail);
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(finReference);

		// Finance Details from Main Table View
		FinanceMain fm = financeMainDAO.getFinanceMainById(finReference, "_AView", false);

		if (fm == null) {
			logger.debug(Literal.LEAVING);
			return receiptData;
		}

		FinReceiptHeader receiptHeader = new FinReceiptHeader();
		receiptData.setReceiptHeader(receiptHeader);
		receiptHeader.setReference(fm.getFinReference());

		Entity entity = entityDAO.getEntity(fm.getLovDescEntityCode(), "");
		if (entity != null) {
			fm.setEntityDesc(entity.getEntityDesc());
		}

		scheduleData.setFinanceMain(fm);

		// Finance Type Details from Table
		FinanceType financeType = financeTypeDAO.getOrgFinanceTypeByID(fm.getFinType(), "_ORGView");

		scheduleData.setFinanceType(financeType);

		// Finance Schedule Details from Main table
		scheduleData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finReference, "", false));

		scheduleData.setFeeEvent(eventCode);

		// Overdue Penalty Rates from main veiw
		scheduleData.setFinODPenaltyRate(finODPenaltyRateDAO.getFinODPenaltyRateByRef(finReference, "_AView"));

		// Profit details from main table
		FinanceProfitDetail profitDetail = profitDetailsDAO.getFinProfitDetailsById(finReference);
		scheduleData.setFinPftDeatil(profitDetail);

		// Finance Customer Details from main view
		if (fm.getCustID() != 0 && fm.getCustID() != Long.MIN_VALUE) {
			CustomerDetails customerDetails = new CustomerDetails();
			customerDetails.setCustomer(customerDAO.getCustomerByID(fm.getCustID(), "_AView"));
			customerDetails.setCustID(fm.getCustID());
			financeDetail.setCustomerDetails(customerDetails);
		}

		// Fetch Excess Amount Details

		// Multi Receipts: Get In Process Receipts
		getInProcessReceiptData(receiptData);

		logger.debug(Literal.LEAVING);
		return receiptData;
	}

	private String getPaymentType(String type) {
		String event = "";
		switch (type) {
		case "E":
			event = RepayConstants.RECEIPTMODE_EXCESS;
			break;
		case "P":
			event = RepayConstants.RECEIPTMODE_PAYABLE;
			break;

		default:
			break;
		}
		return event;
	}

	public void setRepaymentProcessUtil(RepaymentProcessUtil repaymentProcessUtil) {
		this.repaymentProcessUtil = repaymentProcessUtil;
	}

	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public void setReceiptAllocationDetailDAO(ReceiptAllocationDetailDAO receiptAllocationDetailDAO) {
		this.receiptAllocationDetailDAO = receiptAllocationDetailDAO;
	}

	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}
}

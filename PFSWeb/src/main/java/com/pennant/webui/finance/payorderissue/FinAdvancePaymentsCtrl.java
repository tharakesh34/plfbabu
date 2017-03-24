/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related PayOrderIssueHeaders. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * RepayOrderIssueHeaderion or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  PayOrderIssueDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-08-2011    														*
 *                                                                  						*
 * Modified Date    :  12-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-08-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.payorderissue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/PayOrderIssueHeader/PayOrderIssueDialog.zul file.
 */
public class FinAdvancePaymentsCtrl {
	private final static Logger			logger				= Logger.getLogger(FinAdvancePaymentsCtrl.class);
	private Listbox						listbox;
	private String						ccy;
	private int							ccyFormat;
	private boolean						multiParty;
	private String						role;

	List<ValueLabel>					paymentDetailList	= PennantStaticListUtil.getPaymentDetails();
	List<ValueLabel>					paymentTypeList		= PennantStaticListUtil.getPaymentTypes(true);
	private FinanceMain					financeMain;
	private List<FinanceDisbursement>	financeDisbursement;
	private List<FinanceDisbursement>	approvedDisbursments;
	private List<FinFeeDetail>			finFeeDetails;

	public FinAdvancePaymentsCtrl(Listbox listbox, String ccy, boolean multiParty, String role) {
		super();
		this.ccyFormat = CurrencyUtil.getFormat(ccy);
		this.ccy = ccy;
		this.multiParty = multiParty;
		this.role = role;
		this.listbox = listbox;
	}

	@SuppressWarnings("unchecked")
	public void doFillFinAdvancePaymentsDetails(List<FinAdvancePayments> finAdvancePayDetails) {
		logger.debug("Entering");
		listbox.getItems().clear();
		if (finAdvancePayDetails != null && !finAdvancePayDetails.isEmpty()) {
			boolean seqRequired = seqRequired(financeDisbursement);

			Map<Integer, List<FinAdvancePayments>> map = new HashMap<Integer, List<FinAdvancePayments>>();

			for (FinAdvancePayments finAdvancePayments : finAdvancePayDetails) {
				int key = finAdvancePayments.getDisbSeq();

				if (map.containsKey(key)) {
					List<FinAdvancePayments> grouopList = map.get(key);
					grouopList.add(finAdvancePayments);
				} else {
					List<FinAdvancePayments> grouopList = new ArrayList<FinAdvancePayments>();
					grouopList.add(finAdvancePayments);
					map.put(key, grouopList);
				}
			}

			boolean subtotalRequired = true;
			if (map.size() == 1) {
				subtotalRequired = false;
			}

			BigDecimal grandTotal = BigDecimal.ZERO;

			for (Entry<Integer, List<FinAdvancePayments>> entrySet : map.entrySet()) {

				BigDecimal subTotal = BigDecimal.ZERO;

				Integer key = entrySet.getKey();

				FinanceDisbursement groupFinDisbursement = getGroupTotalDisbAmount(financeDisbursement, financeMain,key);


				BigDecimal groupDisbAmount = groupFinDisbursement.getDisbAmount();
				Date groupDate = groupFinDisbursement.getDisbDate();

				//condition to not allow under servicing record to be displayed in disbursement queue.
				if (groupDisbAmount.compareTo(BigDecimal.ZERO) == 0 || groupDate == null) {
					continue;
				}

				Listgroup listgroup = new Listgroup();
				Listcell lc;
				String label = DateUtility.formatToLongDate(groupDate);
				if (seqRequired) {
					label = label.concat(" , ") + key;
				}
				lc = new Listcell(label);
				lc.setStyle("font-weight:bold");
				lc.setParent(listgroup);

				lc = new Listcell("");
				lc.setSpan(5);
				lc.setParent(listgroup);

				lc = new Listcell(PennantApplicationUtil.amountFormate(groupDisbAmount, ccyFormat));
				lc.setStyle("text-align:right;padding-right:10px;font-weight:bold");
				lc.setParent(listgroup);

				lc = new Listcell("");
				lc.setSpan(3);
				lc.setParent(listgroup);
				listbox.appendChild(listgroup);

				List<FinAdvancePayments> list = entrySet.getValue();
				Comparator<FinAdvancePayments> comp = new BeanComparator("paymentSeq");
				Collections.sort(list, comp);

				for (FinAdvancePayments detail : list) {

					if (!isDeleteRecord(detail)) {
						grandTotal = grandTotal.add(detail.getAmtToBeReleased());
						subTotal = subTotal.add(detail.getAmtToBeReleased());
					}
					Listitem item = new Listitem();
					lc = new Listcell(Integer.toString(detail.getPaymentSeq()));
					lc.setParent(item);
					lc = new Listcell(PennantAppUtil.getlabelDesc(detail.getPaymentDetail(), paymentDetailList));
					lc.setParent(item);
					lc = new Listcell(PennantAppUtil.getlabelDesc(detail.getPaymentType(), paymentTypeList));
					lc.setParent(item);

					String bankName = "";
					String custName = "";
					String accoountNum = "";

					String paytype = detail.getPaymentType();

					if (paytype.equals(DisbursementConstants.PAYMENT_TYPE_CHEQUE)
							|| paytype.equals(DisbursementConstants.PAYMENT_TYPE_DD)) {
						bankName = detail.getBankName();
						custName = detail.getLiabilityHoldName();
						accoountNum = detail.getLlReferenceNo();
					} else {
						bankName = detail.getBranchBankName();
						custName = detail.getBeneficiaryName();
						accoountNum = detail.getBeneficiaryAccNo();
					}

					lc = new Listcell(bankName);
					lc.setParent(item);
					lc = new Listcell(custName);
					lc.setParent(item);
					lc = new Listcell(accoountNum);
					lc.setParent(item);

					lc = new Listcell(PennantApplicationUtil.amountFormate(detail.getAmtToBeReleased(), ccyFormat));
					lc.setParent(item);

					lc = new Listcell(detail.getStatus());
					lc.setParent(item);

					lc = new Listcell(PennantJavaUtil.getLabel(detail.getRecordStatus()));
					lc.setParent(item);

					lc = new Listcell(PennantJavaUtil.getLabel(detail.getRecordType()));
					lc.setParent(item);

					item.setAttribute("data", detail);
					ComponentsCtrl.applyForward(item, "onDoubleClick=onFinAdvancePaymentsItemDoubleClicked");
					listbox.appendChild(item);
				}

				if (subtotalRequired) {
					//sub total Display Totals On Footer
					Listgroupfoot item = new Listgroupfoot();
					Listcell listcell;

					listcell = new Listcell();
					listcell.setSpan(5);
					listcell.setParent(item);

					listcell = new Listcell(Labels.getLabel("listheader_AdvancePayments_SubTotal.label"));
					listcell.setStyle("font-weight:bold");
					listcell.setParent(item);

					listcell = new Listcell(PennantApplicationUtil.amountFormate(subTotal, ccyFormat));
					listcell.setStyle("text-align:right;font-weight:bold");
					listcell.setParent(item);

					listcell = new Listcell();
					listcell.setSpan(3);
					listcell.setParent(item);

					listbox.appendChild(item);
				}

			}

			//group total
			if (listbox != null && listbox.getItems().size() > 0) {
				// Display Totals On Footer
				Listitem item = new Listitem();
				Listcell listcell;

				listcell = new Listcell();
				listcell.setSpan(5);
				listcell.setParent(item);

				listcell = new Listcell(Labels.getLabel("listheader_AdvancePayments_GrandTotal.label"));
				listcell.setStyle("font-weight:bold");
				listcell.setParent(item);

				listcell = new Listcell(PennantApplicationUtil.amountFormate(grandTotal, ccyFormat));
				listcell.setStyle("text-align:right;font-weight:bold");
				listcell.setParent(item);

				listcell = new Listcell();
				listcell.setSpan(3);
				listcell.setParent(item);

				listbox.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	private int getNextPaymentSequence(List<FinAdvancePayments> finAdvancePayDetails) {
		int idNumber = 0;
		if (finAdvancePayDetails != null && !finAdvancePayDetails.isEmpty()) {
			for (FinAdvancePayments advancePayments : finAdvancePayDetails) {
				int tempId = advancePayments.getPaymentSeq();
				if (tempId > idNumber) {
					idNumber = tempId;
				}
			}
		}
		return idNumber + 1;
	}

	private boolean isDeleteRecord(FinAdvancePayments aFinAdvancePayments) {
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, aFinAdvancePayments.getRecordType())
				|| StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, aFinAdvancePayments.getRecordType())
				|| StringUtils.equals(DisbursementConstants.STATUS_CANCEL, aFinAdvancePayments.getStatus())) {
			return true;
		}
		return false;
	}

	private boolean seqRequired(List<FinanceDisbursement> list) {
		if (list == null) {
			return false;
		}

		List<Long> dateList = new ArrayList<Long>();
		for (FinanceDisbursement financeDisbursement : list) {
			long disbdate = financeDisbursement.getDisbDate().getTime();
			if (dateList.contains(disbdate)) {
				return true;
			} else {
				dateList.add(disbdate);
			}
		}
		dateList.clear();
		return false;
	}

	public void onDoubleClick(Object listCtrl, Object dialogCtrl, String module, boolean isEnquiry)
			throws InterruptedException {

		Listitem listitem = listbox.getSelectedItem();
		if (listitem != null && listitem.getAttribute("data") != null) {
			final FinAdvancePayments aFinAdvancePayments = (FinAdvancePayments) listitem.getAttribute("data");
			if (!isEnquiry && isDeleteRecord(aFinAdvancePayments)) {
				MessageUtil.showMessage(Labels.getLabel("common_NoMaintainance"));
			} else if (!isEnquiry && isApprovedDisbursments(aFinAdvancePayments)) {
				MessageUtil.showMessage(Labels.getLabel("common_NoMaintainance"));
			} else {
				aFinAdvancePayments.setNewRecord(false);
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("finAdvancePayments", aFinAdvancePayments);
				map.put("ccyFormatter", ccyFormat);
				map.put("custID", financeMain.getCustID());
				map.put("finCcy", ccy);
				map.put("roleCode", role);
				map.put("moduleType", module);
				map.put("enqModule", isEnquiry);
				map.put("multiParty", multiParty);
				map.put("financeDisbursement", financeDisbursement);
				map.put("approvedDisbursments", approvedDisbursments);
				map.put("financeMain", financeMain);
				if ("POISSUE".equals(module)) {
					map.put("payOrderIssueDialogCtrl", dialogCtrl);
					map.put("payOrderIssueListCtrl", listCtrl);
				} else {
					map.put("finAdvancePaymentsListCtrl", listCtrl);
					map.put("financeMainDialogCtrl", dialogCtrl);
				}

				// call the ZUL-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinAdvancePaymentsDialog.zul",
							null, map);
				} catch (Exception e) {
					logger.error("Exception: Opening window", e);
					MessageUtil.showErrorMessage(e.toString());
				}
			}
		}
	}

	private boolean isApprovedDisbursments(FinAdvancePayments aFinAdvancePayments) {
		if (approvedDisbursments != null && !approvedDisbursments.isEmpty()) {
			for (FinanceDisbursement disbursement : approvedDisbursments) {
				if (aFinAdvancePayments.getDisbSeq() == disbursement.getDisbSeq()) {
					return true;
				}
			}
		}
		return false;
	}

	public void onClickNewDisbursement(Object listCtrl, Object dialogCtrl, String module, List<FinAdvancePayments> list)
			throws Exception {

		final FinAdvancePayments aFinAdvancePayments = new FinAdvancePayments();
		aFinAdvancePayments.setFinReference(financeMain.getFinReference());
		aFinAdvancePayments.setPaymentSeq(getNextPaymentSequence(list));
		aFinAdvancePayments.setNewRecord(true);
		aFinAdvancePayments.setWorkflowId(0);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finAdvancePayments", aFinAdvancePayments);
		map.put("ccyFormatter", ccyFormat);
		map.put("newRecord", "true");
		map.put("roleCode", role);
		map.put("finCcy", ccy);
		map.put("custID", financeMain.getCustID());
		map.put("moduleType", module);
		map.put("multiParty", multiParty);
		map.put("financeDisbursement", financeDisbursement);
		map.put("approvedDisbursments", approvedDisbursments);
		map.put("financeMain", financeMain);
		if ("POISSUE".equals(module)) {
			map.put("payOrderIssueDialogCtrl", dialogCtrl);
			map.put("payOrderIssueListCtrl", listCtrl);
		} else {
			map.put("finAdvancePaymentsListCtrl", listCtrl);
			map.put("financeMainDialogCtrl", dialogCtrl);
		}
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinAdvancePaymentsDialog.zul", null, map);
		} catch (Exception e) {
			logger.error("Exception: Opening window", e);
			MessageUtil.showErrorMessage(e.toString());
		}

	}

	/**
	 * @param list
	 * @param netFinAmount
	 * @return
	 */
	public List<ErrorDetails> validateOrgFinAdvancePayment(List<FinAdvancePayments> list, boolean validate) {
		logger.debug(" Entering ");

		List<ErrorDetails> errorList = new ArrayList<ErrorDetails>();
		if (list == null || list.isEmpty()) {
			if (!validate) {
				return errorList;
			}
			ErrorDetails error = new ErrorDetails("60403", null);
			errorList.add(error);
			return errorList;
		}
		
		if (validate) {
			errorList.addAll(validate(list, true));
		}
		return errorList;

	}

	public List<ErrorDetails> validateFinAdvancePayment(List<FinAdvancePayments> list, boolean loanApproved) {
		logger.debug(" Entering ");
		List<ErrorDetails> errorList = new ArrayList<ErrorDetails>();
		errorList.addAll(validate(list, loanApproved));
		return errorList;

	}

	private List<ErrorDetails> validate(List<FinAdvancePayments> list, boolean loanApproved) {
		logger.debug(" Entering ");

		BigDecimal netFinAmount = getTotalDisbAmount(financeDisbursement, financeMain,finFeeDetails);

		List<ErrorDetails> errorList = new ArrayList<ErrorDetails>();
		boolean checkMode = true;

		BigDecimal totDisbAmt = BigDecimal.ZERO;
		Map<Integer, BigDecimal> map = new HashMap<Integer, BigDecimal>();

		if (financeMain.isQuickDisb() && financeDisbursement != null && financeDisbursement.size() > 1) {
			//Multiple disbursement not allowed in quick disbursement
			ErrorDetails error = new ErrorDetails("60405", null);
			errorList.add(error);
			return errorList;
		}

		for (FinAdvancePayments finAdvPayment : list) {
			if (financeMain.isQuickDisb() && checkMode) {
				if (!StringUtils.equals(finAdvPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_CHEQUE)) {
					checkMode = false;
				}
			}

			if (!isDeleteRecord(finAdvPayment)) {
				int key = finAdvPayment.getDisbSeq();

				BigDecimal totalGroupAmt = map.get(key);
				if (totalGroupAmt == null) {
					totalGroupAmt = BigDecimal.ZERO;
				}
				totalGroupAmt = totalGroupAmt.add(finAdvPayment.getAmtToBeReleased());
				map.put(key, totalGroupAmt);
				totDisbAmt = totDisbAmt.add(finAdvPayment.getAmtToBeReleased());
			}
		}

		if (netFinAmount.compareTo(totDisbAmt) != 0) {
			//since if the loan not approved then user can cancel the instruction and resubmit the record in loan origination
			if (loanApproved) {
				//Total amount should match with disbursement amount.
				ErrorDetails error = new ErrorDetails("60401", null);
				errorList.add(error);
				return errorList;
			}
		}

		if (!checkMode) {
			//For quick disbursement, only payment type cheque is allowed.
			ErrorDetails error = new ErrorDetails("60402", null);
			errorList.add(error);
			return errorList;
		}

		//since if the loan not approved then user can cancel the instruction and resubmit the record in loan origination
		if (loanApproved) {
			for (FinanceDisbursement disbursement : financeDisbursement) {
				Date disbDate = disbursement.getDisbDate();
				BigDecimal singletDisbursment = disbursement.getDisbAmount();
				if (disbDate.compareTo(financeMain.getFinStartDate()) == 0) {
					singletDisbursment = singletDisbursment.subtract(financeMain.getDownPayment());
					singletDisbursment = singletDisbursment.subtract(getFeeDedutedFromDisbursment(finFeeDetails));
				}
				int key = disbursement.getDisbSeq();

				BigDecimal totalGroupAmt = map.get(key);
				if (totalGroupAmt == null) {
					totalGroupAmt = BigDecimal.ZERO;
				}
				if (singletDisbursment.compareTo(totalGroupAmt) != 0) {
					String errorDesc = DateUtility.formatToLongDate(disbDate);
					if (disbursement.getDisbSeq() != 1) {
						errorDesc = errorDesc + " , " + disbursement.getDisbSeq();
					}

					//Total amount should match with disbursement amount dated :{0}.
					ErrorDetails error = new ErrorDetails("60404", new String[] { errorDesc });
					errorList.add(error);
					return errorList;
				}
			}
		}

		return errorList;

	}

	private FinanceDisbursement getGroupTotalDisbAmount(List<FinanceDisbursement> list, FinanceMain main, int seq) {

		BigDecimal totdisbAmt = BigDecimal.ZERO;
		Date date = null;
		if (list != null && !list.isEmpty()) {
			for (FinanceDisbursement financeDisbursement : list) {

				if (seq == financeDisbursement.getDisbSeq()) {
					date = financeDisbursement.getDisbDate();

					if (financeDisbursement.getDisbDate().getTime() == main.getFinStartDate().getTime()) {
						totdisbAmt = totdisbAmt.subtract(main.getDownPayment());
						totdisbAmt = totdisbAmt.subtract(getFeeDedutedFromDisbursment(finFeeDetails));
					}
					totdisbAmt = totdisbAmt.add(financeDisbursement.getDisbAmount());
				}
			}
		}

		FinanceDisbursement disbursement = new FinanceDisbursement();
		disbursement.setDisbAmount(totdisbAmt);
		if (date != null) {
			disbursement.setDisbDate(date);
		}
		return disbursement;

	}

	public static BigDecimal getTotalDisbAmount(List<FinanceDisbursement> list, FinanceMain main,List<FinFeeDetail>	finFeeDetails) {
		BigDecimal totdisbAmt = BigDecimal.ZERO;
		if (list != null && !list.isEmpty()) {
			for (FinanceDisbursement financeDisbursement : list) {
				if (financeDisbursement.getDisbDate().getTime() == main.getFinStartDate().getTime()) {
					totdisbAmt = totdisbAmt.subtract(main.getDownPayment());
					totdisbAmt = totdisbAmt.subtract(getFeeDedutedFromDisbursment(finFeeDetails));
				}
				totdisbAmt = totdisbAmt.add(financeDisbursement.getDisbAmount());
			}
		}
		return totdisbAmt;

	}

	public static boolean checkQDPProceeed(FinanceDetail financeDetail) {
		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		if (finMain.isQuickDisb()) {
			List<FinAdvancePayments> list = financeDetail.getAdvancePaymentsList();
			if (list != null && !list.isEmpty()) {
				List<FinanceDisbursement> listDisb = financeDetail.getFinScheduleData().getDisbursementDetails();

				BigDecimal totalDisb = FinAdvancePaymentsCtrl.getTotalDisbAmount(listDisb, finMain,financeDetail.getFinScheduleData().getFinFeeDetailList());
				BigDecimal paidAmount = BigDecimal.ZERO;

				for (FinAdvancePayments finAdvancePayments : list) {
					if (finAdvancePayments.ispOIssued()) {
						if (StringUtils.equals(finAdvancePayments.getStatus(), DisbursementConstants.STATUS_PAID)) {
							paidAmount = paidAmount.add(finAdvancePayments.getAmtToBeReleased());
						}
					}
				}

				if (totalDisb.compareTo(paidAmount) == 0) {
					return true;
				}

				for (FinAdvancePayments finAdvancePayments : list) {
					if (finAdvancePayments.ispOIssued()) {
						if (!StringUtils.equals(finAdvancePayments.getStatus(), DisbursementConstants.STATUS_CANCEL)) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	public static boolean allowReject(List<FinAdvancePayments> list) {
		logger.debug(" Entering ");

		if (list == null || list.isEmpty()) {
			return true;
		}

		for (FinAdvancePayments finAdvPayment : list) {
			if (finAdvPayment.ispOIssued()) {

				if (StringUtils.equals(finAdvPayment.getStatus(), DisbursementConstants.STATUS_AWAITCON)
						|| StringUtils.equals(finAdvPayment.getStatus(), DisbursementConstants.STATUS_PAID)
						|| StringUtils.equals(finAdvPayment.getStatus(), DisbursementConstants.STATUS_APPROVED)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static BigDecimal getFeeDedutedFromDisbursment(List<FinFeeDetail> finFeeDetails) {
		BigDecimal deductedfromDisb=BigDecimal.ZERO;
		
		if (finFeeDetails != null && !finFeeDetails.isEmpty()) {
			for (FinFeeDetail finFeeDetail : finFeeDetails) {
				if (StringUtils.trimToEmpty(finFeeDetail.getFeeScheduleMethod()).equals(CalculationConstants.REMFEE_PART_OF_DISBURSE)) {
					deductedfromDisb=deductedfromDisb.add(finFeeDetail.getRemainingFee());
				}
			}
		}
		return deductedfromDisb;
	}
	
	//FIXME Temporary fix, to be removed once deducted from the disbursement fee amount is saved in the finance main table. 
	// on loan management we are giving the option to adjust disbursement so, to validate total amount we need to get the details of origination only which is approved.
	public void setOrginationFinFeeDetail(String finReference) {
		JdbcSearchObject<FinFeeDetail> jdbcSearchObject = new JdbcSearchObject<FinFeeDetail>(FinFeeDetail.class);
		jdbcSearchObject.addTabelName("FinFeeDetail");
		jdbcSearchObject.addFilterEqual("FinReference", finReference);
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		List<FinFeeDetail> list = pagedListService.getBySearchObject(jdbcSearchObject);
		setFinFeeDetails(list);
	}
	

	public void setFinanceDisbursement(List<FinanceDisbursement> financeDisbursement) {
		this.financeDisbursement = financeDisbursement;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public void setApprovedDisbursments(List<FinanceDisbursement> approvedDisbursments) {
		this.approvedDisbursments = approvedDisbursments;
	}

	public void setFinFeeDetails(List<FinFeeDetail> finFeeDetails) {
		this.finFeeDetails = finFeeDetails;
	}

}

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
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.FinAdvancePaymentsService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.MessageUtil;

public class DisbursementInstCtrl {
	private static final Logger			logger				= Logger.getLogger(DisbursementInstCtrl.class);
	private Listbox						listbox;
	private String						ccy;
	private int							ccyFormat;
	private boolean						multiParty;
	private String						role;

	List<ValueLabel>					paymentDetailList	= PennantStaticListUtil.getPaymentDetails();
	List<ValueLabel>					paymentTypeList		= PennantStaticListUtil.getPaymentTypes(true);
	private FinanceMain					financeMain;
	private List<FinanceDisbursement>	financeDisbursements;
	private List<FinanceDisbursement>	approvedDisbursments;
	private FinAdvancePaymentsService	finAdvancePaymentsService;

	public void init(Listbox listbox, String ccy, boolean multiParty, String role) {
		this.ccyFormat = CurrencyUtil.getFormat(ccy);
		this.ccy = ccy;
		this.multiParty = multiParty;
		this.role = role;
		this.listbox = listbox;
	}

	public static boolean checkQDPProceeed(FinanceDetail financeDetail) {
		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		if (finMain.isQuickDisb()) {
			List<FinAdvancePayments> list = financeDetail.getAdvancePaymentsList();
			if (list != null && !list.isEmpty()) {
				List<FinanceDisbursement> listDisb = financeDetail.getFinScheduleData().getDisbursementDetails();

				FinanceDisbursement totFinDisbursement = getTotal(listDisb, finMain, 0, false);
				BigDecimal totalDisb = totFinDisbursement.getDisbAmount();
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

	@SuppressWarnings("unchecked")
	public void doFillFinAdvancePaymentsDetails(List<FinAdvancePayments> finAdvancePayDetails) {
		logger.debug("Entering");
		listbox.getItems().clear();
		if (finAdvancePayDetails != null && !finAdvancePayDetails.isEmpty()) {

			Map<Integer, List<FinAdvancePayments>> map = groupPayments(finAdvancePayDetails);

			boolean subtotalRequired = true;
			if (map.size() == 1) {
				subtotalRequired = false;
			}

			BigDecimal grandTotal = BigDecimal.ZERO;

			for (Entry<Integer, List<FinAdvancePayments>> entrySet : map.entrySet()) {

				BigDecimal subTotal = BigDecimal.ZERO;
				Integer key = entrySet.getKey();
				FinanceDisbursement groupFinDisbursement = getTotal(financeDisbursements, financeMain, key, true);
				BigDecimal groupDisbAmount = groupFinDisbursement.getDisbAmount();
				Date groupDate = groupFinDisbursement.getDisbDate();

				//condition to not allow under servicing record to be displayed in disbursement queue.
				if (groupDate == null) {
					continue;
				}

				Listgroup listgroup = new Listgroup();
				Listcell lc;
				String label = DateUtility.formatToLongDate(groupDate);
				label = label.concat(" , ") + key;
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

					if (!DisbursementInstCtrl.isDeleteRecord(detail)) {
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
					addListItem(listbox, Labels.getLabel("listheader_AdvancePayments_SubTotal.label"), subTotal, true);
				}
			}

			//group total
			if (listbox != null && listbox.getItems().size() > 0) {
				// Display Totals On Footer
				addListItem(listbox, Labels.getLabel("listheader_AdvancePayments_GrandTotal.label"), grandTotal, false);
			}
		}
		logger.debug("Leaving");
	}

	public void onClickNew(Object listCtrl, Object dialogCtrl, String module, List<FinAdvancePayments> list)
			throws Exception {

		final FinAdvancePayments aFinAdvancePayments = new FinAdvancePayments();
		aFinAdvancePayments.setFinReference(financeMain.getFinReference());
		aFinAdvancePayments.setPaymentSeq(getNextPaymentSequence(list));
		aFinAdvancePayments.setNewRecord(true);
		aFinAdvancePayments.setWorkflowId(0);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finAdvancePayments", aFinAdvancePayments);
		map.put("newRecord", "true");

		doshowDialog(map, listCtrl, dialogCtrl, module, false);

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
			} else if (!isEnquiry && !allowMaintainAfterRequestSent(aFinAdvancePayments)) {
				MessageUtil.showMessage(Labels.getLabel("common_NoMaintainance"));
			} else {
				aFinAdvancePayments.setNewRecord(false);
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("finAdvancePayments", aFinAdvancePayments);
				doshowDialog(map, listCtrl, dialogCtrl, module, isEnquiry);

			}
		}
	}

	private void doshowDialog(HashMap<String, Object> map, Object listCtrl, Object dialogCtrl, String module,
			boolean isEnquiry) throws InterruptedException {

		map.put("ccyFormatter", ccyFormat);
		map.put("custID", financeMain.getCustID());
		map.put("finCcy", ccy);
		map.put("roleCode", role);
		map.put("moduleType", module);
		map.put("enqModule", isEnquiry);
		map.put("multiParty", multiParty);
		map.put("financeDisbursement", financeDisbursements);
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
			MessageUtil.showError(e);
		}

	}

	private void addListItem(Listbox listbox, String lable, BigDecimal total, boolean footer) {
		//sub total Display Totals On Footer
		Listgroupfoot item = new Listgroupfoot();
		Listitem listitem = new Listitem();

		Listcell listcell;

		listcell = new Listcell();
		listcell.setSpan(5);
		setParent(footer, listcell, item, listitem);

		listcell = new Listcell(lable);
		listcell.setStyle("font-weight:bold");
		setParent(footer, listcell, item, listitem);

		listcell = new Listcell(PennantApplicationUtil.amountFormate(total, ccyFormat));
		listcell.setStyle("text-align:right;font-weight:bold");
		setParent(footer, listcell, item, listitem);

		listcell = new Listcell();
		listcell.setSpan(3);
		setParent(footer, listcell, item, listitem);

		if (footer) {
			listbox.appendChild(item);
		} else {
			listbox.appendChild(listitem);
		}

	}

	private void setParent(boolean footer, Listcell listcell, Listgroupfoot item, Listitem listitem) {
		if (footer) {
			listcell.setParent(item);
		} else {
			listcell.setParent(listitem);
		}
	}

	private Map<Integer, List<FinAdvancePayments>> groupPayments(List<FinAdvancePayments> finAdvancePayDetails) {

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
		return map;
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

	public static boolean isDeleteRecord(FinAdvancePayments aFinAdvancePayments) {
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, aFinAdvancePayments.getRecordType())
				|| StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, aFinAdvancePayments.getRecordType())
				|| StringUtils.equals(DisbursementConstants.STATUS_CANCEL, aFinAdvancePayments.getStatus())
				|| StringUtils.equals(DisbursementConstants.STATUS_REJECTED, aFinAdvancePayments.getStatus())) {
			return true;
		}
		return false;
	}

	private boolean allowMaintainAfterRequestSent(FinAdvancePayments aFinAdvancePayments) {
		if (StringUtils.equals(DisbursementConstants.STATUS_AWAITCON, aFinAdvancePayments.getStatus())) {
			if (StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_CHEQUE, aFinAdvancePayments.getPaymentType())
					|| StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_DD,
							aFinAdvancePayments.getPaymentType())) {
				return true;
			}
			return false;
		}

		if (StringUtils.equals(DisbursementConstants.STATUS_PAID, aFinAdvancePayments.getStatus()) && (!StringUtils
				.equals(DisbursementConstants.PAYMENT_TYPE_CHEQUE, aFinAdvancePayments.getPaymentType())
				&& !StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_DD, aFinAdvancePayments.getPaymentType()))) {
			return false;
		}

		return true;
	}

	private List<ErrorDetails> validate(List<FinAdvancePayments> list, boolean loanApproved) {
		return finAdvancePaymentsService.validateFinAdvPayments(list, financeDisbursements, financeMain, loanApproved);
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

	public static FinanceDisbursement getTotal(List<FinanceDisbursement> list, FinanceMain main, int seq,
			boolean group) {

		BigDecimal totdisbAmt = BigDecimal.ZERO;
		Date date = null;
		if (list != null && !list.isEmpty()) {
			for (FinanceDisbursement financeDisbursement : list) {
				if (group && seq != financeDisbursement.getDisbSeq()) {
					continue;
				}

				if (!group) {
					if (StringUtils.equals(FinanceConstants.DISB_STATUS_CANCEL, financeDisbursement.getDisbStatus())) {
						continue;
					}
				}

				date = financeDisbursement.getDisbDate();

				//check is first disbursement
				if (financeDisbursement.getDisbDate().getTime() == main.getFinStartDate().getTime()) {

					totdisbAmt = totdisbAmt.subtract(main.getDownPayment());
					totdisbAmt = totdisbAmt.subtract(main.getDeductFeeDisb());
					totdisbAmt = totdisbAmt.subtract(main.getDeductInsDisb());
					if (StringUtils.trimToEmpty(main.getBpiTreatment()).equals(FinanceConstants.BPI_DISBURSMENT)) {
						totdisbAmt = totdisbAmt.subtract(main.getBpiAmount());
					}
				}
				totdisbAmt = totdisbAmt.add(financeDisbursement.getDisbAmount());
			}
		}

		FinanceDisbursement disbursement = new FinanceDisbursement();
		disbursement.setDisbAmount(totdisbAmt);
		if (date != null) {
			disbursement.setDisbDate(date);
		}
		return disbursement;

	}

	public static BigDecimal getTotalByDisbursment(FinanceDisbursement financeDisbursement, FinanceMain main) {

		BigDecimal totdisbAmt = BigDecimal.ZERO;

		//check is first disbursement
		if (financeDisbursement.getDisbDate().getTime() == main.getFinStartDate().getTime()
				&& financeDisbursement.getDisbSeq() == 1) {

			totdisbAmt = totdisbAmt.subtract(main.getDownPayment());
			totdisbAmt = totdisbAmt.subtract(main.getDeductFeeDisb());
			totdisbAmt = totdisbAmt.subtract(main.getDeductInsDisb());
			if (StringUtils.trimToEmpty(main.getBpiTreatment()).equals(FinanceConstants.BPI_DISBURSMENT)) {
				totdisbAmt = totdisbAmt.subtract(main.getBpiAmount());
			}
		}
		totdisbAmt = totdisbAmt.add(financeDisbursement.getDisbAmount());
		return totdisbAmt;

	}

	public void markCancelIfNoDisbursmnetFound(List<FinAdvancePayments> finAdvancePaymentsList) {

		if (finAdvancePaymentsList != null && !finAdvancePaymentsList.isEmpty()) {
			for (FinAdvancePayments finAdvancePayments : finAdvancePaymentsList) {
				if (PennantConstants.RECORD_TYPE_NEW.equals(finAdvancePayments.getRecordType())) {
					boolean notExists = checkSequnceExists(finAdvancePayments.getDisbSeq());
					if (!notExists) {
						finAdvancePayments.setRecordType(PennantConstants.RECORD_TYPE_CAN);
					}
				}
			}
		}
	}

	private boolean checkSequnceExists(int disbSeq) {
		if (financeDisbursements != null && !financeDisbursements.isEmpty()) {
			for (FinanceDisbursement financeDisbursement : financeDisbursements) {
				if (financeDisbursement.getDisbSeq() == disbSeq) {
					return true;
				}
			}
		}
		return false;
	}

	public void setFinanceDisbursement(List<FinanceDisbursement> financeDisbursement) {
		this.financeDisbursements = financeDisbursement;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public void setApprovedDisbursments(List<FinanceDisbursement> approvedDisbursments) {
		this.approvedDisbursments = approvedDisbursments;
	}

	public void setFinAdvancePaymentsService(FinAdvancePaymentsService finAdvancePaymentsService) {
		this.finAdvancePaymentsService = finAdvancePaymentsService;
	}

}

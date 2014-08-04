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
 *//*

*//**
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  RuleDialogCtrl.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  03-06-2011    
 *                                                                  
 * Modified Date    :  03-06-2011    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-06-2011       Pennant	                 0.1                                         * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 *//*
package com.pennant.webui.financemanagement.payments;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.PaymentDetails;
import com.pennant.backend.model.finance.PaymentFee;
import com.pennant.backend.model.finance.PaymentHeader;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.Fees;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.finance.PaymentService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

*//**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * WEB-INF/pages/FinanceManagement/SchdlRepayment/SchdlRepaymentDialog.zul <br/>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 *//*
public class PaymentDialogCtrl extends GFCBaseCtrl {

	private static final long serialVersionUID = 966281186831332116L;
	private final static Logger logger = Logger
			.getLogger(PaymentDialogCtrl.class);

	
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 
	protected Window window_PaymentDialog;

	protected Tabbox tabbox;
	protected Button btnHelp;
	protected Button btnClose;

	protected Label finReference;
	protected Textbox finCcy;
	protected Label lovDescFinCcyName;
	protected Label finStartDate;
	protected Label maturityDate;
	protected Decimalbox finAmount;
	protected Decimalbox curFinAmount;
	protected Decimalbox totPftAmount;
	protected Decimalbox totPftBalence;
	protected Decimalbox rpyAmount;
	protected Decimalbox rpyAmountBal;
	protected Decimalbox rpyPrincipal;
	protected Decimalbox rpyProfit;
	protected Decimalbox rpyPenalty;
	protected Decimalbox rpyWaiver;
	protected Decimalbox rpyRefund;
	protected Label rpyFromdate;
	protected Label rpyTodate;
	protected Textbox custFundingAcc;
	protected Label custID;
	// Looped details
	protected Label schDate;
	protected Datebox payDate;
	protected Decimalbox schdDefPft;
	protected Decimalbox schdDefPftPaid;
	protected Decimalbox schdDefPftBal;
	protected Decimalbox schdDefPri;
	protected Decimalbox schdDefPriPaid;
	protected Decimalbox schdDefPriBal;
	protected Decimalbox schdpenality;
	protected Decimalbox schdpenalityPaid;
	protected Decimalbox schdpenalityBal;
	protected Decimalbox schdWaiver;
	protected Decimalbox schdWaiverPaid;
	protected Decimalbox schdWaiverBal;
	protected Decimalbox schdRefund;
	protected Decimalbox schdRefundPaid;
	protected Decimalbox schdRefundBal;
	protected Decimalbox schdPft;
	protected Decimalbox schdPftPaid;
	protected Decimalbox schdPftBal;
	protected Decimalbox schdPri;
	protected Decimalbox schdPriPaid;
	protected Decimalbox schdPriBal;
	protected Decimalbox prvSchdPft;
	protected Decimalbox prvSchdPftPaid;
	protected Decimalbox prvSchdPftBal;
	protected Decimalbox prvSchdPri;
	protected Decimalbox prvSchdPriPaid;
	protected Decimalbox prvSchdPriBal;
	protected Decimalbox netPayment;

	protected Button btnNext;
	protected Button btnPrevious;
	protected Button btnPayment;
	protected Window window_RepayFees;
	protected Listbox listBoxFees;

	protected Row row_DeferedProfit;
	protected Row row_DeferedPrincipal;
	protected Row row_prvScheduleProfit;
	protected Row row_prvSchedulePrincipal;
	protected Row row_Refund;

	private transient PagedListService pagedListService;
	private PaymentHeader paymentHeader;
	private PaymentService paymentService;
	private FinanceMain financeMain;
	private FinanceScheduleDetail curFinanceScheduleDetail;
	private FinanceRepayments financeRepayments;
	private AEAmountCodes amountCodes;
	private transient AccountEngineExecution engineExecution;

	private static final BigDecimal zero = new BigDecimal(0);
	private int count = -1;
	boolean isEarlypay = false;
	boolean isLatePay = false;
	private Date prvSchdate;
	private List<FinanceScheduleDetail> listFinSchdDetails = new ArrayList<FinanceScheduleDetail>();
	private HashMap<Date, FinanceRepayments> maFinRepay = new HashMap<Date, FinanceRepayments>();
	private HashMap<Date, FinanceScheduleDetail> maFinSchd = new HashMap<Date, FinanceScheduleDetail>();
	private HashMap<Date, ArrayList<PaymentFee>> mapPayFees = new HashMap<Date, ArrayList<PaymentFee>>();
	private HashMap<Date, ArrayList<PaymentFee>> mapPayRefund = new HashMap<Date, ArrayList<PaymentFee>>();

	*//**
	 * default constructor.<br>
	 *//*
	public PaymentDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	*//**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Rule object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 *//*
	public void onCreate$window_PaymentDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		tabbox = (Tabbox) event.getTarget().getParent().getParent().getParent();
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		this.window_PaymentDialog.setVisible(false);
		Executions.createComponents(
				"/WEB-INF/pages/Finance/FinanceMain/FinanceSelect.zul", null,
				map);
		// set Field Properties
		doSetFieldProperties();

		logger.debug("Leaving" + event.toString());
	}

	*//**
	 * Set the properties of the fields, like maxLength.<br>
	 *//*
	private void doSetFieldProperties() {
		logger.debug("Entering");

		logger.debug("Leaving");
	}

	*//**
	 * 
	 * <br>
	 * IN PaymentDialogCtrl.java void
	 *//*
	public void doWriteComponentsToBean() {
		FinanceRepayments finRepay = new FinanceRepayments();
		finRepay.setFinReference(getFinanceMain().getFinReference());
		finRepay.setFinSchdDate(getCurFinanceScheduleDetail().getSchDate());
		finRepay.setFinPostDate(DateUtility.today());
		finRepay.setFinValueDate(getDate(this.payDate));
		finRepay.setFinBranch(getFinanceMain().getFinBranch());
		finRepay.setFinType(getFinanceMain().getFinType());
		finRepay.setFinCustID(getFinanceMain().getCustID());

		finRepay.setFinWaiver(getUNFRAM(this.schdWaiverPaid.getValue()));
		finRepay.setFinRefund(getUNFRAM(this.schdRefundPaid.getValue()));
		// TODO start-- Check once commentted by Siva

		
		 * //Total paid including current payment
		 * finRepay.setFinSchdPriPaid(getUNFRAM
		 * (getFRAM(getAmountCodeDetail().getTPPSchdPriPaid
		 * ()).add(getDCBValue(this.schdPriPaid)))); // Total paid including
		 * current payment
		 * finRepay.setFinSchdPftPaid(getUNFRAM(getFRAM(getAmountCodeDetail
		 * ().getTPPSchdPftPaid()).add(getDCBValue(this.schdPftPaid)))); // How
		 * much principal balance before this payment
		 * finRepay.setFinBPSchdPriBal
		 * (getAmountCodeDetail().getTEPrincipalSchd()
		 * .subtract(getAmountCodeDetail().getTPPSchdPriPaid()));
		 * finRepay.setFinAPSchdPriBal
		 * (finRepay.getFinSchdPri().subtract(finRepay.getFinSchdPriPaid())); //
		 * How much profit balance before this payment
		 * finRepay.setFinBPSchdPftBal
		 * (getAmountCodeDetail().getTEProfitSchd().subtract
		 * (getAmountCodeDetail().getTPPSchdPftPaid()));
		 

		// Current schedule
		finRepay.setLovDescSchdPriPaid(this.schdPriPaid.getValue());
		finRepay.setLovDescSchdPftPaid(this.schdPftPaid.getValue());
		finRepay.setRepayBal(getDCBValue(this.rpyAmountBal));
		finRepay.setRepaypri(getDCBValue(this.rpyPrincipal));
		finRepay.setRepayPft(getDCBValue(this.rpyProfit));
		finRepay.setRepayPenal(getDCBValue(this.rpyPenalty));
		finRepay.setRepayWaiver(getDCBValue(this.rpyWaiver));
		finRepay.setRepayRefund(getDCBValue(this.rpyRefund));
		setFinanceRepayments(finRepay);

	}

	private void doWriteSceduleDetail() {
		// Update Current schedule with balances paid
		if (getDCBValue(this.schdPftPaid).compareTo(getDCBValue(this.schdPft)) < 0) {
			getCurFinanceScheduleDetail().setSchPftPaid(false);
		} else {
			getCurFinanceScheduleDetail().setSchPftPaid(true);
		}
		if (getDCBValue(this.schdPriPaid).compareTo(getDCBValue(this.schdPri)) < 0) {
			getCurFinanceScheduleDetail().setSchPriPaid(false);
		} else {
			getCurFinanceScheduleDetail().setSchPriPaid(true);
		}

		getCurFinanceScheduleDetail()
				.setSchdPftPaid(
						getUNFRAM(getAddedValue(this.schdPftPaid,
								this.prvSchdPftPaid)));
		getCurFinanceScheduleDetail()
				.setSchdPriPaid(
						getUNFRAM(getAddedValue(this.schdPriPaid,
								this.prvSchdPriPaid)));
		getCurFinanceScheduleDetail().setDefProfit(
				getUNFRAM(getDCBValue(this.schdDefPftPaid)));
		getCurFinanceScheduleDetail().setDefPrincipal(
				getUNFRAM(getDCBValue(this.schdDefPriPaid)));
		getCurFinanceScheduleDetail().setDefProfitBal(
				getUNFRAM(getSubtractedValue(this.schdDefPft,
						this.schdDefPftPaid)));
		getCurFinanceScheduleDetail().setDefPrincipalBal(
				getUNFRAM(getSubtractedValue(this.schdDefPri,
						this.schdDefPriPaid)));
	}

	*//**
	 * 
	 * <br>
	 * IN PaymentDialogCtrl.java void
	 *//*
	private void doSaveProcess() {
		// update finance schedule work table,
		// save details to the repayments
		// getPaymentService().delete(getFinanceRepayments(), "_Work");

		getPaymentService()
				.updateScheduleDetails(getCurFinanceScheduleDetail());
		if (maFinSchd.containsKey(getCurFinanceScheduleDetail().getSchDate())) {
			maFinSchd.remove(getCurFinanceScheduleDetail().getSchDate());
		}
		maFinSchd.put(getCurFinanceScheduleDetail().getSchDate(),
				getCurFinanceScheduleDetail());

		if (maFinRepay.containsKey(getCurFinanceScheduleDetail().getSchDate())) {
			getPaymentService().update(
					maFinRepay.get(getCurFinanceScheduleDetail().getSchDate()),
					"_Work");
			maFinRepay.remove(getCurFinanceScheduleDetail().getSchDate());
		} else {
			getFinanceRepayments().setFinPaySeq(
					getPaymentService().save(getFinanceRepayments(), "_Work"));
		}
		maFinRepay.put(getCurFinanceScheduleDetail().getSchDate(),
				getFinanceRepayments());

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	*//**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 *//*
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_PaymentDialog);
		logger.debug("Leaving" + event.toString());
	}

	*//**
	 * To set data to the fields when fin reference in search dialog is double
	 * clicked <br>
	 * IN PaymentDialogCtrl.java
	 * 
	 * @param object
	 * @param jdbcSearchObject
	 *            void
	 *//*
	public void doSetFinance(Object object,
			JdbcSearchObject<FinanceMain> jdbcSearchObject) {
		FinanceMain financeMain = (FinanceMain) object;
		setFinanceMain(financeMain);

		this.finReference.setValue(financeMain.getFinReference());
		this.custID.setValue(financeMain.getLovDescCustCIF());
		this.finCcy.setValue(financeMain.getFinCcy());
		this.lovDescFinCcyName.setValue(financeMain.getFinCcy() + "-"
				+ financeMain.getLovDescFinCcyName());
		if (financeMain.getFinStartDate() != null) {
			this.finStartDate
					.setValue(DateUtility.formatUtilDate(
							financeMain.getFinStartDate(),
							PennantConstants.dateFormate));
		}
		if (financeMain.getMaturityDate() != null) {
			this.maturityDate
					.setValue(DateUtility.formatUtilDate(
							financeMain.getMaturityDate(),
							PennantConstants.dateFormate));
		}
		this.finAmount.setValue(getFRAM(financeMain.getFinAmount()));
		this.custFundingAcc.setValue(financeMain.getRepayAccountId());
		this.totPftAmount.setValue(getFRAM(getFinanceMain().getTotalProfit()));
		this.rpyAmount.setValue(zero);
		this.rpyAmountBal.setValue(zero);
		this.rpyPrincipal.setValue(zero);
		this.rpyProfit.setValue(zero);
		this.rpyPenalty.setValue(zero);
		this.rpyWaiver.setValue(zero);
		this.rpyRefund.setValue(zero);
		this.rpyFromdate.setValue("");
		this.rpyTodate.setValue("");
		setPaymentHeader(getPaymentService().getPaymentHeader(
				getFinanceMain(),
				(Date) SystemParameterDetails
						.getSystemParameterValue("APP_DATE")));
		listFinSchdDetails = getPaymentHeader().getUnPaidFinSchdDetails();
		if (listFinSchdDetails.size() > 0) {
			// delete and insert in schedule details work table
			getPaymentService().maintainWorkSchedules(
					getFinanceMain().getFinReference(),
					getUserWorkspace().getUserDetails().getUserId(),
					getPaymentHeader().getPaidFinSchdDetails());
			// delete from finance repay work table
			getPaymentService().deleteWorkByFinRef(
					getFinanceMain().getFinReference());
			count = count + 1;
			setCurFinanceScheduleDetail(listFinSchdDetails.get(count));
			doDateChangeProcess(
					(Date) SystemParameterDetails
							.getSystemParameterValue("APP_DATE"),
					getDCBValue(this.rpyAmount));
		} else {
			try {
				closeTab();
				PTMessageUtils.showErrorMessage("Shedule Repay Not Found");
			} catch (InterruptedException e) {
				e.printStackTrace();
				logger.debug(e);
			}
		}
	}

	*//**
	 * 
	 * <br>
	 * IN PaymentDialogCtrl.java
	 * 
	 * @param date
	 * @param repayamount
	 *            void
	 *//*
	private void doDateChangeProcess(Date date, BigDecimal repayamount) {
		this.payDate.setValue(date);
		// Calculate current finance amount with app_date
		getPaymentHeader().setPaymentDetails(
				getPaymentService().getPaymentDetails(
						getFinanceMain().getFinReference(), date));
		PaymentDetails paymentDetails = getPaymentHeader().getPaymentDetails();
		BigDecimal curFinamt = paymentDetails.getDisbAmount()
				.subtract(paymentDetails.getDownPaymentAmount())
				.add(paymentDetails.getCpzAmount())
				.subtract(paymentDetails.getSchdPriPaid())
				.subtract(paymentDetails.getDefPrincipal());
		this.curFinAmount.setValue(getFRAM(curFinamt));
		this.totPftBalence.setValue(getFRAM(getFinanceMain().getTotalProfit()
				.subtract(paymentDetails.getSchdPftPaid())));
		doProcess(listFinSchdDetails.get(count), repayamount, false, true);
		this.window_PaymentDialog.setVisible(true);
	}

	*//**
	 * To calculate the Re payment values of the single Schedule <br>
	 * IN PaymentDialogCtrl.java
	 * 
	 * @param financeScheduleDetail
	 * @param repayAmount
	 * @param isWaiver
	 * @param isFeeCalreq
	 *            void
	 *//*
	private void doProcess(FinanceScheduleDetail financeScheduleDetail,
			BigDecimal repayAmount, boolean isWaiver, boolean isFeeCalreq) {

		if (count == 0) {
			this.rpyFromdate.setValue(DateUtility.formatUtilDate(
					financeScheduleDetail.getSchDate(),
					PennantConstants.dateFormate));
		} else {
			this.prvSchdate = listFinSchdDetails.get(count - 1).getSchDate();
		}
		this.rpyTodate.setValue(DateUtility.formatUtilDate(
				financeScheduleDetail.getSchDate(),
				PennantConstants.dateFormate));
		// decide late pay or early pay
		if (financeScheduleDetail.getSchDate().compareTo(getDate(this.payDate)) != 0) {
			if (financeScheduleDetail.getSchDate().compareTo(
					getDate(this.payDate)) > 0) {
				isEarlypay = true;
				isLatePay = false;
			} else {
				isEarlypay = false;
				isLatePay = true;
			}
		} else {
			isEarlypay = false;
			isLatePay = false;
		}
		// if records greater than 1 show previous button and set repay amount
		// to disabled
		if (count > 0) {
			this.btnPrevious.setVisible(true);
			this.rpyAmount.setDisabled(true);
			this.rpyAmount.setSclass("Mywhite");
		} else {
			this.btnPrevious.setVisible(false);
			this.rpyAmount.setDisabled(false);
			this.rpyAmount.setSclass("");
		}
		// Scheduled values

		// ---schedule Date
		this.schDate.setValue(DateUtility.formatUtilDate(
				financeScheduleDetail.getSchDate(),
				PennantConstants.dateFormate));
		if (isFeeCalreq) {
			// executes the all fee rules and stores in related map
			getFeeValue(financeScheduleDetail);
		}
		// ---schedule penalty
		this.schdpenality.setValue(getFRAM(getPenaltyOrRefund(mapPayFees
				.get(financeScheduleDetail.getSchDate()))));

		// ---schedule differed profit
		this.schdDefPft.setValue(getFRAM(financeScheduleDetail
				.getDefProfitSchd()));
		// hide and show differed profit and principal
		if (getDCBValue(this.schdDefPft).compareTo(zero) == 0) {
			this.row_DeferedProfit.setVisible(false);
		} else {
			this.row_DeferedProfit.setVisible(true);
		}
		// ---schedule differed principal
		this.schdDefPri.setValue(getFRAM(financeScheduleDetail
				.getDefPrincipalSchd()));
		// hide and show differed profit and principal
		if (getDCBValue(this.schdDefPri).compareTo(zero) == 0) {
			this.row_DeferedPrincipal.setVisible(false);
		} else {
			this.row_DeferedPrincipal.setVisible(true);
		}

		// --- previously paid scheduled profit
		this.prvSchdPft
				.setValue(getFRAM(financeScheduleDetail.getProfitSchd()));
		this.prvSchdPftPaid.setValue(getFRAM(financeScheduleDetail
				.getSchdPftPaid()));
		this.prvSchdPftBal.setValue(getFRAM(financeScheduleDetail
				.getProfitSchd().subtract(
						financeScheduleDetail.getSchdPftPaid())));

		// --- previously paid scheduled principal
		this.prvSchdPri.setValue(getFRAM(financeScheduleDetail
				.getPrincipalSchd()));
		this.prvSchdPriPaid.setValue(getFRAM(financeScheduleDetail
				.getSchdPriPaid()));
		this.prvSchdPriBal.setValue(getFRAM(financeScheduleDetail
				.getPrincipalSchd().subtract(
						financeScheduleDetail.getSchdPriPaid())));

		// -- refund
		this.schdRefund.setValue(getFRAM(getPenaltyOrRefund(mapPayRefund
				.get(financeScheduleDetail.getSchDate()))));
		if (getDCBValue(this.schdRefund).compareTo(zero) > 0) {
			this.schdRefundPaid.setDisabled(false);
		} else {
			this.schdRefundPaid.setDisabled(true);
		}

		// hide and show paid scheduled profit details
		if (getDCBValue(this.prvSchdPftPaid).compareTo(zero) == 0) {
			this.row_prvScheduleProfit.setVisible(false);
		} else {
			this.row_prvScheduleProfit.setVisible(true);
		}
		// hide and show paid scheduled principal details
		if (getDCBValue(this.prvSchdPriPaid).compareTo(zero) == 0) {
			this.row_prvSchedulePrincipal.setVisible(false);
		} else {
			this.row_prvSchedulePrincipal.setVisible(true);
		}

		this.schdPft.setValue(getDCBValue(prvSchdPftBal));
		this.schdPri.setValue(getDCBValue(prvSchdPriBal));
		// Re payment values
		// given repay amount minus scheduled values
		BigDecimal avlRepayAmount = repayAmount;
		// process when request is not from waiver or refund given
		if (isWaiver) {
			avlRepayAmount = avlRepayAmount
					.add(getDCBValue(this.schdWaiverPaid));
		}

		if (avlRepayAmount.compareTo(getDCBValue(this.schdpenality)) >= 0) {
			avlRepayAmount = avlRepayAmount
					.subtract(getDCBValue(this.schdpenality));
			this.schdpenalityPaid.setValue(getDCBValue(this.schdpenality));
		} else {
			this.schdpenalityPaid.setValue(avlRepayAmount);
			avlRepayAmount = zero;
		}
		// if (avlRepayAmount.compareTo(zero) == 0 &&
		// getDCBValue(this.schdPft).compareTo(zero) > 0 &&
		// getDCBValue(this.schdPri).compareTo(zero) > 0) {
		// avlRepayAmount = getDCBValue(this.schdpenalityPaid);
		// this.schdpenalityPaid.setValue(zero);
		// }

		if (repayAmount.compareTo(getDCBValue(this.schdDefPft)) >= 0) {
			avlRepayAmount = avlRepayAmount
					.subtract(getDCBValue(this.schdDefPft));
			this.schdDefPftPaid.setValue(getDCBValue(this.schdDefPft));
		} else {
			this.schdDefPftPaid.setValue(avlRepayAmount);
			avlRepayAmount = zero;
		}
		if (avlRepayAmount.compareTo(getDCBValue(this.schdDefPri)) >= 0) {
			avlRepayAmount = avlRepayAmount
					.subtract(getDCBValue(this.schdDefPri));
			this.schdDefPriPaid.setValue(getDCBValue(this.schdDefPri));
		} else {
			this.schdDefPriPaid.setValue(avlRepayAmount);
			avlRepayAmount = zero;
		}

		if (avlRepayAmount.compareTo(getDCBValue(this.schdPft)) >= 0) {
			avlRepayAmount = avlRepayAmount.subtract(getDCBValue(this.schdPft));
			this.schdPftPaid.setValue(getDCBValue(this.schdPft));
		} else {
			this.schdPftPaid.setValue(avlRepayAmount);
			avlRepayAmount = zero;
		}
		if (avlRepayAmount.compareTo(getDCBValue(this.schdPri)) >= 0) {
			avlRepayAmount = avlRepayAmount.subtract(getDCBValue(this.schdPri));
			this.schdPriPaid.setValue(getDCBValue(this.schdPri));
		} else {
			this.schdPriPaid.setValue(avlRepayAmount);
			avlRepayAmount = zero;
		}

		// waiver is always taken from the map
		if (!this.schdWaiverPaid.isReadonly()
				&& maFinRepay.containsKey(getCurFinanceScheduleDetail()
						.getSchDate())) {
			this.schdWaiverPaid
					.setValue(getFRAM(maFinRepay.get(
							getCurFinanceScheduleDetail().getSchDate())
							.getFinWaiver()));
		} else {
			this.schdWaiverPaid.setValue(zero);
		}
		// refund is always taken from the map
		if (!this.schdRefundPaid.isReadonly()
				&& maFinRepay.containsKey(getCurFinanceScheduleDetail()
						.getSchDate())) {
			this.schdRefundPaid
					.setValue(getFRAM(maFinRepay.get(
							getCurFinanceScheduleDetail().getSchDate())
							.getFinRefund()));
		} else {
			this.schdRefundPaid.setValue(zero);
		}
		// calculate total waiver and refund
		if (count == 0) {
			this.rpyWaiver.setValue(getDCBValue(this.schdWaiverPaid));
			this.rpyRefund.setValue(getDCBValue(this.schdRefundPaid));
		} else {
			this.rpyWaiver.setValue(getDCBValue(this.schdWaiverPaid).add(
					getFRAM(maFinRepay.get(prvSchdate).getRepayWaiver())));
			this.rpyRefund.setValue(getDCBValue(this.schdRefundPaid).add(
					getFRAM(maFinRepay.get(prvSchdate).getRepayRefund())));
		}

		// balances
		// Scheduled - payment = balance
		this.schdDefPftBal.setValue(getSubtractedValue(this.schdDefPft,
				this.schdDefPftPaid));
		this.schdDefPriBal.setValue(getSubtractedValue(this.schdDefPri,
				this.schdDefPriPaid));
		this.schdpenalityBal.setValue(getSubtractedValue(this.schdpenality,
				this.schdpenalityPaid));
		this.schdPftBal.setValue(getSubtractedValue(this.schdPft,
				this.schdPftPaid));
		this.schdPriBal.setValue(getSubtractedValue(this.schdPri,
				this.schdPriPaid));
		// net pay
		BigDecimal netpay = getDCBValue(this.schdDefPftPaid)
				.add(getDCBValue(this.schdDefPriPaid))
				.add(getDCBValue(this.schdpenalityPaid))
				.add(getDCBValue(this.schdPftPaid))
				.add(getDCBValue(this.schdPriPaid))
				.subtract(getDCBValue(this.schdWaiverPaid))
				.subtract(getDCBValue(this.schdRefundPaid)).abs();
		this.netPayment.setValue(netpay);
		// Repay total summary
		this.rpyAmountBal.setValue(repayAmount
				.subtract(getDCBValue(this.netPayment)));
		if (!isWaiver) {
			if (this.rpyAmount.isDisabled()) {
				// if it is not the first term add the paid values to the totals
				this.rpyPrincipal.setValue(getAddedValue(this.schdDefPriPaid,
						this.schdPriPaid).add(getDCBValue(this.rpyPrincipal)));//
				this.rpyProfit.setValue(getAddedValue(this.schdDefPftPaid,
						this.schdPftPaid).add(getDCBValue(this.rpyProfit)));//
				this.rpyPenalty.setValue(getDCBValue(this.schdpenalityPaid)
						.add(getDCBValue(this.rpyPenalty)));//
			} else {
				// if it is the first term do not consider the totals just
				// replace the values
				this.rpyPrincipal.setValue(getAddedValue(this.schdDefPriPaid,
						this.schdPriPaid));
				this.rpyProfit.setValue(getAddedValue(this.schdDefPftPaid,
						this.schdPftPaid));
				this.rpyPenalty.setValue(getDCBValue(this.schdpenalityPaid));
			}
		}
		// condition to allow waiver
		if (getDCBValue(this.schdpenality).compareTo(zero) > 0
				&& getDCBValue(this.schdpenalityBal).compareTo(zero) == 0) {
			this.schdWaiverPaid.setDisabled(false);
		} else {
			this.schdWaiverPaid.setDisabled(true);
		}
		// condition to allow refund
		if (getDCBValue(this.schdPftBal).compareTo(zero) == 0
				&& getDCBValue(this.schdPriBal).compareTo(zero) == 0) {
			this.row_Refund.setVisible(true);
		} else {
			this.row_Refund.setVisible(false);
		}

		// if repay amount equal to zero or end of schedule hide next button
		if (getDCBValue(this.rpyAmountBal).compareTo(zero) > 0
				&& count < listFinSchdDetails.size() - 1) {
			this.btnNext.setVisible(true);
			this.btnPayment.setVisible(false);
		} else {
			this.btnNext.setVisible(false);
			if (getDCBValue(this.rpyAmount).compareTo(zero) > 0) {
				this.btnPayment.setVisible(true);
			} else {
				this.btnPayment.setVisible(false);
			}

		}

		
		 * if (repayAmount.compareTo(zero) > 0 &&
		 * getDCBValue(this.schdPftPaid).compareTo(zero) <= 0 &&
		 * getDCBValue(this.schdPriPaid).compareTo(zero) <= 0) { //
		 * doProcess(financeScheduleDetail, zero, false); try {
		 * PTMessageUtils.showErrorMessage("insufficient Amount "); } catch
		 * (InterruptedException e) { e.printStackTrace(); } //throw new
		 * WrongValueException(this.rpyAmount,"insufficient Amount "); }
		 

	}

	*//**
	 * To calculate the Fee amount by schedule date either late payment or early
	 * payment <br>
	 * IN PaymentDialogCtrl.java
	 * 
	 * @param financeScheduleDetail
	 * @return BigDecimal
	 *//*
	private BigDecimal getFeeValue(FinanceScheduleDetail financeScheduleDetail) {
		BigDecimal FeeAmount = new BigDecimal(0);
		if (isEarlypay || isLatePay) {
			List<Rule> listRules = new ArrayList<Rule>();
			if (isEarlypay) {
				listRules = getPaymentHeader().getEarlyPayRule();
			} else if (isLatePay) {
				listRules = getPaymentHeader().getLatePayRule();
			}
			// execute list rules and store in the map
			ArrayList<PaymentFee> paymentFees = new ArrayList<PaymentFee>();
			ArrayList<PaymentFee> paymentRefund = new ArrayList<PaymentFee>();
			for (Rule rule : listRules) {
				PaymentFee paymentFee = new PaymentFee();
				try {
					BeanUtils.copyProperties(paymentFee, rule);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Double feeAmount = executeRule(rule.getSQLRule(), getFeesData());
				paymentFee.setExcAmount(new BigDecimal(feeAmount));

				if (rule.getWaiverDecider().equals("F")) {
					if (rule.getWaiverPerc() != null
							&& rule.getWaiverPerc()
									.compareTo(new BigDecimal(0)) > 0) {
						BigDecimal maxAmount = paymentFee
								.getExcAmount()
								.multiply(rule.getWaiverPerc())
								.divide(new BigDecimal(100), 9,
										RoundingMode.HALF_DOWN);
						paymentFee.setMaxAmount(maxAmount);
					} else {
						paymentFee.setWaiverPerc(zero);
						paymentFee.setMaxAmount(zero);
					}
					paymentFees.add(paymentFee);
				} else {
					paymentFee.setMaxAmount(zero);
					paymentFee.setWaiverPerc(zero);
					paymentRefund.add(paymentFee);

				}

			}
			if (mapPayFees.containsKey(financeScheduleDetail.getSchDate())) {
				mapPayFees.remove(financeScheduleDetail.getSchDate());
			}
			mapPayFees.put(financeScheduleDetail.getSchDate(), paymentFees);
			if (mapPayRefund.containsKey(financeScheduleDetail.getSchDate())) {
				mapPayRefund.remove(financeScheduleDetail.getSchDate());
			}
			mapPayRefund.put(financeScheduleDetail.getSchDate(), paymentRefund);
		} else {
			// not refund and no penalty for payment on scheduled date
			if (mapPayFees.containsKey(financeScheduleDetail.getSchDate())) {
				mapPayFees.remove(financeScheduleDetail.getSchDate());
			}
			if (mapPayRefund.containsKey(financeScheduleDetail.getSchDate())) {
				mapPayRefund.remove(financeScheduleDetail.getSchDate());
			}
		}
		return FeeAmount;
	}

	*//**
	 * 
	 * <br>
	 * IN PaymentDialogCtrl.java
	 * 
	 * @param paymentFees
	 * @return BigDecimal
	 *//*
	private BigDecimal getPenaltyOrRefund(ArrayList<PaymentFee> paymentFees) {
		BigDecimal totAmt = new BigDecimal(0);
		if (paymentFees != null && paymentFees.size() > 0) {
			for (PaymentFee paymentFee : paymentFees) {
				totAmt = totAmt.add(paymentFee.getExcAmount());

			}
		}
		return totAmt;
	}

	*//**
	 * 
	 * <br>
	 * IN PaymentDialogCtrl.java
	 * 
	 * @param paymentFees
	 * @return BigDecimal
	 *//*
	private BigDecimal getMaxPenaltyOrRefund(ArrayList<PaymentFee> paymentFees) {
		BigDecimal totAmt = new BigDecimal(0);
		if (paymentFees != null && paymentFees.size() > 0) {
			for (PaymentFee paymentFee : paymentFees) {
				totAmt = totAmt.add(paymentFee.getMaxAmount());

			}
		}
		return totAmt;
	}

	*//**
	 * 
	 * To Execute the Script Rule with Fees object data IN
	 * PaymentDetailDialogCtrl.java
	 * 
	 * @param rule
	 * @param Fees
	 * @return Double
	 *//*
	@SuppressWarnings("unchecked")
	private Double executeRule(String rule, Object object) {
		HashMap<String, Object> fieldsandvalues = new HashMap<String, Object>();
		if (object instanceof Fees) {
			Fees fees = (Fees) object;
			fieldsandvalues = fees.getDeclaredFieldValues();
		} else {
			fieldsandvalues = (HashMap<String, Object>) object;
		}
		ArrayList<String> keyset = new ArrayList<String>(
				fieldsandvalues.keySet());
		// create a engine factory
		ScriptEngineManager factory = new ScriptEngineManager();
		// specify the engine type
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		// add variables and values to the execution map
		for (int i = 0; i < keyset.size(); i++) {
			Object var = fieldsandvalues.get(keyset.get(i));
			if (var instanceof String) {
				var = var.toString().trim();
			}
			engine.put(keyset.get(i), var);
		}
		try {// pass script
			String ScriptRule = "function Pennant(){" + rule + "}Pennant();";
			// execute rule
			engine.eval(ScriptRule);

		} catch (ScriptException e) {
			e.printStackTrace();
		}
		// clear fields
		fieldsandvalues.clear();
		// return result as Double value
		return (Double) engine.get("Result");

	}

	*//**
	 * To go back to previous schedule calculated amounts <br>
	 * IN PaymentDialogCtrl.java
	 * 
	 * @param financeScheduleDetail
	 * @param repayAmount
	 *            void
	 *//*
	private void doPrevious(FinanceScheduleDetail financeScheduleDetail) {
		// change date
		this.rpyTodate.setValue(DateUtility.formatUtilDate(
				financeScheduleDetail.getSchDate(),
				PennantConstants.dateFormate));
		FinanceRepayments repayments = maFinRepay.get(financeScheduleDetail
				.getSchDate());
		BigDecimal aDecimal;
		if (count != 0) {
			aDecimal = maFinRepay.get(
					listFinSchdDetails.get(count - 1).getSchDate())
					.getRepayBal();
		} else {
			aDecimal = getDCBValue(rpyAmount);
		}
		doProcess(getCurFinanceScheduleDetail(), aDecimal, false, true);
		this.rpyAmountBal.setValue(repayments.getRepayBal());
		this.rpyPrincipal.setValue(repayments.getRepaypri());
		this.rpyProfit.setValue(repayments.getRepayPft());
		this.rpyPenalty.setValue(repayments.getRepayPenal());
		this.rpyWaiver.setValue(repayments.getRepayWaiver());
		this.rpyRefund.setValue(repayments.getRepayRefund());
	}

	// +++++++++++++++++++++++++//
	// ++++ Events +++++++//
	// +++++++++++++++++++++++++//

	*//**
	 * 
	 * <br>
	 * IN PaymentDialogCtrl.java
	 * 
	 * @param event
	 *            void
	 *//*
	public void onChange$payDate(Event event) {
		if (this.payDate.getValue() != null) {
			BigDecimal amt = new BigDecimal(0);
			if (count == 0) {
				amt = getDCBValue(this.rpyAmount);
			} else {
				amt = maFinRepay.get(this.prvSchdate).getRepayBal();
			}
			doDateChangeProcess(getDate(this.payDate), amt);
		}

	}

	*//**
	 * To calculate repay values on repay amount change <br>
	 * IN PaymentDialogCtrl.java
	 * 
	 * @param event
	 *            void
	 *//*
	public void onChange$rpyAmount(Event event) {
		doProcess(getCurFinanceScheduleDetail(), getDCBValue(this.rpyAmount),
				false, false);

	}

	*//**
	 * To calculate repay values on Waiver amount paid <br>
	 * IN PaymentDialogCtrl.java
	 * 
	 * @param event
	 *            void
	 *//*
	public void onChange$schdWaiverPaid(Event event) {
		BigDecimal maxVal = getFRAM(getMaxPenaltyOrRefund(mapPayFees
				.get(getCurFinanceScheduleDetail().getSchDate())));
		if (getDCBValue(this.schdWaiverPaid).compareTo(maxVal) > 0) {
			this.schdWaiverPaid.setValue(zero);
			throw new WrongValueException(this.schdWaiverPaid,
					"Amount Can Not be Greater Than " + maxVal);
		} else {

			if (maFinRepay.containsKey(getCurFinanceScheduleDetail()
					.getSchDate())) {
				maFinRepay.get(getCurFinanceScheduleDetail().getSchDate())
						.setFinWaiver(
								getUNFRAM(getDCBValue(this.schdWaiverPaid)));
			} else {
				doWriteComponentsToBean();
				maFinRepay.put(getCurFinanceScheduleDetail().getSchDate(),
						getFinanceRepayments());
			}

			BigDecimal amt = new BigDecimal(0);
			if (count == 0) {
				amt = getDCBValue(this.rpyAmount);
			} else {
				amt = maFinRepay.get(this.prvSchdate).getRepayBal();
			}

			doProcess(getCurFinanceScheduleDetail(), amt, true, false);
		}
	}

	*//**
	 * To calculate repay values on Waiver amount paid <br>
	 * IN PaymentDialogCtrl.java
	 * 
	 * @param event
	 *            void
	 *//*
	public void onChange$schdRefundPaid(Event event) {

		BigDecimal maxVal = getFRAM(getPenaltyOrRefund(mapPayRefund
				.get(getCurFinanceScheduleDetail().getSchDate())));
		if (getDCBValue(this.schdRefundPaid).compareTo(maxVal) > 0) {
			this.schdRefundPaid.setValue(zero);
			throw new WrongValueException(this.schdRefundPaid,
					"Amount Can Not be Greater Than " + maxVal);
		} else {

			if (maFinRepay.containsKey(getCurFinanceScheduleDetail()
					.getSchDate())) {
				maFinRepay.get(getCurFinanceScheduleDetail().getSchDate())
						.setFinRefund(
								getUNFRAM(getDCBValue(this.schdRefundPaid)));
			} else {
				doWriteComponentsToBean();
				maFinRepay.put(getCurFinanceScheduleDetail().getSchDate(),
						getFinanceRepayments());
			}
			BigDecimal amt = new BigDecimal(0);
			if (count == 0) {
				amt = getDCBValue(this.rpyAmount);
			} else {
				amt = maFinRepay.get(this.prvSchdate).getRepayBal();

			}
			doProcess(getCurFinanceScheduleDetail(), amt, true, false);
		}
	}

	*//**
	 * To calculate the Re payment values of the Next Schedule <br>
	 * IN PaymentDialogCtrl.java
	 * 
	 * @param event
	 *            void
	 *//*
	public void onClick$btnNext(Event event) {
		// save to the repay work table and update the details in schedule work
		// table
		doWriteComponentsToBean();
		doWriteSceduleDetail();
		doSaveProcess();
		if (getDCBValue(this.rpyAmountBal).compareTo(new BigDecimal(0)) > 0) {
			count = count + 1;
			setCurFinanceScheduleDetail(listFinSchdDetails.get(count));
			doProcess(getCurFinanceScheduleDetail(),
					getDCBValue(this.rpyAmountBal), false, true);
		}

	}

	*//**
	 * To calculate the Re payment values of the Previous Schedule <br>
	 * IN PaymentDialogCtrl.java
	 * 
	 * @param event
	 *            void
	 *//*
	public void onClick$btnPrevious(Event event) {
		count = count - 1;
		setCurFinanceScheduleDetail(listFinSchdDetails.get(count));
		doPrevious(getCurFinanceScheduleDetail());

	}

	public void onClick$btnPayment(Event event) {
		// TODO
		doWriteComponentsToBean();
		doWriteSceduleDetail();
		doSaveProcess();
		maFinRepay.put(getCurFinanceScheduleDetail().getSchDate(),
				getFinanceRepayments());
		maFinSchd.put(getCurFinanceScheduleDetail().getSchDate(),
				getCurFinanceScheduleDetail());
		List<Date> dates = new ArrayList<Date>(maFinRepay.keySet());
		for (Date date : dates) {
			getPaymentService().save(maFinRepay.get(date), "");
		}
		getPaymentService().deleteWorkByFinRef(
				getFinanceMain().getFinReference());
		List<Date> schddates = new ArrayList<Date>(maFinSchd.keySet());
		for (Date date : schddates) {
			getPaymentService().updateMainScheduleDetails(maFinSchd.get(date));
		}
		closeTab();
	}

	// +++++++++######## New window for fee list ##############++++++++++++++//

	*//**
	 * 
	 * <br>
	 * IN PaymentDialogCtrl.java
	 * 
	 * @param event
	 *            void
	 *//*
	public void onClick$btnPenalty(Event event) {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("payments",
				mapPayFees.get(getCurFinanceScheduleDetail().getSchDate()));
		hashMap.put("formetter", financeMain.getLovDescFinFormatter());
		Executions.createComponents(
				"/WEB-INF/pages/FinanceManagement/Payments/RepayFees.zul",
				null, hashMap);

	}

	*//**
	 * 
	 * <br>
	 * IN PaymentDialogCtrl.java
	 * 
	 * @param event
	 *            void
	 *//*
	public void onClick$btnRefund(Event event) {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("payments",
				mapPayRefund.get(getCurFinanceScheduleDetail().getSchDate()));
		hashMap.put("formetter", financeMain.getLovDescFinFormatter());
		Executions.createComponents(
				"/WEB-INF/pages/FinanceManagement/Payments/RepayFees.zul",
				null, hashMap);

	}

	*//**
	 * 
	 * <br>
	 * IN PaymentDialogCtrl.java
	 * 
	 * @param event
	 * @throws Exception
	 *             void
	 *//*
	@SuppressWarnings("unchecked")
	public void onCreate$window_RepayFees(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		int formet = 0;
		ArrayList<PaymentFee> paymentFees = new ArrayList<PaymentFee>();
		final Map<String, Object> args = getCreationArgsMap(event);
		if (args.containsKey("payments")) {
			paymentFees = (ArrayList<PaymentFee>) args.get("payments");
		}
		if (args.containsKey("formetter")) {
			formet = (Integer) args.get("formetter");
		}
		if (paymentFees != null && paymentFees.size() > 0) {
			for (PaymentFee payfee : paymentFees) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(payfee.getRuleCode());
				lc.setSclass("defListcell");
				lc.setParent(item);
				lc = new Listcell(payfee.getRuleCodeDesc());
				lc.setSclass("defListcell");
				lc.setParent(item);
				lc = new Listcell(String.valueOf(getFRAM(payfee.getExcAmount(),
						formet)));
				lc.setSclass("defListcell");
				lc.setParent(item);
				lc = new Listcell(String.valueOf(payfee.getWaiverPerc()));
				lc.setSclass("defListcell");
				lc.setParent(item);
				lc = new Listcell(String.valueOf(getFRAM(payfee.getMaxAmount(),
						formet)));
				lc.setSclass("defListcell");
				lc.setParent(item);

				this.listBoxFees.appendChild(item);
			}
		}
		this.window_RepayFees.doModal();

		logger.debug("Leaving" + event.toString());
	}

	*//**
	 * 
	 * <br>
	 * IN PaymentDialogCtrl.java
	 * 
	 * @param bigDecimal
	 * @param formatter
	 * @return BigDecimal
	 *//*
	private BigDecimal getFRAM(BigDecimal bigDecimal, int formatter) {
		BigDecimal some = PennantAppUtil.formateAmount(bigDecimal, formatter);
		some = some.setScale(formatter, RoundingMode.HALF_DOWN);
		return some;
	}

	*//**
	 * 
	 * <br>
	 * IN PaymentDialogCtrl.java
	 * 
	 * @param event
	 *            void
	 *//*
	public void onClick$btnCloseRepayfees(Event event) {
		this.window_RepayFees.onClose();
	}

	// +++++++++######## new window for fee list ##############++++++++++++++//

	// ////=========================/////
	// ////===Utility Helpers===/////
	// ////=========================/////

	*//**
	 * To Close the tab when fin reference search dialog is closed <br>
	 * IN PaymentDialogCtrl.java void
	 *//*
	public void closeTab() {
		this.window_PaymentDialog.onClose();
		Tab tab = (Tab) tabbox.getFellowIfAny("tab_payment");
		tab.close();
	}

	*//**
	 * To avoid null from getting the value of decimal box <br>
	 * IN PaymentDialogCtrl.java
	 * 
	 * @param decimalbox
	 * @return BigDecimal
	 *//*
	private BigDecimal getDCBValue(Decimalbox decimalbox) {
		if (decimalbox.getValue() != null) {
			return decimalbox.getValue();
		}
		return new BigDecimal(0);

	}

	*//**
	 * 
	 * <br>
	 * IN PaymentDialogCtrl.java
	 * 
	 * @param date
	 * @return Date
	 *//*
	private Date getDate(Datebox date) {
		return DateUtility.getDBDate(DateUtility.formatUtilDate(
				date.getValue(), PennantConstants.DBDateFormat));
	}

	*//**
	 * Subtracts the two decimal box values . Null safe <br>
	 * IN PaymentDialogCtrl.java
	 * 
	 * @param decimalbox1
	 * @param decimalbox2
	 * @return BigDecimal
	 *//*
	private BigDecimal getSubtractedValue(Decimalbox decimalbox1,
			Decimalbox decimalbox2) {
		BigDecimal schd;
		BigDecimal schdpaid;
		if (decimalbox1.getValue() != null) {
			schd = decimalbox1.getValue();
		} else {
			schd = new BigDecimal(0);
		}
		if (decimalbox2.getValue() != null) {
			schdpaid = decimalbox2.getValue();
		} else {
			schdpaid = new BigDecimal(0);
		}

		return schd.subtract(schdpaid);

	}

	*//**
	 * Add the two decimal box values . Null safe <br>
	 * IN PaymentDialogCtrl.java
	 * 
	 * @param decimalbox1
	 * @param decimalbox2
	 * @return BigDecimal
	 *//*
	private BigDecimal getAddedValue(Decimalbox decimalbox1,
			Decimalbox decimalbox2) {
		BigDecimal schd;
		BigDecimal schdpaid;
		if (decimalbox1.getValue() != null) {
			schd = decimalbox1.getValue();
		} else {
			schd = new BigDecimal(0);
		}
		if (decimalbox2.getValue() != null) {
			schdpaid = decimalbox2.getValue();
		} else {
			schdpaid = new BigDecimal(0);
		}

		return schd.add(schdpaid);

	}

	*//**
	 * 
	 * <br>
	 * IN PaymentDialogCtrl.java
	 * 
	 * @param bigDecimal
	 * @return BigDecimal
	 *//*
	private BigDecimal getFRAM(BigDecimal bigDecimal) {
		BigDecimal some = PennantAppUtil.formateAmount(bigDecimal,
				financeMain.getLovDescFinFormatter());
		some = some.setScale(financeMain.getLovDescFinFormatter(),
				RoundingMode.HALF_DOWN);
		return some;
	}

	*//**
	 * 
	 * <br>
	 * IN PaymentDialogCtrl.java
	 * 
	 * @param bigDecimal
	 * @return BigDecimal
	 *//*
	private BigDecimal getUNFRAM(BigDecimal bigDecimal) {
		return PennantAppUtil.unFormateAmount(bigDecimal,
				financeMain.getLovDescFinFormatter());
	}

	*//**
	 * To set the Fees data for the Rule Execution <br>
	 * IN PaymentDialogCtrl.java
	 * 
	 * @return Fees
	 *//*
	private Fees getFeesData() {

		String event = "";
		if (isEarlypay) {
			event = "EARLYPAY";
		} else {
			event = "LATEPAY";
		}
		// Amount Codes preparation
		DataSet dataSet = AEAmounts.createDataSet(financeMain, event,
				getDate(this.payDate), getCurFinanceScheduleDetail()
						.getSchDate());

		amountCodes = new AEAmountCodes();
		Date lastRepayDate = null;
		Date nextRepayDate = null;
		if (count == 0) {
			lastRepayDate = getFinanceMain().getLastRepayDate();
			
			 * if (count==listFinSchdDetails.size()-1) { nextRepayDate
			 * =lastRepayDate; }else{ nextRepayDate =
			 * listFinSchdDetails.get(count + 1).getSchDate(); }
			 

		} else if (count > 0) {
			lastRepayDate = listFinSchdDetails.get(count - 1).getSchDate();

			
			 * if
			 * (getCurFinanceScheduleDetail().getSchDate().compareTo(getFinanceMain
			 * ().getMaturityDate()) == 0) { nextRepayDate =
			 * getFinanceMain().getMaturityDate(); }else{ nextRepayDate =
			 * listFinSchdDetails.get(count + 1).getSchDate(); }
			 
		}

		// TODO need to get the Schedule data
		// amountCodes = aeAmounts.procAEAmounts(getFinanceMain(), , pftDetail,
		// valueDate);
		Fees fees = new Fees();
		HashMap<String, Object> paymentsRuleMap = new HashMap<String, Object>();
		try {
			paymentsRuleMap = getEngineExecution().getFeedata(dataSet, amountCodes);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (paymentsRuleMap.containsKey("fees")) {
			fees = (Fees) paymentsRuleMap.get("fees");
		}
		if (paymentsRuleMap.containsKey("amountcodes")) {
			setAmountCodes((AEAmountCodes) paymentsRuleMap.get("amountcodes"));
		}

		return fees;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	*//**
	 * when clicks on button "btnSearchRepayAcctId"
	 * 
	 * @param event
	 *//*
	public void onClick$btnSearchRepayAcctId(Event event) {
		logger.debug("Entering " + event.toString());
		Filter[] filters;
		if (!String.valueOf(getFinanceMain().getCustID()).equals("")) {
			filters = new Filter[3];
			filters[0] = new Filter("AcPurpose", new String[] { "G", "M" },
					Filter.OP_IN);
			filters[1] = new Filter("AcCustId", getFinanceMain().getCustID(),
					Filter.OP_EQUAL);
			filters[2] = new Filter("AcCcy", this.finCcy.getValue(),
					Filter.OP_EQUAL);
		} else {
			filters = new Filter[1];
			filters[0] = new Filter("AcPurpose", "G", Filter.OP_EQUAL);
		}
		Object dataObject = ExtendedSearchListBox.show(
				this.window_PaymentDialog, "Accounts", filters);
		if (dataObject instanceof String) {
			this.custFundingAcc.setValue(getFinanceMain().getRepayAccountId());
		} else {
			Accounts details = (Accounts) dataObject;
			if (details != null) {
				this.custFundingAcc.setValue(details.getAccountId());
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public void setPaymentService(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	public PaymentService getPaymentService() {
		return paymentService;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setCurFinanceScheduleDetail(
			FinanceScheduleDetail curFinanceScheduleDetail) {
		this.curFinanceScheduleDetail = curFinanceScheduleDetail;
	}

	public FinanceScheduleDetail getCurFinanceScheduleDetail() {
		return curFinanceScheduleDetail;
	}

	public void setFinanceRepayments(FinanceRepayments financeRepayments) {
		this.financeRepayments = financeRepayments;
	}

	public FinanceRepayments getFinanceRepayments() {
		return financeRepayments;
	}

	public AEAmountCodes getAmountCodes() {
		return amountCodes;
	}

	public void setAmountCodes(AEAmountCodes amountCodes) {
		this.amountCodes = amountCodes;
	}

	public PaymentHeader getPaymentHeader() {
		return paymentHeader;
	}

	public void setPaymentHeader(PaymentHeader paymentHeader) {
		this.paymentHeader = paymentHeader;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}
	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}

}*/
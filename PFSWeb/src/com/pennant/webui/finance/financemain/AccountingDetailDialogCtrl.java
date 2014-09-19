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
 * FileName    		:  FinanceMainDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.financemain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ReportGenerationUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.TransactionDetail;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/financeMain/AccountingDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class AccountingDetailDialogCtrl extends GFCBaseListCtrl<ReturnDataSet> implements Serializable {

	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(AccountingDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_AccountingDetailDialog; // autoWired
	
	//Finance Basic Details for Filling DIV Details Fields
	protected Label 		acc_finType; 							// autoWired
	protected Label 		acc_finCcy; 							// autoWired
	protected Label 		acc_scheduleMethod; 					// autoWired
	protected Label 		acc_profitDaysBasis; 					// autoWired
	protected Label 		acc_finReference; 						// autoWired
	protected Label 		label_ScheduleDetailDialog_GrcEndDate; 						// autoWired	
	protected Label 		acc_grcEndDate; 						// autoWired	

	// Accounting Set Details Tab
	protected Button 		btnAccounting;							// autoWired
	private Label 		label_AccountingDisbCrVal; 				// autoWired
	private Label 		label_AccountingDisbDrVal; 				// autoWired
	protected Label 		label_AccountingSummaryVal; 			// autoWired
	protected Button 		btnPrintAccounting; 					// autoWired
	protected Listbox 		listBoxFinAccountings;					// autoWired

	private FinanceDetail financeDetail = null; // over handed per parameters
	private Object financeMainDialogCtrl = null;
	private transient boolean 	accountingsExecuted;
	private List<ValueLabel> profitDaysBasisList = new ArrayList<ValueLabel>();
	private List<ValueLabel> schMethodList = new ArrayList<ValueLabel>();
	
	private transient BigDecimal disbCrSum = BigDecimal.ZERO;
	private transient BigDecimal disbDrSum = BigDecimal.ZERO;

	/**
	 * default constructor.<br>
	 */
	public AccountingDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_AccountingDetailDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		
		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) args.get("financeDetail");
		}

		if (args.containsKey("financeMainDialogCtrl")) {
			setFinanceMainDialogCtrl((Object) args.get("financeMainDialogCtrl"));
		}
		if (args.containsKey("profitDaysBasisList")) {			
			profitDaysBasisList = (List<ValueLabel>) args.get("profitDaysBasisList");
		}
		
		if (args.containsKey("schMethodList")) {			
			schMethodList = (List<ValueLabel>) args.get("schMethodList");
		}

		doShowDialog();
		logger.debug("Leaving " + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws Exception 
	 */
	public void doShowDialog() throws Exception {
		logger.debug("Entering");
		
		Exception wvea = null;
		try {

			FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();

			this.acc_finType.setValue(StringUtils.trimToEmpty(financeMain.getLovDescFinTypeName()));
			this.acc_finCcy.setValue(StringUtils.trimToEmpty(financeMain.getLovDescFinCcyName()));
			this.acc_scheduleMethod.setValue(PennantAppUtil.getlabelDesc(financeMain.getScheduleMethod(), schMethodList));
			this.acc_profitDaysBasis.setValue(PennantAppUtil.getlabelDesc(financeMain.getProfitDaysBasis(), profitDaysBasisList));
			this.acc_finReference.setValue(StringUtils.trimToEmpty(financeMain.getFinReference()));
			this.acc_grcEndDate.setValue(DateUtility.formatDate(financeMain.getGrcPeriodEndDate(), PennantConstants.dateFormate)) ;

			try {
				getFinanceMainDialogCtrl().getClass().getMethod("setAccountingDetailDialogCtrl", 
						this.getClass()).invoke(getFinanceMainDialogCtrl(), this);
			} catch (Exception e) {
				logger.error(e);
			}
			
			if(getFinanceDetail().getFinScheduleData().getFinanceType() == null || 
					!getFinanceDetail().getFinScheduleData().getFinanceType().isFInIsAlwGrace()) {
				label_ScheduleDetailDialog_GrcEndDate.setVisible(false);
				acc_grcEndDate.setVisible(false);
			}

			//Finance Accounting Posting Details & Commitment Disbursement Posting Details
			
			if (getFinanceDetail().getTransactionEntries() != null && !getFinanceDetail().getTransactionEntries().isEmpty()) {				
				boolean executed = false;
				if (!financeMain.isNew() && 
						getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() > 0 ) {

					if(!(financeMain.getCustID() == 0 || financeMain.getCustID() == Long.MIN_VALUE)){
						
						try {
							executeAccounting(true);
							executed = true;
						} catch (Exception e) {
							if(e.getCause().getClass().equals(WrongValuesException.class)){
								wvea = e;
							}
							logger.error(e.getMessage());
						}	
					}
				}
				
				if(!executed){
					doFillAccounting(getFinanceDetail().getTransactionEntries());
					doFillCmtAccounting(getFinanceDetail().getCmtFinanceEntries(), 0);
				}
			} 

			getBorderLayoutHeight();
			this.listBoxFinAccountings.setHeight(this.borderLayoutHeight- 220 +"px");
			this.window_AccountingDetailDialog.setHeight(this.borderLayoutHeight-80+"px");

			if(wvea != null){
				if(wvea.getCause().getClass().equals(WrongValuesException.class)){
					throw wvea;
				}
			}
			
		} catch (final Exception e) {
			logger.error(e);
			if(e.getCause().getClass().equals(WrongValuesException.class)){
				throw e;	
			}
			PTMessageUtils.showErrorMessage(e.toString());
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Method to fill list box in Accounting Tab <br>
	 *  
	 * @param accountingSetEntries (List)
	 * 
	 */
	public void doFillAccounting(List<?> accountingSetEntries) {
		logger.debug("Entering");
		
		setDisbCrSum(BigDecimal.ZERO);
		setDisbDrSum(BigDecimal.ZERO);
		
		int formatter = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter();

		this.listBoxFinAccountings.getItems().clear();
		this.listBoxFinAccountings.setSizedByContent(true);
		if (accountingSetEntries != null && !accountingSetEntries.isEmpty()) {
			for (int i = 0; i < accountingSetEntries.size(); i++) {

				Listitem item = new Listitem();
				Listcell lc;
				if (accountingSetEntries.get(i) instanceof TransactionEntry) {
					TransactionEntry entry = (TransactionEntry) accountingSetEntries.get(i);

					//Adding List Group to ListBox
					if(i == 0){
						Listgroup listgroup = new Listgroup(entry.getLovDescEventCodeName()+"-"+entry.getLovDescEventCodeDesc());
						this.listBoxFinAccountings.appendChild(listgroup);
					}

					lc = new Listcell(PennantAppUtil.getlabelDesc(
							entry.getDebitcredit(), PennantStaticListUtil.getTranType()));
					lc.setParent(item);
					lc = new Listcell(entry.getTransDesc());
					lc.setParent(item);
					lc = new Listcell(entry.getTranscationCode());
					lc.setParent(item);
					lc = new Listcell(entry.getRvsTransactionCode());
					lc.setParent(item);
					lc = new Listcell(entry.getAccount());
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
				} else if (accountingSetEntries.get(i) instanceof ReturnDataSet) {
					ReturnDataSet entry = (ReturnDataSet) accountingSetEntries.get(i);
					
					//Highlighting Failed Posting Details 
					String sClassStyle = "";
					if(!StringUtils.trimToEmpty(entry.getErrorId()).equals("") && !StringUtils.trimToEmpty(entry.getErrorId()).equals("0000")){
						sClassStyle = "color:#FF0000;";
					}

					//Adding List Group to ListBox
					if(i == 0){
						Listgroup listgroup = new Listgroup(entry.getFinEvent() +"-"+ entry.getLovDescEventCodeName());
						this.listBoxFinAccountings.appendChild(listgroup);
					}

					Hbox hbox = new Hbox();
					Label label = new Label(PennantAppUtil.getlabelDesc(
							entry.getDrOrCr(), PennantStaticListUtil.getTranType()));
					label.setStyle(sClassStyle);
					hbox.appendChild(label);
					if (!entry.getPostStatus().equals("")) {
						Label la = new Label("*");
						la.setStyle("color:red;");
						hbox.appendChild(la);
					}
					lc = new Listcell();
					lc.setStyle(sClassStyle);
					lc.appendChild(hbox);
					lc.setParent(item);
					lc = new Listcell(entry.getTranDesc());
					lc.setStyle(sClassStyle);
					lc.setParent(item);
					if(entry.isShadowPosting()){
						lc = new Listcell("Shadow");
						lc.setStyle(sClassStyle);
						lc.setParent(item);
						lc = new Listcell("Shadow");
						lc.setStyle(sClassStyle);
						lc.setParent(item);
					}else{
						lc = new Listcell(entry.getTranCode());
						lc.setStyle(sClassStyle);
						lc.setParent(item);
						lc = new Listcell(entry.getRevTranCode());
						lc.setStyle(sClassStyle);
						lc.setParent(item);
					}
					lc = new Listcell(entry.getAccountType());
					lc.setStyle(sClassStyle);
					lc.setParent(item);
					lc = new Listcell(PennantApplicationUtil.formatAccountNumber(entry.getAccount()));
					lc.setStyle("font-weight:bold;");
						lc.setStyle(sClassStyle);
					lc.setParent(item);	
					
					lc = new Listcell(entry.getAcCcy());
					lc.setParent(item);

					BigDecimal amt = entry.getPostAmount()!=null?entry.getPostAmount(): BigDecimal.ZERO;

					if(entry.getAcCcy().equals(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy())){
						lc = new Listcell(PennantApplicationUtil.amountFormate(amt,formatter));
						
						if (entry.getDrOrCr().equals(PennantConstants.CREDIT)) {
							setDisbCrSum(getDisbCrSum().add(amt));
						} else if (entry.getDrOrCr().equals(PennantConstants.DEBIT)) {
							setDisbDrSum(getDisbDrSum().add(amt));
						}
						
					}else{
						lc = new Listcell(PennantApplicationUtil.amountFormate(amt,entry.getFormatter()));
					}

					lc.setStyle("font-weight:bold;text-align:right;");
					lc.setStyle(sClassStyle+"font-weight:bold;text-align:right;");
					lc.setParent(item);
					lc = new Listcell(entry.getErrorId().equals("0000") ? "" : entry.getErrorId());
					lc.setStyle("font-weight:bold;color:red;");
					lc.setTooltiptext(entry.getErrorMsg());
					lc.setParent(item);
					accountingsExecuted = true;
				}
				this.listBoxFinAccountings.appendChild(item);
			}

			this.getLabel_AccountingDisbCrVal().setValue(PennantApplicationUtil.amountFormate(getDisbCrSum(), formatter));
			this.getLabel_AccountingDisbDrVal().setValue(PennantAppUtil.amountFormate(getDisbDrSum(), formatter));
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method to fill list box in Commitment Postings Accounting Tab <br>
	 * 
	 * @param cmtFinEntries
	 *            (List)
	 * @param listbox
	 *            (Listbox)
	 */
	public void doFillCmtAccounting(List<?> cmtFinEntries, int formatter) {
		logger.debug("Entering");

		if (cmtFinEntries != null && !cmtFinEntries.isEmpty()) {
			for (int i = 0; i < cmtFinEntries.size(); i++) {

				Listitem item = new Listitem();
				Listcell lc;
				if (cmtFinEntries.get(i) instanceof TransactionEntry) {
					TransactionEntry entry = (TransactionEntry) cmtFinEntries.get(i);

					//Adding List Group to ListBox
					if(i == 0){
						Listgroup listgroup = new Listgroup(entry.getLovDescEventCodeName()+"-"+entry.getLovDescEventCodeDesc());
						listBoxFinAccountings.appendChild(listgroup);
					}

					lc = new Listcell(PennantAppUtil.getlabelDesc(entry.getDebitcredit(), PennantStaticListUtil.getTranType()));
					lc.setParent(item);
					lc = new Listcell(entry.getTransDesc());
					lc.setParent(item);
					lc = new Listcell(entry.getTranscationCode());
					lc.setParent(item);
					lc = new Listcell(entry.getRvsTransactionCode());
					lc.setParent(item);
					lc = new Listcell(entry.getAccount());
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
				} else if (cmtFinEntries.get(i) instanceof ReturnDataSet) {
					ReturnDataSet entry = (ReturnDataSet) cmtFinEntries.get(i);

					//Adding List Group to ListBox
					if(i == 0){
						Listgroup listgroup = new Listgroup(entry.getFinEvent()+"-"+entry.getLovDescEventCodeName());
						listBoxFinAccountings.appendChild(listgroup);
					}

					Hbox hbox = new Hbox();
					Label label = new Label(PennantAppUtil.getlabelDesc(entry.getDrOrCr(), PennantStaticListUtil.getTranType()));
					hbox.appendChild(label);
					if (!entry.getPostStatus().equals("")) {
						Label la = new Label("*");
						la.setStyle("color:red;");
						hbox.appendChild(la);
					}
					lc = new Listcell();
					lc.appendChild(hbox);
					lc.setParent(item);
					lc = new Listcell(entry.getTranDesc());
					lc.setParent(item);
					if(entry.isShadowPosting()){
						lc = new Listcell("Shadow");
						lc.setParent(item);
						lc = new Listcell("Shadow");
						lc.setParent(item);
					}else{
						lc = new Listcell(entry.getTranCode());
						lc.setParent(item);
						lc = new Listcell(entry.getRevTranCode());
						lc.setParent(item);
					}
					lc = new Listcell(Labels.getLabel("label_"+entry.getAccountType()));
					lc.setParent(item);
					lc = new Listcell(PennantApplicationUtil.formatAccountNumber(entry.getAccount()));
					lc.setStyle("font-weight:bold;");
					lc.setParent(item);
					
					lc = new Listcell(entry.getAcCcy());
					lc.setParent(item);
					
					BigDecimal amt = entry.getPostAmount()!=null?entry.getPostAmount(): BigDecimal.ZERO.setScale(0, RoundingMode.FLOOR);
					
					lc = new Listcell(PennantAppUtil.amountFormate(amt,formatter));
					lc.setStyle("font-weight:bold;text-align:right;");
					lc.setParent(item);
					lc = new Listcell(entry.getErrorId().equals("0000") ? "" : entry.getErrorId());
					lc.setStyle("font-weight:bold;color:red;");
					lc.setTooltiptext(entry.getErrorMsg());
					lc.setParent(item);
				}
				listBoxFinAccountings.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method to fill list box in GraceEnd Postings Accounting Tab <br>
	 * 
	 * @param grcEndFinEntries
	 *            (List)
	 * @param listbox
	 *            (Listbox)
	 */
	public void doFillGraceEndAccounting(List<?> grcEndFinEntries) {
		logger.debug("Entering");
		int formatter = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
		
		if (grcEndFinEntries != null && !grcEndFinEntries.isEmpty()) {
			for (int i = 0; i < grcEndFinEntries.size(); i++) {
		
				Listitem item = new Listitem();
				Listcell lc;
				if (grcEndFinEntries.get(i) instanceof TransactionEntry) {
					TransactionEntry entry = (TransactionEntry) grcEndFinEntries.get(i);
					
					//Adding List Group to ListBox
					if(i == 0){
						Listgroup listgroup = new Listgroup(entry.getLovDescEventCodeName()+"-"+entry.getLovDescEventCodeDesc());
						listBoxFinAccountings.appendChild(listgroup);
					}
					
					lc = new Listcell(PennantAppUtil.getlabelDesc(entry.getDebitcredit(), PennantStaticListUtil.getTranType()));
					lc.setParent(item);
					lc = new Listcell(entry.getTransDesc());
					lc.setParent(item);
					lc = new Listcell(entry.getTranscationCode());
					lc.setParent(item);
					lc = new Listcell(entry.getRvsTransactionCode());
					lc.setParent(item);
					lc = new Listcell(entry.getAccount());
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
				} else if (grcEndFinEntries.get(i) instanceof ReturnDataSet) {
					ReturnDataSet entry = (ReturnDataSet) grcEndFinEntries.get(i);
					
					//Adding List Group to ListBox
					if(i == 0){
						Listgroup listgroup = new Listgroup(entry.getFinEvent()+"-"+entry.getLovDescEventCodeName());
						listBoxFinAccountings.appendChild(listgroup);
					}
					
					Hbox hbox = new Hbox();
					Label label = new Label(PennantAppUtil.getlabelDesc(entry.getDrOrCr(), PennantStaticListUtil.getTranType()));
					hbox.appendChild(label);
					if (!entry.getPostStatus().equals("")) {
						Label la = new Label("*");
						la.setStyle("color:red;");
						hbox.appendChild(la);
					}
					lc = new Listcell();
					lc.appendChild(hbox);
					lc.setParent(item);
					lc = new Listcell(entry.getTranDesc());
					lc.setParent(item);
					if(entry.isShadowPosting()){
						lc = new Listcell("Shadow");
						lc.setParent(item);
						lc = new Listcell("Shadow");
						lc.setParent(item);
					}else{
						lc = new Listcell(entry.getTranCode());
						lc.setParent(item);
						lc = new Listcell(entry.getRevTranCode());
						lc.setParent(item);
					}
					lc = new Listcell(Labels.getLabel("label_"+entry.getAccountType()));
					lc.setParent(item);
					lc = new Listcell(PennantApplicationUtil.formatAccountNumber(entry.getAccount()));
					lc.setStyle("font-weight:bold;");
					lc.setParent(item);
					
					lc = new Listcell(entry.getAcCcy());
					lc.setParent(item);
					
					BigDecimal amt = entry.getPostAmount()!=null?entry.getPostAmount(): BigDecimal.ZERO.setScale(0, RoundingMode.FLOOR);
					
					lc = new Listcell(PennantAppUtil.amountFormate(amt,formatter));
					lc.setStyle("font-weight:bold;text-align:right;");
					lc.setParent(item);
					lc = new Listcell(entry.getErrorId().equals("0000") ? "" : entry.getErrorId());
					lc.setStyle("font-weight:bold;color:red;");
					lc.setTooltiptext(entry.getErrorMsg());
					lc.setParent(item);
				}
				listBoxFinAccountings.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}
	

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++ New Button & Double Click Events for Finance Contributor List+++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Method for Executing Accounting Details List
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnAccounting(Event event) throws Exception { 
		logger.debug("Entering" + event.toString());
		executeAccounting(true);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Executing Accounting Details
	 * @throws Exception 
	 */
	private void executeAccounting(boolean onAction) throws Exception{
		logger.debug("Entering");

		try {
			getFinanceMainDialogCtrl().getClass().getMethod("onExecuteAccountingDetail",
					Boolean.class).invoke(getFinanceMainDialogCtrl(), onAction);				
		} catch (Exception e) {
			if(e.getCause().getClass().equals(WrongValuesException.class)){
				throw e;	
			}
			e.printStackTrace();
		}	
		logger.debug("Leaving");
	}
	/**
	 * when the "btnPrintAccounting" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnPrintAccounting(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		String       usrName     = getUserWorkspace().getUserDetails().getUsername();
		FinanceMain  financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		List<Object> list        = null;
		
		if (accountingsExecuted) {
			list = new ArrayList<Object>();
			List<TransactionDetail> accountingDetails = new ArrayList<TransactionDetail>();
			for (ReturnDataSet dataSet : getFinanceDetail().getReturnDataSetList()) {
				TransactionDetail detail = new TransactionDetail();
				detail.setEventCode(dataSet.getFinEvent());
				detail.setEventDesc(dataSet.getLovDescEventCodeName());
				detail.setTranType(dataSet.getDrOrCr().equals("C") ? "Credit" : "Debit");
				detail.setTransactionCode(dataSet.getTranCode());
				detail.setTransDesc(dataSet.getTranDesc());
				detail.setCcy(dataSet.getAcCcy());
				detail.setAccount(PennantApplicationUtil.formatAccountNumber(dataSet.getAccount()));
				detail.setPostAmount(PennantAppUtil.amountFormate(dataSet.getPostAmount(),dataSet.getFormatter() == 0 ?  
						financeMain.getLovDescFinFormatter() : dataSet.getFormatter()));
				accountingDetails.add(detail);
			}
			
			Window window= (Window) this.window_AccountingDetailDialog.getParent().getParent().getParent().getParent().getParent().getParent().getParent();
			if(!accountingDetails.isEmpty()){
				list.add(accountingDetails);
			}
				
			ReportGenerationUtil.generateReport("FINENQ_AccountingDetail",financeMain, 
						list, true, 1, usrName,window);
		} else {
			PTMessageUtils.showErrorMessage(Labels.getLabel("btnPrintAccounting.Error_Message"));
		}
		logger.debug("Leaving" + event.toString());
	}


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}
	
	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}
	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public boolean isAccountingsExecuted() {
		return accountingsExecuted;
	}
	public void setAccountingsExecuted(boolean accountingsExecuted) {
		this.accountingsExecuted = accountingsExecuted;
	}

	public void setDisbCrSum(BigDecimal disbCrSum) {
		this.disbCrSum = disbCrSum;
	}

	public BigDecimal getDisbCrSum() {
		return disbCrSum;
	}

	public void setDisbDrSum(BigDecimal disbDrSum) {
		this.disbDrSum = disbDrSum;
	}

	public BigDecimal getDisbDrSum() {
		return disbDrSum;
	}

	public void setLabel_AccountingDisbCrVal(Label labelAccountingDisbCrVal) {
		this.label_AccountingDisbCrVal = labelAccountingDisbCrVal;
	}

	public Label getLabel_AccountingDisbCrVal() {
		return label_AccountingDisbCrVal;
	}

	public void setLabel_AccountingDisbDrVal(Label labelAccountingDisbDrVal) {
		this.label_AccountingDisbDrVal = labelAccountingDisbDrVal;
	}

	public Label getLabel_AccountingDisbDrVal() {
		return label_AccountingDisbDrVal;
	}

}
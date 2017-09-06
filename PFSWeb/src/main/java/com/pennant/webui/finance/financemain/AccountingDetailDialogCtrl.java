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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.TransactionDetail;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.ReportGenerationUtil;
import com.pennant.webui.collateral.collateralsetup.CollateralBasicDetailsCtrl;
import com.pennant.webui.configuration.vasrecording.VASRecordingDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pennapps.core.AppException;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Finance/financeMain/AccountingDetailDialog.zul file.
 */
public class AccountingDetailDialogCtrl extends GFCBaseCtrl<ReturnDataSet> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(AccountingDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_AccountingDetailDialog; // autoWired

	//Finance Basic Details for Filling DIV Details Fields
	protected Label 		acc_finType; 							// autoWired
	protected Label 		acc_finCcy; 							// autoWired
	protected Label 		acc_scheduleMethod; 					// autoWired
	protected Label 		acc_profitDaysBasis; 					// autoWired
	protected Label 		acc_finReference; 						// autoWired
	protected Label 		label_AccountingDetailDialog_GrcEndDate; 	// autoWired
	protected Label 		label_AccountingDetailDialog_FinType; 	// autoWired
	protected Label 		acc_grcEndDate; 						// autoWired	

	// Accounting Set Details Tab
	protected Button 		btnAccounting;							// autoWired
	private Label 		label_AccountingDisbCrVal; 				// autoWired
	private Label 		label_AccountingDisbDrVal; 				// autoWired
	protected Label 		label_AccountingSummaryVal; 			// autoWired
	protected Button 		btnPrintAccounting; 					// autoWired
	protected Listbox 		listBoxFinAccountings;					// autoWired
	// Post Accounting Set Details Tab
	private Label 			label_PostAccountingDisbCrVal; 				// autoWired
	private Label 			label_PostAccountingDisbDrVal; 				// autoWired
	protected Listbox 		listBoxPostAccountings;					// autoWired
	protected Tab		    accountDetails;
	protected Tab			postAccountDetails;
	protected Tabpanel		postAccountingtab;
	protected Checkbox		showZeroCal;

	private FinanceDetail financeDetail = null; // over handed per parameters
	private Object financeMainDialogCtrl = null;
	private  VASRecordingDialogCtrl vASRecordingdialogCtrl = null;   
	private  Object dialogCtrl = null;   
	private transient boolean 	accountingsExecuted;
	private boolean isNotFinanceProcess = false;
	private String moduleName;
	private long acSetID;
	private VASRecording vasRecording;

	private FinBasicDetailsCtrl  finBasicDetailsCtrl;
	private CollateralBasicDetailsCtrl  collateralBasicDetailsCtrl;
	protected Groupbox finBasicdetails;

	private transient BigDecimal disbCrSum = BigDecimal.ZERO;
	private transient BigDecimal disbDrSum = BigDecimal.ZERO;
	public List<ReturnDataSet> postingAccountSet = null;
	private PagedListService pagedListService;
	/**
	 * default constructor.<br>
	 */
	public AccountingDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

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
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_AccountingDetailDialog);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
		}

		if (arguments.containsKey("financeMainDialogCtrl")) {
			setFinanceMainDialogCtrl((Object) arguments.get("financeMainDialogCtrl"));
		}

		if (arguments.containsKey("acSetID")) {
			acSetID = (long) arguments.get("acSetID");
		}

		if (arguments.containsKey("isNotFinanceProcess")) {
			isNotFinanceProcess = (boolean) arguments.get("isNotFinanceProcess");
		}
		if (arguments.containsKey("moduleName")) {
			this.moduleName = (String) arguments.get("moduleName");
		}

		if (arguments.containsKey("vASRecording")) {
			this.vasRecording = (VASRecording) arguments.get("vASRecording");
		}

		// Common Dialog Controller
		if (arguments.containsKey("dialogCtrl")) {
			setDialogCtrl((Object)arguments.get("dialogCtrl"));
			this.btnPrintAccounting.setVisible(false);
			this.showZeroCal.setDisabled(true);
		}

		// Post Details Previous Accounting is Visible or not
		if (arguments.containsKey("postAccReq")) {
			boolean postAccReq = (boolean) arguments.get("postAccReq");
			if(!postAccReq){
				postAccountDetails.setVisible(false);
				postAccountingtab.setVisible(false);
			}
		}

		// append finance basic details 
		if (arguments.containsKey("finHeaderList")) {
			appendFinBasicDetails((ArrayList<Object> )arguments.get("finHeaderList"));
		}

		if (arguments.containsKey("enqModule")) {
			enqiryModule = (boolean) arguments.get("enqModule");
		}

		doShowDialog();
		logger.debug("Leaving " + event.toString());
	}

	// GUI operations

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

			//Seeting the visibility of the execute button.
			this.btnAccounting.setVisible(!enqiryModule);

			if(getDialogCtrl() != null){

				try {
					getDialogCtrl().getClass().getMethod("setAccountingDetailDialogCtrl", 
							this.getClass()).invoke(getDialogCtrl(), this);
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}

				// Accounting Posting Details 
				List<TransactionEntry> transactionEntries = AccountingConfigCache.getTransactionEntry(acSetID);
				if (transactionEntries != null && !transactionEntries.isEmpty()) {				
					boolean executed = false;

					try {
						executeAccounting(true);
						executed = true;
					} catch (Exception e) {
						if(e.getCause() instanceof AppException){
							wvea = e;
						}else if(e.getCause().getClass().equals(WrongValuesException.class)){
							wvea = e;
						}
						logger.error("Exception: ", e);
					}	

					if(!executed){
						doFillAccounting(transactionEntries);
					}
				}
			} else {
				FinanceMain main = getFinanceDetail().getFinScheduleData().getFinanceMain();

				try {
					getFinanceMainDialogCtrl().getClass().getMethod("setAccountingDetailDialogCtrl", 
							this.getClass()).invoke(getFinanceMainDialogCtrl(), this);
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}

				//Finance Accounting Posting Details & Commitment Disbursement Posting Details
				List<TransactionEntry> transactionEntries = AccountingConfigCache.getTransactionEntry(acSetID);
				if (transactionEntries != null && !transactionEntries.isEmpty()) {				
					boolean executed = false;
					if (!main.isNew() && 
							(getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() > 0 
									|| getFinanceDetail().getFinScheduleData().getOverdraftScheduleDetails().size()> 0) ) {

						if(!(main.getCustID() == 0 || main.getCustID() == Long.MIN_VALUE)){

							try {
								executeAccounting(true);
								executed = true;
							} catch (Exception e) {
								if(e.getCause() instanceof AppException){
									wvea = e;
								}else if(e.getCause().getClass().equals(WrongValuesException.class)){
									wvea = e;
								}
								logger.error("Exception: ", e);
							}	
						}
					}

					if(!executed){
						doFillAccounting(transactionEntries);
						doFillCmtAccounting(getFinanceDetail().getCmtFinanceEntries(), 0);
					}
				}
			} 
			getBorderLayoutHeight();
			this.listBoxFinAccountings.setHeight((this.borderLayoutHeight- 250) +"px");
			this.listBoxPostAccountings.setHeight((this.borderLayoutHeight- 280) +"px");
			this.window_AccountingDetailDialog.setHeight((this.borderLayoutHeight-80)+"px");

			if(wvea != null){
				if(wvea.getCause() instanceof AppException){
					throw wvea;
				}else if(wvea.getCause().getClass().equals(WrongValuesException.class)){
					throw wvea;
				} 
			}

		} catch (Exception e) {
			if(e.getCause() instanceof AppException){
				MessageUtil.showError((AppException)e.getCause());
			}else if(e.getCause().getClass().equals(WrongValuesException.class)){
				throw e;	
			}else{
				MessageUtil.showError(e);
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method to fill list box in Accounting Tab <br>
	 *  
	 * @param accountingSetEntries (List)
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void doFillAccounting(List<?> accountingSetEntries) {
		logger.debug("Entering");
		boolean isReturnDatasetList = false;
		setDisbCrSum(BigDecimal.ZERO);
		setDisbDrSum(BigDecimal.ZERO);
		List<ReturnDataSet> rdSetaccountingSetEntries = null ;
		int formatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());

		if(getDialogCtrl() == null){
			formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		}
		this.listBoxFinAccountings.getItems().clear();
		this.listBoxFinAccountings.setSizedByContent(true);
		if (accountingSetEntries != null && !accountingSetEntries.isEmpty()) {
			//Remove the ZeroAmount if showZeroCal is not checked
			if (accountingSetEntries.get(0) instanceof ReturnDataSet) {
				rdSetaccountingSetEntries = doRemovePostAmount((List<ReturnDataSet>)accountingSetEntries);
				isReturnDatasetList = true;
			}
			if(!isReturnDatasetList){
				for (int i = 0; i < accountingSetEntries.size(); i++) {

					Listitem item = new Listitem();
					Listcell lc;
					if (accountingSetEntries.get(i) instanceof TransactionEntry) {
						TransactionEntry entry = (TransactionEntry) accountingSetEntries.get(i);

						//Adding List Group to ListBox
						/*if(i == 0){
						Listgroup listgroup = new Listgroup(entry.getLovDescEventCodeName()+"-"+entry.getLovDescEventCodeDesc());
						this.listBoxFinAccountings.appendChild(listgroup);
					}*/

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
					} 
				}
			}else{
				if (rdSetaccountingSetEntries != null && !rdSetaccountingSetEntries.isEmpty()) {
					for(int j =0;j<rdSetaccountingSetEntries.size();j++){
						Listitem item = new Listitem();
						Listcell lc;
						if (rdSetaccountingSetEntries.get(j) instanceof ReturnDataSet) {
							ReturnDataSet entry = (ReturnDataSet) rdSetaccountingSetEntries.get(j);

							//Highlighting Failed Posting Details 
							String sClassStyle = "";
							if(StringUtils.isNotBlank(entry.getErrorId()) && !"0000".equals(StringUtils.trimToEmpty(entry.getErrorId()))){
								sClassStyle = "color:#FF0000;";
							}

							//Adding List Group to ListBox
							/*if(i == 0){
						Listgroup listgroup = new Listgroup(entry.getFinEvent() +"-"+ entry.getLovDescEventCodeName());
						this.listBoxFinAccountings.appendChild(listgroup);
					}*/

							Hbox hbox = new Hbox();
							Label label = new Label(PennantAppUtil.getlabelDesc(
									entry.getDrOrCr(), PennantStaticListUtil.getTranType()));
							label.setStyle(sClassStyle);
							hbox.appendChild(label);
							if (StringUtils.isNotBlank(entry.getPostStatus())) {
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
							lc = new Listcell(PennantApplicationUtil.amountFormate(amt,formatter));

							if (entry.getDrOrCr().equals(AccountConstants.TRANTYPE_CREDIT)) {
								setDisbCrSum(getDisbCrSum().add(amt));
							} else if (entry.getDrOrCr().equals(AccountConstants.TRANTYPE_DEBIT)) {
								setDisbDrSum(getDisbDrSum().add(amt));
							}

							lc.setStyle("font-weight:bold;text-align:right;");
							lc.setStyle(sClassStyle+"font-weight:bold;text-align:right;");
							lc.setParent(item);
							lc = new Listcell("0000".equals(StringUtils.trimToEmpty(entry.getErrorId())) ? "" : StringUtils.trimToEmpty(entry.getErrorId()));
							lc.setStyle("font-weight:bold;color:red;");
							lc.setTooltiptext(entry.getErrorMsg());
							lc.setParent(item);
							accountingsExecuted = true;
						}
						this.listBoxFinAccountings.appendChild(item);
					}
				}
			}
		}
		this.getLabel_AccountingDisbCrVal().setValue(PennantApplicationUtil.amountFormate(getDisbCrSum(), formatter));
		this.getLabel_AccountingDisbDrVal().setValue(PennantAppUtil.amountFormate(getDisbDrSum(), formatter));
		logger.debug("Leaving");
	}

	public void onCheck$showZeroCal(){
		logger.debug("Entering");
		if(vasRecording!=null){
			doFillAccounting(vasRecording.getReturnDataSetList());
		}else{
			doFillAccounting(getFinanceDetail().getReturnDataSetList());
		}
		logger.debug("Leaving");
	}

	public List<ReturnDataSet> doRemovePostAmount(List<ReturnDataSet> rdSetList){
		logger.debug("Entering");
		List<ReturnDataSet> accountingDetails = new ArrayList<ReturnDataSet>();
		if(!this.showZeroCal.isChecked()){
			for(ReturnDataSet rdSet:rdSetList){
				if(rdSet.getPostAmount().compareTo(BigDecimal.ZERO)!=0){
					accountingDetails.add(rdSet);
				}
			}
			return accountingDetails;
		}else{
			logger.debug("Leaving");
			return rdSetList;
		}
	}
	/**
	 * Method to fill  Post AccountSet in the PostAccounting tab<br>
	 *  
	 * @param postingAccountingset (List)
	 * 
	 */
	public void doFillPostAccountings(List<ReturnDataSet> postingAccountingset) {
		logger.debug("Entering");

		setDisbCrSum(BigDecimal.ZERO);
		setDisbDrSum(BigDecimal.ZERO);

		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		this.listBoxPostAccountings.getItems().clear();
		this.listBoxPostAccountings.setSizedByContent(true);
		if (postingAccountingset != null && !postingAccountingset.isEmpty()) {
			for (int i = 0; i < postingAccountingset.size(); i++) {

				Listitem item = new Listitem();
				Listcell lc;
				ReturnDataSet postAccountSet = (ReturnDataSet) postingAccountingset.get(i);
				//Highlighting Failed Posting Details 
				String sClassStyle = "";
				if(StringUtils.isNotBlank(postAccountSet.getErrorId()) && !"0000".equals(StringUtils.trimToEmpty(postAccountSet.getErrorId()))){
					sClassStyle = "color:#FF0000;";
				}
				Hbox hbox = new Hbox();
				Label label = new Label(PennantAppUtil.getlabelDesc(
						postAccountSet.getDrOrCr(), PennantStaticListUtil.getTranType()));
				label.setStyle(sClassStyle);
				hbox.appendChild(label);
				lc = new Listcell();
				lc.setStyle(sClassStyle);
				lc.appendChild(hbox);
				lc.setParent(item);
				lc = new Listcell(postAccountSet.getTranDesc());
				lc.setStyle(sClassStyle);
				lc.setParent(item);
				if(postAccountSet.isShadowPosting()){
					lc = new Listcell("Shadow");
					lc.setStyle(sClassStyle);
					lc.setParent(item);
					lc = new Listcell("Shadow");
					lc.setStyle(sClassStyle);
					lc.setParent(item);
				}else{
					lc = new Listcell(postAccountSet.getTranCode());
					lc.setStyle(sClassStyle);
					lc.setParent(item);
					lc = new Listcell(postAccountSet.getRevTranCode());
					lc.setStyle(sClassStyle);
					lc.setParent(item);
				}
				lc = new Listcell(PennantApplicationUtil.formatAccountNumber(postAccountSet.getAccount()));
				lc.setStyle("font-weight:bold;");
				lc.setStyle(sClassStyle);
				lc.setParent(item);	

				lc = new Listcell(postAccountSet.getAcCcy());
				lc.setParent(item);

				BigDecimal amt = postAccountSet.getPostAmount()!=null?postAccountSet.getPostAmount(): BigDecimal.ZERO;

				if(postAccountSet.getAcCcy().equals(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy())){
					lc = new Listcell(PennantApplicationUtil.amountFormate(amt,formatter));

					if (postAccountSet.getDrOrCr().equals(AccountConstants.TRANTYPE_CREDIT)) {
						setDisbCrSum(getDisbCrSum().add(amt));
					} else if (postAccountSet.getDrOrCr().equals(AccountConstants.TRANTYPE_DEBIT)) {
						setDisbDrSum(getDisbDrSum().add(amt));
					}
				}else{
					lc = new Listcell(PennantApplicationUtil.amountFormate(amt,postAccountSet.getFormatter()));
				}
				lc.setStyle("font-weight:bold;text-align:right;");
				lc.setStyle(sClassStyle+"font-weight:bold;text-align:right;");
				lc.setParent(item);
				lc = new Listcell("0000".equals(StringUtils.trimToEmpty(postAccountSet.getErrorId())) ? "" : StringUtils.trimToEmpty(postAccountSet.getErrorId()));
				lc.setStyle("font-weight:bold;color:red;");
				lc.setTooltiptext(postAccountSet.getErrorMsg());
				lc.setParent(item);

				this.listBoxPostAccountings.appendChild(item);
			}

			this.getLabel_PostAccountingDisbCrVal().setValue(PennantApplicationUtil.amountFormate(getDisbCrSum(), formatter));
			this.getLabel_PostAccountingDisbDrVal().setValue(PennantAppUtil.amountFormate(getDisbDrSum(), formatter));
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

		BigDecimal cmtCrEntry = BigDecimal.ZERO;
		BigDecimal cmtDrEntry = BigDecimal.ZERO;

		this.listBoxFinAccountings.getItems().clear();
		this.listBoxFinAccountings.setSizedByContent(true);
		boolean isOverdraft = false;

		if(StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory())){
			isOverdraft = true;
		}

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
					if (StringUtils.isNotBlank(entry.getPostStatus())) {
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

					if(isOverdraft){
						if (entry.getDrOrCr().equals(AccountConstants.TRANTYPE_CREDIT)) {
							cmtCrEntry = cmtCrEntry.add(amt);
						}else if (entry.getDrOrCr().equals(AccountConstants.TRANTYPE_DEBIT)) {
							cmtDrEntry = cmtDrEntry.add(amt);
						}
						accountingsExecuted = true;
					}
					lc = new Listcell(PennantAppUtil.amountFormate(amt,formatter));
					lc.setStyle("font-weight:bold;text-align:right;");
					lc.setParent(item);
					lc = new Listcell("0000".equals(StringUtils.trimToEmpty(entry.getErrorId())) ? "" : entry.getErrorId());
					lc.setStyle("font-weight:bold;color:red;");
					lc.setTooltiptext(entry.getErrorMsg());
					lc.setParent(item);
				}
				listBoxFinAccountings.appendChild(item);
			}
			if(isOverdraft){
				this.getLabel_AccountingDisbCrVal().setValue(PennantApplicationUtil.amountFormate(cmtCrEntry, formatter));
				this.getLabel_AccountingDisbDrVal().setValue(PennantAppUtil.amountFormate(cmtDrEntry, formatter));
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
		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

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
					if (StringUtils.isNotEmpty(entry.getPostStatus())) {
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
					lc = new Listcell("0000".equals(StringUtils.trimToEmpty(entry.getErrorId())) ? "" : entry.getErrorId());
					lc.setStyle("font-weight:bold;color:red;");
					lc.setTooltiptext(entry.getErrorMsg());
					lc.setParent(item);
				}
				listBoxFinAccountings.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}


	// New Button & Double Click Events for Finance Contributor List

	/**
	 * Method for Executing Accounting Details List
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnAccounting(Event event) throws Exception { 
		logger.debug("Entering" + event.toString());
		try {
			executeAccounting(true);
		} catch (Exception e) {
			if(e.getCause() instanceof AppException){
				MessageUtil.showError((AppException)e.getCause());
			}else{
				MessageUtil.showError(e);
			}
		}
		
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Executing Accounting Details
	 * @throws Exception 
	 */
	private void executeAccounting(boolean onAction) throws Exception{
		logger.debug("Entering");

		if(getDialogCtrl() != null){
			try {
				getDialogCtrl().getClass().getMethod("executeAccounting").invoke(getDialogCtrl());				
			} catch (Exception e) {
				logger.error("Exception: ", e);
				if(e.getCause().getClass().equals(WrongValuesException.class)){
					throw e;	
				}else if(e.getCause() instanceof AppException){
					throw e;	
				}
			}	
		}else{
			try {
				getFinanceMainDialogCtrl().getClass().getMethod("onExecuteAccountingDetail",
						Boolean.class).invoke(getFinanceMainDialogCtrl(), onAction);				
			} catch (Exception e) {
				logger.error("Exception: ", e);
				if(e.getCause().getClass().equals(WrongValuesException.class)){
					throw e;	
				}else if(e.getCause() instanceof AppException){
					throw e;	
				}
			}	
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
		List<Object> list        = null;
		FinanceMain  financeMain = null ;
		List<ReturnDataSet> rdList ;
		int formatter;
		if(vASRecordingdialogCtrl!=null){
			rdList = vasRecording.getReturnDataSetList();
			formatter = 2;
		}else{
			rdList = getFinanceDetail().getReturnDataSetList();
			financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());
		}

		if (accountingsExecuted) {
			list = new ArrayList<Object>();
			List<TransactionDetail> accountingDetails = new ArrayList<TransactionDetail>();
			for (ReturnDataSet dataSet :rdList) {
				TransactionDetail detail = new TransactionDetail();
				detail.setEventCode(dataSet.getFinEvent());
				detail.setEventDesc(dataSet.getLovDescEventCodeName());
				detail.setTranType(dataSet.getDrOrCr().equals(AccountConstants.TRANTYPE_CREDIT) ? "Credit" : "Debit");
				detail.setTransactionCode(dataSet.getTranCode());
				detail.setTransDesc(dataSet.getTranDesc());
				detail.setCcy(dataSet.getAcCcy());
				detail.setAccount(PennantApplicationUtil.formatAccountNumber(dataSet.getAccount()));
				detail.setPostAmount(PennantAppUtil.amountFormate(dataSet.getPostAmount(),dataSet.getFormatter() == 0 ?  
						formatter : dataSet.getFormatter()));
				accountingDetails.add(detail);
			}

			Window window= (Window) this.window_AccountingDetailDialog.getParent().getParent().getParent().getParent().getParent().getParent().getParent();
			if(!accountingDetails.isEmpty()){
				list.add(accountingDetails);
			}

			if(vASRecordingdialogCtrl!=null){
				ReportGenerationUtil.generateReport("FINENQ_AccountingDetail",vasRecording, 
						list, true, 1, usrName,window);
			}else{
				ReportGenerationUtil.generateReport("FINENQ_AccountingDetail",financeMain, 
						list, true, 1, usrName,window);
			}
		} else {
			MessageUtil.showError(Labels.getLabel("btnPrintAccounting.Error_Message"));
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails(ArrayList<Object> finHeaderList) {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this );
			map.put("finHeaderList", finHeaderList );
			map.put("moduleName", moduleName);
			if(isNotFinanceProcess){
				Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CollateralBasicDetails.zul",this.finBasicdetails, map);
			}else {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul",this.finBasicdetails, map);
			}
		} catch (Exception e) {
			logger.debug(e);
		}

	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		if(isNotFinanceProcess){
			getCollateralBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
		}else{
			getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
		}
	}

	/*
	 * Method to Get PostingAccount Details
	 */

	public List<ReturnDataSet> getPostingAccount(String finReference){
		logger.debug("Entering");

		List<ReturnDataSet> postingAccount = new ArrayList<ReturnDataSet>();
		JdbcSearchObject<ReturnDataSet> searchObject=new JdbcSearchObject<ReturnDataSet>(ReturnDataSet.class);
		searchObject.addTabelName("Postings_view");
		searchObject.addFilterEqual("finreference", finReference);
		List<ReturnDataSet>  postings = pagedListService.getBySearchObject(searchObject);
		if(postings!=null && !postings.isEmpty()){
			return postings;
		}

		logger.debug("Leaving");
		return postingAccount ;

	}

	public void onSelect$postAccountDetails(Event event){
		logger.debug("Entering");
		if(postingAccountSet!=null && !postingAccountSet.isEmpty()){
			return;
		}else{
			List<ReturnDataSet> postingaccount = getPostingAccount(getFinanceDetail().getFinScheduleData().getFinReference());
			if(postingaccount!= null && !postingaccount.isEmpty()){
				doFillPostAccountings(postingaccount);
				postingAccountSet = postingaccount;
			}
		}
		logger.debug("Leaving");

	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}
	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl){
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public Label getLabel_PostAccountingDisbCrVal() {
		return label_PostAccountingDisbCrVal;
	}
	public void setLabel_PostAccountingDisbCrVal(
			Label label_PostAccountingDisbCrVal) {
		this.label_PostAccountingDisbCrVal = label_PostAccountingDisbCrVal;
	}

	public Label getLabel_PostAccountingDisbDrVal() {
		return label_PostAccountingDisbDrVal;
	}
	public void setLabel_PostAccountingDisbDrVal(
			Label label_PostAccountingDisbDrVal) {
		this.label_PostAccountingDisbDrVal = label_PostAccountingDisbDrVal;
	}

	public CollateralBasicDetailsCtrl getCollateralBasicDetailsCtrl() {
		return collateralBasicDetailsCtrl;
	}

	public void setCollateralBasicDetailsCtrl(CollateralBasicDetailsCtrl collateralBasicDetailsCtrl) {
		this.collateralBasicDetailsCtrl = collateralBasicDetailsCtrl;
	}

	public Object getDialogCtrl() {
		return dialogCtrl;
	}
	public void setDialogCtrl(Object dialogCtrl) {
		this.dialogCtrl = dialogCtrl;
	}

}
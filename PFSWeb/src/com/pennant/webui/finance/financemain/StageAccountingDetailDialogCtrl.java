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
 * FileName    		:  FinanceMainDialogCtrl.java                                           * 	  
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
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Window;

import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/financeMain/FinanceMainDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class StageAccountingDetailDialogCtrl extends GFCBaseListCtrl<ReturnDataSet> implements Serializable {

	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(StageAccountingDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_StageAccountingDetailsDialog; 				// autoWired

	//Finance Schedule Details Tab

	protected Label 		stageAccounting_finType; 							// autoWired
	protected Label 		stageAccounting_finCcy; 							// autoWired
	protected Label 		stageAccounting_scheduleMethod; 					// autoWired
	protected Label 		stageAccounting_profitDaysBasis; 					// autoWired
	protected Label 		stageAccounting_finReference; 						// autoWired
	protected Label 		stageAccounting_grcEndDate; 						// autoWired	
	
	// Stage Accounting Details Tab

	protected Button 		btnStageAccounting; 					// autoWired
	protected Label 		label_StageAccountingDisbCrVal; 		// autoWired
	protected Label 		label_StageAccountingDisbDrVal; 		// autoWired
	protected Label 		label_StageAccountingDisbSummaryVal; 	// autoWired
	protected Listbox 		listBoxFinStageAccountings;				// autoWired

	protected Label 		recordStatus; 							// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
 
	protected transient BigDecimal stageDisbCrSum = BigDecimal.ZERO;
	protected transient BigDecimal stageDisbDrSum = BigDecimal.ZERO;
  
	// not auto wired variables
	private FinanceDetail 			financeDetail = null; 			
	private FinScheduleData 		finScheduleData = null;
	private FinanceMain 			financeMain = null;
	private Object 					financeMainDialogCtrl = null;
	
	private AEAmountCodes 			amountCodes; 					// over handed per parameters
	protected boolean 	stageAccountingsExecuted = false;
 	private List<ValueLabel> profitDaysBasisList = new ArrayList<ValueLabel>();
	private List<ValueLabel> schMethodList = new ArrayList<ValueLabel>();
	
	//Bean Setters  by application Context
 	private AccountEngineExecution engineExecution;
 	private String roleCode = "";
	
	/**
	 * default constructor.<br>
	 */
	public StageAccountingDetailDialogCtrl() {
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
	public void onCreate$window_StageAccountingDetailsDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

	 
		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("financeDetail")) {
			setFinanceDetail((FinanceDetail) args.get("financeDetail"));
 		}
		
		if (args.containsKey("financeMainDialogCtrl")) {
			this.financeMainDialogCtrl = (Object) args.get("financeMainDialogCtrl");
		}
		
		if (args.containsKey("roleCode")) {
			this.roleCode = (String) args.get("roleCode");
		}
		if (args.containsKey("profitDaysBasisList")) {
			profitDaysBasisList = (List<ValueLabel>) args.get("profitDaysBasisList");
		}
		
		if (args.containsKey("schMethodList")) {
			schMethodList = (List<ValueLabel>) args.get("schMethodList");
		}
		
		getBorderLayoutHeight();
		this.listBoxFinStageAccountings.setHeight(this.borderLayoutHeight- 220+"px");
		this.window_StageAccountingDetailsDialog.setHeight(this.borderLayoutHeight-80+"px");
 
		// Calling method to add asset, checklist and additionaldetails tabs
 		doShowDialog(this.financeDetail);
 
 		logger.debug("Leaving " + event.toString());
	}
 
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain
	 *            financeMain
	 * @throws ParseException 
	 */
	public void doWriteBeanToComponents() throws ParseException {
		logger.debug("Entering");
		
		// Eligibility Details Tab 
 		this.stageAccounting_finType.setValue(StringUtils.trimToEmpty(getFinanceMain().getLovDescFinTypeName()));
		this.stageAccounting_finCcy.setValue(StringUtils.trimToEmpty(getFinanceMain().getLovDescFinCcyName()));
		this.stageAccounting_scheduleMethod.setValue(PennantAppUtil.getlabelDesc(getFinanceMain().getScheduleMethod(), schMethodList));
		this.stageAccounting_profitDaysBasis.setValue(PennantAppUtil.getlabelDesc(getFinanceMain().getProfitDaysBasis(), profitDaysBasisList));
		this.stageAccounting_finReference.setValue(StringUtils.trimToEmpty(getFinanceMain().getFinReference()));
		this.stageAccounting_grcEndDate.setValue(DateUtility.formatDate(getFinanceMain().getGrcPeriodEndDate(), PennantConstants.dateFormate)) ;
 		
		dofillStageAccountingSetbox(getFinanceDetail().getStageTransactionEntries());

 		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinanceDetail afinanceDetail) throws InterruptedException {
		logger.debug("Entering");
		try {
			
			// fill the components with the data
			doWriteBeanToComponents();
			
			try {
				getFinanceMainDialogCtrl().getClass().getMethod("setStageAccountingDetailDialogCtrl", 
						this.getClass()).invoke(getFinanceMainDialogCtrl(), this);
			} catch (Exception e) {
				logger.error(e);
			}

		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}
 

	/**
	 * Method for Filling Stage Accounting Details List
	 * @param accountingSetEntries
	 * @param listbox
	 */
	public void dofillStageAccountingSetbox(List<?> accountingSetEntries) {
		logger.debug("Entering");

		stageDisbCrSum = BigDecimal.ZERO;
		stageDisbDrSum = BigDecimal.ZERO;

		int formatter = getFinanceMain().getLovDescFinFormatter();

		this.listBoxFinStageAccountings.setSizedByContent(true);
		this.listBoxFinStageAccountings.getItems().clear();
		if (accountingSetEntries != null) {
			for (int i = 0; i < accountingSetEntries.size(); i++) {
				Listitem item = new Listitem();
				Listcell lc;
				if (accountingSetEntries.get(i) instanceof TransactionEntry) {
					TransactionEntry entry = (TransactionEntry) accountingSetEntries.get(i);
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
				} else if (accountingSetEntries.get(i) instanceof ReturnDataSet) {
					ReturnDataSet entry = (ReturnDataSet) accountingSetEntries.get(i);
					Hbox hbox = new Hbox();
					Label label = new Label(PennantAppUtil.getlabelDesc(
							entry.getDrOrCr(), PennantStaticListUtil.getTranType()));
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
					lc = new Listcell(entry.getAccountType());
					lc.setParent(item);
					lc = new Listcell(PennantApplicationUtil.formatAccountNumber(entry.getAccount()));
					lc.setStyle("font-weight:bold;");
					lc.setParent(item);
					BigDecimal amt = new BigDecimal(entry.getPostAmount().toString()).setScale(0, RoundingMode.FLOOR);
					lc = new Listcell(PennantAppUtil.amountFormate(amt,formatter));
					
					if (entry.getDrOrCr().equals(PennantConstants.CREDIT)) {
						stageDisbCrSum = stageDisbCrSum.add(amt);
					} else if (entry.getDrOrCr().equals(PennantConstants.DEBIT)) {
						stageDisbDrSum = stageDisbDrSum.add(amt);
					}
					
					lc.setStyle("font-weight:bold;text-align:right;");
					lc.setParent(item);
					lc = new Listcell(entry.getErrorId().equals("0000") ? "" : entry.getErrorId());
					lc.setStyle("font-weight:bold;color:red;");
					lc.setTooltiptext(entry.getErrorMsg());
					lc.setParent(item);
					stageAccountingsExecuted = true;
				}
				this.listBoxFinStageAccountings.appendChild(item);
			}

			this.label_StageAccountingDisbCrVal.setValue(PennantAppUtil.amountFormate(stageDisbCrSum, formatter));
			this.label_StageAccountingDisbDrVal.setValue(PennantAppUtil.amountFormate(stageDisbDrSum, formatter));
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Executing Stage Accounting Details List
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnStageAccounting(Event event) throws Exception { 
		logger.debug("Entering" + event.toString());
 		validateFinanceDetail();

		doSetStageAccounting(getMainFinanceDetail());
		logger.debug("Leaving" + event.toString());
	}
	
	public void doSetStageAccounting(FinanceDetail financeDetail) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException{
		setFinanceDetail(financeDetail);
		DataSet dataSet = AEAmounts.createDataSet(getFinanceMain(), "STAGE",
				getFinanceMain().getFinStartDate(), getFinanceMain().getFinStartDate());
 	 
		amountCodes = AEAmounts.procAEAmounts( getFinanceMain(),
				getFinScheduleData().getFinanceScheduleDetails(),
				new FinanceProfitDetail(),  getFinanceMain().getFinStartDate());
		setAmountCodes(amountCodes);

		List<ReturnDataSet> accountingSetEntries = getEngineExecution().getStageExecResults(dataSet, 
				getAmountCodes(), "N", roleCode, null, getFinScheduleData().getFinanceType(),getFinanceDetail().getPremiumDetail());

		getFinanceDetail().setStageAccountingList(accountingSetEntries);
		dofillStageAccountingSetbox(accountingSetEntries);
 	}
	
	/**
	 * Update FinanceDetail with the values from the Main Ctrl 
	 */
	public FinanceDetail getMainFinanceDetail(){
		try {
			return (FinanceDetail) getFinanceMainDialogCtrl().getClass().getMethod("getFinanceDetail").invoke(getFinanceMainDialogCtrl());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.financeDetail;	
	}
	
	/**Validate basic Finance Details **/
	public void validateFinanceDetail() throws Exception{
		try {
			getFinanceMainDialogCtrl().getClass().getMethod("onExecuteEligibilityDetail").invoke(getFinanceMainDialogCtrl());
		} catch (Exception e) {
			if(e.getCause().getClass().equals(WrongValuesException.class)){
				throw e;	
			}
			e.printStackTrace();
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
		setFinScheduleData(financeDetail.getFinScheduleData());
		setFinanceMain(this.finScheduleData.getFinanceMain());
	}

	public AEAmountCodes getAmountCodes() {
		return amountCodes;
	}
	public void setAmountCodes(AEAmountCodes amountCodes) {
		this.amountCodes = amountCodes;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}
	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}
	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}
	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}
	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}
 	
}
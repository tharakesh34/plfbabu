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
 * FileName    		:  ScheduleDetailDialogCtrl.java                                                   * 	  
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
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/ScheduleDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class DPScheduleDetailDialogCtrl extends GFCBaseListCtrl<FinanceScheduleDetail> implements Serializable {

	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(DPScheduleDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_DPScheduleDetailDialog; 			// autoWired
	protected Listbox 		listBoxSchedule; 						// autoWired
	protected Borderlayout  borderlayoutScheduleDetail;				// autoWired
	
	//Finance Schedule Details Tab
	protected Grid 			grid_effRateOfReturn; 					// autoWired
	
	protected Label			schdl_finType;
	protected Label			schdl_finReference;
	protected Label			schdl_finCcy;
	protected Label			schdl_profitDaysBasis;
	protected Label			schdl_noOfTerms;
	protected Label			schdl_grcEndDate;
	protected Label			schdl_startDate;
	protected Label			schdl_maturityDate;
	protected Decimalbox	schdl_purchasePrice;
	protected Decimalbox	schdl_otherExp;
	protected Decimalbox	schdl_totalCost;
	protected Decimalbox	schdl_totalPft;
	protected Decimalbox	schdl_contractPrice;
	protected Label 		schdl_BankShare;
	protected Label 		schdl_NonBankShare;
	
	public 	  Label 		effectiveRateOfReturn; 					
	
	protected Label			label_ScheduleDetailDialog_FinType;
	protected Label			label_ScheduleDetailDialog_FinReference;
	protected Label			label_ScheduleDetailDialog_FinCcy;
	protected Label			label_ScheduleDetailDialog_ProfitDaysBasis;
	protected Label			label_ScheduleDetailDialog_NoOfTerms;
	protected Label			label_ScheduleDetailDialog_GrcEndDate;
	protected Label			label_ScheduleDetailDialog_StartDate;
	protected Label			label_ScheduleDetailDialog_MaturityDate;
	protected Label			label_ScheduleDetailDialog_PurchasePrice;
	
	protected Listheader    listheader_ScheduleDetailDialog_Date;
	protected Listheader    listheader_ScheduleDetailDialog_CalProfit;
	protected Listheader    listheader_ScheduleDetailDialog_Principal;
	protected Listheader    listheader_ScheduleDetailDialog_Total;
	protected Listheader    listheader_ScheduleDetailDialog_ScheduleEndBal;
	
	private Object financeMainDialogCtrl = null;
	private FinScheduleData finScheduleData = null;
	private FinanceDetail financeDetail = null;
	private FinScheduleListItemRenderer finRender;
	private FinanceDetailService financeDetailService;
	private List<ValueLabel> profitDaysBasisList = null;
	
	/**
	 * default constructor.<br>
	 */
	public DPScheduleDetailDialogCtrl() {
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
	public void onCreate$window_DPScheduleDetailDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering " + event.toString());
		
		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) args.get("financeDetail");
			setFinanceDetail(financeDetail);
			setFinScheduleData(financeDetail.getFinScheduleData());
		}
		
		if (args.containsKey("profitDaysBasisList")) {
			profitDaysBasisList = (List<ValueLabel>) args.get("profitDaysBasisList");
		}
		
		if (args.containsKey("financeMainDialogCtrl")) {
			this.financeMainDialogCtrl = (Object) args.get("financeMainDialogCtrl");
		}
		
		doSetLabels();
		doShowDialog();
		
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * 
	 * Set the Labels for the ListHeader and Basic Details based oon the Finance Types
	 * right is only a string. <br>
	 */
	private void doSetLabels() {
		logger.debug("Entering");
		String product = getFinScheduleData().getFinanceType().getFinCategory();
		
		FinanceMain financeMain = getFinScheduleData().getFinanceMain();		
		this.schdl_finType.setValue(financeMain.getFinType() +" - "+financeMain.getLovDescFinTypeName());
		this.schdl_finCcy.setValue(financeMain.getFinCcy() +" - "+financeMain.getLovDescFinCcyName());
		this.schdl_profitDaysBasis.setValue(PennantAppUtil.getlabelDesc(financeMain.getProfitDaysBasis(), profitDaysBasisList));
		
		String productType = (product.substring(0, 1)).toUpperCase()+(product.substring(1)).toLowerCase();
		label_ScheduleDetailDialog_FinType.setValue(Labels.getLabel("label_" + productType + "_ScheduleDetailDialog_FinType.value"));
		label_ScheduleDetailDialog_FinReference.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_FinReference.value"));
		label_ScheduleDetailDialog_FinCcy.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_FinCcy.value"));
		label_ScheduleDetailDialog_ProfitDaysBasis.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_ProfitDaysBasis.value"));
		label_ScheduleDetailDialog_NoOfTerms.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_NumberOfTerms.value"));
		label_ScheduleDetailDialog_GrcEndDate.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_FinGracePeriodEndDate.value"));
		label_ScheduleDetailDialog_StartDate.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_FinStartDate.value"));
		label_ScheduleDetailDialog_MaturityDate.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_FinMaturityDate.value"));
		label_ScheduleDetailDialog_PurchasePrice.setValue(Labels.getLabel("label_" + productType +"_ScheduleDetailDialog_PurchasePrice.value"));

		listheader_ScheduleDetailDialog_Date.setLabel(Labels.getLabel("listheader_" + productType +"_ScheduleDetailDialog_Date"));
		listheader_ScheduleDetailDialog_CalProfit.setLabel(Labels.getLabel("listheader_" + productType +"_ScheduleDetailDialog_SchProfit"));
		listheader_ScheduleDetailDialog_Principal.setLabel(Labels.getLabel("listheader_" + productType +"_ScheduleDetailDialog_Principal"));
		listheader_ScheduleDetailDialog_Total.setLabel(Labels.getLabel("listheader_" + productType +"_ScheduleDetailDialog_Total"));
		listheader_ScheduleDetailDialog_ScheduleEndBal.setLabel(Labels.getLabel("listheader_" + productType +"_ScheduleDetailDialog_ScheduleEndBal"));
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws InterruptedException
	 */
	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// fill the components with the data
			this.listBoxSchedule.getItems().clear();
			
			FinScheduleData scheduleData = ScheduleCalculator.getDownPaySchd(finScheduleData);
			doFillScheduleList(scheduleData);
			
			getBorderLayoutHeight();
			this.window_DPScheduleDetailDialog.setHeight(calculateBorderLayoutHeight()-15+"px");
			this.listBoxSchedule.setHeight(calculateBorderLayoutHeight()-160+"px");
			setDialog(this.window_DPScheduleDetailDialog);
			this.window_DPScheduleDetailDialog.doModal();
		}catch(Exception e){
			e.printStackTrace();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to fill the Schedule Listbox with provided generated schedule.
	 * 
	 * @param FinScheduleData
	 *            (aFinSchData)
	 */
	public void doFillScheduleList(FinScheduleData aFinSchData) {
		logger.debug("Entering");
		
		FinanceMain financeMain = aFinSchData.getFinanceMain();
		int ccyFormatter = financeMain.getLovDescFinFormatter();
		this.schdl_finReference.setValue(financeMain.getFinReference());
		this.schdl_noOfTerms.setValue(String.valueOf(financeMain.getNumberOfTerms() + financeMain.getGraceTerms()));
		this.schdl_grcEndDate.setValue(DateUtility.formatDate(financeMain.getGrcPeriodEndDate(), PennantConstants.dateFormate));
		this.schdl_startDate.setValue(DateUtility.formatDate(financeMain.getFinStartDate(), PennantConstants.dateFormate));
		this.schdl_maturityDate.setValue(DateUtility.formatDate(financeMain.getMaturityDate(), PennantConstants.dateFormate));
		this.schdl_purchasePrice.setValue(PennantAppUtil.formateAmount(financeMain.getFinAmount(), ccyFormatter));
		
		// Down Payment Schedule List Items
		int dpSchdSize = aFinSchData.getFinanceScheduleDetails().size();
		if (aFinSchData != null && dpSchdSize > 0) {

			finRender = new FinScheduleListItemRenderer();
			for (FinanceScheduleDetail aScheduleDetail : aFinSchData.getFinanceScheduleDetails()) {
				finRender.doFillDPSchedule(this.listBoxSchedule, aScheduleDetail, aFinSchData.getFinanceMain().getLovDescFinFormatter());
			}
		}
		logger.debug("Leaving");
	}
	
	/**
	 * when the "btnPrintSchedule" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnPrintSchedule(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		logger.debug("Leaving" + event.toString());
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}
	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}
	
	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}
	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}
	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
}

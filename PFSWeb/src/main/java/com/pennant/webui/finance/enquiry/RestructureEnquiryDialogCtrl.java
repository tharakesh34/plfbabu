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
 * FileName    		:  RestructureEnquiryDialogCtrl.java													*                           
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  						*
 * Creation Date    :  25-03-2021															*
 *                                                                  						*
 * Modified Date    :  															*
 *                                                                  						*
 * Description 		:												 						*                                 
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 25-03-2021       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.webui.finance.enquiry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.RestructureDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class RestructureEnquiryDialogCtrl extends GFCBaseCtrl<RestructureDetail> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(RestructureEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_RestructureEnquiryDialog;
	protected Listbox listBoxRestructureDetail;
	protected Datebox restructureDate;
	protected Datebox restructureAppDate;
	protected Intbox emiHldPeriod;
	protected Intbox priHldPeriod;
	protected Intbox emiPeriod;
	protected Combobox restructureType;
	protected Combobox restructureReason;
	protected Checkbox tenorChange;
	protected Checkbox emiRecal;
	protected Intbox totNoOfRestructure;
	protected Combobox recalculationType;
	protected Textbox serviceRequestNo;
	protected Textbox remark;
	protected Intbox oldBucket;
	protected Intbox newBucket;
	protected Intbox diffBucket;
	protected Intbox oldDpd;
	protected Intbox newDpd;
	protected Intbox diffDpd;
	protected Decimalbox oldEmiOs;
	protected Decimalbox newEmiOs;
	protected Decimalbox diffEmiOs;
	protected Intbox oldBalTenure;
	protected Intbox newBalTenure;
	protected Intbox diffBalTenure;
	protected Datebox oldMaturity;
	protected Datebox newMaturity;
	protected Textbox diffMaturity;
	protected Datebox lastBilledDate;
	protected Intbox lastBilledInstNo;
	protected Decimalbox actLoanAmount;
	protected Intbox oldTenure;
	protected Intbox newTenure;
	protected Intbox diffTenure;
	protected Decimalbox oldInterest;
	protected Decimalbox newInterest;
	protected Decimalbox diffInterest;
	protected Decimalbox oldCpzInterest;
	protected Decimalbox newCpzInterest;
	protected Decimalbox diffCpzInterest;
	protected Intbox oldMaxUnplannedEmi;
	protected Intbox newMaxUnplannedEmi;
	protected Intbox diffMaxUnplannedEmi;
	protected Intbox oldAvailedUnplanEmi;
	protected Intbox newAvailedUnplanEmi;
	protected Intbox diffAvailedUnplanEmi;
	protected Decimalbox oldFinalEmi;
	protected Decimalbox newFinalEmi;
	protected Decimalbox diffFinalEmi;
	protected Datebox emiHldStartDate;
	protected Datebox emiHldEndDate;
	protected Datebox priHldStartDate;
	protected Datebox priHldEndDate;
	protected Decimalbox oldPOsAmount;
	protected Decimalbox newPOsAmount;
	protected Decimalbox diffPOsAmount;
	protected Decimalbox oldEmiOverdue;
	protected Decimalbox newEmiOverdue;
	protected Decimalbox diffEmiOverdue;
	protected Decimalbox bounceCharge;
	protected Decimalbox oldPenaltyAmount;
	protected Decimalbox newPenaltyAmount;
	protected Decimalbox diffPenaltyAmount;
	protected Decimalbox otherCharge;
	protected Decimalbox restructureCharge;
	protected Decimalbox repayProfitRate;
	protected Decimalbox finCurrAssetValue;
	protected Intbox oldExtOdDays;
	protected Intbox newExtOdDays;
	protected Intbox diffExtOdDays;
	protected Textbox rstTypeCode;
	protected Textbox rstTypeDesc;
	private Tabpanel tabPanel_dialogWindow;
	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private FinanceDetail financeDetail = null;

	/**
	 * default constructor.<br>
	 */
	public RestructureEnquiryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_RestructureEnquiryDialog(ForwardEvent event) throws Exception {
		logger.debug(Literal.ENTERING + event.toString());

		// Set the page level components.
		setPageComponents(window_RestructureEnquiryDialog);

		if (event.getTarget().getParent().getParent() != null) {
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}

		if (arguments.containsKey("financeDetail")) {
			FinanceDetail financeDetail = (FinanceDetail) arguments.get("financeDetail");
			setFinanceDetail(financeDetail);
		}

		if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
			this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments
					.get("financeEnquiryHeaderDialogCtrl");
		}

		doShowDialog();

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws InterruptedException
	 */
	public void doShowDialog() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		try {

			doFillData();

			if (tabPanel_dialogWindow != null) {

				this.window_RestructureEnquiryDialog.setBorder("none");
				this.window_RestructureEnquiryDialog.setTitle("");

				getBorderLayoutHeight();
				int headerRowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount()
						* 20;

				this.window_RestructureEnquiryDialog.setHeight(this.borderLayoutHeight - headerRowsHeight + "px");
				tabPanel_dialogWindow.appendChild(this.window_RestructureEnquiryDialog);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Filling Restructure Details
	 */
	private void doFillData() {
		logger.debug(Literal.ENTERING);
		RestructureDetail rstDetail = financeDetail.getFinScheduleData().getRestructureDetail();
		this.listBoxRestructureDetail.getItems().clear();

		// Filling ListBox Data
		Listitem item = new Listitem();
		Listcell lc;

		lc = new Listcell(DateUtil.formatToLongDate(rstDetail.getAppDate()));
		lc.setParent(item);
		lc = new Listcell(rstDetail.getRestructureReason());
		lc.setParent(item);
		lc = new Listcell(rstDetail.getRstTypeDesc());
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(rstDetail.getRestructureDate()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(rstDetail.getEmiHldPeriod()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(rstDetail.getPriHldPeriod()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(rstDetail.getEmiPeriods()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(rstDetail.getTotNoOfRestructure()));
		lc.setParent(item);
		lc = new Listcell(rstDetail.getRecalculationType());
		lc.setParent(item);
		lc = new Listcell(String.valueOf(rstDetail.isTenorChange()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(rstDetail.isEmiRecal()));
		lc.setParent(item);

		this.listBoxRestructureDetail.appendChild(item);

		// Filling Amount GroupBox Data
		this.oldBucket.setValue(rstDetail.getOldBucket());
		this.newBucket.setValue(rstDetail.getNewBucket());
		this.diffBucket.setValue(Math.abs(rstDetail.getNewBucket() - rstDetail.getOldBucket()));

		this.oldDpd.setValue(rstDetail.getOldDpd());
		this.newDpd.setValue(rstDetail.getNewDpd());
		this.diffDpd.setValue(Math.abs(rstDetail.getNewDpd() - rstDetail.getOldDpd()));

		this.oldEmiOs.setValue(
				PennantApplicationUtil.formateAmount(rstDetail.getOldEmiOs(), PennantConstants.defaultCCYDecPos));
		this.newEmiOs.setValue(
				PennantApplicationUtil.formateAmount(rstDetail.getNewEmiOs(), PennantConstants.defaultCCYDecPos));
		this.diffEmiOs.setValue(PennantApplicationUtil.formateAmount(
				rstDetail.getNewEmiOs().subtract(rstDetail.getOldEmiOs()).abs(), PennantConstants.defaultCCYDecPos));

		this.oldBalTenure.setValue(rstDetail.getOldBalTenure());
		this.newBalTenure.setValue(rstDetail.getNewBalTenure());
		this.diffBalTenure.setValue(Math.abs(rstDetail.getNewBalTenure() - rstDetail.getOldBalTenure()));

		this.oldMaturity.setFormat(DateFormat.LONG_DATE.getPattern());
		this.newMaturity.setFormat(DateFormat.LONG_DATE.getPattern());
		this.oldMaturity.setValue(rstDetail.getOldMaturity());
		this.newMaturity.setValue(rstDetail.getNewMaturity());

		this.oldTenure.setValue(rstDetail.getOldTenure());
		this.newTenure.setValue(rstDetail.getNewTenure());
		this.diffTenure.setValue(Math.abs(rstDetail.getNewTenure() - rstDetail.getOldTenure()));

		this.oldInterest.setValue(
				PennantApplicationUtil.formateAmount(rstDetail.getOldInterest(), PennantConstants.defaultCCYDecPos));
		this.newInterest.setValue(
				PennantApplicationUtil.formateAmount(rstDetail.getNewInterest(), PennantConstants.defaultCCYDecPos));
		this.diffInterest.setValue(PennantApplicationUtil.formateAmount(
				rstDetail.getNewInterest().subtract(rstDetail.getOldInterest()).abs(),
				PennantConstants.defaultCCYDecPos));

		this.oldCpzInterest.setValue(
				PennantApplicationUtil.formateAmount(rstDetail.getOldCpzInterest(), PennantConstants.defaultCCYDecPos));
		this.newCpzInterest.setValue(
				PennantApplicationUtil.formateAmount(rstDetail.getNewCpzInterest(), PennantConstants.defaultCCYDecPos));
		this.diffCpzInterest.setValue(PennantApplicationUtil.formateAmount(
				rstDetail.getNewCpzInterest().subtract(rstDetail.getOldCpzInterest()).abs(),
				PennantConstants.defaultCCYDecPos));

		this.oldMaxUnplannedEmi.setValue(rstDetail.getOldMaxUnplannedEmi());
		this.newMaxUnplannedEmi.setValue(rstDetail.getNewMaxUnplannedEmi());
		this.diffMaxUnplannedEmi
				.setValue(Math.abs(rstDetail.getNewMaxUnplannedEmi() - rstDetail.getOldMaxUnplannedEmi()));

		this.oldAvailedUnplanEmi.setValue(rstDetail.getOldAvailedUnplanEmi());
		this.newAvailedUnplanEmi.setValue(rstDetail.getNewAvailedUnplanEmi());
		this.diffAvailedUnplanEmi
				.setValue(Math.abs(rstDetail.getNewAvailedUnplanEmi() - rstDetail.getOldAvailedUnplanEmi()));

		this.oldFinalEmi.setValue(
				PennantApplicationUtil.formateAmount(rstDetail.getOldFinalEmi(), PennantConstants.defaultCCYDecPos));
		this.newFinalEmi.setValue(
				PennantApplicationUtil.formateAmount(rstDetail.getNewFinalEmi(), PennantConstants.defaultCCYDecPos));
		this.diffFinalEmi.setValue(PennantApplicationUtil.formateAmount(
				rstDetail.getNewFinalEmi().subtract(rstDetail.getOldFinalEmi()).abs(),
				PennantConstants.defaultCCYDecPos));

		this.oldPOsAmount.setValue(
				PennantApplicationUtil.formateAmount(rstDetail.getOldPOsAmount(), PennantConstants.defaultCCYDecPos));
		this.newPOsAmount.setValue(
				PennantApplicationUtil.formateAmount(rstDetail.getNewPOsAmount(), PennantConstants.defaultCCYDecPos));
		this.diffPOsAmount.setValue(PennantApplicationUtil.formateAmount(
				rstDetail.getNewPOsAmount().subtract(rstDetail.getOldPOsAmount()).abs(),
				PennantConstants.defaultCCYDecPos));

		this.oldEmiOverdue.setValue(
				PennantApplicationUtil.formateAmount(rstDetail.getOldEmiOverdue(), PennantConstants.defaultCCYDecPos));
		this.newEmiOverdue.setValue(
				PennantApplicationUtil.formateAmount(rstDetail.getNewEmiOverdue(), PennantConstants.defaultCCYDecPos));
		this.diffEmiOverdue.setValue(PennantApplicationUtil.formateAmount(
				rstDetail.getNewEmiOverdue().subtract(rstDetail.getOldEmiOverdue()).abs(),
				PennantConstants.defaultCCYDecPos));

		this.oldPenaltyAmount.setValue(PennantApplicationUtil.formateAmount(rstDetail.getOldPenaltyAmount(),
				PennantConstants.defaultCCYDecPos));
		this.newPenaltyAmount.setValue(PennantApplicationUtil.formateAmount(rstDetail.getNewPenaltyAmount(),
				PennantConstants.defaultCCYDecPos));
		this.diffPenaltyAmount.setValue(PennantApplicationUtil.formateAmount(
				rstDetail.getNewPenaltyAmount().subtract(rstDetail.getOldPenaltyAmount()).abs(),
				PennantConstants.defaultCCYDecPos));

		this.oldExtOdDays.setValue(rstDetail.getNewExtOdDays());
		this.newExtOdDays.setValue(rstDetail.getNewExtOdDays());
		this.diffExtOdDays.setValue(Math.abs(rstDetail.getNewExtOdDays() - rstDetail.getNewExtOdDays()));

		// Filling Other GroupBox Data
		this.serviceRequestNo.setValue(rstDetail.getServiceRequestNo());
		this.remark.setValue(rstDetail.getRemark());
		this.lastBilledDate.setFormat(DateFormat.LONG_DATE.getPattern());
		this.lastBilledDate.setValue(rstDetail.getLastBilledDate());
		this.lastBilledInstNo.setValue(rstDetail.getLastBilledInstNo());
		this.actLoanAmount.setValue(
				PennantApplicationUtil.formateAmount(rstDetail.getActLoanAmount(), PennantConstants.defaultCCYDecPos));
		this.emiHldStartDate.setFormat(DateFormat.LONG_DATE.getPattern());
		this.emiHldEndDate.setFormat(DateFormat.LONG_DATE.getPattern());
		this.priHldStartDate.setFormat(DateFormat.LONG_DATE.getPattern());
		this.priHldEndDate.setFormat(DateFormat.LONG_DATE.getPattern());
		this.emiHldStartDate.setValue(rstDetail.getEmiHldStartDate());
		this.emiHldEndDate.setValue(rstDetail.getEmiHldEndDate());
		this.priHldStartDate.setValue(rstDetail.getPriHldStartDate());
		this.priHldEndDate.setValue(rstDetail.getEmiHldEndDate());
		this.bounceCharge.setValue(
				PennantApplicationUtil.formateAmount(rstDetail.getBounceCharge(), PennantConstants.defaultCCYDecPos));
		this.otherCharge.setValue(
				PennantApplicationUtil.formateAmount(rstDetail.getOtherCharge(), PennantConstants.defaultCCYDecPos));
		this.restructureCharge.setValue(PennantApplicationUtil.formateAmount(rstDetail.getRestructureCharge(),
				PennantConstants.defaultCCYDecPos));
		this.repayProfitRate.setValue(rstDetail.getRepayProfitRate());
		this.finCurrAssetValue.setValue(PennantApplicationUtil.formateAmount(rstDetail.getFinCurrAssetValue(),
				PennantConstants.defaultCCYDecPos));

		logger.debug(Literal.LEAVING);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

}
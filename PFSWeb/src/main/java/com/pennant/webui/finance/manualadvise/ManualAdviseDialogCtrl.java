/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : ManualAdviseDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-04-2017 * *
 * Modified Date : 23-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.manualadvise;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.A;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.app.util.TDSCalculator;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceTaxDetailService;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.core.EventManager.Notify;
import com.pennant.pff.fee.AdviseType;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.finance.financemain.AccountingDetailDialogCtrl;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.receipt.constants.Allocation;

/**
 * This is the controller class for the /WEB-INF/pages/finance/ManualAdvise/manualAdviseDialog.zul file. <br>
 */
public class ManualAdviseDialogCtrl extends GFCBaseCtrl<ManualAdvise> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ManualAdviseDialogCtrl.class);

	protected Window window_ManualAdviseDialog;
	protected Combobox adviseType;
	protected ExtendedCombobox finReference;
	protected ExtendedCombobox feeTypeID;
	protected Intbox sequence;
	protected CurrencyBox adviseAmount;
	protected CurrencyBox paidAmount;
	protected CurrencyBox waivedAmount;
	protected Textbox remarks;
	protected Row reasonRow;
	protected Textbox reason;
	protected Listbox listBoxAdviseMovements;
	protected Datebox valueDate;
	protected Datebox postDate;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Tabpanel tabpanelBasicdetails;
	protected Tab adviseDetails;
	protected Groupbox adviseMovements;
	protected Label label_FeeTypeID;

	private ManualAdvise manualAdvise;
	private transient ManualAdviseListCtrl manualAdviseListCtrl;
	private transient ManualAdviseService manualAdviseService;
	private FinanceDetailService financeDetailService;
	private FinFeeDetailService finFeeDetailService;
	private transient FinanceTaxDetailService financeTaxDetailService;

	private List<ValueLabel> listAdviseType = AdviseType.getList();

	public static final int DEFAULT_ADVISETYPE = AdviseType.RECEIVABLE.id();

	// FinanceDetails Fields
	protected Label lbl_LoanReference;
	protected Label lbl_LoanType;
	protected Label lbl_CustCIF;
	protected Label lbl_FinAmount;
	protected Label lbl_startDate;
	protected Label lbl_MaturityDate;
	protected Groupbox finBasicdetails;

	// GST Details
	protected Groupbox gb_GSTDetails;
	protected Label label_TaxComponent;
	protected Decimalbox feeAmount;
	protected Decimalbox cgst;
	protected Decimalbox sgst;
	protected Decimalbox igst;
	protected Decimalbox ugst;
	protected Decimalbox totalGST;
	protected Decimalbox cess;
	protected Decimalbox total;
	protected Tab manualAdviseDetailsTab;
	// TDS details
	protected Groupbox gb_TDSDetails;
	protected Decimalbox tds;

	protected Label eligibleAmountLabel;
	protected CurrencyBox eligibleAmount;

	private FinanceMain financeMain;
	private transient DocumentDetailDialogCtrl documentDetailDialogCtrl;
	private transient FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl;
	private transient AccountingDetailDialogCtrl accountingDetailDialogCtrl;
	private long accountsetId;
	private String selectMethodName = "onSelectTab";
	private boolean isAccountingExecuted = false;

	protected A userActivityLog;

	private String module = null;

	/**
	 * default constructor.<br>
	 */
	public ManualAdviseDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ManualAdviseDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.manualAdvise.getAdviseID());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_ManualAdviseDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_ManualAdviseDialog);

		try {
			// Get the required arguments.
			this.manualAdvise = (ManualAdvise) arguments.get("manualAdvise");
			this.manualAdviseListCtrl = (ManualAdviseListCtrl) arguments.get("manualAdviseListCtrl");

			if (this.manualAdvise == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}
			this.financeMain = (FinanceMain) arguments.get("financeMain");
			if (this.financeMain == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}
			// Store the before image.
			ManualAdvise manualAdvise = new ManualAdvise();
			BeanUtils.copyProperties(this.manualAdvise, manualAdvise);
			this.manualAdvise.setBefImage(manualAdvise);

			if (arguments.containsKey("module")) {
				this.module = (String) arguments.get("module");
			}
			// Render the page and display the data.
			doLoadWorkFlow(this.manualAdvise.isWorkflow(), this.manualAdvise.getWorkflowId(),
					this.manualAdvise.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}
			this.listBoxAdviseMovements.setHeight(borderLayoutHeight - 210 + "px");
			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.manualAdvise);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.feeTypeID.setModuleName("FeeType");
		this.feeTypeID.setValueColumn("FeeTypeCode");
		this.feeTypeID.setDescColumn("FeeTypeDesc");
		this.feeTypeID.setValidateColumns(new String[] { "FeeTypeCode" });
		this.feeTypeID.setMandatoryStyle(true);

		this.adviseAmount.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.adviseAmount.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.adviseAmount.setScale(PennantConstants.defaultCCYDecPos);
		this.adviseAmount.setMandatory(true);

		this.paidAmount.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.paidAmount.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.paidAmount.setScale(PennantConstants.defaultCCYDecPos);

		this.waivedAmount.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.waivedAmount.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.waivedAmount.setScale(PennantConstants.defaultCCYDecPos);

		this.sequence.setMaxlength(10);
		this.remarks.setMaxlength(100);
		this.reason.setMaxlength(250);
		this.valueDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.postDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.eligibleAmount.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.eligibleAmount.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.eligibleAmount.setScale(PennantConstants.defaultCCYDecPos);

		this.reasonRow.setVisible(PennantConstants.MANUALADVISE_CANCEL_MODULE.equals(this.module));

		if (enqiryModule) {
			this.groupboxWf.setVisible(false);
		} else {
			setStatusDetails();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ManualAdviseDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ManualAdviseDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ManualAdviseDialog_btnEdit"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ManualAdviseDialog_btnDelete"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.manualAdvise);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		manualAdviseListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.manualAdvise.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$feeTypeID(Event event) {
		logger.debug(Literal.ENTERING);

		FeeType feeType = getFeeType();

		if (feeType != null && feeType instanceof FeeType) {
			setEligibleAmount(feeType);
		}

		calculateGST();

		calculateTDS();

		doDisplayEligibleAmount();

		logger.debug(Literal.LEAVING);
	}

	private FeeType getFeeType() {
		FeeType feeType = null;
		Object dataObject = feeTypeID.getObject();
		if (dataObject instanceof String) {
			this.feeTypeID.setValue(dataObject.toString());
			this.feeTypeID.setDescription("");
			this.feeTypeID.setAttribute("TaxApplicable", false);
			this.feeTypeID.setAttribute("TDSApplicable", false);
			this.feeTypeID.setAttribute("TaxComponent", "");
		} else {
			feeType = (FeeType) dataObject;
			if (feeType != null) {
				this.feeTypeID.setAttribute("FeeTypeID", feeType.getFeeTypeID());
				this.feeTypeID.setAttribute("TaxApplicable", feeType.isTaxApplicable());
				this.feeTypeID.setAttribute("TDSApplicable", feeType.isTdsReq());
				this.feeTypeID.setAttribute("TaxComponent", feeType.getTaxComponent());
			}
		}

		return feeType;
	}

	private void doDisplayEligibleAmount() {
		String adviseTypeValue = getComboboxValue(this.adviseType);
		Object object = (Object) this.feeTypeID.getObject();

		if (StringUtils.equals(adviseTypeValue, PennantConstants.List_Select)) {
			this.eligibleAmountLabel.setVisible(false);
			this.eligibleAmount.setVisible(false);
			this.feeTypeID.setObject(null);
			return;
		}

		int adviseType = Integer.parseInt(adviseTypeValue);

		if (adviseType == AdviseType.RECEIVABLE.id()) {
			this.feeTypeID.setObject(null);
		}

		FeeType feeType = null;

		if (object instanceof FeeType) {
			feeType = (FeeType) object;
		}

		boolean validPayableLink = false;

		if (feeType != null) {
			validPayableLink = isValidPayableLink(feeType.getPayableLinkTo(), adviseType);
		}

		if (adviseType == AdviseType.PAYABLE.id() && validPayableLink) {
			this.eligibleAmountLabel.setVisible(true);
			this.eligibleAmount.setVisible(true);
		} else {
			this.eligibleAmountLabel.setVisible(false);
			this.eligibleAmount.setVisible(false);
		}
	}

	public void onChange$adviseType(Event event) {
		logger.debug(Literal.ENTERING);

		setFeeTypeFilters();

		calculateGST();

		calculateTDS();

		doDisplayEligibleAmount();

		logger.debug(Literal.LEAVING);
	}

	public void onChange$valueDate(Event event) {
		FeeType feeType = getFeeType();

		if (feeType != null && feeType instanceof FeeType) {
			setEligibleAmount(feeType);
		}
	}

	/**
	 * Method for getting Discrepancies based on Finance Amount
	 */
	public void onFulfill$adviseAmount(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		if (this.adviseAmount.getActualValue() != null
				&& this.adviseAmount.getActualValue().compareTo(BigDecimal.ZERO) > 0) {
			// do nothing
		} else {
			this.adviseAmount.setValue(BigDecimal.ZERO);
		}
		calculateGST();
		calculateTDS();

		logger.debug(Literal.LEAVING + event.toString());
	}

	private void calculateGST() {
		logger.debug(Literal.ENTERING);

		boolean taxApplicable = (boolean) this.feeTypeID.getAttribute("TaxApplicable");
		String taxComp = (String) this.feeTypeID.getAttribute("TaxComponent");

		if (taxApplicable) {
			tabpanelBasicdetails.setHeight("100%");
			this.gb_GSTDetails.setVisible(true);
			tabpanelBasicdetails.setHeight("100%");
			FinanceDetail financeDetail = financeDetailService.getFinSchdDetailById(financeMain.getFinID(), "", false);

			Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(financeMain);

			// For Calculating the GST amount, converting fees as FinFeeDetail
			// and FinTypeFees and this is for inquiry purpose only, these
			// values are not saving.
			// GST Calculation is having FinFeeDetailService, so we just use the
			// existing functionality and display the GST amounts.
			if (financeDetail != null) {
				BigDecimal adviseAmount = BigDecimal.ZERO;
				FinFeeDetail finFeeDetail = new FinFeeDetail();
				FinTypeFees finTypeFee = new FinTypeFees();

				if (this.adviseAmount.getActualValue() != null) {
					adviseAmount = (PennantApplicationUtil.unFormateAmount(this.adviseAmount.getActualValue(),
							PennantConstants.defaultCCYDecPos));
				}

				finFeeDetail.setCalculatedAmount(adviseAmount);

				this.feeTypeID.getValidatedValue();

				finFeeDetail.setTaxComponent(taxComp);
				finFeeDetail.setTaxApplicable(taxApplicable);
				finTypeFee.setTaxComponent(taxComp);
				finTypeFee.setTaxApplicable(taxApplicable);
				finTypeFee.setAmount(adviseAmount);

				finFeeDetailService.convertGSTFinTypeFees(finFeeDetail, finTypeFee, financeDetail, taxPercentages);
				finFeeDetailService.calculateFees(finFeeDetail, financeMain, taxPercentages);

				String taxComponent = "";

				if (StringUtils.equals(FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE, finFeeDetail.getTaxComponent())) {
					taxComponent = Labels.getLabel("label_FeeTypeDialog_Exclusive");
				} else if (StringUtils.equals(FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE,
						finFeeDetail.getTaxComponent())) {
					taxComponent = Labels.getLabel("label_FeeTypeDialog_Inclusive");
				}

				this.label_TaxComponent.setValue(taxComponent);

				int formatter = CurrencyUtil.getFormat(financeDetail.getFinScheduleData().getFinanceMain().getFinCcy());

				this.feeAmount.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				this.feeAmount.setScale(formatter);
				this.feeAmount
						.setValue(PennantApplicationUtil.formateAmount(finFeeDetail.getNetAmountOriginal(), formatter));
				readOnlyComponent(true, this.feeAmount);

				BigDecimal cgstAmount = BigDecimal.ZERO;
				BigDecimal sgstAmount = BigDecimal.ZERO;
				BigDecimal igstAmount = BigDecimal.ZERO;
				BigDecimal ugstAmount = BigDecimal.ZERO;
				BigDecimal cessAmount = BigDecimal.ZERO;
				if (finFeeDetail.getTaxHeader() != null) {
					List<Taxes> taxDetails = finFeeDetail.getTaxHeader().getTaxDetails();

					for (Taxes taxes : taxDetails) {
						if (StringUtils.equals(taxes.getTaxType(), RuleConstants.CODE_CGST)) {
							cgstAmount = cgstAmount.add(taxes.getNetTax());
						} else if (StringUtils.equals(taxes.getTaxType(), RuleConstants.CODE_SGST)) {
							sgstAmount = sgstAmount.add(taxes.getNetTax());
						} else if (StringUtils.equals(taxes.getTaxType(), RuleConstants.CODE_IGST)) {
							igstAmount = igstAmount.add(taxes.getNetTax());
						} else if (StringUtils.equals(taxes.getTaxType(), RuleConstants.CODE_UGST)) {
							ugstAmount = ugstAmount.add(taxes.getNetTax());
						} else if (StringUtils.equals(taxes.getTaxType(), RuleConstants.CODE_CESS)) {
							cessAmount = cessAmount.add(taxes.getNetTax());
						}
					}
				}

				this.cgst.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				this.cgst.setValue(PennantApplicationUtil.formateAmount(cgstAmount, formatter));

				this.sgst.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				this.sgst.setValue(PennantApplicationUtil.formateAmount(sgstAmount, formatter));

				this.igst.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				this.igst.setValue(PennantApplicationUtil.formateAmount(igstAmount, formatter));

				this.ugst.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				this.ugst.setValue(PennantApplicationUtil.formateAmount(ugstAmount, formatter));

				this.cess.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				this.cess.setValue(PennantApplicationUtil.formateAmount(cessAmount, formatter));

				BigDecimal totalGstAmount = cgstAmount.add(sgstAmount).add(igstAmount).add(ugstAmount).add(cessAmount);

				BigDecimal totalAmount = BigDecimal.ZERO;
				totalAmount = finFeeDetail.getNetAmountOriginal().add(totalGstAmount);

				this.totalGST.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				this.totalGST.setValue(PennantApplicationUtil.formateAmount(totalGstAmount, formatter));

				this.total.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
				this.total.setValue(PennantApplicationUtil.formateAmount(totalAmount, formatter));
			}
		} else {
			this.gb_GSTDetails.setVisible(false);
			this.cgst.setValue(BigDecimal.ZERO);
			this.sgst.setValue(BigDecimal.ZERO);
			this.igst.setValue(BigDecimal.ZERO);
			this.ugst.setValue(BigDecimal.ZERO);
			this.totalGST.setValue(BigDecimal.ZERO);
			this.total.setValue(BigDecimal.ZERO);
		}

		logger.debug(Literal.LEAVING);
	}

	/*
	 * Calculate TDS based fee type TDS applicable parameter.
	 */
	private void calculateTDS() {
		boolean tdsApplicable = (boolean) this.feeTypeID.getAttribute("TDSApplicable");
		boolean taxApplicable = (boolean) this.feeTypeID.getAttribute("TaxApplicable");
		String taxComp = (String) this.feeTypeID.getAttribute("TaxComponent");

		if (!TDSCalculator.isTDSApplicable(financeMain, tdsApplicable)) {
			this.gb_TDSDetails.setVisible(false);
			this.tds.setValue(BigDecimal.ZERO);
			return;
		}

		FinanceDetail fd = financeDetailService.getFinSchdDetailById(financeMain.getFinID(), "", false);

		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
		tdsApplicable = TDSCalculator.isTDSApplicable(fm);
		this.gb_TDSDetails.setVisible(true);
		int formatter = CurrencyUtil.getFormat(fm.getFinCcy());

		BigDecimal adviseAmountVal = BigDecimal.ZERO;
		BigDecimal tdsAmount = BigDecimal.ZERO;

		if (this.adviseAmount.getActualValue() != null) {
			adviseAmountVal = PennantApplicationUtil.unFormateAmount(this.adviseAmount.getActualValue(), formatter);
		}

		FinFeeDetail fee = new FinFeeDetail();
		fee.setCalculatedAmount(adviseAmountVal);
		fee.setTaxComponent(taxComp);
		fee.setTaxApplicable(taxApplicable);

		FinTypeFees feeType = new FinTypeFees();
		feeType.setTaxComponent(taxComp);
		feeType.setTaxApplicable(taxApplicable);
		feeType.setAmount(adviseAmountVal);

		Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(fm);

		finFeeDetailService.convertGSTFinTypeFees(fee, feeType, fd, taxPercentages);
		finFeeDetailService.calculateFees(fee, financeMain, taxPercentages);

		tdsAmount = TDSCalculator.getTDSAmount(fee.getNetAmountOriginal());

		this.tds.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.tds.setValue(PennantApplicationUtil.formateAmount(tdsAmount, formatter));
	}

	public void setFeeTypeFilters() {
		logger.debug(Literal.ENTERING);

		String adviseTypeValue = this.adviseType.getSelectedItem().getValue().toString();

		Filter filter[] = null;

		if (!PennantConstants.List_Select.equals(adviseTypeValue)) {
			if (Integer.parseInt(adviseTypeValue) == AdviseType.RECEIVABLE.id()) {
				filter = new Filter[1];
				filter[0] = new Filter("AdviseType", AdviseType.PAYABLE.id(), Filter.OP_NOT_EQUAL);
			} else {
				filter = new Filter[1];
				filter[0] = new Filter("AdviseType", AdviseType.PAYABLE.id(), Filter.OP_EQUAL);
			}
		}

		this.feeTypeID.setFilters(filter);
		this.feeTypeID.setValue("");
		this.feeTypeID.setDescription("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param manualAdvise
	 * 
	 */
	public void doWriteBeanToComponents(ManualAdvise aManualAdvise) {
		logger.debug(Literal.ENTERING);

		// FIXME to be changed on adding the payable advises. As of now Advise
		// type is not visible
		// this.adviseType.setValue(String.valueOf(FinanceConstants.MANUAL_ADVISE_RECEIVABLE));

		this.lbl_LoanReference.setValue(financeMain.getFinReference());
		this.lbl_LoanType.setValue(financeMain.getFinType() + " - " + financeMain.getLovDescFinTypeName());
		this.lbl_CustCIF.setValue(financeMain.getLovDescCustCIF() + " - " + financeMain.getLovDescCustShrtName());
		this.lbl_FinAmount.setValue(PennantApplicationUtil.amountFormate(financeMain.getFinAssetValue(),
				CurrencyUtil.getFormat(financeMain.getFinCcy())));
		this.lbl_startDate
				.setValue(DateUtil.format(financeMain.getFinStartDate(), DateFormat.LONG_DATE.getPattern()));
		this.lbl_MaturityDate
				.setValue(DateUtil.format(financeMain.getMaturityDate(), DateFormat.LONG_DATE.getPattern()));

		fillComboBox(this.adviseType, String.valueOf(aManualAdvise.getAdviseType()), listAdviseType, "");

		setFeeTypeFilters();

		// this.finReference.setValue(aManualAdvise.getFinReference());
		this.feeTypeID.setAttribute("FeeTypeID", aManualAdvise.getFeeTypeID());
		this.feeTypeID.setAttribute("TaxApplicable", aManualAdvise.isTaxApplicable());
		this.feeTypeID.setAttribute("TDSApplicable", aManualAdvise.isTdsReq());
		this.feeTypeID.setAttribute("TaxComponent", aManualAdvise.getTaxComponent());
		this.feeTypeID.setValue(aManualAdvise.getFeeTypeCode(), aManualAdvise.getFeeTypeDesc());

		this.sequence.setValue(aManualAdvise.getSequence());
		this.adviseAmount.setValue(PennantApplicationUtil.formateAmount(aManualAdvise.getAdviseAmount(),
				PennantConstants.defaultCCYDecPos));
		this.paidAmount.setValue(
				PennantApplicationUtil.formateAmount(aManualAdvise.getPaidAmount(), PennantConstants.defaultCCYDecPos));
		this.waivedAmount.setValue(PennantApplicationUtil.formateAmount(aManualAdvise.getWaivedAmount(),
				PennantConstants.defaultCCYDecPos));
		this.remarks.setValue(aManualAdvise.getRemarks());

		Date appDate = SysParamUtil.getAppDate();
		if (aManualAdvise.isNewRecord()) {
			this.feeTypeID.setDescription("");
			this.valueDate.setValue(appDate);
			this.postDate.setValue(appDate);
		} else {
			if (aManualAdvise.getFeeTypeCode() != null) {
				this.feeTypeID.setValue(aManualAdvise.getFeeTypeCode(), aManualAdvise.getFeeTypeDesc());
				FeeType feeType = new FeeType(aManualAdvise.getFeeTypeID());
				feeType.setPayableLinkTo(aManualAdvise.getPayableLinkTo());
				this.feeTypeID.setObject(feeType);
			} else {
				this.label_FeeTypeID.setValue(Labels.getLabel("label_ManualAdviseDialog_BounceID.value"));
				this.feeTypeID.setAttribute("BounceID", aManualAdvise.getBounceID());
				this.feeTypeID.setValue(String.valueOf(aManualAdvise.getBounceID()), "");
			}
			this.valueDate.setValue(aManualAdvise.getValueDate());
			this.postDate.setValue(aManualAdvise.getPostDate());

			FeeType feeType = manualAdvise.getFeeType();

			if (feeType != null) {
				setEligibleAmount(feeType);
			}
		}

		if (enqiryModule) {
			this.adviseMovements.setVisible(true);
			List<ManualAdviseMovements> advisemovementList = manualAdviseService
					.getAdivseMovements(this.manualAdvise.getAdviseID());
			doFillMovementDetails(advisemovementList);
		} else {
			this.adviseMovements.setVisible(false);
		}

		this.reason.setValue(manualAdvise.getReason());

		this.recordStatus.setValue(manualAdvise.getRecordStatus());

		doDisplayEligibleAmount();

		calculateGST();

		calculateTDS();

		// Accounting Details Tab Addition
		if ((getWorkFlow() != null && !StringUtils.equals(getWorkFlow().firstTaskOwner(), getRole()))
				|| (this.enqiryModule)) {
			com.pennant.backend.model.finance.FeeType feeType = manualAdvise.getFeeType();
			if (feeType != null && feeType.isDueAccReq()) {
				if (ImplementationConstants.MANUAL_ADVISE_FUTURE_DATE) {
					if (!(manualAdvise.getValueDate().compareTo(appDate) > 0)) {
						appendAccountingDetailTab(manualAdvise, true);
					}
				} else {
					appendAccountingDetailTab(manualAdvise, true);
				}
			}
		}
		appendDocumentDetailTab();
		if (ImplementationConstants.MANUAL_ADVISE_FUTURE_DATE) {
			if (this.enqiryModule) {
				this.userActivityLog.setVisible(true);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$userActivityLog(Event event) throws Exception {
		logger.debug(Literal.ENTERING + event.toString());
		doUserActivityLog();
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void doUserActivityLog() throws Exception {
		logger.debug(Literal.ENTERING);

		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("label_FinanceMainDialog_FinType.value", financeMain.getFinType());
		map.put("label_FinanceMainDialog_FinCcy.value", financeMain.getFinCcy());

		if (StringUtils.isNotEmpty(financeMain.getScheduleMethod())) {
			map.put("label_FinanceMainDialog_ScheduleMethod.value", financeMain.getScheduleMethod());
		} else {
			map.put("label_FinanceMainDialog_ScheduleMethod.value", "");
		}
		map.put("label_FinanceMainDialog_ProfitDaysBasis.value", financeMain.getProfitDaysBasis());
		map.put("label_FinanceMainDialog_FinReference.value", financeMain.getFinReference());
		map.put("label_FinanceMainDialog_CustShrtName.value", financeMain.getLovDescCustShrtName());

		map.put("adviseID", manualAdvise.getAdviseID());

		doShowActivityLog(financeMain.getFinReference(), map);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering
	 * 
	 * @param advisemovementList
	 */
	private void doFillMovementDetails(List<ManualAdviseMovements> movementList) {
		logger.debug(Literal.ENTERING);

		this.listBoxAdviseMovements.getItems().clear();
		if (movementList != null && !movementList.isEmpty()) {
			for (ManualAdviseMovements movement : movementList) {
				Listitem item = new Listitem();
				Listcell lc;

				lc = new Listcell(DateUtil.format(movement.getMovementDate(), DateFormat.LONG_DATE.getPattern()));
				item.appendChild(lc);

				lc = new Listcell(PennantApplicationUtil.amountFormate(movement.getMovementAmount(),
						PennantConstants.defaultCCYDecPos));
				lc.setStyle("text-align:right;");
				item.appendChild(lc);

				lc = new Listcell(PennantApplicationUtil.amountFormate(movement.getPaidAmount(),
						PennantConstants.defaultCCYDecPos));
				lc.setStyle("text-align:right;");
				item.appendChild(lc);

				BigDecimal totalPaidGst = movement.getPaidCGST().add(movement.getPaidUGST()).add(movement.getPaidSGST())
						.add(movement.getPaidIGST()).add(movement.getPaidCESS());

				lc = new Listcell(
						PennantApplicationUtil.amountFormate(totalPaidGst, PennantConstants.defaultCCYDecPos));
				lc.setStyle("text-align:right;");
				item.appendChild(lc);

				BigDecimal totalWaivedGST = movement.getWaivedCGST().add(movement.getWaivedUGST())
						.add(movement.getWaivedSGST()).add(movement.getWaivedIGST()).add(movement.getWaivedCESS());

				BigDecimal waivedamount;
				if (FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE.equals(movement.getTaxComponent())) {
					waivedamount = movement.getWaivedAmount().subtract(totalWaivedGST);
				} else {
					waivedamount = movement.getWaivedAmount();
				}

				lc = new Listcell(
						PennantApplicationUtil.amountFormate(waivedamount, PennantConstants.defaultCCYDecPos));
				lc.setStyle("text-align:right;");
				item.appendChild(lc);

				lc = new Listcell(
						PennantApplicationUtil.amountFormate(totalWaivedGST, PennantConstants.defaultCCYDecPos));
				lc.setStyle("text-align:right;");
				item.appendChild(lc);

				lc = new Listcell(movement.getStatus());
				item.appendChild(lc);

				lc = new Listcell(PennantApplicationUtil.getLabelDesc(movement.getReceiptMode(),
						PennantStaticListUtil.getReceiptModes()));
				item.appendChild(lc);

				this.listBoxAdviseMovements.appendChild(item);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aManualAdvise
	 */
	public void doWriteComponentsToBean(ManualAdvise aManualAdvise) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<>();

		// Advise Type
		try {
			String strAdviseType = null;
			if (this.adviseType.getSelectedItem() != null) {
				strAdviseType = this.adviseType.getSelectedItem().getValue().toString();
			}
			if (strAdviseType != null && !PennantConstants.List_Select.equals(strAdviseType)) {
				aManualAdvise.setAdviseType(Integer.parseInt(strAdviseType));

			} else {
				aManualAdvise.setAdviseType(DEFAULT_ADVISETYPE);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Loan Reference
		try {
			// this.finReference.getValidatedValue();
			aManualAdvise.setFinID(financeMain.getFinID());
			aManualAdvise.setFinReference(this.lbl_LoanReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Fee Type ID
		try {
			this.feeTypeID.getValidatedValue();
			FeeType feeType = (FeeType) this.feeTypeID.getObject();
			if (feeType != null) {
				aManualAdvise.setFeeTypeID(feeType.getFeeTypeID());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Sequence
		try {
			aManualAdvise.setSequence(this.sequence.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Advise Amount
		try {
			if (this.adviseAmount.getActualValue() != null) {
				aManualAdvise.setAdviseAmount(PennantApplicationUtil.unFormateAmount(this.adviseAmount.getActualValue(),
						PennantConstants.defaultCCYDecPos));
				aManualAdvise.setBalanceAmt(PennantApplicationUtil.unFormateAmount(this.adviseAmount.getActualValue(),
						PennantConstants.defaultCCYDecPos));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Paid Amount
		try {
			if (this.paidAmount.getActualValue() != null) {
				aManualAdvise.setPaidAmount(PennantApplicationUtil.unFormateAmount(this.paidAmount.getActualValue(),
						PennantConstants.defaultCCYDecPos));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Waived Amount
		try {
			if (this.waivedAmount.getActualValue() != null) {
				aManualAdvise.setWaivedAmount(PennantApplicationUtil.unFormateAmount(this.waivedAmount.getActualValue(),
						PennantConstants.defaultCCYDecPos));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Remarks
		try {
			aManualAdvise.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aManualAdvise.setValueDate(this.valueDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aManualAdvise.setPostDate(this.postDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Document Details Saving
		if (documentDetailDialogCtrl != null) {
			aManualAdvise.setDocumentDetails(documentDetailDialogCtrl.getDocumentDetailsList());
		} else {
			aManualAdvise.setDocumentDetails(aManualAdvise.getDocumentDetails());
		}
		// Reason
		try {
			aManualAdvise.setReason(this.reason.getValue());

			if (StringUtils.isNotEmpty(aManualAdvise.getReason())) {
				aManualAdvise.setStatus(PennantConstants.MANUALADVISE_CANCEL);
			} else {
				aManualAdvise.setReason(null);
				aManualAdvise.setStatus(null);
			}

			if (PennantConstants.MANUALADVISE_MAINTAIN_MODULE.equals(this.module)) {
				aManualAdvise.setStatus(PennantConstants.MANUALADVISE_MAINTAIN);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.eligibleAmount.getActualValue() != null) {
				aManualAdvise.setEligibleAmount(PennantApplicationUtil
						.unFormateAmount(this.eligibleAmount.getActualValue(), PennantConstants.defaultCCYDecPos));
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param manualAdvise The entity that need to be render.
	 */
	public void doShowDialog(ManualAdvise manualAdvise) {
		logger.debug(Literal.LEAVING);

		if (manualAdvise.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.adviseType.focus();
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(manualAdvise.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.adviseType.focus();
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(manualAdvise);
		this.btnDelete.setVisible(false);

		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		this.feeTypeID.setConstraint(
				new PTStringValidator(Labels.getLabel("label_ManualAdviseDialog_FeeTypeID.value"), null, true, true));

		if (!this.adviseType.isDisabled()) {
			this.adviseType.setConstraint(new StaticListValidator(listAdviseType,
					Labels.getLabel("label_ManualAdviseDialog_AdviseType.value")));
		}

		if (!this.adviseAmount.isReadonly()) {
			this.adviseAmount.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_ManualAdviseDialog_AdviseAmount.value"),
							PennantConstants.defaultCCYDecPos, true, false, 0));
		}
		if (!this.valueDate.isDisabled()) {
			this.valueDate.setConstraint(new PTDateValidator(
					Labels.getLabel("label_ManualAdviseDialog_ValueDate.value"), true, null, null, true));
		}

		if (PennantConstants.MANUALADVISE_CANCEL_MODULE.equals(this.module) && !this.reason.isReadonly()) {
			this.reason.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ManualAdviseDialog_Reason.value"), null, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.adviseType.setConstraint("");
		this.feeTypeID.setConstraint("");
		this.sequence.setConstraint("");
		this.adviseAmount.setConstraint("");
		this.paidAmount.setConstraint("");
		this.waivedAmount.setConstraint("");
		this.remarks.setConstraint("");
		this.valueDate.setConstraint("");
		this.eligibleAmount.setConstraint("");
		this.reason.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		// Advise ID
		// Advise Type
		// Loan Reference
		// Fee Type ID
		// Sequence
		// Advise Amount
		// Paid Amount
		// Waived Amount
		// Remarks

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);
		this.valueDate.setErrorMessage("");
		this.postDate.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final ManualAdvise aManualAdvise = new ManualAdvise();
		BeanUtils.copyProperties(this.manualAdvise, aManualAdvise);

		doDelete(String.valueOf(aManualAdvise.getAdviseID()), aManualAdvise);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		this.eligibleAmount.setReadonly(true);

		if (this.manualAdvise.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(isReadOnly("ManualAdviseDialog_FeeTypeID"), this.feeTypeID);
			readOnlyComponent(isReadOnly("ManualAdviseDialog_AdviseType"), this.adviseType);
			readOnlyComponent(isReadOnly("ManualAdviseDialog_AdviseAmount"), this.adviseAmount);
			readOnlyComponent(isReadOnly("ManualAdviseDialog_PaidAmount"), this.paidAmount);
			readOnlyComponent(isReadOnly("ManualAdviseDialog_WaivedAmount"), this.waivedAmount);
			readOnlyComponent(isReadOnly("ManualAdviseDialog_Sequence"), this.sequence);
			readOnlyComponent(isReadOnly("ManualAdviseDialog_ValueDate"), this.valueDate);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.feeTypeID);
			readOnlyComponent(true, this.paidAmount);
			readOnlyComponent(true, this.waivedAmount);
			readOnlyComponent(true, this.adviseType);
			readOnlyComponent(true, this.sequence);
			readOnlyComponent(true, this.postDate);
		}
		readOnlyComponent(true, this.postDate);
		if (!enqiryModule) {
			readOnlyComponent(isReadOnly("ManualAdviseDialog_Remarks"), this.remarks);
			readOnlyComponent(isReadOnly("ManualAdviseDialog_AdviseAmount"), this.adviseAmount);
			readOnlyComponent(isReadOnly("ManualAdviseDialog_ValueDate"), this.valueDate);
		} else {
			readOnlyComponent(true, this.adviseAmount);
			readOnlyComponent(true, this.remarks);
			readOnlyComponent(true, this.valueDate);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.manualAdvise.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		if (PennantConstants.MANUALADVISE_CANCEL_MODULE.equals(this.module)) {
			if (!isReadOnly("ManualAdviseDialog_Reason")) {
				readOnlyComponent(!isReadOnly("ManualAdviseDialog_FeeTypeID"), this.feeTypeID);
				readOnlyComponent(!isReadOnly("ManualAdviseDialog_AdviseType"), this.adviseType);
				readOnlyComponent(!isReadOnly("ManualAdviseDialog_AdviseAmount"), this.adviseAmount);
				readOnlyComponent(!isReadOnly("ManualAdviseDialog_PaidAmount"), this.paidAmount);
				readOnlyComponent(!isReadOnly("ManualAdviseDialog_WaivedAmount"), this.waivedAmount);
				readOnlyComponent(!isReadOnly("ManualAdviseDialog_Sequence"), this.sequence);
				readOnlyComponent(!isReadOnly("ManualAdviseDialog_ValueDate"), this.valueDate);
				readOnlyComponent(!isReadOnly("ManualAdviseDialog_Remarks"), this.remarks);
			}
			if (!enqiryModule) {
				readOnlyComponent(isReadOnly("ManualAdviseDialog_Reason"), this.reason);
			}
		}

		if (enqiryModule && PennantConstants.MANUALADVISE_CANCEL.equals(manualAdvise.getStatus())) {
			readOnlyComponent(true, this.reason);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);
		readOnlyComponent(true, this.adviseType);
		readOnlyComponent(true, this.finReference);
		readOnlyComponent(true, this.feeTypeID);
		readOnlyComponent(true, this.sequence);
		readOnlyComponent(true, this.adviseAmount);
		readOnlyComponent(true, this.paidAmount);
		readOnlyComponent(true, this.waivedAmount);
		readOnlyComponent(true, this.remarks);
		readOnlyComponent(true, this.valueDate);
		readOnlyComponent(true, this.postDate);
		readOnlyComponent(true, this.eligibleAmount);
		readOnlyComponent(true, this.reason);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);
		this.adviseType.setSelectedIndex(0);
		this.finReference.setValue("");
		this.finReference.setDescription("");
		this.feeTypeID.setValue("");
		this.feeTypeID.setDescription("");
		this.sequence.setText("");
		this.adviseAmount.setValue("");
		this.paidAmount.setValue("");
		this.waivedAmount.setValue("");
		this.remarks.setValue("");
		this.eligibleAmount.setValue("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final ManualAdvise aManualAdvise = new ManualAdvise();
		BeanUtils.copyProperties(this.manualAdvise, aManualAdvise);
		boolean isNew = false;

		doSetValidation();

		if (ImplementationConstants.MANUAL_ADVISE_FUTURE_DATE) {
			if (validateValueDate()) {
				return;
			}
		}

		doWriteComponentsToBean(aManualAdvise);

		String errMsg = validatePayableAmount(aManualAdvise);

		if (errMsg != null) {
			MessageUtil.showError(errMsg);
			return;
		}

		// Accounting Details Tab Addition
		if (!StringUtils.equals(getWorkFlow().firstTaskOwner(), getRole())) {
			com.pennant.backend.model.finance.FeeType feeType = manualAdvise.getFeeType();
			if (feeType != null && feeType.isDueAccReq()) {
				if (ImplementationConstants.MANUAL_ADVISE_FUTURE_DATE) {
					if (!(manualAdvise.getValueDate().compareTo(SysParamUtil.getAppDate()) > 0)) {
						if (validateAccounting()) {
							return;
						}
					}
				} else {
					if (validateAccounting()) {
						return;
					}
				}
			}
		}

		isNew = aManualAdvise.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aManualAdvise.getRecordType())) {
				aManualAdvise.setVersion(aManualAdvise.getVersion() + 1);
				if (isNew) {
					aManualAdvise.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aManualAdvise.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aManualAdvise.setNewRecord(true);
				}
			}
		} else {
			aManualAdvise.setVersion(aManualAdvise.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aManualAdvise, tranType)) {
				refreshList();

				// Customer Notification for Role Identification
				if (StringUtils.isBlank(aManualAdvise.getNextTaskId())) {
					aManualAdvise.setNextRoleCode("");
				}
				String msg = PennantApplicationUtil.getSavingStatus(aManualAdvise.getRoleCode(),
						aManualAdvise.getNextRoleCode(), aManualAdvise.getFinReference(), " Manual Advise ",
						aManualAdvise.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);

				// User Notifications Message/Alert
				publishNotification(Notify.ROLE, aManualAdvise.getFinReference(), aManualAdvise);

				closeDialog();
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private boolean validateAccounting() {
		boolean validate = false;
		validate = validateAccounting(validate);
		// Accounting Details Validations
		if (validate) {
			if (!isAccountingExecuted) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Calc_Accountings"));
				return true;
			}
			if (!this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("Save") && accountingDetailDialogCtrl
					.getDisbCrSum().compareTo(accountingDetailDialogCtrl.getDisbDrSum()) != 0) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Acc_NotMatching"));
				return true;
			}
		}

		return false;
	}

	private boolean validateValueDate() {
		String usrAction = this.userAction.getSelectedItem().getValue();

		if (PennantConstants.RCD_STATUS_RESUBMITTED.equals(usrAction)
				|| PennantConstants.RCD_STATUS_REJECTED.equals(usrAction)
				|| PennantConstants.RCD_STATUS_CANCELLED.equals(usrAction)) {
			return false;
		}

		Date valueDate = this.valueDate.getValue();
		Date maturityDate = financeMain.getMaturityDate();
		boolean finIsActive = financeMain.isFinIsActive();

		if (valueDate.compareTo(maturityDate) > 0 && finIsActive) {
			if (PennantConstants.MANUALADVISE_MAINTAIN_MODULE.equals(this.module)) {
				MessageUtil.showError(
						Labels.getLabel("label_ManualAdviseDialog_Cancel_DueDateCrossedMaturityInMtn.ErrMsg"));
				return true;
			} else if (PennantConstants.MANUALADVISE_CREATE_MODULE.equals(this.module)) {
				MessageUtil.showError(Labels.getLabel("label_ManualAdviseDialog_Cancel_DueDateCrossedMaturity.ErrMsg"));
				return true;
			}
		}

		Date appDate = SysParamUtil.getAppDate();
		if (valueDate.compareTo(appDate) != 0 && !finIsActive) {
			MessageUtil.showError(Labels.getLabel("label_ManualAdviseDialog_Cancel_LoanClosed.ErrMsg"));
			return true;
		}

		if (PennantConstants.MANUALADVISE_MAINTAIN_MODULE.equals(this.module) && valueDate.compareTo(appDate) <= 0) {
			MessageUtil.showError(Labels.getLabel("label_ManualAdviseDialog_Cancel_BackDate.ErrMsg"));
			return true;
		}

		if (PennantConstants.MANUALADVISE_CANCEL_MODULE.equals(this.module) && appDate.compareTo(valueDate) >= 0) {
			MessageUtil
					.showError(Labels.getLabel("label_ManualAdviseDialog_Cancel_DueDateCrossedBeforeApprove.ErrMsg"));
			return true;
		}

		return false;
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(ManualAdvise aManualAdvise, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aManualAdvise.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aManualAdvise.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aManualAdvise.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aManualAdvise.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aManualAdvise.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aManualAdvise);
				}

				if (isNotesMandatory(taskId, aManualAdvise)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}

				}
			}
			if (!StringUtils.isBlank(nextTaskId)) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aManualAdvise.setTaskId(taskId);
			aManualAdvise.setNextTaskId(nextTaskId);
			aManualAdvise.setRoleCode(getRole());
			aManualAdvise.setNextRoleCode(nextRoleCode);
			// Document Details
			if (CollectionUtils.isNotEmpty(aManualAdvise.getDocumentDetails())) {
				for (DocumentDetails details : aManualAdvise.getDocumentDetails()) {
					details.setReferenceId(String.valueOf(aManualAdvise.getId()));
					details.setDocModule(PennantConstants.PAYABLE_ADVISE_DOC_MODULE_NAME);
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setRecordStatus(aManualAdvise.getRecordStatus());
					details.setWorkflowId(aManualAdvise.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(aManualAdvise.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(aManualAdvise.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}
			auditHeader = getAuditHeader(aManualAdvise, tranType);
			String operationRefs = getServiceOperations(taskId, aManualAdvise);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aManualAdvise, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aManualAdvise, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		ManualAdvise aManualAdvise = (ManualAdvise) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = manualAdviseService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = manualAdviseService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = manualAdviseService.doApprove(auditHeader);

					if (aManualAdvise.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = manualAdviseService.doReject(auditHeader);
					if (aManualAdvise.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_ManualAdviseDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_ManualAdviseDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.manualAdvise), true);
				}
			}

			if (retValue == PennantConstants.porcessOVERIDE) {
				auditHeader.setOveride(true);
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);
			}
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	protected void appendAccountingDetailTab(ManualAdvise manualAdvise, boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);

		boolean createTab = false;
		if (getTab(AssetConstants.UNIQUE_ID_ACCOUNTING) == null) {
			createTab = true;
		}

		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_ACCOUNTING, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_ACCOUNTING);
		}

		Tabpanel tabpanel = getTabpanel(AssetConstants.UNIQUE_ID_ACCOUNTING);
		if (tabpanel != null) {
			tabpanel.setHeight(getListBoxHeight(7));

		}
		if (!onLoadProcess) {
			final Map<String, Object> map = new HashMap<>();
			map.put("manualAdvise", manualAdvise);
			map.put("acSetID", accountsetId);
			map.put("enqModule", enqiryModule);
			map.put("dialogCtrl", this);
			map.put("isNotFinanceProcess", false);
			map.put("postAccReq", false);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AccountingDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_ACCOUNTING), map);
			Tab tab = getTab(AssetConstants.UNIQUE_ID_ACCOUNTING);
			if (tab != null) {
				tab.setVisible(true);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Executing Accountng Details
	 */
	public void executeAccounting() {
		logger.debug(Literal.ENTERING);

		List<ReturnDataSet> accountingSetEntries = this.manualAdviseService.getAccountingSetEntries(this.manualAdvise);

		if (accountingDetailDialogCtrl != null) {
			accountingDetailDialogCtrl.doFillAccounting(accountingSetEntries);
			setAccountingExecuted(true);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Document Details Data
	 */
	private void appendDocumentDetailTab() {
		logger.debug(Literal.ENTERING);
		createTab(AssetConstants.UNIQUE_ID_DOCUMENTDETAIL, true);
		final HashMap<String, Object> map = getDefaultArguments();
		map.put("documentDetails", this.manualAdvise.getDocumentDetails());
		map.put("module", DocumentCategories.MANUAL_ADVISE_PAYABLE.getKey());
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DocumentDetailDialog.zul",
				getTabpanel(AssetConstants.UNIQUE_ID_DOCUMENTDETAIL), map);
		logger.debug(Literal.LEAVING);
	}

	private boolean validateAccounting(boolean validate) {
		if (this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("Cancel")
				|| this.userAction.getSelectedItem().getLabel().contains("Reject")
				|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")) {
			validate = false;
		} else {
			validate = true;
		}
		return validate;
	}

	private Tab getTab(String id) {
		return (Tab) tabsIndexCenter.getFellowIfAny(getTabID(id));
	}

	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(getTabpanelID(id));
	}

	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
	}

	/**
	 * This method will create tab and will assign corresponding tab selection method and makes tab visibility based on
	 * parameter
	 * 
	 * @param moduleID
	 * @param tabVisible
	 */
	public void createTab(String moduleID, boolean tabVisible) {
		logger.debug(Literal.ENTERING);
		String tabName = Labels.getLabel("tab_label_" + moduleID);
		Tab tab = new Tab(tabName);
		tab.setId(getTabID(moduleID));
		tab.setVisible(tabVisible);
		tabsIndexCenter.appendChild(tab);
		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId(getTabpanelID(moduleID));
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight("100%");
		ComponentsCtrl.applyForward(tab, "onSelect=" + selectMethodName);
		logger.debug(Literal.LEAVING);
	}

	private void clearTabpanelChildren(String id) {
		Tabpanel tabpanel = getTabpanel(id);
		if (tabpanel != null) {
			tabpanel.setStyle("overflow:auto;");
			tabpanel.getChildren().clear();
		}
	}

	public void onSelectTab(ForwardEvent event) {
		Tab tab = (Tab) event.getOrigin().getTarget();
		logger.debug(tab.getId() + " --> " + Literal.ENTERING);
		String module = getIDbyTab(tab.getId());
		doClearMessage();

		if (StringUtils.equals(module, AssetConstants.UNIQUE_ID_ACCOUNTING)) {
			doWriteComponentsToBean(manualAdvise);
			appendAccountingDetailTab(this.manualAdvise, false);
		}
	}

	private String getIDbyTab(String tabID) {
		return tabID.replace("TAB", "");
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(ManualAdvise aManualAdvise, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aManualAdvise.getBefImage(), aManualAdvise);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aManualAdvise.getUserDetails(),
				getOverideMap());
	}

	public HashMap<String, Object> getDefaultArguments() {
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);
		map.put("isNotFinanceProcess", true);
		map.put("moduleName", DocumentCategories.MANUAL_ADVISE_PAYABLE.getKey());
		map.put("enqiryModule", enqiryModule);
		map.put("finHeaderList", getHeaderBasicDetails());
		map.put("isEditable", !isReadOnly("button_" + this.pageRightName + "_btnNewDocuments"));
		return map;
	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private ArrayList<Object> getHeaderBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		arrayList.add(0, this.lbl_LoanReference.getValue());
		arrayList.add(1, this.lbl_LoanType.getValue());
		arrayList.add(2, this.lbl_CustCIF.getValue());
		arrayList.add(3, this.lbl_FinAmount.getValue());
		arrayList.add(4, this.lbl_startDate.getValue());
		arrayList.add(5, this.lbl_MaturityDate.getValue());
		return arrayList;
	}

	private boolean isValidPayableLink(String linkTo, int adviseType) {
		if (AdviseType.isReceivable(adviseType) || linkTo == null) {
			return false;
		}

		switch (linkTo) {
		case Allocation.PFT:
		case Allocation.PRI:
		case Allocation.MANADV:
		case Allocation.BOUNCE:
		case Allocation.ODC:
		case Allocation.LPFT:
			return true;
		default:
			return false;
		}
	}

	private void setEligibleAmount(FeeType ft) {
		String linkTo = ft.getPayableLinkTo();

		BigDecimal amount = BigDecimal.ZERO;
		if (Allocation.ADHOC.equals(linkTo)) {
			amount = BigDecimal.ZERO;
		} else if (isValidPayableLink(linkTo, ft.getAdviseType())) {
			ManualAdvise ma = new ManualAdvise();

			ma.setFinID(manualAdvise.getFinID());
			ma.setFinReference(manualAdvise.getFinReference());
			ma.setValueDate(this.valueDate.getValue());

			amount = manualAdviseService.getEligibleAmount(ma, ft);
			amount = PennantApplicationUtil.formateAmount(amount, PennantConstants.defaultCCYDecPos);
		}

		this.eligibleAmount.setValue(amount);
	}

	private String validatePayableAmount(final ManualAdvise ma) {
		Radio item = this.userAction.getSelectedItem();
		if (!isValidUserAction(item)) {
			return null;
		}

		Object dataObject = feeTypeID.getObject();
		FeeType feeType = (FeeType) dataObject;

		if (feeType == null || !isValidPayableLink(feeType.getPayableLinkTo(), ma.getAdviseType())) {
			return null;
		}

		long finID = ma.getFinID();
		long fee = feeType.getFeeTypeID();
		String linkTo = feeType.getPayableLinkTo();

		if (manualAdviseService.isDuplicatePayble(finID, fee, linkTo) && !item.getLabel().contains("Approve")
				&& ma.isNewRecord()) {
			return Labels.getLabel("label_Payable_Maintenance");
		}

		BigDecimal advAmount = ma.getAdviseAmount();
		BigDecimal eblAmount = ma.getEligibleAmount();

		if (advAmount == null) {
			advAmount = BigDecimal.ZERO;
		}

		if (eblAmount == null) {
			eblAmount = BigDecimal.ZERO;
		}

		if (advAmount.compareTo(eblAmount) > 0) {
			eblAmount = PennantApplicationUtil.formateAmount(eblAmount, PennantConstants.defaultCCYDecPos);

			String[] params = new String[] { Labels.getLabel("label_ManualAdviseDialog_AdviseAmount.value"),
					Labels.getLabel("label_ManualAdviseDialog_Eligible_Amount.value") };
			return Labels.getLabel("label_ManualAdviseDialog_Eligible_Amount.validation", params);
		}

		if (manualAdviseService.isPaybleExist(finID, feeType.getFeeTypeID(), linkTo)) {
			return Labels.getLabel("label_Payable_DuplicateIntrest");
		}

		return null;
	}

	private boolean isValidUserAction(Radio item) {
		if (item == null) {
			return false;
		}

		switch (item.getLabel().toUpperCase()) {
		case "CANCEL":
		case "REJECT":
		case "RESUBMIT":
			return false;
		default:
			return true;
		}
	}

	public void setManualAdviseService(ManualAdviseService manualAdviseService) {
		this.manualAdviseService = manualAdviseService;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public FinFeeDetailService getFinFeeDetailService() {
		return finFeeDetailService;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	public FinanceTaxDetailService getFinanceTaxDetailService() {
		return financeTaxDetailService;
	}

	public void setFinanceTaxDetailService(FinanceTaxDetailService financeTaxDetailService) {
		this.financeTaxDetailService = financeTaxDetailService;
	}

	public AccountingDetailDialogCtrl getAccountingDetailDialogCtrl() {
		return accountingDetailDialogCtrl;
	}

	public void setAccountingDetailDialogCtrl(AccountingDetailDialogCtrl accountingDetailDialogCtrl) {
		this.accountingDetailDialogCtrl = accountingDetailDialogCtrl;
	}

	public boolean isAccountingExecuted() {
		return isAccountingExecuted;
	}

	public void setAccountingExecuted(boolean isAccountingExecuted) {
		this.isAccountingExecuted = isAccountingExecuted;
	}

	public DocumentDetailDialogCtrl getDocumentDetailDialogCtrl() {
		return documentDetailDialogCtrl;
	}

	public void setDocumentDetailDialogCtrl(DocumentDetailDialogCtrl documentDetailDialogCtrl) {
		this.documentDetailDialogCtrl = documentDetailDialogCtrl;
	}

	public FinanceCheckListReferenceDialogCtrl getFinanceCheckListReferenceDialogCtrl() {
		return financeCheckListReferenceDialogCtrl;
	}

	public void setFinanceCheckListReferenceDialogCtrl(
			FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl) {
		this.financeCheckListReferenceDialogCtrl = financeCheckListReferenceDialogCtrl;
	}
}

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
 * * FileName : FeePostingsDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-06-2013 * *
 * Modified Date : 21-06-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-05-2017 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.Fees.FeePostings;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.fees.FeePostings;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.fees.feepostings.FeePostingService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.limit.LimitDetailService;
import com.pennant.backend.service.rmtmasters.AccountingSetService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.accounting.AccountingUtil;
import com.pennant.pff.accounting.PostAgainst;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.AccountingDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.ScreenCTL;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.receipt.constants.Allocation;

/**
 * This is the controller class for the /WEB-INF/pages/others/JVPosting/jVPostingDialog.zul file.
 */
public class FeePostingsDialogCtrl extends GFCBaseCtrl<FeePostings> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(FeePostingsDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_feePostingsDialog;
	protected Tab tab_Accounting;
	protected Combobox postingAgainst;
	protected ExtendedCombobox reference;
	protected ExtendedCombobox feeTypeCode;
	protected Textbox remarks;
	protected CurrencyBox postingAmount;
	protected ExtendedCombobox postingCcy;
	protected Datebox postDate;
	protected Datebox valueDate;
	protected ExtendedCombobox postingDivision;

	private boolean enqModule = false;
	// not auto wired vars
	private transient FeePostingsListCtrl feePostingsListCtrl; // overhanded per

	protected Tabbox tabbox;
	protected Component jVSummaryEntryListPage;
	protected Component accountingEntryListPage;

	// ServiceDAOs / Domain Classes
	private transient FeePostingService feePostingService;
	private transient PostingsPreparationUtil postingsPreparationUtil;
	protected Textbox moduleType; // autowired

	private FeePostings feePostings;
	protected ExtendedCombobox partnerBankID;
	private Currency aCurrency = null;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Tab basicDetailsTab;
	protected Tabbox tabBoxIndexCenter;
	protected Tabs tabsIndexCenter;
	protected String selectMethodName = "onSelectTab";
	private transient AccountingDetailDialogCtrl accountingDetailDialogCtrl;
	private transient FinanceDetailService financeDetailService;
	private transient CustomerDetailsService customerDetailsService;
	private transient CollateralSetupService collateralSetupService;
	private transient LimitDetailService limitDetailService;
	private transient AccountingSetService accountingSetService;
	private AccountEngineExecution engineExecution;
	private boolean isAccountingExecuted = false;
	Date minReqPostingDate = DateUtil.addDays(SysParamUtil.getAppDate(),
			-SysParamUtil.getValueAsInt(SMTParameterConstants.FEE_POSTING_DATE_BACK_DAYS));

	/**
	 * default constructor.<br>
	 */
	public FeePostingsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FeePostingsDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected JVPosting object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_feePostingsDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_feePostingsDialog);

		try {
			// READ OVERHANDED params !
			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}

			if (arguments.containsKey("FeePostingsListCtrl")) {
				setFeePostingsListCtrl((FeePostingsListCtrl) arguments.get("FeePostingsListCtrl"));
			} else {
				setFeePostingsListCtrl(null);
			}
			// READ OVERHANDED params !
			if (arguments.containsKey("feePostings")) {
				this.feePostings = (FeePostings) arguments.get("feePostings");
				FeePostings befImage = new FeePostings();
				BeanUtils.copyProperties(this.feePostings, befImage);
				this.feePostings.setBefImage(befImage);
				setFeePostings(this.feePostings);
			} else {
				setFeePostings(null);
			}
			doLoadWorkFlow(this.feePostings.isWorkflow(), this.feePostings.getWorkflowId(),
					this.feePostings.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "FeePostingsDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			aCurrency = PennantAppUtil.getCurrencyBycode(SysParamUtil.getValueAsString("EXT_BASE_CCY"));
			getFeePostings().setCurrency(aCurrency.getCcyCode());

			/* set components visible dependent of the users rights */
			doCheckRights();

			doSetFieldProperties();
			// this.listBoxJVPostingAccounting.setHeight(this.borderLayoutHeight - 350 + "px");
			doShowDialog(getFeePostings());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_feePostingsDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnDelete(Event event) {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnSave(Event event) {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		// doWriteBeanToComponents(this.feePostings.getBefImage());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_feePostingsDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	protected void doPostClose() {
		getFeePostingsListCtrl().refreshList();
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug("Entering" + event.toString());
		try {

			ScreenCTL.displayNotes(
					getNotes("JVPosting", String.valueOf(getFeePostings().getPostId()), getFeePostings().getVersion()),
					this);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());

	}

	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFeePostings
	 */
	public void doShowDialog(FeePostings aFeePostings) {
		logger.debug("Entering");
		// set Read only mode accordingly if the object is new or not.
		if (aFeePostings.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aFeePostings.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly(true);
				btnCancel.setVisible(false);
			}
			if (enqModule) {
				doReadOnly(true);
				this.btnSave.setVisible(false);
				this.btnNotes.setVisible(false);
				this.btnDelete.setVisible(false);
				this.groupboxWf.setVisible(false);
			}
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aFeePostings);

			// set ReadOnly mode accordingly if the object is new or not.
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_feePostingsDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getFeePostings().isNewRecord()) {
			this.btnCancel.setVisible(false);

		} else {
			this.btnCancel.setVisible(true);
			this.postDate.setDisabled(isReadOnly("FeePostingsDialog_postingDate"));
		}

		this.postingAgainst.setDisabled(isReadOnly("FeePostingsDialog_postAgainst"));
		this.reference.setReadonly(isReadOnly("FeePostingsDialog_reference"));
		this.feeTypeCode.setReadonly(isReadOnly("FeePostingsDialog_feeTypeCode"));
		this.partnerBankID.setReadonly(isReadOnly("FeePostingsDialog_partnerbankId"));
		this.postingAmount.setReadonly(isReadOnly("FeePostingsDialog_postingAmount"));
		this.postingCcy.setReadonly(isReadOnly("FeePostingsDialog_postingCurrency"));
		this.valueDate.setDisabled(isReadOnly("FeePostingsDialog_valueDate"));
		this.postingDivision.setReadonly(isReadOnly("FeePostingsDialog_postingDivision"));
		this.remarks.setDisabled(isReadOnly("FeePostingsDialog_remarks"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.feePostings.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			// btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Executing Accountng Details
	 */
	public void executeAccounting() {
		logger.debug("Entering");

		List<ReturnDataSet> accountingSetEntries = new ArrayList<ReturnDataSet>();

		getFeePostings().setPostingAmount(
				CurrencyUtil.unFormat(this.postingAmount.getActualValue(), aCurrency.getCcyEditField()));
		AEEvent aeEvent = new AEEvent();
		aeEvent.setAccountingEvent(AccountingEvent.MANFEE);
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}

		String feeReference = getFeePostings().getReference();
		switch (PostAgainst.object(getFeePostings().getPostAgainst())) {
		case LOAN:
			FinanceMain fm = financeDetailService.getFinanceMain(feeReference, TableType.MAIN_TAB);
			amountCodes.setFinType(fm.getFinType());
			aeEvent.setBranch(fm.getFinBranch());
			aeEvent.setCustID(fm.getCustID());
			break;
		case CUSTOMER:
			Customer customer = customerDetailsService.getCustomerByCIF(feeReference);
			aeEvent.setBranch(customer.getCustDftBranch());
			aeEvent.setCustID(customer.getCustID());
			break;
		case COLLATERAL:
			CollateralSetup collateralSetup = collateralSetupService.getApprovedCollateralSetupById(feeReference);
			aeEvent.setCustID(collateralSetup.getDepositorId());
			break;
		case LIMIT:
			LimitHeader header = limitDetailService.getCustomerLimits(Long.valueOf(feeReference));
			aeEvent.setCustID(header.getCustomerId());
			break;
		default:
			break;
		}

		amountCodes.setPartnerBankAc(getFeePostings().getPartnerBankAc());
		amountCodes.setPartnerBankAcType(getFeePostings().getPartnerBankAcType());

		aeEvent.setCcy(getFeePostings().getCurrency());
		aeEvent.setFinReference(getFeePostings().getReference());
		aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());

		getFeePostings().getDeclaredFieldValues(aeEvent.getDataMap());
		aeEvent.getAcSetIDList().add(Long.valueOf(getFeePostings().getAccountSetId()));

		engineExecution.getAccEngineExecResults(aeEvent);
		List<ReturnDataSet> returnSetEntries = aeEvent.getReturnDataSet();

		getFeePostings().setReturnDataSetList(returnSetEntries);
		accountingSetEntries.addAll(returnSetEntries);
		if (accountingDetailDialogCtrl != null) {
			accountingDetailDialogCtrl.doFillAccounting(accountingSetEntries);
			isAccountingExecuted = true;
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	protected void appendAccountingDetailTab(boolean onLoadProcess) {
		logger.debug("Entering");
		boolean createTab = false;
		if (getTab(AssetConstants.UNIQUE_ID_ACCOUNTING) == null) {
			createTab = true;
		}

		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_ACCOUNTING, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_ACCOUNTING);
		}
		if (!onLoadProcess) {

			final Map<String, Object> map = getDefaultArguments();
			map.put("feePosting", getFeePostings());
			if (getFeePostings().getAccountSetId() != null) {
				map.put("acSetID", Long.valueOf(getFeePostings().getAccountSetId()));
			}

			if (enqiryModule) {
				map.put("enqModule", true);
			}
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AccountingDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_ACCOUNTING), map);
			Tab tab = getTab(AssetConstants.UNIQUE_ID_ACCOUNTING);
			if (tab != null) {
				tab.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	public Map<String, Object> getDefaultArguments() {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("dialogCtrl", this);
		map.put("isNotFinanceProcess", true);
		map.put("postAccReq", false);
		return map;
	}

	/**
	 * This method will create tab and will assign corresponding tab selection method and makes tab visibility based on
	 * parameter
	 * 
	 * @param moduleID
	 * @param tabVisible
	 */
	public void createTab(String moduleID, boolean tabVisible) {
		logger.debug("Entering");
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
		ComponentsCtrl.applyForward(tab, ("onSelect=" + selectMethodName));
		logger.debug("Leaving");
	}

	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {

		logger.debug("Entering");

		this.postingAgainst.setDisabled(true);
		this.reference.setReadonly(true);
		this.feeTypeCode.setReadonly(true);
		this.partnerBankID.setReadonly(true);
		this.postingAmount.setReadonly(true);
		this.postingCcy.setReadonly(true);
		this.postDate.setDisabled(true);
		this.valueDate.setDisabled(true);
		this.remarks.setDisabled(true);
		this.postingDivision.setReadonly(true);

		logger.debug("Leaving");

	}

	// Helpers

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		if (!enqModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FeePostingsDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FeePostingsDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FeePostingsDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FeePostingsDialog_btnSave"));

		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {

		this.reference.setMandatoryStyle(true);
		this.feeTypeCode.setMandatoryStyle(true);
		this.feeTypeCode.setModuleName("FeeType");
		this.feeTypeCode.setValueColumn("FeeTypeCode");
		this.feeTypeCode.setDescColumn("FeeTypeDesc");
		this.feeTypeCode.setValidateColumns(new String[] { "FeeTypeCode" });

		ArrayList<String> list = new ArrayList<>();
		list.add(Allocation.BOUNCE);
		list.add(Allocation.ODC);
		list.add(Allocation.LPFT);

		Filter[] filters = new Filter[1];
		filters[0] = Filter.notIn("FeeTypeCode", list);
		feeTypeCode.setFilters(filters);

		this.postingCcy.setMaxlength(LengthConstants.LEN_CURRENCY);
		this.postingCcy.setMandatoryStyle(true);
		this.postingCcy.setModuleName("Currency");
		this.postingCcy.setValueColumn("CcyCode");
		this.postingCcy.setDescColumn("CcyDesc");
		this.postingCcy.setValidateColumns(new String[] { "CcyCode" });

		this.partnerBankID.setModuleName("PartnerBank");
		this.partnerBankID.setValueColumn("PartnerBankCode");
		this.partnerBankID.setDescColumn("PartnerBankName");
		this.partnerBankID.setValidateColumns(new String[] { "PartnerBankCode", "PartnerBankName" });
		this.partnerBankID.setMandatoryStyle(true);

		this.postDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.postDate.setDisabled(true);
		this.valueDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.postingDivision.setMandatoryStyle(true);
		this.postingDivision.setModuleName("DivisionDetail");
		this.postingDivision.setValueColumn("DivisionCode");
		this.postingDivision.setDescColumn("DivisionCodeDesc");
		this.postingDivision.setValidateColumns(new String[] { "DivisionCode" });

		this.postingAmount.setProperties(true, aCurrency.getCcyEditField());

	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aJVPosting JVPosting
	 */
	public void doWriteBeanToComponents(FeePostings aFeePostings) {
		logger.debug("Entering");
		fillComboBox(this.postingAgainst, aFeePostings.getPostAgainst(), AccountingUtil.getpostingPurposeList(), "");
		this.reference.setValue(aFeePostings.getReference());
		this.feeTypeCode.setValue(
				aFeePostings.isNewRecord() ? aFeePostings.getFeeTyeCode() : aFeePostings.getFeeTyeCode().trim());
		this.postingAmount.setValue(CurrencyUtil.parse(aFeePostings.getPostingAmount(), aCurrency.getCcyEditField()));
		this.postingDivision.setValue(aFeePostings.getPostingDivision(), aFeePostings.getDivisionCodeDesc());
		this.postingCcy.setValue(aFeePostings.getCurrency());
		if (aFeePostings.isNewRecord()) {
			this.postDate.setValue(SysParamUtil.getAppDate());
			this.valueDate.setValue(SysParamUtil.getAppDate());
		} else {
			this.postDate.setValue(aFeePostings.getPostDate());
			this.valueDate.setValue(aFeePostings.getValueDate());
		}

		if (!aFeePostings.isNewRecord()) {
			this.partnerBankID.setObject(new PartnerBank(aFeePostings.getPartnerBankId()));
			this.partnerBankID.setValue(String.valueOf(aFeePostings.getPartnerBankId()));
			this.partnerBankID.setDescription(aFeePostings.getPartnerBankName());
		}

		this.remarks.setValue(aFeePostings.getRemarks());
		setFilters(StringUtils.equals(null, aFeePostings.getPostAgainst()) ? aFeePostings.getPostAgainst()
				: aFeePostings.getPostAgainst().trim());
		appendAccountingDetailTab(true);
		this.recordStatus.setValue(aFeePostings.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aJVPosting
	 */
	public void doWriteComponentsToBean(FeePostings aFeePostings) {

		logger.debug("Entering");
		// doSetValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		doSetValidation();

		// Posting Against
		try {
			if (getComboboxValue(this.postingAgainst).equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.postingAgainst, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_feePostingsDialog_PostingAgainst.value") }));
			}
			aFeePostings.setPostAgainst(getComboboxValue(this.postingAgainst));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFeePostings.setReference(this.reference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFeePostings.setFeeTyeCode(this.feeTypeCode.getValue().trim());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFeePostings.setPostingDivision(this.postingDivision.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Partner Bank ID
		try {
			PartnerBank partnerBank = (PartnerBank) this.partnerBankID.getObject();
			aFeePostings.setPartnerBankId(partnerBank.getPartnerBankId());
			aFeePostings.setPartnerBankName(this.partnerBankID.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFeePostings.setPostingAmount(
					CurrencyUtil.unFormat(this.postingAmount.getActualValue(), aCurrency.getCcyEditField()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFeePostings.setCurrency(this.postingCcy.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFeePostings.setPostDate(this.postDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!enqModule) {
				if ((this.valueDate.getValue().before(minReqPostingDate)
						|| this.valueDate.getValue().after(SysParamUtil.getAppDate()))
						&& !this.valueDate.isDisabled()) {

					String minreqPostDate = DateUtil.formatToShortDate(minReqPostingDate);
					String currentDate = DateUtil.formatToShortDate(SysParamUtil.getAppDate());

					throw new WrongValueException(this.valueDate,
							Labels.getLabel("DATE_ALLOWED_RANGE_EQUAL",
									new String[] { Labels.getLabel("label_feePostingsDialog_ValueDate.value"),
											minreqPostDate, currentDate }));
				}

				aFeePostings.setValueDate(this.valueDate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFeePostings.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Basic Details Error Detail
		doRemoveValidation();
		showErrorDetails(wve, this.basicDetailsTab);

		logger.debug("Leaving");

	}

	private void doRemoveValidation() {
		this.postingDivision.setConstraint("");
		this.feeTypeCode.setConstraint("");
		this.postingAmount.setConstraint("");
		this.postDate.setConstraint("");
		this.valueDate.setConstraint("");
	}

	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
				if (i == 0) {
					Component comp = wvea[i].getComponent();
					if (comp instanceof HtmlBasedComponent) {
						Clients.scrollIntoView(comp);
					}
				}
				logger.debug(wvea[i]);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		this.partnerBankID.setConstraint(new PTStringValidator(
				Labels.getLabel("label_FinTypePartnerBankDialog_PartnerBankID.value"), null, true, true));
		this.feeTypeCode.setConstraint(
				new PTStringValidator(Labels.getLabel("label_feePostingsDialog_FeeTypeCode.value"), null, true, true));
		this.reference.setConstraint(
				new PTStringValidator(Labels.getLabel("label_feePostingsDialog_Reference.value"), null, true, true));
		if (this.postingAmount.isVisible() && !this.postingAmount.isReadonly()) {
			this.postingAmount.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_feePostingsDialog_PostingAmount.value"),
							aCurrency.getCcyEditField(), true, false));
		}

		if (!this.postDate.isDisabled()) {
			this.postDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_feePostingsDialog_PostDate.value"), true));
		}

		if (!this.postingDivision.isButtonDisabled()) {
			this.postingDivision.setConstraint(new PTStringValidator(
					Labels.getLabel("label_feePostingsDialog_PostingDivision.value"), null, true, true));
		}

		if (!this.valueDate.isDisabled()) {
			this.valueDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_feePostingsDialog_ValueDate.value"), true));
		}
	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		this.reference.setErrorMessage("");
		this.reference.setConstraint("");
		this.postingDivision.setErrorMessage("");
		this.postingDivision.setConstraint("");
		this.partnerBankID.setErrorMessage("");
		this.partnerBankID.setConstraint("");
	}

	public void onSelectTab(ForwardEvent event) {
		Tab tab = (Tab) event.getOrigin().getTarget();
		logger.debug(tab.getId() + " --> " + "Entering");
		String module = getIDbyTab(tab.getId());
		doClearMessage();

		if (StringUtils.equals(module, AssetConstants.UNIQUE_ID_ACCOUNTING)) {
			doWriteComponentsToBean(getFeePostings());
			if (StringUtils.isEmpty(getFeePostings().getAccountSetId())) {
				if (tab != null) {
					tab.setSelected(true);
				}
				MessageUtil.showError(
						"Accounting Set Not Configured for the Fee Code :" + getFeePostings().getFeeTyeCode());
				return;
			}
			appendAccountingDetailTab(false);
		}

	}

	private String getIDbyTab(String tabID) {
		return tabID.replace("TAB", "");
	}

	private Tab getTab(String id) {
		return (Tab) tabsIndexCenter.getFellowIfAny(getTabID(id));
	}

	private void clearTabpanelChildren(String id) {
		Tabpanel tabpanel = getTabpanel(id);
		if (tabpanel != null) {
			tabpanel.setStyle("overflow:auto;");
			tabpanel.getChildren().clear();
		}
	}

	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(getTabpanelID(id));
	}

	/**
	 * Deletes a JVPosting object from database.<br>
	 */
	private void doDelete() {

	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		logger.debug("Leaving");
	}

	public void onFulfill$feeTypeCode(Event event) {

		logger.debug("Entering");

		if (StringUtils.isBlank(this.feeTypeCode.getValue())) {
			this.feeTypeCode.setValue("", "");
		} else {
			FeeType feeType = (FeeType) this.feeTypeCode.getObject();

			this.feeTypeCode.setValue(feeType.getFeeTypeCode(), feeType.getFeeTypeDesc());
			if (feeType.getAccountSetId() != null) {
				getFeePostings().setAccountSetId(String.valueOf(feeType.getAccountSetId()));
			} else {
				getFeePostings().setAccountSetId(null);
			}

		}
		logger.debug("Leaving");

	}

	public void onFulfill$partnerBankID(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = partnerBankID.getObject();

		if (dataObject instanceof String) {
			this.partnerBankID.setValue(dataObject.toString());
			this.partnerBankID.setDescription("");
		} else {
			PartnerBank partnerbank = (PartnerBank) dataObject;
			if (partnerbank != null) {
				this.partnerBankID.setValue(String.valueOf(partnerbank.getPartnerBankCode()));
				this.partnerBankID.setDescription(partnerbank.getPartnerBankName());
				getFeePostings().setPartnerBankAc(partnerbank.getAccountNo());
				getFeePostings().setPartnerBankAcType(partnerbank.getAcType());
			}
		}

		logger.debug("Leaving");
	}

	public void onFulfill$reference(Event event) {

		logger.debug("Entering");

		if (StringUtils.isBlank(this.reference.getValue())) {
			this.reference.setValue("", "");

		} else {
			if (PostAgainst.isLoan(this.postingAgainst.getSelectedItem().getValue().toString())) {
				FinanceMain financeMain = (FinanceMain) this.reference.getObject();
				this.reference.setValue(financeMain.getFinReference(), financeMain.getFinType());
				this.postingDivision.setValue(financeMain.getLovDescFinDivision());
				this.postingDivision.setReadonly(true);
				this.partnerBankID.setValue("");
				this.partnerBankID.setDescription("");
				Filter[] partnerBank = new Filter[1];
				partnerBank[0] = new Filter("ENTITY", financeMain.getLovDescEntityCode(), Filter.OP_EQUAL);
				this.partnerBankID.setFilters(partnerBank);
			} else {
				this.postingDivision.setReadonly(false);
			}
		}
		logger.debug("Leaving");

	}

	public void onChange$postingAgainst(Event event) {
		doClearMessage();

		this.reference.setValue("", "");
		this.postingDivision.setValue("", "");
		this.postingDivision.setReadonly(false);
		this.partnerBankID.setValue("");
		this.partnerBankID.setDescription("");
		this.partnerBankID.setFilters(null);
		setFilters(this.postingAgainst.getSelectedItem().getValue().toString());
	}

	private void setFilters(String postValue) {
		if (StringUtils.equals(postValue, PennantConstants.List_Select)) {
			addFilters("", "", "");
		}
		if (PostAgainst.isLoan(postValue)) {
			addFilters("FinanceMain", "FinReference", "FinType");
		}
		if (PostAgainst.isCustomer(postValue)) {
			addFilters("Customer", "CustCIF", "CustShrtName");
		}
		if (PostAgainst.isCollateral(postValue)) {
			addFilters("CollateralSetup", "CollateralRef", "CollateralType");
		}
		if (PostAgainst.isLimit(postValue)) {
			addFilters("LimitHeader", "HeaderId", "ResponsibleBranch");
		}
	}

	private void addFilters(String modulename, String valuecolumn, String descColumn) {
		this.reference.setModuleName(modulename);
		this.reference.setValueColumn(valuecolumn);
		this.reference.setDescColumn(descColumn);
		this.reference.setValidateColumns(new String[] { valuecolumn });
	}

	/**
	 * Saves the components to table.
	 */
	public void doSave() {
		logger.debug("Entering");

		final FeePostings aFeePosting = new FeePostings();
		BeanUtils.copyProperties(getFeePostings(), aFeePosting);
		boolean isNew = false;
		boolean validate = false;

		doClearMessage();
		doSetValidation();
		// fill the FinanceType object with the components data
		doWriteComponentsToBean(aFeePosting);
		validate = validateAccounting(validate);

		// Accounting Details Validations
		if (validate && !isAccountingExecuted) {
			MessageUtil.showError(Labels.getLabel("label_Finance_Calc_Accountings"));
			return;
		}

		// if Accounting set not configured stop to save
		if (StringUtils.isEmpty(aFeePosting.getAccountSetId())) {
			Tab tab = getTab(AssetConstants.UNIQUE_ID_ACCOUNTING);
			if (tab != null) {
				tab.setSelected(true);
			}
			MessageUtil.showError("Accounting Set Not Configured for the Fee Code :" + aFeePosting.getFeeTyeCode());
			return;
		}

		// doStoreInitValues();
		isNew = aFeePosting.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFeePosting.getRecordType())) {
				aFeePosting.setVersion(aFeePosting.getVersion() + 1);
				if (isNew) {
					aFeePosting.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFeePosting.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFeePosting.setNewRecord(true);
				}
			}
		} else {
			aFeePosting.setVersion(aFeePosting.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			if (doProcess(aFeePosting, tranType)) {
				// List Detail Refreshment
				refreshList();

				// Confirmation message
				String msg = PennantApplicationUtil.getSavingStatus(aFeePosting.getRoleCode(),
						aFeePosting.getNextRoleCode(), aFeePosting.getReference(), " Fee Postings ",
						aFeePosting.getRecordStatus());
				if (StringUtils.equals(aFeePosting.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)) {
					msg = "Fee Postings with Reference " + aFeePosting.getReference() + " Approved Succesfully.";
				}
				Clients.showNotification(msg, "info", null, null, -1);

				closeDialog();
			}
		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");

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

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */
	protected void refreshList() {
		final JdbcSearchObject<FeePostings> soFeePostings = getFeePostingsListCtrl().getSearchObject();
		getFeePostingsListCtrl().pagingFeePostingList.setActivePage(0);
		getFeePostingsListCtrl().getPagedListWrapper().setSearchObject(soFeePostings);
		if (getFeePostingsListCtrl().listBoxFeePosting != null) {
			getFeePostingsListCtrl().listBoxFeePosting.getListModel();
		}
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * @throws InterfaceException
	 * 
	 */

	protected boolean doProcess(FeePostings aFeePostings, String tranType) throws InterfaceException {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aFeePostings.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFeePostings.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFeePostings.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aFeePostings.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFeePostings.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aFeePostings);
				}

				if (isNotesMandatory(taskId, aFeePostings)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
			} else {
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

			aFeePostings.setTaskId(taskId);
			aFeePostings.setNextTaskId(nextTaskId);
			aFeePostings.setRoleCode(getRole());
			aFeePostings.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFeePostings, tranType);

			String operationRefs = getServiceOperations(taskId, aFeePostings);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFeePostings, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aFeePostings, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * @throws InterfaceException
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws InterfaceException {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		FeePostings aFeePostings = (FeePostings) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getFeePostingService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getFeePostingService().saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getFeePostingService().doApprove(auditHeader);
					if (aFeePostings.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getFeePostingService().doReject(auditHeader);
					if (aFeePostings.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_feePostingsDialog, auditHeader);
					return processCompleted;
				}
			}
			auditHeader = ErrorControl.showErrorDetails(this.window_feePostingsDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;
				if (deleteNotes) {
					deleteNotes(getNotes(this.feePostings), true);
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
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(FeePostings aFeePostings, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFeePostings.getBefImage(), aFeePostings);
		return new AuditHeader(Long.toString(aFeePostings.getPostId()), null, null, null, auditDetail,
				aFeePostings.getUserDetails(), getOverideMap());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public Window getWindow_JVPostingDialog() {
		return window_feePostingsDialog;
	}

	public void setWindow_JVPostingDialog(Window windowJVPostingDialog) {
		this.window_feePostingsDialog = windowJVPostingDialog;
	}

	public FeePostingsListCtrl getFeePostingsListCtrl() {
		return feePostingsListCtrl;
	}

	public void setFeePostingsListCtrl(FeePostingsListCtrl feePostingsListCtrl) {
		this.feePostingsListCtrl = feePostingsListCtrl;
	}

	public PostingsPreparationUtil getPostingsPreparationUtil() {
		return postingsPreparationUtil;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public FeePostings getFeePostings() {
		return feePostings;
	}

	public void setFeePostings(FeePostings feePostings) {
		this.feePostings = feePostings;
	}

	public AccountingDetailDialogCtrl getAccountingDetailDialogCtrl() {
		return accountingDetailDialogCtrl;
	}

	public void setAccountingDetailDialogCtrl(AccountingDetailDialogCtrl accountingDetailDialogCtrl) {
		this.accountingDetailDialogCtrl = accountingDetailDialogCtrl;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public AccountingSetService getAccountingSetService() {
		return accountingSetService;
	}

	public void setAccountingSetService(AccountingSetService accountingSetService) {
		this.accountingSetService = accountingSetService;
	}

	public FeePostingService getFeePostingService() {
		return feePostingService;
	}

	public void setFeePostingService(FeePostingService feePostingService) {
		this.feePostingService = feePostingService;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public CollateralSetupService getCollateralSetupService() {
		return collateralSetupService;
	}

	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}

	public LimitDetailService getLimitDetailService() {
		return limitDetailService;
	}

	public void setLimitDetailService(LimitDetailService limitDetailService) {
		this.limitDetailService = limitDetailService;
	}

}

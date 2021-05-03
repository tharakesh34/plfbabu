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
package com.pennant.webui.verification.tv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.webui.collateral.collateralsetup.CollateralBasicDetailsCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.verification.technicalverification.model.TechnicalVerificationListModelItemRenderer;
import com.pennanttech.dataengine.util.DateUtil.DateFormat;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.verification.Agencies;
import com.pennanttech.pennapps.pff.verification.VerificationCategory;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.model.TechnicalVerification;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pennapps.pff.verification.service.TechnicalVerificationService;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Verification/TechnicalVerificationInitiationList.zul file.
 * 
 */
public class TVInitiationListCtrl extends GFCBaseListCtrl<TechnicalVerification> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(TVInitiationListCtrl.class);

	protected Window window_TechnicalVerificationInitiation;
	protected Borderlayout borderLayout_TechnicalVerificationList;
	protected Paging pagingTechnicalVerificationList;
	protected Listbox listBoxTechnicalVerification;

	// List headers
	protected Listheader listheader_CIF;
	protected Listheader listheader_CollateralType;
	protected Listheader listheader_CollateralReference;
	protected Listheader listheader_LoanReference;
	protected Listheader listheader_Agency;
	protected Listheader listheader_CreadtedOn;

	protected Button button_TechnicalVerificationList_NewTechnicalVerification;
	protected Button button_TechnicalVerificationList_TechnicalVerificationSearch;

	// Search Fields
	protected Listbox sortOperator_CIF;
	protected Listbox sortOperator_CollateralType;
	protected Listbox sortOperator_CollateralReference;
	protected Listbox sortOperator_LoanReference;
	protected Listbox sortOperator_Agency;
	protected Listbox sortOperator_CreadtedOn;

	protected Textbox cif;
	protected Textbox collateralType;
	protected Textbox collateralReference;
	protected Textbox loanReference;
	protected ExtendedCombobox agency;
	protected Datebox CreadtedOn;

	private boolean isFromCollateralSetUp;
	protected Grid searchGrid;
	protected Div headerArea;

	@Autowired
	private transient TechnicalVerificationService technicalVerificationService;
	@Autowired
	private transient FinanceDetailService financeDetailService;
	private CollateralBasicDetailsCtrl collateralBasicDetailsCtrl;
	private FinanceDetail financeDetail;

	/**
	 * default constructor.<br>
	 */
	public TVInitiationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "TechnicalVerification";
		super.pageRightName = "TechnicalVerificationList";
		super.tableName = "Verification_Tv_View";
		super.queueTableName = "Verification_Tv_View";
		super.enquiryTableName = "Verification_Tv_View";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		this.searchObject.addFilter(new Filter("recordType", "", Filter.OP_NOT_EQUAL));
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_TechnicalVerificationInitiation(Event event) {
		logger.debug(Literal.ENTERING);

		doSetFieldProperties();
		setPageComponents(window_TechnicalVerificationInitiation, borderLayout_TechnicalVerificationList,
				listBoxTechnicalVerification, pagingTechnicalVerificationList);

		// Register buttons and fields.
		registerButton(button_TechnicalVerificationList_NewTechnicalVerification,
				"button_TechnicalVerificationList_NewTechnicalVerification", true);
		registerButton(button_TechnicalVerificationList_TechnicalVerificationSearch);

		setItemRender(new TechnicalVerificationListModelItemRenderer());

		// Register buttons and fields.
		registerField("cif", listheader_CIF, SortOrder.ASC, cif, sortOperator_CIF, Operators.STRING);
		registerField("collateralType", listheader_CollateralType, SortOrder.ASC, collateralType,
				sortOperator_CollateralType, Operators.STRING);
		registerField("collateralRef", listheader_CollateralReference, SortOrder.ASC, collateralReference,
				sortOperator_CollateralReference, Operators.STRING);
		registerField("keyReference", listheader_LoanReference, SortOrder.ASC, loanReference,
				sortOperator_LoanReference, Operators.STRING);
		registerField("agencyName", listheader_Agency, SortOrder.ASC, agency, sortOperator_Agency, Operators.DEFAULT);
		registerField("createdOn", listheader_CreadtedOn, SortOrder.NONE, CreadtedOn, sortOperator_CreadtedOn,
				Operators.DATE);
		registerField("verificationId");
		registerField("custName");

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		// Agency
		this.agency.setMaxlength(50);
		this.agency.setTextBoxWidth(120);
		this.agency.setModuleName("VerificationAgencies");
		this.agency.setValueColumn("DealerName");
		this.agency.setDescColumn("DealerCity");
		this.agency.setValidateColumns(new String[] { "DealerName", "DealerCity" });
		Filter[] agencyFilter = new Filter[1];
		agencyFilter[0] = new Filter("DealerType", Agencies.TVAGENCY.getKey(), Filter.OP_EQUAL);
		agency.setFilters(agencyFilter);
		this.CreadtedOn.setFormat(DateFormat.SHORT_DATE.getPattern());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_TechnicalVerificationList_TechnicalVerificationSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onTechnicalVerificationItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxTechnicalVerification.getSelectedItem();
		long id = (long) selectedItem.getAttribute("id");
		TechnicalVerification tv = technicalVerificationService.getTechnicalVerification(id, "_View");

		if (tv == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		if (tv.getVerificationCategory() == VerificationCategory.ONEPAGER.getKey()) {
			technicalVerificationService.getDocumentImage(tv);
		}

		setFinanceDetail(financeDetailService.getVerificationInitiationDetails(tv.getKeyReference(),
				VerificationType.TV, "_View"));

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  Id =? ");

		if (doCheckAuthority(tv, whereCond.toString(), new Object[] { tv.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && tv.getWorkflowId() == 0) {
				tv.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(tv);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_TechnicalVerificationList_NewTechnicalVerification(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		TechnicalVerification technicalVerification = new TechnicalVerification();
		technicalVerification.setNewRecord(true);
		technicalVerification.setWorkflowId(getWorkFlowId());

		Map<String, Object> arg = new HashMap<>();
		arg.put("technicalVerification", technicalVerification);
		arg.put("tvInitiationListCtrl", this);
		arg.put("enqiryModule", enqiryModule);
		arg.put("isFromCollateralSetUp", isFromCollateralSetUp);
		arg.put("module", VerificationType.TV.getValue());
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/SelectTVInitiationDialog.zul",
					null, arg);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param fieldinvestigation
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(TechnicalVerification technicalVerification) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> map = getDefaultArguments();
		if (getFinanceDetail().getTvVerification() == null) {
			getFinanceDetail().setTvVerification(new Verification());
		}
		map.put("tvInitiationListCtrl", this);
		map.put("finHeaderList", getFinBasicDetails());
		map.put("verification", getFinanceDetail().getTvVerification());
		map.put("financeDetail", getFinanceDetail());

		map.put("InitType", true);
		map.put("userRole", getRole());
		map.put("moduleDefiner", "");
		map.put("enqiryModule", true);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/TVInitiation.zul", null, map);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public Map<String, Object> getDefaultArguments() {

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);
		map.put("finHeaderList", getFinBasicDetails());
		map.put("financeDetail", getFinanceDetail());
		map.put("isFinanceProcess", false);
		map.put("ccyFormatter",
				CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()));
		return map;
	}

	private ArrayList<Object> getFinBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		arrayList.add(0, financeMain.getFinType());
		arrayList.add(1, financeMain.getFinCcy());
		arrayList.add(2, "");
		arrayList.add(3, financeMain.getFinReference());
		arrayList.add(4, "");
		arrayList.add(5, financeMain.getGrcPeriodEndDate());
		arrayList.add(6, financeMain.isAllowGrcCpz());
		if (StringUtils.isNotEmpty(getFinanceDetail().getFinScheduleData().getFinanceType().getProduct())) {
			arrayList.add(7, true);
		} else {
			arrayList.add(7, false);
		}
		arrayList.add(8, getFinanceDetail().getFinScheduleData().getFinanceType().getFinCategory());
		String custShrtName = "";
		if (getFinanceDetail().getCustomerDetails() != null
				&& getFinanceDetail().getCustomerDetails().getCustomer() != null) {
			custShrtName = getFinanceDetail().getCustomerDetails().getCustomer().getCustShrtName();
		}

		arrayList.add(9, custShrtName);
		arrayList.add(10, financeMain.isNewRecord());
		arrayList.add(11, "");
		/* arrayList.add(12, getFinanceDetail().getFinScheduleData().getFinanceMain().getFlexiType()); */
		return arrayList;
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 */
	public void onCheck$fromApproved(Event event) {
		search();
	}

	/**
	 * When user clicks on "fromWorkFlow"
	 * 
	 * @param event
	 */
	public void onCheck$fromWorkFlow(Event event) {
		search();
	}

	public void onClick$btnClose(Event event) {
		this.window_TechnicalVerificationInitiation.onClose();
	}

	public TechnicalVerificationService getTechnicalVerificationService() {
		return technicalVerificationService;
	}

	public void setTechnicalVerificationService(TechnicalVerificationService technicalVerificationService) {
		this.technicalVerificationService = technicalVerificationService;
	}

	public CollateralBasicDetailsCtrl getCollateralBasicDetailsCtrl() {
		return collateralBasicDetailsCtrl;
	}

	public void setCollateralBasicDetailsCtrl(CollateralBasicDetailsCtrl collateralBasicDetailsCtrl) {
		this.collateralBasicDetailsCtrl = collateralBasicDetailsCtrl;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

}
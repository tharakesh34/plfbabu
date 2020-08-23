package com.pennant.webui.verification.legalvetting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
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
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.verification.legalvetting.model.LegalVettingListModelItemRender;
import com.pennanttech.dataengine.util.DateUtil.DateFormat;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.verification.Agencies;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.model.LegalVetting;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pennapps.pff.verification.service.LegalVettingService;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class LegalVettingInitiationListCtrl extends GFCBaseListCtrl<LegalVetting> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(LegalVettingInitiationListCtrl.class);

	protected Window window_LegalVettingInitiation;
	protected Borderlayout borderLayout_LegalVettingList;
	protected Paging pagingLegalVettingList;
	protected Listbox listBoxLegalVetting;

	// List headers
	protected Listheader listheader_CIF;
	protected Listheader listheader_CollateralType;
	protected Listheader listheader_CollateralReference;
	protected Listheader listheader_LoanReference;
	protected Listheader listheader_Agency;
	protected Listheader listheader_VerificationCategory;
	protected Listheader listheader_CreatedOn;

	// checkRights
	protected Button button_LegalVettingList_LegalVettingSearch;
	protected Button button_LegalVettingList_NewLegalVetting;

	// Search Fields
	protected Listbox sortOperator_CIF;
	protected Listbox sortOperator_CollateralType;
	protected Listbox sortOperator_CollateralReference;
	protected Listbox sortOperator_LoanReference;
	protected Listbox sortOperator_Agency;
	protected Listbox sortOperator_VerificationCategory;
	protected Listbox sortOperator_CreatedOn;

	protected Textbox cif;
	protected Textbox collateralType;
	protected Textbox collateralReference;
	protected Textbox loanReference;
	protected ExtendedCombobox agency;
	protected Datebox createdOn;
	private FinanceDetail financeDetail;

	private String module = "";

	@Autowired
	private transient LegalVettingService legalVettingService;
	@Autowired
	private transient FinanceDetailService financeDetailService;

	/**
	 * default constructor.<br>
	 */
	public LegalVettingInitiationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "LegalVetting";
		super.pageRightName = "LegalVettingInitiationList";
		super.tableName = "verification_vt_view";
		super.queueTableName = "verification_vt_view";
		super.enquiryTableName = "verification_vt_view";
		this.module = getArgument("module");
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_LegalVettingInitiation(Event event) {
		logger.debug(Literal.ENTERING);

		if ("ENQ".equals(this.module)) {
			enqiryModule = true;
		}

		doSetFieldProperties();
		// Set the page level components.
		setPageComponents(window_LegalVettingInitiation, borderLayout_LegalVettingList, listBoxLegalVetting,
				pagingLegalVettingList);
		setItemRender(new LegalVettingListModelItemRender());

		// Register buttons and fields.label_LegalVettingList_RecordType.value
		registerButton(button_LegalVettingList_LegalVettingSearch);
		registerButton(button_LegalVettingList_NewLegalVetting, "button_LegalVettingInitiationList_NewLegalVetting",
				true);

		registerField("verificationId");
		registerField("cif", listheader_CIF, SortOrder.ASC, cif, sortOperator_CIF, Operators.STRING);
		registerField("collateralType", listheader_CollateralType, SortOrder.ASC, collateralType,
				sortOperator_CollateralType, Operators.STRING);
		registerField("referenceFor", listheader_CollateralReference, SortOrder.ASC, collateralReference,
				sortOperator_CollateralReference, Operators.STRING);
		registerField("keyReference", listheader_LoanReference, SortOrder.ASC, loanReference,
				sortOperator_LoanReference, Operators.STRING);
		registerField("createdOn", listheader_CreatedOn, SortOrder.NONE, createdOn, sortOperator_CreatedOn,
				Operators.DATE);
		registerField("agencyName", listheader_Agency, SortOrder.ASC, agency, sortOperator_Agency, Operators.DEFAULT);
		registerField("VerificationCategory");
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
		agencyFilter[0] = new Filter("DealerType", Agencies.LVAGENCY.getKey(), Filter.OP_EQUAL);
		agency.setFilters(agencyFilter);

		this.createdOn.setFormat(DateFormat.SHORT_DATE.getPattern());
		logger.debug(Literal.LEAVING);
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		if (!enqiryModule) {
			this.searchObject.addFilter(new Filter("recordType", "", Filter.OP_NOT_EQUAL));
		}

	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_LegalVettingList_LegalVettingSearch(Event event) {
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

	public void onLegalVettingItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		// Get the selected record.
		Listitem selectedItem = this.listBoxLegalVetting.getSelectedItem();
		final long verificationId = (long) selectedItem.getAttribute("verificationId");
		LegalVetting lv = new LegalVetting();
		lv.setVerificationId(verificationId);
		lv = legalVettingService.getLegalVetting(lv, "_View");
		if (lv == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		setFinanceDetail(financeDetailService.getVerificationInitiationDetails(lv.getKeyReference(),
				VerificationType.VETTING, "_View"));

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  AND  verificationId = ");
		whereCond.append(lv.getVerificationId());
		whereCond.append(" AND  version=");
		whereCond.append(lv.getVersion());

		if (doCheckAuthority(lv, whereCond.toString())) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && lv.getWorkflowId() == 0) {
				lv.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(lv);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$button_LegalVettingList_NewLegalVetting(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		LegalVetting legalVetting = new LegalVetting();
		legalVetting.setNewRecord(true);
		legalVetting.setWorkflowId(getWorkFlowId());

		Map<String, Object> arg = new HashMap<>();
		arg.put("legalVettingInitiationListCtrl", legalVetting);
		arg.put("legalVettingInitiationListCtrl", this);
		arg.put("enqiryModule", enqiryModule);
		arg.put("module", VerificationType.VETTING.getValue());

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
	private void doShowDialogPage(LegalVetting legalVetting) {
		logger.debug(Literal.ENTERING);

		HashMap<String, Object> map = getDefaultArguments();
		if (getFinanceDetail().getTvVerification() == null) {
			getFinanceDetail().setTvVerification(new Verification());
		}
		map.put("legalVettingInitiationListCtrl", this);
		map.put("finHeaderList", getFinBasicDetails());
		map.put("Vetting", getFinanceDetail().getTvVerification());
		map.put("financeDetail", getFinanceDetail());

		map.put("InitType", true);
		map.put("userRole", getRole());
		map.put("moduleDefiner", "");
		map.put("enqiryModule", true);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/LegalVettingInitiation.zul",
					null, map);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public HashMap<String, Object> getDefaultArguments() {

		final HashMap<String, Object> map = new HashMap<String, Object>();
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

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
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
}

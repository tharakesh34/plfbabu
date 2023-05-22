package com.pennanttech.pff.provision.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.provision.model.Provision;
import com.pennanttech.pff.provision.service.ProvisionService;

public class ManualProvisioningListCtrl extends GFCBaseListCtrl<Provision> {
	private static final long serialVersionUID = 1L;

	protected Window window_ManualProvisioningList;
	protected Borderlayout borderLayout_ManualProvisioningList;
	protected Paging pagingManualProvisioningList;
	protected Listbox listBoxManualProvisioning;

	// List Headers
	protected Listheader listheader_CIFNo;
	protected Listheader listheader_CustName;
	protected Listheader listheader_FinReference;
	protected Listheader listheader_FinType;
	protected Listheader listheader_PrincipalOS;
	protected Listheader listheader_TotalOD;
	protected Listheader listheader_ManualProvision;
	protected Listheader listheader_LoanClassification;
	protected Listheader listheader_EffectiveClassification;

	protected Button button_ManualProvisioningList_ManualProvisioningSearch;
	protected Button button_ManualProvisioningList_NewManualProvisioning;

	// Search fields
	protected ExtendedCombobox cifNo;
	protected ExtendedCombobox finReference;
	protected ExtendedCombobox finType;

	protected Listbox sortOperator_CIFNo;
	protected Listbox sortOperator_FinReference;
	protected Listbox sortOperator_FinType;

	private transient ProvisionService provisionService;

	public ManualProvisioningListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Provision";
		super.pageRightName = "ManualProvisioningList";
		super.tableName = "Loan_Provisions_AView";
		super.queueTableName = "Loan_Provisions_View";
		super.enquiryTableName = "Loan_Provisions_View";
	}

	protected void doAddFilters() {
		super.doAddFilters();
		if (!enqiryModule) {
			this.searchObject.addFilterEqual("FinIsActive", 1);
		}
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_ManualProvisioningList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_ManualProvisioningList, borderLayout_ManualProvisioningList, listBoxManualProvisioning,
				pagingManualProvisioningList);
		setItemRender(new ManualProvisioningListItemRenderer());

		// Register buttons and fields.
		registerButton(button_ManualProvisioningList_ManualProvisioningSearch);
		if (!enqiryModule) {
			registerButton(button_ManualProvisioningList_NewManualProvisioning, "button_ManualProvisioningList_NewList",
					false);
		} else {
			registerButton(button_ManualProvisioningList_NewManualProvisioning, "button_ManualProvisioningList_NewList",
					true);
		}

		registerField("custCIF", listheader_CIFNo, SortOrder.NONE, cifNo, sortOperator_CIFNo, Operators.STRING);
		registerField("FinId");
		registerField("finReference", listheader_FinReference, SortOrder.NONE, finReference, sortOperator_FinReference,
				Operators.STRING);
		registerField("finType", listheader_FinType, SortOrder.NONE, finType, sortOperator_FinType, Operators.STRING);
		registerField("finIsActive");
		registerField("CustShrtName", listheader_CustName);

		registerField("manualProvision", listheader_ManualProvision);
		registerField("osPrincipal", listheader_PrincipalOS);
		registerField("osProfit", listheader_TotalOD); // FIXME
		registerField("loanClassification", listheader_LoanClassification);
		registerField("effectiveClassification", listheader_EffectiveClassification);

		doSetFieldProperties();
		doSetProperties();
		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_ManualProvisioningList_ManualProvisioningSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("FinType");
		this.finType.setDescColumn("FinTypeDesc");
		this.finType.setValidateColumns(new String[] { "FinType" });

		this.cifNo.setModuleName("Customer");
		this.cifNo.setValueColumn("CustCIF");
		this.cifNo.setDescColumn("CustShrtName");
		this.cifNo.setValidateColumns(new String[] { "CustCIF" });

		this.finReference.setModuleName("FinanceMain");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDescColumn("FinType");
		this.finReference.setValidateColumns(new String[] { "FinReference" });

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onManualProvisionItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		Listitem selectedItem = this.listBoxManualProvisioning.getSelectedItem();
		final Provision aProvision = (Provision) selectedItem.getAttribute("data");

		Provision provision = provisionService.getProvisionDetail(aProvision.getFinID());

		if (provision == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		String whereCond = " and FinID = ? AND version = ?";
		Object[] obj = new Object[] { provision.getFinID(), provision.getVersion() };

		if (doCheckAuthority(provision, whereCond, obj)) {
			if (isWorkFlowEnabled() && provision.getWorkflowId() == 0) {
				provision.setWorkflowId(getWorkFlowId());
			}

			doShowDialogPage(provision);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialogPage(Provision provision) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("provision", provision);
		arg.put("manualProvisioningListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/Provision/ManualProvisioningDialog.zul", null,
					arg);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	@Autowired
	public void setProvisionService(ProvisionService provisionService) {
		this.provisionService = provisionService;
	}

}

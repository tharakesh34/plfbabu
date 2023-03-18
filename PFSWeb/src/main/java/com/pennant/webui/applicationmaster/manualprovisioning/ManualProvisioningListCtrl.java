package com.pennant.webui.applicationmaster.manualprovisioning;

import java.util.List;
import java.util.Map;

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
import com.pennant.backend.model.applicationmaster.NPAProvisionHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.service.applicationmaster.NPAProvisionHeaderService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.financemanagement.ProvisionService;
import com.pennant.backend.util.ProvisionConstants;
import com.pennant.webui.applicationmaster.manualprovisioning.model.ManualProvisioningListItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;

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
	protected Listheader listheader_ManualProvisioning;
	protected Listheader listheader_AssetStage;

	protected Button button_ManualProvisioningList_ManualProvisioningSearch;
	protected Button button_ManualProvisioningList_NewManualProvisioning;

	// Search fields
	protected ExtendedCombobox cifNo;
	protected ExtendedCombobox finReference;
	protected ExtendedCombobox finType;

	protected Listbox sortOperator_CIFNo;
	protected Listbox sortOperator_FinReference;
	protected Listbox sortOperator_FinType;
	private String module = null;

	private transient ProvisionService provisionService;
	private transient FinanceDetailService financeDetailService;
	private transient NPAProvisionHeaderService nPAProvisionHeaderService;

	public ManualProvisioningListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Provision";
		super.pageRightName = "ManualProvisioningList";
		super.tableName = "Provisions_AView";
		super.queueTableName = "Provisions_View";
		super.enquiryTableName = "Provisions_View";

		this.module = getArgument("enqiryModule");
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

		registerField("CustCIF", listheader_CIFNo, SortOrder.NONE, cifNo, sortOperator_CIFNo, Operators.STRING);
		registerField("finReference", listheader_FinReference, SortOrder.NONE, finReference, sortOperator_FinReference,
				Operators.STRING);
		registerField("finType", listheader_FinType, SortOrder.NONE, finType, sortOperator_FinType, Operators.STRING);
		// registerField("CustShrtName");
		registerField("finIsActive");
		registerField("CustShrtName", listheader_CustName);
		registerField("manualProvision", listheader_ManualProvisioning);
		registerField("assetCode", listheader_AssetStage);

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
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxManualProvisioning.getSelectedItem();
		final Provision aProvision = (Provision) selectedItem.getAttribute("data");

		String finType = null;
		Provision provision = provisionService.getProvisionById(aProvision.getFinID(), TableType.VIEW);

		if (provision == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		} else {
			FinanceDetail financeDetail = this.financeDetailService.getFinSchdDetailById(provision.getFinID(),
					TableType.AVIEW.getSuffix(), false);

			FinanceProfitDetail finPftDetail = this.financeDetailService.getFinProfitDetailsById(provision.getFinID());
			if (finPftDetail != null) {
				financeDetail.getFinScheduleData().setFinPftDeatil(finPftDetail);
				finType = finPftDetail.getFinType();

			}
			provision.setFinType(finType);
			financeDetail.getFinScheduleData().getFinanceMain().setNewRecord(false);
			if (financeDetail != null) {
				provision.setFinanceDetail(financeDetail);
			}

			List<NPAProvisionHeader> provisions = this.nPAProvisionHeaderService
					.getNPAProvisionsListByFintype(provision.getFinType(), TableType.AVIEW);

			for (NPAProvisionHeader npaProvisionHeader : provisions) {
				String npaTemplateCode = npaProvisionHeader.getNpaTemplateCode();
				if (ProvisionConstants.PROVISION_BOOKS_INT.equals(npaTemplateCode)) {
					provision.setNpaIntHeader(npaProvisionHeader);
				}

				if (ProvisionConstants.PROVISION_BOOKS_REG.equals(npaTemplateCode)) {
					provision.setNpaRegHeader(npaProvisionHeader);
				}
			}
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  AND  finreference = ");
		whereCond.append(provision.getFinReference());
		whereCond.append(" AND  version=");
		whereCond.append(provision.getVersion());

		if (doCheckAuthority(provision, whereCond.toString(),
				new Object[] { provision.getFinReference(), provision.getVersion() })) {
			if (isWorkFlowEnabled() && provision.getWorkflowId() == 0) {
				provision.setWorkflowId(getWorkFlowId());
			}

			Provision oldProvision = provisionService.getProvisionById(aProvision.getFinID(), TableType.AVIEW);
			provision.setOldProvision(oldProvision);
			doShowDialogPage(provision);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param assetclassificationheader The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Provision provision) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("provision", provision);
		arg.put("manualProvisioningListCtrl", this);
		if (enqiryModule) {
			arg.put("enquiry", module);
		}

		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/ManualProvisioning/ManualProvisioningDialog.zul", null, arg);
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

	// Getters and Setters
	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public ProvisionService getProvisionService() {
		return provisionService;
	}

	public void setProvisionService(ProvisionService provisionService) {
		this.provisionService = provisionService;
	}

	public NPAProvisionHeaderService getnPAProvisionHeaderService() {
		return nPAProvisionHeaderService;
	}

	public void setnPAProvisionHeaderService(NPAProvisionHeaderService nPAProvisionHeaderService) {
		this.nPAProvisionHeaderService = nPAProvisionHeaderService;
	}
}

package com.pennanttech.pff.npa.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.npa.model.AssetClassCode;
import com.pennanttech.pff.npa.service.AssetClassCodeService;

public class AssetClassCodeListCtrl extends GFCBaseListCtrl<AssetClassCode> {
	private static final long serialVersionUID = 1L;

	protected Window window_AssetClassCodeList;
	protected Borderlayout borderLayout_AssetClassCodeList;
	protected Paging pagingAssetClassCodeList;
	protected Listbox listBoxAssetClassCode;

	// List headers
	protected Listheader listheader_Code;
	protected Listheader listheader_Description;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_AssetClassCodeList_Search;
	protected Button button_AssetClassCodeList_New;

	// Search Fields
	protected Textbox code;
	protected Checkbox active;

	protected Listbox sortOperator_Code;
	protected Listbox sortOperator_Active;

	private transient AssetClassCodeService assetClassCodeService;

	/**
	 * default constructor.<br>
	 */
	public AssetClassCodeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "AssetClassCode";
		super.pageRightName = "AssetClassCodeList";
		super.tableName = "Asset_Class_Codes_Aview";
		super.queueTableName = "Asset_Class_Codes_View";
		super.enquiryTableName = "Asset_Class_Codes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_AssetClassCodeList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_AssetClassCodeList, borderLayout_AssetClassCodeList, listBoxAssetClassCode,
				pagingAssetClassCodeList);
		setItemRender(new AssetClassCodeListItemRenderer());

		// Register buttons and fields.
		registerButton(button_AssetClassCodeList_Search);
		registerButton(button_AssetClassCodeList_New, "button_AssetClassSetupList_New", true);

		registerField("Id");
		registerField("Code", listheader_Code, SortOrder.NONE, code, sortOperator_Code, Operators.STRING);
		registerField("Description", listheader_Description);
		registerField("Active", listheader_Active, SortOrder.NONE, active, sortOperator_Active, Operators.BOOLEAN);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_AssetClassCodeList_Search(Event event) {
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

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_AssetClassCodeList_New(Event event) {
		logger.debug(Literal.ENTERING);

		AssetClassCode assetClassCode = new AssetClassCode();
		assetClassCode.setNewRecord(true);
		assetClassCode.setWorkflowId(getWorkFlowId());

		doShowDialogPage(assetClassCode);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onAssetClassCodeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxAssetClassCode.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		AssetClassCode assetClassCode = assetClassCodeService.getAssetClassCode(id);

		if (assetClassCode == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  And  Id = ");
		whereCond.append(assetClassCode.getId());
		whereCond.append(" And  version = ");
		whereCond.append(assetClassCode.getVersion());

		if (doCheckAuthority(assetClassCode, whereCond.toString(),
				new Object[] { assetClassCode.getId(), assetClassCode.getVersion() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && assetClassCode.getWorkflowId() == 0) {
				assetClassCode.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(assetClassCode);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialogPage(AssetClassCode assetClassCode) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("assetClassCode", assetClassCode);
		arg.put("assetClassCodeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/Npa/AssetClassCodeDialog.zul", null, arg);
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

	@Autowired
	public void setAssetClassCodeService(AssetClassCodeService assetClassCodeService) {
		this.assetClassCodeService = assetClassCodeService;
	}
}
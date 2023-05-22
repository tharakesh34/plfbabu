package com.pennanttech.pff.npa.web;

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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.npa.model.AssetClassSetupHeader;
import com.pennanttech.pff.npa.service.AssetClassSetupService;

public class AssetClassSetupListCtrl extends GFCBaseListCtrl<AssetClassSetupHeader> {
	private static final long serialVersionUID = 1L;

	protected Window window_AssetClassSetupList;
	protected Borderlayout borderLayout_AssetClassSetupList;
	protected Paging pagingAssetClassSetupList;
	protected Listbox listBoxAssetClassSetup;
	protected Listheader listheader_Entity;
	protected Button button_AssetClassSetupList_Search;
	protected Button button_AssetClassSetupList_New;
	protected Textbox entity;
	protected Listbox sortOperator_Entity;

	private transient AssetClassSetupService assetClassSetupService;

	/**
	 * default constructor.<br>
	 */
	public AssetClassSetupListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "AssetClassSetupHeader";
		super.tableName = "ASSET_CLASS_SETUP_HEADER_AVIEW";
		super.queueTableName = "ASSET_CLASS_SETUP_HEADER_VIEW";
		super.enquiryTableName = "ASSET_CLASS_SETUP_HEADER_VIEW";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_AssetClassSetupList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_AssetClassSetupList, borderLayout_AssetClassSetupList, listBoxAssetClassSetup,
				pagingAssetClassSetupList);
		setItemRender(new AssetClassSetupListItemRenderer());

		// Register buttons and fields.
		registerButton(button_AssetClassSetupList_Search);
		registerButton(button_AssetClassSetupList_New, "button_AssetClassSetupList_New", true);

		registerField("Id");
		registerField("entityCode", listheader_Entity, SortOrder.NONE, entity, sortOperator_Entity, Operators.STRING);
		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_AssetClassSetupList_Search(Event event) {
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
	public void onClick$button_AssetClassSetupList_New(Event event) {
		logger.debug(Literal.ENTERING);

		AssetClassSetupHeader assetClassSetupHeader = new AssetClassSetupHeader();
		assetClassSetupHeader.setNewRecord(true);
		assetClassSetupHeader.setWorkflowId(getWorkFlowId());

		doShowDialogPage(assetClassSetupHeader);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onAssetClassSetupItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxAssetClassSetup.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		AssetClassSetupHeader assetClassSetupHeader = assetClassSetupService.getAssetClassSetup(id);

		if (assetClassSetupHeader == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  And  Id = ");
		whereCond.append(assetClassSetupHeader.getId());
		whereCond.append(" And  version = ");
		whereCond.append(assetClassSetupHeader.getVersion());

		if (doCheckAuthority(assetClassSetupHeader, whereCond.toString(),
				new Object[] { assetClassSetupHeader.getId(), assetClassSetupHeader.getVersion() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && assetClassSetupHeader.getWorkflowId() == 0) {
				assetClassSetupHeader.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(assetClassSetupHeader);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialogPage(AssetClassSetupHeader assetClassSetupHeader) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("assetClassSetup", assetClassSetupHeader);
		arg.put("assetClassSetupListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/Npa/AssetClassSetupDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		if (!enqiryModule) {
			this.searchObject.addFilterEqual("Active", 1);
		}
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

	public void setAssetClassSetupService(AssetClassSetupService assetClassSetupService) {
		this.assetClassSetupService = assetClassSetupService;
	}

}
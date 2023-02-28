package com.pennant.webui.cersai.assetsubtype;

import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.cersai.AssetSubType;
import com.pennant.backend.service.cersai.AssetSubTypeService;
import com.pennant.webui.cersai.assetsubtype.model.AssetSubTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/cersai/AssetSubType/AssetSubTypeList.zul file.
 * 
 */
public class AssetSubTypeListCtrl extends GFCBaseListCtrl<AssetSubType> {
	private static final long serialVersionUID = 1L;

	protected Window window_AssetSubTypeList;
	protected Borderlayout borderLayout_AssetSubTypeList;
	protected Paging pagingAssetSubTypeList;
	protected Listbox listBoxAssetSubType;

	// List headers
	protected Listheader listheader_AssetTypeId;
	protected Listheader listheader_Id;
	protected Listheader listheader_Description;

	// checkRights
	protected Button button_AssetSubTypeList_NewAssetSubType;
	protected Button button_AssetSubTypeList_AssetSubTypeSearch;

	// Search Fields
	protected Intbox assetTypeId; // autowired
	protected Intbox id; // autowired
	protected Textbox description; // autowired

	protected Listbox sortOperator_AssetTypeId;
	protected Listbox sortOperator_Id;
	protected Listbox sortOperator_Description;

	private transient AssetSubTypeService assetSubTypeService;

	/**
	 * default constructor.<br>
	 */
	public AssetSubTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "AssetSubType";
		super.pageRightName = "AssetSubTypeList";
		super.tableName = "CERSAI_AssetSubType_AView";
		super.queueTableName = "CERSAI_AssetSubType_View";
		super.enquiryTableName = "CERSAI_AssetSubType_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_AssetSubTypeList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_AssetSubTypeList, borderLayout_AssetSubTypeList, listBoxAssetSubType,
				pagingAssetSubTypeList);
		setItemRender(new AssetSubTypeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_AssetSubTypeList_AssetSubTypeSearch);
		registerButton(button_AssetSubTypeList_NewAssetSubType, "button_AssetSubTypeList_NewAssetSubType", true);

		registerField("assetTypeId", listheader_AssetTypeId, SortOrder.NONE, assetTypeId, sortOperator_AssetTypeId,
				Operators.NUMERIC);
		registerField("id", listheader_Id, SortOrder.NONE, id, sortOperator_Id, Operators.NUMERIC);
		registerField("description", listheader_Description, SortOrder.NONE, description, sortOperator_Description,
				Operators.STRING);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_AssetSubTypeList_AssetSubTypeSearch(Event event) {
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
	public void onClick$button_AssetSubTypeList_NewAssetSubType(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		AssetSubType assetsubtype = new AssetSubType();
		assetsubtype.setNewRecord(true);
		assetsubtype.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(assetsubtype);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onAssetSubTypeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxAssetSubType.getSelectedItem();
		final Long tmpAssetTypeId = (Long) selectedItem.getAttribute("assetTypeId");

		final int id = (int) selectedItem.getAttribute("id");
		AssetSubType assetsubtype = assetSubTypeService.getAssetSubType(tmpAssetTypeId, id);

		if (assetsubtype == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  AND  AssetTypeId = '");
		whereCond.append(assetsubtype.getAssetTypeId());
		whereCond.append(" ' AND  Id = '");
		whereCond.append(assetsubtype.getId());
		whereCond.append("' AND  version=");
		whereCond.append(assetsubtype.getVersion());

		if (doCheckAuthority(assetsubtype, whereCond.toString())) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && assetsubtype.getWorkflowId() == 0) {
				assetsubtype.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(assetsubtype);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param assetsubtype The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(AssetSubType assetsubtype) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("assetSubType", assetsubtype);
		arg.put("assetSubTypeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/cersai/AssetSubType/AssetSubTypeDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
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

	public void setAssetSubTypeService(AssetSubTypeService assetSubTypeService) {
		this.assetSubTypeService = assetSubTypeService;
	}
}
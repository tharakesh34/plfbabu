package com.pennant.webui.cersai.assettype;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.cersai.AssetTyp;
import com.pennant.backend.service.cersai.AssetTypeService;
import com.pennant.webui.cersai.assettype.model.AssetTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/cersai/AssetType/AssetTypeList.zul file.
 * 
 */
public class AssetTypeListCtrl extends GFCBaseListCtrl<AssetTyp> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(AssetTypeListCtrl.class);

	protected Window window_AssetTypeList;
	protected Borderlayout borderLayout_AssetTypeList;
	protected Paging pagingAssetTypeList;
	protected Listbox listBoxAssetType;

	// List headers
	protected Listheader listheader_AssetCategoryId;
	protected Listheader listheader_Id;
	protected Listheader listheader_Description;

	// checkRights
	protected Button button_AssetTypeList_NewAssetType;
	protected Button button_AssetTypeList_AssetTypeSearch;

	// Search Fields
	protected Textbox assetCategoryId; // autowired
	protected Longbox id; // autowired
	protected Textbox description; // autowired

	protected Listbox sortOperator_AssetCategoryId;
	protected Listbox sortOperator_Id;
	protected Listbox sortOperator_Description;

	private transient AssetTypeService assetTypService;

	/**
	 * default constructor.<br>
	 */
	public AssetTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "AssetTyp";
		super.pageRightName = "AssetTypeList";
		super.tableName = "CERSAI_AssetType_AView";
		super.queueTableName = "CERSAI_AssetType_View";
		super.enquiryTableName = "CERSAI_AssetType_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_AssetTypeList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_AssetTypeList, borderLayout_AssetTypeList, listBoxAssetType, pagingAssetTypeList);
		setItemRender(new AssetTypeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_AssetTypeList_AssetTypeSearch);
		registerButton(button_AssetTypeList_NewAssetType, "button_AssetTypeList_NewAssetType", true);

		registerField("assetCategoryId", listheader_AssetCategoryId, SortOrder.NONE, assetCategoryId,
				sortOperator_AssetCategoryId, Operators.STRING);
		registerField("assetCategoryIdName");
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
	public void onClick$button_AssetTypeList_AssetTypeSearch(Event event) {
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
	public void onClick$button_AssetTypeList_NewAssetType(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		AssetTyp assettyp = new AssetTyp();
		assettyp.setNewRecord(true);
		assettyp.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(assettyp);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onAssetTypeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxAssetType.getSelectedItem();
		final String assetCategoryId = (String) selectedItem.getAttribute("assetCategoryId");
		final int id = (int) selectedItem.getAttribute("id");
		AssetTyp assettyp = assetTypService.getAssetTyp(assetCategoryId, id);

		if (assettyp == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  AND  AssetCategoryId = '");
		whereCond.append(assettyp.getAssetCategoryId());
		whereCond.append(" ' AND  Id = '");
		whereCond.append(assettyp.getId());
		whereCond.append("' AND  version=");
		whereCond.append(assettyp.getVersion());

		if (doCheckAuthority(assettyp, whereCond.toString())) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && assettyp.getWorkflowId() == 0) {
				assettyp.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(assettyp);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param assettype The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(AssetTyp assettyp) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("assetTyp", assettyp);
		arg.put("assetTypListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/cersai/AssetType/AssetTypeDialog.zul", null, arg);
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

	public void setAssetTypService(AssetTypeService assetTypService) {
		this.assetTypService = assetTypService;
	}
}
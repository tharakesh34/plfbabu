package com.pennant.webui.cersai.assetcategory;

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

import com.pennant.backend.model.cersai.AssetCategory;
import com.pennant.backend.service.cersai.AssetCategoryService;
import com.pennant.webui.cersai.assetcategory.model.AssetCategoryListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/cersai/AssetCategory/AssetCategoryList.zul file.
 * 
 */
public class AssetCategoryListCtrl extends GFCBaseListCtrl<AssetCategory> {
	private static final long serialVersionUID = 1L;

	protected Window window_AssetCategoryList;
	protected Borderlayout borderLayout_AssetCategoryList;
	protected Paging pagingAssetCategoryList;
	protected Listbox listBoxAssetCategory;

	// List headers
	protected Listheader listheader_Id;
	protected Listheader listheader_Description;

	// checkRights
	protected Button button_AssetCategoryList_NewAssetCategory;
	protected Button button_AssetCategoryList_AssetCategorySearch;

	// Search Fields
	protected Intbox id; // autowired
	protected Textbox description; // autowired

	protected Listbox sortOperator_Id;
	protected Listbox sortOperator_Description;

	private transient AssetCategoryService assetCategoryService;

	/**
	 * default constructor.<br>
	 */
	public AssetCategoryListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "AssetCategory";
		super.pageRightName = "AssetCategoryList";
		super.tableName = "CERSAI_AssetCategory_AView";
		super.queueTableName = "CERSAI_AssetCategory_View";
		super.enquiryTableName = "CERSAI_AssetCategory_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_AssetCategoryList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_AssetCategoryList, borderLayout_AssetCategoryList, listBoxAssetCategory,
				pagingAssetCategoryList);
		setItemRender(new AssetCategoryListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_AssetCategoryList_AssetCategorySearch);
		registerButton(button_AssetCategoryList_NewAssetCategory, "button_AssetCategoryList_NewAssetCategory", true);

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
	public void onClick$button_AssetCategoryList_AssetCategorySearch(Event event) {
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
	public void onClick$button_AssetCategoryList_NewAssetCategory(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		AssetCategory assetcategory = new AssetCategory();
		assetcategory.setNewRecord(true);
		assetcategory.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(assetcategory);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onAssetCategoryItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxAssetCategory.getSelectedItem();
		final int id = (int) selectedItem.getAttribute("id");
		AssetCategory assetcategory = assetCategoryService.getAssetCategory(id);

		if (assetcategory == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  AND  Id = '");
		whereCond.append(assetcategory.getId());
		whereCond.append("' AND  version=");
		whereCond.append(assetcategory.getVersion());

		if (doCheckAuthority(assetcategory, whereCond.toString())) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && assetcategory.getWorkflowId() == 0) {
				assetcategory.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(assetcategory);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param assetcategory The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(AssetCategory assetcategory) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("assetCategory", assetcategory);
		arg.put("assetCategoryListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/cersai/AssetCategory/AssetCategoryDialog.zul", null, arg);
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

	public void setAssetCategoryService(AssetCategoryService assetCategoryService) {
		this.assetCategoryService = assetCategoryService;
	}
}
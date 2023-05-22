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
import com.pennanttech.pff.npa.model.AssetSubClassCode;
import com.pennanttech.pff.npa.service.AssetSubClassCodeService;

public class AssetSubClassCodeListCtrl extends GFCBaseListCtrl<AssetSubClassCode> {
	private static final long serialVersionUID = 1L;

	protected Window window_AssetSubClassCodeList;
	protected Borderlayout borderLayout_AssetSubClassCodeList;
	protected Paging pagingAssetSubClassCodeList;
	protected Listbox listBoxAssetSubClassCode;

	// List headers
	protected Listheader listheader_Code;
	protected Listheader listheader_Classification_Code;
	protected Listheader listheader_Classification_Description;
	protected Listheader listheader_Description;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_AssetSubClassCodeList_Search;
	protected Button button_AssetSubClassCodeList_New;

	// Search Fields
	protected Textbox code;
	protected Checkbox active;

	protected Listbox sortOperator_Code;
	protected Listbox sortOperator_Active;

	private transient AssetSubClassCodeService assetSubClassCodeService;

	/**
	 * default constructor.<br>
	 */
	public AssetSubClassCodeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "AssetSubClassCode";
		super.pageRightName = "AssetSubClassCodeList";
		super.tableName = "Asset_Sub_Class_Codes_Aview";
		super.queueTableName = "Asset_Sub_Class_Codes_View";
		super.enquiryTableName = "Asset_Sub_Class_Codes_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_AssetSubClassCodeList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_AssetSubClassCodeList, borderLayout_AssetSubClassCodeList, listBoxAssetSubClassCode,
				pagingAssetSubClassCodeList);
		setItemRender(new AssetSubClassCodeListItemRenderer());

		// Register buttons and fields.
		registerButton(button_AssetSubClassCodeList_Search);
		registerButton(button_AssetSubClassCodeList_New, "button_AssetClassSetupList_New", true);

		registerField("Id");
		registerField("ClassCode", listheader_Classification_Code, SortOrder.NONE, code, sortOperator_Code,
				Operators.STRING);
		registerField("Code", listheader_Code);
		registerField("ClassDescription", listheader_Classification_Description);
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
	public void onClick$button_AssetSubClassCodeList_Search(Event event) {
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
	public void onClick$button_AssetSubClassCodeList_New(Event event) {
		logger.debug(Literal.ENTERING);

		AssetSubClassCode assetSubClassCode = new AssetSubClassCode();
		assetSubClassCode.setNewRecord(true);
		assetSubClassCode.setWorkflowId(getWorkFlowId());

		doShowDialogPage(assetSubClassCode);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onAssetSubClassCodeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxAssetSubClassCode.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		AssetSubClassCode assetClassCode = assetSubClassCodeService.getAssetClassCode(id);

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

	private void doShowDialogPage(AssetSubClassCode assetClassCode) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("assetSubClassCode", assetClassCode);
		arg.put("assetSubClassCodeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/Npa/AssetSubClassCodeDialog.zul", null, arg);
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
	public void setAssetSubClassCodeService(AssetSubClassCodeService assetSubClassCodeService) {
		this.assetSubClassCodeService = assetSubClassCodeService;
	}
}
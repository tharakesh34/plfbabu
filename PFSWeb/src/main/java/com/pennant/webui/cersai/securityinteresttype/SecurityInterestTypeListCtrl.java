package com.pennant.webui.cersai.securityinteresttype;

import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
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

import com.pennant.backend.model.cersai.SecurityInterestType;
import com.pennant.backend.service.cersai.SecurityInterestTypeService;
import com.pennant.webui.cersai.securityinteresttype.model.SecurityInterestTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/cersai/SecurityInterestType/SecurityInterestTypeList.zul file.
 * 
 */
public class SecurityInterestTypeListCtrl extends GFCBaseListCtrl<SecurityInterestType> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(SecurityInterestTypeListCtrl.class);

	protected Window window_SecurityInterestTypeList;
	protected Borderlayout borderLayout_SecurityInterestTypeList;
	protected Paging pagingSecurityInterestTypeList;
	protected Listbox listBoxSecurityInterestType;

	// List headers
	protected Listheader listheader_AssetCategoryId;
	protected Listheader listheader_Id;
	protected Listheader listheader_Description;

	// checkRights
	protected Button button_SecurityInterestTypeList_NewSecurityInterestType;
	protected Button button_SecurityInterestTypeList_SecurityInterestTypeSearch;

	// Search Fields
	protected Textbox assetCategoryId; // autowired
	protected Intbox id; // autowired
	protected Textbox description; // autowired

	protected Listbox sortOperator_AssetCategoryId;
	protected Listbox sortOperator_Id;
	protected Listbox sortOperator_Description;

	private transient SecurityInterestTypeService securityInterestTypeService;

	/**
	 * default constructor.<br>
	 */
	public SecurityInterestTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "SecurityInterestType";
		super.pageRightName = "SecurityInterestTypeList";
		super.tableName = "CERSAI_SIType_AView";
		super.queueTableName = "CERSAI_SIType_View";
		super.enquiryTableName = "CERSAI_SIType_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_SecurityInterestTypeList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_SecurityInterestTypeList, borderLayout_SecurityInterestTypeList,
				listBoxSecurityInterestType, pagingSecurityInterestTypeList);
		setItemRender(new SecurityInterestTypeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_SecurityInterestTypeList_SecurityInterestTypeSearch);
		registerButton(button_SecurityInterestTypeList_NewSecurityInterestType,
				"button_SecurityInterestTypeList_NewSecurityInterestType", true);

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
	public void onClick$button_SecurityInterestTypeList_SecurityInterestTypeSearch(Event event) {
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
	public void onClick$button_SecurityInterestTypeList_NewSecurityInterestType(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		SecurityInterestType securityinteresttype = new SecurityInterestType();
		securityinteresttype.setNewRecord(true);
		securityinteresttype.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(securityinteresttype);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onSecurityInterestTypeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxSecurityInterestType.getSelectedItem();
		final String assetCategoryId = (String) selectedItem.getAttribute("assetCategoryId");
		final int id = (int) selectedItem.getAttribute("id");
		SecurityInterestType securityinteresttype = securityInterestTypeService.getSecurityInterestType(assetCategoryId,
				id);

		if (securityinteresttype == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  AND  AssetCategoryId = '");
		whereCond.append(securityinteresttype.getAssetCategoryId());
		whereCond.append(" ' AND  Id = '");
		whereCond.append(securityinteresttype.getId());
		whereCond.append("' AND  version=");
		whereCond.append(securityinteresttype.getVersion());

		if (doCheckAuthority(securityinteresttype, whereCond.toString())) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && securityinteresttype.getWorkflowId() == 0) {
				securityinteresttype.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(securityinteresttype);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param securityinteresttype The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(SecurityInterestType securityinteresttype) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("securityInterestType", securityinteresttype);
		arg.put("securityInterestTypeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/cersai/SecurityInterestType/SecurityInterestTypeDialog.zul",
					null, arg);
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

	public void setSecurityInterestTypeService(SecurityInterestTypeService securityInterestTypeService) {
		this.securityInterestTypeService = securityInterestTypeService;
	}
}
package com.pennant.webui.cersai.areaunit;

import java.util.Map;

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

import com.pennant.backend.model.cersai.AreaUnit;
import com.pennant.backend.service.cersai.AreaUnitService;
import com.pennant.webui.cersai.areaunit.model.AreaUnitListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/cersai/AreaUnit/AreaUnitList.zul file.
 * 
 */
public class AreaUnitListCtrl extends GFCBaseListCtrl<AreaUnit> {
	private static final long serialVersionUID = 1L;

	protected Window window_AreaUnitList;
	protected Borderlayout borderLayout_AreaUnitList;
	protected Paging pagingAreaUnitList;
	protected Listbox listBoxAreaUnit;

	// List headers
	protected Listheader listheader_Id;
	protected Listheader listheader_Description;

	// checkRights
	protected Button button_AreaUnitList_NewAreaUnit;
	protected Button button_AreaUnitList_AreaUnitSearch;

	// Search Fields
	protected Longbox id; // autowired
	protected Textbox description; // autowired

	protected Listbox sortOperator_Id;
	protected Listbox sortOperator_Description;

	private transient AreaUnitService areaUnitService;

	/**
	 * default constructor.<br>
	 */
	public AreaUnitListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "AreaUnit";
		super.pageRightName = "AreaUnitList";
		super.tableName = "CERSAI_AreaUnit_AView";
		super.queueTableName = "CERSAI_AreaUnit_View";
		super.enquiryTableName = "CERSAI_AreaUnit_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_AreaUnitList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_AreaUnitList, borderLayout_AreaUnitList, listBoxAreaUnit, pagingAreaUnitList);
		setItemRender(new AreaUnitListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_AreaUnitList_AreaUnitSearch);
		registerButton(button_AreaUnitList_NewAreaUnit, "button_AreaUnitList_NewAreaUnit", true);

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
	public void onClick$button_AreaUnitList_AreaUnitSearch(Event event) {
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
	public void onClick$button_AreaUnitList_NewAreaUnit(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		AreaUnit areaunit = new AreaUnit();
		areaunit.setNewRecord(true);
		areaunit.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(areaunit);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onAreaUnitItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxAreaUnit.getSelectedItem();
		final Long id = (Long) selectedItem.getAttribute("id");
		AreaUnit areaunit = areaUnitService.getAreaUnit(id);

		if (areaunit == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  AND  Id = '");
		whereCond.append(areaunit.getId());
		whereCond.append("' AND  version=");
		whereCond.append(areaunit.getVersion());

		if (doCheckAuthority(areaunit, whereCond.toString())) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && areaunit.getWorkflowId() == 0) {
				areaunit.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(areaunit);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param areaunit The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(AreaUnit areaunit) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("areaUnit", areaunit);
		arg.put("areaUnitListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/cersai/AreaUnit/AreaUnitDialog.zul", null, arg);
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

	public void setAreaUnitService(AreaUnitService areaUnitService) {
		this.areaUnitService = areaUnitService;
	}
}
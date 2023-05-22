package com.pennant.webui.systemmasters.dealergroup;

import java.util.Map;

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

import com.pennant.backend.model.systemmasters.DealerGroup;
import com.pennant.backend.service.systemmasters.DealerGroupService;
import com.pennant.webui.systemmasters.dealergroup.model.DealerGroupListListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class DealerGroupListCtrl extends GFCBaseListCtrl<DealerGroup> {
	private static final long serialVersionUID = 1L;

	protected Window window_DealerGroupList;
	protected Borderlayout borderLayout_DealerGroupList;
	protected Paging pagingDealerGroupList;
	protected Listbox listBoxDealerGroup;

	// List headers
	protected Listheader listheader_dealerCode;
	protected Listheader listheader_dealerCategoryId;
	protected Listheader listheader_channel;
	protected Listheader listheader_active;

	// checkRights
	protected Button button_DealerGroupList_NewDealerGroup;
	protected Button button_DealerGroupList_DealerGroupSearch;

	// Search Fields
	protected Textbox dealerCode; // autowired
	protected Textbox dealerCategoryId; // autowired
	protected Textbox channel; // autowired
	protected Checkbox active; // autowired

	protected Listbox sortOperator_dealerCode;
	protected Listbox sortOperator_dealerCategoryId;
	protected Listbox sortOperator_channel;
	protected Listbox sortOperator_active;

	private transient DealerGroupService dealerGroupService;

	/**
	 * default constructor.<br>
	 */
	public DealerGroupListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "DealerGroup";
		super.pageRightName = "DealerGroupList";
		super.tableName = "CD_DealerGroup_AVIEW";
		super.queueTableName = "CD_DealerGroup_VIEW";
		super.enquiryTableName = "CD_DealerGroup_VIEW";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_DealerGroupList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_DealerGroupList, borderLayout_DealerGroupList, listBoxDealerGroup,
				pagingDealerGroupList);
		setItemRender(new DealerGroupListListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_DealerGroupList_DealerGroupSearch);
		registerButton(button_DealerGroupList_NewDealerGroup, "button_DealerGroupList_NewDealerGroup", true);

		registerField("dealerGroupId");
		registerField("dealerCode", listheader_dealerCode, SortOrder.NONE, dealerCode, sortOperator_dealerCode,
				Operators.STRING);
		registerField("dealerCategoryId", listheader_dealerCategoryId, SortOrder.NONE, dealerCategoryId,
				sortOperator_dealerCategoryId, Operators.STRING);
		registerField("channel", listheader_channel, SortOrder.NONE, channel, sortOperator_channel, Operators.STRING);
		registerField("active", listheader_active, SortOrder.NONE, active, sortOperator_active, Operators.BOOLEAN);
		// registerField("groupIdName");
		// doSetFieldProperties();
		// Render the page and display the data.
		doRenderPage();
		search();
	}

	public void onClick$button_DealerGroupList_DealerGroupSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_DealerGroupList_NewDealerGroup(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		DealerGroup dealerGroup = new DealerGroup();
		dealerGroup.setNewRecord(true);
		dealerGroup.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(dealerGroup);

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialogPage(DealerGroup dealerGroup) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("dealerGroup", dealerGroup);
		arg.put("dealerGroupListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/DealerGroup/DealerGroupDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
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
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onBuilderCompanyItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxDealerGroup.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		DealerGroup dealerGroup = dealerGroupService.getDealerGroup(id);

		if (dealerGroup == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  id =?");

		if (doCheckAuthority(dealerGroup, whereCond.toString(), new Object[] { dealerGroup.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && dealerGroup.getWorkflowId() == 0) {
				dealerGroup.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(dealerGroup);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
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
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onDealerGroupItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxDealerGroup.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		DealerGroup dealerGroup = dealerGroupService.getDealerGroup(id);

		if (dealerGroup == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  id =? ");

		if (doCheckAuthority(dealerGroup, whereCond.toString(), new Object[] { dealerGroup.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && dealerGroup.getWorkflowId() == 0) {
				dealerGroup.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(dealerGroup);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
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

	public DealerGroupService getDealerGroupService() {
		return dealerGroupService;
	}

	public void setDealerGroupService(DealerGroupService dealerGroupService) {
		this.dealerGroupService = dealerGroupService;
	}

}

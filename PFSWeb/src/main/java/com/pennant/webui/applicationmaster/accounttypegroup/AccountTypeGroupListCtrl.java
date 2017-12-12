package com.pennant.webui.applicationmaster.accounttypegroup;

import java.util.Map;

import org.apache.log4j.Logger;
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

import com.pennant.backend.model.applicationmaster.AccountTypeGroup;
import com.pennant.backend.service.applicationmaster.AccountTypeGroupService;
import com.pennant.webui.applicationmaster.accounttypegroup.model.AccountTypeGroupListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

public class AccountTypeGroupListCtrl extends GFCBaseListCtrl<AccountTypeGroup> {
	private static final long					serialVersionUID	= 5327118548986437717L;
	private static final Logger					logger				= Logger.getLogger(AccountTypeGroupListCtrl.class);

	protected Window							window_AccountTypeGroupList;
	protected Borderlayout						borderLayout_AccountTypeGroupList;
	protected Listbox							listBoxAccountTypeGroup;
	protected Paging							pagingAccountTypeGroupList;

	protected Listheader						listheader_AcctTypeLevel;
	protected Listheader						listheader_GroupCode;
	protected Listheader						listheader_ParentGroup;

	protected Button							button_AccountTypeGroupList_NewAccountTypeGroup;
	protected Button							button_AccountTypeGroupList_AccountTypeGroupSearchDialog;

	protected Intbox							acctTypeLevel;
	protected Textbox							groupCode;
	protected Textbox							parentGroup;

	protected Listbox							sortOperator_AcctTypeLevel;
	protected Listbox							sortOperator_GroupCode;
	protected Listbox							sortOperator_ParentGroup;

	private transient AccountTypeGroupService	accountTypeGroupService;

	/**
	 * The default constructor.
	 */
	public AccountTypeGroupListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "AccountTypeGroup";
		super.pageRightName = "AccountTypeGroupList";
		super.tableName = "AccountTypeGroup_AView";
		super.queueTableName = "AccountTypeGroup_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_AccountTypeGroupList(Event event) {
		// Set the page level components.
		setPageComponents(window_AccountTypeGroupList, borderLayout_AccountTypeGroupList, listBoxAccountTypeGroup,
				pagingAccountTypeGroupList);
		setItemRender(new AccountTypeGroupListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_AccountTypeGroupList_NewAccountTypeGroup,
				"button_AccountTypeGroupList_NewAccountTypeGroup", true);
		registerButton(button_AccountTypeGroupList_AccountTypeGroupSearchDialog);
		
		registerField("groupId");
		registerField("acctTypeLevel", listheader_AcctTypeLevel, SortOrder.ASC, acctTypeLevel,
				sortOperator_AcctTypeLevel, Operators.NUMERIC);
		registerField("groupCode", listheader_GroupCode, SortOrder.NONE, groupCode, sortOperator_GroupCode, Operators.STRING);
		registerField("parentGroup", listheader_ParentGroup, SortOrder.NONE, parentGroup, sortOperator_ParentGroup,
				Operators.STRING);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_AccountTypeGroupList_AccountTypeGroupSearchDialog(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_AccountTypeGroupList_NewAccountTypeGroup(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		AccountTypeGroup accountTypeGroup = new AccountTypeGroup();
		accountTypeGroup.setNewRecord(true);
		accountTypeGroup.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(accountTypeGroup);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onAccountTypeGroupItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxAccountTypeGroup.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		AccountTypeGroup accountTypeGroup = accountTypeGroupService.getAccountTypeGroupById(id);

		if (accountTypeGroup == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND GroupID='" + accountTypeGroup.getGroupId() + "' AND version="
				+ accountTypeGroup.getVersion() + " ";

		if (doCheckAuthority(accountTypeGroup, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && accountTypeGroup.getWorkflowId() == 0) {
				accountTypeGroup.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(accountTypeGroup);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param accountTypeGroup
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(AccountTypeGroup accountTypeGroup) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("accountTypeGroup", accountTypeGroup);
		arg.put("accountTypeGroupListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/AccountTypeGroup/AccountTypeGroupDialog.zul",
					null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
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

	public void setAccountTypeGroupService(AccountTypeGroupService accountTypeGroupService) {
		this.accountTypeGroupService = accountTypeGroupService;
	}

}

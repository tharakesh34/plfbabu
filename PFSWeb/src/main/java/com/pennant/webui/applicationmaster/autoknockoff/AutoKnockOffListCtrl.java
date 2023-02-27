package com.pennant.webui.applicationmaster.autoknockoff;

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

import com.pennant.backend.model.finance.AutoKnockOff;
import com.pennant.backend.service.applicationmaster.AutoKnockOffService;
import com.pennant.webui.applicationmaster.autoknockoff.model.AutoKnockOffListModelItemRender;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class AutoKnockOffListCtrl extends GFCBaseListCtrl<AutoKnockOff> {
	private static final long serialVersionUID = 1L;

	protected Window window_AutoKnockOffList;
	protected Borderlayout borderLayout_AutoKnockOffList;
	protected Paging pagingAutoKnockOffList;
	protected Listbox listBoxAutoKnockOff;

	// List headers
	protected Listheader listheader_KnockOffCode;
	protected Listheader listheader_Description;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_AutoKnockOffList_NewAutoKnockOff;
	protected Button button_AutoKnockOffList_AutoKnockOffSearch;

	// Search Fields
	protected Checkbox active;
	protected Textbox knockOffCode;
	protected Textbox description;

	protected Listbox sortOperator_KnockOffCode;
	protected Listbox sortOperator_Description;

	protected Listbox sortOperator_Active;

	private transient AutoKnockOffService autoKnockOffService;

	public AutoKnockOffListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "AutoKnockOff";
		super.pageRightName = "AutoKnockOffList";
		super.tableName = "AUTO_KNOCKOFF_AVIEW";
		super.queueTableName = "AUTO_KNOCKOFF_VIEW";
		super.enquiryTableName = "AUTO_KNOCKOFF_VIEW";
	}

	public void onCreate$window_AutoKnockOffList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_AutoKnockOffList, borderLayout_AutoKnockOffList, listBoxAutoKnockOff,
				pagingAutoKnockOffList);
		setItemRender(new AutoKnockOffListModelItemRender());

		// Register buttons and fields.
		registerButton(button_AutoKnockOffList_AutoKnockOffSearch);
		registerButton(button_AutoKnockOffList_NewAutoKnockOff, "button_AutoKnockOffList_NewAutoKnockOff", true);

		registerField("id");
		registerField("code", listheader_KnockOffCode, SortOrder.NONE, knockOffCode, sortOperator_KnockOffCode,
				Operators.STRING);
		registerField("description", listheader_Description, SortOrder.NONE, description, sortOperator_Description,
				Operators.STRING);
		registerField("active", listheader_Active, SortOrder.NONE, active, sortOperator_Active,
				Operators.SIMPLE_NUMARIC);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	public void onClick$button_AutoKnockOffList_AutoKnockOffSearch(Event event) {
		search();
	}

	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	public void onClick$button_AutoKnockOffList_NewAutoKnockOff(Event event) {
		logger.debug(Literal.ENTERING);

		AutoKnockOff knockOff = new AutoKnockOff();
		knockOff.setNewRecord(true);
		knockOff.setWorkflowId(getWorkFlowId());

		doShowDialogPage(knockOff);

		logger.debug(Literal.LEAVING);
	}

	public void onAutoKnockOffItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		Listitem selectedItem = this.listBoxAutoKnockOff.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		AutoKnockOff aKnockOff = autoKnockOffService.getAutoKnockOffCode(id);

		if (aKnockOff == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " where Id=?";

		if (doCheckAuthority(aKnockOff, whereCond.toString(), new Object[] { aKnockOff.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aKnockOff.getWorkflowId() == 0) {
				aKnockOff.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(aKnockOff);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialogPage(AutoKnockOff knockOff) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("AutoKnockOff", knockOff);
		arg.put("autoKnockOffListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/AutoKnockOff/AutoKnockOffDialog.zul", null,
					arg);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$print(Event event) {
		doPrintResults();
	}

	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	public void onCheck$fromApproved(Event event) {
		search();
	}

	public void onCheck$fromWorkFlow(Event event) {
		search();
	}

	public AutoKnockOffService getAutoKnockOffService() {
		return autoKnockOffService;
	}

	public void setAutoKnockOffService(AutoKnockOffService autoKnockOffService) {
		this.autoKnockOffService = autoKnockOffService;
	}

}

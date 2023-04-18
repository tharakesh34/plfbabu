package com.pennant.webui.applicationmaster.loantypewriteoff;

import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
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

import com.pennant.backend.model.finance.FinTypeWriteOff;
import com.pennant.backend.service.applicationmaster.LoanTypeWriteOffService;
import com.pennant.webui.applicationmaster.loantypewriteoff.model.LoanTypeWriteOffModelItemRender;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class LoanTypeWriteOffListCtrl extends GFCBaseListCtrl<FinTypeWriteOff> {
	private static final long serialVersionUID = 1L;

	protected Window window_LoanTypeWriteOffList;
	protected Borderlayout borderLayout_LoanTypeWriteOffList;
	protected Paging pagingLoanTypeWriteOffList;
	protected Listbox listBoxLoanTypeWriteOff;

	// List headers
	protected Listheader listheader_LoanType;
	protected Listheader listheader_Description;

	// checkRights
	protected Button button_LoanTypeWriteOffList_NewLoanTypeWriteOff;
	protected Button button_LoanTypeWriteOffList_LoanTypeWriteOffSearch;

	// Search Fields
	protected Textbox loanType;
	protected Listbox sortOperator_LoanType;

	private transient LoanTypeWriteOffService loanTypeWriteOffService;

	public LoanTypeWriteOffListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FinTypeWriteOff";
		super.pageRightName = "LoanTypeWriteOffList";
		super.tableName = "AUTO_WRITEOFF_LOANTYPES";
		super.queueTableName = "AUTO_WRITEOFF_LOANTYPES_LVIEW";
		super.enquiryTableName = "AUTO_WRITEOFF_LOANTYPES_LVIEW";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_LoanTypeWriteOffList(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_LoanTypeWriteOffList, borderLayout_LoanTypeWriteOffList, listBoxLoanTypeWriteOff,
				pagingLoanTypeWriteOffList);
		setItemRender(new LoanTypeWriteOffModelItemRender());

		registerButton(button_LoanTypeWriteOffList_LoanTypeWriteOffSearch);
		registerButton(button_LoanTypeWriteOffList_NewLoanTypeWriteOff,
				"button_LoanTypeWriteOffList_NewLoanTypeWriteOff", true);
		registerField("LoanType", listheader_LoanType, SortOrder.NONE, loanType, sortOperator_LoanType,
				Operators.STRING);

		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_LoanTypeWriteOffList_LoanTypeWriteOffSearch(Event event) {
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
	public void onClick$button_LoanTypeWriteOffList_NewLoanTypeWriteOff(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		FinTypeWriteOff writeOff = new FinTypeWriteOff();
		writeOff.setNewRecord(true);
		writeOff.setWorkflowId(getWorkFlowId());
		Map<String, Object> arg = getDefaultArguments();
		arg.put("loanTypeWriteOff", writeOff);
		arg.put("loanTypeWriteOffListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/LoanTypeWriteOff/SelectLoanTypeWriteOffDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onLoanTypeWriteOffItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		Listitem selectedItem = this.listBoxLoanTypeWriteOff.getSelectedItem();
		final FinTypeWriteOff data = (FinTypeWriteOff) selectedItem.getAttribute("data");
		List<FinTypeWriteOff> finWriteOff = loanTypeWriteOffService.getWriteOffMappingById(data.getLoanType());

		if (finWriteOff.size() == 0) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		FinTypeWriteOff finTypeWriteOff = new FinTypeWriteOff();
		BeanUtils.copyProperties(finWriteOff.get(0), finTypeWriteOff);
		finTypeWriteOff.setLoanTypeWriteOffMapping(finWriteOff);

		if (isWorkFlowEnabled() && finTypeWriteOff.getWorkflowId() == 0) {
			finTypeWriteOff.setWorkflowId(getWorkFlowId());
		}

		Map<String, Object> arg = getDefaultArguments();
		arg.put("loanTypeWriteOff", finTypeWriteOff);
		arg.put("loanTypeWriteOffListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/LoanTypeWriteOff/LoanTypeWriteOffDialog.zul",
					null, arg);
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

	public void setLoanTypeWriteOffService(LoanTypeWriteOffService loanTypeWriteOffService) {
		this.loanTypeWriteOffService = loanTypeWriteOffService;
	}

}

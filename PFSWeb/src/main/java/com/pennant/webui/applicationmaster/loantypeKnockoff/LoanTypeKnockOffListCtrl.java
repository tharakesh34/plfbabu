package com.pennant.webui.applicationmaster.loantypeKnockoff;

import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.BeanUtils;
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
import com.pennant.backend.model.finance.FinTypeKnockOff;
import com.pennant.backend.service.applicationmaster.LoanTypeKnockOffService;
import com.pennant.webui.applicationmaster.loantypeKnockoff.model.LoanTypeKnockOffModelItemRender;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class LoanTypeKnockOffListCtrl extends GFCBaseListCtrl<FinTypeKnockOff>{
	private static final Logger logger = LogManager.getLogger(LoanTypeKnockOffListCtrl.class);

	private static final long serialVersionUID = 1L;
	protected Window window_LoanTypeKnockOffList;
	protected Borderlayout borderLayout_LoanTypeKnockOffList;
	protected Paging pagingLoanTypeKnockOffList;
	protected Listbox listBoxLoanTypeKnockOff;

	// List headers
	protected Listheader listheader_LoanType;
	protected Listheader listheader_Description;
	protected Listheader listheader_AppToFlexiLoans;
	protected Listheader listheader_Vendor;
	protected Listheader listheader_Active;

	// checkRights
	protected Button button_LoanTypeKnockOffList_NewLoanTypeKnockOff;
	protected Button button_LoanTypeKnockOffList_LoanTypeKnockOffSearch;

	// Search Fields
	protected Textbox vendor; 
	protected Checkbox active; 
	protected Textbox loanType;
	protected Textbox description;
	protected Checkbox appToFlexiLoans;
	protected Listbox sortOperator_LoanType;
	protected Listbox sortOperator_Description;
	protected Listbox sortOperator_AppToFlexiLoans;
	protected Listbox sortOperator_Vendor;
	protected Listbox sortOperator_Active;

	private transient LoanTypeKnockOffService loanTypeKnockOffService;

	public LoanTypeKnockOffListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FinTypeKnockOff";
		super.pageRightName = "LoanTypeKnockOffList";
		super.tableName = "AUTO_KNOCKOFF_LOANTYPES_LView";
		super.queueTableName = "AUTO_KNOCKOFF_LOANTYPES_LView";
		super.enquiryTableName = "AUTO_KNOCKOFF_LOANTYPES_LView";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_LoanTypeKnockOffList(Event event) {
		logger.debug(Literal.ENTERING);
		
		setPageComponents(window_LoanTypeKnockOffList, borderLayout_LoanTypeKnockOffList, listBoxLoanTypeKnockOff,
				pagingLoanTypeKnockOffList);
		setItemRender(new LoanTypeKnockOffModelItemRender());

		registerButton(button_LoanTypeKnockOffList_LoanTypeKnockOffSearch);
		registerButton(button_LoanTypeKnockOffList_NewLoanTypeKnockOff, "button_LoanTypeKnockOffList_NewLoanTypeKnockOff", true);
		registerField("LoanType", listheader_LoanType, SortOrder.NONE, loanType, sortOperator_LoanType, Operators.STRING);
		
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_LoanTypeKnockOffList_LoanTypeKnockOffSearch(Event event) {
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
	public void onClick$button_LoanTypeKnockOffList_NewLoanTypeKnockOff(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		FinTypeKnockOff knockOff = new FinTypeKnockOff();
		knockOff.setNewRecord(true);
		knockOff.setWorkflowId(getWorkFlowId());
		Map<String, Object> arg = getDefaultArguments();
		arg.put("loanTypeKnockOff", knockOff);
		arg.put("loanTypeKnockOffListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/LoanTypeKnockOff/SelectLoanTypeKnockOffDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onLoanTypeKnockOffItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		Listitem selectedItem = this.listBoxLoanTypeKnockOff.getSelectedItem();
		final FinTypeKnockOff data = (FinTypeKnockOff) selectedItem.getAttribute("data");
		List<FinTypeKnockOff> finKnockOff = loanTypeKnockOffService.getKnockOffMappingById(data.getLoanType());
		
		if (finKnockOff.size() == 0) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}
		
		FinTypeKnockOff finTypeKnockOff = new FinTypeKnockOff();
		BeanUtils.copyProperties(finKnockOff.get(0), finTypeKnockOff);
		finTypeKnockOff.setLoanTypeKonckOffMapping(finKnockOff);
		
		if (isWorkFlowEnabled() && finTypeKnockOff.getWorkflowId() == 0) {
			finTypeKnockOff.setWorkflowId(getWorkFlowId());
		}

		Map<String, Object> arg = getDefaultArguments();
		arg.put("loanTypeKnockOff", finTypeKnockOff);
		arg.put("loanTypeKnockOffListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/LoanTypeKnockOff/LoanTypeKnockOffDialog.zul", null,
					arg);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}
		
		logger.debug(Literal.LEAVING);
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

	public LoanTypeKnockOffService getLoanTypeKnockOffService() {
		return loanTypeKnockOffService;
	}

	public void setLoanTypeKnockOffService(LoanTypeKnockOffService loanTypeKnockOffService) {
		this.loanTypeKnockOffService = loanTypeKnockOffService;
	}

}

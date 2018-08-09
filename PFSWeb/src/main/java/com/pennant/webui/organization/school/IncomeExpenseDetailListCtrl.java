package com.pennant.webui.organization.school;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.pennant.webui.organization.school.model.IncomeExpenseDetailListModelItemRender;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.incomeexpensedetail.service.IncomeExpenseDetailService;
import com.pennanttech.pff.organization.school.model.IncomeExpenseHeader;

public class IncomeExpenseDetailListCtrl extends GFCBaseListCtrl<IncomeExpenseHeader>{
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(IncomeExpenseDetailListCtrl.class);

	protected Window window_IncomeExpenseList;
	protected Borderlayout borderLayout_IncomeExpenseList;
	protected Listbox listBoxIncomeExpense;
	protected Paging pagingIncomeExpenseList;

	// List headers
	protected Listheader listheader_CIF;
	protected Listheader listheader_Name;
	protected Listheader listheader_FinancialYear;
	protected Listheader listheader_Category;
	
	protected Button button_IncomeExpenseList_NewIncomeExpense;
	protected Button button_IncomeExpenseList_IncomeExpenseSearch;
	
	// Search Fields
	protected Listbox sortOperator_CIF;
	protected Listbox sortOperator_Name;
	protected Listbox sortOperator_FinancialYear;
	
	protected Textbox cif;
	protected Textbox name;
	protected Textbox financialYear;
	
	private String module = "";
	
	@Autowired
	private IncomeExpenseDetailService incomeExpenseDetailService;
	
	public IncomeExpenseDetailListCtrl() {
		super();
	}
	
	@Override
	protected void doSetProperties() {
		super.moduleCode = "IncomeExpenseHeader";
		super.pageRightName = "IncomeExpenseDetailList";
		super.tableName = "org_income_expense_header_view";
		super.queueTableName = "org_income_expense_header_view";
		super.enquiryTableName = "org_income_expense_header_view";
		this.module = getArgument("module");
	}
	
	public void onCreate$window_IncomeExpenseList(Event event) {
		logger.debug(Literal.ENTERING);

		if ("ENQ".equals(this.module)) {
			enqiryModule = true;
		}

		doSetFieldProperties();
		// Set the page level components.
		setPageComponents(window_IncomeExpenseList, borderLayout_IncomeExpenseList, listBoxIncomeExpense,
				pagingIncomeExpenseList);
		setItemRender(new IncomeExpenseDetailListModelItemRender());

		// Register buttons and fields.
		//registerButton(button_IncomeExpenseList_NewIncomeExpense, "button_OrganizationSchoolList_btnNew", true);
		registerButton(button_IncomeExpenseList_IncomeExpenseSearch);

		registerField("id");
		registerField("custCif", listheader_CIF, SortOrder.ASC, cif, sortOperator_CIF, Operators.STRING);
		registerField("financialyear", listheader_FinancialYear, SortOrder.ASC, financialYear, sortOperator_FinancialYear, Operators.STRING);
		registerField("name", listheader_Name, SortOrder.ASC, name, sortOperator_Name, Operators.STRING);
		//registerField("category", listheader_Category, SortOrder.ASC, category, sortOperator_Category,Operators.STRING);
		
		// Render the page and display the data.
		doRenderPage();
		search();
	}
	
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_OrganizationList_OrganizationSearch(Event event) {
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
	
	public void onClick$button_IncomeExpenseList_NewIncomeExpense(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		IncomeExpenseHeader incExpDetail = new IncomeExpenseHeader();
		incExpDetail.setNewRecord(true);
		incExpDetail.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(incExpDetail);

		logger.debug(Literal.LEAVING);
	}
	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onIncomeExpenseItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		// Get the selected record.
		Listitem selectedItem = this.listBoxIncomeExpense.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		IncomeExpenseHeader incExpenseDetail = incomeExpenseDetailService.getIncomeExpense(id, "_View");

		if (incExpenseDetail == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  AND  Id = ");
		whereCond.append(incExpenseDetail.getId());
		whereCond.append(" AND  version=");
		whereCond.append(incExpenseDetail.getVersion());

		if (doCheckAuthority(incExpenseDetail, whereCond.toString())) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && incExpenseDetail.getWorkflowId() == 0) {
				incExpenseDetail.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(incExpenseDetail);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param fieldinvestigation
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(IncomeExpenseHeader incExpHeader) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("incomeExpenseHeader", incExpHeader);
		arg.put("incomeExpenseDetailListCtrl", this);
		arg.put("enqiryModule", enqiryModule);
		arg.put("module", module);

		try {
			if (incExpHeader.isNewRecord()) {
				Executions.createComponents("/WEB-INF/pages/Organization/School/SchoolOrganizationSelect.zul", null, arg);
			} else {
				Executions.createComponents("/WEB-INF/pages/Organization/School/IncomeExpenseDetailDialog.zul", null, arg);
			}
		} catch (Exception e) {
			logger.error("Exception:", e);
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
}

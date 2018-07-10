package com.pennant.webui.sampling;

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

import com.pennant.webui.sampling.model.SamplingListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.service.sampling.SamplingService;

public class SamplingListCtrl extends GFCBaseListCtrl<Sampling> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(SamplingListCtrl.class);

	protected Window window_SamplingList;
	protected Borderlayout borderLayout_SamplingList;
	protected Paging pagingSamplingList;
	protected Listbox listBoxSampling;

	// List headers
	protected Listheader listheader_CIF;
	protected Listheader listheader_CustName;
	protected Listheader listheader_LoanReference;
	protected Listheader listheader_LoanType;

	// checkRights
	protected Button button_SamplingList_SamplingSearch;

	// Search Fields
	protected Listbox sortOperator_CIF;
	protected Listbox sortOperator_CustName;
	protected Listbox sortOperator_LoanReference;
	protected Listbox sortOperator_LoanType;

	protected Textbox cif;
	protected Textbox custName;
	protected Textbox loanReference;
	protected Textbox loanType;

	private String module = "";

	@Autowired
	private transient SamplingService samplingService;

	/**
	 * default constructor.<br>
	 */
	public SamplingListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Sampling";
		super.pageRightName = "SamplingList";
		super.tableName = "sampling_Tview";
		super.queueTableName = "sampling_Tview";
		super.enquiryTableName = "sampling_view";
		this.module = getArgument("module");
	}

	/**
	 * The framework calls this event handler when an application requests that
	 * the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_SamplingList(Event event) {
		logger.debug(Literal.ENTERING);

		if ("ENQ".equals(this.module)) {
			enqiryModule = true;
		}

		doSetFieldProperties();
		// Set the page level components.
		setPageComponents(window_SamplingList, borderLayout_SamplingList, listBoxSampling, pagingSamplingList);
		setItemRender(new SamplingListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_SamplingList_SamplingSearch);

		registerField("id");
		registerField("finTypeDesc");
		registerField("custCif", listheader_CIF, SortOrder.ASC, cif, sortOperator_CIF, Operators.STRING);
		registerField("custShrtName", listheader_CustName, SortOrder.ASC, custName, sortOperator_CustName,
				Operators.STRING);
		registerField("keyReference", listheader_LoanReference, SortOrder.ASC, loanReference,
				sortOperator_LoanReference, Operators.STRING);
		registerField("finType", listheader_LoanType, SortOrder.ASC, loanType,
				sortOperator_LoanType, Operators.STRING);

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_SamplingList_SamplingSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user opens a record to view
	 * it's details. Show the dialog page with the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onSamplingItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxSampling.getSelectedItem();
		final Sampling tempSampling = (Sampling) selectedItem.getAttribute("data");
		Sampling sampling = samplingService.getSampling(tempSampling, "_view");

		if (sampling == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  AND  Id = ");
		whereCond.append(sampling.getId());
		whereCond.append(" AND  version=");
		whereCond.append(sampling.getVersion());

		if (doCheckAuthority(sampling, whereCond.toString())) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && sampling.getWorkflowId() == 0) {
				sampling.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(sampling);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param sampling
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Sampling sampling) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("sampling", sampling);
		arg.put("samplingListCtrl", this);
		arg.put("enqiryModule", enqiryModule);

		try {
			Executions.createComponents("/WEB-INF/pages/Sampling/SamplingDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the print button
	 * to print the results.
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

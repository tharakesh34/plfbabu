package com.pennant.webui.applicationmaster.settlementTypeDetail;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import com.pennant.backend.model.applicationmaster.SettlementTypeDetail;
import com.pennant.backend.service.applicationmaster.SettlementTypeDetailService;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class SettlementTypeDetailListCtrl extends GFCBaseListCtrl<SettlementTypeDetail> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(SettlementTypeDetailListCtrl.class);

	protected Window window_SettlementTypeDetailList;
	protected Borderlayout borderLayout_SettlementTypeDetailList;
	protected Paging pagingSettlementTypeDetailList;
	protected Listbox listBoxSettlementTypeDetail;

	// List headers
	protected Listheader listheader_SettlementCode;
	protected Listheader listheader_SettlementDesc;

	// checkRights
	protected Button button_SettlementTypeDetailList_NewSettlementTypeDetail;
	protected Button button_SettlementTypeDetailList_SettlementTypeDetailSearchDialog;

	// Search Fields
	protected Textbox settlementCode; // autowired
	protected Textbox settlementDesc; // autowired

	protected Listbox sortOperator_Code;
	protected Listbox sortOperator_Desc;

	private transient SettlementTypeDetailService settlementTypeDetailService;

	/**
	 * default constructor.<br>
	 */
	public SettlementTypeDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "SettlementTypeDetail";
		super.pageRightName = "SettlementTypeDetailList";
		super.tableName = "Settlement_Types_AView";
		super.queueTableName = "Settlement_Types_View";
		super.enquiryTableName = "Settlement_Types_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_SettlementTypeDetailList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_SettlementTypeDetailList, borderLayout_SettlementTypeDetailList,
				listBoxSettlementTypeDetail, pagingSettlementTypeDetailList);
		setItemRender(new SettlementTypeDetailListListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_SettlementTypeDetailList_SettlementTypeDetailSearchDialog);
		registerButton(button_SettlementTypeDetailList_NewSettlementTypeDetail,
				"button_SettlementTypeDetailList_NewSettlementTypeDetail", true);

		registerField("ID");
		registerField("settlementCode", listheader_SettlementCode, SortOrder.NONE, settlementCode, sortOperator_Code,
				Operators.STRING);
		registerField("settlementDesc", listheader_SettlementDesc, SortOrder.NONE, settlementDesc, sortOperator_Desc,
				Operators.STRING);
		registerField("active");

		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_SettlementTypeDetailList_SettlementTypeDetailSearchDialog(Event event) {
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
	public void onClick$button_SettlementTypeDetailList_NewSettlementTypeDetail(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		SettlementTypeDetail settlementTypeDetail = new SettlementTypeDetail();
		settlementTypeDetail.setNewRecord(true);
		settlementTypeDetail.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(settlementTypeDetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onSettlementTypeDetailItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxSettlementTypeDetail.getSelectedItem();

		if (selectedItem == null) {
			return;
		}

		final long settlementTypeID = (long) selectedItem.getAttribute("id");
		SettlementTypeDetail settlementTypeDetail = getSettlementTypeDetailService()
				.getSettlementById(settlementTypeID);

		if (settlementTypeDetail == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " where id =?";
		if (doCheckAuthority(settlementTypeDetail, whereCond,
				new Object[] { settlementTypeDetail.getSettlementCode() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && settlementTypeDetail.getWorkflowId() == 0) {
				settlementTypeDetail.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(settlementTypeDetail);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param gender The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(SettlementTypeDetail settlementTypeDetail) {
		logger.debug("Entering");
		Map<String, Object> arg = getDefaultArguments();
		arg.put("settlementTypeDetail", settlementTypeDetail);
		arg.put("settlementTypeDetailListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/SettlementDetails/SettlementTypeDetailDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
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

	public SettlementTypeDetailService getSettlementTypeDetailService() {
		return settlementTypeDetailService;
	}

	public void setSettlementTypeDetailService(SettlementTypeDetailService settlementTypeDetailService) {
		this.settlementTypeDetailService = settlementTypeDetailService;
	}

}

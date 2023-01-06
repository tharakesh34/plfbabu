package com.pennant.webui.financemanagement.excesstransfer;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.excessheadmaster.FinExcessTransfer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.excessheadmaster.ExcessTransferService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.webui.excessheadmaster.model.ExcessTransferListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class ExcessTransferListCtrl extends GFCBaseListCtrl<FinExcessTransfer> {

	private static final long serialVersionUID = 778410382420505812L;
	private static final Logger logger = LogManager.getLogger(ExcessTransferListCtrl.class);

	protected Window window_ExcessTransferList;
	protected Borderlayout borderLayout_ExcessTransferList;
	protected Paging pagingExcessTransferList;
	protected Listbox listBoxExcessTransfer;

	protected Button button_ExcessTransferList_NewExcessTransfer;
	protected Button button_ExcessTransferList_ExcessTransferSearchDialog;

	protected Longbox Id;
	protected Datebox transferDate;
	protected ExtendedCombobox customer;
	protected ExtendedCombobox finReference;

	protected Listheader listheader_TransferId;
	protected Listheader listheader_CustCIF;
	protected Listheader listheader_FinReference;
	protected Listheader listheader_TransferDate;

	protected Listheader listheader_RecordStatus;
	protected Listheader listheader_NextRoleCode;

	protected Listbox sortOperator_transferId;
	protected Listbox sortOperator_customer;
	protected Listbox sortOperator_loanReference;
	protected Listbox sortOperator_transferDate;

	private FinanceMain financeMain;
	private ExcessTransferService excessTransferService;
	private FinanceMainService financeMainService;

	/**
	 * default constructor.<br>
	 */
	public ExcessTransferListCtrl() {
		super();
	}

	protected void doSetProperties() {
		super.moduleCode = "FinExcessTransfer";
		super.pageRightName = "ExcessTransferList";
		super.tableName = "excess_transfer_details_view";
		super.queueTableName = "excess_transfer_details_view";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_ExcessTransferList(Event event) {
		logger.debug("Entering " + event.toString());

		// Set the page level components.
		setPageComponents(window_ExcessTransferList, borderLayout_ExcessTransferList, listBoxExcessTransfer,
				pagingExcessTransferList);
		setItemRender(new ExcessTransferListModelItemRenderer());

		registerButton(button_ExcessTransferList_NewExcessTransfer, "button_FinExcessTransferList_NewExcessTransfer",
				true);
		registerButton(button_ExcessTransferList_ExcessTransferSearchDialog);

		registerField("Id", listheader_TransferId, SortOrder.NONE, Id, sortOperator_transferId, Operators.STRING);

		registerField("custCIF", listheader_CustCIF, SortOrder.NONE, customer, sortOperator_customer, Operators.STRING);

		registerField("finReference", listheader_FinReference, SortOrder.NONE, finReference, sortOperator_loanReference,
				Operators.STRING);

		registerField("transferDate", listheader_TransferDate, SortOrder.NONE, transferDate, sortOperator_transferDate,
				Operators.DATE);

		registerField("recordStatus", listheader_RecordStatus);
		registerField("recordType", listheader_RecordType);
		doSetFieldProperties();
		doRenderPage();
		search();

		this.button_ExcessTransferList_NewExcessTransfer.setVisible(true);

		logger.debug("Leaving " + event.toString());
	}

	public void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.customer.setModuleName("Customer");
		this.customer.setValueColumn("CustCIF");
		this.customer.setDescColumn("CustShrtName");
		this.customer.setValidateColumns(new String[] { "CustCIF" });

		this.finReference.setModuleName("FinanceMain");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDescColumn("FinType");
		this.finReference.setValidateColumns(new String[] { "FinReference" });

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_ExcessTransferList_ExcessTransferSearchDialog(Event event) {
		search();
	}

	// TODO CH : To be changed to a single zul and controller
	public void onClick$button_ExcessTransferList_NewExcessTransfer(Event event) {
		logger.debug("Entering ");

		Map<String, Object> map = new HashMap<String, Object>();
		FinExcessTransfer finExcessTransfer = new FinExcessTransfer();
		finExcessTransfer.setNewRecord(true);
		finExcessTransfer.setWorkflowId(getWorkFlowId());
		map.put("finExcessTransfer", finExcessTransfer);
		map.put("excessTransferListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/ExcessTransfer/SelectExcessTransfer.zul",
					null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving " + event.toString());
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
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	public void onExcessTransferItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxExcessTransfer.getSelectedItem();
		if (selectedItem == null) {
			return;
		}
		FinExcessTransfer finExcessTransfer = (FinExcessTransfer) selectedItem.getAttribute("finExcessTransfer");
		finExcessTransfer = excessTransferService.getExcessTransfer(finExcessTransfer.getId());
		finExcessTransfer.setFinExcessAmount(
				excessTransferService.getFinExcessAmountById(finExcessTransfer.getTransferFromId()));

		if (finExcessTransfer == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		String whereCond = " where Id=?";

		if (doCheckAuthority(finExcessTransfer, whereCond, new Object[] { finExcessTransfer.getId() })) {
			if (isWorkFlowEnabled() && finExcessTransfer.getWorkflowId() == 0) {
				finExcessTransfer.setWorkflowId(getWorkFlowId());
			}
			doShowDialog(finExcessTransfer);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	private void doShowDialog(FinExcessTransfer finExcessTransfer) {
		logger.debug("Entering ");

		final Map<String, Object> map = new HashMap<String, Object>();

		FinExcessTransfer transferData = excessTransferService.getExcessTransferData(finExcessTransfer.getFinId(),
				finExcessTransfer.getId());
		FinanceMain financeMain = financeMainService.getFinanceMainByFinRef(finExcessTransfer.getFinId());
		map.put("finExcessTransfer", finExcessTransfer);
		map.put("ccyFormat", CurrencyUtil.getFormat(SysParamUtil.getAppCurrency()));
		map.put("moduleCode", this.moduleCode);
		map.put("excessTransferListCtrl", this);
		map.put("transferData", transferData);
		map.put("financeMain", financeMain);

		Executions.createComponents("/WEB-INF/pages/FinanceManagement/ExcessTransfer/ExcessTransferDialog.zul", null,
				map);

		logger.debug("Leaving ");

	}

	public ExcessTransferService getExcessTransferService() {
		return excessTransferService;
	}

	public void setExcessTransferService(ExcessTransferService excessTransferService) {
		this.excessTransferService = excessTransferService;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public FinanceMainService getFinanceMainService() {
		return financeMainService;
	}

	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

}

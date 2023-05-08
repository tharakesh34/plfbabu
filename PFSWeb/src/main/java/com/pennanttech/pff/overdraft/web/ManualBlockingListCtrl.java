package com.pennanttech.pff.overdraft.web;

import java.io.Serializable;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.overdraft.model.OverdraftLimit;
import com.pennanttech.pff.overdraft.service.OverdrafLoanService;

public class ManualBlockingListCtrl extends GFCBaseListCtrl<OverdraftLimit> {
	private static final long serialVersionUID = -4317219679571013354L;

	protected Window window_ManualBlockingList;
	protected Borderlayout borderlayout_ManualBlockingList;
	protected Paging pagingManualBlockingList;
	protected Listbox listBoxManualBlocking;

	protected Button button_ManualBlockingList_ManualBlockingSearchDialog;
	protected Button button_ManualBlockingList_PrintList;
	protected Button button_ManualBlockingList_New;
	protected Button btnRefresh;

	protected ExtendedCombobox custCIF;
	protected ExtendedCombobox finReference;

	protected Listbox sortOperator_custCIF;
	protected Listbox sortOperator_FinReference;

	protected Listheader listheader_custCIF;
	protected Listheader listheader_FinReference;

	private OverdrafLoanService OverdrafLoanService;

	/**
	 * default constructor.<br>
	 */
	public ManualBlockingListCtrl() {
		super();
	}

	protected void doSetProperties() {
		super.moduleCode = "OverdraftLimit";
		super.tableName = "OverDraft_Loan_Limits_View";
		super.queueTableName = "OverDraft_Loan_Limits_View";
	}

	public void onCreate$window_ManualBlockingList(Event event) {
		// Set the page level components.
		setPageComponents(window_ManualBlockingList, borderlayout_ManualBlockingList, listBoxManualBlocking,
				pagingManualBlockingList);
		setItemRender(new ManualBlockingListModelItemRenderer());

		registerButton(button_ManualBlockingList_ManualBlockingSearchDialog);

		registerField("CUSTCIF", listheader_custCIF, SortOrder.NONE, custCIF, sortOperator_custCIF, Operators.STRING);
		registerField("FINREFERENCE", listheader_FinReference, SortOrder.NONE, finReference, sortOperator_FinReference,
				Operators.STRING);

		doSetFieldProperties();
		doRenderPage();
		search();
	}

	public void onClick$button_ManualBlockingList_ManualBlockingSearchDialog(Event event) {
		search();
	}

	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	public void onClick$button_ManualBlockingList_PrintList(Event event) throws InterruptedException {
		new PTListReportUtils("OverdraftLimit", super.searchObject, this.pagingManualBlockingList.getTotalSize() + 1);
	}

	public void doSetFieldProperties() {

		this.custCIF.setModuleName("Customer");
		this.custCIF.setValueColumn("CustCIF");
		this.custCIF.setDescColumn("CustShrtName");
		this.custCIF.setValidateColumns(new String[] { "CustCIF" });

		this.finReference.setModuleName("OverdraftLimit");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setValidateColumns(new String[] { "FinReference" });
		this.finReference.setDisplayStyle(2);
		this.finReference.setValidateColumns(new String[] { "FinReference" });
		this.finReference.setMaxlength(LengthConstants.LEN_REF);
		this.finReference.setTextBoxWidth(143);

		Filter[] filter = new Filter[1];
		filter[0] = new Filter("FinIsActive", 1, Filter.OP_EQUAL);

		this.finReference.setFilters(filter);

	}

	public void onClick$button_ManualBlockingList_New(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		OverdraftLimit odl = new OverdraftLimit();
		odl.setNewRecord(true);
		odl.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(odl);

		logger.debug("Leaving");
	}

	public void onManualBlockingLimitItemDoubleClicked(Event event) {
		logger.info(Literal.ENTERING);

		final Listitem item = this.listBoxManualBlocking.getSelectedItem();

		if (item == null) {
			return;
		}
		OverdraftLimit odl = (OverdraftLimit) item.getAttribute("OverdraftLoanLimits");

		long finID = odl.getFinID();

		OverdraftLimit limit = OverdrafLoanService.getLimitByReference(finID, "_View");

		if (limit == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " Where ID = ?";

		if (doCheckAuthority(limit, whereCond, new Object[] { limit.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && limit.getWorkflowId() == 0) {
				limit.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(limit);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.info(Literal.LEAVING);

	}

	private void doShowDialogPage(OverdraftLimit odl) {
		logger.info(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("OverdraftLoanLimits", odl);
		arg.put("manualBlockingListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Limit/LimitDetails/ManualBlockingDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.info(Literal.LEAVING);

	}

	public class ManualBlockingListModelItemRenderer implements ListitemRenderer<OverdraftLimit>, Serializable {
		private static final long serialVersionUID = 5003155481625938369L;

		public ManualBlockingListModelItemRenderer() {
		    super();
		}

		@Override
		public void render(Listitem item, OverdraftLimit odlh, int count) throws Exception {

			Listcell lc;
			lc = new Listcell(odlh.getCustCIF());
			lc.setParent(item);
			lc = new Listcell(odlh.getFinReference());
			lc.setParent(item);
			lc = new Listcell(odlh.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(odlh.getRecordType()));
			lc.setParent(item);

			item.setAttribute("OverdraftLoanLimits", odlh);

			ComponentsCtrl.applyForward(item, "onDoubleClick=onManualBlockingLimitItemDoubleClicked");
		}

	}

	@Autowired
	public void setOverdrafLoanService(OverdrafLoanService overdrafLoanService) {
		OverdrafLoanService = overdrafLoanService;
	}

}

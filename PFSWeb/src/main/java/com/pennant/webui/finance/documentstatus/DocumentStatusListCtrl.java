package com.pennant.webui.finance.documentstatus;

import java.io.Serializable;
import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.documents.model.DocumentStatus;

public class DocumentStatusListCtrl extends GFCBaseListCtrl<DocumentStatus> {
	private static final long serialVersionUID = 5230337712682701210L;

	protected Window window_DocumentStatusList;
	protected Borderlayout borderlayout_DocumentStatusList;
	protected Paging pagingDocumentStatusList;
	protected Listbox listBoxDocumentStaus;

	protected Button button_DocumentStatusList_DocumentStatusSearchDialog;
	protected Button button_DocumentStatusList_PrintList;
	protected Button btnRefresh;

	protected ExtendedCombobox custCIF;
	protected ExtendedCombobox branchCode;
	protected Datebox startDate;
	protected Datebox maturityDate;
	protected ExtendedCombobox finReference;
	protected ExtendedCombobox finProduct;
	protected ExtendedCombobox finType;
	protected ExtendedCombobox finCcy;

	protected Listbox sortOperator_custCIF;
	protected Listbox sortOperator_Branch;
	protected Listbox sortOperator_StartDate;
	protected Listbox sortOperator_MaturityDate;
	protected Listbox sortOperator_FinReference;
	protected Listbox sortOperator_FinProduct;
	protected Listbox sortOperator_FinType;
	protected Listbox sortOperator_FinCcy;

	protected Listheader listheader_CustomerCIF;
	protected Listheader listheader_CustomerName;
	protected Listheader listheader_FinReference;
	protected Listheader listheader_FinType;

	/**
	 * default constructor.<br>
	 */
	public DocumentStatusListCtrl() {
		super();
	}

	protected void doSetProperties() {
		super.moduleCode = "DocumentStatus";
		super.tableName = "DocumentStatus_View";
		super.queueTableName = "DocumentStatus_View";
	}

	public void onCreate$window_DocumentStatusList(Event event) {
		// Set the page level components.
		setPageComponents(window_DocumentStatusList, borderlayout_DocumentStatusList, listBoxDocumentStaus,
				pagingDocumentStatusList);
		setItemRender(new DocumentStatusListModelItemRenderer());

		registerButton(button_DocumentStatusList_DocumentStatusSearchDialog);

		registerField("CustCIF", listheader_CustomerCIF, SortOrder.NONE, custCIF, sortOperator_custCIF,
				Operators.STRING);
		registerField("FinBranch", branchCode, SortOrder.NONE, sortOperator_Branch, Operators.STRING);
		registerField("FinStartDate", startDate, SortOrder.NONE, sortOperator_StartDate, Operators.DATE);
		registerField("MaturityDate", maturityDate, SortOrder.NONE, sortOperator_MaturityDate, Operators.DATE);
		registerField("FinCategory", finProduct, SortOrder.NONE, sortOperator_FinProduct, Operators.STRING);
		registerField("FinReference", listheader_FinReference, SortOrder.NONE, finReference, sortOperator_FinReference,
				Operators.STRING);
		registerField("Fintype", listheader_FinType, SortOrder.NONE, finType, sortOperator_FinType, Operators.STRING);
		registerField("FinCcy", finCcy, SortOrder.NONE, sortOperator_FinCcy, Operators.STRING);
		registerField("CustShrtName", listheader_CustomerName);
		registerField("FinTypeDesc");
		registerField("BranchDesc");
		registerField("ScheduleMethod");
		registerField("ProfitDaysBasis");
		registerField("FinIsActive");

		doSetFieldProperties();
		doRenderPage();
		search();
	}

	public void onClick$button_DocumentStatusList_DocumentStatusSearchDialog(Event event) {
		search();
	}

	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	public void onClick$button_DocumentStatusList_PrintList(Event event) throws InterruptedException {
		new PTListReportUtils("DocumentStatus", super.searchObject, this.pagingDocumentStatusList.getTotalSize() + 1);
	}

	public void doSetFieldProperties() {

		this.custCIF.setModuleName("Customer");
		this.custCIF.setValueColumn("CustCIF");
		this.custCIF.setDescColumn("CustShrtName");
		this.custCIF.setValidateColumns(new String[] { "CustCIF" });

		this.finReference.setModuleName("FinanceMainView");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDescColumn("FinType");
		this.finReference.setDisplayStyle(2);
		this.finReference.setValidateColumns(new String[] { "FinReference" });
		this.finReference.setMaxlength(LengthConstants.LEN_REF);
		this.finReference.setTextBoxWidth(143);

		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("FinType");
		this.finType.setDescColumn("FinTypeDesc");
		this.finType.setValidateColumns(new String[] { "FinType" });

		this.finProduct.setModuleName("FinanceWorkFlow");
		this.finProduct.setValueColumn("FinType");
		this.finProduct.setDescColumn("LovDescFinTypeName");
		this.finProduct.setValidateColumns(new String[] { "FinType" });

		Filter[] filter = new Filter[1];
		filter[0] = new Filter("FinEvent", FinServiceEvent.ORG, Filter.OP_EQUAL);
		this.finProduct.setFilters(filter);

		this.finCcy.setMaxlength(LengthConstants.LEN_CURRENCY);
		this.finCcy.setModuleName("Currency");
		this.finCcy.setValueColumn("CcyCode");
		this.finCcy.setDescColumn("CcyDesc");
		this.finCcy.setValidateColumns(new String[] { "CcyCode" });

		this.branchCode.setModuleName("Branch");
		this.branchCode.setValueColumn("BranchCode");
		this.branchCode.setDescColumn("BranchDesc");
		this.branchCode.setValidateColumns(new String[] { "BranchCode" });

		this.startDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.maturityDate.setFormat(DateFormat.SHORT_DATE.getPattern());
	}

	public void onDocumentStatusItemDoubleClicked(Event event) {
		logger.info(Literal.ENTERING);

		final Listitem item = this.listBoxDocumentStaus.getSelectedItem();

		if (item == null) {
			return;
		}
		FinanceMain fm = (FinanceMain) item.getAttribute("financeMain");

		doShowDialogPage(fm);

		logger.info(Literal.LEAVING);

	}

	private void doShowDialogPage(FinanceMain fm) {
		logger.info(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("documentStatusListCtrl", this);
		arg.put("financeMain", fm);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/DocumentStatus/DocumentStatusDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.info(Literal.LEAVING);

	}

	public class DocumentStatusListModelItemRenderer implements ListitemRenderer<DocumentStatus>, Serializable {
		private static final long serialVersionUID = 5003155481625938369L;

		public DocumentStatusListModelItemRenderer() {
		    super();
		}

		@Override
		public void render(Listitem item, DocumentStatus ds, int count) throws Exception {

			Listcell lc;
			lc = new Listcell(ds.getCustCIF());
			lc.setParent(item);
			lc = new Listcell(ds.getCustShrtName());
			lc.setParent(item);
			lc = new Listcell(ds.getFinReference());
			lc.setParent(item);
			lc = new Listcell(ds.getFinType());
			lc.setParent(item);

			FinanceMain fm = new FinanceMain();

			fm.setFinReference(ds.getFinReference());
			fm.setFinType(ds.getFinType());
			fm.setFinBranch(ds.getFinBranch());
			fm.setFinStartDate(ds.getFinStartDate());
			fm.setMaturityDate(ds.getMaturityDate());
			fm.setFinCcy(ds.getFinCcy());
			fm.setScheduleMethod(ds.getScheduleMethod());
			fm.setProfitDaysBasis(ds.getProfitDaysBasis());
			fm.setLovDescCustCIF(ds.getCustCIF());
			fm.setLovDescCustShrtName(ds.getCustShrtName());
			fm.setFinCategory(ds.getFinCategory());
			fm.setLovDescFinBranchName(ds.getBranchDesc());
			fm.setLovDescFinTypeName(ds.getFinTypeDesc());
			fm.setFinIsActive(ds.isFinIsActive());
			fm.setWorkflowId(ds.getWorkflowId());
			fm.setRoleCode(ds.getRoleCode());
			fm.setNextRoleCode(ds.getNextRoleCode());
			fm.setTaskId(ds.getTaskId());
			fm.setNextTaskId(ds.getNextTaskId());
			fm.setVersion(ds.getVersion());
			fm.setLastMntBy(ds.getLastMntBy());
			fm.setLastMntOn(ds.getLastMntOn());
			fm.setRecordStatus(ds.getRecordStatus());
			fm.setRecordType(ds.getRecordType());

			if (ds.getId() == Long.MIN_VALUE || ds.getId() == 0) {
				fm.setWorkflowId(getWorkFlowId());
			}

			item.setAttribute("financeMain", fm);

			ComponentsCtrl.applyForward(item, "onDoubleClick=onDocumentStatusItemDoubleClicked");
		}

	}
}

package com.pennant.pff.noc.webui;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.pff.letter.LetterType;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennant.pff.noc.service.GenerateLetterService;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.ISearch;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;

public class GenerateLetterListCtrl extends GFCBaseListCtrl<GenerateLetter> {
	private static final long serialVersionUID = -56814543607201850L;

	protected Window windowGenerateLetterList;
	protected Borderlayout blGenerateLetterList;
	protected Paging pagingGenerateLetterList;
	protected Listbox lbGenerateLetter;

	protected Button btnNew;
	protected Button btnSearch;

	protected Listheader finRefHeader;
	protected Listheader custNamHeader;
	protected Listheader coreBankIDHeader;
	protected Listheader branchHeader;
	protected Listheader productHeader;
	protected Listheader letterTypeHeader;
	protected Listheader download;

	protected Listbox finRefSort;
	protected Listbox branchSort;
	protected Listbox corebankidSort;
	protected Listbox custCifSort;

	protected Textbox finReference;
	protected ExtendedCombobox branch;
	protected Textbox coreBankID;
	protected Textbox custCIF;
	protected Button btnSearchCustCIF;

	private transient GenerateLetterService generateLetterService;
	protected JdbcSearchObject<Customer> custCIFSearchObject;
	private transient CustomerDetailsService customerDetailsService;
	protected long custId = Long.MIN_VALUE;
	private Customer customer;

	public GenerateLetterListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "GenerateLetter";
		super.pageRightName = "GenerateLetterList";
		super.tableName = "Loan_Letter_Manual";
		super.queueTableName = "Loan_Letter_Manual";
	}

	public void onCreate$windowGenerateLetterList(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(windowGenerateLetterList, blGenerateLetterList, lbGenerateLetter, pagingGenerateLetterList);

		setItemRender(new GenerateLetterModelItemRenderer());

		registerButton(btnNew, "button_GenerateLetter_btnNew", true);
		registerButton(btnSearch);

		this.finRefHeader.setSortAscending(new FieldComparator("finReference", true));
		this.custNamHeader.setSortDescending(new FieldComparator("custAcctHolderName", false));
		this.coreBankIDHeader.setSortAscending(new FieldComparator("custCoreBank", true));
		this.branchHeader.setSortDescending(new FieldComparator("finBranch", false));
		this.productHeader.setSortAscending(new FieldComparator("product", true));
		this.letterTypeHeader.setSortDescending(new FieldComparator("letterType", false));

		this.finRefSort.setModel(new ListModelList<>(new SearchOperators().getMultiStringOperators()));
		this.finRefSort.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.branchSort.setModel(new ListModelList<>(new SearchOperators().getMultiStringOperators()));
		this.branchSort.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.corebankidSort.setModel(new ListModelList<>(new SearchOperators().getMultiStringOperators()));
		this.corebankidSort.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.custCifSort.setModel(new ListModelList<>(new SearchOperators().getMultiStringOperators()));
		this.custCifSort.setItemRenderer(new SearchOperatorListModelItemRenderer());

		doSetFieldProperties();

		fillListData();

		doRenderPage();

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		this.branch.setModuleName("CSDBranch");
		this.branch.setDisplayStyle(2);
		this.branch.setValueColumn("BranchCode");
		this.branch.setDescColumn("BranchDesc");
		this.branch.setValidateColumns(new String[] { "BranchCode" });
	}

	public void onFulfill$branch(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = branch.getObject();
		if (dataObject instanceof String) {
			this.branch.setValue("");
			this.branch.setDescription("");
		} else {
			Branch details = (Branch) dataObject;
			if (details != null) {
				this.branch.setValue(String.valueOf(details.getBranchCode()));
				this.branch.setDescription(details.getBranchDesc());
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSearch(Event event) {
		logger.debug(Literal.ENTERING);

		List<GenerateLetter> excludeCodes = this.generateLetterService.getResult(getSearchFilters());

		this.lbGenerateLetter.setItemRenderer(new GenerateLetterModelItemRenderer());

		this.pagedListWrapper.initList(excludeCodes, lbGenerateLetter, pagingGenerateLetterList);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnRefresh(Event event) {
		logger.debug(Literal.ENTERING);

		this.finRefSort.setSelectedIndex(0);
		this.branchSort.setSelectedIndex(0);
		this.corebankidSort.setSelectedIndex(0);
		this.custCifSort.setSelectedIndex(0);

		this.finReference.setValue("");
		this.branch.setValue("");
		this.coreBankID.setValue("");
		this.custCIF.setValue("");

		fillListData();

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnNew(Event event) {
		logger.debug(Literal.ENTERING);

		GenerateLetter gl = new GenerateLetter();
		gl.setNewRecord(true);
		gl.setWorkflowId(getWorkFlowId());
		gl.setCreatedBy(getUserWorkspace().getLoggedInUser().getUserId());

		Map<String, Object> arg = getDefaultArguments();
		arg.put("moduleCode", this.moduleCode);
		arg.put("generateLetter", gl);
		arg.put("generateLetterListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/NOC/SelectGenerateLetter.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$print(Event event) {
		logger.debug(Literal.ENTERING);

		doPrintResults(this.generateLetterService.getPrintLetters(getWorkFlowRoles()));

		logger.debug(Literal.LEAVING);
	}

	public void onGenerateLetterItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		long id = (Long) this.lbGenerateLetter.getSelectedItem().getAttribute("Id");

		GenerateLetter letters = this.generateLetterService.getLetter(id);

		generateLetterService.getFinanceDetailById(letters);

		if (letters == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		String whereCond = "Where Id = ?";

		if (doCheckAuthority(letters, whereCond, new Object[] { letters.getFinID() })) {
			if (isWorkFlowEnabled() && letters.getWorkflowId() == 0) {
				letters.setWorkflowId(getWorkFlowId());
			}

			doShowDialogPage(letters);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialogPage(GenerateLetter gl) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("moduleCode", this.moduleCode);
		arg.put("generateLetter", gl);
		arg.put("generateLetterListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/NOC/GenerateLetterDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void fillListData() {

		List<GenerateLetter> letters = this.generateLetterService.getResult(getSearchFilters());

		this.lbGenerateLetter.setItemRenderer(new GenerateLetterModelItemRenderer());

		this.pagedListWrapper.initList(letters, lbGenerateLetter, pagingGenerateLetterList);
	}

	private ISearch getSearchFilters() {
		ISearch search = new Search();

		String selectedfinReference = this.finReference.getValue();
		if (StringUtils.isNotEmpty(this.finReference.getValue())) {
			search.getFilters()
					.add(new Filter("gl.FinReference", selectedfinReference, this.finRefSort.getSelectedIndex()));
		}

		String selectedBranch = this.branch.getValue();
		if (StringUtils.isNotEmpty(selectedBranch)) {
			search.getFilters().add(new Filter("gl.FinBranch", selectedBranch, this.branchSort.getSelectedIndex()));
		}

		String coreBankId = this.coreBankID.getValue();
		if (StringUtils.isNotEmpty(coreBankId)) {
			search.getFilters().add(new Filter("gl.CustCoreBank", coreBankId, this.corebankidSort.getSelectedIndex()));
		}

		String selectedCIF = this.custCIF.getValue();
		if (StringUtils.isNotEmpty(selectedCIF)) {
			search.getFilters().add(new Filter("gl.CustCIF", selectedCIF, this.custCifSort.getSelectedIndex()));
		}

		return search;
	}

	private class GenerateLetterModelItemRenderer implements ListitemRenderer<GenerateLetter>, Serializable {
		private static final long serialVersionUID = 6056180845898696437L;

		public GenerateLetterModelItemRenderer() {
			super();
		}

		@Override
		public void render(Listitem item, GenerateLetter gl, int arg2) throws Exception {
			LetterType letterType = LetterType.getType(gl.getLetterType());

			Listcell lc;
			lc = new Listcell(gl.getFinReference());
			lc.setParent(item);
			lc = new Listcell(gl.getCustAcctHolderName());
			lc.setParent(item);
			lc = new Listcell(String.valueOf(gl.getCustCoreBank()));
			lc.setParent(item);
			lc = new Listcell(gl.getFinBranch());
			lc.setParent(item);
			lc = new Listcell(gl.getProduct());
			lc.setParent(item);
			lc = new Listcell(letterType == null ? "" : letterType.getDescription());
			lc.setParent(item);
			lc = new Listcell(gl.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(gl.getRecordType()));
			lc.setParent(item);

			item.setAttribute("Id", gl.getId());

			ComponentsCtrl.applyForward(item, "onDoubleClick=onGenerateLetterItemDoubleClicked");
		}

	}

	public void onChange$custCIF(Event event) throws Exception {
		customer = fetchCustomerDataByCIF();
		addFilter(customer);
	}

	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING);

		doClearMessage();
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);

		logger.debug(Literal.LEAVING);
	}

	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) {
		logger.debug(Literal.ENTERING);

		this.custCIF.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;
		customer = (Customer) nCustomer;
		addFilter(customer);

		logger.debug(Literal.LEAVING);
	}

	private void addFilter(Customer customer) {
		logger.debug(Literal.ENTERING);

		if (customer != null && customer.getCustID() != 0) {
			this.custId = customer.getCustID();
			this.custCIF.setValue(customer.getCustCIF());

		} else {
			this.finReference.setValue("");
			this.custCIF.setValue("");
		}

		logger.debug(Literal.LEAVING);
	}

	public Customer fetchCustomerDataByCIF() {

		customer = new Customer();
		this.custCIF.setConstraint("");
		this.custCIF.setErrorMessage("");
		this.custCIF.clearErrorMessage();
		String cif = StringUtils.trimToEmpty(this.custCIF.getValue());

		if (this.custCIF.getValue().trim().isEmpty()) {
			MessageUtil.showError("Invalid Customer Please Select valid Customer");
			this.custId = 0;
		} else {
			customer = this.customerDetailsService.checkCustomerByCIF(cif, TableType.MAIN_TAB.getSuffix());
			if (customer != null) {
				this.custId = customer.getCustID();
			} else {
				MessageUtil.showError("Invalid Customer Please Select valid Customer");
			}
		}

		return customer;
	}

	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.finReference.setErrorMessage("");
		this.custCIF.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setGenerateLetterService(GenerateLetterService generateLetterService) {
		this.generateLetterService = generateLetterService;
	}

	@Autowired
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}
}

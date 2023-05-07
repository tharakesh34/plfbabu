package com.pennant.pff.noc.webui;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennant.pff.noc.service.GenerateLetterService;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.ISearch;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.web.util.MessageUtil;

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
	protected Listheader recordStatusHeader;
	protected Listheader recordTypeHeader;

	protected Listbox finRefSort;
	protected Listbox branchSort;
	protected Listbox corebankidSort;
	protected Listbox custCifSort;

	protected Textbox finReference;
	protected ExtendedCombobox branch;
	protected Textbox coreBankID;
	protected Textbox custCif;

	private transient GenerateLetterService generateLetterService;

	public GenerateLetterListCtrl() {
		super();
	}

	protected void doSetProperties() {
		super.moduleCode = "GenerateLetter";
		super.pageRightName = "GenerateLetterList";
		super.tableName = "Letter_Generate_Manual";
		super.queueTableName = "Letter_Generate_Manual";
	}

	public void onCreate$windowGenerateLetterList(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		setPageComponents(windowGenerateLetterList, blGenerateLetterList, lbGenerateLetter, pagingGenerateLetterList);

		setItemRender(new GenerateLetterModelItemRenderer());

		registerButton(btnNew, "button_GenerateLetter_btnNew", true);
		registerButton(btnSearch);
		registerField("finReference", finRefHeader, SortOrder.NONE, finReference, finRefSort, Operators.STRING);
		registerField("CustAcctHolderName", custNamHeader, SortOrder.NONE, null, null, Operators.STRING);
		registerField("CustCoreBank", coreBankIDHeader, SortOrder.NONE, coreBankID, corebankidSort, Operators.STRING);
		registerField("FinBranch", branchHeader, SortOrder.NONE, branch, branchSort, Operators.STRING);
		registerField("Product", productHeader, SortOrder.NONE, null, null, Operators.STRING);
		registerField("LetterType", letterTypeHeader, SortOrder.NONE, null, null, Operators.STRING);
		registerField("recordStatus", recordStatusHeader);
		registerField("recordType", recordTypeHeader);

		fillListData();

		doRenderPage();

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnSearch(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		List<GenerateLetter> excludeCodes = this.generateLetterService.getResult(getSearchFilters());

		this.lbGenerateLetter.setItemRenderer(new GenerateLetterModelItemRenderer());

		this.pagedListWrapper.initList(excludeCodes, lbGenerateLetter, pagingGenerateLetterList);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnRefresh(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doReset();

		fillListData();

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnNew(Event event) {
		logger.debug(Literal.ENTERING);

		GenerateLetter gl = new GenerateLetter();
		gl.setNewRecord(true);
		gl.setWorkflowId(getWorkFlowId());

		doShowDialogPage(gl);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$print(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doPrintResults(this.generateLetterService.getPrintLetters(getWorkFlowRoles()));

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onGenerateLetterItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		long id = (Long) this.lbGenerateLetter.getSelectedItem().getAttribute("FinID");

		GenerateLetter letters = this.generateLetterService.getLetter(id);

		if (letters == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		String whereCond = "Where FinID = ?";

		if (doCheckAuthority(letters, whereCond, new Object[] { letters.getFinID() })) {
			if (isWorkFlowEnabled() && letters.getWorkflowId() == 0) {
				letters.setWorkflowId(getWorkFlowId());
			}

			doShowDialogPage(letters);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void doShowDialogPage(GenerateLetter csb) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("generateLetter", csb);
		arg.put("generateLetterListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/GenerateLetter/GenerateLetterDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void fillListData() {
		List<GenerateLetter> letters = this.generateLetterService.getGenerateLetters(getWorkFlowRoles());

		this.lbGenerateLetter.setItemRenderer(new GenerateLetterModelItemRenderer());

		this.pagedListWrapper.initList(letters, lbGenerateLetter, pagingGenerateLetterList);
	}

	private ISearch getSearchFilters() {
		ISearch search = new Search();

		String finReference = this.finReference.getValue();
		if (StringUtils.isNotEmpty(finReference)) {
			search.getFilters().add(new Filter("gl.FinReference", finReference, this.finRefSort.getSelectedIndex()));
		}

		String branch = this.branch.getValue();
		if (StringUtils.isNotEmpty(branch)) {
			search.getFilters().add(new Filter("gl.Branch", branch, this.branchSort.getSelectedIndex()));
		}

		String coreBankId = this.coreBankID.getValue();
		if (StringUtils.isNotEmpty(coreBankId)) {
			search.getFilters().add(new Filter("gl.CoreBankID", coreBankId, this.corebankidSort.getSelectedIndex()));
		}

		String custCIF = this.custCif.getValue();
		if (StringUtils.isNotEmpty(custCIF)) {
			search.getFilters().add(new Filter("gl.CustCIF", custCIF, this.custCifSort.getSelectedIndex()));
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

			FinanceMain fm = gl.getReceiptDTO().getFinanceMain();
			Listcell lc;
			lc = new Listcell(fm.getFinReference());
			lc.setParent(item);
			lc = new Listcell(fm.getCustAcctHolderName());
			lc.setParent(item);
			lc = new Listcell(String.valueOf(fm.getCustCoreBank()));
			lc.setParent(item);
			lc = new Listcell(fm.getFinBranch());
			lc.setParent(item);
			lc = new Listcell(fm.getProduct());
			lc.setParent(item);

			item.setAttribute("FinID", fm.getFinID());

			ComponentsCtrl.applyForward(item, "onDoubleClick=onGenerateLetterItemDoubleClicked");
		}

	}

	@Autowired
	public void setGenerateLetterService(GenerateLetterService generateLetterService) {
		this.generateLetterService = generateLetterService;
	}
}

package com.pennant.pff.presentment.web;

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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.pff.mandate.MandateUtil;
import com.pennant.pff.presentment.ExcludeReasonCode;
import com.pennant.pff.presentment.model.PresentmentExcludeCode;
import com.pennant.pff.presentment.service.PresentmentExcludeCodeService;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.ISearch;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class PresentmentExcludeCodeListCtrl extends GFCBaseListCtrl<PresentmentExcludeCode> {
	private static final long serialVersionUID = -3571720185247491921L;

	protected Window windowPresentmentExcludeCode;
	protected Borderlayout borderLayoutPresentmentExcludeCodeList;
	protected Paging pagingPresentmentExcludeCodeList;
	protected Listbox listBoxPresentmentExcludeCode;
	protected Button buttonSearchDialog;
	protected Button button_PresentmentExcludeCodeList_NewButton;

	protected Combobox code;
	protected Textbox codeDesc;
	protected Textbox bounceCode;
	protected Combobox instrumentType;

	protected Listbox codeSort;
	protected Listbox codeDescSort;
	protected Listbox bounceCodeSort;
	protected Listbox instrumentTypeSort;

	protected Listheader codeHeader;
	protected Listheader codeDescHeader;
	protected Listheader instrumentTypeHeader;
	protected Listheader bounceCodeHeader;

	private transient PresentmentExcludeCodeService presentmentExcludeCodeService;

	private List<ValueLabel> excludeCodeList = ExcludeReasonCode.getExcludeCodes();
	private List<ValueLabel> mandateTypeList = MandateUtil.getInstrumentTypes();

	public PresentmentExcludeCodeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "PresentmentExcludeCode";
		super.pageRightName = "PresentmentExcludeCodeList";
		super.tableName = "Presentment_Exclude_Codes";
		super.queueTableName = "Presentment_Exclude_Codes";
		super.enquiryTableName = "Presentment_Exclude_Codes_temp";
	}

	public void onCreate$windowPresentmentExcludeCode(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		setPageComponents(windowPresentmentExcludeCode, borderLayoutPresentmentExcludeCodeList,
				listBoxPresentmentExcludeCode, pagingPresentmentExcludeCodeList);

		registerButton(buttonSearchDialog);
		registerButton(button_PresentmentExcludeCodeList_NewButton, "button_PresentmentExcludeCodeList_NewButton",
				true);

		fillComboBox(this.code, "", excludeCodeList, "");
		registerField("code", codeHeader, SortOrder.NONE, code, codeSort, Operators.STRING);
		registerField("description", codeDescHeader, SortOrder.NONE, codeDesc, codeDescSort, Operators.STRING);
		fillComboBox(this.instrumentType, "", mandateTypeList, "");
		registerField("instrumentType", instrumentTypeHeader, SortOrder.NONE, instrumentType, instrumentTypeSort,
				Operators.STRING);
		registerField("bounceCode", bounceCodeHeader, SortOrder.NONE, bounceCode, bounceCodeSort, Operators.STRING);

		fillListData();

		doRenderPage();

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$button_PresentmentExcludeCodeList_NewButton(Event event) {
		logger.debug("Entering");

		PresentmentExcludeCode excludeCode = new PresentmentExcludeCode();
		excludeCode.setNewRecord(true);
		excludeCode.setWorkflowId(getWorkFlowId());

		doShowDialogPage(excludeCode);

		logger.debug("Leaving");
	}

	public void fillListData() {
		List<String> roleCodes = getWorkFlowRoles();

		List<PresentmentExcludeCode> excludeCodes = presentmentExcludeCodeService.getPresentmentExcludeCodes(roleCodes);

		listBoxPresentmentExcludeCode.setItemRenderer(new PECListItemListRenderer());

		pagedListWrapper.initList(excludeCodes, listBoxPresentmentExcludeCode, pagingPresentmentExcludeCodeList);
	}

	public void onClick$buttonSearchDialog(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		List<PresentmentExcludeCode> excludeCodes = presentmentExcludeCodeService.getResult(getSearchFilters());

		listBoxPresentmentExcludeCode.setItemRenderer(new PECListItemListRenderer());

		pagedListWrapper.initList(excludeCodes, listBoxPresentmentExcludeCode, pagingPresentmentExcludeCodeList);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnRefresh(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doReset();

		fillListData();

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		Listitem item = this.listBoxPresentmentExcludeCode.getSelectedItem();

		PresentmentExcludeCode excludeCode = presentmentExcludeCodeService
				.getExcludeCode((long) item.getAttribute("Id"));

		if (excludeCode == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		String whereCond = "Where Id = ?";

		if (doCheckAuthority(excludeCode, whereCond, new Object[] { excludeCode.getCode() })) {
			if (isWorkFlowEnabled() && excludeCode.getWorkflowId() == 0) {
				excludeCode.setWorkflowId(getWorkFlowId());
			}

			doShowDialogPage(excludeCode);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void doShowDialogPage(PresentmentExcludeCode excludeCode) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("PresentmentExcludeCode", excludeCode);
		arg.put("PresentmentExcludeCodeList", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Presentment/PresentmentExcludeCodeDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$print(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		List<String> roleCodes = getWorkFlowRoles();

		doPrintResults(presentmentExcludeCodeService.getPrintCodes(roleCodes));

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private ISearch getSearchFilters() {
		ISearch search = new Search();

		String code = this.code.getValue();
		if (!StringUtils.trimToEmpty(this.code.getSelectedItem().getValue().toString())
				.equals(PennantConstants.List_Select)) {
			search.getFilters().add(new Filter("pec.Code", code, this.codeSort.getSelectedIndex()));
		}

		String description = this.codeDesc.getValue();
		if (StringUtils.isNotEmpty(description)) {
			search.getFilters().add(new Filter("pec.Description", description, this.codeDescSort.getSelectedIndex()));
		}

		String instrumentType = this.instrumentType.getValue();
		if (!StringUtils.trimToEmpty(this.instrumentType.getSelectedItem().getValue().toString())
				.equals(PennantConstants.List_Select)) {
			search.getFilters()
					.add(new Filter("pec.instrumentType", instrumentType, this.instrumentTypeSort.getSelectedIndex()));
		}

		String status = this.recordStatus.getValue();
		if (StringUtils.isNotEmpty(status)) {
			search.getFilters()
					.add(new Filter("pec.RecordStatus", status, this.sortOperator_RecordStatus.getSelectedIndex()));
		}

		String recordType = this.recordType.getSelectedItem().getValue();
		if (StringUtils.isNotEmpty(recordType)) {
			search.getFilters()
					.add(new Filter("pec.RecordType", recordType, this.sortOperator_RecordType.getSelectedIndex()));
		}

		return search;
	}

	private class PECListItemListRenderer implements ListitemRenderer<PresentmentExcludeCode>, Serializable {
		private static final long serialVersionUID = -3817100983277888318L;

		public PECListItemListRenderer() {
			super();
		}

		@Override
		public void render(Listitem item, PresentmentExcludeCode excludeCode, int index) throws Exception {
			Listcell lc;

			lc = new Listcell(excludeCode.getCode());
			lc.setParent(item);
			lc = new Listcell(excludeCode.getDescription());
			lc.setParent(item);
			lc = new Listcell(excludeCode.getInstrumentType());
			lc.setParent(item);
			lc = new Listcell(StringUtils.trimToEmpty(excludeCode.getBounceCode()));
			lc.setParent(item);
			lc = new Listcell(excludeCode.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(excludeCode.getRecordType()));
			lc.setParent(item);

			item.setAttribute("Id", excludeCode.getId());
			ComponentsCtrl.applyForward(item, "onDoubleClick=onItemDoubleClicked");
		}
	}

	@Autowired
	public void setPresentmentExcludeCodeService(PresentmentExcludeCodeService presentmentExcludeCodeService) {
		this.presentmentExcludeCodeService = presentmentExcludeCodeService;
	}

}

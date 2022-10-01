package com.pennant.pff.presentment.web;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.pennant.pff.presentment.model.PresentmentExcludeCode;
import com.pennant.pff.presentment.service.PresentmentExcludeCodeService;
import com.pennant.webui.applicationmaster.bouncecode.model.PresentmentExcludeCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.ISearch;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class PresentmentExcludeCodeListCtrl extends GFCBaseListCtrl<PresentmentExcludeCode> {

	private static final long serialVersionUID = -3571720185247491921L;
	private static final Logger logger = LogManager.getLogger(PresentmentExcludeCodeListCtrl.class);

	protected Window window_BounceCodeList;
	protected Borderlayout borderLayout_BounceCodeList;
	protected Paging pagingBounceCodeList;
	protected Listbox listBoxBounceCode;

	protected Textbox bounceCode;
	protected Textbox bounceCodeDesc;
	protected Checkbox createbounceonduedate;

	protected Listbox sortOperator_bounceCodeList;
	protected Listbox sortOperator_bounceCodeListDesc;
	protected Listbox sortOperator_CreateBounceOnDueDate;
	protected Listbox sortOperator_CodeList;

	protected Listheader listheader_Code;
	protected Listheader listheader_BounceCodeDesc;
	protected Listheader listheader_BounceCode;
	protected Listheader listheader_CreateBounceOnDueDate;

	protected Button button_BounceCodeList_BounceCodeListSearchDialog;

	private transient PresentmentExcludeCodeService presentmentExcludeCodeService;

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

	public void onCreate$window_BounceCodeList(Event event) {

		setPageComponents(window_BounceCodeList, borderLayout_BounceCodeList, listBoxBounceCode, pagingBounceCodeList);
		setItemRender(new PresentmentExcludeCodeListModelItemRenderer());

		registerButton(button_BounceCodeList_BounceCodeListSearchDialog);
		registerField("code", listheader_Code, SortOrder.NONE, new Textbox(), new Listbox(), Operators.STRING);

		registerField("bouncecode", listheader_BounceCode, SortOrder.ASC, bounceCode, sortOperator_bounceCodeList,
				Operators.STRING);

		registerField("description", listheader_BounceCodeDesc, SortOrder.NONE, bounceCodeDesc,
				sortOperator_bounceCodeListDesc, Operators.STRING);
		registerField("createbounceonduedate", listheader_CreateBounceOnDueDate, SortOrder.ASC, createbounceonduedate,
				sortOperator_CreateBounceOnDueDate, Operators.BOOLEAN);

		List<PresentmentExcludeCode> bounceCode = presentmentExcludeCodeService.getBounceCodeById(null);
		listBoxBounceCode.setItemRenderer(new PresentmentExcludeCodeListModelItemRenderer());
		getPagedListWrapper().initList(bounceCode, listBoxBounceCode, pagingBounceCodeList);
		doRenderPage();

	}

	public void onClick$button_BounceCodeList_BounceCodeListSearchDialog(Event event) {
		getfilter();
	}

	public void onClick$btnRefresh(Event event) {
		doReset();
		List<PresentmentExcludeCode> bounceCode = presentmentExcludeCodeService.getBounceCodeById(null);
		listBoxBounceCode.setItemRenderer(new PresentmentExcludeCodeListModelItemRenderer());
		getPagedListWrapper().initList(bounceCode, listBoxBounceCode, pagingBounceCodeList);
	}

	public void onBounceCodeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		Listitem selectedItem = this.listBoxBounceCode.getSelectedItem();

		String code = (String) selectedItem.getAttribute("code");
		PresentmentExcludeCode bounceCode = presentmentExcludeCodeService.getCode(code);

		if (bounceCode == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		String whereCond = " where code=?";

		if (doCheckAuthority(bounceCode, whereCond, new Object[] { bounceCode.getCode() })) {
			if (isWorkFlowEnabled() && bounceCode.getWorkflowId() == 0) {
				bounceCode.setWorkflowId(getWorkFlowId());
			}

			doShowDialogPage(bounceCode);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	public void doShowDialogPage(PresentmentExcludeCode aBounceCode) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("bounceCode", aBounceCode);
		arg.put("bounceCodeListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Presentment/PresentmentExcludeCodeDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	public List<PresentmentExcludeCode> getfilter() {
		logger.debug("Entering");
		ISearch search = new Search();
		Filter bounceCode = getFilter("pec.code", this.bounceCode, this.sortOperator_bounceCodeList);
		Filter bounceCodeDesc = getFilter("pec.description", this.bounceCodeDesc, this.sortOperator_bounceCodeListDesc);
		Filter recordStatus = getFilter("pec.recordStatus", this.recordStatus, this.sortOperator_RecordStatus);
		Filter recordType = getFilter("pec.recordType", this.recordType, this.sortOperator_RecordType);

		if (bounceCode != null) {
			search.getFilters().add(bounceCode);
		}
		if (bounceCodeDesc != null) {
			search.getFilters().add(bounceCodeDesc);
		}
		if (recordStatus != null) {
			search.getFilters().add(recordStatus);
		}
		if (recordType != null) {
			search.getFilters().add(recordType);
		}

		List<PresentmentExcludeCode> data = presentmentExcludeCodeService.getResult(search);
		listBoxBounceCode.setItemRenderer(new PresentmentExcludeCodeListModelItemRenderer());
		getPagedListWrapper().initList(data, listBoxBounceCode, pagingBounceCodeList);
		return null;
	}

	private Filter getFilter(String property, Textbox component, Listbox operator) {
		String value = component.getValue();
		if (StringUtils.isNotBlank(value)) {
			return new Filter(property, value, operator.getSelectedIndex());
		}
		return null;
	}

	private Filter getFilter(String property, Listbox component, Listbox operator) {
		String value = component.getSelectedItem().getValue();
		if (StringUtils.isNotBlank(value)) {
			return new Filter(property, value, operator.getSelectedIndex());
		}
		return null;
	}

	public void onClick$print(Event event) {
		List<PresentmentExcludeCode> bounceCode = presentmentExcludeCodeService.getBounceCodeById(null);
		doPrintResults();
	}

	@Autowired
	public void setPresentmentExcludeCodeService(PresentmentExcludeCodeService presentmentExcludeCodeService) {
		this.presentmentExcludeCodeService = presentmentExcludeCodeService;
	}

}

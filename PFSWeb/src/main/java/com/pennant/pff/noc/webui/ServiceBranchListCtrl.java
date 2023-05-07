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

import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.pff.noc.model.ServiceBranch;
import com.pennant.pff.noc.service.ServiceBranchService;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.ISearch;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class ServiceBranchListCtrl extends GFCBaseListCtrl<ServiceBranch> {
	private static final long serialVersionUID = -56814543607201850L;

	protected Window windowServiceBranchList;
	protected Borderlayout blServiceBranchList;
	protected Paging pagingServiceBranchList;
	protected Listbox lbServiceBranch;
	protected Button btnNew;
	protected Button btnSearch;
	protected Listheader codeHeader;
	protected Listheader descriptionHeader;
	protected Listheader pinCodeHeader;
	protected Listheader cityHeader;
	protected Listheader recordStatusHeader;
	protected Listheader recordTypeHeader;
	protected Listbox codeSort;
	protected Listbox descriptionSort;
	protected Listbox citySort;
	protected Listbox pinCodeSort;
	protected Listbox recordTypeSort;
	protected Listbox recordStatusSort;
	protected Textbox code;
	protected Textbox description;
	protected Textbox pinCode;
	protected Textbox city;
	protected Textbox recordStatus;
	protected Textbox recordType;

	private transient ServiceBranchService serviceBranchService;

	public ServiceBranchListCtrl() {
		super();
	}

	protected void doSetProperties() {
		super.moduleCode = "ServiceBranch";
		super.pageRightName = "ServiceBranchList";
		super.tableName = "Service_Branches";
		super.queueTableName = "Service_Branches";
	}

	public void onCreate$windowServiceBranchList(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		setPageComponents(windowServiceBranchList, blServiceBranchList, lbServiceBranch, pagingServiceBranchList);

		setItemRender(new CustomerServiceBranchModelItemRenderer());

		registerButton(btnNew, "button_CustomerServiceBranch_btnNew", true);
		registerButton(btnSearch);
		registerField("code", codeHeader, SortOrder.NONE, code, codeSort, Operators.STRING);
		registerField("description", descriptionHeader, SortOrder.NONE, description, descriptionSort, Operators.STRING);
		registerField("pinCode", pinCodeHeader, SortOrder.NONE, pinCode, pinCodeSort, Operators.STRING);
		registerField("city", cityHeader, SortOrder.NONE, city, citySort, Operators.STRING);
		registerField("recordStatus", recordStatusHeader);
		registerField("recordType", recordTypeHeader);

		fillListData();

		doRenderPage();

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnSearch(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		List<ServiceBranch> excludeCodes = this.serviceBranchService.getResult(getSearchFilters());

		this.lbServiceBranch.setItemRenderer(new CustomerServiceBranchModelItemRenderer());

		this.pagedListWrapper.initList(excludeCodes, lbServiceBranch, pagingServiceBranchList);

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

		ServiceBranch csb = new ServiceBranch();
		csb.setNewRecord(true);
		csb.setWorkflowId(getWorkFlowId());

		doShowDialogPage(csb);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$print(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doPrintResults(this.serviceBranchService.getPrintServices(getWorkFlowRoles()));

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onServiceBranchItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		long id = (Long) this.lbServiceBranch.getSelectedItem().getAttribute("Id");

		ServiceBranch serviceBranch = this.serviceBranchService.getServiceBranch(id);

		if (serviceBranch == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		String whereCond = "Where Id = ?";

		if (doCheckAuthority(serviceBranch, whereCond, new Object[] { serviceBranch.getCode() })) {
			if (isWorkFlowEnabled() && serviceBranch.getWorkflowId() == 0) {
				serviceBranch.setWorkflowId(getWorkFlowId());
			}

			doShowDialogPage(serviceBranch);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void doShowDialogPage(ServiceBranch csb) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("serviceBranch", csb);
		arg.put("serviceBranchListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/NOC/ServiceBranchDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void fillListData() {
		List<ServiceBranch> excludeCodes = this.serviceBranchService.getServiceBranches(getWorkFlowRoles());

		this.lbServiceBranch.setItemRenderer(new CustomerServiceBranchModelItemRenderer());

		this.pagedListWrapper.initList(excludeCodes, lbServiceBranch, pagingServiceBranchList);
	}

	private ISearch getSearchFilters() {
		ISearch search = new Search();

		String code = this.code.getValue();
		if (StringUtils.isNotEmpty(code)) {
			search.getFilters().add(new Filter("csb.Code", code, this.codeSort.getSelectedIndex()));
		}

		String description = this.description.getValue();
		if (StringUtils.isNotEmpty(description)) {
			search.getFilters()
					.add(new Filter("csb.Description", description, this.descriptionSort.getSelectedIndex()));
		}

		String city = this.city.getValue();
		if (StringUtils.isNotEmpty(city)) {
			search.getFilters().add(new Filter("csb.City", city, this.citySort.getSelectedIndex()));
		}

		String pinCode = this.pinCode.getValue();
		if (StringUtils.isNotEmpty(pinCode)) {
			search.getFilters().add(new Filter("csb.PinCode", pinCode, this.pinCodeSort.getSelectedIndex()));
		}

		String status = this.recordStatus.getValue();
		if (StringUtils.isNotEmpty(status)) {
			search.getFilters().add(new Filter("csb.RecordStatus", status, this.recordStatusSort.getSelectedIndex()));
		}

		String recordType = this.recordType.getValue();
		if (StringUtils.isNotEmpty(recordType)) {
			search.getFilters().add(new Filter("csb.RecordType", recordType, this.recordTypeSort.getSelectedIndex()));
		}

		return search;
	}

	private class CustomerServiceBranchModelItemRenderer implements ListitemRenderer<ServiceBranch>, Serializable {
		private static final long serialVersionUID = 6056180845898696437L;

		public CustomerServiceBranchModelItemRenderer() {
			super();
		}

		@Override
		public void render(Listitem item, ServiceBranch csb, int arg2) throws Exception {
			Listcell lc;
			lc = new Listcell(csb.getCode());
			lc.setParent(item);
			lc = new Listcell(csb.getDescription());
			lc.setParent(item);
			lc = new Listcell(String.valueOf(csb.getPinCodeId()));
			lc.setParent(item);
			lc = new Listcell(csb.getCity());
			lc.setParent(item);
			lc = new Listcell(csb.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(csb.getRecordType()));
			lc.setParent(item);

			item.setAttribute("Id", csb.getId());

			ComponentsCtrl.applyForward(item, "onDoubleClick=onServiceBranchItemDoubleClicked");
		}
	}

	@Autowired
	public void setServiceBranchService(ServiceBranchService serviceBranchService) {
		this.serviceBranchService = serviceBranchService;
	}
}

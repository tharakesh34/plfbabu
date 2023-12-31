/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : FinanceTypeListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 30-06-2011 * * Modified
 * Date : 30-06-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 30-06-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.pff.noc.webui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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

import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.pff.noc.model.LoanTypeLetterMapping;
import com.pennant.pff.noc.service.LoanTypeLetterMappingService;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.ISearch;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class LoanTypeLetterMappingListCtrl extends GFCBaseListCtrl<LoanTypeLetterMapping> {
	private static final long serialVersionUID = -1491703348215991538L;

	protected Window windowLoanTypeLetterMappingList;
	protected Borderlayout borderLayoutLoanTypeLetterMappingList;
	protected Listbox listBoxLoanTypeLetterMapping;
	protected Paging pagingLoanTypeLetterMapping;
	protected Button buttonNewLoanTypeLetterMapping;
	protected Button buttonLoanTypeLetterMappingSearchDialog;
	protected Uppercasebox finType;
	protected Listheader listheaderFinType;
	protected Listheader listheaderRecordStatus;
	protected Listheader listheaderRecordType;
	protected Listbox sortOperatorFinType;

	private transient LoanTypeLetterMappingService loanTypeLetterMappingService;

	public LoanTypeLetterMappingListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "LoanTypeLetterMapping";
		super.pageRightName = "LoanTypeLetterMappingList";
		super.tableName = "Loantype_Letter_Mapping";
		super.queueTableName = "Loantype_Letter_Mapping";
		super.enquiryTableName = "Loantype_Letter_Mapping_Temp";
	}

	public void onCreate$windowLoanTypeLetterMappingList(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(windowLoanTypeLetterMappingList, borderLayoutLoanTypeLetterMappingList,
				listBoxLoanTypeLetterMapping, pagingLoanTypeLetterMapping);

		registerButton(buttonNewLoanTypeLetterMapping, "buttonFinanceTypeLetterMappingListNewFinanceType", true);
		registerButton(buttonLoanTypeLetterMappingSearchDialog);

		registerField("finType", listheaderFinType, SortOrder.NONE, finType, sortOperatorFinType, Operators.STRING);

		fillListData();
		doRenderPage();

		logger.debug(Literal.LEAVING);
	}

	public void fillListData() {
		logger.debug(Literal.ENTERING);

		List<String> roleCodes = getWorkFlowRoles();

		List<LoanTypeLetterMapping> letterMapping = loanTypeLetterMappingService.getLoanTypeLetterMapping(roleCodes);

		List<LoanTypeLetterMapping> templetterMapping = new ArrayList<>();
		List<String> loanType = new ArrayList<>();

		for (LoanTypeLetterMapping lm : letterMapping) {
			if (loanType.contains(lm.getFinType())) {
				templetterMapping.add(lm);
			}

			loanType.add(lm.getFinType());
		}

		letterMapping.removeAll(templetterMapping);

		listBoxLoanTypeLetterMapping.setItemRenderer(new LTLPListModelItemRender());

		pagedListWrapper.initList(letterMapping, listBoxLoanTypeLetterMapping, pagingLoanTypeLetterMapping);

		logger.debug(Literal.LEAVING);
	}

	public class LTLPListModelItemRender implements ListitemRenderer<LoanTypeLetterMapping> {
		public LTLPListModelItemRender() {
			super();
		}

		@Override
		public void render(Listitem item, LoanTypeLetterMapping letterMapping, int index) {
			Listcell lc = new Listcell(letterMapping.getFinType());
			lc.setParent(item);

			lc = new Listcell(letterMapping.getRecordStatus());
			lc.setParent(item);

			lc = new Listcell(PennantJavaUtil.getLabel(letterMapping.getRecordType()));
			lc.setParent(item);

			item.setAttribute("data", letterMapping);

			ComponentsCtrl.applyForward(item, "onDoubleClick=onLoanTypeLetterMappingItemDoubleClicked");
		}
	}

	public void onClick$buttonLoanTypeLetterMappingSearchDialog(Event event) {
		logger.debug(Literal.ENTERING);

		List<LoanTypeLetterMapping> excludeCodes = this.loanTypeLetterMappingService.getResult(getSearchFilters());

		List<LoanTypeLetterMapping> templetterMapping = new ArrayList<>();
		List<String> loanType = new ArrayList<>();

		for (LoanTypeLetterMapping lm : excludeCodes) {
			if (loanType.contains(lm.getFinType())) {
				templetterMapping.add(lm);
			}

			loanType.add(lm.getFinType());
		}

		excludeCodes.removeAll(templetterMapping);

		this.listBoxLoanTypeLetterMapping.setItemRenderer(new LTLPListModelItemRender());

		this.pagedListWrapper.initList(excludeCodes, listBoxLoanTypeLetterMapping, pagingLoanTypeLetterMapping);

		logger.debug(Literal.LEAVING);
	}

	private ISearch getSearchFilters() {
		ISearch search = new Search();

		String selectedFinType = this.finType.getValue();
		if (StringUtils.isNotEmpty(selectedFinType)) {
			search.getFilters()
					.add(new Filter("ltlm.finType", selectedFinType, this.sortOperatorFinType.getSelectedIndex()));
		}

		String status = this.recordStatus.getValue();
		if (StringUtils.isNotEmpty(status)) {
			search.getFilters()
					.add(new Filter("ltlm.RecordStatus", status, this.sortOperator_RecordStatus.getSelectedIndex()));
		}

		String recordType = this.recordType.getSelectedItem().getValue();
		if (StringUtils.isNotEmpty(recordType)) {
			search.getFilters()
					.add(new Filter("ltlm.RecordType", recordType, this.sortOperator_RecordType.getSelectedIndex()));
		}

		return search;
	}

	public void onClick$btnRefresh(Event event) {
		doReset();
		fillListData();
	}

	public void onClick$buttonNewLoanTypeLetterMapping(Event event) {
		logger.debug(Literal.ENTERING);

		LoanTypeLetterMapping loanTypeLetterMapping = new LoanTypeLetterMapping();
		loanTypeLetterMapping.setNewRecord(true);
		loanTypeLetterMapping.setWorkflowId(getWorkFlowId());
		Map<String, Object> map = getDefaultArguments();
		map.put("loanTypeLetterMapping", loanTypeLetterMapping);
		map.put("loanTypeLetterMappingListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/NOC/SelectLoanTypeLetterMappingDialog.zul", null, map);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onLoanTypeLetterMappingItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		Listitem selectedItem = this.listBoxLoanTypeLetterMapping.getSelectedItem();
		final LoanTypeLetterMapping data = (LoanTypeLetterMapping) selectedItem.getAttribute("data");
		List<LoanTypeLetterMapping> letterMapping = loanTypeLetterMappingService
				.getLoanTypeLetterMappingById(data.getFinType());

		if (letterMapping.size() == 0) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		LoanTypeLetterMapping loanTypeLetterMapping = new LoanTypeLetterMapping();
		BeanUtils.copyProperties(letterMapping.get(0), loanTypeLetterMapping);
		loanTypeLetterMapping.setLoanTypeLetterMappingList(letterMapping);

		if (isWorkFlowEnabled() && loanTypeLetterMapping.getWorkflowId() == 0) {
			loanTypeLetterMapping.setWorkflowId(getWorkFlowId());
		}

		Map<String, Object> arg = getDefaultArguments();
		arg.put("loanTypeLetterMapping", loanTypeLetterMapping);
		arg.put("loanTypeLetterMappingListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/NOC/LoanTypeLetterMappingDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$print(Event event) {
		doPrintResults();
	}

	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	@Autowired
	public void setLoanTypeLetterMappingService(LoanTypeLetterMappingService loanTypeLetterMappingService) {
		this.loanTypeLetterMappingService = loanTypeLetterMappingService;
	}
}
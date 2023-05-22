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
 * * FileName : DisbursementRegistrationListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 19-04-2017
 * * * Modified Date : 19-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 19-04-2017 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennanttech.interfacebajaj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Center;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Cluster;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.service.applicationmaster.BranchService;
import com.pennant.backend.service.applicationmaster.ClusterService;
import com.pennant.backend.service.rmtmasters.FinTypePartnerBankService;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.extension.PartnerBankExtension;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.external.disbursement.DisbursementRequestService;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/Mandate/MandateRegistration.zul file.<br>
 * ************************************************************<br>
 * 
 */
public class DisbursementRegistrationListCtrl extends GFCBaseListCtrl<FinAdvancePayments> {
	private static final long serialVersionUID = 1L;

	protected Window window_DisbursementRegistrationList;
	protected Borderlayout borderLayout_DisbursementList;
	protected Paging pagingDisbursementList;
	protected Listbox listBoxDisbursementRegistration;

	protected Listheader listheader_Disbursement_DisbTypes;
	protected Listheader listheader_Disbursement_FinRef;
	protected Listheader listheader_Disbursement_FinType;
	protected Listheader listheader_Disbursement_Custname;
	protected Listheader listheader_Disbursement_BenName;
	protected Listheader listheader_Disbursement_BenAcctno;
	protected Listheader listheader_Disbursement_Branch;
	protected Listheader listheader_Disbursement_Channel;
	protected Listheader listheader_Disbursement_Party;
	protected Listheader listheader_Disbursement_Entity;

	protected Combobox disbTypes;
	protected Space space_disbTypes;
	protected ExtendedCombobox partnerBank;
	protected Datebox fromDate;
	protected Datebox toDate;
	protected ExtendedCombobox finType;
	protected ExtendedCombobox branch;
	protected Checkbox qdp;
	protected Combobox channelTypes;
	protected Textbox finRef;
	protected ExtendedCombobox entity;
	protected Combobox disbParty;
	protected ExtendedCombobox vasManufacturer;
	protected Cell cellVMFC1;
	protected Cell cellVMFC2;
	protected Cell cellVMFC3;
	protected ExtendedCombobox branchOrCluster;

	protected Listbox sortOperator_DisbType;
	protected Listbox sortOperator_PartnerBank;
	protected Listbox sortOperator_FromDate;
	protected Listbox sortOperator_ToDate;
	protected Listbox sortOperator_FinType;
	protected Listbox sortOperator_Branch;
	protected Listbox sortOperator_Channel;
	protected Listbox sortOperator_FinRef;
	protected Listbox sortOperator_Entity;
	protected Listbox sortOperator_DisbParty;
	protected Listbox sortOperator_VasManufacturer;
	protected Listbox sortOperator_BranchOrCluster;

	protected Listheader listHeader_CheckBox_Name;
	protected Listcell listCell_Checkbox;
	protected Listitem listItem_Checkbox;
	protected Checkbox listHeader_CheckBox_Comp;
	protected Checkbox list_CheckBox;

	protected Button button_Search;
	protected Button btnDownload;
	protected int oldVar_sortOperator_finType;

	private Map<Long, FinAdvancePayments> disbursementMap = new HashMap<Long, FinAdvancePayments>();
	private List<ValueLabel> channelTypesList = PennantStaticListUtil.getChannelTypes();
	private List<ValueLabel> paymentDetails = PennantStaticListUtil.getPaymentDetails();
	private List<FinAdvancePayments> finAdvancePaymentsList = new ArrayList<>();

	private int futureDays = 0;

	private transient DisbursementRequestService disbursementRequestService;
	private transient FinTypePartnerBankService finTypePartnerBankService;
	private transient ClusterService clusterService;
	private transient BranchService branchService;

	/**
	 * default constructor.<br>
	 */
	public DisbursementRegistrationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "DisbursementRegistration";
		super.pageRightName = "DisbursementRegistration";
		super.tableName = "INT_DISBURSEMENT_REQUEST_VIEW";
		super.queueTableName = "INT_DISBURSEMENT_REQUEST_VIEW";
		futureDays = Integer.valueOf((SysParamUtil.getValue("NO_FUTURE_DAYS_DISB_DOWNLOAD").toString()));

		// Future day's including current date
		futureDays = futureDays - 1;
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		setFilters(this.searchObject);
	}

	private void setFilters(JdbcSearchObject<FinAdvancePayments> searchObject) {
		if (toDate.getValue() == null) {
			Filter[] filter = new Filter[1];
			java.util.Date date = SysParamUtil.getAppDate();
			date = DateUtil.addDays(date, futureDays);
			filter[0] = new Filter("LLDATE", date, Filter.OP_LESS_OR_EQUAL);
			searchObject.addFilters(filter);
		}

		if (fromDate.getValue() != null) {
			Filter[] filters = new Filter[1];
			filters[0] = new Filter("LLDATE", this.fromDate.getValue(), Filter.OP_GREATER_OR_EQUAL);
			searchObject.addFilters(filters);
		}
		if (toDate.getValue() != null) {
			Filter[] filters = new Filter[1];
			filters[0] = new Filter("LLDATE", this.toDate.getValue(), Filter.OP_LESS_OR_EQUAL);
			searchObject.addFilters(filters);
		}

		/*
		 * if (qdp != null) { Filter[] filters = new Filter[1]; filters[0] = new Filter("QUICKDISB",
		 * this.qdp.isChecked(), Filter.OP_EQUAL); searchObject.addFilters(filters); }
		 */

		// Adding filter to download only if the Download Type is Offline.
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("DOWNLOADTYPE", PennantConstants.OFFLINE, Filter.OP_EQUAL);
		searchObject.addFilters(filters);

	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_DisbursementRegistrationList(Event event) {
		// Set the page level components.
		setPageComponents(window_DisbursementRegistrationList, borderLayout_DisbursementList,
				listBoxDisbursementRegistration, pagingDisbursementList);

		// Register buttons and fields.
		registerButton(button_Search);
		registerField("paymentId");
		registerField("partnerbank_id");
		registerField("branchDesc");
		registerField("alwFileDownload");

		registerField("partnerbank_code", partnerBank, SortOrder.NONE, sortOperator_PartnerBank, Operators.STRING);
		registerField("paymentType", listheader_Disbursement_DisbTypes, SortOrder.NONE, disbTypes,
				sortOperator_DisbType, Operators.STRING);
		registerField("FinID");
		registerField("finReference", listheader_Disbursement_FinRef, SortOrder.NONE, finRef, sortOperator_FinRef,
				Operators.STRING);
		registerField("finType", listheader_Disbursement_FinType, SortOrder.NONE, finType, sortOperator_FinType,
				Operators.MULTISELECT);
		registerField("custShrtName", listheader_Disbursement_Custname, SortOrder.NONE);
		registerField("beneficiaryName", listheader_Disbursement_BenName, SortOrder.NONE);
		registerField("beneficiaryAccNo", listheader_Disbursement_BenAcctno, SortOrder.NONE);
		registerField("branchCode", listheader_Disbursement_Branch, SortOrder.NONE, branch, sortOperator_Branch,
				Operators.STRING);
		registerField("AMTTOBERELEASED");
		registerField("channel", listheader_Disbursement_Channel, SortOrder.NONE, channelTypes, sortOperator_Channel,
				Operators.STRING);
		registerField("entityCode", listheader_Disbursement_Entity, SortOrder.NONE, entity, sortOperator_Entity,
				Operators.STRING);
		registerField("PaymentDetail", listheader_Disbursement_Party, SortOrder.NONE, disbParty, sortOperator_DisbParty,
				Operators.STRING);
		registerField("providerId", vasManufacturer, SortOrder.NONE, sortOperator_VasManufacturer, Operators.STRING);
		doRenderPage();
		this.disbursementMap.clear();
		doSetFieldProperties();

		if (listBoxDisbursementRegistration.getItems().size() > 0) {
			listHeader_CheckBox_Comp.setDisabled(false);
		} else {
			listHeader_CheckBox_Comp.setDisabled(true);
		}
	}

	private void doSetFieldProperties() {
		listItem_Checkbox = new Listitem();
		listCell_Checkbox = new Listcell();
		listHeader_CheckBox_Comp = new Checkbox();
		listCell_Checkbox.appendChild(listHeader_CheckBox_Comp);
		listHeader_CheckBox_Comp.addForward("onClick", self, "onClick_listHeaderCheckBox");
		listItem_Checkbox.appendChild(listCell_Checkbox);

		if (listHeader_CheckBox_Name.getChildren() != null) {
			listHeader_CheckBox_Name.getChildren().clear();
		}
		listHeader_CheckBox_Name.appendChild(listHeader_CheckBox_Comp);

		this.finType.setSclass(PennantConstants.mandateSclass);

		fillComboBox(this.disbTypes, "", PennantStaticListUtil.getDisbRegistrationTypes(), "");
		if (ImplementationConstants.DISB_REQ_RES_FILE_GEN_MODE) {
			this.space_disbTypes.setSclass(PennantConstants.mandateSclass);
		}
		fillComboBox(this.channelTypes, "", channelTypesList, "");
		fillComboBox(this.disbParty, "", paymentDetails, "");

		if (PartnerBankExtension.BRANCH_WISE_MAPPING) {
			doSetBranchWiseProperties();
		} else {
			this.partnerBank.setModuleName("PartnerBank");
			this.partnerBank.setDisplayStyle(2);
			this.partnerBank.setValueColumn("PartnerBankCode");
			this.partnerBank.setDescColumn("PartnerBankName");
			this.partnerBank.setValidateColumns(new String[] { "PartnerBankCode" });
			this.partnerBank.setMandatoryStyle(false);
		}

		this.branch.setModuleName("BankBranch");
		this.branch.setDisplayStyle(2);
		this.branch.setValueColumn("BranchCode");
		this.branch.setDescColumn("BranchDesc");
		this.branch.setValidateColumns(new String[] { "BranchCode" });

		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("FinType");
		this.finType.setDescColumn("FinTypeDesc");
		this.finType.setValidateColumns(new String[] { "FinType" });
		this.finType.setMultySelection(true);
		this.finType.setInputAllowed(false);
		this.finType.setWidth("150px");

		this.entity.setModuleName("Entity");
		this.entity.setMandatoryStyle(true);
		this.entity.setDisplayStyle(2);
		this.entity.setValueColumn("EntityCode");
		this.entity.setDescColumn("EntityDesc");
		this.entity.setValidateColumns(new String[] { "EntityCode" });

		this.partnerBank.setButtonDisabled(true);

		this.vasManufacturer.setMandatoryStyle(false);
		this.vasManufacturer.setModuleName("VehicleDealer");
		this.vasManufacturer.setValueColumn("DealerId");
		this.vasManufacturer.setDescColumn("DealerName");
		this.vasManufacturer.setValueType(DataType.LONG);
		this.vasManufacturer.setValidateColumns(new String[] { "DealerId" });

		this.branchOrCluster.setButtonDisabled(true);

		if (ImplementationConstants.DISBURSEMENT_AUTO_DOWNLOAD
				&& SysParamUtil.isAllowed(SMTParameterConstants.DISBURSEMENT_AUTO_DOWNLOAD_JOB_ENABLED)) {
			this.btnDownload.setDisabled(true);
		}
	}

	private void doSetBranchWiseProperties() {
		this.partnerBank.setModuleName("FinTypePartner");
		this.partnerBank.setMandatoryStyle(true);
		this.partnerBank.setValueColumn("PartnerBankCode");
		this.partnerBank.setDescColumn("PartnerBankName");
		this.partnerBank.setMaxlength(14);
		this.partnerBank.setValidateColumns(new String[] { "PartnerBankCode" });

		this.branchOrCluster.setMandatoryStyle(true);
		this.branchOrCluster.setButtonDisabled(false);

		String moduleName = "FinTypePartnerBankBranch";
		String valueColumn = "BranchCode";
		String descColumn = "BranchDesc";

		if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("C")) {
			moduleName = "Cluster";
			valueColumn = "Code";
			descColumn = "Name";

			this.branchOrCluster.setFilters(
					new Filter[] { new Filter("CLUSTERTYPE", PartnerBankExtension.CLUSTER_TYPE, Filter.OP_EQUAL) });
		}

		this.branchOrCluster.setModuleName(moduleName);
		this.branchOrCluster.setValueColumn(valueColumn);
		this.branchOrCluster.setDescColumn(descColumn);
		this.branchOrCluster.setValidateColumns(new String[] { valueColumn, descColumn });
	}

	/**
	 * Filling the MandateIdMap details and based on checked and unchecked events of listCellCheckBox.
	 */
	public void onClick_listHeaderCheckBox(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		for (int i = 0; i < listBoxDisbursementRegistration.getItems().size(); i++) {
			Listitem listitem = listBoxDisbursementRegistration.getItems().get(i);
			Checkbox cb = (Checkbox) listitem.getChildren().get(0).getChildren().get(0);
			cb.setChecked(listHeader_CheckBox_Comp.isChecked());
		}

		if (listHeader_CheckBox_Comp.isChecked() && listBoxDisbursementRegistration.getItems().size() > 0) {
			disbursementMap = getDisbursementDetails();
		} else {
			disbursementMap.clear();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Filling the MandateIdMap details based on checked and unchecked events of listCellCheckBox.
	 */
	public void onClick_listCellCheckBox(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Checkbox checkBox = (Checkbox) event.getOrigin().getTarget();

		FinAdvancePayments advancePayments = (FinAdvancePayments) checkBox.getAttribute("finAdvancePayments");

		if (checkBox.isChecked()) {
			disbursementMap.put(advancePayments.getPaymentId(), advancePayments);
		} else {
			disbursementMap.remove(advancePayments.getPaymentId());
		}

		if (disbursementMap.size() == this.pagingDisbursementList.getTotalSize()) {
			listHeader_CheckBox_Comp.setChecked(true);
		} else {
			listHeader_CheckBox_Comp.setChecked(false);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Item renderer for listItems in the listBox.
	 */
	private class DisbursementListModelItemRenderer implements ListitemRenderer<FinAdvancePayments>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, FinAdvancePayments payments, int count) {

			Listcell lc;

			lc = new Listcell();
			list_CheckBox = new Checkbox();
			list_CheckBox.setAttribute("finAdvancePayments", payments);
			list_CheckBox.addForward("onClick", self, "onClick_listCellCheckBox");
			lc.appendChild(list_CheckBox);
			if (listHeader_CheckBox_Comp.isChecked()) {
				list_CheckBox.setChecked(true);
			} else {
				list_CheckBox.setChecked(disbursementMap.containsKey(payments.getPaymentId()));
			}
			lc.setParent(item);

			lc = new Listcell(payments.getEntityCode());
			lc.setParent(item);

			lc = new Listcell(payments.getPaymentType());
			lc.setParent(item);

			lc = new Listcell(payments.getFinReference());
			lc.setParent(item);

			lc = new Listcell(payments.getFinType());
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(payments.getAmtToBeReleased(),
					CurrencyUtil.getFormat(payments.getDisbCCy())));
			lc.setParent(item);

			lc = new Listcell(PennantStaticListUtil.getlabelDesc(payments.getPaymentDetail(), paymentDetails));
			lc.setParent(item);

			lc = new Listcell(payments.getCustShrtName());
			lc.setParent(item);

			lc = new Listcell(payments.getBeneficiaryName());
			lc.setParent(item);

			lc = new Listcell(payments.getBeneficiaryAccNo());
			lc.setParent(item);

			lc = new Listcell(payments.getBranchDesc());
			lc.setParent(item);

			lc = new Listcell(PennantStaticListUtil.getlabelDesc(payments.getChannel(), channelTypesList));
			lc.setParent(item);

			item.setAttribute("finAdvancePayments", payments);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onDisbursementDoubleClicked");
		}
	}

	/**
	 * Getting the Disbursement List using JdbcSearchObject with search criteria..
	 */
	private Map<Long, FinAdvancePayments> getDisbursementDetails() {

		List<FinAdvancePayments> list = renderDisbursements();

		Map<Long, FinAdvancePayments> disbMap = new HashMap<Long, FinAdvancePayments>();

		if (list == null || list.isEmpty()) {
			return disbMap;
		}

		for (FinAdvancePayments disbursement : list) {
			disbMap.put(disbursement.getPaymentId(), disbursement);
		}

		return disbMap;
	}

	// Fetching the list from The database and filtered in any pending otc's If
	// there any record is from Disbursement we are not adding in to resultant
	// list.
	private List<FinAdvancePayments> renderDisbursements() {

		JdbcSearchObject<FinAdvancePayments> searchObject = new JdbcSearchObject<FinAdvancePayments>(
				FinAdvancePayments.class);

		searchObject.addField("paymentid");
		searchObject.addField("partnerbank_id");
		searchObject.addField("finType");
		searchObject.addField("branchCode");
		searchObject.addField("branchDesc");
		searchObject.addField("partnerbank_code");
		searchObject.addField("alwFileDownload");
		searchObject.addField("finReference");
		searchObject.addField("entityCode");
		searchObject.addField("custShrtName");
		searchObject.addField("beneficiaryName");
		searchObject.addField("beneficiaryAccNo");
		searchObject.addField("amtToBeReleased");
		searchObject.addField("paymentType");
		searchObject.addField("channel");
		searchObject.addField("providerId");
		searchObject.addField("PaymentDetail");
		searchObject.addField("partnerBankAcType");
		searchObject.addField("llDate");
		searchObject.addField("disbCCy");
		searchObject.addTabelName(this.tableName);

		// Internal Settlements Payment Type details not allowed to Download
		searchObject.addFilterNotEqual("paymentType", DisbursementConstants.PAYMENT_TYPE_IST);

		setFilters(searchObject);

		for (SearchFilterControl searchControl : searchControls) {
			Filter filters = searchControl.getFilter();
			if (filters != null) {
				searchObject.addFilter(filters);
			}
		}

		if (PartnerBankExtension.BRANCH_WISE_MAPPING) {
			if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("C")) {
				if (this.branchOrCluster.getValue() != null) {
					String whereCondition = " Finreference In (Select Finreference from Financemain_view where FinBranch in (Select BranchCode from RMTBranches where ClusterId in("
							+ Long.valueOf(this.branchOrCluster.getId()) + ")))";
					searchObject.addWhereClause(whereCondition);
				}
			} else {
				if (this.branchOrCluster.getValue() != null) {
					String whereCondition = " Finreference In (Select Finreference from Financemain_view where FinBranch in (Select BranchCode From FinTypePartnerBanks where BranchCode in ("
							+ this.branchOrCluster.getValue() + ")))";
					searchObject.addWhereClause(whereCondition);
				}
			}
		}

		List<FinAdvancePayments> searchList = getPagedListWrapper().getPagedListService()
				.getBySearchObject(searchObject);
		List<FinAdvancePayments> resultantList = disbursementRequestService.filterDisbInstructions(searchList);

		this.listbox.setItemRenderer(new DisbursementListModelItemRenderer());
		getPagedListWrapper().setPagedListService(pagedListService);
		getPagedListWrapper().initList(resultantList, this.listBoxDisbursementRegistration, this.paging);

		setFinAdvancePaymentsList(resultantList);
		return resultantList;
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_Search(Event event) {
		this.disbursementMap.clear();
		this.listHeader_CheckBox_Comp.setChecked(false);

		doClear();
		doSetValidations();

		renderDisbursements();

		if (listBoxDisbursementRegistration.getItems().size() > 0) {
			listHeader_CheckBox_Comp.setDisabled(false);
		} else {
			listBoxDisbursementRegistration.setEmptyMessage(Labels.getLabel("listEmptyMessage.title"));
			listHeader_CheckBox_Comp.setDisabled(true);
		}
	}

	private void doSetValidations() {
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (StringUtils.isNotEmpty(this.entity.getValidatedValue())) {
				if (!this.partnerBank.isReadonly()) {
					this.partnerBank.setConstraint(
							new PTStringValidator(Labels.getLabel("label_DisbursementList_PartnerBank.value"),
									PennantRegularExpressions.REGEX_DESCRIPTION, true));
					this.partnerBank.getValue();
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.finType.isReadonly())
				this.finType
						.setConstraint(new PTStringValidator(Labels.getLabel("label_DisbursementList_LoanType.value"),
								PennantRegularExpressions.REGEX_DESCRIPTION, true));
			this.finType.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.entity.isReadonly()) {
				this.entity.setConstraint(new PTStringValidator(Labels.getLabel("label_DisbursementList_Entity.value"),
						PennantRegularExpressions.REGEX_ALPHANUM, true));
				this.entity.getValue();
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			Date date = DateUtil.addDays(SysParamUtil.getAppDate(), futureDays);
			if (DateUtil.compare(this.toDate.getValue(), date) > 0) {
				throw new WrongValueException(this.toDate, Labels.getLabel("DATE_ALLOWED_MAXDATE_EQUAL",
						new String[] { "To Date", DateUtil.formatToShortDate(date) }));
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (getComboboxValue(this.disbTypes).equals(PennantConstants.List_Select)
					&& ImplementationConstants.DISB_REQ_RES_FILE_GEN_MODE) {
				throw new WrongValueException(this.disbTypes, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_DisbursementList_disbInstrctuionType.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.branchOrCluster.isReadonly())
				this.branchOrCluster.setConstraint(new PTStringValidator(
						Labels.getLabel("label_LoanTypePartnerbankMappingDialogue_BranchOrCluster.value"),
						PennantRegularExpressions.REGEX_DESCRIPTION, true));
			this.branchOrCluster.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		if (this.fromDate.getValue() != null && this.toDate.getValue() != null) {
			if (this.fromDate.getValue().compareTo(this.toDate.getValue()) == 1) {
				throw new WrongValueException(this.toDate, "To date should be greater than or equal to From date.");
			}
		}
	}

	private void doRemoveValidation() {
		logger.debug("Entering ");
		this.finType.setErrorMessage("");
		Clients.clearWrongValue(this.partnerBank);
		Clients.clearWrongValue(this.finType);
		Clients.clearWrongValue(this.toDate);
		Clients.clearWrongValue(this.entity);
		this.partnerBank.setConstraint("");
		this.finType.setConstraint("");
		this.toDate.setConstraint("");
		this.entity.setConstraint("");
		logger.debug("Leaving ");
	}

	private void doClear() {
		logger.debug("Entering ");
		this.finType.setErrorMessage("");
		this.partnerBank.setErrorMessage("");
		this.finType.setErrorMessage("");
		this.toDate.setErrorMessage("");
		this.entity.setErrorMessage("");

		logger.debug("Leaving ");
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		searchObject.clearFilters();
		this.fromDate.setValue(null);
		this.toDate.setValue(null);
		this.partnerBank.setButtonDisabled(true);
		this.partnerBank.setMandatoryStyle(false);
		this.disbursementMap.clear();
		this.listHeader_CheckBox_Comp.setChecked(false);
		this.listheader_Disbursement_DisbTypes.setSort("none");
		this.listheader_Disbursement_FinRef.setSort("none");
		this.listheader_Disbursement_FinType.setSort("none");
		this.listheader_Disbursement_Custname.setSort("none");
		this.listheader_Disbursement_BenName.setSort("none");
		this.listheader_Disbursement_BenAcctno.setSort("none");
		this.listheader_Disbursement_Branch.setSort("none");
		this.listheader_Disbursement_Entity.setSort("none");
		this.listBoxDisbursementRegistration.getItems().clear();

		if (listBoxDisbursementRegistration.getItems().size() > 0) {
			listHeader_CheckBox_Comp.setDisabled(false);
		} else {
			listHeader_CheckBox_Comp.setDisabled(true);
		}
		this.pagingDisbursementList.setTotalSize(0);

	}

	/**
	 * when the "Upload" button is clicked. <br>
	 * 
	 * @param event
	 */

	public void onClick$btnDownload(Event event) {
		logger.debug("Entering" + event.toString());

		List<FinAdvancePayments> disbushmentList;

		if (listHeader_CheckBox_Comp.isChecked()) {
			disbursementMap.clear();
			disbursementMap = getDisbursementDetails();
			disbushmentList = new ArrayList<FinAdvancePayments>(disbursementMap.values());
		} else {
			disbushmentList = new ArrayList<FinAdvancePayments>(disbursementMap.values());
		}

		if (listBoxDisbursementRegistration.getItems().size() > 0) {
			listHeader_CheckBox_Comp.setDisabled(false);
		} else {
			listHeader_CheckBox_Comp.setDisabled(true);
		}

		if (disbushmentList.isEmpty()) {
			MessageUtil.showError(Labels.getLabel("MandateDataList_NoEmpty"));
			return;
		}
		String msg = "You have selected " + this.disbursementMap.size() + " Disbursement(s) out of "
				+ getFinAdvancePaymentsList().size() + ".\n Do you want to continue?";

		MessageUtil.confirm(msg, evnt -> {
			if (Messagebox.ON_YES.equals(evnt.getName())) {
				prepareRequest(disbushmentList);
			}
		});
	}

	private void prepareRequest(List<FinAdvancePayments> disbushmentList) {
		try {
			btnDownload.setDisabled(true);
			button_Search.setDisabled(true);

			com.pennanttech.pff.core.disbursement.model.DisbursementRequest request = new com.pennanttech.pff.core.disbursement.model.DisbursementRequest();
			PartnerBank partBank = new PartnerBank();

			if (PartnerBankExtension.BRANCH_WISE_MAPPING) {
				Search search = new Search(PartnerBank.class);
				search.addFilterEqual("PartnerBankId", this.partnerBank.getAttribute("Id"));

				SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
				partBank = (PartnerBank) searchProcessor.getResults(search).get(0);
			} else {
				partBank = (PartnerBank) partnerBank.getObject();
			}

			String configName = partBank.getDataEngineConfigName();
			String fileName = partBank.getFileName();
			long userId = getUserWorkspace().getLoggedInUser().getUserId();
			request.setFinType(this.finType.getValue());
			request.setPartnerBankCode(this.partnerBank.getValue());
			request.setPartnerBankId(partBank.getPartnerBankId());
			request.setFinAdvancePayments(disbushmentList);
			request.setUserId(userId);
			request.setDataEngineConfigName(configName);
			request.setFileNamePrefix(fileName);
			request.setAutoDownload(false);
			request.setChannel(getComboboxValue(this.channelTypes));
			request.setLoggedInUser(getUserWorkspace().getLoggedInUser());
			request.setAppValueDate(SysParamUtil.getAppValueDate());

			disbursementRequestService.prepareRequest(request);

			Map<String, Object> args = new HashMap<String, Object>();
			args.put("module", "DISBURSEMENT");

			MessageUtil.showMessage("File download process initiated.");
			createNewPage("/WEB-INF/pages/InterfaceBajaj/DisbursementFileDownloadList.zul",
					"menu_Item_DisbursementFileDownlaods", args);
		} catch (Exception e) {
			MessageUtil.showError(e.getMessage());

		} finally {
			this.disbursementMap.clear();
			this.listHeader_CheckBox_Comp.setChecked(false);
			renderDisbursements();
			btnDownload.setDisabled(false);
			button_Search.setDisabled(false);
			logger.debug("Leaving");
		}
	}

	protected void createNewPage(String uri, String tabName, Map<String, Object> args) {
		final Borderlayout bl = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		final Center center = bl.getCenter();
		final Tabs tabs = (Tabs) center.getFellow("divCenter").getFellow("tabBoxIndexCenter")
				.getFellow("tabsIndexCenter");

		Tab tab = null;
		if (tabs.getFellowIfAny(tabName.trim().replace("menu_Item_", "tab_")) != null) {
			tab = (Tab) tabs.getFellow(tabName.trim().replace("menu_Item_", "tab_"));
			if (tab != null) {
				tab.close();
			}
		}
		tab = new Tab();
		tab.setId(tabName.trim().replace("menu_Item_", "tab_"));
		tab.setLabel(Labels.getLabel(tabName));
		tab.setClosable(true);
		tab.setParent(tabs);
		tab.setLabel("Disbursement File Control");

		final Tabpanels tabpanels = (Tabpanels) tabs.getFellow("tabpanelsBoxIndexCenter");
		final Tabpanel tabpanel = new Tabpanel();
		tabpanel.setHeight("100%");
		tabpanel.setStyle("padding: 0px;");
		tabpanel.setParent(tabpanels);

		Executions.createComponents(uri, tabpanel, args);
		tab.setSelected(true);
	}

	/**
	 * Based On Entity field,Partner Bank will be Filtered
	 * 
	 * @param event
	 */
	public void onFulfill$entity(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = entity.getObject();
		if (dataObject instanceof String) {
			this.partnerBank.setButtonDisabled(true);
			this.partnerBank.setMandatoryStyle(false);
			this.partnerBank.setValue("");
			this.partnerBank.setDescription("");

			logger.debug(Literal.LEAVING);
			return;
		}

		Entity details = (Entity) dataObject;
		this.partnerBank.setObject("");
		this.partnerBank.setValue("");
		this.partnerBank.setDescription("");

		if (details == null) {
			this.partnerBank.setValue("");
			this.partnerBank.setDescription("");
			this.partnerBank.setButtonDisabled(true);
			this.partnerBank.setMandatoryStyle(false);

			logger.debug(Literal.LEAVING);
			return;
		}

		this.partnerBank.setButtonDisabled(false);
		this.partnerBank.setMandatoryStyle(true);

		if (!PartnerBankExtension.BRANCH_WISE_MAPPING) {
			Filter[] filters = new Filter[1];
			filters[0] = new Filter("Entity", details.getEntityCode(), Filter.OP_EQUAL);
			this.partnerBank.setFilters(filters);

			return;
		}

		Filter[] filters = new Filter[3];
		filters[0] = new Filter("Purpose", "D", Filter.OP_EQUAL);
		filters[1] = new Filter("PaymentMode", this.disbTypes.getSelectedItem().getValue(), Filter.OP_EQUAL);

		String branchCode = getUserWorkspace().getLoggedInUser().getBranchCode();
		Long clusterId = null;

		List<String> branchlist = new ArrayList<>();

		if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("B")) {
			filters[2] = new Filter("BranchCode", branchCode, Filter.OP_EQUAL);

			branchlist.add(branchCode);

		} else if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("C")) {
			clusterId = clusterService.getClustersFilter(branchCode);
			branchlist.addAll(branchService.getBranchCodeByClusterId(clusterId));

			if (CollectionUtils.isEmpty(branchlist)) {
				this.partnerBank.setErrorMessage("please configure the branch with partnerbank.");
				this.partnerBank.setButtonDisabled(true);
			}

			filters[2] = new Filter("ClusterId", clusterId, Filter.OP_EQUAL);
		}

		List<FinTypePartnerBank> list = finTypePartnerBankService.getFintypePartnerBankByBranch(branchlist, clusterId);

		if (list.size() == 1) {
			this.partnerBank.setAttribute("Id", list.get(0).getPartnerBankID());
			this.partnerBank.setValue(list.get(0).getPartnerBankCode());
			this.partnerBank.setDescription(list.get(0).getPartnerBankName());
			this.partnerBank.setObject(list.get(0));

			onFullfillPartnerBank();
		}

		this.partnerBank.setFilters(filters);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$partnerBank(Event event) {
		onFullfillPartnerBank();
	}

	public void onFullfillPartnerBank() {
		if (!PartnerBankExtension.BRANCH_WISE_MAPPING) {
			return;
		}

		Object dataObject = partnerBank.getObject();

		if (dataObject == null || dataObject.equals("")) {
			return;
		}

		FinTypePartnerBank details = (FinTypePartnerBank) dataObject;
		Long partnerBankID = details.getPartnerBankID();
		this.partnerBank.setAttribute("Id", partnerBankID);

		if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("B")) {
			Filter[] filters = new Filter[2];
			filters[0] = new Filter("PartnerbankId", partnerBankID, Filter.OP_EQUAL);
			filters[1] = new Filter("BranchCode", "", Filter.OP_NOT_NULL);

			this.branchOrCluster.setFilters(filters);
			this.branchOrCluster.setButtonDisabled(false);
			this.branchOrCluster.setMandatoryStyle(true);
		} else if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("C")) {
			List<Long> clusterList = new ArrayList<Long>();
			clusterList = finTypePartnerBankService.getByClusterAndPartnerbank(partnerBankID);

			if (CollectionUtils.isNotEmpty(clusterList)) {
				Filter[] filters = new Filter[1];
				filters[0] = new Filter("Id", clusterList, Filter.OP_IN);
				this.branchOrCluster.setFilters(filters);
				this.branchOrCluster.setButtonDisabled(false);
				this.branchOrCluster.setMandatoryStyle(true);
			} else {
				this.branchOrCluster.setErrorMessage("please configure the cluster with partnerbank.");
				this.branchOrCluster.setButtonDisabled(true);
			}
		}
	}

	public void onChange$disbParty(Event event) {
		this.vasManufacturer.setValue("");
		String disbParty = this.disbParty.getSelectedItem().getValue();

		boolean vasParty = DisbursementConstants.PAYMENT_DETAIL_VAS.equals(disbParty);
		this.cellVMFC1.setVisible(vasParty);
		this.cellVMFC2.setVisible(vasParty);
		this.cellVMFC3.setVisible(vasParty);
	}

	public void onFulfill$branchOrCluster(Event event) {

		logger.debug(Literal.ENTERING);

		if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("B")) {
			FinTypePartnerBank finTypePartnerBank = (FinTypePartnerBank) this.branchOrCluster.getObject();

			if (finTypePartnerBank == null) {
				return;
			}
			Search search = new Search(FinTypePartnerBank.class);
			search.addFilterEqual("Id", finTypePartnerBank.getId());

			SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
			FinTypePartnerBank fpb = (FinTypePartnerBank) searchProcessor.getResults(search).get(0);
			this.branchOrCluster.setId(String.valueOf(fpb.getId()));
			this.branchOrCluster.setValue(fpb.getBranchCode());
			this.branchOrCluster.setDescription(fpb.getBranchDesc());
		} else {
			Cluster cluster = (Cluster) this.branchOrCluster.getObject();

			if (cluster == null) {
				return;
			}
			Search search = new Search(Cluster.class);
			search.addFilterEqual("Id", cluster.getId());

			SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
			Cluster clusterData = (Cluster) searchProcessor.getResults(search).get(0);
			this.branchOrCluster.setId(String.valueOf(clusterData.getId()));
			this.branchOrCluster.setValue(clusterData.getCode());
			this.branchOrCluster.setDescription(clusterData.getName());
		}

		logger.debug(Literal.LEAVING + event.toString());

	}

	public List<FinAdvancePayments> getFinAdvancePaymentsList() {
		return finAdvancePaymentsList;
	}

	public void setFinAdvancePaymentsList(List<FinAdvancePayments> finAdvancePaymentsList) {
		this.finAdvancePaymentsList = finAdvancePaymentsList;
	}

	@Autowired
	public void setDisbursementRequestService(DisbursementRequestService disbursementRequestService) {
		this.disbursementRequestService = disbursementRequestService;
	}

	/**
	 * Method for fetching Currency format of selected currency
	 * 
	 * @return
	 */
	public int getCcyFormat() {
		return CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
	}

	public void setFinTypePartnerBankService(FinTypePartnerBankService finTypePartnerBankService) {
		this.finTypePartnerBankService = finTypePartnerBankService;
	}

	public void setClusterService(ClusterService clusterService) {
		this.clusterService = clusterService;
	}

	public BranchService getBranchService() {
		return branchService;
	}

	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}

}
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
 * * FileName : MandateListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 18-10-2016 * * Modified Date
 * : 18-10-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-10-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennanttech.interfacebajaj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Center;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Cluster;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.service.applicationmaster.BranchService;
import com.pennant.backend.service.applicationmaster.ClusterService;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.service.rmtmasters.FinTypePartnerBankService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.extension.MandateExtension;
import com.pennant.pff.extension.PartnerBankExtension;
import com.pennant.pff.mandate.MandateStatus;
import com.pennant.pff.mandate.MandateUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.external.service.ExternalInterfaceService;
import com.pennanttech.pff.model.mandate.MandateData;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/Mandate/MandateRegistration.zul file.<br>
 * ************************************************************<br>
 * 
 */
public class MandateRegistrationListCtrl extends GFCBaseListCtrl<Mandate> {
	private static final long serialVersionUID = 1L;

	protected Window window_MandateRegistrationList;
	protected Borderlayout borderLayout_MandateList;
	protected Paging pagingMandateList;
	protected Listbox listBoxMandateRegistration;

	protected Listheader listheader_CustCIF;
	protected Listheader listheader_MandateType;
	protected Listheader listheader_BankName;
	protected Listheader listheader_AccNumber;
	protected Listheader listheader_AccType;
	protected Listheader listheader_Amount;
	protected Listheader listheader_CustName;
	protected Listheader listheader_ExpiryDate;
	protected Listheader listheader_Status;
	protected Listheader listheader_InputDate;
	protected Listheader listheader_BranchOrClster;

	protected Listheader listHeader_CheckBox_Name;
	protected Listcell listCell_Checkbox;
	protected Listitem listItem_Checkbox;
	protected Checkbox listHeader_CheckBox_Comp;

	protected Checkbox list_CheckBox;

	protected Button button_MandateList_NewMandate;
	protected Button button_MandateList_MandateSearch;
	protected Button btnDownload;

	protected Longbox mandateID;
	protected Combobox mandateType;
	protected Space space_MandateType;
	protected Textbox custCIF;
	protected Textbox bankName;
	protected Combobox status;
	protected Textbox accNumber;
	protected Combobox accType;
	protected Datebox expiryDate;

	protected Datebox fromDate;
	protected Datebox toDate;
	protected ExtendedCombobox bankBranchID;
	protected ExtendedCombobox branchOrCluster;

	protected Listbox sortOperator_MandateID;
	protected Listbox sortOperator_bankBranchID;
	protected Listbox sortOperator_CustCIF;
	protected Listbox sortOperator_MandateType;
	protected Listbox sortOperator_BankName;
	protected Listbox sortOperator_AccNumber;
	protected Listbox sortOperator_AccType;
	protected Listbox sortOperator_ExpiryDate;
	protected Listbox sortOperator_Status;
	protected ExtendedCombobox entityCode;
	protected Listbox sortOperator_entityCode;
	protected ExtendedCombobox partnerBank;
	protected Listbox sortOperator_partnerBank;
	protected Listbox sortOperator_BranchOrCluster;
	protected Row row_partnerBank;
	private transient MandateService mandateService;
	private transient boolean validationOn;
	private Map<Long, String> mandateIdMap = new HashMap<>();
	protected JdbcSearchObject<Customer> custCIFSearchObject;
	private ExternalInterfaceService externalInterfaceService;

	private transient ClusterService clusterService;
	private transient BranchService branchService;
	private transient FinTypePartnerBankService finTypePartnerBankService;

	/**
	 * default constructor.<br>
	 */
	public MandateRegistrationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "MandateRegistration";
		super.pageRightName = "MandateRegistration";
		super.tableName = "INT_MANDATE_REQUEST_VIEW";
		super.queueTableName = "INT_MANDATE_REQUEST_VIEW";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_MandateRegistrationList(Event event) {
		// Set the page level components.
		setPageComponents(window_MandateRegistrationList, borderLayout_MandateList, listBoxMandateRegistration,
				pagingMandateList);
		setItemRender(new MandateListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_MandateList_MandateSearch);

		fillComboBox(this.mandateType, "", MandateUtil.getInstrumentTypes(), "");
		fillComboBox(this.accType, "", MandateUtil.getAccountTypes(), "");
		fillComboBox(this.status, "", MandateUtil.getMandateStatus(), Collections.singletonList(MandateStatus.FIN));

		registerField("inputDate");

		registerField("BranchCode", bankBranchID, SortOrder.ASC, sortOperator_bankBranchID, Operators.MULTISELECT);
		registerField("mandateID", mandateID, SortOrder.ASC, sortOperator_MandateID, Operators.NUMERIC);
		registerField("mandateType", listheader_MandateType, SortOrder.NONE, mandateType, sortOperator_MandateType,
				Operators.STRING);
		registerField("custCIF", listheader_CustCIF, SortOrder.NONE, custCIF, sortOperator_CustCIF, Operators.STRING);
		registerField("accNumber", listheader_AccNumber, SortOrder.NONE, accNumber, sortOperator_AccNumber,
				Operators.STRING);
		registerField("accType", listheader_AccType, SortOrder.NONE, accType, sortOperator_AccType, Operators.STRING);
		registerField("expiryDate", listheader_ExpiryDate, SortOrder.NONE, expiryDate, sortOperator_ExpiryDate,
				Operators.DATE);
		registerField("bankName", listheader_BankName, SortOrder.NONE, bankName, sortOperator_BankName,
				Operators.STRING);
		registerField("status", listheader_Status, SortOrder.NONE, status, sortOperator_Status, Operators.STRING);
		registerField("maxLimit", listheader_Amount);
		registerField("custShrtName", listheader_CustName, SortOrder.NONE);
		registerField("entityCode", entityCode, SortOrder.NONE, sortOperator_entityCode, Operators.STRING);
		if (MandateExtension.PARTNER_BANK_WISE_EXTARCTION) {
			registerField("PartnerBankId");
			registerField("PartnerBankCode", partnerBank, SortOrder.NONE, sortOperator_partnerBank, Operators.STRING);
		}

		// Render the page and display the data.
		doRenderPage();
		this.mandateIdMap.clear();
		doSetFieldProperties();

		if (listBoxMandateRegistration.getItems().size() > 0) {
			listHeader_CheckBox_Comp.setDisabled(false);
		} else {
			listHeader_CheckBox_Comp.setDisabled(true);
		}
	}

	private void doSetFieldProperties() {

		this.expiryDate.setFormat(DateFormat.LONG_DATE.getPattern());
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

		this.entityCode.setMaxlength(8);
		this.entityCode.setTextBoxWidth(135);
		this.entityCode.setMandatoryStyle(true);
		this.entityCode.setModuleName("Entity");
		this.entityCode.setValueColumn("EntityCode");
		this.entityCode.setDescColumn("EntityDesc");
		this.entityCode.setValidateColumns(new String[] { "EntityCode" });

		if (MandateExtension.PARTNER_BANK_WISE_EXTARCTION) {
			this.space_MandateType.setSclass(PennantConstants.mandateSclass);
		} else {
			this.space_MandateType.setSclass("");
		}

		if (MandateExtension.PARTNER_BANK_WISE_EXTARCTION) {
			if (PartnerBankExtension.BRANCH_WISE_MAPPING) {
				this.row_partnerBank.setVisible(true);
				this.partnerBank.setModuleName("FinTypePartner");
				this.partnerBank.setMandatoryStyle(true);
				this.partnerBank.setValueColumn("PartnerBankCode");
				this.partnerBank.setDescColumn("PartnerBankName");
				this.partnerBank.setMaxlength(14);
				this.partnerBank.setValidateColumns(new String[] { "PartnerBankCode" });

				Filter[] filter = new Filter[1];
				filter[0] = new Filter("Purpose", "R", Filter.OP_EQUAL);
				this.partnerBank.setFilters(filter);

				this.partnerBank.setWhereClause("(CLUSTERID is not null)");

				this.branchOrCluster.setButtonDisabled(false);
				this.branchOrCluster.setMandatoryStyle(true);

				String moduleName = "FinTypePartnerBankBranch";
				String valueColumn = "BranchCode";
				String descColumn = "BranchDesc";

				if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("C")) {
					moduleName = "Cluster";
					valueColumn = "Code";
					descColumn = "Name";

					this.branchOrCluster.setFilters(new Filter[] {
							new Filter("CLUSTERTYPE", PartnerBankExtension.CLUSTER_TYPE, Filter.OP_EQUAL) });
				}

				this.branchOrCluster.setModuleName(moduleName);
				this.branchOrCluster.setValueColumn(valueColumn);
				this.branchOrCluster.setDescColumn(descColumn);
				this.branchOrCluster.setValidateColumns(new String[] { valueColumn, descColumn });

			} else {
				this.row_partnerBank.setVisible(true);

				this.partnerBank.setMaxlength(8);
				this.partnerBank.setTextBoxWidth(135);
				this.partnerBank.setMandatoryStyle(false);
				this.partnerBank.setModuleName("PartnerBank");
				this.partnerBank.setValueColumn("PartnerBankCode");
				this.partnerBank.setDescColumn("PartnerBankName");
				this.partnerBank.setValidateColumns(new String[] { "PartnerBankCode" });
				this.partnerBank.setButtonDisabled(true);
			}
		}

		this.bankBranchID.setModuleName("Branch");
		this.bankBranchID.setMandatoryStyle(false);
		this.bankBranchID.setValueColumn("BranchCode");
		this.bankBranchID.setDescColumn("BranchDesc");
		this.bankBranchID.setDisplayStyle(2);
		this.bankBranchID.setValidateColumns(new String[] { "BranchCode" });

		if (MandateExtension.AUTO_DOWNLOAD
				&& SysParamUtil.isAllowed(SMTParameterConstants.MANDATE_AUTO_DOWNLOAD_JOB_ENABLED)) {
			this.btnDownload.setDisabled(true);
		}
	}

	public void onFulfill$entityCode(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = entityCode.getObject();

		if (dataObject instanceof String) {
			this.partnerBank.setButtonDisabled(true);
			this.partnerBank.setMandatoryStyle(false);
			this.partnerBank.setValue("");
			this.partnerBank.setDescription("");

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
		}

		this.partnerBank.setMandatoryStyle(true);
		if (!PartnerBankExtension.BRANCH_WISE_MAPPING) {
			Filter[] filters = new Filter[1];
			filters[0] = new Filter("Entity", details.getEntityCode(), Filter.OP_EQUAL);
			this.partnerBank.setFilters(filters);

			return;
		}

		String branchCode = getUserWorkspace().getLoggedInUser().getBranchCode();
		Long clusterId = null;
		List<String> branchlist = new ArrayList<String>();

		Filter[] filters = new Filter[2];

		if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("B")) {
			filters[0] = new Filter("BranchCode", branchCode, Filter.OP_EQUAL);
			branchlist.add(branchCode);

		} else if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("C")) {
			clusterId = clusterService.getClustersFilter(branchCode);

			if (clusterId == null) {
				this.branchOrCluster.setErrorMessage("please configure the cluster with Branches.");
				this.branchOrCluster.setButtonDisabled(true);
				return;
			}

			branchlist = branchService.getBranchCodeByClusterId(clusterId);
			if (CollectionUtils.isNotEmpty(branchlist)) {
				filters[0] = new Filter("ClusterId", clusterId, Filter.OP_EQUAL);
				this.branchOrCluster.setMandatoryStyle(true);
			} else {
				this.partnerBank.setErrorMessage("please configure the branch with partnerbank.");
				this.partnerBank.setButtonDisabled(true);
			}
		}

		if (MandateExtension.PARTNER_BANK_WISE_EXTARCTION && PartnerBankExtension.BRANCH_WISE_MAPPING) {
			filters[1] = new Filter("Purpose", "R", Filter.OP_EQUAL);
		}

		List<FinTypePartnerBank> list = finTypePartnerBankService.getFintypePartnerBankByBranch(branchlist, clusterId);
		if (list.size() == 1) {
			this.partnerBank.setAttribute("Id", list.get(0).getPartnerBankID());
			this.partnerBank.setValue(list.get(0).getPartnerBankCode());
			this.partnerBank.setDescription(list.get(0).getPartnerBankName());
			onFullfillPartnerBank();
		}

		this.branchOrCluster.setMandatoryStyle(true);
		this.partnerBank.setFilters(filters);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$bankBranchID(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = this.bankBranchID.getObject();

		if (dataObject == null || dataObject instanceof String) {
			this.bankBranchID.setValue("");
			this.bankBranchID.setDescription("");
			this.bankBranchID.setAttribute("BranchCode", null);
		} else {
			Branch details = (Branch) dataObject;
			if (details != null) {
				this.bankBranchID.setAttribute("BranchCode", details.getBranchCode());
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$partnerBank(Event event) {
		onFullfillPartnerBank();
	}

	public void onFullfillPartnerBank() {
		if (!PartnerBankExtension.BRANCH_WISE_MAPPING) {
			PartnerBank details = (PartnerBank) partnerBank.getObject();
			this.partnerBank.setAttribute("PartnerBankId", details.getId());
			onFullfillPartnerBank();
			return;
		}

		Object dataObject = partnerBank.getObject();

		if (dataObject == null || dataObject.equals("")) {
			return;
		}

		FinTypePartnerBank details = (FinTypePartnerBank) dataObject;
		if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("B")) {
			Filter[] filters = new Filter[2];
			filters[0] = new Filter("PartnerbankId", details.getPartnerBankID(), Filter.OP_EQUAL);
			filters[1] = new Filter("BranchCode", "", Filter.OP_NOT_NULL);
			this.branchOrCluster.setFilters(filters);
			this.branchOrCluster.setButtonDisabled(false);
			this.branchOrCluster.setMandatoryStyle(true);
		} else if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("C")) {
			List<Long> clusterList = new ArrayList<Long>();
			clusterList = finTypePartnerBankService.getByClusterAndPartnerbank(details.getPartnerBankID());
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

	/**
	 * Filling the MandateIdMap details and based on checked and unchecked events of listCellCheckBox.
	 */
	public void onClick_listHeaderCheckBox(ForwardEvent event) {
		logger.debug("Entering");

		for (int i = 0; i < listBoxMandateRegistration.getItems().size(); i++) {
			Listitem listitem = listBoxMandateRegistration.getItems().get(i);
			Checkbox cb = (Checkbox) listitem.getChildren().get(0).getChildren().get(0);
			cb.setChecked(listHeader_CheckBox_Comp.isChecked());
		}

		if (listHeader_CheckBox_Comp.isChecked()) {
			List<Long> mandateIdList = getMandateList();
			if (mandateIdList != null) {
				for (Long mandateId : mandateIdList) {
					mandateIdMap.put(mandateId, null);
				}
			}
		} else {
			mandateIdMap.clear();
		}

		logger.debug("Leaving");
	}

	/**
	 * Filling the MandateIdMap details based on checked and unchecked events of listCellCheckBox.
	 */
	public void onClick_listCellCheckBox(ForwardEvent event) {
		logger.debug("Entering");

		Checkbox checkBox = (Checkbox) event.getOrigin().getTarget();
		if (checkBox.isChecked()) {
			mandateIdMap.put(Long.valueOf(checkBox.getValue().toString()), checkBox.getValue().toString());
		} else {
			mandateIdMap.remove(Long.valueOf(checkBox.getValue().toString()));
		}

		if (mandateIdMap.size() == this.pagingMandateList.getTotalSize()) {
			listHeader_CheckBox_Comp.setChecked(true);
		} else {
			listHeader_CheckBox_Comp.setChecked(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * Item renderer for listItems in the listBox.
	 */
	private class MandateListModelItemRenderer implements ListitemRenderer<Mandate>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, Mandate mandate, int count) {

			Listcell lc;

			lc = new Listcell();
			list_CheckBox = new Checkbox();
			list_CheckBox.setValue(mandate.getMandateID());
			list_CheckBox.addForward("onClick", self, "onClick_listCellCheckBox");
			lc.appendChild(list_CheckBox);
			if (listHeader_CheckBox_Comp.isChecked()) {
				list_CheckBox.setChecked(true);
			} else {
				list_CheckBox.setChecked(mandateIdMap.containsKey(mandate.getMandateID()));
			}
			lc.setParent(item);

			lc = new Listcell(mandate.getMandateType());
			lc.setParent(item);
			lc = new Listcell(mandate.getCustCIF());
			lc.setParent(item);
			lc = new Listcell(mandate.getCustShrtName());
			lc.setParent(item);
			lc = new Listcell(mandate.getBankName());
			lc.setParent(item);
			lc = new Listcell(mandate.getAccNumber());
			lc.setParent(item);
			lc = new Listcell(mandate.getAccType());
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.amountFormate(mandate.getMaxLimit(),
					CurrencyUtil.getFormat(mandate.getMandateCcy())));
			lc.setParent(item);
			lc = new Listcell(DateUtil.formatToLongDate(mandate.getExpiryDate()));
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.getLabelDesc(mandate.getStatus(), MandateUtil.getMandateStatus()));
			lc.setParent(item);
			lc = new Listcell(DateUtil.formatToLongDate(mandate.getInputDate()));
			lc.setParent(item);
			lc = new Listcell(mandate.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(mandate.getRecordType()));
			lc.setParent(item);

			item.setAttribute("id", mandate.getMandateID());

			ComponentsCtrl.applyForward(item, "onDoubleClick=onMandateItemDoubleClicked");
		}
	}

	/**
	 * Getting the mandate list using JdbcSearchObject with search criteria..
	 */
	private List<Long> getMandateList() {

		JdbcSearchObject<Map<String, Long>> searchObject = new JdbcSearchObject<>();
		// searchObject.addFilterEqual("active", 1);
		// searchObject.addFilterEqual("Status", MandateConstants.STATUS_NEW);
		// searchObject.addFilter(Filter.isNotNull("OrgReference"));
		searchObject.addField("mandateID");
		searchObject.addTabelName(this.tableName);

		for (SearchFilterControl searchControl : searchControls) {
			Filter filter = searchControl.getFilter();
			if (filter != null) {
				if (filter.getProperty().equals("accType")) {
					List<ValueLabel> accTypeList = MandateUtil.getAccountTypes();
					for (ValueLabel valueLabel : accTypeList) {
						if (valueLabel.getValue().equals(filter.getValue())) {
							filter.setValue(valueLabel.getLabel());
							break;
						}
					}
				}
				searchObject.addFilter(filter);
			}
		}

		String fromDate = PennantAppUtil.formateDate(this.fromDate.getValue(), PennantConstants.DBDateFormat);
		String toDate = PennantAppUtil.formateDate(this.toDate.getValue(), PennantConstants.DBDateFormat);
		String manadateType = getComboboxValue(this.mandateType);
		String partnerBankCode = this.partnerBank.getValue();

		StringBuilder whereClause = new StringBuilder();
		whereClause.append("(INPUTDATE >= ").append("'").append(fromDate).append("'").append(" AND INPUTDATE <= ")
				.append("'").append(toDate).append("'").append(")");
		if (MandateExtension.PARTNER_BANK_WISE_EXTARCTION) {
			whereClause.append(" AND MandateType =").append("'").append(manadateType).append("'")
					.append(" AND PartnerBankCode = ").append("'").append(partnerBankCode).append("'");
		}
		searchObject.addWhereClause(whereClause.toString());

		List<Map<String, Long>> list = getPagedListWrapper().getPagedListService().getBySearchObject(searchObject);
		List<Long> mandateLst = new ArrayList<Long>();

		if (list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Long> map = (Map<String, Long>) list.get(i);
				mandateLst.add(Long.parseLong(String.valueOf(map.get("mandateID"))));
			}
		}
		return mandateLst;
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_MandateList_MandateSearch(Event event) {
		this.mandateIdMap.clear();
		this.listHeader_CheckBox_Comp.setChecked(false);

		doSetValidations();
		search();

		if (listBoxMandateRegistration.getItems().size() > 0) {
			listHeader_CheckBox_Comp.setDisabled(false);
		} else {
			listHeader_CheckBox_Comp.setDisabled(true);
			listBoxMandateRegistration.setEmptyMessage(Labels.getLabel("listEmptyMessage.title"));
		}
	}

	@Override
	public void search() {
		logger.debug("Entering");

		this.searchObject.clearFilters();

		if (paging != null) {
			this.paging.setActivePage(0);
		}

		if (enqiryModule) {
			if (fromApproved != null && fromWorkFlow != null) {
				if (fromApproved.isChecked()) {
					this.searchObject.addTabelName(tableName);
				} else if (fromWorkFlow.isChecked()) {
					this.searchObject.addTabelName(enquiryTableName);
				} else {
					this.searchObject.addTabelName(tableName);
				}
			}
		}

		doAddFilters();

		String fromDate = PennantAppUtil.formateDate(this.fromDate.getValue(), PennantConstants.DBDateFormat);
		String toDate = PennantAppUtil.formateDate(this.toDate.getValue(), PennantConstants.DBDateFormat);

		// searchObject.addFilterEqual("active", 1);
		// searchObject.addFilterEqual("Status", MandateConstants.STATUS_NEW);
		// OrgReference
		// searchObject.addFilter(Filter.isNotNull("OrgReference"));

		StringBuilder whereClause = new StringBuilder();
		whereClause.append("(INPUTDATE >= ").append("'").append(fromDate).append("'").append(" AND INPUTDATE <= ")
				.append("'").append(toDate).append("'").append(")");

		if (PartnerBankExtension.BRANCH_WISE_MAPPING) {
			if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("C")) {
				if (this.branchOrCluster.getValue() != null) {
					whereClause.append(" And BRANCHCODE In (Select BranchCode from RMTBranches where ClusterId in (");
					whereClause.append(this.branchOrCluster.getId());
					whereClause.append("))");
					searchObject.addWhereClause(whereClause.toString());
				}
			} else {
				if (this.branchOrCluster.getValue() != null) {
					whereClause.append(" And BRANCHCODE In (Select BranchCode from RMTBranches where ClusterId in (");
					whereClause.append(this.branchOrCluster.getId());
					whereClause.append("))");
					searchObject.addWhereClause(whereClause.toString());
				}
			}
		}

		this.searchObject.addWhereClause(whereClause.toString());

		this.listbox.setItemRenderer(new MandateListModelItemRenderer());

		getPagedListWrapper().setPagedListService(pagedListService);
		getPagedListWrapper().init(this.searchObject, this.listbox, this.paging);

		logger.debug("Leaving");
	}

	@Override
	protected void doAddFilters() {
		logger.debug("Entering");

		for (SearchFilterControl searchControl : searchControls) {
			Filter filter = searchControl.getFilter();
			if (filter != null) {

				if (filter.getProperty().equals("accType")) {
					List<ValueLabel> accTypeList = MandateUtil.getAccountTypes();
					for (ValueLabel valueLabel : accTypeList) {
						if (valueLabel.getValue().equals(filter.getValue())) {
							filter.setValue(valueLabel.getLabel());
							break;
						}
					}
				}

				if (App.DATABASE == Database.ORACLE && "recordType".equals(filter.getProperty())
						&& Filter.OP_NOT_EQUAL == filter.getOperator()) {
					Filter[] filters = new Filter[2];
					filters[0] = Filter.isNull(filter.getProperty());
					filters[1] = filter;

					this.searchObject.addFilterOr(filters);
				} else {
					this.searchObject.addFilter(filter);
				}
			}
		}
		logger.debug("Leaving");
	}

	private void doSetValidations() {

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (!this.fromDate.isDisabled())
				this.fromDate.setConstraint(new PTDateValidator("From Date", true));
			this.fromDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.toDate.isDisabled())
				this.toDate.setConstraint(new PTDateValidator("To Date", true));
			this.toDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.entityCode.isReadonly())
				this.entityCode.setConstraint(new PTStringValidator(
						Labels.getLabel("label_MandateDialog_EntityCode.value"), null, true, true));
			this.entityCode.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (StringUtils.isNotEmpty(this.entityCode.getValidatedValue())) {
				if (!this.partnerBank.isReadonly() && MandateExtension.PARTNER_BANK_WISE_EXTARCTION) {
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
			if (MandateExtension.PARTNER_BANK_WISE_EXTARCTION
					&& getComboboxValue(this.mandateType).equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.mandateType, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_MandateList_MandateType.value") }));
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

		if (this.fromDate.getValue().compareTo(this.toDate.getValue()) == 1) {
			throw new WrongValueException(this.toDate, "To date should be greater than or equal to From date.");
		}

	}

	private void doRemoveValidation() {
		logger.debug("Entering ");

		this.mandateType.setErrorMessage("");
		this.fromDate.setConstraint("");
		this.toDate.setConstraint("");
		this.entityCode.setConstraint("");
		this.entityCode.setErrorMessage("");
		this.partnerBank.setConstraint("");
		this.partnerBank.setErrorMessage("");
		this.branchOrCluster.setConstraint("");
		this.branchOrCluster.setErrorMessage("");
		logger.debug("Leaving ");

	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		doRemoveValidation();
		this.mandateIdMap.clear();
		this.listHeader_CheckBox_Comp.setChecked(false);
		this.listBoxMandateRegistration.getItems().clear();
		this.fromDate.setValue(null);
		this.toDate.setValue(null);
		this.entityCode.setValue("");
		this.entityCode.setDescColumn("");

		this.partnerBank.setButtonDisabled(false);
		this.partnerBank.setMandatoryStyle(false);
		this.partnerBank.setValue("");
		this.partnerBank.setDescColumn("");

		this.branchOrCluster.setValue("");
		this.branchOrCluster.setDescColumn("");

		this.listheader_AccNumber.setSort("none");
		this.listheader_AccType.setSort("none");
		this.listheader_Amount.setSort("none");
		this.listheader_BankName.setSort("none");
		this.listheader_CustCIF.setSort("none");
		this.listheader_MandateType.setSort("none");
		this.listheader_CustName.setSort("none");
		this.listheader_ExpiryDate.setSort("none");
		this.listheader_Status.setSort("none");
		this.listheader_InputDate.setSort("none");

		if (listBoxMandateRegistration.getItems().size() > 0) {
			listHeader_CheckBox_Comp.setDisabled(false);
		} else {
			listHeader_CheckBox_Comp.setDisabled(true);
			listBoxMandateRegistration.setEmptyMessage("");

		}
		this.pagingMandateList.setTotalSize(0);

	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onMandateItemDoubleClicked(ForwardEvent event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = (Listitem) event.getOrigin().getTarget();
		Mandate mandate = null;
		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		mandate = mandateService.getMandateById(id);

		if (mandate == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " where MandateID=?";

		if (doCheckAuthority(mandate, whereCond, new Object[] { mandate.getMandateID() })) {
			// Since workflow is not applicable for mandate registration
			if (isWorkFlowEnabled() && mandate.getWorkflowId() == 0) {
				mandate.setWorkflowId(0);
			}
			doShowDialogPage(mandate);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param mandate The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Mandate mandate) {
		logger.debug("Entering");

		Map<String, Object> arg = new HashMap<String, Object>();
		arg.put("mandate", mandate);
		arg.put("mandateRegistrationListCtrl", this);
		arg.put("enqModule", enqiryModule);
		arg.put("fromLoan", false);
		arg.put("registration", true);

		try {
			Executions.createComponents("/WEB-INF/pages/Mandate/MandateDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");

	}

	/**
	 * when the "Upload" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnDownload(Event event) {
		logger.debug(Literal.ENTERING);

		List<Long> mandateIdList;
		String mandateCount = String.valueOf(MandateExtension.MANDATE_SPLIT_COUNT);

		if (listHeader_CheckBox_Comp.isChecked()) {
			mandateIdList = getMandateList();
		} else {
			mandateIdList = new ArrayList<Long>(mandateIdMap.keySet());
		}

		if (mandateIdList.isEmpty()) {
			MessageUtil.showError(Labels.getLabel("MandateDataList_NoEmpty"));
			return;
		}

		if (MandateExtension.MANDATE_SPLIT_COUNT > 0
				&& this.mandateIdMap.size() > MandateExtension.MANDATE_SPLIT_COUNT) {
			MessageUtil.showError(Labels.getLabel("MandateDataList_TooLong", new String[] { mandateCount }));
			return;
		}

		String msg = "You have selected " + this.mandateIdMap.size() + " Mandate(s) out of "
				+ this.pagingMandateList.getTotalSize() + ".\nDo you want to continue?";

		MessageUtil.confirm(msg, evnt -> {
			if (Messagebox.ON_YES.equals(evnt.getName())) {
				download(mandateIdList);
			}
		});
	}

	private void download(List<Long> mandateIdList) {
		try {
			btnDownload.setDisabled(true);
			LoggedInUser loggedInUser = getUserWorkspace().getLoggedInUser();
			MandateData mandateData = new MandateData();
			mandateData.setMandateIdList(mandateIdList);
			mandateData.setFromDate(fromDate.getValue());
			mandateData.setToDate(toDate.getValue());
			mandateData.setUserId(loggedInUser.getUserId());
			mandateData.setUserName(loggedInUser.getUserName());
			mandateData.setEntity(this.entityCode.getValue());
			mandateData.setSelectedBranchs(this.bankBranchID.getValue());
			mandateData.setType(getComboboxValue(this.mandateType));

			if (PartnerBankExtension.BRANCH_WISE_MAPPING) {
				mandateData.setBranchOrCluster(this.branchOrCluster.getDescription());
			}

			Object object = this.partnerBank.getAttribute("PartnerBankId");
			if (object != null) {
				mandateData.setPartnerBankId(Long.parseLong(object.toString()));
			}
			String toDate = PennantAppUtil.formateDate(SysParamUtil.getAppDate(), "dd-MM-yyyy");
			mandateData.setRemarks(toDate.concat("_" + String.valueOf(mandateIdList.size())));

			MandateProcessThread process = new MandateProcessThread(mandateData);
			Thread thread = new Thread(process);
			thread.start();

			Map<String, Object> args = new HashMap<String, Object>();
			args.put("module", "MANDATES");
			MessageUtil.showMessage("File Download process initiated.");
			createNewPage("/WEB-INF/pages/InterfaceBajaj/FileDownloadList.zul", "menu_Item_MandatesFileDownlaods",
					args);
			MandateProcessThread.sleep(4000);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			this.mandateIdMap.clear();
			this.listHeader_CheckBox_Comp.setChecked(false);
			search();
			btnDownload.setDisabled(false);
			logger.debug(Literal.LEAVING);
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
		tab.setLabel("Mandate File Control");

		final Tabpanels tabpanels = (Tabpanels) tabs.getFellow("tabpanelsBoxIndexCenter");
		final Tabpanel tabpanel = new Tabpanel();
		tabpanel.setHeight("100%");
		tabpanel.setStyle("padding: 0px;");
		tabpanel.setParent(tabpanels);

		Executions.createComponents(uri, tabpanel, args);
		tab.setSelected(true);
	}

	public void onFulfill$branchOrCluster(Event event) {
		logger.debug(Literal.ENTERING);

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
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void setMandateService(MandateService mandateService) {
		this.mandateService = mandateService;
	}

	public void setDataSource(DataSource dataSource) {
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public class MandateProcessThread extends Thread {
		MandateData mandateData;

		public MandateProcessThread(MandateData mandateData) {
			this.mandateData = mandateData;
		}

		@Override
		public void run() {
			try {
				externalInterfaceService.processMandateRequest(mandateData);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}

	}

	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		doSearchCustomerCIF();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method for Showing Customer Search Window
	 */
	private void doSearchCustomerCIF() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		Map<String, Object> map = getDefaultArguments();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}

	/**
	 * Method for setting Customer Details on Search Filters
	 * 
	 * @param nCustomer
	 * @param newSearchObject
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug("Entering");
		this.custCIF.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.custCIF.setValue(customer.getCustCIF());
		} else {
			this.custCIF.setValue("");
		}
		logger.debug("Leaving ");
	}

	@Autowired
	public void setExternalInterfaceService(ExternalInterfaceService externalInterfaceService) {
		this.externalInterfaceService = externalInterfaceService;
	}

	public void setClusterService(ClusterService clusterService) {
		this.clusterService = clusterService;
	}

	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}

	public void setFinTypePartnerBankService(FinTypePartnerBankService finTypePartnerBankService) {
		this.finTypePartnerBankService = finTypePartnerBankService;
	}

}
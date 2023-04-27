package com.pennant.webui.rmtmasters.financetype;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Cluster;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.service.rmtmasters.FinTypePartnerBankService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.pff.extension.PartnerBankExtension;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class FinTypePartnerbankMappingListCtrl extends GFCBaseListCtrl<FinTypePartnerBank> {
	private static final long serialVersionUID = 1L;

	protected Window window_FinTypeParterbankMappingList;
	protected Borderlayout borderLayout_FinTypeParterbankMappingList;
	protected Paging pagingFinTypePartnerbankMapping;
	protected Listbox listBoxFinTypePartnerbankMapping;

	// List headers
	protected Listheader listheader_Purpose;
	protected Listheader listheader_PaymentMode;
	protected Listheader listheader_Partnerbank;
	protected Listheader listheader_BranchOrClster;
	protected Listheader listheader_FinType;
	protected Listheader listheader_SuspenseAc;

	// checkRights
	protected Button button_FinTypeParterbankMappingList_NewFinTypeParterbankMapping;
	protected Button button_FinTypeParterbankMappingList_FinTypeParterbankMappingSearchDialog;
	protected Button button_FinTypeParterbankMappingList_PrintList;

	// Search Fields
	protected ExtendedCombobox finType;
	protected Textbox finTypeDesc;
	protected Combobox purpose;
	protected Combobox paymentType;
	protected ExtendedCombobox partnerBank;
	protected ExtendedCombobox branchOrCluster;
	protected Listbox operation;
	protected Textbox suspenseAc;

	protected Listbox sortOperator_finType;
	protected Listbox sortOperator_finTypeDesc;
	protected Listbox sortOperator_paymentType;
	protected Listbox sortOperator_partnerBank;
	protected Listbox sortOperator_purpose;
	protected Listbox sortOperator_branchOrCluster;
	protected Listbox sortOperator_SuspenseAc;

	private transient FinTypePartnerBankService finTypePartnerBankService;
	List<ValueLabel> purposeList = PennantStaticListUtil.getPurposeList();
	List<ValueLabel> paymentModesList = PennantStaticListUtil.getAllPaymentTypes();

	/**
	 * default constructor.<br>
	 */
	public FinTypePartnerbankMappingListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FinTypePartnerBank";
		super.pageRightName = "FinTypePartnerBankMappingList";
		super.tableName = "FINTYPEPARTNERBANKS_AVIEW";
		super.queueTableName = "FINTYPEPARTNERBANKS_VIEW";
		super.enquiryTableName = "FINTYPEPARTNERBANKS_VIEW";
	}

	public void onCreate$window_FinTypeParterbankMappingList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_FinTypeParterbankMappingList, borderLayout_FinTypeParterbankMappingList,
				listBoxFinTypePartnerbankMapping, pagingFinTypePartnerbankMapping);
		setItemRender(new FinTypePartnerbankListModelItemRenderer());

		fillComboBox(this.purpose, "", purposeList, "");
		fillComboBox(this.paymentType, "", paymentModesList, "");

		// Register buttons and fields.
		registerButton(button_FinTypeParterbankMappingList_FinTypeParterbankMappingSearchDialog);
		registerButton(button_FinTypeParterbankMappingList_PrintList);
		registerButton(button_FinTypeParterbankMappingList_NewFinTypeParterbankMapping,
				"button_FinTypeParterbankMappingList_NewFinTypeParterbankMapping", true);
		registerField("Id");
		registerField("Purpose", listheader_Purpose, SortOrder.NONE, purpose, sortOperator_purpose, Operators.STRING);
		registerField("PaymentMode", listheader_PaymentMode, SortOrder.ASC, paymentType, sortOperator_paymentType,
				Operators.STRING);
		registerField("PartnerBankCode", listheader_Partnerbank, SortOrder.NONE, partnerBank, sortOperator_partnerBank,
				Operators.STRING);
		registerField("FinType", listheader_FinType, SortOrder.NONE, finType, sortOperator_finType, Operators.STRING);
		registerField("finTypeDesc", finTypeDesc, SortOrder.NONE, sortOperator_finTypeDesc, Operators.STRING);
		registerField("BranchCode");
		registerField("ClusterCode", listheader_BranchOrClster, SortOrder.NONE, branchOrCluster,
				sortOperator_branchOrCluster, Operators.STRING);
		registerField("BranchDesc");
		registerField("Name");
		registerField("ClusterId");
		registerField("suspenseAc", listheader_SuspenseAc, SortOrder.NONE, suspenseAc, sortOperator_SuspenseAc,
				Operators.STRING);

		doSetFieldProperties();
		// fillList(this.category, listCategory, null);
		// Render the page and display the data.
		doRenderPage();
		search();
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		String id = this.branchOrCluster.getId();

		if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("B")) {
			if (StringUtils.isNotEmpty(id) && !id.equals("branchOrCluster")) {
				this.searchObject.addFilterEqual("BRANCHCODE", this.branchOrCluster.getValue());
			} else {
				this.searchObject.addFilterNotEqual("BRANCHCODE", "");
			}
		} else if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("C")) {
			if (StringUtils.isNotEmpty(id) && !id.equals("branchOrCluster")) {
				this.searchObject.addFilterEqual("CLUSTERID", Long.valueOf(id));
			} else {
				this.searchObject.addFilterNotEqual("CLUSTERID", 0);
			}

		}
	}

	public void doSetFieldProperties() {
		this.partnerBank.setModuleName("PartnerBank");
		this.partnerBank.setValueColumn("PartnerBankCode");
		this.partnerBank.setDescColumn("PartnerBankName");
		this.partnerBank.setValidateColumns(new String[] { "PartnerBankCode" });

		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("FinType");
		this.finType.setDescColumn("FinTypeDesc");
		this.finType.setValidateColumns(new String[] { "FinType" });

		if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("B")) {
			this.branchOrCluster.setModuleName("Branch");
			this.branchOrCluster.setValueColumn("BranchCode");
			this.branchOrCluster.setDescColumn("BranchDesc");
			this.branchOrCluster.setValidateColumns(new String[] { "BranchCode" });
		} else if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("C")) {
			this.branchOrCluster.setModuleName("Cluster");
			this.branchOrCluster.setValueColumn("Code");
			this.branchOrCluster.setDescColumn("Name");
			this.branchOrCluster.setValidateColumns(new String[] { "Code" });
			this.branchOrCluster.setFilters(
					new Filter[] { new Filter("CLUSTERTYPE", PartnerBankExtension.CLUSTER_TYPE, Filter.OP_EQUAL) });
		}

	}

	public void onClick$button_FinTypeParterbankMappingList_NewFinTypeParterbankMapping(Event event) {
		logger.debug(Literal.ENTERING);

		FinTypePartnerBank fintypepartnerbank = new FinTypePartnerBank();
		fintypepartnerbank.setNewRecord(true);
		fintypepartnerbank.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(fintypepartnerbank);

		logger.debug(Literal.LEAVING);
	}

	public void onFinTypePartnerBankMappingItemDoubleClicked(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxFinTypePartnerbankMapping.getSelectedItem();
		FinTypePartnerBank afintypePartnerBank = (FinTypePartnerBank) selectedItem.getAttribute("data");

		afintypePartnerBank = finTypePartnerBankService.getPartnerBank(afintypePartnerBank.getFinType(),
				afintypePartnerBank.getID());

		if (afintypePartnerBank == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuffer whereCond = new StringBuffer();
		whereCond.append("  where  Id =? ");

		if (doCheckAuthority(afintypePartnerBank, whereCond.toString(), new Object[] { afintypePartnerBank.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && afintypePartnerBank.getWorkflowId() == 0) {
				afintypePartnerBank.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(afintypePartnerBank);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialogPage(FinTypePartnerBank fintypepartnerbank) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> map = getDefaultArguments();
		map.put("fintypepartnerbank", fintypepartnerbank);
		map.put("fintypepartnerbankMappingListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/FinanceType/FinTypePartnerbankMappingDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$branchOrCluster(Event event) {
		logger.debug(Literal.ENTERING);

		if (PartnerBankExtension.BRANCH_OR_CLUSTER.equals("C")) {
			Cluster cluster = (Cluster) this.branchOrCluster.getObject();

			if (cluster == null) {
				this.branchOrCluster.setId("");
				this.branchOrCluster.setValue("");
				this.branchOrCluster.setDescription("");
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

	public void onClick$button_FinTypeParterbankMappingList_FinTypeParterbankMappingSearchDialog(Event event) {
		search();
	}

	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	public void onClick$button_FinTypeParterbankMappingList_PrintList(Event event) {
		doPrintResults();
	}

	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	public void setFinTypePartnerBankService(FinTypePartnerBankService finTypePartnerBankService) {
		this.finTypePartnerBankService = finTypePartnerBankService;
	}
}

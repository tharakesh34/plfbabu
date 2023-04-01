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
 * * FileName : OverdueChargeRecoveryListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 11-05-2012 * *
 * Modified Date : 11-05-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 11-05-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.financemanagement.overduechargerecovery;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.financemanagement.OverdueChargeRecoveryService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.financemanagement.overduechargerecovery.model.OverdueChargeRecoveryComparator;
import com.pennant.webui.financemanagement.overduechargerecovery.model.OverdueChargeRecoveryListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/FinanceManagement/OverdueChargeRecovery/OverdueChargeRecoveryList.zul file.
 */
public class OverdueChargeRecoveryListCtrl extends GFCBaseListCtrl<OverdueChargeRecovery> {
	private static final long serialVersionUID = -4562972510077651582L;

	protected Window window_OverdueChargeRecoveryList;
	protected Borderlayout borderLayout_OverdueChargeRecoveryList;
	protected Paging pagingOverdueChargeRecoveryList;
	protected Listbox listBoxOverdueChargeRecovery;
	protected Div div_OverdueChargeRecoveryList;
	protected Tabpanel tabPanel_dialogWindow;
	protected Listheader listheader_FinSchdDate;
	protected Listheader listheader_FinODDate;
	protected Listheader listheader_FinODPri;
	protected Listheader listheader_FinODPft;
	protected Listheader listheader_FinODTot;
	protected Textbox chargeAmtPer_header;
	protected Listheader listheader_FinODCPenalty;
	protected Listheader listheader_FinODCWaived;
	protected Listheader listheader_FinODCPLPenalty;
	protected Listheader listheader_FinODCCPenalty;
	protected Listheader listheader_FinODCRecoverySts;
	protected Datebox finSchdDate;
	protected Listbox sortOperator_finSchdDate;
	protected Datebox finODDate;
	protected Listbox sortOperator_finODDate;
	protected Decimalbox finODPrinciple;
	protected Listbox sortOperator_finODPrincpl;
	protected Decimalbox finODProfit;
	protected Listbox sortOperator_finODProfit;
	protected Decimalbox finODTotal;
	protected Listbox sortOperator_finODTotal;
	protected Decimalbox finODTotalCharge;
	protected Listbox sortOperator_finODTotalCharge;
	protected Decimalbox finODWaived;
	protected Listbox sortOperator_finODWaived;
	protected Combobox finODSts;
	protected Listbox sortOperator_finODSts;
	protected Textbox moduleType;
	protected Button button_OverdueChargeRecoveryList_OverdueChargeRecoverySearchDialog;
	protected Button button_OverdueChargeRecoveryList_PrintList;
	protected Button btnHelp;
	protected Button btnRefresh;
	protected Button btnClose;
	protected Textbox recoveryCode;
	protected Grid listGrid;
	protected Combobox penaltyType;
	protected Combobox chargeCalculatedOn;
	protected Decimalbox chargeAmtOrPer;

	private String finReference = "";
	private Long finID;
	private List<ValueLabel> listODRecoveryStatus = PennantStaticListUtil.getODCRecoveryStatus();

	protected JdbcSearchObject<OverdueChargeRecovery> searchObj;
	protected JdbcSearchObject<OverdueChargeRecovery> detailSearchObject;

	private transient OverdueChargeRecoveryService overdueChargeRecoveryService;
	private FinODPenaltyRateDAO finODPenaltyRateDAO;
	private ReceiptService receiptService;

	public OverdueChargeRecoveryListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		moduleCode = "OverdueChargeRecovery";
	}

	public void onCreate$window_OverdueChargeRecoveryList(ForwardEvent event) {
		logger.debug("Entering" + event.toString());

		if (event.getTarget().getParent().getParent() != null) {
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}

		// READ OVERHANDED parameters !
		if (arguments.containsKey("finReference")) {
			this.finReference = (String) arguments.get("finReference");
		}

		if (arguments.containsKey("finID")) {
			this.finID = (Long) arguments.get("finID");
		}

		int ccyFormatter = 0;
		if (arguments.containsKey("ccyFormatter")) {
			ccyFormatter = (Integer) arguments.get("ccyFormatter");
		}

		// DropDown ListBox
		if ("N".equals(this.recoveryCode.getValue())) {
			this.sortOperator_finSchdDate.setModel(new ListModelList<>(new SearchOperators().getNumericOperators()));
			this.sortOperator_finSchdDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

			this.sortOperator_finODDate.setModel(new ListModelList<>(new SearchOperators().getNumericOperators()));
			this.sortOperator_finODDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

			this.sortOperator_finODPrincpl.setModel(new ListModelList<>(new SearchOperators().getNumericOperators()));
			this.sortOperator_finODPrincpl.setItemRenderer(new SearchOperatorListModelItemRenderer());

			this.sortOperator_finODProfit.setModel(new ListModelList<>(new SearchOperators().getNumericOperators()));
			this.sortOperator_finODProfit.setItemRenderer(new SearchOperatorListModelItemRenderer());

			this.sortOperator_finODTotal.setModel(new ListModelList<>(new SearchOperators().getNumericOperators()));
			this.sortOperator_finODTotal.setItemRenderer(new SearchOperatorListModelItemRenderer());

			this.sortOperator_finODTotalCharge
					.setModel(new ListModelList<>(new SearchOperators().getNumericOperators()));
			this.sortOperator_finODTotalCharge.setItemRenderer(new SearchOperatorListModelItemRenderer());

			this.sortOperator_finODWaived.setModel(new ListModelList<>(new SearchOperators().getNumericOperators()));
			this.sortOperator_finODWaived.setItemRenderer(new SearchOperatorListModelItemRenderer());

			this.sortOperator_finODSts.setModel(new ListModelList<>(new SearchOperators().getBooleanOperators()));
			this.sortOperator_finODSts.setItemRenderer(new SearchOperatorListModelItemRenderer());
		} else {
			if (this.listGrid != null) {
				doFillHeaders(ccyFormatter);
			}
		}

		/* set components visible dependent on the users rights */
		doCheckRights();

		// set the paging parameters
		this.listBoxOverdueChargeRecovery.setHeight(getListBoxHeight(6));
		this.borderLayout_OverdueChargeRecoveryList.setVflex("min");

		if ("N".equals(this.recoveryCode.getValue())) {
			// this.pagingOverdueChargeRecoveryList.setPageSize(getListRows());
			this.pagingOverdueChargeRecoveryList.setDetailed(true);

			this.listheader_FinSchdDate.setSortAscending(new FieldComparator("finSchdDate", true));
			this.listheader_FinSchdDate.setSortDescending(new FieldComparator("finSchdDate", false));
			this.listheader_FinODDate.setSortAscending(new FieldComparator("finODDate", true));
			this.listheader_FinODDate.setSortDescending(new FieldComparator("finODDate", false));
			this.listheader_FinODPri.setSortAscending(new FieldComparator("finODPri", true));
			this.listheader_FinODPri.setSortDescending(new FieldComparator("finODPri", false));
			this.listheader_FinODPft.setSortAscending(new FieldComparator("finODPft", true));
			this.listheader_FinODPft.setSortDescending(new FieldComparator("finODPft", false));
			this.listheader_FinODTot.setSortAscending(new FieldComparator("finODTot", true));
			this.listheader_FinODTot.setSortDescending(new FieldComparator("finODTot", false));
			this.listheader_FinODCPenalty.setSortAscending(new FieldComparator("finODCPenalty", true));
			this.listheader_FinODCPenalty.setSortDescending(new FieldComparator("finODCPenalty", false));
			this.listheader_FinODCWaived.setSortAscending(new FieldComparator("finODCWaived", true));
			this.listheader_FinODCWaived.setSortDescending(new FieldComparator("finODCWaived", false));
			this.listheader_FinODCPLPenalty.setSortAscending(new FieldComparator("finODCPLPenalty", true));
			this.listheader_FinODCPLPenalty.setSortDescending(new FieldComparator("finODCPLPenalty", false));
			this.listheader_FinODCCPenalty.setSortAscending(new FieldComparator("finODCCPenalty", true));
			this.listheader_FinODCCPenalty.setSortDescending(new FieldComparator("finODCCPenalty", false));
			this.listheader_FinODCRecoverySts.setSortAscending(new FieldComparator("finODCRecoverySts", true));
			this.listheader_FinODCRecoverySts.setSortDescending(new FieldComparator("finODCRecoverySts", false));
		}

		this.listBoxOverdueChargeRecovery.setItemRenderer(new OverdueChargeRecoveryListModelItemRenderer());

		if ("Y".equals(this.recoveryCode.getValue())) {
			findSearchObject();
		} else {
			doSearch();
			if (this.workFlowFrom != null && !isWorkFlowEnabled()) {
				this.workFlowFrom.setVisible(false);
				this.fromApproved.setSelected(true);
			}
		}

		if ("Y".equals(this.recoveryCode.getValue())) {
			this.div_OverdueChargeRecoveryList.setVisible(false);
		}

		logger.debug("Leaving");
	}

	private void doFillHeaders(int ccyFormatter) {
		FinODPenaltyRate rate = null;

		if (arguments.containsKey("PenaltyRate")) {
			rate = (FinODPenaltyRate) arguments.get("PenaltyRate");
		}

		if (rate == null) {
			rate = finODPenaltyRateDAO.getEffectivePenaltyRate(finID, "_AVIEW");
		}

		List<ValueLabel> odcChargeType = PennantStaticListUtil.getODCChargeType();
		List<ValueLabel> odcCalculatedOn = PennantStaticListUtil.getODCCalculatedOn();

		fillComboBox(this.penaltyType, rate.getODChargeType(), odcChargeType, "");
		fillComboBox(this.chargeCalculatedOn, rate.getODChargeCalOn(), odcCalculatedOn, "");
		this.chargeAmtOrPer.setValue(PennantApplicationUtil.formateAmount(rate.getODChargeAmtOrPerc(), ccyFormatter));

		this.penaltyType.setDisabled(true);
		this.chargeCalculatedOn.setDisabled(true);
		this.chargeAmtOrPer.setReadonly(true);
	}

	/**
	 * Internal Method for Grouping List items
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void findSearchObject() {
		logger.debug("Entering");

		List<OverdueChargeRecovery> odcrList = receiptService.prepareODCRecovery(finID);
		this.listBoxOverdueChargeRecovery
				.setModel(new GroupsModelArray(odcrList.toArray(), new OverdueChargeRecoveryComparator()));

		logger.debug("Leaving");
	}

	/**
	 * This method sets all rightsTypes as ComboItems for ComboBox
	 */
	private void setODrecoveryStatus() {
		logger.debug("Entering ");
		Comboitem comboitem;
		for (int i = 0; i < listODRecoveryStatus.size(); i++) {
			comboitem = new Comboitem();
			comboitem.setLabel(listODRecoveryStatus.get(i).getLabel());
			comboitem.setValue(listODRecoveryStatus.get(i).getValue());
			this.finODSts.appendChild(comboitem);
		}
		logger.debug("Leaving ");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("OverdueChargeRecoveryList");
		this.button_OverdueChargeRecoveryList_OverdueChargeRecoverySearchDialog.setVisible(
				getUserWorkspace().isAllowed("button_OverdueChargeRecoveryList_OverdueChargeRecoveryFindDialog"));
		this.button_OverdueChargeRecoveryList_PrintList
				.setVisible(getUserWorkspace().isAllowed("button_OverdueChargeRecoveryList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see:
	 * com.pennant.webui.financemanagement.overduechargerecovery.model.OverdueChargeRecoveryListModelItemRenderer.java
	 * <br>
	 * 
	 * @param event
	 */
	public void onOverdueChargeRecoveryItemDoubleClicked(Event event) {
		logger.debug(event.toString());

		// get the selected OverdueChargeRecovery object
		final Listitem item = this.listBoxOverdueChargeRecovery.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final OverdueChargeRecovery aOverdueChargeRecovery = (OverdueChargeRecovery) item.getAttribute("data");
			final OverdueChargeRecovery overdueChargeRecovery = getOverdueChargeRecoveryService()
					.getOverdueChargeRecoveryById(aOverdueChargeRecovery.getFinID(),
							aOverdueChargeRecovery.getFinODSchdDate(), aOverdueChargeRecovery.getFinODFor());

			if (overdueChargeRecovery == null) {
				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = aOverdueChargeRecovery.getId();
				errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm),
						getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			} else {
				if (isWorkFlowEnabled()) {
					String whereCond = " AND FinReference='" + overdueChargeRecovery.getFinReference()
							+ "' AND version=" + overdueChargeRecovery.getVersion() + " ";

					boolean userAcces = validateUserAccess(overdueChargeRecovery.getWorkflowId(),
							getUserWorkspace().getLoggedInUser().getUserId(), "OverdueChargeRecovery", whereCond,
							overdueChargeRecovery.getTaskId(), overdueChargeRecovery.getNextTaskId());
					if (userAcces) {
						showDetailView(overdueChargeRecovery);
					} else {
						MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(overdueChargeRecovery);
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the OverdueChargeRecovery dialog with a new empty entry. <br>
	 */
	public void onClick$button_OverdueChargeRecoveryList_NewOverdueChargeRecovery(Event event) {
		logger.debug(event.toString());
		// create a new OverdueChargeRecovery object, We GET it from the backend.
		final OverdueChargeRecovery aOverdueChargeRecovery = getOverdueChargeRecoveryService()
				.getNewOverdueChargeRecovery();
		showDetailView(aOverdueChargeRecovery);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param OverdueChargeRecovery (aOverdueChargeRecovery)
	 */
	private void showDetailView(OverdueChargeRecovery aOverdueChargeRecovery) {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them with a object of the selected item. For
		 * handed over these parameter only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aOverdueChargeRecovery.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aOverdueChargeRecovery.setWorkflowId(getWorkFlowId());
		}

		Map<String, Object> map = getDefaultArguments();
		map.put("overdueChargeRecovery", aOverdueChargeRecovery);
		if ("Y".equals(this.recoveryCode.getValue())) {
			map.put("inquiry", true);
		}

		/*
		 * we can additionally handed over the listBox or the controller self, so we have in the dialog access to the
		 * listbox Listmodel. This is fine for synchronizing the data in the OverdueChargeRecoveryListbox from the
		 * dialog when we do a delete, edit or insert a OverdueChargeRecovery.
		 */
		map.put("overdueChargeRecoveryListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/FinanceManagement/OverdueChargeRecovery/OverdueChargeRecoveryDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		MessageUtil.showHelpWindow(event, window_OverdueChargeRecoveryList);
		logger.debug("Leaving");
	}

	/**
	 * when the "refresh" button is clicked. <br>
	 * <br>
	 * Refreshes the view by calling the onCreate event manually.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException {
		logger.debug(event.toString());
		this.sortOperator_finODDate.setSelectedIndex(0);
		this.finODDate.setValue(null);
		this.sortOperator_finODPrincpl.setSelectedIndex(0);
		this.finODPrinciple.setText("");
		this.sortOperator_finODProfit.setSelectedIndex(0);
		this.finODProfit.setText("");
		this.sortOperator_finODSts.setSelectedIndex(0);
		this.finODSts.setValue("");
		this.sortOperator_finODTotal.setSelectedIndex(0);
		this.finODTotal.setText("");
		this.sortOperator_finODTotalCharge.setSelectedIndex(0);
		this.finODTotalCharge.setText("");
		this.sortOperator_finODWaived.setSelectedIndex(0);
		this.finODWaived.setText("");
		this.sortOperator_finSchdDate.setSelectedIndex(0);
		this.finSchdDate.setValue(null);
		doSearch();
		logger.debug("Leaving");
	}

	/**
	 * Method for call the OverdueChargeRecovery dialog
	 * 
	 * @param event
	 */
	public void onClick$button_OverdueChargeRecoveryList_OverdueChargeRecoverySearchDialog(Event event) {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the overdueChargeRecovery print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_OverdueChargeRecoveryList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("OverdueChargeRecovery", getSearchObj(),
				this.pagingOverdueChargeRecoveryList.getTotalSize() + 1);
		logger.debug("Leaving");
	}

	public void doSearch() {
		logger.debug("Entering");
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<OverdueChargeRecovery>(OverdueChargeRecovery.class, getListRows());

		// Defualt Sort on the table
		this.searchObj.addSort("FinReference", false);

		boolean approvedList = false;

		// Workflow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("FinODCRecovery_View");

			if (this.moduleType == null) {
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
				approvedList = false;

			} else {
				if (this.fromApproved.isSelected()) {
					approvedList = true;
				} else {
					approvedList = false;
				}
			}
		} else {
			approvedList = true;
		}
		if (approvedList) {
			this.searchObj.addTabelName("FinODCRecovery_AView");
		} else {
			this.searchObj.addTabelName("FinODCRecovery_View");
		}

		if ("N".equals(this.recoveryCode.getValue())) {

			setODrecoveryStatus();

			// Scheduled Date
			if (this.finSchdDate.getValue() != null) {
				// searchObj = getSearchFilter(searchObj,this.sortOperator_finSchdDate.getSelectedItem(),
				// this.finSchdDate.getValue() , "finSchdDate");

				searchObj.addFilter(new Filter("finSchdDate",
						DateUtil.format(this.finSchdDate.getValue(), PennantConstants.DBDateFormat),
						Filter.OP_EQUAL));
			}
			// Overdue Date
			if (this.finODDate.getValue() != null) {
				// searchObj = getSearchFilter(searchObj,this.sortOperator_finODDate.getSelectedItem(),
				// this.finODDate.getValue() , "finODDate");

				searchObj.addFilter(new Filter("finODDate",
						DateUtil.format(this.finODDate.getValue(), PennantConstants.DBDateFormat), Filter.OP_EQUAL));
			}
			// Overdue Principle
			if (this.finODPrinciple.getValue() != null) {
				searchObj = getSearchFilter(searchObj, this.sortOperator_finODPrincpl.getSelectedItem(),
						this.finODPrinciple.getValue(), "finODPri");
			}
			// Overdue Profit
			if (this.finODProfit.getValue() != null) {
				searchObj = getSearchFilter(searchObj, this.sortOperator_finODProfit.getSelectedItem(),
						this.finODProfit.getValue(), "finODPft");
			}
			// Overdue Total
			if (this.finODTotal.getValue() != null) {
				searchObj = getSearchFilter(searchObj, this.sortOperator_finODTotal.getSelectedItem(),
						this.finODTotal.getValue(), "finODTot");
			}
			// Overdue Waived
			if (this.finODWaived.getValue() != null) {
				searchObj = getSearchFilter(searchObj, this.sortOperator_finODWaived.getSelectedItem(),
						this.finODWaived.getValue(), "finODCWaiverPaid");
			}

			// Overdue recovery Status
			if (null != this.finODSts.getSelectedItem()
					&& StringUtils.isNotBlank(this.finODSts.getSelectedItem().getValue().toString())) {
				searchObj = getSearchFilter(searchObj, this.sortOperator_finODSts.getSelectedItem(),
						this.finODSts.getSelectedItem().getValue().toString(), "finODCRecoverySts");
			}

			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj, this.listBoxOverdueChargeRecovery,
					this.pagingOverdueChargeRecoveryList);
		} else {
			this.searchObj.addFilter(new Filter("FinReference", this.finReference, Filter.OP_EQUAL));
		}
		logger.debug("Leaving");

	}

	public OverdueChargeRecoveryService getOverdueChargeRecoveryService() {
		return this.overdueChargeRecoveryService;
	}

	public JdbcSearchObject<OverdueChargeRecovery> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<OverdueChargeRecovery> searchObj) {
		this.searchObj = searchObj;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	@Autowired
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	@Autowired
	public void setOverdueChargeRecoveryService(OverdueChargeRecoveryService overdueChargeRecoveryService) {
		this.overdueChargeRecoveryService = overdueChargeRecoveryService;
	}

	@Autowired
	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

	@Autowired
	public void setReceiptService(ReceiptService receiptService) {
		this.receiptService = receiptService;
	}

}
package com.pennant.webui.financemanagement.latepayprofit;

import java.util.ArrayList;
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
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.core.LatePayInterestService;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.financemanagement.OverdueChargeRecoveryService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.finance.enquiry.FinanceEnquiryHeaderDialogCtrl;
import com.pennant.webui.financemanagement.overduechargerecovery.model.OverdueChargeRecoveryComparator;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class LatepayProfitRecoveryListCtrl extends GFCBaseListCtrl<OverdueChargeRecovery> {
	private static final long serialVersionUID = 5175151702906234829L;

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_LatepayProfitRecoveryList; // autowired
	protected Borderlayout borderLayout_LatepayProfitRecoveryList; // autowired
	protected Paging pagingLatepayProfitRecoveryList; // autowired
	protected Listbox listBoxLatepayProfitRecovery; // autowired
	protected Div div_LatepayProfitRecoveryList; // autowired
	private Tabpanel tabPanel_dialogWindow;

	// List headers
	protected Listheader listheader_FinSchdDate; // autowired
	protected Listheader listheader_FinODDate; // autowired
	protected Listheader listheader_FinODPri; // autowired
	protected Listheader listheader_FinODPft; // autowired
	protected Listheader listheader_FinODTot; // autowired
	protected Listheader listheader_FinODCPenalty; // autowired
	protected Listheader listheader_FinODCWaived; // autowired
	protected Listheader listheader_FinODCPLPenalty; // autowired
	protected Listheader listheader_FinODCCPenalty; // autowired
	protected Listheader listheader_FinODCRecoverySts; // autowired

	// Filtering Fields

	protected Datebox finSchdDate; // autowired
	protected Listbox sortOperator_finSchdDate; // autowired
	protected Datebox finODDate; // autowired
	protected Listbox sortOperator_finODDate; // autowired
	protected Decimalbox finODPrinciple; // autowired
	protected Listbox sortOperator_finODPrincpl; // autowired
	protected Decimalbox finODProfit; // autowired
	protected Listbox sortOperator_finODProfit; // autowired
	protected Decimalbox finODTotal; // autowired
	protected Listbox sortOperator_finODTotal; // autowired
	protected Decimalbox finODTotalCharge; // autowired
	protected Listbox sortOperator_finODTotalCharge; // autowired
	protected Decimalbox finODWaived; // autowired
	protected Listbox sortOperator_finODWaived; // autowired
	protected Combobox finODSts; // autowired
	protected Listbox sortOperator_finODSts; // autowired

	private Grid searchGrid; // autowired
	protected Textbox moduleType; // autowired
	protected Radio fromApproved;
	protected Radio fromWorkFlow;
	protected Row workFlowFrom;

	private transient boolean approvedList = false;

	// checkRights
	protected Button button_LatepayProfitRecoveryList_LatepayProfitRecoverySearchDialog; // autowired
	protected Button button_LatepayProfitRecoveryList_PrintList; // autowired
	protected Button btnHelp; // autowired
	protected Button btnRefresh; // autowired

	// NEEDED for the ReUse in the SearchWindow
	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	protected JdbcSearchObject<OverdueChargeRecovery> searchObj;
	protected JdbcSearchObject<OverdueChargeRecovery> detailSearchObject;
	private transient PagedListService pagedListService;
	private transient OverdueChargeRecoveryService overdueChargeRecoveryService;
	private List<ValueLabel> listODRecoveryStatus = PennantStaticListUtil.getODCRecoveryStatus();

	private Textbox recoveryCode;
	private String finReference = "";
	private long finID;
	// private int ccyFormatter = 0;

	@Autowired
	private FinODDetailsDAO finODDetailsDAO;
	@Autowired
	private FinanceMainDAO financeMainDAO;
	@Autowired
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	@Autowired
	private LatePayInterestService latePayInterestService;

	/**
	 * default constructor.<br>
	 */
	public LatepayProfitRecoveryListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		moduleCode = "OverdueChargeRecovery";
	}

	/**
	 * Before binding the data and calling the List window we check, if the ZUL-file is called with a parameter for a
	 * selected OverdueChargeRecovery object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void onCreate$window_LatepayProfitRecoveryList(ForwardEvent event) {
		logger.debug("Entering");

		if (event.getTarget().getParent().getParent() != null) {
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}

		if (arguments.containsKey("finReference")) {
			this.finReference = (String) arguments.get("finReference");
		}

		if (arguments.containsKey("finID")) {
			this.finID = (Long) arguments.get("finID");
		}
		/*
		 * if (args.containsKey("ccyFormatter")) { this.ccyFormatter = (Integer) args.get("ccyFormatter"); }
		 */
		if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
			this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments
					.get("financeEnquiryHeaderDialogCtrl");
		}

		// DropDown ListBox
		if ("N".equals(this.recoveryCode.getValue())) {
			this.sortOperator_finSchdDate
					.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
			this.sortOperator_finSchdDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

			this.sortOperator_finODDate
					.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
			this.sortOperator_finODDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

			this.sortOperator_finODPrincpl
					.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
			this.sortOperator_finODPrincpl.setItemRenderer(new SearchOperatorListModelItemRenderer());

			this.sortOperator_finODProfit
					.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
			this.sortOperator_finODProfit.setItemRenderer(new SearchOperatorListModelItemRenderer());

			this.sortOperator_finODTotal
					.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
			this.sortOperator_finODTotal.setItemRenderer(new SearchOperatorListModelItemRenderer());

			this.sortOperator_finODTotalCharge
					.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
			this.sortOperator_finODTotalCharge.setItemRenderer(new SearchOperatorListModelItemRenderer());

			this.sortOperator_finODWaived
					.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
			this.sortOperator_finODWaived.setItemRenderer(new SearchOperatorListModelItemRenderer());

			this.sortOperator_finODSts
					.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
			this.sortOperator_finODSts.setItemRenderer(new SearchOperatorListModelItemRenderer());
		}

		/* set components visible dependent on the users rights */
		doCheckRights();

		// set the paging parameters
		this.borderLayout_LatepayProfitRecoveryList.setHeight(getBorderLayoutHeight());
		if (this.recoveryCode.getValue().equals("N")) {
			this.listBoxLatepayProfitRecovery.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));
			this.pagingLatepayProfitRecoveryList.setPageSize(getListRows());
			this.pagingLatepayProfitRecoveryList.setDetailed(true);

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

			// set the itemRenderer
			this.listBoxLatepayProfitRecovery.setItemRenderer(new LatepayProfitRecoveryListModelItemRenderer());

		} else {
			this.listBoxLatepayProfitRecovery.setHeight(getListBoxHeight(4));

			// set the itemRenderer
			this.listBoxLatepayProfitRecovery.setItemRenderer(new LatepayProfitRecoveryListModelItemRenderer());
		}

		if (this.recoveryCode.getValue().equals("Y")) {
			// Set the ListModel for the articles.
			findSearchObject();
		} else {
			doSearch();
			if (this.workFlowFrom != null && !isWorkFlowEnabled()) {
				this.workFlowFrom.setVisible(false);
				this.fromApproved.setSelected(true);
			}
		}

		if (this.recoveryCode.getValue().equals("Y")) {
			this.div_LatepayProfitRecoveryList.setVisible(false);
			if (tabPanel_dialogWindow != null) {
				getBorderLayoutHeight();
				int rowsHeight = this.financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount()
						* 20;
				listBoxLatepayProfitRecovery.setHeight(borderLayoutHeight - rowsHeight - 95 + "px");
				this.window_LatepayProfitRecoveryList.setHeight(this.borderLayoutHeight - rowsHeight - 55 + "px");
				tabPanel_dialogWindow.appendChild(this.window_LatepayProfitRecoveryList);
			}

		}

		List<OverdueChargeRecovery> lpiListTodisplay = new ArrayList<OverdueChargeRecovery>();

		FinanceMain finMian = financeMainDAO.getFinanceMainById(finID, "_View", false);
		if ((PennantConstants.YES.equals(this.recoveryCode.getValue())
				&& StringUtils.equals(CalculationConstants.PDPFTCAL_NOTAPP, finMian.getPastduePftCalMthd()))) {
			// If the enquiry type as a Interest over due enquiry, and past due
			// calc method as a NotApplicable then no need show the enquiry.
		} else {
			List<FinODDetails> list = finODDetailsDAO.getFinODBalByFinRef(finID);
			List<FinanceScheduleDetail> schlist = financeScheduleDetailDAO.getFinSchdDetailsForBatch(finID);
			for (FinODDetails fod : list) {
				List<OverdueChargeRecovery> lpiList = latePayInterestService.computeLPI(fod, SysParamUtil.getAppDate(),
						finMian, schlist, null);
				lpiListTodisplay.addAll(lpiList);
			}
		}
		this.listBoxLatepayProfitRecovery
				.setModel(new GroupsModelArray(lpiListTodisplay.toArray(), new OverdueChargeRecoveryComparator()));

		logger.debug("Leaving");
	}

	/**
	 * Internal Method for Grouping List items
	 */
	public void findSearchObject() {
		logger.debug("Entering");

		// ++ create the searchObject and init sorting ++//
		this.detailSearchObject = new JdbcSearchObject<OverdueChargeRecovery>(OverdueChargeRecovery.class);
		this.detailSearchObject.addTabelName("FinODCRecovery_View");
		this.detailSearchObject.addFilter(new Filter("FinReference", this.finReference, Filter.OP_EQUAL));
		this.detailSearchObject
				.addFilter(new Filter("FinODFor", FinanceConstants.SCH_TYPE_LATEPAYPROFIT, Filter.OP_EQUAL));

		// Defualt Sort on the table
		this.detailSearchObject.addSort("FinReference", false);

		// this.listBoxLatepayProfitRecovery.setModel(new GroupsModelArray(
		// getPagedListService().getBySearchObject(detailSearchObject).toArray(),new
		// OverdueChargeRecoveryComparator()));
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
		this.button_LatepayProfitRecoveryList_LatepayProfitRecoverySearchDialog.setVisible(
				getUserWorkspace().isAllowed("button_OverdueChargeRecoveryList_OverdueChargeRecoveryFindDialog"));
		this.button_LatepayProfitRecoveryList_PrintList
				.setVisible(getUserWorkspace().isAllowed("button_OverdueChargeRecoveryList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see:
	 * com.pennant.webui.financemanagement.overduechargerecovery.model.LatepayProfitRecoveryListModelItemRenderer.java
	 * <br>
	 * 
	 * @param event
	 */
	public void onOverdueChargeRecoveryItemDoubleClicked(Event event) {
		logger.debug(event.toString());

		// get the selected OverdueChargeRecovery object
		final Listitem item = this.listBoxLatepayProfitRecovery.getSelectedItem();

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
		// create a new OverdueChargeRecovery object, We GET it from the
		// backend.
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
		if (this.recoveryCode.getValue().equals("Y")) {
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
		MessageUtil.showHelpWindow(event, window_LatepayProfitRecoveryList);
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
				this.pagingLatepayProfitRecoveryList.getTotalSize() + 1);
		logger.debug("Leaving");
	}

	public void doSearch() {
		logger.debug("Entering");
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<OverdueChargeRecovery>(OverdueChargeRecovery.class, getListRows());

		// Defualt Sort on the table
		this.searchObj.addSort("FinReference", false);

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
				// searchObj =
				// getSearchFilter(searchObj,this.sortOperator_finSchdDate.getSelectedItem(),
				// this.finSchdDate.getValue() , "finSchdDate");

				searchObj.addFilter(new Filter("finSchdDate",
						DateUtil.format(this.finSchdDate.getValue(), PennantConstants.DBDateFormat),
						Filter.OP_EQUAL));
			}
			// Overdue Date
			if (this.finODDate.getValue() != null) {
				// searchObj =
				// getSearchFilter(searchObj,this.sortOperator_finODDate.getSelectedItem(),
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
					&& !StringUtils.trimToEmpty(this.finODSts.getSelectedItem().getValue().toString()).equals("")) {
				searchObj = getSearchFilter(searchObj, this.sortOperator_finODSts.getSelectedItem(),
						this.finODSts.getSelectedItem().getValue().toString(), "finODCRecoverySts");
			}

			// Set the ListModel for the articles.
			// getPagedListWrapper().init(this.searchObj,
			// this.listBoxLatepayProfitRecovery,this.pagingLatepayProfitRecoveryList);
		} else {
			this.searchObj.addFilter(new Filter("FinReference", this.finReference, Filter.OP_EQUAL));
		}
		logger.debug("Leaving");

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setOverdueChargeRecoveryService(OverdueChargeRecoveryService overdueChargeRecoveryService) {
		this.overdueChargeRecoveryService = overdueChargeRecoveryService;
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

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
}

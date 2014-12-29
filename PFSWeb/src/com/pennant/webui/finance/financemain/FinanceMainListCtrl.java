/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  FinanceMainListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-11-2011    														*
 *                                                                  						*
 * Modified Date    :  15-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-11-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.webui.finance.financemain;


import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.search.Filter;
import com.pennant.webui.finance.financemain.model.FinanceMainListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/FinanceMainList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class FinanceMainListCtrl extends GFCBaseListCtrl<FinanceMain> implements Serializable {


	private static final long serialVersionUID = -5901195042041627750L;
	private final static Logger logger = Logger.getLogger(FinanceMainListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window       window_FinanceMainList;                    // autoWired
	protected Borderlayout borderLayout_FinanceMainList;              // autoWired
	protected Paging       pagingFinanceMainList;                     // autoWired
	protected Listbox      listBoxFinanceMain;                        // autoWired

	protected Textbox finReference; 					// autowired
	protected Listbox sortOperator_finReference; 		// autowired
	protected Textbox finType; 							// autowired
	protected Listbox sortOperator_finType; 			// autowired
	protected Textbox finCcy; 							// autowired
	protected Listbox sortOperator_finCcy; 				// autowired
	protected Textbox scheduleMethod; 					// autowired
	protected Listbox sortOperator_scheduleMethod; 		// autowired
	protected Textbox profitDaysBasis; 					// autowired
	protected Listbox sortOperator_profitDaysBasis; 	// autowired
	protected Datebox finStartDate; 					// autowired
	protected Listbox sortOperator_finStartDate; 		// autowired
	protected Decimalbox finAmount; 					// autowired
	protected Listbox sortOperator_finAmount; 			// autowired
	protected Textbox custID; 							// autowired
	protected Listbox sortOperator_custID; 				// autowired
	protected Checkbox finIsActive; 					// autowired
	protected Listbox sortOperator_finIsActive; 		// autowired
	protected Textbox recordStatus; 					// autowired
	protected Listbox recordType;						// autowired
	protected Listbox sortOperator_recordStatus; 		// autowired
	protected Listbox sortOperator_recordType; 			// autowired

	// List headers
	protected Listheader listheader_CustomerCIF;                      // autoWired
	protected Listheader listheader_CustomerName;                      // autoWired
	protected Listheader listheader_FinReference;                     // autoWired
	protected Listheader listheader_ProductName;                     // autoWired
	protected Listheader listheader_FinType;                          // autoWired
	protected Listheader listheader_FinCcy;                           // autoWired
	protected Listheader listheader_ScheduleMethod;                   // autoWired
	protected Listheader listheader_FinAmount;                   	  // autoWired
	protected Listheader listheader_FinancingAmount;                  // autoWired
	protected Listheader listheader_RecordStatus;                     // autoWired
	protected Listheader listheader_RecordType;                       // autoWired

	// checkRights
	protected Button btnHelp;                                         // autoWired
	protected Button button_FinanceMainList_NewFinanceMain;           // autoWired
	protected Button button_FinanceMainList_FinanceMainSearchDialog;  // autoWired
	protected Button button_FinanceMainList_PrintList;                // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<FinanceMain> searchObj;
	protected Row row_AlwWorkflow;
	protected Grid searchGrid;

	private transient FinanceDetailService financeDetailService;
	private transient WorkFlowDetails workFlowDetails=null;

	private Textbox loanType;//Field for Maintain Different Finance Product Types
	private String menuItemRightName = null;

	/**
	 * default constructor.<br>
	 */
	public FinanceMainListCtrl() {
		super();
	}

	public void onCreate$window_FinanceMainList(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		//Getting Menu Item Right Name
		if (event.getTarget() != null && event.getTarget().getParent() != null
				&& event.getTarget().getParent().getParent()!=null && 
				event.getTarget().getParent().getParent().getParent() != null && 
				event.getTarget().getParent().getParent().getParent().getParent() != null) {

			String menuItemName = ((Tabbox)event.getTarget().getParent().getParent().getParent()).getSelectedTab().getId();
			menuItemName = menuItemName.trim().replace("tab_", "menu_Item_");

			if(getUserWorkspace().getHasMenuRights().containsKey(menuItemName)){
				menuItemRightName = getUserWorkspace().getHasMenuRights().get(menuItemName);
			}
		}

		this.sortOperator_finReference.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finReference.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finCcy.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finCcy.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_scheduleMethod.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_scheduleMethod.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_profitDaysBasis.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_profitDaysBasis.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finStartDate.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_finStartDate.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finAmount.setModel(new ListModelList<SearchOperators>(new SearchOperators().getNumericOperators()));
		this.sortOperator_finAmount.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_custID.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_custID.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_finIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=setRecordType(this.recordType);
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}else{
			this.row_AlwWorkflow.setVisible(false);
		}

		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_FinanceMainList.setHeight(getBorderLayoutHeight());
		this.listBoxFinanceMain.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingFinanceMainList.setPageSize(getListRows());
		this.pagingFinanceMainList.setDetailed(true);

		this.listheader_CustomerCIF.setSortAscending(new FieldComparator("lovDescCustCIF", true));
		this.listheader_CustomerCIF.setSortDescending(new FieldComparator("lovDescCustCIF", false));
		this.listheader_CustomerName.setSortDescending(new FieldComparator("LovDescCustShrtName", false));
		this.listheader_CustomerName.setSortAscending(new FieldComparator("LovDescCustShrtName", true));
		this.listheader_FinReference.setSortAscending(new FieldComparator("finReference", true));
		this.listheader_FinReference.setSortDescending(new FieldComparator("finReference", false));
		this.listheader_ProductName.setSortAscending(new FieldComparator("lovDescProductCodeName", true));
		this.listheader_ProductName.setSortDescending(new FieldComparator("lovDescProductCodeName", false));
		this.listheader_FinType.setSortAscending(new FieldComparator("finType", true));
		this.listheader_FinType.setSortDescending(new FieldComparator("finType", false));
		this.listheader_FinCcy.setSortAscending(new FieldComparator("finCcy", true));
		this.listheader_FinCcy.setSortDescending(new FieldComparator("finCcy", false));
		this.listheader_FinAmount.setSortAscending(new FieldComparator("finAmount", true));
		this.listheader_FinAmount.setSortDescending(new FieldComparator("finAmount", false));
		/*		this.listheader_FinancingAmount.setSortAscending(new FieldComparator("lovDescFinancingAmount", true));
		this.listheader_FinancingAmount.setSortDescending(new FieldComparator("lovDescFinancingAmount", false));*/		
		this.listheader_ScheduleMethod.setSortAscending(new FieldComparator("scheduleMethod", true));
		this.listheader_ScheduleMethod.setSortDescending(new FieldComparator("scheduleMethod", false));

		this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
		this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
		this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
		this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));

		// ++ create the searchObject and initial sorting ++//
		this.searchObj = new JdbcSearchObject<FinanceMain>(FinanceMain.class,getListRows());
		this.searchObj.addSort("FinReference", false);

		//Field Declarations for Fetching List Data
		this.searchObj.addField("FinReference");
		this.searchObj.addField("FinType");
		this.searchObj.addField("FinCcy");
		this.searchObj.addField("ScheduleMethod");
		this.searchObj.addField("FinAmount");
		this.searchObj.addField("DownPayment");
		this.searchObj.addField("FeeChargeAmt");
		this.searchObj.addField("LovDescCustCIF");
		this.searchObj.addField("LovDescCustShrtName");
		this.searchObj.addField("LovDescProductCodeName");
		this.searchObj.addField("LovDescFinFormatter");
		this.searchObj.addField("RecordStatus");
		this.searchObj.addField("RecordType");

		/*	if(!StringUtils.trimToEmpty(this.loanType.getValue()).equals("")){
			this.searchObj.addFilter(new Filter("lovDescProductCodeName", this.loanType.getValue().trim(), Filter.OP_EQUAL));
		}
		 */

		boolean accessToCreateNewFin = getFinanceDetailService().checkFirstTaskOwnerAccess(this.loanType.getValue().trim(), 
				getUserWorkspace().getLoginUserDetails().getLoginUsrID());

		this.searchObj.addTabelName("FinanceMain_TView");
		if (accessToCreateNewFin) {
			button_FinanceMainList_NewFinanceMain.setVisible(true);
		} else {
			button_FinanceMainList_NewFinanceMain.setVisible(false);
		}

		if (getUserWorkspace().getUserRoles() != null
				&& getUserWorkspace().getUserRoles().size() > 0) {
			String whereClause = "";

			for (int i = 0; i < getUserWorkspace().getUserRoles().size(); i++) {
				if (i > 0) {
					whereClause += " OR ";
				}

				whereClause += "(',' + nextRoleCode + ',' LIKE '%," + getUserWorkspace().getUserRoles().get(i) + ",%')";
			}

			whereClause += " ) AND ( " +getUsrFinAuthenticationQry(false);

			if (!"".equals(whereClause)) {
				this.searchObj.addWhereClause(whereClause);
			}
		}

		setSearchObj(this.searchObj);
		doSearch();
		// set the itemRenderer
		this.listBoxFinanceMain.setItemRenderer(new FinanceMainListModelItemRenderer());

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("FinanceMainList");

		this.button_FinanceMainList_NewFinanceMain.setVisible(getUserWorkspace().isAllowed("button_FinanceMainList_NewFinanceMain"));
		this.button_FinanceMainList_FinanceMainSearchDialog.setVisible(getUserWorkspace().isAllowed("button_FinanceMainList_FinanceMainFindDialog"));
		this.button_FinanceMainList_PrintList.setVisible(getUserWorkspace().isAllowed("button_FinanceMainList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.finance.financemain.model.FinanceMainListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onFinanceMainItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		// get the selected FinanceMain object
		final Listitem item = this.listBoxFinanceMain.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");
			final FinanceDetail financeDetail = getFinanceDetailService().getFinanceDetailById(aFinanceMain.getId(),false,"",false);

			if(financeDetail==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceMain.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm)
				, getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getError());
			}else{

				//Setting Notes Count from the list View
				financeDetail.getFinScheduleData().getFinanceMain().setLovDescNotes(aFinanceMain.getLovDescNotes());
				showDetailView(financeDetail);
			}	
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Call the FinanceMain dialog with a new empty entry. <br>
	 */
	public void onClick$button_FinanceMainList_NewFinanceMain(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		// create a new FinanceMain object, We GET it from the back end.
		final FinanceDetail aFinanceDetail = getFinanceDetailService().getNewFinanceDetail(false);
		aFinanceDetail.setNewRecord(true);

		/*
		 * we can call our SelectFinanceType ZUL-file with parameters. So we can
		 * call them with a object of the selected FinanceMain. For handed over
		 * these parameter only a Map is accepted. So we put the FinanceMain object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainListCtrl", this);
		map.put("searchObject", this.searchObj);
		map.put("financeDetail", aFinanceDetail);
		map.put("loanType", this.loanType.getValue());
		map.put("role", getUserWorkspace().getUserRoles());
		map.put("menuItemRightName", menuItemRightName);
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/SelectFinanceTypeDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FinanceMain (aFinanceMain)
	 * @throws Exception
	 */
	protected void showDetailView(FinanceDetail aFinanceDetail) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		if(aFinanceMain.getWorkflowId()==0 && isWorkFlowEnabled()){
			aFinanceMain.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeDetail", aFinanceDetail);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the FinanceMainListbox from the
		 * dialog when we do a delete, edit or insert a FinanceMain.
		 */
		map.put("financeMainListCtrl", this);
		map.put("menuItemRightName", menuItemRightName);

		// call the ZUL-file with the parameters packed in a map
		try {
			String productType = aFinanceMain.getLovDescProductCodeName();
			productType = (productType.substring(0, 1)).toUpperCase()+(productType.substring(1)).toLowerCase();

			StringBuilder fileLocaation = new StringBuilder("/WEB-INF/pages/Finance/FinanceMain/");
			if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_IJARAH)) {
				fileLocaation.append("IjarahFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_ISTISNA)) {
				fileLocaation.append("IstisnaFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_MUDARABA)) {
				fileLocaation.append("MudarabaFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_MURABAHA)) {
				fileLocaation.append("MurabahaFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_MUSHARAKA)) {
				fileLocaation.append("MusharakFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_TAWARRUQ)) {
				fileLocaation.append("TawarruqFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_SUKUK)) {
				fileLocaation.append("SukukFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_SUKUKNRM)) {
				fileLocaation.append("SukuknrmFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_ISTNORM)) {
				fileLocaation.append("IstnormFinanceMainDialog.zul");
			} else if (productType.equalsIgnoreCase(PennantConstants.FINANCE_PRODUCT_CONVENTIONAL)) {
				fileLocaation.append("ConvFinanceMainDialog.zul");
			} else {
				fileLocaation.append("FinanceMainDialog.zul");
			}

			Executions.createComponents(fileLocaation.toString(), this.window_FinanceMainList,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
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
		logger.debug("Entering " + event.toString());
		PTMessageUtils.showHelpWindow(event, window_FinanceMainList);
		logger.debug("Leaving " + event.toString());
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
		logger.debug("Entering " + event.toString());
		this.sortOperator_custID.setSelectedIndex(0);
		this.custID.setValue("");
		this.sortOperator_finAmount.setSelectedIndex(0);
		this.finAmount.setText("");
		this.sortOperator_finCcy.setSelectedIndex(0);
		this.finCcy.setValue("");
		this.sortOperator_finIsActive.setSelectedIndex(0);
		this.finIsActive.setChecked(false);
		this.sortOperator_finReference.setSelectedIndex(0);
		this.finReference.setValue("");
		this.sortOperator_finStartDate.setSelectedIndex(0);
		this.finStartDate.setValue(null);
		this.sortOperator_finType.setSelectedIndex(0);
		this.finType.setValue("");
		this.sortOperator_profitDaysBasis.setSelectedIndex(0);
		this.profitDaysBasis.setValue("");
		this.sortOperator_scheduleMethod.setSelectedIndex(0);
		this.scheduleMethod.setValue("");
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}

		//Clears all the filters
		this.searchObj.clearFilters();
		this.searchObj.addSort("LovDescProductCodeName",false);
		this.searchObj.addFilter(new Filter("InvestmentRef", "", Filter.OP_EQUAL));
		this.searchObj.addFilter(new Filter("RecordType", PennantConstants.RECORD_TYPE_NEW, Filter.OP_EQUAL));
		
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxFinanceMain,this.pagingFinanceMainList);

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method for call the FinanceMain dialog
	 */
	public void onClick$button_FinanceMainList_FinanceMainSearchDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doSearch();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When the financeMain print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_FinanceMainList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		new PTListReportUtils("FinanceMain", getSearchObj(),this.pagingFinanceMainList.getTotalSize()+1);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method for Searching List based on Filters
	 */
	public void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();

		this.searchObj.addSort("LovDescProductCodeName",false);
		this.searchObj.addFilter(new Filter("InvestmentRef", "", Filter.OP_EQUAL));
		this.searchObj.addFilter(new Filter("RecordType", PennantConstants.RECORD_TYPE_NEW, Filter.OP_EQUAL));

		// CustId
		if (!StringUtils.trimToEmpty(this.custID.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_custID.getSelectedItem(),this.custID.getValue(), "CustID");
		}
		//FinCcy
		if (!StringUtils.trimToEmpty(this.finCcy.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_finCcy.getSelectedItem(),this.finCcy.getValue(), "FinCcy");
		}
		// FinReference
		if (!StringUtils.trimToEmpty(this.finReference.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_finReference.getSelectedItem(),this.finReference.getValue(), "FinReference");
		}
		// FinType
		if (!StringUtils.trimToEmpty(this.finType.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_finType.getSelectedItem(),this.finType.getValue(), "FinType");
		}
		// ProfitDayBasis
		if (!StringUtils.trimToEmpty(this.profitDaysBasis.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_profitDaysBasis.getSelectedItem(),this.profitDaysBasis.getValue(), "ProfitDaysBasis");
		}
		if (!StringUtils.trimToEmpty(this.scheduleMethod.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_scheduleMethod.getSelectedItem(),this.scheduleMethod.getValue(), "ScheduleMethod");
		}
		//FinAmount
		if (this.finAmount.getValue() != null) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_finAmount.getSelectedItem(),
					PennantApplicationUtil.formateAmount(this.finAmount.getValue(), PennantConstants.defaultCCYDecPos), "FinAmount");
		}
		// FinStartDate
		if (this.finStartDate.getValue()!=null) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_finStartDate.getSelectedItem(), DateUtility.formatDate(this.finStartDate.getValue(), PennantConstants.DBDateFormat), "FinStartDate");
		}

		// Active
		int intActive=0;
		if(this.finIsActive.isChecked()){
			intActive=1;
		}
		searchObj = getSearchFilter(searchObj, this.sortOperator_finIsActive.getSelectedItem(),intActive, "FinIsActive");


		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordStatus.getSelectedItem(),this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null
				&& !PennantConstants.List_Select.equals(this.recordType.getSelectedItem().getValue())) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordType.getSelectedItem(),this.recordType.getSelectedItem().getValue().toString(),"RecordType");
		}

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = this.searchObj.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / "
						+ filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxFinanceMain,this.pagingFinanceMainList);

		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public JdbcSearchObject<FinanceMain> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<FinanceMain> searchObj) {
		this.searchObj = searchObj;
	}
}
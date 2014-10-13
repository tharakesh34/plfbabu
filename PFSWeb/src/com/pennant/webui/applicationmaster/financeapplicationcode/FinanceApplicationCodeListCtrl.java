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
 * FileName    		:  FinanceApplicationCodeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.financeapplicationcode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.FinanceApplicationCode;
import com.pennant.backend.service.applicationmaster.FinanceApplicationCodeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.applicationmaster.financeapplicationcode.model.FinanceApplicationCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/FinanceApplicationCode/FinanceApplicationCodeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class FinanceApplicationCodeListCtrl extends	GFCBaseListCtrl<FinanceApplicationCode> implements Serializable {

	private static final long serialVersionUID = -3571720185247491921L;
	private final static Logger logger = Logger.getLogger(FinanceApplicationCodeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_FinanceApplicationCodeList; 			// autoWired
	protected Borderlayout 	borderLayout_FinanceApplicationCodeList; 	// autoWired
	protected Paging 		pagingFinanceApplicationCodeList; 			// autoWired
	protected Listbox 		listBoxFinanceApplicationCode; 				// autoWired

	protected Textbox 	finAppType; 						// autoWired
	protected Listbox 	sortOperator_finAppType; 			// autoWired
	protected Textbox 	finAppDesc; 						// autoWired
	protected Listbox 	sortOperator_finAppDesc; 			// autoWired
	protected Checkbox 	finAppIsActive; 					// autoWired
	protected Listbox 	sortOperator_finAppIsActive; 		// autoWired
	protected Textbox 	recordStatus; 						// autoWired
	protected Listbox 	recordType; 						// autoWired
	protected Listbox 	sortOperator_recordStatus; 			// autoWired
	protected Listbox 	sortOperator_recordType; 			// autoWired

	// List headers
	protected Listheader listheader_FinAppType; 		// autoWired
	protected Listheader listheader_FinAppDesc; 		// autoWired
	protected Listheader listheader_FinAppIsActive; 	// autoWired
	protected Listheader listheader_RecordStatus; 		// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 															   // autoWired
	protected Button button_FinanceApplicationCodeList_NewFinanceApplicationCode;   	   // autoWired
	protected Button button_FinanceApplicationCodeList_FinanceApplicationCodeSearchDialog; // autoWired
	protected Button button_FinanceApplicationCodeList_PrintList; 						   // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<FinanceApplicationCode> searchObj;
	protected Row row_AlwWorkflow;
	protected Grid searchGrid;
	
	private transient FinanceApplicationCodeService financeApplicationCodeService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public FinanceApplicationCodeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceApplicationCode
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceApplicationCodeList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("FinanceApplicationCode");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinanceApplicationCode");

			if (workFlowDetails == null) {
				setWorkFlowEnabled(false);
			} else {
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}
		} else {
			wfAvailable = false;
		}

		this.sortOperator_finAppType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finAppType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finAppDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finAppDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finAppIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_finAppIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = setRecordType(this.recordType);
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		} else {
			this.row_AlwWorkflow.setVisible(false);
		}

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the list box. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_FinanceApplicationCodeList.setHeight(getBorderLayoutHeight());
		this.listBoxFinanceApplicationCode.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));
		// set the paging parameters
		this.pagingFinanceApplicationCodeList.setPageSize(getListRows());
		this.pagingFinanceApplicationCodeList.setDetailed(true);

		this.listheader_FinAppType.setSortAscending(new FieldComparator("finAppType", true));
		this.listheader_FinAppType.setSortDescending(new FieldComparator("finAppType", false));
		this.listheader_FinAppDesc.setSortAscending(new FieldComparator("finAppDesc", true));
		this.listheader_FinAppDesc.setSortDescending(new FieldComparator("finAppDesc", false));
		this.listheader_FinAppIsActive.setSortAscending(new FieldComparator("finAppIsActive", true));
		this.listheader_FinAppIsActive.setSortDescending(new FieldComparator("finAppIsActive", false));

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<FinanceApplicationCode>(FinanceApplicationCode.class, getListRows());
		this.searchObj.addSort("FinAppType", false);
		this.searchObj.addField("finAppType");
		this.searchObj.addField("finAppDesc");
		this.searchObj.addField("finAppIsActive");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTFinanceApplicaitonCodes_View");
			if (isFirstTask()) {
				button_FinanceApplicationCodeList_NewFinanceApplicationCode.setVisible(true);
			} else {
				button_FinanceApplicationCodeList_NewFinanceApplicationCode.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTFinanceApplicaitonCodes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_FinanceApplicationCodeList_NewFinanceApplicationCode.setVisible(false);
			this.button_FinanceApplicationCodeList_FinanceApplicationCodeSearchDialog.setVisible(false);
			this.button_FinanceApplicationCodeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			doSearch();
			// set the itemRenderer
			this.listBoxFinanceApplicationCode.setItemRenderer(new FinanceApplicationCodeListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("FinanceApplicationCodeList");
		this.button_FinanceApplicationCodeList_NewFinanceApplicationCode.setVisible(getUserWorkspace()
				.isAllowed("button_FinanceApplicationCodeList_NewFinanceApplicationCode"));
		this.button_FinanceApplicationCodeList_FinanceApplicationCodeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_FinanceApplicationCodeList_FinanceApplicationCodeFindDialog"));
		this.button_FinanceApplicationCodeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_FinanceApplicationCodeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.financeapplicationcode.model.
	 * FinanceApplicationCodeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onFinanceApplicationCodeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected FinanceApplicationCode object
		final Listitem item = this.listBoxFinanceApplicationCode.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceApplicationCode aFinanceApplicationCode = (FinanceApplicationCode) item.getAttribute("data");
			final FinanceApplicationCode financeApplicationCode = getFinanceApplicationCodeService()
			.getFinanceApplicationCodeById(aFinanceApplicationCode.getId());

			if (financeApplicationCode == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aFinanceApplicationCode.getFinAppType();
				errorParm[0] = PennantJavaUtil.getLabel("label_FinAppType")	+ ":" + aFinanceApplicationCode.getFinAppType();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND FinAppType='" + financeApplicationCode.getFinAppType()
				+ "' AND version=" + financeApplicationCode.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"FinanceApplicationCode", whereCond,financeApplicationCode.getTaskId(),	financeApplicationCode.getNextTaskId());
					if (userAcces) {
						showDetailView(financeApplicationCode);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(financeApplicationCode);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the FinanceApplicationCode dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_FinanceApplicationCodeList_NewFinanceApplicationCode(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new FinanceApplicationCode object, We GET it from the back end
		final FinanceApplicationCode aFinanceApplicationCode = getFinanceApplicationCodeService().getNewFinanceApplicationCode();

		showDetailView(aFinanceApplicationCode);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FinanceApplicationCode
	 *            (aFinanceApplicationCode)
	 * @throws Exception
	 */
	private void showDetailView(FinanceApplicationCode aFinanceApplicationCode) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aFinanceApplicationCode.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aFinanceApplicationCode.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeApplicationCode", aFinanceApplicationCode);

		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the FinanceApplicationCodeListbox
		 * from the dialog when we do a delete, edit or insert a
		 * FinanceApplicationCode.
		 */
		map.put("financeApplicationCodeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/FinanceApplicationCode/FinanceApplicationCodeDialog.zul", null, map);
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
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_FinanceApplicationCodeList);
		logger.debug("Leaving" + event.toString());
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
		logger.debug("Entering" + event.toString());
		this.sortOperator_finAppType.setSelectedIndex(0);
		this.finAppType.setValue("");
		this.sortOperator_finAppDesc.setSelectedIndex(0);
		this.finAppDesc.setValue("");
		this.sortOperator_finAppIsActive.setSelectedIndex(0);
		this.finAppIsActive.setChecked(false);
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		//Clears All the Filters
		this.searchObj.clearFilters();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxFinanceApplicationCode, this.pagingFinanceApplicationCodeList);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the FinanceApplicationCode dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_FinanceApplicationCodeList_FinanceApplicationCodeSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the financeApplicationCode print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_FinanceApplicationCodeList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("FinanceApplicationCode", getSearchObj(),this.pagingFinanceApplicationCodeList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}
	public void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();

		if (!StringUtils.trimToEmpty(this.finAppType.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_finAppType.getSelectedItem(),this.finAppType.getValue(), "FinAppType");
		}
		if (!StringUtils.trimToEmpty(this.finAppDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_finAppDesc.getSelectedItem(),this.finAppDesc.getValue(), "FinAppDesc");
		}

		int intActive=0;
		if(this.finAppIsActive.isChecked()){
			intActive=1;
		}
		searchObj = getSearchFilter(searchObj, this.sortOperator_finAppIsActive.getSelectedItem(),intActive, "FinAppIsActive");

		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_recordStatus.getSelectedItem(),
					this.recordStatus.getValue(), "RecordStatus");
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
		getPagedListWrapper().init(this.searchObj, this.listBoxFinanceApplicationCode,this.pagingFinanceApplicationCodeList);
		logger.debug("Leaving");
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setFinanceApplicationCodeService(FinanceApplicationCodeService financeApplicationCodeService) {
		this.financeApplicationCodeService = financeApplicationCodeService;
	}
	public FinanceApplicationCodeService getFinanceApplicationCodeService() {
		return this.financeApplicationCodeService;
	}

	public JdbcSearchObject<FinanceApplicationCode> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<FinanceApplicationCode> searchObj) {
		this.searchObj = searchObj;
	}

}
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
 * FileName    		:  CorpRelationCodeListCtrl.java                                                   * 	  
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

package com.pennant.webui.applicationmaster.corprelationcode;

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
import com.pennant.backend.model.applicationmaster.CorpRelationCode;
import com.pennant.backend.service.applicationmaster.CorpRelationCodeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.applicationmaster.corprelationcode.model.CorpRelationCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/CorpRelationCode/CorpRelationCodeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CorpRelationCodeListCtrl extends GFCBaseListCtrl<CorpRelationCode>	implements Serializable {

	private static final long serialVersionUID = -2566872901248774242L;
	private final static Logger logger = Logger.getLogger(CorpRelationCodeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CorpRelationCodeList; 		// autoWired
	protected Borderlayout 	borderLayout_CorpRelationCodeList; 	// autoWired
	protected Paging 		pagingCorpRelationCodeList; 		// autoWired
	protected Listbox 		listBoxCorpRelationCode; 			// autoWired

	protected Textbox 	corpRelationCode; 					// autoWired
	protected Listbox 	sortOperator_corpRelationCode; 		// autoWired
	protected Textbox 	corpRelationDesc; 					// autoWired
	protected Listbox 	sortOperator_corpRelationDesc; 		// autoWired
	protected Checkbox 	corpRelationIsActive; 				// autoWired
	protected Listbox 	sortOperator_corpRelationIsActive; 	// autoWired
	protected Textbox 	recordStatus; 						// autoWired
	protected Listbox 	recordType; 						// autoWired
	protected Listbox 	sortOperator_recordStatus; 			// autoWired
	protected Listbox 	sortOperator_recordType; 			// autoWired

	// List headers
	protected Listheader listheader_CorpRelationCode; 		// autoWired
	protected Listheader listheader_CorpRelationDesc; 		// autoWired
	protected Listheader listheader_CorpRelationIsActive; 	// autoWired
	protected Listheader listheader_RecordStatus; 			// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 													// autoWired
	protected Button button_CorpRelationCodeList_NewCorpRelationCode; 			// autoWired
	protected Button button_CorpRelationCodeList_CorpRelationCodeSearchDialog; 	// autoWired
	protected Button button_CorpRelationCodeList_PrintList; 					// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CorpRelationCode> searchObj;
	protected Row row_AlwWorkflow;
	protected Grid searchGrid;
	
	private transient CorpRelationCodeService corpRelationCodeService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public CorpRelationCodeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected CorpRelationCode
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CorpRelationCodeList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CorpRelationCode");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CorpRelationCode");

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
		this.sortOperator_corpRelationCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_corpRelationCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_corpRelationDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_corpRelationDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_corpRelationIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_corpRelationIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType =setRecordType(this.recordType);
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
		this.borderLayout_CorpRelationCodeList.setHeight(getBorderLayoutHeight());
		this.listBoxCorpRelationCode.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingCorpRelationCodeList.setPageSize(getListRows());
		this.pagingCorpRelationCodeList.setDetailed(true);

		this.listheader_CorpRelationCode.setSortAscending(new FieldComparator("corpRelationCode", true));
		this.listheader_CorpRelationCode.setSortDescending(new FieldComparator("corpRelationCode", false));
		this.listheader_CorpRelationDesc.setSortAscending(new FieldComparator("corpRelationDesc", true));
		this.listheader_CorpRelationDesc.setSortDescending(new FieldComparator("corpRelationDesc", false));
		this.listheader_CorpRelationIsActive.setSortAscending(new FieldComparator("corpRelationIsActive", true));
		this.listheader_CorpRelationIsActive.setSortDescending(new FieldComparator("corpRelationIsActive", false));

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
		this.searchObj = new JdbcSearchObject<CorpRelationCode>(CorpRelationCode.class, getListRows());
		this.searchObj.addSort("CorpRelationCode", false);
		this.searchObj.addField("corpRelationCode");
		this.searchObj.addField("corpRelationDesc");
		this.searchObj.addField("corpRelationIsActive");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTCorpRelationCodes_View");
			if (isFirstTask()) {
				button_CorpRelationCodeList_NewCorpRelationCode.setVisible(true);
			} else {
				button_CorpRelationCodeList_NewCorpRelationCode.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTCorpRelationCodes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_CorpRelationCodeList_NewCorpRelationCode.setVisible(false);
			this.button_CorpRelationCodeList_CorpRelationCodeSearchDialog.setVisible(false);
			this.button_CorpRelationCodeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			doSearch();
			// set the itemRenderer
			this.listBoxCorpRelationCode.setItemRenderer(new CorpRelationCodeListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("CorpRelationCodeList");

		this.button_CorpRelationCodeList_NewCorpRelationCode.setVisible(getUserWorkspace()
				.isAllowed("button_CorpRelationCodeList_NewCorpRelationCode"));
		this.button_CorpRelationCodeList_CorpRelationCodeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_CorpRelationCodeList_CorpRelationCodeFindDialog"));
		this.button_CorpRelationCodeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_CorpRelationCodeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.corprelationcode.model.
	 * CorpRelationCodeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCorpRelationCodeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected CorpRelationCode object
		final Listitem item = this.listBoxCorpRelationCode.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CorpRelationCode aCorpRelationCode = (CorpRelationCode) item.getAttribute("data");
			final CorpRelationCode corpRelationCode = getCorpRelationCodeService().getCorpRelationCodeById(aCorpRelationCode.getId());

			if (corpRelationCode == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aCorpRelationCode.getCorpRelationCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_CorpRelationCode") + ":" + aCorpRelationCode.getCorpRelationCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND CorpRelationCode='" + corpRelationCode.getCorpRelationCode()
				+ "' AND version=" + corpRelationCode.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"CorpRelationCode", whereCond, corpRelationCode.getTaskId(), corpRelationCode.getNextTaskId());
					if (userAcces) {
						showDetailView(corpRelationCode);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(corpRelationCode);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the CorpRelationCode dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CorpRelationCodeList_NewCorpRelationCode(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new CorpRelationCode object, We GET it from the back end.
		final CorpRelationCode aCorpRelationCode = getCorpRelationCodeService().getNewCorpRelationCode();
		showDetailView(aCorpRelationCode);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param aCorpRelationCode
	 *            (CorpRelationCode)
	 * @throws Exception
	 */
	private void showDetailView(CorpRelationCode aCorpRelationCode)	throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aCorpRelationCode.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aCorpRelationCode.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("corpRelationCode", aCorpRelationCode);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the CorpRelationCodeListbox from
		 * the dialog when we do a delete, edit or insert a CorpRelationCode.
		 */
		map.put("corpRelationCodeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/CorpRelationCode/CorpRelationCodeDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_CorpRelationCodeList);
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
		this.sortOperator_corpRelationCode.setSelectedIndex(0);
		this.corpRelationCode.setValue("");
		this.sortOperator_corpRelationDesc.setSelectedIndex(0);
		this.corpRelationDesc.setValue("");
		this.sortOperator_corpRelationIsActive.setSelectedIndex(0);
		this.corpRelationIsActive.setChecked(false);
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		//Clears All the Filters
		this.searchObj.clearFilters();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxCorpRelationCode, this.pagingCorpRelationCodeList);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the CorpRelationCode dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CorpRelationCodeList_CorpRelationCodeSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the corpRelationCode print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_CorpRelationCodeList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("CorpRelationCode", getSearchObj(),this.pagingCorpRelationCodeList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}
	public void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();


		if (!StringUtils.trimToEmpty(this.corpRelationCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_corpRelationCode.getSelectedItem(),this.corpRelationCode.getValue(), "CorpRelationCode");
		}
		if (!StringUtils.trimToEmpty(this.corpRelationDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_corpRelationDesc.getSelectedItem(),this.corpRelationDesc.getValue(), "CorpRelationDesc");
		}
		int intActive=0;
		if(this.corpRelationIsActive.isChecked()){
			intActive=1;
		}
		searchObj = getSearchFilter(searchObj, this.sortOperator_corpRelationIsActive.getSelectedItem(),intActive, "CorpRelationIsActive");
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
		getPagedListWrapper().init(this.searchObj, this.listBoxCorpRelationCode,this.pagingCorpRelationCodeList);

		logger.debug("Leaving");
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCorpRelationCodeService(CorpRelationCodeService corpRelationCodeService) {
		this.corpRelationCodeService = corpRelationCodeService;
	}
	public CorpRelationCodeService getCorpRelationCodeService() {
		return this.corpRelationCodeService;
	}

	public JdbcSearchObject<CorpRelationCode> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<CorpRelationCode> searchObj) {
		this.searchObj = searchObj;
	}
}
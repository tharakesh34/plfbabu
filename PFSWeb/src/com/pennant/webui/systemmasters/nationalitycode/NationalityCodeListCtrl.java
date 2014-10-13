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
 * FileName    		:  NationalityCodeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.nationalitycode;

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
import com.pennant.backend.model.systemmasters.NationalityCode;
import com.pennant.backend.service.systemmasters.NationalityCodeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.systemmasters.nationalitycode.model.NationalityCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/NationalityCode/NationalityCodeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class NationalityCodeListCtrl extends GFCBaseListCtrl<NationalityCode>	implements Serializable {

	private static final long serialVersionUID = 1844331787045784573L;
	private final static Logger logger = Logger.getLogger(NationalityCodeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_NationalityCodeList; 			          // autoWired
	protected Borderlayout 	borderLayout_NationalityCodeList; 		          // autoWired
	protected Paging 		pagingNationalityCodeList; 			              // autoWired
	protected Listbox 		listBoxNationalityCode; 				          // autoWired

	protected Textbox 	nationalityCode; 					// autoWired
	protected Listbox 	sortOperator_nationalityCode; 		// autoWired
	protected Textbox 	nationalityDesc; 					// autoWired
	protected Listbox 	sortOperator_nationalityDesc; 		// autoWired
	protected Checkbox 	nationalityIsActive; 				// autoWired
	protected Listbox 	sortOperator_nationalityIsActive; 	// autoWired
	protected Textbox 	recordStatus; 						// autoWired
	protected Listbox 	recordType; 						// autoWired
	protected Listbox 	sortOperator_recordStatus; 			// autoWired
	protected Listbox 	sortOperator_recordType; 			// autoWired

	// List headers
	protected Listheader listheader_NationalityCode; 				          // autoWired
	protected Listheader listheader_NationalityDesc; 				          // autoWired
	protected Listheader listheader_NationalityIsActive; 			          // autoWired
	protected Listheader listheader_RecordStatus; 					          // autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp;  												  // autoWired
	protected Button button_NationalityCodeList_NewNationalityCode; 		  // autoWired
	protected Button button_NationalityCodeList_NationalityCodeSearchDialog;  // autoWired
	protected Button button_NationalityCodeList_PrintList; 				      // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<NationalityCode> searchObj;
	protected Grid searchGrid;
	protected Row row_AlwWorkflow;
	
	private transient NationalityCodeService nationalityCodeService;
	private transient WorkFlowDetails workFlowDetails = null;
	/**
	 * default constructor.<br>
	 */
	public NationalityCodeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected NationalityCode object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_NationalityCodeList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("NationalityCode");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("NationalityCode");

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
		this.sortOperator_nationalityCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_nationalityCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_nationalityDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_nationalityDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_nationalityIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_nationalityIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
		 * currentDesktopHeight from a hidden Initialize box from the index.zul
		 * that are filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_NationalityCodeList.setHeight(getBorderLayoutHeight());
		this.listBoxNationalityCode.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));
		// set the paging parameters
		this.pagingNationalityCodeList.setPageSize(getListRows());
		this.pagingNationalityCodeList.setDetailed(true);

		this.listheader_NationalityCode.setSortAscending(new FieldComparator("nationalityCode", true));
		this.listheader_NationalityCode.setSortDescending(new FieldComparator("nationalityCode", false));
		this.listheader_NationalityDesc.setSortAscending(new FieldComparator("nationalityDesc", true));
		this.listheader_NationalityDesc.setSortDescending(new FieldComparator("nationalityDesc", false));
		this.listheader_NationalityIsActive.setSortAscending(new FieldComparator("nationalityIsActive",	true));
		this.listheader_NationalityIsActive.setSortDescending(new FieldComparator("nationalityIsActive", false));

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
		this.searchObj = new JdbcSearchObject<NationalityCode>(NationalityCode.class, getListRows());
		this.searchObj.addSort("NationalityCode",false);
		this.searchObj.addField("nationalityCode");
		this.searchObj.addField("nationalityDesc");
		this.searchObj.addField("nationalityIsActive");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTNationalityCodes_View");
			if (isFirstTask()) {
				button_NationalityCodeList_NewNationalityCode.setVisible(true);
			} else {
				button_NationalityCodeList_NewNationalityCode.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTNationalityCodes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_NationalityCodeList_NewNationalityCode.setVisible(false);
			this.button_NationalityCodeList_NationalityCodeSearchDialog.setVisible(false);
			this.button_NationalityCodeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			doSearch();
			// set the itemRenderer
			this.listBoxNationalityCode.setItemRenderer(new NationalityCodeListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("NationalityCodeList");

		this.button_NationalityCodeList_NewNationalityCode.setVisible(getUserWorkspace()
				.isAllowed("button_NationalityCodeList_NewNationalityCode"));
		this.button_NationalityCodeList_NationalityCodeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_NationalityCodeList_NationalityCodeFindDialog"));
		this.button_NationalityCodeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_NationalityCodeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.nationalitycodes.model.
	 * NationalityCodeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onNationalityCodeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// get the selected NationalityCode object
		final Listitem item = this.listBoxNationalityCode.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final NationalityCode aNationalityCode = (NationalityCode) item.getAttribute("data");
			final NationalityCode nationalityCode = getNationalityCodeService().getNationalityCodeById(aNationalityCode.getId());

			if (nationalityCode == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aNationalityCode.getNationalityCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_NationalityCode") + ":" + aNationalityCode.getNationalityCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND NationalityCode='" + nationalityCode.getNationalityCode()
				+ "' AND version=" + nationalityCode.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"NationalityCode", whereCond, nationalityCode.getTaskId(), nationalityCode.getNextTaskId());
					if (userAcces) {
						showDetailView(nationalityCode);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(nationalityCode);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the NationalityCode dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_NationalityCodeList_NewNationalityCode(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new NationalityCode object, We GET it from the back end.
		final NationalityCode aNationalityCode = getNationalityCodeService().getNewNationalityCode();
		showDetailView(aNationalityCode);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param NationalityCode
	 *            (aNationalityCode)
	 * @throws Exception
	 */
	private void showDetailView(NationalityCode aNationalityCode) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aNationalityCode.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aNationalityCode.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("nationalityCode", aNationalityCode);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the NationalityCodeListbox from
		 * the dialog when we do a delete, edit or insert a NationalityCode.
		 */
		map.put("nationalityCodeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/NationalityCode/NationalityCodeDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_NationalityCodeList);
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
		this.sortOperator_nationalityCode.setSelectedIndex(0);
		this.nationalityCode.setValue("");
		this.sortOperator_nationalityDesc.setSelectedIndex(0);
		this.nationalityDesc.setValue("");
		this.sortOperator_nationalityIsActive.setSelectedIndex(0);
		this.nationalityIsActive.setChecked(false);
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		//Clears the filters
		this.searchObj.clearFilters();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxNationalityCode, this.pagingNationalityCodeList);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the NationalityCode dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_NationalityCodeList_NationalityCodeSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the nationalityCode print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_NationalityCodeList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("NationalityCode", getSearchObj(),this.pagingNationalityCodeList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}
	public void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();

		if (!StringUtils.trimToEmpty(this.nationalityCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_nationalityCode.getSelectedItem(),this.nationalityCode.getValue(), "NationalityCode");
		}
		if (!StringUtils.trimToEmpty(this.nationalityDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_nationalityDesc.getSelectedItem(),this.nationalityDesc.getValue(), "NationalityDesc");
		}
		// Active
		int intActive=0;
		if(this.nationalityIsActive.isChecked()){
			intActive=1;
		}
		searchObj = getSearchFilter(searchObj, this.sortOperator_nationalityIsActive.getSelectedItem(),intActive, "NationalityIsActive");

		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordStatus.getSelectedItem(),this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null
				&& !PennantConstants.List_Select.equals(this.recordType
						.getSelectedItem().getValue())) {
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
		getPagedListWrapper().init(this.searchObj, this.listBoxNationalityCode,this.pagingNationalityCodeList);

		logger.debug("Leaving");
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setNationalityCodeService(NationalityCodeService nationalityCodeService) {
		this.nationalityCodeService = nationalityCodeService;
	}
	public NationalityCodeService getNationalityCodeService() {
		return this.nationalityCodeService;
	}

	public JdbcSearchObject<NationalityCode> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<NationalityCode> searchObj) {
		this.searchObj = searchObj;
	}
}
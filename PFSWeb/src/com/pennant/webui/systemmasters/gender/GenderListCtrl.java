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
 * FileName    		:  GenderListCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.gender;

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
import com.pennant.backend.model.systemmasters.Gender;
import com.pennant.backend.service.systemmasters.GenderService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.systemmasters.gender.model.GenderListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/Gender/GenderList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class GenderListCtrl extends GFCBaseListCtrl<Gender> implements Serializable {

	private static final long serialVersionUID = 3226455931949186314L;
	private final static Logger logger = Logger.getLogger(GenderListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_GenderList; 			// autoWired
	protected Borderlayout 	borderLayout_GenderList; 	// autoWired
	protected Paging 		pagingGenderList; 			// autoWired
	protected Listbox 		listBoxGender; 				// autoWired

	protected Textbox 	genderCode; 					// autoWired
	protected Listbox 	sortOperator_genderCode; 		// autoWired
	protected Textbox 	genderDesc; 					// autoWired
	protected Listbox 	sortOperator_genderDesc; 		// autoWired
	protected Checkbox 	genderIsActive; 				// autoWired
	protected Listbox 	sortOperator_genderIsActive; 	// autoWired
	protected Textbox 	recordStatus; 					// autoWired
	protected Listbox 	recordType; 					// autoWired
	protected Listbox	sortOperator_recordStatus; 		// autoWired
	protected Listbox 	sortOperator_recordType; 		// autoWired
	// List headers
	protected Listheader listheader_GenderCode; 		// autoWired
	protected Listheader listheader_GenderDesc; 		// autoWired
	protected Listheader listheader_GenderIsActive; 	// autoWired
	protected Listheader listheader_RecordStatus; 		// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 									// autoWired
	protected Button button_GenderList_NewGender; 				// autoWired
	protected Button button_GenderList_GenderSearchDialog; 		// autoWired
	protected Button button_GenderList_PrintList; 				// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Gender> searchObj;
	protected Grid searchGrid;
	protected Row row_AlwWorkflow;
	
	private transient GenderService genderService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public GenderListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Gender object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_GenderList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Gender");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Gender");

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

		this.sortOperator_genderCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_genderCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_genderDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_genderDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_genderIsActive.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_genderIsActive.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
		this.borderLayout_GenderList.setHeight(getBorderLayoutHeight());
		this.listBoxGender.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));
		// set the paging parameters
		this.pagingGenderList.setPageSize(getListRows());
		this.pagingGenderList.setDetailed(true);

		this.listheader_GenderCode.setSortAscending(new FieldComparator("genderCode", true));
		this.listheader_GenderCode.setSortDescending(new FieldComparator("genderCode", false));
		this.listheader_GenderDesc.setSortAscending(new FieldComparator("genderDesc", true));
		this.listheader_GenderDesc.setSortDescending(new FieldComparator("genderDesc", false));
		this.listheader_GenderIsActive.setSortAscending(new FieldComparator("genderIsActive", true));
		this.listheader_GenderIsActive.setSortDescending(new FieldComparator("genderIsActive", false));

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
		this.searchObj = new JdbcSearchObject<Gender>(Gender.class,getListRows());
		this.searchObj.addSort("GenderCode", false);
		this.searchObj.addFilter(new Filter("GenderCode", PennantConstants.NONE, Filter.OP_NOT_EQUAL));
		this.searchObj.addField("genderCode");
		this.searchObj.addField("genderDesc");
		this.searchObj.addField("genderIsActive");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");
		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTGenders_View");
			if (isFirstTask()) {
				button_GenderList_NewGender.setVisible(true);
			} else {
				button_GenderList_NewGender.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTGenders_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_GenderList_NewGender.setVisible(false);
			this.button_GenderList_GenderSearchDialog.setVisible(false);
			this.button_GenderList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			doSearch();
			// set the itemRenderer
			this.listBoxGender.setItemRenderer(new GenderListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("GenderList");

		this.button_GenderList_NewGender.setVisible(getUserWorkspace()
				.isAllowed("button_GenderList_NewGender"));
		this.button_GenderList_GenderSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_GenderList_GenderFindDialog"));
		this.button_GenderList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_GenderList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see:
	 * com.pennant.webui.bmtmasters.gender.model.GenderListModelItemRenderer
	 * .java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onGenderItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Gender object
		final Listitem item = this.listBoxGender.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Gender aGender = (Gender) item.getAttribute("data");
			final Gender gender = getGenderService().getGenderById(aGender.getId());

			if (gender == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aGender.getGenderCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_GenderCode") + ":" + aGender.getGenderCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND GenderCode='" + gender.getGenderCode()
				+ "' AND version=" + gender.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"Gender", whereCond, gender.getTaskId(),gender.getNextTaskId());
					if (userAcces) {
						showDetailView(gender);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(gender);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Gender dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_GenderList_NewGender(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new Gender object, We GET it from the back end.
		final Gender aGender = getGenderService().getNewGender();
		showDetailView(aGender);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param Gender
	 *            (aGender)
	 * @throws Exception
	 */
	private void showDetailView(Gender aGender) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aGender.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aGender.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("gender", aGender);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the GenderListbox from the dialog
		 * when we do a delete, edit or insert a Gender.
		 */
		map.put("genderListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/Gender/GenderDialog.zul", null,map);
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
		PTMessageUtils.showHelpWindow(event, window_GenderList);
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

		this.sortOperator_genderCode.setSelectedIndex(0);
		this.genderCode.setValue("");
		this.sortOperator_genderDesc.setSelectedIndex(0);
		this.genderDesc.setValue("");
		this.sortOperator_genderIsActive.setSelectedIndex(0);
		this.genderIsActive.setChecked(false);
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		// Clears the filters
		this.searchObj.clearFilters();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxGender, this.pagingGenderList);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the Gender dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_GenderList_GenderSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}
	/**
	 * When the gender print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_GenderList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("Gender", getSearchObj(),this.pagingGenderList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}
	public void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();
		
		if (!StringUtils.trimToEmpty(this.genderCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_genderCode.getSelectedItem(),this.genderCode.getValue(), "GenderCode");
		}
		if (!StringUtils.trimToEmpty(this.genderDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_genderDesc.getSelectedItem(),this.genderDesc.getValue(), "GenderDesc");
		}

		// Active
		int intActive=0;
		if(this.genderIsActive.isChecked()){
			intActive=1;
		}
		searchObj = getSearchFilter(searchObj, this.sortOperator_genderIsActive.getSelectedItem(),intActive, "GenderIsActive");

		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordStatus.getSelectedItem(),this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null
				&& !PennantConstants.List_Select.equals(this.recordType.getSelectedItem().getValue())) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_recordType.getSelectedItem(),this.recordType.getSelectedItem().getValue().toString(),"RecordType");
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
		getPagedListWrapper().init(this.searchObj, this.listBoxGender,this.pagingGenderList);

		logger.debug("Leaving");
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setGenderService(GenderService genderService) {
		this.genderService = genderService;
	}
	public GenderService getGenderService() {
		return this.genderService;
	}

	public JdbcSearchObject<Gender> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<Gender> searchObj) {
		this.searchObj = searchObj;
	}

}
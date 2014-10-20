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
 * FileName    		:  CourseTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-09-2011    														*
 *                                                                  						*
 * Modified Date    :  29-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.amtmasters.coursetype;


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
import com.pennant.backend.model.amtmasters.CourseType;
import com.pennant.backend.service.amtmasters.CourseTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.amtmasters.coursetype.model.CourseTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/AMTMasters/CourseType/CourseTypeList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CourseTypeListCtrl extends GFCBaseListCtrl<CourseType> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(CourseTypeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CourseTypeList; // autowired
	protected Borderlayout borderLayout_CourseTypeList; // autowired
	protected Paging pagingCourseTypeList; // autowired
	protected Listbox listBoxCourseType; // autowired

	protected Textbox courseTypeCode;                     // autowired
	protected Listbox sortOperator_courseTypeCode;        // autowired
	protected Textbox courseTypeDesc;                     // autowired
	protected Listbox sortOperator_courseTypeDesc;        // autowired
	protected Textbox recordStatus;                       // autowired
	protected Listbox recordType;	                      // autowired
	protected Listbox sortOperator_recordStatus;          // autowired
	protected Listbox sortOperator_recordType;            // autowired

	// List headers
	protected Listheader listheader_CourseTypeCode; // autowired
	protected Listheader listheader_CourseTypeDesc; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_CourseTypeList_NewCourseType; // autowired
	protected Button button_CourseTypeList_CourseTypeSearchDialog; // autowired
	protected Button button_CourseTypeList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CourseType> searchObj;
	protected Grid searchGrid;
	protected Row row_AlwWorkflow;

	private transient CourseTypeService courseTypeService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public CourseTypeListCtrl() {
		super();
	}

	public void onCreate$window_CourseTypeList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CourseType");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CourseType");
			
			if (workFlowDetails==null){
				setWorkFlowEnabled(false);
			}else{
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}	
		}else{
			wfAvailable=false;
		}

		this.sortOperator_courseTypeCode.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_courseTypeCode.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_courseTypeDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_courseTypeDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

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
		
		this.borderLayout_CourseTypeList.setHeight(getBorderLayoutHeight());
		this.listBoxCourseType.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));
		// set the paging parameters
		this.pagingCourseTypeList.setPageSize(getListRows());
		this.pagingCourseTypeList.setDetailed(true);

		this.listheader_CourseTypeCode.setSortAscending(new FieldComparator("courseTypeCode", true));
		this.listheader_CourseTypeCode.setSortDescending(new FieldComparator("courseTypeCode", false));
		this.listheader_CourseTypeDesc.setSortAscending(new FieldComparator("courseTypeDesc", true));
		this.listheader_CourseTypeDesc.setSortDescending(new FieldComparator("courseTypeDesc", false));
		
		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}
		
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<CourseType>(CourseType.class,getListRows());
		this.searchObj.addSort("CourseTypeCode", false);
		this.searchObj.addField("courseTypeCode");
		this.searchObj.addField("courseTypeDesc");
		this.searchObj.addField("recordStatus");
		this.searchObj.addField("recordType");

		this.searchObj.addTabelName("AMTCourseType_View");
		
		// Workflow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_CourseTypeList_NewCourseType.setVisible(true);
			} else {
				button_CourseTypeList_NewCourseType.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_CourseTypeList_NewCourseType.setVisible(false);
			this.button_CourseTypeList_CourseTypeSearchDialog.setVisible(false);
			this.button_CourseTypeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			doSearch();
			// set the itemRenderer
			this.listBoxCourseType.setItemRenderer(new CourseTypeListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CourseTypeList");
		
		this.button_CourseTypeList_NewCourseType.setVisible(getUserWorkspace().isAllowed("button_CourseTypeList_NewCourseType"));
		this.button_CourseTypeList_CourseTypeSearchDialog.setVisible(getUserWorkspace().isAllowed("button_CourseTypeList_CourseTypeFindDialog"));
		this.button_CourseTypeList_PrintList.setVisible(getUserWorkspace().isAllowed("button_CourseTypeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.amtmaster.coursetype.model.CourseTypeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onCourseTypeItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected CourseType object
		final Listitem item = this.listBoxCourseType.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CourseType aCourseType = (CourseType) item.getAttribute("data");
			final CourseType courseType = getCourseTypeService().getCourseTypeById(aCourseType.getId());
			
			if(courseType==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aCourseType.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_CourseTypeCode")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND CourseTypeCode='"+ courseType.getCourseTypeCode()+"' AND version=" + courseType.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "CourseType", whereCond, courseType.getTaskId(), courseType.getNextTaskId());
					if (userAcces){
						showDetailView(courseType);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(courseType);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the CourseType dialog with a new empty entry. <br>
	 */
	public void onClick$button_CourseTypeList_NewCourseType(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new CourseType object, We GET it from the backend.
		final CourseType aCourseType = getCourseTypeService().getNewCourseType();
		showDetailView(aCourseType);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param CourseType (aCourseType)
	 * @throws Exception
	 */
	private void showDetailView(CourseType aCourseType) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aCourseType.getWorkflowId()==0 && isWorkFlowEnabled()){
			aCourseType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("courseType", aCourseType);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the CourseTypeListbox from the
		 * dialog when we do a delete, edit or insert a CourseType.
		 */
		map.put("courseTypeListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/AMTMasters/CourseType/CourseTypeDialog.zul",null,map);
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
		logger.debug(event.toString());
		PTMessageUtils.showHelpWindow(event, window_CourseTypeList);
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
		this.sortOperator_courseTypeCode.setSelectedIndex(0);
		this.courseTypeCode.setValue("");
		this.sortOperator_courseTypeDesc.setSelectedIndex(0);
		this.courseTypeDesc.setValue("");
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");
			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		//Clear All Filters
		this.searchObj.clearFilters();
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxCourseType,this.pagingCourseTypeList);
		logger.debug("Leaving");
	}

	/*
	 * call the CourseType dialog
	 */
	
	public void onClick$button_CourseTypeList_CourseTypeSearchDialog(Event event) throws Exception {
		logger.debug("Entering");
		doSearch();
		logger.debug("Leaving");
	}

	/**
	 * When the courseType print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CourseTypeList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		new PTListReportUtils("CourseType", getSearchObj(),this.pagingCourseTypeList.getTotalSize()+1);
		logger.debug("Leaving");
	}

	/**
	 * Method for Searching List based on Filters
	 */
	private void doSearch() {
		logger.debug("Entering");

		this.searchObj.clearFilters();

		// CourseTypeCode
		if (!StringUtils.trimToEmpty(this.courseTypeCode.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_courseTypeCode.getSelectedItem(),
					this.courseTypeCode.getValue(), "CourseTypeCode");
		}

		// CourseTypeDescription
		if (!StringUtils.trimToEmpty(this.courseTypeDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_courseTypeDesc.getSelectedItem(),
					this.courseTypeDesc.getValue(), "CourseTypeDesc");
		}
		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,
					this.sortOperator_recordStatus.getSelectedItem(),
					this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null && !PennantConstants.List_Select.equals(this.recordType
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
		getPagedListWrapper().init(this.searchObj, this.listBoxCourseType,this.pagingCourseTypeList);

		logger.debug("Leaving");

	}


	public void setCourseTypeService(CourseTypeService courseTypeService) {
		this.courseTypeService = courseTypeService;
	}

	public CourseTypeService getCourseTypeService() {
		return this.courseTypeService;
	}

	public JdbcSearchObject<CourseType> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<CourseType> searchObj) {
		this.searchObj = searchObj;
	}
}
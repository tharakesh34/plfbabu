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
 * FileName    		:  CourseListCtrl.java                                                   * 	  
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

package com.pennant.webui.amtmasters.course;


import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.amtmasters.Course;
import com.pennant.backend.service.amtmasters.CourseService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.amtmasters.course.model.CourseListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/AMTMasters/Course/CourseList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CourseListCtrl extends GFCBaseListCtrl<Course> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(CourseListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CourseList; // autowired
	protected Borderlayout borderLayout_CourseList; // autowired
	protected Paging pagingCourseList; // autowired
	protected Listbox listBoxCourse; // autowired

	// List headers
	protected Listheader listheader_CourseName; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_CourseList_NewCourse; // autowired
	protected Button button_CourseList_CourseSearchDialog; // autowired
	protected Button button_CourseList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Course> searchObj;
	
	private transient CourseService courseService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public CourseListCtrl() {
		super();
	}

	public void onCreate$window_CourseList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Course");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Course");
			
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
		
		/* set components visible dependent on the users rights */
		doCheckRights();
		
		this.borderLayout_CourseList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCourseList.setPageSize(getListRows());
		this.pagingCourseList.setDetailed(true);

		this.listheader_CourseName.setSortAscending(new FieldComparator("courseName", true));
		this.listheader_CourseName.setSortDescending(new FieldComparator("courseName", false));
		
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
		this.searchObj = new JdbcSearchObject<Course>(Course.class,getListRows());
		this.searchObj.addSort("CourseName", false);

		this.searchObj.addTabelName("AMTCourse_View");
		
		// Workflow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_CourseList_NewCourse.setVisible(true);
			} else {
				button_CourseList_NewCourse.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_CourseList_NewCourse.setVisible(false);
			this.button_CourseList_CourseSearchDialog.setVisible(false);
			this.button_CourseList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxCourse,this.pagingCourseList);
			// set the itemRenderer
			this.listBoxCourse.setItemRenderer(new CourseListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CourseList");
		
		this.button_CourseList_NewCourse.setVisible(getUserWorkspace().isAllowed("button_CourseList_NewCourse"));
		this.button_CourseList_CourseSearchDialog.setVisible(getUserWorkspace().isAllowed("button_CourseList_CourseFindDialog"));
		this.button_CourseList_PrintList.setVisible(getUserWorkspace().isAllowed("button_CourseList_PrintList"));
	logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.amtmaster.course.model.CourseListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onCourseItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected Course object
		final Listitem item = this.listBoxCourse.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Course aCourse = (Course) item.getAttribute("data");
			final Course course = getCourseService().getCourseById(aCourse.getId());
			
			if(course==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aCourse.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_CourseName")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND CourseName='"+ course.getCourseName()+"' AND version=" + course.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "Course", whereCond, course.getTaskId(), course.getNextTaskId());
					if (userAcces){
						showDetailView(course);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(course);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the Course dialog with a new empty entry. <br>
	 */
	public void onClick$button_CourseList_NewCourse(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new Course object, We GET it from the backend.
		final Course aCourse = getCourseService().getNewCourse();
		showDetailView(aCourse);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param Course (aCourse)
	 * @throws Exception
	 */
	private void showDetailView(Course aCourse) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aCourse.getWorkflowId()==0 && isWorkFlowEnabled()){
			aCourse.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("course", aCourse);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the CourseListbox from the
		 * dialog when we do a delete, edit or insert a Course.
		 */
		map.put("courseListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/AMTMasters/Course/CourseDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_CourseList);
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
		this.pagingCourseList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CourseList, event);
		this.window_CourseList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the Course dialog
	 */
	
	public void onClick$button_CourseList_CourseSearchDialog(Event event) throws Exception {
		logger.debug("Entering");
		logger.debug(event.toString());
		/*
		 * we can call our CourseDialog zul-file with parameters. So we can
		 * call them with a object of the selected Course. For handed over
		 * these parameter only a Map is accepted. So we put the Course object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("courseCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/AMTMasters/Course/CourseSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the course print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CourseList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		 new PTListReportUtils("Course", getSearchObj(),this.pagingCourseList.getTotalSize()+1);
		logger.debug("Leaving");
	}

	public void setCourseService(CourseService courseService) {
		this.courseService = courseService;
	}

	public CourseService getCourseService() {
		return this.courseService;
	}

	public JdbcSearchObject<Course> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<Course> searchObj) {
		this.searchObj = searchObj;
	}
}
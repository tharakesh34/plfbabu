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
 * FileName    		:  EducationalLoanListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2011    														*
 *                                                                  						*
 * Modified Date    :  08-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.educationalloan;

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
import com.pennant.backend.model.lmtmasters.EducationalLoan;
import com.pennant.backend.service.lmtmasters.EducationalLoanService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.lmtmasters.educationalloan.model.EducationalLoanListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/EducationalLoan/EducationalLoanList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class EducationalLoanListCtrl extends GFCBaseListCtrl<EducationalLoan> implements Serializable {

	private static final long serialVersionUID = -550147786114089286L;
	private final static Logger logger = Logger.getLogger(EducationalLoanListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_EducationalLoanList; 		// autowired
	protected Borderlayout 	borderLayout_EducationalLoanList; 	// autowired
	protected Paging 		pagingEducationalLoanList; 			// autowired
	protected Listbox 		listBoxEducationalLoan; 			// autowired

	// List headers
	protected Listheader listheader_LoanRefNumber; 		// autowired
	protected Listheader listheader_EduCourse; 			// autowired
	protected Listheader listheader_EduSpecialization; 	// autowired
	protected Listheader listheader_EduCourseType; 		// autowired
	protected Listheader listheader_EduCourseFromBranch;// autowired
	protected Listheader listheader_EduAffiliatedTo; 	// autowired
	protected Listheader listheader_EduCommenceDate; 	// autowired
	protected Listheader listheader_RecordStatus; 		// autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 												// autowired
	protected Button button_EducationalLoanList_NewEducationalLoan;			// autowired
	protected Button button_EducationalLoanList_EducationalLoanSearchDialog;// autowired
	protected Button button_EducationalLoanList_PrintList; 					// autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<EducationalLoan> searchObj;
	private transient EducationalLoanService educationalLoanService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public EducationalLoanListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected EducationalLoan object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_EducationalLoanList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("EducationalLoan");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("EducationalLoan");
			
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
		
		this.borderLayout_EducationalLoanList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingEducationalLoanList.setPageSize(getListRows());
		this.pagingEducationalLoanList.setDetailed(true);

		this.listheader_LoanRefNumber.setSortAscending(new FieldComparator("loanRefNumber", true));
		this.listheader_LoanRefNumber.setSortDescending(new FieldComparator("loanRefNumber", false));
		this.listheader_EduCourse.setSortAscending(new FieldComparator("eduCourse", true));
		this.listheader_EduCourse.setSortDescending(new FieldComparator("eduCourse", false));
		this.listheader_EduSpecialization.setSortAscending(new FieldComparator("eduSpecialization", true));
		this.listheader_EduSpecialization.setSortDescending(new FieldComparator("eduSpecialization", false));
		this.listheader_EduCourseType.setSortAscending(new FieldComparator("eduCourseType", true));
		this.listheader_EduCourseType.setSortDescending(new FieldComparator("eduCourseType", false));
		this.listheader_EduCourseFromBranch.setSortAscending(new FieldComparator("eduCourseFromBranch", true));
		this.listheader_EduCourseFromBranch.setSortDescending(new FieldComparator("eduCourseFromBranch", false));
		this.listheader_EduAffiliatedTo.setSortAscending(new FieldComparator("eduAffiliatedTo", true));
		this.listheader_EduAffiliatedTo.setSortDescending(new FieldComparator("eduAffiliatedTo", false));
		this.listheader_EduCommenceDate.setSortAscending(new FieldComparator("eduCommenceDate", true));
		this.listheader_EduCommenceDate.setSortDescending(new FieldComparator("eduCommenceDate", false));
		
		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}
		
		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<EducationalLoan>(EducationalLoan.class,getListRows());
		this.searchObj.addFilter(new Filter("RecordType", PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL));

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("LMTEducationLoanDetail_View");
			if (isFirstTask()) {
				button_EducationalLoanList_NewEducationalLoan.setVisible(false);
			} else {
				button_EducationalLoanList_NewEducationalLoan.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("LMTEducationLoanDetail_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_EducationalLoanList_NewEducationalLoan.setVisible(false);
			this.button_EducationalLoanList_EducationalLoanSearchDialog.setVisible(false);
			this.button_EducationalLoanList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxEducationalLoan,
					this.pagingEducationalLoanList);
			// set the itemRenderer
			this.listBoxEducationalLoan.setItemRenderer(new EducationalLoanListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("EducationalLoanList");
		
		this.button_EducationalLoanList_NewEducationalLoan.setVisible(false);//getUserWorkspace().isAllowed("button_EducationalLoanList_NewEducationalLoan")
		this.button_EducationalLoanList_EducationalLoanSearchDialog.setVisible(getUserWorkspace().
				isAllowed("button_EducationalLoanList_EducationalLoanFindDialog"));
		this.button_EducationalLoanList_PrintList.setVisible(getUserWorkspace().
				isAllowed("button_EducationalLoanList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.lmtmasters.educationalloan.model.
	 * EducationalLoanListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onEducationalLoanItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected EducationalLoan object
		final Listitem item = this.listBoxEducationalLoan.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final EducationalLoan aEducationalLoan = (EducationalLoan) item.getAttribute("data");
			final EducationalLoan educationalLoan = getEducationalLoanService().getEducationalLoanById(
					aEducationalLoan.getLoanRefNumber());
			
			if(educationalLoan==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aEducationalLoan.getLoanRefNumber());
				errParm[0]=PennantJavaUtil.getLabel("label_EduLoanId")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(
						PennantConstants.KEY_FIELD,"41005", errParm,valueParm), 
						getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND LoanRefNumber="+ educationalLoan.getLoanRefNumber()+
											" AND version=" + educationalLoan.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "EducationalLoan", 
							whereCond, educationalLoan.getTaskId(), educationalLoan.getNextTaskId());
					if (userAcces){
						showDetailView(educationalLoan);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(educationalLoan);
				}
			}	
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the EducationalLoan dialog with a new empty entry. <br>
	 */
	public void onClick$button_EducationalLoanList_NewEducationalLoan(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new EducationalLoan object, We GET it from the backEnd.
		final EducationalLoan aEducationalLoan = getEducationalLoanService().getNewEducationalLoan();
		showDetailView(aEducationalLoan);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param EducationalLoan (aEducationalLoan)
	 * @throws Exception
	 */
	private void showDetailView(EducationalLoan aEducationalLoan) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aEducationalLoan.getWorkflowId()==0 && isWorkFlowEnabled()){
			aEducationalLoan.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("educationalLoan", aEducationalLoan);
		map.put("educationalLoanListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/LMTMasters/EducationalLoan/EducationalLoanDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_EducationalLoanList);
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
		this.pagingEducationalLoanList.setActivePage(0);
		Events.postEvent("onCreate", this.window_EducationalLoanList, event);
		this.window_EducationalLoanList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for calling the EducationalLoan dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_EducationalLoanList_EducationalLoanSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		/*
		 * we can call our EducationalLoanDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected EducationalLoan. For handed over
		 * these parameter only a Map is accepted. So we put the EducationalLoan object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("educationalLoanCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/LMTMasters/EducationalLoan/EducationalLoanSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the educationalLoan print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_EducationalLoanList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTReportUtils.getReport("EducationalLoan", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//	
	
	public void setEducationalLoanService(EducationalLoanService educationalLoanService) {
		this.educationalLoanService = educationalLoanService;
	}
	public EducationalLoanService getEducationalLoanService() {
		return this.educationalLoanService;
	}

	public JdbcSearchObject<EducationalLoan> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<EducationalLoan> searchObj) {
		this.searchObj = searchObj;
	}
}
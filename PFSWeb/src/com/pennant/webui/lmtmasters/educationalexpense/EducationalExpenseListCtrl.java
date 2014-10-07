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
 * FileName    		:  EducationalExpenseListCtrl.java                                                   * 	  
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

package com.pennant.webui.lmtmasters.educationalexpense;


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
import com.pennant.backend.model.lmtmasters.EducationalExpense;
import com.pennant.backend.service.lmtmasters.EducationalExpenseService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.lmtmasters.educationalexpense.model.EducationalExpenseListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/LMTMasters/EducationalExpense/EducationalExpenseList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class EducationalExpenseListCtrl extends GFCBaseListCtrl<EducationalExpense> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(EducationalExpenseListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_EducationalExpenseList; // autowired
	protected Borderlayout borderLayout_EducationalExpenseList; // autowired
	protected Paging pagingEducationalExpenseList; // autowired
	protected Listbox listBoxEducationalExpense; // autowired

	// List headers
	protected Listheader listheader_EduLoanId; // autowired
	protected Listheader listheader_EduExpDetail; // autowired
	protected Listheader listheader_EduExpAmount; // autowired
	protected Listheader listheader_EduExpDate; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_EducationalExpenseList_NewEducationalExpense; // autowired
	protected Button button_EducationalExpenseList_EducationalExpenseSearchDialog; // autowired
	protected Button button_EducationalExpenseList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<EducationalExpense> searchObj;
	
	private transient EducationalExpenseService educationalExpenseService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public EducationalExpenseListCtrl() {
		super();
	}

	public void onCreate$window_EducationalExpenseList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("EducationalExpense");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("EducationalExpense");
			
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
		
		this.borderLayout_EducationalExpenseList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingEducationalExpenseList.setPageSize(getListRows());
		this.pagingEducationalExpenseList.setDetailed(true);

		this.listheader_EduLoanId.setSortAscending(new FieldComparator("eduLoanId", true));
		this.listheader_EduLoanId.setSortDescending(new FieldComparator("eduLoanId", false));
		this.listheader_EduExpDetail.setSortAscending(new FieldComparator("eduExpDetail", true));
		this.listheader_EduExpDetail.setSortDescending(new FieldComparator("eduExpDetail", false));
		this.listheader_EduExpAmount.setSortAscending(new FieldComparator("eduExpAmount", true));
		this.listheader_EduExpAmount.setSortDescending(new FieldComparator("eduExpAmount", false));
		this.listheader_EduExpDate.setSortAscending(new FieldComparator("eduExpDate", true));
		this.listheader_EduExpDate.setSortDescending(new FieldComparator("eduExpDate", false));
		
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
		this.searchObj = new JdbcSearchObject<EducationalExpense>(EducationalExpense.class,getListRows());
		this.searchObj.addSort("EduExpDetailId", false);

		this.searchObj.addTabelName("LMTEduExpenseDetail_View");
		
		// Workflow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_EducationalExpenseList_NewEducationalExpense.setVisible(true);
			} else {
				button_EducationalExpenseList_NewEducationalExpense.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_EducationalExpenseList_NewEducationalExpense.setVisible(false);
			this.button_EducationalExpenseList_EducationalExpenseSearchDialog.setVisible(false);
			this.button_EducationalExpenseList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxEducationalExpense,this.pagingEducationalExpenseList);
			// set the itemRenderer
			this.listBoxEducationalExpense.setItemRenderer(new EducationalExpenseListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("EducationalExpenseList");
		
		this.button_EducationalExpenseList_NewEducationalExpense.setVisible(getUserWorkspace().isAllowed("button_EducationalExpenseList_NewEducationalExpense"));
		this.button_EducationalExpenseList_EducationalExpenseSearchDialog.setVisible(getUserWorkspace().isAllowed("button_EducationalExpenseList_EducationalExpenseFindDialog"));
		this.button_EducationalExpenseList_PrintList.setVisible(getUserWorkspace().isAllowed("button_EducationalExpenseList_PrintList"));
	logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.lmtmasters.educationalexpense.model.EducationalExpenseListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onEducationalExpenseItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected EducationalExpense object
		final Listitem item = this.listBoxEducationalExpense.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final EducationalExpense aEducationalExpense = (EducationalExpense) item.getAttribute("data");
			final EducationalExpense educationalExpense = getEducationalExpenseService()
			.getEducationalExpenseById(aEducationalExpense.getLoanRefNumber(),aEducationalExpense.getEduExpDetail());
			
			if(educationalExpense==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aEducationalExpense.getId());
				errParm[0]=PennantJavaUtil.getLabel("label_EduExpDetailId")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND EduExpDetail="+ educationalExpense.getEduExpDetail()+" AND version=" + educationalExpense.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "EducationalExpense", whereCond, educationalExpense.getTaskId(), educationalExpense.getNextTaskId());
					if (userAcces){
						showDetailView(educationalExpense);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(educationalExpense);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the EducationalExpense dialog with a new empty entry. <br>
	 */
	public void onClick$button_EducationalExpenseList_NewEducationalExpense(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new EducationalExpense object, We GET it from the backend.
		final EducationalExpense aEducationalExpense = getEducationalExpenseService().getNewEducationalExpense();
		showDetailView(aEducationalExpense);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param EducationalExpense (aEducationalExpense)
	 * @throws Exception
	 */
	private void showDetailView(EducationalExpense aEducationalExpense) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aEducationalExpense.getWorkflowId()==0 && isWorkFlowEnabled()){
			aEducationalExpense.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("educationalExpense", aEducationalExpense);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the EducationalExpenseListbox from the
		 * dialog when we do a delete, edit or insert a EducationalExpense.
		 */
		map.put("educationalExpenseListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/LMTMasters/EducationalExpense/EducationalExpenseDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_EducationalExpenseList);
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
		this.pagingEducationalExpenseList.setActivePage(0);
		Events.postEvent("onCreate", this.window_EducationalExpenseList, event);
		this.window_EducationalExpenseList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the EducationalExpense dialog
	 */
	
	public void onClick$button_EducationalExpenseList_EducationalExpenseSearchDialog(Event event) throws Exception {
		logger.debug("Entering");
		logger.debug(event.toString());
		/*
		 * we can call our EducationalExpenseDialog zul-file with parameters. So we can
		 * call them with a object of the selected EducationalExpense. For handed over
		 * these parameter only a Map is accepted. So we put the EducationalExpense object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("educationalExpenseCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/LMTMasters/EducationalExpense/EducationalExpenseSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the educationalExpense print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_EducationalExpenseList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		new PTListReportUtils("EducationalExpense", getSearchObj(),this.pagingEducationalExpenseList.getTotalSize()+1);
		logger.debug("Leaving");
	}

	public void setEducationalExpenseService(EducationalExpenseService educationalExpenseService) {
		this.educationalExpenseService = educationalExpenseService;
	}

	public EducationalExpenseService getEducationalExpenseService() {
		return this.educationalExpenseService;
	}

	public JdbcSearchObject<EducationalExpense> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<EducationalExpense> searchObj) {
		this.searchObj = searchObj;
	}
}
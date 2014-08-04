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
 * FileName    		:  FinCreditRevSubCategoryListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-11-2013    														*
 *                                                                  						*
 * Modified Date    :  13-11-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-11-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.fincreditrevsubcategory;


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
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.service.customermasters.FinCreditRevSubCategoryService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.customermasters.fincreditrevsubcategory.model.FinCreditRevSubCategoryListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Customers/FinCreditRevSubCategory/FinCreditRevSubCategoryList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class FinCreditRevSubCategoryListCtrl extends GFCBaseListCtrl<FinCreditRevSubCategory> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(FinCreditRevSubCategoryListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FinCreditRevSubCategoryList; // autowired
	protected Borderlayout borderLayout_FinCreditRevSubCategoryList; // autowired
	protected Paging pagingFinCreditRevSubCategoryList; // autowired
	protected Listbox listBoxFinCreditRevSubCategory; // autowired

	// List headers
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_FinCreditRevSubCategoryList_NewFinCreditRevSubCategory; // autowired
	protected Button button_FinCreditRevSubCategoryList_FinCreditRevSubCategorySearchDialog; // autowired
	protected Button button_FinCreditRevSubCategoryList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<FinCreditRevSubCategory> searchObj;
	
	private transient FinCreditRevSubCategoryService finCreditRevSubCategoryService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public FinCreditRevSubCategoryListCtrl() {
		super();
	}

	public void onCreate$window_FinCreditRevSubCategoryList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("FinCreditRevSubCategory");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinCreditRevSubCategory");
			
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
		
		this.borderLayout_FinCreditRevSubCategoryList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingFinCreditRevSubCategoryList.setPageSize(getListRows());
		this.pagingFinCreditRevSubCategoryList.setDetailed(true);

		
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
		this.searchObj = new JdbcSearchObject<FinCreditRevSubCategory>(FinCreditRevSubCategory.class,getListRows());
		this.searchObj.addSort("SubCategoryCode", false);
		// Workflow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("FinCreditRevSubCategory_View");
			if (isFirstTask()) {
				button_FinCreditRevSubCategoryList_NewFinCreditRevSubCategory.setVisible(true);
			} else {
				button_FinCreditRevSubCategoryList_NewFinCreditRevSubCategory.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("FinCreditRevSubCategory_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_FinCreditRevSubCategoryList_NewFinCreditRevSubCategory.setVisible(false);
			this.button_FinCreditRevSubCategoryList_FinCreditRevSubCategorySearchDialog.setVisible(false);
			this.button_FinCreditRevSubCategoryList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxFinCreditRevSubCategory,this.pagingFinCreditRevSubCategoryList);
			// set the itemRenderer
			this.listBoxFinCreditRevSubCategory.setItemRenderer(new FinCreditRevSubCategoryListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("FinCreditRevSubCategoryList");
		
		//this.button_FinCreditRevSubCategoryList_NewFinCreditRevSubCategory.setVisible(getUserWorkspace().isAllowed("button_FinCreditRevSubCategoryList_NewFinCreditRevSubCategory"));
		//this.button_FinCreditRevSubCategoryList_FinCreditRevSubCategorySearchDialog.setVisible(getUserWorkspace().isAllowed("button_FinCreditRevSubCategoryList_FinCreditRevSubCategoryFindDialog"));
		//this.button_FinCreditRevSubCategoryList_PrintList.setVisible(getUserWorkspace().isAllowed("button_FinCreditRevSubCategoryList_PrintList"));
	logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.customers.fincreditrevsubcategory.model.FinCreditRevSubCategoryListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onFinCreditRevSubCategoryItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected FinCreditRevSubCategory object
		final Listitem item = this.listBoxFinCreditRevSubCategory.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinCreditRevSubCategory aFinCreditRevSubCategory = (FinCreditRevSubCategory) item.getAttribute("data");
			final FinCreditRevSubCategory finCreditRevSubCategory = getFinCreditRevSubCategoryService().getFinCreditRevSubCategoryById(aFinCreditRevSubCategory.getId());
			
			if(finCreditRevSubCategory==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinCreditRevSubCategory.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_SubCategoryCode")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND SubCategoryCode='"+ finCreditRevSubCategory.getSubCategoryCode()+"' AND version=" + finCreditRevSubCategory.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "FinCreditRevSubCategory", whereCond, finCreditRevSubCategory.getTaskId(), finCreditRevSubCategory.getNextTaskId());
					if (userAcces){
						showDetailView(finCreditRevSubCategory);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(finCreditRevSubCategory);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the FinCreditRevSubCategory dialog with a new empty entry. <br>
	 */
	public void onClick$button_FinCreditRevSubCategoryList_NewFinCreditRevSubCategory(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new FinCreditRevSubCategory object, We GET it from the backend.
		final FinCreditRevSubCategory aFinCreditRevSubCategory = getFinCreditRevSubCategoryService().getNewFinCreditRevSubCategory();
		showDetailView(aFinCreditRevSubCategory);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param FinCreditRevSubCategory (aFinCreditRevSubCategory)
	 * @throws Exception
	 */
	private void showDetailView(FinCreditRevSubCategory aFinCreditRevSubCategory) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aFinCreditRevSubCategory.getWorkflowId()==0 && isWorkFlowEnabled()){
			aFinCreditRevSubCategory.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finCreditRevSubCategory", aFinCreditRevSubCategory);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the FinCreditRevSubCategoryListbox from the
		 * dialog when we do a delete, edit or insert a FinCreditRevSubCategory.
		 */
		map.put("finCreditRevSubCategoryListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/FinCreditRevSubCategory/FinCreditRevSubCategoryDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_FinCreditRevSubCategoryList);
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
		this.pagingFinCreditRevSubCategoryList.setActivePage(0);
		Events.postEvent("onCreate", this.window_FinCreditRevSubCategoryList, event);
		this.window_FinCreditRevSubCategoryList.invalidate();
		logger.debug("Leaving");
	}

	
	/*
	 * call the FinCreditRevSubCategory dialog
	 */
	
	public void onClick$button_FinCreditRevSubCategoryList_FinCreditRevSubCategorySearchDialog(Event event) throws Exception {
		logger.debug("Entering");
		logger.debug(event.toString());
		/*
		 * we can call our FinCreditRevSubCategoryDialog zul-file with parameters. So we can
		 * call them with a object of the selected FinCreditRevSubCategory. For handed over
		 * these parameter only a Map is accepted. So we put the FinCreditRevSubCategory object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finCreditRevSubCategoryCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/FinCreditRevSubCategory/FinCreditRevSubCategorySearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the finCreditRevSubCategory print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_FinCreditRevSubCategoryList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		PTReportUtils.getReport("FinCreditRevSubCategory", getSearchObj());
		logger.debug("Leaving");
	}

	public void setFinCreditRevSubCategoryService(FinCreditRevSubCategoryService finCreditRevSubCategoryService) {
		this.finCreditRevSubCategoryService = finCreditRevSubCategoryService;
	}

	public FinCreditRevSubCategoryService getFinCreditRevSubCategoryService() {
		return this.finCreditRevSubCategoryService;
	}

	public JdbcSearchObject<FinCreditRevSubCategory> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<FinCreditRevSubCategory> searchObj) {
		this.searchObj = searchObj;
	}
}
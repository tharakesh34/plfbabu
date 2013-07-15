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
 * FileName    		:  BasicFinanceTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.basicfinancetype;

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
import com.pennant.backend.model.rmtmasters.BasicFinanceType;
import com.pennant.backend.service.rmtmasters.BasicFinanceTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.rmtmasters.basicfinancetype.model.BasicFinanceTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/RMTMasters/BasicFinanceType/BasicFinanceTypeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class BasicFinanceTypeListCtrl extends GFCBaseListCtrl<BasicFinanceType>
		implements Serializable {

	private static final long serialVersionUID = -7760917091140872565L;
	private final static Logger logger = Logger.getLogger(BasicFinanceTypeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_BasicFinanceTypeList; 		// auto wired
	protected Borderlayout 	borderLayout_BasicFinanceTypeList; 	// auto wired
	protected Paging 		pagingBasicFinanceTypeList; 		// auto wired
	protected Listbox 		listBoxBasicFinanceType; 			// auto wired

	// List headers
	protected Listheader listheader_FinBasicType; // auto wired
	protected Listheader listheader_FinBasicDesc; // auto wired
	protected Listheader listheader_RecordStatus; // auto wired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 													// auto wired
	protected Button button_BasicFinanceTypeList_NewBasicFinanceType; 			// auto wired
	protected Button button_BasicFinanceTypeList_BasicFinanceTypeSearchDialog; 	// auto wired
	protected Button button_BasicFinanceTypeList_PrintList; 					// auto wired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<BasicFinanceType> searchObj;
	
	private transient BasicFinanceTypeService basicFinanceTypeService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public BasicFinanceTypeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected BasicFinanceType
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_BasicFinanceTypeList(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("BasicFinanceType");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("BasicFinanceType");
			
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
		
		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the list box. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_BasicFinanceTypeList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingBasicFinanceTypeList.setPageSize(getListRows());
		this.pagingBasicFinanceTypeList.setDetailed(true);

		this.listheader_FinBasicType.setSortAscending(
				new FieldComparator("finBasicType", true));
		this.listheader_FinBasicType.setSortDescending(
				new FieldComparator("finBasicType", false));
		this.listheader_FinBasicDesc.setSortAscending(
				new FieldComparator("finBasicDesc", true));
		this.listheader_FinBasicDesc.setSortDescending(
				new FieldComparator("finBasicDesc", false));
		
		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(
					new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(
					new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(
					new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(
					new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}
		
		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<BasicFinanceType>(
				BasicFinanceType.class,getListRows());
		this.searchObj.addSort("FinBasicType", false);

		this.searchObj.addTabelName("RMTBasicFinanceTypes_View");
		
		// Work flow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_BasicFinanceTypeList_NewBasicFinanceType.setVisible(true);
			} else {
				button_BasicFinanceTypeList_NewBasicFinanceType.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),
					isFirstTask());
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_BasicFinanceTypeList_NewBasicFinanceType.setVisible(false);
			this.button_BasicFinanceTypeList_BasicFinanceTypeSearchDialog.setVisible(false);
			this.button_BasicFinanceTypeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(
					PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,
					this.listBoxBasicFinanceType,this.pagingBasicFinanceTypeList);
			// set the itemRenderer
			this.listBoxBasicFinanceType.setItemRenderer(
					new BasicFinanceTypeListModelItemRenderer());
		}	
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		
		getUserWorkspace().alocateAuthorities("BasicFinanceTypeList");
		
		this.button_BasicFinanceTypeList_NewBasicFinanceType.setVisible(getUserWorkspace()
				.isAllowed("button_BasicFinanceTypeList_NewBasicFinanceType"));
		this.button_BasicFinanceTypeList_BasicFinanceTypeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_BasicFinanceTypeList_BasicFinanceTypeFindDialog"));
		this.button_BasicFinanceTypeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_BasicFinanceTypeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.rmtmasters.basicfinancetype.model.BasicFinanceTypeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onBasicFinanceTypeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		
		// get the selected BasicFinanceType object
		final Listitem item = this.listBoxBasicFinanceType.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final BasicFinanceType aBasicFinanceType = (BasicFinanceType) item.getAttribute("data");
			final BasicFinanceType basicFinanceType = getBasicFinanceTypeService().getBasicFinanceTypeById(aBasicFinanceType.getId());
			if(basicFinanceType==null){

				String[] valueParm = new String[1];
				String[] errParm= new String[1];

				valueParm[0] = aBasicFinanceType.getFinBasicType();
				errParm[0] = PennantJavaUtil.getLabel("label_FinBasicType") + ":"+ valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				String whereCond =  " AND FinBasicType='"+ basicFinanceType.getFinBasicType()+"' AND version=" + basicFinanceType.getVersion()+" ";

				if(isWorkFlowEnabled()){
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "BasicFinanceType", whereCond, basicFinanceType.getTaskId(), basicFinanceType.getNextTaskId());
					if (userAcces){
						showDetailView(basicFinanceType);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(basicFinanceType);
				}
			}
		}
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Call the BasicFinanceType dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_BasicFinanceTypeList_NewBasicFinanceType(
			Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		
		// create a new BasicFinanceType object, We GET it from the back end.
		final BasicFinanceType aBasicFinanceType = getBasicFinanceTypeService()
				.getNewBasicFinanceType();
		showDetailView(aBasicFinanceType);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param BasicFinanceType (aBasicFinanceType)
	 * @throws Exception
	 */
	private void showDetailView(BasicFinanceType aBasicFinanceType) throws Exception {
		logger.debug("Entering");
		
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aBasicFinanceType.getWorkflowId()==0 && isWorkFlowEnabled()){
			aBasicFinanceType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("basicFinanceType", aBasicFinanceType);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the BasicFinanceTypeListbox from the
		 * dialog when we do a delete, edit or insert a BasicFinanceType.
		 */
		map.put("basicFinanceTypeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/RMTMasters/BasicFinanceType/BasicFinanceTypeDialog.zul",
							null,map);
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
		logger.debug("Entering" +event.toString());
		PTMessageUtils.showHelpWindow(event, window_BasicFinanceTypeList);
		logger.debug("Leaving" +event.toString());
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
		logger.debug("Entering" +event.toString());
		this.pagingBasicFinanceTypeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_BasicFinanceTypeList, event);
		this.window_BasicFinanceTypeList.invalidate();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * call the BasicFinanceType dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_BasicFinanceTypeList_BasicFinanceTypeSearchDialog(
			Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		
		/*
		 * we can call our BasicFinanceTypeDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected BasicFinanceType. For handed over
		 * these parameter only a Map is accepted. So we put the BasicFinanceType object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("basicFinanceTypeCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/RMTMasters/BasicFinanceType/BasicFinanceTypeSearchDialog.zul",
							null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * When the basicFinanceType print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_BasicFinanceTypeList_PrintList(Event event)
			throws InterruptedException {
		logger.debug("Entering" +event.toString());
		PTReportUtils.getReport("BasicFinanceType", getSearchObj());
		logger.debug("Leaving" +event.toString());
	}


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setBasicFinanceTypeService(BasicFinanceTypeService basicFinanceTypeService) {
		this.basicFinanceTypeService = basicFinanceTypeService;
	}
	public BasicFinanceTypeService getBasicFinanceTypeService() {
		return this.basicFinanceTypeService;
	}

	public JdbcSearchObject<BasicFinanceType> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<BasicFinanceType> searchObj) {
		this.searchObj = searchObj;
	}
}
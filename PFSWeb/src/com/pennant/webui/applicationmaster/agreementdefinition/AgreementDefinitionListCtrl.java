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
 * FileName    		:  AgreementDefinitionListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-11-2011    														*
 *                                                                  						*
 * Modified Date    :  23-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.agreementdefinition;

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
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.service.applicationmaster.AgreementDefinitionService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.applicationmaster.agreementdefinition.model.AgreementDefinitionListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/AgreementDefinition/AgreementDefinitionList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class AgreementDefinitionListCtrl extends GFCBaseListCtrl<AgreementDefinition> implements Serializable {

	private static final long serialVersionUID = 1225118639931508378L;
	private final static Logger logger = Logger.getLogger(AgreementDefinitionListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_AgreementDefinitionList; 		// autoWired
	protected Borderlayout 	borderLayout_AgreementDefinitionList; 	// autoWired
	protected Paging 		pagingAgreementDefinitionList; 			// autoWired
	protected Listbox 		listBoxAgreementDefinition; 			// autoWired

	// List headers
	protected Listheader listheader_AggCode; 		// autoWired
	protected Listheader listheader_AggName; 		// autoWired
	protected Listheader listheader_AggDesc; 		// autoWired
	protected Listheader listheader_AggIsActive; 	// autoWired
	protected Listheader listheader_RecordStatus; 	// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 															// autoWired
	protected Button button_AgreementDefinitionList_NewAgreementDefinition; 			// autoWired
	protected Button button_AgreementDefinitionList_AgreementDefinitionSearchDialog; 	// autoWired
	protected Button button_AgreementDefinitionList_PrintList; 							// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<AgreementDefinition> searchObj;

	private transient AgreementDefinitionService agreementDefinitionService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public AgreementDefinitionListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected AddressType object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AgreementDefinitionList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("AgreementDefinition");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("AgreementDefinition");

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

		this.borderLayout_AgreementDefinitionList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingAgreementDefinitionList.setPageSize(getListRows());
		this.pagingAgreementDefinitionList.setDetailed(true);

		this.listheader_AggCode.setSortAscending(new FieldComparator("aggCode", true));
		this.listheader_AggCode.setSortDescending(new FieldComparator("aggCode", false));
		this.listheader_AggName.setSortAscending(new FieldComparator("aggName", true));
		this.listheader_AggName.setSortDescending(new FieldComparator("aggName", false));
		this.listheader_AggDesc.setSortAscending(new FieldComparator("aggDesc", true));
		this.listheader_AggDesc.setSortDescending(new FieldComparator("aggDesc", false));
		this.listheader_AggIsActive.setSortAscending(new FieldComparator("aggIsActive", true));
		this.listheader_AggIsActive.setSortDescending(new FieldComparator("aggIsActive", false));

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
		this.searchObj = new JdbcSearchObject<AgreementDefinition>(AgreementDefinition.class,getListRows());
		this.searchObj.addSort("AggCode", false);

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTAggrementDef_View");
			if (isFirstTask()) {
				button_AgreementDefinitionList_NewAgreementDefinition.setVisible(true);
			} else {
				button_AgreementDefinitionList_NewAgreementDefinition.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("BMTAggrementDef_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_AgreementDefinitionList_NewAgreementDefinition.setVisible(false);
			this.button_AgreementDefinitionList_AgreementDefinitionSearchDialog.setVisible(false);
			this.button_AgreementDefinitionList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxAgreementDefinition,this.pagingAgreementDefinitionList);
			// set the itemRenderer
			this.listBoxAgreementDefinition.setItemRenderer(new AgreementDefinitionListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("AgreementDefinitionList");

		this.button_AgreementDefinitionList_NewAgreementDefinition.setVisible(getUserWorkspace().isAllowed("button_AgreementDefinitionList_NewAgreementDefinition"));
		this.button_AgreementDefinitionList_AgreementDefinitionSearchDialog.setVisible(getUserWorkspace().isAllowed("button_AgreementDefinitionList_AgreementDefinitionFindDialog"));
		this.button_AgreementDefinitionList_PrintList.setVisible(getUserWorkspace().isAllowed("button_AgreementDefinitionList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.agreementdefinition.model.AgreementDefinitionListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onAgreementDefinitionItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected AgreementDefinition object
		final Listitem item = this.listBoxAgreementDefinition.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final AgreementDefinition aAgreementDefinition = (AgreementDefinition) item.getAttribute("data");
			final AgreementDefinition agreementDefinition = getAgreementDefinitionService().getAgreementDefinitionById(aAgreementDefinition.getId());

			if(agreementDefinition==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aAgreementDefinition.getAggCode();
				errParm[0]=PennantJavaUtil.getLabel("label_AggCode")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND AggCode='"+ agreementDefinition.getAggCode()+"' AND version=" + agreementDefinition.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "AgreementDefinition", whereCond, agreementDefinition.getTaskId(), agreementDefinition.getNextTaskId());
					if (userAcces){
						showDetailView(agreementDefinition);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(agreementDefinition);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the AgreementDefinition dialog with a new empty entry. <br>
	 */
	public void onClick$button_AgreementDefinitionList_NewAgreementDefinition(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new AgreementDefinition object, We GET it from the backEnd.
		final AgreementDefinition aAgreementDefinition = getAgreementDefinitionService().getNewAgreementDefinition();
		showDetailView(aAgreementDefinition);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param AgreementDefinition (aAgreementDefinition)
	 * @throws Exception
	 */
	private void showDetailView(AgreementDefinition aAgreementDefinition) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if(aAgreementDefinition.getWorkflowId()==0 && isWorkFlowEnabled()){
			aAgreementDefinition.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("agreementDefinition", aAgreementDefinition);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the AgreementDefinitionListbox from the
		 * dialog when we do a delete, edit or insert a AgreementDefinition.
		 */
		map.put("agreementDefinitionListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/AgreementDefinition/AgreementDefinitionDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_AgreementDefinitionList);
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
		logger.debug("Entering" + event.toString());
		this.pagingAgreementDefinitionList.setActivePage(0);
		Events.postEvent("onCreate", this.window_AgreementDefinitionList, event);
		this.window_AgreementDefinitionList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the AgreementDefinition dialog
	 */

	public void onClick$button_AgreementDefinitionList_AgreementDefinitionSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/*
		 * we can call our AgreementDefinitionDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected AgreementDefinition. For handed over
		 * these parameter only a Map is accepted. So we put the AgreementDefinition object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("agreementDefinitionCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/AgreementDefinition/AgreementDefinitionSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the agreementDefinition print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_AgreementDefinitionList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("AgreementDefinition", getSearchObj(),this.pagingAgreementDefinitionList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setAgreementDefinitionService(AgreementDefinitionService agreementDefinitionService) {
		this.agreementDefinitionService = agreementDefinitionService;
	}

	public AgreementDefinitionService getAgreementDefinitionService() {
		return this.agreementDefinitionService;
	}

	public JdbcSearchObject<AgreementDefinition> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<AgreementDefinition> searchObj) {
		this.searchObj = searchObj;
	}
}
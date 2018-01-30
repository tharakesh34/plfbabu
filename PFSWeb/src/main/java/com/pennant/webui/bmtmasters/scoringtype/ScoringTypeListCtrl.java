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
 * FileName    		:  ScoringTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-11-2011    														*
 *                                                                  						*
 * Modified Date    :  08-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-11-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.bmtmasters.scoringtype;

import java.util.Map;

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
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.bmtmasters.ScoringType;
import com.pennant.backend.service.bmtmasters.ScoringTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.bmtmasters.scoringtype.model.ScoringTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.PTListReportUtils;

/**
 * This is the controller class for the
 * /WEB-INF/pages/RulesFactory/ScoringType/ScoringTypeList.zul file.
 */
public class ScoringTypeListCtrl extends GFCBaseListCtrl<ScoringType> {
	private static final long serialVersionUID = 8118174179738209910L;
	private static final Logger logger = Logger.getLogger(ScoringTypeListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_ScoringTypeList; 		            // autoWired
	protected Borderlayout	borderLayout_ScoringTypeList;            	// autoWired
	protected Paging 		pagingScoringTypeList; 		             	// autoWired
	protected Listbox 		listBoxScoringType; 			            // autoWired

	// List headers
	protected Listheader listheader_ScoType; 		                    // autoWired
	protected Listheader listheader_ScoDesc; 		                    // autoWired
	protected Listheader listheader_RecordStatus; 	                    // autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 											// autoWired
	protected Button button_ScoringTypeList_NewScoringType; 			// autoWired
	protected Button button_ScoringTypeList_ScoringTypeSearchDialog; 	// autoWired
	protected Button button_ScoringTypeList_PrintList; 					// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<ScoringType> searchObj;
	private transient ScoringTypeService scoringTypeService;

	/**
	 * default constructor.<br>
	 */
	public ScoringTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		moduleCode = "ScoringType";
	}

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected ScoringType object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ScoringTypeList(Event event) throws Exception {
		logger.debug("Entering");
		
		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_ScoringTypeList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingScoringTypeList.setPageSize(getListRows());
		this.pagingScoringTypeList.setDetailed(true);

		this.listheader_ScoType.setSortAscending(new FieldComparator("scoType", true));
		this.listheader_ScoType.setSortDescending(new FieldComparator("scoType", false));
		this.listheader_ScoDesc.setSortAscending(new FieldComparator("scoDesc", true));
		this.listheader_ScoDesc.setSortDescending(new FieldComparator("scoDesc", false));

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
		this.searchObj = new JdbcSearchObject<ScoringType>(ScoringType.class,getListRows());
		this.searchObj.addSort("ScoType", false);
		
		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTScoringType_View");
			if (isFirstTask()) {
				button_ScoringTypeList_NewScoringType.setVisible(true);
			} else {
				button_ScoringTypeList_NewScoringType.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("BMTScoringType_AView");
		}

		setSearchObj(this.searchObj);
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxScoringType,this.pagingScoringTypeList);
		// set the itemRenderer
		this.listBoxScoringType.setItemRenderer(new ScoringTypeListModelItemRenderer());
					
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("ScoringTypeList");

		this.button_ScoringTypeList_NewScoringType.setVisible(getUserWorkspace()
				.isAllowed("button_ScoringTypeList_NewScoringType"));
		this.button_ScoringTypeList_ScoringTypeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_ScoringTypeList_ScoringTypeFindDialog"));
		this.button_ScoringTypeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_ScoringTypeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.scoringtype.model.ScoringTypeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onScoringTypeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected ScoringType object
		final Listitem item = this.listBoxScoringType.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final ScoringType aScoringType = (ScoringType) item.getAttribute("data");
			final ScoringType scoringType = getScoringTypeService().getScoringTypeById(aScoringType.getId());

			if(scoringType==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aScoringType.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_ScoType")+":"+valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD,"41005", errParm,valueParm),
						getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND ScoType='"+ scoringType.getScoType()+"' AND version=" + scoringType.getVersion()+" ";

					boolean userAcces =  validateUserAccess(scoringType.getWorkflowId(),
							getUserWorkspace().getLoggedInUser().getUserId(), "ScoringType", 
							whereCond, scoringType.getTaskId(), scoringType.getNextTaskId());
					if (userAcces){
						showDetailView(scoringType);
					}else{
						MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(scoringType);
				}
			}	
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the ScoringType dialog with a new empty entry. <br>
	 */
	public void onClick$button_ScoringTypeList_NewScoringType(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new ScoringType object, We GET it from the backEnd.
		final ScoringType aScoringType = getScoringTypeService().getNewScoringType();
		showDetailView(aScoringType);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param ScoringType (aScoringType)
	 * @throws Exception
	 */
	private void showDetailView(ScoringType aScoringType) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aScoringType.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aScoringType.setWorkflowId(getWorkFlowId());
		}

		Map<String, Object> map = getDefaultArguments();
		map.put("scoringType", aScoringType);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the ScoringTypeListbox from the
		 * dialog when we do a delete, edit or insert a ScoringType.
		 */
		map.put("scoringTypeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/RulesFactory/ScoringType/ScoringTypeDialog.zul",null,map);
		} catch (Exception e) {
			MessageUtil.showError(e);
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
		MessageUtil.showHelpWindow(event, window_ScoringTypeList);
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
		this.pagingScoringTypeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_ScoringTypeList, event);
		this.window_ScoringTypeList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for calling the ScoringType dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_ScoringTypeList_ScoringTypeSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/*
		 * we can call our ScoringTypeDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected ScoringType. For handed over
		 * these parameter only a Map is accepted. So we put the ScoringType object
		 * in a HashMap.
		 */
		Map<String, Object> map = getDefaultArguments();
		map.put("scoringTypeCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/RulesFactory/ScoringType/ScoringTypeSearchDialog.zul",null,map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the scoringType print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_ScoringTypeList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		new PTListReportUtils("ScoringType", getSearchObj(),this.pagingScoringTypeList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setScoringTypeService(ScoringTypeService scoringTypeService) {
		this.scoringTypeService = scoringTypeService;
	}
	public ScoringTypeService getScoringTypeService() {
		return this.scoringTypeService;
	}

	public JdbcSearchObject<ScoringType> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<ScoringType> searchObj) {
		this.searchObj = searchObj;
	}
}
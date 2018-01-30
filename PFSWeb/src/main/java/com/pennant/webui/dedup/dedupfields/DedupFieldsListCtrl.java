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
 * FileName    		:  DedupFieldsListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-08-2011    														*
 *                                                                  						*
 * Modified Date    :  23-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-08-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.dedup.dedupfields;

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
import com.pennant.backend.model.dedup.DedupFields;
import com.pennant.backend.service.dedup.DedupFieldsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.dedup.dedupfields.model.DedupFieldsListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.PTListReportUtils;

/**
 * This is the controller class for the /WEB-INF/pages/Dedup/DedupFields/DedupFieldsList.zul
 * file.
 */
public class DedupFieldsListCtrl extends GFCBaseListCtrl<DedupFields> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(DedupFieldsListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DedupFieldsList; // autowired
	protected Borderlayout borderLayout_DedupFieldsList; // autowired
	protected Paging pagingDedupFieldsList; // autowired
	protected Listbox listBoxDedupFields; // autowired

	// List headers
	protected Listheader listheader_FieldName; // autowired
	protected Listheader listheader_FieldControl; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_DedupFieldsList_NewDedupFields; // autowired
	protected Button button_DedupFieldsList_DedupFieldsSearchDialog; // autowired
	protected Button button_DedupFieldsList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<DedupFields> searchObj;
	
	private transient DedupFieldsService dedupFieldsService;
	
	/**
	 * default constructor.<br>
	 */
	public DedupFieldsListCtrl() {
		super();
	}
	
	@Override
	protected void doSetProperties() {
		moduleCode = "DedupFields";
	}

	public void onCreate$window_DedupFieldsList(Event event) throws Exception {
		logger.debug("Enterring");
			
		/* set components visible dependent on the users rights */
		doCheckRights();
		
		this.borderLayout_DedupFieldsList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingDedupFieldsList.setPageSize(getListRows());
		this.pagingDedupFieldsList.setDetailed(true);

		this.listheader_FieldName.setSortAscending(new FieldComparator("fieldName", true));
		this.listheader_FieldName.setSortDescending(new FieldComparator("fieldName", false));
		this.listheader_FieldControl.setSortAscending(new FieldComparator("fieldControl", true));
		this.listheader_FieldControl.setSortDescending(new FieldComparator("fieldControl", false));
		
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
		this.searchObj = new JdbcSearchObject<DedupFields>(DedupFields.class,getListRows());
		this.searchObj.addSort("FieldName", false);

		this.searchObj.addTabelName("DedupFields_View");
		
		// Workflow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_DedupFieldsList_NewDedupFields.setVisible(true);
			} else {
				button_DedupFieldsList_NewDedupFields.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}

		setSearchObj(this.searchObj);
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxDedupFields, this.pagingDedupFieldsList);
		// set the itemRenderer
		this.listBoxDedupFields.setItemRenderer(new DedupFieldsListModelItemRenderer());
					
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Enterring");
		getUserWorkspace().allocateAuthorities("DedupFieldsList");
		
		this.button_DedupFieldsList_NewDedupFields.setVisible(getUserWorkspace().isAllowed("button_DedupFieldsList_NewDedupFields"));
		this.button_DedupFieldsList_DedupFieldsSearchDialog.setVisible(getUserWorkspace().isAllowed("button_DedupFieldsList_DedupFieldsFindDialog"));
		this.button_DedupFieldsList_PrintList.setVisible(getUserWorkspace().isAllowed("button_DedupFieldsList_PrintList"));
	logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.dedup.dedupfields.model.DedupFieldsListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onDedupFieldsItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected DedupFields object
		final Listitem item = this.listBoxDedupFields.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final DedupFields aDedupFields = (DedupFields) item.getAttribute("data");
			final DedupFields dedupFields = getDedupFieldsService().getDedupFieldsById(aDedupFields.getId());
			
			if(dedupFields==null){
				String[] parm= new String[1];
				String[] valueParm = new String[1];
				parm[0]=PennantJavaUtil.getLabel("label_FieldName")+aDedupFields.getFieldName();
				valueParm[0] = aDedupFields.getFieldName();
				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005",parm,valueParm),getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FieldName='"+ dedupFields.getFieldName()+"' AND version=" + dedupFields.getVersion()+" ";

					boolean userAcces =  validateUserAccess(dedupFields.getWorkflowId(),getUserWorkspace().getLoggedInUser().getUserId(), "DedupFields", whereCond, dedupFields.getTaskId(), dedupFields.getNextTaskId());
					if (userAcces){
						showDetailView(dedupFields);
					}else{
						MessageUtil.showError(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(dedupFields);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the DedupFields dialog with a new empty entry. <br>
	 */
	public void onClick$button_DedupFieldsList_NewDedupFields(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new DedupFields object, We GET it from the backend.
		final DedupFields aDedupFields = getDedupFieldsService().getNewDedupFields();
		showDetailView(aDedupFields);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param DedupFields (aDedupFields)
	 * @throws Exception
	 */
	private void showDetailView(DedupFields aDedupFields) throws Exception {
		logger.debug("Enterring");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if (aDedupFields.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aDedupFields.setWorkflowId(getWorkFlowId());
		}

		Map<String, Object> map = getDefaultArguments();
		map.put("dedupFields", aDedupFields);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the DedupFieldsListbox from the
		 * dialog when we do a delete, edit or insert a DedupFields.
		 */
		map.put("dedupFieldsListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Dedup/DedupFields/DedupFieldsDialog.zul",null,map);
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
		logger.debug(event.toString());
		MessageUtil.showHelpWindow(event, window_DedupFieldsList);
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
		this.pagingDedupFieldsList.setActivePage(0);
		Events.postEvent("onCreate", this.window_DedupFieldsList, event);
		this.window_DedupFieldsList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the DedupFields dialog
	 */
	
	public void onClick$button_DedupFieldsList_DedupFieldsSearchDialog(Event event) throws Exception {
		logger.debug("Enterring");
		/*
		 * we can call our DedupFieldsDialog zul-file with parameters. So we can
		 * call them with a object of the selected DedupFields. For handed over
		 * these parameter only a Map is accepted. So we put the DedupFields object
		 * in a HashMap.
		 */
		Map<String, Object> map = getDefaultArguments();
		map.put("dedupFieldsCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Dedup/DedupFields/DedupFieldsSearchDialog.zul",null,map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * When the dedupFields print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_DedupFieldsList_PrintList(Event event) throws InterruptedException {
		logger.debug("Enterring");
		logger.debug(event.toString());
		new PTListReportUtils("DedupFields", getSearchObj(),this.pagingDedupFieldsList.getTotalSize()+1);
		logger.debug("Leaving");
	}

	public void setDedupFieldsService(DedupFieldsService dedupFieldsService) {
		this.dedupFieldsService = dedupFieldsService;
	}

	public DedupFieldsService getDedupFieldsService() {
		return this.dedupFieldsService;
	}

	public JdbcSearchObject<DedupFields> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<DedupFields> searchObj) {
		this.searchObj = searchObj;
	}
}
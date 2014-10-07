/**
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
 * FileName    		:  CollateralitemListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-02-2013    														*
 *                                                                  						*
 * Modified Date    :  20-02-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-02-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.coremasters.collateralitem;


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
import com.pennant.backend.model.coremasters.Collateralitem;
import com.pennant.backend.service.coremasters.CollateralitemService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.coremasters.collateralitem.model.CollateralitemListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/CoreMasters/Collateralitem/CollateralitemList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CollateralitemListCtrl extends GFCBaseListCtrl<Collateralitem> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(CollateralitemListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CollateralitemList; // autowired
	protected Borderlayout borderLayout_CollateralitemList; // autowired
	protected Paging pagingCollateralitemList; // autowired
	protected Listbox listBoxCollateralitem; // autowired

	// List headers
	protected Listheader listheader_HYCUS; // autowired
	protected Listheader listheader_HYCLC; // autowired
	protected Listheader listheader_HYDLP; // autowired
	protected Listheader listheader_HYAB; // autowired
	protected Listheader listheader_HYAS; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_CollateralitemList_NewCollateralitem; // autowired
	protected Button button_CollateralitemList_CollateralitemSearchDialog; // autowired
	protected Button button_CollateralitemList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Collateralitem> searchObj;
	
	private transient CollateralitemService collateralitemService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public CollateralitemListCtrl() {
		super();
	}

	public void onCreate$window_CollateralitemList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Collateralitem");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Collateralitem");
			
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
		//doCheckRights();
		
		this.borderLayout_CollateralitemList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCollateralitemList.setPageSize(getListRows());
		this.pagingCollateralitemList.setDetailed(true);

		this.listheader_HYCUS.setSortAscending(new FieldComparator("hYCUS", true));
		this.listheader_HYCUS.setSortDescending(new FieldComparator("hYCUS", false));
		this.listheader_HYCLC.setSortAscending(new FieldComparator("hYCLC", true));
		this.listheader_HYCLC.setSortDescending(new FieldComparator("hYCLC", false));
		this.listheader_HYDLP.setSortAscending(new FieldComparator("hYDLP", true));
		this.listheader_HYDLP.setSortDescending(new FieldComparator("hYDLP", false));
		this.listheader_HYAB.setSortAscending(new FieldComparator("hYAB", true));
		this.listheader_HYAB.setSortDescending(new FieldComparator("hYAB", false));
		this.listheader_HYAS.setSortAscending(new FieldComparator("hYAS", true));
		this.listheader_HYAS.setSortDescending(new FieldComparator("hYAS", false));
		
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
		this.searchObj = new JdbcSearchObject<Collateralitem>(Collateralitem.class,getListRows());
		this.searchObj.addSort("HYCUS", false);
		// Workflow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("HYPF_View");
			if (isFirstTask()) {
				button_CollateralitemList_NewCollateralitem.setVisible(true);
			} else {
				button_CollateralitemList_NewCollateralitem.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("HYPF_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_CollateralitemList_NewCollateralitem.setVisible(false);
			this.button_CollateralitemList_CollateralitemSearchDialog.setVisible(false);
			this.button_CollateralitemList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxCollateralitem,this.pagingCollateralitemList);
			// set the itemRenderer
			this.listBoxCollateralitem.setItemRenderer(new CollateralitemListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	@SuppressWarnings("unused")
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CollateralitemList");
		
		this.button_CollateralitemList_NewCollateralitem.setVisible(getUserWorkspace().isAllowed("button_CollateralitemList_NewCollateralitem"));
		this.button_CollateralitemList_CollateralitemSearchDialog.setVisible(getUserWorkspace().isAllowed("button_CollateralitemList_CollateralitemFindDialog"));
		this.button_CollateralitemList_PrintList.setVisible(getUserWorkspace().isAllowed("button_CollateralitemList_PrintList"));
	logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.coremasters.collateralitem.model.CollateralitemListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onCollateralitemItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected Collateralitem object
		final Listitem item = this.listBoxCollateralitem.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Collateralitem aCollateralitem = (Collateralitem) item.getAttribute("data");
			final Collateralitem collateralitem = getCollateralitemService().getCollateralitemById(aCollateralitem.getId());
			
			if(collateralitem==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aCollateralitem.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_HYCUS")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND HYCUS='"+ collateralitem.getHYCUS()+"' AND version=" + collateralitem.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "Collateralitem", whereCond, collateralitem.getTaskId(), collateralitem.getNextTaskId());
					if (userAcces){
						showDetailView(collateralitem);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(collateralitem);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the Collateralitem dialog with a new empty entry. <br>
	 */
	public void onClick$button_CollateralitemList_NewCollateralitem(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new Collateralitem object, We GET it from the backend.
		final Collateralitem aCollateralitem = getCollateralitemService().getNewCollateralitem();
		showDetailView(aCollateralitem);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param Collateralitem (aCollateralitem)
	 * @throws Exception
	 */
	private void showDetailView(Collateralitem aCollateralitem) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aCollateralitem.getWorkflowId()==0 && isWorkFlowEnabled()){
			aCollateralitem.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("collateralitem", aCollateralitem);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the CollateralitemListbox from the
		 * dialog when we do a delete, edit or insert a Collateralitem.
		 */
		map.put("collateralitemListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/CoreMasters/Collateralitem/CollateralitemDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_CollateralitemList);
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
		this.pagingCollateralitemList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CollateralitemList, event);
		this.window_CollateralitemList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the Collateralitem dialog
	 */
	
	public void onClick$button_CollateralitemList_CollateralitemSearchDialog(Event event) throws Exception {
		logger.debug("Entering");
		logger.debug(event.toString());
		/*
		 * we can call our CollateralitemDialog zul-file with parameters. So we can
		 * call them with a object of the selected Collateralitem. For handed over
		 * these parameter only a Map is accepted. So we put the Collateralitem object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("collateralitemCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/CoreMasters/Collateralitem/CollateralitemSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the collateralitem print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CollateralitemList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		new PTListReportUtils("Collateralitem", getSearchObj(),this.pagingCollateralitemList.getTotalSize()+1);
		logger.debug("Leaving");
	}

	public void setCollateralitemService(CollateralitemService collateralitemService) {
		this.collateralitemService = collateralitemService;
	}

	public CollateralitemService getCollateralitemService() {
		return this.collateralitemService;
	}

	public JdbcSearchObject<Collateralitem> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<Collateralitem> searchObj) {
		this.searchObj = searchObj;
	}
}
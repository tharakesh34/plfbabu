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
 * FileName    		:  BaseRateCodeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.baseratecode;

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
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.service.applicationmaster.BaseRateCodeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.applicationmaster.baseratecode.model.BaseRateCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/BaseRateCode/BaseRateCodeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class BaseRateCodeListCtrl extends GFCBaseListCtrl<BaseRateCode> implements Serializable {

	private static final long serialVersionUID = 7711473870956306562L;
	private final static Logger logger = Logger.getLogger(BaseRateCodeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_BaseRateCodeList; 		// auto wired
	protected Borderlayout 	borderLayout_BaseRateCodeList; 	// auto wired
	protected Paging 		pagingBaseRateCodeList; 		// auto wired
	protected Listbox 		listBoxBaseRateCode; 			// auto wired

	// List headers
	protected Listheader listheader_BRType; 		// auto wired
	protected Listheader listheader_BRTypeDesc; 	// auto wired
	protected Listheader listheader_RecordStatus; 	// auto wired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 											// auto wired
	protected Button button_BaseRateCodeList_NewBaseRateCode; 			// auto wired
	protected Button button_BaseRateCodeList_BaseRateCodeSearchDialog; 	// auto wired
	protected Button button_BaseRateCodeList_PrintList; 				// auto wired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<BaseRateCode> searchObj;
	
	private transient BaseRateCodeService baseRateCodeService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public BaseRateCodeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected BaseRateCode object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_BaseRateCodeList(Event event) throws Exception {
		logger.debug("Entering"+event.toString());
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("BaseRateCode");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("BaseRateCode");
			
			if (workFlowDetails==null){
				setWorkFlowEnabled(false);
			}else{
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(
						workFlowDetails.getFirstTaskOwner()));
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
		this.borderLayout_BaseRateCodeList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingBaseRateCodeList.setPageSize(getListRows());
		this.pagingBaseRateCodeList.setDetailed(true);

		this.listheader_BRType.setSortAscending(new FieldComparator("bRType", true));
		this.listheader_BRType.setSortDescending(new FieldComparator("bRType", false));
		this.listheader_BRTypeDesc.setSortAscending(new FieldComparator("bRTypeDesc", true));
		this.listheader_BRTypeDesc.setSortDescending(new FieldComparator("bRTypeDesc", false));
		
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
		this.searchObj = new JdbcSearchObject<BaseRateCode>(
				BaseRateCode.class,getListRows());
		this.searchObj.addSort("BRType", false);
		
		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("RMTBaseRateCodes_View");
			if (isFirstTask()) {
				button_BaseRateCodeList_NewBaseRateCode.setVisible(true);
			} else {
				button_BaseRateCodeList_NewBaseRateCode.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("RMTBaseRateCodes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_BaseRateCodeList_NewBaseRateCode.setVisible(false);
			this.button_BaseRateCodeList_BaseRateCodeSearchDialog.setVisible(false);
			this.button_BaseRateCodeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,
					this.listBoxBaseRateCode,this.pagingBaseRateCodeList);
			// set the itemRenderer
			this.listBoxBaseRateCode.setItemRenderer(new BaseRateCodeListModelItemRenderer());
		}
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("BaseRateCodeList");
		
		this.button_BaseRateCodeList_NewBaseRateCode.setVisible(getUserWorkspace()
				.isAllowed("button_BaseRateCodeList_NewBaseRateCode"));
		this.button_BaseRateCodeList_BaseRateCodeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_BaseRateCodeList_BaseRateCodeFindDialog"));
		this.button_BaseRateCodeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_BaseRateCodeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.rmtmasters.baseratecode.model.
	 * BaseRateCodeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onBaseRateCodeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering"+event.toString());

		// get the selected BaseRateCode object
		final Listitem item = this.listBoxBaseRateCode.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final BaseRateCode aBaseRateCode = (BaseRateCode) item.getAttribute("data");
			final BaseRateCode baseRateCode = getBaseRateCodeService().getBaseRateCodeById(
					aBaseRateCode.getId());
			if(baseRateCode==null){

				String[] valueParm = new String[1];
				String[] errParm= new String[1];

				valueParm[0] = aBaseRateCode.getBRType();
				errParm[0] = PennantJavaUtil.getLabel("label_BRType") + ":"+ valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
			String whereCond =  " AND BRType='"+ baseRateCode.getBRType()+
							"' AND version=" + baseRateCode.getVersion()+" ";
			
			if(isWorkFlowEnabled()){
				boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
						getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
						"BaseRateCode", whereCond, baseRateCode.getTaskId(),
						baseRateCode.getNextTaskId());
				if (userAcces){
					showDetailView(baseRateCode);
				}else{
					PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
				}
			}else{
				showDetailView(baseRateCode);
			}
			}
		}
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * Call the BaseRateCode dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_BaseRateCodeList_NewBaseRateCode(Event event) throws Exception {
		logger.debug("Entering"+event.toString());
		// create a new BaseRateCode object, We GET it from the back end.
		final BaseRateCode aBaseRateCode = getBaseRateCodeService().getNewBaseRateCode();
		showDetailView(aBaseRateCode);
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param BaseRateCode (aBaseRateCode)
	 * @throws Exception
	 */
	private void showDetailView(BaseRateCode aBaseRateCode) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aBaseRateCode.getWorkflowId()==0 && isWorkFlowEnabled()){
			aBaseRateCode.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("baseRateCode", aBaseRateCode);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the BaseRateCodeListbox from the
		 * dialog when we do a delete, edit or insert a BaseRateCode.
		 */
		map.put("baseRateCodeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/BaseRateCode/BaseRateCodeDialog.zul",null,map);
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
		logger.debug("Entering"+event.toString());
		PTMessageUtils.showHelpWindow(event, window_BaseRateCodeList);
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
		logger.debug("Entering"+event.toString());
		this.pagingBaseRateCodeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_BaseRateCodeList, event);
		this.window_BaseRateCodeList.invalidate();
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * call the BaseRateCode dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_BaseRateCodeList_BaseRateCodeSearchDialog(Event event) throws Exception {
		logger.debug("Entering"+event.toString());
		/*
		 * we can call our BaseRateCodeDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected BaseRateCode. For handed over
		 * these parameter only a Map is accepted. So we put the BaseRateCode object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("baseRateCodeCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/BaseRateCode/BaseRateCodeSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * When the baseRateCode print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_BaseRateCodeList_PrintList(Event event)throws InterruptedException {
		logger.debug("Entering"+event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("BaseRateCode", getSearchObj(),this.pagingBaseRateCodeList.getTotalSize()+1);
		logger.debug("Leaving"+event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setBaseRateCodeService(BaseRateCodeService baseRateCodeService) {
		this.baseRateCodeService = baseRateCodeService;
	}
	public BaseRateCodeService getBaseRateCodeService() {
		return this.baseRateCodeService;
	}

	public JdbcSearchObject<BaseRateCode> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<BaseRateCode> searchObj) {
		this.searchObj = searchObj;
	}
}
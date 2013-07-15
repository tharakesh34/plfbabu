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
 * FileName    		:  SplRateCodeListCtrl.java                                                   * 	  
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

package com.pennant.webui.applicationmaster.splratecode;

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
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.service.applicationmaster.SplRateCodeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.applicationmaster.splratecode.model.SplRateCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/SplRateCode/SplRateCodeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class SplRateCodeListCtrl extends GFCBaseListCtrl<SplRateCode> implements Serializable {

	private static final long serialVersionUID = 7426008145901571944L;
	private final static Logger logger = Logger.getLogger(SplRateCodeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_SplRateCodeList; 		// autoWired
	protected Borderlayout 	borderLayout_SplRateCodeList; 	// autoWired
	protected Paging 		pagingSplRateCodeList; 			// autoWired
	protected Listbox 		listBoxSplRateCode; 			// autoWired

	// List headers
	protected Listheader listheader_SRType; 		// autoWired
	protected Listheader listheader_SRTypeDesc; 	// autoWired
	protected Listheader listheader_SRIsActive; 	// autoWired
	protected Listheader listheader_RecordStatus; 	// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 										// autoWired
	protected Button button_SplRateCodeList_NewSplRateCode; 		// autoWired
	protected Button button_SplRateCodeList_SplRateCodeSearchDialog;// autoWired
	protected Button button_SplRateCodeList_PrintList; 				// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<SplRateCode> searchObj;

	private transient SplRateCodeService splRateCodeService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public SplRateCodeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected SplRateCode object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SplRateCodeList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("SplRateCode");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("SplRateCode");

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
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_SplRateCodeList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingSplRateCodeList.setPageSize(getListRows());
		this.pagingSplRateCodeList.setDetailed(true);

		//Apply sorting for getting List in the ListBox 
		this.listheader_SRType.setSortAscending(new FieldComparator("sRType", true));
		this.listheader_SRType.setSortDescending(new FieldComparator("sRType", false));
		this.listheader_SRTypeDesc.setSortAscending(new FieldComparator("sRTypeDesc", true));
		this.listheader_SRTypeDesc.setSortDescending(new FieldComparator("sRTypeDesc", false));
		this.listheader_SRIsActive.setSortAscending(new FieldComparator("sRIsActive", true));
		this.listheader_SRIsActive.setSortDescending(new FieldComparator("sRIsActive", false));

		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initial sorting ++//
		this.searchObj = new JdbcSearchObject<SplRateCode>(SplRateCode.class,getListRows());
		this.searchObj.addSort("SRType", false);

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("RMTSplRateCodes_View");
			if (isFirstTask()) {
				button_SplRateCodeList_NewSplRateCode.setVisible(true);
			} else {
				button_SplRateCodeList_NewSplRateCode.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("RMTSplRateCodes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_SplRateCodeList_NewSplRateCode.setVisible(false);
			this.button_SplRateCodeList_SplRateCodeSearchDialog.setVisible(false);
			this.button_SplRateCodeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxSplRateCode,this.pagingSplRateCodeList);
			// set the itemRenderer
			this.listBoxSplRateCode.setItemRenderer(new SplRateCodeListModelItemRenderer());
		}	
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("SplRateCodeList");

		this.button_SplRateCodeList_NewSplRateCode.setVisible(getUserWorkspace()
				.isAllowed("button_SplRateCodeList_NewSplRateCode"));
		this.button_SplRateCodeList_SplRateCodeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_SplRateCodeList_SplRateCodeFindDialog"));
		this.button_SplRateCodeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_SplRateCodeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.rmtmasters.splratecode.model.
	 * SplRateCodeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onSplRateCodeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected SplRateCode object
		final Listitem item = this.listBoxSplRateCode.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final SplRateCode aSplRateCode = (SplRateCode) item.getAttribute("data");
			final SplRateCode splRateCode = getSplRateCodeService().getSplRateCodeById(
					aSplRateCode.getId());
			if(splRateCode==null){

				String[] valueParm = new String[1];
				String[] errParm= new String[1];

				valueParm[0] = aSplRateCode.getSRType();
				errParm[0] = PennantJavaUtil.getLabel("label_SRType") + ":"+ valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				String whereCond =  " AND SRType='"+ splRateCode.getSRType()+
				"' AND version=" + splRateCode.getVersion()+" ";

				if(isWorkFlowEnabled()){
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "SplRateCode", 
							whereCond, splRateCode.getTaskId(), splRateCode.getNextTaskId());
					if (userAcces){
						showDetailView(splRateCode);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(splRateCode);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the SplRateCode dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_SplRateCodeList_NewSplRateCode(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new SplRateCode object, We GET it from the backEnd.
		final SplRateCode aSplRateCode = getSplRateCodeService().getNewSplRateCode();
		showDetailView(aSplRateCode);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param SplRateCode (aSplRateCode)
	 * @throws Exception
	 */
	private void showDetailView(SplRateCode aSplRateCode) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if(aSplRateCode.getWorkflowId()==0 && isWorkFlowEnabled()){
			aSplRateCode.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("splRateCode", aSplRateCode);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the SplRateCodeListbox from the
		 * dialog when we do a delete, edit or insert a SplRateCode.
		 */
		map.put("splRateCodeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/SplRateCode/SplRateCodeDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_SplRateCodeList);
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
		this.pagingSplRateCodeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_SplRateCodeList, event);
		this.window_SplRateCodeList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the SplRateCode dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_SplRateCodeList_SplRateCodeSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our SplRateCodeDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected SplRateCode. For handed over
		 * these parameter only a Map is accepted. So we put the SplRateCode object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("splRateCodeCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/SplRateCode/SplRateCodeSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the splRateCode print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_SplRateCodeList_PrintList(Event event)throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("SplRateCode", getSearchObj(),this.pagingSplRateCodeList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setSplRateCodeService(SplRateCodeService splRateCodeService) {
		this.splRateCodeService = splRateCodeService;
	}
	public SplRateCodeService getSplRateCodeService() {
		return this.splRateCodeService;
	}

	public JdbcSearchObject<SplRateCode> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<SplRateCode> searchObj) {
		this.searchObj = searchObj;
	}

}
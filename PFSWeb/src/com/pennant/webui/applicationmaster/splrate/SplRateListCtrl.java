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
 * FileName    		:  SplRateListCtrl.java                                                   * 	  
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

package com.pennant.webui.applicationmaster.splrate;

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
import com.pennant.backend.model.applicationmaster.SplRate;
import com.pennant.backend.service.applicationmaster.SplRateService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.applicationmaster.splrate.model.SplRateListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/SplRate/SplRateList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class SplRateListCtrl extends GFCBaseListCtrl<SplRate> implements Serializable {

	private static final long serialVersionUID = -2685575893028486510L;
	private final static Logger logger = Logger.getLogger(SplRateListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_SplRateList; 		// autoWired
	protected Borderlayout 	borderLayout_SplRateList; 	// autoWired
	protected Paging 		pagingSplRateList; 			// autoWired
	protected Listbox 		listBoxSplRate; 			// autoWired

	// List headers
	protected Listheader listheader_SRType; 		// autoWired
	protected Listheader listheader_SREffDate; 		// autoWired
	protected Listheader listheader_SRRate; 		// autoWired
	protected Listheader listheader_RecordStatus; 	// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 									// autoWired
	protected Button button_SplRateList_NewSplRate; 			// autoWired
	protected Button button_SplRateList_SplRateSearchDialog; 	// autoWired
	protected Button button_SplRateList_PrintList; 				// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<SplRate> searchObj;

	private transient SplRateService splRateService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public SplRateListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected SplRate object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SplRateList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("SplRate");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("SplRate");
			
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
		this.borderLayout_SplRateList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingSplRateList.setPageSize(getListRows());
		this.pagingSplRateList.setDetailed(true);

		//Apply sorting for getting List in the ListBox 
		this.listheader_SRType.setSortAscending(new FieldComparator("sRType", true));
		this.listheader_SRType.setSortDescending(new FieldComparator("sRType", false));
		this.listheader_SREffDate.setSortAscending(new FieldComparator("sREffDate", true));
		this.listheader_SREffDate.setSortDescending(new FieldComparator("sREffDate", false));
		this.listheader_SRRate.setSortAscending(new FieldComparator("sRRate", true));
		this.listheader_SRRate.setSortDescending(new FieldComparator("sRRate", false));
		
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
		this.searchObj = new JdbcSearchObject<SplRate>(SplRate.class,getListRows());
		this.searchObj.addSort("SRType", false);
		this.searchObj.addSort("SREffDate", false);
		
		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("RMTSplRates_View");
			if (isFirstTask()) {
				button_SplRateList_NewSplRate.setVisible(true);
			} else {
				button_SplRateList_NewSplRate.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("RMTSplRates_View");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_SplRateList_NewSplRate.setVisible(false);
			this.button_SplRateList_SplRateSearchDialog.setVisible(false);
			this.button_SplRateList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxSplRate,this.pagingSplRateList);
			// set the itemRenderer
			this.listBoxSplRate.setItemRenderer(new SplRateListModelItemRenderer());
		}	
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("SplRateList");
		
		this.button_SplRateList_NewSplRate.setVisible(getUserWorkspace()
				.isAllowed("button_SplRateList_NewSplRate"));
		this.button_SplRateList_SplRateSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_SplRateList_SplRateFindDialog"));
		this.button_SplRateList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_SplRateList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see:
	 * com.pennant.webui.rmtmasters.splrate.model.SplRateListModelItemRenderer
	 * .java <br>
	 * 
	 * @param event
	 *            (Event)
	 * @throws Exception
	 */
	public void onSplRateItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected SplRate object
		final Listitem item = this.listBoxSplRate.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final SplRate aSplRate = (SplRate) item.getAttribute("data");
			final SplRate splRate = getSplRateService().getSplRateById(aSplRate.getId(),aSplRate.getSREffDate());

			if(splRate==null){

				String[] valueParm = new String[2];
				String[] errParm= new String[2];

				valueParm[0] = aSplRate.getSRType();
				valueParm[1] = aSplRate.getSREffDate().toString();

				errParm[0] = PennantJavaUtil.getLabel("label_SRType") + ":"+ valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_SREffDate") + ":"+valueParm[1];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				String whereCond =  " AND SRType='"+ splRate.getSRType()+"' AND SREffDate='"+splRate.getSREffDate()+
				"' AND version=" + splRate.getVersion()+" ";

				if(isWorkFlowEnabled()){
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "SplRate",
							whereCond, splRate.getTaskId(), splRate.getNextTaskId());
					if (userAcces){
						showDetailView(splRate);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(splRate);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the SplRate dialog with a new empty entry. <br>
	 * @param event (Event)
	 * @throws Exception
	 */
	public void onClick$button_SplRateList_NewSplRate(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new SplRate object, We GET it from the backEnd.
		final SplRate aSplRate = getSplRateService().getNewSplRate();
		showDetailView(aSplRate);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param SplRate (aSplRate)
	 * @throws Exception
	 */
	private void showDetailView(SplRate aSplRate) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aSplRate.getWorkflowId()==0 && isWorkFlowEnabled()){
			aSplRate.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("splRate", aSplRate);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the SplRateListbox from the
		 * dialog when we do a delete, edit or insert a SplRate.
		 */
		map.put("splRateListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/SplRate/SplRateDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_SplRateList);
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
		this.pagingSplRateList.setActivePage(0);
		Events.postEvent("onCreate", this.window_SplRateList, event);
		this.window_SplRateList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the SplRate dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_SplRateList_SplRateSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our SplRateDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected SplRate. For handed over
		 * these parameter only a Map is accepted. So we put the SplRate object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("splRateCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/SplRate/SplRateSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the splRate print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_SplRateList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("SplRate", getSearchObj(),this.pagingSplRateList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setSplRateService(SplRateService splRateService) {
		this.splRateService = splRateService;
	}
	public SplRateService getSplRateService() {
		return this.splRateService;
	}

	public JdbcSearchObject<SplRate> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<SplRate> searchObj) {
		this.searchObj = searchObj;
	}
}
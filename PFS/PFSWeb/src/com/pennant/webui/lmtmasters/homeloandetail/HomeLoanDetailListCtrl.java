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
 * FileName    		:  HomeLoanDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-10-2011    														*
 *                                                                  						*
 * Modified Date    :  13-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.homeloandetail;

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
import com.pennant.backend.model.lmtmasters.HomeLoanDetail;
import com.pennant.backend.service.lmtmasters.HomeLoanDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.lmtmasters.homeloandetail.model.HomeLoanDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/HomeLoanDetail/HomeLoanDetailList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class HomeLoanDetailListCtrl extends GFCBaseListCtrl<HomeLoanDetail> implements Serializable {

	private static final long serialVersionUID = -5391208276061575845L;
	private final static Logger logger = Logger.getLogger(HomeLoanDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_HomeLoanDetailList; 			// autowired
	protected Borderlayout 	borderLayout_HomeLoanDetailList; 	// autowired
	protected Paging 		pagingHomeLoanDetailList; 			// autowired
	protected Listbox 		listBoxHomeLoanDetail; 				// autowired

	// List headers
	protected Listheader listheader_LoanRefNumber; 	 // autowired
	protected Listheader listheader_LoanRefType; 	 // autowired
	protected Listheader listheader_HomeDetails; 	 // autowired
	protected Listheader listheader_HomeBuilderName; // autowired
	protected Listheader listheader_HomeCostPerFlat; // autowired
	protected Listheader listheader_RecordStatus; 	 // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_HomeLoanDetailList_NewHomeLoanDetail; 			// autowired
	protected Button button_HomeLoanDetailList_HomeLoanDetailSearchDialog;  // autowired
	protected Button button_HomeLoanDetailList_PrintList; 					// autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<HomeLoanDetail> searchObj;
	private transient HomeLoanDetailService homeLoanDetailService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public HomeLoanDetailListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected HomeLoanDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_HomeLoanDetailList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("HomeLoanDetail");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("HomeLoanDetail");

			if (workFlowDetails == null) {
				setWorkFlowEnabled(false);
			} else {
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(
						workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}
		} else {
			wfAvailable = false;
		}

		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_HomeLoanDetailList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingHomeLoanDetailList.setPageSize(getListRows());
		this.pagingHomeLoanDetailList.setDetailed(true);

		this.listheader_LoanRefNumber.setSortAscending(new FieldComparator("loanRefNumber", true));
		this.listheader_LoanRefNumber.setSortDescending(new FieldComparator("loanRefNumber", false));
		
		this.listheader_LoanRefType.setSortAscending(new FieldComparator("loanRefType", true));
		this.listheader_LoanRefType.setSortDescending(new FieldComparator("loanRefType", false));
		
		this.listheader_HomeDetails.setSortAscending(new FieldComparator("homeDetails", true));
		this.listheader_HomeDetails.setSortDescending(new FieldComparator("homeDetails", false));
		
		this.listheader_HomeBuilderName.setSortAscending(new FieldComparator("homeBuilderName", true));
		this.listheader_HomeBuilderName.setSortDescending(new FieldComparator("homeBuilderName", false));
		
		this.listheader_HomeCostPerFlat.setSortAscending(new FieldComparator("homeCostPerFlat", true));
		this.listheader_HomeCostPerFlat.setSortDescending(new FieldComparator("homeCostPerFlat", false));

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<HomeLoanDetail>(HomeLoanDetail.class, getListRows());
		this.searchObj.addSort("loanRefNumber", false);

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("LMTHomeLoanDetail_View");
			if (isFirstTask()) {
				button_HomeLoanDetailList_NewHomeLoanDetail.setVisible(true);
			} else {
				button_HomeLoanDetailList_NewHomeLoanDetail.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace()
					.getUserRoles(), isFirstTask());
		}else{
			this.searchObj.addTabelName("LMTHomeLoanDetail_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_HomeLoanDetailList_NewHomeLoanDetail.setVisible(false);
			this.button_HomeLoanDetailList_HomeLoanDetailSearchDialog.setVisible(false);
			this.button_HomeLoanDetailList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil
					.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,
					this.listBoxHomeLoanDetail, this.pagingHomeLoanDetailList);
			// set the itemRenderer
			this.listBoxHomeLoanDetail.setItemRenderer(new HomeLoanDetailListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("HomeLoanDetailList");

		this.button_HomeLoanDetailList_NewHomeLoanDetail.setVisible(getUserWorkspace().isAllowed(
				"button_HomeLoanDetailList_NewHomeLoanDetail"));
		this.button_HomeLoanDetailList_HomeLoanDetailSearchDialog.setVisible(getUserWorkspace().isAllowed(
				"button_HomeLoanDetailList_HomeLoanDetailFindDialog"));
		this.button_HomeLoanDetailList_PrintList.setVisible(getUserWorkspace().isAllowed(
				"button_HomeLoanDetailList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.lmtmasters.homeloandetail.model.
	 * HomeLoanDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onHomeLoanDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected HomeLoanDetail object
		final Listitem item = this.listBoxHomeLoanDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final HomeLoanDetail aHomeLoanDetail = (HomeLoanDetail) item.getAttribute("data");
			final HomeLoanDetail homeLoanDetail = getHomeLoanDetailService().getHomeLoanDetailById(
					aHomeLoanDetail.getId());

			if (homeLoanDetail == null) {
				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(aHomeLoanDetail.getId());
				errParm[0] = PennantJavaUtil.getLabel("label_LoanRefNumber") + ":"+ valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
								errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				if (isWorkFlowEnabled()) {
					String whereCond = " AND LoanRefNumber="+ homeLoanDetail.getId() + 
											" AND version="+ homeLoanDetail.getVersion() + " ";

					boolean userAcces = validateUserAccess(workFlowDetails.getId(), 
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(),"HomeLoanDetail", 
							whereCond,homeLoanDetail.getTaskId(),homeLoanDetail.getNextTaskId());
					if (userAcces) {
						showDetailView(homeLoanDetail);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(homeLoanDetail);
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param HomeLoanDetail
	 *            (aHomeLoanDetail)
	 * @throws Exception
	 */
	private void showDetailView(HomeLoanDetail aHomeLoanDetail) throws Exception {
		logger.debug("Entering");
		
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aHomeLoanDetail.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aHomeLoanDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("homeLoanDetail", aHomeLoanDetail);
		map.put("homeLoanDetailListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/LMTMasters/HomeLoanDetail/HomeLoanDetailDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_HomeLoanDetailList);
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
		this.pagingHomeLoanDetailList.setActivePage(0);
		Events.postEvent("onCreate", this.window_HomeLoanDetailList, event);
		this.window_HomeLoanDetailList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for calling the HomeLoanDetail dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_HomeLoanDetailList_HomeLoanDetailSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		/*
		 * we can call our HomeLoanDetailDialog ZUL-file with parameters. So we
		 * can call them with a object of the selected HomeLoanDetail. For
		 * handed over these parameter only a Map is accepted. So we put the
		 * HomeLoanDetail object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("homeLoanDetailCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/LMTMasters/HomeLoanDetail/HomeLoanDetailSearchDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the homeLoanDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_HomeLoanDetailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTReportUtils.getReport("HomeLoanDetail", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setHomeLoanDetailService(
			HomeLoanDetailService homeLoanDetailService) {
		this.homeLoanDetailService = homeLoanDetailService;
	}
	public HomeLoanDetailService getHomeLoanDetailService() {
		return this.homeLoanDetailService;
	}

	public JdbcSearchObject<HomeLoanDetail> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<HomeLoanDetail> searchObj) {
		this.searchObj = searchObj;
	}
}
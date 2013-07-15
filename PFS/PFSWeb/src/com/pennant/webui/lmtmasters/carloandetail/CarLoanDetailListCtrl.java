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
 * FileName    		:  CarLoanDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2011    														*
 *                                                                  						*
 * Modified Date    :  08-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.carloandetail;

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
import com.pennant.backend.model.lmtmasters.CarLoanDetail;
import com.pennant.backend.service.lmtmasters.CarLoanDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.lmtmasters.carloandetail.model.CarLoanDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/CarLoanDetail/CarLoanDetailList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CarLoanDetailListCtrl extends GFCBaseListCtrl<CarLoanDetail> implements Serializable {

	private static final long serialVersionUID = 1654997379840433302L;
	private final static Logger logger = Logger.getLogger(CarLoanDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CarLoanDetailList; 		 // autowired
	protected Borderlayout 	borderLayout_CarLoanDetailList;  // autowired
	protected Paging 		pagingCarLoanDetailList; 		 // autowired
	protected Listbox 		listBoxCarLoanDetail; 			 // autowired

	// List headers
	protected Listheader listheader_LoanRefNumber; 		// autowired
	protected Listheader listheader_CarLoanFor; 		// autowired
	protected Listheader listheader_CarVersion; 		// autowired
	protected Listheader listheader_CarMakeYear; 		// autowired
	protected Listheader listheader_CarDealer; 			// autowired
	protected Listheader listheader_RecordStatus; 		// autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 											// autowired
	protected Button button_CarLoanDetailList_NewCarLoanDetail; 		// autowired
	protected Button button_CarLoanDetailList_CarLoanDetailSearchDialog;// autowired
	protected Button button_CarLoanDetailList_PrintList; 				// autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CarLoanDetail> searchObj;
	private transient CarLoanDetailService carLoanDetailService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public CarLoanDetailListCtrl() {
		super();
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected CarLoan object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CarLoanDetailList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CarLoanDetail");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CarLoanDetail");

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

		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_CarLoanDetailList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCarLoanDetailList.setPageSize(getListRows());
		this.pagingCarLoanDetailList.setDetailed(true);

		this.listheader_LoanRefNumber.setSortAscending(new FieldComparator("loanRefNumber", true));
		this.listheader_LoanRefNumber.setSortDescending(new FieldComparator("loanRefNumber", false));
		
		this.listheader_CarLoanFor.setSortAscending(new FieldComparator("carLoanFor", true));
		this.listheader_CarLoanFor.setSortDescending(new FieldComparator("carLoanFor", false));
		
		this.listheader_CarVersion.setSortAscending(new FieldComparator("carVersion", true));
		this.listheader_CarVersion.setSortDescending(new FieldComparator("carVersion", false));
		
		this.listheader_CarMakeYear.setSortAscending(new FieldComparator("carMakeYear", true));
		this.listheader_CarMakeYear.setSortDescending(new FieldComparator("carMakeYear", false));
		
		this.listheader_CarDealer.setSortAscending(new FieldComparator("carDealer", true));
		this.listheader_CarDealer.setSortDescending(new FieldComparator("carDealer", false));

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
		this.searchObj = new JdbcSearchObject<CarLoanDetail>(CarLoanDetail.class,getListRows());

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("LMTCarLoanDetail_View");
			if (isFirstTask()) {
				button_CarLoanDetailList_NewCarLoanDetail.setVisible(false);
			} else {
				button_CarLoanDetailList_NewCarLoanDetail.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		}else{
			this.searchObj.addTabelName("LMTCarLoanDetail_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_CarLoanDetailList_NewCarLoanDetail.setVisible(false);
			this.button_CarLoanDetailList_CarLoanDetailSearchDialog.setVisible(false);
			this.button_CarLoanDetailList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,
					this.listBoxCarLoanDetail, this.pagingCarLoanDetailList);
			// set the itemRenderer
			this.listBoxCarLoanDetail.setItemRenderer(new CarLoanDetailListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CarLoanDetailList");

		this.button_CarLoanDetailList_NewCarLoanDetail.setVisible(false);
		this.button_CarLoanDetailList_CarLoanDetailSearchDialog.setVisible(getUserWorkspace().isAllowed(
				"button_CarLoanDetailList_CarLoanDetailFindDialog"));
		this.button_CarLoanDetailList_PrintList.setVisible(getUserWorkspace().isAllowed(
				"button_CarLoanDetailList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.lmtmasters.carloandetail.model.
	 * CarLoanDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCarLoanDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected CarLoanDetail object
		final Listitem item = this.listBoxCarLoanDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CarLoanDetail aCarLoanDetail = (CarLoanDetail) item.getAttribute("data");
			final CarLoanDetail carLoanDetail = getCarLoanDetailService().getCarLoanDetailById(
					aCarLoanDetail.getLoanRefNumber());

			if(carLoanDetail==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aCarLoanDetail.getLoanRefNumber());
				errParm[0]=PennantJavaUtil.getLabel("label_CarLoanRefNumber")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
								errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond = " AND loanRefNumber='"+ carLoanDetail.getLoanRefNumber() + 
										"' AND version="+ carLoanDetail.getVersion() + " ";

					boolean userAcces = validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(),"CarLoanDetail",
							whereCond,carLoanDetail.getTaskId(),carLoanDetail.getNextTaskId());
					if (userAcces){
						showDetailView(carLoanDetail);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(carLoanDetail);
				}
			}	
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param CarLoanDetail (aCarLoanDetail)
	 * @throws Exception
	 */
	private void showDetailView(CarLoanDetail aCarLoanDetail) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if(aCarLoanDetail.getWorkflowId()==0 && isWorkFlowEnabled()){
			aCarLoanDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("carLoanDetail", aCarLoanDetail);
		map.put("carLoanDetailListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/LMTMasters/CarLoanDetail/CarLoanDetailDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_CarLoanDetailList);
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
		this.pagingCarLoanDetailList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CarLoanDetailList, event);
		this.window_CarLoanDetailList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for calling the CarLoanDetail dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CarLoanDetailList_CarLoanDetailSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our CarLoanDetailDialog ZUL-file with parameters. So we
		 * can call them with a object of the selected CarLoanDetail. For handed
		 * over these parameter only a Map is accepted. So we put the
		 * CarLoanDetail object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("carLoanDetailCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/LMTMasters/CarLoanDetail/CarLoanDetailSearchDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the carLoanDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CarLoanDetailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTReportUtils.getReport("CarLoanDetail", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCarLoanDetailService(CarLoanDetailService carLoanDetailService) {
		this.carLoanDetailService = carLoanDetailService;
	}
	public CarLoanDetailService getCarLoanDetailService() {
		return this.carLoanDetailService;
	}

	public JdbcSearchObject<CarLoanDetail> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<CarLoanDetail> searchObj) {
		this.searchObj = searchObj;
	}
}
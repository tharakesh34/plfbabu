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
 * FileName    		:  MortgageLoanDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-10-2011    														*
 *                                                                  						*
 * Modified Date    :  14-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.mortgageloandetail;

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
import com.pennant.backend.model.lmtmasters.MortgageLoanDetail;
import com.pennant.backend.service.lmtmasters.MortgageLoanDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.lmtmasters.mortgageloandetail.model.MortgageLoanDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/MortgageLoanDetail/MortgageLoanDetailList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class MortgageLoanDetailListCtrl extends GFCBaseListCtrl<MortgageLoanDetail> implements Serializable {

	private static final long serialVersionUID = -5432419636740556596L;
	private final static Logger logger = Logger.getLogger(MortgageLoanDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_MortgageLoanDetailList; 		// autowired
	protected Borderlayout 	borderLayout_MortgageLoanDetailList;// autowired
	protected Paging 		pagingMortgageLoanDetailList; 		// autowired
	protected Listbox 		listBoxMortgageLoanDetail; 			// autowired

	// List headers
	protected Listheader listheader_LoanRefNumber; 			// autowired
	protected Listheader listheader_LoanRefType; 			// autowired
	protected Listheader listheader_MortgProperty; 			// autowired
	protected Listheader listheader_MortgCurrentValue; 		// autowired
	protected Listheader listheader_MortgPurposeOfLoan; 	// autowired
	protected Listheader listheader_RecordStatus; 			// autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 														// autowired
	protected Button button_MortgageLoanDetailList_NewMortgageLoanDetail; 			// autowired
	protected Button button_MortgageLoanDetailList_MortgageLoanDetailSearchDialog; 	// autowired
	protected Button button_MortgageLoanDetailList_PrintList; 						// autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<MortgageLoanDetail> searchObj;
	private transient MortgageLoanDetailService mortgageLoanDetailService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public MortgageLoanDetailListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected MortgageLoanDetail object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_MortgageLoanDetailList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("MortgageLoanDetail");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("MortgageLoanDetail");
			
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
		
		this.borderLayout_MortgageLoanDetailList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingMortgageLoanDetailList.setPageSize(getListRows());
		this.pagingMortgageLoanDetailList.setDetailed(true);

		this.listheader_LoanRefNumber.setSortAscending(new FieldComparator("loanRefNumber", true));
		this.listheader_LoanRefNumber.setSortDescending(new FieldComparator("loanRefNumber", false));
//		this.listheader_LoanRefType.setSortAscending(new FieldComparator("loanRefType", true));
//		this.listheader_LoanRefType.setSortDescending(new FieldComparator("loanRefType", false));
		this.listheader_MortgProperty.setSortAscending(new FieldComparator("mortgProperty", true));
		this.listheader_MortgProperty.setSortDescending(new FieldComparator("mortgProperty", false));
		this.listheader_MortgCurrentValue.setSortAscending(new FieldComparator("mortgCurrentValue", true));
		this.listheader_MortgCurrentValue.setSortDescending(new FieldComparator("mortgCurrentValue", false));
		this.listheader_MortgPurposeOfLoan.setSortAscending(new FieldComparator("mortgPurposeOfLoan", true));
		this.listheader_MortgPurposeOfLoan.setSortDescending(new FieldComparator("mortgPurposeOfLoan", false));
		
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
		this.searchObj = new JdbcSearchObject<MortgageLoanDetail>(MortgageLoanDetail.class,getListRows());
		this.searchObj.addFilter(new Filter("RecordType", PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL));
		this.searchObj.addSort("loanRefNumber", false);

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("LMTMortgageLoanDetail_View");
			if (isFirstTask()) {
				button_MortgageLoanDetailList_NewMortgageLoanDetail.setVisible(false);
			} else {
				button_MortgageLoanDetailList_NewMortgageLoanDetail.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("LMTMortgageLoanDetail_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_MortgageLoanDetailList_NewMortgageLoanDetail.setVisible(false);
			this.button_MortgageLoanDetailList_MortgageLoanDetailSearchDialog.setVisible(false);
			this.button_MortgageLoanDetailList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxMortgageLoanDetail,
					this.pagingMortgageLoanDetailList);
			// set the itemRenderer
			this.listBoxMortgageLoanDetail.setItemRenderer(new MortgageLoanDetailListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("MortgageLoanDetailList");
		
		this.button_MortgageLoanDetailList_NewMortgageLoanDetail.setVisible(false);//getUserWorkspace().isAllowed("button_MortgageLoanDetailList_NewMortgageLoanDetail")
		this.button_MortgageLoanDetailList_MortgageLoanDetailSearchDialog.setVisible(getUserWorkspace().
				isAllowed("button_MortgageLoanDetailList_MortgageLoanDetailFindDialog"));
		this.button_MortgageLoanDetailList_PrintList.setVisible(getUserWorkspace().
				isAllowed("button_MortgageLoanDetailList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.lmtmasters.mortgageloandetail.model.MortgageLoanDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onMortgageLoanDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected MortgageLoanDetail object
		final Listitem item = this.listBoxMortgageLoanDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final MortgageLoanDetail aMortgageLoanDetail = (MortgageLoanDetail) item.getAttribute("data");
			final MortgageLoanDetail mortgageLoanDetail = getMortgageLoanDetailService().getMortgageLoanDetailById(aMortgageLoanDetail.getId());
			
			if(mortgageLoanDetail==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aMortgageLoanDetail.getId());
				errParm[0]=PennantJavaUtil.getLabel("label_LoanRefNumber")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(
						PennantConstants.KEY_FIELD,"41005", errParm,valueParm),
						getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND LoanRefNumber='"+ mortgageLoanDetail.getId()+
										"' AND version=" + mortgageLoanDetail.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "MortgageLoanDetail",
							whereCond, mortgageLoanDetail.getTaskId(), mortgageLoanDetail.getNextTaskId());
					if (userAcces){
						showDetailView(mortgageLoanDetail);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(mortgageLoanDetail);
				}
			}	
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the MortgageLoanDetail dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_MortgageLoanDetailList_NewMortgageLoanDetail(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new MortgageLoanDetail object, We GET it from the backEnd.
		final MortgageLoanDetail aMortgageLoanDetail = getMortgageLoanDetailService().getNewMortgageLoanDetail();
		showDetailView(aMortgageLoanDetail);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some params in a map if needed. <br>
	 * 
	 * @param MortgageLoanDetail (aMortgageLoanDetail)
	 * @throws Exception
	 */
	private void showDetailView(MortgageLoanDetail aMortgageLoanDetail) throws Exception {
		logger.debug("Entering");
		
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aMortgageLoanDetail.getWorkflowId()==0 && isWorkFlowEnabled()){
			aMortgageLoanDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("mortgageLoanDetail", aMortgageLoanDetail);
		map.put("mortgageLoanDetailListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/LMTMasters/MortgageLoanDetail/MortgageLoanDetailDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_MortgageLoanDetailList);
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
		this.pagingMortgageLoanDetailList.setActivePage(0);
		Events.postEvent("onCreate", this.window_MortgageLoanDetailList, event);
		this.window_MortgageLoanDetailList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for calling the MortgageLoanDetail dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_MortgageLoanDetailList_MortgageLoanDetailSearchDialog(Event event) 
						throws Exception {
		logger.debug("Entering" + event.toString());
		
		/*
		 * we can call our MortgageLoanDetailDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected MortgageLoanDetail. For handed over
		 * these parameter only a Map is accepted. So we put the MortgageLoanDetail object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("mortgageLoanDetailCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL -file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/LMTMasters/MortgageLoanDetail/MortgageLoanDetailSearchDialog.zul",
								null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the mortgageLoanDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_MortgageLoanDetailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTReportUtils.getReport("MortgageLoanDetail", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setMortgageLoanDetailService(MortgageLoanDetailService mortgageLoanDetailService) {
		this.mortgageLoanDetailService = mortgageLoanDetailService;
	}
	public MortgageLoanDetailService getMortgageLoanDetailService() {
		return this.mortgageLoanDetailService;
	}

	public JdbcSearchObject<MortgageLoanDetail> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<MortgageLoanDetail> searchObj) {
		this.searchObj = searchObj;
	}
}
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
 * FileName    		:  CorporateCustomerDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-12-2011    														*
 *                                                                  						*
 * Modified Date    :  01-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.corporatecustomerdetail;

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
import com.pennant.backend.model.customermasters.CorporateCustomerDetail;
import com.pennant.backend.service.customermasters.CorporateCustomerDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.customermasters.corporatecustomerdetail.model.CorporateCustomerDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CorporateCustomerDetail/CorporateCustomerDetailList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CorporateCustomerDetailListCtrl extends GFCBaseListCtrl<CorporateCustomerDetail> implements Serializable {

	private static final long serialVersionUID = 3149018047814219584L;
	private final static Logger logger = Logger.getLogger(CorporateCustomerDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CorporateCustomerDetailList; 		// autowired
	protected Borderlayout 	borderLayout_CorporateCustomerDetailList; 	// autowired
	protected Paging 		pagingCorporateCustomerDetailList; 			// autowired
	protected Listbox 		listBoxCorporateCustomerDetail; 			// autowired

	// List headers
	protected Listheader listheader_CustId; 			// autowired
	protected Listheader listheader_Name; 				// autowired
	protected Listheader listheader_PhoneNumber; 		// autowired
	protected Listheader listheader_EmailId; 			// autowired
	protected Listheader listheader_RecordStatus; 		// autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 																	// autowired
	protected Button button_CorporateCustomerDetailList_NewCorporateCustomerDetail;	 			// autowired
	protected Button button_CorporateCustomerDetailList_CorporateCustomerDetailSearchDialog; 	// autowired
	protected Button button_CorporateCustomerDetailList_PrintList; 								// autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CorporateCustomerDetail> searchObj;
	private transient CorporateCustomerDetailService corporateCustomerDetailService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public CorporateCustomerDetailListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected CorporateCustomerDetail object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CorporateCustomerDetailList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CorporateCustomerDetail");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CorporateCustomerDetail");
			
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
		
		this.borderLayout_CorporateCustomerDetailList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCorporateCustomerDetailList.setPageSize(getListRows());
		this.pagingCorporateCustomerDetailList.setDetailed(true);

		this.listheader_CustId.setSortAscending(new FieldComparator("custId", true));
		this.listheader_CustId.setSortDescending(new FieldComparator("custId", false));
		this.listheader_Name.setSortAscending(new FieldComparator("name", true));
		this.listheader_Name.setSortDescending(new FieldComparator("name", false));
		this.listheader_PhoneNumber.setSortAscending(new FieldComparator("phoneNumber", true));
		this.listheader_PhoneNumber.setSortDescending(new FieldComparator("phoneNumber", false));
		this.listheader_EmailId.setSortAscending(new FieldComparator("emailId", true));
		this.listheader_EmailId.setSortDescending(new FieldComparator("emailId", false));
		
		
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
		this.searchObj = new JdbcSearchObject<CorporateCustomerDetail>(
				CorporateCustomerDetail.class,getListRows());
		this.searchObj.addSort("CustId", false);
		this.searchObj.addFilter(new Filter("lovDescCustRecordType", 
				PennantConstants.RECORD_TYPE_NEW, Filter.OP_NOT_EQUAL));
		
		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("CustomerCorporateDetail_View");
			button_CorporateCustomerDetailList_NewCorporateCustomerDetail.setVisible(false);
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("CustomerCorporateDetail_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_CorporateCustomerDetailList_NewCorporateCustomerDetail.setVisible(false);
			this.button_CorporateCustomerDetailList_CorporateCustomerDetailSearchDialog.setVisible(false);
			this.button_CorporateCustomerDetailList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxCorporateCustomerDetail,
					this.pagingCorporateCustomerDetailList);
			// set the itemRenderer
			this.listBoxCorporateCustomerDetail.setItemRenderer(
					new CorporateCustomerDetailListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CorporateCustomerDetailList");
		
		this.button_CorporateCustomerDetailList_NewCorporateCustomerDetail.setVisible(false);
		this.button_CorporateCustomerDetailList_CorporateCustomerDetailSearchDialog.setVisible(
				getUserWorkspace().isAllowed(
						"button_CorporateCustomerDetailList_CorporateCustomerDetailFindDialog"));
		this.button_CorporateCustomerDetailList_PrintList.setVisible(
				getUserWorkspace().isAllowed("button_CorporateCustomerDetailList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.customermasters.corporatecustomerdetail.model.
	 * CorporateCustomerDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCorporateCustomerDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected CorporateCustomerDetail object
		final Listitem item = this.listBoxCorporateCustomerDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CorporateCustomerDetail aCorporateCustomerDetail = (CorporateCustomerDetail) 
													item.getAttribute("data");
			final CorporateCustomerDetail corporateCustomerDetail = getCorporateCustomerDetailService().
											getCorporateCustomerDetailById(aCorporateCustomerDetail.getId());
			
			if(corporateCustomerDetail==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aCorporateCustomerDetail.getId());
				errParm[0]=PennantJavaUtil.getLabel("label_CustId")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), 
						getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND CustId="+ corporateCustomerDetail.getCustId()+
											" AND version=" + corporateCustomerDetail.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), 
							"CorporateCustomerDetail", whereCond, corporateCustomerDetail.getTaskId(), 
							corporateCustomerDetail.getNextTaskId());
					if (userAcces){
						showDetailView(corporateCustomerDetail);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(corporateCustomerDetail);
				}
			}	
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param CorporateCustomerDetail (aCorporateCustomerDetail)
	 * @throws Exception
	 */
	private void showDetailView(CorporateCustomerDetail aCorporateCustomerDetail) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aCorporateCustomerDetail.getWorkflowId()==0 && isWorkFlowEnabled()){
			aCorporateCustomerDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("corporateCustomerDetail", aCorporateCustomerDetail);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the CorporateCustomerDetailListbox from the
		 * dialog when we do a delete, edit or insert a CorporateCustomerDetail.
		 */
		map.put("corporateCustomerDetailListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CorporateCustomerDetail/CorporateCustomerDetailDialog.zul",
							null,map);
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
		PTMessageUtils.showHelpWindow(event, window_CorporateCustomerDetailList);
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
		this.pagingCorporateCustomerDetailList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CorporateCustomerDetailList, event);
		this.window_CorporateCustomerDetailList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for calling the CorporateCustomerDetail dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CorporateCustomerDetailList_CorporateCustomerDetailSearchDialog(Event event)
										throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our CorporateCustomerDetailDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected CorporateCustomerDetail. For handed over
		 * these parameter only a Map is accepted. So we put the CorporateCustomerDetail object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("corporateCustomerDetailCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CorporateCustomerDetail/CorporateCustomerDetailSearchDialog.zul",
							null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the corporateCustomerDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CorporateCustomerDetailList_PrintList(Event event) 
						throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTReportUtils.getReport("CorporateCustomerDetail", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setCorporateCustomerDetailService(
			CorporateCustomerDetailService corporateCustomerDetailService) {
		this.corporateCustomerDetailService = corporateCustomerDetailService;
	}
	public CorporateCustomerDetailService getCorporateCustomerDetailService() {
		return this.corporateCustomerDetailService;
	}

	public JdbcSearchObject<CorporateCustomerDetail> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<CorporateCustomerDetail> searchObj) {
		this.searchObj = searchObj;
	}
}
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
 * FileName    		:  CommodityDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-11-2011    														*
 *                                                                  						*
 * Modified Date    :  10-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.commodity.commoditydetail;


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
import com.pennant.backend.model.finance.commodity.CommodityDetail;
import com.pennant.backend.service.finance.commodity.CommodityDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.finance.commodity.commoditydetail.model.CommodityDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance.Commodity/CommodityDetail/CommodityDetailList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CommodityDetailListCtrl extends GFCBaseListCtrl<CommodityDetail> implements Serializable {

	private static final long serialVersionUID = -5124936298001620783L;
	private final static Logger logger = Logger.getLogger(CommodityDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL -file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window       window_CommodityDetailList;                       // autoWired
	protected Borderlayout borderLayout_CommodityDetailList;                 // autoWired
	protected Paging       pagingCommodityDetailList;                        // autoWired
	protected Listbox      listBoxCommodityDetail;                           // autoWired

	// List headers
	protected Listheader   listheader_CommodityCode;                         // autoWired
	protected Listheader   listheader_CommodityName;                         // autoWired
	protected Listheader   listheader_CommodityUnitCode;                     // autoWired
	protected Listheader   listheader_CommodityUnitName;                     // autoWired
	protected Listheader   listheader_RecordStatus;                          // autoWired
	protected Listheader   listheader_RecordType;                            // autoWired

	// checkRights
	protected Button btnHelp;                                                // autoWired
	protected Button button_CommodityDetailList_NewCommodityDetail;          // autoWired
	protected Button button_CommodityDetailList_CommodityDetailSearchDialog; // autoWired
	protected Button button_CommodityDetailList_PrintList;                   // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CommodityDetail> searchObj;

	private transient CommodityDetailService commodityDetailService;
	private transient WorkFlowDetails        workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public CommodityDetailListCtrl() {
		super();
	}

	public void onCreate$window_CommodityDetailList(Event event) throws Exception {
		logger.debug("Entering");

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CommodityDetail");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CommodityDetail");

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

		this.borderLayout_CommodityDetailList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCommodityDetailList.setPageSize(getListRows());
		this.pagingCommodityDetailList.setDetailed(true);

		this.listheader_CommodityCode.setSortAscending(new FieldComparator("commodityCode", true));
		this.listheader_CommodityCode.setSortDescending(new FieldComparator("commodityCode", false));
		this.listheader_CommodityName.setSortAscending(new FieldComparator("commodityName", true));
		this.listheader_CommodityName.setSortDescending(new FieldComparator("commodityName", false));
		this.listheader_CommodityUnitCode.setSortAscending(new FieldComparator("commodityUnitCode", true));
		this.listheader_CommodityUnitCode.setSortDescending(new FieldComparator("commodityUnitCode", false));
		this.listheader_CommodityUnitName.setSortAscending(new FieldComparator("commodityUnitName", true));
		this.listheader_CommodityUnitName.setSortDescending(new FieldComparator("commodityUnitName", false));

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
		this.searchObj = new JdbcSearchObject<CommodityDetail>(CommodityDetail.class,getListRows());
		this.searchObj.addSort("CommodityCode", false);
		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("FCMTCommodityDetail_View");
			if (isFirstTask()) {
				button_CommodityDetailList_NewCommodityDetail.setVisible(true);
			} else {
				button_CommodityDetailList_NewCommodityDetail.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("FCMTCommodityDetail_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_CommodityDetailList_NewCommodityDetail.setVisible(false);
			this.button_CommodityDetailList_CommodityDetailSearchDialog.setVisible(false);
			this.button_CommodityDetailList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxCommodityDetail,this.pagingCommodityDetailList);
			// set the itemRenderer
			this.listBoxCommodityDetail.setItemRenderer(new CommodityDetailListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CommodityDetailList");

		this.button_CommodityDetailList_NewCommodityDetail.setVisible(getUserWorkspace().isAllowed("button_CommodityDetailList_NewCommodityDetail"));
		this.button_CommodityDetailList_CommodityDetailSearchDialog.setVisible(getUserWorkspace().isAllowed("button_CommodityDetailList_CommodityDetailFindDialog"));
		this.button_CommodityDetailList_PrintList.setVisible(getUserWorkspace().isAllowed("button_CommodityDetailList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.finance.commodity.commoditydetail.model.CommodityDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onCommodityDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		// get the selected CommodityDetail object
		final Listitem item = this.listBoxCommodityDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CommodityDetail aCommodityDetail = (CommodityDetail) item.getAttribute("data");
			final CommodityDetail commodityDetail = getCommodityDetailService().getCommodityDetailById(aCommodityDetail);

			if(commodityDetail==null){
				String[] errParm= new String[2];
				String[] valueParm= new String[2];
				valueParm[0]=aCommodityDetail.getId();
				valueParm[1]=aCommodityDetail.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_CommodityCode")+":"+valueParm[0];
				errParm[1]=PennantJavaUtil.getLabel("label_CommodityUnitCode")+":"+valueParm[1];
				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND CommodityCode='"+ commodityDetail.getCommodityCode()
					+"' AND version=" + commodityDetail.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId()
							,getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "CommodityDetail", whereCond, commodityDetail.getTaskId(), commodityDetail.getNextTaskId());
					if (userAcces){
						showDetailView(commodityDetail);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(commodityDetail);
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}
	/**
	 * Call the CommodityDetail dialog with a new empty entry. <br>
	 */
	public void onClick$button_CommodityDetailList_NewCommodityDetail(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		// create a new CommodityDetail object, We GET it from the back end.
		final CommodityDetail aCommodityDetail = getCommodityDetailService().getNewCommodityDetail();
		showDetailView(aCommodityDetail);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param CommodityDetail (aCommodityDetail)
	 * @throws Exception
	 */
	private void showDetailView(CommodityDetail aCommodityDetail) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if(aCommodityDetail.getWorkflowId()==0 && isWorkFlowEnabled()){
			aCommodityDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("commodityDetail", aCommodityDetail);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the CommodityDetailListbox from the
		 * dialog when we do a delete, edit or insert a CommodityDetail.
		 */
		map.put("commodityDetailListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Commodity/CommodityDetail/CommodityDetailDialog.zul",null,map);
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
		logger.debug("Entering " + event.toString());
		PTMessageUtils.showHelpWindow(event, window_CommodityDetailList);
		logger.debug("Leaving " + event.toString());
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
		logger.debug("Entering " + event.toString());
		this.pagingCommodityDetailList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CommodityDetailList, event);
		this.window_CommodityDetailList.invalidate();
		logger.debug("Leaving " + event.toString());
	}

	/*
	 * call the CommodityDetail dialog
	 */

	public void onClick$button_CommodityDetailList_CommodityDetailSearchDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		/*
		 * we can call our CommodityDetailDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected CommodityDetail. For handed over
		 * these parameter only a Map is accepted. So we put the CommodityDetail object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("commodityDetailCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Commodity/CommodityDetail/CommodityDetailSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When the commodityDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CommodityDetailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		logger.debug(event.toString());
		new PTListReportUtils("CommodityDetail", getSearchObj(),this.pagingCommodityDetailList.getTotalSize()+1);
		logger.debug("Leaving " + event.toString());
	}

	public void setCommodityDetailService(CommodityDetailService commodityDetailService) {
		this.commodityDetailService = commodityDetailService;
	}

	public CommodityDetailService getCommodityDetailService() {
		return this.commodityDetailService;
	}

	public JdbcSearchObject<CommodityDetail> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<CommodityDetail> searchObj) {
		this.searchObj = searchObj;
	}
}
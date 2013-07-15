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
 * FileName    		:  CommodityBrokerDetailListCtrl.java                                                   * 	  
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

package com.pennant.webui.finance.commodity.commoditybrokerdetail;


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
import com.pennant.backend.model.finance.commodity.CommodityBrokerDetail;
import com.pennant.backend.service.finance.commodity.CommodityBrokerDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.finance.commodity.commoditybrokerdetail.model.CommodityBrokerDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance.Commodity/CommodityBrokerDetail/CommodityBrokerDetailList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CommodityBrokerDetailListCtrl extends GFCBaseListCtrl<CommodityBrokerDetail> 
implements Serializable {

	private static final long serialVersionUID = -6540154685309200504L;
	private final static Logger logger = Logger.getLogger(CommodityBrokerDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window       window_CommodityBrokerDetailList;                           // autoWired
	protected Borderlayout borderLayout_CommodityBrokerDetailList;                     // autoWired
	protected Paging       pagingCommodityBrokerDetailList;                            // autoWired
	protected Listbox      listBoxCommodityBrokerDetail;                               // autoWired

	// List headers
	protected Listheader   listheader_BrokerCode;                                       // autoWired
	protected Listheader   listheader_BrokerCIF;                                        // autoWired
	protected Listheader   listheader_BrokerFrom;                                       // autoWired
	protected Listheader   listheader_BrokerAddrHNbr;                                   // autoWired
	protected Listheader   listheader_BrokerAddrFlatNbr;                                // autoWired
	protected Listheader   listheader_RecordStatus;                                     // autoWired
	protected Listheader   listheader_RecordType;                                       // autoWired

	// checkRights
	protected Button btnHelp;                                                           // autoWired
	protected Button button_CommodityBrokerDetailList_NewCommodityBrokerDetail;         // autoWired
	protected Button button_CommodityBrokerDetailList_CommodityBrokerDetailSearchDialog;// autoWired
	protected Button button_CommodityBrokerDetailList_PrintList;                        // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CommodityBrokerDetail> searchObj;

	private transient CommodityBrokerDetailService commodityBrokerDetailService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public CommodityBrokerDetailListCtrl() {
		super();
	}

	public void onCreate$window_CommodityBrokerDetailList(Event event) throws Exception {
		logger.debug("Entering");

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CommodityBrokerDetail");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CommodityBrokerDetail");

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

		this.borderLayout_CommodityBrokerDetailList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCommodityBrokerDetailList.setPageSize(getListRows());
		this.pagingCommodityBrokerDetailList.setDetailed(true);

		this.listheader_BrokerCode.setSortAscending(new FieldComparator("brokerCode", true));
		this.listheader_BrokerCode.setSortDescending(new FieldComparator("brokerCode", false));
		this.listheader_BrokerCIF.setSortAscending(new FieldComparator("lovDescBrokerCIF", true));
		this.listheader_BrokerCIF.setSortDescending(new FieldComparator("lovDescBrokerCIF", false));
		this.listheader_BrokerFrom.setSortAscending(new FieldComparator("brokerFrom", true));
		this.listheader_BrokerFrom.setSortDescending(new FieldComparator("brokerFrom", false));
		this.listheader_BrokerAddrHNbr.setSortAscending(new FieldComparator("brokerAddrHNbr", true));
		this.listheader_BrokerAddrHNbr.setSortDescending(new FieldComparator("brokerAddrHNbr", false));
		this.listheader_BrokerAddrFlatNbr.setSortAscending(new FieldComparator("brokerAddrFlatNbr", true));
		this.listheader_BrokerAddrFlatNbr.setSortDescending(new FieldComparator("brokerAddrFlatNbr", false));

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
		this.searchObj = new JdbcSearchObject<CommodityBrokerDetail>(CommodityBrokerDetail.class,getListRows());
		this.searchObj.addSort("BrokerCode", false);
		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("FCMTBrokerDetail_View");
			if (isFirstTask()) {
				button_CommodityBrokerDetailList_NewCommodityBrokerDetail.setVisible(true);
			} else {
				button_CommodityBrokerDetailList_NewCommodityBrokerDetail.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("FCMTBrokerDetail_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_CommodityBrokerDetailList_NewCommodityBrokerDetail.setVisible(false);
			this.button_CommodityBrokerDetailList_CommodityBrokerDetailSearchDialog.setVisible(false);
			this.button_CommodityBrokerDetailList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxCommodityBrokerDetail
					,this.pagingCommodityBrokerDetailList);
			// set the itemRenderer
			this.listBoxCommodityBrokerDetail.setItemRenderer(new CommodityBrokerDetailListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CommodityBrokerDetailList");

		this.button_CommodityBrokerDetailList_NewCommodityBrokerDetail
		.setVisible(getUserWorkspace().isAllowed("button_CommodityBrokerDetailList_NewCommodityBrokerDetail"));
		this.button_CommodityBrokerDetailList_CommodityBrokerDetailSearchDialog
		.setVisible(getUserWorkspace().isAllowed("button_CommodityBrokerDetailList_CommodityBrokerDetailFindDialog"));
		this.button_CommodityBrokerDetailList_PrintList
		.setVisible(getUserWorkspace().isAllowed("button_CommodityBrokerDetailList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.finance.commodity.commoditybrokerdetail.model.CommodityBrokerDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onCommodityBrokerDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected CommodityBrokerDetail object
		final Listitem item = this.listBoxCommodityBrokerDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CommodityBrokerDetail aCommodityBrokerDetail = (CommodityBrokerDetail) item.getAttribute("data");
			final CommodityBrokerDetail commodityBrokerDetail = getCommodityBrokerDetailService()
			.getCommodityBrokerDetailById(aCommodityBrokerDetail.getId());

			if(commodityBrokerDetail==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aCommodityBrokerDetail.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_BrokerCode")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD
						,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND BrokerCode='"+ commodityBrokerDetail.getBrokerCode()
					+"' AND version=" + commodityBrokerDetail.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID()
							, "CommodityBrokerDetail", whereCond, commodityBrokerDetail.getTaskId(), commodityBrokerDetail.getNextTaskId());
					if (userAcces){
						showDetailView(commodityBrokerDetail);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(commodityBrokerDetail);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the CommodityBrokerDetail dialog with a new empty entry. <br>
	 */
	public void onClick$button_CommodityBrokerDetailList_NewCommodityBrokerDetail(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new CommodityBrokerDetail object, We GET it from the back end.
		final CommodityBrokerDetail aCommodityBrokerDetail = getCommodityBrokerDetailService().getNewCommodityBrokerDetail();
		showDetailView(aCommodityBrokerDetail);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param CommodityBrokerDetail (aCommodityBrokerDetail)
	 * @throws Exception
	 */
	private void showDetailView(CommodityBrokerDetail aCommodityBrokerDetail) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if(aCommodityBrokerDetail.getWorkflowId()==0 && isWorkFlowEnabled()){
			aCommodityBrokerDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("commodityBrokerDetail", aCommodityBrokerDetail);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the CommodityBrokerDetailListbox from the
		 * dialog when we do a delete, edit or insert a CommodityBrokerDetail.
		 */
		map.put("commodityBrokerDetailListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Commodity/CommodityBrokerDetail/CommodityBrokerDetailDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_CommodityBrokerDetailList);
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
		this.pagingCommodityBrokerDetailList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CommodityBrokerDetailList, event);
		this.window_CommodityBrokerDetailList.invalidate();
		logger.debug("Leaving " + event.toString());
	}

	/*
	 * call the CommodityBrokerDetail dialog
	 */

	public void onClick$button_CommodityBrokerDetailList_CommodityBrokerDetailSearchDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		/*
		 * we can call our CommodityBrokerDetailDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected CommodityBrokerDetail. For handed over
		 * these parameter only a Map is accepted. So we put the CommodityBrokerDetail object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("commodityBrokerDetailCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Commodity/CommodityBrokerDetail/CommodityBrokerDetailSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When the commodityBrokerDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_CommodityBrokerDetailList_PrintList(Event event) 
	throws InterruptedException {

		logger.debug("Entering " + event.toString());
		PTReportUtils.getReport("CommodityBrokerDetail", getSearchObj());
		logger.debug("Leaving " + event.toString());
	}

	public void setCommodityBrokerDetailService(CommodityBrokerDetailService commodityBrokerDetailService) {
		this.commodityBrokerDetailService = commodityBrokerDetailService;
	}

	public CommodityBrokerDetailService getCommodityBrokerDetailService() {
		return this.commodityBrokerDetailService;
	}

	public JdbcSearchObject<CommodityBrokerDetail> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<CommodityBrokerDetail> searchObj) {
		this.searchObj = searchObj;
	}
}
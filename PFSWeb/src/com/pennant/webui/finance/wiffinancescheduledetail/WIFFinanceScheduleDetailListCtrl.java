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
 * FileName    		:  WIFFinanceScheduleDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.wiffinancescheduledetail;


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
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.finance.WIFFinanceScheduleDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.finance.wiffinancescheduledetail.model.WIFFinanceScheduleDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance/WIFFinanceScheduleDetail/WIFFinanceScheduleDetailList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class WIFFinanceScheduleDetailListCtrl extends GFCBaseListCtrl<FinanceScheduleDetail> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(WIFFinanceScheduleDetailListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_WIFFinanceScheduleDetailList; // autowired
	protected Borderlayout borderLayout_WIFFinanceScheduleDetailList; // autowired
	protected Paging pagingWIFFinanceScheduleDetailList; // autowired
	protected Listbox listBoxWIFFinanceScheduleDetail; // autowired

	// List headers
	protected Listheader listheader_FinReference; // autowired
	protected Listheader listheader_SchDate; // autowired
	protected Listheader listheader_SchSeq; // autowired
	protected Listheader listheader_BalanceForPftCal; // autowired
	protected Listheader listheader_DiffProfitSchd; // autowired
	protected Listheader listheader_DIffPrincipalSchd; // autowired
	protected Listheader listheader_ClosingBalance; // autowired
	protected Listheader listheader_PrvRepayAmount; // autowired
	protected Listheader listheader_DeffProfitBal; // autowired
	protected Listheader listheader_DiffPrincipalBal; // autowired
	protected Listheader listheader_SchdPriPaid; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; // autowired
	protected Button button_WIFFinanceScheduleDetailList_NewWIFFinanceScheduleDetail; // autowired
	protected Button button_WIFFinanceScheduleDetailList_WIFFinanceScheduleDetailSearchDialog; // autowired
	protected Button button_WIFFinanceScheduleDetailList_PrintList; // autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<FinanceScheduleDetail> searchObj;
	
	private transient WIFFinanceScheduleDetailService wIFFinanceScheduleDetailService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public WIFFinanceScheduleDetailListCtrl() {
		super();
	}

	public void onCreate$window_WIFFinanceScheduleDetailList(Event event) throws Exception {
		logger.debug("Entering");
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("WIFFinanceScheduleDetail");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("WIFFinanceScheduleDetail");
			
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
		
		this.borderLayout_WIFFinanceScheduleDetailList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingWIFFinanceScheduleDetailList.setPageSize(getListRows());
		this.pagingWIFFinanceScheduleDetailList.setDetailed(true);

		this.listheader_FinReference.setSortAscending(new FieldComparator("finReference", true));
		this.listheader_FinReference.setSortDescending(new FieldComparator("finReference", false));
		this.listheader_SchDate.setSortAscending(new FieldComparator("schDate", true));
		this.listheader_SchDate.setSortDescending(new FieldComparator("schDate", false));
		this.listheader_SchSeq.setSortAscending(new FieldComparator("schSeq", true));
		this.listheader_SchSeq.setSortDescending(new FieldComparator("schSeq", false));
		this.listheader_BalanceForPftCal.setSortAscending(new FieldComparator("balanceForPftCal", true));
		this.listheader_BalanceForPftCal.setSortDescending(new FieldComparator("balanceForPftCal", false));
		this.listheader_DiffProfitSchd.setSortAscending(new FieldComparator("diffProfitSchd", true));
		this.listheader_DiffProfitSchd.setSortDescending(new FieldComparator("diffProfitSchd", false));
		this.listheader_DIffPrincipalSchd.setSortAscending(new FieldComparator("dIffPrincipalSchd", true));
		this.listheader_DIffPrincipalSchd.setSortDescending(new FieldComparator("dIffPrincipalSchd", false));
		this.listheader_ClosingBalance.setSortAscending(new FieldComparator("closingBalance", true));
		this.listheader_ClosingBalance.setSortDescending(new FieldComparator("closingBalance", false));
		this.listheader_PrvRepayAmount.setSortAscending(new FieldComparator("prvRepayAmount", true));
		this.listheader_PrvRepayAmount.setSortDescending(new FieldComparator("prvRepayAmount", false));
		this.listheader_DeffProfitBal.setSortAscending(new FieldComparator("deffProfitBal", true));
		this.listheader_DeffProfitBal.setSortDescending(new FieldComparator("deffProfitBal", false));
		this.listheader_DiffPrincipalBal.setSortAscending(new FieldComparator("diffPrincipalBal", true));
		this.listheader_DiffPrincipalBal.setSortDescending(new FieldComparator("diffPrincipalBal", false));
		this.listheader_SchdPriPaid.setSortAscending(new FieldComparator("schdPriPaid", true));
		this.listheader_SchdPriPaid.setSortDescending(new FieldComparator("schdPriPaid", false));
		
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
		this.searchObj = new JdbcSearchObject<FinanceScheduleDetail>(FinanceScheduleDetail.class,getListRows());
		this.searchObj.addSort("FinReference", false);
		// Workflow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("WIFFinScheduleDetails_View");
			if (isFirstTask()) {
				button_WIFFinanceScheduleDetailList_NewWIFFinanceScheduleDetail.setVisible(true);
			} else {
				button_WIFFinanceScheduleDetailList_NewWIFFinanceScheduleDetail.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("WIFFinScheduleDetails_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_WIFFinanceScheduleDetailList_NewWIFFinanceScheduleDetail.setVisible(false);
			this.button_WIFFinanceScheduleDetailList_WIFFinanceScheduleDetailSearchDialog.setVisible(false);
			this.button_WIFFinanceScheduleDetailList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxWIFFinanceScheduleDetail,this.pagingWIFFinanceScheduleDetailList);
			// set the itemRenderer
			this.listBoxWIFFinanceScheduleDetail.setItemRenderer(new WIFFinanceScheduleDetailListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("WIFFinanceScheduleDetailList");
		
		this.button_WIFFinanceScheduleDetailList_NewWIFFinanceScheduleDetail.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceScheduleDetailList_NewWIFFinanceScheduleDetail"));
		this.button_WIFFinanceScheduleDetailList_WIFFinanceScheduleDetailSearchDialog.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceScheduleDetailList_WIFFinanceScheduleDetailFindDialog"));
		this.button_WIFFinanceScheduleDetailList_PrintList.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceScheduleDetailList_PrintList"));
	logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.finance.wiffinancescheduledetail.model.WIFFinanceScheduleDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onWIFFinanceScheduleDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug(event.toString());

		// get the selected WIFFinanceScheduleDetail object
		final Listitem item = this.listBoxWIFFinanceScheduleDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceScheduleDetail aWIFFinanceScheduleDetail = (FinanceScheduleDetail) item.getAttribute("data");
			final FinanceScheduleDetail wIFFinanceScheduleDetail = getWIFFinanceScheduleDetailService().getWIFFinanceScheduleDetailById(aWIFFinanceScheduleDetail.getId());
			
			if(wIFFinanceScheduleDetail==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aWIFFinanceScheduleDetail.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinReference='"+ wIFFinanceScheduleDetail.getFinReference()+"' AND version=" + wIFFinanceScheduleDetail.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "WIFFinanceScheduleDetail", whereCond, wIFFinanceScheduleDetail.getTaskId(), wIFFinanceScheduleDetail.getNextTaskId());
					if (userAcces){
						showDetailView(wIFFinanceScheduleDetail);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(wIFFinanceScheduleDetail);
				}
			}	
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the WIFFinanceScheduleDetail dialog with a new empty entry. <br>
	 */
	public void onClick$button_WIFFinanceScheduleDetailList_NewWIFFinanceScheduleDetail(Event event) throws Exception {
		logger.debug(event.toString());
		// create a new WIFFinanceScheduleDetail object, We GET it from the backend.
		final FinanceScheduleDetail aWIFFinanceScheduleDetail = getWIFFinanceScheduleDetailService().getNewWIFFinanceScheduleDetail();
		showDetailView(aWIFFinanceScheduleDetail);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param WIFFinanceScheduleDetail (aWIFFinanceScheduleDetail)
	 * @throws Exception
	 */
	private void showDetailView(FinanceScheduleDetail aWIFFinanceScheduleDetail) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aWIFFinanceScheduleDetail.getWorkflowId()==0 && isWorkFlowEnabled()){
			aWIFFinanceScheduleDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("wIFFinanceScheduleDetail", aWIFFinanceScheduleDetail);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the WIFFinanceScheduleDetailListbox from the
		 * dialog when we do a delete, edit or insert a WIFFinanceScheduleDetail.
		 */
		map.put("wIFFinanceScheduleDetailListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/WIFFinanceScheduleDetail/WIFFinanceScheduleDetailDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_WIFFinanceScheduleDetailList);
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
		this.pagingWIFFinanceScheduleDetailList.setActivePage(0);
		Events.postEvent("onCreate", this.window_WIFFinanceScheduleDetailList, event);
		this.window_WIFFinanceScheduleDetailList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the WIFFinanceScheduleDetail dialog
	 */
	
	public void onClick$button_WIFFinanceScheduleDetailList_WIFFinanceScheduleDetailSearchDialog(Event event) throws Exception {
		logger.debug("Entering");
		logger.debug(event.toString());
		/*
		 * we can call our WIFFinanceScheduleDetailDialog zul-file with parameters. So we can
		 * call them with a object of the selected WIFFinanceScheduleDetail. For handed over
		 * these parameter only a Map is accepted. So we put the WIFFinanceScheduleDetail object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("wIFFinanceScheduleDetailCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/WIFFinanceScheduleDetail/WIFFinanceScheduleDetailSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the wIFFinanceScheduleDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_WIFFinanceScheduleDetailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering");
		logger.debug(event.toString());
		new PTListReportUtils("WIFFinanceScheduleDetail", getSearchObj(),this.pagingWIFFinanceScheduleDetailList.getTotalSize()+1);
		logger.debug("Leaving");
	}

	public void setWIFFinanceScheduleDetailService(WIFFinanceScheduleDetailService wIFFinanceScheduleDetailService) {
		this.wIFFinanceScheduleDetailService = wIFFinanceScheduleDetailService;
	}

	public WIFFinanceScheduleDetailService getWIFFinanceScheduleDetailService() {
		return this.wIFFinanceScheduleDetailService;
	}

	public JdbcSearchObject<FinanceScheduleDetail> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<FinanceScheduleDetail> searchObj) {
		this.searchObj = searchObj;
	}
}
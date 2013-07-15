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
 * FileName    		:  ProvinceListCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.province;

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
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.systemmasters.ProvinceService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.systemmasters.province.model.ProvinceListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/Province/ProvinceList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class ProvinceListCtrl extends GFCBaseListCtrl<Province> implements Serializable {

	private static final long serialVersionUID = -3109779707000635809L;
	private final static Logger logger = Logger.getLogger(ProvinceListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_ProvinceList; 		        // autoWired
	protected Borderlayout 	borderLayout_ProvinceList;      	// autoWired
	protected Paging 		pagingProvinceList; 		        // autoWired
	protected Listbox 		listBoxProvince; 			        // autoWired

	// List headers
	protected Listheader listheader_CPCountry; 		            // autoWired
	protected Listheader listheader_CPProvince; 	            // autoWired
	protected Listheader listheader_CPProvinceName;             // autoWired
	protected Listheader listheader_RecordStatus; 	            // autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 									// autoWired
	protected Button button_ProvinceList_NewProvince; 			// autoWired
	protected Button button_ProvinceList_ProvinceSearchDialog; 	// autoWired
	protected Button button_ProvinceList_PrintList; 			// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Province> searchObj;

	private transient ProvinceService provinceService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public ProvinceListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Province object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ProvinceList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Province");
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Province");

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
		this.borderLayout_ProvinceList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingProvinceList.setPageSize(getListRows());
		this.pagingProvinceList.setDetailed(true);

		//Apply sorting for getting List in the ListBox 
		this.listheader_CPCountry.setSortAscending(new FieldComparator("cPCountry", true));
		this.listheader_CPCountry.setSortDescending(new FieldComparator("cPCountry", false));
		this.listheader_CPProvince.setSortAscending(new FieldComparator("cPProvince", true));
		this.listheader_CPProvince.setSortDescending(new FieldComparator("cPProvince", false));
		this.listheader_CPProvinceName.setSortAscending(new FieldComparator("cPProvinceName", true));
		this.listheader_CPProvinceName.setSortDescending(new FieldComparator("cPProvinceName", false));

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
		this.searchObj = new JdbcSearchObject<Province>(Province.class,getListRows());
		this.searchObj.addSort("CPCountry", false);

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("RMTCountryVsProvince_View");
			if (isFirstTask()) {
				button_ProvinceList_NewProvince.setVisible(true);
			} else {
				button_ProvinceList_NewProvince.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		}else{
			this.searchObj.addTabelName("RMTCountryVsProvince_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_ProvinceList_NewProvince.setVisible(false);
			this.button_ProvinceList_ProvinceSearchDialog.setVisible(false);
			this.button_ProvinceList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxProvince,this.pagingProvinceList);
			// set the itemRenderer
			this.listBoxProvince.setItemRenderer(new ProvinceListModelItemRenderer());
		}	
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("ProvinceList");

		this.button_ProvinceList_NewProvince.setVisible(getUserWorkspace()
				.isAllowed("button_ProvinceList_NewProvince"));
		this.button_ProvinceList_ProvinceSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_ProvinceList_ProvinceFindDialog"));
		this.button_ProvinceList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_ProvinceList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.rmtmasters.province.model.
	 * ProvinceListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onProvinceItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Province object
		final Listitem item = this.listBoxProvince.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Province aProvince = (Province) item.getAttribute("data");
			final Province province = getProvinceService().getProvinceById(
					aProvince.getCPCountry(), aProvince.getCPProvince());
			if(province==null){

				String[] valueParm = new String[2];
				String[] errParm= new String[2];

				valueParm[0] = aProvince.getCPCountry();
				valueParm[1] = aProvince.getCPProvince();

				errParm[0] = PennantJavaUtil.getLabel("label_CPCountry") + ":"+ valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_CPProvince") + ":"+valueParm[1];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				String whereCond =  " AND CPCountry='"+ province.getCPCountry()+"'"+
					"AND CPProvince='"+province.getCPProvince()+ "'AND version="+ province.getVersion()+"";

				if(isWorkFlowEnabled()){
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),
							getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "Province", 
							whereCond, province.getTaskId(), province.getNextTaskId());
					if (userAcces){
						showDetailView(province);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(province);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Province dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_ProvinceList_NewProvince(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new Province object, We GET it from the backEnd.
		final Province aProvince = getProvinceService().getNewProvince();
		showDetailView(aProvince);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * overHanded some parameters in a map if needed. <br>
	 * 
	 * @param Province (aProvince)
	 * @throws Exception
	 */
	private void showDetailView(Province aProvince) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if(aProvince.getWorkflowId()==0 && isWorkFlowEnabled()){
			aProvince.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("province", aProvince);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the ProvinceListbox from the
		 * dialog when we do a delete, edit or insert a Province.
		 */
		map.put("provinceListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/Province/ProvinceDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_ProvinceList);
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
		this.pagingProvinceList.setActivePage(0);
		Events.postEvent("onCreate", this.window_ProvinceList, event);
		this.window_ProvinceList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the Province dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_ProvinceList_ProvinceSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our ProvinceDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected Province. For handed over
		 * these parameter only a Map is accepted. So we put the Province object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("provinceCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/Province/ProvinceSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the province print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_ProvinceList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("Province", getSearchObj(),this.pagingProvinceList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString()); 
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setProvinceService(ProvinceService provinceService) {
		this.provinceService = provinceService;
	}
	public ProvinceService getProvinceService() {
		return this.provinceService;
	}

	public JdbcSearchObject<Province> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<Province> searchObj) {
		this.searchObj = searchObj;
	}

}
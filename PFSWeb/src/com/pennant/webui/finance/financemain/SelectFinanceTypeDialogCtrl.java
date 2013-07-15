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
 * FileName    		:  FinanceMainQDEDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-11-2011    														*
 *                                                                  						*
 * Modified Date    :  16-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.financemain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/FinanceMain/ SelectFinanceTypeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class SelectFinanceTypeDialogCtrl extends GFCBaseCtrl implements Serializable{


	private static final long serialVersionUID = 8556168885363682933L;
	private final static Logger logger = Logger.getLogger(SelectFinanceTypeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWiredd by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window       window_SelectFinanceTypeDialog;            // autoWired
	protected Textbox      finType;                                   // autoWired
	protected Textbox      wIfFinaceRef;                              // autoWired
	protected Textbox      lovDescFinTypeName;                        // autoWired
	protected Button       btnSearchWIFFinaceRef;                     // autoWired
	protected Button       btnProceed;                                // autoWired
	protected FinanceMainListCtrl               financeMainListCtrl;  //over handed parameter
	protected transient FinanceWorkFlow         financeWorkFlow;
	private transient   WorkFlowDetails         workFlowDetails=null;
	private FinanceDetail financeDetail =null;
	private FinanceType financeType =null;
	private List<String> userRoleCodeList = new ArrayList<String>();
	private String userRoleCode =null;
	private String screenCode =null;
	private String loanType = "";
	
	private transient FinanceTypeService      financeTypeService;
	private transient   FinanceWorkFlowService  financeWorkFlowService;
	private transient   FinanceDetailService    financeDetailService;   

	/**
	 * default constructor.<br>
	 */
	public SelectFinanceTypeDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //


	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceMain object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_SelectFinanceTypeDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) args.get("financeDetail");
			FinanceDetail befImage = new FinanceDetail();
			BeanUtils.copyProperties(this.financeDetail, befImage);
			this.financeDetail.setBefImage(befImage);

			setFinanceDetail(this.financeDetail);
		} else {
			setFinanceDetail(null);
		}
		if (args.containsKey("financeMainListCtrl")) {
			this.financeMainListCtrl = (FinanceMainListCtrl) args.get("financeMainListCtrl");
			setFinanceMainListCtrl(this.financeMainListCtrl);
		} else {
			setFinanceMainListCtrl(null);;
		}
		
		if (args.containsKey("loanType")) {
			this.loanType = (String) args.get("loanType");
		}
		
		if (args.containsKey("role")) {
			userRoleCodeList = (ArrayList<String>) args.get("role");
			this.userRoleCode = userRoleCodeList.get(0);//FIXME--Temperary purpose
			if(this.userRoleCode.startsWith("FINANCE_QDE_")){//TODO - Hard COde need to give a second look
				this.screenCode = "QDE";
			}else{
				this.screenCode = "DDE";
			}
		}
		showSelectFinanceTypeDialog();
		logger.debug("Leaving " + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * When user clicks on button "SearchFinType" button
	 * @param event
	 */
	public void onClick$btnSearchFinType(Event event){
		logger.debug("Entering " + event.toString());
		String finType=this.finType.getValue();
		Filter[] filters;
		if(!StringUtils.trimToEmpty(this.loanType).equals("")){
			filters = new Filter[3] ;
		}else{
			filters = new Filter[2] ;
		}
		filters[0]= new Filter("ScreenCode", this.screenCode, Filter.OP_EQUAL);
		filters[1]= Filter.in("lovDescFirstTaskOwner", userRoleCodeList);
		if(!StringUtils.trimToEmpty(this.loanType).equals("")){
			filters[2]= new Filter("lovDescProductCodeName", this.loanType, Filter.OP_EQUAL);
		}
		Object dataObject = ExtendedSearchListBox.show(this.window_SelectFinanceTypeDialog,"FinanceWorkFlow",filters);
		if (dataObject instanceof String){
			this.finType.setValue(dataObject.toString());
			this.lovDescFinTypeName.setValue("");
		}else{
			FinanceWorkFlow details= (FinanceWorkFlow) dataObject;
			/*Set FinanceWorkFloe object*/
			setFinanceWorkFlow(details);
			if (details != null) {
				this.finType.setValue(details.getFinType());
				this.lovDescFinTypeName.setValue(details.getFinType()+"-"+details.getLovDescFinTypeName());
			}
		}
		if (!StringUtils.trimToEmpty(finType).equals(this.finType.getValue())) {
			this.wIfFinaceRef.setValue("");
		}
		logger.debug("Leaving " + event.toString());
	}
	/**
	 * When user clicks on button "btnSearchWIFFinaceRef" button
	 * @param event
	 */
	public void onClick$btnSearchWIFFinaceRef(Event event) {
		logger.debug("Entering " + event.toString());
		Filter[] filters;
		if(!StringUtils.trimToEmpty(this.loanType).equals("")){
			filters = new Filter[3] ;
		}else{
			filters = new Filter[2] ;
		}
		Object dataObject;
		if(!this.finType.getValue().trim().equals("")){
			filters[0]= new Filter("FinType", this.finType.getValue(), Filter.OP_EQUAL);
			filters[1]= Filter.in("lovDescFirstTaskOwner", userRoleCodeList);
			if(!StringUtils.trimToEmpty(this.loanType).equals("")){
				filters[2]= new Filter("lovDescProductCodeName", this.loanType, Filter.OP_EQUAL);
			}
			dataObject = ExtendedSearchListBox.show(this.window_SelectFinanceTypeDialog,"WIFFinanceMain",filters);
		}else{
			filters[0]= new Filter("lovDescScreenCode", this.screenCode, Filter.OP_EQUAL);
			filters[1]= Filter.in("lovDescFirstTaskOwner", userRoleCodeList);
			if(!StringUtils.trimToEmpty(this.loanType).equals("")){
				filters[2]= new Filter("lovDescProductCodeName", this.loanType, Filter.OP_EQUAL);
			}
			dataObject = ExtendedSearchListBox.show(this.window_SelectFinanceTypeDialog,"WIFFinanceMain",filters);
		}

		if (dataObject instanceof String){
			this.finType.setValue(dataObject.toString());
			this.lovDescFinTypeName.setValue("");
		}else{
			FinanceMain details= (FinanceMain) dataObject;

			if (details != null) {
				this.wIfFinaceRef.setValue(details.getFinReference());	
				if(StringUtils.trimToEmpty(this.finType.getValue()).equals("")){
					FinanceWorkFlow financeWorkFlow=getFinanceWorkFlowService().getFinanceWorkFlowById(details.getFinType());
					setFinanceWorkFlow(financeWorkFlow);
				}
			}
		}
		logger.debug("Leaving " + event.toString());
	}
	/**
	 * When user clicks on button "btnProceed" button
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnProceed(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		
		String screenPath="";
		doFieldValidation();
		this.window_SelectFinanceTypeDialog.onClose();
		
		if(!this.wIfFinaceRef.getValue().trim().equals("")){
			financeDetail = getFinanceDetailService().getFinanceDetailById(this.wIfFinaceRef.getValue().trim(), true,"");
			financeDetail.getFinScheduleData().getFinanceMain().setNewRecord(true);
			financeDetail.getFinScheduleData().getFinanceMain().setRecordType("");
			financeDetail.getFinScheduleData().getFinanceMain().setVersion(0);
		}else{
			financeType = getFinanceTypeService().getApprovedFinanceTypeById(this.finType.getValue().trim());
			financeDetail.getFinScheduleData().setFinanceMain(new FinanceMain(), financeType);
			financeDetail.getFinScheduleData().setFinanceType(financeType);
		}
		
		financeDetail = getFinanceDetailService().getFinanceReferenceDetails(financeDetail, 
				getFinanceWorkFlow().getLovDescFirstTaskOwner(),getFinanceWorkFlow().getScreenCode(),"");
		financeDetail.setNewRecord(true);

		if(getFinanceWorkFlow()!=null){
			workFlowDetails = WorkFlowUtil.getDetailsByType(getFinanceWorkFlow().getWorkFlowType());
		}
		if (workFlowDetails == null) {
			setWorkFlowEnabled(false);
			financeDetail.setWorkflowId(0);
		} else {
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
			financeDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
			financeDetail.getFinScheduleData().getFinanceMain().setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if(getFinanceWorkFlow()!=null){

			/*if screen code is quick data entry (QDE) navigate to QDE screen otherwise navigate to  Detail data entry screen */
			if(getFinanceWorkFlow().getScreenCode().trim().equals("QDE")){
				screenPath="/WEB-INF/pages/Finance/FinanceMain/FinanceMainQDEDialog.zul";
			} else{		
				screenPath="/WEB-INF/pages/Finance/FinanceMain/FinanceMainDialog.zul";
			}

			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeDetail", financeDetail);
			map.put("financeMainListCtrl", 	this.financeMainListCtrl);
			map.put("financeType", financeType);
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents(screenPath,null,map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}else{
			logger.error("work flow not found");
			PTMessageUtils.showErrorMessage(Labels.getLabel("Workflow_Not_Found")+getFinanceDetail().getFinScheduleData().getFinanceMain().getFinType());
		}

		logger.debug("Leaving " + event.toString());
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ GUI Process++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Opens the SelectFinanceTypeDialog window modal.
	 */
	private void showSelectFinanceTypeDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_SelectFinanceTypeDialog.doModal();
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doFieldValidation() {
		logger.debug("Entering ");
		if(StringUtils.trimToEmpty(this.lovDescFinTypeName.getValue()).equals("")
				&& StringUtils.trimToEmpty(this.wIfFinaceRef.getValue()).equals("")){
			throw new WrongValueException(this.lovDescFinTypeName,Labels.getLabel("CHECK_NO_EMPTY_IN_TWO"
					,new String[]{Labels.getLabel("label_SelectFinanceTypeDialog_FinType.value")
							,Labels.getLabel("label_FinanceMainQDEDialog_WIFFinaceRef.value")})); 
		}
		logger.debug("Leaving ");
	}


	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Getters and Setters ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //


	public FinanceMainListCtrl getFinanceMainListCtrl() {
		return financeMainListCtrl;
	}

	public void setFinanceMainListCtrl(FinanceMainListCtrl financeMainListCtrl) {
		this.financeMainListCtrl = financeMainListCtrl;
	}

	/**
	 * @return the financeDetail
	 */
	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	/**
	 * @param financeDetail the financeDetail to set
	 */
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}
	public FinanceWorkFlow getFinanceWorkFlow() {
		return financeWorkFlow;
	}

	public void setFinanceWorkFlow(FinanceWorkFlow financeWorkFlow) {
		this.financeWorkFlow = financeWorkFlow;
	}
	public FinanceTypeService getFinanceTypeService() {
		return financeTypeService;
	}

	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}

	/**
	 * @return the financeDetailService
	 */
	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	/**
	 * @param financeDetailService the financeDetailService to set
	 */
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}
	
}

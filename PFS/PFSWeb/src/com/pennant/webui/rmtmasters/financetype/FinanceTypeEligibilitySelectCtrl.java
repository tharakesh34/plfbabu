/**
Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 *//*

*//**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  FeeListSelectCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-11-2011    														*
 *                                                                  						*
 * Modified Date    :  01-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-11-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 *//*

package com.pennant.webui.rmtmasters.financetype;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.EligibilityRules;
import com.pennant.backend.model.rmtmasters.FinanceEligibility;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.fee.FeeService;
import com.pennant.backend.service.rmtmasters.FinanceEligibilityService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.webui.rmtmasters.financetype.model.FinanceEligibilityItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;

*//**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/RMTMasters/FinanceType/FeeListSelect.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 *//*
public class FinanceTypeEligibilitySelectCtrl extends GFCBaseListCtrl<EligibilityRules> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(FinanceTypeEligibilitySelectCtrl.class);

	
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 
	protected Window window_FinanceTypeElgRuleSelect; // autoWired
	protected Borderlayout borderLayout_FeeList; // autoWired
	protected Paging pagingFeeElgRule; // autoWired
	protected Listbox listboxFeeElgRule; // autoWired

	// List headers
	protected Listheader listheader_FeeElgRuleType; // autoWired
	protected Listheader listheader_FeeElgRuleCode; // autoWired
	protected Listheader listheader_FeeElgRuleDesc; // autoWired
	protected Listheader listheader_ElgRuleRecordStatus;

	// not auto wired vars
	private FinanceEligibility financeEligibility = null;                        
	private transient FeeService feeService;
	private transient FinanceEligibilityService financeEligibilityService;
	private transient PagedListService pagedListService;
	private transient FinanceTypeDialogCtrl financeTypeDialogCtrl; 
	protected JdbcSearchObject<EligibilityRules> searchObj;
	private List<FinanceEligibility> financeEligibilityList;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	

	*//**
	 * default constructor.<br>
	 *//*
	public FinanceTypeEligibilitySelectCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	*//**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceType object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 *//*
	public void onCreate$window_FinanceTypeElgRuleSelect(Event event) throws Exception {

		logger.debug("Entering" + event.toString());

		*//**
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 *//*

		// set the paging parameters
		this.pagingFeeElgRule.setPageSize(10);
		this.pagingFeeElgRule.setDetailed(true);

		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("financeTypeDialogCtrl")) {
			this.setFinanceTypeDialogCtrl((FinanceTypeDialogCtrl) args.get("financeTypeDialogCtrl"));			
		} else {
			this.setFinanceTypeDialogCtrl(null);
		}

		if (args.containsKey("finFeeCharges")) {
			this.financeEligibility = (FinanceEligibility) args.get("finFeeCharges");
			FinanceEligibility befImage =new FinanceEligibility();
			BeanUtils.copyProperties(this.financeEligibility, befImage);
			this.financeEligibility.setBefImage(befImage);

			setFinanceEligibility(this.financeEligibility);
		} else {
			setFinanceEligibility(null);
		}

		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<EligibilityRules>(EligibilityRules.class,getListRows());
		this.searchObj.addSort("ElgRuleCode", false);
		this.searchObj.addTabelName("LMTEligibilityRules");

		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listboxFeeElgRule,this.pagingFeeElgRule);
		// set the itemRenderer
		this.listboxFeeElgRule.setItemRenderer(new FinanceEligibilityItemRenderer());
		doShowDialog(this.financeEligibility);

		logger.debug("Leaving" + event.toString());
	}


	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++  GUI Process ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	*//**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param afinanceEligibility
	 * @throws InterruptedException
	 *//*
	public void doShowDialog(FinanceEligibility afinanceEligibility) throws InterruptedException {
		logger.debug("Entering") ;

		// if aFinanceFeeCharges == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (afinanceEligibility == null) {
			*//** !!! DO NOT BREAK THE TIERS !!! *//*
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.	
			afinanceEligibility = getFinanceEligibilityService().getNewFinanceEligibility();
			setFinanceEligibility(afinanceEligibility);
		} else {
			setFinanceEligibility(afinanceEligibility);
		}
		this.window_FinanceTypeElgRuleSelect.doModal();
		logger.debug("Leaving") ;
	}


	*//**
	 * This method is forwarded from the ListBoxes item renderer. <br>
	 * see: com.pennant.webui.rmtmasters.financetype.model.
	 * FeeListItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 *//*
	public void onFinanceEligibilityItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		//addFeeCharges();
		addFinanceEligibility();
		logger.debug("Leaving" + event.toString());
	}

	*//**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 *//*
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doClose();
	}

	*//**
	 * closes the dialog window
	 * @throws InterruptedException 
	 *//*
	private void doClose() throws InterruptedException {
		this.window_FinanceTypeElgRuleSelect.onClose();
	}

	*//**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 *//*
	public void onClick$btnSelect(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		//addFeeCharges();
		addFinanceEligibility();
		logger.debug("Leaving" + event.toString());
	}

	public void setFeeService(FeeService feeService) {
		this.feeService = feeService;
	}

	public FeeService getFeeService() {
		return feeService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setFinanceTypeDialogCtrl(FinanceTypeDialogCtrl financeTypeDialogCtrl) {
		this.financeTypeDialogCtrl = financeTypeDialogCtrl;
	}

	public FinanceTypeDialogCtrl getFinanceTypeDialogCtrl() {
		return financeTypeDialogCtrl;
	}    

	public void setFinanceEligibility(FinanceEligibility financeEligibility) {
		this.financeEligibility = financeEligibility;
	}

	public FinanceEligibility getFinanceEligibility() {
		return financeEligibility;
	}

	public JdbcSearchObject<EligibilityRules> getSearchObj() {
		return this.searchObj;
	}

	public void setSearchObj(JdbcSearchObject<EligibilityRules> searchObj) {
		this.searchObj = searchObj;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}



	public FinanceEligibilityService getFinanceEligibilityService() {
		return financeEligibilityService;
	}

	public void setFinanceEligibilityService(FinanceEligibilityService financeEligibilityService) {
		this.financeEligibilityService = financeEligibilityService;
	}

	*//**
	 * Method to add fee charges in finance type control
	 * 
	 * **//*
	private void addFinanceEligibility() throws InterruptedException{
		logger.debug("Entering");
		final FinanceEligibility financeEligibility = new FinanceEligibility();
		BeanUtils.copyProperties(getFinanceEligibility(), financeEligibility);
		boolean isNew = false;

		final Listitem item = this.listboxFeeElgRule.getSelectedItem();
		if(item==null){
			doClose();    
		}
		else if(this.financeTypeDialogCtrl!=null) {
			EligibilityRules eligibilityRule = (EligibilityRules) item.getAttribute("data");
			// fill the CustomerRating object with the components data
			financeEligibility.setFinType("");	
			financeEligibility.setElgRuleCode(eligibilityRule.getElgRuleCode());	
			financeEligibility.setLovDescelgRuleDesc(eligibilityRule.getElgRuleDesc());
			// Write the additional validations as per below example
			// get the selected branch object from the listBox
			// Do data level validations here

			isNew = financeEligibility.isNew();
			String tranType="";

			if(isWorkFlowEnabled()){
				tranType = PennantConstants.TRAN_WF;
				if (StringUtils.trimToEmpty(financeEligibility.getRecordType()).equals("")){
					financeEligibility.setVersion(financeEligibility.getVersion()+1);
					if(isNew){
						financeEligibility.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else{
						financeEligibility.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						financeEligibility.setNewRecord(true);
					}
				}
			}else{
				set the tranType according to RecordType
				if(isNew){
					tranType =PennantConstants.TRAN_ADD;
					financeEligibility.setVersion(1);
					financeEligibility.setRecordType(PennantConstants.RCD_ADD);
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}

				if(StringUtils.trimToEmpty(financeEligibility.getRecordType()).equals("")){
					tranType =PennantConstants.TRAN_UPD;
					financeEligibility.setRecordType(PennantConstants.RCD_UPD);
				}
				if(financeEligibility.getRecordType().equals(PennantConstants.RCD_ADD) && isNew){
					tranType =PennantConstants.TRAN_ADD;
				} else if(financeEligibility.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				} 
			}
			try {
				AuditHeader auditHeader =  newFinanceEligibilityProcess(financeEligibility,tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_FinanceTypeElgRuleSelect, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getFinanceTypeDialogCtrl().doFillEligibility(this.financeEligibilityList);

					this.window_FinanceTypeElgRuleSelect.onClose();
				}
			} catch (final DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	*//**
	 * This method added the FinanceFeeCharges object into financeFeeChargesList
	 *  by setting RecordType according to tranType
	 *  <p>eg: 	if(tranType==PennantConstants.TRAN_DEL){
	 *  	aFinanceFeeCharges.setRecordType(PennantConstants.RECORD_TYPE_DEL);
	 *  }</p>
	 * @param  afinanceEligibility (FinanceFeeCharges)
	 * @param  tranType (String)
	 * @return auditHeader (AuditHeader)
	 *//*
	private AuditHeader newFinanceEligibilityProcess(FinanceEligibility afinanceEligibility,String tranType){
		logger.debug("Entering ");
		boolean recordAdded=false;

		AuditHeader auditHeader= getAuditHeader(afinanceEligibility, tranType);
		financeEligibilityList= new ArrayList<FinanceEligibility>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = afinanceEligibility.getElgRuleCode();
		errParm[0] = PennantJavaUtil.getLabel("label_FinanceTypeDialog_FinFeeAndCharges.title") + ":"+valueParm[0];

		if(getFinanceTypeDialogCtrl().getFinanceEligibilityList()!=null && getFinanceTypeDialogCtrl().getFinanceEligibilityList().size()>0){
			for (int i = 0; i < getFinanceTypeDialogCtrl().getFinanceEligibilityList().size(); i++) {
				FinanceEligibility financeEligibility = getFinanceTypeDialogCtrl().getFinanceEligibilityList().get(i);

				if( StringUtils.equals(afinanceEligibility.getElgRuleCode().trim(), financeEligibility.getElgRuleCode().trim())){ 
					if same fee charges added twice set error detail
					if(getFinanceEligibility().isNew()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if(tranType==PennantConstants.TRAN_DEL){
						if(afinanceEligibility.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							afinanceEligibility.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							financeEligibilityList.add(afinanceEligibility);
						}
						else if(afinanceEligibility.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(afinanceEligibility.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							afinanceEligibility.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							financeEligibilityList.add(afinanceEligibility);
						}else if(afinanceEligibility.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getFinanceTypeDialogCtrl().getFinanceEligibilityList().size(); j++) {
								FinanceEligibility finEligibility =  getFinanceTypeDialogCtrl().getFinanceEligibilityList().get(j);
								if(finEligibility.getFinType()== afinanceEligibility.getFinType() && finEligibility.getElgRuleCode().equals(afinanceEligibility.getElgRuleCode())){
									financeEligibilityList.add(finEligibility);
								}
							}
						}
					}else{
						if(tranType!=PennantConstants.TRAN_UPD ){
							financeEligibilityList.add(financeEligibility);
						}
					}
				}else{
					financeEligibilityList.add(financeEligibility);
				}
			}
		}
		if(!recordAdded){
			financeEligibilityList.add(afinanceEligibility);
		}
		return auditHeader;
	} 


	*//**
	 * This method returns new AuditHeader 
	 * @param afinanceEligibility
	 * @param tranType
	 * @return
	 *//*
	private AuditHeader getAuditHeader(FinanceEligibility afinanceEligibility, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinanceEligibility.getBefImage(), afinanceEligibility);   
		return new AuditHeader(String.valueOf(afinanceEligibility.getId()),null,null,null,auditDetail,afinanceEligibility.getUserDetails(),getOverideMap());
	}	

	*//**
	 * 
	 * @param Exception e
	 *//*
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_FinanceTypeElgRuleSelect, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
	}
	
	
	*//**
	 * Deletes a Finance Fee Charge object from database.<br>
	 * 
	 * @throws InterruptedException
	 *//*
	public void doDelete() throws InterruptedException {
		logger.debug("Entering ");

		final FinanceEligibility financeEligibility = new FinanceEligibility();
		BeanUtils.copyProperties(getFinanceEligibility(), financeEligibility);
		String tranType=PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
									+ financeEligibility.getFinType()+" - "+financeEligibility.getElgRuleCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(financeEligibility.getRecordType()).equals("")){
				financeEligibility.setVersion(financeEligibility.getVersion()+1);
				financeEligibility.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				financeEligibility.setNewRecord(true);

				if (isWorkFlowEnabled()){
					financeEligibility.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}
			try {
				tranType=PennantConstants.TRAN_DEL;
				AuditHeader auditHeader =  newFinanceEligibilityProcess(financeEligibility,tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_FinanceTypeElgRuleSelect, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getFinanceTypeDialogCtrl().doFillEligibility(this.financeEligibilityList);

					//this.window_FeeListSelect.onClose();
				}

			}catch (DataAccessException e){
				showMessage(e);
			}
		}
		logger.debug("Leaving ");
	}
	

}
*/
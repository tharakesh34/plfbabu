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
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  SelectPromotionDialogCtrl.java                                       * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-03-2017    														*
 *                                                                  						*
 * Modified Date    :  21-03-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-03-2017       Pennant	                 0.1                                            * 
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
package com.pennant.webui.rmtmasters.promotion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.applicationmaster.FinTypeInsurances;
import com.pennant.backend.model.rmtmasters.FinTypeAccounting;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.service.rmtmasters.FinTypeAccountingService;
import com.pennant.backend.service.rmtmasters.FinTypeFeesService;
import com.pennant.backend.service.rmtmasters.FinTypeInsurancesService;
import com.pennant.backend.service.rmtmasters.PromotionService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/Promotion/SelectPromotionDialog.zul file.
 */
public class SelectPromotionDialogCtrl extends GFCBaseCtrl<Promotion> {

	private static final long serialVersionUID = -5898229156972529248L;
	private static final Logger					logger				= Logger.getLogger(SelectPromotionDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWiredd by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window							window_SelectPromotionDialog;											
	protected ExtendedCombobox					finType;																
	protected Uppercasebox						promotionCode;
	protected Button							btnProceed;
	
	private PromotionListCtrl					promotionListCtrl;
	private Promotion							promotion;
	private PromotionService					promotionService;
	// Child Services
	private FinTypeFeesService 					finTypeFeesService;
	private FinTypeInsurancesService 			finTypeInsurancesService;
	private FinTypeAccountingService 			finTypeAccountingService;

	private String 								finCcy = "";
	/**
	 * default constructor.<br>
	 */
	public SelectPromotionDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "PromotionDialog";
	}
	
	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected FinanceMain object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SelectPromotionDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SelectPromotionDialog);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("promotionListCtrl")) {
			this.promotionListCtrl =  (PromotionListCtrl) arguments.get("promotionListCtrl");
			setPromotionListCtrl(this.promotionListCtrl);
		} else {
			setPromotionListCtrl(null);
		}

		if (arguments.containsKey("promotion")) {
			this.promotion =  (Promotion) arguments.get("promotion");
		}

		doLoadWorkFlow(this.promotion.isWorkflow(),this.promotion.getWorkflowId(),this.promotion.getNextTaskId());
		
		if (isWorkFlowEnabled() && !enqiryModule) {
			getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
		} 

		doSetFieldProperties();
		doCheckRights();		
		
		this.window_SelectPromotionDialog.doModal();
		
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering") ;
		
		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
		
		this.btnProceed.setVisible(getUserWorkspace().isAllowed("button_PromotionDialog_btnSave"));	

		logger.debug("Leaving") ;
	}
	
	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		// Promotion Code
		this.promotionCode.setMaxlength(8);

		// Finance Type
		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("FinCategory");
		this.finType.setDescColumn("FinTypeDesc");
		this.finType.setValidateColumns(new String[] { "FinType", "FinCategory", "FinTypeDesc" });
		this.finType.setMandatoryStyle(true);

		logger.debug("Leaving");
	}
	
	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnProceed(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		doSetValidation();		
		
		doWriteComponentsToBean(this.promotion);
		
		doShowDialogPage(this.promotion);
		
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param promotion
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Promotion promotion) {
		logger.debug("Entering");

		Map<String, Object> aruments = new HashMap<String, Object>();

		aruments.put("promotion", promotion);
		aruments.put("promotionListCtrl", this.promotionListCtrl);
		aruments.put("moduleCode", moduleCode);
		aruments.put("enqiryModule", enqiryModule);

		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/Promotion/PromotionDialog.zul", null, aruments);
			this.window_SelectPromotionDialog.onClose();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}
	
	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		
		//Code
		if (!this.promotionCode.isReadonly()){
			this.promotionCode.setConstraint(new PTStringValidator(Labels.getLabel("label_PromotionDialog_PromotionCode.value"),PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM,true));
		}
		
		//Finance Type
		this.finType.setConstraint(new PTStringValidator(Labels.getLabel("label_PromotionDialog_FinType.value"), null, true, true));
		
		logger.debug("Leaving");
	}
	
	public void onFulfill$finType(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = finType.getObject();
		
		if (dataObject instanceof String) {
			this.finType.setValue(dataObject.toString());
			this.finType.setDescription("");
		} else {
			FinanceType financeType = (FinanceType) dataObject;
			if (financeType != null) {
				this.finType.setValue(financeType.getFinType());
				this.finType.setObject(new FinanceType(financeType.getFinType()));
				this.finType.setDescription(financeType.getFinTypeDesc());
				this.finCcy = financeType.getFinCcy();
			}
		}
	}

	
	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");

		this.promotionCode.setConstraint("");
		this.finType.setConstraint("");

		logger.debug("Leaving");
	}
	
	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aPromotion
	 */
	public void doWriteComponentsToBean(Promotion aPromotion) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Code
		try {
			String promotionCodeValue = this.promotionCode.getValue();
			boolean promotionExist = this.promotionService.getPromtionExist(promotionCodeValue, "_View");
			
			if (promotionExist) {
				throw new WrongValueException(this.promotionCode, Labels.getLabel("label_SelectPromotionDialog_promotionExist.value"));
			}
			
			aPromotion.setPromotionCode(promotionCodeValue);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		// Finance Type
		try {
			FinanceType FinanceTypeObj = (FinanceType) this.finType.getObject();
			aPromotion.setFinType(FinanceTypeObj.getId());
			aPromotion.setFinTypeDesc(this.finType.getDescription());
			aPromotion.setFinCcy(this.finCcy);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		doRemoveValidation();

		if (wve.isEmpty()) {
			List<FinTypeFees> finTypeFeesList = getFinTypeFeesService().getApprovedFinTypeFeesById(
					aPromotion.getFinType(), FinanceConstants.MODULEID_FINTYPE);
			List<FinTypeInsurances> finTypeInsurancesList = getFinTypeInsurancesService()
					.getApprovedFinTypeInsuranceListByID(aPromotion.getFinType(), FinanceConstants.MODULEID_FINTYPE);
			List<FinTypeAccounting> finTypeAccountingList = getFinTypeAccountingService()
					.getApprovedFinTypeAccountingListByID(aPromotion.getFinType(), FinanceConstants.MODULEID_FINTYPE);
			
			//Fees
			for (FinTypeFees finTypeFee : finTypeFeesList) {
				finTypeFee.setVersion(1);
				finTypeFee.setRecordType(PennantConstants.RCD_ADD);
				finTypeFee.setRecordStatus("");
				finTypeFee.setTaskId("");
				finTypeFee.setNextTaskId("");
				finTypeFee.setRoleCode("");
				finTypeFee.setNextRoleCode("");
				finTypeFee.setModuleId(FinanceConstants.MODULEID_PROMOTION);
				finTypeFee.setFinType(aPromotion.getPromotionCode());
				finTypeFee.setNewRecord(true);
			}
			
			//Insurances
			for (FinTypeInsurances finTypeInsurances : finTypeInsurancesList) {
				finTypeInsurances.setVersion(1);
				finTypeInsurances.setRecordType(PennantConstants.RCD_ADD);
				finTypeInsurances.setRecordStatus("");
				finTypeInsurances.setTaskId("");
				finTypeInsurances.setNextTaskId("");
				finTypeInsurances.setRoleCode("");
				finTypeInsurances.setNextRoleCode("");
				finTypeInsurances.setModuleId(FinanceConstants.MODULEID_PROMOTION);
				finTypeInsurances.setFinType(aPromotion.getPromotionCode());
				finTypeInsurances.setNewRecord(true);
			}
			
			//Accounting
			for (FinTypeAccounting finTypeAccounting : finTypeAccountingList) {
				finTypeAccounting.setVersion(1);
				finTypeAccounting.setRecordType(PennantConstants.RCD_ADD);
				finTypeAccounting.setRecordStatus("");
				finTypeAccounting.setTaskId("");
				finTypeAccounting.setNextTaskId("");
				finTypeAccounting.setRoleCode("");
				finTypeAccounting.setNextRoleCode("");
				finTypeAccounting.setModuleId(FinanceConstants.MODULEID_PROMOTION);
				finTypeAccounting.setFinType(aPromotion.getPromotionCode());
				finTypeAccounting.setNewRecord(true);
			}
			
			aPromotion.setFinTypeFeesList(finTypeFeesList);
			aPromotion.setFinTypeInsurancesList(finTypeInsurancesList);
			aPromotion.setFinTypeAccountingList(finTypeAccountingList);
		} else {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public Promotion getPromotion() {
		return promotion;
	}

	public void setPromotion(Promotion promotion) {
		this.promotion = promotion;
	}

	public PromotionService getPromotionService() {
		return promotionService;
	}

	public void setPromotionService(PromotionService promotionService) {
		this.promotionService = promotionService;
	}

	public PromotionListCtrl getPromotionListCtrl() {
		return promotionListCtrl;
	}

	public void setPromotionListCtrl(PromotionListCtrl promotionListCtrl) {
		this.promotionListCtrl = promotionListCtrl;
	}

	public FinTypeFeesService getFinTypeFeesService() {
		return finTypeFeesService;
	}

	public void setFinTypeFeesService(FinTypeFeesService finTypeFeesService) {
		this.finTypeFeesService = finTypeFeesService;
	}

	public FinTypeInsurancesService getFinTypeInsurancesService() {
		return finTypeInsurancesService;
	}

	public void setFinTypeInsurancesService(FinTypeInsurancesService finTypeInsurancesService) {
		this.finTypeInsurancesService = finTypeInsurancesService;
	}

	public FinTypeAccountingService getFinTypeAccountingService() {
		return finTypeAccountingService;
	}

	public void setFinTypeAccountingService(FinTypeAccountingService finTypeAccountingService) {
		this.finTypeAccountingService = finTypeAccountingService;
	}
}

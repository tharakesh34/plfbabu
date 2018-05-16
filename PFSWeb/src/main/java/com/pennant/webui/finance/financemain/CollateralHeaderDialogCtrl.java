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
 * FileName    		:  CollateralHeaderDialogCtrl.java                                      * 	  
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
 * 10-05-2019		Srinivasa Varma			 0.2		  Development Item 82              	* 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinAssetTypes;
import com.pennant.backend.service.configuration.AssetTypeService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.collateral.collateralsetup.CollateralBasicDetailsCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.webui.verification.LVerificationCtrl;
import com.pennanttech.webui.verification.RCUVerificationDialogCtrl;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/CollateralHeaderDialog.zul file.
 */
public class CollateralHeaderDialogCtrl extends GFCBaseCtrl<CollateralAssignment> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(CollateralHeaderDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 						window_CollateralAssignmentDialog; 			
	protected Borderlayout 					borderlayoutCollateralAssignment; 			

	// Collateral Total Count Details
	protected Label 						collateralCount; 								
	protected Label 						availableCollateral; 									
	protected Button 						btnNew_CollateralAssignment; 				
	protected Div 							collateralDiv; 	
	protected Div 							assetTypeDiv; 	
	protected Grid 							collateralTotalsGrid; 	
	protected Listbox 						listBoxCollateralAssignments; 	
	protected Listbox						listBoxAssetTypeHeader;
	protected Button						btnNew_AssetType;

	private Component 						parent = null;

	private FinanceMainBaseCtrl 			financeMainDialogCtrl;
	private RCUVerificationDialogCtrl	 	rcuVerificationDialogCtrl;
	private LVerificationCtrl 				lVerificationCtrl;
	private String 							roleCode = "";
	private String 							finType = "";
	private BigDecimal 						totalValue = BigDecimal.ZERO;
	private BigDecimal 						utilizedAmount = BigDecimal.ZERO;
	private FinBasicDetailsCtrl 			finBasicDetailsCtrl;
	protected Groupbox 						finBasicdetails;
	private List<CollateralAssignment> 		collateralAssignments = null;
	private List<ExtendedFieldRender> 		extendedFieldRenderList = new ArrayList<ExtendedFieldRender>();
	private AssetTypeService 				assetTypeService;
	private List<FinAssetTypes>				finAssetTypes = new ArrayList<FinAssetTypes>();
	private CollateralBasicDetailsCtrl  	collateralBasicDetailsCtrl;
	private boolean 						isNotFinanceProcess = false;
	private boolean 						assetsReq = false;
	private boolean 						collateralReq = true;
	private String							moduleName;
	private long							customerId;
	private List<String> assignCollateralRef;
	//### 10-05-2018 Start Development Item 82
	private Map<String, Object> rules = new HashMap<>();
	
	public Map<String, Object> getRules() {
		return rules;
	}

	public void setRules(Map<String, Object> rules) {
		this.rules = rules;
	}
	//### 10-05-2018 End Development Item 82
	/**
	 * default constructor.<br>
	 */
	public CollateralHeaderDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CollateralAssignmentDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_CollateralAssignmentDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CollateralAssignmentDialog);

		try {

			if (event.getTarget().getParent() != null) {
				parent = event.getTarget().getParent();
			}

			// READ OVERHANDED parameters !
			if (arguments.containsKey("collateralAssignmentList")) {
				setCollateralAssignments((List<CollateralAssignment>)arguments.get("collateralAssignmentList"));
			}
			
			if (arguments.containsKey("assetTypeList")) {
				setExtendedFieldRenderList((List<ExtendedFieldRender>)arguments.get("assetTypeList"));
			}
			
			if (arguments.containsKey("finassetTypeList")) {
				setFinAssetTypes((List<FinAssetTypes>)arguments.get("finassetTypeList"));
			}

			
			if (arguments.containsKey("financeMainDialogCtrl")) {
				this.financeMainDialogCtrl = (FinanceMainBaseCtrl) arguments.get("financeMainDialogCtrl");
				financeMainDialogCtrl.setCollateralHeaderDialogCtrl(this);
			}

			if (arguments.containsKey("roleCode")) {
				this.roleCode = (String) arguments.get("roleCode");
			}
			
			if (arguments.containsKey("finType")) {
				this.finType = (String) arguments.get("finType");
			}
			
			if (arguments.containsKey("moduleName")) {
				this.moduleName = (String) arguments.get("moduleName");
			}
			
			if (arguments.containsKey("utilizedAmount")) {
				this.utilizedAmount = (BigDecimal) arguments.get("utilizedAmount");
			}
			
			if (arguments.containsKey("totalValue")) {
				this.totalValue = (BigDecimal) arguments.get("totalValue");
			}
			
			if (arguments.containsKey("isNotFinanceProcess")) {
				this.isNotFinanceProcess = (boolean) arguments.get("isNotFinanceProcess");
			}
			
			if (arguments.containsKey("assetsReq")) {
				this.assetsReq = (boolean) arguments.get("assetsReq");
			}
			
			if (arguments.containsKey("collateralReq")) {
				this.collateralReq = (boolean) arguments.get("collateralReq");
			}
			
			if (arguments.containsKey("customerId")) {
				this.customerId = (long) arguments.get("customerId");
			}
			
			if (arguments.containsKey("assignCollateralRef")) {
				this.assignCollateralRef = (List<String>) arguments.get("assignCollateralRef");
			}

			doCheckRights();
			doShowDialog();

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CollateralAssignmentDialog.onClose();
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws Exception
	 */
	public void doShowDialog() throws Exception {
		logger.debug("Entering");

		try {
			// append finance basic details
			appendFinBasicDetails();

			// fill the components with the data
			doFillCollateralDetails(getCollateralAssignments());
			doFillAssetDetails(getExtendedFieldRenderList());

			// Setting Controller to the Parent Controller
			try {
				getFinanceMainDialogCtrl().getClass().getMethod("setCollateralHeaderDialogCtrl", this.getClass()).invoke(getFinanceMainDialogCtrl(), this);
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}

			getBorderLayoutHeight();
			if (parent != null) {

				int borderHeight = ((this.borderLayoutHeight - 240)/2);
				if(!assetsReq || !collateralReq){
					borderHeight = (this.borderLayoutHeight - 230);
					if(!assetsReq){
						this.assetTypeDiv.setVisible(false);
						this.listBoxAssetTypeHeader.setVisible(false);
					}
					if(!collateralReq){
						borderHeight = (this.borderLayoutHeight - 200);
						this.collateralDiv.setVisible(false);
						this.collateralTotalsGrid.setVisible(false);
						this.listBoxCollateralAssignments.setVisible(false);
					}
					this.window_CollateralAssignmentDialog.setHeight(this.borderLayoutHeight - 80 + "px");	
				}else{
					this.window_CollateralAssignmentDialog.setHeight(this.borderLayoutHeight - 75 + "px");
				}
				this.listBoxAssetTypeHeader.setHeight(borderHeight +"px");
				this.listBoxCollateralAssignments.setHeight(borderHeight + "px");
				parent.appendChild(this.window_CollateralAssignmentDialog);
			} else {
				this.listBoxCollateralAssignments.setHeight(  150 + "px");
				this.listBoxAssetTypeHeader.setHeight(150 + "px");
				this.window_CollateralAssignmentDialog.setHeight(this.borderLayoutHeight - 80 + "px");
			}

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_CollateralAssignmentDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Checking Rights for the Collateral Dialog
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		
		getUserWorkspace().allocateAuthorities("CollateralAssignmentDialog", this.roleCode);
		this.btnNew_CollateralAssignment.setVisible(getUserWorkspace().isAllowed("button_CollateralAssignmentDialog_btnNew"));
		this.btnNew_AssetType.setVisible(getUserWorkspace().isAllowed("button_CollateralAssignmentDialog_btnNewAsset"));
		
		logger.debug("Leaving");
	}

	/**
	 * New Button & Double Click Events for Assigning Collateral List
	 * @param event
	 * @throws InterruptedException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void onClick$btnNew_CollateralAssignment(Event event) throws InterruptedException, SecurityException,
	IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering" + event.toString());
		
		// If Record is processing with Prospect Customer and not yet created in Application
		// Same Customer should not eligible to assign Collateral Details
		if(customerId <= 0){
			MessageUtil.showError(Labels.getLabel("label_FinCollateralHeaderDialog_NotFound_Customer"));
			return;
		}

		CollateralAssignment assignment = new CollateralAssignment();
		assignment.setNewRecord(true);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("collateralHeaderDialogCtrl", this);
		map.put("roleCode", this.roleCode);
		map.put("newRecord", true);
		map.put("collateralAssignment", assignment);
		map.put("assignCollateralRef", assignCollateralRef);
		map.put("finType", finType);
		map.put("customerId", customerId);

		Executions.createComponents("/WEB-INF/pages/Collateral/CollateralAssignment/CollateralAssignmentDialog.zul",window_CollateralAssignmentDialog, map);

		logger.debug("Leaving" + event.toString());
	}
	
	
	/**
	 * New Button & Double Click Events for Assigning Collateral List
	 * @param event
	 * @throws InterruptedException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void onClick$btnNew_AssetType(Event event) throws InterruptedException, SecurityException,
	IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering" + event.toString());

		ExtendedFieldRender extendedFieldRender = new ExtendedFieldRender();
		extendedFieldRender.setNewRecord(true);		
		// Finding Maximum Sequence Number
		int seqNo = 0;
		if(getExtendedFieldRenderList() != null && !getExtendedFieldRenderList().isEmpty()){
			for (int i = 0; i < getExtendedFieldRenderList().size(); i++) {
				ExtendedFieldRender render = getExtendedFieldRenderList().get(i);
				if(seqNo <= render.getSeqNo()){
					seqNo = render.getSeqNo();
				}
			}
		}

		extendedFieldRender.setSeqNo(seqNo+1);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("collateralHeaderDialogCtrl", this);
		map.put("extendedFieldRender",extendedFieldRender);
		map.put("ccyFormat",getFormat());
		map.put("newRecord",true);
		map.put("roleCode", this.roleCode);
		map.put("isReadOnly",!getUserWorkspace().isAllowed("button_CollateralAssignmentDialog_btnNew"));
		
		Executions.createComponents("/WEB-INF/pages/AssetType/AssetTypeAssignmentDialog.zul",window_CollateralAssignmentDialog,map);

		logger.debug("Leaving" + event.toString());
	}
	
	private int getFormat() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		int ccyFormat = 0;
		if(getFinanceMainDialogCtrl() != null){
			ccyFormat  = (int) getFinanceMainDialogCtrl().getClass().getMethod("getCcyFormat").invoke(getFinanceMainDialogCtrl());
		}
		return ccyFormat;
	}


	/**
	 * Method for Rendering saved dynamic objects into list
	 * @param extendedFieldRenderList
	 * @param assetType 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public void doFillAssetDetails(List<ExtendedFieldRender> extendedFieldRenderList) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		logger.debug("Entering");

		setExtendedFieldRenderList(extendedFieldRenderList);
		this.listBoxAssetTypeHeader.getItems().clear();
		
		if (extendedFieldRenderList != null && !extendedFieldRenderList.isEmpty()) {
			
			// List Rendering
			for (int i = 0; i < extendedFieldRenderList.size(); i++) {
				ExtendedFieldRender fieldValueDetail = extendedFieldRenderList.get(i);
				Map<String, Object> detail = fieldValueDetail.getMapValues();
				
				Listitem item = new Listitem();
				
				// Sequence No
				Listcell lc = new Listcell();
				lc.setLabel(String.valueOf(fieldValueDetail.getSeqNo()));
				item.appendChild(lc);
				
				// Asset Type
				lc = new Listcell();
				lc.setLabel(fieldValueDetail.getTypeCode() +" - "+fieldValueDetail.getTypeCodeDesc());
				item.appendChild(lc);
				
				// No of Units
				lc = new Listcell();

				Object noOfUnits = detail.get("NOOFUNITS");
				if (noOfUnits == null) {
					noOfUnits = detail.get("NoOfUnits");
				}
				int noOfunits = Integer.valueOf(noOfUnits.toString());
				lc.setStyle("text-align:right;");
				lc.setLabel(String.valueOf(noOfunits));
				item.appendChild(lc);
				
				// Unit Price
				lc = new Listcell();
				
				Object unitPriceObj = detail.get("UNITPRICE");
				if (unitPriceObj == null) {
					unitPriceObj = detail.get("UnitPrice");
				}
				
				BigDecimal unitPrice = (BigDecimal)unitPriceObj;
				lc.setLabel(PennantApplicationUtil.amountFormate(unitPrice, getFormat()));
				lc.setStyle("text-align:right;");
				item.appendChild(lc);
				
				// Asset Value
				lc = new Listcell();
				lc.setLabel(PennantApplicationUtil.amountFormate(unitPrice.multiply(new BigDecimal(noOfunits)), getFormat()));
				lc.setStyle("text-align:right;");
				item.appendChild(lc);
				
				// Status
				lc = new Listcell();
				lc.setLabel(fieldValueDetail.getRecordStatus());
				item.appendChild(lc);
				
				// Operation
				lc = new Listcell();
				lc.setLabel(PennantJavaUtil.getLabel(fieldValueDetail.getRecordType()));
				item.appendChild(lc);
				
				item.setAttribute("data", fieldValueDetail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onAssetTypeItemDoubleClicked");
				listBoxAssetTypeHeader.appendChild(item);

			}
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Double Click of Extended Field Details edition
	 * @param event
	 * @throws Exception
	 */
	public void onAssetTypeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final Listitem item = this.listBoxAssetTypeHeader.getSelectedItem();
		if (item != null) {
			
			final ExtendedFieldRender fieldRender = (ExtendedFieldRender) item.getAttribute("data");
			if (StringUtils.equalsIgnoreCase(fieldRender.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("collateralHeaderDialogCtrl", this);
				map.put("extendedFieldRender",fieldRender);
				map.put("ccyFormat",getFormat());
				map.put("isReadOnly", !getUserWorkspace().isAllowed("button_CollateralAssignmentDialog_btnNew"));

				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/AssetType/AssetTypeAssignmentDialog.zul",window_CollateralAssignmentDialog,map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	
	/**
	 * Method for Filling List box with the list rendering for Assignments
	 * @param CollateralAssignments
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public void doFillCollateralDetails(List<CollateralAssignment> CollateralAssignments) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		logger.debug("Entering");
		
		//### 10-05-2018 Start Development Item 82
		BigDecimal totalBankValuation= new BigDecimal(0);
		BigDecimal balanceAssignedValue= new BigDecimal(0);
		BigDecimal totalAssignedValue= new BigDecimal(0);
				
		//### 10-05-2018 End Development Item 82
		
		int totCollateralCount = 0;
		BigDecimal totAssignedColValue = BigDecimal.ZERO;
		
		this.listBoxCollateralAssignments.getItems().clear();
		setCollateralAssignments(CollateralAssignments);
		
		BigDecimal loanAssignedValue = BigDecimal.ZERO;
		if (CollateralAssignments != null && !CollateralAssignments.isEmpty()) {
			
			for (CollateralAssignment collateralAssignment : CollateralAssignments) {
				loanAssignedValue = loanAssignedValue.add(collateralAssignment.getBankValuation().multiply(
						collateralAssignment.getAssignPerc()).divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN));
			}
			
			for (CollateralAssignment collateralAssignment : CollateralAssignments) {

				int ccyFormat = CurrencyUtil.getFormat(collateralAssignment.getCollateralCcy());
				Listitem listitem = new Listitem();
				Listcell listcell;
				listcell = new Listcell(collateralAssignment.getCollateralRef());
				listitem.appendChild(listcell);
				listcell = new Listcell(collateralAssignment.getCollateralCcy());
				listitem.appendChild(listcell);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(collateralAssignment.getBankValuation(), ccyFormat));
				listcell.setStyle("text-align:right;");
				listitem.appendChild(listcell);
				
				totalBankValuation = totalBankValuation.add(PennantAppUtil.formateAmount(collateralAssignment.getBankValuation(), ccyFormat));
				
				BigDecimal curAssignValue =(collateralAssignment.getBankValuation().multiply(collateralAssignment.getAssignPerc())).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_DOWN);
				
				totalAssignedValue = totalAssignedValue.add(PennantAppUtil.formateAmount(curAssignValue, ccyFormat));
				
				listcell = new Listcell(PennantAppUtil.amountFormate(curAssignValue, ccyFormat));
				listcell.setStyle("text-align:right;");
				listitem.appendChild(listcell);
				
				// Available Assignment value 
				BigDecimal  totAssignedValue = collateralAssignment.getBankValuation().multiply(
						collateralAssignment.getTotAssignedPerc()).divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
				BigDecimal availAssignValue =  collateralAssignment.getBankValuation().subtract(totAssignedValue).subtract(curAssignValue);
				if(availAssignValue.compareTo(BigDecimal.ZERO) < 0){
					availAssignValue = BigDecimal.ZERO;
				}
				
				balanceAssignedValue = balanceAssignedValue.add(PennantAppUtil.formateAmount(availAssignValue, ccyFormat));
				
				listcell = new Listcell(PennantAppUtil.amountFormate(availAssignValue, ccyFormat));
				listcell.setStyle("text-align:right;");
				listitem.appendChild(listcell);
				
				
				BigDecimal utlzedAmt = BigDecimal.ZERO;
				if(loanAssignedValue.compareTo(BigDecimal.ZERO) > 0){
					utlzedAmt = (curAssignValue.multiply(utilizedAmount)).divide(
							loanAssignedValue, 0, RoundingMode.HALF_DOWN);
				}
				listcell = new Listcell(PennantAppUtil.amountFormate(utlzedAmt, ccyFormat));
				listcell.setStyle("text-align:right;");
				listitem.appendChild(listcell);
				
				BigDecimal availAssignPerc = BigDecimal.ZERO;
				if(collateralAssignment.getBankValuation().compareTo(BigDecimal.ZERO) > 0){
					availAssignPerc = availAssignValue.multiply(new BigDecimal(100)).divide(collateralAssignment.getBankValuation(), 2, RoundingMode.HALF_DOWN);
				}
				
				listcell = new Listcell(PennantApplicationUtil.formatRate(availAssignPerc.doubleValue(), 2));
				listcell.setStyle("text-align:right;");
				listitem.appendChild(listcell);

				listcell = new Listcell(collateralAssignment.getRecordStatus());
				listitem.appendChild(listcell);
				listcell = new Listcell(PennantJavaUtil.getLabel(collateralAssignment.getRecordType()));
				listitem.appendChild(listcell);
				if (!(StringUtils.equals(collateralAssignment.getRecordType(), PennantConstants.RECORD_TYPE_DEL) || 
						StringUtils.equals(collateralAssignment.getRecordType(), PennantConstants.RECORD_TYPE_CAN))) {
					totCollateralCount = totCollateralCount + 1;
					totAssignedColValue = totAssignedColValue.add(curAssignValue);
				}
				listitem.setAttribute("data", collateralAssignment);
				ComponentsCtrl.applyForward(listitem, "onDoubleClick=onCollateralAssignItemDoubleClicked");
				this.listBoxCollateralAssignments.appendChild(listitem);
			}
		}

		this.collateralCount.setValue(PennantApplicationUtil.amountFormate(loanAssignedValue, getFormat()));
		if(utilizedAmount.compareTo(totAssignedColValue) > 0 ){
			this.availableCollateral.setValue("Shortfall");
			this.availableCollateral.setStyle("color:red;font-weight:bold;");
		}else if(totalValue.compareTo(BigDecimal.ZERO) > 0 && totalValue.compareTo(totAssignedColValue) > 0){
			this.availableCollateral.setValue("Insufficient for Future Drawdowns");
			this.availableCollateral.setStyle("color:orange;font-weight:bold;");
		}else{
			this.availableCollateral.setValue("Available");
			this.availableCollateral.setStyle("color:Green;font-weight:bold;");
		}

		//### 10-05-2018 Start Development Item 82
		
		rules.put("Collaterals_Total_Assigned", totalAssignedValue);
		rules.put("Collaterals_Total_UN_Assigned", balanceAssignedValue);
		rules.put("Collateral_Bank_Valuation", totalBankValuation);
		
		//### 10-05-2018 End  Development Item 82
		
		if (rcuVerificationDialogCtrl != null) {
			rcuVerificationDialogCtrl.addCollateralDocuments(collateralAssignments);
		}
		
		if (lVerificationCtrl != null) {
			lVerificationCtrl.addCollateralDocuments(collateralAssignments);
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Checking Shortfall of Collateral Value with Finance Amount/Commitment Amount
	 * @param utilizedAmount
	 */
	public void updateShortfall(BigDecimal utilizedAmount) {
		this.utilizedAmount = utilizedAmount;
		BigDecimal totAssignedvalue = BigDecimal.ZERO;
		if (utilizedAmount != null && getCollateralAssignments() != null && !getCollateralAssignments().isEmpty()) {
			for (CollateralAssignment colAssignment : getCollateralAssignments()) {
				
				BigDecimal curAssignValue =(colAssignment.getBankValuation().multiply(
						colAssignment.getAssignPerc())).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_DOWN);
				totAssignedvalue = totAssignedvalue.add(curAssignValue);
			}
		}
		if(utilizedAmount.compareTo(totAssignedvalue) > 0 ){
			this.availableCollateral.setValue("Shortfall");
			this.availableCollateral.setStyle("color:red;font-weight:bold");
		}else if(totalValue.compareTo(BigDecimal.ZERO) > 0 && totalValue.compareTo(totAssignedvalue) > 0){
			this.availableCollateral.setValue("Insufficient for Future Drawdowns");
			this.availableCollateral.setStyle("color:orange;font-weight:bold;");
		}else{
			this.availableCollateral.setValue("Available");
			this.availableCollateral.setStyle("color:Green;font-weight:bold");
		}
	}

	/**
	 * Method for Editing the Collateral Assignment Details on Double Click
	 * @param event
	 * @throws Exception
	 */
	public void onCollateralAssignItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCollateralAssignments.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			CollateralAssignment assignment = (CollateralAssignment) item.getAttribute("data");
			if (StringUtils.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN,StringUtils.trimToEmpty(assignment.getRecordType()))
					|| StringUtils.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL,StringUtils.trimToEmpty(assignment.getRecordType()))) {

				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));

			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();

				map.put("collateralHeaderDialogCtrl", this);
				map.put("roleCode", this.roleCode);
				map.put("collateralAssignment", assignment);
				map.put("finType", finType);
				map.put("customerId", customerId);

				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents(
							"/WEB-INF/pages/Collateral/CollateralAssignment/CollateralAssignmentDialog.zul", null, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails() {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			map.put("moduleName", moduleName);
			if(isNotFinanceProcess){
				Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CollateralBasicDetails.zul",this.finBasicdetails, map);
			}else {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul",this.finBasicdetails, map);
			}
		} catch (Exception e) {
			logger.debug(e);
		}

	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		if(isNotFinanceProcess){
			getCollateralBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
		}else{
			getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
		}
	}
		
	/**
	 * Method for Re-Rendering Utilized amount details for Collateral Assignment Calculations
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public void updateUtilizedAmount(BigDecimal utilizedAmt) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		logger.debug("Entering");
		this.utilizedAmount = utilizedAmt;
		doFillCollateralDetails(getCollateralAssignments());
		logger.debug("Leaving");
	}
	
	/**
	 * Method for validating Total Collateral value against created Loan
	 */
	public boolean validCollateralValue(BigDecimal utilizedAmt) {
		
		BigDecimal totCollateralAssigned = BigDecimal.ZERO;
		for (int i = 0; i < getCollateralAssignments().size(); i++) {
			
			CollateralAssignment collateralAssignment = getCollateralAssignments().get(i);
			if (!(StringUtils.equals(collateralAssignment.getRecordType(), PennantConstants.RECORD_TYPE_DEL) || 
					StringUtils.equals(collateralAssignment.getRecordType(), PennantConstants.RECORD_TYPE_CAN))) {
				BigDecimal curAssignValue =(collateralAssignment.getBankValuation().multiply(
						collateralAssignment.getAssignPerc())).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_DOWN);
				totCollateralAssigned = totCollateralAssigned.add(curAssignValue);
			}
		}
		
		if(totCollateralAssigned.compareTo(utilizedAmt) < 0){
			return false;
		}
		return true;
	}


	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}
	public void setFinanceMainDialogCtrl(FinanceMainBaseCtrl financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}
	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public List<CollateralAssignment> getCollateralAssignments() {
		return collateralAssignments;
	}
	public void setCollateralAssignments(
			List<CollateralAssignment> collateralAssignments) {
		this.collateralAssignments = collateralAssignments;
	}

	public List<ExtendedFieldRender> getExtendedFieldRenderList() {
		return extendedFieldRenderList;
	}

	public void setExtendedFieldRenderList(List<ExtendedFieldRender> extendedFieldRenderList) {
		this.extendedFieldRenderList = extendedFieldRenderList;
	}

	public AssetTypeService getAssetTypeService() {
		return assetTypeService;
	}

	public void setAssetTypeService(AssetTypeService assetTypeService) {
		this.assetTypeService = assetTypeService;
	}

	public List<FinAssetTypes> getFinAssetTypes() {
		return finAssetTypes;
	}

	public void setFinAssetTypes(List<FinAssetTypes> finAssetTypes) {
		this.finAssetTypes = finAssetTypes;
	}

	public CollateralBasicDetailsCtrl getCollateralBasicDetailsCtrl() {
		return collateralBasicDetailsCtrl;
	}

	public void setCollateralBasicDetailsCtrl(CollateralBasicDetailsCtrl collateralBasicDetailsCtrl) {
		this.collateralBasicDetailsCtrl = collateralBasicDetailsCtrl;
	}

	public void setRcuVerificationDialogCtrl(RCUVerificationDialogCtrl rcuVerificationDialogCtrl) {
		this.rcuVerificationDialogCtrl = rcuVerificationDialogCtrl;
	}

	public void setlVerificationCtrl(LVerificationCtrl lVerificationCtrl) {
		this.lVerificationCtrl = lVerificationCtrl;
	}
	
}

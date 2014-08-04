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
 * FileName    		:  CommidityLoanDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.commidityloandetail;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.CommidityLoanDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.lmtmasters.CommidityLoanDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.ScreenCTL;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/CommidityLoanDetail/commidityLoanDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CommidityLoanDetailDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(CommidityLoanDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CommidityLoanDetailDialog; 
	
	protected Label 		label_LoanRefNumber;
	protected Hlayout 		hlayout_LoanRefNumber;
	protected Space 		space_LoanRefNumber; 
	protected Textbox 		loanRefNumber;
	
	protected Label 		label_ItemType;
	protected Hlayout 		hlayout_ItemType;
	protected Space 		space_ItemType; 
	protected Textbox 		itemType; 
	protected Textbox 		lovDescItemType; 
	
	protected Label 		label_Quantity;
	protected Hlayout 		hlayout_Quantity;
	protected Space 		space_Quantity; 
	protected Longbox 		quantity; 
	
	protected Label 		label_UnitBuyPrice;
	protected Hlayout 		hlayout_UnitBuyPrice;
	protected Space 		space_UnitBuyPrice; 
	protected Decimalbox	unitBuyPrice; 
	
	protected Label 		label_BuyAmount;
	protected Hlayout 		hlayout_BuyAmount;
	protected Space 		space_BuyAmount; 
	protected Decimalbox	buyAmount; 
	
	protected Label 		label_UnitSellPrice;
	protected Hlayout 		hlayout_UnitSellPrice;
	protected Space 		space_UnitSellPrice; 
	protected Decimalbox	unitSellPrice; 
	
	protected Label 		label_SellAmount;
	protected Hlayout 		hlayout_SellAmount;
	protected Space 		space_SellAmount; 
	protected Decimalbox	sellAmount; 
	
	protected Label 		recordStatus; 
	protected Label 		recordType;	 
	protected Radiogroup 	userAction;
	protected Groupbox 		gb_statusDetails;
	protected Groupbox 		groupboxWf;
	protected South 		south;
	private boolean 		enqModule=false;

	// not auto wired vars
	private CommidityLoanDetail commidityLoanDetail; // overhanded per param
	private transient CommidityLoanDetailListCtrl commidityLoanDetailListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String  		oldVar_LoanRefNumber;
	private transient String  		oldVar_ItemType;
	private transient long		  		oldVar_Quantity;
	private transient BigDecimal  		oldVar_UnitBuyPrice;
	private transient BigDecimal  		oldVar_BuyAmount;
	private transient BigDecimal  		oldVar_UnitSellPrice;
	private transient BigDecimal  		oldVar_SellAmount;
	
	private transient String oldVar_recordStatus;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CommidityLoanDetailDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 
	protected Button btnEdit; 
	protected Button btnDelete; 
	protected Button btnSave; 
	protected Button btnCancel; 
	protected Button btnClose; 
	protected Button btnHelp; 
	protected Button btnNotes; 

	// ServiceDAOs / Domain Classes
	private transient CommidityLoanDetailService commidityLoanDetailService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public CommidityLoanDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected CommidityLoanDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CommidityLoanDetailDialog(Event event) throws Exception {
		logger.debug("Entring" +event.toString());
		try {

			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);
			// READ OVERHANDED params !
			if (args.containsKey("enqModule")) {
				enqModule=(Boolean) args.get("enqModule");
			}else{
				enqModule=false;
			}

			// READ OVERHANDED params !
			if (args.containsKey("commidityLoanDetail")) {
				this.commidityLoanDetail = (CommidityLoanDetail) args.get("commidityLoanDetail");
				CommidityLoanDetail befImage =new CommidityLoanDetail();
				BeanUtils.copyProperties(this.commidityLoanDetail, befImage);
				this.commidityLoanDetail.setBefImage(befImage);

				setCommidityLoanDetail(this.commidityLoanDetail);
			} else {
				setCommidityLoanDetail(null);
			}
			doLoadWorkFlow(this.commidityLoanDetail.isWorkflow(),this.commidityLoanDetail.getWorkflowId(),
					this.commidityLoanDetail.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule){
				this.userAction	= setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "CommidityLoanDetailDialog");
			}else{
				getUserWorkspace().alocateAuthorities("CommidityLoanDetailDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the commidityLoanDetailListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete commidityLoanDetail here.
			if (args.containsKey("commidityLoanDetailListCtrl")) {
				setCommidityLoanDetailListCtrl((CommidityLoanDetailListCtrl) args.get("commidityLoanDetailListCtrl"));
			} else {
				setCommidityLoanDetailListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getCommidityLoanDetail());
		} catch (Exception e) {
			createException(window_CommidityLoanDetailDialog, e);
			logger.error(e);
		}

		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" +event.toString());
		doStoreInitValues();
		displayComponents(ScreenCTL.SCRN_GNEDT);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		doDelete();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		doSave();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" +event.toString());
		doResetInitValues();
		displayComponents(ScreenCTL.SCRN_GNINT);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		PTMessageUtils.showHelpWindow(event, window_CommidityLoanDetailDialog);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_CommidityLoanDetailDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		doClose();
		logger.debug("Leaving" +event.toString());
	}
	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		try {

			ScreenCTL.displayNotes(getNotes("CommidityLoanDetail",getCommidityLoanDetail().getLoanRefNumber(),
					getCommidityLoanDetail().getVersion()),this);

		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" +event.toString());

	}


	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCommidityLoanDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(CommidityLoanDetail aCommidityLoanDetail) throws InterruptedException {
		logger.debug("Entering") ;

		// if aCommidityLoanDetail == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aCommidityLoanDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aCommidityLoanDetail = getCommidityLoanDetailService().getNewCommidityLoanDetail();

			setCommidityLoanDetail(aCommidityLoanDetail);
		} else {
			setCommidityLoanDetail(aCommidityLoanDetail);
		}

		try {

			// fill the components with the data
			doWriteBeanToComponents(aCommidityLoanDetail);
			// set ReadOnly mode accordingly if the object is new or not.

			displayComponents(ScreenCTL.getMode(enqModule,isWorkFlowEnabled(),aCommidityLoanDetail.isNewRecord()));

			doStoreInitValues();

			// stores the initial data for comparing if they are changed
			// during user action.
			setDialog(this.window_CommidityLoanDetailDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving") ;
	}

	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit

	private void displayComponents(int mode){
		logger.debug("Entering");

		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, isWorkFlowEnabled(),isFirstTask(), this.userAction,this.loanRefNumber,this.itemType));

		if (getCommidityLoanDetail().isNewRecord()){
			//setComponentAccessType("CommidityLoanDetailDialog_LoanRefNumber", false, this.loanRefNumber, this.space_LoanRefNumber, this.label_LoanRefNumber, this.hlayout_LoanRefNumber,null);
			setComponentAccessType("CommidityLoanDetailDialog_ItemType", false, this.itemType, this.space_ItemType, this.label_ItemType, this.hlayout_ItemType,null);
		}

		logger.debug("Leaving");
	} 

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");

		boolean tempReadOnly= readOnly;

		if(readOnly || (!readOnly && (PennantConstants.RECORD_TYPE_DEL.equals(commidityLoanDetail.getRecordType())))) {
			tempReadOnly=true;
		}

		setComponentAccessType("CommidityLoanDetailDialog_LoanRefNumber", true, this.loanRefNumber, this.space_LoanRefNumber, this.label_LoanRefNumber, this.hlayout_LoanRefNumber,null);		
		setComponentAccessType("CommidityLoanDetailDialog_ItemType", true, this.itemType, this.space_ItemType, this.label_ItemType, this.hlayout_ItemType,null);
		setComponentAccessType("CommidityLoanDetailDialog_Quantity", tempReadOnly, this.quantity, this.space_Quantity, this.label_Quantity, this.hlayout_Quantity,null);
		setComponentAccessType("CommidityLoanDetailDialog_UnitPrice", tempReadOnly, this.unitBuyPrice, this.space_UnitBuyPrice, this.label_UnitBuyPrice, this.hlayout_UnitBuyPrice,null);
		setComponentAccessType("CommidityLoanDetailDialog_BuyAmount", tempReadOnly, this.buyAmount, this.space_BuyAmount, this.label_BuyAmount, this.hlayout_BuyAmount,null);
		setComponentAccessType("CommidityLoanDetailDialog_UnitSellPrice", tempReadOnly, this.unitSellPrice, this.space_UnitSellPrice, this.label_UnitSellPrice, this.hlayout_UnitSellPrice,null);
		setComponentAccessType("CommidityLoanDetailDialog_SellAmount", tempReadOnly, this.sellAmount, this.space_SellAmount, this.label_SellAmount, this.hlayout_SellAmount,null);
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering") ;
		
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);
		
		getUserWorkspace().alocateAuthorities("CommidityLoanDetailDialog");
		if(!enqModule){
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CommidityLoanDetailDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CommidityLoanDetailDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CommidityLoanDetailDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CommidityLoanDetailDialog_btnSave"));	
		}

		logger.debug("Leaving") ;
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		
		//Empty sent any required attributes
		this.loanRefNumber.setMaxlength(20);
		this.itemType.setMaxlength(20);
		this.unitBuyPrice.setMaxlength(18);
		this.unitBuyPrice.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.unitBuyPrice.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.unitBuyPrice.setScale(PennantConstants.defaultCCYDecPos);
		this.unitSellPrice.setMaxlength(18);
		this.unitSellPrice.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.unitSellPrice.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.unitSellPrice.setScale(PennantConstants.defaultCCYDecPos);
		
		this.quantity.setMaxlength(10);
		this.buyAmount.setMaxlength(18);
		this.sellAmount.setMaxlength(18);

		setStatusDetails(gb_statusDetails,groupboxWf,south,enqModule);
		logger.debug("Leaving") ;
	}


	/**
	 * Stores the initialinitial values to member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_LoanRefNumber = this.loanRefNumber.getValue();
		this.oldVar_ItemType = this.itemType.getValue();
		this.oldVar_Quantity = this.quantity.longValue();	
		this.oldVar_UnitBuyPrice = this.unitBuyPrice.getValue();
		this.oldVar_BuyAmount = this.buyAmount.getValue();
		this.oldVar_UnitSellPrice = this.unitSellPrice.getValue();
		this.oldVar_SellAmount = this.sellAmount.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.loanRefNumber.setValue(this.oldVar_LoanRefNumber);
		this.itemType.setValue(this.oldVar_ItemType);
		this.quantity.setValue(this.oldVar_Quantity);
		this.unitBuyPrice.setValue(this.oldVar_UnitBuyPrice);
		this.buyAmount.setValue(this.oldVar_BuyAmount);
		this.unitSellPrice.setValue(this.oldVar_UnitSellPrice);
		this.sellAmount.setValue(this.oldVar_SellAmount);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if(isWorkFlowEnabled() & !enqModule){	
			this.userAction.setSelectedIndex(0);	
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCommidityLoanDetail
	 *            CommidityLoanDetail
	 */
	public void doWriteBeanToComponents(CommidityLoanDetail aCommidityLoanDetail) {
		logger.debug("Entering") ;
		this.loanRefNumber.setValue(aCommidityLoanDetail.getLoanRefNumber());
		this.itemType.setValue(aCommidityLoanDetail.getItemType());
		this.quantity.setValue(aCommidityLoanDetail.getQuantity());
		this.unitBuyPrice.setValue(PennantApplicationUtil.formateAmount(aCommidityLoanDetail.getUnitBuyPrice(),PennantConstants.defaultCCYDecPos));
		this.buyAmount.setValue(PennantApplicationUtil.formateAmount(aCommidityLoanDetail.getBuyAmount(),PennantConstants.defaultCCYDecPos));
		this.unitSellPrice.setValue(PennantApplicationUtil.formateAmount(aCommidityLoanDetail.getUnitSellPrice(),PennantConstants.defaultCCYDecPos));
		this.sellAmount.setValue(PennantApplicationUtil.formateAmount(aCommidityLoanDetail.getSellAmount(),PennantConstants.defaultCCYDecPos));
		this.recordStatus.setValue(aCommidityLoanDetail.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aCommidityLoanDetail.getRecordType()));
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCommidityLoanDetail
	 */
	public void doWriteComponentsToBean(CommidityLoanDetail aCommidityLoanDetail) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Loan Ref Number
		try {
			aCommidityLoanDetail.setLoanRefNumber(this.loanRefNumber.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Item Type
		try {
			aCommidityLoanDetail.setItemType(this.itemType.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Unit Price
		try {
			if(this.unitBuyPrice.getValue()!=null){
				aCommidityLoanDetail.setUnitBuyPrice(PennantApplicationUtil.unFormateAmount(this.unitBuyPrice.getValue(),PennantConstants.defaultCCYDecPos));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Buy Amount
		try {
			if(this.buyAmount.getValue()!=null){
				aCommidityLoanDetail.setBuyAmount(PennantApplicationUtil.unFormateAmount(this.buyAmount.getValue(),PennantConstants.defaultCCYDecPos));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Unit Sell Price
		try {
			if(this.unitSellPrice.getValue()!=null){
				aCommidityLoanDetail.setUnitSellPrice(PennantApplicationUtil.unFormateAmount(this.unitSellPrice.getValue(),PennantConstants.defaultCCYDecPos));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Sell Amount
		try {
			if(this.sellAmount.getValue()!=null){
				aCommidityLoanDetail.setSellAmount(PennantApplicationUtil.unFormateAmount(this.sellAmount.getValue(),PennantConstants.defaultCCYDecPos));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Quantity
		try {
			aCommidityLoanDetail.setQuantity(this.quantity.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		aCommidityLoanDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}


	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		logger.debug("Entering");
		//To clear the Error Messages
		doClearMessage();

		if (!StringUtils.trimToEmpty(this.oldVar_LoanRefNumber).equals(StringUtils.trimToEmpty(this.loanRefNumber.getValue()))) {
			return true;
		}
		if (!StringUtils.trimToEmpty(this.oldVar_ItemType).equals(StringUtils.trimToEmpty(this.itemType.getValue()))) {
			return true;
		}
		if (this.oldVar_UnitBuyPrice != this.unitBuyPrice.getValue()) {
			return true;
		}
		if (this.oldVar_Quantity != this.quantity.longValue()) {
			return  true;
		}
		if (this.oldVar_BuyAmount != this.buyAmount.getValue()) {
			return true;
		}
		if (this.oldVar_UnitSellPrice != this.unitSellPrice.getValue()) {
			return true;
		}
		if (this.oldVar_SellAmount != this.sellAmount.getValue()) {
			return true;
		}
		logger.debug("Leaving"); 
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		//Loan Ref Number
		if (!this.loanRefNumber.isReadonly()){
			this.loanRefNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_CommidityLoanDetailDialog_LoanRefNumber.value"),PennantRegularExpressions.REGEX_ALPHANUM_CODE,true));
		}
		//Unit Buy Price
		if (!this.unitBuyPrice.isReadonly()){
			this.unitBuyPrice.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CommidityLoanDetailDialog_UnitBuyPrice.value"),PennantConstants.defaultCCYDecPos,true,false,0));
		}
		//Unit Sell Price
		if (!this.unitSellPrice.isReadonly()){
			this.unitSellPrice.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CommidityLoanDetailDialog_UnitSellPrice.value"),PennantConstants.defaultCCYDecPos,true,false,0));
		}
		//Quantity
		if (!this.quantity.isReadonly()){
			this.quantity.setConstraint(new PTNumberValidator(Labels.getLabel("label_CommidityLoanDetailDialog_Quantity.value"),true,false,0));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.loanRefNumber.setConstraint("");
		this.unitBuyPrice.setConstraint("");
		this.unitSellPrice.setConstraint("");
		this.quantity.setConstraint("");
		logger.debug("Leaving");
	}


	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		this.lovDescItemType.setConstraint("NO EMPTY:" + Labels.getLabel(
				"FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CommidityLoanDetailDialog_ItemType.value")}));
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		this.lovDescItemType.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */

	private void doClearMessage() {
		logger.debug("Entering");
		this.loanRefNumber.setErrorMessage("");
		this.itemType.setErrorMessage("");
		this.unitBuyPrice.setErrorMessage("");
		this.unitSellPrice.setErrorMessage("");
		this.quantity.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */

	private void refreshList(){
		final JdbcSearchObject<CommidityLoanDetail> soCommidityLoanDetail = getCommidityLoanDetailListCtrl().getSearchObj();
		getCommidityLoanDetailListCtrl().pagingCommidityLoanDetailList.setActivePage(0);
		getCommidityLoanDetailListCtrl().getPagedListWrapper().setSearchObject(soCommidityLoanDetail);
		if(getCommidityLoanDetailListCtrl().listBoxCommidityLoanDetail!=null){
			getCommidityLoanDetailListCtrl().listBoxCommidityLoanDetail.getListModel();
		}
	} 


	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering");
		boolean close=true;
		if (!enqModule && isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close=false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("isDataChanged : false");
		}

		if(close){
			closeDialog(this.window_CommidityLoanDetailDialog, "CommidityLoanDetailDialog");	
		}

		logger.debug("Leaving") ;
	}

	/**
	 * Deletes a CommidityLoanDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final CommidityLoanDetail aCommidityLoanDetail = new CommidityLoanDetail();
		BeanUtils.copyProperties(getCommidityLoanDetail(), aCommidityLoanDetail);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aCommidityLoanDetail.getLoanRefNumber();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCommidityLoanDetail.getRecordType()).equals("")){
				aCommidityLoanDetail.setVersion(aCommidityLoanDetail.getVersion()+1);
				aCommidityLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aCommidityLoanDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aCommidityLoanDetail.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aCommidityLoanDetail.getNextTaskId(), aCommidityLoanDetail);
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aCommidityLoanDetail,tranType)){
					refreshList();
					closeDialog(this.window_CommidityLoanDetailDialog, "CommidityLoanDetailDialog"); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showErrorMessage(this.window_CommidityLoanDetailDialog,e);
			}

		}
		logger.debug("Leaving");
	}


	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.loanRefNumber.setValue("");
		this.itemType.setValue("");
		this.unitBuyPrice.setValue("");
		this.unitSellPrice.setValue("");
		this.quantity.setText("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final CommidityLoanDetail aCommidityLoanDetail = new CommidityLoanDetail();
		BeanUtils.copyProperties(getCommidityLoanDetail(), aCommidityLoanDetail);
		boolean isNew = false;

		if(isWorkFlowEnabled()){
			aCommidityLoanDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aCommidityLoanDetail.getNextTaskId(), aCommidityLoanDetail);
		}

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if(!PennantConstants.RECORD_TYPE_DEL.equals(aCommidityLoanDetail.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the CommidityLoanDetail object with the components data
			doWriteComponentsToBean(aCommidityLoanDetail);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aCommidityLoanDetail.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCommidityLoanDetail.getRecordType()).equals("")){
				aCommidityLoanDetail.setVersion(aCommidityLoanDetail.getVersion()+1);
				if(isNew){
					aCommidityLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCommidityLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCommidityLoanDetail.setNewRecord(true);
				}
			}
		}else{
			aCommidityLoanDetail.setVersion(aCommidityLoanDetail.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aCommidityLoanDetail,tranType)){
				//doWriteBeanToComponents(aCommidityLoanDetail);
				refreshList();
				closeDialog(this.window_CommidityLoanDetailDialog, "CommidityLoanDetailDialog");
			}

		} catch (final DataAccessException e) {
			logger.error(e);

			showErrorMessage(this.window_CommidityLoanDetailDialog,e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */

	private boolean doProcess(CommidityLoanDetail aCommidityLoanDetail,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		aCommidityLoanDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCommidityLoanDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCommidityLoanDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (PennantConstants.WF_Audit_Notes.equals(getAuditingReq())) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			aCommidityLoanDetail.setTaskId(getTaskId());
			aCommidityLoanDetail.setNextTaskId(getNextTaskId());
			aCommidityLoanDetail.setRoleCode(getRole());
			aCommidityLoanDetail.setNextRoleCode(getNextRoleCode());

			if (StringUtils.trimToEmpty(getOperationRefs()).equals("")) {
				processCompleted = doSaveProcess(getAuditHeader(aCommidityLoanDetail, tranType),null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader =  getAuditHeader(aCommidityLoanDetail, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			processCompleted = doSaveProcess(getAuditHeader(aCommidityLoanDetail, tranType), null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param  AuditHeader auditHeader
	 * @param method  (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;

		CommidityLoanDetail aCommidityLoanDetail = (CommidityLoanDetail) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())){
						auditHeader = getCommidityLoanDetailService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getCommidityLoanDetailService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))){
						auditHeader = getCommidityLoanDetailService().doApprove(auditHeader);

						if(PennantConstants.RECORD_TYPE_DEL.equals(aCommidityLoanDetail.getRecordType())){
							deleteNotes=true;
						}

					}else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))){
						auditHeader = getCommidityLoanDetailService().doReject(auditHeader);
						if(PennantConstants.RECORD_TYPE_NEW.equals(aCommidityLoanDetail.getRecordType())){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CommidityLoanDetailDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_CommidityLoanDetailDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes("CommidityLoanDetail",aCommidityLoanDetail.getLoanRefNumber(),aCommidityLoanDetail.getVersion()),true);
					}
				}

				if (retValue==PennantConstants.porcessOVERIDE){
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ WorkFlow Components+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(CommidityLoanDetail aCommidityLoanDetail, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCommidityLoanDetail.getBefImage(), aCommidityLoanDetail);   
		return new AuditHeader(aCommidityLoanDetail.getLoanRefNumber(),null,null,null,auditDetail,aCommidityLoanDetail.getUserDetails(),getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
	}	

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public CommidityLoanDetail getCommidityLoanDetail() {
		return this.commidityLoanDetail;
	}

	public void setCommidityLoanDetail(CommidityLoanDetail commidityLoanDetail) {
		this.commidityLoanDetail = commidityLoanDetail;
	}

	public void setCommidityLoanDetailService(CommidityLoanDetailService commidityLoanDetailService) {
		this.commidityLoanDetailService = commidityLoanDetailService;
	}

	public CommidityLoanDetailService getCommidityLoanDetailService() {
		return this.commidityLoanDetailService;
	}

	public void setCommidityLoanDetailListCtrl(CommidityLoanDetailListCtrl commidityLoanDetailListCtrl) {
		this.commidityLoanDetailListCtrl = commidityLoanDetailListCtrl;
	}

	public CommidityLoanDetailListCtrl getCommidityLoanDetailListCtrl() {
		return this.commidityLoanDetailListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

}

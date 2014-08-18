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
 * FileName    		:  GoodsLoanDetailDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.lmtmasters.goodsloandetail;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.GoodsLoanDetail;
import com.pennant.backend.service.lmtmasters.GoodsLoanDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
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
 * /WEB-INF/pages/LMTMasters/GoodsLoanDetail/goodsLoanDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class GoodsLoanDetailDialogListCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(GoodsLoanDetailDialogListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_GoodsLoanDetailDialogList; 
	protected Row 			row0; 
	protected Label 		label_LoanRefNumber;
	protected Hlayout 		hlayout_LoanRefNumber;
	protected Space 		space_LoanRefNumber; 

	protected Textbox 		loanRefNumber; 

	protected Row 			row1; 
	protected Label 		label_ItemNumber;
	protected Hlayout 		hlayout_ItemNumber;
	protected Space 		space_ItemNumber; 

	protected Textbox 		itemNumber; 
	protected Label 		label_ItemDescription;
	protected Hlayout 		hlayout_ItemDescription;
	protected Space 		space_ItemDescription; 

	protected Textbox 		itemDescription; 
	protected Row 			row2; 
	protected Label 		label_UnitPrice;
	protected Hlayout 		hlayout_UnitPrice;
	protected Space 		space_UnitPrice; 

	protected Decimalbox	unitPrice; 
	protected Label 		label_Quantity;
	protected Hlayout 		hlayout_Quantity;
	protected Space 		space_Quantity; 

	protected Intbox 		quantity; 
	protected Row 			row3; 
	protected Label 		label_Addtional1;
	protected Hlayout 		hlayout_Addtional1;
	protected Space 		space_Addtional1; 

	protected Textbox 		addtional1; 
	protected Label 		label_Addtional2;
	protected Hlayout 		hlayout_Addtional2;
	protected Space 		space_Addtional2; 

	protected Textbox 		addtional2; 
	protected Row 			row4; 
	protected Label 		label_Addtional3;
	protected Hlayout 		hlayout_Addtional3;
	protected Space 		space_Addtional3; 

	protected Intbox 		addtional3; 
	protected Label 		label_Addtional4;
	protected Hlayout 		hlayout_Addtional4;
	protected Space 		space_Addtional4; 

	protected Intbox 		addtional4; 
	protected Row 			row5; 
	protected Label 		label_Addtional5;
	protected Hlayout 		hlayout_Addtional5;
	protected Space 		space_Addtional5; 

	protected Datebox 		addtional5; 
	protected Label 		label_Addtional6;
	protected Hlayout 		hlayout_Addtional6;
	protected Space 		space_Addtional6; 

	protected Datebox 		addtional6; 
	protected Row 			row6; 
	protected Label 		label_Addtional7;
	protected Hlayout 		hlayout_Addtional7;
	protected Space 		space_Addtional7; 

	protected Decimalbox	addtional7; 
	protected Label 		label_Addtional8;
	protected Hlayout 		hlayout_Addtional8;
	protected Space 		space_Addtional8; 

	protected Decimalbox	addtional8; 

	protected Longbox       sellerID;
	protected Textbox        lovDescSellerID;
	protected Space         space_SellerID;
	protected Label         label_SellerID;
	protected Hlayout       hlayout_SellerID;

	protected Label 		recordStatus; 
	protected Label 		recordType;	 
	protected Radiogroup 	userAction;
	protected Groupbox 		gb_statusDetails;
	protected Groupbox 		groupboxWf;
	protected South 		south;
	private boolean 		enqModule=false;

	// not auto wired vars
	private GoodsLoanDetail goodsLoanDetail; // overhanded per param
	private transient GoodsLoanDetailListCtrl goodsLoanDetailListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String  		oldVar_LoanRefNumber;
	private transient String  		oldVar_ItemNumber;
	private transient Long  		oldVar_SellerID;
	private transient String  		oldVar_lovDescSellerID;
	private transient String  		oldVar_ItemDescription;
	private transient BigDecimal  	oldVar_UnitPrice;
	private transient int  			oldVar_Quantity;
	private transient String  		oldVar_Addtional1;
	private transient String  		oldVar_Addtional2;
	private transient int  			oldVar_Addtional3;
	private transient int  			oldVar_Addtional4;
	private transient Date  		oldVar_Addtional5;
	private transient Date  		oldVar_Addtional6;
	private transient BigDecimal  	oldVar_Addtional7;
	private transient BigDecimal  	oldVar_Addtional8;
	private transient String 		oldVar_recordStatus;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_GoodsLoanDetailDialog_";
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
	private transient GoodsLoanDetailService goodsLoanDetailService;

	private Object financeMainDialogCtrl;
	private FinGoodsLoanDetailListCtrl finGoodsLoanDetailListCtrl;
	private boolean newRecord=false;
	private boolean newCustomer=false;
	private int ccyFormatter = 0;

	@SuppressWarnings("unused")
	private String moduleType="";

	private List<GoodsLoanDetail> goodsLoanDetails;

	/**
	 * default constructor.<br>
	 */
	public GoodsLoanDetailDialogListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected GoodsLoanDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_GoodsLoanDetailDialogList(Event event) throws Exception {
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
			if (args.containsKey("goodsLoanDetail")) {
				this.goodsLoanDetail = (GoodsLoanDetail) args.get("goodsLoanDetail");
				GoodsLoanDetail befImage =new GoodsLoanDetail();
				BeanUtils.copyProperties(this.goodsLoanDetail, befImage);
				this.goodsLoanDetail.setBefImage(befImage);

				setGoodsLoanDetail(this.goodsLoanDetail);
			} else {
				setGoodsLoanDetail(null);
			}

			if (args.containsKey("moduleType")) {
				this.moduleType = (String) args.get("moduleType");
			}

			if(getGoodsLoanDetail().isNewRecord()){
				setNewRecord(true);
			}
			if(args.containsKey("finGoodsLoanDetailListCtrl")){
				setFinGoodsLoanDetailListCtrl((FinGoodsLoanDetailListCtrl) args.get("finGoodsLoanDetailListCtrl"));
			}
			if(args.containsKey("financeMainDialogCtrl")){

				setFinanceMainDialogCtrl((Object) args.get("financeMainDialogCtrl"));
				setNewCustomer(true);

				if(args.containsKey("ccyFormatter")){
					ccyFormatter =  (Integer) args.get("ccyFormatter");
				}

				if(args.containsKey("newRecord")){
					setNewRecord(true);
				}else{
					setNewRecord(false);
				}
				this.goodsLoanDetail.setWorkflowId(0);
				if(args.containsKey("roleCode")){
					setRole((String)args.get("roleCode"));
					getUserWorkspace().alocateRoleAuthorities(getRole(), "GoodsLoanDetailDialog");
				}
			}else{
				ccyFormatter = 2;
			}

			doLoadWorkFlow(this.goodsLoanDetail.isWorkflow(),this.goodsLoanDetail.getWorkflowId(),this.goodsLoanDetail.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule){
				this.userAction	= setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "GoodsLoanDetailDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the goodsLoanDetailListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete goodsLoanDetail here.
			if (args.containsKey("goodsLoanDetailListCtrl")) {
				setGoodsLoanDetailListCtrl((GoodsLoanDetailListCtrl) args.get("goodsLoanDetailListCtrl"));
			} else {
				setGoodsLoanDetailListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getGoodsLoanDetail());

		} catch (Exception e) {
			createException(window_GoodsLoanDetailDialogList, e);
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
		PTMessageUtils.showHelpWindow(event, window_GoodsLoanDetailDialogList);
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
	public void onClose$window_GoodsLoanDetailDialog(Event event) throws Exception {
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

			ScreenCTL.displayNotes(getNotes("GoodsLoanDetail",getGoodsLoanDetail().getLoanRefNumber(),getGoodsLoanDetail().getVersion()),this);

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
	 * @param aGoodsLoanDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(GoodsLoanDetail aGoodsLoanDetail) throws InterruptedException {
		logger.debug("Entering") ;

		// if aGoodsLoanDetail == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aGoodsLoanDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aGoodsLoanDetail = getGoodsLoanDetailService().getNewGoodsLoanDetail();

			setGoodsLoanDetail(aGoodsLoanDetail);
		} else {
			setGoodsLoanDetail(aGoodsLoanDetail);
		}

		try {

			// fill the components with the data
			doWriteBeanToComponents(aGoodsLoanDetail);
			// set ReadOnly mode accordingly if the object is new or not.

			int screenCode = ScreenCTL.getMode(enqModule, isWorkFlowEnabled(), aGoodsLoanDetail.isNewRecord());
			if (screenCode == ScreenCTL.SCRN_GNINT) {
				screenCode = ScreenCTL.SCRN_GNEDT;
			}
			displayComponents(screenCode);

			doStoreInitValues();

			this.gb_statusDetails.setVisible(false);
			this.window_GoodsLoanDetailDialogList.doModal();
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

		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, isWorkFlowEnabled(),isFirstTask(), this.userAction,this.loanRefNumber,this.itemNumber));

		if (!getGoodsLoanDetail().isNewRecord() && !enqModule){
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_GoodsLoanDetailDialog_btnDelete"));
		}
		setComponentAccessType("GoodsLoanDetailDialog_ItemNumber", true, this.itemNumber, null, this.label_ItemNumber, this.hlayout_ItemNumber,null);
		logger.debug("Leaving");
	} 

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");

		boolean tempReadOnly= readOnly;

		if(readOnly || (!readOnly && (PennantConstants.RECORD_TYPE_DEL.equals(goodsLoanDetail.getRecordType())))) {
			tempReadOnly=true;
		}

		setComponentAccessType("GoodsLoanDetailDialog_LoanRefNumber", true, this.loanRefNumber, null, this.label_LoanRefNumber, this.hlayout_LoanRefNumber,null);		
		setComponentAccessType("GoodsLoanDetailDialog_SellerID", tempReadOnly, this.sellerID, null, this.label_SellerID, this.hlayout_SellerID,null);
		setComponentAccessType("GoodsLoanDetailDialog_ItemNumber", tempReadOnly, this.itemNumber, null, this.label_ItemNumber, this.hlayout_ItemNumber,null);
		setComponentAccessType("GoodsLoanDetailDialog_ItemDescription", tempReadOnly, this.itemDescription, this.space_ItemDescription, this.label_ItemDescription, this.hlayout_ItemDescription,null);
		setRowInvisible(this.row1, this.hlayout_ItemNumber,this.hlayout_ItemDescription);
		setComponentAccessType("GoodsLoanDetailDialog_UnitPrice", tempReadOnly, this.unitPrice, this.space_UnitPrice, this.label_UnitPrice, this.hlayout_UnitPrice,null);
		setComponentAccessType("GoodsLoanDetailDialog_Quantity", tempReadOnly, this.quantity, this.space_Quantity, this.label_Quantity, this.hlayout_Quantity,null);
		setRowInvisible(this.row2, this.hlayout_UnitPrice,this.hlayout_Quantity);
		
		//Additional unused Fields
		setComponentAccessType("GoodsLoanDetailDialog_Addtional1", tempReadOnly, this.addtional1, this.space_Addtional1, this.label_Addtional1, this.hlayout_Addtional1,null);
		setComponentAccessType("GoodsLoanDetailDialog_Addtional2", tempReadOnly, this.addtional2, this.space_Addtional2, this.label_Addtional2, this.hlayout_Addtional2,null);
		setComponentAccessType("GoodsLoanDetailDialog_Addtional3", tempReadOnly, this.addtional3, this.space_Addtional3, this.label_Addtional3, this.hlayout_Addtional3,null);
		setComponentAccessType("GoodsLoanDetailDialog_Addtional4", tempReadOnly, this.addtional4, this.space_Addtional4, this.label_Addtional4, this.hlayout_Addtional4,null);
		setComponentAccessType("GoodsLoanDetailDialog_Addtional5", tempReadOnly, this.addtional5, this.space_Addtional5, this.label_Addtional5, this.hlayout_Addtional5,null);
		setComponentAccessType("GoodsLoanDetailDialog_Addtional6", tempReadOnly, this.addtional6, this.space_Addtional6, this.label_Addtional6, this.hlayout_Addtional6,null);
		setComponentAccessType("GoodsLoanDetailDialog_Addtional7", tempReadOnly, this.addtional7, this.space_Addtional7, this.label_Addtional7, this.hlayout_Addtional7,null);
		setComponentAccessType("GoodsLoanDetailDialog_Addtional8", tempReadOnly, this.addtional8, this.space_Addtional8, this.label_Addtional8, this.hlayout_Addtional8,null);

		this.itemDescription.setReadonly(getUserWorkspace().isReadOnly("GoodsLoanDetailDialog_ItemNumber"));
		this.unitPrice.setReadonly(getUserWorkspace().isReadOnly("GoodsLoanDetailDialog_UnitPrice"));
		this.quantity.setReadonly(getUserWorkspace().isReadOnly("GoodsLoanDetailDialog_Quantity"));

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
		getUserWorkspace().alocateAuthorities("GoodsLoanDetailDialog", getRole());
		if(!enqModule){
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_GoodsLoanDetailDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_GoodsLoanDetailDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_GoodsLoanDetailDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_GoodsLoanDetailDialog_btnSave"));	
		}

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		logger.debug("Leaving") ;
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.loanRefNumber.setMaxlength(20);
		this.itemNumber.setMaxlength(20);
		this.itemDescription.setMaxlength(50);
		this.unitPrice.setMaxlength(18);
		this.unitPrice.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.unitPrice.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.unitPrice.setScale(ccyFormatter);
		this.quantity.setMaxlength(10);
		this.addtional1.setMaxlength(100);
		this.addtional2.setMaxlength(100);
		this.addtional3.setMaxlength(10);
		this.addtional4.setMaxlength(10);
		this.addtional5.setFormat(PennantConstants.dateFormat);
		this.addtional6.setFormat(PennantConstants.dateFormat);
		this.addtional7.setMaxlength(18);
		this.addtional7.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.addtional7.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.addtional7.setScale(ccyFormatter);
		this.addtional8.setMaxlength(18);
		this.addtional8.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.addtional8.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.addtional8.setScale(ccyFormatter);

		setStatusDetails(gb_statusDetails,groupboxWf,south,enqModule);
		logger.debug("Leaving") ;
	}


	/**
	 * Stores the initialinitial values to member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_LoanRefNumber = this.loanRefNumber.getValue();
		this.oldVar_ItemNumber = this.itemNumber.getValue();
		this.oldVar_SellerID  = this.sellerID.longValue();
		this.oldVar_lovDescSellerID=this.lovDescSellerID.getValue();
		this.oldVar_ItemDescription = this.itemDescription.getValue();
		this.oldVar_UnitPrice = this.unitPrice.getValue();
		this.oldVar_Quantity = this.quantity.intValue();	
		this.oldVar_Addtional1 = this.addtional1.getValue();
		this.oldVar_Addtional2 = this.addtional2.getValue();
		this.oldVar_Addtional3 = this.addtional3.intValue();	
		this.oldVar_Addtional4 = this.addtional4.intValue();	
		this.oldVar_Addtional5 = this.addtional5.getValue();
		this.oldVar_Addtional6 = this.addtional6.getValue();
		this.oldVar_Addtional7 = this.addtional7.getValue();
		this.oldVar_Addtional8 = this.addtional8.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.loanRefNumber.setValue(this.oldVar_LoanRefNumber);
		this.itemNumber.setValue(this.oldVar_ItemNumber);
		this.sellerID.setValue(this.oldVar_SellerID);
		this.lovDescSellerID.setValue(this.oldVar_lovDescSellerID);
		this.itemDescription.setValue(this.oldVar_ItemDescription);
		this.unitPrice.setValue(this.oldVar_UnitPrice);
		this.quantity.setValue(this.oldVar_Quantity);
		this.addtional1.setValue(this.oldVar_Addtional1);
		this.addtional2.setValue(this.oldVar_Addtional2);
		this.addtional3.setValue(this.oldVar_Addtional3);
		this.addtional4.setValue(this.oldVar_Addtional4);
		this.addtional5.setValue(this.oldVar_Addtional5);
		this.addtional6.setValue(this.oldVar_Addtional6);
		this.addtional7.setValue(this.oldVar_Addtional7);
		this.addtional8.setValue(this.oldVar_Addtional8);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if(isWorkFlowEnabled() & !enqModule){	
			this.userAction.setSelectedIndex(0);	
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aGoodsLoanDetail
	 *            GoodsLoanDetail
	 */
	public void doWriteBeanToComponents(GoodsLoanDetail aGoodsLoanDetail) {
		logger.debug("Entering") ;
		this.loanRefNumber.setValue(aGoodsLoanDetail.getLoanRefNumber());
		this.itemNumber.setValue(aGoodsLoanDetail.getItemNumber());
		this.sellerID.setValue(aGoodsLoanDetail.getSellerID());
		this.itemDescription.setValue(aGoodsLoanDetail.getItemDescription());
		this.unitPrice.setValue(PennantApplicationUtil.formateAmount(aGoodsLoanDetail.getUnitPrice(),ccyFormatter));
		this.quantity.setValue(aGoodsLoanDetail.getQuantity());
		this.addtional1.setValue(aGoodsLoanDetail.getAddtional1());
		this.addtional2.setValue(aGoodsLoanDetail.getAddtional2());
		this.addtional3.setValue(aGoodsLoanDetail.getAddtional3());
		this.addtional4.setValue(aGoodsLoanDetail.getAddtional4());
		this.addtional5.setValue(aGoodsLoanDetail.getAddtional5());
		this.addtional6.setValue(aGoodsLoanDetail.getAddtional6());
		this.addtional7.setValue(PennantApplicationUtil.formateAmount(aGoodsLoanDetail.getAddtional7(),ccyFormatter));
		this.addtional8.setValue(PennantApplicationUtil.formateAmount(aGoodsLoanDetail.getAddtional8(),ccyFormatter));
		this.lovDescSellerID.setValue(aGoodsLoanDetail.getLovDescSellerID());
		this.recordStatus.setValue(aGoodsLoanDetail.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aGoodsLoanDetail.getRecordType()));
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aGoodsLoanDetail
	 */
	public void doWriteComponentsToBean(GoodsLoanDetail aGoodsLoanDetail) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Loan Ref Number
		try {
			aGoodsLoanDetail.setLoanRefNumber(this.loanRefNumber.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Item Number
		try {
			aGoodsLoanDetail.setItemNumber(this.itemNumber.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Seller ID
		try {
			aGoodsLoanDetail.setLovDescSellerID(this.lovDescSellerID.getValue());
			aGoodsLoanDetail.setSellerID(this.sellerID.longValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		//Item Description
		try {
			aGoodsLoanDetail.setItemDescription(this.itemDescription.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Unit Price
		try {
			if(this.unitPrice.getValue()!=null){
				aGoodsLoanDetail.setUnitPrice(PennantApplicationUtil.unFormateAmount(this.unitPrice.getValue(),ccyFormatter));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Quantity
		try {
			aGoodsLoanDetail.setQuantity(this.quantity.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		/*	//Addtional1
		try {
		    aGoodsLoanDetail.setAddtional1(this.addtional1.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Addtional2
		try {
		    aGoodsLoanDetail.setAddtional2(this.addtional2.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Addtional3
		try {
		    aGoodsLoanDetail.setAddtional3(this.addtional3.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Addtional4
		try {
		    aGoodsLoanDetail.setAddtional4(this.addtional4.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Addtional5
		try {
		    aGoodsLoanDetail.setAddtional5(this.addtional5.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Addtional6
		try {
		    aGoodsLoanDetail.setAddtional6(this.addtional6.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Addtional7
		try {
			if(this.addtional7.getValue()!=null){
			 	aGoodsLoanDetail.setAddtional7(PennantApplicationUtil.unFormateAmount(this.addtional7.getValue(),ccyFormatter));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Addtional8
		try {
			if(this.addtional8.getValue()!=null){
			 	aGoodsLoanDetail.setAddtional8(PennantApplicationUtil.unFormateAmount(this.addtional8.getValue(),ccyFormatter));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}*/

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		aGoodsLoanDetail.setRecordStatus(this.recordStatus.getValue());
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

		if (!StringUtils.trimToEmpty(this.oldVar_ItemNumber).equals(StringUtils.trimToEmpty(this.itemNumber.getValue()))) {
			return true;
		}

		if (this.oldVar_SellerID != this.sellerID.longValue()) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_ItemDescription).equals(StringUtils.trimToEmpty(this.itemDescription.getValue()))) {
			return true;
		}
		if (this.oldVar_UnitPrice != this.unitPrice.getValue()) {
			return true;
		}
		if (this.oldVar_Quantity != this.quantity.intValue()) {
			return  true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_Addtional1).equals(StringUtils.trimToEmpty(this.addtional1.getValue()))) {
			return true;
		}

		if (!StringUtils.trimToEmpty(this.oldVar_Addtional2).equals(StringUtils.trimToEmpty(this.addtional2.getValue()))) {
			return true;
		}
		if (this.oldVar_Addtional3 != this.addtional3.intValue()) {
			return  true;
		}
		if (this.oldVar_Addtional4 != this.addtional4.intValue()) {
			return  true;
		}
		String old_Addtional5 = "";
		String new_Addtional5 ="";
		if (this.oldVar_Addtional5!=null){
			old_Addtional5=DateUtility.formatDate(this.oldVar_Addtional5,PennantConstants.dateFormat);
		}
		if (this.addtional5.getValue()!=null){
			new_Addtional5=DateUtility.formatDate(this.addtional5.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_Addtional5).equals(StringUtils.trimToEmpty(new_Addtional5))) {
			return true;
		}
		String old_Addtional6 = "";
		String new_Addtional6 ="";
		if (this.oldVar_Addtional6!=null){
			old_Addtional6=DateUtility.formatDate(this.oldVar_Addtional6,PennantConstants.dateFormat);
		}
		if (this.addtional6.getValue()!=null){
			new_Addtional6=DateUtility.formatDate(this.addtional6.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_Addtional6).equals(StringUtils.trimToEmpty(new_Addtional6))) {
			return true;
		}
		if (this.oldVar_Addtional7 != this.addtional7.getValue()) {
			return true;
		}
		if (this.oldVar_Addtional8 != this.addtional8.getValue()) {
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
			this.loanRefNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_GoodsLoanDetailDialog_LoanRefNumber.value"),PennantRegularExpressions.REGEX_ALPHANUM_CODE,true));
		}
		//Item Number
		if (!this.itemNumber.isReadonly()){
			this.itemNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_GoodsLoanDetailDialog_ItemNumber.value"),PennantRegularExpressions.REGEX_NUMERIC,true));
		}
		//Item Description
		if (!this.itemDescription.isReadonly()){
			this.itemDescription.setConstraint(new PTStringValidator(Labels.getLabel("label_GoodsLoanDetailDialog_ItemDescription.value"),PennantRegularExpressions.REGEX_ALPHANUM_SPACE,true));
		}
		//Unit Price
		if (!this.unitPrice.isReadonly()){
			this.unitPrice.setConstraint(new PTDecimalValidator(Labels.getLabel("label_GoodsLoanDetailDialog_UnitPrice.value"),ccyFormatter,true,false,0));
		}
		//Quantity
		if (!this.quantity.isReadonly()){
			this.quantity.setConstraint(new PTNumberValidator(Labels.getLabel("label_GoodsLoanDetailDialog_Quantity.value"),true,false,0));
		}
		//Addtional1
		if (!this.addtional1.isReadonly()){
			this.addtional1.setConstraint(new PTStringValidator(Labels.getLabel("label_GoodsLoanDetailDialog_Addtional1.value"),PennantRegularExpressions.REGEX_NAME,true));
		}
		//Addtional2
		if (!this.addtional2.isReadonly()){
			this.addtional2.setConstraint(new PTStringValidator(Labels.getLabel("label_GoodsLoanDetailDialog_Addtional2.value"),PennantRegularExpressions.REGEX_NAME,true));
		}
		//Addtional3
		if (!this.addtional3.isReadonly()){
			this.addtional3.setConstraint(new PTNumberValidator(Labels.getLabel("label_GoodsLoanDetailDialog_Addtional3.value"),true,false,0));
		}
		//Addtional4
		if (!this.addtional4.isReadonly()){
			this.addtional4.setConstraint(new PTNumberValidator(Labels.getLabel("label_GoodsLoanDetailDialog_Addtional4.value"),true,false,0));
		}
		//Addtional5
		if (!this.addtional5.isReadonly()){
			this.addtional5.setConstraint(new PTDateValidator(Labels.getLabel("label_GoodsLoanDetailDialog_Addtional5.value"),true));
		}
		//Addtional6
		if (!this.addtional6.isReadonly()){
			this.addtional6.setConstraint(new PTDateValidator(Labels.getLabel("label_GoodsLoanDetailDialog_Addtional6.value"),true));
		}
		//Addtional7
		if (!this.addtional7.isReadonly()){
			this.addtional7.setConstraint(new PTDecimalValidator(Labels.getLabel("label_GoodsLoanDetailDialog_Addtional7.value"),ccyFormatter,true,false,0));
		}
		//Addtional8
		if (!this.addtional8.isReadonly()){
			this.addtional8.setConstraint(new PTDecimalValidator(Labels.getLabel("label_GoodsLoanDetailDialog_Addtional8.value"),ccyFormatter,true,false,0));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.loanRefNumber.setConstraint("");
		this.itemNumber.setConstraint("");
		this.itemDescription.setConstraint("");
		this.unitPrice.setConstraint("");
		this.quantity.setConstraint("");
		this.addtional1.setConstraint("");
		this.addtional2.setConstraint("");
		this.addtional3.setConstraint("");
		this.addtional4.setConstraint("");
		this.addtional5.setConstraint("");
		this.addtional6.setConstraint("");
		this.addtional7.setConstraint("");
		this.addtional8.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		this.lovDescSellerID.setConstraint("NO EMPTY:" + Labels.getLabel(
				"FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_GoodsLoanDetailDialog_SellerID.value")}));
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveLOVValidation() {
		this.lovDescSellerID.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.loanRefNumber.setErrorMessage("");
		this.itemNumber.setErrorMessage("");
		this.lovDescSellerID.setErrorMessage("");
		this.itemDescription.setErrorMessage("");
		this.unitPrice.setErrorMessage("");
		this.quantity.setErrorMessage("");
		this.addtional1.setErrorMessage("");
		this.addtional2.setErrorMessage("");
		this.addtional3.setErrorMessage("");
		this.addtional4.setErrorMessage("");
		this.addtional5.setErrorMessage("");
		this.addtional6.setErrorMessage("");
		this.addtional7.setErrorMessage("");
		this.addtional8.setErrorMessage("");
		logger.debug("Leaving");
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
			this.window_GoodsLoanDetailDialogList.onClose();	
		}

		logger.debug("Leaving") ;
	}

	/**
	 * Deletes a GoodsLoanDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final GoodsLoanDetail aGoodsLoanDetail = new GoodsLoanDetail();
		BeanUtils.copyProperties(getGoodsLoanDetail(), aGoodsLoanDetail);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aGoodsLoanDetail.getLoanRefNumber();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aGoodsLoanDetail.getRecordType()).equals("")){
				aGoodsLoanDetail.setVersion(aGoodsLoanDetail.getVersion()+1);
				aGoodsLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aGoodsLoanDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aGoodsLoanDetail.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aGoodsLoanDetail.getNextTaskId(), aGoodsLoanDetail);
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(isNewCustomer()){
					tranType=PennantConstants.TRAN_DEL;
					AuditHeader auditHeader =  newGoodsProcess(aGoodsLoanDetail,tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_GoodsLoanDetailDialogList, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
						getFinGoodsLoanDetailListCtrl().doFillGoodLoanDetails(this.goodsLoanDetails);
						this.window_GoodsLoanDetailDialogList.onClose();
					}	
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showErrorMessage(this.window_GoodsLoanDetailDialogList,e);
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
		this.itemNumber.setValue("");
		this.sellerID.setText("");
		this.lovDescSellerID.setText("");
		this.itemDescription.setValue("");
		this.unitPrice.setValue("");
		this.quantity.setText("");
		this.addtional1.setValue("");
		this.addtional2.setValue("");
		this.addtional3.setText("");
		this.addtional4.setText("");
		this.addtional5.setText("");
		this.addtional6.setText("");
		this.addtional7.setValue("");
		this.addtional8.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final GoodsLoanDetail aGoodsLoanDetail = new GoodsLoanDetail();
		BeanUtils.copyProperties(getGoodsLoanDetail(), aGoodsLoanDetail);
		boolean isNew = false;

		if(isWorkFlowEnabled()){
			aGoodsLoanDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aGoodsLoanDetail.getNextTaskId(), aGoodsLoanDetail);
		}

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if(!PennantConstants.RECORD_TYPE_DEL.equals(aGoodsLoanDetail.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the GoodsLoanDetail object with the components data
			doWriteComponentsToBean(aGoodsLoanDetail);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aGoodsLoanDetail.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aGoodsLoanDetail.getRecordType()).equals("")){
				aGoodsLoanDetail.setVersion(aGoodsLoanDetail.getVersion()+1);
				if(isNew){
					aGoodsLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aGoodsLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aGoodsLoanDetail.setNewRecord(true);
				}
			}
		}else{

			if(isNewCustomer()){
				if(isNewRecord()){
					aGoodsLoanDetail.setVersion(1);
					aGoodsLoanDetail.setRecordType(PennantConstants.RCD_ADD);
				}else{
					tranType = PennantConstants.TRAN_UPD;
				}

				if(StringUtils.trimToEmpty(aGoodsLoanDetail.getRecordType()).equals("")){
					aGoodsLoanDetail.setVersion(aGoodsLoanDetail.getVersion()+1);
					aGoodsLoanDetail.setRecordType(PennantConstants.RCD_UPD);
				}

				if(aGoodsLoanDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(aGoodsLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}
			}else{
				aGoodsLoanDetail.setVersion(aGoodsLoanDetail.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}			
		}

		// save it to database
		try {

			if(isNewCustomer()){
				AuditHeader auditHeader =  newGoodsProcess(aGoodsLoanDetail,tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_GoodsLoanDetailDialogList, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getFinGoodsLoanDetailListCtrl().doFillGoodLoanDetails(this.goodsLoanDetails);
					this.window_GoodsLoanDetailDialogList.onClose();
				}
			}

		} catch (final DataAccessException e) {
			logger.error(e);

			showErrorMessage(this.window_GoodsLoanDetailDialogList,e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newGoodsProcess(GoodsLoanDetail agoodsLoanDetail,String tranType){
		boolean recordAdded=false;

		AuditHeader auditHeader= getAuditHeader(agoodsLoanDetail, tranType);
		goodsLoanDetails = new ArrayList<GoodsLoanDetail>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(agoodsLoanDetail.getLoanRefNumber());
		valueParm[1] = agoodsLoanDetail.getItemNumber();

		errParm[0] = PennantJavaUtil.getLabel("label_LoanRefNumber") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_ItemNumber") + ":"+valueParm[1];

		if(getFinGoodsLoanDetailListCtrl().getGoodsDetailLists()!=null && getFinGoodsLoanDetailListCtrl().getGoodsDetailLists().size()>0){
			for (int i = 0; i < getFinGoodsLoanDetailListCtrl().getGoodsDetailLists().size(); i++) {
				GoodsLoanDetail loanDetail = getFinGoodsLoanDetailListCtrl().getGoodsDetailLists().get(i);

				if(agoodsLoanDetail.getLoanRefNumber().equals(loanDetail.getLoanRefNumber()) && (agoodsLoanDetail.getItemNumber().equals(loanDetail.getItemNumber())) ){ // Both Current and Existing list rating same

					if(isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if(tranType==PennantConstants.TRAN_DEL){
						if(agoodsLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							agoodsLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							goodsLoanDetails.add(agoodsLoanDetail);
						}else if(agoodsLoanDetail.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(agoodsLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							agoodsLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							goodsLoanDetails.add(agoodsLoanDetail);
						}else if(agoodsLoanDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getFinGoodsLoanDetailListCtrl().getFinancedetail().getGoodsLoanDetails().size(); j++) {
								GoodsLoanDetail income =  getFinGoodsLoanDetailListCtrl().getFinancedetail().getGoodsLoanDetails().get(j);
								if(income.getLoanRefNumber() == agoodsLoanDetail.getLoanRefNumber() && income.getItemNumber().equals(agoodsLoanDetail.getItemNumber())){
									goodsLoanDetails.add(income);
								}
							}
						}
					}else{
						if(tranType!=PennantConstants.TRAN_UPD){
							goodsLoanDetails.add(loanDetail);
						}
					}
				}else{
					goodsLoanDetails.add(loanDetail);
				}
			}
		}

		if(!recordAdded){
			goodsLoanDetails.add(agoodsLoanDetail);
		}
		return auditHeader;
	} 






	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ WorkFlow Components+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(GoodsLoanDetail aGoodsLoanDetail, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aGoodsLoanDetail.getBefImage(), aGoodsLoanDetail);   
		return new AuditHeader(aGoodsLoanDetail.getLoanRefNumber(),null,null,null,auditDetail,aGoodsLoanDetail.getUserDetails(),getOverideMap());
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

	public GoodsLoanDetail getGoodsLoanDetail() {
		return this.goodsLoanDetail;
	}
	public void setGoodsLoanDetail(GoodsLoanDetail goodsLoanDetail) {
		this.goodsLoanDetail = goodsLoanDetail;
	}

	public void setGoodsLoanDetailService(GoodsLoanDetailService goodsLoanDetailService) {
		this.goodsLoanDetailService = goodsLoanDetailService;
	}
	public GoodsLoanDetailService getGoodsLoanDetailService() {
		return this.goodsLoanDetailService;
	}

	public void setGoodsLoanDetailListCtrl(GoodsLoanDetailListCtrl goodsLoanDetailListCtrl) {
		this.goodsLoanDetailListCtrl = goodsLoanDetailListCtrl;
	}
	public GoodsLoanDetailListCtrl getGoodsLoanDetailListCtrl() {
		return this.goodsLoanDetailListCtrl;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	public boolean isNewRecord() {
		return newRecord;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}
	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setNewCustomer(boolean newCustomer) {
		this.newCustomer = newCustomer;
	}
	public boolean isNewCustomer() {
		return newCustomer;
	}

	public void setFinGoodsLoanDetailListCtrl(FinGoodsLoanDetailListCtrl finGoodsLoanDetailListCtrl) {
		this.finGoodsLoanDetailListCtrl = finGoodsLoanDetailListCtrl;
	}
	public FinGoodsLoanDetailListCtrl getFinGoodsLoanDetailListCtrl() {
		return finGoodsLoanDetailListCtrl;
	}

	public void setGoodsLoanDetails(List<GoodsLoanDetail> goodsLoanDetails) {
		this.goodsLoanDetails = goodsLoanDetails;
	}
	public List<GoodsLoanDetail> getGoodsLoanDetails() {
		return goodsLoanDetails;
	}

}

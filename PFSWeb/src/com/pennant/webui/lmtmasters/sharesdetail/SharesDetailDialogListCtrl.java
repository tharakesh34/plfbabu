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

package com.pennant.webui.lmtmasters.sharesdetail;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
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
import org.zkoss.zul.Caption;
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

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.SharesDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.lmtmasters.SharesDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
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
public class SharesDetailDialogListCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(SharesDetailDialogListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_SharesDetailDialog; 
	protected Label 		label_LoanRefNumber;
	protected Hlayout 		hlayout_LoanRefNumber;
	protected Space 		space_LoanRefNumber; 
	protected Textbox 		loanRefNumber;
	
	protected Label 		label_CompanyName;
	protected Hlayout 		hlayout_CompanyName;
	protected Space 		space_CompanyName; 
	protected Textbox 		CompanyName; 
	protected Textbox 		lovDescItemType; 
	
	protected Label 		label_Quantity;
	protected Hlayout 		hlayout_Quantity;
	protected Space 		space_Quantity; 
	protected Longbox 		quantity; 
	
	protected Label 		label_FaceValue;
	protected Hlayout 		hlayout_FaceValue;
	protected Space 		space_FaceValue; 
	protected Decimalbox	faceValue; 
	
	protected Label 		label_TotalFaceValue;
	protected Hlayout 		hlayout_TotalFaceValue;
	protected Space 		space_TotalFaceValue; 
	protected Decimalbox	totalFaceValue; 
	
	protected Label 		label_MarketValue;
	protected Hlayout 		hlayout_MarketValue;
	protected Space 		space_MarketValue; 
	protected Decimalbox	marketValue; 
	
	protected Label 		label_TotalMarketValue;
	protected Hlayout 		hlayout_TotalMarketValue;
	protected Space 		space_TotalMarketValue; 
	protected Decimalbox	totalMarketValue; 

	protected Label 		recordStatus; 
	protected Label 		recordType;	 
	protected Radiogroup 	userAction;
	protected Groupbox 		gb_statusDetails;
	protected Groupbox 		groupboxWf;
	protected South 		south;
	protected Caption       caption_sharesLoan;
	private boolean 		enqModule=false;

	// not auto wired vars
	private SharesDetail sharesDetail; // overhanded per param
	private transient  SharesDetailDialogListCtrl sharesDetailDialogListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String  			oldVar_LoanRefNumber;
	private transient String  			oldVar_Company;
	private transient long  			oldVar_Quantity;
	private transient BigDecimal  		oldVar_FaceValue;
	private transient BigDecimal  		oldVar_TotalFaceVale;
	private transient BigDecimal  		oldVar_MarketValue;
	private transient BigDecimal  		oldVar_TotalMarketValue;
	private transient String oldVar_recordStatus;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_SharesDetailDialog_";
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
	private transient SharesDetailService sharesDetailService;
	private transient PagedListService pagedListService;

	private Object financeMainDialogCtrl;
	private FinSharesDetailListCtrl finSharesDetailListCtrl;
	private boolean newRecord=false;
	private boolean newCustomer=false;
	private int ccyFormatter = 0;

	@SuppressWarnings("unused")
	private String moduleType="";

	private List<SharesDetail> sharesDetails;

	/**
	 * default constructor.<br>
	 */
	public SharesDetailDialogListCtrl() {
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
	public void onCreate$window_SharesDetailDialog(Event event) throws Exception {
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
			if (args.containsKey("sharesDetail")) {
				this.sharesDetail = (SharesDetail) args.get("sharesDetail");
				SharesDetail befImage = new SharesDetail();
				BeanUtils.copyProperties(this.sharesDetail, befImage);
				this.sharesDetail.setBefImage(befImage);

				setSharesDetail(this.sharesDetail);
			} else {
				setSharesDetail(null);
			}

			if (args.containsKey("moduleType")) {
				this.moduleType = (String) args.get("moduleType");
			}


			if(getSharesDetail().isNewRecord()){
				setNewRecord(true);
			}
			if(args.containsKey("finSharesDetailListCtrl")){
				setFinSharesDetailListCtrl((FinSharesDetailListCtrl)args.get("finSharesDetailListCtrl"));
			}
			if(args.containsKey("financeMainDialogCtrl")){
				setFinanceMainDialogCtrl((Object) args.get("financeMainDialogCtrl"));
				setNewCustomer(true);
				this.caption_sharesLoan.setVisible(true);
				if(args.containsKey("ccyFormatter")){
					ccyFormatter =  (Integer) args.get("ccyFormatter");;
				}

				if(args.containsKey("newRecord")){
					setNewRecord(true);
				}else{
					setNewRecord(false);
				}
				this.sharesDetail.setWorkflowId(0);
				if(args.containsKey("roleCode")){
					getUserWorkspace().alocateRoleAuthorities((String) args.get("roleCode"), "SharesDetailDialog");
				}
			}else{
				ccyFormatter = 2;
			}

			doLoadWorkFlow(this.sharesDetail.isWorkflow(),this.sharesDetail.getWorkflowId(),this.sharesDetail.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule){
				this.userAction	= setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "SharesDetailDialog");
			}else{
				getUserWorkspace().alocateAuthorities("SharesDetailDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getSharesDetail());
		} catch (Exception e) {
			createException(window_SharesDetailDialog, e);
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
		PTMessageUtils.showHelpWindow(event, window_SharesDetailDialog);
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

			ScreenCTL.displayNotes(getNotes("SharesDetail",getSharesDetail().getLoanRefNumber(),getSharesDetail().getVersion()),this);

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
	 * @param ashSharesDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(SharesDetail ashSharesDetail) throws InterruptedException {
		logger.debug("Entering") ;

		// if ashSharesDetail == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (ashSharesDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			ashSharesDetail = getSharesDetailService().getNewSharesDetail();

			setSharesDetail(ashSharesDetail);
		} else {
			setSharesDetail(ashSharesDetail);
		}

		try {

			// fill the components with the data
			doWriteBeanToComponents(ashSharesDetail);
			// set ReadOnly mode accordingly if the object is new or not.

			int screenCode = ScreenCTL.getMode(enqModule, isWorkFlowEnabled(), ashSharesDetail.isNewRecord());
			if (screenCode == ScreenCTL.SCRN_GNINT) {
				screenCode = ScreenCTL.SCRN_GNEDT;
			}
			displayComponents(screenCode);

			doStoreInitValues();

			this.gb_statusDetails.setVisible(false);
			this.window_SharesDetailDialog.doModal();
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

		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, isWorkFlowEnabled(),isFirstTask(), this.userAction, this.loanRefNumber,this.CompanyName));

		if (getSharesDetail().isNewRecord()){
			setComponentAccessType("SharesDetailDialog_RefNumber", false, this.loanRefNumber, this.space_LoanRefNumber, this.label_LoanRefNumber, this.hlayout_LoanRefNumber,null);
			setComponentAccessType("SharesDetailDialog_CompanyName", false, this.CompanyName, this.space_CompanyName, this.label_CompanyName, this.hlayout_CompanyName,null);
		}else{
			setComponentAccessType("SharesDetailDialog_CompanyName", true, this.CompanyName, this.space_CompanyName, this.label_CompanyName, this.hlayout_CompanyName,null);
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SharesDetailDialog_btnDelete"));
		}

		logger.debug("Leaving");
	} 

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");

		boolean tempReadOnly= readOnly;

		if(readOnly || (!readOnly && (PennantConstants.RECORD_TYPE_DEL.equals(this.sharesDetail.getRecordType())))) {
			tempReadOnly=true;
		}

		setComponentAccessType("SharesDetailDialog_RefNumber", true, this.loanRefNumber, this.space_LoanRefNumber, this.label_LoanRefNumber, this.hlayout_LoanRefNumber,null);		
		setComponentAccessType("SharesDetailDialog_CompanyName", tempReadOnly, this.CompanyName, this.space_CompanyName, this.label_CompanyName, this.hlayout_CompanyName,null);
		setComponentAccessType("SharesDetailDialog_Quantity", tempReadOnly, this.quantity, this.space_Quantity, this.label_Quantity, this.hlayout_Quantity,null);
		setComponentAccessType("SharesDetailDialog_FaceVale", tempReadOnly, this.faceValue, this.space_FaceValue, this.label_FaceValue, this.hlayout_FaceValue,null);
		setComponentAccessType("SharesDetailDialog_MarketValue", true, this.totalFaceValue, this.space_TotalFaceValue, this.label_TotalFaceValue, this.hlayout_TotalFaceValue,null);
		setComponentAccessType("SharesDetailDialog_TotalFaceVale", tempReadOnly, this.marketValue, this.space_MarketValue, this.label_MarketValue, this.hlayout_MarketValue,null);
		setComponentAccessType("SharesDetailDialog_TotalMarketValue", true, this.totalMarketValue, this.space_TotalMarketValue, this.label_TotalMarketValue, this.hlayout_TotalMarketValue,null);
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
		
		getUserWorkspace().alocateAuthorities("SharesDetailDialog");		
		if(!enqModule){
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SharesDetailDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SharesDetailDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SharesDetailDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SharesDetailDialog_btnSave"));	
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
		this.CompanyName.setMaxlength(50);
		
		this.quantity.setMaxlength(8);
		quantity.setStyle("text-align:left");
		
		this.faceValue.setMaxlength(25);
		this.faceValue.setFormat(PennantApplicationUtil.getAmountFormate(this.ccyFormatter));
		//this.faceValue.setScale(ccyFormatter);
		
		this.marketValue.setMaxlength(25);
		this.marketValue.setFormat(PennantApplicationUtil.getAmountFormate(this.ccyFormatter));
		//this.marketValue.setScale(ccyFormatter);
		
		this.totalFaceValue.setMaxlength(25);
		this.totalFaceValue.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.totalFaceValue.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.totalFaceValue.setScale(ccyFormatter);
		
		this.totalMarketValue.setMaxlength(25);
		this.totalMarketValue.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.totalMarketValue.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.totalMarketValue.setScale(ccyFormatter);
		

		setStatusDetails(gb_statusDetails,groupboxWf,south,enqModule);
		logger.debug("Leaving") ;
	}


	/**
	 * Stores the initialinitial values to member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_LoanRefNumber = this.loanRefNumber.getValue();
		this.oldVar_Company = this.CompanyName.getValue();
		this.oldVar_Quantity = this.quantity.longValue();	
		this.oldVar_FaceValue = this.faceValue.getValue();
		this.oldVar_MarketValue = this.marketValue.getValue();
		this.oldVar_TotalFaceVale = this.totalFaceValue.getValue();
		this.oldVar_TotalMarketValue = this.totalMarketValue.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.loanRefNumber.setValue(this.oldVar_LoanRefNumber);
		this.CompanyName.setValue(this.oldVar_Company);
		this.quantity.setValue(this.oldVar_Quantity);
		this.faceValue.setValue(this.oldVar_FaceValue);
		this.marketValue.setValue(this.oldVar_MarketValue);
		this.totalFaceValue.setValue(this.oldVar_TotalFaceVale);
		this.marketValue.setValue(this.oldVar_TotalMarketValue);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if(isWorkFlowEnabled() & !enqModule){	
			this.userAction.setSelectedIndex(0);	
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param ashSharesDetail
	 *            CommidityLoanDetail
	 */
	public void doWriteBeanToComponents(SharesDetail aSharesDetail) {
		logger.debug("Entering") ;
		this.loanRefNumber.setValue(aSharesDetail.getLoanRefNumber());
		this.CompanyName.setValue(aSharesDetail.getCompanyName());		
		this.quantity.setValue(aSharesDetail.getQuantity());
		this.faceValue.setValue(PennantApplicationUtil.formateAmount(aSharesDetail.getFaceValue(), 2));
		this.marketValue.setValue(PennantApplicationUtil.formateAmount(aSharesDetail.getMarketValue(), 2));
		this.totalFaceValue.setValue(PennantApplicationUtil.formateAmount(aSharesDetail.getTotalFaceValue(),ccyFormatter));
		this.faceValue.setStyle("text-align:right;");
		this.totalMarketValue.setValue(PennantApplicationUtil.formateAmount(aSharesDetail.getTotalMarketValue(), ccyFormatter));
		this.faceValue.setStyle("text-align:right;");
		this.recordStatus.setValue(aSharesDetail.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aSharesDetail.getRecordType()));
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSharesDetail
	 */
	public void doWriteComponentsToBean(SharesDetail aSharesDetail) {
		logger.debug("Entering") ;
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Loan Ref Number
		try {
			aSharesDetail.setLoanRefNumber(this.loanRefNumber.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Coampany Name
		try {
			aSharesDetail.setCompanyName(this.CompanyName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Face Vale
		try {
			if(this.faceValue.getValue()!=null){
				aSharesDetail.setFaceValue(PennantApplicationUtil.unFormateAmount(this.faceValue.getValue(), 2));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Market Vale
		try {
			if(this.marketValue.getValue()!=null){
				aSharesDetail.setMarketValue(PennantApplicationUtil.unFormateAmount(this.marketValue.getValue(), 2));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Total Face Vale
		try {
			if(this.totalFaceValue.getValue()!=null){
				aSharesDetail.setTotalFaceValue(PennantApplicationUtil.unFormateAmount(this.totalFaceValue.getValue(), ccyFormatter));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//TOtal Market Vale
		try {
			if(this.totalMarketValue.getValue()!=null){
				aSharesDetail.setTotalMarketValue(PennantApplicationUtil.unFormateAmount(this.totalMarketValue.getValue(), ccyFormatter));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Quantity
		try {
			aSharesDetail.setQuantity(this.quantity.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		aSharesDetail.setRecordStatus(this.recordStatus.getValue());
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
		if (!StringUtils.trimToEmpty(this.oldVar_Company).equals(StringUtils.trimToEmpty(this.CompanyName.getValue()))) {
			return true;
		}
		if (this.oldVar_FaceValue != this.faceValue.getValue()) {
			return true;
		}
		if (this.oldVar_Quantity != this.quantity.longValue()) {
			return  true;
		}
		if (this.oldVar_MarketValue != this.marketValue.getValue()) {
			return true;
		}
		if (this.oldVar_TotalFaceVale != this.totalFaceValue.getValue()) {
			return true;
		}
		if (this.oldVar_TotalMarketValue != this.totalMarketValue.getValue()) {
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
		
		if (!this.CompanyName.isReadonly()){
			this.CompanyName.setConstraint(new PTStringValidator(Labels.getLabel("label_SharesDetailDialog_CompanyName.value"), PennantRegularExpressions.REGEX_ALPHA, true));
		}
		//Face Vale
		if (!this.faceValue.isReadonly()){
			this.faceValue.setConstraint(new AmountValidator(18, 0, Labels.getLabel("label_SharesDetailDialog_FaceVale.value")));
		}
		//Market Vale
		if (!this.marketValue.isReadonly()){
			this.marketValue.setConstraint(new AmountValidator(18, 0, Labels.getLabel("label_SharesDetailDialog_MarketVale.value")));
		}
		//Quantity
		if (!this.quantity.isReadonly()){
			this.quantity.setConstraint(new PTNumberValidator(Labels.getLabel("label_SharesDetailDialog_Quantity.value"),true, false, 0));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.loanRefNumber.setConstraint("");
		this.CompanyName.setConstraint("");
		this.faceValue.setConstraint("");
		this.marketValue.setConstraint("");
		this.quantity.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */

	private void doClearMessage() {
		logger.debug("Entering");
		this.loanRefNumber.setErrorMessage("");
		this.CompanyName.setErrorMessage("");
		this.faceValue.setErrorMessage("");
		this.marketValue.setErrorMessage("");
		this.quantity.setErrorMessage("");
		logger.debug("Leaving");
	}
		
	public void onChange$quantity(Event event){
		logger.debug("Entering" + event.toString());
		changeVale();
		logger.debug("Leaving" + event.toString());
	}
	
	public void onChange$faceValue(Event event){
		logger.debug("Entering" + event.toString());
		changeVale();
		logger.debug("Leaving" + event.toString());
	}
	public void onChange$marketValue(Event event){
		logger.debug("Entering" + event.toString());
		doRemoveValidation();
		changeVale();
		logger.debug("Leaving" + event.toString());	
	}
	
	
	private void changeVale(){
		long enterQuantity = this.quantity.longValue();	
		BigDecimal totalFaceVale = BigDecimal.ZERO;
		BigDecimal totalMarketVale =  BigDecimal.ZERO;
		if(enterQuantity > 0){
			totalFaceVale = PennantAppUtil.unFormateAmount(this.faceValue.getValue(), ccyFormatter).multiply(new BigDecimal(enterQuantity));
			totalMarketVale = PennantAppUtil.unFormateAmount(this.marketValue.getValue(), ccyFormatter).multiply(new BigDecimal(enterQuantity));
		}
		this.totalFaceValue.setValue(PennantAppUtil.formateAmount(totalFaceVale, ccyFormatter));
		this.totalMarketValue.setValue(PennantAppUtil.formateAmount(totalMarketVale, ccyFormatter));
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
			closeDialog(this.window_SharesDetailDialog, "SharesDetailDialog");
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
		final SharesDetail ashSharesDetail = new SharesDetail();
		BeanUtils.copyProperties(getSharesDetail(), ashSharesDetail);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + ashSharesDetail.getLoanRefNumber();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(ashSharesDetail.getRecordType()).equals("")){
				ashSharesDetail.setVersion(ashSharesDetail.getVersion()+1);
				ashSharesDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					ashSharesDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					ashSharesDetail.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), ashSharesDetail.getNextTaskId(), ashSharesDetail);
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(isNewCustomer()){
					tranType=PennantConstants.TRAN_DEL;
					AuditHeader auditHeader =  newShareProcess(ashSharesDetail, tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_SharesDetailDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
						getFinSharesDetailListCtrl().doFillSharesDetails(this.sharesDetails);
						closeDialog(this.window_SharesDetailDialog, "SharesDetailDialog");
					}	
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showErrorMessage(this.window_SharesDetailDialog,e);
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
		this.CompanyName.setValue("");
		this.faceValue.setValue("");
		this.marketValue.setValue("");
		this.quantity.setText("");
		this.totalFaceValue.setText("");
		this.totalMarketValue.setText("");
		
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final SharesDetail ashSharesDetail = new SharesDetail();
		BeanUtils.copyProperties(getSharesDetail(), ashSharesDetail);
		boolean isNew = false;

		if(isWorkFlowEnabled()){
			ashSharesDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), ashSharesDetail.getNextTaskId(), ashSharesDetail);
		}

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if(!PennantConstants.RECORD_TYPE_DEL.equals(ashSharesDetail.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the CommidityLoanDetail object with the components data
			doWriteComponentsToBean(ashSharesDetail);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = ashSharesDetail.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(ashSharesDetail.getRecordType()).equals("")){
				ashSharesDetail.setVersion(ashSharesDetail.getVersion()+1);
				if(isNew){
					ashSharesDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					ashSharesDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					ashSharesDetail.setNewRecord(true);
				}
			}
		}else{

			if(isNewCustomer()){
				if(isNewRecord()){
					ashSharesDetail.setVersion(1);
					ashSharesDetail.setRecordType(PennantConstants.RCD_ADD);
				}else{
					tranType = PennantConstants.TRAN_UPD;
				}

				if(StringUtils.trimToEmpty(ashSharesDetail.getRecordType()).equals("")){
					ashSharesDetail.setVersion(ashSharesDetail.getVersion()+1);
					ashSharesDetail.setRecordType(PennantConstants.RCD_UPD);
				}

				if(ashSharesDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(ashSharesDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}
			}else{
				ashSharesDetail.setVersion(ashSharesDetail.getVersion() + 1);
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
				AuditHeader auditHeader =  newShareProcess(ashSharesDetail, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_SharesDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getFinSharesDetailListCtrl().doFillSharesDetails(this.sharesDetails);
					closeDialog(this.window_SharesDetailDialog, "SharesDetailDialog");
				}
			}

		} catch (final DataAccessException e) {
			logger.error(e);

			showErrorMessage(this.window_SharesDetailDialog,e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newShareProcess(SharesDetail ashSharesDetail, String tranType){
		boolean recordAdded=false;

		AuditHeader auditHeader= getAuditHeader(ashSharesDetail, tranType);
		this.sharesDetails = new ArrayList<SharesDetail>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(ashSharesDetail.getLoanRefNumber());
		valueParm[1] = ashSharesDetail.getCompanyName();

		errParm[0] = PennantJavaUtil.getLabel("label_LoanRefNumber") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CompanyName") + ":"+valueParm[1];

		if(getFinSharesDetailListCtrl().getSharesDetailLists() !=null && getFinSharesDetailListCtrl().getSharesDetailLists().size()>0){
			for (int i = 0; i < getFinSharesDetailListCtrl().getSharesDetailLists().size(); i++) {
				SharesDetail loanDetail = getFinSharesDetailListCtrl().getSharesDetailLists().get(i);

				if(ashSharesDetail.getLoanRefNumber().equals(loanDetail.getLoanRefNumber()) && (ashSharesDetail.getCompanyName().equals(loanDetail.getCompanyName())) ){ // Both Current and Existing list rating same

					if(isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if(tranType == PennantConstants.TRAN_DEL){
						if(ashSharesDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							ashSharesDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							sharesDetails.add(ashSharesDetail);
						}else if(ashSharesDetail.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(ashSharesDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							ashSharesDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							sharesDetails.add(ashSharesDetail);
						}else if(ashSharesDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getFinSharesDetailListCtrl().getFinancedetail().getSharesDetails().size(); j++) {
								SharesDetail income =  getFinSharesDetailListCtrl().getFinancedetail().getSharesDetails().get(j);
								if(income.getLoanRefNumber() == ashSharesDetail.getLoanRefNumber() && income.getCompanyName().equals(ashSharesDetail.getCompanyName())){
									sharesDetails.add(income);
								}
							}
						}
					}else{
						if(tranType!=PennantConstants.TRAN_UPD){
							sharesDetails.add(loanDetail);
						}
					}
				}else{
					sharesDetails.add(loanDetail);
				}
			}
		}

		if(!recordAdded){
			sharesDetails.add(ashSharesDetail);
		}
		return auditHeader;
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

	@SuppressWarnings("unused")
	private boolean doProcess(SharesDetail ashSharesDetail, String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		ashSharesDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		ashSharesDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		ashSharesDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());

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

			ashSharesDetail.setTaskId(getTaskId());
			ashSharesDetail.setNextTaskId(getNextTaskId());
			ashSharesDetail.setRoleCode(getRole());
			ashSharesDetail.setNextRoleCode(getNextRoleCode());

			if (StringUtils.trimToEmpty(getOperationRefs()).equals("")) {
				processCompleted = doSaveProcess(getAuditHeader(ashSharesDetail, tranType),null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader =  getAuditHeader(ashSharesDetail, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			processCompleted = doSaveProcess(getAuditHeader(ashSharesDetail, tranType), null);
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

		SharesDetail ashSharesDetail = (SharesDetail) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())){
						auditHeader = getSharesDetailService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getSharesDetailService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))){
						auditHeader = getSharesDetailService().doApprove(auditHeader);

						if(PennantConstants.RECORD_TYPE_DEL.equals(ashSharesDetail.getRecordType())){
							deleteNotes=true;
						}

					}else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))){
						auditHeader = getSharesDetailService().doReject(auditHeader);
						if(PennantConstants.RECORD_TYPE_NEW.equals(ashSharesDetail.getRecordType())){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_SharesDetailDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_SharesDetailDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes("CommidityLoanDetail",ashSharesDetail.getLoanRefNumber(),ashSharesDetail.getVersion()),true);
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

	private AuditHeader getAuditHeader(SharesDetail ashSharesDetail, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, ashSharesDetail.getBefImage(), ashSharesDetail);   
		return new AuditHeader(ashSharesDetail.getLoanRefNumber(),ashSharesDetail.getCompanyName(),null,null,auditDetail,ashSharesDetail.getUserDetails(),getOverideMap());
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

	public SharesDetailService getSharesDetailService() {
		return sharesDetailService;
	}

	public void setSharesDetailService(SharesDetailService sharesDetailService) {
		this.sharesDetailService = sharesDetailService;
	}

	public FinSharesDetailListCtrl getFinSharesDetailListCtrl() {
		return finSharesDetailListCtrl;
	}

	public void setFinSharesDetailListCtrl(
			FinSharesDetailListCtrl finSharesDetailListCtrl) {
		this.finSharesDetailListCtrl = finSharesDetailListCtrl;
	}

	public List<SharesDetail> getSharesDetails() {
		return sharesDetails;
	}

	public void setSharesDetails(List<SharesDetail> sharesDetails) {
		this.sharesDetails = sharesDetails;
	}

	public SharesDetail getSharesDetail() {
		return sharesDetail;
	}

	public void setSharesDetail(SharesDetail sharesDetail) {
		this.sharesDetail = sharesDetail;
	}

	public SharesDetailDialogListCtrl getSharesDetailDialogListCtrl() {
		return sharesDetailDialogListCtrl;
	}

	public void setSharesDetailDialogListCtrl(
			SharesDetailDialogListCtrl sharesDetailDialogListCtrl) {
		this.sharesDetailDialogListCtrl = sharesDetailDialogListCtrl;
	}
	

}

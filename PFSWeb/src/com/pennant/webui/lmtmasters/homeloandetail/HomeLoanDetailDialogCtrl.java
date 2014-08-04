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
 * FileName    		:  HomeLoanDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-10-2011    														*
 *                                                                  						*
 * Modified Date    :  13-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.homeloandetail;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.HomeLoanDetail;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.lmtmasters.HomeLoanDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.util.Constraint.PTPhoneNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/HomeLoanDetail/homeLoanDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class HomeLoanDetailDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 3141943554064485540L;
	private final static Logger logger = Logger.getLogger(HomeLoanDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_HomeLoanDetailDialog;// autowired
	protected Textbox 		loanRefNumber; 				// autowired
	protected Checkbox 		loanRefType; 				// autowired
	protected ExtendedCombobox homeDetails; 				// autowired
	protected Textbox 		homeBuilderName; 			// autowired
	protected Decimalbox 	homeCostPerFlat; 			// autowired
	protected Decimalbox 	homeCostOfLand; 			// autowired
	protected Decimalbox 	homeCostOfConstruction; 	// autowired
	protected Combobox 		homeConstructionStage; 		// autowired
	protected Datebox 		homeDateOfPocession; 		// autowired
	protected Decimalbox 	homeAreaOfLand; 			// autowired
	protected Decimalbox 	homeAreaOfFlat; 			// autowired
	protected ExtendedCombobox 	homePropertyType; 			// autowired
	protected ExtendedCombobox  homeOwnerShipType; 			// autowired
	protected Textbox 		homeAddrFlatNbr; 			// autowired
	protected Textbox 		homeAddrStreet; 			// autowired
	protected Textbox 		homeAddrLane1; 				// autowired
	protected Textbox 		homeAddrLane2; 				// autowired
	protected Textbox 		homeAddrPOBox; 				// autowired
	protected ExtendedCombobox 		homeAddrCountry; 			// autowired
	protected ExtendedCombobox 		homeAddrProvince; 			// autowired
	protected ExtendedCombobox 		homeAddrCity; 				// autowired
	protected Textbox 		homeAddrZIP; 				// autowired
	protected Textbox 		homeAddrPhone; 				// autowired
	protected Textbox       homeTitleDeedNo; 				// autowired
	protected Caption		caption_homeLoan;

	protected Label 		recordStatus; 				// autowired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	protected Row 			statusRow;

	// not auto wired vars
	private HomeLoanDetail homeLoanDetail; 							 // overhanded per param
	private transient HomeLoanDetailListCtrl homeLoanDetailListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last initialize.
	private transient String  		oldVar_loanRefNumber;
	private transient boolean  		oldVar_loanRefType;
	private transient long  		oldVar_homeDetails;
	private transient String  		oldVar_homeBuilderName;
	private transient BigDecimal  	oldVar_homeCostPerFlat;
	private transient BigDecimal  	oldVar_homeCostOfLand;
	private transient BigDecimal  	oldVar_homeCostOfConstruction;
	private transient int  			oldVar_homeConstructionStage;
	private transient Date  		oldVar_homeDateOfPocession;
	private transient BigDecimal  	oldVar_homeAreaOfLand;
	private transient BigDecimal  	oldVar_homeAreaOfFlat;
	private transient long  		oldVar_homePropertyType;
	private transient long  		oldVar_homeOwnerShipType;
	private transient String  		oldVar_homeAddrFlatNbr;
	private transient String  		oldVar_homeAddrStreet;
	private transient String  		oldVar_homeAddrLane1;
	private transient String  		oldVar_homeAddrLane2;
	private transient String  		oldVar_homeAddrPOBox;
	private transient String  		oldVar_homeAddrCountry;
	private transient String  		oldVar_homeAddrProvince;
	private transient String  		oldVar_homeAddrCity;
	private transient String  		oldVar_homeAddrZIP;
	private transient String  		oldVar_homeAddrPhone;
	private transient String  		oldVar_titleDeedNo;
	private transient String 		oldVar_recordStatus;
	private transient String 		oldVar_lovDescHomeDetailsName;
	private transient String 		oldVar_lovDescHomePropertyTypeName;
	private transient String 		oldVar_lovDescHomeOwnerShipTypeName;
	private transient String 		oldVar_lovDescHomeAddrCountryName;
	private transient String 		oldVar_lovDescHomeAddrProvinceName;
	private transient String 		oldVar_lovDescHomeAddrCityName;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_HomeLoanDetailDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 	// autowire
	protected Button btnEdit; 	// autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; 	// autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; 	// autowire
	protected Button btnHelp; 	// autowire
	protected Button btnNotes; 	// autowire


	// ServiceDAOs / Domain Classes
	private transient HomeLoanDetailService homeLoanDetailService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();

	//For Dynamically calling of this Controller
	private Div toolbar;
	private Object financeMainDialogCtrl;
	private Tabpanel panel = null;
	private Grid grid_basicDetails;

	private transient boolean recSave = false;
	private transient boolean   newFinance;
	public transient int   ccyFormatter = 0;
	private  String sHomeAddrCountry;
	private  String sHomeAddrProvince;

	/**
	 * default constructor.<br>
	 */
	public HomeLoanDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected HomeLoanDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_HomeLoanDetailDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		if(event.getTarget().getParent() != null){
			panel = (Tabpanel) event.getTarget().getParent();
		}

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("homeLoanDetail")) {
			this.homeLoanDetail = (HomeLoanDetail) args.get("homeLoanDetail");
			HomeLoanDetail befImage = new HomeLoanDetail();
			BeanUtils.copyProperties(this.homeLoanDetail, befImage);
			this.homeLoanDetail.setBefImage(befImage);
			setHomeLoanDetail(this.homeLoanDetail);
		} else {
			setHomeLoanDetail(null);
		}

		if(args.containsKey("financeMainDialogCtrl")){
			this.financeMainDialogCtrl = (Object) args.get("financeMainDialogCtrl");
			try {
					financeMainDialogCtrl.getClass().getMethod("setChildWindowDialogCtrl", Object.class).invoke(financeMainDialogCtrl, this);
			} catch (Exception e) {
				logger.error(e);
			}
			setNewFinance(true);
			this.homeLoanDetail.setWorkflowId(0);
			this.window_HomeLoanDetailDialog.setTitle("");
			this.caption_homeLoan.setVisible(true);
		}

		if(args.containsKey("roleCode")){
			setRole((String) args.get("roleCode"));
			getUserWorkspace().alocateRoleAuthorities(getRole(), "HomeLoanDetailDialog");
		}

		if(args.containsKey("ccyFormatter")){
			this.ccyFormatter = (Integer)args.get("ccyFormatter");
		}		

		doLoadWorkFlow(this.homeLoanDetail.isWorkflow(),
				this.homeLoanDetail.getWorkflowId(), this.homeLoanDetail.getNextTaskId());

		if (isWorkFlowEnabled() && !isNewFinance()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "HomeLoanDetailDialog");
		}
		
		/* set components visible dependent of the users rights */
		doCheckRights();

		// READ OVERHANDED params !
		// we get the homeLoanDetailListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete homeLoanDetail here.
		if (args.containsKey("homeLoanDetailListCtrl")) {
			setHomeLoanDetailListCtrl((HomeLoanDetailListCtrl) args.get("homeLoanDetailListCtrl"));
		} else {
			setHomeLoanDetailListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getHomeLoanDetail());
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	public void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.homeDetails.setInputAllowed(false);
		this.homeDetails.setDisplayStyle(3);
        this.homeDetails.setMandatoryStyle(true);
		this.homeDetails.setModuleName("LovFieldDetail");
		this.homeDetails.setValueColumn("FieldCode");
		this.homeDetails.setDescColumn("FieldCodeValue");
		this.homeDetails.setValidateColumns(new String[] { "FieldCode" });
		Filter[] homeDetailsFilters = new Filter[1];
		homeDetailsFilters[0] = new Filter("FieldCode", "PROPDETAIL", Filter.OP_EQUAL);
		this.homeDetails.setFilters(homeDetailsFilters);
		
		this.homePropertyType.setInputAllowed(false);
		this.homePropertyType.setDisplayStyle(3);
        this.homePropertyType.setMandatoryStyle(true);
		this.homePropertyType.setModuleName("LovFieldDetail");
		this.homePropertyType.setValueColumn("FieldCode");
		this.homePropertyType.setDescColumn("FieldCodeValue");
		this.homePropertyType.setValidateColumns(new String[] { "FieldCode" });
		Filter[] homePropTypeFilters = new Filter[1];
		homePropTypeFilters[0] = new Filter("FieldCode", "PROPTYPE", Filter.OP_EQUAL);
		this.homePropertyType.setFilters(homePropTypeFilters);
		
		this.homeOwnerShipType.setInputAllowed(false);
		this.homeOwnerShipType.setDisplayStyle(3);
        this.homeOwnerShipType.setMandatoryStyle(false);
		this.homeOwnerShipType.setModuleName("LovFieldDetail");
		this.homeOwnerShipType.setValueColumn("FieldCode");
		this.homeOwnerShipType.setDescColumn("FieldCodeValue");
		this.homeOwnerShipType.setValidateColumns(new String[] { "FieldCode" });
		Filter[] homeOwnerShipTypeFilters = new Filter[1];
		homeOwnerShipTypeFilters[0] = new Filter("FieldCode", "OWNERTYPE", Filter.OP_EQUAL);
		this.homeOwnerShipType.setFilters(homeOwnerShipTypeFilters);
		
		this.homeAddrCountry.setMaxlength(2);
        this.homeAddrCountry.setMandatoryStyle(false);
		this.homeAddrCountry.setModuleName("Country");
		this.homeAddrCountry.setValueColumn("CountryCode");
		this.homeAddrCountry.setDescColumn("CountryDesc");
		this.homeAddrCountry.setValidateColumns(new String[] { "CountryCode" });
		
		this.homeAddrProvince.setMaxlength(8);
        this.homeAddrProvince.setMandatoryStyle(false);
		this.homeAddrProvince.setModuleName("Province");
		this.homeAddrProvince.setValueColumn("CPProvince");
		this.homeAddrProvince.setDescColumn("CPProvinceName");
		this.homeAddrProvince.setValidateColumns(new String[] { "CPProvince" });
		
		this.homeAddrCity.setMaxlength(8);
        this.homeAddrCity.setMandatoryStyle(false);
		this.homeAddrCity.setModuleName("City");
		this.homeAddrCity.setValueColumn("PCCity");
		this.homeAddrCity.setDescColumn("PCCityName");
		this.homeAddrCity.setValidateColumns(new String[] { "PCCity" });
		
		this.homeBuilderName.setMaxlength(50);
		this.homeCostPerFlat.setMaxlength(18);
		this.homeCostPerFlat.setFormat(PennantAppUtil.getAmountFormate(this.ccyFormatter));
		this.homeCostOfLand.setMaxlength(18);
		this.homeCostOfLand.setFormat(PennantAppUtil.getAmountFormate(this.ccyFormatter));
		this.homeCostOfConstruction.setMaxlength(18);
		this.homeCostOfConstruction.setFormat(PennantAppUtil.getAmountFormate(this.ccyFormatter));
		this.homeDateOfPocession.setFormat(PennantConstants.dateFormat);
		this.homeAreaOfLand.setMaxlength(8);
		this.homeAreaOfLand.setFormat(PennantAppUtil.getAmountFormate(2));
		this.homeAreaOfLand.setScale(2);
		this.homeAreaOfFlat.setMaxlength(8);
		this.homeAreaOfFlat.setFormat(PennantAppUtil.getAmountFormate(2));
		this.homeAreaOfFlat.setScale(2);
		this.homeAddrFlatNbr.setMaxlength(20);
		this.homeAddrStreet.setMaxlength(50);
		this.homeAddrLane1.setMaxlength(50);
		this.homeAddrLane2.setMaxlength(50);
		this.homeAddrPOBox.setMaxlength(8);
		this.homeAddrZIP.setMaxlength(10);
		this.homeAddrPhone.setMaxlength(20);
		this.homeTitleDeedNo.setMaxlength(50);

		this.homeAddrProvince.setReadonly(true);
		this.homeAddrCity.setReadonly(true);
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("HomeLoanDetailDialog", getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_HomeLoanDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_HomeLoanDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_HomeLoanDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_HomeLoanDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	@SuppressWarnings("unchecked")
	public void onAssetValidation(Event event){
		logger.debug("Entering" + event.toString());

		String userAction = "";
		Map<String,Object> map = new HashMap<String,Object>();
		if(event.getData() != null){
			map = (Map<String, Object>) event.getData();
		}

		if(map.containsKey("userAction")){
			userAction = (String) map.get("userAction");
		}
		doClearMessage();
		recSave = false;
		if(("Save".equalsIgnoreCase(userAction) || "Cancel".equalsIgnoreCase(userAction))
				&& !map.containsKey("agreement")){
			recSave = true;
		}else{
			doSetValidation();	
			doSetLOVValidation();
		}
		doWriteComponentsToBean(getHomeLoanDetail());
		if(StringUtils.trimToEmpty(getHomeLoanDetail().getRecordType()).equals("")){
			getHomeLoanDetail().setVersion(getHomeLoanDetail().getVersion() + 1);
			getHomeLoanDetail().setRecordType(PennantConstants.RECORD_TYPE_NEW);
			getHomeLoanDetail().setNewRecord(true);
		}
		if (getFinanceMainDialogCtrl() != null) {
			try {
					financeMainDialogCtrl.getClass().getMethod("setHomeLoanDetail", HomeLoanDetail.class).invoke(financeMainDialogCtrl, getHomeLoanDetail());
			} catch (Exception e) {
				logger.error(e);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for checking whethter data has been changed before closing
	 * @param event
	 * @return 
	 * */
	public void onAssetClose(Event event){
		logger.debug("Entering" + event.toString());
		if (getFinanceMainDialogCtrl() != null) {
			try {
					financeMainDialogCtrl.getClass().getMethod("setAssetDataChanged", Boolean.class).invoke(financeMainDialogCtrl, isDataChanged());
			} catch (Exception e) {
				logger.error(e);
			}
		}
		logger.debug("Leaving" + event.toString());
	}


	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_HomeLoanDetailDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		// remember the old vars
		doStoreInitValues();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_HomeLoanDetailDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" + event.toString());
		doNew();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++++ GUI Process ++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++ //

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
		boolean close = true;
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("isDataChanged : false");
		}

		if (close) {
			closeDialog(this.window_HomeLoanDetailDialog, "HomeLoanDetailDialog");
		}

		logger.debug("Leaving");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aHomeLoanDetail
	 *            HomeLoanDetail
	 */
	public void doWriteBeanToComponents(HomeLoanDetail aHomeLoanDetail) {
		logger.debug("Entering");
		this.loanRefNumber.setValue(aHomeLoanDetail.getLoanRefNumber());
		this.loanRefType.setChecked(aHomeLoanDetail.isLoanRefType());
		this.homeDetails.setValue(String.valueOf(aHomeLoanDetail.getHomeDetails()));
		this.homeBuilderName.setValue(aHomeLoanDetail.getHomeBuilderName());
		this.homeCostPerFlat.setValue(PennantAppUtil.formateAmount(
				aHomeLoanDetail.getHomeCostPerFlat(), this.ccyFormatter));
		this.homeCostOfLand.setValue(PennantAppUtil.formateAmount(
				aHomeLoanDetail.getHomeCostOfLand(), this.ccyFormatter));
		this.homeCostOfConstruction.setValue(PennantAppUtil.formateAmount(
				aHomeLoanDetail.getHomeCostOfConstruction(), this.ccyFormatter));
		String constructionStage = "";
		if (StringUtils.trimToEmpty(aHomeLoanDetail.getHomeConstructionStage()).length() > 0) {
			constructionStage = StringUtils.trimToEmpty(aHomeLoanDetail.getHomeConstructionStage());
		}
		List<LovFieldDetail> constructionStageList = getHomeLoanDetailService().getHomeConstructionStage();
		fillComboBox(homeConstructionStage,constructionStage,constructionStageList);
		this.homeDateOfPocession.setValue(aHomeLoanDetail.getHomeDateOfPocession());
		this.homeAreaOfLand.setValue(PennantAppUtil.formateAmount(
				aHomeLoanDetail.getHomeAreaOfLand(), 2));
		this.homeAreaOfFlat.setValue(PennantAppUtil.formateAmount(
				aHomeLoanDetail.getHomeAreaOfFlat(), 2));
		this.homePropertyType.setValue(String.valueOf(aHomeLoanDetail.getHomePropertyType()));
		this.homeOwnerShipType.setValue(String.valueOf(aHomeLoanDetail.getHomeOwnerShipType()));
		this.homeAddrFlatNbr.setValue(aHomeLoanDetail.getHomeAddrFlatNbr());
		this.homeAddrStreet.setValue(aHomeLoanDetail.getHomeAddrStreet());
		this.homeAddrLane1.setValue(aHomeLoanDetail.getHomeAddrLane1());
		this.homeAddrLane2.setValue(aHomeLoanDetail.getHomeAddrLane2());
		this.homeAddrPOBox.setValue(aHomeLoanDetail.getHomeAddrPOBox());
		this.homeAddrCountry.setValue(aHomeLoanDetail.getHomeAddrCountry());
		this.homeAddrProvince.setValue(aHomeLoanDetail.getHomeAddrProvince());
		this.homeAddrCity.setValue(aHomeLoanDetail.getHomeAddrCity());
		this.homeAddrZIP.setValue(StringUtils.trimToEmpty(aHomeLoanDetail.getHomeAddrZIP()));
		this.homeAddrPhone.setValue(aHomeLoanDetail.getHomeAddrPhone());
		this.homeTitleDeedNo.setValue(aHomeLoanDetail.getHomeTitleDeedNo());

		if (aHomeLoanDetail.isNewRecord()) {
			this.homeDetails.setDescription("");
			this.homePropertyType.setDescription("");
			this.homeOwnerShipType.setDescription("");
			this.homeAddrCountry.setDescription("");
			this.homeAddrProvince.setDescription("");
			this.homeAddrCity.setDescription("");
		} else {
			this.homeDetails.setDescription(aHomeLoanDetail.getLovDescHomeDetailsName());
			this.homePropertyType.setDescription(aHomeLoanDetail.getLovDescHomePropertyTypeName());
			this.homeOwnerShipType.setDescription(aHomeLoanDetail.getLovDescHomeOwnerShipTypeName());
			this.homeAddrCountry.setDescription(aHomeLoanDetail.getLovDescHomeAddrCountryName());
			this.homeAddrProvince.setDescription(aHomeLoanDetail.getLovDescHomeAddrProvinceName());
			this.homeAddrCity.setDescription(aHomeLoanDetail.getLovDescHomeAddrCityName());
		}
		sHomeAddrCountry = this.homeAddrCountry.getValue();
		sHomeAddrProvince = this.homeAddrProvince.getValue();
		this.recordStatus.setValue(aHomeLoanDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aHomeLoanDetail
	 */
	public void doWriteComponentsToBean(HomeLoanDetail aHomeLoanDetail) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aHomeLoanDetail.setLoanRefNumber(this.loanRefNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aHomeLoanDetail.setLoanRefType(this.loanRefType.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aHomeLoanDetail.setLovDescHomeDetailsName(this.homeDetails.getDescription());
			if(this.homeDetails.getValue() != null) {
				aHomeLoanDetail.setHomeDetails(Long.valueOf(this.homeDetails.getValue())); //FIXME After Demo
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aHomeLoanDetail.setHomeBuilderName(this.homeBuilderName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.homeCostPerFlat.getValue() != null) {
				aHomeLoanDetail.setHomeCostPerFlat(PennantAppUtil
						.unFormateAmount(this.homeCostPerFlat.getValue(), this.ccyFormatter));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.homeCostOfLand.getValue() != null) {
				aHomeLoanDetail.setHomeCostOfLand(PennantAppUtil
						.unFormateAmount(this.homeCostOfLand.getValue(), this.ccyFormatter));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.homeCostOfConstruction.getValue() != null) {
				aHomeLoanDetail.setHomeCostOfConstruction(PennantAppUtil
						.unFormateAmount(this.homeCostOfConstruction.getValue(), this.ccyFormatter));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			/*if(!recSave && !this.homeConstructionStage.isDisabled() 
					&& this.homeConstructionStage.getSelectedItem().getValue().equals("#")){
				throw new WrongValueException(homeConstructionStage, Labels.getLabel("STATIC_INVALID",					//FIXME After Demo
						new String[]{Labels.getLabel("label_HomeLoanDetailDialog_HomeConstructionStage.value")}));
			}*/
			aHomeLoanDetail.setHomeConstructionStage(this.homeConstructionStage
					.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aHomeLoanDetail.setHomeDateOfPocession(this.homeDateOfPocession.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.homeAreaOfLand.getValue() != null) {
				aHomeLoanDetail.setHomeAreaOfLand(PennantAppUtil
						.unFormateAmount(this.homeAreaOfLand.getValue(), 2));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.homeAreaOfFlat.getValue() != null) {
				aHomeLoanDetail.setHomeAreaOfFlat(PennantAppUtil
						.unFormateAmount(this.homeAreaOfFlat.getValue(), 2));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aHomeLoanDetail.setLovDescHomePropertyTypeName(this.homePropertyType.getDescription());
			aHomeLoanDetail.setHomePropertyType(Long.valueOf(this.homePropertyType.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aHomeLoanDetail.setLovDescHomeOwnerShipTypeName(this.homeOwnerShipType.getDescription());
			aHomeLoanDetail.setHomeOwnerShipType(Long.valueOf(this.homeOwnerShipType.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aHomeLoanDetail.setHomeAddrFlatNbr(this.homeAddrFlatNbr.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aHomeLoanDetail.setHomeAddrStreet(this.homeAddrStreet.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aHomeLoanDetail.setHomeAddrLane1(this.homeAddrLane1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aHomeLoanDetail.setHomeAddrLane2(this.homeAddrLane2.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aHomeLoanDetail.setHomeAddrPOBox(this.homeAddrPOBox.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aHomeLoanDetail.setLovDescHomeAddrCountryName(this.homeAddrCountry.getDescription());
			aHomeLoanDetail.setHomeAddrCountry(this.homeAddrCountry.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aHomeLoanDetail.setLovDescHomeAddrProvinceName(this.homeAddrProvince.getDescription());
			aHomeLoanDetail.setHomeAddrProvince(this.homeAddrProvince.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aHomeLoanDetail.setLovDescHomeAddrCityName(this.homeAddrCity.getDescription());
			aHomeLoanDetail.setHomeAddrCity(this.homeAddrCity.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aHomeLoanDetail.setHomeAddrZIP(this.homeAddrZIP.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aHomeLoanDetail.setHomeAddrPhone(this.homeAddrPhone.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aHomeLoanDetail.setHomeTitleDeedNo(this.homeTitleDeedNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if(!recSave){
			if (wve.size() > 0) {
				WrongValueException[] wvea = new WrongValueException[wve.size()];
				for (int i = 0; i < wve.size(); i++) {
					wvea[i] = (WrongValueException) wve.get(i);
				}
				if(panel != null){
					((Tab)panel.getParent().getParent().getFellowIfAny("loanAssetTab")).setSelected(true);
				}
				throw new WrongValuesException(wvea);
			}
		}

		aHomeLoanDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aHomeLoanDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(HomeLoanDetail aHomeLoanDetail) throws InterruptedException {
		logger.debug("Entering");

		// if aHomeLoanDetail == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aHomeLoanDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aHomeLoanDetail = getHomeLoanDetailService().getNewHomeLoanDetail();
			setHomeLoanDetail(aHomeLoanDetail);
		} else {
			setHomeLoanDetail(aHomeLoanDetail);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aHomeLoanDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit(); 
			// setFocus
			this.homeDetails.focus();
		} else {
			this.homeDetails.focus();
			if(isNewFinance()){
				doEdit(); 
			}else if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aHomeLoanDetail);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			if(panel != null){
				this.toolbar.setVisible(false);
				this.groupboxWf.setVisible(false);
				this.statusRow.setVisible(false);
				this.window_HomeLoanDetailDialog.setHeight(grid_basicDetails.getRows().getVisibleItemCount()*20+100+"px");
				//panel.setHeight(grid_basicDetails.getRows().getVisibleItemCount()*20+180+"px");
				panel.appendChild(this.window_HomeLoanDetailDialog);
			}else{
				setDialog(this.window_HomeLoanDetailDialog);
			}
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_loanRefNumber = this.loanRefNumber.getValue();
		this.oldVar_loanRefType = this.loanRefType.isChecked();
		this.oldVar_homeDetails = Long.valueOf(this.homeDetails.getValue());
		this.oldVar_lovDescHomeDetailsName = this.homeDetails.getDescription();
		this.oldVar_homeBuilderName = this.homeBuilderName.getValue();
		this.oldVar_homeCostPerFlat = this.homeCostPerFlat.getValue();
		this.oldVar_homeCostOfLand = this.homeCostOfLand.getValue();
		this.oldVar_homeCostOfConstruction = this.homeCostOfConstruction.getValue();
		this.oldVar_homeConstructionStage = this.homeConstructionStage.getSelectedIndex();
		this.oldVar_homeDateOfPocession = this.homeDateOfPocession.getValue();
		this.oldVar_homeAreaOfLand = this.homeAreaOfLand.getValue();
		this.oldVar_homeAreaOfFlat = this.homeAreaOfFlat.getValue();
		this.oldVar_homePropertyType = Long.valueOf(this.homePropertyType.getValue());
		this.oldVar_lovDescHomePropertyTypeName = this.homePropertyType.getDescription();
		this.oldVar_homeOwnerShipType = Long.valueOf(this.homeOwnerShipType.getValue());
		this.oldVar_lovDescHomeOwnerShipTypeName = this.homeOwnerShipType.getDescription();
		this.oldVar_homeAddrFlatNbr = this.homeAddrFlatNbr.getValue();
		this.oldVar_homeAddrStreet = this.homeAddrStreet.getValue();
		this.oldVar_homeAddrLane1 = this.homeAddrLane1.getValue();
		this.oldVar_homeAddrLane2 = this.homeAddrLane2.getValue();
		this.oldVar_homeAddrPOBox = this.homeAddrPOBox.getValue();
		this.oldVar_homeAddrCountry = this.homeAddrCountry.getValue();
		this.oldVar_lovDescHomeAddrCountryName = this.homeAddrCountry.getDescription();
		this.oldVar_homeAddrProvince = this.homeAddrProvince.getValue();
		this.oldVar_lovDescHomeAddrProvinceName = this.homeAddrProvince.getDescription();
		this.oldVar_homeAddrCity = this.homeAddrCity.getValue();
		this.oldVar_lovDescHomeAddrCityName = this.homeAddrCity.getDescription();
		this.oldVar_homeAddrZIP = this.homeAddrZIP.getValue();
		this.oldVar_homeAddrPhone = this.homeAddrPhone.getValue();
		this.oldVar_titleDeedNo = this.homeTitleDeedNo.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.loanRefNumber.setValue(this.oldVar_loanRefNumber);
		this.loanRefType.setChecked(this.oldVar_loanRefType);
		this.homeDetails.setValue(String.valueOf(this.oldVar_homeDetails));
		this.homeDetails.setDescription(this.oldVar_lovDescHomeDetailsName);
		this.homeBuilderName.setValue(this.oldVar_homeBuilderName);
		this.homeCostPerFlat.setValue(this.oldVar_homeCostPerFlat);
		this.homeCostOfLand.setValue(this.oldVar_homeCostOfLand);
		this.homeCostOfConstruction.setValue(this.oldVar_homeCostOfConstruction);
		this.homeConstructionStage.setSelectedIndex(this.oldVar_homeConstructionStage);
		for (int i = 0; i < homeConstructionStage.getItemCount(); i++) {
			if (homeConstructionStage.getItemAtIndex(i).getValue().toString()
					.equals(this.oldVar_homeConstructionStage)) {
				this.homeConstructionStage.setSelectedIndex(i);
				break;
			}
			this.homeConstructionStage.setSelectedIndex(0);
		}
		this.homeDateOfPocession.setValue(this.oldVar_homeDateOfPocession);
		this.homeAreaOfLand.setValue(this.oldVar_homeAreaOfLand);
		this.homeAreaOfFlat.setValue(this.oldVar_homeAreaOfFlat);
		this.homePropertyType.setValue(String.valueOf(this.oldVar_homePropertyType));
		this.homePropertyType.setDescription(this.oldVar_lovDescHomePropertyTypeName);
		this.homeOwnerShipType.setValue(String.valueOf(this.oldVar_homeOwnerShipType));
		this.homeOwnerShipType.setDescription(this.oldVar_lovDescHomeOwnerShipTypeName);
		this.homeAddrFlatNbr.setValue(this.oldVar_homeAddrFlatNbr);
		this.homeAddrStreet.setValue(this.oldVar_homeAddrStreet);
		this.homeAddrLane1.setValue(this.oldVar_homeAddrLane1);
		this.homeAddrLane2.setValue(this.oldVar_homeAddrLane2);
		this.homeAddrPOBox.setValue(this.oldVar_homeAddrPOBox);
		this.homeAddrCountry.setValue(this.oldVar_homeAddrCountry);
		this.homeAddrCountry.setDescription(this.oldVar_lovDescHomeAddrCountryName);
		this.homeAddrProvince.setValue(this.oldVar_homeAddrProvince);
		this.homeAddrProvince.setDescription(this.oldVar_lovDescHomeAddrProvinceName);
		this.homeAddrCity.setValue(this.oldVar_homeAddrCity);
		this.homeAddrCity.setDescription(this.oldVar_lovDescHomeAddrCityName);
		this.homeAddrZIP.setValue(this.oldVar_homeAddrZIP);
		this.homeAddrPhone.setValue(this.oldVar_homeAddrPhone);
		this.homeTitleDeedNo.setValue(this.oldVar_titleDeedNo);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {

		// To clear the Error Messages
		doClearMessage();

		if (this.oldVar_loanRefNumber != this.loanRefNumber.getValue()) {
			return true;
		}
		if (this.oldVar_loanRefType != this.loanRefType.isChecked()) {
			return true;
		}
		if (this.oldVar_homeDetails != Long.valueOf(this.homeDetails.getValue())) {
			return true;
		}
		if (this.oldVar_homeConstructionStage != this.homeConstructionStage.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_homeBuilderName != this.homeBuilderName.getValue()) {
			return true;
		}
		if (this.oldVar_homeCostPerFlat != this.homeCostPerFlat.getValue()) {
			return true;
		}
		if (this.oldVar_homeCostOfLand != this.homeCostOfLand.getValue()) {
			return true;
		}
		if (this.oldVar_homeCostOfConstruction != this.homeCostOfConstruction.getValue()) {
			return true;
		}
		String old_homeDateOfPocession = "";
		String new_homeDateOfPocession = "";
		if (this.oldVar_homeDateOfPocession != null) {
			old_homeDateOfPocession = DateUtility.formatDate(
					this.oldVar_homeDateOfPocession,PennantConstants.dateFormat);
		}
		if (this.homeDateOfPocession.getValue() != null) {
			new_homeDateOfPocession = DateUtility.formatDate(
					this.homeDateOfPocession.getValue(), PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_homeDateOfPocession).equals(
				StringUtils.trimToEmpty(new_homeDateOfPocession))) {
			return true;
		}
		if (this.oldVar_homeAreaOfLand != this.homeAreaOfLand.getValue()) {
			return true;
		}
		if (this.oldVar_homeAreaOfFlat != this.homeAreaOfFlat.getValue()) {
			return true;
		}
		if (this.oldVar_homePropertyType != Long.valueOf(this.homePropertyType.getValue())) {
			return true;
		}
		if (this.oldVar_homeOwnerShipType != Long.valueOf(this.homeOwnerShipType.getValue())) {
			return true;
		}
		if (this.oldVar_homeAddrFlatNbr != this.homeAddrFlatNbr.getValue()) {
			return true;
		}
		if (this.oldVar_homeAddrStreet != this.homeAddrStreet.getValue()) {
			return true;
		}
		if (this.oldVar_homeAddrLane1 != this.homeAddrLane1.getValue()) {
			return true;
		}
		if (this.oldVar_homeAddrLane2 != this.homeAddrLane2.getValue()) {
			return true;
		}
		if (this.oldVar_homeAddrPOBox != this.homeAddrPOBox.getValue()) {
			return true;
		}
		if (this.oldVar_homeAddrCountry != this.homeAddrCountry.getValue()) {
			return true;
		}
		if (this.oldVar_homeAddrProvince != this.homeAddrProvince.getValue()) {
			return true;
		}
		if (this.oldVar_homeAddrCity != this.homeAddrCity.getValue()) {
			return true;
		}
		if (this.oldVar_homeAddrZIP != this.homeAddrZIP.getValue()) {
			return true;
		}
		if (this.oldVar_homeAddrPhone != this.homeAddrPhone.getValue()) {
			return true;
		}
		if (this.oldVar_titleDeedNo != this.homeTitleDeedNo.getValue()) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.homeBuilderName.isReadonly()) {
			this.homeBuilderName.setConstraint(new PTStringValidator(Labels.getLabel("label_HomeLoanDetailDialog_HomeBuilderName.value"), 
					PennantRegularExpressions.REGEX_NAME, false));
		}
		/*if (!this.homeCostPerFlat.isReadonly()) {
			this.homeCostPerFlat.setConstraint(new AmountValidator(
					18,0,Labels.getLabel("label_HomeLoanDetailDialog_HomeCostPerFlat.value"),false));
		}
		if (!this.homeCostOfLand.isReadonly()) {
			this.homeCostOfLand.setConstraint(new AmountValidator(
					18,0,Labels.getLabel("label_HomeLoanDetailDialog_HomeCostOfLand.value"),false));
		}
		if (!this.homeCostOfConstruction.isReadonly()) {
			this.homeCostOfConstruction.setConstraint(new AmountValidator(
					18,0,Labels.getLabel("label_HomeLoanDetailDialog_HomeCostOfConstruction.value"),false));
		}
		if (this.homeConstructionStage.isReadonly()) {
			this.homeConstructionStage.setConstraint("NO EMPTY:"+Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_HomeLoanDetailDialog_HomeConstructionStage.value") }));
		}
		if (!this.homeDateOfPocession.isDisabled()) {
			this.homeDateOfPocession.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_HomeLoanDetailDialog_HomeDateOfPocession.value") }));
		}*/
		if (!this.homeAreaOfLand.isReadonly()) {
			this.homeAreaOfLand.setConstraint(new AmountValidator(
					8,2,Labels.getLabel("label_HomeLoanDetailDialog_HomeAreaOfLand.value")));
		}
		/*if (!this.homeAreaOfFlat.isReadonly()) {
			this.homeAreaOfFlat.setConstraint(new AmountValidator(
					8,2,Labels.getLabel("label_HomeLoanDetailDialog_HomeAreaOfFlat.value")));
		}*/
		if (!this.homeAddrFlatNbr.isReadonly()) {
			this.homeAddrFlatNbr.setConstraint(new PTStringValidator(Labels.getLabel("label_HomeLoanDetailDialog_HomeAddrFlatNbr.value"),
					PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.homeAddrStreet.isReadonly()) {
			this.homeAddrStreet.setConstraint(new PTStringValidator(Labels.getLabel("label_HomeLoanDetailDialog_HomeAddrStreet.value"),PennantRegularExpressions.REGEX_ADDRESS, false));
			
		}
		if (!this.homeAddrPOBox.isReadonly()) {
			this.homeAddrPOBox.setConstraint(new PTStringValidator(Labels.getLabel("label_HomeLoanDetailDialog_HomeAddrPOBox.value"),
					PennantRegularExpressions.REGEX_NUMERIC, false));
		}
		if (!this.homeAddrZIP.isReadonly()) {
			this.homeAddrZIP.setConstraint(new PTStringValidator(Labels.getLabel("label_HomeLoanDetailDialog_HomeAddrZIP.value"), PennantRegularExpressions.REGEX_ZIP, false));
		}
		if (!this.homeAddrPhone.isReadonly()) {
			this.homeAddrPhone.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_HomeLoanDetailDialog_HomeAddrPhone.value"),false));
		}
		if (!this.homeTitleDeedNo.isReadonly()) {
			this.homeTitleDeedNo.setConstraint(new PTStringValidator(Labels.getLabel("label_HomeLoanDetailDialog_HomeTitleDeedNo.value"), PennantRegularExpressions.REGEX_ALPHANUM, false));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.loanRefNumber.setConstraint("");
		this.homeBuilderName.setConstraint("");
		this.homeCostPerFlat.setConstraint("");
		this.homeCostOfLand.setConstraint("");
		this.homeCostOfConstruction.setConstraint("");
		this.homeConstructionStage.setConstraint("");
		this.homeDateOfPocession.setConstraint("");
		this.homeAreaOfLand.setConstraint("");
		this.homeAreaOfFlat.setConstraint("");
		this.homeAddrFlatNbr.setConstraint("");
		this.homeAddrStreet.setConstraint("");
		this.homeAddrLane1.setConstraint("");
		this.homeAddrLane2.setConstraint("");
		this.homeAddrPOBox.setConstraint("");
		this.homeAddrZIP.setConstraint("");
		this.homeAddrPhone.setConstraint("");
		this.homeTitleDeedNo.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Set the constraints to LOV fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");

		this.homeDetails.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_HomeLoanDetailDialog_HomeDetails.value") }));

		this.homePropertyType.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_HomeLoanDetailDialog_HomePropertyType.value") }));

		/*this.homeOwnerShipType.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_HomeLoanDetailDialog_HomeOwnerShipType.value") }));*/

		/*this.homeAddrCountry.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_HomeLoanDetailDialog_HomeAddrCountry.value") }));

		this.homeAddrProvince.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_HomeLoanDetailDialog_HomeAddrProvince.value") }));

		this.homeAddrCity.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_HomeLoanDetailDialog_HomeAddrCity.value") }));*/

		logger.debug("Leaving");
	}

	/**
	 * Method for remove constraints to LOV fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.homeDetails.setConstraint("");
		this.homePropertyType.setConstraint("");
		this.homeOwnerShipType.setConstraint("");
		this.homeAddrCountry.setConstraint("");
		this.homeAddrProvince.setConstraint("");
		this.homeAddrCity.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method for clear the error Messages 
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.loanRefNumber.setErrorMessage("");
		this.homeDetails.setErrorMessage("");
		this.homeBuilderName.setErrorMessage("");
		this.homeCostPerFlat.setErrorMessage("");
		this.homeCostOfLand.setErrorMessage("");
		this.homeCostOfConstruction.setErrorMessage("");
		this.homeConstructionStage.setErrorMessage("");
		this.homeDateOfPocession.setErrorMessage("");
		this.homeAreaOfLand.setErrorMessage("");
		this.homeAreaOfFlat.setErrorMessage("");
		this.homePropertyType.setErrorMessage("");
		this.homeOwnerShipType.setErrorMessage("");
		this.homeAddrFlatNbr.setErrorMessage("");
		this.homeAddrStreet.setErrorMessage("");
		this.homeAddrLane1.setErrorMessage("");
		this.homeAddrLane2.setErrorMessage("");
		this.homeAddrPOBox.setErrorMessage("");
		this.homeAddrCountry.setErrorMessage("");
		this.homeAddrProvince.setErrorMessage("");
		this.homeAddrCity.setErrorMessage("");
		this.homeAddrZIP.setErrorMessage("");
		this.homeAddrPhone.setErrorMessage("");
		this.homeTitleDeedNo.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void refreshList() {
		logger.debug("Entering");
		final JdbcSearchObject<HomeLoanDetail> soHomeLoanDetail = getHomeLoanDetailListCtrl()
		.getSearchObj();
		getHomeLoanDetailListCtrl().pagingHomeLoanDetailList.setActivePage(0);
		getHomeLoanDetailListCtrl().getPagedListWrapper().setSearchObject(soHomeLoanDetail);
		if (getHomeLoanDetailListCtrl().listBoxHomeLoanDetail != null) {
			getHomeLoanDetailListCtrl().listBoxHomeLoanDetail.getListModel();
		}
		logger.debug("Leaving");
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a HomeLoanDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final HomeLoanDetail aHomeLoanDetail = new HomeLoanDetail();
		BeanUtils.copyProperties(getHomeLoanDetail(), aHomeLoanDetail);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")
		+ "\n\n --> " + aHomeLoanDetail.getId();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aHomeLoanDetail.getRecordType()).equals("")) {
				aHomeLoanDetail.setVersion(aHomeLoanDetail.getVersion() + 1);
				aHomeLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aHomeLoanDetail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aHomeLoanDetail, tranType)) {
					refreshList();
					closeDialog(this.window_HomeLoanDetailDialog, "HomeLoanDetailDialog");
				}
			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new HomeLoanDetail object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old vars
		doStoreInitValues();
		final HomeLoanDetail aHomeLoanDetail = getHomeLoanDetailService().getNewHomeLoanDetail();
		setHomeLoanDetail(aHomeLoanDetail);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.homeDetails.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getHomeLoanDetail().isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		this.loanRefNumber.setReadonly(true);
		this.loanRefType.setDisabled(isReadOnly("HomeLoanDetailDialog_loanRefType"));
		this.homeDetails.setReadonly(isReadOnly("HomeLoanDetailDialog_homeDetails"));
		this.homeBuilderName.setReadonly(isReadOnly("HomeLoanDetailDialog_homeBuilderName"));
		this.homeCostPerFlat.setReadonly(isReadOnly("HomeLoanDetailDialog_homeCostPerFlat"));
		this.homeCostOfLand.setReadonly(isReadOnly("HomeLoanDetailDialog_homeCostOfLand"));
		this.homeCostOfConstruction.setReadonly(isReadOnly("HomeLoanDetailDialog_homeCostOfConstruction"));
		this.homeConstructionStage.setDisabled(isReadOnly("HomeLoanDetailDialog_homeConstructionStage"));
		this.homeDateOfPocession.setDisabled(isReadOnly("HomeLoanDetailDialog_homeDateOfPocession"));
		this.homeAreaOfLand.setReadonly(isReadOnly("HomeLoanDetailDialog_homeAreaOfLand"));
		this.homeAreaOfFlat.setReadonly(isReadOnly("HomeLoanDetailDialog_homeAreaOfFlat"));
		this.homePropertyType.setReadonly(isReadOnly("HomeLoanDetailDialog_homePropertyType"));
		this.homeOwnerShipType.setReadonly(isReadOnly("HomeLoanDetailDialog_homeOwnerShipType"));
		this.homeAddrFlatNbr.setReadonly(isReadOnly("HomeLoanDetailDialog_homeAddrFlatNbr"));
		this.homeAddrStreet.setReadonly(isReadOnly("HomeLoanDetailDialog_homeAddrStreet"));
		this.homeAddrLane1.setReadonly(isReadOnly("HomeLoanDetailDialog_homeAddrLane1"));
		this.homeAddrLane2.setReadonly(isReadOnly("HomeLoanDetailDialog_homeAddrLane2"));
		this.homeAddrPOBox.setReadonly(isReadOnly("HomeLoanDetailDialog_homeAddrPOBox"));
		this.homeAddrCountry.setReadonly(isReadOnly("HomeLoanDetailDialog_homeAddrCountry"));
		this.homeAddrZIP.setReadonly(isReadOnly("HomeLoanDetailDialog_homeAddrZIP"));
		this.homeAddrPhone.setReadonly(isReadOnly("HomeLoanDetailDialog_homeAddrPhone"));
		this.homeTitleDeedNo.setReadonly(isReadOnly("HomeLoanDetailDialog_titleDeedNo"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.homeLoanDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName){
		if (isWorkFlowEnabled() || isNewFinance()){
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.loanRefNumber.setReadonly(true);
		this.loanRefType.setDisabled(true);
		this.homeDetails.setReadonly(true);
		this.homeBuilderName.setReadonly(true);
		this.homeCostPerFlat.setReadonly(true);
		this.homeCostOfLand.setReadonly(true);
		this.homeCostOfConstruction.setReadonly(true);
		this.homeConstructionStage.setDisabled(true);
		this.homeDateOfPocession.setDisabled(true);
		this.homeAreaOfLand.setReadonly(true);
		this.homeAreaOfFlat.setReadonly(true);
		this.homePropertyType.setReadonly(true);
		this.homeOwnerShipType.setReadonly(true);
		this.homeAddrFlatNbr.setReadonly(true);
		this.homeAddrStreet.setReadonly(true);
		this.homeAddrLane1.setReadonly(true);
		this.homeAddrLane2.setReadonly(true);
		this.homeAddrPOBox.setReadonly(true);
		this.homeAddrCountry.setReadonly(true);
		this.homeAddrProvince.setReadonly(true);
		this.homeAddrCity.setReadonly(true);
		this.homeAddrZIP.setReadonly(true);
		this.homeAddrPhone.setReadonly(true);
		this.homeTitleDeedNo.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.loanRefNumber.setText("");
		this.loanRefType.setChecked(false);
		this.homeDetails.setValue(String.valueOf(new Long(0)));
		this.homeDetails.setDescription("");
		this.homeBuilderName.setValue("");
		this.homeCostPerFlat.setValue("");
		this.homeCostOfLand.setValue("");
		this.homeCostOfConstruction.setValue("");
		this.homeConstructionStage.setText("");
		this.homeConstructionStage.setSelectedIndex(0);
		this.homeDateOfPocession.setText("");
		this.homeAreaOfLand.setValue("");
		this.homeAreaOfFlat.setValue("");
		this.homePropertyType.setValue(String.valueOf(new Long(0)));
		this.homePropertyType.setDescription("");
		this.homeOwnerShipType.setValue(String.valueOf(new Long(0)));
		this.homeOwnerShipType.setDescription("");
		this.homeAddrFlatNbr.setValue("");
		this.homeAddrStreet.setValue("");
		this.homeAddrLane1.setValue("");
		this.homeAddrLane2.setValue("");
		this.homeAddrPOBox.setValue("");
		this.homeAddrCountry.setValue("");
		this.homeAddrCountry.setDescription("");
		this.homeAddrProvince.setValue("");
		this.homeAddrProvince.setDescription("");
		this.homeAddrCity.setValue("");
		this.homeAddrCity.setDescription("");
		this.homeAddrZIP.setValue("");
		this.homeAddrPhone.setValue("");
		this.homeTitleDeedNo.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final HomeLoanDetail aHomeLoanDetail = new HomeLoanDetail();
		BeanUtils.copyProperties(getHomeLoanDetail(), aHomeLoanDetail);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		doSetLOVValidation();
		// fill the HomeLoanDetail object with the components data
		doWriteComponentsToBean(aHomeLoanDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aHomeLoanDetail.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aHomeLoanDetail.getRecordType()).equals("")) {
				aHomeLoanDetail.setVersion(aHomeLoanDetail.getVersion() + 1);
				if (isNew) {
					aHomeLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aHomeLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aHomeLoanDetail.setNewRecord(true);
				}
			}
		} else {
			aHomeLoanDetail.setVersion(aHomeLoanDetail.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aHomeLoanDetail, tranType)) {
				refreshList();
				closeDialog(this.window_HomeLoanDetailDialog, "HomeLoanDetailDialog");
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aHomeLoanDetail (HomeLoanDetail)
	 * @param tranType (String)
	 * 
	 * @return boolean
	 */
	private boolean doProcess(HomeLoanDetail aHomeLoanDetail, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aHomeLoanDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aHomeLoanDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aHomeLoanDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String
			aHomeLoanDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aHomeLoanDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId,aHomeLoanDetail);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow()
						.getAuditingReq(taskId, aHomeLoanDetail))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
			}

			if (StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode = getWorkFlow().firstTask.owner;
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode + ",";
						}
						nextRoleCode = getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aHomeLoanDetail.setTaskId(taskId);
			aHomeLoanDetail.setNextTaskId(nextTaskId);
			aHomeLoanDetail.setRoleCode(getRole());
			aHomeLoanDetail.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aHomeLoanDetail, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId, aHomeLoanDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aHomeLoanDetail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aHomeLoanDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * @param method (String)
	 * 
	 * @return boolean
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		HomeLoanDetail aHomeLoanDetail = (HomeLoanDetail) auditHeader.getAuditDetail().getModelData();

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getHomeLoanDetailService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getHomeLoanDetailService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getHomeLoanDetailService().doApprove(auditHeader);

						if (aHomeLoanDetail.getRecordType().equals(
								PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getHomeLoanDetailService().doReject(auditHeader);
						if (aHomeLoanDetail.getRecordType().equals(
								PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(
								this.window_HomeLoanDetailDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(
						this.window_HomeLoanDetailDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
			setOverideMap(auditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++ Search Button Component Events+++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void onFulfill$homeDetails(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = this.homeDetails.getObject();
		if (dataObject instanceof String) {
			this.homeDetails.setValue(String.valueOf(new Long(0)));
		} else {
			LovFieldDetail details = (LovFieldDetail) dataObject;
			if (details != null) {
				this.homeDetails.setValue(String.valueOf(details.getFieldCodeId()));
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/*public void onClick$btnSearchHomeConstructionStage(Event event) {
		logger.debug("Entering" + event.toString());

		Filter[] filters = new Filter[1];
		filters[0] = new Filter("FieldCode", "CONSTSTAGE", Filter.OP_EQUAL);
		Object dataObject = ExtendedSearchListBox.show(
				this.window_HomeLoanDetailDialog, "LovFieldDetail", filters);

		if (dataObject instanceof String) {
			this.homeConstructionStage.setText("");
			this.lovDescHomeConstructionStageName.setValue("");
		} else {
			LovFieldDetail details = (LovFieldDetail) dataObject;
			if (details != null) {
				this.homeConstructionStage.setValue(details.getFieldCodeId());
				this.lovDescHomeConstructionStageName.setValue(details
						.getFieldCodeId() + "-" + details.getFieldCodeValue());
			}
		}
		logger.debug("Leaving" + event.toString());
	}*/

	public void onFulfill$homePropertyType(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = this.homePropertyType.getObject();

		if (dataObject instanceof String) {
			this.homePropertyType.setValue(dataObject.toString());
		} else {
			LovFieldDetail details = (LovFieldDetail) dataObject;
			if (details != null) {
				this.homePropertyType.setValue(String.valueOf(details.getFieldCodeId()));
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$homeOwnerShipType(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = this.homeOwnerShipType.getObject();
		if (dataObject instanceof String) {
			this.homeOwnerShipType.setValue(String.valueOf(new Long(0)));
		} else {
			LovFieldDetail details = (LovFieldDetail) dataObject;
			if (details != null) {
				this.homeOwnerShipType.setValue(String.valueOf(details.getFieldCodeId()));
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$homeAddrCountry(Event event) {
		logger.debug("Entering" + event.toString());
		if (!StringUtils.trimToEmpty(sHomeAddrCountry).equals(
				this.homeAddrCountry.getValue())) {
			this.homeAddrProvince.setValue("");
			this.homeAddrProvince.setDescription("");
			this.homeAddrCity.setValue("");
			this.homeAddrCity.setDescription("");
			this.homeAddrCity.setReadonly(true);	
			this.homeAddrCity.setMandatoryStyle(false);	
		}
		if(!StringUtils.trimToEmpty(this.homeAddrCountry.getValue()).equals("")){
			this.homeAddrProvince.setReadonly(false);
			this.homeAddrProvince.setMandatoryStyle(true);
		}else{
			this.homeAddrProvince.setReadonly(true);
			this.homeAddrCity.setReadonly(true);
			this.homeAddrProvince.setMandatoryStyle(false);
			this.homeAddrCity.setMandatoryStyle(false);
			
		}
		sHomeAddrCountry = this.homeAddrCountry.getValue();
		Filter[] provinceFilters = new Filter[1];
		provinceFilters[0] = new Filter("CPCountry", this.homeAddrCountry.getValue(), Filter.OP_EQUAL);
		this.homeAddrProvince.setFilters(provinceFilters);
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$homeAddrProvince(Event event) {
		logger.debug("Entering" + event.toString());
		if (!StringUtils.trimToEmpty(sHomeAddrProvince).equals(this.homeAddrProvince.getValue())) {
			this.homeAddrCity.setValue("");
			this.homeAddrCity.setDescription("");
		}
		if(!StringUtils.trimToEmpty(this.homeAddrProvince.getValue()).equals("")){
			this.homeAddrCity.setReadonly(false);
			this.homeAddrCity.setMandatoryStyle(true);
		}else{
			this.homeAddrCity.setReadonly(true);		   
			this.homeAddrCity.setMandatoryStyle(false);		   
		}
		sHomeAddrProvince = this.homeAddrProvince.getValue();
		Filter[] cityFilters = new Filter[1];
		cityFilters[0] = new Filter("PCProvince", this.homeAddrProvince.getValue(),
				Filter.OP_EQUAL);
		this.homeAddrCity.setFilters(cityFilters);

		logger.debug("Leaving" + event.toString());
	}


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get Audit Header Details
	 * 
	 * @param aHomeLoanDetail
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(HomeLoanDetail aHomeLoanDetail,
			String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aHomeLoanDetail.getBefImage(), aHomeLoanDetail);
		return new AuditHeader(String.valueOf(aHomeLoanDetail.getId()),
				null, null, null, auditDetail,
				aHomeLoanDetail.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_HomeLoanDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
		logger.debug("Leaving");
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
		logger.debug("Entering" + event.toString());

		final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
		logger.debug("Leaving");
	}	

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("HomeLoanDetail");
		notes.setReference(getHomeLoanDetail().getId());
		notes.setVersion(getHomeLoanDetail().getVersion());
		return notes;
	}

	@SuppressWarnings("rawtypes")
	private void fillComboBox(Combobox combobox, String value, List list) {
		logger.debug("Entering fillComboBox()");
		combobox.getChildren().clear();
		String combolabel = "";
		String combovalue = "";
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof LovFieldDetail) {
				LovFieldDetail valueLabel = (LovFieldDetail) list.get(i);
				combolabel = StringUtils.trim(valueLabel.getFieldCodeValue());
				combovalue = StringUtils.trim(valueLabel.getFieldCodeValue());
			}
			comboitem = new Comboitem();
			comboitem.setValue(combovalue);
			comboitem.setLabel(combolabel);
			combobox.appendChild(comboitem);
			if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(combovalue))) {
				combobox.setSelectedItem(comboitem);
			}
		}
		logger.debug("Leaving fillComboBox()");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}
	public boolean isValidationOn() {
		return this.validationOn;
	}

	public HomeLoanDetail getHomeLoanDetail() {
		return this.homeLoanDetail;
	}
	public void setHomeLoanDetail(HomeLoanDetail homeLoanDetail) {
		this.homeLoanDetail = homeLoanDetail;
	}

	public void setHomeLoanDetailService(
			HomeLoanDetailService homeLoanDetailService) {
		this.homeLoanDetailService = homeLoanDetailService;
	}
	public HomeLoanDetailService getHomeLoanDetailService() {
		return this.homeLoanDetailService;
	}

	public void setHomeLoanDetailListCtrl(
			HomeLoanDetailListCtrl homeLoanDetailListCtrl) {
		this.homeLoanDetailListCtrl = homeLoanDetailListCtrl;
	}
	public HomeLoanDetailListCtrl getHomeLoanDetailListCtrl() {
		return this.homeLoanDetailListCtrl;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	public void setOverideMap(
			HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}
	public boolean isNewFinance() {
		return newFinance;
	}
	public void setNewFinance(boolean newFinance) {
		this.newFinance = newFinance;
	}
	
	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}
	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

}

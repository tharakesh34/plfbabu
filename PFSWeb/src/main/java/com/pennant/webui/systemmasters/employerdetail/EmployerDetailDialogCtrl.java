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
 * FileName    		:  EmployerDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-07-2013    														*
 *                                                                  						*
 * Modified Date    :  31-07-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-07-2013       Pennant	                 0.1                                            * 
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
package com.pennant.webui.systemmasters.employerdetail;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.EmployerDetail;
import com.pennant.backend.service.systemmasters.EmployerDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTEmailValidator;
import com.pennant.util.Constraint.PTMobileNumberValidator;
import com.pennant.util.Constraint.PTPhoneNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.PTWebValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.ScreenCTL;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/EmployerDetail/employerDetailDialog.zul file.
 */
public class EmployerDetailDialogCtrl extends GFCBaseCtrl<EmployerDetail> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(EmployerDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_EmployerDetailDialog; 
	protected Row 			row0; 
	protected Label 		label_EmpIndustry;
	protected Hlayout 		hlayout_EmpIndustry;
	protected Space 		space_EmpIndustry; 

	protected ExtendedCombobox 		empIndustry; 
	protected Label 		label_EmpName;
	protected Hlayout 		hlayout_EmpName;
	protected Space 		space_EmpName; 

	protected Textbox 		empName; 
	protected Row 			row1; 
	protected Label 		label_EstablishDate;
	protected Hlayout 		hlayout_EstablishDate;
	protected Space 		space_EstablishDate; 

	protected Datebox 		establishDate; 
	protected Label 		label_EmpAddrHNbr;
	protected Hlayout 		hlayout_EmpAddrHNbr;
	protected Space 		space_EmpAddrHNbr; 

	protected Textbox 		empAddrHNbr; 
	protected Row 			row2; 
	protected Label 		label_EmpFlatNbr;
	protected Hlayout 		hlayout_EmpFlatNbr;
	protected Space 		space_EmpFlatNbr; 

	protected Textbox 		empFlatNbr; 
	protected Label 		label_EmpAddrStreet;
	protected Hlayout 		hlayout_EmpAddrStreet;
	protected Space 		space_EmpAddrStreet; 

	protected Textbox 		empAddrStreet; 
	protected Row 			row3; 
	protected Label 		label_EmpAddrLine1;
	protected Hlayout 		hlayout_EmpAddrLine1;
	protected Space 		space_EmpAddrLine1; 

	protected Textbox 		empAddrLine1; 
	protected Label 		label_EmpAddrLine2;
	protected Hlayout 		hlayout_EmpAddrLine2;
	protected Space 		space_EmpAddrLine2; 

	protected Textbox 		empAddrLine2; 
	protected Row 			row4; 
	protected Label 		label_EmpPOBox;
	protected Hlayout 		hlayout_EmpPOBox;
	protected Space 		space_EmpPOBox; 

	protected Textbox 		empPOBox; 
	protected Label 		label_EmpCountry;
	protected Hlayout 		hlayout_EmpCountry;
	protected Space 		space_EmpCountry; 

	protected ExtendedCombobox 		empCountry; 
	protected Row 			row5; 
	protected Label 		label_EmpProvince;
	protected Hlayout 		hlayout_EmpProvince;
	protected Space 		space_EmpProvince; 

	protected ExtendedCombobox 		empProvince; 
	protected Label 		label_EmpCity;
	protected Hlayout 		    hlayout_EmpCity;
	protected Space 		space_EmpCity; 

	protected ExtendedCombobox 		empCity; 
	protected Row 			row6; 
	protected Label 		label_EmpPhone;
	protected Hlayout 		hlayout_EmpPhone;
	protected Space 		space_EmpPhone; 

	protected Textbox 		empPhone; 
	//protected Textbox 		phoneCountryCode; 						
	//protected Textbox 		phoneAreaCode; 	
	
	protected Label 		label_EmpFax;
	protected Hlayout 		hlayout_EmpFax;
	protected Space 		space_EmpFax; 

	protected Textbox 		empFax; 
	protected Textbox       empFaxCountryCode;
	protected Textbox		empFaxAreaCode;

	
	protected Row 			row7; 
	protected Label 		label_EmpTelexNo;
	protected Hlayout 		hlayout_EmpTelexNo;
	protected Space 		space_EmpTelexNo; 

	protected Textbox 		empTelexNo; 
	protected Textbox       empTelexCountryCode;
	protected Textbox		empTelexAreaCode;
	
	protected Label 		label_EmpEmailId;
	protected Hlayout 		hlayout_EmpEmailId;
	protected Space 		space_EmpEmailId; 

	protected Textbox 		empEmailId; 
	protected Row 			row8; 
	protected Label 		label_EmpWebSite;
	protected Hlayout 		hlayout_EmpWebSite;
	protected Space 		space_EmpWebSite; 

	protected Textbox 		empWebSite; 
	protected Label 		label_ContactPersonName;
	protected Hlayout 		hlayout_ContactPersonName;
	protected Space 		space_ContactPersonName; 

	protected Textbox 		contactPersonName; 
	protected Row 			row9; 
	protected Label 		label_ContactPersonNo;
	protected Hlayout 		hlayout_ContactPersonNo;
	protected Space 		space_ContactPersonNo; 

	protected Textbox 		contactPersonNo; 
	//protected Textbox 		cpPhoneCountryCode; 						
	//protected Textbox 		cpPhoneAreaCode; 	
	
	protected Label 		label_EmpAlocationType;
	protected Hlayout 		hlayout_EmpAlocationType;
	protected Space 		space_EmpAlocationType; 
    protected Combobox 		empAlocationType; 
    
    protected Textbox       cityName;
    protected Row 			row10; 
	protected Label 		label_BankRefNo;
	protected Hlayout 		hlayout_BankRefNo;
	
    protected Textbox       bankRefNo;
	
	private boolean 		enqModule=false;
	protected Checkbox      empIsActive; 			// autoWired
	// not auto wired vars
	private EmployerDetail employerDetail; // overhanded per param
	private transient EmployerDetailListCtrl employerDetailListCtrl; // overhanded per param

	private transient String   sEmpCountry;
	private transient String sEmpProvince;
	// ServiceDAOs / Domain Classes
	private transient EmployerDetailService employerDetailService;

	/**
	 * default constructor.<br>
	 */
	public EmployerDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "EmployerDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected EmployerDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_EmployerDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_EmployerDetailDialog);

		try {
			if (PennantConstants.CITY_FREETEXT) {
				this.empCity.setVisible(false);
				this.cityName.setVisible(true);
			} else {
				this.empCity.setVisible(true);
				this.cityName.setVisible(false);
			}

			if (arguments.containsKey("enqModule")) {
				enqModule=(Boolean) arguments.get("enqModule");
			}else{
				enqModule=false;
			}

			if (arguments.containsKey("employerDetail")) {
				this.employerDetail = (EmployerDetail) arguments.get("employerDetail");
				EmployerDetail befImage =new EmployerDetail();
				BeanUtils.copyProperties(this.employerDetail, befImage);
				this.employerDetail.setBefImage(befImage);

				setEmployerDetail(this.employerDetail);
			} else {
				setEmployerDetail(null);
			}
			doLoadWorkFlow(this.employerDetail.isWorkflow(),this.employerDetail.getWorkflowId(),this.employerDetail.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule){
				this.userAction	= setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "EmployerDetailDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the employerDetailListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete employerDetail here.
			if (arguments.containsKey("employerDetailListCtrl")) {
				setEmployerDetailListCtrl((EmployerDetailListCtrl) arguments.get("employerDetailListCtrl"));
			} else {
				setEmployerDetailListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getEmployerDetail());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_EmployerDetailDialog.onClose();
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
		displayComponents(ScreenCTL.SCRN_GNEDT);
      this.btnCancel.setVisible(true);	
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
		doWriteBeanToComponents(this.employerDetail.getBefImage());
		displayComponents(ScreenCTL.SCRN_GNINT);
		this.btnCancel.setVisible(false);
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
		MessageUtil.showHelpWindow(event, window_EmployerDetailDialog);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
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
			ScreenCTL.displayNotes(getNotes("EmployerDetail",
					String.valueOf(getEmployerDetail().getEmployerId()),
					getEmployerDetail().getVersion()),this);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" +event.toString());

	}

	public void onFulfill$empCountry(Event event){
		logger.debug("Entering" + event.toString());
		doSetProvProp();
		doSetCityProp();
		logger.debug("Leaving" + event.toString());
	}
	
	public void onFulfill$empProvince(Event event){
		logger.debug("Entering" + event.toString());
		doSetCityProp();
		logger.debug("Leaving" + event.toString());
	}
	private void doSetProvProp(){
		if (!StringUtils.trimToEmpty(sEmpCountry).equals(this.empCountry.getValue())){
			this.empProvince.setObject("");
			this.empProvince.setValue("");
			this.empProvince.setDescription("");
			this.empCity.setObject("");
			this.empCity.setValue("");
			this.empCity.setDescription("");
		}
		sEmpCountry = this.empCountry.getValue();
		Filter[] filtersProvince = new Filter[1] ;
		filtersProvince[0]= new Filter("CPCountry", this.empCountry.getValue(), Filter.OP_EQUAL);
		this.empProvince.setFilters(filtersProvince);
	}
	
	private void doSetCityProp(){
		if (!StringUtils.trimToEmpty(sEmpProvince).equals(this.empProvince.getValue())){
			this.empCity.setObject("");
			this.empCity.setValue("");
			this.empCity.setDescription("");   
		}
		sEmpProvince= this.empProvince.getValue();
		Filter[] filtersCity = new Filter[2] ;
		filtersCity[0] = new Filter("PCCountry", this.empCountry.getValue(),Filter.OP_EQUAL);
		filtersCity[1]= new Filter("PCProvince", this.empProvince.getValue(), Filter.OP_EQUAL);
		this.empCity.setFilters(filtersCity);
	}
	
	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aEmployerDetail
	 * @throws Exception
	 */
	public void doShowDialog(EmployerDetail aEmployerDetail) throws Exception {
		logger.debug("Entering");

		try {

			// fill the components with the data
			doWriteBeanToComponents(aEmployerDetail);
			// set ReadOnly mode accordingly if the object is new or not.

			displayComponents(ScreenCTL.getMode(enqModule,isWorkFlowEnabled(),aEmployerDetail.isNewRecord()));

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_EmployerDetailDialog.onClose();
		} catch (Exception e) {
			throw e;
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

		System.out.println();
		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, 
				isWorkFlowEnabled(),isFirstTask(), this.userAction,this.empIndustry, this.empAlocationType));

		if (getEmployerDetail().isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.empIndustry.setMandatoryStyle(true);
			this.empCountry.setMandatoryStyle(true);
			this.empCity.setMandatoryStyle(false);
			this.empProvince.setMandatoryStyle(true);
		}

		logger.debug("Leaving");
	} 

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");

		boolean tempReadOnly= readOnly;

		if(readOnly){ 
			tempReadOnly=true;
		}else if (PennantConstants.RECORD_TYPE_DEL.equals(employerDetail.getRecordType())) {
				tempReadOnly=true;	
		}
		
		setComponentAccessType("EmployerDetailDialog_EmpIndustry", tempReadOnly, this.empIndustry, this.space_EmpIndustry, this.label_EmpIndustry, this.hlayout_EmpIndustry,null);
		setComponentAccessType("EmployerDetailDialog_EmpName", tempReadOnly, this.empName, this.space_EmpName, this.label_EmpName, this.hlayout_EmpName,null);
		setRowInvisible(this.row0, this.hlayout_EmpIndustry,this.hlayout_EmpName);
		setComponentAccessType("EmployerDetailDialog_EstablishDate", tempReadOnly, this.establishDate, this.space_EstablishDate, this.label_EstablishDate, this.hlayout_EstablishDate,null);
		setComponentAccessType("EmployerDetailDialog_EmpAddrHNbr", tempReadOnly, this.empAddrHNbr, this.space_EmpAddrHNbr, this.label_EmpAddrHNbr, this.hlayout_EmpAddrHNbr,null);
		setRowInvisible(this.row1, this.hlayout_EstablishDate,this.hlayout_EmpAddrHNbr);
		setComponentAccessType("EmployerDetailDialog_EmpFlatNbr", tempReadOnly, this.empFlatNbr, this.space_EmpFlatNbr, this.label_EmpFlatNbr, this.hlayout_EmpFlatNbr,null);
		setComponentAccessType("EmployerDetailDialog_EmpAddrStreet", tempReadOnly, this.empAddrStreet, this.space_EmpAddrStreet, this.label_EmpAddrStreet, this.hlayout_EmpAddrStreet,null);
		setRowInvisible(this.row2, this.hlayout_EmpFlatNbr,this.hlayout_EmpAddrStreet);
		setComponentAccessType("EmployerDetailDialog_EmpAddrLine1", tempReadOnly, this.empAddrLine1, this.space_EmpAddrLine1, this.label_EmpAddrLine1, this.hlayout_EmpAddrLine1,null);
		setComponentAccessType("EmployerDetailDialog_EmpAddrLine2", tempReadOnly, this.empAddrLine2, this.space_EmpAddrLine2, this.label_EmpAddrLine2, this.hlayout_EmpAddrLine2,null);
		setRowInvisible(this.row3, this.hlayout_EmpAddrLine1,this.hlayout_EmpAddrLine2);
		setComponentAccessType("EmployerDetailDialog_EmpPOBox", tempReadOnly, this.empPOBox, this.space_EmpPOBox, this.label_EmpPOBox, this.hlayout_EmpPOBox,null);
		setComponentAccessType("EmployerDetailDialog_EmpCountry", tempReadOnly, this.empCountry, this.space_EmpCountry, this.label_EmpCountry, this.hlayout_EmpCountry,null);
		setRowInvisible(this.row4, this.hlayout_EmpPOBox,this.hlayout_EmpCountry);
		setComponentAccessType("EmployerDetailDialog_EmpProvince", tempReadOnly, this.empProvince, this.space_EmpProvince, this.label_EmpProvince, this.hlayout_EmpProvince,null);
		setComponentAccessType("EmployerDetailDialog_EmpCity", tempReadOnly, this.empCity, this.space_EmpCity, this.label_EmpCity, this.hlayout_EmpCity,null);
		setComponentAccessType("EmployerDetailDialog_EmpCity", tempReadOnly, this.cityName, this.space_EmpCity, this.label_EmpCity, this.hlayout_EmpCity,null);
		setRowInvisible(this.row5, this.hlayout_EmpProvince,this.hlayout_EmpCity);
		setComponentAccessType("EmployerDetailDialog_EmpPhone", tempReadOnly, this.empPhone, this.space_EmpPhone, this.label_EmpPhone, this.hlayout_EmpPhone,null);
		setComponentAccessType("EmployerDetailDialog_EmpFax", tempReadOnly, this.empFax, this.space_EmpFax, this.label_EmpFax, this.hlayout_EmpFax,null);
		setComponentAccessType("EmployerDetailDialog_EmpFax", tempReadOnly, this.empFaxAreaCode, this.space_EmpFax, this.label_EmpFax, this.hlayout_EmpFax,null);
		setComponentAccessType("EmployerDetailDialog_EmpFax", tempReadOnly, this.empFaxCountryCode, this.space_EmpFax, this.label_EmpFax, this.hlayout_EmpFax,null);
		setRowInvisible(this.row6, this.hlayout_EmpPhone,this.hlayout_EmpFax);
		setComponentAccessType("EmployerDetailDialog_EmpTelexNo", tempReadOnly, this.empTelexCountryCode, this.space_EmpTelexNo, this.label_EmpTelexNo, this.hlayout_EmpTelexNo,null);
		setComponentAccessType("EmployerDetailDialog_EmpTelexNo", tempReadOnly, this.empTelexAreaCode, this.space_EmpTelexNo, this.label_EmpTelexNo, this.hlayout_EmpTelexNo,null);
		setComponentAccessType("EmployerDetailDialog_EmpTelexNo", tempReadOnly, this.empTelexNo, this.space_EmpTelexNo, this.label_EmpTelexNo, this.hlayout_EmpTelexNo,null);
		setComponentAccessType("EmployerDetailDialog_EmpEmailId", tempReadOnly, this.empEmailId, this.space_EmpEmailId, this.label_EmpEmailId, this.hlayout_EmpEmailId,null);
		setRowInvisible(this.row7, this.hlayout_EmpTelexNo,this.hlayout_EmpEmailId);
		setComponentAccessType("EmployerDetailDialog_EmpWebSite", tempReadOnly, this.empWebSite, this.space_EmpWebSite, this.label_EmpWebSite, this.hlayout_EmpWebSite,null);
		setComponentAccessType("EmployerDetailDialog_ContactPersonName", tempReadOnly, this.contactPersonName, this.space_ContactPersonName, this.label_ContactPersonName, this.hlayout_ContactPersonName,null);
		setRowInvisible(this.row8, this.hlayout_EmpWebSite,this.hlayout_ContactPersonName);
		setComponentAccessType("EmployerDetailDialog_ContactPersonNo", tempReadOnly, this.contactPersonNo, this.space_ContactPersonNo, this.label_ContactPersonNo, this.hlayout_ContactPersonNo,null);
		setComponentAccessType("EmployerDetailDialog_EmpAlocationType", tempReadOnly, this.empAlocationType, this.space_EmpAlocationType, this.label_EmpAlocationType, this.hlayout_EmpAlocationType,null);
		setRowInvisible(this.row9, this.hlayout_ContactPersonNo,this.hlayout_EmpAlocationType);
		setComponentAccessType("EmployerDetailDialog_BankRefNo", tempReadOnly, this.bankRefNo, null, this.label_BankRefNo, this.hlayout_BankRefNo,null);
		setRowInvisible(this.row10, this.hlayout_BankRefNo,null);
		logger.debug("Leaving");
	}

	// Helpers

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

		if(!enqModule){
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_EmployerDetailDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_EmployerDetailDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_EmployerDetailDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_EmployerDetailDialog_btnSave"));	
			this.empIsActive.setDisabled(isReadOnly("EmployerDetailDialog_empIsActive"));
		}

		logger.debug("Leaving") ;
	}
	
	
	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.empIndustry.setMaxlength(8);
		this.empName.setMaxlength(50);
		this.establishDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.empAddrHNbr.setMaxlength(20);
		this.empFlatNbr.setMaxlength(20);
		this.empAddrStreet.setMaxlength(20);
		this.empAddrLine1.setMaxlength(20);
		this.empAddrLine2.setMaxlength(20);
		this.empPOBox.setMaxlength(8);
		this.empCountry.setMaxlength(2);
		this.empProvince.setMaxlength(8);
		this.empCity.setMaxlength(8);
		this.cityName.setMaxlength(50);
		this.empEmailId.setMaxlength(100);
		this.empWebSite.setMaxlength(100);
		this.contactPersonName.setMaxlength(20);
		this.contactPersonNo.setMaxlength(10);
		this.empFax.setMaxlength(8);
		this.empFaxAreaCode.setMaxlength(4);
		this.empFaxCountryCode.setMaxlength(4);
		this.empPhone.setMaxlength(10);
		this.empTelexAreaCode.setMaxlength(3);
		this.empTelexCountryCode.setMaxlength(3);
		this.empTelexNo.setMaxlength(8);
		this.bankRefNo.setMaxlength(20);
		this.empIndustry.setMandatoryStyle(true);
		this.empIndustry.setModuleName("Industry");
		this.empIndustry.setValueColumn("IndustryCode");
		this.empIndustry.setDescColumn("IndustryDesc");
		this.empIndustry.setValidateColumns(new String[]{"IndustryCode"});
		
		this.empCountry.setMandatoryStyle(true);
		this.empCountry.setModuleName("Country");
		this.empCountry.setValueColumn("CountryCode");
		this.empCountry.setDescColumn("CountryDesc");
		this.empCountry.setValidateColumns(new String[]{"CountryCode"});
		
		this.empProvince.setMandatoryStyle(true);
		this.empProvince.setModuleName("Province");
		this.empProvince.setValueColumn("CPProvince");
		this.empProvince.setDescColumn("CPProvinceName");
		this.empProvince.setValidateColumns(new String[] { "CPProvince" });
		
		this.empCity.setMandatoryStyle(false);
		this.empCity.setModuleName("City");
		this.empCity.setValueColumn("PCCity");
		this.empCity.setDescColumn("PCCityName");
		this.empCity.setValidateColumns(new String[] { "PCCity" });
		
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aEmployerDetail
	 *            EmployerDetail
	 */
	public void doWriteBeanToComponents(EmployerDetail aEmployerDetail) {
		logger.debug("Entering") ;
		this.empIndustry.setValue(aEmployerDetail.getEmpIndustry());
		this.empName.setValue(aEmployerDetail.getEmpName());
		this.establishDate.setValue(aEmployerDetail.getEstablishDate());
		this.empAddrHNbr.setValue(aEmployerDetail.getEmpAddrHNbr());
		this.empFlatNbr.setValue(aEmployerDetail.getEmpFlatNbr());
		this.empAddrStreet.setValue(aEmployerDetail.getEmpAddrStreet());
		this.empAddrLine1.setValue(aEmployerDetail.getEmpAddrLine1());
		this.empAddrLine2.setValue(aEmployerDetail.getEmpAddrLine2());
		this.empPOBox.setValue(aEmployerDetail.getEmpPOBox());
		this.empCountry.setValue(aEmployerDetail.getEmpCountry());
		this.empProvince.setValue(aEmployerDetail.getEmpProvince());
		this.empCity.setValue(aEmployerDetail.getEmpCity());
		this.cityName.setValue(aEmployerDetail.getEmpCity());
		this.empPhone.setValue(aEmployerDetail.getEmpPhone());
		String[]fax = PennantApplicationUtil.unFormatPhoneNumber(aEmployerDetail.getEmpFax());
		this.empFaxCountryCode.setValue(fax[0]);
		this.empFaxAreaCode.setValue(fax[1]);
		this.empFax.setValue(fax[2]);
		String[]telexNo=PennantApplicationUtil.unFormatPhoneNumber(aEmployerDetail.getEmpTelexNo());
		this.empTelexCountryCode.setValue(telexNo[0]);
		this.empTelexAreaCode.setValue(telexNo[1]);
		this.empTelexNo.setValue(telexNo[2]);
		this.empEmailId.setValue(aEmployerDetail.getEmpEmailId());
		this.empWebSite.setValue(aEmployerDetail.getEmpWebSite());
		this.contactPersonName.setValue(aEmployerDetail.getContactPersonName());
		this.contactPersonNo.setValue(aEmployerDetail.getContactPersonNo());
		fillComboBox(this.empAlocationType, aEmployerDetail.getEmpAlocationType(), PennantStaticListUtil.getEmpAlocList(),"");
		this.bankRefNo.setValue(aEmployerDetail.getBankRefNo());
		this.empIsActive.setChecked(aEmployerDetail.isEmpIsActive());
		
		if (aEmployerDetail.isNewRecord()){
			this.empIndustry.setDescription("");
			this.empCountry.setDescription("");
			this.empProvince.setDescription("");
			this.empCity.setDescription("");
		}else{
			this.empIndustry.setDescription(aEmployerDetail.getLovDescIndustryDesc());
			this.empCountry.setDescription(aEmployerDetail.getLovDescCountryDesc());
			this.empProvince.setDescription(aEmployerDetail.getLovDescProvinceName());
			this.empCity.setDescription(aEmployerDetail.getLovDescCityName());
		}
		if(aEmployerDetail.isNew() || (aEmployerDetail.getRecordType() != null ? aEmployerDetail.getRecordType() : "").equals(PennantConstants.RECORD_TYPE_NEW)){
			this.empIsActive.setChecked(true);
			this.empIsActive.setDisabled(true);
		}
		this.recordStatus.setValue(aEmployerDetail.getRecordStatus());
		sEmpCountry = this.empCountry.getValue();
		sEmpProvince = this.empProvince.getValue();
		doSetCityProp();
		doSetProvProp();
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aEmployerDetail
	 */
	public void doWriteComponentsToBean(EmployerDetail aEmployerDetail) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Emp Industry
		try {
			aEmployerDetail.setLovDescIndustryDesc(this.empIndustry.getDescription());
			aEmployerDetail.setEmpIndustry(this.empIndustry.getValidatedValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Name
		try {
			aEmployerDetail.setEmpName(this.empName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Establish Date
		try {
			aEmployerDetail.setEstablishDate(this.establishDate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Addr H Nbr
		try {
			aEmployerDetail.setEmpAddrHNbr(this.empAddrHNbr.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Flat Nbr
		try {
			aEmployerDetail.setEmpFlatNbr(this.empFlatNbr.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Addr Street
		try {
			aEmployerDetail.setEmpAddrStreet(this.empAddrStreet.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Addr Line1
		try {
			aEmployerDetail.setEmpAddrLine1(this.empAddrLine1.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Addr Line2
		try {
			aEmployerDetail.setEmpAddrLine2(this.empAddrLine2.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp P O Box
		try {
			aEmployerDetail.setEmpPOBox(this.empPOBox.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Country
		try {
			aEmployerDetail.setLovDescCountryDesc(this.empCountry.getDescription());
			aEmployerDetail.setEmpCountry(this.empCountry.getValidatedValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Province
		try {
			aEmployerDetail.setLovDescProvinceName(this.empProvince.getDescription());
			aEmployerDetail.setEmpProvince(this.empProvince.getValidatedValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp City
		try {
			
			if (PennantConstants.CITY_FREETEXT) {
				aEmployerDetail.setEmpCity(StringUtils.trimToNull(this.cityName
						.getValue()));
			} else {
				aEmployerDetail.setLovDescCityName(StringUtils
						.trimToNull(this.empCity.getDescription()));
				aEmployerDetail.setEmpCity(StringUtils.trimToNull(this.empCity
						.getValidatedValue()));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Phone
		try {
			aEmployerDetail.setEmpPhone(this.empPhone.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Fax
		try {
			aEmployerDetail.setEmpFax(PennantApplicationUtil.formatPhoneNumber(this.empFaxCountryCode.getValue(),
					this.empFaxAreaCode.getValue(),this.empFax.getValue()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Telex No
		try {
			aEmployerDetail.setEmpTelexNo(PennantApplicationUtil.formatPhoneNumber(this.empTelexCountryCode.getValue(),
					this.empTelexAreaCode.getValue(),this.empTelexNo.getValue()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Email Id
		try {
			aEmployerDetail.setEmpEmailId(this.empEmailId.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Web Site
		try {
			aEmployerDetail.setEmpWebSite(this.empWebSite.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Contact Person Name
		try {
			aEmployerDetail.setContactPersonName(this.contactPersonName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Contact Person No
		try {
			aEmployerDetail.setContactPersonNo(this.contactPersonNo.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Emp Alocation Type
		try {
			String strEmpAlocationType =null; 
			if(this.empAlocationType.getSelectedItem()!=null){
				strEmpAlocationType = this.empAlocationType.getSelectedItem().getValue().toString();
			}
			if(strEmpAlocationType!= null && !PennantConstants.List_Select.equals(strEmpAlocationType)){
				aEmployerDetail.setEmpAlocationType(strEmpAlocationType);	
			}else{
				aEmployerDetail.setEmpAlocationType(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Bank Reference Number
		try {
			aEmployerDetail.setBankRefNo(this.bankRefNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aEmployerDetail.setEmpIsActive(this.empIsActive.isChecked());
		} catch (WrongValueException we) {
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

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		Date appStartDate = DateUtility.getAppDate();
		Date startDate = SysParamUtil.getValueAsDate("APP_DFT_START_DATE");
		//Emp Name
		if (!this.empName.isReadonly()){
			this.empName.setConstraint(new PTStringValidator(Labels.getLabel("label_EmployerDetailDialog_EmpName.value"), 
					PennantRegularExpressions.REGEX_UPPERCASENAME, true));
		}
		//Establish Date
		if (!this.establishDate.isReadonly()) {
			this.establishDate.setConstraint(new PTDateValidator(Labels.getLabel("label_EmployerDetailDialog_EstablishDate.value"), true, startDate, appStartDate, false));
		}
		boolean addressConstraint = false;
		if (StringUtils.isBlank(this.empAddrHNbr.getValue())
				&& StringUtils.isBlank(this.empFlatNbr.getValue())
				&& StringUtils.isBlank(this.empAddrStreet.getValue())
				&& StringUtils.isBlank(this.empAddrLine1.getValue())
				&& StringUtils.isBlank(this.empAddrLine2.getValue())) {
			addressConstraint = true;
		}
		//Emp Addr H Nbr
		if (addressConstraint) {
			this.empAddrHNbr.setConstraint(new PTStringValidator(Labels.getLabel("label_EmployerDetailDialog_EmpAddrHNbr.value"), PennantRegularExpressions.REGEX_ADDRESS, true));
		}
		//Emp Flat Nbr
		if (addressConstraint) {
			this.empFlatNbr.setConstraint(new PTStringValidator(Labels.getLabel("label_EmployerDetailDialog_EmpFlatNbr.value"), PennantRegularExpressions.REGEX_ADDRESS, true));
		}
		//Emp Addr Street
		if (addressConstraint) {
			this.empAddrStreet.setConstraint(new PTStringValidator(Labels.getLabel("label_EmployerDetailDialog_EmpAddrStreet.value"), PennantRegularExpressions.REGEX_ADDRESS, true));
		}
		//Emp Addr Line1
		if (addressConstraint) {
			this.empAddrLine1.setConstraint(new PTStringValidator(Labels.getLabel("label_EmployerDetailDialog_EmpAddrLine1.value"), PennantRegularExpressions.REGEX_ADDRESS, true));
		}
		//Emp Addr Line2
		if (addressConstraint) {
			this.empAddrLine2.setConstraint(new PTStringValidator(Labels.getLabel("label_EmployerDetailDialog_EmpAddrLine2.value"), PennantRegularExpressions.REGEX_ADDRESS, true));
		}
		//Emp P O Box
		if (!this.empPOBox.isReadonly()) {
			this.empPOBox.setConstraint(new PTStringValidator(Labels.getLabel("label_EmployerDetailDialog_EmpPOBox.value"),
					PennantRegularExpressions.REGEX_NUMERIC, true));
		}
		//Emp Phone
		if (!this.empPhone.isReadonly()) {
			this.empPhone.setConstraint(new PTMobileNumberValidator(Labels.getLabel("label_EmployerDetailDialog_EmpPhone.value"), true));
		}
		if(!this.empFaxCountryCode.isReadonly()){
			this.empFaxCountryCode.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_EmployerDetailDialog_faxCountryCode.value"),true,1));
		}
		if(!this.empFaxAreaCode.isReadonly()){
			this.empFaxAreaCode.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_EmployerDetailDialog_faxAreaCode.value"),true,2));
		}
		//Emp Fax
		if (!this.empFax.isReadonly()) {
			this.empFax.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_EmployerDetailDialog_EmpFax.value"),true,3));
		}
		if(!this.empTelexCountryCode.isReadonly()){
			this.empTelexCountryCode.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_EmployerDetailDialog_telexCountryCode.value"),true,1));
		}
		if(!this.empTelexAreaCode.isReadonly()){
			this.empTelexAreaCode.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_EmployerDetailDialog_telexAreaCode.value"),true,2));
		}
		//Emp Telex No
		if (!this.empTelexNo.isReadonly()) {
			this.empTelexNo.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_EmployerDetailDialog_EmpTelexNo.value"),true,3));
		}
		//Emp Email Id MAND_FIELD_MAIL
		if (!this.empEmailId.isReadonly()){
			this.empEmailId.setConstraint(new PTEmailValidator(Labels.getLabel("label_EmployerDetailDialog_EmpEmailId.value"),true));
		}
		//Emp Web Site
		if (!this.empWebSite.isReadonly()){
			this.empWebSite.setConstraint(new PTWebValidator(Labels.getLabel("label_EmployerDetailDialog_EmpWebSite.value"), true));
		}
		//Contact Person Name
		if (!this.contactPersonName.isReadonly()){
			this.contactPersonName.setConstraint(new PTStringValidator(Labels.getLabel("label_EmployerDetailDialog_ContactPersonName.value"), 
					PennantRegularExpressions.REGEX_NAME, true));
		}
		//Contact Person No
		if (!this.contactPersonNo.isReadonly()) {
			this.contactPersonNo.setConstraint(new PTMobileNumberValidator(Labels.getLabel("label_EmployerDetailDialog_ContactPersonNo.value"), true));
		}
		//Emp Alocation Type
		if (!this.empAlocationType.isDisabled()){
			this.empAlocationType.setConstraint(new StaticListValidator(PennantStaticListUtil.getEmpAlocList(),
					Labels.getLabel("label_EmployerDetailDialog_EmpAlocationType.value")));
		}
		
		// city name
		if (PennantConstants.CITY_FREETEXT) {
			if (!this.cityName.isReadonly()) {
				this.cityName.setConstraint(new PTStringValidator(Labels
						.getLabel("label_EmployerDetailDialog_CityName.value"), PennantRegularExpressions.REGEX_NAME,
						false));
			}
		}
		//Emp Name
		if (!this.bankRefNo.isReadonly()) {
			this.bankRefNo.setConstraint(new PTStringValidator(Labels.getLabel("label_EmployerDetailDialog_BankRefNo.value"), PennantRegularExpressions.REGEX_ALPHANUM_CODE,false));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.empName.setConstraint("");
		this.establishDate.setConstraint("");
		this.empAddrHNbr.setConstraint("");
		this.empFlatNbr.setConstraint("");
		this.empAddrStreet.setConstraint("");
		this.empAddrLine1.setConstraint("");
		this.empAddrLine2.setConstraint("");
		this.empPOBox.setConstraint("");
		this.empPhone.setConstraint("");
		this.empFax.setConstraint("");
		this.empTelexNo.setConstraint("");
		this.empEmailId.setConstraint("");
		this.empWebSite.setConstraint("");
		this.contactPersonName.setConstraint("");
		this.contactPersonNo.setConstraint("");
		this.empAlocationType.setConstraint("");
		this.empFaxAreaCode.setConstraint("");
		this.empFaxCountryCode.setConstraint("");
		this.empTelexAreaCode.setConstraint("");
		this.empTelexCountryCode.setConstraint("");
		this.cityName.setConstraint("");
		this.bankRefNo.setConstraint("");
		logger.debug("Leaving");
	}


	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		//Emp Industry
			this.empIndustry.setConstraint(new PTStringValidator(Labels.getLabel("label_EmployerDetailDialog_EmpCategory.value"), null, true,true));
		//Emp Country
			this.empCountry.setConstraint(new PTStringValidator(Labels.getLabel("label_EmployerDetailDialog_EmpCountry.value"), null, true,true));
		//Emp Province
			this.empProvince.setConstraint(new PTStringValidator(Labels.getLabel("label_EmployerDetailDialog_EmpProvince.value"), null, true,true));
		//Emp City
			if(!PennantConstants.CITY_FREETEXT) {
			this.empCity.setConstraint(new PTStringValidator(Labels.getLabel("label_EmployerDetailDialog_EmpCity.value"), null, false,true));
			}
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		this.empIndustry.setConstraint("");
		this.empCountry.setConstraint("");
		this.empProvince.setConstraint("");
		this.empCity.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.empIndustry.setErrorMessage("");
		this.empName.setErrorMessage("");
		this.establishDate.setErrorMessage("");
		this.empAddrHNbr.setErrorMessage("");
		this.empFlatNbr.setErrorMessage("");
		this.empAddrStreet.setErrorMessage("");
		this.empAddrLine1.setErrorMessage("");
		this.empAddrLine2.setErrorMessage("");
		this.empPOBox.setErrorMessage("");
		this.empCountry.setErrorMessage("");
		this.empProvince.setErrorMessage("");
		this.empCity.setErrorMessage("");
		this.empPhone.setErrorMessage("");
		this.empFax.setErrorMessage("");
		this.empTelexNo.setErrorMessage("");
		this.empEmailId.setErrorMessage("");
		this.empWebSite.setErrorMessage("");
		this.contactPersonName.setErrorMessage("");
		this.contactPersonNo.setErrorMessage("");
		this.empAlocationType.setErrorMessage("");
		this.empFaxAreaCode.setErrorMessage("");
		this.empFaxCountryCode.setErrorMessage("");
		this.empTelexAreaCode.setErrorMessage("");
		this.empTelexCountryCode.setErrorMessage("");
		this.bankRefNo.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getEmployerDetailListCtrl().search();
	}

	/**
	 * Deletes a EmployerDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final EmployerDetail aEmployerDetail = new EmployerDetail();
		BeanUtils.copyProperties(getEmployerDetail(), aEmployerDetail);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " +
				Labels.getLabel("label_EmployerDetailDialog_EmpCategory.value")+" : "+aEmployerDetail.getEmpIndustry()+","+
				Labels.getLabel("label_EmployerDetailDialog_EmpName.value")+" : "+aEmployerDetail.getEmpName();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aEmployerDetail.getRecordType())){
				aEmployerDetail.setVersion(aEmployerDetail.getVersion()+1);
				aEmployerDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aEmployerDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aEmployerDetail.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aEmployerDetail.getNextTaskId(), aEmployerDetail);
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aEmployerDetail,tranType)){
					refreshList();
					closeDialog(); 
				}

			}catch (DataAccessException e){
				MessageUtil.showError(e);
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

		this.empIndustry.setValue("");
		this.empIndustry.setDescription("");
		this.empName.setValue("");
		this.establishDate.setText("");
		this.empAddrHNbr.setValue("");
		this.empFlatNbr.setValue("");
		this.empAddrStreet.setValue("");
		this.empAddrLine1.setValue("");
		this.empAddrLine2.setValue("");
		this.empPOBox.setValue("");
		this.empCountry.setValue("");
		this.empCountry.setDescription("");
		this.empProvince.setValue("");
		this.empProvince.setDescription("");
		this.empCity.setValue("");
		this.empCity.setDescription("");
		this.empPhone.setValue("");
		this.empFax.setValue("");
		this.empTelexNo.setValue("");
		this.empEmailId.setValue("");
		this.empWebSite.setValue("");
		this.contactPersonName.setValue("");
		this.contactPersonNo.setValue("");
		this.empFaxAreaCode.setValue("");
		this.empFaxCountryCode.setValue("");
		this.empTelexAreaCode.setValue("");
		this.empTelexCountryCode.setValue("");
		this.bankRefNo.setValue("");
		this.empAlocationType.setSelectedIndex(0);
		this.empIsActive.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final EmployerDetail aEmployerDetail = new EmployerDetail();
		BeanUtils.copyProperties(getEmployerDetail(), aEmployerDetail);
		boolean isNew = false;

		if(isWorkFlowEnabled()){
			aEmployerDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aEmployerDetail.getNextTaskId(), aEmployerDetail);
		}

		// force validation, if on, than execute by component.getValue()
		if(!PennantConstants.RECORD_TYPE_DEL.equals(aEmployerDetail.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the EmployerDetail object with the components data
			doWriteComponentsToBean(aEmployerDetail);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aEmployerDetail.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aEmployerDetail.getRecordType())){
				aEmployerDetail.setVersion(aEmployerDetail.getVersion()+1);
				if(isNew){
					aEmployerDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aEmployerDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aEmployerDetail.setNewRecord(true);
				}
			}
		}else{
			aEmployerDetail.setVersion(aEmployerDetail.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aEmployerDetail,tranType)){
				//doWriteBeanToComponents(aEmployerDetail);
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
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

	private boolean doProcess(EmployerDetail aEmployerDetail,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		aEmployerDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aEmployerDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aEmployerDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (auditingReq) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			aEmployerDetail.setTaskId(getTaskId());
			aEmployerDetail.setNextTaskId(getNextTaskId());
			aEmployerDetail.setRoleCode(getRole());
			aEmployerDetail.setNextRoleCode(getNextRoleCode());

			if (StringUtils.isBlank(getOperationRefs())) {
				processCompleted = doSaveProcess(getAuditHeader(aEmployerDetail, tranType),null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader =  getAuditHeader(aEmployerDetail, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			processCompleted = doSaveProcess(getAuditHeader(aEmployerDetail, tranType), null);
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

		EmployerDetail aEmployerDetail = (EmployerDetail) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.isBlank(method)){
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())){
						auditHeader = getEmployerDetailService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getEmployerDetailService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))){
						auditHeader = getEmployerDetailService().doApprove(auditHeader);

						if(PennantConstants.RECORD_TYPE_DEL.equals(aEmployerDetail.getRecordType())){
							deleteNotes=true;
						}

					}else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))){
						auditHeader = getEmployerDetailService().doReject(auditHeader);
						if(PennantConstants.RECORD_TYPE_NEW.equals(aEmployerDetail.getRecordType())){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_EmployerDetailDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_EmployerDetailDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes("EmployerDetail",aEmployerDetail.getEmpName(),aEmployerDetail.getVersion()),true);
					}
				}

				if (retValue==PennantConstants.porcessOVERIDE){
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
			setOverideMap(auditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}		

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(EmployerDetail aEmployerDetail, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aEmployerDetail.getBefImage(), aEmployerDetail);   
		return new AuditHeader(String.valueOf(aEmployerDetail.getEmployerId()),null,null,null,auditDetail,aEmployerDetail.getUserDetails(),getOverideMap());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public EmployerDetail getEmployerDetail() {
		return this.employerDetail;
	}

	public void setEmployerDetail(EmployerDetail employerDetail) {
		this.employerDetail = employerDetail;
	}
	public void setEmployerDetailService(EmployerDetailService employerDetailService) {
		this.employerDetailService = employerDetailService;
	}

	public EmployerDetailService getEmployerDetailService() {
		return this.employerDetailService;
	}

	public void setEmployerDetailListCtrl(EmployerDetailListCtrl employerDetailListCtrl) {
		this.employerDetailListCtrl = employerDetailListCtrl;
	}
	public EmployerDetailListCtrl getEmployerDetailListCtrl() {
		return this.employerDetailListCtrl;
	}


}

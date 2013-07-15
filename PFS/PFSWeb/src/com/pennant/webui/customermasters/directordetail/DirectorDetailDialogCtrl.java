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
 * FileName    		:  DirectorDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-12-2011    														*
 *                                                                  						*
 * Modified Date    :  01-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.directordetail;

import java.io.Serializable;
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
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.SimpleConstraint;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Gender;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.model.systemmasters.Salutation;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.DirectorDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.customermasters.customer.CustomerSelectCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/DirectorDetail/directorDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class DirectorDetailDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -3436424948986683205L;
	private final static Logger logger = Logger.getLogger(DirectorDetailDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window  window_DirectorDetailDialog; 	// autowired
	protected Longbox custID; 						// autowired
	protected Textbox firstName; 					// autowired
	protected Textbox middleName; 					// autowired
	protected Textbox lastName; 					// autowired
	protected Textbox shortName; 					// autowired
	protected Textbox custGenderCode; 				// autowired
	protected Textbox custSalutationCode; 			// autowired
	protected Textbox custAddrHNbr; 				// autowired
	protected Textbox custFlatNbr;	 				// autowired
	protected Textbox custAddrStreet; 				// autowired
	protected Textbox custAddrLine1; 				// autowired
	protected Textbox custAddrLine2; 				// autowired
	protected Textbox custPOBox; 					// autowired
	protected Textbox custAddrCity; 				// autowired
	protected Textbox custAddrProvince; 			// autowired
	protected Textbox custAddrCountry; 				// autowired
	protected Textbox custAddrZIP; 					// autowired
	protected Textbox custAddrPhone; 				// autowired
	protected Datebox custAddrFrom; 				// autowired
	protected Textbox custCIF;						// autowired
	protected Label   custShrtName;					// autowired

	protected Label    		recordStatus; 			// autowired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;

	// not auto wired vars
	private DirectorDetail directorDetail; // overhanded per param
	private transient DirectorDetailListCtrl directorDetailListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient long  		oldVar_custID;
	private transient String  		oldVar_firstName;
	private transient String  		oldVar_middleName;
	private transient String  		oldVar_lastName;
	private transient String  		oldVar_shortName;
	private transient String  		oldVar_custGenderCode;
	private transient String  		oldVar_custSalutationCode;
	private transient String  		oldVar_custAddrHNbr;
	private transient String  		oldVar_custFlatNbr;
	private transient String  		oldVar_custAddrStreet;
	private transient String  		oldVar_custAddrLine1;
	private transient String  		oldVar_custAddrLine2;
	private transient String  		oldVar_custPOBox;
	private transient String  		oldVar_custAddrCity;
	private transient String  		oldVar_custAddrProvince;
	private transient String  		oldVar_custAddrCountry;
	private transient String  		oldVar_custAddrZIP;
	private transient String  		oldVar_custAddrPhone;
	private transient Date  		oldVar_custAddrFrom;
	private transient String 		oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_DirectorDetailDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 	// autowire
	protected Button btnEdit; 	// autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; 	// autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; 	// autowire
	protected Button btnHelp; 	// autowire
	protected Button btnNotes;	// autowire
	
	protected Button btnSearchCustGenderCode; 	// autowire
	protected Textbox lovDescCustGenderCodeName;
	private transient String 		oldVar_lovDescCustGenderCodeName;
	
	protected Button btnSearchCustSalutationCode;// autowire
	protected Textbox lovDescCustSalutationCodeName;
	private transient String 		oldVar_lovDescCustSalutationCodeName;
	
	protected Button btnSearchCustAddrCity; 	// autowire
	protected Textbox lovDescCustAddrCityName;
	private transient String 		oldVar_lovDescCustAddrCityName;
	
	protected Button btnSearchCustAddrProvince; // autowire
	protected Textbox lovDescCustAddrProvinceName;
	private transient String 		oldVar_lovDescCustAddrProvinceName;
	
	protected Button btnSearchCustAddrCountry; 	// autowire
	protected Textbox lovDescCustAddrCountryName;
	private transient String 		oldVar_lovDescCustAddrCountryName;
	
	// ServiceDAOs / Domain Classes
	private transient DirectorDetailService directorDetailService;
	private transient PagedListService pagedListService;
	private transient CustomerSelectCtrl customerSelectCtrl;

	private boolean newRecord=false;
	private boolean newCustomer=false;
	private List<DirectorDetail> customerDirectors;
	private CustomerDialogCtrl customerDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject ;
	protected Button btnSearchPRCustid;
	/**
	 * default constructor.<br>
	 */
	public DirectorDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected DirectorDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_DirectorDetailDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();
		
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, 
				true, this.btnNew,this.btnEdit, this.btnDelete, this.btnSave, 
				this.btnCancel, this.btnClose,this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		
		// READ OVERHANDED params !
		if (args.containsKey("directorDetail")) {
			this.directorDetail = (DirectorDetail) args.get("directorDetail");
			DirectorDetail befImage =new DirectorDetail();
			BeanUtils.copyProperties(this.directorDetail, befImage);
			this.directorDetail.setBefImage(befImage);
			
			setDirectorDetail(this.directorDetail);
		} else {
			setDirectorDetail(null);
		}

		if(getDirectorDetail().isNewRecord()){
			setNewRecord(true);
		}

		if(args.containsKey("customerDialogCtrl")){
			
			setCustomerDialogCtrl((CustomerDialogCtrl) args.get("customerDialogCtrl"));
			setNewCustomer(true);
			
			if(args.containsKey("newRecord")){
				setNewRecord(true);
			}else{
				setNewRecord(false);
			}
			this.directorDetail.setWorkflowId(0);
			if(args.containsKey("roleCode")){
				getUserWorkspace().alocateRoleAuthorities((String) args.get("roleCode"), "DirectorDetailDialog");
			}
		}
	
		doLoadWorkFlow(this.directorDetail.isWorkflow(),this.directorDetail.getWorkflowId(),
				this.directorDetail.getNextTaskId());
		
		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "DirectorDetailDialog");
		}
	
		// READ OVERHANDED params !
		// we get the directorDetailListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete directorDetail here.
		if (args.containsKey("directorDetailListCtrl")) {
			setDirectorDetailListCtrl((DirectorDetailListCtrl) args.get("directorDetailListCtrl"));
		} else {
			setDirectorDetailListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getDirectorDetail());
		
		//Calling SelectCtrl For proper selection of Customer
		if(isNewRecord() & !isNewCustomer()){
			onload();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.custID.setMaxlength(19);
		this.firstName.setMaxlength(20);
		this.middleName.setMaxlength(20);
		this.lastName.setMaxlength(20);
		this.shortName.setMaxlength(20);
		this.custGenderCode.setMaxlength(8);
		this.custSalutationCode.setMaxlength(8);
		this.custAddrHNbr.setMaxlength(50);
		this.custFlatNbr.setMaxlength(50);
		this.custAddrStreet.setMaxlength(50);
		this.custAddrLine1.setMaxlength(50);
		this.custAddrLine2.setMaxlength(50);
		this.custPOBox.setMaxlength(8);
		this.custAddrCity.setMaxlength(8);
		this.custAddrProvince.setMaxlength(8);
		this.custAddrCountry.setMaxlength(2);
		this.custAddrZIP.setMaxlength(10);
		this.custAddrPhone.setMaxlength(50);
	 	this.custAddrFrom.setFormat(PennantConstants.dateFormat);
		
		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
			
		}else{
			this.groupboxWf.setVisible(false);
			
		}
		
		logger.debug("Leaving") ;
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
		logger.debug("Entering") ;
		
		getUserWorkspace().alocateAuthorities("DirectorDetailDialog");
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_DirectorDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_DirectorDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_DirectorDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_DirectorDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);
		
		logger.debug("Leaving") ;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_DirectorDetailDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_DirectorDetailDialog);
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

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
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
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, 
					MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

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
			closeWindow();
		}
		logger.debug("Leaving") ;
	}

	/**
	 * Method for closing Customer Selection Window 
	 * @throws InterruptedException
	 */
	public void closeWindow() throws InterruptedException{
		logger.debug("Entering");
		
		if(isNewCustomer()){
			window_DirectorDetailDialog.onClose();	
		}else{
			closeDialog(this.window_DirectorDetailDialog, "DirectorDetail");
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
		logger.debug("Entering") ;
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aDirectorDetail
	 *            DirectorDetail
	 */
	public void doWriteBeanToComponents(DirectorDetail aDirectorDetail) {
		logger.debug("Entering") ;
		if(aDirectorDetail.getCustID()!=Long.MIN_VALUE){
			this.custID.setValue(aDirectorDetail.getCustID());	
		}

		this.firstName.setValue(aDirectorDetail.getFirstName());
		this.middleName.setValue(aDirectorDetail.getMiddleName());
		this.lastName.setValue(aDirectorDetail.getLastName());
		this.shortName.setValue(aDirectorDetail.getShortName());
		this.custGenderCode.setValue(aDirectorDetail.getCustGenderCode());
		this.custSalutationCode.setValue(aDirectorDetail.getCustSalutationCode());
		this.custAddrHNbr.setValue(aDirectorDetail.getCustAddrHNbr());
		this.custFlatNbr.setValue(aDirectorDetail.getCustFlatNbr());
		this.custAddrStreet.setValue(aDirectorDetail.getCustAddrStreet());
		this.custAddrLine1.setValue(aDirectorDetail.getCustAddrLine1());
		this.custAddrLine2.setValue(aDirectorDetail.getCustAddrLine2());
		this.custPOBox.setValue(aDirectorDetail.getCustPOBox());
		this.custAddrCity.setValue(aDirectorDetail.getCustAddrCity());
		this.custAddrProvince.setValue(aDirectorDetail.getCustAddrProvince());
		this.custAddrCountry.setValue(aDirectorDetail.getCustAddrCountry());
		this.custAddrZIP.setValue(aDirectorDetail.getCustAddrZIP());
		this.custAddrPhone.setValue(aDirectorDetail.getCustAddrPhone());
		this.custAddrFrom.setValue(aDirectorDetail.getCustAddrFrom());
		this.custCIF.setValue(aDirectorDetail.getLovDescCustCIF()==null?"":
			aDirectorDetail.getLovDescCustCIF().trim());
		this.custShrtName.setValue(aDirectorDetail.getLovDescCustShrtName()==null?"":
			aDirectorDetail.getLovDescCustShrtName().trim());

		if (isNewRecord()){
			this.lovDescCustGenderCodeName.setValue("");
			this.lovDescCustSalutationCodeName.setValue("");
			this.lovDescCustAddrCityName.setValue("");
			this.lovDescCustAddrProvinceName.setValue("");
			this.lovDescCustAddrCountryName.setValue("");
		}else{
			if(isNewCustomer()){
				this.lovDescCustGenderCodeName.setValue(aDirectorDetail.getLovDescCustGenderCodeName());
				this.lovDescCustSalutationCodeName.setValue(aDirectorDetail.getLovDescCustSalutationCodeName());
				this.lovDescCustAddrCityName.setValue(aDirectorDetail.getLovDescCustAddrCityName());
				this.lovDescCustAddrProvinceName.setValue(aDirectorDetail.getLovDescCustAddrProvinceName());
				this.lovDescCustAddrCountryName.setValue(aDirectorDetail.getLovDescCustAddrCountryName());
			}else{
				this.lovDescCustGenderCodeName.setValue(aDirectorDetail.getCustGenderCode()+"-"+
						aDirectorDetail.getLovDescCustGenderCodeName());
				this.lovDescCustSalutationCodeName.setValue(aDirectorDetail.getCustSalutationCode()+"-"+
						aDirectorDetail.getLovDescCustSalutationCodeName());
				this.lovDescCustAddrCityName.setValue(aDirectorDetail.getCustAddrCity()+"-"+
						aDirectorDetail.getLovDescCustAddrCityName());
				this.lovDescCustAddrProvinceName.setValue(aDirectorDetail.getCustAddrProvince()+"-"+
						aDirectorDetail.getLovDescCustAddrProvinceName());
				this.lovDescCustAddrCountryName.setValue(aDirectorDetail.getCustAddrCountry()+"-"+
						aDirectorDetail.getLovDescCustAddrCountryName());
			}
			
		}
		this.recordStatus.setValue(aDirectorDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aDirectorDetail
	 */
	public void doWriteComponentsToBean(DirectorDetail aDirectorDetail) {
		logger.debug("Entering") ;
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
	 		aDirectorDetail.setCustID(this.custID.getValue());	
	 		aDirectorDetail.setLovDescCustCIF(this.custCIF.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aDirectorDetail.setFirstName(this.firstName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aDirectorDetail.setMiddleName(this.middleName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aDirectorDetail.setLastName(this.lastName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aDirectorDetail.setShortName(this.shortName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		aDirectorDetail.setLovDescCustGenderCodeName(this.lovDescCustGenderCodeName.getValue());
	 		aDirectorDetail.setCustGenderCode(this.custGenderCode.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		aDirectorDetail.setLovDescCustSalutationCodeName(this.lovDescCustSalutationCodeName.getValue());
	 		aDirectorDetail.setCustSalutationCode(this.custSalutationCode.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aDirectorDetail.setCustAddrHNbr(this.custAddrHNbr.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aDirectorDetail.setCustFlatNbr(this.custFlatNbr.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aDirectorDetail.setCustAddrStreet(this.custAddrStreet.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aDirectorDetail.setCustAddrLine1(this.custAddrLine1.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aDirectorDetail.setCustAddrLine2(this.custAddrLine2.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aDirectorDetail.setCustPOBox(this.custPOBox.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		aDirectorDetail.setLovDescCustAddrCityName(this.lovDescCustAddrCityName.getValue());
	 		aDirectorDetail.setCustAddrCity(this.custAddrCity.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		aDirectorDetail.setLovDescCustAddrProvinceName(this.lovDescCustAddrProvinceName.getValue());
	 		aDirectorDetail.setCustAddrProvince(this.custAddrProvince.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		aDirectorDetail.setLovDescCustAddrCountryName(this.lovDescCustAddrCountryName.getValue());
	 		aDirectorDetail.setCustAddrCountry(this.custAddrCountry.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aDirectorDetail.setCustAddrZIP(this.custAddrZIP.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aDirectorDetail.setCustAddrPhone(this.custAddrPhone.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		aDirectorDetail.setCustAddrFrom(new Timestamp(this.custAddrFrom.getValue().getTime()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		doRemoveValidation();
		doRemoveLOVValidation();
		
		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		aDirectorDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aDirectorDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(DirectorDetail aDirectorDetail) throws InterruptedException {
		logger.debug("Entering") ;
	
		// set ReadOnly mode accordingly if the object is new or not.
		if (aDirectorDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custCIF.focus();
		} else {
			this.firstName.focus();
			if (isNewCustomer()){
				doEdit();
			}else if (isWorkFlowEnabled()){
				this.btnNotes.setVisible(true);
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aDirectorDetail);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			if(isNewCustomer()){
				this.window_DirectorDetailDialog.setHeight("580px");
				this.window_DirectorDetailDialog.setWidth("800px");
				this.groupboxWf.setVisible(false);
				this.window_DirectorDetailDialog.doModal() ;
			}else{
				this.window_DirectorDetailDialog.setWidth("100%");
				this.window_DirectorDetailDialog.setHeight("100%");
				setDialog(this.window_DirectorDetailDialog);
			}
		
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving") ;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
 		this.oldVar_custID = this.custID.longValue();
		this.oldVar_firstName = this.firstName.getValue();
		this.oldVar_middleName = this.middleName.getValue();
		this.oldVar_lastName = this.lastName.getValue();
		this.oldVar_shortName = this.shortName.getValue();
 		this.oldVar_custGenderCode = this.custGenderCode.getValue();
 		this.oldVar_lovDescCustGenderCodeName = this.lovDescCustGenderCodeName.getValue();
 		this.oldVar_custSalutationCode = this.custSalutationCode.getValue();
 		this.oldVar_lovDescCustSalutationCodeName = this.lovDescCustSalutationCodeName.getValue();
		this.oldVar_custAddrHNbr = this.custAddrHNbr.getValue();
		this.oldVar_custFlatNbr = this.custFlatNbr.getValue();
		this.oldVar_custAddrStreet = this.custAddrStreet.getValue();
		this.oldVar_custAddrLine1 = this.custAddrLine1.getValue();
		this.oldVar_custAddrLine2 = this.custAddrLine2.getValue();
		this.oldVar_custPOBox = this.custPOBox.getValue();
 		this.oldVar_custAddrCity = this.custAddrCity.getValue();
 		this.oldVar_lovDescCustAddrCityName = this.lovDescCustAddrCityName.getValue();
 		this.oldVar_custAddrProvince = this.custAddrProvince.getValue();
 		this.oldVar_lovDescCustAddrProvinceName = this.lovDescCustAddrProvinceName.getValue();
 		this.oldVar_custAddrCountry = this.custAddrCountry.getValue();
 		this.oldVar_lovDescCustAddrCountryName = this.lovDescCustAddrCountryName.getValue();
		this.oldVar_custAddrZIP = this.custAddrZIP.getValue();
		this.oldVar_custAddrPhone = this.custAddrPhone.getValue();
		this.oldVar_custAddrFrom = PennantAppUtil.getTimestamp(this.custAddrFrom.getValue());	
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from member vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
 		this.custID.setValue(this.oldVar_custID);
		this.firstName.setValue(this.oldVar_firstName);
		this.middleName.setValue(this.oldVar_middleName);
		this.lastName.setValue(this.oldVar_lastName);
		this.shortName.setValue(this.oldVar_shortName);
 		this.custGenderCode.setValue(this.oldVar_custGenderCode);
 		this.lovDescCustGenderCodeName.setValue(this.oldVar_lovDescCustGenderCodeName);
 		this.custSalutationCode.setValue(this.oldVar_custSalutationCode);
 		this.lovDescCustSalutationCodeName.setValue(this.oldVar_lovDescCustSalutationCodeName);
		this.custAddrHNbr.setValue(this.oldVar_custAddrHNbr);
		this.custFlatNbr.setValue(this.oldVar_custFlatNbr);
		this.custAddrStreet.setValue(this.oldVar_custAddrStreet);
		this.custAddrLine1.setValue(this.oldVar_custAddrLine1);
		this.custAddrLine2.setValue(this.oldVar_custAddrLine2);
		this.custPOBox.setValue(this.oldVar_custPOBox);
 		this.custAddrCity.setValue(this.oldVar_custAddrCity);
 		this.lovDescCustAddrCityName.setValue(this.oldVar_lovDescCustAddrCityName);
 		this.custAddrProvince.setValue(this.oldVar_custAddrProvince);
 		this.lovDescCustAddrProvinceName.setValue(this.oldVar_lovDescCustAddrProvinceName);
 		this.custAddrCountry.setValue(this.oldVar_custAddrCountry);
 		this.lovDescCustAddrCountryName.setValue(this.oldVar_lovDescCustAddrCountryName);
		this.custAddrZIP.setValue(this.oldVar_custAddrZIP);
		this.custAddrPhone.setValue(this.oldVar_custAddrPhone);
		this.custAddrFrom.setValue(this.oldVar_custAddrFrom);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		
		if(isWorkFlowEnabled()){
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

		//To clear the Error Messages
		doClearMessage();
		
		if (this.oldVar_custID != this.custID.getValue()) {
			return true;
		}
		if (this.oldVar_firstName != this.firstName.getValue()) {
			return true;
		}
		if (this.oldVar_middleName != this.middleName.getValue()) {
			return true;
		}
		if (this.oldVar_lastName != this.lastName.getValue()) {
			return true;
		}
		if (this.oldVar_shortName != this.shortName.getValue()) {
			return true;
		}
		if (this.oldVar_custGenderCode != this.custGenderCode.getValue()) {
			return true;
		}
		if (this.oldVar_custSalutationCode != this.custSalutationCode.getValue()) {
			return true;
		}
		if (this.oldVar_custAddrHNbr != this.custAddrHNbr.getValue()) {
			return true;
		}
		if (this.oldVar_custFlatNbr != this.custFlatNbr.getValue()) {
			return true;
		}
		if (this.oldVar_custAddrStreet != this.custAddrStreet.getValue()) {
			return true;
		}
		if (this.oldVar_custAddrLine1 != this.custAddrLine1.getValue()) {
			return true;
		}
		if (this.oldVar_custAddrLine2 != this.custAddrLine2.getValue()) {
			return true;
		}
		if (this.oldVar_custPOBox != this.custPOBox.getValue()) {
			return true;
		}
		if (this.oldVar_custAddrCity != this.custAddrCity.getValue()) {
			return true;
		}
		if (this.oldVar_custAddrProvince != this.custAddrProvince.getValue()) {
			return true;
		}
		if (this.oldVar_custAddrCountry != this.custAddrCountry.getValue()) {
			return true;
		}
		if (this.oldVar_custAddrZIP != this.custAddrZIP.getValue()) {
			return true;
		}
		if (this.oldVar_custAddrPhone != this.custAddrPhone.getValue()) {
			return true;
		}
		String old_custAddrFrom = "";
		String new_custAddrFrom = "";
		if (this.oldVar_custAddrFrom != null) {
			old_custAddrFrom = DateUtility.formatDate(this.oldVar_custAddrFrom,PennantConstants.dateFormat);
		}
		if (this.custAddrFrom.getValue() != null) {
			new_custAddrFrom = DateUtility.formatDate(this.custAddrFrom.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_custAddrFrom).equals(StringUtils.trimToEmpty(new_custAddrFrom))) {
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
		setValidationOn(true);
		if (!this.custID.isReadonly()){
			this.custCIF.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_DirectorDetailDialog_CustID.value")}));
		}
		if (!this.firstName.isReadonly()){
			this.firstName.setConstraint(new SimpleConstraint(PennantConstants.NAME_REGEX,
					Labels.getLabel("MAND_FIELD_CHARACTER",	new String[] { Labels.getLabel(
							"label_DirectorDetailDialog_FirstName.value") })));
		}	
		if (!this.middleName.isReadonly()){
			this.middleName.setConstraint(new SimpleConstraint(PennantConstants.NM_NAME_REGEX,
					Labels.getLabel("FIELD_CHARACTER",new String[] { Labels.getLabel(
							"label_DirectorDetailDialog_MiddleName.value") })));
		}	
		if (!this.lastName.isReadonly()){
			this.lastName.setConstraint(new SimpleConstraint(PennantConstants.NAME_REGEX,
					Labels.getLabel("MAND_FIELD_CHARACTER",new String[] { Labels.getLabel(
							"label_DirectorDetailDialog_LastName.value") })));
		}	
		if (!this.shortName.isReadonly()){
			this.shortName.setConstraint(new SimpleConstraint(PennantConstants.NAME_REGEX,
					Labels.getLabel("MAND_FIELD_CHARACTER",new String[] { Labels.getLabel(
							"label_DirectorDetailDialog_ShortName.value") })));
		}	
		if (!this.custAddrHNbr.isReadonly()){
			this.custAddrHNbr.setConstraint(new SimpleConstraint(PennantConstants.HNO_FNO_REGEX,
					Labels.getLabel("MAND_FIELD_ALPHANUMERIC_SPECIALCHAR",new String[]{Labels.getLabel(
								"label_DirectorDetailDialog_CustAddrHNbr.value")})));
		}	
		if (!this.custFlatNbr.isReadonly()){
			this.custFlatNbr.setConstraint(new SimpleConstraint(PennantConstants.NM_HNO_FNO_REGEX, 
					Labels.getLabel("ALPHANUMERIC_SPECIALCHAR",new String[]{Labels.getLabel(
								"label_DirectorDetailDialog_CustFlatNbr.value")})));
		}	
		if (!this.custAddrStreet.isReadonly()){
			this.custAddrStreet.setConstraint(new SimpleConstraint(PennantConstants.ADDRESS_LINE1_REGEX,
					Labels.getLabel("MAND_FIELD_CHAR_NUMBER",new String[]{Labels.getLabel(
							"label_DirectorDetailDialog_CustAddrStreet.value")})));
		}	
		if (!this.custAddrLine1.isReadonly()){
			this.custAddrLine1.setConstraint(new SimpleConstraint(PennantConstants.NM_ADDRESS_LINE1_REGEX,
					Labels.getLabel("FIELD_CHAR_NUMBER",new String[]{Labels.getLabel(
								"label_DirectorDetailDialog_CustAddrLine1.value")})));
		}	
		if (!this.custAddrLine2.isReadonly()){
			this.custAddrLine2.setConstraint(new SimpleConstraint(PennantConstants.NM_ADDRESS_LINE1_REGEX,
					Labels.getLabel("FIELD_CHAR_NUMBER",new String[]{Labels.getLabel(
								"label_DirectorDetailDialog_CustAddrLine2.value")})));
		}	
		if (!this.custPOBox.isReadonly()){
			this.custPOBox.setConstraint(new SimpleConstraint(PennantConstants.NUM_REGEX, 
					Labels.getLabel("FIELD_NUMBER",new String[]{Labels.getLabel(
								"label_DirectorDetailDialog_CustPOBox.value")})));
		}	
		if (!this.custAddrZIP.isReadonly()){
			this.custAddrZIP.setConstraint(new SimpleConstraint(PennantConstants.ZIP_REGEX,
					Labels.getLabel("FIELD_NUMBER",new String[]{Labels.getLabel(
							"label_DirectorDetailDialog_CustAddrZIP.value")})));
		}	
		if (!this.custAddrPhone.isReadonly()){
			this.custAddrPhone.setConstraint(new SimpleConstraint(PennantConstants.PH_REGEX, 
					Labels.getLabel("FIELD_NUMBER",new String[]{Labels.getLabel(
								"label_DirectorDetailDialog_CustAddrPhone.value")})));
		}	
		if (!this.custAddrFrom.isDisabled()){
			this.custAddrFrom.setConstraint("NO EMPTY,NO TODAY,NO FUTURE:"+ Labels.getLabel(
					"DATE_EMPTY_FUTURE_TODAY",new String[] { Labels.getLabel(
							"label_DirectorDetailDialog_CustAddrFrom.value") }));
		}
	logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.custCIF.setConstraint("");
		this.firstName.setConstraint("");
		this.middleName.setConstraint("");
		this.lastName.setConstraint("");
		this.shortName.setConstraint("");
		this.custAddrHNbr.setConstraint("");
		this.custFlatNbr.setConstraint("");
		this.custAddrStreet.setConstraint("");
		this.custAddrLine1.setConstraint("");
		this.custAddrLine2.setConstraint("");
		this.custPOBox.setConstraint("");
		this.custAddrZIP.setConstraint("");
		this.custAddrPhone.setConstraint("");
		this.custAddrFrom.setConstraint("");
		logger.debug("Leaving");
	}
	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.lovDescCustGenderCodeName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[]{Labels.getLabel("label_DirectorDetailDialog_CustGenderCode.value")}));
		this.lovDescCustSalutationCodeName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[]{Labels.getLabel("label_DirectorDetailDialog_CustSalutationCode.value")}));
		this.lovDescCustAddrCityName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[]{Labels.getLabel("label_DirectorDetailDialog_CustAddrCity.value")}));
		this.lovDescCustAddrProvinceName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[]{Labels.getLabel("label_DirectorDetailDialog_CustAddrProvince.value")}));
		this.lovDescCustAddrCountryName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[]{Labels.getLabel("label_DirectorDetailDialog_CustAddrCountry.value")}));
		logger.debug("Leaving");
	}
	
	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.lovDescCustGenderCodeName.setConstraint("");
		this.lovDescCustSalutationCodeName.setConstraint("");
		this.lovDescCustAddrCityName.setConstraint("");
		this.lovDescCustAddrProvinceName.setConstraint("");
		this.lovDescCustAddrCountryName.setConstraint("");
		logger.debug("Leaving");
	}
	
	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.custCIF.setErrorMessage("");
		this.firstName.setErrorMessage("");
		this.middleName.setErrorMessage("");
		this.lastName.setErrorMessage("");
		this.shortName.setErrorMessage("");
		this.lovDescCustGenderCodeName.setErrorMessage("");
		this.lovDescCustSalutationCodeName.setErrorMessage("");
		this.custAddrHNbr.setErrorMessage("");
		this.custFlatNbr.setErrorMessage("");
		this.custAddrStreet.setErrorMessage("");
		this.custAddrLine1.setErrorMessage("");
		this.custAddrLine2.setErrorMessage("");
		this.custPOBox.setErrorMessage("");
		this.lovDescCustAddrCityName.setErrorMessage("");
		this.lovDescCustAddrProvinceName.setErrorMessage("");
		this.lovDescCustAddrCountryName.setErrorMessage("");
		this.custAddrZIP.setErrorMessage("");
		this.custAddrPhone.setErrorMessage("");
		this.custAddrFrom.setErrorMessage("");
		logger.debug("Leaving");
	
	}
	
	// Method for refreshing the list after successful updating
	private void refreshList() {
		getDirectorDetailListCtrl().findSearchObject();
		if (getDirectorDetailListCtrl().listBoxDirectorDetail != null) {
			getDirectorDetailListCtrl().listBoxDirectorDetail.getListModel();
		}
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a DirectorDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final DirectorDetail aDirectorDetail = new DirectorDetail();
		BeanUtils.copyProperties(getDirectorDetail(), aDirectorDetail);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aDirectorDetail.getDirectorId();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		
		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aDirectorDetail.getRecordType()).equals("")){
				aDirectorDetail.setVersion(aDirectorDetail.getVersion()+1);
				aDirectorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				aDirectorDetail.setNewRecord(true);
				
				if (isWorkFlowEnabled()){
					aDirectorDetail.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(isNewCustomer()){
					tranType=PennantConstants.TRAN_DEL;
					AuditHeader auditHeader =  newCusomerProcess(aDirectorDetail,tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_DirectorDetailDialog,
							auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue==PennantConstants.porcessCONTINUE || 
							retValue==PennantConstants.porcessOVERIDE){
						getCustomerDialogCtrl().doFillCustomerDirectors(this.customerDirectors);
						// send the data back to customer
						closeWindow();
					}	

				}else if(doProcess(aDirectorDetail,tranType)){
					refreshList();
					closeWindow();
				}
			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new DirectorDetail object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old vars
		doStoreInitValues();

		final DirectorDetail aDirectorDetail = getDirectorDetailService().getNewDirectorDetail();
		setDirectorDetail(aDirectorDetail);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.custCIF.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (isNewRecord()){
			if(isNewCustomer()){
				this.btnCancel.setVisible(false);	
				this.btnSearchPRCustid.setVisible(false);
			}else{
				this.btnSearchPRCustid.setVisible(true);
			}
			this.btnSearchCustSalutationCode.setDisabled(true);
			this.btnSearchCustAddrCity.setDisabled(true);
			this.btnSearchCustAddrProvince.setDisabled(true);
			this.firstName.setReadonly(isReadOnly("DirectorDetailDialog_firstName"));
			this.lastName.setReadonly(isReadOnly("DirectorDetailDialog_lastName"));

		}else{
			this.btnCancel.setVisible(true);
			this.btnSearchPRCustid.setVisible(false);
			this.btnSearchCustSalutationCode.setDisabled(isReadOnly("DirectorDetailDialog_custSalutationCode"));
			this.btnSearchCustAddrCity.setDisabled(isReadOnly("DirectorDetailDialog_custAddrCity"));
			this.btnSearchCustAddrProvince.setDisabled(isReadOnly("DirectorDetailDialog_custAddrProvince"));
			this.firstName.setReadonly(true);
			this.lastName.setReadonly(true);
		}

		this.custCIF.setReadonly(true);
		this.middleName.setReadonly(isReadOnly("DirectorDetailDialog_middleName"));
		this.shortName.setReadonly(isReadOnly("DirectorDetailDialog_shortName"));
		this.btnSearchCustGenderCode.setDisabled(isReadOnly("DirectorDetailDialog_custGenderCode"));
		this.custAddrHNbr.setReadonly(isReadOnly("DirectorDetailDialog_custAddrHNbr"));
		this.custFlatNbr.setReadonly(isReadOnly("DirectorDetailDialog_custFlatNbr"));
		this.custAddrStreet.setReadonly(isReadOnly("DirectorDetailDialog_custAddrStreet"));
		this.custAddrLine1.setReadonly(isReadOnly("DirectorDetailDialog_custAddrLine1"));
		this.custAddrLine2.setReadonly(isReadOnly("DirectorDetailDialog_custAddrLine2"));
		this.custPOBox.setReadonly(isReadOnly("DirectorDetailDialog_custPOBox"));
		this.btnSearchCustAddrCountry.setDisabled(isReadOnly("DirectorDetailDialog_custAddrCountry"));
		this.custAddrZIP.setReadonly(isReadOnly("DirectorDetailDialog_custAddrZIP"));
		this.custAddrPhone.setReadonly(isReadOnly("DirectorDetailDialog_custAddrPhone"));
		this.custAddrFrom.setDisabled(isReadOnly("DirectorDetailDialog_custAddrFrom"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.directorDetail.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			if(newCustomer){
				if (isNewRecord()){
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				}else{
					this.btnCtrl.setWFBtnStatus_Edit(newCustomer);
				}
			}else{
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	//Check Rights for Each Component
	public boolean isReadOnly(String componentName){
		if (isWorkFlowEnabled() || isNewCustomer()){
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}
	
	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.custCIF.setReadonly(true);
		this.btnSearchPRCustid.setDisabled(true);
		this.firstName.setReadonly(true);
		this.middleName.setReadonly(true);
		this.lastName.setReadonly(true);
		this.shortName.setReadonly(true);
		this.btnSearchCustGenderCode.setDisabled(true);
		this.btnSearchCustSalutationCode.setDisabled(true);
		this.custAddrHNbr.setReadonly(true);
		this.custFlatNbr.setReadonly(true);
		this.custAddrStreet.setReadonly(true);
		this.custAddrLine1.setReadonly(true);
		this.custAddrLine2.setReadonly(true);
		this.custPOBox.setReadonly(true);
		this.btnSearchCustAddrCity.setDisabled(true);
		this.btnSearchCustAddrProvince.setDisabled(true);
		this.btnSearchCustAddrCountry.setDisabled(true);
		this.custAddrZIP.setReadonly(true);
		this.custAddrPhone.setReadonly(true);
		this.custAddrFrom.setDisabled(true);

		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if(isWorkFlowEnabled()){
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
		this.custCIF.setValue("");
		this.custShrtName.setValue("");
		this.firstName.setValue("");
		this.middleName.setValue("");
		this.lastName.setValue("");
		this.shortName.setValue("");
		this.custGenderCode.setValue("");
		this.lovDescCustGenderCodeName.setValue("");
		this.custSalutationCode.setValue("");
		this.lovDescCustSalutationCodeName.setValue("");
		this.custAddrHNbr.setValue("");
		this.custFlatNbr.setValue("");
		this.custAddrStreet.setValue("");
		this.custAddrLine1.setValue("");
		this.custAddrLine2.setValue("");
		this.custPOBox.setValue("");
		this.custAddrCity.setValue("");
		this.lovDescCustAddrCityName.setValue("");
		this.custAddrProvince.setValue("");
		this.lovDescCustAddrProvinceName.setValue("");
		this.custAddrCountry.setValue("");
		this.lovDescCustAddrCountryName.setValue("");
		this.custAddrZIP.setValue("");
		this.custAddrPhone.setValue("");
		this.custAddrFrom.setText("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final DirectorDetail aDirectorDetail = new DirectorDetail();
		BeanUtils.copyProperties(getDirectorDetail(), aDirectorDetail);
		boolean isNew = false;
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the DirectorDetail object with the components data
		doWriteComponentsToBean(aDirectorDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here
		
		isNew = aDirectorDetail.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aDirectorDetail.getRecordType()).equals("")){
				aDirectorDetail.setVersion(aDirectorDetail.getVersion()+1);
				if(isNew){
					aDirectorDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aDirectorDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aDirectorDetail.setNewRecord(true);
				}
			}
		}else{
			if(isNewCustomer()){
				if(isNewRecord()){
					aDirectorDetail.setVersion(1);
					aDirectorDetail.setRecordType(PennantConstants.RCD_ADD);
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}

				if(StringUtils.trimToEmpty(aDirectorDetail.getRecordType()).equals("")){
					aDirectorDetail.setVersion(aDirectorDetail.getVersion()+1);
					aDirectorDetail.setRecordType(PennantConstants.RCD_UPD);
				}
				
				if(aDirectorDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(aDirectorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}
				
			}else{
				aDirectorDetail.setVersion(aDirectorDetail.getVersion()+1);
				if(isNew){
					tranType =PennantConstants.TRAN_ADD;
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}
			}
		}
		
		// save it to database
		try {
			if(isNewCustomer()){
				AuditHeader auditHeader =  newCusomerProcess(aDirectorDetail,tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_DirectorDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || 
						retValue==PennantConstants.porcessOVERIDE){
					getCustomerDialogCtrl().doFillCustomerDirectors(this.customerDirectors);
					// send the data back to customer
					closeWindow();
				}
			}else if(doProcess(aDirectorDetail,tranType)){
				refreshList();
				// Close the Existing Dialog
				closeWindow();
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newCusomerProcess(DirectorDetail aDirectorDetail,String tranType){
		boolean recordAdded=false;
		
		AuditHeader auditHeader= getAuditHeader(aDirectorDetail, tranType);
		customerDirectors = new ArrayList<DirectorDetail>();
		
		String[] valueParm = new String[3];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aDirectorDetail.getCustID());
		valueParm[1] = aDirectorDetail.getFirstName();
		valueParm[2] = aDirectorDetail.getLastName();

		errParm[0] = PennantJavaUtil.getLabel("label_CustID") + ":"+ valueParm[0]+" "+
							PennantJavaUtil.getLabel("label_FirstName") + ":"+ valueParm[1];;
		errParm[1] = PennantJavaUtil.getLabel("label_LastName") + ":"+valueParm[2];
		
		if(getCustomerDialogCtrl().getDirectorsList()!=null && 
				getCustomerDialogCtrl().getDirectorsList().size()>0){
			for (int i = 0; i < getCustomerDialogCtrl().getDirectorsList().size(); i++) {
				DirectorDetail directorDetail = getCustomerDialogCtrl().getDirectorsList().get(i);
				
				if(directorDetail.getFirstName().equals(aDirectorDetail.getFirstName()) && 
						directorDetail.getLastName().equals(aDirectorDetail.getLastName())){ // Both Current and Existing list directorDetail same
					
					if(isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD,"41001",
										errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					
					if(tranType==PennantConstants.TRAN_DEL){
						if(aDirectorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aDirectorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							customerDirectors.add(aDirectorDetail);
						}else if(aDirectorDetail.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aDirectorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aDirectorDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							customerDirectors.add(aDirectorDetail);
						}else if(aDirectorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getCustomerDialogCtrl().getCustomerDetails().getDirectorsList().size(); j++) {
								DirectorDetail detail =  getCustomerDialogCtrl().getCustomerDetails().getDirectorsList().get(j);
								if(detail.getCustID() == aDirectorDetail.getCustID() && 
										directorDetail.getFirstName().equals(aDirectorDetail.getFirstName()) && 
											directorDetail.getLastName().equals(aDirectorDetail.getLastName())){
									customerDirectors.add(detail);
								}
							}
						}
					}else{
						if(tranType!=PennantConstants.TRAN_UPD){
							customerDirectors.add(directorDetail);
						}
					}
				}else{
					customerDirectors.add(directorDetail);
				}
			}
		}
		if(!recordAdded){
			customerDirectors.add(aDirectorDetail);
		}
		return auditHeader;
	} 

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aDirectorDetail (DirectorDetail)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(DirectorDetail aDirectorDetail,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";
		
		aDirectorDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aDirectorDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aDirectorDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());
		
		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aDirectorDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aDirectorDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aDirectorDetail);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aDirectorDetail))) {
					try {
						if (!isNotes_Entered()){
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
				nextRoleCode= getWorkFlow().firstTask.owner;
			} else {
				String[] nextTasks = nextTaskId.split(";");
				
				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {
						
						if(nextRoleCode.length()>1){
							nextRoleCode =nextRoleCode+",";
						}
						nextRoleCode= getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode= getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aDirectorDetail.setTaskId(taskId);
			aDirectorDetail.setNextTaskId(nextTaskId);
			aDirectorDetail.setRoleCode(getRole());
			aDirectorDetail.setNextRoleCode(nextRoleCode);
			
			auditHeader =  getAuditHeader(aDirectorDetail, tranType);
			
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aDirectorDetail);
			
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aDirectorDetail, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aDirectorDetail, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	
	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;
		
		DirectorDetail aDirectorDetail = (DirectorDetail) auditHeader.getAuditDetail().getModelData();
		
		try {
			
			while(retValue==PennantConstants.porcessOVERIDE){
				
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getDirectorDetailService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getDirectorDetailService().saveOrUpdate(auditHeader);	
					}
					
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getDirectorDetailService().doApprove(auditHeader);

						if(aDirectorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doReject)){
						auditHeader = getDirectorDetailService().doReject(auditHeader);
						if(aDirectorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_DirectorDetailDialog,
								auditHeader);
						return processCompleted; 
					}
				}
				
				auditHeader =	ErrorControl.showErrorDetails(this.window_DirectorDetailDialog, 
						auditHeader);
				retValue = auditHeader.getProcessStatus();
				
				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes(),true);
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

	public void onClick$btnSearchCustGenderCode(Event event){

		String sCustGenderCode= this.custGenderCode.getValue();

		Object dataObject = ExtendedSearchListBox.show(this.window_DirectorDetailDialog,"Gender");
		if (dataObject instanceof String){
			this.custGenderCode.setValue(dataObject.toString());
			this.lovDescCustGenderCodeName.setValue("");
		}else{
			Gender details= (Gender) dataObject;
			if (details != null) {
				this.custGenderCode.setValue(details.getGenderCode());
				this.lovDescCustGenderCodeName.setValue(details.getGenderCode()
						+ "-" + details.getGenderDesc());
			}
		}
		if (!StringUtils.trimToEmpty(sCustGenderCode).equals(
				this.custGenderCode.getValue())) {
			this.custSalutationCode.setValue("");
			this.lovDescCustSalutationCodeName.setValue("");
		}
		if (this.custGenderCode.getValue() != "") {
			this.btnSearchCustSalutationCode.setDisabled(false);
		} else {
			this.btnSearchCustSalutationCode.setDisabled(true);
		}
	}

	public void onClick$btnSearchCustSalutationCode(Event event){

		Filter[] filters = new Filter[1];
		filters[0] = new Filter("SalutationGenderCode",
				this.custGenderCode.getValue(), Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(this.window_DirectorDetailDialog,"Salutation",
				filters);
		if (dataObject instanceof String){
			this.custSalutationCode.setValue(dataObject.toString());
			this.lovDescCustSalutationCodeName.setValue("");
		}else{
			Salutation details= (Salutation) dataObject;
			if (details != null) {
				this.custSalutationCode.setValue(details.getSalutationCode());
				this.lovDescCustSalutationCodeName.setValue(details.getSalutationCode() + "-"
						+ details.getSaluationDesc());
			}
		}
	}

	public void onClick$btnSearchCustAddrCity(Event event){
		logger.debug("Entering" + event.toString());

		Filter[] filters = new Filter[1];
		filters[0] = new Filter("PCProvince", this.custAddrProvince.getValue(),
				Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(
				this.window_DirectorDetailDialog, "City", filters);
		if (dataObject instanceof String) {
			this.custAddrCity.setValue(dataObject.toString());
			this.lovDescCustAddrCityName.setValue("");
		} else {
			City details = (City) dataObject;
			if (details != null) {
				this.custAddrCity.setValue(details.getPCCity());
				this.lovDescCustAddrCityName.setValue(details.getPCCity()
						+ "-" + details.getPCCityName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchCustAddrProvince(Event event){
		logger.debug("Entering" + event.toString());
		String sCustAddrProvince = this.custAddrProvince.getValue();
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("CPCountry", this.custAddrCountry.getValue(),
				Filter.OP_EQUAL);
		Object dataObject = ExtendedSearchListBox.show(
				this.window_DirectorDetailDialog, "Province", filters);
		if (dataObject instanceof String) {
			this.custAddrProvince.setValue(dataObject.toString());
			this.lovDescCustAddrProvinceName.setValue("");
		} else {
			Province details = (Province) dataObject;
			if (details != null) {
				this.custAddrProvince.setValue(details.getCPProvince());
				this.lovDescCustAddrProvinceName.setValue(details.getLovValue()
						+ "-" + details.getCPProvinceName());
			}
		}
		if (!StringUtils.trimToEmpty(sCustAddrProvince).equals(
				this.custAddrProvince.getValue())) {
			this.custAddrCity.setValue("");
			this.lovDescCustAddrCityName.setValue("");
			this.btnSearchCustAddrCity.setDisabled(true);
		}
		if (this.custAddrProvince.getValue() != "") {
			this.btnSearchCustAddrCity.setDisabled(false);
		} else {
			this.btnSearchCustAddrCity.setDisabled(true);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchCustAddrCountry(Event event){
		logger.debug("Entering" + event.toString());
		String sCustAddrCountry = this.custAddrCountry.getValue();

		Object dataObject = ExtendedSearchListBox.show(
				this.window_DirectorDetailDialog, "Country");
		if (dataObject instanceof String) {
			this.custAddrCountry.setValue(dataObject.toString());
			this.lovDescCustAddrCountryName.setValue("");
		} else {
			Country details = (Country) dataObject;
			if (details != null) {
				this.custAddrCountry.setValue(details.getCountryCode());
				this.lovDescCustAddrCountryName.setValue(details.getCountryCode()
						+ "-" + details.getCountryDesc());
			}
		}
		if (!StringUtils.trimToEmpty(sCustAddrCountry).equals(
				this.custAddrCountry.getValue())) {
			this.custAddrProvince.setValue("");
			this.custAddrCity.setValue("");
			this.lovDescCustAddrProvinceName.setValue("");
			this.lovDescCustAddrCityName.setValue("");
			this.btnSearchCustAddrCity.setDisabled(true);
		}
		if (this.custAddrCountry.getValue() != "") {
			this.btnSearchCustAddrProvince.setDisabled(false);
		} else {
			this.btnSearchCustAddrCity.setDisabled(true);
			this.btnSearchCustAddrProvince.setDisabled(true);
		}
		logger.debug("Leaving" + event.toString());
	}

 /**
    * Method for Calling list Of existed Customers
    * @param event
    * @throws SuspendNotAllowedException
    * @throws InterruptedException
    */
	public void onClick$btnSearchPRCustid(Event event) throws SuspendNotAllowedException, 
					InterruptedException{
		logger.debug("Entering" + event.toString());
		onload();
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * To load the customerSelect filter dialog
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void onload() throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();	
		map.put("DialogCtrl", this);
		map.put("filtertype","Extended");
		map.put("custCtgType","C");
		map.put("searchObject",this.newSearchObject);
		Executions.createComponents(
				"/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul",null,map);
		logger.debug("Leaving");
	}
	
	/**
	 * To set the customer id from Customer filter
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer,JdbcSearchObject<Customer> newSearchObject) 
						throws InterruptedException{
		logger.debug("Entering"); 
		final Customer aCustomer = (Customer)nCustomer; 		
		this.custID.setValue(aCustomer.getCustID());
		this.custCIF.setValue(aCustomer.getCustCIF().trim());
		this.custShrtName.setValue(aCustomer.getCustShrtName());
		this.newSearchObject = newSearchObject;
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(DirectorDetail aDirectorDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aDirectorDetail.getBefImage(), 
				aDirectorDetail);   
		return new AuditHeader(String.valueOf(aDirectorDetail.getDirectorId())
				,String.valueOf(aDirectorDetail.getCustID()),null,
				null,auditDetail,aDirectorDetail.getUserDetails(),getOverideMap());

	}
	
	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_DirectorDetailDialog, auditHeader);
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
		notes.setModuleName("DirectorDetail");
		notes.setReference(String.valueOf(getDirectorDetail().getDirectorId()));
		notes.setVersion(getDirectorDetail().getVersion());
		logger.debug("Leaving");
		return notes;
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

	public DirectorDetail getDirectorDetail() {
		return this.directorDetail;
	}
	public void setDirectorDetail(DirectorDetail directorDetail) {
		this.directorDetail = directorDetail;
	}

	public void setDirectorDetailService(DirectorDetailService directorDetailService) {
		this.directorDetailService = directorDetailService;
	}
	public DirectorDetailService getDirectorDetailService() {
		return this.directorDetailService;
	}

	public void setDirectorDetailListCtrl(DirectorDetailListCtrl directorDetailListCtrl) {
		this.directorDetailListCtrl = directorDetailListCtrl;
	}
	public DirectorDetailListCtrl getDirectorDetailListCtrl() {
		return this.directorDetailListCtrl;
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
	
	public void setCustomerSelectCtrl(CustomerSelectCtrl customerSelectctrl) {
		this.customerSelectCtrl = customerSelectctrl;
	}
	public CustomerSelectCtrl getCustomerSelectCtrl() {
		return customerSelectCtrl;
	}

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewCustomer() {
		return newCustomer;
	}
	public void setNewCustomer(boolean newCustomer) {
		this.newCustomer = newCustomer;
	}

	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}
	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}

}

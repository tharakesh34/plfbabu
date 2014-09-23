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
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.model.systemmasters.Designation;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.DirectorDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTPhoneNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.customermasters.customer.CustomerSelectCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

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
	protected Longbox directorID; 						// autowired
	protected Textbox firstName; 					// autowired
	protected Textbox lastName; 					// autowired
	protected Textbox shortName; 					// autowired
	protected Decimalbox sharePerc;                 // autowired
	protected Space space_SharePerc;                // autowired
	protected Combobox custGenderCode; 				// autowired
	protected Combobox custSalutationCode; 			// autowired
	protected Textbox custAddrHNbr; 				// autowired
	protected Textbox custFlatNbr;	 				// autowired
	protected Textbox custAddrStreet; 				// autowired
	protected Textbox custAddrLine1; 				// autowired
	protected Textbox custAddrLine2; 				// autowired
	protected Textbox custPOBox; 					// autowired
	protected ExtendedCombobox custAddrCity; 				// autowired
	protected ExtendedCombobox custAddrProvince; 			// autowired
	protected ExtendedCombobox custAddrCountry; 				// autowired
	protected Textbox custAddrZIP; 					// autowired
	protected Textbox custAddrPhone; 				// autowired
	protected Datebox custAddrFrom; 				// autowired
	protected Textbox custCIF;						// autowired
	protected Label   custShrtName;					// autowired
	protected Checkbox shareholder;                 // autowired
	protected Checkbox director;                    // autowired
	protected ExtendedCombobox designation;         // autowired
	protected ExtendedCombobox idType;                       // autowired
	protected Textbox idReference;                  // autowired
	protected ExtendedCombobox nationality;                  // autowired
	protected Datebox  dob;                  // autowired

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
	private transient boolean       oldVar_shareHolder;
	private transient boolean       oldVar_director;
	private transient String 		oldVar_designation;

	private transient String 		oldVar_idType;
	private transient String 		oldVar_idReference;
	private transient String 		oldVar_nationality;
	private transient Date  		oldVar_dob;
	
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
	
	
	private transient String 		oldVar_lovDescCustAddrCityName;
	private transient String 		oldVar_lovDescCustAddrProvinceName;
	private transient String 		oldVar_lovDescCustAddrCountryName;
	private transient String 		oldVar_lovDescNationalityName;
	private transient String 		oldVar_lovDescCustDocTypeName;
	
	// ServiceDAOs / Domain Classes
	private transient DirectorDetailService directorDetailService;
	private transient PagedListService pagedListService;
	private transient CustomerSelectCtrl customerSelectCtrl;

	private boolean newRecord=false;
	private boolean newCustomer=false;
	private CustomerDialogCtrl customerDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject ;
	protected Button btnSearchPRCustid;
	
	private List<DirectorDetail> directorDetailList;
	private List<ValueLabel>	genderCodes	      = PennantAppUtil.getGenderCodes();
	private String sCustGenderCode;
	private String sCustAddrCountry;
	private String sCustAddrProvince;
	protected Row Row_Gender;
	
	private BigDecimal totSharePerc;
	private String userRole="";
	private boolean isEnquiry = false;

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
        
		if(args.containsKey("totSharePerc")){
        	this.totSharePerc = (BigDecimal) args.get("totSharePerc");
        }
		
		if(args.containsKey("isEnquiry")){
			isEnquiry = (Boolean) args.get("isEnquiry");
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
		}
		doLoadWorkFlow(this.directorDetail.isWorkflow(),this.directorDetail.getWorkflowId(),
				this.directorDetail.getNextTaskId());
		
		if(args.containsKey("roleCode")){
			userRole = (String) args.get("roleCode");
			getUserWorkspace().alocateRoleAuthorities(userRole,"DirectorDetailDialog");
		}
		/* set components visible dependent of the users rights */
		doCheckRights();
		
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
		/*if(isNewRecord() & !isNewCustomer()){
			onload();
		}*/
		logger.debug("Leaving" + event.toString());
	}

	private void doCheckEnquiry() {
		if(isEnquiry){
			this.btnSave.setVisible(false);
			this.btnDelete.setVisible(false);
			this.btnHelp.setVisible(false);
		}
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.custID.setMaxlength(19);
		this.firstName.setMaxlength(50);
		this.lastName.setMaxlength(50);
		this.shortName.setMaxlength(50);
		this.custAddrHNbr.setMaxlength(50);
		this.custFlatNbr.setMaxlength(50);
		this.custAddrStreet.setMaxlength(50);
		this.custAddrLine1.setMaxlength(50);
		this.custAddrLine2.setMaxlength(50);
		this.custPOBox.setMaxlength(8);
		
		this.custAddrCity.setMaxlength(8);
		this.custAddrCity.setModuleName("City");
		this.custAddrCity.setValueColumn("PCCity");
		this.custAddrCity.setDescColumn("PCCityName");
		this.custAddrCity.setValidateColumns(new String[] { "PCCity" });
		
		this.custAddrProvince.setMaxlength(8);
		this.custAddrProvince.setModuleName("Province");
		this.custAddrProvince.setValueColumn("CPProvince");
		this.custAddrProvince.setDescColumn("CPProvinceName");
		this.custAddrProvince.setValidateColumns(new String[] { "CPProvince" });
		
		this.custAddrCountry.setMaxlength(2);
		this.custAddrCountry.setMandatoryStyle(true);
		this.custAddrCountry.setModuleName("Country");
		this.custAddrCountry.setValueColumn("CountryCode");
		this.custAddrCountry.setDescColumn("CountryDesc");
		this.custAddrCountry.setValidateColumns(new String[] {"CountryCode"});
		
		this.custAddrZIP.setMaxlength(10);
		this.custAddrPhone.setMaxlength(11);
	 	this.custAddrFrom.setFormat(PennantConstants.dateFormat);
	 	
		this.designation.setMaxlength(8);
		this.designation.setModuleName("Designation");
		this.designation.setValueColumn("DesgCode");
		this.designation.setDescColumn("DesgDesc");
		this.designation.setValidateColumns(new String[] { "DesgCode" });
		
		this.idType.setMaxlength(2);
		this.idType.setModuleName("CustDocumentType");
		this.idType.setValueColumn("DocTypeCode");
		this.idType.setDescColumn("DocTypeDesc");
		this.idType.setValidateColumns(new String[] { "DocTypeCode" });
		
		this.idReference.setMaxlength(35);
		
		this.nationality.setMaxlength(2);
		this.nationality.setMandatoryStyle(false);
		this.nationality.setModuleName("Country");
		this.nationality.setValueColumn("CountryCode");
		this.nationality.setDescColumn("CountryDesc");
		this.nationality.setValidateColumns(new String[] {"CountryCode"});
		
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
		
		getUserWorkspace().alocateAuthorities("DirectorDetailDialog",userRole);
		
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
			closePopUpWindow(this.window_DirectorDetailDialog,"DirectorDetailDialog");
		}
		logger.debug("Leaving") ;
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
		fillComboBox(this.custGenderCode, aDirectorDetail.getCustGenderCode(), genderCodes, "");
		fillComboBox(this.custSalutationCode, aDirectorDetail.getCustSalutationCode(), PennantAppUtil.getSalutationCodes(aDirectorDetail.getCustGenderCode()), "");
		if(aDirectorDetail.getCustID()!=Long.MIN_VALUE){
			this.custID.setValue(aDirectorDetail.getCustID());	
		}
		this.firstName.setValue(aDirectorDetail.getFirstName());
		this.lastName.setValue(aDirectorDetail.getLastName());
		this.shortName.setValue(StringUtils.trimToEmpty(aDirectorDetail.getShortName()));
		this.shareholder.setChecked(aDirectorDetail.isShareholder());
		this.director.setChecked(aDirectorDetail.isDirector());
		this.designation.setValue(aDirectorDetail.getDesignation());
		this.sharePerc.setValue(aDirectorDetail.getSharePerc());
		this.custAddrHNbr.setValue(aDirectorDetail.getCustAddrHNbr());
		this.custFlatNbr.setValue(aDirectorDetail.getCustFlatNbr());
		this.custAddrStreet.setValue(aDirectorDetail.getCustAddrStreet());
		this.custAddrLine1.setValue(aDirectorDetail.getCustAddrLine1());
		this.custAddrLine2.setValue(aDirectorDetail.getCustAddrLine2());
		this.custPOBox.setValue(aDirectorDetail.getCustPOBox());
		this.custAddrCity.setValue(aDirectorDetail.getCustAddrCity());
		this.custAddrProvince.setValue(aDirectorDetail.getCustAddrProvince());
		this.custAddrCountry.setValue(StringUtils.trimToEmpty(aDirectorDetail.getCustAddrCountry()));
		this.custAddrZIP.setValue(aDirectorDetail.getCustAddrZIP());
		this.custAddrPhone.setValue(aDirectorDetail.getCustAddrPhone());
		this.custAddrFrom.setValue(aDirectorDetail.getCustAddrFrom());
		this.custCIF.setValue(aDirectorDetail.getLovDescCustCIF()==null?"":
			aDirectorDetail.getLovDescCustCIF().trim());
		this.custShrtName.setValue(aDirectorDetail.getLovDescCustShrtName()==null?"":
			aDirectorDetail.getLovDescCustShrtName().trim());
		this.idType.setValue(StringUtils.trimToEmpty(aDirectorDetail.getIdType()));
		this.idReference.setValue(StringUtils.trimToEmpty(aDirectorDetail.getIdReference()));
		this.nationality.setValue(StringUtils.trimToEmpty(aDirectorDetail.getNationality()));
		this.dob.setValue(aDirectorDetail.getDob());
		if (isNewRecord()){
			this.custAddrCity.setDescription("");
			this.custAddrProvince.setDescription("");
			this.custAddrCountry.setDescription("");
			this.designation.setDescription("");
			this.nationality.setDescription("");
			this.idType.setDescription("");
		}else{
				this.custAddrCity.setDescription(aDirectorDetail.getLovDescCustAddrCityName());
				this.custAddrProvince.setDescription(aDirectorDetail.getLovDescCustAddrProvinceName());
				this.custAddrCountry.setDescription(aDirectorDetail.getLovDescCustAddrCountryName());
				this.designation.setDescription(aDirectorDetail.getLovDescDesignationName());
				this.nationality.setDescription(aDirectorDetail.getLovDescNationalityName());
				this.idType.setDescription(aDirectorDetail.getLovDescCustDocCategoryName());
				isShareHolderChecked(aDirectorDetail.isShareholder());
				isDirectorChecked(aDirectorDetail.isDirector());
		}
		if(aDirectorDetail.getSharePerc() != null && aDirectorDetail.getSharePerc().intValue() != Integer.valueOf(0)){
			this.shareholder.setChecked(true);
		}
		if(!StringUtils.trimToEmpty(aDirectorDetail.getDesignation()).equals("")){
			this.director.setChecked(true);
		}
		this.custAddrProvince.setFilters(new Filter[]{new Filter("CPCountry", this.custAddrCountry.getValue(),Filter.OP_EQUAL)});
		this.custAddrCity.setFilters(new Filter[]{new Filter("PCCountry", this.custAddrCountry.getValue(),Filter.OP_EQUAL),
				             new Filter("PCProvince", this.custAddrProvince.getValue(),Filter.OP_EQUAL)});
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
			if(getComboboxValue(this.custGenderCode).equals("#")) {
				aDirectorDetail.setCustGenderCode("");				
			}else{
				aDirectorDetail.setCustGenderCode(getComboboxValue(this.custGenderCode));	
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(getComboboxValue(this.custSalutationCode).equals("#")) {
				aDirectorDetail.setCustSalutationCode("");	
			}else{
				aDirectorDetail.setCustSalutationCode(getComboboxValue(this.custSalutationCode));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if((!this.shareholder.isDisabled() || !this.director.isDisabled()) && (!this.shareholder.isChecked() && !this.director.isChecked())){
				throw new WrongValueException(this.shareholder, Labels.getLabel("label_DirectorDetailDialog_ShareOrDirector.value"));
			}
			if(this.sharePerc.getValue() != null){
			if((this.totSharePerc.add(this.sharePerc.getValue())).compareTo(new BigDecimal(100)) > 0){
				BigDecimal availableSharePerc = new BigDecimal(100).subtract(this.totSharePerc);
				throw new WrongValueException(this.sharePerc, Labels.getLabel("Total_Percentage",
						new String[] { Labels.getLabel("label_DirectorDetailDialog_SharePerc.value"),availableSharePerc.toString() }));
			}
			}
			aDirectorDetail.setSharePerc(this.sharePerc.getValue() == null ? BigDecimal.ZERO : this.sharePerc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
	 		aDirectorDetail.setLovDescCustAddrCityName(this.custAddrCity.getDescription());
	 		aDirectorDetail.setCustAddrCity(this.custAddrCity.getValidatedValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		aDirectorDetail.setLovDescCustAddrProvinceName(this.custAddrProvince.getDescription());
	 		aDirectorDetail.setCustAddrProvince(this.custAddrProvince.getValidatedValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		aDirectorDetail.setLovDescCustAddrCountryName(this.custAddrCountry.getDescription());
	 		aDirectorDetail.setCustAddrCountry(this.custAddrCountry.getValidatedValue());	
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
		if(this.custAddrFrom.getValue() != null){
			Date appDate = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
			if(appDate.compareTo(this.custAddrFrom.getValue()) != 1){
				throw new WrongValueException(this.custAddrFrom, Labels.getLabel("NUMBER_MAXVALUE",
						new String[] {Labels.getLabel("label_DirectorDetailDialog_CustAddrFrom.value"), 
						"Application Date"}));
			}
			aDirectorDetail.setCustAddrFrom(new Timestamp(this.custAddrFrom.getValue().getTime()));
		}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		aDirectorDetail.setShareholder(this.shareholder.isChecked());
		aDirectorDetail.setDirector(this.director.isChecked());
		
		try {
		    aDirectorDetail.setLovDescDesignationName(this.designation.getDescription());
		    aDirectorDetail.setDesignation(this.designation.getValidatedValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			aDirectorDetail.setLovDescCustDocCategoryName(this.idType.getDescription());
			aDirectorDetail.setIdType(this.idType.getValidatedValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			aDirectorDetail.setIdReference(this.idReference.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			aDirectorDetail.setLovDescNationalityName(this.nationality.getDescription());
			aDirectorDetail.setNationality(this.nationality.getValidatedValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			aDirectorDetail.setDob(this.dob.getValue());
			if(this.dob.getValue() != null){
			if (DateUtility.compare(this.dob.getValue(),
					(Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR)) != -1) {
				throw new WrongValueException(this.dob,Labels.getLabel("DATE_FUTURE_TODAY",
						new String[] {Labels.getLabel("label_DirectorDetailDialog_DOB.value") }));
			}
			}
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
			doCheckEnquiry();
			if(isNewCustomer()){
				this.window_DirectorDetailDialog.setHeight("530px");
				this.window_DirectorDetailDialog.setWidth("85%");
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
		this.oldVar_lastName = this.lastName.getValue();
		this.oldVar_shortName = this.shortName.getValue();
 		this.oldVar_custGenderCode = this.custGenderCode.getValue();
 		this.oldVar_custSalutationCode = this.custSalutationCode.getValue();
		this.oldVar_custAddrHNbr = this.custAddrHNbr.getValue();
		this.oldVar_custFlatNbr = this.custFlatNbr.getValue();
		this.oldVar_custAddrStreet = this.custAddrStreet.getValue();
		this.oldVar_custAddrLine1 = this.custAddrLine1.getValue();
		this.oldVar_custAddrLine2 = this.custAddrLine2.getValue();
		this.oldVar_custPOBox = this.custPOBox.getValue();
 		this.oldVar_custAddrCity = this.custAddrCity.getValue();
 		this.oldVar_lovDescCustAddrCityName = this.custAddrCity.getDescription();
 		this.oldVar_custAddrProvince = this.custAddrProvince.getValue();
 		this.oldVar_lovDescCustAddrProvinceName = this.custAddrProvince.getDescription();
 		this.oldVar_custAddrCountry = this.custAddrCountry.getValue();
 		this.oldVar_lovDescCustAddrCountryName = this.custAddrCountry.getDescription();
		this.oldVar_custAddrZIP = this.custAddrZIP.getValue();
		this.oldVar_custAddrPhone = this.custAddrPhone.getValue();
		this.oldVar_custAddrFrom = PennantAppUtil.getTimestamp(this.custAddrFrom.getValue());
		this.oldVar_shareHolder = this.shareholder.isChecked();
		this.oldVar_director = this.director.isChecked();
		this.oldVar_designation = this.designation.getValue();
		this.oldVar_idType = this.idType.getValue();
		this.oldVar_idReference = this.idReference.getValue();
		this.oldVar_nationality = this.nationality.getValue();
		this.oldVar_dob = this.dob.getValue();
		this.oldVar_lovDescCustDocTypeName = this.idType.getDescription();
		this.oldVar_lovDescNationalityName = this.nationality.getDescription();
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
		this.lastName.setValue(this.oldVar_lastName);
		this.shortName.setValue(this.oldVar_shortName);
 		this.custGenderCode.setValue(this.oldVar_custGenderCode);
 		this.custSalutationCode.setValue(this.oldVar_custSalutationCode);
		this.custAddrHNbr.setValue(this.oldVar_custAddrHNbr);
		this.custFlatNbr.setValue(this.oldVar_custFlatNbr);
		this.custAddrStreet.setValue(this.oldVar_custAddrStreet);
		this.custAddrLine1.setValue(this.oldVar_custAddrLine1);
		this.custAddrLine2.setValue(this.oldVar_custAddrLine2);
		this.custPOBox.setValue(this.oldVar_custPOBox);
 		this.custAddrCity.setValue(this.oldVar_custAddrCity);
 		this.custAddrCity.setDescription(this.oldVar_lovDescCustAddrCityName);
 		this.custAddrProvince.setValue(this.oldVar_custAddrProvince);
 		this.custAddrProvince.setDescription(this.oldVar_lovDescCustAddrProvinceName);
 		this.custAddrCountry.setValue(this.oldVar_custAddrCountry);
 		this.custAddrCountry.setDescription(this.oldVar_lovDescCustAddrCountryName);
		this.custAddrZIP.setValue(this.oldVar_custAddrZIP);
		this.custAddrPhone.setValue(this.oldVar_custAddrPhone);
		this.custAddrFrom.setValue(this.oldVar_custAddrFrom);
		this.shareholder.setChecked(this.oldVar_shareHolder);
		this.director.setChecked(this.oldVar_director);
		this.designation.setValue(this.oldVar_designation);
		this.idType.setValue(this.oldVar_idType);
		this.idReference.setValue(this.oldVar_idReference);
		this.nationality.setValue(this.oldVar_nationality);
		this.dob.setValue(this.oldVar_dob);
		this.nationality.setDescription(this.oldVar_lovDescNationalityName);
		this.idType.setDescription(this.oldVar_lovDescCustDocTypeName);
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
		if (this.oldVar_shareHolder != this.shareholder.isChecked()) {
			return true;
		}
		if (this.oldVar_director != this.director.isChecked()) {
			return true;
		}
		if (this.oldVar_designation != this.designation.getValue()) {
			return true;
		}
		if (this.oldVar_idType != this.idType.getValue()) {
			return true;
		}
		if (this.oldVar_idReference != this.idReference.getValue()) {
			return true;
		}
		if (this.oldVar_nationality != this.nationality.getValue()) {
			return true;
		}
		if (this.oldVar_dob != this.dob.getValue()) {
			return true;
		}
		String oldCustAddrFrom = "";
		String newCustAddrFrom = "";
		if (this.oldVar_custAddrFrom != null) {
			oldCustAddrFrom = DateUtility.formatDate(this.oldVar_custAddrFrom,PennantConstants.dateFormat);
		}
		if (this.custAddrFrom.getValue() != null) {
			newCustAddrFrom = DateUtility.formatDate(this.custAddrFrom.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldCustAddrFrom).equals(StringUtils.trimToEmpty(newCustAddrFrom))) {
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
		clearShareDirector();
		if (!this.custID.isReadonly()){
			this.custCIF.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_DirectorDetailDialog_CustID.value")}));
		}
		if (!this.firstName.isReadonly()){
			this.firstName.setConstraint(new PTStringValidator(Labels.getLabel("label_DirectorDetailDialog_FirstName.value"),
					PennantRegularExpressions.REGEX_NAME, false));
		}	
		if (!this.lastName.isReadonly()){
			this.lastName.setConstraint(new PTStringValidator(Labels.getLabel("label_DirectorDetailDialog_LastName.value"),
					PennantRegularExpressions.REGEX_NAME, false));
		}	
		if (!this.shortName.isReadonly()){
			this.shortName.setConstraint(new PTStringValidator(Labels.getLabel("label_DirectorDetailDialog_ShortName.value"), 
					PennantRegularExpressions.REGEX_NAME, false));
		}
		if (!this.firstName.isReadonly() && !this.lastName.isReadonly() && !this.shortName.isReadonly()){
			if(StringUtils.trimToEmpty(this.firstName.getValue()).equals("") && StringUtils.trimToEmpty(this.lastName.getValue()).equals("")
					&& StringUtils.trimToEmpty(this.shortName.getValue()).equals("")){
				
				this.shortName.setConstraint(new PTStringValidator(Labels.getLabel("label_DirectorDetailDialog_AnyName.value"), 
						PennantRegularExpressions.REGEX_NAME, true));
			}
		}
		if(this.shareholder.isChecked()){
			if(!this.sharePerc.isReadonly() && !this.sharePerc.isDisabled()){
				this.sharePerc.setConstraint("NO EMPTY,NO ZERO:" + Labels.getLabel("FIELD_NO_EMPTY",
						new String[]{Labels.getLabel("label_DirectorDetailDialog_SharePerc.value")}));
			}
		}
		if(this.director.isChecked()){
			if(!this.designation.isReadonly()){
				this.designation.setConstraint("NO EMPTY,NO ZERO:" + Labels.getLabel("FIELD_NO_EMPTY",
						new String[]{Labels.getLabel("label_DirectorDetailDialog_Designation.value")}));
			}
		}
		if (!this.custAddrHNbr.isReadonly()){
			this.custAddrHNbr.setConstraint(new PTStringValidator(Labels.getLabel("label_DirectorDetailDialog_CustAddrHNbr.value"),
					PennantRegularExpressions.REGEX_ADDRESS, false));
		}	
		if (!this.custFlatNbr.isReadonly()){
			this.custFlatNbr.setConstraint(new PTStringValidator(Labels.getLabel("label_DirectorDetailDialog_CustFlatNbr.value"),
					PennantRegularExpressions.REGEX_ADDRESS, false));
		}	
		if (!this.custAddrStreet.isReadonly()){
			this.custAddrStreet.setConstraint(new PTStringValidator(Labels.getLabel("label_DirectorDetailDialog_CustAddrStreet.value"),PennantRegularExpressions.REGEX_ADDRESS, false));
		}	
		if (!this.custAddrLine1.isReadonly()){
			this.custAddrLine1.setConstraint(new PTStringValidator(Labels.getLabel("label_DirectorDetailDialog_CustAddrLine1.value"),PennantRegularExpressions.REGEX_ADDRESS, false));
		}	
		if (!this.custAddrLine2.isReadonly()){
			this.custAddrLine2.setConstraint(new PTStringValidator(Labels.getLabel("label_DirectorDetailDialog_CustAddrLine2.value"),PennantRegularExpressions.REGEX_ADDRESS, false));
		}	
		if (!this.custPOBox.isReadonly()){
			this.custPOBox.setConstraint(new PTStringValidator(Labels.getLabel("label_DirectorDetailDialog_CustPOBox.value"),
					PennantRegularExpressions.REGEX_NUMERIC, false));
		}	
		if (!this.custAddrZIP.isReadonly()){
			this.custAddrZIP.setConstraint(new PTStringValidator(Labels.getLabel("label_DirectorDetailDialog_CustAddrZIP.value"), PennantRegularExpressions.REGEX_ZIP, false));
		}	
		if (!this.custAddrPhone.isReadonly()){
			this.custAddrPhone.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_DirectorDetailDialog_CustAddrPhone.value"),false));
		}	
		if (!this.idReference.isReadonly()){
			this.idReference.setConstraint(new PTStringValidator(Labels.getLabel("label_DirectorDetailDialog_IDReference.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, false));
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
		this.designation.setConstraint("");
		this.idType.setConstraint("");
		this.idReference.setConstraint("");
		this.nationality.setConstraint("");
		this.dob.setConstraint("");
		logger.debug("Leaving");
	}
	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.custAddrCountry.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[]{Labels.getLabel("label_DirectorDetailDialog_CustAddrCountry.value")}));
		logger.debug("Leaving");
	}
	
	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.custAddrCity.setConstraint("");
		this.custAddrProvince.setConstraint("");
		this.custAddrCountry.setConstraint("");
		logger.debug("Leaving");
	}
	
	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.sharePerc.setErrorMessage("");
		this.custCIF.setErrorMessage("");
		this.firstName.setErrorMessage("");
		this.lastName.setErrorMessage("");
		this.shortName.setErrorMessage("");
		this.custGenderCode.setErrorMessage("");
		this.custSalutationCode.setErrorMessage("");
		this.custAddrHNbr.setErrorMessage("");
		this.custFlatNbr.setErrorMessage("");
		this.custAddrStreet.setErrorMessage("");
		this.custAddrLine1.setErrorMessage("");
		this.custAddrLine2.setErrorMessage("");
		this.custPOBox.setErrorMessage("");
		this.custAddrCity.setErrorMessage("");
		this.custAddrProvince.setErrorMessage("");
		this.custAddrCountry.setErrorMessage("");
		this.custAddrZIP.setErrorMessage("");
		this.custAddrPhone.setErrorMessage("");
		this.custAddrFrom.setErrorMessage("");
		this.designation.setErrorMessage("");
		this.idType.setErrorMessage("");
		this.idReference.setErrorMessage("");
		this.nationality.setErrorMessage("");
		this.dob.setErrorMessage("");
		logger.debug("Leaving");
	
	}
	
	// Method for refreshing the list after successful updating
	private void refreshList() {
		logger.debug("Entering");
		final JdbcSearchObject<DirectorDetail> soDirectorDetail = getDirectorDetailListCtrl().getSearchObj();
		getDirectorDetailListCtrl().pagingDirectorDetailList.setActivePage(0);
		getDirectorDetailListCtrl().getPagedListWrapper().setSearchObject(soDirectorDetail);
		if (getDirectorDetailListCtrl().listBoxDirectorDetail != null) {
			getDirectorDetailListCtrl().listBoxDirectorDetail.getListModel();
		}
		logger.debug("Leaving");
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
		String name = "";
        if(!StringUtils.trimToEmpty(aDirectorDetail.getShortName()).equals("")){
        	name = aDirectorDetail.getShortName();
        }else{
        	 name = aDirectorDetail.getFirstName() + "  " + aDirectorDetail.getLastName();
        }
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + name;
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		
		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aDirectorDetail.getRecordType()).equals("")){
				aDirectorDetail.setVersion(aDirectorDetail.getVersion()+1);
				aDirectorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if(getCustomerDialogCtrl() != null &&  getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow()){
						aDirectorDetail.setNewRecord(true);	
				}
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
					AuditHeader auditHeader =  newDirectorProcess(aDirectorDetail,tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_DirectorDetailDialog,
							auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue==PennantConstants.porcessCONTINUE || 
							retValue==PennantConstants.porcessOVERIDE){
						getCustomerDialogCtrl().doFillCustomerDirectory(this.directorDetailList);
						// send the data back to customer
						closePopUpWindow(this.window_DirectorDetailDialog,"DirectorDetailDialog");
					}	

				}else if(doProcess(aDirectorDetail,tranType)){
					refreshList();
					closePopUpWindow(this.window_DirectorDetailDialog,"DirectorDetailDialog");
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
			this.custSalutationCode.setDisabled(true);

		}else{
			this.btnCancel.setVisible(true);
			this.btnSearchPRCustid.setVisible(false);
			this.custSalutationCode.setDisabled(isReadOnly("DirectorDetailDialog_custSalutationCode"));
			this.custAddrCity.setReadonly(isReadOnly("DirectorDetailDialog_custAddrCity"));
			this.custAddrProvince.setReadonly(isReadOnly("DirectorDetailDialog_custAddrProvince"));
		}

		this.custCIF.setReadonly(true);
		this.shortName.setReadonly(isReadOnly("DirectorDetailDialog_shortName"));
		this.firstName.setReadonly(isReadOnly("DirectorDetailDialog_firstName"));
		this.lastName.setReadonly(isReadOnly("DirectorDetailDialog_lastName"));
		this.custGenderCode.setDisabled(isReadOnly("DirectorDetailDialog_custGenderCode"));
		this.custAddrHNbr.setReadonly(isReadOnly("DirectorDetailDialog_custAddrHNbr"));
		this.custFlatNbr.setReadonly(isReadOnly("DirectorDetailDialog_custFlatNbr"));
		this.custAddrStreet.setReadonly(isReadOnly("DirectorDetailDialog_custAddrStreet"));
		this.custAddrLine1.setReadonly(isReadOnly("DirectorDetailDialog_custAddrLine1"));
		this.custAddrLine2.setReadonly(isReadOnly("DirectorDetailDialog_custAddrLine2"));
		this.custPOBox.setReadonly(isReadOnly("DirectorDetailDialog_custPOBox"));
		this.custAddrCountry.setReadonly(isReadOnly("DirectorDetailDialog_custAddrCountry"));
		this.custAddrCountry.setMandatoryStyle(!(isReadOnly("DirectorDetailDialog_custAddrCountry")));
		this.custAddrZIP.setReadonly(isReadOnly("DirectorDetailDialog_custAddrZIP"));
		this.custAddrPhone.setReadonly(isReadOnly("DirectorDetailDialog_custAddrPhone"));
		this.custAddrFrom.setDisabled(isReadOnly("DirectorDetailDialog_custAddrFrom"));
		this.sharePerc.setReadonly(isReadOnly("DirectorDetailDialog_sharePerc"));
		this.shareholder.setDisabled(isReadOnly("DirectorDetailDialog_shareholder"));
		this.director.setDisabled(isReadOnly("DirectorDetailDialog_director"));
		this.designation.setReadonly(isReadOnly("DirectorDetailDialog_designation"));
		this.idType.setReadonly(isReadOnly("DirectorDetailDialog_idType"));
		this.idReference.setReadonly(isReadOnly("DirectorDetailDialog_idReference"));
		this.nationality.setReadonly(isReadOnly("DirectorDetailDialog_nationality"));
		this.dob.setDisabled(isReadOnly("DirectorDetailDialog_dob"));

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
		boolean isCustomerWorkflow = false;
		if(getCustomerDialogCtrl() != null){
			isCustomerWorkflow = getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow();
		}
		if (isWorkFlowEnabled() || isCustomerWorkflow){
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
		this.lastName.setReadonly(true);
		this.shortName.setReadonly(true);
		this.custGenderCode.setDisabled(true);
		this.custSalutationCode.setDisabled(true);
		this.custAddrHNbr.setReadonly(true);
		this.custFlatNbr.setReadonly(true);
		this.custAddrStreet.setReadonly(true);
		this.custAddrLine1.setReadonly(true);
		this.custAddrLine2.setReadonly(true);
		this.custPOBox.setReadonly(true);
		this.custAddrCity.setReadonly(true);
		this.custAddrProvince.setReadonly(true);
		this.custAddrCountry.setReadonly(true);
		this.custAddrZIP.setReadonly(true);
		this.custAddrPhone.setReadonly(true);
		this.custAddrFrom.setDisabled(true);
		this.shareholder.setDisabled(true);
		this.director.setDisabled(true);
		this.designation.setReadonly(true);
		this.idType.setReadonly(true);
		this.idReference.setReadonly(true);
		this.nationality.setReadonly(true);
		this.dob.setDisabled(true);
		this.sharePerc.setDisabled(true);

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
		this.sharePerc.setValue("");
		this.custCIF.setValue("");
		this.custShrtName.setValue("");
		this.firstName.setValue("");
		this.lastName.setValue("");
		this.shortName.setValue("");
		this.custGenderCode.setValue("");
		this.custSalutationCode.setValue("");
		this.custSalutationCode.setValue("");
		this.custAddrHNbr.setValue("");
		this.custFlatNbr.setValue("");
		this.custAddrStreet.setValue("");
		this.custAddrLine1.setValue("");
		this.custAddrLine2.setValue("");
		this.custPOBox.setValue("");
		this.custAddrCity.setValue("");
		this.custAddrCity.setDescription("");
		this.custAddrProvince.setValue("");
		this.custAddrProvince.setDescription("");
		this.custAddrCountry.setValue("");
		this.custAddrCountry.setDescription("");
		this.custAddrZIP.setValue("");
		this.custAddrPhone.setValue("");
		this.custAddrFrom.setText("");
		this.shareholder.setChecked(false);
		this.director.setChecked(false);
		this.designation.setValue("");
		this.idType.setValue("");
		this.idType.setDescription("");
		this.idReference.setValue("");
		this.nationality.setValue("");
		this.nationality.setDescription("");
		this.dob.setText("");
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
				AuditHeader auditHeader =  newDirectorProcess(aDirectorDetail,tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_DirectorDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || 
						retValue==PennantConstants.porcessOVERIDE){
					getCustomerDialogCtrl().doFillCustomerDirectory(this.directorDetailList);
					// send the data back to customer
					closePopUpWindow(this.window_DirectorDetailDialog,"DirectorDetailDialog");
				}
			}else if(doProcess(aDirectorDetail,tranType)){
				refreshList();
				// Close the Existing Dialog
				closePopUpWindow(this.window_DirectorDetailDialog,"DirectorDetailDialog");
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newDirectorProcess(DirectorDetail aDirectorDetail,String tranType){
		boolean recordAdded=false;
		
		AuditHeader auditHeader= getAuditHeader(aDirectorDetail, tranType);
		directorDetailList = new ArrayList<DirectorDetail>();
		
		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aDirectorDetail.getLovDescCustCIF());
		String name = "";
        if(!StringUtils.trimToEmpty(aDirectorDetail.getShortName()).equals("")){
        	name = aDirectorDetail.getShortName();
        }else{
        	 name = aDirectorDetail.getFirstName() + "  " + aDirectorDetail.getLastName();
        }
		valueParm[1] = String.valueOf(name);;

		errParm[0] = PennantJavaUtil.getLabel("label_CustID") + ":"+ valueParm[0] + " , ";
		errParm[1] = PennantJavaUtil.getLabel("label_DirectorDetailDialog_ShortName.value") + ":"+valueParm[1];
		
		if(getCustomerDialogCtrl().getDirectorList()!=null && getCustomerDialogCtrl().getDirectorList().size()>0){
			for (int i = 0; i < getCustomerDialogCtrl().getDirectorList().size(); i++) {
				DirectorDetail directorDetail = getCustomerDialogCtrl().getDirectorList().get(i);
				
				if(aDirectorDetail.getId() == directorDetail.getId()){ // Both Current and Existing list director same
					if(isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					
					if(tranType==PennantConstants.TRAN_DEL){
						if(aDirectorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aDirectorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							directorDetailList.add(aDirectorDetail);
						}else if(aDirectorDetail.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aDirectorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aDirectorDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							directorDetailList.add(aDirectorDetail);
						}else if(aDirectorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getCustomerDialogCtrl().getCustomerDetails().getCustomerDirectorList().size(); j++) {
								DirectorDetail director =  getCustomerDialogCtrl().getCustomerDetails().getCustomerDirectorList().get(j);
								if(director.getCustID() == aDirectorDetail.getCustID() && director.getId() == aDirectorDetail.getId()){
									directorDetailList.add(director);
								}
							}
						}
					}else{
						if(tranType!=PennantConstants.TRAN_UPD){
							directorDetailList.add(directorDetail);
						}
					}
				}else{
					directorDetailList.add(directorDetail);
				}
			}
		}
		if(!recordAdded){
			directorDetailList.add(aDirectorDetail);
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

	public void onSelect$custGenderCode(Event event){
		logger.debug("Entering");
		if (!StringUtils.trimToEmpty(sCustGenderCode).equals(
				this.custGenderCode.getValue())) {
			this.custSalutationCode.setValue("");
		}
		if (this.custGenderCode.getValue() != "") {
			this.custSalutationCode.setDisabled(false);
		} else {
			this.custSalutationCode.setDisabled(true);
		}
		sCustGenderCode = this.custGenderCode.getValue();
		String genderCodeTemp = this.custGenderCode.getSelectedItem().getValue().toString();
		fillComboBox(this.custSalutationCode, this.custSalutationCode.getValue(), PennantAppUtil.getSalutationCodes(genderCodeTemp), "");
		logger.debug("Leaving");
	}


	public void onFulfill$custAddrProvince(Event event){
		logger.debug("Entering" + event.toString());
		this.custAddrCity.setErrorMessage("");
		if (!StringUtils.trimToEmpty(sCustAddrProvince).equals(
				this.custAddrProvince.getValue())) {
			this.custAddrCity.setValue("");
			this.custAddrCity.setDescription("");
			this.custAddrCity.setFocus(true);
		}
		sCustAddrProvince = this.custAddrProvince.getValue();
		Filter[] filtersCity = new Filter[2];
		filtersCity[0] = new Filter("PCCountry", this.custAddrCountry.getValue(),Filter.OP_EQUAL);
		filtersCity[1] = new Filter("PCProvince", this.custAddrProvince.getValue(),Filter.OP_EQUAL);
		this.custAddrCity.setFilters(filtersCity);
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$custAddrCountry(Event event){
		logger.debug("Entering" + event.toString());
		this.custAddrProvince.setErrorMessage("");
		this.custAddrCity.setErrorMessage("");
		if (!StringUtils.trimToEmpty(sCustAddrCountry).equals(
				this.custAddrCountry.getValue())) {
			this.custAddrProvince.setValue("");
			this.custAddrCity.setValue("");
			this.custAddrProvince.setDescription("");
			this.custAddrCity.setDescription("");
		}
		sCustAddrCountry = this.custAddrCountry.getValue();
		Filter[] filtersProvince = new Filter[1];
		filtersProvince[0] = new Filter("CPCountry", this.custAddrCountry.getValue(),
				Filter.OP_EQUAL);
		this.custAddrProvince.setFilters(filtersProvince);
		logger.debug("Leaving" + event.toString());
	}
	
	public void onFulfill$designation(Event event) {
		logger.debug("Entering");
		clearShareDirector();
		Object dataObject = designation.getObject();
		if (dataObject instanceof String) {
			//
		} else {
			Designation details = (Designation) dataObject;
			if (details != null) {
				this.director.setChecked(true);
				isDirectorChecked(true);
			}
		}
		logger.debug("Leaving");
	}
	
	public void onChange$sharePerc(Event event){
		logger.debug("Entering");
		if(this.sharePerc.getValue() != null){
		clearShareDirector();
		isShareHolderChecked(true);
		this.shareholder.setChecked(true);
		}
		logger.debug("Leaving");
	}
	
	public void clearShareDirector(){
		doClearMessage();
		Clients.clearWrongValue(this.shortName);
		Clients.clearWrongValue(this.shareholder);
		Clients.clearWrongValue(this.sharePerc);
		Clients.clearWrongValue(this.designation);
		this.sharePerc.setConstraint("");
		this.designation.setConstraint("");
		this.shortName.setConstraint(""); 	
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
	
	
	private void isShareHolderChecked(boolean isShareHolder){
		logger.debug("Entering"); 
		if(isShareHolder){
			this.space_SharePerc.setSclass("mandatory");
		}else{ 
			this.space_SharePerc.setSclass("");
		}
		logger.debug("Leaving"); 
	}
	
	private void isDirectorChecked(boolean isDirector){
		logger.debug("Entering"); 
		if(isDirector){
			this.designation.setMandatoryStyle(true);
		}else{ 
			this.designation.setMandatoryStyle(false);
		}
		logger.debug("Leaving"); 
	}
	
	public void onCheck$shareholder(Event event){
		logger.debug("Entering"); 
		Clients.clearWrongValue(this.shareholder);
		Clients.clearWrongValue(this.sharePerc);
		isShareHolderChecked(this.shareholder.isChecked());
		logger.debug("Leaving"); 
	}
	public void onCheck$director(Event event){
		logger.debug("Entering"); 
		Clients.clearWrongValue(this.shareholder);
		this.designation.setErrorMessage("");
		isDirectorChecked(this.director.isChecked());
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
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
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

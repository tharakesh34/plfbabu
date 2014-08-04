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
 * FileName    		:  CommodityBrokerDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-11-2011    														*
 *                                                                  						*
 * Modified Date    :  10-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.commodity.commoditybrokerdetail;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.zkoss.zul.Row;
import org.zkoss.zul.SimpleConstraint;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.finance.commodity.CommodityBrokerDetail;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.finance.commodity.CommodityBrokerDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTEmailValidator;
import com.pennant.util.Constraint.PTPhoneNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance.Commodity/CommodityBrokerDetail/CommodityBrokerDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CommodityBrokerDetailDialogCtrl extends GFCBaseCtrl implements Serializable {


	private static final long serialVersionUID = -4697540691852649079L;
	private final static Logger logger = Logger.getLogger(CommodityBrokerDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected  Window  window_CommodityBrokerDetailDialog; // autoWired
	protected  Textbox brokerCode;                         // autoWired
	protected  Longbox brokerCustID;                       // autoWired
	protected  Textbox brokerAddrHNbr;                     // autoWired
	protected  Textbox brokerAddrFlatNbr;                  // autoWired
	protected  Textbox brokerAddrStreet;                   // autoWired
	protected  Textbox brokerAddrLane1;                    // autoWired
	protected  Textbox brokerAddrLane2;                    // autoWired
	protected  Textbox brokerAddrPOBox;                    // autoWired
	protected  ExtendedCombobox brokerAddrCountry;                  // autoWired
	protected  ExtendedCombobox brokerAddrProvince;                 // autoWired
	protected  ExtendedCombobox brokerAddrCity;                     // autoWired
	protected  Textbox brokerAddrZIP;                      // autoWired
	protected  Textbox brokerAddrPhone;                    // autoWired
	protected  Textbox brokerAddrFax;                      // autoWired
	protected  Textbox brokerEmail;                        // autoWired
	protected  Textbox agreementRef;                       // autoWired

	protected  Datebox brokerFrom;                         // autoWired

	protected  Textbox lovDescBrokerCIF;                   // autoWired

	protected  Label   recordStatus;                       // autoWired
	protected  Textbox   brokerShtName;                      // autoWired
	//buttons 
	protected Button    btnNew;                            // autoWired
	protected Button    btnEdit;                           // autoWired
	protected Button    btnDelete;                         // autoWired
	protected Button    btnSave;                           // autoWired
	protected Button    btnCancel;                         // autoWired
	protected Button    btnClose;                          // autoWired
	protected Button    btnHelp;                           // autoWired
	protected Button    btnNotes;                          // autoWired
	protected Button    btnSearchBrokerCustID;             // autoWired
	protected Button    btnSearchAddress;                  // autoWired

	protected Radiogroup userAction;
	protected Groupbox   groupboxWf;

	// not auto wired variables
	private CommodityBrokerDetail commodityBrokerDetail;                           // overHanded per parameters
	private CommodityBrokerDetail prvCommodityBrokerDetail;                        // overHanded per parameters
	private transient CommodityBrokerDetailListCtrl commodityBrokerDetailListCtrl; // overHanded per parameters

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String  		oldVar_brokerCode;
	private transient long  		oldVar_brokerCustID;
	private transient String 		oldVar_lovDescBrokerCIF;
	private transient Date  		oldVar_brokerFrom;
	private transient String  		oldVar_brokerAddrHNbr;
	private transient String  		oldVar_brokerAddrFlatNbr;
	private transient String  		oldVar_brokerAddrStreet;
	private transient String  		oldVar_brokerAddrLane1;
	private transient String  		oldVar_brokerAddrLane2;
	private transient String  		oldVar_brokerAddrPOBox;
	private transient String  		oldVar_brokerAddrCountry;
	private transient String  		oldVar_brokerAddrProvince;
	private transient String  		oldVar_brokerAddrCity;
	private transient String  		oldVar_brokerAddrZIP;
	private transient String  		oldVar_brokerAddrPhone;
	private transient String  		oldVar_brokerAddrFax;
	private transient String  		oldVar_brokerEmail;
	private transient String  		oldVar_agreementRef;
	private transient String 		oldVar_lovDescBrokerAddrCountryName;
	private transient String 		oldVar_lovDescBrokerAddrProvinceName;
	private transient String 		oldVar_lovDescBrokerAddrCityName;
	private transient String        oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean           notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CommodityBrokerDetailDialog_";
	private transient ButtonStatusCtrl btnCtrl;



	// ServiceDAOs / Domain Classes
	private transient CommodityBrokerDetailService commodityBrokerDetailService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	protected JdbcSearchObject<Customer> newSearchObject;
	private String sBrokerAddrCountry;
	private String sBrokerAddrProvince;
	/**
	 * default constructor.<br>
	 */
	public CommodityBrokerDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CommodityBrokerDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CommodityBrokerDetailDialog(Event event) throws Exception {
		logger.debug(event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("commodityBrokerDetail")) {
			this.commodityBrokerDetail = (CommodityBrokerDetail) args.get("commodityBrokerDetail");
			CommodityBrokerDetail befImage =new CommodityBrokerDetail();
			BeanUtils.copyProperties(this.commodityBrokerDetail, befImage);
			this.commodityBrokerDetail.setBefImage(befImage);

			setCommodityBrokerDetail(this.commodityBrokerDetail);
		} else {
			setCommodityBrokerDetail(null);
		}

		doLoadWorkFlow(this.commodityBrokerDetail.isWorkflow(),this.commodityBrokerDetail.getWorkflowId(),this.commodityBrokerDetail.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "CommodityBrokerDetailDialog");
		}


		// READ OVERHANDED parameters !
		// we get the commodityBrokerDetailListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete commodityBrokerDetail here.
		if (args.containsKey("commodityBrokerDetailListCtrl")) {
			setCommodityBrokerDetailListCtrl((CommodityBrokerDetailListCtrl) 
					args.get("commodityBrokerDetailListCtrl"));
		} else {
			setCommodityBrokerDetailListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCommodityBrokerDetail());
		logger.debug("Leaving");
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_CommodityBrokerDetailDialog(Event event) throws Exception {
		logger.debug(event.toString());
		doClose();
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doSave();
		logger.debug("Leaving");
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(event.toString());
		doEdit();
		// remember the old variables
		doStoreInitValues();
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		PTMessageUtils.showHelpWindow(event, window_CommodityBrokerDetailDialog);
		logger.debug("Leaving");
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug(event.toString());
		doNew();
		logger.debug("Leaving");
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doDelete();
		logger.debug("Leaving");
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(event.toString());
		doCancel();
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving");
	}
	/**
	 * When user clicks on button "customerId Search" button
	 * @param event
	 */
	public void onClick$btnSearchBrokerCustID(Event event) throws 
	SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		onLoad();
		logger.debug("Leaving " + event.toString());
	}
	/**
	 * To load the customerSelect filter dialog
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void onLoad() throws SuspendNotAllowedException,
	InterruptedException {
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject",this.newSearchObject);
		Executions.createComponents(
				"/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul",
				null, map);
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
		this.brokerCustID.setValue(aCustomer.getCustID());
		this.brokerShtName.setValue(aCustomer.getCustShrtName());
		this.lovDescBrokerCIF.setValue(String.valueOf(aCustomer.getCustCIF()));
		this.newSearchObject=newSearchObject;

	}
	/**
	 * When user Clicks on "Notes" button 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {

		logger.debug(event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on button "customerId Search" button
	 * @param event
	 */
	public void onClick$btnSearchAddress(Event event) throws 
	SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());

		Filter[] filters = new Filter[1] ;
		filters[0]= new Filter("CustID",this.brokerCustID.getValue(), Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(this.window_CommodityBrokerDetailDialog,"CustomerAddres",filters);
		if (dataObject instanceof String){
			doClear();
		}else{
			CustomerAddres details= (CustomerAddres) dataObject;
			if (details != null) {
				this.brokerAddrHNbr.setValue(details.getCustAddrHNbr());
				this.brokerAddrFlatNbr.setValue(details.getCustFlatNbr());
				this.brokerAddrStreet.setValue(details.getCustAddrStreet());
				this.brokerAddrLane1.setValue(details.getCustAddrLine1());
				this.brokerAddrLane2.setValue(details.getCustAddrLine2());	
				this.brokerAddrPOBox.setValue(details.getCustPOBox());
				this.brokerAddrCountry.setValue(details.getCustAddrCountry());
				this.brokerAddrCountry.setDescription(details.getLovDescCustAddrCountryName());
				this.brokerAddrProvince.setValue(details.getCustAddrProvince());
				this.brokerAddrProvince.setDescription(details.getLovDescCustAddrProvinceName());
				this.brokerAddrCity.setValue(details.getCustAddrCity());
				this.brokerAddrCity.setDescription(details.getLovDescCustAddrCityName());
				this.brokerAddrZIP.setValue(details.getCustAddrZIP());
				this.brokerAddrPhone.setValue(details.getCustAddrPhone());	
			}
		}
		if(this.brokerAddrHNbr.getValue().trim().equals("")){
			if(getCommodityBrokerDetail().isNew()){
				doClear();
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when "btnSearchMortgAddrCountry" is clicked
	 * @param event
	 */
	public void onFulfill$brokerAddrCountry(Event event){
		logger.debug("Entering" + event.toString());

		if (!StringUtils.trimToEmpty(sBrokerAddrCountry).equals(
				this.brokerAddrCountry.getValue())) {
			this.brokerAddrProvince.setValue("");
			this.brokerAddrCity.setValue("");
			this.brokerAddrProvince.setDescription("");
			this.brokerAddrCity.setDescription("");
			this.brokerAddrCity.setReadonly(true);
		}
		if (this.brokerAddrCountry.getValue() != "") {
			this.brokerAddrProvince.setReadonly(false);
			this.brokerAddrProvince.setMandatoryStyle(true);
		} else {
			this.brokerAddrCity.setReadonly(true);
			this.brokerAddrProvince.setReadonly(true);
		}
		sBrokerAddrCountry = this.brokerAddrCountry.getValue();
		Filter[] filtersProvince = new Filter[1];
		filtersProvince[0] = new Filter("CPCountry", this.brokerAddrCountry.getValue(),
				Filter.OP_EQUAL);
		this.brokerAddrProvince.setFilters(filtersProvince);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when "btnSearchMortgAddrProvince" is clicked 
	 * @param event
	 */
	public void onFulfill$brokerAddrProvince(Event event){
		logger.debug("Entering" + event.toString());
		if (!StringUtils.trimToEmpty(sBrokerAddrProvince).equals(
				this.brokerAddrProvince.getValue())) {
			this.brokerAddrCity.setValue("");
			this.brokerAddrCity.setDescription("");
			this.brokerAddrCity.setReadonly(true);
		}
		if (this.brokerAddrProvince.getValue() != "") {
			this.brokerAddrCity.setReadonly(false);
			this.brokerAddrCity.setMandatoryStyle(true);
		} else {
			this.brokerAddrCity.setReadonly(true);
		}
		sBrokerAddrProvince = this.brokerAddrProvince.getValue();
		Filter[] filtersCity = new Filter[1];
		filtersCity[0] = new Filter("PCProvince", this.brokerAddrProvince.getValue(),
				Filter.OP_EQUAL);
		this.brokerAddrCity.setFilters(filtersCity);
		logger.debug("Leaving" + event.toString());
	}


	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ GUI Process +++++++++++++++++++++++
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
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES
					| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

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
			closeDialog(this.window_CommodityBrokerDetailDialog, "CommodityBrokerDetail");	
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
	 * @param aCommodityBrokerDetail
	 *            CommodityBrokerDetail
	 */
	public void doWriteBeanToComponents(CommodityBrokerDetail aCommodityBrokerDetail) {
		logger.debug("Entering") ;
		this.brokerCode.setValue(aCommodityBrokerDetail.getBrokerCode());
		this.brokerCustID.setValue(aCommodityBrokerDetail.getBrokerCustID());
		this.brokerFrom.setValue(aCommodityBrokerDetail.getBrokerFrom());
		this.brokerAddrHNbr.setValue(aCommodityBrokerDetail.getBrokerAddrHNbr());
		this.brokerAddrFlatNbr.setValue(aCommodityBrokerDetail.getBrokerAddrFlatNbr());
		this.brokerAddrStreet.setValue(aCommodityBrokerDetail.getBrokerAddrStreet());
		this.brokerAddrLane1.setValue(aCommodityBrokerDetail.getBrokerAddrLane1());
		this.brokerAddrLane2.setValue(aCommodityBrokerDetail.getBrokerAddrLane2());
		this.brokerAddrPOBox.setValue(aCommodityBrokerDetail.getBrokerAddrPOBox());
		this.brokerAddrCountry.setValue(aCommodityBrokerDetail.getBrokerAddrCountry());
		this.brokerAddrProvince.setValue(aCommodityBrokerDetail.getBrokerAddrProvince());
		this.brokerAddrCity.setValue(aCommodityBrokerDetail.getBrokerAddrCity());
		this.brokerAddrZIP.setValue(StringUtils.trimToEmpty(aCommodityBrokerDetail.getBrokerAddrZIP()));
		this.brokerAddrPhone.setValue(aCommodityBrokerDetail.getBrokerAddrPhone());
		this.brokerAddrFax.setValue(aCommodityBrokerDetail.getBrokerAddrFax());
		this.brokerEmail.setValue(aCommodityBrokerDetail.getBrokerEmail());
		this.agreementRef.setValue(aCommodityBrokerDetail.getAgreementRef());
		this.brokerAddrCountry.setDescription(aCommodityBrokerDetail.getLovDescBrokerAddrCountryName());
		this.brokerAddrProvince.setDescription(aCommodityBrokerDetail.getLovDescBrokerAddrProvinceName());
		this.brokerAddrCity.setDescription(aCommodityBrokerDetail.getLovDescBrokerAddrCityName());
		this.brokerShtName.setValue(aCommodityBrokerDetail.getLovDescBrokerShortName());
		if (aCommodityBrokerDetail.isNewRecord()){
			this.lovDescBrokerCIF.setValue("");
		}else{
			this.lovDescBrokerCIF.setValue(aCommodityBrokerDetail.getLovDescBrokerCIF());
		}
		this.recordStatus.setValue(aCommodityBrokerDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCommodityBrokerDetail
	 */
	public void doWriteComponentsToBean(CommodityBrokerDetail aCommodityBrokerDetail) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCommodityBrokerDetail.setBrokerCode(this.brokerCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCommodityBrokerDetail.setLovDescBrokerCIF(this.lovDescBrokerCIF.getValue());
			aCommodityBrokerDetail.setBrokerCustID(this.brokerCustID.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCommodityBrokerDetail.setBrokerFrom(this.brokerFrom.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCommodityBrokerDetail.setBrokerAddrHNbr(this.brokerAddrHNbr.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCommodityBrokerDetail.setBrokerAddrFlatNbr(this.brokerAddrFlatNbr.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCommodityBrokerDetail.setBrokerAddrStreet(this.brokerAddrStreet.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCommodityBrokerDetail.setBrokerAddrLane1(this.brokerAddrLane1.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCommodityBrokerDetail.setBrokerAddrLane2(this.brokerAddrLane2.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCommodityBrokerDetail.setBrokerAddrPOBox(this.brokerAddrPOBox.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCommodityBrokerDetail.setLovDescBrokerAddrCountryName(this.brokerAddrCountry.getDescription());
			aCommodityBrokerDetail.setBrokerAddrCountry(this.brokerAddrCountry.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCommodityBrokerDetail.setLovDescBrokerAddrProvinceName(this.brokerAddrProvince.getDescription());
			aCommodityBrokerDetail.setBrokerAddrProvince(this.brokerAddrProvince.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCommodityBrokerDetail.setLovDescBrokerAddrCityName(this.brokerAddrCity.getDescription());
			aCommodityBrokerDetail.setBrokerAddrCity(this.brokerAddrCity.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCommodityBrokerDetail.setBrokerAddrZIP(StringUtils.trimToEmpty(this.brokerAddrZIP.getValue()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCommodityBrokerDetail.setBrokerAddrPhone(this.brokerAddrPhone.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCommodityBrokerDetail.setBrokerAddrFax(this.brokerAddrFax.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCommodityBrokerDetail.setBrokerEmail(this.brokerEmail.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCommodityBrokerDetail.setAgreementRef(this.agreementRef.getValue());
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

		aCommodityBrokerDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCommodityBrokerDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(CommodityBrokerDetail aCommodityBrokerDetail) throws InterruptedException {
		logger.debug("Entering") ;

		// if aCommodityBrokerDetail == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aCommodityBrokerDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aCommodityBrokerDetail = getCommodityBrokerDetailService().getNewCommodityBrokerDetail();

			setCommodityBrokerDetail(aCommodityBrokerDetail);
		} else {
			setCommodityBrokerDetail(aCommodityBrokerDetail);
		}

		// set Read only mode accordingly if the object is new or not.
		if (aCommodityBrokerDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.brokerCode.focus();
		} else {
			this.brokerCustID.focus();
			if (isWorkFlowEnabled()){
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
			doWriteBeanToComponents(aCommodityBrokerDetail);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_CommodityBrokerDetailDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving") ;
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_CommodityBrokerDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.brokerCode.setMaxlength(8);
		this.brokerCustID.setMaxlength(19);
		this.brokerFrom.setFormat(PennantConstants.dateFormat);
		this.brokerAddrHNbr.setMaxlength(50);
		this.brokerAddrFlatNbr.setMaxlength(50);
		this.brokerAddrStreet.setMaxlength(50);
		this.brokerAddrLane1.setMaxlength(50);
		this.brokerAddrLane2.setMaxlength(50);
		this.brokerAddrPOBox.setMaxlength(8);
		this.brokerAddrCountry.setMaxlength(2);
		this.brokerAddrCountry.setMandatoryStyle(true);
		this.brokerAddrCountry.setModuleName("Country");
		this.brokerAddrCountry.setValueColumn("CountryCode");
		this.brokerAddrCountry.setDescColumn("CountryDesc");
		this.brokerAddrCountry.setValidateColumns(new String[] {"CountryCode"});
		this.brokerAddrProvince.setMaxlength(8);
        this.brokerAddrProvince.setMandatoryStyle(true);
		this.brokerAddrProvince.setModuleName("Province");
		this.brokerAddrProvince.setValueColumn("CPProvince");
		this.brokerAddrProvince.setDescColumn("CPProvinceName");
		this.brokerAddrProvince.setValidateColumns(new String[] { "CPProvince" });
		this.brokerAddrCity.setMaxlength(8);
        this.brokerAddrCity.setMandatoryStyle(true);
		this.brokerAddrCity.setModuleName("City");
		this.brokerAddrCity.setValueColumn("PCCity");
		this.brokerAddrCity.setDescColumn("PCCityName");
		this.brokerAddrCity.setValidateColumns(new String[] { "PCCity" });
		this.brokerAddrZIP.setMaxlength(10);
		this.brokerAddrPhone.setMaxlength(25);
		this.brokerAddrFax.setMaxlength(25);
		this.brokerEmail.setMaxlength(50);
		this.agreementRef.setMaxlength(100);
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

		getUserWorkspace().alocateAuthorities("CommodityBrokerDetailDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CommodityBrokerDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CommodityBrokerDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CommodityBrokerDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CommodityBrokerDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving") ;
	}

	/**
	 * Stores the initial values in memory variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_brokerCode = this.brokerCode.getValue();
		this.oldVar_brokerCustID = this.brokerCustID.longValue();
		this.oldVar_lovDescBrokerCIF = this.lovDescBrokerCIF.getValue();
		this.oldVar_brokerFrom = this.brokerFrom.getValue();
		this.oldVar_brokerAddrHNbr = this.brokerAddrHNbr.getValue();
		this.oldVar_brokerAddrFlatNbr = this.brokerAddrFlatNbr.getValue();
		this.oldVar_brokerAddrStreet = this.brokerAddrStreet.getValue();
		this.oldVar_brokerAddrLane1 = this.brokerAddrLane1.getValue();
		this.oldVar_brokerAddrLane2 = this.brokerAddrLane2.getValue();
		this.oldVar_brokerAddrPOBox = this.brokerAddrPOBox.getValue();
		this.oldVar_brokerAddrCountry = this.brokerAddrCountry.getValue();
		this.oldVar_brokerAddrProvince = this.brokerAddrProvince.getValue();
		this.oldVar_brokerAddrCity = this.brokerAddrCity.getValue();
		this.oldVar_brokerAddrZIP = this.brokerAddrZIP.getValue();
		this.oldVar_brokerAddrPhone = this.brokerAddrPhone.getValue();
		this.oldVar_brokerAddrFax = this.brokerAddrFax.getValue();
		this.oldVar_brokerEmail = this.brokerEmail.getValue();
		this.oldVar_agreementRef = this.agreementRef.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		this.oldVar_lovDescBrokerAddrCountryName=this.brokerAddrCountry.getDescription();
		this.oldVar_lovDescBrokerAddrProvinceName=this.brokerAddrProvince.getDescription();
		this.oldVar_lovDescBrokerAddrCityName=this.brokerAddrCity.getDescription();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from memory variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.brokerCode.setValue(this.oldVar_brokerCode);
		this.brokerCustID.setValue(this.oldVar_brokerCustID);
		this.lovDescBrokerCIF.setValue(this.oldVar_lovDescBrokerCIF);
		this.brokerFrom.setValue(this.oldVar_brokerFrom);
		this.brokerAddrHNbr.setValue(this.oldVar_brokerAddrHNbr);
		this.brokerAddrFlatNbr.setValue(this.oldVar_brokerAddrFlatNbr);
		this.brokerAddrStreet.setValue(this.oldVar_brokerAddrStreet);
		this.brokerAddrLane1.setValue(this.oldVar_brokerAddrLane1);
		this.brokerAddrLane2.setValue(this.oldVar_brokerAddrLane2);
		this.brokerAddrPOBox.setValue(this.oldVar_brokerAddrPOBox);
		this.brokerAddrCountry.setValue(this.oldVar_brokerAddrCountry);
		this.brokerAddrProvince.setValue(this.oldVar_brokerAddrProvince);
		this.brokerAddrCity.setValue(this.oldVar_brokerAddrCity);
		this.brokerAddrZIP.setValue(this.oldVar_brokerAddrZIP);
		this.brokerAddrPhone.setValue(this.oldVar_brokerAddrPhone);
		this.brokerAddrFax.setValue(this.oldVar_brokerAddrFax);
		this.brokerEmail.setValue(this.oldVar_brokerEmail);
		this.agreementRef.setValue(this.oldVar_agreementRef);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		this.brokerAddrCountry.setDescription(this.oldVar_lovDescBrokerAddrCountryName);
		this.brokerAddrProvince.setDescription(this.oldVar_lovDescBrokerAddrProvinceName);
		this.brokerAddrCity.setDescription(this.oldVar_lovDescBrokerAddrCityName);
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
		logger.debug("Entering");
		//To clear the Error Messages
		doClearMessage();
		if (this.oldVar_brokerCode != this.brokerCode.getValue()) {
			return true;
		}
		if (this.oldVar_brokerCustID != this.brokerCustID.getValue()) {
			return true;
		}
		if (this.oldVar_lovDescBrokerCIF != this.lovDescBrokerCIF.getValue()) {
			return true;
		}
		if (this.oldVar_brokerFrom != this.brokerFrom.getValue()) {
			return true;
		}
		if (this.oldVar_brokerAddrHNbr != this.brokerAddrHNbr.getValue()) {
			return true;
		}
		if (this.oldVar_brokerAddrFlatNbr != this.brokerAddrFlatNbr.getValue()) {
			return true;
		}
		if (this.oldVar_brokerAddrStreet != this.brokerAddrStreet.getValue()) {
			return true;
		}
		if (this.oldVar_brokerAddrLane1 != this.brokerAddrLane1.getValue()) {
			return true;
		}
		if (this.oldVar_brokerAddrLane2 != this.brokerAddrLane2.getValue()) {
			return true;
		}
		if (this.oldVar_brokerAddrPOBox != this.brokerAddrPOBox.getValue()) {
			return true;
		}
		if (this.oldVar_brokerAddrCountry != this.brokerAddrCountry.getValue()) {
			return true;
		}
		if (this.oldVar_brokerAddrProvince != this.brokerAddrProvince.getValue()) {
			return true;
		}
		if (this.oldVar_brokerAddrCity != this.brokerAddrCity.getValue()) {
			return true;
		}
		if (this.oldVar_brokerAddrZIP != this.brokerAddrZIP.getValue()) {
			return true;
		}
		if (this.oldVar_brokerAddrPhone != this.brokerAddrPhone.getValue()) {
			return true;
		}
		if (this.oldVar_brokerAddrFax != this.brokerAddrFax.getValue()) {
			return true;
		}
		if (this.oldVar_brokerEmail != this.brokerEmail.getValue()) {
			return true;
		}
		if (this.oldVar_agreementRef != this.agreementRef.getValue()) {
			return true;
		}
		if (this.oldVar_recordStatus != this.recordStatus.getValue()) {
			return true;
		}
		if (this.oldVar_lovDescBrokerAddrCountryName != this.brokerAddrCountry.getDescription()) {
			return true;
		}
		if (this.oldVar_lovDescBrokerAddrProvinceName != this.brokerAddrProvince.getDescription()) {
			return true;
		}
		if (this.oldVar_lovDescBrokerAddrCityName != this.brokerAddrCity.getDescription()) {
			return true;
		}

		logger.debug("Leaving"); 
		return false;
	}

	/**
	 *  Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.brokerCode.setErrorMessage("");
		this.lovDescBrokerCIF.setErrorMessage("");
		this.brokerFrom.setErrorMessage("");
		this.brokerAddrHNbr.setErrorMessage("");
		this.brokerAddrFlatNbr.setErrorMessage("");
		this.brokerAddrStreet.setErrorMessage("");
		this.brokerAddrPOBox.setErrorMessage("");
		this.brokerAddrCountry.setErrorMessage("");
		this.brokerAddrProvince.setErrorMessage("");
		this.brokerAddrCity.setErrorMessage("");
		this.brokerAddrZIP.setErrorMessage("");
		this.brokerAddrPhone.setErrorMessage("");
		this.brokerAddrFax.setErrorMessage("");
		this.brokerEmail.setErrorMessage("");
		this.agreementRef.setErrorMessage("");
		logger.debug("Leaving");
	}


	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.brokerCode.isReadonly()){
			this.brokerCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerCode.value"),PennantRegularExpressions.REGEX_ALPHANUM_UNDERSCORE, true));
		}	
		if (!this.brokerFrom.isReadonly()){
			this.brokerFrom.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY"
					,new String[]{Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerFrom.value")}));
		}	
		if (!this.brokerAddrHNbr.isReadonly()){
			this.brokerAddrHNbr.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerAddrHNbr.value"),
					PennantRegularExpressions.REGEX_ADDRESS, true));

		}	
		if (!this.brokerAddrFlatNbr.isReadonly()){
			this.brokerAddrFlatNbr.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerAddrFlatNbr.value"),
					PennantRegularExpressions.REGEX_ADDRESS, true));

		}	
		if (!this.brokerAddrStreet.isReadonly()){
			this.brokerAddrStreet.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerAddrStreet.value"),PennantRegularExpressions.REGEX_ADDRESS, true));

		}	
		if (!this.brokerAddrPOBox.isReadonly()){
			this.brokerAddrPOBox.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerAddrPOBox.value"),
					PennantRegularExpressions.REGEX_NUMERIC, true));

		}	
		if (!this.brokerAddrCountry.isReadonly()){
			this.brokerAddrCountry.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY"
					,new String[]{Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerAddrCountry.value")}));
		}	
		if (!this.brokerAddrProvince.isReadonly()){
			this.brokerAddrProvince.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY"
					,new String[]{Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerAddrProvince.value")}));
		}	
		if (!this.brokerAddrCity.isReadonly()){
			this.brokerAddrCity.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY"
					,new String[]{Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerAddrCity.value")}));
		}	
		if (!this.brokerAddrZIP.isReadonly()){
			this.brokerAddrZIP.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerAddrZIP.value"), PennantRegularExpressions.REGEX_ZIP, false));
		}	
		if (!this.brokerAddrPhone.isReadonly()){
			this.brokerAddrPhone.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerAddrPhone.value"),true));
		}	

		if (!this.brokerEmail.isReadonly()){
			this.brokerEmail.setConstraint(new PTEmailValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerEmail.value"),true));

		}	
		if (!this.agreementRef.isReadonly()){
			this.agreementRef.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_AgreementRef.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));

		}	
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.brokerCode.setConstraint("");
		this.brokerFrom.setConstraint("");
		this.brokerAddrHNbr.setConstraint("");
		this.brokerAddrFlatNbr.setConstraint("");
		this.brokerAddrStreet.setConstraint("");
		this.brokerAddrPOBox.setConstraint("");
		this.brokerAddrCountry.setConstraint("");
		this.brokerAddrProvince.setConstraint("");
		this.brokerAddrCity.setConstraint("");
		this.brokerAddrZIP.setConstraint("");
		this.brokerAddrPhone.setConstraint("");
		this.brokerAddrFax.setConstraint("");
		this.brokerEmail.setConstraint("");
		this.agreementRef.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 *Sets the Validation by setting the accordingly constraints to the LOVFields.
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering ");
		this.lovDescBrokerCIF.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY"
				,new String[]{Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerCustCIF.value")}));
		this.brokerAddrCountry.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY"
				,new String[]{Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerAddrCountry.value")}));
		this.brokerAddrProvince.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY"
				,new String[]{Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerAddrProvince.value")}));
		this.brokerAddrCity.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY"
				,new String[]{Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerAddrCity.value")}));
		logger.debug("Leaving ");
	}
	/**
	 * Disables the Validation by setting empty constraints to the LovFields.
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering ");
		this.lovDescBrokerCIF.setConstraint("");
		this.brokerAddrCountry.setConstraint("");
		this.brokerAddrProvince.setConstraint("");
		this.brokerAddrCity.setConstraint("");
		logger.debug("Leaving ");

	}


	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.brokerCode.setReadonly(true);
		this.btnSearchBrokerCustID.setDisabled(true);
		this.brokerFrom.setDisabled(true);
		this.brokerAddrHNbr.setReadonly(true);
		this.brokerAddrFlatNbr.setReadonly(true);
		this.brokerAddrStreet.setReadonly(true);
		this.brokerAddrLane1.setReadonly(true);
		this.brokerAddrLane2.setReadonly(true);
		this.brokerAddrPOBox.setReadonly(true);
		this.brokerAddrCountry.setReadonly(true);
		this.brokerAddrProvince.setReadonly(true);
		this.brokerAddrCity.setReadonly(true);
		this.brokerAddrZIP.setReadonly(true);
		this.brokerAddrPhone.setReadonly(true);
		this.brokerAddrFax.setReadonly(true);
		this.brokerEmail.setReadonly(true);
		this.agreementRef.setReadonly(true);

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

		this.brokerCode.setValue("");
		this.brokerCustID.setText("");
		this.agreementRef.setValue("");
		this.lovDescBrokerCIF.setValue("");
		this.brokerFrom.setText("");
		this.brokerAddrHNbr.setValue("");
		this.brokerAddrFlatNbr.setValue("");
		this.brokerAddrStreet.setValue("");
		this.brokerAddrLane1.setValue("");
		this.brokerAddrLane2.setValue("");
		this.brokerAddrPOBox.setValue("");
		this.brokerAddrCountry.setValue("");
		this.brokerAddrProvince.setValue("");
		this.brokerAddrCity.setValue("");
		this.brokerAddrZIP.setValue("");
		this.brokerAddrPhone.setValue("");
		this.brokerAddrFax.setValue("");
		this.brokerEmail.setValue("");

		this.brokerAddrCountry.setDescription("");
		this.brokerAddrProvince.setDescription("");
		this.brokerAddrCity.setDescription("");
		logger.debug("Leaving");
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CommodityBrokerDetail aCommodityBrokerDetail, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCommodityBrokerDetail.getBefImage(), aCommodityBrokerDetail);   
		return new AuditHeader(aCommodityBrokerDetail.getBrokerCode(),null,null,null,auditDetail,aCommodityBrokerDetail.getUserDetails(),getOverideMap());
	}


	/**
	 * This method Check notes Entered or not and sets notes_Entered value
	 * @param notes
	 */
	public void setNotes_entered(String notes) {
		logger.debug("Entering ");
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
		logger.debug("Leaving ");
	}	

	/**
	 *  This method Get the notes entered for rejected or resubmitted reason
	 * @return
	 */
	private Notes getNotes(){
		logger.debug("Entering ");
		Notes notes = new Notes();
		notes.setModuleName("CommodityBrokerDetail");
		notes.setReference(getCommodityBrokerDetail().getBrokerCode());
		notes.setVersion(getCommodityBrokerDetail().getVersion());
		logger.debug("Leaving ");
		return notes;
	}

	/**
	 * This method refresh the list after successful update of record
	 */
	private void refreshList(){
		logger.debug("Entering ");

		final JdbcSearchObject<CommodityBrokerDetail> soCommodityBrokerDetail
		= getCommodityBrokerDetailListCtrl().getSearchObj();
		getCommodityBrokerDetailListCtrl().pagingCommodityBrokerDetailList.setActivePage(0);
		getCommodityBrokerDetailListCtrl().getPagedListWrapper().setSearchObject(soCommodityBrokerDetail);
		if(getCommodityBrokerDetailListCtrl().listBoxCommodityBrokerDetail!=null){
			getCommodityBrokerDetailListCtrl().listBoxCommodityBrokerDetail.getListModel();
		}
		logger.debug("Leaving ");
	} 


	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a CommodityBrokerDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final CommodityBrokerDetail aCommodityBrokerDetail = new CommodityBrokerDetail();
		BeanUtils.copyProperties(getCommodityBrokerDetail(), aCommodityBrokerDetail);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") 
		+ "\n\n --> " + aCommodityBrokerDetail.getBrokerCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES
				| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCommodityBrokerDetail.getRecordType()).equals("")){
				aCommodityBrokerDetail.setVersion(aCommodityBrokerDetail.getVersion()+1);
				aCommodityBrokerDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aCommodityBrokerDetail.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aCommodityBrokerDetail,tranType)){
					refreshList();
					closeDialog(this.window_CommodityBrokerDetailDialog, "CommodityBrokerDetail"); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new CommodityBrokerDetail object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		final CommodityBrokerDetail aCommodityBrokerDetail = getCommodityBrokerDetailService()
		.getNewCommodityBrokerDetail();
		aCommodityBrokerDetail.setNewRecord(true);
		setCommodityBrokerDetail(aCommodityBrokerDetail);
		doClear();                  // clear all components
		doEdit();                   // edit mode
		this.btnCtrl.setBtnStatus_New();
		doStoreInitValues();        // remember the old variables
		this.brokerCode.focus();	// setFocus
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getCommodityBrokerDetail().isNewRecord()){
			this.brokerCode.setReadonly(false);
			this.btnCancel.setVisible(false);
			this.btnSearchBrokerCustID.setVisible(true);
			this.brokerAddrProvince.setReadonly(true);
			this.brokerAddrCity.setReadonly(true);
		}else{
			this.brokerCode.setReadonly(true);
			this.btnCancel.setVisible(true);
			this.btnSearchBrokerCustID.setVisible(false);
			this.brokerAddrProvince.setReadonly(isReadOnly("CommodityBrokerDetailDialog_brokerAddrProvince"));
			this.brokerAddrCity.setReadonly(isReadOnly("CommodityBrokerDetailDialog_brokerAddrCity"));
		}

		this.brokerFrom.setDisabled(isReadOnly("CommodityBrokerDetailDialog_brokerFrom"));
		this.brokerAddrHNbr.setReadonly(isReadOnly("CommodityBrokerDetailDialog_brokerAddrHNbr"));
		this.brokerAddrFlatNbr.setReadonly(isReadOnly("CommodityBrokerDetailDialog_brokerAddrFlatNbr"));
		this.brokerAddrStreet.setReadonly(isReadOnly("CommodityBrokerDetailDialog_brokerAddrStreet"));
		this.brokerAddrLane1.setReadonly(isReadOnly("CommodityBrokerDetailDialog_brokerAddrLane1"));
		this.brokerAddrLane2.setReadonly(isReadOnly("CommodityBrokerDetailDialog_brokerAddrLane2"));
		this.brokerAddrPOBox.setReadonly(isReadOnly("CommodityBrokerDetailDialog_brokerAddrPOBox"));
		this.brokerAddrCountry.setReadonly(isReadOnly("CommodityBrokerDetailDialog_brokerAddrCountry"));
		this.brokerAddrZIP.setReadonly(isReadOnly("CommodityBrokerDetailDialog_brokerAddrZIP"));
		this.brokerAddrPhone.setReadonly(isReadOnly("CommodityBrokerDetailDialog_brokerAddrPhone"));
		this.brokerAddrFax.setReadonly(isReadOnly("CommodityBrokerDetailDialog_brokerAddrFax"));
		this.brokerEmail.setReadonly(isReadOnly("CommodityBrokerDetailDialog_brokerEmail"));
		this.agreementRef.setReadonly(isReadOnly("CommodityBrokerDetailDialog_agreementRef"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.commodityBrokerDetail.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final CommodityBrokerDetail aCommodityBrokerDetail = new CommodityBrokerDetail();
		BeanUtils.copyProperties(getCommodityBrokerDetail(), aCommodityBrokerDetail);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the CommodityBrokerDetail object with the components data
		doWriteComponentsToBean(aCommodityBrokerDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aCommodityBrokerDetail.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCommodityBrokerDetail.getRecordType()).equals("")){
				aCommodityBrokerDetail.setVersion(aCommodityBrokerDetail.getVersion()+1);
				if(isNew){
					aCommodityBrokerDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCommodityBrokerDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCommodityBrokerDetail.setNewRecord(true);
				}
			}
		}else{
			aCommodityBrokerDetail.setVersion(aCommodityBrokerDetail.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aCommodityBrokerDetail,tranType)){
				doWriteBeanToComponents(aCommodityBrokerDetail);
				refreshList();
				closeDialog(this.window_CommodityBrokerDetailDialog, "CommodityBrokerDetail");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			e.printStackTrace();
			showMessage(e);
		}
		logger.debug("Leaving");
	}
	/**
	 * Set the workFlow Details List to Object
	 * @param aCommodityBrokerDetail
	 * @param tranType
	 * @return
	 */
	private boolean doProcess(CommodityBrokerDetail aCommodityBrokerDetail,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aCommodityBrokerDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCommodityBrokerDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCommodityBrokerDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCommodityBrokerDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCommodityBrokerDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCommodityBrokerDetail);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aCommodityBrokerDetail))) {
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

			aCommodityBrokerDetail.setTaskId(taskId);
			aCommodityBrokerDetail.setNextTaskId(nextTaskId);
			aCommodityBrokerDetail.setRoleCode(getRole());
			aCommodityBrokerDetail.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aCommodityBrokerDetail, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aCommodityBrokerDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aCommodityBrokerDetail, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{

			auditHeader =  getAuditHeader(aCommodityBrokerDetail, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;

		CommodityBrokerDetail aCommodityBrokerDetail = (CommodityBrokerDetail) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getCommodityBrokerDetailService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getCommodityBrokerDetailService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getCommodityBrokerDetailService().doApprove(auditHeader);

						if(aCommodityBrokerDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getCommodityBrokerDetailService().doReject(auditHeader);
						if(aCommodityBrokerDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999
								, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CommodityBrokerDetailDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_CommodityBrokerDetailDialog, auditHeader);
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
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public CommodityBrokerDetail getCommodityBrokerDetail() {
		return this.commodityBrokerDetail;
	}

	public void setCommodityBrokerDetail(CommodityBrokerDetail commodityBrokerDetail) {
		this.commodityBrokerDetail = commodityBrokerDetail;
	}

	public void setCommodityBrokerDetailService(CommodityBrokerDetailService commodityBrokerDetailService) {
		this.commodityBrokerDetailService = commodityBrokerDetailService;
	}

	public CommodityBrokerDetailService getCommodityBrokerDetailService() {
		return this.commodityBrokerDetailService;
	}

	public void setCommodityBrokerDetailListCtrl(CommodityBrokerDetailListCtrl commodityBrokerDetailListCtrl) {
		this.commodityBrokerDetailListCtrl = commodityBrokerDetailListCtrl;
	}

	public CommodityBrokerDetailListCtrl getCommodityBrokerDetailListCtrl() {
		return this.commodityBrokerDetailListCtrl;
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

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public CommodityBrokerDetail getPrvCommodityBrokerDetail() {
		return prvCommodityBrokerDetail;
	}
}

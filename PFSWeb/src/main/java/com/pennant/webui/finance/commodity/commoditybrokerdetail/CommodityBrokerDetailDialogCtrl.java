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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.finance.commodity.BrokerCommodityDetail;
import com.pennant.backend.model.finance.commodity.CommodityBrokerDetail;
import com.pennant.backend.service.finance.commodity.CommodityBrokerDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTEmailValidator;
import com.pennant.util.Constraint.PTPhoneNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Finance.Commodity/CommodityBrokerDetail
 * /CommodityBrokerDetailDialog.zul file.
 */
public class CommodityBrokerDetailDialogCtrl extends GFCBaseCtrl<CommodityBrokerDetail> {
	private static final long serialVersionUID = -4697540691852649079L;
	private static final Logger logger = Logger.getLogger(CommodityBrokerDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
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
	protected  Space  space_brokerAddrZIP;                 // autoWired
	protected  Textbox	phoneCountryCode; 				   // autoWired						
	protected  Textbox	phoneAreaCode;					   // autoWired	
	protected  Textbox brokerAddrPhone;                    // autoWired
	protected Textbox       faxCountryCode;		// autoWired
	protected Textbox		faxAreaCode;		// autoWired
	protected  Textbox brokerAddrFax;                      // autoWired
	protected  Textbox brokerEmail;                        // autoWired
	protected  Textbox agreementRef;                       // autoWired
	protected CurrencyBox feeOnUnsold;                     // autoWired
	protected AccountSelectionBox accountNumber;           // autoWired
	protected Textbox   commodityDetail;				   // autoWired
	protected Button	btnSearchCommodityDetail;		   // autoWired
	protected Listbox	listbox_commodity;		           // autoWired
	protected Decimalbox commissionRate;		           // autoWired
    protected Textbox     cityName;                        // autoWired
	
	protected  Datebox brokerFrom;                         // autoWired

	protected  Textbox lovDescBrokerCIF;                   // autoWired

	protected  Textbox   brokerShtName;                      // autoWired
	//buttons 
	protected Button    btnSearchBrokerCustID;             // autoWired
	protected Button    btnSearchAddress;                  // autoWired


	// not auto wired variables
	private CommodityBrokerDetail commodityBrokerDetail;                           // overHanded per parameters
	private transient CommodityBrokerDetailListCtrl commodityBrokerDetailListCtrl; // overHanded per parameters

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient CommodityBrokerDetailService commodityBrokerDetailService;
	private HashMap<String, ArrayList<ErrorDetail>> overideMap= new HashMap<String, ArrayList<ErrorDetail>>();
	protected JdbcSearchObject<Customer> newSearchObject;
	private String sBrokerAddrCountry;
	private String sBrokerAddrProvince;
	private int	 defaultCCYDecPos = SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT);
	/**
	 * default constructor.<br>
	 */
	public CommodityBrokerDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CommodityBrokerDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CommodityBrokerDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CommodityBrokerDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CommodityBrokerDetailDialog);

		try {
			if (PennantConstants.CITY_FREETEXT) {
				this.brokerAddrCity.setVisible(false);
				this.cityName.setVisible(true);
			} else {
				this.brokerAddrCity.setVisible(true);
				this.cityName.setVisible(false);
			}

			/* set components visible dependent of the users rights */
			doCheckRights();
			// READ OVERHANDED parameters !
			if (arguments.containsKey("commodityBrokerDetail")) {
				this.commodityBrokerDetail = (CommodityBrokerDetail) arguments
						.get("commodityBrokerDetail");
				CommodityBrokerDetail befImage = new CommodityBrokerDetail();
				BeanUtils.copyProperties(this.commodityBrokerDetail, befImage);
				this.commodityBrokerDetail.setBefImage(befImage);

				setCommodityBrokerDetail(this.commodityBrokerDetail);
			} else {
				setCommodityBrokerDetail(null);
			}

			doLoadWorkFlow(this.commodityBrokerDetail.isWorkflow(),
					this.commodityBrokerDetail.getWorkflowId(),
					this.commodityBrokerDetail.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"CommodityBrokerDetailDialog");
			}

			// READ OVERHANDED parameters !
			// we get the commodityBrokerDetailListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete commodityBrokerDetail here.
			if (arguments.containsKey("commodityBrokerDetailListCtrl")) {
				setCommodityBrokerDetailListCtrl((CommodityBrokerDetailListCtrl) arguments
						.get("commodityBrokerDetailListCtrl"));
			} else {
				setCommodityBrokerDetailListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getCommodityBrokerDetail());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CommodityBrokerDetailDialog.onClose();
		}
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
		MessageUtil.showHelpWindow(event, window_CommodityBrokerDetailDialog);
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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
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
		this.accountNumber.setCustCIF(String.valueOf(aCustomer.getCustCIF()));
		this.accountNumber.setValue("");
		this.newSearchObject=newSearchObject;

	}
	/**
	 * When user Clicks on "Notes" button 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.commodityBrokerDetail);
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
		if(StringUtils.isEmpty(this.brokerAddrHNbr.getValue().trim())){
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
		
		if(StringUtils.isNotEmpty(this.brokerAddrCountry.getValue())){
			this.brokerAddrProvince.setReadonly(false);
			this.brokerAddrProvince.setMandatoryStyle(true);
		} else {
			this.brokerAddrCity.setReadonly(true);
			this.brokerAddrCity.setValue("");
			this.brokerAddrProvince.setReadonly(true);
			this.brokerAddrProvince.setValue("");
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
		if(StringUtils.isNotEmpty(this.brokerAddrProvince.getValue())){
			this.brokerAddrCity.setReadonly(false);
			this.brokerAddrCity.setMandatoryStyle(false);
		} else {
			this.brokerAddrCity.setReadonly(true);
		}
		sBrokerAddrProvince = this.brokerAddrProvince.getValue();
		Filter[] filtersCity = new Filter[2];
		filtersCity[0] = new Filter("PCCountry", this.brokerAddrCountry.getValue(),Filter.OP_EQUAL);
		filtersCity[1] = new Filter("PCProvince", this.brokerAddrProvince.getValue(),Filter.OP_EQUAL);
		this.brokerAddrCity.setFilters(filtersCity);
		logger.debug("Leaving" + event.toString());
	}


	public void onClick$btnSearchCommodityDetail(Event event) throws Exception{
		logger.debug("Entering  "+event.toString());
		this.commodityDetail.setErrorMessage("");
		Clients.clearWrongValue(listbox_commodity);
		
		Object dataObject = MultiSelectionSearchListBox.show(this.window_CommodityBrokerDetailDialog,"CommodityDetail",String.valueOf(this.commodityDetail.getValue()),null);
		
		if (dataObject !=null) {
			String details = (String) dataObject;
			this.commodityDetail.setValue(details);
		}
		doFillCommodityDetails(this.commodityDetail.getValue());
	    logger.debug("Leaving  "+event.toString());
	    
	}
	
	public void doFillCommodityDetails(String commodityDetails){
		this.listbox_commodity.getItems().clear();
		if(StringUtils.isNotBlank(commodityDetails)){
			List<String>  commodityDetailList = new ArrayList<String>();
			if(commodityDetails.contains(",")){
				String[] details = commodityDetails.split(",");
				for (String commodity : details) {
					commodityDetailList.add(commodity);
				}
			}else{
				commodityDetailList.add(commodityDetails);
			}
			List<ValueLabel> commodityList = PennantAppUtil.getCommodityValues();
			for (String commodity : commodityDetailList) {
				Listitem item = new Listitem();
				Listcell lc = new Listcell(PennantAppUtil.getlabelDesc(commodity, commodityList));
				lc.setTooltiptext(PennantAppUtil.getlabelDesc(commodity, commodityList));
				lc.setParent(item);
				this.listbox_commodity.appendChild(item);
			}
		}
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering") ;
		doWriteBeanToComponents(this.commodityBrokerDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
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
		this.cityName.setValue(aCommodityBrokerDetail.getBrokerAddrCity());
		this.brokerAddrProvince.setValue(aCommodityBrokerDetail.getBrokerAddrProvince());
		if(!PennantConstants.CITY_FREETEXT){
			this.brokerAddrCity.setValue(aCommodityBrokerDetail.getBrokerAddrCity());
		}
		this.brokerAddrZIP.setValue(StringUtils.trimToEmpty(aCommodityBrokerDetail.getBrokerAddrZIP()));
		String[]mobile = PennantApplicationUtil.unFormatPhoneNumber(aCommodityBrokerDetail.getBrokerAddrPhone());
		this.phoneCountryCode.setValue(mobile[0]);
		this.phoneAreaCode.setValue(mobile[1]);
		this.brokerAddrPhone.setValue(mobile[2]);
		String[] fax = PennantApplicationUtil.unFormatPhoneNumber(aCommodityBrokerDetail.getBrokerAddrFax());
		this.faxCountryCode.setValue(fax[0]);
		this.faxAreaCode.setValue(fax[1]);
		this.brokerAddrFax.setValue(fax[2]);
		this.brokerEmail.setValue(aCommodityBrokerDetail.getBrokerEmail());
		this.agreementRef.setValue(aCommodityBrokerDetail.getAgreementRef());
		this.brokerAddrCountry.setDescription(aCommodityBrokerDetail.getLovDescBrokerAddrCountryName());
		this.brokerAddrProvince.setDescription(aCommodityBrokerDetail.getLovDescBrokerAddrProvinceName());
		this.brokerAddrCity.setDescription(aCommodityBrokerDetail.getLovDescBrokerAddrCityName());
		this.brokerShtName.setValue(aCommodityBrokerDetail.getLovDescBrokerShortName());
		this.feeOnUnsold.setValue(PennantApplicationUtil.formateAmount(aCommodityBrokerDetail.getFeeOnUnsold(), defaultCCYDecPos));
		this.accountNumber.setValue(aCommodityBrokerDetail.getAccountNumber());
		if (aCommodityBrokerDetail.isNewRecord()){
			this.lovDescBrokerCIF.setValue("");
		}else{
			this.lovDescBrokerCIF.setValue(aCommodityBrokerDetail.getLovDescBrokerCIF());
			this.accountNumber.setCustCIF(aCommodityBrokerDetail.getLovDescBrokerCIF());
		}
		
		aCommodityBrokerDetail.setLovDescCommodityDetail(processCommodityList(aCommodityBrokerDetail.getBrokerCommodityList()));
		this.commodityDetail.setValue(aCommodityBrokerDetail.getLovDescCommodityDetail());
		doFillCommodityDetails(aCommodityBrokerDetail.getLovDescCommodityDetail());
		
		this.commissionRate.setValue(aCommodityBrokerDetail.getCommissionRate());
		this.recordStatus.setValue(aCommodityBrokerDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCommodityBrokerDetail
	 * @throws InterruptedException 
	 */
	public void doWriteComponentsToBean(CommodityBrokerDetail aCommodityBrokerDetail) throws InterruptedException {
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
		aCommodityBrokerDetail.setLovDescBrokerAddrCityName(StringUtils.trimToNull(this.brokerAddrCity.getDescription()));
		aCommodityBrokerDetail.setBrokerAddrCity(StringUtils.trimToNull(this.brokerAddrCity.getValue()));
	}catch (WrongValueException we ) {
		wve.add(we);
	}
		
		try {
			aCommodityBrokerDetail.setBrokerAddrZIP(StringUtils.trimToNull(this.brokerAddrZIP.getValue()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCommodityBrokerDetail.setBrokerAddrPhone(PennantApplicationUtil.formatPhoneNumber(this.phoneCountryCode.getValue(), this.phoneAreaCode.getValue(), this.brokerAddrPhone.getValue()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCommodityBrokerDetail.setBrokerAddrFax(PennantApplicationUtil.formatPhoneNumber(this.faxCountryCode.getValue(),this.faxAreaCode.getValue(),this.brokerAddrFax.getValue()));
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
		try {
			aCommodityBrokerDetail.setFeeOnUnsold(PennantApplicationUtil.unFormateAmount(this.feeOnUnsold.getValidateValue(), defaultCCYDecPos));
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		try {
			this.accountNumber.validateValue();
			aCommodityBrokerDetail.setAccountNumber(PennantApplicationUtil.unFormatAccountNumber(this.accountNumber.getValue()));

		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			this.brokerCode.setConstraint("");
			aCommodityBrokerDetail.setLovDescCommodityDetail(this.commodityDetail.getValue());
			aCommodityBrokerDetail.setBrokerCommodityList(fetchCommodityList(this.brokerCode.getValue(),this.commodityDetail.getValue()));

		} catch (WrongValueException we) {
			wve.add(new WrongValueException(this.listbox_commodity, we.getMessage()));
		}
		
		try {
			aCommodityBrokerDetail.setCommissionRate(this.commissionRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
			
		}
		
		try {
			if (PennantConstants.CITY_FREETEXT) {
				aCommodityBrokerDetail.setBrokerAddrCity(StringUtils.trimToNull(this.cityName
						.getValue()));
			} else {
				aCommodityBrokerDetail.setLovDescBrokerAddrCityName(StringUtils
						.trimToNull(this.brokerAddrCity.getDescription()));
				aCommodityBrokerDetail.setBrokerAddrCity(StringUtils.trimToNull(this.brokerAddrCity
						.getValidatedValue()));
			}
	
		} catch(WrongValueException we) {
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

	private String processCommodityList(List<BrokerCommodityDetail> detailList){
		String commodityDetail = "";
		if(detailList != null && !detailList.isEmpty()){
			for (BrokerCommodityDetail brokerCommodityDetail : detailList) {
				if(StringUtils.isEmpty(commodityDetail)){
					commodityDetail = brokerCommodityDetail.getCommodityCode();
				}else{
					commodityDetail = commodityDetail + ","+brokerCommodityDetail.getCommodityCode();
				}
			}
		}
		return commodityDetail;
	}
	private List<BrokerCommodityDetail> fetchCommodityList(String brokerCode,String commodityDetail){
		List<BrokerCommodityDetail> detailList = new ArrayList<BrokerCommodityDetail>();
		if(StringUtils.isNotBlank(commodityDetail)){
			BrokerCommodityDetail detail = null;
			if(commodityDetail.contains(",")){
				String[] details = commodityDetail.split(",");
				for (String commodity : details) {
					detail = new BrokerCommodityDetail();
					detail.setBrokerCode(brokerCode);
					detail.setCommodityCode(commodity);
					detailList.add(detail);
				}
			}else{
				detail = new BrokerCommodityDetail();
				detail.setBrokerCode(brokerCode);
				detail.setCommodityCode(commodityDetail);
				detailList.add(detail);
			}
		}
		return detailList;
	}
	
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCommodityBrokerDetail
	 * @throws Exception
	 */
	public void doShowDialog(CommodityBrokerDetail aCommodityBrokerDetail) throws Exception {
		logger.debug("Entering");

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

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_CommodityBrokerDetailDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving") ;
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_CommodityBrokerDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}
	
	// Helpers

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.brokerCode.setMaxlength(8);
		this.brokerCustID.setMaxlength(19);
		this.brokerFrom.setFormat(DateFormat.SHORT_DATE.getPattern());
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
        this.brokerAddrCity.setMandatoryStyle(false);
		this.brokerAddrCity.setModuleName("City");
		this.brokerAddrCity.setValueColumn("PCCity");
		this.brokerAddrCity.setDescColumn("PCCityName");
		this.brokerAddrCity.setValidateColumns(new String[] { "PCCity" });
		this.cityName.setMaxlength(50);
		this.brokerAddrZIP.setMaxlength(10);
		this.phoneCountryCode.setMaxlength(3);
		this.phoneAreaCode.setMaxlength(3);
		this.brokerAddrPhone.setMaxlength(8);
		this.brokerAddrFax.setMaxlength(8);
		this.faxCountryCode.setMaxlength(4);
		this.faxAreaCode.setMaxlength(4);
		this.brokerEmail.setMaxlength(50);
		this.agreementRef.setMaxlength(100);
		this.feeOnUnsold.setMandatory(true);
		this.feeOnUnsold.setFormat(PennantApplicationUtil.getAmountFormate(defaultCCYDecPos));
		this.feeOnUnsold.setScale(defaultCCYDecPos);
		
		this.accountNumber.setAcountDetails(AccountConstants.ACTYPES_COMMODITYBROKER, "",  true);
		this.accountNumber.setFormatter(defaultCCYDecPos);
		this.accountNumber.setMandatoryStyle(true);
		this.accountNumber.setTextBoxWidth(165);
		
		this.commissionRate.setMaxlength(6);
		this.commissionRate.setFormat(PennantConstants.rateFormate9);
		this.commissionRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.commissionRate.setScale(2);
		
		
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

		getUserWorkspace().allocateAuthorities(super.pageRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CommodityBrokerDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CommodityBrokerDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CommodityBrokerDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CommodityBrokerDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving") ;
	}

	/**
	 *  Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
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
		this.phoneAreaCode.setErrorMessage("");
		this.phoneCountryCode.setErrorMessage("");
		this.brokerAddrFax.setErrorMessage("");
		this.faxCountryCode.setErrorMessage("");
		this.faxAreaCode.setErrorMessage("");
		this.brokerEmail.setErrorMessage("");
		this.agreementRef.setErrorMessage("");
		this.feeOnUnsold.setErrorMessage("");
		this.accountNumber.setErrorMessage("");
		this.commodityDetail.setErrorMessage("");
		this.commissionRate.setErrorMessage("");
		this.cityName.setErrorMessage("");
		Clients.clearWrongValue(listbox_commodity);
		logger.debug("Leaving");
	}


	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		Date appStartDate = DateUtility.getAppDate();
		Date startDate = SysParamUtil.getValueAsDate("APP_DFT_START_DATE");

		if (!this.brokerCode.isReadonly()){
			this.brokerCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerCode.value"),PennantRegularExpressions.REGEX_UPPBOX_ALPHANUM_UNDERSCORE, true));
		}	
		if (!this.brokerFrom.isReadonly()){
			this.brokerFrom.setConstraint(new PTDateValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerFrom.value"), true, startDate, appStartDate, false));
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
			this.brokerAddrCountry.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerAddrCountry.value"),null,true));
		}	
		if (!this.brokerAddrProvince.isReadonly()){
			this.brokerAddrProvince.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerAddrProvince.value"),null,true));
		}	
		
		if (!this.brokerAddrZIP.isReadonly()){
			this.brokerAddrZIP.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerAddrZIP.value"), 
					PennantRegularExpressions.REGEX_ZIP,StringUtils.isBlank(this.space_brokerAddrZIP.getSclass())?false:true));
		}		
		if(!this.phoneCountryCode.isReadonly()){
			this.phoneCountryCode.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_mobileCountryCode.value"),true,1));
		}
		if(!this.phoneAreaCode.isReadonly()){
			this.phoneAreaCode.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_mobileAreaCode.value"),true,2));
		}
		if (!this.brokerAddrPhone.isReadonly()){
			this.brokerAddrPhone.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerAddrPhone.value"),true,3));
		}	
		if (!this.faxCountryCode.isReadonly()) {
			this.faxCountryCode.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_FaxCountryCode.value"),false,1));
		}
		if (!this.faxAreaCode.isReadonly() && StringUtils.isNotEmpty(this.faxCountryCode.getValue())) {
			this.faxAreaCode.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_FaxAreaCode.value"),true,2));
		}
		if (!this.brokerAddrFax.isReadonly() && StringUtils.isNotEmpty(this.faxAreaCode.getValue())){
			this.brokerAddrFax.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerAddrFax.value"),true,3));
		}
		
		if (!this.brokerEmail.isReadonly()){
			this.brokerEmail.setConstraint(new PTEmailValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerEmail.value"),true));

		}	
		if (!this.agreementRef.isReadonly()){
			this.agreementRef.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_AgreementRef.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));

		}
		if (!this.feeOnUnsold.isReadonly()) {
			this.feeOnUnsold.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_FeeOnUnsold.value"), 0, true, false));
		}
		if (!this.accountNumber.isReadonly()) {
			this.accountNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_AccountNumber.value"),null,true));
		}
		if (!this.btnSearchCommodityDetail.isDisabled()) {
			this.commodityDetail.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_CommodityDetail.value"),null,true));
		}

		if (!this.commissionRate.isDisabled()) {
			this.commissionRate.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_CommissionRate.value"),2,true,false,100));
		}
		
		if (PennantConstants.CITY_FREETEXT) {
			if(!this.cityName.isReadonly()){
				this.cityName.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_CityName.value"),PennantRegularExpressions.REGEX_NAME, false));
			} 
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
		this.phoneAreaCode.setConstraint("");
		this.phoneCountryCode.setConstraint("");
		this.brokerAddrFax.setConstraint("");
		this.faxCountryCode.setConstraint("");
		this.faxAreaCode.setConstraint("");
		this.brokerEmail.setConstraint("");
		this.agreementRef.setConstraint("");
		this.feeOnUnsold.setConstraint("");
		this.accountNumber.setConstraint("");
		this.commodityDetail.setConstraint("");
		this.commissionRate.setConstraint("");
		this.cityName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 *Sets the Validation by setting the accordingly constraints to the LOVFields.
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering ");
		this.lovDescBrokerCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerCustCIF.value"),null,true));
		this.brokerAddrCountry.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerAddrCountry.value"),null,true,true));
		this.brokerAddrProvince.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerAddrProvince.value"),null,true,true));
		if(!PennantConstants.CITY_FREETEXT) {
			this.brokerAddrCity.setConstraint(new PTStringValidator(Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerAddrCity.value"),null,false,true));
		}
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
		this.btnSearchBrokerCustID.setVisible(false);
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
		this.cityName.setReadonly(true);
		this.brokerAddrZIP.setReadonly(true);
		this.brokerAddrPhone.setReadonly(true);
		this.phoneAreaCode.setReadonly(true);
		this.phoneCountryCode.setReadonly(true);
		this.brokerAddrFax.setReadonly(true);
		this.faxAreaCode.setReadonly(true);
		this.faxCountryCode.setReadonly(true);
		this.brokerEmail.setReadonly(true);
		this.agreementRef.setReadonly(true);
		this.feeOnUnsold.setDisabled(true);
		this.accountNumber.setReadonly(true);
		this.commissionRate.setReadonly(true);
		this.btnSearchCommodityDetail.setDisabled(true);
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
		this.phoneAreaCode.setValue("");
		this.phoneCountryCode.setValue("");
		this.brokerAddrFax.setValue("");
		this.faxCountryCode.setValue("");
		this.faxAreaCode.setValue("");
		this.brokerEmail.setValue("");
		this.commodityDetail.setValue("");

		this.brokerAddrCountry.setDescription("");
		this.brokerAddrProvince.setDescription("");
		this.brokerAddrCity.setDescription("");
		this.feeOnUnsold.setValue("");
		this.commissionRate.setText("");
		this.cityName.setText("");
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
	
	@Override
	protected String getReference() {
		return String.valueOf(this.commodityBrokerDetail.getBrokerCode());
	}


	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getCommodityBrokerDetailListCtrl().search();
	}


	// CRUD operations

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
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")+ "\n\n --> " + 
				Labels.getLabel("label_CommodityBrokerDetailDialog_BrokerCode.value")+" : "+aCommodityBrokerDetail.getBrokerCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aCommodityBrokerDetail.getRecordType())){
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
					closeDialog(); 
				}

			}catch (DataAccessException e){
				MessageUtil.showError(e);
			}

		}
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
			this.brokerAddrProvince.setReadonly(isReadOnly("CommodityBrokerDetailDialog_brokerAddrProvince"));
			this.brokerAddrCity.setReadonly(isReadOnly("CommodityBrokerDetailDialog_brokerAddrCity"));
			this.brokerAddrCountry.setMandatoryStyle(true);
			this.brokerAddrProvince.setMandatoryStyle(true);
		}
		
		this.brokerCustID.setDisabled(isReadOnly("CommodityBrokerDetailDialog_brokerCustID"));
		this.btnSearchBrokerCustID.setDisabled(isReadOnly("CommodityBrokerDetailDialog_brokerCustID"));
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
		this.phoneAreaCode.setReadonly(isReadOnly("CommodityBrokerDetailDialog_brokerAddrPhone"));
		this.phoneCountryCode.setReadonly(isReadOnly("CommodityBrokerDetailDialog_brokerAddrPhone"));
		this.brokerAddrFax.setReadonly(isReadOnly("CommodityBrokerDetailDialog_brokerAddrFax"));
		this.faxAreaCode.setReadonly(isReadOnly("CommodityBrokerDetailDialog_brokerAddrFax"));
	  	this.faxCountryCode.setReadonly(isReadOnly("CommodityBrokerDetailDialog_brokerAddrFax"));
		this.brokerEmail.setReadonly(isReadOnly("CommodityBrokerDetailDialog_brokerEmail"));
		this.agreementRef.setReadonly(isReadOnly("CommodityBrokerDetailDialog_agreementRef"));
		this.feeOnUnsold.setDisabled(isReadOnly("CommodityBrokerDetailDialog_feeOnUnsold"));
		this.btnSearchCommodityDetail.setDisabled(isReadOnly("CommodityBrokerDetailDialog_CommodityDetail"));
		this.accountNumber.setReadonly(isReadOnly("CommodityBrokerDetailDialog_accountNumber"));
		this.commissionRate.setReadonly(isReadOnly("COMMODITYBROKERDETAILDIALOG_COMMISSIONRATE"));
		this.cityName.setReadonly(isReadOnly("CommodityBrokerDetailDialog_brokerAddrCity"));
		
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
			//btnCancel.setVisible(true);
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

		// force validation, if on, than execute by component.getValue()
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
			if (StringUtils.isBlank(aCommodityBrokerDetail.getRecordType())){
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
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
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

		aCommodityBrokerDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCommodityBrokerDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCommodityBrokerDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCommodityBrokerDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCommodityBrokerDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCommodityBrokerDetail);
				}

				if (isNotesMandatory(taskId, aCommodityBrokerDetail)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}


			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode= getFirstTaskOwner();
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {

						if(nextRoleCode.length()>1){
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aCommodityBrokerDetail.setTaskId(taskId);
			aCommodityBrokerDetail.setNextTaskId(nextTaskId);
			aCommodityBrokerDetail.setRoleCode(getRole());
			aCommodityBrokerDetail.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aCommodityBrokerDetail, tranType);

			String operationRefs = getServiceOperations(taskId, aCommodityBrokerDetail);

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

				if (StringUtils.isBlank(method)){
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
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999
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
						deleteNotes(getNotes(this.commodityBrokerDetail),true);
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
			logger.error("Exception: ", e);
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetail>> getOverideMap() {
		return overideMap;
	}
}

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
 * FileName    		:  CoOwnerDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-09-2013    														*
 *                                                                  						*
 * Modified Date    :  10-09-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-09-2013       Pennant	                 0.1                                            * 
 *                                                                                          * 
 * 11-05-2013       Sai Krishna              0.2          1. PSD - 126100                   * 
 *                                                        City not populated for existing   * 
 *                                                        customer                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.collateral.collateralsetup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CoOwnerDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTEmailValidator;
import com.pennant.util.Constraint.PTMobileNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.ScreenCTL;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class CoOwnerDetailDialogCtrl extends GFCBaseCtrl<CoOwnerDetail> {
	private static final long			serialVersionUID	= 1L;
	private static final Logger			logger				= Logger.getLogger(CoOwnerDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window					window_CoOwnerDetailDialog;

	protected Row						row0;
	protected Checkbox					bankCustomer;

	protected Combobox					coOwnerIDType;
	protected Decimalbox				coOwnerPercentage;

	protected Textbox					coOwnerCIF;
	protected Button					btnSearchCoOwnerCIF;
	protected Button					viewCustInfo;
	protected Button					btnViewCoOwnerProof;

	protected Uppercasebox				coOwnerIDNumber;
	protected Textbox					coOwnerCIFName;

//	protected Textbox					phoneCountryCode;
	//protected Textbox					phoneAreaCode;
	protected Textbox					mobileNo;
	protected Textbox					emailId;

	protected Textbox					coOwnerProofName;
	protected Button					btnUploadCoOwnerProof;

	protected Textbox					addrHNbr;
	protected Textbox					flatNbr;

	protected Textbox					addrStreet;
	protected Textbox					addrLine1;

	protected Textbox					addrLine2;
	protected Textbox					poBox;

	protected ExtendedCombobox			addrCountry;
	protected ExtendedCombobox			addrProvince;

	protected ExtendedCombobox			addrCity;
	protected Textbox					cityName;
	protected Textbox					addrZIP;

	protected Textbox					remarks;

	protected Groupbox					gb_statusDetails;

	protected Hlayout					hlayout_CoOwnerCIF;
	protected Hlayout					hlayout_CoOwnerIDNumber;

	protected Space						space_CoOwnerCIF;
	protected Space						space_CoOwnerPercentage;
	protected Space						space_CoOwnerIDType;
	protected Space						space_Name;
	protected Space						space_MobileNo;
	protected Space						space_EmailId;
	protected Space						space_CoOwnerProof;
	protected Space						space_addrHNbr;
	protected Space						space_addrStreet;
	protected Space						space_poBox;
	protected Space						space_CoOwnerIDNumber;

	private CollateralSetupDialogCtrl	collateralSetupDialogCtrl;
	private CoOwnerDetail				coOwnerDetail;
	private transient PagedListService	pagedListService;

	private boolean						enqModule			= false;
	private int							index;
	private String						cif[]				= null;
	private Customer					customer			= null;
	private boolean						newRecord			= false;
	private List<ValueLabel>			listCoOwnerIDType	= PennantAppUtil.getIdentityType();
	private byte[]						coOwnerProofContent;
	private String						addrCountryTemp;
	private String						addrProvinceTemp;
	private List<CoOwnerDetail>			coOwnerDetailList;
	private String						primaryCustCif;
	private boolean						newCoOwnerDetails	= false;
	
	public CoOwnerDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CoOwnerDetailDialog";
	}

	public void onCreate$window_CoOwnerDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CoOwnerDetailDialog);

		try {
			if (PennantConstants.CITY_FREETEXT) {
				this.addrCity.setVisible(false);
				this.cityName.setVisible(true);
			} else {
				this.addrCity.setVisible(true);
				this.cityName.setVisible(false);
			}

			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} 
			// READ OVERHANDED params !
			if (arguments.containsKey("coOwnerDetail")) {
				this.coOwnerDetail = (CoOwnerDetail) arguments.get("coOwnerDetail");
				CoOwnerDetail befImage = new CoOwnerDetail();
				BeanUtils.copyProperties(this.coOwnerDetail, befImage);
				this.coOwnerDetail.setBefImage(befImage);
				setCoOwnerDetail(this.coOwnerDetail);
			} else {
				setCoOwnerDetail(null);
			}
			if (arguments.containsKey("index")) {
				this.index = (Integer) arguments.get("index");
			}

			if (arguments.containsKey("filter")) {
				this.cif = (String[]) arguments.get("filter");
			}

			if (arguments.containsKey("primaryCustCif")) {
				primaryCustCif = (String) arguments.get("primaryCustCif");
			}
			
			//collateralSetupCtrl
			if (arguments.containsKey("collateralSetupCtrl")) {
				setCollateralSetupDialogCtrl((CollateralSetupDialogCtrl) arguments.get("collateralSetupCtrl"));
				setNewCoOwnerDetails(true);
				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.coOwnerDetail.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					setRole((String) arguments.get("roleCode"));
					getUserWorkspace().allocateRoleAuthorities(getRole(), "CoOwnerDetailDialog");
				}
			}
			doLoadWorkFlow(this.coOwnerDetail.isWorkflow(), this.coOwnerDetail.getWorkflowId(),
					this.coOwnerDetail.getNextTaskId());

			doCheckRights();
			doSetFieldProperties();
			doShowDialog(getCoOwnerDetail());
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
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
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doWriteBeanToComponents(this.coOwnerDetail.getBefImage());
		doEdit();
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
		MessageUtil.showHelpWindow(event, window_CoOwnerDetailDialog);
		logger.debug("Leaving" + event.toString());
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
		logger.debug("Entering" + event.toString());
		try {
			ScreenCTL.displayNotes(getNotes("CoOwnerDetail", String.valueOf(getCoOwnerDetail().getCoOwnerId()), getCoOwnerDetail()
							.getVersion()), this);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchCoOwnerCIF(Event event) {
		this.customer = null;
		doClearMessage();
		if (cif != null) {
			Filter filter[] = new Filter[1];
			filter[0] = new Filter("CustCIF", cif, Filter.OP_NOT_IN);
			Object dataObject = ExtendedSearchListBox.show(this.window_CoOwnerDetailDialog, "CustomerData", filter);
			if (dataObject instanceof String) {
				this.coOwnerCIF.setValue(dataObject.toString());
				this.coOwnerCIFName.setValue("");
				this.coOwnerIDNumber.setValue("");
				this.mobileNo.setValue("");
				this.emailId.setValue("");
				this.coOwnerProofName.setValue("");
			} else {
				customer = (Customer) dataObject;
				if (customer != null) {
					this.coOwnerCIF.setValue(customer.getCustCIF());
					this.coOwnerCIFName.setValue(customer.getCustShrtName());
					this.coOwnerIDNumber.setValue(customer.getCustCRCPR());
					this.mobileNo.setValue(customer.getPhoneNumber());
					this.emailId.setValue(customer.getEmailID());
					getCoOwnerDetail().setCustomerId(customer.getCustID());
					dosetCustAddress(customer.getCustID());
				}
			}
		}
		setCustomerDetails(customer);
	}

	public void dosetCustAddress(long custID) {
		CustomerAddres customerAddress = getCustAddress(custID);
		if (customerAddress != null) {
			this.addrHNbr.setValue(customerAddress.getCustAddrHNbr());
			this.addrStreet.setValue(customerAddress.getCustAddrStreet());
			this.cityName.setValue(customerAddress.getCustAddrCity());
			// ### 11-05-2018 Ticket ID : 126100
			this.addrCity.setValue(customerAddress.getCustAddrCity());
			this.addrCity.setDescription(customerAddress.getLovDescCustAddrCityName());
			this.addrCountry.setValue(customerAddress.getCustAddrCountry());
			this.addrCountry.setDescription(customerAddress.getLovDescCustAddrCountryName());
			this.addrProvince.setValue(customerAddress.getCustAddrProvince());
			this.addrProvince.setDescription(customerAddress.getLovDescCustAddrProvinceName());
			this.addrLine1.setValue(customerAddress.getCustAddrLine1());
			this.addrLine2.setValue(customerAddress.getCustAddrLine2());
			this.poBox.setValue(customerAddress.getCustPOBox());
			this.flatNbr.setValue(customerAddress.getCustFlatNbr());
			this.addrZIP.setValue(customerAddress.getCustAddrZIP());
		}
	}

	/*
	 * Method to get the Customer Address Details when CustID is Entered
	 */
	public CustomerAddres getCustAddress(long custID) {
		logger.debug("Entering");
		CustomerAddres customerAddress = null;
		JdbcSearchObject<CustomerAddres> searchObject = new JdbcSearchObject<CustomerAddres>(CustomerAddres.class);
		searchObject.addTabelName("CustomerAddresses");
		searchObject.addFilterEqual("CustID", custID);
		List<CustomerAddres> custAddress = pagedListService.getBySearchObject(searchObject);
		if (custAddress != null && !custAddress.isEmpty()) {
			return custAddress.get(0);
		}
		logger.debug("Leaving");

		return customerAddress;
	}

	public void setCustomerDetails(Customer customer) {
		if (customer != null) {
			getCoOwnerDetail().setCoOwnerCIF(customer.getCustCIF());
		}
	}

	public void onClick$viewCustInfo(Event event) {
		
		this.coOwnerCIF.setConstraint("");
		this.coOwnerCIF.setErrorMessage("");
		if ((!this.btnSearchCoOwnerCIF.isDisabled()) && StringUtils.isEmpty(this.coOwnerCIF.getValue())) {
			throw new WrongValueException(this.coOwnerCIF, Labels.getLabel("FIELD_NO_INVALID",
					new String[] { Labels.getLabel("label_CoOwnerDetailDialog_CoOwnerCIF/ID.value") }));
		}
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("custid", customer.getCustID());
			map.put("custCIF", customer.getCustCIF());
			map.put("custShrtName", customer.getCustShrtName());
			map.put("finFormatter",CurrencyUtil.getFormat(customer.getCustBaseCcy()));
			map.put("finance", true);
			if (StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/FinCustomerDetailsEnq.zul",
						this.window_CoOwnerDetailDialog, map);
			} else {
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Enquiry/CustomerSummary.zul",
						this.window_CoOwnerDetailDialog, map);
			}
		} catch (Exception e) {
			logger.error("Exception: Opening window", e);
		}
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aCoOwnerDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(CoOwnerDetail aCoOwnerDetail) throws InterruptedException {
		logger.debug("Entering");
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.bankCustomer.focus();
		} else {
			if (isNewCoOwnerDetails()) {
				doEdit();
			} else if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		try {
			doWriteBeanToComponents(aCoOwnerDetail);
			onCheckBankCustomer();
			if (isNewCoOwnerDetails()) {
				this.groupboxWf.setVisible(false);
			}
			this.window_CoOwnerDetailDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.coOwnerIDType.setDisabled(false);
			this.bankCustomer.setDisabled(false);
			this.btnSearchCoOwnerCIF.setDisabled(false);
		} else {
			this.btnCancel.setVisible(true);
			this.coOwnerIDType.setDisabled(true);
			this.bankCustomer.setDisabled(true);
			this.btnSearchCoOwnerCIF.setDisabled(true);
		}
		this.coOwnerPercentage.setDisabled(isReadOnly("CoOwnerDetailDialog_CoOwnerPercentage"));
		this.coOwnerCIF.setReadonly(isReadOnly("CoOwnerDetailDialog_CoOwnerCIF"));  
		this.coOwnerCIFName.setReadonly(isReadOnly("CoOwnerDetailDialog_CoOwnerCIFName"));
		this.coOwnerIDNumber.setReadonly(isReadOnly("CoOwnerDetailDialog_CoOwnerIDNumber"));
		this.coOwnerProofName.setReadonly(isReadOnly("CoOwnerDetailDialog_CoOwnerProofName"));
		this.mobileNo.setReadonly(isReadOnly("CoOwnerDetailDialog_MobileNo"));
		this.emailId.setReadonly(isReadOnly("CoOwnerDetailDialog_EmailId"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.coOwnerDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (isNewCoOwnerDetails()) {
				if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(isNewCoOwnerDetails());
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewCoOwnerDetails()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
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
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		if (!enqModule) {
			getUserWorkspace().allocateAuthorities("CoOwnerDetailDialog", getRole());
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CoOwnerDetailDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CoOwnerDetailDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CoOwnerDetailDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CoOwnerDetailDialog_btnSave"));
			this.btnUploadCoOwnerProof.setVisible(getUserWorkspace().isAllowed("button_CoOwnerDetailDialog_btnUploadCoOwnerProof"));
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.coOwnerCIF.setMaxlength(LengthConstants.LEN_CIF);
		this.coOwnerIDNumber.setMaxlength(20);
		this.coOwnerCIFName.setMaxlength(100);
		this.coOwnerPercentage.setMaxlength(6);
		this.coOwnerPercentage.setFormat(PennantApplicationUtil.getAmountFormate(2));
		this.coOwnerPercentage.setScale(2);
		this.mobileNo.setMaxlength(10);
		this.emailId.setMaxlength(200);
		this.remarks.setMaxlength(500);
		this.coOwnerProofName.setMaxlength(500);

		this.addrHNbr.setMaxlength(50);
		this.flatNbr.setMaxlength(50);
		this.addrStreet.setMaxlength(50);
		this.addrLine1.setMaxlength(50);
		this.addrLine2.setMaxlength(50);
		this.poBox.setMaxlength(8);

		this.addrCountry.setMaxlength(2);
		this.addrCountry.setTextBoxWidth(121);
		this.addrCountry.setSpacing("2px");
		this.addrCountry.setMandatoryStyle(true);
		this.addrCountry.setModuleName("Country");
		this.addrCountry.setValueColumn("CountryCode");
		this.addrCountry.setDescColumn("CountryDesc");
		this.addrCountry.setValidateColumns(new String[] { "CountryCode" });

		this.addrProvince.setMaxlength(8);
		this.addrProvince.setTextBoxWidth(121);
		this.addrCountry.setSpacing("2px");
		this.addrProvince.setMandatoryStyle(true);
		this.addrProvince.setModuleName("Province");
		this.addrProvince.setValueColumn("CPProvince");
		this.addrProvince.setDescColumn("CPProvinceName");
		this.addrProvince.setValidateColumns(new String[] { "CPProvince" });

		this.addrCity.setMaxlength(8);
		this.addrCity.setTextBoxWidth(121);
		this.addrCountry.setSpacing("2px");
		this.addrCity.setMandatoryStyle(false);
		this.addrCity.setModuleName("City");
		this.addrCity.setValueColumn("PCCity");
		this.addrCity.setDescColumn("PCCityName");
		this.addrCity.setValidateColumns(new String[] { "PCCity" });
		this.cityName.setMaxlength(8);
		this.addrZIP.setMaxlength(50);

		setStatusDetails(gb_statusDetails, groupboxWf, south, enqModule);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCoOwnerDetail
	 *            CoOwnerDetail
	 */
	public void doWriteBeanToComponents(CoOwnerDetail aCoOwnerDetail) {
		logger.debug("Entering");
		this.bankCustomer.setChecked(aCoOwnerDetail.isBankCustomer());
		if (!aCoOwnerDetail.isBankCustomer()) {
			fillComboBox(this.coOwnerIDType, aCoOwnerDetail.getCoOwnerIDType(), listCoOwnerIDType, "");
			if (!PennantConstants.List_Select.equals(this.coOwnerIDType.getSelectedItem().getValue().toString())) {
				aCoOwnerDetail.setCoOwnerIDTypeName(getComboboxValue(coOwnerIDType));
			}
		} else {
			fillComboBox(this.coOwnerIDType, PennantConstants.List_Select, listCoOwnerIDType, "");
		}
		this.coOwnerCIFName.setValue(aCoOwnerDetail.getCoOwnerCIFName());
		this.coOwnerIDNumber.setValue(aCoOwnerDetail.getCoOwnerIDNumber());
		this.coOwnerPercentage.setValue(aCoOwnerDetail.getCoOwnerPercentage());
		this.mobileNo.setValue(aCoOwnerDetail.getMobileNo());
		this.emailId.setValue(aCoOwnerDetail.getEmailId());
		this.coOwnerProofContent = aCoOwnerDetail.getCoOwnerProof();
		this.coOwnerProofName.setValue(aCoOwnerDetail.getCoOwnerProofName());
		this.remarks.setValue(aCoOwnerDetail.getRemarks());

		if (!aCoOwnerDetail.isBankCustomer()) {
			this.addrHNbr.setValue(aCoOwnerDetail.getAddrHNbr());
			this.flatNbr.setValue(aCoOwnerDetail.getFlatNbr());
			this.addrStreet.setValue(aCoOwnerDetail.getAddrStreet());
			this.addrLine1.setValue(aCoOwnerDetail.getAddrLine1());
			this.addrLine2.setValue(aCoOwnerDetail.getAddrLine2());
			this.poBox.setValue(aCoOwnerDetail.getPOBox());
			this.addrCountry.setValue(aCoOwnerDetail.getAddrCountry());
			this.addrProvince.setValue(aCoOwnerDetail.getAddrProvince());
			this.addrCity.setValue(aCoOwnerDetail.getAddrCity());
			this.addrZIP.setValue(aCoOwnerDetail.getAddrZIP());
			this.addrCountry.setDescription(aCoOwnerDetail.getLovDescAddrCountryName());
			this.addrProvince.setDescription(aCoOwnerDetail.getLovDescAddrProvinceName());
			this.addrCity.setDescription(aCoOwnerDetail.getLovDescAddrCityName());
			this.cityName.setValue(aCoOwnerDetail.getAddrCity());
		} else {
			this.coOwnerCIF.setValue(getCustData(aCoOwnerDetail.getCustomerId()));
			dosetCustAddress(aCoOwnerDetail.getCustomerId());
		}
		getcoOwnerIdNumber();
		addrCountryTemp = this.addrCountry.getValue();
		Filter[] provinceFilters = new Filter[1];
		provinceFilters[0] = new Filter("CPCountry", this.addrCountry.getValue(), Filter.OP_EQUAL);
		this.addrProvince.setFilters(provinceFilters);

		addrProvinceTemp = this.addrProvince.getValue();
		Filter[] filters = new Filter[2];
		filters[0] = new Filter("PCCountry", this.addrCountry.getValue(), Filter.OP_EQUAL);
		filters[1] = new Filter("PCProvince", this.addrProvince.getValue(), Filter.OP_EQUAL);
		this.addrCity.setFilters(filters);
		logger.debug("Leaving");
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
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CoOwnerDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Uploading Proof Details File
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onUpload$btnUploadCoOwnerProof(UploadEvent event) throws Exception {
		logger.debug("Entering" + event.toString());
		Media media = event.getMedia();
		this.coOwnerProofName.setValue(media.getName());
		this.coOwnerProofContent = IOUtils.toByteArray(media.getStreamData());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCoOwnerDetail
	 */
	public void doWriteComponentsToBean(CoOwnerDetail aCoOwnerDetail) {
		logger.debug("Entering");
		
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Bank Customer
		try {
			aCoOwnerDetail.setBankCustomer(this.bankCustomer.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// ID Type
		try {
			String strCoOwnerIDType = null;
			if (this.coOwnerIDType.getSelectedItem() != null) {
				strCoOwnerIDType = this.coOwnerIDType.getSelectedItem().getValue().toString();
			}
			if (strCoOwnerIDType != null && !PennantConstants.List_Select.equals(strCoOwnerIDType)) {
				aCoOwnerDetail.setCoOwnerIDType(strCoOwnerIDType);
				aCoOwnerDetail.setCoOwnerIDTypeName(this.coOwnerIDType.getSelectedItem().getLabel());
			} else {
				aCoOwnerDetail.setCoOwnerIDType(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// ID Number
		try {
			if (!this.bankCustomer.isChecked()) {
				aCoOwnerDetail.setCoOwnerIDNumber(this.coOwnerIDNumber.getValue());
			}
			getcoOwnerIdNumber();
			if (this.coOwnerIDType.getSelectedIndex() != 0) {
				if (this.coOwnerIDType.getSelectedItem().getValue().toString().equals(PennantConstants.CPRCODE)) {
					aCoOwnerDetail.setCoOwnerIDNumber(PennantApplicationUtil.unFormatEIDNumber(this.coOwnerIDNumber
							.getValue()));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Percentage
		try {
			BigDecimal percValue = this.coOwnerPercentage.getValue()==null ? BigDecimal.ZERO : this.coOwnerPercentage.getValue();
			
			List<CoOwnerDetail> coOwnerDetailsList = getCollateralSetupDialogCtrl().getCoOwnerDetailList();
			if (coOwnerDetailsList != null && !coOwnerDetailsList.isEmpty()) {
				for (CoOwnerDetail coOwnerDetail : coOwnerDetailsList) {
					percValue = percValue.add(coOwnerDetail.getCoOwnerPercentage());
				}
			}
			// Discussed with raju below validation changed.
			if (percValue.compareTo(new BigDecimal(100)) > 0) {
				throw new WrongValueException(coOwnerPercentage, Labels.getLabel("NUMBER_MAXVALUE_EQ", new String[] {
						Labels.getLabel("label_CoOwnerDetailDialog_CoOwnerPercentageValidation.value"), "100" }));
			}

			aCoOwnerDetail.setCoOwnerPercentage(this.coOwnerPercentage.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		// CoOwner CIF
		try {
			aCoOwnerDetail.setCoOwnerCIFName(this.coOwnerCIFName.getValue());
			aCoOwnerDetail.setCoOwnerCIF(this.coOwnerCIF.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Mobile No
		try {
			aCoOwnerDetail.setMobileNo(this.mobileNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Email Id
		try {
			aCoOwnerDetail.setEmailId(this.emailId.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Proof
		try {
			if (coOwnerProofContent != null) {
				aCoOwnerDetail.setCoOwnerProof(coOwnerProofContent);
				aCoOwnerDetail.setCoOwnerProofName(this.coOwnerProofName.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Proof Name
		try {
			if (!this.bankCustomer.isChecked() && (StringUtils.isBlank(this.coOwnerProofName.getValue()) || this.coOwnerProofName == null)) {
				throw new WrongValueException(this.coOwnerProofName, Labels.getLabel("MUST_BE_UPLOADED", new String[] { Labels.getLabel("label_CoOwnerDetailDialog_CoOwnerProof.value") }));
			}
			aCoOwnerDetail.setCoOwnerProofName(this.coOwnerProofName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Remarks
		try {
			aCoOwnerDetail.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		// city name and addrcity
		try {
			if (PennantConstants.CITY_FREETEXT) {
				aCoOwnerDetail.setAddrCity(this.cityName.getValue());
			} else {
				aCoOwnerDetail.setLovDescAddrCityName(this.addrCity.getDescription());
				aCoOwnerDetail.setAddrCity(this.addrCity.getValidatedValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Address Details
		try {
			aCoOwnerDetail.setAddrHNbr(this.addrHNbr.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCoOwnerDetail.setFlatNbr(this.flatNbr.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCoOwnerDetail.setAddrStreet(this.addrStreet.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCoOwnerDetail.setAddrLine1(this.addrLine1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCoOwnerDetail.setAddrLine2(this.addrLine2.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCoOwnerDetail.setPOBox(this.poBox.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCoOwnerDetail.setLovDescAddrCountryName(getLovDescription(this.addrCountry.getDescription()));
			aCoOwnerDetail.setAddrCountry(this.addrCountry.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCoOwnerDetail.setLovDescAddrProvinceName(this.addrProvince.getDescription());
			aCoOwnerDetail.setAddrProvince(this.addrProvince.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {

			if (PennantConstants.CITY_FREETEXT) {
				aCoOwnerDetail.setAddrCity(StringUtils.trimToNull(this.cityName.getValue()));
			} else {
				aCoOwnerDetail.setLovDescAddrCityName(StringUtils.trimToNull(this.addrCity.getDescription()));
				aCoOwnerDetail.setAddrCity(StringUtils.trimToNull(this.addrCity.getValidatedValue()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCoOwnerDetail.setAddrZIP(this.addrZIP.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();
		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		aCoOwnerDetail.setRecordStatus(this.recordStatus.getValue());

		setCoOwnerDetail(aCoOwnerDetail);
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		
		doClearMessage();
		
		if (this.bankCustomer.isChecked()) {
			this.coOwnerCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_CoOwnerDetailDialog_CoOwnerCIF/ID.value"), PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		
		if (!this.coOwnerPercentage.isReadonly()) {
			this.coOwnerPercentage.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CoOwnerDetailDialog_CoOwnerPercentage.value"), 2, true,false,1,100));
		}

		if (!this.bankCustomer.isChecked()) {
			
			if (!this.coOwnerIDNumber.isReadonly()) {
					
					if(StringUtils.equals(this.coOwnerIDType.getSelectedItem().getValue().toString(), PennantConstants.CPRCODE)){
						this.coOwnerIDNumber.setConstraint(new PTStringValidator(Labels
								.getLabel("label_GuarantorDetailDialog_GuarantorIDNumber.value"),
								PennantRegularExpressions.REGEX_AADHAR_NUMBER, true));
							
					}else if(StringUtils.equals(this.coOwnerIDType.getSelectedItem().getValue().toString(), PennantConstants.PANNUMBER)){
						if(this.coOwnerIDNumber.getConstraint()!=null){
							this.coOwnerIDNumber.setConstraint("");
						}
						this.coOwnerIDNumber.setConstraint(new PTStringValidator(Labels
								.getLabel("label_GuarantorDetailDialog_GuarantorIDNumber.value"),
								PennantRegularExpressions.REGEX_PANNUMBER, true));
					}else{
						this.coOwnerIDNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_GuarantorIDNumber.value"), null, true));					
					}
				}
			
			if (!this.coOwnerIDType.isDisabled()) {
				this.coOwnerIDType.setConstraint(new StaticListValidator(listCoOwnerIDType, Labels.getLabel("label_CoOwnerDetailDialog_CoOwnerIDType.value")));
			}
			
			if (!this.coOwnerCIFName.isReadonly()) {
				this.coOwnerCIFName.setConstraint(new PTStringValidator(Labels.getLabel("label_CoOwnerDetailDialog_Name.value"), PennantRegularExpressions.REGEX_ACC_HOLDER_NAME, true));
			}

			if (!this.mobileNo.isReadonly()) {
				this.mobileNo.setConstraint(new PTMobileNumberValidator(Labels.getLabel("label_CoOwnerDetailDialog_MobileNo.value"), true));
			}
			 
			if (!this.emailId.isReadonly()) {
				this.emailId.setConstraint(new PTEmailValidator(Labels.getLabel("label_CoOwnerDetailDialog_EmailId.value"), true));
			}

			if (!this.addrHNbr.isReadonly()) {
				this.addrHNbr.setConstraint(new PTStringValidator(Labels.getLabel("label_CoOwnerDetailDialog_AddrHNbr.value"), PennantRegularExpressions.REGEX_ADDRESS, true));
			}

			if (!this.flatNbr.isReadonly()) {
				this.flatNbr.setConstraint(new PTStringValidator(Labels.getLabel("label_CoOwnerDetailDialog_FlatNbr.value"), PennantRegularExpressions.REGEX_ADDRESS, false));
			}

			boolean addressConstraint = false;
			if (StringUtils.isBlank(this.addrStreet.getValue()) && StringUtils.isBlank(this.addrLine1.getValue()) && StringUtils.isBlank(this.addrLine2.getValue())) {
				addressConstraint = true;
			}
			if (!this.addrStreet.isReadonly() && addressConstraint) {
				this.addrStreet.setConstraint(new PTStringValidator(Labels.getLabel("label_CoOwnerDetailDialog_AddrStreet.value"), PennantRegularExpressions.REGEX_ADDRESS, true));
			}

			if (!this.addrLine1.isReadonly() && addressConstraint) {
				this.addrLine1.setConstraint(new PTStringValidator(Labels.getLabel("label_CoOwnerDetailDialog_AddrLine1.value"), PennantRegularExpressions.REGEX_ADDRESS, false));
			}

			if (!this.addrLine2.isReadonly() && addressConstraint) {
				this.addrLine2.setConstraint(new PTStringValidator(Labels.getLabel("label_CoOwnerDetailDialog_AddrLine2.value"), PennantRegularExpressions.REGEX_ADDRESS, false));
			}

			if (!this.poBox.isReadonly()) {
				this.poBox.setConstraint(new PTStringValidator(Labels.getLabel("label_CoOwnerDetailDialog_POBox.value"), PennantRegularExpressions.REGEX_NUMERIC, true));
			}

			if (!this.addrZIP.isReadonly()) {
				this.addrZIP.setConstraint(new PTStringValidator(Labels.getLabel("label_CoOwnerDetailDialog_AddrZIP.value"), PennantRegularExpressions.REGEX_ZIP, false));
			}

			if (PennantConstants.CITY_FREETEXT) {
				if (!this.cityName.isReadonly()) {
					this.cityName.setConstraint(new PTStringValidator(Labels.getLabel("label_CoOwnerDetailDialog_AddrCity.value"), PennantRegularExpressions.REGEX_NAME, false));
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.coOwnerIDType.setConstraint("");
		this.coOwnerIDNumber.setConstraint("");
		this.coOwnerCIF.setConstraint("");
		this.coOwnerCIFName.setConstraint("");
		this.coOwnerPercentage.setConstraint("");
		this.mobileNo.setConstraint("");
		this.emailId.setConstraint("");
		this.coOwnerProofName.setConstraint("");
		this.addrHNbr.setConstraint("");
		this.flatNbr.setConstraint("");
		this.addrStreet.setConstraint("");
		this.addrLine1.setConstraint("");
		this.addrLine2.setConstraint("");
		this.poBox.setConstraint("");
		this.addrZIP.setConstraint("");
		this.cityName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		
		this.addrCountry.setConstraint(new PTStringValidator(Labels.getLabel("label_CoOwnerDetailDialog_AddrCountry.value"), null, true, true));
		this.addrProvince.setConstraint(new PTStringValidator(Labels.getLabel("label_CoOwnerDetailDialog_AddrProvince.value"), null, true, true));
		if (!PennantConstants.CITY_FREETEXT) {
			this.addrCity.setConstraint(new PTStringValidator(Labels.getLabel("label_CoOwnerDetailDialog_AddrCity.value"), null, false, true));
		}
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveLOVValidation() {
		this.coOwnerCIFName.setConstraint("");
		this.addrCountry.setConstraint("");
		this.addrProvince.setConstraint("");
		this.addrCity.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.coOwnerCIFName.setErrorMessage("");
		this.coOwnerCIF.setErrorMessage("");
		this.coOwnerIDType.setErrorMessage("");
		this.coOwnerIDNumber.setErrorMessage("");
		this.coOwnerPercentage.setErrorMessage("");
		this.mobileNo.setErrorMessage("");
		this.emailId.setErrorMessage("");
		this.coOwnerProofName.setErrorMessage("");
		this.remarks.setErrorMessage("");
		this.addrHNbr.setErrorMessage("");
		this.flatNbr.setErrorMessage("");
		this.addrStreet.setErrorMessage("");
		this.addrLine1.setErrorMessage("");
		this.addrLine2.setErrorMessage("");
		this.poBox.setErrorMessage("");
		this.addrZIP.setErrorMessage("");
		this.addrCountry.setErrorMessage("");
		this.addrProvince.setErrorMessage("");
		this.addrCity.setErrorMessage("");
		this.cityName.setErrorMessage("");
		logger.debug("Leaving");
	}

	/*
	 * Is Bank customer or not checking
	 */
	public void onCheck$bankCustomer(Event event) {
		doClearMessage();
		doClear();
		onCheckBankCustomer();
	}

	private void onCheckBankCustomer() {
		logger.debug("Entering");
		if (this.bankCustomer.isChecked()) {
			this.coOwnerIDType.setDisabled(true);
			this.coOwnerCIF.setReadonly(true);
			this.btnSearchCoOwnerCIF.setVisible(true);
			this.coOwnerIDNumber.setDisabled(true);
			this.coOwnerIDNumber.setReadonly(true);
			this.coOwnerCIFName.setReadonly(true);
			this.mobileNo.setReadonly(true);
			this.emailId.setReadonly(true);
			this.btnUploadCoOwnerProof.setVisible(false);

			this.hlayout_CoOwnerCIF.setVisible(true);
			this.hlayout_CoOwnerIDNumber.setVisible(false);
			this.space_CoOwnerCIF.setVisible(true);
			this.space_CoOwnerPercentage.setVisible(true);

			fillComboBox(this.coOwnerIDType, PennantConstants.List_Select, listCoOwnerIDType, "");

			this.space_CoOwnerIDType.setSclass("");
			this.space_CoOwnerIDNumber.setSclass("");
			this.space_Name.setSclass("");
			this.space_MobileNo.setSclass("");
			this.space_EmailId.setSclass("");
			this.space_CoOwnerProof.setSclass("");

			this.coOwnerProofName.setValue("");
			this.coOwnerProofContent = null;

			// Address details
			this.addrHNbr.setReadonly(true);
			this.space_addrHNbr.setSclass("");
			this.flatNbr.setReadonly(true);
			this.addrStreet.setReadonly(true);
			this.space_addrStreet.setSclass("");
			this.addrLine1.setReadonly(true);
			this.addrLine2.setReadonly(true);
			this.poBox.setReadonly(true);
			this.space_poBox.setSclass("");
			this.addrCountry.setReadonly(true);
			this.addrProvince.setReadonly(true);
			this.addrCity.setReadonly(true);
			this.addrZIP.setReadonly(true);
			this.cityName.setReadonly(true);

		} else {
			this.coOwnerPercentage.setReadonly(isReadOnly("CoOwnerDetailDialog_CoOwnerPercentage"));
			this.coOwnerIDType.setDisabled(isReadOnly("CoOwnerDetailDialog_CoOwnerIDType"));
			this.coOwnerCIF.setReadonly(isReadOnly("CoOwnerDetailDialog_CoOwnerCIF"));
			this.coOwnerCIFName.setReadonly(isReadOnly("CoOwnerDetailDialog_CoOwnerCIFName"));
			this.btnSearchCoOwnerCIF.setVisible(false);
			this.coOwnerIDNumber.setDisabled(isReadOnly("CoOwnerDetailDialog_CoOwnerIDNumber"));
			this.coOwnerIDNumber.setReadonly(isReadOnly("CoOwnerDetailDialog_CoOwnerIDNumber"));
			this.coOwnerCIF.setReadonly(isReadOnly("CoOwnerDetailDialog_CoOwnerCIFName"));
			this.mobileNo.setReadonly(isReadOnly("CoOwnerDetailDialog_MobileNo"));
			this.emailId.setReadonly(isReadOnly("CoOwnerDetailDialog_EmailId"));
			this.btnUploadCoOwnerProof.setVisible(getUserWorkspace().isAllowed("button_CoOwnerDetailDialog_btnUploadCoOwnerProof"));
			this.hlayout_CoOwnerCIF.setVisible(false);
			this.hlayout_CoOwnerIDNumber.setVisible(true);
			this.space_CoOwnerIDType.setVisible(true);
			this.space_CoOwnerIDNumber.setVisible(true);
			this.space_Name.setVisible(true);
			this.space_MobileNo.setVisible(true);
			this.space_EmailId.setVisible(true);
			this.space_CoOwnerProof.setVisible(true);
			this.space_CoOwnerCIF.setVisible(true);
			this.space_CoOwnerPercentage.setVisible(true);
			this.space_CoOwnerIDType.setSclass(PennantConstants.mandateSclass);
			this.space_CoOwnerIDNumber.setSclass(PennantConstants.mandateSclass);
			this.space_EmailId.setSclass(PennantConstants.mandateSclass);
			this.space_Name.setSclass(PennantConstants.mandateSclass);
			this.space_MobileNo.setSclass(PennantConstants.mandateSclass);
			this.space_CoOwnerProof.setSclass(PennantConstants.mandateSclass);

			this.addrHNbr.setReadonly(isReadOnly("CoOwnerDetailDialog_addrHNbr"));
			this.flatNbr.setReadonly(isReadOnly("CoOwnerDetailDialog_flatNbr"));
			this.addrStreet.setReadonly(isReadOnly("CoOwnerDetailDialog_addrStreet"));
			this.addrLine1.setReadonly(isReadOnly("CoOwnerDetailDialog_addrLine1"));
			this.addrLine2.setReadonly(isReadOnly("CoOwnerDetailDialog_addrLine2"));
			this.poBox.setReadonly(isReadOnly("CoOwnerDetailDialog_poBox"));
			this.addrCountry.setReadonly(isReadOnly("CoOwnerDetailDialog_addrCountry"));
			this.addrProvince.setReadonly(isReadOnly("CoOwnerDetailDialog_addrProvince"));
			this.addrCity.setReadonly(isReadOnly("CoOwnerDetailDialog_addrCity"));
			this.cityName.setReadonly(isReadOnly("CoOwnerDetailDialog_addrCity"));
			this.addrZIP.setReadonly(isReadOnly("CoOwnerDetailDialog_addrZIP"));
			this.space_addrHNbr.setSclass(PennantConstants.mandateSclass);
			this.space_addrStreet.setSclass(PennantConstants.mandateSclass);
			this.space_poBox.setSclass(PennantConstants.mandateSclass);
			this.addrCountry.setMandatoryStyle(true);
			this.addrProvince.setMandatoryStyle(true);
			this.addrCity.setMandatoryStyle(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * Deletes a CoOwnerDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final CoOwnerDetail aCoOwnerDetail = new CoOwnerDetail();
		BeanUtils.copyProperties(getCoOwnerDetail(), aCoOwnerDetail);
		String tranType = PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ (aCoOwnerDetail.isBankCustomer() ? Labels.getLabel("label_CoOwnerDetailDialog_CoOwnerCIF/ID.value")
						+ " : " + this.coOwnerCIF.getValue() : Labels .getLabel("label_CoOwnerDetailDialog_CoOwnerIDType.value")
						+ " : " + aCoOwnerDetail.getCoOwnerIDType());

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aCoOwnerDetail.getRecordType())) {
				aCoOwnerDetail.setVersion(aCoOwnerDetail.getVersion() + 1);
				aCoOwnerDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				aCoOwnerDetail.setNewRecord(true);
				if (isWorkFlowEnabled()) {
					aCoOwnerDetail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (isNewCoOwnerDetails()) {
					tranType = PennantConstants.TRAN_DEL;
					AuditHeader auditHeader = newCoOwnerDetailProcess(aCoOwnerDetail, tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_CoOwnerDetailDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
						if (getCollateralSetupDialogCtrl() != null) {
							getCollateralSetupDialogCtrl().doFillCoOwnerDetails(this.coOwnerDetailList);
						}
						closeDialog();
					}
				}
			} catch (DataAccessException e) {
				logger.error("Exception: ", e);
				showMessage(e);
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
		this.coOwnerCIF.setValue("");
		this.coOwnerCIFName.setValue("");
		this.coOwnerIDType.setSelectedIndex(0);
		this.coOwnerIDNumber.setValue("");
		this.coOwnerPercentage.setValue("0");
		this.mobileNo.setValue("");
		this.emailId.setValue("");
		this.coOwnerProofName.setValue("");
		this.addrHNbr.setValue("");
		this.flatNbr.setValue("");
		this.addrStreet.setValue("");
		this.addrLine1.setValue("");
		this.addrLine2.setValue("");
		this.poBox.setValue("");
		this.addrCountry.setValue("");
		this.addrCountry.setDescription("");
		this.addrProvince.setValue("");
		this.addrProvince.setDescription("");
		this.addrCity.setValue("");
		this.addrCity.setDescription("");
		this.addrZIP.setValue("");
		this.remarks.setValue("");
		this.cityName.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final CoOwnerDetail aCoOwnerDetail = new CoOwnerDetail();
		BeanUtils.copyProperties(getCoOwnerDetail(), aCoOwnerDetail);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doRemoveValidation();
		doRemoveLOVValidation();
		doSetValidation();
		// fill the DocumentDetails object with the components data
		doWriteComponentsToBean(aCoOwnerDetail);
		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here
		isNew = aCoOwnerDetail.isNew();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCoOwnerDetail.getRecordType())) {
				aCoOwnerDetail.setVersion(aCoOwnerDetail.getVersion() + 1);
				if (isNew) {
					aCoOwnerDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCoOwnerDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCoOwnerDetail.setNewRecord(true);
				}
			}
		} else {
			if (isNewCoOwnerDetails()) {
				if (isNewRecord()) {
					aCoOwnerDetail.setVersion(1);
					aCoOwnerDetail.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
				if (StringUtils.isBlank(aCoOwnerDetail.getRecordType())) {
					aCoOwnerDetail.setVersion(aCoOwnerDetail.getVersion() + 1);
					aCoOwnerDetail.setRecordType(PennantConstants.RCD_UPD);
					aCoOwnerDetail.setNewRecord(true);
				}
				if (aCoOwnerDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aCoOwnerDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}
			} else {
				aCoOwnerDetail.setVersion(aCoOwnerDetail.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}
		// save it to database
		try {
			if (isNewCoOwnerDetails()) {
				AuditHeader auditHeader = newCoOwnerDetailProcess(aCoOwnerDetail, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CoOwnerDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					if (getCollateralSetupDialogCtrl() != null) {
						getCollateralSetupDialogCtrl().doFillCoOwnerDetails(this.coOwnerDetailList);
					}
					closeDialog();
				}
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
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
	private AuditHeader newCoOwnerDetailProcess(CoOwnerDetail aCoOwnerDetail, String tranType) {
		boolean recordAdded = false;
		boolean duplicateRecord = false;

		AuditHeader auditHeader = getAuditHeader(aCoOwnerDetail, tranType);
		this.coOwnerDetailList = new ArrayList<CoOwnerDetail>();
		String[] valueParm = new String[1];
		String[] errParm = new String[1];

		if (aCoOwnerDetail.isBankCustomer()) {
			valueParm[0] = aCoOwnerDetail.getCoOwnerCIF();
		} else {
			if (aCoOwnerDetail.getCoOwnerIDType().equals(PennantConstants.CPRCODE)) {
				valueParm[0] = PennantApplicationUtil.formatEIDNumber(aCoOwnerDetail.getCoOwnerIDNumber());
			} else {
				valueParm[0] = aCoOwnerDetail.getCoOwnerIDNumber();
			}
		}
		errParm[0] = PennantJavaUtil.getLabel("label_CoOwnerCIF") + ": " + valueParm[0];
		// Checks whether CoOwner customerCIF is same as actual custCIF
		if (StringUtils.equals(primaryCustCif, aCoOwnerDetail.getCoOwnerCIF())) {
			auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), getUserWorkspace().getUserLanguage()));
		}
		List<CoOwnerDetail> oldCoOwnerDetailsList = null;
		if (getCollateralSetupDialogCtrl() != null) {
			oldCoOwnerDetailsList = getCollateralSetupDialogCtrl().getCoOwnerDetailList();
		}
		if (oldCoOwnerDetailsList != null && !oldCoOwnerDetailsList.isEmpty()) {
			for (CoOwnerDetail oldCoOwnerDetail : oldCoOwnerDetailsList) {
				if (!aCoOwnerDetail.isBankCustomer() && !oldCoOwnerDetail.isBankCustomer()) {
					if (oldCoOwnerDetail.getCoOwnerIDNumber().equals(aCoOwnerDetail.getCoOwnerIDNumber())) {
						duplicateRecord = true;
					}
				} else if (aCoOwnerDetail.isBankCustomer() && oldCoOwnerDetail.isBankCustomer()) {
					if (oldCoOwnerDetail.getCustomerId() ==  aCoOwnerDetail.getCustomerId()) {
						duplicateRecord = true;
					}
				} else if (StringUtils.equals(oldCoOwnerDetail.getCoOwnerIDTypeName(),aCoOwnerDetail.getCoOwnerIDTypeName())
						&& StringUtils.equals(oldCoOwnerDetail.getCoOwnerIDNumber(),aCoOwnerDetail.getCoOwnerIDNumber())) {
					duplicateRecord = true;
				}
				
				if (duplicateRecord) {
					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm, valueParm), getUserWorkspace()
								.getUserLanguage()));
						return auditHeader;
					}
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aCoOwnerDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aCoOwnerDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							this.coOwnerDetailList.add(aCoOwnerDetail);
						} else if (aCoOwnerDetail.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aCoOwnerDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aCoOwnerDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							this.coOwnerDetailList.add(aCoOwnerDetail);
						} else if (aCoOwnerDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
						}
					} else {
						this.coOwnerDetailList.add(oldCoOwnerDetail);
					}
				} else {
					this.coOwnerDetailList.add(oldCoOwnerDetail);
				}
				duplicateRecord = false;
			}
		}
		if (PennantConstants.TRAN_UPD.equals(tranType)) {
			this.coOwnerDetailList.remove(index);
			this.coOwnerDetailList.add(coOwnerDetail);
			recordAdded = true;
		}
		if (!recordAdded) {
			this.coOwnerDetailList.add(aCoOwnerDetail);
		}
		return auditHeader;
	}

	// WorkFlow Components

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(CoOwnerDetail aCoOwnerDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCoOwnerDetail.getBefImage(), aCoOwnerDetail);
		return new AuditHeader(String.valueOf(aCoOwnerDetail.getCoOwnerId()), null, null, null, auditDetail,
				aCoOwnerDetail.getUserDetails(), getOverideMap());
	}

	/**
	 * OnChange IdNumber Calling the Method To set EIDNumber Format
	 */
	public void onChange$coOwnerIDNumber(Event event) {
		logger.debug("Entering" + event.toString());
		getcoOwnerIdNumber();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method to Set The Format For EIDNumber
	 */
	public void getcoOwnerIdNumber() {
		logger.debug("Entering");
		if (this.coOwnerIDType.getSelectedIndex() != 0) {
			if (this.coOwnerIDType.getSelectedItem().getValue().toString().equals(PennantConstants.CPRCODE)) {
				this.coOwnerIDNumber.setValue(PennantApplicationUtil.formatEIDNumber(this.coOwnerIDNumber.getValue()));
			}
		}
		logger.debug("Leaving");
	}

	public void onChange$coOwnerIDType(Event event) {
		logger.debug("Entering" + event.toString());
		this.coOwnerIDNumber.setErrorMessage("");
		if (this.coOwnerIDNumber.getValue().trim().length() > 9) {
			this.coOwnerIDNumber.setValue("");
		}
		logger.debug("Leaving" + event.toString());
	}

	private String getLovDescription(String value) {
		value = StringUtils.trimToEmpty(value);
		try {
			value = StringUtils.split(value, "-", 2)[1];
		} catch (Exception e) {
			logger.error("Exception :", e);
		}
		return value;
	}

	public void onFulfill$addrCountry(Event event) {
		logger.debug("Entering" + event.toString());
		if (!StringUtils.trimToEmpty(addrCountryTemp).equals(this.addrCountry.getValue())) {
			this.addrProvince.setObject("");
			this.addrProvince.setValue("");
			this.addrProvince.setDescription("");
			this.addrCity.setObject("");
			this.addrCity.setValue("");
			this.addrCity.setDescription("");
		}
		addrCountryTemp = this.addrCountry.getValue();
		Filter[] provinceFilters = new Filter[1];
		provinceFilters[0] = new Filter("CPCountry", this.addrCountry.getValue(), Filter.OP_EQUAL);
		this.addrProvince.setFilters(provinceFilters);
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$addrProvince(Event event) {
		logger.debug("Entering" + event.toString());
		if (!StringUtils.trimToEmpty(addrProvinceTemp).equals(this.addrProvince.getValue())) {
			this.addrCity.setObject("");
			this.addrCity.setValue("");
			this.addrCity.setDescription("");
		}
		addrProvinceTemp = this.addrProvince.getValue();
		Filter[] filters = new Filter[2];
		filters[0] = new Filter("PCCountry", this.addrCountry.getValue(), Filter.OP_EQUAL);
		filters[1] = new Filter("PCProvince", this.addrProvince.getValue(), Filter.OP_EQUAL);
		this.addrCity.setFilters(filters);
		logger.debug("Leaving" + event.toString());
	}

	/*
	 * Method to get the CustID From Customers
	 */
	public String getCustData(long custID) {
		logger.debug("Entering");
		JdbcSearchObject<Customer> searchObject = new JdbcSearchObject<Customer>(Customer.class);
		searchObject.addTabelName("Customers");
		searchObject.addField("CustCIF");
		searchObject.addFilterEqual("CustID", custID);
		List<Customer> custData = pagedListService.getBySearchObject(searchObject);
		if (custData != null && !custData.isEmpty()) {
			return custData.get(0).getCustCIF();
		}
		logger.debug("Leaving");
		return null;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public CollateralSetupDialogCtrl getCollateralSetupDialogCtrl() {
		return collateralSetupDialogCtrl;
	}

	public void setCollateralSetupDialogCtrl(CollateralSetupDialogCtrl collateralSetupDialogCtrl) {
		this.collateralSetupDialogCtrl = collateralSetupDialogCtrl;
	}

	public CoOwnerDetail getCoOwnerDetail() {
		return coOwnerDetail;
	}

	public void setCoOwnerDetail(CoOwnerDetail coOwnerDetail) {
		this.coOwnerDetail = coOwnerDetail;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public boolean isNewCoOwnerDetails() {
		return newCoOwnerDetails;
	}

	public void setNewCoOwnerDetails(boolean newCoOwnerDetails) {
		this.newCoOwnerDetails = newCoOwnerDetails;
	}

}

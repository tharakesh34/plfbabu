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
 * FileName    		:  TaxDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-06-2017    														*
 *                                                                  						*
 * Modified Date    :  14-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-06-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.taxdetail;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.applicationmaster.TaxDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.applicationmaster.TaxDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.systemmasters.province.ProvinceDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * This is the controller class for the /WEB-INF/pages/applicationmaster/TaxDetail/taxDetailDialog.zul file. <br>
 */
public class TaxDetailDialogCtrl extends GFCBaseCtrl<TaxDetail> {

	private static final long				serialVersionUID	= 1L;
	private static final Logger				logger				= Logger.getLogger(TaxDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window						window_TaxDetailDialog;
	protected ExtendedCombobox				country;
	protected ExtendedCombobox				stateCode;
	protected ExtendedCombobox				entityCode;
	protected Textbox						taxCode;
	protected Textbox						addressLine1;
	protected Textbox						addressLine2;
	protected Textbox						addressLine3;
	protected Textbox						addressLine4;
	protected ExtendedCombobox				pinCode;
	protected ExtendedCombobox				cityCode;
	private TaxDetail						taxDetail;															

	private transient TaxDetailListCtrl		taxDetailListCtrl;													
	private transient TaxDetailService		taxDetailService;
	private transient ProvinceDialogCtrl	provinceDialogCtrl;
	private List<TaxDetail>					listTaxDetails;
	private boolean							fromProvince		= false;

	/**
	 * default constructor.<br>
	 */
	public TaxDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "TaxDetailDialog";
	}

	@Override
	protected String getReference() {
		StringBuffer referenceBuffer = new StringBuffer(String.valueOf(this.taxDetail.getId()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_TaxDetailDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_TaxDetailDialog);

		try {
			// Get the required arguments.
			this.taxDetail = (TaxDetail) arguments.get("taxdetail");

			if (this.taxDetail == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}
			
			if (arguments.containsKey("taxdetailListCtrl")) {
				this.taxDetailListCtrl = (TaxDetailListCtrl) arguments.get("taxdetailListCtrl");
			}

			// Store the before image.
			TaxDetail taxDetail = new TaxDetail();
			BeanUtils.copyProperties(this.taxDetail, taxDetail);
			this.taxDetail.setBefImage(taxDetail);
			
			if (arguments.containsKey("provinceDialogCtrl")) {
				this.provinceDialogCtrl = (ProvinceDialogCtrl) arguments.get("provinceDialogCtrl");
				fromProvince = true;
				this.taxDetail.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					setRole(arguments.get("roleCode").toString());
					getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
				}
			}

			// Render the page and display the data.
			doLoadWorkFlow(this.taxDetail.isWorkflow(), this.taxDetail.getWorkflowId(), this.taxDetail.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.taxDetail);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.country.setMandatoryStyle(true);
		this.country.setModuleName("Country");
		this.country.setValueColumn("CountryCode");
		this.country.setDescColumn("CountryDesc");
		this.country.setValidateColumns(new String[] { "CountryCode" });

		this.stateCode.setMandatoryStyle(true);
		this.stateCode.setModuleName("Province");
		this.stateCode.setValueColumn("CPProvince");
		this.stateCode.setDescColumn("CPProvinceName");
		this.stateCode.setValidateColumns(new String[] { "CPProvince" });

		this.entityCode.setMandatoryStyle(true);
		this.entityCode.setModuleName("Entity");
		this.entityCode.setValueColumn("EntityCode");
		this.entityCode.setDescColumn("EntityDesc");
		this.entityCode.setValidateColumns(new String[] { "EntityCode" });

		this.pinCode.setMandatoryStyle(true);
		this.pinCode.setModuleName("PinCode");
		this.pinCode.setValueColumn("PinCode");
		this.pinCode.setDescColumn("AreaName");
		this.pinCode.setValidateColumns(new String[] { "PinCode" });

		this.cityCode.setMandatoryStyle(true);
		this.cityCode.setModuleName("City");
		this.cityCode.setValueColumn("PCCity");
		this.cityCode.setDescColumn("PCCityName");
		this.cityCode.setValidateColumns(new String[] { "PCCity" });

		this.entityCode.setMaxlength(8);
		this.pinCode.setMaxlength(10);
		this.taxCode.setMaxlength(15);
		this.cityCode.setMaxlength(8);
		this.country.setMaxlength(8);
		this.stateCode.setMaxlength(8);
		this.addressLine1.setMaxlength(100);
		this.addressLine2.setMaxlength(100);
		this.addressLine3.setMaxlength(100);
		this.addressLine4.setMaxlength(100);

		if (fromProvince) {
			Filter[] filters1 = new Filter[1];
			filters1[0] = new Filter("PCProvince", this.taxDetail.getStateCode(), Filter.OP_EQUAL);
			this.cityCode.setFilters(filters1);
		}

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_TaxDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_TaxDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_TaxDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_TaxDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws InterruptedException 
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	public void onFulfill$stateCode(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = stateCode.getObject();
		String pcProvince = null;
		String taxCodeValue = this.taxCode.getValue();
		if (dataObject instanceof String) {
			if (taxCodeValue.length() > 10) {
				String suffix = taxCodeValue.substring(2);
				this.taxCode.setValue(suffix);
			} else if(taxCodeValue.length() == 2) {
				this.taxCode.setValue("");
			} else if (taxCodeValue.length() == 10) {
				//do nothing
			} else {
				this.taxCode.setValue("");
			}
			fillPindetails(null, null);
		} else {
			Province province = (Province) dataObject;
			if (province == null) {
				fillPindetails(null, null);
			}
			if (province != null) {
				this.stateCode.setErrorMessage("");
				this.taxCode.setErrorMessage("");
				
				pcProvince = this.stateCode.getValue();
				fillPindetails(null, pcProvince);
				String taxStateCode = province.getTaxStateCode() == null ? "" : province.getTaxStateCode();
				if (taxCodeValue.length() == 10) {
					this.taxCode.setValue(taxStateCode + taxCodeValue);
				} else if (taxCodeValue.length() > 10) {
					String suffix = taxCodeValue.substring(2);
					this.taxCode.setValue(taxStateCode + suffix);
				} else {
					this.taxCode.setValue(taxStateCode);
				}
				this.country.setValue(province.getCPCountry(),province.getLovDescCPCountryName());
			}else{
				if (taxCodeValue.length() > 10) {
					String suffix = taxCodeValue.substring(2);
					this.taxCode.setValue(suffix);
				} else if(taxCodeValue.length() == 2) {
					this.taxCode.setValue("");
				} else if (taxCodeValue.length() == 10) {
					//do nothing
				} else {
					this.taxCode.setValue("");
				}
			}
		}
		
		this.cityCode.setObject("");
		this.pinCode.setObject("");
		this.cityCode.setValue("");
		this.cityCode.setDescription("");
		this.pinCode.setValue("");
		this.pinCode.setDescription("");
		fillCitydetails(pcProvince);
		
		logger.debug("Leaving" + event.toString());
	}
	
	public void onFulfill$country(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = country.getObject();
		String pcProvince = null;
		if (dataObject instanceof String) {
			this.stateCode.setValue("");
			this.stateCode.setDescription("");
			this.cityCode.setValue("");
			this.cityCode.setDescription("");
			this.pinCode.setValue("");
			this.pinCode.setDescription("");
			fillPindetails(null, null);
		} else if (!(dataObject instanceof String)) {
			Country country = (Country) dataObject; 
			if (country == null) {
				fillProvinceDetails(null);
			}
			if (country != null) {
				this.stateCode.setErrorMessage("");
				pcProvince =country.getCountryCode();
				fillProvinceDetails(pcProvince);
			} else {
				this.stateCode.setObject("");
				this.cityCode.setObject("");
				this.pinCode.setObject("");
				this.stateCode.setValue("");
				this.stateCode.setDescription("");
				this.cityCode.setValue("");
				this.cityCode.setDescription("");
				this.pinCode.setValue("");
				this.pinCode.setDescription("");
				this.taxCode.setValue("");
			}
			fillPindetails(null, null);
		}
		logger.debug("Leaving" + event.toString());
	}
	
	private void fillProvinceDetails(String country){
		this.stateCode.setMandatoryStyle(true);
		this.stateCode.setModuleName("Province");
		this.stateCode.setValueColumn("CPProvince");
		this.stateCode.setDescColumn("CPProvinceName");
		this.stateCode.setValidateColumns(new String[] { "CPProvince" });
		
		Filter[] filters1 = new Filter[1];

		if (country == null || country.equals("")) {
			filters1[0] = new Filter("CPCountry", null, Filter.OP_NOT_EQUAL);
		} else {
			filters1[0] = new Filter("CPCountry", country, Filter.OP_EQUAL);
		}

		this.stateCode.setFilters(filters1);
	}

	private void fillCitydetails(String state) {
		logger.debug("Entering");

		this.cityCode.setModuleName("City");
		this.cityCode.setValueColumn("PCCity");
		this.cityCode.setDescColumn("PCCityName");
		this.cityCode.setValidateColumns(new String[] { "PCCity" });
		Filter[] filters1 = new Filter[2];
		
		if (state == null) {
			filters1[0] = new Filter("PCProvince", null, Filter.OP_NOT_EQUAL);
		} else {
			filters1[0] = new Filter("PCProvince", state, Filter.OP_EQUAL);
		}
		
		filters1[1] = new Filter("CITYISACTIVE", 1, Filter.OP_EQUAL);
		
		this.cityCode.setFilters(filters1);
	}

	/**
	 * onChanging Branch
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$cityCode(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = cityCode.getObject();

		String cityValue = null;
		if (dataObject instanceof String) {
			this.cityCode.setValue("");
			this.cityCode.setDescription("");
		} else {
			City city = (City) dataObject;
			if (city != null) {
				
				this.cityCode.setErrorMessage("");
				this.stateCode.setErrorMessage("");
				this.taxCode.setErrorMessage("");
				
				this.stateCode.setValue(city.getPCProvince());
				this.stateCode.setDescription(city.getLovDescPCProvinceName());
				cityValue = this.cityCode.getValue();
				String taxCodeValue = this.taxCode.getValue();
				String citytaxStateCode = city.getTaxStateCode() == null ? "" : city.getTaxStateCode();
				this.country.setValue(city.getPCCountry(),city.getLovDescPCCountryName());
				if (taxCodeValue.length() == 10) {
					this.taxCode.setValue(citytaxStateCode + taxCodeValue);
				}else if (taxCodeValue.length() > 10) {
					String suffix = taxCodeValue.substring(2);
					this.taxCode.setValue(citytaxStateCode + suffix);
				} else {
					this.taxCode.setValue(citytaxStateCode);
				}
			}
		}
		fillPindetails(cityValue, this.stateCode.getValue());
		
		this.pinCode.setObject("");
		this.pinCode.setValue("");
		this.pinCode.setDescription("");
		
		logger.debug("Leaving");
	}

	private void fillPindetails(String cityValue, String provice) {
		this.pinCode.setModuleName("PinCode");
		this.pinCode.setValueColumn("PinCode");
		this.pinCode.setDescColumn("AreaName");
		this.pinCode.setValidateColumns(new String[] { "PinCode" });
		Filter[] filters1 = new Filter[2];
		
		if (cityValue != null) {
			filters1[0] = new Filter("City", cityValue, Filter.OP_EQUAL);
		} else if(provice != null && !provice.isEmpty()) {
			filters1[0] = new Filter("PCProvince", provice, Filter.OP_EQUAL);
		} else {
			filters1[0] = new Filter("City", null, Filter.OP_NOT_EQUAL);
		}
		
		filters1[1] = new Filter("Active", 1, Filter.OP_EQUAL);
		
		this.pinCode.setFilters(filters1);
	}

	/**
	 * onChanging Branch
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$pinCode(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = pinCode.getObject();
		if (dataObject instanceof String) {
			this.pinCode.setValue("");
			this.pinCode.setDescription("");
		} else {
			PinCode pinCode = (PinCode) dataObject;
			if (pinCode != null) {
				this.cityCode.setValue(pinCode.getCity());
				this.cityCode.setDescription(pinCode.getPCCityName());
				this.stateCode.setValue(pinCode.getPCProvince());
				this.stateCode.setDescription(pinCode.getLovDescPCProvinceName());
				this.country.setValue(pinCode.getpCCountry(),pinCode.getLovDescPCCountryName());
				
				this.cityCode.setErrorMessage("");
				this.stateCode.setErrorMessage("");
				this.pinCode.setErrorMessage("");
				this.taxCode.setErrorMessage("");
				
				String taxCodeValue = this.taxCode.getValue();
				String gstInValue = pinCode.getGstin() == null ? "" : pinCode.getGstin();
				if (taxCodeValue.length() == 10) {
					this.taxCode.setValue(gstInValue + taxCodeValue);
				} else if (taxCodeValue.length() > 10) {
					String suffix = taxCodeValue.substring(2);
					this.taxCode.setValue(gstInValue + suffix);
				} else {
					this.taxCode.setValue(gstInValue);
				}
			} 
		}
		
		logger.debug("Leaving");
	}

	/**
	 * onChanging Branch
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$entityCode(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		Object dataObject = entityCode.getObject();
		this.taxCode.setErrorMessage("");
		String taxCodeValue = this.taxCode.getValue();
		if (dataObject instanceof String) {
			if (taxCodeValue.length() > 10) {
				String prefix = taxCodeValue.substring(0, 2);
				this.taxCode.setValue(prefix);
			} else {
				this.taxCode.setValue("");
			}
		} else {
			Entity entity = (Entity) dataObject;
			if(entity != null) {
				if (taxCodeValue.length() == 2) {
					this.taxCode.setValue(taxCodeValue + entity.getPANNumber());
				} else if (taxCodeValue.length() > 10) {
					String prefix = taxCodeValue.substring(0, 2);
					this.taxCode.setValue(prefix + entity.getPANNumber());
				} else {
					this.taxCode.setValue(entity.getPANNumber());
				}
			}else{
				if (taxCodeValue.length() > 10) {
					String prefix = taxCodeValue.substring(0, 2);
					this.taxCode.setValue(prefix);
				} else {
					this.taxCode.setValue("");
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.taxDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		taxDetailListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.taxDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param taxDetail
	 * 
	 */
	public void doWriteBeanToComponents(TaxDetail aTaxDetail) {
		logger.debug(Literal.ENTERING);

		this.addressLine1.setValue(aTaxDetail.getAddressLine1());
		this.addressLine2.setValue(aTaxDetail.getAddressLine2());
		this.addressLine3.setValue(aTaxDetail.getAddressLine3());
		this.addressLine4.setValue(aTaxDetail.getAddressLine4());
		
		if (getProvinceDialogCtrl() != null || !aTaxDetail.isNewRecord()) {
			fillCitydetails(aTaxDetail.getStateCode());
			fillPindetails(aTaxDetail.getCityCode(), aTaxDetail.getStateCode());
		}
		
		this.country.setValue(aTaxDetail.getCountry());
		this.country.setDescription(aTaxDetail.getCountryName());
		
		this.stateCode.setValue(aTaxDetail.getStateCode());
		this.stateCode.setDescription(aTaxDetail.getProvinceName());
		
		this.cityCode.setValue(aTaxDetail.getCityCode());
		this.cityCode.setDescription(aTaxDetail.getCityName());
		
		this.entityCode.setValue(aTaxDetail.getEntityCode());
		this.entityCode.setDescription(aTaxDetail.getEntityDesc());
		
		this.pinCode.setValue(aTaxDetail.getPinCode());
		this.pinCode.setDescription(aTaxDetail.getCityName());
		
		this.taxCode.setValue(aTaxDetail.getTaxCode());
		this.recordStatus.setValue(aTaxDetail.getRecordStatus());
		
		if (fromProvince) {
			this.country.setReadonly(true);
			this.stateCode.setReadonly(true);
			
			if ((StringUtils.isNotBlank(this.taxDetail.getRecordStatus())
					|| StringUtils.isNotBlank(this.taxDetail.getRecordType())) && !enqiryModule) {
				this.btnDelete.setVisible(true);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aTaxDetail
	 */
	public void doWriteComponentsToBean(TaxDetail aTaxDetail) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Country
		try {
			aTaxDetail.setCountry(this.country.getValidatedValue());
			aTaxDetail.setCountryName(this.country.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//State Code
		try {
			aTaxDetail.setStateCode(this.stateCode.getValidatedValue());
			aTaxDetail.setProvinceName(this.stateCode.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Entity Code
		try {
			aTaxDetail.setEntityCode(this.entityCode.getValidatedValue());
			aTaxDetail.setEntityDesc(this.entityCode.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Tax Code
		try {
			aTaxDetail.setTaxCode(this.taxCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Address Line 1
		try {
			aTaxDetail.setAddressLine1(this.addressLine1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Address Line 2
		try {
			aTaxDetail.setAddressLine2(this.addressLine2.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Address Line 3
		try {
			aTaxDetail.setAddressLine3(this.addressLine3.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Address Line 4
		try {
			aTaxDetail.setAddressLine4(this.addressLine4.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Pin Code
		try {
			aTaxDetail.setPinCode(this.pinCode.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//City Code
		try {
			aTaxDetail.setCityCode(this.cityCode.getValidatedValue());
			aTaxDetail.setCityName(this.cityCode.getDescription());
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

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param taxDetail
	 *            The entity that need to be render.
	 */
	public void doShowDialog(TaxDetail taxDetail) {
		logger.debug(Literal.LEAVING);

		if (taxDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.country.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(taxDetail.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.country.focus();
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				if (fromProvince && !enqiryModule) {
					doEdit();
				}
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(taxDetail);
		
		if (fromProvince) {
			this.window_TaxDetailDialog.setHeight("80%");
			this.window_TaxDetailDialog.setWidth("80%");
			this.groupboxWf.setVisible(false);
			this.window_TaxDetailDialog.doModal();
		} else {
			this.window_TaxDetailDialog.setWidth("100%");
			this.window_TaxDetailDialog.setHeight("100%");
			setDialog(DialogType.EMBEDDED);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.country.isReadonly()) {
			this.country.setConstraint(
					new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_Country.value"), null, true, true));
		}
		if (!this.stateCode.isReadonly()) {
			this.stateCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_StateCode.value"), null, true, true));
		}
		if (!this.entityCode.isReadonly()) {
			this.entityCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_EntityCode.value"), null, true, true));
		}
		if (!this.taxCode.isReadonly()){
			this.taxCode.setConstraint(new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_TaxCode.value"),PennantRegularExpressions.REGEX_GSTIN,true,15));
		}
		if (!this.addressLine1.isReadonly()) {
			this.addressLine1
					.setConstraint(new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_AddressLine1.value"),
							PennantRegularExpressions.REGEX_ADDRESS, true));
		}
		if (!this.addressLine2.isReadonly()) {
			this.addressLine2
					.setConstraint(new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_AddressLine2.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.addressLine3.isReadonly()) {
			this.addressLine3
					.setConstraint(new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_AddressLine3.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.addressLine4.isReadonly()) {
			this.addressLine4
					.setConstraint(new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_AddressLine4.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.pinCode.isReadonly()) {
			this.pinCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_PinCode.value"), null, true, true));
		}
		if (!this.cityCode.isReadonly()) {
			this.cityCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_CityCode.value"), null, true, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.country.setConstraint("");
		this.stateCode.setConstraint("");
		this.entityCode.setConstraint("");
		this.taxCode.setConstraint("");
		this.addressLine1.setConstraint("");
		this.addressLine2.setConstraint("");
		this.addressLine3.setConstraint("");
		this.addressLine4.setConstraint("");
		this.pinCode.setConstraint("");
		this.cityCode.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		//Id
		//Country
		//State Code
		//Entity Code
		//Tax Code
		//Address Line 1
		//Address Line 2
		//Address Line 3
		//Address Line 4
		//Pin Code
		//City Code

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Deletes a TaxDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final TaxDetail aTaxDetail = new TaxDetail();
		BeanUtils.copyProperties(this.taxDetail, aTaxDetail);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		String msg = "";
		if (fromProvince) {
			msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record");
		} else {
			msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aTaxDetail.getId();
		}
		
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (fromProvince) {
				aTaxDetail.setVersion(aTaxDetail.getVersion() + 1);
				
				if (StringUtils.isBlank(aTaxDetail.getRecordType())) {
					aTaxDetail.setNewRecord(true);
				}  
				
				tranType = PennantConstants.TRAN_DEL;
				AuditHeader auditHeader = newTaxDetailProcess(aTaxDetail, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_TaxDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getProvinceDialogCtrl().doFillGSTINMappingDetails(this.listTaxDetails);
					// send the data back to customer
					closeDialog();
				}
			} else {
				try {
					if (StringUtils.isBlank(aTaxDetail.getRecordType())) {
						
						aTaxDetail.setVersion(aTaxDetail.getVersion() + 1);
						aTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

						if (isWorkFlowEnabled()) {
							aTaxDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
							aTaxDetail.setNewRecord(true);
							tranType = PennantConstants.TRAN_WF;
							getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aTaxDetail.getNextTaskId(), aTaxDetail);
						} else {
							tranType = PennantConstants.TRAN_DEL;
						}
					}  else if (PennantConstants.RCD_UPD.equals(aTaxDetail.getRecordType())) {
						aTaxDetail.setVersion(aTaxDetail.getVersion() + 1);
						aTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					}
					if (doProcess(aTaxDetail, tranType)) {
						refreshList();
						closeDialog();
					}
				} catch (DataAccessException e) {
					MessageUtil.showError(e);
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.taxDetail.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.taxCode);
		} else {
			this.btnCancel.setVisible(true);
		}
		readOnlyComponent(isReadOnly("TaxDetailDialog_TaxCode"), this.taxCode);
		readOnlyComponent(isReadOnly("TaxDetailDialog_Country"), this.country);
		readOnlyComponent(isReadOnly("TaxDetailDialog_StateCode"), this.stateCode);
		readOnlyComponent(isReadOnly("TaxDetailDialog_EntityCode"), this.entityCode);
		readOnlyComponent(isReadOnly("TaxDetailDialog_AddressLine1"), this.addressLine1);
		readOnlyComponent(isReadOnly("TaxDetailDialog_AddressLine2"), this.addressLine2);
		readOnlyComponent(isReadOnly("TaxDetailDialog_AddressLine3"), this.addressLine3);
		readOnlyComponent(isReadOnly("TaxDetailDialog_AddressLine4"), this.addressLine4);
		readOnlyComponent(isReadOnly("TaxDetailDialog_PinCode"), this.pinCode);
		readOnlyComponent(isReadOnly("TaxDetailDialog_CityCode"), this.cityCode);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.taxDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);

		readOnlyComponent(true, this.country);
		readOnlyComponent(true, this.stateCode);
		readOnlyComponent(true, this.entityCode);
		readOnlyComponent(true, this.taxCode);
		readOnlyComponent(true, this.addressLine1);
		readOnlyComponent(true, this.addressLine2);
		readOnlyComponent(true, this.addressLine3);
		readOnlyComponent(true, this.addressLine4);
		readOnlyComponent(true, this.pinCode);
		readOnlyComponent(true, this.cityCode);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		this.country.setValue("");
		this.country.setDescription("");
		this.stateCode.setValue("");
		this.stateCode.setDescription("");
		this.entityCode.setValue("");
		this.taxCode.setValue("");
		this.addressLine1.setValue("");
		this.addressLine2.setValue("");
		this.addressLine3.setValue("");
		this.addressLine4.setValue("");
		this.pinCode.setValue("");
		this.cityCode.setValue("");
		this.cityCode.setDescription("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * @throws InterruptedException 
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final TaxDetail aTaxDetail = new TaxDetail();
		BeanUtils.copyProperties(this.taxDetail, aTaxDetail);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aTaxDetail);

		isNew = aTaxDetail.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aTaxDetail.getRecordType())) {
				aTaxDetail.setVersion(aTaxDetail.getVersion() + 1);
				if (isNew) {
					aTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aTaxDetail.setNewRecord(true);
				}
			}
		} else {
			if (fromProvince) {
				if (isNew) {
					aTaxDetail.setVersion(1);
					aTaxDetail.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(aTaxDetail.getRecordType())) {
					aTaxDetail.setVersion(aTaxDetail.getVersion() + 1);
					aTaxDetail.setRecordType(PennantConstants.RCD_UPD);
				}
			} else {
				aTaxDetail.setVersion(aTaxDetail.getVersion() + 1);
			}
			
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (fromProvince) {
				AuditHeader auditHeader = newTaxDetailProcess(aTaxDetail, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_TaxDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getProvinceDialogCtrl().doFillGSTINMappingDetails(this.listTaxDetails);
					// send the data back to customer
					closeDialog();
				}
			} else if (doProcess(aTaxDetail, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		
		logger.debug("Leaving");
	}

	

	private AuditHeader newTaxDetailProcess(TaxDetail aTaxDetail, String tranType) {
		logger.debug("Entering");
		
		boolean recordAdded = false;
		AuditHeader auditHeader = getAuditHeader(aTaxDetail, tranType);
		this.listTaxDetails = new ArrayList<TaxDetail>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = aTaxDetail.getProvinceName();
		valueParm[1] = aTaxDetail.getEntityDesc();

		errParm[0] = PennantJavaUtil.getLabel("label_TaxDetailDialog_StateCode.value") + ":" + valueParm[0] + " and ";
		errParm[1] = PennantJavaUtil.getLabel("label_TaxDetailDialog_EntityCode.value") + ":" + valueParm[1];

		if (getProvinceDialogCtrl().getTaxDetailList() != null
				&& (getProvinceDialogCtrl().getTaxDetailList().size() > 0)) {
			
			for (int i = 0; i < getProvinceDialogCtrl().getTaxDetailList().size(); i++) {
				
				TaxDetail taxDetail = getProvinceDialogCtrl().getTaxDetailList().get(i);

				if (aTaxDetail.getStateCode().equals(taxDetail.getStateCode())
						&& aTaxDetail.getEntityCode().equals(taxDetail.getEntityCode())) { // Both Current and Existing list addresses same

					if (aTaxDetail.isNewRecord()  && !PennantConstants.TRAN_DEL.equals(tranType)) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (PennantConstants.RECORD_TYPE_UPD.equals(aTaxDetail.getRecordType())) {
							aTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							this.listTaxDetails.add(aTaxDetail);
						} else if (PennantConstants.RCD_ADD.equals(aTaxDetail.getRecordType())) {
							recordAdded = true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(aTaxDetail.getRecordType())) {
							aTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							this.listTaxDetails.add(aTaxDetail);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(aTaxDetail.getRecordType())) {
							recordAdded = true;
							
							for (int j = 0; j < getProvinceDialogCtrl().getTaxDetailList().size(); j++) {
								TaxDetail detail = getProvinceDialogCtrl().getTaxDetailList().get(j);
								if (aTaxDetail.getStateCode().equals(taxDetail.getStateCode())
										&& aTaxDetail.getEntityCode().equals(taxDetail.getEntityCode())) {
									this.listTaxDetails.add(detail);
								}
							}
						} else if(StringUtils.isBlank(aTaxDetail.getRecordType())) {
							aTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							this.listTaxDetails.add(aTaxDetail);
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							this.listTaxDetails.add(taxDetail);
						}
					}
				} else if(aTaxDetail.getTaxCode().equals(taxDetail.getTaxCode())  && !PennantConstants.TRAN_DEL.equals(tranType)) {
					
					valueParm = new String[1];
					errParm = new String[1];
					
					valueParm[0] = aTaxDetail.getTaxCode();
					errParm[0] = PennantJavaUtil.getLabel("label_TaxDetailDialog_TaxCode.value") + ":" + valueParm[0];
					
					auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
							new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
							getUserWorkspace().getUserLanguage()));
					return auditHeader;
				} else {
					this.listTaxDetails.add(taxDetail);
				}
			}
		}

		if (!recordAdded) {
			this.listTaxDetails.add(aTaxDetail);
		}
		
		logger.debug("Leaving");
		
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
	private boolean doProcess(TaxDetail aTaxDetail, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aTaxDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aTaxDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aTaxDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aTaxDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aTaxDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aTaxDetail);
				}

				if (isNotesMandatory(taskId, aTaxDetail)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}

				}
			}
			if (!StringUtils.isBlank(nextTaskId)) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aTaxDetail.setTaskId(taskId);
			aTaxDetail.setNextTaskId(nextTaskId);
			aTaxDetail.setRoleCode(getRole());
			aTaxDetail.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aTaxDetail, tranType);
			String operationRefs = getServiceOperations(taskId, aTaxDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aTaxDetail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aTaxDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader
	 *            auditHeader
	 * @param method
	 *            (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		TaxDetail aTaxDetail = (TaxDetail) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = taxDetailService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = taxDetailService.saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = taxDetailService.doApprove(auditHeader);

						if (aTaxDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = taxDetailService.doReject(auditHeader);
						if (aTaxDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_TaxDetailDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_TaxDetailDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.taxDetail), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
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

		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(TaxDetail aTaxDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aTaxDetail.getBefImage(), aTaxDetail);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aTaxDetail.getUserDetails(),
				getOverideMap());
	}

	public void setTaxDetailService(TaxDetailService taxDetailService) {
		this.taxDetailService = taxDetailService;
	}

	public List<TaxDetail> getListTaxDetails() {
		return listTaxDetails;
	}

	public void setListTaxDetails(List<TaxDetail> listTaxDetails) {
		this.listTaxDetails = listTaxDetails;
	}

	public ProvinceDialogCtrl getProvinceDialogCtrl() {
		return provinceDialogCtrl;
	}

	public void setProvinceDialogCtrl(ProvinceDialogCtrl provinceDialogCtrl) {
		this.provinceDialogCtrl = provinceDialogCtrl;
	}
}

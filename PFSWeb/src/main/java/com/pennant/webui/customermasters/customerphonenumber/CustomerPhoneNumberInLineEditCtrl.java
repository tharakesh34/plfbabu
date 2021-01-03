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
 * FileName    		:  CustomerDialogCtrl.java                                              * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  27-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 * 09-05-2018		Vinay					 0.2      Extended Details tab changes for 		*
 * 													  Customer Enquiry menu based on rights	* 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.customermasters.customerphonenumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.systemmasters.PhoneType;
import com.pennant.backend.service.systemmasters.PhoneTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/Customer/customerDialog.zul file.
 */
public class CustomerPhoneNumberInLineEditCtrl extends GFCBaseCtrl<CustomerDetails> {
	private static final Logger logger = Logger.getLogger(CustomerPhoneNumberInLineEditCtrl.class);
	private static final long serialVersionUID = -1289772081447044673L;
	private final List<ValueLabel> customerPriorityList = PennantStaticListUtil.getCustomerEmailPriority();
	private PhoneTypeService phoneTypeService;
	private boolean isFinanceProcess;

	public CustomerPhoneNumberInLineEditCtrl() {
		super();
	}

	public void doRenderPhoneNumberList(List<CustomerPhoneNumber> customerPhoneNumbers, Listbox listbox, String custcif,
			boolean isFinance) {
		//render start
		listbox.getItems().clear();
		if (CollectionUtils.isNotEmpty(customerPhoneNumbers)) {
			for (CustomerPhoneNumber customerPhoneNumber : customerPhoneNumbers) {
				customerPhoneNumber.setLovDescCustCIF(custcif);
				doFillPhoneNumbers(customerPhoneNumber, listbox, isFinance);
			}
		}
	}

	/**
	 * This method will prepare the list components for PhoneNumber list render
	 * 
	 * @param customerPhoneNumber
	 * @param listbox
	 * @param isFinance
	 */
	public void doFillPhoneNumbers(CustomerPhoneNumber customerPhoneNumber, Listbox listbox, boolean isFinance) {
		logger.debug(Literal.ENTERING);
		isFinanceProcess = isFinance;
		String regex = "";
		Space space = null;
		Hbox hbox = null;
		Listitem item = new Listitem();
		Listcell cellCustomerID = new Listcell();
		Listcell cellPhoneTypeCode = new Listcell();
		Listcell cellPhoneNumber = new Listcell();
		Listcell cellPriority = new Listcell();
		Listcell cellRecordType = new Listcell();
		Listcell cellDelete = new Listcell();
		// **************** CustomerID
		Textbox customerId = new Textbox();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		space.setSclass(PennantConstants.mandateSclass);
		customerId.setMaxlength(10);
		customerId.setReadonly(true);
		customerId.setWidth("100px");
		customerId.setValue(customerPhoneNumber.getLovDescCustCIF().trim());
		hbox.appendChild(space);
		hbox.appendChild(customerId);
		cellCustomerID.appendChild(hbox);

		// **************** PhoneNumber Type
		ExtendedCombobox custPhoneType = getPhoneNumberType(customerPhoneNumber);
		Object[] phoneNumberData = new Object[1];
		regex = (String) custPhoneType.getAttribute("regex");
		phoneNumberData[0] = cellPhoneNumber;
		custPhoneType.addForward("onFulfill", self, "onFulfillCustPhoneType", phoneNumberData);
		custPhoneType.setReadonly(getUserWorkspace().isReadOnly("CustomerDialog_custPhoneType"));
		custPhoneType.setTextBoxWidth(150);
		if (!customerPhoneNumber.isNewRecord()) {
			custPhoneType.setReadonly(true);
		}
		cellPhoneTypeCode.appendChild(custPhoneType);
		// ********************* phoneNumber
		Textbox phoneNumber = new Textbox();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		space.setSclass(PennantConstants.mandateSclass);
		phoneNumber.setMaxlength(dosetFieldLength(regex));
		phoneNumber.setReadonly(getUserWorkspace().isReadOnly("CustomerDialog_custPhoneNumber"));
		phoneNumber.setValue(customerPhoneNumber.getPhoneNumber());
		hbox.appendChild(space);
		hbox.appendChild(phoneNumber);
		cellPhoneNumber.appendChild(hbox);

		// ********************************** Priority

		Combobox priority = new Combobox();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		space.setSclass(PennantConstants.mandateSclass);
		priority.setMaxlength(30);
		priority.setDisabled(getUserWorkspace().isReadOnly("CustomerDialog_custPhonePriority"));
		fillComboBox(priority, String.valueOf(customerPhoneNumber.getPhoneTypePriority()), customerPriorityList, "");
		hbox.appendChild(space);
		hbox.appendChild(priority);
		cellPriority.appendChild(hbox);

		// ******************
		cellRecordType.setLabel(PennantJavaUtil.getLabel(customerPhoneNumber.getRecordType()));

		// Delete action
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		Button button = new Button();
		button.setSclass("z-toolbarbutton");
		button.setLabel(Labels.getLabel("btnDelete.label"));
		button.setDisabled(getUserWorkspace().isReadOnly("CustomerDialog_custPhoneButtonDelete"));
		button.addForward("onClick", self, "onClickPhoneNumberButtonDelete", item);
		hbox.appendChild(space);
		hbox.appendChild(button);
		cellDelete.appendChild(hbox);
		// set parent
		cellCustomerID.setParent(item);
		cellPhoneTypeCode.setParent(item);
		cellPhoneNumber.setParent(item);
		cellPriority.setParent(item);
		cellRecordType.setParent(item);
		cellDelete.setParent(item);

		item.setAttribute("data", customerPhoneNumber);

		listbox.appendChild(item);

		if (PennantConstants.RECORD_TYPE_DEL.equals(customerPhoneNumber.getRecordType())) {
			doReadOnly(item);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method will perform the delete operation
	 * 
	 * @param listbox
	 * @param listitem
	 * @param isFinanceProcess
	 */
	public void doDelete(Listbox listbox, Listitem listitem, boolean isFinanceProcess) {

		if (listitem != null && listitem.getAttribute("data") != null) {
			CustomerPhoneNumber customerPhoneNumber = (CustomerPhoneNumber) listitem.getAttribute("data");
			Listcell cellRecordType = (Listcell) listitem.getChildren().get(4);

			String phoneType = customerPhoneNumber.getPhoneTypeCode();
			String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record");
			if (StringUtils.isNotEmpty(phoneType)) {
				msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
						+ Labels.getLabel("listheader_PhoneTypeCode.label") + " : " + phoneType;
			}
			// Show a confirm box
			if (MessageUtil.confirm(msg) == MessageUtil.YES) {
				doReadOnly(listitem);
				if (customerPhoneNumber.isNewRecord()) {
					listbox.removeChild(listitem);
					return;
				} else if (StringUtils.isBlank(customerPhoneNumber.getRecordType())) {
					customerPhoneNumber.setVersion(customerPhoneNumber.getVersion() + 1);
					customerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					if (!isFinanceProcess) {
						customerPhoneNumber.setNewRecord(true);
					}
				} else if (PennantConstants.RECORD_TYPE_UPD.equals(customerPhoneNumber.getRecordType())) {
					customerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RECORD_TYPE_NEW.equals(customerPhoneNumber.getRecordType())) {
					customerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				}

				cellRecordType.setLabel(PennantJavaUtil.getLabel(customerPhoneNumber.getRecordType()));
			}
		}

	}

	/**
	 * Setting the components to read only
	 * 
	 * @param listitem
	 */
	private void doReadOnly(Listitem listitem) {
		List<Component> listCells = listitem.getChildren();
		Button delete = (Button) listCells.get(5).getChildren().get(0).getLastChild();
		Textbox textbox = (Textbox) listCells.get(2).getChildren().get(0).getLastChild();
		Combobox combobox = (Combobox) listCells.get(3).getChildren().get(0).getLastChild();
		delete.setDisabled(true);
		textbox.setReadonly(true);
		combobox.setDisabled(true);
	}

	/**
	 * Creating extended combo box component
	 * 
	 * @param customerPhoneNumber
	 * @return
	 */
	private ExtendedCombobox getPhoneNumberType(CustomerPhoneNumber customerPhoneNumber) {
		ExtendedCombobox custPhoneType = new ExtendedCombobox();
		custPhoneType.setMaxlength(20);
		custPhoneType.getTextbox().setMaxlength(50);
		custPhoneType.setMandatoryStyle(true);
		custPhoneType.setTextBoxWidth(150);
		custPhoneType.setModuleName("PhoneType");
		custPhoneType.setValueColumn("PhoneTypeCode");
		custPhoneType.setDescColumn("PhoneTypeDesc");
		custPhoneType.setValidateColumns(new String[] { "PhoneTypeCode" });
		custPhoneType.setValue(customerPhoneNumber.getPhoneTypeCode(),
				customerPhoneNumber.getLovDescPhoneTypeCodeName());
		if (!customerPhoneNumber.isNewRecord()) {
			//get the regex using dao if old record
			PhoneType phoneType = phoneTypeService.getApprovedPhoneTypeById(customerPhoneNumber.getPhoneTypeCode());
			custPhoneType.setAttribute("regex", phoneType.getPhoneTypeRegex());
		} else if (customerPhoneNumber.isNewRecord()
				&& StringUtils.isNotEmpty(customerPhoneNumber.getPhoneTypeCode())) {
			PhoneType phoneType = phoneTypeService.getApprovedPhoneTypeById(customerPhoneNumber.getPhoneTypeCode());
			custPhoneType.setAttribute("regex", phoneType.getPhoneTypeRegex());
		}
		return custPhoneType;
	}

	/**
	 * This method will prepare the customer PhoneNumbers list for data saving
	 * 
	 * @param aCustomerDetails
	 * @param listBoxPhoneNumber
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, List> prepareCustomerPhoneNumberData(CustomerDetails aCustomerDetails,
			Listbox listBoxPhoneNumber) {
		logger.debug(Literal.ENTERING);
		List<Listitem> listItems = listBoxPhoneNumber.getItems();
		List<CustomerPhoneNumber> customerPhoneNumbers = new ArrayList<>();
		Customer customer = aCustomerDetails.getCustomer();
		ArrayList<WrongValueException> wve = new ArrayList<>();
		Map<String, List> phoneNumberData = new HashMap<>();

		if (CollectionUtils.isEmpty(listItems)) {
			return phoneNumberData;
		}

		for (Listitem listItem : listItems) {
			CustomerPhoneNumber aCustomerPhoneNumber = (CustomerPhoneNumber) listItem.getAttribute("data");

			if (aCustomerPhoneNumber == null) {
				continue;
			}

			String regEx = "";
			aCustomerPhoneNumber.setLovDescCustShrtName(customer.getCustShrtName());
			aCustomerPhoneNumber.setPhoneCustID(customer.getCustID());
			aCustomerPhoneNumber.setLovDescCustCIF(customer.getCustCIF());

			List<Component> listCells = listItem.getChildren();
			try {
				// Getting Phone Type
				ExtendedCombobox extPhoneType = (ExtendedCombobox) listCells.get(1).getChildren().get(0);
				regEx = (String) extPhoneType.getAttribute("regex");
				if (aCustomerPhoneNumber.isNewRecord() && StringUtils.isEmpty(extPhoneType.getValidatedValue())) {
					throw new WrongValueException(extPhoneType.getTextbox(), Labels.getLabel("FIELD_IS_MAND",
							new String[] { Labels.getLabel("listheader_PhoneTypeCode.label") }));
				}
				doSetPhoneTypeDetails(aCustomerPhoneNumber, extPhoneType);
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				// Getting Phone Number
				Textbox phoneNumber = (Textbox) listCells.get(2).getChildren().get(0).getLastChild();
				if (phoneNumber != null) {
					validatePhoneNumber(phoneNumber, regEx, aCustomerPhoneNumber.getPhoneTypeCode());
					aCustomerPhoneNumber.setPhoneNumber(phoneNumber.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				// Getting Priority
				Combobox priority = (Combobox) listCells.get(3).getChildren().get(0).getLastChild();
				if ("#".equals(getComboboxValue(priority))) {
					throw new WrongValueException(priority, Labels.getLabel("STATIC_INVALID", new String[] {
							Labels.getLabel("label_CustomerPhoneNumberDialog_CustPhonePriority.value") }));
				} else {
					aCustomerPhoneNumber.setPhoneTypePriority(Integer.parseInt(getComboboxValue(priority)));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			CustomerPhoneNumber oldData = isINexsistingList(aCustomerPhoneNumber,
					aCustomerDetails.getCustomerPhoneNumList());
			if (oldData == null) {
				aCustomerPhoneNumber.setVersion(aCustomerPhoneNumber.getVersion() + 1);
				aCustomerPhoneNumber.setRecordType(PennantConstants.RCD_ADD);
			} else {
				isRecordUpdated(aCustomerPhoneNumber, aCustomerDetails.getCustomerPhoneNumList());
			}
			customerPhoneNumbers.add(aCustomerPhoneNumber);
		}

		phoneNumberData.put("errorList", wve);
		phoneNumberData.put("customerPhoneNumbers", customerPhoneNumbers);

		logger.debug(Literal.LEAVING);
		return phoneNumberData;
	}

	private void validatePhoneNumber(Textbox phoneNumber, String regex, String phoneTypeCode) {
		phoneNumber.setMaxlength(dosetFieldLength(regex));

		if (phoneNumber.isReadonly()) {
			return;
		}

		if (phoneNumber.getValue().isEmpty()) {
			throw new WrongValueException(phoneNumber,
					Labels.getLabel("FIELD_IS_MAND", new String[] { Labels.getLabel("listheader_PhoneNumber.label") }));
		}

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(phoneNumber.getValue());
		if (!matcher.matches()) {
			throw new WrongValueException(phoneNumber, Labels.getLabel("FIELD_MOBILE",
					new String[] { Labels.getLabel("listheader_PhoneNumber.label"), String.valueOf(pattern) }));
		}
	}

	private void doSetPhoneTypeDetails(CustomerPhoneNumber customerPhoneNumber, ExtendedCombobox extPhoneType) {
		if (extPhoneType != null) {
			if (extPhoneType.getObject() != null && customerPhoneNumber.isNewRecord()) {
				PhoneType phoneType = (PhoneType) extPhoneType.getObject();
				customerPhoneNumber.setPhoneTypeCode(phoneType.getPhoneTypeCode());
				customerPhoneNumber.setLovDescPhoneTypeCodeName(phoneType.getPhoneTypeDesc());
			}
		}
	}

	private void isRecordUpdated(CustomerPhoneNumber acp, List<CustomerPhoneNumber> list) {
		if (CollectionUtils.isEmpty(list)) {
			return;
		}

		for (CustomerPhoneNumber cp : list) {
			if (PennantConstants.RECORD_TYPE_DEL.equals(cp.getRecordType())
					|| PennantConstants.RECORD_TYPE_CAN.equals(cp.getRecordType()) || acp.isNewRecord()) {
				continue;
			}
			String aPhoneTypeCode = acp.getPhoneTypeCode();
			String phoneTypeCode = cp.getPhoneTypeCode();
			if (StringUtils.equals(aPhoneTypeCode, phoneTypeCode)) {
				int aPhoneTypePriority = acp.getPhoneTypePriority();
				int phoneTypePriority = cp.getPhoneTypePriority();
				if (!acp.getPhoneNumber().equals(cp.getPhoneNumber()) || !aPhoneTypeCode.equals(phoneTypeCode)
						|| aPhoneTypePriority != phoneTypePriority) {
					if (StringUtils.isBlank(acp.getRecordType())) {
						acp.setNewRecord(true);
						if (isFinanceProcess) {
							acp.setNewRecord(false);
						}
						acp.setVersion(acp.getVersion() + 1);
						acp.setRecordType(PennantConstants.RCD_UPD);
					}
				}
			}
		}
	}

	private CustomerPhoneNumber isINexsistingList(CustomerPhoneNumber aCustPhone,
			List<CustomerPhoneNumber> custPhoneList) {
		if (CollectionUtils.isNotEmpty(custPhoneList)) {
			for (CustomerPhoneNumber customerPhoneNumber : custPhoneList) {
				if (StringUtils.equals(aCustPhone.getPhoneTypeCode(), customerPhoneNumber.getPhoneTypeCode())) {
					return customerPhoneNumber;
				}
			}
		}
		return null;
	}

	public int dosetFieldLength(String regex) {
		if (StringUtils.isBlank(regex)) {
			regex = PennantRegularExpressions.REGEX_FAX;
		}

		String length = regex.substring(regex.lastIndexOf("}") - 2, regex.lastIndexOf("}"));
		return Integer.parseInt(length);
	}

	public void setPhoneTypeService(PhoneTypeService phoneTypeService) {
		this.phoneTypeService = phoneTypeService;
	}
}

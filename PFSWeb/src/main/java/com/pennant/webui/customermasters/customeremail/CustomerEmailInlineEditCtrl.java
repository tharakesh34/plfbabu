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
package com.pennant.webui.customermasters.customeremail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.systemmasters.EMailType;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.Constraint.PTEmailValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/Customer/customerDialog.zul file.
 */
public class CustomerEmailInlineEditCtrl extends GFCBaseCtrl<CustomerDetails> {
	private static final Logger logger = Logger.getLogger(CustomerEmailInlineEditCtrl.class);
	private static final long serialVersionUID = -1289772081447044673L;
	private final List<ValueLabel> customerPriorityList = PennantStaticListUtil.getCustomerEmailPriority();
	private boolean isFinanceProcess;

	public CustomerEmailInlineEditCtrl() {
		super();
	}

	public void doRenderEmailsList(List<CustomerEMail> customerEMails, Listbox listbox, String custcif,
			boolean isFinance) {
		//render start
		listbox.getItems().clear();
		if (CollectionUtils.isNotEmpty(customerEMails)) {
			for (CustomerEMail customerEMail : customerEMails) {
				customerEMail.setLovDescCustCIF(custcif);
				doFillEmails(customerEMail, listbox, isFinance);
			}
		}
	}

	/**
	 * This method will prepare the list components for Emails list render
	 * 
	 * @param customerEMail
	 * @param listbox
	 * @param isFinance
	 */
	public void doFillEmails(CustomerEMail customerEMail, Listbox listbox, boolean isFinance) {
		logger.debug(Literal.ENTERING);
		isFinanceProcess = isFinance;
		Space space = null;
		Hbox hbox = null;
		Listitem item = new Listitem();
		Listcell cellCustomerID = new Listcell();
		Listcell cellEmailTypeCode = new Listcell();
		Listcell cellEmailId = new Listcell();
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
		customerId.setWidth("100px");
		customerId.setReadonly(true);
		customerId.setValue(customerEMail.getLovDescCustCIF().trim());
		hbox.appendChild(space);
		hbox.appendChild(customerId);
		cellCustomerID.appendChild(hbox);
		// **************** Email Type
		ExtendedCombobox custEmailType = getEmailType(customerEMail);
		Object[] emailData = new Object[1];
		emailData[0] = cellCustomerID;
		custEmailType.addForward("onFulfill", self, "onFulfillCustEmailType", emailData);
		custEmailType.setReadonly(getUserWorkspace().isReadOnly("CustomerDialog_custEmailType"));
		custEmailType.setTextBoxWidth(150);
		if (!customerEMail.isNewRecord()) {
			custEmailType.setReadonly(true);
		}
		cellEmailTypeCode.appendChild(custEmailType);
		// ********************* email Id
		Textbox emailId = new Textbox();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		space.setSclass(PennantConstants.mandateSclass);
		emailId.setMaxlength(100);
		emailId.setWidth("275px");
		emailId.setReadonly(getUserWorkspace().isReadOnly("CustomerDialog_custEmail"));
		emailId.setValue(customerEMail.getCustEMail());
		hbox.appendChild(space);
		hbox.appendChild(emailId);
		cellEmailId.appendChild(hbox);
		// ********************************** Priority
		Combobox priority = new Combobox();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		space.setSclass(PennantConstants.mandateSclass);
		priority.setMaxlength(30);
		priority.setDisabled(getUserWorkspace().isReadOnly("CustomerDialog_custEmailPriority"));
		fillComboBox(priority, String.valueOf(customerEMail.getCustEMailPriority()), customerPriorityList, "");
		hbox.appendChild(space);
		hbox.appendChild(priority);
		cellPriority.appendChild(hbox);
		// ******************
		cellRecordType.setLabel(PennantJavaUtil.getLabel(customerEMail.getRecordType()));
		// Delete action
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		Button button = new Button();
		button.setSclass("z-toolbarbutton");
		button.setLabel(Labels.getLabel("btnDelete.label"));
		button.setDisabled(getUserWorkspace().isReadOnly("CustomerDialog_custEmailButtonDelete"));
		button.addForward("onClick", self, "onClickEmailButtonDelete", item);
		hbox.appendChild(space);
		hbox.appendChild(button);
		cellDelete.appendChild(hbox);
		// set parent
		cellCustomerID.setParent(item);
		cellEmailTypeCode.setParent(item);
		cellEmailId.setParent(item);
		cellPriority.setParent(item);
		cellRecordType.setParent(item);
		cellDelete.setParent(item);

		item.setAttribute("data", customerEMail);

		listbox.appendChild(item);

		if (PennantConstants.RECORD_TYPE_DEL.equals(customerEMail.getRecordType())) {
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
			CustomerEMail customerEMail = (CustomerEMail) listitem.getAttribute("data");
			Listcell cellRecordType = (Listcell) listitem.getChildren().get(4);

			String emailType = customerEMail.getCustEMailTypeCode();
			String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record");
			if (StringUtils.isNotEmpty(emailType)) {
				msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
						+ Labels.getLabel("listheader_CustEMailTypeCode.label") + " : " + emailType;
			}
			// Show a confirm box
			if (MessageUtil.confirm(msg) == MessageUtil.YES) {
				doReadOnly(listitem);
				if (customerEMail.isNewRecord()) {
					listbox.removeChild(listitem);
					return;
				} else if (StringUtils.isBlank(customerEMail.getRecordType())) {
					customerEMail.setVersion(customerEMail.getVersion() + 1);
					customerEMail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					if (!isFinanceProcess) {
						customerEMail.setNewRecord(true);
					}
				} else if (PennantConstants.RECORD_TYPE_UPD.equals(customerEMail.getRecordType())) {
					customerEMail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RECORD_TYPE_NEW.equals(customerEMail.getRecordType())) {
					customerEMail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				}

				cellRecordType.setLabel(PennantJavaUtil.getLabel(customerEMail.getRecordType()));
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
		delete.setDisabled(true);
	}

	/**
	 * Creating extended combo box component
	 * 
	 * @param customerEMail
	 * @return
	 */
	private ExtendedCombobox getEmailType(CustomerEMail customerEMail) {
		ExtendedCombobox custEmailType = new ExtendedCombobox();
		custEmailType.setMaxlength(8);
		custEmailType.getTextbox().setMaxlength(50);
		custEmailType.setMandatoryStyle(true);
		custEmailType.setTextBoxWidth(116);
		custEmailType.setModuleName("EMailType");
		custEmailType.setValueColumn("EmailTypeCode");
		custEmailType.setDescColumn("EmailTypeDesc");
		custEmailType.setValidateColumns(new String[] { "EmailTypeCode" });
		custEmailType.setValue(customerEMail.getCustEMailTypeCode(), customerEMail.getLovDescCustEMailTypeCode());
		//Object data preparing
		EMailType eMailType = new EMailType();
		custEmailType.setObject(eMailType);
		return custEmailType;
	}

	/**
	 * This method will prepare the customer Emails list for data saving
	 * 
	 * @param aCustomerDetails
	 * @param listBoxEmail
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, List> prepareCustomerEmailData(CustomerDetails aCustomerDetails, Listbox listBoxEmail) {
		logger.debug(Literal.ENTERING);
		List<Listitem> listItems = listBoxEmail.getItems();
		List<CustomerEMail> customerEMails = new ArrayList<>();
		Customer customer = aCustomerDetails.getCustomer();
		ArrayList<WrongValueException> wve = new ArrayList<>();
		Map<String, List> emailData = new HashMap<>();
		if (!CollectionUtils.isEmpty(listItems)) {
			for (Listitem listItem : listItems) {
				CustomerEMail aCustomerEMail = (CustomerEMail) listItem.getAttribute("data");
				if (aCustomerEMail != null) {
					aCustomerEMail.setLovDescCustShrtName(customer.getCustShrtName());
					aCustomerEMail.setCustID(customer.getCustID());
					aCustomerEMail.setLovDescCustCIF(customer.getCustCIF());
					List<Component> listCells = listItem.getChildren();
					try {
						// Getting Email Type
						ExtendedCombobox extEmailType = (ExtendedCombobox) listCells.get(1).getChildren().get(0);
						if (aCustomerEMail.isNewRecord() && StringUtils.isEmpty(extEmailType.getValidatedValue())) {
							throw new WrongValueException(extEmailType.getTextbox(), Labels.getLabel("FIELD_IS_MAND",
									new String[] { Labels.getLabel("listheader_CustEMailTypeCode.label") }));
						}
						doSetEmailTypeDetails(aCustomerEMail, extEmailType);
					} catch (WrongValueException we) {
						wve.add(we);
					}

					try {
						// Getting EmailId
						Textbox emailId = (Textbox) listCells.get(2).getChildren().get(0).getLastChild();
						if (emailId != null) {
							if (!emailId.isReadonly() && (emailId.getValue().isEmpty())) {
								throw new WrongValueException(emailId, Labels.getLabel("FIELD_IS_MAND",
										new String[] { Labels.getLabel("listheader_CustomerEmail.label") }));
							} else if (!emailId.isReadonly()) {
								emailId.setConstraint(new PTEmailValidator(
										Labels.getLabel("label_CustomerEMailDialog_CustEMail.value"), true));
								aCustomerEMail.setCustEMail(emailId.getValue());
							}
							aCustomerEMail.setCustEMail(emailId.getValue());
						}
					} catch (WrongValueException we) {
						wve.add(we);
					}

					try {
						// Getting Priority
						Combobox priority = (Combobox) listCells.get(3).getChildren().get(0).getLastChild();
						if ("#".equals(getComboboxValue(priority))) {
							throw new WrongValueException(priority, Labels.getLabel("STATIC_INVALID",
									new String[] { Labels.getLabel("listheader_CustEMailPriority.label") }));
						} else {
							aCustomerEMail.setCustEMailPriority(
									Integer.parseInt(priority.getSelectedItem().getValue().toString()));
						}
					} catch (WrongValueException we) {
						wve.add(we);
					}
					CustomerEMail oldData = isINexsistingList(aCustomerEMail, aCustomerDetails.getCustomerEMailList());
					if (oldData == null) {
						aCustomerEMail.setVersion(aCustomerEMail.getVersion() + 1);
						aCustomerEMail.setRecordType(PennantConstants.RCD_ADD);
					} else {
						isRecordUpdated(aCustomerEMail, aCustomerDetails.getCustomerEMailList());
					}
					customerEMails.add(aCustomerEMail);

				}
			}
			emailData.put("errorList", wve);
			emailData.put("customerEMails", customerEMails);
		}
		logger.debug(Literal.LEAVING);
		return emailData;
	}

	/**
	 * Method will set the Email type default values to customerEMail bean
	 * 
	 * @param customerEMail
	 * @param extEmailType
	 */
	private void doSetEmailTypeDetails(CustomerEMail customerEMail, ExtendedCombobox extEmailType) {

		// Getting Email Type
		if (extEmailType != null) {
			if (extEmailType.getObject() != null && customerEMail.isNewRecord()) {
				EMailType eMailType = (EMailType) extEmailType.getObject();
				customerEMail.setCustEMailTypeCode(eMailType.getEmailTypeCode());
				customerEMail.setLovDescCustEMailTypeCode(eMailType.getEmailTypeDesc());
			}
		}
	}

	/**
	 * Method will check if the newly added record is already available in the list
	 * 
	 * @param aCustomerEMail
	 * @param list
	 * @return
	 */
	private void isRecordUpdated(CustomerEMail aCustomerEMail, List<CustomerEMail> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			for (CustomerEMail customerEMail : list) {
				if (PennantConstants.RECORD_TYPE_DEL.equals(customerEMail.getRecordType())
						|| PennantConstants.RECORD_TYPE_CAN.equals(customerEMail.getRecordType())
						|| aCustomerEMail.isNewRecord()) {
					continue;
				}
				if (StringUtils.equals(aCustomerEMail.getCustEMailTypeCode(), customerEMail.getCustEMailTypeCode())) {

					//checking data is updated or not
					if (!aCustomerEMail.getCustEMail().equals(customerEMail.getCustEMail())
							|| !aCustomerEMail.getCustEMailTypeCode().equals(customerEMail.getCustEMailTypeCode())
							|| aCustomerEMail.getCustEMailPriority() != customerEMail.getCustEMailPriority()) {
						if (StringUtils.isBlank(aCustomerEMail.getRecordType())) {
							aCustomerEMail.setNewRecord(true);
							if (isFinanceProcess) {
								aCustomerEMail.setNewRecord(false);
							}
							aCustomerEMail.setId(customerEMail.getId());
							aCustomerEMail.setVersion(aCustomerEMail.getVersion() + 1);
							aCustomerEMail.setRecordType(PennantConstants.RCD_UPD);
						}
					}
				}
			}
		}
	}

	/**
	 * Method will check if the newly added record is already available in the list
	 * 
	 * @param aCustomerEMail
	 * @param customerEMailList
	 * @return
	 */
	private CustomerEMail isINexsistingList(CustomerEMail aCustomerEMail, List<CustomerEMail> customerEMailList) {
		if (CollectionUtils.isNotEmpty(customerEMailList)) {
			for (CustomerEMail customerEMail : customerEMailList) {
				if (StringUtils.equals(aCustomerEMail.getCustEMailTypeCode(), customerEMail.getCustEMailTypeCode())) {
					return customerEMail;
				}
			}
		}
		return null;
	}
}

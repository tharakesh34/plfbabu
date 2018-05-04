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
 * FileName    		:  BranchDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.branch;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.SessionUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.applicationmaster.BranchService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTPhoneNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.framework.security.core.User;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/Branch/branchDialog.zul file.
 */
public class BranchDialogCtrl extends GFCBaseCtrl<Branch> {
	private static final long			serialVersionUID	= -4832204841676720745L;
	private static final Logger			logger				= Logger.getLogger(BranchDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window					window_BranchDialog;

	protected Textbox					branchCode;
	protected Textbox					branchDesc;
	protected Textbox					branchAddrLine1;
	protected Textbox					branchAddrLine2;
	protected Textbox					branchPOBox;
	protected ExtendedCombobox			branchCity;
	protected ExtendedCombobox			branchProvince;
	protected ExtendedCombobox			branchCountry;
	protected Textbox					branchFax;
	protected Textbox					faxCountryCode;
	protected Textbox					faxAreaCode;
	protected Textbox					branchTel;
	protected Textbox					phoneCountryCode;
	protected Textbox					phoneAreaCode;
	protected Textbox					branchSwiftBankCode;
	protected ExtendedCombobox			branchSwiftCountry;
	protected Textbox					branchSwiftLocCode;
	protected Textbox					branchSwiftBrnCde;
	protected Textbox					branchSortCode;
	protected Checkbox					branchIsActive;
	protected Textbox					cityName;
	protected Row						row_NewBranch;
	protected ExtendedCombobox			newBranchCode;
	protected Checkbox					miniBranch;
	protected Combobox					branchType;
	protected ExtendedCombobox			parentBranch;
	protected Combobox					region;
	protected Textbox					bankRefNo;
	protected Textbox					branchAddrHNbr;
	protected Textbox					branchFlatNbr;
	protected Textbox					branchAddrStreet;
	protected ExtendedCombobox			pinCode;

	// not autoWired Var's
	private Branch						branch;															// overHanded per parameter
	private transient BranchListCtrl	branchListCtrl;													// overHanded per parameter

	private transient boolean			validationOn;

	// ServiceDAOs / Domain Classes
	private transient BranchService		branchService;
	private transient String			sBranchCountry;
	private transient String			sBranchProvince;
	private transient String			sBranchCity;
	private transient String			sPinCode;

	private final List<ValueLabel>		branchTypeList		= PennantStaticListUtil.getBranchTypeList();
	private final List<ValueLabel>		regionList			= PennantStaticListUtil.getRegionList();

	/**
	 * default constructor.<br>
	 */
	public BranchDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "BranchDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Branch object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_BranchDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_BranchDialog);

		try {
			if (PennantConstants.CITY_FREETEXT) {
				this.branchCity.setVisible(false);
				this.cityName.setVisible(true);
			} else {
				this.branchCity.setVisible(true);
				this.cityName.setVisible(false);
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED parameters !
			if (arguments.containsKey("branch")) {
				this.branch = (Branch) arguments.get("branch");
				Branch befImage = new Branch();
				BeanUtils.copyProperties(this.branch, befImage);
				this.branch.setBefImage(befImage);

				setBranch(this.branch);
			} else {
				setBranch(null);
			}

			doLoadWorkFlow(this.branch.isWorkflow(), this.branch.getWorkflowId(), this.branch.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "BranchDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the branchListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete branch here.
			if (arguments.containsKey("branchListCtrl")) {
				setBranchListCtrl((BranchListCtrl) arguments.get("branchListCtrl"));
			} else {
				setBranchListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getBranch());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_BranchDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		this.branchCode.setMaxlength(LengthConstants.LEN_BRANCH);
		this.branchDesc.setMaxlength(50);
		this.branchAddrLine1.setMaxlength(50);
		this.branchAddrLine2.setMaxlength(50);
		this.branchPOBox.setMaxlength(8);
		this.branchCity.setMaxlength(50);
		this.cityName.setMaxlength(50);
		this.branchProvince.setMaxlength(8);
		this.branchCountry.setMaxlength(2);
		this.faxCountryCode.setMaxlength(4);
		this.faxAreaCode.setMaxlength(4);
		this.branchFax.setMaxlength(8);
		this.phoneCountryCode.setMaxlength(3);
		this.phoneAreaCode.setMaxlength(3);
		this.branchTel.setMaxlength(8);
		this.branchSwiftBankCode.setMaxlength(4);
		this.branchSwiftLocCode.setMaxlength(2);
		this.branchSwiftBrnCde.setMaxlength(3);
		this.branchSortCode.setMaxlength(6);
		this.bankRefNo.setMaxlength(20);
		this.branchAddrHNbr.setMaxlength(50);
		this.branchFlatNbr.setMaxlength(50);
		this.branchAddrStreet.setMaxlength(50);

		this.branchCountry.setMandatoryStyle(true);
		this.branchCountry.setModuleName("Country");
		this.branchCountry.setValueColumn("CountryCode");
		this.branchCountry.setDescColumn("CountryDesc");
		this.branchCountry.setValidateColumns(new String[] { "CountryCode" });

		this.branchProvince.setMaxlength(8);
		this.branchProvince.setMandatoryStyle(true);
		this.branchProvince.setModuleName("Province");
		this.branchProvince.setValueColumn("CPProvince");
		this.branchProvince.setDescColumn("CPProvinceName");
		this.branchProvince.setValidateColumns(new String[] { "CPProvince" });

		this.branchCity.setMaxlength(8);
		this.branchCity.setMandatoryStyle(false);
		this.branchCity.setModuleName("City");
		this.branchCity.setValueColumn("PCCity");
		this.branchCity.setDescColumn("PCCityName");
		this.branchCity.setValidateColumns(new String[] { "PCCity" });

		this.branchSwiftCountry.setMaxlength(2);
		this.branchSwiftCountry.setMandatoryStyle(false);
		this.branchSwiftCountry.setModuleName("Country");
		this.branchSwiftCountry.setValueColumn("CountryCode");
		this.branchSwiftCountry.setDescColumn("CountryDesc");
		this.branchSwiftCountry.setValidateColumns(new String[] { "CountryCode" });

		this.newBranchCode.setMaxlength(12);
		this.newBranchCode.setMandatoryStyle(true);
		this.newBranchCode.setModuleName("Branch");
		this.newBranchCode.setValueColumn("BranchCode");
		this.newBranchCode.setDescColumn("BranchDesc");
		this.newBranchCode.setValidateColumns(new String[] { "BranchCode" });

		this.parentBranch.setMaxlength(12);
		this.parentBranch.setModuleName("Branch");
		this.parentBranch.setValueColumn("BranchCode");
		this.parentBranch.setDescColumn("BranchDesc");
		this.parentBranch.setValidateColumns(new String[] { "BranchCode", "BranchDesc" });

		this.pinCode.setMaxlength(10);
		this.pinCode.setMandatoryStyle(true);
		this.pinCode.setModuleName("PinCode");
		this.pinCode.setValueColumn("PinCode");
		this.pinCode.setDescColumn("AreaName");
		this.pinCode.setValidateColumns(new String[] { "PinCode" });

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_BranchDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_BranchDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_BranchDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_BranchDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
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
		MessageUtil.showHelpWindow(event, window_BranchDialog);
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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.branch.getBefImage());
		doReadOnly();
		doSetNewBranchProp();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aBranch
	 *            Branch
	 */
	public void doWriteBeanToComponents(Branch aBranch) {
		logger.debug("Entering");
		this.branchCode.setValue(aBranch.getBranchCode());
		this.branchDesc.setValue(aBranch.getBranchDesc());
		this.branchAddrLine1.setValue(aBranch.getBranchAddrLine1());
		this.branchAddrLine2.setValue(aBranch.getBranchAddrLine2());
		this.branchPOBox.setValue(aBranch.getBranchPOBox());
		this.branchCity.setValue(aBranch.getBranchCity());
		this.cityName.setValue(aBranch.getBranchCity());
		this.branchProvince.setValue(aBranch.getBranchProvince());
		this.branchCountry.setValue(aBranch.getBranchCountry());
		this.bankRefNo.setValue(aBranch.getBankRefNo());
		this.branchAddrHNbr.setValue(aBranch.getBranchAddrHNbr());
		this.branchFlatNbr.setValue(aBranch.getBranchFlatNbr());
		this.branchAddrStreet.setValue(aBranch.getBranchAddrStreet());
		String[] fax = PennantApplicationUtil.unFormatPhoneNumber(aBranch.getBranchFax());
		this.faxCountryCode.setValue(fax[0]);
		this.faxAreaCode.setValue(fax[1]);
		this.branchFax.setValue(fax[2]);
		String[] telephone = PennantApplicationUtil.unFormatPhoneNumber(aBranch.getBranchTel());
		this.phoneCountryCode.setValue(telephone[0]);
		this.phoneAreaCode.setValue(telephone[1]);
		this.branchTel.setValue(telephone[2]);
		this.branchSwiftBankCode.setValue(aBranch.getBranchSwiftBankCde());
		this.branchSwiftCountry.setValue(aBranch.getBranchSwiftCountry());
		this.branchSwiftLocCode.setValue(aBranch.getBranchSwiftLocCode());
		this.branchSwiftBrnCde.setValue(aBranch.getBranchSwiftBrnCde());
		this.branchSortCode.setValue(aBranch.getBranchSortCode());
		this.branchIsActive.setChecked(aBranch.isBranchIsActive());
		this.newBranchCode.setValue(aBranch.getNewBranchCode());
		this.miniBranch.setChecked(aBranch.isMiniBranch());
		fillComboBox(this.branchType, aBranch.getBranchType(), branchTypeList, "");
		fillComboBox(this.region, aBranch.getRegion(), regionList, "");
		this.pinCode.setValue(aBranch.getPinCode());
		if (this.miniBranch.isChecked()) {
			this.parentBranch.setAttribute("branchCode", aBranch.getBranchCode());
			this.parentBranch.setValue(aBranch.getBranchCode(), aBranch.getBranchDesc());
		}

		if (aBranch.isNewRecord()) {
			this.branchCity.setDescription("");
			this.branchProvince.setDescription("");
			this.branchCountry.setDescription("");
			this.branchSwiftCountry.setDescription("");
			this.newBranchCode.setDescription("");
			this.parentBranch.setDescription("");
			this.pinCode.setDescription("");
		} else {
			this.branchCity.setDescription(aBranch.getLovDescBranchCityName());
			this.branchProvince.setDescription(aBranch.getLovDescBranchProvinceName());
			this.branchCountry.setDescription(aBranch.getLovDescBranchCountryName());
			this.branchSwiftCountry.setDescription(aBranch.getLovDescBranchSwiftCountryName());
			this.newBranchCode.setDescription(aBranch.getNewBranchDesc());
			this.pinCode.setDescription(aBranch.getPinAreaDesc());
		}
		this.recordStatus.setValue(aBranch.getRecordStatus());
		if (aBranch.isNew() || (aBranch.getRecordType() != null ? aBranch.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.branchIsActive.setChecked(true);
			this.branchIsActive.setDisabled(true);
		}
		sBranchCountry = this.branchCountry.getValue();
		sBranchProvince = this.branchProvince.getValue();
		sBranchCity = this.branchCity.getValue();
		sPinCode = this.pinCode.getValue();
		
		if (!aBranch.isNew()) {
			Filter[] filterProvince = new Filter[1];
			filterProvince[0] = new Filter("CPCountry", sBranchCountry, Filter.OP_EQUAL);
			this.branchProvince.setFilters(filterProvince);
			Filter[] filterCity = new Filter[1];
			filterCity[0] = new Filter("PCProvince", sBranchProvince, Filter.OP_EQUAL);
			this.branchCity.setFilters(filterCity);
			Filter[] filterPin = new Filter[1];
			filterPin[0] = new Filter("City", sBranchCity, Filter.OP_EQUAL);
			this.pinCode.setFilters(filterPin);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aBranch
	 */
	public void doWriteComponentsToBean(Branch aBranch) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		/*
		 * try { aBranch.setBranchCode(StringUtils.leftPad(this.branchCode.getValue() ,LengthConstants.LEN_BRANCH,'0'));
		 * }catch (WrongValueException we ) { wve.add(we); }
		 */
		try {
			aBranch.setBranchCode(this.branchCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBranch.setBranchDesc(this.branchDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setBranchAddrLine1(this.branchAddrLine1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setBranchAddrLine2(this.branchAddrLine2.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setBranchPOBox(this.branchPOBox.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (PennantConstants.CITY_FREETEXT) {
				aBranch.setBranchCity(StringUtils.trimToNull(this.cityName.getValue()));
			} else {
				aBranch.setLovDescBranchCityName(StringUtils.trimToNull(this.branchCity.getDescription()));
				aBranch.setBranchCity(StringUtils.trimToNull(this.branchCity.getValidatedValue()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setLovDescBranchCityName(this.branchCity.getDescription());
			aBranch.setBranchCity(this.branchCity.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setLovDescBranchProvinceName(this.branchProvince.getDescription());
			aBranch.setBranchProvince(this.branchProvince.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setLovDescBranchCountryName(this.branchCountry.getDescription());
			aBranch.setBranchCountry(this.branchCountry.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setBranchFax(PennantApplicationUtil.formatPhoneNumber(this.faxCountryCode.getValue(),
					this.faxAreaCode.getValue(), this.branchFax.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setBranchTel(PennantApplicationUtil.formatPhoneNumber(this.phoneCountryCode.getValue(),
					this.phoneAreaCode.getValue(), this.branchTel.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setBranchSwiftBankCde(this.branchSwiftBankCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setBranchSwiftCountry(this.branchSwiftCountry.getValidatedValue().toUpperCase());
			aBranch.setLovDescBranchSwiftCountryName(this.branchSwiftCountry.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setBranchSwiftLocCode(this.branchSwiftLocCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setBranchSwiftBrnCde(this.branchSwiftBrnCde.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setBranchSortCode(this.branchSortCode.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setBranchIsActive(this.branchIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setNewBranchCode(this.newBranchCode.getValidatedValue());
			aBranch.setNewBranchDesc(this.newBranchCode.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setMiniBranch(this.miniBranch.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setBranchType(this.branchType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setRegion(this.region.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.parentBranch.getValidatedValue();
			String parentBranch = String.valueOf(this.parentBranch.getAttribute("parentBranch"));
			aBranch.setParentBranch(parentBranch);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (this.branchIsActive.isChecked()) {
			aBranch.setNewBranchCode("");
			aBranch.setNewBranchDesc("");
		}

		try {
			aBranch.setBankRefNo(this.bankRefNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBranch.setBranchAddrHNbr(this.branchAddrHNbr.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBranch.setBranchFlatNbr(this.branchFlatNbr.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBranch.setBranchAddrStreet(this.branchAddrStreet.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBranch.setPinCode(this.pinCode.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aBranch.setRecordStatus(this.recordStatus.getValue());
		
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aBranch
	 * @throws Exception
	 */
	public void doShowDialog(Branch aBranch) throws Exception {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aBranch.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.branchCode.focus();
		} else {
			this.branchDesc.focus();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aBranch.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aBranch);
			doSetNewBranchProp();

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_BranchDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	private void doSetNewBranchProp() {
		logger.debug("Entering");
		if (!this.branchIsActive.isChecked() && !getBranch().isNewRecord()) {
			this.row_NewBranch.setVisible(true);
			this.newBranchCode.setMandatoryStyle(true);
		} else {
			this.row_NewBranch.setVisible(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.branchCode.isReadonly()) {
			this.branchCode.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchCode.value"),
					PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		if (!this.branchDesc.isReadonly()) {
			this.branchDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchDesc.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.branchAddrLine1.isReadonly()) {
			this.branchAddrLine1
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchAddrLine1.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.branchAddrLine2.isReadonly()) {
			this.branchAddrLine2
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchAddrLine2.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.branchPOBox.isReadonly()) {
			this.branchPOBox
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchPOBox.value"),
							PennantRegularExpressions.REGEX_NUMERIC, false));
		}
		if (!this.faxCountryCode.isReadonly()) {
			this.faxCountryCode.setConstraint(
					new PTPhoneNumberValidator(Labels.getLabel("label_BranchDialog_FaxCountryCode.value"), true, 1));
		}
		if (!this.faxAreaCode.isReadonly()) {
			this.faxAreaCode.setConstraint(
					new PTPhoneNumberValidator(Labels.getLabel("label_BranchDialog_FaxAreaCode.value"), true, 2));
		}
		if (!this.branchFax.isReadonly()) {
			this.branchFax.setConstraint(
					new PTPhoneNumberValidator(Labels.getLabel("label_BranchDialog_BranchFax.value"), true, 3));
		}
		if (!this.phoneCountryCode.isReadonly()) {
			this.phoneCountryCode.setConstraint(
					new PTPhoneNumberValidator(Labels.getLabel("label_BranchDialog_phoneCountryCode.value"), true, 1));
		}
		if (!this.phoneAreaCode.isReadonly()) {
			this.phoneAreaCode.setConstraint(
					new PTPhoneNumberValidator(Labels.getLabel("label_BranchDialog_phoneAreaCode.value"), true, 2));
		}
		if (!this.branchTel.isReadonly()) {
			this.branchTel.setConstraint(
					new PTPhoneNumberValidator(Labels.getLabel("label_BranchDialog_BranchTel.value"), true, 3));
		}
		if (!this.branchSwiftBankCode.isReadonly()) {
			this.branchSwiftBankCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchSwiftBankCde.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_FL4, false));
		}
		if (!this.branchSwiftLocCode.isReadonly()) {
			this.branchSwiftLocCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchSwiftLocCode.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_FL2, false));
		}
		if (!this.branchSwiftBrnCde.isReadonly()) {
			this.branchSwiftBrnCde
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchSwiftBrnCde.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		if (!this.branchSortCode.isReadonly()) {
			this.branchSortCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchSortCode.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_FL4, false));
		}
		if (!this.branchCountry.isReadonly()) {
			this.branchCountry.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchCountry.value"), null, true, true));
		}
		if (!this.branchProvince.isReadonly()) {
			this.branchProvince.setConstraint(new PTStringValidator(
					Labels.getLabel("label_BranchDialog_BranchProvince.value"), null, true, true));
		}
		if (!this.branchCity.isReadonly()) {
			this.branchCity.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchCity.value"), null, true, true));
		}
		if (PennantConstants.CITY_FREETEXT) {
			if (!this.cityName.isReadonly()) {
				this.cityName.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_CityName.value"),
						PennantRegularExpressions.REGEX_NAME, false));
			}

		} else {
			if (!this.branchCity.isReadonly()) {
				this.branchCity.setConstraint(new PTStringValidator(
						Labels.getLabel("label_BranchDialog_BranchCity.value"), null, false, true));

			}

		}
		if (!this.branchSwiftCountry.isReadonly()) {
			this.branchSwiftCountry.setConstraint(new PTStringValidator(
					Labels.getLabel("label_BranchDialog_BranchSwiftCountry.value"), null, false, true));
		}
		if (this.row_NewBranch.isVisible() && !this.newBranchCode.isReadonly()) {
			this.newBranchCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BranchDialog_NewBranchCode.value"), null, true, true));
		}
		if (!this.branchType.isDisabled()) {
			this.branchType.setConstraint(
					new StaticListValidator(branchTypeList, Labels.getLabel("label_BranchDialog_BranchType.value")));
		}
		if (!this.region.isDisabled()) {
			this.region.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BranchDialog_Region.value"), null, false, true));
		}
		if (!this.bankRefNo.isReadonly()) {
			this.bankRefNo.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BankRefNo.value"),
					PennantRegularExpressions.REGEX_NUMERIC, false));
		}
		if (this.miniBranch.isChecked()) {
			this.parentBranch.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BranchDialog_ParentBranch.value"), null, true, true));
		}
		if (!this.branchAddrHNbr.isReadonly()) {
			this.branchAddrHNbr
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchAddrHNbr.value"),
							PennantRegularExpressions.REGEX_ADDRESS, true));
		}
		if (!this.branchFlatNbr.isReadonly()) {
			this.branchFlatNbr
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchFlatNbr.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.branchAddrStreet.isReadonly()) {
			this.branchAddrStreet
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_BranchAddrStreet.value"),
							PennantRegularExpressions.REGEX_ADDRESS, true));
		}
		if (!this.pinCode.isReadonly()) {
			this.pinCode.setConstraint(new PTStringValidator(Labels.getLabel("label_BranchDialog_PinCode.value"),
					PennantRegularExpressions.REGEX_ADDRESS, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.branchCode.setConstraint("");
		this.branchDesc.setConstraint("");
		this.branchAddrLine1.setConstraint("");
		this.branchAddrLine2.setConstraint("");
		this.branchPOBox.setConstraint("");
		this.branchFax.setConstraint("");
		this.branchTel.setConstraint("");
		this.branchSwiftBankCode.setConstraint("");
		this.branchSwiftCountry.setConstraint("");
		this.branchSwiftLocCode.setConstraint("");
		this.branchSwiftBrnCde.setConstraint("");
		this.branchSortCode.setConstraint("");
		this.branchCountry.setConstraint("");
		this.branchCity.setConstraint("");
		this.cityName.setConstraint("");
		this.branchProvince.setConstraint("");
		this.branchSwiftCountry.setConstraint("");
		this.phoneCountryCode.setConstraint("");
		this.phoneAreaCode.setConstraint("");
		this.faxCountryCode.setConstraint("");
		this.faxAreaCode.setConstraint("");
		this.newBranchCode.setConstraint("");
		this.branchType.setConstraint("");
		this.region.setConstraint("");
		this.parentBranch.setConstraint("");
		this.bankRefNo.setConstraint("");
		this.branchAddrHNbr.setConstraint("");
		this.branchFlatNbr.setConstraint("");
		this.branchAddrStreet.setConstraint("");
		this.pinCode.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Enterring");
		this.branchCode.setErrorMessage("");
		this.branchDesc.setErrorMessage("");
		this.branchAddrLine1.setErrorMessage("");
		this.branchAddrLine2.setErrorMessage("");
		this.branchPOBox.setErrorMessage("");
		this.branchFax.setErrorMessage("");
		this.branchTel.setErrorMessage("");
		this.branchSwiftBankCode.setErrorMessage("");
		this.branchSwiftCountry.setErrorMessage("");
		this.branchSwiftLocCode.setErrorMessage("");
		this.branchSwiftBrnCde.setErrorMessage("");
		this.branchSortCode.setErrorMessage("");
		this.branchCity.setErrorMessage("");
		this.cityName.setErrorMessage("");
		this.branchProvince.setErrorMessage("");
		this.branchCountry.setErrorMessage("");
		this.phoneCountryCode.setErrorMessage("");
		this.phoneAreaCode.setErrorMessage("");
		this.faxCountryCode.setErrorMessage("");
		this.faxAreaCode.setErrorMessage("");
		this.newBranchCode.setErrorMessage("");
		this.branchType.setErrorMessage("");
		this.parentBranch.setErrorMessage("");
		this.region.setErrorMessage("");
		this.bankRefNo.setErrorMessage("");
		this.branchAddrHNbr.setErrorMessage("");
		this.branchFlatNbr.setErrorMessage("");
		this.branchAddrStreet.setErrorMessage("");
		this.pinCode.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getBranchListCtrl().search();
	}

	// CRUD operations

	/**
	 * Deletes a Branch object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final Branch aBranch = new Branch();
		BeanUtils.copyProperties(getBranch(), aBranch);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_BranchDialog_BranchCode.value") + " : " + aBranch.getBranchCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aBranch.getRecordType())) {
				aBranch.setVersion(aBranch.getVersion() + 1);
				aBranch.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aBranch.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aBranch, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (Exception e) {
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
		if (getBranch().isNewRecord()) {
			this.branchCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.branchCountry.setMandatoryStyle(true);
			this.branchCity.setMandatoryStyle(true);
			this.branchProvince.setMandatoryStyle(true);
			this.pinCode.setMandatoryStyle(true);
			this.branchCode.setReadonly(isReadOnly("BranchDialog_newBranchCode"));
			this.btnCancel.setVisible(true);
		}

		this.branchDesc.setReadonly(isReadOnly("BranchDialog_branchDesc"));
		this.branchAddrLine1.setReadonly(isReadOnly("BranchDialog_branchAddrLine1"));
		this.branchAddrLine2.setReadonly(isReadOnly("BranchDialog_branchAddrLine2"));
		this.branchPOBox.setReadonly(isReadOnly("BranchDialog_branchPOBox"));
		this.branchCity.setReadonly(isReadOnly("BranchDialog_branchCity"));
		this.cityName.setReadonly(isReadOnly("BranchDialog_branchCity"));
		this.branchProvince.setReadonly(isReadOnly("BranchDialog_branchProvince"));
		this.branchCountry.setReadonly(isReadOnly("BranchDialog_branchCountry"));
		this.faxAreaCode.setReadonly(isReadOnly("BranchDialog_branchFax"));
		this.faxCountryCode.setReadonly(isReadOnly("BranchDialog_branchFax"));
		this.branchFax.setReadonly(isReadOnly("BranchDialog_branchFax"));
		this.branchTel.setReadonly(isReadOnly("BranchDialog_branchTel"));
		this.phoneAreaCode.setReadonly(isReadOnly("BranchDialog_branchTel"));
		this.phoneCountryCode.setReadonly(isReadOnly("BranchDialog_branchTel"));
		this.branchSwiftBankCode.setReadonly(isReadOnly("BranchDialog_branchSwiftBankCde"));
		this.branchSwiftCountry.setReadonly(isReadOnly("BranchDialog_branchSwiftCountry"));
		this.branchSwiftCountry.setReadonly(isReadOnly("BranchDialog_branchCountry"));
		this.branchSwiftLocCode.setReadonly(isReadOnly("BranchDialog_branchSwiftLocCode"));
		this.branchSwiftBrnCde.setReadonly(isReadOnly("BranchDialog_branchSwiftBrnCde"));
		this.branchSortCode.setReadonly(isReadOnly("BranchDialog_branchSortCode"));
		this.branchIsActive.setDisabled(isReadOnly("BranchDialog_branchIsActive"));
		this.branchType.setDisabled(isReadOnly("BranchDialog_BranchType"));
		this.miniBranch.setDisabled(isReadOnly("BranchDialog_MiniBranch"));
		this.bankRefNo.setReadonly(isReadOnly("BranchDialog_BankRefNo"));
		this.branchAddrHNbr.setReadonly(isReadOnly("BranchDialog_BranchAddrHNbr"));
		this.branchFlatNbr.setReadonly(isReadOnly("BranchDialog_BranchFlatNbr"));
		this.branchAddrStreet.setReadonly(isReadOnly("BranchDialog_BranchAddrStreet"));
		this.pinCode.setReadonly(isReadOnly("BranchDialog_PinCode"));
		if (this.miniBranch.isChecked()) {
			this.parentBranch.setReadonly(isReadOnly("BranchDialog_ParentBranch"));
		} else {
			this.parentBranch.setReadonly(true);
		}
		this.region.setDisabled(isReadOnly("BranchDialog_Region"));
		if (getBranch().isBranchIsActive()) {
			this.newBranchCode.setReadonly(isReadOnly("BranchDialog_newBranchCode"));
		} else {
			this.newBranchCode.setReadonly(true);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.branch.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			// btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.branchCode.setReadonly(true);
		this.branchDesc.setReadonly(true);
		this.branchAddrLine1.setReadonly(true);
		this.branchAddrLine2.setReadonly(true);
		this.branchPOBox.setReadonly(true);
		this.branchCity.setReadonly(true);
		this.cityName.setReadonly(true);
		this.branchProvince.setReadonly(true);
		this.branchCountry.setReadonly(true);
		this.branchFax.setReadonly(true);
		this.faxAreaCode.setReadonly(true);
		this.faxCountryCode.setReadonly(true);
		this.phoneAreaCode.setReadonly(true);
		this.phoneCountryCode.setReadonly(true);
		this.branchTel.setReadonly(true);
		this.branchSwiftBankCode.setReadonly(true);
		this.branchSwiftCountry.setReadonly(true);
		this.branchSwiftLocCode.setReadonly(true);
		this.branchSwiftBrnCde.setReadonly(true);
		this.branchSortCode.setReadonly(true);
		this.branchIsActive.setDisabled(true);
		this.newBranchCode.setReadonly(true);
		this.branchType.setReadonly(true);
		this.parentBranch.setReadonly(true);
		this.region.setReadonly(true);
		this.bankRefNo.setReadonly(true);
		this.branchAddrHNbr.setReadonly(true);
		this.branchFlatNbr.setReadonly(true);
		this.branchAddrStreet.setReadonly(true);
		this.pinCode.setReadonly(true);
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
		this.branchCode.setValue("");
		this.branchDesc.setValue("");
		this.branchAddrLine1.setValue("");
		this.branchAddrLine2.setValue("");
		this.branchPOBox.setValue("");
		this.branchCity.setValue("");
		this.branchCity.setDescription("");
		this.cityName.setValue("");
		this.branchProvince.setValue("");
		this.branchProvince.setDescription("");
		this.branchCountry.setValue("");
		this.branchCountry.setDescription("");
		this.branchFax.setValue("");
		this.branchTel.setValue("");
		this.branchSwiftBankCode.setValue("");
		this.branchSwiftCountry.setValue("");
		this.branchSwiftCountry.setDescription("");
		this.branchSwiftLocCode.setValue("");
		this.branchSwiftBrnCde.setValue("");
		this.branchSortCode.setValue("");
		this.phoneCountryCode.setValue("");
		this.phoneAreaCode.setValue("");
		this.faxCountryCode.setValue("");
		this.faxAreaCode.setValue("");
		this.branchIsActive.setChecked(false);
		this.newBranchCode.setValue("");
		this.miniBranch.setValue("");
		this.branchType.setValue("");
		this.parentBranch.setValue("");
		this.region.setValue("");
		this.bankRefNo.setValue("");
		this.branchAddrStreet.setValue("");
		this.pinCode.setValue("");
		this.branchFlatNbr.setValue("");
		this.branchAddrHNbr.setValue("");
		this.parentBranch.setDescription("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final Branch aBranch = new Branch();
		BeanUtils.copyProperties(getBranch(), aBranch);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the Branch object with the components data
		doWriteComponentsToBean(aBranch);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aBranch.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aBranch.getRecordType())) {
				aBranch.setVersion(aBranch.getVersion() + 1);
				if (isNew) {
					aBranch.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aBranch.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aBranch.setNewRecord(true);
				}
			}
		} else {
			aBranch.setVersion(aBranch.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		if (aBranch.getBefImage() != null && aBranch.getBefImage().isBranchIsActive() && !aBranch.isBranchIsActive()) {
			String loggedInUsers = getLoggedInUsers();
			if (StringUtils.isNotEmpty(loggedInUsers)) {
				String msg = Labels.getLabel("branch_update_user_validation") + System.lineSeparator() + loggedInUsers;
				Clients.showNotification(msg, "info", null, null, -1, true);
				return;
			}

			if (MessageUtil.confirm(Labels.getLabel("branch_update_postings_info")) != MessageUtil.YES) {
				return;
			}
		}

		// save it to database
		try {

			if (doProcess(aBranch, tranType)) {
				refreshList();
				// Close the Existing Dialog
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
	 * @param aBranch
	 *            (Branch)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(Branch aBranch, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aBranch.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aBranch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aBranch.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aBranch.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aBranch.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aBranch);
				}

				if (isNotesMandatory(taskId, aBranch)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isNotBlank(nextTaskId)) {
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

			aBranch.setTaskId(taskId);
			aBranch.setNextTaskId(nextTaskId);
			aBranch.setRoleCode(getRole());
			aBranch.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aBranch, tranType);

			String operationRefs = getServiceOperations(taskId, aBranch);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aBranch, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aBranch, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
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
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		Branch aBranch = (Branch) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getBranchService().delete(auditHeader);

						deleteNotes = true;
					} else {
						auditHeader = getBranchService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getBranchService().doApprove(auditHeader);

						if (aBranch.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getBranchService().doReject(auditHeader);
						if (aBranch.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_BranchDialog, auditHeader);
						logger.debug("Leaving");
						return processCompleted;
					}
				}

				retValue = ErrorControl.showErrorControl(this.window_BranchDialog, auditHeader);

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.branch), true);
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
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	public void onFulfill$branchCity(Event event) {
		logger.debug("Entering");
		Object dataObject = branchCity.getObject();
		if (dataObject instanceof String) {
			this.branchCity.setValue("","");
			this.pinCode.setFilters(null);
		} else {
			City details = (City) dataObject;

			if (details != null) {
				this.branchCity.setValue(details.getPCCity());
				this.branchCity.setDescription(details.getPCCityName());

				Filter[] filterPin = new Filter[1];
				filterPin[0] = new Filter("City", details.getPCCity(), Filter.OP_EQUAL);
				this.pinCode.setFilters(filterPin);

			} else {
				if(getBranch().isNew()){
				this.pinCode.setValue("", "");
				this.pinCode.setFilters(null);
				}
			}
		}

		logger.debug("Leaving");
	}
	
	
	public void onFulfill$branchProvince(Event event) {
		logger.debug("Entering");
		Object dataObject = branchProvince.getObject();
		if (dataObject instanceof String) {
			this.branchCity.setValue("", "");
			this.pinCode.setValue("", "");
			this.branchCity.setFilters(null);
			this.branchProvince.setFilters(null);
			this.pinCode.setFilters(null);
		}else{
			Province details = (Province) dataObject;
			if (details != null) {
				this.branchProvince.setValue(details.getCPProvince(), details.getCPProvinceName());

				Filter[] filterProvince = new Filter[1];
				filterProvince[0] = new Filter("PCProvince", details.getCPProvince(), Filter.OP_EQUAL);
				if(this.branchCity.getFilters()==null){				
					this.branchCity.setFilters(filterProvince);
				}

			} else {
				if(getBranch().isNew()){					
					this.branchCity.setValue("", "");
					this.branchCity.setFilters(null);
					this.branchProvince.setFilters(null);
					this.pinCode.setValue("", "");
					this.pinCode.setFilters(null);
				}
			}
		}
		

		logger.debug("Leaving");
	}
	
	
	public void onFulfill$pinCode(Event event) {
		logger.debug("Entering");

		Object dataObject = pinCode.getObject();

		if (dataObject instanceof String) {
			this.pinCode.setValue("", "");
			this.branchCity.setValue("", "");
			this.branchProvince.setValue("", "");
			this.branchCity.setFilters(null);
			this.branchProvince.setFilters(null);
			this.branchCountry.setValue("","");
			this.pinCode.setFilters(null);
		} else {
			PinCode details = (PinCode) dataObject;
			if (details != null) {

				this.branchCity.setValue(details.getCity());
				this.branchCity.setDescription(details.getPCCityName());
				Filter[] filtersCity = new Filter[1];
				filtersCity[0] = new Filter("PCCity", details.getCity(), Filter.OP_EQUAL);
				this.branchCity.setFilters(filtersCity);

				this.branchProvince.setValue(details.getPCProvince());
				this.branchProvince.setDescription(details.getLovDescPCProvinceName());
				Filter[] filtersProvince = new Filter[1];
				filtersProvince[0] = new Filter("CPProvince", details.getPCProvince(), Filter.OP_EQUAL);
				this.branchProvince.setFilters(filtersProvince);

				this.branchCountry.setValue(details.getpCCountry());
				this.branchCountry.setDescription(details.getLovDescPCCountryName());

			} else {
				if(getBranch().isNew()){
				this.pinCode.setValue("", "");
				this.branchCity.setValue("", "");
				this.branchProvince.setValue("", "");
				this.branchCity.setFilters(null);
				this.branchProvince.setFilters(null);
				this.branchCountry.setValue("","");
				this.pinCode.setFilters(null);
				}
			}
			logger.debug("Leaving");
		}

	}

	public void onCheck$miniBranch(Event event) {
		logger.debug("Entering" + event.toString());
		if (this.miniBranch.isChecked()) {
			this.parentBranch.setMandatoryStyle(true);
			readOnlyComponent(isReadOnly("BranchDialog_ParentBranch"), this.parentBranch);
		} else {
			this.parentBranch.setMandatoryStyle(false);
			readOnlyComponent(true, this.parentBranch);
			this.parentBranch.setValue("");
			this.parentBranch.setDescription("");

		}
		logger.debug("Leaving" + event.toString());
	}

	
	
	public void onFulfill$parentBranch(Event event) {
		logger.debug("Entering");

		Object dataObject = parentBranch.getObject();

		if (dataObject instanceof String) {
			this.parentBranch.setValue(dataObject.toString());

		} else {
			Branch details = (Branch) dataObject;

			if (details != null) {
				this.parentBranch.setAttribute("branchCode", details.getBranchCode());
				this.parentBranch.setValue(details.getBranchCode());
				this.parentBranch.setDescription(details.getBranchDesc());
			}
		}

		logger.debug("Leaving");

	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aBranch
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(Branch aBranch, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aBranch.getBefImage(), aBranch);
		return new AuditHeader(String.valueOf(aBranch.getId()), null, null, null, auditDetail, aBranch.getUserDetails(),
				getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 *
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_BranchDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
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
		doShowNotes(this.branch);
	}

	/**
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onCheck$branchIsActive(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSetNewBranchProp();
		logger.debug("Leaving" + event.toString());
	}

	private String getLoggedInUsers() {
		StringBuilder builder = new StringBuilder();
		List<User> users = SessionUtil.getLoggedInUsers();
		SecurityUser secUser = null;
		if (!users.isEmpty()) {
			for (User user : users) {
				if (user.getUserId() != getUserWorkspace().getLoggedInUser().getUserId()) {
					if (builder.length() > 0) {
						builder.append("</br>");
					}
					secUser = user.getSecurityUser();
					builder.append("&bull;").append("&nbsp;").append(user.getUserId()).append("&ndash;")
							.append(secUser.getUsrFName() + " " + StringUtils.trimToEmpty(secUser.getUsrMName()) + " "
									+ secUser.getUsrLName());
				}
			}
		}
		return builder.toString();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.branch.getBranchCode());
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

	public Branch getBranch() {
		return this.branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}

	public BranchService getBranchService() {
		return this.branchService;
	}

	public void setBranchListCtrl(BranchListCtrl branchListCtrl) {
		this.branchListCtrl = branchListCtrl;
	}

	public BranchListCtrl getBranchListCtrl() {
		return this.branchListCtrl;
	}

}

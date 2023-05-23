/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : LegalPropertyDetailDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 16-06-2018 * *
 * Modified Date : 16-06-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 16-06-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.legal.legalpropertydetail;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.legal.LegalApplicantDetail;
import com.pennant.backend.model.legal.LegalPropertyDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.legal.legaldetail.LegalDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Legal/LegalPropertyDetail/legalPropertyDetailDialog.zul file.
 * <br>
 */
public class LegalPropertyDetailDialogCtrl extends GFCBaseCtrl<LegalPropertyDetail> {

	private static final String NOT_REQUIRED = ",NQ,";
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(LegalPropertyDetailDialogCtrl.class);

	protected Window window_LegalPropertyDetailDialog;
	protected Combobox scheduleType;
	protected Textbox propertySchedule;
	protected Combobox propertyType;
	protected Textbox northBy;
	protected Textbox southBy;
	protected Textbox eastBy;
	protected Textbox westBy;
	protected Decimalbox measurement;
	protected Textbox registrationOffice;
	protected Textbox registrationDistrict;
	protected Combobox propertyOwner;
	private LegalPropertyDetail legalPropertyDetail;

	private List<ValueLabel> listScheduleType = PennantStaticListUtil.getScheduleTypes();
	private List<ValueLabel> listPropertyType = PennantStaticListUtil.getLegalPropertyTypes();
	private List<ValueLabel> listPropertyOwners;

	private boolean enquiry = false;
	private boolean newRecord = false;
	private boolean newLegalPropertyDetails = false;
	private LegalDetailDialogCtrl legalDetailDialogCtrl;

	private List<LegalPropertyDetail> legalPropertyDetailList;

	protected Groupbox gb_MiscellaneousPropertyDetails;
	protected Combobox isUrbanLandCeiling;
	protected Combobox isMinorshareInvolved;
	protected Combobox isPropertyIsGramanatham;
	protected Combobox isPropertyReleased;
	protected Combobox isPropOriginalsAvailable;
	protected Combobox isPropertyIsAgricultural;
	protected Combobox isNOCObtainedFromLPA;
	protected Combobox isAnyMortgagePending;

	protected Groupbox gb_PropertyMeasurementDetails;
	protected Textbox northSideEastByWest;
	protected Textbox southSideWestByEast;
	protected Textbox eastSideNorthBySouth;
	protected Textbox westSideSouthByNorth;

	private List<ValueLabel> yesNoList = PennantStaticListUtil.getYesNo();

	/**
	 * default constructor.<br>
	 * `
	 */
	public LegalPropertyDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "LegalPropertyDetailDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.legalPropertyDetail.getLegalPropertyId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_LegalPropertyDetailDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_LegalPropertyDetailDialog);

		try {
			// Get the required arguments.
			this.legalPropertyDetail = (LegalPropertyDetail) arguments.get("legalPropertyDetail");

			if (this.legalPropertyDetail == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			this.setLegalDetailDialogCtrl((LegalDetailDialogCtrl) arguments.get("legalDetailDialogCtrl"));
			setNewLegalPropertyDetails(true);
			if (arguments.containsKey("newRecord")) {
				setNewRecord(true);
			} else {
				setNewRecord(false);
			}
			this.legalPropertyDetail.setWorkflowId(0);
			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			if (arguments.containsKey("enquiry")) {
				setEnquiry((boolean) arguments.get("enquiry"));
			}

			// Store the before image.
			LegalPropertyDetail legalPropertyDetail = new LegalPropertyDetail();
			BeanUtils.copyProperties(this.legalPropertyDetail, legalPropertyDetail);
			this.legalPropertyDetail.setBefImage(legalPropertyDetail);

			// Render the page and display the data.
			doLoadWorkFlow(this.legalPropertyDetail.isWorkflow(), this.legalPropertyDetail.getWorkflowId(),
					this.legalPropertyDetail.getNextTaskId());

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.legalPropertyDetail);
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

		this.propertySchedule.setMaxlength(3000);
		this.northBy.setMaxlength(100);
		this.southBy.setMaxlength(100);
		this.eastBy.setMaxlength(100);
		this.westBy.setMaxlength(100);

		this.measurement.setMaxlength(18);
		this.measurement.setFormat(PennantConstants.rateFormate3);
		this.measurement.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.measurement.setScale(3);

		this.registrationOffice.setMaxlength(200);
		this.registrationDistrict.setMaxlength(100);
		this.propertyOwner.setMaxlength(1000);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		if (!isEnquiry()) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_LegalPropertyDetailDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_LegalPropertyDetailDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_LegalPropertyDetailDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_LegalPropertyDetailDialog_btnSave"));
			this.btnCancel.setVisible(false);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.legalPropertyDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.legalPropertyDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param legalPropertyDetail
	 * 
	 */
	public void doWriteBeanToComponents(LegalPropertyDetail aLegalPropertyDetail) {
		logger.debug(Literal.ENTERING);

		listPropertyOwners = prepareOwnersNamesList(aLegalPropertyDetail);

		fillComboBox(this.scheduleType, aLegalPropertyDetail.getScheduleType(), listScheduleType, "");
		this.propertySchedule.setValue(aLegalPropertyDetail.getPropertySchedule());
		fillComboBox(this.propertyType, aLegalPropertyDetail.getPropertyType(), listPropertyType, "");
		this.northBy.setValue(aLegalPropertyDetail.getNorthBy());
		this.southBy.setValue(aLegalPropertyDetail.getSouthBy());
		this.eastBy.setValue(aLegalPropertyDetail.getEastBy());
		this.westBy.setValue(aLegalPropertyDetail.getWestBy());

		this.measurement.setValue(aLegalPropertyDetail.getMeasurement());
		this.registrationOffice.setValue(aLegalPropertyDetail.getRegistrationOffice());
		this.registrationDistrict.setValue(aLegalPropertyDetail.getRegistrationDistrict());
		fillEditableComboBox(this.propertyOwner, aLegalPropertyDetail.getPropertyOwner(), listPropertyOwners, "");

		fillComboBox(this.isUrbanLandCeiling, aLegalPropertyDetail.getUrbanLandCeiling(), yesNoList, NOT_REQUIRED);
		fillComboBox(this.isMinorshareInvolved, aLegalPropertyDetail.getMinorshareInvolved(), yesNoList, NOT_REQUIRED);
		fillComboBox(this.isPropertyIsGramanatham, aLegalPropertyDetail.getPropertyIsGramanatham(), yesNoList,
				NOT_REQUIRED);
		fillComboBox(this.isPropertyReleased, aLegalPropertyDetail.getPropertyReleased(), yesNoList, NOT_REQUIRED);
		fillComboBox(this.isPropOriginalsAvailable, aLegalPropertyDetail.getPropOriginalsAvailable(), yesNoList,
				NOT_REQUIRED);
		fillComboBox(this.isPropertyIsAgricultural, aLegalPropertyDetail.getPropertyIsAgricultural(), yesNoList,
				NOT_REQUIRED);
		fillComboBox(this.isNOCObtainedFromLPA, aLegalPropertyDetail.getNocObtainedFromLPA(), yesNoList, NOT_REQUIRED);
		fillComboBox(this.isAnyMortgagePending, aLegalPropertyDetail.getAnyMortgagePending(), yesNoList, NOT_REQUIRED);

		this.northSideEastByWest.setValue(aLegalPropertyDetail.getNorthSideEastByWest());
		this.southSideWestByEast.setValue(aLegalPropertyDetail.getSouthSideWestByEast());
		this.eastSideNorthBySouth.setValue(aLegalPropertyDetail.getEastSideNorthBySouth());
		this.westSideSouthByNorth.setValue(aLegalPropertyDetail.getWestSideSouthByNorth());

		logger.debug(Literal.LEAVING);
	}

	private List<ValueLabel> prepareOwnersNamesList(LegalPropertyDetail aLegalPropertyDetail) {
		List<ValueLabel> valueLableList = new ArrayList<>();
		List<LegalApplicantDetail> applicantsList = getLegalDetailDialogCtrl().getApplicantDetailList();

		boolean added = false;
		if (CollectionUtils.isNotEmpty(applicantsList)) {
			for (LegalApplicantDetail detail : applicantsList) {
				if (detail.getPropertyOwnersName().equals(aLegalPropertyDetail.getPropertyOwner())) {
					added = true;
				}
				valueLableList.add(new ValueLabel(detail.getPropertyOwnersName(), detail.getPropertyOwnersName()));
			}
		}

		if (StringUtils.trimToNull(aLegalPropertyDetail.getPropertyOwner()) != null && !added) {
			valueLableList.add(
					new ValueLabel(aLegalPropertyDetail.getPropertyOwner(), aLegalPropertyDetail.getPropertyOwner()));
		}
		return valueLableList;
	}

	/**
	 * Method to fill the Editable combo box with given list of values
	 * 
	 * @param combobox
	 * @param value
	 * @param list
	 */
	public void fillEditableComboBox(Combobox combobox, String value, List<ValueLabel> list, String excludeFields) {
		logger.debug(Literal.ENTERING);

		combobox.getChildren().clear();
		Comboitem comboitem = null;
		for (ValueLabel valueLabel : list) {
			if (!excludeFields.contains("," + valueLabel.getValue() + ",")) {
				comboitem = new Comboitem();
				comboitem.setValue(valueLabel.getValue());
				comboitem.setLabel(valueLabel.getLabel());
				combobox.appendChild(comboitem);
			}
			if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(valueLabel.getValue()))) {
				combobox.setSelectedItem(comboitem);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aLegalPropertyDetail
	 */
	public void doWriteComponentsToBean(LegalPropertyDetail aLegalPropertyDetail) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Schedule Type
		try {
			aLegalPropertyDetail.setScheduleType(this.scheduleType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Property Schedule
		try {
			aLegalPropertyDetail.setPropertySchedule(this.propertySchedule.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Property Type
		try {
			aLegalPropertyDetail.setPropertyType(this.propertyType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// North By
		try {
			aLegalPropertyDetail.setNorthBy(this.northBy.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// South By
		try {
			aLegalPropertyDetail.setSouthBy(this.southBy.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// East By
		try {
			aLegalPropertyDetail.setEastBy(this.eastBy.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// West By
		try {
			aLegalPropertyDetail.setWestBy(this.westBy.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Measurement
		try {
			aLegalPropertyDetail.setMeasurement(this.measurement.getValue());
			if (aLegalPropertyDetail.getMeasurement() == null) {
				aLegalPropertyDetail.setMeasurement(BigDecimal.ZERO);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Registration Office
		try {
			aLegalPropertyDetail.setRegistrationOffice(this.registrationOffice.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Registration District
		try {
			aLegalPropertyDetail.setRegistrationDistrict(this.registrationDistrict.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Property Owner
		try {
			aLegalPropertyDetail.setPropertyOwner(this.propertyOwner.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// isUrbanLandCeiling
		try {
			String strUrbanLandCeiling = null;
			aLegalPropertyDetail.setUrbanLandCeiling(this.isUrbanLandCeiling.getSelectedItem().getValue().toString());
			if (this.isUrbanLandCeiling.getSelectedItem() != null) {
				strUrbanLandCeiling = this.isUrbanLandCeiling.getSelectedItem().getValue().toString();
			}
			if (strUrbanLandCeiling != null && !PennantConstants.List_Select.equals(strUrbanLandCeiling)) {
				aLegalPropertyDetail.setUrbanLandCeiling(strUrbanLandCeiling);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Minor Share Involved
		try {
			String strMinorshareInvolved = null;
			if (this.isMinorshareInvolved.getSelectedItem() != null) {
				strMinorshareInvolved = this.isMinorshareInvolved.getSelectedItem().getValue().toString();
			}
			if (strMinorshareInvolved != null && !PennantConstants.List_Select.equals(strMinorshareInvolved)) {
				aLegalPropertyDetail.setMinorshareInvolved(strMinorshareInvolved);
			} else {
				aLegalPropertyDetail.setMinorshareInvolved(null);
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Property Is Gramanatham
		try {
			String strPropertyIsGramanatham = null;
			if (this.isPropertyIsGramanatham.getSelectedItem() != null) {
				strPropertyIsGramanatham = this.isPropertyIsGramanatham.getSelectedItem().getValue().toString();
			}
			if (strPropertyIsGramanatham != null && !PennantConstants.List_Select.equals(strPropertyIsGramanatham)) {
				aLegalPropertyDetail.setPropertyIsGramanatham(strPropertyIsGramanatham);
			} else {
				aLegalPropertyDetail.setPropertyIsGramanatham(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Property Is Released
		try {
			String strPropertyReleased = null;
			if (this.isPropertyReleased.getSelectedItem() != null) {
				strPropertyReleased = this.isPropertyReleased.getSelectedItem().getValue().toString();
			}
			if (strPropertyReleased != null && !PennantConstants.List_Select.equals(strPropertyReleased)) {
				aLegalPropertyDetail.setPropertyReleased(strPropertyReleased);
			} else {
				aLegalPropertyDetail.setPropertyReleased(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Property Originals Available
		try {
			String strPropOriginalsAvailable = null;
			if (this.isPropOriginalsAvailable.getSelectedItem() != null) {
				strPropOriginalsAvailable = this.isPropOriginalsAvailable.getSelectedItem().getValue().toString();
			}
			if (strPropOriginalsAvailable != null && !PennantConstants.List_Select.equals(strPropOriginalsAvailable)) {
				aLegalPropertyDetail.setPropOriginalsAvailable(strPropOriginalsAvailable);
			} else {
				aLegalPropertyDetail.setPropOriginalsAvailable(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Property Is Agricultural
		try {
			String strPropertyIsAgricultural = null;
			if (this.isPropertyIsAgricultural.getSelectedItem() != null) {
				strPropertyIsAgricultural = this.isPropertyIsAgricultural.getSelectedItem().getValue().toString();
			}
			if (strPropertyIsAgricultural != null && !PennantConstants.List_Select.equals(strPropertyIsAgricultural)) {
				aLegalPropertyDetail.setPropertyIsAgricultural(strPropertyIsAgricultural);
			} else {
				aLegalPropertyDetail.setPropertyIsAgricultural(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// NOC Obtained From LPA
		try {
			String strNOCObtainedFromLPA = null;
			if (this.isNOCObtainedFromLPA.getSelectedItem() != null) {
				strNOCObtainedFromLPA = this.isNOCObtainedFromLPA.getSelectedItem().getValue().toString();
			}
			if (strNOCObtainedFromLPA != null && !PennantConstants.List_Select.equals(strNOCObtainedFromLPA)) {
				aLegalPropertyDetail.setNocObtainedFromLPA(strNOCObtainedFromLPA);
			} else {
				aLegalPropertyDetail.setNocObtainedFromLPA(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			String strAnyMortgagePending = null;
			if (this.isAnyMortgagePending.getSelectedItem() != null) {
				strAnyMortgagePending = this.isAnyMortgagePending.getSelectedItem().getValue().toString();
			}
			if (strAnyMortgagePending != null && !PennantConstants.List_Select.equals(strAnyMortgagePending)) {
				aLegalPropertyDetail.setAnyMortgagePending(strAnyMortgagePending);
			} else {
				aLegalPropertyDetail.setAnyMortgagePending(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aLegalPropertyDetail.setNorthSideEastByWest(this.northSideEastByWest.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLegalPropertyDetail.setWestSideSouthByNorth(this.westSideSouthByNorth.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLegalPropertyDetail.setSouthSideWestByEast(this.southSideWestByEast.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aLegalPropertyDetail.setEastSideNorthBySouth(this.eastSideNorthBySouth.getValue());
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
	 * @param legalPropertyDetail The entity that need to be render.
	 */
	public void doShowDialog(LegalPropertyDetail legalPropertyDetail) {
		logger.debug(Literal.LEAVING);

		if (legalPropertyDetail.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.scheduleType.focus();
		} else {
			if (isNewLegalPropertyDetails()) {
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
			doWriteBeanToComponents(legalPropertyDetail);
			if (isNewLegalPropertyDetails()) {
				this.groupboxWf.setVisible(false);
			}
			if (isEnquiry()) {
				this.btnCtrl.setBtnStatus_Enquiry();
				this.btnNotes.setVisible(false);
				doReadOnly();
			}
			setDialog(DialogType.MODAL);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.scheduleType.isReadonly()) {
			this.scheduleType.setConstraint(new StaticListValidator(listScheduleType,
					Labels.getLabel("label_LegalPropertyDetailDialog_ScheduleType.value")));
		}
		if (!this.propertySchedule.isReadonly()) {
			this.propertySchedule.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LegalPropertyDetailDialog_PropertySchedule.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}
		if (!this.propertyType.isReadonly()) {
			this.propertyType.setConstraint(new StaticListValidator(listPropertyType,
					Labels.getLabel("label_LegalPropertyDetailDialog_PropertyType.value")));
		}
		if (!this.northBy.isReadonly()) {
			this.northBy.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LegalPropertyDetailDialog_NorthBy.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}
		if (!this.southBy.isReadonly()) {
			this.southBy.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LegalPropertyDetailDialog_SouthBy.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}
		if (!this.eastBy.isReadonly()) {
			this.eastBy.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LegalPropertyDetailDialog_EastBy.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}
		if (!this.westBy.isReadonly()) {
			this.westBy.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LegalPropertyDetailDialog_WestBy.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}
		if (!this.measurement.isReadonly()) {
			this.measurement.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_LegalPropertyDetailDialog_Measurement.value"), 3, false, false));
		}
		if (!this.registrationOffice.isReadonly()) {
			this.registrationOffice.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LegalPropertyDetailDialog_RegistrationOffice.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.registrationDistrict.isReadonly()) {
			this.registrationDistrict.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LegalPropertyDetailDialog_RegistrationDistrict.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.registrationDistrict.isReadonly()) {
			this.registrationDistrict.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LegalPropertyDetailDialog_RegistrationDistrict.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.isUrbanLandCeiling.isDisabled()) {
			this.isUrbanLandCeiling.setConstraint(new PTListValidator<ValueLabel>(
					Labels.getLabel("label_LegalPropertyDetailDialog_IsUrbanLandCeiling.value"), yesNoList, true));
		}
		if (!this.isMinorshareInvolved.isDisabled()) {
			this.isMinorshareInvolved.setConstraint(new PTListValidator<ValueLabel>(
					Labels.getLabel("label_LegalPropertyDetailDialog_IsMinorshareInvolved.value"), yesNoList, true));
		}
		if (!this.isPropertyIsGramanatham.isDisabled()) {
			this.isPropertyIsGramanatham.setConstraint(new PTListValidator<ValueLabel>(
					Labels.getLabel("label_LegalPropertyDetailDialog_IsPropertyIsGramanatham.value"), yesNoList, true));
		}

		if (!this.isPropertyReleased.isDisabled()) {
			this.isPropertyReleased.setConstraint(new PTListValidator<ValueLabel>(
					Labels.getLabel("label_LegalPropertyDetailDialog_IsPropertyReleased.value"), yesNoList, true));
		}
		if (!this.isPropOriginalsAvailable.isDisabled()) {
			this.isPropOriginalsAvailable.setConstraint(new PTListValidator<ValueLabel>(
					Labels.getLabel("label_LegalPropertyDetailDialog_IsPropertyOriginalsAvailable.value"), yesNoList,
					true));
		}
		if (!this.isPropertyIsAgricultural.isDisabled()) {
			this.isPropertyIsAgricultural.setConstraint(new PTListValidator<ValueLabel>(
					Labels.getLabel("label_LegalPropertyDetailDialog_IsPropertyIsAgricultural.value"), yesNoList,
					true));
		}
		if (!this.isNOCObtainedFromLPA.isDisabled()) {
			this.isNOCObtainedFromLPA.setConstraint(new PTListValidator<ValueLabel>(
					Labels.getLabel("label_LegalPropertyDetailDialog_IsNOCobtainedfromLPA.value"), yesNoList, true));
		}
		if (!this.isAnyMortgagePending.isDisabled()) {
			this.isAnyMortgagePending.setConstraint(new PTListValidator<ValueLabel>(
					Labels.getLabel("label_LegalPropertyDetailDialog_IsAnyMortgagePending.value"), yesNoList, true));
		}

		if (!this.northSideEastByWest.isReadonly()) {
			this.northSideEastByWest.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LegalPropertyDetailDialog_NorthSideEastByWest.value"),
							PennantRegularExpressions.REGEX_ALPHA_NUMERIC_DOT_SPACE, true));
		}
		if (!this.westSideSouthByNorth.isReadonly()) {
			this.westSideSouthByNorth.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LegalPropertyDetailDialog_WestSideSouthByNorth.value"),
							PennantRegularExpressions.REGEX_ALPHA_NUMERIC_DOT_SPACE, true));
		}
		if (!this.eastSideNorthBySouth.isReadonly()) {
			this.eastSideNorthBySouth.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LegalPropertyDetailDialog_EastSideNorthBySouth.value"),
							PennantRegularExpressions.REGEX_ALPHA_NUMERIC_DOT_SPACE, true));
		}
		if (!this.southSideWestByEast.isReadonly()) {
			this.southSideWestByEast.setConstraint(
					new PTStringValidator(Labels.getLabel("label_LegalPropertyDetailDialog_SouthSideWestByEast.value"),
							PennantRegularExpressions.REGEX_ALPHA_NUMERIC_DOT_SPACE, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.scheduleType.setConstraint("");
		this.propertySchedule.setConstraint("");
		this.propertyType.setConstraint("");
		this.northBy.setConstraint("");
		this.southBy.setConstraint("");
		this.eastBy.setConstraint("");
		this.westBy.setConstraint("");
		this.measurement.setConstraint("");
		this.registrationOffice.setConstraint("");
		this.registrationDistrict.setConstraint("");
		this.propertyOwner.setConstraint("");

		this.isUrbanLandCeiling.setConstraint("");
		this.isMinorshareInvolved.setConstraint("");
		this.isAnyMortgagePending.setConstraint("");
		this.isNOCObtainedFromLPA.setConstraint("");
		this.isPropertyIsAgricultural.setConstraint("");
		this.isPropertyIsGramanatham.setConstraint("");
		this.isPropOriginalsAvailable.setConstraint("");
		this.isPropertyReleased.setConstraint("");

		this.northSideEastByWest.setConstraint("");
		this.southSideWestByEast.setConstraint("");
		this.eastSideNorthBySouth.setConstraint("");
		this.westSideSouthByNorth.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

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

	protected boolean doCustomDelete(final LegalPropertyDetail aLegalPropertyDetail, String tranType) {
		if (isNewLegalPropertyDetails()) {
			tranType = PennantConstants.TRAN_DEL;
			AuditHeader auditHeader = processDetails(aLegalPropertyDetail, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_LegalPropertyDetailDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				if (getLegalDetailDialogCtrl() != null) {
					getLegalDetailDialogCtrl().doFillPropertyDetails(this.legalPropertyDetailList);
				}
				return true;
			}
		}
		return false;
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final LegalPropertyDetail aLegalPropertyDetail = new LegalPropertyDetail();
		BeanUtils.copyProperties(this.legalPropertyDetail, aLegalPropertyDetail);

		doDelete(aLegalPropertyDetail.getPropertyType(), aLegalPropertyDetail);

		logger.debug(Literal.LEAVING);

	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_LegalPropertyDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */
	private AuditHeader processDetails(LegalPropertyDetail aLegalPropertyDetail, String tranType) {
		boolean recordAdded = false;
		boolean duplicateRecord = false;

		AuditHeader auditHeader = getAuditHeader(aLegalPropertyDetail, tranType);
		this.legalPropertyDetailList = new ArrayList<>();

		List<LegalPropertyDetail> oldLegalProipertytDetailsList = null;

		if (getLegalDetailDialogCtrl() != null) {
			oldLegalProipertytDetailsList = getLegalDetailDialogCtrl().getLegalPropertyDetailList();
		}

		if (oldLegalProipertytDetailsList != null && !oldLegalProipertytDetailsList.isEmpty()) {
			for (LegalPropertyDetail oldDeatils : oldLegalProipertytDetailsList) {

				if (oldDeatils.getSeqNum() == aLegalPropertyDetail.getSeqNum()) {
					duplicateRecord = true;
				}

				if (duplicateRecord) {
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aLegalPropertyDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aLegalPropertyDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							this.legalPropertyDetailList.add(aLegalPropertyDetail);
						} else if (aLegalPropertyDetail.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aLegalPropertyDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aLegalPropertyDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							this.legalPropertyDetailList.add(aLegalPropertyDetail);
						} else if (aLegalPropertyDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							this.legalPropertyDetailList.add(oldDeatils);
						}
					}
				} else {
					this.legalPropertyDetailList.add(oldDeatils);
				}
				duplicateRecord = false;
			}
		}
		if (PennantConstants.TRAN_UPD.equals(tranType)) {
			this.legalPropertyDetailList.add(aLegalPropertyDetail);
			recordAdded = true;
		}
		if (!recordAdded) {
			this.legalPropertyDetailList.add(aLegalPropertyDetail);
		}
		return auditHeader;
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.legalPropertyDetail.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}
		readOnlyComponent(isReadOnly("LegalPropertyDetailDialog_ScheduleType"), this.scheduleType);
		readOnlyComponent(isReadOnly("LegalPropertyDetailDialog_PropertySchedule"), this.propertySchedule);
		readOnlyComponent(isReadOnly("LegalPropertyDetailDialog_PropertyType"), this.propertyType);
		readOnlyComponent(isReadOnly("LegalPropertyDetailDialog_NorthBy"), this.northBy);
		readOnlyComponent(isReadOnly("LegalPropertyDetailDialog_SouthBy"), this.southBy);
		readOnlyComponent(isReadOnly("LegalPropertyDetailDialog_EastBy"), this.eastBy);
		readOnlyComponent(isReadOnly("LegalPropertyDetailDialog_WestBy"), this.westBy);
		readOnlyComponent(isReadOnly("LegalPropertyDetailDialog_Measurement"), this.measurement);
		readOnlyComponent(isReadOnly("LegalPropertyDetailDialog_RegistrationOffice"), this.registrationOffice);
		readOnlyComponent(isReadOnly("LegalPropertyDetailDialog_RegistrationDistrict"), this.registrationDistrict);
		readOnlyPropertyOwnerComponent(isReadOnly("LegalPropertyDetailDialog_PropertyOwner"), this.propertyOwner);

		// Property Misc details
		this.gb_MiscellaneousPropertyDetails
				.setVisible(getUserWorkspace().isAllowed("LegalPropertyDetailDialog_gb_miscellaneousPropertyDetails"));
		readOnlyComponent(isReadOnly("LegalPropertyDetailDialog_IsUrbanLandCeiling"), this.isUrbanLandCeiling);
		readOnlyComponent(isReadOnly("LegalPropertyDetailDialog_IsMinorshareInvolved"), this.isMinorshareInvolved);
		readOnlyComponent(isReadOnly("LegalPropertyDetailDialog_IsPropertyIsGramanatham"),
				this.isPropertyIsGramanatham);
		readOnlyComponent(isReadOnly("LegalPropertyDetailDialog_IsPropertyReleased"), this.isPropertyReleased);
		readOnlyComponent(isReadOnly("LegalPropertyDetailDialog_IsPropertyOriginalsAvailable"),
				this.isPropOriginalsAvailable);
		readOnlyComponent(isReadOnly("LegalPropertyDetailDialog_IsPropertyIsAgricultural"),
				this.isPropertyIsAgricultural);
		readOnlyComponent(isReadOnly("LegalPropertyDetailDialog_IsNOCobtainedfromLPA"), this.isNOCObtainedFromLPA);
		readOnlyComponent(isReadOnly("LegalPropertyDetailDialog_IsAnyMortgagePending"), this.isAnyMortgagePending);

		// Property Measurements
		this.gb_PropertyMeasurementDetails
				.setVisible(getUserWorkspace().isAllowed("LegalDocumentDialog_gb_PropertyMeasurementDetails"));
		readOnlyComponent(isReadOnly("LegalPropertyDetailDialog_NorthSideEastByWest"), this.northSideEastByWest);
		readOnlyComponent(isReadOnly("LegalPropertyDetailDialog_SouthSideWestByEast"), this.southSideWestByEast);
		readOnlyComponent(isReadOnly("LegalPropertyDetailDialog_EastSideNorthBySouth"), this.eastSideNorthBySouth);
		readOnlyComponent(isReadOnly("LegalPropertyDetailDialog_WestSideSouthByNorth"), this.westSideSouthByNorth);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.legalPropertyDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (isNewLegalPropertyDetails()) {
				if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(isNewLegalPropertyDetails());
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewLegalPropertyDetails()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);

		readOnlyComponent(true, this.scheduleType);
		readOnlyComponent(true, this.propertySchedule);
		readOnlyComponent(true, this.propertyType);
		readOnlyComponent(true, this.northBy);
		readOnlyComponent(true, this.southBy);
		readOnlyComponent(true, this.eastBy);
		readOnlyComponent(true, this.westBy);
		readOnlyComponent(true, this.measurement);
		readOnlyComponent(true, this.registrationOffice);
		readOnlyComponent(true, this.registrationDistrict);
		readOnlyComponent(true, this.propertyOwner);

		readOnlyComponent(true, this.isUrbanLandCeiling);
		readOnlyComponent(true, this.isMinorshareInvolved);
		readOnlyComponent(true, this.isPropertyIsGramanatham);
		readOnlyComponent(true, this.isPropertyReleased);
		readOnlyComponent(true, this.isPropOriginalsAvailable);
		readOnlyComponent(true, this.isPropertyIsAgricultural);
		readOnlyComponent(true, this.isNOCObtainedFromLPA);
		readOnlyComponent(true, this.isAnyMortgagePending);

		readOnlyComponent(true, this.northSideEastByWest);
		readOnlyComponent(true, this.southSideWestByEast);
		readOnlyComponent(true, this.eastSideNorthBySouth);
		readOnlyComponent(true, this.westSideSouthByNorth);

		if (PennantConstants.YES.equals(SysParamUtil.getValueAsString("LEGAL_DETAIL_ADDITIONAL_FIELDS_ENQUIRY"))) {
			this.gb_MiscellaneousPropertyDetails.setVisible(true);
			this.gb_PropertyMeasurementDetails.setVisible(true);
		} else {
			this.gb_MiscellaneousPropertyDetails.setVisible(false);
			this.gb_PropertyMeasurementDetails.setVisible(false);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		this.scheduleType.setSelectedIndex(0);
		this.propertySchedule.setValue("");
		this.propertyType.setSelectedIndex(0);
		this.northBy.setValue("");
		this.southBy.setValue("");
		this.eastBy.setValue("");
		this.westBy.setValue("");
		this.measurement.setValue("");
		this.registrationOffice.setValue("");
		this.registrationDistrict.setValue("");
		this.propertyOwner.setSelectedIndex(0);

		this.isUrbanLandCeiling.setValue("");
		this.isMinorshareInvolved.setValue("");
		this.isPropertyIsGramanatham.setValue("");
		this.isPropertyReleased.setValue("");
		this.isPropOriginalsAvailable.setValue("");
		this.isPropertyIsAgricultural.setValue("");
		this.isNOCObtainedFromLPA.setValue("");
		this.isAnyMortgagePending.setValue("");

		this.northSideEastByWest.setValue("");
		this.southSideWestByEast.setValue("");
		this.eastSideNorthBySouth.setValue("");
		this.westSideSouthByNorth.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final LegalPropertyDetail aLegalPropertyDetail = new LegalPropertyDetail();
		BeanUtils.copyProperties(this.legalPropertyDetail, aLegalPropertyDetail);
		boolean isNew = false;

		doRemoveValidation();
		doRemoveLOVValidation();

		doSetValidation();
		doWriteComponentsToBean(aLegalPropertyDetail);

		isNew = aLegalPropertyDetail.isNewRecord();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aLegalPropertyDetail.getRecordType())) {
				aLegalPropertyDetail.setVersion(aLegalPropertyDetail.getVersion() + 1);
				if (isNew) {
					aLegalPropertyDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aLegalPropertyDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aLegalPropertyDetail.setNewRecord(true);
				}
			}
		} else {
			if (isNewLegalPropertyDetails()) {
				if (isNewRecord()) {
					aLegalPropertyDetail.setVersion(1);
					aLegalPropertyDetail.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
				if (StringUtils.isBlank(aLegalPropertyDetail.getRecordType())) {
					aLegalPropertyDetail.setVersion(aLegalPropertyDetail.getVersion() + 1);
					aLegalPropertyDetail.setRecordType(PennantConstants.RCD_UPD);
					aLegalPropertyDetail.setNewRecord(true);
				}
				if (aLegalPropertyDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aLegalPropertyDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}
			} else {
				aLegalPropertyDetail.setVersion(aLegalPropertyDetail.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}
		// save it to database
		try {
			if (isNewLegalPropertyDetails()) {
				AuditHeader auditHeader = processDetails(aLegalPropertyDetail, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_LegalPropertyDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					if (getLegalDetailDialogCtrl() != null) {
						getLegalDetailDialogCtrl().doFillPropertyDetails(this.legalPropertyDetailList);
					}
					closeDialog();
				}
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		} catch (final Exception e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	public void readOnlyPropertyOwnerComponent(boolean isReadOnly, Component component) {
		if (isReadOnly) {
			if (component instanceof Combobox) {
				((Combobox) component).setTabindex(-1);
				((Combobox) component).setAutodrop(false);
				((Combobox) component).setStyle("#F2F2F2");
				((Combobox) component).setButtonVisible(false);
				((Combobox) component).setAutocomplete(false);
				((Combobox) component).setDisabled(true);
			}
		} else {
			if (component instanceof Combobox) {
				((Combobox) component).setTabindex(0);
				((Combobox) component).setAutodrop(true);
				((Combobox) component).setButtonVisible(true);
				((Combobox) component).setStyle("#F2F2F2");
				((Combobox) component).setDisabled(false);
			}
		}
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(LegalPropertyDetail aLegalPropertyDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aLegalPropertyDetail.getBefImage(),
				aLegalPropertyDetail);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aLegalPropertyDetail.getUserDetails(),
				getOverideMap());
	}

	public LegalPropertyDetail getLegalPropertyDetail() {
		return legalPropertyDetail;
	}

	public void setLegalPropertyDetail(LegalPropertyDetail legalPropertyDetail) {
		this.legalPropertyDetail = legalPropertyDetail;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewLegalPropertyDetails() {
		return newLegalPropertyDetails;
	}

	public void setNewLegalPropertyDetails(boolean newLegalPropertyDetails) {
		this.newLegalPropertyDetails = newLegalPropertyDetails;
	}

	public LegalDetailDialogCtrl getLegalDetailDialogCtrl() {
		return legalDetailDialogCtrl;
	}

	public void setLegalDetailDialogCtrl(LegalDetailDialogCtrl legalDetailDialogCtrl) {
		this.legalDetailDialogCtrl = legalDetailDialogCtrl;
	}

	public boolean isEnquiry() {
		return enquiry;
	}

	public void setEnquiry(boolean enquiry) {
		this.enquiry = enquiry;
	}

}

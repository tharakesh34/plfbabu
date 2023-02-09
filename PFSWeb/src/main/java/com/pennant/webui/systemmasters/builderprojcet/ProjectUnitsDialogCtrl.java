package com.pennant.webui.systemmasters.builderprojcet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.ProjectUnits;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class ProjectUnitsDialogCtrl extends GFCBaseCtrl<ProjectUnits> {
	private static final Logger logger = LogManager.getLogger(ProjectUnitsDialogCtrl.class);

	private static final long serialVersionUID = 1L;
	// Project Units
	protected Window windowProjectUnitsDialog;
	protected Combobox unitType;
	protected Textbox tower;
	protected Textbox floorNumber;
	protected Textbox unitNumber;
	protected Intbox unitArea;
	protected Intbox rate;
	protected CurrencyBox price;
	protected CurrencyBox otherCharges;
	protected CurrencyBox totalPrice;
	protected Decimalbox unitRpsf;
	protected Decimalbox unitPlotArea;
	protected Decimalbox unitSuperBuiltUp;
	protected Space space_tower;
	protected Space space_floornumber;
	protected Combobox unitAreaConsidered;
	protected Decimalbox carpetArea;
	protected Decimalbox unitBuiltUpArea;
	protected Combobox rateConsidered;
	protected Decimalbox rateAsPerCarpetArea;
	protected Decimalbox rateAsPerBuiltUpArea;
	protected Decimalbox rateAsPerSuperBuiltUpArea;
	protected Decimalbox rateAsPerBranchAPF;
	protected Decimalbox rateAsPerCostSheet;
	protected CurrencyBox floorRiseCharges;
	protected CurrencyBox openCarParkingCharges;
	protected CurrencyBox closedCarParkingCharges;
	protected CurrencyBox gst;
	protected Textbox remarks;
	protected Space space_carpetArea;
	protected Space space_unitBuiltUpArea;
	protected Space space_unitSuperBuiltUp;
	protected Space space_rateAsPerCarpetArea;
	protected Space space_rateAsPerBuiltUpArea;
	protected Space space_rateAsPerSuperBuiltUpArea;
	protected Space space_rateAsPerBranchAPF;
	protected Space space_rateAsPerCostSheet;
	protected Space space_unitRpsf;
	private ProjectUnits projectUnits;
	private static List<ValueLabel> unitTypesList = PennantStaticListUtil.getUnitTypes();
	private static List<ValueLabel> unitAreaConsideredList = PennantStaticListUtil.getUnitAreaConsidered();
	private static List<ValueLabel> rateConsideredList = PennantStaticListUtil.geRateConsidered();

	private BuilderProjcetDialogCtrl builderProjcetDialogCtrl;
	private List<ProjectUnits> projectUnitsList;
	private boolean newRecord = false;
	private boolean fromParent = false;
	private int finFormatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
	private String roleCode;

	/**
	 * default constructor.<br>
	 */
	public ProjectUnitsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ProjectUnitsDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.projectUnits.getId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$windowProjectUnitsDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(windowProjectUnitsDialog);

		try {
			// Get the required arguments.
			this.projectUnits = (ProjectUnits) arguments.get("projectUnits");

			if (this.projectUnits == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			if (arguments.containsKey("builderProjectDialogCtrl")) {
				this.builderProjcetDialogCtrl = (BuilderProjcetDialogCtrl) arguments.get("builderProjectDialogCtrl");
				setFromParent(true);
			}
			if (arguments.containsKey("roleCode")) {
				this.roleCode = (String) arguments.get("roleCode");
			}

			// Store the before image.
			ProjectUnits projectUnits = new ProjectUnits();
			BeanUtils.copyProperties(this.projectUnits, projectUnits);
			this.projectUnits.setBefImage(projectUnits);

			if (projectUnits.isNewRecord()) {
				setNewRecord(projectUnits.isNewRecord());
			}
			// Render the page and display the data.
			doLoadWorkFlow(this.projectUnits.isWorkflow(), this.projectUnits.getWorkflowId(),
					this.projectUnits.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				getUserWorkspace().allocateRoleAuthorities(this.roleCode, this.pageRightName);
			} else if (!enqiryModule) {
				getUserWorkspace().allocateRoleAuthorities(this.roleCode, this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.projectUnits);
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
		this.space_tower.setVisible(false);
		this.space_floornumber.setVisible(false);
		this.price.setDisabled(true);
		this.totalPrice.setDisabled(true);
		this.price.setProperties(false, finFormatter);
		this.otherCharges.setProperties(false, finFormatter);
		this.floorRiseCharges.setProperties(false, finFormatter);
		this.openCarParkingCharges.setProperties(false, finFormatter);
		this.closedCarParkingCharges.setProperties(false, finFormatter);
		this.gst.setProperties(false, finFormatter);
		this.remarks.setMaxlength(500);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		if (!enqiryModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ProjectUnitsDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ProjectUnitsDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ProjectUnitsDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ProjectUnitsDialog_btnSave"));
		} else {
			this.btnNew.setVisible(false);
			this.btnEdit.setVisible(false);
			this.btnDelete.setVisible(false);
			this.btnSave.setVisible(false);
		}
		this.btnCancel.setVisible(false);

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
		doShowNotes(this.projectUnits);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.projectUnits.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param builderProjcet
	 * 
	 */
	public void doWriteBeanToComponents(ProjectUnits aProjectUnits) {
		logger.debug(Literal.ENTERING);
		// Project Units
		fillComboBox(this.unitType, aProjectUnits.getUnitType(), unitTypesList, "");
		if (StringUtils.equals(PennantConstants.FLAT, aProjectUnits.getUnitType())) {
			this.space_tower.setVisible(true);
			this.space_floornumber.setVisible(true);
			this.space_tower.setSclass("mandatory");
			this.space_floornumber.setSclass("mandatory");
		} else {
			this.space_tower.setVisible(false);
			this.space_floornumber.setVisible(false);
			this.space_tower.setSclass("");
			this.space_floornumber.setSclass("");
		}
		this.tower.setValue(aProjectUnits.getTower());
		this.floorNumber.setValue(aProjectUnits.getFloorNumber());
		this.unitNumber.setValue(aProjectUnits.getUnitNumber());
		this.unitArea.setValue(aProjectUnits.getUnitArea());
		this.unitArea.addForward("onChange", windowProjectUnitsDialog, "onChangeUnitArea", unitArea);

		this.rate.setValue(aProjectUnits.getRate());
		this.rate.addForward("onChange", windowProjectUnitsDialog, "onChangeRate", rate);

		this.price.setValue(aProjectUnits.getPrice());
		this.price.addForward("onFulfill", windowProjectUnitsDialog, "onFulfillPrice", price);

		this.otherCharges.setValue(aProjectUnits.getOtherCharges());
		this.otherCharges.addForward("onFulfill", windowProjectUnitsDialog, "onFulfillOtherCharges", otherCharges);
		this.totalPrice.setValue(aProjectUnits.getTotalPrice());
		this.unitRpsf.setValue(aProjectUnits.getUnitRpsf());
		this.unitPlotArea.setValue(aProjectUnits.getUnitPlotArea());
		this.unitSuperBuiltUp.setValue(aProjectUnits.getUnitSuperBuiltUp());

		fillComboBox(this.unitAreaConsidered, aProjectUnits.getUnitAreaConsidered(), unitAreaConsideredList, "");
		this.unitAreaConsidered.addForward("onSelect", windowProjectUnitsDialog, "onSelectUnitAreaConsidered",
				unitAreaConsidered);
		this.carpetArea.setValue(aProjectUnits.getCarpetArea());
		this.unitBuiltUpArea.setValue(aProjectUnits.getUnitBuiltUpArea());
		fillComboBox(this.rateConsidered, aProjectUnits.getRateConsidered(), rateConsideredList, "");
		this.rateConsidered.addForward("onSelect", windowProjectUnitsDialog, "onSelectRateConsidered", rateConsidered);
		this.rateAsPerCarpetArea.setValue(aProjectUnits.getRateAsPerCarpetArea());
		this.rateAsPerBuiltUpArea.setValue(aProjectUnits.getRateAsPerBuiltUpArea());
		this.rateAsPerSuperBuiltUpArea.setValue(aProjectUnits.getRateAsPerSuperBuiltUpArea());
		this.rateAsPerBranchAPF.setValue(aProjectUnits.getRateAsPerBranchAPF());
		this.rateAsPerCostSheet.setValue(aProjectUnits.getRateAsPerCostSheet());
		this.floorRiseCharges.setValue(aProjectUnits.getFloorRiseCharges());
		this.openCarParkingCharges.setValue(aProjectUnits.getOpenCarParkingCharges());
		this.closedCarParkingCharges.setValue(aProjectUnits.getClosedCarParkingCharges());
		this.gst.setValue(aProjectUnits.getGst());
		this.remarks.setValue(aProjectUnits.getRemarks());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aBuilderProjcet
	 */
	public void doWriteComponentsToBean(ProjectUnits aProjectUnits) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Project Units
		try {
			String unitType = getComboboxValue(this.unitType);
			if (this.unitType.getSelectedItem() != null) {
				unitType = this.unitType.getSelectedItem().getValue().toString();
			}
			if (PennantConstants.List_Select.equals(unitType)) {
				throw new WrongValueException(this.unitType, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_ProjectUnitsDialog_UnitType.value") }));

			} else {
				aProjectUnits.setUnitType(unitType);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aProjectUnits.setTower(this.tower.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProjectUnits.setFloorNumber(this.floorNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProjectUnits.setUnitNumber(this.unitNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProjectUnits.setUnitArea(this.unitArea.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProjectUnits.setRate(this.rate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProjectUnits.setPrice(this.price.getValidateValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProjectUnits.setOtherCharges(this.otherCharges.getValidateValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProjectUnits.setTotalPrice(this.totalPrice.getValidateValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProjectUnits.setUnitRpsf(this.unitRpsf.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aProjectUnits.setUnitPlotArea(this.unitPlotArea.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProjectUnits.setUnitSuperBuiltUp(this.unitSuperBuiltUp.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if ("#".equals(getComboboxValue(this.unitAreaConsidered))) {
				if (!this.unitAreaConsidered.isDisabled()) {
					throw new WrongValueException(this.unitAreaConsidered, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_ProjectUnitsDialog_AreaConsidered.value") }));
				}
			} else {
				aProjectUnits.setUnitAreaConsidered(getComboboxValue(this.unitAreaConsidered));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aProjectUnits.setCarpetArea(this.carpetArea.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProjectUnits.setUnitBuiltUpArea(this.unitBuiltUpArea.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if ("#".equals(getComboboxValue(this.rateConsidered))) {
				if (!this.rateConsidered.isDisabled()) {
					throw new WrongValueException(this.rateConsidered, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_ProjectUnitsDialog_RateConsidered.value") }));
				}
			} else {
				aProjectUnits.setRateConsidered(getComboboxValue(this.rateConsidered));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProjectUnits.setRateAsPerCarpetArea(this.rateAsPerCarpetArea.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProjectUnits.setRateAsPerBuiltUpArea(this.rateAsPerBuiltUpArea.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProjectUnits.setRateAsPerSuperBuiltUpArea(this.rateAsPerSuperBuiltUpArea.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProjectUnits.setRateAsPerBranchAPF(this.rateAsPerBranchAPF.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProjectUnits.setRateAsPerCostSheet(this.rateAsPerCostSheet.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProjectUnits.setFloorRiseCharges(this.floorRiseCharges.getValidateValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProjectUnits.setOpenCarParkingCharges(this.openCarParkingCharges.getValidateValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProjectUnits.setClosedCarParkingCharges(this.closedCarParkingCharges.getValidateValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProjectUnits.setGst(this.gst.getValidateValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProjectUnits.setRemarks(this.remarks.getValue());
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
	 * @param builderProjcet The entity that need to be render.
	 */
	public void doShowDialog(ProjectUnits projectUnits) {
		logger.debug(Literal.ENTERING);
		if (projectUnits.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isFromParent()) {
				if (enqiryModule) {
					doReadOnly();
					this.btnCtrl.setBtnStatus_Enquiry();
					this.btnNotes.setVisible(false);
				} else {
					doEdit();
				}
			} else if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(projectUnits.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		doWriteBeanToComponents(projectUnits);
		windowProjectUnitsDialog.setHeight("75%");
		windowProjectUnitsDialog.setWidth("90%");
		setDialog(DialogType.MODAL);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		/* Project Units */
		if (!this.unitType.isReadonly()) {
			this.unitType.setConstraint(
					new StaticListValidator(unitTypesList, Labels.getLabel("label_ProjectUnitsDialog_UnitType.value")));
		}
		if (!this.tower.isReadonly() && this.space_tower.isVisible()) {
			this.tower.setConstraint(new PTStringValidator(Labels.getLabel("label_ProjectUnitsDialog_Tower.value"),
					PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, this.space_tower.isVisible()));
		}
		if (!this.floorNumber.isReadonly() && this.space_floornumber.isVisible()) {
			this.floorNumber
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ProjectUnitsDialog_FloorNumber.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, this.space_floornumber.isVisible()));
		}

		if (!this.unitNumber.isReadonly()) {
			this.unitNumber
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ProjectUnitsDialog_UnitNumber.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, true, false));
		}

		if (!this.rate.isReadonly()) {
			this.rate.setConstraint(new PTNumberValidator(Labels.getLabel("label_ProjectUnitsDialog_Rate.value"), true,
					false, 1, 999999999));

		}

		if (!this.unitArea.isReadonly()) {
			this.unitArea.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_ProjectUnitsDialog_UnitArea.value"), true, false, 1, 999999999));
		}
		if (!this.unitAreaConsidered.isReadonly()) {
			this.unitAreaConsidered.setConstraint(new StaticListValidator(unitAreaConsideredList,
					Labels.getLabel("label_ProjectUnitsDialog_AreaConsidered.value")));
		}
		if (!this.carpetArea.isDisabled()) {
			this.carpetArea.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_ProjectUnitsDialog_CarpetArea.value"), 2, true));
		}
		if (!this.unitBuiltUpArea.isDisabled()) {
			this.unitBuiltUpArea.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_ProjectUnitsDialog_UnitBuiltUpArea.value"), 2, true));
		}
		if (!this.unitSuperBuiltUp.isDisabled()) {
			this.unitSuperBuiltUp.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_ProjectUnitsDialog_UnitSuperBuiltUp.value"), 2, true));
		}
		if (!this.rateConsidered.isReadonly()) {
			this.rateConsidered.setConstraint(new StaticListValidator(rateConsideredList,
					Labels.getLabel("label_ProjectUnitsDialog_RateConsidered.value")));
		}
		if (!this.rateAsPerCarpetArea.isDisabled()) {
			this.rateAsPerCarpetArea.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_ProjectUnitsDialog_RateAsPerCarpetArea.value"), 2, true));
		}
		if (!this.rateAsPerBuiltUpArea.isDisabled()) {
			this.rateAsPerBuiltUpArea.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_ProjectUnitsDialog_RateAsPerBuiltUpArea.value"), 2, true));
		}
		if (!this.rateAsPerSuperBuiltUpArea.isDisabled()) {
			this.rateAsPerSuperBuiltUpArea.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_ProjectUnitsDialog_RateAsPerSuperBuiltUpArea.value"), 2, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		this.unitType.setConstraint("");
		this.tower.setConstraint("");
		this.floorNumber.setConstraint("");
		this.unitNumber.setConstraint("");
		this.unitArea.setConstraint("");
		this.rate.setConstraint("");
		this.price.setConstraint("");
		this.otherCharges.setConstraint("");
		this.totalPrice.setConstraint("");
		this.unitRpsf.setConstraint("");
		this.unitPlotArea.setConstraint("");
		this.unitSuperBuiltUp.setConstraint("");
		this.unitAreaConsidered.setConstraint("");
		this.carpetArea.setConstraint("");
		this.unitBuiltUpArea.setConstraint("");
		this.rateConsidered.setConstraint("");
		this.rateAsPerCarpetArea.setConstraint("");
		this.rateAsPerBuiltUpArea.setConstraint("");
		this.rateAsPerSuperBuiltUpArea.setConstraint("");
		this.rateAsPerBranchAPF.setConstraint("");
		this.rateAsPerCostSheet.setConstraint("");
		this.floorRiseCharges.setConstraint("");
		this.openCarParkingCharges.setConstraint("");
		this.closedCarParkingCharges.setConstraint("");
		this.gst.setConstraint("");
		this.remarks.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.ENTERING);
		this.tower.setConstraint("");
		this.floorNumber.setConstraint("");
		this.unitNumber.setConstraint("");
		this.rate.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);
		this.unitType.setErrorMessage("");
		this.tower.setErrorMessage("");
		this.floorNumber.setErrorMessage("");
		this.unitNumber.setErrorMessage("");
		this.unitArea.setErrorMessage("");
		this.rate.setErrorMessage("");
		this.price.setErrorMessage("");
		this.otherCharges.setErrorMessage("");
		this.totalPrice.setErrorMessage("");
		this.unitRpsf.setErrorMessage("");
		this.unitPlotArea.setErrorMessage("");
		this.unitSuperBuiltUp.setErrorMessage("");
		this.unitAreaConsidered.setErrorMessage("");
		this.carpetArea.setErrorMessage("");
		this.unitBuiltUpArea.setErrorMessage("");
		this.rateConsidered.setErrorMessage("");
		this.rateAsPerCarpetArea.setErrorMessage("");
		this.rateAsPerBuiltUpArea.setErrorMessage("");
		this.rateAsPerSuperBuiltUpArea.setErrorMessage("");
		this.rateAsPerBranchAPF.setErrorMessage("");
		this.rateAsPerCostSheet.setErrorMessage("");
		this.floorRiseCharges.setErrorMessage("");
		this.openCarParkingCharges.setErrorMessage("");
		this.closedCarParkingCharges.setErrorMessage("");
		this.gst.setErrorMessage("");
		this.remarks.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	protected boolean doCustomDelete(final ProjectUnits aProjectUnits, String tranType) {
		tranType = PennantConstants.TRAN_DEL;
		AuditHeader auditHeader = processProjectUnits(aProjectUnits, tranType);
		auditHeader = ErrorControl.showErrorDetails(this.windowProjectUnitsDialog, auditHeader);
		int retValue = auditHeader.getProcessStatus();
		if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
			builderProjcetDialogCtrl.doRenderProjectUnits(this.projectUnitsList);
			builderProjcetDialogCtrl.setProjectUnitsList(this.projectUnitsList);
			return true;
		}
		return false;
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final ProjectUnits pu = new ProjectUnits();
		BeanUtils.copyProperties(this.projectUnits, pu);

		StringBuilder message = new StringBuilder();
		message.append("Unit Type: ").append(pu.getUnitType());
		message.append(", Floor Number: ").append(pu.getFloorNumber());
		message.append(", Tower: ").append(pu.getTower());
		message.append(", Unit Number: ").append(pu.getUnitNumber());

		doDelete(message.toString(), pu);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.projectUnits.isNewRecord()) {
			this.unitType.setDisabled(false);
			this.unitNumber.setReadonly(false);
			this.tower.setReadonly(false);
			this.floorNumber.setReadonly(false);
		} else {
			this.unitType.setDisabled(true);
			this.unitNumber.setReadonly(true);
			this.tower.setReadonly(true);
			this.floorNumber.setReadonly(true);

		}
		readOnlyComponent(isReadOnly("ProjectUnitsDialog_unitArea"), this.unitArea);
		readOnlyComponent(isReadOnly("ProjectUnitsDialog_rate"), this.rate);
		readOnlyComponent(isReadOnly("ProjectUnitsDialog_price"), this.price);
		readOnlyComponent(isReadOnly("ProjectUnitsDialog_otherCharges"), this.otherCharges);
		readOnlyComponent(isReadOnly("ProjectUnitsDialog_totalPrice"), this.totalPrice);
		readOnlyComponent(isReadOnly("ProjectUnitsDialog_unitRpsf"), this.unitRpsf);
		readOnlyComponent(isReadOnly("ProjectUnitsDialog_unitPlotArea"), this.unitPlotArea);
		readOnlyComponent(isReadOnly("ProjectUnitsDialog_unitSuperBuiltUp"), this.unitSuperBuiltUp);
		readOnlyComponent(isReadOnly("ProjectUnitsDialog_AreaConsidered"), this.unitAreaConsidered);
		readOnlyComponent(isReadOnly("ProjectUnitsDialog_CarpetArea"), this.carpetArea);
		readOnlyComponent(isReadOnly("ProjectUnitsDialog_UnitBuiltUpArea"), this.unitBuiltUpArea);
		readOnlyComponent(isReadOnly("ProjectUnitsDialog_RateConsidered"), this.rateConsidered);
		readOnlyComponent(isReadOnly("ProjectUnitsDialog_RateAsPerCarpetArea"), this.rateAsPerCarpetArea);
		readOnlyComponent(isReadOnly("ProjectUnitsDialog_RateAsPerBuiltUpArea"), this.rateAsPerBuiltUpArea);
		readOnlyComponent(isReadOnly("ProjectUnitsDialog_RateAsPerSuperBuiltUpArea"), this.rateAsPerSuperBuiltUpArea);
		readOnlyComponent(isReadOnly("ProjectUnitsDialog_RateAsPerBranchAPF"), this.rateAsPerBranchAPF);
		readOnlyComponent(isReadOnly("ProjectUnitsDialog_RateAsPerCostSheet"), this.rateAsPerCostSheet);
		readOnlyComponent(isReadOnly("ProjectUnitsDialog_FloorRiseCharges"), this.floorRiseCharges);
		readOnlyComponent(isReadOnly("ProjectUnitsDialog_OpenCarParkingCharges"), this.openCarParkingCharges);
		readOnlyComponent(isReadOnly("ProjectUnitsDialog_ClosedCarParkingCharges"), this.closedCarParkingCharges);
		readOnlyComponent(isReadOnly("ProjectUnitsDialog_GST"), this.gst);
		readOnlyComponent(isReadOnly("ProjectUnitsDialog_Remarks"), this.remarks);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);
		readOnlyComponent(true, this.unitType);
		readOnlyComponent(true, this.tower);
		readOnlyComponent(true, this.floorNumber);
		readOnlyComponent(true, this.unitNumber);
		readOnlyComponent(true, this.unitArea);
		readOnlyComponent(true, this.rate);
		readOnlyComponent(true, this.price);
		readOnlyComponent(true, this.otherCharges);
		readOnlyComponent(true, this.totalPrice);
		readOnlyComponent(true, this.unitRpsf);
		readOnlyComponent(true, this.unitPlotArea);
		readOnlyComponent(true, this.unitSuperBuiltUp);
		readOnlyComponent(true, this.unitAreaConsidered);
		readOnlyComponent(true, this.carpetArea);
		readOnlyComponent(true, this.unitBuiltUpArea);
		readOnlyComponent(true, this.rateConsidered);
		readOnlyComponent(true, this.rateAsPerCarpetArea);
		readOnlyComponent(true, this.rateAsPerBuiltUpArea);
		readOnlyComponent(true, this.rateAsPerSuperBuiltUpArea);
		readOnlyComponent(true, this.rateAsPerBranchAPF);
		readOnlyComponent(true, this.rateAsPerCostSheet);
		readOnlyComponent(true, this.floorRiseCharges);
		readOnlyComponent(true, this.openCarParkingCharges);
		readOnlyComponent(true, this.closedCarParkingCharges);
		readOnlyComponent(true, this.gst);
		readOnlyComponent(true, this.remarks);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);
		this.unitType.setValue("");
		this.tower.setValue("");
		this.floorNumber.setValue("");
		this.unitNumber.setValue("");
		this.price.setValue("");
		this.totalPrice.setValue("");
		this.otherCharges.setValue("");
		this.unitRpsf.setValue("");
		this.unitPlotArea.setValue("");
		this.unitSuperBuiltUp.setValue("");
		this.unitAreaConsidered.setValue("");
		this.carpetArea.setValue("");
		this.unitBuiltUpArea.setValue("");
		this.rateConsidered.setValue("");
		this.rateAsPerCarpetArea.setValue("");
		this.rateAsPerBuiltUpArea.setValue("");
		this.rateAsPerSuperBuiltUpArea.setValue("");
		this.rateAsPerBranchAPF.setValue("");
		this.rateAsPerCostSheet.setValue("");
		this.floorRiseCharges.setValue("");
		this.openCarParkingCharges.setValue("");
		this.closedCarParkingCharges.setValue("");
		this.gst.setValue("");
		this.remarks.setValue("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final ProjectUnits aProjectUnits = new ProjectUnits();
		BeanUtils.copyProperties(this.projectUnits, aProjectUnits);
		boolean isNew = false;
		doSetValidation();
		doWriteComponentsToBean(aProjectUnits);
		isNew = aProjectUnits.isNewRecord();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aProjectUnits.getRecordType())) {
				aProjectUnits.setVersion(aProjectUnits.getVersion() + 1);
				if (isNew) {
					aProjectUnits.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aProjectUnits.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aProjectUnits.setNewRecord(true);
				}
			}
		} else {
			aProjectUnits.setVersion(aProjectUnits.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
				aProjectUnits.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}

			if (StringUtils.isBlank(aProjectUnits.getRecordType())) {
				aProjectUnits.setVersion(aProjectUnits.getVersion() + 1);
				aProjectUnits.setRecordType(PennantConstants.RCD_UPD);
			}

			if (aProjectUnits.getRecordType().equals(PennantConstants.RCD_ADD) && isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aProjectUnits.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			AuditHeader auditHeader = processProjectUnits(aProjectUnits, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.windowProjectUnitsDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				builderProjcetDialogCtrl.doRenderProjectUnits(this.projectUnitsList);
				closeDialog();
			}
		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(ProjectUnits aProjectUnits, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aProjectUnits.getBefImage(), aProjectUnits);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aProjectUnits.getUserDetails(),
				getOverideMap());
	}

	private AuditHeader processProjectUnits(ProjectUnits aProjectUnits, String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aProjectUnits, tranType);
		projectUnitsList = new ArrayList<ProjectUnits>();
		List<ProjectUnits> projectUnits = builderProjcetDialogCtrl.getProjectUnitsList();
		if (projectUnits != null && projectUnits.size() > 0) {
			for (int i = 0; i < projectUnits.size(); i++) {
				ProjectUnits projectUnit = projectUnits.get(i);
				auditHeader = doValidation(projectUnit, aProjectUnits, auditHeader);
				if (!CollectionUtils.isEmpty(auditHeader.getErrorMessage())) {
					return auditHeader;
				}
				// Both Current and Existing list record same
				if (doCheckUniqueConstraint(projectUnit, aProjectUnits)) {

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (PennantConstants.RECORD_TYPE_UPD.equals(aProjectUnits.getRecordType())) {
							aProjectUnits.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							projectUnitsList.add(aProjectUnits);
						} else if (PennantConstants.RCD_ADD.equals(aProjectUnits.getRecordType())) {
							recordAdded = true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(aProjectUnits.getRecordType())) {
							aProjectUnits.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							projectUnitsList.add(aProjectUnits);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(aProjectUnits.getRecordType())) {
							recordAdded = true;
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							projectUnitsList.add(projectUnit);
						}
					}
				} else {
					projectUnitsList.add(projectUnit);
				}
			}
		}
		if (!recordAdded) {
			projectUnitsList.add(aProjectUnits);
		}
		return auditHeader;
	}

	private boolean doCheckUniqueConstraint(ProjectUnits projectUnit, ProjectUnits aProjectUnits) {
		if (projectUnit != null && aProjectUnits != null) {
			String unitType = aProjectUnits.getUnitType();
			String floorNumber = aProjectUnits.getFloorNumber();
			String tower = aProjectUnits.getTower();
			String unitNumber = aProjectUnits.getUnitNumber();
			if (StringUtils.equals(unitType, projectUnit.getUnitType())
					&& StringUtils.equals(tower, projectUnit.getTower())
					&& StringUtils.equals(floorNumber, projectUnit.getFloorNumber())
					&& StringUtils.equals(unitNumber, projectUnit.getUnitNumber())) {
				return true;
			}
		}
		return false;
	}

	public void onSelectUnitAreaConsidered() {
		logger.debug(Literal.ENTERING);
		if (unitAreaConsidered.getSelectedItem().getValue() != null) {
			if (unitAreaConsidered.getSelectedItem().getValue()
					.equals(PennantStaticListUtil.getUnitAreaConsidered().get(0).getValue())) {
				this.carpetArea.setDisabled(false);
				this.space_carpetArea.setSclass("mandatory");
				this.unitBuiltUpArea.setDisabled(true);
				this.unitBuiltUpArea.setConstraint("");
				this.unitBuiltUpArea.setErrorMessage("");
				this.unitBuiltUpArea.clearErrorMessage();
				this.space_unitBuiltUpArea.setSclass("");
				this.unitSuperBuiltUp.setDisabled(true);
				this.unitSuperBuiltUp.setConstraint("");
				this.unitSuperBuiltUp.setErrorMessage("");
				this.unitSuperBuiltUp.clearErrorMessage();
				this.space_unitSuperBuiltUp.setSclass("");
				this.unitBuiltUpArea.setValue(BigDecimal.ZERO);
				this.unitSuperBuiltUp.setValue(BigDecimal.ZERO);

			}
			if (unitAreaConsidered.getSelectedItem().getValue()
					.equals(PennantStaticListUtil.getUnitAreaConsidered().get(1).getValue())) {
				this.unitBuiltUpArea.setDisabled(false);
				this.space_unitBuiltUpArea.setSclass("mandatory");
				this.carpetArea.setDisabled(true);
				this.carpetArea.setConstraint("");
				this.carpetArea.setErrorMessage("");
				this.carpetArea.clearErrorMessage();
				this.space_carpetArea.setSclass("");
				this.unitSuperBuiltUp.setDisabled(true);
				this.unitSuperBuiltUp.setConstraint("");
				this.unitSuperBuiltUp.setErrorMessage("");
				this.unitSuperBuiltUp.clearErrorMessage();
				this.space_unitSuperBuiltUp.setSclass("");
				this.carpetArea.setValue(BigDecimal.ZERO);
				this.unitSuperBuiltUp.setValue(BigDecimal.ZERO);

			}
			if (unitAreaConsidered.getSelectedItem().getValue()
					.equals(PennantStaticListUtil.getUnitAreaConsidered().get(2).getValue())) {
				this.unitSuperBuiltUp.setDisabled(false);
				this.space_unitSuperBuiltUp.setSclass("mandatory");
				this.carpetArea.setDisabled(true);
				this.carpetArea.setConstraint("");
				this.carpetArea.setErrorMessage("");
				this.carpetArea.clearErrorMessage();
				this.space_carpetArea.setSclass("");
				this.unitBuiltUpArea.setDisabled(true);
				this.unitBuiltUpArea.setConstraint("");
				this.unitBuiltUpArea.setErrorMessage("");
				this.unitBuiltUpArea.clearErrorMessage();
				this.space_unitBuiltUpArea.setSclass("");
				this.carpetArea.setValue(BigDecimal.ZERO);
				this.unitBuiltUpArea.setValue(BigDecimal.ZERO);

			}

		}
		onChangeUnitArea();
		logger.debug(Literal.LEAVING);
	}

	public void onSelectRateConsidered() {
		logger.debug(Literal.ENTERING);
		if (rateConsidered.getSelectedItem().getValue() != null) {
			switch (rateConsidered.getSelectedItem().getValue().toString()) {
			case PennantConstants.CARPET_AREA_RATE:
				this.rateAsPerCarpetArea.setDisabled(false);
				this.space_rateAsPerCarpetArea.setSclass("mandatory");

				this.rateAsPerBuiltUpArea.setDisabled(true);
				this.rateAsPerBuiltUpArea.setConstraint("");
				this.rateAsPerBuiltUpArea.setErrorMessage("");
				this.rateAsPerBuiltUpArea.clearErrorMessage();
				this.space_rateAsPerBuiltUpArea.setSclass("");
				this.rateAsPerBuiltUpArea.setValue(BigDecimal.ZERO);

				this.rateAsPerSuperBuiltUpArea.setDisabled(true);
				this.rateAsPerSuperBuiltUpArea.setConstraint("");
				this.rateAsPerSuperBuiltUpArea.setErrorMessage("");
				this.rateAsPerSuperBuiltUpArea.clearErrorMessage();
				this.space_rateAsPerSuperBuiltUpArea.setSclass("");
				this.rateAsPerSuperBuiltUpArea.setValue(BigDecimal.ZERO);

				this.rateAsPerBranchAPF.setDisabled(true);
				this.rateAsPerBranchAPF.setConstraint("");
				this.rateAsPerBranchAPF.setErrorMessage("");
				this.rateAsPerBranchAPF.clearErrorMessage();
				this.space_rateAsPerBranchAPF.setSclass("");
				this.rateAsPerBranchAPF.setValue(BigDecimal.ZERO);

				this.rateAsPerCostSheet.setDisabled(true);
				this.rateAsPerCostSheet.setConstraint("");
				this.rateAsPerCostSheet.setErrorMessage("");
				this.rateAsPerCostSheet.clearErrorMessage();
				this.space_rateAsPerCostSheet.setSclass("");
				this.rateAsPerCostSheet.setValue(BigDecimal.ZERO);

				this.unitRpsf.setDisabled(true);
				this.unitRpsf.setConstraint("");
				this.unitRpsf.setErrorMessage("");
				this.unitRpsf.clearErrorMessage();
				this.space_unitRpsf.setSclass("");
				this.unitRpsf.setValue(BigDecimal.ZERO);

				break;
			case PennantConstants.BUILTUP_AREA_RATE:
				this.rateAsPerBuiltUpArea.setDisabled(false);
				this.space_rateAsPerBuiltUpArea.setSclass("mandatory");

				this.rateAsPerCarpetArea.setDisabled(true);
				this.rateAsPerCarpetArea.setConstraint("");
				this.rateAsPerCarpetArea.setErrorMessage("");
				this.rateAsPerCarpetArea.clearErrorMessage();
				this.space_rateAsPerCarpetArea.setSclass("");
				this.rateAsPerCarpetArea.setValue(BigDecimal.ZERO);

				this.rateAsPerSuperBuiltUpArea.setDisabled(true);
				this.rateAsPerSuperBuiltUpArea.setConstraint("");
				this.rateAsPerSuperBuiltUpArea.setErrorMessage("");
				this.rateAsPerSuperBuiltUpArea.clearErrorMessage();
				this.space_rateAsPerSuperBuiltUpArea.setSclass("");
				this.rateAsPerSuperBuiltUpArea.setValue(BigDecimal.ZERO);

				this.rateAsPerBranchAPF.setDisabled(true);
				this.rateAsPerBranchAPF.setConstraint("");
				this.rateAsPerBranchAPF.setErrorMessage("");
				this.rateAsPerBranchAPF.clearErrorMessage();
				this.space_rateAsPerBranchAPF.setSclass("");
				this.rateAsPerBranchAPF.setValue(BigDecimal.ZERO);

				this.rateAsPerCostSheet.setDisabled(true);
				this.rateAsPerCostSheet.setConstraint("");
				this.rateAsPerCostSheet.setErrorMessage("");
				this.rateAsPerCostSheet.clearErrorMessage();
				this.space_rateAsPerCostSheet.setSclass("");
				this.rateAsPerCostSheet.setValue(BigDecimal.ZERO);

				this.unitRpsf.setDisabled(true);
				this.unitRpsf.setConstraint("");
				this.unitRpsf.setErrorMessage("");
				this.unitRpsf.clearErrorMessage();
				this.space_unitRpsf.setSclass("");
				this.unitRpsf.setValue(BigDecimal.ZERO);

				break;
			case PennantConstants.SUPERBUILTUP_AREA_RATE:
				this.rateAsPerSuperBuiltUpArea.setDisabled(false);
				this.space_rateAsPerSuperBuiltUpArea.setSclass("mandatory");

				this.rateAsPerCarpetArea.setDisabled(true);
				this.rateAsPerCarpetArea.setConstraint("");
				this.rateAsPerCarpetArea.setErrorMessage("");
				this.rateAsPerCarpetArea.clearErrorMessage();
				this.space_rateAsPerCarpetArea.setSclass("");
				this.rateAsPerCarpetArea.setValue(BigDecimal.ZERO);

				this.rateAsPerBuiltUpArea.setDisabled(true);
				this.rateAsPerBuiltUpArea.setConstraint("");
				this.rateAsPerBuiltUpArea.setErrorMessage("");
				this.rateAsPerBuiltUpArea.clearErrorMessage();
				this.space_rateAsPerBuiltUpArea.setSclass("");
				this.rateAsPerBuiltUpArea.setValue(BigDecimal.ZERO);

				this.rateAsPerBranchAPF.setDisabled(true);
				this.rateAsPerBranchAPF.setConstraint("");
				this.rateAsPerBranchAPF.setErrorMessage("");
				this.rateAsPerBranchAPF.clearErrorMessage();
				this.space_rateAsPerBranchAPF.setSclass("");
				this.rateAsPerBranchAPF.setValue(BigDecimal.ZERO);

				this.rateAsPerCostSheet.setDisabled(true);
				this.rateAsPerCostSheet.setConstraint("");
				this.rateAsPerCostSheet.setErrorMessage("");
				this.rateAsPerCostSheet.clearErrorMessage();
				this.space_rateAsPerCostSheet.setSclass("");
				this.rateAsPerCostSheet.setValue(BigDecimal.ZERO);

				this.unitRpsf.setDisabled(true);
				this.unitRpsf.setConstraint("");
				this.unitRpsf.setErrorMessage("");
				this.unitRpsf.clearErrorMessage();
				this.space_unitRpsf.setSclass("");
				this.unitRpsf.setValue(BigDecimal.ZERO);
				break;
			case PennantConstants.BRANCH_APF_RATE:
				this.rateAsPerBranchAPF.setDisabled(false);
				this.space_rateAsPerBranchAPF.setSclass("mandatory");

				this.rateAsPerCarpetArea.setDisabled(true);
				this.rateAsPerCarpetArea.setConstraint("");
				this.rateAsPerCarpetArea.setErrorMessage("");
				this.rateAsPerCarpetArea.clearErrorMessage();
				this.space_rateAsPerCarpetArea.setSclass("");
				this.rateAsPerCarpetArea.setValue(BigDecimal.ZERO);

				this.rateAsPerBuiltUpArea.setDisabled(true);
				this.rateAsPerBuiltUpArea.setConstraint("");
				this.rateAsPerBuiltUpArea.setErrorMessage("");
				this.rateAsPerBuiltUpArea.clearErrorMessage();
				this.space_rateAsPerBuiltUpArea.setSclass("");
				this.rateAsPerBuiltUpArea.setValue(BigDecimal.ZERO);

				this.rateAsPerSuperBuiltUpArea.setDisabled(true);
				this.rateAsPerSuperBuiltUpArea.setConstraint("");
				this.rateAsPerSuperBuiltUpArea.setErrorMessage("");
				this.rateAsPerSuperBuiltUpArea.clearErrorMessage();
				this.space_rateAsPerSuperBuiltUpArea.setSclass("");
				this.rateAsPerSuperBuiltUpArea.setValue(BigDecimal.ZERO);

				this.rateAsPerCostSheet.setDisabled(true);
				this.rateAsPerCostSheet.setConstraint("");
				this.rateAsPerCostSheet.setErrorMessage("");
				this.rateAsPerCostSheet.clearErrorMessage();
				this.space_rateAsPerCostSheet.setSclass("");
				this.rateAsPerCostSheet.setValue(BigDecimal.ZERO);

				this.unitRpsf.setDisabled(true);
				this.unitRpsf.setConstraint("");
				this.unitRpsf.setErrorMessage("");
				this.unitRpsf.clearErrorMessage();
				this.space_unitRpsf.setSclass("");
				this.unitRpsf.setValue(BigDecimal.ZERO);

				break;
			case PennantConstants.COST_SHEET_RATE:
				this.rateAsPerCostSheet.setDisabled(false);
				this.space_rateAsPerCostSheet.setSclass("mandatory");

				this.rateAsPerCarpetArea.setDisabled(true);
				this.rateAsPerCarpetArea.setConstraint("");
				this.rateAsPerCarpetArea.setErrorMessage("");
				this.rateAsPerCarpetArea.clearErrorMessage();
				this.space_rateAsPerCarpetArea.setSclass("");
				this.rateAsPerCarpetArea.setValue(BigDecimal.ZERO);

				this.rateAsPerBuiltUpArea.setDisabled(true);
				this.rateAsPerBuiltUpArea.setConstraint("");
				this.rateAsPerBuiltUpArea.setErrorMessage("");
				this.rateAsPerBuiltUpArea.clearErrorMessage();
				this.space_rateAsPerBuiltUpArea.setSclass("");
				this.rateAsPerBuiltUpArea.setValue(BigDecimal.ZERO);

				this.rateAsPerSuperBuiltUpArea.setDisabled(true);
				this.rateAsPerSuperBuiltUpArea.setConstraint("");
				this.rateAsPerSuperBuiltUpArea.setErrorMessage("");
				this.rateAsPerSuperBuiltUpArea.clearErrorMessage();
				this.space_rateAsPerSuperBuiltUpArea.setSclass("");
				this.rateAsPerSuperBuiltUpArea.setValue(BigDecimal.ZERO);

				this.rateAsPerBranchAPF.setDisabled(true);
				this.rateAsPerBranchAPF.setConstraint("");
				this.rateAsPerBranchAPF.setErrorMessage("");
				this.rateAsPerBranchAPF.clearErrorMessage();
				this.space_rateAsPerBranchAPF.setSclass("");
				this.rateAsPerBranchAPF.setValue(BigDecimal.ZERO);

				this.unitRpsf.setDisabled(true);
				this.unitRpsf.setConstraint("");
				this.unitRpsf.setErrorMessage("");
				this.unitRpsf.clearErrorMessage();
				this.space_unitRpsf.setSclass("");
				this.unitRpsf.setValue(BigDecimal.ZERO);

				break;
			case PennantConstants.RATE_PER_SQUARE_FEET:
				this.unitRpsf.setDisabled(false);
				this.space_unitRpsf.setSclass("mandatory");

				this.rateAsPerCarpetArea.setDisabled(true);
				this.rateAsPerCarpetArea.setConstraint("");
				this.rateAsPerCarpetArea.setErrorMessage("");
				this.rateAsPerCarpetArea.clearErrorMessage();
				this.space_rateAsPerCarpetArea.setSclass("");
				this.rateAsPerCarpetArea.setValue(BigDecimal.ZERO);

				this.rateAsPerBuiltUpArea.setDisabled(true);
				this.rateAsPerBuiltUpArea.setConstraint("");
				this.rateAsPerBuiltUpArea.setErrorMessage("");
				this.rateAsPerBuiltUpArea.clearErrorMessage();
				this.space_rateAsPerBuiltUpArea.setSclass("");
				this.rateAsPerBuiltUpArea.setValue(BigDecimal.ZERO);

				this.rateAsPerSuperBuiltUpArea.setDisabled(true);
				this.rateAsPerSuperBuiltUpArea.setConstraint("");
				this.rateAsPerSuperBuiltUpArea.setErrorMessage("");
				this.rateAsPerSuperBuiltUpArea.clearErrorMessage();
				this.space_rateAsPerSuperBuiltUpArea.setSclass("");
				this.rateAsPerSuperBuiltUpArea.setValue(BigDecimal.ZERO);

				this.rateAsPerBranchAPF.setDisabled(true);
				this.rateAsPerBranchAPF.setConstraint("");
				this.rateAsPerBranchAPF.setErrorMessage("");
				this.rateAsPerBranchAPF.clearErrorMessage();
				this.space_rateAsPerBranchAPF.setSclass("");
				this.rateAsPerBranchAPF.setValue(BigDecimal.ZERO);

				this.rateAsPerCostSheet.setDisabled(true);
				this.rateAsPerCostSheet.setConstraint("");
				this.rateAsPerCostSheet.setErrorMessage("");
				this.rateAsPerCostSheet.clearErrorMessage();
				this.space_rateAsPerCostSheet.setSclass("");
				this.rateAsPerCostSheet.setValue(BigDecimal.ZERO);
				break;
			default:
				break;
			}
		}

		onChangeUnitArea();
		totalPrice.setValue(price.getActualValue().add(getTotalPrice()));
		logger.debug(Literal.LEAVING);
	}

	public void onChangeUnitArea() {
		logger.debug(Literal.ENTERING);
		if (unitArea.getValue() != null && rate.getValue() != null) {
			price.setValue(new BigDecimal(unitArea.getValue() * rate.getValue())
					.add(carpetArea.getValue().multiply(rateAsPerCarpetArea.getValue()))
					.add(unitBuiltUpArea.getValue().multiply(rateAsPerBuiltUpArea.getValue()))
					.add(unitSuperBuiltUp.getValue().multiply(rateAsPerSuperBuiltUpArea.getValue())));

		} else {
			price.setValue(BigDecimal.ZERO);
		}
		totalPrice.setValue(price.getActualValue().add(getTotalPrice()));
		logger.debug(Literal.LEAVING);
	}

	public void onChangeRate() {
		logger.debug(Literal.ENTERING);
		if (unitArea.getValue() != null && rate.getValue() != null) {
			price.setValue(new BigDecimal(unitArea.getValue() * rate.getValue())
					.add(carpetArea.getValue().multiply(rateAsPerCarpetArea.getValue()))
					.add(unitBuiltUpArea.getValue().multiply(rateAsPerBuiltUpArea.getValue()))
					.add(unitSuperBuiltUp.getValue().multiply(rateAsPerSuperBuiltUpArea.getValue())));
		} else {
			price.setValue(BigDecimal.ZERO);
		}
		totalPrice.setValue(price.getActualValue().add(getTotalPrice()));
		logger.debug(Literal.LEAVING);
	}

	public void onChange$carpetArea(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		if (this.carpetArea.getValue() != null && this.carpetArea.getValue().compareTo(BigDecimal.ZERO) > 0
				&& this.rateConsidered.getSelectedItem().getValue()
						.equals(PennantStaticListUtil.geRateConsidered().get(0).getValue())) {
			price.setValue(carpetArea.getValue().multiply(rateAsPerCarpetArea.getValue())
					.add(new BigDecimal(unitArea.getValue() * rate.getValue())));

		} else {
			price.setValue(new BigDecimal(unitArea.getValue() * rate.getValue()));
		}
		totalPrice.setValue(price.getActualValue().add(getTotalPrice()));
		logger.debug(Literal.LEAVING + event.toString());

	}

	public void onChange$unitBuiltUpArea(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		if (this.unitBuiltUpArea.getValue() != null && this.unitBuiltUpArea.getValue().compareTo(BigDecimal.ZERO) > 0
				&& this.rateConsidered.getSelectedItem().getValue()
						.equals(PennantStaticListUtil.geRateConsidered().get(1).getValue())) {
			price.setValue(unitBuiltUpArea.getValue().multiply(rateAsPerBuiltUpArea.getValue())
					.add(new BigDecimal(unitArea.getValue() * rate.getValue())));

		} else {
			price.setValue(new BigDecimal(unitArea.getValue() * rate.getValue()));
		}
		totalPrice.setValue(price.getActualValue().add(getTotalPrice()));
		logger.debug(Literal.LEAVING + event.toString());

	}

	public void onChange$unitSuperBuiltUp(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		if (this.unitSuperBuiltUp.getValue() != null && this.unitSuperBuiltUp.getValue().compareTo(BigDecimal.ZERO) > 0
				&& this.rateConsidered.getSelectedItem().getValue()
						.equals(PennantStaticListUtil.geRateConsidered().get(2).getValue())) {
			price.setValue(unitSuperBuiltUp.getValue().multiply(rateAsPerSuperBuiltUpArea.getValue())
					.add(new BigDecimal(unitArea.getValue() * rate.getValue())));

		} else {
			price.setValue(new BigDecimal(unitArea.getValue() * rate.getValue()));
		}
		totalPrice.setValue(price.getActualValue().add(getTotalPrice()));
		logger.debug(Literal.LEAVING + event.toString());

	}

	public void onChange$rateAsPerCarpetArea(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		if (this.rateAsPerCarpetArea.getValue() != null
				&& this.rateAsPerCarpetArea.getValue().compareTo(BigDecimal.ZERO) > 0
				&& this.unitAreaConsidered.getSelectedItem().getValue()
						.equals(PennantStaticListUtil.getUnitAreaConsidered().get(0).getValue())) {
			price.setValue(carpetArea.getValue().multiply(rateAsPerCarpetArea.getValue())
					.add(new BigDecimal(unitArea.getValue() * rate.getValue())));

		} else {
			price.setValue(new BigDecimal(unitArea.getValue() * rate.getValue()));
		}
		totalPrice.setValue(price.getActualValue().add(getTotalPrice()));
		logger.debug(Literal.LEAVING + event.toString());

	}

	public void onChange$rateAsPerBuiltUpArea(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		if (this.rateAsPerBuiltUpArea.getValue() != null
				&& this.rateAsPerBuiltUpArea.getValue().compareTo(BigDecimal.ZERO) > 0
				&& this.unitAreaConsidered.getSelectedItem().getValue()
						.equals(PennantStaticListUtil.getUnitAreaConsidered().get(1).getValue())) {
			price.setValue(unitBuiltUpArea.getValue().multiply(rateAsPerBuiltUpArea.getValue())
					.add(new BigDecimal(unitArea.getValue() * rate.getValue())));

		} else {
			price.setValue(new BigDecimal(unitArea.getValue() * rate.getValue()));

		}
		totalPrice.setValue(price.getActualValue().add(getTotalPrice()));
		logger.debug(Literal.LEAVING + event.toString());

	}

	public void onChange$rateAsPerSuperBuiltUpArea(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		if (this.rateAsPerSuperBuiltUpArea.getValue() != null
				&& this.rateAsPerSuperBuiltUpArea.getValue().compareTo(BigDecimal.ZERO) > 0
				&& this.unitAreaConsidered.getSelectedItem().getValue()
						.equals(PennantStaticListUtil.getUnitAreaConsidered().get(2).getValue())) {
			price.setValue(unitSuperBuiltUp.getValue().multiply(rateAsPerSuperBuiltUpArea.getValue())
					.add(new BigDecimal(unitArea.getValue() * rate.getValue())));

		} else {
			price.setValue(new BigDecimal(unitArea.getValue() * rate.getValue()));

		}
		totalPrice.setValue(price.getActualValue().add(getTotalPrice()));
		logger.debug(Literal.LEAVING + event.toString());

	}

	public BigDecimal getTotalPrice() {
		logger.debug(Literal.ENTERING);
		BigDecimal totalPrice = BigDecimal.ZERO;
		if (floorRiseCharges.getActualValue().compareTo(BigDecimal.ZERO) > 0) {
			totalPrice = floorRiseCharges.getActualValue()
					.add(openCarParkingCharges.getActualValue().add(closedCarParkingCharges.getActualValue()
							.add(otherCharges.getActualValue()).add(gst.getActualValue())));
		} else {
			totalPrice = openCarParkingCharges.getActualValue().add(closedCarParkingCharges.getActualValue()
					.add(otherCharges.getActualValue()).add(gst.getActualValue()));

		}
		logger.debug(Literal.LEAVING);
		return totalPrice;
	}

	public void onFulfillPrice() {
		logger.debug(Literal.ENTERING);
		totalPrice.setValue(price.getActualValue().add(getTotalPrice()));
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$floorRiseCharges(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		totalPrice.setValue(price.getActualValue().add(getTotalPrice()));
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onFulfill$openCarParkingCharges(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		totalPrice.setValue(price.getActualValue().add(getTotalPrice()));
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onFulfill$closedCarParkingCharges(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		totalPrice.setValue(price.getActualValue().add(getTotalPrice()));
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onFulfillOtherCharges() {
		logger.debug(Literal.ENTERING);
		totalPrice.setValue(price.getActualValue().add(getTotalPrice()));
		logger.debug(Literal.ENTERING);
	}

	public void onFulfill$gst(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		totalPrice.setValue(price.getActualValue().add(getTotalPrice()));
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onChange$unitType(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		this.unitType.setErrorMessage("");
		String unitType = this.unitType.getSelectedItem().getValue();
		visibleComponent(unitType);
		getTotalPrice();
		totalPrice.setValue(price.getActualValue().add(getTotalPrice()));
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void visibleComponent(String flat) {
		if (StringUtils.equals(flat, PennantConstants.FLAT)) {
			this.space_tower.setSclass("mandatory");
			this.space_tower.setVisible(true);
			this.space_floornumber.setSclass("mandatory");
			this.space_floornumber.setVisible(true);
			this.unitPlotArea.setDisabled(true);
			this.unitPlotArea.setValue(BigDecimal.ZERO);
			this.floorRiseCharges.setDisabled(false);

		} else {
			this.space_tower.setSclass("");
			this.space_tower.setVisible(false);
			this.space_floornumber.setSclass("");
			this.space_floornumber.setVisible(false);
			this.tower.setErrorMessage("");
			this.floorNumber.setErrorMessage("");
			this.unitPlotArea.setDisabled(false);
			this.floorRiseCharges.setDisabled(true);
			this.floorRiseCharges.setValue(BigDecimal.ZERO);

		}
	}

	private AuditHeader doValidation(final ProjectUnits projectUnitsExist, final ProjectUnits projectUnitsNew,
			AuditHeader auditHeader) {
		String[] valueParm = new String[4];
		String unitType = projectUnitsNew.getUnitType();
		String floorNumber = projectUnitsNew.getFloorNumber();
		String tower = projectUnitsNew.getTower();
		String unitNumber = projectUnitsNew.getUnitNumber();
		if (doCheckUniqueConstraint(projectUnitsExist, projectUnitsNew)) {
			if (isNewRecord()) {
				valueParm[0] = unitType;
				valueParm[1] = tower;
				valueParm[2] = floorNumber;
				valueParm[3] = unitNumber;
				auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail("PROJU001", valueParm)));
				return auditHeader;
			}
		}
		return auditHeader;
	}

	public boolean isReadOnly(String componentName) {
		if (enqiryModule) {
			return true;
		} else if (isWorkFlowEnabled()) {
			return getUserWorkspace().isReadOnly(componentName);
		} else {
			return getUserWorkspace().isReadOnly(componentName);
		}
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public boolean isFromParent() {
		return fromParent;
	}

	public void setFromParent(boolean fromParent) {
		this.fromParent = fromParent;
	}

}

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
 * FileName    		:  PFSParameterDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-07-2011    														*
 *                                                                  						*
 * Modified Date    :  12-07-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-07-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.smtmasters.pfsparameter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.model.staticparms.Language;
import com.pennant.backend.service.smtmasters.PFSParameterService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/PFSParameter/pFSParameterDialog.zul file.
 */
public class PFSParameterDialogCtrl extends GFCBaseCtrl<PFSParameter> {
	private static final long serialVersionUID = 5922960172101690001L;
	private static final Logger logger = Logger.getLogger(PFSParameterDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_PFSParameterDialog; // autowired

	protected Textbox  sysParmCode;  	  	// autowired
	protected Textbox  sysParmDesc; 	  	// autowired
	protected Textbox  sysParmValue; 	  	// autowired
	protected Textbox  txtParmValue; 	  	// autowired
	protected Combobox comboParmValue; 	  	// autowired
	protected Textbox  sysParmDescription; 	// autowired
	
	protected Space paramValueSpace;		// autowired
	protected Space paramDescSpace;			// autowired
	protected Space paramShortDescSpace;	// autowired
	protected Space paramCodeSpace;			// autowired

	// Language variables
	protected Textbox txtLanguageParmValue; // autowired
	protected Textbox lovDescLanguageName;
	protected Button  btnSearchLanguage; 	// autowire

	// Decimal variables
	protected Decimalbox doubleParamValue; // autowired

	// Date variables
	protected Datebox dateParamValue;
	private SimpleDateFormat dateformat = new SimpleDateFormat(PennantConstants.DBDateFormat);
	protected Row sysParmValueRow;

	// not auto wired vars
	private PFSParameter pFSParameter; // overhanded per param
	private transient PFSParameterListCtrl pFSParameterListCtrl; // overhanded per param

	private transient String  sysParmType;
	private transient int 	  sysParmLength;
	private transient int 	  sysParmDec;
	private transient String  sysParmList;
	private transient int 	  parmType = 0;

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient PFSParameterService systemParameterService;
	private List<ValueLabel> listSysParmType = null; // autowired

	/**
	 * default constructor.<br>
	 */
	public PFSParameterDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "PFSParameterDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected PFSParameter object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_PFSParameterDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_PFSParameterDialog);

		try {

			// READ OVERHANDED params !
			if (arguments.containsKey("pFSParameter")) {
				this.pFSParameter = (PFSParameter) arguments.get("pFSParameter");
				PFSParameter befImage = new PFSParameter();
				BeanUtils.copyProperties(this.pFSParameter, befImage);
				this.pFSParameter.setBefImage(befImage);
				setPFSParameter(this.pFSParameter);
			} else {
				setPFSParameter(null);
			}

			// READ OVERHANDED params !
			// we get the pFSParameterListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete pFSParameter here.
			if (arguments.containsKey("pFSParameterListCtrl")) {
				setPFSParameterListCtrl((PFSParameterListCtrl) arguments.get("pFSParameterListCtrl"));
			} else {
				setPFSParameterListCtrl(null);
			}

			doLoadWorkFlow(this.pFSParameter.isWorkflow(), this.pFSParameter.getWorkflowId(),
					this.pFSParameter.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "PFSParameterDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getPFSParameter());
			
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_PFSParameterDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering ");
		// Empty sent any required attributes
		this.sysParmCode.setMaxlength(50);
		this.sysParmDesc.setMaxlength(100);
		this.txtParmValue.setVisible(false);
		this.comboParmValue.setVisible(false);
		this.txtLanguageParmValue.setVisible(false);
		this.doubleParamValue.setVisible(false);
		this.doubleParamValue.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.doubleParamValue.setScale(0);
		this.txtLanguageParmValue.setMaxlength(2);
		this.dateParamValue.setVisible(false);
		this.dateParamValue.setFormat(DateFormat.SHORT_DATE.getPattern());

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving ");
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
		logger.debug("Entering ");
		
		getUserWorkspace().allocateAuthorities(super.pageRightName, getRole());
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_PFSParameterDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_PFSParameterDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_PFSParameterDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_PFSParameterDialog_btnSave"));
		this.btnCancel.setVisible(false);
		
		logger.debug("Leaving ");
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
	 * @throws ParseException 
	 */
	public void onClick$btnEdit(Event event) throws ParseException {
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
		MessageUtil.showHelpWindow(event, window_PFSParameterDialog);
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
	 * @throws ParseException 
	 */
	public void onClick$btnCancel(Event event) throws ParseException {
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
	 * @throws ParseException 
	 * 
	 */
	private void doCancel() throws ParseException {
		logger.debug("Entering ");
		doWriteBeanToComponents(this.pFSParameter.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aPFSParameter
	 *            PFSParameter
	 * @throws ParseException 
	 */
	public void doWriteBeanToComponents(PFSParameter aPFSParameter) throws ParseException {
		logger.debug("Entering ");
		this.sysParmType = aPFSParameter.getSysParmType();
		this.sysParmValue.setValue(aPFSParameter.getSysParmValue());
		this.sysParmCode.setValue(aPFSParameter.getSysParmCode());
		this.sysParmDesc.setValue(aPFSParameter.getSysParmDesc());
		this.sysParmDescription.setValue(aPFSParameter.getSysParmDescription());
		this.recordStatus.setValue(aPFSParameter.getRecordStatus());
		this.sysParmList = aPFSParameter.getSysParmList();
		this.sysParmLength = aPFSParameter.getSysParmLength();
		this.sysParmDec = aPFSParameter.getSysParmDec();
		
		// Default txtParmValue
		if ("String".equalsIgnoreCase(StringUtils.trimToEmpty(this.sysParmType))
				&& StringUtils.isNotBlank(aPFSParameter.getSysParmList())
				&& StringUtils.trimToEmpty(aPFSParameter.getSysParmList()).contains("MOD_")) {

			parmType = 1; // combo box

		} else if ("Double".equalsIgnoreCase(StringUtils.trimToEmpty(this.sysParmType))) {

			parmType = 2; // Decimal box

		} else if ("List".equalsIgnoreCase(StringUtils.trimToEmpty(this.sysParmType))) {

			parmType = 3; // Listbox

		} else if ("String".equalsIgnoreCase(StringUtils.trimToEmpty(this.sysParmType))
				&& StringUtils.isNotBlank(aPFSParameter.getSysParmList())) {

			parmType = 4; // Textbox

		} else if ("Date".equalsIgnoreCase(StringUtils.trimToEmpty(this.sysParmType))) {

			parmType = 5; // Datebox

		}
		 
		switch (this.parmType) {
		case 1:

			this.comboParmValue.setVisible(true);
			this.comboParmValue.setReadonly(false);
			this.comboParmValue.setMaxlength(3);
			this.txtParmValue.setDisabled(true);
			this.txtLanguageParmValue.setDisabled(true);
			this.lovDescLanguageName.setDisabled(true);
			this.btnSearchLanguage.setDisabled(true);
			this.doubleParamValue.setDisabled(true);
			this.dateParamValue.setDisabled(true);
			setListSysParmType();
			break;

		case 2:

			this.doubleParamValue.setVisible(true);
			this.doubleParamValue.setReadonly(false);
			this.doubleParamValue.setMaxlength(this.sysParmLength);
			if (this.sysParmDec == 0) {
				this.doubleParamValue.setFormat(PennantApplicationUtil.getAmountFormate(0));
				this.doubleParamValue.setScale(0);
			} else if (this.sysParmDec == 1) {
				this.doubleParamValue.setFormat(PennantApplicationUtil.getAmountFormate(1));
				this.doubleParamValue.setScale(1);
			} else if (this.sysParmDec == 2) {
				this.doubleParamValue.setFormat(PennantApplicationUtil.getAmountFormate(2));
				this.doubleParamValue.setScale(2);
			} else if (this.sysParmDec == 3) {
				this.doubleParamValue.setFormat(PennantApplicationUtil.getAmountFormate(3));
				this.doubleParamValue.setScale(3);
			}
			this.doubleParamValue.setValue(this.sysParmValue.getValue());
			this.comboParmValue.setDisabled(true);
			this.txtLanguageParmValue.setDisabled(true);
			this.lovDescLanguageName.setDisabled(true);
			this.btnSearchLanguage.setDisabled(true);
			this.txtParmValue.setDisabled(true);
			this.dateParamValue.setDisabled(true);
			break;

		case 3:

			this.txtLanguageParmValue.setValue(this.sysParmValue.getValue());
			this.lovDescLanguageName.setValue(this.sysParmValue.getValue());
			this.lovDescLanguageName.setMaxlength(this.sysParmLength);
			this.lovDescLanguageName.setVisible(true);
			this.btnSearchLanguage.setVisible(true);
			this.txtParmValue.setDisabled(true);
			this.comboParmValue.setDisabled(true);
			this.doubleParamValue.setDisabled(true);
			this.dateParamValue.setDisabled(true);
			break;

		case 4:

			this.txtParmValue.setVisible(true);
			this.txtParmValue.setValue(this.sysParmValue.getValue());
			this.txtParmValue.setMaxlength(this.sysParmLength);
			this.comboParmValue.setDisabled(true);
			this.txtLanguageParmValue.setDisabled(true);
			this.lovDescLanguageName.setDisabled(true);
			this.btnSearchLanguage.setDisabled(true);
			this.doubleParamValue.setDisabled(true);
			this.dateParamValue.setDisabled(true);
			break;

		case 5:

			this.dateParamValue.setVisible(true);
			this.dateParamValue.setDisabled(false);
			try {
				this.dateParamValue.setValue((Date) dateformat.parse(this.sysParmValue.getValue()));
			} catch (WrongValueException e) {
				throw e;
			} catch (ParseException e) {
				throw e;
			}

			this.txtParmValue.setDisabled(true);
			this.comboParmValue.setDisabled(true);
			this.txtLanguageParmValue.setDisabled(true);
			this.lovDescLanguageName.setDisabled(true);
			this.btnSearchLanguage.setDisabled(true);
			this.doubleParamValue.setDisabled(true);
			break;

		default:

			this.txtParmValue.setVisible(true);
			//this.txtParmValue.setReadonly(false);
			this.txtParmValue.setMaxlength(this.sysParmLength);
			this.txtParmValue.setValue(this.sysParmValue.getValue());
			this.comboParmValue.setDisabled(true);
			this.txtLanguageParmValue.setDisabled(true);
			this.lovDescLanguageName.setDisabled(true);
			this.btnSearchLanguage.setDisabled(true);
			this.doubleParamValue.setDisabled(true);
			break;

		}

		logger.debug("Leaving ");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aPFSParameter
	 */
	public void doWriteComponentsToBean(PFSParameter aPFSParameter) {
		logger.debug("Entering ");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (StringUtils.isBlank(this.sysParmCode.getValue())) {
				throw new WrongValueException(this.sysParmCode,Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_PFSParameterDialog_SysParmCode.value") }));
			}
			aPFSParameter.setSysParmCode(this.sysParmCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (StringUtils.isBlank(this.sysParmDesc.getValue())) {
				throw new WrongValueException(this.sysParmDesc,Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_PFSParameterDialog_SysParmDesc.value") }));
			}
			aPFSParameter.setSysParmDesc(this.sysParmDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.txtParmValue.isReadonly() && 
					StringUtils.isNotBlank(this.txtParmValue.getValue())) {
				
				this.sysParmValue.setValue(this.txtParmValue.getValue());
				
			} else if (!this.comboParmValue.isDisabled()
					&& this.comboParmValue.getChildren().size() > 0) {
				if (!StringUtils.isBlank(
						this.comboParmValue.getSelectedItem().getValue().toString())) {
					
					this.sysParmValue.setValue(this.comboParmValue.getSelectedItem().getValue().toString());
					
				}
			} else if (!this.lovDescLanguageName.isDisabled() && 
					!StringUtils.isBlank(this.lovDescLanguageName.getValue())) {
				
				this.sysParmValue.setValue(this.txtLanguageParmValue.getValue());
				
			} else if (!this.doubleParamValue.isDisabled()
					&& this.doubleParamValue.getValue() != null) {
				
				this.sysParmValue.setValue(this.doubleParamValue.getValue().toString());
				
				if ("CID_RETAIN_PRD".equals(this.sysParmCode.getValue())
						|| "CIF_LENGTH".equals(this.sysParmCode.getValue())
						|| "LOAN_RETAIL_PRD".equals(this.sysParmCode.getValue())) {
					
					if (Integer.parseInt(this.sysParmValue.getValue()) <= 0) {
						
						throw new WrongValueException(this.doubleParamValue,
								Labels.getLabel("const_const_NO_NEGATIVE_ZERO",
								new String[] { Labels.getLabel("label_PFSParameterDialog_SysParmValue.value") }));
						
					}
				}
			} else if (this.dateParamValue.getValue() != null) {
				this.sysParmValue.setValue(dateformat.format(this.dateParamValue.getValue()));
			}
			if (StringUtils.isBlank(this.sysParmValue.getValue())) {
				
				throw new WrongValueException(this.sysParmValue,Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_PFSParameterDialog_SysParmValue.value") }));
				
			}
			aPFSParameter.setSysParmValue(this.sysParmValue.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (StringUtils.isBlank(this.sysParmDescription.getValue())) {
				
				throw new WrongValueException(this.sysParmDescription,Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_PFSParameterDialog_SysParmDescription.value") }));
				
			}
			aPFSParameter.setSysParmDescription(this.sysParmDescription.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aPFSParameter.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving ");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aPFSParameter
	 * @throws Exception
	 *
	 */
	public void doShowDialog(PFSParameter aPFSParameter) throws Exception {
		logger.debug("Entering");

		// set Readonly mode accordingly if the object is new or not.
		if (aPFSParameter.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.sysParmCode.focus();
		} else {
			this.sysParmDesc.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aPFSParameter);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_PFSParameterDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving ");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering ");
		setValidationOn(true);

		if (!this.sysParmCode.isReadonly()) {
			this.sysParmCode.setConstraint(new PTStringValidator( Labels.getLabel("label_PFSParameterDialog_SysParmCode.value"),PennantRegularExpressions.REGEX_ALPHA_CODE,true));
		}
		if (!this.sysParmDesc.isReadonly()) {
			this.sysParmDesc.setConstraint(new PTStringValidator( Labels.getLabel("label_PFSParameterDialog_SysParmDesc.value"),null,true));
		}
		if (!this.sysParmValue.isReadonly()) {
			this.sysParmValue.setConstraint(new PTStringValidator(Labels.getLabel("label_PFSParameterDialog_SysParmValue.value"),null,true ));
		}
		if (!this.comboParmValue.isDisabled()) {
			this.comboParmValue.setConstraint(new StaticListValidator(listSysParmType,
					Labels.getLabel("label_PFSParameterDialog_SysParmValue.value")));
		}
		if (!this.txtParmValue.isDisabled()) {
			this.txtParmValue.setConstraint(new PTStringValidator(Labels.getLabel("label_PFSParameterDialog_SysParmValue.value"),null,true ));
		}
		if (!this.doubleParamValue.isDisabled()) {
			this.doubleParamValue.setConstraint(new PTStringValidator(Labels.getLabel("label_PFSParameterDialog_SysParmValue.value"),null,true ));
		}
		if (!this.dateParamValue.isReadonly()) {
			this.dateParamValue.setConstraint(new PTDateValidator(Labels.getLabel("label_PFSParameterDialog_SysParmValue.value"),true));
		}
		if (!this.sysParmDescription.isReadonly()) {
			this.sysParmDescription.setConstraint(new PTStringValidator(Labels.getLabel("label_PFSParameterDialog_SysParmDescription.value"),null,true));
		}
		logger.debug("Leaving ");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering ");
		setValidationOn(false);
		this.sysParmCode.setConstraint("");
		this.sysParmDesc.setConstraint("");
		this.sysParmDescription.setConstraint("");
		this.comboParmValue.setConstraint("");
		this.txtParmValue.setConstraint("");
		this.doubleParamValue.setConstraint("");
		this.dateParamValue.setConstraint("");
		logger.debug("Leaving ");
	}

	// CRUD operations

	/**
	 * Deletes a PFSParameter object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering ");
		
		final PFSParameter aPFSParameter = new PFSParameter();
		BeanUtils.copyProperties(getPFSParameter(), aPFSParameter);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")+ "\n\n --> " + 
				Labels.getLabel("label_PFSParameterDialog_SysParmCode.value")+" : "+aPFSParameter.getSysParmCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aPFSParameter.getRecordType())) {
				aPFSParameter.setVersion(aPFSParameter.getVersion() + 1);
				aPFSParameter.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aPFSParameter.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aPFSParameter, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}

		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the components for edit mode. <br>
	 * @throws ParseException 
	 */
	private void doEdit() throws ParseException {
		logger.debug("Entering ");
		this.paramDescSpace.setVisible(true);
		this.paramValueSpace.setVisible(true);
		this.paramCodeSpace.setVisible(true);
		this.paramShortDescSpace.setVisible(true);
		if (getPFSParameter().isNewRecord()) {
			this.sysParmCode.setReadonly(false);
			this.sysParmDesc.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.sysParmCode.setReadonly(true);
			this.sysParmDesc.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		
		this.sysParmValue.setVisible(false);
		this.sysParmValue.setReadonly(isReadOnly("PFSParameterDialog_sysParmValue"));
		this.txtParmValue.setReadonly(isReadOnly("PFSParameterDialog_sysParmValue"));
		this.comboParmValue.setReadonly(isReadOnly("PFSParameterDialog_sysParmValue"));
		this.txtLanguageParmValue.setReadonly(isReadOnly("PFSParameterDialog_sysParmValue"));
		this.lovDescLanguageName.setReadonly(isReadOnly("PFSParameterDialog_sysParmValue"));
		this.doubleParamValue.setReadonly(isReadOnly("PFSParameterDialog_sysParmValue"));
		this.dateParamValue.setReadonly(isReadOnly("PFSParameterDialog_sysParmValue"));
		this.sysParmDescription.setReadonly(isReadOnly("PFSParameterDialog_sysParmDescription"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.pFSParameter.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}

		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering ");
		this.sysParmCode.setReadonly(true);
		this.sysParmDesc.setReadonly(true);
		this.sysParmValue.setReadonly(true);
		this.sysParmDescription.setReadonly(true);
		this.doubleParamValue.setReadonly(true);
		this.dateParamValue.setDisabled(true);
		this.txtLanguageParmValue.setReadonly(true);
		this.txtParmValue.setReadonly(true);
		this.comboParmValue.setReadonly(true);
		this.btnSearchLanguage.setDisabled(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering ");
		// remove validation, if there are a save before

		this.sysParmCode.setValue("");
		this.sysParmDesc.setValue("");
		this.sysParmValue.setValue("");
		this.sysParmDescription.setValue("");
		this.txtLanguageParmValue.setValue("");
		this.lovDescLanguageName.setValue("");
		logger.debug("Leaving ");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering ");
		
		final PFSParameter aPFSParameter = new PFSParameter();
		BeanUtils.copyProperties(getPFSParameter(), aPFSParameter);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the PFSParameter object with the components data
		doWriteComponentsToBean(aPFSParameter);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aPFSParameter.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			if (StringUtils.isBlank(aPFSParameter.getRecordType())) {
				aPFSParameter.setVersion(aPFSParameter.getVersion() + 1);
				if (isNew) {
					aPFSParameter.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aPFSParameter.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aPFSParameter.setNewRecord(true);
				}
			}
		} else {
			aPFSParameter.setVersion(aPFSParameter.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aPFSParameter, tranType)) {
				
				//Parameter Updation in Map Details
				SysParamUtil.updateParamDetails(aPFSParameter.getSysParmCode(), aPFSParameter.getSysParmValue());
				
				refreshList();
				// Close the Existing Dialog
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aPFSParameter
	 *            (PFSParameter)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(PFSParameter aPFSParameter, String tranType) {
		logger.debug("Entering ");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aPFSParameter.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aPFSParameter.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aPFSParameter.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aPFSParameter.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aPFSParameter
						.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aPFSParameter);
				}

				if (isNotesMandatory(taskId, aPFSParameter)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isNotBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();

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

			aPFSParameter.setTaskId(taskId);
			aPFSParameter.setNextTaskId(nextTaskId);
			aPFSParameter.setRoleCode(getRole());
			aPFSParameter.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aPFSParameter, tranType);

			String operationRefs = getServiceOperations(taskId, aPFSParameter);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aPFSParameter, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
				}
			}
		} else {

			auditHeader = getAuditHeader(aPFSParameter, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving ");
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
		logger.debug("Entering ");
		
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		PFSParameter aPFSParameter = (PFSParameter) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = systemParameterService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = systemParameterService.saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = systemParameterService.doApprove(auditHeader);
						if (aPFSParameter.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = systemParameterService.doReject(auditHeader);
						if (aPFSParameter.getRecordType().equals(
								PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(
								this.window_PFSParameterDialog, auditHeader);
						return processCompleted;
					}
				}

				retValue = ErrorControl.showErrorControl(
						this.window_PFSParameterDialog, auditHeader);

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.pFSParameter), true);
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
		logger.debug("Leaving ");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(PFSParameter aPFSParameter,
			String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aPFSParameter.getBefImage(), aPFSParameter);
		return new AuditHeader(String.valueOf(aPFSParameter.getId()), null,
				null, null, auditDetail, aPFSParameter.getUserDetails(),
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
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_PFSParameterDialog,
					auditHeader);
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
	  doShowNotes(this.pFSParameter);
	}

	private void doSetLOVValidation() {
		this.lovDescLanguageName.setConstraint(new PTStringValidator(Labels.getLabel(
				"label_PFSParameterDialog_SysParmValue.value"),null,true));
	}

	private void doRemoveLOVValidation() {
		this.lovDescLanguageName.setConstraint("");
	}

	
	@Override
	protected String getReference() {
		return String.valueOf(this.pFSParameter.getSysParmCode());
	}

	/*
	 * Method to populate the parameter values in combo box based on the
	 * parameter list type.
	 */
	private void setListSysParmType() {
		logger.debug("Entering");

		if ("MOD_YESNO".equals(this.sysParmList)) {
			listSysParmType = PennantStaticListUtil.getYesNo();
		} else if ("MOD_CBCID".equals(this.sysParmList)) {
			listSysParmType = PennantStaticListUtil.getMOD_CBCID();
		}

		if (listSysParmType != null && listSysParmType.size() > 0) {
			for (int i = 0; i < listSysParmType.size(); i++) {
				Comboitem comboitem = new Comboitem();
				comboitem = new Comboitem();
				comboitem.setLabel(listSysParmType.get(i).getLabel());
				comboitem.setValue(listSysParmType.get(i).getValue());
				this.comboParmValue.appendChild(comboitem);
				if (this.sysParmValue.getValue().equals(listSysParmType.get(i).getValue())) {
					this.comboParmValue.setSelectedItem(comboitem);
				}
			}
		}

		logger.debug("Leaving");
	}

	/*
	 * Method to populate the language values.
	 */
	public void onClick$btnSearchLanguage(Event event) {
		logger.debug("Entering ");
		Object dataObject = ExtendedSearchListBox.show(
				this.window_PFSParameterDialog, "Language");
		if (dataObject instanceof String) {
			this.txtLanguageParmValue.setValue(dataObject.toString());
			this.lovDescLanguageName.setValue("");
		} else {
			Language details = (Language) dataObject;
			if (details != null) {
				this.txtLanguageParmValue.setValue(details.getLngCode());
				// this.lovDescLanguageName.setValue(details.getLngCode()+"-"+details.getLngDesc());
				this.lovDescLanguageName.setValue(details.getLngCode());
			}
		}
		logger.debug("Leaving ");
	}
	
	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getPFSParameterListCtrl().search();
		
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

	public PFSParameter getPFSParameter() {
		return this.pFSParameter;
	}
	public void setPFSParameter(PFSParameter pFSParameter) {
		this.pFSParameter = pFSParameter;
	}
	
	public void setPFSParameterListCtrl(
			PFSParameterListCtrl pFSParameterListCtrl) {
		this.pFSParameterListCtrl = pFSParameterListCtrl;
	}
	public PFSParameterListCtrl getPFSParameterListCtrl() {
		return this.pFSParameterListCtrl;
	}

	public void setSystemParameterService(PFSParameterService systemParameterService) {
		this.systemParameterService = systemParameterService;
	}

}

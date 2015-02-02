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

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.model.staticparms.Language;
import com.pennant.backend.service.smtmasters.PFSParameterService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.SystemParameterUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/PFSParameter/pFSParameterDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class PFSParameterDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 5922960172101690001L;

	private final static Logger logger = Logger.getLogger(PFSParameterDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
	private transient BigDecimal oldVar_doubleParamValue;

	// Date variables
	protected Datebox dateParamValue;
	private transient Date oldVar_dateParamValue;
	private SimpleDateFormat dateformat = new SimpleDateFormat(PennantConstants.DBDateFormat);

	protected Label recordStatus; // autowired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	protected Row sysParmValueRow;

	// not auto wired vars
	private PFSParameter pFSParameter; // overhanded per param
	private transient PFSParameterListCtrl pFSParameterListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_sysParmCode;
	private transient String oldVar_sysParmDesc;
	private transient String oldVar_sysParmValue;
	private transient String oldVar_sysParmDescription;
	private transient String oldVar_recordStatus;
	private transient String oldVar_comboParmValue;
	private transient String oldVar_txtParmValue;
	private transient String oldVar_txtLanguageParmValue;
	private transient String oldVar_lovDescLanguageName;

	private transient String  sysParmType;
	private transient int 	  sysParmLength;
	private transient int 	  sysParmDec;
	private transient String  sysParmList;
	private transient int 	  parmType = 0;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_PFSParameterDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 	// autowire
	protected Button btnEdit; 	// autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; 	// autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; 	// autowire
	protected Button btnHelp; 	// autowire
	protected Button btnNotes; 	// autowire

	// ServiceDAOs / Domain Classes
	private transient PFSParameterService pFSParameterService;
	private List<ValueLabel> listSysParmType = null; // autowired

	/**
	 * default constructor.<br>
	 */
	public PFSParameterDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected PFSParameter object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_PFSParameterDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("pFSParameter")) {
			this.pFSParameter = (PFSParameter) args.get("pFSParameter");
			PFSParameter befImage = new PFSParameter();
			BeanUtils.copyProperties(this.pFSParameter, befImage);
			this.pFSParameter.setBefImage(befImage);
			setPFSParameter(this.pFSParameter);
		} else {
			setPFSParameter(null);
		}

		doLoadWorkFlow(this.pFSParameter.isWorkflow(),
				this.pFSParameter.getWorkflowId(), this.pFSParameter.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "PFSParameterDialog");
		}

		// READ OVERHANDED params !
		// we get the pFSParameterListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete pFSParameter here.
		if (args.containsKey("pFSParameterListCtrl")) {
			setPFSParameterListCtrl((PFSParameterListCtrl) args.get("pFSParameterListCtrl"));
		} else {
			setPFSParameterListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getPFSParameter());
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
		this.dateParamValue.setFormat(PennantConstants.dateFormat);

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
		
		getUserWorkspace().alocateAuthorities("PFSParameterDialog");
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_PFSParameterDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_PFSParameterDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_PFSParameterDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_PFSParameterDialog_btnSave"));
		this.btnCancel.setVisible(false);
		
		logger.debug("Leaving ");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_PFSParameterDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
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
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 * @throws ParseException 
	 */
	public void onClick$btnEdit(Event event) throws ParseException {
		logger.debug("Entering" + event.toString());
		doEdit();
		// remember the old vars
		doStoreInitValues();
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
		PTMessageUtils.showHelpWindow(event, window_PFSParameterDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 * @throws ParseException 
	 */
	public void onClick$btnNew(Event event) throws ParseException {
		logger.debug("Entering" + event.toString());
		doNew();
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
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		try {
			doClose();
		} catch (final WrongValueException e) {
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
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
		logger.debug("Entering ");
		boolean close = true;
		
		if (isDataChanged()) {
			logger.debug("Data Changed(): True");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("Data Changed(): false");
		}

		if (close) {
			closeDialog(this.window_PFSParameterDialog, "PFSParameter");
		}
		logger.debug("Leaving ");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering ");
		doResetInitValues();
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
	 */
	public void doWriteBeanToComponents(PFSParameter aPFSParameter) {
		logger.debug("Entering ");
		this.sysParmCode.setValue(aPFSParameter.getSysParmCode());
		this.sysParmDesc.setValue(aPFSParameter.getSysParmDesc());
		this.sysParmDescription.setValue(aPFSParameter.getSysParmDescription());
		this.sysParmValue.setValue(aPFSParameter.getSysParmValue());
		this.recordStatus.setValue(aPFSParameter.getRecordStatus());
		this.sysParmType = aPFSParameter.getSysParmType();
		this.sysParmList = aPFSParameter.getSysParmList();
		this.sysParmLength = aPFSParameter.getSysParmLength();
		this.sysParmDec = aPFSParameter.getSysParmDec();

		// Default txtParmValue
		if (StringUtils.trimToEmpty(this.sysParmType).equalsIgnoreCase("String")
				&& !StringUtils.trimToEmpty(aPFSParameter.getSysParmList()).equals("")
				&& StringUtils.trimToEmpty(aPFSParameter.getSysParmList()).contains("MOD_")) {
			
			parmType = 1; // combo box
			
		} else if (StringUtils.trimToEmpty(this.sysParmType).equalsIgnoreCase("Double")) {
			
			parmType = 2; // Decimal box
			
		} else if (StringUtils.trimToEmpty(this.sysParmType).equalsIgnoreCase("List")) {
			
			parmType = 3; // Listbox
			
		} else if (StringUtils.trimToEmpty(this.sysParmType).equalsIgnoreCase("String")
						&& !StringUtils.trimToEmpty(aPFSParameter.getSysParmList()).equals("")) {
			
			parmType = 4; // Textbox
			
		} else if (StringUtils.trimToEmpty(this.sysParmType).equalsIgnoreCase("Date")) {
			
			parmType = 5; // Datebox
			
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
			if (StringUtils.trimToEmpty(this.sysParmCode.getValue()).equals("")) {
				throw new WrongValueException(this.sysParmCode,Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_PFSParameterDialog_SysParmCode.value") }));
			}
			aPFSParameter.setSysParmCode(this.sysParmCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (StringUtils.trimToEmpty(this.sysParmDesc.getValue()).equals("")) {
				throw new WrongValueException(this.sysParmDesc,Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_PFSParameterDialog_SysParmDesc.value") }));
			}
			aPFSParameter.setSysParmDesc(this.sysParmDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.txtParmValue.isReadonly() && 
					!StringUtils.trimToEmpty(this.txtParmValue.getValue()).equals("")) {
				
				this.sysParmValue.setValue(this.txtParmValue.getValue());
				
			} else if (!this.comboParmValue.isDisabled()
					&& this.comboParmValue.getChildren().size() > 0) {
				if (!StringUtils.trimToEmpty(
						this.comboParmValue.getSelectedItem().getValue().toString()).equals("")) {
					
					this.sysParmValue.setValue(this.comboParmValue.getSelectedItem().getValue().toString());
					
				}
			} else if (!this.lovDescLanguageName.isDisabled() && 
					!StringUtils.trimToEmpty(this.lovDescLanguageName.getValue()).equals("")) {
				
				this.sysParmValue.setValue(this.txtLanguageParmValue.getValue());
				
			} else if (!this.doubleParamValue.isDisabled()
					&& this.doubleParamValue.getValue() != null) {
				
				this.sysParmValue.setValue(this.doubleParamValue.getValue().toString());
				
				if (this.sysParmCode.getValue().equals("CID_RETAIN_PRD")
						|| this.sysParmCode.getValue().equals("CIF_LENGTH")
						|| this.sysParmCode.getValue().equals("LOAN_RETAIL_PRD")) {
					
					if (Integer.parseInt(this.sysParmValue.getValue()) <= 0) {
						
						throw new WrongValueException(this.doubleParamValue,
								Labels.getLabel("const_const_NO_NEGATIVE_ZERO",
								new String[] { Labels.getLabel("label_PFSParameterDialog_SysParmValue.value") }));
						
					}
				}
			} else if (this.dateParamValue.getValue() != null) {
				this.sysParmValue.setValue(dateformat.format(this.dateParamValue.getValue()));
			}
			if (StringUtils.trimToEmpty(this.sysParmValue.getValue()).equals("")) {
				
				throw new WrongValueException(this.sysParmValue,Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_PFSParameterDialog_SysParmValue.value") }));
				
			}
			aPFSParameter.setSysParmValue(this.sysParmValue.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (StringUtils.trimToEmpty(this.sysParmDescription.getValue()).equals("")) {
				
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
	 * @throws InterruptedException
	 * @throws ParseException 
	 */
	public void doShowDialog(PFSParameter aPFSParameter) throws InterruptedException, ParseException {
		logger.debug("Entering ");
		// if aPFSParameter == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aPFSParameter == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aPFSParameter = getPFSParameterService().getNewPFSParameter();
			setPFSParameter(aPFSParameter);
		} else {
			setPFSParameter(aPFSParameter);
		}

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

			// stores the inital data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_PFSParameterDialog);
		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving ");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering ");
		this.oldVar_sysParmCode = this.sysParmCode.getValue();
		this.oldVar_sysParmDesc = this.sysParmDesc.getValue();
		this.oldVar_sysParmValue = this.sysParmValue.getValue();
		this.oldVar_sysParmDescription = this.sysParmDescription.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		this.oldVar_comboParmValue = this.comboParmValue.getValue().toString();
		this.oldVar_txtParmValue = this.txtParmValue.getValue();
		this.oldVar_txtLanguageParmValue = this.txtLanguageParmValue.getValue();
		this.oldVar_lovDescLanguageName = this.lovDescLanguageName.getValue();
		this.oldVar_doubleParamValue = this.doubleParamValue.getValue();
		this.oldVar_dateParamValue = this.dateParamValue.getValue();
		logger.debug("Leaving ");
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering ");
		this.sysParmCode.setValue(this.oldVar_sysParmCode);
		this.sysParmDesc.setValue(this.oldVar_sysParmDesc);
		this.sysParmValue.setVisible(true);
		this.sysParmValue.setMaxlength(this.sysParmLength);
		this.sysParmValue.setValue(this.oldVar_sysParmValue);
		this.comboParmValue.getChildren().clear();
		this.comboParmValue.setVisible(false);
		this.txtParmValue.setVisible(false);
		this.txtLanguageParmValue.setVisible(false);
		this.lovDescLanguageName.setVisible(false);
		this.btnSearchLanguage.setVisible(false);
		this.doubleParamValue.setVisible(false);
		this.dateParamValue.setVisible(false);
		this.paramDescSpace.setVisible(false);
		this.paramValueSpace.setVisible(false);
		this.paramCodeSpace.setVisible(false);
		this.paramShortDescSpace.setVisible(false);
		this.txtLanguageParmValue.setValue(this.oldVar_txtLanguageParmValue);
		this.lovDescLanguageName.setValue(this.oldVar_lovDescLanguageName);
		this.doubleParamValue.setValue(this.oldVar_doubleParamValue);
		this.sysParmDescription.setValue(this.oldVar_sysParmDescription);
		this.dateParamValue.setValue(this.oldVar_dateParamValue);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		
		if (this.oldVar_sysParmCode != this.sysParmCode.getValue()) {
			return true;
		}
		if (this.oldVar_sysParmDesc != this.sysParmDesc.getValue()) {
			return true;
		}
		if (this.oldVar_sysParmValue != this.sysParmValue.getValue()) {
			return true;
		}
		if (!this.oldVar_txtParmValue.equals(this.txtParmValue.getValue())) {
			return true;
		}
		if (!this.oldVar_comboParmValue.equals(this.comboParmValue.getValue().toString())) {
			return true;
		}
		if (!this.oldVar_txtLanguageParmValue.equals(this.txtLanguageParmValue.getValue())) {
			return true;
		}
		if (this.oldVar_doubleParamValue != this.doubleParamValue.getValue()) {
			return true;
		}
		if (this.oldVar_dateParamValue != this.dateParamValue.getValue()) {
			return true;
		}
		if (this.oldVar_sysParmDescription != this.sysParmDescription.getValue()) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering ");
		setValidationOn(true);

		if (!this.sysParmCode.isReadonly()) {
			this.sysParmCode.setConstraint(new PTStringValidator( Labels.getLabel("label_PFSParameterDialog_SysParmCode.value"),null,true));
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

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")
									+ "\n\n --> " + aPFSParameter.getSysParmCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aPFSParameter.getRecordType()).equals("")) {
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
					closeDialog(this.window_PFSParameterDialog, "PFSParameter");
				}

			} catch (DataAccessException e) {
				showMessage(e);
			}

		}
		logger.debug("Leaving ");
	}

	/**
	 * Create a new PFSParameter object. <br>
	 * @throws ParseException 
	 */
	private void doNew() throws ParseException {
		logger.debug("Entering ");
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new PFSParameter() in the frontend.
		// we get it from the backend.
		
		// remember the old vars
		doStoreInitValues();
		
		final PFSParameter aPFSParameter = getPFSParameterService().getNewPFSParameter();
		aPFSParameter.setNewRecord(true);
		setPFSParameter(aPFSParameter);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.sysParmCode.focus();
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
			this.btnCancel.setVisible(false);
		} else {
			this.sysParmCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		switch (this.parmType) {
		case 1:
			
			this.comboParmValue.setVisible(true);
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
			this.txtParmValue.setMaxlength(this.sysParmLength);
			this.txtParmValue.setValue(this.sysParmValue.getValue());
			this.comboParmValue.setDisabled(true);
			this.txtLanguageParmValue.setDisabled(true);
			this.lovDescLanguageName.setDisabled(true);
			this.btnSearchLanguage.setDisabled(true);
			this.doubleParamValue.setDisabled(true);
			break;
			
		}

		this.sysParmValue.setVisible(false);
		this.sysParmValue.setReadonly(isReadOnly("PFSParameterDialog_sysParmValue"));
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

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the PFSParameter object with the components data
		doWriteComponentsToBean(aPFSParameter);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aPFSParameter.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			if (StringUtils.trimToEmpty(aPFSParameter.getRecordType()).equals("")) {
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
				SystemParameterDetails.setParmDetails(aPFSParameter.getSysParmCode(), aPFSParameter.getSysParmValue());
				
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_PFSParameterDialog, "PFSParameter");
			}

		} catch (final DataAccessException e) {
			showMessage(e);
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

		aPFSParameter.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aPFSParameter.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aPFSParameter.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aPFSParameter.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aPFSParameter
						.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId,
							aPFSParameter);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow()
						.getAuditingReq(taskId, aPFSParameter))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			if (!StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode = getWorkFlow().firstTask.owner;

				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode + ",";
						}
						nextRoleCode = getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aPFSParameter.setTaskId(taskId);
			aPFSParameter.setNextTaskId(nextTaskId);
			aPFSParameter.setRoleCode(getRole());
			aPFSParameter.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aPFSParameter, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId, aPFSParameter);

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

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getPFSParameterService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getPFSParameterService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getPFSParameterService().doApprove(auditHeader);
						if (aPFSParameter.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getPFSParameterService().doReject(auditHeader);
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
						deleteNotes(getNotes(), true);
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
			e.printStackTrace();
		}
		logger.debug("Leaving ");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_PFSParameterDialog,
					auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
		logger.debug("Leaving");
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
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

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,
					map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	public void setNotes_entered(String notes) {
		logger.debug("Entering ");
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes)
					.equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving ");
	}

	private void doSetLOVValidation() {
		this.lovDescLanguageName.setConstraint(new PTStringValidator(Labels.getLabel(
				"label_PFSParameterDialog_SysParmValue.value"),null,true));
	}

	private void doRemoveLOVValidation() {
		this.lovDescLanguageName.setConstraint("");
	}

	private Notes getNotes() {
		Notes notes = new Notes();
		notes.setModuleName("PFSParameter");
		notes.setReference(getPFSParameter().getSysParmCode());
		notes.setVersion(getPFSParameter().getVersion());
		return notes;
	}

	/*
	 * Method to populate the parameter values in combo box based on the
	 * parameter list type.
	 */
	private void setListSysParmType() {
		logger.debug("Entering ");
		if (this.sysParmList.equals("MOD_YESNO")) {
			listSysParmType = SystemParameterUtil.getMOD_YESNO();
		} else if (this.sysParmList.equals("MOD_CBCID")) {
			listSysParmType = SystemParameterUtil.getMOD_CBCID();
		}
		for (int i = 0; i < listSysParmType.size(); i++) {
			Comboitem comboitem = new Comboitem();
			comboitem = new Comboitem();
			comboitem.setLabel(listSysParmType.get(i).getLabel());
			comboitem.setValue(listSysParmType.get(i).getValue());
			this.comboParmValue.appendChild(comboitem);
			if (this.sysParmValue.getValue().equals(
					listSysParmType.get(i).getValue())) {
				this.comboParmValue.setSelectedItem(comboitem);
			}
		}
		logger.debug("Leaving ");
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
	
	// Method for refreshing the list after successful updation
	private void refreshList() {
		logger.debug("Entering");
		final JdbcSearchObject<PFSParameter> soParameter = getPFSParameterListCtrl().getSearchObj();
		getPFSParameterListCtrl().pagingPFSParameterList.setActivePage(0);
		getPFSParameterListCtrl().getPagedListWrapper().setSearchObject(soParameter);
		if (getPFSParameterListCtrl().listBoxPFSParameter != null) {
			getPFSParameterListCtrl().listBoxPFSParameter.getListModel();
		}
		logger.debug("Leaving");
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

	public PFSParameter getPFSParameter() {
		return this.pFSParameter;
	}
	public void setPFSParameter(PFSParameter pFSParameter) {
		this.pFSParameter = pFSParameter;
	}

	public void setPFSParameterService(PFSParameterService pFSParameterService) {
		this.pFSParameterService = pFSParameterService;
	}
	public PFSParameterService getPFSParameterService() {
		return this.pFSParameterService;
	}

	public void setPFSParameterListCtrl(
			PFSParameterListCtrl pFSParameterListCtrl) {
		this.pFSParameterListCtrl = pFSParameterListCtrl;
	}
	public PFSParameterListCtrl getPFSParameterListCtrl() {
		return this.pFSParameterListCtrl;
	}

}

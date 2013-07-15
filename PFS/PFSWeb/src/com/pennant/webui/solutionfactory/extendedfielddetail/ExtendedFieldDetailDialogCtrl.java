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
 * FileName    		:  ExtendedFieldDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  28-12-2011    														*
 *                                                                  						*
 * Modified Date    :  28-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 28-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.solutionfactory.extendedfielddetail;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.staticparms.ExtendedFieldHeader;
import com.pennant.backend.service.solutionfactory.ExtendedFieldDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMasters/ExtendedFieldDetail
 * /extendedFieldDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class ExtendedFieldDetailDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long	serialVersionUID	= -5800673813892917464L;
	private final static Logger logger = Logger.getLogger(ExtendedFieldDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_ExtendedFieldDetailDialog; 	// autowired
	protected Combobox 		moduleId;							// autowired
	protected Textbox 		fieldName; 							// autowired
	protected Combobox 		fieldType; 							// autowired
	protected Intbox	 	fieldLength; 						// autowired
	protected Intbox 		fieldPrec; 							// autowired
	protected Textbox 		fieldLabel; 						// autowired
	protected Checkbox 		fieldMandatory;	 					// autowired
	protected Combobox 		fieldConstraint; 					// autowired
	protected Intbox 		fieldSeqOrder; 						// autowired
	protected Combobox 		combofieldList; 					// autowired
	protected Textbox 		fieldDefaultValue; 					// autowired
	protected Longbox 		fieldMinValue; 						// autowired
	protected Longbox 		fieldMaxValue; 						// autowired
	protected Checkbox 		fieldUnique; 						// autowired
	protected Label			label_ExtendedFieldDetailDialog_FieldListInstrLabel;

	protected Listbox 		listBoxFieldDet;
	protected Paging 		pagingFieldDetList;
	protected Textbox 		fieldList;

	protected Label 		recordStatus; 						// autowired
	protected Radiogroup 	userAction;
	protected Groupbox	 	groupboxWf;
	protected Row 			statusRow;
	protected Grid 			grid_label;

	// not auto wired vars
	private ExtendedFieldDetail extendedFieldDetail; // overhanded per param
	private transient ExtendedFieldDetailListCtrl extendedFieldDetailListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.

	private transient String 	oldVar_fieldName;
	private transient String 	oldVar_fieldType;
	private transient int 		oldVar_fieldLength;
	private transient int 		oldVar_fieldPrec;
	private transient String 	oldVar_fieldLabel;
	private transient boolean 	oldVar_fieldMandatory;
	private transient String	oldVar_fieldConstraint;
	private transient int 		oldVar_fieldSeqOrder;
	private transient String 	oldVar_fieldList;
	private transient String 	oldVar_fieldDefaultValue;
	private transient long 		oldVar_fieldMinValue;
	private transient long 		oldVar_fieldMaxValue;
	private transient boolean 	oldVar_fieldUnique;
	private transient String 	oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_ExtendedFieldDetailDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 	// autowire
	protected Button btnEdit; 	// autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; 	// autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; 	// autowire
	protected Button btnHelp; 	// autowire
	protected Button btnNotes; 	// autowire
	protected Button btnNew_HolidayDet;

	// ServiceDAOs / Domain Classes
	private transient ExtendedFieldDetailService extendedFieldDetailService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();

	private List<ValueLabel> listFieldList = PennantStaticListUtil.getAdditionalFieldList(); // autowired
	private ArrayList<ValueLabel> listFieldTypeList = PennantStaticListUtil.getFieldType();
	private ArrayList<ValueLabel> fieldConstraintList = PennantStaticListUtil.getRegexType();
	private ArrayList<ValueLabel> dateConstraintList = PennantStaticListUtil.getDateType();

	protected Row rowfieldDefaultValue;
	protected Row rowfieldList;
	protected Row rowfieldMinValue;
	protected Row rowfieldMaxValue;
	protected Row rowfieldLength;
	protected Row rowfieldPrec;
	protected Row rowUnique;
	protected Row rowMandatory;
	protected Row rowConstraint;
	protected Hlayout parent_fieldConstraint;
	private boolean newRecord=false;
	private boolean newFieldDetail=false;
	private ExtendedFieldDialogCtrl extendedFieldDialogCtrl;
	private List<ExtendedFieldDetail> extendedFieldDetails;
	String layoutDesign = "";

	/**
	 * default constructor.<br>
	 */
	public ExtendedFieldDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected ExtendedFieldDetail
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ExtendedFieldDetailDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		try {

			/* set components visible dependent of the users rights */
			doCheckRights();

			/* create the Button Controller. Disable not used buttons during working */
			this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
					this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);

			// READ OVERHANDED params !
			if (args.containsKey("extendedFieldDetail")) {
				this.extendedFieldDetail = (ExtendedFieldDetail) args.get("extendedFieldDetail");
				ExtendedFieldDetail befImage = new ExtendedFieldDetail();
				BeanUtils.copyProperties(this.extendedFieldDetail, befImage);
				this.extendedFieldDetail.setBefImage(befImage);

				setExtendedFieldDetail(this.extendedFieldDetail);
			} else {
				setExtendedFieldDetail(null);
			}

			if(args.containsKey("extendedFieldDialogCtrl")){
				setExtendedFieldDialogCtrl((ExtendedFieldDialogCtrl) args.get("extendedFieldDialogCtrl"));
				setNewFieldDetail(true);

				if(args.containsKey("newRecord")){
					setNewRecord(true);
				}else{
					setNewRecord(false);
				}

				if(args.containsKey("roleCode")){
					getUserWorkspace().alocateRoleAuthorities((String) args.get("roleCode"), "ExtendedFieldDetailDialog");
				}
			}

			if(args.containsKey("layoutDesign")){
				layoutDesign = (String) args.get("layoutDesign");
			}

			doLoadWorkFlow(this.extendedFieldDetail.isWorkflow(), this.extendedFieldDetail.getWorkflowId(),
					this.extendedFieldDetail.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "ExtendedFieldDetailDialog");
			}

			setListFieldList();

			// READ OVERHANDED params !
			// we get the extendedFieldDetailListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete extendedFieldDetail here.
			if (args.containsKey("extendedFieldDetailListCtrl")) {
				setExtendedFieldDetailListCtrl((ExtendedFieldDetailListCtrl) args.get("extendedFieldDetailListCtrl"));
			} else {
				setExtendedFieldDetailListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getExtendedFieldDetail());
		} catch (Exception e) {
			PTMessageUtils.showErrorMessage(e.getMessage());
			e.printStackTrace();
			window_ExtendedFieldDetailDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.fieldName.setMaxlength(18);
		this.fieldLength.setMaxlength(4);
		this.fieldPrec.setMaxlength(2);
		this.fieldLabel.setMaxlength(40);
		this.fieldSeqOrder.setMaxlength(10);
		this.fieldDefaultValue.setMaxlength(50);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
		}

		logger.debug("Leaving");
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
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("ExtendedFieldDetailDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldDetailDialog_btnNewD"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldDetailDialog_btnEditD"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldDetailDialog_btnDeleteD"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldDetailDialog_btnSaveD"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
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
	public void onClose$window_ExtendedFieldDetailDialog(Event event) throws Exception {
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
	 */
	public void onClick$btnEdit(Event event) {
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
		PTMessageUtils.showHelpWindow(event, window_ExtendedFieldDetailDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
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
		} catch (final WrongValuesException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	// GUI Process

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
		logger.debug("Entering");

		boolean close = true;
		doClearMessage();
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("isDataChanged : false");
		}

		if (close) {
			closeWindow();
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for closing Customer Selection Window 
	 * @throws InterruptedException
	 */
	public void closeWindow() throws InterruptedException{
		logger.debug("Entering");

		if(isNewFieldDetail()){
			window_ExtendedFieldDetailDialog.onClose();	
		}else{
			closeDialog(this.window_ExtendedFieldDetailDialog, "ExtendedFieldDetailDialog");
		}
		logger.debug("Leaving");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aExtendedFieldDetail
	 *            ExtendedFieldDetail
	 */
	public void doWriteBeanToComponents(ExtendedFieldDetail aExtendedFieldDetail) {
		logger.debug("Entering");

		this.fieldName.setValue(aExtendedFieldDetail.getFieldName());

		fillCombobox(this.fieldType,listFieldTypeList,aExtendedFieldDetail.getFieldType());

		if(isTextType()){
			fillCombobox(this.fieldConstraint,fieldConstraintList,aExtendedFieldDetail.getFieldConstraint());
			this.fieldConstraint.setValue(PennantAppUtil.getlabelDesc(aExtendedFieldDetail.getFieldConstraint(), fieldConstraintList));
		}else if(isDateType()){
			fillCombobox(this.fieldConstraint,dateConstraintList,aExtendedFieldDetail.getFieldConstraint().split(",")[0]);
			this.fieldConstraint.setValue(PennantAppUtil.getlabelDesc(aExtendedFieldDetail.getFieldConstraint().split(",")[0], dateConstraintList));
		}

		this.fieldLength.setValue(aExtendedFieldDetail.getFieldLength());
		this.fieldPrec.setValue(aExtendedFieldDetail.getFieldPrec());
		this.fieldLabel.setValue(aExtendedFieldDetail.getFieldLabel());
		this.fieldMandatory.setChecked(aExtendedFieldDetail.isFieldMandatory());
		this.fieldSeqOrder.setValue(aExtendedFieldDetail.getFieldSeqOrder());
		this.combofieldList.setValue(PennantAppUtil.getlabelDesc(aExtendedFieldDetail.getFieldList(), listFieldList));
		this.fieldDefaultValue.setValue(aExtendedFieldDetail.getFieldDefaultValue());
		this.fieldMinValue.setValue(aExtendedFieldDetail.getFieldMinValue());
		this.fieldMaxValue.setValue(aExtendedFieldDetail.getFieldMaxValue());
		this.fieldUnique.setChecked(aExtendedFieldDetail.isFieldUnique());
		this.recordStatus.setValue(aExtendedFieldDetail.getRecordStatus());
		if(!StringUtils.trimToEmpty(aExtendedFieldDetail.getFieldType()).equals("")){
			comboChange(aExtendedFieldDetail.getFieldType(),aExtendedFieldDetail.isNew());
		}

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aExtendedFieldDetail
	 */
	public void doWriteComponentsToBean(ExtendedFieldDetail aExtendedFieldDetail) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		//TODO
		String reservedKeys = ",ADD,EXTERNAL,PROCEDURE,ALL,FETCH,PUBLIC,ALTER,FILE,RAISERROR,AND,FILLFACTOR,READ," +
				"ANY,FOR,READTEXT,AS,FOREIGN,RECONFIGURE,ASC,FREETEXT,REFERENCES,AUTHORIZATION,FREETEXTTABLE," +
				"REPLICATION,BACKUP,FROM,RESTORE,BEGIN,FULL,RESTRICT,BETWEEN,FUNCTION,RETURN,BREAK,GOTO,REVERT," +
				"BROWSE,GRANT,REVOKE,BULK,GROUP,RIGHT,BY,HAVING,ROLLBACK,CASCADE,HOLDLOCK,ROWCOUNT,CASE," +
				"IDENTITY,ROWGUIDCOL,CHECK,IDENTITY_INSERT,RULE,CHECKPOINT,IDENTITYCOL,SAVE,CLOSE,IF,SCHEMA," +
				"CLUSTERED,IN,SECURITYAUDIT,COALESCE,INDEX,SELECT,COLLATE,INNER,SEMANTICKEYPHRASETABLE,COLUMN," +
				"INSERT,SEMANTICSIMILARITYDETAILSTABLE,COMMIT,INTERSECT,SEMANTICSIMILARITYTABLE,INTO,SESSION_USER," +
				"CONSTRAINT,IS,SET,CONTAINS,JOIN,SETUSER,CONTAINSTABLE,KEY,SHUTDOWN,CONTINUE,KILL,SOME," +
				"CONVERT,LEFT,STATISTICS,CREATE,LIKE,SYSTEM_USER,CROSS,LINENO,TABLE,CURRENT,LOAD,TABLESAMPLE," +
				"MERGE,TEXTSIZE,CURRENT_TIME,NATIONAL,THEN,CURRENT_TIMESTAMP,NOCHECK,TO,CURRENT_USER," +
				"NONCLUSTERED,TOP,CURSOR,NOT,TRAN,DATABASE,NULL,TRANSACTION,DBCC,NULLIF,TRIGGER,DEALLOCATE," +
				"OF,TRUNCATE,DECLARE,OFF,TRY_CONVERT,DEFAULT,OFFSETS,TSEQUAL,DELETE,ON,UNION,DENY,OPEN," +
				"UNIQUE,DESC,OPENDATASOURCE,UNPIVOT,DISK,OPENQUERY,UPDATE,DISTINCT,OPENROWSET,UPDATETEXT," +
				"DISTRIBUTED,OPENXML,USE,DOUBLE,OPTION,USER,DROP,OR,VALUES,DUMP,ORDER,VARYING,ELSE,OUTER," +
				"VIEW,END,OVER,WAITFOR,ERRLVL,PERCENT,WHEN,ESCAPE,PIVOT,WHERE,EXCEPT,PLAN,WHILE,EXEC," +
				"PRECISION,WITH,EXECUTE,PRIMARY,EXISTS,PRINT,WRITETEXT,EXIT,PROC,";

		try {
			this.fieldName.getValue();			
			if(reservedKeys.contains(","+this.fieldName.getValue().toUpperCase()+",")){
				throw new WrongValueException(this.fieldName, Labels.getLabel("RESERVED_KEYS",
						new String[]{Labels.getLabel("label_ExtendedFieldDetailDialog_FieldName.value")}));
			}
			aExtendedFieldDetail.setFieldName(this.fieldName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aExtendedFieldDetail.setFieldLabel(this.fieldLabel.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aExtendedFieldDetail.setFieldSeqOrder(this.fieldSeqOrder.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(!this.fieldType.isDisabled() && this.fieldType.getSelectedIndex()<1){
				throw new WrongValueException(fieldType, Labels.getLabel("STATIC_INVALID",
						new String[]{Labels.getLabel("label_ExtendedFieldDetailDialog_FieldType.value")}));
			}
			aExtendedFieldDetail.setFieldType(this.fieldType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (aExtendedFieldDetail.getFieldType() != null && (isTextType() || isDateType())) {
				if(isTextType()){
					if(!this.fieldConstraint.isDisabled() && this.fieldConstraint.getSelectedIndex()<1){
						throw new WrongValueException(fieldConstraint, Labels.getLabel("STATIC_INVALID",
								new String[]{Labels.getLabel("label_ExtendedFieldDetailDialog_FieldConstraint.value")}));
					}
					aExtendedFieldDetail.setFieldConstraint(this.fieldConstraint.getSelectedItem().getValue().toString());
					
				}else if(isDateType()){

					if(!"TIME".equals(aExtendedFieldDetail.getFieldType())){

						if(!this.fieldConstraint.isDisabled() && this.fieldConstraint.getSelectedIndex()<1){
							throw new WrongValueException(fieldConstraint, Labels.getLabel("STATIC_INVALID",
									new String[]{Labels.getLabel("label_ExtendedFieldDetailDialog_FieldConstraint.value")}));
						}

						String value = this.fieldConstraint.getSelectedItem().getValue().toString();

						if("RANGE".equals(value)){

							if(this.parent_fieldConstraint.getFellowIfAny("range_From") != null){
								Datebox range_From = (Datebox) this.parent_fieldConstraint.getFellowIfAny("range_From");
								if(range_From.getValue()==null || range_From.getValue().after(DateUtility.getUtilDate()) ){
									throw new WrongValueException(range_From, Labels.getLabel("DATE_ALLOWED_MAXDATE_EQUAL", new String[] {"From Date",
											DateUtility.formatUtilDate(DateUtility.getUtilDate(), PennantConstants.dateFormat)}));
								}

								value = value +","+DateUtility.formatUtilDate(range_From.getValue(),PennantConstants.dateTimeFormat);
							}

							if(this.parent_fieldConstraint.getFellowIfAny("range_To") != null){
								Datebox range_To = (Datebox) this.parent_fieldConstraint.getFellowIfAny("range_To");
								if(range_To.getValue()==null || range_To.getValue().before(DateUtility.getUtilDate()) ){
									throw new WrongValueException(range_To, Labels.getLabel("DATE_ALLOWED_MINDATE_EQUAL", new String[] {"To Date",
											DateUtility.formatUtilDate(DateUtility.getUtilDate(), PennantConstants.dateFormat)}));
								}
								value = value +","+DateUtility.formatUtilDate(range_To.getValue(),PennantConstants.dateTimeFormat);
							}

						} else if("FUTURE_DAYS".equals(value) ||  "PAST_DAYS".equals(value)){

							if(this.parent_fieldConstraint.getFellowIfAny("noOfDays") != null){
								Intbox days = (Intbox) this.parent_fieldConstraint.getFellowIfAny("noOfDays");
								if(days.intValue()<=0){
									throw new WrongValueException(days, Labels.getLabel("NUMBER_MINVALUE", new String[] {"No Of Days","0"}));
								}
								value = value +","+days.intValue();
							}
						}
						aExtendedFieldDetail.setFieldConstraint(value);
					}else{
						aExtendedFieldDetail.setFieldConstraint("");
					}
				}
			}else{
				aExtendedFieldDetail.setFieldConstraint("");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (aExtendedFieldDetail.getFieldType() != null && (aExtendedFieldDetail.getFieldType().equals("TXT")
				|| aExtendedFieldDetail.getFieldType().equals("MTXT"))) {
			aExtendedFieldDetail.setFieldPrec(0);
			aExtendedFieldDetail.setFieldList("");
			aExtendedFieldDetail.setFieldDefaultValue(this.fieldDefaultValue.getValue());
			aExtendedFieldDetail.setFieldMinValue(0);
			aExtendedFieldDetail.setFieldMaxValue(0);

			try {
				if (this.fieldLength.getValue() == null || this.fieldLength.intValue() == 0) {
					throw new WrongValueException(this.fieldLength, Labels.getLabel("FIELD_NO_EMPTY",
							Labels.getLabel("label_ExtendedFieldDetailDialog_FieldLength.value")));
				}
				aExtendedFieldDetail.setFieldLength(this.fieldLength.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		if (aExtendedFieldDetail.getFieldType() != null && (aExtendedFieldDetail.getFieldType().equals("NUMERIC") 
				|| aExtendedFieldDetail.getFieldType().equals("PRCT"))) {

			try {
				if (this.fieldLength.getValue() == null || this.fieldLength.intValue() == 0) {
					throw new WrongValueException(this.fieldLength, Labels.getLabel("FIELD_NO_EMPTY",
							Labels.getLabel("label_ExtendedFieldDetailDialog_FieldLength.value")));
				}
				aExtendedFieldDetail.setFieldLength(this.fieldLength.intValue());
				aExtendedFieldDetail.setFieldList("");
				aExtendedFieldDetail.setFieldDefaultValue(this.fieldDefaultValue.getValue() == null?"":this.fieldDefaultValue.getValue());
				aExtendedFieldDetail.setFieldMinValue(this.fieldMinValue.longValue());
				aExtendedFieldDetail.setFieldMaxValue(this.fieldMaxValue.longValue());
				aExtendedFieldDetail.setFieldPrec(this.fieldPrec.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		if (aExtendedFieldDetail.getFieldType() != null && (aExtendedFieldDetail.getFieldType().equals("AMT") 
				|| aExtendedFieldDetail.getFieldType().equals("RATE"))) {

			try {
				if (this.fieldLength.getValue() == null || this.fieldLength.intValue() == 0) {
					throw new WrongValueException(this.fieldLength, Labels.getLabel("FIELD_NO_EMPTY",
							Labels.getLabel("label_ExtendedFieldDetailDialog_FieldLength.value")));
				}
				aExtendedFieldDetail.setFieldLength(this.fieldLength.intValue());
				aExtendedFieldDetail.setFieldList("");
				aExtendedFieldDetail.setFieldDefaultValue(this.fieldDefaultValue.getValue() == null?"":this.fieldDefaultValue.getValue());
				aExtendedFieldDetail.setFieldMinValue(this.fieldMinValue.longValue());
				aExtendedFieldDetail.setFieldMaxValue(this.fieldMaxValue.longValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aExtendedFieldDetail.setFieldPrec(this.fieldPrec.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		if (aExtendedFieldDetail.getFieldType() != null &&  (aExtendedFieldDetail.getFieldType().equals("SLIST") || 
				aExtendedFieldDetail.getFieldType().equals("SMLIST"))) {

			try {
				if (this.fieldLength.getValue() == null || this.fieldLength.intValue() == 0) {
					throw new WrongValueException(this.fieldLength, Labels.getLabel("FIELD_NO_EMPTY",
							Labels.getLabel("label_ExtendedFieldDetailDialog_FieldLength.value")));
				}else if(this.fieldLength.intValue() > 20){
					throw new WrongValueException(this.fieldLength, Labels.getLabel("FIELD_NO_EMPTY_LESSTHAN",
							new String[]{Labels.getLabel("label_ExtendedFieldDetailDialog_FieldLength.value"),String.valueOf(20)}));
				}
				aExtendedFieldDetail.setFieldLength(this.fieldLength.intValue());
				aExtendedFieldDetail.setFieldPrec(0);
				aExtendedFieldDetail.setFieldMinValue(0);
				aExtendedFieldDetail.setFieldMaxValue(0);
				aExtendedFieldDetail.setFieldDefaultValue("");
				if (grid_label.getFellowIfAny("SListId") != null){
					Textbox text=(Textbox) grid_label.getFellowIfAny("SListId");
					String[] statList = text.getValue().split(",");
					Map<String,Boolean> fieldList = new HashMap<String,Boolean>();
					for (String statValue : statList) {
						if(fieldList.containsKey(statValue)){
							throw new WrongValueException(text, Labels.getLabel("FIELD_REPEAT",
									Labels.getLabel("label_ExtendedFieldDetailDialog_FieldList.value")));
						}
						fieldList.put(statValue, false);
						if(statValue.length() == 0 || (statValue.length() > this.fieldLength.intValue())){
							throw new WrongValueException(text, Labels.getLabel("FIELD_LEN_RENGE",
									new String[]{Labels.getLabel("label_ExtendedFieldDetailDialog_FieldList.value"),
									String.valueOf(this.fieldLength.intValue())}));
						}
					}
					aExtendedFieldDetail.setFieldList(text.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		if (aExtendedFieldDetail.getFieldType() != null &&  aExtendedFieldDetail.getFieldType().equals("RADIO")) {

			try {
				if (this.fieldLength.getValue() == null || this.fieldLength.intValue() == 0) {
					throw new WrongValueException(this.fieldLength, Labels.getLabel("FIELD_NO_EMPTY",
							Labels.getLabel("label_ExtendedFieldDetailDialog_FieldLength.value")));
				}else if(this.fieldLength.intValue() > 20){
					throw new WrongValueException(this.fieldLength, Labels.getLabel("FIELD_NO_EMPTY_LESSTHAN",
							new String[]{Labels.getLabel("label_ExtendedFieldDetailDialog_FieldLength.value"),String.valueOf(20)}));
				}
				aExtendedFieldDetail.setFieldLength(this.fieldLength.intValue());
				aExtendedFieldDetail.setFieldPrec(0);
				aExtendedFieldDetail.setFieldMinValue(0);
				aExtendedFieldDetail.setFieldMaxValue(0);
				aExtendedFieldDetail.setFieldDefaultValue("");
				if (grid_label.getFellowIfAny("RadioList") != null){
					Textbox text=(Textbox) grid_label.getFellowIfAny("RadioList");
					String[] statList = text.getValue().split(",");
					if(statList.length > 10){
						throw new WrongValueException(text, Labels.getLabel("FIELD_COUNT_RENGE",
								new String[]{Labels.getLabel("label_ExtendedFieldDetailDialog_RadioGroup.value"),
								String.valueOf(10)}));
					}
					Map<String,Boolean> fieldList = new HashMap<String,Boolean>();
					for (String statValue : statList) {
						if(fieldList.containsKey(statValue)){
							throw new WrongValueException(text, Labels.getLabel("FIELD_REPEAT",
									Labels.getLabel("label_ExtendedFieldDetailDialog_FieldList.value")));
						}
						fieldList.put(statValue, false);
						if(statValue.length() == 0 || (statValue.length() > this.fieldLength.intValue())){
							throw new WrongValueException(text, Labels.getLabel("FIELD_LEN_RENGE",
									new String[]{Labels.getLabel("label_ExtendedFieldDetailDialog_RadioGroup.value"),
									String.valueOf(this.fieldLength.intValue())}));
						}
					}
					aExtendedFieldDetail.setFieldList(text.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		if (aExtendedFieldDetail.getFieldType() != null &&  
				aExtendedFieldDetail.getFieldType().equals("CHKB")) {
			aExtendedFieldDetail.setFieldLength(0);
			aExtendedFieldDetail.setFieldPrec(0);
			aExtendedFieldDetail.setFieldMinValue(0);
			aExtendedFieldDetail.setFieldMaxValue(0);
			aExtendedFieldDetail.setFieldDefaultValue("");
			aExtendedFieldDetail.setFieldList("");
		}

		if (aExtendedFieldDetail.getFieldType() != null &&  (aExtendedFieldDetail.getFieldType().equals("DLIST") 
				|| aExtendedFieldDetail.getFieldType().equals("DMLIST"))) {

			try {
				if (this.combofieldList.getValue().equals(null) || this.combofieldList.getValue().equals("")) {
					throw new WrongValueException(this.combofieldList, Labels.getLabel("SELECT_FIELD",
							Labels.getLabel("label_ExtendedFieldDetailDialog_FieldList.value")));
				}
				aExtendedFieldDetail.setFieldList(this.combofieldList.getSelectedItem().getValue().toString());
				aExtendedFieldDetail.setFieldPrec(0);
				aExtendedFieldDetail.setFieldMinValue(0);
				aExtendedFieldDetail.setFieldMaxValue(0);
				aExtendedFieldDetail.setFieldDefaultValue("");
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (this.fieldLength.getValue() == null || this.fieldLength.intValue() == 0) {
					throw new WrongValueException(this.fieldLength, Labels.getLabel("FIELD_NO_EMPTY",
							Labels.getLabel("label_ExtendedFieldDetailDialog_FieldLength.value")));
				}
				aExtendedFieldDetail.setFieldLength(this.fieldLength.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		if (aExtendedFieldDetail.getFieldType() != null && (aExtendedFieldDetail.getFieldType().equals("CHKB")
				|| aExtendedFieldDetail.getFieldType().equals("DATE")
				|| aExtendedFieldDetail.getFieldType().equals("DATETIME")
				|| aExtendedFieldDetail.getFieldType().equals("TIME"))) {
			aExtendedFieldDetail.setFieldPrec(0);
			aExtendedFieldDetail.setFieldMinValue(0);
			aExtendedFieldDetail.setFieldMaxValue(0);
			aExtendedFieldDetail.setFieldDefaultValue("");
			aExtendedFieldDetail.setFieldLength(0);
			aExtendedFieldDetail.setFieldList("");
		}

		try {
			this.fieldDefaultValue.getValue();
			if(aExtendedFieldDetail.getFieldType() != null && isNumericType() && 
					!StringUtils.trimToEmpty(this.fieldDefaultValue.getValue()).equals("")){
				if(aExtendedFieldDetail.getFieldType().equals("PRCT")){
					this.fieldDefaultValue.setConstraint(new PTDecimalValidator(Labels.getLabel("label_ExtendedFieldDetailDialog_FieldDefaultValue.value"),
							2, false, false, 0, 100));
				}else{
					int length = this.fieldLength.intValue();
					int prec = this.fieldPrec.intValue();
					this.fieldDefaultValue.setConstraint(new PTDecimalValidator(Labels.getLabel("label_ExtendedFieldDetailDialog_FieldDefaultValue.value"),
							prec, false, false, 0, Math.pow(10, length-prec) -1));
				}
				this.fieldDefaultValue.getValue();
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(aExtendedFieldDetail.getFieldType() != null && isNumericType() && this.fieldMinValue.longValue() != 0){
				if(aExtendedFieldDetail.getFieldType().equals("PRCT")){
					this.fieldMinValue.setConstraint(new PTDecimalValidator(Labels.getLabel("label_ExtendedFieldDetailDialog_FieldMinValue.value"),
							2, false, false, 0, 100));
				}else{
					int length = this.fieldLength.intValue();
					int prec = this.fieldPrec.intValue();
					
					this.fieldMinValue.setConstraint(new PTDecimalValidator(Labels.getLabel("label_ExtendedFieldDetailDialog_FieldMinValue.value"),
							prec, false, false, 0, Math.pow(10, length-prec) -1));
				}
				this.fieldMinValue.longValue();
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(aExtendedFieldDetail.getFieldType() != null && isNumericType() && this.fieldMaxValue.longValue() != 0){
				if(aExtendedFieldDetail.getFieldType().equals("PRCT")){
					this.fieldMaxValue.setConstraint(new PTDecimalValidator(Labels.getLabel("label_ExtendedFieldDetailDialog_FieldMaxValue.value"),
							2, false, false, 0, 100));
				}else{
					int length = this.fieldLength.intValue();
					int prec = this.fieldPrec.intValue();
					this.fieldMaxValue.setConstraint(new PTDecimalValidator(Labels.getLabel("label_ExtendedFieldDetailDialog_FieldMaxValue.value"),
							prec, false, false, 0, Math.pow(10, length-prec) -1));
				}
				this.fieldMaxValue.longValue();
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(aExtendedFieldDetail.getFieldType() != null && isNumericType()){
				this.fieldMinValue.longValue();
				this.fieldMaxValue.longValue();
				if(this.fieldMinValue.longValue() > this.fieldMaxValue.longValue()){
					throw new WrongValueException(fieldMinValue, Labels.getLabel("NUMBER_MAXVALUE_EQ", 
							new String[] { Labels.getLabel("label_ExtendedFieldDetailDialog_FieldMinValue.value") ,
							Labels.getLabel("label_ExtendedFieldDetailDialog_FieldMaxValue.value")}));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aExtendedFieldDetail.setFieldMandatory(this.fieldMandatory.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aExtendedFieldDetail.setFieldUnique(this.fieldUnique.isChecked());
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

		aExtendedFieldDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aExtendedFieldDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(ExtendedFieldDetail aExtendedFieldDetail) throws InterruptedException {
		logger.debug("Entering");

		// if aExtendedFieldDetail == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aExtendedFieldDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aExtendedFieldDetail = getExtendedFieldDetailService().getNewExtendedFieldDetail();

			setExtendedFieldDetail(aExtendedFieldDetail);
		} else {
			setExtendedFieldDetail(aExtendedFieldDetail);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.fieldName.focus();
		} else {
			this.fieldType.focus();
			if (isNewFieldDetail()){
				doEdit();
			}else  if (isWorkFlowEnabled()){
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
			doWriteBeanToComponents(aExtendedFieldDetail);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			if(isNewFieldDetail()){
				this.window_ExtendedFieldDetailDialog.setHeight("480px");
				this.window_ExtendedFieldDetailDialog.setWidth("800px");
				this.groupboxWf.setVisible(false);
				this.window_ExtendedFieldDetailDialog.doModal() ;
			}else{
				this.window_ExtendedFieldDetailDialog.setWidth("100%");
				this.window_ExtendedFieldDetailDialog.setHeight("100%");
				setDialog(this.window_ExtendedFieldDetailDialog);
			}
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.getMessage());
			this.window_ExtendedFieldDetailDialog.onClose();
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		// this.oldVar_lovDescModuleName = this.lovDescModuleName.getValue();
		this.oldVar_fieldName = this.fieldName.getValue();
		this.oldVar_fieldType = this.fieldType.getValue();
		this.oldVar_fieldLength = this.fieldLength.intValue();
		this.oldVar_fieldPrec = this.fieldPrec.intValue();
		this.oldVar_fieldLabel = this.fieldLabel.getValue();
		this.oldVar_fieldMandatory = this.fieldMandatory.isChecked();
		this.oldVar_fieldConstraint = this.fieldConstraint.getValue();
		this.oldVar_fieldSeqOrder = this.fieldSeqOrder.intValue();
		this.oldVar_fieldList = this.combofieldList.getValue();
		this.oldVar_fieldDefaultValue = this.fieldDefaultValue.getValue();
		this.oldVar_fieldMinValue = this.fieldMinValue.longValue();
		this.oldVar_fieldMaxValue = this.fieldMaxValue.longValue();
		this.oldVar_fieldUnique = this.fieldUnique.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.fieldName.setValue(this.oldVar_fieldName);
		this.fieldType.setValue(this.oldVar_fieldType);
		this.fieldLength.setValue(this.oldVar_fieldLength);
		this.fieldPrec.setValue(this.oldVar_fieldPrec);
		this.fieldLabel.setValue(this.oldVar_fieldLabel);
		this.fieldMandatory.setChecked(this.oldVar_fieldMandatory);
		this.fieldConstraint.setValue(this.oldVar_fieldConstraint);
		this.fieldSeqOrder.setValue(this.oldVar_fieldSeqOrder);
		this.combofieldList.setValue(this.oldVar_fieldList);
		this.fieldDefaultValue.setValue(this.oldVar_fieldDefaultValue);
		this.fieldMinValue.setValue(this.oldVar_fieldMinValue);
		this.fieldMaxValue.setValue(this.oldVar_fieldMaxValue);
		this.fieldUnique.setChecked(this.oldVar_fieldUnique);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {

		// To clear the Error Messages
		doClearMessage();

		if (this.oldVar_fieldName != this.fieldName.getValue()) {
			return true;
		}
		if (this.oldVar_fieldType != this.fieldType.getValue()) {
			return true;
		}
		if (this.oldVar_fieldLength != this.fieldLength.intValue()) {
			return true;
		}
		if (this.oldVar_fieldPrec != this.fieldPrec.intValue()) {
			return true;
		}
		if (this.oldVar_fieldLabel != this.fieldLabel.getValue()) {
			return true;
		}
		if (this.oldVar_fieldMandatory != this.fieldMandatory.isChecked()) {
			return true;
		}
		if (this.oldVar_fieldConstraint != this.fieldConstraint.getValue()) {
			return true;
		}
		if (this.oldVar_fieldSeqOrder != this.fieldSeqOrder.intValue()) {
			return true;
		}
		if (this.oldVar_fieldList != this.combofieldList.getValue()) {
			return true;
		}
		if (this.oldVar_fieldDefaultValue != this.fieldDefaultValue.getValue()) {
			return true;
		}
		if (this.oldVar_fieldMinValue != this.fieldMinValue.longValue()) {
			return true;
		}
		if (this.oldVar_fieldMaxValue != this.fieldMaxValue.longValue()) {
			return true;
		}
		if (this.oldVar_fieldUnique != this.fieldUnique.isChecked()) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		this.fieldPrec.getValue();

		if (!this.fieldName.isReadonly()) {
			this.fieldName.setConstraint(new PTStringValidator(Labels.getLabel("label_ExtendedFieldDetailDialog_FieldName.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_UNDERSCORE,true));
		}
		
		if (!this.fieldLabel.isReadonly()) {
			this.fieldLabel.setConstraint(new PTStringValidator(Labels.getLabel("label_ExtendedFieldDetailDialog_FieldLabel.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL,true));
		}

		if (!this.fieldLength.isReadonly()) {
			if (isTextType() || isListType()) {
				this.fieldLength.setConstraint(new PTNumberValidator(Labels.getLabel("label_ExtendedFieldDetailDialog_FieldLength.value"),
						false, false, 1, 300));
			} else if (isNumericType()) {
				this.fieldLength.setConstraint(new PTNumberValidator(Labels.getLabel("label_ExtendedFieldDetailDialog_FieldLength.value"),
						false, false, 1, 21));
			}
		}

		if (!this.fieldPrec.isReadonly()) {
			this.fieldPrec.setErrorMessage("");
			if (isNumericType()) {
				Integer length = 21;

				try {
					length = this.fieldLength.intValue();
					if (this.fieldPrec.intValue()!= 0){
						this.fieldPrec.setConstraint(new PTNumberValidator(
								Labels.getLabel("label_ExtendedFieldDetailDialog_FieldPrec.value"),
								false, false, 0, length));
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}

		if (!this.fieldSeqOrder.isReadonly()) {
			this.fieldSeqOrder.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_ExtendedFieldDetailDialog_FieldSeqOrder.value"),
					false, false, 1, 1000));
		}
		if (!this.combofieldList.isDisabled() && rowfieldList.isVisible()) {
			this.combofieldList.setConstraint(new StaticListValidator(listFieldList, Labels
					.getLabel("label_ExtendedFieldDetailDialog_FieldList.value")));
		}

		if (!this.fieldDefaultValue.isReadonly() && rowfieldDefaultValue.isVisible()) {
			if(isNumericType()){
				this.fieldDefaultValue.setConstraint(new PTStringValidator(
						Labels.getLabel("label_ExtendedFieldDetailDialog_FieldDefaultValue.value"),
						PennantRegularExpressions.REGEX_NM_AMOUNT,false));
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
		this.fieldName.setConstraint("");
		this.fieldType.setConstraint("");
		this.fieldLength.setConstraint("");
		this.fieldPrec.setConstraint("");
		this.fieldLabel.setConstraint("");
		this.fieldConstraint.setConstraint("");
		this.fieldSeqOrder.setConstraint("");
		this.combofieldList.setConstraint("");
		this.fieldDefaultValue.setConstraint("");
		this.fieldMinValue.setConstraint("");
		this.fieldMaxValue.setConstraint("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a ExtendedFieldDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final ExtendedFieldDetail aExtendedFieldDetail = new ExtendedFieldDetail();
		BeanUtils.copyProperties(getExtendedFieldDetail(), aExtendedFieldDetail);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
		+ aExtendedFieldDetail.getModuleId();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aExtendedFieldDetail.getRecordType()).equals("")) {
				aExtendedFieldDetail.setVersion(aExtendedFieldDetail.getVersion() + 1);
				aExtendedFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				aExtendedFieldDetail.setNewRecord(true);

				if (isWorkFlowEnabled()) {
					aExtendedFieldDetail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if(isNewFieldDetail()){
					tranType=PennantConstants.TRAN_DEL;
					AuditHeader auditHeader =  newFieldProcess(aExtendedFieldDetail,tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_ExtendedFieldDetailDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
						getExtendedFieldDialogCtrl().doFillFieldsList(this.extendedFieldDetails);

						// send the data back to customer
						closeWindow();
					}	

				}else if(doProcess(aExtendedFieldDetail,tranType)){
					refreshList();
					closeWindow();
				}
			}catch (DataAccessException e){
				logger.error(e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new ExtendedFieldDetail object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old vars
		doStoreInitValues();

		final ExtendedFieldDetail aExtendedFieldDetail = getExtendedFieldDetailService().getNewExtendedFieldDetail();
		setExtendedFieldDetail(aExtendedFieldDetail);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.fieldType.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (isNewRecord()) {
			this.fieldName.setReadonly(false);
			if (isNewFieldDetail()) {
				this.fieldName.setReadonly(false);
				this.btnCancel.setVisible(false);
			}
		} else {
			this.fieldName.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		
		this.fieldType.setDisabled(isReadOnly("ExtendedFieldDetailDialog_fieldType"));
		this.fieldLength.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldLength"));
		this.fieldPrec.setDisabled(isReadOnly("ExtendedFieldDetailDialog_fieldPrec"));
		this.fieldLabel.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldLabel"));
		this.fieldMandatory.setDisabled(isReadOnly("ExtendedFieldDetailDialog_fieldMandatory"));
		this.fieldConstraint.setDisabled(isReadOnly("ExtendedFieldDetailDialog_fieldConstraint"));
		this.fieldSeqOrder.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldSeqOrder"));
		this.combofieldList.setDisabled(isReadOnly("ExtendedFieldDetailDialog_fieldList"));
		this.fieldDefaultValue.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldDefaultValue"));
		this.fieldMinValue.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldMinValue"));
		this.fieldMaxValue.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldMaxValue"));
		this.fieldUnique.setDisabled(isReadOnly("ExtendedFieldDetailDialog_fieldUnique"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.extendedFieldDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if(isNewFieldDetail()){
				if (isNewRecord()){
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				}else{
					this.btnCtrl.setWFBtnStatus_Edit(isNewFieldDetail());
				}
			}else{
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}

		logger.debug("Leaving");
	}
	
	public boolean isReadOnly(String componentName){
		if (isWorkFlowEnabled() || isNewFieldDetail()){
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.fieldName.setReadonly(true);
		this.fieldType.setDisabled(true);
		this.fieldLength.setReadonly(true);
		this.fieldPrec.setReadonly(true);
		this.fieldLabel.setReadonly(true);
		this.fieldMandatory.setDisabled(true);
		this.fieldConstraint.setReadonly(true);
		this.fieldSeqOrder.setReadonly(true);
		this.combofieldList.setDisabled(true);
		this.fieldDefaultValue.setReadonly(true);
		this.fieldMinValue.setReadonly(true);
		this.fieldMaxValue.setReadonly(true);
		this.fieldUnique.setDisabled(true);
		doEdit();
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

		this.fieldName.setValue("");
		this.fieldType.setValue("");
		this.fieldLength.setText("");
		this.fieldPrec.setText("");
		this.fieldLabel.setValue("");
		this.fieldMandatory.setChecked(false);
		this.fieldConstraint.setValue("");
		this.fieldSeqOrder.setText("");
		this.combofieldList.setValue("");
		this.fieldDefaultValue.setValue("");
		this.fieldMinValue.setText("");
		this.fieldMaxValue.setText("");
		this.fieldUnique.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final ExtendedFieldDetail aExtendedFieldDetail = new ExtendedFieldDetail();
		BeanUtils.copyProperties(getExtendedFieldDetail(), aExtendedFieldDetail);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doRemoveValidation();
		doSetValidation();

		// fill the ExtendedFieldDetail object with the components data
		doWriteComponentsToBean(aExtendedFieldDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aExtendedFieldDetail.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aExtendedFieldDetail.getRecordType()).equals("")) {
				aExtendedFieldDetail.setVersion(aExtendedFieldDetail.getVersion() + 1);
				if (isNew) {
					aExtendedFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aExtendedFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aExtendedFieldDetail.setNewRecord(true);
				}
			}
		} else {

			if(isNewFieldDetail()){
				if(isNewRecord()){
					aExtendedFieldDetail.setVersion(1);
					aExtendedFieldDetail.setRecordType(PennantConstants.RCD_ADD);
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}

				if(StringUtils.trimToEmpty(aExtendedFieldDetail.getRecordType()).equals("")){
					aExtendedFieldDetail.setVersion(aExtendedFieldDetail.getVersion()+1);
					aExtendedFieldDetail.setRecordType(PennantConstants.RCD_UPD);
				}

				if(aExtendedFieldDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(aExtendedFieldDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}

			}else{
				aExtendedFieldDetail.setVersion(aExtendedFieldDetail.getVersion()+1);
				if(isNew){
					tranType =PennantConstants.TRAN_ADD;
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {
			if(isNewFieldDetail()){
				AuditHeader auditHeader =  newFieldProcess(aExtendedFieldDetail,tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_ExtendedFieldDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getExtendedFieldDialogCtrl().doFillFieldsList(this.extendedFieldDetails);
					closeWindow();
				}

			}else if (doProcess(aExtendedFieldDetail, tranType)) {
				refreshList();
				closeDialog(this.window_ExtendedFieldDetailDialog, "ExtendedFieldDetail");
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(ExtendedFieldDetail aExtendedFieldDetail, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aExtendedFieldDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aExtendedFieldDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aExtendedFieldDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			aExtendedFieldDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aExtendedFieldDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aExtendedFieldDetail);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aExtendedFieldDetail))) {
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

			aExtendedFieldDetail.setTaskId(taskId);
			aExtendedFieldDetail.setNextTaskId(nextTaskId);
			aExtendedFieldDetail.setRoleCode(getRole());
			aExtendedFieldDetail.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aExtendedFieldDetail, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId, aExtendedFieldDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aExtendedFieldDetail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aExtendedFieldDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		ExtendedFieldDetail aExtendedFieldDetail = (ExtendedFieldDetail) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getExtendedFieldDetailService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getExtendedFieldDetailService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getExtendedFieldDetailService().doApprove(auditHeader);

						if (aExtendedFieldDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getExtendedFieldDetailService().doReject(auditHeader);
						if (aExtendedFieldDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_ExtendedFieldDetailDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_ExtendedFieldDetailDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

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
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	private void fillCombobox(Combobox combobox,ArrayList<ValueLabel> arrayList,String value){
		combobox.getItems().clear();
		Comboitem comboitem = new Comboitem();		
		comboitem.setLabel("----Select-----");
		comboitem.setValue("");
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);

		if (arrayList!=null) {
			for (int i = 0; i < arrayList.size(); i++) {
				comboitem = new Comboitem();
				comboitem.setLabel(arrayList.get(i).getLabel());
				comboitem.setValue(arrayList.get(i).getValue());
				combobox.appendChild(comboitem);
				if (StringUtils.trimToEmpty(value).equals(arrayList.get(i).getValue())) {
					combobox.setSelectedItem(comboitem);
				}
			}
		} 
	}

	private void setListFieldList() {
		for (int i = 0; i < listFieldList.size(); i++) {
			Comboitem comboitem = new Comboitem();
			comboitem = new Comboitem();
			comboitem.setLabel(listFieldList.get(i).getLabel());
			comboitem.setValue(listFieldList.get(i).getValue());
			this.combofieldList.appendChild(comboitem);
		}
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

	public ExtendedFieldDetail getExtendedFieldDetail() {
		return this.extendedFieldDetail;
	}
	public void setExtendedFieldDetail(ExtendedFieldDetail extendedFieldDetail) {
		this.extendedFieldDetail = extendedFieldDetail;
	}

	public void setExtendedFieldDetailService(ExtendedFieldDetailService extendedFieldDetailService) {
		this.extendedFieldDetailService = extendedFieldDetailService;
	}
	public ExtendedFieldDetailService getExtendedFieldDetailService() {
		return this.extendedFieldDetailService;
	}

	public void setExtendedFieldDetailListCtrl(ExtendedFieldDetailListCtrl extendedFieldDetailListCtrl) {
		this.extendedFieldDetailListCtrl = extendedFieldDetailListCtrl;
	}
	public ExtendedFieldDetailListCtrl getExtendedFieldDetailListCtrl() {
		return this.extendedFieldDetailListCtrl;
	}

	private AuditHeader getAuditHeader(ExtendedFieldDetail aExtendedFieldDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aExtendedFieldDetail.getBefImage(), aExtendedFieldDetail);
		return new AuditHeader(String.valueOf(aExtendedFieldDetail.getModuleId()), null, null, null, auditDetail,
				aExtendedFieldDetail.getUserDetails(), getOverideMap());
	}

	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_ExtendedFieldDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering");
		// logger.debug(event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
	}

	private void doSetLOVValidation() {
	}

	private void doRemoveLOVValidation() {
	}

	private Notes getNotes() {
		Notes notes = new Notes();
		notes.setModuleName("ExtendedFieldDetail");
		notes.setReference(String.valueOf(getExtendedFieldDetail().getModuleId()));
		notes.setVersion(getExtendedFieldDetail().getVersion());
		return notes;
	}

	private void doClearMessage() {
		logger.debug("Entering");
		this.fieldName.setErrorMessage("");
		this.fieldType.setErrorMessage("");
		this.fieldLength.setErrorMessage("");
		this.fieldPrec.setErrorMessage("");
		this.fieldLabel.setErrorMessage("");
		this.fieldConstraint.setErrorMessage("");
		this.fieldSeqOrder.setErrorMessage("");
		this.combofieldList.setErrorMessage("");
		this.fieldDefaultValue.setErrorMessage("");
		this.fieldMinValue.setErrorMessage("");
		this.fieldMaxValue.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void refreshList() {
		final JdbcSearchObject<ExtendedFieldHeader> soExtendedFieldDetail = getExtendedFieldDetailListCtrl().getSearchObj();
		getExtendedFieldDetailListCtrl().pagingExtendedFieldDetailList.setActivePage(0);
		getExtendedFieldDetailListCtrl().getPagedListWrapper().setSearchObject(soExtendedFieldDetail);
		if (getExtendedFieldDetailListCtrl().listBoxExtendedFieldDetail != null) {
			getExtendedFieldDetailListCtrl().listBoxExtendedFieldDetail.getListModel();
		}
	}

	public void onChange$fieldType(Event event) {
		if (this.fieldType.getSelectedItem() != null
				&& !this.fieldType.getSelectedItem().getValue().equals("")) {
			comboChange(this.fieldType.getSelectedItem().getValue().toString(),true);
		}
	}

	public void onBlur$fieldLength(Event event) {
		this.fieldDefaultValue.setMaxlength(this.fieldLength.intValue()+1);
		this.fieldMinValue.setMaxlength(this.fieldLength.intValue()+1);
		this.fieldMaxValue.setMaxlength(this.fieldLength.intValue()+1);
	}

	private boolean isTextType() {
		String type = this.fieldType.getSelectedItem().getValue().toString();
		return "|TXT|MTXT|".contains("|" + type + "|");
	}

	private boolean isDateType() {
		String type = this.fieldType.getSelectedItem().getValue().toString();
		return "|DATE|DATETIME|TIME|".contains("|" + type + "|");
	}

	private boolean isNumericType() {
		String type = this.fieldType.getSelectedItem().getValue().toString();
		return "|AMT|RATE|NUMERIC|PRCT|".contains("|" + type + "|");
	}

	private boolean isListType() {
		String type = this.fieldType.getSelectedItem().getValue().toString();
		return "|DLIST|SLIST|DMLIST|RADIO|".contains("|" + type + "|");
	}

	private void comboChange(String fieldType, boolean newSelection) {
		logger.debug("Entering");

		if(this.rowfieldList.getFellowIfAny("SListId") != null){
			this.combofieldList.getPreviousSibling().detach();
		}
		if(this.rowfieldList.getFellowIfAny("RadioList") != null){
			this.combofieldList.getPreviousSibling().detach();
		}

		// Set the visibility of fields
		this.rowfieldLength.setVisible(isTextType() || isNumericType() || isListType());
		this.rowfieldPrec.setVisible(isNumericType());
		this.fieldConstraint.removeForward("onChange", this.window_ExtendedFieldDetailDialog, "onDateContSelect");

		doClearMessage();

		this.rowfieldDefaultValue.setVisible(false);
		this.rowfieldList.setVisible(false);
		this.rowfieldMinValue.setVisible(false);
		this.rowfieldMaxValue.setVisible(false);
		this.rowMandatory.setVisible(true);
		this.rowUnique.setVisible(true);
		this.fieldLength.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldLength"));
		this.fieldPrec.setDisabled(isReadOnly("ExtendedFieldDetailDialog_fieldPrec"));
		this.rowConstraint.setVisible(false);
		this.label_ExtendedFieldDetailDialog_FieldListInstrLabel.setVisible(false);
		
		if (isTextType()) {
			this.rowfieldDefaultValue.setVisible(true);
			this.rowConstraint.setVisible(true);
			if(newSelection){
				this.fieldConstraint.getItems().clear();
				fillCombobox(this.fieldConstraint,fieldConstraintList,"");
			}
		} else if (isNumericType()) {
			this.rowfieldDefaultValue.setVisible(true);
			this.rowfieldMinValue.setVisible(true);
			this.rowfieldMaxValue.setVisible(true);
			this.rowUnique.setVisible(false);
			this.fieldPrec.setReadonly(true);

			if(newSelection){
				// Set the default values
				if ("AMT".equals(fieldType)) {
					this.fieldLength.setValue(21);
					this.fieldPrec.setValue(SystemParameterDetails.getSystemParameterObject("APP_DFT_CURR").getSysParmDec());
				} else if ("RATE".equals(fieldType)) {
					this.fieldLength.setValue(13);
					this.fieldPrec.setValue(9);
					this.fieldPrec.setDisabled(isReadOnly("ExtendedFieldDetailDialog_fieldPrec"));
				} else if ("PRCT".equals(fieldType)) {
					this.fieldLength.setValue(5);
					this.fieldPrec.setValue(2);
					this.fieldLength.setReadonly(true);
				} else if("NUMERIC".equals(fieldType)){
					this.fieldLength.setValue(21);
					this.fieldPrec.setValue(0);
					this.rowfieldPrec.setVisible(false);
				}
			}else{
				if ("PRCT".equals(fieldType)) {
					this.fieldLength.setReadonly(true);
				} else if("RATE".equals(fieldType)){
					this.fieldPrec.setDisabled(isReadOnly("ExtendedFieldDetailDialog_fieldPrec"));
				}
			}

		} else if (fieldType.equals("DLIST") || fieldType.equals("DMLIST")) {
			this.rowfieldList.setVisible(true);
			this.combofieldList.setVisible(true);
			this.fieldLength.setReadonly(true);
			this.fieldLength.setValue(20);
			if(fieldType.equals("DMLIST")){
				this.fieldLength.setValue(200);
			}

			if (!this.combofieldList.getParent().getLastChild().getId().equals(this.combofieldList.getId())) {
				this.combofieldList.getParent().getLastChild().setVisible(false);
			}
		} else if (fieldType.equals("SLIST") ) {
			this.rowfieldList.setVisible(true);
			Uppercasebox textbox = new Uppercasebox();
			textbox.setId("SListId");
			textbox.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldList"));
			if(!newSelection){
				textbox.setValue(StringUtils.trimToEmpty(getExtendedFieldDetail().getFieldList()));
			}
			
			this.fieldLength.setValue(20);
			
			textbox.setConstraint(new PTStringValidator(
					Labels.getLabel("label_ExtendedFieldDetailDialog_FieldList.value") ,
					PennantRegularExpressions.REGEX_STATICLIST,true));
			
			this.combofieldList.getParent().insertBefore(textbox,this.combofieldList);
			this.combofieldList.setVisible(false);
			this.label_ExtendedFieldDetailDialog_FieldListInstrLabel.setVisible(true);

		} else if(fieldType.equals("CHKB")){
			this.rowMandatory.setVisible(false);
			this.rowUnique.setVisible(false);
			
		} else if(fieldType.equals("RADIO")){
			this.rowUnique.setVisible(false);
			this.rowfieldList.setVisible(true);
			this.fieldLength.setMaxlength(20);
			
			Uppercasebox textbox = new Uppercasebox();
			textbox.setId("RadioList");
			textbox.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldList"));
			textbox.setValue(StringUtils.trimToEmpty(getExtendedFieldDetail().getFieldList()));
			
			textbox.setConstraint(new PTStringValidator(
					Labels.getLabel("label_ExtendedFieldDetailDialog_RadioGroup.value") ,
					PennantRegularExpressions.REGEX_STATICLIST,true));
			
			this.combofieldList.getParent().insertBefore(textbox,this.combofieldList);
			this.combofieldList.setVisible(false);
			
		}else if(isDateType()){
			if(!fieldType.equals("TIME")){
				this.rowConstraint.setVisible(true);
				if(newSelection){
					this.fieldConstraint.getItems().clear();
					fillCombobox(this.fieldConstraint,dateConstraintList,"");
				}else{
					onChangeDateConstraint(getExtendedFieldDetail().getFieldConstraint().split(",")[0],false);
				}
				this.fieldConstraint.addForward("onChange", this.window_ExtendedFieldDetailDialog, "onDateContSelect");
			}
		}

		if(this.rowfieldLength.isVisible()){
			if(isTextType()){
				this.fieldDefaultValue.setStyle("text-align:left;");
				this.fieldDefaultValue.setMaxlength(this.fieldLength.intValue());
			}else if(isNumericType()){
				this.fieldDefaultValue.setStyle("text-align:right;");
				this.fieldDefaultValue.setMaxlength(this.fieldLength.intValue()+1);
				this.fieldDefaultValue.setConstraint(new PTStringValidator(
						Labels.getLabel("label_ExtendedFieldDetailDialog_FieldDefaultValue.value"),
						PennantRegularExpressions.REGEX_NM_AMOUNT,false));
			}
		}

		logger.debug("Leaving");
	}

	public void onDateContSelect(Event event){

		if(this.parent_fieldConstraint.getFellowIfAny("range")!= null){
			this.parent_fieldConstraint.removeChild(this.parent_fieldConstraint.getFellowIfAny("range"));
		}else if(this.parent_fieldConstraint.getFellowIfAny("days") != null){
			this.parent_fieldConstraint.removeChild(this.parent_fieldConstraint.getFellowIfAny("days"));
		}

		if(isDateType() && 
				!StringUtils.trimToEmpty(this.fieldConstraint.getSelectedItem().getValue().toString()).equals("")){

			String constType = this.fieldConstraint.getSelectedItem().getValue().toString();
			onChangeDateConstraint(constType,true);
		}
		
	}
	
	private void onChangeDateConstraint(String constType,boolean newSel){

		if("RANGE".equals(constType)){
			Datebox range_From = new Datebox();
			range_From.setId("range_From");
			range_From.setFormat(PennantConstants.dateFormat);

			Datebox range_To = new Datebox();
			range_To.setId("range_To");
			range_To.setFormat(PennantConstants.dateFormat);

			Hbox hbox = new Hbox(); 
			hbox.setId("range");
			hbox.appendChild(range_From);
			hbox.setStyle("padding-left:10px;");

			hbox.appendChild(new Label(" To "));
			hbox.appendChild(range_To);
			parent_fieldConstraint.appendChild(hbox);
			
			if(!newSel){
				range_From.setValue(DateUtility.getUtilDate(getExtendedFieldDetail().getFieldConstraint().split(",")[1],PennantConstants.dateFormat));
				range_To.setValue(DateUtility.getUtilDate(getExtendedFieldDetail().getFieldConstraint().split(",")[2],PennantConstants.dateFormat));
			}

		}else if("FUTURE_DAYS".equals(constType) || "PAST_DAYS".equals(constType)){

			Hbox hbox = new Hbox(); 
			hbox.setId("days");
			Label label = new Label(" No Of Days : ");
			hbox.appendChild(label);

			Intbox intbox = new Intbox(); 
			intbox.setMaxlength(5);
			intbox.setWidth("50px");
			intbox.setId("noOfDays");
			hbox.appendChild(intbox);
			parent_fieldConstraint.appendChild(hbox);
			
			if(!newSel){
				intbox.setValue(Integer.parseInt(getExtendedFieldDetail().getFieldConstraint().split(",")[1]));
			}
		}
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public ExtendedFieldDialogCtrl getExtendedFieldDialogCtrl() {
		return extendedFieldDialogCtrl;
	}
	public void setExtendedFieldDialogCtrl(ExtendedFieldDialogCtrl extendedFieldDialogCtrl) {
		this.extendedFieldDialogCtrl = extendedFieldDialogCtrl;
	}

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewFieldDetail() {
		return newFieldDetail;
	}
	public void setNewFieldDetail(boolean newFieldDetail) {
		this.newFieldDetail = newFieldDetail;
	}

	private AuditHeader newFieldProcess(ExtendedFieldDetail aExtendedFieldDetail, String tranType) {
		logger.debug("Entering");

		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aExtendedFieldDetail, tranType);
		extendedFieldDetails = new ArrayList<ExtendedFieldDetail>();

		String[] valueParm = new String[1];
		String[] errParm = new String[1];

		if (getExtendedFieldDialogCtrl().getExtendedFieldDetailsList() != null
				&& getExtendedFieldDialogCtrl().getExtendedFieldDetailsList().size() > 0) {
			for (int i = 0; i < getExtendedFieldDialogCtrl().getExtendedFieldDetailsList().size(); i++) {
				ExtendedFieldDetail extendedFieldDetail = getExtendedFieldDialogCtrl().getExtendedFieldDetailsList()
				.get(i);

				if (extendedFieldDetail.getFieldName().equals(aExtendedFieldDetail.getFieldName())
						|| extendedFieldDetail.getFieldLabel().equals(aExtendedFieldDetail.getFieldLabel())) {
					if (isNewRecord()) {
						if (extendedFieldDetail.getFieldName().equals(aExtendedFieldDetail.getFieldName())) {
							valueParm[0] = aExtendedFieldDetail.getFieldName();
							errParm[0] = PennantJavaUtil.getLabel("label_FieldName") + ":" + valueParm[0];
						} else if (extendedFieldDetail.getFieldLabel().equals(aExtendedFieldDetail.getFieldLabel())) {
							valueParm[0] = aExtendedFieldDetail.getFieldLabel();
							errParm[0] = PennantJavaUtil.getLabel("label_FieldLabel") + ":" + valueParm[0];
						}

						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41001", errParm, valueParm), getUserWorkspace()
								.getUserLanguage()));
						return auditHeader;
					}

					if (tranType == PennantConstants.TRAN_DEL) {
						if (aExtendedFieldDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aExtendedFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							extendedFieldDetails.add(aExtendedFieldDetail);
						} else if (aExtendedFieldDetail.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aExtendedFieldDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aExtendedFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							extendedFieldDetails.add(aExtendedFieldDetail);
						} else if (aExtendedFieldDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < getExtendedFieldDialogCtrl().getExtendedFieldDetailsList().size(); j++) {
								ExtendedFieldDetail detail = getExtendedFieldDialogCtrl().getExtendedFieldDetailsList()
								.get(j);
								if (detail.getModuleId() == aExtendedFieldDetail.getModuleId()
										&& detail.getFieldType().equals(aExtendedFieldDetail.getFieldType())) {
									extendedFieldDetails.add(detail);
								}
							}
						}
					} else {
						if (tranType != PennantConstants.TRAN_UPD) {
							extendedFieldDetails.add(extendedFieldDetail);
						}
					}
				} else {
					extendedFieldDetails.add(extendedFieldDetail);
				}
			}
		}

		if (!recordAdded) {
			extendedFieldDetails.add(aExtendedFieldDetail);
		}
		logger.debug("Leaving");
		return auditHeader;
	}
}
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
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.solutionfactory.ExtendedFieldDetailService;
import com.pennant.backend.util.ExtendedFieldConstants;
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
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SystemMasters/ExtendedFieldDetail
 * /extendedFieldDetailDialog.zul file.
 */
public class ExtendedFieldDetailDialogCtrl extends GFCBaseCtrl<ExtendedFieldDetail> {
	private static final long	serialVersionUID	= -5800673813892917464L;
	private static final Logger logger = Logger.getLogger(ExtendedFieldDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_ExtendedFieldDetailDialog; 	// autowired
	protected Combobox 		moduleId;							// autowired
	protected Uppercasebox 	fieldName; 							// autowired
	protected Combobox 		fieldType; 							// autowired
	protected Intbox	 	fieldLength; 						// autowired
	protected Intbox 		fieldPrec; 							// autowired
	protected Textbox 		fieldLabel; 						// autowired
	protected Checkbox 		fieldMandatory;	 					// autowired
	protected Combobox 		fieldConstraint; 					// autowired
	protected Intbox 		fieldSeqOrder; 						// autowired
	protected Combobox 		combofieldList; 					// autowired
	protected Textbox 		fieldDefaultValue; 					// autowired
	protected Checkbox 		fieldDefaultValue_Boolean; 			// autowired
	protected Combobox 		fieldDefaultValue_Date; 			// autowired
	protected Longbox 		fieldMinValue; 						// autowired
	protected Longbox 		fieldMaxValue; 						// autowired
	protected Checkbox 		fieldUnique; 						// autowired
	protected Label			label_ExtendedFieldDetailDialog_FieldListInstrLabel;
	protected Intbox		fieldMultilinetxt;
	protected Combobox 		parentTag;
	protected Label			label_ExtendedFieldDetailDialog_FieldSeqOrder;
	protected Checkbox 		fieldEditable;

	protected Listbox 		listBoxFieldDet;
	protected Paging 		pagingFieldDetList;
	protected Textbox 		fieldList;

	protected Grid 			grid_label;

	// not auto wired vars
	private ExtendedFieldDetail extendedFieldDetail; // overhanded per param
	private transient ExtendedFieldDetailListCtrl extendedFieldDetailListCtrl; // overhanded per param

	private transient boolean validationOn;

	protected Button btnNew_HolidayDet;

	// ServiceDAOs / Domain Classes
	private transient ExtendedFieldDetailService extendedFieldDetailService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();
	protected Row rowfieldDefaultValue;
	protected Row rowfieldList;
	protected Row rowfieldMinValue;
	protected Row rowfieldMaxValue;
	protected Row rowfieldLength;
	protected Row rowfieldPrec;
	protected Row rowUnique;
	protected Row rowMandatory;
	protected Row rowConstraint;
	protected Row rowfieldMultilinetxt;
	protected Row rowfieldparentTag;
	protected Row rowfieldIsEditable;
	protected Hbox parent_fieldConstraint;
	private boolean newRecord=false;
	private boolean newFieldDetail=false;
	private boolean firstTaskRole=false;
	private int maxSeqNo = 0;
	private ExtendedFieldDialogCtrl extendedFieldDialogCtrl;
	private List<ExtendedFieldDetail> extendedFieldDetails;
	private List<ValueLabel> moduleList = PennantAppUtil.getExtendedModuleList();

	/**
	 * default constructor.<br>
	 */
	public ExtendedFieldDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ExtendedFieldDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected ExtendedFieldDetail
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ExtendedFieldDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ExtendedFieldDetailDialog);

		try {

			if (arguments.containsKey("extendedFieldDetail")) {
				this.extendedFieldDetail = (ExtendedFieldDetail) arguments.get("extendedFieldDetail");
				ExtendedFieldDetail befImage = new ExtendedFieldDetail();
				BeanUtils.copyProperties(this.extendedFieldDetail, befImage);
				this.extendedFieldDetail.setBefImage(befImage);

				setExtendedFieldDetail(this.extendedFieldDetail);
			} else {
				setExtendedFieldDetail(null);
			}

			if(arguments.containsKey("extendedFieldDialogCtrl")){
				setExtendedFieldDialogCtrl((ExtendedFieldDialogCtrl) arguments.get("extendedFieldDialogCtrl"));
				setNewFieldDetail(true);

				if(arguments.containsKey("newRecord")){
					setNewRecord(true);
				}else{
					setNewRecord(false);
				}
				
				if(arguments.containsKey("maxSeqNo")){
					maxSeqNo = (int) arguments.get("maxSeqNo");
				}

				this.extendedFieldDetail.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					setRole((String) arguments.get("roleCode"));
					getUserWorkspace().allocateRoleAuthorities(getRole(), "ExtendedFieldDetailDialog");
				}
				if (arguments.containsKey("firstTaskRole")) {
					this.firstTaskRole =  (boolean) arguments.get("firstTaskRole");
				}
			}

			doLoadWorkFlow(this.extendedFieldDetail.isWorkflow(), this.extendedFieldDetail.getWorkflowId(),
					this.extendedFieldDetail.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "ExtendedFieldDetailDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the extendedFieldDetailListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete extendedFieldDetail here.
			if (arguments.containsKey("extendedFieldDetailListCtrl")) {
				setExtendedFieldDetailListCtrl((ExtendedFieldDetailListCtrl) arguments.get("extendedFieldDetailListCtrl"));
			} else {
				setExtendedFieldDetailListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getExtendedFieldDetail());
		} catch (Exception e) {
			MessageUtil.showError(e);
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
		this.fieldLabel.setMaxlength(50);
		this.fieldLength.setMaxlength(4);
		this.fieldPrec.setMaxlength(2);
		this.fieldSeqOrder.setMaxlength(4);
		this.fieldDefaultValue.setMaxlength(50);
		this.fieldMultilinetxt.setMaxlength(2);

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
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities("ExtendedFieldDetailDialog",getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldDetailDialog_btnNewD"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldDetailDialog_btnEditD"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldDetailDialog_btnDeleteD"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldDetailDialog_btnSaveD"));
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
		MessageUtil.showHelpWindow(event, window_ExtendedFieldDetailDialog);
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
		doWriteBeanToComponents(this.extendedFieldDetail.getBefImage());
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

		fillComboBox(this.fieldType,aExtendedFieldDetail.getFieldType(),PennantStaticListUtil.getFieldType(),"");
		fillComboBox(this.parentTag, aExtendedFieldDetail.getParentTag(), getGroupBoxList(), "");

		if(isTextType()){
			fillComboBox(this.fieldConstraint,aExtendedFieldDetail.getFieldConstraint(),PennantStaticListUtil.getRegexType(),"");
			this.fieldConstraint.setValue(PennantAppUtil.getlabelDesc(aExtendedFieldDetail.getFieldConstraint(), PennantStaticListUtil.getRegexType()));
		}else if(isDateType()){
			fillComboBox(this.fieldConstraint,aExtendedFieldDetail.getFieldConstraint().split(",")[0],PennantStaticListUtil.getDateType(),"");
			this.fieldConstraint.setValue(PennantAppUtil.getlabelDesc(aExtendedFieldDetail.getFieldConstraint().split(",")[0], PennantStaticListUtil.getDateType()));
		}

		fillComboBox(this.combofieldList, aExtendedFieldDetail.getFieldList(), moduleList, "");
		this.fieldLength.setValue(aExtendedFieldDetail.getFieldLength());
		this.fieldPrec.setValue(aExtendedFieldDetail.getFieldPrec());
		this.fieldLabel.setValue(aExtendedFieldDetail.getFieldLabel());
		this.fieldMandatory.setChecked(aExtendedFieldDetail.isFieldMandatory());
		this.fieldSeqOrder.setValue(aExtendedFieldDetail.getFieldSeqOrder());
		if(StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_BOOLEAN, aExtendedFieldDetail.getFieldType())){
			if(StringUtils.equals(PennantConstants.YES, aExtendedFieldDetail.getFieldDefaultValue())){
				this.fieldDefaultValue_Boolean.setChecked(true);
			}else{
				this.fieldDefaultValue_Boolean.setChecked(false);
			}
		}else if(isDateType()){
			fillComboBox(fieldDefaultValue_Date, aExtendedFieldDetail.getFieldDefaultValue(), 
					getDateDefaultType(aExtendedFieldDetail.getFieldType()), "");
		}else{
			this.fieldDefaultValue.setValue(aExtendedFieldDetail.getFieldDefaultValue());
		}
		this.fieldMinValue.setValue(aExtendedFieldDetail.getFieldMinValue());
		this.fieldMaxValue.setValue(aExtendedFieldDetail.getFieldMaxValue());
		this.fieldUnique.setChecked(aExtendedFieldDetail.isFieldUnique());
		this.fieldMultilinetxt.setValue(aExtendedFieldDetail.getMultiLine());
		this.recordStatus.setValue(aExtendedFieldDetail.getRecordStatus());
		if (StringUtils.isNotBlank(aExtendedFieldDetail.getFieldType())) {
			onFieldTypeChange(aExtendedFieldDetail.getFieldType(), isNewRecord());
		}
		if (aExtendedFieldDetail.isNewRecord()) {
			this.fieldEditable.setChecked(true);
		} else {
			this.fieldEditable.setChecked(aExtendedFieldDetail.isEditable());
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for get the list of parent components in the ExtendedFields except the same parent element and button.
	 * 
	 * @return parentList
	 */
	private List<ValueLabel> getGroupBoxList() {
		List<ValueLabel> parentList = null;
		List<ExtendedFieldDetail> extendedFieldDetail = getExtendedFieldDialogCtrl().getExtendedFieldDetailsList();
		if (extendedFieldDetail != null) {
			parentList = new ArrayList<ValueLabel>();
			for (ExtendedFieldDetail detail : extendedFieldDetail) {
				if (!detail.isInputElement() && !detail.getFieldName().equals(this.fieldName.getValue())
						&& !StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_BUTTON, detail.getFieldType())) {
					parentList.add(new ValueLabel(detail.getFieldName(), detail.getFieldName()));
				}
			}
		}
		return parentList;
	}

	private boolean isGroupContainChilds(String groupName) {
		List<ExtendedFieldDetail> extendedFieldDetail=getExtendedFieldDialogCtrl().getExtendedFieldDetailsList();
		for(ExtendedFieldDetail detail:extendedFieldDetail){
			if(StringUtils.equals(groupName,detail.getParentTag())){
				return true;
			}
		}
		return false;
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
				"PRECISION,WITH,EXECUTE,PRIMARY,EXISTS,PRINT,WRITETEXT,EXIT,PROC,VERSION,LASTMNTON,LASTMNTBY,"+
				"RECORDSTATUS,ROLECODE,NEXTROLECODE,TASKID,NEXTTASKID,RECORDTYPE,WORKFLOWID,DATE,LONG,";

		try {
			this.fieldName.getValue();			
			if(reservedKeys.contains(","+this.fieldName.getValue().toUpperCase()+",")){
				throw new WrongValueException(this.fieldName, Labels.getLabel("RESERVED_KEYS", new String[]{Labels.getLabel("label_ExtendedFieldDetailDialog_FieldName.value")}));
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
			
			int seqNo = this.fieldSeqOrder.intValue();
			if(seqNo == 0){
				seqNo = maxSeqNo + 10;
				if(seqNo > 1000){
					seqNo = 1000;
				}
			}
			aExtendedFieldDetail.setFieldSeqOrder(seqNo);
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

						if ("RANGE".equals(value)) {
							Datebox rangeFrom = null;
							Datebox rangeTo = null;

							if (this.parent_fieldConstraint.getFellowIfAny("range_From") != null) {
								rangeFrom = (Datebox) this.parent_fieldConstraint.getFellowIfAny("range_From");
								if (rangeFrom.getValue() == null) {
									throw new WrongValueException(rangeFrom,  Labels.getLabel("FIELD_IS_MAND", new String[] {"From Date"}));
								}
								value = value +","+DateUtility.formatUtilDate(rangeFrom.getValue(),PennantConstants.dateTimeFormat);
							}

							if (this.parent_fieldConstraint.getFellowIfAny("range_To") != null) {
								rangeTo = (Datebox) this.parent_fieldConstraint.getFellowIfAny("range_To");
								if (rangeTo.getValue() == null) {
									throw new WrongValueException(rangeTo, Labels.getLabel("FIELD_IS_MAND", new String[] {"To Date"}));
								}

								if (rangeTo.getValue().compareTo(rangeFrom.getValue()) <= 0) {
									throw new WrongValueException(rangeTo, Labels.getLabel("DATE_ALLOWED_MINDATE",
											new String[] { "To Date", "From Date" }));
								}
								value = value +","+DateUtility.formatUtilDate(rangeTo.getValue(),PennantConstants.dateTimeFormat);
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

		if (aExtendedFieldDetail.getFieldType() != null && (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_TEXT,aExtendedFieldDetail.getFieldType())
				|| StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_UPPERTEXT,aExtendedFieldDetail.getFieldType())
				|| StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_MULTILINETEXT,aExtendedFieldDetail.getFieldType()))) {
			aExtendedFieldDetail.setFieldPrec(0);
			aExtendedFieldDetail.setFieldList("");
			aExtendedFieldDetail.setFieldDefaultValue(this.fieldDefaultValue.getValue());
			aExtendedFieldDetail.setFieldMinValue(0);
			aExtendedFieldDetail.setFieldMaxValue(0);

			try {
				if (this.fieldLength.intValue() == 0) {
					throw new WrongValueException(this.fieldLength, Labels.getLabel("FIELD_NO_EMPTY",
							Labels.getLabel("label_ExtendedFieldDetailDialog_FieldLength.value")));
				}
				aExtendedFieldDetail.setFieldLength(this.fieldLength.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		try {
			aExtendedFieldDetail.setMultiLine(this.fieldMultilinetxt.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (aExtendedFieldDetail.getFieldType() != null && (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_INT,aExtendedFieldDetail.getFieldType())
				|| StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_LONG,aExtendedFieldDetail.getFieldType())
				|| StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_PERCENTAGE,aExtendedFieldDetail.getFieldType()))) {

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

		if (aExtendedFieldDetail.getFieldType() != null && (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_AMOUNT,aExtendedFieldDetail.getFieldType()) 
				|| StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_ACTRATE,aExtendedFieldDetail.getFieldType())
				|| StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_DECIMAL,aExtendedFieldDetail.getFieldType()))) {

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

		if (aExtendedFieldDetail.getFieldType() != null &&  
				StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_STATICCOMBO,aExtendedFieldDetail.getFieldType())) {

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
					
					if(statList.length > 10){
						throw new WrongValueException(text, Labels.getLabel("NUMBER_MAXVALUE_EQ",
								new String[]{Labels.getLabel("label_ExtendedFieldDetailDialog_FieldList.value"),"10 Items"}));
					}
					Map<String,Boolean> fieldList = new HashMap<String,Boolean>();
					for (String statValue : statList) {
						if(fieldList.containsKey(statValue)){
							throw new WrongValueException(text, Labels.getLabel("FIELD_REPEAT",
									new String[]{Labels.getLabel("label_ExtendedFieldDetailDialog_FieldList.value"),""}));
						}
						fieldList.put(statValue, false);
						if(statValue.length() == 0){
							throw new WrongValueException(text, Labels.getLabel("NOEMPTY_LISTVALUE"));
						}
						if(statValue.length() > this.fieldLength.intValue()){
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
		if (aExtendedFieldDetail.getFieldType() != null && 
				StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_MULTISTATICCOMBO,aExtendedFieldDetail.getFieldType())) {
			
			try {
				
				aExtendedFieldDetail.setFieldLength(200);
				aExtendedFieldDetail.setFieldPrec(0);
				aExtendedFieldDetail.setFieldMinValue(0);
				aExtendedFieldDetail.setFieldMaxValue(0);
				aExtendedFieldDetail.setFieldDefaultValue("");
				if (grid_label.getFellowIfAny("SListId") != null){
					Textbox text=(Textbox) grid_label.getFellowIfAny("SListId");
					String[] statList = text.getValue().split(",");
					if(statList.length > 10){
						throw new WrongValueException(text, Labels.getLabel("NUMBER_MAXVALUE_EQ",
								new String[]{Labels.getLabel("label_ExtendedFieldDetailDialog_FieldList.value"),"10 Items"}));
					}
					Map<String,Boolean> fieldList = new HashMap<String,Boolean>();
					for (String statValue : statList) {
						if(fieldList.containsKey(statValue)){
							throw new WrongValueException(text, Labels.getLabel("FIELD_REPEAT",
									new String[]{Labels.getLabel("label_ExtendedFieldDetailDialog_FieldList.value"),""}));
						}
						fieldList.put(statValue, false);
						if(statValue.length() == 0){
							throw new WrongValueException(text, Labels.getLabel("NOEMPTY_LISTVALUE"));
						}
						if(statValue.length() > 20){
							throw new WrongValueException(text, Labels.getLabel("FIELD_LEN_RENGE",
									new String[]{Labels.getLabel("label_ExtendedFieldDetailDialog_FieldList.value"),
									String.valueOf(20)}));
						}
					}
					aExtendedFieldDetail.setFieldList(text.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		if (aExtendedFieldDetail.getFieldType() != null && 
				StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_RADIO,aExtendedFieldDetail.getFieldType())) {

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
				"BOOLEAN".equals(aExtendedFieldDetail.getFieldType())) {
			aExtendedFieldDetail.setFieldLength(0);
			aExtendedFieldDetail.setFieldPrec(0);
			aExtendedFieldDetail.setFieldMinValue(0);
			aExtendedFieldDetail.setFieldMaxValue(0);
			aExtendedFieldDetail.setFieldDefaultValue("");
			aExtendedFieldDetail.setFieldList("");
		}

		if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_EXTENDEDCOMBO,aExtendedFieldDetail.getFieldType()) || 
				StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_MULTIEXTENDEDCOMBO,aExtendedFieldDetail.getFieldType())) {
			try {
				if (StringUtils.isEmpty(this.combofieldList.getValue())) {
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

		if (aExtendedFieldDetail.getFieldType() != null && (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_BOOLEAN,aExtendedFieldDetail.getFieldType())
				|| StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_DATE,aExtendedFieldDetail.getFieldType())
				|| StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_DATETIME,aExtendedFieldDetail.getFieldType())
				|| StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_TIME,aExtendedFieldDetail.getFieldType()))) {
			aExtendedFieldDetail.setFieldPrec(0);
			aExtendedFieldDetail.setFieldMinValue(0);
			aExtendedFieldDetail.setFieldMaxValue(0);
			aExtendedFieldDetail.setFieldLength(0);
			aExtendedFieldDetail.setFieldList("");
			
			if(StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_BOOLEAN,aExtendedFieldDetail.getFieldType())){
				if(this.fieldDefaultValue_Boolean.isChecked()){
					aExtendedFieldDetail.setFieldDefaultValue(PennantConstants.YES);
				}else{
					aExtendedFieldDetail.setFieldDefaultValue(PennantConstants.NO);
				}
			}else{
				aExtendedFieldDetail.setFieldDefaultValue(getComboboxValue(this.fieldDefaultValue_Date));
			}
		}

		try {
			this.fieldDefaultValue.getValue();
			if (aExtendedFieldDetail.getFieldType() != null && isNumericType()
					&& StringUtils.isNotBlank(this.fieldDefaultValue.getValue())) {
				if(StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_PERCENTAGE,aExtendedFieldDetail.getFieldType())){
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
				if(StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_PERCENTAGE,aExtendedFieldDetail.getFieldType())){
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
				if(StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_PERCENTAGE,aExtendedFieldDetail.getFieldType())){
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
				if(this.fieldMaxValue.longValue() > 0){
					if(this.fieldMinValue.longValue() > this.fieldMaxValue.longValue()){
						throw new WrongValueException(fieldMinValue, Labels.getLabel("NUMBER_MAXVALUE_EQ", 
								new String[] { Labels.getLabel("label_ExtendedFieldDetailDialog_FieldMinValue.value") ,
								Labels.getLabel("label_ExtendedFieldDetailDialog_FieldMaxValue.value")}));
					}
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
		try {
			if(!this.parentTag.isDisabled() && this.parentTag.getSelectedIndex()>0){
				aExtendedFieldDetail.setParentTag(this.parentTag.getSelectedItem().getValue().toString());	
			}else{
				aExtendedFieldDetail.setParentTag(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//If the fieldType is groupbox ,tabpanel or button then make inputElement as false otherwise true
		try{
			if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_GROUPBOX, aExtendedFieldDetail.getFieldType())
					|| StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_TABPANEL,aExtendedFieldDetail.getFieldType())
					||StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_BUTTON,aExtendedFieldDetail.getFieldType())) {
				aExtendedFieldDetail.setInputElement(false);
			}else{
				aExtendedFieldDetail.setInputElement(true);
			}
		}catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aExtendedFieldDetail.setEditable(this.fieldEditable.isChecked());
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

			if(isNewFieldDetail()){
				this.window_ExtendedFieldDetailDialog.setHeight("480px");
				this.window_ExtendedFieldDetailDialog.setWidth("800px");
				this.groupboxWf.setVisible(false);
				this.window_ExtendedFieldDetailDialog.doModal() ;
			}else{
				this.window_ExtendedFieldDetailDialog.setWidth("100%");
				this.window_ExtendedFieldDetailDialog.setHeight("100%");
				setDialog(DialogType.EMBEDDED);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ExtendedFieldDetailDialog.onClose();
		}
		logger.debug("Leaving");
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
					PennantRegularExpressions.REGEX_UPPBOX_ALPHANUM_UNDERSCORE,true));
		}

		if (!this.fieldLabel.isReadonly()) {
			this.fieldLabel.setConstraint(new PTStringValidator(Labels.getLabel("label_ExtendedFieldDetailDialog_FieldLabel.value"),
					PennantRegularExpressions.REGEX_FIELDLABEL,true));
		}

		if (!this.fieldLength.isReadonly()) {

			int maxLength = 0;
			int minLength = 1;
			if (StringUtils.equals(getComboboxValue(fieldType), ExtendedFieldConstants.FIELDTYPE_TEXT) ||
					StringUtils.equals(getComboboxValue(fieldType), ExtendedFieldConstants.FIELDTYPE_UPPERTEXT)) {
				maxLength = 100;
			}else if (StringUtils.equals(getComboboxValue(fieldType), ExtendedFieldConstants.FIELDTYPE_MULTILINETEXT)) {
				maxLength = 1000;
			}else if (StringUtils.equals(getComboboxValue(fieldType), ExtendedFieldConstants.FIELDTYPE_STATICCOMBO) ||
					StringUtils.equals(getComboboxValue(fieldType), ExtendedFieldConstants.FIELDTYPE_RADIO)) {
				maxLength = 20;
			}else if (StringUtils.equals(getComboboxValue(fieldType), ExtendedFieldConstants.FIELDTYPE_ACTRATE)) {
				maxLength = 21;
				minLength = 9;
			}else if (StringUtils.equals(getComboboxValue(fieldType), ExtendedFieldConstants.FIELDTYPE_DECIMAL)) {
				maxLength = 21;
			}else if (StringUtils.equals(getComboboxValue(fieldType), ExtendedFieldConstants.FIELDTYPE_INT)) {
				maxLength = 8;
			}else if (StringUtils.equals(getComboboxValue(fieldType), ExtendedFieldConstants.FIELDTYPE_LONG)) {
				maxLength = 12;
			}

			if (maxLength != 0) {
				this.fieldLength.setConstraint(new PTNumberValidator(Labels.getLabel("label_ExtendedFieldDetailDialog_FieldLength.value"),
						false, false, minLength, maxLength));
			}
		}

		if (!this.fieldPrec.isReadonly()) {
			this.fieldPrec.setConstraint(new PTNumberValidator(Labels.getLabel("label_ExtendedFieldDetailDialog_FieldPrec.value"),
					false, false, 0, 4));
		}

		if (!this.fieldSeqOrder.isReadonly()) {
			this.fieldSeqOrder.setConstraint(new PTNumberValidator(	Labels.getLabel("label_ExtendedFieldDetailDialog_FieldSeqOrder.value"),
					false, false, 0, 1000));
		}

		if (!this.combofieldList.isDisabled() && rowfieldList.isVisible()) {
			this.combofieldList.setConstraint(new StaticListValidator(moduleList, Labels.getLabel("label_ExtendedFieldDetailDialog_FieldList.value")));
		}

		if (!this.fieldDefaultValue.isReadonly() && rowfieldDefaultValue.isVisible()) {
			if(isNumericType()){
				this.fieldDefaultValue.setConstraint(new PTStringValidator(
						Labels.getLabel("label_ExtendedFieldDetailDialog_FieldDefaultValue.value"),
						PennantRegularExpressions.REGEX_NM_AMOUNT,false));
			}
		}
		if(!this.fieldMultilinetxt.isReadonly()){
			if(StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_MULTILINETEXT,getComboboxValue(fieldType))){
				this.fieldMultilinetxt.setConstraint(new PTNumberValidator(	Labels.getLabel("label_ExtendedFieldDetailDialog_FieldMultilinetxt.value"),
						true, false, 1, 10));
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
		this.fieldMultilinetxt.setConstraint("");
		logger.debug("Leaving");
	}

	// CRUD operations

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
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "+
				Labels.getLabel("label_ExtendedFieldDetailDialog_FieldName.value")+" : "+ aExtendedFieldDetail.getFieldName();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if ((!aExtendedFieldDetail.isInputElement() && !isGroupContainChilds(aExtendedFieldDetail.getFieldName())
					|| aExtendedFieldDetail.isInputElement())) {
			if (StringUtils.isBlank(aExtendedFieldDetail.getRecordType())) {
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
						closeDialog();
					}	

				}else if(doProcess(aExtendedFieldDetail,tranType)){
					refreshList();
					closeDialog();
				}
			}catch (DataAccessException e){
				logger.error("Exception: ", e);
				showMessage(e);
			}
			} else {
				final String errMsg = Labels.getLabel("message.error.unable_to_delete_childs_are_avaliable")
						+ "\n\n --> " + Labels.getLabel("label_ExtendedFieldDetailDialog_FieldName.value") + " : "
						+ aExtendedFieldDetail.getFieldName();
				MessageUtil.showError(errMsg);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		
		if(StringUtils.equalsIgnoreCase("NOOFUNITS", getExtendedFieldDetail().getFieldName()) || 
				StringUtils.equalsIgnoreCase("UNITPRICE", getExtendedFieldDetail().getFieldName())){
			this.fieldName.setReadonly(true);
			this.fieldLabel.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldLabel"));
			this.fieldType.setDisabled(true);
			this.fieldLength.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldLength"));
			this.fieldPrec.setReadonly(true);
			this.fieldMandatory.setDisabled(true);
			this.fieldConstraint.setDisabled(true);
			this.fieldSeqOrder.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldSeqOrder"));
			this.combofieldList.setDisabled(true);
			this.fieldDefaultValue.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldDefaultValue"));
			this.fieldDefaultValue_Boolean.setDisabled(isReadOnly("ExtendedFieldDetailDialog_fieldDefaultValue"));
			this.fieldDefaultValue_Date.setDisabled(isReadOnly("ExtendedFieldDetailDialog_fieldDefaultValue"));
			this.fieldMinValue.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldMinValue"));
			this.fieldMaxValue.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldMaxValue"));
			this.fieldUnique.setDisabled(true);
			this.fieldMultilinetxt.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldMultilinetxt"));
			
		}else{
			if (isNewRecord()) {
				this.fieldName.setReadonly(false);
				if (isNewFieldDetail()) {
					this.btnCancel.setVisible(false);
				}
			} else {
				this.fieldName.setReadonly(true);
				this.btnCancel.setVisible(true);
			}

			this.fieldLabel.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldLabel"));
			this.fieldType.setDisabled(isReadOnly("ExtendedFieldDetailDialog_fieldType"));
			this.fieldLength.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldLength"));
			this.fieldPrec.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldPrec"));
			this.fieldMandatory.setDisabled(isReadOnly("ExtendedFieldDetailDialog_fieldMandatory"));
			this.fieldConstraint.setDisabled(isReadOnly("ExtendedFieldDetailDialog_fieldConstraint"));
			this.fieldSeqOrder.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldSeqOrder"));
			this.combofieldList.setDisabled(isReadOnly("ExtendedFieldDetailDialog_fieldList"));
			this.fieldDefaultValue_Boolean.setDisabled(isReadOnly("ExtendedFieldDetailDialog_fieldDefaultValue"));
			this.fieldDefaultValue_Date.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldDefaultValue"));
			this.fieldDefaultValue.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldDefaultValue"));
			this.fieldMinValue.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldMinValue"));
			this.fieldMaxValue.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldMaxValue"));
			this.fieldUnique.setDisabled(isReadOnly("ExtendedFieldDetailDialog_fieldUnique"));
			this.fieldMultilinetxt.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldMultilinetxt"));
			this.parentTag.setDisabled((isReadOnly("ExtendedFieldDetailDialog_parentTag")));
			this.fieldEditable.setDisabled(isReadOnly("ExtendedFieldDetailDialog_fieldEditable"));
		}
		
		boolean isMaintainRcd = false;
		if(!getExtendedFieldDetail().isNewRecord() && StringUtils.isEmpty(getExtendedFieldDetail().getRecordType())){
			this.fieldType.setDisabled(true);
			this.fieldLength.setReadonly(true);
			this.fieldPrec.setReadonly(true);
			this.fieldMandatory.setDisabled(true);
			this.fieldUnique.setDisabled(true);
			
			isMaintainRcd = true;
		}
		
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
					this.btnCtrl.setWFBtnStatus_Edit(firstTaskRole);
					if(isMaintainRcd){
						this.btnDelete.setVisible(false);// For Not Allowing Deletion of Fields.
					}
				}
			}else{
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		
		if(StringUtils.equalsIgnoreCase("NOOFUNITS", getExtendedFieldDetail().getFieldName()) || 
				StringUtils.equalsIgnoreCase("UNITPRICE", getExtendedFieldDetail().getFieldName())){
			this.btnDelete.setVisible(false);
			this.btnDelete.setDisabled(true);
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
		this.fieldDefaultValue_Boolean.setDisabled(true);
		this.fieldDefaultValue_Date.setDisabled(true);
		this.fieldMinValue.setReadonly(true);
		this.fieldMaxValue.setReadonly(true);
		this.fieldUnique.setDisabled(true);
		this.fieldMultilinetxt.setReadonly(true);
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
		this.fieldDefaultValue_Boolean.setChecked(false);
		this.fieldDefaultValue_Date.setSelectedIndex(0);
		this.fieldMinValue.setText("");
		this.fieldMaxValue.setText("");
		this.fieldUnique.setChecked(false);
		this.fieldMultilinetxt.setText("");
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

		// force validation, if on, than execute by component.getValue()
		doRemoveValidation();
		doClearMessage();
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
			if (StringUtils.isBlank(aExtendedFieldDetail.getRecordType())) {
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

				if(StringUtils.isBlank(aExtendedFieldDetail.getRecordType())){
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
					closeDialog();
				}

			}else if (doProcess(aExtendedFieldDetail, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(ExtendedFieldDetail aExtendedFieldDetail, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aExtendedFieldDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aExtendedFieldDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aExtendedFieldDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aExtendedFieldDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aExtendedFieldDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aExtendedFieldDetail);
				}

				if (isNotesMandatory(taskId, aExtendedFieldDetail)) {
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

			aExtendedFieldDetail.setTaskId(taskId);
			aExtendedFieldDetail.setNextTaskId(nextTaskId);
			aExtendedFieldDetail.setRoleCode(getRole());
			aExtendedFieldDetail.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aExtendedFieldDetail, tranType);

			String operationRefs = getServiceOperations(taskId, aExtendedFieldDetail);

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

				if (StringUtils.isBlank(method)) {
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
						deleteNotes(getNotes(this.extendedFieldDetail), true);
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

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
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
			logger.error("Exception: ", exp);
		}
	}

	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.extendedFieldDetail);
	}

	private void doSetLOVValidation() {
	}

	private void doRemoveLOVValidation() {
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.extendedFieldDetail.getModuleId());
	}
	@Override
	protected void doClearMessage() {
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
		this.fieldMultilinetxt.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void refreshList() {
		getExtendedFieldDetailListCtrl().search();
	}

	public void onChange$fieldType(Event event) {
		if (!("").equals(this.fieldType.getSelectedItem().getValue())) {
			onFieldTypeChange(this.fieldType.getSelectedItem().getValue().toString(),true);
		}
	}

	public void onChange$fieldLength(Event event) {

		int length = this.fieldLength.intValue();
		if (StringUtils.equals(getComboboxValue(fieldType), ExtendedFieldConstants.FIELDTYPE_ACTRATE) ||
				StringUtils.equals(getComboboxValue(fieldType), ExtendedFieldConstants.FIELDTYPE_DECIMAL) ||
				StringUtils.equals(getComboboxValue(fieldType), ExtendedFieldConstants.FIELDTYPE_PERCENTAGE)) {
			length = length +1;
		}
		this.fieldDefaultValue.setMaxlength(length);
		this.fieldMinValue.setMaxlength(length);
		this.fieldMaxValue.setMaxlength(length);
	}

	private boolean isTextType() {
		String type = this.fieldType.getSelectedItem().getValue().toString();
		return "|TEXT|UPPERTEXT|MULTILINETEXT|".contains("|" + type + "|");
	}

	private boolean isDateType() {
		String type = this.fieldType.getSelectedItem().getValue().toString();
		return "|DATE|DATETIME|TIME|".contains("|" + type + "|");
	}

	private boolean isNumericType() {
		String type = this.fieldType.getSelectedItem().getValue().toString();
		return "|CURRENCY|ACTRATE|DECIMAL|INT|LONG|PERCENTAGE|".contains("|" + type + "|");
	}

	private boolean isListType() {
		String type = this.fieldType.getSelectedItem().getValue().toString();
		return "|EXTENDEDCOMBO|STATICCOMBO|MULTISTATICCOMBO|MULTIEXTENDEDCOMBO|RADIO|".contains("|" + type + "|");
	}

	private void onFieldTypeChange(String fieldType, boolean newSelection) {
		logger.debug("Entering");

		if(StringUtils.equals(PennantConstants.List_Select,fieldType)){
			this.fieldLength.setText("");
			this.fieldPrec.setText("");
			this.fieldLength.setReadonly(false);
			this.fieldPrec.setReadonly(false);
			this.fieldMandatory.setChecked(false);
			this.fieldUnique.setChecked(false);
			fillComboBox(this.fieldConstraint,"",PennantStaticListUtil.getRegexType(),"");
			this.fieldConstraint.setSelectedIndex(0);
			this.fieldSeqOrder.setText("");
			this.rowfieldLength.setVisible(true);
			this.rowfieldPrec.setVisible(true);
			this.rowMandatory.setVisible(true);
			this.rowUnique.setVisible(true);
			this.rowConstraint.setVisible(true);
			this.rowfieldDefaultValue.setVisible(false);
			this.rowfieldList.setVisible(false);
			this.rowfieldMinValue.setVisible(false);
			this.rowfieldMaxValue.setVisible(false);
			this.rowfieldMultilinetxt.setVisible(false);
			
		}else{

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
			this.rowfieldMultilinetxt.setVisible(false);

			doClearMessage();

			this.rowfieldDefaultValue.setVisible(false);
			this.fieldDefaultValue.setVisible(false);
			this.fieldDefaultValue_Boolean.setVisible(false);
			this.fieldDefaultValue_Date.setVisible(false);
			this.rowfieldList.setVisible(false);
			this.rowfieldMinValue.setVisible(false);
			this.rowfieldMaxValue.setVisible(false);
			this.rowMandatory.setVisible(true);
			this.rowUnique.setVisible(false);
			this.fieldLength.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldLength"));
			this.fieldPrec.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldPrec"));
			this.rowConstraint.setVisible(false);
			this.label_ExtendedFieldDetailDialog_FieldListInstrLabel.setVisible(false);

			if(newSelection){
				this.fieldDefaultValue.setValue("");
			}

			if (isTextType()) {
				this.rowfieldDefaultValue.setVisible(true);
				this.fieldDefaultValue.setVisible(true);
				this.rowConstraint.setVisible(true);
				if(newSelection){
					this.fieldConstraint.getItems().clear();
					fillComboBox(this.fieldConstraint,"",PennantStaticListUtil.getRegexType(),"");
				}

				if(StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_MULTILINETEXT, fieldType)){
					this.rowfieldMultilinetxt.setVisible(true);
				}else{
					this.rowUnique.setVisible(true);
				}
			} else if (isNumericType()) {
				this.rowfieldDefaultValue.setVisible(true);
				this.fieldDefaultValue.setVisible(true);
				this.rowfieldMinValue.setVisible(true);
				this.rowfieldMaxValue.setVisible(true);
				this.fieldPrec.setReadonly(true);

				if(newSelection){
					// Set the default values
					if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_AMOUNT, fieldType)) {
						this.fieldLength.setValue(18);
						this.fieldLength.setReadonly(true);
						this.fieldPrec.setValue(0);
					} else if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_ACTRATE, fieldType)) {
						this.fieldLength.setValue(13);
						this.fieldPrec.setValue(9);
					} else if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_DECIMAL, fieldType)) {
						this.fieldLength.setValue(18);
						this.fieldPrec.setValue(0);
						this.fieldPrec.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldPrec"));
					} else if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_PERCENTAGE, fieldType)) {
						this.fieldLength.setValue(5);
						this.fieldPrec.setValue(2);
						this.fieldLength.setReadonly(true);
					} else if(StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_INT, fieldType)){
						this.fieldLength.setValue(8);
						this.fieldPrec.setValue(0);
						this.rowfieldPrec.setVisible(false);
					} else if(StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_LONG, fieldType)){
						this.fieldLength.setValue(12);
						this.fieldPrec.setValue(0);
						this.rowfieldPrec.setVisible(false);
					}
				}else{
					if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_PERCENTAGE, fieldType) || 
							StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_AMOUNT, fieldType)) {
						this.fieldLength.setReadonly(true);
					} else if(StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_INT, fieldType) || 
							StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_LONG, fieldType)){
						this.rowfieldPrec.setVisible(false);
					} else if(StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_ACTRATE, fieldType)){
						this.fieldPrec.setReadonly(true);
					} else if(StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_DECIMAL, fieldType)){
						this.fieldPrec.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldPrec"));
					}
				}


			} else if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_EXTENDEDCOMBO, fieldType) || 
					StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_MULTIEXTENDEDCOMBO, fieldType)) {
				this.rowfieldList.setVisible(true);
				this.combofieldList.setVisible(true);
				this.fieldLength.setReadonly(true);
				this.fieldLength.setValue(20);
				if(StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_MULTIEXTENDEDCOMBO, fieldType)){
					this.fieldLength.setValue(220);
				}

				if (!this.combofieldList.getParent().getLastChild().getId().equals(this.combofieldList.getId())) {
					this.combofieldList.getParent().getLastChild().setVisible(false);
				}
			} else if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_STATICCOMBO, fieldType)) {
				this.rowfieldList.setVisible(true);
				Uppercasebox textbox = new Uppercasebox();
				textbox.setId("SListId");
				textbox.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldList"));
				if(!newSelection){
					textbox.setValue(StringUtils.trimToEmpty(getExtendedFieldDetail().getFieldList()));
					this.fieldLength.setValue(getExtendedFieldDetail().getFieldLength());
				}else{
					this.fieldLength.setValue(20);
				}
				textbox.setConstraint(new PTStringValidator(
						Labels.getLabel("label_ExtendedFieldDetailDialog_FieldList.value") ,
						PennantRegularExpressions.REGEX_STATICLIST,true));

				this.combofieldList.getParent().insertBefore(textbox,this.combofieldList);
				this.combofieldList.setVisible(false);
				this.label_ExtendedFieldDetailDialog_FieldListInstrLabel.setVisible(true);

			} else if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_MULTISTATICCOMBO, fieldType)) {

				this.rowfieldList.setVisible(true);
				Uppercasebox textbox = new Uppercasebox();
				textbox.setId("SListId");
				textbox.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldList"));
				if(!newSelection){
					textbox.setValue(StringUtils.trimToEmpty(getExtendedFieldDetail().getFieldList()));
				}
				this.fieldLength.setReadonly(true);
				this.fieldLength.setValue(220);
				textbox.setConstraint(new PTStringValidator(
						Labels.getLabel("label_ExtendedFieldDetailDialog_FieldList.value") ,
						PennantRegularExpressions.REGEX_STATICLIST,true));

				this.combofieldList.getParent().insertBefore(textbox,this.combofieldList);
				this.combofieldList.setVisible(false);
				this.label_ExtendedFieldDetailDialog_FieldListInstrLabel.setVisible(true);

			} else if(StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_BOOLEAN, fieldType)){
				this.rowMandatory.setVisible(false);
				this.rowfieldDefaultValue.setVisible(true);
				this.fieldDefaultValue_Boolean.setVisible(true);

			} else if(StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_RADIO, fieldType)){
				this.rowfieldList.setVisible(true);
				this.fieldLength.setValue(20);

				Uppercasebox textbox = new Uppercasebox();
				textbox.setId("RadioList");
				textbox.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldList"));
				textbox.setValue(StringUtils.trimToEmpty(getExtendedFieldDetail().getFieldList()));

				textbox.setConstraint(new PTStringValidator(
						Labels.getLabel("label_ExtendedFieldDetailDialog_RadioGroup.value") ,
						PennantRegularExpressions.REGEX_STATICLIST,true));

				this.combofieldList.getParent().insertBefore(textbox,this.combofieldList);
				this.combofieldList.setVisible(false);
				this.label_ExtendedFieldDetailDialog_FieldListInstrLabel.setVisible(true);

			}else if(StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_EXTENDEDCOMBO, fieldType)){
				Uppercasebox textbox = new Uppercasebox();
				textbox.setId("ExtCmbId");
				textbox.setReadonly(isReadOnly("ExtendedFieldDetailDialog_fieldList"));

			}else if(isDateType()){
				this.rowfieldDefaultValue.setVisible(true);
				this.fieldDefaultValue_Date.setVisible(true);
				if(newSelection){
					fillComboBox(fieldDefaultValue_Date, "", getDateDefaultType(fieldType), "");
				}
				if(!StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_TIME, fieldType)){
					this.rowConstraint.setVisible(true);
					if(newSelection){
						this.fieldConstraint.getChildren().clear();
						while (this.fieldConstraint.getNextSibling() != null) {
							this.fieldConstraint.getNextSibling().detach();
						}
						fillComboBox(this.fieldConstraint,"",PennantStaticListUtil.getDateType(),"");
					}else{
						onChangeDateConstraint(getExtendedFieldDetail().getFieldConstraint().split(",")[0],false);
					}
					this.fieldConstraint.addForward("onChange", this.window_ExtendedFieldDetailDialog, "onDateContSelect");
				}
			}
		
			if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_GROUPBOX, fieldType)
					|| StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_TABPANEL, fieldType)) {
				this.rowfieldDefaultValue.setVisible(false);
				this.rowMandatory.setVisible(false);
				this.rowfieldIsEditable.setVisible(false);
			} else {

				this.rowfieldIsEditable.setVisible(true);
			}

			if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_TABPANEL, fieldType)) {
				this.rowfieldparentTag.setVisible(false);
			} else {
				this.rowfieldparentTag.setVisible(true);
			}
			if(StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_BUTTON, fieldType)){
				this.rowMandatory.setVisible(false);
			}
			
			if(this.rowfieldLength.isVisible()){
				if(isTextType()){
					this.fieldDefaultValue.setStyle("text-align:left;");
					this.fieldDefaultValue.setMaxlength(this.fieldLength.intValue());
				}else if(isNumericType()){
					this.fieldDefaultValue.setStyle("text-align:right;");
					if(StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_INT) || 
							StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_LONG)){

						this.fieldDefaultValue.setMaxlength(this.fieldLength.intValue());
					}else{
						this.fieldDefaultValue.setMaxlength(this.fieldLength.intValue()+1);
					}
					this.fieldDefaultValue.setConstraint(new PTStringValidator(
							Labels.getLabel("label_ExtendedFieldDetailDialog_FieldDefaultValue.value"),
							PennantRegularExpressions.REGEX_NM_AMOUNT,false));
				}
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

		if(isDateType() && this.fieldConstraint.getSelectedIndex() > 0 && 
				StringUtils.isNotBlank(this.fieldConstraint.getSelectedItem().getValue().toString())){

			String constType = this.fieldConstraint.getSelectedItem().getValue().toString();
			onChangeDateConstraint(constType,true);
		}

	}

	private void onChangeDateConstraint(String constType,boolean newSel){

		if("RANGE".equals(constType)){
			Datebox rangeFrom = new Datebox();
			rangeFrom.setId("range_From");
			rangeFrom.setFormat(DateFormat.SHORT_DATE.getPattern());

			Datebox rangeTo = new Datebox();
			rangeTo.setId("range_To");
			rangeTo.setFormat(DateFormat.SHORT_DATE.getPattern());

			Hbox hbox = new Hbox(); 
			hbox.setId("range");
			hbox.appendChild(rangeFrom);
			hbox.setStyle("padding-left:10px;");

			hbox.appendChild(new Label(" To "));
			hbox.appendChild(rangeTo);
			parent_fieldConstraint.appendChild(hbox);

			if(!newSel){
				rangeFrom.setValue(DateUtility.getUtilDate(getExtendedFieldDetail().getFieldConstraint().split(",")[1],PennantConstants.dateFormat));
				rangeTo.setValue(DateUtility.getUtilDate(getExtendedFieldDetail().getFieldConstraint().split(",")[2],PennantConstants.dateFormat));
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

		if (getExtendedFieldDialogCtrl().getExtendedFieldDetailsList() != null 	&& getExtendedFieldDialogCtrl().getExtendedFieldDetailsList().size() > 0) {
			for (int i = 0; i < getExtendedFieldDialogCtrl().getExtendedFieldDetailsList().size(); i++) {
				ExtendedFieldDetail extendedFieldDetail = getExtendedFieldDialogCtrl().getExtendedFieldDetailsList().get(i);

				if (StringUtils.equalsIgnoreCase(extendedFieldDetail.getFieldName(), aExtendedFieldDetail.getFieldName())) {
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

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (PennantConstants.RECORD_TYPE_UPD.equals(aExtendedFieldDetail.getRecordType())) {
							aExtendedFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							extendedFieldDetails.add(aExtendedFieldDetail);
						} else if (PennantConstants.RCD_ADD.equals(aExtendedFieldDetail.getRecordType())) {
							recordAdded = true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(aExtendedFieldDetail.getRecordType())) {
							aExtendedFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							extendedFieldDetails.add(aExtendedFieldDetail);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(aExtendedFieldDetail.getRecordType())) {
							recordAdded = true;
							for (int j = 0; j < getExtendedFieldDialogCtrl().getExtendedFieldDetailsList().size(); j++) {
								ExtendedFieldDetail detail = getExtendedFieldDialogCtrl().getExtendedFieldDetailsList()
										.get(j);
								if (detail.getModuleId() == aExtendedFieldDetail.getModuleId()
										&& detail.getFieldName().equals(aExtendedFieldDetail.getFieldName())) {
									extendedFieldDetails.add(detail);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
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
	
	private ArrayList<ValueLabel> getDateDefaultType(String fielType) {
		
		ArrayList<ValueLabel> dateDefaultTypes = new ArrayList<ValueLabel>(1);
		if(StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_DATE, fielType)){
			dateDefaultTypes.add(new ValueLabel(ExtendedFieldConstants.DFTDATETYPE_APPDATE, Labels.getLabel("label_DateDefaultType_AppDate")));
			dateDefaultTypes.add(new ValueLabel(ExtendedFieldConstants.DFTDATETYPE_SYSDATE, Labels.getLabel("label_DateDefaultType_SysDate")));
		}else if(StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_DATETIME, fielType)){
			dateDefaultTypes.add(new ValueLabel(ExtendedFieldConstants.DFTDATETYPE_APPDATE, Labels.getLabel("label_DateDefaultType_AppDateSysTime")));
			dateDefaultTypes.add(new ValueLabel(ExtendedFieldConstants.DFTDATETYPE_SYSDATE, Labels.getLabel("label_DateDefaultType_SysDateSysTime")));
		}else if(StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_TIME, fielType)){
			dateDefaultTypes.add(new ValueLabel(ExtendedFieldConstants.DFTDATETYPE_SYSTIME, Labels.getLabel("label_DateDefaultType_SysTime")));
		}
		return dateDefaultTypes;
	}
}
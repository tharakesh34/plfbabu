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
 * FileName    		:  EducationalLoanDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2011    														*
 *                                                                  						*
 * Modified Date    :  08-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-10-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.lmtmasters.educationalloan;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.EducationalExpense;
import com.pennant.backend.model.lmtmasters.EducationalLoan;
import com.pennant.backend.service.lmtmasters.EducationalLoanService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.lmtmasters.educationalexpense.model.EducationalExpenseListModelItemRenderer;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/EducationalLoan/educationalLoanDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class EducationalLoanDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 433160262354337142L;
	private final static Logger logger = Logger.getLogger(EducationalLoanDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window         window_EducationalLoanDialog;               // autoWired
	protected Textbox        loanRefNumber;                              // autoWired
	protected Checkbox       loanRefType;                                // autoWired
	protected ExtendedCombobox   eduCourse;                                  // autoWired
	protected Textbox        eduSpecialization;                          // autoWired
	protected ExtendedCombobox   eduCourseType;                              // autoWired
	protected Textbox        eduCourseFrom;                              // autoWired
	protected Textbox        eduCourseFromBranch;                        // autoWired
	protected Textbox        eduAffiliatedTo;                            // autoWired
	protected Datebox        eduCommenceDate;                            // autoWired
	protected Datebox        eduCompletionDate;                          // autoWired
	protected CurrencyBox     eduExpectedIncome;                          // autoWired
	protected ExtendedCombobox   eduLoanFromBranch;                          // autoWired
	protected Listbox        listbox_EduExpenseDetails;                  // autoWired
	protected Paging         pagingEduExpenseDetailsList;                // autoWired
	protected Caption		 caption_eduLoan;
	
	protected Label          recordStatus;                               // autoWired
	protected Radiogroup     userAction;                                 // autoWired
	protected Groupbox       groupboxWf;                                 // autoWired
	protected Row            statusRow;                                  // autoWired
	
	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_EducationalLoanDialog_";
	private transient       ButtonStatusCtrl btnCtrl;
	protected Button         btnNew;                                     // autoWired
	protected Button         btnEdit;                                    // autoWired
	protected Button         btnDelete;                                  // autoWired
	protected Button         btnSave;                                    // autoWired
	protected Button         btnCancel;                                  // autoWired
	protected Button         btnClose;                                   // autoWired
	protected Button         btnHelp;                                    // autoWired
	protected Button         btnNotes;                                   // autoWired
	protected Button         btnNew_EducationalExpense;                  // autoWired
	

	// not auto wired variables
	private EducationalLoan                   educationalLoan;           // over handed per parameters
	private transient EducationalLoanListCtrl educationalLoanListCtrl;   // over handed per parameters
	
	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String  		oldVar_loanRefNumber;
	private transient boolean  		oldVar_loanRefType;
	private transient String  		oldVar_eduCourse;
	private transient String  		oldVar_eduSpecialization;
	private transient String  		oldVar_eduCourseType;
	private transient String  		oldVar_eduCourseFrom;
	private transient String  		oldVar_eduCourseFromBranch;
	private transient String  		oldVar_eduAffiliatedTo;
	private transient Date  		oldVar_eduCommenceDate;
	private transient Date  		oldVar_eduCompletionDate;
	private transient BigDecimal  	oldVar_eduExpectedIncome;
	private transient String  		oldVar_eduLoanFromBranch;
	private transient String        oldVar_recordStatus;
	private transient String 		oldVar_lovDescEduCourseTypeName;
	private transient String        oldVar_lovDescEduCourseName;
	private transient String 		oldVar_lovDescEduLoanFromBranchName;
	private transient boolean       validationOn;
	private boolean                 notes_Entered=false;
	private transient boolean       newFinance;
	private transient int   ccyFormatter = 0;

	// ServiceDAOs / Domain Classes
	private transient EducationalLoanService         educationalLoanService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	private List<EducationalExpense>             eduExpenseDetailList=new ArrayList<EducationalExpense>();
	private PagedListWrapper<EducationalExpense> eduExpenseDetailPagedListWrapper;

	//For Dynamically calling of this Controller
	private Div toolbar;
	private Object financeMainDialogCtrl;
	private Tabpanel panel = null;
	private Grid grid_eduLoanDetails;
	private transient boolean recSave = false;
	private boolean isEnquiry = false;
	
	/**
	 * default constructor.<br>
	 */
	public EducationalLoanDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected EducationalLoan object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_EducationalLoanDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		if(event.getTarget().getParent() != null){
			panel = (Tabpanel) event.getTarget().getParent();
		}
		
		setEduExpenseDetailPagedListWrapper();
		
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, 
				true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave,
				this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("educationalLoan")) {
			this.educationalLoan = (EducationalLoan) args.get("educationalLoan");
			EducationalLoan befImage =new EducationalLoan();
			BeanUtils.copyProperties(this.educationalLoan, befImage);
			this.educationalLoan.setBefImage(befImage);
			setEducationalLoan(this.educationalLoan);
		} else {
			setEducationalLoan(null);
		}

		if(args.containsKey("financeMainDialogCtrl")){
			this.financeMainDialogCtrl = (Object) args.get("financeMainDialogCtrl");
			try {
				financeMainDialogCtrl.getClass().getMethod("setChildWindowDialogCtrl", Object.class).invoke(financeMainDialogCtrl, this);
			} catch (Exception e) {
				logger.error(e);
			}
			setNewFinance(true);
			this.educationalLoan.setWorkflowId(0);
			this.window_EducationalLoanDialog.setTitle("");
			this.caption_eduLoan.setVisible(true);
		}
		
		if(args.containsKey("roleCode")){
			setRole((String) args.get("roleCode"));
			getUserWorkspace().alocateRoleAuthorities(getRole(), "EducationalLoanDialog");
		}
		
		if(args.containsKey("ccyFormatter")){
			this.ccyFormatter = (Integer)args.get("ccyFormatter");
		}
		
		if (args.containsKey("isEnquiry")) {
			isEnquiry = (Boolean) args.get("isEnquiry");
		}
		
		doLoadWorkFlow(this.educationalLoan.isWorkflow(),this.educationalLoan.getWorkflowId(),
				this.educationalLoan.getNextTaskId());

		if (isWorkFlowEnabled() && !isNewFinance()) {
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "EducationalLoanDialog");
		}

		/* set components visible dependent of the users rights */
		doCheckRights();
		
		// READ OVERHANDED params !
		// we get the educationalLoanListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete educationalLoan here.
		if (args.containsKey("educationalLoanListCtrl")) {
			setEducationalLoanListCtrl((EducationalLoanListCtrl) args.get("educationalLoanListCtrl"));
		} else {
			setEducationalLoanListCtrl(null);
		}
		
		this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue() - PennantConstants.borderlayoutMainNorth;
		
		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getEducationalLoan());
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.eduCourse.setMaxlength(10);
        this.eduCourse.setMandatoryStyle(true);
		this.eduCourse.setModuleName("Course");
		this.eduCourse.setValueColumn("CourseName");
		this.eduCourse.setDescColumn("CourseDesc");
		this.eduCourse.setValidateColumns(new String[] { "CourseName" });
		
		this.eduCourseType.setMaxlength(20);
        this.eduCourseType.setMandatoryStyle(true);
		this.eduCourseType.setModuleName("CourseType");
		this.eduCourseType.setValueColumn("CourseTypeCode");
		this.eduCourseType.setDescColumn("CourseTypeDesc");
		this.eduCourseType.setValidateColumns(new String[] { "CourseTypeCode" });
		
		this.eduLoanFromBranch.setMaxlength(8);
        this.eduLoanFromBranch.setMandatoryStyle(false);
		this.eduLoanFromBranch.setModuleName("Branch");
		this.eduLoanFromBranch.setValueColumn("BranchCode");
		this.eduLoanFromBranch.setDescColumn("BranchDesc");
		this.eduLoanFromBranch.setValidateColumns(new String[] { "BranchCode" });
		
		
		this.eduSpecialization.setMaxlength(50);
		this.eduCourseFrom.setMaxlength(100);
		this.eduAffiliatedTo.setMaxlength(100);
		this.eduCommenceDate.setFormat(PennantConstants.dateFormat);
		this.eduCompletionDate.setFormat(PennantConstants.dateFormat);
				
		this.eduExpectedIncome.setMandatory(true);
		this.eduExpectedIncome.setMaxlength(18);
		this.eduExpectedIncome.setFormat(PennantApplicationUtil.getAmountFormate(this.ccyFormatter));
		
		
		this.eduCourseFromBranch.setMaxlength(50);

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
		}

		logger.debug("Leaving") ;
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
		logger.debug("Entering") ;
		getUserWorkspace().alocateAuthorities("EducationalLoanDialog", getRole());
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_EducationalLoanDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_EducationalLoanDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_EducationalLoanDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_EducationalLoanDialog_btnSave"));
		this.btnCancel.setVisible(false);
		
		this.btnNew_EducationalExpense.setVisible(getUserWorkspace().isAllowed(
					"button_EducationalLoanDialog_btnNew_EducationalExpense"));
		
		logger.debug("Leaving") ;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	@SuppressWarnings("unchecked")
	public void onAssetValidation(Event event){
		logger.debug("Entering" + event.toString());
		
		String userAction = "";
		Map<String,Object> map = new HashMap<String,Object>();
		if(event.getData() != null){
			map = (Map<String, Object>) event.getData();
		}
		if(map.containsKey("userAction")){
			userAction = (String) map.get("userAction");
		}
		
		doClearMessage();
		if(("Save".equalsIgnoreCase(userAction) || "Cancel".equalsIgnoreCase(userAction))
				&& !map.containsKey("agreement")){
			recSave = true;
		}else{
			doSetValidation();
		}
		doWriteComponentsToBean(getEducationalLoan());
		if(StringUtils.trimToEmpty(getEducationalLoan().getRecordType()).equals("")){
			getEducationalLoan().setVersion(getEducationalLoan().getVersion() + 1);
			getEducationalLoan().setRecordType(PennantConstants.RECORD_TYPE_NEW);
			getEducationalLoan().setNewRecord(true);
		}
		//this.financeMainDialogCtrl.getFinanceDetail().setEducationalLoan(getEducationalLoan());
		try {
			financeMainDialogCtrl.getClass().getMethod("setEducationalLoanDetail", EducationalLoan.class).invoke(financeMainDialogCtrl, this.getEducationalLoan());
		} catch (Exception e) {
			logger.error(e);
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method for checking whethter data has been changed before closing
	 * @param event
	 * @return 
	 * */
	public void onAssetClose(Event event){
		logger.debug("Entering" + event.toString());
		//this.financeMainDialogCtrl.setAssetDataChanged(isDataChanged());
		
		try {
			financeMainDialogCtrl.getClass().getMethod("setAssetDataChanged", Boolean.class).invoke(financeMainDialogCtrl, this.isDataChanged());
		} catch (Exception e) {
			logger.error(e);
		}
		
		
		
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_EducationalLoanDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doClose();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		doSave();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering " + event.toString());
		doEdit();
		// remember the old variables
		doStoreInitValues();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		PTMessageUtils.showHelpWindow(event, window_EducationalLoanDialog);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering " + event.toString());
		doNew();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		doDelete();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering " + event.toString());
		doCancel();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 *  when clicks on  "btnNew_DetailsOfExpense" 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNew_EducationalExpense(Event event) throws Exception{
		logger.debug("Entering " + event.toString());
		
		EducationalExpense educationalExpense = new EducationalExpense();
		EducationalLoan educationalLoan = new EducationalLoan();
		doWriteComponentsToBean(educationalLoan);
		educationalExpense.setNewRecord(true);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("educationalLoanDialogCtrl", this);
		map.put("educationalLoan",educationalLoan);
		map.put("educationalExpense", educationalExpense);
		map.put("roleCode", getRole());
		map.put("ccyFormatter", this.ccyFormatter);
		try {
			Executions.createComponents(
					"/WEB-INF/pages/LMTMasters/EducationalExpense/EducationalExpenseDialog.zul",null,map);

		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving " + event.toString());
	}
	
	public void onEducationalExpenseItemDoubleClicked(Event event)throws Exception{
		logger.debug("Entering " + event.toString());
		
		final Listitem item=this.listbox_EduExpenseDetails.getSelectedItem();
		if(item!=null){	
			final EducationalExpense educationalExpense=(EducationalExpense)item.getAttribute("data");	
			educationalExpense.setNewRecord(false);
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("educationalLoanDialogCtrl", this);
			map.put("educationalLoan",getEducationalLoan());
			map.put("educationalExpense", educationalExpense);
			map.put("roleCode", getRole());
			map.put("ccyFormatter", this.ccyFormatter);
			try {
				Executions.createComponents(
						"/WEB-INF/pages/LMTMasters/EducationalExpense/EducationalExpenseDialog.zul",null,map);

			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}
		logger.debug("Leaving " + event.toString());	
	}
	

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ GUI Process ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

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
		boolean close=true;
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close=false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("isDataChanged : false");
		}

		if(close){
			closeDialog(this.window_EducationalLoanDialog, "EducationalLoanDialog");	
		}

		logger.debug("Leaving") ;
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering") ;
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aEducationalLoan
	 *            EducationalLoan
	 */
	public void doWriteBeanToComponents(EducationalLoan aEducationalLoan) {
		logger.debug("Entering") ;
		this.loanRefNumber.setValue(aEducationalLoan.getLoanRefNumber());
		this.loanRefType.setChecked(aEducationalLoan.isLoanRefType());
		this.eduCourse.setValue(aEducationalLoan.getEduCourse());
		this.eduSpecialization.setValue(aEducationalLoan.getEduSpecialization());
		this.eduCourseType.setValue(aEducationalLoan.getEduCourseType());
		this.eduCourseFrom.setValue(aEducationalLoan.getEduCourseFrom());
		this.eduCourseFromBranch.setValue(aEducationalLoan.getEduCourseFromBranch());
		this.eduAffiliatedTo.setValue(aEducationalLoan.getEduAffiliatedTo());
		this.eduCommenceDate.setValue(aEducationalLoan.getEduCommenceDate());
		this.eduCompletionDate.setValue(aEducationalLoan.getEduCompletionDate());
		this.eduExpectedIncome.setValue(PennantAppUtil.formateAmount(aEducationalLoan.getEduExpectedIncome(),this.ccyFormatter));
		this.eduLoanFromBranch.setValue(aEducationalLoan.getEduLoanFromBranch());

		if (aEducationalLoan.isNewRecord()){
			this.eduCourse.setDescription("");
			this.eduCourseType.setDescription("");
			this.eduLoanFromBranch.setDescription("");
		}else{
			this.eduCourse.setDescription(aEducationalLoan.getLovDescEduCourseName());
			this.eduCourseType.setDescription(aEducationalLoan.getLovDescEduCourseTypeName());
			this.eduLoanFromBranch.setDescription(aEducationalLoan.getLovDescEduLoanFromBranchName());
		}
		this.recordStatus.setValue(aEducationalLoan.getRecordStatus());
		doFillExpenseDetailsList(getEducationalLoan().getEduExpenseList());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aEducationalLoan
	 */
	public void doWriteComponentsToBean(EducationalLoan aEducationalLoan) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aEducationalLoan.setLoanRefNumber(this.loanRefNumber.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aEducationalLoan.setLoanRefType(this.loanRefType.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aEducationalLoan.setLovDescEduCourseName(this.eduCourse.getDescription());
			aEducationalLoan.setEduCourse(this.eduCourse.getValidatedValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aEducationalLoan.setEduSpecialization(this.eduSpecialization.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aEducationalLoan.setLovDescEduCourseTypeName(this.eduCourseType.getDescription());
			aEducationalLoan.setEduCourseType(this.eduCourseType.getValidatedValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aEducationalLoan.setEduCourseFrom(this.eduCourseFrom.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aEducationalLoan.setEduCourseFromBranch(this.eduCourseFromBranch.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aEducationalLoan.setEduAffiliatedTo(this.eduAffiliatedTo.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aEducationalLoan.setEduCommenceDate(this.eduCommenceDate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aEducationalLoan.setEduCompletionDate(this.eduCompletionDate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.eduExpectedIncome.getValue() != null){
				aEducationalLoan.setEduExpectedIncome(PennantAppUtil.unFormateAmount(this.eduExpectedIncome.getValue(), 
						this.ccyFormatter));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
/*		try {
			aEducationalLoan.setLovDescEduLoanFromBranchName(this.eduLoanFromBranch.getDescription());
			aEducationalLoan.setEduLoanFromBranch(this.eduLoanFromBranch.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}*/
		try {
			int i=DateUtility.compare(aEducationalLoan.getEduCompletionDate(),aEducationalLoan.getEduCommenceDate());
			if(i==0 ){
				throw new WrongValueException(
						this.eduCommenceDate,Labels.getLabel("DATES_NOT_SAME",new String[]{Labels.getLabel(
								"label_EducationalLoanDialog_EduCommenceDate.value") ,Labels.getLabel(
										"label_EducationalLoanDialog_EduCompletionDate.value")}));
			}
			if(i<0){
				throw new WrongValueException(
						this.eduCompletionDate,Labels.getLabel("DATE_NOT_BEFORE",new String[]{Labels.getLabel(
								"label_EducationalLoanDialog_EduCompletionDate.value") ,Labels.getLabel(
										"label_EducationalLoanDialog_EduCommenceDate.value")}));
			}
		}

		catch (WrongValueException we ) {
			wve.add(we);
		}
		doRemoveValidation();
		doRemoveLOVValidation();

		if(!recSave){
			if (wve.size()>0) {
				WrongValueException [] wvea = new WrongValueException[wve.size()];
				for (int i = 0; i < wve.size(); i++) {
					wvea[i] = (WrongValueException) wve.get(i);
				}
				if(panel != null){
					((Tab)panel.getParent().getParent().getFellowIfAny("loanAssetTab")).setSelected(true);
				}
				throw new WrongValuesException(wvea);
			}
		}

		aEducationalLoan.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aEducationalLoan
	 * @throws InterruptedException
	 */
	public void doShowDialog(EducationalLoan aEducationalLoan) throws InterruptedException {
		logger.debug("Entering") ;

		// if aEducationalLoan == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aEducationalLoan == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aEducationalLoan = getEducationalLoanService().getNewEducationalLoan();
			setEducationalLoan(aEducationalLoan);
		} else {
			setEducationalLoan(aEducationalLoan);
		}

		// set Read only mode accordingly if the object is new or not.
		if (aEducationalLoan.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.eduCourse.focus();
		} else {
			this.eduCourse.focus();
			if(isNewFinance()){
				doEdit();
			}else if (isWorkFlowEnabled()){
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
			doWriteBeanToComponents(aEducationalLoan);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			doCheckEnquiry();
			if(panel != null){
				this.toolbar.setVisible(false);
				this.groupboxWf.setVisible(false);
				this.statusRow.setVisible(false);
				this.window_EducationalLoanDialog.setHeight(this.borderLayoutHeight - 80 + "px");
				listbox_EduExpenseDetails.setHeight((grid_eduLoanDetails.getRows().getVisibleItemCount()*20+72)+"px");
				//panel.setHeight(grid_eduLoanDetails.getRows().getVisibleItemCount()*40+260+"px");
				panel.appendChild(this.window_EducationalLoanDialog);
			}else{
				setDialog(this.window_EducationalLoanDialog);
			}
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving") ;
	}

	private void doCheckEnquiry() {
		if(isEnquiry){
			this.btnNew_EducationalExpense.setVisible(false);
		}
	}
	
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_loanRefNumber = this.loanRefNumber.getValue();
		this.oldVar_loanRefType = this.loanRefType.isChecked();
		this.oldVar_eduCourse = this.eduCourse.getValue();
		this.oldVar_lovDescEduCourseName = this.eduCourse.getDescription();
		this.oldVar_eduSpecialization = this.eduSpecialization.getValue();
		this.oldVar_eduCourseType = this.eduCourseType.getValue();
		this.oldVar_lovDescEduCourseTypeName = this.eduCourseType.getDescription();
		this.oldVar_eduCourseFrom = this.eduCourseFrom.getValue();
		this.oldVar_eduCourseFromBranch = this.eduCourseFromBranch.getValue();
		this.oldVar_eduAffiliatedTo = this.eduAffiliatedTo.getValue();
		this.oldVar_eduCommenceDate = this.eduCommenceDate.getValue();
		this.oldVar_eduCompletionDate = this.eduCompletionDate.getValue();
		this.oldVar_eduExpectedIncome = this.eduExpectedIncome.getValue();
		this.oldVar_eduLoanFromBranch = this.eduLoanFromBranch.getValue();
		this.oldVar_lovDescEduLoanFromBranchName = this.eduLoanFromBranch.getDescription();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from member vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.loanRefNumber.setValue(this.oldVar_loanRefNumber);
		this.loanRefType.setChecked(this.oldVar_loanRefType);
		this.eduCourse.setValue(this.oldVar_eduCourse);
		this.eduCourse.setDescription(this.oldVar_lovDescEduCourseName);
		this.eduSpecialization.setValue(this.oldVar_eduSpecialization);
		this.eduCourseType.setValue(this.oldVar_eduCourseType);
		this.eduCourseType.setDescription(this.oldVar_lovDescEduCourseTypeName);
		this.eduCourseFrom.setValue(this.oldVar_eduCourseFrom);
		this.eduCourseFromBranch.setValue(this.oldVar_eduCourseFromBranch);
		this.eduAffiliatedTo.setValue(this.oldVar_eduAffiliatedTo);
		this.eduCommenceDate.setValue(this.oldVar_eduCommenceDate);
		this.eduCompletionDate.setValue(this.oldVar_eduCompletionDate);
		this.eduExpectedIncome.setValue(this.oldVar_eduExpectedIncome);
		this.eduLoanFromBranch.setValue(this.oldVar_eduLoanFromBranch);
		this.eduLoanFromBranch.setDescription(this.oldVar_lovDescEduLoanFromBranchName);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if(isWorkFlowEnabled()){
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

		//To clear the Error Messages
		doClearMessage();
		
		if (this.oldVar_loanRefNumber != this.loanRefNumber.getValue()) {
			return true;
		}
		if (this.oldVar_loanRefType != this.loanRefType.isChecked()) {
			return true;
		}
		if (this.oldVar_eduCourse != this.eduCourse.getValue()) {
			return true;
		}
		if (this.oldVar_eduSpecialization != this.eduSpecialization.getValue()) {
			return true;
		}
		if (this.oldVar_eduCourseType != this.eduCourseType.getValue()) {
			return true;
		}
		if (this.oldVar_eduCourseFrom != this.eduCourseFrom.getValue()) {
			return true;
		}
		if (this.oldVar_eduCourseFromBranch != this.eduCourseFromBranch.getValue()) {
			return true;
		}
		if (this.oldVar_eduAffiliatedTo != this.eduAffiliatedTo.getValue()) {
			return true;
		}
		String oldEduCommenceDate = "";
		String newEduCommenceDate ="";
		if (this.oldVar_eduCommenceDate!=null){
			oldEduCommenceDate=DateUtility.formatDate(this.oldVar_eduCommenceDate,PennantConstants.dateFormat);
		}
		if (this.eduCommenceDate.getValue()!=null){
			newEduCommenceDate=DateUtility.formatDate(this.eduCommenceDate.getValue(),
					PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldEduCommenceDate).equals(StringUtils.trimToEmpty(
				newEduCommenceDate))) {
			return true;
		}
		String oldEduCompletionDate = "";
		String newEduCompletionDate ="";
		if (this.oldVar_eduCompletionDate!=null){
			oldEduCompletionDate=DateUtility.formatDate(this.oldVar_eduCompletionDate,
					PennantConstants.dateFormat);
		}
		if (this.eduCompletionDate.getValue()!=null){
			newEduCompletionDate=DateUtility.formatDate(this.eduCompletionDate.getValue(),
					PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldEduCompletionDate).equals(StringUtils.trimToEmpty(
				newEduCompletionDate))) {
			return true;
		}
		if (this.oldVar_eduExpectedIncome != this.eduExpectedIncome.getValue()) {
			return true;
		}
		if (this.oldVar_eduLoanFromBranch != this.eduLoanFromBranch.getValue()) {
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

		if (!this.eduSpecialization.isReadonly()){
			this.eduSpecialization.setConstraint(new PTStringValidator(Labels.getLabel("label_EducationalLoanDialog_EduSpecialization.value"),
					PennantRegularExpressions.REGEX_NAME, true));
		}
		if (!this.eduCourseFrom.isReadonly()){
			this.eduCourseFrom.setConstraint(new PTStringValidator(Labels.getLabel("label_EducationalLoanDialog_EduCourseFrom.value"), 
					PennantRegularExpressions.REGEX_NAME, true));
		}
		if (!this.eduCourseFromBranch.isReadonly()){
			this.eduCourseFromBranch.setConstraint(new PTStringValidator(Labels.getLabel("label_EducationalLoanDialog_EduCourseFromBranch.value"), 
					PennantRegularExpressions.REGEX_NAME, true));
		}
		if (!this.eduAffiliatedTo.isReadonly()){
			this.eduAffiliatedTo.setConstraint(new PTStringValidator(Labels.getLabel("label_EducationalLoanDialog_EduAffiliatedTo.value"), 
					PennantRegularExpressions.REGEX_NAME, true));
		}
		if (!this.eduCommenceDate.isDisabled()){
			this.eduCommenceDate.setConstraint(new PTDateValidator(Labels.getLabel("label_EducationalLoanDialog_EduCommenceDate.value"), true));
		}
		if (!this.eduCompletionDate.isDisabled()){
			this.eduCompletionDate.setConstraint(new PTDateValidator(Labels.getLabel("label_EducationalLoanDialog_EduCompletionDate.value"), true, new Date(), null, false));
		}
		if (!this.eduExpectedIncome.isReadonly()){
			this.eduExpectedIncome.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_EducationalLoanDialog_EduExpectedIncome.value"),false));
		}	
		
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.loanRefNumber.setConstraint("");
		this.eduSpecialization.setConstraint("");
		this.eduCourseFrom.setConstraint("");
		this.eduCourseFromBranch.setConstraint("");
		this.eduAffiliatedTo.setConstraint("");
		this.eduCommenceDate.setConstraint("");
		this.eduCompletionDate.setConstraint("");
		this.eduExpectedIncome.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.loanRefNumber.setReadonly(true);
		this.loanRefType.setDisabled(true);
		this.eduCourse.setReadonly(true);
		this.eduSpecialization.setReadonly(true);
		this.eduCourseType.setReadonly(true);
		this.eduCourseFrom.setReadonly(true);
		this.eduCourseFromBranch.setReadonly(true);
		this.eduAffiliatedTo.setReadonly(true);
		this.eduCommenceDate.setDisabled(true);
		this.eduCompletionDate.setDisabled(true);
		this.eduExpectedIncome.setReadonly(true);
		this.eduLoanFromBranch.setReadonly(true);

		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if(isWorkFlowEnabled()){
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
		this.loanRefNumber.setText("");
		this.loanRefType.setChecked(false);
		this.eduCourse.setValue("");
		this.eduCourse.setDescription("");
		this.eduSpecialization.setValue("");
		this.eduCourseType.setValue("");
		this.eduCourseType.setDescription("");
		this.eduCourseFrom.setValue("");
		this.eduCourseFromBranch.setValue("");
		this.eduAffiliatedTo.setValue("");
		this.eduCommenceDate.setText("");
		this.eduCompletionDate.setText("");
		this.eduExpectedIncome.setValue("");
		this.eduLoanFromBranch.setValue("");
		this.eduLoanFromBranch.setDescription("");
		logger.debug("Leaving");
	}
	
	/**
	 * This method set the lovField validation
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering ");
		this.eduCourse.setConstraint(new PTStringValidator(Labels.getLabel("label_EducationalLoanDialog_EduCourse.value"), null, true));
		this.eduCourseType.setConstraint(new PTStringValidator(Labels.getLabel("label_EducationalLoanDialog_EduCourseType.value"), null, true));
	//	this.eduLoanFromBranch.setConstraint(new PTStringValidator(Labels.getLabel("label_EducationalLoanDialog_EduLoanFromBranch.value"), null, true));
		logger.debug("Leaving ");
	}
	
	/**
	 * This method removes lovFiled validation
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering ");
		this.eduCourse.setConstraint("");
		this.eduCourseType.setConstraint("");
		this.eduLoanFromBranch.setConstraint("");
		logger.debug("Leaving ");
	}
	
	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.loanRefNumber.setErrorMessage("");
		this.eduCourse.setErrorMessage("");
		this.eduSpecialization.setErrorMessage("");
		this.eduCourseType.setErrorMessage("");
		this.eduCourseFrom.setErrorMessage("");
		this.eduCourseFromBranch.setErrorMessage("");
		this.eduAffiliatedTo.setErrorMessage("");
		this.eduCommenceDate.setErrorMessage("");
		this.eduCompletionDate.setErrorMessage("");
		this.eduExpectedIncome.setErrorMessage("");
		this.eduLoanFromBranch.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * 	 Method for refreshing the list after successful Update
	 */
	private void refreshList(){
		logger.debug("Entering ");
		final JdbcSearchObject<EducationalLoan> soEducationalLoan = getEducationalLoanListCtrl().getSearchObj();
		getEducationalLoanListCtrl().pagingEducationalLoanList.setActivePage(0);
		getEducationalLoanListCtrl().getPagedListWrapper().setSearchObject(soEducationalLoan);
		if(getEducationalLoanListCtrl().listBoxEducationalLoan!=null){
			getEducationalLoanListCtrl().listBoxEducationalLoan.getListModel();
		}
		logger.debug("Leaving ");
	} 
	
	/**
	 * This method fills expense details list 
	 * @param expenseDetails
	 */
	@SuppressWarnings("unchecked")
	public void doFillExpenseDetailsList(List<EducationalExpense> expenseDetailList){
		logger.debug("Entering ");
		Comparator<Object> comp = new BeanComparator("lovDescEduExpDetailName");
		Collections.sort(expenseDetailList,comp);
		this.setEduExpenseDetailList(expenseDetailList);
		getEducationalLoan().setEduExpenseList(expenseDetailList);
		this.pagingEduExpenseDetailsList.setPageSize(PennantConstants.listGridSize);
		this.pagingEduExpenseDetailsList.setDetailed(true);
		getEduExpenseDetailPagedListWrapper().initList(expenseDetailList
				, this.listbox_EduExpenseDetails, pagingEduExpenseDetailsList);
		this.listbox_EduExpenseDetails.setItemRenderer(new EducationalExpenseListModelItemRenderer());
		logger.debug("Leaving ");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a EducationalLoan object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final EducationalLoan aEducationalLoan = new EducationalLoan();
		BeanUtils.copyProperties(getEducationalLoan(), aEducationalLoan);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") +
								"\n\n --> "	+ aEducationalLoan.getLoanRefNumber();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aEducationalLoan.getRecordType()).equals("")){
				aEducationalLoan.setVersion(aEducationalLoan.getVersion()+1);
				aEducationalLoan.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aEducationalLoan.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aEducationalLoan,tranType)){
					refreshList();
					closeDialog(this.window_EducationalLoanDialog, "EducationalLoanDialog"); 
				}
			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new EducationalLoan object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		
		// remember the old vars
		doStoreInitValues();

		final EducationalLoan aEducationalLoan = getEducationalLoanService().getNewEducationalLoan();
		setEducationalLoan(aEducationalLoan);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		
		// setFocus
		this.eduCourse.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getEducationalLoan().isNewRecord()){
			this.btnCancel.setVisible(false);
		}else{
			this.btnCancel.setVisible(true);
		}
		this.loanRefNumber.setReadonly(true);
		this.loanRefType.setDisabled(isReadOnly("EducationalLoanDialog_loanRefType"));
		this.eduCourse.setReadonly(isReadOnly("EducationalLoanDialog_eduCourse"));
		this.eduSpecialization.setReadonly(isReadOnly("EducationalLoanDialog_eduSpecialization"));
		this.eduCourseType.setReadonly(isReadOnly("EducationalLoanDialog_eduCourseType"));
		this.eduCourseFrom.setReadonly(isReadOnly("EducationalLoanDialog_eduCourseFrom"));
		this.eduCourseFromBranch.setReadonly(isReadOnly("EducationalLoanDialog_eduCourseFromBranch"));
		this.eduAffiliatedTo.setReadonly(isReadOnly("EducationalLoanDialog_eduAffiliatedTo"));
		this.eduCommenceDate.setDisabled(isReadOnly("EducationalLoanDialog_eduCommenceDate"));
		this.eduCompletionDate.setDisabled(isReadOnly("EducationalLoanDialog_eduCompletionDate"));
		this.eduExpectedIncome.setReadonly(isReadOnly("EducationalLoanDialog_eduExpectedIncome"));
		this.eduLoanFromBranch.setReadonly(isReadOnly("EducationalLoanDialog_eduLoanFromBranch"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.educationalLoan.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}	
		logger.debug("Leaving");
	}
	
	public boolean isReadOnly(String componentName){
		if (isWorkFlowEnabled() || isNewFinance()){
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}
	
	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final EducationalLoan aEducationalLoan = new EducationalLoan();
		BeanUtils.copyProperties(getEducationalLoan(), aEducationalLoan);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the EducationalLoan object with the components data
		doWriteComponentsToBean(aEducationalLoan);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aEducationalLoan.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aEducationalLoan.getRecordType()).equals("")){
				aEducationalLoan.setVersion(aEducationalLoan.getVersion()+1);
				if(isNew){
					aEducationalLoan.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aEducationalLoan.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aEducationalLoan.setNewRecord(true);
				}
			}
		}else{
			aEducationalLoan.setVersion(aEducationalLoan.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aEducationalLoan,tranType)){
				refreshList();
				closeDialog(this.window_EducationalLoanDialog, "EducationalLoanDialog");
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 *  Set the workFlow Details List to Object
	 * @param aEducationalLoan
	 * @param tranType
	 * @return
	 */
	private boolean doProcess(EducationalLoan aEducationalLoan,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aEducationalLoan.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aEducationalLoan.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aEducationalLoan.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aEducationalLoan.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aEducationalLoan.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aEducationalLoan);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aEducationalLoan))) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
			}


			if (StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode= getWorkFlow().firstTask.owner;
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {

						if(nextRoleCode.length()>1){
							nextRoleCode =nextRoleCode+",";
						}
						nextRoleCode= getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode= getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aEducationalLoan.setTaskId(taskId);
			aEducationalLoan.setNextTaskId(nextTaskId);
			aEducationalLoan.setRoleCode(getRole());
			aEducationalLoan.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aEducationalLoan, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aEducationalLoan);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aEducationalLoan, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aEducationalLoan, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;

		EducationalLoan aEducationalLoan = (EducationalLoan) auditHeader.getAuditDetail().getModelData();
		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getEducationalLoanService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getEducationalLoanService().saveOrUpdate(auditHeader);	
					}
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getEducationalLoanService().doApprove(auditHeader);

						if(aEducationalLoan.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}
					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doReject)){
						auditHeader = getEducationalLoanService().doReject(auditHeader);
						if(aEducationalLoan.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999
								, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_EducationalLoanDialog,
								auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_EducationalLoanDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes(),true);
					}
				}

				if (retValue==PennantConstants.porcessOVERIDE){
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
			setOverideMap(auditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get Audit Header Details
	 * @param aEducationalLoan 
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(EducationalLoan aEducationalLoan, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aEducationalLoan.getBefImage(),
						aEducationalLoan);   
		return new AuditHeader(String.valueOf(aEducationalLoan.getLoanRefNumber()),null,
				null,null,auditDetail,aEducationalLoan.getUserDetails(),getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 *
	 * @param e (Exception)
	 */
	private void showMessage(Exception e){
		logger.debug("Entering");
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_EducationalLoanDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
		logger.debug("Leaving");
	}

	/**
	 *  Method for Entering Notes
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	//Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
		logger.debug("Leaving");
	}	

	/**
	 * 	Get the notes entered for rejected reason
	 * @return notes (Notes)
	 */
	private Notes getNotes(){
		logger.debug("Entering ");
		Notes notes = new Notes();
		notes.setModuleName("EducationalLoan");
		notes.setReference(String.valueOf(getEducationalLoan().getLoanRefNumber()));
		notes.setVersion(getEducationalLoan().getVersion());
		logger.debug("Leaving ");
		return notes;
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

	public EducationalLoan getEducationalLoan() {
		return this.educationalLoan;
	}
	public void setEducationalLoan(EducationalLoan educationalLoan) {
		this.educationalLoan = educationalLoan;
	}

	public void setEducationalLoanService(EducationalLoanService educationalLoanService) {
		this.educationalLoanService = educationalLoanService;
	}
	public EducationalLoanService getEducationalLoanService() {
		return this.educationalLoanService;
	}

	public void setEducationalLoanListCtrl(EducationalLoanListCtrl educationalLoanListCtrl) {
		this.educationalLoanListCtrl = educationalLoanListCtrl;
	}
	public EducationalLoanListCtrl getEducationalLoanListCtrl() {
		return this.educationalLoanListCtrl;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public void setEduExpenseDetailList(List<EducationalExpense> eduExpenseDetailList) {
		this.eduExpenseDetailList = eduExpenseDetailList;
	}
	public PagedListWrapper<EducationalExpense> getEduExpenseDetailPagedListWrapper() {
		return eduExpenseDetailPagedListWrapper;
	}

	@SuppressWarnings("unchecked")
	public void setEduExpenseDetailPagedListWrapper() {
		if(this.eduExpenseDetailPagedListWrapper == null){
			this.eduExpenseDetailPagedListWrapper = (PagedListWrapper<EducationalExpense>) SpringUtil.getBean(
					"pagedListWrapper");;
		}
	}
	public List<EducationalExpense> getEduExpenseDetailList() {
		return eduExpenseDetailList;
	}

	public boolean isNewFinance() {
		return newFinance;
	}

	public void setNewFinance(boolean newFinance) {
		this.newFinance = newFinance;
	}
	
}

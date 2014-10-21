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
 * FileName    		:  JountAccountDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-09-2013    														*
 *                                                                  						*
 * Modified Date    :  10-09-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-09-2013       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.jointaccountdetail;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.accounts.AccountsService;
import com.pennant.backend.service.finance.JointAccountDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.ScreenCTL;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/JountAccountDetail/jountAccountDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class JointAccountDetailDialogCtrl extends GFCBaseCtrl implements Serializable {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(JointAccountDetailDialogCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_JountAccountDetailDialog;
	protected Row row0;
	protected Label label_CustCIF;
	protected Hlayout hlayout_CustCIF;
	protected Space space_CustCIF;
	protected Textbox custCIF;
	protected Longbox custID;
	protected Label label_CustCIFName;
	protected Hlayout hlayout_CustCIFName;
	protected Space space_CustCIFName;
	protected Textbox custCIFName;
	protected Row row1;
	protected Label label_IncludeRepay;
	protected Hlayout hlayout_IncludeRepay;
	protected Space space_IncludeRepay;
	protected Checkbox includeRepay;
	protected Label label_RepayAccountId;
	protected Hlayout hlayout_RepayAccountId;
	protected Space space_RepayAccountId;

	protected Textbox repayAccountId;
	protected Button btnSearchrepayAccountId;
	protected Label repayAccountBlc;


	protected Row row2;
	protected Label label_CustCIFStatus;
	protected Hlayout hlayout_CustCIFStatus;
	protected Space space_CustCIFStatus;
	protected Textbox custCIFStatus;
	protected Label label_CustCIFWorstStatus;
	protected Hlayout hlayout_CustCIFWorstStatus;
	protected Space space_CustCIFWorstStatus;
	protected Textbox custCIFWorstStatus;
	protected Label recordStatus;
	protected Label recordType;
	protected Radiogroup userAction;
	protected Groupbox gb_statusDetails;
	protected Groupbox groupboxWf;
	protected South south;
	private boolean enqModule = false;
	private int index;
	// Exposure List fields
	protected Groupbox gb_JointAccountPrimaryJoint;
	protected Groupbox gb_JointAccountSecondaryJoint;
	protected Groupbox gb_JointAccountGuarantorJoint;
	protected Listbox listBox_JointAccountPrimary;
	protected Listbox listBox_JointAccountSecondary;
	protected Listbox listBox_JointAccountGuarantor;
	protected Groupbox gb_EmptyExposure;
	private FinanceExposure sumPrimaryDetails = null;
	private FinanceExposure sumSecondaryDetails = null;
	private FinanceExposure sumGurantorDetails = null;
	// not auto wired vars
	private com.pennant.webui.finance.financemain.JointAccountDetailDialogCtrl finJointAccountCtrl;
	private FinanceMain financeMain;
	private JointAccountDetail jountAccountDetail;
	private List<JointAccountDetail> jointAccountDetailList; // overhanded per
	// param
	private String moduleType = "";
	private transient JointAccountDetailListCtrl jountAccountDetailListCtrl; // overhanded
	// per
	// param
	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_CustCIF;
	private transient boolean oldVar_IncludeRepay;
	private transient String oldVar_RepayAccountId;
	private transient String oldVar_recordStatus;
	private boolean notes_Entered = false;
	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_JountAccountDetailDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew;
	protected Button btnEdit;
	protected Button btnDelete;
	protected Button btnSave;
	protected Button btnCancel;
	protected Button btnClose;
	protected Button btnHelp;
	protected Button btnNotes;
	protected Button btnSearchCustCIF;
	private transient String oldVar_CustCIFName;
	// ServiceDAOs / Domain Classes
	private transient JointAccountDetailService jointAccountDetailService;
	private transient PagedListService pagedListService;
	public int borderLayoutHeight = 0;
	private boolean newRecord = false;
	private boolean newContributor = false;
	private List<FinanceExposure> primaryList = null;
	private List<FinanceExposure> secoundaryList = null;
	private List<FinanceExposure> guarantorList = null;
	BigDecimal totfinAmt =BigDecimal.ZERO;
	BigDecimal totCurrentAmt =BigDecimal.ZERO;
	BigDecimal totDueAmt =BigDecimal.ZERO;
	long recordCount = 0;
	String primaryCustId;
	int ccyEditField = 0;
	String finCcy="";
	private String cif[]=null;

	/**
	 * default constructor.<br>
	 */
	public JointAccountDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected JountAccountDetail
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_JountAccountDetailDialog(Event event) throws Exception {
		logger.debug("Entring" + event.toString());
		try {
			
			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);
			// READ OVERHANDED params !
			if (args.containsKey("enqModule")) {
				enqModule = (Boolean) args.get("enqModule");
			} else {
				enqModule = false;
			}
			
			// READ OVERHANDED params !
			if (args.containsKey("jountAccountDetail")) {
				this.jountAccountDetail = (JointAccountDetail) args.get("jountAccountDetail");
				JointAccountDetail befImage = new JointAccountDetail();
				BeanUtils.copyProperties(this.jountAccountDetail, befImage);
				this.jountAccountDetail.setBefImage(befImage);
				setNewContributor(true);
				setJountAccountDetail(this.jountAccountDetail);
			} else {
				setJountAccountDetail(null);
			}
			
			if (args.containsKey("index")) {
				this.index = (Integer) args.get("index");
			}
			
			if (args.containsKey("ccy")) {
				this.finCcy = (String) args.get("ccy");
			}
			
			if (args.containsKey("ccDecimal")) {
				this.ccyEditField = (Integer) args.get("ccDecimal");
			}
			
			if (args.containsKey("filter")) {
				this.cif = (String[]) args.get("filter");
			}
			
			if (args.containsKey("finJointAccountCtrl")) {
				setFinanceMainDialogCtrl((com.pennant.webui.finance.financemain.JointAccountDetailDialogCtrl) args.get("finJointAccountCtrl"));
				setNewContributor(true);
				if (args.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.jountAccountDetail.setWorkflowId(0);
				if (args.containsKey("roleCode")) {
					setRole((String) args.get("roleCode"));
					getUserWorkspace().alocateRoleAuthorities(getRole(), "JountAccountDetailDialog");
				}
			}
			
			if (args.containsKey("financeMain")) {
				setFinanceMain((FinanceMain) args.get("financeMain"));
			}
			
			if (args.containsKey("primaryCustID")) {
				primaryCustId = (String) args.get("primaryCustID");
			}
			
			doLoadWorkFlow(this.jountAccountDetail.isWorkflow(), this.jountAccountDetail.getWorkflowId(), this.jountAccountDetail.getNextTaskId());
			if (isWorkFlowEnabled() && !enqModule && !isNewContributor()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "JountAccountDetailDialog");
			}
			
			/* set components visible dependent of the users rights */
			doCheckRights();
			
			// READ OVERHANDED params !
			// we get the jountAccountDetailListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete jountAccountDetail here.
			if (args.containsKey("jountAccountDetailListCtrl")) {
				setJountAccountDetailListCtrl((JointAccountDetailListCtrl) args.get("jountAccountDetailListCtrl"));
			} else {
				setJountAccountDetailListCtrl(null);
			}
			
			this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue() - PennantConstants.borderlayoutMainNorth;
			this.listBox_JointAccountPrimary.setHeight(((this.borderLayoutHeight - 120 - 220) / 1) + "px");
			this.listBox_JointAccountSecondary.setHeight(((this.borderLayoutHeight - 120 - 220) / 1) + "px");
			this.listBox_JointAccountGuarantor.setHeight(((this.borderLayoutHeight - 120 - 220) / 1) + "px");

			primaryList = getJointAccountDetailService().getPrimaryExposureList(getJountAccountDetail());
			secoundaryList = getJointAccountDetailService().getSecondaryExposureList(getJountAccountDetail());
			guarantorList = getJointAccountDetailService().getGuarantorExposureList(getJountAccountDetail());
			sumPrimaryDetails = getJointAccountDetailService().getExposureSummaryDetail(primaryList);
			sumSecondaryDetails = getJointAccountDetailService().getExposureSummaryDetail(secoundaryList);
			sumGurantorDetails = getJointAccountDetailService().getExposureSummaryDetail(guarantorList);
			
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getJountAccountDetail());
			
		} catch (Exception e) {
			createException(window_JountAccountDetailDialog, e);
			logger.error(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doStoreInitValues();
		// displayComponents(ScreenCTL.SCRN_GNEDT);
		doEdit();
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
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doResetInitValues();
		// displayComponents(ScreenCTL.SCRN_GNINT);
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
		PTMessageUtils.showHelpWindow(event, window_JountAccountDetailDialog);
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
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_JountAccountDetailDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
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
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_JountAccountDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
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
		logger.debug("Entering" + event.toString());
		try {
			ScreenCTL.displayNotes(getNotes("JountAccountDetail", String.valueOf(getJountAccountDetail().getJointAccountId()), getJountAccountDetail().getVersion()), this);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onChange$custCIF(Event event) {
		logger.debug("Entering" + event.toString());
		this.custCIF.clearErrorMessage();

		Customer customer = (Customer)PennantAppUtil.getCustomerObject(this.custCIF.getValue(), null);

		if(customer == null) {
			this.custID.setValue(Long.valueOf(0));
			this.custCIFName.setValue("");
			throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_JountAccountDetailDialog_CustCIF.value")}));
		} else {
			this.custCIF.setValue(customer.getCustCIF());
			this.custID.setValue(customer.getCustID());
			this.custCIFName.setValue(customer.getCustShrtName());
		}
		setCustomerDetails(customer);
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchCustCIF(Event event) {
		Customer customer = null;
		if (cif != null) {
			Filter filter[] = new Filter[1];
			filter[0] = new Filter("CustCIF", cif, Filter.OP_NOT_IN);
			Object dataObject = ExtendedSearchListBox.show(this.window_JountAccountDetailDialog, "Customer",filter);
			if (dataObject instanceof String) {
				this.custCIF.setValue(dataObject.toString());
				this.custCIFName.setValue("");
			} else {
				customer = (Customer) dataObject;
				if (customer != null) {
					this.custCIF.setValue(customer.getCustCIF());
					this.custID.setValue(customer.getCustID());
					this.custCIFName.setValue(customer.getCustShrtName());
				}
			}
		}
		setCustomerDetails(customer);
	}

	public void setCustomerDetails(Customer customer) {
		if(customer !=null) {
			BigDecimal currentExpoSure = BigDecimal.ZERO;
			getJountAccountDetail().setCustCIF(customer.getCustCIF());			

			this.primaryList = getJointAccountDetailService().getPrimaryExposureList(getJountAccountDetail());
			currentExpoSure = getJointAccountDetailService().doFillExposureDetails(this.primaryList, getJountAccountDetail());
			getJountAccountDetail().setPrimaryExposure(String.valueOf(currentExpoSure));

			this.secoundaryList = getJointAccountDetailService().getSecondaryExposureList(getJountAccountDetail());
			currentExpoSure =  getJointAccountDetailService().doFillExposureDetails(this.secoundaryList, getJountAccountDetail());
			getJountAccountDetail().setSecondaryExposure(String.valueOf(currentExpoSure));

			this.guarantorList = getJointAccountDetailService().getGuarantorExposureList(getJountAccountDetail());
			currentExpoSure =  getJointAccountDetailService().doFillExposureDetails(this.guarantorList, getJountAccountDetail());
			getJountAccountDetail().setGuarantorExposure(String.valueOf(currentExpoSure));

			this.sumPrimaryDetails = getJointAccountDetailService().getExposureSummaryDetail(primaryList);
			this.sumSecondaryDetails = getJointAccountDetailService().getExposureSummaryDetail(secoundaryList);
			this.sumGurantorDetails = getJointAccountDetailService().getExposureSummaryDetail(guarantorList);

			if(this.primaryList != null) {
				doFillPrimaryExposureDetails(this.primaryList);
			}

			if(this.secoundaryList != null) {
				doFillSecoundaryExposureDetails(this.secoundaryList);
			}

			if(this.guarantorList != null) {
				doFillGuarantorExposureDetails(this.guarantorList);
			}

			setVisibleGrid();
		}
	}

	public void onClick$includeRepay(Event event) {
		logger.debug("Entering" + event.toString());
		doCheckIncludeRepay();
		logger.debug("Leaving" + event.toString());
	}

	public void doCheckIncludeRepay() {
		if (includeRepay.isChecked()) {
			btnSearchrepayAccountId.setVisible(true);
			space_RepayAccountId.setVisible(true);
		} else {
			btnSearchrepayAccountId.setVisible(false);
			space_RepayAccountId.setVisible(false);
			this.repayAccountId.setValue("");
			this.repayAccountBlc.setValue("");
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aJountAccountDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(JointAccountDetail aJountAccountDetail) throws InterruptedException {
		logger.debug("Entering");
		// if aJountAccountDetail == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aJountAccountDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aJountAccountDetail = getJointAccountDetailService().getNewJountAccountDetail();
			setJountAccountDetail(aJountAccountDetail);
		} else {
			setJountAccountDetail(aJountAccountDetail);
		}
		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custCIF.focus();
		} else {
			if (isNewContributor()) {
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
			doFillPrimaryExposureDetails(this.primaryList);
			doFillSecoundaryExposureDetails(this.secoundaryList);
			doFillGuarantorExposureDetails(this.guarantorList);

			setVisibleGrid();		

			// fill the components with the data
			doWriteBeanToComponents(aJountAccountDetail);
			// set ReadOnly mode accordingly if the object is new or not.
			// displayComponents(ScreenCTL.getMode(enqModule,isWorkFlowEnabled(),aJountAccountDetail.isNewRecord()));
			doStoreInitValues();
			doCheckIncludeRepay();
			// stores the initial data for comparing if they are changed
			// during user action.
			// setDialog(this.window_JountAccountDetailDialog);
			this.window_JountAccountDetailDialog.setWidth("90%");
			this.window_JountAccountDetailDialog.setHeight("90%");
			if (isNewContributor()) {
				this.groupboxWf.setVisible(false);
				this.window_JountAccountDetailDialog.doModal();
			} else {
				setDialog(this.window_JountAccountDetailDialog);
			}

		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	private void setVisibleGrid() {
		if (this.primaryList==null || this.primaryList.size()==0) {
			this.gb_JointAccountPrimaryJoint.setVisible(false);
		} else {
			this.gb_JointAccountPrimaryJoint.setVisible(true);
		}

		if (this.secoundaryList==null || this.secoundaryList.size()==0) {
			this.gb_JointAccountSecondaryJoint.setVisible(false);
		} else {
			this.gb_JointAccountSecondaryJoint.setVisible(true);
		}

		if (this.guarantorList==null || this.guarantorList.size()==0) {
			this.gb_JointAccountGuarantorJoint.setVisible(false);
		} else {
			this.gb_JointAccountGuarantorJoint.setVisible(true);
		}

		if ((this.primaryList == null || this.primaryList.size()== 0)
				&& (this.secoundaryList == null || this.secoundaryList.size() == 0)
				&& (this.guarantorList == null || this.guarantorList.size() == 0)) {

			if (getJountAccountDetail().getCustCIF() != null) {
				gb_EmptyExposure.setVisible(true);
			} else {
				gb_EmptyExposure.setVisible(false);
			}
		}
	}

	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit
	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (isNewRecord()) {
			// this.btnSearchCustCIF.setDisabled(false);
			if (isNewContributor()) {
				this.btnCancel.setVisible(false);
			}
		} else {
			this.custCIF.setDisabled(true);
			this.btnCancel.setVisible(true);
		}
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.jountAccountDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (newContributor) {
				if ("ENQ".equals(this.moduleType)) {
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
				} else if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(newContributor);
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	public void doReadOnly() {
		logger.debug("Entering");
		this.custCIF.setReadonly(true);
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

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);
		if (!enqModule) {
			getUserWorkspace().alocateAuthorities("JountAccountDetailDialog", getRole());
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_JountAccountDetailDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_JountAccountDetailDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_JountAccountDetailDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_JountAccountDetailDialog_btnSave"));
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.custCIF.setMaxlength(12);
		this.custCIFName.setMaxlength(20);
		this.repayAccountId.setMaxlength(20);
		this.custCIFName.setReadonly(true);
		if (!isNewRecord()) {
			this.row1.setVisible(false);
			this.row2.setVisible(true);
			this.btnSearchCustCIF.setVisible(false);
			this.custCIF.setReadonly(false);
			this.gb_JointAccountPrimaryJoint.setVisible(true);
			this.gb_JointAccountSecondaryJoint.setVisible(true);
			this.gb_JointAccountGuarantorJoint.setVisible(true);
		}
		setStatusDetails(gb_statusDetails, groupboxWf, south, enqModule);
		logger.debug("Leaving");
	}

	/**
	 * Stores the initialinitial values to member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		// this.oldVar_FinReference = this.finReference.getValue();
		this.oldVar_CustCIF = this.custCIF.getValue();
		this.oldVar_CustCIFName = this.custCIFName.getValue();
		this.oldVar_IncludeRepay = this.includeRepay.isChecked();
		this.oldVar_RepayAccountId = this.repayAccountId.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		// this.finReference.setValue(this.oldVar_FinReference);
		this.custCIF.setValue(this.oldVar_CustCIF);
		this.custCIFName.setValue(this.oldVar_CustCIFName);
		this.includeRepay.setChecked(this.oldVar_IncludeRepay);
		this.repayAccountId.setValue(this.oldVar_RepayAccountId);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		if (isWorkFlowEnabled() & !enqModule) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for closing Customer Selection Window
	 * 
	 * @throws InterruptedException
	 */
	public void closeWindow() throws InterruptedException {
		logger.debug("Entering");
		if (isNewContributor()) {
			closePopUpWindow(this.window_JountAccountDetailDialog, "JountAccountDetailDialog");
		} else {
			closeDialog(this.window_JountAccountDetailDialog, "JountAccountDetailDialog");
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aJountAccountDetail
	 *            JountAccountDetail
	 */
	public void doWriteBeanToComponents(JointAccountDetail aJountAccountDetail) {
		logger.debug("Entering");
		// this.finReference.setValue(aJountAccountDetail.getFinReference());
		this.custCIF.setValue(aJountAccountDetail.getCustCIF());
		this.includeRepay.setChecked(aJountAccountDetail.isIncludeRepay());
		this.repayAccountId.setValue(PennantApplicationUtil.formatAccountNumber(aJountAccountDetail.getRepayAccountId()));
		this.custCIFName.setValue(aJountAccountDetail.getLovDescCIFName());
		this.custCIFStatus.setValue(aJountAccountDetail.getStatus());
		this.custCIFWorstStatus.setValue(aJountAccountDetail.getWorstStatus());
		this.recordStatus.setValue(aJountAccountDetail.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aJountAccountDetail.getRecordType()));
		logger.debug("Leaving");
	}

	// ================Primary Exposure Details
	public void doFillPrimaryExposureDetails(List<FinanceExposure> primaryExposureList) {
		logger.debug("Entering");
		if (primaryExposureList != null) {
			this.listBox_JointAccountPrimary.getItems().clear();
			recordCount = primaryExposureList.size();
			for (FinanceExposure primaryExposure : primaryExposureList) {
				Listitem listitem = new Listitem();
				Listcell listcell;
				listcell = new Listcell(primaryExposure.getFinType());
				listitem.appendChild(listcell);
				listcell = new Listcell(primaryExposure.getFinReference());
				listitem.appendChild(listcell);
				listcell = new Listcell(DateUtility.formatUtilDate(primaryExposure.getFinStartDate(), PennantConstants.dateFormate));
				listitem.appendChild(listcell);
				listcell = new Listcell(DateUtility.formatUtilDate(primaryExposure.getMaturityDate(), PennantConstants.dateFormate));
				listitem.appendChild(listcell);
				listcell = new Listcell(primaryExposure.getFinCCY());
				listitem.appendChild(listcell);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(primaryExposure.getFinanceAmt(), primaryExposure.getCcyEditField()));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(primaryExposure.getCurrentExpoSureinBaseCCY(), 3));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);
				listcell = new Listcell();

				if (primaryExposure.isOverdue()) {
					listcell.setLabel(PennantConstants.YES);
				} else {
					listcell.setLabel(PennantConstants.NO);
				}

				listitem.appendChild(listcell);
				listcell = new Listcell(primaryExposure.getPastdueDays());
				listitem.appendChild(listcell);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(primaryExposure.getOverdueAmtBaseCCY(), 3));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);
				listitem.setAttribute("data", primaryExposure);
				this.listBox_JointAccountPrimary.appendChild(listitem);
			}
			String footerStyle1 = "text-align:right; font-weight:bold;";

			Listitem item = new Listitem();
			Listcell lc = null;

			lc = new Listcell("TOTAL");
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle("text-align:left; font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(recordCount + "");
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);
			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);
			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(this.sumPrimaryDetails.getCurrentExpoSureinBaseCCY(), this.sumPrimaryDetails.getCcyEditField()));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);
			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");

			lc = new Listcell(PennantApplicationUtil.amountFormate(this.sumPrimaryDetails.getOverdueAmtBaseCCY(), this.sumPrimaryDetails.getCcyEditField()));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			item.setParent(this.listBox_JointAccountPrimary);
		}
		logger.debug("Leaving");
	}

	public void doFillSecoundaryExposureDetails(List<FinanceExposure> secondaryExposureList) {
		logger.debug("Entering");
		if (secondaryExposureList != null) {
			this.listBox_JointAccountSecondary.getItems().clear();
			recordCount = secondaryExposureList.size();
			for (FinanceExposure secondaryExposure : secondaryExposureList) {
				Listitem listitem = new Listitem();
				Listcell listcell;
				
				listcell = new Listcell(secondaryExposure.getFinType());
				listitem.appendChild(listcell);
				
				listcell = new Listcell(secondaryExposure.getFinReference());
				listitem.appendChild(listcell);
				
				listcell = new Listcell(DateUtility.formatUtilDate(secondaryExposure.getFinStartDate(), PennantConstants.dateFormate));
				listitem.appendChild(listcell);
				
				listcell = new Listcell(DateUtility.formatUtilDate(secondaryExposure.getMaturityDate(), PennantConstants.dateFormate));
				listitem.appendChild(listcell);
				
				listcell = new Listcell(secondaryExposure.getFinCCY());
				listitem.appendChild(listcell);
				
				listcell = new Listcell(PennantApplicationUtil.amountFormate(secondaryExposure.getFinanceAmt(), secondaryExposure.getCcyEditField()));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);
				
				listcell = new Listcell(PennantApplicationUtil.amountFormate(secondaryExposure.getCurrentExpoSureinBaseCCY(), 3));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);
				
				listcell = new Listcell();
				if (secondaryExposure.isOverdue()) {
					listcell.setLabel(PennantConstants.YES);
				} else {
					listcell.setLabel(PennantConstants.NO);
				}
				listitem.appendChild(listcell);
				
				listcell = new Listcell(secondaryExposure.getPastdueDays());
				listitem.appendChild(listcell);
				
				listcell = new Listcell(PennantApplicationUtil.amountFormate(secondaryExposure.getOverdueAmtBaseCCY(), 3));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);
				
				listcell = new Listcell(secondaryExposure.getCustCif());
				listitem.appendChild(listcell);
				
				listitem.setAttribute("data", secondaryExposure);
				this.listBox_JointAccountSecondary.appendChild(listitem);
			}
			String footerStyle1 = "text-align:right; font-weight:bold;";

			Listitem item = new Listitem();
			Listcell lc = null;

			lc = new Listcell("TOTAL");
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle("text-align:left; font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(recordCount + "");
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);
			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);
			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(this.sumSecondaryDetails.getCurrentExpoSureinBaseCCY(), this.sumPrimaryDetails.getCcyEditField()));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);
			

			lc = new Listcell(PennantApplicationUtil.amountFormate(this.sumSecondaryDetails.getOverdueAmtBaseCCY(), this.sumSecondaryDetails.getCcyEditField()));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);
			
			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);
			
			this.listBox_JointAccountSecondary.appendChild(item);
		}
		logger.debug("Leaving");
	}

	public void doFillGuarantorExposureDetails(List<FinanceExposure> guarantorExposureList) {
		logger.debug("Entering");
		if (guarantorExposureList != null) {
			this.listBox_JointAccountGuarantor.getItems().clear();
			recordCount = guarantorExposureList.size();
			for (FinanceExposure guarantorExposure : guarantorExposureList) {
				Listitem listitem = new Listitem();
				Listcell listcell;
				
				listcell = new Listcell(guarantorExposure.getFinType());
				listitem.appendChild(listcell);
				
				listcell = new Listcell(guarantorExposure.getFinReference());
				listitem.appendChild(listcell);
				
				listcell = new Listcell(DateUtility.formatUtilDate(guarantorExposure.getFinStartDate(), PennantConstants.dateFormate));
				listitem.appendChild(listcell);
				
				listcell = new Listcell(DateUtility.formatUtilDate(guarantorExposure.getMaturityDate(), PennantConstants.dateFormate));
				listitem.appendChild(listcell);
				
				listcell = new Listcell(guarantorExposure.getFinCCY());
				listitem.appendChild(listcell);
				
				listcell = new Listcell(PennantApplicationUtil.amountFormate(guarantorExposure.getFinanceAmt(), guarantorExposure.getCcyEditField()));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);
				
				listcell = new Listcell(PennantApplicationUtil.amountFormate(guarantorExposure.getCurrentExpoSureinBaseCCY(), 3));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);
				
				listcell = new Listcell();
				if (guarantorExposure.isOverdue()) {
					listcell.setLabel(PennantConstants.YES);
				} else {
					listcell.setLabel(PennantConstants.NO);
				}
				listitem.appendChild(listcell);
				
				listcell = new Listcell(guarantorExposure.getPastdueDays());
				listitem.appendChild(listcell);
				
				listcell = new Listcell(PennantApplicationUtil.amountFormate(guarantorExposure.getOverdueAmtBaseCCY(), 3));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);
				
				listcell = new Listcell(guarantorExposure.getCustCif());
				listitem.appendChild(listcell);
				
				listitem.setAttribute("data", guarantorExposure);
				this.listBox_JointAccountGuarantor.appendChild(listitem);
			}
			String footerStyle1 = "text-align:right; font-weight:bold;";

			Listitem item = new Listitem();
			Listcell lc = null;

			lc = new Listcell("TOTAL");
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle("text-align:left; font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(recordCount + "");
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);
			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);
			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(this.sumGurantorDetails.getCurrentExpoSureinBaseCCY(), this.sumGurantorDetails.getCcyEditField()));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);
			
			lc = new Listcell(PennantApplicationUtil.amountFormate(this.sumGurantorDetails.getOverdueAmtBaseCCY(), this.sumGurantorDetails.getCcyEditField()));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);
			
			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);
			
			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);
			
			
			this.listBox_JointAccountGuarantor.appendChild(item);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aJountAccountDetail
	 */
	public void doWriteComponentsToBean(JointAccountDetail aJountAccountDetail) {
		logger.debug("Entering");
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		// Cust C I F
		try {
			aJountAccountDetail.setCustCIF(this.custCIF.getValue());
			if((!this.custCIF.isDisabled()) && this.custID.getValue() == 0) {
				wve.add(new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_JountAccountDetailDialog_CustCIF.value")})));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// cust CIF Name
		try {
			aJountAccountDetail.setLovDescCIFName(this.custCIFName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Include Repay
		try {
			aJountAccountDetail.setIncludeRepay(this.includeRepay.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Repay Account Id
		try {
			// Repay Account Id
			if (!this.btnSearchrepayAccountId.isDisabled() && this.btnSearchrepayAccountId.isVisible()) {
				if (StringUtils.trimToEmpty(this.repayAccountId.getValue()).equals("")) {
					throw new WrongValueException(this.repayAccountId,Labels.getLabel("FIELD_NO_EMPTY", new String[]{Labels.getLabel("label_JountAccountDetailDialog_RepayAccountId.value")}));
				}
			}

			aJountAccountDetail.setRepayAccountId(PennantApplicationUtil.unFormatAccountNumber(this.repayAccountId.getValue()));
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
		aJountAccountDetail.setRecordStatus(this.recordStatus.getValue());
		setJountAccountDetail(aJountAccountDetail);
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		logger.debug("Entering");
		// To clear the Error Messages
		doClearMessage();
		if (!StringUtils.trimToEmpty(this.oldVar_CustCIF).equals(StringUtils.trimToEmpty(this.custCIF.getValue()))) {
			return true;
		}
		if (!StringUtils.trimToEmpty(this.oldVar_CustCIFName).equals(StringUtils.trimToEmpty(this.custCIFName.getValue()))) {
			return true;
		}
		if (this.oldVar_IncludeRepay != this.includeRepay.isChecked()) {
			return true;
		}
		if (!StringUtils.trimToEmpty(this.oldVar_RepayAccountId).equals(StringUtils.trimToEmpty(this.repayAccountId.getValue()))) {
			return true;
		}
		logger.debug("Leaving");
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		//		// Repay Account Id
		//		if (!this.btnSearchrepayAccountId.isDisabled() && this.btnSearchrepayAccountId.isVisible()) {
		//			this.repayAccountId.setConstraint(new PTStringValidator(Labels.getLabel("label_JountAccountDetailDialog_RepayAccountId.value"), PennantRegularExpressions.REGEX_ALPHANUM, true));
		//		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.custCIF.setConstraint("");
		this.repayAccountId.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		// Cust C I F
		if (!btnSearchCustCIF.isDisabled()) {
			this.custCIF.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_JountAccountDetailDialog_CustCIF.value") }));
		}
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveLOVValidation() {
		this.custCIFName.setConstraint("");
		this.custCIF.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.custCIFName.setErrorMessage("");
		this.repayAccountId.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */
	/*
	 * private void refreshList(){ final JdbcSearchObject<JountAccountDetail>
	 * soJountAccountDetail = getJountAccountDetailListCtrl().getSearchObj();
	 * getJountAccountDetailListCtrl
	 * ().pagingJountAccountDetailList.setActivePage(0);
	 * getJountAccountDetailListCtrl
	 * ().getPagedListWrapper().setSearchObject(soJountAccountDetail);
	 * if(getJountAccountDetailListCtrl().listBoxJountAccountDetail!=null){
	 * getJountAccountDetailListCtrl().listBoxJountAccountDetail.getListModel();
	 * } }
	 */
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
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
		logger.debug("Entering");
		boolean close = true;
		if (!enqModule && isDataChanged()) {
			logger.debug("isDataChanged : true");
			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");
			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);
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
	 * Deletes a JountAccountDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final JointAccountDetail aJountAccountDetail = new JointAccountDetail();
		BeanUtils.copyProperties(getJountAccountDetail(), aJountAccountDetail);
		String tranType = PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aJountAccountDetail.getCustCIF();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));
		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");
			if (StringUtils.trimToEmpty(aJountAccountDetail.getRecordType()).equals("")) {
				aJountAccountDetail.setVersion(aJountAccountDetail.getVersion() + 1);
				aJountAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				aJountAccountDetail.setNewRecord(true);
				if (isWorkFlowEnabled()) {
					aJountAccountDetail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (isNewContributor()) {
					tranType = PennantConstants.TRAN_DEL;
					AuditHeader auditHeader = newJountAccountProcess(aJountAccountDetail, tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_JountAccountDetailDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
						getFinanceMainDialogCtrl().doFillJointDetails(this.jointAccountDetailList);
						// send the data back to customer
						closeWindow();
					}
				}
			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.custCIF.setValue("");
		this.custCIFName.setValue("");
		this.includeRepay.setChecked(false);
		this.repayAccountId.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final JointAccountDetail aJountAccountDetail = new JointAccountDetail();
		BeanUtils.copyProperties(getJountAccountDetail(), aJountAccountDetail);
		boolean isNew = false;
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the DocumentDetails object with the components data
		doWriteComponentsToBean(aJountAccountDetail);
		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here
		isNew = aJountAccountDetail.isNew();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aJountAccountDetail.getRecordType()).equals("")) {
				aJountAccountDetail.setVersion(aJountAccountDetail.getVersion() + 1);
				if (isNew) {
					aJountAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aJountAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aJountAccountDetail.setNewRecord(true);
				}
			}
		} else {
			if (isNewContributor()) {
				if (isNewRecord()) {
					aJountAccountDetail.setVersion(1);
					aJountAccountDetail.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
				if (StringUtils.trimToEmpty(aJountAccountDetail.getRecordType()).equals("")) {
					aJountAccountDetail.setVersion(aJountAccountDetail.getVersion() + 1);
					aJountAccountDetail.setRecordType(PennantConstants.RCD_UPD);
				}
				if (aJountAccountDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aJountAccountDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}
			} else {
				aJountAccountDetail.setVersion(aJountAccountDetail.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}
		// save it to database
		try {
			if (isNewContributor()) {
				AuditHeader auditHeader = newJountAccountProcess(aJountAccountDetail, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_JountAccountDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getFinanceMainDialogCtrl().doFillJointDetails(this.jointAccountDetailList);
					// true;
					// send the data back to customer
					closeWindow();
				}
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private AuditHeader newJountAccountProcess(JointAccountDetail aJountAccountDetail, String tranType) {
		boolean recordAdded = false;
		AuditHeader auditHeader = getAuditHeader(aJountAccountDetail, tranType);
		jointAccountDetailList = new ArrayList<JointAccountDetail>();
		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		valueParm[0] = aJountAccountDetail.getCustCIF();
		valueParm[1] = aJountAccountDetail.getLovDescCIFName();
		errParm[0] = PennantJavaUtil.getLabel("label_CustCIF") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_JointCustCIf") + ":" + valueParm[1];
		// Checks whether jointAccount custCIF is same as actual custCIF
		if (getFinanceMain() != null) {
			if (StringUtils.trimToEmpty(primaryCustId).equals(aJountAccountDetail.getCustCIF())) {
				auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), getUserWorkspace().getUserLanguage()));
			}
		}
		if (getFinanceMainDialogCtrl().getJountAccountDetailList() != null && getFinanceMainDialogCtrl().getJountAccountDetailList().size() > 0) {
			for (int i = 0; i < getFinanceMainDialogCtrl().getJountAccountDetailList().size(); i++) {
				JointAccountDetail jountAccountDetail = getFinanceMainDialogCtrl().getJountAccountDetailList().get(i);
				if (jountAccountDetail.getCustCIF().equals(aJountAccountDetail.getCustCIF())) { // Both
					// Current
					// and
					// Existing
					// list
					// rating
					// same
					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					} else if (index != i) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), getUserWorkspace().getUserLanguage()));
					}
					if (tranType == PennantConstants.TRAN_DEL) {
						if (aJountAccountDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aJountAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							jointAccountDetailList.add(aJountAccountDetail);
						} else if (aJountAccountDetail.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aJountAccountDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aJountAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							jointAccountDetailList.add(aJountAccountDetail);
						} else if (aJountAccountDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							/*
							 * for (int j = 0; j <
							 * getFinanceMainDialogCtrl().getFinanceDetail
							 * ().getFinContributorHeader
							 * ().getContributorDetailList().size(); j++) {
							 * DocumentDetails detail =
							 * getFinanceMainDialogCtrl(
							 * ).getFinanceDetail().getFinContributorHeader
							 * ().getContributorDetailList().get(j);
							 * if(detail.getCustID() ==
							 * aDocumentDetails.getCustID()){
							 * contributorDetails.add(deta il); } }
							 */
						}
					} else {
						jointAccountDetailList.add(jountAccountDetail);
					}
				} else {
					jointAccountDetailList.add(jountAccountDetail);
				}
			}
		}
		if (tranType == PennantConstants.TRAN_UPD) {
			this.jointAccountDetailList.remove(index);
			this.jointAccountDetailList.add(jountAccountDetail);
			recordAdded = true;
		}
		if (!recordAdded) {
			jointAccountDetailList.add(aJountAccountDetail);
		}
		return auditHeader;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ WorkFlow Components+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(JointAccountDetail aJountAccountDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aJountAccountDetail.getBefImage(), aJountAccountDetail);
		return new AuditHeader(String.valueOf(aJountAccountDetail.getJointAccountId()), null, null, null, auditDetail, aJountAccountDetail.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public JointAccountDetail getJountAccountDetail() {
		return this.jountAccountDetail;
	}

	public void setJountAccountDetail(JointAccountDetail jountAccountDetail) {
		this.jountAccountDetail = jountAccountDetail;
	}


	public JointAccountDetailService getJointAccountDetailService() {
		return jointAccountDetailService;
	}

	public void setJointAccountDetailService(
			JointAccountDetailService jointAccountDetailService) {
		this.jointAccountDetailService = jointAccountDetailService;
	}

	public void setJountAccountDetailListCtrl(JointAccountDetailListCtrl jountAccountDetailListCtrl) {
		this.jountAccountDetailListCtrl = jountAccountDetailListCtrl;
	}

	public JointAccountDetailListCtrl getJountAccountDetailListCtrl() {
		return this.jountAccountDetailListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

	public List<JointAccountDetail> getJountAccountDetailList() {
		return jointAccountDetailList;
	}

	public void setJountAccountDetailList(List<JointAccountDetail> jountAccountDetailList) {
		this.jointAccountDetailList = jountAccountDetailList;
	}

	public boolean isNewContributor() {
		return newContributor;
	}

	public void setNewContributor(boolean newContributor) {
		this.newContributor = newContributor;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public com.pennant.webui.finance.financemain.JointAccountDetailDialogCtrl getFinanceMainDialogCtrl() {
		return finJointAccountCtrl;
	}

	public void setFinanceMainDialogCtrl(com.pennant.webui.finance.financemain.JointAccountDetailDialogCtrl finJointAccountCtrl) {
		this.finJointAccountCtrl = finJointAccountCtrl;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	private AccountsService accountsService;
	private AccountInterfaceService accountInterfaceService;
	public AccountsService getAccountsService() {
		return accountsService;
	}
	public void setAccountsService(AccountsService accountsService) {
		this.accountsService = accountsService;
	}
	public AccountInterfaceService getAccountInterfaceService() {
		return accountInterfaceService;
	}
	public void setAccountInterfaceService(AccountInterfaceService accountInterfaceService) {
		this.accountInterfaceService = accountInterfaceService;
	}
	/**
	 * when clicks on button "btnSearchRepayAcctId"
	 * 
	 * @param event
	 * @throws InterruptedException 
	 * @throws AccountNotFoundException
	 */
	public void onClick$btnSearchrepayAccountId(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		this.custCIF.clearErrorMessage();
		this.repayAccountId.clearErrorMessage();

		if(!StringUtils.trimToEmpty(this.custCIF.getValue()).equals("")) {
			Object dataObject;
 
			List<IAccounts> iAccountList = new ArrayList<IAccounts>();
			IAccounts iAccount = new IAccounts();
			iAccount.setAcCcy(finCcy);
			iAccount.setAcType("");
			iAccount.setDivision(getFinanceMainDialogCtrl().getFinanceDetail().getFinScheduleData().getFinanceType().getFinDivision());
			

			iAccount.setAcCustCIF(this.custCIF.getValue());
			try {
				iAccountList = getAccountInterfaceService().fetchExistAccountList(iAccount);
				dataObject = ExtendedSearchListBox.show(this.window_JountAccountDetailDialog, "Accounts", iAccountList);
				if (dataObject instanceof String) {
					this.repayAccountId.setValue(dataObject.toString());
				} else {
					IAccounts details = (IAccounts) dataObject;
					if (details != null) {
						this.repayAccountId.setValue(PennantApplicationUtil.formatAccountNumber(details.getAccountId()));
						this.repayAccountBlc.setValue(getAcBalance(details.getAccountId()));
					}
				}
			} catch (Exception e) {
				logger.error(e);
				Messagebox.show("Account Details not Found!!!", Labels.getLabel("message.Error") , 
						Messagebox.ABORT, Messagebox.ERROR);
			}
		}else {
			throw new WrongValueException(this.custCIF,Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_JountAccountDetailDialog_CustCIFName.value") }));
		}
		logger.debug("Leaving " + event.toString());
	}
	private String getAcBalance(String acId){
		if (!StringUtils.trimToEmpty(acId).equals("")) {
			return PennantAppUtil.amountFormate(getAccountInterfaceService().getAccountAvailableBal(acId),ccyEditField);
		}else{
			return "";
		}
	}
}

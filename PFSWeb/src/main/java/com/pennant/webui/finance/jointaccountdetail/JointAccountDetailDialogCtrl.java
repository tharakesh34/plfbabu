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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.accounts.AccountsService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.JointAccountDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.ScreenCTL;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Finance/JountAccountDetail/jountAccountDetailDialog.zul file.
 */
public class JointAccountDetailDialogCtrl extends GFCBaseCtrl<JointAccountDetail> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(JointAccountDetailDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_JountAccountDetailDialog;
	protected Row row0;
	protected Label label_CustCIF;
	protected Hbox hbox_CustCIF;
	protected Space space_CustCIF;
	protected Textbox custCIF;
	protected Longbox custID;
	protected Button viewCustInfo;
	protected Label label_CustCIFName;
	protected Hbox hbox_CustCIFName;
	protected Space space_CustCIFName;
	protected Textbox custCIFName;
	protected Row row1;
	protected Label label_IncludeRepay;
	protected Hbox hbox_IncludeRepay;
	protected Space space_IncludeRepay;
	protected Checkbox includeRepay;
	protected Label label_RepayAccountId;
	protected AccountSelectionBox repayAccountId;
	

	protected Row row2;
	protected Label label_CustCIFStatus;
	protected Hbox hbox_CustCIFStatus;
	protected Space space_CustCIFStatus;
	protected Textbox custCIFStatus;
	protected Label label_CustCIFWorstStatus;
	protected Hbox hbox_CustCIFWorstStatus;
	protected Space space_CustCIFWorstStatus;
	protected Textbox custCIFWorstStatus;

	protected Row row3;
	protected Label label_CatOfCoApplicant;
	protected Hbox hbox_CatOfCoApplicant;
	protected Space space_CatOfCoApplicant;
	protected Combobox catOfCoApplicant;
	
	protected Checkbox															authoritySignatory;
	protected Intbox															sequence;
	protected Hbox																hbox_Sequence;
	protected Label																label_Sequence;

	protected Label recordType;
	protected Groupbox gb_statusDetails;
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
	private transient JointAccountDetailListCtrl jountAccountDetailListCtrl; // overhanded per param
	protected Button btnSearchCustCIF;

	
	// ServiceDAOs / Domain Classes
	private transient JointAccountDetailService jointAccountDetailService;
	private transient PagedListService pagedListService;
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
	Customer customer = null;
	private int baseCcyDecFormat = SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT);
	List<ValueLabel> coapplicantList = PennantAppUtil.getcoApplicants();
	@Autowired
	CustomerDetailsService customerDetailsService;
	
	/**
	 * default constructor.<br>
	 */
	public JointAccountDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "JountAccountDetailDialog";
	}

	// Component Events
	
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected JountAccountDetail
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_JountAccountDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_JountAccountDetailDialog);

		try {
			
			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}
			if (arguments.containsKey("moduleType")) {
				moduleType = (String) arguments.get("moduleType");
			} 
			// READ OVERHANDED params !
			if (arguments.containsKey("jountAccountDetail")) {
				this.jountAccountDetail = (JointAccountDetail) arguments.get("jountAccountDetail");
				JointAccountDetail befImage = new JointAccountDetail();
				BeanUtils.copyProperties(this.jountAccountDetail, befImage);
				this.jountAccountDetail.setBefImage(befImage);
				setNewContributor(true);
				setJountAccountDetail(this.jountAccountDetail);
			} else {
				setJountAccountDetail(null);
			}
			
			if (arguments.containsKey("index")) {
				this.index = (Integer) arguments.get("index");
			}
			
			if (arguments.containsKey("ccy")) {
				this.finCcy = (String) arguments.get("ccy");
			}
			
			if (arguments.containsKey("ccDecimal")) {
				this.ccyEditField = (Integer) arguments.get("ccDecimal");
			}
			
			if (arguments.containsKey("filter")) {
				this.cif = (String[]) arguments.get("filter");
			}
			
			if (arguments.containsKey("finJointAccountCtrl")) {
				setFinanceMainDialogCtrl((com.pennant.webui.finance.financemain.JointAccountDetailDialogCtrl) arguments.get("finJointAccountCtrl"));
				setNewContributor(true);
				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.jountAccountDetail.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					setRole((String) arguments.get("roleCode"));
					getUserWorkspace().allocateRoleAuthorities(getRole(), "JountAccountDetailDialog");
				}
			}
			
			if (arguments.containsKey("financeMain")) {
				setFinanceMain((FinanceMain) arguments.get("financeMain"));
			}
			
			if (arguments.containsKey("primaryCustID")) {
				primaryCustId = (String) arguments.get("primaryCustID");
			}
			
			doLoadWorkFlow(this.jountAccountDetail.isWorkflow(), this.jountAccountDetail.getWorkflowId(), this.jountAccountDetail.getNextTaskId());
			if (isWorkFlowEnabled() && !enqModule && !isNewContributor()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "JountAccountDetailDialog");
			}
			
			/* set components visible dependent of the users rights */
			doCheckRights();
			
			// READ OVERHANDED params !
			// we get the jountAccountDetailListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete jountAccountDetail here.
			if (arguments.containsKey("jountAccountDetailListCtrl")) {
				setJountAccountDetailListCtrl((JointAccountDetailListCtrl) arguments.get("jountAccountDetailListCtrl"));
			} else {
				setJountAccountDetailListCtrl(null);
			}
			
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
			MessageUtil.showError(e);
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
		doWriteBeanToComponents(this.jountAccountDetail.getBefImage());
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
		MessageUtil.showHelpWindow(event, window_JountAccountDetailDialog);
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
		logger.debug("Entering" + event.toString());
		try {
			ScreenCTL.displayNotes(getNotes("JountAccountDetail", String.valueOf(getJountAccountDetail().getJointAccountId()), getJountAccountDetail().getVersion()), this);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onChange$custCIF(Event event) {
		logger.debug("Entering" + event.toString());
		this.custCIF.clearErrorMessage();
		List<Filter>  list = null;
		if (cif != null) {
			list = new ArrayList<>();
			list.add(new Filter("CustCIF", cif, Filter.OP_NOT_IN));
		}

		customer = (Customer)PennantAppUtil.getCustomerObject(this.custCIF.getValue(), list);

		if(customer == null) {
			this.custID.setValue(Long.valueOf(0));
			this.custCIFName.setValue("");
			this.primaryList = null;
			this.secoundaryList = null;
			this.guarantorList = null;
			
			this.listBox_JointAccountPrimary.getItems().clear();
			this.listBox_JointAccountSecondary.getItems().clear();
			this.listBox_JointAccountGuarantor.getItems().clear();
			
			setVisibleGrid();
			throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_JountAccountDetailDialog_CustCIF.value")}));
		} else {
			this.custCIF.setValue(customer.getCustCIF());
			this.custID.setValue(customer.getCustID());
			this.custCIFName.setValue(customer.getCustShrtName());
			if(!(StringUtils.isEmpty(customer.getCustCoreBank())) && (customer.getCustCoreBank()!= null)){
				this.row1.setVisible(false);
			}else{
				this.row1.setVisible(false);
				this.includeRepay.setChecked(false);
				this.repayAccountId.setValue("");
			}
		}
		setCustomerDetails(customer);
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchCustCIF(Event event) {
		customer = null;
		this.row1.setVisible(false);
		Object dataObject=null;
		
		if (cif != null) {
			Filter filter[] = new Filter[1];
			if(cif[0] != null){
			filter[0] = new Filter("CustCIF", cif, Filter.OP_NOT_IN);
			//filter[1] = new Filter("CustCoreBank", "", Filter.OP_NOT_EQUAL);
			 dataObject = ExtendedSearchListBox.show(this.window_JountAccountDetailDialog, "CustomerData",filter);
			}else{
			 dataObject = ExtendedSearchListBox.show(this.window_JountAccountDetailDialog, "CustomerData");	
			}
			if (dataObject instanceof String) {
				this.custCIF.setValue(dataObject.toString());
				this.custCIFName.setValue("");
				this.gb_JointAccountPrimaryJoint.setVisible(false);
				this.gb_JointAccountSecondaryJoint.setVisible(false);
				this.gb_JointAccountGuarantorJoint.setVisible(false);
			} else {
				customer = (Customer) dataObject;
				if (customer != null) {
					this.custCIF.setValue(customer.getCustCIF());
					this.custID.setValue(customer.getCustID());
					this.custCIFName.setValue(customer.getCustShrtName());
					if(customer.getCustCoreBank()!= null && StringUtils.isNotEmpty(customer.getCustCoreBank())){
						this.row1.setVisible(false);
					}else{
						this.row1.setVisible(false);
						this.includeRepay.setChecked(false);
						this.repayAccountId.setValue("");
					}
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
		}else{
			this.primaryList = null;
			this.secoundaryList = null;
			this.guarantorList = null;
		}
	}
	
	public void onClick$viewCustInfo(Event event){
		if((!this.custCIF.isDisabled()) && (this.custID.getValue() == null || this.custID.getValue() == 0)) {
			throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID",
					new String[] { Labels.getLabel("label_JountAccountDetailDialog_CustCIF.value")}));
		}
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			
			if (ImplementationConstants.CO_APP_ENQ_SAME_AS_CUST_ENQ) {
				CustomerDetails customerDetails = customerDetailsService.getApprovedCustomerById(this.custID.longValue());
				map.put("customerDetails", customerDetails);
				map.put("newRecord", false);
				map.put("isEnqProcess", true);
				map.put("CustomerEnq", true);
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerDialog.zul", null, map);
			}else{
				map.put("custCIF", this.custCIF.getValue());
				customer = (Customer)PennantAppUtil.getCustomerObject(this.custCIF.getValue(), null);
				map.put("custid",customer.getCustID());
				map.put("jointcustid", this.custCIF.getValue());
				
				if(getFinanceMain() != null && StringUtils.isNotEmpty(getFinanceMain().getFinReference())){
					map.put("finFormatter", CurrencyUtil.getFormat(getFinanceMain().getFinCcy()));
					map.put("finReference", getFinanceMain().getFinReference());
				}
				map.put("finance", true);
				if (StringUtils.equals(customer.getCustCtgCode(),PennantConstants.PFF_CUSTCTG_INDIV)) {
					Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/FinCustomerDetailsEnq.zul", this.window_JountAccountDetailDialog, map);
				}else{
					Executions.createComponents("/WEB-INF/pages/CustomerMasters/Enquiry/CustomerSummary.zul", this.window_JountAccountDetailDialog, map);
				}
			}
		} catch (Exception e) {
			logger.error("Exception: Opening window", e);
		}
	}

	public void onCheck$includeRepay(Event event) {
		logger.debug("Entering" + event.toString());
		//doCheckIncludeRepay();
		logger.debug("Leaving" + event.toString());
	}

	public void doCheckIncludeRepay() {
		if (includeRepay.isChecked()) {
			this.repayAccountId.setButtonVisible(true);
			this.repayAccountId.setMandatoryStyle(true);
			this.repayAccountId.setValue("");
		} else {
			this.repayAccountId.setValue("");
			this.repayAccountId.setButtonVisible(false);
			this.repayAccountId.setMandatoryStyle(false);
			this.repayAccountId.setErrorMessage("");
			this.repayAccountId.setConstraint("");
		}
	}

	// GUI operations
	
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
		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custCIF.focus();
			this.hbox_Sequence.setVisible(false);
			this.label_Sequence.setVisible(false);

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
			if (aJountAccountDetail.getSequence() == 0) {
				this.hbox_Sequence.setVisible(false);
				this.label_Sequence.setVisible(false);
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
			//doCheckIncludeRepay();
			// setDialog(DialogType.EMBEDDED);
			this.window_JountAccountDetailDialog.setWidth("90%");
			this.window_JountAccountDetailDialog.setHeight("90%");
			if (isNewContributor()) {
				this.groupboxWf.setVisible(false);
				this.window_JountAccountDetailDialog.doModal();
			} else {
				setDialog(DialogType.EMBEDDED);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
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
		readOnlyComponent(isReadOnly("JountAccountDetailDialog_catOfCoApplicant"),this.catOfCoApplicant);
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
					this.viewCustInfo.setVisible(false);
				} else if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(newContributor);
					//this.btnSave.setVisible(false);
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}
	
	public boolean isReadOnly(String componentName){
		return getUserWorkspace().isReadOnly(componentName);
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

	// Helpers
	
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
		if (!enqModule) {
			getUserWorkspace().allocateAuthorities("JountAccountDetailDialog", getRole());
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_JountAccountDetailDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_JountAccountDetailDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_JountAccountDetailDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_JountAccountDetailDialog_btnSave"));
			this.btnSearchCustCIF.setVisible(getUserWorkspace().isAllowed("button_JountAccountDetailDialog_btnSearchCustCIF"));
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
		this.custCIFName.setMaxlength(50);
		this.custCIFName.setReadonly(true);
		if(!enqModule){
		this.repayAccountId.setAccountDetails(getFinanceMainDialogCtrl().getFinanceDetail().getFinScheduleData().getFinanceType().getFinType(), AccountConstants.FinanceAccount_REPY, finCcy);
		this.repayAccountId.setFormatter(CurrencyUtil.getFormat(getFinanceMainDialogCtrl().getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()));
		this.repayAccountId.setBranchCode(getFinanceMainDialogCtrl().getFinanceDetail().getFinScheduleData().getFinanceMain().getFinBranch());
		}
		if (!isNewRecord()) {
			this.row1.setVisible(false);
			this.row2.setVisible(true);
			//this.btnSearchCustCIF.setVisible(false);
			this.custCIF.setReadonly(false);
			this.gb_JointAccountPrimaryJoint.setVisible(true);
			this.gb_JointAccountSecondaryJoint.setVisible(true);
			this.gb_JointAccountGuarantorJoint.setVisible(true);
		}
		setStatusDetails(gb_statusDetails, groupboxWf, south, enqModule);
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
		this.custID.setValue(aJountAccountDetail.getCustID());
		this.includeRepay.setChecked(aJountAccountDetail.isIncludeRepay());
		this.repayAccountId.setValue(PennantApplicationUtil.formatAccountNumber(aJountAccountDetail.getRepayAccountId()));
		this.custCIFName.setValue(aJountAccountDetail.getLovDescCIFName());
		this.custCIFStatus.setValue(aJountAccountDetail.getStatus());
		this.custCIFWorstStatus.setValue(aJountAccountDetail.getWorstStatus());
		fillComboBox(this.catOfCoApplicant,aJountAccountDetail.getCatOfcoApplicant(),this.coapplicantList,"");	
		this.recordStatus.setValue(aJountAccountDetail.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aJountAccountDetail.getRecordType()));
		this.authoritySignatory.setChecked(aJountAccountDetail.isAuthoritySignatory());
		this.sequence.setValue(aJountAccountDetail.getSequence());
		logger.debug("Leaving");
	}

	// ================Primary Exposure Details
	public void doFillPrimaryExposureDetails(List<FinanceExposure> primaryExposureList) {
		logger.debug("Entering");
		this.listBox_JointAccountPrimary.getItems().clear();
		if (primaryExposureList != null) {
			recordCount = primaryExposureList.size();
			for (FinanceExposure primaryExposure : primaryExposureList) {
				Listitem listitem = new Listitem();
				Listcell listcell;
				listcell = new Listcell(primaryExposure.getFinType());
				listitem.appendChild(listcell);
				listcell = new Listcell(primaryExposure.getFinReference());
				listitem.appendChild(listcell);
				listcell = new Listcell(DateUtility.formatToLongDate(primaryExposure.getFinStartDate()));
				listitem.appendChild(listcell);
				listcell = new Listcell(DateUtility.formatToLongDate(primaryExposure.getMaturityDate()));
				listitem.appendChild(listcell);
				listcell = new Listcell(primaryExposure.getFinCCY());
				listitem.appendChild(listcell);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(primaryExposure.getFinanceAmt(), primaryExposure.getCcyEditField()));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(primaryExposure.getCurrentExpoSureinBaseCCY(), baseCcyDecFormat));
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
				listcell = new Listcell(PennantApplicationUtil.amountFormate(primaryExposure.getOverdueAmtBaseCCY(), baseCcyDecFormat));
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

			lc = new Listcell(PennantApplicationUtil.amountFormate(this.sumPrimaryDetails.getCurrentExpoSureinBaseCCY(), baseCcyDecFormat));
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

			lc = new Listcell(PennantApplicationUtil.amountFormate(this.sumPrimaryDetails.getOverdueAmtBaseCCY(), baseCcyDecFormat));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			item.setParent(this.listBox_JointAccountPrimary);
		}
		logger.debug("Leaving");
	}

	public void doFillSecoundaryExposureDetails(List<FinanceExposure> secondaryExposureList) {
		logger.debug("Entering");
		this.listBox_JointAccountSecondary.getItems().clear();
		if (secondaryExposureList != null) {
			recordCount = secondaryExposureList.size();
			for (FinanceExposure secondaryExposure : secondaryExposureList) {
				Listitem listitem = new Listitem();
				Listcell listcell;
				
				listcell = new Listcell(secondaryExposure.getFinType());
				listitem.appendChild(listcell);
				
				listcell = new Listcell(secondaryExposure.getFinReference());
				listitem.appendChild(listcell);
				
				listcell = new Listcell(DateUtility.formatToLongDate(secondaryExposure.getFinStartDate()));
				listitem.appendChild(listcell);
				
				listcell = new Listcell(DateUtility.formatToLongDate(secondaryExposure.getMaturityDate()));
				listitem.appendChild(listcell);
				
				listcell = new Listcell(secondaryExposure.getFinCCY());
				listitem.appendChild(listcell);
				
				listcell = new Listcell(PennantApplicationUtil.amountFormate(secondaryExposure.getFinanceAmt(), secondaryExposure.getCcyEditField()));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);
				
				listcell = new Listcell(PennantApplicationUtil.amountFormate(secondaryExposure.getCurrentExpoSureinBaseCCY(), baseCcyDecFormat));
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
				
				listcell = new Listcell(PennantApplicationUtil.amountFormate(secondaryExposure.getOverdueAmtBaseCCY(), baseCcyDecFormat));
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

			lc = new Listcell(PennantApplicationUtil.amountFormate(this.sumSecondaryDetails.getCurrentExpoSureinBaseCCY(), baseCcyDecFormat));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);
			

			lc = new Listcell(PennantApplicationUtil.amountFormate(this.sumSecondaryDetails.getOverdueAmtBaseCCY(), baseCcyDecFormat));
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
		this.listBox_JointAccountGuarantor.getItems().clear();
		if (guarantorExposureList != null) {
			recordCount = guarantorExposureList.size();
			for (FinanceExposure guarantorExposure : guarantorExposureList) {
				Listitem listitem = new Listitem();
				Listcell listcell;
				
				listcell = new Listcell(guarantorExposure.getFinType());
				listitem.appendChild(listcell);
				
				listcell = new Listcell(guarantorExposure.getFinReference());
				listitem.appendChild(listcell);
				
				listcell = new Listcell(DateUtility.formatToLongDate(guarantorExposure.getFinStartDate()));
				listitem.appendChild(listcell);
				
				listcell = new Listcell(DateUtility.formatToLongDate(guarantorExposure.getMaturityDate()));
				listitem.appendChild(listcell);
				
				listcell = new Listcell(guarantorExposure.getFinCCY());
				listitem.appendChild(listcell);
				
				listcell = new Listcell(PennantApplicationUtil.amountFormate(guarantorExposure.getFinanceAmt(), guarantorExposure.getCcyEditField()));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);
				
				listcell = new Listcell(PennantApplicationUtil.amountFormate(guarantorExposure.getCurrentExpoSureinBaseCCY(), baseCcyDecFormat));
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
				
				listcell = new Listcell(PennantApplicationUtil.amountFormate(guarantorExposure.getOverdueAmtBaseCCY(), baseCcyDecFormat));
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

			lc = new Listcell(PennantApplicationUtil.amountFormate(this.sumGurantorDetails.getCurrentExpoSureinBaseCCY(), baseCcyDecFormat));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);
			
			lc = new Listcell(PennantApplicationUtil.amountFormate(this.sumGurantorDetails.getOverdueAmtBaseCCY(), baseCcyDecFormat));
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
		if(!this.includeRepay.isChecked()){
			this.repayAccountId.setErrorMessage("");
		}
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		// Cust C I F
		try {
			aJountAccountDetail.setCustCIF(this.custCIF.getValue());
			if((!this.custCIF.isDisabled()) && this.custID.getValue() == 0) {
				wve.add(new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_JountAccountDetailDialog_CustCIF.value")})));
			}
			aJountAccountDetail.setCustID(this.custID.getValue());
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
			aJountAccountDetail.setRepayAccountId(PennantApplicationUtil.unFormatAccountNumber(this.repayAccountId.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// catOfCoApplicant
		try {
			aJountAccountDetail.setCatOfcoApplicant(this.catOfCoApplicant.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aJountAccountDetail.setAuthoritySignatory(this.authoritySignatory.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aJountAccountDetail.setSequence(this.sequence.getValue());
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
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		if (this.repayAccountId.isVisible() && this.repayAccountId.getValue().isEmpty()) {
			this.repayAccountId.setConstraint(new PTStringValidator(Labels.getLabel("label_JountAccountDetailDialog_RepayAccountId.value"),null,true));
		}

		if (!this.sequence.isReadonly() && this.authoritySignatory.isChecked()) {
			this.sequence.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_JountAccountDetailDialog_Sequence.value"), true, false, 1, 9));
		}

		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.custCIF.setConstraint("");
		this.repayAccountId.setConstraint("");
		this.catOfCoApplicant.setConstraint("");
		this.sequence.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		// Cust C I F
		if (!btnSearchCustCIF.isDisabled()) {
			this.custCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_JountAccountDetailDialog_CustCIF.value"),null,true));
		}
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveLOVValidation() {
		this.custCIFName.setConstraint("");
		this.custCIF.setConstraint("");
		this.catOfCoApplicant.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.custCIF.setErrorMessage("");
		this.custCIFName.setErrorMessage("");
		this.repayAccountId.setErrorMessage("");
		this.catOfCoApplicant.setErrorMessage("");
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
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aJountAccountDetail.getRecordType())) {
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
						closeDialog();
					}
				}
			} catch (DataAccessException e) {
				logger.error("Exception: ", e);
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
		this.catOfCoApplicant.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final JointAccountDetail aJointAccountDetail = new JointAccountDetail();
		BeanUtils.copyProperties(getJountAccountDetail(), aJointAccountDetail);
		boolean isNew = false;
		
		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the DocumentDetails object with the components data
		doWriteComponentsToBean(aJointAccountDetail);
		
		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here
		isNew = aJointAccountDetail.isNew();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aJointAccountDetail.getRecordType())) {
				aJointAccountDetail.setVersion(aJointAccountDetail.getVersion() + 1);
				if (isNew) {
					aJointAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aJointAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aJointAccountDetail.setNewRecord(true);
				}
			}
		} else {
			if (isNewContributor()) {
				if (isNewRecord()) {
					aJointAccountDetail.setVersion(1);
					aJointAccountDetail.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
				if (StringUtils.isBlank(aJointAccountDetail.getRecordType())) {
					aJointAccountDetail.setVersion(aJointAccountDetail.getVersion() + 1);
					aJointAccountDetail.setRecordType(PennantConstants.RCD_UPD);
					aJointAccountDetail.setNewRecord(true);
				}
				if (aJointAccountDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aJointAccountDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}
			} else {
				aJointAccountDetail.setVersion(aJointAccountDetail.getVersion() + 1);
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
				AuditHeader auditHeader = newJountAccountProcess(aJointAccountDetail, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_JountAccountDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					
					getFinanceMainDialogCtrl().doFillJointDetails(this.jointAccountDetailList);
					closeDialog();
				}
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
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
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
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
						}
					} else {
						jointAccountDetailList.add(jountAccountDetail);
					}
				} else if (jountAccountDetail.getSequence() == aJountAccountDetail.getSequence()
						&& aJountAccountDetail.getSequence() != 0) {
					String[] valueParam = new String[1];
					String[] errParam = new String[1];
					valueParam[0] = String.valueOf(aJountAccountDetail.getSequence());
					errParam[0] = PennantJavaUtil.getLabel("label_JointSequence") + ":" + valueParam[0];
					if (isNewRecord()) {

						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParam, valueParam),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					} else if (index != i) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParam, valueParam),
								getUserWorkspace().getUserLanguage()));
					}
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
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
						}
					} else {
						jointAccountDetailList.add(jountAccountDetail);
					}

				} else {
					jointAccountDetailList.add(jountAccountDetail);
				}
			}
		}
		if (PennantConstants.TRAN_UPD.equals(tranType)) {
			this.jointAccountDetailList.remove(index);
			this.jointAccountDetailList.add(jountAccountDetail);
			recordAdded = true;
		}
		if (!recordAdded) {
			jointAccountDetailList.add(aJountAccountDetail);
		}
		return auditHeader;
	}

	// WorkFlow Components
	
	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(JointAccountDetail aJountAccountDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aJountAccountDetail.getBefImage(), aJountAccountDetail);
		return new AuditHeader(String.valueOf(aJountAccountDetail.getJointAccountId()), null, null, null, auditDetail, aJountAccountDetail.getUserDetails(), getOverideMap());
	}

	public void onCheck$authoritySignatory(Event event) {
		logger.debug("Entering" + event.toString());
		doCheckAuthoritySignatory();
		logger.debug("Leaving" + event.toString());
	}

	public void doCheckAuthoritySignatory() {
		if (authoritySignatory.isChecked()) {
			this.hbox_Sequence.setVisible(true);
			this.label_Sequence.setVisible(true);
		} else {
			this.hbox_Sequence.setVisible(false);
			this.label_Sequence.setVisible(false);
			this.sequence.setValue(0);
		}
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
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
	
}

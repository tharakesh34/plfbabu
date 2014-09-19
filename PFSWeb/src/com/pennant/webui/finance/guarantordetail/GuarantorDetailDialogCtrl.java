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
 * FileName    		:  GuarantorDetailDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.finance.guarantordetail;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.finance.GuarantorDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTEmailValidator;
import com.pennant.util.Constraint.PTPhoneNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.finance.financemain.JointAccountDetailDialogCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.ScreenCTL;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/GuarantorDetail/guarantorDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class GuarantorDetailDialogCtrl extends GFCBaseCtrl implements Serializable {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(GuarantorDetailDialogCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_GuarantorDetailDialog;
	protected Row row0;
	protected Label label_FinReference;
	protected Hlayout hlayout_FinReference;
	protected Space space_FinReference;
	// protected Textbox finReference;
	protected Label label_BankCustomer;
	protected Hlayout hlayout_BankCustomer;
	protected Space space_BankCustomer;
	protected Checkbox bankCustomer;
	protected Row row1;
	protected Label label_GuarantorCIF;
	protected Hlayout hlayout_GuarantorCIF;
	protected Space space_GuarantorCIF;
	protected Textbox guarantorCIF;
	protected Label label_GuarantorIDType;
	protected Hlayout hlayout_GuarantorIDType;
	protected Space space_GuarantorIDType;
	protected Combobox guarantorIDType;
	protected Row row2;
	// protected Label label_GuarantorIDNumber;
	protected Hlayout hlayout_GuarantorIDNumber;
	protected Space space_GuarantorIDNumber;
	protected Textbox guarantorIDNumber;
	protected Label label_Name;
	protected Hlayout hlayout_Name;
	protected Space space_Name;
	protected Textbox guarantorCIFName;
	protected Row row3;
	protected Label label_GuranteePercentage;
	protected Hlayout hlayout_GuranteePercentage;
	protected Space space_GuranteePercentage;
	protected Decimalbox guranteePercentage;
	protected Label label_MobileNo;
	protected Hlayout hlayout_MobileNo;
	protected Space space_MobileNo;
	protected Textbox mobileNo;
	protected Row row4;
	protected Label label_EmailId;
	protected Hlayout hlayout_EmailId;
	protected Space space_EmailId;
	protected Textbox emailId;
	protected Label label_GuarantorProof;
	protected Hlayout hlayout_GuarantorProof;
	protected Space space_GuarantorProof;
	protected Textbox guarantorProof;
	private byte[] guarantorProofContent;
	protected Row row5;
	protected Row row6;
	protected Label label_Remarks;
	protected Hlayout hlayout_Remarks;
	protected Space space_Remarks;
	protected Textbox remarks;
	protected Textbox status;
	protected Textbox worstStatus;
	protected Textbox guarantorProofName;
	protected Label recordStatus;
	protected Label recordType;
	protected Radiogroup userAction;
	protected Groupbox gb_statusDetails;
	protected Groupbox groupboxWf;
	protected Groupbox gb_basicDetails;
	protected Groupbox gb_GurantorsPrimaryExposure;
	protected Groupbox gb_GurantorsSecoundaryExposure;
	protected Groupbox gb_GurantorsExposure;
	protected South south;
	private boolean enqModule = false;
	private int index;	
	// not auto wired vars
	private GuarantorDetail guarantorDetail; // overhanded per param
	private transient GuarantorDetailListCtrl guarantorDetailListCtrl; // overhanded
	// per
	// param
	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient boolean oldVar_BankCustomer;
	private transient String oldVar_GuarantorCIF;
	private transient String oldVar_GuarantorIDType;
	private transient String oldVar_GuarantorIDNumber;
	private transient BigDecimal oldVar_GuranteePercentage;
	private transient String oldVar_MobileNo;
	private transient String oldVar_EmailId;
	private transient String oldVar_GuarantorProof;
	private transient String oldVar_GuarantorProofName;
	private transient String oldVar_recordStatus;
	private transient String oldVar_Remarks;
	private boolean notes_Entered = false;
	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_GuarantorDetailDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew;
	protected Button btnEdit;
	protected Button btnDelete;
	protected Button btnSave;
	protected Button btnCancel;
	protected Button btnClose;
	protected Button btnHelp;
	protected Button btnNotes;
	protected Button btnSearchGuarantorCIF;
	protected Button btnUploadGuarantorProof;
	private transient String oldVar_GuarantorCIFName;
	// ServiceDAOs / Domain Classes
	private transient GuarantorDetailService guarantorDetailService;
	private transient PagedListService pagedListService;
	private List<ValueLabel> listGuarantorIDType = PennantAppUtil.getIdentityType();
	private boolean newRecord = false;
	private boolean newGuarantor = false;
	private JointAccountDetailDialogCtrl finJointAccountCtrl;
	private List<GuarantorDetail> guarantorDetailDetailList; // overhanded per
	// param
	private List<FinanceExposure> primaryList = null;
	private List<FinanceExposure> secoundaryList = null;
	private List<FinanceExposure> guarantorList = null;
	private String moduleType = "";
	public int borderLayoutHeight = 0;
	protected Listbox listBoxGurantorsPrimaryExposure;
	protected Listbox listBoxGurantorsSecoundaryExposure;
	protected Listbox listBoxGurantorsExposure;
	BigDecimal totfinAmt = new BigDecimal(0);
	BigDecimal totCurrentAmt = new BigDecimal(0);
	BigDecimal totDueAmt = new BigDecimal(0);
	long recordCount = 0;
	String primaryCustId;
	int ccDecimal = 0;
	private String cif[]=null;
	/**
	 * default constructor.<br>
	 */
	public GuarantorDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected GuarantorDetail object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_GuarantorDetailDialog(Event event) throws Exception {
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
			if (args.containsKey("guarantorDetail")) {
				this.guarantorDetail = (GuarantorDetail) args.get("guarantorDetail");
				GuarantorDetail befImage = new GuarantorDetail();
				BeanUtils.copyProperties(this.guarantorDetail, befImage);
				this.guarantorDetail.setBefImage(befImage);
				setNewGuarantor(true);
				setGuarantorDetail(this.guarantorDetail);
			} else {
				setGuarantorDetail(null);
			}
			if (args.containsKey("index")) {
				this.index = (Integer) args.get("index");
			}
			if (args.containsKey("ccDecimal")) {
				this.ccDecimal = (Integer) args.get("ccDecimal");
			}
			if (args.containsKey("filter")) {
				this.cif = (String[]) args.get("filter");
			}
			if (args.containsKey("finJointAccountCtrl")) {
				setFinanceMainDialogCtrl((JointAccountDetailDialogCtrl) args.get("finJointAccountCtrl"));
				setNewGuarantor(true);
				if (args.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.guarantorDetail.setWorkflowId(0);
				if (args.containsKey("roleCode")) {
					setRole((String) args.get("roleCode"));
					getUserWorkspace().alocateRoleAuthorities(getRole(), "GuarantorDetailDialog");
				}
			}
			if (args.containsKey("primaryCustID")) {
				primaryCustId = (String) args.get("primaryCustID");
			}
			doLoadWorkFlow(this.guarantorDetail.isWorkflow(), this.guarantorDetail.getWorkflowId(), this.guarantorDetail.getNextTaskId());
			if (isWorkFlowEnabled() && !enqModule && !isNewGuarantor()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "GuarantorDetailDialog");
			} 

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the guarantorsDetailListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete guarantorsDetail here.
			if (args.containsKey("guarantorDetailListCtrl")) {
				setGuarantorDetailListCtrl((GuarantorDetailListCtrl) args.get("guarantorDetailListCtrl"));
			} else {
				setGuarantorDetailListCtrl(null);
			}
			this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue() - PennantConstants.borderlayoutMainNorth;
			this.window_GuarantorDetailDialog.setHeight(this.borderLayoutHeight - 152 + "px");// 425px
			// this.finDocumentPdfView.setHeight(this.borderLayoutHeight - 152+
			// "px");// 425px
			// set Field Properties
			if (getGuarantorDetail().isBankCustomer()) {
				primaryList = getGuarantorDetailService().getPrimaryExposureList(getGuarantorDetail());
				secoundaryList = getGuarantorDetailService().getSecondaryExposureList(getGuarantorDetail());
				guarantorList = getGuarantorDetailService().getGuarantorExposureList(getGuarantorDetail());
			}
			doSetFieldProperties();
			doShowDialog(getGuarantorDetail());
		} catch (Exception e) {
			createException(window_GuarantorDetailDialog, e);
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
		PTMessageUtils.showHelpWindow(event, window_GuarantorDetailDialog);
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
	public void onClose$window_GuarantorDetailDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
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
			ScreenCTL.displayNotes(getNotes("GuarantorDetail", String.valueOf(getGuarantorDetail().getGuarantorId()), getGuarantorDetail().getVersion()), this);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchGuarantorCIF(Event event) {
		Customer customer = null;
		if (cif != null) {
			Filter filter[] = new Filter[1];
			filter[0] = new Filter("CustCIF", cif, Filter.OP_NOT_IN);
			Object dataObject = ExtendedSearchListBox.show(this.window_GuarantorDetailDialog, "Customer", filter);
			if (dataObject instanceof String) {
				this.guarantorCIF.setValue(dataObject.toString());
				this.guarantorCIFName.setValue("");
			} else {
				customer = (Customer) dataObject;
				if (customer != null) {
					this.guarantorCIF.setValue(customer.getCustCIF());
					this.guarantorCIFName.setValue(customer.getCustShrtName());
				}
			}
		}
		setCustomerDetails(customer);
	}

	public void setCustomerDetails(Customer customer) {
		if(customer !=null) {
			getGuarantorDetail().setGuarantorCIF(customer.getCustCIF());	
			getGuarantorDetail().setStatus(customer.getLovDescCustStsName());
			getGuarantorDetail().setWorstStatus(getGuarantorDetailService().getWorstStaus(customer.getCustID()));
			this.primaryList = getGuarantorDetailService().getPrimaryExposureList(getGuarantorDetail());
			this.secoundaryList = getGuarantorDetailService().getSecondaryExposureList(getGuarantorDetail());
			this.guarantorList = getGuarantorDetailService().getGuarantorExposureList(getGuarantorDetail());
		
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

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aGuarantorDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(GuarantorDetail aGuarantorDetail) throws InterruptedException {
		logger.debug("Entering");
		// if aGuarantorDetail == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aGuarantorDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aGuarantorDetail = getGuarantorDetailService().getNewGuarantorDetail();
			setGuarantorDetail(aGuarantorDetail);
		} else {
			setGuarantorDetail(aGuarantorDetail);
		}
		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.bankCustomer.focus();
		} else {
			if (isNewGuarantor()) {
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
			doWriteBeanToComponents(aGuarantorDetail);
			// set ReadOnly mode accordingly if the object is new or not.
			// displayComponents(ScreenCTL.getMode(enqModule,isWorkFlowEnabled(),aGuarantorDetail.isNewRecord()));
			onCheck$bankCustomer(new Event("onCheck$bankCustomer"));
			if (this.primaryList==null || this.primaryList.size()==0) {
				this.gb_GurantorsPrimaryExposure.setVisible(false);
			}
			if (this.secoundaryList==null || this.secoundaryList.size()==0) {
				this.gb_GurantorsSecoundaryExposure.setVisible(false);
			}
			if (this.guarantorList==null || this.guarantorList.size()==0) {
				this.gb_GurantorsExposure.setVisible(false);
			}
			doStoreInitValues();
			this.window_GuarantorDetailDialog.setHeight("90%");
			this.window_GuarantorDetailDialog.setWidth("90%");
			if (isNewGuarantor()) {
				this.groupboxWf.setVisible(false);
				this.window_GuarantorDetailDialog.doModal();
			} else {
				setDialog(this.window_GuarantorDetailDialog);
			}
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (isNewRecord()) {
			if (isNewGuarantor()) {
				this.btnCancel.setVisible(false);
			}
		} else {
			this.btnCancel.setVisible(true);
			this.guranteePercentage.setReadonly(isReadOnly("GuarantorDetailDialog_GuranteePercentage"));
		}
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.guarantorDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (newGuarantor) {
				if ("ENQ".equals(this.moduleType)) {
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
				} else if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(newGuarantor);
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}
	
	public boolean isReadOnly(String componentName){
		if (isWorkFlowEnabled() || isNewGuarantor()){
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
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

	public void readOnlyExposureFields(boolean exposure) {
		if (exposure) {
			if (getGuarantorDetail().isBankCustomer()) {
				this.gb_GurantorsPrimaryExposure.setVisible(true);
				this.gb_GurantorsSecoundaryExposure.setVisible(true);
				this.gb_GurantorsExposure.setVisible(true);
			} else {
				this.gb_GurantorsPrimaryExposure.setVisible(false);
				this.gb_GurantorsSecoundaryExposure.setVisible(false);
				this.gb_GurantorsExposure.setVisible(false);
			}
			this.row0.setVisible(false);
			this.row5.setVisible(false);
			this.row4.setVisible(false);
			this.row6.setVisible(true);
			this.btnSearchGuarantorCIF.setVisible(false);
		} else {
			this.gb_GurantorsPrimaryExposure.setVisible(false);
			this.gb_GurantorsSecoundaryExposure.setVisible(false);
			this.gb_GurantorsExposure.setVisible(false);
			this.row0.setVisible(true);
			this.row5.setVisible(true);
			this.row4.setVisible(true);
			this.row6.setVisible(false);
			this.btnSearchGuarantorCIF.setVisible(true);
		}
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
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		if (!enqModule) {
			getUserWorkspace().alocateAuthorities("GuarantorDetailDialog", getRole());
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_GuarantorDetailDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_GuarantorDetailDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_GuarantorDetailDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_GuarantorDetailDialog_btnSave"));
		}
		/* create the Button Controller. Disable not used buttons during working */
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		// this.finReference.setMaxlength(20);
		this.guarantorCIF.setMaxlength(12);
		this.guarantorIDNumber.setMaxlength(20);
		this.guarantorCIFName.setMaxlength(100);
		this.guranteePercentage.setMaxlength(5);
		this.guranteePercentage.setFormat(PennantConstants.rateFormate2);
		this.guranteePercentage.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.guranteePercentage.setScale(2);
		this.mobileNo.setMaxlength(25);
		this.emailId.setMaxlength(200);
		this.guarantorProof.setMaxlength(1073741823);
		this.remarks.setMaxlength(500);
		setStatusDetails(gb_statusDetails, groupboxWf, south, enqModule);
		logger.debug("Leaving");
	}

	/**
	 * Stores the initialinitial values to member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		// this.oldVar_FinReference = this.finReference.getValue();
		this.oldVar_BankCustomer = this.bankCustomer.isChecked();
		this.oldVar_GuarantorCIF = this.guarantorCIF.getValue();
		this.oldVar_GuarantorCIFName = this.guarantorCIFName.getValue();
		this.oldVar_GuarantorIDType = PennantConstants.List_Select;
		if (this.guarantorIDType.getSelectedItem() != null) {
			this.oldVar_GuarantorIDType = this.guarantorIDType.getSelectedItem().getValue().toString();
		}
		this.oldVar_GuarantorIDNumber = this.guarantorIDNumber.getValue();
		this.oldVar_GuarantorCIFName = this.guarantorCIFName.getValue();
		this.oldVar_GuranteePercentage = this.guranteePercentage.getValue();
		this.oldVar_MobileNo = this.mobileNo.getValue();
		this.oldVar_EmailId = this.emailId.getValue();
		this.oldVar_GuarantorProof = this.guarantorProof.getValue();
		this.oldVar_GuarantorProofName = this.guarantorProofName.getValue();
		this.oldVar_Remarks = this.remarks.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		// this.finReference.setValue(this.oldVar_FinReference);
		this.bankCustomer.setChecked(this.oldVar_BankCustomer);
		this.guarantorCIF.setValue(this.oldVar_GuarantorCIF);
		this.guarantorCIFName.setValue(this.oldVar_GuarantorCIFName);
		fillComboBox(this.guarantorIDType, this.oldVar_GuarantorIDType, listGuarantorIDType, "");
		this.guarantorIDNumber.setValue(this.oldVar_GuarantorIDNumber);
		this.guarantorCIFName.setValue(this.oldVar_GuarantorCIFName);
		this.guranteePercentage.setValue(this.oldVar_GuranteePercentage);
		this.mobileNo.setValue(this.oldVar_MobileNo);
		this.emailId.setValue(this.oldVar_EmailId);
		this.guarantorProof.setValue(this.oldVar_GuarantorProof);
		this.guarantorProofName.setValue(this.oldVar_GuarantorProofName);
		this.remarks.setValue(this.oldVar_Remarks);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		if (isWorkFlowEnabled() & !enqModule) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aGuarantorDetail
	 *            GuarantorDetail
	 */
	public void doWriteBeanToComponents(GuarantorDetail aGuarantorDetail) {
		logger.debug("Entering");
		// this.finReference.setValue(aGuarantorDetail.getFinReference());
		this.bankCustomer.setChecked(aGuarantorDetail.isBankCustomer());
		if (!aGuarantorDetail.isBankCustomer()) {
			fillComboBox(this.guarantorIDType, aGuarantorDetail.getGuarantorIDType(), listGuarantorIDType, "");
		} else {
			this.guarantorIDType.setValue("Bank CIF");
		}
		this.guarantorCIF.setValue(aGuarantorDetail.getGuarantorCIF());
		this.guarantorCIFName.setValue(aGuarantorDetail.getGuarantorCIFName());
		this.guarantorIDNumber.setValue(aGuarantorDetail.getGuarantorIDNumber());
		this.guranteePercentage.setValue(aGuarantorDetail.getGuranteePercentage());
		this.mobileNo.setValue(aGuarantorDetail.getMobileNo());
		this.emailId.setValue(aGuarantorDetail.getEmailId());
		this.guarantorProofContent = aGuarantorDetail.getGuarantorProof();
		this.guarantorProofName.setValue(aGuarantorDetail.getGuarantorProofName());
		this.remarks.setValue(aGuarantorDetail.getRemarks());
		this.status.setValue(aGuarantorDetail.getStatus());
		this.worstStatus.setValue(aGuarantorDetail.getWorstStatus());
		this.recordStatus.setValue(aGuarantorDetail.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aGuarantorDetail.getRecordType()));

		setGurantorIDNumProp();
		logger.debug("Leaving");
	}
	String toCcy = SystemParameterDetails.getSystemParameterValue("APP_DFT_CURR").toString();
	int DFT_CURR_EDIT_FIELD = Integer.parseInt(SystemParameterDetails.getSystemParameterValue("APP_DFT_CURR_EDIT_FIELD").toString());
	// ================Primary Exposure Details
	public void doFillPrimaryExposureDetails(List<FinanceExposure> primaryExposureList) {
		logger.debug("Entering");
		if (primaryExposureList != null) {
			
			BigDecimal totFinaceAmout = BigDecimal.ZERO;
			BigDecimal totCurrentExposer = BigDecimal.ZERO;
			BigDecimal totOverDueAmount = BigDecimal.ZERO;
			
			this.listBoxGurantorsPrimaryExposure.getItems().clear();
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
				totFinaceAmout=totFinaceAmout.add(primaryExposure.getFinanceAmt());
				listcell = new Listcell(PennantApplicationUtil.amountFormate(primaryExposure.getFinanceAmt(), primaryExposure.getCcyEditField()));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);
				BigDecimal currentExpoSure=CalculationUtil.getConvertedAmount(primaryExposure.getFinCCY(), toCcy, primaryExposure.getCurrentExpoSure());
				totCurrentExposer=totCurrentExposer.add(currentExpoSure);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(currentExpoSure,DFT_CURR_EDIT_FIELD));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);
				listcell = new Listcell();
				if (primaryExposure.getOverdueAmt()!= null && primaryExposure.getOverdueAmt().compareTo(BigDecimal.ZERO)!=0) {
					listcell.setLabel(PennantConstants.YES);
				} else {
					listcell.setLabel(PennantConstants.NO);
				}
				listitem.appendChild(listcell);
				listcell = new Listcell(primaryExposure.getPastdueDays());
				listitem.appendChild(listcell);
				BigDecimal overdueAmt=CalculationUtil.getConvertedAmount(primaryExposure.getFinCCY(), toCcy, primaryExposure.getOverdueAmt());
				totOverDueAmount=totOverDueAmount.add(overdueAmt);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(overdueAmt,DFT_CURR_EDIT_FIELD));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);
				listitem.setAttribute("data", primaryExposure);
				this.listBoxGurantorsPrimaryExposure.appendChild(listitem);
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

			lc = new Listcell(PennantApplicationUtil.amountFormate(totCurrentExposer,DFT_CURR_EDIT_FIELD));
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

			lc = new Listcell(PennantApplicationUtil.amountFormate(totOverDueAmount,DFT_CURR_EDIT_FIELD));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);
			item.setParent(this.listBoxGurantorsPrimaryExposure);
			getGuarantorDetail().setPrimaryExposure(String.valueOf(totCurrentExposer));
		}
		logger.debug("Leaving");
	}

	public void doFillSecoundaryExposureDetails(List<FinanceExposure> secondaryExposureList) {
		logger.debug("Entering");
		if (secondaryExposureList != null) {
			BigDecimal totFinaceAmout = BigDecimal.ZERO;
			BigDecimal totCurrentExposer = BigDecimal.ZERO;
			BigDecimal totOverDueAmount = BigDecimal.ZERO;
			
			this.listBoxGurantorsSecoundaryExposure.getItems().clear();
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

				totFinaceAmout=totFinaceAmout.add(secondaryExposure.getFinanceAmt());
				listcell = new Listcell(PennantApplicationUtil.amountFormate(secondaryExposure.getFinanceAmt(), secondaryExposure.getCcyEditField()));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);

				BigDecimal currentExpoSure=CalculationUtil.getConvertedAmount(secondaryExposure.getFinCCY(), toCcy, secondaryExposure.getCurrentExpoSure());
				totCurrentExposer=totCurrentExposer.add(currentExpoSure);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(currentExpoSure,DFT_CURR_EDIT_FIELD));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);

				listcell = new Listcell();
				if (secondaryExposure.getOverdueAmt()!= null && secondaryExposure.getOverdueAmt().compareTo(BigDecimal.ZERO)!=0) {
					listcell.setLabel(PennantConstants.YES);
				} else {
					listcell.setLabel(PennantConstants.NO);
				}
				listitem.appendChild(listcell);

				listcell = new Listcell(secondaryExposure.getPastdueDays());
				listitem.appendChild(listcell);

				BigDecimal overdueAmt=CalculationUtil.getConvertedAmount(secondaryExposure.getFinCCY(), toCcy, secondaryExposure.getOverdueAmt());
				totOverDueAmount=totOverDueAmount.add(overdueAmt);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(overdueAmt,DFT_CURR_EDIT_FIELD));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);

				listcell = new Listcell(secondaryExposure.getCustCif());
				listitem.appendChild(listcell);

				listitem.setAttribute("data", secondaryExposure);
				this.listBoxGurantorsSecoundaryExposure.appendChild(listitem);
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

			lc = new Listcell(PennantApplicationUtil.amountFormate(totCurrentExposer,DFT_CURR_EDIT_FIELD));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);


			lc = new Listcell(PennantApplicationUtil.amountFormate(totOverDueAmount,DFT_CURR_EDIT_FIELD));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);
			getGuarantorDetail().setSecondaryExposure(String.valueOf(totCurrentExposer));
			this.listBoxGurantorsSecoundaryExposure.appendChild(item);
		}
		logger.debug("Leaving");
	}

	public void doFillGuarantorExposureDetails(List<FinanceExposure> guarantorExposureList) {
		logger.debug("Entering");
		if (guarantorExposureList != null) {
			BigDecimal totFinaceAmout = BigDecimal.ZERO;
			BigDecimal totCurrentExposer = BigDecimal.ZERO;
			BigDecimal totOverDueAmount = BigDecimal.ZERO;
			
			this.listBoxGurantorsExposure.getItems().clear();
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

				totFinaceAmout=totFinaceAmout.add(guarantorExposure.getFinanceAmt());
				listcell = new Listcell(PennantApplicationUtil.amountFormate(guarantorExposure.getFinanceAmt(), guarantorExposure.getCcyEditField()));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);

				BigDecimal currentExpoSure=CalculationUtil.getConvertedAmount(guarantorExposure.getFinCCY(), toCcy, guarantorExposure.getCurrentExpoSure());
				totCurrentExposer=totCurrentExposer.add(currentExpoSure);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(currentExpoSure,DFT_CURR_EDIT_FIELD));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);

				listcell = new Listcell();
				if (guarantorExposure.getOverdueAmt()!= null && guarantorExposure.getOverdueAmt().compareTo(BigDecimal.ZERO)!=0) {
					listcell.setLabel(PennantConstants.YES);
				} else {
					listcell.setLabel(PennantConstants.NO);
				}
				listitem.appendChild(listcell);

				listcell = new Listcell(guarantorExposure.getPastdueDays());
				listitem.appendChild(listcell);

				BigDecimal overdueAmt=CalculationUtil.getConvertedAmount(guarantorExposure.getFinCCY(), toCcy, guarantorExposure.getOverdueAmt());
				totOverDueAmount=totOverDueAmount.add(overdueAmt);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(overdueAmt,DFT_CURR_EDIT_FIELD));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);

				listcell = new Listcell(guarantorExposure.getCustCif());
				listitem.appendChild(listcell);

				listitem.setAttribute("data", guarantorExposure);
				this.listBoxGurantorsExposure.appendChild(listitem);
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

			lc = new Listcell(PennantApplicationUtil.amountFormate(totCurrentExposer,DFT_CURR_EDIT_FIELD));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totOverDueAmount,DFT_CURR_EDIT_FIELD));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			getGuarantorDetail().setGuarantorExposure(String.valueOf(totCurrentExposer));
			this.listBoxGurantorsExposure.appendChild(item);
		}
		logger.debug("Leaving");
	}

	/*
	 * Method for closing Customer Selection Window
	 * 
	 * @throws InterruptedException
	 */
	public void closeWindow() throws InterruptedException {
		logger.debug("Entering");
		if (isNewGuarantor()) {
			closePopUpWindow(this.window_GuarantorDetailDialog, "GuarantorDetailDialog");
		} else {
			closeDialog(this.window_GuarantorDetailDialog, "GuarantorDetailDialog");
		}
		logger.debug("Leaving");
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
			ErrorControl.showErrorControl(this.window_GuarantorDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Uploading Proof Details File
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onUpload$btnUploadGuarantorProof(UploadEvent event) throws Exception {
		logger.debug("Entering" + event.toString());
		Media media = event.getMedia();
		this.guarantorProofName.setValue((media.getName()));
		this.guarantorProof.setValue((String.valueOf(IOUtils.toByteArray(media.getStreamData()))));
		this.guarantorProofContent = IOUtils.toByteArray(media.getStreamData());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aGuarantorDetail
	 */
	public void doWriteComponentsToBean(GuarantorDetail aGuarantorDetail) {
		logger.debug("Entering");
		doSetLOVValidation();
		setGurantorIDNumProp();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		// Fin Reference
		/*
		 * try { aGuarantorDetail.setFinReference(this.finReference.getValue());
		 * }catch (WrongValueException we ) { wve.add(we); }
		 */
		// Bank Customer
		try {
			aGuarantorDetail.setBankCustomer(this.bankCustomer.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Guarantor CIF
		try {
			aGuarantorDetail.setGuarantorCIFName(this.guarantorCIFName.getValue());
			aGuarantorDetail.setGuarantorCIF(this.guarantorCIF.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// ID Type
		try {
			String strGuarantorIDType = null;
			if (this.guarantorIDType.getSelectedItem() != null) {
				strGuarantorIDType = this.guarantorIDType.getSelectedItem().getValue().toString();
			}
			if (strGuarantorIDType != null && !PennantConstants.List_Select.equals(strGuarantorIDType)) {
				aGuarantorDetail.setGuarantorIDType(strGuarantorIDType);
				aGuarantorDetail.setGuarantorIDTypeName(this.guarantorIDType.getSelectedItem().getLabel());
			} else {
				aGuarantorDetail.setGuarantorIDType(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// ID Number
		try {
			aGuarantorDetail.setGuarantorIDNumber(this.guarantorIDNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Name
		try {
			aGuarantorDetail.setGuarantorCIFName(this.guarantorCIFName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Percentage
		try {
			if (this.guranteePercentage.getValue() != null) {
				aGuarantorDetail.setGuranteePercentage(this.guranteePercentage.getValue());
			}
			if (this.guranteePercentage.getValue() != null) {
				aGuarantorDetail.setGuranteePercentage(this.guranteePercentage.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Mobile No
		try {
			aGuarantorDetail.setMobileNo(this.mobileNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Email Id
		try {
			aGuarantorDetail.setEmailId(this.emailId.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Proof
		try {
			aGuarantorDetail.setGuarantorProof(guarantorProofContent);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Proof Name
		try {
			if (!this.bankCustomer.isChecked() && (StringUtils.trimToEmpty(this.guarantorProofName.getValue()).equals("") || this.guarantorProofContent == null)) {
				throw new WrongValueException(this.guarantorProofName, Labels.getLabel("MUST_BE_UPLOADED",
						new String[] { Labels.getLabel("label_GuarantorDetailDialog_GuarantorProof.value")}));
			}
			aGuarantorDetail.setGuarantorProofName(this.guarantorProofName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Remarks
		try {
			aGuarantorDetail.setRemarks(this.remarks.getValue());
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
		aGuarantorDetail.setRecordStatus(this.recordStatus.getValue());
		setGuarantorDetail(aGuarantorDetail);
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
		/*
		 * if
		 * (!StringUtils.trimToEmpty(this.oldVar_FinReference).equals(StringUtils
		 * .trimToEmpty(this.finReference.getValue()))) { return true; }
		 */
		if (this.oldVar_BankCustomer != this.bankCustomer.isChecked()) {
			return true;
		}
		if (!StringUtils.trimToEmpty(this.oldVar_GuarantorCIF).equals(StringUtils.trimToEmpty(this.guarantorCIF.getValue()))) {
			return true;
		}
		String strGuarantorIDType = PennantConstants.List_Select;
		if (this.guarantorIDType.getSelectedItem() != null) {
			strGuarantorIDType = this.guarantorIDType.getSelectedItem().getValue().toString();
		}
		if (!StringUtils.trimToEmpty(this.oldVar_GuarantorIDType).equals(strGuarantorIDType)) {
			return true;
		}
		if (!StringUtils.trimToEmpty(this.oldVar_GuarantorIDNumber).equals(StringUtils.trimToEmpty(this.guarantorIDNumber.getValue()))) {
			return true;
		}
		if (!StringUtils.trimToEmpty(this.oldVar_GuarantorCIFName).equals(StringUtils.trimToEmpty(this.guarantorCIFName.getValue()))) {
			return true;
		}
		if (this.oldVar_GuranteePercentage != this.guranteePercentage.getValue()) {
			return true;
		}
		if (!StringUtils.trimToEmpty(this.oldVar_MobileNo).equals(StringUtils.trimToEmpty(this.mobileNo.getValue()))) {
			return true;
		}
		if (!StringUtils.trimToEmpty(this.oldVar_EmailId).equals(StringUtils.trimToEmpty(this.emailId.getValue()))) {
			return true;
		}
		if (!StringUtils.trimToEmpty(this.oldVar_GuarantorProof).equals(StringUtils.trimToEmpty(this.guarantorProof.getValue()))) {
			return true;
		}
		if (!StringUtils.trimToEmpty(this.oldVar_GuarantorProofName).equals(StringUtils.trimToEmpty(this.guarantorProofName.getValue()))) {
			return true;
		}
		if (!StringUtils.trimToEmpty(this.oldVar_Remarks).equals(StringUtils.trimToEmpty(this.remarks.getValue()))) {
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
		if (this.bankCustomer.isChecked()) {
			this.guarantorCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_GuarantorCIF.value"), PennantRegularExpressions.REGEX_NUMERIC, true));
		}
		// Percentage
		if (!this.guranteePercentage.isReadonly()) {
			this.guranteePercentage.setConstraint(new PTDecimalValidator(Labels.getLabel("label_GuarantorDetailDialog_GuranteePercentage.value"), 2, true, false, 100));
		}
		if (!this.bankCustomer.isChecked()) {
			// ID Type
			if (this.guarantorIDType.isReadonly()) {
				this.guarantorIDType.setConstraint(new StaticListValidator(listGuarantorIDType, Labels.getLabel("label_GuarantorDetailDialog_GuarantorIDType.value")));
			}
			// ID Number
			if (!this.guarantorIDNumber.isReadonly()) {
				this.guarantorIDNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_GuarantorIDNumber.value"), PennantRegularExpressions.REGEX_ALPHANUM, true));
			}
			// Name
			if (!this.guarantorCIFName.isReadonly()) {
				this.guarantorCIFName.setConstraint(new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_Name.value"), PennantRegularExpressions.REGEX_NAME, true));
			}
			// Mobile No
			/*if (!this.mobileNo.isReadonly()) {
				this.mobileNo.setConstraint(new SimpleConstraint(PennantRegularExpressions.MOBILE_REGEX, Labels.getLabel("MAND_FIELD_PHONENUM", new String[] { Labels.getLabel("label_GuarantorDetailDialog_MobileNo.value") })));
			}*/
			if (!this.mobileNo.isReadonly()) {
				this.mobileNo.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_GuarantorDetailDialog_MobileNo.value"),true));
			}
			// Email Id
			if (!this.emailId.isReadonly()) {
				this.emailId.setConstraint(new PTEmailValidator( Labels.getLabel("label_GuarantorDetailDialog_EmailId.value") , false));
			}
			/*
			 * if (!this.guarantorProofName.isReadonly()) {
			 * this.guarantorProofName .setConstraint(new PTStringValidator(
			 * Labels
			 * .getLabel("label_GuarantorDetailDialog_GuarantorProof.value"),
			 * PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL, true)); }
			 */
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		// this.finReference.setConstraint("");
		this.guarantorIDType.setConstraint("");
		this.guarantorIDNumber.setConstraint("");
		this.guarantorCIF.setConstraint("");
		this.guarantorCIFName.setConstraint("");
		this.guranteePercentage.setConstraint("");
		this.mobileNo.setConstraint("");
		this.emailId.setConstraint("");
		this.guarantorProof.setConstraint("");
		this.guarantorProofName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		// Guarantor CIF
		if (!btnSearchGuarantorCIF.isVisible()) {
			this.guarantorCIFName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_GuarantorDetailDialog_GuarantorCIF.value") }));
		}
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveLOVValidation() {
		this.guarantorCIFName.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		// this.finReference.setErrorMessage("");
		this.guarantorCIFName.setErrorMessage("");
		this.guarantorIDType.setErrorMessage("");
		this.guarantorIDNumber.setErrorMessage("");
		this.guarantorCIFName.setErrorMessage("");
		this.guranteePercentage.setErrorMessage("");
		this.mobileNo.setErrorMessage("");
		this.emailId.setErrorMessage("");
		this.guarantorProof.setErrorMessage("");
		this.guarantorProofName.setErrorMessage("");
		this.remarks.setErrorMessage("");
		logger.debug("Leaving");
	}

	public void onCheck$bankCustomer(Event event) {
		logger.debug("Entering");
		if (this.bankCustomer.isChecked()) {
			doClearMessage();
			this.guarantorIDType.setDisabled(true);
			this.guarantorCIF.setReadonly(true);
			this.btnSearchGuarantorCIF.setVisible(true);
			this.guarantorIDNumber.setDisabled(true);
			this.guarantorIDNumber.setReadonly(true);
			this.guarantorCIFName.setReadonly(true);
			this.mobileNo.setReadonly(true);
			this.emailId.setReadonly(true);
			this.btnUploadGuarantorProof.setVisible(false);
			this.hlayout_GuarantorCIF.setVisible(true);
			this.hlayout_GuarantorIDNumber.setVisible(false);
			this.space_GuarantorIDType.setVisible(false);
			this.space_GuarantorIDNumber.setVisible(false);
			this.space_Name.setVisible(false);
			this.space_MobileNo.setVisible(false);
			this.space_EmailId.setVisible(false);
			this.space_GuarantorProof.setVisible(false);
			this.space_GuarantorCIF.setVisible(true);
			this.space_GuranteePercentage.setVisible(true);
			this.guarantorIDType.setValue("Bank CIF");
		} else {
			this.guranteePercentage.setReadonly(isReadOnly("GuarantorDetailDialog_GuranteePercentage"));
			this.guarantorIDType.setDisabled(isReadOnly("GuarantorDetailDialog_GuarantorIDType"));
			this.guarantorCIF.setReadonly(isReadOnly("GuarantorDetailDialog_GuarantorCIF"));
			this.btnSearchGuarantorCIF.setVisible(false);
			this.guarantorIDNumber.setDisabled(isReadOnly("GuarantorDetailDialog_GuarantorIDNumber"));
			this.guarantorIDNumber.setReadonly(isReadOnly("GuarantorDetailDialog_GuarantorIDNumber"));
			this.guarantorCIFName.setReadonly(isReadOnly("GuarantorDetailDialog_GuarantorCIFName"));
			this.mobileNo.setReadonly(isReadOnly("GuarantorDetailDialog_MobileNo"));
			this.emailId.setReadonly(isReadOnly("GuarantorDetailDialog_EmailId"));
			this.btnUploadGuarantorProof.setVisible(true);
			this.hlayout_GuarantorCIF.setVisible(false);
			this.hlayout_GuarantorIDNumber.setVisible(true);
			this.space_GuarantorIDType.setVisible(true);
			this.space_GuarantorIDNumber.setVisible(true);
			this.space_Name.setVisible(true);
			this.space_MobileNo.setVisible(true);
			this.space_EmailId.setVisible(true);
			this.space_GuarantorProof.setVisible(true);
			this.space_GuarantorCIF.setVisible(true);
			this.space_GuranteePercentage.setVisible(true);
		}
		if (!isNewRecord()) {
			readOnlyExposureFields(true);
		} else {
			readOnlyExposureFields(false);
		}
		logger.debug("Leaving");
	}

	public void onChange$guarantorIDType(Event event){
		logger.debug("Entering" + event.toString());
		this.guarantorIDNumber.setErrorMessage("");
		if(this.guarantorIDNumber.getValue().trim().length() >9){
			this.guarantorIDNumber.setValue("");
		}
		setGurantorIDNumProp();
		logger.debug("Leaving" + event.toString());
	}

	private void setGurantorIDNumProp(){
		if(this.guarantorIDType.getSelectedItem() != null){  
			if(this.guarantorIDType.getSelectedItem().getValue().toString().equals(PennantConstants.CPRCODE)){
				this.guarantorIDNumber.setMaxlength(9);
				this.guarantorIDNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_GuarantorCIF/ID.value"), 
						PennantRegularExpressions.REGEX_NUMERIC_FL9, true));
			}else if(this.guarantorIDType.getSelectedItem().getValue().toString()
					.equals(PennantConstants.BAHRAINI_CR)){
				this.guarantorIDNumber.setMaxlength(10);
				this.guarantorIDNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_GuarantorCIF/ID.value"), 
						PennantRegularExpressions.REGEX_CR, true));
			}else if(this.guarantorIDType.getSelectedItem().getValue().toString()
					.equals(PennantConstants.PASSPORT)){
				this.guarantorIDNumber.setMaxlength(20);
				this.guarantorIDNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_GuarantorCIF/ID.value"), 
						PennantRegularExpressions.REGEX_ALPHANUM, true));
			}else if(this.guarantorIDType.getSelectedItem().getValue().toString()
					.equals(PennantConstants.NON_BAHRAINI_INTERNATIONAL_CR)){
				this.guarantorIDNumber.setMaxlength(10);
				this.guarantorIDNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_GuarantorCIF/ID.value"), 
						PennantRegularExpressions.REGEX_CR, true));
			}else if(this.guarantorIDType.getSelectedItem().getValue().toString()
					.equals(PennantConstants.BAHRAINI_GOVERNMENT_ENTITY)){
				this.guarantorIDNumber.setMaxlength(20);
				this.guarantorIDNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_GuarantorCIF/ID.value"), 
						PennantRegularExpressions.REGEX_ALPHANUM, true));
			}else if(this.guarantorIDType.getSelectedItem().getValue().toString()
					.equals(PennantConstants.NON_BAHRAINI_GOVERNMENT_ENTITY)){
				this.guarantorIDNumber.setMaxlength(20);
				this.guarantorIDNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_GuarantorCIF/ID.value"), 
						PennantRegularExpressions.REGEX_ALPHANUM, true));
			}else if(this.guarantorIDType.getSelectedItem().getValue().toString()
					.equals(PennantConstants.HAFEEZA)){
				this.guarantorIDNumber.setMaxlength(20);
				this.guarantorIDNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_GuarantorCIF/ID.value"), 
						PennantRegularExpressions.REGEX_ALPHANUM, true));
			}else if(this.guarantorIDType.getSelectedItem().getValue().toString()
					.equals(PennantConstants.IQAMA)){
				this.guarantorIDNumber.setMaxlength(20);
				this.guarantorIDNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_GuarantorCIF/ID.value"), 
						PennantRegularExpressions.REGEX_ALPHANUM, true));
			}else if(this.guarantorIDType.getSelectedItem().getValue().toString()
					.equals(PennantConstants.FAMILY_CARD)){
				this.guarantorIDNumber.setMaxlength(20);
				this.guarantorIDNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_GuarantorCIF/ID.value"), 
						PennantRegularExpressions.REGEX_ALPHANUM, true));
			}else if(this.guarantorIDType.getSelectedItem().getValue().toString()
					.equals(PennantConstants.JOINT_CIF)){
				this.guarantorIDNumber.setMaxlength(6);
				this.guarantorIDNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_GuarantorCIF/ID.value"), 
						PennantRegularExpressions.REGEX_ALPHANUM, true));
			}else if(this.guarantorIDType.getSelectedItem().getValue().toString()
					.equals(PennantConstants.NEW)){
				this.guarantorIDNumber.setMaxlength(20);
				this.guarantorIDNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_GuarantorCIF/ID.value"), 
						PennantRegularExpressions.REGEX_ALPHANUM, true));
			}else{
				this.guarantorIDNumber.setMaxlength(20);
			}
		}
	}

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
	 * Deletes a GuarantorDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final GuarantorDetail aGuarantorDetail = new GuarantorDetail();
		BeanUtils.copyProperties(getGuarantorDetail(), aGuarantorDetail);
		String tranType = PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aGuarantorDetail.getGuarantorCIF();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));
		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");
			if (StringUtils.trimToEmpty(aGuarantorDetail.getRecordType()).equals("")) {
				aGuarantorDetail.setVersion(aGuarantorDetail.getVersion() + 1);
				aGuarantorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				aGuarantorDetail.setNewRecord(true);
				if (isWorkFlowEnabled()) {
					aGuarantorDetail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (isNewGuarantor()) {
					tranType = PennantConstants.TRAN_DEL;
					AuditHeader auditHeader = newGuarantorDetailProcess(aGuarantorDetail, tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_GuarantorDetailDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
						getFinanceMainDialogCtrl().doFillGurantorsDetails(this.guarantorDetailDetailList);
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
		// this.finReference.setValue("");
		// this.bankCustomer.setChecked(false);
		this.guarantorCIF.setValue("");
		this.guarantorCIFName.setValue("");
		this.guarantorIDType.setSelectedIndex(0);
		this.guarantorIDNumber.setValue("");
		this.guarantorCIFName.setValue("");
		this.guranteePercentage.setValue("0");
		this.mobileNo.setValue("");
		this.emailId.setValue("");
		this.guarantorProof.setValue("");
		this.guarantorProofName.setValue("");
		this.remarks.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final GuarantorDetail aGuarantorDetail = new GuarantorDetail();
		BeanUtils.copyProperties(getGuarantorDetail(), aGuarantorDetail);
		boolean isNew = false;
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the DocumentDetails object with the components data
		doWriteComponentsToBean(aGuarantorDetail);
		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here
		isNew = aGuarantorDetail.isNew();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aGuarantorDetail.getRecordType()).equals("")) {
				aGuarantorDetail.setVersion(aGuarantorDetail.getVersion() + 1);
				if (isNew) {
					aGuarantorDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aGuarantorDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aGuarantorDetail.setNewRecord(true);
				}
			}
		} else {
			if (isNewGuarantor()) {
				if (isNewRecord()) {
					aGuarantorDetail.setVersion(1);
					aGuarantorDetail.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
				if (StringUtils.trimToEmpty(aGuarantorDetail.getRecordType()).equals("")) {
					aGuarantorDetail.setVersion(aGuarantorDetail.getVersion() + 1);
					aGuarantorDetail.setRecordType(PennantConstants.RCD_UPD);
				}
				if (aGuarantorDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aGuarantorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}
			} else {
				aGuarantorDetail.setVersion(aGuarantorDetail.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}
		// save it to database
		try {
			if (isNewGuarantor()) {
				AuditHeader auditHeader = newGuarantorDetailProcess(aGuarantorDetail, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_GuarantorDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getFinanceMainDialogCtrl().doFillGurantorsDetails(this.guarantorDetailDetailList);
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
	private AuditHeader newGuarantorDetailProcess(GuarantorDetail aGuarantorDetail, String tranType) {
		boolean recordAdded = false;
		AuditHeader auditHeader = getAuditHeader(aGuarantorDetail, tranType);
		guarantorDetailDetailList = new ArrayList<GuarantorDetail>();
		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		valueParm[0] = aGuarantorDetail.getFinReference();

		boolean dupicateRecord = false;

		if(aGuarantorDetail.isBankCustomer()) {		
			valueParm[1] = aGuarantorDetail.getGuarantorCIF();
		} else {
			valueParm[1] = aGuarantorDetail.getGuarantorIDNumber();
		}

		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_GuarantorCIF") + ":" + valueParm[1];
		// Checks whether jointAccount custCIF is same as actual custCIF
		if (StringUtils.isNotBlank(aGuarantorDetail.getGuarantorCIF()) && StringUtils.trimToEmpty(primaryCustId).equals(aGuarantorDetail.getGuarantorCIF())) {
			auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), getUserWorkspace().getUserLanguage()));
		}

		List<GuarantorDetail> guarantorDetailList = getFinanceMainDialogCtrl().getGuarantorDetailList();
		if (guarantorDetailList != null && !guarantorDetailList.isEmpty()) {
			for (GuarantorDetail guarantorDetail : guarantorDetailList) {
				if (aGuarantorDetail.isBankCustomer()) {
					if (guarantorDetail.getGuarantorCIF().equals(aGuarantorDetail.getGuarantorCIF())) {
						dupicateRecord = true;
					}
				} else if (guarantorDetail.getGuarantorIDNumber().equals(aGuarantorDetail.getGuarantorIDNumber())) {
					dupicateRecord = true;
				}

				if (dupicateRecord) { 
					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					if (tranType == PennantConstants.TRAN_DEL) {
						if (aGuarantorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aGuarantorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							guarantorDetailDetailList.add(aGuarantorDetail);
						} else if (aGuarantorDetail.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aGuarantorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aGuarantorDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							guarantorDetailDetailList.add(aGuarantorDetail);
						} else if (aGuarantorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
						}
					} else {
						guarantorDetailDetailList.add(guarantorDetail);
					}
				} else {
					guarantorDetailDetailList.add(guarantorDetail);
				}
				dupicateRecord = false;
			}
		}
		if (tranType == PennantConstants.TRAN_UPD) {
			this.guarantorDetailDetailList.remove(index);
			this.guarantorDetailDetailList.add(guarantorDetail);
			recordAdded = true;
		}
		if (!recordAdded) {
			guarantorDetailDetailList.add(aGuarantorDetail);
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
	private AuditHeader getAuditHeader(GuarantorDetail aGuarantorDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aGuarantorDetail.getBefImage(), aGuarantorDetail);
		return new AuditHeader(String.valueOf(aGuarantorDetail.getGuarantorId()), null, null, null, auditDetail, aGuarantorDetail.getUserDetails(), getOverideMap());
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

	private void setVisibleGrid() {
		if (this.primaryList==null || this.primaryList.size()==0) {
			this.gb_GurantorsPrimaryExposure.setVisible(false);
		} else {
			this.gb_GurantorsPrimaryExposure.setVisible(true);
		}

		if (this.secoundaryList==null || this.secoundaryList.size()==0) {
			this.gb_GurantorsSecoundaryExposure.setVisible(false);
		} else {
			this.gb_GurantorsSecoundaryExposure.setVisible(true);
		}

		if (this.guarantorList==null || this.guarantorList.size()==0) {
			this.gb_GurantorsExposure.setVisible(false);
		} else {
			this.gb_GurantorsExposure.setVisible(true);
		}

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public GuarantorDetail getGuarantorDetail() {
		return this.guarantorDetail;
	}

	public void setGuarantorDetail(GuarantorDetail guarantorDetail) {
		this.guarantorDetail = guarantorDetail;
	}

	public void setGuarantorDetailService(GuarantorDetailService guarantorDetailService) {
		this.guarantorDetailService = guarantorDetailService;
	}

	public GuarantorDetailService getGuarantorDetailService() {
		return this.guarantorDetailService;
	}

	public void setGuarantorDetailListCtrl(GuarantorDetailListCtrl guarantorDetailListCtrl) {
		this.guarantorDetailListCtrl = guarantorDetailListCtrl;
	}

	public GuarantorDetailListCtrl getGuarantorDetailListCtrl() {
		return this.guarantorDetailListCtrl;
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

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public JointAccountDetailDialogCtrl getFinanceMainDialogCtrl() {
		return finJointAccountCtrl;
	}

	public void setFinanceMainDialogCtrl(JointAccountDetailDialogCtrl finJointAccountCtrl) {
		this.finJointAccountCtrl = finJointAccountCtrl;
	}

	public boolean isNewGuarantor() {
		return newGuarantor;
	}

	public void setNewGuarantor(boolean newGuarantor) {
		this.newGuarantor = newGuarantor;
	}

	public List<GuarantorDetail> getGuarantorDetailDetailList() {
		return guarantorDetailDetailList;
	}

	public void setGuarantorDetailDetailList(List<GuarantorDetail> guarantorDetailDetailList) {
		this.guarantorDetailDetailList = guarantorDetailDetailList;
	}	
}

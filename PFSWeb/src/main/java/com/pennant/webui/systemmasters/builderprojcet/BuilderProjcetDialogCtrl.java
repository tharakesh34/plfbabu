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
 * * FileName : BuilderProjcetDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-05-2017 * *
 * Modified Date : 22-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.systemmasters.builderprojcet;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.North;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.systemmasters.BuilderCompany;
import com.pennant.backend.model.systemmasters.BuilderProjcet;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.ProjectUnits;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.systemmasters.BuilderProjcetService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/masters/BuilderProjcet/builderProjcetDialog.zul file. <br>
 */
public class BuilderProjcetDialogCtrl extends GFCBaseCtrl<BuilderProjcet> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(BuilderProjcetDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_BuilderProjcetDialog;
	// protected Longbox id;
	protected Uppercasebox name;
	protected ExtendedCombobox builderId;
	protected Textbox apfNo;
	private BuilderProjcet builderProjcet; // overhanded per param

	private transient BuilderProjcetListCtrl builderprojcetListCtrl; // overhanded per param
	private transient BuilderProjcetService builderProjcetService;
	protected North north; // autowired
	protected South south; // autowired
	protected Groupbox gb_keyDetails; // autowired
	protected Groupbox gp_BuilderProjectDetails; // autowired
	protected Tabs tabsIndexCenter;
	protected Tab tabProjectDetails;
	protected Tab tabProjectUnits;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Tabpanel tp_ProjectDetails;
	protected Tabpanel tp_ProjectUnits;
	protected Div divKeyDetails;
	// Project Details
	protected Textbox registrationNumber;
	protected Textbox addressLine1;
	protected Textbox addressLine2;
	protected Textbox addressLine3;
	protected Textbox landmark;
	protected Textbox areaOrLocality;
	protected ExtendedCombobox city;
	protected ExtendedCombobox state;
	protected ExtendedCombobox pinCode;
	protected Combobox projectType;
	protected Combobox typesOfApf;
	protected Intbox totalUnits;
	protected Intbox numberOfTowers;
	protected Intbox noOfIndependentHouses;
	protected Datebox projectStartDate;
	protected Datebox projectEndDate;
	protected Textbox remarks;
	protected Textbox commencementCertificateNo;
	protected Textbox commencecrtfctissuingauthority;
	protected Intbox totalPlotArea;
	protected Intbox constructedArea;
	protected Combobox technicalDone;
	protected Combobox legalDone;
	protected Combobox rcuDone;
	protected Decimalbox constrctincompletionpercentage;
	protected Decimalbox disbursalRecommendedPercentage;
	protected Textbox beneficiaryName;
	protected ExtendedCombobox bankBranchID;
	protected Textbox bankName;
	protected Textbox branch;
	protected Textbox accountNo;
	protected Space space_BeneficiaryName;
	protected Space space_AccountNo;
	protected Textbox branchBankName;
	protected Textbox branchDesc;

	private transient BankDetailService bankDetailService;
	private transient BankBranchService bankBranchService;

	protected String selectMethodName = "onSelectTab";
	protected Button btnNewProjectUnits;
	protected Listbox listBoxProjectUnits;
	private transient DocumentDetailDialogCtrl documentDetailDialogCtrl;
	private transient FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl;
	private static List<ValueLabel> projectTypeList = PennantStaticListUtil.getProjectType();
	private List<ProjectUnits> projectUnitsList = new ArrayList<ProjectUnits>();
	private static List<ValueLabel> apfTypesList = PennantStaticListUtil.getApfTypes();
	private static List<ValueLabel> technicalDoneList = PennantStaticListUtil.getTechnicalDone();
	private static List<ValueLabel> legalDoneList = PennantStaticListUtil.getLegalDone();
	private static List<ValueLabel> rcuDoneList = PennantStaticListUtil.getRCUDone();

	private String apfSequence;

	/**
	 * default constructor.<br>
	 */
	public BuilderProjcetDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "BuilderProjcetDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.builderProjcet.getId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_BuilderProjcetDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_BuilderProjcetDialog);

		try {
			// Get the required arguments.
			this.builderProjcet = (BuilderProjcet) arguments.get("builderprojcet");
			this.builderprojcetListCtrl = (BuilderProjcetListCtrl) arguments.get("builderprojcetListCtrl");

			if (this.builderProjcet == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			BuilderProjcet builderProjcet = new BuilderProjcet();
			BeanUtils.copyProperties(this.builderProjcet, builderProjcet);
			this.builderProjcet.setBefImage(builderProjcet);

			// Render the page and display the data.
			doLoadWorkFlow(this.builderProjcet.isWorkflow(), this.builderProjcet.getWorkflowId(),
					this.builderProjcet.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}
			// Generating the APF Sequence
			if (StringUtils.isEmpty(builderProjcet.getApfNo())) {
				apfSequence = ReferenceGenerator.generateAPFSequence();
			} else {
				String apfSeq = builderProjcet.getApfNo();
				apfSequence = apfSeq.substring(apfSeq.length() - 5);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.builderProjcet);
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

		this.name.setMaxlength(50);
		this.builderId.setModuleName("BuilderCompany");
		this.builderId.setValueColumn("Name");
		this.builderId.setDescColumn("Segmentation");
		this.builderId.setValidateColumns(new String[] { "Name" });
		this.builderId.setMandatoryStyle(true);
		this.apfNo.setMaxlength(14);
		this.apfNo.setReadonly(true);

		// state
		this.state.setModuleName("Province");
		this.state.setValueColumn("CPProvince");
		this.state.setDescColumn("CPProvinceName");
		this.state.setValidateColumns(new String[] { "CPProvince" });
		this.state.setMandatoryStyle(false);
		// City
		this.city.setModuleName("City");
		this.city.setValueColumn("PCCity");
		this.city.setDescColumn("PCCityName");
		this.city.setValidateColumns(new String[] { "PCCity" });
		this.city.setMandatoryStyle(false);
		// Pincode
		this.pinCode.setModuleName("PinCode");
		this.pinCode.setValueColumn("PinCode");
		this.pinCode.setDescColumn("AreaName");
		this.pinCode.setValidateColumns(new String[] { "PinCode" });
		this.pinCode.setMandatoryStyle(false);
		this.projectStartDate.setFormat(PennantConstants.dateFormat);
		this.projectStartDate.setConstraint("no future");
		this.projectEndDate.setFormat(PennantConstants.dateFormat);

		this.remarks.setMaxlength(150);
		this.commencementCertificateNo.setMaxlength(50);
		this.commencecrtfctissuingauthority.setMaxlength(100);
		this.constrctincompletionpercentage.setMaxlength(6);
		this.disbursalRecommendedPercentage.setMaxlength(6);
		this.beneficiaryName.setMaxlength(500);

		this.bankBranchID.setModuleName("BankBranch");
		this.bankBranchID.setMandatoryStyle(false);
		this.bankBranchID.setValueColumn("IFSC");
		this.bankBranchID.setDescColumn("");
		this.bankBranchID.setDisplayStyle(2);
		this.bankBranchID.setValidateColumns(new String[] { "IFSC" });

		this.addressLine1.setMaxlength(50);
		this.addressLine2.setMaxlength(50);
		this.addressLine3.setMaxlength(50);
		this.registrationNumber.setMaxlength(50);
		this.landmark.setMaxlength(20);
		this.areaOrLocality.setMaxlength(20);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_BuilderProjcetDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_BuilderProjcetDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_BuilderProjcetDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_BuilderProjcetDialog_btnSave"));
		this.btnNewProjectUnits
				.setVisible(getUserWorkspace().isAllowed("button_BuilderProjcetDialog_btnNewProjectUnits"));
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
		doShowNotes(this.builderProjcet);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		builderprojcetListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.builderProjcet.getBefImage());
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
	public void doWriteBeanToComponents(BuilderProjcet aBuilderProjcet) {
		logger.debug(Literal.ENTERING);

		this.name.setValue(aBuilderProjcet.getName());
		this.apfNo.setValue(aBuilderProjcet.getApfNo());
		this.builderId.setDescription(aBuilderProjcet.getSegmentation());

		if (aBuilderProjcet.isNewRecord()) {
			this.builderId.setDescription("");
		} else {
			this.builderId.setValue(String.valueOf(aBuilderProjcet.getbuilderIdName()));
			this.builderId.setAttribute("builderId", aBuilderProjcet.getBuilderId());
		}
		this.recordStatus.setValue(aBuilderProjcet.getRecordStatus());
		// Project Details
		this.registrationNumber.setValue(aBuilderProjcet.getRegistrationNumber());

		this.addressLine1.setValue(aBuilderProjcet.getAddressLine1());

		this.addressLine2.setValue(aBuilderProjcet.getAddressLine2());

		this.addressLine3.setValue(aBuilderProjcet.getAddressLine3());

		this.landmark.setValue(aBuilderProjcet.getLandmark());

		this.areaOrLocality.setValue(aBuilderProjcet.getAreaOrLocality());

		this.city.setValue(aBuilderProjcet.getCity());

		this.state.setValue(aBuilderProjcet.getState());

		this.pinCode.setValue(aBuilderProjcet.getPinCode());

		fillComboBox(this.projectType, aBuilderProjcet.getProjectType(), projectTypeList, "");

		fillComboBox(this.typesOfApf, aBuilderProjcet.getTypesOfApf(), apfTypesList, "");

		this.totalUnits.setValue(aBuilderProjcet.getTotalUnits());

		this.numberOfTowers.setValue(aBuilderProjcet.getNumberOfTowers());

		this.noOfIndependentHouses.setValue(aBuilderProjcet.getNoOfIndependentHouses());

		this.projectStartDate.setValue(aBuilderProjcet.getProjectStartDate());

		this.projectEndDate.setValue(aBuilderProjcet.getProjectEndDate());

		this.remarks.setValue(aBuilderProjcet.getRemarks());

		this.commencementCertificateNo.setValue(aBuilderProjcet.getCommencementCertificateNo());

		this.commencecrtfctissuingauthority.setValue(aBuilderProjcet.getCommencecrtfctissuingauthority());

		this.totalPlotArea.setValue(aBuilderProjcet.getTotalPlotArea());

		this.constructedArea.setValue(aBuilderProjcet.getConstructedArea());

		fillComboBox(this.technicalDone, aBuilderProjcet.getTechnicalDone(), technicalDoneList, "");

		fillComboBox(this.legalDone, aBuilderProjcet.getLegalDone(), legalDoneList, "");

		fillComboBox(this.rcuDone, aBuilderProjcet.getRcuDone(), rcuDoneList, "");

		this.constrctincompletionpercentage.setValue(aBuilderProjcet.getConstrctincompletionpercentage());

		this.disbursalRecommendedPercentage.setValue(aBuilderProjcet.getDisbursalRecommendedPercentage());

		if (aBuilderProjcet.getBankBranchID() != Long.MIN_VALUE && aBuilderProjcet.getBankBranchID() != 0) {
			this.bankBranchID.setAttribute("bankBranchID", aBuilderProjcet.getBankBranchID());
			this.bankBranchID.setValue(StringUtils.trimToEmpty(aBuilderProjcet.getiFSC()));
			int accNoLength = getBankBranchService().getAccNoLengthByIFSC(this.bankBranchID.getValue());
			if (accNoLength != 0) {
				this.accountNo.setMaxlength(accNoLength);
			} else {
				this.accountNo.setMaxlength(LengthConstants.LEN_ACCOUNT);
			}

		}
		if (this.bankBranchID.getValue() != null && StringUtils.isNotEmpty(this.bankBranchID.getValue())) {
			this.space_BeneficiaryName.setSclass(PennantConstants.mandateSclass);
			this.space_AccountNo.setSclass(PennantConstants.mandateSclass);
		}

		this.bankName.setValue(aBuilderProjcet.getBranchBankName());

		this.branch.setValue(aBuilderProjcet.getBranchDesc());

		this.beneficiaryName.setValue(aBuilderProjcet.getBeneficiaryName());

		this.accountNo.setValue(aBuilderProjcet.getAccountNo());

		if (aBuilderProjcet.getAddressLine1() == null) {
			this.state.setDescription("");
			this.city.setDescription("");
			this.pinCode.setDescription("");
		} else {
			this.state.setDescription(aBuilderProjcet.getState());
			this.city.setDescription(aBuilderProjcet.getCity());
			this.pinCode.setDescription(aBuilderProjcet.getPinCode());
		}

		appendDocumentDetailTab();
		doRenderProjectUnits(aBuilderProjcet.getProjectUnits());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aBuilderProjcet
	 */
	public void doWriteComponentsToBean(BuilderProjcet aBuilderProjcet) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		/*
		 * //Project ID try { aBuilderProjcet.setId(this.id.getValue()); }catch (WrongValueException we ) { wve.add(we);
		 * }
		 */
		// Name
		try {
			aBuilderProjcet.setName(this.name.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Company
		try {
			String validatedValue = this.builderId.getValidatedValue();

			if (validatedValue != null) {
				Object object = this.builderId.getAttribute("builderId");

				if (object != null) {
					aBuilderProjcet.setBuilderId(Long.parseLong(object.toString()));

				} else {
					aBuilderProjcet.setBuilderId(0);
				}
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		// APF No
		try {
			aBuilderProjcet.setApfNo(this.apfNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Project Details
		try {
			aBuilderProjcet.setRegistrationNumber(this.registrationNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderProjcet.setAddressLine1(this.addressLine1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderProjcet.setAddressLine2(this.addressLine2.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderProjcet.setAddressLine3(this.addressLine3.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderProjcet.setLandmark(this.landmark.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderProjcet.setAreaOrLocality(this.areaOrLocality.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderProjcet.setCity(StringUtils.trimToNull(this.city.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderProjcet.setState(this.state.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderProjcet.setPinCode(this.pinCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderProjcet.setProjectType(this.projectType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderProjcet.setTypesOfApf(this.typesOfApf.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBuilderProjcet.setTotalUnits(this.totalUnits.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBuilderProjcet.setNumberOfTowers(this.numberOfTowers.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderProjcet.setNoOfIndependentHouses(this.noOfIndependentHouses.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderProjcet.setProjectStartDate(this.projectStartDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderProjcet.setProjectEndDate(this.projectEndDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBuilderProjcet.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBuilderProjcet.setCommencementCertificateNo(this.commencementCertificateNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBuilderProjcet.setCommencecrtfctissuingauthority(this.commencecrtfctissuingauthority.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBuilderProjcet.setTotalPlotArea(this.totalPlotArea.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBuilderProjcet.setConstructedArea(this.constructedArea.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBuilderProjcet.setTechnicalDone(this.technicalDone.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBuilderProjcet.setLegalDone(this.legalDone.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBuilderProjcet.setRcuDone(this.rcuDone.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBuilderProjcet.setDisbursalRecommendedPercentage(this.disbursalRecommendedPercentage.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBuilderProjcet.setConstrctincompletionpercentage(this.constrctincompletionpercentage.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBuilderProjcet.setBeneficiaryName(this.beneficiaryName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderProjcet.setBranchBankName(this.bankName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderProjcet.setBranchDesc(this.branch.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBuilderProjcet.setAccountNo(this.accountNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.bankBranchID.getValidatedValue();
			Object object = this.bankBranchID.getAttribute("bankBranchID");
			if (object != null) {
				aBuilderProjcet.setBankBranchID((Long.parseLong(object.toString())));
			} else {
				aBuilderProjcet.setBankBranchID(0);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Project Units Saving
		if (projectUnitsList != null) {
			aBuilderProjcet.setProjectUnits(projectUnitsList);
		} else {
			aBuilderProjcet.setProjectUnits(this.builderProjcet.getProjectUnits());
		}

		// Document Details Saving
		if (documentDetailDialogCtrl != null) {
			aBuilderProjcet.setDocumentDetails(documentDetailDialogCtrl.getDocumentDetailsList());
		} else {
			aBuilderProjcet.setDocumentDetails(this.builderProjcet.getDocumentDetails());
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
	public void doShowDialog(BuilderProjcet builderProjcet) {
		logger.debug(Literal.LEAVING);

		if (builderProjcet.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.name.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(builderProjcet.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.name.focus();
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
			this.btnNewProjectUnits.setVisible(false);
		}

		doWriteBeanToComponents(builderProjcet);
		getBorderLayoutHeight();
		this.listBoxProjectUnits.setHeight(this.borderLayoutHeight - 210 + "px");
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.name.isReadonly()) {
			this.name.setConstraint(new PTStringValidator(Labels.getLabel("label_BuilderProjcetDialog_name.value"),
					PennantRegularExpressions.REGEX_ACC_HOLDER_NAME, true, 5, 50));
		}
		if (!this.builderId.isReadonly()) {
			this.builderId.setConstraint(new PTStringValidator(
					Labels.getLabel("label_BuilderProjcetDialog_builderId.value"), null, true, true));
		}
		if (!this.registrationNumber.isReadonly()) {
			this.registrationNumber.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BuilderProjectDialog_registrationNumber.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_FSLASH_SPACE, false, false));
		}
		if (!this.addressLine1.isReadonly()) {
			this.addressLine1.setConstraint(new PTStringValidator(
					Labels.getLabel("label_BuilderProjectDialog_addressLine1.value"), null, true, false));
		}
		if (!this.addressLine2.isReadonly()) {
			this.addressLine2.setConstraint(new PTStringValidator(
					Labels.getLabel("label_BuilderProjectDialog_addressLine2.value"), null, true, false));
		}
		if (!this.city.isReadonly()) {
			this.city.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BuilderProjectDialog_City.value"), null, false, true));
		}
		if (!this.state.isReadonly()) {
			this.state.setConstraint(new PTStringValidator(Labels.getLabel("label_BuilderProjectDialog_State.value"),
					null, false, true));
		}
		if (!this.pinCode.isReadonly()) {
			this.pinCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BuilderProjectDialog_Code.value"), null, false, true));
		}

		if (!this.totalUnits.isReadonly()) {
			this.totalUnits.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_BuilderProjectDialog_TotalUnits.value"), true, false));
		}

		if (!this.numberOfTowers.isReadonly()) {
			this.numberOfTowers.setConstraint(new PTStringValidator(
					Labels.getLabel("label_BuilderProjectDialog_NumberOfTowers.value"), null, false, false));
		}

		if (!this.noOfIndependentHouses.isReadonly()) {
			this.noOfIndependentHouses.setConstraint(new PTStringValidator(
					Labels.getLabel("label_BuilderProjectDialog_NoOfIndependentHouses.value"), null, false, false));
		}

		if (!this.projectStartDate.isReadonly()) {
			this.projectStartDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_BuilderProjectDialog_ProjectStartDate.value"), false,
							null, SysParamUtil.getAppDate(), true));
		}

		if (!this.projectEndDate.isReadonly()) {
			this.projectEndDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_BuilderProjectDialog_ProjectEndDate.value"), false,
							this.projectStartDate.getValue(), null, true));
		}

		if (!this.bankBranchID.isReadonly()) {
			this.bankBranchID.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BuilderProjectDialog_IFSC.value"), null, false));

		}

		if (StringUtils.isNotEmpty(this.bankBranchID.getValue())) {
			this.beneficiaryName.setConstraint(new PTStringValidator(
					Labels.getLabel("label_BuilderProjectDialog__BeneficiaryName.value"), null, true));
			this.accountNo.setConstraint(
					new PTStringValidator(Labels.getLabel("label__BuilderProjectDialog_AccountNo.value"), null, true));
		}

		if (!this.accountNo.isReadonly() && !StringUtils.isEmpty(this.accountNo.getValue())) {
			if (this.accountNo.getMaxlength() != this.accountNo.getValue().length()) {
				throw new WrongValueException(this.accountNo,
						Labels.getLabel("NUMBER_EQ_LENGTH",
								new String[] { Labels.getLabel("label__BuilderProjectDialog_AccountNo.value"),
										String.valueOf(this.accountNo.getMaxlength()) }));

			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.name.setConstraint("");
		this.builderId.setConstraint("");
		this.apfNo.setConstraint("");
		this.registrationNumber.setConstraint("");
		this.addressLine1.setConstraint("");
		this.addressLine2.setConstraint("");
		this.addressLine3.setConstraint("");
		this.landmark.setConstraint("");
		this.areaOrLocality.setConstraint("");
		this.city.setConstraint("");
		this.state.setConstraint("");
		this.pinCode.setConstraint("");
		this.projectType.setConstraint("");
		this.typesOfApf.setConstraint("");
		this.totalUnits.setConstraint("");
		this.numberOfTowers.setConstraint("");
		this.noOfIndependentHouses.setConstraint("");
		this.projectStartDate.setConstraint("");
		this.projectEndDate.setConstraint("");
		this.remarks.setConstraint("");
		this.commencementCertificateNo.setConstraint("");
		this.commencecrtfctissuingauthority.setConstraint("");
		this.totalPlotArea.setConstraint("");
		this.constructedArea.setConstraint("");
		this.technicalDone.setConstraint("");
		this.legalDone.setConstraint("");
		this.rcuDone.setConstraint("");
		this.constrctincompletionpercentage.setConstraint("");
		this.disbursalRecommendedPercentage.setConstraint("");
		this.beneficiaryName.setConstraint("");
		this.accountNo.setConstraint("");
		this.bankBranchID.setConstraint("");
		this.bankName.setConstraint("");
		this.branch.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		// Project ID
		// Name
		// Company
		// APF No

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.LEAVING);
		this.name.setConstraint("");
		this.builderId.setConstraint("");
		this.apfNo.setConstraint("");
		this.registrationNumber.setConstraint("");
		this.addressLine1.setConstraint("");
		this.addressLine2.setConstraint("");
		this.city.setConstraint("");
		this.state.setConstraint("");
		this.pinCode.setConstraint("");
		this.projectType.setConstraint("");
		this.typesOfApf.setConstraint("");
		this.totalUnits.setConstraint("");
		this.numberOfTowers.setConstraint("");
		this.noOfIndependentHouses.setConstraint("");
		this.projectStartDate.setConstraint("");
		this.projectEndDate.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);
		this.name.setErrorMessage("");
		this.builderId.setErrorMessage("");
		this.apfNo.setErrorMessage("");
		this.registrationNumber.setErrorMessage("");
		this.addressLine1.setErrorMessage("");
		this.addressLine2.setErrorMessage("");
		this.addressLine3.setErrorMessage("");
		this.landmark.setErrorMessage("");
		this.areaOrLocality.setErrorMessage("");
		this.city.setErrorMessage("");
		this.state.setErrorMessage("");
		this.pinCode.setErrorMessage("");
		this.projectType.setErrorMessage("");
		this.typesOfApf.setErrorMessage("");
		this.totalUnits.setErrorMessage("");
		this.numberOfTowers.setErrorMessage("");
		this.noOfIndependentHouses.setErrorMessage("");
		this.projectStartDate.setErrorMessage("");
		this.projectEndDate.setErrorMessage("");
		this.remarks.setErrorMessage("");
		this.commencementCertificateNo.setErrorMessage("");
		this.commencecrtfctissuingauthority.setErrorMessage("");
		this.totalPlotArea.setErrorMessage("");
		this.constructedArea.setErrorMessage("");
		this.technicalDone.setErrorMessage("");
		this.legalDone.setErrorMessage("");
		this.rcuDone.setErrorMessage("");
		this.constrctincompletionpercentage.setErrorMessage("");
		this.disbursalRecommendedPercentage.setErrorMessage("");
		this.beneficiaryName.setErrorMessage("");
		this.accountNo.setErrorMessage("");
		this.bankName.setErrorMessage("");
		this.branch.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final BuilderProjcet aBuilderProjcet = new BuilderProjcet();
		BeanUtils.copyProperties(this.builderProjcet, aBuilderProjcet);

		doDelete(String.valueOf(aBuilderProjcet.getId()), aBuilderProjcet);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.builderProjcet.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);

		}

		readOnlyComponent(isReadOnly("BuilderProjcetDialog_name"), this.name);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_builderId"), this.builderId);
		// OnlyComponent(isReadOnly("BuilderProjcetDialog_apfNo"), this.apfNo);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_registrationNumber"), this.registrationNumber);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_addressLine1"), this.addressLine1);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_addressLine2"), this.addressLine2);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_addressLine3"), this.addressLine3);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_landmark"), this.landmark);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_areaOrLocality"), this.areaOrLocality);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_city"), this.city);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_state"), this.state);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_pinCode"), this.pinCode);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_projectType"), this.projectType);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_typesOfApf"), this.typesOfApf);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_totalUnits"), this.totalUnits);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_numberOfTowers"), this.numberOfTowers);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_noOfIndependentHouses"), this.noOfIndependentHouses);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_projectStartDate"), this.projectStartDate);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_projectEndDate"), this.projectEndDate);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_remarks"), this.remarks);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_commencementCertificateNo"), this.commencementCertificateNo);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_commencementCertificateIssuingAuthority"),
				this.commencecrtfctissuingauthority);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_TotalPlotArea"), this.totalPlotArea);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_ConstructedArea"), this.constructedArea);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_TechnicalDone"), this.technicalDone);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_LegalDone"), this.legalDone);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_RCUDone"), this.rcuDone);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_ConstructionCompletionPercentage"),
				this.constrctincompletionpercentage);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_DisbursalRecommendedPercentage"),
				this.disbursalRecommendedPercentage);

		readOnlyComponent(isReadOnly("BuilderProjcetDialog_BeneficiaryName"), this.beneficiaryName);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_IFSC"), this.bankBranchID);
		readOnlyComponent(isReadOnly("BuilderProjcetDialog_AccountNo"), this.accountNo);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.builderProjcet.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);

		readOnlyComponent(true, this.name);
		readOnlyComponent(true, this.builderId);
		readOnlyComponent(true, this.apfNo);
		readOnlyComponent(true, this.registrationNumber);
		readOnlyComponent(true, this.addressLine1);
		readOnlyComponent(true, this.addressLine2);
		readOnlyComponent(true, this.addressLine3);
		readOnlyComponent(true, this.landmark);
		readOnlyComponent(true, this.areaOrLocality);
		readOnlyComponent(true, this.city);
		readOnlyComponent(true, this.state);
		readOnlyComponent(true, this.pinCode);
		readOnlyComponent(true, this.projectType);
		readOnlyComponent(true, this.typesOfApf);
		readOnlyComponent(true, this.totalUnits);
		readOnlyComponent(true, this.numberOfTowers);
		readOnlyComponent(true, this.noOfIndependentHouses);
		readOnlyComponent(true, this.projectStartDate);
		readOnlyComponent(true, this.projectEndDate);
		readOnlyComponent(true, this.remarks);
		readOnlyComponent(true, this.commencementCertificateNo);
		readOnlyComponent(true, this.commencecrtfctissuingauthority);
		readOnlyComponent(true, this.totalPlotArea);
		readOnlyComponent(true, this.constructedArea);
		readOnlyComponent(true, this.technicalDone);
		readOnlyComponent(true, this.legalDone);
		readOnlyComponent(true, this.rcuDone);
		readOnlyComponent(true, this.constrctincompletionpercentage);
		readOnlyComponent(true, this.disbursalRecommendedPercentage);

		this.beneficiaryName.setReadonly(true);
		readOnlyComponent(true, this.bankName);
		readOnlyComponent(true, this.bankBranchID);
		readOnlyComponent(true, this.branch);
		readOnlyComponent(true, this.accountNo);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		this.name.setValue("");
		this.builderId.setValue("");
		this.builderId.setDescription("");
		this.apfNo.setValue("");
		this.registrationNumber.setValue("");
		this.addressLine1.setValue("");
		this.addressLine2.setValue("");
		this.addressLine3.setValue("");
		this.landmark.setValue("");
		this.areaOrLocality.setValue("");
		this.city.setValue("");
		this.city.setDescription("");
		this.state.setValue("");
		this.state.setDescription("");
		this.pinCode.setValue("");
		this.pinCode.setDescription("");
		this.projectType.setValue("");
		this.typesOfApf.setValue("");
		this.remarks.setValue("");
		this.commencementCertificateNo.setValue("");
		this.commencecrtfctissuingauthority.setValue("");
		this.technicalDone.setValue("");
		this.rcuDone.setValue("");
		this.legalDone.setValue("");
		this.constrctincompletionpercentage.setValue("");
		this.disbursalRecommendedPercentage.setValue("");
		this.beneficiaryName.setValue("");
		this.bankName.setValue("");
		this.branch.setValue("");
		this.bankBranchID.setValue("");
		this.accountNo.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final BuilderProjcet aBuilderProjcet = new BuilderProjcet();
		BeanUtils.copyProperties(this.builderProjcet, aBuilderProjcet);
		boolean isNew = false;

		doSetValidation();

		generateApfNo();
		doWriteComponentsToBean(aBuilderProjcet);

		isNew = aBuilderProjcet.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aBuilderProjcet.getRecordType())) {
				aBuilderProjcet.setVersion(aBuilderProjcet.getVersion() + 1);
				if (isNew) {
					aBuilderProjcet.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aBuilderProjcet.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aBuilderProjcet.setNewRecord(true);
				}
			}
		} else {
			aBuilderProjcet.setVersion(aBuilderProjcet.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aBuilderProjcet, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
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
	protected boolean doProcess(BuilderProjcet aBuilderProjcet, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aBuilderProjcet.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aBuilderProjcet.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aBuilderProjcet.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aBuilderProjcet.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aBuilderProjcet.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aBuilderProjcet);
				}

				if (isNotesMandatory(taskId, aBuilderProjcet)) {
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

			aBuilderProjcet.setTaskId(taskId);
			aBuilderProjcet.setNextTaskId(nextTaskId);
			aBuilderProjcet.setRoleCode(getRole());
			aBuilderProjcet.setNextRoleCode(nextRoleCode);
			// Document Details
			if (aBuilderProjcet.getDocumentDetails() != null && !aBuilderProjcet.getDocumentDetails().isEmpty()) {
				for (DocumentDetails details : aBuilderProjcet.getDocumentDetails()) {
					details.setReferenceId(String.valueOf(aBuilderProjcet.getId()));
					details.setDocModule(PennantConstants.MODULE_NAME);
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setRecordStatus(aBuilderProjcet.getRecordStatus());
					details.setWorkflowId(aBuilderProjcet.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(aBuilderProjcet.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(aBuilderProjcet.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}

			// Project Unit Details
			if (!CollectionUtils.isEmpty(aBuilderProjcet.getProjectUnits())) {
				for (ProjectUnits projectUnits : aBuilderProjcet.getProjectUnits()) {
					projectUnits.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					projectUnits.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					projectUnits.setRecordStatus(aBuilderProjcet.getRecordStatus());
					projectUnits.setWorkflowId(aBuilderProjcet.getWorkflowId());
					projectUnits.setTaskId(taskId);
					projectUnits.setNextTaskId(nextTaskId);
					projectUnits.setRoleCode(getRole());
					projectUnits.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(aBuilderProjcet.getRecordType())) {
						if (StringUtils.trimToNull(projectUnits.getRecordType()) == null) {
							projectUnits.setRecordType(aBuilderProjcet.getRecordType());
							projectUnits.setNewRecord(true);
						}
					}
				}
			}
			auditHeader = getAuditHeader(aBuilderProjcet, tranType);
			String operationRefs = getServiceOperations(taskId, aBuilderProjcet);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aBuilderProjcet, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aBuilderProjcet, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		BuilderProjcet aBuilderProjcet = (BuilderProjcet) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = builderProjcetService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = builderProjcetService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = builderProjcetService.doApprove(auditHeader);

					if (aBuilderProjcet.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = builderProjcetService.doReject(auditHeader);
					if (aBuilderProjcet.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_BuilderProjcetDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_BuilderProjcetDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.builderProjcet), true);
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

		logger.debug("Leaving");
		return processCompleted;
	}

	public void onFulfill$builderId(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		Object dataObject = builderId.getObject();

		if (dataObject instanceof String) {
			this.builderId.setValue(dataObject.toString());
			this.builderId.setDescription("");
		} else {
			BuilderCompany builderCompany = (BuilderCompany) dataObject;
			if (builderCompany != null) {
				this.builderId.setAttribute("builderId", builderCompany.getId());
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(BuilderProjcet aBuilderProjcet, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aBuilderProjcet.getBefImage(), aBuilderProjcet);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aBuilderProjcet.getUserDetails(),
				getOverideMap());
	}

	/**
	 * Method for Rendering Document Details Data
	 */
	private void appendDocumentDetailTab() {
		logger.debug(Literal.ENTERING);
		createTab("DOCUMENTDETAIL", true);
		final HashMap<String, Object> map = getDefaultArguments();
		map.put("documentDetails", this.builderProjcet.getDocumentDetails());
		map.put("module", DocumentCategories.BUILDER_PROJ_DOC.getKey());
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DocumentDetailDialog.zul",
				getTabpanel("DOCUMENTDETAIL"), map);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method will create tab and will assign corresponding tab selection method and makes tab visibility based on
	 * parameter
	 * 
	 * @param moduleID
	 * @param tabVisible
	 */
	public void createTab(String moduleID, boolean tabVisible) {
		logger.debug(Literal.ENTERING);
		String tabName = Labels.getLabel("tab_label_" + moduleID);
		Tab tab = new Tab(tabName);
		tab.setId(getTabID(moduleID));
		tab.setVisible(tabVisible);
		tabsIndexCenter.appendChild(tab);
		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId(getTabpanelID(moduleID));
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight("100%");
		ComponentsCtrl.applyForward(tab, "onSelect=" + selectMethodName);
		logger.debug(Literal.LEAVING);
	}

	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
	}

	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(getTabpanelID(id));
	}

	public HashMap<String, Object> getDefaultArguments() {
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);
		map.put("isNotFinanceProcess", true);
		map.put("moduleName", PennantConstants.PROJECT_DOC);
		map.put("enqiryModule", enqiryModule);
		map.put("isEditable", !isReadOnly("button_BuilderProjcetDialog_btnNewDocuments"));
		return map;
	}

	public void onFulfill$state(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = state.getObject();
		String pcProvince = null;
		if (dataObject instanceof String) {
			doSetPinCodeFilters("", "");
		} else {
			Province province = (Province) dataObject;
			if (province == null) {
				doSetPinCodeFilters("", "");
			} else if (province != null) {
				this.state.setErrorMessage("");
				pcProvince = this.state.getValue();
				doSetPinCodeFilters(null, pcProvince);
			}
		}

		this.city.setValue("");
		this.city.setDescription("");
		this.pinCode.setValue("");
		this.pinCode.setDescription("");
		doSetCityFilters(pcProvince);
		logger.debug(Literal.LEAVING);
	}

	private void doSetCityFilters(String state) {
		Filter[] filters = new Filter[2];

		if (!StringUtils.isEmpty(state)) {
			filters[0] = new Filter("PCProvince", state, Filter.OP_EQUAL);
		} else {
			filters[0] = new Filter("PCProvince", "", Filter.OP_EQUAL);
		}

		filters[1] = new Filter("CITYISACTIVE", 1, Filter.OP_EQUAL);
		this.city.setFilters(filters);
	}

	private void doSetPinCodeFilters(String cityValue, String stateValue) {
		Filter[] filters = new Filter[2];
		if (!StringUtils.isEmpty(cityValue)) {
			filters[0] = new Filter("City", cityValue, Filter.OP_EQUAL);
		} else if (!StringUtils.isEmpty(stateValue)) {
			filters[0] = new Filter("PCProvince", stateValue, Filter.OP_EQUAL);
		} else {
			filters[0] = new Filter("City", null, Filter.OP_NOT_EQUAL);
		}
		filters[1] = new Filter("Active", 1, Filter.OP_EQUAL);
		this.pinCode.setFilters(filters);
	}

	public void onFulfill$city(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		Object dataObject = city.getObject();

		String cityValue = null;
		if (dataObject instanceof String) {
			this.city.setValue("");
			this.city.setDescription("");
			doSetPinCodeFilters(null, null);
		} else {
			City city = (City) dataObject;
			if (city != null) {
				this.city.setErrorMessage("");
				this.state.setErrorMessage("");

				this.state.setValue(city.getPCProvince());
				this.state.setDescription(city.getLovDescPCProvinceName());
				cityValue = this.city.getValue();
			}
		}
		doSetPinCodeFilters(cityValue, this.state.getValue());

		this.pinCode.setObject("");
		this.pinCode.setValue("");
		this.pinCode.setDescription("");

		Filter[] filters = null;
		if (StringUtils.isNotBlank(state.getValue())) {
			filters = new Filter[2];
			filters[1] = new Filter("CITYISACTIVE", 1, Filter.OP_EQUAL);
			filters[0] = new Filter("PCProvince", state.getValue(), Filter.OP_EQUAL);
		} else {
			filters = new Filter[1];
			filters[0] = new Filter("CITYISACTIVE", 1, Filter.OP_EQUAL);
		}

		this.city.setFilters(filters);
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$pinCode(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		Object dataObject = pinCode.getObject();
		if (dataObject instanceof String) {
			this.pinCode.setValue("");
			this.pinCode.setDescription("");
		} else {
			PinCode pinCode = (PinCode) dataObject;
			if (pinCode != null) {
				this.city.setValue(pinCode.getCity());
				this.city.setDescription(pinCode.getPCCityName());
				this.state.setValue(pinCode.getPCProvince());
				this.state.setDescription(pinCode.getLovDescPCProvinceName());

				this.city.setErrorMessage("");
				this.state.setErrorMessage("");
				this.pinCode.setErrorMessage("");
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$bankBranchID(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = bankBranchID.getObject();

		if (dataObject == null || dataObject instanceof String) {
			this.bankBranchID.setValue("");
			this.bankName.setValue("");
			this.branch.setValue("");
			this.space_BeneficiaryName.setSclass("");
			this.space_AccountNo.setSclass("");
			this.accountNo.setSclass("");
			this.beneficiaryName.setValue("");
			this.accountNo.setValue("");
			this.bankBranchID.setAttribute("bankBranchID", 0);
		} else {
			BankBranch details = (BankBranch) dataObject;

			if (details != null) {
				this.bankBranchID.setAttribute("bankBranchID", details.getBankBranchID());
				this.bankName.setValue(details.getBankName());
				this.builderProjcet.setCity(details.getCity());
				this.builderProjcet.setBranchBankName(details.getBankName());
				this.builderProjcet.setBranchDesc(details.getBranchDesc());
				this.builderProjcet.setiFSC(details.getIFSC());
				this.branch.setValue(details.getBranchDesc());
				this.bankBranchID.setValue(details.getIFSC());
				this.space_BeneficiaryName.setSclass("mandatory");
				this.space_AccountNo.setSclass("mandatory");

			}

			int maxAccNoLength = 0;
			if (StringUtils.isNotBlank(details.getBankCode())) {
				BankDetail bankDetail = bankDetailService.getAccNoLengthByCode(details.getBankCode());
				maxAccNoLength = bankDetail.getAccNoLength();
			}
			this.accountNo.setMaxlength(maxAccNoLength);

		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNewProjectUnits(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		ProjectUnits projectUnits = new ProjectUnits();
		projectUnits.setNewRecord(true);
		projectUnits.setWorkflowId(0);
		// Display the dialog page.
		doShowDialogPage(projectUnits);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param builderprojcet The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ProjectUnits aProjectUnits) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("projectUnits", aProjectUnits);
		arg.put("builderProjectDialogCtrl", this);
		try {
			Executions.createComponents("/WEB-INF/pages/SystemMaster/BuilderProjcet/ProjectUnitsDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void doRenderProjectUnits(List<ProjectUnits> projectUnitsList) {
		logger.debug(Literal.ENTERING);
		this.listBoxProjectUnits.getItems().clear();
		if (projectUnitsList != null && !projectUnitsList.isEmpty()) {
			setProjectUnitsList(projectUnitsList);
			for (ProjectUnits projectUnits : projectUnitsList) {
				Listitem listitem = new Listitem();
				Listcell listcell;
				listcell = new Listcell(projectUnits.getUnitType());
				listitem.appendChild(listcell);
				listcell = new Listcell(projectUnits.getTower());
				listitem.appendChild(listcell);
				listcell = new Listcell(projectUnits.getFloorNumber());
				listitem.appendChild(listcell);
				listcell = new Listcell(projectUnits.getUnitNumber());
				listitem.appendChild(listcell);
				listcell = new Listcell(PennantJavaUtil.getLabel(projectUnits.getRecordType()));
				listitem.appendChild(listcell);
				listitem.setAttribute("data", projectUnits);
				ComponentsCtrl.applyForward(listitem, "onDoubleClick=onProjectUnitsItemDoubleClicked");
				this.listBoxProjectUnits.appendChild(listitem);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onProjectUnitsItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);
		// Get the selected record.
		Listitem selectedItem = this.listBoxProjectUnits.getSelectedItem();
		if (selectedItem == null) {
			return;
		}
		// Get the selected entity.
		final ProjectUnits aProjectUnits = (ProjectUnits) selectedItem.getAttribute("data");
		if (aProjectUnits == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		if (!enqiryModule && isDeleteRecord(aProjectUnits)) {
			MessageUtil.showMessage(Labels.getLabel("common_NoMaintainance"));
			return;
		}

		aProjectUnits.setNewRecord(false);
		// Display the dialog page.
		doShowDialogPage(aProjectUnits);
		logger.debug(Literal.LEAVING);
	}

	public static boolean isDeleteRecord(ProjectUnits aProjectUnits) {
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, aProjectUnits.getRecordType())
				|| StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, aProjectUnits.getRecordType())) {
			return true;
		}
		return false;
	}

	public String generateApfNo() {
		logger.debug(Literal.ENTERING);

		try {
			String projectName = name.getValue();
			if (StringUtils.isNotEmpty(projectName) && StringUtils.isNotEmpty(city.getValue())
					&& StringUtils.isNotEmpty(state.getValue())) {
				String s1 = projectName.substring(0, 5);
				String s2 = state.getValue().substring(0, 2);
				String s3 = city.getValue().substring(0, 2);
				this.apfNo.setValue(s1.concat(s2).concat(s3).concat(apfSequence));

			} else {
				this.apfNo.setValue("");
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return apfSequence;

	}

	public DocumentDetailDialogCtrl getDocumentDetailDialogCtrl() {
		return documentDetailDialogCtrl;
	}

	public void setDocumentDetailDialogCtrl(DocumentDetailDialogCtrl documentDetailDialogCtrl) {
		this.documentDetailDialogCtrl = documentDetailDialogCtrl;
	}

	public FinanceCheckListReferenceDialogCtrl getFinanceCheckListReferenceDialogCtrl() {
		return financeCheckListReferenceDialogCtrl;
	}

	public void setFinanceCheckListReferenceDialogCtrl(
			FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl) {
		this.financeCheckListReferenceDialogCtrl = financeCheckListReferenceDialogCtrl;
	}

	public BuilderProjcetListCtrl getBuilderProjcetListCtrl() {
		return builderprojcetListCtrl;
	}

	public void setBuilderProjcetListCtrl(BuilderProjcetListCtrl builderProjcetListCtrl) {
		this.builderprojcetListCtrl = builderProjcetListCtrl;
	}

	public void setBuilderProjcetService(BuilderProjcetService builderProjcetService) {
		this.builderProjcetService = builderProjcetService;
	}

	public List<ProjectUnits> getProjectUnitsList() {
		return projectUnitsList;
	}

	public void setProjectUnitsList(List<ProjectUnits> projectUnitsList) {
		this.projectUnitsList = projectUnitsList;
	}

	public BankDetailService getBankDetailService() {
		return bankDetailService;
	}

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

	public BankBranchService getBankBranchService() {
		return bankBranchService;
	}

	public void setBankBranchService(BankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}

}
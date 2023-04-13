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
 * * FileName : FacilityDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 25-11-2013 * * Modified
 * Date : 25-11-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 25-11-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.facility.facility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.Interface.service.CustomerLimitIntefaceService;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.Collateral;
import com.pennant.backend.model.collateral.FacilityDetail;
import com.pennant.backend.model.customermasters.CustomerGroup;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.model.facility.Facility;
import com.pennant.backend.model.lmtmasters.FacilityReferenceDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.facility.FacilityService;
import com.pennant.backend.util.FacilityConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.PTCKeditor;
import com.pennant.coreinterface.model.CustomerCollateral;
import com.pennant.coreinterface.model.CustomerLimit;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.dedup.dedupparm.ShowDedupListBox;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.ScreenCTL;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.notifications.service.NotificationService;
import com.pennapps.core.util.ObjectUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Facility/Facility/facilityDialog.zul file.
 */
public class FacilityDialogCtrl extends GFCBaseCtrl<Facility> {
	private static final long serialVersionUID = -1600733082543197594L;
	private static final Logger logger = LogManager.getLogger(FacilityDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FacilityDialog;
	protected Row row0;
	protected Label label_CAFReference;
	protected Hlayout hlayout_CAFReference;
	protected Space space_CAFReference;
	protected Textbox cAFReference;
	protected Label label_CustID;
	protected Hlayout hlayout_CustID;
	protected Space space_CustID;
	protected Textbox custID;
	protected Row row1;
	protected Label label_StartDate;
	protected Hlayout hlayout_StartDate;
	protected Space space_StartDate;
	protected Datebox startDate;
	protected Label label_PresentingUnit;
	protected Hlayout hlayout_PresentingUnit;
	protected Space space_PresentingUnit;
	protected Textbox presentingUnit;
	protected Row row2;
	protected Label label_CountryOfDomicile;
	protected Hlayout hlayout_CountryOfDomicile;
	protected Space space_CountryOfDomicile;
	protected ExtendedCombobox countryOfDomicile;
	protected Label label_DeadLine;
	protected Hlayout hlayout_DeadLine;
	protected Space space_DeadLine;
	protected Datebox deadLine;
	protected Row row3;
	protected Label label_CountryOfRisk;
	protected Hlayout hlayout_CountryOfRisk;
	protected Space space_CountryOfRisk;
	protected ExtendedCombobox countryOfRisk;
	protected Label label_EstablishedDate;
	protected Hlayout hlayout_EstablishedDate;
	protected Space space_EstablishedDate;
	protected Datebox establishedDate;
	protected Row row4;
	protected Label label_NatureOfBusiness;
	protected Hlayout hlayout_NatureOfBusiness;
	protected Space space_NatureOfBusiness;
	protected ExtendedCombobox natureOfBusiness;
	protected Label label_SICCode;
	// protected Hlayout hlayout_SICCode;
	// protected Space space_SICCode;
	protected ExtendedCombobox sICCode;
	protected Row row5;
	protected Label label_CountryManager;
	protected Hlayout hlayout_CountryManager;
	protected Space space_CountryManager;
	protected ExtendedCombobox countryManager;
	protected Label label_CustomerRiskType;
	// protected Hlayout hlayout_CustomerRiskType;
	// protected Space space_CustomerRiskType;
	protected ExtendedCombobox customerRiskType;
	protected Row row6;
	protected Label label_RelationshipManager;
	protected Hlayout hlayout_RelationshipManager;
	protected Space space_RelationshipManager;
	protected Textbox relationshipManager;
	protected Label label_CustomerGroup;
	protected Hlayout hlayout_CustomerGroup;
	protected Space space_CustomerGroup;
	protected ExtendedCombobox customerGroup;
	protected Row row7;
	protected Label label_NextReviewDate;
	protected Hlayout hlayout_NextReviewDate;
	protected Space space_NextReviewDate;
	protected Datebox nextReviewDate;
	protected Label recordType;

	protected Caption limits_Caption;
	// New
	protected Combobox levelOfApproval;
	protected Textbox countryLimitAdeq;
	protected Textbox reviewCenter;
	protected Decimalbox countryLimit;
	protected Label label_FacilityDialog_CountryLimit;
	protected Decimalbox countryExposure;
	protected Label label_FacilityDialog_CountryExposure;
	protected Decimalbox custGroupLimit;
	protected Label label_FacilityDialog_CustGroupLimit;
	protected Decimalbox custGroupExposure;
	protected Label label_FacilityDialog_CustGroupExposure;
	protected Textbox connectedCustomer;
	protected Checkbox customerConnected;
	protected Checkbox customerRelated;
	protected Datebox aIBRelation;
	protected Textbox txb_aibRelation;

	protected Listbox listBoxCustomerLimit;
	protected Listbox listBoxCustomerRating;
	protected Listbox listBoxCustomerDirectory;
	protected Div basicDetailTabDiv;

	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsIndexCenter;
	protected Tab customerOverview;

	protected PTCKeditor customerBackGround; // autowired
	protected PTCKeditor strength; // autowired
	protected PTCKeditor weaknesses; // autowired
	protected PTCKeditor sourceOfRepayment; // autowired
	protected PTCKeditor adequacyOfCashFlows; // autowired
	protected PTCKeditor typesOfsecurities; // autowired
	protected PTCKeditor guaranteeDescription; // autowired
	protected PTCKeditor financialSummary; // autowired
	protected PTCKeditor mitigants; // autowired

	protected Label label_antiMoneyLaunderClear;
	protected Hlayout hlayout_antiMoneyLaunderClear;
	protected Textbox antiMoneyLaunderClear;
	protected Datebox interim;

	protected PTCKeditor purpose; // autowired
	protected PTCKeditor accountRelation; // autowired
	protected PTCKeditor limitAndAncillary; // autowired
	protected PTCKeditor antiMoneyLaunderSection; // autowired

	protected Row row_antiMoneyLaunderSection;
	protected Row row_accountRelation;
	protected Row row_limitAndAncillary;

	private boolean enqModule = false;
	// not auto wired vars
	private Facility facility; // overhanded per param
	private transient FacilityListCtrl facilityListCtrl; // overhanded per param

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_FacilityDialog_";
	protected Button btnHelp;

	private List<ValueLabel> approvalList = PennantStaticListUtil.getLevelOfApprovalList();

	// ServiceDAOs / Domain Classes
	private transient FacilityService facilityService;
	private transient PagedListService pagedListService;
	private CustomerLimitIntefaceService customerLimitIntefaceService;
	private FacilityDocumentDetailDialogCtrl facilityDocumentDetailDialogCtrl;
	private FacilityCheckListReferenceDialogCtrl facilityCheckListReferenceDialogCtrl;
	private FacilityScoringDetailDialogCtrl facilityScoringDetailDialogCtrl;
	private FacilityDetailListCtrl facilityDetailListCtrl;
	private FacilityAgreementDetailDialogCtrl facilityAgreementDetailDialogCtrl;

	private NotificationService notificationService;
	List<CustomerCollateral> collateralsFromEquation = null;

	private boolean recommendEntered = false;

	public List<CustomerCollateral> getCollateralsFromEquation() {
		return collateralsFromEquation;
	}

	public void setCollateralsFromEquation(List<CustomerCollateral> collaterals) {
		this.collateralsFromEquation = collaterals;
	}

	Date appldate = SysParamUtil.getAppDate();

	int ccyFormat = 0;
	private BigDecimal countryLimitEQ = BigDecimal.ZERO;
	private BigDecimal countryExposureEQ = BigDecimal.ZERO;
	private String countryLimitCureency = "";
	private boolean isCreditAdminManager;
	private boolean isCommerical;

	/**
	 * default constructor.<br>
	 */
	public FacilityDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FacilityDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected Facility object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_FacilityDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FacilityDialog);

		try {
			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}
			// READ OVERHANDED params !
			if (arguments.containsKey("facility")) {
				this.facility = (Facility) arguments.get("facility");
				Facility befImage = new Facility();
				BeanUtils.copyProperties(this.facility, befImage);
				this.facility.setBefImage(befImage);
				setFacility(this.facility);
			} else {
				setFacility(null);
			}
			doLoadWorkFlow(this.facility.isWorkflow(), this.facility.getWorkflowId(), this.facility.getNextTaskId());
			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "FacilityDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}
			/* set components visible dependent of the users rights */
			doCheckRights();
			// READ OVERHANDED params !
			// we get the facilityListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete facility here.
			if (arguments.containsKey("facilityListCtrl")) {
				setFacilityListCtrl((FacilityListCtrl) arguments.get("facilityListCtrl"));
			} else {
				setFacilityListCtrl(null);
			}
			this.basicDetailTabDiv.setHeight(this.borderLayoutHeight - 80 - 52 + "px");
			// set Field Properties
			doSetFieldProperties();

			if (getFacility().getFacilityType().equals(FacilityConstants.FACILITY_COMMERCIAL)) {
				isCommerical = true;
			}
			if (!enqModule && "CreditAdminManager".equals(getTaskTabs(getTaskId(getRole())))) {
				isCreditAdminManager = true;
			}
			doShowDialog(getFacility());
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
		displayComponents(ScreenCTL.SCRN_GNEDT);
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
	 */
	public void onClick$btnSave(Event event) {
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
		doWriteBeanToComponents(this.facility.getBefImage());
		displayComponents(ScreenCTL.SCRN_GNINT);
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
		MessageUtil.showHelpWindow(event, window_FacilityDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	protected void doPostClose() {
		deAllocateChildWindowRights();
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug("Entering" + event.toString());
		try {
			ScreenCTL.displayNotes(getNotes("Facility", getFacility().getCAFReference(), getFacility().getVersion()),
					this);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$customerGroup(Event event) {
		logger.debug("Entering" + event.toString());
		fillLimitListBox(getFacility().getCustCIF(), this.customerGroup.getValue());
		setCountryLimitAdeq(getFacility().getFacilityDetails());
		logger.debug("Leaving" + event.toString());
	}

	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFacility
	 * @throws InterruptedException
	 */
	public void doShowDialog(Facility aFacility) throws InterruptedException {
		logger.debug("Entering");
		try {
			// fill the components with the data
			doWriteBeanToComponents(aFacility);
			// set ReadOnly mode accordingly if the object is new or not.
			displayComponents(ScreenCTL.getMode(enqModule, isWorkFlowEnabled(), aFacility.isNewRecord()));

			doCheckOnCreateCondiitons();

			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private void doCheckOnCreateCondiitons() {
		logger.debug("Entering");
		doDesignByMode();
		doCheckCustomerType();
		checkConnectedCustomer();
		setCountryLimitAdeq(getFacility().getFacilityDetails());
		logger.debug("Leaving");
	}

	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit
	private void displayComponents(int mode) {
		logger.debug("Entering");
		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, isWorkFlowEnabled(), isFirstTask(),
				this.userAction, this.cAFReference, this.custID));
		if (getFacility().isNewRecord()) {
			setComponentAccessType("FacilityDialog_CAFReference", false, this.cAFReference, this.space_CAFReference,
					this.label_CAFReference, this.hlayout_CAFReference, null);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");
		boolean tempReadOnly = readOnly;
		if (readOnly) {
			tempReadOnly = true;
		} else if (PennantConstants.RECORD_TYPE_DEL.equals(this.facility.getRecordType())) {
			tempReadOnly = true;
		}
		setComponentAccessType("FacilityDialog_CAFReference", true, this.cAFReference, this.space_CAFReference,
				this.label_CAFReference, this.hlayout_CAFReference, null);
		setRowInvisible(this.row0, this.hlayout_CAFReference, this.hlayout_CustID);
		setComponentAccessType("FacilityDialog_DeadLine", tempReadOnly, this.deadLine, null, this.label_DeadLine,
				this.hlayout_DeadLine, null);
		setRowInvisible(this.row2, countryOfDomicile, this.hlayout_DeadLine);
		setComponentAccessType("FacilityDialog_EstablishedDate", tempReadOnly, this.establishedDate,
				this.space_EstablishedDate, this.label_EstablishedDate, this.hlayout_EstablishedDate, null);
		setRowInvisible(this.row3, countryOfRisk, this.hlayout_EstablishedDate);
		setComponentAccessType("FacilityDialog_RelationshipManager", tempReadOnly, this.relationshipManager,
				this.space_RelationshipManager, this.label_RelationshipManager, this.hlayout_RelationshipManager, null);
		setRowInvisible(this.row6, this.hlayout_RelationshipManager, customerGroup);
		setComponentAccessType("FacilityDialog_NextReviewDate", tempReadOnly, this.nextReviewDate,
				this.space_NextReviewDate, this.label_NextReviewDate, this.hlayout_NextReviewDate, null);
		setRowInvisible(this.row7, this.hlayout_NextReviewDate, null);

		readOnlyComponent(isReadOnly("FacilityDialog_ReviewCenter"), this.reviewCenter);
		readOnlyComponent(isReadOnly("FacilityDialog_CountryLimitAdeq"), this.countryLimitAdeq);
		readOnlyComponent(isReadOnly("FacilityDialog_CountryLimit"), this.countryLimit);
		readOnlyComponent(isReadOnly("FacilityDialog_CountryExposure"), this.countryExposure);
		// Seperate Rights to be created for the Customer Group Limits
		readOnlyComponent(isReadOnly("FacilityDialog_CountryLimit"), this.custGroupLimit);
		readOnlyComponent(isReadOnly("FacilityDialog_CountryExposure"), this.custGroupExposure);

		readOnlyComponent(isReadOnly("FacilityDialog_antiMoneyLaunderClear"), this.antiMoneyLaunderClear);
		readOnlyComponent(isReadOnly("FacilityDialog_interim"), this.interim);

		if (!enqModule) {
			this.countryOfDomicile.setReadonly(isReadOnly("FacilityDialog_CountryOfDomicile"));
			this.countryOfRisk.setReadonly(isReadOnly("FacilityDialog_CountryOfRisk"));
			this.natureOfBusiness.setReadonly(isReadOnly("FacilityDialog_NatureOfBusiness"));
			this.countryManager.setReadonly(isReadOnly("FacilityDialog_CountryManager"));
			this.customerGroup.setReadonly(true);// isReadOnly("FacilityDialog_CustomerGroup")
			this.sICCode.setReadonly(isReadOnly("FacilityDialog_SICCode"));
			this.customerRiskType.setReadonly(isReadOnly("FacilityDialog_CustomerRiskType"));
			this.customerBackGround.setReadonly(isReadOnly("FacilityDialog_CustomerBackGround"));
			this.strength.setReadonly(isReadOnly("FacilityDialog_Strength"));
			this.weaknesses.setReadonly(isReadOnly("FacilityDialog_Weaknesses"));
			this.sourceOfRepayment.setReadonly(isReadOnly("FacilityDialog_SourceOfRepayment"));
			this.adequacyOfCashFlows.setReadonly(isReadOnly("FacilityDialog_AdequacyOfCashFlows"));
			this.typesOfsecurities.setReadonly(isReadOnly("FacilityDialog_TypesOfSecurities"));
			this.guaranteeDescription.setReadonly(isReadOnly("FacilityDialog_GuaranteeDescription"));
			this.financialSummary.setReadonly(isReadOnly("FacilityDialog_FinancialSummary"));
			this.mitigants.setReadonly(isReadOnly("FacilityDialog_Mitigants"));

			this.purpose.setReadonly(isReadOnly("FacilityDialog_purpose"));
			this.accountRelation.setReadonly(isReadOnly("FacilityDialog_accountRelation"));
			this.limitAndAncillary.setReadonly(isReadOnly("FacilityDialog_limitAndAncillary"));
			this.antiMoneyLaunderSection.setReadonly(isReadOnly("FacilityDialog_antiMoneyLaunderSection"));

		} else {
			this.countryOfDomicile.setReadonly(enqModule);
			this.countryOfRisk.setReadonly(enqModule);
			this.natureOfBusiness.setReadonly(enqModule);
			this.countryManager.setReadonly(enqModule);
			this.customerGroup.setReadonly(enqModule);
			this.sICCode.setReadonly(enqModule);
			this.customerRiskType.setReadonly(enqModule);
			this.customerBackGround.setReadonly(enqModule);
			this.strength.setReadonly(enqModule);
			this.weaknesses.setReadonly(enqModule);
			this.sourceOfRepayment.setReadonly(enqModule);
			this.adequacyOfCashFlows.setReadonly(enqModule);
			this.typesOfsecurities.setReadonly(enqModule);
			this.guaranteeDescription.setReadonly(enqModule);
			this.financialSummary.setReadonly(enqModule);
			this.mitigants.setReadonly(enqModule);

			this.purpose.setReadonly(enqModule);
			this.accountRelation.setReadonly(enqModule);
			this.limitAndAncillary.setReadonly(enqModule);
			this.antiMoneyLaunderSection.setReadonly(enqModule);

			readOnlyComponent(enqModule, this.reviewCenter);
			readOnlyComponent(enqModule, this.countryLimitAdeq);
			readOnlyComponent(enqModule, this.countryLimit);
			readOnlyComponent(enqModule, this.countryExposure);
			readOnlyComponent(enqModule, this.custGroupLimit);
			readOnlyComponent(enqModule, this.custGroupExposure);
			readOnlyComponent(enqModule, this.antiMoneyLaunderClear);
			readOnlyComponent(enqModule, this.interim);
		}
		if (isCreditAdminManager && !enqModule) {
			readOnlyComponent(false, this.levelOfApproval);
		} else {
			readOnlyComponent(true, this.levelOfApproval);
		}

		logger.debug("Leaving");
	}

	// Helpers

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("FacilityDialog", getRole());
		if (!enqModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FacilityDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FacilityDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FacilityDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FacilityDialog_btnSave"));
			this.button_CollateralList_NewCollateral
					.setVisible(getUserWorkspace().isAllowed("button_FacilityDialog_btnNew"));
		}
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.cAFReference.setMaxlength(50);
		this.startDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.presentingUnit.setMaxlength(50);
		this.countryOfDomicile.setTextBoxWidth(30);
		this.countryOfDomicile.setModuleName("Country");
		this.countryOfDomicile.setValueColumn("CountryCode");
		this.countryOfDomicile.setDescColumn("CountryDesc");
		this.countryOfDomicile.setValidateColumns(new String[] { "CountryCode" });
		this.countryOfDomicile.setMandatoryStyle(true);

		this.deadLine.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.countryOfRisk.setTextBoxWidth(30);
		this.countryOfRisk.setModuleName("Country");
		this.countryOfRisk.setValueColumn("CountryCode");
		this.countryOfRisk.setDescColumn("CountryDesc");
		this.countryOfRisk.setValidateColumns(new String[] { "CountryCode" });
		this.countryOfRisk.setMandatoryStyle(true);

		this.establishedDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.natureOfBusiness.setTextBoxWidth(100);
		this.natureOfBusiness.setModuleName("Sector");
		this.natureOfBusiness.setValueColumn("SectorCode");
		this.natureOfBusiness.setDescColumn("SectorDesc");
		this.natureOfBusiness.setValidateColumns(new String[] { "SectorCode" });
		this.natureOfBusiness.setMandatoryStyle(true);
		this.sICCode.setMaxlength(8);
		this.sICCode.setMandatoryStyle(true);
		this.sICCode.setModuleName("SubSector");
		this.sICCode.setValueColumn("SubSectorCode");
		this.sICCode.setDescColumn("SubSectorDesc");
		this.sICCode.setValidateColumns(new String[] { "SubSectorCode" });
		this.customerRiskType.setMaxlength(8);
		this.customerRiskType.setMandatoryStyle(true);
		this.customerRiskType.setModuleName("CustRiskType");
		this.customerRiskType.setValueColumn("RiskCode");
		this.customerRiskType.setDescColumn("RiskDesc");
		this.customerRiskType.setValidateColumns(new String[] { "RiskCode" });
		this.countryManager.setTextBoxWidth(100);
		this.countryManager.setModuleName("EntityCodes");
		this.countryManager.setValueColumn("EntityCode");
		this.countryManager.setDescColumn("EntityDesc");
		this.countryManager.setValidateColumns(new String[] { "EntityCode" });
		this.countryManager.setMandatoryStyle(true);

		this.relationshipManager.setMaxlength(50);
		this.customerGroup.setTextBoxWidth(100);
		this.customerGroup.setModuleName("CustomerGroup");
		this.customerGroup.setValueColumn("CustGrpCode");
		this.customerGroup.setDescColumn("CustGrpDesc");
		this.customerGroup.setValidateColumns(new String[] { "CustGrpCode" });
		// this.customerGroup.setMandatoryStyle(true);

		this.nextReviewDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.countryExposure.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.countryExposure.setScale(ccyFormat);
		this.countryLimit.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.countryLimit.setScale(ccyFormat);
		this.custGroupExposure.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.custGroupExposure.setScale(ccyFormat);
		this.custGroupLimit.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.custGroupLimit.setScale(ccyFormat);

		this.reviewCenter.setMaxlength(50);
		this.countryLimitAdeq.setMaxlength(50);
		this.levelOfApproval.setMaxlength(50);

		this.interim.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.antiMoneyLaunderClear.setMaxlength(200);

		setStatusDetails(null, groupboxWf, south, enqModule);
		logger.debug("Leaving");
	}

	public void onFulfill$natureOfBusiness(Event event) {
		doSetSubSectorFilters(this.natureOfBusiness.getValidatedValue());
	}

	public void doSetSubSectorFilters(String sectorcode) {
		if (StringUtils.isNotBlank(sectorcode)) {
			Filter filters[] = new Filter[1];
			filters[0] = new Filter("SectorCode", sectorcode, Filter.OP_EQUAL);
			this.sICCode.setFilters(filters);
		}
		this.sICCode.setValue("", "");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFacility Facility
	 */
	public void doWriteBeanToComponents(Facility aFacility) {
		logger.debug("Entering");
		doFillCustomerRatings(aFacility.getCustomerRatings());
		doFillDirectordetails(aFacility.getCustID());
		doFillCollaterals(aFacility.getCollaterals());
		fillLimitListBox(aFacility.getCustCIF(), aFacility.getCustGrpCodeName());
		doFillCustomerCollateral(aFacility.getCustCIF());
		appendTabs(aFacility);
		aFacility.setCountryLimit(countryLimitEQ);
		aFacility.setCountryExposure(countryExposureEQ);
		this.cAFReference.setValue(aFacility.getCAFReference());
		this.custID.setValue(aFacility.getCustCIF() + "-" + aFacility.getCustShrtName());
		this.startDate.setValue(aFacility.getStartDate());
		this.presentingUnit.setValue(aFacility.getPresentingUnit());
		this.countryOfDomicile.setValue(aFacility.getCountryOfDomicile());
		this.deadLine.setValue(aFacility.getDeadLine());
		this.countryOfRisk.setValue(aFacility.getCountryOfRisk());
		this.establishedDate.setValue(aFacility.getEstablishedDate());
		this.natureOfBusiness.setValue(aFacility.getNatureOfBusiness());
		doSetSubSectorFilters(aFacility.getNatureOfBusiness());
		this.sICCode.setValue(aFacility.getSICCode());
		this.countryManager.setValue(aFacility.getCountryManager());
		this.customerRiskType.setValue(aFacility.getCustomerRiskType());
		this.relationshipManager.setValue(aFacility.getRelationshipManager());

		this.nextReviewDate.setValue(aFacility.getNextReviewDate());
		this.countryOfDomicile.setDescription(StringUtils.trimToEmpty(aFacility.getCountryOfDomicileName()));
		this.countryOfRisk.setDescription(StringUtils.trimToEmpty(aFacility.getCountryOfRiskName()));
		this.natureOfBusiness.setDescription(StringUtils.trimToEmpty(aFacility.getNatureOfBusinessName()));
		this.countryManager.setDescription(StringUtils.trimToEmpty(aFacility.getCountryManagerName()));
		this.sICCode.setDescription(StringUtils.trimToEmpty(aFacility.getSICCodeName()));
		this.customerRiskType.setDescription(StringUtils.trimToEmpty(aFacility.getCustomerRiskTypeName()));
		this.customerBackGround.setValue(aFacility.getCustomerBackGround());
		this.strength.setValue(aFacility.getStrength());
		this.weaknesses.setValue(aFacility.getWeaknesses());
		this.sourceOfRepayment.setValue(aFacility.getSourceOfRepayment());
		this.adequacyOfCashFlows.setValue(aFacility.getAdequacyOfCashFlows());
		this.typesOfsecurities.setValue(aFacility.getTypesOfSecurities());
		this.guaranteeDescription.setValue(aFacility.getGuaranteeDescription());
		this.financialSummary.setValue(aFacility.getFinancialSummary());
		this.mitigants.setValue(aFacility.getMitigants());
		if (StringUtils.trimToEmpty(aFacility.getCustRelation()).equals(FacilityConstants.CUSTRELATION_RELATED)) {
			this.connectedCustomer.setValue(Labels.getLabel("Related_Customer"));
			this.customerRelated.setChecked(true);
		} else if (StringUtils.trimToEmpty(aFacility.getCustRelation())
				.equals(FacilityConstants.CUSTRELATION_CONNECTED)) {
			this.connectedCustomer.setValue(Labels.getLabel("Connected_Customer"));
			this.customerConnected.setChecked(true);
		} else {
			this.connectedCustomer.setValue(Labels.getLabel("NoRelation"));
		}
		checkConnectedCustomer();

		if (aFacility.getCustDOB() != null) {
			this.aIBRelation.setValue(aFacility.getCustDOB());
		} else {
			this.aIBRelation.setVisible(false);
			this.txb_aibRelation.setVisible(true);
			this.txb_aibRelation.setValue("NEW");
		}

		this.customerGroup.setObject(String.valueOf(aFacility.getCustomerGroup()));
		this.customerGroup.setValue(aFacility.getCustGrpCodeName());
		this.customerGroup.setDescription(StringUtils.trimToEmpty(aFacility.getCustomerGroupName()));

		this.countryExposure.setValue(CurrencyUtil.parse(aFacility.getCountryExposure(), ccyFormat));
		this.countryLimit.setValue(CurrencyUtil.parse(aFacility.getCountryLimit(), ccyFormat));
		this.reviewCenter.setValue(aFacility.getReviewCenter());
		this.countryLimitAdeq.setValue(aFacility.getCountryLimitAdeq());
		if (!aFacility.isOverriddeCirculation()) {
			fillComboBox(this.levelOfApproval, getFacilityService().getActualLevelAprroval(aFacility), approvalList);
		} else {
			fillComboBox(this.levelOfApproval, aFacility.getLevelOfApproval(), approvalList);
		}

		this.purpose.setValue(aFacility.getPurpose());
		this.accountRelation.setValue(aFacility.getAccountRelation());
		this.limitAndAncillary.setValue(aFacility.getLimitAndAncillary());
		this.antiMoneyLaunderSection.setValue(aFacility.getAntiMoneyLaunderSection());
		this.antiMoneyLaunderClear.setValue(aFacility.getAntiMoneyLaunderClear());
		this.interim.setValue(aFacility.getInterim());
		this.recordStatus.setValue(aFacility.getRecordStatus());
		logger.debug("Leaving");
	}

	public void checkConnectedCustomer() {
		if (this.customerRelated.isChecked()) {
			this.customerGroup.setMandatoryStyle(true);
		} else {
			this.customerGroup.setMandatoryStyle(false);
		}
	}

	public void setCountryLimitAdeq(List<FacilityDetail> facilityDetails) {
		logger.debug("Entering");
		this.countryLimitAdeq.setDisabled(true);
		BigDecimal exposure = this.countryExposure.getValue() == null ? BigDecimal.ZERO
				: this.countryExposure.getValue();
		BigDecimal countryLimit = this.countryLimit.getValue() == null ? BigDecimal.ZERO : this.countryLimit.getValue();
		BigDecimal totfacilityUsd = BigDecimal.ZERO;
		if (facilityDetails != null && !facilityDetails.isEmpty()) {
			for (FacilityDetail facilityDetail : facilityDetails) {
				totfacilityUsd = totfacilityUsd.add(CalculationUtil.getConvertedAmount(facilityDetail.getFacilityCCY(),
						AccountConstants.CURRENCY_USD, facilityDetail.getNewLimit()));
			}
			totfacilityUsd = CurrencyUtil.parse(totfacilityUsd, AccountConstants.CURRENCY_USD_FORMATTER);
		}

		if (StringUtils.isNotEmpty(countryLimitCureency)
				&& !countryLimitCureency.equals(AccountConstants.CURRENCY_USD)) {
			totfacilityUsd = CalculationUtil.getConvertedAmount(AccountConstants.CURRENCY_USD, countryLimitCureency,
					totfacilityUsd);
		}
		if ((exposure.add(totfacilityUsd)).compareTo(countryLimit) > 0) {
			this.countryLimitAdeq.setValue(Labels.getLabel("common.Yes"));
		} else {
			this.countryLimitAdeq.setValue(Labels.getLabel("common.No"));
		}
		logger.debug("Leaving");
	}

	protected void fillComboBox(Combobox combobox, String value, List<ValueLabel> list) {
		logger.debug("Entering fillComboBox()");
		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		combobox.setReadonly(true);
		for (ValueLabel valueLabel : list) {
			if (!isCommerical && StringUtils.trim(valueLabel.getValue())
					.equals(FacilityConstants.FACILITY_LOA_COMM_BANKING_CREDIT_COMMITTEE)) {
				continue;
			}
			comboitem = new Comboitem();
			comboitem.setValue(valueLabel.getValue());
			comboitem.setLabel(valueLabel.getLabel());
			combobox.appendChild(comboitem);
			if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(valueLabel.getValue()))) {
				combobox.setSelectedItem(comboitem);
			}
		}
		logger.debug("Leaving fillComboBox()");
	}

	private void doFillDirectordetails(long custID) {
		logger.debug("Entering");
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<DirectorDetail> jdbcSearchObject = new JdbcSearchObject<DirectorDetail>(DirectorDetail.class);
		jdbcSearchObject.addTabelName("CustomerDirectorDetail_AView");
		jdbcSearchObject.addFilterEqual("Shareholder", 1);
		jdbcSearchObject.addFilterEqual("CustID", custID);
		List<DirectorDetail> directorList = pagedListService.getBySearchObject(jdbcSearchObject);
		if (directorList != null && !directorList.isEmpty()) {
			Listitem item;
			BigDecimal totSharePerc = BigDecimal.ZERO;
			for (DirectorDetail directorDetail : directorList) {
				item = new Listitem();
				String name = "";
				if (StringUtils.isNotBlank(directorDetail.getShortName())) {
					name = directorDetail.getShortName();
				} else {
					name = directorDetail.getFirstName() + "  " + directorDetail.getLastName();
				}
				Listcell lc = new Listcell(name);
				lc.setParent(item);
				lc = new Listcell(String.valueOf(directorDetail.getDirectorId()));
				lc.setParent(item);
				lc = new Listcell(directorDetail.getLovDescCustAddrCountryName());
				lc.setParent(item);
				lc = new Listcell(String.valueOf(directorDetail.getSharePerc()));
				lc.setParent(item);
				this.listBoxCustomerDirectory.appendChild(item);
				totSharePerc = totSharePerc.add(directorDetail.getSharePerc());
			}
			item = new Listitem();
			Listcell lc = new Listcell("Total");
			lc.setStyle("font-weight:bold");
			lc.setParent(item);
			lc = new Listcell();
			lc.setSpan(2);
			lc.setParent(item);
			lc = new Listcell(String.valueOf(totSharePerc));
			lc.setParent(item);
			this.listBoxCustomerDirectory.appendChild(item);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFacility
	 */
	public void doWriteComponentsToBean(Facility aFacility) {
		logger.debug("Entering");
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		// CAF Reference
		try {
			aFacility.setCAFReference(this.cAFReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Start Date
		try {
			aFacility.setStartDate(this.startDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Presenting Unit
		try {
			aFacility.setPresentingUnit(this.presentingUnit.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Country of Domicile
		try {
			aFacility.setCountryOfDomicile(this.countryOfDomicile.getValidatedValue());
			aFacility.setCountryOfDomicileName(this.countryOfDomicile.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Dead Line
		try {
			aFacility.setDeadLine(this.deadLine.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Country of Risk
		try {
			aFacility.setCountryOfRisk(this.countryOfRisk.getValidatedValue());
			aFacility.setCountryOfRiskName(this.countryOfRisk.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Established Date
		try {
			aFacility.setEstablishedDate(this.establishedDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Nature of Business
		try {
			aFacility.setNatureOfBusiness(this.natureOfBusiness.getValidatedValue());
			aFacility.setNatureOfBusinessName(this.natureOfBusiness.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// SIC Code
		try {
			aFacility.setSICCode(this.sICCode.getValidatedValue());
			aFacility.setSICCodeName(this.sICCode.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Country Manager
		try {
			aFacility.setCountryManager(this.countryManager.getValidatedValue());
			aFacility.setCountryManagerName(this.countryManager.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Customer Risk Type
		try {
			aFacility.setCustomerRiskType(this.customerRiskType.getValidatedValue());
			aFacility.setCustomerRiskTypeName(this.customerRiskType.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Relationship Manager
		try {
			aFacility.setRelationshipManager(this.relationshipManager.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Customer Group
		try {
			this.customerGroup.getValidatedValue();
			Object object = this.customerGroup.getObject();
			if (object != null) {
				if (object instanceof CustomerGroup) {
					CustomerGroup customerGroup = (CustomerGroup) object;
					aFacility.setCustomerGroup(customerGroup.getCustGrpID());
					aFacility.setCustGrpCodeName(customerGroup.getCustGrpCode());
				} else if (object instanceof String) {
					String customerGroup = (String) object;
					aFacility.setCustomerGroup(StringUtils.isEmpty(customerGroup) ? 0 : Long.parseLong(customerGroup));
				}
			}
			aFacility.setCustomerGroupName(this.customerGroup.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Next Review Date
		try {
			aFacility.setNextReviewDate(this.nextReviewDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Country Exposure
		try {
			aFacility.setCountryExposure(CurrencyUtil.unFormat(this.countryExposure.getValue(), ccyFormat));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Country Limit
		try {
			aFacility.setCountryLimit(CurrencyUtil.unFormat(this.countryLimit.getValue(), ccyFormat));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Review Center
		try {
			aFacility.setReviewCenter(this.reviewCenter.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// countryLimitAdeq
		try {
			aFacility.setCountryLimitAdeq(this.countryLimitAdeq.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// level Of Approval
		try {
			aFacility.setLevelOfApproval(this.levelOfApproval.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		aFacility.setCustomerBackGround(this.customerBackGround.getValue());
		aFacility.setStrength(this.strength.getValue());
		aFacility.setWeaknesses(this.weaknesses.getValue());
		aFacility.setSourceOfRepayment(this.sourceOfRepayment.getValue());
		aFacility.setAdequacyOfCashFlows(this.adequacyOfCashFlows.getValue());
		aFacility.setTypesOfSecurities(this.typesOfsecurities.getValue());
		aFacility.setGuaranteeDescription(this.guaranteeDescription.getValue());
		aFacility.setFinancialSummary(this.financialSummary.getValue());
		aFacility.setMitigants(this.mitigants.getValue());

		try {
			aFacility.setAntiMoneyLaunderClear(this.antiMoneyLaunderClear.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacility.setInterim(this.interim.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFacility.setPurpose(this.purpose.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFacility.setAccountRelation(this.accountRelation.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacility.setLimitAndAncillary(this.limitAndAncillary.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacility.setAntiMoneyLaunderSection(this.antiMoneyLaunderSection.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		aFacility.setCollaterals(getCollateralsList());
		aFacility.setCustomerRatings(getRatingsList());
		doRemoveValidation();
		doRemoveLOVValidation();
		if (!wve.isEmpty()) {
			this.customerOverview.setSelected(true);
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		// CAF Reference
		// if (!this.cAFReference.isReadonly()) {
		// this.cAFReference.setConstraint(new
		// PTStringValidator(Labels.getLabel("label_FacilityDialog_CAFReference.value"),
		// PennantRegularExpressions.REGEX_NAME, true));
		// }
		// Customer
		if (!this.custID.isReadonly()) {
			this.custID.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityDialog_CustID.value"),
					PennantRegularExpressions.REGEX_NAME, true));
		}
		// Start Date
		if (!this.startDate.isReadonly()) {
			this.startDate
					.setConstraint(new PTDateValidator(Labels.getLabel("label_FacilityDialog_StartDate.value"), true));
		}
		// Presenting Unit
		if (!this.presentingUnit.isReadonly()) {
			this.presentingUnit
					.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityDialog_PresentingUnit.value"),
							PennantRegularExpressions.REGEX_NAME, true));
		}
		// Country of Domicile
		if (!this.countryOfDomicile.isReadonly()) {
			this.countryOfDomicile.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FacilityDialog_CountryOfDomicile.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_CODE, true, true));
		}
		// Dead Line
		if (!this.deadLine.isReadonly()) {
			this.deadLine.setConstraint(new PTDateValidator(Labels.getLabel("label_FacilityDialog_DeadLine.value"),
					false, appldate, null, true));
		}
		// Country of Risk
		if (!this.countryOfRisk.isReadonly()) {
			this.countryOfRisk
					.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityDialog_CountryOfRisk.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_CODE, true, true));
		}
		// Established Date
		if (!this.establishedDate.isReadonly()) {
			this.establishedDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FacilityDialog_EstablishedDate.value"), true));
		}
		// Nature of Business
		if (!this.natureOfBusiness.isReadonly()) {
			this.natureOfBusiness
					.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityDialog_NatureOfBusiness.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_CODE, true, true));
		}
		// SIC Code
		if (!this.sICCode.isReadonly()) {
			this.sICCode.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityDialog_SICCode.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_CODE, true, true));
		}
		// Country Manager
		if (!this.countryManager.isReadonly()) {
			this.countryManager
					.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityDialog_CountryManager.value"),
							PennantRegularExpressions.REGEX_NAME, true, true));
		}
		// Customer Risk Type
		if (!this.customerRiskType.isReadonly()) {
			this.customerRiskType
					.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityDialog_CustomerRiskType.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_CODE, true, true));
		}
		// Relationship Manager
		if (!this.relationshipManager.isReadonly()) {
			this.relationshipManager.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FacilityDialog_RelationshipManager.value"),
							PennantRegularExpressions.REGEX_NAME, true));
		}
		// Customer Group
		if (!this.customerGroup.isReadonly() && this.customerConnected.isChecked()) {
			this.customerGroup
					.setConstraint(new PTStringValidator(Labels.getLabel("label_FacilityDialog_CustomerGroup.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_CODE, true, true));
		}
		// Next Review Date
		if (!this.nextReviewDate.isReadonly()) {
			this.nextReviewDate.setConstraint(new PTDateValidator(
					Labels.getLabel("label_FacilityDialog_NextReviewDate.value"), true, appldate, null, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.cAFReference.setConstraint("");
		this.custID.setConstraint("");
		this.startDate.setConstraint("");
		this.presentingUnit.setConstraint("");
		this.countryOfDomicile.setConstraint("");
		this.deadLine.setConstraint("");
		this.countryOfRisk.setConstraint("");
		this.establishedDate.setConstraint("");
		this.natureOfBusiness.setConstraint("");
		this.sICCode.setConstraint("");
		this.countryManager.setConstraint("");
		this.customerRiskType.setConstraint("");
		this.relationshipManager.setConstraint("");
		this.customerGroup.setConstraint("");
		this.nextReviewDate.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		// CAF Reference
		// Customer
		// Start Date
		// Presenting Unit
		// Country of Domicile
		// Dead Line
		// Country of Risk
		// Established Date
		// Nature of Business
		// SIC Code
		// Country Manager
		// Customer Risk Type
		// Relationship Manager
		// Customer Group
		// Next Review Date
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveLOVValidation() {
		this.countryOfDomicile.setConstraint("");
		this.countryOfRisk.setConstraint("");
		this.natureOfBusiness.setConstraint("");
		this.countryManager.setConstraint("");
		this.customerGroup.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.cAFReference.setErrorMessage("");
		this.custID.setErrorMessage("");
		this.startDate.setErrorMessage("");
		this.presentingUnit.setErrorMessage("");
		this.countryOfDomicile.setErrorMessage("");
		this.deadLine.setErrorMessage("");
		this.countryOfRisk.setErrorMessage("");
		this.establishedDate.setErrorMessage("");
		this.natureOfBusiness.setErrorMessage("");
		this.sICCode.setErrorMessage("");
		this.countryManager.setErrorMessage("");
		this.customerRiskType.setErrorMessage("");
		this.relationshipManager.setErrorMessage("");
		this.customerGroup.setErrorMessage("");
		this.nextReviewDate.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */
	protected void refreshList() {
		getFacilityListCtrl().search();
	}

	private void deAllocateChildWindowRights() {
		deAllocateAuthorities("FacilityDetailDialog");
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final Facility aFacility = new Facility();
		BeanUtils.copyProperties(getFacility(), aFacility);

		doDelete(aFacility.getCAFReference(), aFacility);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.cAFReference.setValue("");
		this.custID.setValue("");
		this.startDate.setText("");
		this.presentingUnit.setValue("");
		this.countryOfDomicile.setValue("");
		this.countryOfDomicile.setDescription("");
		this.deadLine.setText("");
		this.countryOfRisk.setValue("");
		this.countryOfRisk.setDescription("");
		this.establishedDate.setText("");
		this.natureOfBusiness.setValue("");
		this.natureOfBusiness.setDescription("");
		this.sICCode.setValue("");
		this.countryManager.setValue("");
		this.countryManager.setDescription("");
		this.customerRiskType.setValue("");
		this.relationshipManager.setValue("");
		this.customerGroup.setValue("");
		this.customerGroup.setDescription("");
		this.nextReviewDate.setText("");
		this.customerBackGround.setValue("");
		this.strength.setValue("");
		this.weaknesses.setValue("");
		this.sourceOfRepayment.setValue("");
		this.adequacyOfCashFlows.setValue("");
		this.typesOfsecurities.setValue("");
		this.guaranteeDescription.setValue("");
		this.financialSummary.setValue("");
		this.mitigants.setValue("");

		this.antiMoneyLaunderClear.setValue("");
		this.interim.setText("");
		this.purpose.setValue("");
		this.accountRelation.setValue("");
		this.limitAndAncillary.setValue("");
		this.antiMoneyLaunderSection.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table.
	 */
	@SuppressWarnings("unchecked")
	public void doSave() {
		logger.debug("Entering");

		Facility aFacility = new Facility();
		aFacility = ObjectUtil.clone(getFacility());

		boolean isNew = false;
		if (isWorkFlowEnabled()) {
			aFacility.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aFacility.getNextTaskId(), aFacility);
		}

		// force validation, if on, than execute by component.getValue()
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aFacility.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the Facility object with the components data
			doWriteComponentsToBean(aFacility);
		}
		boolean validate = false;
		if (!("Save".equalsIgnoreCase(userAction.getSelectedItem().getLabel())
				|| "Cancel".equalsIgnoreCase(userAction.getSelectedItem().getLabel())
				|| "Resubmit".equalsIgnoreCase(userAction.getSelectedItem().getLabel())
				|| "Reject".equalsIgnoreCase(userAction.getSelectedItem().getLabel()))) {
			validate = true;
		}

		// Validation For Mandatory Recommendation

		if (validate && !isRecommeandEntered()) {
			MessageUtil.showError(Labels.getLabel("label_FacilityDialog_RecommendMand"));
			Tab tab = null;
			if (tabsIndexCenter.getFellowIfAny(MODULE_RECOMMENDATIONS + "Tab") != null) {
				tab = (Tab) tabsIndexCenter.getFellowIfAny(MODULE_RECOMMENDATIONS + "Tab");
				tab.setSelected(true);
			}
			return;
		}

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here
		isNew = aFacility.isNewRecord();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFacility.getRecordType())) {
				aFacility.setVersion(aFacility.getVersion() + 1);
				if (isNew) {
					aFacility.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFacility.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFacility.setNewRecord(true);
				}
			}

		} else {
			aFacility.setVersion(aFacility.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			// save it to database

			// Check Dedup
			List<FacilityDetail> dedupList = getDedupList(getFacility().getCAFReference(), getFacility().getCustID());
			if (dedupList != null && !dedupList.isEmpty() && !getFacility().isSkipDedup()) {
				Object dataObject = ShowDedupListBox.show(this.window_FacilityDialog, dedupList,
						"CAFReference,FacilityRef,FacilityType,FacilityFor,Revolving", null,
						getUserWorkspace().getUserDetails().getUsername());
				ShowDedupListBox details = (ShowDedupListBox) dataObject;
				int userAction = details.getUserAction();
				if (userAction == -1) {
					getFacility().setDedupFound(false);
				} else {
					getFacility().setDedupFound(true);
					if (userAction == 1) {
						getFacility().setSkipDedup(true);
					} else {
						getFacility().setSkipDedup(false);
						return;
					}
				}
			}

			// Facility Details Tab
			if (getFacilityDetailListCtrl() != null) {
				List<FacilityDetail> list = getFacilityDetailListCtrl().getFacilityDetailList();
				if (list != null && !list.isEmpty()) {
					aFacility.setFacilityDetails(list);
				} else {
					if (validate) {
						MessageUtil.showError(Labels.getLabel("LABEL_ATLEAST_ONE_FACILITY"));
						return;
					}
				}
			}

			if (validate && isCreditAdminManager && !aFacility.isOverriddeCirculation()) {
				String actualApprval = getFacilityService().getActualLevelAprroval(aFacility);
				if (!actualApprval.equals(aFacility.getLevelOfApproval())) {
					String msg = Labels.getLabel("Override_LevelOfApproval",
							new String[] { PennantStaticListUtil.getlabelDesc(actualApprval, approvalList) });
					if (MessageUtil.confirm(msg) == MessageUtil.YES) {
						aFacility.setOverriddeCirculation(true);
					} else {
						return;
					}
				}
			}

			// Scoring Details tab
			if (getFacilityScoringDetailDialogCtrl() != null) {
				getFacilityScoringDetailDialogCtrl().doSave_ScoreDetail(aFacility);
				if (!aFacility.isSufficientScore() && validate) {
					MessageUtil.showError(Labels.getLabel("label_Finance_Insufficient_Score"));
					return;
				}
			}
			// Check List Details Tab
			try {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("control", this);
				map.put("facility", aFacility);
				map.put("userAction", this.userAction.getSelectedItem().getLabel());
				if (tabpanelsIndexCenter.getFellowIfAny(MODULE_CHECKLIST + "TabPanel") != null) {
					Events.sendEvent("onChkListValidation",
							tabpanelsIndexCenter.getFellowIfAny(MODULE_CHECKLIST + "TabPanel").getFirstChild(), map);
				}

				Object object = map.get("Error");
				if (object != null) {
					ArrayList<WrongValueException> wve = (ArrayList<WrongValueException>) object;
					if (!wve.isEmpty()) {
						Tab tab = (Tab) tabpanelsIndexCenter.getFellowIfAny(MODULE_CHECKLIST + "Tab");
						tab.setSelected(true);
						WrongValueException[] wvea = new WrongValueException[wve.size()];
						for (int i = 0; i < wve.size(); i++) {
							wvea[i] = (WrongValueException) wve.get(i);
						}
						throw new WrongValuesException(wvea);
					}
				}

			} catch (Exception e) {
				logger.debug(e);
				if (e instanceof WrongValuesException) {
					throw e;
				}
				return;
			}
			// Document Details Tab
			if (getFacilityDocumentDetailDialogCtrl() != null) {
				aFacility.setDocumentDetailsList(getFacilityDocumentDetailDialogCtrl().getDocumentDetailsList());
			}

			getFacilityService().getTotalAmountsInUSDAndBHD(aFacility);
			if (doProcess(aFacility, tranType)) {
				refreshList();
				// Customer Notification for Role Identification
				if (StringUtils.isNotBlank(aFacility.getNextTaskId())) {
					String msg = PennantApplicationUtil.getSavingStatus(aFacility.getRoleCode(),
							aFacility.getNextRoleCode(), aFacility.getCAFReference(), " Facility ",
							aFacility.getRecordStatus());
					Clients.showNotification(msg, "info", null, null, -1);
				}

				// Mail Alert Notification for User
				if (StringUtils.isNotBlank(aFacility.getNextTaskId())
						&& !StringUtils.trimToEmpty(aFacility.getNextRoleCode()).equals(aFacility.getRoleCode())) {
					notificationService.sendNotifications(NotificationConstants.MAIL_MODULE_CAF, aFacility);
				}

				deAllocateChildWindowRights();
				closeDialog();
			}
		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	// WorkFlow Creations

	private String getServiceTasks(String taskId, Facility facility, String finishedTasks) {
		logger.debug("Entering");

		String serviceTasks = getServiceOperations(taskId, facility);

		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug("Leaving");
		return serviceTasks;
	}

	private void setNextTaskDetails(String taskId, Facility facility) {
		logger.debug("Entering");

		// Set the next task id
		String action = userAction.getSelectedItem().getLabel();
		String nextTaskId = StringUtils.trimToEmpty(facility.getNextTaskId());

		if ("".equals(nextTaskId)) {
			if ("Save".equals(action)) {
				nextTaskId = taskId + ";";
			}
		} else {
			if (!"Save".equals(action)) {
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			}
		}

		if ("".equals(nextTaskId)) {
			nextTaskId = getNextTaskIds(taskId, facility);
		}

		// Set the role codes for the next tasks
		String nextRoleCode = "";

		if ("".equals(nextTaskId)) {
			nextRoleCode = getFirstTaskOwner();
		} else {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks.length > 0) {
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode.concat(",");
					}
					nextRoleCode += getTaskOwner(nextTasks[i]);
				}
			}
		}

		facility.setTaskId(taskId);
		facility.setNextTaskId(nextTaskId);
		facility.setRoleCode(getRole());
		facility.setNextRoleCode(nextRoleCode);

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
	 */
	protected boolean doProcess(Facility aFacility, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = true;
		AuditHeader auditHeader = null;
		aFacility.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFacility.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFacility.setUserDetails(getUserWorkspace().getLoggedInUser());
		if (isWorkFlowEnabled()) {

			String taskId = getTaskId(getRole());
			aFacility.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, aFacility, finishedTasks);

			if (isNotesMandatory(taskId, aFacility)) {
				if (!notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
				}
			}

			auditHeader = getAuditHeader(aFacility, PennantConstants.TRAN_WF);
			while (!"".equals(serviceTasks)) {
				String method = serviceTasks.split(";")[0];
				if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doBlacklist)) {
					Facility tFacility = (Facility) auditHeader.getAuditDetail().getModelData();
					boolean isBlackListed = getFacilityService().doCheckBlackListedCustomer(auditHeader);
					tFacility.setAbuser(isBlackListed);
					if (isBlackListed) {
						processCompleted = false;
					} else {
						processCompleted = true;
					}
					auditHeader.getAuditDetail().setModelData(tFacility);
				} else if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckProspectCustomer)) {
					// Prospect Customer Checking
					Facility tFacility = (Facility) auditHeader.getAuditDetail().getModelData();
					if (StringUtils.isBlank(tFacility.getCustCoreBank())) {
						MessageUtil.showError(Labels.getLabel("label_FinanceMainDialog_Mandatory_Prospect.value"));
						return false;
					}

				} else if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckDeviations)) {
					/*
					 * List<FinanceDeviations> list = aFinanceDetail.getFinanceDeviations(); if (list!=null &&
					 * !list.isEmpty()) {
					 * aFinanceDetail.getFinScheduleData().getFinanceMain().setDeviationApproval(true); }
					 */
				} else {

					Facility tFacility = (Facility) auditHeader.getAuditDetail().getModelData();
					setNextTaskDetails(taskId, tFacility);
					auditHeader.getAuditDetail().setModelData(tFacility);
					processCompleted = doSaveProcess(auditHeader, method);

				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				Facility tFacility = (Facility) auditHeader.getAuditDetail().getModelData();
				serviceTasks = getServiceTasks(taskId, tFacility, finishedTasks);
			}

			Facility tFacility = (Facility) auditHeader.getAuditDetail().getModelData();

			// Check whether to proceed further or not
			String nextTaskId = getNextTaskIds(taskId, tFacility);

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in WorkFlow
			if (processCompleted) {

				if (!"".equals(nextTaskId) || "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, aFacility);
					auditHeader.getAuditDetail().setModelData(tFacility);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}

		} else {

			auditHeader = getAuditHeader(aFacility, tranType);
			processCompleted = doSaveProcess(auditHeader, null);

		}
		logger.debug("return value :" + processCompleted);
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
		boolean deleteNotes = false;
		Facility aFacility = (Facility) auditHeader.getAuditDetail().getModelData();
		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
					auditHeader = getFacilityService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getFacilityService().saveOrUpdate(auditHeader);
				}
			} else {
				if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
					auditHeader = getFacilityService().doApprove(auditHeader);
					aFacility = (Facility) auditHeader.getAuditDetail().getModelData();
					if (PennantConstants.RECORD_TYPE_DEL.equals(aFacility.getRecordType())) {
						deleteNotes = true;
					}
				} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
					auditHeader = getFacilityService().doReject(auditHeader);
					if (PennantConstants.RECORD_TYPE_NEW.equals(aFacility.getRecordType())) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_FacilityDialog, auditHeader);
					return processCompleted;
				}
			}
			auditHeader = ErrorControl.showErrorDetails(this.window_FacilityDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;
				if (deleteNotes) {
					deleteNotes(getNotes("Facility", aFacility.getCAFReference(), aFacility.getVersion()), true);
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
		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(Facility aFacility, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFacility.getBefImage(), aFacility);
		return new AuditHeader(aFacility.getCAFReference(), null, null, null, auditDetail, aFacility.getUserDetails(),
				getOverideMap());
	}

	// New Methods

	public void doFillCustomerRatings(List<CustomerRating> customerRatings) {
		logger.debug("Entering");
		setRatingsList(customerRatings);
		this.listBoxCustomerRating.getItems().clear();
		if (customerRatings != null && !customerRatings.isEmpty()) {
			for (CustomerRating rating : customerRatings) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell("Internal");
				lc.setParent(item);
				lc = new Listcell(rating.getCustRatingType());
				lc.setParent(item);
				lc = new Listcell(rating.getCustRating() + (StringUtils.isBlank(rating.getLovDescCustRatingName()) ? ""
						: "-" + StringUtils.trimToEmpty(rating.getLovDescCustRatingName())));
				lc.setParent(item);
				lc = new Listcell(
						rating.getCustRatingCode() + (StringUtils.isBlank(rating.getLovDesccustRatingCodeDesc()) ? ""
								: "-" + StringUtils.trimToEmpty(rating.getLovDesccustRatingCodeDesc())));
				lc.setParent(item);
				lc = new Listcell(rating.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(rating.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", rating);
				if (!enqModule) {
					ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerRatingItemDoubleClicked");
				}
				this.listBoxCustomerRating.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	public void fillLimitListBox(String custMnemonic, String custGroup) {
		logger.debug("Entering");
		this.listBoxCustomerLimit.getItems().clear();
		CustomerLimit limit = new CustomerLimit();
		limit.setCustMnemonic(custMnemonic);
		limit.setCustGrpCode(custGroup);
		limit.setCustLocation("");
		List<CustomerLimit> list = null;
		int formatter = 3;
		try {
			list = getCustomerLimitIntefaceService().fetchLimitEnquiryDetails(limit);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		boolean setCaption = false;
		if (list != null) {
			for (CustomerLimit category : list) {
				if (!setCaption) {
					limits_Caption
							.setLabel(Labels.getLabel("title_west_bottom") + "(" + category.getLimitCurrency() + ")");
					setCaption = true;
				}
				if ("##".equals(StringUtils.trimToEmpty(category.getLimitCountry()))) {
					BigDecimal custGroupLimitEQ = category.getLimitAmount().divide(
							new BigDecimal(Math.pow(10, formatter + category.getLimitCcyEdit())), RoundingMode.HALF_UP);
					BigDecimal custGroupExposureEQ = category.getRiskAmount().divide(
							new BigDecimal(Math.pow(10, formatter + category.getLimitCcyEdit())), RoundingMode.HALF_UP);
					this.custGroupLimit.setValue(CurrencyUtil.parse(custGroupLimitEQ, ccyFormat));
					this.custGroupExposure.setValue(CurrencyUtil.parse(custGroupExposureEQ, ccyFormat));
					label_FacilityDialog_CustGroupExposure.setValue(label_FacilityDialog_CustGroupExposure.getValue()
							+ "(" + category.getLimitCurrency() + ")");
					label_FacilityDialog_CustGroupLimit.setValue(
							label_FacilityDialog_CustGroupLimit.getValue() + "(" + category.getLimitCurrency() + ")");
				} else if (StringUtils.isNotBlank(category.getLimitCountry())) {
					countryLimitEQ = category.getLimitAmount().divide(
							new BigDecimal(Math.pow(10, formatter + category.getLimitCcyEdit())), RoundingMode.HALF_UP);
					countryExposureEQ = category.getRiskAmount().divide(
							new BigDecimal(Math.pow(10, formatter + category.getLimitCcyEdit())), RoundingMode.HALF_UP);
					countryLimitCureency = StringUtils.trimToEmpty(category.getLimitCurrency());
					label_FacilityDialog_CountryExposure.setValue(
							label_FacilityDialog_CountryExposure.getValue() + "(" + countryLimitCureency + ")");
					label_FacilityDialog_CountryLimit
							.setValue(label_FacilityDialog_CountryLimit.getValue() + "(" + countryLimitCureency + ")");

				} else {
					Listitem item = new Listitem();
					Listcell lc;
					lc = new Listcell(category.getLimitCategory());
					lc.setParent(item);
					lc = new Listcell(category.getLimitCategoryDesc());
					lc.setParent(item);
					lc = new Listcell(CurrencyUtil.format(category.getRiskAmount().divide(
							new BigDecimal(Math.pow(10, formatter + category.getLimitCcyEdit())), RoundingMode.HALF_UP),
							0));
					lc.setParent(item);
					lc = new Listcell(CurrencyUtil.format(category.getLimitAmount().divide(
							new BigDecimal(Math.pow(10, formatter + category.getLimitCcyEdit())), RoundingMode.HALF_UP),
							0));
					lc.setParent(item);
					lc = new Listcell(CurrencyUtil.format(category.getAvailAmount().divide(
							new BigDecimal(Math.pow(10, formatter + category.getLimitCcyEdit())), RoundingMode.HALF_UP),
							0));
					lc.setParent(item);
					item.setAttribute("data", category);

					item.setId(category.getLimitCategory());
					this.listBoxCustomerLimit.appendChild(item);
				}
			}
		} else {
			limits_Caption.setLabel(Labels.getLabel("title_west_bottom"));
		}
		logger.debug("Leaving");
	}

	private void doFillCustomerCollateral(String custCIF) {
		logger.debug("Entering");
		try {
			doFillCustomerEquationCollateral();
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
	}

	private void doFillCustomerEquationCollateral() {
		logger.debug("Entering");
		if (collateralsFromEquation != null && !collateralsFromEquation.isEmpty()) {
			int formatter = 6;
			for (CustomerCollateral customerCollateral : collateralsFromEquation) {
				Listitem item = new Listitem();
				Listcell cell;
				cell = new Listcell(customerCollateral.getCollType());
				cell.setParent(item);
				cell = new Listcell(customerCollateral.getCollReference());
				cell.setParent(item);
				cell = new Listcell(DateUtil.formatToLongDate((Date) customerCollateral.getColllastRvwDate()));
				cell.setParent(item);
				cell = new Listcell(customerCollateral.getCollCcy());
				cell.setParent(item);
				cell = new Listcell(CurrencyUtil.format(new BigDecimal(customerCollateral.getCollValue().toString())
						.divide(BigDecimal.valueOf(Math.pow(10, formatter)), RoundingMode.HALF_UP), 0));
				cell.setStyle("text-align:right;");
				cell.setParent(item);
				cell = new Listcell(CurrencyUtil.format(new BigDecimal(customerCollateral.getCollBankVal().toString())
						.divide(BigDecimal.valueOf(Math.pow(10, formatter)), RoundingMode.HALF_UP), 0));
				cell.setStyle("text-align:right;");
				cell.setParent(item);
				BigDecimal per = new BigDecimal(customerCollateral.getCollBankValMar().toString())
						.divide(BigDecimal.valueOf(Math.pow(10, 2)), RoundingMode.HALF_UP);
				cell = new Listcell(per + "%");
				cell.setStyle("text-align:right;");
				cell.setParent(item);
				cell = new Listcell(customerCollateral.getCollTypeDesc());
				cell.setParent(item);
				cell = new Listcell(customerCollateral.getCollComplete());
				cell.setParent(item);
				cell = new Listcell(DateUtil.formatToLongDate((Date) customerCollateral.getCollExpDate()));
				cell.setParent(item);
				item.setAttribute("data", customerCollateral);
				if (!enqModule) {
					ComponentsCtrl.applyForward(item, "onDoubleClick=onCollateralEnquiryItemDoubleClicked");
				}
				// cell = new
				// Listcell(customerCollateral.getColllocationDesc());
				// cell.setParent(item);
				// cell = new Listcell(customerCollateral.getColllocation());
				// cell.setParent(item);
				this.listBoxCollateral.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	// ================== Collateral ===========================//
	protected Listbox listBoxCollateral;
	protected Button button_CollateralList_NewCollateral;
	private List<Collateral> collateralsList = new ArrayList<Collateral>();

	public void doFillCollaterals(List<Collateral> collaterals) {
		logger.debug("Entering");
		try {
			if (collaterals != null) {
				getCollateralsList().clear();
				setCollateralsList(collaterals);
				fillCollaterals(collaterals);
				doFillCustomerEquationCollateral();
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
	}

	private void fillCollaterals(List<Collateral> collaterals) {
		logger.debug("Entering");
		this.listBoxCollateral.getItems().clear();
		for (Collateral collateral : collaterals) {
			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell(collateral.getCAFReference());
			lc.setParent(item);
			lc = new Listcell(collateral.getReference());
			lc.setParent(item);
			lc = new Listcell(collateral.getLastReview());
			lc.setParent(item);
			lc = new Listcell(collateral.getCurrency());
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.amountFormate(collateral.getValue(), collateral.getCcyFormat()));
			lc.setParent(item);
			lc = new Listcell(
					PennantApplicationUtil.amountFormate(collateral.getBankvaluation(), collateral.getCcyFormat()));
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.formatAmount(collateral.getBankmargin(), 2));
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.formatAmount(collateral.getProposedCoverage(), 2));
			lc.setParent(item);
			lc = new Listcell(collateral.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(collateral.getRecordType()));
			lc.setParent(item);
			item.setAttribute("data", collateral);
			if (!enqModule) {
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCollateralItemDoubleClicked");
			}
			this.listBoxCollateral.appendChild(item);
		}
		logger.debug("Leaving");
	}

	public void onClick$button_CollateralList_NewCollateral(Event event) {
		logger.debug("Entering" + event.toString());
		// create a new IncomeExpenseDetail object, We GET it from the backEnd.
		final Collateral aCollateral = getFacilityService().getNewCollateral();
		aCollateral.setCAFReference(this.cAFReference.getValue());
		aCollateral.setLastReview("New");
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("collateral", aCollateral);
		map.put("facilityDialogCtrl", this);
		map.put("role", getRole());
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Facility/Facility/CollateralDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onCollateralItemDoubleClicked(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Listitem item = (Listitem) event.getOrigin().getTarget();
		Collateral itemdata = (Collateral) item.getAttribute("data");
		if (!StringUtils.trimToEmpty(itemdata.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
			itemdata.setNewRecord(false);
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("collateral", itemdata);
			map.put("facilityDialogCtrl", this);
			map.put("role", getRole());
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/Facility/Facility/CollateralDialog.zul", null, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onCollateralEnquiryItemDoubleClicked(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Listitem item = (Listitem) event.getOrigin().getTarget();
		CustomerCollateral itemdata = (CustomerCollateral) item.getAttribute("data");
		// Nag
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("eqtnCollateral", itemdata);
		map.put("facilityDialogCtrl", this);
		map.put("role", getRole());
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Facility/Facility/CollateralEnquiryDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void setCollateralsList(List<Collateral> productDetail) {
		this.collateralsList = productDetail;
	}

	public List<Collateral> getCollateralsList() {
		return collateralsList;
	}

	// Customer Rating
	private List<CustomerRating> ratingsList = new ArrayList<CustomerRating>();

	public void setRatingsList(List<CustomerRating> ratingsList) {
		this.ratingsList = ratingsList;
	}

	public List<CustomerRating> getRatingsList() {
		return ratingsList;
	}

	public void onCustomerRatingItemDoubleClicked(Event event) {
		logger.debug("Entering");
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerRating.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerRating customerRating = (CustomerRating) item.getAttribute("data");
			customerRating.setNewRecord(false);
			if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("customerRating", customerRating);
				map.put("customerDialogCtrl", this);
				map.put("roleCode", getRole());
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/Facility/Facility/FacilityCustomerRatingDialog.zul",
							window_FacilityDialog, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving");
	}

	public Facility getAgrFacilitty() {
		logger.debug("Entering");
		doWriteComponentsToBean(getFacility());
		Facility aFacility = new Facility();
		aFacility = ObjectUtil.clone(getFacility());
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map.put("control", this);
			map.put("facility", aFacility);
			map.put("agreement", null);
			map.put("userAction", this.userAction.getSelectedItem().getLabel());
			if (tabpanelsIndexCenter.getFellowIfAny(MODULE_CHECKLIST + "TabPanel") != null) {
				Events.sendEvent("onChkListValidation",
						tabpanelsIndexCenter.getFellowIfAny(MODULE_CHECKLIST + "TabPanel").getFirstChild(), map);
			}
		} catch (Exception e) {
			logger.debug(e);

		}
		if (getFacilityScoringDetailDialogCtrl() != null) {
			getFacilityScoringDetailDialogCtrl().doSave_ScoreDetail(aFacility);
		}
		if (getFacilityDetailListCtrl() != null) {
			aFacility.setFacilityDetails(getFacilityDetailListCtrl().getFacilityDetailList());
		}
		logger.debug("Leaving");
		return aFacility;
	}

	private static final String MODULE_FACILITYDETAILS = "FacilityDetails";
	private static final String MODULE_CREDITREVIEW = "CreditReview";
	private static final String MODULE_SCORING = "Scoring";
	private static final String MODULE_AGREEMENTS = "Agreements";
	private static final String MODULE_DOCUMENTS = "Documents";
	private static final String MODULE_CHECKLIST = "CheckList";
	private static final String MODULE_RECOMMENDATIONS = "Recommendations";

	private void appendTabs(Facility aFacility) {
		logger.debug("Entering");
		// 1
		createTabAndTabpanel(MODULE_FACILITYDETAILS, "/WEB-INF/pages/Facility/Facility/FacilityDetailList.zul", false);
		// 2
		if (StringUtils.isNotBlank(getFacility().getCustCIF())) {
			String custCtg = StringUtils.trimToEmpty(getFacility().getCustCtgCode());
			if (custCtg.equals(PennantConstants.PFF_CUSTCTG_SME) || custCtg.equals(PennantConstants.PFF_CUSTCTG_CORP)) {
				createTabAndTabpanel(MODULE_CREDITREVIEW,
						"/WEB-INF/pages/FinanceManagement/BankOrCorpCreditReview/CreditApplicationReviewEnquiry.zul",
						true);
			}
		}
		// 3
		if (aFacility.getScoringGroupList() != null && !aFacility.getScoringGroupList().isEmpty()) {
			createTabAndTabpanel(MODULE_SCORING, "/WEB-INF/pages/Facility/Facility/FacilityScoringDetailDialog.zul",
					false);
		}
		// 4
		if (!enqModule && getFacility().getAggrementList() != null && !getFacility().getAggrementList().isEmpty()
				&& isAtleastOneToShow(aFacility.getAggrementList())) {
			createTabAndTabpanel(MODULE_AGREEMENTS,
					"/WEB-INF/pages/Facility/Facility/FacilityAgreementDetailDialog.zul", false);
		}
		// 5
		createTabAndTabpanel(MODULE_DOCUMENTS, "/WEB-INF/pages/Facility/Facility/FacilityDocumentDetailDialog.zul",
				false);
		// 6
		if (!enqModule && aFacility.getCheckList() != null && !aFacility.getCheckList().isEmpty()
				&& isAtleastOneToShow(aFacility.getCheckList())) {
			createTabAndTabpanel(MODULE_CHECKLIST,
					"/WEB-INF/pages/Facility/Facility/FacilityCheckListReferenceDialog.zul", false);
		}
		// 7
		if (!enqModule) {
			createTabAndTabpanel(MODULE_RECOMMENDATIONS, "/WEB-INF/pages/notes/notes.zul", false);
		}
		logger.debug("Leaving");
	}

	private boolean isAtleastOneToShow(List<FacilityReferenceDetail> checkList) {
		logger.debug("Entering");
		for (FacilityReferenceDetail facilityReferenceDetail : checkList) {
			if (isAllowedToShow(facilityReferenceDetail, getRole())) {
				logger.debug("Leaving");
				return true;
			}
		}
		logger.debug("Leaving");
		return false;
	}

	public boolean isAllowedToShow(FacilityReferenceDetail financeReferenceDetail, String userRole) {
		logger.debug("Entering");
		String showinStage = StringUtils.trimToEmpty(financeReferenceDetail.getShowInStage());
		if (showinStage.contains(",")) {
			String[] roles = showinStage.split(",");
			for (String string : roles) {
				if (userRole.equals(string)) {
					logger.debug("Leaving");
					return true;
				}
			}
		}
		logger.debug("Leaving");
		return false;
	}

	public void createTabAndTabpanel(String tabName, String zulPath, boolean loadOnDemand) {
		logger.debug("Entering");
		try {

			Tab tab = new Tab(Labels.getLabel("Tab_" + tabName));
			tab.setId(tabName + "Tab");
			tabsIndexCenter.appendChild(tab);
			if (loadOnDemand) {
				ComponentsCtrl.applyForward(tab, "onSelect=onSelectTab");
			}
			Tabpanel tabpanel = new Tabpanel();
			tabpanel.setId(tabName + "TabPanel");
			tabpanel.setHeight(this.borderLayoutHeight + "px");
			tabpanel.setStyle("overflow:auto");
			tabpanel.setParent(tabpanelsIndexCenter);
			if (!loadOnDemand) {
				renderComponent(zulPath, tabpanel);
			}
		} catch (Exception e) {
			logger.error("Exception: Opening window", e);
		}
		logger.debug("Leaving");
	}

	private void renderComponent(String zulPath, Tabpanel tabpanel) {
		logger.debug("Entering");
		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("isFinanceNotes", true);
			map.put("facility", getFacility());
			map.put("isRecommendMand", true);
			map.put("notes", getNotes(this.facility));
			map.put("control", this);
			map.put("custCIF", getFacility().getCustCIF());
			map.put("custID", getFacility().getCustID());
			map.put("userRole", getRole());
			map.put("custCtgType", getFacility().getCustCtgCode());
			if (enqModule) {
				map.put("enqModule", enqModule);
			}
			Executions.createComponents(zulPath, tabpanel, map);
		} catch (Exception e) {
			logger.error("Exception: Opening window", e);
		}
		logger.debug("Leaving");
	}

	public void onSelectTab(ForwardEvent event) {
		logger.debug("Entering");
		Tab tab = (Tab) event.getOrigin().getTarget();
		if (tab.getId().equals(MODULE_CREDITREVIEW + "Tab")) {
			Tabpanel tabpanel = (Tabpanel) tabpanelsIndexCenter.getFellowIfAny(MODULE_CREDITREVIEW + "TabPanel");
			if (tabpanel.getChildren().isEmpty()) {
				renderComponent(
						"/WEB-INF/pages/FinanceManagement/BankOrCorpCreditReview/CreditApplicationReviewEnquiry.zul",
						tabpanel);
			}
		}
		logger.debug("Leaving");
	}

	private void doCheckCustomerType() {
		logger.debug("Entering");
		if (StringUtils.trimToEmpty(getFacility().getCustCtgCode()).equals(PennantConstants.PFF_CUSTCTG_SME)) {
			this.row_accountRelation.setVisible(true);
			this.row_antiMoneyLaunderSection.setVisible(true);
			this.row_limitAndAncillary.setVisible(true);
			this.hlayout_antiMoneyLaunderClear.setVisible(true);
			this.label_antiMoneyLaunderClear.setVisible(true);
		} else {
			this.row_accountRelation.setVisible(false);
			this.row_antiMoneyLaunderSection.setVisible(false);
			this.row_limitAndAncillary.setVisible(false);
			this.hlayout_antiMoneyLaunderClear.setVisible(false);
			this.label_antiMoneyLaunderClear.setVisible(false);
		}
		logger.debug("Leaving");
	}

	private void doDesignByMode() {
		logger.debug("Entering");
		if (enqModule) {
			this.button_CollateralList_NewCollateral.setVisible(false);
		}
		logger.debug("Leaving");
	}

	private List<FacilityDetail> getDedupList(String cafReference, long custID) {
		JdbcSearchObject<FacilityDetail> jdbcSearchObject = new JdbcSearchObject<FacilityDetail>(FacilityDetail.class);
		jdbcSearchObject.addTabelName("FacilityDetails_Temp");
		jdbcSearchObject.addFilterNotEqual("CAFReference", cafReference);
		jdbcSearchObject.addFilterEqual("custID", custID);
		return getPagedListService().getBySearchObject(jdbcSearchObject);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	public Facility getFacility() {
		return this.facility;
	}

	public void setFacility(Facility facility) {
		this.facility = facility;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public FacilityService getFacilityService() {
		return this.facilityService;
	}

	public void setFacilityListCtrl(FacilityListCtrl facilityListCtrl) {
		this.facilityListCtrl = facilityListCtrl;
	}

	public FacilityListCtrl getFacilityListCtrl() {
		return this.facilityListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public void setCustomerLimitIntefaceService(CustomerLimitIntefaceService customerLimitIntefaceService) {
		this.customerLimitIntefaceService = customerLimitIntefaceService;
	}

	public CustomerLimitIntefaceService getCustomerLimitIntefaceService() {
		return customerLimitIntefaceService;
	}

	public void setFacilityDocumentDetailDialogCtrl(FacilityDocumentDetailDialogCtrl facilityDocumentDetailDialogCtrl) {
		this.facilityDocumentDetailDialogCtrl = facilityDocumentDetailDialogCtrl;
	}

	public FacilityDocumentDetailDialogCtrl getFacilityDocumentDetailDialogCtrl() {
		return facilityDocumentDetailDialogCtrl;
	}

	public void setFacilityCheckListReferenceDialogCtrl(
			FacilityCheckListReferenceDialogCtrl facilityCheckListReferenceDialogCtrl) {
		this.facilityCheckListReferenceDialogCtrl = facilityCheckListReferenceDialogCtrl;
	}

	public FacilityCheckListReferenceDialogCtrl getFacilityCheckListReferenceDialogCtrl() {
		return facilityCheckListReferenceDialogCtrl;
	}

	public void setFacilityScoringDetailDialogCtrl(FacilityScoringDetailDialogCtrl facilityScoringDetailDialogCtrl) {
		this.facilityScoringDetailDialogCtrl = facilityScoringDetailDialogCtrl;
	}

	public FacilityScoringDetailDialogCtrl getFacilityScoringDetailDialogCtrl() {
		return facilityScoringDetailDialogCtrl;
	}

	public void setFacilityDetailListCtrl(FacilityDetailListCtrl facilityDetailListCtrl) {
		this.facilityDetailListCtrl = facilityDetailListCtrl;
	}

	public FacilityDetailListCtrl getFacilityDetailListCtrl() {
		return facilityDetailListCtrl;
	}

	public FacilityAgreementDetailDialogCtrl getFacilityAgreementDetailDialogCtrl() {
		return facilityAgreementDetailDialogCtrl;
	}

	public void setFacilityAgreementDetailDialogCtrl(
			FacilityAgreementDetailDialogCtrl facilityAgreementDetailDialogCtrl) {
		this.facilityAgreementDetailDialogCtrl = facilityAgreementDetailDialogCtrl;
	}

	public String getReference() {
		return this.cAFReference.getValue();
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public boolean isRecommeandEntered() {
		return recommendEntered;
	}

	public void setRecommendEntered(Boolean recommendEntered) {
		this.recommendEntered = recommendEntered;
	}

}

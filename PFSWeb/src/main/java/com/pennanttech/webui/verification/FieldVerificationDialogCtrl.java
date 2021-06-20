package com.pennanttech.webui.verification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.systemmasters.AddressType;
import com.pennant.backend.service.customermasters.CustomerAddresService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.JointAccountDetailService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.verification.fieldinvestigation.FIInitiationListCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.pff.verification.Agencies;
import com.pennanttech.pennapps.pff.verification.Decision;
import com.pennanttech.pennapps.pff.verification.Module;
import com.pennanttech.pennapps.pff.verification.RequestType;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.WaiverReasons;
import com.pennanttech.pennapps.pff.verification.fi.FIStatus;
import com.pennanttech.pennapps.pff.verification.fi.TVStatus;
import com.pennanttech.pennapps.pff.verification.model.FieldInvestigation;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pennapps.pff.verification.service.FieldInvestigationService;
import com.pennanttech.pennapps.pff.verification.service.VerificationService;
import com.pennanttech.pennapps.web.util.MessageUtil;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FieldVerificationDialogCtrl extends GFCBaseCtrl<Verification> {
	private static final long serialVersionUID = 8661799804403963415L;
	private static final Logger logger = LogManager.getLogger(FieldVerificationDialogCtrl.class);

	protected Window window_FIVerificationDialog;
	protected Groupbox finBasicdetails;
	protected Listbox listBoxFIVerification;
	protected Groupbox fiInquiry;
	protected Radiogroup fi;
	protected Listheader listheader_FIVerification_ReInitAgency;
	protected Listheader listheader_FIVerification_ReInitRemarks;

	private Button btnFIInitiateSave;
	private Button btnFIInitiateClose;

	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private Verification verification;
	private FinanceDetail financeDetail;
	private List<Verification> customerVerifications = new ArrayList<>();
	private List<Verification> coApplicantVerifications = new ArrayList<>();
	private Set<String> requiredCodes = new HashSet<>();

	private transient List<Long> deletedJointAccountS;
	private transient boolean validationOn;
	private transient boolean initType;
	private boolean recSave;

	@Autowired
	private SearchProcessor searchProcessor;
	@Autowired
	private transient FieldInvestigationService fieldInvestigationService;
	@Autowired
	private transient VerificationService verificationService;
	@Autowired
	private transient CustomerDetailsService customerDetailsService;
	@Autowired
	private transient CustomerAddresService customerAddresService;
	@Autowired
	private RuleService ruleService;
	@Autowired
	private JointAccountDetailService jointAccountDetailService;

	private boolean fromVerification;
	private FIInitiationListCtrl fiInitiationListCtrl;

	/**
	 * default constructor.<br>
	 */
	public FieldVerificationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	public void onCreate$window_FIVerificationDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_FIVerificationDialog);

		if (arguments.get("finHeaderList") != null) {
			appendFinBasicDetails(arguments.get("finHeaderList"));
		}
		this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
		this.verification = financeDetail.getFiVerification();
		if (this.verification == null) {
			this.verification = new Verification();
			this.financeDetail.setFiVerification(this.verification);
		}
		this.verification.setKeyReference(financeDetail.getFinScheduleData().getFinReference());

		if (arguments.containsKey("financeMainBaseCtrl")) {
			((FinanceMainBaseCtrl) arguments.get("financeMainBaseCtrl")).setFieldVerificationDialogCtrl(this);
		} else {
			finBasicdetails.setVisible(false);
		}

		if (arguments.containsKey("fiInitiationListCtrl")) {
			fiInitiationListCtrl = (FIInitiationListCtrl) arguments.get("fiInitiationListCtrl");
			this.fromVerification = true;
			finBasicdetails.setVisible(true);
		}

		if (arguments.get("InitType") != null) {
			initType = (Boolean) arguments.get("InitType");
		}

		if (arguments.get("enqiryModule") != null) {
			enqiryModule = (Boolean) arguments.get("enqiryModule");
		}

		setFiRequiredCodes();
		doCheckRights();
		doShowDialog();

		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		if (fromVerification) {
			this.btnFIInitiateSave.setVisible(fromVerification);
			this.btnFIInitiateClose.setVisible(fromVerification);
		}
		if (enqiryModule) {
			if (btnFIInitiateSave != null) {
				this.btnFIInitiateSave.setVisible(!enqiryModule);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void doShowDialog() {
		logger.debug(Literal.ENTERING);

		setVerifications();
		renderFIVerificationList();
		if (initType) {
			this.btnFIInitiateSave.setVisible(fromVerification);
			this.btnFIInitiateClose.setVisible(fromVerification);
		}
		if (fromVerification) {
			setDialog(DialogType.EMBEDDED);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails(Object finHeaderList) {
		logger.debug(Literal.ENTERING);
		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", finHeaderList);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private org.zkoss.zk.ui.Component getComponent(Listitem listitem, String listcellId) {
		List<Listcell> listcels = listitem.getChildren();

		for (Listcell listcell : listcels) {
			String id = StringUtils.trimToNull(listcell.getId());

			if (id == null) {
				continue;
			}

			id = id.replaceAll("\\d", "");
			if (StringUtils.equals(id, listcellId)) {
				return listcell.getFirstChild();
			}
		}
		return null;
	}

	public void onChnageFiv(ForwardEvent event) throws Exception {
		Listitem listitem = (Listitem) event.getData();

		Combobox cfiv = (Combobox) getComponent(listitem, "RequestType");
		ExtendedCombobox cAgency = (ExtendedCombobox) getComponent(listitem, "Agency");
		ExtendedCombobox cReason = (ExtendedCombobox) getComponent(listitem, "Reason");

		onchangeVerificationType(cfiv, cAgency, cReason);
	}

	private void onchangeVerificationType(Combobox cfiv, ExtendedCombobox cAgency, ExtendedCombobox cReason) {
		RequestType type;
		type = RequestType.getType(Integer.parseInt(cfiv.getSelectedItem().getValue()));

		switch (type) {
		case INITIATE:
			cAgency.setReadonly(false);
			cReason.setValue("");
			cReason.setReadonly(true);
			break;
		case WAIVE:
			cAgency.setValue("");
			cAgency.setReadonly(true);
			cReason.setReadonly(false);
			break;
		case NOT_REQUIRED:
			cAgency.setValue("");
			cAgency.setReadonly(true);
			cReason.setReadonly(false);
			break;
		default:
			cAgency.setValue("");
			cReason.setValue("");
			cAgency.setReadonly(true);
			cReason.setReadonly(true);
			break;
		}
	}

	public void onChangeAgency(ForwardEvent event) throws Exception {
		Listitem listitem = (Listitem) event.getData();
		ExtendedCombobox agency = (ExtendedCombobox) getComponent(listitem, "Agency");
		Object dataObject = agency.getObject();

		if (dataObject instanceof String) {
			agency.setValue(dataObject.toString());
			agency.setDescription("");
		} else {
			VehicleDealer vehicleDealer = (VehicleDealer) dataObject;
			if (vehicleDealer != null) {
				agency.setAttribute("agencyId", vehicleDealer.getId());
			}
		}
	}

	public void onChangeReInitAgency(ForwardEvent event) throws Exception {
		Listitem listitem = (Listitem) event.getData();
		ExtendedCombobox agency = (ExtendedCombobox) getComponent(listitem, "ReInitAgency");
		Object dataObject = agency.getObject();

		if (dataObject instanceof String) {
			agency.setValue(dataObject.toString());
			agency.setDescription("");
		} else {
			VehicleDealer vehicleDealer = (VehicleDealer) dataObject;
			if (vehicleDealer != null) {
				agency.setAttribute("reInitAgencyId", vehicleDealer.getId());
			}
		}
	}

	public void onChangeReason(ForwardEvent event) throws Exception {
		Listitem listitem = (Listitem) event.getData();
		ExtendedCombobox reason = (ExtendedCombobox) getComponent(listitem, "Reason");
		Object dataObject = reason.getObject();

		if (dataObject instanceof String) {
			reason.setValue(dataObject.toString());
			reason.setDescription("");
		} else {
			ReasonCode reasonCode = (ReasonCode) dataObject;
			if (reasonCode != null) {
				reason.setAttribute("reasonId", reasonCode.getId());
			}
		}
	}

	public void onChangeDecision(ForwardEvent event) throws Exception {
		Listitem listitem = (Listitem) event.getData();
		ExtendedCombobox reInitAgency = (ExtendedCombobox) getComponent(listitem, "ReInitAgency");
		Combobox decisionBox = (Combobox) getComponent(listitem, "Decision");
		Decision decision;
		try {
			decision = Decision.getType(Integer.parseInt(decisionBox.getSelectedItem().getValue()));
		} catch (Exception e) {
			String value = decisionBox.getValue();
			decisionBox.setValue("");
			throw new WrongValueException(decisionBox,
					Labels.getLabel("STATIC_INVALID", new String[] { value + " is not valid," }));
		}

		if (decision.getKey() == Decision.RE_INITIATE.getKey()) {
			reInitAgency.setReadonly(false);
		} else {
			reInitAgency.setValue("");
			reInitAgency.setReadonly(true);
		}
	}

	public void onCheck$fi(Event event) {
		FieldInvestigation fieldInvestigation = null;
		if (fiInquiry.getChildren().size() >= 2) {
			fiInquiry.getChildren().remove(1);
		}

		final Map<String, Object> map = new HashMap<>();
		fieldInvestigation = fieldInvestigationService.getFieldInvestigation(fi.getSelectedItem().getValue(), "_View");
		if (fieldInvestigation != null && StringUtils.isEmpty(fieldInvestigation.getNextRoleCode())) {
			map.put("LOAN_ORG", true);
			map.put("fieldInvestigation", fieldInvestigation);
			Executions.createComponents("/WEB-INF/pages/Verification/FieldInvestigation/FieldInvestigationDialog.zul",
					fiInquiry, map);
		} else if (fieldInvestigation != null) {
			MessageUtil.showMessage("Verification is not yet completed.");
		} else {
			MessageUtil.showMessage("Initiation request not available.");
		}
	}

	/**
	 * Method to fill FI Initiation/Approval tab.
	 * 
	 * @param customer
	 */
	public void renderFIVerificationList() {
		logger.debug(Literal.ENTERING);

		if (this.listBoxFIVerification.getItems() != null) {
			this.listBoxFIVerification.getItems().clear();
		}

		// set Initiated flag to initiated Records
		setInitiated(this.verification.getVerifications());

		// Render Verifications
		int i = 0;
		for (Verification vrf : this.verification.getVerifications()) {
			i++;
			Listitem item = new Listitem();
			Listcell listCell;
			if (!initType && !fromVerification) {

				// Radio Button
				listCell = new Listcell();
				listCell.setId("select".concat(String.valueOf(i)));
				Radio select = new Radio();
				select.setRadiogroup(fi);
				select.setValue(vrf.getId());
				listCell.appendChild(select);
				listCell.setParent(item);
			}

			// Applicant Type
			listCell = new Listcell();
			listCell.setId("ReferenceType".concat(String.valueOf(i)));
			listCell.appendChild(new Label(vrf.getReferenceType()));
			listCell.setParent(item);

			// CIF
			listCell = new Listcell();
			listCell.setId("Reference".concat(String.valueOf(i)));
			listCell.appendChild(new Label(vrf.getCif()));
			listCell.setParent(item);

			// Customer Name
			listCell = new Listcell(vrf.getCustomerName());
			listCell.setParent(item);

			// Address Type
			listCell = new Listcell();
			A addrLink = new A();
			listCell.setId("ReferenceFor".concat(String.valueOf(i)));
			addrLink.setLabel(vrf.getReferenceFor());
			addrLink.addForward("onClick", self, "onClickAddressType", vrf);
			addrLink.setStyle("text-decoration:underline;");
			listCell.appendChild(addrLink);
			listCell.setParent(item);

			// FIVA
			listCell = new Listcell();
			listCell.setId("RequestType".concat(String.valueOf(i)));
			Combobox requestType = new Combobox();
			requestType.setReadonly(true);
			requestType.setValue(String.valueOf(vrf.getRequestType()));

			List<ValueLabel> list = new ArrayList<>();
			int reqType = vrf.getRequestType();
			if (reqType == RequestType.NOT_REQUIRED.getKey() && requiredCodes.contains(vrf.getReferenceFor())) {
				list = RequestType.getList();
			} else if (reqType == RequestType.WAIVE.getKey() && !requiredCodes.contains(vrf.getReferenceFor())) {
				list = RequestType.getList();
			} else if (requiredCodes.contains(vrf.getReferenceFor())) {
				for (ValueLabel valueLabel : RequestType.getList()) {
					if (Integer.parseInt(valueLabel.getValue()) != RequestType.NOT_REQUIRED.getKey()) {
						list.add(valueLabel);
					}
				}
			} else {
				for (ValueLabel valueLabel : RequestType.getList()) {
					if (Integer.parseInt(valueLabel.getValue()) != RequestType.WAIVE.getKey()) {
						list.add(valueLabel);
					}
				}
			}

			fillComboBox(requestType, reqType, list);

			requestType.setParent(listCell);
			listCell.setParent(item);

			// Agency
			listCell = new Listcell();
			listCell.setId("Agency".concat(String.valueOf(i)));
			ExtendedCombobox agency = new ExtendedCombobox();
			if (vrf.getAgencyName() != null) {
				agency.setValue(String.valueOf(vrf.getAgencyName()));
				agency.setAttribute("agencyId", vrf.getAgency());
				agency.setAttribute("oldAgencyId", vrf.getAgency());
				agency.setAttribute("agencyName", vrf.getAgencyName());
			}
			fillAgencies(agency, vrf.getCity());
			listCell.appendChild(agency);
			listCell.setParent(item);

			// Reason
			listCell = new Listcell();
			listCell.setId("Reason".concat(String.valueOf(i)));
			ExtendedCombobox reason = new ExtendedCombobox();
			if (vrf.getReasonName() != null) {
				reason.setValue(vrf.getReasonName());
				reason.setAttribute("reasonId", vrf.getReason());
			}
			fillReasons(reason);
			listCell.appendChild(reason);
			listCell.setParent(item);

			// Remarks
			listCell = new Listcell();
			listCell.setId("Remarks".concat(String.valueOf(i)));
			Textbox remarks = new Textbox(vrf.getRemarks());
			remarks.setMaxlength(500);
			listCell.appendChild(remarks);
			listCell.setParent(item);

			if (initType) {
				// Last Verification Agency
				listCell = new Listcell();
				listCell.appendChild(new Label(vrf.getLastAgency()));
				listCell.setParent(item);
			}

			// Status
			listCell = new Listcell();
			Label status = new Label();

			if (initType && vrf.getLastStatus() != 0) {
				status.setValue(TVStatus.getType(vrf.getLastStatus()).getValue());

			} else if (!initType && vrf.getStatus() != 0) {
				status.setValue(TVStatus.getType(vrf.getStatus()).getValue());
			}

			listCell.appendChild(status);
			listCell.setParent(item);

			// Verification Date
			listCell = new Listcell();
			if (initType) {
				listCell.appendChild(new Label(DateUtil.formatToShortDate(vrf.getLastVerificationDate())));
			} else {
				listCell.appendChild(new Label(DateUtil.formatToShortDate(vrf.getVerificationDate())));
			}
			listCell.setParent(item);

			if (!initType) {
				// Decision
				listCell = new Listcell();
				listCell.setId("Decision".concat(String.valueOf(i)));
				Combobox decision = new Combobox();
				decision.setReadonly(true);
				if (Decision.getType(vrf.getDecision()) != null) {
					decision.setValue(String.valueOf(Decision.getType(vrf.getDecision()).getValue()));
				}
				fillDecision(vrf, decision);

				decision.setParent(listCell);
				listCell.setParent(item);

				// Re-Initiation Agency
				listCell = new Listcell();
				listCell.setId("ReInitAgency".concat(String.valueOf(i)));
				ExtendedCombobox reInitAgency = new ExtendedCombobox();
				reInitAgency.setReadonly(true);
				fillAgencies(reInitAgency, vrf.getCity());
				listCell.appendChild(reInitAgency);
				listCell.setParent(item);

				// Re-Initiation Remarks
				listCell = new Listcell();
				listCell.setId("ReInitRemarks".concat(String.valueOf(i)));
				Textbox reInitRemarks = new Textbox();
				reInitRemarks.setMaxlength(500);
				reInitRemarks.setValue(vrf.getDecisionRemarks());
				listCell.appendChild(reInitRemarks);
				listCell.setParent(item);

				decision.addForward("onChange", self, "onChangeDecision", item);
				reInitAgency.addForward("onFulfill", self, "onChangeReInitAgency", item);

				if (vrf.getDecision() == Decision.RE_INITIATE.getKey()) {
					decision.setDisabled(true);
					reInitAgency.setReadonly(true);
					reInitRemarks.setReadonly(true);
				}

				if (enqiryModule) {
					decision.setDisabled(true);
				}
			}

			requestType.addForward("onChange", self, "onChnageFiv", item);
			agency.addForward("onFulfill", self, "onChangeAgency", item);
			reason.addForward("onFulfill", self, "onChangeReason", item);

			onchangeVerificationType(requestType, agency, reason);

			item.setAttribute("verification", vrf);

			this.listBoxFIVerification.appendChild(item);

			if (!initType || vrf.isInitiated()) {
				requestType.setDisabled(true);
				agency.setReadonly(true);
				reason.setReadonly(true);
				remarks.setReadonly(true);
			}

			if (enqiryModule) {
				if (!fromVerification) {
					listheader_FIVerification_ReInitAgency.setVisible(false);
					listheader_FIVerification_ReInitRemarks.setVisible(false);
				}
				requestType.setDisabled(true);
				agency.setReadonly(true);
				reason.setReadonly(true);
				remarks.setReadonly(true);
			}

			if (SysParamUtil.isAllowed("VERIFICATION_FI_AUTO")) {
				setAutoAgency(requestType, agency);
			}
		}

		logger.debug(Literal.LEAVING);

	}

	private void setAutoAgency(Combobox requestType, ExtendedCombobox agency) {
		if (checkVerificationRule()) {
			List<VehicleDealer> vehicleDealerList = getDealerObject();
			VehicleDealer vehicleDealer = null;
			if (vehicleDealerList != null) {
				if (vehicleDealerList.size() > 0) {
					vehicleDealer = getDealerObject().get(0);
				}
			}

			if (vehicleDealer != null) {
				agency.setAttribute("agencyId", vehicleDealer.getDealerId());
				agency.setValue(vehicleDealer.getDealerName());
				agency.setReadonly(true);
				requestType.setSelectedIndex(0);
				requestType.setDisabled(true);
			}
		}
	}

	private void setInitiated(List<Verification> verifications) {
		for (Verification item : verifications) {
			if (item.getRequestType() == RequestType.INITIATE.getKey()
					&& verificationService.isVerificationInRecording(item, VerificationType.FI)) {
				item.setInitiated(true);
			}
		}
	}

	private void fillDecision(Verification vrf, Combobox decision) {
		List<ValueLabel> decisionList = new ArrayList<>();
		int requestType = vrf.getRequestType();
		int status = vrf.getStatus();

		if (requestType == RequestType.NOT_REQUIRED.getKey() || status == FIStatus.POSITIVE.getKey()) {
			decisionList.add(new ValueLabel(String.valueOf(Decision.OVERRIDE.getKey()), Decision.OVERRIDE.getValue()));
			decisionList.add(new ValueLabel(String.valueOf(Decision.SELECT.getKey()), Decision.SELECT.getValue()));
			if (vrf.getDecision() == Decision.SELECT.getKey()) {
				vrf.setDecision(Decision.APPROVE.getKey());
			}
			fillComboBox(decision, vrf.getDecision(), filterDecisions(decisionList));
		} else if (status == FIStatus.NEGATIVE.getKey() || status == FIStatus.REFER_TO_CREDIT.getKey()) {
			decisionList.add(new ValueLabel(String.valueOf(Decision.APPROVE.getKey()), Decision.APPROVE.getValue()));
			if (vrf.getDecision() == Decision.APPROVE.getKey()) {
				vrf.setDecision(Decision.SELECT.getKey());
			}
			fillComboBox(decision, vrf.getDecision(), filterDecisions(decisionList));
		} else if (requestType == RequestType.WAIVE.getKey()) {
			decisionList.add(new ValueLabel(String.valueOf(Decision.OVERRIDE.getKey()), Decision.OVERRIDE.getValue()));
			fillComboBox(decision, vrf.getDecision(), filterDecisions(decisionList));
		} else {
			fillComboBox(decision, vrf.getDecision(), Decision.getList());
		}
	}

	private List<ValueLabel> filterDecisions(List<ValueLabel> list) {
		boolean flag = true;
		List<ValueLabel> decisionList = new ArrayList<>();
		for (ValueLabel decValueLabel : Decision.getList()) {
			for (ValueLabel valueLabel : list) {
				if (Integer.parseInt(valueLabel.getValue()) == Integer.parseInt(decValueLabel.getValue())) {
					flag = false;
				}
			}
			if (flag) {
				decisionList.add(decValueLabel);
			}
			flag = true;
		}
		return decisionList;
	}

	private void fillAgencies(ExtendedCombobox agency, String city) {
		logger.debug(Literal.ENTERING);
		agency.setModuleName("VerificationAgencies");
		agency.setValueColumn("DealerName");
		agency.setValidateColumns(new String[] { "DealerName" });
		Filter[] agencyFilter = new Filter[1];
		agencyFilter[0] = new Filter("DealerType", Agencies.FIAGENCY.getKey(), Filter.OP_EQUAL);
		if (ImplementationConstants.VER_AGENCY_FILTER_BY_CITY && StringUtils.isNotBlank(city)) {
			agencyFilter = Arrays.copyOf(agencyFilter, agencyFilter.length + 1);
			//Applying city filter based on customer Address city code
			agencyFilter[1] = new Filter("DealerCity", city, Filter.OP_EQUAL);
		}
		agency.setFilters(agencyFilter);
		logger.debug(Literal.LEAVING);
	}

	private void fillReasons(ExtendedCombobox reason) {
		logger.debug(Literal.ENTERING);

		reason.setModuleName("VerificationWaiverReason");
		reason.setValueColumn("Code");
		reason.setValidateColumns(new String[] { "Code" });
		Filter[] reasonFilter = new Filter[1];
		reasonFilter[0] = new Filter("ReasonTypecode", WaiverReasons.FIWRES.getKey(), Filter.OP_EQUAL);
		reason.setFilters(reasonFilter);

		logger.debug(Literal.LEAVING);
	}

	public void addCustomerAddresses(List<CustomerAddres> addresses, boolean flag) {
		if (!initType) {
			return;
		}

		if (addresses != null) {
			CustomerDetails custDetails = new CustomerDetails();

			if (financeDetail.getCustomerDetails() != null) {
				BeanUtils.copyProperties(financeDetail.getCustomerDetails(), custDetails);
			}
			custDetails.setAddressList(addresses);
			customerVerifications.clear();
			customerVerifications.addAll(getFinalVerifications(custDetails, false));

			getTotalVerifications();
			if (flag) {
				renderFIVerificationList();
			}
		}
	}

	public void addCoApplicantAddresses(List<JointAccountDetail> jointAccountDetails, boolean flag) {
		if (!initType) {
			return;
		}
		List<CustomerDetails> customerDetails = new ArrayList<>();
		List<CustomerAddres> addresses = new ArrayList<>();
		deletedJointAccountS = new ArrayList<>();

		if (jointAccountDetails != null) {
			for (JointAccountDetail jointAccountDetail : jointAccountDetails) {
				customerDetails.add(customerDetailsService.getApprovedCustomerById(jointAccountDetail.getCustID()));
				if (!isNotDeleted(jointAccountDetail.getRecordType())) {
					deletedJointAccountS.add(jointAccountDetail.getCustID());
				}
			}

			coApplicantVerifications.clear();
			for (CustomerDetails custDetail : customerDetails) {
				addresses.addAll(getAddresses(custDetail));
				coApplicantVerifications.addAll(getFinalVerifications(custDetail, true));
			}

			getTotalVerifications();
			if (flag) {
				renderFIVerificationList();
			}
		}
	}

	private void getTotalVerifications() {
		this.verification.getVerifications().clear();
		this.verification.getVerifications().addAll(customerVerifications);
		this.verification.getVerifications().addAll(coApplicantVerifications);
	}

	private List<CustomerAddres> getAddresses(CustomerDetails customerDetails) {
		List<CustomerAddres> addresses = new ArrayList<>();
		if (customerDetails.getAddressList() != null) {
			for (CustomerAddres customerAddres : customerDetails.getAddressList()) {
				addresses.add(customerAddres);
			}
		}
		return addresses;
	}

	private void setVerifications() {
		if (initType) {
			addCustomerAddresses(financeDetail.getCustomerDetails().getAddressList(), false);
			addCoApplicantAddresses(financeDetail.getJointAccountDetailList(), false);
			getTotalVerifications();
		} else {
			this.verification.setVerifications(getOldVerifications(null));
		}
		renderFIVerificationList();
	}

	private List<Verification> getFinalVerifications(CustomerDetails customerDetails, boolean coApplicant) {
		boolean exists = false;
		Customer customer = customerDetails.getCustomer();
		List<CustomerAddres> addresses = customerDetails.getAddressList();
		List<Verification> verifications = new ArrayList<>();
		Map<String, Verification> addressMap = new HashMap<>();
		Map<String, Verification> newAddressMap = new HashMap<>();
		List<Verification> tempVerifications = new ArrayList<>();
		Set<String> deleteSet = new HashSet<>();
		Verification newVrf;

		// set deleted addresses of Co-Applicant
		if (coApplicant) {
			for (CustomerAddres addr : addresses) {
				if (deletedJointAccountS.contains(addr.getCustID())) {
					addr.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				}
			}
		}

		if (initType) {
			// Prepare Customer Addresses
			for (CustomerAddres address : addresses) {
				if (isNotDeleted(address.getRecordType())) {
					Verification object = getVerification(customer, address, coApplicant);
					addressMap.put(object.getReferenceFor(), object);
				} else {
					deleteSet.add(address.getCustAddrType());
				}
			}
		}

		// Set the old verification fields back.
		List<Verification> oldVerifications = getOldVerifications(customer.getCustCIF());
		tempVerifications.addAll(oldVerifications);
		for (Verification previous : tempVerifications) {
			if (previous.getReinitid() != null) {
				verifications.add(previous);
				oldVerifications.remove(previous);
				continue;
			}
			// create new Verification if initiated Address has changed
			Verification current = addressMap.get(previous.getReferenceFor());
			if (current != null) {
				for (CustomerAddres newAddress : addresses) {
					if (StringUtils.equals(newAddress.getCustAddrType(), previous.getReferenceFor())
							&& (newAddress.getCustID() == previous.getCustId())) {
						CustomerAddres oldAddres = customerAddresService.getCustomerAddresById(previous.getCustId(),
								previous.getReferenceFor());
						if (fieldInvestigationService.isAddressChange(oldAddres, newAddress)) {
							if (verificationService.isVerificationInRecording(previous, VerificationType.FI)) {
								newVrf = new Verification();
								BeanUtils.copyProperties(current, newVrf);
								newVrf.setNewRecord(true);
								verifications.add(newVrf);
							} else {
								exists = true;
							}
						}
						break;
					}

				}
				if (!exists) {
					setOldVerificationFields(current, previous);
				}
				exists = false;
				current.setId(previous.getId());
				current.setNewRecord(false);
				oldVerifications.remove(previous);
				newAddressMap.put(current.getReferenceFor(), current);
				addressMap.remove(current.getReferenceFor());
			}
		}

		verifications.addAll(addressMap.values());
		verifications.addAll(newAddressMap.values());
		verifications.addAll(oldVerifications);

		for (Verification item : verifications) {
			if ((deleteSet.contains(item.getReferenceFor())
					&& (item.isNew() || !verificationService.isVerificationInRecording(item, VerificationType.FI)))) {
				item.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
		}

		if (verifications.isEmpty()) {
			return verifications;
		}

		if (initType) {
			for (CustomerAddres addr : addresses) {
				for (Verification ver : verifications) {
					if (StringUtils.equals(addr.getCustAddrType(), ver.getReferenceFor())) {
						if (ver.isNew() || ver.getRequestType() == 0) {
							if (requiredCodes.contains(addr.getCustAddrType())) {
								ver.setRequestType(RequestType.INITIATE.getKey());
							} else {
								ver.setRequestType(RequestType.NOT_REQUIRED.getKey());
							}
						}
						verificationService.setLastStatus(ver);
					}
				}
			}
		}
		return verifications;
	}

	private List<Verification> getOldVerifications(String custCif) {
		List<Verification> verifications;
		List<Verification> result = new ArrayList<>();
		String keyReference = this.verification.getKeyReference();
		verifications = verificationService.getVerifications(keyReference, VerificationType.FI.getKey());
		if (custCif == null) {
			List<FieldInvestigation> fiList = fieldInvestigationService.getList(keyReference);
			for (Verification vrf : verifications) {
				for (FieldInvestigation item : fiList) {
					if (vrf.getId() == item.getVerificationId()) {
						vrf.setFieldInvestigation(item);
					}
				}
				if (vrf.getReferenceType().equals("Primary")) {
					result.add(vrf);
				} else if (vrf.getReferenceType().equals("Co-applicant")) {
					// getting co-applicants based on each verification
					JointAccountDetail coApplicant = jointAccountDetailService.getJointAccountDetailByRef(keyReference,
							vrf.getReference(), "_Temp");
					// retrieving verifications from verification_fi table
					FieldInvestigation fi = fieldInvestigationService.getFieldInvestigation(vrf.getId(), "_view");

					if (coApplicant != null) {
						result.add(vrf);
					} else {
						if (fi != null) {
							result.add(vrf);
						}
					}
				}
			}
			return result;
		}
		for (Verification item : verifications) {
			if (item.getReference().equals(custCif)) {
				result.add(item);
			}
		}
		return result;
	}

	private void setOldVerificationFields(Verification current, Verification previous) {
		current.setRequestType(previous.getRequestType());
		current.setAgency(previous.getAgency());
		current.setAgencyName(previous.getAgencyName());
		current.setReason(previous.getReason());
		current.setReasonName((previous.getReasonName()));
		current.setRemarks(previous.getRemarks());
		current.setDecision(previous.getDecision());
		current.setDecisionRemarks(previous.getDecisionRemarks());
		current.setReference(previous.getReference());
		current.setCreatedOn(previous.getCreatedOn());
		current.setVerificationType(VerificationType.FI.getKey());
	}

	private Verification getVerification(Customer customer, CustomerAddres address, boolean coApplicant) {
		Verification item = new Verification();

		item.setReferenceFor(address.getCustAddrType());
		item.setCif(customer.getCustCIF());
		item.setCustomerName(customer.getCustShrtName());
		item.setCustId(customer.getCustID());
		item.setReference(customer.getCustCIF());
		item.setKeyReference(this.verification.getKeyReference());
		if (coApplicant) {
			item.setReferenceType("Co-applicant");
		} else {
			item.setReferenceType("Primary");
		}
		item.setNewRecord(true);
		item.setVerificationType(VerificationType.FI.getKey());
		item.setCreatedBy(getUserWorkspace().getLoggedInUser().getUserId());
		item.setModule(Module.LOAN.getKey());
		//to set Agency Filter based on customer address city
		item.setCity(address.getCustAddrCity());
		verification.setCreatedOn(SysParamUtil.getAppDate());

		return item;
	}

	private boolean isNotDeleted(String recordType) {
		return !(PennantConstants.RECORD_TYPE_DEL.equals(recordType)
				|| PennantConstants.RECORD_TYPE_CAN.equals(recordType));
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		for (Listitem listitem : listBoxFIVerification.getItems()) {
			Combobox fivComboBox = (Combobox) getComponent(listitem, "RequestType");
			ExtendedCombobox agencyComboBox = (ExtendedCombobox) getComponent(listitem, "Agency");
			ExtendedCombobox reasonComboBox = (ExtendedCombobox) getComponent(listitem, "Reason");
			Combobox decision = (Combobox) getComponent(listitem, "Decision");
			ExtendedCombobox reInitagencyComboBox = (ExtendedCombobox) getComponent(listitem, "ReInitAgency");
			Textbox reInitRemarks = (Textbox) getComponent(listitem, "ReInitRemarks");
			Textbox remarks = (Textbox) getComponent(listitem, "Remarks");

			fivComboBox.clearErrorMessage();
			agencyComboBox.clearErrorMessage();
			reasonComboBox.clearErrorMessage();

			if (decision != null) {
				decision.clearErrorMessage();
			}
			if (reInitagencyComboBox != null) {
				reInitagencyComboBox.clearErrorMessage();
			}
			if (reInitRemarks != null) {
				reInitRemarks.clearErrorMessage();
			}
			if (remarks != null) {
				remarks.clearErrorMessage();
			}
		}
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		for (Listitem listitem : listBoxFIVerification.getItems()) {
			Combobox fivComboBox = (Combobox) getComponent(listitem, "RequestType");
			ExtendedCombobox agencyComboBox = (ExtendedCombobox) getComponent(listitem, "Agency");
			ExtendedCombobox reasonComboBox = (ExtendedCombobox) getComponent(listitem, "Reason");
			ExtendedCombobox reInitAgencyComboBox = (ExtendedCombobox) getComponent(listitem, "ReInitAgency");

			if (!fivComboBox.isDisabled()) {
				fivComboBox.setConstraint(
						new PTStringValidator(Labels.getLabel("label_FIVerificationDialog_FIV.value"), null, true));
			}

			if (!agencyComboBox.isReadonly()) {
				agencyComboBox.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FIVerificationDialog_Agency.value"), null, true, true));
			}

			if (Integer.parseInt(fivComboBox.getSelectedItem().getValue()) != RequestType.NOT_REQUIRED.getKey()) {
				if (!reasonComboBox.isReadonly()) {
					reasonComboBox.setConstraint(new PTStringValidator(
							Labels.getLabel("label_FIVerificationDialog_Reason.value"), null, true, true));
				}
			}

			if (!initType && !reInitAgencyComboBox.isReadonly()) {
				reInitAgencyComboBox.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FIVerificationDialog_Agency.value"), null, true, true));
			}
		}
		logger.debug("Leaving");
	}

	private void setValue(Listitem listitem, String comonentId) {
		Verification verification = (Verification) listitem.getAttribute("verification");

		switch (comonentId) {
		case "RequestType":
			String requestType = ((Combobox) getComponent(listitem, "RequestType")).getSelectedItem().getValue();
			verification.setRequestType(Integer.parseInt(requestType));
			break;
		case "Agency":
			ExtendedCombobox agency = ((ExtendedCombobox) getComponent(listitem, "Agency"));
			if (StringUtils.isNotEmpty(agency.getValue())) {
				verification.setAgency(Long.parseLong(agency.getAttribute("agencyId").toString()));
			} else {
				verification.setAgency(null);
			}
			break;
		case "Reason":
			ExtendedCombobox reason = ((ExtendedCombobox) getComponent(listitem, "Reason"));
			if (StringUtils.isNotEmpty(reason.getValue())) {
				verification.setReason(Long.parseLong(reason.getAttribute("reasonId").toString()));
			} else {
				verification.setReason(null);
			}
		case "Remarks":
			if (userAction != null) {
				if (!userAction.getSelectedItem().getValue().toString().contains("Resubmit")) {
					Textbox remarks = (Textbox) getComponent(listitem, "Remarks");
					verification.setRemarks(remarks.getValue());
					if (ImplementationConstants.VER_INITATE_REMARKS_MANDATORY) {
						if (verification.getRequestType() == RequestType.NOT_REQUIRED.getKey()
								&& StringUtils.isEmpty(verification.getRemarks())) {
							throw new WrongValueException(remarks,
									"Remarks are mandatory when Verification is Not Required");
						}
					}
				}
			}
			break;
		case "Decision":
			Combobox combobox = (Combobox) getComponent(listitem, "Decision");
			int decision = Integer.parseInt(getComboboxValue(combobox).toString());
			if (combobox.isDisabled()) {
				verification.setIgnoreFlag(true);
			}
			verification.setDecision(decision);
			if (!combobox.isDisabled() && decision == 0 && !this.recSave) {
				throw new WrongValueException(combobox,
						Labels.getLabel("STATIC_INVALID", new String[] { "Decision should be mandatory" }));
			}
			break;
		case "ReInitAgency":
			ExtendedCombobox reInitAgency = ((ExtendedCombobox) getComponent(listitem, "ReInitAgency"));
			if (StringUtils.isNotEmpty(reInitAgency.getValue())) {
				verification.setReInitAgency(Long.parseLong(reInitAgency.getAttribute("reInitAgencyId").toString()));
			} else {
				verification.setReInitAgency(null);
			}
			break;
		case "ReInitRemarks":
			Textbox textbox = (Textbox) getComponent(listitem, "ReInitRemarks");
			verification.setDecisionRemarks(textbox.getValue());
			if (verification.getDecision() == Decision.OVERRIDE.getKey()
					&& StringUtils.isEmpty(verification.getDecisionRemarks())) {
				throw new WrongValueException(textbox, Labels.getLabel("label_OVERRIDE_Validation"));
			}
			break;
		default:
			break;
		}
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param verification
	 * @return
	 */
	public List<WrongValueException> doWriteComponentsToBean() {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<>();

		if (this.userAction != null && this.userAction.getSelectedItem().getLabel().contains("Resubmit")) {
			return wve;
		}

		for (Listitem listitem : listBoxFIVerification.getItems()) {
			try {
				setValue(listitem, "RequestType");
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				setValue(listitem, "Agency");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				setValue(listitem, "Reason");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				setValue(listitem, "Remarks");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			if (!initType) {
				try {
					setValue(listitem, "Decision");
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					setValue(listitem, "ReInitAgency");
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					setValue(listitem, "ReInitRemarks");
				} catch (WrongValueException we) {
					wve.add(we);
				}
			}
		}

		doRemoveValidation();

		logger.debug("Leaving");
		return wve;

	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		setValidationOn(false);
		for (Listitem listitem : listBoxFIVerification.getItems()) {
			Combobox fivComboBox = (Combobox) getComponent(listitem, "RequestType");
			ExtendedCombobox agencyComboBox = (ExtendedCombobox) getComponent(listitem, "Agency");
			ExtendedCombobox reasonComboBox = (ExtendedCombobox) getComponent(listitem, "Reason");
			Combobox decision = (Combobox) getComponent(listitem, "Decision");
			ExtendedCombobox reInitagencyComboBox = (ExtendedCombobox) getComponent(listitem, "ReInitAgency");
			Textbox reInitRemarks = (Textbox) getComponent(listitem, "ReInitRemarks");

			fivComboBox.setConstraint("");
			agencyComboBox.setConstraint("");
			reasonComboBox.setConstraint("");

			if (decision != null) {
				decision.setConstraint("");
			}
			if (reInitagencyComboBox != null) {
				reInitagencyComboBox.setConstraint("");
			}
			if (reInitRemarks != null) {
				reInitRemarks.setConstraint("");
			}

		}

		logger.debug(Literal.LEAVING);

	}

	private void fillComboBox(Combobox combobox, int value, List<ValueLabel> list) {
		combobox.getChildren().clear();
		for (ValueLabel valueLabel : list) {
			Comboitem comboitem = new Comboitem();
			comboitem.setValue(valueLabel.getValue());
			comboitem.setLabel(valueLabel.getLabel());
			combobox.appendChild(comboitem);
			if (Integer.parseInt(valueLabel.getValue()) == value) {
				combobox.setSelectedItem(comboitem);
			}
		}
	}

	/**
	 * Writes the showErrorDetails method for .<br>
	 * displaying exceptions if occured
	 */
	private void showErrorDetails(List<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");

		doRemoveValidation();

		if (!wve.isEmpty()) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			if (tab != null) {
				tab.setSelected(true);
			}
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	public boolean doSave(FinanceDetail financeDetail, Tab tab, boolean recSave, Radiogroup userAction)
			throws InterruptedException {
		logger.debug(Literal.ENTERING);
		this.userAction = userAction;
		this.recSave = recSave;
		List<Verification> list = new ArrayList<>();
		doClearMessage();

		if (ImplementationConstants.VER_INITATE_DURING_SAVE) {
			doSetValidation();
		} else if (!recSave) {
			doSetValidation();
		}

		List<WrongValueException> wve = doWriteComponentsToBean();

		if (!wve.isEmpty() && tab != null) {
			tab.setSelected(true);
		}

		showErrorDetails(wve, tab);

		this.verification.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		for (Verification item : this.verification.getVerifications()) {
			if (item.getReinitid() != null && item.getRequestType() == RequestType.WAIVE.getKey()) {
				continue;
			}
			list.add(item);
		}
		this.verification.setVerifications(list);
		financeDetail.setFiVerification(this.verification);

		logger.debug("Leaving");
		if (tab != null && tab.getId().equals("TAB".concat(AssetConstants.UNIQUE_ID_FIAPPROVAL))) {
			return validateReinitiation(financeDetail.getFiVerification().getVerifications());
		}
		return true;
	}

	private boolean validateReinitiation(List<Verification> verifications) {
		int days = SysParamUtil.getValueAsInt(SMTParameterConstants.VER_FI_VALIDITY_DAYS);
		Date appDate = SysParamUtil.getAppDate();

		for (Verification verification : verifications) {
			if (verification.getDecision() == Decision.RE_INITIATE.getKey()
					&& !userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_SAVED)
					&& verification.getReinitid() == null) {
				MessageUtil.showError("Field Investigation Re-Initiation is allowed only when user action is save");
				return false;
			}

			if (days > 0) {
				int diff = DateUtil.getDaysBetween(appDate, verification.getVerificationDate());
				//FI Verification validity check
				if (verification.getDecision() == Decision.APPROVE.getKey() && diff > days && !recSave) {
					StringBuilder error = new StringBuilder("For ");
					error.append(Labels.getLabel("listheader_FIVerification_ApplicantType.label")).append(": ")
							.append(verification.getReferenceType()).append(", ");
					error.append(Labels.getLabel("listheader_FIVerification_CIF.label")).append(": ")
							.append(verification.getCif()).append(", ");
					error.append(Labels.getLabel("listheader_FIVerification_Name.label")).append(": ")
							.append(verification.getCustomerName()).append(", ");
					error.append(Labels.getLabel("listheader_FIVerification_AddressType.label")).append(": ")
							.append(verification.getReferenceFor()).append(", ");
					error.append(Labels.getLabel("label_FI_Verification_Exp"));
					if (MessageUtil.confirm(error.toString()) == MessageUtil.NO) {
						return false;
					}
				}

				if (verification.getDecision() == Decision.APPROVE.getKey() && !recSave
						&& ImplementationConstants.ALW_VERIFICATION_SYNC) {
					FieldInvestigation fieldInvestigation = fieldInvestigationService
							.getFieldInvestigation(verification.getId(), "_View");
					verification.setFieldInvestigation(fieldInvestigation);
				}

			}
		}
		return true;
	}

	private void setFiRequiredCodes() {
		this.requiredCodes.clear();

		Search search = new Search(AddressType.class);
		search.addField("addrtypecode");
		search.addTabelName("bmtaddresstypes");
		search.addFilter(new Filter("addrtypefirequired", 1));
		List<AddressType> list = searchProcessor.getResults(search);

		for (AddressType addressType : list) {
			requiredCodes.add(addressType.getAddrTypeCode());
		}
	}

	private boolean checkVerificationRule() {
		boolean isverified = false;
		Rule rule = ruleService.getApprovedRuleById("VERRULE", RuleConstants.MODULE_VERRULE,
				RuleConstants.EVENT_VERRULE);

		if (rule == null) {
			return false;
		}

		Map<String, Object> dataMap = new HashMap<String, Object>();
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		Customer customer = customerDetails.getCustomer();

		dataMap.putAll(PennantApplicationUtil.getExtendedFieldsDataMap(financeDetail));
		dataMap.putAll(PennantApplicationUtil.getExtendedFieldsDataMap(customerDetails));
		dataMap.putAll(customer.getDeclaredFieldValues());
		dataMap.putAll(financeMain.getDeclaredFieldValues());
		isverified = (boolean) RuleExecutionUtil.executeRule(rule.getSQLRule(), dataMap, financeMain.getFinCcy(),
				RuleReturnType.BOOLEAN);
		return isverified;
	}

	private List<VehicleDealer> getDealerObject() {
		logger.debug(Literal.LEAVING);
		String branchcode = financeDetail.getCustomerDetails().getCustomer().getCustDftBranch();
		branchcode = "%" + branchcode + "%";
		Search search = new Search(com.pennant.backend.model.amtmasters.VehicleDealer.class);
		search.addField("DealerId");
		search.addField("DealerName");
		search.addTabelName("AMTVehicleDealer_AView");
		search.addFilter(new Filter("DealerType", Agencies.FIAGENCY.getKey()));
		search.addFilter(new Filter("Branchcode", branchcode, Filter.OP_LIKE));
		search.addFilter(new Filter("Active", 1));
		List<com.pennant.backend.model.amtmasters.VehicleDealer> list = searchProcessor.getResults(search);
		return list;
	}

	public class PhonePriority implements Comparator<CustomerPhoneNumber> {
		@Override
		public int compare(CustomerPhoneNumber o1, CustomerPhoneNumber o2) {
			return o2.getPhoneTypePriority() - o1.getPhoneTypePriority();
		}

	}

	//++++++++++Technical verification initiation out side the loan start++++++++++++++++++++//
	/*
	 * Saving the Technical verification initiation out side the loan.
	 */
	public void onClick$btnFIInitiateSave(Event event) {
		logger.debug(Literal.ENTERING);
		try {
			doSave(financeDetail, null, recSave, fi);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			verificationService.saveOrUpdate(financeDetail, VerificationType.FI, PennantConstants.TRAN_WF, initType);
			refreshList();
			String msg = Labels.getLabel("FI_INITIATION",
					new String[] { financeDetail.getFinScheduleData().getFinReference() });
			Clients.showNotification(msg, "info", null, null, -1);
			closeDialog();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * To Render customer address details
	 */
	public void onClickAddressType(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Verification details = (Verification) event.getData();
		if (details != null) {
			CustomerAddres addrDetails = customerAddresService.getCustomerAddresById(details.getCustId(),
					details.getReferenceFor());
			if (addrDetails != null) {
				addrDetails.setLovDescCustCIF(details.getCif());
				addrDetails.setLovDescCustShrtName(details.getCustomerName());
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("customerAddres", addrDetails);
				map.put("roleCode", getRole());
				map.put("moduleType", PennantConstants.MODULETYPE_ENQ);
				map.put("fieldVerificationDialogCtrl", this);
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents(
							"/WEB-INF/pages/CustomerMasters/CustomerAddres/CustomerAddresDialog.zul", null, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			} else {
				MessageUtil.showMessage(Labels.getLabel("label_FieldInvestigationList_AddressType_Error.value"));
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void refreshList() {
		fiInitiationListCtrl.search();
	}

	public void onClick$btnFIInitiateClose(Event event) {
		doClose(this.btnFIInitiateSave.isVisible());
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		finBasicDetailsCtrl.doWriteBeanToComponents(finHeaderList);
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public Verification getVerification() {
		return verification;
	}

	public void setVerification(Verification verification) {
		this.verification = verification;
	}

	public boolean isValidationOn() {
		return validationOn;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public RuleService getRuleService() {
		return ruleService;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

}

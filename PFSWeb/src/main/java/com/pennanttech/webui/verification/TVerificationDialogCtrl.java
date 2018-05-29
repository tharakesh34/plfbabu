package com.pennanttech.webui.verification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.pff.verification.Agencies;
import com.pennanttech.pennapps.pff.verification.Decision;
import com.pennanttech.pennapps.pff.verification.RequestType;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.WaiverReasons;
import com.pennanttech.pennapps.pff.verification.fi.TVStatus;
import com.pennanttech.pennapps.pff.verification.model.TechnicalVerification;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pennapps.pff.verification.service.TechnicalVerificationService;
import com.pennanttech.pennapps.pff.verification.service.VerificationService;
import com.pennanttech.pennapps.web.util.MessageUtil;

@Component(value = "tVerificationDialogCtrl")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TVerificationDialogCtrl extends GFCBaseCtrl<Verification> {
	private static final long serialVersionUID = 8661799804403963415L;
	private static final Logger logger = LogManager.getLogger(TVerificationDialogCtrl.class);

	protected Window window_TVerificationDialog;
	protected Groupbox finBasicdetails;
	protected Listbox listBoxTechnicalVerification;
	protected Groupbox tvInquiry;

	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private FinanceMainBaseCtrl financeMainDialogCtrl = null;
	private Verification verification;
	private FinanceDetail financeDetail;

	private transient boolean validationOn;
	private transient boolean initType;
	List<String> requiredCodes;

	@Autowired
	private TechnicalVerificationService technicalVerificationService;
	@Autowired
	private transient VerificationService verificationService;
	@Autowired
	private SearchProcessor searchProcessor;

	private List<Verification> deletedList = new ArrayList<>();

	protected Radiogroup tv;

	/**
	 * default constructor.<br>
	 */
	public TVerificationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	public void onCreate$window_TVerificationDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_TVerificationDialog);

		appendFinBasicDetails(arguments.get("finHeaderList"));

		this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
		this.verification = financeDetail.getTvVerification();
		if (this.verification == null) {
			this.verification = new Verification();
			this.financeDetail.setTvVerification(verification);
		}
		this.verification.setKeyReference(financeDetail.getFinScheduleData().getFinReference());

		financeMainDialogCtrl = (FinanceMainBaseCtrl) arguments.get("financeMainBaseCtrl");

		if (arguments.get("InitType") != null) {
			initType = (Boolean) arguments.get("InitType");
		}

		financeMainDialogCtrl.settVerificationDialogCtrl(this);

		requiredCodes = getRequiredCollaterals();

		doShowDialog();

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialog() {
		logger.debug(Literal.ENTERING);

		setVerifications();

		logger.debug(Literal.LEAVING);
	}

	private List<String> getRequiredCollaterals() {
		List<String> codes = new ArrayList<>();
		Search search = new Search(CollateralSetup.class);
		search.addField("collateraltype");
		search.addTabelName("collateralstructure");
		search.addFilter(new Filter("collateralvaluatorreq", 1));
		List<CollateralSetup> list = searchProcessor.getResults(search);
		for (CollateralSetup collateralSetup : list) {
			codes.add(collateralSetup.getCollateralType());
		}
		return codes;
	}

	private void setVerifications() {
		List<CollateralAssignment> collaterls = null;

		if (initType) {
			collaterls = financeDetail.getCollateralAssignmentList();
		}

		renderList(getVerifications(collaterls));
	}

	private boolean isNotDeleted(String recordType) {
		return !(PennantConstants.RECORD_TYPE_DEL.equals(recordType)
				|| PennantConstants.RECORD_TYPE_CAN.equals(recordType));
	}

	public void addCollaterals(List<CollateralAssignment> collaterals) {
		if (!initType) {
			return;
		}
		if (collaterals != null) {
			renderList(getVerifications(collaterals));
		}
	}

	private void setOldVerificationFields(Verification current, Verification previous) {
		current.setId(previous.getId());
		current.setRequestType(previous.getRequestType());
		current.setAgency(previous.getAgency());
		current.setAgencyName(previous.getAgencyName());
		current.setReason(previous.getReason());
		current.setRemarks(previous.getRemarks());
		current.setDecision(previous.getDecision());
		current.setDecisionRemarks(previous.getDecisionRemarks());
		current.setReference(previous.getReference());
		current.setVerificationType(VerificationType.TV.getKey());
		current.setNewRecord(false);
	}

	private Verification getVerification(CollateralAssignment collateral) {
		Verification item = new Verification();

		Customer customer = financeDetail.getCustomerDetails().getCustomer();

		item.setReferenceFor(collateral.getCollateralRef());
		item.setCif(customer.getCustCIF());
		item.setCustomerName(customer.getCustShrtName());
		item.setCustId(customer.getCustID());
		item.setKeyReference(this.verification.getKeyReference());
		item.setReferenceType(collateral.getLovValue());
		item.setNewRecord(true);
		item.setVerificationType(VerificationType.TV.getKey());
		item.setCreatedBy(getUserWorkspace().getLoggedInUser().getUserId());

		return item;
	}

	private List<Verification> getVerifications(List<CollateralAssignment> collaterals) {
		List<Verification> verifications = new ArrayList<>();
		Map<String, Verification> collateralMap = new HashMap<>();
		Set<String> deletedSet = new HashSet<>();

		if (initType) {
			// Prepare Customer Collaterals
			for (CollateralAssignment collateral : collaterals) {
				if (isNotDeleted(collateral.getRecordType())) {
					Verification object = getVerification(collateral);
					collateralMap.put(object.getReferenceFor(), object);
				} else {
					deletedSet.add(collateral.getCollateralRef());
				}
			}
		}

		// Set the old verification fields back.
		List<Verification> oldVerifications = getOldVerifications();
		for (Verification previous : oldVerifications) {
			Verification current = collateralMap.get(previous.getReferenceFor());
			if (current != null) {
				setOldVerificationFields(current, previous);
				collateralMap.remove(current.getReferenceFor());
			}
		}

		verifications.addAll(oldVerifications);
		verifications.addAll(collateralMap.values());

		for (Verification object : verifications) {
			if ((deletedSet.contains(object.getReferenceFor()) && (object.isNew()
					|| !verificationService.isVerificationInRecording(object, VerificationType.TV, null)))) {
				object.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
		}

		if (verifications.isEmpty()) {
			return verifications;
		}

		if (initType) {
			String[] references = new String[verifications.size()];
			int i = 0;
			for (Verification reference : verifications) {
				references[i++] = reference.getReferenceFor();
			}

			List<Verification> collateralDetails = verificationService.getCollateralDetails(references);

			for (Verification item : collateralDetails) {
				for (Verification ver : verifications) {
					if (StringUtils.equals(item.getReferenceFor(), ver.getReferenceFor())) {
						ver.setReferenceType(item.getReferenceType());
						ver.setReference(item.getReference());
						ver.setCif(item.getReference());
						ver.setCustomerName(item.getCustomerName());
						ver.setVerificationReq(item.isVerificationReq());

						if (ver.isNew()) {
							if (ver.isVerificationReq()) {
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

	private List<Verification> getOldVerifications() {
		return verificationService.getVerifications(this.verification.getKeyReference(), VerificationType.TV.getKey());
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails(Object finHeaderList) {
		logger.debug(Literal.ENTERING);
		try {
			final HashMap<String, Object> map = new HashMap<>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", finHeaderList);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
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

	public void onChnageTv(ForwardEvent event) throws Exception {
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
		default:
			cAgency.setValue("");
			cReason.setValue("");
			cAgency.setReadonly(true);
			cReason.setReadonly(true);
			break;
		}
	}

	public void onChangeAgency(ForwardEvent event) {
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

	public void onChangeReason(ForwardEvent event) {
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

	public void onChangeReInitAgency(ForwardEvent event) {
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

	public void onChangeDecision(ForwardEvent event) {

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

	public void onCheck$tv(Event event) {
		final HashMap<String, Object> map = new HashMap<>();
		TechnicalVerification technicalVerification = technicalVerificationService
				.getTechnicalVerification(tv.getSelectedItem().getValue(), "_View");
		if (technicalVerification != null && StringUtils.isEmpty(technicalVerification.getNextRoleCode())) {
			map.put("LOAN_ORG", true);
			map.put("technicalVerification", technicalVerification);
			if (tvInquiry.getChildren() != null) {
				tvInquiry.getChildren().clear();
			}
			Executions.createComponents(
					"/WEB-INF/pages/Verification/TechnicalVerification/TechnicalVerificationDialog.zul", tvInquiry,
					map);
		} else if (technicalVerification != null) {
			MessageUtil.showMessage("Verification is not yet completed.");
		} else {
			MessageUtil.showMessage("Initiation request not available.");
		}
	}

	/**
	 * Method to fill TV Initiation/Approval tab.
	 * 
	 * @param customer
	 */
	public void renderList(List<Verification> verifications) {
		logger.debug(Literal.ENTERING);

		if (listBoxTechnicalVerification.getItems() != null) {
			listBoxTechnicalVerification.getItems().clear();
		}

		deletedList.clear();

		//set Initiated flag to initiated Records
		setInitiated(verifications);

		//Render Verifications
		int i = 0;
		for (Verification vrf : verifications) {
			if (PennantConstants.RECORD_TYPE_DEL.equals(vrf.getRecordType())) {
				deletedList.add(vrf);
				continue;
			}

			i++;
			Listitem item = new Listitem();
			Listcell listCell;
			Radio select = null;
			if (!initType) {
				// Select
				listCell = new Listcell();
				listCell.setId("select".concat(String.valueOf(i)));
				select = new Radio();
				select.setRadiogroup(tv);
				select.setValue(vrf.getId());
				listCell.appendChild(select);
				listCell.setParent(item);
			}

			// Collateral Type
			listCell = new Listcell();
			listCell.setId("ReferenceType".concat(String.valueOf(i)));
			listCell.appendChild(new Label(vrf.getReferenceType()));
			listCell.setParent(item);

			// Depositor CIF
			listCell = new Listcell();
			listCell.setId("Reference".concat(String.valueOf(i)));
			listCell.appendChild(new Label(vrf.getCif()));
			listCell.setParent(item);

			// Dipositor Name
			listCell = new Listcell(vrf.getCustomerName());
			listCell.setParent(item);

			// Collateral Reference
			listCell = new Listcell();
			listCell.setId("ReferenceFor".concat(String.valueOf(i)));
			listCell.appendChild(new Label(vrf.getReferenceFor()));
			listCell.setParent(item);

			// TV
			listCell = new Listcell();
			listCell.setId("RequestType".concat(String.valueOf(i)));
			Combobox requestType = new Combobox();
			requestType.setWidth("200px");
			requestType.setReadonly(true);
			requestType.setValue(String.valueOf(vrf.getRequestType()));

			List<ValueLabel> list = new ArrayList<>();
			int reqType = vrf.getRequestType();
			if (reqType == RequestType.NOT_REQUIRED.getKey() && requiredCodes.contains(vrf.getReferenceType())) {
				list = RequestType.getList();
			} else if (reqType == RequestType.WAIVE.getKey() && !requiredCodes.contains(vrf.getReferenceType())) {
				list = RequestType.getList();
			} else if (requiredCodes.contains(vrf.getReferenceType())) {
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
			agency.setWidth("200px");
			agency.setHflex("min");
			if (vrf.getAgencyName() != null) {
				agency.setValue(String.valueOf(vrf.getAgencyName()));
				agency.setAttribute("agencyId", vrf.getAgency());
				agency.setAttribute("oldAgencyId", vrf.getAgency());
				agency.setAttribute("agencyName", vrf.getAgencyName());
			}
			fillAgencies(agency);
			listCell.appendChild(agency);
			listCell.setParent(item);

			// Reason
			listCell = new Listcell();
			listCell.setId("Reason".concat(String.valueOf(i)));
			ExtendedCombobox reason = new ExtendedCombobox();
			reason.setWidth("200px");
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
			remarks.setWidth("200px");
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
				fillAgencies(reInitAgency);
				listCell.appendChild(reInitAgency);
				listCell.setParent(item);

				// Re-Initiation Remarks
				listCell = new Listcell();
				listCell.setId("ReInitRemarks".concat(String.valueOf(i)));
				Textbox reInitRemarks = new Textbox();
				reInitRemarks.setValue(vrf.getDecisionRemarks());
				reInitRemarks.setMaxlength(500);
				listCell.appendChild(reInitRemarks);
				listCell.setParent(item);

				decision.addForward("onChange", self, "onChangeDecision", item);
				reInitAgency.addForward("onFulfill", self, "onChangeReInitAgency", item);

				if (vrf.getDecision() == Decision.RE_INITIATE.getKey()) {
					decision.setDisabled(true);
					reInitAgency.setReadonly(true);
					reInitRemarks.setReadonly(true);
				}
			}

			requestType.addForward("onChange", self, "onChnageTv", item);
			agency.addForward("onFulfill", self, "onChangeAgency", item);
			reason.addForward("onFulfill", self, "onChangeReason", item);

			onchangeVerificationType(requestType, agency, reason);

			String key = vrf.getReferenceFor().concat(vrf.getCif());
			item.setAttribute(key, vrf);

			this.listBoxTechnicalVerification.appendChild(item);

			if (!initType || vrf.isInitiated()) {
				requestType.setDisabled(true);
				agency.setReadonly(true);
				reason.setReadonly(true);
				remarks.setReadonly(true);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void setInitiated(List<Verification> verifications) {
		for (Verification verification : verifications) {
			if (verification.getRequestType() == RequestType.INITIATE.getKey()
					&& verificationService.isVerificationInRecording(verification, VerificationType.TV, null)) {
				verification.setInitiated(true);
			}
		}
	}

	private void fillDecision(Verification vrf, Combobox combobox) {
		List<ValueLabel> decisionList = new ArrayList<>();

		int requestType = vrf.getRequestType();
		int status = vrf.getStatus();
		int decision = vrf.getDecision();

		if (requestType == RequestType.NOT_REQUIRED.getKey() || status == TVStatus.POSITIVE.getKey()) {
			decisionList.add(new ValueLabel(String.valueOf(Decision.OVERRIDE.getKey()), Decision.OVERRIDE.getValue()));
			decisionList.add(new ValueLabel(String.valueOf(Decision.SELECT.getKey()), Decision.SELECT.getValue()));
			if (decision == Decision.SELECT.getKey()) {
				vrf.setDecision(Decision.APPROVE.getKey());
			}
			fillComboBox(combobox, vrf.getDecision(), filterDecisions(decisionList));
		} else if (status == TVStatus.NEGATIVE.getKey()) {
			decisionList.add(new ValueLabel(String.valueOf(Decision.APPROVE.getKey()), Decision.APPROVE.getValue()));
			fillComboBox(combobox, decision, filterDecisions(decisionList));
		} else if (requestType == RequestType.WAIVE.getKey()) {
			decisionList.add(new ValueLabel(String.valueOf(Decision.OVERRIDE.getKey()), Decision.OVERRIDE.getValue()));
			fillComboBox(combobox, decision, filterDecisions(decisionList));
		} else {
			fillComboBox(combobox, decision, Decision.getList());
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

	private void fillAgencies(ExtendedCombobox agency) {
		logger.debug(Literal.ENTERING);

		agency.setModuleName("VerificationAgencies");
		agency.setValueColumn("DealerName");
		agency.setValidateColumns(new String[] { "DealerName" });
		Filter agencyFilter[] = new Filter[1];
		agencyFilter[0] = new Filter("DealerType", Agencies.TVAGENCY.getKey(), Filter.OP_EQUAL);
		agency.setFilters(agencyFilter);

		logger.debug(Literal.LEAVING);
	}

	private void fillReasons(ExtendedCombobox reason) {
		logger.debug(Literal.ENTERING);

		reason.setModuleName("VerificationWaiverReason");
		reason.setValueColumn("Code");
		reason.setValidateColumns(new String[] { "Code" });
		Filter reasonFilter[] = new Filter[1];
		reasonFilter[0] = new Filter("ReasonTypecode", WaiverReasons.TVWRES.getKey(), Filter.OP_EQUAL);
		reason.setFilters(reasonFilter);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		for (Listitem listitem : listBoxTechnicalVerification.getItems()) {
			Combobox fivComboBox = (Combobox) getComponent(listitem, "RequestType");
			ExtendedCombobox agencyComboBox = (ExtendedCombobox) getComponent(listitem, "Agency");
			ExtendedCombobox reasonComboBox = (ExtendedCombobox) getComponent(listitem, "Reason");
			Combobox decision = (Combobox) getComponent(listitem, "Decision");
			ExtendedCombobox reInitagencyComboBox = (ExtendedCombobox) getComponent(listitem, "ReInitAgency");
			Textbox reInitRemarks = (Textbox) getComponent(listitem, "ReInitRemarks");

			if (fivComboBox != null) {
				fivComboBox.clearErrorMessage();
			}

			if (agencyComboBox != null) {
				agencyComboBox.clearErrorMessage();
			}

			if (reasonComboBox != null) {
				reasonComboBox.clearErrorMessage();
			}

			if (decision != null) {
				decision.clearErrorMessage();
			}

			if (reInitagencyComboBox != null) {
				reInitagencyComboBox.clearErrorMessage();
			}

			if (reInitRemarks != null) {
				reInitRemarks.clearErrorMessage();
			}
		}
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);
		setValidationOn(true);

		for (Listitem listitem : listBoxTechnicalVerification.getItems()) {
			Combobox tvComboBox = (Combobox) getComponent(listitem, "RequestType");
			ExtendedCombobox agencyComboBox = (ExtendedCombobox) getComponent(listitem, "Agency");
			ExtendedCombobox reasonComboBox = (ExtendedCombobox) getComponent(listitem, "Reason");
			ExtendedCombobox reInitAgencyComboBox = (ExtendedCombobox) getComponent(listitem, "ReInitAgency");

			if (!tvComboBox.isDisabled()) {
				tvComboBox.setConstraint(new PTStringValidator(
						Labels.getLabel("label_TechnicalVerificationDialog_TV.value"), null, true));
			}

			if (!agencyComboBox.isReadonly()) {
				agencyComboBox.setConstraint(new PTStringValidator(
						Labels.getLabel("label_TechnicalVerificationDialog_Agency.value"), null, true, true));
			}

			if (!reasonComboBox.isReadonly()) {
				reasonComboBox.setConstraint(new PTStringValidator(
						Labels.getLabel("label_TechnicalVerificationDialog_Reason.value"), null, true, true));
			}

			if (!initType && !reInitAgencyComboBox.isReadonly()) {
				reInitAgencyComboBox.setConstraint(new PTStringValidator(
						Labels.getLabel("label_TechnicalVerificationDialog_Agency.value"), null, true, true));
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private Verification getVerification(Listitem listitem, String comonentId) {
		Verification item = null;
		String referenceFor = ((Label) getComponent(listitem, "ReferenceFor")).getValue();
		String reference = ((Label) getComponent(listitem, "Reference")).getValue();

		String key = referenceFor.concat(reference);

		item = (Verification) listitem.getAttribute(key);

		switch (comonentId) {
		case "RequestType":
			String requestType = ((Combobox) getComponent(listitem, "RequestType")).getSelectedItem().getValue();
			item.setRequestType(Integer.parseInt(requestType));
			break;
		case "Agency":
			ExtendedCombobox agency = ((ExtendedCombobox) getComponent(listitem, "Agency"));
			if (StringUtils.isNotEmpty(agency.getValue())) {
				item.setAgency(Long.parseLong(agency.getAttribute("agencyId").toString()));
			} else {
				item.setAgency(null);
			}
			break;
		case "Reason":
			ExtendedCombobox reason = ((ExtendedCombobox) getComponent(listitem, "Reason"));
			if (StringUtils.isNotEmpty(reason.getValue())) {
				item.setReason(Long.parseLong(reason.getAttribute("reasonId").toString()));
			} else {
				item.setReason(null);
			}
			break;
		case "Remarks":
			item.setRemarks(((Textbox) getComponent(listitem, "Remarks")).getValue());
			break;
		case "Decision":
			Combobox combobox = (Combobox) getComponent(listitem, "Decision");
			int decision = Integer.parseInt(getComboboxValue(combobox).toString());
			if (combobox.isDisabled()) {
				item.setIgnoreFlag(true);
			}
			item.setDecision(decision);
			if (!combobox.isDisabled() && decision == 0) {
				throw new WrongValueException(combobox,
						Labels.getLabel("STATIC_INVALID", new String[] { "Decision should be mandatory" }));
			}
			break;
		case "ReInitAgency":
			ExtendedCombobox reInitAgency = ((ExtendedCombobox) getComponent(listitem, "ReInitAgency"));
			if (StringUtils.isNotEmpty(reInitAgency.getValue())) {
				item.setReInitAgency(Long.parseLong(reInitAgency.getAttribute("reInitAgencyId").toString()));
			} else {
				item.setReInitAgency(null);
			}
			break;
		case "ReInitRemarks":
			Textbox textbox = (Textbox) getComponent(listitem, "ReInitRemarks");
			item.setDecisionRemarks(textbox.getValue());
			if (item.getDecision() == Decision.OVERRIDE.getKey() && StringUtils.isEmpty(item.getDecisionRemarks())) {
				throw new WrongValueException(textbox, "Remarks are mandatory when Decision is Override");
			}
			break;
		default:
			break;
		}

		item.setRecordStatus(this.recordStatus.getValue());

		return item;
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param verification
	 * @return
	 */
	public List<WrongValueException> doWriteComponentsToBean() {
		logger.debug(Literal.ENTERING);
		List<Verification> verifications = new ArrayList<>();

		ArrayList<WrongValueException> wve = new ArrayList<>();

		Verification item = null;

		for (Listitem listitem : listBoxTechnicalVerification.getItems()) {
			try {
				item = getVerification(listitem, "RequestType");
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				item = getVerification(listitem, "Agency");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				item = getVerification(listitem, "Reason");
			} catch (WrongValueException we) {
				wve.add(we);
			}

			item = getVerification(listitem, "Remarks");

			if (!initType) {
				try {
					item = getVerification(listitem, "Decision");
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					item = getVerification(listitem, "ReInitAgency");
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					item = getVerification(listitem, "ReInitRemarks");
				} catch (WrongValueException we) {
					wve.add(we);
				}
			}

			verifications.add(item);
		}
		verifications.addAll(deletedList);

		doRemoveValidation();

		this.verification.setVerifications(verifications);

		logger.debug(Literal.LEAVING);

		return wve;
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		setValidationOn(false);
		for (Listitem listitem : listBoxTechnicalVerification.getItems()) {
			Combobox tvComboBox = (Combobox) getComponent(listitem, "RequestType");
			ExtendedCombobox agencyComboBox = (ExtendedCombobox) getComponent(listitem, "Agency");
			ExtendedCombobox reasonComboBox = (ExtendedCombobox) getComponent(listitem, "Reason");
			Combobox decision = (Combobox) getComponent(listitem, "Decision");
			ExtendedCombobox reinitAgencyComboBox = (ExtendedCombobox) getComponent(listitem, "ReInitAgency");
			Textbox reInitRemarks = (Textbox) getComponent(listitem, "ReInitRemarks");

			if (tvComboBox != null) {
				tvComboBox.setConstraint("");
			}

			if (agencyComboBox != null) {
				agencyComboBox.setConstraint("");
			}

			if (reasonComboBox != null) {
				reasonComboBox.setConstraint("");
			}

			if (decision != null) {
				decision.setConstraint("");
			}

			if (reinitAgencyComboBox != null) {
				reinitAgencyComboBox.setConstraint("");
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

	public boolean doSave(FinanceDetail financeDetail, Tab tab, boolean recSave) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		doClearMessage();
		doSetValidation();

		List<WrongValueException> wve = doWriteComponentsToBean();

		if (!wve.isEmpty() && tab != null) {
			tab.setSelected(true);
		}

		showErrorDetails(wve, tab);

		this.verification.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		financeDetail.setTvVerification(this.verification);

		logger.debug(Literal.LEAVING);

		if (tab.getId().equals("TAB".concat(AssetConstants.UNIQUE_ID_TVAPPROVAL))) {
			return validateReinitiation(financeDetail.getTvVerification().getVerifications());
		}
		return true;

	}

	private boolean validateReinitiation(List<Verification> verifications) {
		for (Verification verification : verifications) {
			if (verification.getDecision() == Decision.RE_INITIATE.getKey()
					&& !userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_SAVED)
					&& verification.getReinitid() == null) {
				MessageUtil.showError("Technical Verification Re-Initiation is allowed only when user action is save");
				return false;
			}
		}
		return true;
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
}

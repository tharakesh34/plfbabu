package com.pennanttech.webui.verification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.verification.Agencies;
import com.pennanttech.pennapps.pff.verification.Decision;
import com.pennanttech.pennapps.pff.verification.RequestType;
import com.pennanttech.pennapps.pff.verification.Status;
import com.pennanttech.pennapps.pff.verification.WaiverReasons;
import com.pennanttech.pennapps.pff.verification.model.TechnicalVerification;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pennapps.pff.verification.service.TechnicalVerificationService;
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

	private transient boolean validationOn;
	private transient boolean initType;

	@Autowired
	private TechnicalVerificationService technicalVerificationService;

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

		verification = (Verification) arguments.get("verification");

		financeMainDialogCtrl = (FinanceMainBaseCtrl) arguments.get("financeMainBaseCtrl");

		if (arguments.get("InitType") != null) {
			initType = (Boolean) arguments.get("InitType");
		}

		doShowDialog();

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialog() {
		logger.debug(Literal.ENTERING);

		financeMainDialogCtrl.settVerificationDialogCtrl(this);

		renderTechnicalVerificationList(verification);

		this.listBoxTechnicalVerification.setHeight(this.borderLayoutHeight - 600 - 90 + "px");
		this.window_TVerificationDialog.setHeight(this.borderLayoutHeight - 80 + "px");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails(Object finHeaderList) {
		logger.debug(Literal.ENTERING);
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
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

	public void onChnageTv(ForwardEvent event) throws Exception {
		Listitem listitem = (Listitem) event.getData();

		Combobox cfiv = (Combobox) getComponent(listitem, "RequestType");
		ExtendedCombobox cAgency = (ExtendedCombobox) getComponent(listitem, "Agency");
		ExtendedCombobox cReason = (ExtendedCombobox) getComponent(listitem, "Reason");

		onchangeVerificationType(cfiv, cAgency, cReason);
	}

	private void onchangeVerificationType(Combobox cfiv, ExtendedCombobox cAgency, ExtendedCombobox cReason) {
		RequestType type;
		try {
			type = RequestType.getType(Integer.parseInt(cfiv.getSelectedItem().getValue()));
		} catch (Exception e) {
			String value = cfiv.getValue();
			cfiv.setValue("");
			throw new WrongValueException(cfiv,
					Labels.getLabel("STATIC_INVALID", new String[] { value + " is not valid," }));
		}
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

	public void onCheck$tv(Event event) {
		final HashMap<String, Object> map = new HashMap<>();
		TechnicalVerification technicalVerification = technicalVerificationService
				.getTechnicalVerification(tv.getSelectedItem().getValue());
		if (technicalVerification != null) {
			map.put("LOAN_ORG", true);
			map.put("technicalVerification", technicalVerification);
			if (tvInquiry.getChildren() != null) {
				tvInquiry.getChildren().clear();
			}
			Executions.createComponents(
					"/WEB-INF/pages/Verification/TechnicalVerification/TechnicalVerificationDialog.zul", tvInquiry,
					map);
		} else {
			MessageUtil.showMessage("Initiation request not available in Technical Verification Module.");
		}

	}

	/**
	 * Method to fill TV Initiation/Approval tab.
	 * 
	 * @param customer
	 */
	public void renderTechnicalVerificationList(Verification verification) {
		logger.debug(Literal.ENTERING);

		this.verification = verification;

		if (listBoxTechnicalVerification.getItems() != null) {
			listBoxTechnicalVerification.getItems().clear();
		}

		int i = 0;
		for (Verification vrf : verification.getVerifications()) {
			if (vrf.getReinitid() != null && vrf.getRequestType() == RequestType.WAIVE.getKey()) {
				continue;
			}

			i++;
			Listitem item = new Listitem();
			Listcell listCell;
			if (!initType) {

				//Select
				listCell = new Listcell();
				listCell.setId("select".concat(String.valueOf(i)));
				Radio select = new Radio();
				select.setRadiogroup(tv);
				select.setValue(vrf.getId());
				listCell.appendChild(select);
				listCell.setParent(item);
			}

			//Collateral Type
			listCell = new Listcell();
			listCell.setId("ReferenceType".concat(String.valueOf(i)));
			listCell.appendChild(new Label(vrf.getReferenceType()));
			listCell.setParent(item);

			//Depositor CIF
			listCell = new Listcell();
			listCell.setId("Reference".concat(String.valueOf(i)));
			listCell.appendChild(new Label(vrf.getCif()));
			listCell.setParent(item);

			//Dipositor Name
			listCell = new Listcell(vrf.getCustomerName());
			listCell.setParent(item);

			//Collateral Reference
			listCell = new Listcell();
			listCell.setId("ReferenceFor".concat(String.valueOf(i)));
			listCell.appendChild(new Label(vrf.getReferenceFor()));
			listCell.setParent(item);

			//TV
			listCell = new Listcell();
			listCell.setId("RequestType".concat(String.valueOf(i)));
			Combobox requestType = new Combobox();
			requestType.setValue(String.valueOf(vrf.getRequestType()));
			List<ValueLabel> list = new ArrayList<>();
			if (vrf.getRequestType() == RequestType.NOT_REQUIRED.getKey()) {
				for (ValueLabel valueLabel : RequestType.getList()) {
					if (Integer.parseInt(valueLabel.getValue()) != RequestType.WAIVE.getKey()) {
						list.add(valueLabel);
					}
				}
				fillComboBox(requestType, vrf.getRequestType(), list);
			} else {
				fillComboBox(requestType, vrf.getRequestType(), RequestType.getList());
			}
			requestType.setParent(listCell);
			listCell.setParent(item);

			//Agency
			listCell = new Listcell();
			listCell.setId("Agency".concat(String.valueOf(i)));
			ExtendedCombobox agency = new ExtendedCombobox();
			if (vrf.getAgencyName() != null) {
				agency.setValue(String.valueOf(vrf.getAgencyName()));
				agency.setAttribute("agencyId", vrf.getAgency());
				agency.setAttribute("oldAgencyId", vrf.getAgency());
				agency.setAttribute("agencyName", vrf.getAgencyName());
			}
			fillAgencies(agency);
			listCell.appendChild(agency);
			listCell.setParent(item);

			//Reason
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

			//Remarks
			listCell = new Listcell();
			listCell.setId("Remarks".concat(String.valueOf(i)));
			Textbox remarks = new Textbox(vrf.getRemarks());
			listCell.appendChild(remarks);
			listCell.setParent(item);

			//Status
			listCell = new Listcell();
			Label status = new Label();
			if (Status.getType(vrf.getStatus()) != null) {
				status.setValue(Status.getType(vrf.getStatus()).getValue());
			}
			listCell.appendChild(status);
			listCell.setParent(item);

			//Verification Date
			listCell = new Listcell();
			listCell.appendChild(new Label(DateUtil.formatToShortDate(vrf.getVerificationDate())));
			listCell.setParent(item);

			if (!initType) {

				//Decision
				listCell = new Listcell();
				listCell.setId("Decision".concat(String.valueOf(i)));
				Combobox decision = new Combobox();
				if (Decision.getType(vrf.getDecision()) != null) {
					decision.setValue(String.valueOf(Decision.getType(vrf.getDecision()).getValue()));
				}

				fillDecision(vrf, decision);

				decision.setParent(listCell);
				listCell.setParent(item);

				//Re-Initiation Agency
				listCell = new Listcell();
				listCell.setId("ReInitAgency".concat(String.valueOf(i)));
				ExtendedCombobox reInitAgency = new ExtendedCombobox();
				reInitAgency.setReadonly(true);
				fillAgencies(reInitAgency);
				listCell.appendChild(reInitAgency);
				listCell.setParent(item);

				//Re-Initiation Remarks
				listCell = new Listcell();
				listCell.setId("ReInitRemarks".concat(String.valueOf(i)));
				Textbox reInitRemarks = new Textbox();
				listCell.appendChild(reInitRemarks);
				listCell.setParent(item);

				decision.addForward("onChange", self, "onChangeDecision", item);
				reInitAgency.addForward("onFulfill", self, "onChangeReInitAgency", item);
			}

			requestType.addForward("onChange", self, "onChnageTv", item);
			agency.addForward("onFulfill", self, "onChangeAgency", item);
			reason.addForward("onFulfill", self, "onChangeReason", item);

			onchangeVerificationType(requestType, agency, reason);

			String key = vrf.getReferenceFor().concat(vrf.getCif());
			item.setAttribute(key, vrf);

			this.listBoxTechnicalVerification.appendChild(item);

			if (!initType) {
				requestType.setDisabled(true);
				agency.setReadonly(true);
				reason.setReadonly(true);
				remarks.setReadonly(true);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void fillDecision(Verification vrf, Combobox decision) {
		List<ValueLabel> decisionList = new ArrayList<>();
		if (vrf.getRequestType() == RequestType.NOT_REQUIRED.getKey() || vrf.getStatus() == Status.POSITIVE.getKey()) {
			decisionList.add(new ValueLabel(String.valueOf(Decision.OVERRIDE.getKey()), Decision.OVERRIDE.getValue()));
			decisionList.add(new ValueLabel(String.valueOf(Decision.SELECT.getKey()), Decision.SELECT.getValue()));
			if (vrf.getDecision() == Decision.SELECT.getKey()) {
				vrf.setDecision(Decision.APPROVE.getKey());
			}
			fillComboBox(decision, vrf.getDecision(), filterDecisions(decisionList));
		} else if (vrf.getStatus() == Status.NOTCMPLTD.getKey() || vrf.getStatus() == Status.NEGATIVE.getKey()) {
			decisionList.add(new ValueLabel(String.valueOf(Decision.APPROVE.getKey()), Decision.APPROVE.getValue()));
			fillComboBox(decision, vrf.getDecision(), filterDecisions(decisionList));
		} else if (vrf.getRequestType() == RequestType.WAIVE.getKey()) {
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

			fivComboBox.clearErrorMessage();
			agencyComboBox.clearErrorMessage();
			reasonComboBox.clearErrorMessage();

			if (decision != null) {
				decision.clearErrorMessage();
			}
			if (reInitagencyComboBox != null) {
				reInitagencyComboBox.clearErrorMessage();
			}
		}
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
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
		logger.debug("Leaving");
	}

	private void setValue(Listitem listitem, String comonentId) {
		Verification verification = null;
		String referenceFor = ((Label) getComponent(listitem, "ReferenceFor")).getValue();
		String reference = ((Label) getComponent(listitem, "Reference")).getValue();

		String key = referenceFor.concat(reference);

		verification = (Verification) listitem.getAttribute(key);

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
			break;
		case "Remarks":
			verification.setRemarks(((Textbox) getComponent(listitem, "Remarks")).getValue());
			break;
		case "Decision":
			Combobox combobox = (Combobox) getComponent(listitem, "Decision");
			int decision = Integer.parseInt(getComboboxValue(combobox).toString());
			verification.setDecision(decision);
			if (!combobox.isDisabled() && decision == 0) {
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
				throw new WrongValueException(textbox, "Remarks is mandatory");
			}
			break;
		default:
			break;
		}

		verification.setRecordStatus(this.recordStatus.getValue());
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param verification
	 * @return
	 */
	public ArrayList<WrongValueException> doWriteComponentsToBean() {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		for (Listitem listitem : listBoxTechnicalVerification.getItems()) {
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

			setValue(listitem, "Remarks");

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

				setValue(listitem, "ReInitRemarks");
			}
		}

		doRemoveValidation();

		logger.debug("Leaving");
		return wve;
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		setValidationOn(false);
		for (Listitem listitem : listBoxTechnicalVerification.getItems()) {
			Combobox fivComboBox = (Combobox) getComponent(listitem, "RequestType");
			ExtendedCombobox agencyComboBox = (ExtendedCombobox) getComponent(listitem, "Agency");
			ExtendedCombobox reasonComboBox = (ExtendedCombobox) getComponent(listitem, "Reason");
			Combobox decision = (Combobox) getComponent(listitem, "Decision");

			fivComboBox.setConstraint("");
			agencyComboBox.setConstraint("");
			reasonComboBox.setConstraint("");

			if (decision != null) {
				decision.setConstraint("");
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
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");

		doRemoveValidation();

		if (wve.size() > 0) {
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

	public void doSave_FiVerification(FinanceDetail financeDetail, Tab tab, boolean recSave)
			throws InterruptedException {
		logger.debug("Entering");

		doClearMessage();
		doSetValidation();

		ArrayList<WrongValueException> wve = doWriteComponentsToBean();

		if (!wve.isEmpty() && tab != null) {
			tab.setSelected(true);
		}

		showErrorDetails(wve, tab);

		this.verification.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		financeDetail.setTvVerification(this.verification);

		logger.debug("Leaving");
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

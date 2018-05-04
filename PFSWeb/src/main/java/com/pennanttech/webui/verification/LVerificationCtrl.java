package com.pennanttech.webui.verification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.verification.Decision;
import com.pennanttech.pennapps.pff.verification.RequestType;
import com.pennanttech.pennapps.pff.verification.Status;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.WaiverReasons;
import com.pennanttech.pennapps.pff.verification.model.TechnicalVerification;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pennapps.pff.verification.service.TechnicalVerificationService;
import com.pennanttech.pennapps.pff.verification.service.VerificationService;
import com.pennanttech.pennapps.web.util.MessageUtil;

@Component(value = "lVerificationCtrl")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LVerificationCtrl extends GFCBaseListCtrl<Verification> {
	private static final long serialVersionUID = 8661799804403963415L;
	private static final Logger logger = LogManager.getLogger(LVerificationCtrl.class);

	protected Window window_LegalVerificationDialog;
	protected Groupbox finBasicdetails;
	protected Listbox listBoxInitiation;
	protected Listbox listBoxWaiver;
	protected Groupbox tvInquiry;
	protected Button btnNew_Initiation;
	protected Button btnNew_Waiver;

	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private FinanceMainBaseCtrl financeMainDialogCtrl = null;
	private Verification verification;

	private transient boolean validationOn;
	private transient boolean initType;

	@Autowired
	private TechnicalVerificationService technicalVerificationService;
	@Autowired
	private VerificationService verificationService;

	protected Radiogroup tv;

	/**
	 * default constructor.<br>
	 */
	public LVerificationCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	public void onCreate$window_LegalVerificationDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_LegalVerificationDialog);

		appendFinBasicDetails(arguments.get("finHeaderList"));

		verification = (Verification) arguments.get("verification");
		verification.setReference(verification.getCif());

		financeMainDialogCtrl = (FinanceMainBaseCtrl) arguments.get("financeMainBaseCtrl");

		if (arguments.get("InitType") != null) {
			initType = (Boolean) arguments.get("InitType");
		}

		doShowDialog();

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialog() {
		logger.debug(Literal.ENTERING);

		financeMainDialogCtrl.setLegalVerificationListCtrl(this);
		renderLVInitiationList();
		//renderTechnicalVerificationList(verification);

		this.listBoxInitiation.setHeight(this.borderLayoutHeight - 380 - 90 + "px");
		this.listBoxWaiver.setHeight(this.borderLayoutHeight - 380 - 90 + "px");
		this.window_LegalVerificationDialog.setHeight(this.borderLayoutHeight - 80 + "px");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNew_Initiation(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		Verification verification = new Verification();
		BeanUtils.copyProperties(this.verification, verification);
		verification.setNewRecord(true);
		verification.getLvDocuments().clear();
		// Display the dialog page.
		Map<String, Object> arg = getDefaultArguments();
		arg.put("initiation", true);
		arg.put("verification", verification);
		arg.put("legalVerificationListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/LVInitiationDialog.zul", null,
					arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNew_Waiver(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		//Verification verification = new Verification();
		this.verification.setNewRecord(true);

		// Display the dialog page.
		Map<String, Object> arg = getDefaultArguments();
		arg.put("verification", this.verification);
		arg.put("legalVerificationListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/LVInitiationDialog.zul", null,
					arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

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

			id = id.substring(0, id.length() - 1);
			if (StringUtils.equals(id, listcellId)) {
				return listcell.getFirstChild();
			}
		}
		return null;
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
	 * Method to fill LV Initiation List.
	 * 
	 * @param customer
	 */
	public void renderLVInitiationList() {
		logger.debug(Literal.ENTERING);
		List<Verification> verifications = verificationService.getVerifications(this.verification.getKeyReference(), VerificationType.LV.getKey());

		if (this.listBoxInitiation.getItems() != null) {
			this.listBoxInitiation.getItems().clear();
		}

		int i = 0;
		for (Verification vrf : verifications) {
			if (vrf.getReinitid() != null && vrf.getRequestType() == RequestType.WAIVE.getKey()) {
				continue;
			}

			i++;
			Listitem item = new Listitem();
			Listcell listCell;

			//Collateral Type
			listCell = new Listcell();
			listCell.setId("CollateralType".concat(String.valueOf(i)));
			listCell.appendChild(new Label(vrf.getReferenceType()));
			listCell.setParent(item);

			//Agency
			listCell = new Listcell();
			listCell.setId("Agency".concat(String.valueOf(i)));
			Label agency = new Label();
			if (vrf.getAgencyName() != null) {
				agency.setValue(String.valueOf(vrf.getAgencyName()));
				agency.setAttribute("agencyId", vrf.getAgency());
			}
			listCell.appendChild(agency);
			listCell.setParent(item);

			//Remarks
			listCell = new Listcell();
			listCell.setId("Remarks".concat(String.valueOf(i)));
			Label remarks = new Label(vrf.getRemarks());
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

			String key = vrf.getReferenceFor().concat(vrf.getCif());
			item.setAttribute(key, vrf);

			this.listBoxInitiation.appendChild(item);

		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method to fill LV Waiver List.
	 * 
	 * @param customer
	 */
	public void renderLVWaiverList(Verification verification) {
		logger.debug(Literal.ENTERING);

		this.verification = verification;

		if (this.listBoxWaiver.getItems() != null) {
			this.listBoxWaiver.getItems().clear();
		}

		int i = 0;
		for (Verification vrf : verification.getVerifications()) {
			if (vrf.getReinitid() != null && vrf.getRequestType() == RequestType.WAIVE.getKey()) {
				continue;
			}

			i++;
			Listitem item = new Listitem();
			Listcell listCell;

			//Document Name Type
			listCell = new Listcell();
			listCell.setId("DocumentName".concat(String.valueOf(i)));
			listCell.appendChild(new Label(vrf.getReferenceFor()));
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

			reason.addForward("onFulfill", self, "onChangeReason", item);

			String key = vrf.getReferenceFor().concat(vrf.getCif());
			item.setAttribute(key, vrf);

			this.listBoxWaiver.appendChild(item);

		}
		logger.debug(Literal.LEAVING);
	}

	private void fillReasons(ExtendedCombobox reason) {
		logger.debug(Literal.ENTERING);

		reason.setModuleName("VerificationWaiverReason");
		reason.setValueColumn("Code");
		reason.setValidateColumns(new String[] { "Code" });
		Filter reasonFilter[] = new Filter[1];
		reasonFilter[0] = new Filter("ReasonTypecode", WaiverReasons.LVWRES.getKey(), Filter.OP_EQUAL);
		reason.setFilters(reasonFilter);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	private void doClearMessage() {
		for (Listitem listitem : listBoxInitiation.getItems()) {
			ExtendedCombobox reasonComboBox = (ExtendedCombobox) getComponent(listitem, "Reason");

			reasonComboBox.clearErrorMessage();

		}
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		for (Listitem listitem : listBoxInitiation.getItems()) {
			ExtendedCombobox reasonComboBox = (ExtendedCombobox) getComponent(listitem, "Reason");

			if (!reasonComboBox.isReadonly()) {
				reasonComboBox.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FIVerificationDialog_Reason.value"), null, true, true));
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
			int decision = Integer.parseInt(getComboboxValue(combobox));
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
			verification.setReInitRemarks(textbox.getValue());
			if (verification.getDecision() == Decision.OVERRIDE.getKey()
					&& StringUtils.isEmpty(verification.getReInitRemarks())) {
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

		for (Listitem listitem : listBoxInitiation.getItems()) {
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
		for (Listitem listitem : listBoxInitiation.getItems()) {
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

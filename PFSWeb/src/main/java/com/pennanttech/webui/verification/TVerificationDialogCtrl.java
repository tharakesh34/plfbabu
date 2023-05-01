package com.pennanttech.webui.verification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.zkoss.zk.ui.util.Clients;
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

import com.healthmarketscience.sqlbuilder.CreateTableQuery.TableType;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.verification.tv.TVInitiationListCtrl;
import com.pennanttech.dataengine.constants.ValueLabel;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.pff.verification.Agencies;
import com.pennanttech.pennapps.pff.verification.Decision;
import com.pennanttech.pennapps.pff.verification.RequestType;
import com.pennanttech.pennapps.pff.verification.VerificationCategory;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.WaiverReasons;
import com.pennanttech.pennapps.pff.verification.fi.TVStatus;
import com.pennanttech.pennapps.pff.verification.model.TechnicalVerification;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pennapps.pff.verification.service.TechnicalVerificationService;
import com.pennanttech.pennapps.pff.verification.service.VerificationService;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

@Component(value = "tVerificationDialogCtrl")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TVerificationDialogCtrl extends GFCBaseCtrl<Verification> {
	private static final long serialVersionUID = 8661799804403963415L;
	private static final Logger logger = LogManager.getLogger(TVerificationDialogCtrl.class);
	private static final String DOCVALUE = ImplementationConstants.VER_TV_COLL_ED_PROP_COST_COLUMN;
	private static final String TOTALVALUATIONASPE = ImplementationConstants.VER_TV_COLL_ED_PROP_VAL_COLUMN;

	protected Window window_TVerificationDialog;
	protected Groupbox finBasicdetails;
	protected Listbox listBoxTechnicalVerification;
	protected Groupbox tvInquiry;
	protected Radiogroup tv;
	protected Listheader listheader_TechnicalVerification_ReInitAgency;
	protected Listheader listheader_TechnicalVerification_ReInitRemarks;
	protected Listheader listheader_TechnicalVerification_AddAgency;

	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private Verification verification;
	private FinanceDetail financeDetail;

	private Button btnTVInitiateSave;
	private Button btnTVInitiateClose;

	private boolean validationOn;
	private boolean initType;
	private boolean recSave;
	private List<String> requiredCodes;

	private Button btnNew_FinalValuation;
	private List<Verification> verifications;

	private List<Verification> valuationDetails = new ArrayList<>();
	private Map<String, BigDecimal> collateralCOP = new HashMap<>();
	private Map<String, String> collateralCity = new HashMap<>();

	@Autowired
	private transient TechnicalVerificationService technicalVerificationService;
	@Autowired
	private transient VerificationService verificationService;
	@Autowired
	private SearchProcessor searchProcessor;

	private List<Verification> deletedList = new ArrayList<>();
	private String userRole = "";
	private String moduleDefiner = "";
	private String propCity = "";

	private boolean fromVerification;
	private TVInitiationListCtrl tvInitiationListCtrl;

	/**
	 * default constructor.<br>
	 */
	public TVerificationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinanceMainDialog";
	}

	public void onCreate$window_TVerificationDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_TVerificationDialog);

		if (arguments.containsKey("finHeaderList") && arguments.get("finHeaderList") != null) {
			appendFinBasicDetails(arguments.get("finHeaderList"));
		}
		this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
		this.verification = financeDetail.getTvVerification();
		if (this.verification == null) {
			this.verification = new Verification();
			this.financeDetail.setTvVerification(verification);
		}
		this.verification.setKeyReference(financeDetail.getFinScheduleData().getFinReference());

		if (arguments.containsKey("financeMainBaseCtrl")) {
			((FinanceMainBaseCtrl) arguments.get("financeMainBaseCtrl")).settVerificationDialogCtrl(this);
		} else {
			finBasicdetails.setVisible(false);
		}

		if (arguments.containsKey("tvInitiationListCtrl")) {
			tvInitiationListCtrl = (TVInitiationListCtrl) arguments.get("tvInitiationListCtrl");
			this.fromVerification = true;
			finBasicdetails.setVisible(true);
		}

		if (arguments.get("InitType") != null) {
			initType = (Boolean) arguments.get("InitType");
		}

		if (arguments.get("enqiryModule") != null) {
			enqiryModule = (Boolean) arguments.get("enqiryModule");
		}

		if (arguments.containsKey("moduleDefiner")) {
			moduleDefiner = (String) arguments.get("moduleDefiner");
		}

		if (!enqiryModule && arguments.get("userRole") != null) {
			setWorkFlowEnabled(true);
			userRole = (String) arguments.get("userRole");
			getUserWorkspace().allocateRoleAuthorities(userRole, pageRightName);
		}

		requiredCodes = getRequiredCollaterals();

		doSetProperties();
		doCheckRights();
		doShowDialog();

		if (!enqiryModule && StringUtils.equals("Approved",
				financeDetail.getFinScheduleData().getFinanceMain().getRecordStatus())) {
			// renderVerificationForAddDisbursement(financeDetail.getCollateralAssignmentList(), financeDetail);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		if (fromVerification) {
			this.btnTVInitiateSave.setVisible(fromVerification);
			this.btnTVInitiateClose.setVisible(fromVerification);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialog() {
		logger.debug(Literal.ENTERING);
		setVerifications();

		if (initType) {
			this.btnTVInitiateSave.setVisible(fromVerification);
			this.btnTVInitiateClose.setVisible(fromVerification);
		}
		if (enqiryModule) {
			if (btnTVInitiateSave != null) {
				this.btnTVInitiateSave.setVisible(!enqiryModule);
			}
		}
		if (fromVerification) {
			setDialog(DialogType.EMBEDDED);
		}
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

		collaterls = financeDetail.getCollateralAssignmentList();

		verifications = getFinalVerifications(collaterls, financeDetail);

		if (StringUtils.equals(moduleDefiner, FinServiceEvent.ADDDISB)) {
			List<Verification> tempList = new ArrayList<Verification>();
			for (Verification verification : verifications) {
				if (verification.getModule() != 0) {
					tempList.add(verification);
				}

			}

			verifications.clear();
			verifications.addAll(tempList);
		}

		List<Long> verificationIDs = new ArrayList<>();

		for (Verification verification : verifications) {
			verificationIDs.add(verification.getId());
		}

		if (verificationIDs.size() > 0) {
			List<Verification> verificationList = technicalVerificationService.getTvValuation(verificationIDs, "_View");
			valuationDetails.addAll(verificationList);
		}

		for (Verification valuation : valuationDetails) {
			for (Verification verification : verifications) {
				if (verification.getId() == valuation.getId()) {
					verification.setValuationAmount(valuation.getValuationAmount());
					verification.setFinalValAmt(valuation.getFinalValAmt());
					verification.setFinalValDecision(valuation.getFinalValDecision());
					verification.setFinalValRemarks(valuation.getFinalValRemarks());
					verification.setCollateralType(valuation.getCollateralType());
					verification.setTvRecordStatus(valuation.getTvRecordStatus());
				}
			}
		}

		for (Verification verification : verifications) {
			if (StringUtils.isBlank(verification.getCollateralType())) {
				String collateralType = technicalVerificationService.getCollaterlType(verification.getId());
				if (collateralType != null) {
					verification.setCollateralType(collateralType);
				}
			}
		}

		// Get Document Value from collateral extended fields.

		for (Verification veri : verifications) {
			String collRef = veri.getReferenceFor();
			if (!collateralCOP.containsKey(collRef.concat(DOCVALUE))) {
				Map<String, Object> valAmounts = new HashMap<>();
				if (StringUtils.isNotEmpty(veri.getCollateralType())) {
					valAmounts = technicalVerificationService.getCostOfPropertyValue(collRef, veri.getCollateralType(),
							TOTALVALUATIONASPE);
				}
				if (!valAmounts.isEmpty()) {
					if (valAmounts.get(DOCVALUE) != null) {
						collateralCOP.put(collRef.concat(DOCVALUE),
								(PennantApplicationUtil.formateAmount(
										new BigDecimal(valAmounts.get(DOCVALUE).toString()),
										PennantConstants.defaultCCYDecPos)));
					}
					if (valAmounts.get(TOTALVALUATIONASPE) != null) {
						collateralCOP.put(collRef.concat(TOTALVALUATIONASPE),
								new BigDecimal(valAmounts.get(TOTALVALUATIONASPE).toString()));
					}
				}
			}
		}

		if (!collateralCOP.isEmpty()) {
			for (Verification verification : verifications) {
				if (collateralCOP.containsKey(verification.getReferenceFor().concat(DOCVALUE))) {
					verification.setValueForCOP(collateralCOP.get(verification.getReferenceFor().concat(DOCVALUE)));
				}
				if (collateralCOP.containsKey(verification.getReferenceFor().concat(TOTALVALUATIONASPE))) {
					verification.setFinalValAsPerPE(
							collateralCOP.get(verification.getReferenceFor().concat(TOTALVALUATIONASPE)));
				}
			}
		}

		renderList(verifications);
	}

	private boolean isNotDeleted(String recordType) {
		return !(PennantConstants.RECORD_TYPE_DEL.equals(recordType)
				|| PennantConstants.RECORD_TYPE_CAN.equals(recordType));
	}

	private void renderVerificationForAddDisbursement(List<CollateralAssignment> collaterals,
			FinanceDetail financeDetail) {

		if (collaterals != null) {
			renderList(getFinalVerifications(collaterals, financeDetail));
		}
	}

	public void addCollaterals(List<CollateralAssignment> collaterals, FinanceDetail financeDetail) {
		if (!initType) {
			return;
		}
		if (collaterals != null) {
			if (financeDetail != null && CollectionUtils.isNotEmpty(financeDetail.getCollaterals())) {
				if (CollectionUtils.isNotEmpty(financeDetail.getCollaterals().get(0).getExtendedFieldRenderList())) {
					Map<String, Object> map = financeDetail.getCollaterals().get(0).getExtendedFieldRenderList().get(0)
							.getMapValues();
					propCity = String.valueOf(map.get(ImplementationConstants.VER_TV_COLL_ED_ADDR_COLUMN));
				}
				collateralCity.put(financeDetail.getCollaterals().get(0).getCollateralRef(), propCity);
			}
			renderList(getFinalVerifications(collaterals, financeDetail));
		}
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
		current.setVerificationType(VerificationType.TV.getKey());
		current.setVerificationCategory(previous.getVerificationCategory());
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

	private List<Verification> getFinalVerifications(List<CollateralAssignment> collaterals,
			FinanceDetail financeDetails) {
		List<Verification> verifications = new ArrayList<>();
		Map<String, Verification> collateralMap = new HashMap<>();
		Set<String> deletedSet = new HashSet<>();
		List<Verification> tempVerifications = new ArrayList<>();
		Map<String, Verification> newcollateralMap = new HashMap<>();
		boolean exists = false;
		Verification newVrf;

		if (initType) {
			// Prepare Customer Collateral's
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
		// PSD#157163 UAT2 : TV Initiation : able to initiate TV for deleted collateral.
		List<Verification> tempOldVerifications = new ArrayList<>();
		if (enqiryModule) {
			collaterals = Collections.emptyList();
			tempOldVerifications.addAll(oldVerifications);
		}
		if (CollectionUtils.isNotEmpty(collaterals)) {
			for (Verification verification : oldVerifications) {
				for (CollateralAssignment collateral : collaterals) {
					if (StringUtils.equals(verification.getReferenceFor(), collateral.getCollateralRef())) {
						for (CollateralSetup csp : financeDetails.getCollaterals()) {
							if (StringUtils.equals(csp.getCollateralRef(), collateral.getCollateralRef())) {
								if (CollectionUtils.isNotEmpty(csp.getExtendedFieldRenderList())) {
									ExtendedFieldRender eh = csp.getExtendedFieldRenderList().get(0);
									if (eh.getMapValues().containsKey("costofproperty")) {
										BigDecimal cop = (BigDecimal) csp.getExtendedFieldRenderList().get(0)
												.getMapValues().get("costofproperty");
										verification.setValueForCOP(cop);
									}

									if (eh.getMapValues().containsKey("trscntype")) {
										String tp = (String) csp.getExtendedFieldRenderList().get(0).getMapValues()
												.get("trscntype");
										verification.setCollTranType(StringUtils.trimToEmpty(tp));
									}
								}
							}
						}

						tempOldVerifications.add(verification);
					}
				}
			}
		}
		tempVerifications.addAll(tempOldVerifications);
		for (Verification previous : tempVerifications) {
			if (previous.getReinitid() != null) {
				verifications.add(previous);
				tempOldVerifications.remove(previous);
				continue;
			}

			// create new Verification if initiated Collateral has Changed
			Verification current = collateralMap.get(previous.getReferenceFor());
			if (current != null) {
				for (CollateralAssignment collateral : collaterals) {
					if (StringUtils.equals(collateral.getCollateralRef(), previous.getReferenceFor())) {
						boolean collateralChanged = false;
						if (ImplementationConstants.VER_INITATE_DURING_SAVE) {
							collateralChanged = technicalVerificationService.isCollateralChanged(previous,
									TableType.TEMP_TAB);
						} else {
							collateralChanged = technicalVerificationService.isCollateralChanged(previous,
									TableType.MAIN_TAB);
						}
						if (collateralChanged) {
							if (verificationService.isVerificationInRecording(previous, VerificationType.TV)) {
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

				if (StringUtils.equals(FinServiceEvent.ADDDISB, moduleDefiner) && (StringUtils.equals("Approved",
						financeDetail.getFinScheduleData().getFinanceMain().getRecordStatus()))) {
					current.setNewRecord(true);
				} else {
					current.setNewRecord(false);
				}
				tempOldVerifications.remove(previous);
				newcollateralMap.put(current.getReferenceFor(), current);
				collateralMap.remove(current.getReferenceFor());
			}
		}

		verifications.addAll(newcollateralMap.values());
		verifications.addAll(collateralMap.values());
		verifications.addAll(tempOldVerifications);

		for (Verification object : verifications) {
			if ((deletedSet.contains(object.getReferenceFor()) && (object.isNewRecord()
					|| !verificationService.isVerificationInRecording(object, VerificationType.TV)))) {
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

			List<Verification> collateralDetails = getCollateralDetails(financeDetails, references);

			for (Verification item : collateralDetails) {
				for (Verification ver : verifications) {
					if (StringUtils.equals(item.getReferenceFor(), ver.getReferenceFor())) {
						ver.setReferenceType(item.getReferenceType());
						ver.setReference(item.getReference());
						ver.setCif(item.getReference());
						ver.setCustomerName(item.getCustomerName());
						ver.setVerificationReq(item.isVerificationReq());

						if (ver.isNewRecord()) {
							if (ver.isVerificationReq()) {
								ver.setRequestType(RequestType.INITIATE.getKey());
							} else {
								ver.setRequestType(RequestType.NOT_REQUIRED.getKey());
							}

						}
					}
				}
			}

			for (Verification ver : verifications) {
				verificationService.setLastStatus(ver);
			}
		}

		return verifications;
	}

	private List<Verification> getCollateralDetails(FinanceDetail financeDetails, String[] references) {

		List<Verification> newCollateralDetails = new ArrayList<Verification>();

		List<Verification> collateralDetails = verificationService.getCollateralDetails(references);

		if (financeDetails != null) {
			List<CollateralSetup> collateralSetupList = financeDetails.getCollaterals();
			CustomerDetails customerDetails = financeDetails.getCustomerDetails();

			if (collateralSetupList != null && !collateralSetupList.isEmpty()) {
				for (CollateralSetup collateralSetup : collateralSetupList) {
					if (PennantConstants.RECORD_TYPE_NEW.equals(collateralSetup.getRecordType())) {
						for (int i = 0; i < references.length; i++) {
							String collReference = references[i];
							if (collateralSetup.getCollateralRef().equals(collReference)) {
								boolean isAdded = false;
								for (Verification verification : collateralDetails) {
									if (verification.getReferenceFor().equals(collReference)) {
										isAdded = true;
										break;
									}
								}
								if (!isAdded) {
									Verification verification = new Verification();
									verification.setReferenceFor(collateralSetup.getCollateralRef());
									verification.setReferenceType(collateralSetup.getCollateralType());
									verification.setReference(customerDetails.getCustomer().getCustCIF());
									verification.setCustomerName(customerDetails.getCustomer().getCustShrtName());
									verification.setVerificationReq(
											collateralSetup.getCollateralStructure().isCollateralValuatorReq());
									newCollateralDetails.add(verification);
								}
							}
						}
					}
				}
			}
		}

		collateralDetails.addAll(newCollateralDetails);
		return collateralDetails;
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
			final Map<String, Object> map = new HashMap<>();
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

	public void onChnageTv(ForwardEvent event) {
		Listitem listitem = (Listitem) event.getData();

		Combobox cfiv = (Combobox) getComponent(listitem, "RequestType");
		ExtendedCombobox cAgency = (ExtendedCombobox) getComponent(listitem, "Agency");
		ExtendedCombobox cReason = (ExtendedCombobox) getComponent(listitem, "Reason");

		onchangeVerificationType(cfiv, cAgency, cReason);
	}

	public void onChangeVerificationCategory(ForwardEvent event) {
		Listitem listitem = (Listitem) event.getData();
		ExtendedCombobox cAgency = null;
		Combobox verificationCate = (Combobox) getComponent(listitem, "VerificationCategory");
		if (initType) {
			cAgency = (ExtendedCombobox) getComponent(listitem, "Agency");
		} else {
			cAgency = (ExtendedCombobox) getComponent(listitem, "ReInitAgency");
		}
		Label collRef = (Label) getComponent(listitem, "ReferenceFor");
		Label collType = (Label) getComponent(listitem, "ReferenceType");
		cAgency.setValue("");
		fillAgencies(cAgency, verificationCate, collRef.getValue(), collType.getValue());
	}

	private void onchangeVerificationType(Combobox cfiv, ExtendedCombobox cAgency, ExtendedCombobox cReason) {
		RequestType type;
		type = RequestType.getType(NumberUtils.toInt(cfiv.getSelectedItem().getValue()));

		if (type == null) {
			cAgency.setValue("");
			cReason.setValue("");
			cAgency.setReadonly(true);
			cReason.setReadonly(true);
		} else {
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
	}

	public void onChangeAgency(ForwardEvent event) {
		Listitem listitem = (Listitem) event.getData();
		ExtendedCombobox agency = (ExtendedCombobox) getComponent(listitem, "Agency");
		Label collRefLabel = (Label) getComponent(listitem, "ReferenceFor");

		Object dataObject = agency.getObject();

		if (dataObject != null) {
			List<Listitem> listItems = listBoxTechnicalVerification.getItems();
			int selectedIndex = listitem.getIndex();
			for (Listitem listItm : listItems) {
				int listItemIndex = listItm.getIndex();
				if (selectedIndex != listItemIndex && (StringUtils.isBlank(moduleDefiner))) {
					ExtendedCombobox agenc = (ExtendedCombobox) getComponent(listItm, "Agency");
					Label collRef = (Label) getComponent(listItm, "ReferenceFor");
					if (collRefLabel.getValue().equals(collRef.getValue())
							&& agenc.getValue().equals(agency.getValue())) {
						agency.setValue("");
						MessageUtil.showMessage(
								"Agency already selected for the collateral ".concat(collRefLabel.getValue()));
						break;
					}
				}
			}
		}

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
		Label collRefLabel = (Label) getComponent(listitem, "ReferenceFor");

		Object dataObject = agency.getObject();

		if (dataObject != null) {
			List<Listitem> listItems = listBoxTechnicalVerification.getItems();
			int selectedIndex = listitem.getIndex();
			for (Listitem listItm : listItems) {
				int listItemIndex = listItm.getIndex();
				if (selectedIndex != listItemIndex) {
					ExtendedCombobox reInitAgency = (ExtendedCombobox) getComponent(listItm, "ReInitAgency");
					Label collRef = (Label) getComponent(listItm, "ReferenceFor");
					if (collRefLabel.getValue().equals(collRef.getValue())
							&& reInitAgency.getValue().equals(agency.getValue())) {
						agency.setValue("");
						MessageUtil.showMessage(
								"Agency already selected for the collateral ".concat(collRefLabel.getValue()));
						break;
					}
				}
			}
		}

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
		final Map<String, Object> map = new HashMap<>();
		TechnicalVerification technicalVerification = technicalVerificationService
				.getTechnicalVerification(tv.getSelectedItem().getValue(), "_View");

		if (technicalVerification != null
				&& technicalVerification.getVerificationCategory() == VerificationCategory.ONEPAGER.getKey()) {
			technicalVerificationService.getDocumentImage(technicalVerification);
		}

		if (tvInquiry.getChildren().size() >= 2) {
			tvInquiry.getChildren().remove(1);
		}
		if (technicalVerification != null && StringUtils.isEmpty(technicalVerification.getNextRoleCode())) {
			map.put("LOAN_ORG", true);
			map.put("technicalVerification", technicalVerification);
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

		boolean isRequest = getUserWorkspace().isAllowed("FinanceMainDialog_TVInitiation");

		if (fromVerification) {
			isRequest = false;
		}

		boolean isButtonVisible = false;
		List<String> collatrealRef = new ArrayList<>();

		if (listBoxTechnicalVerification.getItems() != null) {
			listBoxTechnicalVerification.getItems().clear();
		}

		deletedList.clear();

		// set Initiated flag to initiated Records
		setInitiated(verifications);

		// Render Verifications
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
			if (!initType && !fromVerification) {
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
			listCell = new Listcell();
			listCell.setId("CustName".concat(String.valueOf(i)));
			listCell.appendChild(new Label(vrf.getCustomerName()));
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

			if (!isRequest && reqType == RequestType.REQUEST.getKey()) {
				reqType = RequestType.INITIATE.getKey();
				for (ValueLabel valueLabel : list) {
					if (valueLabel.getValue().equals(RequestType.REQUEST.getKey().toString())) {
						list.remove(list.indexOf(valueLabel));
						break;
					}
				}
			}

			if (isRequest) {
				reqType = RequestType.REQUEST.getKey();
				requestType.setDisabled(true);
			}

			fillComboBox(requestType, reqType, list, "");

			requestType.setParent(listCell);
			listCell.setParent(item);

			// Verification Category
			listCell = new Listcell();
			listCell.setId("VerificationCategory".concat(String.valueOf(i)));
			Combobox verificationCategory = new Combobox();
			verificationCategory.setWidth("150px");
			verificationCategory.setReadonly(true);
			verificationCategory.setValue(String.valueOf(vrf.getVerificationCategory()));

			List<ValueLabel> verificationCatList = new ArrayList<>();
			verificationCatList = VerificationCategory.getList();
			int verificationCate = vrf.getVerificationCategory();
			if (verificationCate == 0) {
				verificationCate = VerificationCategory.EXTERNAL.getKey();
			}

			if (isRequest) {
				verificationCategory.setDisabled(true);
			}

			fillComboBox(verificationCategory, verificationCate, verificationCatList,
					"," + VerificationCategory.ONEPAGER.getKey().toString().concat(","));
			listCell.appendChild(verificationCategory);
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
			fillAgencies(agency, verificationCategory, vrf.getReferenceFor(), vrf.getReferenceType());
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

			if (isRequest) {
				remarks.setReadonly(true);
			}

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

			if (!initType && !isButtonVisible && !fromVerification) {
				isButtonVisible = true;
				btnNew_FinalValuation.setVisible(true);
			}
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
				fillAgencies(reInitAgency, verificationCategory, vrf.getReferenceFor(), vrf.getReferenceType());
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

				if (enqiryModule) {
					decision.setDisabled(true);
				}
			}

			if (initType && !collatrealRef.contains(vrf.getReferenceFor()) && !isRequest && !enqiryModule) {
				collatrealRef.add(vrf.getReferenceFor());
				listCell = new Listcell();
				listCell.setId("AgencyButton".concat(String.valueOf(i)));
				Button addAgencyButton = new Button();
				addAgencyButton.setLabel("ADD AGENCY");
				listCell.appendChild(addAgencyButton);
				listCell.setParent(item);
				listheader_TechnicalVerification_AddAgency.setVisible(true);
				addAgencyButton.addForward("onClick", self, "onClickAgencyButton", item);
			} else if (initType) {
				listCell = new Listcell();
				listCell.setParent(item);
			}

			requestType.addForward("onChange", self, "onChnageTv", item);
			agency.addForward("onFulfill", self, "onChangeAgency", item);
			reason.addForward("onFulfill", self, "onChangeReason", item);
			verificationCategory.addForward("onChange", self, "onChangeVerificationCategory", item);

			onchangeVerificationType(requestType, agency, reason);

			item.setAttribute("verification", vrf);

			this.listBoxTechnicalVerification.appendChild(item);

			if (!initType || vrf.isInitiated()) {
				requestType.setDisabled(true);
				agency.setReadonly(true);
				reason.setReadonly(true);
				remarks.setReadonly(true);
				verificationCategory.setDisabled(true);
			}

			if (enqiryModule) {
				if (!fromVerification) {
					listheader_TechnicalVerification_ReInitAgency.setVisible(false);
					listheader_TechnicalVerification_ReInitRemarks.setVisible(false);
				}
				requestType.setDisabled(true);
				agency.setReadonly(true);
				reason.setReadonly(true);
				remarks.setReadonly(true);
				verificationCategory.setDisabled(true);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onClickAgencyButton(ForwardEvent event) {

		int noOfVerification = 0;

		Listitem listItem = (Listitem) event.getData();
		Label referenceFor = (Label) getComponent(listItem, "ReferenceFor");

		List<Listitem> listItems = this.listBoxTechnicalVerification.getItems();

		if (CollectionUtils.isNotEmpty(listItems)) {
			for (Listitem listIt : listItems) {
				Label collateralRef = (Label) getComponent(listIt, "ReferenceFor");
				if (StringUtils.equals(collateralRef.getValue(), referenceFor.getValue())) {
					noOfVerification++;
				}

				if (noOfVerification == 3) {
					MessageUtil.showMessage(Labels.getLabel("label_TVInitiation_AgencyValidation.value"));
					return;
				}
			}
		}

		Label refType = (Label) getComponent(listItem, "ReferenceType");
		Label reference = (Label) getComponent(listItem, "Reference");
		Label custName = (Label) getComponent(listItem, "CustName");
		Verification verification = new Verification();
		verification.setNewRecord(true);
		verification.setReferenceType(refType.getValue());
		verification.setCif(reference.getValue());
		verification.setReference(reference.getValue());
		verification.setCustomerName(custName.getValue());
		verification.setReferenceFor(referenceFor.getValue());
		verification.setVerificationType(VerificationType.TV.getKey());
		renderVerification(verification);

	}

	private void renderVerification(Verification verification) {
		Listitem item = new Listitem();
		Listcell listCell;

		int i = this.listBoxTechnicalVerification.getItems().size();
		i = i + 1;
		// Collateral Type
		listCell = new Listcell();
		listCell.setId("ReferenceType".concat(String.valueOf(i)));
		listCell.appendChild(new Label(verification.getReferenceType()));
		listCell.setParent(item);

		// Depositor CIF
		listCell = new Listcell();
		listCell.setId("Reference".concat(String.valueOf(i)));
		listCell.appendChild(new Label(verification.getCif()));
		listCell.setParent(item);

		// Depositor Name
		listCell = new Listcell();
		listCell.setId("CustName".concat(String.valueOf(i)));
		listCell.appendChild(new Label(verification.getCustomerName()));
		listCell.setParent(item);

		// Collateral Reference
		listCell = new Listcell();
		listCell.setId("ReferenceFor".concat(String.valueOf(i)));
		listCell.appendChild(new Label(verification.getReferenceFor()));
		listCell.setParent(item);

		// TV
		listCell = new Listcell();
		listCell.setId("RequestType".concat(String.valueOf(i)));
		Combobox requestType = new Combobox();
		requestType.setWidth("200px");
		requestType.setReadonly(true);
		requestType.setValue(String.valueOf(verification.getRequestType()));
		List<ValueLabel> list = RequestType.getList();
		listCell.appendChild(requestType);
		listCell.setParent(item);
		list.remove(3);
		fillComboBox(requestType, RequestType.INITIATE.getKey(), list, "");

		// Verification Category
		Combobox verificationCategory = new Combobox();
		if (SysParamUtil.isAllowed(SMTParameterConstants.VERIFICATION_CATEGORY_REQUIRED)) {
			listCell = new Listcell();
			listCell.setId("VerificationCategory".concat(String.valueOf(i)));
			verificationCategory.setWidth("150px");
			verificationCategory.setReadonly(true);
			verificationCategory.setValue(String.valueOf(verification.getVerificationCategory()));

			List<ValueLabel> verificationCatList = new ArrayList<>();
			verificationCatList = VerificationCategory.getList();
			int verificationCate = verification.getVerificationCategory();
			if (verificationCate == 0) {
				verificationCate = VerificationCategory.EXTERNAL.getKey();
			}

			fillComboBox(verificationCategory, verificationCate, verificationCatList, "");
			listCell.appendChild(verificationCategory);
			listCell.setParent(item);

			fillComboBox(verificationCategory, verificationCate, verificationCatList,
					"," + VerificationCategory.ONEPAGER.getKey().toString().concat(","));
		}

		listCell.appendChild(verificationCategory);
		listCell.setParent(item);

		// Agency
		listCell = new Listcell();
		listCell.setId("Agency".concat(String.valueOf(i)));
		ExtendedCombobox agency = new ExtendedCombobox();
		agency.setWidth("200px");
		agency.setHflex("min");
		if (verification.getAgencyName() != null) {
			agency.setValue(String.valueOf(verification.getAgencyName()));
			agency.setAttribute("agencyId", verification.getAgency());
			agency.setAttribute("oldAgencyId", verification.getAgency());
			agency.setAttribute("agencyName", verification.getAgencyName());
		}
		fillAgencies(agency, verificationCategory, verification.getReferenceFor(), verification.getReferenceType());
		listCell.appendChild(agency);
		listCell.setParent(item);

		// Reason
		listCell = new Listcell();
		listCell.setId("Reason".concat(String.valueOf(i)));
		ExtendedCombobox reason = new ExtendedCombobox();
		reason.setWidth("200px");
		if (verification.getReasonName() != null) {
			reason.setValue(verification.getReasonName());
			reason.setAttribute("reasonId", verification.getReason());
		}
		fillReasons(reason);
		listCell.appendChild(reason);
		listCell.setParent(item);

		// Remarks
		listCell = new Listcell();
		listCell.setId("Remarks".concat(String.valueOf(i)));
		Textbox remarks = new Textbox(verification.getRemarks());
		remarks.setWidth("200px");
		remarks.setMaxlength(500);
		listCell.appendChild(remarks);
		listCell.setParent(item);

		if (initType) {
			// Last Verification Agency
			listCell = new Listcell();
			listCell.appendChild(new Label(verification.getLastAgency()));
			listCell.setParent(item);
		}

		// Status
		listCell = new Listcell();
		Label status = new Label();

		if (initType && verification.getLastStatus() != 0) {
			status.setValue(TVStatus.getType(verification.getLastStatus()).getValue());

		} else if (!initType && verification.getStatus() != 0) {
			status.setValue(TVStatus.getType(verification.getStatus()).getValue());
		}

		listCell.appendChild(status);
		listCell.setParent(item);
		// Verification Date
		listCell = new Listcell();
		if (initType) {
			listCell.appendChild(new Label(DateUtil.formatToShortDate(verification.getLastVerificationDate())));
		} else {
			listCell.appendChild(new Label(DateUtil.formatToShortDate(verification.getVerificationDate())));
		}
		listCell.setParent(item);

		listCell = new Listcell();
		listCell.setId("DeleteAgency".concat(String.valueOf(i)));
		Button button = new Button();
		button.setSclass("z-toolbarbutton");
		button.setLabel("Delete");
		button.addForward("onClick", self, "onClickAgencyDeleteButton", item);
		listCell.appendChild(button);
		listCell.setParent(item);

		requestType.addForward("onChange", self, "onChnageTv", item);
		agency.addForward("onFulfill", self, "onChangeAgency", item);
		reason.addForward("onFulfill", self, "onChangeReason", item);
		verificationCategory.addForward("onChange", self, "onChangeVerificationCategory", item);

		onchangeVerificationType(requestType, agency, reason);

		item.setAttribute("verification", verification);

		this.listBoxTechnicalVerification.appendChild(item);

		if (!initType || verification.isInitiated()) {
			requestType.setDisabled(true);
			agency.setReadonly(true);
			reason.setReadonly(true);
			remarks.setReadonly(true);
		}
	}

	public void onClickAgencyDeleteButton(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Listitem item = (Listitem) event.getData();
		listBoxTechnicalVerification.removeItemAt(item.getIndex());
		logger.debug(Literal.LEAVING);
	}

	private void setInitiated(List<Verification> verifications) {
		for (Verification item : verifications) {
			if (item.getRequestType() == RequestType.INITIATE.getKey()
					&& verificationService.isVerificationInRecording(item, VerificationType.TV)) {
				item.setInitiated(true);
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
			fillComboBox(combobox, vrf.getDecision(), filterDecisions(decisionList), "");
		} else if (status == TVStatus.NEGATIVE.getKey()) {
			decisionList.add(new ValueLabel(String.valueOf(Decision.APPROVE.getKey()), Decision.APPROVE.getValue()));
			if (decision == Decision.APPROVE.getKey()) {
				vrf.setDecision(Decision.SELECT.getKey());
			}
			fillComboBox(combobox, vrf.getDecision(), filterDecisions(decisionList), "");
		} else if (requestType == RequestType.WAIVE.getKey()) {
			decisionList.add(new ValueLabel(String.valueOf(Decision.OVERRIDE.getKey()), Decision.OVERRIDE.getValue()));
			fillComboBox(combobox, decision, filterDecisions(decisionList), "");
		} else {
			fillComboBox(combobox, decision, Decision.getList(), "");
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

	private void fillAgencies(ExtendedCombobox agency, Combobox verificationCategory, String collRef, String collType) {
		logger.debug(Literal.ENTERING);

		if (!collateralCity.containsKey(collRef)) {
			String propCity = technicalVerificationService.getPropertyCity(collRef, collType);
			collateralCity.put(collRef, propCity);
		}

		if (collateralCity.containsKey(collRef) && StringUtils.isBlank(collateralCity.get(collRef))) {
			collateralCity.put(collRef, propCity);
		}

		agency.setModuleName("VerificationAgencies");
		agency.setValueColumn("DealerName");
		agency.setValidateColumns(new String[] { "DealerName" });
		Filter agencyFilter[] = null;
		if (SysParamUtil.isAllowed(SMTParameterConstants.VERIFICATION_CATEGORY_REQUIRED)) {
			agencyFilter = new Filter[2];
		} else {
			agencyFilter = new Filter[1];
		}

		agencyFilter[0] = new Filter("DealerType", Agencies.TVAGENCY.getKey(), Filter.OP_EQUAL);
		if (verificationCategory != null) {
			if (SysParamUtil.isAllowed(SMTParameterConstants.VERIFICATION_CATEGORY_REQUIRED)) {
				int verficationCate = Integer.parseInt(getComboboxValue(verificationCategory));
				if (verficationCate == VerificationCategory.INTERNAL.getKey()) {
					agencyFilter[1] = new Filter("DealerName", VerificationCategory.INTERNAL.getValue(),
							Filter.OP_EQUAL);
				} else if (verficationCate == VerificationCategory.ONEPAGER.getKey()) {
					agencyFilter[1] = new Filter("DealerName", VerificationCategory.ONEPAGER.getValue(),
							Filter.OP_EQUAL);
				} else {
					String[] agencies = new String[2];
					agencies[0] = VerificationCategory.INTERNAL.getValue();
					agencies[1] = VerificationCategory.ONEPAGER.getValue();
					agencyFilter[1] = new Filter("DealerName", agencies, Filter.OP_NOT_IN);
					// Commented the below lines to fix the external agency filter issue as per the core version
					// agencyFilter = Arrays.copyOf(agencyFilter, agencyFilter.length + 1);
					// agencyFilter[2] = new Filter("DealerCity", collateralCity.get(collRef), Filter.OP_EQUAL);

				}
			}
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

			if (Integer.parseInt(tvComboBox.getSelectedItem().getValue()) != RequestType.NOT_REQUIRED.getKey()) {
				if (!reasonComboBox.isReadonly()) {
					reasonComboBox.setConstraint(new PTStringValidator(
							Labels.getLabel("label_TechnicalVerificationDialog_Reason.value"), null, true, true));
				}
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

		item = (Verification) listitem.getAttribute("verification");

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
		case "VerificationCategory":
			String verCategory = ((Combobox) getComponent(listitem, "VerificationCategory")).getSelectedItem()
					.getValue();
			if (!verCategory.equals(PennantConstants.List_Select)) {
				item.setVerificationCategory(Integer.parseInt(verCategory));
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
			if (!combobox.isDisabled() && decision == 0 && !this.recSave) {
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
				throw new WrongValueException(textbox, Labels.getLabel("label_OVERRIDE_Validation"));
			}
			break;
		default:
			break;
		}

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
		for (int i = 0; i < listBoxTechnicalVerification.getItems().size(); i++) {

			Listitem listitem = listBoxTechnicalVerification.getItemAtIndex(i);

			item = (Verification) listitem.getAttribute("verification");

			if (item == null) {
				continue;
			}

			try {
				item = getVerification(listitem, "RequestType");

			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				item = getVerification(listitem, "VerificationCategory");
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

	private void fillComboBox(Combobox combobox, int value, List<ValueLabel> list, String excludeFields) {
		combobox.getChildren().clear();

		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue(PennantConstants.List_Select);
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		combobox.setReadonly(true);

		for (ValueLabel valueLabel : list) {

			if (!excludeFields.contains("," + valueLabel.getValue() + ",")) {
				comboitem = new Comboitem();
				comboitem.setValue(valueLabel.getValue());
				comboitem.setLabel(valueLabel.getLabel());
				combobox.appendChild(comboitem);
			}

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

	public boolean doSave(FinanceDetail financeDetail, Tab tab, boolean recSave, Radiogroup userAction) {
		logger.debug(Literal.ENTERING);
		this.recSave = recSave;
		this.userAction = userAction;
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
		financeDetail.setTvVerification(this.verification);

		// then only The below code will use only if the tab is approval tab
		List<Verification> verificationList = financeDetail.getTvVerification().getVerifications();
		boolean validTV = false;
		if (tab != null && tab.getId().equals("TAB".concat(AssetConstants.UNIQUE_ID_TVAPPROVAL))) {
			validTV = validateReinitiation(verificationList);
			if (!validTV) {
				return validTV;
			}

			Map<String, BigDecimal> valAmounts = new HashMap<>();
			BigDecimal finalvalAmt = BigDecimal.ZERO;
			BigDecimal loanAmt = BigDecimal.ZERO;

			if (!StringUtils.contains(userAction.getSelectedItem().getValue(), (PennantConstants.RCD_STATUS_SAVED))
					&& !StringUtils.contains(userAction.getSelectedItem().getValue(),
							(PennantConstants.RCD_STATUS_RESUBMITTED))
					&& !StringUtils.contains(userAction.getSelectedItem().getValue(),
							(PennantConstants.RCD_STATUS_REJECTED))
					&& !StringUtils.contains(userAction.getSelectedItem().getValue(),
							(PennantConstants.RCD_STATUS_CANCELLED))) {
				for (Verification verification : verificationList) {
					if (!(verification.getRequestType() == RequestType.REQUEST.getKey())) {

						if (!valAmounts.containsKey(verification.getReferenceFor())) {
							valAmounts.put(verification.getReferenceFor(), verification.getFinalValAmt());
						}

						// For request type waive skip the collateral validations
						if (verification.getRequestType() != 2 || verification.getRequestType() != 3) {
							if (!"OK".equals(verification.getFinalValDecision())) {
								MessageUtil.showError("Collateral final valuation not met.");
								return false;
							}
						}
					}
				}

				for (Map.Entry<String, BigDecimal> entry : valAmounts.entrySet()) {
					finalvalAmt = finalvalAmt.add(entry.getValue());
				}

				if (financeDetail.getFinScheduleData().getFinanceType().getFinLTVCheck()
						.equals(PennantConstants.COLLATERAL_LTV_CHECK_DISBAMT)) {
					loanAmt = financeDetail.getFinScheduleData().getFinanceMain().getFinAmount();
				} else if (financeDetail.getFinScheduleData().getFinanceType().getFinLTVCheck()
						.equals(PennantConstants.COLLATERAL_LTV_CHECK_FINAMT)) {
					loanAmt = financeDetail.getFinScheduleData().getFinanceMain().getFinAssetValue();
				}
				String formatFinalValAmt = PennantApplicationUtil.amountFormate(finalvalAmt,
						PennantConstants.defaultCCYDecPos);
				String formatLoanAmt = PennantApplicationUtil.amountFormate(loanAmt, PennantConstants.defaultCCYDecPos);
				for (Verification verification : verificationList) {
					// For request type waive skip the collateral validations
					if (!(verification.getRequestType() == RequestType.REQUEST.getKey()
							|| verification.getRequestType() == RequestType.WAIVE.getKey())) {
						if (finalvalAmt.compareTo(loanAmt) < 0) {
							MessageUtil.showError("Valuation amount :".concat(formatFinalValAmt)
									.concat(" is lesser than the loan amount :".concat(formatLoanAmt)));
							return false;
						}
					}
				}

				BigDecimal collAssignment = BigDecimal.ZERO;

				for (Verification verification : verificationList) {
					// For request type waive skip the collateral validations
					if (!(verification.getRequestType() == RequestType.REQUEST.getKey())
							&& !(verification.getRequestType() == RequestType.WAIVE.getKey())
							&& !(verification.getRequestType() == RequestType.NOT_REQUIRED.getKey())) {
						for (CollateralAssignment collData : financeDetail.getTempCollateralAssignmentList()) {
							if (verification.getReferenceFor().equals(collData.getCollateralRef())) {
								collAssignment = collAssignment.add(collData.getBankValuation());
							}
						}
					}
				}
				String msg = null;
				if ((finalvalAmt.compareTo(collAssignment) < 0) && (finalvalAmt.compareTo(loanAmt) < 0)) {
					String valuation = PennantApplicationUtil.amountFormate(finalvalAmt,
							PennantConstants.defaultCCYDecPos);
					String collAssignmentAmt = PennantApplicationUtil.amountFormate(collAssignment,
							PennantConstants.defaultCCYDecPos);
					String loanAmount = PennantApplicationUtil.amountFormate(loanAmt,
							PennantConstants.defaultCCYDecPos);
					msg = String.format(
							"Collateral valuation %s is less than the collateral assignment %s or loan amount %s",
							valuation, collAssignmentAmt, loanAmount);
					MessageUtil.showError(msg);
					return false;
				}
			}
		}

		logger.debug(Literal.LEAVING);

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

	public void onClick$btnNew_FinalValuation(Event event) {
		logger.debug(Literal.ENTERING);
		Map<String, Object> arg = new HashMap<>();
		List<String> collatearlRef = new ArrayList<>();

		if (!verifications.isEmpty()) {
			for (Verification verification : verifications) {
				if (!collatearlRef.contains(verification.getReferenceFor()))
					collatearlRef.add(verification.getReferenceFor());
			}
		}
		arg.put("CollateralRef", collatearlRef);
		arg.put("verifications", verifications);
		arg.put("tVerificationDialogCtrl", this);
		arg.put("enqiryModule", enqiryModule);
		arg.put("financeDetail", financeDetail);
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/FinalValuationDialog.zul",
					null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	// ++++++++++Technical verification initiation out side the loan start++++++++++++++++++++//
	/*
	 * Saving the Technical verification initiation out side the loan.
	 */
	public void onClick$btnTVInitiateSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave(financeDetail, null, recSave, tv);
		try {
			verificationService.saveOrUpdate(financeDetail, VerificationType.TV, PennantConstants.TRAN_WF, initType);
			refreshList();
			String msg = Labels.getLabel("TV_INITIATION",
					new String[] { financeDetail.getFinScheduleData().getFinReference() });
			Clients.showNotification(msg, "info", null, null, -1);
			closeDialog();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	protected void refreshList() {
		tvInitiationListCtrl.search();
	}

	public void onClick$btnTVInitiateClose(Event event) {
		doClose(this.btnTVInitiateSave.isVisible());
	}

	// ++++++++++Technical verification initiation out side the loan end++++++++++++++++++++//
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

	public List<Verification> getVerifications() {
		return verifications;
	}
}

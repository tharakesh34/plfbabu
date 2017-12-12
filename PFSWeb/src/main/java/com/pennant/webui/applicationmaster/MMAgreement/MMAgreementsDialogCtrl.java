package com.pennant.webui.applicationmaster.MMAgreement;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.aspose.words.SaveFormat;
import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.RateBox;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.MMAgreement.MMAgreement;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.finance.AgreementDetail;
import com.pennant.backend.service.applicationmaster.MMAgreementService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.TemplateEngine;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTPhoneNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

import javassist.NotFoundException;

public class MMAgreementsDialogCtrl extends GFCBaseCtrl<MMAgreement> {
	private static final long				serialVersionUID	= 9031340167587772517L;
	private static final Logger				logger				= Logger.getLogger(MMAgreementsDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_MMAgreementDialog;
	protected Textbox mMAReference;
	protected ExtendedCombobox custCIF;
	protected CurrencyBox contractAmt;
	protected ExtendedCombobox purchRegOffice;
	protected Textbox purchaddress;
	protected Textbox fax;
	protected Textbox faxCountryCode;
	protected Textbox faxAreaCode;
	protected Textbox attention;

	protected Datebox contractDate;
	protected Textbox titleNo;
	protected Decimalbox rate;
	protected Combobox product;
	protected Combobox agreeName;
	protected Combobox mMAgreeType;
	protected ExtendedCombobox city;
	protected ExtendedCombobox province;
	protected ExtendedCombobox country;
	// facility Details
	protected ExtendedCombobox folReference;
	protected Datebox fOLIssueDate;
	protected Datebox maturityDate;
	protected CurrencyBox facilityLimit;
	protected RateBox rateCode;
	protected Decimalbox profitRate;
	protected Decimalbox minRate;
	protected Decimalbox latePayRate;
	protected CurrencyBox minAmount;
	protected Intbox numberOfTerms;
	protected Intbox profitPeriod;

	protected Intbox avlPerDays;
	protected Decimalbox maxCapProfitRate;
	protected Decimalbox minCapRate;
	protected Datebox facOfferLetterDate;
	protected Intbox numOfContracts;
	protected Textbox pmaryRelOfficer;
	protected AccountSelectionBox custAccount;

	protected CurrencyBox assetValue;
	protected Textbox assetDesc;
	protected CurrencyBox sharePerc;
	protected Textbox custPOBox;
	protected ExtendedCombobox dealer;

	protected Grid grid_basicDetails;
	protected Radiogroup numberOfCommits;

	private transient boolean validationOn;

	private int ccyformatt = 0;
	// not auto wired Var's
	private MMAgreement aMMAgreement; // overHanded per
	private transient MMAgreementsListCtrl aMMAgreementsListCtrl; // overHanded
	protected JdbcSearchObject<Customer> custCIFSearchObject;

	// ServiceDAOs / Domain Classes
	private transient MMAgreementService mMAgreementService;

	/**
	 * default constructor.<br>
	 */
	public MMAgreementsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "MMAgreementsDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_MMAgreementDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_MMAgreementDialog);

		logger.debug("Entering");

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();
			if (arguments.containsKey("MMAgreement")) {
				this.aMMAgreement = (MMAgreement) arguments.get("MMAgreement");
				MMAgreement befImage = new MMAgreement();
				BeanUtils.copyProperties(this.aMMAgreement, befImage);
				this.aMMAgreement.setBefImage(befImage);

				setMMAgreement(this.aMMAgreement);
			} else {
				setMMAgreement(null);
			}

			doLoadWorkFlow(this.aMMAgreement.isWorkflow(),
					this.aMMAgreement.getWorkflowId(),
					this.aMMAgreement.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"MMAgreementsDialog");
			}

			// READ OVERHANDED parameters !
			// we get the MMAgreement controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete currency here.
			if (arguments.containsKey("MMAgreementsListCtrl")) {
				setaMMAgreementsListCtrl((MMAgreementsListCtrl) arguments
						.get("MMAgreementsListCtrl"));
			} else {
				setaMMAgreementsListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getMMAgreement());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_MMAgreementDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		this.mMAReference.setMaxlength(20);
		this.custCIF.setMaxlength(6);
		this.custCIF.setMandatoryStyle(true);
		this.custCIF.setModuleName("Customer");
		this.custCIF.setValueColumn("CustCIF");
		this.custCIF.setDescColumn("CustShrtName");
		this.custCIF.setValidateColumns(new String[] { "CustCIF" });
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("CustCoreBank", "", Filter.OP_NOT_EQUAL);
		this.custCIF.setFilters(filters);
		ccyformatt = SysParamUtil
				.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT);
		this.purchRegOffice.setMandatoryStyle(true);
		this.purchRegOffice.setTextBoxWidth(161);
		this.purchRegOffice.setModuleName("Province");
		this.purchRegOffice.setValueColumn("CPProvince");
		this.purchRegOffice.setDescColumn("CPProvinceName");
		this.purchRegOffice.setValidateColumns(new String[] { "CPProvince" });
		Filter[] purchRegOffice = new Filter[1];
		purchRegOffice[0] = new Filter("CPCountry", "AE", Filter.OP_EQUAL);
		this.purchRegOffice.setFilters(purchRegOffice);
		this.contractAmt.setMandatory(true);
		this.contractAmt.setFormat(PennantApplicationUtil
				.getAmountFormate(ccyformatt));
		this.contractAmt.setScale(ccyformatt);
		this.contractAmt.setValue(BigDecimal.ZERO);
		this.contractDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.faxCountryCode.setMaxlength(4);
		this.faxAreaCode.setMaxlength(4);
		this.fax.setMaxlength(8);
		this.rate.setMaxlength(13);
		this.rate.setFormat(PennantConstants.rateFormate9);
		this.rate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.rate.setScale(9);

		this.country.setMandatoryStyle(true);
		this.country.setModuleName("Country");
		this.country.setValueColumn("CountryCode");
		this.country.setDescColumn("CountryDesc");
		this.country.setValidateColumns(new String[] { "CountryCode" });

		this.province.setMaxlength(8);
		this.province.setMandatoryStyle(true);
		this.province.setModuleName("Province");
		this.province.setValueColumn("CPProvince");
		this.province.setDescColumn("CPProvinceName");
		this.province.setValidateColumns(new String[] { "CPProvince" });

		this.city.setMaxlength(8);
		this.city.setMandatoryStyle(false);
		this.city.setModuleName("City");
		this.city.setValueColumn("PCCity");
		this.city.setDescColumn("PCCityName");
		this.city.setValidateColumns(new String[] { "PCCity" });
		// Facility Details
		this.folReference.setMandatoryStyle(true);
		this.folReference.setModuleName("CustomerLimit");
		this.folReference.setValueColumn("LimitRef");
		this.folReference.setDescColumn("LimitDesc");
		this.folReference.setValidateColumns(new String[] { "LimitRef" });

		this.fOLIssueDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.maturityDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.facOfferLetterDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.facilityLimit.setMandatory(true);
		this.facilityLimit.setFormat(PennantApplicationUtil
				.getAmountFormate(ccyformatt));
		this.facilityLimit.setScale(ccyformatt);
		this.facilityLimit.setValue(BigDecimal.ZERO);

		this.assetValue.setMandatory(true);
		this.assetValue.setFormat(PennantApplicationUtil
				.getAmountFormate(ccyformatt));
		this.assetValue.setScale(ccyformatt);
		this.assetValue.setValue(BigDecimal.ZERO);

		this.sharePerc.setMandatory(true);
		this.sharePerc.setFormat(PennantApplicationUtil
				.getAmountFormate(ccyformatt));
		this.sharePerc.setScale(ccyformatt);
		this.sharePerc.setValue(BigDecimal.ZERO);

		// this.minAmount.setMandatory(true);
		this.minAmount.setFormat(PennantApplicationUtil
				.getAmountFormate(ccyformatt));
		this.minAmount.setScale(ccyformatt);
		this.minAmount.setValue(BigDecimal.ZERO);

		this.profitRate.setMaxlength(13);
		this.profitRate.setFormat(PennantConstants.rateFormate9);
		this.profitRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.profitRate.setScale(9);

		this.minRate.setMaxlength(13);
		this.minRate.setFormat(PennantConstants.rateFormate9);
		this.minRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.minRate.setScale(9);
		this.rateCode.setBaseProperties("BaseRateCode", "BRType", "BRTypeDesc");
		this.latePayRate.setMaxlength(13);
		this.latePayRate.setFormat(PennantConstants.rateFormate9);
		this.latePayRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.latePayRate.setScale(9);

		this.maxCapProfitRate.setMaxlength(13);
		this.maxCapProfitRate.setFormat(PennantConstants.rateFormate9);
		this.maxCapProfitRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.maxCapProfitRate.setScale(9);

		this.minCapRate.setMaxlength(13);
		this.minCapRate.setFormat(PennantConstants.rateFormate9);
		this.minCapRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.minCapRate.setScale(9);
		this.avlPerDays.setMaxlength(9);
		this.dealer.setInputAllowed(false);
		this.dealer.setDisplayStyle(3);
		// this.dealer.setMandatoryStyle(true);
		this.dealer.setModuleName("VehicleDealer");
		this.dealer.setValueColumn("DealerId");
		this.dealer.setDescColumn("DealerName");
		this.dealer.setValidateColumns(new String[] { "DealerId" });
		Filter dealerfilter[] = new Filter[1];
		dealerfilter[0] = new Filter("SellerType", PennantConstants.DEALER,
				Filter.OP_EQUAL);
		this.dealer.setFilters(dealerfilter);
		this.attention.setMaxlength(50);
		this.dealer.setTextBoxWidth(161);
		this.purchaddress.setMaxlength(100);
		this.assetDesc.setMaxlength(50);
		this.pmaryRelOfficer.setMaxlength(20);
		this.custAccount.setAcountDetails(
				AccountConstants.ACTYPES_COMMITCHARGE, "", true); // need ask
																	// which
																	// account
																	// type
																	// should be
																	// added
		this.custAccount.setFormatter(ccyformatt);
		this.custAccount.setMandatoryStyle(true);
		this.custAccount.setTextBoxWidth(165);

		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering ");
		getUserWorkspace().allocateAuthorities(super.pageRightName);
		this.btnEdit.setVisible(getUserWorkspace().isAllowed(
				"button_MMAgreementsDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed(
				"button_MMAgreementsDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed(
				"button_MMAgreementsDialog_btnSave"));
		this.print.setVisible(getUserWorkspace().isAllowed(
				"button_MMAgreementsDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
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
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
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
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering ");
		doWriteBeanToComponents(this.aMMAgreement.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		this.btnEdit.setVisible(true);
		this.btnDelete.setVisible(true);
		logger.debug("Leaving ");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCurrency
	 * @throws Exception
	 */
	public void doShowDialog(MMAgreement aMMAgreement) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aMMAgreement.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custCIF.focus();
		} else {
			this.custCIF.focus();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aMMAgreement.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aMMAgreement);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_MMAgreementDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving ");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aMMAgreements
	 * 
	 */
	public void doWriteBeanToComponents(MMAgreement aMMAgreement) {
		logger.debug("Entering ");
		fillComboBox(this.product, aMMAgreement.getProduct(),
				PennantStaticListUtil.getProductForMMA(), "");
		fillComboBox(
				this.agreeName,
				aMMAgreement.getAgreeName(),
				PennantAppUtil.getFieldCodeList(product.getSelectedItem()
						.getValue().toString()), "");
		this.mMAReference.setValue(aMMAgreement.getMMAReference());
		this.custCIF.setValue(aMMAgreement.getCustCIF());
		this.custCIF.setDescription(aMMAgreement.getCustShrtName());
		this.purchaddress.setValue(aMMAgreement.getPurchaddress());
		this.purchRegOffice.setValue(aMMAgreement.getPurchRegOffice());
		this.purchRegOffice.setDescription(aMMAgreement
				.getLovDescPurchRegOffice());
		this.contractAmt.setValue(PennantAppUtil.formateAmount(
				aMMAgreement.getContractAmt(), ccyformatt));
		this.contractDate.setValue(aMMAgreement.getContractDate());
		this.rate.setValue(aMMAgreement.getRate() == null ? BigDecimal.ZERO
				: aMMAgreement.getRate());
		String[] fax = PennantApplicationUtil.unFormatPhoneNumber(aMMAgreement
				.getFax());
		this.faxCountryCode.setValue(fax[0]);
		this.faxAreaCode.setValue(fax[1]);
		this.fax.setValue(fax[2]);
		this.titleNo.setValue(aMMAgreement.getTitleNo());
		this.country.setValue(aMMAgreement.getAttention());

		// Facility Details
		this.fOLIssueDate.setValue(aMMAgreement.getfOLIssueDate());
		this.maturityDate.setValue(aMMAgreement.getMaturityDate());
		this.facilityLimit.setValue(PennantAppUtil.formateAmount(
				aMMAgreement.getFacilityLimit(), ccyformatt));
		this.minAmount.setValue(PennantAppUtil.formateAmount(
				aMMAgreement.getMinAmount(), ccyformatt));
		this.profitRate.setValue(aMMAgreement.getProfitRate());
		this.rateCode.setMarginValue(aMMAgreement.getMargin() == null ? BigDecimal.ZERO
				: aMMAgreement.getMargin());
		this.minRate
				.setValue(aMMAgreement.getMinRate() == null ? BigDecimal.ZERO
						: aMMAgreement.getMinRate());
		this.latePayRate
				.setValue(aMMAgreement.getLatePayRate() == null ? BigDecimal.ZERO
						: aMMAgreement.getLatePayRate());
		this.numberOfTerms.setValue(aMMAgreement.getNumberOfTerms());
		this.profitPeriod.setValue(aMMAgreement.getProfitPeriod());

		this.rateCode.setBaseValue(aMMAgreement.getBaseRateCode());
		this.rateCode.setBaseDescription(aMMAgreement.getLovDescBaseRateName());
		this.folReference.setValue(aMMAgreement.getfOlReference());
		this.avlPerDays.setValue(aMMAgreement.getAvlPerDays());
		this.maxCapProfitRate
				.setValue(aMMAgreement.getMaxCapProfitRate() == null ? BigDecimal.ZERO
						: aMMAgreement.getMaxCapProfitRate());
		this.facOfferLetterDate.setValue(aMMAgreement.getFacOfferLetterDate());
		this.pmaryRelOfficer.setValue(aMMAgreement.getPmaryRelOfficer());
		this.custAccount.setValue(aMMAgreement.getCustAccount());
		this.minCapRate
				.setValue(aMMAgreement.getMinCapRate() == null ? BigDecimal.ZERO
						: aMMAgreement.getMinCapRate());
		this.dealer.setValue(String.valueOf(aMMAgreement.getDealer()));
		this.dealer.setDescription(StringUtils.trimToEmpty(aMMAgreement
				.getDealerName()));
		this.assetValue.setValue(aMMAgreement.getAssetValue());
		this.sharePerc.setValue(aMMAgreement.getSharePerc());
		this.assetDesc.setValue(aMMAgreement.getAssetDesc());

		doSetFilltersFOLRef();
		logger.debug("Leaving ");
	}

	public void onSelect$product(Event event) {
		logger.debug("Entering" + event.toString());
		ArrayList<ValueLabel> list1 = PennantAppUtil.getFieldCodeList(product
				.getSelectedItem().getValue().toString());
		this.agreeName.setDisabled(false);
		if (list1.size() == 1) {
			fillComboBox(this.agreeName, list1.get(0).getValue(), list1, "");
			this.agreeName.setDisabled(true);
		} else {
			fillComboBox(this.agreeName, "", list1, "");
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering ");

		setValidationOn(false);
		this.mMAReference.setConstraint("");
		this.custCIF.setConstraint("");
		this.contractAmt.setConstraint("");
		this.contractDate.setConstraint("");
		this.purchRegOffice.setConstraint("");
		this.titleNo.setConstraint("");
		this.fax.setConstraint("");
		this.faxCountryCode.setConstraint("");
		this.faxAreaCode.setConstraint("");
		this.purchaddress.setConstraint("");
		this.rate.setConstraint("");
		this.agreeName.setConstraint("");
		this.product.setConstraint("");
		this.mMAgreeType.setConstraint("");
		this.fOLIssueDate.setConstraint("");
		this.maturityDate.setConstraint("");
		this.facilityLimit.setConstraint("");
		this.minAmount.setConstraint("");
		this.profitRate.setConstraint("");
		this.rateCode.setMarginConstraint("");
		this.minRate.setConstraint("");
		this.latePayRate.setConstraint("");
		this.numberOfTerms.setConstraint("");
		this.profitPeriod.setConstraint("");

		this.pmaryRelOfficer.setConstraint("");
		this.custAccount.setConstraint("");
		this.maxCapProfitRate.setConstraint("");
		this.folReference.setConstraint("");
		this.attention.setConstraint("");
		this.maxCapProfitRate.setConstraint("");
		logger.debug("Leaving ");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Enterring");

		this.custCIF.setErrorMessage("");
		this.mMAReference.setErrorMessage("");
		this.contractAmt.setErrorMessage("");
		this.contractDate.setErrorMessage("");
		this.purchRegOffice.setErrorMessage("");
		this.titleNo.setErrorMessage("");
		this.fax.setErrorMessage("");
		this.faxCountryCode.setErrorMessage("");
		this.faxAreaCode.setErrorMessage("");
		this.purchaddress.setErrorMessage("");
		this.rate.setErrorMessage("");
		this.attention.setErrorMessage("");
		this.agreeName.setErrorMessage("");
		this.product.setErrorMessage("");
		this.mMAgreeType.setErrorMessage("");

		this.fOLIssueDate.setErrorMessage("");
		this.maturityDate.setErrorMessage("");
		this.facilityLimit.setErrorMessage("");
		this.minAmount.setErrorMessage("");
		this.profitRate.setErrorMessage("");
		this.rateCode.setMarginErrorMessage("");
		this.minRate.setErrorMessage("");
		this.latePayRate.setErrorMessage("");
		this.numberOfTerms.setErrorMessage("");
		this.profitPeriod.setErrorMessage("");
		this.folReference.setErrorMessage("");
		this.maxCapProfitRate.setErrorMessage("");
		this.minCapRate.setErrorMessage("");
		this.custAccount.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getaMMAgreementsListCtrl().search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.aMMAgreement.getCustCIF());
	}

	// CRUD operations

	/**
	 * Deletes a Currency object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering ");
		final MMAgreement aMMAgreement = new MMAgreement();
		BeanUtils.copyProperties(getMMAgreement(), aMMAgreement);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels
				.getLabel("message.Question.Are_you_sure_to_delete_this_record")
				+ "\n\n --> "
				+ Labels.getLabel("label_MMAgreementDialog_MMAReference.value")
				+ " : " + aMMAgreement.getMMAReference();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aMMAgreement.getRecordType())) {
				aMMAgreement.setVersion(aMMAgreement.getVersion() + 1);
				aMMAgreement.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aMMAgreement.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aMMAgreement, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}

		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering ");

		if (getMMAgreement().isNewRecord()) {
			this.mMAReference.setReadonly(false);
			this.btnCancel.setVisible(false);
			this.custCIF.setReadonly(isReadOnly("MMAgreementsDialog_CustCIF"));
		} else {
			this.custCIF.setMandatoryStyle(true);
			this.purchRegOffice.setMandatoryStyle(true);
			this.folReference.setMandatoryStyle(true);
			this.mMAReference.setReadonly(true);
			this.btnCancel.setVisible(true);
			this.custCIF.setReadonly(true);

		}
		this.mMAgreeType
				.setDisabled(isReadOnly("MMAgreementsDialog_MMAgreeType"));
		this.product.setDisabled(isReadOnly("MMAgreementsDialog_Product"));
		this.agreeName.setDisabled(isReadOnly("MMAgreementsDialog_AgreeName"));
		this.contractAmt
				.setDisabled(isReadOnly("MMAgreementsDialog_ContractAmt"));
		this.contractDate
				.setDisabled(isReadOnly("MMAgreementsDialog_ContractDate"));
		this.titleNo.setReadonly(isReadOnly("MMAgreementsDialog_TitleNo"));
		this.rate.setDisabled(isReadOnly("MMAgreementsDialog_Rate"));
		this.purchRegOffice
				.setReadonly(isReadOnly("MMAgreementsDialog_PurchRegOffice"));
		this.fax.setReadonly(isReadOnly("MMAgreementsDialog_Fax"));
		this.faxCountryCode.setReadonly(isReadOnly("MMAgreementsDialog_Fax"));
		this.faxAreaCode.setReadonly(isReadOnly("MMAgreementsDialog_Fax"));
		this.purchaddress
				.setReadonly(isReadOnly("MMAgreementsDialog_Purchaddress"));
		this.attention.setReadonly(isReadOnly("MMAgreementsDialog_Attention"));

		this.fOLIssueDate
				.setDisabled(isReadOnly("MMAgreementsDialog_FOLIssueDate"));
		this.maturityDate
				.setDisabled(isReadOnly("MMAgreementsDialog_maturityDate"));
		this.facilityLimit
				.setDisabled(isReadOnly("MMAgreementsDialog_facilityLimit"));
		this.profitRate
				.setDisabled(isReadOnly("MMAgreementsDialog_profitRate"));
		this.minRate.setDisabled(isReadOnly("MMAgreementsDialog_minRate"));
		this.minAmount.setDisabled(isReadOnly("MMAgreementsDialog_minAmount"));
		this.numberOfTerms
				.setReadonly(isReadOnly("MMAgreementsDialog_numberOfTerms"));
		this.profitPeriod
				.setReadonly(isReadOnly("MMAgreementsDialog_profitPeriod"));
		this.country.setReadonly(isReadOnly("MMAgreementsDialog_Country"));
		this.rateCode.setBaseReadonly(isReadOnly("MMAgreementsDialog_BaseRateCode"));
		this.folReference
				.setReadonly(isReadOnly("MMAgreementsDialog_folReference"));
		this.avlPerDays
				.setReadonly(isReadOnly("MMAgreementsDialog_AvlPerDays"));
		this.maxCapProfitRate
				.setDisabled(isReadOnly("MMAgreementsDialog_MaxCapProfitRate"));
		this.facOfferLetterDate
				.setDisabled(isReadOnly("MMAgreementsDialog_FacOfferLetterDate"));
		this.pmaryRelOfficer
				.setReadonly(isReadOnly("MMAgreementsDialog_PmaryRelOfficer"));
		this.custAccount
				.setReadonly(isReadOnly("MMAgreementsDialog_CustAccount"));
		this.minCapRate
				.setDisabled(isReadOnly("MMAgreementsDialog_MinCapRate"));
		this.rateCode.setMarginReadonly(isReadOnly("MMAgreementsDialog_margin"));
		this.latePayRate
				.setDisabled(isReadOnly("MMAgreementsDialog_latePayRate"));

		this.dealer.setReadonly(isReadOnly("MMAgreementsDialog_Dealer"));
		this.assetValue
				.setDisabled(isReadOnly("MMAgreementsDialog_assetValue"));
		this.assetDesc.setReadonly(isReadOnly("MMAgreementsDialog_assetDesc"));
		this.custPOBox.setDisabled(isReadOnly("MMAgreementsDialog_custPOBox"));
		this.sharePerc.setDisabled(isReadOnly("MMAgreementsDialog_sharePerc"));
		this.city.setReadonly(isReadOnly("MMAgreementsDialog_CustCity"));
		this.province
				.setReadonly(isReadOnly("MMAgreementsDialog_CustProvince"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.aMMAgreement.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			this.btnSave.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering ");

		this.mMAReference.setReadonly(true);
		this.custCIF.setReadonly(true);
		this.mMAgreeType.setDisabled(true);
		this.product.setDisabled(true);
		this.agreeName.setDisabled(true);
		this.contractAmt.setDisabled(true);
		this.contractDate.setDisabled(true);
		this.titleNo.setReadonly(true);
		this.rate.setDisabled(true);
		this.purchRegOffice.setReadonly(true);
		this.fax.setReadonly(true);
		this.faxCountryCode.setReadonly(true);
		this.faxAreaCode.setReadonly(true);
		this.purchaddress.setReadonly(true);
		this.attention.setReadonly(true);

		this.fOLIssueDate.setDisabled(true);
		this.maturityDate.setDisabled(true);
		this.facilityLimit.setDisabled(true);
		this.profitRate.setDisabled(true);
		this.minRate.setDisabled(true);
		this.minRate.setDisabled(true);
		this.minAmount.setDisabled(true);
		this.numberOfTerms.setReadonly(true);
		this.profitPeriod.setReadonly(true);

		this.rateCode.setBaseReadonly(true);
		this.folReference.setReadonly(true);
		this.avlPerDays.setReadonly(true);
		this.maxCapProfitRate.setDisabled(true);
		this.facOfferLetterDate.setDisabled(true);
		this.pmaryRelOfficer.setReadonly(true);
		this.custAccount.setReadonly(true);
		this.minCapRate.setDisabled(true);
		this.country.setReadonly(true);
		this.dealer.setReadonly(true);
		this.assetValue.setReadonly(true);
		this.assetDesc.setReadonly(true);
		this.custPOBox.setDisabled(true);
		this.sharePerc.setDisabled(true);
		this.city.setReadonly(true);
		this.province.setReadonly(true);
		this.rateCode.setMarginReadonly(true);
		this.latePayRate.setDisabled(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering ");
		final MMAgreement aMMAgreement = new MMAgreement();
		BeanUtils.copyProperties(getMMAgreement(), aMMAgreement);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		// doClearMessage();
		doSetValidation();
		// fill the Currency object with the components data
		doWriteComponentsToBean(aMMAgreement);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aMMAgreement.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aMMAgreement.getRecordType())) {
				aMMAgreement.setVersion(aMMAgreement.getVersion() + 1);
				if (isNew) {
					aMMAgreement
							.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aMMAgreement
							.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aMMAgreement.setNewRecord(true);
				}
			}
		} else {
			aMMAgreement.setVersion(aMMAgreement.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aMMAgreement, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCurrency
	 *            (Currency)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(MMAgreement aMMAgreement, String tranType) {
		logger.debug("Entering ");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aMMAgreement.setLastMntBy(getUserWorkspace().getLoggedInUser()
				.getLoginUsrID());
		aMMAgreement.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aMMAgreement.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aMMAgreement.setRecordStatus(userAction.getSelectedItem()
					.getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aMMAgreement
						.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aMMAgreement);
				}

				if (isNotesMandatory(taskId, aMMAgreement)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isNotBlank(nextTaskId)) {
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

			aMMAgreement.setTaskId(taskId);
			aMMAgreement.setNextTaskId(nextTaskId);
			aMMAgreement.setRoleCode(getRole());
			aMMAgreement.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aMMAgreement, tranType);

			String operationRefs = getServiceOperations(taskId, aMMAgreement);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aMMAgreement,
							PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
				}
			}
		} else {
			auditHeader = getAuditHeader(aMMAgreement, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving ");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		MMAgreement aMMAgreement = (MMAgreement) auditHeader.getAuditDetail()
				.getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getmMAgreementService().delete(
								auditHeader);

						deleteNotes = true;
					} else {
						auditHeader = getmMAgreementService().saveOrUpdate(
								auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getmMAgreementService().doApprove(
								auditHeader);

						if (aMMAgreement.getRecordType().equals(
								PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getmMAgreementService().doReject(
								auditHeader);
						if (aMMAgreement.getRecordType().equals(
								PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels
										.getLabel("InvalidWorkFlowMethod"),
								null));
						retValue = ErrorControl.showErrorControl(
								this.window_MMAgreementDialog, auditHeader);
						logger.debug("Leaving");
						return processCompleted;
					}
				}

				retValue = ErrorControl.showErrorControl(
						this.window_MMAgreementDialog, auditHeader);

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.aMMAgreement), true);
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
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving ");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aMMAgreement
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(MMAgreement aMMAgreement, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aMMAgreement.getBefImage(), aMMAgreement);
		return new AuditHeader(String.valueOf(aMMAgreement.getId()), null,
				null, null, auditDetail, aMMAgreement.getUserDetails(),
				getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_MMAgreementDialog,
					auditHeader);
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
		doShowNotes(this.aMMAgreement);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		Date appDate = DateUtility.getAppDate();
		Date appEndDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");

		if (!this.mMAReference.isReadonly()) {
			this.mMAReference.setConstraint(new PTStringValidator(Labels
					.getLabel("label_MMAgreementDialog_MMAReference.value"),
					null, true));
		}
		if (!this.custCIF.isReadonly()) {
			this.custCIF.setConstraint(new PTStringValidator(Labels
					.getLabel("label_MMAgreementDialog_CustCIF.value"), null,
					true, true));
		}
		if (!this.rate.isReadonly()) {
			this.rate.setConstraint(new PTDecimalValidator(Labels
					.getLabel("label_MMAgreementDialog_Rate.value"), 9, false,
					false, 9999));
		}
		if (!this.purchRegOffice.isReadonly()) {
			this.purchRegOffice.setConstraint(new PTStringValidator(Labels
					.getLabel("label_MMAgreementDialog_PurchRegOffice.value"),
					null, true, true));
		}
		if (!this.purchaddress.isReadonly()) {
			this.purchaddress.setConstraint(new PTStringValidator(Labels
					.getLabel("label_MMAgreementDialog_Purchaddress.value"),
					PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.contractDate.isReadonly()) {
			this.contractDate.setConstraint(new PTDateValidator(Labels
					.getLabel("label_MMAgreementDialog_Date.value"), true,
					appDate, appEndDate, false));
		}
		if (!this.titleNo.isReadonly()) {
			this.titleNo.setConstraint(new PTStringValidator(Labels
					.getLabel("label_MMAgreementDialog_TitleNo.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, false));
		}
		if (!this.attention.isReadonly()) {
			this.attention.setConstraint(new PTStringValidator(Labels
					.getLabel("label_MMAgreementDialog_Attention.value"),
					PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.faxCountryCode.isReadonly()) {
			this.faxCountryCode.setConstraint(new PTPhoneNumberValidator(Labels
					.getLabel("label_MMAgreementDialog_FaxCountryCode.value"),
					false, 1));
		}
		if (!this.faxAreaCode.isReadonly()) {
			this.faxAreaCode.setConstraint(new PTPhoneNumberValidator(Labels
					.getLabel("label_MMAgreementDialog_FaxAreaCode.value"),
					false, 2));
		}
		if (!this.fax.isReadonly()) {
			this.fax.setConstraint(new PTPhoneNumberValidator(Labels
					.getLabel("label_MMAgreementDialog_fax.value"), false, 3));
		}
		if (!this.rateCode.isMarginReadonly()) {
			this.rateCode.setMarginConstraint(new PTDecimalValidator(Labels
					.getLabel("label_MMAgreementDialog_Margin.value"), 9,
					false, false, 9999));
		}

		if (!this.minRate.isDisabled()) {
			this.minRate.setConstraint(new PTDecimalValidator(Labels
					.getLabel("label_MMAgreementDialog_minRate.value"), 9,
					false, false, 9999));
		}

		if (!this.profitRate.isDisabled()) {
			this.profitRate.setConstraint(new PTDecimalValidator(Labels
					.getLabel("label_MMAgreementDialog_ProfitRate.value"), 9,
					false, false, 9999));
		}
		if (!this.latePayRate.isDisabled()) {
			this.latePayRate.setConstraint(new PTDecimalValidator(Labels
					.getLabel("label_MMAgreementDialog_LatePayRate.value"), 9,
					false, false, 9999));
		}
		if (!this.maxCapProfitRate.isDisabled()) {
			this.maxCapProfitRate
					.setConstraint(new PTDecimalValidator(
							Labels.getLabel("label_MMAgreementDialog_maxCapProfitRate.value"),
							9, false, false, 9999));
		}

		if (!this.folReference.isReadonly()) {
			this.folReference.setConstraint(new PTStringValidator(Labels
					.getLabel("label_MMAgreementDialog_FOL_Reference.value"),
					null, true, true));
		}

		if (this.custAccount.isMandatory()) {
			this.custAccount.setConstraint(new PTStringValidator(Labels
					.getLabel("label_MMAgreementDialog_CustAccount.value"),
					null, true));
		}

		if (!this.pmaryRelOfficer.isReadonly()) {
			this.pmaryRelOfficer
					.setConstraint(new PTStringValidator(
							Labels.getLabel("label_MMAgreementDialog_PrimaryRelationshipOfficer.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_SPACE,
							false));
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aMMAgreements
	 */
	public void doWriteComponentsToBean(MMAgreement aMMAgreement) {
		logger.debug("Entering ");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (this.product.getSelectedItem() != null
					&& !"#".equals(this.product.getSelectedItem().getValue())) {
				aMMAgreement.setProduct(this.product.getSelectedItem()
						.getValue().toString());
			} else {
				throw new WrongValueException(
						this.product,
						Labels.getLabel(
								"STATIC_INVALID",
								new String[] { Labels
										.getLabel("label_MMAgreementDialog_Product.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.agreeName.getSelectedItem() != null
					&& !"#".equals(this.agreeName.getSelectedItem().getValue())) {
				aMMAgreement.setAgreeName(this.agreeName.getSelectedItem()
						.getValue().toString());
			} else {
				throw new WrongValueException(
						this.agreeName,
						Labels.getLabel(
								"STATIC_INVALID",
								new String[] { Labels
										.getLabel("label_MMAgreementDialog_AgreeName.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMMAgreement.setMMAReference(this.mMAReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMMAgreement.setCustCIF(this.custCIF.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aMMAgreement.setRate(this.rate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aMMAgreement.setContractDate(this.contractDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMMAgreement.setPurchRegOffice(this.purchRegOffice.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMMAgreement.setContractAmt(PennantAppUtil.unFormateAmount(
					this.contractAmt.getValidateValue(), ccyformatt));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMMAgreement.setPurchaddress(this.purchaddress.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMMAgreement.setAttention(this.attention.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMMAgreement.setDealer(Long.parseLong(this.dealer.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMMAgreement.setAssetValue(this.assetValue.getActualValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMMAgreement.setAssetDesc(this.assetDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMMAgreement.setSharePerc(this.sharePerc.getActualValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMMAgreement.setFax(PennantApplicationUtil.formatPhoneNumber(
					this.faxCountryCode.getValue(),
					this.faxAreaCode.getValue(), this.fax.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMMAgreement.setTitleNo(this.titleNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.fOLIssueDate.getValue() != null
					&& this.maturityDate.getValue() != null) {
				if (!this.fOLIssueDate.getValue().before(
						this.maturityDate.getValue())) {
					throw new WrongValueException(
							this.fOLIssueDate,
							Labels.getLabel(
									"DATE_ALLOWED_MAXDATE",
									new String[] {
											Labels.getLabel("label_MMAgreementDialog_fOLIssueDate.value"),
											Labels.getLabel("label_MMAgreementDialog_maturityDate.value") }));
				}
			}
			aMMAgreement.setfOLIssueDate(this.fOLIssueDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aMMAgreement.setMaturityDate(this.maturityDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMMAgreement.setFacilityLimit(PennantAppUtil.unFormateAmount(
					this.facilityLimit.getValidateValue(), ccyformatt));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMMAgreement.setMinAmount(PennantAppUtil.unFormateAmount(
					this.minAmount.getValidateValue(), ccyformatt));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMMAgreement.setBaseRateCode(this.rateCode.getBaseValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMMAgreement.setProfitRate(this.profitRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aMMAgreement.setMargin(this.rateCode.getMarginValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aMMAgreement.setMinRate(this.minRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMMAgreement.setLatePayRate(this.latePayRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMMAgreement.setNumberOfTerms(this.numberOfTerms.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMMAgreement.setProfitPeriod(this.profitPeriod.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMMAgreement.setfOlReference(this.folReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMMAgreement.setAvlPerDays(this.avlPerDays.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMMAgreement.setMaxCapProfitRate(this.maxCapProfitRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMMAgreement.setMinCapRate(this.minCapRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMMAgreement.setFacOfferLetterDate(this.facOfferLetterDate
					.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aMMAgreement.setPmaryRelOfficer(this.pmaryRelOfficer.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aMMAgreement.setCustAccount(PennantApplicationUtil
					.unFormatAccountNumber(this.custAccount.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aMMAgreement.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving ");

	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_MMAgreementDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws NotFoundException
	 * @throws LimitProcessException
	 */
	public void onClick$btnPrint(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		printAgreement(event);
		logger.debug("Leaving" + event.toString());
	}

	private void printAgreement(Event event) throws Exception {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		doSetValidation();
		AgreementDetail aAgreementDetail = new AgreementDetail();
		Date appDate = DateUtility.getAppDate();

		try {
			aAgreementDetail.setmMADate(DateUtility
					.formatToLongDate(this.contractDate.getValue()));
			aAgreementDetail.setStartDate(DateUtility
					.formatToLongDate(this.contractDate.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aAgreementDetail.setCustCIF(this.custCIF.getValue());
			aAgreementDetail.setCustName(this.custCIF.getDescription());
			aAgreementDetail.setCustArabicName(getMMAgreement()
					.getCustShrtNameLclLng());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aAgreementDetail.setCustName(this.custCIF.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// WHY TWO CONTROLLERS DUPLICATED WITH SAME FIELDS-- NEED TO CHECK THIS
		// =====================================
		try {
			aAgreementDetail.setmMAPurchRegOffice(this.purchRegOffice
					.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDetail.setmMAContractAmt(PennantApplicationUtil
					.formatAmount(this.contractAmt.getValidateValue(),
							ccyformatt, false));
			BigDecimal finAmt = this.contractAmt.getValidateValue();
			BigDecimal contribution = BigDecimal.valueOf(30);
			BigDecimal aHBBankShare = (contribution.multiply(finAmt))
					.divide(new BigDecimal(100));
			aAgreementDetail.setSharePerc(String.valueOf(aHBBankShare));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDetail.setmMAPurchaddress(this.purchaddress.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aAgreementDetail.setAttention(this.attention.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDetail.setmMAFax(PennantApplicationUtil
					.formatPhoneNumber(this.faxCountryCode.getValue(),
							this.faxAreaCode.getValue(), this.fax.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDetail.setBaseRateCode(this.rateCode.getBaseValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDetail.setmMARate(String.valueOf(this.rate.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDetail.setmMAFOLIssueDate(DateUtility
					.formatToLongDate(this.fOLIssueDate.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDetail.setmMAMaturityDate(DateUtility
					.formatToLongDate(this.maturityDate.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDetail.setmMAFacilityLimit(PennantApplicationUtil
					.formatAmount(this.facilityLimit.getValidateValue(),
							ccyformatt, false));
			String word = NumberToEnglishWords.getAmountInText(
					this.facilityLimit.getValidateValue(), "");
			aAgreementDetail.setFacLimitInWords(word);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// aAgreementDetail.setFacLimitInWords(NumberToEnglishWords.getAmountInText(this.facilityLimit.getValidateValue(),aAgreementDetail.getFinCcy()));
		try {
			aAgreementDetail.setmMAMinAmount(PennantApplicationUtil
					.formatAmount(this.minAmount.getValidateValue(),
							ccyformatt, false));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.profitRate.getValue() != null) {
				aAgreementDetail
						.setmMAPftRate(PennantApplicationUtil.formatRate(
								this.profitRate.getValue().doubleValue(), 9));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.rateCode.getMarginValue() != null) {
				aAgreementDetail.setmMAMargin(PennantApplicationUtil
						.formatRate(this.rateCode.getMarginValue().doubleValue(), 9));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.minRate.getValue() != null) {
				aAgreementDetail.setmMAMinRate(PennantApplicationUtil
						.formatRate(this.minRate.getValue().doubleValue(), 9));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.latePayRate.getValue() != null) {
				aAgreementDetail.setmMALatePayRate(PennantApplicationUtil
						.formatRate(this.latePayRate.getValue().doubleValue(),
								9));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aAgreementDetail.setmMANumberOfTerms(String
					.valueOf(this.numberOfTerms.intValue()));
			String leasePeriodInWords = NumberToEnglishWords
					.getNumberToWords(BigInteger.valueOf(this.numberOfTerms
							.intValue()));
			aAgreementDetail.setLeaseTermsWords(leasePeriodInWords);

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDetail.setmMAProfitPeriod(String
					.valueOf(this.profitPeriod.intValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDetail.setFolReference(String.valueOf(this.folReference
					.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDetail.setAvlPerDays(String.valueOf(this.avlPerDays
					.intValue()));
			String word = NumberToEnglishWords.getNumberToWords(BigInteger
					.valueOf(this.avlPerDays.getValue()));
			aAgreementDetail.setAvlPeriodInWords(word);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.maxCapProfitRate.getValue() != null) {
				aAgreementDetail.setMaxCapProfitRate(PennantApplicationUtil
						.formatRate(this.maxCapProfitRate.getValue()
								.doubleValue(), 9));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.minCapRate.getValue() != null) {
				aAgreementDetail
						.setMinCapRate(PennantApplicationUtil.formatRate(
								this.minCapRate.getValue().doubleValue(), 9));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDetail.setFacOfferLetterDate(DateUtility
					.formatToLongDate(this.facOfferLetterDate.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDetail.setPmaryRelOfficer(String
					.valueOf(this.pmaryRelOfficer.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.custAccount.getValidatedValue();
			aAgreementDetail.setCustAccount(PennantApplicationUtil
					.unFormatAccountNumber(this.custAccount.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDetail.setDealerName(this.dealer.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDetail.setDealerCity(StringUtils
					.trimToEmpty(getMMAgreement().getDealerCity()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDetail.setAssetValue(PennantApplicationUtil.formatAmount(
					this.assetValue.getActualValue(), ccyformatt, false));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDetail.setAssetDesc(this.assetDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDetail.setSharePerc(PennantApplicationUtil.formatAmount(
					this.sharePerc.getActualValue(), ccyformatt, false));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aAgreementDetail.setCustPOBox(getMMAgreement().getCustPOBox());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDetail.setCustAddrCountry(getMMAgreement()
					.getLovDescCustAddrCountryName());
			aAgreementDetail.setCustAddrProvince(getMMAgreement()
					.getLovDescCustAddrProvinceName());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDetail.setCustAddrCity(getMMAgreement()
					.getLovDescCustAddrCityName());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDetail.setDealerCountry(getMMAgreement()
					.getDealerCountry());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aAgreementDetail.setAppDate(String.valueOf(appDate));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (this.fOLIssueDate.getValue() != null) {
			aAgreementDetail.setMM(String.valueOf(DateUtility
					.getMonth(this.fOLIssueDate.getValue())));
			aAgreementDetail.setDD(String.valueOf(DateUtility
					.getDay(this.fOLIssueDate.getValue())));
			String year = String.valueOf(DateUtility.getYear(this.fOLIssueDate
					.getValue()));
			aAgreementDetail.setYY(year.substring(2, 4));
		}

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		String agreementType = this.agreeName.getSelectedItem().getLabel()
				+ ".docx";

		doAgreementGeneration(aAgreementDetail, agreementType);

		logger.debug("Leaving");
	}

	public void doAgreementGeneration(AgreementDetail aAgreementDetail,
			String agreementType) throws IllegalArgumentException,
			InterruptedException {
		try {
			String templatePath = PathUtil.getPath(PathUtil.MMA_AGREEMENTS);
			TemplateEngine engine = new TemplateEngine(templatePath,
					templatePath);
			String refNo = aAgreementDetail.getCustCIF();
			String reportName = refNo + "_" + agreementType;
			engine.setTemplate(agreementType);
			engine.loadTemplate();
			engine.mergeFields(aAgreementDetail);
			engine.showDocument(this.window_MMAgreementDialog, reportName,
					SaveFormat.DOCX);
			engine = null;
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showError(Labels.getLabel("Label_Template"));
		}
	}

	public void onFulfill$dealer(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = dealer.getObject();
		if (dataObject instanceof String) {
			getMMAgreement().setDealerAddr("");
			getMMAgreement().setDealerCountry("");
			getMMAgreement().setDealerCity("");

		} else {
			VehicleDealer details = (VehicleDealer) dataObject;
			if (details != null) {
				getMMAgreement().setDealerAddr(
						StringUtils.trimToEmpty(details.getDealerAddress1())
								+ " "
								+ StringUtils.trimToEmpty(details
										.getDealerAddress2()));
				getMMAgreement().setDealerCountry(
						StringUtils.trimToEmpty(details.getDealerCountry()));
				getMMAgreement().setDealerCity(
						StringUtils.trimToEmpty(details.getDealerCity()));
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$custCIF(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = custCIF.getObject();
		this.folReference.setValue("");
		if (dataObject instanceof String) {
			getMMAgreement().setDealerAddr("");
			getMMAgreement().setDealerCountry("");
			getMMAgreement().setLovDescCustAddrCountryName("");
			getMMAgreement().setLovDescCustAddrProvinceName("");
			getMMAgreement().setLovDescCustAddrCityName("");
		} else {
			Customer details = (Customer) dataObject;
			if (details != null) {
				CustomerAddres aCustomerAddres = getmMAgreementService()
						.getCustomerAddressDetailsByIdCustID(
								details.getCustID());
				if (aCustomerAddres != null) {
					getMMAgreement().setCustCountry(
							aCustomerAddres.getCustAddrCountry());
					getMMAgreement().setCustPOBox(
							aCustomerAddres.getCustPOBox());
					getMMAgreement().setCustCity(
							aCustomerAddres.getCustAddrCity());
					getMMAgreement().setLovDescCustAddrCountryName(
							aCustomerAddres.getLovDescCustAddrCountryName());
					getMMAgreement().setLovDescCustAddrProvinceName(
							aCustomerAddres.getLovDescCustAddrProvinceName());
					getMMAgreement().setLovDescCustAddrCityName(
							aCustomerAddres.getLovDescCustAddrCityName());
				}
			}
		}
		doSetFilltersFOLRef();
		logger.debug("Leaving" + event.toString());
	}

	private void doSetFilltersFOLRef() {
		Filter filter1[] = new Filter[1];
		filter1[0] = new Filter("CustomerReference", this.custCIF.getValue(),
				Filter.OP_EQUAL);
		this.folReference.setFilters(filter1);
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

	// GUI operations

	// Helpers
	public void doSetCustomer(Object nCustomer,
			JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug("Entering");
		this.custCIF.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;

		if (customer != null) {
			this.custCIF.setValue(customer.getCustCIF());

		} else {
			this.custCIF.setValue("");
		}
		logger.debug("Leaving ");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");

		// remove validation, if there are a save before
		this.custCIF.getValue();
		this.purchaddress.setValue("");
		this.purchRegOffice.setValue("");
		this.contractAmt.setValue("");
		this.contractDate.setValue(null);
		this.fax.setValue("");
		this.faxAreaCode.setValue("");
		this.faxCountryCode.setValue("");
		this.titleNo.setValue("");
		this.attention.setValue("");
		this.rateCode.setBaseValue("");
		this.folReference.setValue("");
		this.avlPerDays.setValue(0);
		this.maxCapProfitRate.setValue("");
		this.facOfferLetterDate.setValue(null);
		this.pmaryRelOfficer.setValue("");
		this.custAccount.setValue("");
		this.minCapRate.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public boolean isValidationOn() {
		return validationOn;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public MMAgreement getMMAgreement() {
		return aMMAgreement;
	}

	public void setMMAgreement(MMAgreement aMMAgreement) {
		this.aMMAgreement = aMMAgreement;
	}

	public MMAgreementsListCtrl getaMMAgreementsListCtrl() {
		return aMMAgreementsListCtrl;
	}

	public void setaMMAgreementsListCtrl(
			MMAgreementsListCtrl aMMAgreementsListCtrl) {
		this.aMMAgreementsListCtrl = aMMAgreementsListCtrl;
	}

	public MMAgreementService getmMAgreementService() {
		return mMAgreementService;
	}

	public void setmMAgreementService(MMAgreementService mMAgreementService) {
		this.mMAgreementService = mMAgreementService;
	}

}

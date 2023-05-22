package com.pennant.webui.reports;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zhtml.Filedownload;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.aspose.words.SaveFormat;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.agreement.CovenantAggrement;
import com.pennant.backend.model.agreement.InterestCertificate;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.systemmasters.InterestCertificateService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.document.generator.TemplateEngine;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;

public class InterestCertificateGenerationDialogCtrl extends GFCBaseCtrl<InterestCertificate> {
	private static final long serialVersionUID = 9031340167587772517L;
	private static final Logger logger = LogManager.getLogger(InterestCertificateGenerationDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_InterestCertificateGeneration; // autowired

	protected ExtendedCombobox finReference;
	protected Combobox financeYear;
	protected Label WindowTitle;
	protected ExtendedCombobox finType;
	protected Label label_SelectFinReferenceDialog_FinType;
	protected Label label_InterestCertificate_FinanceYear;
	protected Space Space_FinanceYear;

	// Declaration of Service(s) & DAO(s)
	private transient InterestCertificateService interestCertificateService;

	protected Tabbox tabbox;

	protected Window parentWindow;
	private InterestCertificateGenerationDialogCtrl interestCertificateGenerationDialogCtrl = null;

	private List<ValueLabel> financeYearList = null;

	boolean isCustomer360 = false;

	/**
	 * default constructor.<br>
	 */
	public InterestCertificateGenerationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AgreementGenerationDialog";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected Customer object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_InterestCertificateGeneration(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_InterestCertificateGeneration);
		if (event.getTarget().getParent() != null && event.getTarget().getParent().getParent() != null) {
			tabbox = (Tabbox) event.getTarget().getParent().getParent().getParent().getParent();
		}
		if (arguments.containsKey("window")) {
			parentWindow = (Window) arguments.get("window");

		}
		if (arguments.containsKey("module")) {
			if ("provisional".equalsIgnoreCase(getArgument("module"))) {
				this.WindowTitle.setValue(Labels.getLabel("label_Item_ProvisionalCertificate.value"));
			}
			if ("interest".equalsIgnoreCase(getArgument("module"))) {
				this.WindowTitle.setValue(Labels.getLabel("label_Item_InterestCertficate.value"));
			}
			if ("covenant".equalsIgnoreCase(getArgument("module"))) {
				this.WindowTitle.setValue(Labels.getLabel("menu_Item_CovenantStatusReport.value"));
			}
		}

		if (arguments.containsKey("finType")) {
			this.finType.setValue((String) arguments.get("finType"));
		}

		if (arguments.containsKey("financeReference")) {
			this.finReference.setValue((String) arguments.get("financeReference"));
		}

		// For customer360 Report should be displayed as modal
		if (arguments.containsKey("customer360")) {
			isCustomer360 = arguments.containsKey("customer360");
		}

		doSetFieldProperties();
		this.window_InterestCertificateGeneration.doModal();

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		if (!"covenant".equalsIgnoreCase(getArgument("module"))) {
			this.finType.setMaxlength(8);
			this.finType.setMandatoryStyle(true);
			this.finType.setModuleName("FinanceType");
			this.finType.setValueColumn("FinType");
			this.finType.setDescColumn("FinTypeDesc");
			this.finType.setValidateColumns(new String[] { "FinType" });
			String loanTypes = SysParamUtil.getValueAsString(SMTParameterConstants.PROVCERT_LOANTYPES);

			if (StringUtils.isNotEmpty(loanTypes)) {
				Filter[] loanTypeFilter = new Filter[1];
				loanTypeFilter[0] = new Filter("FinType", loanTypes.split(","), Filter.OP_IN);
				finType.setFilters(loanTypeFilter);
			}

			this.finReference.setMaxlength(20);
			this.finReference.setTextBoxWidth(120);
			this.finReference.setMandatoryStyle(true);
			this.finReference.setModuleName("FinanceMain");
			this.finReference.setValueColumn("FinReference");
			this.finReference.setValidateColumns(new String[] { "FinReference" });

			if (StringUtils.equalsAnyIgnoreCase("interest", getArgument("module"))) {
				StringBuilder whereClause = new StringBuilder();
				whereClause.append(
						"FinisActive = 1 OR ClosingStatus=" + "'" + FinanceConstants.CLOSE_STATUS_MATURED + "'");
				whereClause.append(" OR ClosingStatus=" + "'" + FinanceConstants.CLOSE_STATUS_WRITEOFF + "'");
				whereClause.append(" OR ClosingStatus=" + "'" + FinanceConstants.CLOSE_STATUS_EARLYSETTLE + "'");
				this.finReference.setWhereClause(whereClause.toString());
			} else {
				this.finReference.setWhereClause("FinisActive = 1");
			}

			financeYearList = new ArrayList<ValueLabel>();

			if (StringUtils.equalsAnyIgnoreCase("provisional", getArgument("module"))) {
				String year = String.valueOf(DateUtil.getYear(SysParamUtil.getAppDate()));
				int month = DateUtil.getMonth(SysParamUtil.getAppDate());
				int years = -1;
				// As of financeYear Start from April
				if (month >= 4) {
					years = 0;
				}

				financeYearList.add(new ValueLabel(String.valueOf(Integer.valueOf(year) + years),
						String.valueOf(Integer.valueOf(year) + years) + "-"
								+ String.valueOf(Integer.valueOf(year.substring(year.length() - 2)) + years + 1)));
			} else if (StringUtils.equalsAnyIgnoreCase("interest", getArgument("module"))) {
				this.financeYear.setDisabled(true);
			}
			fillComboBox(this.financeYear, "", financeYearList, "");
		}

		if ("covenant".equalsIgnoreCase(getArgument("module"))) {
			this.finType.setVisible(false);
			this.financeYear.setVisible(false);

			this.finReference.setMaxlength(20);
			this.finReference.setTextBoxWidth(160);
			this.finReference.setMandatoryStyle(true);
			this.finReference.setModuleName("FinanceMaintenance");
			this.finReference.setValueColumn("FinReference");
			this.finReference.setValidateColumns(new String[] { "FinReference" });
			this.finReference.setWhereClause("FinisActive = 1");

			this.label_SelectFinReferenceDialog_FinType.setVisible(false);
			this.label_InterestCertificate_FinanceYear.setVisible(false);

			this.Space_FinanceYear.setSclass("");
		}
		logger.debug("Leaving");
	}

	public void onFulfill$finType(Event event) {
		logger.debug("Entering" + event.toString());
		Clients.clearWrongValue(this.finType);
		this.finType.setConstraint("");
		this.finType.setErrorMessage("");
		Object dataObject = this.finType.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.finType.setValue("");
			this.finType.setObject("");
			this.finReference.setButtonDisabled(true);
			this.finReference.setObject("");
		} else {
			FinanceType details = (FinanceType) dataObject;
			if (details != null) {
				this.finType.setValue(details.getFinType());
				this.finReference.setButtonDisabled(false);
				this.finReference.setMandatoryStyle(true);
				Filter[] filters = new Filter[1];
				filters[0] = new Filter("FinType", details.getFinType(), Filter.OP_EQUAL);
				this.finReference.setFilters(filters);

			}
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$finReference(Event event) {
		logger.debug(Literal.ENTERING);
		Clients.clearWrongValue(this.finReference);
		this.finReference.setConstraint("");
		this.finReference.setErrorMessage("");
		Object dataObject = this.finReference.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.finReference.setValue("");
			this.finReference.setObject("");
			this.financeYear.setConstraint("");
			this.financeYear.setErrorMessage("");
			if (StringUtils.equalsAnyIgnoreCase("interest", getArgument("module"))) {
				this.financeYear.setDisabled(true);
				financeYearList = new ArrayList<ValueLabel>();
				fillComboBox(this.financeYear, "", financeYearList, "");
			}
		} else {
			FinanceMain details = (FinanceMain) dataObject;
			if (details != null) {
				this.finReference.setValue(details.getFinReference());
				if (StringUtils.equalsAnyIgnoreCase("interest", getArgument("module"))) {
					this.financeYear.setDisabled(false);
					doFillFinanceYear();
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void doFillFinanceYear() {
		Date appDate = SysParamUtil.getAppDate();
		FinanceMain fm = interestCertificateService.getFinanceMain(this.finReference.getValue(), TableType.MAIN_TAB);

		if (fm == null) {
			return;
		}

		Date finStartDate = fm.getFinStartDate();
		int finStartDateMonth = DateUtil.getMonth(finStartDate);
		String finStartDateYear = String.valueOf(DateUtil.getYear(finStartDate));

		int appDateMonth = DateUtil.getMonth(appDate);
		int appDateDay = DateUtil.getDay(appDate);
		int finStartDateDay = DateUtil.getDay(finStartDate);
		int years = DateUtil.getYearsBetween(finStartDate, appDate);
		String appDateYear = String.valueOf(DateUtil.getYear(appDate));

		if (finStartDateMonth < 4 && years > 0) {
			finStartDateYear = String.valueOf(Integer.valueOf(finStartDateYear) - 1);
			years = years + 1;
		}

		if (appDateMonth > finStartDateMonth) {
			years = years - 1;
		} else if ((appDateDay == finStartDateDay || appDateDay >= finStartDateDay) && years > 0) {
			years = years - 1;
		} else if (appDateMonth < 4) {
			years = years - 1;
		}

		financeYearList = new ArrayList<ValueLabel>();
		if (years < 0 && finStartDateMonth < 4) {
			// if finstartDate and appDate both are equal then we are not allowing to count years
			// if AppDate is greater than or equal to 4 adding years with 1
			if (DateUtil.compare(appDate, finStartDate) > 0 && appDateMonth >= 4) {
				years = years + 1;
			}
			for (int i = 0; i <= years; i++) {
				String newValue = String.valueOf(Integer.valueOf(finStartDateYear) + i - 1);
				String newLabel = String.valueOf(Integer.valueOf(finStartDateYear) - 1) + "-" + String
						.valueOf(Integer.valueOf(finStartDateYear.substring(finStartDateYear.length() - 2)) + i);
				financeYearList.add(new ValueLabel(newValue, newLabel));
			}
		} else if (!(finStartDateYear.equals(appDateYear) && !(finStartDateMonth < 4))) {
			// if FinstartDateYear & appDateYear both are equal & finstartDateMonth less than 4 we are not adding
			// into list...
			if (years >= 1 && DateUtil.getYearsBetween(finStartDate, appDate) >= 1 && appDateMonth < 4
					&& appDateDay != finStartDateDay) {
				// if years greater than or equal to 1 and appdate less than 4 and appdateday not equal to
				// finstartday than subtract years - 1
				// i.e@03-01-2020 to 15-01-2022 i.e@2019-20,2020-21
				years = years - 1;
			}
			for (int i = 0; i <= years; i++) {
				financeYearList.add(new ValueLabel(String.valueOf(Integer.valueOf(finStartDateYear) + i),
						String.valueOf(Integer.valueOf(finStartDateYear) + i) + "-" + String.valueOf(
								Integer.valueOf(finStartDateYear.substring(finStartDateYear.length() - 2)) + i + 1)));
			}
		}
		fillComboBox(this.financeYear, "", financeYearList, "");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		if (!this.finReference.isReadonly()) {
			this.finReference.setConstraint(new PTStringValidator(
					Labels.getLabel("label_SelectFinReferenceDialog_FinReference.value"), null, true, true));
		}
		if (!"covenant".equalsIgnoreCase(getArgument("module"))) {
			if (!this.financeYear.isDisabled() && this.financeYear.isVisible()) {
				this.financeYear.setConstraint(new StaticListValidator(financeYearList,
						Labels.getLabel("label_InterestCertificate_FinanceYear.value")));
			}
		}
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		MessageUtil.showHelpWindow(event, window_InterestCertificateGeneration);
	}

	public void onClick$btnPrint(Event event) throws Exception {
		printAgreement(event);
	}

	private String getAddress(String seperator, String... values) {
		StringBuilder builder = new StringBuilder();

		for (String value : values) {
			if (StringUtils.isEmpty(value)) {
				continue;
			}

			if (builder.length() > 0) {
				builder.append(seperator);
			}

			builder.append(builder);
		}

		return builder.toString();
	}

	private void printAgreement(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		doSetValidation();
		doRemoveValidation();

		if (!"covenant".equalsIgnoreCase(getArgument("module"))) {
			try {
				if (this.finType.getValidatedValue() == "") {
					wve.add(new WrongValueException(this.finType, Labels.getLabel("FIELD_NO_INVALID",
							new String[] { Labels.getLabel("label_SelectFinReferenceDialog_FinType.value") })));
				}
			} catch (WrongValueException e) {
				wve.add(e);
			}

			try {
				if (getComboboxValue(this.financeYear).equals(PennantConstants.List_Select)) {
					wve.add(new WrongValueException(this.financeYear, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_InterestCertificate_FinanceYear.value") })));
				}
			} catch (WrongValueException e) {
				wve.add(e);
			}
		}

		try {
			if (this.finReference.getValidatedValue() == "" && !this.finReference.isButtonDisabled()) {
				wve.add(new WrongValueException(this.finReference, Labels.getLabel("FIELD_NO_INVALID",
						new String[] { Labels.getLabel("label_SelectFinReferenceDialog_FinReference.value") })));
			}
		} catch (WrongValueException e) {
			wve.add(e);
		}

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		if (!"covenant".equalsIgnoreCase(getArgument("module"))) {
			int year = Integer.parseInt(getComboboxValue(this.financeYear));

			Date startDate = DateUtil.getDate(year, 3, 1);
			Date endDate = DateUtil.getDate(year + 1, 2, 31);

			boolean isProvCert = false;
			if ("provisional".equalsIgnoreCase(getArgument("module"))) {
				isProvCert = true;
			}

			InterestCertificate intCert = getInterestCertificateService()
					.getInterestCertificateDetails(this.finReference.getValue(), startDate, endDate, isProvCert);
			if (intCert == null) {
				MessageUtil.showError("Details Not Found");
				return;
			}

			Date appldate = SysParamUtil.getAppDate();
			String appDate = DateUtil.formatToLongDate(appldate);
			intCert.setAppDate(appDate);
			intCert.setFinStartDate("01-04-" + getComboboxValue(this.financeYear));
			intCert.setFinEndDate("31-03-" + String.valueOf(Integer.valueOf(getComboboxValue(this.financeYear)) + 1));
			intCert.setFinPostDate(appldate);

			Method[] methods = intCert.getClass().getDeclaredMethods();

			for (Method property : methods) {
				if (property.getName().startsWith("get")) {
					String field = property.getName().substring(3);
					Object value;

					try {
						value = property.invoke(intCert);
					} catch (Exception e) {
						continue;
					}

					if (value == null) {
						try {
							String stringParameter = "";
							intCert.getClass().getMethod("set" + field, new Class[] { String.class }).invoke(intCert,
									stringParameter);
						} catch (Exception e) {
							logger.error("Exception: ", e);
						}
					}
				}
			}

			String address = getAddress("\n", intCert.getCustAddrHnbr(), intCert.getCustFlatNbr(),
					intCert.getCustAddrStreet(), intCert.getCustAddrCity(), intCert.getCustAddrState(),
					intCert.getCustAddrZIP(), intCert.getCountryDesc());

			if ("provisional".equalsIgnoreCase(getArgument("module"))) {
				address = getAddress("\n", address, intCert.getCustEmail(), intCert.getCustPhoneNumber());
				intCert.setCustAddress(address);
			}

			if ("interest".equalsIgnoreCase(getArgument("module"))) {
				intCert.setCustAddress(address);
			}

			String agreement = null;
			if ("provisional".equalsIgnoreCase(getArgument("module"))) {
				agreement = "ProvisionalCertificate.docx";
			}

			if ("interest".equalsIgnoreCase(getArgument("module"))) {
				agreement = "InterestCertificate.docx";
			}

			String templatePath = PathUtil.getPath(PathUtil.FINANCE_INTERESTCERTIFICATE);
			TemplateEngine engine = null;
			try {
				engine = new TemplateEngine(templatePath, templatePath);
			} catch (Exception e) {
				MessageUtil
						.showError(String.format("[%s] template not found in [%s] location.", agreement, templatePath));
				return;
			}
			String refNo = intCert.getFinReference();
			String reportName = refNo + "_" + agreement.substring(0, agreement.length() - 4) + "pdf";
			try {
				engine.setTemplate(agreement);
			} catch (Exception e) {
				MessageUtil.showError(agreement + " Not Found");
				return;
			}
			engine.loadTemplate();
			engine.mergeFields(intCert);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			engine.getDocument().save(stream, SaveFormat.PDF);
			showDocument(this.window_InterestCertificateGeneration, reportName, SaveFormat.PDF, false, tabbox, stream);

		}
		if ("covenant".equalsIgnoreCase(getArgument("module"))) {
			List<CovenantAggrement> cvntAggrement = interestCertificateService
					.getCovenantReportStatus(this.finReference.getValue());

			if (CollectionUtils.isEmpty(cvntAggrement)) {
				MessageUtil.showError("Details Not Found");
				return;
			}
			CovenantAggrement covenantAgreement = new CovenantAggrement();
			if (CollectionUtils.isNotEmpty(cvntAggrement)) {
				CovenantAggrement ca = cvntAggrement.get(0);
				String combinedString = null;
				if (ca.getCustFlatNbr() == null) {
					ca.setCustFlatNbr("");
				}
				if (ca.getCustPOBox() == null) {
					ca.setCustPOBox("");
				}
				combinedString = ca.getCustAddrHnbr() + ca.getCustFlatNbr() + "\n" + ca.getCustAddrStreet() + " "
						+ ca.getCustAddrCity() + "\n" + ca.getCustAddrProvince() + "\n" + ca.getCustAddrCountry() + "\n"
						+ ca.getCustPOBox();
				covenantAgreement.setCustAddrHnbr(combinedString);
				covenantAgreement.setFinReference(ca.getFinReference());
				covenantAgreement.setCustshrtname(ca.getCustshrtname());

			}
			Date appdate = SysParamUtil.getAppDate();
			covenantAgreement.setAppDate(DateUtil.format(appdate, DateFormat.LONG_DATE));
			covenantAgreement.setCovenantAggrementList(cvntAggrement);

			String agreement = "LOD.docx";
			String reportName = agreement.substring(0, agreement.length() - 4) + "pdf";
			ByteArrayOutputStream stream = new ByteArrayOutputStream();

			for (CovenantAggrement covenantAggrement : cvntAggrement) {

				Method[] methods = covenantAggrement.getClass().getDeclaredMethods();

				for (Method property : methods) {
					if (property.getName().startsWith("get")) {
						String field = property.getName().substring(3);
						Object value;

						try {
							value = property.invoke(covenantAggrement);
						} catch (Exception e) {
							continue;
						}

						if (field.equalsIgnoreCase("CovenantAggrementList")) {
							continue;
						}

						if (value == null) {
							try {
								String stringParameter = "";
								covenantAggrement.getClass().getMethod("set" + field, new Class[] { String.class })
										.invoke(covenantAggrement, stringParameter);
							} catch (Exception e) {
								logger.error("Exception: ", e);
							}
						}
					}
				}

				String templatePath = PathUtil.getPath(PathUtil.COVENANT_STATUS_REPORT);
				TemplateEngine engine = null;
				try {
					engine = new TemplateEngine(templatePath, templatePath);
				} catch (Exception e) {
					MessageUtil.showError("Path Not Found");
					return;
				}
				try {
					engine.setTemplate(agreement);
				} catch (Exception e) {
					MessageUtil.showError(agreement + " Not Found");
					return;
				}
				engine.loadTemplate();
				engine.mergeFields(covenantAgreement);

				engine.getDocument().save(stream, SaveFormat.PDF);
			}

			showDocument(this.window_InterestCertificateGeneration, reportName, SaveFormat.PDF, false, tabbox, stream);

		}
		logger.debug("Leaving");
	}

	private void doRemoveValidation() {
		this.finReference.setConstraint("");
		this.financeYear.setConstraint("");

	}

	public void showDocument(Window window, String reportName, int format, boolean saved, Tabbox tabbox,
			ByteArrayOutputStream stream) {
		logger.debug("Entering ");

		if ((SaveFormat.DOCX) == format) {
			Filedownload.save(new AMedia(reportName, "msword", "application/msword", stream.toByteArray()));
		} else {

			Map<String, Object> arg = new HashMap<String, Object>();
			arg.put("reportBuffer", stream.toByteArray());
			arg.put("parentWindow", window);
			arg.put("reportName", reportName);
			arg.put("isAgreement", false);
			arg.put("docFormat", format);
			arg.put("searchClick", false);
			arg.put("dialogWindow", window);
			// For customer360 Report should be displayed as modal
			if (isCustomer360) {
				arg.put("Customer360", true);
				arg.put("searchClick", true);
			} else {
				arg.put("selectTab", tabbox.getSelectedTab());
				arg.put("tabbox", tabbox);
			}
			Executions.createComponents("/WEB-INF/pages/Reports/ReportView.zul", window, arg);
		}
		stream = null;
		logger.debug("Leaving");
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	protected void doPostClose() {
		if (tabbox != null) {
			tabbox.getSelectedTab().close();
		}
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");

		this.financeYear.setText("");
		this.finReference.setValue("");

		logger.debug("Leaving");
	}

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.financeYear.setErrorMessage("");
		this.finReference.setErrorMessage("");
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

	public InterestCertificateGenerationDialogCtrl getInterestCertificateGenerationDialogCtrl() {
		return interestCertificateGenerationDialogCtrl;
	}

	public void setInterestCertificateGenerationDialogCtrl(
			InterestCertificateGenerationDialogCtrl interestCertificateGenerationDialogCtrl) {
		this.interestCertificateGenerationDialogCtrl = interestCertificateGenerationDialogCtrl;
	}

	public InterestCertificateService getInterestCertificateService() {
		return interestCertificateService;
	}

	public void setInterestCertificateService(InterestCertificateService interestCertificateService) {
		this.interestCertificateService = interestCertificateService;
	}

}

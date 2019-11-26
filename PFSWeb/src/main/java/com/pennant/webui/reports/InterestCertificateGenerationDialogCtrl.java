package com.pennant.webui.reports;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;
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
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.aspose.words.SaveFormat;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PathUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.agreement.InterestCertificate;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.systemmasters.InterestCertificateService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.document.generator.TemplateEngine;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class InterestCertificateGenerationDialogCtrl extends GFCBaseCtrl<InterestCertificate> {
	private static final long serialVersionUID = 9031340167587772517L;
	private static final Logger logger = Logger.getLogger(InterestCertificateGenerationDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_InterestCertificateGeneration; // autowired

	protected ExtendedCombobox finReference;
	protected Combobox financeYear;
	protected Label WindowTitle;
	protected ExtendedCombobox finType;

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
	 * @throws Exception
	 */
	public void onCreate$window_InterestCertificateGeneration(Event event) throws Exception {
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
		}
		// For customer360 Report should be displayed as modal 
		if (arguments.containsKey("customer360")) {
			isCustomer360 = (boolean) arguments.containsKey("customer360");
		}

		doSetFieldProperties();
		this.finReference.setButtonDisabled(true);
		this.window_InterestCertificateGeneration.doModal();

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 * 
	 * @throws ParseException
	 */
	private void doSetFieldProperties() throws ParseException {
		logger.debug("Entering");

		this.finType.setMaxlength(8);
		this.finType.setMandatoryStyle(true);
		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("FinType");
		this.finType.setDescColumn("FinTypeDesc");
		this.finType.setValidateColumns(new String[] { "FinType" });

		this.finReference.setMaxlength(20);
		this.finReference.setTextBoxWidth(120);
		this.finReference.setMandatoryStyle(true);
		this.finReference.setModuleName("FinanceMain");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setValidateColumns(new String[] { "FinReference" });
		this.finReference.setWhereClause("FinisActive = 1");

		String startYear = SMTParameterConstants.BAJAJFINANCE_STARTDATE;
		String year = startYear.substring(startYear.length() - 4);

		int years = DateUtility.getYearsBetween(new SimpleDateFormat("dd/MM/yyyy").parse(startYear),
				DateUtility.getAppDate());

		int month = DateUtility.getMonth(DateUtility.getAppDate());

		// As of financeYear Start from april
		if (month >= 4) {
			years = years + 1;
		}

		financeYearList = new ArrayList<ValueLabel>(years);
		for (int i = 0; i <= years - 1; i++) {
			financeYearList.add(
					new ValueLabel(String.valueOf(Integer.valueOf(year) + i), String.valueOf(Integer.valueOf(year) + i)
							+ "-" + String.valueOf(Integer.valueOf(year.substring(year.length() - 2)) + i + 1)));
		}

		fillComboBox(this.financeYear, "", financeYearList, "");

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
		logger.debug("Entering" + event.toString());
		Clients.clearWrongValue(this.finReference);
		this.finReference.setConstraint("");
		this.finReference.setErrorMessage("");
		Object dataObject = this.finReference.getObject();
		if (dataObject instanceof String) {
			this.finReference.setValue("");
			this.finReference.setObject("");
		} else {
			FinanceMain details = (FinanceMain) dataObject;
			if (details != null) {
				this.finReference.setValue(details.getFinReference());
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		if (!this.finReference.isReadonly()) {
			this.finReference.setConstraint(new PTStringValidator(
					Labels.getLabel("label_SelectFinReferenceDialog_FinReference.value"), null, true, true));
		}
		if (!this.financeYear.isDisabled() && this.financeYear.isVisible()) {
			this.financeYear.setConstraint(new StaticListValidator(financeYearList,
					Labels.getLabel("label_InterestCertificate_FinanceYear.value")));
		}
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_InterestCertificateGeneration);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws CustomerNotFoundException
	 * @throws CustomerLimitProcessException
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
		doRemoveValidation();

		try {
			if (this.finType.getValidatedValue() == "") {
				wve.add(new WrongValueException(this.finType, Labels.getLabel("FIELD_NO_INVALID",
						new String[] { Labels.getLabel("label_SelectFinReferenceDialog_FinType.value") })));
			}
		} catch (WrongValueException e) {
			wve.add(e);
		}

		try {
			if (this.finReference.getValidatedValue() == "" && !this.finReference.isButtonDisabled()) {
				wve.add(new WrongValueException(this.finReference, Labels.getLabel("FIELD_NO_INVALID",
						new String[] { Labels.getLabel("label_SelectFinReferenceDialog_FinReference.value") })));
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

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		String startDate = "1/4/" + getComboboxValue(this.financeYear);
		String endDate = "31/3/" + String.valueOf(Integer.valueOf(getComboboxValue(this.financeYear)) + 1);
		boolean isProvCert = false;
		if ("provisional".equalsIgnoreCase(getArgument("module"))) {
			isProvCert = true;
		}
		
		InterestCertificate interestCertificate = getInterestCertificateService()
				.getInterestCertificateDetails(this.finReference.getValue(), startDate, endDate, isProvCert);
		if (interestCertificate == null) {
			MessageUtil.showError("Details Not Found");
			return;
		}
		Date appldate = DateUtility.getAppDate();
		String appDate = DateUtility.formatToLongDate(appldate);
		interestCertificate.setAppDate(appDate);
		interestCertificate.setFinStartDate(startDate);
		interestCertificate.setFinEndDate(endDate);
		interestCertificate.setFinPostDate(DateUtility.getAppDate());

		Method[] methods = interestCertificate.getClass().getDeclaredMethods();

		for (Method property : methods) {
			if (property.getName().startsWith("get")) {
				String field = property.getName().substring(3);
				Object value;

				try {
					value = property.invoke(interestCertificate);
				} catch (Exception e) {
					continue;
				}

				if (value == null) {
					try {
						String stringParameter = "";
						interestCertificate.getClass().getMethod("set" + field, new Class[] { String.class })
								.invoke(interestCertificate, stringParameter);
					} catch (Exception e) {
						logger.error("Exception: ", e);
					}
				}
			}
		}
		String combinedString = null;
		String custflatnbr = null;
		if (interestCertificate.getCustFlatNbr().equals("")) {
			custflatnbr = " ";
		} else {
			custflatnbr = " " + interestCertificate.getCustFlatNbr() + "\n";
		}
		if ("provisional".equalsIgnoreCase(getArgument("module"))) {
			combinedString = interestCertificate.getCustAddrHnbr() + custflatnbr
					+ interestCertificate.getCustAddrStreet() + "\n" + interestCertificate.getCustAddrCity() + "\n"
					+ interestCertificate.getCustAddrState() + " " + interestCertificate.getCustAddrZIP() + "\n"
					+ interestCertificate.getCountryDesc() + "\n" + interestCertificate.getCustEmail() + "\n"
					+ interestCertificate.getCustPhoneNumber();
			interestCertificate.setCustAddrHnbr(combinedString);
		}

		if ("interest".equalsIgnoreCase(getArgument("module"))) {
			combinedString = interestCertificate.getCustAddrHnbr() + custflatnbr
					+ interestCertificate.getCustAddrStreet() + "\n" + interestCertificate.getCustAddrCity() + "\n"
					+ interestCertificate.getCustAddrState() + " " + interestCertificate.getCustAddrZIP() + "\n"
					+ interestCertificate.getCountryDesc();
			interestCertificate.setCustAddrHnbr(combinedString);
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
			MessageUtil.showError("Path Not Found");
			return;
		}
		String refNo = interestCertificate.getFinReference();
		String reportName = refNo + "_" + agreement.substring(0, agreement.length() - 4) + "pdf";
		try {
			engine.setTemplate(agreement);
		} catch (Exception e) {
			MessageUtil.showError(agreement + " Not Found");
			return;
		}
		engine.loadTemplate();
		engine.mergeFields(interestCertificate);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		engine.getDocument().save(stream, SaveFormat.PDF);
		showDocument(this.window_InterestCertificateGeneration, reportName, SaveFormat.PDF, false, tabbox, stream);
		logger.debug("Leaving");
	}

	private void doRemoveValidation() {
		this.finReference.setConstraint("");
		this.financeYear.setConstraint("");

	}

	public void showDocument(Window window, String reportName, int format, boolean saved, Tabbox tabbox,
			ByteArrayOutputStream stream) throws Exception {
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
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		if (doClose(false)) {
			if (tabbox != null) {
				tabbox.getSelectedTab().close();
			}
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

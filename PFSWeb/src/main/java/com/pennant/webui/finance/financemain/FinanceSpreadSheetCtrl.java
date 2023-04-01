package com.pennant.webui.finance.financemain;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zss.api.Importer;
import org.zkoss.zss.api.Importers;
import org.zkoss.zss.api.Range;
import org.zkoss.zss.api.Ranges;
import org.zkoss.zss.api.model.Book;
import org.zkoss.zss.api.model.CellData;
import org.zkoss.zss.api.model.CellData.CellType;
import org.zkoss.zss.api.model.CellStyle;
import org.zkoss.zss.api.model.EditableCellStyle;
import org.zkoss.zss.api.model.Sheet;
import org.zkoss.zss.ui.AuxAction;
import org.zkoss.zss.ui.Spreadsheet;
import org.zkoss.zul.Button;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CostComponentDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.finance.CreditReviewData;
import com.pennant.backend.model.finance.CreditReviewDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.spreadsheet.SheetCopier;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.service.hook.ExtendedCreditReviewHook;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class FinanceSpreadSheetCtrl extends GFCBaseCtrl<CreditReviewData> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(FinanceSpreadSheetCtrl.class);

	protected Window window_SpreadSheetDialog;

	protected Button button_FetchData;
	protected Spreadsheet spreadSheet = null;

	private FinanceMainBaseCtrl financeMainDialogCtrl = null;
	private boolean isReadOnly;
	private boolean enqiryModule;
	private boolean isValidationAlw;

	private CreditReviewDetails creditReviewDetails = null;
	private CreditReviewData creditReviewData = null;

	private FinanceDetail fd;
	private Tab parentTab;

	private List<Verification> verifications;

	int format = PennantConstants.defaultCCYDecPos;

	@Autowired(required = false)
	@Qualifier("extendedCreditReviewHook")
	private ExtendedCreditReviewHook extendedCreditReviewHook;

	public FinanceSpreadSheetCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinanceMainDialog";
	}

	public void onCreate$window_SpreadSheetDialog(ForwardEvent event) {
		logger.debug("Entering");

		setPageComponents(window_SpreadSheetDialog);

		try {

			if (arguments.containsKey("parentTab")) {
				this.parentTab = (Tab) arguments.get("parentTab");
			}

			if (arguments.containsKey("creditReviewDetails")) {
				this.creditReviewDetails = (CreditReviewDetails) arguments.get("creditReviewDetails");
			}

			if (arguments.containsKey("creditReviewData") && arguments.get("creditReviewData") != null) {
				creditReviewData = (CreditReviewData) arguments.get("creditReviewData");
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				this.financeMainDialogCtrl = (FinanceMainBaseCtrl) arguments.get("financeMainDialogCtrl");
			}

			if (arguments.containsKey("financeDetail")) {
				this.fd = (FinanceDetail) arguments.get("financeDetail");
			}

			if (arguments.containsKey("Right_Eligibility")) {
				isReadOnly = (boolean) arguments.get("Right_Eligibility");
			}

			if (arguments.containsKey("enqiryModule")) {
				isReadOnly = (boolean) arguments.get("enqiryModule");
			}

			if (arguments.containsKey("enqiryModule")) {
				enqiryModule = (boolean) arguments.get("enqiryModule");
			}

			if (arguments.containsKey("isValidationAlw")) {
				this.isValidationAlw = (boolean) arguments.get("isValidationAlw");
			}

			if (arguments.containsKey("verifications")) {
				this.verifications = (List<Verification>) arguments.get("verifications");
			}

			if (this.creditReviewDetails != null) {
				doShowDialog();
			}

			if (financeMainDialogCtrl != null) {
				financeMainDialogCtrl.setFinanceSpreadSheetCtrl(this);
			}

			if (parentTab != null && creditReviewDetails != null) {
				doDisplayTab(creditReviewDetails.getEligibilityMethod());
			} else {
				doDisplayTab(null);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_SpreadSheetDialog.onClose();
		}

		logger.debug("Leaving");
	}

	public void doDisplayTab(String eligibilityMethod) {
		if (parentTab != null) {
			this.parentTab.setVisible(StringUtils.isNotEmpty(eligibilityMethod));
		}
	}

	public boolean isTabVisible() {
		return parentTab == null ? false : parentTab.isVisible();
	}

	private File getFile(String fileName) {
		String reportSrc = PathUtil.getPath(PathUtil.REPORTS_FINANCIALS_SPREADSHEETS) + "/" + fileName;
		return new File(reportSrc);
	}

	private Sheet getSheet(String sheetNamePrefix, Book book, Map<String, Object> dataMap) {
		String employmentType = (String) dataMap.get("CUST_EMPLOYMENT_TYPE");

		if ("#".equals(employmentType) && isValidationAlw) {
			throw new AppException("Employment Type rquired for Co-Applicants.");
		} else if ("#".equals(employmentType)) {
			return null;
		}

		return getSheet(sheetNamePrefix, book, dataMap, employmentType);
	}

	private Sheet getSheet(String sheetNamePrefix, Book book, Map<String, Object> dataMap, String employmentType) {
		String custCIF = (String) dataMap.get("CIF");
		String sheetName = sheetNamePrefix;

		if ("#".equals(employmentType) && isValidationAlw) {
			throw new AppException("Employment Type rquired for Customer.");
		} else if ("#".equals(employmentType)) {
			return null;
		}

		Sheet sourceSheet = book.getSheet(sheetName);

		if (sourceSheet == null) {
			throw new AppException(sheetName + " Sheet not found.");
		}

		sheetName = sheetName.concat("_").concat(employmentType).concat("_").concat(custCIF);

		return SheetCopier.clone(sheetName, sourceSheet);
	}

	public void doShowDialog() {
		logger.debug(Literal.ENTERING);

		try {

			String templateName = this.creditReviewDetails.getTemplateName();

			// Loading Template
			Book book = null;
			if (spreadSheet.getBook() == null) {
				Importer importer = Importers.getImporter();
				book = importer.imports(getFile(templateName), templateName);

				spreadSheet.disableUserAction(AuxAction.ADD_SHEET, true);
				spreadSheet.disableUserAction(AuxAction.RENAME_SHEET, true);
				spreadSheet.disableUserAction(AuxAction.HIDE_SHEET, true);
				spreadSheet.disableUserAction(AuxAction.MOVE_SHEET_LEFT, true);
				spreadSheet.disableUserAction(AuxAction.MOVE_SHEET_RIGHT, true);
				spreadSheet.disableUserAction(AuxAction.DELETE, true);
				spreadSheet.disableUserAction(AuxAction.UNHIDE_SHEET, true);
				spreadSheet.disableUserAction(AuxAction.PROTECT_SHEET, true);
				spreadSheet.disableUserAction(AuxAction.COPY, true);

				spreadSheet.setHiderowhead(true);

				spreadSheet.setHidecolumnhead(true);
				spreadSheet.setShowSheetbar(true);
				spreadSheet.enableBindingAnnotation();

				spreadSheet.setMaxVisibleColumns(100);
				spreadSheet.setMaxVisibleRows(130);
				spreadSheet.setPreloadRowSize(100);

				spreadSheet.setBook(book);
			} else {
				book = spreadSheet.getBook();
			}

			// Getting applicant data from screen.
			Map<String, Object> applicantDataMap = getApplicantData();

			/* Getting co-applicant data from screen. */
			Map<String, Map<String, Object>> coApplicantData = getCoApplicantData();

			if (applicantDataMap.isEmpty()) {
				return;
			}

			/*
			 * Setting applicant data to corresponding cell based on the configuration, either from Fields or FieldKeys
			 */
			String employmentType = (String) applicantDataMap.get("CUST_EMPLOYMENT_TYPE");
			Sheet appSheet = getSheet("APP", book, applicantDataMap, employmentType);

			if (appSheet == null) {
				return;
			}

			doSetData(appSheet);

			if (!enqiryModule) {
				doSetScreenData(appSheet, applicantDataMap);
				doSetVarificationData(appSheet);
			}

			spreadSheet.setSelectedSheet(appSheet.getSheetName());

			/* Setting co-applicant data to corresponding cell based on the configuration either from Fields or */
			for (Entry<String, Map<String, Object>> coAppData : coApplicantData.entrySet()) {
				Map<String, Object> coappData = coAppData.getValue();
				Sheet coAppSheet = getSheet("CO_APP", book, coappData);

				if (coAppSheet == null) {
					return;
				}

				doSetData(coAppSheet);

				Map<String, Object> modicoappData = new HashMap<String, Object>();
				for (Entry<String, Object> data : coappData.entrySet()) {
					modicoappData.put("CO_APP_" + data.getKey(), data.getValue());
				}

				doSetScreenData(coAppSheet, modicoappData);

			}

			setFinalSheet(book, applicantDataMap);

			this.button_FetchData.setVisible(!isReadOnly);

			this.window_SpreadSheetDialog.setHeight((getContentAreaHeight() - 75) + "px");
		} catch (Exception e) {
			MessageUtil.showMessage(e.getMessage());
		}

		logger.debug(Literal.LEAVING);
	}

	private Sheet getFinalSheet() {
		Sheet sheet = null;
		Book book = spreadSheet.getBook();
		int numberOfSheets = book.getNumberOfSheets();

		for (int i = 0; i < numberOfSheets; i++) {
			sheet = book.getSheetAt(i);

			if (!sheet.isHidden()) {
				break;
			}
		}

		return sheet;
	}

	private void setFinalSheet(Book book, Map<String, Object> applicantDataMap) {
		Sheet sheet = getFinalSheet();
		Ranges.range(sheet).setSheetOrder(spreadSheet.getBook().getNumberOfSheets() - 1);

		String formulaFieldName = "TOT_M_GI_SUM";

		setFormula(sheet, formulaFieldName);

		doSetData(sheet);

		if (!enqiryModule) {
			doSetScreenData(sheet, applicantDataMap);
		}
	}

	private String removeEndOperator(String deriveFormula) {
		while (StringUtils.endsWith(deriveFormula, "+") || StringUtils.endsWith(deriveFormula, "+")) {
			deriveFormula = StringUtils.removeEnd(deriveFormula, "+");
			deriveFormula = StringUtils.removeEnd(deriveFormula, "-");
		}

		return deriveFormula;
	}

	private void setFormula(Sheet sheet, String formulaFieldName) {
		Range range = getRange(sheet, formulaFieldName);

		if (range == null) {
			return;
		}

		CellData cellData = range.getCellData();

		if (cellData == null) {
			return;
		}

		CellType type = cellData.getType();

		if (type != CellType.FORMULA) {
			return;
		}

		String completeFormula = cellData.getFormulaValue();

		String formula = getFormula(formulaFieldName, completeFormula);

		String deriveFormula = "";
		for (String formulaField : StringUtils.split(formula, "+")) {
			if (deriveFormula.length() > 0) {
				deriveFormula = deriveFormula + "+";
			}

			deriveFormula = deriveFormula + deriveFormula(formulaField);

		}

		deriveFormula = removeEndOperator(deriveFormula);

		completeFormula = completeFormula.replace(formula, deriveFormula);

		cellData.setEditText("=" + completeFormula);

	}

	private String deriveFormula(String format) {
		String finalFormula = "";

		String prefix = format.split("!")[0];
		String reference = format.split("!")[1];

		String prefix1 = prefix;

		Book book = spreadSheet.getBook();
		int numberOfSheets = book.getNumberOfSheets();

		for (int i = 0; i < numberOfSheets; i++) {
			Sheet sheet = book.getSheetAt(i);

			if (sheet.isHidden()) {
				continue;
			}

			String sheetName = sheet.getSheetName();
			if (sheetName.startsWith(prefix) || sheetName.startsWith(prefix1)) {

				if (finalFormula.length() > 0) {
					finalFormula = finalFormula + "+";
				}
				if (sheetName.contains("-")) {
					sheetName = "'" + sheetName + "'";
				}
				finalFormula = finalFormula + sheetName + "!" + reference;
			}
		}

		return finalFormula;
	}

	private String getFormula(String formula, String formulaValue) {
		if (formula.endsWith("SUM") && formulaValue.startsWith("SUM")) {
			formulaValue = formulaValue.replace("SUM(", "");
			formulaValue = formulaValue.replace(")", "");
		}

		return formulaValue;
	}

	private Range getRange(org.zkoss.zss.api.model.Sheet sheet, String cellName) {

		Range range = null;
		try {
			range = Ranges.rangeByName(sheet, cellName);
		} catch (Exception e) {
			//
		}

		if (range != null) {
			return range;
		}

		try {
			range = Ranges.range(sheet, cellName);
		} catch (Exception e) {
			//
		}

		return range;
	}

	private void setCellValue(Sheet sheet, String cellName, Object object) {
		Range range = getRange(sheet, cellName);

		if (range == null || range.isWholeColumn()) {
			return;
		}
		CellData cellData = range.getCellData();
		CellType cellType = cellData.getType();

		if (cellData.isFormula()) {
			return;
		}

		if (cellType != null && object == null) {
			switch (cellType) {
			case NUMERIC:
				object = "0.00";
				break;
			case STRING:
			case BLANK:
				object = "";
				break;
			case BOOLEAN:
				object = false;
				break;
			default:
				object = "";
				break;
			}
		}

		try {
			if (range.getColumn() < 16383) {
				range.setCellValue(object);
			}
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}
	}

	private void doSetCellValue(Sheet sheet, String fieldName, Map<String, Object> dataMap, boolean savedData) {
		fieldName = StringUtils.trim(fieldName);

		String key = fieldName;
		if (savedData) {
			String sheetName = sheet.getSheetName();
			key = sheetName + "_" + fieldName;
		}

		Object obj = dataMap.get(key);
		if (obj == null) {
			return;
		}

		setCellValue(sheet, fieldName, dataMap.get(key));
	}

	private Object getCellValue(Sheet sheet, String cellName) {
		cellName = StringUtils.trim(cellName);

		Range range = getRange(sheet, cellName);

		if (range == null) {
			return "";
		}

		return range.getCellValue();
	}

	private Map<String, Object> getFieldsBySheet() {
		return convertStringToMap(creditReviewDetails.getFields());
	}

	private Map<String, Object> getKeyFieldsBySheet() {
		return convertStringToMap(creditReviewDetails.getFieldKeys());
	}

	private Map<String, Object> getProtectedFieldsBySheet() {
		return convertStringToMap(creditReviewDetails.getProtectedCells());
	}

	private Map<String, Object> getFormulaFieldsBySheet() {
		return convertStringToMap(creditReviewDetails.getFormulaCells());
	}

	private String[] getFields(Object object) {
		String[] fields = null;
		if (object == null || StringUtils.isEmpty(object.toString())) {
			return new String[0];
		}

		String[] array = object.toString().split(",");

		fields = new String[array.length];

		int index = 0;
		for (String fieldName : array) {
			fields[index++] = fieldName.trim();
		}

		return fields;
	}

	/**
	 * This method will set the data to corresponding cell, here the data is previous saved data of editable fields
	 * 
	 * The editable fields are configured in CreditReviewConfig#Fields (input data)
	 * 
	 * @param sheet
	 */
	private void doSetData(Sheet sheet) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> dataMap = new HashMap<>();

		if (creditReviewData != null) {
			dataMap = convertStringToMap(creditReviewData.getTemplateData());
		}

		Map<String, Object> fieldsBySheet = getFieldsBySheet();

		for (Entry<String, Object> fieldMap : fieldsBySheet.entrySet()) {
			if (sheet.getSheetName().startsWith(fieldMap.getKey())) {
				for (String fieldName : getFields(fieldMap.getValue())) {
					fieldName = StringUtils.trim(fieldName);

					unProtectField(sheet, fieldName);

					try {
						doSetCellValue(sheet, fieldName, dataMap, true);
					} catch (Exception e) {
						logger.warn(Literal.EXCEPTION, e);
					}
				}
			}
		}

		if (creditReviewDetails.getProtectedCells() != null) {
			Map<String, Object> protectedCellsBySheet = getProtectedFieldsBySheet();
			for (Entry<String, Object> fieldMap : protectedCellsBySheet.entrySet()) {
				if (sheet.getSheetName().startsWith(fieldMap.getKey())) {
					for (String fieldName : getFields(fieldMap.getValue())) {
						fieldName = StringUtils.trim(fieldName);
						protectField(sheet, fieldName);
					}
				}
			}
		}

		if (creditReviewDetails.getFormulaCells() != null) {
			Map<String, Object> formulaCellsBySheet = getFormulaFieldsBySheet();
			for (Entry<String, Object> fieldMap : formulaCellsBySheet.entrySet()) {
				if (sheet.getSheetName().startsWith(fieldMap.getKey())) {
					for (String fieldName : getFields(fieldMap.getValue())) {
						fieldName = StringUtils.trim(fieldName);
						setFormula(sheet, fieldName);
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method will set the data to corresponding cell, here the data is loaded from the screen
	 * 
	 * The editable fields are configured in CreditReviewConfig#FieldKeys (populate data from screen)
	 * 
	 * Here the cell data which is set by doSetData method will be overwrite by with screen data
	 * 
	 * @param sheet
	 * @param dataMap
	 */
	private void doSetScreenData(Sheet sheet, Map<String, Object> dataMap) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> fieldsBySheet = getKeyFieldsBySheet();

		for (Entry<String, Object> fieldMap : fieldsBySheet.entrySet()) {
			if (sheet.getSheetName().startsWith(fieldMap.getKey())) {
				for (String fieldName : getFields(fieldMap.getValue())) {
					fieldName = StringUtils.trim(fieldName);

					try {
						doSetCellValue(sheet, fieldName, dataMap, false);
					} catch (Exception e) {
						logger.warn(Literal.EXCEPTION, e);
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetVarificationData(Sheet sheet) {
		if (this.verifications == null) {
			return;
		}

		try {

			String marketValueFiedls = SysParamUtil.getValueAsString("CREDIT_MARKET_VALUATION");
			if (StringUtils.isEmpty(marketValueFiedls)) {
				return;
			}
			BigDecimal value1 = null;
			BigDecimal value2 = null;
			String[] arr = marketValueFiedls.split(",");
			for (int i = 0; i < this.verifications.size(); i++) {
				Verification verification = this.verifications.get(i);
				if (verification.getReinitid() == null && !StringUtils.isEmpty(verification.getFinalValDecision())) {
					if (verification.getAgencyName() == null) {
						continue;
					}

					if (value1 == null) {
						value1 = verification.getValuationAmount();
						continue;
					}

					value2 = verification.getValuationAmount();
					break;

				}
			}

			if (value1 == null) {
				value1 = BigDecimal.ZERO;
			}

			if (value2 == null) {
				value2 = BigDecimal.ZERO;
			}

			setCellValue(sheet, arr[0], PennantApplicationUtil.formateAmount(value1, 2));
			setCellValue(sheet, arr[1], PennantApplicationUtil.formateAmount(value2, 2));

		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * This method will re-set the cell values in the screen from the screen * The editable fields are configured in
	 * 
	 * CreditReviewConfig#FieldKeys (populate data from screen)
	 * 
	 * Here the cell data which is set by doSetData method will be overwrite by with screen data
	 */
	private void doResetScreenData() {
		logger.debug(Literal.ENTERING);

		Map<String, Object> keyFields = getKeyFieldsBySheet();

		Book book = spreadSheet.getBook();
		int numberOfSheets = book.getNumberOfSheets();

		Map<String, Object> applicantData = getApplicantData();
		Map<String, Map<String, Object>> coApplicantData = getCoApplicantData();

		for (int i = 0; i < numberOfSheets; i++) {
			Sheet sheet = book.getSheetAt(i);

			if (sheet.isHidden()) {
				continue;
			}

			String sheetName = sheet.getSheetName();

			for (Entry<String, Object> fieldMap : keyFields.entrySet()) {
				if (sheetName.startsWith(fieldMap.getKey())) {
					if (sheetName.startsWith("CO_APP")) {
						doSetScreenData(sheet, coApplicantData.get(getCIF(sheetName)));
					} else {
						doSetScreenData(sheet, applicantData);
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method will unprotected the corresponding cell, these cells are editable cells configured in
	 * CreditReviewConfig#Fields (input data)
	 * 
	 * @param sheet
	 * @param fieldName
	 */
	private void unProtectField(Sheet sheet, String fieldName) {

		if (isReadOnly) {
			return;
		}

		fieldName = StringUtils.trim(fieldName);

		Range range = getRange(sheet, fieldName);

		if (range != null) {
			CellStyle cellStyle = range.getCellStyle();
			EditableCellStyle newStyle = range.getCellStyleHelper().createCellStyle(cellStyle);
			newStyle.setLocked(false);
			range.setCellStyle(newStyle);
		}
	}

	private void protectField(Sheet sheet, String fieldName) {

		if (isReadOnly) {
			return;
		}

		fieldName = StringUtils.trim(fieldName);

		Range range = getRange(sheet, fieldName);

		if (range != null) {
			CellStyle cellStyle = range.getCellStyle();
			EditableCellStyle newStyle = range.getCellStyleHelper().createCellStyle(cellStyle);
			newStyle.setLocked(true);
			range.setCellStyle(newStyle);
		}
	}

	private Map<String, Object> convertStringToMap(String payload) {
		Map<String, Object> map = new HashMap<>();

		ObjectMapper obj = new ObjectMapper();

		try {
			return obj.readValue(payload, new TypeReference<HashMap<String, Object>>() {
			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return map;
	}

	private String convertMapToString(Map<String, Object> dataMap) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			return mapper.writeValueAsString(dataMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}

	private void doWriteComponentstoBean() {
		logger.debug(Literal.ENTERING);

		Map<String, Object> dataMap = new HashMap<>();

		Book book = spreadSheet.getBook();
		int numberOfSheets = book.getNumberOfSheets();

		Map<String, Object> fieldsBySheet = getFieldsBySheet();

		for (int i = 0; i < numberOfSheets; i++) {
			Sheet sheet = book.getSheetAt(i);

			if (sheet.isHidden()) {
				continue;
			}

			String sheetName = sheet.getSheetName();

			for (Entry<String, Object> fieldMap : fieldsBySheet.entrySet()) {
				if (sheetName.startsWith(fieldMap.getKey())) {
					for (String fieldName : getFields(fieldMap.getValue())) {
						String key = sheetName + "_" + StringUtils.trim(fieldName);

						dataMap.put(key, getCellValue(sheet, fieldName));
					}
				}
			}
		}

		if (this.creditReviewData == null) {
			this.creditReviewData = new CreditReviewData();
		}

		this.creditReviewData.setTemplateName(this.creditReviewDetails.getTemplateName());
		this.creditReviewData.setTemplateVersion(this.creditReviewDetails.getTemplateVersion());
		this.creditReviewData.setTemplateData(convertMapToString(dataMap));

		logger.debug(Literal.LEAVING);
	}

	public void onClick$button_FetchData(Event event) {
		doResetScreenData();
	}

	public boolean doSave(Radiogroup userAction, boolean isFromLoan) {
		logger.debug(Literal.ENTERING);

		boolean isDataChanged = checkIsDataChanged(userAction, isFromLoan);

		if (!isDataChanged) {
			doWriteComponentstoBean();

			if (StringUtils.isBlank(creditReviewData.getRecordType())) {
				creditReviewData.setVersion(creditReviewData.getVersion() + 1);
				creditReviewData.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				creditReviewData.setNewRecord(true);
			}
			creditReviewData.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			creditReviewData.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		}

		logger.debug(Literal.LEAVING);

		return isDataChanged;
	}

	private boolean isDataChanged(Sheet sheet, String cellName, Map<String, Object> dataMap) {
		if (!dataMap.containsKey(cellName)) {
			return false;
		}

		Object object1 = dataMap.get(cellName);
		Object object2 = getCellValue(sheet, cellName);

		if (object1 == null && object2 == null) {
			return false;
		}

		if (object1 != null && object2 == null) {
			return true;
		}

		if (object1 == null && object2 != null) {
			return true;
		}

		if (object1 instanceof BigDecimal || object2 instanceof BigDecimal || object1 instanceof Integer
				|| object2 instanceof Integer || object1 instanceof Double || object2 instanceof Double) {
			Double data1 = Double.valueOf(object1.toString());

			Double data2 = Double.valueOf(object2.toString());

			if (data1.compareTo(data2) != 0) {
				return true;
			}
		}

		return false;
	}

	private String getCIF(String sheetName) {
		String[] tokens = sheetName.split("_");

		return tokens[tokens.length - 1];
	}

	protected boolean isDataChanged() {
		Map<String, Object> keyFields = getKeyFieldsBySheet();

		Book book = spreadSheet.getBook();
		int numberOfSheets = book.getNumberOfSheets();

		Map<String, Object> applicantData = getApplicantData();
		Map<String, Map<String, Object>> coApplicantData = getCoApplicantData();

		for (int i = 0; i < numberOfSheets; i++) {
			Sheet sheet = book.getSheetAt(i);

			if (sheet.isHidden()) {
				continue;
			}

			String sheetName = sheet.getSheetName();

			for (Entry<String, Object> fieldMap : keyFields.entrySet()) {
				if (sheetName.startsWith(fieldMap.getKey())) {
					for (String cellName : getFields(fieldMap.getValue())) {

						boolean dataChanged = false;

						if (sheetName.startsWith("CO_APP")) {
							dataChanged = isDataChanged(sheet, cellName, coApplicantData.get(getCIF(sheetName)));
						} else {
							dataChanged = isDataChanged(sheet, cellName, applicantData);
						}

						if (dataChanged) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	private boolean checkIsDataChanged(Radiogroup userAction, boolean isFromLoan) {
		logger.debug(Literal.ENTERING);

		boolean isDataChanged = false;

		Object recordStatus = userAction.getSelectedItem().getValue();
		if (!(PennantConstants.RCD_STATUS_RESUBMITTED.equals(recordStatus)
				|| PennantConstants.RCD_STATUS_REJECTED.equals(recordStatus)
				|| PennantConstants.RCD_STATUS_CANCELLED.equals(recordStatus))) {

			isDataChanged = isDataChanged();

			if (isDataChanged) {
				MessageUtil.showMessage("Loan details has changed, eligibility needs to be verified.");
			}
		}

		logger.debug(Literal.LEAVING);
		return isDataChanged;
	}

	private Map<String, Object> getApplicantData() {

		CustomerDialogCtrl customerDialogCtrl = null;

		if (financeMainDialogCtrl != null) {
			customerDialogCtrl = financeMainDialogCtrl.getCustomerDialogCtrl();
		}

		CustomerDetails cd = null;
		if (customerDialogCtrl != null) {
			cd = customerDialogCtrl.getCustomerDetails();
		}

		if (cd == null && fd != null) {
			cd = fd.getCustomerDetails();
		}

		if (cd == null) {
			return new HashMap<>();
		}

		return getCustData(cd);
	}

	private Map<String, Map<String, Object>> getCoApplicantData() {
		Map<String, Map<String, Object>> map = new HashMap<>();

		JointAccountDetailDialogCtrl jaddCtrl = null;

		if (financeMainDialogCtrl != null) {
			jaddCtrl = financeMainDialogCtrl.getJointAccountDetailDialogCtrl();
		}

		List<JointAccountDetail> jadList = null;
		if (jaddCtrl != null) {
			jadList = jaddCtrl.getJointAccountDetailList();
		}

		if (jadList == null && fd != null) {
			jadList = fd.getJointAccountDetailList();
		}

		if (jadList == null) {
			return map;
		}

		for (JointAccountDetail jad : jadList) {
			CustomerDetails cd = jad.getCustomerDetails();
			Customer customer = cd.getCustomer();

			String employmentType = customer.getSubCategory();
			String custCategory = customer.getCustCtgCode();

			if (StringUtils.isEmpty(employmentType) && PennantConstants.PFF_CUSTCTG_CORP.equals(custCategory)) {
				employmentType = PennantConstants.EMPLOYMENTTYPE_SENP;
			}

			String custCIF = customer.getCustCIF();

			map.put(custCIF, getCustData(cd));
		}

		return map;
	}

	private Map<String, Object> getCustData(CustomerDetails cd) {
		Map<String, Object> dataMap = new HashMap<>();

		Customer customer = cd.getCustomer();

		String custCIF = customer.getCustCIF();
		Date finStartDate = getFinStartDate();
		Date maturityDate = getMaturityDate();
		String employmentType = customer.getSubCategory();
		String custCategory = customer.getCustCtgCode();

		if (StringUtils.isEmpty(employmentType) && PennantConstants.PFF_CUSTCTG_CORP.equals(custCategory)) {
			employmentType = PennantConstants.EMPLOYMENTTYPE_SENP;
		}

		if ("SALARIED".equals(employmentType)) {
			employmentType = "SAL";
		}

		dataMap.put("CIF", custCIF);
		dataMap.put("CUST_CTG", custCategory);
		dataMap.put("CUST_FULL_NAME", customer.getCustShrtName());
		dataMap.put("CUST_EMPLOYMENT_TYPE", employmentType);
		dataMap.put("CUST_DOB", customer.getCustDOB());
		dataMap.put("CUST_AGE", DateUtil.getYearsBetween(SysParamUtil.getAppDate(), customer.getCustDOB()));

		dataMap.put("FIN_START_DATE", finStartDate);
		dataMap.put("FIN_MATURITY_DATE", maturityDate);
		dataMap.put("CUST_AGE_AT_MATURITY", DateUtil.getYearsBetween(maturityDate, customer.getCustDOB()));

		dataMap.put("ROI", getROI());
		dataMap.put("FIN_ASSET_VALUE", getFinAssetValue());
		dataMap.put("TENOR", getTenor());
		dataMap.put("FIRST_PRI_PFT", getFIRST_PRI_PFT());

		dataMap.putAll(getIncomeMap(cd.getCustomerIncomeList()));

		setCollateralData(dataMap);

		setCustFinanceExposure(cd, dataMap);

		return dataMap;
	}

	private void setCustFinanceExposure(CustomerDetails cd, Map<String, Object> dataMap) {
		List<FinanceEnquiry> list = cd.getCustFinanceExposureList();

		Date date = SysParamUtil.getAppDate();
		BigDecimal lessThan2years = BigDecimal.ZERO;
		BigDecimal greaterThan2years = BigDecimal.ZERO;
		BigDecimal greaterThan12months = BigDecimal.ZERO;

		for (FinanceEnquiry financeEnquiry : list) {

			int years = DateUtil.getYearsBetween(financeEnquiry.getFinStartDate(), date);
			if (years <= 2) {
				lessThan2years = lessThan2years.add(financeEnquiry.getMaxInstAmount());
			} else {
				greaterThan2years = greaterThan2years.add(financeEnquiry.getMaxInstAmount());
			}

			if ((financeEnquiry.getNOInst() - financeEnquiry.getNOPaidinst()) > 12) {
				greaterThan12months = greaterThan12months.add(financeEnquiry.getMaxInstAmount());
			}

		}

		List<CustomerExtLiability> list2 = cd.getCustomerExtLiabilityList();
		if (list2 != null) {
			for (CustomerExtLiability custExt : list2) {
				if (custExt.getBalanceTenure() > 12) {
					BigDecimal installAmount = custExt.getInstalmentAmount();
					if (installAmount == null) {
						installAmount = BigDecimal.ZERO;
					}
					greaterThan12months = greaterThan12months.add(installAmount);
				}
			}
		}
		dataMap.put("CUST_EXPOS_LESS2", PennantApplicationUtil.formateAmount(lessThan2years, format));
		dataMap.put("CUST_EXPOS_GREATER2", PennantApplicationUtil.formateAmount(greaterThan2years, format));
		dataMap.put("CUST_OBLIGATION",
				PennantApplicationUtil.formateAmount(lessThan2years.add(greaterThan2years), format));

		BigDecimal custObligation = lessThan2years.add(greaterThan2years);
		dataMap.put("CUST_OBLIGATION", PennantApplicationUtil.formateAmount(custObligation, format));
		dataMap.put("CUST_OBLIGATION_GREATER12", PennantApplicationUtil.formateAmount(greaterThan12months, format));
	}

	private void setCollateralData(Map<String, Object> dataMap) {
		BigDecimal declaredValue = BigDecimal.ZERO;
		BigDecimal consideredForLTV = BigDecimal.ZERO;
		BigDecimal consideredForSanction = BigDecimal.ZERO;

		List<CollateralAssignment> list = null;

		if (financeMainDialogCtrl != null) {
			CollateralHeaderDialogCtrl collheadr = financeMainDialogCtrl.getCollateralHeaderDialogCtrl();
			if (collheadr != null) {
				list = collheadr.getCollateralAssignments();
			} else {
				if (fd != null) {
					list = fd.getCollateralAssignmentList();
				}
			}
		}

		if (list != null) {
			for (CollateralAssignment collateralAssignment : list) {
				List<CostComponentDetail> costlist = collateralAssignment.getCostComponentDetailList();
				if (costlist != null) {
					for (CostComponentDetail costComp : costlist) {
						declaredValue = declaredValue.add(costComp.getDeclaredValue());
						consideredForLTV = consideredForLTV.add(costComp.getConsideredForLTV());
						consideredForSanction = consideredForSanction.add(costComp.getConsideredForSanction());
					}
				}

			}
		}

		dataMap.put("DECLAREDVALUE", CurrencyUtil.unFormat(declaredValue, format));
		dataMap.put("CONSIDEREDFORLTV", CurrencyUtil.unFormat(consideredForLTV, format));
		dataMap.put("CONSIDEREDFORSANCTION", CurrencyUtil.unFormat(consideredForSanction, format));
	}

	private Map<String, Object> getIncomeMap(List<CustomerIncome> list) {
		Map<String, Object> dataMap = new HashMap<>();

		for (CustomerIncome item : list) {
			BigDecimal income = item.getIncome();
			String incomeType = item.getIncomeType();
			String formatedIncome = CurrencyUtil.format(income, format);
			dataMap.put(incomeType, formatedIncome);
		}

		return dataMap;
	}

	private Date getMaturityDate() {
		if (financeMainDialogCtrl != null) {
			return financeMainDialogCtrl.maturityDate_two.getValue();
		}

		return null;
	}

	private Date getFinStartDate() {
		if (financeMainDialogCtrl != null) {
			return financeMainDialogCtrl.finStartDate.getValue();
		}
		return null;

	}

	private BigDecimal getFinAssetValue() {
		if (financeMainDialogCtrl != null) {
			return financeMainDialogCtrl.finAssetValue.getActualValue();
		}

		return BigDecimal.ZERO;
	}

	private BigDecimal getROI() {
		BigDecimal roi = BigDecimal.ZERO;

		if (financeMainDialogCtrl == null) {
			return roi;
		}

		String rateBasis = getComboboxValue(financeMainDialogCtrl.repayRateBasis);

		if (CalculationConstants.RATE_BASIS_R.equals(rateBasis)
				|| CalculationConstants.RATE_BASIS_C.equals(rateBasis)) {
			if (StringUtils.isNotEmpty(financeMainDialogCtrl.repayRate.getBaseValue())) {
				roi = financeMainDialogCtrl.repayRate.getEffRateValue();
			} else {
				roi = financeMainDialogCtrl.repayProfitRate.getValue();
			}
		} else {
			roi = financeMainDialogCtrl.repayProfitRate.getValue();
		}

		if (roi == null) {
			roi = BigDecimal.ZERO;
		}

		return roi.divide(new BigDecimal(100));
	}

	private int getTenor() {
		if (financeMainDialogCtrl != null) {
			return financeMainDialogCtrl.numberOfTerms_two.intValue();
		}
		return 0;
	}

	private BigDecimal getFIRST_PRI_PFT() {

		BigDecimal emi = new BigDecimal(0);
		if (financeMainDialogCtrl != null) {
			FinanceDetail fanced = financeMainDialogCtrl.getFinanceDetail();
			if (fanced == null) {
				return emi;
			}

			if (fanced.getFinScheduleData() == null) {
				return emi;
			}

			List<FinanceScheduleDetail> listsc = fanced.getFinScheduleData().getFinanceScheduleDetails();

			if (listsc == null || listsc.isEmpty()) {
				return emi;
			}

			for (FinanceScheduleDetail fsd : listsc) {

				if (fsd.getPrincipalSchd().compareTo(BigDecimal.ZERO) > 0
						&& fsd.getProfitSchd().compareTo(BigDecimal.ZERO) > 0) {
					emi = fsd.getPrincipalSchd().add(fsd.getProfitSchd());
					break;
				}

			}

		}
		if (emi.compareTo(BigDecimal.ZERO) > 0) {
			emi = CurrencyUtil.unFormat(emi, format);
		}

		return emi;
	}

	public void doSaveScoreDetail(FinanceDetail afd) {
		if (extendedCreditReviewHook != null) {
			extendedCreditReviewHook.saveExtCreditReviewDetails(afd);
		}
	}

	public CreditReviewData getCreditReviewData() {
		return creditReviewData;
	}

}
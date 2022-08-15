package com.pennant.webui.finance.financemain;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.json.JSONObject;
import org.zkoss.json.parser.JSONParser;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zss.api.Importer;
import org.zkoss.zss.api.Importers;
import org.zkoss.zss.api.Range;
import org.zkoss.zss.api.Ranges;
import org.zkoss.zss.api.model.Book;
import org.zkoss.zss.api.model.CellStyle;
import org.zkoss.zss.api.model.EditableCellStyle;
import org.zkoss.zss.ui.AuxAction;
import org.zkoss.zss.ui.Spreadsheet;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.BankInfoDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.CreditReviewData;
import com.pennant.backend.model.finance.CreditReviewDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;
import com.pennant.backend.model.rmtmasters.ScoringSlab;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.spreadsheet.CustomerSalaried;
import com.pennant.backend.service.finance.ScoringDetailService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class FinanceSpreadSheetCtrl extends GFCBaseCtrl<CreditReviewData> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(FinanceSpreadSheetCtrl.class);

	protected Window window_SpreadSheetDialog;
	protected Button button_FetchData;
	protected Spreadsheet spreadSheet = null;
	protected Div spreadSheetDiv;

	// protected Listbox listBoxCustomerExternalLiability;
	private CreditReviewDetails creditReviewDetails = null;
	private CreditReviewData creditReviewData = null;
	private List<CustomerIncome> customerIncomeList = new ArrayList<CustomerIncome>();

	private Object financeMainDialogCtrl = null;
	private boolean isEditable;
	private Map<String, Object> dataMap = new HashMap<>();
	List<CustomerExtLiability> appExtLiabilities = new ArrayList<>();
	private boolean isReadOnly;
	private FinanceDetail financeDetail = null;
	private BigDecimal totalExposure = BigDecimal.ZERO;
	StringBuilder fields = new StringBuilder();
	private RuleService ruleService;
	private ScoringDetailService finScoringDetailService;

	/**
	 * default constructor.<br>
	 */
	public FinanceSpreadSheetCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinanceMainDialog";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Rule object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_SpreadSheetDialog(ForwardEvent event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SpreadSheetDialog);

		try {
			// READ OVERHANDED parameters !
			if (arguments.containsKey("creditReviewDetails")) {
				this.creditReviewDetails = (CreditReviewDetails) arguments.get("creditReviewDetails");
			}

			if (arguments.containsKey("creditReviewData") && arguments.get("creditReviewData") != null) {
				creditReviewData = (CreditReviewData) arguments.get("creditReviewData");
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				this.financeMainDialogCtrl = arguments.get("financeMainDialogCtrl");
			}

			if (arguments.containsKey("financeDetail")) {
				this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
			}

			if (arguments.containsKey("isEditable")) {
				isEditable = (boolean) arguments.get("isEditable");
			}

			if (arguments.containsKey("dataMap")) {
				dataMap = (Map<String, Object>) arguments.get("dataMap");
			}

			if (arguments.containsKey("externalLiabilities")) {
				appExtLiabilities = (List<CustomerExtLiability>) arguments.get("externalLiabilities");
			}

			if (arguments.containsKey("enqiryModule")) {
				isReadOnly = (boolean) arguments.get("enqiryModule");
			}

			if (this.creditReviewDetails != null) {
				doShowDialog();
			}

			// Set Spread Sheet Dialog Controller instance in base Controller
			if (getFinanceMainDialogCtrl() != null) {
				try {
					Class[] paramType = { this.getClass() };
					Object[] stringParameter = { this };
					if (financeMainDialogCtrl.getClass().getMethod("setFinanceSpreadSheetCtrl", paramType) != null) {
						financeMainDialogCtrl.getClass().getMethod("setFinanceSpreadSheetCtrl", paramType)
								.invoke(financeMainDialogCtrl, stringParameter);
					}
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_SpreadSheetDialog.onClose();
		}

		logger.debug("Leaving");
	}

	private File getFile(String fileName) {
		String reportSrc = PathUtil.getPath(PathUtil.REPORTS_FINANCIALS_SPREADSHEETS) + "/" + fileName;
		return new File(reportSrc);
	}

	public void doShowDialog() {
		logger.debug(Literal.ENTERING);

		try {
			// access components after calling super.doAfterCompose()
			Importer importer = Importers.getImporter();

			Book book = importer.imports(getFile(this.creditReviewDetails.getTemplateName()),
					this.creditReviewDetails.getTemplateName());

			// hidden the top row and column left
			spreadSheet.setHiderowhead(true);
			spreadSheet.setHidecolumnhead(true);
			spreadSheet.setBook(book);
			spreadSheet.setShowSheetbar(true);
			spreadSheet.enableBindingAnnotation();
			spreadSheet.disableUserAction(AuxAction.ADD_SHEET, true);
			spreadSheet.setPreloadRowSize(100);
			if (!enqiryModule) {
				doWriteBeanToComponents(creditReviewDetails, creditReviewData);
			} else if (enqiryModule) {
				renderCellsData(creditReviewDetails, creditReviewData);
			}
			getBorderLayoutHeight();
			this.window_SpreadSheetDialog.setHeight(this.borderLayoutHeight - 80 + "px");

		} catch (Exception e) {
			MessageUtil.showMessage(e.getMessage());
		}

		logger.debug(Literal.LEAVING);
	}

	// Default value "0" to be displayed for input fields numeric
	public void defaultValues$spreadSheet() {
		for (int colnum = 1; colnum <= 7; colnum++) {
			for (int rownum = 5; rownum <= 13; rownum++) {
				if (rownum % 2 != 0 && colnum % 2 != 0) {
					Range range = Ranges.range(spreadSheet.getSelectedSheet(), rownum, colnum);
					if (range.getCellValue() == null) {
						range.setCellValue(0);
					}
				}
			}
		}
	}

	public void doWriteBeanToComponents(CreditReviewDetails creditReviewDetails, CreditReviewData creditReviewData) {
		logger.debug(Literal.ENTERING);

		String fields = creditReviewDetails.getFieldKeys();
		if (StringUtils.isNotBlank(fields)) {
			String fieldsArray[] = fields.split(",");
			for (int i = 0; i < fieldsArray.length; i++) {
				Range range = Ranges.rangeByName(spreadSheet.getSelectedSheet(), fieldsArray[i]);
				range.setCellValue(dataMap.get(fieldsArray[i]) == null ? 0 : dataMap.get(fieldsArray[i]));
			}
		}

		String protectedCells = creditReviewDetails.getProtectedCells();
		if (StringUtils.isNoneBlank(protectedCells)) {
			String protectedCellsArray[] = protectedCells.split(",");
			for (String protectedCell : protectedCellsArray) {
				Range range = Ranges.rangeByName(spreadSheet.getSelectedSheet(), protectedCell);
				CellStyle cellStyle = range.getCellStyle();
				EditableCellStyle newStyle = range.getCellStyleHelper().createCellStyle(cellStyle);
				newStyle.setLocked(isEditable);
				range.setCellStyle(newStyle);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void renderCellsData(CreditReviewDetails creditReviewDetails, CreditReviewData creditReviewData) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> dataMap = new HashMap<>();
		if (creditReviewData != null) {
			dataMap = convertStringToMap(creditReviewData.getTemplateData());
			// doFillExternalLiabilities(appExtLiabilities, dataMap);
		}

		String fields = creditReviewDetails.getFields();

		if (StringUtils.isNotBlank(fields)) {
			String fieldsArray[] = fields.split(",");
			for (int i = 0; i < fieldsArray.length; i++) {
				Range range = Ranges.rangeByName(spreadSheet.getSelectedSheet(), fieldsArray[i]);
				range.setCellValue(dataMap.get(fieldsArray[i]) == null ? 0 : dataMap.get(fieldsArray[i]));
			}
		}

		String protectedCells = creditReviewDetails.getProtectedCells();
		if (StringUtils.isNoneBlank(protectedCells)) {
			String protectedCellsArray[] = protectedCells.split(",");
			for (String protectedCell : protectedCellsArray) {
				Range range = Ranges.rangeByName(spreadSheet.getSelectedSheet(), protectedCell);
				CellStyle cellStyle = range.getCellStyle();
				EditableCellStyle newStyle = range.getCellStyleHelper().createCellStyle(cellStyle);
				newStyle.setLocked(isEditable);
				range.setCellStyle(newStyle);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public static Map<String, Object> convertStringToMap(String payload) {
		logger.debug(Literal.ENTERING);

		ObjectMapper obj = new ObjectMapper();
		Map<String, Object> map = null;
		try {
			map = obj.readValue(payload, new TypeReference<HashMap<String, Object>>() {
			});
		} catch (JsonParseException e) {
			logger.debug(Literal.EXCEPTION, e);
		} catch (JsonMappingException e) {
			logger.debug(Literal.EXCEPTION, e);
		} catch (IOException e) {
			logger.debug(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);

		return map;
	}

	public void doWriteComponentstoBean() {
		logger.debug(Literal.ENTERING);

		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = null;

		String fields = creditReviewDetails.getFields();
		Map<String, Object> dataMap = new HashMap<String, Object>();

		if (StringUtils.isNotBlank(fields)) {
			String fieldsArray[] = fields.split(",");
			for (int i = 0; i < fieldsArray.length; i++) {
				Range range = Ranges.rangeByName(spreadSheet.getSelectedSheet(), fieldsArray[i]);
				dataMap.put(fieldsArray[i], range.getCellValue());
			}
		}

		try {
			saveConsideredObligations(dataMap);
			jsonInString = mapper.writeValueAsString(dataMap);
		} catch (Exception e) {
			logger.error("Exception in json request string" + e);
		}

		if (this.creditReviewData == null) {
			this.creditReviewData = new CreditReviewData();
			// this.creditReviewData.setNewRecord(true);
		}

		this.creditReviewData.setTemplateName(this.creditReviewDetails.getTemplateName());
		this.creditReviewData.setTemplateVersion(this.creditReviewDetails.getTemplateVersion());
		this.creditReviewData.setTemplateData(jsonInString);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$button_FetchData(Event event) {
		logger.debug(Literal.ENTERING);
		checkCreditRevData();
		logger.debug(Literal.LEAVING);
	}

	private void checkCreditRevData() {
		logger.debug(Literal.ENTERING);
		try {
			if (getFinanceMainDialogCtrl() instanceof FinanceMainBaseCtrl) {
				((FinanceMainBaseCtrl) getFinanceMainDialogCtrl()).isCreditReviewDataChanged(creditReviewDetails,
						false);
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void setDataToCells(CreditReviewDetails creditReviewDetails, Map<String, Object> dataMap) {
		logger.debug(Literal.ENTERING);

		String fields = creditReviewDetails.getFields();

		if (StringUtils.isNotBlank(fields)) {
			String fieldsArray[] = fields.split(",");
			for (int i = 0; i < fieldsArray.length; i++) {
				try {
					Range range = Ranges.rangeByName(spreadSheet.getSelectedSheet(), fieldsArray[i]);
					range.setCellValue(dataMap.get(fieldsArray[i]) == null ? 0 : dataMap.get(fieldsArray[i]));
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void setDataToCells(String fields, Map<String, BigDecimal> dataMap) {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isNotBlank(fields)) {
			String fieldsArray[] = fields.split(",");
			for (int i = 0; i < fieldsArray.length; i++) {
				Range range = Ranges.rangeByName(spreadSheet.getSelectedSheet(), fieldsArray[i]);
				range.setCellValue(dataMap.get(fieldsArray[i]) == null ? 0 : dataMap.get(fieldsArray[i]));
				if (dataMap.get(fieldsArray[i]) == null) {
					dataMap.put(fieldsArray[i], BigDecimal.ZERO);
				}
			}
		}

		logger.debug(Literal.LEAVING);
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

	private boolean checkIsDataChanged(Radiogroup userAction, boolean isFromLoan) {
		logger.debug(Literal.ENTERING);
		boolean isDataChanged = false;
		if (!(userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_RESUBMITTED)
				|| userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_REJECTED)
				|| userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_CANCELLED))) {
			if (getFinanceMainDialogCtrl() instanceof FinanceMainBaseCtrl) {
				isDataChanged = ((FinanceMainBaseCtrl) getFinanceMainDialogCtrl())
						.isCreditReviewDataChanged(creditReviewDetails, isFromLoan);
			}
			if (isDataChanged) {
				MessageUtil.showMessage("Loan details has changed.Eligibility needs to be verified.");
			}
		}
		logger.debug(Literal.LEAVING);
		return isDataChanged;
	}

	public void onClickToBeConsidered(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Checkbox checkBox = (Checkbox) event.getOrigin().getTarget();
		CustomerExtLiability custLiability = (CustomerExtLiability) checkBox.getAttribute("custExtLiability");
		if (checkBox.isChecked()) {
			this.totalExposure = this.totalExposure.add(PennantApplicationUtil
					.formateAmount(custLiability.getInstalmentAmount(), PennantConstants.defaultCCYDecPos));
		} else {
			this.totalExposure = this.totalExposure.subtract(PennantApplicationUtil
					.formateAmount(custLiability.getInstalmentAmount(), PennantConstants.defaultCCYDecPos));
		}

		if (financeMainDialogCtrl != null) {
			BigDecimal totalEmi = PennantApplicationUtil.unFormateAmount(totalExposure,
					PennantConstants.defaultCCYDecPos);
			try {
				financeMainDialogCtrl.getClass().getMethod("setTotalEmiConsideredObligations", BigDecimal.class)
						.invoke(financeMainDialogCtrl, totalEmi);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public Map<String, Object> saveConsideredObligations(Map<String, Object> dataMap) {
		logger.debug(Literal.ENTERING);

		/*
		 * if (this.listBoxCustomerExternalLiability.getItems().size() > 0) { for (int i = 0; i <
		 * listBoxCustomerExternalLiability.getItems().size(); i++) { Listitem listitem =
		 * listBoxCustomerExternalLiability.getItemAtIndex(i); Checkbox checkBox = (Checkbox) getComponent(listitem,
		 * "toBeConsidered"); CustomerExtLiability custExtLiability = (CustomerExtLiability) checkBox
		 * .getAttribute("custExtLiability"); if (checkBox.isChecked()) { String key =
		 * String.valueOf(custExtLiability.getLinkId()) .concat(String.valueOf(custExtLiability.getSeqNo()));
		 * dataMap.put(key, 1); } } }
		 */

		logger.debug(Literal.LEAVING);

		return dataMap;
	}

	private Component getComponent(Listitem listitem, String listcellId) {
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

	public void setSpreedSheetData(FinanceDetail aFinanceDetail) {
		logger.debug(Literal.ENTERING);

		try {
			ExtendedFieldRender extendedFieldRender = aFinanceDetail.getExtendedFieldRender();
			Map<String, Object> mapValues = extendedFieldRender.getMapValues();
			Range range = Ranges.rangeByName(spreadSheet.getBook().getSheet("Scorecard"), "RiskScore");
			if (range.getCellValue() != null) {
				if (mapValues.containsKey("RISKSCORE")) {
					mapValues.put("RISKSCORE", (double) range.getCellValue());
				}
			}
			extendedFieldRender.setMapValues(mapValues);
			aFinanceDetail.setExtendedFieldRender(extendedFieldRender);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	private BigDecimal getMaxMetricScore(String rule) {
		BigDecimal max = BigDecimal.ZERO;
		String[] codevalue = rule.split("Result");

		for (int i = 0; i < codevalue.length; i++) {
			if (i == 0) {
				continue;
			}

			if (codevalue[i] != null && codevalue[i].contains(";")) {
				String code = codevalue[i].substring(codevalue[i].indexOf('=') + 1, codevalue[i].indexOf(';'));

				if (code.contains("'")) {
					code = code.replace("'", "");
				}

				if (code.contains("?")) {
					String[] fields = code.split("[^a-zA-Z]+");

					HashMap<String, Object> fieldValuesMap = new HashMap<String, Object>();

					for (int j = 0; j < fields.length; j++) {
						if (!StringUtils.isEmpty(fields[j])) {
							fieldValuesMap.put(fields[j], BigDecimal.ONE);
						}
					}
					code = String.valueOf(RuleExecutionUtil.executeRule("Result = " + code + ";", fieldValuesMap, null,
							RuleReturnType.INTEGER)); // FIXME Code should be checked
				}

				if (new BigDecimal(code.trim()).compareTo(max) > 0) {
					max = new BigDecimal(code.trim());
				}
			}
		}

		return max;
	}

	private String getScrSlab(long refId, BigDecimal grpTotalScore, String execCreditWorth, boolean isRetail,
			List<ScoringSlab> scoringSlabList) throws InterruptedException {
		logger.debug("Entering");
		List<ScoringSlab> slabList = scoringSlabList;
		String creditWorth = "None";
		BigDecimal minScore = new BigDecimal(35);
		if (CollectionUtils.isNotEmpty(scoringSlabList)) {
			minScore = new BigDecimal(slabList.get(0).getLovDescMinScore());
		}
		List<Long> scoringValues = new ArrayList<>();

		for (ScoringSlab scoringSlab : slabList) {
			scoringValues.add(scoringSlab.getScoringSlab());
		}

		Collections.sort(scoringValues);

		if (slabList != null && !slabList.isEmpty()) {
			for (Long slab : scoringValues) {
				if (isRetail) {
					if (grpTotalScore.compareTo(minScore) >= 0 && grpTotalScore.compareTo(new BigDecimal(slab)) <= 0) {

						for (ScoringSlab scoringSlab : slabList) {
							if (slab.compareTo(scoringSlab.getScoringSlab()) == 0) {
								creditWorth = scoringSlab.getCreditWorthness();
							}
						}
						break;
					}
				}
			}
		} else if (StringUtils.isNotBlank(execCreditWorth)) {
			creditWorth = execCreditWorth;
		}

		logger.debug("Leaving");
		return creditWorth;
	}

	private Map<String, Object> executeScoringMetrics(CustomerDetails customerDetails,
			ExtendedFieldRender extendedFieldRender) {
		logger.debug(Literal.ENTERING);

		Customer customer = customerDetails.getCustomer();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("bonus_incentive", 0);
		dataMap.put("annual_Net_Sal", 0);
		dataMap.put("dpdL6M", 0);
		dataMap.put("dpdL12M", 0);
		dataMap.put("YearOfExp", 0);
		dataMap.put("splRule", "N");
		dataMap.put("custEmpSts", "");
		dataMap.put("custEmpsts", "");
		dataMap.put("custCity", "");
		if (StringUtils.equals(customer.getSubCategory(), PennantConstants.EMPLOYMENTTYPE_SALARIED)) {
			Rule rule = ruleService.getRuleById("LOSVARCA", "BRERULE", "BRERULE");
			Map<String, Object> mapValues = new HashMap<>();
			if (extendedFieldRender != null && extendedFieldRender.getMapValues() != null) {
				mapValues = extendedFieldRender.getMapValues();
			}

			Map<String, Object> resutlMap = new HashMap<String, Object>();
			if (rule != null) {

				if (!StringUtils.equals("", rule.getSPLRule())) {
					dataMap.put("splRule", "Y");
				}

				if (mapValues != null) {
					dataMap.put("bonus_incentive",
							mapValues.get("NET_BONUS") == null ? BigDecimal.ZERO
									: PennantApplicationUtil.formateAmount(
											new BigDecimal(mapValues.get("NET_BONUS").toString()),
											PennantConstants.defaultCCYDecPos));// DDE
					dataMap.put("annual_Net_Sal",
							mapValues.get("NET_ANNUAL") == null ? BigDecimal.ZERO
									: mapValues.get("NET_BONUS") == null ? BigDecimal.ZERO
											: PennantApplicationUtil.formateAmount(
													new BigDecimal(mapValues.get("NET_ANNUAL").toString()),
													PennantConstants.defaultCCYDecPos));// DDE
					dataMap.put("dpdL6M",
							mapValues.get("CIBIL_DPD60M") == null ? BigDecimal.ZERO : mapValues.get("CIBIL_DPD60M"));
					dataMap.put("dpdL12M",
							mapValues.get("CIBIL_DPDL12M") == null ? BigDecimal.ZERO : mapValues.get("CIBIL_DPDL12M"));
					if (mapValues.get("Score") != null && mapValues.get("Score").equals("000-1")) {
						dataMap.put("cibilScore", -1);
					} else {
						dataMap.put("cibilScore",
								mapValues.get("Score") == null ? BigDecimal.ZERO : mapValues.get("Score"));
					}
				}

				dataMap.put("monthCount_p", 0);
				dataMap.put("perfiosIncome", BigDecimal.ZERO);
				if (StringUtils.equals(customer.getSubCategory(), PennantConstants.EMPLOYMENTTYPE_SALARIED)) {
					if (CollectionUtils.isNotEmpty(customerDetails.getCustomerBankInfoList())) {
						int i = 1;
						int bankInfoSize = 0;
						int noOfMonths = 0;
						BigDecimal totalSalary = BigDecimal.ZERO;
						List<CustomerBankInfo> customerBankInfos = customerDetails.getCustomerBankInfoList();
						if (CollectionUtils.isNotEmpty(customerBankInfos)) {
							for (CustomerBankInfo customerBankInfo : customerBankInfos) {
								List<BankInfoDetail> bankInfoDetails = customerBankInfo.getBankInfoDetails();
								if (CollectionUtils.isNotEmpty(customerBankInfo.getBankInfoDetails())) {
									for (BankInfoDetail bankInfoDetail : bankInfoDetails) {
										dataMap.put("emi_" + i, PennantApplicationUtil.formateAmount(
												bankInfoDetail.getTotalEmi(), PennantConstants.defaultCCYDecPos));
										dataMap.put("sal_month_" + i, PennantApplicationUtil.formateAmount(
												bankInfoDetail.getTotalSalary(), PennantConstants.defaultCCYDecPos));
										dataMap.put("emiBounce_" + i, bankInfoDetail.getEmiBounceNo());
										dataMap.put("grossReceipts_" + i, BigDecimal.ZERO);

										if (bankInfoDetail.getTotalSalary().compareTo(BigDecimal.ZERO) > 0) {
											++noOfMonths;
											totalSalary = totalSalary.add(PennantApplicationUtil.formateAmount(
													bankInfoDetail.getTotalSalary(),
													PennantConstants.defaultCCYDecPos));
										}
										++i;
									}
									bankInfoSize = bankInfoSize + bankInfoDetails.size();
								}
							}
						}

						if (totalSalary.compareTo(BigDecimal.ZERO) > 0) {
							dataMap.put("perfiosIncome",
									totalSalary.divide(new BigDecimal(noOfMonths), RoundingMode.HALF_DOWN));
						}
						dataMap.put("monthCount_p", bankInfoSize);
					}
				}

				if (CollectionUtils.isNotEmpty(customerDetails.getCustomerIncomeList())) {
					List<CustomerIncome> customerIncomes = customerDetails.getCustomerIncomeList();
					for (CustomerIncome customerIncome : customerIncomes) {
						if (StringUtils.equals(customerIncome.getIncomeType(), "RENINC")) {
							dataMap.put("rental_income", PennantApplicationUtil
									.formateAmount(customerIncome.getIncome(), PennantConstants.defaultCCYDecPos));// DDE
						}
						if (StringUtils.equals(customerIncome.getIncomeType(), "INTINC")) {
							dataMap.put("interest_income", PennantApplicationUtil
									.formateAmount(customerIncome.getIncome(), PennantConstants.defaultCCYDecPos));// DDE
						}
					}
				}

				String custEmpType = "";
				if (CollectionUtils.isNotEmpty(customerDetails.getEmploymentDetailsList())) {
					List<CustomerEmploymentDetail> employmentList = customerDetails.getEmploymentDetailsList();
					for (CustomerEmploymentDetail customerEmploymentDetail : employmentList) {
						if (customerEmploymentDetail.isCurrentEmployer())
							custEmpType = StringUtils.trimToEmpty(customerEmploymentDetail.getCustEmpType());
					}
				}

				String custCity = "";

				if (CollectionUtils.isNotEmpty(customerDetails.getAddressList())) {
					List<CustomerAddres> customerAddressList = customerDetails.getAddressList();
					if (CollectionUtils.isNotEmpty(customerAddressList)) {
						for (CustomerAddres address : customerAddressList) {
							if (StringUtils.equals(PennantConstants.KYC_PRIORITY_VERY_HIGH,
									String.valueOf(address.getCustAddrPriority()))) {
								custCity = address.getCustAddrCity();
							}
						}
					}
				}

				dataMap.put("custEmpSts", custEmpType);
				dataMap.put("custEmpsts", custEmpType);
				dataMap.put("custCity", custCity);

				String result = (String) RuleExecutionUtil.executeRule(rule.getSQLRule(), dataMap,
						financeDetail.getFinScheduleData().getFinanceMain().getFinCcy(), RuleReturnType.CALCSTRING);

				// creditWorth = calculateScore(scoringMetrics, creditWorth, customer, dataMap, result);
				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(result.toString());
				resutlMap = convertStringToMap(json.toString());
			}
			// dataMapValues.put(creditWorth, dataMap);
			if (resutlMap != null)
				dataMap.putAll(resutlMap);
		}
		logger.debug(Literal.LEAVING);
		return dataMap;
	}

	private Map<String, Object> calculateScore(List<ScoringMetrics> scoringMetrics, Customer customer,
			Map<String, Object> dataMap) {

		String creditWorth = "None";
		Map<String, Object> mapValues = new HashMap<>();
		if (!dataMap.isEmpty()) {
			CustomerEligibilityCheck customerEligibilityCheck = new CustomerEligibilityCheck();
			BigDecimal totalGrpMaxScore = BigDecimal.ZERO;
			BigDecimal totalGrpExecScore = BigDecimal.ZERO;

			String custType = (String) dataMap.get("custType");

			if (StringUtils.isEmpty(custType)) {
				custType = "R";
			}

			if (customer.getCustDOB() != null) {
				int dobMonths = DateUtility.getMonthsBetween(customer.getCustDOB(), SysParamUtil.getAppDate());
				int months = dobMonths % 12;
				BigDecimal age = BigDecimal.ZERO;
				if (months <= 9) {
					age = new BigDecimal((dobMonths / 12) + ".0" + (dobMonths % 12));
				} else {
					age = new BigDecimal((dobMonths / 12) + "." + (dobMonths % 12));
				}
				customerEligibilityCheck.setCustAge(age);
			}

			// Get the slab based on the scoreGroupId
			List<ScoringSlab> scoringSlabList = finScoringDetailService
					.getScoringSlabsByScoreGrpId(scoringMetrics.get(0).getScoreGroupId(), "_AView");

			customerEligibilityCheck.setDataMap(dataMap);

			// Execute the Metrics
			scoringMetrics = finScoringDetailService.executeScoringMetrics(scoringMetrics, customerEligibilityCheck);
			try {
				for (ScoringMetrics scoringMetric : scoringMetrics) {

					dataMap.put(scoringMetric.getLovDescScoringCode(), scoringMetric.getLovDescExecutedScore());

					if (scoringMetric.getLovDescMetricMaxPoints() != null) {
						totalGrpMaxScore = totalGrpMaxScore.add(scoringMetric.getLovDescMetricMaxPoints());
					}
					if (scoringMetric.getLovDescExecutedScore() != null) {
						totalGrpExecScore = totalGrpExecScore.add(scoringMetric.getLovDescExecutedScore());
					}
				}

				// Get the Scoring Group
				creditWorth = getScrSlab(scoringMetrics.get(0).getScoreGroupId(), totalGrpExecScore, "", true,
						scoringSlabList);
				mapValues.put("CreditWorth", creditWorth);
				mapValues.put("RiskScore", totalGrpExecScore);
			} catch (Exception e) {
				logger.debug(Literal.EXCEPTION, e);
			}
		}
		return mapValues;
	}

	private void prepareFinacialDetailsForCustomer(CustomerDetails customerDetails, CustomerSalaried custSalaried) {
		List<CustomerIncome> customerIncomeList = customerDetails.getCustomerIncomeList();
		if (CollectionUtils.isNotEmpty(customerIncomeList)) {
			for (CustomerIncome customerIncome : customerIncomeList) {

				if (StringUtils.equals(customerIncome.getIncomeExpense(), PennantConstants.INCOME)) {

					if (StringUtils.equals(customerIncome.getCategory(), PennantConstants.INC_CATEGORY_SALARY)) {
						switch (customerIncome.getIncomeType()) {
						case "BS":
							custSalaried.setBs(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
									PennantConstants.defaultCCYDecPos));
							break;
						case "GP":
							custSalaried.setGp(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
									PennantConstants.defaultCCYDecPos));
							break;
						case "DA":
							custSalaried.setDa(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
									PennantConstants.defaultCCYDecPos));
							break;
						case "HRA":
							custSalaried.setHra(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
									PennantConstants.defaultCCYDecPos));
							break;
						case "CLA":
							custSalaried.setCla(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
									PennantConstants.defaultCCYDecPos));
						case "MA":
							custSalaried.setMa(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
									PennantConstants.defaultCCYDecPos));
							break;
						case "SA":
							custSalaried.setSa(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
									PennantConstants.defaultCCYDecPos));
							break;
						case "OA":
							custSalaried.setOa(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
									PennantConstants.defaultCCYDecPos));
							break;
						case "CV":
							custSalaried.setCv(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
									PennantConstants.defaultCCYDecPos));
							break;
						case "VP":
							custSalaried.setVp(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
									PennantConstants.defaultCCYDecPos));
						case "AO":
							custSalaried.setAo(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
									PennantConstants.defaultCCYDecPos));
							break;

						default:
							break;
						}
					} else if (StringUtils.equals(customerIncome.getCategory(), PennantConstants.INC_CATEGORY_OTHER)) {
						switch (customerIncome.getIncomeType()) {
						case "RENINC":
							custSalaried.setRenInc(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
									PennantConstants.defaultCCYDecPos));
							break;
						case "INTINC":
							custSalaried.setIntInc(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
									PennantConstants.defaultCCYDecPos));
							break;

						default:
							break;
						}
					}
				} else if (StringUtils.equals(customerIncome.getIncomeExpense(), PennantConstants.EXPENSE)
						&& StringUtils.equals(customerIncome.getCategory(), PennantConstants.INC_CATEGORY_SALARY)) {
					switch (customerIncome.getIncomeType()) {
					case "PF":
						custSalaried.setPf(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
								PennantConstants.defaultCCYDecPos));
						break;
					case "PPF":
						custSalaried.setPpf(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
								PennantConstants.defaultCCYDecPos));
						break;
					case "NPS":
						custSalaried.setNps(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
								PennantConstants.defaultCCYDecPos));
						break;
					case "IT":
						custSalaried.setIt(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
								PennantConstants.defaultCCYDecPos));
						break;
					case "EC":
						custSalaried.setEc(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
								PennantConstants.defaultCCYDecPos));
						break;
					case "LAPF":
						custSalaried.setLapf(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
								PennantConstants.defaultCCYDecPos));
						break;
					case "HLDS":
						custSalaried.setHlds(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
								PennantConstants.defaultCCYDecPos));
						break;
					case "PLDS":
						custSalaried.setPlds(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
								PennantConstants.defaultCCYDecPos));
						break;
					case "ALDS":
						custSalaried.setAlds(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
								PennantConstants.defaultCCYDecPos));
						break;
					case "ODDS":
						custSalaried.setOdds(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
								PennantConstants.defaultCCYDecPos));
						break;
					case "OLDS":
						custSalaried.setOlds(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
								PennantConstants.defaultCCYDecPos));
						break;
					case "IDS":
						custSalaried.setIds(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
								PennantConstants.defaultCCYDecPos));
						break;
					case "OD":
						custSalaried.setOd(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
								PennantConstants.defaultCCYDecPos));
						break;
					case "SADV":
						custSalaried.setSalAdv(PennantApplicationUtil.formateAmount(customerIncome.getIncome(),
								PennantConstants.defaultCCYDecPos));
						break;

					default:
						break;
					}

				}
			}
		}
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public CreditReviewDetails getCreditReviewDetails() {
		return creditReviewDetails;
	}

	public void setCreditReviewDetails(CreditReviewDetails creditReviewDetails) {
		this.creditReviewDetails = creditReviewDetails;
	}

	public CreditReviewData getCreditReviewData() {
		return creditReviewData;
	}

	public void setCreditReviewData(CreditReviewData creditReviewData) {
		this.creditReviewData = creditReviewData;
	}

	public BigDecimal getTotalExposure() {
		return totalExposure;
	}

	public void setTotalExposure(BigDecimal totalExposure) {
		this.totalExposure = totalExposure;
	}

	public List<CustomerIncome> getCustomerIncomeList() {
		return customerIncomeList;
	}

	public void setCustomerIncomeList(List<CustomerIncome> customerIncomeList) {
		this.customerIncomeList = customerIncomeList;
	}

	public void doSave_ScoreDetail(FinanceDetail aFinanceDetail) {
		// TODO Auto-generated method stub

	}

	@Autowired
	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	@Autowired
	public void setFinScoringDetailService(ScoringDetailService finScoringDetailService) {
		this.finScoringDetailService = finScoringDetailService;
	}

}
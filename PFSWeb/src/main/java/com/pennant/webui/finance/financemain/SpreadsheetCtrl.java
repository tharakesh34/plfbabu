package com.pennant.webui.finance.financemain;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Window;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pennant.app.util.PathUtil;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.CreditReviewData;
import com.pennant.backend.model.finance.CreditReviewDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class SpreadsheetCtrl extends GFCBaseCtrl<CreditReviewData> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(SpreadsheetCtrl.class);

	protected Window window_SpreadSheetDialog;
	protected Button button_FetchData;
	protected Spreadsheet spreadSheet = null;
	protected Div spreadSheetDiv;

	protected Listbox listBoxCustomerExternalLiability;
	private CreditReviewDetails creditReviewDetails = null;
	private CreditReviewData creditReviewData = null;
	private List<CustomerIncome> customerIncomeList = new ArrayList<CustomerIncome>();

	private Object financeMainDialogCtrl = null;
	private boolean isEditable;
	private Map<String, Object> btMap = new HashMap<>();
	List<CustomerExtLiability> appExtLiabilities = new ArrayList<>();
	private FinanceDetail financeDetail = null;
	private BigDecimal totalExposure = BigDecimal.ZERO;
	StringBuilder fields = new StringBuilder();

	/**
	 * default constructor.<br>
	 */
	public SpreadsheetCtrl() {
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

			if (arguments.containsKey("btMap")) {
				btMap = (Map<String, Object>) arguments.get("btMap");
			}

			if (arguments.containsKey("externalLiabilities")) {
				appExtLiabilities = (List<CustomerExtLiability>) arguments.get("externalLiabilities");
			}

			if (this.creditReviewDetails != null) {
				doShowDialog();
			}

			// Set Spread Sheet Dialog Controller instance in base Controller
			if (getFinanceMainDialogCtrl() != null) {
				try {
					Class[] paramType = { this.getClass() };
					Object[] stringParameter = { this };
					if (financeMainDialogCtrl.getClass().getMethod("setSpreadSheetCtrl", paramType) != null) {
						financeMainDialogCtrl.getClass().getMethod("setSpreadSheetCtrl", paramType)
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
			if (!("PREA".equals(creditReviewDetails.getEligibilityMethod())
					&& !(PennantConstants.List_Select.equals(creditReviewDetails.getFinCategory()))
					&& !(FinanceConstants.LOAN_CATEGORY_BT.equals(creditReviewDetails.getFinCategory()))
					&& (PennantConstants.EMPLOYMENTTYPE_SEP.equals(creditReviewDetails.getEmploymentType())
							|| PennantConstants.EMPLOYMENTTYPE_SENP.equals(creditReviewDetails.getEmploymentType())))) {

				// access components after calling super.doAfterCompose()
				Importer importer = Importers.getImporter();

				Book book = importer.imports(getFile(this.creditReviewDetails.getTemplateName()),
						this.creditReviewDetails.getTemplateName());

				// hidden the top row and column left
				spreadSheet.setHiderowhead(true);
				spreadSheet.setHidecolumnhead(true);
				spreadSheet.setBook(book);
				spreadSheet.setShowSheetbar(true);
				spreadSheet.setPreloadRowSize(100);
				spreadSheet.disableUserAction(AuxAction.ADD_SHEET, true);
				if (!enqiryModule) {
					doWriteBeanToComponents(creditReviewDetails, creditReviewData);
				} else if (enqiryModule) {
					renderCellsData(creditReviewDetails, creditReviewData);
				}
				getBorderLayoutHeight();
				this.window_SpreadSheetDialog.setHeight(this.borderLayoutHeight - 80 + "px");
			}
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

	public void doWriteBeanToComponents(CreditReviewDetails crdd, CreditReviewData crd) {
		logger.debug("Entering");

		Map<String, Object> dataMap = new HashMap<>();
		if (crd != null) {
			dataMap = convertStringToMap(crd.getTemplateData());

			doFillExternalLiabilities(appExtLiabilities, dataMap);

			dataMap.putAll(btMap);
			dataMap.put("Loan_Req", crdd.getTotalLoanAmount());
			dataMap.put("TENOR", crdd.getTenor());
			dataMap.put("ROI", crdd.getRoi());
			dataMap.put("ABB", crdd.getAvgBankBal());
			dataMap.put("SNCTNAMNT", crdd.getSanctionedAmt());
			dataMap.put("OTSTNDNGAMNT", crdd.getOutStandingLoanAmt());
			dataMap.put("CC_ACNTKIMIT", crdd.getAccountLimit());
			dataMap.put("LNAMNT_BT", crdd.getLoanAmount());
			dataMap.put("DSCR_PBDIT", btMap.get("DSCR_PBDIT"));
			dataMap.put("EMI_ALL_LOANS", btMap.get("EMI_ALL_LOANS"));
			dataMap.put("CRNTRATIO", btMap.get("CRNTRATIO"));
			dataMap.put("SP_DBTEQTRATIO", btMap.get("DEBTEQUITY"));
			dataMap.put("DSCR", btMap.get("DSCR_GF"));
			dataMap.put("ABB_EMI", crdd.getTotalAbb());
			dataMap.put("MARGINI", btMap.get("MARGINI"));
			dataMap.put("ANNUAL_TURNOVER", btMap.get("ANNUAL_TURNOVER"));

		} else {
			doFillExternalLiabilities(appExtLiabilities, dataMap);

			dataMap.putAll(btMap);
			dataMap.put("Loan_Req", crdd.getTotalLoanAmount());
			dataMap.put("TENOR", crdd.getTenor());
			dataMap.put("ROI", crdd.getRoi());
			dataMap.put("ABB", crdd.getAvgBankBal());
			dataMap.put("DSCR_PBDIT", btMap.get("DSCR_PBDIT"));
			dataMap.put("EMI_ALL_LOANS", btMap.get("EMI_ALL_LOANS"));
			dataMap.put("DSCR_GF", btMap.get("DSCR_GF"));
			dataMap.put("CRNTRATIO", btMap.get("CRNTRATIO"));
			dataMap.put("SP_DBTEQTRATIO", btMap.get("DEBTEQUITY"));
			dataMap.put("DSCR", btMap.get("DSCR_GF"));
			dataMap.put("ABB_EMI", crdd.getTotalAbb());
			dataMap.put("MARGINI", btMap.get("MARGINI"));
			dataMap.put("ANNUAL_TURNOVER", btMap.get("ANNUAL_TURNOVER"));
			dataMap.put("SNCTNAMNT", crdd.getSanctionedAmt());
			dataMap.put("OTSTNDNGAMNT", crdd.getOutStandingLoanAmt());
			dataMap.put("CC_ACNTKIMIT", crdd.getAccountLimit());
			dataMap.put("LNAMNT_BT", crdd.getLoanAmount());
		}

		String fields = crdd.getFields();

		if (StringUtils.isNotBlank(fields)) {
			String fieldsArray[] = fields.split(",");
			for (int i = 0; i < fieldsArray.length; i++) {
				Range range = Ranges.rangeByName(spreadSheet.getSelectedSheet(), fieldsArray[i]);
				if (fieldsArray[i].equals("MAXELGLOANAMOUNT")) {
					dataMap.put("MAXELGLOANAMOUNT", range.getCellValue().toString());
				} else {
					range.setCellValue(dataMap.get(fieldsArray[i]) == null ? 0 : dataMap.get(fieldsArray[i]));
				}

			}
		}

		String protectedCells = crdd.getProtectedCells();
		if (StringUtils.isNoneBlank(protectedCells)) {
			String protectedCellsArray[] = protectedCells.split(",");
			for (String protectedCell : protectedCellsArray) {
				protectedCell = StringUtils.trim(protectedCell);

				Range range = Ranges.rangeByName(spreadSheet.getSelectedSheet(), protectedCell);
				CellStyle cellStyle = range.getCellStyle();
				EditableCellStyle newStyle = range.getCellStyleHelper().createCellStyle(cellStyle);
				newStyle.setLocked(isEditable);
				range.setCellStyle(newStyle);
			}
		}

		financeDetail.setCreditRevDataMap(dataMap);
		logger.debug("Leaving");
	}

	private void renderCellsData(CreditReviewDetails creditReviewDetails, CreditReviewData creditReviewData) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> dataMap = new HashMap<>();
		if (creditReviewData != null) {
			dataMap = convertStringToMap(creditReviewData.getTemplateData());
			doFillExternalLiabilities(appExtLiabilities, dataMap);
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

	public void doWriteComponentstoBean(FinanceDetail aFinanceDetail) {
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
			setSpreedSheetData(dataMap, aFinanceDetail);
		} catch (Exception e) {
			logger.error("Exception in json request string" + e);
		}

		if (this.creditReviewData == null) {
			this.creditReviewData = new CreditReviewData();
		}

		this.creditReviewData.setTemplateName(this.creditReviewDetails.getTemplateName());
		this.creditReviewData.setTemplateVersion(this.creditReviewDetails.getTemplateVersion());
		this.creditReviewData.setTemplateData(jsonInString);

		logger.debug(Literal.LEAVING);
	}

	private void setSpreedSheetData(Map<String, Object> dataMap, FinanceDetail aFinanceDetail) {
		ExtendedFieldRender extendedFieldRender = aFinanceDetail.getExtendedFieldRender();
		Map<String, Object> mapValues = extendedFieldRender.getMapValues();
		Object elgAmount = dataMap.get("MAXELGLOANAMOUNT");

		if (elgAmount != null) {
			mapValues.put("SPRDSHEET", elgAmount.toString());
		} else {
			mapValues.put("SPRDSHEET", BigDecimal.ZERO);
		}

		extendedFieldRender.setMapValues(mapValues);
		aFinanceDetail.setExtendedFieldRender(extendedFieldRender);
	}

	public void onClick$button_FetchData(Event event) {
		logger.debug(Literal.ENTERING);
		checkCreditRevData();
		logger.debug(Literal.LEAVING);
	}

	private void checkCreditRevData() {
		logger.debug(Literal.ENTERING);
		try {
			// FIXME Murthy
			/*
			 * if (getFinanceMainDialogCtrl() instanceof FinanceMainBaseCtrl) { ((FinanceMainBaseCtrl)
			 * getFinanceMainDialogCtrl()).isCreditReviewDataChanged(creditReviewDetails, false); }
			 */
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

	public boolean doSave(Radiogroup userAction, boolean isFromLoan, FinanceDetail aFinanceDetail) {
		logger.debug(Literal.ENTERING);
		boolean isDataChanged = checkIsDataChanged(userAction, isFromLoan);
		if (!isDataChanged) {
			doWriteComponentstoBean(aFinanceDetail);

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
				/*
				 * FIXME Murthy isDataChanged = ((FinanceMainBaseCtrl) getFinanceMainDialogCtrl())
				 * .isCreditReviewDataChanged(creditReviewDetails, isFromLoan);
				 */
			}
			if (isDataChanged) {
				MessageUtil.showMessage("Loan details has changed.Eligibility needs to be verified.");
			}
		}
		logger.debug(Literal.LEAVING);
		return isDataChanged;
	}

	public void doFillExternalLiabilities(List<CustomerExtLiability> customerExtLiabilityDetails,
			Map<String, Object> dataMap) {
		logger.debug(Literal.ENTERING);

		this.listBoxCustomerExternalLiability.getItems().clear();
		if (CollectionUtils.isNotEmpty(customerExtLiabilityDetails)) {
			int i = 0;
			for (CustomerExtLiability custExtLiability : customerExtLiabilityDetails) {
				i++;
				Listitem item = new Listitem();
				Listcell lc;
				Checkbox toBeConsidered = null;

				if (custExtLiability.getFinDate() == null) {
					lc = new Listcell();
				} else {
					lc = new Listcell(DateUtil.formatToLongDate(custExtLiability.getFinDate()));
				}
				lc.setParent(item);

				lc = new Listcell(custExtLiability.getFinTypeDesc());
				lc.setParent(item);

				lc = new Listcell(custExtLiability.getLoanBankName());
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(custExtLiability.getOriginalAmount(),
						PennantConstants.defaultCCYDecPos));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(custExtLiability.getInstalmentAmount(),
						PennantConstants.defaultCCYDecPos));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(custExtLiability.getOutstandingBalance(),
						PennantConstants.defaultCCYDecPos));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				lc = new Listcell(custExtLiability.getCustStatusDesc());
				lc.setParent(item);

				lc = new Listcell();
				toBeConsidered = new Checkbox();
				lc.setId("toBeConsidered".concat(String.valueOf(i)));
				toBeConsidered.setAttribute("custExtLiability", custExtLiability);
				toBeConsidered.addForward("onClick", self, "onClickToBeConsidered");
				toBeConsidered.setDisabled(isEditable);
				lc.appendChild(toBeConsidered);
				lc.setParent(item);
				String key = String.valueOf(custExtLiability.getLinkId())
						.concat(String.valueOf(custExtLiability.getSeqNo()));
				if (dataMap != null && dataMap.containsKey(key)) {
					this.totalExposure = this.totalExposure.add(PennantApplicationUtil
							.formateAmount(custExtLiability.getInstalmentAmount(), PennantConstants.defaultCCYDecPos));
					toBeConsidered.setChecked(true);
				}

				lc = new Listcell(custExtLiability.getRecordStatus());
				lc.setParent(item);

				lc = new Listcell(PennantJavaUtil.getLabel(custExtLiability.getRecordType()));
				lc.setParent(item);

				item.setAttribute("data", custExtLiability);
				this.listBoxCustomerExternalLiability.appendChild(item);

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
		}
		logger.debug(Literal.LEAVING);
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

		if (this.listBoxCustomerExternalLiability.getItems().size() > 0) {
			for (int i = 0; i < listBoxCustomerExternalLiability.getItems().size(); i++) {
				Listitem listitem = listBoxCustomerExternalLiability.getItemAtIndex(i);
				Checkbox checkBox = (Checkbox) getComponent(listitem, "toBeConsidered");
				CustomerExtLiability custExtLiability = (CustomerExtLiability) checkBox
						.getAttribute("custExtLiability");
				if (checkBox.isChecked()) {
					String key = String.valueOf(custExtLiability.getLinkId())
							.concat(String.valueOf(custExtLiability.getSeqNo()));
					dataMap.put(key, 1);
				}
			}
		}

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

	public boolean doSave(Radiogroup userAction, boolean b) {
		// TODO Auto-generated method stub
		return false;
	}
}
package com.pennant.webui.finance.financemain;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
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

import com.pennant.app.util.PathUtil;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.finance.CreditReviewData;
import com.pennant.backend.model.finance.CreditReviewDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class FinanceSpreadSheetCtrl extends GFCBaseCtrl<CreditReviewData> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(FinanceSpreadSheetCtrl.class);

	protected Window window_SpreadSheetDialog;
	protected Button button_FetchData;
	protected Spreadsheet spreadSheet = null;
	protected Div spreadSheetDiv;

	//protected Listbox listBoxCustomerExternalLiability;
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
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_SpreadSheetDialog(ForwardEvent event) throws Exception {
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
				btMap = (Map<String, Object>) arguments.get("dataMap");
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
				range.setCellValue(btMap.get(fieldsArray[i]) == null ? 0 : btMap.get(fieldsArray[i]));
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
			//doFillExternalLiabilities(appExtLiabilities, dataMap);
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
		HashMap<String, Object> map = null;
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
			//this.creditReviewData.setNewRecord(true);
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

		/*if (this.listBoxCustomerExternalLiability.getItems().size() > 0) {
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
		}*/

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
}
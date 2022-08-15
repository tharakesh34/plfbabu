package com.pennant.webui.external.creditreview;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zss.api.Importer;
import org.zkoss.zss.api.Importers;
import org.zkoss.zss.api.Range;
import org.zkoss.zss.api.Ranges;
import org.zkoss.zss.api.model.Book;
import org.zkoss.zss.api.model.CellData;
import org.zkoss.zss.api.model.CellStyle;
import org.zkoss.zss.api.model.EditableCellStyle;
import org.zkoss.zss.api.model.Sheet;
import org.zkoss.zss.model.CellRegion;
import org.zkoss.zss.ui.AuxAction;
import org.zkoss.zss.ui.Spreadsheet;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Window;

import com.google.common.base.CharMatcher;
import com.penanttech.pff.model.external.bre.ApplicantOutElement;
import com.penanttech.pff.model.external.bre.BREService;
import com.penanttech.pff.model.external.bre.EligibilityApclOutElement;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.financemanagement.bankorcorpcreditreview.CreditReviewSummaryDAO;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.BankInfoDetail;
import com.pennant.backend.model.customermasters.BankInfoSubDetail;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.CreditReviewData;
import com.pennant.backend.model.finance.ExtBreDetails;
import com.pennant.backend.model.finance.ExtCreditReviewConfig;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.spreadsheet.SheetCopier;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class FinanceExtCreditReviewSpreadSheetCtrl extends GFCBaseCtrl<CreditReviewData> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(FinanceExtCreditReviewSpreadSheetCtrl.class);

	protected Window window_SpreadSheetDialog;
	protected Spreadsheet spreadSheet = null;

	private CreditReviewData creditReviewData = null;

	private Object financeMainDialogCtrl = null;
	private boolean isReadOnly;
	private FinanceDetail financeDetail = null;
	private CreditApplicationReviewService creditApplicationReviewService;
	private ExtCreditReviewConfig extCreditReviewConfig = null;
	private ExtBreDetails extBreDetails = null;
	StringBuilder fields = new StringBuilder();
	Map<String, String> cifs = new LinkedHashMap<>();
	Map<Long, ExtendedFieldRender> extFieldValues = new HashMap<>();
	@Autowired
	private CreditReviewSummaryDAO creditReviewSummaryDao;

	LinkedHashMap<String, Sheet> sepSheetsMap = new LinkedHashMap<>();
	private long auditYr;

	/**
	 * default constructor.<br>
	 */
	public FinanceExtCreditReviewSpreadSheetCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinanceMainDialog";
	}

	/**
	 * 
	 * @param event
	 */
	@SuppressWarnings({ "rawtypes" })
	public void onCreate$window_SpreadSheetDialog(ForwardEvent event) {
		logger.debug("Entering");
		// Set the page level components.
		setPageComponents(window_SpreadSheetDialog);

		try {
			// READ OVERHANDED parameters !
			if (arguments.containsKey("extCreditReviewConfig")) {
				this.extCreditReviewConfig = (ExtCreditReviewConfig) arguments.get("extCreditReviewConfig");
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				this.financeMainDialogCtrl = arguments.get("financeMainDialogCtrl");
			}

			if (arguments.containsKey("financeDetail")) {
				this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
			}

			if (arguments.containsKey("creditReviewData") && arguments.get("creditReviewData") != null) {
				creditReviewData = (CreditReviewData) arguments.get("creditReviewData");
			}

			if (arguments.containsKey("isReadOnly")) {
				isReadOnly = (boolean) arguments.get("isReadOnly");
			}

			if (arguments.containsKey("extBreDetails")) {
				this.extBreDetails = (ExtBreDetails) arguments.get("extBreDetails");
			}

			if (this.extCreditReviewConfig != null) {
				doShowDialog();
			}

			// Set Spread Sheet Dialog Controller instance in base Controller
			if (getFinanceMainDialogCtrl() != null) {
				try {
					Class[] paramType = { this.getClass() };
					Object[] stringParameter = { this };
					if (financeMainDialogCtrl.getClass().getMethod("setFinanceExtCreditReviewSpreadSheetCtrl",
							paramType) != null) {
						financeMainDialogCtrl.getClass()
								.getMethod("setFinanceExtCreditReviewSpreadSheetCtrl", paramType)
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
			Importer importer = Importers.getImporter();
			Book book = importer.imports(getFile(this.extCreditReviewConfig.getTemplateName()),
					this.extCreditReviewConfig.getTemplateName());

			doWriteBeanToComponents(creditReviewData, book);

			this.window_SpreadSheetDialog.setHeight(this.borderLayoutHeight - 80 + "px");

		} catch (Exception e) {
			MessageUtil.showMessage(e.getMessage());
		}

		logger.debug(Literal.LEAVING);
	}

	private void unProtectCells(String userEntryFields, Sheet sheet) {
		if (StringUtils.isNoneBlank(userEntryFields)) {
			String protectedCellsArray[] = userEntryFields.split(",");
			for (String protectedCell : protectedCellsArray) {
				Range cellRange = Ranges.range(sheet, protectedCell);
				CellStyle cellStyle = cellRange.getCellStyle();
				cellStyle.isLocked();
				EditableCellStyle newStyle = cellRange.getCellStyleHelper().createCellStyle(cellStyle);
				newStyle.setLocked(isReadOnly);
				cellRange.setCellStyle(newStyle);
			}
		}
	}

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

	public void doWriteBeanToComponents(CreditReviewData creditReviewData, Book book) {
		logger.debug(Literal.ENTERING);

		try {
			// Prepare MainApplicant Sheet
			if (!financeDetail.getCustomerDetails().getCustomer().getCustCIF().isEmpty()) {
				prepareMainApplicantSheet(book);
			}

			// Prepare JointApplicants Sheets
			List<JointAccountDetail> coapplicantsList = financeDetail.getJointAccountDetailList();
			for (JointAccountDetail jointAccountDetail : coapplicantsList) {
				prepareCoApplicantsSheets(jointAccountDetail.getCustomerDetails(), book);
			}

			// Prepare consolidated corp financials Sheet
			if (StringUtils.isNotEmpty(this.extCreditReviewConfig.getSepFields())) {
				prepareConsolidatedFinancialsSheet(book);
			}

			// Prepare consolidated obligations Sheet
			if (StringUtils.isNotEmpty(this.extCreditReviewConfig.getConsolidatedObligationsFields())) {
				prepareConsolidatedObligationsSheet(coapplicantsList, book);
			}

			// Prepare FinalEligibility Sheet
			if (StringUtils.isNotEmpty(this.extCreditReviewConfig.getFinalEligibilityFields())) {
				prepareFinalEligibilitySheet(coapplicantsList, book);
			}

		} catch (Exception e) {
			logger.debug(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public static Map<String, Object> convertStringToMap(String payload) {
		logger.debug(Literal.ENTERING);

		org.codehaus.jackson.map.ObjectMapper mapper = new org.codehaus.jackson.map.ObjectMapper();
		HashMap<String, Object> map = null;

		try {
			map = mapper.readValue(payload, new TypeReference<HashMap<String, Object>>() {
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void prepareFinalEligibilitySheet(List<JointAccountDetail> coApplicantsList, Book book) {
		logger.debug(Literal.ENTERING);
		List<String> al = new ArrayList<>();
		List<String> alFoir = new ArrayList<>();
		BigDecimal custObligations = BigDecimal.ZERO;
		Sheet finalEligibilitySheet = null;
		finalEligibilitySheet = book.getSheet("Final_Eligibility");

		// Set the properties to SpreadSheet
		spreadSheet.setHiderowhead(true);
		spreadSheet.setHidecolumnhead(true);
		spreadSheet.setBook(book);
		spreadSheet.setShowSheetbar(true);
		spreadSheet.enableBindingAnnotation();
		spreadSheet.disableUserAction(AuxAction.ADD_SHEET, true);
		spreadSheet.setMaxVisibleColumns(100);
		spreadSheet.setMaxVisibleRows(300);
		spreadSheet.setSelectedSheet("Final_Eligibility");

		// Unprotect Final Eligibility fields block
		String incomeUserEntryFields = this.extCreditReviewConfig.getFinalEligIncomeUserEntryFields();
		for (int i = 0; i <= coApplicantsList.size() + 1; i++) {
			incomeUserEntryFields = CharMatcher.BREAKING_WHITESPACE.removeFrom(incomeUserEntryFields);
			unProtectCells(incomeUserEntryFields, finalEligibilitySheet);
			incomeUserEntryFields = incomeUserEntryFields.replace(incomeUserEntryFields.charAt(0),
					(char) (incomeUserEntryFields.charAt(0) + 1));
		}
		unProtectCells(this.extCreditReviewConfig.getFinalEligLowLtvUserEntryFields(), finalEligibilitySheet);
		unProtectCells(this.extCreditReviewConfig.getFinalEligLRDUserEntryFields(), finalEligibilitySheet);
		unProtectCells(this.extCreditReviewConfig.getFinalEligSuperLowerEntryFields(), finalEligibilitySheet);
		unProtectCells(this.extCreditReviewConfig.getFinalEligSuperHigherEntryField(), finalEligibilitySheet);

		// populate Editable Cells Data
		Map<String, Object> dataMap = new HashMap<>();

		if (creditReviewData != null) {
			dataMap = convertStringToMap(creditReviewData.getTemplateData());
		}
		if (!dataMap.isEmpty()) {
			for (Entry<String, Object> entrySet : dataMap.entrySet()) {
				String key = entrySet.getKey();
				String str[] = key.split("!");
				String rows[] = str[1].split(":");
				Sheet sheet = spreadSheet.getBook().getSheet(str[0].replace("'", ""));
				Range range = Ranges.range(sheet, rows[0] + ":" + rows[1]);
				range.setCellValue(entrySet.getValue());
			}
		}

		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
		BREService response = null;
		try {

			response = mapper.readValue(this.extBreDetails.getResponse(), BREService.class);
		} catch (IOException e) {
			logger.debug("BRE respose is not present");
		} catch (NullPointerException e) {
			logger.debug("BRE respose is not present");
		}

		// Populate Income Details block
		String incomeDetailsRange = this.extCreditReviewConfig.getFinalEligIncomeDetailsFields();
		// Income foir fields
		String incomeFoirRange = this.extCreditReviewConfig.getFinalEligIncomeFoirFields();
		populateCifAndName(coApplicantsList, finalEligibilitySheet);// FIXME - Optimize
		if (ObjectUtils.isNotEmpty(response)) {

			List<ApplicantOutElement> applOutElmntList = response.getDaXMLDocument().getPchflOut().getApplicantOut()
					.getElement();
			for (ApplicantOutElement applicantOutElement : applOutElmntList) {
				if (ObjectUtils.isNotEmpty(applicantOutElement.getEligApclOut())) {

					if (StringUtils.equalsAnyIgnoreCase(applicantOutElement.getApplicantDetails().getCccid(),
							financeDetail.getCustomerDetails().getCustomer().getCustCIF())) {
						List<CustomerExtLiability> mainAppCustExtLiabList = financeDetail.getCustomerDetails()
								.getCustomerExtLiabilityList();
						for (CustomerExtLiability customerExtLiability : mainAppCustExtLiabList) {
							custObligations = custObligations.add(PennantApplicationUtil.formateAmount(
									customerExtLiability.getInstalmentAmount(), PennantConstants.defaultCCYDecPos));

						}
						al.add(financeDetail.getCustomerDetails().getCustomer().getCustShrtName());
						al.add(financeDetail.getCustomerDetails().getCustomer().getCustCIF());
						al.add(custObligations.toString());
					} else {
						for (JointAccountDetail jointAccountDetail : coApplicantsList) {
							String custCif = jointAccountDetail.getCustomerDetails().getCustomer().getCustCIF();
							if (custCif.equalsIgnoreCase(applicantOutElement.getApplicantDetails().getCccid())) {
								List<CustomerExtLiability> cpAppCustExtLiabList = jointAccountDetail
										.getCustomerDetails().getCustomerExtLiabilityList();
								for (CustomerExtLiability customerExtLiability : cpAppCustExtLiabList) {
									custObligations = custObligations.add(PennantApplicationUtil.formateAmount(
											customerExtLiability.getInstalmentAmount(),
											PennantConstants.defaultCCYDecPos));
								}
								al.add(jointAccountDetail.getCustomerDetails().getCustomer().getCustShrtName());
								al.add(custCif);
								al.add(custObligations.toString());
							}
						}
					}

					List<EligibilityApclOutElement> eligApclOutElmtList = applicantOutElement.getEligApclOut()
							.getEligApclOutElement();

					try {
						for (EligibilityApclOutElement eligibilityApclOutElement : eligApclOutElmtList) {
							if (ObjectUtils.isNotEmpty(eligibilityApclOutElement.getIncomeMethod())) {
								getEligibleIncome(al, eligibilityApclOutElement);
							}
						}

						incomeDetailsRange = CharMatcher.BREAKING_WHITESPACE.removeFrom(incomeDetailsRange);
						String incomeDetailsCells[] = incomeDetailsRange.split(",");

						for (int i = 0; i < incomeDetailsCells.length; i++) {
							Range range = Ranges.range(finalEligibilitySheet, incomeDetailsCells[i]);
							range.setCellValue(al.get(i));
						}
					} catch (Exception e) {
						logger.info("Exception occured while populating Final Eligbility income details block");
					}

					try {
						for (EligibilityApclOutElement eligibilityApclOutElement : eligApclOutElmtList) {
							if (ObjectUtils.isNotEmpty(eligibilityApclOutElement.getIncomeMethod())) {
								getEligibileFoir(alFoir, eligibilityApclOutElement);
							}
						}
						incomeFoirRange = CharMatcher.BREAKING_WHITESPACE.removeFrom(incomeFoirRange);
						String incomeFoirCells[] = incomeFoirRange.split(",");

						for (int i = 0; i < incomeFoirCells.length; i++) {
							Range range = Ranges.range(finalEligibilitySheet, incomeFoirCells[i]);
							range.setCellValue(alFoir.get(i));
						}

					} catch (Exception e) {
						logger.info("Exception occured while populating Final Eligbility Foir block");
					}

				}
				custObligations = BigDecimal.ZERO;
				al.clear();
				alFoir.clear();
				incomeDetailsRange = incomeDetailsRange.replace(incomeDetailsRange.charAt(0),
						(char) (incomeDetailsRange.charAt(0) + 1));
				incomeFoirRange = incomeFoirRange.replace(incomeFoirRange.charAt(0),
						(char) (incomeFoirRange.charAt(0) + 1));
			}
			al = null;
		}

		List<VASRecording> vasRecrsList = financeDetail.getFinScheduleData().getVasRecordingList();
		BigDecimal liVasAmtSum = BigDecimal.ZERO;
		BigDecimal giVasAmtSum = BigDecimal.ZERO;
		for (VASRecording vasRecording : vasRecrsList) {
			if (vasRecording.getProductCode().equalsIgnoreCase("LI"))
				liVasAmtSum = liVasAmtSum.add(vasRecording.getFee());
			else if (vasRecording.getProductCode().equalsIgnoreCase("GI"))
				giVasAmtSum = giVasAmtSum.add(vasRecording.getFee());
		}

		// Populate Final Eligibility block
		String finalEligFieldsRange = this.extCreditReviewConfig.getFinalEligibilityFields();
		finalEligFieldsRange = CharMatcher.BREAKING_WHITESPACE.removeFrom(finalEligFieldsRange);
		String finalEligCells[] = finalEligFieldsRange.split(",");
		List finalEligList = new ArrayList<>();
		if (ObjectUtils.isNotEmpty(response) && ObjectUtils
				.isNotEmpty(response.getDaXMLDocument().getPchflOut().getApplicationOut().getFinalEligibility())) {

			finalEligList.add(Double.parseDouble(response.getDaXMLDocument().getPchflOut().getApplicationOut()
					.getFinalEligibility().getConsideredMonthlyIncome()));
			finalEligList.add(Double.parseDouble(response.getDaXMLDocument().getPchflOut().getApplicationOut()
					.getFinalEligibility().getExistingMonthlyObligations()));
			finalEligList.add(Double.parseDouble(response.getDaXMLDocument().getPchflOut().getApplicationOut()
					.getFinalEligibility().getMaxPossibleEmi()));
			finalEligList.add(response.getDaXMLDocument().getPchflOut().getApplicationOut().getFinalEligibility()
					.getTenorAsPerNorms());
			finalEligList.add(financeDetail.getFinScheduleData().getFinanceMain().getNumberOfTerms());
			finalEligList.add(Double.parseDouble(response.getDaXMLDocument().getPchflOut().getApplicationOut()
					.getFinalEligibility().getEligibleLoan()));
			finalEligList.add(Double.parseDouble(
					response.getDaXMLDocument().getPchflOut().getApplicationOut().getFinalEligibility().getEmi()));
			finalEligList.add(Double.parseDouble(
					response.getDaXMLDocument().getPchflOut().getApplicationOut().getFinalEligibility().getEmiLiGi()));
			finalEligList.add(
					response.getDaXMLDocument().getPchflOut().getApplicationOut().getFinalEligibility().getFoirCalc());
			finalEligList.add(response.getDaXMLDocument().getPchflOut().getApplicationOut().getFinalEligibility()
					.getFoirCalcLigi());
			finalEligList.add(financeDetail.getFinScheduleData().getFinanceMain().getRepayProfitRate().doubleValue());
			BigDecimal childLoansRoi = BigDecimal.ZERO;
			BigDecimal childLoansSumAmt = BigDecimal.ZERO;
			if (ObjectUtils.isNotEmpty(financeDetail.getPricingDetail())) {
				List<FinanceMain> childFinList = financeDetail.getPricingDetail().getFinanceMains();

				for (FinanceMain childFin : childFinList) {
					childLoansRoi = childLoansRoi.add(childFin.getRepayProfitRate());
					childLoansSumAmt = childLoansSumAmt.add(childFin.getFinAssetValue());
				}
			}
			finalEligList.add(childLoansRoi.doubleValue());
			finalEligList.add(Double.parseDouble(response.getDaXMLDocument().getPchflOut().getApplicationOut()
					.getFinalEligibility().getFinalEligibleLoanBtHl()));
			finalEligList.add(Double.parseDouble(response.getDaXMLDocument().getPchflOut().getApplicationOut()
					.getFinalEligibility().getFinalEligibleLoanHeTopup()));
			finalEligList.add(PennantApplicationUtil
					.formateAmount((BigDecimal) financeDetail.getFinScheduleData().getFinanceMain().getFinAssetValue(),
							PennantConstants.defaultCCYDecPos)
					.doubleValue());
			finalEligList.add(PennantApplicationUtil
					.formateAmount((BigDecimal) childLoansSumAmt, PennantConstants.defaultCCYDecPos).doubleValue());
			finalEligList.add(PennantApplicationUtil
					.formateAmount((BigDecimal) liVasAmtSum, PennantConstants.defaultCCYDecPos).doubleValue());
			finalEligList.add(PennantApplicationUtil
					.formateAmount((BigDecimal) giVasAmtSum, PennantConstants.defaultCCYDecPos).doubleValue());
			finalEligList.add(Double.parseDouble(
					response.getDaXMLDocument().getPchflOut().getApplicationOut().getFinalEligibility().getEmiLi()));
			finalEligList.add(Double.parseDouble(
					response.getDaXMLDocument().getPchflOut().getApplicationOut().getFinalEligibility().getEmiGi()));
			finalEligList.add(Double.parseDouble(
					response.getDaXMLDocument().getPchflOut().getApplicationOut().getFinalEligibility().getEmiBtHl()));
			finalEligList.add(Double.parseDouble(response.getDaXMLDocument().getPchflOut().getApplicationOut()
					.getFinalEligibility().getEmiHeTopup()));

			for (int i = 0; i < finalEligCells.length; i++) {
				Range range = Ranges.range(finalEligibilitySheet, finalEligCells[i]);
				range.setCellValue(finalEligList.get(i));
			}
		}
		finalEligList = null;

		// Populate SuperLower block
		if (ObjectUtils.isNotEmpty(response) && ObjectUtils.isNotEmpty(
				response.getDaXMLDocument().getPchflOut().getApplicationOut().getFinalEligibility().getSuperLowEmi())) {
			String superLowerRange = this.extCreditReviewConfig.getFinalEligSuperLowerFields();
			String superLowerCells[] = superLowerRange.split(",");
			List superLowerList = new ArrayList<>();

			superLowerList.add(Double.parseDouble(response.getDaXMLDocument().getPchflOut().getApplicationOut()
					.getFinalEligibility().getSuperLowEmi().getFoir_insurance()));
			superLowerList.add(response.getDaXMLDocument().getPchflOut().getApplicationOut().getFinalEligibility()
					.getSuperLowEmi().getPerc_low_emi_possible());
			superLowerList.add(Double.parseDouble(response.getDaXMLDocument().getPchflOut().getApplicationOut()
					.getFinalEligibility().getSuperLowEmi().getProposed_loan_effective_emi()));
			superLowerList.add(response.getDaXMLDocument().getPchflOut().getApplicationOut().getFinalEligibility()
					.getSuperLowEmi().getPerc_change_emi_balance_60());
			superLowerList.add(Double.parseDouble(response.getDaXMLDocument().getPchflOut().getApplicationOut()
					.getFinalEligibility().getSuperLowEmi().getApplicable_emi_first_60()));
			superLowerList.add(Double.parseDouble(response.getDaXMLDocument().getPchflOut().getApplicationOut()
					.getFinalEligibility().getSuperLowEmi().getApplicable_emi_next_60()));
			superLowerList.add(Double.parseDouble(response.getDaXMLDocument().getPchflOut().getApplicationOut()
					.getFinalEligibility().getSuperLowEmi().getApplicable_emi_balance()));

			try {
				for (int i = 0; i < superLowerCells.length; i++) {
					Range range = Ranges.range(finalEligibilitySheet, superLowerCells[i]);
					range.setCellValue(superLowerList.get(i));
				}
			} catch (Exception e) {
				logger.info("Exception occured while populating final eligbility super lower block");
			}

			superLowerList = null;
		}

		// Populate SuperHigher block
		else if (ObjectUtils.isNotEmpty(response) && ObjectUtils.isNotEmpty(response.getDaXMLDocument().getPchflOut()
				.getApplicationOut().getFinalEligibility().getSuperHigherLoan())) {
			String superHigherRange = this.extCreditReviewConfig.getFinalEligSuperHigherFields();
			superHigherRange = CharMatcher.BREAKING_WHITESPACE.removeFrom(superHigherRange);
			String superHigherCells[] = superHigherRange.split(",");
			List<Double> superHigherList = new ArrayList<>();
			superHigherList.add(Double.parseDouble(response.getDaXMLDocument().getPchflOut().getApplicationOut()
					.getFinalEligibility().getSuperHigherLoan().getFoirInsurance()));
			superHigherList.add(Double.parseDouble(response.getDaXMLDocument().getPchflOut().getApplicationOut()
					.getFinalEligibility().getSuperHigherLoan().getPercIncreaseElig()));
			superHigherList.add(Double.parseDouble(response.getDaXMLDocument().getPchflOut().getApplicationOut()
					.getFinalEligibility().getSuperHigherLoan().getPossibleHigherLoan()));
			superHigherList.add(Double.parseDouble(response.getDaXMLDocument().getPchflOut().getApplicationOut()
					.getFinalEligibility().getSuperHigherLoan().getPercChangeEmiBalance()));
			superHigherList.add(Double.parseDouble(response.getDaXMLDocument().getPchflOut().getApplicationOut()
					.getFinalEligibility().getSuperHigherLoan().getApplicableEmiFirst60()));
			superHigherList.add(Double.parseDouble(response.getDaXMLDocument().getPchflOut().getApplicationOut()
					.getFinalEligibility().getSuperHigherLoan().getApplicableEmiNext60()));
			superHigherList.add(Double.parseDouble(response.getDaXMLDocument().getPchflOut().getApplicationOut()
					.getFinalEligibility().getSuperHigherLoan().getApplicableEmiBalance()));

			try {
				for (int i = 0; i < superHigherCells.length; i++) {
					Range range = Ranges.range(finalEligibilitySheet, superHigherCells[i]);
					range.setCellValue(superHigherList.get(i));
				}
			} catch (Exception e) {
				logger.info("Exception occured while populating final eligbility super higher block");
			}

			superHigherList = null;
		}

		// Populate Advantage block
		if (ObjectUtils.isNotEmpty(response) && ObjectUtils.isNotEmpty(
				response.getDaXMLDocument().getPchflOut().getApplicationOut().getFinalEligibility().getAdvantage())) {
			String advFieldsRange = this.extCreditReviewConfig.getFinalEligAdvantageFields();
			advFieldsRange = CharMatcher.BREAKING_WHITESPACE.removeFrom(advFieldsRange);
			String advantageCells[] = advFieldsRange.split(",");
			List<Double> advList = new ArrayList<>();
			advList.add(Double.parseDouble(response.getDaXMLDocument().getPchflOut().getApplicationOut()
					.getFinalEligibility().getAdvantage().getEligibleLoanAdvantage()));
			advList.add(Double.parseDouble(response.getDaXMLDocument().getPchflOut().getApplicationOut()
					.getFinalEligibility().getAdvantage().getCombinedLoanSanctioned()));
			advList.add(Double.parseDouble(response.getDaXMLDocument().getPchflOut().getApplicationOut()
					.getFinalEligibility().getAdvantage().getRoiAdvantage()));
			advList.add(Double.valueOf(financeDetail.getFinScheduleData().getFinanceMain().getNumberOfTerms()));
			List<String> emiAmtItems = response.getDaXMLDocument().getPchflOut().getApplicationOut()
					.getFinalEligibility().getAdvantage().getEmiAmount().getItem();
			List<String> monthsItems = response.getDaXMLDocument().getPchflOut().getApplicationOut()
					.getFinalEligibility().getAdvantage().getMonths().getItem();

			for (int i = 0; i <= emiAmtItems.size() || i <= monthsItems.size(); i++) {
				advList.add(Double.parseDouble(emiAmtItems.get(i)));
				advList.add(Double.parseDouble(monthsItems.get(i)));
			}

			try {
				for (int i = 0; i < advantageCells.length; i++) {
					Range range = Ranges.range(finalEligibilitySheet, advantageCells[i]);
					range.setCellValue(advList.get(i));
				}
			} catch (Exception e) {
				logger.info("Exception occured while populating final eligbility Advantage block");
			}
			advList = null;
		}

		logger.debug(Literal.LEAVING);
	}

	// Writing data into below list to populate customer name and cif even if the final eligibility sheet is blank.
	private void populateCifAndName(List<JointAccountDetail> coApplicantsList, Sheet finalEligibilitySheet) {
		logger.debug(Literal.ENTERING);
		String incomeCellsRange = this.extCreditReviewConfig.getFinalEligIncomeDetailsFields();
		incomeCellsRange = CharMatcher.BREAKING_WHITESPACE.removeFrom(incomeCellsRange);
		Range range;
		String incmCellRange[] = incomeCellsRange.split(",");

		range = Ranges.range(finalEligibilitySheet, incmCellRange[0]);
		range.setCellValue(financeDetail.getCustomerDetails().getCustomer().getCustShrtName());

		range = Ranges.range(finalEligibilitySheet, incmCellRange[1]);
		range.setCellValue(financeDetail.getCustomerDetails().getCustomer().getCustCIF());

		for (JointAccountDetail jointAccountDetail : coApplicantsList) {
			incomeCellsRange = incomeCellsRange.replace(incomeCellsRange.charAt(0),
					(char) (incomeCellsRange.charAt(0) + 1));
			String incomeCells[] = incomeCellsRange.split(",");

			range = Ranges.range(finalEligibilitySheet, incomeCells[0]);
			range.setCellValue(jointAccountDetail.getCustomerDetails().getCustomer().getCustShrtName());

			range = Ranges.range(finalEligibilitySheet, incomeCells[1]);
			range.setCellValue(jointAccountDetail.getCustomerDetails().getCustomer().getCustCIF());
		}

		logger.debug(Literal.LEAVING);
	}

	private void getEligibleIncome(List<String> al, EligibilityApclOutElement eligibilityApclOutElement) {
		switch (eligibilityApclOutElement.getIncomeMethod()) {
		case "GROSS SALARIED":
			al.add(eligibilityApclOutElement.getEligibleIncome());
			break;
		case "CASH PROFIT":
			al.add(eligibilityApclOutElement.getEligibleIncome());
			break;
		case "GROSS PROFIT":
			al.add(eligibilityApclOutElement.getEligibleIncome());
			break;
		case "INDUSTRY MARGIN":
			al.add(eligibilityApclOutElement.getEligibleIncome());
			break;
		case "GROSS RECEIPT":
			al.add(eligibilityApclOutElement.getEligibleIncome());
			break;
		case "LIP":
			al.add(eligibilityApclOutElement.getEligibleIncome());
			break;
		case "AVERAGE BANKING":
			al.add(eligibilityApclOutElement.getEligibleIncome());
			break;
		case "RTR PROGRAM":
			al.add(eligibilityApclOutElement.getEligibleIncome());
			break;
		default:
			break;
		}
	}

	private void getEligibileFoir(List<String> al, EligibilityApclOutElement eligibilityApclOutElement) {
		switch (eligibilityApclOutElement.getIncomeMethod()) {
		case "GROSS SALARIED":
			al.add(eligibilityApclOutElement.getFoirNorm());
			break;
		case "CASH PROFIT":
			al.add(eligibilityApclOutElement.getFoirNorm());
			break;
		case "GROSS PROFIT":
			al.add(eligibilityApclOutElement.getFoirNorm());
			break;
		case "INDUSTRY MARGIN":
			al.add(eligibilityApclOutElement.getFoirNorm());
			break;
		case "GROSS RECEIPT":
			al.add(eligibilityApclOutElement.getFoirNorm());
			break;
		case "LIP":
			al.add(eligibilityApclOutElement.getFoirNorm());
			break;
		case "AVERAGE BANKING":
			al.add(eligibilityApclOutElement.getFoirNorm());
			break;
		case "RTR PROGRAM":
			al.add(eligibilityApclOutElement.getFoirNorm());
			break;
		default:
			break;
		}
	}

	private void prepareConsolidatedFinancialsSheet(Book book) {
		logger.debug(Literal.ENTERING);

		Sheet consldtdFinancialssSheet = null;
		consldtdFinancialssSheet = book.getSheet("Consolidated Financials");
		Sheet consolFinancialsSheet = SheetCopier.clone("Consolidated_Financials", consldtdFinancialssSheet);

		// Set the properties to SpreadSheet
		spreadSheet.setHiderowhead(true);
		spreadSheet.setHidecolumnhead(true);
		spreadSheet.setBook(book);
		spreadSheet.setShowSheetbar(true);
		spreadSheet.enableBindingAnnotation();
		spreadSheet.disableUserAction(AuxAction.ADD_SHEET, true);
		spreadSheet.setMaxVisibleColumns(100);
		spreadSheet.setMaxVisibleRows(300);
		spreadSheet.setSelectedSheet("Consolidated_Financials");

		String sepCellsRange = this.extCreditReviewConfig.getSepFields();
		sepCellsRange = CharMatcher.BREAKING_WHITESPACE.removeFrom(sepCellsRange);

		if (auditYr != 0) {
			Range range0 = Ranges.range(consolFinancialsSheet, "C4");
			range0.setCellValue(
					"FY " + String.valueOf(auditYr).substring(2) + "-" + String.valueOf(auditYr + 1).substring(2));
			for (int i = 0; i <= 2; i++) {
				String sepFieldsCell[] = sepCellsRange.split(",");
				populateConsolidtdFinancialsSheet(consolFinancialsSheet, sepFieldsCell);
				sepCellsRange = sepCellsRange.replace(sepCellsRange.charAt(0), (char) (sepCellsRange.charAt(0) + 1));
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void populateConsolidtdFinancialsSheet(Sheet consolFinancialsSheet, String sepFieldsCell[]) {
		BigDecimal sumValue = BigDecimal.ZERO;
		for (int i = 0; i < sepFieldsCell.length; i++) {
			for (Entry<String, Sheet> entrySet : sepSheetsMap.entrySet()) {
				Sheet sepSheet = entrySet.getValue();
				Range range = Ranges.range(sepSheet, sepFieldsCell[i]);
				BigDecimal cellValue = getValueInBigDecimal(range.getCellData().getValue());
				sumValue = sumValue.add(cellValue);
			}
			Range consolFinancialsSheetRange = Ranges.range(consolFinancialsSheet, sepFieldsCell[i]);
			consolFinancialsSheetRange.setCellValue(sumValue);
			sumValue = BigDecimal.ZERO;
		}
	}

	public static BigDecimal getValueInBigDecimal(Object value) {
		BigDecimal ret = BigDecimal.ZERO;

		if (value != null) {
			if (value instanceof BigDecimal) {
				ret = (BigDecimal) value;
			} else if (value instanceof String) {
				ret = new BigDecimal((String) value);
			} else if (value instanceof BigInteger) {
				ret = new BigDecimal((BigInteger) value);
			} else if (value instanceof Number) {
				ret = new BigDecimal(((Number) value).doubleValue());
			} else {
				return ret;
			}
		}
		return ret;
	}

	private void prepareConsolidatedObligationsSheet(List<JointAccountDetail> coapplicantsList, Book book) {
		logger.debug(Literal.ENTERING);

		Sheet consldtdObligsSheet = null;
		consldtdObligsSheet = book.getSheet("Consolidated Obligations");
		Sheet consolObligSheet = SheetCopier.clone("Consolidated_Obligations", consldtdObligsSheet);

		// Set the properties to SpreadSheet
		spreadSheet.setHiderowhead(true);
		spreadSheet.setHidecolumnhead(true);
		spreadSheet.setBook(book);
		spreadSheet.setShowSheetbar(true);
		spreadSheet.enableBindingAnnotation();
		spreadSheet.disableUserAction(AuxAction.ADD_SHEET, true);
		spreadSheet.setMaxVisibleColumns(100);
		spreadSheet.setMaxVisibleRows(300);
		spreadSheet.setSelectedSheet("Consolidated_Obligations");

		ArrayList<CustomerExtLiability> custExtLiabList = new ArrayList<>();
		List<CustomerExtLiability> mainAppExtLiabList = financeDetail.getCustomerDetails()
				.getCustomerExtLiabilityList();

		for (CustomerExtLiability customerExtLiability : mainAppExtLiabList) {
			custExtLiabList.add(customerExtLiability);
		}

		for (JointAccountDetail jointAccountDetail : coapplicantsList) {
			List<CustomerExtLiability> coAppExLiabList = jointAccountDetail.getCustomerDetails()
					.getCustomerExtLiabilityList();
			for (CustomerExtLiability customerExtLiability : coAppExLiabList) {
				custExtLiabList.add(customerExtLiability);
			}
		}

		int counter = 1;
		List<Object> al = new ArrayList<>();
		String consldtdObligCellsRange = this.extCreditReviewConfig.getConsolidatedObligationsFields();

		for (CustomerExtLiability customerExtLiability : custExtLiabList) {
			if (StringUtils.equalsIgnoreCase("S", customerExtLiability.getFinStatus())) {
				continue;
			}
			al.add(counter);
			al.add(customerExtLiability.getCustShrtName());
			al.add(customerExtLiability.getLoanBankName());
			al.add(customerExtLiability.getLoanPurpose());
			al.add(PennantApplicationUtil.formateAmount(customerExtLiability.getOriginalAmount(),
					PennantConstants.defaultCCYDecPos));
			al.add(customerExtLiability.getTenure());
			al.add(customerExtLiability.getMob());
			al.add(customerExtLiability.getBalanceTenure());
			al.add(PennantApplicationUtil.formateAmount(customerExtLiability.getPrincipalOutstanding(),
					PennantConstants.defaultCCYDecPos));
			al.add(PennantApplicationUtil.formateAmount(customerExtLiability.getInstalmentAmount(),
					PennantConstants.defaultCCYDecPos));
			al.add(customerExtLiability.getFinStatus());
			al.add(PennantApplicationUtil.formateAmount(customerExtLiability.getRateOfInterest(),
					PennantConstants.defaultCCYDecPos));
			al.add(PennantStaticListUtil.getSourceInfoList().get(customerExtLiability.getSource()).getLabel());
			al.add(customerExtLiability.getSecurityDetails());
			al.add(customerExtLiability.getRepayBankName());
			al.add(customerExtLiability.getRepayFromAccNo());
			al.add(customerExtLiability.isConsideredBasedOnRTR());

			String consldtdObligCells[] = consldtdObligCellsRange.split(",");

			for (int i = 0; i < consldtdObligCells.length; i++) {
				Range range = Ranges.range(consolObligSheet, consldtdObligCells[i]);
				range.setCellValue(al.get(i));
			}
			char cr = consldtdObligCellsRange.charAt(1);
			Integer i = Integer.parseInt(String.valueOf(cr));
			Integer j = i + 1;
			String nextChar = j.toString();
			char nextCr = nextChar.charAt(0);

			consldtdObligCellsRange = consldtdObligCellsRange.replace(consldtdObligCellsRange.charAt(1), nextCr);
			al.clear();
			counter++;
		}
		custExtLiabList = null;
		al = null;
		logger.debug(Literal.LEAVING);
	}

	private void prepareCoApplicantsSheets(CustomerDetails custDetails, Book book) {
		logger.debug(Literal.ENTERING);
		String coApplicantCtg = custDetails.getCustomer().getCustCtgCode();
		String subCat = custDetails.getCustomer().getSubCategory();

		if (StringUtils.equalsIgnoreCase(coApplicantCtg, "RETAIL") && (StringUtils.equalsIgnoreCase(subCat, "SAL")
				|| StringUtils.equalsIgnoreCase(subCat, "NON-WORKING"))) {
			prepareSalSheet("C_", custDetails, book);

		} else if (StringUtils.equalsIgnoreCase(coApplicantCtg, "RETAIL")
				&& (StringUtils.equalsIgnoreCase(subCat, "SEP") || StringUtils.equalsIgnoreCase(subCat, "SENP"))) {
			prepareSepSheet("C_", custDetails, book);

		} else if (StringUtils.equalsIgnoreCase(coApplicantCtg, "CORP")) {
			prepareSepSheet("C_", custDetails, book);
		}
		logger.debug(Literal.LEAVING);
	}

	private void prepareMainApplicantSheet(Book book) {
		logger.debug(Literal.ENTERING);
		String applicantCtg = financeDetail.getCustomerDetails().getCustomer().getCustCtgCode();
		String subCat = financeDetail.getCustomerDetails().getCustomer().getSubCategory();

		if (StringUtils.equalsIgnoreCase(applicantCtg, "RETAIL") && (StringUtils.equalsIgnoreCase(subCat, "SALARIED")
				|| StringUtils.equalsIgnoreCase(subCat, "NON-WORKING"))) {
			prepareSalSheet("A_", financeDetail.getCustomerDetails(), book);

		} else if (StringUtils.equalsIgnoreCase(applicantCtg, "RETAIL")
				&& (StringUtils.equalsIgnoreCase(subCat, "SEP") || StringUtils.equalsIgnoreCase(subCat, "SENP"))) {
			prepareSepSheet("A_", financeDetail.getCustomerDetails(), book);

		} else if (StringUtils.equalsIgnoreCase(applicantCtg, "CORP")) {
			prepareSepSheet("A_", financeDetail.getCustomerDetails(), book);
		}
		logger.debug(Literal.LEAVING);
	}

	private void prepareSepSheet(String appType, CustomerDetails custDetails, Book book) {
		logger.debug(Literal.ENTERING);
		Sheet sheet = null;
		Sheet sepSheet = null;
		LinkedHashMap<String, Object> creditRevMap = new LinkedHashMap<>();
		sheet = book.getSheet("SEP");
		if (custDetails.getCustomer().getCustCtgCode().equalsIgnoreCase("RETAIL")) {
			sepSheet = SheetCopier.clone(
					appType + custDetails.getCustomer().getCustCtgCode() + "_"
							+ custDetails.getCustomer().getSubCategory() + "_" + custDetails.getCustomer().getCustCIF(),
					sheet);

		} else {
			sepSheet = SheetCopier.clone(
					appType + custDetails.getCustomer().getCustCtgCode() + "_" + custDetails.getCustomer().getCustCIF(),
					sheet);
		}

		sepSheetsMap.put(custDetails.getCustomer().getCustCIF(), sepSheet);

		// Set the properties to SpreadSheet
		spreadSheet.setHiderowhead(true);
		spreadSheet.setHidecolumnhead(true);
		spreadSheet.setBook(book);
		spreadSheet.setShowSheetbar(true);
		spreadSheet.enableBindingAnnotation();
		spreadSheet.disableUserAction(AuxAction.ADD_SHEET, true);
		spreadSheet.setMaxVisibleColumns(100);
		spreadSheet.setMaxVisibleRows(300);

		if (custDetails.getCustomer().getCustCtgCode().equalsIgnoreCase("RETAIL")) {
			spreadSheet.setSelectedSheet(appType + custDetails.getCustomer().getCustCtgCode() + "_"
					+ custDetails.getCustomer().getSubCategory() + "_" + custDetails.getCustomer().getCustCIF());
		} else {
			spreadSheet.setSelectedSheet(appType + custDetails.getCustomer().getCustCtgCode() + "_"
					+ custDetails.getCustomer().getCustCIF());
		}
		String sepCellsRange = this.extCreditReviewConfig.getSepFields().trim();
		sepCellsRange = CharMatcher.BREAKING_WHITESPACE.removeFrom(sepCellsRange);
		List<String> auditYearsList = creditReviewSummaryDao
				.getAuditYearsbyCustdId(custDetails.getCustomer().getCustID());
		List<Integer> auditYears = auditYearsList.stream().map(s -> Integer.parseInt(s)).collect(Collectors.toList());

		String finCrdtRevRange = sepCellsRange.replace(sepCellsRange.charAt(0), (char) (sepCellsRange.charAt(0) - 2));
		String finCrdtRevCells[] = finCrdtRevRange.split(",");

		Collections.sort(auditYears, Collections.reverseOrder());
		int count = 1;
		for (long auditYear : auditYears) {
			Range range = Ranges.range(sepSheet, "C4");
			auditYr = auditYear;
			range.setCellValue(
					"FY " + String.valueOf(auditYear).substring(2) + "-" + String.valueOf(auditYear + 1).substring(2));

			creditRevMap = creditReviewSummaryDao
					.getFinCreditRevSummaryByCustIdAndAdtYr(custDetails.getCustomer().getCustID(), auditYear);

			setCorpFinancialsData(sepSheet, sepCellsRange, finCrdtRevCells, creditRevMap);
			sepCellsRange = sepCellsRange.replace(sepCellsRange.charAt(0), (char) (sepCellsRange.charAt(0) + 1));
			count++;
			if (count == 4)
				break;
		}

		// Prepare Banking Info block
		String consldtdBnkCellsRange = this.extCreditReviewConfig.getConsolidatedBankingFields();
		consldtdBnkCellsRange = CharMatcher.BREAKING_WHITESPACE.removeFrom(consldtdBnkCellsRange);
		String bankInfoFieldsRange[] = consldtdBnkCellsRange.split("##");
		List<CustomerBankInfo> custBnkInfoList = custDetails.getCustomerBankInfoList();

		List<CustomerBankInfo> caAccountsList = new ArrayList<>();
		List<CustomerBankInfo> nonCaAccountsList = new ArrayList<>();
		List<CustomerBankInfo> allBanksAccounts = new ArrayList<>();

		for (CustomerBankInfo customerBankInfo : custBnkInfoList) {
			if (StringUtils.equalsIgnoreCase(customerBankInfo.getAccountType(), "CA")) {
				caAccountsList.add(customerBankInfo);
			} else {
				nonCaAccountsList.add(customerBankInfo);
			}
		}
		allBanksAccounts.addAll(caAccountsList);
		allBanksAccounts.addAll(nonCaAccountsList);
		int counter = 0;
		for (CustomerBankInfo customerBankInfo : allBanksAccounts) {
			if (counter == bankInfoFieldsRange.length) {
				break;
			}
			populateBankingInfoFields(sepSheet, bankInfoFieldsRange, counter, customerBankInfo);
			counter = counter + 1;
		}
		caAccountsList = null;
		nonCaAccountsList = null;
		allBanksAccounts = null;
		logger.debug(Literal.LEAVING);
	}

	private void populateBankingInfoFields(Sheet sepSheet, String[] bankInfoFieldsRange, int counter,
			CustomerBankInfo customerBankInfo) {
		String dayBalSum = "";
		List<String> al = new ArrayList<>();
		String accountCellsRange = bankInfoFieldsRange[counter];
		String accountHeaderRange[] = accountCellsRange.split("#");

		String accountHeaderFields = accountHeaderRange[0].toString();
		String accountHeaderCells[] = accountHeaderFields.split(",");
		List<String> headerItems = new ArrayList<>();

		headerItems.add(customerBankInfo.getAccountHolderName());
		headerItems.add(customerBankInfo.getLovDescBankName());
		headerItems.add(customerBankInfo.getLovDescAccountType());
		if (StringUtils.equalsIgnoreCase(customerBankInfo.getAccountType(), "CA"))
			headerItems.add("YES");
		else
			headerItems.add("NO");
		headerItems.add(PennantApplicationUtil
				.formateAmount((BigDecimal) customerBankInfo.getCcLimit(), PennantConstants.defaultCCYDecPos)
				.toString());
		headerItems.add("0");

		for (int i = 0; i < accountHeaderCells.length; i++) {
			Range range = Ranges.range(sepSheet, accountHeaderCells[i]);
			range.setCellValue(headerItems.get(i));
		}

		String bankInfoDays = SysParamUtil.getValueAsString("BANKINFO_DAYS");
		int days[] = Arrays.asList(bankInfoDays.split(",")).stream().mapToInt(Integer::parseInt).toArray();
		int monthsCounts = 0;
		List<BankInfoDetail> bankInfoList = customerBankInfo.getBankInfoDetails();
		for (BankInfoDetail bankInfoDetail : bankInfoList) {
			if (monthsCounts == 6)
				break;
			String accountCells[] = accountHeaderRange[1].split(",");
			al.clear();

			al.add(String.valueOf(bankInfoDetail.getCreditNo()));
			al.add(PennantApplicationUtil
					.formateAmount((BigDecimal) bankInfoDetail.getCreditAmt(), PennantConstants.defaultCCYDecPos)
					.toString());
			al.add(String.valueOf(bankInfoDetail.getDebitNo()));
			al.add(PennantApplicationUtil
					.formateAmount((BigDecimal) bankInfoDetail.getDebitAmt(), PennantConstants.defaultCCYDecPos)
					.toString());
			al.add(PennantApplicationUtil
					.formateAmount(getDayEndBalance(bankInfoDetail, days[0]), PennantConstants.defaultCCYDecPos)
					.toString());
			al.add(PennantApplicationUtil
					.formateAmount(getDayEndBalance(bankInfoDetail, days[1]), PennantConstants.defaultCCYDecPos)
					.toString());
			al.add(PennantApplicationUtil
					.formateAmount(getDayEndBalance(bankInfoDetail, days[2]), PennantConstants.defaultCCYDecPos)
					.toString());
			al.add(PennantApplicationUtil
					.formateAmount(getDayEndBalance(bankInfoDetail, days[3]), PennantConstants.defaultCCYDecPos)
					.toString());
			al.add(PennantApplicationUtil
					.formateAmount((BigDecimal) bankInfoDetail.getClosingBal(), PennantConstants.defaultCCYDecPos)
					.toString());
			al.add(String.valueOf(PennantApplicationUtil.formateAmount((BigDecimal) bankInfoDetail.getBounceIn(),
					PennantConstants.defaultCCYDecPos)));
			al.add(String.valueOf(PennantApplicationUtil.formateAmount((BigDecimal) bankInfoDetail.getBounceOut(),
					PennantConstants.defaultCCYDecPos)));

			dayBalSum = String.valueOf(getDayEndBalance(bankInfoDetail, days[0])
					.add(getDayEndBalance(bankInfoDetail, days[1])).add(getDayEndBalance(bankInfoDetail, days[2]))
					.add(getDayEndBalance(bankInfoDetail, days[3])));

			Double odccUtilization = Double.parseDouble(dayBalSum) / days.length;

			al.add(PennantApplicationUtil
					.formateAmount(BigDecimal.valueOf(odccUtilization), PennantConstants.defaultCCYDecPos).toString());
			al.add(PennantApplicationUtil
					.formateAmount((BigDecimal) bankInfoDetail.getoDCCLimit(), PennantConstants.defaultCCYDecPos)
					.toString());
			al.add(PennantApplicationUtil
					.formateAmount((BigDecimal) bankInfoDetail.getInterest(), PennantConstants.defaultCCYDecPos)
					.toString());
			al.add(PennantApplicationUtil
					.formateAmount((BigDecimal) bankInfoDetail.getTrf(), PennantConstants.defaultCCYDecPos).toString());

			for (int i = 0; i < accountCells.length; i++) {
				Range range = Ranges.range(sepSheet, accountCells[i]);
				range.setCellValue(al.get(i));
			}
			monthsCounts++;
			accountHeaderRange[1] = accountHeaderRange[1].replace(accountHeaderRange[1].charAt(0),
					(char) (accountHeaderRange[1].charAt(0) + 1));
		}
	}

	private BigDecimal getDayEndBalance(BankInfoDetail bankInfoDetail, int day) {
		BigDecimal dayEndBal = BigDecimal.ZERO;
		List<BankInfoSubDetail> bankInfoSubDetialList = bankInfoDetail.getBankInfoSubDetails();

		for (BankInfoSubDetail bankInfoSubDetail : bankInfoSubDetialList) {
			if (bankInfoSubDetail.getDay() == day) {
				dayEndBal = bankInfoSubDetail.getBalance();
				break;
			}
		}
		return dayEndBal;
	}

	private void setCorpFinancialsData(Sheet sepSheet, String sepCellsRange, String finCrdtRevCells[],
			Map<String, Object> creditRevMap) {
		String sepCells[] = sepCellsRange.split(",");

		for (int i = 0; i < sepCells.length; i++) {
			Range range = Ranges.range(sepSheet, sepCells[i]);
			Range range1 = Ranges.range(sepSheet, finCrdtRevCells[i]);
			range.setCellValue(PennantApplicationUtil.formateAmount(
					(BigDecimal) creditRevMap.get(range1.getCellValue()), PennantConstants.defaultCCYDecPos));
		}
	}

	private void prepareSalSheet(String appType, CustomerDetails custDetails, Book book) {
		logger.debug(Literal.ENTERING);
		Sheet sheet = null;
		sheet = book.getSheet("SAL");

		Sheet salSheet = SheetCopier.clone(appType + custDetails.getCustomer().getCustCtgCode() + "_"
				+ custDetails.getCustomer().getSubCategory() + "_" + custDetails.getCustomer().getCustCIF(), sheet);

		// Set the properties to SpreadSheet
		spreadSheet.setHiderowhead(true);
		spreadSheet.setHidecolumnhead(true);
		spreadSheet.setBook(book);
		spreadSheet.setShowSheetbar(true);
		spreadSheet.enableBindingAnnotation();
		spreadSheet.disableUserAction(AuxAction.ADD_SHEET, true);
		spreadSheet.setMaxVisibleColumns(100);
		spreadSheet.setMaxVisibleRows(300);
		spreadSheet.setSelectedSheet(appType + custDetails.getCustomer().getCustCtgCode() + "_"
				+ custDetails.getCustomer().getSubCategory() + "_" + custDetails.getCustomer().getCustCIF());

		HashMap<String, String> hMap = getsalariedFinancialsData(custDetails);
		String salCellsRange = this.extCreditReviewConfig.getSalFields();
		String salCells[] = salCellsRange.split(",");

		for (int i = 0; i < salCells.length; i++) {
			String str[] = salCells[i].split(":");
			Range range = Ranges.range(salSheet, str[1]);
			range.setCellValue(hMap.get(str[0]));
		}
		logger.debug(Literal.LEAVING);
	}

	private HashMap<String, String> getsalariedFinancialsData(CustomerDetails custDetails) {
		HashMap<String, String> hMap = new HashMap<>();
		List<CustomerIncome> custIncomeList = custDetails.getCustomerIncomeList();
		if (ObjectUtils.isNotEmpty(custIncomeList)) {
			for (CustomerIncome customerIncome : custIncomeList) {
				switch (customerIncome.getIncomeType()) {
				case "BASIC":
					hMap.put("BASIC",
							PennantApplicationUtil.formateAmount((BigDecimal) customerIncome.getCalculatedAmount(),
									PennantConstants.defaultCCYDecPos).toString());
					break;
				case "HRA":
					hMap.put("HRA",
							PennantApplicationUtil.formateAmount((BigDecimal) customerIncome.getCalculatedAmount(),
									PennantConstants.defaultCCYDecPos).toString());
					break;
				case "OTHFIX":
					hMap.put("OTHFIX",
							PennantApplicationUtil.formateAmount((BigDecimal) customerIncome.getCalculatedAmount(),
									PennantConstants.defaultCCYDecPos).toString());
					break;
				case "SPALLWNC":
					hMap.put("SPALLWNC",
							PennantApplicationUtil.formateAmount((BigDecimal) customerIncome.getCalculatedAmount(),
									PennantConstants.defaultCCYDecPos).toString());
					break;
				case "MONTH3":
					hMap.put("MONTH3",
							PennantApplicationUtil.formateAmount((BigDecimal) customerIncome.getCalculatedAmount(),
									PennantConstants.defaultCCYDecPos).toString());
					break;
				case "MONTH2":
					hMap.put("MONTH2",
							PennantApplicationUtil.formateAmount((BigDecimal) customerIncome.getCalculatedAmount(),
									PennantConstants.defaultCCYDecPos).toString());
					break;
				case "MONTH1":
					hMap.put("MONTH1",
							PennantApplicationUtil.formateAmount((BigDecimal) customerIncome.getCalculatedAmount(),
									PennantConstants.defaultCCYDecPos).toString());
					break;
				case "QUARTER2":
					hMap.put("QUARTER2",
							PennantApplicationUtil.formateAmount((BigDecimal) customerIncome.getCalculatedAmount(),
									PennantConstants.defaultCCYDecPos).toString());
					break;
				case "QUARTER1":
					hMap.put("QUARTER1",
							PennantApplicationUtil.formateAmount((BigDecimal) customerIncome.getCalculatedAmount(),
									PennantConstants.defaultCCYDecPos).toString());
					break;
				case "ANNUAL":
					hMap.put("ANNUAL",
							PennantApplicationUtil.formateAmount((BigDecimal) customerIncome.getCalculatedAmount(),
									PennantConstants.defaultCCYDecPos).toString());
					break;
				case "RENTINC":
					hMap.put("RENTINC",
							PennantApplicationUtil.formateAmount((BigDecimal) customerIncome.getCalculatedAmount(),
									PennantConstants.defaultCCYDecPos).toString());
					break;
				case "PREVINT":
					hMap.put("PREVINT",
							PennantApplicationUtil.formateAmount((BigDecimal) customerIncome.getCalculatedAmount(),
									PennantConstants.defaultCCYDecPos).toString());
					break;
				case "LATINT":
					hMap.put("LATINT",
							PennantApplicationUtil.formateAmount((BigDecimal) customerIncome.getCalculatedAmount(),
									PennantConstants.defaultCCYDecPos).toString());
					break;
				case "PREVDIV":
					hMap.put("PREVDIV",
							PennantApplicationUtil.formateAmount((BigDecimal) customerIncome.getCalculatedAmount(),
									PennantConstants.defaultCCYDecPos).toString());
					break;
				case "LATDIV":
					hMap.put("LATDIV",
							PennantApplicationUtil.formateAmount((BigDecimal) customerIncome.getCalculatedAmount(),
									PennantConstants.defaultCCYDecPos).toString());
					break;
				case "PREVCOMM":
					hMap.put("PREVCOMM",
							PennantApplicationUtil.formateAmount((BigDecimal) customerIncome.getCalculatedAmount(),
									PennantConstants.defaultCCYDecPos).toString());
					break;
				case "LATCOMM":
					hMap.put("LATCOMM",
							PennantApplicationUtil.formateAmount((BigDecimal) customerIncome.getCalculatedAmount(),
									PennantConstants.defaultCCYDecPos).toString());
					break;

				}
			}
		}
		return hMap;

	}

	public void doWriteComponentstoBean() {
		logger.debug(Literal.ENTERING);

		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = null;
		Map<String, Object> dataMap = new LinkedHashMap<String, Object>();

		// Saving final Eligibility sheet editable cells data.
		if (ObjectUtils.isNotEmpty(spreadSheet.getBook().getSheet("Final_Eligibility"))) {
			Sheet sheet = spreadSheet.getBook().getSheet("Final_Eligibility");
			Range range = Ranges.range(sheet);
			CellRegion data = range.getDataRegion();
			for (int i = 0; i <= data.lastRow; i++) {
				for (int j = 0; j <= data.lastColumn; j++) {
					Range range2 = Ranges.range(sheet, i, j);
					CellStyle cellStyle = range2.getCellStyle();
					CellData celldata = range2.getCellData();
					if (celldata != null && !cellStyle.isLocked() && !celldata.isFormula()) {
						dataMap.put(range2.toString(), celldata.getValue());
					}
					if (i == 7 && (j > 1 && j < 11)) {
						dataMap.put(range2.toString(), celldata.getValue());
					}
				}
			}
		}

		try {
			jsonInString = mapper.writeValueAsString(dataMap);

		} catch (Exception e) {
			logger.error("Exception in json request string" + e);
		}

		if (this.creditReviewData == null) {
			this.creditReviewData = new CreditReviewData();
		}

		this.creditReviewData.setTemplateName(this.extCreditReviewConfig.getTemplateName());
		this.creditReviewData.setTemplateData(jsonInString);

		logger.debug(Literal.LEAVING);
	}

	public boolean doSave(Radiogroup userAction, boolean isFromLoan) {
		logger.debug(Literal.ENTERING);

		if ((userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_RESUBMITTED)
				|| userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_REJECTED)
				|| userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_CANCELLED))) {
			return true;
		}

		try {
			doWriteComponentstoBean();

			if (StringUtils.isBlank(creditReviewData.getRecordType())) {
				creditReviewData.setVersion(creditReviewData.getVersion() + 1);
				creditReviewData.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				creditReviewData.setNewRecord(true);
			}
			creditReviewData.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			creditReviewData.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return false;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public ExtCreditReviewConfig getExtCreditReviewDetails() {
		return extCreditReviewConfig;
	}

	public void setExtCreditReviewDetails(ExtCreditReviewConfig extCreditReviewConfig) {
		this.extCreditReviewConfig = extCreditReviewConfig;
	}

	public CreditReviewData getCreditReviewData() {
		return creditReviewData;
	}

	public void setCreditReviewData(CreditReviewData creditReviewData) {
		this.creditReviewData = creditReviewData;
	}

	public CreditReviewSummaryDAO getCreditReviewSummaryDao() {
		return creditReviewSummaryDao;
	}

	public void setCreditReviewSummaryDao(CreditReviewSummaryDAO creditReviewSummaryDao) {
		this.creditReviewSummaryDao = creditReviewSummaryDao;
	}

	public CreditApplicationReviewService getCreditApplicationReviewService() {
		return creditApplicationReviewService;
	}

	public void setCreditApplicationReviewService(CreditApplicationReviewService creditApplicationReviewService) {
		this.creditApplicationReviewService = creditApplicationReviewService;
	}

}
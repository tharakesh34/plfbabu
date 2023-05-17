/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : WIFinanceTypeSelectListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 10-10-2011 * *
 * Modified Date : 10-10-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 10-10-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.financemanagement.bankorcorpcreditreview;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;
import com.pennant.backend.model.reports.CreditReviewMainCtgDetails;
import com.pennant.backend.model.reports.CreditReviewSubCtgDetails;
import com.pennant.backend.service.customermasters.FinCreditRevSubCategoryService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;
import com.pennant.backend.util.FacilityConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.interfacebajaj.fileextract.service.ExcelFileImport;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.notifications.service.NotificationService;

public class CorporateApplicationFinanceFileUploadDialogCtrl extends GFCBaseCtrl<Customer> {
	private static final long serialVersionUID = 3257569537441008225L;
	private static final Logger logger = LogManager.getLogger(CorporateApplicationFinanceFileUploadDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CorporateCreditRevFinanceFileUploadDialog; // autoWired
	protected Borderlayout borderLayout_FinanceTypeList; // autoWired
	protected Radiogroup custType;
	protected Radio custType_Existing;
	protected Radio custType_Prospect;
	protected Row customerRow;
	protected Longbox custID;
	protected Textbox lovDescCustCIF;
	protected Button btnSearchCustCIF;
	protected Label custShrtName;
	protected org.zkoss.zul.Row auditYearRow;
	protected Intbox auditYear;
	protected org.zkoss.zul.Row customerCategoryRow;
	protected Combobox custCategory;
	protected Radiogroup qualifiedUnQualified; // autowire
	protected Radio qualRadio; // autowire
	protected Radio unQualRadio; // autowire
	protected Textbox lovDescFinCcyName; // autowire
	protected Row auditPeriodRow;
	protected Combobox auditPeriod;
	protected Textbox bankName;
	protected Textbox auditors;
	protected Textbox location;
	protected Datebox auditedDate;
	protected Combobox auditType;
	protected ExtendedCombobox currencyType;
	protected Decimalbox conversionRate;
	protected Textbox documentName;
	protected Button btnUploadDoc;
	protected Textbox document; // autowired
	protected Radiogroup conSolOrUnConsol; // autowired
	protected Radio conSolidated; // autowired
	protected Radio unConsolidated;
	public List<CustomerDocument> customerDocumentList = new ArrayList<CustomerDocument>(); // autowired
	protected Textbox auditedYear; // autowired
	private ExcelFileImport fileImport = null;
	private Media media;
	protected Textbox txtFileName;
	protected Grid statusGrid;
	public List<Notes> notesList = new ArrayList<Notes>();
	protected Label label_CreditApplicationReviewDialog_BankName; // autowired

	@SuppressWarnings("unused")
	private transient CreditApplicationReviewDialogCtrl creditApplicationReviewDialogCtrl;
	private transient CreditApplicationReviewListCtrl creditApplicationReviewListCtrl;
	private transient FinCreditReviewDetails creditReviewDetail;
	private transient boolean validationOn;
	private Customer customer;
	private CreditApplicationReviewService creditApplicationReviewService;
	private FinCreditRevSubCategoryService finCreditRevSubCategoryService;
	private FinCreditReviewDetails creditReviewDetails;
	public List<FinCreditRevSubCategory> listOfFinCreditRevSubCategory = null;
	protected ExtendedCombobox custCIF; // autowired// overhanded per param
	protected Listitem duplicateItem;
	protected Grid grid_Basicdetails; // autoWired
	protected Space space_BankName; // autowired
	protected Groupbox gb_basicDetails; // autowired

	public List<FinCreditRevSubCategory> modifiedFinCreditRevSubCategoryList = new ArrayList<FinCreditRevSubCategory>();
	public List<CreditReviewSubCtgDetails> creditReviewSubCtgDetailsList = new ArrayList<CreditReviewSubCtgDetails>();
	private WIFCustomer wifcustomer = new WIFCustomer();
	int currentYear = DateUtil.getYear(SysParamUtil.getAppDate());
	private List<FinCreditReviewDetails> finCreditReviewDetailsList = null;
	List<Filter> filterList = null;
	private NotificationService notificationService;
	protected JdbcSearchObject<Customer> newSearchObject;
	CreditReviewMainCtgDetails creditReviewMainCtgDetails = new CreditReviewMainCtgDetails();
	int currFormatter = SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT);
	String amtFormat = PennantApplicationUtil.getAmountFormate(currFormatter);

	Date date = SysParamUtil.getAppDate();
	static boolean isError = false;

	Map<String, FinCreditReviewDetails> map = new HashMap<>();
	Set<String> plYearData = null;
	Set<String> bsYearData = null;
	ByteArrayInputStream inputStream = null;
	private List<String> auditYearsList = null;

	/**
	 * default constructor.<br>
	 */
	public CorporateApplicationFinanceFileUploadDialogCtrl() {
		super();
	}

	/**
	 * Before binding the data and calling the List window we check, if the ZUL-file is called with a parameter for a
	 * selected FinanceType object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CorporateCreditRevFinanceFileUploadDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		if (arguments.containsKey("creditApplicationReviewDialogCtrl")) {
			this.creditApplicationReviewDialogCtrl = (CreditApplicationReviewDialogCtrl) arguments
					.get("creditApplicationReviewDialogCtrl");
		} else {
			this.creditApplicationReviewDialogCtrl = null;
		}

		if (arguments.containsKey("creditApplicationReviewListCtrl")) {
			this.creditApplicationReviewListCtrl = (CreditApplicationReviewListCtrl) arguments
					.get("creditApplicationReviewListCtrl");
		} else {
			this.creditApplicationReviewListCtrl = null;
		}

		if (arguments.containsKey("aCreditReviewDetails")) {
			this.creditReviewDetail = (FinCreditReviewDetails) arguments.get("aCreditReviewDetails");
		} else {
			this.creditReviewDetail = null;
		}

		FinCreditReviewDetails befImage = new FinCreditReviewDetails();
		BeanUtils.copyProperties(this.creditReviewDetail, befImage);
		setCreditReviewDetail(creditReviewDetail);
		this.creditReviewDetail.setBefImage(befImage);

		doLoadWorkFlow(this.creditReviewDetail.isWorkflow(), this.creditReviewDetail.getWorkflowId(),
				this.creditReviewDetail.getNextTaskId());
		this.auditPeriod.setReadonly(true);
		this.auditPeriod.setDisabled(true);
		fillComboBox(auditPeriod, "12", PennantStaticListUtil.getPeriodList(), "");

		fillComboBox(auditType, "", PennantStaticListUtil.getCreditReviewAuditTypesList(), "");
		doSetFieldProperties();
		this.lovDescCustCIF.setVisible(true);
		this.window_CorporateCreditRevFinanceFileUploadDialog.doModal();

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		this.lovDescCustCIF.clearErrorMessage();
		onload();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.custID.setMaxlength(8);
		this.bankName.setMaxlength(50);
		this.auditors.setMaxlength(100);
		this.location.setMaxlength(50);
		this.auditType.setMaxlength(20);
		this.auditedDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.currencyType.setMaxlength(3);
		this.currencyType.setMandatoryStyle(true);
		this.currencyType.setModuleName("Currency");
		this.currencyType.setValueColumn("CcyCode");
		this.currencyType.setDescColumn("CcyDesc");
		this.currencyType.setValidateColumns(new String[] { "CcyCode" });
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		if (!this.auditedDate.isReadonly()) {
			this.auditedDate.setConstraint(new PTDateValidator(
					Labels.getLabel("label_CreditApplicationReviewDialog_AuditedDate.value"), true, null, true, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * To load the customerSelect filter dialog
	 * 
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void onload() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("filtersList", getFilterList());
		map.put("searchObject", getSearchObj());

		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}

	private List<Filter> getFilterList() {
		filterList = new ArrayList<Filter>();
		filterList.add(new Filter("lovDescCustCtgType", new String[] { PennantConstants.PFF_CUSTCTG_CORP,
				PennantConstants.PFF_CUSTCTG_SME, PennantConstants.PFF_CUSTCTG_INDIV }, Filter.OP_IN));
		return filterList;
	}

	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug("Entering");
		Customer aCustomer = (Customer) nCustomer;
		if (aCustomer != null) {
			this.custID.setValue(aCustomer.getCustID());
			this.lovDescCustCIF.setValue(aCustomer.getCustCIF());
			this.custShrtName.setValue(aCustomer.getCustShrtName());
			BeanUtils.copyProperties(aCustomer, wifcustomer);
		}
		logger.debug("Leaving");
	}

	public JdbcSearchObject<Customer> getSearchObj() {

		newSearchObject = new JdbcSearchObject<Customer>(Customer.class, getListRows());
		newSearchObject.addTabelName("Customers_AView");
		if (filterList != null && filterList.size() > 0) {
			for (int k = 0; k < filterList.size(); k++) {
				newSearchObject.addFilter(filterList.get(k));
			}
		}
		return this.newSearchObject;
	}

	/**
	 * To set the customer id from Customer filter
	 * 
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void onChange$lovDescCustCIF(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		this.lovDescCustCIF.clearErrorMessage();
		customer = (Customer) PennantAppUtil.getCustomerObject(this.lovDescCustCIF.getValue(), getFilterList());

		if (customer != null) {
			this.custID.setValue(customer.getCustID());
			this.lovDescCustCIF.setValue(String.valueOf(customer.getCustCIF()));
			this.custShrtName.setValue(customer.getCustShrtName());
			BeanUtils.copyProperties(customer, wifcustomer);
		} else {
			if (!"".equals(this.lovDescCustCIF.getValue())) {
				this.custShrtName.setValue("");
				this.custID.setValue(Long.valueOf(0));
				throw new WrongValueException(this.lovDescCustCIF, Labels.getLabel("FIELD_NO_INVALID",
						new String[] { Labels.getLabel("label_CreditRevSelectCategory_CustomerCIF.value") }));
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onCheck$custType(Event event) {
		if (this.custType.getSelectedItem() != null) {
			if (this.custType.getSelectedIndex() == 0) {
				this.lovDescCustCIF.setVisible(true);
				this.lovDescCustCIF.setValue("");
				this.custShrtName.setValue("");
			} else {
				this.lovDescCustCIF.setValue("");
				this.btnSearchCustCIF.setVisible(true);
			}
		}
	}

	public void onUploadDocumentValidation() {
		FinCreditReviewDetails aCreditReviewDetails = new FinCreditReviewDetails();
		List<WrongValueException> wveList = new ArrayList<WrongValueException>();
		try {
			if ((this.documentName.getValue() == null || StringUtils.isEmpty(this.documentName.getValue()))) {
				throw new WrongValueException(this.documentName, Labels.getLabel("MUST_BE_UPLOADED",
						new String[] { Labels.getLabel("label_CreditRevSelectCategory_CorporateDoc.value") }));
			}
			aCreditReviewDetails.setDocument(this.documentName.getValue());
		} catch (WrongValueException wve) {
			wveList.add(wve);
		}
		if (wveList.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wveList.size()];
			for (int i = 0; i < wveList.size(); i++) {
				wvea[i] = (WrongValueException) wveList.get(i);
			}
			throw new WrongValuesException(wvea);
		}

	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnProceed(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		FinCreditReviewDetails aCreditReviewDetails = new FinCreditReviewDetails();
		BeanUtils.copyProperties(this.creditReviewDetail, aCreditReviewDetails);
		// *************************************************************
		// force validation, if on, than execute by component.getValue()
		// *************************************************************
		doWriteComponentsToBean(aCreditReviewDetails);
		onUploadDocumentValidation();
		excelSave();
		this.window_CorporateCreditRevFinanceFileUploadDialog.onClose();
		refreshList();
		logger.debug("Leaving" + event.toString());
	}

	// set the workflow details
	private void workFlowDetails(FinCreditReviewDetails aCreditReviewDetails) {
		logger.debug(Literal.ENTERING);
		aCreditReviewDetails.setWorkflowId(this.creditReviewDetail.getWorkflowId());
		aCreditReviewDetails.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
		aCreditReviewDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		aCreditReviewDetails.setWorkflowId(this.creditReviewDetail.getWorkflowId());
		aCreditReviewDetails.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCreditReviewDetails.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCreditReviewDetails.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";

			nextTaskId = taskId + ";";
			nextTaskId = StringUtils.trimToEmpty(aCreditReviewDetails.getNextTaskId());

			nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			if ("".equals(nextTaskId)) {
				nextTaskId = getNextTaskIds(taskId, aCreditReviewDetails);
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

			aCreditReviewDetails.setTaskId(taskId);
			aCreditReviewDetails.setNextTaskId(nextTaskId);
			aCreditReviewDetails.setRoleCode(getRole());
			aCreditReviewDetails.setNextRoleCode(nextRoleCode);

		}
		logger.debug(Literal.LEAVING);
	}

	// for getting creditApplication review dialog
	public void getCreditApplicationRevDialog() {

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("creditReviewDetails", creditReviewDetail);
		/*
		 * we can additionally handed over the listBox or the controller self, so we have in the dialog access to the
		 * listBox ListModel. This is fine for synchronizing the data in the CreditReviewDetailsListbox from the dialog
		 * when we do a delete, edit or insert a FinCreditReviewDetails.
		 */
		map.put("creditApplicationReviewListCtrl", creditApplicationReviewListCtrl);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/FinanceManagement/BankOrCorpCreditReview/CreditApplicationReviewDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	/**
	 * Method for refreshing the list in ListCtrl
	 */
	protected void refreshList() {
		getCreditApplicationReviewListCtrl().search();
	}

	// method for uploading documnet and setting path to textbox and validation
	// for Excel file
	// accepts only excel (XLS,XLSX) file formats only
	public void onUpload$btnUploadDoc(UploadEvent event) throws Exception {
		logger.debug(Literal.ENTERING);
		FinCreditReviewDetails aCreditReviewDetails = new FinCreditReviewDetails();
		BeanUtils.copyProperties(this.creditReviewDetail, aCreditReviewDetails);
		doWriteComponentsToBean(aCreditReviewDetails);
		this.fileImport = null;
		media = event.getMedia();

		if (!MediaUtil.isExcel(media)) {
			MessageUtil.showError(Labels.getLabel("upload_document_invalid", new String[] { "excel" }));
			return;
		}

		File myFile = new File(media.getName());
		String fileName = myFile.getCanonicalPath();
		try {
			String filePath = SysParamUtil.getValueAsString("UPLOAD_FILEPATH");
			this.documentName.setText(fileName);
			this.documentName.setValue(fileName);
			this.fileImport = new ExcelFileImport(media, filePath);

			finCreditReviewDetailsList = getCreditApplicationReviewService()
					.getFinCreditRevDetailsByCustomerId(customer.getCustID(), "_View");

			setListDetails();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);

	}

	/**
	 * entry point of program, reading whole excel.
	 * 
	 * @return String
	 * @throws Exception
	 */
	/**
	 * @return
	 * @throws Exception
	 */
	private Map<String, Map<String, List<Object[]>>> loadExcelLines() throws Exception {
		logger.debug(Literal.ENTERING);
		Map<String, Map<String, List<Object[]>>> mapValues = new HashMap<>();

		inputStream = new ByteArrayInputStream(media.getByteData());

		try (HSSFWorkbook workBook = new HSSFWorkbook(inputStream);) {

			for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
				HSSFSheet sheet = workBook.getSheetAt(i);
				String sheetName = workBook.getSheetName(i);
				if (!("P&L".equalsIgnoreCase(sheetName.toString())) && (!("BS".equalsIgnoreCase(sheetName.toString())))
						&& !("Assets".equalsIgnoreCase(sheetName.toString()))) {
					MessageUtil.showMessage(sheetName + " is Invalid");
					documentName.setValue("");
					break;
				}
				Map<String, List<Object[]>> sheetDataMap = readSheet(sheet, i);
				if (sheetDataMap != null) {
					mapValues.put(sheetName, sheetDataMap);
				} else {
					return null;
				}

			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return mapValues;
	}

	// method for reading sheets
	private boolean validateHeaderRow(Row headerRow, String sheetName) {
		logger.debug(Literal.ENTERING);
		boolean duplicate = false;
		int minColumns = headerRow.getLastCellNum();
		if (minColumns < 5) {
			MessageUtil.showMessage("Minimum Header Columns are not available , invalid File uploaded");
			documentName.setValue("");
			return true;
		} else {
			for (int k = 0; k < headerRow.getLastCellNum(); k++) {
				String readCell = null;
				String readYearCell = null;
				if (k < 4) {
					readCell = headerRow.getCell(k).getStringCellValue();
				} else {
					Cell rycell = headerRow.getCell(k);
					if (rycell != null) {
						readYearCell = rycell.toString();
					}
				}
				if (k == 0)
					if (!"ID".equalsIgnoreCase(readCell)) {
						MessageUtil.showMessage(readCell + " Invalid column Header, Please upload valid data");
						documentName.setValue("");
						return true;
					}
				if (k == 1) {
					if (!"Description".equalsIgnoreCase(readCell)) {
						MessageUtil.showMessage(readCell + " Invalid column Header, Please upload valid data");
						documentName.setValue("");
						return true;
					}
				}
				if (k == 3) {
					if (!"Type".equalsIgnoreCase(readCell)) {
						MessageUtil.showMessage(readCell + " Invalid column Header, Please upload valid data");
						documentName.setValue("");
						return true;
					}
				}
				if (k >= 4) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(SysParamUtil.getAppDate());
					double year = cal.get(Calendar.YEAR);
					plYearData = new HashSet<String>();
					bsYearData = new HashSet<String>();
					if (StringUtils.equalsIgnoreCase("P&L", sheetName)) {
						if (plYearData.contains(readYearCell)) {
							MessageUtil.showMessage(
									readYearCell.substring(0, 4) + " Year having duplicates in 'P&L' Sheet");
							plYearData.clear();
							documentName.setValue("");
							return true;
						}
						plYearData.add((readYearCell));
						continue;
					} else {
						if (bsYearData.contains(readYearCell)) {
							MessageUtil.showMessage(
									readYearCell.substring(0, 4) + " Year having duplicates in 'BS' Sheet");
							bsYearData.clear();
							documentName.setValue("");
							return true;
						}
						bsYearData.add(readYearCell);
					}
					if (Double.parseDouble(readYearCell) <= 1950) {
						MessageUtil.showMessage(
								readYearCell.substring(0, 4) + " Year is not Allowed please upload valid year data");
						documentName.setValue("");
						return true;
					}
					if (Double.parseDouble(readYearCell) > year) {
						MessageUtil.showMessage(
								readYearCell + " Invalid audit year column header, Please upload valid data");
						documentName.setValue("");
						return true;
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return duplicate;
	}

	private Map<String, List<Object[]>> readSheet(HSSFSheet sheet, int sheetNumber) throws Exception {
		logger.debug(Literal.ENTERING);
		auditYearsList = new ArrayList<>();
		Map<String, List<Object[]>> yearDataMap = new HashMap<>();
		List<Object[]> list;

		Row row;
		Row headerRow = null;
		Cell cell;

		for (int j = 0; j <= sheet.getLastRowNum(); j++) {
			row = sheet.getRow(j);
			// validation for data in sheets in EXCEL File
			if (sheet.getLastRowNum() == 0) {
				MessageUtil.showMessage("No data Found in sheet only header available");
				documentName.setValue("");
				break;
			}
			// validation for sheets in EXCEL File
			if (j == 0) {
				headerRow = sheet.getRow(j);
				if (headerRow == null) {
					MessageUtil.showMessage("No data Found in sheets");
					documentName.setValue("");
					break;
				}
				if (validateHeaderRow(headerRow, sheet.getSheetName())) {
					return null;
				}
				// validation for valid years in both sheets in EXCEL File
				if (sheetNumber == 1) {
					for (String years : plYearData) {
						if (!bsYearData.contains(years)) {
							bsYearData.clear();
							bsYearData = null;
							plYearData.clear();
							plYearData = null;
							map.clear();
							map = null;
							yearDataMap.clear();
							yearDataMap = null;
							MessageUtil.showMessage(years.substring(0, 4) + " Year should be in both sheets");
							documentName.setValue("");
							return null;
						}
					}
				}
				// read the excel data from the 4th column
				for (int k = 0; k < row.getLastCellNum(); k++) {
					if (k < 4) {
						continue;
					}
					cell = row.getCell(k);
					String year = cell.toString();
					year = getYear(year, true);
					list = yearDataMap.get(year);
					if (list == null) {
						list = new ArrayList<Object[]>();
						yearDataMap.put(year, list);
					}
				}
				continue;
			}
			// reading headers from excel file
			for (int i = 4; i <= headerRow.getLastCellNum(); i++) {
				cell = headerRow.getCell(i);
				if (cell == null) {
					break;
				}
				String year = cell.toString();
				year = getYear(year, false);
				list = yearDataMap.get(year);
				Object[] dataArray = new Object[3];

				Cell idcell = row.getCell(0);

				int id = 0;
				if (idcell != null) {
					id = (int) idcell.getNumericCellValue();
				}
				Cell desc = row.getCell(1);
				String description = null;
				if (desc != null) {
					description = desc.toString();
				}
				Cell cellAmount = row.getCell(i);
				String amnt = null;
				if (cellAmount != null) {
					amnt = cellAmount.toString();
				}
				BigDecimal ammount = null;
				if (amnt != null && !"".equals(amnt)) {
					ammount = new BigDecimal(amnt);
				} else {
					ammount = new BigDecimal(0);
				}
				ammount = CurrencyUtil.unFormat(ammount, 2);
				dataArray[0] = id;
				dataArray[1] = ammount;
				dataArray[2] = description;

				list.add(dataArray);
			}

		}

		// validating audit years from excel sheet headers with existing data
		for (String auditYear : auditYearsList) {
			for (FinCreditReviewDetails details : finCreditReviewDetailsList) {
				if (StringUtils.equals(auditYear, details.getAuditYear())) {
					String errorMsg = "Credit Review For The Customer with CIF Number: " + customer.getCustCIF()
							+ " and Audit Year: " + auditYear + " is already in process please process it";
					MessageUtil.showError(errorMsg);
					this.documentName.setValue("");
					return null;
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return yearDataMap;
	}

	// set audit year read from excel file
	private String getYear(String year, boolean exlHdrYears) {
		logger.debug(Literal.ENTERING);
		String[] array = StringUtils.split(year, ".");

		if (array != null && year.length() > 1) {
			year = array[0];
		} else if (array != null && year.length() == 1) {
			year = array[0];
		}
		logger.debug(Literal.LEAVING);
		if (exlHdrYears) {
			auditYearsList.add(year);
		}
		return year;
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aCreditReviewDetails
	 * @throws Exception
	 */
	public void doShowDialog(FinCreditReviewDetails aCreditReviewDetails) throws Exception {
		logger.debug(Literal.ENTERING);

		if (customer != null) {
			if (customer.getCustCtgCode().equals(PennantConstants.PFF_CUSTCTG_SME)) {
				listOfFinCreditRevSubCategory = this.creditApplicationReviewService
						.getFinCreditRevSubCategoryByMainCategory(PennantConstants.PFF_CUSTCTG_SME);
			} else {
				listOfFinCreditRevSubCategory = this.creditApplicationReviewService
						.getFinCreditRevSubCategoryByMainCategory(PennantConstants.PFF_CUSTCTG_CORP);
			}
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aCreditReviewDetails.isNewRecord()) {
			this.btnCtrl.setInitNew();
			this.custCIF.focus();
		} else {
			this.custCIF.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);

			} else {
				this.btnCtrl.setInitEdit();

				btnCancel.setVisible(false);
			}
		}

		try {
			if (customer != null) {

				doSetCustomer(customer, newSearchObject);
			}

			if (PennantConstants.PFF_CUSTCTG_SME.equals(customer.getCustCtgCode())) {
				this.bankName.setValue(customer.getCustShrtName());
				this.bankName.setReadonly(true);
				this.label_CreditApplicationReviewDialog_BankName
						.setValue(Labels.getLabel("label_CustomerDialog_CustShrtName.value"));
				this.label_CreditApplicationReviewDialog_BankName.setVisible(false);
				this.bankName.setVisible(false);
				this.space_BankName.setVisible(false);
			}

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_CorporateCreditRevFinanceFileUploadDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method to read the list items and then add to the list.<br>
	 * 
	 * @return
	 * @throws Exception
	 */
	public void setListDetails() throws Exception {
		logger.debug(Literal.ENTERING);

		Map<String, Map<String, List<Object[]>>> data = loadExcelLines();
		if (data == null) {
			return;
		} else {

			for (Entry<String, Map<String, List<Object[]>>> item : data.entrySet()) {
				// reading the sheets
				String categorydesc = item.getKey();

				if ("P&L".equalsIgnoreCase(categorydesc)) {
					categorydesc = "Profit and Loss";
				} else if ("BS".equalsIgnoreCase(categorydesc)) {
					categorydesc = "Balance Sheet";
				} else if ("ASSETS".equalsIgnoreCase(categorydesc)) {
					categorydesc = "Assets";
				}
				// get sheetNames and categoryCode from data base for validating
				// with Excel file
				List<FinCreditRevSubCategory> subcategories = creditApplicationReviewService
						.getFinCreditRevSubCategoryByCustCtg(customer.getCustCtgCode(), categorydesc);

				for (Entry<String, List<Object[]>> revData : item.getValue().entrySet()) {
					FinCreditReviewDetails detail = null;
					if (map != null) {
						detail = map.get(revData.getKey());
					} else {
						map = new HashMap<String, FinCreditReviewDetails>();
					}
					// set the Bean
					if (detail == null) {
						detail = new FinCreditReviewDetails();
						detail.setCreditRevCode(customer.getCustCtgCode());
						doWriteComponentsToBean(detail);
						detail.setAuditYear(revData.getKey());
						workFlowDetails(detail);
						map.put(revData.getKey(), detail);
					}

					// for validation excel file in content level
					Map<String, String> desc = new HashMap<String, String>();
					Map<String, String> seqId = new HashMap<>();

					// reading Description and sequence ID For validating with
					// Excel ID and Description
					for (FinCreditRevSubCategory fcr : subcategories) {
						desc.put(fcr.getSubCategoryDesc(), fcr.getSubCategoryDesc());
						String.valueOf(fcr.getSubCategorySeque());
						seqId.put(fcr.getSubCategorySeque(), fcr.getSubCategorySeque());
					}

					for (Object[] object : revData.getValue()) {
						if (object[2] == null || object[0] == null) {
							break;
						}

						// validation for ID
						if (!seqId.containsKey(object[0].toString())) {
							MessageUtil.showError(object[0].toString() + "Invalid sequence Id");
							data.clear();
							documentName.setValue("");
							return;
						}

					}

					for (Object[] object : revData.getValue()) {
						for (FinCreditRevSubCategory fcr : subcategories) {
							String.valueOf(fcr.getSubCategorySeque());
							if (StringUtils.equalsIgnoreCase(object[0].toString(), fcr.getSubCategorySeque())) {
								FinCreditReviewSummary summary = new FinCreditReviewSummary();
								summary.setNewRecord(true);
								BigDecimal itemValue = new BigDecimal(object[1].toString());
								summary.setItemValue(itemValue);
								summary.setSubCategoryCode(fcr.getSubCategoryCode());
								summary.setRecordType(PennantConstants.RCD_ADD);
								summary.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
								summary.setTaskId(taskId);
								summary.setNextTaskId(nextTaskId);
								summary.setRoleCode(getRole());
								summary.setNextRoleCode(nextRoleCode);
								detail.getCreditReviewSummaryEntries().add(summary);
								break;
							}
						}
					}
				}

			}
		}
		logger.debug(Literal.LEAVING);
	}

	// if uploaded file is sucessfully validated then call this method for
	// saving the data
	public void excelSave() {
		logger.debug(Literal.ENTERING);
		for (FinCreditReviewDetails reviewDetails : map.values()) {
			AuditHeader auditHeader = null;
			reviewDetails.setNewRecord(true);
			auditHeader = getAuditHeader(reviewDetails, PennantConstants.TRAN_ADD);
			creditApplicationReviewService.saveOrUpdate(auditHeader);
		}
		map.clear();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	public void doWriteComponentsToBean(FinCreditReviewDetails aCreditReviewDetails) {
		logger.debug(Literal.ENTERING);
		List<WrongValueException> wveList = new ArrayList<WrongValueException>();
		doSetValidation();
		if (StringUtils.isNotBlank(this.lovDescCustCIF.getValue())) {
			this.lovDescCustCIF.clearErrorMessage();
			customer = (Customer) PennantAppUtil.getCustomerObject(this.lovDescCustCIF.getValue(), getFilterList());
			if (customer != null) {
				aCreditReviewDetails.setLovDescCustCIF(this.lovDescCustCIF.getValue());
				aCreditReviewDetails.setCreditRevCode(customer.getCustCtgCode());
				aCreditReviewDetails.setCustomerId(customer.getCustID());
				aCreditReviewDetails.setUserDetails(getUserWorkspace().getLoggedInUser());
			} else {
				try {
					throw new WrongValueException(this.lovDescCustCIF,
							Labels.getLabel("FIELD_NO_EMPTY",
									new String[] { Labels.getLabel("label_CreditRevSelectCategory_CustomerCIF.value"),
											Labels.getLabel("label_CreditRevSelectCategory_CustomerCIF.value") }));
				} catch (WrongValueException wve) {
					wveList.add(wve);
				}
			}
		} else {
			try {
				throw new WrongValueException(this.lovDescCustCIF,
						Labels.getLabel("FIELD_NO_EMPTY",
								new String[] { Labels.getLabel("label_CreditRevSelectCategory_CustomerCIF.value"),
										Labels.getLabel("label_CreditRevSelectCategory_CustomerCIF.value") }));

			} catch (WrongValueException wve) {
				wveList.add(wve);
			}
		}
		// auditPeriod validation

		if (!"#".equals(StringUtils.trimToEmpty(this.auditPeriod.getSelectedItem().getValue().toString()))) {
			aCreditReviewDetails
					.setAuditPeriod(Integer.parseInt(this.auditPeriod.getSelectedItem().getValue().toString()));

		} else {
			try {
				throw new WrongValueException(this.auditPeriod,
						Labels.getLabel("FIELD_NO_EMPTY",
								new String[] { Labels.getLabel("label_CreditRevSelectCategory_auditPeriod.value"),
										Labels.getLabel("label_CreditRevSelectCategory_auditPeriod.value") }));
			} catch (WrongValueException wve) {
				wveList.add(wve);
			}
		}

		String category = "";
		// Duplicate Audit Year Validation
		if (StringUtils.isNotBlank(this.lovDescCustCIF.getValue())) {
			if (customer == null) {
				customer = (Customer) PennantAppUtil.getCustomerObject(this.lovDescCustCIF.getValue(), getFilterList());
			}
			if (StringUtils.equals(PennantConstants.PFF_CUSTCTG_SME, customer.getCustCtgCode())) {
				category = PennantConstants.PFF_CUSTCTG_SME;
				aCreditReviewDetails.setLovDescCustCtgCode(category);
			} else if (StringUtils.equals(PennantConstants.PFF_CUSTCTG_INDIV, customer.getCustCtgCode())) {
				category = PennantConstants.PFF_CUSTCTG_INDIV;
				aCreditReviewDetails.setLovDescCustCtgCode(category);
			} else {
				category = PennantConstants.PFF_CUSTCTG_CORP;
				aCreditReviewDetails.setLovDescCustCtgCode(category);
			}
		}
		// AuditType validation

		if (!"#".equals(StringUtils.trimToEmpty(this.auditType.getSelectedItem().getValue().toString()))) {
			aCreditReviewDetails.setAuditType(this.auditType.getSelectedItem().getValue().toString());

		} else {
			try {
				throw new WrongValueException(this.auditType,
						Labels.getLabel("FIELD_NO_EMPTY",
								new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_AuditType.value"),
										Labels.getLabel("label_CreditApplicationReviewDialog_AuditType.value") }));
			} catch (WrongValueException wve) {
				wveList.add(wve);
			}
		}

		if (this.custID.getValue() != null && creditReviewDetail.getAuditPeriod() != 0) {
			Map<String, List<FinCreditReviewSummary>> creditReviewSummaryMap = getCreditApplicationReviewService()
					.getListCreditReviewSummaryByCustId2(custID.getValue(), 0, this.auditYear.intValue(), category,
							creditReviewDetail.getAuditPeriod(), true, "_View");
			if (creditReviewSummaryMap.size() > 0) {

				throw new WrongValueException(this.auditYear, Labels.getLabel("const_YEAR_PERIOD", new String[] {
						String.valueOf(this.auditYear.intValue()), String.valueOf(this.auditPeriod.getValue()) }));
			}
		}

		try {
			if (StringUtils.isEmpty(this.bankName.getValue())) {
				throw new WrongValueException(this.bankName,
						Labels.getLabel("label_CreditApplicationReviewDialog_BankName.value"));
			} else {

				aCreditReviewDetails.setBankName(this.bankName.getValue());
			}
		} catch (WrongValueException wve) {
			wveList.add(wve);
		}

		try {
			if (StringUtils.isEmpty(this.location.getValue())) {
				throw new WrongValueException(this.location,
						Labels.getLabel("label_CreditApplicationReviewDialog_Location.value"));
			} else {
				aCreditReviewDetails.setLocation(this.location.getValue());
			}
		} catch (WrongValueException wve) {
			wveList.add(wve);
		}

		try {
			if (StringUtils.isEmpty(this.auditors.getValue())) {
				throw new WrongValueException(this.auditors,
						Labels.getLabel("label_CreditApplicationReviewDialog_Auditors.value"));
			} else {

				aCreditReviewDetails.setAuditors(this.auditors.getValue());
			}
		} catch (WrongValueException wve) {
			wveList.add(wve);
		}

		try {
			if (StringUtils.isEmpty(this.currencyType.getValue())) {
				throw new WrongValueException(this.currencyType, Labels.getLabel("FIELD_NO_INVALID",
						new String[] { Labels.getLabel("labelCreditApplicationReviewDialog_FinCcy.value") }));
			} else {
				aCreditReviewDetails.setCurrency(this.currencyType.getValue());
			}
		} catch (WrongValueException wve) {
			wveList.add(wve);
		}

		try {
			if (this.qualifiedUnQualified.getSelectedItem().getValue().toString()
					.equals(FacilityConstants.CREDITREVIEW_QUALIFIED)) {
				aCreditReviewDetails.setQualified(true);
			} else {
				aCreditReviewDetails.setQualified(false);
			}
		} catch (WrongValueException wve) {
			wveList.add(wve);
		}

		try {
			if (this.auditedDate.getValue() != null) {
				aCreditReviewDetails.setAuditedDate(this.auditedDate.getValue());
			} else {
				throw new WrongValueException(this.auditedDate,
						Labels.getLabel("FIELD_NO_EMPTY",
								new String[] { Labels.getLabel("label_CreditApplicationReviewDialog_AuditDate.value"),
										Labels.getLabel("label_CreditApplicationReviewDialog_AuditDate.value") }));
			}
		} catch (WrongValueException wve) {
			wveList.add(wve);
		}

		try {
			if (this.conSolOrUnConsol.getSelectedItem().getValue().toString()
					.equals(FacilityConstants.CREDITREVIEW_CONSOLIDATED)) {
				aCreditReviewDetails.setConsolidated(true);
			} else {
				aCreditReviewDetails.setConsolidated(false);
			}
		} catch (WrongValueException wve) {
			wveList.add(wve);
		}

		try {
			aCreditReviewDetails.setDocument(this.documentName.getValue());
		} catch (WrongValueException wve) {
			wveList.add(wve);
		}
		for (int i = 0; i < wveList.size(); i++) {
			if (wveList.isEmpty()) {
				try {
					if ((this.documentName.getValue() == null || StringUtils.isEmpty(this.documentName.getValue()))) {
						throw new WrongValueException(this.documentName, Labels.getLabel("MUST_BE_UPLOADED",
								new String[] { Labels.getLabel("label_CreditRevSelectCategory_CorporateDoc.value") }));
					}
					aCreditReviewDetails.setDocument(this.documentName.getValue());
				} catch (WrongValueException wve) {
					wveList.add(wve);
				}
			}
		}

		doRemoveValidation();

		if (wveList.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wveList.size()];
			for (int i = 0; i < wveList.size(); i++) {
				wvea[i] = (WrongValueException) wveList.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		setValidationOn(false);
		this.location.setConstraint("");
		this.bankName.setConstraint("");
		this.auditors.setConstraint("");
		this.auditType.setConstraint("");
		this.auditedDate.setConstraint("");
		this.currencyType.setConstraint("");
		this.documentName.setConstraint("");
		logger.debug(Literal.LEAVING);

	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(FinCreditReviewDetails aCreditReviewDetails, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCreditReviewDetails.getBefImage(),
				aCreditReviewDetails);
		return new AuditHeader(String.valueOf(aCreditReviewDetails.getDetailId()), null, null, null, auditDetail,
				aCreditReviewDetails.getUserDetails(), getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.creditReviewDetails);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.creditReviewDetails.getDetailId());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public FinCreditReviewDetails getCreditReviewDetail() {
		return creditReviewDetail;
	}

	public void setCreditReviewDetail(FinCreditReviewDetails creditReviewDetail) {
		this.creditReviewDetail = creditReviewDetail;
	}

	public void setCreditApplicationReviewService(CreditApplicationReviewService creditApplicationReviewService) {
		this.creditApplicationReviewService = creditApplicationReviewService;
	}

	public CreditApplicationReviewService getCreditApplicationReviewService() {
		return creditApplicationReviewService;
	}

	public FinCreditReviewDetails getCreditReviewDetails() {
		return this.creditReviewDetails;
	}

	public void setCreditReviewDetails(FinCreditReviewDetails creditReviewDetails) {
		this.creditReviewDetails = creditReviewDetails;
	}

	public void setFinCreditRevSubCategoryService(FinCreditRevSubCategoryService finCreditRevSubCategoryService) {
		this.finCreditRevSubCategoryService = finCreditRevSubCategoryService;
	}

	public FinCreditRevSubCategoryService getFinCreditRevSubCategoryService() {
		return finCreditRevSubCategoryService;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public CreditApplicationReviewListCtrl getCreditApplicationReviewListCtrl() {
		return this.creditApplicationReviewListCtrl;
	}

}

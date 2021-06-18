package com.pennant.webui.customermasters.customer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.BankDetailDAO;
import com.pennant.backend.dao.customermasters.CustomerExtLiabilityDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.applicationmaster.OtherBankFinanceType;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.ExtLiabilityPaymentdetails;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.dao.customer.liability.ExternalLiabilityDAO;

public class CustomerExtLiabilityUploadDialogCtrl extends GFCBaseCtrl<CustomerExtLiability> {
	private final static Logger logger = LogManager.getLogger(CustomerExtLiabilityUploadDialogCtrl.class);

	private static final long serialVersionUID = 1L;

	protected Window window_CustomerExtLiabilityUpload; // autoWired
	protected Label extFileUpload; // autoWired
	protected Button extBtnUpload; // autoWired

	protected Button btnSave; // autowired
	protected Button btnCancel; // autowired
	protected Textbox extUplodedFileName;
	private Media media;
	protected Listbox listBoxErrorDetails;

	private long custId = Long.MIN_VALUE;
	protected transient boolean enqiryModule;
	private int formater = CurrencyUtil.getFormat("");
	private int noOfInstallments = 0;

	Map<String, CustomerExtLiability> map = new HashMap<>();
	private CustomerExtLiabilityDAO customerExtLiabilityDAO;
	private ExternalLiabilityDAO externalLiabilityDAO;
	private PagedListService pagedListService;
	private CustomerDialogCtrl customerDialogCtrl;
	private BankDetailDAO bankDetailDAO;

	private final List<ValueLabel> sourceInfoList = PennantStaticListUtil.getSourceInfoList();
	private final List<ValueLabel> trackCheckList = PennantStaticListUtil.getTrackCheckList();
	private final List<ValueLabel> emiClearance = PennantStaticListUtil.getEmiClearance();

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerExtLiabilityUploadDialog";
	}

	public void onCreate$window_CustomerExtLiabilityUpload(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_CustomerExtLiabilityUpload);
		try {
			if (arguments.containsKey("custId")) {
				custId = (long) arguments.get("custId");
			}

			if (arguments.containsKey("enqiryModule")) {
				enqiryModule = (boolean) arguments.get("enqiryModule");
			}
			if (arguments.containsKey("customerDialogCtrl")) {
				customerDialogCtrl = (CustomerDialogCtrl) arguments.get("customerDialogCtrl");
			}

			doCheckRights();
			doShowDialog();
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displaying the dialog window
	 */
	private void doShowDialog() {
		logger.debug(Literal.ENTERING);
		this.listBoxErrorDetails.setHeight(borderLayoutHeight - 226 + "px");
		this.listBoxErrorDetails.setHeight(borderLayoutHeight - 226 + "px");
		this.window_CustomerExtLiabilityUpload.setHeight("85%");
		this.window_CustomerExtLiabilityUpload.setWidth("85%");
		setDialog(DialogType.MODAL);
		logger.debug(Literal.LEAVING);

	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		this.btnNew.setVisible(false);
		this.btnEdit.setVisible(false);
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(true);
		this.btnNotes.setVisible(false);
		this.btnCancel.setVisible(false);
		this.btnSave.setVisible(false);
		if (enqiryModule) {
			this.extBtnUpload.setDisabled(true);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "Upload" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onUpload$extBtnUpload(UploadEvent event) throws Exception {
		logger.debug(Literal.ENTERING + event.toString());
		media = event.getMedia();
		if (!uploadDocFormatValidation(media)) {
			return;
		}
		if (media.getName().length() > 100) {
			throw new WrongValueException(this.extUplodedFileName, Labels.getLabel("label_Filename_length_File"));
		} else {
			this.extUplodedFileName.setValue(media.getName());
		}
		readFromExcel();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * This method read data from XL file
	 * 
	 * @throws IOException
	 */
	private void readFromExcel() throws IOException {
		map.clear();
		Workbook workBook = null;
		try {
			if (media.getName().toLowerCase().endsWith(".xls")) {
				try {
					workBook = new HSSFWorkbook(media.getStreamData());
				} catch (OfficeXmlFileException e) {
					// due to some xl version issue we have to use
					workBook = new XSSFWorkbook(media.getStreamData());
				}
			} else if (media.getName().toLowerCase().endsWith(".xlsx")) {
				workBook = new XSSFWorkbook(media.getStreamData());
			}
		} catch (Exception e) {
			MessageUtil.showError(Labels.getLabel("label_ValidatedUploadFile"));
			return;
		}

		if (workBook == null) {
			MessageUtil.showError(Labels.getLabel("label_ValidatedUploadFile"));
			return;
		}

		Sheet myExcelSheet = workBook.getSheetAt(0);
		// if it is empty sheet return
		if (myExcelSheet == null) {
			MessageUtil.showError(Labels.getLabel("label_ValidatedUploadFile"));
			return;
		}

		if (!myExcelSheet.getSheetName().contains(Labels.getLabel("label_ExternalLibilities.label"))) {
			MessageUtil.showError(Labels.getLabel("label_ValidatedUploadFile"));
			return;
		}

		// only header column is available with out data return
		int rowCount = myExcelSheet.getPhysicalNumberOfRows();
		if (rowCount <= 1) {
			MessageUtil.showError(Labels.getLabel("label_ValidatedUploadFile"));
			return;
		}

		Iterator<Row> rows = myExcelSheet.iterator();

		int seqNo = 0;
		while (rows.hasNext()) {
			Row row = rows.next();

			if (row.getRowNum() == 0) {
				if (row.getLastCellNum() != 37) {
					MessageUtil.showError(Labels.getLabel("label_ValidatedUploadFile"));
					return;
				}
				continue;
			}
			seqNo++;
			CustomerExtLiability ce = new CustomerExtLiability();
			ExtLiabilityPaymentdetails epd = new ExtLiabilityPaymentdetails();
			String key = null;

			for (Cell cell : row) {
				if (cell.getColumnIndex() == 0) {
					key = getValue(cell.toString());
				}

				if (key == null) {
					MessageUtil.showError("ID is mandatory for every record.");
					return;
				}

				try {
					if (!map.containsKey(key)) {
						ce.setSeqNo(seqNo);
						ce.setCustId(custId);
						prepareCustomerExtLiabilityData(ce, cell.toString(), cell.getColumnIndex());
						prepareRTRData(epd, cell.toString(), cell.getColumnIndex());
					} else {
						ce = map.get(key);
						prepareRTRData(epd, cell.toString(), cell.getColumnIndex());
					}
				} catch (WrongValueException e) {
					MessageUtil.showError(e.getMessage());
					return;
				}
			}
			setWorkFlowData(ce, epd);

			ce.getExtLiabilitiesPayments().add(epd);

			map.put(key, ce);
		}

		workBook.close();

		// save the data
		String msg = Labels.getLabel("label_FileUpload_Save_Confirmation");
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			doSave();
		}
	}

	private void prepareCustomerExtLiabilityData(CustomerExtLiability ce, String cellValue, int i) {
		switch (i) {
		case 1:
			// Product
			ce.setFinType(getValue(cellValue));
			break;
		case 2:
			// Financer Name
			ce.setLoanBank(getValue(cellValue));
			break;
		case 3:
			// Other Financial Institution
			ce.setOtherFinInstitute(getValue(cellValue));
			break;
		case 4:
			// Installment Amount/EMI
			ce.setInstalmentAmount(getValueAsAmount(cellValue));
			break;
		case 5:
			// Outstanding Balance/Limit
			ce.setOutstandingBalance(getValueAsAmount(cellValue));
			break;
		case 6:
			// Loan Amount
			ce.setOriginalAmount(getValueAsAmount(cellValue));
			break;
		case 7:
			// Loan Date
			ce.setFinDate(DateUtil.parse(cellValue, DateFormat.LONG_DATE));
			break;
		case 8:
			// Status
			ce.setFinStatus(getValue(cellValue));
			break;
		case 9:
			// ROI
			ce.setRateOfInterest(getValueAsAmount(cellValue));
			break;
		case 10:
			// Loan Tenure
			ce.setTenure(getValueAsInt(cellValue));
			break;
		case 11:
			// Balance Tenure
			ce.setBalanceTenure(getValueAsInt(cellValue));
			break;
		case 12:
			// Number of Bounces in last 3 months
			ce.setBounceInstalments(getValueAsInt(cellValue));
			break;
		case 13:
			// Number of Bounces in last 6 months
			ce.setNoOfBouncesInSixMonths(getValueAsInt(cellValue));
			break;
		case 14:
			// Number of Bounces in last 12 months
			ce.setNoOfBouncesInTwelveMonths(getValueAsInt(cellValue));
			break;
		case 15:
			// POS
			ce.setPrincipalOutstanding(getValueAsAmount(cellValue));
			break;
		case 16:
			// Overdue
			ce.setOverdueAmount(getValueAsAmount(cellValue));
			break;
		case 17:
			// EMI Considered for FOIR
			ce.setFoir(getValueAsBoolean(cellValue));
			break;
		case 18:
			// Source of Info
			ce.setSource(getListAsInt(cellValue));
			break;
		case 19:
			// Track Check from
			ce.setCheckedBy(getListAsInt(cellValue));
			break;
		case 20:
			// Security Details/Property details
			ce.setSecurityDetails(getValue(cellValue));
			break;
		case 21:
			// End Use of Funds
			ce.setLoanPurpose(getValue(cellValue));
			break;
		case 22:
			// Repayment Account Bank Name
			ce.setRepayBank(getValue(cellValue));
			break;
		case 23:
			// Repayment Bank Account No
			ce.setRepayFromAccNo(getValue(cellValue));
			break;
		case 24:
			// Considered for RTR based loan
			ce.setConsideredBasedOnRTR(getValueAsBoolean(cellValue));
			break;
		case 25:
			// Number of Installments for RTR
			noOfInstallments = getValueAsInt(cellValue);
			break;
		case 26:
			// Imputed EMI
			ce.setImputedEmi(getValueAsAmount(cellValue));
			break;
		case 27:
			// OwnerShip
			ce.setOwnerShip(getValue(cellValue));
			break;
		case 28:
			// Last 24 Months
			ce.setLastTwentyFourMonths(getValueAsBoolean(cellValue));
			break;
		case 29:
			// Last 6 Months
			ce.setLastSixMonths(getValueAsBoolean(cellValue));
			break;
		case 30:
			// Last 3 Months
			ce.setLastThreeMonths(getValueAsBoolean(cellValue));
			break;
		case 31:
			// Current OverDue
			ce.setCurrentOverDue(getValueAsAmount(cellValue));
			break;
		case 32:
			// MOB
			BigDecimal bd = new BigDecimal(cellValue);
			ce.setMob(getValueAsInt(bd.toString()));
			break;
		case 33:
			// Remarks
			ce.setRemarks(getValue(cellValue));
			break;
		default:
			break;
		}
	}

	private void prepareRTRData(ExtLiabilityPaymentdetails epd, String cellValue, int i) {
		switch (i) {
		case 34:
			epd.setEmiType(getValue(cellValue));
			break;
		case 35:
			epd.setEmiClearance(getValue(cellValue));
			break;
		case 36:
			epd.setEmiClearedDay(getValueAsInt(cellValue));
			break;
		default:
			break;
		}
	}

	private void setWorkFlowData(CustomerExtLiability ce, ExtLiabilityPaymentdetails epd) {
		LoggedInUser loggedInUser = getUserWorkspace().getLoggedInUser();
		long userId = loggedInUser.getUserId();
		String rcdStatus = PennantConstants.RCD_STATUS_APPROVED;

		ce.setLastMntBy(userId);
		ce.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		ce.setUserDetails(loggedInUser);
		ce.setVersion(1);
		ce.setRecordStatus(rcdStatus);

		epd.setLastMntBy(userId);
		epd.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		epd.setUserDetails(loggedInUser);
		epd.setVersion(1);
		epd.setRecordStatus(rcdStatus);
	}

	private String getValue(String value) {
		return StringUtils.trimToNull(value);
	}

	private int getValueAsInt(String value) {
		value = getValue(value);
		if (value == null) {
			return 0;
		}
		value = value.split("\\.")[0];
		try {
			int num = Integer.valueOf(value);
			if (num < 0) {
				throw new WrongValueException("The given file having wrong formatted values : " + value);
			}
			return num;
		} catch (NumberFormatException e) {
			throw new WrongValueException("The given file having wrong formatted values : " + value);
		}
	}

	private int getListAsInt(String value) {
		value = getValue(value);
		if (value == null) {
			throw new WrongValueException("The given file having wrong formatted values : " + value);
		}
		value = value.split("\\.")[0];
		try {
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			throw new WrongValueException("The given file having wrong formatted values : " + value);
		}
	}

	private BigDecimal getValueAsAmount(String value) {
		try {
			BigDecimal amount = PennantApplicationUtil.unFormateAmount(value, formater);
			if (amount.compareTo(BigDecimal.ZERO) < 0) {
				throw new WrongValueException("The given file having wrong formatted values : " + value);
			}
			return amount;
		} catch (NumberFormatException e) {
			throw new WrongValueException("The given file having wrong formatted values : " + value);
		}
	}

	private boolean getValueAsBoolean(String value) {
		value = getValue(value);
		if (value == null) {
			return false;
		}

		switch (value.toLowerCase()) {
		case "true":
			return true;
		case "false":
			return true;
		default:
			throw new WrongValueException("The given file having wrong formatted values : " + value);
		}
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		// doSave();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * This method will save the external liabilities
	 */
	private void doSave() {
		logger.debug(Literal.ENTERING);
		boolean flag = false;
		try {
			if (map != null && !map.isEmpty()) {
				for (String key : map.keySet()) {
					CustomerExtLiability customerExtLiability = map.get(key);
					if (customerExtLiability != null) {
						doWriteComponentsToBean(customerExtLiability);
						extendedComboValidations(customerExtLiability);
						// setting the linkId
						customerExtLiabilityDAO.setLinkId(customerExtLiability);
						if (!flag) {
							// delete the existing data first while upload
							externalLiabilityDAO.deleteByLinkId(customerExtLiability.getLinkId(), "");
							// delete the existing data first while upload
							customerExtLiabilityDAO.delete(customerExtLiability.getLinkId(), "");
							flag = true;
						}

						// Saving the Data
						externalLiabilityDAO.save(customerExtLiability, "");
						if (customerExtLiability.getExtLiabilitiesPayments() != null
								&& !customerExtLiability.getExtLiabilitiesPayments().isEmpty()) {
							for (ExtLiabilityPaymentdetails extLiabilityPaymentdetails : customerExtLiability
									.getExtLiabilitiesPayments()) {
								extLiabilityPaymentdetails.setLiabilityId(customerExtLiability.getId());
							}
							// saving the list
							customerExtLiabilityDAO.save(customerExtLiability.getExtLiabilitiesPayments(), "");
						}
					}
				}
				// Displaying the success message
				MessageUtil.showMessage(Labels.getLabel("label_ValidatedDataSaved"));
				// Data retrieving to render the data in liability list
				CustomerExtLiability liability = new CustomerExtLiability();
				liability.setCustId(custId);
				List<CustomerExtLiability> customerExtLiabilities = externalLiabilityDAO
						.getLiabilities(liability.getCustId(), "");
				if (CollectionUtils.isNotEmpty(customerExtLiabilities)) {
					for (CustomerExtLiability extLiability : customerExtLiabilities) {
						extLiability.setExtLiabilitiesPayments(
								customerExtLiabilityDAO.getExtLiabilitySubDetailById(extLiability.getId(), ""));
					}
				}
				// Refreshing the list box data after successful upload
				customerDialogCtrl.doFillCustomerExtLiabilityDetails(customerExtLiabilities);
				// close the dialog after save
				closeDialog();
				logger.debug(Literal.LEAVING);
			}
		} catch (WrongValueException e) {
			MessageUtil.showError(e.getMessage());
		}
	}

	public void doWriteComponentsToBean(CustomerExtLiability ce) {
		StringBuilder errors = new StringBuilder();

		if (StringUtils.isEmpty(ce.getLoanBank())) {
			errors.append("Bank Name is Mandatory;");
		}

		if (StringUtils.isEmpty(ce.getFinType())) {
			errors.append("Product is Mandatory;");
		}

		if (ce.getInstalmentAmount().compareTo(BigDecimal.ZERO) <= 0) {
			errors.append("Installment Amount/EMI should be greater than Zero;");
		}

		if (ce.getOriginalAmount().compareTo(BigDecimal.ZERO) <= 0) {
			errors.append("Original Amount should be greater than Zero;");
		}

		if (ce.getFinDate() == null) {
			errors.append("Loan Date should be mandatory;");
		}

		if (StringUtils.isEmpty(ce.getFinStatus())) {
			errors.append("Status is Mandatory;");
		}

		if (ce.getTenure() <= 0) {
			errors.append("Total Tenure should be greater than Zero;");
		}

		if (ce.getBalanceTenure() <= 0) {
			errors.append("Balance Tenure should be greater than Zero;");
		}

		if (StringUtils.isEmpty(ce.getRepayBank())) {
			errors.append("Repayment from is Mandatory;");
		}

		BankDetail bd = bankDetailDAO.getAccNoLengthByCode(ce.getRepayBank(), "");
		if (bd == null) {
			errors.append("Given Repayment from is not valid;");
		}

		int maxAccNoLength = bd.getAccNoLength();
		int minAccNoLength = bd.getMinAccNoLength();
		int givenAccNoLength = ce.getRepayFromAccNo().length();

		if (givenAccNoLength < minAccNoLength || givenAccNoLength > maxAccNoLength) {
			errors.append("Repayment Bank Account No should be greater than ");
			errors.append(maxAccNoLength);
			errors.append(" or less than or equal to ").append(minAccNoLength);
		}
		if (ce.getOutstandingBalance().compareTo(BigDecimal.ZERO) <= 0) {
			errors.append("Outstanding Balance/Limit should be greater than Zero;");
		}

		if (ce.getRateOfInterest().compareTo(BigDecimal.ZERO) <= 0) {
			errors.append("ROI should be greater than Zero;");
		}

		if (ce.getPrincipalOutstanding().compareTo(BigDecimal.ZERO) <= 0) {
			errors.append("POS should be greater than Zero;");
		}

		if (StringUtils.isEmpty(ce.getLoanPurpose())) {
			errors.append("End Use of Funds is Mandatory;");
		}

		if (noOfInstallments == 0) {
			errors.append("Number of Installments should be greater than Zero;");
		}

		List<ExtLiabilityPaymentdetails> payments = ce.getExtLiabilitiesPayments();

		if (noOfInstallments != payments.size()) {
			errors.append("Number of Installments for RTR and given months are not matched;");
		}

		Date appDate = SysParamUtil.getAppDate();
		int i = 0;
		for (ExtLiabilityPaymentdetails elp : payments) {
			Date emiDate = null;
			String emiType = elp.getEmiType();
			if (StringUtils.isEmpty(emiType)) {
				errors.append("EMI Month is Mandatory;");
				break;
			}

			try {
				emiDate = DateUtil.parse(emiType, DateFormat.LONG_DATE);
			} catch (IllegalArgumentException e) {
				errors.append("EMI Month format is wrong, Please provide valid format;");
				break;
			}

			if (emiDate.after(appDate)) {
				errors.append("EMI Month is crossed Application Date" + appDate);
				break;
			}
			int monthsBetween = DateUtil.getMonthsBetween(DateUtil.addMonths(appDate, -i), emiDate);
			i++;
			if (monthsBetween != 0) {
				errors.append("EMI month are not in sequence or 1st EMI month is not started with Application month");
				break;
			}

			if (StringUtils.isEmpty(elp.getEmiClearance())) {
				errors.append("Month Clearance is Mandatory;");
				break;
			}

			int count = 0;
			for (ValueLabel emi : emiClearance) {
				if (emi.getValue().equals(elp.getEmiClearance())) {
					count++;
					break;
				}
			}
			if (count == 0) {
				errors.append(" Emi Clearance data is not matched;");
				break;
			}

			if (PennantConstants.CLEARED.equals(elp.getEmiClearance()) && elp.getEmiClearedDay() <= 0) {
				errors.append("Month Clearance is selected as 'CLRD', so Cleared Day is Mandatory;");
				break;
			}

			if (!PennantConstants.CLEARED.equals(elp.getEmiClearance()) && elp.getEmiClearedDay() > 0) {
				elp.setEmiClearedDay(0);
			}
		}

		if (StringUtils.isNotEmpty(errors.toString())) {
			throw new WrongValueException(errors.toString());
		}
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is to validate the upload document formats like .EXE,.BAT,.SH.
	 * 
	 * @param Media
	 * @return boolean
	 */
	public static boolean uploadDocFormatValidation(final Media media) {
		if (media != null) {
			String filenamesplit[] = media.getName().split("\\.");
			if (filenamesplit.length >= 2) {
				for (int i = 0; i < filenamesplit.length; i++) {
					if (filenamesplit.length == i + 1) {
						if (filenamesplit[i] != null
								&& (filenamesplit[i].equalsIgnoreCase("exe") || filenamesplit[i].equalsIgnoreCase("bat")
										|| filenamesplit[i].equalsIgnoreCase("sh"))) {
							MessageUtil.showError(Labels.getLabel("UnSupported_Document_V2"));
							return false;
						}
					} else if (filenamesplit[i] != null && (filenamesplit[i].equalsIgnoreCase("exe")
							|| filenamesplit[i].equalsIgnoreCase("bat") || filenamesplit[i].equalsIgnoreCase("sh")
							|| filenamesplit[i].equalsIgnoreCase("jpg") || filenamesplit[i].equalsIgnoreCase("jpeg")
							|| filenamesplit[i].equalsIgnoreCase("png") || filenamesplit[i].equalsIgnoreCase("rar")
							|| filenamesplit[i].equalsIgnoreCase("zip") || filenamesplit[i].equalsIgnoreCase("msg")
							|| filenamesplit[i].equalsIgnoreCase("doc") || filenamesplit[i].equalsIgnoreCase("docx")
							|| filenamesplit[i].equalsIgnoreCase("ppt") || filenamesplit[i].equalsIgnoreCase("pptx"))) {
						MessageUtil.showError(Labels.getLabel("UnSupported_Document_V2"));
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * This method is used to download the customer liabilities
	 * 
	 * @param customerExtLiabilities
	 */
	public void downloadExternalLiability(List<CustomerExtLiability> customerExtLiabilities) {
		try {
			byte[] data = processExternalLiabilitiesForXLDownload(customerExtLiabilities);
			if (data != null) {
				Filedownload.save(data, "application/vnd.ms-excel", "ExternalLiabilities");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prepare the liability list
	 * 
	 * @param customerExtLiability
	 * @param paymentdetails
	 * @param list
	 */
	private List<String> prepareLiabilityValuesFromObject(CustomerExtLiability customerExtLiability,
			ExtLiabilityPaymentdetails paymentdetails) {
		ArrayList<String> list = new ArrayList<>();
		list.add(String.valueOf(customerExtLiability.getId()));
		// Product
		list.add(customerExtLiability.getFinType());

		// Financer Name
		list.add(customerExtLiability.getLoanBank());

		// Other Financial Institution
		list.add(customerExtLiability.getOtherFinInstitute());

		// Installment Amount/EMI
		BigDecimal instalmentAmount = customerExtLiability.getInstalmentAmount();
		list.add(PennantApplicationUtil.amountFormate(instalmentAmount, formater).replace(",", ""));

		// Outstanding Balance/Limit
		BigDecimal outstandingBalance = customerExtLiability.getOutstandingBalance();
		list.add(PennantApplicationUtil.amountFormate(outstandingBalance, formater).replace(",", ""));

		// Loan Amount
		BigDecimal originalAmount = customerExtLiability.getOriginalAmount();
		list.add(PennantApplicationUtil.amountFormate(originalAmount, formater).replace(",", ""));

		// Loan Date
		Date finDate = customerExtLiability.getFinDate();
		if (finDate != null && DateUtility.format(finDate, "dd-MM-yyyy") != null) {
			list.add(DateUtility.format(finDate, "dd-MM-yyyy"));
		} else {
			list.add(" ");
		}

		// Status
		String finStatus = customerExtLiability.getFinStatus();
		list.add(finStatus);

		// ROI
		BigDecimal rateOfInterest = customerExtLiability.getRateOfInterest();
		list.add(PennantApplicationUtil.amountFormate(rateOfInterest, formater));

		// Loan Tenure
		int tenure = customerExtLiability.getTenure();
		list.add(String.valueOf(tenure));

		// Balance Tenure
		int balanceTenure = customerExtLiability.getBalanceTenure();
		list.add(String.valueOf(balanceTenure));

		// Number of Bounces in last 3 months
		int bounceInstalments = customerExtLiability.getBounceInstalments();
		list.add(String.valueOf(bounceInstalments));

		// Number of Bounces in last 6 months
		int noOfBouncesInSixMonths = customerExtLiability.getNoOfBouncesInSixMonths();
		list.add(String.valueOf(noOfBouncesInSixMonths));

		// Number of Bounces in last 12 months
		int noOfBouncesInTwelveMonths = customerExtLiability.getNoOfBouncesInTwelveMonths();
		list.add(String.valueOf(noOfBouncesInTwelveMonths));

		// POS
		BigDecimal principalOutstanding = customerExtLiability.getPrincipalOutstanding();
		list.add(PennantApplicationUtil.amountFormate(principalOutstanding, formater).replace(",", ""));

		// Overdue
		BigDecimal overdueAmount = customerExtLiability.getOverdueAmount();
		list.add(PennantApplicationUtil.amountFormate(overdueAmount, formater).replace(",", ""));

		// EMI Considered for FOIR
		boolean foir = customerExtLiability.isFoir();

		list.add(foir ? "true" : "false");

		// Source of Info
		int source = customerExtLiability.getSource();
		list.add(String.valueOf(source));

		// Track Check from
		int checkedBy = customerExtLiability.getCheckedBy();
		list.add(String.valueOf(checkedBy));

		// Security Details/Property details
		String securityDetails = customerExtLiability.getSecurityDetails();
		list.add(String.valueOf(securityDetails));

		// End Use of Funds
		String loanPurpose = customerExtLiability.getLoanPurpose();
		list.add(String.valueOf(loanPurpose));

		// Repayment Account Bank Name
		String repayBank = customerExtLiability.getRepayBank();
		list.add(String.valueOf(repayBank));

		// Repayment Bank Account No
		String repayFromAccNo = customerExtLiability.getRepayFromAccNo();
		list.add(String.valueOf(repayFromAccNo));

		// Considered for RTR based loan
		boolean consideredBasedOnRTR = customerExtLiability.isConsideredBasedOnRTR();
		list.add(consideredBasedOnRTR ? "true" : "false");

		// Number of Installments for RTR
		int size = customerExtLiability.getExtLiabilitiesPayments().size();
		list.add(String.valueOf(size));

		// Imputed EMI
		BigDecimal imputedEmi = customerExtLiability.getImputedEmi();
		list.add(PennantApplicationUtil.amountFormate(imputedEmi, formater).replace(",", ""));

		// OwnerShip
		String ownerShip = customerExtLiability.getOwnerShip();
		list.add(ownerShip);

		// Last 24 Months
		boolean lastTwentyFourMonths = customerExtLiability.isLastTwentyFourMonths();
		list.add(lastTwentyFourMonths ? "true" : "false");

		// Last 6 Months
		boolean lastSixMonths = customerExtLiability.isLastSixMonths();
		list.add(lastSixMonths ? "true" : "false");

		// Last 3 Months
		boolean lastThreeMonths = customerExtLiability.isLastThreeMonths();
		list.add(lastThreeMonths ? "true" : "false");

		// Current OverDue
		BigDecimal currentOverDue = customerExtLiability.getCurrentOverDue();
		list.add(PennantApplicationUtil.amountFormate(currentOverDue, formater).replace(",", ""));

		// MOB
		int mob = customerExtLiability.getMob();
		list.add(String.valueOf(mob));

		// Remarks
		String remarks = customerExtLiability.getRemarks();
		list.add(String.valueOf(remarks));

		// RTR data
		if (paymentdetails != null) {
			list.add(paymentdetails.getEmiType());
			list.add(paymentdetails.getEmiClearance());
			list.add(String.valueOf(paymentdetails.getEmiClearedDay()));
		} else {
			list.add(" ");
			list.add(" ");
			list.add(" ");
		}
		return list;
	}

	/**
	 * This method will prepare a xl for a given liabilities
	 * 
	 * @param customerExtLiabilities
	 * @return
	 */
	private byte[] processExternalLiabilitiesForXLDownload(List<CustomerExtLiability> customerExtLiabilities) {
		FileInputStream file = null;
		Workbook workbook = null;
		Sheet sheet = null;
		String path = PathUtil.getPath(PathUtil.ExtLiability) + "/ExternalLiabilities.xlsx";
		try {
			// Reading the template
			file = new FileInputStream(new File(path));
			workbook = new XSSFWorkbook(file);
			// Getting the external liability sheet at index 0
			sheet = workbook.getSheetAt(0);
			if (sheet == null) {
				MessageUtil.showError(Labels.getLabel("label_ValidatedUploadFile"));
				return null;
			} else {
				// writing the data to existing template
				createExternalLiabilitiesSheet(sheet, customerExtLiabilities);
				// Here we need to prepare master data
				createProductSheet(workbook);// sheet at index 1
				createBankMasterSheet(workbook);// sheet at index 2
				createStatusSheet(workbook);// sheet at index 3
				createSourceInfoSheet(workbook);// sheet at index 4
				createTrackCheckFromSheet(workbook);// sheet at index 5
				createEMIClearanceSheet(workbook);// sheet at index 6
				file.close();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				workbook.write(baos);
				return baos.toByteArray();
			}
		} catch (FileNotFoundException e) {
			MessageUtil.showError(Labels.getLabel("label_File_Not_Exists_Message").concat(path));
			return null;
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (workbook != null) {
					workbook.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * This Method will Source of info sheet
	 * 
	 * @param workbook
	 */
	private void createSourceInfoSheet(Workbook workbook) {
		// getting the source of info sheet at index
		Sheet sheet = workbook.getSheetAt(4);
		if (sheet == null) {
			return;
		}
		List<ValueLabel> valueLabels = PennantStaticListUtil.getSourceInfoList();
		int rowcount = 0;
		Row row = null;
		if (CollectionUtils.isNotEmpty(valueLabels)) {
			for (ValueLabel valueLabel : valueLabels) {
				row = sheet.createRow(++rowcount);
				// code
				createCell(row, 0, valueLabel.getValue());
				// description
				createCell(row, 1, valueLabel.getLabel());
			}
		}
	}

	/**
	 * This Method will prepare track check from sheet
	 * 
	 * @param workbook
	 */
	private void createTrackCheckFromSheet(Workbook workbook) {
		Sheet trackSheet = workbook.getSheetAt(5);
		if (trackSheet == null) {
			return;
		}
		List<ValueLabel> valueLabels = PennantStaticListUtil.getTrackCheckList();
		int rowcount = 0;
		Row row = null;
		if (CollectionUtils.isNotEmpty(valueLabels)) {
			for (ValueLabel valueLabel : valueLabels) {
				row = trackSheet.createRow(++rowcount);
				// code
				createCell(row, 0, valueLabel.getValue());
				// description
				createCell(row, 1, valueLabel.getLabel());
			}
		}
	}

	/**
	 * This method will create a Status sheet for an workbook
	 * 
	 * @param workbook
	 */
	private void createStatusSheet(Workbook workbook) {
		Sheet statusSheet = workbook.getSheetAt(3);
		if (statusSheet == null) {
			return;
		}
		// get the status data
		JdbcSearchObject<CustomerStatusCode> searchObject = new JdbcSearchObject<CustomerStatusCode>(
				CustomerStatusCode.class);
		searchObject.addField("CustStsCode");
		searchObject.addField("CustStsDescription");
		searchObject.addTabelName("BMTCustStatusCodes_AView");
		List<CustomerStatusCode> customerStatusCodes = pagedListService.getBySearchObject(searchObject);
		int rowcount = 0;
		Row row = null;
		if (CollectionUtils.isNotEmpty(customerStatusCodes)) {
			for (CustomerStatusCode customerStatusCode : customerStatusCodes) {
				row = statusSheet.createRow(++rowcount);
				// code
				createCell(row, 0, customerStatusCode.getCustStsCode());
				// description
				createCell(row, 1, customerStatusCode.getCustStsDescription());
			}
		}
	}

	/**
	 * This method will create a Financier_RepaymentAccountBankName sheet for an workbook
	 * 
	 * @param workbook
	 */
	private void createBankMasterSheet(Workbook workbook) {
		Sheet financierSheet = workbook.getSheetAt(2);
		if (financierSheet == null) {
			return;
		}
		// get the bank details data
		JdbcSearchObject<BankDetail> searchObject = new JdbcSearchObject<BankDetail>(BankDetail.class);
		searchObject.addField("BankCode");
		searchObject.addField("BankName");
		searchObject.addTabelName("BMTBankDetail_AView");
		List<BankDetail> bankDetails = pagedListService.getBySearchObject(searchObject);
		int rowcount = 0;
		Row row = null;
		if (CollectionUtils.isNotEmpty(bankDetails)) {
			for (BankDetail bankDetail : bankDetails) {
				row = financierSheet.createRow(++rowcount);
				// bank code code
				createCell(row, 0, bankDetail.getBankCode());
				// bank name
				createCell(row, 1, bankDetail.getBankName());
			}
		}
	}

	/**
	 * This method will create a Product sheet for an workbook
	 * 
	 * @param workbook
	 */
	private void createProductSheet(Workbook workbook) {
		Sheet productSheet = workbook.getSheetAt(1);
		if (productSheet == null) {
			return;
		}
		// get the loan type data
		JdbcSearchObject<OtherBankFinanceType> searchObject = new JdbcSearchObject<OtherBankFinanceType>(
				OtherBankFinanceType.class);
		searchObject.addField("FinType");
		searchObject.addField("FinTypeDesc");
		searchObject.addTabelName("OtherBankFinanceType_AView");
		List<OtherBankFinanceType> financeTypes = pagedListService.getBySearchObject(searchObject);
		int rowcount = 0;
		// header
		Row row = null;
		if (CollectionUtils.isNotEmpty(financeTypes)) {
			for (OtherBankFinanceType financeType : financeTypes) {
				row = productSheet.createRow(++rowcount);
				// code
				createCell(row, 0, financeType.getFinType());
				// description
				createCell(row, 1, financeType.getFinTypeDesc());
			}
		}
	}

	/**
	 * This method will create a Clearance sheet for an workbook
	 * 
	 * @param workbook
	 */
	private void createEMIClearanceSheet(Workbook workbook) {
		Sheet emiClearanceSheet = workbook.getSheetAt(6);
		if (emiClearanceSheet == null) {
			return;
		}
		List<ValueLabel> valueLabels = PennantStaticListUtil.getEmiClearance();
		int rowcount = 0;
		Row row = null;
		if (CollectionUtils.isNotEmpty(valueLabels)) {
			for (ValueLabel valueLabel : valueLabels) {
				row = emiClearanceSheet.createRow(++rowcount);
				// code
				createCell(row, 0, valueLabel.getValue());
				// description
				createCell(row, 1, valueLabel.getLabel());
			}
		}
	}

	/**
	 * Prepare external liability data
	 * 
	 * @param sheet
	 * @param customerExtLiabilities
	 */
	private void createExternalLiabilitiesSheet(Sheet sheet, List<CustomerExtLiability> customerExtLiabilities) {
		int rowCount = 0;
		List<String> values = null;
		if (CollectionUtils.isNotEmpty(customerExtLiabilities)) {
			for (CustomerExtLiability extLiability : customerExtLiabilities) {
				// getting the default RTR upload list based on app date and loan start date for each record
				List<ExtLiabilityPaymentdetails> paymentDetails = getPaymentDetails(extLiability);
				if (CollectionUtils.isNotEmpty(paymentDetails)) {
					// as per the UD we are not considering existing RTR details, so we are setting the default RTR's
					extLiability.setExtLiabilitiesPayments(paymentDetails);
					for (ExtLiabilityPaymentdetails paymentdetails : paymentDetails) {
						if (PennantConstants.RCD_STATUS_APPROVED.equals(extLiability.getRecordStatus())) {
							Row row = sheet.createRow(++rowCount);
							// preparing the list of values with format
							values = prepareLiabilityValuesFromObject(extLiability, paymentdetails);
							writeData(row, values);
						}

					}
				}
			}
		}
	}

	/**
	 * Write Data into each cell
	 * 
	 * @param row
	 * @param cellValues
	 */
	private static void writeData(Row row, List<String> cellValues) {
		int columnCount = -1;
		for (String value : cellValues) {
			createCell(row, ++columnCount, value);
		}
	}

	/**
	 * Create a Cell for a Row
	 * 
	 * @param row
	 * @param columnCount
	 * @param field
	 */
	private static void createCell(Row row, int columnCount, String field) {
		Cell cell = row.createCell(columnCount);
		cell.setCellType(CellType.STRING);
		cell.setCellValue(field);
	}

	/**
	 * prepare a RTR payment track
	 * 
	 * @param customerExtLiability
	 * @return
	 */
	public static List<ExtLiabilityPaymentdetails> getPaymentDetails(CustomerExtLiability customerExtLiability) {
		// getting the emi list between app date and loan start date
		Date dtStartDate = com.pennant.app.util.DateUtility.addMonths(customerExtLiability.getFinDate(), -1);
		Date dtEndDate = com.pennant.app.util.DateUtility.addMonths(dtStartDate, -6);
		List<ExtLiabilityPaymentdetails> months = getFrequency(dtStartDate, dtEndDate);
		return months;
	}

	/**
	 * Retrieve the Repayments Frequency from start date and end date
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private static List<ExtLiabilityPaymentdetails> getFrequency(final Date startDate, final Date endDate) {
		List<ExtLiabilityPaymentdetails> list = new ArrayList<>();
		if (startDate == null || endDate == null) {
			return list;
		}
		Date tempStartDate = (Date) startDate.clone();
		Date tempEndDate = (Date) endDate.clone();
		while (DateUtil.compare(tempStartDate, tempEndDate) > 0) {
			ExtLiabilityPaymentdetails temp = new ExtLiabilityPaymentdetails();
			String key = DateUtil.format(tempStartDate, DateFormat.LONG_MONTH);
			temp.setEmiType(key);
			tempStartDate = DateUtil.addMonths(tempStartDate, -1);
			list.add(temp);
		}
		return list;
	}

	private void extendedComboValidations(CustomerExtLiability custExt) {
		int count = 0;

		String finStatus = custExt.getFinStatus();
		String finType = custExt.getFinType();
		String loanBank = custExt.getLoanBank();
		String repayBank = custExt.getRepayBank();
		String loanPurpose = custExt.getLoanPurpose();
		String checkedBy = Integer.toString(custExt.getCheckedBy());
		String extSource = Integer.toString(custExt.getSource());

		StringBuilder errorMessage = new StringBuilder();

		String sql = "";
		if (StringUtils.isNotEmpty(finStatus)) {
			sql = "Select Coalesce(Count(*), 0) from BMTCustStatusCodes_AView Where Custstscode = ? and CuststsIsActive = ?";
			if (!customerExtLiabilityDAO.getExtendedComboData(sql, finStatus)) {
				errorMessage.append("Status,");
			}
		}

		if (StringUtils.isNotEmpty(finType)) {
			sql = "Select Coalesce(Count(*), 0) from OtherBankFinanceType_AView Where FinType = ? and Active = ?";
			if (!customerExtLiabilityDAO.getExtendedComboData(sql, finType)) {
				errorMessage.append(" Product,");
			}
		}

		if (StringUtils.isNotEmpty(loanBank)) {
			sql = "Select Coalesce(Count(*), 0) from BMTBankDetail_AView Where BankCode = ? and Active = ?";
			if (!customerExtLiabilityDAO.getExtendedComboData(sql, loanBank)) {
				errorMessage.append(" Financer Name/Bank Name,");
			}
		}

		if (StringUtils.isNotEmpty(repayBank)) {
			sql = "Select Coalesce(Count(*), 0) from BMTBankDetail_AView Where BankCode = ? and Active = ?";
			if (!customerExtLiabilityDAO.getExtendedComboData(sql, repayBank)) {
				errorMessage.append(" Repayment from/Repayment Account Bank Name,");
			}
		}

		if (StringUtils.isNotEmpty(loanPurpose)) {
			sql = "Select Coalesce(Count(*), 0) from LoanPurposes_AView Where LoanPurposeCode = ? and LoanPurposeIsActive = ?";
			if (!customerExtLiabilityDAO.getExtendedComboData(sql, loanPurpose)) {
				errorMessage.append(" End Use of Funds/Loan Purpose,");
			}
		}

		for (ValueLabel source : sourceInfoList) {
			if (source.getValue().equals(extSource)) {
				count++;
				break;
			}
		}

		if (count == 0) {
			errorMessage.append(" Source Of Info,");
		}

		count = 0;
		for (ValueLabel trackCheck : trackCheckList) {
			if (trackCheck.getValue().equals(checkedBy)) {
				count++;
				break;
			}
		}

		if (count == 0) {
			errorMessage.append(" Track Check,");
		}

		Date appDate = SysParamUtil.getAppDate();
		if (DateUtil.compare(custExt.getFinDate(), appDate) > 0) {
			errorMessage.append(" Loan Date should on/before Application Date,");
		}

		if (StringUtils.isNotEmpty(errorMessage.toString())) {
			errorMessage.deleteCharAt(errorMessage.length() - 1);
			errorMessage.append(" : Given Fields data is invalid.");

			throw new WrongValueException(errorMessage.toString());
		}
	}

	public void setCustomerExtLiabilityDAO(CustomerExtLiabilityDAO customerExtLiabilityDAO) {
		this.customerExtLiabilityDAO = customerExtLiabilityDAO;
	}

	public void setExternalLiabilityDAO(ExternalLiabilityDAO externalLiabilityDAO) {
		this.externalLiabilityDAO = externalLiabilityDAO;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public void setBankDetailDAO(BankDetailDAO bankDetailDAO) {
		this.bankDetailDAO = bankDetailDAO;
	}

}
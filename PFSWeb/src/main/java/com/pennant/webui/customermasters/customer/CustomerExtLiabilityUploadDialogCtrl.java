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
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.spring.SpringUtil;
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
import com.pennant.app.util.PathUtil;
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
import com.pennant.equation.util.DateUtility;
import com.pennant.webui.util.GFCBaseCtrl;
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
	Map<String, CustomerExtLiability> map = new HashMap<String, CustomerExtLiability>();
	@Autowired
	private CustomerExtLiabilityDAO customerExtLiabilityDAO;
	@Autowired
	private ExternalLiabilityDAO externalLiabilityDAO;
	protected transient boolean enqiryModule;
	private static int columnsize = 37;
	private int formater = CurrencyUtil.getFormat("");
	private PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
	private DataFormatter objDefaultFormat = new DataFormatter();// for cell value formating
	private FormulaEvaluator formulaEvaluator = null; // for cell value formating
	private CustomerDialogCtrl customerDialogCtrl;

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
		Workbook workBook = null;
		try {
			if (media.getName().toLowerCase().endsWith(".xls")) {
				try {
					workBook = new HSSFWorkbook(media.getStreamData());
					this.formulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workBook);
				} catch (OfficeXmlFileException e) {
					//due to some xl version issue we have to use	
					workBook = new XSSFWorkbook(media.getStreamData());
					this.formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workBook);
				}
			} else if (media.getName().toLowerCase().endsWith(".xlsx")) {
				workBook = new XSSFWorkbook(media.getStreamData());
				this.formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workBook);
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
		//if it is empty sheet return
		if (myExcelSheet == null) {
			MessageUtil.showError(Labels.getLabel("label_ValidatedUploadFile"));
			return;
		}
		//only header column is available with out data return
		int rowCount = myExcelSheet.getPhysicalNumberOfRows();
		if (rowCount <= 1) {
			MessageUtil.showError(Labels.getLabel("label_ValidatedUploadFile"));
			return;
		}

		Iterator<Row> rows = myExcelSheet.iterator();
		String data[] = new String[myExcelSheet.getRow(0).getPhysicalNumberOfCells()];
		int seqNo = 0;
		while (rows.hasNext()) {
			Row row = rows.next();
			if (row.getRowNum() == 0) {
				continue;
			}
			int cellCount = -1;
			//for list maintenance
			seqNo += 1;
			Iterator<Cell> cells = row.cellIterator();
			//reading each row and preparing the list of values
			while (cells.hasNext()) {
				data[++cellCount] = this.objDefaultFormat.formatCellValue(cells.next(), this.formulaEvaluator);
			}
			//This method will create  
			prepareLiabilityList(data, map, seqNo);
		}
		workBook.close();

		//save the data
		String msg = Labels.getLabel("label_FileUpload_Save_Confirmation");
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			doSave();
		}
	}

	/**
	 * This method will create a external liability object with passed values
	 * 
	 * @param data
	 * @param map
	 * @param seqNo
	 */
	private void prepareLiabilityList(String[] data, Map<String, CustomerExtLiability> map, int seqNo) {
		if (data != null && data.length == columnsize) {
			if (!map.containsKey(data[0])) {
				//Preparing CustomerExtLiability Data
				CustomerExtLiability customerExtLiability = new CustomerExtLiability();
				customerExtLiability.setSeqNo(seqNo);
				customerExtLiability.setCustId(custId);
				prepareLiabilityData(customerExtLiability, data);
				//Here we have to set RTR data
				prepareRTRData(customerExtLiability, data);
				map.put(data[0], customerExtLiability);
			} else if (map.containsKey(data[0])) {
				CustomerExtLiability customerExtLiability = map.get(data[0]);
				//Only need to set RTR data for Duplicate Liability Record
				prepareRTRData(customerExtLiability, data);
			}
		}

	}

	/**
	 * This method will set the data to CustomerExtLiability object
	 * 
	 * @param customerExtLiability
	 * @param data
	 */
	private void prepareLiabilityData(CustomerExtLiability customerExtLiability, String[] data) {
		//Product
		customerExtLiability.setFinType(data[1]);

		//Financer Name
		customerExtLiability.setLoanBank(data[2]);

		//Other Financial Institution
		customerExtLiability.setOtherFinInstitute(data[3]);

		//Installment Amount/EMI
		customerExtLiability.setInstalmentAmount(PennantApplicationUtil.unFormateAmount(data[4], formater));

		//Outstanding Balance/Limit
		customerExtLiability.setOutstandingBalance(PennantApplicationUtil.unFormateAmount(data[5], formater));

		//Loan Amount
		customerExtLiability.setOriginalAmount(PennantApplicationUtil.unFormateAmount(data[6], formater));

		//Loan Date
		customerExtLiability.setFinDate(DateUtil.parse(data[7], DateFormat.SHORT_DATE));

		//Status
		customerExtLiability.setFinStatus(data[8]);

		//ROI
		customerExtLiability.setRateOfInterest(PennantApplicationUtil.unFormateAmount(data[9], formater));

		//Loan Tenure
		customerExtLiability.setTenure(Integer.valueOf(data[10]));

		//Balance Tenure
		customerExtLiability.setBalanceTenure(Integer.valueOf(data[11]));

		//Number of Bounces in last 3 months
		customerExtLiability.setBounceInstalments(Integer.valueOf(data[12]));

		//Number of Bounces in last 6 months
		customerExtLiability.setNoOfBouncesInSixMonths(Integer.valueOf(data[13]));

		//Number of Bounces in last 12 months
		customerExtLiability.setNoOfBouncesInTwelveMonths(Integer.valueOf(data[14]));

		//POS
		customerExtLiability.setPrincipalOutstanding(PennantApplicationUtil.unFormateAmount(data[15], formater));

		//Overdue
		customerExtLiability.setOverdueAmount(PennantApplicationUtil.unFormateAmount(data[16], formater));

		//EMI Considered for FOIR
		customerExtLiability.setFoir("true".equalsIgnoreCase(data[17]) ? true : false);//data[17]

		//Source of Info
		customerExtLiability.setSource(Integer.valueOf(data[18]));

		//Track Check from
		customerExtLiability.setCheckedBy(Integer.valueOf(data[19]));

		//Security Details/Property details
		customerExtLiability.setSecurityDetails(data[20]);

		//End Use of Funds
		customerExtLiability.setLoanPurpose(data[21]);

		//Repayment Account Bank Name
		customerExtLiability.setRepayBank(data[22]);

		//Repayment Bank Account No
		customerExtLiability.setRepayFromAccNo(data[23]);

		//Considered for RTR based loan
		customerExtLiability.setConsideredBasedOnRTR("true".equalsIgnoreCase(data[24]) ? true : false);//data[24]

		//Number of Installments for RTR

		//Imputed EMI
		customerExtLiability.setImputedEmi(PennantApplicationUtil.unFormateAmount(data[26], formater));

		//OwnerShip
		customerExtLiability.setOwnerShip(data[27]);

		//Last 24 Months
		customerExtLiability.setLastTwentyFourMonths("true".equalsIgnoreCase(data[28]) ? true : false);//data[28]

		//Last 6 Months
		customerExtLiability.setLastSixMonths("true".equalsIgnoreCase(data[29]) ? true : false);//data[29]

		//Last 3 Months
		customerExtLiability.setLastThreeMonths("true".equalsIgnoreCase(data[30]) ? true : false);//data[30]

		//Current OverDue
		customerExtLiability.setCurrentOverDue(PennantApplicationUtil.unFormateAmount(data[31], formater));

		//MOB
		if (StringUtils.isNotBlank(data[32])) {
			customerExtLiability.setMob(Integer.valueOf(data[32]));
		}

		//Remarks
		customerExtLiability.setRemarks(data[33]);

		//workflow data
		customerExtLiability.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		customerExtLiability.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		customerExtLiability.setUserDetails(getUserWorkspace().getLoggedInUser());
		customerExtLiability.setVersion(customerExtLiability.getVersion() + 1);
		customerExtLiability.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
	}

	/**
	 * This method will set the data to ExtLiabilityPaymentdetails object
	 * 
	 * @param customerExtLiability
	 * @param data
	 */
	private void prepareRTRData(CustomerExtLiability customerExtLiability, String[] data) {
		ExtLiabilityPaymentdetails paymentdetails = new ExtLiabilityPaymentdetails();
		//Month
		paymentdetails.setEmiType(data[34]);
		//Cleared
		paymentdetails.setEmiClearance(data[35]);
		//Cleared Day
		if (StringUtils.isNotBlank(data[36])) {
			paymentdetails.setEmiClearedDay(Integer.valueOf(data[36]));
		}
		//workflow data
		paymentdetails.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		paymentdetails.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		paymentdetails.setUserDetails(getUserWorkspace().getLoggedInUser());
		paymentdetails.setVersion(paymentdetails.getVersion() + 1);
		paymentdetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		customerExtLiability.getExtLiabilitiesPayments().add(paymentdetails);
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		//doSave();
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
						//setting the linkId
						customerExtLiabilityDAO.setLinkId(customerExtLiability);
						if (!flag) {
							//delete the existing data first while upload 
							externalLiabilityDAO.deleteByLinkId(customerExtLiability.getLinkId(), "");
							//delete the existing data first while upload 
							customerExtLiabilityDAO.delete(customerExtLiability.getLinkId(), "");
							flag = true;
						}
						//Saving the Data
						externalLiabilityDAO.save(customerExtLiability, "");
						if (customerExtLiability.getExtLiabilitiesPayments() != null
								&& !customerExtLiability.getExtLiabilitiesPayments().isEmpty()) {
							for (ExtLiabilityPaymentdetails extLiabilityPaymentdetails : customerExtLiability
									.getExtLiabilitiesPayments()) {
								extLiabilityPaymentdetails.setLiabilityId(customerExtLiability.getId());
							}
							//saving the list
							customerExtLiabilityDAO.save(customerExtLiability.getExtLiabilitiesPayments(), "");
						}
					}
				}
				//Displaying the success message
				MessageUtil.showMessage(Labels.getLabel("label_ValidatedDataSaved"));
				//Data retrieving to render the data in liability list
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
				//Refreshing the list box data after successful upload
				customerDialogCtrl.doFillCustomerExtLiabilityDetails(customerExtLiabilities);
				//close the dialog after save
				closeDialog();
				logger.debug(Literal.LEAVING);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
		//Product
		list.add(customerExtLiability.getFinType());

		//Financer Name
		list.add(customerExtLiability.getLoanBank());

		//Other Financial Institution
		list.add(customerExtLiability.getOtherFinInstitute());

		//Installment Amount/EMI
		BigDecimal instalmentAmount = customerExtLiability.getInstalmentAmount();
		list.add(PennantApplicationUtil.amountFormate(instalmentAmount, formater).replace(",", ""));

		//Outstanding Balance/Limit
		BigDecimal outstandingBalance = customerExtLiability.getOutstandingBalance();
		list.add(PennantApplicationUtil.amountFormate(outstandingBalance, formater).replace(",", ""));

		//Loan Amount
		BigDecimal originalAmount = customerExtLiability.getOriginalAmount();
		list.add(PennantApplicationUtil.amountFormate(originalAmount, formater).replace(",", ""));

		//Loan Date
		Date finDate = customerExtLiability.getFinDate();
		if (finDate != null && DateUtility.formatDate(finDate, "dd-MM-yyyy") != null) {
			list.add(DateUtility.formatDate(finDate, "dd-MM-yyyy"));
		} else {
			list.add(" ");
		}

		//Status
		String finStatus = customerExtLiability.getFinStatus();
		list.add(finStatus);

		//ROI
		BigDecimal rateOfInterest = customerExtLiability.getRateOfInterest();
		list.add(PennantApplicationUtil.amountFormate(rateOfInterest, formater));

		//Loan Tenure
		int tenure = customerExtLiability.getTenure();
		list.add(String.valueOf(tenure));

		//Balance Tenure
		int balanceTenure = customerExtLiability.getBalanceTenure();
		list.add(String.valueOf(balanceTenure));

		//Number of Bounces in last 3 months
		int bounceInstalments = customerExtLiability.getBounceInstalments();
		list.add(String.valueOf(bounceInstalments));

		//Number of Bounces in last 6 months
		int noOfBouncesInSixMonths = customerExtLiability.getNoOfBouncesInSixMonths();
		list.add(String.valueOf(noOfBouncesInSixMonths));

		//Number of Bounces in last 12 months
		int noOfBouncesInTwelveMonths = customerExtLiability.getNoOfBouncesInTwelveMonths();
		list.add(String.valueOf(noOfBouncesInTwelveMonths));

		//POS
		BigDecimal principalOutstanding = customerExtLiability.getPrincipalOutstanding();
		list.add(PennantApplicationUtil.amountFormate(principalOutstanding, formater).replace(",", ""));

		//Overdue
		BigDecimal overdueAmount = customerExtLiability.getOverdueAmount();
		list.add(PennantApplicationUtil.amountFormate(overdueAmount, formater).replace(",", ""));

		//EMI Considered for FOIR
		boolean foir = customerExtLiability.isFoir();

		list.add(foir ? "true" : "false");

		//Source of Info
		int source = customerExtLiability.getSource();
		list.add(String.valueOf(source));

		//Track Check from
		int checkedBy = customerExtLiability.getCheckedBy();
		list.add(String.valueOf(checkedBy));

		//Security Details/Property details
		String securityDetails = customerExtLiability.getSecurityDetails();
		list.add(String.valueOf(securityDetails));

		//End Use of Funds
		String loanPurpose = customerExtLiability.getLoanPurpose();
		list.add(String.valueOf(loanPurpose));

		//Repayment Account Bank Name
		String repayBank = customerExtLiability.getRepayBank();
		list.add(String.valueOf(repayBank));

		//Repayment Bank Account No
		String repayFromAccNo = customerExtLiability.getRepayFromAccNo();
		list.add(String.valueOf(repayFromAccNo));

		//Considered for RTR based loan
		boolean consideredBasedOnRTR = customerExtLiability.isConsideredBasedOnRTR();
		list.add(consideredBasedOnRTR ? "true" : "false");

		//Number of Installments for RTR
		int size = customerExtLiability.getExtLiabilitiesPayments().size();
		list.add(String.valueOf(size));

		//Imputed EMI
		BigDecimal imputedEmi = customerExtLiability.getImputedEmi();
		list.add(PennantApplicationUtil.amountFormate(imputedEmi, formater).replace(",", ""));

		//OwnerShip
		String ownerShip = customerExtLiability.getOwnerShip();
		list.add(ownerShip);

		//Last 24 Months
		boolean lastTwentyFourMonths = customerExtLiability.isLastTwentyFourMonths();
		list.add(lastTwentyFourMonths ? "true" : "false");

		//Last 6 Months
		boolean lastSixMonths = customerExtLiability.isLastSixMonths();
		list.add(lastSixMonths ? "true" : "false");

		//Last 3 Months
		boolean lastThreeMonths = customerExtLiability.isLastThreeMonths();
		list.add(lastThreeMonths ? "true" : "false");

		//Current OverDue
		BigDecimal currentOverDue = customerExtLiability.getCurrentOverDue();
		list.add(PennantApplicationUtil.amountFormate(currentOverDue, formater).replace(",", ""));

		//MOB
		int mob = customerExtLiability.getMob();
		list.add(String.valueOf(mob));

		//Remarks
		String remarks = customerExtLiability.getRemarks();
		list.add(String.valueOf(remarks));

		//RTR data
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
			//Reading the template
			file = new FileInputStream(new File(path));
			workbook = new XSSFWorkbook(file);
			//Getting the external liability sheet at index 0
			sheet = workbook.getSheetAt(0);
			if (sheet == null) {
				MessageUtil.showError(Labels.getLabel("label_ValidatedUploadFile"));
				return null;
			} else {
				//writing the data to existing template
				createExternalLiabilitiesSheet(sheet, customerExtLiabilities);
				//Here we need to prepare master data
				createProductSheet(workbook);//sheet at index 1
				createBankMasterSheet(workbook);//sheet at index 2
				createStatusSheet(workbook);//sheet at index 3
				createSourceInfoSheet(workbook);//sheet at index 4
				createTrackCheckFromSheet(workbook);//sheet at index 5
				createEMIClearanceSheet(workbook);//sheet at index 6
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
		//getting the source of info sheet at index
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
				//code
				createCell(row, 0, valueLabel.getValue());
				//description
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
				//code
				createCell(row, 0, valueLabel.getValue());
				//description
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
		//get the status data
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
				//code
				createCell(row, 0, customerStatusCode.getCustStsCode());
				//description
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
		//get the bank details data
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
				//bank code code
				createCell(row, 0, bankDetail.getBankCode());
				//bank name
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
		//get the loan type data
		JdbcSearchObject<OtherBankFinanceType> searchObject = new JdbcSearchObject<OtherBankFinanceType>(
				OtherBankFinanceType.class);
		searchObject.addField("FinType");
		searchObject.addField("FinTypeDesc");
		searchObject.addTabelName("OtherBankFinanceType_AView");
		List<OtherBankFinanceType> financeTypes = pagedListService.getBySearchObject(searchObject);
		int rowcount = 0;
		//header
		Row row = null;
		if (CollectionUtils.isNotEmpty(financeTypes)) {
			for (OtherBankFinanceType financeType : financeTypes) {
				row = productSheet.createRow(++rowcount);
				//code
				createCell(row, 0, financeType.getFinType());
				//description
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
				//code
				createCell(row, 0, valueLabel.getValue());
				//description
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
				//getting the default RTR upload list based on app date and loan start date for each record
				List<ExtLiabilityPaymentdetails> paymentDetails = getPaymentDetails(extLiability);
				if (CollectionUtils.isNotEmpty(paymentDetails)) {
					// as per the UD we are not considering existing RTR details, so we are setting the default RTR's
					extLiability.setExtLiabilitiesPayments(paymentDetails);
					for (ExtLiabilityPaymentdetails paymentdetails : paymentDetails) {
						if (PennantConstants.RCD_STATUS_APPROVED.equals(extLiability.getRecordStatus())) {
							Row row = sheet.createRow(++rowCount);
							//preparing the list of values with format
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
		//getting the emi list between app date and loan start date
		Date dtStartDate = com.pennant.app.util.DateUtility.addMonths(customerExtLiability.getFinDate(), 1);
		Date dtEndDate = com.pennant.app.util.DateUtility.addMonths(dtStartDate, 6);
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
		while (com.pennant.app.util.DateUtility.compare(tempStartDate, tempEndDate) < 0) {
			ExtLiabilityPaymentdetails temp = new ExtLiabilityPaymentdetails();
			String key = DateUtil.format(tempStartDate, DateFormat.LONG_MONTH);
			temp.setEmiType(key);
			tempStartDate = DateUtil.addMonths(tempStartDate, 1);
			list.add(temp);
		}
		return list;
	}
}
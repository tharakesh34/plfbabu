package com.pennant.webui.finance.expenses;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.expenses.FinExpenseDetails;
import com.pennant.backend.model.expenses.FinExpenseMovements;
import com.pennant.backend.model.expenses.UploadFinExpenses;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.UploadHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.ReportGenerationUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.interfacebajaj.fileextract.service.ExcelFileImport;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;


public class ExpenseUplaodCtrl extends GFCBaseCtrl<UploadHeader> {

	private static final long serialVersionUID = 4783031677099154138L;
	private static final Logger logger = Logger.getLogger(ExpenseUplaodCtrl.class);

	protected Window window_ExpenseUpload;
	
	protected Button btnBrowse;
	protected Button btnSave;
	protected Button btnRefresh;
	protected Button btndownload;
	
	protected Textbox txtFileName;
	
	protected Row panelRow;
	
	protected Combobox moduleType;
	
	protected Label fileName;
	protected Label totalCount;
	protected Label successCount;
	protected Label failedCount;
	
	protected Grid statusGrid;
	
	private Workbook workbook 					= null;
	private DataFormatter objDefaultFormat 		= new DataFormatter();// for cell value formating
	private FormulaEvaluator formulaEvaluator 	= null; // for cell value formating
	private String errorMsg 					= null;
	private ExcelFileImport fileImport 			= null;
	
	private UploadHeader uploadHeader 			= new UploadHeader();
	private UploadHeaderService uploadHeaderService;

	public ExpenseUplaodCtrl() {
		super();
	}
	
	@Override
	protected void doSetProperties() {
		super.pageRightName = "ExpenseUpload";
	}


	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected Customer object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ExpenseUpload(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(this.window_ExpenseUpload);
		
		try {
			
			// Store the before image.
			UploadHeader uploadHeader = new UploadHeader();
			BeanUtils.copyProperties(this.uploadHeader, uploadHeader);
			this.uploadHeader.setBefImage(uploadHeader);
			
			// Render the page and display the data.
			doLoadWorkFlow(this.uploadHeader.isWorkflow(), this.uploadHeader.getWorkflowId(), this.uploadHeader.getNextTaskId());
			
			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}
			
			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.uploadHeader);
			
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}
	
	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		this.statusGrid.setVisible(false);

		logger.debug("Leaving");
	}
	
	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		
		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
		
		this.btnBrowse.setVisible(getUserWorkspace().isAllowed("button_ExpenseUpload_Browse"));
		this.btnRefresh.setVisible(getUserWorkspace().isAllowed("button_ExpenseUpload_Refresh"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ExpenseUpload_Save"));
		//this.btndownload.setVisible(getUserWorkspace().isAllowed("button_ExpenseUpload_Report"));

		logger.debug("Leaving");
	}
	
	/**
	 * Displays the dialog page.
	 * 
	 * @param aAcademic
	 *            The entity that need to be render.
	 */
	public void doShowDialog(UploadHeader uploadHeader) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (uploadHeader.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.moduleType.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.moduleType.focus();
				if (StringUtils.isNotBlank(uploadHeader.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				//this.btnCtrl.setInitEdit();
				//doReadOnly();
				//btnCancel.setVisible(false);
			}
		}
		
		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
		}
		
		// fill the components with the data
		doWriteBeanToComponents(uploadHeader);
		
		//setDialog(DialogType.EMBEDDED);

		logger.debug("Leaving");
	}
	
	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param academic
	 * 
	 */
	public void doWriteBeanToComponents(UploadHeader uploadHeader) {
		logger.debug("Entering");

		fillComboBox(this.moduleType, uploadHeader.getModule(), PennantStaticListUtil.getUploadLevelsList(), "");

		logger.debug("Leaving");
	}
	
	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (this.uploadHeader.isNew()) {
			readOnlyComponent(false, this.moduleType);
			this.btnBrowse.setVisible(true);
			this.btnBrowse.setDisabled(false);
		}

		this.txtFileName.setReadonly(true);
		readOnlyComponent(isReadOnly("ExpenseUpload_UploadLevel"), this.moduleType);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.uploadHeader.isNew()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			//this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		
		readOnlyComponent(true, this.moduleType);
		this.txtFileName.setReadonly(true);
		
		if (isWorkFlowEnabled()) {
			
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}

		logger.debug("Leaving");
	}
	
	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");

		this.moduleType.setConstraint("");
		this.moduleType.setErrorMessage("");
		
		this.txtFileName.setConstraint("");
		this.txtFileName.setErrorMessage("");

		logger.debug("Leaving");
	}

	/**
	 * This Method/Event for getting the uploaded document should be comma separated values and then read the document
	 * and setting the values to the Lead VO and added those vos to the List and it also shows the information about
	 * where we go the wrong data
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onUpload$btnBrowse(UploadEvent event) throws Exception {
		logger.debug(Literal.ENTERING);

		this.txtFileName.setText("");
		this.fileName.setValue("");
		this.fileImport = null;
		this.errorMsg = null;
		
		doRemoveValidation();
		
		isValidComboValue(this.moduleType, Labels.getLabel("label_ExpenseUpload_UploadLevel.value"));
		
		readOnlyComponent(true, this.moduleType);
		
		Media media = event.getMedia();
		String fileName = media.getName();
		
		try {
			if (!(StringUtils.endsWith(fileName.toLowerCase(), ".xls") || StringUtils.endsWith(fileName.toLowerCase(), ".xlsx"))) {
				this.errorMsg = "The uploaded file could not be recognized. Please upload a valid excel file.";
				MessageUtil.showError(this.errorMsg);
				readOnlyComponent(false, this.moduleType);
				media = null;
				return;
			} else {
				String module = getComboboxValue(this.moduleType);
				String filePath = SysParamUtil.getValueAsString("UPLOAD_FILEPATH");
				filePath = filePath.concat(File.separator).concat(module);
				
				this.fileImport = new ExcelFileImport(media, filePath);
				this.txtFileName.setText(fileName);
				this.fileName.setValue(fileName);
			}
		} catch (Exception e) {
			this.errorMsg = e.getMessage();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * when the "Process" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btndownload(Event event) {
		logger.debug(Literal.ENTERING);

		String whereCond = " and FILENAME in (" + "'" + this.txtFileName.getValue() + "'" + ")";
		StringBuilder searchCriteria = new StringBuilder(" ");
		searchCriteria.append("File Name is " + this.txtFileName.getValue());

		String moduleType = getComboboxValue(this.moduleType);
		String reportName = "";

		if (PennantConstants.EXPENSE_UPLOAD_LOAN.equals(moduleType)) {
			reportName = "ExpenseReport_LoanLevel";
		} else if (PennantConstants.EXPENSE_UPLOAD_LOANTYPE.equals(moduleType)) {
			reportName = "ExpenseReport_LoanType";
		}

		ReportGenerationUtil.generateReport(getUserWorkspace().getLoggedInUser().getFullName(), reportName, whereCond,
				searchCriteria, this.window_ExpenseUpload, true);

		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * entry point of program, reading whole excel and calling other methods to prepare jsonObject.
	 * 
	 * @return String
	 * @throws Exception
	 */
	private List<UploadFinExpenses> processUploadFinExpenses(String moduleType, long uploadId) throws Exception {
		logger.debug("Entering");

		List<UploadFinExpenses> uploadFinExpensesList = new ArrayList<UploadFinExpenses>();
		
		if (this.workbook != null) {

			Sheet sheet = this.workbook.getSheetAt(0);

			Iterator<org.apache.poi.ss.usermodel.Row> rows = sheet.iterator();

			String finType 			= null;
			String fromDate			= null;
			String toDate			= null;
			String finReference 	= null;
			Date financeStartDate 	= null;
			Date financeEndDate 	= null;
			String expenseTypeCode 	= null;
			String percentage 		= null;
			String amountValue 		= null;
			String appendOrOverride = null;
			
			if (PennantConstants.EXPENSE_UPLOAD_LOANTYPE.equals(moduleType)) {
				
				while (rows.hasNext()) {

					org.apache.poi.ss.usermodel.Row row = rows.next();
					int rowIndex = row.getRowNum();

					if (rowIndex > 0) {

						List<String> rowValue = getRowValuesByIndex(this.workbook, 0, rowIndex);
						
						if (CollectionUtils.isNotEmpty(rowValue)) {
							
							String reason 		= null;	//reason of the failure
							boolean valid 		= true;	//verify the data
							
							finType 			= rowValue.get(0);
							fromDate 			= rowValue.get(1);
							toDate 				= rowValue.get(2);
							expenseTypeCode 	= rowValue.get(3);
							percentage 			= rowValue.get(4);
							amountValue 		= rowValue.get(5);
							appendOrOverride 	= rowValue.get(6);
							
							UploadFinExpenses uploadFinExpense = new UploadFinExpenses();
							uploadFinExpense.setUploadId(uploadId);
							uploadFinExpense.setFinType(finType);
							uploadFinExpense.setType(appendOrOverride);
							
							String dateFormat	=  DateFormat.LONG_DATE.getPattern();
							
							//Approval Start Date
							if (StringUtils.isBlank(fromDate)) {
								reason = "Approval Start Date is mandatory, it should be in " + dateFormat + " format.";
								valid  = false;
							} else {
								try {
									financeStartDate = getUtilDate(fromDate, dateFormat);
								} catch (ParseException e) {
									reason = "Invalid Approval Start Date, it should be in " + dateFormat + " format.";
									valid  = false;
								}
							}  
							
							//Approval End Date
							if (StringUtils.isBlank(toDate)) {
								if (valid) {
									reason = "Approval End Date is mandatory, it should be in " + dateFormat + " format.";
									valid  = false;
								} else {
									reason = reason + "| Approval End Date is mandatory, it should be in " + dateFormat + " format.";
								}
							} else {
								try {
									financeEndDate = getUtilDate(toDate, dateFormat);
								} catch (ParseException e) {
									if (valid) {
										reason = "Invalid Approval End Date, it should be in " + dateFormat + " format.";
										valid  = false;
									}  else {
										reason = reason + "| Invalid Approval End Date, it should be in " + dateFormat + " format.";
									}
								}
							}
							
							if (valid) {
								if (financeStartDate.compareTo(financeEndDate) > 0) {
									reason = "Invalid Approval End Date, it should be less than or equals to Approval Start Date.";
									valid  = false;
									financeEndDate = null;
								}
							}
							
							uploadFinExpense.setFinApprovalStartDate(financeStartDate);
							uploadFinExpense.setFinApprovalEndDate(financeEndDate);
							
							//Loan Type validation
							if (StringUtils.isBlank(uploadFinExpense.getFinType())) {
								if (valid) {
									reason = "Loan Type is mandatory.";
									valid  = false;
								} else {
									reason = reason + "| Loan Type is mandatory.";
								}
							} else {
								
								if (uploadFinExpense.getFinType().length() > 8) {
									if (valid) {
										reason = "Loan Type : (" +  uploadFinExpense.getFinType() + ") length is exceeded, it should be lessthan or equals to 8.";
										valid  = false;
									} else {
										reason = reason + "| Loan Type : (" +  uploadFinExpense.getFinType() + ") length is exceeded, it should be lessthan or equals to 8.";
									}
									
									uploadFinExpense.setFinType(uploadFinExpense.getFinType().substring(0, 8));
								} else {
									
									int count = this.uploadHeaderService.getFinTypeCount(uploadFinExpense.getFinType());
									
									if (count <= 0) {
										if (valid) {
											reason = "Loan Type : " +  uploadFinExpense.getFinType() + " is invalid.";
											valid  = false;
										} else {
											reason = reason + "| Loan Type : " +  uploadFinExpense.getFinType() + " is invalid.";
										}
									}
								}
							}
							
							//validate the common data
							valid = validateCommonData(uploadFinExpense, expenseTypeCode, percentage, amountValue, valid, reason);
							reason = uploadFinExpense.getReason();
							
							if (valid) {
								uploadFinExpense.setStatus(PennantConstants.UPLOAD_STATUS_SUCCESS);
							} else {
								uploadFinExpense.setStatus(PennantConstants.UPLOAD_STATUS_FAIL);
							}
							
							uploadFinExpense.setReason(reason);
							
							uploadFinExpensesList.add(uploadFinExpense);
						}
					}
				}
			} else if (PennantConstants.EXPENSE_UPLOAD_LOAN.equals(moduleType)) {
				
				while (rows.hasNext()) {

					org.apache.poi.ss.usermodel.Row row = rows.next();
					int rowIndex = row.getRowNum();

					if (rowIndex > 0) {

						List<String> rowValue = getRowValuesByIndex(this.workbook, 0, rowIndex);
						
						if (CollectionUtils.isNotEmpty(rowValue)) {
							
							String reason 		= null;	//reason of the failure
							boolean valid 		= true;	//verify the data
							
							finReference 		= rowValue.get(0);
							expenseTypeCode 	= rowValue.get(1);
							percentage 			= rowValue.get(2);
							amountValue 		= rowValue.get(3);
							appendOrOverride 	= rowValue.get(4);
							
							UploadFinExpenses uploadFinExpenses = new UploadFinExpenses();
							uploadFinExpenses.setUploadId(uploadId);
							uploadFinExpenses.setFinReference(finReference);
							uploadFinExpenses.setType(appendOrOverride);
							
							valid = validateCommonData(uploadFinExpenses, expenseTypeCode, percentage, amountValue, valid, reason);
							reason = uploadFinExpenses.getReason();
							
							//FinReference
							if (StringUtils.isBlank(finReference)) {
								if (valid) {
									valid  = false;
									reason = "Loan Reference is mandatory";
								} else {
									reason = reason + "| Loan Reference is mandatory";
								}
								uploadFinExpenses.setFinReference("FINREF");	//default value for finReference 
							} else if (finReference.length() > 20) {
								if (valid) {
									valid  = false;
									reason = "Expense Type Code : (" + finReference + ") length is exceeded, it should be lessthan or equal to 8.";
								} else {
									reason = reason + "| Expense Type Code : (" + finReference + ") length is exceeded, it should be lessthan or equal to 8.";
								}
								uploadFinExpenses.setFinReference(finReference.substring(0, 20));
							} else {
								int count = this.uploadHeaderService.getFinanceCountById(finReference);
								
								if (count != 1) {
									if (valid) {
										reason = "Loan Reference: (" + finReference + ") is not valid.";
										valid  = false;
									} else {
										reason = reason + "|Loan Reference: (" + finReference + ") is not valid.";
									}
								}
							}
							
							
							if (valid) {
								uploadFinExpenses.setStatus(PennantConstants.UPLOAD_STATUS_SUCCESS);
							} else {
								uploadFinExpenses.setStatus(PennantConstants.UPLOAD_STATUS_FAIL);
							}
							
							uploadFinExpenses.setReason(reason);
							
							uploadFinExpensesList.add(uploadFinExpenses);
						}
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
		
		return uploadFinExpensesList;
	}	
	
	public boolean validateCommonData (UploadFinExpenses uploadFinExpenses, String expenseType, String percentage, String amount, boolean valid, String reason) {
		
		BigDecimal percentageValue 	= BigDecimal.ZERO;
		BigDecimal amountValue 		= BigDecimal.ZERO;
		
		//AppendOrOverride
		if (!(PennantConstants.EXPENSE_UPLOAD_ADD.equalsIgnoreCase(uploadFinExpenses.getType())
				|| PennantConstants.EXPENSE_UPLOAD_OVERRIDE.equalsIgnoreCase(uploadFinExpenses.getType()))) {
			
			uploadFinExpenses.setType("E");	//default value for Type
			
			if (valid) {
				reason = "Append/Override is mandatory, it should be (A) or (O).";
				valid  = false;
			} else {
				reason = reason + "| Append/Override is mandatory, it should be (A) or (O).";
			}
		}
		
		//Percentage
		if (StringUtils.isBlank(percentage)) {
			if (valid) {
				reason = "Percentage is mandatory.";
				valid  = false;
			} else {
				reason = reason + "| Percentage is mandatory.";
			}
		} else {
			try {
				percentageValue = new BigDecimal(percentage);
				
				if (percentageValue.compareTo(BigDecimal.ZERO) < 0) {
					throw new Exception();
				} else if (percentageValue.compareTo(new BigDecimal(100)) > 0) {
					throw new Exception();
				} else {
					uploadFinExpenses.setPercentage(percentageValue);
				}
			} catch (Exception e) {
				if (valid) {
					reason = "Percentage is mandatory, it should be greater than or equals to 0 and less than or equals to 100.";
					valid  = false;
				} else {
					reason = reason + "| Percentage is mandatory, it should be greater than or equals to 0 and less than or equals to 100.";
				}
				uploadFinExpenses.setPercentage(BigDecimal.ZERO);
			}
		}
		
		//Amount
		if (StringUtils.isBlank(amount)) {
			if (valid) {
				reason = "Amount is mandatory, it should be greater than or equals to 0.";
				valid  = false;
			} else {
				reason = reason + "| Amount is mandatory, it should be greater than or equals to 0.";
			}
		} else {
			
			try {
				
				amountValue = new BigDecimal(amount);
				
				if (amountValue.compareTo(BigDecimal.ZERO) < 0 && PennantConstants.EXPENSE_UPLOAD_OVERRIDE.equals(uploadFinExpenses.getType())) {
					throw new Exception("Negative values are not allowed in Orverride method.");
				}
				
				if (amount.length() > 19) {
					throw new Exception("Length is exceeded, it should be lessthan or equal to 19.");
				}
				
				int formatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
				uploadFinExpenses.setAmountValue(PennantAppUtil.unFormateAmount(amountValue, formatter));
			} catch (Exception exception) {
				
				uploadFinExpenses.setAmountValue(BigDecimal.ZERO);
				
				if (valid) {
					reason = "Amount: (" + amount + ") is invalid. ";
					valid  = false;
				} else {
					reason = reason + "| Amount: (" + amount + ") is invalid. ";
				}
				
				if (StringUtils.isNotBlank(exception.getMessage())) {
					reason = reason + exception.getMessage();
				}
			}
		}
		
		if (BigDecimal.ZERO.compareTo(amountValue) == 0 && BigDecimal.ZERO.compareTo(percentageValue) == 0 ) {
			if (valid) {
				valid  = false;
				reason = "Amount Value and Percentage (%) both should not be 0, enter either Amount Value or Percentage (%) ";
			} else {
				reason = reason + "| Amount Value and Percentage (%) both should not be 0, enter either Amount Value or Percentage (%) ";
			}
		} else if (BigDecimal.ZERO.compareTo(amountValue) != 0 && BigDecimal.ZERO.compareTo(percentageValue) != 0 ) {
			if (valid) {
				reason = "Amount Value and Percentage (%) both should not be 0, enter either Amount Value or Percentage (%) ";
				valid  = false;
			} else {
				reason = reason + "| Amount Value and Percentage (%) both should not be 0, enter either Amount Value or Percentage (%) ";
			}
			uploadFinExpenses.setAmountValue(BigDecimal.ZERO);
			uploadFinExpenses.setPercentage(BigDecimal.ZERO);
		}
		
		//Expense Type Code
		if (StringUtils.isBlank(expenseType)) {
			if (valid) {
				valid  = false;
				reason = "Expense Type Code is mandatory.";
			} else {
				reason = reason + "| Expense Type Code is mandatory.";
			}
			uploadFinExpenses.setExpenseTypeCode("EXPError");
		} else if (expenseType.length() > 8) {
			if (valid) {
				valid  = false;
				reason = "Expense Type Code : (" + expenseType + ") length is exceeded, it should be lessthan or equal to 8.";
			} else {
				reason = reason + "| Expense Type Code : (" + expenseType + ") length is exceeded, it should be lessthan or equal to 8.";
			}
			uploadFinExpenses.setExpenseTypeCode(expenseType.substring(0, 8));
		} else {
			
			long finExpenseId = this.uploadHeaderService.getFinExpenseIdByExpType(expenseType);
			
			if (finExpenseId == 0 || finExpenseId == Long.MIN_VALUE) {
				
				if (valid) {
					valid = false;
					reason = "Expense Type Code : (" + expenseType + ") is invalid.";
				} else {
					reason = reason + "| Expense Type Code : (" + expenseType + ") is invalid.";
				}
			}
			
			uploadFinExpenses.setExpenseId(finExpenseId);
			uploadFinExpenses.setExpenseTypeCode(expenseType);
		}
		
		uploadFinExpenses.setReason(reason);
		
		return valid;
	}
	
	private List<String> getRowValuesByIndex(Workbook workbook , int sheetIndex, int rowindex) {
		
		List<String> rowValues = new ArrayList<String>();
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		org.apache.poi.ss.usermodel.Row row = sheet.getRow(rowindex);
	
		for (Cell cell : row) {
			
			this.formulaEvaluator.evaluate(cell);
			
			String cellValue = this.objDefaultFormat.formatCellValue(cell, this.formulaEvaluator);
			
			rowValues.add(cellValue.trim());
		}
		
		return rowValues;
	}
	
	private Date getUtilDate(String date, String format) throws ParseException {
		
		Date uDate = null;
		SimpleDateFormat df = new SimpleDateFormat(format);
		
		try {
			if (StringUtils.isBlank(date)) {
				throw new ParseException(null, 0);
			}
			
			String [] dateformat = date.split("-");
			
			if (dateformat.length != 3) {
				throw new ParseException(null, 0);
			}
			
			String dateValue = dateformat[0];
			String month = dateformat[1];
			String year = dateformat[2];
			
			boolean leapYear = false;
			
			if (StringUtils.isBlank(dateValue) || StringUtils.isBlank(month) || StringUtils.isBlank(year)) {
				throw new ParseException(null, 0);
			}
			
			int dateVal = Integer.parseInt(dateValue);
			
			if (year.length() == 4) {
				int yearValue = Integer.parseInt(year);
				int rem = yearValue % 4;
				if (rem == 0) {
					leapYear = true;
				}
			} else {
				throw new ParseException(null, 0);
			}
			
			switch (month.toUpperCase()) {
				case "JAN":
				case "MAR":
				case "MAY":	
				case "JUL":
				case "AUG":	
				case "OCT":	
				case "DEC":
						if (dateVal > 31) {
							throw new ParseException(null, 0);
						}
					break;
				
				case "FEB":
						if (leapYear) {
							if (dateVal > 29) {
								throw new ParseException(null, 0);
							}
						} else {
							if (dateVal > 28) {
								throw new ParseException(null, 0);
							}
						}
				break;
					
				case "APR":
				case "JUN":	
				case "SEP":
				case "NOV":	
					if (dateVal > 30) {
						throw new ParseException(null, 0);
					}
				break;
				
				default :
					throw new ParseException(null, 0);
			}
			
			uDate = df.parse(date);
			
		} catch (ParseException e) {
			throw e;
		}
		
		return uDate;
	}
	
	/**
	 * when the "refresh" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnRefresh(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		
		doResetData();
		
		logger.debug(Literal.LEAVING);
	}
	
	private void doResetData() {
		logger.debug(Literal.ENTERING);
		
		doRemoveValidation();
		
		this.txtFileName.setText("");
		this.fileName.setValue("");
		this.moduleType.setSelectedIndex(0);
		
		this.fileImport = null;
		this.errorMsg = null;
		
		this.workbook = null;
		this.objDefaultFormat = new DataFormatter();// for cell value formating
		this.formulaEvaluator = null; // for cell value formating
		
		this.statusGrid.setVisible(false);
		this.btndownload.setVisible(false);
		
		readOnlyComponent(false, this.moduleType);
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		doValidations();

		readOnlyComponent(true, this.moduleType);
		this.btnBrowse.setDisabled(true);
		this.btnRefresh.setDisabled(true);

		try {
			
			if (this.errorMsg != null) {
				throw new Exception(this.errorMsg);
			}
			
			doSave();

		} catch (Exception e) {
			this.errorMsg = e.getMessage();
			doResetData();
			MessageUtil.showError(e);
			return;
		} finally {
			readOnlyComponent(false, this.moduleType);
			this.btnBrowse.setDisabled(false);
			this.btnRefresh.setDisabled(false);
			logger.debug(Literal.LEAVING);
		}
	}

	private void doValidations() {
		logger.debug(Literal.ENTERING);

		doRemoveValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
			isValidComboValue(this.moduleType, Labels.getLabel("label_ExpenseUpload_ModuleType.value"));
			
			if (StringUtils.trimToNull(this.txtFileName.getValue()) == null) {
				throw new WrongValueException(this.txtFileName, Labels.getLabel("empty_file"));
			} else {
				boolean fileExist = this.uploadHeaderService.isFileNameExist(this.txtFileName.getValue());
				if (fileExist) {
					throw new WrongValueException(this.txtFileName, this.txtFileName.getValue() + ": file name already Exist.");
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		logger.debug(Literal.LEAVING);
	}

	private void doSave() throws Exception {
		logger.debug(Literal.ENTERING);

		if (this.fileImport != null) {

			//Reading excel data and returning as a workbook
			this.workbook = this.fileImport.writeFile();
			
			String moduleType = getComboboxValue(this.moduleType);
			
			if (this.workbook != null) {

				Sheet sheet = this.workbook.getSheetAt(0);
				
				if (sheet.getPhysicalNumberOfRows() > 1) {
					
					if (this.workbook instanceof HSSFWorkbook) {
						this.formulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) this.workbook);
					} else if (this.workbook instanceof XSSFWorkbook) {
						this.formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) this.workbook);
					}
					
					List<String> keys = getRowValuesByIndex(this.workbook, 0, 0);
					
					if (PennantConstants.EXPENSE_UPLOAD_LOANTYPE.equals(moduleType)) {
						if (!StringUtils.equalsIgnoreCase("Loan Type", keys.get(0))) {
							MessageUtil.showError("The uploaded file could not be recognized. Please upload a valid xls or xlsx file.");
							return;
						}
					} else if (PennantConstants.EXPENSE_UPLOAD_LOAN.equals(moduleType)) {
						if (!StringUtils.equalsIgnoreCase("Loan Reference", keys.get(0))) {
							MessageUtil.showError("The uploaded file could not be recognized. Please upload a valid xls or xlsx file.");
							return;
						}
					} else {
						return;
					}

					UploadHeader uploadHeader = new UploadHeader();
					uploadHeader.setFileName(this.txtFileName.getValue());
					uploadHeader.setTransactionDate(DateUtility.getSysDate());
					uploadHeader.setModule(moduleType);
					uploadHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					uploadHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					
					long uploadId = this.uploadHeaderService.save(uploadHeader);
					
					//Process the UploadFinExpenses
					List<UploadFinExpenses> uploadFinExpensesList = processUploadFinExpenses(moduleType, uploadId);

					if (CollectionUtils.isNotEmpty(uploadFinExpensesList)) {

						this.uploadHeaderService.saveUploadFinExpenses(uploadFinExpensesList);

						for (UploadFinExpenses uploadFinExpense : uploadFinExpensesList) {
							
							if (StringUtils.isNotBlank(uploadFinExpense.getReason())) {	
								continue;	//if data is not entered correctly
							}

							long finExpenseId = uploadFinExpense.getExpenseId();

							if (finExpenseId != 0 && finExpenseId != Long.MIN_VALUE) {

								if (PennantConstants.EXPENSE_UPLOAD_LOANTYPE.equals(moduleType)) {

									List<FinanceMain> financesList = this.uploadHeaderService.getFinancesByExpenseType(
											uploadFinExpense.getFinType(), uploadFinExpense.getFinApprovalStartDate(), uploadFinExpense.getFinApprovalEndDate());

									if (CollectionUtils.isNotEmpty(financesList)) {
										for (FinanceMain financeMain : financesList) {
											processFinExpenseDetails(financeMain, uploadFinExpense, finExpenseId); // Process the FinExpenseDetails and FinExpenseMovements
										}
									}
								} else if (PennantConstants.EXPENSE_UPLOAD_LOAN.equals(moduleType)) {

									FinanceMain financeMain = this.uploadHeaderService.getFinancesByFinReference(uploadFinExpense.getFinReference());

									processFinExpenseDetails(financeMain, uploadFinExpense, finExpenseId); // Process the FinExpenseDetails and FinExpenseMovements
								}
							}
						}
					}
					
					this.uploadHeaderService.updateRecordCounts(uploadHeader);
					
					uploadHeader = this.uploadHeaderService.getUploadHeader(uploadHeader.getUploadId());
				
					Clients.showNotification("Data imported successfully.", "info", null, null, -1);
					
					//Create backup file
					this.fileImport.backUpFile();
					
					//doResetData();
					
					this.statusGrid.setVisible(true);
					this.btndownload.setVisible(true);
					
					this.totalCount.setValue(String.valueOf(uploadHeader.getTotalRecords()));
					this.successCount.setValue(String.valueOf(uploadHeader.getSuccessCount()));
					this.failedCount.setValue(String.valueOf(uploadHeader.getFailedCount()));
				
				} else {
					MessageUtil.showError("File should not contain the data.");
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * process the Fin Expense Details and Fee Fin Expense Movements
	 * @param financeMain
	 * @param uploadDetail
	 * @param finExpenseId
	 */
	private void processFinExpenseDetails(FinanceMain financeMain, UploadFinExpenses uploadDetail, long finExpenseId) {
		logger.debug(Literal.ENTERING);
		
		int formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());
		BigDecimal txnAmount = uploadDetail.getAmountValue();
		Date transactionDate = DateUtility.getAppDate();
		
		if (BigDecimal.ZERO.compareTo(txnAmount) == 0) {
			
			if (BigDecimal.ZERO.compareTo(uploadDetail.getPercentage()) == 0) {
				return;
			}
			
			if (financeMain.getFinAssetValue() != null && financeMain.getFinAssetValue().compareTo(BigDecimal.ZERO) != 0) {
				BigDecimal percentage = uploadDetail.getPercentage();
				//formatting the amount
				txnAmount = PennantAppUtil.formateAmount(financeMain.getFinAssetValue(), formatter);
				//calculating percentage
				txnAmount = (percentage.multiply(txnAmount)).divide(new BigDecimal(100));
				//un-formatting the amount
				txnAmount = PennantAppUtil.unFormateAmount(txnAmount, formatter);
			} else {
				return;
			}
		}
		
		long finExpenseDetailId = 0;
		FinExpenseDetails finExpenseDetails = this.uploadHeaderService.getFinExpenseDetailsByReference(financeMain.getFinReference(), finExpenseId);
		
		if (finExpenseDetails == null) {
			
			finExpenseDetails = new FinExpenseDetails();
			finExpenseDetails.setFinReference(financeMain.getFinReference());
			finExpenseDetails.setExpenseTypeId(finExpenseId);
			finExpenseDetails.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			finExpenseDetails.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			finExpenseDetails.setAmount(txnAmount);
			
			finExpenseDetailId = this.uploadHeaderService.saveFinExpenseDetails(finExpenseDetails);
			
			finExpenseDetails.setFinExpenseId(finExpenseDetailId);
		} else {
			
			finExpenseDetailId = finExpenseDetails.getFinExpenseId();
			
			if (PennantConstants.EXPENSE_UPLOAD_ADD.equals(uploadDetail.getType())) {
				finExpenseDetails.setAmount(txnAmount.add(finExpenseDetails.getAmount()));
			} else if (PennantConstants.EXPENSE_UPLOAD_OVERRIDE.equals(uploadDetail.getType())) {
				finExpenseDetails.setAmount(txnAmount);
			}
			
			this.uploadHeaderService.update(finExpenseDetails);
		}
		
		FinExpenseMovements finExpenseMovements = new FinExpenseMovements();
		finExpenseMovements.setFinExpenseId(finExpenseDetailId);
		finExpenseMovements.setFinReference(financeMain.getFinReference());
		finExpenseMovements.setUploadId(uploadDetail.getUploadId());
		finExpenseMovements.setModeType(PennantConstants.EXPENSE_MODE_UPLOAD);
		finExpenseMovements.setTransactionAmount(txnAmount);
		finExpenseMovements.setTransactionType(uploadDetail.getType());
		finExpenseMovements.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		finExpenseMovements.setTransactionDate(transactionDate);
		
		this.uploadHeaderService.saveFinExpenseMovements(finExpenseMovements);
	
		logger.debug(Literal.LEAVING);
	}

	public void setUploadHeaderService(UploadHeaderService uploadHeaderService) {
		this.uploadHeaderService = uploadHeaderService;
	}

	public void setUploadHeader(UploadHeader uploadHeader) {
		this.uploadHeader = uploadHeader;
	}
}

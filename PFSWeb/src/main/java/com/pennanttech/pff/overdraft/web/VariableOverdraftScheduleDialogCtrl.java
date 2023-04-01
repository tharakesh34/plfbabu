package com.pennanttech.pff.overdraft.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.overdraft.model.VariableOverdraftSchdDetail;
import com.pennanttech.pff.overdraft.model.VariableOverdraftSchdHeader;
import com.pennanttech.pff.overdraft.service.VariableOverdraftSchdService;

public class VariableOverdraftScheduleDialogCtrl extends GFCBaseCtrl<VariableOverdraftSchdHeader> {
	private static final Logger logger = LogManager.getLogger(VariableOverdraftScheduleDialogCtrl.class);
	private static final long serialVersionUID = 1L;

	protected Window window_VariableOverdraftScheduleDialog;

	protected Borderlayout borderlayout_VariableODSchedule;
	public Listbox listScheduleDetails;
	protected Listheader listheader_Reason;

	protected Tabbox tabBoxIndexCenter;
	protected Decimalbox odLimitAmt;
	protected Intbox noOfInstallments;
	protected Intbox noOfInstallments_two;
	public Textbox txtFileName;
	protected Groupbox finBasicdetails;
	protected Button btnUpload;
	protected Button btnClose;
	protected Button btnImport;
	protected Button btnNewScheduleDetail;

	protected Label finBasic_profitDaysBasis;
	protected Label finBasic_finType;
	protected Label finBasic_finCcy;
	protected Label finBasic_scheduleMethod;
	protected Label finBasic_finReference;
	protected Label finBasic_custShrtName;
	protected Label label_finAmount;
	protected Label label_VariableODcheduleDialog_Installments;

	private int ccyFormat = 0;
	private Media media = null;
	private FinanceDetail financeDetail;
	private FinScheduleData finScheduleData;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private FinanceMainBaseCtrl financeMainBaseCtrl = null;
	private ArrayList<Object> finHeaderList = new ArrayList<Object>();
	private Object parentController = null;
	private String filePath = "";
	private String roleCode = "";

	private static final String HEADER_DATE = "Date";
	private static final String HEADER_AMOUNT = "Dropline Amount";
	private static final int HEADERS_COUNT = 2;
	private int ccyEditField = PennantConstants.defaultCCYDecPos;
	private boolean schdChange = false;

	private VariableOverdraftSchdHeader variableOverdraftSchdHeader = new VariableOverdraftSchdHeader();
	private List<VariableOverdraftSchdDetail> variableOverdraftSchdDetail = new ArrayList<VariableOverdraftSchdDetail>();

	private VariableOverdraftSchdService variableOverdraftSchdService;

	public VariableOverdraftScheduleDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "VariableOverdraftScheduleDialog";
	}

	@SuppressWarnings("unchecked")
	public void onCreate$window_VariableOverdraftScheduleDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_VariableOverdraftScheduleDialog);
		this.borderlayout_VariableODSchedule.setHeight(getBorderLayoutHeight());

		try {

			if (arguments.containsKey("financeDetail")) {
				this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
				setFinScheduleData(financeDetail.getFinScheduleData());
			} else if (arguments.containsKey("finScheduleData")) {
				setFinScheduleData((FinScheduleData) arguments.get("finScheduleData"));
			}

			if (arguments.containsKey("finHeaderList")) {
				this.finHeaderList = (ArrayList<Object>) arguments.get("finHeaderList");
			}

			if (arguments.containsKey("parentCtrl")) {
				this.financeMainBaseCtrl = (FinanceMainBaseCtrl) arguments.get("parentCtrl");
			}

			if (arguments.containsKey("roleCode")) {
				roleCode = (String) arguments.get("roleCode");
			}

			doCheckRights();
			doSetFieldProperties();
			doShowDialog(getFinScheduleData());

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_VariableOverdraftScheduleDialog.onClose();
		}
		logger.debug(Literal.LEAVING);
	}

	private void appendFinBasicDetails() {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", this.finHeaderList);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.noOfInstallments.setMaxlength(3);
		this.noOfInstallments.setStyle("text-align:right;");
		this.noOfInstallments_two.setMaxlength(3);
		this.noOfInstallments_two.setStyle("text-align:right;");

		this.odLimitAmt.setReadonly(true);
		this.noOfInstallments.setReadonly(true);
		this.noOfInstallments_two.setReadonly(true);
		this.btnImport.setVisible(false);

		this.listScheduleDetails.setHeight(borderLayoutHeight - 280 + "px");
		this.window_VariableOverdraftScheduleDialog.setHeight(borderLayoutHeight - 80 + "px");

		this.ccyFormat = CurrencyUtil.getFormat(finScheduleData.getFinanceMain().getFinCcy());

		setFilePath();

		logger.debug(Literal.LEAVING);
	}

	private void setFilePath() {
		this.filePath = SysParamUtil.getValueAsString(SMTParameterConstants.UPLOAD_FILEPATH);
		this.filePath = this.filePath.concat(File.separator).concat("Overdraft_Schedule");
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities("VariableOverdraftScheduleDialog", roleCode);
		this.btnUpload.setVisible(getUserWorkspace().isAllowed("button_VariableOverdraftScheduleDialog_btnUpload"));
		this.btnImport.setVisible(getUserWorkspace().isAllowed("button_VariableOverdraftScheduleDialog_btnImport"));
		this.btnNewScheduleDetail
				.setVisible(getUserWorkspace().isAllowed("button_VariableOverdraftScheduleDialog_btnImport"));

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialog(FinScheduleData finSchdData) {
		logger.debug(Literal.ENTERING);

		try {
			appendFinBasicDetails();

			try {
				Class[] paramType = { this.getClass() };
				Object[] stringParameter = { this };
				if (financeMainBaseCtrl.getClass().getMethod("setVariableOverdraftScheduleDialogCtrl",
						paramType) != null) {
					financeMainBaseCtrl.getClass().getMethod("setVariableOverdraftScheduleDialogCtrl", paramType)
							.invoke(financeMainBaseCtrl, stringParameter);
				}
			} catch (Exception e) {
				logger.error(e);
			}

			doWriteBeanToComponents(finSchdData);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			this.window_VariableOverdraftScheduleDialog.onClose();
		}
		logger.debug(Literal.LEAVING);
	}

	public void doWriteBeanToComponents(FinScheduleData schdData) {
		logger.debug(Literal.ENTERING);

		int noOfTerms = 0;
		int noOfTerms2 = 0;
		BigDecimal odLimit = BigDecimal.ZERO;

		// OD LimitAmt
		try {
			odLimit = this.financeMainBaseCtrl.finAssetValue.getActualValue();
		} catch (WrongValueException we) {
		}

		// Number of terms
		try {
			if (this.financeMainBaseCtrl.odYearlyTerms.getValue() == null) {
				noOfTerms = 0;
			} else {
				noOfTerms = this.financeMainBaseCtrl.odYearlyTerms.getValue();
			}
		} catch (WrongValueException we) {
		}

		// Number of terms
		try {
			if (this.financeMainBaseCtrl.odMnthlyTerms.getValue() == null) {
				noOfTerms2 = 0;
			} else {
				noOfTerms2 = this.financeMainBaseCtrl.odMnthlyTerms.getValue();
			}
		} catch (WrongValueException we) {
		}

		this.odLimitAmt.setValue(odLimit);
		this.noOfInstallments.setValue(noOfTerms);
		this.noOfInstallments_two.setValue(noOfTerms2);

		VariableOverdraftSchdHeader header = schdData.getVariableOverdraftSchdHeader();

		if (header != null) {
			this.variableOverdraftSchdHeader = header;
			List<VariableOverdraftSchdDetail> details = this.variableOverdraftSchdHeader
					.getVariableOverdraftSchdDetails();

			this.txtFileName.setValue(this.variableOverdraftSchdHeader.getFileName());
			this.variableOverdraftSchdHeader.setCurPOSAmt(odLimit);

			if (CollectionUtils.isNotEmpty(details)) {
				doFillScheduleDetails(details, false);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void doFillScheduleDetails(List<VariableOverdraftSchdDetail> odSchdDetails, boolean isChanged) {
		logger.debug(Literal.ENTERING);

		this.listScheduleDetails.getItems().clear();

		if (CollectionUtils.isEmpty(odSchdDetails)) {
			return;
		}

		int noOfTerms = 0;
		BigDecimal totDropLineAmt = BigDecimal.ZERO;
		String repaymentdate = "";
		String principalAmt = "";
		boolean failReasonReq = false;

		FinanceMain financeMain = getFinScheduleData().getFinanceMain();
		int format = CurrencyUtil.getFormat(financeMain.getFinCcy());

		for (VariableOverdraftSchdDetail detail : odSchdDetails) {

			if (StringUtils.isBlank(detail.getReason())) {
				repaymentdate = DateUtil.formatToLongDate(detail.getSchDate());
				principalAmt = PennantApplicationUtil.amountFormate(detail.getDroplineAmount(), this.ccyFormat);
				failReasonReq = false;
				totDropLineAmt = totDropLineAmt.add(detail.getDroplineAmount());
			} else {
				repaymentdate = detail.getStrSchDate();
				principalAmt = detail.getStrDroplineAmount();
				failReasonReq = true;
			}
			if (failReasonReq) {
				this.listheader_Reason.setVisible(true);
				if (this.variableOverdraftSchdHeader != null) {
					this.variableOverdraftSchdHeader.setValidSchdUpload(false);
				}
			}

			noOfTerms++;

			Listitem listitem = new Listitem();
			Listcell listcell;
			listcell = new Listcell(String.valueOf(noOfTerms));
			listitem.appendChild(listcell);

			listcell = new Listcell(repaymentdate);
			listcell.setStyle("font-weight:bold;text-align:center;");
			listitem.appendChild(listcell);

			listcell = new Listcell(principalAmt);
			listcell.setStyle("text-align:right;");
			listitem.appendChild(listcell);

			listcell = new Listcell(detail.getReason());
			listcell.setStyle("font-weight:bold;text-align:center;color:#F20707;");
			listitem.appendChild(listcell);
			listitem.setAttribute("data", detail);
			ComponentsCtrl.applyForward(listitem, "onDoubleClick=onSchduleDetailItemDoubleClicked");
			this.listScheduleDetails.appendChild(listitem);
		}

		this.variableOverdraftSchdHeader
				.setTotDropLineAmt(PennantApplicationUtil.formateAmount(totDropLineAmt, format));
		this.variableOverdraftSchdHeader.setNumberOfTerms(noOfTerms);
		setOverdraftVariableSchdDetail(odSchdDetails);
		this.variableOverdraftSchdHeader.setVariableOverdraftSchdDetails(odSchdDetails);

		if (isChanged) {
			setSchdChange(true);
		}

		if (StringUtils.trimToNull(this.txtFileName.getValue()) == null) {
			saveUploadHeader();
		}

		logger.debug(Literal.LEAVING);
	}

	public void onSchduleDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		final Listitem item = this.listScheduleDetails.getSelectedItem();
		if (item != null) {
			VariableOverdraftSchdDetail schdDetail = (VariableOverdraftSchdDetail) item.getAttribute("data");

			if (StringUtils.trimToNull(schdDetail.getReason()) != null) {
				MessageUtil.showError(schdDetail.getReason());
				return;
			}

			final Map<String, Object> map = new HashMap<String, Object>();

			map.put("VariableOverdraftScheduleDialogCtrl", this);
			map.put("VariableOverdraftSchdDetail", schdDetail);
			map.put("isEditable", this.btnNewScheduleDetail.isVisible());
			map.put("ccyEditField", ccyEditField);
			map.put("finScheduleData", getFinScheduleData());
			map.put("maturityDate", this.financeMainBaseCtrl.calMaturityDate());
			map.put("odLimit", PennantApplicationUtil.unFormateAmount(this.odLimitAmt.getValue(), ccyEditField));
			map.put("newRecord", false);

			try {
				Executions.createComponents(
						"/WEB-INF/pages/Finance/Overdraft/VariableOverdraftScheduleDetailDialog.zul", null, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnNewScheduleDetail(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		VariableOverdraftSchdDetail schdDetail = new VariableOverdraftSchdDetail();
		final Map<String, Object> map = new HashMap<>();
		map.put("VariableOverdraftScheduleDialogCtrl", this);
		map.put("VariableOverdraftSchdDetail", schdDetail);
		map.put("newRecord", true);
		map.put("ccyEditField", ccyEditField);
		map.put("isEditable", this.btnNewScheduleDetail.isVisible());
		map.put("finScheduleData", getFinScheduleData());
		map.put("maturityDate", this.financeMainBaseCtrl.calMaturityDate());
		map.put("odLimit", PennantApplicationUtil.unFormateAmount(this.odLimitAmt.getValue(), ccyEditField));
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Overdraft/VariableOverdraftScheduleDetailDialog.zul",
					null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.txtFileName.setConstraint("");
		this.txtFileName.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This Method/Event for getting the uploaded document should be comma separated values and then read the document
	 * and setting the values to the Lead VO and added those vos to the List and it also shows the information about
	 * where we go the wrong data
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onUpload$btnUpload(UploadEvent event) throws Exception {
		logger.debug(Literal.ENTERING);

		doClear();

		this.media = event.getMedia();
		String fileName = media.getName();

		if (!(StringUtils.endsWith(fileName.toLowerCase(), ".csv"))) {
			MessageUtil.showError(Labels.getLabel("VALID_FILE_EXTENSION"));
			media = null;
			return;
		}

		this.txtFileName.setText(fileName);

		validateUploadData();

		logger.debug(Literal.LEAVING);
	}

	private void validateUploadData() throws Exception {
		logger.debug(Literal.ENTERING);

		doValidations();

		this.btnUpload.setDisabled(true);
		this.listheader_Reason.setVisible(false);

		try {
			doSave();
		} catch (Exception e) {
			MessageUtil.showError(e.getMessage());
			return;
		} finally {
			this.btnUpload.setDisabled(false);
		}

		logger.debug(Literal.LEAVING);
	}

	protected void doSave() throws Exception {
		logger.debug(Literal.ENTERING);

		VariableOverdraftSchdDetail variableODSchdDetail = null;
		FinanceMain financeMain = getFinScheduleData().getFinanceMain();
		Date prvSchdDate = financeMain.getFinStartDate();
		Date maturityDate = this.financeMainBaseCtrl.calMaturityDate();

		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		File parent = new File(this.filePath);

		if (!parent.exists()) {
			parent.mkdirs();
		}

		File file = new File(parent.getPath().concat(File.separator).concat(this.media.getName()));
		if (file.exists()) {
			file.delete();
		}

		try {

			file.createNewFile();
			if (media.isBinary()) {
				FileUtils.writeByteArrayToFile(file, this.media.getByteData());
			} else {
				FileUtils.writeStringToFile(file, this.media.getStringData());
			}

			int recordCount = 0;
			this.variableOverdraftSchdHeader.setValidSchdUpload(true);

			br = new BufferedReader(new FileReader(file));
			List<VariableOverdraftSchdDetail> odSchdDetails = new ArrayList<VariableOverdraftSchdDetail>();

			while ((line = br.readLine()) != null) {

				String[] row = line.split(cvsSplitBy);

				try {
					if (row.length == HEADERS_COUNT && recordCount == 0) {
						if (!(HEADER_DATE.equalsIgnoreCase(row[0]) && HEADER_AMOUNT.equalsIgnoreCase(row[1]))) {
							throw new Exception(Labels.getLabel("HEADER_MISMATCH"));
						}

						saveUploadHeader();

					} else if (recordCount == 0) {
						throw new Exception(Labels.getLabel("HEADER_MISMATCH_SPACE"));
					}
				} catch (Exception e) {
					MessageUtil.showError(e.getMessage());
					return;
				}

				if (recordCount == 0 || row.length == 0) {
					recordCount++;
					continue;
				}

				variableODSchdDetail = new VariableOverdraftSchdDetail();

				if (row.length == 2) {
					if (StringUtils.isBlank(row[0]) && StringUtils.isBlank(row[1])) {
						recordCount++;
						continue;
					}
					variableODSchdDetail.setStrSchDate(row[0]);
					variableODSchdDetail.setStrDroplineAmount(row[1]);
				} else if (row.length == 1) {
					variableODSchdDetail.setStrSchDate(row[0]);
				}

				validateUploadDetails(variableODSchdDetail, prvSchdDate, maturityDate);

				if (variableODSchdDetail.getReason().isEmpty()) {
					prvSchdDate = variableODSchdDetail.getSchDate();
				}

				odSchdDetails.add(variableODSchdDetail);

				recordCount++;

			}

			saveUploadHeader();

			doFillScheduleDetails(odSchdDetails, false);
			this.variableOverdraftSchdHeader.setCurPOSAmt(this.odLimitAmt.getValue());
			setSchdChange(true);
			this.variableOverdraftSchdHeader.setVariableOverdraftSchdDetails(odSchdDetails);
			if (financeDetail != null) {
				financeDetail.getFinScheduleData().setVariableOverdraftSchdHeader(variableOverdraftSchdHeader);
			} else {
				getFinScheduleData().setVariableOverdraftSchdHeader(this.variableOverdraftSchdHeader);
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		} finally {
			backUpFile(file);
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * entry point of program, reading whole excel and calling other methods to prepare jsonObject.
	 * 
	 * @param maturityDate
	 * 
	 * @return String
	 * @throws Exception
	 */
	private void validateUploadDetails(VariableOverdraftSchdDetail variableODSchdDetail, Date prvSchdDate,
			Date maturityDate) throws Exception {
		logger.debug(Literal.ENTERING);

		String reason = "";
		boolean valid = true;
		BigDecimal dropLineAmt = BigDecimal.ZERO;
		Date schdDate = null;

		String strSchdDate = variableODSchdDetail.getStrSchDate();
		String strDropLineAmnt = variableODSchdDetail.getStrDroplineAmount();

		String dateFormat = DateFormat.LONG_DATE.getPattern();

		// RePayment Date
		if (StringUtils.isBlank(strSchdDate)) {
			if (valid) {
				reason = "Schedule Date is mandatory, it should be in " + dateFormat + " format.";
				valid = false;
			}
		} else {
			try {
				schdDate = getUtilDate(strSchdDate, dateFormat);
			} catch (ParseException e) {
				if (valid) {
					reason = "Invalid Schedule Date, it should be in " + dateFormat + " format.";
					valid = false;
				}
				schdDate = null;
			}
		}
		if (schdDate != null) {
			if (schdDate.compareTo(prvSchdDate) <= 0) {
				if (valid) {
					reason = "Schedule date should be greater than "
							+ DateUtil.format(prvSchdDate, DateFormat.LONG_DATE.getPattern());
					valid = false;
				}
			}

			if (maturityDate != null && schdDate.compareTo(maturityDate) > 0) {
				if (valid) {
					reason = "Schedule date should be less than or equal to "
							+ DateUtil.format(maturityDate, DateFormat.LONG_DATE.getPattern());
					valid = false;
				}
			}

			variableODSchdDetail.setSchDate(schdDate);
		}

		// Principle Amount
		if (StringUtils.isBlank(strDropLineAmnt)) {
			if (valid) {
				reason = "Dropline Amount is mandatory.";
				valid = false;
			} else {
				reason = reason + "| Dropline Amount is mandatory.";
			}
		} else {
			try {
				dropLineAmt = new BigDecimal(strDropLineAmnt);

				if (dropLineAmt.compareTo(BigDecimal.ZERO) < 0) {
					throw new Exception();
				} else {
					variableODSchdDetail
							.setDroplineAmount(PennantApplicationUtil.unFormateAmount(dropLineAmt, this.ccyFormat));
				}
			} catch (NumberFormatException e) {
				dropLineAmt = BigDecimal.ONE;
				if (valid) {
					reason = "Dropline Amount : (" + strDropLineAmnt + ") is invalid";
					valid = false;
				} else {
					reason = reason + "| Dropline Amount : (" + strDropLineAmnt + ") is invalid";
				}
			} catch (Exception e) {
				if (valid) {
					reason = "Dropline Amount is mandatory, it should be greater than or equals to 0 .";
					valid = false;
				} else {
					reason = reason + "| Dropline Amount is mandatory, it should be greater than or equals to 0 .";
				}
			}
		}

		if (StringUtils.isEmpty(reason)) {
			variableODSchdDetail.setStatus(UploadConstants.UPLOAD_STATUS_SUCCESS);
		} else {
			variableODSchdDetail.setStatus(UploadConstants.UPLOAD_STATUS_FAIL);
		}
		variableODSchdDetail.setReason(reason);
	}

	private void doValidations() {
		logger.debug(Literal.ENTERING);

		doRemoveValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (StringUtils.trimToNull(this.txtFileName.getValue()) == null) {
				throw new WrongValueException(this.txtFileName, Labels.getLabel("empty_file"));
			} else {
				boolean fileExist = this.variableOverdraftSchdService.isFileNameExist(this.txtFileName.getValue());
				if (fileExist) {
					throw new WrongValueException(this.txtFileName,
							this.txtFileName.getValue() + ": file name already Exist.");
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.noOfInstallments.isReadonly() && this.noOfInstallments.getValue() <= 0) {
				throw new WrongValueException("NO NEGATIVE:" + Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO",
						new String[] { this.label_VariableODcheduleDialog_Installments.getValue() }));
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

	private void saveUploadHeader() {
		this.variableOverdraftSchdHeader.setFileName(this.txtFileName.getValue());
		this.variableOverdraftSchdHeader.setTransactionDate(DateUtil.getSysDate());
		this.variableOverdraftSchdHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		this.variableOverdraftSchdHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
	}

	/**
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void backUpFile(File file) throws IOException {
		logger.debug(Literal.ENTERING);

		if (file != null) {

			File backupFile = new File(file.getParent() + "/BackUp");

			if (!backupFile.exists()) {
				backupFile.mkdir();
			}

			FileUtils.copyFile(file, new File(backupFile.getPath().concat(File.separator).concat(file.getName())));

			if (file.exists()) {
				if (!file.delete()) {
					file.deleteOnExit();
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Validate the date given in the excel sheet.<br>
	 * 
	 * @param date
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	private Date getUtilDate(String date, String format) throws ParseException {
		Date uDate = null;
		SimpleDateFormat df = new SimpleDateFormat(format);
		try {
			if (StringUtils.isBlank(date)) {
				throw new ParseException(null, 0);
			}
			String[] dateformat = date.split("-");
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

			default:
				throw new ParseException(null, 0);
			}
			uDate = df.parse(date);
		} catch (ParseException e) {
			throw e;
		}
		return uDate;
	}

	public void onChange$noOfInstallments(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		this.variableOverdraftSchdHeader.setNumberOfTerms(this.noOfInstallments.getValue());
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	public void onClick$btnClose(Event event) {
		doClose(true);
	}

	/**
	 * Clear error messages and values
	 */
	private void doClear() {
		this.txtFileName.setText("");
		this.listScheduleDetails.getItems().clear();
		getFinScheduleData().setVariableOverdraftSchdHeader(null);
	}

	public void deAllocateAuthorities() {
		if (getUserWorkspace() != null) {
			getUserWorkspace().deAllocateAuthorities("VariableOverdraftScheduleDialog");
		}
	}

	public Object getObject() {
		return parentController;
	}

	public void setObject(Object object) {
		this.parentController = object;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public int getNoOfInstallments() {
		return noOfInstallments.getValue();
	}

	public Intbox getNoOfInstallments_two() {
		return noOfInstallments_two;
	}

	public Button getBtnUpload() {
		return btnUpload;
	}

	public void setBtnUpload(Button btnUpload) {
		this.btnUpload = btnUpload;
	}

	public List<VariableOverdraftSchdDetail> getOverdraftVariableSchdDetails() {
		return variableOverdraftSchdDetail;
	}

	public void setOverdraftVariableSchdDetail(List<VariableOverdraftSchdDetail> overdraftVariableSchdDetails) {
		this.variableOverdraftSchdDetail = overdraftVariableSchdDetails;
	}

	public void setVariableOverdraftSchdHeader(VariableOverdraftSchdHeader variableOverdraftSchdHeader) {
		this.variableOverdraftSchdHeader = variableOverdraftSchdHeader;
	}

	public boolean isSchdChange() {
		return schdChange;
	}

	public void setSchdChange(boolean schdChange) {
		this.schdChange = schdChange;
	}

	@Autowired
	public void setVariableOverdraftSchdService(VariableOverdraftSchdService variableOverdraftSchdService) {
		this.variableOverdraftSchdService = variableOverdraftSchdService;
	}

}

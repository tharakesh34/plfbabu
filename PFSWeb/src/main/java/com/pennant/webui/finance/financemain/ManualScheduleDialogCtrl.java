package com.pennant.webui.finance.financemain;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.manual.schedule.ManualScheduleDetail;
import com.pennant.backend.model.finance.manual.schedule.ManualScheduleHeader;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.DocType;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.upload.FileImport;

public class ManualScheduleDialogCtrl extends GFCBaseCtrl<ManualScheduleHeader> {
	private static final long serialVersionUID = -8384860175347850484L;
	private static final Logger logger = LogManager.getLogger(ManualScheduleDialogCtrl.class);

	protected Window window_UploadManualScheduleDialog;
	protected Borderlayout borderlayout_UploadManualSchedule;
	protected Tab scheduleDetailsTab;
	protected Tab openScheduleDetailsTab;
	protected Listbox listScheduleDetails;
	protected Listheader listheader_Reason;
	protected Tabpanel tp_UploadMS;
	protected Tabpanel tp_ViewUploadMS;
	protected Tabbox tabBoxIndexCenter;
	protected Groupbox gb_UploadMS;

	protected Decimalbox currentPOS;
	protected Intbox noOfInstallments;
	protected Decimalbox effectiveRate;
	protected Textbox txtFileName;
	protected Groupbox finBasicdetails;
	protected Button btnUpload;
	protected Button btnClose;
	protected Button btnImport;

	protected Label finBasic_profitDaysBasis;
	protected Label finBasic_finType;
	protected Label finBasic_finCcy;
	protected Label finBasic_scheduleMethod;
	protected Label finBasic_finReference;
	protected Label finBasic_custShrtName;
	protected Label label_finAmount;
	protected Label label_UploadManualScheduleDialog_installments;

	private FinanceDetail financeDetail;
	private FinScheduleData finScheduleData;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private ManualScheduleHeader manualSchdHeader = new ManualScheduleHeader();
	private List<Object> finHeaderList = new ArrayList<>();
	private Object object = null;
	private String moduleDefiner = "";
	private String roleCode = "";
	protected Button sampleFileDownload;
	protected Combobox fileFormat;

	private FileImport<ManualScheduleHeader> manualScheduleFileImport;

	/**
	 * default constructor.<br>
	 */
	public ManualScheduleDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ManualScheduleDialog";
	}

	@SuppressWarnings("unchecked")
	public void onCreate$window_UploadManualScheduleDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_UploadManualScheduleDialog);
		this.borderlayout_UploadManualSchedule.setHeight(getBorderLayoutHeight());
		try {

			if (arguments.containsKey("financeDetail")) {
				this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
				setFinScheduleData(financeDetail.getFinScheduleData());
			} else if (arguments.containsKey("finScheduleData")) {
				this.finScheduleData = (FinScheduleData) arguments.get("finScheduleData");
				setFinScheduleData(finScheduleData);
			}

			if (arguments.containsKey("finHeaderList")) {
				this.finHeaderList = (List<Object>) arguments.get("finHeaderList");
			}

			if (arguments.containsKey("parentCtrl")) {
				setObject(arguments.get("parentCtrl"));
			}

			if (arguments.containsKey("moduleDefiner")) {
				moduleDefiner = (String) arguments.get("moduleDefiner");
			}

			if (arguments.containsKey("roleCode")) {
				roleCode = (String) arguments.get("roleCode");
			}

			doCheckRights();
			doSetFieldProperties();
			doShowDialog(finScheduleData);

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_UploadManualScheduleDialog.onClose();
		}

		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities("ManualScheduleDialog", roleCode);
		this.btnUpload.setVisible(getUserWorkspace().isAllowed("button_ManualScheduleDialog_btnUploadManualSchedule"));
		this.btnImport.setVisible(getUserWorkspace().isAllowed("button_ManualScheduleDialog_btnImport"));

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for deAllocating the ManualSchedule Authorities.
	 * 
	 */
	public void deAllocateAuthorities() {
		if (getUserWorkspace() != null) {
			getUserWorkspace().deAllocateAuthorities("ManualScheduleDialog");
		}
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails() {
		try {
			final Map<String, Object> map = new HashMap<>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", this.finHeaderList);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isEmpty(moduleDefiner)) {
			label_finAmount.setValue(Labels.getLabel("label_FinAmount"));
		}
		this.noOfInstallments.setMaxlength(4);
		this.noOfInstallments.setStyle("text-align:right;");

		this.currentPOS.setReadonly(true);
		this.noOfInstallments.setReadonly(true);
		this.btnImport.setVisible(false);

		if (FinServiceEvent.RECALCULATE.equals(moduleDefiner)) {
			this.noOfInstallments.setReadonly(false);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param aAcademic The entity that need to be render.
	 */
	public void doShowDialog(FinScheduleData finSchdData) {
		logger.debug(Literal.ENTERING);

		try {
			if (StringUtils.isEmpty(moduleDefiner)) {
				appendFinBasicDetails();
			}

			try {
				@SuppressWarnings("rawtypes")
				Class[] paramType = { this.getClass() };
				Object[] stringParameter = { this };
				object.getClass().getMethod("setManualScheduleDialogCtrl", paramType).invoke(object, stringParameter);
			} catch (Exception e) {
				//
			}

			if (StringUtils.isEmpty(moduleDefiner)) {
				this.gb_UploadMS.setHeight(borderLayoutHeight - 200 + "px");
				this.tp_UploadMS.setHeight(borderLayoutHeight - 191 + "px");
				this.tp_ViewUploadMS.setHeight(borderLayoutHeight - 191 + "px");
				this.listScheduleDetails.setHeight(borderLayoutHeight - 200 + "px");
				this.window_UploadManualScheduleDialog.setHeight(borderLayoutHeight - 80 + "px");
			} else if (moduleDefiner.equals(FinServiceEvent.ADDDISB)
					|| moduleDefiner.equals(FinServiceEvent.RECALCULATE)) {
				this.tp_UploadMS.setHeight(borderLayoutHeight - 300 + "px");
				this.gb_UploadMS.setHeight(borderLayoutHeight - 309 + "px");
				this.tp_ViewUploadMS.setHeight(borderLayoutHeight - 300 + "px");
				this.listScheduleDetails.setHeight(borderLayoutHeight - 309 + "px");
				this.window_UploadManualScheduleDialog.setHeight(borderLayoutHeight - 270 + "px");
			} else if (moduleDefiner.equals(FinServiceEvent.RECEIPT)) {
				this.gb_UploadMS.setHeight(borderLayoutHeight - 145 + "px");
				this.tp_UploadMS.setHeight(borderLayoutHeight - 135 + "px");
				this.tp_ViewUploadMS.setHeight(borderLayoutHeight - 135 + "px");
				this.listScheduleDetails.setHeight(borderLayoutHeight - 145 + "px");
				this.window_UploadManualScheduleDialog.setHeight(borderLayoutHeight - 100 + "px");
			}

			doWriteBeanToComponents(finSchdData);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			this.window_UploadManualScheduleDialog.onClose();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * set the basic data to Loan Amount and no of Installment
	 * 
	 * @param schdData
	 */
	public void doWriteBeanToComponents(FinScheduleData schdData) {
		logger.debug(Literal.ENTERING);

		int noOfTerms = 0;
		BigDecimal closingBal = BigDecimal.ZERO;
		BigDecimal effectiveRate = BigDecimal.ZERO;

		FinanceMain fm = schdData.getFinanceMain();
		List<FinanceScheduleDetail> schedules = schdData.getFinanceScheduleDetails();
		int format = CurrencyUtil.getFormat(fm.getFinCcy());

		Date evtFromDate = fm.getEventFromDate();
		if (evtFromDate == null) {
			evtFromDate = SysParamUtil.getAppDate();
		}

		if (object instanceof FinanceMainBaseCtrl && StringUtils.isEmpty(moduleDefiner)) {
			FinanceMainBaseCtrl ctrl = (FinanceMainBaseCtrl) object;

			try {
				closingBal = ctrl.finAmount.getActualValue();
			} catch (WrongValueException we) {
			}

			try {
				if (ctrl.numberOfTerms.getValue() != null && ctrl.numberOfTerms.getValue() > 0) {
					noOfTerms = ctrl.numberOfTerms.getValue();
				} else {
					noOfTerms = ctrl.numberOfTerms_two.getValue();
				}
			} catch (WrongValueException we) {
				//
			}

			try {
				if (StringUtils.isNotEmpty(fm.getRepayBaseRate()) && CalculationConstants.RATE_BASIS_R
						.equals(ctrl.repayRateBasis.getSelectedItem().getValue().toString())) {
					effectiveRate = ctrl.repayRate.getEffRateValue();
				} else {
					effectiveRate = ctrl.repayProfitRate.getValue();
				}
			} catch (WrongValueException we) {
				//
			}
		} else {
			FinanceScheduleDetail detail = schedules.stream()
					.max(Comparator.comparing(FinanceScheduleDetail::getInstNumber)).get();
			int maxInstNo = detail.getInstNumber();
			int minInstNo = 0;

			noOfTerms = schedules.size();
			for (FinanceScheduleDetail schedule : schedules) {

				if (schedule.getSchDate().compareTo(evtFromDate) <= 0) {
					// To avoid disbursement count
					if (schedule.getInstNumber() > 0) {
						minInstNo++;
					}
					effectiveRate = schedule.getCalculatedRate();
					closingBal = PennantApplicationUtil.formateAmount(schedule.getClosingBalance(), format);

				} else if (schedule.getPresentmentId() > 0) {
					effectiveRate = schedule.getCalculatedRate();
					closingBal = PennantApplicationUtil.formateAmount(schedule.getClosingBalance(), format);
					minInstNo++;
					break;
				}
			}
			noOfTerms = maxInstNo - minInstNo;
		}

		fillComboBox(this.fileFormat, "", PennantStaticListUtil.getFileFormatList(), "");

		this.currentPOS.setValue(closingBal);
		this.effectiveRate.setValue(effectiveRate);
		this.noOfInstallments.setValue(noOfTerms);

		if (schdData.getManualScheduleHeader() != null) {
			this.manualSchdHeader = schdData.getManualScheduleHeader();
			ManualScheduleHeader scheduleHeader = schdData.getManualScheduleHeader();
			List<ManualScheduleDetail> details = scheduleHeader.getManualSchedules();

			this.txtFileName.setValue(this.manualSchdHeader.getFileName());
			this.manualSchdHeader.setCurPOSAmt(closingBal);

			if (CollectionUtils.isNotEmpty(details)) {

				if (StringUtils.equals(moduleDefiner, FinServiceEvent.RECALCULATE)) {
					this.noOfInstallments.setValue(details.size());
				}
				doFillScheduleDetails(details);
				this.openScheduleDetailsTab.setSelected(true);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) {
		doClose(true);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.txtFileName.setConstraint("");
		this.txtFileName.setErrorMessage("");

		this.noOfInstallments.setConstraint("");
		this.noOfInstallments.setErrorMessage("");

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
		doRemoveValidation();

		this.btnUpload.setDisabled(true);
		this.listheader_Reason.setVisible(false);

		File file = null;
		try {
			file = manualScheduleFileImport.create(event.getMedia());

			manualSchdHeader = manualScheduleFileImport.read(file, event.getMedia().getContentType());

			manualSchdHeader.setValidSchdUpload(true);
			this.txtFileName.setValue(file.getName());
			manualSchdHeader.setFileName(this.txtFileName.getValue());
			manualSchdHeader.setTransactionDate(DateUtil.getSysDate());
			manualSchdHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			manualSchdHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));

			manualSchdHeader.setPrvSchdDate(getPrvSchdDate());
			manualSchdHeader.setModuleDefiner(moduleDefiner);
			manualSchdHeader.setNumberOfTerms(this.noOfInstallments.getValue());

			manualScheduleFileImport.validate(manualSchdHeader);

			doFillScheduleDetails(manualSchdHeader.getManualSchedules());
			manualSchdHeader.setMaturityDate(finScheduleData.getFinanceMain().getMaturityDate());
			manualSchdHeader.setCurPOSAmt(this.currentPOS.getValue());
			manualSchdHeader.setManualSchdChange(true);

			if (financeDetail != null) {
				financeDetail.getFinScheduleData().setManualScheduleHeader(manualSchdHeader);
			} else {
				finScheduleData.setManualScheduleHeader(manualSchdHeader);
			}

			this.openScheduleDetailsTab.setSelected(true);
		} catch (AppException e) {
			MessageUtil.showError(e.getMessage());
			return;
		} finally {
			manualScheduleFileImport.backUp(file);
			this.btnUpload.setDisabled(false);
			logger.debug(Literal.LEAVING);
		}
	}

	private Date getPrvSchdDate() {
		Date prvSchdDate = null;
		FinanceMain fm = finScheduleData.getFinanceMain();
		if (StringUtils.isEmpty(moduleDefiner)) {
			prvSchdDate = fm.getFinStartDate();
		} else {
			prvSchdDate = fm.getEventFromDate();
			if (prvSchdDate == null || prvSchdDate.compareTo(SysParamUtil.getAppDate()) < 0) {
				prvSchdDate = SysParamUtil.getAppDate();
			}

			// for getting max schedule date of Presentment
			for (FinanceScheduleDetail schedule : finScheduleData.getFinanceScheduleDetails()) {
				if ((schedule.getPresentmentId() > 0 || schedule.getDisbAmount().compareTo(BigDecimal.ZERO) > 0
						|| schedule.getPartialPaidAmt().compareTo(BigDecimal.ZERO) > 0)
						&& schedule.getSchDate().compareTo(prvSchdDate) > 0) {
					prvSchdDate = schedule.getSchDate();
				}
			}
		}
		return prvSchdDate;
	}

	/**
	 * Clear error messages and values
	 */
	private void doClear() {
		this.txtFileName.setText("");
		this.listScheduleDetails.getItems().clear();
		finScheduleData.setManualScheduleHeader(null);
	}

	/**
	 * Render the upload data in Schedule View Tab.
	 * 
	 * @param uploadDetails
	 */
	public void doFillScheduleDetails(List<ManualScheduleDetail> uploadDetails) {
		logger.debug(Literal.ENTERING);

		BigDecimal totPriSchdAmt = BigDecimal.ZERO;
		this.listScheduleDetails.getItems().clear();

		String repaymentdate = "";
		String principalAmt = "";
		String interestFlag = "";
		String rateReviewFlag = "";
		String reason = "";
		int noOfTerms = 0;

		FinanceMain fm = finScheduleData.getFinanceMain();
		int format = CurrencyUtil.getFormat(fm.getFinCcy());

		for (ManualScheduleDetail uploadDetail : uploadDetails) {
			repaymentdate = DateUtil.formatToLongDate(uploadDetail.getSchDate());
			principalAmt = PennantApplicationUtil.amountFormate(uploadDetail.getPrincipalSchd(), format);
			interestFlag = uploadDetail.isPftOnSchDate() ? "Y" : "N";
			rateReviewFlag = uploadDetail.isRvwOnSchDate() ? "Y" : "N";
			reason = uploadDetail.getReason();
			totPriSchdAmt = totPriSchdAmt.add(uploadDetail.getPrincipalSchd());

			if (StringUtils.isNotBlank(uploadDetail.getReason())) {
				this.listheader_Reason.setVisible(true);
				if (manualSchdHeader != null) {
					manualSchdHeader.setValidSchdUpload(false);
				}
			}

			Listitem listitem = new Listitem();
			Listcell listcell;
			listcell = new Listcell("" + (noOfTerms + 1));
			listitem.appendChild(listcell);

			listcell = new Listcell(repaymentdate);
			listcell.setStyle("font-weight:bold;text-align:center;");
			listitem.appendChild(listcell);

			listcell = new Listcell(principalAmt);
			listcell.setStyle("text-align:right;");
			listitem.appendChild(listcell);

			listcell = new Listcell(interestFlag);
			listcell.setStyle("text-align:center;");
			listitem.appendChild(listcell);

			listcell = new Listcell(rateReviewFlag);
			listcell.setStyle("text-align:center;");
			listitem.appendChild(listcell);

			listcell = new Listcell(reason);
			listcell.setStyle("font-weight:bold;color:#F20707;");
			listitem.appendChild(listcell);

			this.listScheduleDetails.appendChild(listitem);
			noOfTerms++;
		}
		fm.setMaturityDate(DateUtil.parse(repaymentdate, DateFormat.LONG_DATE));

		manualSchdHeader.setTotPrincipleAmt(PennantApplicationUtil.formateAmount(totPriSchdAmt, format));

		if (StringUtils.equals(moduleDefiner, FinServiceEvent.RECALCULATE)) {
			manualSchdHeader.setNumberOfTerms(this.noOfInstallments.getValue());
		} else {
			manualSchdHeader.setNumberOfTerms(noOfTerms);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onChange$noOfInstallments(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		manualSchdHeader.setNumberOfTerms(this.noOfInstallments.getValue());
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onSelect$fileFormat() {
		if (this.fileFormat.getSelectedIndex() == 0) {
			this.sampleFileDownload.setDisabled(true);
		} else {
			this.sampleFileDownload.setDisabled(false);
		}
	}

	// sample file download for adding to include or exclude manually
	public void onClick$sampleFileDownload(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		String path = App.getResourcePath(PathUtil.TEMPLATES, PathUtil.MANUAL_SCHEDULES);

		String comboboxValue = getComboboxValue(this.fileFormat);

		String fileName = "ManualSchedule_{1}.xlsx";

		if (comboboxValue.equals("CSV")) {
			fileName = "ManualSchedule_{1}.csv";
		}

		File template = new File(path.concat(File.separator).concat(fileName));

		if (!template.exists()) {
			MessageUtil.showError(String.format(
					"%s template not exists in %s location, please contact system administrator", fileName, path));
			return;
		}

		Filedownload.save(template, DocType.XLSX.getContentType());

		logger.debug(Literal.LEAVING);
	}

	public void uploadIsVisible() {
		this.btnUpload.setVisible(false);
		this.btnImport.setVisible(false);
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		finBasicDetailsCtrl.doWriteBeanToComponents(finHeaderList);
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public void setManualSchdHeader(ManualScheduleHeader manualSchdHeader) {
		this.manualSchdHeader = manualSchdHeader;
	}

	public void setBtnUpload(Button btnUpload) {
		this.btnUpload = btnUpload;
	}

	public void setManualScheduleFileImport(FileImport<ManualScheduleHeader> manualScheduleFileImport) {
		this.manualScheduleFileImport = manualScheduleFileImport;
	}

	public int getNoOfInstallments() {
		return noOfInstallments.getValue();
	}
}

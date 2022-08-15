package com.pennant.webui.finance.upload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.hunter.HunterUpload;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class HunterUploadDialogCtrl extends GFCBaseCtrl<HunterUpload> {
	private static final long serialVersionUID = -1651727895823020231L;
	private static final Logger logger = LogManager.getLogger(HunterUploadDialogCtrl.class);

	protected Window window_HunterUpload;
	protected Button btnUpload;
	protected Button btnRefresh;
	protected Textbox txtFileName;
	private Label label_fileName;
	private Media media = null;
	int totalColumns = 3;
	private DataFormatter objDefaultFormat = new DataFormatter();// for cell value formating
	private FormulaEvaluator formulaEvaluator = null; // for cell value formating
	@Autowired
	private FinanceMainDAO financeMainDAO;
	@Autowired
	private ExtendedFieldRenderDAO extendedFieldRenderDAO;
	private static String leadIdLabel = Labels.getLabel("label_HunterUploadDialog_leadID.value");
	private static String categoryLabel = Labels.getLabel("label_HunterUploadDialog_category.value");
	private static String reasonsLabel = Labels.getLabel("label_HunterUploadDialog_reasons.value");

	public HunterUploadDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "HunterUploadDialog";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_HunterUpload(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(this.window_HunterUpload);

		try {
			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}
			doCheckRights();
			doShowDialog();
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void doShowDialog() {
		logger.debug(Literal.ENTERING);
		this.txtFileName.focus();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		this.btnUpload.setVisible(true);
		this.btnRefresh.setVisible(true);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);
		this.txtFileName.setReadonly(true);
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

	public void onUpload$btnUpload(UploadEvent event) throws Exception {
		logger.debug(Literal.ENTERING);
		media = event.getMedia();
		doResetData();
		if (!uploadDocFormatValidation(media)) {
			return;
		}
		if (media.getName().length() > 100) {
			throw new WrongValueException(this.txtFileName, Labels.getLabel("label_Filename_length_File"));
		} else {
			this.txtFileName.setValue(media.getName());
		}
		doValidations();
		this.btnUpload.setDisabled(true);
		this.btnRefresh.setDisabled(true);
		try {
			readFromExcel(media);
		} catch (Exception e) {
			doResetData();
			MessageUtil.showError(e);
			return;
		} finally {
			this.btnUpload.setDisabled(false);
			this.btnRefresh.setDisabled(false);
			logger.debug(Literal.LEAVING);
		}

		logger.debug(Literal.LEAVING);
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

	/**
	 * Reset the Data onclick the refresh button
	 */
	private void doResetData() {
		logger.debug(Literal.ENTERING);
		doRemoveValidation();
		this.txtFileName.setText("");
		this.label_fileName.setValue("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Validate the ModuleType And File Name
	 */
	private void doValidations() {
		logger.debug(Literal.ENTERING);

		doRemoveValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if (StringUtils.trimToNull(this.txtFileName.getValue()) == null) {
				throw new WrongValueException(this.txtFileName, Labels.getLabel("empty_file"));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (wve.size() > 0) {
			doRemoveValidation();
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug(Literal.LEAVING);
	}

	public void readFromExcel(Media media) throws IOException {
		Workbook myExcelBook = null;
		if (media.getName().toLowerCase().endsWith(".xls")) {
			myExcelBook = new HSSFWorkbook(media.getStreamData());
			this.formulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) myExcelBook);
		} else {
			myExcelBook = new XSSFWorkbook(media.getStreamData());
			this.formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) myExcelBook);
		}

		Sheet myExcelSheet = myExcelBook.getSheetAt(0);
		int rowCount = myExcelSheet.getPhysicalNumberOfRows();
		if (rowCount <= 1) {
			MessageUtil.showError(Labels.getLabel("label_ValidatedUploadFile"));
			return;
		}
		Row row = myExcelSheet.getRow(0);
		if (row == null) {
			MessageUtil.showError(Labels.getLabel("label_ValidatedUploadFile"));
			return;
		}
		String leadIdHeader = this.objDefaultFormat.formatCellValue(row.getCell(0), this.formulaEvaluator);
		if (!StringUtils.equalsIgnoreCase(leadIdHeader, leadIdLabel)) {
			MessageUtil.showError(Labels.getLabel("label_ValidatedUploadFile"));
			return;
		}
		String categoryHeader = this.objDefaultFormat.formatCellValue(row.getCell(1), this.formulaEvaluator);
		if (!StringUtils.equalsIgnoreCase(categoryHeader, categoryLabel)) {
			MessageUtil.showError(Labels.getLabel("label_ValidatedUploadFile"));
			return;
		}
		String reasonsHeader = this.objDefaultFormat.formatCellValue(row.getCell(2), this.formulaEvaluator);
		if (!StringUtils.equalsIgnoreCase(reasonsHeader, reasonsLabel)) {
			MessageUtil.showError(Labels.getLabel("label_ValidatedUploadFile"));
			return;
		}

		List<HunterUpload> hunterUploads = new ArrayList<HunterUpload>();
		for (int i = 1; i < rowCount; i++) {
			Row row1 = myExcelSheet.getRow(i);
			HunterUpload hunterUpload = new HunterUpload();
			// read the columns
			String leadId = this.objDefaultFormat.formatCellValue(row1.getCell(0), this.formulaEvaluator);
			hunterUpload.setLeadId(StringUtils.trim(leadId));
			String category = this.objDefaultFormat.formatCellValue(row1.getCell(1), this.formulaEvaluator);
			hunterUpload.setCategory(StringUtils.trim(category));
			String reason = this.objDefaultFormat.formatCellValue(row1.getCell(2), this.formulaEvaluator);
			hunterUpload.setReasons(StringUtils.trim(reason));
			hunterUploads.add(hunterUpload);
		}
		myExcelBook.close();

		processData(hunterUploads);
	}

	private void processData(List<HunterUpload> hunterUploads) {
		List<String> failedLeadIds = new ArrayList<String>();
		for (HunterUpload hunterUpload : hunterUploads) {
			String leadId2 = hunterUpload.getLeadId();
			if (StringUtils.isNotBlank(leadId2)) {
				try {
					FinanceMain finDetailsForHunter = financeMainDAO.getFinDetailsForHunter(leadId2, "_TEMP");
					if (finDetailsForHunter == null && !failedLeadIds.contains(leadId2)) {
						failedLeadIds.add(leadId2);
					} else {
						String finType = finDetailsForHunter.getFinType();
						String finReference = finDetailsForHunter.getFinReference();
						String tableName = ExtendedFieldConstants.MODULE_LOAN + ("_") + finType + ("_")
								+ (ExtendedFieldConstants.MODULE_ORGANIZATION) + ("_ED");
						Map<String, Object> map = new HashMap<>();
						map.put("Category", hunterUpload.getCategory());
						map.put("Reasons", hunterUpload.getReasons());
						extendedFieldRenderDAO.update(finReference, 1, map, "_TEMP", tableName);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}
		if (!CollectionUtils.isEmpty(failedLeadIds)) {
			String joinedString = String.join(", ", failedLeadIds);
			MessageUtil.showError(joinedString + " Lead Id's does not exist");
		} else {
			if (hunterUploads != null && !hunterUploads.isEmpty()) {

				Clients.showNotification(Labels.getLabel("label_ValidatedDataSaved"), "info", null, null, -1);
			} else {
				MessageUtil.showError(Labels.getLabel("label_ReceiptUpload_File_NoData"));
			}
		}

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
					if (filenamesplit[i] != null && (filenamesplit[i].equalsIgnoreCase("exe")
							|| filenamesplit[i].equalsIgnoreCase("bat") || filenamesplit[i].equalsIgnoreCase("sh")
							|| filenamesplit[i].equalsIgnoreCase("jpg") || filenamesplit[i].equalsIgnoreCase("jpeg")
							|| filenamesplit[i].equalsIgnoreCase("png") || filenamesplit[i].equalsIgnoreCase("rar")
							|| filenamesplit[i].equalsIgnoreCase("zip") || filenamesplit[i].equalsIgnoreCase("msg")
							|| filenamesplit[i].equalsIgnoreCase("doc") || filenamesplit[i].equalsIgnoreCase("docx")
							|| filenamesplit[i].equalsIgnoreCase("ppt") || filenamesplit[i].equalsIgnoreCase("pptx")
							|| filenamesplit[i].equalsIgnoreCase("java") || filenamesplit[i].equalsIgnoreCase("csv")
							|| filenamesplit[i].equalsIgnoreCase("txt"))) {
						MessageUtil.showError(Labels.getLabel("Mandate_Supported_Document"));
						return false;
					}
				}
			}
		}
		return true;
	}
}
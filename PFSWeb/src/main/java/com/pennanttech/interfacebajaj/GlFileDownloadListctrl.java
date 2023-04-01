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
 * * FileName : FileDownloadListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-06-2013 * * Modified
 * Date : 26-06-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-06-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennanttech.interfacebajaj;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.service.administration.SecurityUserService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.dataengine.util.EncryptionUtil;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.interfacebajaj.model.FileDownlaod;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.external.sapgl.SAPGLExtract;
import com.pennanttech.pff.trialbalance.TrailBalanceEngine;
import com.pennanttech.service.AmazonS3Bucket;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/FileDownload/DisbursementFileDownloadList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class GlFileDownloadListctrl extends GFCBaseListCtrl<FileDownlaod> {
	private static final long serialVersionUID = 1L;

	protected Window window_GlFileDownloadList;
	protected Borderlayout borderLayout_GlFileDownloadList;
	protected Paging pagingFileDownloadList;
	protected Listbox listBoxFileDownload;
	protected Listbox listBoxFileDownloadTrailBalance;
	protected Button btnRefresh;
	protected Button btnexecute;
	protected Combobox dimention;
	protected Combobox months;
	protected ExtendedCombobox entityCode;
	private List<ValueLabel> dimentionsList = new ArrayList<>();
	private List<ValueLabel> monthsList = getMonthEnd();

	protected Textbox stateCode;
	protected Button btnSearchState;
	protected Space space_stateType;

	private boolean isTrailBalance = false;

	protected Row row_MonthSelection;
	protected Row row_DateSelection;
	protected Row row_State;
	protected Label id_dimension;
	protected Hbox hbox_Dimension;
	protected Datebox fromdate;
	protected Datebox toDate;

	@Autowired
	protected DataEngineConfig dataEngineConfig;
	private Button downlaod;

	protected AmazonS3Bucket bucket;

	protected SecurityUserService securityUserService;

	/**
	 * default constructor.<br>
	 */
	public GlFileDownloadListctrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FileDownload";
		super.pageRightName = "FileDownload";
		super.tableName = "DE_FILE_CONTROL_VIEW";
		super.queueTableName = "DE_FILE_CONTROL_VIEW";

		if (StringUtils.equals(getArgument("module"), "TrailBalance")) {
			this.isTrailBalance = true;
		} else {
			this.isTrailBalance = false;
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_GlFileDownloadList(Event event) {
		logger.debug(Literal.ENTERING);

		if (isTrailBalance) {
			// Set the page level components.
			setPageComponents(window_GlFileDownloadList, borderLayout_GlFileDownloadList,
					listBoxFileDownloadTrailBalance, pagingFileDownloadList);
			setItemRender(new FileDownloadTrailBalanceListModelItemRenderer());
			this.listBoxFileDownload.setVisible(false);
		} else {
			// Set the page level components.
			setPageComponents(window_GlFileDownloadList, borderLayout_GlFileDownloadList, listBoxFileDownload, null);
			setItemRender(new FileDownloadListModelItemRenderer());
			this.listBoxFileDownloadTrailBalance.setVisible(false);
			setComparator(new FileDownloadComparator());
		}

		dimentionsList.add(
				new ValueLabel(TrailBalanceEngine.Dimension.STATE.name(), TrailBalanceEngine.Dimension.STATE.name()));
		dimentionsList.add(new ValueLabel(TrailBalanceEngine.Dimension.CONSOLIDATE.name(),
				TrailBalanceEngine.Dimension.CONSOLIDATE.name()));

		fillComboBox(dimention, "", dimentionsList, "");
		fillComboBox(months, "", monthsList, "");

		// Application Deployment Date
		registerField("Id", SortOrder.DESC);
		registerField("Name");
		registerField("Status");
		registerField("CONFIGID");
		registerField("POSTEVENT");
		registerField("FileName");
		registerField("FileLocation");
		registerField("UserId");
		registerField("endTime");
		registerField("ValueDate", SortOrder.DESC);

		if (isTrailBalance) {
			registerField("startDate");
			registerField("endDate");
		}

		doRenderPage();
		search();
		doSetFieldProperties();
		this.listBoxFileDownload.setHeight(this.borderLayoutHeight - 100 + "px");
		this.listBoxFileDownloadTrailBalance.setHeight(this.borderLayoutHeight - 100 + "px");
		logger.debug(Literal.LEAVING);
	}

	public class FileDownloadComparator implements Comparator<Object>, Serializable {
		private static final long serialVersionUID = -8606975433219761922L;

		public FileDownloadComparator() {

		}

		@Override
		public int compare(Object o1, Object o2) {
			FileDownlaod data = (FileDownlaod) o1;
			FileDownlaod data2 = (FileDownlaod) o2;
			return data2.getValueDate().compareTo(data.getValueDate());
		}
	}

	protected void doAddFilters() {
		super.doAddFilters();
		List<String> list = new ArrayList<>();

		if (isTrailBalance) {
			list.add("TRIAL_BALANCE_EXPORT_STATE");
			list.add("TRIAL_BALANCE_EXPORT_CONSOLIDATE");
			if (App.DATABASE == App.Database.ORACLE) {
				this.searchObject.addWhereClause(" rownum<11 ");
			} else {

			}
		} else {
			list.add("GL_TRANSACTION_EXPORT");
			list.add("GL_TRANSACTION_SUMMARY_EXPORT");
		}

		this.searchObject.addFilterIn("NAME", list);
	}

	private void doSetFieldProperties() {

		this.entityCode.setMaxlength(8);
		this.entityCode.setTextBoxWidth(100);
		this.entityCode.setMandatoryStyle(true);
		this.entityCode.setModuleName("Entity");
		this.entityCode.setValueColumn("EntityCode");
		this.entityCode.setDescColumn("EntityDesc");
		this.entityCode.setValidateColumns(new String[] { "EntityCode" });

		this.stateCode.setMaxlength(500);
		this.stateCode.setDisabled(true);
		this.btnSearchState.setDisabled(true);
		this.space_stateType.setSclass("");

		this.toDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.fromdate.setFormat(DateFormat.SHORT_DATE.getPattern());

		if (isTrailBalance) {
			this.row_MonthSelection.setVisible(false);
			this.row_State.setVisible(false);
		} else {
			this.row_DateSelection.setVisible(false);
			this.row_State.setVisible(false);
			this.id_dimension.setVisible(false);
			this.hbox_Dimension.setVisible(false);
		}
	}

	/**
	 * Call the FileDownload dialog with a new empty entry. <br>
	 */
	public void onClick$btnRefresh(Event event) {
		refresh();
	}

	/**
	 * Call the FileDownload dialog with a new empty entry. <br>
	 */
	public void onClick$btnexecute(Event event) {
		doSetValidations();
		ArrayList<WrongValueException> wve = new ArrayList<>();

		if (isTrailBalance) {
			try {
				this.dimention.getValue();
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		try {
			this.months.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.entityCode.isReadonly())
				this.entityCode.setConstraint(new PTStringValidator(
						Labels.getLabel("label_MandateDialog_EntityCode.value"), null, true, true));
			this.entityCode.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (isTrailBalance) {
			try {
				this.fromdate.getValue();
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				this.toDate.getValue();
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (this.toDate != null && DateUtil.compare(this.toDate.getValue(), this.fromdate.getValue()) < 0) {
					throw new WrongValueException(this.toDate, "To Date should be greater than From Date");
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (this.toDate != null
						&& DateUtil.compare(this.toDate.getValue(), SysParamUtil.getLastBusinessdate()) > 0) {
					throw new WrongValueException(this.toDate, "To Date should be less than Last Business Date");
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (this.fromdate != null && this.toDate != null) {
					int startMonth = SysParamUtil.getValueAsInt("FINANCIAL_YEAR_START_MONTH");
					Calendar finYearEnd = Calendar.getInstance();
					finYearEnd.setTime(this.fromdate.getValue());

					if (startMonth == 1) {
						finYearEnd.set(Calendar.MONTH, Calendar.DECEMBER);
					} else {
						if (finYearEnd.get(Calendar.MONTH) < (startMonth - 1)) {
							finYearEnd.set(Calendar.MONTH, startMonth - 2);
						} else {
							finYearEnd.set(Calendar.YEAR, finYearEnd.get(Calendar.YEAR) + 1);
							finYearEnd.set(Calendar.MONTH, startMonth - 2);
						}
					}

					finYearEnd.set(Calendar.DATE, finYearEnd.getActualMaximum(Calendar.DATE));

					if (DateUtil.compare(this.toDate.getValue(), finYearEnd.getTime()) > 0) {
						throw new WrongValueException(this.toDate,
								"From Date and To Date should be with in financial year");
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (!this.btnSearchState.isDisabled())
					this.stateCode.getValue();
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		try {
			String selectedDimention = dimention.getSelectedItem().getValue();
			String entityCode = this.entityCode.getValue();

			if (isTrailBalance) {
				TrailBalanceEngine trialbal = new TrailBalanceEngine((DataSource) SpringUtil.getBean("dataSource"),
						getUserWorkspace().getUserDetails().getUserId(), SysParamUtil.getAppDate(),
						SysParamUtil.getAppDate(), this.fromdate.getValue(), this.toDate.getValue());

				if (trialbal.isBatchExists(selectedDimention, entityCode)) {
					final String msg = "Trial balance already generated for the selected month.\n Do you want to continue?";
					MessageUtil.confirm(msg, evnt -> {
						if (Messagebox.ON_YES.equals(evnt.getName())) {
							processThread(selectedDimention, trialbal);
						}
					});
				} else {
					processThread(selectedDimention, trialbal);
				}
			} else {
				Date valueDate = null;
				Date appDate = null;
				String selectedMonth = months.getSelectedItem().getValue();
				appDate = DateUtil.parse(selectedMonth, PennantConstants.DBDateFormat);
				valueDate = appDate;
				Date stateDate = DateUtil.getMonthStart(appDate);
				Date endDate = DateUtil.getMonthEnd(appDate);
				new SAPGLExtract((DataSource) SpringUtil.getBean("dataSource"),
						getUserWorkspace().getUserDetails().getUserId(), valueDate, appDate).extractReport(
								new String[] { this.entityCode.getValue(), this.entityCode.getDescription() },
								stateDate, endDate);

			}

		} catch (Exception e) {
			MessageUtil.showError(e.getMessage());
		}

		refresh();

	}

	private void processThread(String selectedDimention, TrailBalanceEngine trialbal) throws Exception {
		DataEngineStatus status = TrailBalanceEngine.EXTRACT_STATUS;
		status.setStatus("I");

		if (selectedDimention.equals(TrailBalanceEngine.Dimension.STATE.name())) {
			trialbal.extractReport(TrailBalanceEngine.Dimension.STATE,
					new String[] { this.entityCode.getValue(), this.entityCode.getDescription() },
					this.stateCode.getValue());
		} else if (selectedDimention.equals(TrailBalanceEngine.Dimension.CONSOLIDATE.name())) {
			trialbal.extractReport(TrailBalanceEngine.Dimension.CONSOLIDATE,
					new String[] { this.entityCode.getValue(), this.entityCode.getDescription() },
					this.stateCode.getValue());
		}
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidations() {
		if (!this.dimention.isDisabled()) {
			this.dimention.setConstraint(
					new StaticListValidator(dimentionsList, Labels.getLabel("label_GLFileList_Dimension.value")));
		}

		if (this.row_MonthSelection.isVisible() && !this.months.isDisabled()
				&& PennantConstants.List_Select.equals(this.months.getSelectedItem().getValue()))
			this.months.setConstraint(
					new StaticListValidator(monthsList, Labels.getLabel("label_GLFileList_Months.value")));

		if (this.row_DateSelection.isVisible()) {
			this.fromdate
					.setConstraint(new PTDateValidator(Labels.getLabel("label_TrailBalance_FromDate.value"), true));
			this.toDate.setConstraint(new PTDateValidator(Labels.getLabel("label_TrailBalance_ToDate.value"), true));
		}

		if (!this.btnSearchState.isDisabled()) {
			this.stateCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_TrailBalance_StateCode.value"), null, false, true));
		}
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering ");
		this.dimention.setConstraint("");
		this.months.setConstraint("");
		this.toDate.setConstraint("");
		this.fromdate.setConstraint("");
		this.entityCode.setConstraint("");
		this.stateCode.setConstraint("");

		logger.debug("Leaving ");
	}

	public void onClick_Downlaod(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		try {

			Button downloadButt = (Button) event.getOrigin().getTarget();
			FileDownlaod fileDownlaod = (FileDownlaod) downloadButt.getAttribute("object");

			if (com.pennanttech.dataengine.Event.MOVE_TO_S3_BUCKET.name().equals(fileDownlaod.getPostEvent())) {
				String prefix = loadS3Bucket(fileDownlaod.getConfigId());

				downloadFromS3Bucket(prefix, fileDownlaod.getFileName());
			} else {
				downloadFromServer(fileDownlaod);
			}
			dataEngineConfig.saveDowloadHistory(fileDownlaod.getId(), getUserWorkspace().getUserDetails().getUserId());
			refresh();
		} catch (Exception e) {
			MessageUtil.showError(e.getMessage());
		}
		logger.debug(Literal.LEAVING);
	}

	private String loadS3Bucket(long configId) {

		EventProperties eventproperties = dataEngineConfig.getEventProperties(configId, "S3");

		bucket = new AmazonS3Bucket(eventproperties.getRegionName(), eventproperties.getBucketName(),
				EncryptionUtil.decrypt(eventproperties.getAccessKey()),
				EncryptionUtil.decrypt(eventproperties.getSecretKey()));

		return eventproperties.getPrefix();
	}

	private void downloadFromServer(FileDownlaod fileDownlaod) throws FileNotFoundException, IOException {
		String filePath = fileDownlaod.getFileLocation();
		String fileName = fileDownlaod.getFileName();

		if (filePath != null && fileName != null) {
			filePath = filePath.concat("/").concat(fileName);
		}

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		InputStream inputStream = new FileInputStream(filePath);
		int data;
		while ((data = inputStream.read()) >= 0) {
			stream.write(data);
		}

		inputStream.close();
		inputStream = null;
		Filedownload.save(stream.toByteArray(), "text/csv", fileName);
		stream.close();
	}

	private void downloadFromS3Bucket(String prefix, String fileName) {
		String key = prefix.concat("/").concat(fileName);

		try {
			byte[] fileData = bucket.getObject(key);
			Filedownload.save(fileData, "text/csv", fileName);
		} catch (Exception e) {
			throw new AppException(e.getMessage(), e);
		}
	}

	private void refresh() {
		doRemoveValidation();
		doClearMessage();
		doClearData();
		doReset();
		search();
	}

	protected void doClearMessage() {
		this.entityCode.setErrorMessage("");
	}

	private void doClearData() {

		this.entityCode.setValue("");
		if (isTrailBalance) {
			this.dimention.setValue(Labels.getLabel("Combo.Select"));
			this.stateCode.setValue("");
			this.btnSearchState.setDisabled(true);
			this.fromdate.setText("");
			this.toDate.setText("");
			this.row_State.setVisible(false);
		} else {
			this.months.setValue(Labels.getLabel("Combo.Select"));
			this.row_State.setVisible(false);
		}
	}

	/**
	 * Item renderer for listitems in the listbox.
	 * 
	 */
	private class FileDownloadListModelItemRenderer implements ListitemRenderer<FileDownlaod>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, FileDownlaod fileDownlaod, int count) {
			Listcell lc;

			if (item instanceof Listgroup) {
				item.appendChild(new Listcell(
						(DateUtil.format(fileDownlaod.getValueDate(), DateFormat.LONG_MONTH.getPattern()))));
			} else if (item instanceof Listgroupfoot) {
				Listcell cell = new Listcell("");
				cell.setSpan(4);
				item.appendChild(cell);

			} else {

				lc = new Listcell(fileDownlaod.getName());
				lc.setParent(item);

				lc = new Listcell(fileDownlaod.getFileName());
				lc.setParent(item);

				lc = new Listcell(DateUtil.format(fileDownlaod.getValueDate(), PennantConstants.dateFormat));
				lc.setParent(item);

				lc = new Listcell(ExecutionStatus.getStatus(fileDownlaod.getStatus()).getValue());
				lc.setParent(item);

				lc = new Listcell();
				downlaod = new Button();
				downlaod.addForward("onClick", self, "onClick_Downlaod");
				lc.appendChild(downlaod);
				downlaod.setLabel("Download");
				downlaod.setTooltiptext("Download");

				downlaod.setAttribute("object", fileDownlaod);
				StringBuilder builder = new StringBuilder();
				builder.append(fileDownlaod.getFileLocation());
				builder.append(File.separator);
				builder.append(fileDownlaod.getFileName());

				if (!ExecutionStatus.S.name().equals(fileDownlaod.getStatus())) {
					downlaod.setDisabled(true);
					downlaod.setTooltiptext("SAPGL request for file generation failed.");
				}

				if (!com.pennanttech.dataengine.Event.MOVE_TO_S3_BUCKET.name().equals(fileDownlaod.getPostEvent())) {
					File file = new File(builder.toString());
					if (!file.exists()) {
						downlaod.setDisabled(true);
						downlaod.setTooltiptext("File not available.");
					}
				}

				lc.setParent(item);
			}

		}
	}

	/**
	 * 
	 * @param startDate
	 * @param endDate
	 * @param sortOrder
	 * @return
	 */
	public static List<ValueLabel> getMonthEndList(Date startDate, Date endDate, SortOrder sortOrder) {

		List<ValueLabel> monthEndList = new ArrayList<ValueLabel>();
		startDate = DateUtil.getMonthEnd(startDate);

		// Prepare Month End list between two dates, by Default Ascending
		while (DateUtil.getMonthEnd(endDate).compareTo(startDate) > 0) {

			monthEndList.add(new ValueLabel(DateUtil.format(startDate, PennantConstants.DBDateFormat),
					DateUtil.format(startDate, DateFormat.LONG_MONTH.getPattern())));

			startDate = DateUtil.addDays(startDate, 1);
			startDate = DateUtil.getMonthEnd(startDate);
		}

		// Month End List in Descending order
		if (sortOrder == SortOrder.DESC) {
			Collections.reverse(monthEndList);
		}
		return monthEndList;
	}

	/**
	 * Item renderer for listitems in the listbox.
	 * 
	 */
	private class FileDownloadTrailBalanceListModelItemRenderer
			implements ListitemRenderer<FileDownlaod>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, FileDownlaod fileDownlaod, int count) {
			Listcell lc;

			lc = new Listcell(fileDownlaod.getFileName());
			lc.setParent(item);

			lc = new Listcell(getSecurityUser(fileDownlaod.getUserId()));
			lc.setParent(item);

			lc = new Listcell(DateUtil.format(fileDownlaod.getStartDate(), PennantConstants.dateFormat));
			lc.setParent(item);

			lc = new Listcell(DateUtil.format(fileDownlaod.getEndDate(), PennantConstants.dateFormat));
			lc.setParent(item);

			lc = new Listcell(DateUtil.format(fileDownlaod.getValueDate(), PennantConstants.dateFormat));
			lc.setParent(item);

			lc = new Listcell(ExecutionStatus.getStatus(fileDownlaod.getStatus()).getValue());
			lc.setParent(item);

			lc = new Listcell();
			downlaod = new Button();
			downlaod.addForward("onClick", self, "onClick_Downlaod");
			lc.appendChild(downlaod);
			downlaod.setLabel("Download");
			downlaod.setTooltiptext("Download");

			downlaod.setAttribute("object", fileDownlaod);
			StringBuilder builder = new StringBuilder();
			builder.append(fileDownlaod.getFileLocation());
			builder.append(File.separator);
			builder.append(fileDownlaod.getFileName());

			if (!ExecutionStatus.S.name().equals(fileDownlaod.getStatus())) {
				downlaod.setDisabled(true);
				downlaod.setTooltiptext("SAPGL request for file generation failed.");
			}

			if (!com.pennanttech.dataengine.Event.MOVE_TO_S3_BUCKET.name().equals(fileDownlaod.getPostEvent())) {
				File file = new File(builder.toString());
				if (!file.exists()) {
					downlaod.setDisabled(true);
					downlaod.setTooltiptext("File not available.");
				}
			}

			lc.setParent(item);
		}

	}

	private String getSecurityUser(long usrId) {

		SecurityUser securityUser = getSecurityUserService().getSecurityUserById(usrId);
		return securityUser.getUsrLogin();

	}

	private List<ValueLabel> getMonthEnd() {

		List<ValueLabel> monthEndList = new ArrayList<ValueLabel>();

		SimpleDateFormat valueDateFormat = new SimpleDateFormat(PennantConstants.DBDateFormat);
		SimpleDateFormat displayDateFormat = new SimpleDateFormat(DateFormat.LONG_MONTH.getPattern());

		GregorianCalendar gc = null;
		Date appDate = SysParamUtil.getAppDate();
		int month = DateUtil.getMonth(appDate);
		int year = DateUtil.getYear(appDate);
		Calendar startDate = new GregorianCalendar(2017, Calendar.AUGUST, 01);
		Calendar endDate = new GregorianCalendar();
		endDate.setTime(appDate);
		int yearsInBetween = endDate.get(Calendar.YEAR) - startDate.get(Calendar.YEAR);
		int monthsDiff = yearsInBetween * 12 + endDate.get(Calendar.MONTH) - startDate.get(Calendar.MONTH);

		for (int i = 1; i <= monthsDiff; i++) {
			if (month == 0) {
				month = 11;
				year = year - 1;
			} else {
				month = month - 1;
			}
			gc = new GregorianCalendar();
			gc.set(year, month - 1, 1);
			monthEndList.add(new ValueLabel(valueDateFormat.format(DateUtil.getMonthEnd(gc.getTime())),
					displayDateFormat.format(gc.getTime())));
		}
		return monthEndList;
	}

	/**
	 * Salutation codes will be populated based on the selected gender code.
	 * 
	 * @param event
	 */
	public void onSelect$dimention(Event event) {
		logger.debug(Literal.ENTERING);

		String code = dimention.getSelectedItem().getValue();

		if (PennantConstants.List_Select.equals(code)
				|| StringUtils.equals(code, TrailBalanceEngine.Dimension.CONSOLIDATE.name())) {
			this.stateCode.setDisabled(true);
			this.btnSearchState.setDisabled(true);
			this.space_stateType.setSclass("");
			this.row_State.setVisible(false);
			this.stateCode.setText("");
		} else {
			this.stateCode.setDisabled(false);
			this.btnSearchState.setDisabled(false);
			this.space_stateType.setSclass("");
			this.row_State.setVisible(true);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSearchState(Event event) {
		logger.debug("Entering  " + event.toString());
		this.stateCode.setErrorMessage("");
		Object dataObject = MultiSelectionSearchListBox.show(this.window_GlFileDownloadList, "Province",
				String.valueOf(this.stateCode.getValue()), null);
		if (dataObject != null) {
			String details = (String) dataObject;
			this.stateCode.setValue(details);
			this.stateCode.setErrorMessage("");
		}
		logger.debug("Leaving  " + event.toString());

	}

	public SecurityUserService getSecurityUserService() {
		return securityUserService;
	}

	public void setSecurityUserService(SecurityUserService securityUserService) {
		this.securityUserService = securityUserService;
	}
}
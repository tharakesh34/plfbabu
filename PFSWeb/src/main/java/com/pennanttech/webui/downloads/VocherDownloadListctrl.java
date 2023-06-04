package com.pennanttech.webui.downloads;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.service.administration.SecurityUserService;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.dataengine.util.EncryptionUtil;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.interfacebajaj.model.FileDownlaod;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.external.gl.VocherDownloadService;
import com.pennanttech.service.AmazonS3Bucket;

public class VocherDownloadListctrl extends GFCBaseListCtrl<FileDownlaod> {
	private static final long serialVersionUID = 1L;

	protected Window window_VocherDownloadList;
	protected Borderlayout borderLayout_VocherDownloadList;
	protected Paging pagingVocherDownloadList;
	protected Listbox listBoxVocherDownload;
	protected Button btnRefresh;
	protected Button btnexecute;
	protected Datebox fromDate;
	protected Datebox toDate;
	private Button downlaod;

	protected DataEngineConfig dataEngineConfig;
	protected SecurityUserService securityUserService;
	private VocherDownloadService vocherDownloadService;

	protected AmazonS3Bucket bucket;
	private Map<Long, String> userMap = new HashMap<>();

	/**
	 * default constructor.<br>
	 */
	public VocherDownloadListctrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FileDownload";
		super.pageRightName = "FileDownload";
		super.tableName = "DE_FILE_CONTROL_VIEW";
		super.queueTableName = "DE_FILE_CONTROL_VIEW";
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_VocherDownloadList(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_VocherDownloadList, borderLayout_VocherDownloadList, listBoxVocherDownload,
				pagingVocherDownloadList);
		setItemRender(new VocherDownloadListModelItemRenderer());

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
		registerField("startDate");

		fromDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		toDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		doRenderPage();
		search();
		doSetFieldProperties();
		this.listBoxVocherDownload.setHeight(this.borderLayoutHeight - 100 + "px");
		logger.debug(Literal.LEAVING);
	}

	protected void doAddFilters() {
		super.doAddFilters();
		this.searchObject.addFilterEqual("NAME", "GL_VOCHER_DOWNLOAD_TALLY");
	}

	private void doSetFieldProperties() {

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

		try {
			this.fromDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.toDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
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
			vocherDownloadService.downloadVocher(getUserWorkspace().getLoggedInUser().getUserId(),
					getUserWorkspace().getLoggedInUser().getUserName(), this.fromDate.getValue(),
					this.toDate.getValue());
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		refresh();

	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidations() {
		Date appDate = SysParamUtil.getAppDate();

		if (this.fromDate.getValue() != null && this.fromDate.getValue().compareTo(SysParamUtil.getAppDate()) > 0) {
			throw new WrongValueException(this.fromDate,
					Labels.getLabel("DATE_ALLOWED_ON_BEFORE",
							new String[] { Labels.getLabel("label_VocherDownload_FromDate.value"),
									DateUtil.format(appDate, DateFormat.SHORT_DATE) }));
		}

		if (this.toDate.getValue() != null && this.toDate.getValue().compareTo(SysParamUtil.getAppDate()) > 0) {
			throw new WrongValueException(this.fromDate,
					Labels.getLabel("DATE_ALLOWED_ON_BEFORE",
							new String[] { Labels.getLabel("label_VocherDownload_ToDate.value"),
									DateUtil.format(appDate, DateFormat.SHORT_DATE) }));
		}

		if ((this.fromDate.getValue() != null && this.toDate.getValue() != null)
				&& this.fromDate.getValue().compareTo(toDate.getValue()) > 0) {
			throw new WrongValueException(this.toDate,
					Labels.getLabel("DATE_ALLOWED_ON_AFTER",
							new String[] { Labels.getLabel("label_VocherDownload_ToDate.value"),
									DateUtil.format(this.fromDate.getValue(), DateFormat.SHORT_DATE) }));
		}

		this.fromDate.setConstraint(new PTDateValidator(Labels.getLabel("label_VocherDownload_FromDate.value"), true));
		this.toDate.setConstraint(new PTDateValidator(Labels.getLabel("label_VocherDownload_ToDate.value"), true));
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		this.fromDate.setConstraint("");
		this.toDate.setConstraint("");
		logger.debug(Literal.LEAVING);
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

		try (ByteArrayOutputStream stream = new ByteArrayOutputStream();) {
			try (InputStream inputStream = new FileInputStream(filePath)) {
				int data;
				while ((data = inputStream.read()) >= 0) {
					stream.write(data);
				}

				Filedownload.save(stream.toByteArray(), "text/plain", fileName);
			}
		}
	}

	private void downloadFromS3Bucket(String prefix, String fileName) {
		String key = prefix.concat("/").concat(fileName);

		try {
			byte[] fileData = bucket.getObject(key);
			Filedownload.save(fileData, "text/plain", fileName);
		} catch (Exception e) {
			throw new AppException(e.getMessage(), e);
		}
	}

	private void refresh() {
		doRemoveValidation();
		doClearData();
		doReset();
		search();
	}

	private void doClearData() {
		this.fromDate.setText("");
		this.toDate.setText("");
	}

	private class VocherDownloadListModelItemRenderer implements ListitemRenderer<FileDownlaod>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, FileDownlaod fileDownlaod, int count) {
			Listcell lc;

			lc = new Listcell(fileDownlaod.getFileName());
			lc.setParent(item);

			lc = new Listcell(getSecurityUser(fileDownlaod.getUserId()));
			lc.setParent(item);

			lc = new Listcell(DateUtil.format(fileDownlaod.getValueDate(), DateFormat.LONG_DATE));
			lc.setParent(item);

			lc = new Listcell(ExecutionStatus.getStatus(fileDownlaod.getStatus()).getValue());
			lc.setParent(item);

			lc = new Listcell();
			downlaod = new Button();
			downlaod.addForward("onClick", self, "onClick_Downlaod");
			lc.appendChild(downlaod);
			downlaod.setLabel("Download");
			downlaod.setTooltiptext("Download");
			downlaod.setAutodisable(
					"btnexecute, button_FileDownloadList_FileDownloadSearchDialog, btnRefresh, help, fromDate, toDate");

			downlaod.setAttribute("object", fileDownlaod);
			StringBuilder builder = new StringBuilder();
			builder.append(fileDownlaod.getFileLocation());
			builder.append(File.separator);
			builder.append(fileDownlaod.getFileName());

			if (!ExecutionStatus.S.name().equals(fileDownlaod.getStatus())) {
				downlaod.setDisabled(true);
				downlaod.setTooltiptext("File generation failed.");
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
		return userMap.computeIfAbsent(usrId, userLogin -> getUserLogin(usrId));
	}

	private String getUserLogin(long usrId) {
		return securityUserService.getSecurityUserById(usrId).getUsrLogin();
	}

	@Autowired
	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}

	@Autowired
	public void setSecurityUserService(SecurityUserService securityUserService) {
		this.securityUserService = securityUserService;
	}

	@Autowired
	public void setVocherDownloadService(VocherDownloadService vocherDownloadService) {
		this.vocherDownloadService = vocherDownloadService;
	}

}

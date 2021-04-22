package com.pennant.webui.finance.upload;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.model.ratechangeupload.RateChangeUploadHeader;
import com.pennant.pff.service.RateChangeupload.RateChangeUploadService;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.excecution.ProcessExecution;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class RateChangeUploadDialogCtrl extends GFCBaseCtrl<Configuration> {
	private static final Logger logger = LogManager.getLogger(RateChangeUploadDialogCtrl.class);
	private static final long serialVersionUID = 1297405999029019920L;

	protected Window window_RateChangeUploadDialogCtrl;
	protected Textbox fileName;
	protected Button btnFileUpload;
	protected Rows panelRows;
	protected Button btnImport;
	private Media media = null;
	private File file = null;
	protected Timer timer;
	private RateChangeUploadService rateChangeUploadService;

	protected DataEngineConfig dataEngineConfig;
	private long userId;
	protected Combobox entityType;
	List<ValueLabel> defaultEntityType;
	private DataEngineStatus RATE_CHANGE_UPLOAD = new DataEngineStatus(PennantConstants.RATE_CHANGE_UPLOAD);

	public RateChangeUploadDialogCtrl() {
		super();
	}

	public void onCreate$window_RateChangeUploadDialogCtrl(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_RateChangeUploadDialogCtrl);
		doSetFieldProperties();
		this.userId = getUserWorkspace().getLoggedInUser().getUserId();
		getUserWorkspace().allocateAuthorities(super.pageRightName);

		getConfigData();

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		defaultEntityType = new ArrayList<ValueLabel>(1);
		List<Entity> entities = rateChangeUploadService.getEntites();
		for (Entity entity : entities) {
			defaultEntityType.add(new ValueLabel(entity.getEntityCode(), entity.getEntityDesc()));
		}

		fillComboBox(this.entityType, "", defaultEntityType, "");
	}

	private void doFillPanel(Configuration config, DataEngineStatus ds) {
		ProcessExecution pannel = new ProcessExecution();
		pannel.setId(config.getName());
		pannel.setBorder("normal");
		pannel.setTitle(config.getName());
		pannel.setWidth("480px");
		pannel.setProcess(ds);
		pannel.render();

		Row rows = (Row) panelRows.getLastChild();

		if (rows == null) {
			Row row = new Row();
			row.setStyle("overflow: visible !important");
			Hbox hbox = new Hbox();
			hbox.setAlign("center");
			hbox.appendChild(pannel);
			row.appendChild(hbox);
			panelRows.appendChild(row);
		} else {
			Hbox hbox = null;
			List<Hbox> item = rows.getChildren();
			hbox = (Hbox) item.get(0);
			if (hbox.getChildren().size() == 2) {
				rows = new Row();
				rows.setStyle("overflow: visible !important");
				hbox = new Hbox();
				hbox.setAlign("center");
				hbox.appendChild(pannel);
				rows.appendChild(hbox);
				panelRows.appendChild(rows);
			} else {
				hbox.appendChild(pannel);
			}
		}
	}

	public void onClick$btnImport(Event event) throws InterruptedException {
		this.btnImport.setDisabled(true);

		if (getComboboxValue(this.entityType).equals(PennantConstants.List_Select)) {
			MessageUtil.showError("Please Select Entity Code.");
			return;
		}
		if (media == null) {
			MessageUtil.showError("Please upload file.");
			return;
		}

		try {
			try {
				Thread thread = new Thread(new ProcessData(this.userId, RATE_CHANGE_UPLOAD, this.entityType));
				thread.start();
			} catch (Exception e) {
				MessageUtil.showError(e);
				return;
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
			return;
		}

	}

	public class ProcessData implements Runnable {
		private long userId;
		private DataEngineStatus status;
		private Combobox entityType;

		public ProcessData(long userId, DataEngineStatus status, Combobox entityType) {
			this.userId = userId;
			this.status = status;
			this.entityType = entityType;
		}

		@Override
		public void run() {
			try {
				RateChangeUploadHeader rateChangeUploadHeader = new RateChangeUploadHeader();
				rateChangeUploadHeader.setUserId(this.userId);
				rateChangeUploadHeader.setDeStatus(status);
				rateChangeUploadHeader.setFile(file);
				rateChangeUploadHeader.setMedia(media);
				rateChangeUploadHeader.setEntityCode(getComboboxValue(this.entityType));
				rateChangeUploadService.importData(rateChangeUploadHeader);
			} catch (Exception e) {
				logger.error("Exception:", e);
			}
		}
	}

	public void getConfigData() throws Exception {
		ValueLabel valueLabel = null;
		List<ValueLabel> menuList = new ArrayList<ValueLabel>();
		List<Configuration> configList = dataEngineConfig.getMenuList(true);

		for (Configuration config : configList) {
			String configName = config.getName();
			if ("RATE_CHANGE_UPLOAD".equals(configName)) {
				RATE_CHANGE_UPLOAD = dataEngineConfig.getLatestExecution("RATE_CHANGE_UPLOAD");
				valueLabel = new ValueLabel(configName, "RateChange process upload");
				doFillPanel(config, RATE_CHANGE_UPLOAD);
				menuList.add(valueLabel);
			}
		}
	}

	public void onUpload$btnFileUpload(UploadEvent event) throws Exception {
		fileName.setText("");
		media = event.getMedia();

		if (!MediaUtil.isExcel(media)) {
			MessageUtil.showError(Labels.getLabel("upload_document_invalid", new String[] { "excel" }));
			media = null;
			return;
		}

		fileName.setText(media.getName());
		this.btnImport.setDisabled(false);
	}

	public void onTimer$timer(Event event) {
		List<Row> rows = this.panelRows.getChildren();
		for (Row row : rows) {
			List<Hbox> hboxs = row.getChildren();
			for (Hbox hbox : hboxs) {
				List<ProcessExecution> list = hbox.getChildren();
				for (ProcessExecution pe : list) {
					String status = pe.getProcess().getStatus();

					if ("RATE_CHANGE_UPLOAD".equals(pe.getProcess().getName())) {
						if (ExecutionStatus.I.name().equals(status)) {
							this.btnImport.setDisabled(true);
							this.btnFileUpload.setDisabled(true);
						} else {
							this.btnImport.setDisabled(false);
							this.btnFileUpload.setDisabled(false);
						}
					}
					pe.render();
				}
			}
		}
	}

	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}

	public RateChangeUploadService getRateChangeUploadService() {
		return rateChangeUploadService;
	}

	public void setRateChangeUploadService(RateChangeUploadService rateChangeUploadService) {
		this.rateChangeUploadService = rateChangeUploadService;
	}

}

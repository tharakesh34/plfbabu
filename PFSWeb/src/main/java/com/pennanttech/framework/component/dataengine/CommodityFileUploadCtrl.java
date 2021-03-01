package com.pennanttech.framework.component.dataengine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.excecution.ProcessExecution;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.DocType;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.commodity.webui.CommodityFileUploadResponce;

public class CommodityFileUploadCtrl extends GFCBaseCtrl<Configuration> {
	private static final Logger logger = LogManager.getLogger(CommodityFileUploadCtrl.class);
	private static final long serialVersionUID = 1297405999029019920L;

	protected Window window_CollateralFileUploadCtrl;
	protected Textbox fileName;
	protected Button btnFileUpload;
	protected Rows panelRows;
	protected Button btnImport;

	private transient Media media = null;
	private File file = null;

	protected Timer timer;

	protected transient DataEngineConfig dataEngineConfig;
	private long userId;
	private Configuration config;

	private DataEngineStatus dataEngineStatus = new DataEngineStatus(PennantConstants.COLLATERAL_VALUE_UPDATE);

	private static final String COLLETARAL_VALUE_UPDATE = "COLLETARAL_VALUE_UPDATE";

	@Autowired(required = false)
	private transient CommodityFileUploadResponce commodityFileUploadResponce;

	public CommodityFileUploadCtrl() {
		super();
	}

	public void onCreate$window_CollateralFileUploadCtrl(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_CollateralFileUploadCtrl);
		this.userId = getUserWorkspace().getLoggedInUser().getUserId();
		getUserWorkspace().allocateAuthorities(super.pageRightName);

		loadConfig();

		logger.debug(Literal.LEAVING);
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
			hbox = item.get(0);
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
		if (media == null) {
			MessageUtil.showError("Please upload file.");
			return;
		}

		try {
			try {
				Thread thread = new Thread(new ProcessData(this.userId, dataEngineStatus));
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

		public ProcessData(long userId, DataEngineStatus status) {
			this.userId = userId;
			this.status = status;
		}

		@Override
		public void run() {
			try {
				commodityFileUploadResponce.collateralFileUploadProcessResponseFile(this.userId, status, file, media,
						false);
			} catch (Exception e) {
				logger.error("Exception:", e);
			}
		}
	}

	private void loadConfig() throws Exception {
		if (config == null) {
			List<ValueLabel> menuList = new ArrayList<>();
			this.config = dataEngineConfig.getConfigurationByName(COLLETARAL_VALUE_UPDATE);
			dataEngineStatus = dataEngineConfig.getLatestExecution(COLLETARAL_VALUE_UPDATE);
			ValueLabel valueLabel = new ValueLabel(COLLETARAL_VALUE_UPDATE, "Colletaral Value Update");
			menuList.add(valueLabel);
		}

		doFillPanel(config, dataEngineStatus);

	}

	public void onUpload$btnFileUpload(UploadEvent event) throws Exception {
		// Clear the file name.
		this.fileName.setText("");

		// Get the media of the selected file.
		media = event.getMedia();

		String mediaName = media.getName();

		// Get the selected configuration details.
		String prefix = config.getFilePrefixName();
		String extension = config.getFileExtension();

		DocType docType = MediaUtil.getDocType(media);

		if (!MediaUtil.isValid(media, docType)) {
			MessageUtil.showError(Labels.getLabel("upload_document_invalid", new String[] { docType.name() }));
			media = null;
			return;
		}

		if (docType.getExtension().equalsIgnoreCase(extension)) {
			MessageUtil.showError(Labels.getLabel("invalid_file_ext", new String[] { extension }));
			media = null;
			return;
		}

		if (!docType.getExtension().equalsIgnoreCase(extension)) {
			MessageUtil.showError(Labels.getLabel("invalid_file_ext", new String[] { extension }));
			media = null;
			return;
		}

		// Validate the file prefix.
		if (prefix != null && !(StringUtils.startsWith(mediaName, prefix))) {
			MessageUtil.showError(Labels.getLabel("invalid_file_prefix", new String[] { prefix }));
			media = null;
			return;
		}

		this.fileName.setText(mediaName);
	}

	public void onTimer$timer(Event event) {
		List<Row> rows = this.panelRows.getChildren();
		for (Row row : rows) {
			List<Hbox> hboxs = row.getChildren();
			for (Hbox hbox : hboxs) {
				List<ProcessExecution> list = hbox.getChildren();
				for (ProcessExecution pe : list) {
					String status = pe.getProcess().getStatus();

					if (COLLETARAL_VALUE_UPDATE.equals(pe.getProcess().getName())) {
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

	public void setCommodityFileUploadResponce(CommodityFileUploadResponce commodityFileUploadResponce) {
		this.commodityFileUploadResponce = commodityFileUploadResponce;
	}

	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}
}

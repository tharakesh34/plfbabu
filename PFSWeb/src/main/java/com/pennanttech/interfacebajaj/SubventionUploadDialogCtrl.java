package com.pennanttech.interfacebajaj;

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
import com.pennant.pff.model.subvention.SubventionHeader;
import com.pennant.pff.service.subvention.SubventionKnockOffUpload;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.excecution.ProcessExecution;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class SubventionUploadDialogCtrl extends GFCBaseCtrl<Configuration> {
	private static final Logger logger = LogManager.getLogger(SubventionUploadDialogCtrl.class);

	private static final long serialVersionUID = 1297405999029019920L;

	protected Window window_SubventionUploadDialogCtrl;
	protected Textbox fileName;
	protected Button btnFileUpload;
	protected Rows panelRows;
	protected Button btnImport;
	protected Combobox entityType;

	private Media media = null;
	private File file = null;

	protected Timer timer;

	protected DataEngineConfig dataEngineConfig;
	private long userId;
	private String userBranch;
	private DataEngineStatus SUBVENTION_REQUEST_UPLOAD = new DataEngineStatus(
			PennantConstants.SUBVENTION_REQUEST_UPLOAD);

	private SubventionKnockOffUpload subventionKnockOffUpload;
	List<ValueLabel> defaultEntityType;

	public SubventionUploadDialogCtrl() {
		super();
	}

	public void onCreate$window_SubventionUploadDialogCtrl(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_SubventionUploadDialogCtrl);
		doSetFieldProperties();
		this.userId = getUserWorkspace().getLoggedInUser().getUserId();
		this.userBranch = getUserWorkspace().getLoggedInUser().getBranchCode();
		getUserWorkspace().allocateAuthorities(super.pageRightName);

		getConfigData();

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		defaultEntityType = new ArrayList<>(1);
		List<Entity> entities = subventionKnockOffUpload.getEntites();
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
			MessageUtil.showError("Please Select Entity.");
			return;
		}
		if (media == null) {
			MessageUtil.showError("Please upload file.");
			return;
		}

		try {
			try {
				Thread thread = new Thread(
						new ProcessData(this.userId, SUBVENTION_REQUEST_UPLOAD, this.userBranch, this.entityType));
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
		private String userBranch;
		private Combobox entityType;

		public ProcessData(long userId, DataEngineStatus status, String userBranch, Combobox entityType) {
			this.userId = userId;
			this.status = status;
			this.userBranch = userBranch;
			this.entityType = entityType;
		}

		@Override
		public void run() {
			try {
				SubventionHeader subventionHead = new SubventionHeader();
				subventionHead.setUserId(this.userId);
				subventionHead.setDeStatus(status);
				subventionHead.setFile(file);
				subventionHead.setMedia(media);
				subventionHead.setUserBranch(this.userBranch);
				subventionHead.setEntityCode(getComboboxValue(this.entityType));
				subventionKnockOffUpload.importData(subventionHead);
			} catch (Exception e) {
				logger.error("Exception:", e);
			}
		}
	}

	public void getConfigData() throws Exception {
		List<ValueLabel> menuList = new ArrayList<>();
		List<Configuration> configList = dataEngineConfig.getMenuList(true);

		for (Configuration config : configList) {
			String configName = config.getName();
			if ("SUBVENTION_REQUEST_UPLOAD".equals(configName)) {
				SUBVENTION_REQUEST_UPLOAD = dataEngineConfig.getLatestExecution("SUBVENTION_REQUEST_UPLOAD");
				ValueLabel valueLabel = new ValueLabel(configName, "Subvention process upload");
				doFillPanel(config, SUBVENTION_REQUEST_UPLOAD);
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
					if ("SUBVENTION_REQUEST_UPLOAD".equals(pe.getProcess().getName())) {
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

	public void setSubventionKnockOffUpload(SubventionKnockOffUpload subventionKnockOffUpload) {
		this.subventionKnockOffUpload = subventionKnockOffUpload;
	}

}
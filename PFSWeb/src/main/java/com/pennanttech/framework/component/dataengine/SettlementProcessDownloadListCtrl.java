package com.pennanttech.framework.component.dataengine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.settlementprocess.model.SettlementProcess;
import com.pennanttech.pff.settlementprocess.webui.SettlementProcessUploadResponce;

public class SettlementProcessDownloadListCtrl extends GFCBaseListCtrl<SettlementProcess> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(SettlementProcessDownloadListCtrl.class);

	private Window window_SettlementProcessDownLoad;
	private Borderlayout borderLayout_SettlementProcessDownload;
	private Paging pagingFileDownloadList;
	private Listbox listBoxFileDownload;
	protected Button btnRefresh;
	protected Timer timer;
	private Button downlaod;

	private Media media = null;
	private File file = null;
	String module = null;
	@Autowired
	protected DataEngineConfig dataEngineConfig;

	private long userId;
	private DataEngineStatus SETTLEMENT_REQUEST_DOWNLOAD = new DataEngineStatus(
			PennantConstants.SETTLEMENT_REQUEST_DOWNLOAD);

	@Autowired(required = false)
	private SettlementProcessUploadResponce settlementProcessUploadResponce;

	/**
	 * default constructor.<br>
	 */
	public SettlementProcessDownloadListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "SettlementProcess";
		super.pageRightName = "SettlementProcess";

		this.module = getArgument("module");

		super.tableName = "SETTLEMENT_REQUEST_VIEW";
		super.queueTableName = "SETTLEMENT_REQUEST_VIEW";

	}

	public void onCreate$window_SettlementProcessDownLoad(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_SettlementProcessDownLoad, borderLayout_SettlementProcessDownload, listBoxFileDownload,
				pagingFileDownloadList);
		this.userId = getUserWorkspace().getLoggedInUser().getUserId();
		setItemRender(new FileDownloadListModelItemRenderer());

		registerField("ID");
		registerField("Hostreference");
		registerField("Transactionamount");
		registerField("Status");
		registerField("Name");
		registerField("EndTime");
		registerField("ConfigId");
		registerField("PostEvent");
		registerField("Transactiondatetime");
		registerField("Merchantname");
		registerField("FileName");
		registerField("FileLocation");
		registerField("Requestbatchid");

		doRenderPage();
		search();
		this.borderLayout_SettlementProcessDownload.setHeight(borderLayoutHeight + "px");
		logger.debug(Literal.LEAVING);
	}

	protected void doAddFilters() {
		// super.doAddFilters();
	}

	/**
	 * Call the FileDownload dialog with a new empty entry. <br>
	 */
	public void onClick$btnRefresh(Event event) {
		refresh();
	}

	private void refresh() {
		doReset();
		search();
	}

	public void onClick_Downlaod(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		try {

			Button downloadButt = (Button) event.getOrigin().getTarget();
			SettlementProcess settlementData = (SettlementProcess) downloadButt.getAttribute("object");

			try {
				try {
					DataEngineStatus status = settlementProcessUploadResponce.settlementFileDownload(this.userId,
							getUserWorkspace().getLoggedInUser().getUserName(), settlementData.getRequestBatchId());
					Configuration config = dataEngineConfig.getConfigurationByName(status.getName());
					downloadFromServer(status.getFileName(), config.getUploadPath());

				} catch (Exception e) {
					MessageUtil.showError(e);
					return;
				}
			} catch (Exception e) {
				MessageUtil.showError(e);
				return;
			}

		} catch (Exception e) {
			MessageUtil.showError(e.getMessage());
		}
		logger.debug(Literal.LEAVING);
	}

	private void downloadFromServer(String fileName, String filePath) throws FileNotFoundException, IOException {
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
		Filedownload.save(stream.toByteArray(), "application/octet-stream", fileName);
		stream.close();
	}

	/**
	 * Item renderer for listitems in the listbox.
	 * 
	 */
	private class FileDownloadListModelItemRenderer implements ListitemRenderer<SettlementProcess>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, SettlementProcess fileDownlaod, int count) {
			Listcell lc;

			lc = new Listcell(fileDownlaod.getName());
			lc.setParent(item);

			lc = new Listcell(fileDownlaod.getFileName());
			lc.setParent(item);

			lc = new Listcell(DateUtility.format(fileDownlaod.getEndTime(), PennantConstants.dateTimeFormat));
			lc.setParent(item);

			lc = new Listcell(ExecutionStatus.getStatus(fileDownlaod.getStatus()).getValue());
			lc.setParent(item);

			lc = new Listcell();
			downlaod = new Button();
			downlaod.addForward("onClick", self, "onClick_Downlaod");
			lc.appendChild(downlaod);
			downlaod.setLabel("Download");
			downlaod.setAttribute("object", fileDownlaod);

			StringBuilder builder = new StringBuilder();
			builder.append(fileDownlaod.getFileLocation());
			builder.append(File.separator);
			builder.append(fileDownlaod.getFileName());

			if (!ExecutionStatus.S.name().equals(fileDownlaod.getStatus())) {
				downlaod.setDisabled(true);
				downlaod.setTooltiptext("File generation failed.");
			}

			lc.setParent(item);
		}
	}

	public void onTimer$timer(Event event) {
		/*
		 * if (pagingFileDownloadList.getActivePage() == 0) { Events.postEvent("onCreate",
		 * this.window_SettlementProcessDownLoad, event); searchObject.clearFields(); }
		 */
	}

	public void setSettlementProcessUploadResponce(SettlementProcessUploadResponce settlementProcessUploadResponce) {
		this.settlementProcessUploadResponce = settlementProcessUploadResponce;
	}

	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}

}

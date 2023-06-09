package com.pennanttech.pff.overdraft.web;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.odsettlementprocess.model.ODSettlementProcess;
import com.pennanttech.pff.overdraft.upload.OverdraftSettlementResponseUpload;

public class OverdraftSettlementDownloadListCtrl extends GFCBaseListCtrl<ODSettlementProcess> {
	private static final long serialVersionUID = 1L;

	private Window window_overDraftSettlementDownload;
	private Borderlayout borderLayout_ODSettlementProcessDownload;
	private Paging pagingFileDownloadList;
	private Listbox listBoxFileDownload;
	protected Button btnRefresh;
	protected Timer timer;
	private Button downlaod;

	String module = null;

	private long userId;

	protected DataEngineConfig dataEngineConfig;
	private OverdraftSettlementResponseUpload overdraftSettlementResponseUpload;

	/**
	 * default constructor.<br>
	 */
	public OverdraftSettlementDownloadListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ODSettlementProcess";
		super.pageRightName = "ODSettlementProcess";

		this.module = getArgument("module");

		super.tableName = "OVERDRAFT_SETTLEMENT_REQ_VIEW";
		super.queueTableName = "OVERDRAFT_SETTLEMENT_REQ_VIEW";

	}

	public void onCreate$window_overDraftSettlementDownload(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_overDraftSettlementDownload, borderLayout_ODSettlementProcessDownload,
				listBoxFileDownload, pagingFileDownloadList);
		this.userId = getUserWorkspace().getLoggedInUser().getUserId();
		setItemRender(new FileDownloadListModelItemRenderer());

		registerField("Distinct(ID)");
		registerField("RequestBatchId");
		registerField("FileName");
		registerField("FileLocation");
		registerField("EndTime");
		registerField("ConfigId");
		registerField("Status");
		registerField("Name");

		doRenderPage();
		search();
		this.borderLayout_ODSettlementProcessDownload.setHeight(borderLayoutHeight + "px");
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

	public void onClick_Downlaod(ForwardEvent event) throws Exception {
		logger.debug(Literal.ENTERING);
		try {

			Button downloadButt = (Button) event.getOrigin().getTarget();
			ODSettlementProcess oDSettlementData = (ODSettlementProcess) downloadButt.getAttribute("object");

			try {
				try {
					DataEngineStatus status = overdraftSettlementResponseUpload.oDSettlementFileDownload(this.userId,
							getUserWorkspace().getLoggedInUser().getUserName(), oDSettlementData.getRequestBatchId());
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

	private void downloadFromServer(String fileName, String filePath) throws IOException {
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

		Filedownload.save(stream.toByteArray(), "application/octet-stream", fileName);
		stream.close();
	}

	/**
	 * Item renderer for listitems in the listbox.
	 * 
	 */
	private class FileDownloadListModelItemRenderer implements ListitemRenderer<ODSettlementProcess>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, ODSettlementProcess fileDownlaod, int count) throws Exception {
			Listcell lc;

			lc = new Listcell(fileDownlaod.getName());
			lc.setParent(item);

			lc = new Listcell(fileDownlaod.getFileName());
			lc.setParent(item);

			lc = new Listcell(DateUtil.format(fileDownlaod.getEndTime(), PennantConstants.dateTimeFormat));
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

	@Autowired
	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}

	@Autowired
	public void setOverdraftSettlementResponseUpload(
			OverdraftSettlementResponseUpload overdraftSettlementResponseUpload) {
		this.overdraftSettlementResponseUpload = overdraftSettlementResponseUpload;
	}

}

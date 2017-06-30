package com.pennanttech.framework.component.dataengine;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.constants.DataEngineConstants.ParserNames;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pff.core.Literal;

public class MasterDataExtractionCtrl extends GFCBaseCtrl<Configuration> {
	private static final Logger logger = Logger.getLogger(MasterDataExtractionCtrl.class);
	private static final long serialVersionUID = 1297405999029019920L;

	protected Window window_MasterDataExtractCtrl;
	protected Button btnDownload;

	protected Combobox masterConfiguration;
	protected DataEngineConfig dataEngineConfig;

	/**
	 * default constructor.<br>
	 */
	public MasterDataExtractionCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FileDBInterface";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_MasterDataExtractCtrl(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window);

		List<ValueLabel> menuList = new ArrayList<ValueLabel>();

		String[] parsers = new String[2];
		parsers[0] = ParserNames.READER.name();
		parsers[1] = ParserNames.DBREADER.name();
		List<Configuration> configList = dataEngineConfig.getMenuList(parsers, false);

		getUserWorkspace().allocateAuthorities(super.pageRightName);

		for (Configuration config : configList) {
			String configName = config.getName();
			if (configName.startsWith("ME_")) {
				ValueLabel valueLabel = new ValueLabel(configName, configName.substring(3));
				menuList.add(valueLabel);
			}
		}

		fillComboBox(masterConfiguration, "", menuList, "");

		logger.debug(Literal.LEAVING);
	}


	/**
	 * When user clicks on "btnImport"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnDownload(Event event) throws InterruptedException {
		if(masterConfiguration.getSelectedItem().getValue() == null) {
			MessageUtil.showError("Please Select any Master.");
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
				
			} catch (Exception e) {
				logger.error("Exception:", e);
			}
		}
	}

	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}
}

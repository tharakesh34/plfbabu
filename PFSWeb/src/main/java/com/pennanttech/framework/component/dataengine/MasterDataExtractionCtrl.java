package com.pennanttech.framework.component.dataengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class MasterDataExtractionCtrl extends GFCBaseCtrl<Configuration> {
	private static final Logger logger = LogManager.getLogger(MasterDataExtractionCtrl.class);
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
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_MasterDataExtractCtrl(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window);

		List<ValueLabel> menuList = new ArrayList<ValueLabel>();
		List<Configuration> configList = dataEngineConfig.getMenuList(false);

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
	 */
	public void onClick$btnDownload(Event event) throws Exception {
		if (masterConfiguration.getSelectedItem().getValue() == null
				|| "#".equals(masterConfiguration.getSelectedItem().getValue())) {
			MessageUtil.showError("Please select the master configuration to download.");
			return;
		}

		String config = masterConfiguration.getSelectedItem().getValue();
		DataEngineStatus status = new DataEngineStatus(config);
		DataEngineExport export = new DataEngineExport(dataEngineConfig.getDataSource(), 1000, App.DATABASE.toString(),
				true, null, status);
		Map<String, Object> filterMap = new HashMap<String, Object>();
		export.setFilterMap(filterMap);
		export.setChecksumRequired(true);
		try {
			export.exportData(config);

		} catch (Exception e) {
			throw e;
		}
		while ("I".equals(status.getStatus())) {
			Thread.sleep(100);
		}

		if ("S".equals(status.getStatus())) {
			MessageUtil.showMessage(String.format("%s master download completed successfully.", status.getName()));
		} else {
			logger.error(status.getRemarks());
			MessageUtil.showError(String.format("%s master download failed, please contact the system administrator.",
					status.getRemarks()));
		}
	}

	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}
}

package com.pennanttech.framework.component.dataengine;

import com.pennant.backend.service.cibil.CIBILService;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.bajaj.process.ALMRequestProcess;
import com.pennanttech.bajaj.process.ControlDumpRequestProcess;
import com.pennanttech.bajaj.process.DataMartRequestProcess;
import com.pennanttech.bajaj.process.PosidexRequestProcess;
import com.pennanttech.bajaj.process.TrailBalanceEngine;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.constants.DataEngineConstants.ParserNames;
import com.pennanttech.dataengine.excecution.ProcessExecution;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.baja.BajajInterfaceConstants;
import com.pennanttech.pff.reports.cibil.CIBILReport;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

public class DataExtractionCtrl extends GFCBaseCtrl<Configuration> {

	private static final long serialVersionUID = 1297405999029019920L;
	private static final Logger logger = Logger.getLogger(DataExtractionCtrl.class);

	protected Window window_DataImportCtrl;

	protected Row row1;
	protected Rows panelRows;

	protected Timer timer;

	protected DataEngineConfig dataEngineConfig;

	@Autowired
	private CIBILService cibilService;

	/**
	 * default constructor.<br>
	 */
	public DataExtractionCtrl() {
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
	public void onCreate$window_DataImportCtrl(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window);

		String[] parsers = new String[2];
		parsers[0] = ParserNames.READER.name();
		parsers[1] = ParserNames.DBREADER.name();
		List<Configuration> configList = dataEngineConfig.getMenuList();

		getUserWorkspace().allocateAuthorities(super.pageRightName);

		for (Configuration config : configList) {
			String configName = config.getName();
			if (!("ALM_REQUEST".equals(configName) || "CONTROL_DUMP_REQUEST".equals(configName)
					|| "POSIDEX_CUSTOMER_UPDATE_REQUEST".equals(configName) || "DATA_MART_REQUEST".equals(configName)
					|| "POSIDEX_CUSTOMER_UPDATE_RESPONSE".equals(configName)
					|| "GL_TRAIL_BALANCE_EXPORT".equals(configName))) {
				continue;
			}
			if ("ALM_REQUEST".equals(configName)) {
				ALMRequestProcess.EXTRACT_STATUS = dataEngineConfig.getLatestExecution("ALM_REQUEST");
				doFillPanel(config, ALMRequestProcess.EXTRACT_STATUS);
			}
			if ("CONTROL_DUMP_REQUEST".equals(configName)) {
				ControlDumpRequestProcess.EXTRACT_STATUS = dataEngineConfig.getLatestExecution("CONTROL_DUMP_REQUEST");
				doFillPanel(config, ControlDumpRequestProcess.EXTRACT_STATUS);
			}

			if ("POSIDEX_CUSTOMER_UPDATE_REQUEST".equals(configName)) {
				PosidexRequestProcess.EXTRACT_STATUS = dataEngineConfig
						.getLatestExecution("POSIDEX_CUSTOMER_UPDATE_REQUEST");
				doFillPanel(config, PosidexRequestProcess.EXTRACT_STATUS);
			}

			if ("POSIDEX_CUSTOMER_UPDATE_RESPONSE".equals(configName)) {
				BajajInterfaceConstants.POSIDEX_RESPONSE_STATUS = dataEngineConfig
						.getLatestExecution("POSIDEX_CUSTOMER_UPDATE_RESPONSE");
				doFillPanel(config, BajajInterfaceConstants.POSIDEX_RESPONSE_STATUS);
			}

			if ("DATA_MART_REQUEST".equals(configName)) {
				DataMartRequestProcess.EXTRACT_STATUS = dataEngineConfig.getLatestExecution("DATA_MART_REQUEST");
				doFillPanel(config, DataMartRequestProcess.EXTRACT_STATUS);
			}

			if ("GL_TRAIL_BALANCE_EXPORT".equals(configName)) {
				TrailBalanceEngine.EXTRACT_STATUS = dataEngineConfig.getLatestExecution("GL_TRAIL_BALANCE_EXPORT");
				doFillPanel(config, TrailBalanceEngine.EXTRACT_STATUS);
			}

		}

		CIBILReport.EXTRACT_STATUS = cibilService.getLatestExecution();
		CIBILReport.EXTRACT_STATUS.setName("CIBIL_EXPORT_STATUS");
		doFillPanel(null, CIBILReport.EXTRACT_STATUS);
		timer.start();
		logger.debug(Literal.LEAVING);
	}

	public void onTimer$timer(Event event) {
		List<Row> rows = this.panelRows.getChildren();
		for (Row row : rows) {
			List<Hbox> hboxs = row.getChildren();
			for (Hbox hbox : hboxs) {
				List<ProcessExecution> list = hbox.getChildren();
				for (ProcessExecution pe : list) {
					pe.render();
				}
			}
		}
	}

	private void doFillPanel(Configuration config, DataEngineStatus ds) {
		ProcessExecution pannel = new ProcessExecution();
		pannel.setId(ds.getName());
		pannel.setBorder("normal");
		pannel.setTitle(ds.getName());
		pannel.setWidth("420px");
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
			if (hbox.getChildren().size() == 3) {
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

	public void setDataEngineConfig(DataEngineConfig dataEngineConfig) {
		this.dataEngineConfig = dataEngineConfig;
	}
}

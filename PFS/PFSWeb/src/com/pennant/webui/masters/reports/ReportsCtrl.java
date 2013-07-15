package com.pennant.webui.masters.reports;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.West;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.FinanceMainService;

public class ReportsCtrl extends GenericForwardComposer {
	private static final long serialVersionUID = -3455808868502431564L;
	private transient FinanceMainService financeMainService;

	private Window window_Report;
	private Iframe report;

	private final Borderlayout borderlayout = (Borderlayout) Path
			.getComponent("/outerIndexWindow/borderlayoutMain");
	private West menuWest;
	private Groupbox groupboxMenu;
	private Tab tab;
	private transient DataSource pfsDataSourceObj = (DataSource) SpringUtil
			.getBean("pfsDatasource");
	private String reportName;
	private String path = "C:\\JasperReports\\";
	private Tabpanel tabpanel;

	public void onCreate$window_Report(Event event) throws Exception {
		tabpanel = (Tabpanel) window_Report.getParent();
		Tabpanels tabpanels = (Tabpanels) tabpanel.getParent();
		Tabbox tabbox = (Tabbox) tabpanels.getParent();
		tab = tabbox.getSelectedTab();
		reportName = tab.getId().substring(4);
		reportName = "ReportWithObjects";

		menuWest = borderlayout.getWest();
		groupboxMenu = (Groupbox) borderlayout.getFellowIfAny("groupbox_menu");
		menuWest.setVisible(false);
		groupboxMenu.setVisible(false);
		window_Report.setParent(groupboxMenu.getParent());

		// Generate the report
		try {
			// Get the finance main details
			FinanceMain financeMain = getFinanceMainService()
					.getFinanceMainById("EMP007", false);
			FinanceMain financeMain1 = getFinanceMainService()
					.getFinanceMainById("EMP007", false);
			financeMain1.setFinType("Hsello! Repeat!!!");

			// Generate the main report data source
			Collection<FinanceMain> financeMains = new ArrayList<FinanceMain>();
			financeMains.add(financeMain);
			financeMains.add(financeMain1);

			JRBeanCollectionDataSource financeMainDS = new JRBeanCollectionDataSource(
					financeMains);

			// Set the parameters
			Map<String, Object> parameters = new HashMap<String, Object>();

			path = path.trim() + reportName.trim() + ".jasper";
			byte[] buf = null;
			buf = JasperRunManager.runReportToPdf(path, parameters,
					financeMainDS);

			// prepare the AMedia for iframe
			final InputStream mediais = new ByteArrayInputStream(buf);
			final AMedia amedia = new AMedia("PFSReport.pdf", "pdf",
					"application/pdf", mediais);

			// set iframe content
			report.setContent(amedia);
		} catch (JRException jex) {
			throw jex;
		}
	}

	public void onClick$btnClose(Event event) {
		menuWest.setVisible(true);
		groupboxMenu.setVisible(true);
		window_Report.onClose();
		tab.close();
	}

	public FinanceMainService getFinanceMainService() {
		return financeMainService;
	}

	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}
}
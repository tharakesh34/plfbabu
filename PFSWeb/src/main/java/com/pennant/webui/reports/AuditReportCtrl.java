package com.pennant.webui.reports;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PathUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

import net.sf.jasperreports.engine.JasperRunManager;

public class AuditReportCtrl extends GFCBaseCtrl<AbstractWorkflowEntity> {

	private static final long serialVersionUID = 4678287540046204660L;
	private static final Logger logger = Logger.getLogger(AuditReportCtrl.class);

	protected Window  		window_AuditReport;
	protected Borderlayout  borderlayout;
	protected Tabbox         tabbox;
	
	protected Datebox 		fromDate;
	protected Datebox 		toDate;
	protected Combobox 		comboModuleList;

	public AuditReportCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}
	
	/**
	 * Method for Creating Window for Audit Report
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AuditReport(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_AuditReport);

		this.fromDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.toDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		fillComboBox(comboModuleList, "", PennantAppUtil.getModuleList(true), "");
		this.borderlayout.setHeight(getBorderLayoutHeight());
		
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Generating Report on Click Search button
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_Search(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		tabbox = (Tabbox)event.getTarget().getParent().getParent().getParent().getParent();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		String reportName = "";
		
		this.fromDate.setConstraint(new PTDateValidator("From Date", true,null ,DateUtility.getSysDate(),true));
		this.toDate.setConstraint(new PTDateValidator("To Date", true,null ,DateUtility.getSysDate(),true));

		try {
			if(!this.comboModuleList.isDisabled() && this.comboModuleList.getSelectedIndex() <= 0){
				throw new WrongValueException(comboModuleList, Labels.getLabel("STATIC_INVALID",
						new String[]{Labels.getLabel("label_AuditReport_ModuleName.value")}));
			}
			reportName = this.comboModuleList.getSelectedItem().getValue().toString();
			map.put("reportName", reportName);	
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (fromDate.getValue() == null ) {
				throw new WrongValueException(fromDate, Labels.getLabel("label_AuditReport_FromDate.value"));
			}
			if (toDate.getValue() == null ) {
				throw new WrongValueException(toDate, Labels.getLabel("label_AuditReport_ToDate.value"));
			}
			if (fromDate.getValue().after(toDate.getValue())) {
				throw new WrongValueException(fromDate, Labels.getLabel("label_AuditReport_FromDate.NotGreater"));
			}
			map.put("fromDate", DateUtility.getDate(DateUtility.formatUtilDate(fromDate.getValue(), PennantConstants.dateFormat)));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (toDate.getValue() == null ) {
				throw new WrongValueException(toDate, Labels.getLabel("label_AuditReport_ToDate.value"));
			}
			map.put("toDate", DateUtility.getDate(DateUtility.formatUtilDate(toDate.getValue(), PennantConstants.dateFormat)));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		wve = null;
		
		StringBuilder where= new StringBuilder(" Where AuditDateTime >= '"); 
		where.append(DateUtility.formatUtilDate(fromDate.getValue(), PennantConstants.DBDateFormat));
		where.append("' AND AuditDateTime <= '");
		where.append(DateUtility.formatUtilDate(toDate.getValue(), PennantConstants.DBDateFormat));
		where.append(" 23:59:59'");
		
		String reportSrc = PathUtil.getPath(PathUtil.REPORTS_AUDIT)+ "/" +reportName+ ".jasper";
		
		map.put("whereCondition",where);
		map.put("organizationLogo",PathUtil.getPath(PathUtil.REPORTS_IMAGE_CLIENT));
		map.put("productLogo",PathUtil.getPath(PathUtil.REPORTS_IMAGE_PRODUCT));
		map.put("userName", getUserWorkspace().getLoggedInUser().getUserName());
		
		File file = null;
		DataSource auditDatasource = null;
		try {			
			file = new File(reportSrc) ;
			if(file.exists()){
				byte[] buf = null;
				auditDatasource = (DataSource) SpringUtil.getBean("auditDatasource");
				buf = JasperRunManager.runReportToPdf(reportSrc, map, auditDatasource.getConnection());
				final HashMap<String, Object> auditMap = new HashMap<String, Object>();
				auditMap.put("reportBuffer", buf);

				// call the ZUL-file with the parameters packed in a map
				Executions.createComponents("/WEB-INF/pages/Reports/ReportView.zul", null, auditMap);

				//Setting to Default Values
				this.comboModuleList.setValue("");
				this.fromDate.setConstraint("");
				this.toDate.setConstraint("");
				this.fromDate.setText("");
				this.toDate.setText("");
			}else{
				MessageUtil.showError(Labels.getLabel("message.error.reportNotImpl"));
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showError(Labels.getLabel("message.error.reportNotFound"));
		} finally{
			map = null;
			where = null;
			file = null;
		}

		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClose$window_AuditReport(Event event) throws Exception {
		if (doClose(false)) {
			if (tabbox != null) {
				tabbox.getSelectedTab().close();
			}
		}
	}
}

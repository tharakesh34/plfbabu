package com.pennant.webui.reports;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import net.sf.jasperreports.engine.JasperRunManager;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;

public class AuditReportCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 4678287540046204660L;

	private final static Logger logger = Logger.getLogger(AuditReportCtrl.class);

	protected Datebox fromDate;
	protected Datebox toDate;
	protected Combobox comboModuleList;

	//private Window window_AuditReport;
	private List<ValueLabel> moduleList = PennantAppUtil.getModuleList();
	private transient DataSource pfsDataSourceObj = (DataSource) SpringUtil
	.getBean("auditDatasource");

	public void onCreate$window_AuditReport(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		setModuleNamesList();
		logger.debug("Leaving" + event.toString());

	}

	public void onClick$button_Search(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		final HashMap<String, Object> map = new HashMap<String, Object>();
		String reportName = "";

		try {
			if(!this.comboModuleList.isDisabled() && this.comboModuleList.getSelectedIndex()<0){
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
			if (fromDate.getValue().compareTo(DateUtility.today())>=0){
				throw new WrongValueException(fromDate, Labels.getLabel("label_AuditReport_FromDate.Lessthan"));
			}
			map.put("fromDate", DateUtility.getDate(DateUtility.formatUtilDate(fromDate.getValue(), PennantConstants.dateFormat)));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (toDate.getValue() == null ) {
				throw new WrongValueException(toDate, Labels.getLabel("label_AuditReport_ToDate.value"));
			}
			if (toDate.getValue().compareTo(DateUtility.today())>=0){
				throw new WrongValueException(toDate, Labels.getLabel("label_AuditReport_ToDate.Lessthan"));
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
		
		StringBuffer where= new StringBuffer(" Where AuditDateTime >= '"); 
		where.append(DateUtility.formatUtilDate(fromDate.getValue(), PennantConstants.DBDateFormat));

		where.append("' AND AuditDateTime <= '");
		where.append(DateUtility.formatUtilDate(toDate.getValue(), PennantConstants.DBDateFormat));
		where.append(" 23:59:59'");
		
		map.put("whereCondition",where);
		String organizationLogo =SystemParameterDetails.getSystemParameterValue("REPORTS_ORG_LOGO_PATH").toString();
		map.put("organizationLogo",organizationLogo);
		String productLogo =SystemParameterDetails.getSystemParameterValue("REPORTS_PRODUCT_LOGO_PATH").toString();
		map.put("productLogo",productLogo);
			
		String userName=getUserWorkspace().getUserDetails().getUsername();

		map.put("userName", userName);
		String reportSrc = SystemParameterDetails.getSystemParameterValue("REPORTS_AUDIT_PATH").toString()+ "/" +reportName+ ".jasper";
		if(PennantConstants.server_OperatingSystem.equals("LINUX")){
			 reportSrc = SystemParameterDetails.getSystemParameterValue("LINUX_REPORTS_AUDIT_PATH").toString()+ "/" +reportName+ ".jasper";
		}
		try {			
			File file = new File(reportSrc) ;
			if(file.exists()){
				byte[] buf = null;
				buf = JasperRunManager.runReportToPdf(reportSrc, map, pfsDataSourceObj.getConnection());
				final HashMap<String, Object> auditMap = new HashMap<String, Object>();
				auditMap.put("reportBuffer", buf);

				// call the ZUL-file with the parameters packed in a map
				Executions.createComponents("/WEB-INF/pages/Reports/reports.zul", null, auditMap);

				//Setting to Default Values
				this.comboModuleList.setValue("");
				this.fromDate.setText("");
				this.toDate.setText("");
			}else{
				PTMessageUtils.showErrorMessage("Not Yet Implemented !!!");
			}

		} catch (final Exception e) {
			PTMessageUtils.showErrorMessage("Error in Configuring the " +reportName+ " report");
		}

		logger.debug("Leaving" + event.toString());
	}
	//Setting the list of ModuleNames
	private void setModuleNamesList() {
		logger.debug("Entering ");
		for (int i = 0; i < moduleList.size(); i++) {
			Comboitem comboitem = new Comboitem();
			comboitem = new Comboitem();
			comboitem.setLabel(moduleList.get(i).getLabel());
			comboitem.setValue(moduleList.get(i).getValue());
			this.comboModuleList.appendChild(comboitem);
		}
		logger.debug("Leaving ");
	}

}

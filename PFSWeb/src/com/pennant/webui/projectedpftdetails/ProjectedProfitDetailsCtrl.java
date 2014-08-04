package com.pennant.webui.projectedpftdetails;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Filedownload;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.ProcessExecution;
import com.pennant.app.eod.accrual.AccrualProcess;
import com.pennant.app.eod.service.AmortizationService;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.reports.ReportConfiguration;
import com.pennant.backend.model.reports.ReportFilterFields;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.util.EODProperties;
import com.pennant.search.Filter;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;

public class ProjectedProfitDetailsCtrl extends GFCBaseCtrl{

	private static final long serialVersionUID = 1L;
	public final static Logger logger = Logger.getLogger(ProjectedProfitDetailsCtrl.class);

	protected Window window_ProjectedProfitDetails;
	protected Datebox   valueDate;       //autowire
	protected Button    btnExecute;      //autowire
	protected Timer timer;
	
	ProcessExecution calc;
	ProcessExecution posting;
	
	private AmortizationService amortizationService; 
	private EODProperties eodProperties;
	AccrualProcess accrualProcess = AccrualProcess.getInstance();
	private  ReportConfiguration reportConfiguration;
	private transient PagedListService pagedListService;
	private 	StringBuffer searchCriteriaDesc=new StringBuffer(" ");
	private static final String EXCEL_TYPE="Excel:";
	private boolean isExcel=false;
	
	public ProjectedProfitDetailsCtrl(){
		super();
	}
	
	int calcPercentage = 0;
	int postingPercentage = 0;
	
	
	public void onCreate$window_ProjectedProfitDetails(Event event) throws Exception {
		logger.debug("Entering : "+event);
		
		if("".equals(AccrualProcess.ACC_RUNNING)) {
			this.valueDate.setValue(DateUtility.getMonthEndDate(DateUtility.getDate(SystemParameterDetails.getSystemParameterValue("APP_DATE").toString(), PennantConstants.DBDateFormat)));
			this.valueDate.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_ProjectedProfitDetails_valueDate.value") }));
		}
		
		
		if(!"".equals(AccrualProcess.ACC_RUNNING)) {
			this.valueDate.setDisabled(true);
			this.btnExecute.setVisible(false);
			doFillExecutions(accrualProcess);
		} else {
			timer.stop();
			this.valueDate.setDisabled(false);
			this.btnExecute.setVisible(true);
			
		}
		
		if("COMPLETED".equals(AccrualProcess.ACC_RUNNING)) {
			Clients.showNotification(Labels.getLabel("labels_ProcessCompleted.value"),  "info", null, null, -1);
			AccrualProcess.ACC_RUNNING = "";
			getEodProperties().destroy();
		}
		
		if("FAILED".equals(AccrualProcess.ACC_RUNNING)) {
			doFillExecutions(accrualProcess);
			Clients.showNotification(Labels.getLabel("labels_ProcessFailed.value"),  "info", null, null, -1);
			AccrualProcess.ACC_RUNNING = "";
			timer.stop();
			this.valueDate.setDisabled(false);
			this.btnExecute.setVisible(true);
			getEodProperties().destroy();
		}
		
		logger.debug("Leaving  : "+event);
	}


	public void onClick$btnExecute(Event event){
		logger.debug("Entering : "+event);
		
		try {
			getEodProperties().init();
			
			// To Check Last Day income Adding One Day to Value Date(Because Calculation will Follow End Of the Day process )
			String valueDate = DateUtility.formatUtilDate(this.valueDate.getValue(), PennantConstants.dateFormat);
			
			accrualProcess = AccrualProcess.getInstance(getAmortizationService(), DateUtility.getDate(valueDate), getUserWorkspace().getUserDetails().getSecurityUser().getUsrBranchCode());
			this.timer.start();
			this.valueDate.setDisabled(true);
			this.btnExecute.setVisible(false);
			accrualProcess.start();
		} catch (WrongValueException we) {
			throw new WrongValueException(this.valueDate, "");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} finally {
		}
		
		this.btnExecute.setDisabled(false);

		logger.debug("Leaving  : "+event);
	}
	
	
	/**
	 * Method for Rendering Step Execution Details List
	 * @param stepExecution
	 * @throws Exception
	 */
	private void doFillExecutions(AccrualProcess accrualProcess) throws Exception {
		logger.debug("Entering");
		this.calc.setProcess(accrualProcess.getCalculation());	
		this.calc.render();
		this.posting.setProcess(accrualProcess.getPosting());	
		
		if(!"STARTING".equals(accrualProcess.getPosting().getStatus())) {
			this.posting.render();
		}
		
		logger.debug("Leaving");
	}
	
	/**
	 * When the Projected Profit Details print button is clicked.
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$button_ProjectedProfitDetails_Print(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		reportConfiguration = getReportConfiguration("ProjectedProfitDetails");
		if(reportConfiguration == null || (reportConfiguration.isPromptRequired()&& reportConfiguration.getListReportFieldsDetails().size()==0)){
			PTMessageUtils.showErrorMessage(Labels.getLabel("label_ReportNotConfigured.error"));	
		}else {
			
			if (reportConfiguration.getReportName().startsWith(EXCEL_TYPE)) {
				isExcel = true;
			} else {
				isExcel = false;
			}
			//if prompt Required Render components else direct report 
			if(!reportConfiguration.isPromptRequired()){
				doShowReport(null, null, null);
			}
		}
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * This method retries the Report Detail Configuration and Filter Components  
	 * @param   reportMenuCode
	 * @return aReportConfiguration(ReportConfiguration)
	 */
	private ReportConfiguration getReportConfiguration(String reportjaspername) throws Exception {
		ReportConfiguration aReportConfiguration =null;
		logger.debug("Entering");
		try{
			// ++ create the searchObject and initialize sorting ++//
			JdbcSearchObject<ReportConfiguration> searchObj = new JdbcSearchObject<ReportConfiguration>(ReportConfiguration.class);
			searchObj.addTabelName("REPORTCONFIGURATION");
			searchObj.addFilter(new Filter("REPORTJASPERNAME", reportjaspername, Filter.OP_EQUAL));

			List<ReportConfiguration> listReportConfiguration= getPagedListService().getBySearchObject(searchObj);

			if(listReportConfiguration.size()>0){
				aReportConfiguration =listReportConfiguration.get(0);
				if(aReportConfiguration!=null){
					this.window_ProjectedProfitDetails.setTitle(aReportConfiguration.getReportHeading());
					JdbcSearchObject<ReportFilterFields> filtersSearchObj = new JdbcSearchObject<ReportFilterFields>(ReportFilterFields.class);
					filtersSearchObj.addTabelName("REPORTFILTERFIELDS");
					filtersSearchObj.addFilter(new Filter("reportID", aReportConfiguration.getReportID(), Filter.OP_EQUAL));
					filtersSearchObj.addSort("SEQORDER", false);
					List<ReportFilterFields> listReportFilterFields= getPagedListService().getBySearchObject(filtersSearchObj);
					aReportConfiguration.setListReportFieldsDetails(listReportFilterFields);
				}
			}
		}catch (Exception e) {
			logger.error("Error while Retriving Configuration Details"+e.toString());
			throw e;
		}
		logger.debug("Leaving");
		return aReportConfiguration;
	}

	/**
	 * This method  call the report control to generate the report 
	 * @throws Exception
	 */
	public void doShowReport(String whereCond, String fromDate, String toDate) throws Exception {
		logger.debug("Entering" );

		HashMap<String, Object> reportArgumentsMap = new HashMap<String, Object>(10);
		SecurityUser securityUser = getUserWorkspace().getUserDetails().getSecurityUser();
		String usrName = (securityUser.getUsrFName().trim() +" "+securityUser.getUsrMName().trim()+" "+securityUser.getUsrLName()).trim();
		
		reportArgumentsMap.put("userName", usrName);
		reportArgumentsMap.put("reportHeading", reportConfiguration.getReportHeading());
		reportArgumentsMap.put("reportGeneratedBy", Labels.getLabel("Reports_footer_ReportGeneratedBy.lable"));
		reportArgumentsMap.put("appDate", DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_DATE").toString()));
		reportArgumentsMap.put("appCcy", (String)SystemParameterDetails.getSystemParameterValue("APP_DFT_CURR"));
		reportArgumentsMap.put("appccyEditField", Integer.parseInt((String)SystemParameterDetails.getSystemParameterValue("APP_DFT_CURR_EDIT_FIELD")));

		if(whereCond != null){
			reportArgumentsMap.put("whereCondition", whereCond);
		}
		if(fromDate != null){
			reportArgumentsMap.put("fromDate", "'"+DateUtility.getDBDate(fromDate).toString()+"'");
		}
		if(toDate != null){
			reportArgumentsMap.put("toDate", "'"+DateUtility.getDBDate(toDate).toString()+"'");
		}

		if(!reportConfiguration.isPromptRequired()){
			reportArgumentsMap.put("whereCondition", "");
		}

		if(PennantConstants.server_OperatingSystem.equals("LINUX")){			
			reportArgumentsMap.put("organizationLogo",SystemParameterDetails.getSystemParameterValue("LINUX_REPORTS_ORG_LOGO_PATH").toString());
			reportArgumentsMap.put("productLogo",SystemParameterDetails.getSystemParameterValue("LINUX_REPORTS_PRODUCT_LOGO_PATH").toString());
		}else{
			reportArgumentsMap.put("organizationLogo",SystemParameterDetails.getSystemParameterValue("REPORTS_ORG_LOGO_PATH").toString());
			reportArgumentsMap.put("productLogo",SystemParameterDetails.getSystemParameterValue("REPORTS_PRODUCT_LOGO_PATH").toString());
		}

		reportArgumentsMap.put("searchCriteria", searchCriteriaDesc.toString());
		String reportName=reportConfiguration.getReportJasperName();//This will come dynamically
		String reportSrc = "";
		if(PennantConstants.server_OperatingSystem.equals("LINUX")){		
			reportSrc = SystemParameterDetails.getSystemParameterValue("LINUX_REPORTS_ORG_PATH").toString()+"/"+ reportName+".jasper";
		}else{
			reportSrc = SystemParameterDetails.getSystemParameterValue("REPORTS_ORG_PATH").toString()+"/"+ reportName+".jasper";
		}

		byte[] buf = null;
		Connection con=null;
		DataSource reportDataSourceObj = null;

		try {			
			File file = new File(reportSrc) ;
			if(file.exists()){

				logger.debug("Buffer started" );

				reportDataSourceObj = (DataSource) SpringUtil.getBean(reportConfiguration.getDataSourceName());//This will come dynamically
				con= reportDataSourceObj.getConnection();
				
				if (!isExcel) {
					
					buf = JasperRunManager.runReportToPdf(reportSrc, reportArgumentsMap,con);
					HashMap<String, Object> auditMap = new HashMap<String, Object>(4);
					auditMap.put("reportBuffer", buf);
					auditMap.put("parentWindow", this.window_ProjectedProfitDetails);
					auditMap.put("reportName", reportConfiguration.getReportName().replace(EXCEL_TYPE, ""));

					// call the ZUL-file with the parameters packed in a map
					Executions.createComponents("/WEB-INF/pages/Reports/reports.zul", null, auditMap);
				}else{	
					
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					String printfileName = JasperFillManager.fillReportToFile(reportSrc, reportArgumentsMap, con);
					JRXlsExporter excelExporter = new JRXlsExporter();
					
					excelExporter.setParameter(JRExporterParameter.INPUT_FILE_NAME,printfileName); 
					excelExporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);  
					excelExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);  
					excelExporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);  
					excelExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE); 
					excelExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE); 
					excelExporter.setParameter(JRXlsExporterParameter.IS_IGNORE_GRAPHICS,Boolean.FALSE);  
					excelExporter.setParameter(JRXlsExporterParameter.IS_IGNORE_CELL_BORDER,Boolean.FALSE);       
					excelExporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN,Boolean.TRUE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_IMAGE_BORDER_FIX_ENABLED, Boolean.FALSE);
					excelExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
					excelExporter.exportReport();
					Filedownload.save(new AMedia(reportName, "xls", "application/vnd.ms-excel", outputStream.toByteArray()));
				}

			}else{
				PTMessageUtils.showErrorMessage(Labels.getLabel("label_Error_ReportNotImplementedYet.vlaue"));
			}

		} catch (final Exception e) {
			logger.error("Error While Preparing jasper Report"+e.toString());
			PTMessageUtils.showErrorMessage("Error in Configuring the " +reportName+ " report");
		}finally{
			if(con!=null){
				con.close();
			}
			con=null;
			reportDataSourceObj = null;
			buf = null;
		}
		logger.debug("Leaving" );
	}
	
	
	public void onTimer$timer(Event event) {
		logger.debug("Entering" + event.toString());
		Events.postEvent("onCreate", this.window_ProjectedProfitDetails, event);
		logger.debug("Leaving" + event.toString());
	}

	public void setAmortizationService(AmortizationService amortizationService) {
		this.amortizationService = amortizationService;
	}

	public AmortizationService getAmortizationService() {
		return amortizationService;
	}


	public void setEodProperties(EODProperties eodProperties) {
		this.eodProperties = eodProperties;
	}

	public EODProperties getEodProperties() {
		return eodProperties;
	}
	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
}

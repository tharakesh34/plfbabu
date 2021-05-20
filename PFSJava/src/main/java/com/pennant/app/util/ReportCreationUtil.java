package com.pennant.app.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.model.finance.BulkProcessHeader;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;

public class ReportCreationUtil {
	private static final Logger logger = LogManager.getLogger(ReportCreationUtil.class);

	private static final String RPT_NOT_FOUND = "%s report not found in %s location. Please contact the system administrator.";
	private static final String RPT_EXCEPTION = "Unable to generate the %s report. Please contact the system administrator.";

	private ReportCreationUtil() {
		//
	}

	public static void showPDF(String reportName, Object object, List<Object> listData, String userName) {
		byte[] buf = generatePDF(reportName, object, listData, userName);

		Map<String, Object> dataMap = getDataMap(reportName, listData, buf, null);
		Executions.createComponents("/WEB-INF/pages/Reports/ReportView.zul", null, dataMap);
	}

	public static void showPDF(String reportName, Object object, List<Object> listData, String userName,
			Window window) {
		byte[] buf = generatePDF(reportName, object, listData, userName);

		Map<String, Object> dataMap = getDataMap(reportName, listData, buf, window);
		Executions.createComponents("/WEB-INF/pages/Reports/ReportView.zul", null, dataMap);
	}

	public static byte[] generatePDF(String reportName, Object object, List<Object> listData, String userName) {
		logger.info(Literal.ENTERING);

		byte[] buf = null;

		String template = getTemplate(reportName);

		Map<String, Object> parameters = getParameters(userName, reportName, object);

		JRBeanCollectionDataSource mainDS = getDataSource(object);

		setDataSource(parameters, listData);

		try {
			buf = JasperRunManager.runReportToPdf(template, parameters, mainDS);
		} catch (JRException e) {
			logger.error(Literal.EXCEPTION, e);
			throw new AppException(String.format(RPT_EXCEPTION, reportName));
		}
		logger.info(Literal.LEAVING);
		return buf;
	}

	public static void downloadExcel(String reportName, Object object, List<Object> listData, String userName) {
		logger.info(Literal.ENTERING);

		String template = getTemplate(reportName);

		Map<String, Object> parameters = getParameters(userName, reportName, object);

		JRBeanCollectionDataSource mainDS = getDataSource(object);

		setDataSource(parameters, listData);

		String printfileName = null;
		try {
			printfileName = JasperFillManager.fillReportToFile(template, parameters, mainDS);
		} catch (JRException e) {
			logger.error(Literal.EXCEPTION, e);
			throw new AppException(String.format(RPT_EXCEPTION, reportName));
		}

		SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
		configuration.setDetectCellType(true);
		configuration.setWhitePageBackground(false);
		configuration.setRemoveEmptySpaceBetweenRows(true);
		configuration.setRemoveEmptySpaceBetweenColumns(true);
		configuration.setIgnoreGraphics(false);
		configuration.setIgnoreCellBorder(false);
		configuration.setCollapseRowSpan(true);
		configuration.setImageBorderFixEnabled(false);

		JRXlsExporter excelExporter = new JRXlsExporter();
		excelExporter.setExporterInput(new SimpleExporterInput(printfileName));
		excelExporter.setConfiguration(configuration);

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			excelExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
			excelExporter.exportReport();
			Filedownload.save(new AMedia(reportName, "xls", "application/vnd.ms-excel", outputStream.toByteArray()));

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new AppException(String.format(RPT_EXCEPTION, reportName));
		}

		logger.info(Literal.LEAVING);
	}

	public static String getTemplate(String reportName) {
		String path = PathUtil.getPath(PathUtil.REPORTS_FINANCE);
		return getTemplate(path, reportName);
	}

	public static String getTemplate(String reportPath, String reportName) {
		String reportSrc = reportPath + "/" + reportName + ".jasper";
		logger.info("Report Template: {}", reportSrc);

		if (!new File(reportSrc).exists()) {
			throw new AppException(String.format(RPT_NOT_FOUND, reportName, reportPath));
		}

		return reportSrc;
	}

	private static Map<String, Object> getParameters(String userName, String reportName, Object object) {
		Map<String, Object> parameters = new HashMap<>();

		parameters.put("userName", userName);
		parameters.put("organizationLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_CLIENT));
		parameters.put("client", PathUtil.getPath(PathUtil.REPORTS_IMAGE_CLIENT_DIGITAL));
		parameters.put("productLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_PRODUCT));
		parameters.put("SOAOrgLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_SOA));

		if ("FINENQ_BulkDifferemmentDetails".equals(reportName)) {
			String recalType = ((BulkProcessHeader) object).getReCalType();
			if (recalType.equals(CalculationConstants.RPYCHG_ADJMDT)
					|| recalType.equals(CalculationConstants.RPYCHG_ADDTERM)) {
				parameters.put("recalTypeSubParm", "T");
			}
		}

		return parameters;
	}

	private static JRBeanCollectionDataSource getDataSource(Object object) {
		List<Object> list = new ArrayList<Object>();
		list.add(object);
		JRBeanCollectionDataSource mainDS = new JRBeanCollectionDataSource(list);

		return mainDS;
	}

	private static void setDataSource(Map<String, Object> parameters, List<Object> listData) {
		JRBeanCollectionDataSource subListDS;

		for (int i = 0; i < listData.size(); i++) {
			Object obj = listData.get(i);
			if (obj instanceof List) {
				subListDS = new JRBeanCollectionDataSource((List<?>) obj);
			} else {
				List<Object> subList = new ArrayList<Object>();
				subList.add(obj);
				subListDS = new JRBeanCollectionDataSource(subList);
			}

			parameters.put("subDataSource" + (i + 1), subListDS);
		}

	}

	private static Map<String, Object> getDataMap(String reportName, List<Object> listData, byte[] buf, Window window) {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		String genReportName = Labels.getLabel(reportName);
		dataMap.put("reportBuffer", buf);
		dataMap.put("reportName", StringUtils.isBlank(genReportName) ? reportName : genReportName);
		dataMap.put("isModelWindow", isModelWindow(listData));
		if (window != null) {
			dataMap.put("dialogWindow", window);
		}

		return dataMap;
	}

	private static boolean isModelWindow(List<Object> listData) {
		for (int i = 0; i < listData.size(); i++) {
			Object obj = listData.get(i);
			if (obj instanceof Map) {

				@SuppressWarnings("unchecked")
				Map<Object, Object> map = (Map<Object, Object>) obj;
				if (map.containsKey("isModelWindow")) {

					return (boolean) map.get("isModelWindow");
				}

			}
		}
		return false;
	}
}

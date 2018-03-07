package com.pennant.app.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.media.AMedia;
import org.zkoss.zul.Filedownload;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.model.finance.BulkProcessHeader;
import com.pennanttech.pennapps.core.resource.Literal;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

public class ReportCreationUtil {
	private static final Logger logger = Logger.getLogger(ReportCreationUtil.class);

	public static byte[] reportGeneration(String reportName, Object object, List<Object> listData, String reportSrc,
			String userName, boolean createExcel) throws JRException {
		logger.debug("Entering");

		JRBeanCollectionDataSource subListDS;
		Map<String, Object> parameters = new HashMap<String, Object>();

		// Generate the main report data source
		List<Object> mainList = new ArrayList<Object>();
		mainList.add(object);

		JRBeanCollectionDataSource mainDS = new JRBeanCollectionDataSource(mainList);
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

		// Set the parameters
		parameters.put("userName", userName);
		parameters.put("organizationLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_CLIENT));
		parameters.put("client", PathUtil.getPath(PathUtil.REPORTS_IMAGE_CLIENT_DIGITAL));
		parameters.put("productLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_PRODUCT));

		if ("FINENQ_BulkDifferemmentDetails".equals(reportName)) {
			String recalType = ((BulkProcessHeader) object).getReCalType();
			if (recalType.equals(CalculationConstants.RPYCHG_ADJMDT)
					|| recalType.equals(CalculationConstants.RPYCHG_ADDTERM)) {
				parameters.put("recalTypeSubParm", "T");
			}
		}
		if (createExcel) {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			String printfileName = JasperFillManager.fillReportToFile(reportSrc, parameters, mainDS);
			JRXlsExporter excelExporter = new JRXlsExporter();

			excelExporter.setParameter(JRExporterParameter.INPUT_FILE_NAME, printfileName);
			excelExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
			excelExporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
			excelExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
			excelExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
			excelExporter.setParameter(JRXlsExporterParameter.IS_IGNORE_GRAPHICS, Boolean.FALSE);
			excelExporter.setParameter(JRXlsExporterParameter.IS_IGNORE_CELL_BORDER, Boolean.FALSE);
			excelExporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, Boolean.TRUE);
			excelExporter.setParameter(JRXlsExporterParameter.IS_IMAGE_BORDER_FIX_ENABLED, Boolean.FALSE);
			excelExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
			excelExporter.exportReport();
			Filedownload.save(new AMedia(reportName, "xls", "application/vnd.ms-excel", outputStream.toByteArray()));
		} else {
			try {
				GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();

				Font font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(
						new File(PathUtil.getPath(PathUtil.REPORTS_FONT) + "Montserrat-Regular.ttf")));
				graphicsEnvironment.registerFont(font);

				Font font1 = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(
						new File(PathUtil.getPath(PathUtil.REPORTS_FONT) + "Montserrat-Medium.ttf")));
				graphicsEnvironment.registerFont(font);
				graphicsEnvironment.registerFont(font1);
			}
			catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
				byte[] buf = JasperRunManager.runReportToPdf(reportSrc, parameters, mainDS);
				logger.debug("Leaving");
				return buf;
		}
		return null;

	}
}

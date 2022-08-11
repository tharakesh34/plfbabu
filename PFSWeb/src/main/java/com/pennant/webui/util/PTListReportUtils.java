/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 *
 * FileName : PTReportUtils.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 26-04-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.util;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.PathUtil;
import com.pennant.backend.model.reports.ReportList;
import com.pennant.backend.model.reports.ReportListDetail;
import com.pennant.backend.model.reports.ReportListHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.reports.ReportListService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class PTListReportUtils implements Serializable {

	private static final long serialVersionUID = 8400638894656139790L;
	private static final Logger logger = LogManager.getLogger(PTListReportUtils.class);

	private static ReportListService reportListService;

	public PTListReportUtils() {
		super();
	}

	public PTListReportUtils(String code, JdbcSearchObject<?> searchObj, int size) {
		super();

		logger.debug(Literal.ENTERING);

		ReportListHeader header = new ReportListHeader();
		JdbcSearchObject<ReportListDetail> searchObject = new JdbcSearchObject<ReportListDetail>(
				ReportListDetail.class);
		setReportListService();

		searchObject.setSorts(searchObj.getSorts());
		searchObject.setFilters(searchObj.getFilters());
		searchObject.addTabelName(searchObj.getTabelName());
		if (StringUtils.trimToNull(searchObj.getWhereClause()) != null) {
			searchObject.addWhereClause(searchObj.getWhereClause());
		}

		// Report List Details Fetching
		ReportList reportList = getReportListService().getApprovedReportListById(code);
		searchObject.setMaxResults(size + 1);

		if (reportList == null) {
			MessageUtil.showError(Labels.getLabel("message.error.reportNotFound"));
			logger.debug(Literal.LEAVING);
			return;
		}

		String[] fields = reportList.getValues();
		String[] types = reportList.getType();

		if (reportList.isFormatReq()) {
			searchObject.addField("ReportFormat");
		}

		for (int i = 0; i < fields.length; i++) {
			String field = StringUtils.trimToEmpty(fields[i]);
			String type = StringUtils.trimToEmpty(types[i]);
			searchObject.addField(field + " AS field" + type.substring(0, 1).toUpperCase() + type.substring(1).trim()
					+ StringUtils.leftPad(String.valueOf(i + 1), 2, '0'));
		}

		fields = null;
		types = null;

		header.setFiledLabel(reportList.getLabels());
		@SuppressWarnings("static-access")
		Map<String, Object> parameters = header.getReportListHeader(header);
		parameters = reportList.getMainHeaderDetails(parameters);

		// Set Report Images to parameter Fields
		parameters.put("organizationLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_CLIENT));
		parameters.put("signimage", PathUtil.getPath(PathUtil.REPORTS_IMAGE_SIGN));
		parameters.put("productLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_PRODUCT));

		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		if ("DDARep".equals(code)) {// FIXME
			pagedListService = (PagedListService) SpringUtil.getBean("extPagedListService");
		}

		List<ReportListDetail> list = pagedListService.getBySearchObject(searchObject);

		if ("SCHENQ".equals(code) || "FINENQ".equals(code)) {
			for (ReportListDetail rld : list) {
				String closeStatus = rld.getFieldString18();

				if (FinanceConstants.CLOSE_STATUS_CANCELLED.equals(closeStatus)) {
					rld.setFieldBigDecimal11(BigDecimal.ZERO);
					continue;
				}

				BigDecimal outstBigDecimal = BigDecimal.ZERO;

				BigDecimal finCurrAssetValue = rld.getFieldBigDecimal12();
				BigDecimal feeChargeAmt = rld.getFieldBigDecimal13();
				BigDecimal TotalCpz = rld.getFieldBigDecimal14();
				BigDecimal finRepaymentAmount = rld.getFieldBigDecimal15();
				BigDecimal svAmount = rld.getFieldBigDecimal16();
				BigDecimal downPayment = rld.getFieldBigDecimal17();

				outstBigDecimal = finCurrAssetValue.add(feeChargeAmt).add(TotalCpz).subtract(finRepaymentAmount)
						.subtract(svAmount);

				if (!ImplementationConstants.ALW_DOWNPAY_IN_LOANENQ_AND_SOA) {
					outstBigDecimal = outstBigDecimal.subtract(downPayment);
				}

				rld.setFieldBigDecimal11(outstBigDecimal);
			}
		}

		JRBeanCollectionDataSource listDetailsDS = new JRBeanCollectionDataSource(list);

		String reportSrc = PathUtil.getPath(PathUtil.REPORTS_LIST);
		if (code.equals(Labels.getLabel("label_CheckList.value"))) {
			reportSrc = reportSrc + "/" + "CheckListReport.jasper";
		} else {
			reportSrc = reportSrc + "/" + reportList.getReportFileName() + ".jasper";
		}

		File file = null;
		try {
			file = new File(reportSrc);
			if (file.exists()) {
				byte[] buf = null;
				buf = JasperRunManager.runReportToPdf(reportSrc, parameters, listDetailsDS);
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("reportBuffer", buf);
				map.put("reportName", reportList.getReportHeading());

				// call the ZUL-file with the parameters packed in a map
				Executions.createComponents("/WEB-INF/pages/Reports/ReportView.zul", null, map);
			} else {
				MessageUtil.showError(Labels.getLabel("message.error.reportNotImpl"));
			}
		} catch (JRException e) {
			MessageUtil.showError(e);
		} finally {

			fields = null;
			types = null;
			file = null;
			reportSrc = null;
			reportList = null;
			parameters = null;
			pagedListService = null;
			searchObject = null;
			header = null;
			listDetailsDS = null;
		}

		logger.debug(Literal.LEAVING);
	}

	public static ReportListService getReportListService() {
		return reportListService;
	}

	public void setReportListService() {
		if (reportListService == null) {
			PTListReportUtils.reportListService = (ReportListService) SpringUtil.getBean("reportListService");
		}
	}

}
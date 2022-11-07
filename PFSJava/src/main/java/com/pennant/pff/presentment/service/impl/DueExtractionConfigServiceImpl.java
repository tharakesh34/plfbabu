package com.pennant.pff.presentment.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.presentment.dao.DueExtractionConfigDAO;
import com.pennant.pff.presentment.model.DueExtractionConfig;
import com.pennant.pff.presentment.model.DueExtractionHeader;
import com.pennant.pff.presentment.model.InstrumentTypes;
import com.pennant.pff.presentment.service.DueExtractionConfigService;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.core.TableType;

public class DueExtractionConfigServiceImpl implements DueExtractionConfigService {
	private static final Logger logger = LogManager.getLogger(DueExtractionConfigServiceImpl.class);

	@Autowired
	private DueExtractionConfigDAO dueExtractionConfigDAO;

	public DueExtractionConfigServiceImpl() {
		super();
	}

	public Map<String, DueExtractionHeader> extractHeader(Date startDate, Date endDate) {
		List<DueExtractionHeader> list = new ArrayList<>();

		int noOfMonths = DateUtil.getMonthsBetweenInclusive(startDate, endDate);

		Map<String, DueExtractionHeader> map = new HashMap<>();

		Timestamp currentTime = new Timestamp(System.currentTimeMillis());

		for (int i = 0; i < noOfMonths; i++) {
			DueExtractionHeader header = new DueExtractionHeader();

			String extractionMonth = getExtractionMonth(startDate, i);

			header.setID(dueExtractionConfigDAO.getHeaderID());
			header.setExtractionMonth(extractionMonth);

			header.setCreatedBy(1000);
			header.setCreatedOn(currentTime);
			header.setApprovedBy(1000);
			header.setApprovedOn(currentTime);
			header.setLastMntBy(1000);
			header.setLastMntOn(currentTime);
			header.setVersion(1);
			header.setRoleCode("");
			header.setNextRoleCode("");
			header.setTaskId("");
			header.setNextTaskId("");
			header.setRecordType("");
			header.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			header.setWorkflowId(0);

			map.put(extractionMonth, header);
			list.add(header);
		}

		dueExtractionConfigDAO.saveHeader(list, TableType.MAIN_TAB);

		return map;
	}

	@Override
	public void extarctDueConfig() {
		logger.debug(Literal.ENTERING);

		Date appDate = SysParamUtil.getLastBusinessdate();

		Date march31st = DateUtil.getDate(DateUtil.getYear(appDate), 2, 31);

		if (DateUtil.compare(appDate, march31st) == 0) {
			dueExtractionConfigDAO.deleteConfig();
		}

		boolean configExists = dueExtractionConfigDAO.isConfigExists();

		// Date marchFirst = DateUtil.getDate(DateUtil.getYear(appDate), 2, 1);

		if (configExists) { // && DateUtil.compare(appDate, marchFirst) != 0
			return;
		}

		Date yearStratDate = getFinancialYearStart(appDate);
		Date yearEndDate = getFinancialYearEnd(appDate);

		if (!configExists) {
			int year = DateUtil.getYear(appDate);
			int month = DateUtil.getMonth(appDate);

			yearStratDate = (Date) appDate.clone();
			if (month == 2) {
				yearStratDate = DateUtil.getDate(year, month - 1, 1);
			}
		}

		int noOfDays = DateUtil.getDaysBetween(yearStratDate, yearEndDate);

		Map<String, DueExtractionHeader> extractHeader = extractHeader(yearStratDate, yearEndDate);

		List<InstrumentTypes> instruments = dueExtractionConfigDAO.getInstrumentTypes();

		List<DueExtractionConfig> presentmentExtractConfig = new ArrayList<>();

		Timestamp cuurentTime = new Timestamp(System.currentTimeMillis());

		for (InstrumentTypes it : instruments) {
			DueExtractionHeader header = extractHeader.get(DateUtil.format(yearStratDate, DateFormat.LONG_MONTH));
			for (int i = 1; i <= noOfDays; i++) {
				DueExtractionConfig pec = new DueExtractionConfig();

				pec.setInstrumentID(it.getID());
				Date dueDate = DateUtil.addDays(yearStratDate, i);

				if (DateUtil.getMonth(yearStratDate) != DateUtil.getMonth(dueDate)) {
					header = extractHeader.get(DateUtil.format(dueDate, DateFormat.LONG_MONTH));
				}

				pec.setID(dueExtractionConfigDAO.getNextValue());
				pec.setMonthID(header.getID());
				pec.setDueDate(DateUtil.getDatePart(dueDate));
				Date extractionDate = DateUtil.addDays(pec.getDueDate(), -it.getExtractionDays());
				pec.setExtractionDate(DateUtil.getDatePart(extractionDate));
				pec.setVersion(1);
				pec.setCreatedBy(it.getCreatedBy());
				pec.setCreatedOn(cuurentTime);
				pec.setApprovedBy(it.getApprovedBy());
				pec.setApprovedOn(cuurentTime);
				pec.setLastMntBy(it.getLastMntBy());
				pec.setLastMntOn(cuurentTime);
				pec.setActive(true);
				pec.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				pec.setRoleCode("");
				pec.setNextRoleCode("");
				pec.setTaskId("");
				pec.setNextTaskId("");
				pec.setRecordType("");
				pec.setWorkflowId(0);

				presentmentExtractConfig.add(pec);
			}

		}

		dueExtractionConfigDAO.save(presentmentExtractConfig, TableType.MAIN_TAB);
		logger.debug(Literal.LEAVING);
	}

	private String getExtractionMonth(Date startDate, int i) {
		Date processDate = DateUtil.addMonths(startDate, i);

		return DateUtil.format(processDate, DateFormat.LONG_MONTH);
	}

	@Override
	public List<InstrumentTypes> getInstrumentDetails() {
		List<InstrumentTypes> types = dueExtractionConfigDAO.getInstrumentHeader();

		types.stream().forEach(l1 -> l1.setMapping(dueExtractionConfigDAO.getConfig(l1.getID())));

		return types;
	}

	public List<DueExtractionHeader> getDueExtractionHeaders() {
		return dueExtractionConfigDAO.getDueExtractionHeaders();
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		DueExtractionHeader header = (DueExtractionHeader) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;

		if (header.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		} else {
			header.getConfig().stream().forEach(dec -> dec.setModified(false));
		}

		List<DueExtractionHeader> headList = new ArrayList<>();
		headList.add(header);

		if (header.isNewRecord()) {
			dueExtractionConfigDAO.saveHeader(headList, tableType);
			dueExtractionConfigDAO.save(header.getConfig(), tableType);
		} else {
			dueExtractionConfigDAO.updateHeader(headList, tableType);
			dueExtractionConfigDAO.update(header.getConfig(), tableType);
		}

		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		DueExtractionHeader header = (DueExtractionHeader) auditHeader.getAuditDetail().getModelData();

		dueExtractionConfigDAO.delete(header, TableType.TEMP_TAB);

		header.setRoleCode("");
		header.setNextRoleCode("");
		header.setTaskId("");
		header.setNextTaskId("");
		header.setWorkflowId(0);
		header.setRecordType("");

		List<DueExtractionHeader> headList = new ArrayList<>();
		headList.add(header);

		header.getConfig().stream().forEach(dec -> dec.setModified(false));

		if (header.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			dueExtractionConfigDAO.saveHeader(headList, TableType.MAIN_TAB);
			dueExtractionConfigDAO.save(header.getConfig(), TableType.MAIN_TAB);
		} else {
			dueExtractionConfigDAO.updateHeader(headList, TableType.MAIN_TAB);
			dueExtractionConfigDAO.update(header.getConfig(), TableType.MAIN_TAB);
		}

		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		DueExtractionHeader header = (DueExtractionHeader) auditHeader.getAuditDetail().getModelData();

		dueExtractionConfigDAO.delete(header, TableType.TEMP_TAB);

		return auditHeader;
	}

	@Override
	public List<DueExtractionConfig> getDueExtractionConfig(long monthID) {
		return dueExtractionConfigDAO.getDueExtractionConfig(monthID);
	}

	@Override
	public Map<Long, InstrumentTypes> getInstrumentTypesMap() {
		return dueExtractionConfigDAO.getInstrumentTypesMap();
	}

	private static Date getFinancialYearStart(Date date) {
		int year = DateUtil.getYear(date);

		int month = DateUtil.getMonth(date);

		if (month <= 3) {
			year = year - 1;
		}

		return DateUtil.getDate(year, 3, 1);
	}

	private static Date getFinancialYearEnd(Date date) {
		int year = DateUtil.getYear(date);

		int month = DateUtil.getMonth(date);

		if (month <= 3) {
			year = year - 1;
		}

		return DateUtil.getDate(year + 1, 2, 31);
	}
}

package com.pennant.pff.presentment.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

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
			header.setWorkflowId(0);

			map.put(extractionMonth, header);
			list.add(header);
		}

		dueExtractionConfigDAO.saveHeader(list, TableType.MAIN_TAB);

		return map;
	}

	@Override
	public void extarctDueConfig(Date startDate, Date endDate) {
		logger.debug(Literal.ENTERING);

		int noOfDays = DateUtil.getDaysBetween(startDate, endDate);

		Map<String, DueExtractionHeader> extractHeader = extractHeader(startDate, endDate);

		List<InstrumentTypes> instruments = dueExtractionConfigDAO.getInstrumentTypes();

		int maxExtractionDay = instruments.stream().max(Comparator.comparingInt(InstrumentTypes::getExtractionDays))
				.get().getExtractionDays();

		List<DueExtractionConfig> presentmentExtractConfig = new ArrayList<>();

		Timestamp cuurentTime = new Timestamp(System.currentTimeMillis());

		for (InstrumentTypes it : instruments) {
			DueExtractionHeader header = extractHeader.get(DateUtil.format(startDate, DateFormat.LONG_MONTH));
			for (int i = 1; i <= noOfDays; i++) {
				DueExtractionConfig pec = new DueExtractionConfig();

				pec.setInstrumentID(it.getID());
				Date dueDate = DateUtil.addDays(startDate, i);

				if (DateUtil.getMonth(startDate) != DateUtil.getMonth(dueDate)) {
					header = extractHeader.get(DateUtil.format(dueDate, DateFormat.LONG_MONTH));
				}

				pec.setID(dueExtractionConfigDAO.getNextValue());
				pec.setMonthID(header.getID());
				pec.setDueDate(dueDate);
				pec.setExtractionDate(DateUtil.addDays(pec.getDueDate(), -maxExtractionDay));
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
		List<InstrumentTypes> instrumentTypes = (List<InstrumentTypes>) auditHeader.getAuditDetail().getModelData();

		List<InstrumentTypes> saveList = new ArrayList<>();
		List<InstrumentTypes> updateList = new ArrayList<>();

		TableType tableType = TableType.MAIN_TAB;
		for (InstrumentTypes it : instrumentTypes) {
			if (instrumentTypes.get(0).isWorkflow()) {
				tableType = TableType.TEMP_TAB;
			}

			if (it.isNewRecord()) {
				saveList.add(it);
				auditHeader.getAuditDetail().setModelData(it);
			} else {
				updateList.add(it);
			}
		}

		for (InstrumentTypes it : saveList) {
			dueExtractionConfigDAO.save(it, tableType);
			dueExtractionConfigDAO.save(it.getMapping(), tableType);
		}

		return null;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DueExtractionConfig> getDueExtractionConfig(long monthID) {
		return dueExtractionConfigDAO.getDueExtractionConfig(monthID);
	}

	@Override
	public Map<Long, InstrumentTypes> getInstrumentTypesMap() {
		return dueExtractionConfigDAO.getInstrumentTypesMap();
	}
}

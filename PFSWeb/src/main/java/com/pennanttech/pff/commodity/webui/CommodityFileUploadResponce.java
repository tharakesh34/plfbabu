package com.pennanttech.pff.commodity.webui;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.zkoss.util.media.Media;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.ProcessRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.Table;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.commodity.dao.CommoditiesDAO;
import com.pennanttech.pff.commodity.dao.CommodityTypeDAO;
import com.pennanttech.pff.commodity.model.Commodity;
import com.pennanttech.pff.commodity.service.CommoditiesService;

public class CommodityFileUploadResponce extends BasicDao<Commodity> implements ProcessRecord {
	private static final Logger logger = Logger.getLogger(CommodityFileUploadResponce.class);
	public String tableName;
	NamedParameterJdbcTemplate jdbcTemplate;
	CommoditiesDAO commoditiesDAO;
	private CommoditiesService commoditiesService;
	private long userId;
	private DataSource dataSource;
	private Map<String, Long> mapCommodityTypes = new HashMap<String, Long>();
	private CommodityTypeDAO commodityTypeDAO;

	public CommodityFileUploadResponce() {
		super();
	}

	public void collateralFileUploadProcessResponseFile(Object... params) throws Exception {
		userId = (Long) params[0];
		DataEngineStatus status = (DataEngineStatus) params[1];
		File file = (File) params[2];
		Media media = (Media) params[3];
		String configName = status.getName();
		String name = "";
		mapCommodityTypes = commodityTypeDAO.getCommodityTypeData();
		if (file != null) {
			name = file.getName();
		} else if (media != null) {
			name = media.getName();
		}

		status.reset();
		status.setFileName(name);
		status.setRemarks("initiated Collateral upload  file [ " + name + " ] processing..");

		DataEngineImport dataEngine = new DataEngineImport(dataSource, userId, App.DATABASE.name(), true,
				DateUtility.getAppDate(), status);
		dataEngine.setFile(file);
		dataEngine.setMedia(media);
		dataEngine.setValueDate(DateUtility.getAppDate());
		Map<String, Object> filterMap = new HashMap<>();
		dataEngine.setFilterMap(filterMap);
		dataEngine.setProcessRecord(this);
		dataEngine.importData(configName);

		do {
			if ("S".equals(status.getStatus()) || "F".equals(status.getStatus())) {
				break;
			}
		} while ("S".equals(status.getStatus()) || "F".equals(status.getStatus()));

	}

	@Override
	public void saveOrUpdate(DataEngineAttributes attributes, MapSqlParameterSource record, Table table) {
		try {
		Commodity commodity = new Commodity();

		commodity.setCode((String) record.getValue("Code"));
		commodity.setHSNCode((String) record.getValue("HSNCode"));
		commodity.setDescription((String) record.getValue("Description"));
		commodity.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		commodity.setLastMntBy(userId);
		commodity.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		Object objCurrentValue = record.getValue("CurrentValue");
		String strCurrentValue = null;
		BigDecimal currentValue = BigDecimal.ZERO;

		if (objCurrentValue != null) {
			strCurrentValue = objCurrentValue.toString();
		}

		if (StringUtils.isNumeric(strCurrentValue)) {
			currentValue = new BigDecimal(strCurrentValue);
		}

		commodity.setCurrentValue(currentValue);
		commodity.setUpload(true);
		String commodityType = record.getValue("CommodityTypeCode") == null ? ""
				: record.getValue("CommodityTypeCode").toString();
		if (mapCommodityTypes.get(commodityType) != null) {
			commodity.setCommodityType(mapCommodityTypes.get(commodityType));
		} else {
			throw new ConcurrencyException();
		}

		Commodity Oldcommodity = commoditiesDAO.getQueryOperation(commodity);

		if (Oldcommodity == null) {
			commodity.setVersion(1);
			commodity.setNewRecord(true);
			commodity.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		} else {
			commodity.setId(Oldcommodity.getId());
			commodity.setVersion(Oldcommodity.getVersion() + 1);
			commodity.setNewRecord(false);
			commodity.setRecordType(PennantConstants.TRAN_UPD);
		}

		AuditHeader auditHeader = getAuditHeader(commodity, PennantConstants.TRAN_WF);
		commoditiesService.doApprove(auditHeader);

		MapSqlParameterSource beforeMapdata = new MapSqlParameterSource();
		MapSqlParameterSource afterMapdata = new MapSqlParameterSource();

		beforeMapdata.addValue("CommodityId", Oldcommodity == null ? commodity.getId() : Oldcommodity.getId());
		beforeMapdata.addValue("AuditImage", PennantConstants.TRAN_BEF_IMG);
		beforeMapdata.addValue("CurrentValue", Oldcommodity == null ? 0 : Oldcommodity.getCurrentValue());
		beforeMapdata.addValue("BatchId", attributes.getStatus().getId());
		beforeMapdata.addValue("ModifiedBy", userId);
		beforeMapdata.addValue("ModifiedOn", new Timestamp(System.currentTimeMillis()));
		if (Oldcommodity == null) {
			commoditiesDAO.saveCommoditiesLog(beforeMapdata);
		} else {
			if (Oldcommodity.getCurrentValue().compareTo(commodity.getCurrentValue()) != 0) {
				commoditiesDAO.saveCommoditiesLog(beforeMapdata);
			}
		}

		afterMapdata = beforeMapdata;
		afterMapdata.addValue("AuditImage", PennantConstants.TRAN_AFT_IMG);
		afterMapdata.addValue("CurrentValue", commodity.getCurrentValue());
		commoditiesDAO.saveCommoditiesLog(afterMapdata);
		} catch(Exception e)
		{
			logger.debug(Literal.EXCEPTION, e);
			updateLog(attributes.getStatus().getId(), "0", "F", e.getMessage());
			
		}
		
	}

	private void updateLog(long id, String keyId, String status, String reason) {

		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		StringBuilder query = null;
		MapSqlParameterSource source = null;

		query = new StringBuilder("Update DATA_ENGINE_LOG");
		query.append(" Set Status = :Status, Reason =:Reason Where Id = :Id and KeyId = :KeyId");

		source = new MapSqlParameterSource();
		source.addValue("Id", id);
		source.addValue("KeyId", keyId);
		source.addValue("Status", status);
		source.addValue("Reason", reason = reason.length() > 2000 ? reason.substring(0, 1995) : reason);

		int count = this.jdbcTemplate.update(query.toString(), source);

		if (count == 0) {
			query = new StringBuilder();
			query.append(" INSERT INTO DATA_ENGINE_LOG");
			query.append(" (Id, KeyId, Status, Reason)");
			query.append(" VALUES(:Id, :KeyId, :Status, :Reason)");
			this.jdbcTemplate.update(query.toString(), source);
		}
		query = null;
		source = null;
	}
	
	/**
	 * Get Audit Header Details
	 * 
	 * @param aCommodities
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(Commodity aCommodities, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCommodities.getBefImage(), aCommodities);
		return new AuditHeader(String.valueOf(aCommodities.getId()), String.valueOf(aCommodities.getId()), null, null,
				auditDetail, aCommodities.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
		this.dataSource = dataSource;
	}

	public void setCommoditiesDAO(CommoditiesDAO commoditiesDAO) {
		this.commoditiesDAO = commoditiesDAO;
	}

	public void setCommoditiesService(CommoditiesService commoditiesService) {
		this.commoditiesService = commoditiesService;
	}

	public void setCommodityTypeDAO(CommodityTypeDAO commodityTypeDAO) {
		this.commodityTypeDAO = commodityTypeDAO;
	}
}

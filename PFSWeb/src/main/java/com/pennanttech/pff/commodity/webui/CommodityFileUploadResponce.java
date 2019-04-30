package com.pennanttech.pff.commodity.webui;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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
import com.pennanttech.pff.commodity.dao.CommoditiesDAO;
import com.pennanttech.pff.commodity.dao.CommodityTypeDAO;
import com.pennanttech.pff.commodity.model.Commodity;
import com.pennanttech.pff.commodity.service.CommoditiesService;

public class CommodityFileUploadResponce extends BasicDao<Commodity> implements ProcessRecord {
	CommoditiesDAO commoditiesDAO;
	private CommoditiesService commoditiesService;
	private long userId;
	private DataSource dataSource;
	private CommodityTypeDAO commodityTypeDAO;

	public CommodityFileUploadResponce() {
		super();
	}

	public void collateralFileUploadProcessResponseFile(Object... params) throws Exception {
		long userId = (Long) params[0];
		DataEngineStatus status = (DataEngineStatus) params[1];
		File file = (File) params[2];
		Media media = (Media) params[3];
		String configName = status.getName();
		String name = "";

		Map<String, Long> mapCommodityTypes = commodityTypeDAO.getCommodityTypeData();

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
		Map<String, Object> parametersMap = new HashMap<>();
		parametersMap.put("CommodityType", mapCommodityTypes);
		dataEngine.setParameterMap(parametersMap);
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

			@SuppressWarnings("unchecked")
			Map<String, Long> mapCommodityTypes = (Map<String, Long>) attributes.getParameterMap().get("CommodityType");

			Commodity commodity = new Commodity();

			commodity.setHSNCode((String) record.getValue("HSNCode"));
			Object objCurrentValue = record.getValue("CurrentValue");
			String commodityType = record.getValue("CommodityTypeCode") == null ? ""
					: record.getValue("CommodityTypeCode").toString();
			commodity.setCode((String) record.getValue("Code"));
			commodity.setDescription((String) record.getValue("Description"));
			commodity.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			commodity.setLastMntBy(userId);
			commodity.setLastMntOn(new Timestamp(System.currentTimeMillis()));

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

			if (mapCommodityTypes.get(commodityType) != null && !commodityType.equals("")) {
				commodity.setCommodityType(mapCommodityTypes.get(commodityType));
			}

			Commodity Oldcommodity = commoditiesDAO.getQueryOperation(commodity);

			if (Oldcommodity == null) {
				if ((mapCommodityTypes.get(commodityType) != null && (commodity.getCommodityType() != Long.MIN_VALUE)
						&& commodity.getCommodityType() > 0) && !commodity.getCode().equals("")) {
					commodity.setVersion(1);
					commodity.setNewRecord(true);
					commodity.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					throw new ConcurrencyException();
				}
			} else {
				commodity.setId(Oldcommodity.getId());
				commodity.setVersion(Oldcommodity.getVersion() + 1);
				commodity.setNewRecord(false);
				commodity.setRecordType(PennantConstants.TRAN_UPD);
				if (commodity.getCode() == null || commodity.getCode().equals("")) {
					commodity.setCode(Oldcommodity.getCode());
				}
				if ((commodity.getCommodityType() == Long.MIN_VALUE) || commodity.getCommodityType() == 0) {
					commodity.setCommodityType(Oldcommodity.getCommodityType());
				}
				commodity.setActive(Oldcommodity.isActive());
				commodity.setDescription(Oldcommodity.getDescription());
				commodity.setAlertsRequired(Oldcommodity.isAlertsRequired());
				commodity.setAlertToRoles(Oldcommodity.getAlertToRoles());
				commodity.setAlertToRolesName(Oldcommodity.getAlertToRolesName());
				commodity.setAlertType(Oldcommodity.getAlertType());
				commodity.setAlertTypeName(Oldcommodity.getAlertTypeName());
				commodity.setCustomerTemplate(Oldcommodity.getCustomerTemplate());
				commodity.setCustomerTemplateCode(Oldcommodity.getCustomerTemplateCode());
				commodity.setCustomerTemplateName(Oldcommodity.getCustomerTemplateName());
				commodity.setUserTemplate(Oldcommodity.getUserTemplate());
				commodity.setUserTemplateCode(Oldcommodity.getUserTemplateCode());
				commodity.setUserTemplateName(Oldcommodity.getUserTemplateName());
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
		} catch (Exception e) {
			throw e;

		}

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

	@Autowired
	public void setCommoditiesDAO(CommoditiesDAO commoditiesDAO) {
		this.commoditiesDAO = commoditiesDAO;
	}

	@Autowired
	public void setCommoditiesService(CommoditiesService commoditiesService) {
		this.commoditiesService = commoditiesService;
	}

	@Autowired
	public void setCommodityTypeDAO(CommodityTypeDAO commodityTypeDAO) {
		this.commodityTypeDAO = commodityTypeDAO;
	}
}

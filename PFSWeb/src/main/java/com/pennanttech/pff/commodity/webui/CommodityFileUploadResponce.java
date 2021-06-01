package com.pennanttech.pff.commodity.webui;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
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
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.commodity.dao.CommoditiesDAO;
import com.pennanttech.pff.commodity.dao.CommodityTypeDAO;
import com.pennanttech.pff.commodity.model.Commodity;
import com.pennanttech.pff.commodity.service.CommoditiesService;

public class CommodityFileUploadResponce extends BasicDao<Commodity> implements ProcessRecord {
	CommoditiesDAO commoditiesDAO;
	private CommoditiesService commoditiesService;
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
			DataEngineStatus status = attributes.getStatus();

			@SuppressWarnings("unchecked")
			Map<String, Long> mapCommodityTypes = (Map<String, Long>) attributes.getParameterMap().get("CommodityType");

			String commodityType = "";
			Commodity commodity = new Commodity();

			commodity.setHSNCode((String) record.getValue("HSNCode"));
			Object objCurrentValue = record.getValue("CurrentValue");

			if (record.hasValue("CommodityTypeCode")) {
				commodityType = record.getValue("CommodityTypeCode") == null ? ""
						: record.getValue("CommodityTypeCode").toString();
			}

			if (record.hasValue("Code")) {
				commodity.setCode((String) record.getValue("Code"));
			}

			if (record.hasValue("Description")) {
				commodity.setDescription((String) record.getValue("Description"));
			}

			commodity.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			commodity.setLastMntBy(status.getUserId());
			commodity.setLastMntOn(new Timestamp(System.currentTimeMillis()));

			String strCurrentValue = null;
			BigDecimal currentValue = BigDecimal.ZERO;

			if (objCurrentValue != null) {
				strCurrentValue = objCurrentValue.toString();
			}

			//FIXME need Consider Currency
			if (StringUtils.isNotBlank(strCurrentValue)) {
				currentValue = (new BigDecimal(strCurrentValue)).multiply(new BigDecimal(100));
			}

			commodity.setCurrentValue(currentValue);
			commodity.setUpload(true);

			commodityType = StringUtils.upperCase(commodityType);
			if (mapCommodityTypes.get(commodityType) == null) {
				throw new AppException(
						"The Commodity Type Code " + commodityType + " not exists in Commodity Types master.");
			} else {
				commodity.setCommodityType(mapCommodityTypes.get(commodityType));
			}

			Commodity oldcommodity = commoditiesDAO.getCommodity(commodity);

			if (oldcommodity == null) {
				commodity.setVersion(1);
				commodity.setNewRecord(true);
				commodity.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				AuditHeader auditHeader = getAuditHeader(commodity, PennantConstants.TRAN_WF);
				commoditiesService.doApprove(auditHeader);

				if (auditHeader.getErrorMessage() != null) {
					throw new AppException(auditHeader.getErrorMessage().get(0).getError());
				}

			} else {
				commodity.setId(oldcommodity.getId());
				commodity.setVersion(oldcommodity.getVersion() + 1);
				commoditiesDAO.updateCommodity(commodity);
			}

			if (oldcommodity != null && (oldcommodity.getCurrentValue().compareTo(commodity.getCurrentValue()) != 0)) {
				MapSqlParameterSource parameterSource = new MapSqlParameterSource();
				parameterSource.addValue("CommodityId", oldcommodity.getId());
				parameterSource.addValue("AuditImage", PennantConstants.TRAN_BEF_IMG);
				parameterSource.addValue("CurrentValue", oldcommodity.getCurrentValue());
				parameterSource.addValue("BatchId", status.getId());
				parameterSource.addValue("ModifiedBy", status.getUserId());
				parameterSource.addValue("ModifiedOn", new Timestamp(System.currentTimeMillis()));
				commoditiesDAO.saveCommoditiesLog(parameterSource);

				parameterSource.addValue("AuditImage", PennantConstants.TRAN_AFT_IMG);
				parameterSource.addValue("CurrentValue", commodity.getCurrentValue());
				commoditiesDAO.saveCommoditiesLog(parameterSource);

			}
		} catch (Exception e) {
			throw new AppException(e.getMessage());

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
				auditDetail, aCommodities.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
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

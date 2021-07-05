package com.pennant.pff.service.subvention;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.zkoss.util.media.Media;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.EntityDAO;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.pff.dao.subvention.SubventionUploadDAO;
import com.pennant.pff.model.subvention.Subvention;
import com.pennant.pff.model.subvention.SubventionHeader;
import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.ProcessRecord;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.Table;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

public class SubventionKnockOffUpload extends BasicDao<Subvention> implements ProcessRecord {

	private DataSource dataSource;
	private SubventionKnockOffService subventionKnockOffService;
	private SubventionUploadDAO subventionUploadDAO;
	private EntityDAO entityDAO;

	public SubventionKnockOffUpload() {
		super();
	}

	public void importData(SubventionHeader subventionHead) throws Exception {
		long userId = subventionHead.getUserId();
		DataEngineStatus des = subventionHead.getDeStatus();
		File file = subventionHead.getFile();
		Media media = subventionHead.getMedia();
		String configName = des.getName();
		String name = "";
		StringBuilder remarks = new StringBuilder();
		Map<String, Object> parametersMap = new HashMap<>();
		String entityCode = subventionHead.getEntityCode();

		if (file != null) {
			name = file.getName();
		} else if (media != null) {
			name = media.getName();
		}

		des.reset();
		des.setFileName(name);
		boolean exists = subventionKnockOffService.isFileExists(name);
		long id;
		if (!exists) {
			id = subventionKnockOffService.saveHeader(name, entityCode);
			parametersMap.put("BATCHID", id);
			subventionHead.setId(id);
		} else {
			remarks.append("Selected file already processed");
			des.setStatus(ExecutionStatus.F.name());
			des.setEndTime(new Timestamp(System.currentTimeMillis()));
			des.setRemarks(remarks.toString());
			des.setName(configName);
			throw new Exception("Selected file already processed");
		}
		des.setRemarks("initiated Subvention upload  file [ " + name + " ] processing..");
		Date appDate = SysParamUtil.getAppDate();
		DataEngineImport dataEngine = new DataEngineImport(dataSource, userId, App.DATABASE.name(), true, appDate, des);
		dataEngine.setFile(file);
		dataEngine.setMedia(media);
		dataEngine.setParameterMap(parametersMap);
		dataEngine.setValueDate(appDate);
		dataEngine.setProcessRecord(this);
		dataEngine.importData(configName);

		do {
			if ("S".equals(des.getStatus()) || "F".equals(des.getStatus())) {
				List<Subvention> subventions = subventionUploadDAO.getSubventionDetails(id);
				subventionHead.setSubventions(subventions);
				subventionKnockOffService.process(subventionHead);
				break;
			}
		} while ("S".equals(des.getStatus()) || "F".equals(des.getStatus()));

	}

	public List<Entity> getEntites() {
		return entityDAO.getEntites();
	}

	@Override
	public void saveOrUpdate(DataEngineAttributes attributes, MapSqlParameterSource record, Table table) {
		Subvention subvention = new Subvention();

		subvention.setBatchId((Long) record.getValue("BATCHID"));
		subvention.setFinReference((String) record.getValue("FINREFERENCE"));
		subvention.setFinType((String) record.getValue("FINTYPE"));
		subvention.setReferenceCode((String) record.getValue("REFERENCECODE"));
		subvention.setCustomerName((String) record.getValue("CUSTOMERNAME"));
		subvention.setPostDate((Date) record.getValue("POSTDATE"));
		subvention.setValueDate((Date) record.getValue("VALUEDATE"));
		subvention.setTransref((String) record.getValue("TRANSREF"));
		subvention.setPartnerBankId((Long) record.getValue("PARTNERBANKID"));
		subvention.setPartnerAccNo((String) record.getValue("PARTNERACCNO"));
		BigDecimal subAmt = new BigDecimal((Integer) record.getValue("AMOUNT"));
		BigDecimal amount = subAmt.multiply(new BigDecimal(100));
		subvention.setAmount(amount);

		List<Subvention> subventions = new ArrayList<>();
		subventions.add(subvention);

		subventionUploadDAO.saveSubvention(subventions, subvention.getBatchId());
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
		this.dataSource = dataSource;
	}

	public void setSubventionKnockOffService(SubventionKnockOffService subventionKnockOffService) {
		this.subventionKnockOffService = subventionKnockOffService;
	}

	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	public void setSubventionUploadDAO(SubventionUploadDAO subventionUploadDAO) {
		this.subventionUploadDAO = subventionUploadDAO;
	}

}

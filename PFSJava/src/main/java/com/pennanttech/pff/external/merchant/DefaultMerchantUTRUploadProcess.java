package com.pennanttech.pff.external.merchant;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.zkoss.util.media.Media;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineLog;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.AbstractInterface;
import com.pennanttech.pff.model.merchant.MerchantUTRUpload;
import com.pennanttech.pff.service.extended.fields.ExtendedFieldService;

public class DefaultMerchantUTRUploadProcess extends AbstractInterface implements MerchantUTRProcess {
	protected final Logger logger = Logger.getLogger(getClass());
	private DataEngineImport dataEngine;
	String localLocation = "";
	String job = "";

	Channel channel = null;
	ChannelSftp channelSftp = null;
	Session session = null;

	@Autowired(required = false)
	@Qualifier("merchantUTRUploadValidationImpl")
	private ValidateRecord merchantUTRUploadValidationImpl;
	@Autowired(required = false)
	private ExtendedFieldService extendedFieldServiceHook;
	@Autowired
	private FinanceMainDAO financeMainDAO;

	public DefaultMerchantUTRUploadProcess() {
		super();
	}

	@Override
	public void processMerchantUTRUpload(long userId, File file, Media media, DataEngineStatus status)
			throws Exception {
		logger.debug(Literal.ENTERING);
		String configName = status.getName();
		String name = "";
		if (file != null) {
			name = file.getName();
		} else if (media != null) {
			name = media.getName();
		}
		status.reset();
		status.setFileName(name);
		status.setRemarks("initiated Merchant UTR Upload file [ " + name + " ] processing..");
		dataEngine = new DataEngineImport(dataSource, userId, App.DATABASE.name(), true, SysParamUtil.getAppValueDate(),
				status);
		dataEngine.setFile(file);
		dataEngine.setMedia(media);
		dataEngine.setValueDate(SysParamUtil.getAppValueDate());
		dataEngine.setValidateRecord(merchantUTRUploadValidationImpl);
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("APP_DATE", SysParamUtil.getAppDate());
		dataEngine.setParameterMap(parameterMap);
		dataEngine.importData(configName);

		do {
			if ("S".equals(status.getStatus()) || "F".equals(status.getStatus())) {
				processResponse(status.getId(), status);
				break;
			}
		} while ("S".equals(status.getStatus()) || "F".equals(status.getStatus()));

		logger.debug(Literal.LEAVING);
	}

	private void processResponse(long respBatchId, DataEngineStatus status) {
		logger.debug(Literal.ENTERING);
		setExceptionLog(status);
		List<MerchantUTRUpload> merchantUTRUploadList = getMerchantUTRUploadList(respBatchId);
		List<String> lanRefs = new ArrayList<>();

		if (merchantUTRUploadList == null || merchantUTRUploadList.isEmpty()) {
			return;
		}
		// Updating the Merchant UTR Number and TransferDate
		updateMerchantUTRNumber(merchantUTRUploadList);
		for (int i = 0; i < merchantUTRUploadList.size(); i++) {
			lanRefs.add(merchantUTRUploadList.get(i).getLanReference());
		}
		updateResponseStatus(lanRefs, respBatchId);
		logger.debug(Literal.LEAVING);
	}

	private List<MerchantUTRUpload> getMerchantUTRUploadList(long respBatchId) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT LanReference, UTRNumber, TransferDate, Status, Remarks");
		sql.append(" FROM Merchant_UTR_Upload");
		sql.append(" WHERE RESP_BATCH_ID = ?");
		try {
			return this.jdbcTemplate.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, respBatchId);
				}
			}, new RowMapper<MerchantUTRUpload>() {
				@Override
				public MerchantUTRUpload mapRow(ResultSet rs, int rowNum) throws SQLException {
					MerchantUTRUpload merchantUTRUpload = new MerchantUTRUpload();
					merchantUTRUpload.setLanReference(rs.getString("LanReference"));
					merchantUTRUpload.setUtrNumber(rs.getString("UTRNumber"));
					merchantUTRUpload.setTransferDate(rs.getDate("TransferDate"));
					merchantUTRUpload.setStatus(rs.getString("Status"));
					merchantUTRUpload.setRemarks(rs.getString("Remarks"));
					return merchantUTRUpload;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return new ArrayList<>();
	}

	private void updateMerchantUTRNumber(List<MerchantUTRUpload> merchantUTRUploads) {
		for (MerchantUTRUpload merchantUTRUpload : merchantUTRUploads) {
			FinanceMain financeMain = financeMainDAO.getFinCategoryByFinRef(merchantUTRUpload.getLanReference());
			if (financeMain != null) {
				String tableName = ExtendedFieldConstants.MODULE_LOAN + ("_") + financeMain.getFinCategory() + ("_")
						+ (ExtendedFieldConstants.MODULE_MAINTENANCE) + ("_ED");
				extendedFieldServiceHook.processExtendedFields(tableName, merchantUTRUpload);
			}
		}
	}

	private void updateResponseStatus(List<String> lanRefList, long batchId) {
		logger.debug(Literal.ENTERING);
		try {
			this.namedJdbcTemplate.getJdbcOperations().batchUpdate(
					"update Merchant_UTR_Upload set status = ?, remarks = ? where LanReference=? and RESP_BATCH_ID=?",
					new BatchPreparedStatementSetter() {
						public void setValues(PreparedStatement ps, int i) throws SQLException {
							ps.setString(1, "SUCCESS");
							ps.setString(2, "Uploaded Successfully");
							ps.setString(3, lanRefList.get(i));
							ps.setLong(4, batchId);
						}

						public int getBatchSize() {
							return lanRefList.size();
						}
					});
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		logger.debug(Literal.LEAVING);
	}

	// Setting the exception log data engine status.
	private void setExceptionLog(DataEngineStatus status) {
		List<DataEngineLog> engineLogs = getExceptions(status.getId());
		if (CollectionUtils.isNotEmpty(engineLogs)) {
			status.setDataEngineLogList(engineLogs);
		}
	}

	// Getting the exception log
	public List<DataEngineLog> getExceptions(long batchId) {
		RowMapper<DataEngineLog> rowMapper = null;
		MapSqlParameterSource parameterMap = null;
		StringBuilder sql = null;

		try {
			sql = new StringBuilder("Select * from DATA_ENGINE_LOG where StatusId = :ID");
			parameterMap = new MapSqlParameterSource();
			parameterMap.addValue("ID", batchId);
			rowMapper = BeanPropertyRowMapper.newInstance(DataEngineLog.class);
			return namedJdbcTemplate.query(sql.toString(), parameterMap, rowMapper);
		} catch (Exception e) {
		} finally {
			rowMapper = null;
			sql = null;
		}
		return null;
	}

	public ValidateRecord getMerchantUTRUploadValidationImpl() {
		return merchantUTRUploadValidationImpl;
	}

	public void setMerchantUTRUploadValidationImpl(ValidateRecord merchanUTRUploadValidationImpl) {
		this.merchantUTRUploadValidationImpl = merchanUTRUploadValidationImpl;
	}

	public ExtendedFieldService getExtendedFieldServiceHook() {
		return extendedFieldServiceHook;
	}

	public void setExtendedFieldServiceHook(ExtendedFieldService extendedFieldServiceHook) {
		this.extendedFieldServiceHook = extendedFieldServiceHook;
	}

}

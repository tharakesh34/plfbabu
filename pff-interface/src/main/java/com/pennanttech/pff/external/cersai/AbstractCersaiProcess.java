package com.pennanttech.pff.external.cersai;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.zkoss.util.media.Media;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.cersai.Cersai;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.AbstractInterface;
import com.pennanttech.pff.external.CersaiProcess;

public class AbstractCersaiProcess extends AbstractInterface implements CersaiProcess {
	protected final Logger logger = LogManager.getLogger(getClass());

	public AbstractCersaiProcess() {
		super();
	}

	@Override
	public void processResponseFile(long userId, File file, Media media) throws Exception {
		logger.debug(Literal.ENTERING);

		String configName = CERSAI_IMPORT.getName();
		String name = "";

		if (file != null) {
			name = file.getName();
		} else if (media != null) {
			name = media.getName();
		}

		CERSAI_IMPORT.reset();
		CERSAI_IMPORT.setFileName(name);
		CERSAI_IMPORT.setRemarks("initiated cersai response file [ " + name + " ] processing..");

		DataEngineImport dataEngine;
		dataEngine = new DataEngineImport(dataSource, userId, App.DATABASE.name(), true, SysParamUtil.getAppDate(),
				CERSAI_IMPORT);
		dataEngine.setFile(file);
		dataEngine.setMedia(media);
		dataEngine.setValueDate(SysParamUtil.getAppDate());
		dataEngine.importData(configName);

		do {
			if ("S".equals(CERSAI_IMPORT.getStatus()) || "F".equals(CERSAI_IMPORT.getStatus())) {
				receiveResponse(CERSAI_IMPORT.getId());
				break;
			}
		} while ("S".equals(CERSAI_IMPORT.getStatus()) || "F".equals(CERSAI_IMPORT.getStatus()));

		logger.debug(Literal.LEAVING);
	}

	private void receiveResponse(long respBatchId) {

		MapSqlParameterSource paramMap = null;
		StringBuilder sql = null;
		List<Cersai> cersaiList = null;
		RowMapper<Cersai> rowMapper = null;

		sql = new StringBuilder();
		sql.append(" SELECT  CERSAI_ASSET_ID, LOAN_REFERENCE_NUMBER, COLLATERAL_REFERENCE_NUMBER ");
		sql.append(" FROM CERSAI_RESPONSE ");
		sql.append(" WHERE RESP_BATCH_ID = :RESP_BATCH_ID");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("RESP_BATCH_ID", respBatchId);

		rowMapper = BeanPropertyRowMapper.newInstance(Cersai.class);
		cersaiList = namedJdbcTemplate.query(sql.toString(), paramMap, rowMapper);

		if (cersaiList == null || cersaiList.isEmpty()) {
			return;
		}

		try {
			for (Cersai respCersai : cersaiList) {

				CollateralAssignment assignment = getCollateralAssignment(respCersai.getLOAN_REFERENCE_NUMBER(),
						respCersai.getCOLLATERAL_REFERENCE_NUMBER());

				boolean isSuccess = true;
				if (assignment == null) {
					respCersai.setREMARKS("Assignment not exist.");
					respCersai.setSTATUS("F");
					isSuccess = false;
				} else if (StringUtils.isNotBlank(assignment.getHostReference())) {
					respCersai.setREMARKS("CERSAI ID already exists.");
					respCersai.setSTATUS("F");
					isSuccess = false;
				} else {
					respCersai.setSTATUS("S");
				}

				// Update Status of CERSAI Record
				if (isSuccess) {
					assignment.setHostReference(respCersai.getCERSAI_ASSET_ID());
					updateAssignmentCersai(assignment);
				}

				// CERSAI Response Update
				updateCersaiResp(respCersai);
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	/**
	 * Method for Fetching Collateral Assignment Info against Loan
	 * 
	 * @param finReference
	 * @param collateralRef
	 * @return
	 */
	private CollateralAssignment getCollateralAssignment(String finReference, String collateralRef) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT Reference, CollateralRef, HostReference ");
		sql.append(" From CollateralAssignment ");
		sql.append(" Where Reference =:Reference and CollateralRef=:CollateralRef ");
		source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralRef);
		source.addValue("Reference", finReference);

		RowMapper<CollateralAssignment> typeRowMapper = BeanPropertyRowMapper.newInstance(CollateralAssignment.class);
		CollateralAssignment assignment = null;
		try {
			assignment = this.namedJdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			assignment = null;
		}

		logger.debug(Literal.LEAVING);
		return assignment;
	}

	private void updateCersaiResp(Cersai respCersai) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append("update CERSAI_RESPONSE");
		sql.append(" set REMARKS = :REMARKS , STATUS = :STATUS");
		sql.append(" where CERSAI_ASSET_ID = :CERSAI_ASSET_ID AND LOAN_REFERENCE_NUMBER=:LOAN_REFERENCE_NUMBER AND ");
		sql.append(" COLLATERAL_REFERENCE_NUMBER=:COLLATERAL_REFERENCE_NUMBER ");

		paramMap.addValue("CERSAI_ASSET_ID", respCersai.getCERSAI_ASSET_ID());
		paramMap.addValue("LOAN_REFERENCE_NUMBER", respCersai.getLOAN_REFERENCE_NUMBER());
		paramMap.addValue("COLLATERAL_REFERENCE_NUMBER", respCersai.getCOLLATERAL_REFERENCE_NUMBER());
		paramMap.addValue("REMARKS", respCersai.getREMARKS());
		paramMap.addValue("STATUS", respCersai.getSTATUS());

		try {
			this.namedJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void updateAssignmentCersai(CollateralAssignment assignment) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();

		sql.append(" Update CollateralAssignment ");
		sql.append(" Set HostReference = :HostReference ");
		sql.append(" Where Reference =:Reference and CollateralRef=:CollateralRef ");

		paramMap.addValue("Reference", assignment.getReference());
		paramMap.addValue("CollateralRef", assignment.getCollateralRef());
		paramMap.addValue("HostReference", assignment.getHostReference());

		this.namedJdbcTemplate.update(sql.toString(), paramMap);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void processResponseFile(long userId, File file, Media media, DataEngineStatus ds) throws Exception {
		// TODO Auto-generated method stub
	}
}
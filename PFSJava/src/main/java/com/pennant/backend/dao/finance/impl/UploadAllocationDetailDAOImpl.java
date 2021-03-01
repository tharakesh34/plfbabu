/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  UploadAllocationDetailDAOImpl.java                                             * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-07-2018    														*
 *                                                                  						*
 * Modified Date    :  13-07-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-07-2018       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/

package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.UploadAllocationDetailDAO;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>UploadHeader model</b> class.<br>
 * 
 */
public class UploadAllocationDetailDAOImpl extends SequenceDao<UploadAlloctionDetail>
		implements UploadAllocationDetailDAO {

	private static Logger logger = LogManager.getLogger(UploadAllocationDetailDAOImpl.class);

	public UploadAllocationDetailDAOImpl() {
		super();
	}

	/**
	 * This method insert new Records into UploadHeader or UploadHeader_Temp.
	 * 
	 * save Promotion
	 * 
	 * @param Promotion
	 *            (promotion)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(List<UploadAlloctionDetail> listUploadAllocationDetail, long details, String rootId) {
		logger.debug("Entering");

		for (UploadAlloctionDetail uploadAlloctionDetail : listUploadAllocationDetail) {
			StringBuilder sql = new StringBuilder();
			if (uploadAlloctionDetail.getUploadAlloctionDetailId() == Long.MIN_VALUE) {
				uploadAlloctionDetail.setUploadAlloctionDetailId(getNextValue("SeqUploadAllocationDetail"));
				logger.debug("get NextID:" + uploadAlloctionDetail.getUploadAlloctionDetailId());
			}
			uploadAlloctionDetail.setUploadDetailId(details);
			uploadAlloctionDetail.setRootId(rootId);

			sql.append(" Insert Into UploadAlloctionDetails");
			sql.append(
					" (UploadDetailId, UploadAlloctionDetailId,rootId,AllocationType, ReferenceCode, PaidAmount, WaivedAmount)");
			sql.append(
					" Values (:UploadDetailId, :UploadAlloctionDetailId,:rootId, :allocationType, :referenceCode, :paidAmount, :waivedAmount)");

			logger.debug("sql: " + sql.toString());

			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(uploadAlloctionDetail);

			try {
				this.jdbcTemplate.update(sql.toString(), beanParameters);
			} catch (DataAccessException e) {
				logger.debug("Exception " + e.getMessage());
			}

			logger.debug("Leaving");

		}
	}

	@Override
	public void delete(long receiptHeaderId) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder deleteSql = new StringBuilder("delete from UPLOADALLOCTIONDETAILS");
		deleteSql.append(" where UPLOADDETAILID = :UPLOADDETAILID");

		logger.trace(Literal.SQL + deleteSql.toString());
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("UPLOADDETAILID", receiptHeaderId);

		try {
			this.jdbcTemplate.update(deleteSql.toString(), paramSource);
		} catch (DataAccessException e) {
			logger.debug("Error " + e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<UploadAlloctionDetail> getUploadedAllocatations(long ulDetailID) {

		UploadAlloctionDetail ulAlocDetail = new UploadAlloctionDetail();
		ulAlocDetail.setUploadDetailId(ulDetailID);

		StringBuilder selectSql = new StringBuilder(" Select UploadDetailId, UploadAlloctionDetailId,rootId,");
		selectSql.append(" AllocationType, ReferenceCode, PaidAmount, WaivedAmount");
		selectSql.append(" From UploadAlloctionDetails");
		selectSql.append(" Where UploadDetailId =:UploadDetailId order by UploadAlloctionDetailId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(ulAlocDetail);
		RowMapper<UploadAlloctionDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(UploadAlloctionDetail.class);

		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

}
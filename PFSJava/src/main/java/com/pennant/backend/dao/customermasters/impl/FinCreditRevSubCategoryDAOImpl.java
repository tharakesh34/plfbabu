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
 * * FileName : FinCreditRevSubCategoryDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 13-11-2013 *
 * * Modified Date : 13-11-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 13-11-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.customermasters.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.customermasters.FinCreditRevSubCategoryDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>FinCreditRevSubCategory model</b> class.<br>
 * 
 */

public class FinCreditRevSubCategoryDAOImpl extends BasicDao<FinCreditRevSubCategory>
		implements FinCreditRevSubCategoryDAO {
	private static Logger logger = LogManager.getLogger(FinCreditRevSubCategoryDAOImpl.class);

	public FinCreditRevSubCategoryDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new FinCreditRevSubCategory
	 * 
	 * @return FinCreditRevSubCategory
	 */

	@Override
	public FinCreditRevSubCategory getFinCreditRevSubCategory() {
		logger.debug("Entering");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinCreditRevSubCategory");
		FinCreditRevSubCategory finCreditRevSubCategory = new FinCreditRevSubCategory();
		if (workFlowDetails != null) {
			finCreditRevSubCategory.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug("Leaving");
		return finCreditRevSubCategory;
	}

	/**
	 * This method get the module from method getFinCreditRevSubCategory() and set the new record flag as true and
	 * return FinCreditRevSubCategory()
	 * 
	 * @return FinCreditRevSubCategory
	 */

	@Override
	public FinCreditRevSubCategory getNewFinCreditRevSubCategory() {
		logger.debug("Entering");
		FinCreditRevSubCategory finCreditRevSubCategory = getFinCreditRevSubCategory();
		finCreditRevSubCategory.setNewRecord(true);
		logger.debug("Leaving");
		return finCreditRevSubCategory;
	}

	/**
	 * Fetch the Record Finance Credit Review Sub Category details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return FinCreditRevSubCategory
	 */
	@Override
	public FinCreditRevSubCategory getFinCreditRevSubCategoryById(final String id, String type) {
		logger.debug("Entering");
		FinCreditRevSubCategory finCreditRevSubCategory = getFinCreditRevSubCategory();

		finCreditRevSubCategory.setId(id);

		StringBuilder selectSql = new StringBuilder(
				"Select SubCategoryCode, SubCategorySeque, CategoryId, SubCategoryDesc, SubCategoryItemType, ItemsToCal, ItemRule, isCreditCCY, mainSubCategoryCode, CalcSeque, format, percentCategory, grand");
		selectSql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From FinCreditRevSubCategory");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where SubCategoryCode =:SubCategoryCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCreditRevSubCategory);
		RowMapper<FinCreditRevSubCategory> typeRowMapper = BeanPropertyRowMapper
				.newInstance(FinCreditRevSubCategory.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * This method Deletes the Record from the FinCreditRevSubCategory or FinCreditRevSubCategory_Temp. if Record not
	 * deleted then throws DataAccessException with error 41003. delete Finance Credit Review Sub Category by key
	 * SubCategoryCode
	 * 
	 * @param Finance Credit Review Sub Category (finCreditRevSubCategory)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinCreditRevSubCategory finCreditRevSubCategory, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From FinCreditRevSubCategory");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where SubCategoryCode =:SubCategoryCode");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCreditRevSubCategory);
		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into FinCreditRevSubCategory or FinCreditRevSubCategory_Temp.
	 *
	 * save Finance Credit Review Sub Category
	 * 
	 * @param Finance Credit Review Sub Category (finCreditRevSubCategory)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(FinCreditRevSubCategory finCreditRevSubCategory, String type) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into FinCreditRevSubCategory");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(
				" (SubCategoryCode, SubCategorySeque, CategoryId, SubCategoryDesc, SubCategoryItemType, ItemsToCal, ItemRule, isCreditCCY, mainSubCategoryCode, CalcSeque, format, percentCategory, grand");
		insertSql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(
				" Values(:SubCategoryCode, :SubCategorySeque, :CategoryId, :SubCategoryDesc, :SubCategoryItemType, :ItemsToCal, :ItemRule, :isCreditCCY, :mainSubCategoryCode, :CalcSeque, :format, :percentCategory, :grand");
		insertSql.append(
				", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCreditRevSubCategory);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return finCreditRevSubCategory.getId();
	}

	@Override
	public boolean updateSubCategories(List<FinCreditRevSubCategory> finCreditRevSubCategoryList) {
		logger.debug("Entering");

		int sequence = 10;
		Collections.sort(finCreditRevSubCategoryList, new compareCalSeq());

		for (int i = 0; i < finCreditRevSubCategoryList.size(); i++) {
			finCreditRevSubCategoryList.get(i).setCalcSeque(String.valueOf(sequence));
			finCreditRevSubCategoryList.get(i).setSubCategorySeque(sequence);
			sequence = sequence + 2;
		}

		SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(finCreditRevSubCategoryList.toArray());
		String upDateQuery = " Update FinCreditRevSubCategory set SubCategorySeque = :SubCategorySeque , CalcSeque = :CalcSeque "
				+ " where SubCategoryCode = :SubCategoryCode";

		logger.debug("Update Query : " + upDateQuery);
		int[] updateCounts = jdbcTemplate.batchUpdate(upDateQuery, batch);
		logger.debug("Leaving");

		if (finCreditRevSubCategoryList.size() == updateCounts.length) {
			return true;
		}

		return false;
	}

	public class compareCalSeq implements Comparator<FinCreditRevSubCategory> {

		public compareCalSeq() {
		    super();
		}

		@Override
		public int compare(FinCreditRevSubCategory o1, FinCreditRevSubCategory o2) {
			if (Integer.parseInt(o1.getCalcSeque()) < Integer.parseInt(o2.getCalcSeque())) {
				return 0;
			} else {
				return 1;
			}
		}
	}

	/**
	 * This method updates the Record FinCreditRevSubCategory or FinCreditRevSubCategory_Temp. if Record not updated
	 * then throws DataAccessException with error 41004. update Finance Credit Review Sub Category by key
	 * SubCategoryCode and Version
	 * 
	 * @param Finance Credit Review Sub Category (finCreditRevSubCategory)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(FinCreditRevSubCategory finCreditRevSubCategory, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update FinCreditRevSubCategory");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(
				" Set SubCategorySeque = :SubCategorySeque, CategoryId = :CategoryId, SubCategoryDesc = :SubCategoryDesc, SubCategoryItemType = :SubCategoryItemType, ItemsToCal = :ItemsToCal, ItemRule = :ItemRule, isCreditCCY = :isCreditCCY, mainSubCategoryCode = :mainSubCategoryCode, CalcSeque = :CalcSeque, format = :format, percentCategory = :percentCategory, grand = :grand");
		updateSql.append(
				", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where SubCategoryCode =:SubCategoryCode");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCreditRevSubCategory);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

}
package com.pennanttech.extendedfield;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennanttech.pennapps.core.resource.Literal;

public class ExtendedFieldMappingUtil {
	public static Logger logger = LogManager.getLogger(ExtendedFieldMappingUtil.class);

	public static enum CustType {
		RETAIL, CORP, SME;
	}

	private static Map<String, String> cust_RETAIL = new HashMap<String, String>();
	private static Map<String, String> cust_CORP = new HashMap<String, String>();
	private static Map<String, String> cust_SME = new HashMap<String, String>();
	private static NamedParameterJdbcTemplate jdbcTemplate;
	protected DataSource dataSource;

	public static void resetCustomerMapping(CustType type) {
		try {
			type = CustType.RETAIL;
			switch (type) {
			case CORP:
				cust_CORP = new HashMap<String, String>();
				break;
			case SME:
				cust_SME = new HashMap<String, String>();
				break;
			default:
				cust_RETAIL = new HashMap<String, String>();
				break;
			}
			getCustomerMapping(type);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	public static Map<String, String> getCustomerMapping(CustType type) {
		switch (type) {
		case CORP:
			if (cust_CORP.isEmpty()) {
				cust_CORP = fetchCustomerMapping(type);
			}
			return cust_CORP;
		case SME:
			if (cust_SME.isEmpty()) {
				cust_SME = fetchCustomerMapping(type);
			}
			return cust_SME;
		default:
			cust_RETAIL = fetchCustomerMapping(type);
			return cust_RETAIL;
		}

	}

	private static Map<String, String> fetchCustomerMapping(CustType type) {
		Map<String, String> customerMapping = new HashMap<String, String>();
		ExtendedFieldsMapping extendedFieldsmapping = new ExtendedFieldsMapping();
		extendedFieldsmapping.setModuleCode("CUSTOMER_" + type.toString());

		List<ExtendedFieldsMapping> list = new ArrayList<ExtendedFieldsMapping>();
		StringBuilder sql = new StringBuilder("select * from INTERFACE_EXTENDED_MAPPING ");
		sql.append(" where module_code=:moduleCode ");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("moduleCode", extendedFieldsmapping.getModuleCode());

		RowMapper<ExtendedFieldsMapping> typeRowMapper = BeanPropertyRowMapper.newInstance(ExtendedFieldsMapping.class);
		try {
			list = jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		for (ExtendedFieldsMapping mapping : list) {
			customerMapping.put(mapping.getFieldCode(), mapping.getFieldName());
		}
		return customerMapping;
	}

	public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
		ExtendedFieldMappingUtil.jdbcTemplate = jdbcTemplate;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

	}

}

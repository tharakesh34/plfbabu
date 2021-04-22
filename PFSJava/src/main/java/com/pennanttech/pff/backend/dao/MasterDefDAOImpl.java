package com.pennanttech.pff.backend.dao;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.MasterDef;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

public class MasterDefDAOImpl extends BasicDao<MasterDef> implements MasterDefDAO {

	@Override
	public List<MasterDef> getMasterDefList() {
		RowMapper<MasterDef> rowMapper = BeanPropertyRowMapper.newInstance(MasterDef.class);

		try {
			return jdbcTemplate.query("select * from master_def", new MapSqlParameterSource(), rowMapper);
		} catch (Exception e) {
			throw new AppException("unable to load Master Definations");
		}
	}
}

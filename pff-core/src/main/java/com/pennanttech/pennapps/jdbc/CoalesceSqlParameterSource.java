package com.pennanttech.pennapps.jdbc;

import org.springframework.jdbc.core.namedparam.AbstractSqlParameterSource;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * {@link SqlParameterSource} implementation that obtains parameter values from bean properties of a given JavaBean
 * object. The {@code addValue} methods on this class will make adding several additional values easier, and will take
 * precedence over the bean parameters.
 */
public class CoalesceSqlParameterSource extends AbstractSqlParameterSource {
	private BeanPropertySqlParameterSource beanPropertySqlParameterSource;
	private MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

	/**
	 * Create a new CoalesceSqlParameterSource for the given bean.
	 * 
	 * @param object The bean instance to wrap.
	 */
	public CoalesceSqlParameterSource(Object object) {
		this.beanPropertySqlParameterSource = new BeanPropertySqlParameterSource(object);
	}

	/**
	 * Add a parameter to this parameter source.
	 * 
	 * @param paramName The name of the parameter.
	 * @param value     The value of the parameter.
	 */
	public void addValue(String paramName, Object value) {
		mapSqlParameterSource.addValue(paramName, value);
	}

	@Override
	public boolean hasValue(String paramName) {
		return mapSqlParameterSource.hasValue(paramName) || beanPropertySqlParameterSource.hasValue(paramName);
	}

	@Override
	public Object getValue(String paramName) throws IllegalArgumentException {
		return mapSqlParameterSource.hasValue(paramName) ? mapSqlParameterSource.getValue(paramName)
				: beanPropertySqlParameterSource.getValue(paramName);
	}

	@Override
	public int getSqlType(String paramName) {
		return mapSqlParameterSource.hasValue(paramName) ? mapSqlParameterSource.getSqlType(paramName)
				: beanPropertySqlParameterSource.getSqlType(paramName);
	}
}

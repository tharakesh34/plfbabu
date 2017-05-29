package com.pennant.backend.dao.maillog.impl;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.app.util.MailLog;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.maillog.MailLogDAO;
import com.pennant.backend.model.rulefactory.Notifications;

public class MailLogDAOImpl extends BasisNextidDaoImpl<Notifications> implements MailLogDAO {

	private static Logger logger = Logger.getLogger(MailLogDAOImpl.class);

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public MailLogDAOImpl() {
		super();
	}
	
	/**
	 * Method for log(save) the Mail sending information
	 */
	@Override
    public void saveMailLog(MailLog mailLog) {
		logger.debug("Entering");
		
    	StringBuilder insertSql = new StringBuilder("Insert Into MailLog ");
		insertSql.append(" (MailReference , Module , Reference , MailType , ");
		insertSql.append(" ValueDate , ReqUser , ReqUserRole ,UniqueRef )");
		insertSql.append(" Values(:MailReference , :Module , :Reference , :MailType , ");
		insertSql.append(" :ValueDate , :ReqUser , :ReqUserRole ,:UniqueRef )");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mailLog);
		int mailSent = this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		
		if(mailSent == 1) {
			//update SeqMailLog table
			String updateSql = "Update SeqMailLog set SeqNo = (Select MAX(:MailReference) from MailLog)";
			this.namedParameterJdbcTemplate.update(updateSql,beanParameters);
		}
		logger.debug("Leaving");
	    
    }

	/**
	 * Method for get Max sequence number from SeqMailLog table
	 */
	@Override
    public long getMailReference() {
	    logger.debug("Entering");
	    
	    MapSqlParameterSource source = new MapSqlParameterSource();
		
	    StringBuilder selectSql = new StringBuilder("Select Max(SeqNo) from SeqMailLog ");

		logger.debug("insertSql: " + selectSql.toString());

		logger.debug("Leaving");
		
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Long.class);
    }

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
}

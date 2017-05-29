package com.pennant.backend.dao.lmtmasters.impl;

import java.math.BigDecimal;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.dao.lmtmasters.FinCommodityInventoryDAO;
import com.pennant.backend.model.commodity.FinCommodityInventory;

public class FinCommodityInventoryDAOImpl extends BasisNextidDaoImpl<FinCommodityInventory> 
										  implements FinCommodityInventoryDAO {

	private static Logger logger = Logger.getLogger(FinCommodityInventoryDAOImpl.class);

	public FinCommodityInventoryDAOImpl() {
		super();
	}

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public long save(FinCommodityInventory finCommodityInventory,String type) {
		logger.debug("Entering");
		
		if(finCommodityInventory.getId()== 0 || finCommodityInventory.getId()==Long.MIN_VALUE){
			finCommodityInventory.setFinInventoryID(getNextidviewDAO().getNextId("SeqFinCommodityInventory"));	
		}
		
		StringBuilder insertSql = new StringBuilder("Insert Into FinCommodityInventory");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinInventoryID, Finreference, BrokerCode, HoldCertificateNo, ");
		insertSql.append(" Quantity, SaleQuantity, SalePrice, UnitSalePrice, CommodityStatus, DateOfAllocation,");
		insertSql.append(" DateOfSelling , DateCancelled, FeeCalculated, FeePayableDate, FeeBalance)");
		insertSql.append(" Values(:FinInventoryID, :Finreference, :BrokerCode, :HoldCertificateNo, ");
		insertSql.append(" :Quantity, :SaleQuantity, :SalePrice, :UnitSalePrice, :CommodityStatus, :DateOfAllocation,");
		insertSql.append(" :DateOfSelling, :DateCancelled, :FeeCalculated, :FeePayableDate, :FeeBalance)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCommodityInventory);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return finCommodityInventory.getFinInventoryID();
	}
	
	@Override
	public void update(FinCommodityInventory finCommodityInventory,String type) {
		logger.debug("Entering");

		StringBuilder updateSql =new StringBuilder("Update FinCommodityInventory");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append("  Set BrokerCode =:BrokerCode, HoldCertificateNo =:HoldCertificateNo,  ");
		updateSql.append("  Quantity =:Quantity, SaleQuantity =:SaleQuantity, SalePrice =:SalePrice, UnitSalePrice =:UnitSalePrice, CommodityStatus =:CommodityStatus, ");
		updateSql.append("  DateOfAllocation =:DateOfAllocation, DateOfSelling =:DateOfSelling, DateCancelled =:DateCancelled, FeeCalculated =:FeeCalculated, ");
		updateSql.append("  FeePayableDate =:FeePayableDate, FeeBalance =:FeeBalance ");
		updateSql.append("  Where Finreference =:Finreference");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCommodityInventory);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");

	}

	/**
	 * Method for fetch Commodity Inventory details
	 * 
	 * @return FinCommodityInventory
	 */
	@Override
    public FinCommodityInventory getFinCommodityInventoryById(String loanRefNumber,String type) {
		logger.debug("Entering");
		
		FinCommodityInventory finCommodityInventory = new FinCommodityInventory();
		finCommodityInventory.setFinreference(loanRefNumber);
		
		StringBuilder selectSql = new StringBuilder("SELECT  FinInventoryID, Finreference, BrokerCode, HoldCertificateNo,");
		selectSql.append("  Quantity, SaleQuantity, SalePrice, UnitSalePrice, CommodityStatus, DateOfAllocation,");
		selectSql.append("  DateOfSelling, DateCancelled, FeeCalculated, FeePayableDate, FeeBalance ");
		selectSql.append("  FROM  FinCommodityInventory");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append("  Where Finreference =:Finreference");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCommodityInventory);
		RowMapper<FinCommodityInventory> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinCommodityInventory.class);
		
		try{
			finCommodityInventory = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);	
		}catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finCommodityInventory = null;
		}
		logger.debug("Leaving");
		return finCommodityInventory;
    }

	/**
	 * Method for return total count of Bulk purchase commodities
	 * 
	 * @param finCommInventory
	 * @return Integer
	 */
	@Override
    public int getBulkCommodities(FinCommodityInventory finCommInventory) {
		logger.debug("Entering");
		
		StringBuilder selectSql = new StringBuilder("SELECT  Count(*) ");
		selectSql.append("  FROM  FinCommodityInventory");
		selectSql.append("  Where BrokerCode =:BrokerCode AND HoldCertificateNo =:HoldCertificateNo");
		selectSql.append("  AND Finreference !=:Finreference AND CommodityStatus !=:CommodityStatus");
		
		logger.debug("selectSql: " + selectSql.toString());
		
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCommInventory);
		
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);	
    }
	
	/**
	 * Method for return total count of Bulk purchase commodities
	 * 
	 * @param finCommInventory
	 * @return Integer
	 */
	@Override
	public BigDecimal getTotalCommodityQuantity(FinCommodityInventory finCommInventory) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("SELECT SUM(COALESCE(SaleQuantity,0)) ");
		selectSql.append("  FROM  FinCommodityInventory");
		selectSql.append("  Where BrokerCode =:BrokerCode AND HoldCertificateNo =:HoldCertificateNo");
		selectSql.append("  AND Finreference !=:Finreference AND CommodityStatus !=:CommodityStatus");

		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finCommInventory);

		BigDecimal totQuantity = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, BigDecimal.class);
		if(totQuantity == null){
			totQuantity = BigDecimal.ZERO;
		}
		logger.debug("Leaving");
		return totQuantity;
	}
	
	/**
	 * To Set  dataSource
	 * @param dataSource
	 */

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

}

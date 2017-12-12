package com.pennant.backend.service.limitservice.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.limit.LimitDetailDAO;
import com.pennant.backend.dao.limit.LimitHeaderDAO;
import com.pennant.backend.dao.limit.LimitReferenceMappingDAO;
import com.pennant.backend.dao.limit.LimitTransactionDetailsDAO;
import com.pennant.backend.dao.rulefactory.impl.LimitRuleDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.limit.LimitDetails;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.limit.LimitTransactionDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.LimitFilterQuery;
import com.pennant.backend.util.LimitConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;

public class LimitRuleManagement {

	private static Logger logger = Logger.getLogger(LimitRuleManagement.class);
	private  LimitDetailDAO limitDetailDAO;
	private  CustomerDAO customerDAO;
	private  LimitRuleDAO limitRuleDAO;
	private  LimitHeaderDAO limitHeaderDAO;
	private  LimitTransactionDetailsDAO limitTransactionDetailDAO;

	private  LimitReferenceMappingDAO limitReferenceMappingDAO;
	private  FinanceMainDAO financeMainDAO;
	private  BigDecimal utilizedLimit=BigDecimal.ZERO;  
	List<LimitFilterQuery> limitparamsList=null;

	public void processLimitRuleTransactions(String ruleBasedOn){	
		logger.debug("Entering");

		List<Customer> customerList = new ArrayList<Customer>();	
		// Send the details to Limitparms table  and get all the Limitparm rules (Execute the rule)
		limitparamsList=getLimitRuleDAO().getLimitRuleByModule(RuleConstants.MODULE_LMTLINE,ruleBasedOn,"_View");
		if(limitparamsList!=null){
			StringBuffer selectSql = new StringBuffer();
			for(LimitFilterQuery limitRule:limitparamsList){			
				if(StringUtils.equals(LimitConstants.LIMIT_CATEGORY_BANK, ruleBasedOn)){								
					prepareUnionAllQuery(limitRule.getQueryCode(),limitRule.getSQLQuery(),selectSql);					
				}else{

				}
			}
			if(!StringUtils.trimToEmpty(selectSql.toString()).isEmpty())
			customerList=getCustomerDAO().getCustomerByLimitRule(selectSql.toString(),"");
		}
		if(customerList!=null){
			String ruleCode="";
			LimitHeader header=null;		
			Map<String,LimitDetails> itemsListByrulecode=new HashMap<String,LimitDetails>();
			for(Customer cust:customerList){						
				if(!StringUtils.equals(cust.getRuleCode(),ruleCode)){
					header=null;									
					itemsListByrulecode=new HashMap<String,LimitDetails>();		
					header = getLimitHeaderDAO().getLimitHeaderByRule(cust.getRuleCode(), "", "_AView");
					if(header!=null){					
						convertToMap(getLimitDetailDAO().getLimitDetailsByLimitLine(header.getHeaderId(),""),itemsListByrulecode,header.getLimitCcy());		
					}
				}
				ruleCode=cust.getRuleCode();
				if(itemsListByrulecode.size()>0){
					List<FinanceMain> financesEnqList = getFinanceMainDAO().getFinanceMainbyCustId(cust.getCustID());					
					for(FinanceMain finnaceEnq:financesEnqList){
						getReferenceMapping(itemsListByrulecode,finnaceEnq,header.getRuleCode(),header.getRuleValue(),cust,new FinanceType());
					}
				}
			}
		}
		logger.debug("Leaving");
	}

	private void prepareUnionAllQuery(String queryCode, String sqlQuery, StringBuffer selectSql) {
		logger.debug("Entering");
		if(!StringUtils.trimToEmpty(selectSql.toString()).equals("")){
			selectSql.append(" UNION ALL ");
		}
		selectSql.append("SELECT CustID ,custDftBranch ,custCtgCode ,custTypeCode ,custGenderCode ,custIsStaff ,custIndustry ,custSector ,custSubSector ,custEmpSts ,custMaritalSts ,custSegment ,custSubSegment ,custParentCountry ,custRiskCountry ,custNationality ,custAddrProvince,'"+queryCode+"' AS ");
		selectSql.append(" RuleCode  From FINANCEMAIN_LIMIT_CHECK_AVIEW  ");
		selectSql.append(sqlQuery);
		logger.debug("Leaving");
	}

	private void getReferenceMapping(Map<String, LimitDetails> itemsListByrulecode, FinanceMain financeMain,
			String ruleCode, String ruleValue, Customer customer, FinanceType financeType) {
		logger.debug("Entering");

		RuleExecutionUtil ruleExecution = new RuleExecutionUtil();

		for (LimitDetails details : itemsListByrulecode.values()) {
			HashMap<String, Object> fieldsandvalues = new HashMap<String, Object>();
			Object ruleResult = null;

			if (financeMain != null)
				fieldsandvalues.putAll(financeMain.getDeclaredFieldValues());
			if (customer != null)
				fieldsandvalues.putAll(customer.getDeclaredFieldValues());
			if (financeType != null)
				fieldsandvalues.putAll(financeType.getDeclaredFieldValues());

			ruleResult = ruleExecution.executeRule(details.getSqlRule(), fieldsandvalues, financeMain.getFinCcy(),
					RuleReturnType.BOOLEAN);

			if (ruleResult != null && StringUtils.equals(ruleResult.toString(), "1")) {
				ruleResult = ruleExecution.executeRule(details.getSqlRule(), financeMain.getDeclaredFieldValues(),
						financeMain.getFinCcy(), RuleReturnType.BOOLEAN);
				if (ruleResult != null && StringUtils.equals(ruleResult.toString(), "1")) {
					saveLimitRuleTransactiondetails(financeMain, details, ruleCode, ruleValue);
					break;
				}
			}
		}

		logger.debug("Leaving");
	}

	private void saveLimitRuleTransactiondetails(FinanceMain financeMain,LimitDetails limitDetails, String ruleCode, String ruleValue) {
		logger.debug("entering");
		BigDecimal transactionAmount = financeMain.getFinAmount();
		if(!StringUtils.equals(financeMain.getFinCcy(), limitDetails.getCurrency())){
			transactionAmount = CalculationUtil.getConvertedAmount(financeMain.getFinCcy(), limitDetails.getCurrency(),financeMain.getFinAmount());
		}
		LimitTransactionDetail limitTransactionDetail  =new LimitTransactionDetail();
		limitTransactionDetail.setReferenceCode("F");
		limitTransactionDetail.setReferenceNumber(financeMain.getFinReference());
		limitTransactionDetail.setLimitLine(limitDetails.getLimitLine());
		limitTransactionDetail.setTransactionType(LimitConstants.APPROVE);
		limitTransactionDetail.setTransactionDate(new Timestamp(System.currentTimeMillis()));
		limitTransactionDetail.setOverrideFlag(false);
		limitTransactionDetail.setTransactionAmount(financeMain.getFinAmount());
		limitTransactionDetail.setTransactionCurrency(financeMain.getFinCcy());
		limitTransactionDetail.setLimitCurrency(limitDetails.getCurrency());
		limitTransactionDetail.setLimitAmount(transactionAmount);
		if(financeMain.getUserDetails()!=null){
			limitTransactionDetail.setCreatedBy(financeMain.getUserDetails().getUserId());
			limitTransactionDetail.setLastMntBy(financeMain.getUserDetails().getUserId());
		}
		limitTransactionDetail.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		limitTransactionDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));

		getLimitTransactionDetailDAO().saveLimitRuleTransactiondetails(limitTransactionDetail, "");
		logger.debug("Leaving");
	}

	public ArrayList<ErrorDetails> maintainLimitDetails() {
		logger.debug("Entering");

		ArrayList<ErrorDetails> errorDetails=new ArrayList<ErrorDetails>();
		getLimitTransactionDetailDAO().deleteAllRuleTransactions("");
		processLimitRuleTransactions(RuleConstants.EVENT_BANK);				
		updateRuleBasedLimits();
		logger.debug("Leaving");
		return errorDetails;
	}

	private void updateRuleBasedLimits() {
		logger.debug("Entering");
		if(limitparamsList!=null){
			for(LimitFilterQuery limitRule:limitparamsList){			

				LimitHeader limitHeader =getLimitHeaderDAO().getLimitHeaderByRule(limitRule.getQueryCode(), "", "_AView");
				List<LimitDetails> limitDetailsList= null;
				if(limitHeader!=null){
					limitDetailsList= getLimitDetailDAO().getLimitDetailsByHeaderId(limitHeader.getHeaderId(), "_AView");
					for(LimitDetails limitDetails:limitDetailsList){
						utilizedLimit=BigDecimal.ZERO;  
						if(limitDetails.getLimitLine()!=null){
							utilizedLimit= getLimitTransactionDetailDAO().getUtilizedSumByRulecode(limitHeader.getRuleCode(),limitDetails.getLimitLine(),"");
							limitDetails.setUtilisedLimit(utilizedLimit);
							limitDetails.setReservedLimit(BigDecimal.ZERO);
							getLimitDetailDAO().update(limitDetails, "");
						}
					}
				}

			}
		}
		logger.debug("Leaving");
	}

	private void convertToMap(ArrayList<LimitDetails> limitDetailsByRuleCode,
			Map<String, LimitDetails> itemsListByrulecode, String ccy) {
		for(LimitDetails limit:limitDetailsByRuleCode){
			limit.setCurrency(ccy);
			itemsListByrulecode.put(limit.getLimitLine(), limit);
		}

	}

	public LimitDetailDAO getLimitDetailDAO() {
		return limitDetailDAO;
	}

	public void setLimitDetailDAO(LimitDetailDAO limitDetailDAO) {
		this.limitDetailDAO = limitDetailDAO;
	}

	public LimitReferenceMappingDAO getLimitReferenceMappingDAO() {
		return limitReferenceMappingDAO;
	}

	public void setLimitReferenceMappingDAO(LimitReferenceMappingDAO limitReferenceMappingDAO) {
		this.limitReferenceMappingDAO = limitReferenceMappingDAO;
	}

	public LimitHeaderDAO getLimitHeaderDAO() {
		return limitHeaderDAO;
	}

	public void setLimitHeaderDAO(LimitHeaderDAO limitHeaderDAO) {
		this.limitHeaderDAO = limitHeaderDAO;
	}

	public LimitTransactionDetailsDAO getLimitTransactionDetailDAO() {
		return limitTransactionDetailDAO;
	}

	public void setLimitTransactionDetailDAO(LimitTransactionDetailsDAO limitTransactionDetailDAO) {
		this.limitTransactionDetailDAO = limitTransactionDetailDAO;
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public LimitRuleDAO getLimitRuleDAO() {
		return limitRuleDAO;
	}

	public void setLimitRuleDAO(LimitRuleDAO limitRuleDAO) {
		this.limitRuleDAO = limitRuleDAO;
	}
	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
}
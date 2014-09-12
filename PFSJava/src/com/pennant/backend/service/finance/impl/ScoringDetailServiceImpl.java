package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceScoreHeaderDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.rmtmasters.ScoringMetricsDAO;
import com.pennant.backend.dao.rmtmasters.ScoringSlabDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.GlobalVariable;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerScoringCheck;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;
import com.pennant.backend.model.rmtmasters.ScoringSlab;
import com.pennant.backend.model.rulefactory.NFScoreRuleDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.ScoringDetailService;
import com.pennant.backend.util.PennantConstants;

public class ScoringDetailServiceImpl extends GenericService<FinanceDetail> implements ScoringDetailService{
	
	private final static Logger logger = Logger.getLogger(ScoringDetailServiceImpl.class);
	
	private FinanceScoreHeaderDAO financeScoreHeaderDAO;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private ScoringSlabDAO scoringSlabDAO;
	private ScoringMetricsDAO scoringMetricsDAO;
	private RuleDAO ruleDAO;
	private RuleExecutionUtil ruleExecutionUtil;
	private CustomerDAO customerDAO;

	public ScoringDetailServiceImpl() {
		
	}
	
	
	/**
	 * Set Finance Eligibility Details to the Finance Detail
	 * @param financeDetail
	 * @param finType
	 * @param userRole
	 */
	@Override
	public FinanceDetail setFinanceScoringDetails(FinanceDetail financeDetail, String finType, String userRole, String ctgType){
		logger.debug("Entering");

		financeDetail.getScoringMetrics().clear();
		financeDetail.getScoringSlabs().clear();		
		List<ScoringMetrics> finScoringMetricList = null;
		List<ScoringMetrics> nonFinScoringMetricList = null;		
		List<ScoringSlab> scoringSlabsList = null;
		List<ScoringMetrics> scoringMetricslist = null;

		//Finance Scoring Metric/Group Rule Details List

		if (StringUtils.trimToNull(ctgType) == null) {
			return financeDetail;
		}

		List<FinanceReferenceDetail> scoringGroupList = null;
		if (PennantConstants.CUST_CAT_INDIVIDUAL.equals(ctgType)) {
			scoringGroupList = getFinanceReferenceDetailDAO().getFinRefDetByRoleAndFinType(finType, userRole, null, "_ASGView");
		} else {
			scoringGroupList = getFinanceReferenceDetailDAO().getFinRefDetByRoleAndFinType(finType, userRole, null, "_ACSGView");
		}


		if (scoringGroupList != null && !scoringGroupList.isEmpty()) {
			for (FinanceReferenceDetail scoringGroup : scoringGroupList) {
				//Scoring Slab Details List
				scoringSlabsList = getScoringSlabDAO().getScoringSlabsByScoreGrpId(scoringGroup.getFinRefId(), "_AView");
				financeDetail.setScoringSlabs(scoringGroup.getFinRefId(), scoringSlabsList);

				if (PennantConstants.CUST_CAT_INDIVIDUAL.equals(ctgType)) {			
					//Scoring Metric Details For Retail Customers
					scoringMetricslist = getScoringMetricsDAO().getScoringMetricsByScoreGrpId(scoringGroup.getFinRefId(), "R", "_AView");
					financeDetail.setScoringMetrics(scoringGroup.getFinRefId(), scoringMetricslist);
				} else {
					//Corporate Scoring Group for Financial Details
					finScoringMetricList = getScoringMetricsDAO().getScoringMetricsByScoreGrpId(scoringGroup.getFinRefId(),  "F", "_AView");
					//Non - Financial Scoring Metric Details
					nonFinScoringMetricList = getScoringMetricsDAO().getScoringMetricsByScoreGrpId(scoringGroup.getFinRefId(),  "N", "_AView");
				}
			}
		}

		if (finScoringMetricList != null && !finScoringMetricList.isEmpty()) {
			List<Long> metricIdList = new ArrayList<Long>();

			for (ScoringMetrics metric : finScoringMetricList) {
				metricIdList.add(metric.getScoringId());
			}

			/*List<Rule> ruleList = getRuleDAO().getRulesByFinScoreGroup(metricIdList,
						         "");
						ScoringMetrics metric = null;
						for (Rule rule : ruleList) {
							metric = new ScoringMetrics();
							metric.setScoringId(rule.getRuleId());
							metric.setLovDescScoringCode(rule.getRuleCode());
							metric.setLovDescScoringCodeDesc(rule.getRuleCodeDesc());
							metric.setLovDescSQLRule(rule.getSQLRule());
							List<ScoringMetrics> subMetricList = null;
							if (financeDetail.getScoringMetrics().containsKey(rule.getGroupId())) {
								subMetricList = financeDetail.getScoringMetrics().get(
								        rule.getGroupId());
							} else {
								subMetricList = new ArrayList<ScoringMetrics>();
							}
							subMetricList.add(metric);
							financeDetail.setScoringMetrics(rule.getGroupId(), subMetricList);
						}*/

			List<NFScoreRuleDetail> ruleList = getRuleDAO().getNFRulesByNFScoreGroup(metricIdList, "");
			ScoringMetrics metric = null;

			for (NFScoreRuleDetail rule : ruleList) {
				metric = new ScoringMetrics();
				metric.setScoringId(rule.getNFRuleId());
				metric.setLovDescScoringCode(String.valueOf(rule.getNFRuleId()));
				metric.setLovDescScoringCodeDesc(rule.getNFRuleDesc());
				metric.setLovDescMetricMaxPoints(rule.getMaxScore());

				List<ScoringMetrics> subMetricList = null;

				if (financeDetail.getScoringMetrics().containsKey(rule.getGroupId())) {
					subMetricList = financeDetail.getScoringMetrics().get(rule.getGroupId());
				} else {
					subMetricList = new ArrayList<ScoringMetrics>();
				}

				subMetricList.add(metric);
				financeDetail.setScoringMetrics(rule.getGroupId(), subMetricList);
			}

		}

		if (nonFinScoringMetricList != null && !nonFinScoringMetricList.isEmpty()) {
			List<Long> metricIdList = new ArrayList<Long>();

			for (ScoringMetrics metric : nonFinScoringMetricList) {
				metricIdList.add(metric.getScoringId());
			}

			List<NFScoreRuleDetail> ruleList = getRuleDAO().getNFRulesByNFScoreGroup( metricIdList, "");						
			ScoringMetrics metric = null;

			for (NFScoreRuleDetail rule : ruleList) {
				metric = new ScoringMetrics();
				metric.setScoringId(rule.getNFRuleId());
				metric.setLovDescScoringCode(String.valueOf(rule.getNFRuleId()));
				metric.setLovDescScoringCodeDesc(rule.getNFRuleDesc());
				metric.setLovDescMetricMaxPoints(rule.getMaxScore());

				List<ScoringMetrics> subMetricList = null;

				if (financeDetail.getScoringMetrics().containsKey(rule.getGroupId())) {
					subMetricList = financeDetail.getScoringMetrics().get(
							rule.getGroupId());
				} else {
					subMetricList = new ArrayList<ScoringMetrics>();
				}

				subMetricList.add(metric);
				financeDetail.setScoringMetrics(rule.getGroupId(), subMetricList);
			}
		}

		financeDetail.setScoringGroupList(scoringGroupList);
		financeDetail.setFinScoringMetricList(finScoringMetricList);
		financeDetail.setNonFinScoringMetricList(nonFinScoringMetricList);

		if(!financeDetail.getFinScheduleData().getFinanceMain().isNewRecord()){
			setFinScoreHeaderList(financeDetail);
			setExecutedScore(financeDetail);
		}
		logger.debug("Leaving");
		return financeDetail;
	}
	
	
	/**
	 * set the executed scoring details into map
	 * @param financeDetail
	 * @param 	 finType
	 * @return   List<FinanceScoreHeader> finScoreHeaderList
	 */
	private void setFinScoreHeaderList(FinanceDetail financeDetail) {
	    // Finance Scoring Module Details List 
	    List<String> groupIds = null;
	    List<FinanceScoreHeader> finScoreHeaderList = financeDetail.getFinScoreHeaderList();
	    List<Long> headerIds = null;
	    List<FinanceScoreDetail> financeScoreDetails = null;
	    List<FinanceScoreDetail> scoreDetailList = null;
	    
	    String finReference = financeDetail.getFinScheduleData().getFinReference(); 
	    
	    if (StringUtils.trimToNull(finReference) != null) {    	
	    	finScoreHeaderList =  getFinanceScoreHeaderDAO().getFinScoreHeaderList(financeDetail.getFinScheduleData().getFinReference(), "_View");
	    					
	    	if (finScoreHeaderList != null && !finScoreHeaderList.isEmpty()) {
	    		headerIds = new ArrayList<Long>();
	    		groupIds = new ArrayList<String>();
	    		
	    		for (FinanceScoreHeader header : finScoreHeaderList) {
	    			headerIds.add(header.getHeaderId());
	    			groupIds.add(String.valueOf(header.getGroupId()));
	    		}
	    		
	    		if (financeDetail.getScoreDetailListMap() == null) {
	    			new HashMap<Long, List<FinanceScoreDetail>>();
	    		} 
	    		
	    		financeDetail.getScoreDetailListMap().clear();
	    		
	    		financeScoreDetails  = getFinanceScoreHeaderDAO().getFinScoreDetailList(headerIds, "_View");
	    		
	    		if(financeScoreDetails != null) {						
	    			for (FinanceScoreDetail scoreDetail : financeScoreDetails) {
	    				scoreDetailList = new ArrayList<FinanceScoreDetail>();
	    				
	    				if (financeDetail.getScoreDetailListMap().containsKey(scoreDetail.getHeaderId())) {
	    					scoreDetailList = financeDetail.getScoreDetailListMap().get(scoreDetail.getHeaderId());
	    					financeDetail.getScoreDetailListMap().remove(scoreDetail.getHeaderId());
	    				}
	    				
	    				scoreDetailList.add(scoreDetail);
	    				financeDetail.getScoreDetailListMap().put(scoreDetail.getHeaderId(), scoreDetailList);
	    			}
	    		}
	    	}
	    }
	    
	    financeDetail.setFinScoreHeaderList(finScoreHeaderList);
    }
	

	/**
	 * Calculate the scoring for the metrics list based on the CustomerScoringCheck 
	 * @param List<ScoringMetrics> scoringMetricsList
	 * @param  CustomerScoringCheck customerScoringCheck
	 * 
	 */
	@Override
	public List<ScoringMetrics> executeScoringMetrics(List<ScoringMetrics> scoringMetricsList, CustomerScoringCheck customerScoringCheck) {
		ScriptEngine engine = getScriptEngine(customerScoringCheck);
		List<GlobalVariable> globaVariableList = SystemParameterDetails.getGlobaVariableList();
		BigDecimal lovDescExecutedScore = BigDecimal.ZERO;
		
		for (ScoringMetrics scoringMetrics : scoringMetricsList) {
			lovDescExecutedScore = getRuleScore(scoringMetrics.getLovDescSQLRule(), engine, globaVariableList);
			scoringMetrics.setLovDescExecutedScore(lovDescExecutedScore);
        }
		
		return scoringMetricsList;
	}
	
	/**
	 * Save or update the Customer Scoring Details
	 * @param financeDetail
	 */
	@Override
	public void saveOrUpdate(FinanceDetail financeDetail) {
		List<FinanceScoreHeader>  finScoreHeaderList = financeDetail.getFinScoreHeaderList();
		
		if (finScoreHeaderList != null && !finScoreHeaderList.isEmpty()) {
		
			for (FinanceScoreHeader header : finScoreHeaderList) {
				List<FinanceScoreDetail> scoreDetailList  = null;
				
				if (financeDetail.getScoreDetailListMap().containsKey(header.getHeaderId())) {
					scoreDetailList = financeDetail.getScoreDetailListMap().get(header.getHeaderId());
				}
				
				header.setFinReference(financeDetail.getFinScheduleData().getFinReference());
				getFinanceScoreHeaderDAO().deleteHeader(header, "");
				long headerId = getFinanceScoreHeaderDAO().saveHeader(header, "");
				
				if (scoreDetailList!=null) {
					for (FinanceScoreDetail detail : scoreDetailList) {
						detail.setHeaderId(headerId);
					}
					getFinanceScoreHeaderDAO().deleteDetailList(scoreDetailList, "");
					getFinanceScoreHeaderDAO().saveDetailList(scoreDetailList, "");
				}
			}
		}
	}
	
	/**
	 * Method for Fetching Score Details
	 */
	@Override
	public List<Object> getFinScoreDetailList(String finReference) {
		logger.debug("Entering");

		List<Object> scoreObjectList = null;
		List<FinanceScoreHeader> financeScoreheaders = getFinanceScoreHeaderDAO().getFinScoreHeaderList(finReference, "_View");
		List<Long> headerIds = new ArrayList<Long>();
		if (financeScoreheaders != null && financeScoreheaders.size() > 0) {

			HashMap<Long, List<FinanceScoreDetail>> scoreDetailListMap = new HashMap<Long, List<FinanceScoreDetail>>();
			for (FinanceScoreHeader header : financeScoreheaders) {
				headerIds.add(header.getHeaderId());
			}

			List<FinanceScoreDetail> financeScoreDetails = getFinanceScoreHeaderDAO()
			        .getFinScoreDetailList(headerIds, "_View");
			for (FinanceScoreDetail scoreDetail : financeScoreDetails) {
				List<FinanceScoreDetail> scoreDetailList = new ArrayList<FinanceScoreDetail>();
				if (scoreDetailListMap.containsKey(scoreDetail.getHeaderId())) {
					scoreDetailList = scoreDetailListMap.get(scoreDetail.getHeaderId());
					scoreDetailListMap.remove(scoreDetail.getHeaderId());
				}
				scoreDetailList.add(scoreDetail);
				scoreDetailListMap.put(scoreDetail.getHeaderId(), scoreDetailList);
			}

			scoreObjectList = new ArrayList<Object>(2);
			scoreObjectList.add(financeScoreheaders);
			scoreObjectList.add(scoreDetailListMap);
			logger.debug("Leaving");

		}
		logger.debug("Leaving");
		return scoreObjectList;
	}
	
	
	/**
	 * 
	 * Validate the Customer Scoring Details if the user role having AllowInputInStage
	 * @param financeMain
	 * @param auditDetail
	 * @param errParm
	 * @param valueParm
	 * @param usrLanguage
	 */
	@Override
	public void validate(FinanceDetail financeDetail, AuditDetail auditDetail, String[] errParm, String[] valueParm, String usrLanguage) {
		boolean isInputAllowed = false;

		List<FinanceReferenceDetail> scoringGroupList = financeDetail.getScoringGroupList();
		if(scoringGroupList != null && !scoringGroupList.isEmpty()) {
			if(StringUtils.trimToNull(scoringGroupList.get(0).getMandInputInStage()) != null) {
				isInputAllowed = true;
			}
		}

		if(isInputAllowed) {
			if (!financeDetail.isSufficientScore()) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,  "S0008", errParm, valueParm), usrLanguage));
			}
		}
	}
	
	
	/**
	 * Calculate the scoring for the metrics list based on the CustomerScoringCheck 
	 * @param List<ScoringMetrics> scoringMetricsList
	 * @param  CustomerScoringCheck customerScoringCheck
	 * 
	 */
	
	private void setExecutedScore(FinanceDetail financeDetail) {
		List<FinanceScoreHeader> scoringHeaderList = financeDetail.getFinScoreHeaderList();
		HashMap<Long, List<FinanceScoreDetail>> scoreDtlListMap = financeDetail.getScoreDetailListMap();
		List<FinanceScoreDetail> scoreDetailList = null;
		List<FinanceReferenceDetail> financeReferenceList =  financeDetail.getScoringGroupList();
		List<ScoringMetrics> scoringMetricsList = null;
		
		int minScore = 0;
		int executedScore = 0;
		int overRideScore = 0;
		boolean isOverride = false;
		

		// Retail Scoring
		if ((financeReferenceList != null && !financeReferenceList.isEmpty())) {
			
			for (FinanceReferenceDetail financeReferenceDetail : financeReferenceList) {
				scoringMetricsList = financeDetail.getScoringMetrics().get(financeReferenceDetail.getFinRefId());
				if(scoringMetricsList != null && !scoringMetricsList.isEmpty()) {
					for (ScoringMetrics scoringMetrics : scoringMetricsList) {
						if(scoringMetrics.getLovDescExecutedScore().compareTo(BigDecimal.ZERO) == 0) {
							for (FinanceScoreHeader header : scoringHeaderList) {
								minScore = header.getMinScore();
								overRideScore = header.getOverrideScore();
								isOverride = header.isOverride();
								
								if (scoreDtlListMap != null && scoreDtlListMap.containsKey(header.getHeaderId())) {
									scoreDetailList = scoreDtlListMap.get(header.getHeaderId());

									for (FinanceScoreDetail finScoreDetail : scoreDetailList) {
										if(finScoreDetail.getRuleId() == scoringMetrics.getScoringId()) {
											scoringMetrics.setLovDescExecutedScore(finScoreDetail.getExecScore());
											executedScore = executedScore + finScoreDetail.getExecScore().intValue(); // TODO CHECK
											break; // TODO Have to check 
										}
									}
								}

							}
						}
					}
				}

			}
		}	
		
		// Corporate Scoring
		if(financeDetail.getFinScoringMetricList() != null) {

			for (ScoringMetrics scoringMetric : financeDetail.getFinScoringMetricList()) {
				if(financeDetail.getScoringMetrics().containsKey(scoringMetric.getScoringId())){
					List<ScoringMetrics> subMetricList = financeDetail.getScoringMetrics().get(scoringMetric.getScoringId());

					if (subMetricList != null) {
						for (ScoringMetrics subScoreMetric : subMetricList) {
							if(subScoreMetric.getLovDescExecutedScore().compareTo(BigDecimal.ZERO) == 0) {
								for (FinanceScoreHeader header : scoringHeaderList) {
									minScore = header.getMinScore();
									overRideScore = header.getOverrideScore();
									isOverride = header.isOverride();

									if (scoreDtlListMap != null && scoreDtlListMap.containsKey(header.getHeaderId())) {
										scoreDetailList = scoreDtlListMap.get(header.getHeaderId());

										for (FinanceScoreDetail finScoreDetail : scoreDetailList) {
											if(finScoreDetail.getRuleId() == subScoreMetric.getScoringId()) {
												subScoreMetric.setLovDescExecutedScore(finScoreDetail.getExecScore());
												executedScore = executedScore + finScoreDetail.getExecScore().intValue(); // TODO CHECK
												break; // TODO Have to check 
											}
										}
									}

								}
							}
						}
					}

				}
			}		
		}
		
		if (minScore != 0) {
			if (minScore <= executedScore) {
				financeDetail.setSufficientScore(true);
			} else if (isOverride && (executedScore >= overRideScore)) {
				financeDetail.setSufficientScore(true);
			}
		}
	}
	
	
	/**
	 * Method to invoke method to execute Eligibility rules and return result.
	 * 
	 * @param financeReferenceDetail
	 * @return String
	 */
	public FinanceEligibilityDetail getScoringResult(FinanceEligibilityDetail financeEligibilityDetail, FinanceDetail financeDetail) {
		logger.debug("Entering");
		CustomerEligibilityCheck customerEligibilityCheck = financeDetail.getCustomerEligibilityCheck();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain(); 
 		String finCcy = financeMain.getFinCcy();
		
		String ruleResString = "";
		String rule = financeEligibilityDetail.getElgRuleValue(); 
  		BigDecimal finAmount = customerEligibilityCheck.getReqFinAmount();
  		customerEligibilityCheck.setReqFinAmount(CalculationUtil.getConvertedAmount(finCcy, null, finAmount));
  		//Amount Conversion Based upon Finance Currency
		if("D".equals(financeEligibilityDetail.getRuleResultType()) &&  StringUtils.isNumeric(ruleResString)){
			ruleResString = getRuleExecutionUtil().executeRule(rule, customerEligibilityCheck,finCcy);
			
			//Get Finance Currency
			ruleResString = CalculationUtil.getConvertedAmount(null, finCcy, new BigDecimal(ruleResString)).toString();
			financeMain.setFinAmount(finAmount);
		}else{
			ruleResString = getRuleExecutionUtil().executeRule(rule, customerEligibilityCheck, finCcy);
			
		}
		customerEligibilityCheck.setReqFinAmount(finAmount);
 		financeEligibilityDetail.setRuleResult(ruleResString);
		//financeEligibilityDetail.setEligible(getEligibilityDetailService().getEligibilityStatus(financeEligibilityDetail, financeMain));
		logger.debug("Leaving");
		return financeEligibilityDetail;
	}
	
	
	
	/**
	 * Method for Processing of SQL Rule and get Executed Score
	 * 
	 * @return
	 */
	public BigDecimal getRuleScore(String sqlRule, ScriptEngine engine, List<GlobalVariable> globalVariableList) {
		logger.debug("Entering");
		String reslut = "";
		
		try {
			reslut = (String) getRuleExecutionUtil().processEngineRule(sqlRule, engine, globalVariableList, PennantConstants.DEBIT);
		} catch (Exception e) {
			logger.debug(e);
			reslut = null;
		}
		
		if (reslut == null || reslut.equals("")) {
			reslut = "0";
		} 
		
		logger.debug("Leaving");
		return new BigDecimal(reslut);
	}
	
	
	private ScriptEngine getScriptEngine(CustomerScoringCheck customerScoringCheck) {
		ScriptEngine engine;
		HashMap<String, Object> fieldsandvalues;

		fieldsandvalues = new HashMap<String, Object>();
		engine = new ScriptEngineManager().getEngineByName("JavaScript");

		if (customerScoringCheck != null) {
			fieldsandvalues = customerScoringCheck.getDeclaredFieldValues();
		}

		ArrayList<String> keyset = new ArrayList<String>(fieldsandvalues.keySet());

		for (int i = 0; i < keyset.size(); i++) {
			Object var = fieldsandvalues.get(keyset.get(i));

			if (var instanceof String) {
				var = var.toString().trim();
			}

			engine.put(keyset.get(i),var );
		}
		return engine;
	}
	
	
	
	

	
	// Setters and getters
	public FinanceScoreHeaderDAO getFinanceScoreHeaderDAO() {
    	return financeScoreHeaderDAO;
    }

	public void setFinanceScoreHeaderDAO(FinanceScoreHeaderDAO financeScoreHeaderDAO) {
    	this.financeScoreHeaderDAO = financeScoreHeaderDAO;
    }

	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
	    this.financeReferenceDetailDAO = financeReferenceDetailDAO;
    }

	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
	    return financeReferenceDetailDAO;
    }



	public void setScoringSlabDAO(ScoringSlabDAO scoringSlabDAO) {
	    this.scoringSlabDAO = scoringSlabDAO;
    }



	public ScoringSlabDAO getScoringSlabDAO() {
	    return scoringSlabDAO;
    }



	public void setScoringMetricsDAO(ScoringMetricsDAO scoringMetricsDAO) {
	    this.scoringMetricsDAO = scoringMetricsDAO;
    }



	public ScoringMetricsDAO getScoringMetricsDAO() {
	    return scoringMetricsDAO;
    }



	public void setRuleDAO(RuleDAO ruleDAO) {
	    this.ruleDAO = ruleDAO;
    }



	public RuleDAO getRuleDAO() {
	    return ruleDAO;
    }



	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
	    this.ruleExecutionUtil = ruleExecutionUtil;
    }



	public RuleExecutionUtil getRuleExecutionUtil() {
	    return ruleExecutionUtil;
    }


	public void setCustomerDAO(CustomerDAO customerDAO) {
	    this.customerDAO = customerDAO;
    }


	public CustomerDAO getCustomerDAO() {
	    return customerDAO;
    }
	

}
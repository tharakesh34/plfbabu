package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.dao.finance.FinanceScoreHeaderDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.rmtmasters.ScoringMetricsDAO;
import com.pennant.backend.dao.rmtmasters.ScoringSlabDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;
import com.pennant.backend.model.rmtmasters.ScoringSlab;
import com.pennant.backend.model.rulefactory.NFScoreRuleDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.ScoringDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RuleReturnType;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class ScoringDetailServiceImpl extends GenericService<FinanceDetail> implements ScoringDetailService{
	
	private static final Logger logger = Logger.getLogger(ScoringDetailServiceImpl.class);
	
	private FinanceScoreHeaderDAO financeScoreHeaderDAO;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private ScoringSlabDAO scoringSlabDAO;
	private ScoringMetricsDAO scoringMetricsDAO;
	private RuleDAO ruleDAO;
	private RuleExecutionUtil ruleExecutionUtil;

	public ScoringDetailServiceImpl() {
		super();
	}
	
	/**
	 * Set Finance Eligibility Details to the Finance Detail
	 * @param financeDetail
	 * @param scoringGroupList
	 * @param ctgType
	 */
	@Override
	public FinanceDetail fetchFinScoringDetails(FinanceDetail financeDetail,List<FinanceReferenceDetail> scoringGroupList, String ctgType){
		return  fetchScoringSlabMetrics(financeDetail, scoringGroupList, ctgType);
	}
	
	
	/**
	 * Set Finance Eligibility Details to the Finance Detail
	 * @param financeDetail
	 * @param finType
	 * @param userRole
	 */
	@Override
	public FinanceDetail setFinanceScoringDetails(FinanceDetail financeDetail, String finType, String userRole, String ctgType,String screenEvent){
		logger.debug("Entering");
		if (StringUtils.trimToNull(ctgType) == null) {
			return financeDetail;
		}
		List<FinanceReferenceDetail> scoringGroupList = null;
		if (PennantConstants.PFF_CUSTCTG_INDIV.equals(ctgType)) {
			scoringGroupList = getFinanceReferenceDetailDAO().getFinRefDetByRoleAndFinType(finType,screenEvent, userRole, null, "_ASGView");
		} else {
			scoringGroupList = getFinanceReferenceDetailDAO().getFinRefDetByRoleAndFinType(finType,screenEvent, userRole, null, "_ACSGView");
		}
		logger.debug("Leaving");
		return fetchScoringSlabMetrics(financeDetail, scoringGroupList, ctgType);
	}
	
	
	/**
	 * Fetch and process scoring details
	 */
	private FinanceDetail fetchScoringSlabMetrics(FinanceDetail financeDetail,List<FinanceReferenceDetail> scoringGroupList,String ctgType){

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

		if (scoringGroupList != null && !scoringGroupList.isEmpty()) {
			for (FinanceReferenceDetail scoringGroup : scoringGroupList) {
				//Scoring Slab Details List
				scoringSlabsList = getScoringSlabDAO().getScoringSlabsByScoreGrpId(scoringGroup.getFinRefId(), "_AView");
				financeDetail.setScoringSlabs(scoringGroup.getFinRefId(), scoringSlabsList);

				if (PennantConstants.PFF_CUSTCTG_INDIV.equals(ctgType)) {			
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
		logger.debug("Entering");
		
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
	    logger.debug("Leaving");
    }
	

	/**
	 * Calculate the scoring for the metrics list based on the CustomerScoringCheck 
	 * @param List<ScoringMetrics> scoringMetricsList
	 * @param  CustomerScoringCheck customerScoringCheck
	 * 
	 */
	@Override
	public List<ScoringMetrics> executeScoringMetrics(List<ScoringMetrics> scoringMetricsList, CustomerEligibilityCheck customerEligibilityCheck) {
		logger.debug("Entering");
		
		HashMap<String, Object> fieldsandvalues = null;
		
		if (customerEligibilityCheck != null) {
			fieldsandvalues = customerEligibilityCheck.getDeclaredFieldValues();
		}
		
		for (ScoringMetrics scoringMetrics : scoringMetricsList) {
			Integer lovDescExecutedScore = (Integer) this.ruleExecutionUtil.executeRule(scoringMetrics.getLovDescSQLRule(), fieldsandvalues, null,RuleReturnType.INTEGER);
			scoringMetrics.setLovDescExecutedScore(new BigDecimal(lovDescExecutedScore));
        }
		logger.debug("Leaving");
		return scoringMetricsList;
	}
	
	/**
	 * Save or update the Customer Scoring Details
	 * @param financeDetail
	 */
	@Override
	public List<AuditDetail> saveOrUpdate(FinanceDetail financeDetail) {
		logger.debug("Entering");
		
		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();
		List<FinanceScoreHeader>  finScoreHeaderList = financeDetail.getFinScoreHeaderList();
		if (finScoreHeaderList != null && !finScoreHeaderList.isEmpty()) {
			
			FinanceScoreHeader tempHeader = new FinanceScoreHeader();
			String[] headerFields = PennantJavaUtil.getFieldDetails(tempHeader, tempHeader.getExcludeFields());
			tempHeader = null;
			
			FinanceScoreDetail tempDetail = new FinanceScoreDetail();
			String[] detailFields = PennantJavaUtil.getFieldDetails(tempDetail, tempDetail.getExcludeFields());
			tempDetail = null;
			
			long lastmntBy = financeDetail.getFinScheduleData().getFinanceMain().getLastMntBy();
			String recordStatus = financeDetail.getFinScheduleData().getFinanceMain().getRecordStatus();
			String rolecode = financeDetail.getFinScheduleData().getFinanceMain().getRoleCode();
		
			for(int i=0; i<finScoreHeaderList.size(); i++){
				
				FinanceScoreHeader header = finScoreHeaderList.get(i);
				List<FinanceScoreDetail> scoreDetailList  = null;
				
				if (financeDetail.getScoreDetailListMap().containsKey(header.getHeaderId())) {
					scoreDetailList = financeDetail.getScoreDetailListMap().get(header.getHeaderId());
				}
				
				long headerId = header.getHeaderId();
				header.setFinReference(financeDetail.getFinScheduleData().getFinReference());
				getFinanceScoreHeaderDAO().deleteHeader(header, "");
				headerId = getFinanceScoreHeaderDAO().saveHeader(header, "");
				
				header.setLastMntBy(lastmntBy);
				header.setRecordStatus(recordStatus);
				header.setRoleCode(rolecode);
				auditDetailList.add(new AuditDetail(PennantConstants.TRAN_WF, i+1, headerFields[0], headerFields[1], null, header));
				
				if (scoreDetailList!=null) {
					for(int j=0; j<scoreDetailList.size(); j++){
						
						FinanceScoreDetail detail = scoreDetailList.get(j);
						detail.setHeaderId(headerId);
						detail.setLastMntBy(lastmntBy);
						detail.setRecordStatus(recordStatus);
						detail.setRoleCode(rolecode);
						auditDetailList.add(new AuditDetail(PennantConstants.TRAN_WF, j+1, detailFields[0], detailFields[1], null, detail));
					}
					getFinanceScoreHeaderDAO().deleteDetailList(headerId, "");
					getFinanceScoreHeaderDAO().saveDetailList(scoreDetailList, "");
				}
			}
		}
		logger.debug("Leaving");
		return auditDetailList;
	}
	
	
	/**
	 * @param header
	 * @param type
	 * @return
	 */
	@Override
	public long saveHeader(FinanceScoreHeader header,String type){
		return getFinanceScoreHeaderDAO().saveHeader(header,type);
	}
	
	/**
	 * @param scoreDetailList
	 * @param type
	 */
	@Override
	public void saveDetailList(List<FinanceScoreDetail> scoreDetailList,String type){
		 getFinanceScoreHeaderDAO().saveDetailList(scoreDetailList, type);
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
	 * @param finReference
	 * @return
	 */
	@Override
	public List<FinanceScoreHeader> getFinScoreHeaderList(String finReference,String type){
		return getFinanceScoreHeaderDAO().getFinScoreHeaderList(finReference, type);
	}
	
	
	/**
	 * @param finReference
	 * @return
	 */
	@Override
	public List<FinanceScoreDetail> getFinScoreDetailList(List<Long> headerIds,String type){
	return getFinanceScoreHeaderDAO().getFinScoreDetailList(headerIds, type);
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
		logger.debug("Entering");
		boolean isInputAllowed = false;

		List<FinanceReferenceDetail> scoringGroupList = financeDetail.getScoringGroupList();
		if(scoringGroupList != null && !scoringGroupList.isEmpty()) {
			if(StringUtils.trimToNull(scoringGroupList.get(0).getMandInputInStage()) != null) {
				isInputAllowed = true;
			}
		}

		if(isInputAllowed) {
			if (!financeDetail.isSufficientScore()) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,  "30564", errParm, valueParm), usrLanguage));
			}
		}
		logger.debug("Leaving");
	}
	
	
	/**
	 * Calculate the scoring for the metrics list based on the CustomerScoringCheck 
	 * @param List<ScoringMetrics> scoringMetricsList
	 * @param  CustomerScoringCheck customerScoringCheck
	 * 
	 */
	
	private void setExecutedScore(FinanceDetail financeDetail) {
		logger.debug("Entering");
		
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
		if (financeReferenceList != null && !financeReferenceList.isEmpty()) {
			
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
											executedScore = executedScore + finScoreDetail.getExecScore().intValue(); 
											break; 
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
												executedScore = executedScore + finScoreDetail.getExecScore().intValue(); 
												break; 
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
		logger.debug("Leaving");
	}
	
	
	/**
	 * Method for Deleting Lit of Headers  by Finance Reference
	 * @param finReference
	 * @param type
	 */
	@Override
	public void deleteHeaderList(String finReference, String type){
		getFinanceScoreHeaderDAO().deleteHeaderList(finReference, type);
	}
	
	/**
	 * Method for Deleting Lit of Score Details by Header ID Details
	 * @param finReference
	 * @param type
	 */
	@Override
	public void deleteDetailList(List<Long> headerList, String type){
		getFinanceScoreHeaderDAO().deleteDetailList(headerList, type);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
		
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
}
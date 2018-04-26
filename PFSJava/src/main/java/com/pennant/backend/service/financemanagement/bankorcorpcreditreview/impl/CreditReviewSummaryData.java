package com.pennant.backend.service.financemanagement.bankorcorpcreditreview.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;
import com.pennant.backend.util.FacilityConstants;

public class CreditReviewSummaryData {

	private static Logger logger= Logger.getLogger(CreditReviewSummaryData.class);

	private Map<String,List<FinCreditReviewSummary>> detailsMap = new HashMap<String,List<FinCreditReviewSummary>> ();
	private Map<String,String> dataMap = null;
	private Map<String,String> itemTotCalMap = null;
	private Map<String,String> itemRuleMap = new HashMap<String,String> ();
	private boolean ratioFlag= false;
	private transient CreditApplicationReviewService creditApplicationReviewService;


	private List<FinCreditRevCategory> listOfFinCreditRevCategory = null;
	
	public CreditReviewSummaryData() {
		super();
	}
	
	/**
	 * This method for setting the data or storing the all the data in map.<BR>
	 * If we call this we will get all the data in map.<BR>
	 * @param custID
	 * @param year
	 * @param noOfYears
	 * @return Map<String,String>
	 */
	public Map<String,String>  setDataMap(long custID, int year, int noOfYears, String custCtgType, boolean required, boolean isEnquiry, Map<String, String> externalDataMap) {
		logger.debug("Entering");

		// create a script engine manager
		ScriptEngineManager factory = new ScriptEngineManager();

		// create a JavaScript engine
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		List<FinCreditRevSubCategory>  listOfFinCreditRevSubCategoryRatio = null;
		dataMap = new HashMap<String,String> ();
		itemTotCalMap = new HashMap<String,String> ();
		Map<String ,List<FinCreditReviewSummary>> detailedMap;
		if(isEnquiry){
		detailedMap = this.creditApplicationReviewService.getListCreditReviewSummaryByCustId(custID, noOfYears+1,year, "");
		} else {
			detailedMap = this.creditApplicationReviewService.getListCreditReviewSummaryByCustId(custID, noOfYears+1,year, "_View");
		}

		logger.debug("Loading external data to dataMap");
		// Load extended points to script engine.
		// This data is expected to come from Finance Main Dialog (Mainly Loan Details, banking details and obligations)
		// If there is no external data, no need to load the data to Map
		// If this screen is not loaded from Finance Main tabs data will be empty and if any ratios/cells using these variables will not work
		// Changes done to use eligibility tab without the facility for Profectus
		
		if(externalDataMap != null && externalDataMap.size() > 0){
			for (Entry<String, String> entry : externalDataMap.entrySet()) {
				if (entry.getKey().startsWith("EXT_")) {
					engine.put(entry.getKey(),externalDataMap.get(entry.getKey()));
					dataMap.put(entry.getKey(),externalDataMap.get(entry.getKey()));
				}
			}
		}
		logger.debug("Loading external data to dataMap");
		
		Set<String> set = detailedMap.keySet();

		//put the values in the map
		for(String key : set){
			this.detailsMap.put(key, detailedMap.get(key));
		}
		ratioFlag= false;
		listOfFinCreditRevCategory = this.creditApplicationReviewService.getCreditRevCategoryByCreditRevCode(custCtgType);
		if(detailsMap.size() > 0){
			for(FinCreditRevCategory fcrcy:listOfFinCreditRevCategory){
				if(FacilityConstants.CREDITREVIEW_REMARKS.equals(fcrcy.getRemarks())){
					this.ratioFlag = true;
				}
				listOfFinCreditRevSubCategoryRatio = this.creditApplicationReviewService.
				getFinCreditRevSubCategoryByCategoryId(fcrcy.getCategoryId());
				List<FinCreditRevSubCategory>  listOfFinCreditRevSubCategory = this.creditApplicationReviewService.
				getFinCreditRevSubCategoryByCategoryIdAndCalcSeq(fcrcy.getCategoryId());
				
				if(detailedMap.get(year-noOfYears) != null){
					List<FinCreditReviewSummary>	listOfCreditReviewSummary = this.detailsMap.get(year-noOfYears);
					for(FinCreditReviewSummary finCreditReviewSummary : listOfCreditReviewSummary){
						engine.put("YM"+finCreditReviewSummary.getSubCategoryCode(), 
								finCreditReviewSummary.getItemValue() != null ? finCreditReviewSummary.getItemValue() : BigDecimal.ZERO);
					}
				} else {
					for(FinCreditRevSubCategory finCreditRevSubCategory : listOfFinCreditRevSubCategory){
						engine.put("YM"+finCreditRevSubCategory.getSubCategoryCode(), BigDecimal.ZERO);
					} 
				}
				
				for(int i =0 ;i<listOfFinCreditRevSubCategory.size();i++){

					FinCreditRevSubCategory finCreditRevSubCategory = null;
					finCreditRevSubCategory=listOfFinCreditRevSubCategory.get(i);

					if("Calc".equals(finCreditRevSubCategory.getSubCategoryItemType())){
						itemTotCalMap.put(finCreditRevSubCategory.getSubCategoryCode(), finCreditRevSubCategory.getItemsToCal());
					}  
					itemRuleMap.put(finCreditRevSubCategory.getSubCategoryCode(), finCreditRevSubCategory.getItemRule());
				}

				// entries
				for(int j=noOfYears;j>=1;j--){	

					String auditYear =String.valueOf(year-j+1);
					List<FinCreditReviewSummary> listOfCreditReviewSummary;

					listOfCreditReviewSummary = this.detailsMap.get(auditYear);
                    
					if(listOfCreditReviewSummary.size()>0){	
						for(int k=0;k<listOfCreditReviewSummary.size();k++){
							// Putting Values into map
							FinCreditReviewSummary creditReviewSummary = listOfCreditReviewSummary.get(k);
							if(!dataMap.containsKey("lovDescCcyEditField")){
								dataMap.put("lovDescCcyEditField", String.valueOf(creditReviewSummary.getLovDescCcyEditField()));
							}

							engine.put("EXCHANGE", creditReviewSummary.getLovDescConversionRate());
							engine.put("NoOfShares", creditReviewSummary.getLovDescNoOfShares());
							engine.put("MarketPrice", creditReviewSummary.getLovDescMarketPrice());
							String value = "--";									
							if(!itemTotCalMap.keySet().contains(creditReviewSummary.getSubCategoryCode())){
								try{
									value = creditReviewSummary.getItemValue().toString();
								} catch (Exception e) {
									value ="--";
									logger.error("Exception: ", e);
								}
								engine.put("Y"+(noOfYears-j)+creditReviewSummary.getSubCategoryCode(),!("--").equals(value)?new BigDecimal(value):BigDecimal.ZERO);
								dataMap.put("Y"+(noOfYears-j)+"_"+creditReviewSummary.getSubCategoryCode(),value);
								if(noOfYears == j){
									engine.put("Y"+(noOfYears-j)+"DIVCOUNT",BigDecimal.ONE);
								} else {
									engine.put("Y"+(noOfYears-j)+"DIVCOUNT", new BigDecimal(2));
								}
							}	
						}
					}else{
						for(int r=0;r<listOfFinCreditRevSubCategoryRatio.size();r++){
							FinCreditRevSubCategory finCreditRevSubCategory =listOfFinCreditRevSubCategory.get(r);
							if(!itemTotCalMap.keySet().contains(finCreditRevSubCategory.getSubCategoryCode())){
								engine.put("Y"+(noOfYears-j)+finCreditRevSubCategory.getSubCategoryCode(),BigDecimal.ZERO);
								dataMap.put("Y"+(noOfYears-j)+"_"+finCreditRevSubCategory.getSubCategoryCode(),String.valueOf(BigDecimal.ZERO));
							}
						}
					}
				}
				
				//Total Calculations
				for(int m=noOfYears;m>=1;m--){	
					for(int q=0;q<listOfFinCreditRevSubCategoryRatio.size();q++){
						
						FinCreditRevSubCategory finCreditRevSubCategory =listOfFinCreditRevSubCategory.get(q);
						if(itemTotCalMap.keySet().contains(finCreditRevSubCategory.getSubCategoryCode())){							
							String value = "--";

							try{
								if(StringUtils.isBlank(itemTotCalMap.get(finCreditRevSubCategory.getSubCategoryCode())) ){
									value = String.valueOf(0);
								}else{
									value = engine.eval(replaceYear(itemTotCalMap.get(finCreditRevSubCategory.getSubCategoryCode()),noOfYears-m)).toString();
									if("NaN".equals(value) || value.contains("Infinity")){
										value ="--";
									}
								}	
							} catch (Exception e) {
								value ="--";
								logger.error("Exception: ", e);
							}
							engine.put("Y"+(noOfYears-m)+finCreditRevSubCategory.getSubCategoryCode(),!value.contains("--")?new BigDecimal(value):BigDecimal.ZERO);
							dataMap.put("Y"+(noOfYears-m)+"_"+finCreditRevSubCategory.getSubCategoryCode(),String.valueOf(!value.contains("--")?new BigDecimal(value):BigDecimal.ZERO));
						}
					}
				}

				//break down
				//entries
				for(int l=noOfYears;l>=1;l--){	
					for(int p=0;p<listOfFinCreditRevSubCategoryRatio.size();p++){
						FinCreditRevSubCategory finCreditRevSubCategory = null;
						finCreditRevSubCategory=listOfFinCreditRevSubCategory.get(p);
						if(itemRuleMap.keySet().contains(finCreditRevSubCategory.getSubCategoryCode())){
							String value ="--";
							try{
								if(StringUtils.isNotEmpty(itemRuleMap.get(finCreditRevSubCategory.getSubCategoryCode()))){
									value = engine.eval(replaceYear(itemRuleMap.get(finCreditRevSubCategory.getSubCategoryCode()),noOfYears-l)).toString();
									if("NaN".equals(value) || value.contains("Infinity")){
										value ="--";
									}
								} 
							}catch (Exception e) {
								logger.error("Exception: ", e);
								value ="--";
							}
							if(this.ratioFlag ){
								engine.put("Y"+(noOfYears-l)+finCreditRevSubCategory.getSubCategoryCode(),!value.contains("--")?new BigDecimal(value):BigDecimal.ZERO);
								dataMap.put("Y" + (noOfYears - l) + "_"
										+ finCreditRevSubCategory.getSubCategoryCode(), String
										.valueOf(!value.contains("--") ? new BigDecimal(value) : BigDecimal.ZERO));
							}else if(required){
								dataMap.put("RY" + (noOfYears - l) + "_"
										+ finCreditRevSubCategory.getSubCategoryCode(), String
										.valueOf(!value.contains("--") ? new BigDecimal(value)  : BigDecimal.ZERO));
							}
						}
					}

					// % change calculation
					if(noOfYears != l ){
						BigDecimal ratioCalValPrev = BigDecimal.ZERO;
						BigDecimal ratioCalValCurr = BigDecimal.ZERO;
						String value = "--";
						for(int p=0;p<listOfFinCreditRevSubCategoryRatio.size();p++){
							FinCreditRevSubCategory finCreditRevSubCategory=listOfFinCreditRevSubCategory.get(p);
							try{
								ScriptEngine engine1 = factory.getEngineByName("JavaScript");
								if(dataMap.get("Y"+(noOfYears-l-1)+"_"+finCreditRevSubCategory.getSubCategoryCode()) != null){
									ratioCalValPrev =new BigDecimal(dataMap.get("Y"+(noOfYears-l-1)+"_"+finCreditRevSubCategory.getSubCategoryCode()));
								} else {
									ratioCalValPrev = BigDecimal.ZERO;
								}
								if(dataMap.get("Y"+(noOfYears-l)+"_"+finCreditRevSubCategory.getSubCategoryCode()) != null){
									ratioCalValCurr =new BigDecimal(dataMap.get("Y"+(noOfYears-l)+"_"+finCreditRevSubCategory.getSubCategoryCode()));
								} else {
									ratioCalValCurr = BigDecimal.ZERO;
								}
								engine1.put("ratioCalValPrev", ratioCalValPrev);
								engine1.put("ratioCalValCurr", ratioCalValCurr);
								value = engine1.eval("((ratioCalValCurr-ratioCalValPrev)/ratioCalValPrev)*100").toString();
								if("NaN".equals(value) || value.contains("Infinity")){
									value = "--";
								}
							}catch(Exception e){
								value = "--";
								logger.error("Exception: ", e);
							}
							if(required){
								dataMap.put("CY"+(noOfYears-l)+"_"+finCreditRevSubCategory.getSubCategoryCode(),String
										.valueOf(!value.contains("--") ? new BigDecimal(value) : BigDecimal.ZERO)); 
							}
						}
					}
				}
			}
		}
		logger.debug("Leaving");
		return dataMap;
	}
	public CreditApplicationReviewService getCreditApplicationReviewService() {
		return creditApplicationReviewService;
	}
	public void setCreditApplicationReviewService(
			CreditApplicationReviewService creditApplicationReviewService) {
		this.creditApplicationReviewService = creditApplicationReviewService;
	}
	
	public String replaceYear(String formula,int year){
		
		int noOfYears;
		noOfYears = SysParamUtil.getValueAsInt("NO_OF_YEARS_TOSHOW");
		String formatedFormula= formula;
		for(int i= 0;i< noOfYears;i++){
			if(i==0){
				formatedFormula = formatedFormula.replace("YN.","Y"+year);
			}else{
				formatedFormula = formatedFormula.replace("YN-"+i+".","Y"+(year == 0 ? "M" : year-i));
			}
		}
		return formatedFormula;
	}
}

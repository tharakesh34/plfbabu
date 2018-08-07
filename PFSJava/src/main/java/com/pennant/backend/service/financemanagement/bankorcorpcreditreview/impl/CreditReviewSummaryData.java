package com.pennant.backend.service.financemanagement.bankorcorpcreditreview.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.script.ScriptEngine;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

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
	private transient ScriptEngine scriptEngine;

	public CreditReviewSummaryData() {
		super();
	}
	
	public static BigDecimal formateAmount(BigDecimal amount, int dec) {
		BigDecimal returnAmount = BigDecimal.ZERO;
		if (amount != null) {
			returnAmount = amount.divide(BigDecimal.valueOf(Math.pow(10, dec)));
		}
		return returnAmount;
	}
	
	/**
	 * This method for setting the data or storing the all the data in map.<BR>
	 * If we call this we will get all the data in map.<BR>
	 * @param custID
	 * @param year
	 * @param noOfYears
	 * @return Map<String,String>
	 */
	public Map<String,String>  setDataMap(long custID, Set<Long> custIds, int year, int noOfYears, String custCtgType, boolean required, boolean isEnquiry, Map<String, String> externalDataMap
			, List<FinCreditRevCategory> listOfFinCreditRevCategory) {
		logger.debug("Entering");

		// create a JavaScript engine
		// get Script engine object
		ScriptEngine engine = this.scriptEngine;
		
		List<FinCreditRevSubCategory>  listOfFinCreditRevSubCategoryRatio = null;
		dataMap = new HashMap<String,String> ();
		itemTotCalMap = new HashMap<String,String> ();
		Map<String ,List<FinCreditReviewSummary>> detailedMap;
		
		if(isEnquiry){
			detailedMap = this.creditApplicationReviewService.getListCreditReviewSummaryByCustId(custID, noOfYears+1,year, "");
			
			for (Long coAppcustID : custIds) {

				Map<String ,List<FinCreditReviewSummary>>	codetailedMap = this.creditApplicationReviewService.getListCreditReviewSummaryByCustId(coAppcustID,
						noOfYears + 1, year, "");
				for (Entry<String, List<FinCreditReviewSummary>> finCreditRevSubCategory : codetailedMap.entrySet()) {

					if (!finCreditRevSubCategory.getValue().isEmpty()) {
						// co-app data
						String key = finCreditRevSubCategory.getKey();
						List<FinCreditReviewSummary> coAppDataList = finCreditRevSubCategory.getValue();

						// customer data
						List<FinCreditReviewSummary> custDataList = detailedMap.get(key);
						if (custDataList != null && !custDataList.isEmpty()) {

							for (FinCreditReviewSummary finCreditReviewSummary : custDataList) {
								// Checking sub category code to add item value
								FinCreditReviewSummary coAppdate = isFinCredirtExsist(finCreditReviewSummary,
										coAppDataList);
								if (coAppdate != null) {
									finCreditReviewSummary.setItemValue(
											finCreditReviewSummary.getItemValue().add(coAppdate.getItemValue()));
								}
							}
						}
					}
				}
			}
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
					//dataMap.put(entry.getKey(),externalDataMap.get(entry.getKey()));
				}
			}
			dataMap.putAll(externalDataMap);
		}
		logger.debug("Loading external data to dataMap");
		
		Set<String> set = detailedMap.keySet();

		//put the values in the map
		for(String key : set){
			this.detailsMap.put(key, detailedMap.get(key));
		}
		ratioFlag= false;
		if(detailsMap.size() > 0){
			for(FinCreditRevCategory fcrcy:listOfFinCreditRevCategory){
				int noOfYearsToShow = fcrcy.getNoOfyears();
				long categoryId = fcrcy.getCategoryId();
				int  dataYear = year-noOfYears;
				if(FacilityConstants.CREDITREVIEW_REMARKS.equals(fcrcy.getRemarks())){
					this.ratioFlag = true;
				}
				listOfFinCreditRevSubCategoryRatio = this.creditApplicationReviewService.
				getFinCreditRevSubCategoryByCategoryId(categoryId);
				List<FinCreditRevSubCategory>  listOfFinCreditRevSubCategory = this.creditApplicationReviewService.
				getFinCreditRevSubCategoryByCategoryIdAndCalcSeq(categoryId);
				
				if(detailedMap.get(dataYear) != null){
					List<FinCreditReviewSummary>	listOfCreditReviewSummary = this.detailsMap.get(dataYear);
					for(FinCreditReviewSummary finCreditReviewSummary : listOfCreditReviewSummary){
						engine.put("YM"+finCreditReviewSummary.getSubCategoryCode(), 
								finCreditReviewSummary.getItemValue() != null ? formateAmount(finCreditReviewSummary.getItemValue(),2) : BigDecimal.ZERO);
					}
				} else {
					for(FinCreditRevSubCategory finCreditRevSubCategory : listOfFinCreditRevSubCategory){
						engine.put("YM"+finCreditRevSubCategory.getSubCategoryCode(), BigDecimal.ZERO);
					} 
				}
				
				for(int i =0 ;i<listOfFinCreditRevSubCategory.size();i++){

					FinCreditRevSubCategory finCreditRevSubCategory = null;
					finCreditRevSubCategory=listOfFinCreditRevSubCategory.get(i);
					String subCategoryCode = finCreditRevSubCategory.getSubCategoryCode();

					if("Calc".equals(finCreditRevSubCategory.getSubCategoryItemType())){
						itemTotCalMap.put(subCategoryCode, finCreditRevSubCategory.getItemsToCal());
					}  
					itemRuleMap.put(subCategoryCode, finCreditRevSubCategory.getItemRule());
				}

				// entries
				for(int j=noOfYears;j>=1;j--){	

					String auditYear =String.valueOf(year-j+1);
					int years = noOfYears-j;
					List<FinCreditReviewSummary> listOfCreditReviewSummary;

					listOfCreditReviewSummary = this.detailsMap.get(auditYear);
                    
					if(listOfCreditReviewSummary.size()>0){	
						for(int k=0;k<listOfCreditReviewSummary.size();k++){
							// Putting Values into map
							FinCreditReviewSummary creditReviewSummary = listOfCreditReviewSummary.get(k);
							String subCategoryCode = creditReviewSummary.getSubCategoryCode();
							if(!dataMap.containsKey("lovDescCcyEditField")){
								dataMap.put("lovDescCcyEditField", String.valueOf(creditReviewSummary.getLovDescCcyEditField()));
							}

							engine.put("EXCHANGE", creditReviewSummary.getLovDescConversionRate());
							engine.put("NoOfShares", creditReviewSummary.getLovDescNoOfShares());
							engine.put("MarketPrice", creditReviewSummary.getLovDescMarketPrice());
							String value = "--";									
							if(!itemTotCalMap.keySet().contains(subCategoryCode)){
								try{
									value = creditReviewSummary.getItemValue().toString();
								} catch (Exception e) {
									value ="--";
									logger.error("Exception: ", e);
								}
								engine.put("Y"+(years)+subCategoryCode,!("--").equals(value)?formateAmount(new BigDecimal(value),2):BigDecimal.ZERO);
								dataMap.put("Y"+(years)+"_"+subCategoryCode,value);
								if(noOfYears == j){
									engine.put("Y"+(years)+"DIVCOUNT",BigDecimal.ONE);
								} else {
									engine.put("Y"+(years)+"DIVCOUNT", new BigDecimal(2));
								}
							}	
						}
					}else{
						for(int r=0;r<listOfFinCreditRevSubCategoryRatio.size();r++){
							FinCreditRevSubCategory finCreditRevSubCategory =listOfFinCreditRevSubCategory.get(r);
							String subCategoryCode = finCreditRevSubCategory.getSubCategoryCode();
							if(!itemTotCalMap.keySet().contains(subCategoryCode)){
								engine.put("Y"+(years)+subCategoryCode,BigDecimal.ZERO);
								dataMap.put("Y"+(years)+"_"+subCategoryCode,String.valueOf(BigDecimal.ZERO));
							}
						}
					}
				}
				
				//Total Calculations
				for(int m=noOfYears;m>=1;m--){	
					for(int q=0;q<listOfFinCreditRevSubCategoryRatio.size();q++){
						int years = noOfYears-m;
						FinCreditRevSubCategory finCreditRevSubCategory =listOfFinCreditRevSubCategory.get(q);
						String subCategoryCode = finCreditRevSubCategory.getSubCategoryCode();
						if(itemTotCalMap.keySet().contains(subCategoryCode)){							
							String value = "--";

							try{
								if(StringUtils.isBlank(itemTotCalMap.get(subCategoryCode)) ){
									value = String.valueOf(0);
								}else{
									value = engine.eval(replaceYear(itemTotCalMap.get(subCategoryCode),years,noOfYearsToShow)).toString();
									if("NaN".equals(value) || value.contains("Infinity")){
										value ="--";
									}
								}	
							} catch (Exception e) {
								value ="--";
								logger.error("Exception: ", e);
							}
							engine.put("Y"+(years)+subCategoryCode,!value.contains("--")?new BigDecimal(value):BigDecimal.ZERO);
							dataMap.put("Y"+(years)+"_"+subCategoryCode,String.valueOf(!value.contains("--")?new BigDecimal(value):BigDecimal.ZERO));
						}
					}
				}

				//break down
				//entries
				for(int l=noOfYears;l>=1;l--){
					int years = noOfYears-l;
					for(int p=0;p<listOfFinCreditRevSubCategoryRatio.size();p++){
						FinCreditRevSubCategory finCreditRevSubCategory = null;
						finCreditRevSubCategory=listOfFinCreditRevSubCategory.get(p);
						String subCategoryCode = finCreditRevSubCategory.getSubCategoryCode();
						if(itemRuleMap.keySet().contains(subCategoryCode)){
							String value ="--";
							try{
								if(StringUtils.isNotEmpty(itemRuleMap.get(subCategoryCode))){
									value = engine.eval(replaceYear(itemRuleMap.get(subCategoryCode),years,noOfYearsToShow)).toString();
									if("NaN".equals(value) || value.contains("Infinity")){
										value ="--";
									}
								} 
							}catch (Exception e) {
								logger.error("Exception: ", e);
								value ="--";
							}
							if(this.ratioFlag ){
								engine.put("Y"+(years)+subCategoryCode,!value.contains("--")?new BigDecimal(value):BigDecimal.ZERO);
								dataMap.put("Y" + (noOfYears - l) + "_"
										+ subCategoryCode, String
										.valueOf(!value.contains("--") ? new BigDecimal(value) : BigDecimal.ZERO));
							}else if(required){
								dataMap.put("RY" + (noOfYears - l) + "_"
										+ subCategoryCode, String
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
							String subCategoryCode = finCreditRevSubCategory.getSubCategoryCode();
							try{
								if(dataMap.get("Y"+(years-1)+"_"+subCategoryCode) != null){
									ratioCalValPrev =new BigDecimal(dataMap.get("Y"+(years-1)+"_"+subCategoryCode));
								} else {
									ratioCalValPrev = BigDecimal.ZERO;
								}
								if(dataMap.get("Y"+(years)+"_"+subCategoryCode) != null){
									ratioCalValCurr =new BigDecimal(dataMap.get("Y"+(years)+"_"+subCategoryCode));
								} else {
									ratioCalValCurr = BigDecimal.ZERO;
								}
								engine.put("ratioCalValPrev", ratioCalValPrev);
								engine.put("ratioCalValCurr", ratioCalValCurr);
								value = engine.eval("((ratioCalValCurr-ratioCalValPrev)/ratioCalValPrev)*100").toString();
								if("NaN".equals(value) || value.contains("Infinity")){
									value = "--";
								}
							}catch(Exception e){
								value = "--";
								logger.error("Exception: ", e);
							}
							if(required){
								dataMap.put("CY"+(years)+"_"+subCategoryCode,String
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
	
	public String replaceYear(String formula,int year, int noOfYearsToShow){

		String formatedFormula= formula;
		for(int i= 0;i< noOfYearsToShow;i++){
			if(i==0){
				formatedFormula = formatedFormula.replace("YN.","Y"+year);
			}else{
				formatedFormula = formatedFormula.replace("YN-"+i+".","Y"+(year == 0 ? "M" : year-i));
			}
		}
		return formatedFormula;
	}
	
	
	/**
	 * Checking sub-category code for Customer and Co-Applicants list 
	 * @param finCreditReviewSummary
	 * @param coAppDataList
	 * @return finCreditReviewSummary
	 */
	private FinCreditReviewSummary isFinCredirtExsist(FinCreditReviewSummary finCreditReviewSummary,
			List<FinCreditReviewSummary> coAppDataList) {
		for (FinCreditReviewSummary finCreditReviewSummary2 : coAppDataList) {
			if (finCreditReviewSummary2.getSubCategoryCode().equals(finCreditReviewSummary.getSubCategoryCode())) {
				return finCreditReviewSummary2;
			}
		}
		return null;
	}

	public void setScriptEngine(ScriptEngine scriptEngine) {
		this.scriptEngine = scriptEngine;
	}

}

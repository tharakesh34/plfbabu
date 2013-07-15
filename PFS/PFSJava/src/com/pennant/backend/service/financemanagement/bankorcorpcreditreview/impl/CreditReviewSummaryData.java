package com.pennant.backend.service.financemanagement.bankorcorpcreditreview.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;

public class CreditReviewSummaryData {

	private static Logger logger= Logger.getLogger(CreditReviewSummaryData.class);

	private int noOfYears = Integer.parseInt(SystemParameterDetails.getSystemParameterValue("NO_OF_YEARS_TOSHOW").toString());;
	private Map<String,List<FinCreditReviewSummary>> detailsMap = new HashMap<String,List<FinCreditReviewSummary>> ();
	private Map<String,String> dataMap = null;
	private Map<String,String> itemTotCalMap = null;
	private Map<String,String> itemRuleMap = new HashMap<String,String> ();
	private boolean ratioFlag= true;
	private transient CreditApplicationReviewService creditApplicationReviewService;


	private List<FinCreditRevCategory> listOfFinCreditRevCategory = null;
	/**
	 * This method for setting the data or storing the all the data in map.<BR>
	 * If we call this we will get all the data in map.<BR>
	 * @param custID
	 * @param noOfYears
	 * @param year
	 * @return Map<String,String>
	 */
	public Map<String,String>  setDataMap(long custID,int year,String custCtgType,boolean required) {
		logger.debug("Entering");

		// create a script engine manager
		ScriptEngineManager factory = new ScriptEngineManager();

		// create a JavaScript engine
		ScriptEngine engine = factory.getEngineByName("JavaScript");

		List<FinCreditRevSubCategory>  listOfFinCreditRevSubCategoryRatio = null;
		dataMap = new HashMap<String,String> ();
		itemTotCalMap = new HashMap<String,String> ();
		Map<String ,List<FinCreditReviewSummary>> detailedMap = this.creditApplicationReviewService.
		getListCreditReviewSummaryByCustId(custID, noOfYears,year);
		Set<String> set = detailedMap.keySet();

		//put the values in the map
		for(String key : set){
			this.detailsMap.put(key, detailedMap.get(key));
		}
		ratioFlag= true;
		listOfFinCreditRevCategory = this.creditApplicationReviewService.getCreditRevCategoryByCreditRevCode(custCtgType);
		if(detailsMap.size() > 0){
			for(FinCreditRevCategory fcrcy:listOfFinCreditRevCategory){
				if(fcrcy.getRemarks().equals("R")){
					this.ratioFlag = false;
				}
				listOfFinCreditRevSubCategoryRatio = this.creditApplicationReviewService.
				getFinCreditRevSubCategoryByCategoryId(fcrcy.getCategoryId());
				List<FinCreditRevSubCategory>  listOfFinCreditRevSubCategory = this.creditApplicationReviewService.
				getFinCreditRevSubCategoryByCategoryIdAndCalcSeq(fcrcy.getCategoryId());


				for(int i =0 ;i<listOfFinCreditRevSubCategory.size();i++){

					FinCreditRevSubCategory finCreditRevSubCategory = null;
					finCreditRevSubCategory=listOfFinCreditRevSubCategory.get(i);

					if(finCreditRevSubCategory.getSubCategoryItemType().equals("Calc")){
						itemTotCalMap.put(finCreditRevSubCategory.getSubCategoryCode(), finCreditRevSubCategory.getItemsToCal());
					}
					itemRuleMap.put(finCreditRevSubCategory.getSubCategoryCode(), finCreditRevSubCategory.getItemRule());

				}

				// entries
				for(int j=noOfYears;j>=1;j--){	

					String auditYear =String.valueOf(year-j);
					List<FinCreditReviewSummary> listOfCreditReviewSummary = this.detailsMap.get(auditYear);
					if(listOfCreditReviewSummary.size()>0){
						for(int k=0;k<listOfCreditReviewSummary.size();k++){

							FinCreditReviewSummary creditReviewSummary = listOfCreditReviewSummary.get(k);
							engine.put("EXCHANGE", creditReviewSummary.getLovDescConversionRate());
							engine.put("NoOfShares", creditReviewSummary.getLovDescNoOfShares());
							engine.put("MarketPrice", creditReviewSummary.getLovDescMarketPrice());
							String value = "--";									
							if(!itemTotCalMap.keySet().contains(creditReviewSummary.getSubCategoryCode())){
								try{
									value = creditReviewSummary.getItemValue().toString();

								} catch (Exception e) {
									value ="--";
									logger.error(e);
								}
								engine.put("Y"+(noOfYears-j)+creditReviewSummary.getSubCategoryCode(),!value.equals("--")?new BigDecimal(value).setScale(2, RoundingMode.HALF_DOWN):null);
								dataMap.put("Y"+(noOfYears-j)+"_"+creditReviewSummary.getSubCategoryCode(),String.valueOf(!value.equals("--")?new BigDecimal(value).setScale(2, RoundingMode.HALF_DOWN):"--"));
							}	
						}
					}else{
						for(int r=0;r<listOfFinCreditRevSubCategoryRatio.size();r++){
							FinCreditRevSubCategory finCreditRevSubCategory =listOfFinCreditRevSubCategory.get(r);
							if(!itemTotCalMap.keySet().contains(finCreditRevSubCategory.getSubCategoryCode())){
								engine.put("Y"+(noOfYears-j)+finCreditRevSubCategory.getSubCategoryCode(),"--");
								dataMap.put("Y"+(noOfYears-j)+"_"+finCreditRevSubCategory.getSubCategoryCode(),"--");
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
								if(StringUtils.trimToEmpty(itemTotCalMap.get(finCreditRevSubCategory.getSubCategoryCode())).equals("") ){
									value = String.valueOf(0);

								}else if((engine.eval(replaceYear(itemTotCalMap.get(finCreditRevSubCategory.getSubCategoryCode()),(noOfYears-m))).toString().equals("NaN")) ||
										(engine.eval(replaceYear(itemTotCalMap.get(finCreditRevSubCategory.getSubCategoryCode()),(noOfYears-m))).toString().equals("Infinity"))){
									value ="--";
								}else{
									value = engine.eval(replaceYear(itemTotCalMap.get(finCreditRevSubCategory.getSubCategoryCode()),(noOfYears-m))).toString();
								}
							} catch (Exception e) {
								value ="--";
								logger.error(e);
							}
							engine.put("Y"+(noOfYears-m)+finCreditRevSubCategory.getSubCategoryCode(),!value.contains("--")?new BigDecimal(value).setScale(2, RoundingMode.HALF_DOWN):value);
							dataMap.put("Y"+(noOfYears-m)+"_"+finCreditRevSubCategory.getSubCategoryCode(),String.valueOf(!value.contains("--")?new BigDecimal(value).setScale(2, RoundingMode.HALF_DOWN):"--"));
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
								if(!itemRuleMap.get(finCreditRevSubCategory.getSubCategoryCode()).equals("")){
									if((engine.eval(replaceYear(itemRuleMap.get(finCreditRevSubCategory.getSubCategoryCode()),(noOfYears-l))).toString().equals("NaN")) ||
											(engine.eval(replaceYear(itemRuleMap.get(finCreditRevSubCategory.getSubCategoryCode()),(noOfYears-l))).toString().equals("Infinity"))){
										value ="--";
									}else{
										value = engine.eval(replaceYear(itemRuleMap.get(finCreditRevSubCategory.getSubCategoryCode()),(noOfYears-l))).toString();
									}
								} 
							}catch (Exception e) {
								logger.error(e);
								value ="--";
							}
							if(!this.ratioFlag ){
								engine.put("Y"+(noOfYears-l)+finCreditRevSubCategory.getSubCategoryCode(),String
										.valueOf(!value.contains("--") ? new BigDecimal(value)
										.setScale(2, RoundingMode.HALF_DOWN) : "--"));
							}
							if (!this.ratioFlag) {
								dataMap.put("Y" + (noOfYears - l) + "_"
										+ finCreditRevSubCategory.getSubCategoryCode(), String
										.valueOf(!value.contains("--") ? new BigDecimal(value)
										.setScale(2, RoundingMode.HALF_DOWN) : "--"));
							} else if(required){

								dataMap.put("RY" + (noOfYears - l) + "_"
										+ finCreditRevSubCategory.getSubCategoryCode(), String
										.valueOf(!value.contains("--") ? new BigDecimal(value)
										.setScale(2, RoundingMode.HALF_DOWN) : "--"));
							}
						}
					}


					// % change calculation
					if(noOfYears != l ){
						BigDecimal ratioCalValPrev = null;
						BigDecimal ratioCalValCurr = null;
						//	BigDecimal subtotal = null;
						BigDecimal totCalvalue= null;
						//BigDecimal divtotal= null;
						for(int p=0;p<listOfFinCreditRevSubCategoryRatio.size();p++){
							FinCreditRevSubCategory finCreditRevSubCategory=listOfFinCreditRevSubCategory.get(p);
							/*	if(itemRuleMap.keySet().contains(finCreditRevSubCategory.getSubCategoryCode()) && 
									!itemRuleMap.get(finCreditRevSubCategory.getSubCategoryCode()).equals("")){*/
							try{
								ScriptEngine engine1 = factory.getEngineByName("JavaScript");
								ratioCalValPrev =new BigDecimal(dataMap.get("Y"+(noOfYears-l-1)+"_"+finCreditRevSubCategory.getSubCategoryCode()));
								ratioCalValCurr =new BigDecimal(dataMap.get("Y"+(noOfYears-l)+"_"+finCreditRevSubCategory.getSubCategoryCode()));
								engine1.put("ratioCalValPrev", ratioCalValPrev);
								engine1.put("ratioCalValCurr", ratioCalValCurr);
								try {
									totCalvalue = new BigDecimal(engine1.eval("((ratioCalValCurr-ratioCalValPrev)/ratioCalValPrev)*100").toString());
								} catch (Exception e) {
									totCalvalue = null;
								}
								/*subtotal=ratioCalValCurr.subtract(ratioCalValPrev);
									divtotal = BigDecimal.ZERO;
									totCalvalue = BigDecimal.ZERO;
									if (ratioCalValPrev != null &&  ratioCalValPrev != BigDecimal.ZERO && ratioCalValPrev.compareTo(BigDecimal.ZERO)!=0) {
										divtotal = (subtotal).divide(ratioCalValPrev,RoundingMode.HALF_DOWN);
										totCalvalue =divtotal.multiply(new BigDecimal(100));
									}else{
										totCalvalue = null;
									}*/

							}catch(Exception aex){
								logger.error(aex);
								totCalvalue = null;
							}
							if(required){
								dataMap.put("CY"+(noOfYears-l)+"_"+finCreditRevSubCategory.getSubCategoryCode(),totCalvalue!=null?String.valueOf(totCalvalue.setScale(2, RoundingMode.HALF_DOWN)):"--");
							}
						}
					}
					//}
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
		String formatedFormula= formula;
		for(int i= 0;i<this.noOfYears;i++){
			if(i==0){
				formatedFormula = formatedFormula.replace("YN.","Y"+year);
			}else{
				formatedFormula = formatedFormula.replace("YN-"+i+".","Y"+(year-i));
			}
		}
		return formatedFormula;
	}
}

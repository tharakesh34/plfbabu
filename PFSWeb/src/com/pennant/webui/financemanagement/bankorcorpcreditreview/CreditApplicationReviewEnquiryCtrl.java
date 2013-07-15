package com.pennant.webui.financemanagement.bankorcorpcreditreview;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.impl.CreditReviewSummaryData;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

public class CreditApplicationReviewEnquiryCtrl extends GFCBaseListCtrl<FinanceMain> {

	private static final long serialVersionUID = 966281186831332116L;
	private final static Logger logger = Logger.getLogger(CreditApplicationReviewEnquiryCtrl.class);

	protected Window 			window_CreditApplicationReviewDialog;
	protected Borderlayout		borderlayout_CreditApplicationReview;
	protected Grid 				creditApplicationReviewGrid;
	protected Longbox 		 	custID; 						   	
	protected Textbox 			auditedYear;
	protected Intbox  			toYear;
	protected Textbox 	 		custCIF;							
	//protected Label 			custShrtName;						
	protected Groupbox 			gb_CreditReviwDetails;
	protected Tabbox 			tabBoxIndexCenter;
	protected Tabs 				tabsIndexCenter;
	protected Tabpanels 		tabpanelsBoxIndexCenter;
	protected Button 			btnSearch;


	protected Button btnSearchPRCustid; 			
	private JdbcSearchObject<Customer> newSearchObject ;
	private transient CreditApplicationReviewService creditApplicationReviewService;
	private transient CreditReviewSummaryData creditReviewSummaryData;

	private List<FinCreditRevCategory> listOfFinCreditRevCategory = null;
	private int noOfYears = Integer.parseInt(SystemParameterDetails.getSystemParameterValue("NO_OF_YEARS_TOSHOW").toString());;
	private int currFormatter;
	private Map<String,String> dataMap = null;

	//Commented for Creating new Class for SetDataMap method
	//private Map<String,List<FinCreditReviewSummary>> detailsMap = new HashMap<String,List<FinCreditReviewSummary>> ();
	//private Map<String,String> itemTotCalMap = null;
	//private Map<String,String> itemRuleMap = new HashMap<String,String> ();
	//private String bankName = "";

	private int year;
	private boolean ratioFlag= true;
	private String custCtgCode = null;
	// create a script engine manager
	ScriptEngineManager factory = new ScriptEngineManager();

	// create a JavaScript engine
	ScriptEngine engine = factory.getEngineByName("JavaScript");
	/**
	 * default constructor.<br>
	 */
	public CreditApplicationReviewEnquiryCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Rule object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CreditApplicationReviewDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		try{
			/*Calendar calender = Calendar.getInstance();
			DateUtility.getYear(calender.getTime());
			this.year =DateUtility.getYear(calender.getTime());		*/
			currFormatter = this.creditApplicationReviewService.getCurrencyById(SystemParameterDetails.getSystemParameterObject("APP_DFT_CURR").getSysParmValue()).getCcyEditField();
			setDialog(this.window_CreditApplicationReviewDialog);		
		}catch(Exception e){
			this.window_CreditApplicationReviewDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}


	/**
	 * This method for setting the list of the tabs.<br>
	 * @throws Exception
	 */
	public void setTabs() throws Exception{
		logger.debug("Entering");

		this.dataMap = this.creditReviewSummaryData.setDataMap(this.custID.getValue(),this.toYear.getValue(),this.custCtgCode,true);

		for(FinCreditRevCategory fcrc:listOfFinCreditRevCategory){
			if(fcrc.getRemarks().equals("R")){
				this.ratioFlag = false;
			}else{
				this.ratioFlag = true;
			}
			Tab tab = new Tab();
			tab.setId("tab_"+fcrc.getCategoryId());
			tab.setLabel(fcrc.getCategoryDesc());
			tab.setParent(this.tabsIndexCenter);
			Tabpanel tabPanel = new Tabpanel();	
			tabPanel.setId("tabPanel_"+fcrc.getCategoryId());
			tabPanel.setParent(this.tabpanelsBoxIndexCenter);
			render(fcrc.getCategoryId(),setListToTab("tabPanel_"+fcrc.getCategoryId(),tabPanel,fcrc));
		}
		logger.debug("Leaving");
	}

	/**
	 * This method for setting the data or storing the all the data in map.<BR>
	 * If we call this we will get all the data in map.<BR>
	 * @param custID
	 * @param noOfYears
	 * @param year
	 * @return Map<String,String>
	 *//*
	public Map<String,String>  setDataMap(long custID,int noOfYears,int year,String custCtgType) {
		logger.debug("Entering");
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
		if(listOfFinCreditRevCategory == null && custCtgType != null ){
			listOfFinCreditRevCategory = this.creditApplicationReviewService.getCreditRevCategoryByCreditRevCode(custCtgType);
		}
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

					for(int k=0;k<listOfCreditReviewSummary.size();k++){

						FinCreditReviewSummary creditReviewSummary = listOfCreditReviewSummary.get(k);
						engine.put("EXCHANGE", creditReviewSummary.getLovDescConversionRate());
						engine.put("NoOfShares", creditReviewSummary.getLovDescNoOfShares());
						engine.put("MarketPrice", creditReviewSummary.getLovDescMarketPrice());
						String value = "--";									
						if(!itemTotCalMap.keySet().contains(creditReviewSummary.getSubCategoryCode())){
							try{
								value = PennantAppUtil.formateAmount(creditReviewSummary.getItemValue(), this.currFormatter).toString();

							} catch (Exception e) {
								value ="--";
								logger.error(e);
							}
							engine.put("Y"+(noOfYears-j)+creditReviewSummary.getSubCategoryCode(),!value.equals("--")?new BigDecimal(value).setScale(2, RoundingMode.HALF_DOWN):value);
							dataMap.put("Y"+(noOfYears-j)+"."+creditReviewSummary.getSubCategoryCode(),String.valueOf(!value.equals("--")?new BigDecimal(value).setScale(2, RoundingMode.HALF_DOWN):"--"));
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
							engine.put("Y"+(noOfYears-m)+finCreditRevSubCategory.getSubCategoryCode(),!value.equals("--")?new BigDecimal(value).setScale(2, RoundingMode.HALF_DOWN):value);
							dataMap.put("Y"+(noOfYears-m)+"."+finCreditRevSubCategory.getSubCategoryCode(),String.valueOf(!value.equals("--")?new BigDecimal(value).setScale(2, RoundingMode.HALF_DOWN):"--"));
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
								engine.put("Y"+(noOfYears-l)+finCreditRevSubCategory.getSubCategoryCode(),value);
							}
							if(!this.ratioFlag ){
								dataMap.put("Y"+(noOfYears-l)+"."+finCreditRevSubCategory.getSubCategoryCode(),
										String.valueOf(!value.equals("--")?new BigDecimal(value).setScale(2, RoundingMode.HALF_DOWN):"--"));
							}else{
								dataMap.put("RY"+(noOfYears-l)+"."+finCreditRevSubCategory.getSubCategoryCode(),
										String.valueOf(!value.equals("--")?new BigDecimal(value).setScale(2, RoundingMode.HALF_DOWN):"--"));
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
							if(itemRuleMap.keySet().contains(finCreditRevSubCategory.getSubCategoryCode()) && 
									!itemRuleMap.get(finCreditRevSubCategory.getSubCategoryCode()).equals("")){
								try{
									ScriptEngine engine1 = factory.getEngineByName("JavaScript");
										ratioCalValPrev =new BigDecimal(dataMap.get("Y"+(noOfYears-l-1)+"."+finCreditRevSubCategory.getSubCategoryCode()));
										ratioCalValCurr =new BigDecimal(dataMap.get("Y"+(noOfYears-l)+"."+finCreditRevSubCategory.getSubCategoryCode()));
									engine1.put("ratioCalValPrev", ratioCalValPrev);
									engine1.put("ratioCalValCurr", ratioCalValCurr);
									try {
										totCalvalue = new BigDecimal(engine1.eval("((ratioCalValCurr-ratioCalValPrev)/ratioCalValPrev)*100").toString());
									} catch (Exception e) {
										totCalvalue = null;
									}
									subtotal=ratioCalValCurr.subtract(ratioCalValPrev);
									divtotal = BigDecimal.ZERO;
									totCalvalue = BigDecimal.ZERO;
									if (ratioCalValPrev != null &&  ratioCalValPrev != BigDecimal.ZERO && ratioCalValPrev.compareTo(BigDecimal.ZERO)!=0) {
										divtotal = (subtotal).divide(ratioCalValPrev,RoundingMode.HALF_DOWN);
										totCalvalue =divtotal.multiply(new BigDecimal(100));
									}else{
										totCalvalue = null;
									}

								}catch(Exception aex){
									logger.error(aex);
									totCalvalue = null;
								}
								dataMap.put("CY"+(noOfYears-l)+"."+finCreditRevSubCategory.getSubCategoryCode(),totCalvalue!=null?String.valueOf(totCalvalue.setScale(2, RoundingMode.HALF_DOWN)):"--");
							}
						}
					//}
				}
			}
		}
		logger.debug("Leaving");
		return dataMap;
}*/



	/**
	 * This Method for rendering with data
	 * @param categoryId
	 * @param listbox
	 * @throws Exception
	 */
	public void render(long categoryId,Listbox listbox) throws Exception {
		logger.debug("Entering");
		Listitem item = null;
		Listcell lc = null;
		Listgroup lg = null;
		String mainCategory = "";
		List<FinCreditRevSubCategory>  listOfFinCreditRevSubCategory= this.creditApplicationReviewService.
		getFinCreditRevSubCategoryByCategoryId(categoryId);
		for(int i =0 ;i<listOfFinCreditRevSubCategory.size();i++){
			FinCreditRevSubCategory finCreditRevSubCategory =listOfFinCreditRevSubCategory.get(i);
			item = new Listitem();
			item.setStyle("background: none repeat scroll 0 0 #FFFFFF;");

			item.setId(String.valueOf("li"+finCreditRevSubCategory.getSubCategoryCode()));

			if(!this.ratioFlag && !mainCategory.equals(finCreditRevSubCategory.getMainSubCategoryCode())){
				mainCategory = finCreditRevSubCategory.getMainSubCategoryCode();
				lg =  new Listgroup();
				lg.setId(mainCategory);
				if(!listbox.hasFellow(mainCategory)){
					lg.setLabel(mainCategory);
					lg.setOpen(true);
					lg.setParent(listbox);
					lg.setStyle("font-weight:bold;font-weight:bold;background-color: #ADD8E6;");
					//TODO COLOR
				}
			}

			lc = new Listcell();
			lc.setStyle("border: 1px inset snow;");
			Label label1  = new Label();
			if(finCreditRevSubCategory.getSubCategoryItemType().equals("Calc") && this.ratioFlag){
				label1.setStyle("font-weight:bold; color:#000000;");
			}else{
				label1.setStyle("font-weight:bold;");
			}

			label1.setValue(String.valueOf(finCreditRevSubCategory.getSubCategoryDesc()));
			label1.setParent(lc);
			lc.setParent(item);

			for(int j=noOfYears;j>=1;j--){				
				lc = new Listcell();
				lc.setStyle("text-align:right;border: 1px inset snow;");
				lc.setId("lcdb"+finCreditRevSubCategory.getSubCategoryCode()+String.valueOf(year-j));
				Label valueLabel= new Label();
				if(finCreditRevSubCategory.getSubCategoryItemType().equals("Calc") && this.ratioFlag){
					valueLabel.setStyle("font-weight:bold; color:#000000;");
					if(finCreditRevSubCategory.isGrand()){
						item.setStyle("background-color: #CCFF99;");
					} else{
						item.setStyle("background-color: #ADD8E6;");
					}
					//TODO COLOR
				}
				valueLabel.setId("db"+finCreditRevSubCategory.getSubCategoryCode()+String.valueOf(year-j));
				String value =this.dataMap.get("Y"+(noOfYears-j)+"_"+finCreditRevSubCategory.getSubCategoryCode());

				valueLabel.setValue((value!=null && !value.equals("--") && finCreditRevSubCategory.isFormat()) ?
						PennantAppUtil.amountFormate(new BigDecimal(value),this.currFormatter):value);

				valueLabel.setParent(lc);
				lc.setParent(item);

				lc = new Listcell();
				lc.setStyle("text-align:right;border: 1px inset snow;");
				lc.setId("lcra"+finCreditRevSubCategory.getSubCategoryCode()+String.valueOf(year-j));
				Label rLabel = new Label(); 
				if(finCreditRevSubCategory.getSubCategoryItemType().equals("Calc") && this.ratioFlag){
					rLabel.setStyle("font-weight:bold; color:#000000;");
				}
				rLabel.setId("rLabel"+finCreditRevSubCategory.getSubCategoryCode()+String.valueOf(year-j));
				if(this.ratioFlag){
					rLabel.setValue(this.dataMap.get("RY"+(noOfYears-j)+"_"+finCreditRevSubCategory.getSubCategoryCode()));
				}else{
					rLabel.setValue("0");
				}
				rLabel.setParent(lc);
				lc.setParent(item);

				if(j != noOfYears){
					lc = new Listcell();
					lc.setStyle("text-align:right;border: 1px inset snow;");
					lc.setId("lcdiff"+finCreditRevSubCategory.getSubCategoryCode()+String.valueOf(year-j));
					Label diffLabel = new Label(); 
					if(finCreditRevSubCategory.getSubCategoryItemType().equals("Calc") && this.ratioFlag){
						diffLabel.setStyle("font-weight:bold;color:#000000;");
					}
					diffLabel.setId("diffLabel"+finCreditRevSubCategory.getSubCategoryCode()+String.valueOf(year-j));
					diffLabel.setValue(!this.dataMap.get("CY"+(noOfYears-j)+"_"+finCreditRevSubCategory.getSubCategoryCode()).contains("--")?this.dataMap.get("CY"+(noOfYears-j)+"_"
							+finCreditRevSubCategory.getSubCategoryCode()).toString()+"%"
							:this.dataMap.get("CY"+(noOfYears-j)+"_"+finCreditRevSubCategory.getSubCategoryCode()));
					diffLabel.setParent(lc);
					lc.setParent(item);
				}

			}
			item.setAttribute("finData", finCreditRevSubCategory);
			item.setParent(listbox);
		}
		listbox.setAttribute("ratio", ratioFlag);
		//	setData(listbox);
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		PTMessageUtils.showHelpWindow(event, window_CreditApplicationReviewDialog);
		logger.debug("Leaving " + event.toString());
	}


	/**
	 * This method for building the listbox with dynamic headers.<br>
	 * 
	 */	
	public Listbox setListToTab(String tabId,Tabpanel tabPanel,FinCreditRevCategory fcrc){
		logger.debug("Entering");
		Div div = new Div();
		div.setId("div_"+fcrc.getCategoryId());
		div.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px"))) - 100 - 40-20 + "px");
		Listbox listbox = new Listbox();
		listbox.setVflex(true);
		listbox.setSpan(true);
		listbox.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px"))) - 100 - 40-20 + "px");
		listbox.setId("lb_"+fcrc.getCategoryId());
		Listhead listHead = new Listhead();
		//listHead.setStyle("background:#447294;color:white;");
		//TODO COLOR
		listHead.setId("listHead_"+fcrc.getCategoryId());
		Listheader listheader_bankName = new Listheader();
		listheader_bankName.setLabel(Labels.getLabel("listheader_bankName.value",new String[]{"Albaraka"}));
		listheader_bankName.setHflex("min");
		//listheader_bankName.setStyle("color:white;");
		listheader_bankName.setParent(listHead);

		for(int j=noOfYears;j>=1;j--){
			Listheader listheader_audAmt = new Listheader();
			listheader_audAmt.setLabel(Labels.getLabel("listheader_audAmt.value",new String[]{String.valueOf(year-j)}));
			listheader_audAmt.setHflex("min");
			//listheader_audAmt.setStyle("color:white;");
			listheader_audAmt.setParent(listHead);
			Listheader listheader_breakDown= new Listheader();
			listheader_breakDown.setLabel(Labels.getLabel("listheader_breakDown.value",new String[]{String.valueOf(year-j)}));
			listheader_breakDown.setHflex("min");
			//listheader_breakDown.setStyle("color:white;");
			listheader_breakDown.setVisible(fcrc.isBrkdowndsply());
			listheader_breakDown.setParent(listHead);
			if(j!= noOfYears){
				Listheader listheader_diff= new Listheader();
				listheader_diff.setLabel(Labels.getLabel("listheader_diff.value",new String[]{String.valueOf(year-j-1)+String.valueOf("/"+(year-j))}));
				listheader_diff.setHflex("min");
				//listheader_diff.setStyle("color:white;");
				listheader_diff.setVisible(fcrc.isChangedsply());
				listheader_diff.setParent(listHead);
			}
		}

		listHead.setParent(listbox);
		listbox.setParent(div);
		div.setParent(tabPanel);
		logger.debug("Leaving");
		return listbox;

	}
	/**
	 * Method for Calling list Of existed Customers
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchPRCustid(Event event) throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering" + event.toString());
		onload();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To load the customerSelect filter dialog
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void onload() throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();	

		List<Filter> filtersList=new ArrayList<Filter>();
		Filter filter=new Filter("lovDescCustCtgType", "C", Filter.OP_EQUAL);
		filtersList.add(filter);

		map.put("DialogCtrl", this);
		map.put("filtertype","Extended");
		map.put("filtersList", filtersList);
		map.put("searchObject",this.newSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul",null,map);
		logger.debug("Leaving");
	}

	/**
	 * To set the customer id from Customer filter
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer,JdbcSearchObject<Customer> newSearchObject) throws InterruptedException{
		logger.debug("Entering"); 
		final Customer aCustomer = (Customer)nCustomer; 		
		this.custID.setValue(aCustomer.getCustID());
		this.custCIF.setValue(aCustomer.getCustCIF().trim() +"-"+aCustomer.getCustShrtName());
		this.custCIF.setTooltiptext(aCustomer.getCustCIF().trim() +"-"+aCustomer.getCustShrtName());
		//this.custShrtName.setValue(aCustomer.getCustShrtName());
		this.newSearchObject = newSearchObject;
		this.custCtgCode = aCustomer.getLovDescCustCtgType();
		this.listOfFinCreditRevCategory = this.creditApplicationReviewService.getCreditRevCategoryByCreditRevCode(this.custCtgCode);
		logger.debug("Leaving");
	}

	public CreditApplicationReviewService getCreditApplicationReviewService() {
		return creditApplicationReviewService;
	}

	public void setCreditApplicationReviewService(
			CreditApplicationReviewService creditApplicationReviewService) {
		this.creditApplicationReviewService = creditApplicationReviewService;
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnClose(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		closeDialog(this.window_CreditApplicationReviewDialog, "FinCreditReviewSummary");
		final Borderlayout borderlayout = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");  
		final Tabbox tabbox = (Tabbox) borderlayout.getFellow("center").getFellow("divCenter").getFellow("tabBoxIndexCenter");
		tabbox.getSelectedTab().close();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * This method for selecting customer id from lov and after that setting sheet on bases of the customer type.<BR>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		ratioFlag= true;
		if(this.tabpanelsBoxIndexCenter.getChildren().size()>0){
			this.tabpanelsBoxIndexCenter.getChildren().clear();
		}
		if(this.tabsIndexCenter.getChildren().size()>0){
			this.tabsIndexCenter.getChildren().clear();
		}		
		doClearMessage();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try{
			if (this.toYear.getValue()==null){
				throw new WrongValueException(this.toYear, Labels.getLabel("label_NoOfYearsToDisplay_Must.value"));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}	
		try{
			if (this.custID.getValue()==null){
				throw new WrongValueException(this.custCIF, Labels.getLabel("label_CustID_Must.value"));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}	

		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			logger.debug("Leaving");
			throw new WrongValuesException(wvea);
		}
		if(wve.size() == 0){
			this.year = this.toYear.getValue();
			setTabs();
		}
		logger.debug("Leaving" + event.toString());
	}


	private void doClearMessage() {
		logger.debug("Entering");
		this.custCIF.clearErrorMessage();
		this.toYear.clearErrorMessage();
		logger.debug("Leaving");
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

	public void setCreditReviewSummaryData(CreditReviewSummaryData creditReviewSummaryData) {
		this.creditReviewSummaryData = creditReviewSummaryData;
	}

	public CreditReviewSummaryData getCreditReviewSummaryData() {
		return creditReviewSummaryData;
	}
}
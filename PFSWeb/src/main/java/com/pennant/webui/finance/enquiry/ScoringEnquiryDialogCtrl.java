package com.pennant.webui.finance.enquiry;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.DeviationConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class ScoringEnquiryDialogCtrl extends GFCBaseCtrl<FinanceScoreDetail> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(ScoringEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window        window_ScoringEnquiryDialog;               
	protected Listbox       listBoxFinancialScoRef;                    
	protected Listbox       listBoxNonFinancialScoRef;                 

	protected Borderlayout   borderlayoutScoringEnquiry;		       
	protected Tab 			 finScoreMetricTab;
	protected Tab 			 nonFinScoreMetricTab;
	protected Grid			 grid_FinScoreDetail;
	
	protected Decimalbox 	maxFinTotScore;							
	protected Decimalbox 	maxNonFinTotScore;						
	protected Intbox 		minScore;								
	protected Decimalbox 	calTotScore;							
	protected Checkbox 		isOverride;								
	protected Intbox 		overrideScore;							
	protected Row 			row_finScoreOverride;					
	protected Label 		label_ScoreSummary;					
	protected Label 		label_ScoreSummaryVal;					
	protected Label 		label_DeviationValue;					

	private Tabpanel 		 tabPanel_dialogWindow;
	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private List<FinanceScoreHeader> finScoreHeaderList = null;
	private HashMap<Long, List<FinanceScoreDetail>> scoreDetailListMap = new HashMap<Long, List<FinanceScoreDetail>>();
	private String custCtgType = "";


	/**
	 * default constructor.<br>
	 */
	public ScoringEnquiryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_ScoringEnquiryDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ScoringEnquiryDialog);

		if(event.getTarget().getParent().getParent() != null){
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}

		if (arguments.containsKey("scoringList")) {
			List<Object> scoreObjectList= (List<Object>) arguments.get("scoringList");
			if(scoreObjectList != null){
				finScoreHeaderList = (List<FinanceScoreHeader>) scoreObjectList.get(0);
				scoreDetailListMap = (HashMap<Long, List<FinanceScoreDetail>>) scoreObjectList.get(1);
			}
		}

		if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
			this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments.get("financeEnquiryHeaderDialogCtrl");
		}
		if(arguments.containsKey("custTypeCtg")){
			this.custCtgType = (String) arguments.get("custTypeCtg");
		  }

		doShowDialog();

		logger.debug("Leaving " + event.toString());
	}


	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws InterruptedException
	 */
	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering");
		try {

			doFillExecutedScoreDetails();
			
			if(tabPanel_dialogWindow != null){
				
				this.window_ScoringEnquiryDialog.setBorder("none");
				this.window_ScoringEnquiryDialog.setTitle("");

				getBorderLayoutHeight();
				int headerRowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount()*20;
				int rowsHeight = 0;
				if(this.grid_FinScoreDetail.isVisible()){
					rowsHeight = headerRowsHeight + grid_FinScoreDetail.getRows().getVisibleItemCount()*20;
				}else{
					rowsHeight = headerRowsHeight;
				}
				this.listBoxFinancialScoRef.setHeight(this.borderLayoutHeight-rowsHeight-100+"px");
				this.listBoxNonFinancialScoRef.setHeight(this.borderLayoutHeight-rowsHeight-100+"px");

				this.window_ScoringEnquiryDialog.setHeight(this.borderLayoutHeight-headerRowsHeight+"px");
				tabPanel_dialogWindow.appendChild(this.window_ScoringEnquiryDialog);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Filling Scoring Details
	 */
	private void doFillExecutedScoreDetails(){
		logger.debug("Entering");
		
		this.finScoreMetricTab.setVisible(true);
		this.nonFinScoreMetricTab.setVisible(true);

		if(getFinScoreHeaderList() != null && getFinScoreHeaderList().size() > 0){

			if(StringUtils.equals(PennantConstants.PFF_CUSTCTG_INDIV, custCtgType)){

				this.finScoreMetricTab.setLabel("Retail Scoring Metric Details");
				this.nonFinScoreMetricTab.setVisible(false);
				this.grid_FinScoreDetail.setVisible(false);

				for (int i = 0; i < getFinScoreHeaderList().size(); i++) {
					FinanceScoreHeader header = getFinScoreHeaderList().get(i);
					addListGroup("", this.listBoxFinancialScoRef, PennantConstants.PFF_CUSTCTG_INDIV, header);
					BigDecimal totalGrpMaxScore = BigDecimal.ZERO;
					BigDecimal totalGrpExecScore = BigDecimal.ZERO;
					if(getScoreDetailListMap().containsKey(header.getHeaderId())){
						List<FinanceScoreDetail> scoreDetailList = getScoreDetailListMap().get(header.getHeaderId());
						for (FinanceScoreDetail retailScoreDetail : scoreDetailList) {
							addExecutedListItem(retailScoreDetail, this.listBoxFinancialScoRef);
							totalGrpMaxScore = totalGrpMaxScore.add(retailScoreDetail.getMaxScore());
							totalGrpExecScore = totalGrpExecScore.add(retailScoreDetail.getExecScore());
						}
					}
					addListFooter(totalGrpMaxScore,totalGrpExecScore, this.listBoxFinancialScoRef,"I",header.getCreditWorth());
				}

			}else if(StringUtils.equals(PennantConstants.PFF_CUSTCTG_CORP, custCtgType)){

				FinanceScoreHeader header = getFinScoreHeaderList().get(0);
				
				this.minScore.setValue(header.getMinScore());
				this.isOverride.setChecked(header.isOverride());
				this.isOverride.setDisabled(true);
				this.overrideScore.setValue(header.getOverrideScore());

				if(getScoreDetailListMap().containsKey(header.getHeaderId())){
					List<FinanceScoreDetail> scoreDetailList = getScoreDetailListMap().get(header.getHeaderId());

					long prvGrpId = 0;
					BigDecimal totalgrpExecScore = BigDecimal.ZERO;
					BigDecimal totalgrpMaxScore = BigDecimal.ZERO;
					BigDecimal finTotalScore = BigDecimal.ZERO;
					BigDecimal nonFinTotalScore = BigDecimal.ZERO;
					BigDecimal calTotalScore = BigDecimal.ZERO;

					for (int i = 0; i < scoreDetailList.size() ; i++) {
						FinanceScoreDetail curScoreDetail = scoreDetailList.get(i);

						//Adding List Group 
						if((prvGrpId == 0) || (prvGrpId != curScoreDetail.getSubGroupId())){
							totalgrpExecScore = BigDecimal.ZERO; 
							totalgrpMaxScore = BigDecimal.ZERO;
							if("F".equals(curScoreDetail.getCategoryType())){
								addListGroup(curScoreDetail.getSubGrpCodeDesc(), this.listBoxFinancialScoRef,
										curScoreDetail.getCategoryType(), null);
							}else if("N".equals(curScoreDetail.getCategoryType())){
								addListGroup(curScoreDetail.getSubGrpCodeDesc(), this.listBoxNonFinancialScoRef, 
										curScoreDetail.getCategoryType(), null);
							}
						}

						//Adding List Item
						if("F".equals(curScoreDetail.getCategoryType())){
							addExecutedListItem(curScoreDetail, this.listBoxFinancialScoRef);
							finTotalScore = finTotalScore.add(curScoreDetail.getMaxScore());
						}else if("N".equals(curScoreDetail.getCategoryType())){
							addExecutedListItem(curScoreDetail, this.listBoxNonFinancialScoRef);
							nonFinTotalScore = nonFinTotalScore.add(curScoreDetail.getMaxScore());
						}
						totalgrpExecScore = totalgrpExecScore.add(curScoreDetail.getExecScore());
						totalgrpMaxScore = totalgrpMaxScore.add(curScoreDetail.getMaxScore());
						calTotalScore = calTotalScore.add(curScoreDetail.getExecScore());


						//Adding List Group Footer
						if((i == scoreDetailList.size()-1) || 
								(curScoreDetail.getSubGroupId() != scoreDetailList.get(i+1).getSubGroupId())){
							if("F".equals(curScoreDetail.getCategoryType())){
								addListFooter(totalgrpMaxScore,totalgrpExecScore, this.listBoxFinancialScoRef,"F",header.getCreditWorth());
							}else if("N".equals(curScoreDetail.getCategoryType())){
								addListFooter(totalgrpMaxScore,totalgrpExecScore, this.listBoxNonFinancialScoRef,"N",header.getCreditWorth());
							}
						}
						prvGrpId = curScoreDetail.getSubGroupId();
					}
					
					this.maxFinTotScore.setValue(finTotalScore);
					this.maxNonFinTotScore.setValue(nonFinTotalScore);
					this.calTotScore.setValue(calTotalScore);


				}
			}
		}
		
		setScoreSummaryStyle(getFinScoreHeaderList(),getScoreDetailListMap());
		
		logger.debug("Leaving");
	}
	
	/**
	 * @param financeScoreHeaders
	 * @param scoreMapsco
	 * @return 
	 */
	private void setScoreSummaryStyle(List<FinanceScoreHeader>  financeScoreHeaders, HashMap<Long, List<FinanceScoreDetail>> scoreMap ) {
		boolean deviationSuff=false;
		String deviationVal="";
		
		if (financeScoreHeaders!=null && !financeScoreHeaders.isEmpty()) {
			List<FinanceDeviations> list = getFinanceDevaitions(financeScoreHeaders.get(0).getFinReference());
			
			for (FinanceScoreHeader financeScoreHeader : financeScoreHeaders) {
				
				FinanceDeviations dev = isDevaitionApproved(list,financeScoreHeader);
				if (dev!=null) {
					deviationVal=dev.getDeviationValue();
					deviationSuff=true;
				}
			
				if (deviationSuff) {
					break;
				}
			}
			
		}
		
		this.label_ScoreSummaryVal.setStyle("font-weight:bold;font-size:11px;color:green;");
		
		if (deviationSuff) {
			this.label_ScoreSummaryVal.setValue(Labels.getLabel("label_SuffScr_Deviation.label"));
			this.label_DeviationValue.setValue(Labels.getLabel("label_Deviation_Value",new String[]{deviationVal}));
			this.label_DeviationValue.setVisible(true);
		}else{
			this.label_ScoreSummaryVal.setValue(Labels.getLabel("label_SuffScr.label"));
		}
		
	}

	
	/**
	 * @param list
	 * @param financeScoreHeader
	 * @return
	 */
	private FinanceDeviations isDevaitionApproved(List<FinanceDeviations> list, FinanceScoreHeader financeScoreHeader) {
		logger.debug(" Entering ");
		if (list==null || list.isEmpty()) {
			return null;
		}
		for (FinanceDeviations financeDeviations : list) {
			if (financeDeviations.getDeviationCode().equals(String.valueOf(financeScoreHeader.getGroupId()))) {
				if (PennantConstants.RCD_STATUS_APPROVED.equals(financeDeviations.getApprovalStatus())) {
					return financeDeviations;
				}
			}
		}
		return null;
	}

	public List<FinanceDeviations> getFinanceDevaitions(String finReference){
		JdbcSearchObject<FinanceDeviations> jdbcSearchObject=new JdbcSearchObject<FinanceDeviations>(FinanceDeviations.class);
		jdbcSearchObject.addTabelName("FinanceDeviations");
		jdbcSearchObject.addFilterEqual("FinReference", finReference);
		jdbcSearchObject.addFilterEqual("Module", DeviationConstants.TY_SCORE);
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		return pagedListService.getBySearchObject(jdbcSearchObject);
	}
	
	/**
	 * Method for Adding List Group to listBox in Corporation
	 * @param scoringMetric
	 * @param listbox
	 */
	private void addListGroup(String groupCodeDesc, Listbox listbox, String ctgType, Object object){
		logger.debug("Entering");
		Listgroup listgroup = new Listgroup();

		if(PennantConstants.PFF_CUSTCTG_INDIV.equals(ctgType)){

			FinanceScoreHeader header = null;
			Label label = null;
			Space space = null;
			Listcell lc = null;
			
			if(object instanceof FinanceScoreHeader){
				header = (FinanceScoreHeader) object;

				lc = new Listcell(PennantJavaUtil.concat(header.getGroupCode(), " - ", header.getGroupCodeDesc()));
				lc.setParent(listgroup);

				lc = new Listcell();
				lc.setSpan(3);	
				label = new Label(PennantJavaUtil.concat(Labels.getLabel("label_MinScore"), " :", String.valueOf(header.getMinScore())));

				lc.appendChild(label);				
				space = new Space();
				space.setWidth("100px");				
				lc.appendChild(space);				
				

				lc.setParent(listgroup);
			}

			FinanceReferenceDetail detail = null;
			if (object instanceof FinanceReferenceDetail) {
				detail = (FinanceReferenceDetail) object;

				lc = new Listcell(PennantJavaUtil.concat(detail.getLovDescCodelov(), "-", detail.getLovDescNamelov()));
				lc.setParent(listgroup);

				lc = new Listcell();
				lc.setSpan(3);
				label = new Label(PennantJavaUtil.concat(Labels.getLabel("label_MinScore"), " :",
						String.valueOf(detail.getLovDescminScore())));
				lc.appendChild(label);
				space = new Space();
				space.setWidth("100px");
				lc.appendChild(space);

				lc.setParent(listgroup);
			}
		}else{
			listgroup.setLabel(groupCodeDesc);
		}

		listgroup.setOpen(true);
		listbox.appendChild(listgroup);
		logger.debug("Leaving");
	}


	/**
	 * Method for Filling Listbox Using Executed Score Details for Retail/Corporate
	 * @param scoreDetail
	 * @param listbox
	 */
	private void addExecutedListItem(FinanceScoreDetail scoreDetail, Listbox listbox){
		logger.debug("Entering");

		Listitem item = new Listitem();
		Listcell lc;
		lc = new Listcell(scoreDetail.getRuleCode());
		lc.setParent(item);
		lc = new Listcell(scoreDetail.getRuleCodeDesc());
		lc.setParent(item);
		lc = new Listcell(String.valueOf(scoreDetail.getMaxScore()));
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.setParent(item);
		lc = new Listcell(String.valueOf(scoreDetail.getExecScore()));
		lc.setStyle("text-align:right;font-weight:bold;color:#ff7300;");
		lc.setParent(item);
		listbox.appendChild(item);

		logger.debug("Leaving");
	}

	/**
	 * Method for Adding List Group to listBox in Corporation
	 * @param scoringMetric
	 * @param listbox
	 */
	private void addListFooter(BigDecimal totalMaxGrpScore, BigDecimal totalExecGrpScore, Listbox listbox, String ctgType, String creditWorth){
		logger.debug("Entering");

		Listgroupfoot listgroupfoot = new Listgroupfoot();

		Listcell cell = null;

		if("I".equals(ctgType)){
			cell = new Listcell("Credit-Worth");
			cell.setStyle("text-align:right;font-weight:normal;");
			listgroupfoot.appendChild(cell);

			cell = new Listcell();
			Label label = new Label(creditWorth);
			label.setStyle("float:left;font-weight:bold;");
			cell.appendChild(label);

			label = new Label("Group Grand Total");
			label.setStyle("float:right;");
			cell.appendChild(label);
			listgroupfoot.appendChild(cell);
		}else if("F".equals(ctgType) || "N".equals(ctgType)){
			cell = new Listcell("Sub Group Total");
			cell.setSpan(2);
			cell.setStyle("font-weight:bold;text-align:right;");
			listgroupfoot.appendChild(cell);
		}


		cell = new Listcell(String.valueOf(totalMaxGrpScore));
		cell.setStyle("font-weight:bold;text-align:right;");
		listgroupfoot.appendChild(cell);

		if("N".equals(ctgType)){
			cell = new Listcell("");
		}else{
			cell = new Listcell();
			Label label = new Label(String.valueOf(totalExecGrpScore));
			label.setStyle("font-weight:bold;float:right;");
			cell.appendChild(label);
		}
		listgroupfoot.appendChild(cell);

		listbox.appendChild(listgroupfoot);
		logger.debug("Leaving");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinScoreHeaderList(List<FinanceScoreHeader> finScoreHeaderList) {
		this.finScoreHeaderList = finScoreHeaderList;
	}
	public List<FinanceScoreHeader> getFinScoreHeaderList() {
		return finScoreHeaderList;
	}

	public void setScoreDetailListMap(HashMap<Long, List<FinanceScoreDetail>> scoreDetailListMap) {
		this.scoreDetailListMap = scoreDetailListMap;
	}
	public HashMap<Long, List<FinanceScoreDetail>> getScoreDetailListMap() {
		return scoreDetailListMap;
	}
	
}
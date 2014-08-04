package com.pennant.webui.finance.enquiry;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
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

import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

public class ScoringEnquiryDialogCtrl extends GFCBaseListCtrl<FinanceScoreDetail> implements Serializable {

	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(ScoringEnquiryDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */

	protected Window        window_ScoringEnquiryDialog;               // autoWired
	protected Listbox       listBoxFinancialScoRef;                    // autoWired
	protected Listbox       listBoxNonFinancialScoRef;                 // autoWired

	protected Borderlayout   borderlayoutScoringEnquiry;		       // autoWired
	protected Tab 			 finScoreMetricTab;
	protected Tab 			 nonFinScoreMetricTab;
	protected Grid			 grid_FinScoreDetail;
	
	protected Decimalbox 	maxFinTotScore;							// autoWired
	protected Decimalbox 	maxNonFinTotScore;						// autoWired
	protected Intbox 		minScore;								// autoWired
	protected Decimalbox 	calTotScore;							// autoWired
	protected Checkbox 		isOverride;								// autoWired
	protected Intbox 		overrideScore;							// autoWired
	protected Row 			row_finScoreOverride;					// autoWired

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

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

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
		logger.debug("Entering " + event.toString());

		if(event != null && event.getTarget().getParent().getParent() != null){
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("scoringList")) {
			List<Object> scoreObjectList= (List<Object>) args.get("scoringList");
			if(scoreObjectList != null){
				finScoreHeaderList = (List<FinanceScoreHeader>) scoreObjectList.get(0);
				scoreDetailListMap = (HashMap<Long, List<FinanceScoreDetail>>) scoreObjectList.get(1);
			}
		}

		if (args.containsKey("financeEnquiryHeaderDialogCtrl")) {
			this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) args.get("financeEnquiryHeaderDialogCtrl");
		}
		if(args.containsKey("custTypeCtg")){
			this.custCtgType = (String) args.get("custTypeCtg");
		  }

		doShowDialog();

		logger.debug("Leaving " + event.toString());
	}


	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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

		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
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

			if("I".equals(custCtgType)){
				
				this.finScoreMetricTab.setLabel("Retail Scoring Metric Details");
				this.nonFinScoreMetricTab.setVisible(false);
				this.grid_FinScoreDetail.setVisible(false);

				for (int i = 0; i < getFinScoreHeaderList().size(); i++) {
					FinanceScoreHeader header = getFinScoreHeaderList().get(i);
					addListGroup("", this.listBoxFinancialScoRef, "I", header);
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

			}else if("C".equals(custCtgType)){

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
		logger.debug("Leaving");
	}

	/**
	 * Method for Adding List Group to listBox in Corporation
	 * @param scoringMetric
	 * @param listbox
	 */
	private void addListGroup(String groupCodeDesc, Listbox listbox, String ctgType, Object object){
		logger.debug("Entering");
		Listgroup listgroup = new Listgroup();

		if("I".equals(ctgType)){

			FinanceScoreHeader header = null;
			if(object instanceof FinanceScoreHeader){
				header = (FinanceScoreHeader) object;

				Listcell lc = new Listcell(header.getGroupCode()+" - "+header.getGroupCodeDesc());
				lc.setParent(listgroup);

				lc = new Listcell();
				lc.setSpan(3);
				Label label = new Label("Min Score :"+header.getMinScore());
				lc.appendChild(label);
				Space space = new Space();
				space.setWidth("100px");
				lc.appendChild(space);
				label = new Label("Is Override :");
				label.setStyle("float:center;");
				lc.appendChild(label);

				Checkbox checkbox = new Checkbox();
				checkbox.setDisabled(true);
				checkbox.setStyle("float:center;");
				checkbox.setChecked(header.isOverride());
				lc.appendChild(checkbox);
				space = new Space();
				space.setWidth("100px");
				lc.appendChild(space);
				if(header.isOverride()){
					label = new Label("Override Score :"+header.getOverrideScore());
					label.setStyle("float:right;");
					lc.appendChild(label);
				}
				lc.setParent(listgroup);
			}

			FinanceReferenceDetail detail = null;
			if(object instanceof FinanceReferenceDetail){
				detail = (FinanceReferenceDetail) object;

				Listcell lc = new Listcell(detail.getLovDescCodelov() + "-" + detail.getLovDescNamelov());
				lc.setParent(listgroup);

				lc = new Listcell();
				lc.setSpan(3);
				Label label = new Label("Min Score :"+detail.getLovDescminScore());
				lc.appendChild(label);
				Space space = new Space();
				space.setWidth("100px");
				lc.appendChild(space);
				if(detail.isLovDescisoverride()){
					label = new Label("Is Override :");
					lc.appendChild(label);

					Checkbox checkbox = new Checkbox();
					checkbox.setDisabled(false);

					List<Object> overrideList = new ArrayList<Object>();
					overrideList.add(detail.getFinRefId());//1. Group Id
					overrideList.add(checkbox);//2. Overrided CheckBox
					overrideList.add(detail.getLovDescminScore());//3. Min Group Score
					overrideList.add(detail.getLovDescoverrideScore());//4. Group Overriden Score

					lc.appendChild(checkbox);

					space = new Space();
					space.setWidth("100px");
					lc.appendChild(space);

					label = new Label("Override Score :"+detail.getLovDescoverrideScore());
					lc.appendChild(label);
				}
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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
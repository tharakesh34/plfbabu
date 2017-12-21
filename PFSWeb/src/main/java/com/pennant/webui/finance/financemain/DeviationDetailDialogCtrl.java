package com.pennant.webui.finance.financemain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.North;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.bmtmasters.CheckList;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.rmtmasters.ScoringGroup;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.solutionfactory.DeviationParam;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.DeviationConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class DeviationDetailDialogCtrl extends GFCBaseCtrl<FinanceDeviations> {
	private static final long	serialVersionUID		= 2290501784830847866L;
	private static final Logger	logger					= Logger.getLogger(DeviationDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window			window_deviationDetailDialog;													// autoWired
	protected Borderlayout		borderlayoutDeviationDetail;													// autoWired
	protected North				northdeviationDetailDialog;
	protected Button			btnProceed;

	protected Listbox			listBoxDeviationDetails;														// autoWired
	protected Listbox			listBoxApprovedDeviationDetails;												// autoWired
	
	private FinBasicDetailsCtrl  finBasicDetailsCtrl;
	protected Groupbox finBasicdetails;

	private Object				financeMainDialogCtrl	= null;
	private FinanceDetail		financeDetail			= null;

	List<DeviationParam>		eligibilitiesList		= PennantAppUtil.getDeviationParams();
	boolean						enquiry					= false;
	boolean						approvalEnquiry			= false;

	
	List<FinanceDeviations> approvalEnqList=null;
	Tab parenttab=null;
	
	private Tabpanel 		tabPanel_dialogWindow;
	private Groupbox 		gbDeviations;
	private Caption 		cpApprovedDeviations;
	int ccyformat = 0;
	/**
	 * default constructor.<br>
	 */
	public DeviationDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_deviationDetailDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_deviationDetailDialog);

		try {

		// READ OVERHANDED parameters !
			if (arguments.containsKey("financeDetail")) {
				this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
				setFinanceDetail(financeDetail);
				ccyformat= CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				this.financeMainDialogCtrl = arguments.get("financeMainDialogCtrl");
			}
			
			if (arguments.containsKey("enquiry")) {
				enquiry = true;
			}
			
			if (arguments.containsKey("approvalEnquiry")) {
				approvalEnquiry = true;
			}
			
			if (arguments.containsKey("approvalEnqList")) {
				approvalEnqList = (List<FinanceDeviations>) arguments.get("approvalEnqList");
			}
			if (arguments.containsKey("ccyformat")) {
				ccyformat =Integer.parseInt(arguments.get("ccyformat").toString());
			}
			
			if (arguments.containsKey("tab")) {
				parenttab = (Tab) arguments.get("tab");
			}
			
			if (arguments.containsKey("tabPaneldialogWindow")) {
				tabPanel_dialogWindow = (Tabpanel) arguments.get("tabPaneldialogWindow");
			}
			
			if (arguments.containsKey("finHeaderList")) {
				appendFinBasicDetails((ArrayList<Object>) arguments.get("finHeaderList"));	
			}else{
				appendFinBasicDetails(null);	
			}
			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_deviationDetailDialog.onClose();
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws Exception
	 */
	public void doShowDialog() throws Exception {
		logger.debug("Entering");
		try {
			
			if (approvalEnquiry) {
				this.finBasicdetails.setVisible(false);
				this.northdeviationDetailDialog.setVisible(false);
				this.gbDeviations.setVisible(false);
				this.cpApprovedDeviations.setVisible(false);
				
				int height = borderLayoutHeight - 200;
				this.listBoxApprovedDeviationDetails.setHeight(height + "px");
				this.window_deviationDetailDialog.setHeight(borderLayoutHeight - 150 + "px");
				
				doFillDeviationDetails(approvalEnqList, this.listBoxApprovedDeviationDetails);
				
				this.tabPanel_dialogWindow.appendChild(this.window_deviationDetailDialog);
				return;
			}else{
				
				List<FinanceDeviations> list = new ArrayList<FinanceDeviations>(getFinanceDetail().getFinanceDeviations());

				doFillDeviationDetails(getFinanceDetail().getApprovedFinanceDeviations(), this.listBoxApprovedDeviationDetails);
				doFillDeviationDetails(list, this.listBoxDeviationDetails);
				checkTabDisplay();
			}
			
			// fill the components with the data
			if (enquiry) {
				int height = (borderLayoutHeight - 240)/2;

				this.listBoxDeviationDetails.setHeight(height + "px");
				this.listBoxApprovedDeviationDetails.setHeight(height + "px");

				this.window_deviationDetailDialog.setHeight("75%");
				this.window_deviationDetailDialog.setWidth("90%");
				this.window_deviationDetailDialog.doModal();
				
			} else {
				int height = (borderLayoutHeight - 340)/2;
				this.listBoxDeviationDetails.setHeight(height + "px");
				this.listBoxApprovedDeviationDetails.setHeight(height + "px");

				this.window_deviationDetailDialog.setHeight(borderLayoutHeight - 75 + "px");
				try {
					getFinanceMainDialogCtrl().getClass().getMethod("setDeviationDetailDialogCtrl", this.getClass()).invoke(getFinanceMainDialogCtrl(), this);
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
				this.northdeviationDetailDialog.setVisible(false);
			}

		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
	}

	private static final String styleListGroup = " font-weight: bold;";
	private static final String	devNotallowedStype	= "font-weight:bold;color:red;";
	
	
	public void doFillDeviationDetails(List<FinanceDeviations> financeDeviations) {
		doFillDeviationDetails(financeDeviations,this.listBoxDeviationDetails);
		checkTabDisplay();
	}
	
	/**
	 * 
	 */
	public void checkTabDisplay(){
		logger.debug(" Entering ");
		
		if (parenttab != null) {
			List<Listitem> list = this.listBoxDeviationDetails.getItems();
			List<Listitem> applist = this.listBoxApprovedDeviationDetails.getItems();
			if (list.isEmpty() && applist.isEmpty()) {
				this.parenttab.setVisible(false);	
			}else{
				this.parenttab.setVisible(true);
			}
			
		}
		
		logger.debug(" Leaving ");
	}
	

	public void doFillDeviationDetails(List<FinanceDeviations> financeDeviations, Listbox listbox) {
		logger.debug("Entering");

		listbox.getItems().clear();
		if (financeDeviations == null || financeDeviations.isEmpty()) {
			return;
		}
		Collections.sort(financeDeviations, new CompareDeviation());

		String module = "";

		for (FinanceDeviations deviationDetail : financeDeviations) {

			boolean deviationNotallowed = false;
			Listcell listcell;
			if (!module.equals(deviationDetail.getModule())) {
				module = deviationDetail.getModule();
				Listgroup listgroup = new Listgroup();
				listcell = new Listcell(Labels.getLabel("listGroup_" + deviationDetail.getModule()));
				listcell.setStyle(styleListGroup);
				listgroup.appendChild(listcell);
				listbox.appendChild(listgroup);
			}
			if (StringUtils.isEmpty(deviationDetail.getDelegationRole())) {
				deviationNotallowed = true;
			}

			Listitem listitem = new Listitem();

			String deviationCodedesc = getDeviationDesc(deviationDetail);
			listcell = getNewListCell(deviationCodedesc, deviationNotallowed);
			listitem.appendChild(listcell);
			listcell = getNewListCell(deviationDetail.getDeviationType(), deviationNotallowed);
			listitem.appendChild(listcell);
			listcell = getNewListCell(getDeviationValue(deviationDetail), deviationNotallowed);
			listitem.appendChild(listcell);
			listcell = getNewListCell(deviationDetail.getUserRole(), deviationNotallowed);
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantStaticListUtil.getlabelDesc(deviationDetail.getDelegationRole(), PennantAppUtil.getSecRolesList(null)));
			listitem.appendChild(listcell);
			listcell = getNewListCell(deviationDetail.getApprovalStatus(), deviationNotallowed);
			listitem.appendChild(listcell);
			listcell = getNewListCell(deviationDetail.getDeviationUserId(), deviationNotallowed);
			listitem.appendChild(listcell);
			listcell = getNewListCell(deviationDetail.getDelegatedUserId(), deviationNotallowed);
			listitem.appendChild(listcell);
			listcell = getNewListCell(DateUtility.formatToShortDate(deviationDetail.getDeviationDate()), deviationNotallowed);
			listitem.appendChild(listcell);
			listitem.setAttribute("data", deviationDetail);
			listbox.appendChild(listitem);
		}
		logger.debug("Leaving");
	}

	public String getDeviationDesc(FinanceDeviations deviationDetail) {
		String devCode = deviationDetail.getDeviationCode();

		if (DeviationConstants.TY_PRODUCT.equals(deviationDetail.getModule())) {

			return getProlabelDesc(devCode, eligibilitiesList);

		} else if (DeviationConstants.TY_ELIGIBILITY.equals(deviationDetail.getModule())) {

			return getRuleDesc(devCode, RuleConstants.MODULE_ELGRULE, null);

		} else if (DeviationConstants.TY_CHECKLIST.equals(deviationDetail.getModule())) {

			String temp = getChklabelDesc(devCode.substring(0, devCode.indexOf('_')));
			String cskDevType = devCode.substring(devCode.indexOf('_'));
			return temp + Labels.getLabel("deviation_checklist", new String[] { PennantStaticListUtil.getlabelDesc(cskDevType, PennantStaticListUtil.getCheckListDeviationType()) });

		} else if (DeviationConstants.TY_FEE.equals(deviationDetail.getModule())) {

			return getRuleDesc(null, RuleConstants.MODULE_FEES, devCode);
			
		} else if (DeviationConstants.TY_SCORE.equals(deviationDetail.getModule())) {

			return getScoreinglabelDesc(devCode);
		}
		return "";
	}

	public String getDeviationValue(FinanceDeviations deviationDetail) {

		String devType = deviationDetail.getDeviationType();
		String devValue = deviationDetail.getDeviationValue();
		
		if (DeviationConstants.DT_BOOLEAN.equals(devType)) {

			return devValue;

		} else if (DeviationConstants.DT_PERCENTAGE.equals(devType)) {

			return devValue + " % ";

		} else if (DeviationConstants.DT_DECIMAL.equals(devType)) {

			BigDecimal amount = new BigDecimal(devValue);
			return PennantAppUtil.amountFormate(amount, ccyformat);

		} else if (DeviationConstants.DT_INTEGER.equals(devType)) {

			BigDecimal amount = new BigDecimal(devValue);
			return Integer.toString(amount.intValue());
		}
		return "";
	}

	private Listcell getNewListCell(String val, boolean colrRed) {
		Listcell listcell = new Listcell(val);
		if (colrRed) {
			listcell.setStyle(devNotallowedStype);
		}
		return listcell;
	}

	class CompareDeviation implements Comparator<FinanceDeviations> {

		public CompareDeviation() {
			
		}
		
		@Override
		public int compare(FinanceDeviations o1, FinanceDeviations o2) {
			return o1.getModule().compareTo(o2.getModule());
		}
	}

	/**
	 * @param value
	 * @param deviationParamsList
	 * @return
	 */
	private String getProlabelDesc(String value, List<DeviationParam> deviationParamsList) {

		if (deviationParamsList != null && !deviationParamsList.isEmpty()) {
			for (DeviationParam param : deviationParamsList) {
				if (param.getCode().equals(value)) {
					return param.getDescription();
				}

			}
		}
		return "";
	}

	/**
	 * @param ruleid
	 * @param ruleModule
	 * @param rulecode
	 * @return
	 */
	private String getRuleDesc(String ruleid, String ruleModule, String rulecode) {

		logger.debug(" Entering ");

		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<Rule> searchObject = new JdbcSearchObject<Rule>(Rule.class);
		searchObject.addTabelName("Rules");
		if (!StringUtils.isEmpty(ruleid)) {
			searchObject.addFilterEqual("RuleId", ruleid);
		}
		if (!StringUtils.isEmpty(ruleModule)) {
			searchObject.addFilterEqual("RuleModule", ruleModule);
		}
		if (!StringUtils.isEmpty(rulecode)) {
			searchObject.addFilterEqual("RuleCode", rulecode);
		}

		List<Rule> list = pagedListService.getBySearchObject(searchObject);
		if (list != null && !list.isEmpty()) {

			logger.debug(" Leaving ");
			return list.get(0).getRuleCodeDesc();
		}

		logger.debug(" Leaving ");
		return "";
	}

	/**
	 * @param value
	 * @return
	 */
	private String getChklabelDesc(String value) {

		logger.debug(" Entering ");

		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<CheckList> searchObject = new JdbcSearchObject<CheckList>(CheckList.class);
		searchObject.addTabelName("BMTCheckList");
		searchObject.addFilterIn("CheckListId", value);

		List<CheckList> list = pagedListService.getBySearchObject(searchObject);
		if (list != null && !list.isEmpty()) {

			logger.debug(" Leaving ");
			return list.get(0).getCheckListDesc();
		}

		logger.debug(" Leaving ");
		return "";
	}
	
	/**
	 * @param value
	 * @return
	 */
	private String getScoreinglabelDesc(String value) {
		
		logger.debug(" Entering ");
		
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		
		JdbcSearchObject<ScoringGroup> searchObject = new JdbcSearchObject<ScoringGroup>(ScoringGroup.class);
		searchObject.addTabelName("RMTScoringGroup");
		searchObject.addFilterIn("ScoreGroupId", value);
		
		List<ScoringGroup> list = pagedListService.getBySearchObject(searchObject);
		if (list != null && !list.isEmpty()) {
			
			logger.debug(" Leaving ");
			return list.get(0).getScoreGroupName();
		}
		
		logger.debug(" Leaving ");
		return "";
	}


	public void onClick$btnProceed() throws InterruptedException {

		Executions.getCurrent().setAttribute("devationConfirm", true);
		List<FinanceDeviations> list = getFinanceDetail().getFinanceDeviations();
		boolean valid = true;
		for (FinanceDeviations financeDeviations : list) {
			if (StringUtils.isEmpty(financeDeviations.getDelegationRole())) {
				valid = false;
				break;
			}
		}

		if (!valid) {
			MessageUtil.showError("There are some deviation with out deligation.");
			return;
		}

		Executions.notify(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinReference());
		this.window_deviationDetailDialog.onClose();
	}

	public void onClick$btnCancel() {
		Executions.notify(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinReference());
		this.window_deviationDetailDialog.onClose();
	}
	
	/**
	 * This method is for append finance basic details to respective parent tabs
	 * @param arrayList 
	 */
	private void appendFinBasicDetails(ArrayList<Object> arrayList) {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this );
			if (arrayList!=null) {
				map.put("finHeaderList", arrayList );
			}
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul",this.finBasicdetails, map);
		} catch (Exception e) {
			logger.debug(e);
		}
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}



}

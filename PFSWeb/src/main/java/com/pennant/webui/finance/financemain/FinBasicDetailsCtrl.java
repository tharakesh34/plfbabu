package com.pennant.webui.finance.financemain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.A;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.CustomerFinanceDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.service.approvalstatusenquiry.ApprovalStatusEnquiryService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;

public class FinBasicDetailsCtrl extends GFCBaseCtrl<FinanceDetail> {

	private static final long serialVersionUID = -4843661930948561711L;

	private final static Logger logger = Logger.getLogger(FinBasicDetailsCtrl.class);
	
	protected Window window_FinBasicDetails; // autowired
	// Finance Schedule Details Tab
	protected Label finBasic_finType; 								// autoWired
	protected Label finBasic_finCcy; 								// autoWired
	protected Label finBasic_scheduleMethod; 						// autoWired
	protected Label finBasic_profitDaysBasis; 						// autoWired
	protected Label finBasic_finReference; 							// autoWired
	protected Label finBasic_grcEndDate; 							// autoWired
	protected Label label_FinBasicDetails_FinType; 					// autoWired
	protected Label label_FinBasicDetails_ProfitDaysBasis; 			// autoWired
	protected Label finBasic_custShrtName;							// autoWired
	protected Row row_grcPeriodEndDate;								// autoWired
	protected Row row_ProfitDays;									// autoWired
	
	protected A userActivityLog;									// autoWired
	
	private Object parentCtrl = null;
	private String finEventCode = null;
	
	private ApprovalStatusEnquiryService approvalStatusEnquiryService;


	public FinBasicDetailsCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}
	
	@SuppressWarnings("unchecked")
	public void onCreate$window_FinBasicDetails(ForwardEvent event)	throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinBasicDetails);

		if (arguments.containsKey("parentCtrl")) {
			parentCtrl = arguments.get("parentCtrl");
			
			if(!arguments.containsKey("addlFields")) {
				parentCtrl.getClass().getMethod("setFinBasicDetailsCtrl", this.getClass()).invoke(parentCtrl, this);
			} else {
				parentCtrl = arguments.get("finMainBaseCtrl");
				parentCtrl.getClass().getMethod("setFinBasicDetailsCtrl", this.getClass()).invoke(parentCtrl, this);
			}
		}
		if (arguments.containsKey("finHeaderList")) {
			doWriteBeanToComponents( (ArrayList<Object>) arguments.get("finHeaderList"));
		}
		logger.debug("Leaving " + event.toString());
	}
	
	public void doWriteBeanToComponents(ArrayList<Object> finHeaderList) {
		logger.debug("Entering");
		
		this.finBasic_finType.setValue(String.valueOf(finHeaderList.get(0)));
		this.finBasic_finCcy.setValue(String.valueOf(finHeaderList.get(1)));
		this.finBasic_scheduleMethod.setValue(String.valueOf(finHeaderList.get(2)));
		this.finBasic_finReference.setValue(String.valueOf(finHeaderList.get(3)));
		this.finBasic_profitDaysBasis.setValue(String.valueOf(finHeaderList.get(4)));
		
		Date grcEndDate=  (Date) finHeaderList.get(5);
		Boolean allowgrace= (Boolean) finHeaderList.get(6);
		String finCategory= String.valueOf(finHeaderList.get(8));
		this.finBasic_custShrtName.setValue(String.valueOf(finHeaderList.get(9)));
		if(StringUtils.equals(finCategory, FinanceConstants.PRODUCT_ISTISNA)){
			allowgrace = true;
		}
		
		if(StringUtils.equals(finCategory, FinanceConstants.PRODUCT_QARDHASSAN)){
			this.row_ProfitDays.setVisible(false);
		}
		
		if(allowgrace){
			this.finBasic_grcEndDate.setValue(DateUtility.formatToLongDate(grcEndDate));
			this.row_grcPeriodEndDate.setVisible(true);
		} else {
			this.row_grcPeriodEndDate.setVisible(false);
		}
		
		Boolean promotion=(Boolean) finHeaderList.get(7);
		if (promotion) {
			this.label_FinBasicDetails_FinType.setValue(Labels.getLabel("label_FinanceMainDialog_PromotionCode.value"));
		}
		
		Boolean newRecord=(Boolean) finHeaderList.get(10);
		this.finEventCode= String.valueOf(finHeaderList.get(11));
		
		if(!newRecord && isActivityLogVisible(finEventCode)) {
			this.userActivityLog.setVisible(true);
		}
		logger.debug("Leaving");
	}
	
	private boolean isActivityLogVisible(String finEvent){
		if(StringUtils.equals(FinanceConstants.FINSER_EVENT_LIABILITYREQ, finEvent) ||StringUtils.equals(FinanceConstants.FINSER_EVENT_NOCISSUANCE, finEvent) ||
				StringUtils.equals(FinanceConstants.FINSER_EVENT_TIMELYCLOSURE, finEvent)){
			return false;
		}
		return true;
	}
	public void onClick$userActivityLog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		doUserActivityLog();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * 
	 * @throws Exception
	 */
	private void doUserActivityLog() throws Exception {
		logger.debug("Entering ");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		CustomerFinanceDetail customerFinanceDetail=new CustomerFinanceDetail();		
		
		if(finBasic_finReference !=null && finBasic_finReference.getValue()!=null) {
			if(StringUtils.isEmpty(finEventCode)){
				customerFinanceDetail = getApprovalStatusEnquiryService().getCustomerFinanceById(this.finBasic_finReference.getValue(), finEventCode);
			}else{
				customerFinanceDetail = getApprovalStatusEnquiryService().getApprovedCustomerFinanceById(this.finBasic_finReference.getValue(), finEventCode);
			}
		}	
		
		if(customerFinanceDetail != null){
			map.put("customerFinanceDetail", customerFinanceDetail);
			map.put("userActivityLog", true);
			try {
				Executions.createComponents(
						"/WEB-INF/pages/FinanceEnquiry/FinApprovalStsInquiry/FinApprovalStsInquiryDialog.zul", null, map);
			} catch (Exception e) {
				logger.error("Exception: ", e);
				MessageUtil.showErrorMessage(e);
			}
		}else{
			MessageUtil.showError(Labels.getLabel("listbox.emptyMessage"));
		}
		
		logger.debug("Leaving ");
		
	}
	public ApprovalStatusEnquiryService getApprovalStatusEnquiryService() {
		return approvalStatusEnquiryService;
	}

	public void setApprovalStatusEnquiryService(ApprovalStatusEnquiryService approvalStatusEnquiryService) {
		this.approvalStatusEnquiryService = approvalStatusEnquiryService;
	}
	
}

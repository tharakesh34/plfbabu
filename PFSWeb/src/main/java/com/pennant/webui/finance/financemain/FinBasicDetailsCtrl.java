package com.pennant.webui.finance.financemain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.A;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.CustomerFinanceDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.reason.details.ReasonDetailsLog;
import com.pennant.backend.service.approvalstatusenquiry.ApprovalStatusEnquiryService;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

public class FinBasicDetailsCtrl extends GFCBaseCtrl<FinanceDetail> {

	private static final long serialVersionUID = -4843661930948561711L;

	private static final Logger logger = LogManager.getLogger(FinBasicDetailsCtrl.class);

	protected Window window_FinBasicDetails; // autowired
	// Finance Schedule Details Tab
	protected Label finBasic_finType; // autoWired
	protected Label finBasic_finCcy; // autoWired
	protected Label finBasic_scheduleMethod; // autoWired
	protected Label finBasic_profitDaysBasis; // autoWired
	protected Label finBasic_finReference; // autoWired
	protected Label finBasic_grcEndDate; // autoWired
	protected Label label_FinBasicDetails_FinType; // autoWired
	protected Label label_FinBasicDetails_ProfitDaysBasis; // autoWired
	protected Label finBasic_custShrtName; // autoWired
	protected Row row_grcPeriodEndDate; // autoWired
	protected Row row_ProfitDays; // autoWired

	protected A userActivityLog; // autoWired
	protected A reasonDeatilsLog; // autoWired

	boolean displayLog = true;
	private Object parentCtrl = null;
	private String finEventCode = null;
	private ArrayList<Object> finHeaderList;

	private ApprovalStatusEnquiryService approvalStatusEnquiryService;

	public FinBasicDetailsCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FinanceMain";
		super.pageRightName = "";
	}

	@SuppressWarnings("unchecked")
	public void onCreate$window_FinBasicDetails(ForwardEvent event) throws Exception {
		logger.trace(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_FinBasicDetails);
		if (arguments.containsKey("displayLog")) {
			displayLog = (boolean) arguments.get("displayLog");
		}

		if (arguments.containsKey("parentCtrl")) {
			parentCtrl = arguments.get("parentCtrl");

			if (!arguments.containsKey("addlFields")) {
				parentCtrl.getClass().getMethod("setFinBasicDetailsCtrl", this.getClass()).invoke(parentCtrl, this);
			} else {
				parentCtrl = arguments.get("finMainBaseCtrl");
				parentCtrl.getClass().getMethod("setFinBasicDetailsCtrl", this.getClass()).invoke(parentCtrl, this);
			}
		}
		if (arguments.containsKey("finHeaderList")) {
			finHeaderList = (ArrayList<Object>) arguments.get("finHeaderList");
			doWriteBeanToComponents(finHeaderList);
		}

		userActivityLog.setVisible(displayLog);
		reasonDeatilsLog.setVisible(displayLog);

		logger.trace(Literal.LEAVING);
	}

	private String getString(List<Object> finHeaderList, int index) {
		if (finHeaderList.size() <= index) {
			return "";
		}

		return String.valueOf(finHeaderList.get(index));
	}

	private Date getDate(List<Object> finHeaderList, int index) {
		if (finHeaderList.size() <= index) {
			return null;
		}

		return (Date) finHeaderList.get(index);
	}

	private boolean getBoolean(List<Object> finHeaderList, int index) {
		if (finHeaderList.size() <= index) {
			return false;
		}

		return (Boolean) finHeaderList.get(index);
	}

	public void doWriteBeanToComponents(ArrayList<Object> finHeaderList) {
		if (finHeaderList != null) {
			this.finHeaderList = finHeaderList;
		}

		this.finBasic_finType.setValue(getString(finHeaderList, 0));
		this.finBasic_finCcy.setValue(getString(finHeaderList, 1));
		this.finBasic_scheduleMethod.setValue(getString(finHeaderList, 2));
		this.finBasic_finReference.setValue(getString(finHeaderList, 3));
		this.finBasic_profitDaysBasis.setValue(getString(finHeaderList, 4));

		Date grcEndDate = getDate(finHeaderList, 5);
		Boolean allowgrace = getBoolean(finHeaderList, 6);
		this.finBasic_custShrtName.setValue(getString(finHeaderList, 9));

		if (allowgrace) {
			this.finBasic_grcEndDate.setValue(DateUtil.formatToLongDate(grcEndDate));
			this.row_grcPeriodEndDate.setVisible(true);
		} else {
			this.row_grcPeriodEndDate.setVisible(false);
		}

		Boolean promotion = getBoolean(finHeaderList, 7);
		if (promotion) {
			this.label_FinBasicDetails_FinType.setValue(Labels.getLabel("label_FinanceMainDialog_PromotionCode.value"));
		}

		Boolean newRecord = getBoolean(finHeaderList, 10);
		this.finEventCode = getString(finHeaderList, 11);

		if (!newRecord && isActivityLogVisible(finEventCode)) {
			this.userActivityLog.setVisible(true);
			this.reasonDeatilsLog.setVisible(true);
		}
	}

	private boolean isActivityLogVisible(String finEvent) {
		if (StringUtils.equals(FinServiceEvent.LIABILITYREQ, finEvent)
				|| StringUtils.equals(FinServiceEvent.NOCISSUANCE, finEvent)
				|| StringUtils.equals(FinServiceEvent.TIMELYCLOSURE, finEvent)) {
			return false;
		}
		return true;
	}

	public void onClick$userActivityLog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doUserActivityLog();
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$reasonDeatilsLog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doReasonDeatilsLog();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * 
	 * @throws Exception
	 */
	private void doUserActivityLog() throws Exception {
		logger.debug("Entering ");
		/*
		 * final Map<String, Object> map = new HashMap<String, Object>(); CustomerFinanceDetail
		 * customerFinanceDetail=new CustomerFinanceDetail();
		 * 
		 * if(finBasic_finReference !=null && finBasic_finReference.getValue()!=null) {
		 * if(StringUtils.isEmpty(finEventCode)){ customerFinanceDetail =
		 * getApprovalStatusEnquiryService().getCustomerFinanceById(this.finBasic_finReference.getValue(),
		 * finEventCode); }else{ customerFinanceDetail =
		 * getApprovalStatusEnquiryService().getApprovedCustomerFinanceById(this.finBasic_finReference.getValue(),
		 * finEventCode); } }
		 * 
		 * if(customerFinanceDetail != null){ map.put("customerFinanceDetail", customerFinanceDetail);
		 * map.put("userActivityLog", true); try { Executions.createComponents(
		 * "/WEB-INF/pages/FinanceEnquiry/FinApprovalStsInquiry/FinApprovalStsInquiryDialog.zul", null, map); } catch
		 * (Exception e) { MessageUtil.showError(e); } }else{
		 * MessageUtil.showError(Labels.getLabel("listbox.emptyMessage")); }
		 * 
		 */

		Map<String, Object> map = new LinkedHashMap<String, Object>();
		if (finHeaderList != null && !finHeaderList.isEmpty()) {
			map.put("label_FinanceMainDialog_FinType.value", finHeaderList.get(0));
			map.put("label_FinanceMainDialog_FinCcy.value", finHeaderList.get(1));
			map.put("label_FinanceMainDialog_ScheduleMethod.value", finHeaderList.get(2));
			map.put("label_FinanceMainDialog_ProfitDaysBasis.value", finHeaderList.get(4));
			map.put("label_FinanceMainDialog_FinReference.value", finHeaderList.get(3));
			map.put("label_FinanceMainDialog_CustShrtName.value", finHeaderList.get(9));

			doShowActivityLog(finHeaderList.get(3), map);
		}
		logger.debug("Leaving ");
	}

	private void doReasonDeatilsLog() {
		logger.debug("Entering ");

		List<ReasonDetailsLog> list = null;
		final Map<String, Object> map = new HashMap<String, Object>();
		CustomerFinanceDetail customerFinanceDetail = new CustomerFinanceDetail();

		if (finBasic_finReference != null && finBasic_finReference.getValue() != null) {
			list = approvalStatusEnquiryService.getResonDetailsLog(this.finBasic_finReference.getValue());
			if (StringUtils.isEmpty(finEventCode)) {
				customerFinanceDetail = getApprovalStatusEnquiryService()
						.getCustomerFinanceById(this.finBasic_finReference.getValue(), finEventCode);
			} else {
				customerFinanceDetail = getApprovalStatusEnquiryService()
						.getApprovedCustomerFinanceById(this.finBasic_finReference.getValue(), finEventCode);
			}
		}

		if (list != null && !list.isEmpty()) {
			map.put("reasonDetails", list);
			map.put("customerFinanceDetail", customerFinanceDetail);
			try {
				Executions.createComponents("/WEB-INF/pages/ReasonDetail/ReasonDetailsLogDialog.zul", null, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		} else if (finBasic_finReference != null && finBasic_finReference.getValue() != null) {
			MessageUtil.showInfo("REASON_DETAILS_LOG", finBasic_finReference.getValue());
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

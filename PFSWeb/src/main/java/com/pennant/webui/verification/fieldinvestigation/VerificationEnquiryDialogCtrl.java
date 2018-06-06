package com.pennant.webui.verification.fieldinvestigation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pennapps.pff.verification.service.VerificationService;

@Component(value = "verificationEnquiryDialogCtrl")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class VerificationEnquiryDialogCtrl extends GFCBaseCtrl<Verification> {
	private static final Logger logger = LogManager.getLogger(VerificationEnquiryDialogCtrl.class);
	private static final long serialVersionUID = -7291043288227495026L;
	protected Window window_VerificationEnquiry;

	protected Tabs tabsIndexCenter;
	protected Tab fiDetailTab;
	protected Tab tvDetailTab;
	protected Tab lvDetailTab;
	protected Tab rcuDetailTab;

	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Tabpanel fiDetailTabPanel;
	protected Tabpanel tvDetailTabPanel;
	protected Tabpanel lvDetailTabPAnel;
	protected Tabpanel rcuDetailTabPanel;
	protected List<Integer> verificationTypes = new ArrayList<>();

	private FinanceMainBaseCtrl financeMainDialogCtrl = null;
	private FinanceDetail financeDetail;
	private ArrayList<Object> finHeaderList;
	protected Combobox enquiryCombobox;
	@Autowired
	private VerificationService verificationService;

	/**
	 * default constructor.<br>
	 */
	public VerificationEnquiryDialogCtrl() {
		super();
	}

	@SuppressWarnings("unchecked")
	public void onCreate$window_VerificationEnquiry(Event event) throws Exception {
		setPageComponents(window_VerificationEnquiry);

		finHeaderList = (ArrayList<Object>) arguments.get("finHeaderList");
		financeDetail = (FinanceDetail) arguments.get("financeDetail");
		financeMainDialogCtrl = (FinanceMainBaseCtrl) arguments.get("financeMainBaseCtrl");
		enquiryCombobox = (Combobox) arguments.get("enuiryCombobox");
		
		fiDetailTabPanel.setHeight(getDesktopHeight()-10+"px");
		tvDetailTabPanel.setHeight(getDesktopHeight()-10+"px");
		lvDetailTabPAnel.setHeight(getDesktopHeight()-10+"px");
		rcuDetailTabPanel.setHeight(getDesktopHeight()-10+"px");

		doShowDialog();

	}

	public void doShowDialog() {
		logger.debug(Literal.ENTERING);
		try {
			visibleTabs();
			setDialog(DialogType.MODAL);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			this.window_VerificationEnquiry.onClose();
		}
		logger.debug(Literal.LEAVING);
	}

	protected void visibleTabs() {
		logger.debug(Literal.ENTERING);
		boolean isSelected = false;
		String finReference = String.valueOf(finHeaderList.get(3));
		FinanceDetail temp = new FinanceDetail();
		
		if (StringUtils.isBlank(finReference)) {
			return;
		} else {
			BeanUtils.copyProperties(financeDetail, temp);
		}
		
		verificationTypes = verificationService.getVerificationTypes(finReference);
		Map<String, Object> map = new HashMap<>();
		map.put("finHeaderList", finHeaderList);
		map.put("financeDetail", temp);
		map.put("financeMainBaseCtrl", financeMainDialogCtrl);
		map.put("enqiryModule", true);
		try {
			for (Integer verificationType : verificationTypes) {
				if (verificationType == VerificationType.FI.getKey()
						&& (!(financeDetail.isFiInitTab() || financeDetail.isFiApprovalTab()))) {
					this.fiDetailTab.setVisible(true);
					if (!isSelected) {
						isSelected = true;
						this.fiDetailTab.setSelected(true);
					}
					Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/FIApproval.zul",
							this.fiDetailTabPanel, map);
				} else if (verificationType == VerificationType.TV.getKey()
						&& (!(financeDetail.isTvInitTab() || financeDetail.isTvApprovalTab()))) {
					this.tvDetailTab.setVisible(true);
					if (!isSelected) {
						isSelected = true;
						this.tvDetailTab.setSelected(true);
					}
					Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/TVApproval.zul",
							this.tvDetailTabPanel, map);
				} else if (verificationType == VerificationType.LV.getKey()
						&& (!(financeDetail.isLvInitTab() || financeDetail.isLvApprovalTab()))) {
					this.lvDetailTab.setVisible(true);
					if (!isSelected) {
						isSelected = true;
						this.lvDetailTab.setSelected(true);
					}
					Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/LVApproval.zul",
							this.lvDetailTabPAnel, map);
				} else if (verificationType == VerificationType.RCU.getKey()
						&& (!(financeDetail.isRcuInitTab() || financeDetail.isRcuApprovalTab()))) {
					this.rcuDetailTab.setVisible(true);
					if (!isSelected) {
						isSelected = true;
						this.rcuDetailTab.setSelected(true);
					}
					Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/RCUApproval.zul",
							this.rcuDetailTabPanel, map);
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			this.window_VerificationEnquiry.onClose();
		}
		logger.debug(Literal.LEAVING);
	}

	public void onClick$verificationBtnClose(Event event) {
		logger.debug(Literal.ENTERING);
		this.enquiryCombobox.setSelectedIndex(0);
		window_VerificationEnquiry.onClose();
		logger.debug(Literal.LEAVING);
	}

}

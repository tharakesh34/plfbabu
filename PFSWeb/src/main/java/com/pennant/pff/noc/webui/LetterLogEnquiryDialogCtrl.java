package com.pennant.pff.noc.webui;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennant.webui.finance.enquiry.FinanceEnquiryHeaderDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;

public class LetterLogEnquiryDialogCtrl extends GFCBaseCtrl<FinExcessAmount> {
	private static final long serialVersionUID = 3184249234920071313L;
	private static final Logger logger = LogManager.getLogger(LetterLogEnquiryDialogCtrl.class);

	protected Window windowLetterLogEnquiryDialog;
	protected Listbox listBoxLetterLog;
	protected Borderlayout blGenLetterEnquiry;
	private Component parent = null;
	private Tab parentTab = null;

	protected Label finType;
	protected Label finccy;
	protected Label schMethod;
	protected Label profitbasis;
	protected Label finReference;
	protected Label custName;

	private Listheader listheaderletterTypeDialogButton;
	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl;
	private GenerateLetter generateLetter;

	public LetterLogEnquiryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	@SuppressWarnings("unchecked")
	public void onCreate$windowLetterLogEnquiryDialog(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(windowLetterLogEnquiryDialog);

		if (event.getTarget().getParent() != null) {
			parent = event.getTarget().getParent();
		}

		if (arguments.containsKey("parentTab")) {
			parentTab = (Tab) arguments.get("parentTab");
		}

		if (arguments.containsKey("generateLetter")) {
			this.generateLetter = (GenerateLetter) arguments.get("generateLetter");
		}

		GenerateLetter genLtr = new GenerateLetter();
		BeanUtils.copyProperties(this.generateLetter, genLtr);

		doSetFieldProperties();

		doShowDialog(this.generateLetter);
		logger.debug(Literal.LEAVING);
	}

	public void doShowDialog(GenerateLetter genLtr) {
		logger.debug(Literal.ENTERING);

		fillHeaderData(genLtr);

		logger.debug(Literal.LEAVING);
	}

	private void fillHeaderData(GenerateLetter genLtr) {
		FinanceMain fm = genLtr.getFinanceDetail().getFinScheduleData().getFinanceMain();

		this.finType.setValue(fm.getFinType());
		this.finccy.setValue(fm.getFinCcy());
		this.schMethod.setValue(fm.getScheduleMethod());
		this.profitbasis.setValue(fm.getProfitDaysBasis());
		this.finReference.setValue(fm.getFinReference());
		this.custName.setValue(genLtr.getFinanceDetail().getCustomerDetails().getCustomer().getCustShrtName());
	}

	private void doSetFieldProperties() {
		logger.debug("Entering");

		int divHeight = this.borderLayoutHeight - 80;
		int semiBorderlayoutHeights = divHeight / 2;

		this.listBoxLetterLog.setHeight(semiBorderlayoutHeights - 105 + "px");

		if (parent != null) {
			this.windowLetterLogEnquiryDialog.setHeight(borderLayoutHeight - 75 + "px");
			parent.appendChild(this.windowLetterLogEnquiryDialog);
		}

		logger.debug("Leaving");
	}

	public void onExpand(ForwardEvent event) {

	}

	public void onCollapse(ForwardEvent event) {

	}

	private void doFillHeaderList(List<FinExcessAmount> excesslist) {

	}

	private void doFillHeaderList(String excessType, List<FinExcessAmount> feList) {
		logger.debug(Literal.ENTERING);

	}

	private void doFillChildDetail(List<FinExcessAmount> feDetail) {
		logger.debug(Literal.ENTERING);

	}

	private Button getButton(FinExcessAmount temp) {
		Button button = new Button();

		if (temp.isExpand()) {
			button.setImage("/images/icons/delete.png");
			button.setStyle("background:white;border:0px;");
			button.addForward("onClick", self, "onExpand");
		} else {
			button.setImage("/images/icons/add.png");
			button.setStyle("background:#FFFFFF;border:0px;onMouseOver ");
			button.addForward("onClick", self, "onCollapse");
		}

		button.setAttribute("pd", temp);
		return button;
	}
}

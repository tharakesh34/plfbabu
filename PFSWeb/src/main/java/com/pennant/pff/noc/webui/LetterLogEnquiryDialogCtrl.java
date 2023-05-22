package com.pennant.pff.noc.webui;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennant.pff.noc.service.GenerateLetterService;
import com.pennant.webui.finance.enquiry.FinanceEnquiryHeaderDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class LetterLogEnquiryDialogCtrl extends GFCBaseCtrl<FinExcessAmount> {
	private static final long serialVersionUID = 3184249234920071313L;
	private static final Logger logger = LogManager.getLogger(LetterLogEnquiryDialogCtrl.class);

	protected Window windowLetterLogEnquiryDialog;
	protected Listbox listBoxLetterLog;
	protected Borderlayout blGenLetterEnquiry;
	private Component parent = null;
	private Tab parentTab = null;
	private Tabpanel tabPanel_dialogWindow;

	protected Label finType;
	protected Label finccy;
	protected Label schMethod;
	protected Label profitbasis;
	protected Label finReference;
	protected Label custName;

	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl;
	private GenerateLetter generateLetter;
	private transient GenerateLetterService generateLetterService;

	public LetterLogEnquiryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

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

		try {
			fillEnquirly(genLtr);

			if (tabPanel_dialogWindow != null) {
				getBorderLayoutHeight();
				int rowsHeight;
				if (financeEnquiryHeaderDialogCtrl != null) {
					rowsHeight = (financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount() * 20)
							+ 1;
				} else {
					rowsHeight = 20;
				}

				this.listBoxLetterLog.setHeight(this.borderLayoutHeight - rowsHeight - 200 + "px");
				this.windowLetterLogEnquiryDialog.setHeight(this.borderLayoutHeight - rowsHeight + "px");
				tabPanel_dialogWindow.appendChild(this.windowLetterLogEnquiryDialog);

			}
		} catch (UiException e) {
			logger.error(Literal.EXCEPTION, e);
			this.windowLetterLogEnquiryDialog.onClose();
		} catch (Exception e) {
			throw e;
		}

		logger.debug(Literal.LEAVING);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void fillEnquirly(GenerateLetter genLtr) {
		List<GenerateLetter> letterInfo = generateLetterService.getLetterInfo(genLtr);

		this.listBoxLetterLog.setModel(new GroupsModelArray(letterInfo.toArray(), new LogLetterEnquiryComparator()));
		this.listBoxLetterLog.setItemRenderer(new LogLetterEnquiryModelItemRenderer());
		logger.debug("Leaving");

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

	private class LogLetterEnquiryModelItemRenderer implements ListitemRenderer<GenerateLetter>, Serializable {
		private static final long serialVersionUID = 6056180845898696437L;

		public LogLetterEnquiryModelItemRenderer() {
			super();
		}

		@Override
		public void render(Listitem item, GenerateLetter data, int arg2) throws Exception {

			if (item instanceof Listgroup) {
				item.appendChild(new Listcell(data.getLetterType()));
				item.setStyle("text-align:left;");
			} else if (item instanceof Listgroupfoot) {
				Listcell cell = new Listcell("");
				cell.setSpan(9);
				item.appendChild(cell);
			} else {

				Listcell lc;
				lc = new Listcell(DateUtil.formatToLongDate(data.getGeneratedDate()));
				lc.setParent(item);

				lc = new Listcell(data.getModeofTransfer());
				lc.setParent(item);
				lc = new Listcell(data.getRequestType());
				lc.setParent(item);
				lc = new Listcell(String.valueOf(data.getApprovedBy()));
				lc.setParent(item);
				lc = new Listcell(data.getCourierAgencyName());
				lc.setParent(item);
				lc = new Listcell(DateUtil.formatToLongDate(data.getGeneratedDate()));
				lc.setParent(item);
				lc = new Listcell(data.getStatus());
				lc.setParent(item);
				lc = new Listcell(DateUtil.formatToLongDate(data.getGeneratedDate()));
				lc.setParent(item);
				lc = new Listcell(data.getEmail());
				lc.setParent(item);
				lc = new Listcell(data.getFileName());
				lc.setParent(item);
			}
		}
	}

	private class LogLetterEnquiryComparator implements Comparator<Object>, Serializable {

		private static final long serialVersionUID = 9112640872865877333L;

		public LogLetterEnquiryComparator() {
			super();
		}

		@Override
		public int compare(Object o1, Object o2) {
			GenerateLetter data = (GenerateLetter) o1;
			GenerateLetter data2 = (GenerateLetter) o2;
			return String.valueOf(data.getLetterType()).compareTo(String.valueOf(data2.getLetterType()));
		}
	}

	@Autowired
	public void setGenerateLetterService(GenerateLetterService generateLetterService) {
		this.generateLetterService = generateLetterService;
	}
}

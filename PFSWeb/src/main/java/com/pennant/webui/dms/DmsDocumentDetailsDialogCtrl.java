package com.pennant.webui.dms;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.payment.PaymentHeader;
import com.pennant.webui.payment.paymentheader.PaymentHeaderListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.model.dms.DMSDocumentDetails;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

@SuppressWarnings("unused")
public class DmsDocumentDetailsDialogCtrl extends GFCBaseCtrl<DMSDocumentDetails> {
	private static final long serialVersionUID = 1153958690214979057L;
	private static final Logger	logger = Logger.getLogger(DmsDocumentDetailsDialogCtrl.class);
	private Window window_DmsDocumentDetailDialog;
	private List<DMSDocumentDetails> dmsDocumentDetaillog=null;
	private DMSDocumentDetails dmsDocumentDetails;
	private Label label_DmsDocumentDetailList_FinReference_Value;
	private Label label_DmsDocumentDetailList_DmsDocumentStatus_Value;
	private Label label_DmsDocumentDetailList_DmsId_Value;
	private Listbox listBoxDmsDocumentErrorDetail;
	private Borderlayout borderlayout_DmsDocumentDetailDialog;

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_DmsDocumentDetailDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_DmsDocumentDetailDialog);
		try {
			if(arguments.containsKey("dmsDocumentDetaillog")){
				this.dmsDocumentDetaillog=(List<DMSDocumentDetails>) arguments.get("dmsDocumentDetaillog");
			}
			if(arguments.containsKey("dmsDocumentDetails")){
				this.dmsDocumentDetails=(DMSDocumentDetails) arguments.get("dmsDocumentDetails");
			}
			if (enqiryModule) {
				listBoxDmsDocumentErrorDetail.setHeight("350px");
				this.borderlayout_DmsDocumentDetailDialog.setHeight(getBorderLayoutHeight());
			}
			doWriteBeanToComponents(dmsDocumentDetails,dmsDocumentDetaillog);
			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void doWriteBeanToComponents(DMSDocumentDetails dmsDocumentDetails,
			List<DMSDocumentDetails> dmsDocumentDetaillog) {
		if(null!=dmsDocumentDetails){
			label_DmsDocumentDetailList_FinReference_Value.setValue(dmsDocumentDetails.getFinReference());
			label_DmsDocumentDetailList_DmsId_Value.setValue(String.valueOf(dmsDocumentDetails.getId()));
			label_DmsDocumentDetailList_DmsDocumentStatus_Value.setValue(dmsDocumentDetails.getStatus());
		}
		fillDmsDocumentDetails(dmsDocumentDetaillog);
	}

	private void fillDmsDocumentDetails(List<DMSDocumentDetails> dmsDocumentDetaillog) {
		if(CollectionUtils.isNotEmpty(dmsDocumentDetaillog)){
			for (DMSDocumentDetails dmsDocumentDetails : dmsDocumentDetaillog) {
				if(null!=dmsDocumentDetails && StringUtils.isNotBlank(dmsDocumentDetails.getErrorDesc())){
					Listitem item = new Listitem();
					Listcell abc = new Listcell();
					Textbox test = new Textbox();
					test.setReadonly(true);
					test.setStyle("text-align:right; ");
					test.setValue(dmsDocumentDetails.getErrorDesc());
					abc.appendChild(test);
					abc.setParent(item);
					this.listBoxDmsDocumentErrorDetail.appendChild(item);
				}
			}
		}
	}
	
	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(false);
		logger.debug(Literal.LEAVING);
	}
}

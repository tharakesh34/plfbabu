package com.pennant.webui.errordetail.errordetail;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.webui.util.GFCBaseCtrl;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMasters/Sector/SectorList.zul file.
 */
public class AuditErrorDetailsListCtrl extends GFCBaseCtrl<AuditHeader> {
	private static final long	serialVersionUID	= -4561944744750744817L;

	protected Window			window_ErrorDetails;
	protected Borderlayout		borderLayout_ErrorDetails;
	protected Paging			pagingErrorDetails;
	protected Listbox			listBoxErrorDetails;

	protected Listheader		listheader_ErrorCode;
	protected Listheader		listheader_ErrorDescription;

	protected Button			btnClose;

	private AuditHeader			auditHeader;

	/**
	 * default constructor.<br>
	 */
	public AuditErrorDetailsListCtrl() {
		super();
	}

	public void onCreate$window_ErrorDetails(Event event) {
		setPageComponents(window_ErrorDetails);
		if (arguments.containsKey("AuditHeader")) {
			setAuditHeader((AuditHeader) arguments.get("AuditHeader"));
		}

		doFillErrorDetails(auditHeader);
		this.listBoxErrorDetails.setHeight(this.borderLayoutHeight - 50 + "px");
		this.window_ErrorDetails.doModal();

	}

	public void doFillErrorDetails(AuditHeader auditHeader) {

		this.listBoxErrorDetails.getItems().clear();
		if (auditHeader != null) {

			for (ErrorDetail errorDetails : auditHeader.getErrorMessage()) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(errorDetails.getCode());
				lc.setParent(item);
				lc = new Listcell(errorDetails.getError());
				lc.setParent(item);
				item.setAttribute("data", errorDetails);

				this.listBoxErrorDetails.appendChild(item);
			}
		}
	}

	public void onClick$btnClose(Event event) throws InterruptedException {
		this.window_ErrorDetails.onClose();
	}

	public void setAuditHeader(AuditHeader auditHeader) {
		this.auditHeader = auditHeader;
	}

}
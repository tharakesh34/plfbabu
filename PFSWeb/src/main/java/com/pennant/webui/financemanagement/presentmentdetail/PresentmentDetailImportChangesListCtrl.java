package com.pennant.webui.financemanagement.presentmentdetail;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/com.pennant.financemanagement/PresentmentDetail/PresentmentDetailImportChangesList.zul file.
 * 
 */
public class PresentmentDetailImportChangesListCtrl extends GFCBaseCtrl<PresentmentDetail> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(PresentmentDetailImportChangesListCtrl.class);

	protected Window window_PresentmentDetailList;
	protected Borderlayout borderLayout_PresentmentDetailList;
	protected Paging pagingPresentmentDetailList;
	protected Listbox listBoxPresentmentDetail;

	// List headers
	protected Listheader listheader_isActionRequired;
	protected Listheader listheader_FinReference;
	protected Listheader listheader_presentmentRef;
	protected Listheader listheader_AddedTo;
	PresentmentDetailDialogCtrl presentmentDetailDialogCtrl;
	String moduleType;
	protected List<Long> includeList;

	protected List<Long> excludeList;

	protected Button btnProceed;

	public PresentmentDetailImportChangesListCtrl() {
		super();
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_PresentmentDetailList(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_PresentmentDetailList);

		presentmentDetailDialogCtrl = (PresentmentDetailDialogCtrl) arguments.get("presentmentDetailDialogCtrl");
		moduleType = (String) arguments.get("ModuleType");
		includeList = (List<Long>) arguments.get("includeList");
		excludeList = (List<Long>) arguments.get("excludeList");
		List<PresentmentDetail> presentmentDetailImportChangesList = (List<PresentmentDetail>) arguments
				.get("presentmentDetailImportChangesList");
		render(presentmentDetailImportChangesList);
		setDialog(DialogType.MODAL);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(btnClose.isVisible());
		presentmentDetailDialogCtrl.uploadedfileName.setValue("");
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnProceed(Event event) {
		logger.debug(Literal.ENTERING);
		this.listBoxPresentmentDetail.getSelectedItem();

		if (!this.listBoxPresentmentDetail.getSelectedItems().isEmpty()) {
			for (Listitem listitem : this.listBoxPresentmentDetail.getItems()) {
				if (!listitem.isSelected()) {
					Long Id = (Long) listitem.getAttribute("ID");
					if (includeList.contains(Id)) {
						includeList.remove(Id);
					} else if (excludeList.contains(Id)) {
						excludeList.remove(Id);
					}
				}
			}
		} else {
			MessageUtil.showError("Please select atleast one record to proceed");
			return;
		}
		presentmentDetailDialogCtrl.saveModifiedList(includeList, excludeList);
		closeDialog();
		logger.debug(Literal.LEAVING);
	}

	// To display the Include Exclude changes
	public void render(List<PresentmentDetail> data) {
		for (PresentmentDetail presentmentDetail : data) {
			Listitem item = new Listitem();
			Listcell lc;
			item.setAttribute("ID", presentmentDetail.getId());
			lc = new Listcell();
			lc.setParent(item);

			lc = new Listcell(presentmentDetail.getFinReference());
			lc.setParent(item);

			if (presentmentDetail.getPresentmentRef() != null) {
				lc = new Listcell(presentmentDetail.getPresentmentRef());
				lc.setParent(item);
			} else {
				lc = new Listcell("");
				lc.setParent(item);
			}

			int excludeReason = presentmentDetail.getExcludeReason();
			switch (excludeReason) {
			case 0:
				lc = new Listcell(" Add to Exclude");
				lc.setParent(item);
				item.setSelected(true);
				break;
			case 1:
				lc = new Listcell("Record already exits in include");
				lc.setParent(item);
				item.setDisabled(true);
				break;
			case 2:
				lc = new Listcell("Record already exits in Exclude");
				lc.setParent(item);
				item.setDisabled(true);
				break;
			case 3:
				lc = new Listcell("Cannot change AutoExlcude record");
				lc.setParent(item);
				item.setDisabled(true);
				;
				break;
			case 4:
				lc = new Listcell("No record available");
				lc.setParent(item);
				item.setDisabled(true);
				break;
			case 6:
				lc = new Listcell("Add to Include");
				lc.setParent(item);
				item.setSelected(true);
				break;
			}

			this.listBoxPresentmentDetail.appendChild(item);
		}

	}
}

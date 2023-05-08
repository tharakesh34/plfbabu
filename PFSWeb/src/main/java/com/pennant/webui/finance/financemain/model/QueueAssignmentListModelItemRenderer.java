package com.pennant.webui.finance.financemain.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.QueueAssignmentHeader;

public class QueueAssignmentListModelItemRenderer implements ListitemRenderer<QueueAssignmentHeader>, Serializable {

	private static final long serialVersionUID = -800292670064839471L;

	public QueueAssignmentListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, QueueAssignmentHeader queueAssignment, int count) {

		Listcell cel = new Listcell("");
		item.appendChild(cel);
		if (item instanceof Listgroup) {
			Listcell lc = new Listcell(queueAssignment.getUserId() + " - " + queueAssignment.getLovDescUserName());
			lc.setStyle("font-weight:bold;");
			item.appendChild(lc);
		} else if (item instanceof Listgroupfoot) {
			Listcell cell = new Listcell("");
			cell.setSpan(2);
			item.appendChild(cell);
		} else {
			Listcell lc;
			lc = new Listcell(queueAssignment.getRoleDesc());
			lc.setParent(item);
			lc = new Listcell(String.valueOf(queueAssignment.getAssignedCount()));
			lc.setParent(item);
			item.setAttribute("data", queueAssignment);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onQueueAssignmentItemDoubleClicked");
		}
	}
}

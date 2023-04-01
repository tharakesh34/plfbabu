package com.pennant.webui.verification.personaldiscussion.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.verification.model.PersonalDiscussion;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class PersonalDiscussionListModelItemRenderer implements ListitemRenderer<PersonalDiscussion>, Serializable {
	private static final long serialVersionUID = 1L;

	public PersonalDiscussionListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, PersonalDiscussion pd, int count) {
		Listcell lc;
		lc = new Listcell(String.valueOf(pd.getCif()));
		lc.setParent(item);
		lc = new Listcell(pd.getAddressType());
		lc.setParent(item);
		lc = new Listcell(pd.getZipCode());
		lc.setParent(item);
		lc = new Listcell(pd.getKeyReference());
		lc.setParent(item);
		lc = new Listcell(pd.getAgencyName());
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(pd.getCreatedOn()));
		lc.setParent(item);
		lc = new Listcell(pd.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(pd.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", pd.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onPersonalDiscussionItemDoubleClicked");
	}
}
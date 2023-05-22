package com.pennant.webui.organization.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.organization.OrganizationType;
import com.pennanttech.pff.organization.model.Organization;

public class OrganizationListModelItemRender implements ListitemRenderer<Organization>, Serializable {
	private static final long serialVersionUID = 1L;

	public OrganizationListModelItemRender() {
		super();
	}

	@Override
	public void render(Listitem item, Organization org, int index) throws Exception {
		Listcell lc;

		if (OrganizationType.SCHOOL.getKey() == org.getType()) {
			lc = new Listcell(OrganizationType.SCHOOL.getValue());
			lc.setParent(item);
		}
		lc = new Listcell(org.getCif());
		lc.setParent(item);
		lc = new Listcell(org.getCode());
		lc.setParent(item);
		lc = new Listcell(org.getName());
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(org.getDate_Incorporation()));
		lc.setParent(item);
		lc = new Listcell(org.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(org.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", org.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onOrganizationItemDoubleClicked");

	}

}

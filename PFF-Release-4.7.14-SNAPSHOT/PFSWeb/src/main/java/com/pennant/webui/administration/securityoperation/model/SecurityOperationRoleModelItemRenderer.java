package com.pennant.webui.administration.securityoperation.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.administration.SecurityRight;

public class SecurityOperationRoleModelItemRenderer implements ListitemRenderer<Object>, Serializable  {

	private static final long serialVersionUID = 8842120255261997095L;
	SecurityRight secRights;
	@Override
	public void render(Listitem item, Object data,int count) throws Exception {

		secRights=(SecurityRight)data;
		Listcell lc=new Listcell(String.valueOf(secRights.getRightName()));
		lc.setParent(item);
		item.setAttribute("data", secRights);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onSecurityGroupItemDoubleClicked");		
	}
}


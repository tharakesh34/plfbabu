package com.pennant.pff.presentment.web;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.pff.presentment.model.RePresentmentUploadDetail;
import com.pennanttech.pennapps.core.util.DateUtil;

public class RePresentmentListModelItemRenderer implements ListitemRenderer<RePresentmentUploadDetail>, Serializable {
	private static final long serialVersionUID = 6906998807263283546L;

	public RePresentmentListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, RePresentmentUploadDetail rpud, int index) throws Exception {
		Listcell lc;

		String reference = rpud.getReference();

		if (StringUtils.isNotBlank(reference)) {
			reference = reference.toUpperCase();
		}

		lc = new Listcell(reference);
		lc.setParent(item);

		lc = new Listcell(DateUtil.formatToLongDate(rpud.getDueDate()));
		lc.setParent(item);

		lc = new Listcell(rpud.getProgress() == 2 ? "SUCCESS" : "FAILED");
		lc.setParent(item);

		lc = new Listcell(rpud.getRemarks());
		lc.setParent(item);

	}
}

/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : DirectorDetailListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 01-12-2011 * * Modified Date : 01-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 01-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.customermasters.directordetail.model;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class DirectorDetailListModelItemRenderer implements ListitemRenderer<DirectorDetail>, Serializable {

	private List<ValueLabel> countryList = null;
	private List<ValueLabel> docTypeList = null;

	public DirectorDetailListModelItemRenderer(List<ValueLabel> countryListDetails, List<ValueLabel> docTypes) {
		countryList = countryListDetails;
		docTypeList = docTypes;
	}

	private static final long serialVersionUID = -6611216779270185816L;

	public DirectorDetailListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, DirectorDetail directorDetail, int count) {

		if (item instanceof Listgroup) {
			item.appendChild(new Listcell(String.valueOf(directorDetail.getLovDescCustCIF())));
		} else if (item instanceof Listgroupfoot) {
			Listcell cell = new Listcell("");
			cell.setSpan(6);
			item.appendChild(cell);
		} else {
			String name = "";
			if (StringUtils.isNotBlank(directorDetail.getShortName())) {
				name = directorDetail.getShortName();
			} else if (StringUtils.isNotBlank(directorDetail.getFirstName())
					|| StringUtils.isNotBlank(directorDetail.getLastName())) {
				name = (directorDetail.getFirstName() == null ? " " : directorDetail.getFirstName()) + "  "
						+ (directorDetail.getLastName() == null ? " " : directorDetail.getLastName());
			}
			if (StringUtils.trimToEmpty(directorDetail.getCustAddrCountry())
					.equals(StringUtils.trimToEmpty(directorDetail.getLovDescCustAddrCountryName()))) {
				String desc = PennantApplicationUtil.getLabelDesc(directorDetail.getCustAddrCountry(), countryList);
				directorDetail.setLovDescCustAddrCountryName(desc);
			}
			Listcell lc = new Listcell(name);
			lc.setParent(item);
			if (StringUtils.isNotBlank(directorDetail.getLovDescCustAddrCountryName())) {
				lc = new Listcell(
						directorDetail.getCustAddrCountry() + " - " + directorDetail.getLovDescCustAddrCountryName());
			} else {
				lc = new Listcell(directorDetail.getCustAddrCountry());
			}
			lc.setParent(item);
			if (directorDetail.getSharePerc() != null) {
				lc = new Listcell(String.valueOf(directorDetail.getSharePerc().doubleValue()));
				lc.setParent(item);
			}
			if (StringUtils.trimToEmpty(directorDetail.getIdType())
					.equals(StringUtils.trimToEmpty(directorDetail.getLovDescCustDocCategoryName()))) {
				String desc = PennantApplicationUtil.getLabelDesc(directorDetail.getIdType(), docTypeList);
				directorDetail.setLovDescCustDocCategoryName(desc);
			}
			if (StringUtils.trimToEmpty(directorDetail.getNationality())
					.equals(StringUtils.trimToEmpty(directorDetail.getLovDescNationalityName()))) {
				String desc = PennantApplicationUtil.getLabelDesc(directorDetail.getNationality(), countryList);
				directorDetail.setLovDescNationalityName(desc);
			}
			if (StringUtils.isNotBlank(directorDetail.getLovDescCustDocCategoryName())) {
				lc = new Listcell(directorDetail.getIdType() + " - " + directorDetail.getLovDescCustDocCategoryName());
			} else {
				lc = new Listcell(directorDetail.getIdType());
			}
			lc.setParent(item);
			lc = new Listcell(directorDetail.getIdReference());
			lc.setParent(item);
			if (StringUtils.isNotBlank(directorDetail.getLovDescNationalityName())) {
				lc = new Listcell(directorDetail.getNationality() + " - " + directorDetail.getLovDescNationalityName());
			} else {
				lc = new Listcell(directorDetail.getNationality());
			}
			lc.setParent(item);
			lc = new Listcell();
			final Checkbox director = new Checkbox();
			director.setDisabled(true);
			director.setChecked(directorDetail.isDirector());
			lc.appendChild(director);
			lc.setParent(item);
			lc = new Listcell(directorDetail.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(directorDetail.getRecordType()));
			lc.setParent(item);

			item.setAttribute("directorId", directorDetail.getDirectorId());
			item.setAttribute("custID", directorDetail.getCustID());
			item.setAttribute("data", directorDetail);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onDirectorDetailItemDoubleClicked");
		}
	}
}
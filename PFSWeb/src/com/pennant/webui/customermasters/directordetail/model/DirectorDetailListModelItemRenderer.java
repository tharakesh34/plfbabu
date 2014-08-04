/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  DirectorDetailListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-12-2011    														*
 *                                                                  						*
 * Modified Date    :  01-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-12-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.webui.customermasters.directordetail.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class DirectorDetailListModelItemRenderer implements ListitemRenderer<DirectorDetail>, Serializable {

	private static final long serialVersionUID = -6611216779270185816L;

	@Override
	public void render(Listitem item, DirectorDetail directorDetail, int count) throws Exception {

		if (item instanceof Listgroup) { 
			item.appendChild(new Listcell(String.valueOf(directorDetail.getLovDescCustCIF()))); 
		} else if (item instanceof Listgroupfoot) { 
			Listcell cell = new Listcell("");
			cell.setSpan(6);
			item.appendChild(cell); 
		} else { 
			String name = "";
            if(!StringUtils.trimToEmpty(directorDetail.getShortName()).equals("")){
            	name = directorDetail.getShortName();
            }else if(!StringUtils.trimToEmpty(directorDetail.getFirstName()).equals("") || !StringUtils.trimToEmpty(directorDetail.getLastName()).equals("")){
            	 name = directorDetail.getFirstName() + "  " + directorDetail.getLastName();
            }
            if (StringUtils.trimToEmpty(directorDetail.getCustAddrCountry()).equals(StringUtils.trimToEmpty(directorDetail.getLovDescCustAddrCountryName()))) {
				String desc = PennantAppUtil.getlabelDesc(directorDetail.getCustAddrCountry(), PennantAppUtil.getCustomerCountryTypesList());
				directorDetail.setLovDescCustAddrCountryName(desc);
			}
			Listcell lc = new Listcell(name);
			lc.setParent(item);
			if(!StringUtils.trimToEmpty(directorDetail.getLovDescCustAddrCountryName()).equals("")){
				lc = new Listcell(directorDetail.getCustAddrCountry()+ " - " +directorDetail.getLovDescCustAddrCountryName());
			}else{
				lc = new Listcell(directorDetail.getCustAddrCountry());
			}
			lc.setParent(item);
			if(directorDetail.getSharePerc() != null){
			lc = new Listcell(String.valueOf(directorDetail.getSharePerc().doubleValue()));
			lc.setParent(item);
			}
			if (StringUtils.trimToEmpty(directorDetail.getIdType()).equals(StringUtils.trimToEmpty(directorDetail.getLovDescCustDocCategoryName()))) {
				String desc = PennantAppUtil.getlabelDesc(directorDetail.getIdType(), PennantAppUtil.getCustomerDocumentTypesList());
				directorDetail.setLovDescCustDocCategoryName(desc);
			}
			if (StringUtils.trimToEmpty(directorDetail.getNationality()).equals(StringUtils.trimToEmpty(directorDetail.getLovDescNationalityName()))) {
				String desc = PennantAppUtil.getlabelDesc(directorDetail.getNationality(), PennantAppUtil.getCustomerCountryTypesList());
				directorDetail.setLovDescNationalityName(desc);
			}
			if(!StringUtils.trimToEmpty(directorDetail.getLovDescCustDocCategoryName()).equals("")){
			lc = new Listcell(directorDetail.getIdType()+" - "+directorDetail.getLovDescCustDocCategoryName());
			}else{
				lc = new Listcell(directorDetail.getIdType());	
			}
			lc.setParent(item);
			lc = new Listcell(directorDetail.getIdReference());
			lc.setParent(item);
			if(!StringUtils.trimToEmpty(directorDetail.getLovDescNationalityName()).equals("")){
			lc = new Listcell(directorDetail.getNationality()+ " - " +directorDetail.getLovDescNationalityName());
			}else{
			lc = new Listcell(directorDetail.getNationality());
			}
			lc.setParent(item);
			lc = new Listcell(directorDetail.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(directorDetail.getRecordType()));
			lc.setParent(item);
			item.setAttribute("data", directorDetail);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onDirectorDetailItemDoubleClicked");
		}
	}
}
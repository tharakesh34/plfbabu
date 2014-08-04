package com.pennant.webui.customermasters.customer.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CustomerDocumentsListModelItemRenderer implements ListitemRenderer<CustomerDocument>, Serializable {

	private static final long serialVersionUID = -4024561676267012282L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, CustomerDocument customerDocument, int count) throws Exception {

		//final CustomerDocument customerDocument = (CustomerDocument) data;
		Listcell lc;
		if (StringUtils.trimToEmpty(customerDocument.getCustDocCategory()).equals(StringUtils.trimToEmpty(customerDocument.getLovDescCustDocCategory()))) {
			String desc = PennantAppUtil.getlabelDesc(customerDocument.getCustDocCategory(), PennantAppUtil.getCustomerDocumentTypesList());
			customerDocument.setLovDescCustDocCategory(desc);
		}	
		lc = new Listcell(customerDocument.getCustDocCategory() + "-" + customerDocument.getLovDescCustDocCategory());
		lc.setParent(item);
		lc = new Listcell(customerDocument.getCustDocTitle());
		lc.setParent(item);
		lc = new Listcell(customerDocument.getLovDescCustDocIssuedCountry());
		lc.setParent(item); 
		lc = new Listcell(customerDocument.getLovDescCustDocVerifiedBy());
		lc.setParent(item); 
		lc = new Listcell(PennantApplicationUtil.formateDate(customerDocument.getCustDocIssuedOn(),PennantConstants.dateFormate));
		lc.setParent(item); 
		lc = new Listcell(PennantApplicationUtil.formateDate(customerDocument.getCustDocExpDate(),PennantConstants.dateFormate));
		lc.setParent(item); 
		lc = new Listcell(customerDocument.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(customerDocument.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", customerDocument);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerDocumentItemDoubleClicked");
	}
}

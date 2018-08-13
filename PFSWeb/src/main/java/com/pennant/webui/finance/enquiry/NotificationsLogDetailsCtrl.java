package com.pennant.webui.finance.enquiry;

import java.util.List;

import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinODDetails;

public class NotificationsLogDetailsCtrl {

	
	protected Window window_OverdueEnquiryDialog; // autoWired
	protected Listbox listBoxOverdue; // autoWired
	protected Borderlayout borderlayoutOverdueEnquiry; // autoWired
	private Tabpanel tabPanel_dialogWindow;

	private FinanceEnquiryHeaderDialogCtrl financeEnquiryHeaderDialogCtrl = null;
	private List<FinODDetails> finODDetailList;
	private int ccyformat = 0; 
	
	
	
	
}

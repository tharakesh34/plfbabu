package com.pennant.webui.batch.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.StepExecution;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.batch.admin.BatchAdminDAO;
import com.pennant.backend.batch.admin.BatchProcess;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseCtrl;
/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Batch/BatchAdmin.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class StepDetailCtrl   extends GFCBaseCtrl implements Serializable {
	
	private static final long serialVersionUID = 4309463490869641570L;
	private final static Logger logger = Logger.getLogger(StepDetailCtrl.class);
	
 	protected Window window_StepDetails;
 	protected Listbox list_box_steps;
 	protected Listhead list_head;
 	protected Label label_valueDate;
 	  	
	private BatchAdminDAO batchAdminDAO;
	ArrayList<BatchProcess> batchDtl = null;
	
	
	private StepExecution stepExecution; 						// over handed per parameter
	private transient BatchAdminCtrl batchAdminCtrl;	 		// overHanded per parameter
		
	public void onCreate$window_StepDetails(Event event) throws Exception {

		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("step")) {
			this.stepExecution = (StepExecution) args.get("step");
			setStepExecution(this.stepExecution);
		} else {
			setStepExecution(null);
		}

		batchDtl = (ArrayList<BatchProcess>)getBatchAdminDAO().getStepDetails(this.stepExecution);

		if (batchDtl != null && batchDtl.size() > 0) {
			prepareHeader(batchDtl);
			for (BatchProcess batchProcess : batchDtl) {				
				render(batchProcess);
			}
		} 
		
		if (batchDtl != null && batchDtl.size() > 0) {
			this.window_StepDetails.setTitle(getStepExecution().getStepName().toUpperCase());
			Date date = null;
			if(stepExecution.getExecutionContext().containsKey(stepExecution.getId().toString())) {
				date =  (java.sql.Date) stepExecution.getExecutionContext().get(stepExecution.getId().toString());	
			}

			String dateValue = DateUtility.formatDate(date !=null ? date : new Date(0000000000), PennantConstants.dateFormat);
			label_valueDate.setValue("Value Date : " + dateValue);
			label_valueDate.setStyle("font-weight :bold;");
			this.window_StepDetails.doModal();
		} else {
			this.window_StepDetails.onClose();
		}
		
		
	}

	public BatchAdminDAO getBatchAdminDAO() {
		return batchAdminDAO;
	}
	public void setBatchAdminDAO(BatchAdminDAO batchAdminDAO) {
		this.batchAdminDAO = batchAdminDAO;
	}

	/**
	 * @param batchProcess
	 * @throws Exception
	 */
	private void render(BatchProcess batchProcess) throws Exception {

		
		Listitem item = null;
		Listcell lc;

		item = new Listitem();
		lc = new Listcell(batchProcess.getFinRef());
		lc.setParent(item);

		if (batchProcess.getDetailFields() != null) {
			String[] details = batchProcess.getDetailFields().split(";");
			for (String dtls : details) {
				if(dtls != null && dtls.split("-").length == 2) {
					lc = new Listcell(StringUtils.trimToEmpty(dtls.split("-")[1]));
					lc.setParent(item);
				}
			}

		}

		item.setAttribute("data", batchProcess);
		list_box_steps.appendChild(item);
	}
	
	private void prepareHeader(ArrayList<BatchProcess> batchDtl) {
		Listheader listheader = null;

		try {
			this.list_head.getFellow("FinRef");
		} catch (Exception e) {
			listheader = new Listheader("FinRef");
			listheader.setParent(this.list_head);
		}

		String details = ((BatchProcess) batchDtl.get(0)).getDetailFields();
		if (batchDtl != null && batchDtl.size() > 0) {
			if (details != null) {
				String[] subDetails = details.split(";");
				for (String dtl : subDetails) {
					if(dtl != null && dtl.length() > 2) {
						try {
							this.list_head.getFellow(dtl.split("-")[0]);

						} catch (Exception e) {
							listheader = new Listheader(dtl.split("-")[0]);
							listheader.setParent(this.list_head);
						}
					}
				}
			}
		}

	}


	public StepExecution getStepExecution() {
		return stepExecution;
	}

	public void setStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	public BatchAdminCtrl getBatchAdminCtrl() {
		return batchAdminCtrl;
	}


	public void setBatchAdminCtrl(BatchAdminCtrl batchAdminCtrl) {
		this.batchAdminCtrl = batchAdminCtrl;
	}
	

	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			this.window_StepDetails.onClose();
		} catch (final WrongValuesException e) {
			logger.error(e);
			this.window_StepDetails.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}
	
	
	

}

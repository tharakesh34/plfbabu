package com.pennant.webui.collateral.collateralsetup;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.CommitmentConstants;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.webui.util.GFCBaseCtrl;

public class CollateralBasicDetailsCtrl extends GFCBaseCtrl<FinanceDetail> {

	private static final long serialVersionUID = -4843661930948561711L;
	private static final Logger logger = Logger.getLogger(CollateralBasicDetailsCtrl.class);
	
	protected Window window_CollateralBasicDetails; 				// autowired
	protected Label colBasic_depositerCif; 							// autoWired
	protected Label colBasic_colRef; 								// autoWired
	protected Label colBasic_depositerName; 						// autoWired
	protected Label colBasic_colCcy; 								// autoWired
	protected Label colBasic_colType; 								// autoWired
	protected Label colBasic_colLoc; 								// autoWired
	
	protected Label label_CollateralBasicDetails_depositerCif;
	protected Label label_CollateralBasicDetails_colRef;
	protected Label label_CollateralBasicDetails_depositerName;
	protected Label label_CollateralBasicDetails_colCcy;
	protected Label label_CollateralBasicDetails_colType;
	protected Label label_CollateralBasicDetails_colLoc;
	
	private Object parentCtrl = null;
	private String moduleName = "";
	
	public CollateralBasicDetailsCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}
	
	@SuppressWarnings("unchecked")
	public void onCreate$window_CollateralBasicDetails(ForwardEvent event)	throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CollateralBasicDetails);

		if (arguments.containsKey("parentCtrl")) {
			parentCtrl = arguments.get("parentCtrl");
			parentCtrl.getClass().getMethod("setCollateralBasicDetailsCtrl", this.getClass()).invoke(parentCtrl, this);
		}
		if (arguments.containsKey("moduleName")) {
			setModuleName((String) arguments.get("moduleName"));
		}
		if (arguments.containsKey("finHeaderList")) {
			doWriteBeanToComponents( (ArrayList<Object>) arguments.get("finHeaderList"));
		}
		logger.debug("Leaving");
	}
	
	public void doWriteBeanToComponents(ArrayList<Object> finHeaderList) {
		logger.debug("Entering");

		this.colBasic_depositerCif.setValue(String.valueOf(finHeaderList.get(0)));
		this.colBasic_colRef.setValue(String.valueOf(finHeaderList.get(1)));
		this.colBasic_depositerName.setValue(String.valueOf(finHeaderList.get(2)));
		this.colBasic_colCcy.setValue(String.valueOf(finHeaderList.get(3)));
		this.colBasic_colType.setValue(String.valueOf(finHeaderList.get(4)));
		this.colBasic_colLoc.setValue(String.valueOf(finHeaderList.get(5)));

		if (StringUtils.equals(getModuleName(), CommitmentConstants.MODULE_NAME)) {

			this.label_CollateralBasicDetails_depositerCif.setValue(Labels.getLabel("label_CommitmentDialog_CustCIF.value"));
			this.label_CollateralBasicDetails_colRef.setValue(Labels.getLabel("label_CommitmentDialog_CmtReference.value"));
			this.label_CollateralBasicDetails_depositerName.setValue(Labels.getLabel("label_CommitmentDialog_CmtCcy.value"));
			this.label_CollateralBasicDetails_colCcy.setValue(Labels.getLabel("label_CommitmentDialog_CmtAmount.value"));
			this.label_CollateralBasicDetails_colType.setValue(Labels.getLabel("label_CommitmentDialog_CmtUtilizedTotAmount.value"));
			this.label_CollateralBasicDetails_colLoc.setValue(Labels.getLabel("label_CommitmentDialog_CmtUnUtilizedAmount.value"));
			
			this.colBasic_colCcy.setStyle("font-weight:bold;padding-left:50px;");
			this.colBasic_colType.setStyle("font-weight:bold;padding-left:50px;");
			this.colBasic_colLoc.setStyle("font-weight:bold;padding-left:50px;");

		} else if (StringUtils.equals(moduleName, CollateralConstants.MODULE_NAME)){
			
			this.label_CollateralBasicDetails_depositerCif.setValue(Labels.getLabel("label_CollateralSetupDialog_DepositorCif.value"));
			this.label_CollateralBasicDetails_colRef.setValue(Labels.getLabel("label_CollateralSetupDialog_CollateralRef.value"));
			this.label_CollateralBasicDetails_depositerName.setValue(Labels.getLabel("label_CollateralSetupDialog_DepositorName.value"));
			this.label_CollateralBasicDetails_colCcy.setValue(Labels.getLabel("label_CollateralSetupDialog_CollateralCcy.value"));
			this.label_CollateralBasicDetails_colType.setValue(Labels.getLabel("label_CollateralSetupDialog_CollateralType.value"));
			this.label_CollateralBasicDetails_colLoc.setValue(Labels.getLabel("label_CollateralSetupDialog_CollateralLoc.value"));
			
		} else if (StringUtils.equals(moduleName, VASConsatnts.MODULE_NAME)){
			this.label_CollateralBasicDetails_depositerCif.setValue(Labels.getLabel("label_VASRecordingDialog_ProductType.value"));
			this.label_CollateralBasicDetails_colRef.setValue(Labels.getLabel("label_VASRecordingDialog_ProductCode.value"));
			this.label_CollateralBasicDetails_depositerName.setValue(Labels.getLabel("label_VASRecordingList_VasReference.value"));
			this.label_CollateralBasicDetails_colCcy.setValue(Labels.getLabel("label_VASRecordingList_PrimaryLinkRef.value"));
			this.label_CollateralBasicDetails_colType.setValue(Labels.getLabel("label_VASConfigurationDialog_VASType.value"));
			this.label_CollateralBasicDetails_colLoc.setValue(Labels.getLabel("label_VASConfigurationDialog_VASCategory.value"));
		}  

		logger.debug("Leaving");
	}
	
	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
}

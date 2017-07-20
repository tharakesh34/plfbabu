package com.pennant.webui.solutionfactory.extendedfielddetail;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.FrequencyUtil;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.staticparms.ExtendedFieldHeader;
import com.pennant.backend.model.staticparms.ExtendedFieldRender;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.collateral.collateralsetup.CollateralBasicDetailsCtrl;
import com.pennant.webui.collateral.collateralsetup.CollateralSetupDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class ExtendedFieldRenderDialogCtrl extends GFCBaseCtrl<ExtendedFieldHeader>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -995824171042829810L;
	private static final Logger logger = Logger.getLogger(ExtendedFieldRenderDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_ExtendedFieldRenderDialog; 
	protected Listbox 		listBoxExtendedFieldRenderdetails;					       
	protected Button        btnNew;
	protected Groupbox		finBasicdetails;

	protected Label 		collateralDepositorCif;
	protected Label			assignmentDetailCollateralRef;
	protected Label			collateralDepositorName;
	protected Label			collateralCurrency;
	protected Label 		assignesCollateralType;
	protected Label			assignedCollateralLoc;
	protected Listhead     	listHead;

	private Object	     	dialogCtrl;	
	private CollateralBasicDetailsCtrl  collateralBasicDetailsCtrl;
	private ExtendedFieldHeader 		extendedFieldHeader;
	private List<ExtendedFieldRender> extendedFieldRenderList;
	private String 				menuItemRightName = null;
	private String 				preValidationScript = null;
	private String 				postValidationScript = null;
	private String 				moduleType = "";
	private String 				moduleName = "";

	/**
	 * default constructor.<br>
	 */
	public ExtendedFieldRenderDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ExtendedFieldRenderDialog";
	}

	/**
	 * Method for creating window dynamically
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_ExtendedFieldRenderDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ExtendedFieldRenderDialog);

		if (arguments.containsKey("dialogCtrl")) {
			setDialogCtrl(arguments.get("dialogCtrl"));
		}
		if (arguments.containsKey("extendedFieldHeader")) {
			setExtendedFieldHeader((ExtendedFieldHeader) arguments.get("extendedFieldHeader"));
		}
		if (arguments.containsKey("moduleName")) {
			moduleName = (String) arguments.get("moduleName");
		}
		if (arguments.containsKey("finHeaderList")) {
			appendFinBasicDetails((ArrayList<Object> )arguments.get("finHeaderList"));
		}
		
		if (arguments.containsKey("menuItemRightName")) {
			menuItemRightName = (String) arguments.get("menuItemRightName");
		}
		if (arguments.containsKey("preValidationScript")) {
			setPreValidationScript((String) arguments.get("preValidationScript"));
		}
		if (arguments.containsKey("postValidationScript")) {
			setPostValidationScript((String) arguments.get("postValidationScript"));
		}
		if (arguments.containsKey("fieldRenderList")) {
			extendedFieldRenderList = (List<ExtendedFieldRender>) arguments.get("fieldRenderList");
		}
		
		if (arguments.containsKey("roleCode")) {
			setRole((String) arguments.get("roleCode"));
			getUserWorkspace().allocateMenuRoleAuthorities(getRole(), "ExtendedFieldRenderDialog", menuItemRightName);	
		}

		if (arguments.containsKey("moduleType")) {
			this.moduleType = (String) arguments.get("moduleType");
		}
		
		//Set the listbox height...
		this.listBoxExtendedFieldRenderdetails.setHeight(borderLayoutHeight - 195 + "px");
		
		doCheckRights();
		
		doWriteBeanToComponents();
		logger.debug("Leaving");
	}

	
	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		if(!PennantConstants.MODULETYPE_ENQ.equals(moduleType)){
			getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldRenderDialog_btnNew"));
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails(ArrayList<Object> finHeaderList) {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", finHeaderList);
			map.put("moduleName", moduleName);
			Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CollateralBasicDetails.zul",this.finBasicdetails, map);
		} catch (Exception e) {
			logger.debug(e);
		}
	}
	
	/**
	 * Setting Basic Details on Header
	 * @param finHeaderList
	 */
	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getCollateralBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	/**
	 * Method for Double Click of Extended Field Details edition
	 * @param event
	 * @throws Exception
	 */
	public void onExtendedFieldItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final Listitem item = this.listBoxExtendedFieldRenderdetails.getSelectedItem();
		if (item != null) {
			
			final ExtendedFieldRender fieldRender = (ExtendedFieldRender) item.getAttribute("data");
			if (StringUtils.equalsIgnoreCase(fieldRender.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("extendedFieldRenderDialogCtrl", this);
				map.put("extendedFieldHeader",extendedFieldHeader);
				map.put("extendedFieldRender",fieldRender);
				map.put("ccyFormat",getFormat());
				map.put("isReadOnly",getUserWorkspace().isAllowed("button_ExtendedFieldRenderDialog_btnNew"));
				map.put("moduleType",moduleType);
				
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/SolutionFactory/ExtendedFieldDetail/ExtendedFieldCaptureDialog.zul",
							window_ExtendedFieldRenderDialog, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Rendering Extended field components dynamically onClick New button
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNew(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		ExtendedFieldRender extendedFieldRender = new ExtendedFieldRender();
		extendedFieldRender.setNewRecord(true);
		
		// Finding Maximum Sequence Number
		int seqNo = 0;
		if(getExtendedFieldRenderList() != null && !getExtendedFieldRenderList().isEmpty()){
			for (int i = 0; i < getExtendedFieldRenderList().size(); i++) {
				ExtendedFieldRender render = getExtendedFieldRenderList().get(i);
				if(seqNo <= render.getSeqNo()){
					seqNo = render.getSeqNo();
				}
			}
		}

		extendedFieldRender.setSeqNo(seqNo+1);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("extendedFieldRenderDialogCtrl", this);
		map.put("extendedFieldHeader",extendedFieldHeader);
		map.put("extendedFieldRender",extendedFieldRender);
		map.put("ccyFormat",getFormat());
		map.put("newRecord",true);
		map.put("preValidationScript",getPreValidationScript());
		map.put("postValidationScript",getPostValidationScript());
		map.put("isReadOnly",getUserWorkspace().isAllowed("button_ExtendedFieldRenderDialog_btnNew"));
		
		Executions.createComponents("/WEB-INF/pages/SolutionFactory/ExtendedFieldDetail/ExtendedFieldCaptureDialog.zul",window_ExtendedFieldRenderDialog,map);

		logger.debug("Leaving");
	}
	
	private int getFormat() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		int ccyFormat = 0;
		if(getDialogCtrl() != null){
			ccyFormat  = (int) getDialogCtrl().getClass().getMethod("getCcyFormat").invoke(getDialogCtrl());
		}
		return ccyFormat;
	}
	/**
	 * Method for writing data from object to fields
	 */
	private void doWriteBeanToComponents() {
		logger.debug("Entering");
		
		// Setting Controller to Main Controller
		try {
			getDialogCtrl().getClass().getMethod("setExtendedFieldRenderDialogCtrl", 
					this.getClass()).invoke(getDialogCtrl(), this);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		
		prepareListHeaders();
		doFillExtendedFieldDetails(getExtendedFieldRenderList());
		logger.debug("Leaving");
	}

	/**
	 * Dynamic Listbox preparation with minimal fields
	 */
	private void prepareListHeaders() {
		logger.debug("Entering");

		if(getExtendedFieldHeader() != null && !getExtendedFieldHeader().getExtendedFieldDetails().isEmpty()){
			List<ExtendedFieldDetail> list = getExtendedFieldHeader().getExtendedFieldDetails();

			for (int i = 0; i < list.size(); i++) {
				
				ExtendedFieldDetail fieldDetail = list.get(i);
				if(i == 0){
					Listheader listHeader = new Listheader("Seq No");
					listHeader.setId("lh_SeqNo");
					listHeader.setHflex("min");
					listHeader.setParent(listHead);
				}

				Listheader listHeader = new Listheader(fieldDetail.getFieldLabel());
				listHeader.setId("lh_"+fieldDetail.getFieldName());
				if(isRightAlign(fieldDetail.getFieldType())){
					listHeader.setStyle("text-align:right");
				}
				listHeader.setHflex("min");
				listHeader.setParent(listHead);

				if((i+1) == 6 || (i == (list.size() -1))){

					listHeader = new Listheader(Labels.getLabel("label.RecordStatus"));
					listHeader.setHflex("min");
					listHeader.setId("lh_status");
					listHeader.setParent(listHead);

					listHeader = new Listheader(Labels.getLabel("label.RecordType"));
					listHeader.setHflex("min");
					listHeader.setId("lh_operation");
					listHeader.setParent(listHead);

					break;
				}
			}
		}

		logger.debug("Leaving");
	}
	
	/**
	 * Method for verification of field is set to Right align or not.
	 * @param fieldType
	 * @return
	 */
	private boolean isRightAlign(String fieldType){
		
		if(StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_ACTRATE) || 
				StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_AMOUNT) ||
				StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_DECIMAL) ||
				StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_INT) ||
				StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_LONG) ||
				StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_PERCENTAGE)){
			return true;
		}
		
		return false;
	}

	/**
	 * Method for Rendering saved dynamic objects into list
	 * @param extendedFieldRenderList
	 */
	public void doFillExtendedFieldDetails(List<ExtendedFieldRender> extendedFieldRenderList) {
		logger.debug("Entering");

		setExtendedFieldRenderList(extendedFieldRenderList);
		this.listBoxExtendedFieldRenderdetails.getItems().clear();
		
		int totalUnits = 0;
		BigDecimal totalValue = BigDecimal.ZERO;
		
		if (extendedFieldRenderList != null && !extendedFieldRenderList.isEmpty()) {
			
			// Identify Type of fields to render with formats
			List<ExtendedFieldDetail> extList = extendedFieldHeader.getExtendedFieldDetails();
			Map<String, ExtendedFieldDetail> fieldTypeMap = new HashMap<>();
			for (int i = 0; i < extList.size(); i++) {
				fieldTypeMap.put(extList.get(i).getFieldName(), extList.get(i));
			}			

			List<Listheader> listHeaders = listBoxExtendedFieldRenderdetails.getListhead().getChildren();
			int format;
			try {
				format = getFormat();
			} catch (Exception e) {
				logger.error(e);
				format = 0;
			} 
			
			// List Rendering
			for (int i = 0; i < extendedFieldRenderList.size(); i++) {
				ExtendedFieldRender fieldValueDetail = extendedFieldRenderList.get(i);
				Map<String, Object> detail = fieldValueDetail.getMapValues();
				
				Listitem item = new Listitem();
				
				// Component identify and data setting
				for (int j = 0; j < listHeaders.size(); j++) {
					
					String fieldName = listHeaders.get(j).getId().replace("lh_", "");
					Object fieldValue = null;
					boolean checkFieldType = true;
					String cellValue = "";
					if(detail.containsKey(fieldName)){
						fieldValue = detail.get(fieldName);
					}else if(StringUtils.equals(fieldName, "SeqNo")){
						cellValue = String.valueOf(fieldValueDetail.getSeqNo());
						checkFieldType = false;
					}else if(StringUtils.equals(fieldName, "status")){
						cellValue = fieldValueDetail.getRecordStatus();
						checkFieldType = false;
					}else if(StringUtils.equals(fieldName, "operation")){
						cellValue = PennantJavaUtil.getLabel(fieldValueDetail.getRecordType());
						checkFieldType = false;
					}
					
					// Set List Cell value based on Field type from above
					Listcell lc = new Listcell();
					String fieldType = "";
					if(fieldTypeMap.containsKey(fieldName) && checkFieldType){
						
						ExtendedFieldDetail fieldDetail = fieldTypeMap.get(fieldName);
						fieldType = fieldDetail.getFieldType();
						
						if(StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_DATE)){
							cellValue = DateUtil.formatToLongDate((Date)fieldValue);
						}else if(StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_DATETIME)){
							cellValue = DateUtil.format((Date)fieldValue,DateFormat.LONG_DATE_TIME);
						}else if(StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_TIME)){
							cellValue =  DateUtil.format((Date)fieldValue,DateFormat.LONG_TIME);
						}else if(StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_INT)){
							cellValue = String.valueOf(fieldValue);
							lc.setStyle("text-align:right");
						}else if(StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_LONG)){
							cellValue = String.valueOf(fieldValue);
							lc.setStyle("text-align:right");
						}else if(StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_STATICCOMBO)){
							if(!StringUtils.equals(String.valueOf(fieldValue), PennantConstants.List_Select)){
								cellValue = String.valueOf(fieldValue);
							}
						}else if(StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_MULTISTATICCOMBO)){
							if(!StringUtils.equals(String.valueOf(fieldValue), PennantConstants.List_Select)){
								cellValue = String.valueOf(fieldValue);
							}
						}else if(StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_ACTRATE)){
							cellValue = PennantApplicationUtil.formatRate(Double.valueOf(String.valueOf(fieldValue)), fieldDetail.getFieldPrec());
							lc.setStyle("text-align:right");
						}else if(StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_DECIMAL)){
							cellValue = PennantApplicationUtil.amountFormate(new BigDecimal(String.valueOf(fieldValue)),fieldDetail.getFieldPrec());
							lc.setStyle("text-align:right");
						}else if(StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_AMOUNT)){
							cellValue = PennantApplicationUtil.amountFormate(new BigDecimal(String.valueOf(fieldValue)), format);
							lc.setStyle("text-align:right");
						}else if(StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_PERCENTAGE)){
							if(fieldValue == null){
								cellValue = PennantApplicationUtil.formatRate(Double.valueOf(0), fieldDetail.getFieldPrec());
							}else{
								cellValue = PennantApplicationUtil.formatRate(Double.valueOf(String.valueOf(fieldValue)), fieldDetail.getFieldPrec());
							}
							lc.setStyle("text-align:right");
						}else if(StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_BOOLEAN)){
							Checkbox checkbox = new Checkbox();
							checkbox.setDisabled(true);
							checkbox.setChecked(Integer.parseInt(fieldValue.toString()) == 1 ? true : false);
							lc.appendChild(checkbox);
						}else if(StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_FRQ)){
							cellValue = FrequencyUtil.getFrequencyDetail(String.valueOf(fieldValue)).getFrequencyDescription();
						}else if(StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_BASERATE)){
							cellValue = detail.get(fieldName+"_BR") +"/"+detail.get(fieldName+"_SR") +"/"+ 
											PennantApplicationUtil.formatRate(Double.valueOf(String.valueOf(detail.get(fieldName+"_MR"))), 9);
						}else{
							cellValue = String.valueOf(fieldValue == null ? "" : fieldValue);
						}
					}
					
					//Setting Value to List Cell
					if(!StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_BOOLEAN)){
						lc.setLabel(cellValue);
					}
					
					item.appendChild(lc);
				}
				item.setAttribute("data", fieldValueDetail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onExtendedFieldItemDoubleClicked");
				listBoxExtendedFieldRenderdetails.appendChild(item);
				
				// Setting Number of units
				int noOfUnits = 0;
				if(detail.containsKey("NOOFUNITS")){
					noOfUnits = Integer.parseInt(detail.get("NOOFUNITS").toString());
				}
				
				// Setting Total Value
				BigDecimal curValue = BigDecimal.ZERO;
				if(detail.containsKey("UNITPRICE")){
					curValue = new BigDecimal(detail.get("UNITPRICE").toString());
				}
				
				//Total Number of Units
				totalUnits = totalUnits + noOfUnits;
				totalValue = totalValue.add(curValue.multiply(new BigDecimal(noOfUnits)));
				
			}
			
			if(getDialogCtrl() instanceof CollateralSetupDialogCtrl) {
				CollateralSetupDialogCtrl csd = (CollateralSetupDialogCtrl)getDialogCtrl();
				csd.setExtendedFieldRenderList(extendedFieldRenderList);
				
				csd.setDeafultValues(totalUnits, totalValue);
				setDialogCtrl(csd);
			}
		}
		logger.debug("Leaving");
	}
		
	public List<ExtendedFieldRender> getExtendedFieldRenderList() {
		return extendedFieldRenderList;
	}
	public void setExtendedFieldRenderList(List<ExtendedFieldRender> extendedFieldRenderList) {
		this.extendedFieldRenderList = extendedFieldRenderList;
	}
	
	public Object getDialogCtrl() {
		return dialogCtrl;
	}
	public void setDialogCtrl(Object dialogCtrl) {
		this.dialogCtrl = dialogCtrl;
	}

	public ExtendedFieldHeader getExtendedFieldHeader() {
		return extendedFieldHeader;
	}
	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}

	public CollateralBasicDetailsCtrl getCollateralBasicDetailsCtrl() {
		return collateralBasicDetailsCtrl;
	}
	public void setCollateralBasicDetailsCtrl(CollateralBasicDetailsCtrl collateralBasicDetailsCtrl) {
		this.collateralBasicDetailsCtrl = collateralBasicDetailsCtrl;
	}

	public String getPreValidationScript() {
		return preValidationScript;
	}

	public void setPreValidationScript(String preValidationScript) {
		this.preValidationScript = preValidationScript;
	}

	public String getPostValidationScript() {
		return postValidationScript;
	}

	public void setPostValidationScript(String postValidationScript) {
		this.postValidationScript = postValidationScript;
	}

}

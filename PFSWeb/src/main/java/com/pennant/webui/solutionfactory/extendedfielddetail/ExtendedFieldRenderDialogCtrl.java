package com.pennant.webui.solutionfactory.extendedfielddetail;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.collateral.collateralsetup.CollateralBasicDetailsCtrl;
import com.pennant.webui.collateral.collateralsetup.CollateralSetupDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class ExtendedFieldRenderDialogCtrl extends GFCBaseCtrl<ExtendedFieldHeader> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -995824171042829810L;
	private static final Logger logger = LogManager.getLogger(ExtendedFieldRenderDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ExtendedFieldRenderDialog;
	protected Listbox listBoxExtendedFieldRenderdetails;
	protected Button btnNew;
	protected Groupbox finBasicdetails;

	protected Label collateralDepositorCif;
	protected Label assignmentDetailCollateralRef;
	protected Label collateralDepositorName;
	protected Label collateralCurrency;
	protected Label assignesCollateralType;
	protected Label assignedCollateralLoc;
	protected Listhead listHead;

	private Object dialogCtrl;
	private CollateralBasicDetailsCtrl collateralBasicDetailsCtrl;
	private ExtendedFieldHeader extendedFieldHeader;
	private List<ExtendedFieldRender> extendedFieldRenderList;
	private String menuItemRightName = null;
	private String preValidationScript = null;
	private String postValidationScript = null;
	private String moduleType = "";
	private String moduleName = "";
	private String querySubCode = "";
	private String queryCode = "";
	private long queryId;
	private BigDecimal currentValue;
	private boolean isCommodity = false;

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
	 * 
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
			appendFinBasicDetails((ArrayList<Object>) arguments.get("finHeaderList"));
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
		if (arguments.containsKey("queryId")) {
			this.queryId = (long) arguments.get("queryId");
		}
		if (arguments.containsKey("querySubCode")) {
			this.querySubCode = (String) arguments.get("querySubCode");
		}

		if (arguments.containsKey("queryCode")) {
			this.queryCode = (String) arguments.get("queryCode");
		}
		if (arguments.containsKey("roleCode")) {
			setRole((String) arguments.get("roleCode"));
			getUserWorkspace().allocateMenuRoleAuthorities(getRole(), "ExtendedFieldRenderDialog", menuItemRightName);
		}

		if (arguments.containsKey("moduleType")) {
			this.moduleType = (String) arguments.get("moduleType");
		}

		if (arguments.containsKey("currentValue")) {
			if ((BigDecimal) arguments.get("currentValue") != null) {
				this.currentValue = (BigDecimal) arguments.get("currentValue");
			}

		}

		if (arguments.containsKey("isCommodity")) {
			this.isCommodity = (boolean) arguments.get("isCommodity");
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
		if (!PennantConstants.MODULETYPE_ENQ.equals(moduleType)) {
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
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", finHeaderList);
			map.put("moduleName", moduleName);
			Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CollateralBasicDetails.zul",
					this.finBasicdetails, map);
		} catch (Exception e) {
			logger.debug(e);
		}
	}

	/**
	 * Setting Basic Details on Header
	 * 
	 * @param finHeaderList
	 */
	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getCollateralBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	/**
	 * Method for Double Click of Extended Field Details edition
	 * 
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
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("extendedFieldRenderDialogCtrl", this);
				map.put("extendedFieldHeader", extendedFieldHeader);
				map.put("extendedFieldRender", fieldRender);
				map.put("ccyFormat", getFormat());
				map.put("isReadOnly", getUserWorkspace().isAllowed("button_ExtendedFieldRenderDialog_btnNew"));
				map.put("moduleType", moduleType);
				map.put("queryId", this.queryId);
				map.put("querySubCode", this.querySubCode);
				map.put("queryCode", this.queryCode);
				map.put("currentValue", this.currentValue);
				map.put("isCommodity", this.isCommodity);
				if (this.isCommodity) {
					map.put("hsnCodes", getSelectedHSNCodes(getHSNCodeValue(fieldRender.getMapValues())));
				}
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents(
							"/WEB-INF/pages/SolutionFactory/ExtendedFieldDetail/ExtendedFieldCaptureDialog.zul",
							window_ExtendedFieldRenderDialog, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	private List<String> getSelectedHSNCodes(String currentCode) {
		List<String> hsnCodes = new ArrayList<>();

		if (CollectionUtils.isEmpty(getExtendedFieldRenderList())) {
			return hsnCodes;
		}

		for (ExtendedFieldRender render : getExtendedFieldRenderList()) {
			if (currentCode != null) {
				continue;
			}

			hsnCodes.add(getHSNCodeValue(render.getMapValues()));
		}

		return hsnCodes;
	}

	private String getHSNCodeValue(Map<String, Object> values) {
		if (values.containsKey("HSNCODE")) {
			return values.get("HSNCODE").toString();
		}

		return null;
	}

	/**
	 * Method for Rendering Extended field components dynamically onClick New button
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNew(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ExtendedFieldRender extendedFieldRender = new ExtendedFieldRender();
		extendedFieldRender.setNewRecord(true);

		// Finding Maximum Sequence Number
		int seqNo = 0;
		if (getExtendedFieldRenderList() != null && !getExtendedFieldRenderList().isEmpty()) {
			for (int i = 0; i < getExtendedFieldRenderList().size(); i++) {
				ExtendedFieldRender render = getExtendedFieldRenderList().get(i);
				if (seqNo <= render.getSeqNo()) {
					seqNo = render.getSeqNo();
				}
			}
		}

		extendedFieldRender.setSeqNo(seqNo + 1);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("extendedFieldRenderDialogCtrl", this);
		map.put("extendedFieldHeader", extendedFieldHeader);
		map.put("extendedFieldRender", extendedFieldRender);
		map.put("ccyFormat", getFormat());
		map.put("newRecord", true);
		map.put("preValidationScript", getPreValidationScript());
		map.put("postValidationScript", getPostValidationScript());
		map.put("isReadOnly", getUserWorkspace().isAllowed("button_ExtendedFieldRenderDialog_btnNew"));
		map.put("queryId", this.queryId);
		map.put("querySubCode", this.querySubCode);
		map.put("queryCode", this.queryCode);
		map.put("currentValue", this.currentValue);
		map.put("isCommodity", this.isCommodity);
		if (this.isCommodity) {
			map.put("hsnCodes", getSelectedHSNCodes(null));
		}

		Executions.createComponents("/WEB-INF/pages/SolutionFactory/ExtendedFieldDetail/ExtendedFieldCaptureDialog.zul",
				window_ExtendedFieldRenderDialog, map);

		logger.debug("Leaving");
	}

	private int getFormat() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		int ccyFormat = 0;
		if (getDialogCtrl() != null) {
			ccyFormat = (int) getDialogCtrl().getClass().getMethod("getCcyFormat").invoke(getDialogCtrl());
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
			getDialogCtrl().getClass().getMethod("setExtendedFieldRenderDialogCtrl", this.getClass())
					.invoke(getDialogCtrl(), this);
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

		Listheader listHeader = new Listheader("Seq No");
		listHeader.setId("lh_SeqNo");
		listHeader.setHflex("min");
		listHeader.setParent(listHead);

		if (getExtendedFieldHeader() != null && !getExtendedFieldHeader().getExtendedFieldDetails().isEmpty()) {
			List<ExtendedFieldDetail> list = getExtendedFieldHeader().getExtendedFieldDetails();
			int columnNo = 0;

			for (ExtendedFieldDetail fieldDetail : list) {
				if (!fieldDetail.isInputElement()) {
					continue;
				}

				listHeader = new Listheader(fieldDetail.getFieldLabel());
				listHeader.setId("lh_" + fieldDetail.getFieldName());
				if (isRightAlign(fieldDetail.getFieldType())) {
					listHeader.setStyle("text-align:right");
				}
				listHeader.setHflex("min");
				listHeader.setParent(listHead);
				columnNo++;

				if (columnNo == 6) {
					break;
				}
			}
		}

		listHeader = new Listheader(Labels.getLabel("label.RecordStatus"));
		listHeader.setId("lh_status");
		listHeader.setHflex("min");
		listHeader.setParent(listHead);

		listHeader = new Listheader(Labels.getLabel("label.RecordType"));
		listHeader.setId("lh_operation");
		listHeader.setHflex("min");
		listHeader.setParent(listHead);

		logger.debug("Leaving");
	}

	/**
	 * Method for verification of field is set to Right align or not.
	 * 
	 * @param fieldType
	 * @return
	 */
	private boolean isRightAlign(String fieldType) {

		if (StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_ACTRATE)
				|| StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_AMOUNT)
				|| StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_DECIMAL)
				|| StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_INT)
				|| StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_LONG)
				|| StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_PERCENTAGE)) {
			return true;
		}

		return false;
	}

	/**
	 * Method for Rendering saved dynamic objects into list
	 * 
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
					if (detail.containsKey(fieldName)) {
						fieldValue = detail.get(fieldName);
					} else if (StringUtils.equals(fieldName, "SeqNo")) {
						cellValue = String.valueOf(fieldValueDetail.getSeqNo());
						checkFieldType = false;
					} else if (StringUtils.equals(fieldName, "status")) {
						cellValue = fieldValueDetail.getRecordStatus();
						checkFieldType = false;
					} else if (StringUtils.equals(fieldName, "operation")) {
						cellValue = PennantJavaUtil.getLabel(fieldValueDetail.getRecordType());
						checkFieldType = false;
					}

					// Set List Cell value based on Field type from above
					Listcell lc = new Listcell();
					String fieldType = "";
					if (fieldTypeMap.containsKey(fieldName) && checkFieldType) {
						ExtendedFieldDetail fieldDetail = fieldTypeMap.get(fieldName);
						fieldType = fieldDetail.getFieldType();

						switch (fieldType) {
						case ExtendedFieldConstants.FIELDTYPE_DATE:
							Date date = null;
							if (fieldValue != null && fieldValue instanceof Date) {
								date = (Date) fieldValue;
							}
							cellValue = DateUtil.formatToLongDate(date);
							break;
						case ExtendedFieldConstants.FIELDTYPE_DATETIME:
							Date dateTime = null;
							if (fieldValue != null && fieldValue instanceof Date) {
								dateTime = (Date) fieldValue;
							}
							cellValue = DateUtil.format(dateTime, DateFormat.LONG_DATE_TIME);
							break;
						case ExtendedFieldConstants.FIELDTYPE_TIME:
							Date time = null;
							if (fieldValue != null && fieldValue instanceof Date) {
								time = (Date) fieldValue;
							}

							cellValue = DateUtil.format(time, DateFormat.LONG_TIME);
							break;
						case ExtendedFieldConstants.FIELDTYPE_INT:
							String integer = "0";
							if (fieldValue != null) {
								integer = String.valueOf(fieldValue);
							}

							cellValue = integer;

							lc.setStyle("text-align:right");
							break;
						case ExtendedFieldConstants.FIELDTYPE_LONG:
							cellValue = String.valueOf(fieldValue);
							lc.setStyle("text-align:right");
							break;
						case ExtendedFieldConstants.FIELDTYPE_STATICCOMBO:
							if (!StringUtils.equals(String.valueOf(fieldValue), PennantConstants.List_Select)) {
								cellValue = String.valueOf(fieldValue);
							}
							break;
						case ExtendedFieldConstants.FIELDTYPE_MULTISTATICCOMBO:
							if (!StringUtils.equals(String.valueOf(fieldValue), PennantConstants.List_Select)) {
								cellValue = String.valueOf(fieldValue);
							}
							break;
						case ExtendedFieldConstants.FIELDTYPE_ACTRATE:
							Double rate = Double.valueOf("0");
							if (fieldValue != null) {
								rate = Double.valueOf(String.valueOf(fieldValue));
							}

							cellValue = PennantApplicationUtil.formatRate(rate, fieldDetail.getFieldPrec());
							lc.setStyle("text-align:right");
							break;
						case ExtendedFieldConstants.FIELDTYPE_DECIMAL:
							BigDecimal decimal = BigDecimal.ZERO;
							if (fieldValue != null) {
								decimal = new BigDecimal(String.valueOf(fieldValue));
							}

							cellValue = PennantApplicationUtil.amountFormate(decimal, fieldDetail.getFieldPrec());

							lc.setStyle("text-align:right");
							break;
						case ExtendedFieldConstants.FIELDTYPE_AMOUNT:
							BigDecimal amount = BigDecimal.ZERO;
							if (fieldValue != null) {
								amount = new BigDecimal(String.valueOf(fieldValue));
							}

							cellValue = PennantApplicationUtil.amountFormate(amount, format);
							lc.setStyle("text-align:right");
							break;
						case ExtendedFieldConstants.FIELDTYPE_PERCENTAGE:
							Double percentage = Double.valueOf("0");
							if (fieldValue != null) {
								percentage = Double.valueOf(String.valueOf(fieldValue));
							}

							cellValue = PennantApplicationUtil.formatRate(percentage, fieldDetail.getFieldPrec());
							lc.setStyle("text-align:right");
							break;
						case ExtendedFieldConstants.FIELDTYPE_BOOLEAN:
							Checkbox checkbox = new Checkbox();
							checkbox.setDisabled(true);
							if (fieldValue != null) {
								if (fieldValue instanceof Boolean) {
									checkbox.setChecked((boolean) fieldValue);
								} else {
									checkbox.setChecked(Integer.parseInt(fieldValue.toString()) == 1 ? true : false);
								}
							}
							lc.appendChild(checkbox);
							break;
						case ExtendedFieldConstants.FIELDTYPE_FRQ:
							if (fieldValue == null) {
								fieldValue = "";
							}
							cellValue = FrequencyUtil.getFrequencyDetail(fieldValue.toString())
									.getFrequencyDescription();
							break;
						case ExtendedFieldConstants.FIELDTYPE_BASERATE:
							cellValue = detail.get(fieldName + "_BR") + "/" + detail.get(fieldName + "_SR") + "/"
									+ PennantApplicationUtil.formatRate(
											Double.valueOf(String.valueOf(detail.get(fieldName + "_MR"))), 9);
							break;
						default:
							cellValue = String.valueOf(fieldValue == null ? "" : fieldValue);
							break;
						}

					}

					//Setting Value to List Cell
					if (!ExtendedFieldConstants.FIELDTYPE_BOOLEAN.equals(fieldType)) {
						lc.setLabel(cellValue);
					}

					item.appendChild(lc);
				}

				item.setAttribute("data", fieldValueDetail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onExtendedFieldItemDoubleClicked");
				listBoxExtendedFieldRenderdetails.appendChild(item);

				// Setting Number of units
				int noOfUnits = 0;
				if (detail.containsKey("NOOFUNITS")) {
					noOfUnits = Integer.parseInt(detail.get("NOOFUNITS").toString());
				}

				// Setting Total Value
				BigDecimal curValue = BigDecimal.ZERO;
				if (detail.containsKey("UNITPRICE")) {
					curValue = new BigDecimal(detail.get("UNITPRICE").toString());
				}

				//Total Number of Units
				totalUnits = totalUnits + noOfUnits;
				totalValue = totalValue.add(curValue.multiply(new BigDecimal(noOfUnits)));

			}

			if (getDialogCtrl() instanceof CollateralSetupDialogCtrl) {
				CollateralSetupDialogCtrl csd = (CollateralSetupDialogCtrl) getDialogCtrl();
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

package com.pennant.webui.util.constraint;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTEmailValidator;
import com.pennant.util.Constraint.PTMobileNumberValidator;
import com.pennant.util.Constraint.PTPhoneNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.PTWebValidator;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class AdditionalDetailValidation implements Serializable {

	private static final long	serialVersionUID	= 7801946116526475384L;
	private final static Logger logger = Logger.getLogger(AdditionalDetailValidation.class);

	public AdditionalDetailValidation() {
		
	}
	
	public void doPrepareAdditionalDetails(HashMap<String, Object> lovDescExtendedFieldValues,
			List<ExtendedFieldDetail> extendedFieldDetailList,Window dialogWindow,
			Rows rowsAddlDetail, int columnCount, boolean isReadOnly) throws ParseException{
		
		logger.debug("Entering");

		if (extendedFieldDetailList != null && extendedFieldDetailList.size() != 0) {

			Row row = null;
			Space space = null;
			Textbox textbox = null;
			Datebox datebox = null;
			Timebox timebox = null;
			Decimalbox decimalbox = null;
			Combobox combobox = null;
			Comboitem comboitem = null;
			Hbox hbox = null;
			Button button = null;
			Checkbox checkbox = null;
			Radiogroup radiogroup = null;
			Radio radio = null;

			for (int i = 0; i < extendedFieldDetailList.size(); i++) {
				ExtendedFieldDetail detail = extendedFieldDetailList.get(i);

				if(columnCount == 2){
					if(i%2 == 0){						
						row = new Row();
					}
				}else{
					row = new Row();
				}

				if ("TXT".equals(detail.getFieldType().trim()) || "MTXT".equals(detail.getFieldType().trim())) {

					row.appendChild(new Label(detail.getFieldLabel()));
					space = new Space();
					space.setWidth("2px");
					if (detail.isFieldMandatory() && !isReadOnly) {
						space.setSclass("mandatory");
					}

					textbox = new Textbox();
					textbox.setId("ad_"+detail.getFieldName());
					if(lovDescExtendedFieldValues.containsKey(detail.getFieldName()) &&
							lovDescExtendedFieldValues.get(detail.getFieldName()) != null &&
							StringUtils.isNotBlank(lovDescExtendedFieldValues.get(detail.getFieldName()).toString())){
						textbox.setValue(lovDescExtendedFieldValues.get(detail.getFieldName()).toString());
					} else if(StringUtils.isNotBlank(detail.getFieldDefaultValue())){
						textbox.setValue(detail.getFieldDefaultValue());
					}

					if("MTXT".equals(detail.getFieldType().trim())){
						textbox.setRows(3);
					}
					
					if(detail.getFieldLength() <= 20){
						textbox.setWidth(detail.getFieldLength()*10+"px");
					}else{
						textbox.setWidth("250px");
					}
					textbox.setMaxlength(detail.getFieldLength());
					
					if(isReadOnly){
						textbox.setReadonly(true);
					}

					row.appendChild(space);
					row.appendChild(textbox);

				} else if ("DATE".equals(detail.getFieldType().trim())
						|| "DATETIME".equals(detail.getFieldType().trim())
						|| "TIME".equals(detail.getFieldType().trim())) {

					row.appendChild(new Label(detail.getFieldLabel()));
					space = new Space();
					space.setWidth("2px");
					if (detail.isFieldMandatory() && !isReadOnly) {
						space.setSclass("mandatory");
					}

					if ("DATE".equals(detail.getFieldType().trim())){
						datebox = new Datebox();
						datebox.setFormat(DateFormat.SHORT_DATE.getPattern());
						datebox.setId("ad_"+detail.getFieldName());
						datebox.setWidth("100px");
						if(lovDescExtendedFieldValues.containsKey(detail.getFieldName()) && 
								lovDescExtendedFieldValues.get(detail.getFieldName()) != null && 
								StringUtils.isNotBlank(lovDescExtendedFieldValues.get(detail.getFieldName()).toString())){
							
							SimpleDateFormat formatter = new SimpleDateFormat(PennantConstants.DBDateTimeFormat);
							Date date = (Date)formatter.parse(lovDescExtendedFieldValues.get(detail.getFieldName()).toString());  
							datebox.setValue(date);
						} 
						
						if(isReadOnly){
							datebox.setButtonVisible(false);
							datebox.setDisabled(true);
						}

						row.appendChild(space);
						row.appendChild(datebox);
					}

					if ("DATETIME".equals(detail.getFieldType().trim())){
						datebox = new Datebox();
						datebox.setFormat(PennantConstants.dateTimeFormat);
						datebox.setId("ad_"+detail.getFieldName());
						datebox.setWidth("150px");
						if(lovDescExtendedFieldValues.containsKey(detail.getFieldName()) &&
								lovDescExtendedFieldValues.get(detail.getFieldName()) != null && 
								StringUtils.isNotBlank(lovDescExtendedFieldValues.get(detail.getFieldName()).toString())){
							
							SimpleDateFormat formatter = new SimpleDateFormat(PennantConstants.DBDateTimeFormat);
							Date date = (Date)formatter.parse(lovDescExtendedFieldValues.get(detail.getFieldName()).toString());  
							datebox.setValue(date);
						}
						
						if(isReadOnly){
							datebox.setButtonVisible(false);
							datebox.setDisabled(true);
						}
						row.appendChild(space);
						row.appendChild(datebox);
					}

					if ("TIME".equals(detail.getFieldType().trim())){

						timebox = new Timebox();
						timebox.setFormat(PennantConstants.timeFormat);
						timebox.setId("ad_"+detail.getFieldName());
						timebox.setWidth("80px");
						if(lovDescExtendedFieldValues.containsKey(detail.getFieldName()) && 
								lovDescExtendedFieldValues.get(detail.getFieldName()) != null && 
								StringUtils.isNotBlank(lovDescExtendedFieldValues.get(detail.getFieldName()).toString())){
							SimpleDateFormat formatter = new SimpleDateFormat(PennantConstants.DBDateTimeFormat);
							Date date = (Date)formatter.parse(lovDescExtendedFieldValues.get(detail.getFieldName()).toString());  
							timebox.setValue(date);
						}
						
						if(isReadOnly){
							timebox.setButtonVisible(false);
							timebox.setDisabled(true);
						}
						row.appendChild(space);
						row.appendChild(timebox);
					}

				} else if ("RATE".equals(detail.getFieldType().trim())
						|| "PRCT".equals(detail.getFieldType().trim())
						|| "AMT".equals(detail.getFieldType().trim())
						|| "NUMERIC".equals(detail.getFieldType().trim())) {

					row.appendChild(new Label(detail.getFieldLabel()));
					space = new Space();
					space.setWidth("2px");
					if (detail.isFieldMandatory() && !isReadOnly) {
						space.setSclass("mandatory");
					}

					decimalbox = new Decimalbox();
					decimalbox.setStyle("text-align:right");
					decimalbox.setId("ad_"+detail.getFieldName());
					decimalbox.setWidth(detail.getFieldLength()*10+"px");
					decimalbox.setMaxlength(detail.getFieldLength()+1);
					decimalbox.setScale(detail.getFieldPrec());
					
					if(isReadOnly){
						decimalbox.setDisabled(true);
					}
					
					if ("RATE".equals(detail.getFieldType().trim())){
						decimalbox.setFormat(PennantApplicationUtil.getRateFormate(detail.getFieldPrec()));
					} else if("AMT".equals(detail.getFieldType().trim())){
						decimalbox.setFormat(PennantApplicationUtil.getAmountFormate(detail.getFieldPrec()));
					} else if("PRCT".equals(detail.getFieldType().trim())){
						decimalbox.setFormat(PennantApplicationUtil.getAmountFormate(detail.getFieldPrec()));
					} else if ("NUMERIC".equals(detail.getFieldType().trim())) {
						decimalbox.setFormat(PennantConstants.defaultNoFormate);
						decimalbox.setMaxlength(detail.getFieldLength());
					}
					
					if(lovDescExtendedFieldValues.containsKey(detail.getFieldName()) && 
							lovDescExtendedFieldValues.get(detail.getFieldName()) != null &&
							StringUtils.isNotBlank(lovDescExtendedFieldValues.get(detail.getFieldName()).toString())){
						if("AMT".equals(detail.getFieldType().trim())){
							decimalbox.setValue(PennantApplicationUtil.formateAmount(new BigDecimal(lovDescExtendedFieldValues.get(
									detail.getFieldName()).toString()),detail.getFieldPrec()));
						}else {
							decimalbox.setValue(new BigDecimal(lovDescExtendedFieldValues.get(detail.getFieldName()).toString()));
						}
					} else if(StringUtils.isNotBlank(detail.getFieldDefaultValue())){
						if("AMT".equals(detail.getFieldType().trim())){
							decimalbox.setValue(PennantApplicationUtil.formateAmount(new BigDecimal(detail.getFieldDefaultValue()),
									detail.getFieldPrec()));
						}else {
							decimalbox.setValue(new BigDecimal(detail.getFieldDefaultValue()));
						}
					} else{
						decimalbox.setValue(BigDecimal.ZERO);
					}

					row.appendChild(space);
					row.appendChild(decimalbox);

				} else if ("SLIST".equals(detail.getFieldType().trim())) {

					row.appendChild(new Label(detail.getFieldLabel()));
					space = new Space();
					space.setWidth("2px");
					if (detail.isFieldMandatory() && !isReadOnly) {
						space.setSclass("mandatory");
					} 

					combobox = new Combobox();
					comboitem = new Comboitem();
					combobox.setId("ad_"+detail.getFieldName());
					if(detail.getFieldLength() < 10){
						combobox.setWidth("100px");
					}else{
						combobox.setWidth(detail.getFieldLength()*10+"px");
					}

					comboitem.setValue("#");
					comboitem.setLabel(Labels.getLabel("Combo.Select"));
					combobox.appendChild(comboitem);
					combobox.setReadonly(true);
					combobox.setSelectedItem(comboitem);
					
					if(isReadOnly){
						combobox.setDisabled(true);
					}

					String[] staticList = detail.getFieldList().split(",");
					for (int j = 0; j < staticList.length; j++) {

						comboitem = new Comboitem();
						comboitem.setValue(staticList[j]);
						comboitem.setLabel(staticList[j]);
						combobox.appendChild(comboitem);

						if(lovDescExtendedFieldValues.containsKey(detail.getFieldName()) && 
								lovDescExtendedFieldValues.get(detail.getFieldName()) != null &&
								StringUtils.trimToEmpty(lovDescExtendedFieldValues.get(detail.getFieldName()).toString()).equals(
										staticList[j])){
							combobox.setSelectedItem(comboitem);
						}
					}

					row.appendChild(space);
					row.appendChild(combobox);

				} else if ("DLIST".equals(detail.getFieldType().trim()) || 
						"DMLIST".equals(detail.getFieldType().trim())) {

					row.appendChild(new Label(detail.getFieldLabel()));
					space = new Space();
					space.setWidth("2px");
					if (detail.isFieldMandatory() && !isReadOnly) {
						space.setSclass("mandatory");
					} 

					List<Object> list =new ArrayList<Object>();

					hbox = new Hbox();
					textbox =  new Textbox();
					textbox.setId("ad_"+detail.getFieldName());
					textbox.setReadonly(true);
					if(lovDescExtendedFieldValues.containsKey(detail.getFieldName()) &&
							lovDescExtendedFieldValues.get(detail.getFieldName()) != null &&
							StringUtils.isNotBlank(lovDescExtendedFieldValues.get(detail.getFieldName()).toString())){
						textbox.setValue(lovDescExtendedFieldValues.get(detail.getFieldName()).toString());
					}
					hbox.appendChild(textbox);
					list.add(textbox);

					button =  new Button();
					button.setImage("/images/icons/search.png");
					list.add(detail);

					hbox.appendChild(button);
					
					if(isReadOnly){
						button.setVisible(false);
					}else{
						button.addForward("onClick", dialogWindow, "onLookUpButtonClicked", list);
					}

					row.appendChild(space);
					row.appendChild(hbox);

				} else if ("CHKB".equals(detail.getFieldType().trim())) {

					row.appendChild(new Label(detail.getFieldLabel()));
					space = new Space();
					space.setWidth("2px");
					checkbox = new Checkbox();
					checkbox.setId("ad_"+detail.getFieldName());

					if(lovDescExtendedFieldValues.containsKey(detail.getFieldName()) && 
							lovDescExtendedFieldValues.get(detail.getFieldName()) != null && 
							StringUtils.isNotBlank(lovDescExtendedFieldValues.get(detail.getFieldName()).toString())){
						checkbox.setChecked(Integer.parseInt(lovDescExtendedFieldValues.get(detail.getFieldName()).toString()) == 1 ? true:false);
					}

					if(isReadOnly){
						checkbox.setDisabled(true);
					}
					row.appendChild(space);
					row.appendChild(checkbox);

				} else if ("RADIO".equals(detail.getFieldType().trim())) {

					row.appendChild(new Label(detail.getFieldLabel()));
					space = new Space();
					space.setWidth("2px");
					if (detail.isFieldMandatory() && !isReadOnly) {
						space.setSclass("mandatory");
					} 

					radiogroup = new Radiogroup();
					radiogroup.setId("ad_"+detail.getFieldName());
					String[] radiofields = detail.getFieldList().split(",");
					for (int j = 0; j < radiofields.length; j++) {
						radio = new Radio();
						radio.setLabel(radiofields[j]);
						radio.setValue(radiofields[j]);
						
						if(isReadOnly){
							radio.setDisabled(true);
						}

						if(lovDescExtendedFieldValues.containsKey(detail.getFieldName()) && 
								lovDescExtendedFieldValues.get(detail.getFieldName()) != null && 
								StringUtils.isNotBlank(lovDescExtendedFieldValues.get(detail.getFieldName()).toString()) &&
								StringUtils.trimToEmpty(lovDescExtendedFieldValues.get(detail.getFieldName()).toString()).equals(radiofields[j])){
							radio.setChecked(true);
						} else {
							radio.setChecked(false);
						}
						radiogroup.appendChild(radio);
					}
					
					row.appendChild(space);
					row.appendChild(radiogroup);
				}
				rowsAddlDetail.appendChild(row);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to set validation & Save for Additional Field Details 
	 * @param extendedFieldDetailList
	 * @param rowsAddlDetail
	 */
	public FinanceDetail doSaveAdditionFieldDetails(FinanceDetail financeDetail ,Rows rowsAddlDetail,
			ArrayList<WrongValueException> wve,Tab tab,boolean isReadOnly) {
		
		logger.debug("Entering");

		if(financeDetail.getExtendedFieldHeader() !=null && financeDetail.getExtendedFieldHeader().getExtendedFieldDetails() != null){
			List<ExtendedFieldDetail> extendedFieldDetailList = financeDetail.getExtendedFieldHeader().getExtendedFieldDetails();
			List<Component> compList = new ArrayList<Component>();
			if (extendedFieldDetailList != null && extendedFieldDetailList.size() > 0) {

				for (int i = 0; i < extendedFieldDetailList.size(); i++) {
					ExtendedFieldDetail detail = extendedFieldDetailList.get(i);

					if (rowsAddlDetail.getFellowIfAny("ad_"+ detail.getFieldName()) != null) {

						Component component = rowsAddlDetail.getFellowIfAny("ad_"+ detail.getFieldName());
						compList.add(component);

						if (component instanceof Decimalbox) {
							Decimalbox decimalbox = (Decimalbox) component;
							decimalbox.setConstraint("");
							decimalbox.setErrorMessage("");
							if(!isReadOnly && (detail.isFieldMandatory() || decimalbox.getValue() != null)){
								decimalValidation(decimalbox, detail);
							}

							try {
								BigDecimal decimalValue = BigDecimal.ZERO;
								if("AMT".equals(detail.getFieldType())){
									decimalValue = PennantApplicationUtil.unFormateAmount(decimalbox.getValue(), detail.getFieldPrec());
								}else{
									decimalValue = decimalbox.getValue();
								}

								financeDetail.setLovDescExtendedFieldValues(detail.getFieldName(),decimalValue);
							} catch (WrongValueException we) {
								wve.add(we);
							}

						} else if (component instanceof Timebox) {
							Timebox timebox = (Timebox) component;
							timebox.setConstraint("");
							timebox.setErrorMessage("");
							
							try {
								financeDetail.setLovDescExtendedFieldValues(detail.getFieldName(),timebox.getValue());
							} catch (WrongValueException we) {
								wve.add(we);
							}
						} else if (component instanceof Datebox) {
							Datebox datebox = (Datebox) component;
							datebox.setConstraint("");
							datebox.setErrorMessage("");
							if(!isReadOnly && (detail.isFieldMandatory() || datebox.getValue() != null)){
								dateValidation(datebox, detail);
							}

							try {
								financeDetail.setLovDescExtendedFieldValues(detail.getFieldName(),datebox.getValue());
							} catch (WrongValueException we) {
								wve.add(we);
							}
						} else if (component instanceof Combobox) {
							Combobox combobox = (Combobox) component;
							combobox.setConstraint("");
							combobox.setErrorMessage("");

							try {
								if (detail.isFieldMandatory()) {
									if (!isReadOnly && (combobox.getSelectedItem() == null
											|| combobox.getSelectedItem().getValue() == null
											|| "#".equals(combobox.getSelectedItem().getValue().toString()))) {
										throw new WrongValueException(combobox, Labels.getLabel("STATIC_INVALID", 
												new String[] { detail.getFieldLabel() }));
									}
								}
						

								financeDetail.setLovDescExtendedFieldValues(detail.getFieldName(),combobox.getSelectedItem().getValue().toString());
							} catch (WrongValueException we) {
								wve.add(we);
							}
						} else if (component instanceof Radiogroup) {
							Radiogroup radiogroup = (Radiogroup) component;
							if(detail.isFieldMandatory()){
								if(radiogroup.getSelectedItem() == null){
									try {
										throw new WrongValueException(radiogroup, Labels.getLabel("SELECT_FIELD",new String[]{detail.getFieldLabel()}));
									} catch (WrongValueException we) {
										wve.add(we);
									}
								}else{
									financeDetail.setLovDescExtendedFieldValues(detail.getFieldName(),radiogroup.getSelectedItem().getValue().toString());
								}
							}else{
								String radioGroupValue= "";
								if(radiogroup.getSelectedItem() != null){
									radioGroupValue = radiogroup.getSelectedItem().getValue().toString();
								}
								financeDetail.setLovDescExtendedFieldValues(detail.getFieldName(),radioGroupValue);
							}
						} else if (component instanceof Checkbox) {
							Checkbox checkbox = (Checkbox) component;
							financeDetail.setLovDescExtendedFieldValues(detail.getFieldName(),checkbox.isChecked() ? 1 : 0);
						} else if (component instanceof Textbox) {
							Textbox textbox = (Textbox) component;
							textbox.setConstraint("");
							textbox.setErrorMessage("");
							if(!isReadOnly && (detail.isFieldMandatory() || textbox.getValue() != null)){

								if (StringUtils.isNotBlank(detail.getFieldConstraint())) {
									
									if ("REGEX_EMAIL".equals(detail.getFieldConstraint())) {
										textbox.setConstraint(new PTEmailValidator(detail.getFieldLabel(), detail.isFieldMandatory()));
									} else if ("REGEX_WEB".equals(detail.getFieldConstraint())) {
										textbox.setConstraint(new PTWebValidator(detail.getFieldLabel(), detail.isFieldMandatory()));
									} else if ("REGEX_TELEPHONE_FAX".equals(detail.getFieldConstraint())) {
										textbox.setConstraint(new PTPhoneNumberValidator(detail.getFieldLabel(), detail.isFieldMandatory()));
									} else if ("REGEX_MOBILE".equals(detail.getFieldConstraint())) {
										textbox.setConstraint(new PTMobileNumberValidator(detail.getFieldLabel(), detail.isFieldMandatory()));
									} else {
										if (textbox.isReadonly()) {
											textbox.setConstraint(new PTStringValidator(detail.getFieldLabel(), null, detail.isFieldMandatory()));
										} else {
											textbox.setConstraint(new PTStringValidator(detail.getFieldLabel(), detail.getFieldConstraint(), detail.isFieldMandatory()));
										}
									}
									
								}
							}

							try {
								financeDetail.setLovDescExtendedFieldValues(detail.getFieldName(),textbox.getValue());
							} catch (WrongValueException we) {
								wve.add(we);
							}
						} 
					}
				}
				showErrorDetails(wve,compList, tab);	
			}
		}
		logger.debug("Leaving");
		return financeDetail;
	}
	
	/**
	 * Method to set validation & Save for Additional Field Details 
	 * @param extendedFieldDetailList
	 * @param rowsaddlDetail
	 */
	public boolean isAddlDetailChanged(FinanceDetail financeDetail ,Rows rowsaddlDetail) {

		logger.debug("Entering");
		
		if(financeDetail.getExtendedFieldHeader() != null && financeDetail.getExtendedFieldHeader().getExtendedFieldDetails() != null ){
			List<ExtendedFieldDetail> extendedFieldDetailList = financeDetail.getExtendedFieldHeader().getExtendedFieldDetails();
			HashMap<String, Object> lovDescExtendedFieldValues = financeDetail.getLovDescExtendedFieldValues();
			if (extendedFieldDetailList != null && extendedFieldDetailList.size() > 0) {

				for (int i = 0; i < extendedFieldDetailList.size(); i++) {
					ExtendedFieldDetail detail = extendedFieldDetailList.get(i);

					if (rowsaddlDetail.getFellowIfAny("ad_"+ detail.getFieldName()) != null) {

						Component component = rowsaddlDetail.getFellowIfAny("ad_"+ detail.getFieldName());

						if (component instanceof Combobox) {
							Combobox combobox = (Combobox) component;
							combobox.setConstraint("");
							combobox.setErrorMessage("");
							if(!(lovDescExtendedFieldValues.get(detail.getFieldName())==null?"#"
									:lovDescExtendedFieldValues.get(detail.getFieldName()).toString()).equals(combobox.getSelectedItem().getValue().toString())){
								logger.debug("Leaving");
								return true;
							}
						} else if (component instanceof Decimalbox) {
							Decimalbox decimalbox = (Decimalbox) component;
							decimalbox.setConstraint("");
							decimalbox.setErrorMessage("");
							BigDecimal decimalValue = null;
							BigDecimal oldVarDecimalValue = null;
							if("AMT".equals(detail.getFieldType())){
								decimalValue = PennantApplicationUtil.unFormateAmount(decimalbox.getValue(), detail.getFieldPrec());
								oldVarDecimalValue = new BigDecimal(new BigInteger(
										StringUtils.isBlank(detail.getFieldDefaultValue())? "0" : 
											detail.getFieldDefaultValue() ),0);
							}else{
								decimalValue = decimalbox.getValue();
								oldVarDecimalValue = new BigDecimal(new BigInteger(
										StringUtils.isBlank(detail.getFieldDefaultValue())? "0" : 
											detail.getFieldDefaultValue() ), detail.getFieldPrec());
							}

							if(!((lovDescExtendedFieldValues.get(detail.getFieldName())== null ? oldVarDecimalValue
									:new BigDecimal(lovDescExtendedFieldValues.get(detail.getFieldName()).toString())).equals(decimalValue))){
								logger.debug("Leaving");
								return true;
							}

						} else if (component instanceof Datebox) {
							Datebox datebox = (Datebox) component;
							datebox.setConstraint("");
							datebox.setErrorMessage("");
							
							String actualDate = "";
							
							if(lovDescExtendedFieldValues.get(detail.getFieldName()) != null){
								SimpleDateFormat formatter = new SimpleDateFormat(PennantConstants.DBDateTimeFormat);
								try {
									Date date = (Date)formatter.parse(lovDescExtendedFieldValues.get(detail.getFieldName()).toString());
									actualDate = DateUtility.formatUtilDate(date,PennantConstants.DBDateTimeFormat);
								} catch (ParseException e) {
									logger.error("Exception: ", e);
								}  
							}
							
							if(!actualDate.equals(datebox.getValue() == null ? "": DateUtility.formatUtilDate(datebox.getValue(),
									PennantConstants.DBDateTimeFormat))){
								logger.debug("Leaving");
								return true;
							}
						} else if (component instanceof Timebox) {
							Timebox timebox = (Timebox) component;
							timebox.setConstraint("");
							timebox.setErrorMessage("");
							
							String actualTime = "";
							
							if(lovDescExtendedFieldValues.get(detail.getFieldName()) != null){
								SimpleDateFormat formatter = new SimpleDateFormat(PennantConstants.DBDateTimeFormat);
								try {
									Date date = (Date)formatter.parse(lovDescExtendedFieldValues.get(detail.getFieldName()).toString());
									actualTime = DateUtility.formatUtilDate(date,PennantConstants.DBDateTimeFormat);
								} catch (ParseException e) {
									logger.error("Exception: ", e);
								}  
							}
							
							if(!actualTime.equals(timebox.getValue() == null ? "": DateUtility.formatUtilDate(timebox.getValue(),
									PennantConstants.DBDateTimeFormat))){
								logger.debug("Leaving");
								return true;
							}

						} else if (component instanceof Radiogroup) {
							Radiogroup radiogroup = (Radiogroup) component;
							if(!(lovDescExtendedFieldValues.get(detail.getFieldName())==null?""
									:lovDescExtendedFieldValues.get(detail.getFieldName()).toString()).equals(
									radiogroup.getSelectedItem() == null ? "" : radiogroup.getSelectedItem().getValue().toString())){
								logger.debug("Leaving");
								return true;
							}
						} else if (component instanceof Checkbox) {
							Checkbox checkbox = (Checkbox) component;
							if(Integer.parseInt(lovDescExtendedFieldValues.get(detail.getFieldName())==null?"0"
									:lovDescExtendedFieldValues.get(detail.getFieldName()).toString()) != (checkbox.isChecked() ? 1 : 0)){
								logger.debug("Leaving");
								return true;
							}
						} else if (component instanceof Textbox) {
							Textbox textbox = (Textbox) component;
							textbox.setConstraint("");
							textbox.setErrorMessage("");
							
							String defaultValue = StringUtils.trimToEmpty(detail.getFieldDefaultValue());
							if(!StringUtils.trimToEmpty(lovDescExtendedFieldValues.get(detail.getFieldName())== null ? defaultValue
									:lovDescExtendedFieldValues.get(detail.getFieldName()).toString()).equals(textbox.getValue())){
								logger.debug("Leaving");
								return true;
							}

						} 
					}
				}
			}
		}
		logger.debug("Leaving");
		return false;
	}
	
	/**
	 * Method for Date Validation setting
	 * @param datebox
	 * @param detail
	 */
	private void dateValidation(Datebox datebox, ExtendedFieldDetail detail){
		logger.debug("Entering");
		
		String value = detail.getFieldConstraint().split(",")[0];

		PTDateValidator dateValidator = null;

		if("RANGE".equals(value)){

			dateValidator = new PTDateValidator(detail.getFieldLabel(), detail.isFieldMandatory(), 
					DateUtility.getUtilDate(detail.getFieldConstraint().split(",")[1],PennantConstants.dateFormat),
					DateUtility.getUtilDate(detail.getFieldConstraint().split(",")[2],PennantConstants.dateFormat), true);

		} else if("FUTURE_DAYS".equals(value)){

			dateValidator = new PTDateValidator(detail.getFieldLabel(), detail.isFieldMandatory(), 
					null,
					DateUtility.addDays(DateUtility.getSysDate(),Integer.parseInt(detail.getFieldConstraint().split(",")[1])),
					true);

		} else if("PAST_DAYS".equals(value)){

			dateValidator = new PTDateValidator(detail.getFieldLabel(), detail.isFieldMandatory(), 
					DateUtility.addDays(DateUtility.getSysDate(),-(Integer.parseInt(detail.getFieldConstraint().split(",")[1]))),
					null, true);

		} else if("FUTURE_TODAY".equals(value)){

			dateValidator = new PTDateValidator(detail.getFieldLabel(), detail.isFieldMandatory(), 
					true, null, false);

		} else if("PAST_TODAY".equals(value)){

			dateValidator = new PTDateValidator(detail.getFieldLabel(), detail.isFieldMandatory(), 
					null, true, false);

		} else if("FUTURE".equals(value)){

			dateValidator = new PTDateValidator(detail.getFieldLabel(), detail.isFieldMandatory(), 
					false, null, false);

		} else if("PAST".equals(value)){

			dateValidator = new PTDateValidator(detail.getFieldLabel(), detail.isFieldMandatory(), 
					null, false, false);

		}
		datebox.setConstraint(dateValidator);
		logger.debug("Leaving");
	}

	/**
	 * Method for Date Validation setting
	 * @param datebox
	 * @param detail
	 */
	private void decimalValidation(Decimalbox decimalbox , ExtendedFieldDetail detail){
		logger.debug("Entering");
		
		PTDecimalValidator decimalValidator = null;
		
		long minValue = detail.getFieldMinValue();
		long maxValue = detail.getFieldMaxValue();

		if ("RATE".equals(detail.getFieldType().trim())){
			
			if(maxValue != 0 && maxValue > Math.pow(10,detail.getFieldLength()-detail.getFieldPrec()) -1){
				maxValue = (long) (Math.pow(10,detail.getFieldLength()-detail.getFieldPrec()) -1);
			}
			decimalValidator = new PTDecimalValidator(detail.getFieldLabel(), detail.getFieldPrec(),
					detail.isFieldMandatory(), false, minValue,maxValue);

		} else if("AMT".equals(detail.getFieldType().trim())){
			
			if(maxValue != 0 && maxValue > Math.pow(10,detail.getFieldLength()-detail.getFieldPrec()) -1){
				maxValue = (long) (Math.pow(10,detail.getFieldLength()-detail.getFieldPrec()) -1);
			}
			decimalValidator = new PTDecimalValidator(detail.getFieldLabel(), detail.getFieldPrec(),
					detail.isFieldMandatory(), false,  minValue,maxValue);

		} else if("PRCT".equals(detail.getFieldType().trim())){
			
			if(maxValue != 0 && maxValue > 100){
				maxValue = 100;
			}
			decimalValidator = new PTDecimalValidator(detail.getFieldLabel(), detail.getFieldPrec(),
					detail.isFieldMandatory(), false, minValue, maxValue);

		} else if ("NUMERIC".equals(detail.getFieldType().trim())) {
			
			if(maxValue != 0 && maxValue > Math.pow(10,detail.getFieldLength())-1){
				maxValue = (long) Math.pow(10,detail.getFieldLength())-1;
			}
			decimalValidator = new PTDecimalValidator(detail.getFieldLabel(), detail.getFieldPrec(),
					detail.isFieldMandatory(), false, Math.pow(10,detail.getFieldLength())-1);
		}
		decimalbox.setConstraint(decimalValidator);
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Showing Error Details
	 * @param wve
	 * @param tab
	 */
	private void showErrorDetails(ArrayList<WrongValueException> wve, List<Component> compList, Tab tab) {
		logger.debug("Entering");
		if (wve.size() > 0) {
			tab.setSelected(true);
			for (Component component : compList) {

				if (component instanceof Decimalbox) {
					Decimalbox decimalbox = (Decimalbox) component;
					decimalbox.setConstraint("");
					decimalbox.setErrorMessage("");

				} else if (component instanceof Datebox) {
					Datebox datebox = (Datebox) component;
					datebox.setConstraint("");
					datebox.setErrorMessage("");

				} else if (component instanceof Timebox) {
					Timebox timebox = (Timebox) component;
					timebox.setConstraint("");
					timebox.setErrorMessage("");

				} else if (component instanceof Combobox) {
					Combobox combobox = (Combobox) component;
					combobox.setConstraint("");
					combobox.setErrorMessage("");

				} else if (component instanceof Textbox) {
					Textbox textbox = (Textbox) component;
					textbox.setConstraint("");
					textbox.setErrorMessage("");
				} 
			}
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			logger.debug("Leaving");
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}
	
}

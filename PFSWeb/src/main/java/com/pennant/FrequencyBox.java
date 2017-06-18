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
 * FileName    		:  FrequencyBox.java		                                            * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-11-2016    														*
 *                                                                  						*
 * Modified Date    :  02-11-2016    														*
 *                                                                  						*
 * Description 		:  Frequency box                                            		*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-11-2016       Manoj	      0.1       		                            * 
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
package com.pennant;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;

import com.pennant.app.constants.FrequencyCodeTypes;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.PennantConstants;

public class FrequencyBox extends Hbox {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(FrequencyBox.class);

	private Space space;
	private Textbox frqValue;
	private Combobox frqCode;
	private Combobox frqMonth;
	private Combobox frqDay;
	private Hbox hbox;

	private String reaOnlyStyle = "#F2F2F2";
	private String alwFrqDays = "";
	private boolean frqMonthDisable = false;
	private boolean frqDayDisable = false;

	/**
	 * FrequencyBox Constructor Defining the components and events
	 */
	public FrequencyBox() {
		logger.debug("Entering");

		// Space
		space = new Space();
		space.setWidth("2px");
		this.appendChild(space);

		// Hbox
		hbox = new Hbox();
		hbox.setSpacing("2px");

		// Textbox
		frqValue = new Textbox();
		frqValue.setVisible(false);
		frqValue.setMaxlength(15);
		frqValue.setStyle("border:0px;margin:0px;");
		hbox.appendChild(frqValue);

		// Combobox
		frqCode = new Combobox();
		frqCode.setReadonly(true);
		frqCode.setTabindex(-1);
		frqCode.setStyle(reaOnlyStyle);
		frqCode.setWidth("90px");
		frqCode.addForward("onSelect", this, "onSelectFrqCode");
		hbox.appendChild(frqCode);

		// Combobox
		frqMonth = new Combobox();
		frqMonth.setReadonly(true);
		frqMonth.setTabindex(-1);
		frqMonth.setStyle(reaOnlyStyle);
		frqMonth.setWidth("130px");
		frqMonth.addForward("onSelect", this, "onSelectFrqMonth");
		hbox.appendChild(frqMonth);

		// Combobox
		frqDay = new Combobox();
		frqDay.setReadonly(true);
		frqDay.setTabindex(-1);
		frqDay.setStyle(reaOnlyStyle);
		frqDay.setWidth("50px");
		frqDay.addForward("onSelect", this, "onSelectFrqDay");
		hbox.appendChild(frqDay);
		this.appendChild(hbox);

		logger.debug("Leaving");
	}

	/**
	 * The controller calls this event handler when user select the frequency Month.
	 * @param event   An event sent to the event handler of the component.
	 */
	public void onSelectFrqCode(Event event) {
		setFrqCodeDetails();
		Events.postEvent("onSelectCode", this, null);
	}
	
	/**
	 * The controller calls this event handler when user select the frequency Month.
	 * @param event   An event sent to the event handler of the component.
	 */
	public void onSelectFrqMonth(Event event) {
		setFrqMonthDetails();
		Events.postEvent("onSelectMonth", this, null);
	}

	/**
	 * The controller calls this event handler when user select the frequency Day.
	 * @param event  An event sent to the event handler of the component.
	 */
	public void onSelectFrqDay(Event event) {
		setFrqDayDetails();
		Events.postEvent("onSelectDay", this, null);
	}

	/**
	 * This method is To fill frequency code with values by calling
	 * getFrequency() method of FrequencyUtil Class
	 * 
	 * @param frqency
	 */
	private void fillFrqCode(String frqency) {
		clearData(this.frqCode);

		List<ValueLabel> frqList = FrequencyUtil.getFrequency();

		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));

		this.frqCode.appendChild(comboitem);
		this.frqCode.setSelectedItem(comboitem);

		for (int i = 0; i < frqList.size(); i++) {
			comboitem = new Comboitem();
			comboitem.setValue(frqList.get(i).getValue());
			comboitem.setLabel(frqList.get(i).getLabel());
			this.frqCode.appendChild(comboitem);
			if (FrequencyUtil.getFrequencyCode(frqency).equals(frqList.get(i).getValue())) {
				this.frqCode.setSelectedItem(comboitem);
			}
		}
	}

	/**
	 * This method is To fill frequency month with values by calling
	 * getFrequencyMth() method of FrequencyUtil Class
	 * 
	 * @param frqency
	 */
	private void fillFrqMonth(String frqency) {
		clearData(this.frqMonth);

		String frqCode = FrequencyUtil.getFrequencyCode(frqency);
		String frqMth = FrequencyUtil.getFrequencyMth(frqency);

		List<ValueLabel> frqMthList = FrequencyUtil.getFrequencyDetails(frqCode.charAt(0));

		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));

		this.frqMonth.appendChild(comboitem);
		this.frqMonth.setSelectedItem(comboitem);

		for (int i = 0; i < frqMthList.size(); i++) {
			comboitem = new Comboitem();
			comboitem.setValue(frqMthList.get(i).getValue());
			comboitem.setLabel(frqMthList.get(i).getLabel());
			this.frqMonth.appendChild(comboitem);
			if (StringUtils.trimToEmpty(frqMth).equals(frqMthList.get(i).getValue())) {
				this.frqMonth.setSelectedItem(comboitem);
			}
		}
		
		boolean doDisableTemp = this.frqMonthDisable;
		switch (frqCode.charAt(0)) {
		case 'M':
			doDisableTemp = true;
			break;
		case 'X':
			doDisableTemp = true;
			break;
		case 'F':
			doDisableTemp = true;
			break;
		case 'W':
			doDisableTemp = true;
			break;
		case 'D':
			doDisableTemp = true;
			break;
		}
		doDisable(this.frqMonth, doDisableTemp);
	}

	/**
	 * This method is To fill frequency days with values by calling getFrqdays()
	 * method of FrequencyUtil Class
	 * 
	 * @param frqency
	 */
	private void fillFrqDay(String frqency) {
		clearData(this.frqDay);
		if(frqency != null){
			frqency = frqency.replace("#", "00");
		}
		List<ValueLabel> frqDaysList = FrequencyUtil.getFrqdays(frqency);

		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel("00");

		this.frqDay.appendChild(comboitem);
		this.frqDay.setSelectedItem(comboitem);

		for (int i = 0; i < frqDaysList.size(); i++) {
			
			if(StringUtils.isNotEmpty(alwFrqDays) && !alwFrqDays.contains(","+frqDaysList.get(i).getValue()+",")){
				continue;
			}
			comboitem = new Comboitem();
			comboitem.setValue(frqDaysList.get(i).getValue());
			comboitem.setLabel(frqDaysList.get(i).getLabel());
			this.frqDay.appendChild(comboitem);
			if (FrequencyUtil.getFrequencyDay(frqency).equals(frqDaysList.get(i).getValue())) {
				this.frqDay.setSelectedItem(comboitem);
			}
		}
		
		if (FrequencyUtil.getFrequencyCode(frqency).equals(FrequencyCodeTypes.FRQ_DAILY)) {
			doDisable(this.frqDay, true);
		}else{
			doDisable(this.frqDay, this.frqDayDisable);
		}
	}

	/**
	 * This method is To fill the Frequency Month and Day value based on the
	 * selected value of Frequency code parms are Selected value of frequency
	 * code and frequency code comboBox name and names of the month and day
	 * comboBoxes and a text field name to store the selected day value
	 **/
	public void setFrqCodeDetails() {
		logger.debug("Entering");
		String frqCode = getFrqCodeValue();
		boolean fillDays = false;
		if (!"#".equalsIgnoreCase(frqCode)) {
			switch (frqCode.charAt(0)) {
			case 'M':
				fillDays = true;
				break;
			case 'X':
				fillDays = true;
				break;
			case 'F':
				fillDays = true;
				break;
			case 'W':
				fillDays = true;
				break;
			case 'D':
				fillDays = true;
				break;
			default:
				fillDays = false;
			}
			
			if (fillDays) {
				this.frqValue.setValue(frqCode + "0000");
			} else {
				this.frqValue.setValue(frqCode);
			}
			
			fillFrqMonth(this.frqValue.getValue());
			
			if (fillDays) {
				fillFrqDay(this.frqValue.getValue());
				this.frqDay.setSelectedIndex(0);
				this.frqDay.setFocus(true);
				this.frqDay.setSelectionRange(0, this.frqDay.getValue()
						.length());
				this.frqMonth.setSelectedIndex(1);
			} else {
				this.frqMonth.setSelectedIndex(0);
				this.frqMonth.setFocus(true);
				this.frqMonth.setSelectionRange(0, this.frqMonth.getValue()
						.length());
			}
		} else {
			this.frqMonth.setSelectedIndex(0);
			this.frqDay.setSelectedIndex(0);
			this.frqValue.setValue("");
		}
		logger.debug("Leaving");
	}

	/**
	 * This method is To fill the Frequency Month value based on the selected
	 * value of Frequency Code params: Selected comboBox value as a
	 * String,Parent comboBox(frequency code) Selected value as a String, Two
	 * ComboBoxes (Month and Day) Names and the TextBox Name
	 */
	private void setFrqMonthDetails() {
		logger.debug("Entering");
		if (!"#".equalsIgnoreCase(getFrqMonthValue())) {
			this.frqValue.setValue(getFrqCodeValue() + getFrqMonthValue());
			fillFrqDay(this.frqValue.getValue());
			this.frqDay.setSelectedIndex(0);
			this.frqDay.setFocus(true);
			this.frqDay.setSelectionRange(0, this.frqDay.getValue().length());
		} else {
			this.frqValue.setValue("");
			this.frqDay.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method is To set the Frequency value based on the selected value of
	 * Frequency day value
	 */
	private void setFrqDayDetails() {
		logger.debug("Entering");
		String frqDay = getFrqDayValue();
		if (!"#".equalsIgnoreCase(frqDay)) {
			this.frqValue.setValue(getFrqCodeValue() + getFrqMonthValue() + frqDay);
		}
		logger.debug("Leaving");
	}

	
	/**
	 * This method is for changing Frequency based on new start date
	 */
	public void updateFrequency(String month,String day) {
		logger.debug("Entering");
		if (!PennantConstants.List_Select.equals(getFrqCodeValue())) {
			fillFrqMonth(month);
			String frq = getFrqCodeValue().concat(getFrqMonthValue()).concat(day);
			fillFrqDay(frq);
			this.frqValue.setValue(frq);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method is to reset frequency day and to set frequency value
	 * 
	 * @param selectedIndex
	 */
	public void resetFrqDay(int selectedIndex) {
		logger.debug("Entering");
		this.frqDay.setSelectedIndex(getFrqDayIndex(selectedIndex));
		setFrqDayDetails();
		logger.debug("Leaving");
	}

	/**
	 * This method is to fetch frequence day index by selectedIndex
	 * 
	 * @param dayIndex
	 */
	private int getFrqDayIndex(int dayIndex) {
		char frqCode = (this.frqValue.getValue()).charAt(0);
		if (frqCode == 'D') {
			dayIndex = 0;
		} else if (frqCode == 'W') {
			dayIndex = dayIndex % 7;
		} else if (frqCode == 'F') {
			dayIndex = dayIndex % 14;
		}
		return dayIndex;
	}

	/**
	 * This method to validate frequency and dates
	 * 
	 * @param date
	 * @param grcDate
	 **/
	public boolean validateFrquency(Date date, Date grcDate) {
		if (!this.frqCode.isReadonly() && this.frqCode.getSelectedIndex() != 0) {
			if (!FrequencyUtil.isFrqDate(this.frqValue.getValue(), date) && date != grcDate) {
				return false;
			}
		}
		return true;
	}

	/**
	 * This method to validate frequency combobox values
	 **/
	public boolean isValidComboValue() {
		if (!this.frqCode.isDisabled() && this.frqCode.getSelectedIndex() <= 0) {
			throw new WrongValueException(this.frqCode, Labels.getLabel(
					"STATIC_INVALID",
					new String[] { Labels.getLabel("label_FrqCode.value") }));
		} else if (!this.frqMonth.isDisabled()
				&& this.frqMonth.getSelectedIndex() <= 0) {
			throw new WrongValueException(this.frqMonth, Labels.getLabel(
					"STATIC_INVALID",
					new String[] { Labels.getLabel("label_FrqMth.value") }));
		} else if (!this.frqDay.isDisabled()
				&& this.frqDay.getSelectedIndex() <= 0) {
			throw new WrongValueException(this.frqDay, Labels.getLabel(
					"STATIC_INVALID",
					new String[] { Labels.getLabel("label_FrqDay.value") }));
		}
		return true;
	}

	/**
	 * This method will remove all combo items of combobox
	 * 
	 * @param combobox
	 */
	public void clearData(Combobox combobox) {
		int count = combobox.getItemCount();
		for (int i = 0; i < count; i++) {
			combobox.removeItemAt(0);
		}
	}

	/**
	 * This method will disable or enable frequency fields
	 * 
	 * @param isDisable
	 */
	public void setDisabled(boolean isDisable) {
		setDisableFrqCode(isDisable);
		setDisableFrqMonth(isDisable);
		setDisableFrqDay(isDisable);
	}

	
	public void setDisableFrqCode(boolean isDisable){
		doDisable(this.frqCode, isDisable);
	}
	
	public void setDisableFrqMonth(boolean isDisable){
		this.frqMonthDisable = isDisable;
		doDisable(this.frqMonth, isDisable);
	}
	
	public void setDisableFrqDay(boolean isDisable){
		this.frqDayDisable = isDisable;
		doDisable(this.frqDay, isDisable);
	}
	
	/**
	 * This method will set disable properties for combobox which is passed as
	 * parameter
	 * 
	 * @param combobox
	 * @param isDisable
	 */
	private void doDisable(Combobox combobox, boolean isDisable) {
		if (isDisable) {
			combobox.setTabindex(-1);
			combobox.setAutodrop(false);
			combobox.setStyle(reaOnlyStyle);
			combobox.setButtonVisible(false);
			combobox.setAutocomplete(false);
			combobox.setDisabled(true);
		} else {
			combobox.setTabindex(0);
			combobox.setAutodrop(true);
			combobox.setButtonVisible(true);
			combobox.setStyle(reaOnlyStyle);
			combobox.setDisabled(false);
		}
	}

	public void setErrorMessage(String msg){
		this.frqCode.setErrorMessage(msg);
		this.frqMonth.setErrorMessage(msg);
		this.frqDay.setErrorMessage(msg);
	}
	
	public String getValue() {
		return this.frqValue.getValue();
	}

	public void setValue(String frqValue) {
		fillFrqCode(frqValue);
		fillFrqMonth(frqValue);
		fillFrqDay(frqValue);
		this.frqValue.setValue(frqValue);
	}
	
	/**
	 * This method is used to get selected combobox value
	 * 
	 * @param combobox
	 * @return
	 */
	public String getComboboxValue(Combobox combobox) {
		String comboValue = "";
		if (combobox.getSelectedItem() != null) {
			comboValue = combobox.getSelectedItem().getValue().toString();
		} else {
			combobox.setSelectedIndex(0);
		}
		return comboValue;
	}

	public void setMandatoryStyle(boolean mandatory) {
		if (mandatory) {
			this.space.setSclass(PennantConstants.mandateSclass);
		} else {
			this.space.setSclass("");
		}
	}

	public void setAlwFrqDays(String alwFrqDays) {
		if(StringUtils.isNotEmpty(alwFrqDays)){
			this.alwFrqDays = ","+alwFrqDays+",";
		}
	}
	
	public boolean setVisible(boolean visible) {
		return super.setVisible(visible);
	}

	public void setFrqValue(String frqValue) {
		this.frqValue.setValue(frqValue);
	}

	public String getFrqCodeValue() {
		return getComboboxValue(this.frqCode);
	}

	public String getFrqMonthValue() {
		return getComboboxValue(this.frqMonth);
	}

	public String getFrqDayValue() {
		return getComboboxValue(this.frqDay);
	}

	public int getDaySelectedIndex() {
		return this.frqDay.getSelectedIndex();
	}

	public Combobox getFrqCodeCombobox() {
		return this.frqCode;
	}

	public Combobox getFrqMonthCombobox() {
		return this.frqMonth;
	}

	public Combobox getFrqDayCombobox() {
		return this.frqDay;
	}
}

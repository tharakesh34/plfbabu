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
 * FileName    		:  RateBox.java		                                            		* 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-12-2016    														*
 *                                                                  						*
 * Modified Date    :  05-12-2016    														*
 *                                                                  						*
 * Description 		:  Rate box                                            					*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-12-2016       CMV	      0.1       		                            				* 
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

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Space;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.util.PennantConstants;

public class RateBox extends Hbox{
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(RateBox.class);

	private Space space;
	private ExtendedCombobox baseRateBox;
	private ExtendedCombobox specialRateBox;
	private Decimalbox marginRate;
	private Decimalbox effRate;
	private Hbox hbox;
	private int rateMaxlength = 8; 

	
	/**
	 * FrequencyBox Constructor Defining the components and events
	 */
	public RateBox() {
		super();
		logger.debug("Entering");
		
		space = new Space();
		space.setWidth("2px");
		this.appendChild(space);
		
		hbox = new Hbox();
		
		baseRateBox = new ExtendedCombobox(false);
		baseRateBox.getTextbox().setPlaceholder("base");
		baseRateBox.setTextBoxWidth(60);
		hbox.appendChild(baseRateBox);

		specialRateBox = new ExtendedCombobox(false);
		specialRateBox.setVisible(ImplementationConstants.ALLOW_SPECIALRATE);
		specialRateBox.getTextbox().setPlaceholder("special");
		specialRateBox.setTextBoxWidth(60);
		hbox.appendChild(specialRateBox);
		
		marginRate = new Decimalbox();
		marginRate.setMaxlength(13);
		marginRate.setFormat(PennantConstants.rateFormate9);
		marginRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		marginRate.setScale(9);
		marginRate.setPlaceholder("margin");
		marginRate.setHeight("23px");
		marginRate.setWidth("79px");
		marginRate.addForward("onChange", this, "onChangeMargin");
		hbox.appendChild(marginRate);
		
		effRate = new Decimalbox();
		effRate.setVisible(false);
		effRate.setReadonly(true);
		effRate.setMaxlength(13);
		effRate.setFormat(PennantConstants.rateFormate9);
		effRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		effRate.setScale(9);
		effRate.setHeight("23px");
		effRate.setWidth("79px");
		hbox.appendChild(effRate);
		
		this.appendChild(hbox);
		logger.debug("Leaving");
	}
	
	
	/**
	 * Called when changing the value of the text box
	 * @param event
	 */
	public void onChangeMargin(Event event) {
		logger.debug("Entering");
		Events.postEvent("onFulfill", this, PennantConstants.RATE_MARGIN);
		logger.debug("Leaving");
	}
	
	public Object getBaseObject(){
		return this.baseRateBox.getObject();
	}
	
	public Object getSpecialObject(){
		return this.specialRateBox.getObject();
	}
	
	public ExtendedCombobox getBaseComp(){
		return this.baseRateBox;
	}
	
	public ExtendedCombobox getSpecialComp(){
		return this.specialRateBox;
	}
	
	public Decimalbox getMarginComp(){
		return this.marginRate;
	}
	
	public Decimalbox getEffRateComp(){
		return this.effRate;
	}
	
	public String getBaseValue(){
		return this.baseRateBox.getValue();
	}
	public void setBaseValue(String baseValue){
		this.baseRateBox.setValue(baseValue);
	}
	
	public String getBaseDescription(){
		return this.baseRateBox.getDescription();
	}
	public void setBaseDescription(String baseDesc){
		this.baseRateBox.setDescription(baseDesc);
	}
	
	public String getSpecialValue(){
		return this.specialRateBox.getValue();
	}
	public void setSpecialValue(String spclValue){
		this.specialRateBox.setValue(spclValue);
	}
	
	public String getSpecialDescription(){
		return this.specialRateBox.getDescription();
	}
	public void setSpecialDescription(String spclDesc){
		this.specialRateBox.setDescription(spclDesc);
	}
	
	public BigDecimal getMarginValue(){
		return this.marginRate.getValue();
	}
	public void setMarginValue(BigDecimal margin){
		this.marginRate.setValue(margin);
	}
	public void setMarginText(String margin){
		this.marginRate.setText(margin);
	}
	
	public BigDecimal getEffRateValue(){
		return this.effRate.getValue();
	}
	public void setEffRateValue(BigDecimal effRate){
		this.effRate.setValue(effRate);
	}
	public void setEffRateText(String margin){
		this.effRate.setText(margin);
	}
	
	public void setBaseConstraint(String constraint) {
		this.baseRateBox.setConstraint(constraint);
	}
	public void setBaseConstraint(Constraint constraint) {
		this.baseRateBox.setConstraint(constraint);
	}
	
	public void setSpecialConstraint(String constraint) {
		this.specialRateBox.setConstraint(constraint);
	}
	public void setSpecialConstraint(Constraint constraint) {
		this.specialRateBox.setConstraint(constraint);
	}
	
	public void setMarginConstraint(String constraint) {
		this.marginRate.setConstraint(constraint);
	}
	public void setMarginConstraint(Constraint constraint) {
		this.marginRate.setConstraint(constraint);
	}
	
	public void setBaseErrorMessage(String erroMsg) {
		this.baseRateBox.setErrorMessage(erroMsg);
	}
	
	public void setSpecialErrorMessage(String erroMsg) {
		this.specialRateBox.setErrorMessage(erroMsg);
	}
	
	public void setMarginErrorMessage(String erroMsg) {
		this.marginRate.setErrorMessage(erroMsg);
	}
	
	public void setReadonly(boolean isReadonly){
		setBaseReadonly(isReadonly);
		setSpecialReadonly(isReadonly);
		setMarginReadonly(isReadonly);
	}
	
	public boolean isBaseReadonly(){
		return this.baseRateBox.isReadonly();
	}
	public void setBaseReadonly(boolean isReadonly){
		this.baseRateBox.setReadonly(isReadonly);
		this.baseRateBox.getTextbox().setPlaceholder(isReadonly?"":"base");
	}
	
	public boolean isSpecialReadonly(){
		return this.specialRateBox.isReadonly();
	}
	public void setSpecialReadonly(boolean isReadonly){
		this.specialRateBox.setReadonly(isReadonly);
		this.specialRateBox.getTextbox().setPlaceholder(isReadonly?"":"special");
	}
	
	public boolean isMarginReadonly(){
		return this.marginRate.isReadonly();
	}
	public void setMarginReadonly(boolean isReadonly){
		this.marginRate.setReadonly(isReadonly);
		this.marginRate.setPlaceholder(isReadonly?"":"margin");
	}
	
	public void setMandatoryStyle(boolean mandatory) {
		if (mandatory) {
			this.space.setSclass(PennantConstants.mandateSclass);
		} else {
			this.space.setSclass("");
		}
	}
	
	public boolean isBaseVisible(){
		return this.baseRateBox.isVisible();
	}
	public void setBaseVisible(boolean visible){
		this.baseRateBox.setVisible(visible);
	}
	
	public boolean isSpecialVisible(){
		return this.specialRateBox.isVisible();
	}
	
	public void setEffectiveRateVisible(boolean visible){
		this.effRate.setVisible(visible);
	}
	
	public void setBaseProperties(String moduleName,String valueColumn,String descColumn){
		this.baseRateBox.setProperties(moduleName, valueColumn, descColumn, PennantConstants.RATE_BASE, this.rateMaxlength);
	}
	
	public void setSpecialProperties(String moduleName,String valueColumn,String descColumn){
		this.specialRateBox.setProperties(moduleName, valueColumn, descColumn, PennantConstants.RATE_SPECIAL, this.rateMaxlength);
	}
	
}

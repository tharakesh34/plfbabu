package com.pennant.webui.finance.eligibility;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.lmtmasters.LoanEligibility;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/finance/parameters/projectSummaryDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CustomerFinanceEligibilityCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -6950119237517363391L;
	private final static Logger logger = Logger.getLogger(CustomerFinanceEligibilityCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CustFinElg; 		// autowired

	protected Grid 			grid_Fields; 			// autowired
	protected Rows 			rows_Fields; 			// autowired
	protected Button 		btn_Simulate;
	protected Groupbox		gb_ruleCodes;
	protected Radiogroup	rg_ruleCode;
	
	protected Borderlayout 	borderlayout_Rules;		// autowired
	Textbox textbox ;
	Decimalbox decimalbox ;
	Checkbox checkbox;
	Longbox longbox;
	Datebox datebox;
	
	protected Customer customer;
	protected LoanEligibility loanEligibility;
	String ruleFormula="";
	List<String> fieldList ;
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Finance Eligibility object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustFinElg(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("customer")) {
			customer = (Customer) args.get("customer");
		}else{
			customer = null;
		}
		
		if (args.containsKey("loanEligibility")) {
			loanEligibility = (LoanEligibility) args.get("loanEligibility");
		}else{
			loanEligibility = null;
		}
		
		if(customer == null){
			prepareCustomerData();
			this.borderlayout_Rules.setVisible(true);
			this.borderlayout_Rules.setHeight("600px");
			this.window_CustFinElg.doModal();
		}else{
			this.window_CustFinElg.detach();
		}
		
		
		logger.debug("Leaving" + event.toString());
	}
	
	
	
	/**
	 * Method for check Event for dynamically 
	 * @param event
	 * @throws InterruptedException
	 * @throws NoSuchFieldException 
	 * @throws SecurityException 
	 */
	public void prepareCustomerData() throws SecurityException, NoSuchFieldException{
		logger.debug("Entering" );

		if(customer == null){

			// get the variables and its values from the object into map
			HashMap<String, Object> customerElgFields = getLoanEligibility().getCustomerEligibilityCheck().getDeclaredFieldValues();

			ArrayList<String> fieldSet = new ArrayList<String>(customerElgFields.keySet());

			Row row;
			Label label;
			Space space;
			rows_Fields.getChildren().clear();
			for (int i = 0; i < fieldSet.size(); i++) {

				row = new Row();
				String fieldName = fieldSet.get(i).toString().substring(0, 1).toUpperCase()
										+fieldSet.get(i).toString().substring(1);
				label = new Label(Labels.getLabel("label_"+fieldName));
				row.appendChild(label);

				space = new Space();
				space.setStyle("width:2px;background-color:red;");
				row.appendChild(space);

				Class<CustomerEligibilityCheck>  aClass = CustomerEligibilityCheck.class;
				Field field = aClass.getDeclaredField(fieldSet.get(i).toString());
				String fieldType = field.getType().getSimpleName();

				if(fieldType.equalsIgnoreCase("String")){
					textbox = new Textbox();
					textbox.setId(fieldSet.get(i));
					textbox.setStyle("width:160px;");
					textbox.setConstraint("NO EMPTY :" + Labels.getLabel("FIELD_NO_EMPTY",
							new String[]{textbox.getId()}));
					row.appendChild(textbox);
					rows_Fields.appendChild(row);
				}else if(fieldType.equalsIgnoreCase("boolean")){
					checkbox = new Checkbox();
					checkbox.setId(fieldSet.get(i));
					checkbox.setChecked(false);
					row.appendChild(checkbox);
					rows_Fields.appendChild(row);
				}else if(fieldType.equalsIgnoreCase("BigDecimal") || fieldType.equalsIgnoreCase("Double")){
					decimalbox = new Decimalbox();
					decimalbox.setId(fieldSet.get(i));
					decimalbox.setStyle("width:160px;");
					decimalbox.setFormat("#,###");
					decimalbox.setConstraint("NO EMPTY :" + Labels.getLabel("FIELD_NO_EMPTY",
							new String[]{decimalbox.getId()}));
					row.appendChild(decimalbox);
					rows_Fields.appendChild(row);
				}else if(fieldType.equalsIgnoreCase("Integer") || fieldType.equalsIgnoreCase("Long")){
					longbox = new Longbox();
					longbox.setId(fieldSet.get(i));
					longbox.setStyle("width:160px;");
					longbox.setConstraint("NO EMPTY :" + Labels.getLabel("FIELD_NO_EMPTY",
							new String[]{longbox.getId()}));
					row.appendChild(longbox);
					rows_Fields.appendChild(row);
				}else if(fieldType.equalsIgnoreCase("Date")){
					datebox = new Datebox();
					datebox.setId(fieldSet.get(i));
					datebox.setStyle("width:160px;");
					datebox.setConstraint("NO EMPTY :" + Labels.getLabel("FIELD_NO_EMPTY",
							new String[]{datebox.getId()}));
					row.appendChild(datebox);
					rows_Fields.appendChild(row);
				}
			}
			if(grid_Fields.getRows().getVisibleItemCount()>0){
				grid_Fields.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * On click event for stimulate button
	 */
	public void onClick$btn_Simulate(Event event) throws InterruptedException,ScriptException {
		logger.debug("Entering" + event.toString());
		
		try {
			HashMap<String, Object> customerElgFields = new HashMap<String, Object>();
			Component component;
			for (int i = 0; i < fieldList.size(); i++) {
				component = (Component)rows_Fields.getFellowIfAny(fieldList.get(i));
				if(component instanceof Textbox){
					textbox = (Textbox)component;
					customerElgFields.put(textbox.getId(), textbox.getValue());
				}else if(component instanceof Decimalbox || component instanceof Doublebox){
					decimalbox = (Decimalbox)component;
					customerElgFields.put(decimalbox.getId(), decimalbox.getValue());
				}else if(component instanceof Longbox || component instanceof Intbox){
					longbox = (Longbox)component;
					customerElgFields.put(longbox.getId(), longbox.getValue());
				}else if(component instanceof Checkbox){
					checkbox = (Checkbox)component;
					customerElgFields.put(checkbox.getId(), checkbox.isChecked());
				}else if(component instanceof Datebox){
					datebox = (Datebox)component;
					customerElgFields.put(datebox.getId(), datebox.getValue());
				}
			}
		} catch (final WrongValueException e) {
			logger.error(e);
			throw e;
		} catch (final WrongValuesException e) {
			logger.error(e);
			throw e;
		} catch (Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.getMessage());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		
		try {
			doClose();
		} catch (final Exception e) {
			logger.error(e);
			// close anyway
			this.window_CustFinElg.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	private void doClose() throws InterruptedException {
		logger.debug("Entering");
		
		final String msg = "Do you want cancel simulation";
		final String title = "Cancel Confirmation";

		MultiLineMessageBox.doSetTemplate();
		if (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES
				| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true,
				new EventListener() {
					@Override
					public void onEvent(Event evt) {
						switch (((Integer) evt.getData()).intValue()) {
						case MultiLineMessageBox.YES:
						case MultiLineMessageBox.NO:
							break; //
						}
					}
				}

		) == MultiLineMessageBox.YES) {
			this.window_CustFinElg.onClose();
		}
		logger.debug("Leaving");
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public LoanEligibility getLoanEligibility() {
		return loanEligibility;
	}
	public void setLoanEligibility(LoanEligibility loanEligibility) {
		this.loanEligibility = loanEligibility;
	}

}

/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */
package com.pennanttech.framework.web.components;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.PennantConstants;
import com.pennant.search.Filter;
import com.pennanttech.framework.core.SearchOperator;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.pff.core.util.DateUtil;

public class SearchFilterControl implements Serializable {
	private static final long serialVersionUID = 5088276424935815163L;

	private String fieldName;
	private Component component;
	private Listbox sortOperator;
	private String defaultValue;
	private Operators operators;

	protected SearchFilterControl() {
		super();
	}

	public SearchFilterControl(String fieldName, Component component, Listbox sortOperator, Operators operators) {
		this.fieldName = fieldName;
		this.component = component;
		this.sortOperator = sortOperator;
		this.operators = operators;

		renderOperators(sortOperator, operators);
	}

	public SearchFilterControl(String fieldName, Component component, Listbox sortOperator, Operators operators,
			String defaultValue) {
		this.fieldName = fieldName;
		this.component = component;
		this.sortOperator = sortOperator;
		this.operators = operators;
		this.defaultValue = defaultValue;

		renderOperators(sortOperator, operators);
	}

	public Filter getFilter() {
		Object value = getValue(component);
		return getFilter(fieldName, value, sortOperator);
	}

	/**
	 * Return Filter object
	 * 
	 * @param fieldName
	 *            Name of the column to filter on
	 * @param value
	 *            The value to compare the column with
	 * @param sortOperator
	 *            The type of comparison to do between the column and the value
	 * @return Filer
	 */
	public static Filter getFilter(String fieldName, Object value, Listbox sortOperator) {

		if (value instanceof String) {
			value = StringUtils.trimToNull((String) value);
		}

		if (value == null) {
			return null;
		}

		Listitem selectedItem = sortOperator.getSelectedItem();

		Filter filter = null;
		if (selectedItem != null) {
			int searchOpId = -1;
			if (selectedItem.getAttribute("data") != null) {
				searchOpId = Integer.parseInt(((ValueLabel) selectedItem.getAttribute("data")).getValue());
			}

			switch (searchOpId) {
			case -1:
				break;
			case Filter.OP_LIKE:
				filter = new Filter(fieldName, "%" + value + "%", searchOpId);
				break;
			case Filter.OP_IN:
			case Filter.OP_NOT_IN:
				filter = new Filter(fieldName, String.valueOf(value).trim().split(","), searchOpId);
				break;
			default:
				filter = new Filter(fieldName, value, searchOpId);
				break;
			}
		}
		return filter;
	}
	
	public static void renderOperators(Listbox sortOperator, Operators operators) {
		sortOperator.setModel(new ListModelList<ValueLabel>(SearchOperator.getOperators(operators)));
		sortOperator.setItemRenderer(new SearchFiltersRender());
		sortOperator.setSelectedIndex(0);
	}
	
	public static void resetFilters(Component component) {
		resetValue(component);
	}
	
	public static void resetFilters(Component component, Listbox sortOperator) {
		sortOperator.setSelectedIndex(0);

		resetFilters(component);
	}
	
	public void resetFilters() {
		sortOperator.setSelectedIndex(0);

		resetValue(component);
	}

	private static Object getValue(Component component) {
		Object value = "";
		if (component instanceof Combobox) {
			Comboitem comboitem = ((Combobox) component).getSelectedItem();
			if (comboitem != null) {
				String selectedValue = comboitem.getValue();
				if ("#".equals(selectedValue)) {
					return null;
				} else {

				}
				return selectedValue;
			}
		}

		if (component instanceof Textbox) {
			return ((Textbox) component).getValue();
		}

		if (component instanceof Intbox) {
			return ((Intbox) component).getValue();
		}

		if (component instanceof Decimalbox) {
			return ((Decimalbox) component).getValue();
		}

		if (component instanceof Checkbox) {
			return ((Checkbox) component).isChecked() ? 1 : 0;
		}

		if (component instanceof Listbox) {
			Listitem listitem = ((Listbox) component).getSelectedItem();
			return listitem == null ? "" : listitem.getValue();
		}

		if (component instanceof Datebox) {
			Date date = ((Datebox) component).getValue();

			if (date != null) {
				return DateUtil.format(date, PennantConstants.DBDateFormat);
			}
		}

		return value;
	}

	private static void resetValue(Component component) {
		if (component instanceof Combobox) {
			Combobox combobox = (Combobox) component;
			Comboitem comboitem = (Comboitem) combobox.getFirstChild();
			combobox.setSelectedItem(comboitem);
		}

		if (component instanceof Textbox) {
			((Textbox) component).setValue("");
		}

		if (component instanceof Intbox) {
			((Intbox) component).setValue(null);
		}

		if (component instanceof Decimalbox) {
			((Decimalbox) component).setText("");
		}

		if (component instanceof Checkbox) {
			((Checkbox) component).setChecked(false);
		}

		if (component instanceof Listbox) {
			((Listbox) component).setSelectedIndex(0);
		}

		if (component instanceof Datebox) {
			((Datebox) component).setValue(null);
		}
	}

	public Component getComponent() {
		return component;
	}

	public Listbox getSortOperator() {
		return sortOperator;
	}

	public String getColumnName() {
		return fieldName;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public Operators getOperators() {
		return operators;
	}

	public static class SearchFiltersRender implements ListitemRenderer<ValueLabel>, Serializable {
		private static final long serialVersionUID = -6882191877151456253L;

		public SearchFiltersRender() {

		}

		@Override
		public void render(Listitem item, ValueLabel valueLabel, int count) throws Exception {

			final Listcell lc = new Listcell(valueLabel.getLabel());
			lc.setParent(item);
			item.setAttribute("data", valueLabel);

			// Default Selecting of EQUAL Parameter List item on Selection
			if (count == 0) {
				if (item.getParent() instanceof Listbox) {
					if (((Listbox) item.getParent()).getSelectedItem() == null) {
						((Listbox) item.getParent()).setSelectedItem(item);
					}
				}
			}
		}

	}
}

package com.pennant.component.extendedfields;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Bandpopup;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.FrequencyBox;
import com.pennant.RateBox;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTEmailValidator;
import com.pennant.util.Constraint.PTMobileNumberValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTPhoneNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.PTWebValidator;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.framework.web.AbstractController;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class ExtendedFieldsGenerator extends AbstractController {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private static final Logger	logger				= Logger.getLogger(ExtendedFieldsGenerator.class);

	private Window				window;
	private Tabs				tabs;
	private Tabpanel			tabpanel;
	private Map<String, Object>	fieldValueMap		= new HashMap<>();
	private boolean				isReadOnly;
	private String				tabHeight;
	private String				labelKey;
	private int					ccyFormat;
	private int					rowWidth;
	private final String		TABPANEL_ID			= "Tab_Panel";
	private Row					row;

	public ExtendedFieldsGenerator() {

	}

	/**
	 * Method for Preparation of Additional Details Tab
	 * 
	 * @param extendedFieldHeader
	 * @param newRecord
	 * @throws ParseException
	 */
	public void renderWindow(ExtendedFieldHeader fieldHeader, boolean newRecord) throws ParseException {
		logger.debug(Literal.ENTERING);

		List<ExtendedFieldDetail> extendedFieldDetails = null;

		if (fieldHeader != null) {
			extendedFieldDetails = fieldHeader.getExtendedFieldDetails();
		}

		if (extendedFieldDetails == null || extendedFieldDetails.isEmpty()) {
			return;
		}

		int columnCount = Integer.parseInt(fieldHeader.getNumberOfColumns());

		List<ExtendedFieldDetail> containers = new ArrayList<ExtendedFieldDetail>();
		List<ExtendedFieldDetail> inputElemetswithoutParents = new ArrayList<ExtendedFieldDetail>();

		// group the Containers and inputElements
		for (ExtendedFieldDetail extendedFieldDetail : extendedFieldDetails) {
			if (extendedFieldDetail.isInputElement()) {
				if (extendedFieldDetail.getParentTag() == null) {
					inputElemetswithoutParents.add(extendedFieldDetail);
				}
			} else {
				containers.add(extendedFieldDetail);
			}
		}

		// render the elements which is not having a parent container
		Collections.sort(inputElemetswithoutParents, new ExtendedFieldsComparator());
		if (!inputElemetswithoutParents.isEmpty()) {
			for (int i = 0; i < inputElemetswithoutParents.size(); i++) {
				ExtendedFieldDetail inputElemetswithoutParent = inputElemetswithoutParents.get(i);
				renderComponents(inputElemetswithoutParent, columnCount, tabpanel, isReadOnly, newRecord, i);
			}
		}

		//render the elements which is having a parent container
		Collections.sort(containers, new ExtendedFieldsComparator());
		for (ExtendedFieldDetail containerElement : containers) {
			Component existting = this.tabpanel.getFellowIfAny(containerElement.getFieldName());
			if (existting == null) {
				prepareGroupdElements(newRecord, columnCount, extendedFieldDetails, containerElement, tabpanel);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Group the elements based on parent child relation between components and render the elements. if
	 * parent contains subParents also it is calling itself, it is a recursive method.
	 * 
	 * @param newRecord
	 * @param columnCount
	 * @param extendedFieldDetails
	 * @param containerElement
	 * @param rootElement
	 * @throws ParseException
	 */
	private void prepareGroupdElements(boolean newRecord, int columnCount,
			List<ExtendedFieldDetail> extendedFieldDetails, ExtendedFieldDetail containerElement, Component rootElement)
			throws ParseException {

		Component parent = createContainer(containerElement, rootElement);

		List<ExtendedFieldDetail> childs = getChilds(extendedFieldDetails, containerElement);
		Collections.sort(childs, new ExtendedFieldsComparator());
		for (int i = 0; i < childs.size(); i++) {
			ExtendedFieldDetail child = childs.get(i);

			//if the child is also a parent then creates the container and call the prepareGroupdElements method to
			//group their child's and then render the elements. if the child is not a parent then render the elements.
			if (!child.isInputElement() && child.getParentTag() != null) {
				Component childContainer = null;
				if (containerElement.getFieldType().equals(ExtendedFieldConstants.FIELDTYPE_TABPANEL)) {
					childContainer = this.tabpanel.getFellowIfAny(TABPANEL_ID + containerElement.getFieldName());
				} else {
					childContainer = this.tabpanel.getFellowIfAny(containerElement.getFieldName());
				}

				prepareGroupdElements(newRecord, columnCount, extendedFieldDetails, child, childContainer);
			} else {
				renderComponents(child, columnCount, parent, isReadOnly, newRecord, i);
			}
		}
	}

	/**
	 * Method to group the list of child's for given parent
	 * 
	 * @param extendedFieldDetails
	 * @param parent
	 * @return
	 */
	private List<ExtendedFieldDetail> getChilds(List<ExtendedFieldDetail> extendedFieldDetails,
			ExtendedFieldDetail parent) {

		List<ExtendedFieldDetail> parentinputElemets = new ArrayList<ExtendedFieldDetail>();
		for (ExtendedFieldDetail extendedFieldDetail : extendedFieldDetails) {
			if (StringUtils.equals(extendedFieldDetail.getParentTag(), parent.getFieldName())) {
				parentinputElemets.add(extendedFieldDetail);
			}
		}
		return parentinputElemets;
	}

	/**
	 * Method for create the Container, it creates the container based on the fieldType and append that container to the
	 * rootElement
	 * 
	 * @param container
	 * @param rootElement
	 * @return
	 */
	private Component createContainer(ExtendedFieldDetail container, Component rootElement) {
		String key = container.getFieldType().trim();
		switch (key) {
		case ExtendedFieldConstants.FIELDTYPE_GROUPBOX:
			Groupbox groupbox = getGroupbox(container);
			rootElement.appendChild(groupbox);
			return groupbox;
		case ExtendedFieldConstants.FIELDTYPE_TABPANEL:
			return getTabpanel(container);
		default:
			return this.tabpanel;

		}
	}

	/**
	 * Method For Create the Component and append that component to the parent Container.
	 * 
	 * @param detail
	 * @param columnCount
	 * @param parentComponent
	 * @param isReadOnly
	 * @param newRecord
	 * @param i
	 * @throws ParseException
	 */
	private void renderComponents(ExtendedFieldDetail detail, int columnCount, Component parentComponent,
			boolean isReadOnly, boolean newRecord, int i) throws ParseException {
		logger.debug(Literal.ENTERING);

		if (rowWidth == 0) {
			rowWidth = 220;//default
		}

		Grid grid = new Grid();
		grid.setStyle("border:0px");
		grid.setSclass("GridLayoutNoBorder");
		parentComponent.appendChild(grid);

		Columns columns = new Columns();
		grid.appendChild(columns);
		Rows rows = new Rows();
		grid.appendChild(rows);

		if (columnCount == 2) {
			columns.appendChild(getColumn("220px"));
			columns.appendChild(getColumn());
			columns.appendChild(getColumn("220px"));
			columns.appendChild(getColumn());
		} else {
			columns.appendChild(new Column("", null, "250px"));
			columns.appendChild(new Column("", null));
		}

		Hbox hbox = null;

		row = getRow(columnCount, row, i);
		hbox = new Hbox();
		row.appendChild(getLabel(detail.getFieldLabel()));
		row.appendChild(hbox);

		String key = detail.getFieldType().trim();

		Component component = null;

		switch (key) {

		case ExtendedFieldConstants.FIELDTYPE_TEXT:
		case ExtendedFieldConstants.FIELDTYPE_MULTILINETEXT:
			appendSpace(detail.isFieldMandatory(), isReadOnly, hbox);
			component = getTextbox(detail);
			break;

		case ExtendedFieldConstants.FIELDTYPE_UPPERTEXT:
			appendSpace(detail.isFieldMandatory(), isReadOnly, hbox);
			component = getUppercasebox(detail);
			break;

		case ExtendedFieldConstants.FIELDTYPE_DATE:
		case ExtendedFieldConstants.FIELDTYPE_DATETIME:
		case ExtendedFieldConstants.FIELDTYPE_TIME:
			appendSpace(detail.isFieldMandatory(), isReadOnly, hbox);
			//Datebox properties Setup
			if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_DATE, detail.getFieldType().trim())
					|| StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_DATETIME, detail.getFieldType().trim())) {
				component = getDatebox(detail, newRecord);
			}
			//Timebox properties Setup
			if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_TIME, detail.getFieldType().trim())) {
				component = getTimebox(detail);

			}
			break;

		case ExtendedFieldConstants.FIELDTYPE_ACTRATE:
		case ExtendedFieldConstants.FIELDTYPE_DECIMAL:
		case ExtendedFieldConstants.FIELDTYPE_PERCENTAGE:
			appendSpace(detail.isFieldMandatory(), isReadOnly, hbox);
			component = getDecimalbox(detail);
			break;

		case ExtendedFieldConstants.FIELDTYPE_AMOUNT:
			component = getCurrencyBox(detail);
			break;

		case ExtendedFieldConstants.FIELDTYPE_STATICCOMBO:
			appendSpace(detail.isFieldMandatory(), isReadOnly, hbox);
			component = getCombobox(detail);
			break;

		case ExtendedFieldConstants.FIELDTYPE_MULTISTATICCOMBO:
			appendSpace(detail.isFieldMandatory(), isReadOnly, hbox);
			component = getBandbox(detail);
			break;

		case ExtendedFieldConstants.FIELDTYPE_EXTENDEDCOMBO:
			component = getExtendedCombobox(detail);
			break;

		case ExtendedFieldConstants.FIELDTYPE_MULTIEXTENDEDCOMBO:
			appendSpace(detail.isFieldMandatory(), isReadOnly, hbox);
			component = getMultiExtendedCombo(detail);
			break;

		case ExtendedFieldConstants.FIELDTYPE_BASERATE:
			component = getRateBox(detail);
			break;

		case ExtendedFieldConstants.FIELDTYPE_BOOLEAN:
			appendSpace(detail.isFieldMandatory(), isReadOnly, hbox);
			component = getCheckbox(detail);
			break;

		case ExtendedFieldConstants.FIELDTYPE_INT:
			appendSpace(detail.isFieldMandatory(), isReadOnly, hbox);
			component = getIntbox(detail);
			break;

		case ExtendedFieldConstants.FIELDTYPE_LONG:
			appendSpace(detail.isFieldMandatory(), isReadOnly, hbox);
			component = getLongbox(detail);
			break;

		case ExtendedFieldConstants.FIELDTYPE_RADIO:
			appendSpace(detail.isFieldMandatory(), isReadOnly, hbox);
			component = getRadiogroup(detail);
			break;

		case ExtendedFieldConstants.FIELDTYPE_ACCOUNT:
			component = getAccountSelectionBox(detail);
			break;

		case ExtendedFieldConstants.FIELDTYPE_FRQ:
			component = getFrequencyBox(detail);
			break;

		case ExtendedFieldConstants.FIELDTYPE_ADDRESS:
			appendSpace(detail.isFieldMandatory(), isReadOnly, hbox);
			component = getTextbox(detail);
			break;

		case ExtendedFieldConstants.FIELDTYPE_PHONE:
			appendSpace(detail.isFieldMandatory(), isReadOnly, hbox);
			component = getPhonebox(detail);
			break;
		default:
			break;
		}
		if (component != null) {
			readOnlyComponent(!detail.isEditable(), component);
			hbox.appendChild(component);
		}

		rows.appendChild(row);

		logger.debug(Literal.LEAVING);
	}

	private Component getColumn(String width) {
		Column column = new Column();
		column.setWidth(width);
		return column;
	}

	private Component getColumn() {
		Column column = new Column();
		return column;
	}

	/**
	 * Method to set validation & Save for Additional Field Details
	 * 
	 * @param extendedFieldDetailList
	 * @param row
	 * @throws ParseException
	 */
	public Map<String, Object> doSave(List<ExtendedFieldDetail> extendedFieldDetailList, boolean isReadOnly)
			throws ParseException {
		logger.debug(Literal.ENTERING);

		Map<String, Object> values = new HashMap<String, Object>();
		List<Component> compList = new ArrayList<Component>();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		if (extendedFieldDetailList == null) {
			return values;
		}

		for (ExtendedFieldDetail detail : extendedFieldDetailList) {
			String id = getComponentId(detail.getFieldName());
			isReadOnly=!detail.isEditable();

			if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_PHONE, detail.getFieldType())) {
				id = "ad_".concat(detail.getFieldName().concat("_CC"));
			}

			Component component = tabpanel.getFellowIfAny(id);
			if (component != null) {
				compList.add(component);

				if (component instanceof CurrencyBox) {
					CurrencyBox currencyBox = (CurrencyBox) component;
					currencyBox.setConstraint("");
					currencyBox.setErrorMessage("");
					if (!isReadOnly && (detail.isFieldMandatory() || currencyBox.getActualValue() != null)) {
						currencyBox.setConstraint(new PTDecimalValidator(detail.getFieldLabel(), getCcyFormat(),
								detail.isFieldMandatory(), false));
					}
					try {
						values.put(detail.getFieldName(),
								PennantApplicationUtil.unFormateAmount(currencyBox.getActualValue(), ccyFormat));
					} catch (WrongValueException we) {
						wve.add(we);
					}
				} else if (component instanceof Decimalbox) {
					Decimalbox decimalbox = (Decimalbox) component;
					decimalbox.setConstraint("");
					decimalbox.setErrorMessage("");
					if (!isReadOnly && (detail.isFieldMandatory() || decimalbox.getValue() != null)) {
						decimalValidation(decimalbox, detail);
					}

					try {
						values.put(detail.getFieldName(), decimalbox.getValue());
					} catch (WrongValueException we) {
						wve.add(we);
					}
				} else if (component instanceof Intbox) {
					Intbox intbox = (Intbox) component;
					intbox.setConstraint("");
					intbox.setErrorMessage("");
					if (!isReadOnly) {
						intbox.setConstraint(new PTNumberValidator(detail.getFieldLabel(), detail.isFieldMandatory(),
								false, Integer.parseInt(String.valueOf(detail.getFieldMinValue())),
								Integer.parseInt(String.valueOf(detail.getFieldMaxValue()))));
					}

					try {
						values.put(detail.getFieldName(), intbox.intValue());
					} catch (WrongValueException we) {
						wve.add(we);
					}
				} else if (component instanceof Longbox) {
					Longbox longbox = (Longbox) component;
					longbox.setConstraint("");
					longbox.setErrorMessage("");
					if (!isReadOnly) {//TODO: Check for LONG Validation
						longbox.setConstraint(new PTNumberValidator(detail.getFieldLabel(), detail.isFieldMandatory(),
								false, Integer.parseInt(String.valueOf(detail.getFieldMinValue())),
								Integer.parseInt(String.valueOf(detail.getFieldMaxValue()))));
					}

					try {
						values.put(detail.getFieldName(), longbox.longValue());
					} catch (WrongValueException we) {
						wve.add(we);
					}

				} else if (component instanceof AccountSelectionBox) {
					AccountSelectionBox accSelectionBox = (AccountSelectionBox) component;
					accSelectionBox.setConstraint("");
					accSelectionBox.setErrorMessage("");

					if (!isReadOnly && detail.isFieldMandatory()) {
						accSelectionBox.setConstraint(new PTStringValidator(detail.getFieldLabel(), null, true));
					}

					try {
						accSelectionBox.validateValue();
						values.put(detail.getFieldName(), accSelectionBox.getValue());
					} catch (WrongValueException we) {
						wve.add(we);
					} catch (InterruptedException e) {
						logger.error(e);
					}
				} else if (component instanceof FrequencyBox) {
					FrequencyBox frqBox = (FrequencyBox) component;

					try {
						if (!isReadOnly && detail.isFieldMandatory()) {
							frqBox.isValidComboValue();
						}
						values.put(detail.getFieldName(), frqBox.getValue());
					} catch (WrongValueException we) {
						wve.add(we);
					}
				} else if (component instanceof RateBox) {
					RateBox rateBox = (RateBox) component;

					if (!isReadOnly && detail.isFieldMandatory()) {
						rateBox.setBaseConstraint(
								new PTStringValidator(detail.getFieldLabel(), null, detail.isFieldMandatory(), true));
					}
					try {
						values.put(detail.getFieldName().concat("_BR"), rateBox.getBaseValue());
						values.put(detail.getFieldName().concat("_SR"), rateBox.getSpecialValue());
						values.put(detail.getFieldName().concat("_MR"), rateBox.getMarginValue());
					} catch (WrongValueException we) {
						wve.add(we);
					}
				} else if (component instanceof Timebox) {
					Timebox timebox = (Timebox) component;
					timebox.setConstraint("");
					timebox.setErrorMessage("");

					try {
						Date timeValue = DateUtil.parse(DateUtil.format(timebox.getValue(), DateFormat.LONG_TIME),
								DateFormat.LONG_TIME);
						if (!isReadOnly && (detail.isFieldMandatory() && timeValue == null)) {
							throw new WrongValueException(timebox,
									Labels.getLabel("FIELD_NO_EMPTY", new String[] { detail.getFieldLabel() }));
						}

						values.put(detail.getFieldName(), timeValue);
					} catch (WrongValueException we) {
						wve.add(we);
					}
				} else if (component instanceof Datebox) {
					Datebox datebox = (Datebox) component;
					datebox.setConstraint("");
					datebox.setErrorMessage("");
					if (!isReadOnly && (detail.isFieldMandatory() || datebox.getValue() != null)) {
						dateValidation(datebox, detail);
					}

					try {
						if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_DATE, detail.getFieldType())) {
							values.put(detail.getFieldName(), DateUtil
									.parse(DateUtil.formatToShortDate(datebox.getValue()), DateFormat.SHORT_DATE));
						} else {
							values.put(detail.getFieldName(),
									DateUtil.parse(DateUtil.format(datebox.getValue(), DateFormat.LONG_DATE_TIME),
											DateFormat.LONG_DATE_TIME));
						}
					} catch (WrongValueException we) {
						wve.add(we);
					}
				} else if (component instanceof ExtendedCombobox) {
					ExtendedCombobox extendedCombobox = (ExtendedCombobox) component;
					extendedCombobox.setConstraint("");
					extendedCombobox.setErrorMessage("");
					if (!isReadOnly && detail.isFieldMandatory()) {
						extendedCombobox.setConstraint(
								new PTStringValidator(detail.getFieldLabel(), null, detail.isFieldMandatory(), true));
					}

					try {
						values.put(detail.getFieldName(), extendedCombobox.getValue());
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
								throw new WrongValueException(combobox,
										Labels.getLabel("STATIC_INVALID", new String[] { detail.getFieldLabel() }));
							}
						}

						values.put(detail.getFieldName(), combobox.getSelectedItem().getValue().toString());
					} catch (WrongValueException we) {
						wve.add(we);
					}
				} else if (component instanceof Bandbox) {
					Bandbox bandbox = (Bandbox) component;
					bandbox.setConstraint("");
					bandbox.setErrorMessage("");

					try {
						if (detail.isFieldMandatory()) {
							if (!isReadOnly && StringUtils.isEmpty(bandbox.getValue())) {
								bandbox.setConstraint(
										new PTStringValidator(detail.getFieldLabel(), null, detail.isFieldMandatory()));
							}
						}

						values.put(detail.getFieldName(), bandbox.getValue());
					} catch (WrongValueException we) {
						wve.add(we);
					}
				} else if (component instanceof Radiogroup) {
					Radiogroup radiogroup = (Radiogroup) component;
					if (detail.isFieldMandatory()) {
						if (radiogroup.getSelectedItem() == null) {
							try {
								throw new WrongValueException(radiogroup,
										Labels.getLabel("STATIC_INVALID", new String[] { detail.getFieldLabel() }));
							} catch (WrongValueException we) {
								wve.add(we);
							}
						} else {
							values.put(detail.getFieldName(), radiogroup.getSelectedItem().getValue().toString());
						}
					} else {
						String radioGroupValue = "";
						if (radiogroup.getSelectedItem() != null) {
							radioGroupValue = radiogroup.getSelectedItem().getValue().toString();
						}
						values.put(detail.getFieldName(), radioGroupValue);
					}
				} else if (component instanceof Checkbox) {
					Checkbox checkbox = (Checkbox) component;
					values.put(detail.getFieldName(), checkbox.isChecked() ? true : false);
				} else if (component instanceof Textbox) {

					if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_PHONE, detail.getFieldType())) {

						Textbox countryCode = (Textbox) component;
						Textbox areCode = (Textbox) tabpanel.getFellowIfAny(id.replace("_CC", "_AC"));
						Textbox subCode = (Textbox) tabpanel.getFellowIfAny(id.replace("_CC", "_SC"));

						if (!isReadOnly) {
							countryCode.setConstraint(new PTPhoneNumberValidator(
									detail.getFieldLabel().concat(" Country Code "), detail.isFieldMandatory(), 1));
							areCode.setConstraint(new PTPhoneNumberValidator(
									detail.getFieldLabel().concat(" Area Code "), detail.isFieldMandatory(), 2));
							subCode.setConstraint(new PTPhoneNumberValidator(
									detail.getFieldLabel().concat(" Subsidary Code "), detail.isFieldMandatory(), 3));
						}

						try {
							values.put(detail.getFieldName().concat("_CC"), countryCode.getValue());
							values.put(detail.getFieldName().concat("_AC"), areCode.getValue());
							values.put(detail.getFieldName().concat("_SC"), subCode.getValue());
						} catch (WrongValueException we) {
							wve.add(we);
						}

					} else {
						Textbox textbox = (Textbox) component;
						textbox.setConstraint("");
						textbox.setErrorMessage("");
						if (!isReadOnly && (detail.isFieldMandatory() || textbox.getValue() != null)) {

							String regEx = StringUtils.trimToEmpty(detail.getFieldConstraint());

							switch (regEx) {
							case "REGEX_EMAIL":
								textbox.setConstraint(
										new PTEmailValidator(detail.getFieldLabel(), detail.isFieldMandatory()));
								break;
							case "REGEX_WEB":
								textbox.setConstraint(
										new PTWebValidator(detail.getFieldLabel(), detail.isFieldMandatory()));
								break;
							case "REGEX_TELEPHONE_FAX":
								textbox.setConstraint(
										new PTPhoneNumberValidator(detail.getFieldLabel(), detail.isFieldMandatory()));
								break;
							case "REGEX_MOBILE":
								textbox.setConstraint(
										new PTMobileNumberValidator(detail.getFieldLabel(), detail.isFieldMandatory()));
								break;
							default:
								if (textbox.isReadonly() || StringUtils.isEmpty(detail.getFieldConstraint())) {
									textbox.setConstraint(new PTStringValidator(detail.getFieldLabel(), null,
											detail.isFieldMandatory()));
								} else {
									textbox.setConstraint(new PTStringValidator(detail.getFieldLabel(),
											detail.getFieldConstraint(), detail.isFieldMandatory()));
								}
								break;
							}
						}
						try {
							values.put(detail.getFieldName(), textbox.getValue());
						} catch (WrongValueException we) {
							wve.add(we);
						}
					}
				}
			}
		}
		showErrorDetails(wve, compList);

		logger.debug(Literal.LEAVING);
		return values;
	}

	/**
	 * Method for Date Validation setting
	 * 
	 * @param datebox
	 * @param detail
	 */
	private void dateValidation(Datebox datebox, ExtendedFieldDetail detail) {
		String[] value = detail.getFieldConstraint().split(",");

		PTDateValidator dateValidator = null;

		String label = detail.getFieldLabel();
		boolean isMandatory = detail.isFieldMandatory();

		switch (value[0]) {
		case "RANGE":
			dateValidator = new PTDateValidator(label, isMandatory,
					DateUtility.getUtilDate(value[1], PennantConstants.dateFormat),
					DateUtility.getUtilDate(value[2], PennantConstants.dateFormat), true);
			break;
		case "FUTURE_DAYS":
			dateValidator = new PTDateValidator(label, isMandatory, null,
					DateUtility.addDays(DateUtility.getAppDate(), Integer.parseInt(value[1])), true);
			break;
		case "PAST_DAYS":
			dateValidator = new PTDateValidator(label, isMandatory,
					DateUtility.addDays(DateUtility.getAppDate(), -(Integer.parseInt(value[1]))), null, true);
			break;
		case "FUTURE_TODAY":
			dateValidator = new PTDateValidator(label, isMandatory, true, null, true);
			break;
		case "PAST_TODAY":
			if (!StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_DATETIME, detail.getFieldType().trim())) {
				dateValidator = new PTDateValidator(label, isMandatory, null, true, true);
			} else {
				dateValidator = new PTDateValidator(label, isMandatory, null,
						DateUtility.addDays(DateUtility.getAppDate(), 1), false);
			}
			break;
		case "FUTURE":
			dateValidator = new PTDateValidator(label, isMandatory, false, null, false);
			break;
		case "PAST":
			dateValidator = new PTDateValidator(label, isMandatory, null, false, false);
			break;

		default:
			break;
		}
		datebox.setConstraint(dateValidator);
	}

	/**
	 * Method for Date Validation setting
	 * 
	 * @param datebox
	 * @param detail
	 */
	private void decimalValidation(Decimalbox decimalbox, ExtendedFieldDetail detail) {

		PTDecimalValidator decimalValidator = null;

		long minValue = detail.getFieldMinValue();
		long maxValue = detail.getFieldMaxValue();

		if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_ACTRATE, detail.getFieldType().trim())) {

			if (maxValue != 0 && maxValue > Math.pow(10, detail.getFieldLength() - detail.getFieldPrec()) - 1) {
				maxValue = (long) (Math.pow(10, detail.getFieldLength() - detail.getFieldPrec()) - 1);
			}
			decimalValidator = new PTDecimalValidator(detail.getFieldLabel(), detail.getFieldPrec(),
					detail.isFieldMandatory(), false, minValue, maxValue);

		} else if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_PERCENTAGE, detail.getFieldType().trim())) {

			if (maxValue != 0 && maxValue > 100) {
				maxValue = 100;
			}
			decimalValidator = new PTDecimalValidator(detail.getFieldLabel(), detail.getFieldPrec(),
					detail.isFieldMandatory(), false, minValue, maxValue);

		} else if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_DECIMAL, detail.getFieldType().trim())) {

			if (maxValue != 0 && maxValue > Math.pow(10, detail.getFieldLength()) - 1) {
				maxValue = (long) Math.pow(10, detail.getFieldLength()) - 1;
			}
			decimalValidator = new PTDecimalValidator(detail.getFieldLabel(), detail.getFieldPrec(),
					detail.isFieldMandatory(), false, Math.pow(10, detail.getFieldLength()) - 1);
		}
		decimalbox.setConstraint(decimalValidator);
	}

	/**
	 * 
	 * On multiple Selection List box item selected
	 * 
	 * 
	 */
	public final class onMultiSelectionItemSelected implements EventListener<Event> {

		public onMultiSelectionItemSelected() {
		}

		public void onEvent(Event event) throws Exception {
			Checkbox checkbox = (Checkbox) event.getTarget();
			Listitem listItem = (Listitem) checkbox.getParent().getParent();
			Bandbox bandBox = (Bandbox) listItem.getParent().getParent().getParent();
			bandBox.setErrorMessage("");
			String displayString = "";
			Map<String, String> valuesMap = new LinkedHashMap<String, String>();
			String[] bandBoxValues = bandBox.getValue().split(",");
			for (int i = 0; i < bandBoxValues.length; i++) {
				valuesMap.put(bandBoxValues[i], bandBoxValues[i]);
			}
			if (checkbox.isChecked()) {
				valuesMap.put(checkbox.getValue().toString(), checkbox.getValue().toString());
			} else {
				valuesMap.remove(checkbox.getValue().toString());
			}
			for (String values : valuesMap.keySet()) {
				displayString = displayString.concat(("").equals(bandBox.getValue().trim()) ? values : values + ",");
			}

			//Excluding Last added Comma
			if (StringUtils.trimToEmpty(displayString).endsWith(",")) {
				displayString = displayString.substring(0, displayString.length() - 1);
			}

			bandBox.setValue(displayString);
			bandBox.setTooltiptext(displayString);
			valuesMap = null;
		}
	}

	/**
	 * Method for Multi Selection Applciation list value
	 * 
	 * @param event
	 */
	public final class onMultiSelButtonClick implements EventListener<Event> {

		public onMultiSelButtonClick() {
		}

		@SuppressWarnings("unchecked")
		public void onEvent(Event event) throws Exception {
			Button button = (Button) event.getTarget();
			List<Object> paras = (List<Object>) button.getAttribute("data");
			String moduleCode = String.valueOf(paras.get(0));
			Textbox textbox = (Textbox) paras.get(1);
			Window window = (Window) paras.get(2);

			Object dataObject = MultiSelectionSearchListBox.show(window, moduleCode, textbox.getValue(), null);

			if (dataObject instanceof String) {
				textbox.setValue(dataObject.toString());
			} else {
				HashMap<String, Object> details = (HashMap<String, Object>) dataObject;
				if (details != null) {
					String tempValue = "";
					List<String> valueKeys = new ArrayList<>(details.keySet());
					for (int i = 0; i < valueKeys.size(); i++) {
						if (StringUtils.isEmpty(valueKeys.get(i))) {
							continue;
						}
						if (i == 0) {
							tempValue = valueKeys.get(i);
						} else {
							tempValue = tempValue.concat(",".concat(valueKeys.get(i)));
						}
					}
					textbox.setValue(tempValue);
				}
			}
		}

	}

	/**
	 * Method for Showing Error Details
	 * 
	 * @param wve
	 */
	private void showErrorDetails(ArrayList<WrongValueException> wve, List<Component> compList) {
		logger.debug(Literal.ENTERING);

		if (wve.size() > 0) {
			for (Component component : compList) {
				if (component instanceof CurrencyBox) {
					CurrencyBox currencyBox = (CurrencyBox) component;
					currencyBox.setConstraint("");
					currencyBox.setErrorMessage("");

				} else if (component instanceof Decimalbox) {
					Decimalbox decimalbox = (Decimalbox) component;
					decimalbox.setConstraint("");
					decimalbox.setErrorMessage("");

				} else if (component instanceof ExtendedCombobox) {
					ExtendedCombobox extendedCombobox = (ExtendedCombobox) component;
					extendedCombobox.setConstraint("");
					extendedCombobox.setErrorMessage("");

				} else if (component instanceof AccountSelectionBox) {
					AccountSelectionBox accountSelectionBox = (AccountSelectionBox) component;
					accountSelectionBox.setConstraint("");
					accountSelectionBox.setErrorMessage("");

				} else if (component instanceof FrequencyBox) {
					FrequencyBox frequencyBox = (FrequencyBox) component;
					frequencyBox.setErrorMessage("");

				} else if (component instanceof Intbox) {
					Intbox intbox = (Intbox) component;
					intbox.setConstraint("");
					intbox.setErrorMessage("");

				} else if (component instanceof Longbox) {
					Longbox longbox = (Longbox) component;
					longbox.setConstraint("");
					longbox.setErrorMessage("");

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

				} else if (component instanceof Bandbox) {
					Bandbox bandbox = (Bandbox) component;
					bandbox.setConstraint("");
					bandbox.setErrorMessage("");

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
			logger.debug(Literal.LEAVING);
			throw new WrongValuesException(wvea);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for getting the Row
	 * 
	 * @param int
	 *            columnCount
	 * @param Row
	 *            row
	 * @param int
	 *            i
	 * @return Row row
	 */
	private Row getRow(int columnCount, Row row, int i) {

		if (columnCount == 2) {
			if (i % 2 == 0) {
				row = new Row();
			}
		} else {
			row = new Row();
		}
		return row;
	}

	/**
	 * Method for getting the label
	 * 
	 * @param String
	 *            labelName
	 * @return Label label
	 */
	private Label getLabel(String labelName) {
		Label label = new Label(labelName);
		return label;
	}

	/**
	 * Method for adding space component to define mandatory field or not
	 * 
	 * @param isMand
	 * @param isReadOnly
	 * @param hlayout
	 */
	private void appendSpace(boolean isMand, boolean isReadOnly, Hbox hbox) {
		Space space = new Space();
		space.setWidth("2px");
		if (isMand && !isReadOnly) {
			space.setSclass(PennantConstants.mandateSclass);
		}
		hbox.appendChild(space);
	}

	/**
	 * Method for create Textbox based on the Extended field details.
	 * 
	 * @param detail
	 * @return Textbox
	 */
	private Textbox getTextbox(ExtendedFieldDetail detail) {
		Textbox textbox = null;
		textbox = new Textbox();
		textbox.setId(getComponentId(detail.getFieldName()));
		textbox.setMaxlength(detail.getFieldLength());
		textbox.setReadonly(isReadOnly);
		if (StringUtils.equals(detail.getFieldType().trim(), ExtendedFieldConstants.FIELDTYPE_ADDRESS)) {
			textbox.setMaxlength(100);
			textbox.setWidth("250px");
		}

		// Data Setting
		if (fieldValueMap.containsKey(detail.getFieldName()) && fieldValueMap.get(detail.getFieldName()) != null
				&& StringUtils.isNotBlank(fieldValueMap.get(detail.getFieldName()).toString())) {
			textbox.setValue(fieldValueMap.get(detail.getFieldName()).toString());
		} else if (StringUtils.isNotBlank(detail.getFieldDefaultValue())) {
			textbox.setValue(detail.getFieldDefaultValue());
		}
		if (StringUtils.equals(detail.getFieldType().trim(), ExtendedFieldConstants.FIELDTYPE_ADDRESS)) {
			return textbox;
		}
		// Multiple-Line Text box Preparation
		if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_MULTILINETEXT, detail.getFieldType().trim())) {
			textbox.setRows(detail.getMultiLine());
		}

		// TextBox Width Setting 
		if (detail.getFieldLength() <= 20) {
			textbox.setWidth(detail.getFieldLength() * 10 + "px");
		} else {
			textbox.setWidth("250px");
		}

		return textbox;
	}

	/**
	 * Method for create Uppercasebox based on the Extended field details.
	 * 
	 * @param detail
	 * @return Uppercasebox
	 */
	private Uppercasebox getUppercasebox(ExtendedFieldDetail detail) {

		Uppercasebox uppercasebox = new Uppercasebox();
		uppercasebox.setId(getComponentId(detail.getFieldName()));
		uppercasebox.setMaxlength(detail.getFieldLength());
		uppercasebox.setReadonly(isReadOnly);

		if (fieldValueMap.containsKey(detail.getFieldName()) && fieldValueMap.get(detail.getFieldName()) != null
				&& StringUtils.isNotBlank(fieldValueMap.get(detail.getFieldName()).toString())) {
			uppercasebox.setValue(fieldValueMap.get(detail.getFieldName()).toString());
		} else if (StringUtils.isNotBlank(detail.getFieldDefaultValue())) {
			uppercasebox.setValue(detail.getFieldDefaultValue());
		}

		if (detail.getFieldLength() <= 20) {
			uppercasebox.setWidth(detail.getFieldLength() * 10 + "px");
		} else {
			uppercasebox.setWidth("250px");
		}
		return uppercasebox;
	}

	/**
	 * Method for create Datebox based on the Extended field details.
	 * 
	 * @param detail
	 * @param newRecord
	 * @return Datebox
	 */
	private Datebox getDatebox(ExtendedFieldDetail detail, boolean newRecord) {

		Datebox datebox = new Datebox();
		datebox.setId(getComponentId(detail.getFieldName()));
		datebox.setDisabled(isReadOnly);

		if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_DATE, detail.getFieldType().trim())) {
			datebox.setFormat(DateFormat.SHORT_DATE.getPattern());
			datebox.setWidth("100px");
		} else if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_DATETIME, detail.getFieldType().trim())) {
			datebox.setFormat(DateFormat.SHORT_DATE_TIME.getPattern());
			datebox.setWidth("150px");
		}

		// Data Setting
		if (fieldValueMap.containsKey(detail.getFieldName()) && fieldValueMap.get(detail.getFieldName()) != null
				&& StringUtils.isNotBlank(fieldValueMap.get(detail.getFieldName()).toString())) {
			if (newRecord) {
				try {
					Object dateVal = fieldValueMap.get(detail.getFieldName());
					if (dateVal != null) {
						Date date = DateUtility.parse(dateVal.toString(), DateFormat.SHORT_DATE.getPattern());
						datebox.setValue(date);
					}
				} catch (Exception e) {
					logger.error("Exception :", e);
				}
			} else {
				datebox.setValue((Date) fieldValueMap.get(detail.getFieldName()));
			}
		} else if (StringUtils.isNotBlank(detail.getFieldDefaultValue())) {

			if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_DATE, detail.getFieldType().trim())) {

				if (StringUtils.equals(ExtendedFieldConstants.DFTDATETYPE_APPDATE, detail.getFieldDefaultValue())) {
					datebox.setValue(DateUtility.getAppDate());
				} else if (StringUtils.equals(ExtendedFieldConstants.DFTDATETYPE_SYSDATE,
						detail.getFieldDefaultValue())) {
					datebox.setValue(DateUtility.getSysDate());
				}

			} else if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_DATETIME, detail.getFieldType().trim())) {
				if (StringUtils.equals(ExtendedFieldConstants.DFTDATETYPE_APPDATE, detail.getFieldDefaultValue())) {
					datebox.setText(DateUtility.getAppDate(DateFormat.SHORT_DATE_TIME));
				} else if (StringUtils.equals(ExtendedFieldConstants.DFTDATETYPE_SYSDATE,
						detail.getFieldDefaultValue())) {
					datebox.setText(DateUtility.getSysDate(DateFormat.SHORT_DATE_TIME));
				}
			}
		}
		return datebox;
	}

	/**
	 * Method for create Timebox based on the Extended field details.
	 * 
	 * @param detail
	 * @return Timebox
	 */
	private Timebox getTimebox(ExtendedFieldDetail detail) {

		Timebox timebox = new Timebox();
		timebox.setFormat(PennantConstants.timeFormat);
		timebox.setId(getComponentId(detail.getFieldName()));
		timebox.setWidth("80px");
		timebox.setButtonVisible(!isReadOnly);
		timebox.setDisabled(isReadOnly);

		// Data Setting
		if (fieldValueMap.containsKey(detail.getFieldName()) && fieldValueMap.get(detail.getFieldName()) != null
				&& StringUtils.isNotBlank(fieldValueMap.get(detail.getFieldName()).toString())) {
			timebox.setValue((Date) fieldValueMap.get(detail.getFieldName()));
		} else if (StringUtils.isNotBlank(detail.getFieldDefaultValue())) {
			if (StringUtils.equals(ExtendedFieldConstants.DFTDATETYPE_SYSTIME, detail.getFieldDefaultValue())) {
				timebox.setValue(DateUtility.getTimestamp(new Date()));
			}
		}
		return timebox;
	}

	/**
	 * Method for create Decimalbox based on the Extended field details.
	 * 
	 * @param detail
	 * @return Decimalbox
	 */
	private Decimalbox getDecimalbox(ExtendedFieldDetail detail) {
		Decimalbox decimalbox = new Decimalbox();
		decimalbox.setStyle("text-align:right");
		decimalbox.setId(getComponentId(detail.getFieldName()));
		decimalbox.setWidth(detail.getFieldLength() * 10 + "px");
		decimalbox.setMaxlength(detail.getFieldLength() + 1);
		decimalbox.setScale(detail.getFieldPrec());
		decimalbox.setDisabled(isReadOnly);
		String fieldType = StringUtils.trimToEmpty(detail.getFieldType());

		// Format Setting based on Field Type
		if (StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_ACTRATE)) {
			decimalbox.setFormat(PennantApplicationUtil.getRateFormate(detail.getFieldPrec()));
		} else if (StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_PERCENTAGE)) {
			decimalbox.setFormat(PennantConstants.percentageFormate2);

		} else if (StringUtils.equals(fieldType, ExtendedFieldConstants.FIELDTYPE_DECIMAL)) {
			decimalbox.setFormat(PennantApplicationUtil.getAmountFormate(detail.getFieldPrec()));
		}

		// Data Setting
		if (fieldValueMap.containsKey(detail.getFieldName()) && fieldValueMap.get(detail.getFieldName()) != null
				&& StringUtils.isNotBlank(fieldValueMap.get(detail.getFieldName()).toString())) {
			decimalbox.setValue(new BigDecimal(fieldValueMap.get(detail.getFieldName()).toString()));
		} else if (StringUtils.isNotBlank(detail.getFieldDefaultValue())) {
			decimalbox.setValue(new BigDecimal(detail.getFieldDefaultValue()));
		} else {
			decimalbox.setValue(BigDecimal.ZERO);
		}
		return decimalbox;

	}

	/**
	 * Method for create CurrencyBox based on the Extended field details.
	 * 
	 * @param detail
	 * @return CurrencyBox
	 */
	private CurrencyBox getCurrencyBox(ExtendedFieldDetail detail) {
		CurrencyBox currencyBox = new CurrencyBox();
		currencyBox.setId(getComponentId(detail.getFieldName()));
		currencyBox.setProperties(detail.isFieldMandatory(), getCcyFormat());
		currencyBox.setReadonly(isReadOnly);

		if (fieldValueMap.containsKey(detail.getFieldName()) && fieldValueMap.get(detail.getFieldName()) != null
				&& StringUtils.isNotBlank(fieldValueMap.get(detail.getFieldName()).toString())) {
			currencyBox.setValue(PennantApplicationUtil
					.formateAmount(new BigDecimal(fieldValueMap.get(detail.getFieldName()).toString()), ccyFormat));
		} else if (StringUtils.isNotBlank(detail.getFieldDefaultValue())) {
			currencyBox.setValue(
					PennantApplicationUtil.formateAmount(new BigDecimal(detail.getFieldDefaultValue()), ccyFormat));
		} else {
			currencyBox.setValue(BigDecimal.ZERO);
		}
		return currencyBox;
	}

	/**
	 * Method for create Combobox based on the Extended field details.
	 * 
	 * @param detail
	 * @return Combobox
	 */
	private Combobox getCombobox(ExtendedFieldDetail detail) {
		String[] staticList = null;
		Combobox combobox = new Combobox();
		combobox.setId(getComponentId(detail.getFieldName()));
		combobox.setDisabled(isReadOnly);
		if (detail.getFieldLength() < 10) {
			combobox.setWidth("100px");
		} else {
			combobox.setWidth(detail.getFieldLength() * 10 + "px");
		}

		// Data Rendering and Setting existing value
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		combobox.setReadonly(true);
		combobox.setSelectedIndex(0);

		staticList = detail.getFieldList().split(",");
		for (int j = 0; j < staticList.length; j++) {

			comboitem = new Comboitem();
			comboitem.setValue(staticList[j]);
			comboitem.setLabel(staticList[j]);
			combobox.appendChild(comboitem);

			if (fieldValueMap.containsKey(detail.getFieldName()) && fieldValueMap.get(detail.getFieldName()) != null
					&& StringUtils.equals(fieldValueMap.get(detail.getFieldName()).toString(), staticList[j])) {
				combobox.setSelectedItem(comboitem);
			}
		}
		return combobox;
	}

	/**
	 * Method for create Bandbox based on the Extended field details.
	 * 
	 * @param detail
	 * @return Bandbox
	 */
	private Bandbox getBandbox(ExtendedFieldDetail detail) {
		String[] staticList = null;
		Bandbox bandBox = new Bandbox();
		Listbox listBox = new Listbox();
		bandBox.setId(getComponentId(detail.getFieldName()));
		bandBox.setReadonly(true);
		bandBox.setTabindex(-1);
		bandBox.setDisabled(isReadOnly);

		Bandpopup bandpopup = new Bandpopup();
		listBox.setMultiple(true);
		listBox.setDisabled(true);
		bandpopup.appendChild(listBox);
		bandBox.appendChild(bandpopup);

		staticList = detail.getFieldList().split(",");
		int maxFieldLength = 0;
		for (int j = 0; j < staticList.length; j++) {

			Listitem listItem = new Listitem();
			Listcell listCell = new Listcell();
			Checkbox checkBox = new Checkbox();
			checkBox.addEventListener("onCheck", new onMultiSelectionItemSelected());
			checkBox.setValue(staticList[j]);

			Label label = new Label(staticList[j]);
			label.setStyle("padding-left:5px");
			listCell.setValue(staticList[j]);
			listCell.appendChild(checkBox);
			listCell.appendChild(label);
			listItem.appendChild(listCell);
			listBox.appendChild(listItem);

			if (maxFieldLength < staticList[j].length()) {
				maxFieldLength = staticList[j].length();
			}

			if (fieldValueMap.containsKey(detail.getFieldName()) && fieldValueMap.get(detail.getFieldName()) != null
					&& StringUtils.contains(fieldValueMap.get(detail.getFieldName()).toString(), staticList[j])) {
				checkBox.setChecked(true);
				bandBox.setValue(fieldValueMap.get(detail.getFieldName()).toString());
			}
		}

		if (maxFieldLength < 10) {
			bandBox.setWidth("100px");
			listBox.setWidth("100px");
		} else {

			int length = maxFieldLength * 10;
			if (length > 220) {
				length = 220;
			}
			bandBox.setWidth(length + "px");
			listBox.setWidth(length + "px");
		}
		return bandBox;
	}

	/**
	 * Method for create ExtendedCombobox based on the Extended field details.
	 * 
	 * @param detail
	 * @return ExtendedCombobox
	 */
	private ExtendedCombobox getExtendedCombobox(ExtendedFieldDetail detail) {
		ExtendedCombobox extendedCombobox = new ExtendedCombobox();
		extendedCombobox.setId(getComponentId(detail.getFieldName()));
		extendedCombobox.setReadonly(isReadOnly);

		// Module Parameters Identification from Module Mapping
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap(detail.getFieldList());
		String[] lovefields = moduleMapping.getLovFields();
		if (lovefields.length >= 2) {
			extendedCombobox.setProperties(detail.getFieldList(), lovefields[0], lovefields[1],
					detail.isFieldMandatory(), 8);
		}

		//Data Setting
		if (fieldValueMap.containsKey(detail.getFieldName()) && fieldValueMap.get(detail.getFieldName()) != null
				&& StringUtils.isNotBlank(fieldValueMap.get(detail.getFieldName()).toString())) {
			extendedCombobox.setValue(fieldValueMap.get(detail.getFieldName()).toString());
		}
		return extendedCombobox;
	}

	/**
	 * Method for create MultiExtendedCombox based on the Extended field details.
	 * 
	 * @param detail
	 * @return Hbox
	 */
	private Hbox getMultiExtendedCombo(ExtendedFieldDetail detail) {
		Hbox extHbox = new Hbox();
		Textbox textbox = new Textbox();
		textbox.setId("ad_" + detail.getFieldName());
		textbox.setReadonly(true);

		// Data Setting
		if (fieldValueMap.containsKey(detail.getFieldName()) && fieldValueMap.get(detail.getFieldName()) != null
				&& StringUtils.isNotBlank(fieldValueMap.get(detail.getFieldName()).toString())) {
			textbox.setValue(fieldValueMap.get(detail.getFieldName()).toString());
		}
		extHbox.appendChild(textbox);

		Button button = null;
		button = new Button();
		button.setImage("/images/icons/search.png");
		button.setVisible(!isReadOnly);
		extHbox.appendChild(button);

		if (!isReadOnly) {
			List<Object> list = new ArrayList<Object>();
			list.add(detail.getFieldList());
			list.add(textbox);
			list.add(this.window);
			button.setAttribute("data", list);
			button.addEventListener("onClick", new onMultiSelButtonClick());
		}
		return extHbox;
	}

	/**
	 * Method for create Checkbox based on the Extended field details.
	 * 
	 * @param detail
	 * @return Checkbox
	 */
	private Checkbox getCheckbox(ExtendedFieldDetail detail) {
		Checkbox checkbox = new Checkbox();
		checkbox.setId(getComponentId(detail.getFieldName()));
		checkbox.setDisabled(isReadOnly);

		//data Setting
		if (fieldValueMap.containsKey(detail.getFieldName()) && fieldValueMap.get(detail.getFieldName()) != null
				&& StringUtils.isNotBlank(fieldValueMap.get(detail.getFieldName()).toString())) {
			//checkbox.setChecked((boolean) fieldValueMap.get(detail.getFieldName()));
		if(App.DATABASE == Database.POSTGRES){
			checkbox.setChecked(
					fieldValueMap.get(detail.getFieldName()).toString().equals("true") ? true : false);
		}else{
			checkbox.setChecked(
					Integer.parseInt(fieldValueMap.get(detail.getFieldName()).toString()) == 1 ? true : false);
		}
			
		} else if (StringUtils.isNotBlank(detail.getFieldDefaultValue())) {

			if (StringUtils.equals(PennantConstants.YES, detail.getFieldDefaultValue())) {
				checkbox.setChecked(true);
			} else {
				checkbox.setChecked(false);
			}
		}
		return checkbox;
	}

	/**
	 * Method for create Intbox based on the Extended field details.
	 * 
	 * @param detail
	 * @return Intbox
	 */
	private Intbox getIntbox(ExtendedFieldDetail detail) {
		Intbox intbox = new Intbox();
		intbox.setId(getComponentId(detail.getFieldName()));
		intbox.setReadonly(isReadOnly);
		intbox.setMaxlength(detail.getFieldLength());

		//Data Setting
		if (fieldValueMap.containsKey(detail.getFieldName()) && fieldValueMap.get(detail.getFieldName()) != null
				&& StringUtils.isNotBlank(fieldValueMap.get(detail.getFieldName()).toString())) {
			intbox.setValue(Integer.parseInt(fieldValueMap.get(detail.getFieldName()).toString()));
		} else if (StringUtils.isNotBlank(detail.getFieldDefaultValue())) {
			intbox.setValue(Integer.parseInt(detail.getFieldDefaultValue().toString()));
		}
		return intbox;
	}

	/**
	 * Method for create Longbox based on the Extended field details.
	 * 
	 * @param detail
	 * @return Longbox
	 */
	private Longbox getLongbox(ExtendedFieldDetail detail) {
		Longbox longbox = new Longbox();
		longbox.setId(getComponentId(detail.getFieldName()));
		longbox.setReadonly(isReadOnly);

		//Data Setting
		if (fieldValueMap.containsKey(detail.getFieldName()) && fieldValueMap.get(detail.getFieldName()) != null
				&& StringUtils.isNotBlank(fieldValueMap.get(detail.getFieldName()).toString())) {
			longbox.setValue(Long.parseLong(fieldValueMap.get(detail.getFieldName()).toString()));
		} else if (StringUtils.isNotBlank(detail.getFieldDefaultValue())) {
			longbox.setValue(Long.parseLong(detail.getFieldDefaultValue().toString()));
		}
		return longbox;
	}

	/**
	 * Method for create AccountSelectionBox based on the Extended field details.
	 * 
	 * @param detail
	 * @return AccountSelectionBox
	 */
	private AccountSelectionBox getAccountSelectionBox(ExtendedFieldDetail detail) {
		AccountSelectionBox accbox = new AccountSelectionBox();
		accbox.setId(getComponentId(detail.getFieldName()));
		accbox.setFormatter(getCcyFormat());
		accbox.setTextBoxWidth(165);
		accbox.setAccountDetails("", "J7", "1010200250001,1010200500001", true);//TODO : Account Types need to define
		accbox.setMandatoryStyle(detail.isFieldMandatory());
		accbox.setButtonVisible(false);// !isReadOnly
		accbox.setReadonly(isReadOnly);

		//Data Setting
		if (fieldValueMap.containsKey(detail.getFieldName()) && fieldValueMap.get(detail.getFieldName()) != null
				&& StringUtils.isNotBlank(fieldValueMap.get(detail.getFieldName()).toString())) {
			accbox.setValue(fieldValueMap.get(detail.getFieldName()).toString());
		}
		return accbox;
	}

	/**
	 * Method for create Radiogroup based on the Extended field details.
	 * 
	 * @param detail
	 * @return Radiogroup
	 */
	private Radiogroup getRadiogroup(ExtendedFieldDetail detail) {
		Radiogroup radiogroup = new Radiogroup();
		radiogroup.setId(getComponentId(detail.getFieldName()));

		//options data rendering
		String[] radiofields = detail.getFieldList().split(",");
		for (int j = 0; j < radiofields.length; j++) {
			Radio radio = new Radio();
			radio.setLabel(radiofields[j]);
			radio.setValue(radiofields[j]);

			radio.setDisabled(isReadOnly);

			//Data Setting
			if (fieldValueMap.containsKey(detail.getFieldName()) && fieldValueMap.get(detail.getFieldName()) != null
					&& StringUtils.isNotBlank(fieldValueMap.get(detail.getFieldName()).toString()) && StringUtils
							.trimToEmpty(fieldValueMap.get(detail.getFieldName()).toString()).equals(radiofields[j])) {
				radio.setChecked(true);
			} else {
				radio.setChecked(false);
			}
			radiogroup.appendChild(radio);
		}
		return radiogroup;
	}

	/**
	 * Method for create FrequencyBox based on the Extended field details.
	 * 
	 * @param detail
	 * @return FrequencyBox
	 */
	private FrequencyBox getFrequencyBox(ExtendedFieldDetail detail) {
		FrequencyBox frqBox;
		frqBox = new FrequencyBox();
		frqBox.setId(getComponentId(detail.getFieldName()));
		frqBox.setMandatoryStyle(detail.isFieldMandatory());

		//Data Setting
		if (fieldValueMap.containsKey(detail.getFieldName()) && fieldValueMap.get(detail.getFieldName()) != null
				&& StringUtils.isNotBlank(fieldValueMap.get(detail.getFieldName()).toString())) {
			frqBox.setValue(fieldValueMap.get(detail.getFieldName()).toString());
		} else {
			frqBox.setValue("");
		}
		return frqBox;
	}

	/**
	 * Method for create Phonebox based on the Extended field details.
	 * 
	 * @param detail
	 * @return Phonebox (Hbox containing Textboxs)
	 */
	private Hbox getPhonebox(ExtendedFieldDetail detail) {
		Hbox phHbox = new Hbox();

		Textbox countryCode = new Textbox();
		countryCode.setId("ad_".concat(detail.getFieldName().concat("_CC")));
		countryCode.setMaxlength(4);
		countryCode.setReadonly(isReadOnly);
		countryCode.setWidth("48px");

		//Data Setting 
		if (fieldValueMap.containsKey(detail.getFieldName().concat("_CC"))
				&& fieldValueMap.get(detail.getFieldName().concat("_CC")) != null
				&& StringUtils.isNotBlank(fieldValueMap.get(detail.getFieldName().concat("_CC")).toString())) {
			countryCode.setValue(fieldValueMap.get(detail.getFieldName().concat("_CC")).toString());
		} else if (StringUtils.isNotBlank(detail.getFieldDefaultValue())) {
			countryCode.setValue(detail.getFieldDefaultValue());
		}
		Textbox areaCode = new Textbox();
		areaCode.setId("ad_".concat(detail.getFieldName().concat("_AC")));
		areaCode.setMaxlength(4);
		areaCode.setReadonly(isReadOnly);
		areaCode.setWidth("48px");

		if (fieldValueMap.containsKey(detail.getFieldName().concat("_AC"))
				&& fieldValueMap.get(detail.getFieldName().concat("_AC")) != null
				&& StringUtils.isNotBlank(fieldValueMap.get(detail.getFieldName().concat("_AC")).toString())) {
			areaCode.setValue(fieldValueMap.get(detail.getFieldName().concat("_AC")).toString());
		} else if (StringUtils.isNotBlank(detail.getFieldDefaultValue())) {
			areaCode.setValue(detail.getFieldDefaultValue());
		}

		Textbox subCode = new Textbox();
		subCode.setId("ad_".concat(detail.getFieldName().concat("_SC")));
		subCode.setMaxlength(8);
		subCode.setReadonly(isReadOnly);
		subCode.setWidth("96px");

		if (fieldValueMap.containsKey(detail.getFieldName().concat("_SC"))
				&& fieldValueMap.get(detail.getFieldName().concat("_SC")) != null
				&& StringUtils.isNotBlank(fieldValueMap.get(detail.getFieldName().concat("_SC")).toString())) {
			subCode.setValue(fieldValueMap.get(detail.getFieldName().concat("_SC")).toString());
		} else if (StringUtils.isNotBlank(detail.getFieldDefaultValue())) {
			subCode.setValue(detail.getFieldDefaultValue());
		}

		phHbox.appendChild(countryCode);
		phHbox.appendChild(areaCode);
		phHbox.appendChild(subCode);
		return phHbox;
	}

	/**
	 * Method for create RateBox based on the Extended field details.
	 * 
	 * @param detail
	 * @return RateBox
	 */
	private RateBox getRateBox(ExtendedFieldDetail detail) {

		RateBox rateBox = new RateBox();
		rateBox.setId(getComponentId(detail.getFieldName()));
		rateBox.setBaseProperties("BaseRateCode", "BRType", "BRTypeDesc");
		rateBox.setSpecialProperties("SplRateCode", "SRType", "SRTypeDesc");
		rateBox.setMandatoryStyle(detail.isFieldMandatory());
		rateBox.setReadonly(isReadOnly);

		//Data Setting 
		if (fieldValueMap.containsKey(detail.getFieldName().concat("_BR"))
				&& fieldValueMap.get(detail.getFieldName().concat("_BR")) != null
				&& StringUtils.isNotBlank(fieldValueMap.get(detail.getFieldName().concat("_BR")).toString())) {
			rateBox.setBaseValue(fieldValueMap.get(detail.getFieldName().concat("_BR")).toString());
		}
		if (fieldValueMap.containsKey(detail.getFieldName().concat("_SR"))
				&& fieldValueMap.get(detail.getFieldName().concat("_SR")) != null
				&& StringUtils.isNotBlank(fieldValueMap.get(detail.getFieldName().concat("_SR")).toString())) {
			rateBox.setSpecialValue(fieldValueMap.get(detail.getFieldName().concat("_SR")).toString());
		}
		if (fieldValueMap.containsKey(detail.getFieldName().concat("_MR"))
				&& fieldValueMap.get(detail.getFieldName().concat("_MR")) != null
				&& StringUtils.isNotBlank(fieldValueMap.get(detail.getFieldName().concat("_MR")).toString())) {
			rateBox.setMarginValue(new BigDecimal(fieldValueMap.get(detail.getFieldName().concat("_MR")).toString()));
		}
		return rateBox;
	}

	/**
	 * Method for create Groupbox based on the Extended field details.
	 * 
	 * @param container
	 * @return Groupbox
	 */
	private Groupbox getGroupbox(ExtendedFieldDetail container) {
		Groupbox groupbox = new Groupbox();
		groupbox.setId(container.getFieldName());
		Caption caption = new Caption(StringUtils.trimToEmpty(container.getFieldLabel()));
		caption.setParent(groupbox);
		return groupbox;
	}

	/**
	 * Method for create Tabpanel based on the Extended field details.
	 * 
	 * @param container
	 * @return Tabpanel
	 */
	private Tabpanel getTabpanel(ExtendedFieldDetail container) {
		Tabbox tabbox = (Tabbox) this.tabpanel.getFellowIfAny("Tab_ROOT_");
		if (tabbox == null) {
			tabbox = new Tabbox();
			tabbox.setId("Tab_ROOT_");
			this.tabpanel.appendChild(tabbox);
		}
		Tabs tabs = tabbox.getTabs();
		if (tabs == null) {
			tabs = new Tabs();
			tabs.setParent(tabbox);
		}
		Tab tab = new Tab(StringUtils.trimToEmpty(container.getFieldLabel()));
		tab.setId(container.getFieldName());
		tabs.appendChild(tab);

		Tabpanels tabpanels = tabbox.getTabpanels();
		if (tabpanels == null) {
			tabpanels = new Tabpanels();
			tabpanels.setParent(tabbox);
		}

		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId(TABPANEL_ID + container.getFieldName());
		tabpanel.setStyle("overflow:auto;border:none;");
		tabpanel.setHeight("100%");
		tabpanels.appendChild(tabpanel);
		tabpanels.setParent(tabbox);
		return tabpanel;
	}

	/**
	 * 
	 * This Comparator class is used to sort the ExtendedFieldDetail based on their sequenceNumber
	 */
	public class ExtendedFieldsComparator implements Comparator<ExtendedFieldDetail> {
		@Override
		public int compare(ExtendedFieldDetail arg0, ExtendedFieldDetail arg1) {

			if (arg0.getFieldSeqOrder() < arg1.getFieldSeqOrder()) {
				return 1;
			} else {
				return 0;
			}

		}

	}

	/**
	 * Method for Preparing component id
	 * 
	 * @param String
	 *            fieldName
	 * @return String id
	 */
	private String getComponentId(String fieldName) {
		return "ad_".concat(fieldName);
	}

	public Window getWindow() {
		return window;
	}

	public void setWindow(Window window) {
		this.window = window;
	}

	public Tabs getTabs() {
		return tabs;
	}

	public void setTabs(Tabs tabs) {
		this.tabs = tabs;
	}

	public Map<String, Object> getFieldValueMap() {
		return fieldValueMap;
	}

	public void setFieldValueMap(HashMap<String, Object> fieldValueMap) {
		this.fieldValueMap = fieldValueMap;
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}

	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	public String getTabHeight() {
		return tabHeight;
	}

	public void setTabHeight(String tabHeight) {
		this.tabHeight = tabHeight;
	}

	public String getLabelKey() {
		return labelKey;
	}

	public void setLabelKey(String labelKey) {
		this.labelKey = labelKey;
	}

	public int getCcyFormat() {
		return ccyFormat;
	}

	public void setCcyFormat(int ccyFormat) {
		this.ccyFormat = ccyFormat;
	}

	public Tabpanel getTabpanel() {
		return tabpanel;
	}

	public void setTabpanel(Tabpanel tabpanel) {
		this.tabpanel = tabpanel;
	}

	public int getRowWidth() {
		return rowWidth;
	}

	public void setRowWidth(int rowWidth) {
		this.rowWidth = rowWidth;
	}

}

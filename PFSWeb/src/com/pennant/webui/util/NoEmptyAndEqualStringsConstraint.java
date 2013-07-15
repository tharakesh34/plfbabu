package com.pennant.webui.util;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Textbox;

/**
 * Constraint for comparing the value from a textbox with a string.<br>
 * Throws an error message if not equals or is empty. Used in the userDialog for
 * checking that the reTyped password is same as first written password.<br>
 * 
 * <pre>
 * call from java: usrPasswordRetype.setConstraint(new
 * NoEmptyAndEqualStringsConstraint(this.usrPassword));
 * </pre>
 * 
 */
public class NoEmptyAndEqualStringsConstraint implements Constraint, java.io.Serializable {

	private static final long serialVersionUID = 4052163775381888061L;
	private final Component compareComponent;

	public NoEmptyAndEqualStringsConstraint(Component compareComponent) {
		super();
		this.compareComponent = compareComponent;
	}

	@Override
	public void validate(Component comp, Object value) throws WrongValueException {

		if (comp instanceof Textbox) {

			final String enteredValue = (String) value;

			if (compareComponent instanceof Textbox) {
				if (enteredValue.isEmpty()) {
					throw new WrongValueException(comp, Labels.getLabel("message.error.CannotBeEmpty"));
				}

				final String comparedValue = ((Textbox) compareComponent).getValue();
				if (!enteredValue.equals(comparedValue)) {
					throw new WrongValueException(comp, Labels.getLabel("message.error.RetypedPasswordMustBeSame"));
				}
			}
		}
	}

}

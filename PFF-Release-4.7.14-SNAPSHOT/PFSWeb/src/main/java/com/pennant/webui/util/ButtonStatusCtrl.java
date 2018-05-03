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
 *
 * FileName    		:  ButtonStatusCtl.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.zul.Button;

import com.pennant.UserWorkspace;

/**
 * Button controller for the buttons in the dialog windows. <br>
 * <br>
 * Works by calling the setBtnStatus_xxx where xxx is the kind of pressed <br>
 * button action, i.e. new delete or save. After calling these methods <br>
 * all buttons are disabled/enabled or visible/not visible by <br>
 * param disableButtons. <br>
 * <br>
 * disableButtons = true --> Buttons are disabled/enabled <br>
 * disableButtons = false --> Buttons are visible/not visible <br>
 * 
 * 
 */
public class ButtonStatusCtrl implements Serializable {

	private static final long serialVersionUID = -4907914938602465474L;

	private static enum ButtonEnum {
		New, Edit, Delete, Save, Cancel, Close, Notes;
	}

	private final Map<ButtonEnum, Button> buttons = new HashMap<ButtonEnum, Button>(6);

	final private UserWorkspace workspace;

	/** rightName prefix */
	private final String _rightPrefix;

	/**
	 * Var for disable/enable or visible/not visible mode of the butttons. <br>
	 * true = disable the button <br>
	 * false = make the button unvisible<br>
	 */
	private final boolean disableButtons = false;

	/** with close button */
	boolean closeButton = true;

	/**
	 * Constructor
	 * 
	 * @param btnNew
	 *            (New Button)
	 * @param btnEdit
	 *            (Edit Button)
	 * @param btnDelete
	 *            (Delete Button)
	 * @param btnSave
	 *            (Save Button)
	 * @param btnClose
	 *            (Close Button)
	 */
	public ButtonStatusCtrl(UserWorkspace userWorkspace, String rightPrefix, boolean withCloseBtn, Button btnNew,
			Button btnEdit, Button btnDelete, Button btnSave, Button btnCancel, Button btnClose, Button btnNotes) {
		super();
		this.workspace = userWorkspace;

		this._rightPrefix = rightPrefix + "btn";
		this.closeButton = withCloseBtn;

		buttons.put(ButtonEnum.New, btnNew);
		buttons.put(ButtonEnum.Edit, btnEdit);
		buttons.put(ButtonEnum.Delete, btnDelete);
		buttons.put(ButtonEnum.Save, btnSave);
		buttons.put(ButtonEnum.Cancel, btnCancel);
		buttons.put(ButtonEnum.Close, btnClose);
		buttons.put(ButtonEnum.Notes, btnNotes);

		setBtnImages();
	}

	/**
	 * Set the images fore the buttons.<br>
	 */
	private void setBtnImages() {
		setImage(buttons.get(ButtonEnum.New), "New");
		setImage(buttons.get(ButtonEnum.Edit), "Edit");
		setImage(buttons.get(ButtonEnum.Save), "Save");
		setImage(buttons.get(ButtonEnum.Delete), "Delete");
		setImage(buttons.get(ButtonEnum.Cancel), "Cancel");
		setImage(buttons.get(ButtonEnum.Close), "Close");
		setImage(buttons.get(ButtonEnum.Notes), "Notes");
	}

	private void setImage(Button button, String label) {
		if (button == null) {
			return;
		}

		//button.setSclass(Labels.getLabel("CSS_BUTTON_CLASS_NAME"));
		button.setLabel(label);
	}

	/**
	 * Set all Buttons for the Mode NEW is pressed. <br>
	 */
	public void setBtnStatus_New() {
		if (disableButtons) {
			setDisabled(ButtonEnum.New, true);
			setDisabled(ButtonEnum.Edit, true);
			setDisabled(ButtonEnum.Delete, true);
			setDisabled(ButtonEnum.Save, false);
			setDisabled(ButtonEnum.Cancel, false);
			if (closeButton) {
				setDisabled(ButtonEnum.Close, false);
			} else {
				setVisible(ButtonEnum.Close, false);
			}

		} else {
			setVisible(ButtonEnum.New, false);
			setVisible(ButtonEnum.Edit, false);
			setVisible(ButtonEnum.Delete, false);
			setVisible(ButtonEnum.Save, true);
			setVisible(ButtonEnum.Cancel, true);
			if (closeButton) {
				setVisible(ButtonEnum.Close, true);
			} else {
				setVisible(ButtonEnum.Close, false);
			}
		}
	}

	/**
	 * Set all Buttons for the Mode EDIT is pressed. <br>
	 */
	public void setBtnStatus_Edit() {
		if (disableButtons) {
			setDisabled(ButtonEnum.New, true);
			setDisabled(ButtonEnum.Edit, true);
			setDisabled(ButtonEnum.Delete, true);
			setDisabled(ButtonEnum.Save, false);
			setDisabled(ButtonEnum.Cancel, false);
			if (closeButton) {
				setDisabled(ButtonEnum.Close, false);
			} else {
				setVisible(ButtonEnum.Close, false);
			}
		} else {
			setVisible(ButtonEnum.New, false);
			setVisible(ButtonEnum.Edit, false);
			setVisible(ButtonEnum.Delete, false);
			setVisible(ButtonEnum.Save, true);
			setVisible(ButtonEnum.Cancel, true);
			if (closeButton) {
				setVisible(ButtonEnum.Close, true);
			} else {
				setVisible(ButtonEnum.Close, false);
			}
		}
	}

	public void setWFBtnStatus_Edit(boolean firstTaks) {
		if (disableButtons) {
			setDisabled(ButtonEnum.Edit, true);
			setDisabled(ButtonEnum.Cancel, true);
			setVisible(ButtonEnum.New, false);
			if (firstTaks) {
				// setDisabled(ButtonEnum.New, false);
				setDisabled(ButtonEnum.Delete, false);
				setDisabled(ButtonEnum.Save, false);
			} else {
				// setDisabled(ButtonEnum.New, true);
				setDisabled(ButtonEnum.Delete, true);
				setDisabled(ButtonEnum.Save, false);

			}
			if (closeButton) {
				setDisabled(ButtonEnum.Close, false);
			} else {
				setVisible(ButtonEnum.Close, false);
			}

		} else {
			setVisible(ButtonEnum.Edit, false);
			setVisible(ButtonEnum.Cancel, false);
			setVisible(ButtonEnum.New, false);

			if (firstTaks) {
				// setVisible(ButtonEnum.New, true);
				setVisible(ButtonEnum.Delete, true);
				setVisible(ButtonEnum.Save, true);
			} else {
				// setVisible(ButtonEnum.New, false);
				setVisible(ButtonEnum.Delete, false);
				setVisible(ButtonEnum.Save, true);
			}

			if (closeButton) {
				setVisible(ButtonEnum.Close, true);
			} else {
				setVisible(ButtonEnum.Close, false);
			}
		}
	}

	/**
	 * Not needed yet, because after pressed the delete button <br>
	 * the window is closing. <br>
	 */
	public void setBtnStatus_Delete() {
	}

	/**
	 * Set all Buttons for the Mode SAVE is pressed. <br>
	 */
	public void setBtnStatus_Save() {
		setInitEdit();
	}

	/**
	 * Set all Buttons for the Mode init in EDIT mode. <br>
	 * This means that the Dialog window is opened and <br>
	 * shows data. <br>
	 */
	public void setInitEdit() {
		if (disableButtons) {
			setDisabled(ButtonEnum.New, false);
			setDisabled(ButtonEnum.Edit, false);
			setDisabled(ButtonEnum.Delete, false);
			setDisabled(ButtonEnum.Save, true);
			setDisabled(ButtonEnum.Cancel, false);
			if (closeButton) {
				setDisabled(ButtonEnum.Close, false);
			} else {
				setVisible(ButtonEnum.Close, false);
			}
		} else {
			setVisible(ButtonEnum.New, false);
			setVisible(ButtonEnum.Edit, true);
			setVisible(ButtonEnum.Delete, true);
			setVisible(ButtonEnum.Save, false);
			setVisible(ButtonEnum.Cancel, true);
			if (closeButton) {
				setVisible(ButtonEnum.Close, true);
			} else {
				setVisible(ButtonEnum.Close, false);
			}
		}
	}

	/**
	 * Set all Buttons for the Mode init in NEW mode. <br>
	 * This means that the Dialog window is freshly new <br>
	 * and have no data. <br>
	 */
	public void setInitNew() {
		if (disableButtons) {
			setDisabled(ButtonEnum.New, true);
			setDisabled(ButtonEnum.Edit, true);
			setDisabled(ButtonEnum.Delete, true);
			setDisabled(ButtonEnum.Save, false);
			setDisabled(ButtonEnum.Cancel, true);
			if (closeButton) {
				setDisabled(ButtonEnum.Close, false);
			} else {
				setVisible(ButtonEnum.Close, false);
			}
		} else {
			setVisible(ButtonEnum.New, false);
			setVisible(ButtonEnum.Edit, false);
			setVisible(ButtonEnum.Delete, false);
			setVisible(ButtonEnum.Save, true);
			setVisible(ButtonEnum.Cancel, false);
			if (closeButton) {
				setVisible(ButtonEnum.Close, true);
			} else {
				setVisible(ButtonEnum.Close, false);
			}
		}
	}

	/**
	 * Sets the image of a button.<br>
	 * 
	 * @param b
	 * @param imagePath
	 *            path and image name
	 */
	@SuppressWarnings("unused")
	private void setImage(ButtonEnum b, String imagePath) {
		buttons.get(b).setImage(imagePath);
	}

	/**
	 * Set the button visible.<br>
	 * 
	 * @param b
	 * @param visible
	 *            True or False
	 */
	private void setVisible(ButtonEnum b, boolean visible) {
		if (buttons.get(b) == null) {
			return;
		}

		if (visible) {
			if (workspace.isAllowed(_rightPrefix + b.name())) {
				buttons.get(b).setVisible(visible);
			}
		} else {
			buttons.get(b).setVisible(visible);
		}
	}

	/**
	 * Sets the button disabled.<br>
	 * 
	 * @param b
	 * @param disabled
	 *            True or False
	 */
	private void setDisabled(ButtonEnum b, boolean disabled) {
		if (buttons.get(b) == null) {
			return;
		}

		if (disabled) {
			buttons.get(b).setDisabled(disabled);
		} else {
			if (workspace.isAllowed(_rightPrefix + b.name())) {
				buttons.get(b).setDisabled(disabled);
			}
		}
	}

	/**
	 * Set all Buttons for the Mode EDIT is pressed. <br>
	 */
	public void setBtnStatus_Enquiry() {
		if (disableButtons) {
			setDisabled(ButtonEnum.New, true);
			setDisabled(ButtonEnum.Edit, true);
			setDisabled(ButtonEnum.Delete, true);
			setDisabled(ButtonEnum.Save, true);
			setDisabled(ButtonEnum.Cancel, true);
			setDisabled(ButtonEnum.Close, false);
		} else {
			setVisible(ButtonEnum.New, false);
			setVisible(ButtonEnum.Edit, false);
			setVisible(ButtonEnum.Delete, false);
			setVisible(ButtonEnum.Save, false);
			setVisible(ButtonEnum.Cancel, false);
			setVisible(ButtonEnum.Close, true);
		}
	}

	public void setCloseFocus() {
		if (buttons.get(ButtonEnum.Close) != null) {
			buttons.get(ButtonEnum.Close).focus();

		}
	}
}

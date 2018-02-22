package com.pennanttech.pennapps.core.lic;

import java.io.IOException;
import java.io.Serializable;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.A;
import org.zkoss.zul.Html;
import org.zkoss.zul.Window;

import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.lic.License;
import com.pennanttech.pennapps.lic.exception.LicenseException;

@org.springframework.stereotype.Component("copyRightCtrl")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CopyRightCtrl extends GenericForwardComposer<Component> implements Serializable  {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(CopyRightCtrl.class);

	protected Window window_Copyright;
	protected Html copyright;
	protected A close;

	/**
	 * default constructor.<br>
	 */
	public CopyRightCtrl() {
		super();
	}


	public void onCreate$window_Copyright(Event event) throws Exception {
		setCopyRight();
		this.window_Copyright.doModal();
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onOK$close(Event event) {
		window_Copyright.onClose();
	}
	
	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onCancel$close(Event event) {
		window_Copyright.onClose();
	}

	private void setCopyRight() {
		try {
			copyright.setContent(License.getCopyRight(App.CODE));
		} catch (IOException | LicenseException e) {
			log.error(Literal.EXCEPTION, e);
		}
	}

}
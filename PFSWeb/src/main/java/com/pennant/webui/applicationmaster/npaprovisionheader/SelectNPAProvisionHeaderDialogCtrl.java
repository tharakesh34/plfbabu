package com.pennant.webui.applicationmaster.npaprovisionheader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.applicationmaster.NPAProvisionDetail;
import com.pennant.backend.model.applicationmaster.NPAProvisionHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.applicationmaster.NPAProvisionHeaderService;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;

public class SelectNPAProvisionHeaderDialogCtrl extends GFCBaseCtrl<NPAProvisionHeader> {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(SelectNPAProvisionHeaderDialogCtrl.class);
	protected Window window_SelectNPAProvisionHeaderDialog;
	protected ExtendedCombobox finType;
	protected ExtendedCombobox entity;
	protected Button btnProceed;
	private NPAProvisionHeader nPAProvisionHeader;
	private NPAProvisionHeaderListCtrl nPAProvisionHeaderListCtrl;

	private transient NPAProvisionHeaderService nPAProvisionHeaderService;
	private boolean isCopyProcess;

	public SelectNPAProvisionHeaderDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	public void onCreate$window_SelectNPAProvisionHeaderDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		try {
			// Get the required arguments.
			this.nPAProvisionHeader = (NPAProvisionHeader) arguments.get("nPAProvisionHeader");
			this.nPAProvisionHeaderListCtrl = (NPAProvisionHeaderListCtrl) arguments.get("nPAProvisionHeaderListCtrl");
			if (this.nPAProvisionHeader == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}
			if (arguments.containsKey("isCopyProcess")) {
				this.isCopyProcess = (boolean) arguments.get("isCopyProcess");
			}

			doSetFieldProperties();
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		setPageComponents(window_SelectNPAProvisionHeaderDialog);
		showSelectNPAProvisionHeaderDialog();
		logger.debug(Literal.LEAVING);
	}

	private void showSelectNPAProvisionHeaderDialog() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		try {
			// open the dialog in modal mode
			this.window_SelectNPAProvisionHeaderDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.entity.setMandatoryStyle(true);
		this.entity.setModuleName("Entity");
		this.entity.setValueColumn("entityCode");
		this.entity.setDescColumn("entityDesc");
		this.entity.setValidateColumns(new String[] { "entityCode" });

		this.finType.setMandatoryStyle(true);
		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("finType");
		this.finType.setDescColumn("finTypeName");
		this.finType.setValidateColumns(new String[] { "finType" });

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$finType(Event event) {
		logger.debug(Literal.ENTERING + " " + event.toString());

		this.finType.setConstraint("");
		this.finType.clearErrorMessage();
		Clients.clearWrongValue(finType);
		Object dataObject = this.finType.getObject();

		if (dataObject instanceof String) {
			this.finType.setValue(dataObject.toString());
			this.finType.setDescription("");
		} else {
			FinanceType details = (FinanceType) dataObject;
			if (details != null) {
				this.finType.setValue(details.getFinType());
				this.finType.setDescription(details.getFinTypeDesc());
			}
		}
		logger.debug(Literal.LEAVING + " " + event.toString());
	}

	public void onFulfill$entity(Event event) {
		logger.debug(Literal.ENTERING + " " + event.toString());

		this.entity.setConstraint("");
		this.entity.clearErrorMessage();
		Clients.clearWrongValue(entity);
		Object dataObject = this.entity.getObject();

		if (dataObject instanceof String) {
			this.entity.setValue(dataObject.toString());
			this.entity.setDescription("");
		} else {
			Entity details = (Entity) dataObject;
			if (details != null) {
				this.entity.setValue(details.getEntityCode());
				this.entity.setDescription(details.getEntityDesc());
			}
		}
		logger.debug(Literal.LEAVING + " " + event.toString());
	}

	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnProceed(Event event) throws Exception {
		logger.debug(Literal.ENTERING + " " + event.toString());

		doSetFieldValidation();
		boolean isExists = false;
		String finTypeLabel = null;
		this.nPAProvisionHeader.setFinType(this.finType.getValue());
		this.nPAProvisionHeader.setEntity(this.entity.getValue());
		finTypeLabel = nPAProvisionHeader.getFinType() + " ";
		isExists = this.nPAProvisionHeaderService.getIsFinTypeExists(nPAProvisionHeader.getFinType(), TableType.VIEW);

		if (isExists) {
			MessageUtil.showError(finTypeLabel.concat(Labels.getLabel("label_FinType_AlreadyExists")));
			return;
		}

		if (isCopyProcess) {

			List<NPAProvisionDetail> oldDetailsList = nPAProvisionHeader.getProvisionDetailsList();

			List<NPAProvisionDetail> resvDetailsList = new ArrayList<>();
			List<NPAProvisionDetail> newDetailsList = new ArrayList<>();

			NPAProvisionHeader newNPAProvisionHeader = new NPAProvisionHeader();
			newNPAProvisionHeader.setFinType(this.finType.getValue());
			newNPAProvisionHeader = this.nPAProvisionHeaderService.getNewNPAProvisionHeader(newNPAProvisionHeader,
					TableType.VIEW);

			if (newNPAProvisionHeader != null) {
				newDetailsList = newNPAProvisionHeader.getProvisionDetailsList();
			}

			if (CollectionUtils.isEmpty(newDetailsList)) {
				MessageUtil.showError(Labels.getLabel("label_FinType_AssetCodes_Configurations_Notavailable"));
				return;
			}

			if (CollectionUtils.isNotEmpty(oldDetailsList) && CollectionUtils.isNotEmpty(newDetailsList)) {

				resvDetailsList = newDetailsList;

				for (NPAProvisionDetail oldDetails : oldDetailsList) {
					for (NPAProvisionDetail newDetails : newDetailsList) {
						if (StringUtils.equals(oldDetails.getAssetCode(), newDetails.getAssetCode())
								&& oldDetails.getAssetStageOrder() == newDetails.getAssetStageOrder()) {
							resvDetailsList.add(oldDetails);
							resvDetailsList.remove(newDetails);
							break;
						}
					}
				}

				nPAProvisionHeader.setProvisionDetailsList(resvDetailsList);
				this.nPAProvisionHeader.setEntityName(this.entity.getDescription());
				this.nPAProvisionHeader.setFinTypeName(this.finType.getDescription());
				showDetailView();
			}
		} else {
			this.nPAProvisionHeader = this.nPAProvisionHeaderService.getNewNPAProvisionHeader(nPAProvisionHeader,
					TableType.VIEW);

			if (CollectionUtils.isEmpty(this.nPAProvisionHeader.getProvisionDetailsList())) {
				MessageUtil.showError(Labels.getLabel("label_FinType_AssetCodes_Configurations_Notavailable"));
				return;
			}

			this.nPAProvisionHeader.setEntityName(this.entity.getDescription());
			this.nPAProvisionHeader.setFinTypeName(this.finType.getDescription());
			showDetailView();
		}
		logger.debug(Literal.LEAVING + " " + event.toString());
	}

	private void showDetailView() {
		logger.debug(Literal.ENTERING);

		HashMap<String, Object> arguments = new HashMap<String, Object>();
		arguments.put("nPAProvisionHeader", this.nPAProvisionHeader);
		arguments.put("nPAProvisionHeaderListCtrl", this.nPAProvisionHeaderListCtrl);
		arguments.put("isCopyProcess", isCopyProcess);
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/NPAProvisionHeader/NPAProvisionHeaderDialog.zul", null,
					arguments);
			this.window_SelectNPAProvisionHeaderDialog.onClose();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetFieldValidation() {
		logger.debug(Literal.ENTERING);
		doClearMessage();
		doRemoveValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (StringUtils.isBlank(this.finType.getValue())) {
				throw new WrongValueException(this.finType, Labels.getLabel("CHECK_NO_EMPTY",
						new String[] { Labels.getLabel("label_NPAProvisionHeaderDialog_FinType.value") }));
			}
		} catch (WrongValueException e) {
			wve.add(e);
		}
		try {
			if (StringUtils.isBlank(this.entity.getValue())) {
				throw new WrongValueException(this.entity, Labels.getLabel("CHECK_NO_EMPTY",
						new String[] { Labels.getLabel("label_NPAProvisionHeaderDialog_Entity.value") }));
			}
		} catch (WrongValueException e) {
			wve.add(e);
		}

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.entity.setConstraint("");
		this.finType.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);
		this.entity.setErrorMessage("");
		this.finType.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	public NPAProvisionHeaderService getnPAProvisionHeaderService() {
		return nPAProvisionHeaderService;
	}

	public void setnPAProvisionHeaderService(NPAProvisionHeaderService nPAProvisionHeaderService) {
		this.nPAProvisionHeaderService = nPAProvisionHeaderService;
	}
}

package com.pennant.webui.unauthorizedtransactions.reports;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.PathUtil;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.bmtmasters.Product;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.systemmasters.DivisionDetail;
import com.pennant.backend.model.transactions.UnAuthorizedTransaction;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.transactions.UnAuthorizedTransactionService;

public class UnAuthorizedTransactionCtrl extends GFCBaseCtrl<UnAuthorizedTransaction> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(UnAuthorizedTransactionCtrl.class);

	protected Window windowUnAuthTransac;

	protected ExtendedCombobox entity;
	protected ExtendedCombobox division;
	protected ExtendedCombobox branch;
	protected ExtendedCombobox product;
	protected ExtendedCombobox loanType;
	protected Radio pdfFormat;
	protected Radio excelFormat;
	protected Button btnRefresh;
	protected Button btnSearch;

	private transient UnAuthorizedTransactionService unAuthorizedTransactionService;

	public UnAuthorizedTransactionCtrl() {
		super();
	}

	public void onCreate$windowUnAuthTransac(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		setPageComponents(this.windowUnAuthTransac);

		doSetFieldProperties();

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.entity.setMandatoryStyle(true);
		this.entity.setModuleName("Entity");
		this.entity.setValueColumn("entityCode");
		this.entity.setDescColumn("entityDesc");

		this.division.setModuleName("DivisionDetail");
		this.division.setValueColumn("divisionCode");
		this.division.setDescColumn("divisionDesc");

		this.branch.setModuleName("Branch");
		this.branch.setValueColumn("branchCode");
		this.branch.setDescColumn("branchDesc");

		this.product.setModuleName("Product");
		this.product.setValueColumn("productCode");
		this.product.setDescColumn("productDesc");

		this.loanType.setModuleName("FinanceType");
		this.loanType.setValueColumn("finType");
		this.loanType.setDescColumn("finTypeDesc");

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnRefresh(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		clearData();

		Clients.clearWrongValue(this.entity);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void clearData() {
		doRemoveValidation();
		doClearMessage();
		doClear();
	}

	@Override
	protected void doClearMessage() {
		this.entity.setErrorMessage("");
		this.division.setErrorMessage("");
		this.branch.setErrorMessage("");
		this.product.setErrorMessage("");
		this.loanType.setErrorMessage("");
	}

	private void doClear() {
		this.entity.setValue("");
		this.division.setValue("");
		this.branch.setValue("");
		this.product.setValue("");
		this.loanType.setValue("");
	}

	private void doRemoveValidation() {
		this.entity.setConstraint("");
		this.division.setConstraint("");
		this.branch.setConstraint("");
		this.product.setConstraint("");
		this.loanType.setConstraint("");
	}

	public void onFulfill$entity(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

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

		logger.debug(Literal.ENTERING.concat(event.toString()));
	}

	public void onFulfill$division(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		this.division.setConstraint("");
		this.division.clearErrorMessage();
		Clients.clearWrongValue(division);
		Object dataObject = this.division.getObject();

		if (dataObject instanceof String) {
			this.division.setValue(dataObject.toString());
			this.division.setDescription("");
		} else {
			DivisionDetail divisionDetail = (DivisionDetail) dataObject;
			if (divisionDetail != null) {
				this.division.setValue(divisionDetail.getDivisionCodeDesc());
				this.division.setDescription(divisionDetail.getDivisionCodeDesc());
			}
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onFulfill$branch(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		this.branch.setConstraint("");
		this.branch.clearErrorMessage();
		Clients.clearWrongValue(branch);
		Object dataObject = this.branch.getObject();

		if (dataObject instanceof String) {
			this.branch.setValue(dataObject.toString());
			this.branch.setDescription("");
		} else {
			Branch details = (Branch) dataObject;
			if (details != null) {
				this.branch.setValue(details.getBranchCode());
				this.branch.setDescription(details.getBranchDesc());
			}
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onFulfill$product(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		this.product.setConstraint("");
		this.product.clearErrorMessage();
		Clients.clearWrongValue(product);
		Object dataObject = this.product.getObject();

		if (dataObject instanceof String) {
			this.product.setValue(dataObject.toString());
			this.product.setDescription("");
		} else {
			Product details = (Product) dataObject;
			if (details != null) {
				this.product.setValue(details.getProductCode());
				this.product.setDescription(details.getProductDesc());
			}
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onFulfill$loanType(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		this.loanType.setConstraint("");
		this.loanType.clearErrorMessage();
		Clients.clearWrongValue(loanType);
		Object dataObject = this.loanType.getObject();

		if (dataObject instanceof String) {
			this.loanType.setValue(dataObject.toString());
			this.loanType.setDescription("");
		} else {
			FinanceType details = (FinanceType) dataObject;
			if (details != null) {
				this.loanType.setValue(details.getFinType());
				this.loanType.setDescription(details.getFinTypeDesc());
			}
		}

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	public void onClick$btnSearch(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		doWriteComponentsToBean();

		unAuthorizedTransactionService.process();

		int formater = CurrencyUtil.getFormat("");

		List<UnAuthorizedTransaction> list = unAuthorizedTransactionService.getTransactions(prepareWhereClause(),
				getPreparedStatement());

		list.forEach(uat -> uat.setFormat(formater));

		downloadUAT(list);

		logger.debug(Literal.LEAVING);
	}

	public void doWriteComponentsToBean() {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();

		doRemoveValidation();

		try {
			if (!this.entity.isReadonly() && (this.entity.getValue() == null || this.entity.getValue().equals(""))) {
				throw new WrongValueException(this.entity, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_UnAuthTransac_Entity") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	private void downloadUAT(List<UnAuthorizedTransaction> uat) {
		byte[] data = unAuthorizedTransactionsDownload(uat);

		if (data != null && this.excelFormat.isChecked()) {
			Filedownload.save(data, "application/vnd.ms-excel", "UnAuthorizedTransactions");
		} else {
			MessageUtil.showError(" Select the report format ");
		}
	}

	private byte[] unAuthorizedTransactionsDownload(List<UnAuthorizedTransaction> uat) {
		String path = App.getResourcePath(PathUtil.TEMPLATES, PathUtil.UNAUTHORIZED_TRANSACTION).concat(File.separator)
				.concat("UnAuthorizedTransactions.xlsx");

		try (FileInputStream file = new FileInputStream(new File(path))) {
			Workbook workbook = new XSSFWorkbook(file);
			Sheet sheet = workbook.getSheetAt(0);

			if (sheet == null) {
				workbook.close();
				MessageUtil.showError(Labels.getLabel("label_SheetEmpty"));
				return null;
			}

			createSheet(sheet, uat);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			workbook.write(baos);
			workbook.close();
			return baos.toByteArray();
		} catch (FileNotFoundException e) {
			MessageUtil.showError(Labels.getLabel("label_file_not_exists").concat(path));
			return null;
		} catch (IOException e1) {
			logger.error(Literal.EXCEPTION, e1);
		}

		return null;
	}

	private void createSheet(Sheet sheet, List<UnAuthorizedTransaction> uat) {
		int rowCount = 0;
		List<String> values = null;
		if (CollectionUtils.isNotEmpty(uat)) {
			for (UnAuthorizedTransaction uatr : uat) {
				values = createHeaders(uatr);
				Row row = sheet.createRow(++rowCount);
				writeData(row, values);
			}
		}

	}

	private static void writeData(Row row, List<String> cellValues) {
		int columnCount = -1;
		for (String value : cellValues) {
			createCell(row, ++columnCount, value);
		}
	}

	private static void createCell(Row row, int columnCount, String field) {
		Cell cell = row.createCell(columnCount);
		cell.setCellType(CellType.STRING);
		cell.setCellValue(field);
	}

	private List<String> createHeaders(UnAuthorizedTransaction uat) {
		List<String> list = new ArrayList<>();

		list.add(uat.getCustCIF());
		list.add(uat.getCustShrtName());
		list.add(uat.getFinType());
		list.add(uat.getFinReference());
		list.add(uat.getEvent());

		Date lmo = uat.getLastMntOn();
		if (lmo != null && DateUtil.format(lmo, "dd-MM-yyyy") != null) {
			list.add(DateUtil.format(lmo, "dd-MM-yyyy"));
		} else {
			list.add(" ");
		}

		long lmb = uat.getLastMntBy();
		list.add(String.valueOf(lmb));
		list.add(uat.getMakerName());
		list.add(uat.getBranchCode());
		list.add(uat.getBranchName());

		BigDecimal transactionAmount = uat.getTransactionAmount();
		list.add(PennantApplicationUtil.amountFormate(transactionAmount, uat.getFormat()).replace(",", ""));

		int noOfDays = uat.getNoOfDays();
		list.add(String.valueOf(noOfDays));
		list.add(uat.getStage());
		list.add(uat.getCurrentRole());
		list.add(uat.getPreviousRole());

		return list;

	}

	private String prepareWhereClause() {
		StringBuilder sql = new StringBuilder();

		if (StringUtils.isNotEmpty(this.entity.getValue())) {
			sql.append("Entity = ?");
		}

		if (StringUtils.isNotEmpty(this.division.getValue())) {
			sql.append(" and Division = ?");
		}

		if (StringUtils.isNotEmpty(this.branch.getValue())) {
			sql.append(" and Branch = ?");
		}

		if (StringUtils.isNotEmpty(this.product.getValue())) {
			sql.append(" and Product = ?");
		}

		if (StringUtils.isNotEmpty(this.loanType.getValue())) {
			sql.append(" and FinType = ?");
		}

		return sql.toString();
	}

	private List<String> getPreparedStatement() {
		List<String> list = new ArrayList<>();

		String strEntity = this.entity.getValue();
		if (StringUtils.isNotEmpty(strEntity)) {
			list.add(strEntity);
		}

		String strDivision = this.division.getValue();
		if (StringUtils.isNotEmpty(strDivision)) {
			list.add(strDivision);
		}
		String strBranch = this.branch.getValue();
		if (StringUtils.isNotEmpty(strBranch)) {
			list.add(strBranch);
		}

		String strProduct = this.product.getValue();
		if (StringUtils.isNotEmpty(strProduct)) {
			list.add(strProduct);
		}

		String strLoanType = this.loanType.getValue();
		if (StringUtils.isNotEmpty(strLoanType)) {
			list.add(strLoanType);
		}

		return list;
	}

	@Autowired
	public void setUnAuthorizedTransactionService(UnAuthorizedTransactionService unAuthorizedTransactionService) {
		this.unAuthorizedTransactionService = unAuthorizedTransactionService;
	}
}

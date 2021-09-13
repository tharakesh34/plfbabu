package com.pennanttech.pff.dao.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.ExternalDocument;
import com.pennanttech.pff.core.TableType;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestCustomerDocumentDAO {

	@Autowired
	private CustomerDocumentDAO customerDocumentDAO;

	@Test
	@Transactional
	@Rollback(true)
	public void TestGetCustomerDocumentById() {
		customerDocumentDAO.getCustomerDocumentById(4, "01", "");
		customerDocumentDAO.getCustomerDocumentById(4, "01", "_View");
		customerDocumentDAO.getCustomerDocumentById(100, "01", "");

		customerDocumentDAO.getCustomerDocumentByCustomer(4, "");

		customerDocumentDAO.getCustomerDocumentByCustomerId(1387);

		customerDocumentDAO.deleteByCustomer(4, "");

		customerDocumentDAO.getExternalDocuments(733, "");

		customerDocumentDAO.getCustDocByCustAndDocType(1387, "AUDRP", "");
		customerDocumentDAO.getCustDocByCustAndDocType(1387, "AUDRP", "_View");
		customerDocumentDAO.getCustDocByCustAndDocType(1, "AUDRP", "_View");

		customerDocumentDAO.getCustDocByCustId(1387, "");
		// customerDocumentDAO.getCustDocByCustId(4, "_View"); //LovDescCustCIF does not exist in CustomerDocuments_View

		customerDocumentDAO.isDuplicateTitle(4, "01", "945456564545");

		customerDocumentDAO.getDocTypeCount("CUSTDOC");

		customerDocumentDAO.getVersion(1387, "AUDRP");
		customerDocumentDAO.getVersion(1, "AUDRP");

		customerDocumentDAO.getCustCountryCount("UK");
		customerDocumentDAO.getCustCountryCount("");

		customerDocumentDAO.getDuplicateDocByTitle("01", "945456564545");

		customerDocumentDAO.updateDocURI("2", 2136, TableType.MAIN_TAB);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestDelete() {
		CustomerDocument cd = new CustomerDocument();
		cd = customerDocumentDAO.getCustomerDocumentById(4, "01", "");
		customerDocumentDAO.delete(cd, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestDelete1() {
		// recordCount <= 0
		CustomerDocument cd = new CustomerDocument();
		cd = customerDocumentDAO.getCustomerDocumentById(4, "01", "");
		cd.setCustID(100);
		customerDocumentDAO.delete(cd, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestSave() {
		CustomerDocument cd = new CustomerDocument();
		cd = customerDocumentDAO.getCustomerDocumentById(4, "01", "");
		cd.setCustID(1463);
		customerDocumentDAO.save(cd, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestSaveED() {
		ExternalDocument ed = new ExternalDocument();
		ed.setId(1749);
		ed.setFinID(5354);
		ed.setFinReference("1500BUS0003280");
		customerDocumentDAO.save(ed, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestUpdate() {
		CustomerDocument cd = new CustomerDocument();
		cd = customerDocumentDAO.getCustomerDocumentById(4, "01", "");
		customerDocumentDAO.update(cd, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestUpdate1() {
		// recordCount <= 0
		CustomerDocument cd = new CustomerDocument();
		cd = customerDocumentDAO.getCustomerDocumentById(4, "01", "");
		cd.setCustID(100);
		customerDocumentDAO.update(cd, "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void TestGetCustDocListByDocTypes() {
		List<String> docType = new ArrayList<>();
		docType.add("01");
		customerDocumentDAO.getCustDocListByDocTypes(4, docType, "");
	}

}

package com.pennanttech.pff.dao.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.constants.AccountingEvent;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestPostingsDAO {

	@Autowired
	private PostingsDAO postingsDAO;

	Date dt1 = DateUtil.parse("29/03/2035", DateFormat.SHORT_DATE);

	@Test
	@Transactional
	@Rollback(true)
	public void testSave() {
		postingsDAO.getPostingsByFinRefAndEvent("0", "'PIS_NORM','NORM_PIS'", true, "", "_View");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSave2() {
		postingsDAO.getPostingsByFinRefAndEvent("0", "'PIS_NORM','NORM_PIS'", true, "", "");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSave3() {
		postingsDAO.getPostingsByFinRefAndEvent("0", "'PIS_NORM','NORM_PIS'", false, "FinEvent", "_Temp");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSave4() {
		postingsDAO.getPostingsByFinRefAndEvent("0", "'PIS_NORM','NORM_PIS'", false, "PostDate", "_Temp");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSave5() {
		postingsDAO.getPostingsByFinRefAndEvent("0", "'PIS_NORM','NORM_PIS'", false, "Account", "_Temp");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testSavedat() {
		postingsDAO.getPostingsByLinkTransId(15643);
		postingsDAO.getPostingsByLinkTransId(1564380);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetTran() {
		ReturnDataSet rd = new ReturnDataSet();
		List<Long> df = new ArrayList<>();
		df.add(rd.getLinkedTranId());
		postingsDAO.getPostingsByTransIdList(df);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetPost() {
		postingsDAO.getPostingsByPostRef("kjhjghuytft");
		postingsDAO.getPostingsByPostRef("1500-PROIN-INR");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testsaveBatch() {
		ReturnDataSet ds = new ReturnDataSet();
		ds.setLinkedTranId(225886458);
		ds.setFinReference("1500BUS0003280");
		ds.setTransOrder(910);
		ds.setAppDate(dt1);
		ds.setAppValueDate(dt1);
		ds.setFinID((long) 5354);
		List<ReturnDataSet> rs = new ArrayList<ReturnDataSet>();
		rs.add(ds);
		postingsDAO.saveBatch(rs);// null pointer exception
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetStatusByLinkedTranId() {
		postingsDAO.updateStatusByLinkedTranId(15643, "s");
		postingsDAO.updateStatusByLinkedTranId(1564365478, "n");

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetStatus() {
		postingsDAO.updateStatusByFinRef("0", "s");
		postingsDAO.updateStatusByFinRef("3", "n");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetStatuses() {

		String[] finEvent = { AccountingEvent.VAS_FEE, AccountingEvent.INSPAY };
		postingsDAO.getPostingsByVasref("0000AGR0008869", finEvent);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetPostingsByFinRef() {
		postingsDAO.getPostingsByFinRef("1500CL00002338", true);// null pointer exception
	}

	@Test
	@Transactional
	@Rollback(true)
	public void tests() {
		postingsDAO.getPostingId();
		postingsDAO.getLinkedTransId();

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testCls() {
		postingsDAO.updatePostCtg();
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetPosts() {
		postingsDAO.getDisbursementPostings(0);
		postingsDAO.getDisbursementPostings(48);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateStatus() {
		postingsDAO.updateStatusByPostRef("s", "1500-INTIN-INR");
		postingsDAO.updateStatusByPostRef(null, null);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetPostings() {

		postingsDAO.getPostings("1500-INTIN-INR", "INSTDATE");
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateStatu() {
		postingsDAO.getPostingsByPostRef("1500-INTIN-INR");
		postingsDAO.getPostingsByPostRef(0);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testGetPostingsByFinnance() {
		postingsDAO.getPostingsbyFinanceBranch("1500");
		postingsDAO.getPostingsbyFinanceBranch(null);
	}

}

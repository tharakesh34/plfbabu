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

import com.pennant.backend.dao.finance.FinStatusDetailDAO;
import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

@ContextConfiguration(locations = "classpath:dao-test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestFinStatusDetailDAO {

	@Autowired
	private FinStatusDetailDAO finStatusDetailDAO;
	private FinStatusDetail sd;
	Date dt = DateUtil.parse("13/09/2021", DateFormat.SHORT_DATE);

	@Test
	@Transactional
	@Rollback(true)
	public void test() {

		sd = new FinStatusDetail();
		sd.setFinReference("u");
		sd.setCustId(9);
		sd.setFinStatus("ok");
		sd.setValueDate(dt);

		finStatusDetailDAO.getFinStatusDetailByRefId(22);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void save() {

		sd = new FinStatusDetail();
		sd.setFinReference("u");
		sd.setCustId(3);
		sd.setFinStatus("ok");
		sd.setValueDate(dt);
		sd.setODDays(7);
		finStatusDetailDAO.save(sd);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void update() {
		List<FinStatusDetail> cs = new ArrayList<FinStatusDetail>();
		sd = new FinStatusDetail();
		sd.setFinReference("u");
		sd.setCustId(2);
		sd.setFinStatus("ok");
		sd.setValueDate(dt);
		cs.add(sd);
		finStatusDetailDAO.updateCustStatuses(cs);

	}

	@Test
	@Transactional
	@Rollback(true)
	public void saveUpdate() {
		sd = new FinStatusDetail();
		sd.setFinReference("k");
		sd.setCustId(1);
		sd.setFinStatus("no");
		sd.setValueDate(dt);
		finStatusDetailDAO.saveOrUpdateFinStatus(sd);

	}
}

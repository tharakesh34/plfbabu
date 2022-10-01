package com.pennant.backend.dao.apicollecetiondetails;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.model.external.collection.CollectionAPIDetail;

public class CollectionAPIDetailDAOImpl extends SequenceDao<CollectionAPIDetail> implements CollectionAPIDetailDAO {
	private static Logger logger = LogManager.getLogger(CollectionAPIDetailDAOImpl.class);

	public CollectionAPIDetailDAOImpl() {
		super();
	}

	@Override
	public void save(CollectionAPIDetail ca) {
		StringBuilder sql = new StringBuilder("Insert Into CollectionAPIDetail");
		sql.append(" (APIID, FinReference, ReceiptId, Amount, ServiceName, ModuleCode)");
		sql.append(" Values(?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, ca.getApiID());
			ps.setString(index++, ca.getFinReference());
			ps.setLong(index++, ca.getReceiptID());
			ps.setBigDecimal(index++, ca.getAmount());
			ps.setString(index++, ca.getServiceName());
			ps.setString(index, ca.getModuleCode());

		});
	}

	@Override
	public void update(long linkedTranId, String type, long receiptId) {
		String sql = "Update CollectionAPIDetail Set LinkedTranId = ? Where ServiceName = ? and ReceiptID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			ps.setLong(1, linkedTranId);
			ps.setString(2, type);
			ps.setLong(3, receiptId);
		});
	}

	@Override
	public boolean isEntryExists(long receiptId, String serviceName) {
		String sql = "Select count(ReceiptId) from CollectionAPIDetail where ReceiptId = ? and ServiceName = ? and LinkedTranId > ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, receiptId, serviceName, 0) > 0;
	}
}

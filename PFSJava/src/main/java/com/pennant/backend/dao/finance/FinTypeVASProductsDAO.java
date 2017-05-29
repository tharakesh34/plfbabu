package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.financemanagement.FinTypeVASProducts;

public interface FinTypeVASProductsDAO {
	FinTypeVASProducts getfinTypeVASProducts();
	FinTypeVASProducts getNewfinTypeVASProducts();
	void save(FinTypeVASProducts finTypeVASProducts, String type);
	void delete(String finType, String vasProduct, String type);
	void update(FinTypeVASProducts finTypeVASProducts,String type);
	void deleteList(String finType,  String type);
	List<FinTypeVASProducts> getVASProductsByFinType(String finType,  String type);
	FinTypeVASProducts getFinTypeVASProducts(String finType,String vasProduct, String type);

}

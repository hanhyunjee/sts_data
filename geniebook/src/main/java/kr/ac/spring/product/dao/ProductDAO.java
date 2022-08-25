package kr.ac.spring.product.dao;

import java.util.HashMap;
import java.util.List;
import org.springframework.dao.DataAccessException;

import kr.ac.spring.product.vo.ProductVO;


public interface ProductDAO {
	public List<ProductVO> selectRecommendationList() throws DataAccessException;
	public ProductVO selectBooksDetail(int bookNo) throws DataAccessException;
	public List<ProductVO> selectProductListAll() throws DataAccessException;
	public List<ProductVO> selectProductByCategory(String category);
	
	public List<ProductVO> selectProductByTitle(String searchWord) throws DataAccessException;
	public List<ProductVO> selectProductByWriter(String searchWord) throws DataAccessException;
	public List<String> selectKeywordSearch(String keyword) throws DataAccessException;
}

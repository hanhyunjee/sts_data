package kr.ac.spring.product.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import kr.ac.spring.product.vo.ProductVO;


@Repository("ProductDAO")
public class ProductDAOImpl implements ProductDAO {
	@Autowired
	private SqlSession sqlSession;

	@Override
	public List<ProductVO> selectRecommendationList() throws DataAccessException {
		return sqlSession.selectList("mapper.product.selectBookListByRecommendation");
	}
	
	@Override
	public List<ProductVO> selectProductListAll() throws DataAccessException {
		return sqlSession.selectList("mapper.product.selectProductListAll");
	}

	@Override
	public ProductVO selectBooksDetail(int bookNo) throws DataAccessException {
		return sqlSession.selectOne("mapper.product.selectProductByBookNo",bookNo);
	}

	@Override
	public List<ProductVO> selectProductByCategory(String category) {
		return sqlSession.selectList("mapper.product.selectBookListByCategory", category);
	}
	
	@Override
	public List<String> selectKeywordSearch(String keyword) throws DataAccessException {
	   List<String> list=sqlSession.selectList("mapper.product.selectKeywordSearch",keyword);
	   return list;
	}
	
	@Override
	public ArrayList selectProductByTitle(String searchWord) throws DataAccessException{
		ArrayList list=(ArrayList) sqlSession.selectList("mapper.product.selectProductByTitle",searchWord);
		 return list;
	}
	
	@Override
	public ArrayList selectProductByWriter(String searchWord) throws DataAccessException{
		ArrayList list=(ArrayList) sqlSession.selectList("mapper.product.selectProductByWriter",searchWord);
		 return list;
	}

}

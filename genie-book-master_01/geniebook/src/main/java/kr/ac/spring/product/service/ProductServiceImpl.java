package kr.ac.spring.product.service;


import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import kr.ac.spring.product.dao.ProductDAO;
import kr.ac.spring.product.vo.ProductVO;

@Service("ProductService")
@Transactional(propagation=Propagation.REQUIRED)
public class ProductServiceImpl implements ProductService {
	@Autowired
	private ProductDAO productDAO;
	
	@Override
	// 추천도서
	public List<ProductVO> listRecommendation() throws Exception {
		List<ProductVO> productList = productDAO.selectRecommendationList(); 
		return productList;
	}
	@Override
	// 도서 상세보기
	public ProductVO bookDetail(int bookNo) throws Exception {
		ProductVO product = productDAO.selectBooksDetail(bookNo);
		return product;
	}

	@Override
	// 상품전체보기
	public List<ProductVO> listProductAll() throws Exception {
		List<ProductVO> productList = productDAO.selectProductListAll();
		return productList;
	}
	@Override
	// 카테고리
	public List<ProductVO> listProductByCategory(String category) {
		List<ProductVO> productList = productDAO.selectProductByCategory(category);
		return productList;
	}
	
}

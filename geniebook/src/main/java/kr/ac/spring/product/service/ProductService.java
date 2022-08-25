package kr.ac.spring.product.service;

import java.util.HashMap;
import java.util.List;

import kr.ac.spring.product.vo.ProductVO;


public interface ProductService {
	public List<ProductVO> listRecommendation() throws Exception;
	public List<ProductVO> listProductAll() throws Exception;
	public ProductVO bookDetail(int bookNo) throws Exception;
	public List<ProductVO> listProductByCategory(String category);
	
	public List<String> keywordSearch(String keyword) throws Exception;
	public List<ProductVO> searchProductbyTitle(String searchWord) throws Exception;
	public List<ProductVO> searchProductbyWriter(String searchWord) throws Exception;

}

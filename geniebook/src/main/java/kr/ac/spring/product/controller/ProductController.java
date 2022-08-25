package kr.ac.spring.product.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

public interface ProductController {
	public ModelAndView productDetail(@RequestParam("bookNo") int bookNo, HttpServletRequest request, HttpServletResponse response) throws Exception;
	public ModelAndView productAll(HttpServletRequest request) throws Exception;
	
	public @ResponseBody String keywordSearch(@RequestParam("keyword") String keyword,HttpServletRequest request, HttpServletResponse response) throws Exception;
	public ModelAndView searchProductbyTitle(@RequestParam("searchWord") String searchWord,@RequestParam("searchType") String searchType,HttpServletRequest request, HttpServletResponse response) throws Exception;
	public ModelAndView searchProductbyWriter(@RequestParam("searchWord") String searchWord,@RequestParam("searchType") String searchType,HttpServletRequest request, HttpServletResponse response) throws Exception;
}

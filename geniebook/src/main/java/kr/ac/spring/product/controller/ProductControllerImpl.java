package kr.ac.spring.product.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


import kr.ac.spring.product.service.ProductService;
import kr.ac.spring.product.vo.ProductVO;
import net.sf.json.JSONObject;

@Controller
public class ProductControllerImpl implements ProductController {
	@Autowired
	private ProductService productService;


	// 세션 이용시 추가
	// HttpSession session=request.getSession();
	// session.setAttribute("productVO", productVO);

	@Override
	@RequestMapping(value = "/productDetail", method = RequestMethod.GET)
	public ModelAndView productDetail(@RequestParam("bookNo") int bookNo, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName"); // setAttribute("viewName", ***) 이 없는데 
		ProductVO product = productService.bookDetail(bookNo);       // 정상적으로 실행?
		ModelAndView mav = new ModelAndView(viewName);               // 30장을 보면 getViewName에서 setAttribute로 뷰네임을 지정한다음에
		mav.addObject("product", product);                           // 컨트롤러에서 getAttribute로 뷰네임을 가져와서 mav 객체를 리턴하는 방식..
		System.out.println(mav.getViewName());                       // getViewName 출력 결과 콘솔에서 null 나옴
		return mav;                                                  // viewName이 null인데 뷰는 이상없이 출력
	}                                                                // 추측 : 스프링자체에서 viewName이 null이면 tiles.xml에서
	                                                                 // 요청 url과 일치하는 이름의 jsp파일을 찾아주는거같다.
                                                                     // 30장을 대상으로 테스트해본결과 mav 객체에 viewName을 저장하지않고 
	                                                                 // /board/listArticles.do 였던것을 listArticles로 줄이고 
	   															     // tiles의 name도 listArticles로 서로 맞췄더니 오류없이 실행 
	                                                                 // 무엇이 원인인지 추측불가
	
	@RequestMapping(value = "/category/{categoryName}/{categoryNum}", method = RequestMethod.GET)   // "/category/novel/1" , 1은 나라별소설 2는 고전소설 3은 장르소설
	public ModelAndView productByCategory(@PathVariable("categoryName") String categoryName, @PathVariable("categoryNum") String num, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String category = categoryName+num;  // novel1 => product 테이블의 category 컬럼 참고
		System.out.println(category);
		List<ProductVO> productList = productService.listProductByCategory(category);
		ModelAndView mav = new ModelAndView("productByCategory");
		mav.addObject("productList", productList);
		return mav;   // 이것도 viewName 지정 안했는데 정상 출력..
	}

	@Override
	@RequestMapping("/productAll")
	public ModelAndView productAll(HttpServletRequest request) throws Exception {
		System.out.println("productAll");
		
		String viewName = (String) request.getAttribute("viewName");
		System.out.println("viewName : " +viewName);
		List<ProductVO> productList = productService.listProductAll();
		
		ModelAndView mav = new ModelAndView(viewName);
		mav.addObject("productList", productList);
		System.out.println("viewName : " + mav.getView());
		return mav;
		
	}
	
	@RequestMapping(value="/keywordSearch",method = RequestMethod.GET,produces = "application/text; charset=utf8")
	public @ResponseBody String keywordSearch(@RequestParam("keyword") String keyword,
			                                  HttpServletRequest request, HttpServletResponse response) throws Exception{
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		System.out.println(keyword);
		if(keyword == null || keyword.equals(""))
		   return null ;
	
		keyword = keyword.toUpperCase();
	    List<String> keywordList =productService.keywordSearch(keyword);
	    
	 // 최종 완성될 JSONObject 선언(전체)
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("keyword", keywordList);
		 		
	    String jsonInfo = jsonObject.toString();
	    System.out.println(jsonInfo);
	    return jsonInfo ;
	}
	
	@RequestMapping(value="/searchProductbyTitle" ,method = RequestMethod.GET)
	public ModelAndView searchProductbyTitle(@RequestParam("searchWord") String searchWord, @RequestParam("searchType") String searchType,
			                       HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		List<ProductVO> productList= productService.searchProductbyTitle(searchWord);
		ModelAndView mav = new ModelAndView("productAll");
		mav.addObject("searchType",searchType);
		mav.addObject("searchWord",searchWord);
		mav.addObject("productList", productList);
		return mav;
		}
	
	@RequestMapping(value="/searchProductbyWriter" ,method = RequestMethod.GET)
	public ModelAndView searchProductbyWriter(@RequestParam("searchWord") String searchWord, @RequestParam("searchType") String searchType,
			                       HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		List<ProductVO> productList= productService.searchProductbyWriter(searchWord);
		ModelAndView mav = new ModelAndView("productAll");
		mav.addObject("searchType",searchType);
		mav.addObject("searchWord",searchWord);
		mav.addObject("productList", productList);
		return mav;
		}
		
	}


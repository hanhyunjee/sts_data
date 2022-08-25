package kr.ac.spring.admin.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import kr.ac.spring.admin.service.AdminService;
import kr.ac.spring.member.service.MemberService;
import kr.ac.spring.member.vo.AddrVO;
import kr.ac.spring.member.vo.MemberVO;
import kr.ac.spring.product.service.ProductService;
import kr.ac.spring.product.vo.ProductVO;

@Controller
@RequestMapping("/admin")
public class AdminController {
    // 필요한 각 메서드들을 실행시키기 위한 빈 주입
	@Autowired
	AdminService adminService;

	@Autowired
	MemberService memberService;

	@Autowired
	ProductService productService;

	@Autowired
	private JavaMailSenderImpl mailSender;  // 회원에게 메일 보내기
	
	private static final String PRODUCT_IMAGE_REPO = "resources/images/BookImg";

	/*-------------------------- admin의 회원 관리 ----------------------------*/
	@RequestMapping(value = "/adminManager")  // "/admin/adminManager"
	public String adminManager(HttpServletRequest request, Model model) throws Exception {
		System.out.println(request.getServletPath());

		List<MemberVO> memberList = adminService.getMemberAllInfo();   // 아이디가 admin인 것을 제외한 모든 회원정보 조회 
		List<ProductVO> productList = productService.listProductAll(); // 모든 상품 조회 (bookno 순으로)
		
		model.addAttribute("memberList", memberList);       // model 객체에 정보들을 담고 
		model.addAttribute("productList", productList);     

		return "adminManager";    // 관리자 화면으로 이동
	}

	@RequestMapping(value = "/member_search", method = RequestMethod.GET)  // 회원 ID 검색 버튼
	public String member_search(HttpServletRequest request, @RequestParam("id") String id, Model model) {   // url에서 id 파라미터 추출후 id 변수에 저장
		System.out.println(request.getServletPath());
		List<MemberVO> memberList = adminService.selectMemberInfo(id);     // 해당 id의 회원 정보 조회

		model.addAttribute("memberList", memberList);       // model 객체에 회원정보를 담고

		return "member_search";   //  admin/adminManager.jsp 으로 이동
	}

	@RequestMapping(value = "/memberDetail", method = RequestMethod.GET)  // 상세보기 버튼
	public String memberInfo_detail(HttpServletRequest request, @RequestParam("id") String id, Model model) {
		System.out.println(request.getServletPath());
		MemberVO memberVO = new MemberVO();
		AddrVO addrVO = new AddrVO();

		memberVO = memberService.selectUserInfo(id);      
		addrVO = memberService.selectUserInfo_Addr(id);   
	

		model.addAttribute("memberVO", memberVO);
		model.addAttribute("addrVO", addrVO);
		
		System.out.println("receive: "+memberVO.getReceive_email());
		

		return "memberDetail";
	}

	/*-------------------------- 메일 전송 ----------------------------*/

	@RequestMapping(value = "/mailSending/{id}")
	public String mailSending(HttpServletRequest request, @PathVariable("id") String id, Model model) {
		System.out.println(request.getServletPath());
		MemberVO memberVO = new MemberVO();
		if (id.equals("all")) { 	// 모든 회원에게 메일보내기

			return "mailSending_all";
		} else {                    // 특정 회원에게 메일보내기
			memberVO = memberService.selectUserInfo(id);
			model.addAttribute("memberVO", memberVO);
			return "mailSending";
		}

	}

	@RequestMapping(value = { "/mailSending/mailSending.do", "/mailSending/mailSending_all.do" })
	public String sendMail(HttpServletRequest request) {
		System.out.println(request.getServletPath());

		if (request.getServletPath().equals("/mailSending/mailSending.do")) {
			/* mail configuration => mybatis-context.xml */
			String to = request.getParameter("toMail");
			String title = request.getParameter("mailTitle");
			String content = request.getParameter("mailContent");

			final MimeMessagePreparator preparator = new MimeMessagePreparator() {
				@Override
				public void prepare(MimeMessage mimeMessage) throws Exception {
					final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
					helper.setTo(to);
					helper.setSubject(title);
					helper.setText(content, true);
				}
			};
			mailSender.send(preparator);

			return "redirect:../adminManager";

		} else {
			List<MemberVO> memberList;
			List<MemberVO> memList = new ArrayList<MemberVO>();
			memberList = adminService.getMemberAllInfo();

			for (MemberVO vo : memberList) {
				if (vo.getReceive_email() == 1) {
					memList.add(vo);
				} else
					continue;
			}
			if (memList.size() != 0) {
				MimeMessagePreparator[] preparators = new MimeMessagePreparator[memList.size()];
				int i = 0;
				String title = request.getParameter("mailTitle");
				String content = request.getParameter("mailContent");
				for (final MemberVO vo : memList) {

					preparators[i++] = new MimeMessagePreparator() {
						@Override
						public void prepare(MimeMessage mimeMessage) throws Exception {

							final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
							helper.setTo(vo.getEmail());
							helper.setSubject(title);
							helper.setText(content);
						}

					};

				}
				mailSender.send(preparators);
			}
			return "redirect:../adminManager";
		}
	}
    /*-------------------------- Admin의 상품 검색---------------------------------------------*/
	@RequestMapping(value = "/adminbyTitle" ,method = RequestMethod.GET)
	public ModelAndView searchProductbyTitle(@RequestParam("searchWord") String searchWord, @RequestParam("searchType") String searchType,
			                       HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		List<ProductVO> productList= adminService.searchProductByTitle(searchWord);
		ModelAndView mav = new ModelAndView("adminManager");
		mav.addObject("searchType",searchType);
		mav.addObject("searchWord",searchWord);
		mav.addObject("productList", productList);
		return mav;
		}
	@RequestMapping(value = "/adminbyCategory" ,method = RequestMethod.GET)
	public ModelAndView searchProductbyCategory(@RequestParam("searchWord") String searchWord, @RequestParam("searchType") String searchType,
			                       HttpServletRequest request, HttpServletResponse response) throws Exception{
		// 관리자가 검색한 "나라별소설"을 novel1으로 바꾸는 과정
		HashMap<String, String> categories = new HashMap<String, String>();
		
		String[][] categories_String = { { "나라별소설", "novel1" }, { "고전/문학", "novel2" }, { "장르소설", "novel3" },
				{ "한국시", "poem1" }, { "외국시", "poem2" }, { "여행 에세이", "poem3" }, { "대화/협상", "selfDevelopment1" },
				{ "자기능력계발", "selfDevelopment2" }, { "인문일반", "liberal1" }, { "심리", "liberal2" }, { "철학", "liberal3" },
				{ "어린이(공통)", "child1" }, { "초등", "child2" }, };
		
		for (String[] c : categories_String) {
			categories.put(c[0], c[1]);
		}
		
		String category = categories.get(searchWord);
		System.out.println(category);
		// 과정 끝
		List<ProductVO> productList= adminService.searchProductByCategory(category);
		ModelAndView mav = new ModelAndView("adminManager");
		
		mav.addObject("searchType",searchType);
		mav.addObject("searchWord",searchWord);
		mav.addObject("productList", productList);
		return mav;
		}
	@RequestMapping(value = "/adminbyWriter" ,method = RequestMethod.GET)
	public ModelAndView searchProductbyWriter(@RequestParam("searchWord") String searchWord, @RequestParam("searchType") String searchType,
			                       HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		List<ProductVO> productList= adminService.searchProductByWriter(searchWord);
		ModelAndView mav = new ModelAndView("adminManager");
		mav.addObject("searchType",searchType);
		mav.addObject("searchWord",searchWord);
		mav.addObject("productList", productList);
		return mav;
		}
	@RequestMapping(value = "/adminbyPublisher" ,method = RequestMethod.GET)
	public ModelAndView searchProductbyPublisher(@RequestParam("searchWord") String searchWord, @RequestParam("searchType") String searchType,
			                       HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		List<ProductVO> productList= adminService.searchProductByPublisher(searchWord);
		ModelAndView mav = new ModelAndView("adminManager");
		mav.addObject("searchType",searchType);
		mav.addObject("searchWord",searchWord);
		mav.addObject("productList", productList);
		return mav;
		}
	
	
	
	
	/*-------------------------- Admin의 상품 관리(adminManager.jsp) --------------------------*/

	@RequestMapping(value = "/addProductForm")
	public String addProductForm(HttpServletRequest request, HttpServletResponse response) {
		System.out.println(request.getServletPath());
		return "addProductForm";
	}

	@RequestMapping(value = "/deleteProduct", method = RequestMethod.GET)
	public ModelAndView deleteProduct(@RequestParam("bookNo") int bookNo, HttpServletRequest request,  // url에서 책번호 가져옴
			HttpServletResponse response) throws IOException {
		System.out.println(request.getServletPath());
		ModelAndView mav = new ModelAndView("adminManager");   // ModelAndView 객체에 뷰(jsp) 이름을 저장

		response.setContentType("text/html; charset=UTF-8");
		mav.addObject("deleteStatus", adminService.deleteProduct(bookNo));  // ModelAndView 객체에 {"deleteStatus" : 삭제된 행 갯수} 저장
		System.out.println(mav.getModel());                    // {deleteStatus=1} 출력
		return mav;    // 서블릿 디스패처에게 ModelAndView 객체 리턴 
	}

	@RequestMapping(value = "/updateProductForm", method = RequestMethod.GET)  // productDetail.jsp 수정하기 버튼
	public String updateProductForm(@RequestParam("bookNo") int bookNo, HttpServletRequest request,
			HttpServletResponse response, Model model) throws Exception {
		System.out.println(request.getServletPath());
		ProductVO productVO = productService.bookDetail(bookNo);  // 책 번호에 해당하는 책 정보 조회
		model.addAttribute("productVO", productVO);               // model 객체에 조회된 정보 저장
		return "updateProductForm";         
	}

	@RequestMapping(value = "/updateProduct", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView updateProduct(@RequestParam("bookNo") int bookNo, MultipartHttpServletRequest request,  // MultipartHttpServletRequest 클래스의 메서드들  
			HttpServletResponse response, MultipartFile uploadFile) throws Exception {                          // : getParameterNames, getSession
		System.out.println(request.getServletPath());
		ProductVO productVO = productService.bookDetail(bookNo);  // 책 번호에 해당하는 책 정보 조회
		request.setCharacterEncoding("utf-8");

		Map<String, Object> map = new HashMap<String, Object>(); 
		Enumeration enu = request.getParameterNames();           // getParameterNames의 리턴형이 열거형이므로 Enumeration형 변수 선언.
		System.out.println(enu);                                 // updateProductForm.jsp 에서 넘어온 파라미터들을 한꺼번에 받아온다.
		while (enu.hasMoreElements()) {                          // 파라미터들이 없을때까지,
			String name = (String) enu.nextElement();            // 차례로 name에 저장
			Object value = request.getParameter(name);           // 파라미터의 '이름'만 받아왔다면, '값'을 다시 받아온다. 
			System.out.println(name + ", " + value);             // price, 100  imageFileName, ...
			map.put(name, value);
			System.out.println(productVO.toString());            // ProductVO [bookNo=0, category=novel1, bookName=test, writer=test,..]
			productVO.setProperty(name, value);
		}
		String imageFileName = upload(request);                  // 밑에있는 upload 메서드를 통해 이미지 파일을 경로로 이동시키고 이름을 얻어옴
		map.put("imageFileName", imageFileName);
		if (imageFileName != null)
			productVO.setImageFileName(imageFileName);

		response.setContentType("text/html; charset=UTF-8");
		ModelAndView mav = new ModelAndView("productDetail_admin");              // ModelAndView 객체를 생성함과 동시에 뷰(jsp) 이름 저장 
		mav.addObject("updateStatus", adminService.updateProduct(productVO));    // ModelAndView 객체에 {"updateStatus" : 수정된 행 갯수} 저장
		mav.addObject("bookNo",bookNo);                                          // ModelAndView 객체에 책번호 저장
		System.out.println(request.getServletPath());
		System.out.println(mav.getModel());                                      // {updateStatus=1, bookNo=28}
		return mav;   // ModelAndView 객체를 반환하고 productDetail_admin으로 이동
	}

	@RequestMapping(value = "/addProduct")
	public ModelAndView addProduct(MultipartHttpServletRequest request, HttpServletResponse response,
			MultipartFile uploadFile, Model model) throws Exception {
		System.out.println(request.getServletPath());

		ProductVO productVO = new ProductVO();
		request.setCharacterEncoding("utf-8");

		Map<String, Object> map = new HashMap<String, Object>();
		Enumeration enu = request.getParameterNames();

		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			Object value = request.getParameter(name);
			System.out.println(name + ", " + value);    
			map.put(name, value);
			productVO.setProperty(name, value);
		}
		String imageFileName = upload(request);
		map.put("imageFileName", imageFileName);
		if (imageFileName != null)
			productVO.setImageFileName(imageFileName);
		else
			productVO.setImageFileName(null);
		System.out.println(productVO.toString());

		ModelAndView mav = new ModelAndView("redirect:/admin/adminManager");

		response.setContentType("text/html; charset=UTF-8");
		mav.addObject("insertStatus", adminService.addProduct(productVO));
		return mav;
	}

	@RequestMapping(value = "/productDetail", method = RequestMethod.GET)
	public String productDetail(HttpServletRequest request, @RequestParam("bookNo") int bookNo, Model model)
			throws Exception {
		System.out.println(request.getServletPath());

		ProductVO productVO = new ProductVO();
		productVO = productService.bookDetail(bookNo);  // 상품 번호에 해당하는 책 정보 조회

		HashMap<String, String> categories = new HashMap<String, String>();
		// n행 m열의 배열 생성
		String[][] categories_String = { { "novel1", "나라별소설" }, { "novel2", "고전/문학" }, { "novel3", "장르소설" },
				{ "poem1", "한국시" }, { "poem2", "외국시" }, { "poem3", "여행 에세이" }, { "selfDevelopment1", "대화/협상" },
				{ "selfDevelopment2", "자기능력계발" }, { "liberal1", "인문일반" }, { "liberal2", "심리" }, { "liberal3", "철학" },
				{ "child1", "어린이(공통)" }, { "child2", "초등" }, };

		for (String[] c : categories_String) {
			categories.put(c[0], c[1]);
		}
      
		model.addAttribute("productVO", productVO);    // model 객체에 책 정보와 카테고리 배열을 저장한 후,
		model.addAttribute("categories", categories);
		System.out.println(model.toString());          // {productVO=ProductVO [bookNo=2, category=novel1, bookName=죽음을 보는 재능,..], categories={selfDevelopment1=대화/협상,...}}
		return "productDetail_admin";                  // admin/productDetail.jsp으로 이동
	}

	/* File Upload */
	private String upload(MultipartHttpServletRequest multipartRequest) throws Exception {
		String imageFileName = null;
		Iterator<String> fileNames = multipartRequest.getFileNames();
		String path = multipartRequest.getSession().getServletContext().getRealPath(PRODUCT_IMAGE_REPO);
		System.out.println(path);
		while (fileNames.hasNext()) {
			String fileName = fileNames.next();
			MultipartFile mFile = multipartRequest.getFile(fileName);
			imageFileName = mFile.getOriginalFilename();
			File file = new File(path + "/" + fileName);
			if (mFile.getSize() != 0) { // File Null Check
				if (!file.exists()) { // 경로 상에 파일이 존재하지 않다면
					if (file.getParentFile().mkdirs()) { // 파일의 경로에 해당하는 디렉토리를 모두 생성(mkdirs는 경로상 폴더를 모두 만들어줌)
						file.createNewFile(); // 파일 생성
					}
				}
				mFile.transferTo(new File(path + "/"+ imageFileName)); // 파일을 이동시킨다.
			}
		}
		return imageFileName;
	}

	private String delete(MultipartHttpServletRequest multipartRequest, String deleteFileName) throws Exception {
		String imageFileName = null;
		Iterator<String> fileNames = multipartRequest.getFileNames();
		String path = multipartRequest.getSession().getServletContext().getRealPath(PRODUCT_IMAGE_REPO);
		while (fileNames.hasNext()) {
			String fileName = fileNames.next();
			MultipartFile mFile = multipartRequest.getFile(fileName);
			imageFileName = mFile.getOriginalFilename();
			File file = new File(path + "/" + fileName);
			if (mFile.getSize() != 0) { // File Null Check
				if (!file.exists()) { // 경로 상에 파일이 존재하지 않다면
					if (file.getParentFile().mkdirs()) { // 파일의 경로에 해당하는 디렉토리를 모두 생성(mkdirs는 경로상 폴더를 모두 만들어줌)
						file.createNewFile(); // 파일 생성
					}
				}
				mFile.transferTo(new File(path + "/" + imageFileName)); // 파일을 이동시킨다.
			}
		}
		return imageFileName;
	}
}

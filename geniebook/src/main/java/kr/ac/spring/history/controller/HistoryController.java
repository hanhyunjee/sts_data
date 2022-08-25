package kr.ac.spring.history.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import kr.ac.spring.cart.service.CartItemService;
import kr.ac.spring.cart.service.CartService;
import kr.ac.spring.cart.vo.CartItemVO;
import kr.ac.spring.cart.vo.CartVO;
import kr.ac.spring.history.service.HistoryService;
import kr.ac.spring.history.vo.HistoryVO;
import kr.ac.spring.member.service.MemberService;
import kr.ac.spring.member.vo.AddrVO;
import kr.ac.spring.member.vo.MemberVO;
import kr.ac.spring.product.service.ProductServiceImpl;
import kr.ac.spring.product.vo.ProductVO;

@Controller
public class HistoryController {
	// 
	@Autowired
	private HistoryService historyService;
	@Autowired
	private CartService cartService;
	@Autowired
	private ProductServiceImpl productService;
	@Autowired
	private MemberService memberService;

	@Autowired
	private CartItemService cartItemService;

	@RequestMapping(value = "historyDetail/{id}/{purchase_date}", method = RequestMethod.GET)  // "/historyDetail?id=abc&purchase_date=2022-08-15" 형식이 아닌, Rustful 형식의 url.
	public String historyDetail(@PathVariable("id") String id, @PathVariable("purchase_date") 
			@DateTimeFormat(pattern="E MMM dd HH:mm:ss z yyyy") Date purchase_date,   // Restful 형식의 url에서 파라미터를 가져오기 위한 @PathVariable 어노테이션
			HttpServletRequest request , Model model) throws Exception {
		
		System.out.println("경로 :" + request.getServletPath());
		List<HistoryVO> history = historyService.getHistoryByIdByDate(id, purchase_date);   // url에서 가져온 아이디와 구매날짜로 구매내역 조회
		List<ProductVO> productList = new ArrayList<ProductVO>();
		if (history.size() != 0) {                            // 리스트에 요소가 존재하면
			for (int i = 0; i < history.size(); i++) {        // 리스트 크기만큼 for문 돌림
				productList.add(productService.bookDetail(history.get(i).getBookNo()));  // 리스트의 i번째에 해당하는 책의 번호를 얻어오고 그번호에 해당하는 책의 상세 정보를
			}                                                                        // 새로운 product라는 리스트에 삽입.
		}
		model.addAttribute("historyList", history);               // model 객체에 구매내역과 책의 상세 정보들을 담고
		model.addAttribute("product", productList);
		return "historyDetail";                               // historyDetail.jsp로 이동
	}

	@RequestMapping(value = "purchaseHistory/{id}", method = RequestMethod.GET)
	public String memberHistory(Model model, @PathVariable("id") String id) throws Exception {
		List<String> productName = new ArrayList<String>();
		List<HistoryVO> historyList = historyService.getHistoryById(id);
		if (historyList.size() != 0) {
			for (int i = 0; i < historyList.size(); i++) {
				productName.add(productService.bookDetail(historyList.get(i).getBookNo()).getBookName());  // i번째에 해당하는 책의 번호를 가져와 bookDetail를 실행한 후 그결과는 'bookVO' 형이므로
			}                                                                                              // bookVO.getBookName()로 다시 책 이름을 가져와 productName이라는 리스트에 담는다.
		}
		
		model.addAttribute("historyList", historyList);
		model.addAttribute("productName", productName);
		return "memberHistory";
	}
	// 포인트 적립 
	// cart 테이블과 cartItem 테이블 차이 
	// cart  : cartID와 장바구니에 담긴 총액
	// cartItem : cartID, 책번호, 수량, 가격
	@RequestMapping(value = "/payment", method = RequestMethod.POST)   // checkout.jsp 결제하기 버튼
	public String payment(@ModelAttribute("memberVO") MemberVO member, @ModelAttribute("addrVO") AddrVO addr,   // memberVO,addrVO 객체 생성. url에서 넘어온 파라미터를 자동으로 바인딩 
			HttpServletRequest request, @RequestParam("cartId") int cartId) throws Exception {                  // url에서 cartId 가져옴
		CartVO cartVO = cartService.getCartById(cartId);     // cartid에 해당하는 회원의 장바구니 총액 (member테이블의 cartid_mem)
		int grandTotal = (int) cartVO.getGrandTotal();       // 총액
		List<CartItemVO> cartItemList = cartItemService.getCartItemsByCartId(cartId);   // cartID에 해당하는 회원의 장바구니에 담긴 상품 조회
		for (int i = 0; i < cartItemList.size(); i++) {                                 // 장바구니에 담긴 상품 갯수만큼 for문
			ProductVO product = productService.bookDetail(cartItemList.get(i).getBookNo());     // i번째에 해당하는 상품의 상품번호 상세정보 조회
			HistoryVO history = new HistoryVO();
			history.setId(member.getId());                  
			history.setName(member.getName());
			history.setContent(request.getParameter("content"));  // 요청사항
			history.setEmail(member.getEmail());
			history.setPhoneNum(member.getPhoneNum());
			history.setAddr_detail(addr.getAddr_detail());
			history.setAddr_num(addr.getAddr_num());
			history.setAddr_road(addr.getAddr_road());
			history.setBookNo(product.getBookNo());
			history.setPrice(product.getPrice());
			history.setCartId(cartId);
			history.setQuantity(cartItemList.get(i).getQuantity());
			history.setGrandTotal(grandTotal);

			historyService.addHistory(history);  // 구매내역에 추가 

		}

		memberService.addAcc_Price(member.getId(), (int) grandTotal);        // 해당하는 회원의 지금까지 총 구매액(Acc_Price)에 장바구니 총 금액 추가
		memberService.addPoint(member.getId(), (int) (grandTotal * 0.02));   // 장바구니 총 금액의 2%만큼 포인트 적립 
		cartVO.setGrandTotal(0);
		cartService.updateGrandTotal(cartVO);    // 구매가 끝났으므로 다시 장바구니의 총액 0으로 초기화
		cartItemService.removeAll(cartId);       // 장바구니의 상품들 삭제
		return "redirect:/";
	}

	@RequestMapping(value = "/historyDetail", method = RequestMethod.POST)    // purchaseHistory 상세보기 버튼 
	public String historyDetail(Model model, HttpServletRequest request) throws Exception {
		System.out.println("date" + request.getParameter("purchase_date"));
		System.out.println("id " + request.getParameter("id"));

		String id = request.getParameter("id");
		SimpleDateFormat dt = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
		Date data = dt.parse(request.getParameter("purchase_date").toString());

		
		System.out.println("data: "+data);

		Date purchase_date = dt.parse(request.getParameter("purchase_date").toString());
		List<HistoryVO> history = historyService.getHistoryByIdByDate(id, purchase_date);  // 아이디와 구매날짜로 구매내역 조회
		List<ProductVO> productList = new ArrayList<>();
		System.out.println(history.size());

		if (history.size() != 0) {     // 구매 내역이 있다면
			for (int i = 0; i < history.size(); i++) {
				productList.add(productService.bookDetail(history.get(i).getBookNo()));  
				model.addAttribute("historyList", history);
				model.addAttribute("product", productList);
			}
			model.addAttribute("history", history.get(0));   // 
		}

		return "historyDetail";
	}
}

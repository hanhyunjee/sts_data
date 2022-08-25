package kr.ac.spring.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import kr.ac.spring.history.vo.HistoryVO;
import kr.ac.spring.member.service.MemberService;
import kr.ac.spring.member.vo.AddrVO;
import kr.ac.spring.member.vo.MemberVO;
import kr.ac.spring.product.service.ProductServiceImpl;
import kr.ac.spring.product.vo.ProductVO;

@Controller
public class CartController {

	@Autowired
	private CartService cartService;
	@Autowired
	private ProductServiceImpl productService;
	@Autowired
	private MemberService memberService;

	@Autowired
	private CartItemService cartItemService;

	@RequestMapping("/cart")
	public String getCart(Model model) throws Exception {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();  // 스프링 시큐리티를 이용하여 현재 세션의 사용자 정보 찾기
		String id = authentication.getName();
		int cartId = memberService.selectUserInfo(id).getCartId_mem();  // 현재 세션의 사용자 id로 cartID 조회
		CartVO cart = cartService.getCartById(cartId);                  // 장바구니 조회
		List<ProductVO> productList = new ArrayList<>();
		List<CartItemVO> cartItemList = cartItemService.getCartItemsByCartId(cart.getId());  // cartID로 장바구니에 있는 상품들 조회
		int grandTotal = 0;
		if (cartItemList.size() != 0) {
			for (int i = 0; i < cartItemList.size(); i++) {
				System.out.println(productService.bookDetail(cartItemList.get(i).getBookNo()).getBookName());
				productList.add(productService.bookDetail(cartItemList.get(i).getBookNo())); // i번째에 해당하는 상품의 상품번호로 상세정보를 조회한후 productList라는 리스트에 삽입
				grandTotal += cartItemList.get(i).getTotalPrice();                           // i번째 상품의 가격을 총액에 더함
			}
			cart.setGrandTotal(grandTotal);  // 장바구니안의 상품 총 가격 
		}
		cartService.updateGrandTotal(cart);  
		model.addAttribute("grandTotal",grandTotal);
		model.addAttribute("product", productList);
		model.addAttribute("cartItemList", cartItemList);
		model.addAttribute("id", id);
		model.addAttribute("cartId", cartId);

		return "cart";
	}
	
	@RequestMapping(value = "/checkout/{id}/{cartId}" , method =  RequestMethod.GET)  // 상품 구매 
	public String CheckOut(@PathVariable("cartId") int cartId, @PathVariable("id") String id, Model model) throws Exception {  // Restful 형식의 url에서 파라미터를 가져오기 위한 @PathVariable 어노테이션
		MemberVO memberVO = memberService.selectUserInfo(id);   // url에서 가져온 id를 바탕으로 회원정보 조회 
		List<ProductVO> productList = new ArrayList<>();
		List<CartItemVO> cartItemList = cartItemService.getCartItemsByCartId(cartId);  // url에서 가져온 cartID를 바탕으로 장바구니 상품목록 조회
		int grandTotal = 0;
		if (cartItemList.size() != 0) {
			for (int i = 0; i < cartItemList.size(); i++) {
				System.out.println(productService.bookDetail(cartItemList.get(i).getBookNo()).getBookName());
				productList.add(productService.bookDetail(cartItemList.get(i).getBookNo()));
				grandTotal += cartItemList.get(i).getTotalPrice();
			}
		}else {  // 장바구니에 상품이 없는 경우
			String message ="<script>alert('상품이 없습니다.');</script>";
			model.addAttribute("message", message);
			return "cart";
		}
		
		AddrVO addrVO = memberService.selectUserInfo_Addr(id);   // url에서 가져온 id를 바탕으로 회원의 주소 조회
		model.addAttribute("product", productList);
		model.addAttribute("grandTotal", grandTotal);
		model.addAttribute("cartItemList", cartItemList);
		model.addAttribute("memberVO", memberVO);
		model.addAttribute("addrVO", addrVO);
		model.addAttribute("cartId", cartId);
		return "checkout";
	}

	@RequestMapping(value = "/cartRemoveAll")
	public String cartRemoveAll(@RequestParam("cartId") int cartId, String id) {
		cartItemService.removeAll(cartId);
		return "redirect:cart";
	}
	
	
}

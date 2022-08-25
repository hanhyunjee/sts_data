package kr.ac.spring.cart.controller;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;

import kr.ac.spring.cart.service.CartItemService;
import kr.ac.spring.cart.service.CartService;
import kr.ac.spring.cart.vo.CartItemVO;
import kr.ac.spring.cart.vo.CartVO;
import kr.ac.spring.member.service.MemberService;
import kr.ac.spring.product.service.ProductServiceImpl;
import kr.ac.spring.product.vo.ProductVO;

@Controller
@RequestMapping("/api/cart")
public class CartItemController {

	@Autowired
	private CartService cartService;
	@Autowired
	private ProductServiceImpl productService;
	@Autowired
	private MemberService memberService;

	@Autowired
	private CartItemService cartItemService;

	@RequestMapping(value = "/add/{bookNo}/{quantity}", method = RequestMethod.GET)  // Rest 방식 url
	public String addCartItem(Model model, @PathVariable("bookNo") int bookNo, @PathVariable("quantity") int quantity)
			throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();  // 스프링 시큐리티를 이용한 현재 세션 이용자 정보 찾기
		String id = authentication.getName();
		ProductVO productVO = productService.bookDetail(bookNo);   // url에서 가져온 상품번호로 책의 상세정보 조회
		int cartId = memberService.selectUserInfo(id).getCartId_mem();  // 현재 세션의 이용자 id로 정보를 조회하여 cartID 조회
		CartItemVO cartItem = new CartItemVO();
		if (!cartItemService.isCartItemByBookNo(cartId, bookNo)) { // cartID와 상품번호가 테이블에 존재하면 true, 아니면 false 리턴

			cartItem.setCartId(cartId);      
			cartItem.setBookNo(bookNo);
			cartItem.setQuantity(quantity);
			cartItem.setTotalPrice(quantity * productVO.getPrice());
			cartItemService.addCartItem(cartItem);  // 상품을 카트에 추가 
			return "redirect:/cart";
		} else {                                    // 만약 해당 cartID와 상품번호가 테이블에 이미 존재하면,
			int price = productVO.getPrice();
			cartItemService.addCartItemQuantity(bookNo, cartId, price, quantity);  // 수량을 추가하고 총액을 증가시킴
			return "redirect:/cart";
		}
	}

	@RequestMapping(value = "/delete/{cartId}/{bookNo}") // cart.jsp의 delete_cartItem 자바스크립트 함수에 전달
	public ResponseEntity deleteCartItem(@PathVariable("cartId") int cartId, @PathVariable("bookNo") int bookNo) {
		cartItemService.deleteCartItem(cartId, bookNo);
		ResponseEntity resEntity = null;
		resEntity = new ResponseEntity(HttpStatus.OK);  // 장바구니에서 상품삭제 후 클라이언트에게 상태코드 전달 
		return resEntity;

	}

	@RequestMapping(value = "/plus/{cartId}/{bookNo}", method = RequestMethod.POST) // cart.jsp의 plus_cartItem 자바스크립트 함수에서 전달 
	public @ResponseBody JSONObject plus(@PathVariable("cartId") int cartId, @PathVariable("bookNo") int bookNo)  // json 타입의 데이터를 리턴
			throws Exception {

		System.out.println(cartId + "   " + bookNo);
		int price = productService.bookDetail(bookNo).getPrice();                 // url에서 가져온 상품번호로 상세정보를 조회한 후 가격을 조회
		int quantity = cartItemService.getCartItem(cartId, bookNo).getQuantity(); // url에서 가져온 상품번호와 cartID로 장바구니안의 상품을 조회한 후 수량을 조회
		cartItemService.addCartItemQuantity(bookNo, cartId, price, 1);        // 해당상품의 수량을 1개 추가하며 금액을 증가시킴
		CartItemVO cartItem = cartItemService.getCartItem(cartId, bookNo);    // 수량을 추가하여 업데이트된 상품을 조회

		JSONObject json = new JSONObject();                                   // json 타입 데이터를 저장하기 위한 객체 생성
		json.put("price", productService.bookDetail(cartItem.getBookNo()).getPrice());  // 수량을 추가했던 상품의 상품번호로 상세정보를 조회하여 가격을 조회
		json.put("quantity", cartItem.getQuantity());                                   // 추가된것을 포함한 수량 조회
		json.put("totalPrice", cartItem.getTotalPrice());                               // 마지막 총액 조회 
                                                                                        // 키,값 쌍 형태로 jsonobject 객체에 저장 
		return json;  // cart.jsp의 plus_cartItem 함수의 콜백함수로 전달 
	}

	@RequestMapping(value = "/minus/{cartId}/{bookNo}", method = RequestMethod.POST) // cart.jsp의 minus_cartItem 자바스크립트 함수에서 전달 
	public @ResponseBody JSONObject minus(@PathVariable("cartId") int cartId, @PathVariable("bookNo") int bookNo)
			throws Exception {
		String message = "";
		JSONObject json = new JSONObject();
		System.out.println(cartId + "   " + bookNo);
		int price = productService.bookDetail(bookNo).getPrice();                
		int quantity = cartItemService.getCartItem(cartId, bookNo).getQuantity(); 
		if (quantity == 1) {  // 최소 수량 = 1 
			message = "false"; // => arlet : 최소 수량은 1입니다
			json.put("message", message);
			return json;
		} else {              // 수량이 2개 이상이면 실행 
			cartItemService.minusCartItemQuantity(bookNo, cartId, price, 1);  // 해당상품
			CartItemVO cartItem = cartItemService.getCartItem(cartId, bookNo);

			message = "true";
			json.put("message", message);
			json.put("price", productService.bookDetail(cartItem.getBookNo()).getPrice());
			json.put("quantity", cartItem.getQuantity());
			json.put("totalPrice", cartItem.getTotalPrice());

			return json;  // cart.jsp의 minus_cartItem 함수의 콜백함수로 전달 
		}
	}
}

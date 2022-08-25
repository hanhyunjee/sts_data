package kr.ac.spring.member.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import kr.ac.spring.cart.service.CartService;
import kr.ac.spring.cart.vo.CartVO;
import kr.ac.spring.history.service.HistoryService;
import kr.ac.spring.member.service.MemberService;
import kr.ac.spring.member.vo.AddrVO;
import kr.ac.spring.member.vo.MemberVO;

@Controller
public class MemberController {
	// 각각의 필요한 메서드를 실행시키기 위한 빈 주입
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private CartService cartService;
	@Autowired
	private HistoryService historyService;

	@RequestMapping(value = "/login", method = { RequestMethod.POST, RequestMethod.GET })  // "/login" 호출시 핸들러 실행
	public String login(HttpServletRequest request, @RequestParam(value = "error", required = false) String error,  // error 파라미터를 가져옴(필수는아님)
			Model model) {
		System.out.println(request.getServletPath());
		if (error != null) {                                                   // http://localhost:8090/geniebook/login?error=1
			model.addAttribute("errorMsg", "아이디 혹은 비밀번호가 일치하지 않습니다."); // model 객체에 에러 메시지 담고 
		}

 		return "login";                                                        // login.jsp로 넘어감
	}

//---------------------여기까지 로그인--------------------------------------------------
	@RequestMapping("/registerForm_member")  // "/registerForm_member" 호출시 핸들러 실행
	public String userRegisterForm(HttpServletRequest request) {
		System.out.println(request.getServletPath());

		return "registerForm_member";        // registerForm_member.jsp로 이동
	}

	@RequestMapping(value = "/registerMember", method = RequestMethod.POST)     //  "/registerMember" 호출시 핸들러 실행
	public String registerMember(@ModelAttribute("memberVO") MemberVO memberVO, // memberVO 객체 생성. url에서 넘어온 파라미터를 자동으로 바인딩 (memberVO 클래스 내에 있는 속성들과 파라미터들 결합)
			 RedirectAttributes rAttr, HttpServletRequest request,              // 리다이렉트시 데이터 전달을 위한 RedirectAttributes 객체 생성
			Model model) {                                                                               
		System.out.println(request.getServletPath());

		String message = "";                

		AddrVO addrVO = new AddrVO();        // 주소를 참조하는 객체
		addrVO.setId(request.getParameter("id"));      
		addrVO.setAddr_num(request.getParameter("addr_num"));
		addrVO.setAddr_road(request.getParameter("addr_road"));
		addrVO.setAddr_detail(request.getParameter("addr_detail"));

		if (memberVO.getPassword().equals(request.getParameter("password2"))) {  // resiterForm_member.jsp 에서 입력되어 넘어온 값들 읽어옴
			memberVO.setAuthority("ROLE_USER");  // 스프링 시큐리티 요소. ROLE_USER, ROLE_ADMIN
			memberVO.setEnabled(1);              // 0은 비활성화, 1은 활성화
			memberVO.setPoint(0);                // 포인트 초기설정
			memberVO.setAcc_Price(0);            // 잔액 초기설정
			                                     // id나 pw 들은 이미 자동으로 memberVO 객체에 담겨짐.
			memberService.addMember(memberVO, addrVO);  // 회원 등록
			
			cartService.addCart(memberService.selectUserInfo(memberVO.getId()).getCartId_mem());  // 장바구니 생성 
			
			message = "<script>";
			message += " alert('축하합니다. 회원가입이 완료됐습니다.');";
			message += " </script>";
			
			rAttr.addFlashAttribute("message",message);
			
			return "redirect:/";              // 홈으로 이동

		} else {                              // 비밀번호 확인 불일치시 

			message = "<script>";
			message += " alert('비밀번호를 확인해주세요.');";
			message += " </script>";

			model.addAttribute("memberVO", memberVO);
			model.addAttribute("addrVO", addrVO);
			model.addAttribute("message", message);

			return "registerForm_member";     // 회원가입창으로 이동 
		}
	}
    // id 중복검사 
	@RequestMapping(value = "/registerMember/overlapped.do", method = RequestMethod.POST)
	public ResponseEntity overlapped(@RequestParam("id") String id, HttpServletRequest request,   // url에서 "id" 파라미터 가져옴
			HttpServletResponse response) throws Exception {
		System.out.println(request.getServletPath());
		ResponseEntity resEntity = null;
		String result = memberService.overlapped(id);      // 같은 아이디가 있으면 true, 아니면 false
		System.out.println(result);
		resEntity = new ResponseEntity(result, HttpStatus.OK);     // 클라이언트에게 응답 : HTTP 상태코드 OK
		return resEntity;
	}
//---------------------여기까지 회원가입--------------------------------------------------

	@RequestMapping(value = { "/memberInfo_ui", "/pwdCheck", "/pwdCheck_delete",  // /memberInfo_ui = 회원 정보 화면
			"/updateForm_member" }, method = RequestMethod.GET)
	public String selectUserInfo(Model model, HttpServletRequest request) {
		MemberVO memberVO = new MemberVO();
		AddrVO addrVO = new AddrVO();
		Authentication authentivation = SecurityContextHolder.getContext().getAuthentication();   // 스프링 시큐리티를 이용하여 현재 세션의 사용자 정보 찾기

		String id = authentivation.getName();

		memberVO = memberService.selectUserInfo(id);
		addrVO = memberService.selectUserInfo_Addr(id);

		model.addAttribute("memberVO", memberVO);
		model.addAttribute("addrVO", addrVO);

		System.out.println(request.getServletPath());
		if (request.getServletPath().equals("/memberInfo_ui")) {
			/* 개인정보 ui */
			
			return "memberInfo_ui";
		} else if (request.getServletPath().equals("/pwdCheck")) {
			/* 개인정보 접근 비밀번호 체크 */
			return "pwdCheck";
		} else if (request.getServletPath().equals("/pwdCheck_delete")) {
			/* 개인정보 접근 비밀번호 체크 */
			return "pwdCheck_delete";
		} else {
			/* 개인정보 수정폼 */
			System.err.println("test: " + memberVO.getReceive_email());
			return "updateForm_member";
		}

	}

	@RequestMapping(value = "/updateMember", method = RequestMethod.POST)
	public String updateMember(@RequestParam("password2") String password2,
			@ModelAttribute("memberVO") MemberVO memberVO, @ModelAttribute("addrVO") AddrVO addrVO,  // MemberVO, AddrVO 객체 생성하여 url에서 넘어온 파라미터를 자동으로 바인딩.
			HttpServletRequest request, HttpServletResponse response, Model model,
			RedirectAttributes redirectAttributes) {                                                 // 리다이렉트시 데이터 전달을 위한 RedirectAttributes 객체 생성

		System.out.println(request.getServletPath());

		String message = "";

		if (memberVO.getPassword().equals(password2)) {      // 비밀번호를 확인했을때 비밀번호 일치시

			if (!memberVO.getPassword().equals("") && !password2.equals("")) {    // 공백이 아닐때,

				memberService.updateMember(memberVO, addrVO);   // 파라미터들(id, pw 등)과 바인딩 되었던 MemberVO, AddrVO 객체를 가지고 회원정보 업데이트 실행
				                                              

				message = "<script>";
				message += " alert('회원정보 수정이 완료되었습니다. 다시 로그인 해주세요');";
				message += " location.href='" + request.getContextPath() + "/login';";
				message += " </script>";

				Authentication auth = SecurityContextHolder.getContext().getAuthentication();  // 스프링 시큐리티를 이용하여 현재 세션의 사용자 정보 찾기
				if (auth != null) {
					new SecurityContextLogoutHandler().logout(request, response, auth);  // 현재 세션 로그아웃.
				}                                                                        

				model.addAttribute("message", message);
				return "login";    
			} else {                   // 그외의 경우에,
				memberService.updateMember(memberVO, addrVO);    // 파라미터들(id, pw 등)과 바인딩 되었던 MemberVO, AddrVO 객체를 가지고 회원정보 업데이트 실행
				return "redirect:/";   // 홈으로 이동
			}
		} else {                 // 비밀번호 불일치시,
			message = "<script>";
			message += " alert('비밀번호를 확인해주세요.');";
			message += " </script>";

			model.addAttribute("memberVo", memberVO);
			model.addAttribute("addrVo", addrVO);
			model.addAttribute("message", message);

			return "updateForm_member";
		}
	}

	@RequestMapping("/deleteMember.do/{id}")  // "/deleteMember?id=abc" 형식이 아닌, Rustful 형식의 url.
	public String deleteMember(@PathVariable("id") String id, HttpServletRequest request,  // Restful 형식의 url에서 파라미터를 가져오기 위한 @PathVariable 어노테이션
			HttpServletResponse response) {

		System.out.println(request.getServletPath());
	
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();  
		
		// 스프링 시큐리티를 이용하여 현재 세션의 사용자 정보 찾기
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);            // 현재 세션 로그아웃

		}
		memberService.deleteMember(id);    // 회원아이디, 장바구니, 이용내역 삭제
//		cartService.deleteCart(memberService.selectUserInfo(id).getCartId_mem());
//		historyService.deleteHistory(memberService.selectUserInfo(id).getCartId_mem());
		

		return "redirect:/";   // 홈으로 이동
		
	}

//---------------------여기까지 유저정보 수정--------------------------------------------------
	@RequestMapping(value = { "/findIdPwd", "/findId", "/findPwd" })   // login.jsp의 '아이디 또는 비밀번호 찾기' 버튼
	public String findIdPwd(HttpServletRequest request, Model model) {
		System.out.println(request.getServletPath());

		if (request.getServletPath().equals("/findPwd"))
			return "findPwd";
		else if (request.getServletPath().equals("/findId")) {
			return "findId";
		} else {
			return "findIdPwd";
		}
	}

	@RequestMapping(value = "/findPwd.do", method = RequestMethod.POST)  // findPwd_success 에서 넘어옴
	public String findPwdUpdate(HttpServletRequest request, Model model) {
		System.out.println(request.getServletPath());
		String id = request.getParameter("id");
		String password = request.getParameter("password");
		String password2 = request.getParameter("password2");
		String message = "";
		Map<String, Object> map = new HashMap<String, Object>();         // {키 : 값} 저장할 map 객체 생성
		
		System.out.println("비밀번호 1:" + password);
		System.out.println("비밀번호 2:" + password2);
		if (password.equals(password2)) {
			System.out.println("비밀번호 서로 같음");
			map.put("id", id);
			map.put("password", password);          // {"password"= 1234, "id"= abcd }
			memberService.updatePwdById(map);       // id와 일치하는 회원의 비밀번호 변경
			message = "<script>";
			message += " window.close();";
			message += " </script>";                // 창 종료
			model.addAttribute("message", message);
			return "findPwd";                       
		} else {
			System.out.println("비밀번호 서로 다름");
			message = "<script>";
			message += " alert('입력한 정보를 확인해주세요.');";
			message += " </script>";
			model.addAttribute("message", message);
			return "findPwd_success";
		}
	}

	@RequestMapping(value = { "/findId_chk", "/findPwd_chk" })     // findPwd, findId 에서 넘어옴
	public String findIdPwd_chk(HttpServletRequest request, Model model) {
		System.out.println(request.getServletPath());
		String message = "";
		if (request.getServletPath().equals("/findPwd_chk")) {     // 비밀번호 찾기 버튼
			String id = request.getParameter("id");
			String pwHint = request.getParameter("pwHint");
			String pwHintAns = request.getParameter("pwHintAns");
			System.out.println("pwhint: " + pwHintAns);
			MemberVO memberVO = memberService.selectUserInfo(id);   // id에 해당하는 회원의 모든 정보 가져옴
			if (pwHint.equals(memberVO.getPwHint()) && pwHintAns.equals(memberVO.getPwHintAns())) {   // 그 정보에서 가져온 비밀번호 힌트 질문과 답이 모두 일치하면,
				model.addAttribute("id", id);   // model 객체에 id를 담고
				return "findPwd_success";       // 비밀번호 변경 화면으로 이동 
			} else {                    // 일치하지 않으면, 
				message = "<script>";
				message += " alert('입력한 정보를 확인해주세요.');";
				message += " </script>";
				model.addAttribute("message", message);  // model 객체에 메세지를 담고 
				return "findPwd";                        // 다시 이전화면으로 이동 
			}

		} else if (request.getServletPath().equals("/findId_chk")) { // id 찾기 버튼
			String name = request.getParameter("name");
			String jumin = request.getParameter("jumin1") +"-" +request.getParameter("jumin2");
			System.out.println("id]: "+ name);
			System.out.println("id]: "+ jumin);
			Map<String, Object> map = new HashMap<String, Object>();    // {키 : 값} 저장할 map 객체 생성
			map.put("name", name);                                      // {"name" = 이름, "jumin"= 주민번호}
			map.put("jumin", jumin);
			MemberVO memberVO = memberService.selectUserNameInfo(map);  // 이름과 주민번호를 가지고 id 조회 
			
			if (memberVO != null) {                       // 조회한 결과가 존재하면, 
				System.out.println(memberVO.getId());
				model.addAttribute("memberVO", memberVO); // model 객체에 조회한 결과를 담고 
				return "findId_success";                  // 아이디를 알려주는 화면으로 이동
			}
			else {                   // 존재하지 않으면, 
				message = "<script>";
				message += " alert('입력한 정보를 확인해주세요.');";
				message += " </script>";
				model.addAttribute("message", message);
				
				return "findId";
			}
		} else {
			return "findIdPwd";
		}
	}
}

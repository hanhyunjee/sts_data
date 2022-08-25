package kr.ac.spring.common.controller;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import kr.ac.spring.common.service.CommonService;
import kr.ac.spring.common.vo.CommonVO;
import kr.ac.spring.common.vo.Pagination;


@Controller("commonController")
public class CommonController {

	private static final String COMMON_IMAGE_REPO = "resource/images/upload";
	@Autowired
	private CommonService commonService;
	//, method = { RequestMethod.GET, RequestMethod.POST }
	@RequestMapping(value = "/common/listcommons", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView listCommons(@RequestParam(value="page", required = false, defaultValue="1") int page, //page, range 기본값 1
			@RequestParam(value="range", required = false, defaultValue="1") int range, HttpServletRequest request, HttpServletResponse response, 
			Model model, @ModelAttribute("commonVO") CommonVO commonVO) throws Exception {  // url에서 넘어온 파라미터를 자동으로 바인딩 (commonVO 클래스 내에 있는 속성들과 파라미터들 결합)
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();   // 스프링 시큐리티를 이용한 현재세션의 유저 정보 조회

		String id = authentication.getName();
		
		//전체 게시글 갯수
		int listCnt = commonService.getCommonListCnt();  
		
		//Pagination 객체 생성
		Pagination pagination = new Pagination();
		pagination.pageInfo(page, range, listCnt);  
		
		//model.addAttribute("pagination",pagination);
		//model.addAttribute("boardList",boardService.listBoards(pagination));
		model.addAttribute("id",id);
		
		List<CommonVO> commonList = commonService.listCommons(pagination);
		ModelAndView mav = new ModelAndView("listcommons");
		mav.addObject("commonlist", commonList);
		mav.addObject("pagination", pagination);
		return mav;
	}

	@RequestMapping("/common/commonForm")
	public String commonForm(Model model) throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String id = authentication.getName();
		System.out.println("id chk  : " + id);
		model.addAttribute("memberId", id);
		return "commonForm";
	}
	
	@RequestMapping("/common/replyForm")
	public String replyForm(@ModelAttribute("commonVO") CommonVO commonVO, Model model, HttpServletRequest request) throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String id = authentication.getName();
		System.out.println("id chk  : " + id);
		model.addAttribute("memberId", id);
		model.addAttribute("originNo",request.getParameter("originNo"));
		model.addAttribute("groupOrd",request.getParameter("groupOrd"));
		model.addAttribute("groupLayer",request.getParameter("groupLayer"));
		return "replyForm";
	}

	@RequestMapping(value = "/common/addCommon")
	public ModelAndView addCommon(@ModelAttribute("commonVO") CommonVO commonVO, MultipartHttpServletRequest request,
			HttpServletResponse response, MultipartFile uploadFile) throws Exception {
		request.setCharacterEncoding("utf-8");
		
		Map<String, Object> map = new HashMap<String, Object>();
		Enumeration enu = request.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = request.getParameter(name);
			// System.out.println(name+", "+value);
			map.put(name, value);		// 키값이 아닌, 데이터를 이용한 정렬
		}
		String imageFileName = upload(request);
		map.put("imageFileName", imageFileName);
		if (imageFileName != null)
			commonVO.setImageFileName(imageFileName);
		else
			commonVO.setImageFileName(null);
		//System.out.println(request.getParameter("originNo"));
		//boardVO.setOriginNo(Integer.parseInt(request.getParameter("boardId")));
		commonVO.setGroupOrd(0);
		commonVO.setGroupLayer(0);
		ModelAndView mav = new ModelAndView("redirect:/common/listcommons");
		int result = 0;
//		if (uploadFile != null)
//			boardVO.setImageFileName(uploadFile.getOriginalFilename());
//		else
//			boardVO.setImageFileName(null);
		result = commonService.addCommon(commonVO);

		return mav;
	}
	
	@RequestMapping(value = "/common/replyCommon")
	public ModelAndView replyCommon(@ModelAttribute("commonVO") CommonVO commonVO, MultipartHttpServletRequest request,
			HttpServletResponse response, MultipartFile uploadFile) throws Exception {
		request.setCharacterEncoding("utf-8");
		String str = "";
		Map<String, Object> map = new HashMap<String, Object>();
		Enumeration enu = request.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = request.getParameter(name);
			// System.out.println(name+", "+value);
			map.put(name, value);
		}
		String imageFileName = upload(request);
		map.put("imageFileName", imageFileName);
		if (imageFileName != null)
			commonVO.setImageFileName(imageFileName);
		else
			commonVO.setImageFileName(null);
		System.out.println("zzz" + commonVO.getGroupOrd());
		commonVO.setOriginNo(Integer.parseInt(request.getParameter("originNo")));
		commonVO.setGroupOrd(Integer.parseInt(request.getParameter("groupOrd"))+1);
		commonVO.setGroupLayer(Integer.parseInt(request.getParameter("groupLayer"))+1);
		
		for(int i=0;i<commonVO.getGroupLayer();i++) {
			str += " ";
		}
		commonVO.setTitle(" RE : " + commonVO.getTitle());
		ModelAndView mav = new ModelAndView("redirect:/common/listcommons");
		int result = 0;
//		if (uploadFile != null)
//			boardVO.setImageFileName(uploadFile.getOriginalFilename());
//		else
//			boardVO.setImageFileName(null);
		result = commonService.replyCommon(commonVO);

		return mav;
	}

	@RequestMapping("/common/updateCommon")
	public ModelAndView updateCommon(@ModelAttribute("commonVO") CommonVO commonVO, MultipartHttpServletRequest request,
			HttpServletResponse response, MultipartFile uploadFile) throws Exception {
		request.setCharacterEncoding("utf-8");
		ModelAndView mav = new ModelAndView("redirect:/common/listcommons");
		int result = 0;
		Map<String, Object> map = new HashMap<String, Object>();
		Enumeration enu = request.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = request.getParameter(name);
			// System.out.println(name+", "+value);
			map.put(name, value);
		}
		//System.out.println("zzz " + request.getParameter("mod_file"));
		String imageFileName = upload(request);
		map.put("imageFileName", imageFileName);
		if (imageFileName != null)
			commonVO.setImageFileName(imageFileName);
		else
			commonVO.setImageFileName(null);
		//System.out.println(boardVO.getOriginNo());
		//boardVO.setTitle(request.getParameter("title"));
//		if(request.getParameter("mod_file") != null)
//			boardVO.setImageFileName(request.getParameter("mod_file"));
//		else
//			boardVO.setImageFileName(request.getParameter("uploadFile"));
		result = commonService.updateCommon(commonVO);

		return mav;
	}

	@RequestMapping(value = "/common/viewCommon", method = RequestMethod.GET)
	public ModelAndView viewCommon(@RequestParam("commonId") int commonId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Model model) throws Exception {
		// String viewName = (String)request.getAttribute("viewName");
		commonService.increaseViewCnt(commonId, session);
		Authentication authentivation = SecurityContextHolder.getContext().getAuthentication();
		String id = authentivation.getName();
		model.addAttribute("id",id);
		CommonVO commonVO = commonService.viewCommon(commonId);
		ModelAndView mav = new ModelAndView("viewCommon");
		mav.addObject("commonVO", commonVO);
		return mav;
	}

	@RequestMapping("/common/deleteCommon")
	public String deleteCommon(@RequestParam("originNo") int originNo, @RequestParam("groupLayer") int groupLayer, Model model) throws Exception {
		int result = 0;
		Map<String, Object> parameters = new HashMap<String, Object>();
	    parameters.put("originNo", originNo);
	    parameters.put("groupLayer", groupLayer);
		result = commonService.deleteCommon(parameters);
		return "redirect:/common/listcommons";
	}


	private String upload(MultipartHttpServletRequest multipartRequest) throws Exception {
		String imageFileName = null;
		Iterator<String> fileNames = multipartRequest.getFileNames();
		String path = multipartRequest.getSession().getServletContext().getRealPath(COMMON_IMAGE_REPO);
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
	
	private String delete(MultipartHttpServletRequest multipartRequest, String deleteFileName) throws Exception {
		String imageFileName = null;
		Iterator<String> fileNames = multipartRequest.getFileNames();
		String path = multipartRequest.getSession().getServletContext().getRealPath(COMMON_IMAGE_REPO);
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
	
	@RequestMapping(value = "/common/{id}", method = { RequestMethod.GET, RequestMethod.POST })	// 회원정보에서 문의내역으로 넘어가는 경로
	public ModelAndView listCommonById(@RequestParam(value="page", required = false, defaultValue="1") int page,
			@RequestParam(value="range", required = false, defaultValue="1") int range, HttpServletRequest request,
			HttpServletResponse response, Model model, @ModelAttribute("commonVO") CommonVO commonVO, @PathVariable("id") String id) throws Exception {
	
		//전체 게시글 갯수
		int listCnt = commonService.getCommonListCntById(id);
		
		//Pagination 객체 생성
		Pagination pagination = new Pagination();
		pagination.pageInfo(page, range, listCnt);
		
		model.addAttribute("id",id);
		
		List<CommonVO> commonList = commonService.listCommonById(pagination,id);
		ModelAndView mav = new ModelAndView("listcommonById");
		mav.addObject("commonlist", commonList);
		mav.addObject("pagination", pagination);
		return mav;
	}
}
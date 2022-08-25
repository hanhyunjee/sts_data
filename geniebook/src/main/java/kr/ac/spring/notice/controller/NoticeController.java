package kr.ac.spring.notice.controller;

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


import kr.ac.spring.notice.service.NoticeService;
import kr.ac.spring.notice.vo.NoticeVO;
import kr.ac.spring.notice.vo.Pagination;

@Controller("noticeController")
public class NoticeController {

	private static final String BOARD_IMAGE_REPO = "resource/images/upload";
	@Autowired
	private NoticeService noticeService;
	//, method = { RequestMethod.GET, RequestMethod.POST }
	@RequestMapping(value = "/notice/listnotices", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView listNotices(@RequestParam(value="page", required = false, defaultValue="1") int page, 
			@RequestParam(value="range", required = false, defaultValue="1") int range, HttpServletRequest request, HttpServletResponse response, Model model, 
			@ModelAttribute("noticeVO") NoticeVO noticeVO) throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String id = authentication.getName();
		
		//전체 게시글 갯수
		int listCnt = noticeService.getNoticeListCnt();
		
		//Pagination 객체 생성
		Pagination pagination = new Pagination();
		pagination.pageInfo(page, range, listCnt);
		
		//model.addAttribute("pagination",pagination);
		//model.addAttribute("boardList",boardService.listBoards(pagination));
		model.addAttribute("id",id);
		
		List<NoticeVO> noticeList = noticeService.listNotices(pagination);
		ModelAndView mav = new ModelAndView("listnotices");
		mav.addObject("noticelist", noticeList);
		mav.addObject("pagination", pagination);
		return mav;
	}

	@RequestMapping("/notice/noticeForm")
	public String noticeForm(Model model) throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String id = authentication.getName();
		System.out.println("id chk  : " + id);
		model.addAttribute("adminId", id);
		return "noticeForm";
	}
	
	
	@RequestMapping(value = "/notice/addNotice")
	public ModelAndView addNotice(@ModelAttribute("noticeVO") NoticeVO noticeVO, MultipartHttpServletRequest request,
			HttpServletResponse response, MultipartFile uploadFile) throws Exception {
		request.setCharacterEncoding("utf-8");
		
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
			noticeVO.setImageFileName(imageFileName);
		else
			noticeVO.setImageFileName(null);
		//System.out.println(request.getParameter("originNo"));
		//boardVO.setOriginNo(Integer.parseInt(request.getParameter("boardId")));
//		boardVO.setGroupOrd(0);
//		boardVO.setGroupLayer(0);
		ModelAndView mav = new ModelAndView("redirect:/notice/listnotices");
		int result = 0;
//		if (uploadFile != null)
//			boardVO.setImageFileName(uploadFile.getOriginalFilename());
//		else
//			boardVO.setImageFileName(null);
		result = noticeService.addNotice(noticeVO);

		return mav;
	}
	
	
	@RequestMapping("/notice/updateNotice")
	public ModelAndView updateNotice(@ModelAttribute("noticeVO") NoticeVO noticeVO, MultipartHttpServletRequest request,
			HttpServletResponse response, MultipartFile uploadFile) throws Exception {
		request.setCharacterEncoding("utf-8");
		ModelAndView mav = new ModelAndView("redirect:/notice/listnotices");
		int result = 0;
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
			noticeVO.setImageFileName(imageFileName);
		else
			noticeVO.setImageFileName(null);
		//System.out.println(boardVO.getOriginNo());
		//boardVO.setTitle(request.getParameter("title"));
//		if(request.getParameter("mod_file") != null)
//			boardVO.setImageFileName(request.getParameter("mod_file"));
//		else
//			boardVO.setImageFileName(request.getParameter("uploadFile"));
		result = noticeService.updateNotice(noticeVO);

		return mav;
	}

	@RequestMapping(value = "/notice/viewNotice", method = RequestMethod.GET)
	public ModelAndView viewNotice(@RequestParam("noticeId") int noticeId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Model model) throws Exception {
		// String viewName = (String)request.getAttribute("viewName");
		noticeService.increaseViewCnt(noticeId, session);
		Authentication authentivation = SecurityContextHolder.getContext().getAuthentication();
		String id = authentivation.getName();
		model.addAttribute("id",id);
		NoticeVO noticeVO = noticeService.viewNotice(noticeId);
		ModelAndView mav = new ModelAndView("viewNotice");
		mav.addObject("noticeVO", noticeVO);
		return mav;
	}

	@RequestMapping("/notice/deleteNotice")
	public String deleteNotice(@RequestParam("originNo") int originNo, Model model) throws Exception {
		int result = 0;
		Map<String, Object> parameters = new HashMap<String, Object>();
	    parameters.put("originNo", originNo);
//	    parameters.put("groupLayer", groupLayer);
		result = noticeService.deleteNotice(parameters);
		return "redirect:/notice/listnotices";
	}


	private String upload(MultipartHttpServletRequest multipartRequest) throws Exception {
		String imageFileName = null;
		Iterator<String> fileNames = multipartRequest.getFileNames();
		String path = multipartRequest.getSession().getServletContext().getRealPath(BOARD_IMAGE_REPO);
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
		String path = multipartRequest.getSession().getServletContext().getRealPath(BOARD_IMAGE_REPO);
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
	
	@RequestMapping(value = "/notice/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView listNoticeById(@RequestParam(value="page", required = false, defaultValue="1") int page,
			@RequestParam(value="range", required = false, defaultValue="1") int range, HttpServletRequest request,
			HttpServletResponse response, Model model, @ModelAttribute("noticeVO") NoticeVO noticeVO, @PathVariable("id") String id) throws Exception {
	
		//전체 게시글 갯수
		int listCnt = noticeService.getNoticeListCntById(id);
		
		//Pagination 객체 생성
		Pagination pagination = new Pagination();
		pagination.pageInfo(page, range, listCnt);
		
		model.addAttribute("id",id);
		
		List<NoticeVO> noticeList = noticeService.listNoticeById(pagination,id);
		ModelAndView mav = new ModelAndView("listnoticeById");
		mav.addObject("noticelist", noticeList);
		mav.addObject("pagination", pagination);
		return mav;
	}
}
package kr.ac.spring.notice.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import kr.ac.spring.notice.dao.NoticeDAO;
import kr.ac.spring.notice.vo.NoticeVO;
import kr.ac.spring.notice.vo.Pagination;

@Service("noticeService")
public class NoticeService {
	@Autowired
	NoticeDAO noticeDAO;
	
	public List<NoticeVO> listNotices(Pagination pagination) throws Exception{
		List<NoticeVO> noticesList =  noticeDAO.selectAllNoticeList(pagination);
        return noticesList;
	}

	public int addNotice(NoticeVO noticeVO) throws Exception{
		return noticeDAO.addNotice(noticeVO);
	}

	public int updateNotice(NoticeVO noticeVO) throws Exception {
		return noticeDAO.updateNotice(noticeVO);
	}

	public NoticeVO viewNotice(int noticeId) throws Exception{
		return noticeDAO.viewNotice(noticeId);
	}

	public int deleteNotice(Map<String, Object> parameters) throws Exception{
		return noticeDAO.deleteNotice(parameters);
	}
	
	public int getNoticeListCnt() throws Exception{
		return noticeDAO.getNoticeListCnt();
	}
	
	public void increaseViewCnt(int noticeId, HttpSession session) throws Exception{
		long update_time = 0;
		// 세션에 저장된 조회시간 검색
		//최초로 조회할 경우 세션에 저장된 값이 없기 때문에 if문은 실행 되지 않는다.
		if(session.getAttribute("update_time_" + noticeId) != null)
			update_time = (long)session.getAttribute("update_time_" + noticeId);
		//시스템의 현재 시간을 current_time에 저장 
		long current_time = System.currentTimeMillis();
		//일정 시간이 경과 후 조회 수 증가 처리 24*60*60*1000 (24시간)
		//시스템 현재시간 - 열람 시간 > 일정 시간 (조회수 증가가 가능하도록 지정한 시간)
		if(current_time - update_time > 5 * 1000) {
			noticeDAO.increaseViewCnt(noticeId);
			//세션에 시간을 저장
			session.setAttribute("update_time_"+noticeId, current_time);
		}
	}


	public int getNoticeListCntById(String id) {
		// TODO Auto-generated method stub
		return noticeDAO.getNoticeListCntById(id);
	}

	public List<NoticeVO> listNoticeById(Pagination pagination, String id) {
		// TODO Auto-generated method stub
		List<NoticeVO> noticesList =  noticeDAO.selectAllNoticeListById(pagination, id);
        return noticesList;
	}
	
}

package kr.ac.spring.common.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.ac.spring.common.dao.CommonDAO;
import kr.ac.spring.common.vo.CommonVO;
import kr.ac.spring.common.vo.Pagination;


@Service("commonService")
public class CommonService {
	@Autowired
	CommonDAO commonDAO;
	
	public List<CommonVO> listCommons(Pagination pagination) throws Exception{		// 모든 회원들의 글
		List<CommonVO> commonsList =  commonDAO.selectAllCommonList(pagination);
        return commonsList;
	}

	public int addCommon(CommonVO commonVO) throws Exception{
		return commonDAO.addCommon(commonVO);
	}

	public int updateCommon(CommonVO commonVO) throws Exception {
		return commonDAO.updateCommon(commonVO);
	}

	public CommonVO viewCommon(int commonId) throws Exception{
		return commonDAO.viewCommon(commonId);
	}

	public int deleteCommon(Map<String, Object> parameters) throws Exception{
		return commonDAO.deleteCommon(parameters);
	}
	
	public int getCommonListCnt() throws Exception{
		return commonDAO.getCommonListCnt();
	}
	
	public void increaseViewCnt(int commonId, HttpSession session) throws Exception{
		long update_time = 0;
		// 세션에 저장된 조회시간 검색
		//최초로 조회할 경우 세션에 저장된 값이 없기 때문에 if문은 실행 되지 않는다.
		if(session.getAttribute("update_time_" + commonId) != null)
			update_time = (long)session.getAttribute("update_time_" + commonId);
		//시스템의 현재 시간을 current_time에 저장 
		long current_time = System.currentTimeMillis();
		//일정 시간이 경과 후 조회 수 증가 처리 24*60*60*1000 (24시간)
		//시스템 현재시간 - 열람 시간 > 일정 시간 (조회수 증가가 가능하도록 지정한 시간)
		if(current_time - update_time > 5 * 1000) {
			commonDAO.increaseViewCnt(commonId);
			//세션에 시간을 저장
			session.setAttribute("update_time_"+commonId, current_time);
		}
	}

	public int replyCommon(CommonVO commonVO) {
		return commonDAO.replyCommon(commonVO);
	}

	public int getCommonListCntById(String id) {

		return commonDAO.getCommonListCntById(id);
		
	}

	public List<CommonVO> listCommonById(Pagination pagination, String id) { 		// 회원정보에서 문의내역
		
		List<CommonVO> commonsList =  commonDAO.selectAllCommonListById(pagination, id);
        return commonsList;
	}
}

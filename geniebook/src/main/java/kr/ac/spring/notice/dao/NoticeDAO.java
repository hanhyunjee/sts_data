package kr.ac.spring.notice.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import kr.ac.spring.notice.vo.NoticeVO;
import kr.ac.spring.notice.vo.Pagination;

@Repository("noticeDAO")
public class NoticeDAO {
	@Autowired
	private SqlSession sqlSession;

	public List<NoticeVO> selectAllNoticeList(Pagination pagination) throws DataAccessException {
		List<NoticeVO> NoticesList =  sqlSession.selectList("mapper.notice.selectAllNoticeList",pagination);
		return NoticesList;
	}

	public int addNotice(NoticeVO noticeVO) throws DataAccessException{
		return sqlSession.insert("mapper.notice.addNotice",noticeVO);
	}

	public int updateNotice(NoticeVO noticeVO) throws DataAccessException{
		return sqlSession.update("mapper.notice.updateNotice",noticeVO);
	}
	
	public NoticeVO viewNotice(int noticeId) throws DataAccessException{
		return sqlSession.selectOne("mapper.notice.selectByNoticeId",noticeId);
	}

	public int deleteNotice(Map<String, Object> parameters) throws DataAccessException{
		return sqlSession.delete("mapper.notice.deleteNotice",parameters);
	}

	public void increaseViewCnt(int noticeId) throws DataAccessException{
		sqlSession.update("mapper.notice.increaseViewCnt",noticeId);
	}
	
	public int getNoticeListCnt() throws DataAccessException{
		return sqlSession.selectOne("mapper.notice.getNoticeListCnt");
	}

	public int getNoticeListCntById(String id) {
		// TODO Auto-generated method stub
		return sqlSession.selectOne("mapper.notice.getNoticeListCntById",id);
	}

	public List<NoticeVO> selectAllNoticeListById(Pagination pagination, String id) throws DataAccessException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pagination", pagination);
		map.put("id", id);
		
		List<NoticeVO> NoticesList =  sqlSession.selectList("mapper.notice.selectAllNoticeListById",map);
		return NoticesList;
	}
}

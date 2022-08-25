package kr.ac.spring.common.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import kr.ac.spring.common.vo.CommonVO;
import kr.ac.spring.common.vo.Pagination;

@Repository("commonDAO")
public class CommonDAO {
	@Autowired
	private SqlSession sqlSession;

	public List<CommonVO> selectAllCommonList(Pagination pagination) throws DataAccessException {
		List<CommonVO> CommonsList =  sqlSession.selectList("mapper.common.selectAllCommonList",pagination);
		return CommonsList;
	}

	public int addCommon(CommonVO commonVO) throws DataAccessException{
		return sqlSession.insert("mapper.common.addCommon",commonVO);
	}

	public int updateCommon(CommonVO commonVO) throws DataAccessException{
		return sqlSession.update("mapper.common.updateCommon",commonVO);
	}
	
	public CommonVO viewCommon(int commonId) throws DataAccessException{
		return sqlSession.selectOne("mapper.common.selectByCommonId",commonId);
	}

	public int deleteCommon(Map<String, Object> parameters) throws DataAccessException{
		return sqlSession.delete("mapper.common.deleteCommon",parameters);
	}

	public void increaseViewCnt(int commonId) throws DataAccessException{
		sqlSession.update("mapper.common.increaseViewCnt",commonId);
	}

	public int replyCommon(CommonVO commonVO) throws DataAccessException{
		return sqlSession.insert("mapper.common.replyCommon",commonVO);
	}
	
	public int getCommonListCnt() throws DataAccessException{
		return sqlSession.selectOne("mapper.common.getCommonListCnt");
	}

	public int getCommonListCntById(String id) {
		// TODO Auto-generated method stub
		return sqlSession.selectOne("mapper.common.getCommonListCntById",id);
	}

	public List<CommonVO> selectAllCommonListById(Pagination pagination, String id) throws DataAccessException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pagination", pagination);
		map.put("id", id);
		
		List<CommonVO> CommonsList =  sqlSession.selectList("mapper.common.selectAllCommonListById",map);
		return CommonsList;
	}
}

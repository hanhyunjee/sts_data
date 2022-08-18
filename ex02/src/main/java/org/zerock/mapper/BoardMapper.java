package org.zerock.mapper;

import java.util.List;

import org.zerock.domain.BoardVO;

public interface BoardMapper {

//    public int getTotalCount(Criteria cri);

    //@Select("select * from tbl_board where bno > 0")
    public List<BoardVO> getList();

//    public List<BoardVO> getListWithPaging(Criteria cri);

    public void insert(BoardVO board);

    public void insertSelectKey(BoardVO board);

    public BoardVO read(Long bno);

    public int delete(Long bno);

    public int update(BoardVO board);
}
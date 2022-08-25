package kr.ac.spring.common.vo;

public class Pagination {
    
	private int listSize = 10;                //초기값으로 목록개수를 10으로 셋팅

	private int rangeSize = 10;            //초기값으로 페이지범위를 10으로 셋팅

	private int page;

	private int range;

	private int listCnt;

	private int pageCnt;

	private int startPage;

	private int startList;
	
	private int endList;

	private int endPage;

	private boolean prev;  // true면 prev버튼

	private boolean next;  // ture면 next버튼

	

	public int getRangeSize() {

		return rangeSize;

	}



	public int getPage() {

		return page;

	}



	public void setPage(int page) {

		this.page = page;

	}



	public int getRange() {

		return range;

	}



	public void setRange(int range) {

		this.range = range;

	}



	public int getStartPage() {

		return startPage;

	}



	public void setStartPage(int startPage) {

		this.startPage = startPage;

	}



	public int getEndPage() {

		return endPage;

	}



	public void setEndPage(int endPage) {

		this.endPage = endPage;

	}



	public boolean isPrev() {

		return prev;

	}



	public void setPrev(boolean prev) {

		this.prev = prev;

	}



	public boolean isNext() {

		return next;

	}



	public void setNext(boolean next) {

		this.next = next;

	}



	public int getListSize() {

		return listSize;

	}



	public void setListSize(int listSize) {

		this.listSize = listSize;

	}

	

	public int getListCnt() {

		return listCnt;

	}



	public void setListCnt(int listCnt) {

		this.listCnt = listCnt;

	}

	

	public int getStartList() {

		return startList;

	}

	public void pageInfo(int page, int range, int listCnt) {

		this.page = page;

		this.range = range;

		this.listCnt = listCnt;

		
		// listSize : 한 페이지에서 보이는 글 개수 = 10 
		// rangesize : 글목록 번호 최대 개수 = 10 ( 초과되면 next 버튼생김)
		//전체 페이지수. 전체글수를 10으로 나눈결과를 올림함(정수)

		this.pageCnt = (int) Math.ceil((double)listCnt/listSize);

		

		//시작 페이지. range가 1이면 시작페이지 = 1
		// range가 2이면 시작페이지 = 11
		this.startPage = (range - 1) * rangeSize + 1 ;

		

		//끝 페이지. range가 1이면 끝페이지 = 10
		// range가 2이면 끝페이지 = 20
		this.endPage = range * rangeSize;

				

		//게시판 시작번호. 1페이지일때 0, 2페이지일때 10번부터 시작
		this.startList = (page - 1) * listSize;

		//게시판 끝 번호. 1페이지일때 10, 2페이지일때 20번이 끝번호
		this.endList = startList + rangeSize;
	
		//이전 버튼 상태. 
		this.prev = range == 1 ? false : true;

		

		//다음 버튼 상태

		this.next = endPage > pageCnt ? false : true;

		if (this.endPage > this.pageCnt) {

			this.endPage = this.pageCnt;

			this.next = false;

		}

	}


}
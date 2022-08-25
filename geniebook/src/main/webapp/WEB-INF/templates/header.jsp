<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript">


/* var loopSearch=true;
function keywordSearch(){
	if(loopSearch==false)
		return;
 var value=document.frmSearch.searchWord.value;
	$.ajax({
		type : "get",
		async : true, //false인 경우 동기식으로 처리한다.
		url : "${contextPath}/keywordSearch/",
		data : {keyword:value},
		success : function(data, textStatus) {
		    var jsonInfo = JSON.parse(data);
			displayResult(jsonInfo);
		},
		error : function(data, textStatus) {
			alert("에러가 발생했습니다."+data);
		},
		complete : function(data, textStatus) {
			//alert("작업을완료 했습니다");
			
		}
	}); //end ajax	
} */

/* function displayResult(jsonInfo){
	var count = jsonInfo.keyword.length;
	if(count > 0) {
	    var html = '';
	    for(var i in jsonInfo.keyword){
		   html += "<a href=\"javascript:select('"+jsonInfo.keyword[i]+"')\">"+jsonInfo.keyword[i]+"</a><br/>";
	    }
	    var listView = document.getElementById("suggestList");
	    listView.innerHTML = html;
	    show('suggest');
	}else{
	    hide('suggest');
	} 
}

function select(selectedKeyword) {
	 document.frmSearch.searchWord.value=selectedKeyword;
	 loopSearch = false;
	 hide('suggest');
}
	
function show(elementId) {
	 var element = document.getElementById(elementId);
	 if(element) {
	  element.style.display = 'block';
	 }
	}

function hide(elementId){
   var element = document.getElementById(elementId);
   if(element){
	  element.style.display = 'none';
   }
} */







 function bookNo4() {
		location.href = "${contextPath}/productDetail?bookNo=4";
}
 function bookNo7() {
		location.href = "${contextPath}/productDetail?bookNo=7";
}
 function bookNo20() {
		location.href = "${contextPath}/productDetail?bookNo=20";
}

</script>
<header id="header"><!--header-->
		
		
		<div class="header-middle"><!--header-middle-->
			<div class="container">
				<div class="row">
					<div class="col-sm-4">
						<div class="logo pull-left">
							<a href="<c:url value="/"/>"><img width="400" height="" src="<c:url value="/resources/images/home/GenieBook.png"/>" alt="" /></a>
						</div>
						
					</div>
					<div class="col-sm-8">
						<div class="shop-menu pull-right">
							<ul class="nav navbar-nav">
								<c:if test="${pageContext.request.userPrincipal.name != null}">
									<c:if test="${pageContext.request.userPrincipal.name == 'admin'}">
										<li><a class="nav-link" href="<c:url value="/memberInfo_ui"/> "><i class="fa fa-user"></i>관리자정보</a></li>
									</c:if>
					
									<c:if test="${pageContext.request.userPrincipal.name != 'admin'}">
										<li><a class="nav-link" href="<c:url value="/memberInfo_ui"/> "><i class="fa fa-user"></i>회원정보</a></li>
									</c:if>
								</c:if>
								<c:if test="${pageContext.request.userPrincipal.name != null}">
									<c:if test="${pageContext.request.userPrincipal.name == 'admin'}">
										<li class="nav-item"><a class="nav-link" href="<c:url value="/admin/adminManager"/> "><i class="fa fa-gears"></i>관리자기능</a></li>
									</c:if>
					
									<c:if test="${pageContext.request.userPrincipal.name != 'admin'}">
										<li class="nav-item"><a class="nav-link" href="<c:url value="/cart"/> "><i class="fa fa-shopping-cart"></i>장바구니</a></li>
									</c:if>
					
									<li class="nav-item"><a class="nav-link" href="javascript:document.getElementById('logout').submit()"><i class="fa fa-unlock"></i>로그아웃</a>
									</li>
									<form id="logout" action="<c:url value="/logout" />" method="post">
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
									</form>
								</c:if>
							<c:if test="${pageContext.request.userPrincipal.name == null}">
								<li class="nav-item"> <a class="nav-link" href="<c:url value="/login"/> "><i class="fa fa-lock"></i>로그인</a>
								</li>
								<li class="nav-item"> <a class="nav-link" href="<c:url value="/registerForm_member"/> "><i class="fa fa-linkedin-square"></i>회원가입</a>
								</li>
							</c:if>
							</ul>
						</div>
					</div>
				</div>
			</div>
		</div><!--/header-middle-->
	
		<div class="header-bottom"><!--header-bottom-->
			<div class="container">
				<div class="row">
					<div class="col-sm-9">
						<div class="navbar-header">
							<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
								<span class="sr-only">Toggle navigation</span>
								<span class="icon-bar"></span>
								<span class="icon-bar"></span>
								<span class="icon-bar"></span>
							</button>
						</div>
						<div class="mainmenu pull-left">
							<ul class="nav navbar-nav collapse navbar-collapse">
								<li><a href="<c:url value="/"/>" class="active"><i class="fa fa-home"></i>Home</a></li>
								<li class="dropdown"><a href="<c:url value="/notice/listnotices"/>"><i class="fa fa-bullhorn"></i>공지사항</a></li> <!-- 공지사항 추가 -->
								<li class="dropdown"><a href="<c:url value="/productAll"/>"><i class="fa fa-book"></i>Books<!-- <i class="fa fa-angle-down"></i> --></a>
								<li class="dropdown"><a href="<c:url value="/board/listboards"/>"><i class="fa fa-question"></i>Q&A</a>
								<li class="dropdown"><a href="<c:url value="/common/listcommons"/>"><i class="fa fa-comment-o"></i>커뮤니티</a> <!-- 커뮤니티 추가 -->
                            	 <c:if test="${pageContext.request.userPrincipal.name == 'admin'}">
									<li class="dropdown"><a	href="<c:url value="/admin/adminManager"/>"><i class="fa fa-user"></i>회원관리</a>
								</c:if>
						
							</ul>
						</div>
					</div><!-- 검색기능 -->
					<div>
					<form name="frmSearch" action="${formAction}" method="get">
						<select id="searchType" name="searchType" style = "width : 100px; height : 30px; margin-right : 0px; ">
							<option value="">검색조건</option>
							<option value="${contextPath}/searchProductbyTitle">책제목</option> 
							<option value="${contextPath}/searchProductbyWriter">작가</option>
						</select>
						<input name="searchWord" class="search_box" type="text" style = "margin-right : 0px;" > 
						<input type="submit" name="search" class="btn btn-default get"  value="검 색" onClick="searchType()" style = "margin-left : 1090px;" >
					</form> 
					 <script src="http://code.jquery.com/jquery-latest.min.js"></script>
					<script>
					    $(function() {
					        function searchType()
					        {
					            const formAction = $('#searchType').val();
					            if (!formAction || formAction == "") {
					                alert('옵션을 선택하세요!');
					                return false;
					            }
					
					            $('form[name=frmSearch]').attr('action', formAction);
					            $('form[name=frmSearch]').submit();
					            console.log(formAction);
					        }
					        $('input[name=search]').on('click', searchType);
					    });
					</script> <!-- 자동완성 미구현 -->
						<%-- <form name="frmSearch" action="${contextPath }/searchProductbyTitle" method="get">
						<select id="searchType" name="searchType" style = "width : 100px; height : 30px;">
							<option value="">검색조건</option>
							<option value="title">책제목</option> 
							<option value="writer">작가</option>
						</select>
						<input name="searchWord" class="main_input" type="text"  onClick="keywordSearch()"> 
						<input type="submit" name="search" class="btn1"  value="검 색">
					</form> --%>
					</div><!-- 자동완성 -->
					<div id="suggest">
       					 <div id="suggestList"></div>
   					</div>
				</div><!-- 검색기능 끝 -->
			</div>
		</div><!--/header-bottom-->
	</header><!--/header-->
	
 	<section id="slider"><!--slider-->
		<div class="container">
			<div class="row">
				<div class="col-sm-12">
					<div id="slider-carousel" class="carousel slide" data-ride="carousel">
						<ol class="carousel-indicators">
							<li data-target="#slider-carousel" data-slide-to="0" class="active"></li>
							<li data-target="#slider-carousel" data-slide-to="1"></li>
							<li data-target="#slider-carousel" data-slide-to="2"></li>
						</ol>
						
						<div class="carousel-inner">
							<div class="item active">
								<div class="col-sm-6">
									<h1><span>작은아씨들</span></h1>
									<h2>루이자 메이 알코트</h2>
									<p>성격도 생김새도 완전히 다른 네 자매, 메그, 조, 베스, 에이미. <br>소설 역사상 가장 사랑받은 자매들의 이야기를 968쪽의 디럭스 양장본으로 만나보세요!</p>
									<button type="button" class="btn btn-default get" onclick="bookNo4()">상세보기</button>
								</div>
								<div class="col-sm-6">
									<img width="282" height="400" src="<c:url value="/resources/images/home/Book_1.jpg"/>" class="girl img-responsive" alt="" />
									<img src="<c:url value="/resources/images/home/pricing1.png"/>"  class="pricing" alt="" />
								</div>
							</div>
							<div class="item">
								<div class="col-sm-6">
									<h1><span>윤동주 전 시집</span></h1>
									<h2>윤동주</h2>
									<p>한국인이 사랑하는 시인 1위, 독립운동가 3위에 랭크된 시인,<br>하늘과 바람과 별을 노래한 서정시와 저항과 성찰의 민족시인.<br>윤동주 탄생 100주년 기념 윤동주 전체 작품 124편을 다 담았습니다. </p>
									<button type="button" class="btn btn-default get" onclick="bookNo7()">상세보기</button>
								</div>
								<div class="col-sm-6">
									<img width="260" height="400" src="<c:url value="/resources/images/home/Book_2.jpg"/>" class="girl img-responsive" alt="" />
									<img src="<c:url value="/resources/images/home/pricing2.png"/>"  class="pricing" alt="" />
								</div>
							</div>
							
							<div class="item">
								<div class="col-sm-6">
									<h1><span>말의 품격</span></h1>
									<h2>이기주</h2>
									<p>말과 사람과 품격에 대한 이야기.<br>《언어의 온도》로 많은 독자의 공감을 얻은 이기주 작가의 새로운 에세이집을 만나보세요!</p>
									<button type="button" class="btn btn-default get" onclick="bookNo20()">상세보기</button>
								</div>
								<div class="col-sm-6">
									<img width="290" height="400" src="<c:url value="/resources/images/home/Book_3.jpg"/>" class="girl img-responsive" alt="" />
									<img src="<c:url value="/resources/images/home/pricing3.png"/>" class="pricing" alt="" />
								</div>
							</div>
							
						</div>
						
						<a href="#slider-carousel" class="left control-carousel hidden-xs" data-slide="prev">
							<i class="fa fa-angle-left"></i>
						</a>
						<a href="#slider-carousel" class="right control-carousel hidden-xs" data-slide="next">
							<i class="fa fa-angle-right"></i>
						</a>
					</div>
					
				</div>
			</div>
		</div>
	</section><!--/slider-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%@include file="../includes/header.jsp"%>

<div class="row">
	<div class="col-lg-12">
		<h1 class="page-header">Tables</h1>
	</div>
	<!-- /.col-lg-12 -->
</div>
<!-- /.row -->
<div class="row">
	<div class="col-lg-12">
		<div class="panel panel-default">
			<div class="panel-heading">
				Board List Page
				<button id="regBtn" type="button" class="btn btn-xs pull-right">Register
					New Board</button>
			</div>
			<!-- /.panel-heading -->
			<div class="panel-body">
				<table width="100%"
					class="table table-striped table-bordered table-hover">
					<thead>
						<tr>
							<th>번호</th>
							<th>제목</th>
							<th>작성자(s)</th>
							<th>작성일</th>
							<th>수정일</th>
						</tr>
					</thead>
					<c:forEach items="${list}" var="board">
						<tr>
							<td><c:out value="${board.bno}" /></td>

							<td><a class='move' href='<c:out value="${board.bno}"/>' />
								<c:out value="${board.title}" /></a></td>

							<td><c:out value="${board.writer}" /></td>
							<td><fmt:formatDate pattern="yyyy-MM-dd"
									value="${board.regdate}" /></td>
							<td><fmt:formatDate pattern="yyyy-MM-dd"
									value="${board.updateDate}" /></td>
						</tr>
					</c:forEach>
				</table>
				<!-- 테이블 종료 -->

				<!-- 페이지 처리 Start -->
				<%--    <h3>${pageMaker}</h3> --%>
				<div class="pull-right">
					<ul class="pagination">

						<c:if test="${pageMaker.prev }">
							<li class="page-item"><a class="page-link"
								href="${pageMaker.startPage-1}" tabindex="-1">Previous</a></li>
						</c:if>

						<c:forEach begin="${pageMaker.startPage }"
							end="${pageMaker.endPage }" var="num">
							<li class="paginate_item  ${pageMaker.cri.pageNum == num ? "active" : "" }">
								<a class="page-link" href="${num}">${num}</a>
							</li>
						</c:forEach>

						<c:if test="${pageMaker.next }">
							<li class="page-item"><a class="page-link"
								href="${pageMaker.endPage+1}" tabindex="-1">Next</a></li>
						</c:if>
					</ul>
				</div>
				<form id='actionForm' action="/board/list" method='get'>
					<input type="hidden" name="pageNum"
						value="<c:out value='${pageMaker.cri.pageNum}'/>"> <input
						type="hidden" name="amount"
						value="<c:out value='${pageMaker.cri.amount}'/>">
				</form>
				<!-- 페이지 처리 End -->

				<!-- 모달 추가 -->
				<div id="myModal" class="modal" tabindex="-1" role="dialog">
					<div class="modal-dialog" role="document">
						<div class="modal-content">
							<div class="modal-header">
								<h5 class="modal-title">Modal title</h5>
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
							</div>
							<div class="modal-body">
								<p>Modal body text goes here.</p>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-primary">Save
									changes</button>
								<button type="button" class="btn btn-secondary"
									data-dismiss="modal">Close</button>
							</div>
						</div>
					</div>
				</div>
				<script>
            $(document).ready(function(){
            	
               var result = '<c:out value = "${result}"/>';
               
               checkModal(result);
            
               /* stack 구조로 동작하는 window.history 객체 for 브라우저의 세션 기록 조작 */
                history.replaceState({}, null, null);
               
                function checkModal(result){
                    if (result == '' || history.state) {
                        return;
                    }
                    
                    if(result==='success'){
                       $(".modal-body").html(
                           "정상적으로 처리되었습니다."); // 삭제용
                    }else if(parseInt(result) > 0){
                     $(".modal-body").html(
                        "게시물 " + parseInt(result) + " 번이 등록되었습니다.");
                  }
                  $("#myModal").modal("show");
               }
               
                   $("#regBtn").on("click", function(){
                          self.location = "/board/register";
                      });
                   
                   var actionForm = $("#actionForm");
                   
                   $(".page-link").on("click", function(e){
                	   
                      e.preventDefault();
                      
                      var targetPage = $(this).attr("href");
                      
                      console.log(targetPage);
                      
                  //  console.log('click');
                      
                      actionForm.find("input[name='pageNum']").val($(this).attr("href"));
                      
                      actionForm.submit();
                      
                   });
                   
                   
                   $(".move").on("click", function(e){
                	   e.preventDefault();
  						var targetBno = $(this).attr("href")
                       
                       colsole.log(targetBno);
  						
  						actionForm.append("<input type='hidden' name='bno' value='" + 
  	                             
  		                       $(this).attr("href") + "'>");
  		                       
  		                       actionForm.attr("action", "/board/get");
  		                       
  		                       actionForm.submit();

                   })
                
            });
            </script>

				<!-- /.panel-body -->
			</div>
			<!-- /.panel -->
		</div>
	</div>
	<!-- /.row -->

	<script type="text/javascript">
       $(document).ready(function(){
       var result = '<c:out value="${result}"/>'; 
    });
   </script>

	<%@include file="../includes/footer.jsp"%>
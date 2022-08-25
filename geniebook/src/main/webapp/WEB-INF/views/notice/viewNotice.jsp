<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<%
	request.setCharacterEncoding("UTF-8");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript">
	function readURL(input) {
		if (input.files && input.files[0]) {
			var reader = new FileReader();
			reader.onload = function(e) {
				$('#image').attr('src', e.target.result);
			}
			reader.readAsDataURL(input.files[0]);
		}
	}
	function fn_enable(obj) {
		//document.getElementById("i_title").disabled = false;
		document.getElementById("i_content").disabled = false;
		document.getElementById("mod_btn").style.display = "none";
		document.getElementById("submit").style.display = "block";
		if ($('#uploadFile').val() != "") {
			document.getElementById("del_btn").style.display = "block";
		}
		document.getElementById("mod_file").style.display = "block";
		//document.getElementById("tr_file_upload").style.display="block";
		//document.getElementById("tr_btn").style.display="none"; */
	}
	
	 function fn_reply_form(url, noticeId){
		 var form = document.createElement("form");
		 form.setAttribute("method", "post");
		 form.setAttribute("action", url);
	     var noticeIdInput = document.createElement("input");
	     noticeIdInput.setAttribute("type","hidden");
	     noticeIdInput.setAttribute("name","noticeId");
	     noticeIdInput.setAttribute("value", noticeId);
		 
	     form.appendChild(noticeIdInput);
	     document.body.appendChild(form);
		 form.submit();
	 }
	function del_image(obj) {
		$("#uploadFile").val("");
		document.getElementById("image").style.display = "none";
	}
	/*  function fn_remove_article(url,boardId){
		 var form = document.createElement("form");
		 form.setAttribute("method", "post");
		 form.setAttribute("action", url);
	     var articleNOInput = document.createElement("input");
	     articleNOInput.setAttribute("type","hidden");
	     articleNOInput.setAttribute("name","articleNO");
	     articleNOInput.setAttribute("value", articleNO);
		 
	     form.appendChild(articleNOInput);
	     document.body.appendChild(form);
	     form.submit();
	 
	 } */
</script>
</head>
<body>
	<div class="container">
		<div class="product-details">
			<!--product-details-->
			<div class="view-product">
				<h2 align=center>공지사항</h2>
				<form name="frmNotice" method="post"
					action="${contextPath}/notice/updateNotice?${_csrf.parameterName}=${_csrf.token}"
					enctype="multipart/form-data">
					<table class="table" border=0 align="center" style="width: 800px">
						<tr>
							<td width=150 align="center">글번호</td>
							<td><input type="text" value="${noticeVO.noticeId }"
								style="border: 0" disabled /> <input type="hidden"
								name="noticeId" value="${noticeVO.noticeId}" /> <input
								type="hidden" name="originNo" value="${noticeVO.originNo}" /> 
							</td>
						</tr>
						<tr>
							<td width="150" align="center">작성자</td>
							<td><input type=text style="border: 0"
								value="${noticeVO.adminId }" name="adminId" disabled /></td>
						</tr>
						<tr>
							<td width="150" align="center">조회수</td>
							<td><input type="number" style="border: 0"
								value="${noticeVO.viewCnt }" name="viewCnt" disabled /></td>
						</tr>
						<tr>
							<td width="150" align="center">제목</td>
							<td><input type=text value="${noticeVO.title }"
								style="border: 0; width: 600px;" name="title"
								disabled /></td>
						</tr>
						<tr>
							<td width="150" align="center">내용</td>
							<td><textarea rows="20" cols="60" name="content"
									id="i_content" disabled />${noticeVO.content}</textarea></td>
						</tr>

						<tr>
							<td width="150" align="center">이미지</td>
							<td><input type="hidden" name="uploadFile" id="uploadFile"
								value="${noticeVO.imageFileName }" /> <c:if
									test="${not empty noticeVO.imageFileName }">
									<img
										src="${contextPath}/download.do?imageFileName=${noticeVO.imageFileName}"
										id="image" style="width: 200px; height: auto;" />
								</c:if> <input type="button" value="이미지 삭제" id="del_btn"
								onClick="del_image(this.form)" style="display: none"> <input
								type="file" name="mod_file " id="mod_file"
								onchange="readURL(this);" style="display: none" /><br></td>
						</tr>

						<!-- <tr>
							<td><input type="file" name="imageFileName "
								id="i_imageFileName" disabled onchange="readURL(this);" /></td>
						</tr> -->
						<tr>
							<td width="150" align="center">등록일자</td>
							<td><input type=text style="border: 0"
								value="<fmt:formatDate value="${noticeVO.boardDate}" pattern="yyyy-MM-dd"/>"
								disabled /></td>
						</tr>
						<tr id="tr_btn_modify" align="center">
							<td colspan="2">
								<c:if test="${id eq noticeVO.adminId }">
									<input type=submit value="수정반영하기" id="submit"
									style="display: none; background: #428bca; border: medium none; border-radius: 0; color: #FFFFFF; font-family: 'Roboto', sans-serif; padding: 6px 25px;"><br>
									<input type=button value="수정하기" onClick="fn_enable(this.form)"
									id="mod_btn"
									style="background: #428bca; border: medium none; border-radius: 0; color: #FFFFFF; font-family: 'Roboto', sans-serif; padding: 6px 25px;">
								</c:if>
								<c:if test="${id eq noticeVO.adminId || id eq 'admin' }">
									<input type=button value="삭제하기"
									onClick="location.href='${contextPath}/notice/deleteNotice?originNo=${noticeVO.originNo }"
									style="background: #428bca; border: medium none; border-radius: 0; color: #FFFFFF; font-family: 'Roboto', sans-serif; padding: 6px 25px;">
								</c:if>
								<input type=button value="목록보기"
								onClick="location.href='${contextPath}/notice/listnotices'"
								style="background: #428bca; border: medium none; border-radius: 0; color: #FFFFFF; font-family: 'Roboto', sans-serif; padding: 6px 25px;">
								
							</td>
								
							
						</tr>
						<tr id="tr_btn">
							<td colspan="2" align="center"></td>
						</tr>
					</table>
				</form>
			</div>
		</div>
	</div>
</body>
</html>
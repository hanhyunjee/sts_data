<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    isELIgnored="false" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
  request.setCharacterEncoding("UTF-8");
%> 
<c:set var="contextPath"  value="${pageContext.request.contextPath}"  />

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
<title>헤더</title>
</head>
<body>
<table border=0  width="100%">
  <tr>
     <td width=22px>
		<a href="${contextPath}/main.do">
			<img src="${contextPath}/resources/image/small.gif"  align="left" />
		</a>		<!-- 이미지 누르면 main.do로 이동 -->
     </td>
     <td>
       <h1><font size=30 >Spring 실습홈페이지</font></h1>
     </td>
     
     <td>
       <!-- <a href="#"><h3>로그인</h3></a> -->
       <c:choose>
          <c:when test="${isLogOn == true  && member!= null}"> <!-- c:when이 case문이랑 같은 역할 -->
          <!-- isLogOn은 MemberControllerImpl.java의  
          session.setAttribute("isLogOn", true); 코드있음 -->
          
          
            <h3>환영합니다. ${member.name }님!</h3>
            <a href="${contextPath}/member/logout.do"><button>로그아웃</button></a>
          </c:when>
          <c:otherwise>
	        <a href="${contextPath}/member/loginForm.do"><button>로그인</button></a>
	      </c:otherwise>
	   </c:choose>     
     </td>
  </tr>
</table>


</body>
</html>
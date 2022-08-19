package kr.ac.spring.notice.vo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component("noticeVO")
public class NoticeVO {
		private int noticeId;
		private String adminId;
		private Date boardDate;
		private String title;
		private String content;
		private int viewCnt;
		private String imageFileName;
		private int originNo; // 원글 번호
		
		
		public int getNoticeId() {
			return noticeId;
		}
		public void setNoticeId(int noticeId) {
			this.noticeId = noticeId;
		}
		public String getAdminId() {
			return adminId;
		}
		public void setAdminId(String adminId) {
			this.adminId = adminId;
		}
		public Date getBoardDate() {
			return boardDate;
		}
		public void setBoardDate(Date boardDate) {
			this.boardDate = boardDate;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public int getViewCnt() {
			return viewCnt;
		}
		public void setViewCnt(int viewCnt) {
			this.viewCnt = viewCnt;
		}
		public String getImageFileName() {
			return imageFileName;
		}
		public void setImageFileName(String imageFileName) {
			try {
				if(imageFileName!= null && imageFileName.length()!=0) {
					this.imageFileName = URLEncoder.encode(imageFileName,"UTF-8");
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		public int getOriginNo() {
			return originNo;
		}
		public void setOriginNo(int originNo) {
			this.originNo = originNo;
		}
		@Override
		public String toString() {
			return "NoticeVO [noticeId=" + noticeId + ", adminId=" + adminId + ", boardDate=" + boardDate + ", title="
					+ title + ", content=" + content + ", viewCnt=" + viewCnt + ", imageFileName=" + imageFileName
					+ ", originNo=" + originNo + "]";
		}



		public NoticeVO() {
			
		}









}


package kr.ac.spring.review.controller;

import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import kr.ac.spring.review.service.ReviewService;
import kr.ac.spring.review.vo.ReviewVO;

@RestController("reviewController") // @ResposeBody + @Controller
@RequestMapping("/review")
public class ReviewController {
	@Autowired
	private ReviewService reviewService;
	
	@Inject
	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)     // productDetail.jsp 에서 
	public ResponseEntity<String> insertReview(@RequestBody ReviewVO reviewVO){    // 리뷰등록은 json으로 데이터를 송신하기때문에 @RequestBody 어노테이션 사용
		ResponseEntity<String> entity = null;                                      // json은 url에 데이터를 보내지않고 HTTP Body에 데이터를 보내기 때문에 
		try {                                                                      // url에서 데이터를 읽어오는 RequestParam방식으로는 JSON 데이터를 읽어올수없다.
			reviewService.insertReview(reviewVO);           // 리뷰 등록
			entity = new ResponseEntity<>("insertSuccess",HttpStatus.OK);          // 클라이언트에게 HTTP 메세지로 "insertSucces"와 상태코드 OK를 보냄
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);  // 에러 발생시 에러메시지와 상태코드 전송
		}
		return entity;
	}
	
	@RequestMapping(value="/all/{bookNo}", method = RequestMethod.GET)
	public ResponseEntity<List<ReviewVO>> listReviews(@PathVariable("bookNo") int bookNo){
		ResponseEntity<List<ReviewVO>> entity = null;
		try {
			entity = new ResponseEntity<>(reviewService.selectReviewByBookNo(bookNo), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
	@RequestMapping(value="/{reviewId}", method = {RequestMethod.PUT, RequestMethod.PATCH})  // Rest(GET,POST, PUT, DELETE) 방식의 요청
	public ResponseEntity<String> updateReview(@PathVariable("reviewId") int reviewId, @RequestBody ReviewVO reviewVO){
		ResponseEntity<String> entity = null;
		try {
			reviewService.updateReview(reviewVO);
			entity = new ResponseEntity<>("updateSuccess",HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
	@RequestMapping(value="/{reviewId}", method=RequestMethod.DELETE)
	public ResponseEntity<String> deleteReview(@PathVariable("reviewId") int reviewId){
		ResponseEntity<String> entity = null;
		try {
			reviewService.deleteReview(reviewId);
			entity = new ResponseEntity<>("deleteSuccess",HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
}

package poly.service.impl;

import java.util.Iterator;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import poly.dto.MovieDTO;
import poly.persistance.mapper.IMovieMapper;
import poly.service.IMovieService;
import poly.util.CmmUtil;
import poly.util.DateUtil;

@Service("MovieService")
public class MovieService implements IMovieService {

	@Resource(name="MovieMapper")
	private IMovieMapper movieMapper;
	
	//로그 파일 생성 및 로그 출력을 위한 log4j 프레임워크의 자바 객체
	private Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * JSOUP 라이브러리를 통한 CGV 영화 순위 정보 가져오기
	 * */
	@Override
	public int getMovieInfoFromWEB() throws Exception {

		//로그 찍기(추후 찍은 로그를 통해 이 함수에 접근했는지 파악하기 용이하다.)
		log.info(this.getClass().getName() + ".getMovieInfoFromWEB Start!");
		
		int res = 0; //크롤링 결과(0보다 크면 크롤링 성공)
		
		//CGV 영화 순위 정보 가져올 사이트 주소
		String url = "http://www.cgv.co.kr/movies/";
		
		//JSOUP 라이브러리를 통해 사이트 접속 되면, 그 사이트의 전체 HTML 소스 저장할 변수
		Document doc = null; 
		
		//사이트 접속(http 프로토콜만 가능, https 프로토콜은 보안상 안 됨)
		doc = Jsoup.connect(url).get();
		
		//CGV 웹페이지의 전체 소스 중 일부 태그를 선택하기 위해 사용
		// <div class="sect-movie-chart"> 이 태그 내에서 있는 HTML 소스만 element에 저장됨
		Elements element = doc.select("div.sect-movie-chart");
		
		//Iterator를 사용하여 영화 순위 정보를 가져오기
		//영화 순위는 기본적으로 1개 이상의 영화가 존재하기 때문에 태그의 반복이 존재할 수밖에 없음
		Iterator<Element> movie_rank = element.select("strong.rank").iterator(); //영화 순위
		Iterator<Element> movie_name = element.select("strong.title").iterator(); //영화 이름
		Iterator<Element> movie_reserve = element.select("strong.percent span").iterator(); //영화 예매율
		Iterator<Element> score = element.select("span.percent").iterator(); //점수
		Iterator<Element> open_day = element.select("span.txt-info").iterator(); //개봉일
		
		MovieDTO pDTO = null;
		
		//수집된 데이터 DB에 저장하기
		while(movie_rank.hasNext()) {
			
			pDTO = new MovieDTO(); //수집된 영화정보를 DTO에 저장하기 위해 메모리에 올리기
			
			//수집시간을 기본키(pk)로 사용
			pDTO.setRank_check_time(DateUtil.getDateTime("yyyyMMdd"));
			
			//영화 순위(trim 함수 추가 이유 : trim 함수는 글자의 앞뒤 공백 삭제 역할을 수행하며, 데이터 수집 시,
			//홈페이지 개발자들을 앞뒤 공백을 집어넣을 수 있어서 추가함
			String rank = (CmmUtil.nvl(movie_rank.next().text()).trim()); //No.1 들어옴
			pDTO.setMovie_rank(rank.substring(3,rank.length()));
			
			//영화 제목
			pDTO.setMovie_nm(CmmUtil.nvl(movie_name.next().text()).trim());
			
			//영화 예매율
			pDTO.setMovie_reserve(CmmUtil.nvl(movie_reserve.next().text()).trim());
			
			//영화 점수
			pDTO.setScore(CmmUtil.nvl(score.next().text()).trim());
			
			//수집되는 데이터가 '2019.10.23' 개봉인 경우, 앞에 10자리인 2019.10.23만 저장
			pDTO.setOpen_day(CmmUtil.nvl(open_day.next().text()).trim().substring(0,10));
			
			//등록자
			pDTO.setReg_id("admin");
			
			//영화 한개씩 추가
			res += movieMapper.InsertMovieInfo(pDTO);
			
		}
		
		//로그 찍기(추후 찍은 로그를 통해 이 함수에 접근했는지 파악하기 용이하다.)
		log.info(this.getClass().getName() + ".getMovieInfoFromWEB end!");
		
		return res;
	}
	
	

}

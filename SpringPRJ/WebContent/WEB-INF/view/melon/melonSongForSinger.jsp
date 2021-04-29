<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>멜론 TOP에 랭크된 아이유의 노래 제목 가져오기</title>
<script src="/js/jquery-3.4.1.min.js"></script>
<script type="text/javascript">
	$(window).on("load", function() {
		
		// 페이지 로딩 완료 후, 멜론 순위 가져오기 함수 실행함
		getRank();
	});
	
	//멜론 순위 가져오기
	function getRank() {
		
		//Ajax 호출
		$.ajax({
			url : "/melon/getSongForsinger.do",
			type : "post",
			dataType : "JSON",
			contentType : "application/json; charset=UTF-8",
			success : function(json) {
				
				var melon_rank = "";
				
				for (var i=0; i<json.length; i++) {
					melon_rank += (json[i].rank + "위 | ");
					melon_rank += (json[i].song + "<br>");
					
				}
				
				$('#melon_rank').html(melon_rank);
			}
			
		})
	}

</script>
</head>
<body>
	<h1>차트에 있는 방탄소년단 노래 순위</h1>
	<hr />
	<div id="melon_rank"></div>	
	<br />
	<hr />
</body>
</html>
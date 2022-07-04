/*******************************************
* Note : get this week
* 설명 : 이번주 일 ~ 토 요일 날짜로 구하기
*******************************************/ 
function fn_thisWeek(){
	
	var currentDay = new Date();  				// 오늘
	var theYear = currentDay.getFullYear();		// 이번년도
	var theMonth = currentDay.getMonth();		// 이번달
	var theDate  = currentDay.getDate();		// 일
	var theDayOfWeek = currentDay.getDay();		// 요일
	 
	var thisWeek = [];
	 
	for(var i=0; i<7; i++) {
	  var resultDay = new Date(theYear, theMonth, theDate + (i - theDayOfWeek));
	  var yyyy = resultDay.getFullYear();
	  var mm = Number(resultDay.getMonth()) + 1;
	  var dd = resultDay.getDate();
	 
	  mm = String(mm).length === 1 ? '0' + mm : mm;
	  dd = String(dd).length === 1 ? '0' + dd : dd;
	 
	  thisWeek[i] = yyyy + '-' + mm + '-' + dd;
	}
	
	return thisWeek;
}
/*******************************************
* Note : get this month
* 설명 : 이번달 1일 ~ 31일 까지 구하기
*******************************************/ 
function fn_thisMonth(){
	
	var currentDay = new Date();  				// 오늘
	var theYear = currentDay.getFullYear();		// 이번년도
	var theMonth = currentDay.getMonth();		// 이번달
	var theDate  = currentDay.getDate();		// 일
	var firstDate = new Date(theYear, theMonth, 1).getDate();		// 이번달 1일
	var theDayOfWeek = currentDay.getDay();		// 요일
	 
	var thisMonth = [];
	
	for(var i=0; i<31; i++) {
	  var resultDay = new Date(theYear, theMonth, firstDate + i);
	  var yyyy = resultDay.getFullYear();
	  var mm = Number(resultDay.getMonth()) + 1;
	  var dd = resultDay.getDate();
	 
	  mm = String(mm).length === 1 ? '0' + mm : mm;
	  dd = String(dd).length === 1 ? '0' + dd : dd;
	 
	  thisMonth[i] = yyyy + '-' + mm + '-' + dd;
	}
	
	return thisMonth;
	
}

/*******************************************
* Note : task all start date check
* 설명 : '전체업무'페이지에서 '시작일'(라디오 버튼) 체크시
*******************************************/ 
function fn_checkStartDate(el){
	var item = $(el);	// input
	var id = item.attr('id');
	var date = new Date();
	var nowDate = new Date(date).toISOString().slice(0,10);		// 현재날짜
	var tableTr = $('.table tbody tr');
	var startDate = $('.table').find('.task-start-date');
	var thisWeek = fn_thisWeek();
	var thisMonth = fn_thisMonth();
	
	tableTr.hide();
	
	for(var i=0; i < tableTr.length; i++){
		// 전체
		if(id == 'taskAll-start-date'){
			tableTr.show();
		}
		// 오늘
		else if(id == 'taskAll-start-todoay'){
			if(startDate.eq(i).text() == nowDate){
				startDate.eq(i).parent().parent('tr').show();
			}
		}
		// 이번주
		else if(id == 'taskAll-start-week'){
			for(var j=0; j < 7; j++){
				if(startDate.eq(i).text() == thisWeek[j]){
					startDate.eq(i).parent().parent('tr').show();
				}
			}
		}
		// 이번달
		else if(id == 'taskAll-start-month'){
			for(var j=0; j < 31; j++){
				if(startDate.eq(i).text() == thisMonth[j]){
					startDate.eq(i).parent().parent('tr').show();
				}
			}
		}
		// 날짜 미정
		else if(id == 'taskAll-start-null'){
			if(startDate.eq(i).text() == ""){
				startDate.eq(i).parent().parent('tr').show();
			}
		}
	}
}

/*******************************************
* Note : task all end date check
* 설명 : '전체업무'페이지에서 '마감일'(라디오 버튼) 체크시
*******************************************/ 
function fn_checkEndDate(el){
	var item = $(el);	// input
	var id = item.attr('id');
	var date = new Date();	// 현재날짜
	var nowDate = new Date(date).toISOString().slice(0,10);		// 현재날짜(yyyy-mm-dd)
	var tableTr = $('.table tbody tr');
	var startDate = $('.table').find('.task-end-date');
	var thisWeek = fn_thisWeek();
	var thisMonth = fn_thisMonth();
	
	var theYear = date.getFullYear();							// 이번년도
	var theMonth = date.getMonth()+1;							// 이번달
	var lastDay = new Date(theYear, theMonth, 0).getDate();		// 이번달 마지막 일
	var resultLastDate = theYear+"-"+theMonth+"-"+lastDay;		// 이번달 마지막 날짜(2018-10-31)
	
	
	tableTr.hide();
	
	for(var i=0; i < tableTr.length; i++){
		// 전체
		if(id == 'taskAll-end-date'){
			tableTr.show();
		}
		// 오늘
		else if(id == 'taskAll-end-todoay'){
			if(startDate.eq(i).text() == nowDate){
				startDate.eq(i).parent().parent('tr').show();
			}
		}
		// 이번주까지
		else if(id == 'taskAll-end-week'){
			for(var j=0; j < 7; j++){
				if(startDate.eq(i).text() == thisWeek[j]){
					startDate.eq(i).parent().parent('tr').show();
				}
			}
		}
		// 이번달까지
		else if(id == 'taskAll-end-month'){
			if(startDate.eq(i).text() != '' && startDate.eq(i).text() <= resultLastDate){
				startDate.eq(i).parent().parent('tr').show();
			}
		}
		// 날짜 미정
		else if(id == 'taskAll-end-null'){
			if(startDate.eq(i).text() == ""){
				startDate.eq(i).parent().parent('tr').show();
			}
		}
	}
}
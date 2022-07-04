$(function(){

			// nav toggle
			$(".nav-toggle-btn").on("click", function(){
				if($(".body-wrap-full").hasClass("open-nav")){
					$(".body-wrap-full").removeClass("open-nav");
					$(".body-wrap-full").addClass("close-nav");
				} else {
					$(".body-wrap-full").addClass("open-nav");
					$(".body-wrap-full").removeClass("close-nav");
				}
			});

			// body click box closed
			$('body').mouseup(function(e){
				var editColorBtn = $(e.target).hasClass("edit-color-btn");	// 프로젝트 컬러 설정 버튼
				var hideItem = $(".edit-color-box");

				// 선택(target)한 element가 특정 클래스를 가지고있지 않을 때만, hide() 처리
				if(!editColorBtn){
					hideItem.hide();
				}
			});

		});

// nav scroll
var swiper = new Swiper('.swiper-container.asdie-scroll', {
	direction: 'vertical',
	slidesPerView: 'auto',
	freeMode: true,
	scrollbar: {
		el: '.swiper-scrollbar',
	},
	mousewheel: true,
});

$(function(){

	// box project
	$(".folder-detail").click(function(){
		var box_no = $(this).data("boxno");
		var box_name = $(this).data("boxname");
		$("#boxDetail #box_no").val(box_no);
		$("#boxDetail #box_name").val(box_name);
		$("#boxDetail").submit();
	});

	// box update
	$(".folder-edit").click(function(){
		var item = $(this).parent().siblings("a");
		var box_no = item.data("boxno");
		var box_name = item.data("boxname");
		$("#editFolder #box_no").val(box_no);
		$("#editFolder #box_name").val(box_name);
		layer_popup("#editFolder");
	});

	// box delete
	$(".folder-delete").click(function(){
		var box_no = $(this).parent().siblings("a").data("boxno");
		$("#deleteFolder #box_no").val(box_no);
		layer_popup("#deleteFolder");
	});

	// my-folder toggle button (메뉴)
	$(".folder-toggle-btn").on("click", function(){
		if($(".sub-nav-list").css("display") == "block"){
		    $(".sub-nav-list").slideUp();
		} else {
		    $(".sub-nav-list").slideDown();
		}
	});

	// 2차메뉴를 닫지 않고 메뉴바를 확장했을 떄
	$(".nav-toggle-btn").on("click", function(){
		if($(".sub-nav-list").css("display") == "block"){
			$(".sub-nav-list").slideUp();
		}
	});

	// sortable을 적용한 item의 id값이 순서가 됨.
	$('.nav-folder').sortable({
        update: function(event, ui) {
           var productOrder = $(this).sortable('toArray').toString();
           $(".test").text(productOrder);
        }
	});
});

$(function(){
		// search box open
		$(".icon-search").on("click", function(){
			$(".search-box").slideDown();
			$(".search-input").focus();
		});

		// search box close
		$(".close-icon").on("click", function(){
			$(".search-box").slideUp();
			$(".search-input").val();
		});

		// chat drop box slideUp & Down
		// : 채팅아이콘 클릭시 채팅 메뉴박스 보이기
		$(".chat-ptn").on("click", function(){
			if($(".chat-ptn-wrap").css("display") == "none"){
				$(".chat-ptn-wrap").slideDown();
			} else {
				$(".chat-ptn-wrap").slideUp();
			}
		});

		// logout
//		$(".logout").on("click", function(){
//			signOut();
//			$("#logout").submit();
//		});

		$("dl.alim-pop").on("click", function(){
			var mem_id = $(this).data("apply");
			var ptn_no = $(this).data("ptn_no");
			var alim_no = $(this).data("alim_no");
			var my_id = "${member.mem_id}";
			var src = $(this).children("dt.posi-re").children("img").attr("src");

			$.ajax({
				url : "/chat/pop2",
				method : "POST",
				dataType : "json", // server로 부터 받을 data type
				data : {mem_id : mem_id, my_id:my_id},
				success : function(data) {
					if (data) {
						$("#ptnAcceptPopup .flnm").text(data.mem.mem_nick);
						$("#ptnAcceptPopup .cmnm").text(data.mem.mem_id);
						$("#ptnAcceptPopup .phone").text(data.mem.mem_phone);
						$("#ptnAcceptPopup .ptn_no").text(ptn_no);
						$("#ptnAcceptPopup .alim_no").text(alim_no);
						$("#ptnAcceptPopup .pop-pic img").attr('src', src);
						layer_popup("#ptnAcceptPopup");
					} else {
						alert("해당 회원이 존재하지 않습니다.");
					}
				},
				error : function(error) {
					alert("error");
				}
			});

		});

	});

//	function signOut() {
//		var auth2 = gapi.auth2.getAuthInstance();
//		auth2.signOut().then(function () {
//			console.log('User signed out.');
//		});
//	}

	//longpolling alert
	(function(){
		var poll = function(){
			$.ajax({
				url : '/schd/dbcheck', 	//요청 보내는 url
				type : 'get',			//메서드 타입
				dataType : 'json' 		//서버로부터 되돌려 받는 데이터타입 명시
//				success : function(data){
//					if(data.schedule.length >0){
//						var msg = '';
//						for(i = 0; i < data.schedule.length ; i++ ){
//							msg += '<br>'+(i+1)+' 번째 일정명 :' +data.schedule[i].schd_title+'['+data.schedule[i].schd_start_time+']' ;
//						}
//						alertCustom2('현재 '+data.schedule.length+' 개의 미리알림 일정이 있습니다.'+msg, 'alert-warning');
//					}
//
//				},
//				error:function(request, status, error){
//					console.log("[ajax error]");
//					console.log("code:"+request.status+"\n"+"message : "+request.responseText+"\n"+"error:"+error);
//				}
			}).done(res => {
			    var i = 0;
			    res.forEach((schedule) => {
			        var msg = '';
			        msg += '<br>' + (i+1) + ' 번째 일정명 : ' + schedule.schd_title + '[' + schedule.schd_start_time + ']';
                    alertCustom2('현재 '+schedule.length+' 개의 미리알림 일정이 있습니다.'+msg, 'alert-warning');
                    i++;
			    });
			}).fail(error => {
                console.log("오류", error);
            });

		};
		poll();

		setInterval(function(){
// 			poll();
		}, 6000);

		setInterval(function(){
// 			search();
		}, 3000);
	})
	();

	function alertCustom2(text, className){
		var alertBox = $('.alert');

		alertBox.html(text);
		alertBox.addClass(className);
		alertBox.css('margin-left', -(alertBox.outerWidth()/2));
		alertBox.css('color', 'black');
		alertBox.fadeIn();
		setTimeout(function(){alertBox.fadeOut()}, 8000);
	}


$(function(){

		// 채팅창 띄우기(본인일 때)
		$("#userInfoPop").find(".my-chat-btn").on("click", function(){
			var popup = $("#userInfoPop");
			var id = popup.find(".pop-user-id").text();
			var nick = popup.find(".pop-user-nick").text();
			var my_id = popup.find(".pop-my-id").text();

			// open nav
			popup.find("#user_email").val(my_id);
			popup.find("#user_chat_title").val(nick);
			//popup.find("#user_ptn_id").val(id);

			$("#email_ptn").val(my_id);
			$("#chat_title_ptn").val(nick);
			$("#ptn_id_ptn").val("");

			var check = document.userChatFom;
			window.open('', "new", "width=467,height=640,top=100,left=100");
			check.action='/chat/insert';
			check.target='new';
			check.submit();
		});


		// 채팅창 띄우기(본인이 아닐 때)
		$("#userInfoPop").find(".chat-btn").on("click", function(){
			var popup = $("#userInfoPop");
			var id = popup.find(".pop-user-id").text();
			var nick = popup.find(".pop-user-nick").text();
			var my_id = popup.find(".pop-my-id").text();

			// open nav
			popup.find("#user_email").val(my_id);
			popup.find("#user_chat_title").val(nick);
			popup.find("#user_ptn_id").val(id);

			$("#email_ptn").val(my_id);
			$("#chat_title_ptn").val(nick);
			$("#ptn_id_ptn").val(id);

			var check = document.userChatFom;
			window.open('', "new", "width=467,height=640,top=100,left=100");
			check.action='/chat/insert';
			check.target='new';
			check.submit();
		});

		// 친구신청하기
		$("#userInfoPop").find("#apply-btn-ptn").on("click", function(){
			var popup = $("#userInfoPop");
			var ptn_accept = popup.find(".pop-user-id").text();
			var ptn_apply = popup.find(".pop-my-id").text();

			$.ajax({
				url : "/ptn/apply",
				data : {ptn_apply:ptn_apply, ptn_accept:ptn_accept},
				dataType : 'json',
				type : 'POST',
				success : function(data) {
					window.opener.location.reload();
					self.close();
				},
				error : function(data) {
					alert("실패");
				}
			});
		});

	});


$(function(){
		// 채팅창 띄우기
		$("#chat-btn-my").on("click", function(){
			var id = $("#profileMyPopup").find(".cmnm").text();
			var nick = $("#profileMyPopup .flnm").text();

			// open nav
			$("#email_my").val(id);
			$("#chat_title_my").val(nick);
			$("#ptn_id_my").val("");

			var check = document.myChatFom;
			window.open('', "new", "width=467,height=640,top=100,left=100");
			check.action='/chat/insert';
			check.target='new';
			check.submit();
		});

	});


$(function(){

		// 채팅창 띄우기
		$("#chat-btn-ptn").on("click", function(){
			var id = $("#ptnPopup .cmnm").text();
			var nick = $("#ptnPopup .flnm").text();
			var my_id = $("#ptnPopup .my_id").text();

			// open nav
			$("#email_ptn").val(my_id);
			$("#chat_title_ptn").val(nick);
			$("#ptn_id_ptn").val(id);

			var check = document.ptnChatFom;
			window.open('', "new", "width=467,height=640,top=100,left=100");
			check.action='/chat/insert';
			check.target='new';
			check.submit();
		});

		// 친구신청하기
		$("#apply-btn-ptn").on("click", function(){
			var ptn_accept = $("#ptnPopup").find(".cmnm").text();
			var ptn_apply = $("#ptnPopup .my_id").text();

			$.ajax({
				url : "/ptn/apply",
				data : {ptn_apply:ptn_apply, ptn_accept:ptn_accept},
				dataType : 'json',
				type : 'POST',
				success : function(data) {
					window.opener.location.reload();
					self.close();
				},
				error : function(data) {
					alert("실패");
				}
			});
		});

	});

$("#chat-btn-pro").on("click", function(){
		var id = $("#proPopup").find(".cmnm").text();
		var nick = $("#proPopup .flnm").text();
		var my_id = $("#proPopup .my_id").text();

		// open nav
		$("#email_pro").val(my_id);
		$("#chat_title_pro").val(nick);
		$("#ptn_id_pro").val(id);

		var check = document.proChatFom;
		window.open('', "new", "width=467,height=640,top=100,left=100");
		check.action='/chat/insert';
		check.target='new';
		check.submit();
	});

$(function(){
			// 친구수락하기
			$("#accept-btn-ptn").on("click", function(){
				var ptn_no = $("#ptnAcceptPopup .ptn_no").text();
				var alim_no = $("#ptnAcceptPopup .alim_no").text();

				$.ajax({
					url : "/ptn/accept",
					data : {ptn_no:ptn_no, alim_no:alim_no, ptn_chk:'y'},
					dataType : 'json',
					type : 'POST',
					success : function(data) {
						window.location.reload();
						self.close();
					},
					error : function(data) {
						alert("실패");
					}
				});
			});

			// 거절하기
			$("#cancel-btn-ptn").on("click", function(){
				var ptn_no = $("#ptnAcceptPopup .ptn_no").text();
				var alim_no = $("#ptnAcceptPopup .alim_no").text();

				$.ajax({
					url : "/ptn/acceptCancel",
					data : {ptn_no:ptn_no, alim_no:alim_no, ptn_chk:'n'},
					dataType : 'json',
					type : 'POST',
					success : function(data) {
						window.location.reload();
						self.close();
					},
					error : function(data) {
						alert("실패");
					}
				});
			});

		});

function fn_failDeletePro(){
	alertCustom("글이 있는 프로젝트는 삭제가 불가능 합니다.", "alert-danger");
}

$(function(){

	// 보관함 설정
	$(".custom-check-input").change(function(){
		var box_no = $(this).val();
		var pro_no = $(this).parents(".proFolderEdit-form").children("#pro_no").val();

		if($(this).is(":checked")){
			$.ajax({
				url : "/boxPro/insert?box_no="+box_no+"&pro_no="+pro_no,
				dataType : "json",
				success : function(data){
					alertCustom("보관함 설정되었습니다.","alert-warning");
				}
			});
		}else{
			$.ajax({
				url : "/boxPro/delete?box_no="+box_no+"&pro_no="+pro_no,
				dataType : "json",
				success : function(data){
					alertCustom("보관함 설정되었습니다.","alert-warning");
				}
			});
		}
	});

	// 상단 고정 확인 버튼
	$(".fix-submit-btn").on('click', function(){

		// 상단 고정 alert
		var item = $(this);
		var fixTextN = item.parent().siblings(".pop-con").children("p.fix-n");
		var fixTextY = item.parent().siblings(".pop-con").children("p.fix-y");

		// parameter
		var col = $(".fix-form .timeline_col");
		var no = $(".fix-form .timeline_no");
		var fixChk = $(".fix-form .fix_chk");

		if(fixTextN.css("display") == 'block'){	// 고정

			fixChk.val("y");

			if(col.val() == 'basic_no'){		// 일반글
				$(".fix-form").attr("action", "/basic/fix");

			}else if(col.val() == 'schd_no'){	// 일정
				$(".fix-form").attr("action", "/schd/fix");

			}else if(col.val() == 'task_no'){	// 업무
				$(".fix-form").attr("action", "/task/fix");

			}else if(col.val() == 'todo_no'){	// 할일
				$(".fix-form").attr("action", "/todo/fix");

			}else if(col.val() == 'vote_no'){	// 투표
				$(".fix-form").attr("action", "/vote/fix");
			}

		}else{	// 해제

			fixChk.val("n");

			if(col.val() == 'basic_no'){		// 일반글
				$(".fix-form").attr("action", "/basic/fix");

			}else if(col.val() == 'schd_no'){	// 일정
				$(".fix-form").attr("action", "/schd/fix");

			}else if(col.val() == 'task_no'){	// 업무
				$(".fix-form").attr("action", "/task/fix");

			}else if(col.val() == 'todo_no'){	// 할일
				$(".fix-form").attr("action", "/todo/fix");

			}else if(col.val() == 'vote_no'){	// 투표
				$(".fix-form").attr("action", "/vote/fix");
			}

		}

		$(".fix-form").submit();	// submit
	});

	// 삭제 확인 버튼 이벤트
	$(".timeline-del-btn").on('click', function(){

		var col = $(".timeline-del-form .timeline_col");

		if(col.val() == 'basic_no'){		// 일반글
			$(".timeline-del-form").attr("action", "/basic/delete");

		}else if(col.val() == 'schd_no'){	// 일정
			$(".timeline-del-form").attr("action", "/schd/delete");

		}else if(col.val() == 'task_no'){	// 업무
			$(".timeline-del-form").attr("action", "/task/delete");

		}else if(col.val() == 'todo_no'){	// 할일
			$(".timeline-del-form").attr("action", "/todo/delete");

		}else if(col.val() == 'vote_no'){	// 투표
			$(".timeline-del-form").attr("action", "/vote/delete");

		}else if(col.val() == 'post_no'){	// 게시글
			$(".timeline-del-form").attr("action", "/post/delete");

		}else if(col.val() == 'adm_post_no'){	// 관리자 게시글
			$(".timeline-del-form").attr("action", "/adm/delete");

		}else if(col.val() == 'rep_no'){	// 댓글
			$(".timeline-del-form").attr("action", "/rep/delete");
		}

		$(".timeline-del-form").submit();
	});
});

/*******************************************
* Note : layer pop-up
* 설명 : <a> 태그 버튼 클릭시 경로(href)에 지정된
* 		아이디값과 동일한 아이디를 가진 박스를 layer 팝업으로 띄워라
*******************************************/

// 새 비밀번호 설정(NEW PASSWORD)
$('.find-pw-btn').click(function(){
	var $href = $(this).attr('href');
	layer_popup($href);
});

// 보관함(MY FOLDER)
$('.add-folder-pop-btn').click(function(){
    var $href = $(this).attr('href');
    layer_popup($href);
});

// 보관함 수정(MY FOLDER > EDIT)
$('.folder-edit').click(function(){
	var $href = $(this).attr('href');
	layer_popup($href);
});

// 보관함 삭제(MY FOLDER > DELETE)
$('.folder-delete').click(function(){
	var $href = $(this).attr('href');
	layer_popup($href);
});

// 프로젝트 추가(ADD PROJECT > INSERT)
$('.add-pro-link').click(function(){
	var $href = $(this).attr('href');
	layer_popup($href);
});

// 프로젝트 보관함 설정
$('.pro-folder-edit-btn').click(function(){
	var $href = $(this).attr('href');
	layer_popup($href);
});

//채팅 팝업 동료 탭에서 내 프로필 클릭시 자세히보기
$('.my-pop').click(function(){
	var $href = $(this).attr('href');
	layer_popup($href);
});

// 기본 layer popup
function layer_popup(el){

    var $el = $(el);        //레이어의 id를 $el 변수에 저장
    var isDim = $el.prev().hasClass('dimBg');   //dimmed 레이어를 감지하기 위한 boolean 변수
    var layer = $el.parent('.dim-layer');

    isDim ? layer.fadeIn() : $el.fadeIn();

    var $elWidth = ~~($el.outerWidth()),
        $elHeight = ~~($el.outerHeight()),
        docWidth = $(document).width(),
        docHeight = $(document).height();

    // 화면의 중앙에 레이어를 띄운다.
    if ($elHeight < docHeight || $elWidth < docWidth) {
        $el.css({
            marginTop: -$elHeight /2,
            marginLeft: -$elWidth/2
        })
    } else {
        $el.css({top: 0, left: 0});
    }

    $el.find('.btn-close').click(function(){
        $el.find('input[type=text]').val('');
        $el.find('input[type=password]').val('');
        $el.find('input[type=checkbox]').prop('checked', false);
        $el.find('input[type=radio]').prop('checked', false);
        $el.find('textarea').val('');
        isDim ? $('.dim-layer').fadeOut() : $el.fadeOut(); // 닫기 버튼을 클릭하면 레이어가 닫힌다.
        return false;
    });

    $('.layer .dimBg').click(function(){
        $('.dim-layer').fadeOut();
        return false;
    });

}



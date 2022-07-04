/*******************************************
* Note : add file list
* 설명 : 게시글 파일첨부(<input type="file">)를 통해 파일 선택시,
* 		파일정보(파일이름, 파일확장명, 파일값)를 가져와 파일목록에 추가한다.
*******************************************/

function fileUpload(file){
	var fileValue = $(file).val();	// 파일값
	var fileName = file.files[0].name; // 파일명
	var fileEx = fileValue.split(".").pop().toLowerCase();	// 파일 확장명
	var className = "";	// 파일 아이콘 클래스명
	
	var item = $(file);	
	var itemId = item.attr("id");
	
	// 사이즈체크
    var maxSize  = 5 * 1024 * 1024   //5MB
    var fileSize = 0;
    var byte = "byte";
    
    // 브라우저 확인
	var browser=navigator.appName;	
	if (browser=="Microsoft Internet Explorer") {	// 익스플로러일 경우
		var oas = new ActiveXObject("Scripting.FileSystemObject");
		fileSize = oas.getFile(file.value).size;
	} else {	// 익스플로러가 아닐경우
		fileSize = file.files[0].size;
	}

    if(fileSize > maxSize) {
        alert("첨부파일 사이즈는 5MB 이내로 등록 가능합니다.");
        return;
    }
    
    if (fileSize >= 1024) {
    	fileSize = (fileSize / 1024).toFixed(1);
    	byte = "KB";
    }
    
    if (fileSize >= 1024) {
		fileSize = (fileSize / 1024).toFixed(1);
		byte = "MB";
	}
	
	// 파일확장명 검사 : 확장명에 따라 아이콘 변경
	switch (fileEx) {
	case "hwp" :
		className = "file-hwp";
		break;
	case "xlsx": case "xlsm": case "xlsb": case "xltx": case "xltm": case "xls": case "xlt": case "xlam" :
		className = "file-excel";
		break;
	case "doc": case "docx": case "docm": case "dot": case "dotx": case "dotm" :
		className = "file-word";
		break;
	case "pptx": case "pptm": case "potx": case "potm": case "ppam": case "ppsx": case "ppsm": case "sldx": case "sldm": case "thmx" :
		className = "file-ppt";
		break;
	case "msg": case "pst": case "ost" :
		className = "file-outlook";
		break;
	case "pdf" :
		className = "file-pdf";
		break;
	case "jpeg": case "jpg": case "gif": case "png": case "svg": case "bmp": 
		className = "file-img";
		break;
	case "avi": case "mp4": case "mpg": case "mpeg": case "mpe": case "wmv": case "asf": case "asx": case "flv": case "rm": case "mov": case "dat": case "mkv" :
		className = "file-video";
		break;
	case "aiff": case "aac": case "alac": case "amr": case "wav": case "au": case "awb": case "dvf": case "flac": case "mmf": case "mp3": case "wma" :
		className = "file-audio";
		break;
	case "zip": case "ais": case "arj": case "cab": case "gz": case "hgx": case "jas": case "lzh": case "pak": case "psz": case "rar": case "sea": case "tgz": case "zool": case "jar" :
		className = "file-zip";
		break;
	default:
		className = "file-default";
		break;
	}
	
	var fileListBox = $(file).parent().prev().children().eq(-1);
	var fileInfoBox = fileListBox.children();
	
	// 파일정보(리스트) 추가하기
	if(fileValue != null){
		// 파일개수 제한(5개 이상일 땐, 알림창 띄우기)
		if(fileInfoBox.length < 5){
			
			item.attr("id","");
			item.attr('data-name', fileName);
			item.parent().prepend("<input type=\"file\" id=\""+itemId+"\" name=\"articleFile\" class=\"dis-none\" onchange=\"fileUpload(this)\">");
			
			fileListBox.append(
				"<div class=\"upload-file-info martop-20\" data-name=\""+fileName+"\">"
					+ "<dl>"
						+ "<dt>"
							+ "<i class=\"dis-inblock file-icon " + className + "\"></i>"
						+ "</dt>"
						+ "<dd>"
							+ "<span class=\"dis-block size-18 color-black\">" + fileName + "</span>"
							+ "<span class=\"dis-block martop-5 size-14 color-gray\">" + fileSize +" "+byte+ "</span>"
						+ "</dd>"
					+ "</dl>"
					+ "<i class=\"far fa-times-circle file-close-btn\" onclick=\"fileRemove(this)\"></i>"
				+ "</div>"
			);
		} else {
			alert("파일은 5개까지만 등록 가능합니다.");
			item.attr('name','');
			fileValue = null;
		}
	}
		
}

/*******************************************
* Note : add img list
* 설명 : 게시글 이미지첨부(<input type="file">)를 통해 이미지파일 선택시,
* 		이미지를 게시글 하단 이미지목록에 추가한다.
*******************************************/
function imgUpload(file){
	var fileValue = $(file).val();	// 파일값
	var fileName = file.files[0].name; // 파일명
	var fileEx = fileValue.split(".").pop().toLowerCase();	// 파일 확장명
	
	var item = $(file);
	var itemId = item.attr("id");
	
	var imgListBox = $(file).parent().prev().children().eq(-2);
	var imgInfoBox = imgListBox.children();
	
	// 이미지(리스트) 추가하기
	if(fileValue != null){
		// 파일개수 제한(5개 이상일 떈, 알림창 띄우기)
		if(imgInfoBox.length < 5){
			
			item.attr("id","");
			item.attr('data-name', fileName);
			item.parent().prepend("<input type=\"file\" id=\""+itemId+"\" name=\"imageFile\" class=\"dis-none\" onchange=\"imgUpload(this)\" accept=\"image/*\">");
			
			// 리스트에 추가
			imgListBox.append(
				"<div class=\"upload-img-info martop-20\" data-name=\""+fileName+"\">"
					+ "<div class=\"upload-img\"></div>"
					+ "<i class=\"fas fa-times-circle img-close-btn\" onclick=\"fileRemove(this)\"></i>"
					+ "<input type=\"hidden\" value=\"" + fileValue + "\" class=\"img-input\">"
				+ "</div>"
			);
			
			// 리스트 이미지태그에 이미지 미리보기 적용
			imgInfoBox = imgListBox.children(".upload-img-info");
			var img = "";
			imgInfoBox.find(".img-input").each(function(){
				if(fileValue == $(this).val()){
					img = $(this).parent().children(".upload-img");
					var reader = new FileReader();
					reader.onload = function(e) {
						img.css('background-image', "url('" + e.target.result + "')");
					}
					reader.readAsDataURL(file.files[0]);
				}
			});
			
		} else {
			alert("이미지파일은 5개까지만 등록 가능합니다.");
			item.attr('name','');
			fileValue = null;
		}
	}
	
}

/*******************************************
 * Note : file list remove
 * 설명 : 해당 파일을 fileList에서 삭제
 *******************************************/
function fileRemove(el){
	var item = $(el);
	var dataName = item.parent().attr('data-name');
	var tabConBox = item.parent().parent().parent();
	var tabDnBox = tabConBox.siblings('.tab-dn-box');	
	
	item.parent("div").remove();
	
	tabDnBox.find('input[type=file]').each(function(i, e){
		if(tabDnBox.find('input[type=file]').eq(i).attr('data-name') == dataName){
			tabDnBox.find('input[type=file]').eq(i).remove();
			return false;
		}
	});
	
}

/*******************************************
 * Note : file delete
 * 설명 : 글 수정상태에서 첨부파일을 삭제
 *******************************************/
function fileDelete(el){
	var item = $(el);
	var files_no = item.parent("div").data("no");
	var frm = item.parents("form");
	
	frm.prepend("<input type=\"hidden\" name=\"del_files_no\" value=\""+files_no+"\">");
	item.parent("div").remove();
}

/*******************************************
* Note : comment add img & file list
* 설명 : 댓글 이미지 및 파일첨부(<input type="file">)를 통해 파일 선택시,
* 		이미지와 파일을 게시글 하단 이미지목록에 추가한다.
*******************************************/

function commentFileUpload(file){
	var fileValue = $(file).val();	// 파일값
	var fileName = file.files[0].name; // 파일명
	var fileEx = fileValue.split(".").pop().toLowerCase();	// 파일 확장명
	var className = "";	// 파일 아이콘 클래스명
	
	// 사이즈체크
    var maxSize  = 5 * 1024 * 1024   //5MB
    var fileSize = 0;
    var byte = "byte";
    
    // 브라우저 확인
	var browser=navigator.appName;	
	if (browser=="Microsoft Internet Explorer") {	// 익스플로러일 경우
		var oas = new ActiveXObject("Scripting.FileSystemObject");
		fileSize = oas.getFile(file.value).size;
	} else {	// 익스플로러가 아닐경우
		fileSize = file.files[0].size;
	}

    if(fileSize > maxSize) {
        alert("첨부파일 사이즈는 5MB 이내로 등록 가능합니다.");
        return;
    }
    
    if (fileSize >= 1024) {
    	fileSize = (fileSize / 1024).toFixed(1);
    	byte = "KB";
    }    
    if (fileSize >= 1024) {
		fileSize = (fileSize / 1024).toFixed(1);
		byte = "MB";
	}
	
	// 파일확장명 검사 : 확장명에 따라 아이콘 변경
	switch (fileEx) {
	case "hwp" :
		className = "file-hwp";
		break;
	case "xlsx": case "xlsm": case "xlsb": case "xltx": case "xltm": case "xls": case "xlt": case "xlam" :
		className = "file-excel";
		break;
	case "doc": case "docx": case "docm": case "dot": case "dotx": case "dotm" :
		className = "file-word";
		break;
	case "pptx": case "pptm": case "potx": case "potm": case "ppam": case "ppsx": case "ppsm": case "sldx": case "sldm": case "thmx" :
		className = "file-ppt";
		break;
	case "msg": case "pst": case "ost" :
		className = "file-outlook";
		break;
	case "pdf" :
		className = "file-pdf";
		break;
	case "jpeg": case "jpg": case "gif": case "png": case "svg": case "bmp": 
		className = "file-img";
		break;
	case "avi": case "mp4": case "mpg": case "mpeg": case "mpe": case "wmv": case "asf": case "asx": case "flv": case "rm": case "mov": case "dat": case "mkv" :
		className = "file-video";
		break;
	case "aiff": case "aac": case "alac": case "amr": case "wav": case "au": case "awb": case "dvf": case "flac": case "mmf": case "mp3": case "wma" :
		className = "file-audio";
		break;
	case "zip": case "ais": case "arj": case "cab": case "gz": case "hgx": case "jas": case "lzh": case "pak": case "psz": case "rar": case "sea": case "tgz": case "zool": case "jar" :
		className = "file-zip";
		break;
	default:
		className = "file-default";
		break;
	}
	
	var item = $(file);
	var itemId = item.attr("id");
	
	var imgListBox = $(file).siblings(".comment-upload-img-list");
	var imgInfoBox = imgListBox.children();
	var fileListBox = $(file).siblings(".comment-upload-file-list");
	var fileInfoBox = fileListBox.children();
	
	// 파일정보(리스트) 추가하기
	if(fileValue != null){		
		
		if(className == "file-img") {	// 이미지일때
			// 파일개수 제한(5개 이상일 떈, 알림창 띄우기)
			
			if(imgInfoBox.length < 5){
				
				item.attr("id","");
				item.attr("name","imageFile");
				item.attr('data-name', fileName);
				item.parent().prepend("<input type=\"file\" id=\""+itemId+"\" class=\"dis-none\" onchange=\"commentFileUpload(this)\">");
								
				// 리스트에 추가
				imgListBox.append(
					"<div class=\"upload-img-info martop-20\" data-name=\""+fileName+"\">"
						+ "<div class=\"upload-img\"></div>"
						+ "<i class=\"fas fa-times-circle img-close-btn\" onclick=\"commentFileRemove(this)\"></i>"
						+ "<input type=\"hidden\" value=\"" + fileValue + "\" class=\"img-input\">"
					+ "</div>"
				);
				
				// 리스트 이미지태그에 이미지 미리보기 적용
				imgInfoBox = imgListBox.children(".upload-img-info");
				var img = "";
				imgInfoBox.find(".img-input").each(function(){
					if(fileValue == $(this).val()){
						img = $(this).parent().children(".upload-img");
						var reader = new FileReader();
						reader.onload = function(e) {
							img.css('background-image', "url('" + e.target.result + "')");
						}
						reader.readAsDataURL(file.files[0]);
					}
				});
				
			} else {
				alert("이미지파일은 5개까지만 등록 가능합니다.");
				item.attr('name','');
				fileValue = null;
			}
			
			// close btn clicked
			$(".img-close-btn").on("click", function(){
				$(this).parent("div").remove();
			});
			
		} else {	// 이미지외 다른 파일일때
			
			// 파일개수 제한(5개 이상일 땐, 알림창 띄우기)
			if(fileInfoBox.length < 5){
				
				item.attr("id","");
				item.attr("name","articleFile");
				item.attr('data-name', fileName);
				item.parent().prepend("<input type=\"file\" id=\""+itemId+"\" class=\"dis-none\" onchange=\"commentFileUpload(this)\">");
								
				fileListBox.append(
					"<div class=\"upload-file-info martop-10\" data-name=\""+fileName+"\">"
						+ "<dl>"
							+ "<dt>"
								+ "<i class=\"dis-inblock file-icon-s " + className + "\"></i>"
							+ "</dt>"
							+ "<dd>"
								+ "<div class=\"dis-block size-14 color-black\" style=\"height:26px;line-height:26px;\">"
									+ fileName + "<span class=\"marleft-10 color-blue-l\">(" + fileSize +" "+ byte +")</span>"
								+ "</div>"
							+ "</dd>"
						+ "</dl>"
						+ "<i class=\"far fa-times-circle file-close-btn\" onclick=\"commentFileRemove(this)\"></i>"
					+ "</div>"
				);
			} else {
				alert("파일은 5개까지만 등록 가능합니다.");
				item.attr('name','');
				fileValue = null;
			}
			
		}
	}
	
	var imgListBox = $(file).parent().prev().children().eq(-2);
	var imgInfoBox = imgListBox.children();
	
	// 이미지(리스트) 추가하기
	if(fileValue != null){
	}
}

/*******************************************
 * Note : comment file list remove
 * 설명 : 해당 파일을 fileList에서 삭제
 *******************************************/
function commentFileRemove(el){
	var item = $(el);
	var dataName = item.parent().attr('data-name');
	var repBox = item.parent().parent().parent();
	
	item.parent("div").remove();
	
	repBox.find('input[type=file]').each(function(i, e){
		if(repBox.find('input[type=file]').eq(i).attr('data-name') == dataName){
			repBox.find('input[type=file]').eq(i).remove();
			return false;
		}
	});
	
}

/*******************************************
 * Note : comment file delete
 * 설명 : 댓글 수정상태에서 첨부파일을 삭제
 *******************************************/
function commentFileDelete(el){
	var item = $(el);
	var files_no = item.parent("div").data("no");
	var frm = item.parents(".comment-edit-form");
	
	frm.prepend("<input type=\"hidden\" name=\"del_files_no\" value=\""+files_no+"\">");
	item.parent("div").remove();
}

/*******************************************
 * Note : file icon change
 * 설명 : 파일 확장명에 따라 아이콘 변경
 *******************************************/
function fileExCheck(el){
	
	var item = $(el);
	var fileName = item.data("name");
	var fileEx = fileName.split(".").pop().toLowerCase();	// 파일 확장명
	var className;
	
	// 파일확장명 검사 : 확장명에 따라 아이콘 변경
	switch (fileEx) {
	case "hwp" :
		className = "file-hwp";
		break;
	case "xlsx": case "xlsm": case "xlsb": case "xltx": case "xltm": case "xls": case "xlt": case "xlam" :
		className = "file-excel";
		break;
	case "doc": case "docx": case "docm": case "dot": case "dotx": case "dotm" :
		className = "file-word";
		break;
	case "pptx": case "pptm": case "potx": case "potm": case "ppam": case "ppsx": case "ppsm": case "sldx": case "sldm": case "thmx" :
		className = "file-ppt";
		break;
	case "msg": case "pst": case "ost" :
		className = "file-outlook";
		break;
	case "pdf" :
		className = "file-pdf";
		break;
	case "jpeg": case "jpg": case "gif": case "png": case "svg": case "bmp": 
		className = "file-img";
		break;
	case "avi": case "mp4": case "mpg": case "mpeg": case "mpe": case "wmv": case "asf": case "asx": case "flv": case "rm": case "mov": case "dat": case "mkv" :
		className = "file-video";
		break;
	case "aiff": case "aac": case "alac": case "amr": case "wav": case "au": case "awb": case "dvf": case "flac": case "mmf": case "mp3": case "wma" :
		className = "file-audio";
		break;
	case "zip": case "ais": case "arj": case "cab": case "gz": case "hgx": case "jas": case "lzh": case "pak": case "psz": case "rar": case "sea": case "tgz": case "zool": case "jar" :
		className = "file-zip";
		break;
	default:
		className = "file-default";
		break;
	}
	
	item.addClass(className);
}


	
	
	
	
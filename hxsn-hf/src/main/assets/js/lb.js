// JavaScript Document

var picCount = $("#activity").val();
	
	var forEach = function(array, callback){
		for (var i = 0, len = array.length; i < len; i++) { callback.call(this, array[i], i); }
	}
	var st = createPicMove("idContainer2", "idSlider2", picCount);	//图片数量更改后需更改此数值
	var nums = [];
	//插入数字
	for(var i = 0, n = st._count - 1; i <= n;i++){
		var li = document.createElement("li");
		nums[i] = document.getElementById("idNum").appendChild(li);
	}
	//设置按钮样式
	st.onStart = function(){
		//forEach(nums, function(o, i){ o.className = ""})
		forEach(nums, function(o, i){ o.className = st.Index == i ? "new-tbl-cell on" : "new-tbl-cell"; })
	}
	// 重新设置浮动
	$("#idSlider2").css("position","relative");
	
	var _initX = 0;
	var _finishX = 0;
	var _startX = 0;
	var _startY = 0;
	function touchStart(event) {
		_startX = event.touches[0].clientX;
		_startY = event.touches[0].clientY;
		_initX = _startX;
	}
	function touchMove(event) {
		var touches = event.touches;
		var _endX = event.touches[0].clientX;
		var _endY = event.touches[0].clientY;
		if(Math.abs(_endY-_startY)>Math.abs(_endX-_startX)){
			return;		
		}
		event.preventDefault();
		_finishX = _endX;
		var _absX = Math.abs(_endX-_startX);
		var lastX = $('#idSlider2').css('left').replace('px','');
		if(_startX>_endX){
			st.Stop();
			$('#idSlider2').css('left',(parseInt(lastX)-_absX)+'px');
		}else{
			st.Stop();
			$('#idSlider2').css('left',(parseInt(lastX)+_absX)+'px');
		} 
		_startX = _endX;
	}
	//触屏  离开屏幕事件
	function touchEnd(event) {
		if(_finishX==0){
			return;
		}
		if(_initX>_finishX){
			bindEvent(_initX,_finishX);
		}else if(_initX<_finishX){
			bindEvent(_initX,_finishX);
		}
		_initX = 0;
		_finishX = 0;
	}

    /**
     *  绑定触屏触发事件
     * @param start
     * @param end
     */
    function bindEvent(start,end){
         if (start >= end) {
                   st.Next();
                } else {
                    st.Previous();
                }
    }
	st.Run();
	var resetScrollEle = function(){
		$("#shelper").css("width",$("#newkeyword").width()+"px");
		var slider2Li = $("#idSlider2 li");
		slider2Li.css("width",$(".scroll-wrapper").width()+"px");
		$("#shelper").css("width",$("#newkeyword").width()+"px");
	}
	
	window.addEventListener("resize",function(){
		st.Change = st._slider.offsetWidth/st._count;
		st.Next();
		resetScrollEle();
	});
	window.addEventListener("orientationchange",function(){
		st.Change = st._slider.offsetWidth/st._count;
		st.Next();
		resetScrollEle();
	})
	
	
		
	
	$(document).ready(function(){
		$("#categoryMenu li").addClass("route");
	})
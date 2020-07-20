$(function(){
	$(window).resize(infinite);
	function infinite() {
		var htmlWidth = $('html').width();
		if (htmlWidth >= 750) {
			$("html").css({
				"font-size" : "30px"
			});
		} else {
			$("html").css({
				"font-size" :  30 / 750 * htmlWidth + "px"
			});
		}
	}infinite();
});
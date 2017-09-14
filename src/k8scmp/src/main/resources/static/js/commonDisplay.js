$("#common-header").load("/js/common.html #app-header",function(){
	var menuId = $("#commonScript").attr("menu");
	$("a[id="+menuId+"]").parent().addClass("active");
	$('#allMenu li').click(function(){
		$(this).parent().find('li.active').removeClass('active'); 
		$(this).addClass("active");
		
		alert("in");
		//e.preventDefault();
		alert($(this).html());
//		var submenu = $(this).siblings('ul');
//		var li = $(this).parents('li');
//		var submenus = $('#sidebar li.submenu ul');
//		var submenus_parents = $('#sidebar li.submenu');
//		if(li.hasClass('open'))
//		{
//			alert("close");
//			if(($(window).width() > 768) || ($(window).width() < 479)) {
//				submenu.slideUp();
//			} else {
//				submenu.fadeOut(500);
//			}
//			li.removeClass('open');
//		} else 
//		{
//			alert("open");
//			if(($(window).width() > 768) || ($(window).width() < 479)) {
//				submenus.slideUp();			
//				submenu.slideDown();
//			} else {
//				submenus.fadeOut(500);			
//				submenu.fadeIn(500);
//			}
//			submenus_parents.removeClass('open');		
//			li.addClass('open');	
//		}
	});
});
$("#common-footer").load("/js/common.html #app-footer");



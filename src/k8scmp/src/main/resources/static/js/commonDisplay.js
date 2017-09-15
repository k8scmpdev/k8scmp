$("#common-header").load("/js/common.html #app-header",function(){
	var menuId = $("#commonScript").attr("menu");
	$("a[id="+menuId+"]").parent().addClass("active");
	$('#allMenu li').click(function(){
		$(this).parent().find('li.active').removeClass('active'); 
		$(this).addClass("active");
		
		if($(this).children("ul")){
			var submenu = $(this).children("a").siblings('ul');
			var li = $(this);
			var submenus = $('#sidebar li.submenu ul');
			//var submenus_parents = $('#sidebar li.submenu');
			var submenus_parents = submenu.parent();
			if(li.hasClass('open'))
			{
				if(($(window).width() > 768) || ($(window).width() < 479)) {
					submenu.slideUp();
				} else {
					submenu.fadeOut(500);
				}
				li.removeClass('open');
			} else 
			{
				if(($(window).width() > 768) || ($(window).width() < 479)) {
					submenus.slideUp();			
					submenu.slideDown();
				} else {
					submenus.fadeOut(500);			
					submenu.fadeIn(500);
				}
				submenus_parents.removeClass('open');		
				li.addClass('open');	
			}	
		}
	});
});
$("#common-footer").load("/js/common.html #app-footer");



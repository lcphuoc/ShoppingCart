function showDeleteConfirmModal(link, entityName) {
	entityId = link.attr("entityId");//link là 1 đối tượng JQuery -->lấy ra giá trị của thuộc tính entityId

	$("#yesButton").attr("href", link.attr("href"));//gán giá trị của thuộc tính href vào thuộc tính href của thẻ có id là yesButton

	$("#confirmText").text("Are you sure you want to delete this "
		+ entityName + " ID " + entityId + "?");//thay đổi nội dung của thẻ có id là confirmText

	$("#confirmModal").modal();//hiển thị modal
}

function showWarningModal(title, message) {
	$("#modalTitle").text(title);
	$("#modalBody").text(message);
	$("#modalDialog").modal();
}

function clearFilter() {
	window.location = moduleURL;
}

$(document).ready(function(){
	$("#tickall").on("click",function(){
		if($(this).is(':checked')){
			$(".checkBoxDetails").prop('checked',true);
		}else{
			$(".checkBoxDetails").prop('checked',false);
		}
	});

	$("#batchPrinting").on("click",function(e){
		let orderIds ="";
		if($('.checkBoxDetails:checked').length>0){
			$('.checkBoxDetails:checked').each(function() {
				orderIds+=$(this).val() + ",";
			});
			$("#orderIds").val(orderIds);
			document.forms["formOder"].submit(); 
		}
		else{
			showWarningModal("Warning","You Need To Select At Least One Order");
			$("#modalDialog").modal("show")
		}
	})
})

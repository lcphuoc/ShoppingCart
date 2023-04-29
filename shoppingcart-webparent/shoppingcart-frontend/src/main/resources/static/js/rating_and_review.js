totalRating = "[[${totalRating}]]";
productId = "[[${product.id}]]";//sử dụng trong addToCart()
contextPath = "[[@{/}]]";//sử dụng trong addToCart()
var csrfHeaderName = "[[${_csrf.headerName}]]";//X-CSRF-TOKEN, nhấn f12 để xem
var csrfValue = "[[${_csrf.token}]]";//a11dbd49-400e-481d-b66e-d2c7f66550bc, nhấn f12 để xem

function RatingAndReview(form) {
        url = "[[@{/products/saveRatingAndReview}]]";
        rating = parseInt($(".selected-rating").val());
        review = $("#review").val();

    //  csrfValue2 = $("input[name='_csrf']").val();

        params = { rating: rating, review: review,productId: productId, _csrf: csrfValue };

        $.post(url, params, function (response) {
        $(".rating_star").hide();
        child=`<div class="row border rounded p-1 RatingBoard">
                    <div class="col-12">
                        <div>${response.fullName}</div>
                        <div>${response.createdTime}</div>
                        <div class="ratingdetails" th:value="${response.rating}" th:rating = "${response.rating}" th:ratingID = "${response.id}">
                            <span  id="1_${response.id}" class="fa fa-star RatingNew "></span>
                            <span  id="2_${response.id}" class="fa fa-star RatingNew "></span>
                            <span  id="3_${response.id}" class="fa fa-star RatingNew "></span>
                            <span  id="4_${response.id}" class="fa fa-star RatingNew "></span>
                            <span  id="5_${response.id}" class="fa fa-star RatingNew "></span>
                        </div>
                        <div>${response.review}</div>
                    </div>
                </div>
                <hr>
                `
        rating = `${response.rating}`;
        productIdDTO = `${response.id}`;
        $("#RatingBoard").append(child);
        for(let i =1;i<=rating;i++){
            $("#"+i+"_"+productIdDTO).addClass("checked");
        }
        $("#5Star").text("5 Star ("+response.fiveStar+")");
        $("#4Star").text("4 Star ("+response.fourStar+")");
        $("#3Star").text("3 Star ("+response.threeStar+")");
        $("#2Star").text("2 Star ("+response.twoStar+")");
        $("#1Star").text("1 Star ("+response.oneStar+")");

        $("#totalRating").text(response.totalRating);
        for(let i =1;i<=response.totalRating;i++){
            $("#totalRating"+i).addClass("checked");
        }
        showSuccessModal("You Have Successfully Evaluated, Shopping Team Sincerely Thank You ");
        $("#noRating").addClass("d-none");
        $("#RatingBoard").removeClass("d-none");
        }).fail(function () {
        showErrorModal("An Error Occurred During Evaluation");
        });

        return false;
    }

$(document).ready(function() {
    $(".btnViewRating").click(function(){
        url = "[[@{/products/viewRatingByStar}]]";
        ratingStar = $(this).val();
        let check = false;
        params ={ratingStar: ratingStar,productId: productId,_csrf: csrfValue };
        $.post(url,params,function(response){
            $("#RatingBoard").empty();
            $.each(response,function(index,ratingDTO){
                check = true;
                child=`<div class="row border rounded p-1 RatingBoard">
                    <div class="col-12">
                        <div>${ratingDTO.fullName}</div>
                        <div>${ratingDTO.createdTime}</div>
                        <div class="ratingdetails" th:value="${ratingDTO.rating}" th:rating = "${ratingDTO.rating}" th:ratingID = "${ratingDTO.id}">
                            <span  id="1_${ratingDTO.id}" class="fa fa-star RatingNew "></span>
                            <span  id="2_${ratingDTO.id}" class="fa fa-star RatingNew "></span>
                            <span  id="3_${ratingDTO.id}" class="fa fa-star RatingNew "></span>
                            <span  id="4_${ratingDTO.id}" class="fa fa-star RatingNew "></span>
                            <span  id="5_${ratingDTO.id}" class="fa fa-star RatingNew "></span>
                        </div>
                        <div>${ratingDTO.review}</div>
                    </div>
                </div>
                <hr>
                `
                rating = `${ratingDTO.rating}`;
                productIdDTO = `${ratingDTO.id}`;
                $("#RatingBoard").append(child);
                // $(".RatingNew").each(function(){
                // 	$(this).addClass("checked");
                // })
                for(let i =1;i<=rating;i++){
                    $("#"+i+"_"+productIdDTO).addClass("checked");
                }
            })
            if(check){
                $("#noRating").addClass("d-none");
            }else{
                $("#noRating").removeClass("d-none");
            }
        })
    });

    // $(".ratingdetails").each(function(){
    // 	rating = $(this).attr("rating");
    // 	ratingID = $(this).attr("ratingID");
    // 	for(let i = 1;i<=rating;i++){
    // 		$("#"+i+"_"+ratingID).addClass("checked");
    // 	}
    // });

    // for(let i = 1;i<=totalRating;i++){
    // 	$("#totalRating"+i).addClass("checked");
    // }

    $(".btnrating").on('click',(function(e) {

        var previous_value = $("#selected_rating").val();
        
        var selected_value = $(this).attr("data-attr");
        $("#selected_rating").val(selected_value);
        
        $(".selected-rating").empty();
        $(".selected-rating").html(selected_value);
        $(".selected-rating").val(selected_value);

        for (i = 1; i <= selected_value; ++i) {
        $("#rating-star1-"+i).toggleClass('checked');
        }

        for (ix = 1; ix <= previous_value; ++ix) {
        $("#rating-star1-"+ix).toggleClass('checked');
        }
    }));

    bigImage = $("#bigImage");
    
    $(".image-thumbnail").mouseover(function() {//khi hover vào một hình bất kỳ thì set nội dung của thẻ có id là bigImage thành hình đang hover 
        currentImageSource = $(this).attr("src");//lấy giá trị của thuộc tính src hình đang được hover
        currentImageIndex = $(this).attr("index");//lấy giá trị của thuộc tính index hình đang được hover
        
        bigImage.attr("src", currentImageSource);//gán giá trị của thuộc tính src hình đang được hover cho thuộc tính src của bigImage
        bigImage.attr("index", currentImageIndex);//gán giá trị của thuộc tính index hình đang được hover cho thuộc tính index của bigImage
    });
    
    bigImage.on("click", function() {
        $("#carouselModal").modal("show");//khi click vào một hình bất kỳ thì hiển thị modal
        imageIndex = parseInt(bigImage.attr("index"));//lấy giá trị của thuộc tính index hình đang được click vào
        $("#carouselExampleIndicators").carousel(imageIndex);//carousel có nhiều hình thì nó sẽ hiển thị tại vị trí hình đang click vào
    });
});
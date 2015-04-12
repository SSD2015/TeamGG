(function(){
$(function(){

    $('body').on('change', 'input[type=file][data-preview]', function(){
       if(this.files && this.files[0]){
           $('#preview_' + this.getAttribute('data-preview'))
               .attr('src', window.URL.createObjectURL(this.files[0]))
               .one('load', function(){
                   window.URL.revokeObjectURL(this.src);
               });
       }

        $(this).siblings('.btn').addClass('btn-warning').removeClass('btn-default');
    });

});
})();

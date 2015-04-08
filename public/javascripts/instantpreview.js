(function(){
$(function(){

    $('body').on('change', 'input[type=file][data-preview]', function(){
       if(this.files && this.files[0]){
           var reader = new FileReader(), self = this;

           reader.onload = function(e){
               $('#preview_' + self.getAttribute('data-preview')).attr('src', e.target.result);
           };

           reader.readAsDataURL(this.files[0]);
       }

        $(this).siblings('.btn').addClass('btn-warning').removeClass('btn-default');
    });

});
})();

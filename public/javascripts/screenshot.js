(function(){

    var blocker = function(){
        return false;
    }

    $('#ssupload').change(function(){
        if(!this.files || this.files.length === 0){
            return;
        }
        var data = new FormData();
        data.append('file', this.files[0]);
        data.append('csrfToken', $('input[name=csrfToken]').val());

        var container = $(this).closest('.col-sm-4');

        // XXX: jQuery won't work.
        var xhr = new XMLHttpRequest();
        xhr.open('POST', window.ssUploadTarget, true);
        var node = $('<div class="col-sm-4 item"><div class="thumbnail"><img src="/assets/images/ring.svg" class="loading"></div></div>');
        $(node).insertBefore(container);
        container.hide();

        xhr.onload = function(e){
            if(xhr.status == 200){
                var data = JSON.parse(xhr.responseText);
                node.find('img').attr('src', data.file).removeClass('loading');
                node.data('id', data.id);
            }else{
                alert(xhr.responseText);
                node.remove();
            }

            $('form').unbind('submit', blocker);
            container.show();
        };
        xhr.send(data);

        $('form').submit(blocker);

        this.value = null;
    });

    $('#projectForm').submit(function(){
        var order = [];
        $('.screenshot .item').each(function(){
            var id = $(this).data('id');
            if(id){
                order.push(id);
            }
        });
        $('input[name=ssOrder]').val(order.join(','));
    });

    $('.screenshot').sortable({
        items: '.item',
        placeholder: 'col-sm-4 placeholder item'
    }).disableSelection();

    $('.screenshot').on('click', '.del', function(){
        if(!confirm('Delete this image?')){
            return false;
        }

        $(this).closest('.item').remove();
        return false;
    })

})();
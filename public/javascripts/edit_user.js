(function(){
    $('#datatable').on('click', '.editbtn', function(e){
        e.preventDefault();

        var panel = $('#editpanel').show();
        var $this = $(this);

        var data = $this.closest('tr').data('data');
        $.each(data, function (k, v) {
            panel.find('[name=' + k + ']').val(v);
        });

        $('input[name=changepw]').prop('checked', false);
        panel.find('input[name=password]')
            .attr('disabled', true)
            .attr('placeholder', 'Unchanged')
            .val('');

        panel.find('input[name=username]').get(0).focus();
    });

    $('input[name=changepw]').on('change', function(){
        $('#editpanel input[name=password]').attr('disabled', !this.checked)
            .attr('placeholder', this.checked ? 'Use KU login' : 'Unchanged');
    });

    $('#editpanel').hide().on('submit', function(e){
        e.preventDefault();
        var error = $('#error').empty();
        var $this = $(this);
        var data = $this.serialize();
        var submit = $this.find('input[type=submit]').attr('disabled', true);
        $.post(this.getAttribute('action'), data)
            .done(function(){
                window.location = window.location;
            })
            .fail(function(data){
                data = data.responseJSON;
                $.each(data, function(k, v){
                    $('<div class="alert alert-sm alert-danger" />')
                        .text(k + ': ' + v)
                        .appendTo(error);
                });
            })
            .always(function(){
                submit.attr('disabled', false);
            });
    });
    $('#deleteuser').on('click', function(){
        var form = $(this).closest('form');
        var id = form.find('input[name=id]').val();

        if(id == window.myId){
            alert('You may not delete yourself');
            return;
        }

        if(!confirm('Delete selected user?')){
            return;
        }

        var self = this;
        var error = $('#error').empty();
        this.disabled = true;
        $.post(form.attr('action'), {
            id: id,
            csrfToken: form.find('input[name=csrfToken]').val(),
            'delete': true
        })
            .done(function(){
                window.location = window.location;
            })
            .fail(function(data){
                data = data.responseJSON;
                $.each(data, function(k, v){
                    $('<div class="alert alert-sm alert-danger" />')
                        .text(k + ': ' + v)
                        .appendTo(error);
                });
            }).always(function(){
                self.disabled = false;
            });
    });
})();
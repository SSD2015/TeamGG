$(function(){
    var settings = {
        toolbar: 'top',
        buttons: {
            bold: {
                title: 'Bold (Ctrl+B)',
                image: '\uf032',
                hotkey: 'b'
            },
            italic: {
                title: 'Italic (Ctrl+I)',
                image: '\uf033',
                hotkey: 'i'
            },
            underline: {
                title: 'Underline (Ctrl+U)',
                image: '\uf0cd',
                hotkey: 'u'
            },
            strikethrough: {
                title: 'Strikethrough (Ctrl+S)',
                image: '\uf0cc',
                hotkey: 's'
            },
            forecolor: {
                title: 'Text color',
                image: '\uf1fc'
            },
            highlight: {
                title: 'Background color',
                image: '\uf043'
            },
            alignleft: {
                title: 'Left',
                image: '\uf036'
            },
            aligncenter: {
                title: 'Center',
                image: '\uf037'
            },
            subscript: {
                title: 'Subscript',
                image: '\uf12c'
            },
            superscript: {
                title: 'Superscript',
                image: '\uf12b'
            },
            indent: {
                title: 'Indent',
                image: '\uf03c'
            },
            outdent: {
                title: 'Outdent',
                image: '\uf03b'
            },
            orderedList: {
                title: 'Ordered list',
                image: '\uf0cb'
            },
            unorderedList: {
                title: 'Unordered list',
                image: '\uf0ca'
            },
            insertimage: {
                title: 'Insert image',
                image: '\uf030'
            },
            insertvideo: {
                title: 'Insert video',
                image: '\uf03d'
            },
            insertlink: {
                title: 'Insert link',
                image: '\uf08e'
            },
            removeformat: {
                title: 'Remove format',
                image: '\uf12d'
            }
        },
        submit: {
            title: 'Submit',
            image: '\uf00c' // <img src="path/to/image.png" width="16" height="16" alt="" />
        },
        maxImageSize: [600, 200],
        videoFromUrl: function(url) {
            // youtube - http://stackoverflow.com/questions/3392993/php-regex-to-get-youtube-video-id
            var youtube_match = url.match( /^(?:http(?:s)?:\/\/)?(?:[a-z0-9.]+\.)?(?:youtu\.be|youtube\.com)\/(?:(?:watch)?\?(?:.*&)?v(?:i)?=|(?:embed|v|vi|user)\/)([^\?&\"'>]+)/ );
            if( youtube_match && youtube_match[1].length == 11 )
                return '<iframe src="//www.youtube.com/embed/' + youtube_match[1] + '" width="640" height="360" frameborder="0" allowfullscreen></iframe>';

            // vimeo - http://embedresponsively.com/
            var vimeo_match = url.match( /^(?:http(?:s)?:\/\/)?(?:[a-z0-9.]+\.)?vimeo\.com\/([0-9]+)$/ );
            if( vimeo_match )
                return '<iframe src="//player.vimeo.com/video/' + vimeo_match[1] + '" width="640" height="360" frameborder="0" webkitAllowFullScreen mozallowfullscreen allowFullScreen></iframe>';

            // dailymotion - http://embedresponsively.com/
            var dailymotion_match = url.match( /^(?:http(?:s)?:\/\/)?(?:[a-z0-9.]+\.)?dailymotion\.com\/video\/([0-9a-z]+)$/ );
            if( dailymotion_match )
                return '<iframe src="//www.dailymotion.com/embed/video/' + dailymotion_match[1] + '" width="640" height="360" frameborder="0" webkitAllowFullScreen mozallowfullscreen allowFullScreen></iframe>';
        },
        placeholderUrl: 'https://www.youtube.com/watch?v=...',
        placeholderEmbed: '<embed />',
    };

    $('#description').wysiwyg(settings);
});
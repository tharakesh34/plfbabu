CKEDITOR.editorConfig = function(config) {
    config.resize_enabled = false;
    config.scayt_autoStartup = true;
    config.readOnly = false;
    config.toolbar = 'Simple';
    config.toolbar_Simple = [ 
                                { name: 'basicstyles', items : [ 'Bold','Italic','Underline','Strike','-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'] },
                                { name: 'paragraph', items : [ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote','-','Undo','Redo'] },
                                { name: 'paragraph', items : [ 'TextColor','BGColor','-','SpecialChar','PageBreak','-','SpellChecker','Scayt'] }
                            ];
 
 };
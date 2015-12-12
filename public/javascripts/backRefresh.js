$(document).ready(function(e) {
    var $input = $('#refresh');

    $input.val() == 'yes' ? location.reload(true) : $input.val('yes');
});
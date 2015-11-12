window.setInterval("refreshDiv()", 500);

function refreshDiv() {
    $('#current').load(document.URL +  ' #current');
    $('#bestLaps').load(document.URL +  ' #bestLaps');
    $('#bestMinutes').load(document.URL +  ' #bestMinutes');
}
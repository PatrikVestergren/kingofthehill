
function refreshDiv() {
    $('.current').load(document.URL +  ' .current');
    $('.totalLaps').load(document.URL +  ' .totalLaps');
    $('.bestLaps2wd').load(document.URL +  ' .bestLaps2wd');
    $('.bestMinutes2wd').load(document.URL +  ' .bestMinutes2wd');
    $('.bestLaps4wd').load(document.URL +  ' .bestLaps4wd');
    $('.bestMinutes4wd').load(document.URL +  ' .bestMinutes4wd');
    $('.bestLapsUnknown').load(document.URL +  ' .bestLapsUnknown');
    $('.bestMinutesUnknown').load(document.URL +  ' .bestMinutesUnknown');
}

var event = new EventSource("/updates");
   event.addEventListener('message', function( event ) {
       refreshDiv();
   }
,false);

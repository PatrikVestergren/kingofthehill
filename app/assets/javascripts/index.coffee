$ ->
    $.get "/getLaps", (laps) ->
        $.each laps, (index, lap) ->
            $("#best2wd").append $("<tr><td class=\"pos\">" + (index + 1) + "</td><td class=\"driver\">" + lap.name + "</td><td class=\"col\">" + 11 + "</td><td class=\"col\">" + 5.23 + "</td><td class=\"col\">" + "2015-01-01" + "</td</tr>")


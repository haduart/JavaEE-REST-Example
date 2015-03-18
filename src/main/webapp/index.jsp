<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>REST API Console</title>

    <style>
        .log-entry {
            border: 1px dashed gray;
            margin: 1em;
        }
    </style>

    <script>
        "use strict";
        ( function (scope) {

            function doRequest(ev) {
                ev.preventDefault();
                ev.stopPropagation();

                var method = document.getElementById("request-method").value;
                var url = document.getElementById("request-url").value;
                var type = document.getElementById("request-type").value;
                var content = document.getElementById("request-content").value;

                var xhr = new XMLHttpRequest();
                xhr.onreadystatechange = function (ev) {
                    if (xhr.readyState===4) {
                        printResponse(method, url, xhr);
                    }
                };
                xhr.open(method, url);
                xhr.setRequestHeader("Content-Type", type);
                if (method=="GET" || method=="HEAD" || method=="DELETE") {
                    xhr.send("");
                } else {
                    xhr.send(content);
                }
            }

            function printResponse(method, url, xhr) {
                var log = document.getElementById("response-content");

                var logEntry = document.createElement("div");
                logEntry.setAttribute("class", "log-entry");

                var header = document.createElement("pre");
                header.textContent = method + " " + url + " " + xhr.status + " " + xhr.statusText + "\n";
                logEntry.appendChild(header);

                var content = document.createElement("pre");
                if (xhr.status < 300 && xhr.getResponseHeader("Content-Type") == "application/json") {
                    content.textContent = JSON.stringify(JSON.parse(xhr.responseText), null, 2);
                } else {
                    content.textContent = xhr.responseText + "\r\n\r\n";
                }
                highlightURLs(content.firstChild);
                logEntry.appendChild(content);

                log.insertBefore(logEntry, log.firstChild);
            }

            function highlightURLs(content) {
                if (content.nodeType != content.TEXT_NODE) {
                    return;
                }

                var searchNode = content;

                do {
                    var res = /\"(\/[^\"]+)\"/.exec(searchNode.nodeValue);
                    if (res) {
                        var before = searchNode.nodeValue.substring(0, res.index+1);
                        var link = searchNode.nodeValue.substring(res.index+1, res.index+res[0].length-1);
                        var after = searchNode.nodeValue.substring(res.index+res[0].length-1, searchNode.nodeValue.length);

                        var beforeNode = document.createTextNode(before);
                        var linkNode = document.createElement("a");
                        linkNode.setAttribute("href", link);
                        linkNode.textContent = link;
                        var afterNode = document.createTextNode(after);

                        var parent = searchNode.parentNode;
                        parent.removeChild(searchNode);
                        parent.appendChild(beforeNode);
                        parent.appendChild(linkNode);
                        parent.appendChild(afterNode);

                        searchNode = afterNode;
                    }
                } while (res);
            }

            window.addEventListener("load", function (ev) {
                document.getElementById("request-form").addEventListener("submit", doRequest, false);
                document.getElementById("request-clear").addEventListener("click", function (ev) {
                    ev.preventDefault();
                    ev.stopPropagation();
                    var responses = document.getElementById("response-content");
                    while (responses.firstChild) {
                        responses.removeChild(responses.firstChild);
                    }
                }, false);
                document.getElementById("response-content").addEventListener("click", function (ev) {
                    if (ev.target && ev.target.nodeName && ev.target.nodeName == "A") {
                        ev.preventDefault();
                        ev.stopPropagation();
                        document.getElementById("request-method").value = "GET";
                        document.getElementById("request-url").value = ev.target.getAttribute("href");
                    }
                }, false);
            }, false);
        }) (window);
    </script>

</head>

<body>
<h1>REST API Console</h1>

<form id="request-form">
    <p><input type="text" id="request-method" value="GET" placeholder="method"></p>
    <p><input type="text" id="request-url" value="/RESTExample/api/" size="80" placeholder="url"></p>
    <p><input type="text" id="request-type" value="application/json" size="80" placeholder="media type"></p>
    <p><textarea id="request-content" cols="80" rows="10"></textarea></p>
    <p><input type="submit" id="request-send" value="Send"> <input type="submit" id="request-clear" value="Clear"></p>
</form>

<div id="response-content"> </div>

</body>
</html>

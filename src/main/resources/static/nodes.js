var network;
var state;
var nodesCache;

var UISTATE = "";
var toJoin = 0;

function generateGraph(redrawInsteadInit) {
    var xhr = new XMLHttpRequest();
    xhr.open('GET', '/graph');
    xhr.send(null);

    xhr.onreadystatechange = function () {
      var DONE = 4;
      var OK = 200;
      if (xhr.readyState === DONE) {
        if (xhr.status === OK) {
          init(JSON.parse(xhr.responseText), redrawInsteadInit);
        } else {
          console.log('Error: ' + xhr.status);
        }
      }
    };
}

function init(graph, redrawInsteadInit) {

    nodesCache = graph.nodes;

    var container = document.getElementById('mynetwork');
    var data = {
        nodes: graph.nodes,
        edges: graph.edges
    };
    var options = {
        nodes: {
            shape: 'dot',
            size: 16
        },
        physics: {
            forceAtlas2Based: {
                gravitationalConstant: -26,
                centralGravity: 0.005,
                springLength: 230,
                springConstant: 0.18
            },
            maxVelocity: 146,
            solver: 'forceAtlas2Based',
            timestep: 0.35,
            stabilization: {iterations: 150}
        },
        layout: {
            randomSeed: 0
        }
    };

    if (!redrawInsteadInit) {
        network = new vis.Network(container, data, options);

        network.on("selectNode", function (params) {

            var selectedNodeId = network.getSelection().nodes[0];
            var selectedNode = nodesCache.filter(n => n.id == selectedNodeId);

            $('#card .header').text("#" + selectedNode[0].id + " -> " + selectedNode[0].type);
            $('#card .meta').text(selectedNode[0].date);
            $('#card .description').text(selectedNode[0].label);

            if (UISTATE == "JOIN") {
                sendRequest('PATCH', "/node/join/" + network.getSelection().nodes[0] + "?toBeLinked=" + toJoin, "", false, function() {
                    console.log("hahhaha")
                    UISTATE = "";
                    toJoin = 0;
                    updateGraph();
                });

            }

        });
    } else {
        network.setData(data);
    }
}

var updateGraph = function() {
    generateGraph(true);
}

function keyPressHandler(event) {

    var input = document.getElementById("command-line");
    var inputValue = input.value;

    if (state === "FIND" && inputValue != "") {
        var filteredNodeIds = [];

        for (var i = 0; i < nodesCache.length; i++) {
            if (nodesCache[i].label.includes(inputValue)) {
                filteredNodeIds.push(nodesCache[i].id);
            }
        }

        network.selectNodes(filteredNodeIds);

        if (event.key === "Enter") {
            input.value = "";
            setNextState(input);
        }

        return false;
    }

    if(!(event instanceof KeyboardEvent) || event.key !== "Enter") {
        return true;
    }

    if (inputValue === "") {
        // handle blank enter case
        setNextState(input);
        return false;
    }

    var connectionIds = [];
    for (var i = 0; i < network.getSelection().nodes.length; i++) {
        connectionIds.push(network.getSelection().nodes[i]);
    }

    var content = {
        peerIds: connectionIds.join(),
        content: inputValue
    };

    // add node
    sendRequest('POST', '/node', content, isJson = true, function(xhr) {
        updateGraph();
        input.value = "";
    });


    return false;
}

function setNextState(input) {
    sendRequest('GET', '/state/next', "", isJson = false, function(xhr) {
        input.placeholder = xhr.responseText;
        state = xhr.responseText;
    });
}

function find_in_object(my_object, my_criteria){

  return my_object.filter(function(obj) {
    return obj['label'].includes(my_criteria);
  });

}

function sendRequest(method, endpoing, content, isJson, callback) {
    var xhr = new XMLHttpRequest();
    xhr.open(method, endpoing);
    if (isJson) {
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.send(JSON.stringify(content));
    } else {
        xhr.send(null);
    }

    xhr.onreadystatechange = function () {
      var DONE = 4;
      var OK = 200;
      if (xhr.readyState === DONE) {
        if (xhr.status === OK) {
          callback(xhr);
        } else {
          console.log('Error: ' + xhr.status);
        }
      }
    };
}

// CLICK HANDLERS
$( document ).ready(function() {
    $('#remove-node').click(function() {
        $('.ui.basic.modal').modal('show');
    });

    $('#join-node').click(function() {
       UISTATE = "JOIN";
       toJoin = network.getSelection().nodes[0];
    });

    $('#delete-selected-node').click(function() {
        network.getSelection().nodes[0];

        sendRequest('DELETE', "/node/" + network.getSelection().nodes[0], "", false, function() {
            console.log("hahhaha")
            updateGraph();
        });
    });
});








var network;
var state;
var nodesCache;
var edgesCache;

var UISTATE = "";
var toJoin = 0;

function generateGraph() {
    var xhr = new XMLHttpRequest();
    xhr.open('GET', '/graph');
    xhr.send(null);

    xhr.onreadystatechange = function () {
      var DONE = 4;
      var OK = 200;
      if (xhr.readyState === DONE) {
        if (xhr.status === OK) {
          init(JSON.parse(xhr.responseText));
        } else {
          console.log('Error: ' + xhr.status);
        }
      }
    };
}

function init(graph) {

    nodesCache = new vis.DataSet(graph.nodes);
    edgesCache = new vis.DataSet(graph.edges);

    var container = document.getElementById('mynetwork');
    var options = {
            nodes: {
                shape: 'dot',
                size: 16
            },
            physics: {
                forceAtlas2Based: {
                    gravitationalConstant: -20,
                    centralGravity: 0.003,
                    springLength: 160,
                    springConstant: 0.1
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

    var data = {
        nodes: nodesCache,
        edges: edgesCache,
        options: options
    };

    network = new vis.Network(container, data, options);

    network.on("selectNode", function (params) {

        var selectedNodeId = network.getSelection().nodes[0];
        var selectedNode = nodesCache.get(selectedNodeId);

        $('#card .header').text("#" + selectedNode.id + " -> " + selectedNode.type);
        $('#card .meta').text(selectedNode.date + " | mass: " + selectedNode.mass);
        $('#card .description').text(selectedNode.label);

        if (UISTATE == "JOIN") {
            sendRequest('PATCH', "/node/join/" + network.getSelection().nodes[0] + "?toBeLinked=" + toJoin, "", false, function() {
                UISTATE = "";
                toJoin = 0;
                // TODO refactor
                updateGraph({
                    edges: [{from: toJoin, to: network.getSelection().nodes[0]}]
                }, false);
            });

        }

    });
}

var updateGraph = function(data, isRemove) {
    // TODO check if array
    //      also generally sort out tis concept
    if (data.hasOwnProperty("edges")) {
        if (isRemove) {
            // TODO traverse through array
            edgesCache.remove(data.edges[0].id);
        } else {
            edgesCache.update(data.edges);
        }
    } else if(data.hasOwnProperty("nodes")) {
        if (isRemove) {
            nodesCache.remove(data.nodes[0].id);
        } else {
            nodesCache.update(data.nodes);
        }
    } else {
        // TODO throw error
    }

}

function keyPressHandler(event) {

    var input = document.getElementById("command-line");
    var inputValue = input.value;

    if (state === "FIND" && inputValue != "") {
        var filteredNodeIds = [];

        nodesCache.forEach(function(node) {
            if (node.label.includes(inputValue)) {
                filteredNodeIds.push(node.id);
            }
        });

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
        updateGraph({
            nodes: [JSON.parse(xhr.response)]
        }, false);
    });
    input.value = "";


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
            var node = nodesCache.get(network.getSelection().nodes[0]);
            updateGraph({
                nodes: [node]
            }, true);
        });
    });
});








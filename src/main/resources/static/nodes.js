var network;
var state;
var nodesCache;

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
        }
    };
    network = new vis.Network(container, data, options);
}

var updateGraph = function() {
    generateGraph();
}

function keyPressHandler(event) {

    var input = document.getElementById("command-line");
    var inputValue = input.value;

    if (state === "FIND") {
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
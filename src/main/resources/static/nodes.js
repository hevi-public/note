var network;

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

function updateGraph() {
    generateGraph();
}

function keyPressHandler(event) {
    if(!(event instanceof KeyboardEvent) || event.key !== "Enter") {
        return true;
    }

    var input = document.getElementById("command-line");
    var inputValue = input.value;

    if (inputValue === "") {
        // handle blank enter case
        return false;
    }

    var content = {
        peerId: network.getSelection().nodes[0],
        content: inputValue
    };

    // add node
    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/node');
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify(content));

    xhr.onreadystatechange = function () {
      var DONE = 4;
      var OK = 200;
      if (xhr.readyState === DONE) {
        if (xhr.status === OK) {
          updateGraph()
          input.value = "";
        } else {
          console.log('Error: ' + xhr.status);
        }
      }
    };


    return false;
}
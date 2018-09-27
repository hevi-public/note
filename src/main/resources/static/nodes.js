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

    document.getElementById('command-line-status').text = 'I think...';
}

function init(graph) {

    nodesCache = new vis.DataSet(graph.nodes);
    edgesCache = new vis.DataSet(graph.edges);

    var container = document.getElementById('mynetwork');
    var options = {
            nodes: {
                shape: 'dot',
                size: 16,
                scaling: {
                    customScalingFunction: function (min,max,total,value) {
                        return value/total;
                    },
                    min:5,
                    max:500
                },
                font: {
                    color: '#dddddd'
                }
            },
            physics: {
                forceAtlas2Based: {
                    gravitationalConstant: -20,
                    centralGravity: 0.003,
                    springLength: 180,
                    springConstant: 0.5
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
        $('#card .meta').text(selectedNode.date + " | value: " + selectedNode.value);
        $('#card .description').text(selectedNode.label);

        if (UISTATE == "JOIN") {
            sendRequest('PATCH', "/node/join/" + network.getSelection().nodes[0] + "?toBeLinked=" + toJoin, "", false, function() {
                // TODO refactor
                updateGraph({
                    edges: [{from: toJoin, to: network.getSelection().nodes[0]}]
                }, false);
                UISTATE = "";
                toJoin = 0;
                setCursorByID(document.body, 'default');
            });

        }

    });

    network.on("click", function (params) {
        $(function () {
            $('input').blur();
        });
    });
}

var updateGraph = function(data, isRemove) {
    // TODO check if array
    //      also generally sort out this concept
    if (data.hasOwnProperty("edges")) {
        if (isRemove) {
            // TODO traverse through array
            edgesCache.remove(data.edges[0].id);
        } else {
            edgesCache.update(data.edges[0]);
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

function setCursorByID(id,cursorStyle) {
    if (id.style) id.style.cursor=cursorStyle;
}

function changeSelectedNodeType(type) {
    if (network.getSelection().nodes.length === 0) return;

    var selectedNodeId = network.getSelection().nodes[0];
    var selectedNode = nodesCache.get(selectedNodeId);

    sendRequest('PATCH', "/node/" + selectedNodeId + "/type/" + type, "", false, function(xhr) {
        var graphResponse = JSON.parse(xhr.response);
        nodesCache.remove(selectedNodeId);
        nodesCache.add(graphResponse.nodes);
    });
}

function setNextState(input) {
    sendRequest('GET', '/state/next', "", isJson = false, function(xhr) {
        document.getElementById('command-line-status').text = xhr.responseText;
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
        confirmDeleteSelectedNode();
    });

    $('#join-node').click(function() {
       UISTATE = "JOIN";
       toJoin = network.getSelection().nodes[0];
    });

    $('#delete-selected-node').click(function() {
        if (network.getSelectedNodes().length > 0) {
            sendRequest('DELETE', "/node/" + network.getSelection().nodes[0], "", false, function() {
                var node = nodesCache.get(network.getSelection().nodes[0]);
                updateGraph({
                    nodes: [node]
                }, true);
            });
        } else {
            var selectedEdgeId = network.getSelectedEdges()[0];
            var selectedEdge = edgesCache.get(selectedEdgeId);
            sendRequest('DELETE', "/node/link/between/" + selectedEdge.from + "/and/" + selectedEdge.to, "", false, function() {
                edgesCache.remove(selectedEdgeId);
            });
        }
    });
});

var confirmDeleteSelectedNode = function() {
    $('.ui.basic.modal').modal('show');
}

function find(nodesCache, inputValue) {
    var filteredNodeIds = [];

    nodesCache.forEach(function(node) {
        if (inputValue != "" && node.label.toLowerCase().indexOf(inputValue.toLowerCase()) !== -1) {
            filteredNodeIds.push(node.id);
        }
    });

    return filteredNodeIds;
}

function searchInputKeyPressHandler(event) {

    var input = document.getElementById("search_input");
    var inputValue = input.value;

    var filteredNodeIds = find(nodesCache, inputValue)

    var html = "";

    if (inputValue != "") {
        network.selectNodes(filteredNodeIds);
    } else {
        // TODO check if filteredNodeIds are empty or not when inputValue is empty
        // it feels like it matches everything when searching for empty string
        // if fixed, this call shouldn't be needed
        //
        // Seems like we need it after all
        network.unselectAll();
    }

    network.fit({
        nodes: filteredNodeIds,
        animation: {
            duration: 500,
            easingFunction: 'easeInQuad'
        }
    });

    // TODO move it away
    var source   = document.getElementById("feed-search-element").innerHTML;
    var template = Handlebars.compile(source);

    var nodes = []
    for (var i = 0; i < network.getSelectedNodes().length; i++) {
        var nodeId = network.getSelectedNodes()[i]
        nodes.push(nodesCache.get(nodeId))
    }

    var context = nodes

    var html = template(context);
    $('#search_feed_element_container').html(html);
    //document.getElementById('search_feed_element_container').innerHTML = template(context);

    return event;
}


function textInputKeyPressHandler(event) {

    var input = document.getElementById("command-line");
    var inputValue = input.value;

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

function bodyKeyPressHandler(event) {
    if (event.ctrlKey === true) {
        switch (event.key) {
            case 'd':
                confirmDeleteSelectedNode();
                break;
            case 's':
                UISTATE = "JOIN";
                toJoin = network.getSelection().nodes[0];
                setCursorByID(document.body, 'crosshair');
                break;
            case 'p':
                $('.ui.sidebar')
                    .sidebar('setting', 'transition', 'overlay')
                    .sidebar('toggle');
                break;
            case '1':
                changeSelectedNodeType('NODE');
                break;
            case '2':
                changeSelectedNodeType('CATEGORY');
                break;
        }
    }
}

var beforeSearchView;

function focusOnNode(event) {
    var selectedId = event.target.dataset.id

    network.fit({
        nodes: [selectedId],
        animation: {
            duration: 300,
            easingFunction: 'easeInQuad'
        }
    });
}

function feed_mouse_enter_handler(event) {
    if (!beforeSearchView) {
        beforeSearchView = {
            position: network.getViewPosition(),
            scale: network.getScale()
        }
    }
}

function feed_mouse_leave_handler(event) {
    network.moveTo({
        position: beforeSearchView.position,
        scale: beforeSearchView.scale,
        animation: {
            duration: 500,
            easingFunction: 'easeInQuad'
        }
    });
    beforeSearchView = null;
}
var createGraph = require('./ngraph.graph.min');
var npath = require('./ngraph.path.min');

global.astar_function = {
    test1: function test1() {

        let graph = createGraph();

        graph.addLink('a', 'b', {weight: 3});
        graph.addLink('a', 'c', {weight: 10});
        graph.addLink('c', 'd', {weight: 5});
        graph.addLink('b', 'd', {weight: 10});


        let pathFinder = npath.aStar(graph, {
            distance(a, b, link) {
                return link.data.weight;
            }
        });
        let path = pathFinder.find('a', 'd');

        // console.log(path[0].id);
        // console.log(path[1].id);
        // console.log(path[2].id);
        return path;
    },

    test2: function test2() {
        let graph = createGraph();

        // We want to find a path from a to e.
        // a -> b <- e
        //  \       /
        //   c -> d
        // In undirected graph the `a, b, e` will be the solution.
        // In directed graph it sohuld be `a c d e`
        graph.addLink('a', 'b');
        graph.addLink('e', 'b');
        graph.addLink('a', 'c');
        graph.addLink('c', 'd');
        graph.addLink('d', 'e');

        let pathFinder = npath.aStar(graph, {
            oriented: true
        });
        let path = pathFinder.find('a', 'e');

        return path;
    },

    test3: function test3() {
        let graph = createGraph();

        // Our graph has cities:
        graph.addNode('NYC', {x: 0, y: 0});
        graph.addNode('Boston', {x: 1, y: 1});
        graph.addNode('Philadelphia', {x: -1, y: -1});
        graph.addNode('Washington', {x: -2, y: -2});

        // and railroads:
        graph.addLink('NYC', 'Boston');
        graph.addLink('NYC', 'Philadelphia');
        graph.addLink('Philadelphia', 'Washington');

        var pathFinder = npath.aStar(graph, {
            distance: this.distance,
            heuristic: this.distance
        });
        let path = pathFinder.find('NYC', 'Washington');

        return path;
    },

    graph: {},
    xyBBox: new BBox(),
    ractor: 1,
    initGraph: function initGraph(nodeData, edgeData) {
        this.graph = createGraph();

        for (let i = 0; i < nodeData.length; i++) {
            let id = nodeData[i].nodeId;
            let x = nodeData[i].lon*this.ractor;
            let y = nodeData[i].lat*this.ractor;
            this.graph.addNode(id, {x, y});
            // this.xyBBox.addPoint(x, y);
        }

        for (let i = 0; i < edgeData.length; i++) {
            let edge = edgeData[i];
            this.graph.addLink(edge.node1, edge.node2, {weight: edge.distance});
        }

        // this.moveCoordTo0();
    },

    findPath: function findPath(fromId, toId) {
        let pathFinder = npath.aStar(this.graph, {
            distance(a, b, link) {return link.data.weight;}
        });

        let nodes = pathFinder.find(fromId, toId);
        return nodes;
        // return this.unMoveCoordTo0(nodes);
    },

    distance: function distance(a, b) {
        let dx = a.x - b.x;
        let dy = a.y - b.y;

        return Math.sqrt(dx * dx + dy * dy);
    },

    moveCoordTo0: function moveCoordTo0() {
        this.graph.forEachNode(node => {
            node.data.x = node.data.x - this.xyBBox.cx;
            node.data.y = node.data.y - this.xyBBox.cy;
        });
    },

    unMoveCoordTo0: function unMoveCoordTo0(nodes) {
        for (let i=0; i<nodes.length; i++){
            nodes[i].data.x = (nodes[i].data.x + this.xyBBox.cx)/this.ractor;
            nodes[i].data.y = (nodes[i].data.y + this.xyBBox.cy)/this.ractor;
        }

        return nodes;
    },
};
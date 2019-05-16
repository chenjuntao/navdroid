var aStar = require('./ngraph.path.min').aStar;
var createGraph = require('./ngraph.graph.min');

global.mytest = {
    test1:function test1() {

        let graph = createGraph();

        graph.addLink('a', 'b', {weight: 3});
        graph.addLink('a', 'c', {weight: 10});
        graph.addLink('c', 'd', {weight: 5});
        graph.addLink('b', 'd', {weight: 10});


        let pathFinder = aStar(graph, {
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

    test2 : function test2() {
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


        let pathFinder = aStar(graph, {
            oriented: true
        });
        let path = pathFinder.find('a', 'e');

        return path;
    },

    test3 : function test3() {
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

        var pathFinder = aStar(graph, {
            distance(fromNode, toNode) {
                // In this case we have coordinates. Lets use them as
                // distance between two nodes:
                let dx = fromNode.data.x - toNode.data.x;
                let dy = fromNode.data.y - toNode.data.y;

                return Math.sqrt(dx * dx + dy * dy);
            },
            heuristic(fromNode, toNode) {
                // this is where we "guess" distance between two nodes.
                // In this particular case our guess is the same as our distance
                // function:
                let dx = fromNode.data.x - toNode.data.x;
                let dy = fromNode.data.y - toNode.data.y;

                return Math.sqrt(dx * dx + dy * dy);
            }
        });
        let path = pathFinder.find('NYC', 'Washington');

        return path;
    }
}
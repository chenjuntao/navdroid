const path = require('path');
var webpack = require('webpack');

module.exports = {

    entry:[
        "./astar.js",
        "./ngraph.graph.min.js",
        "./ngraph.path.min.js"
    ],
    output:{
        filename:'bundle.js',
        path:path.resolve(__dirname,'dist'),
    },
    optimization: {
        minimize: true
    },
    // plugins: [
    //     new webpack.ProvidePlugin({
    //         ttt: "test"
    //     })
    // ]
};
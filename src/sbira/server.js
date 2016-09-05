var webpack = require('webpack');
var WebpackDevServer = require('webpack-dev-server');
var config = require('./webpack.config');

var host = 'localhost'; // if you change here - also edit webpack config
var backendTarget = 'https://localhost:8080/';

new WebpackDevServer(webpack(config), {
    publicPath: config.output.publicPath,
    colors: true,
    stats: {colors: true},
    progress: true,
    'display-error-details': true,
    hot: true,
    historyApiFallback: true,
    compress: true,
    proxy: {
        '/task/*': {
            target: backendTarget,
            secure:false
        },
        '/pub/*': {
            target: backendTarget,
            secure:false
        },
        '/login/*': {
            target: backendTarget,
            secure:false
        },
        '/project/*': {
            target: backendTarget,
            secure:false
        },
        '/user/*': {
            target: backendTarget,
            secure:false
        }
    }
}).listen(80, host, function(err, result) {
    if (err) {
        return console.log(err);
    }
    console.log('Listening at http://' + host + ':80/');
});

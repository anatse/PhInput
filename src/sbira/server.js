var webpack = require('webpack');
var WebpackDevServer = require('webpack-dev-server');
var config = require('./webpack.config');

var host = 'localhost'; // if you change here - also edit webpack config
var servicesHost = 'localhost:8080/';

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
            target: 'http://' + servicesHost
        },
        '/pub/*': {
            target: 'http://' + servicesHost
        },
        '/login/*': {
            target: 'http://' + servicesHost
        }
    }
}).listen(80, host, function(err, result) {
    if (err) {
        return console.log(err);
    }
    console.log('Listening at http://' + host + ':80/');
});

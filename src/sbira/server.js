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
            target: 'https://' + servicesHost,
            secure:false
        },
        '/pub/*': {
            target: 'https://' + servicesHost,
            secure:false
        },
        '/login/*': {
            target: 'https://' + servicesHost,
            secure:false
        },
        '/project/*': {
            target: 'https://' + servicesHost,
            secure:false
        }
    }
}).listen(80, host, function(err, result) {
    if (err) {
        return console.log(err);
    }
    console.log('Listening at http://' + host + ':80/');
});

var path = require('path');
var webpack = require('webpack');
var ExtractTextPlugin = require('extract-text-webpack-plugin');

var _js = 'js'; // путь до папки с JS компонентами

module.exports = {
  devtool: 'source-map', // or cheap-inline-module-source-map
  // context: __dirname + '/',
  entry: {
    main: './index'
  },
  output: {
    path: path.join(__dirname, '../webapp/sbira'),
    filename: '[name].js',
    publicPath: '/pub/sbira/'
  },
  plugins: [
    new webpack.HotModuleReplacementPlugin(),
    new webpack.DefinePlugin({
      'process.env': {
        NODE_ENV: '"production"'
      },

      __CLIENT__: true,
      __SERVER__: false,
      __DEVELOPMENT__: false,
      __DEVTOOLS__: false
    }),
    // optimizations
    new webpack.optimize.DedupePlugin(),
    new webpack.optimize.OccurenceOrderPlugin(),
    new webpack.optimize.UglifyJsPlugin({
      compress: {
        warnings: false
      }
    }),
    new ExtractTextPlugin('[name].css')
  ],
  progress: true,
  colors: true,
  resolve: {
    // root: path.resolve(__dirname, '../../'),
    extensions: ['', '.webpack.js', '.web.js', '.js', '.jsx', '.es6'],
    alias: {
      'img': path.join(__dirname, 'img')
    }
  },
  module: {
    loaders: [{
        test: /\.jsx?$/,
        loaders: ['react-hot', 'babel'],
        include: [
          path.resolve(__dirname, _js),
          path.resolve(__dirname, 'App'),
        ]
      }, {
        test: /\.(?:js|es).?$/,
        exclude: [/node_modules/, /.less$/],
        loader: 'babel-loader',
        query: {
          presets: [
            require.resolve('babel-preset-es2015'),
            require.resolve('babel-preset-react'),
            require.resolve('babel-preset-stage-0')
          ]
        }
      }, {
        test: /\.scss$/,
        loader: ExtractTextPlugin.extract(
            'style', // The backup style loader
            'css?sourceMap!sass?sourceMap'
          )
          // loaders: [ 'style', 'css?sourceMap', 'sass?sourceMap' ]
      }, {
        test: /\.less$/,
        loader: // 'style-loader!css-loader!less-loader'
        ExtractTextPlugin.extract(
            'style',
            'css?sourceMap!less-loader?sourceMap'
          )
      }, {
        test: /\.css$/,
        loader: //'style-loader!css-loader'
        ExtractTextPlugin.extract(
            'style',
            'css?sourceMap'
          )
      }, {
        test: /\.(gif|jpg|png|woff|woff2|eot|ttf|svg)(\?v=[0-9]\.[0-9]\.[0-9])?$/,
        loader: 'file?name=[path][name].[ext]?[hash]'
      }
      // {
      //   test: /\.(png|woff|woff2|eot|ttf|svg)(\?v=[0-9]\.[0-9]\.[0-9])?$/,
      //   loader: 'url-loader?name=[path][name].[ext]?[hash]'
      // }
    ]
  }
};

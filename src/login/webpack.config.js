var path = require('path');
var webpack = require('webpack');

module.exports = {
  devtool: 'eval', // or cheap-inline-module-source-map
  // context: __dirname + '/',
  entry: {
    main:[
      'webpack-dev-server/client?http://localhost:80',
      'webpack/hot/only-dev-server',
      './index'
    ]
  },
  output: {
    path: path.join(__dirname, 'public'),
    filename: '[name].js',
    publicPath: '/pub/login/'
  },
  plugins: [
    new webpack.HotModuleReplacementPlugin()
  ],
  resolve: {
    extensions: ['', '.webpack.js', '.web.js', '.js', '.jsx', '.es6'],
    alias: {
      'img': path.join(__dirname, 'img')
    }
  },
  progress: true,
  colors : true,
  module: {
    loaders: [
      {
        test: /\.jsx?$/,
        loaders: ['react-hot', 'babel'],
        // include: path.join(__dirname, 'src'),
        include: [
          path.resolve(__dirname, 'components'),
          path.resolve(__dirname, 'containers'),
        ]
      },
      {
        test: /\.(?:js|es).?$/,
        exclude: [ /node_modules/, /.less$/],
        loader: 'babel-loader',
        query: {
          presets: [
            require.resolve('babel-preset-es2015'),
            require.resolve('babel-preset-react'),
            require.resolve('babel-preset-stage-0')
          ]
        }
      },
      {
        test: /\.scss$/,
        loaders: [ 'style', 'css?sourceMap', 'sass?sourceMap' ]
      },
      { test: /\.css$/, loader: 'style-loader!css-loader' },
      { test: /\.less$/, loader: 'style-loader!css-loader!less-loader' },
      {
        test: /\.(gif|jpg|png|woff|woff2|eot|ttf|svg)(\?v=[0-9]\.[0-9]\.[0-9])?$/,
        loader: 'url-loader?name=[path][name].[ext]?[hash]'
      }
    ]
  }
};

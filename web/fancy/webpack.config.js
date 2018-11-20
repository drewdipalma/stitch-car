const path = require('path');
const webpack = require('webpack');
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');

const libName = 'rover';

module.exports = (env = {}) => ({
  entry: [
    'react-hot-loader/patch',
    'webpack-dev-server/client?http://0.0.0.0:8001',
    'webpack/hot/only-dev-server',
    './src/index.tsx'
  ],
  output: {
    filename: 'bundle.js',
    path: path.resolve(__dirname, 'dist'),
    publicPath: '/static/'
  },
  devtool: 'inline-source-map',
  resolve: {
    modules: [path.resolve('./src'), path.resolve('./node_modules')],
    extensions: ['.ts', '.tsx', '.js']
  },
  module: {
    rules: [
      {
        test: /\.(j|t)sx?$/,
        loader: 'ts-loader',
        exclude: /node_modules/
      },
      {
        test: /\.svg/,
        loader: ['svg-url-loader']
      },
      {
        test: /.(png|jpg|jpeg|gif|woff|woff2|eot|ttf)(\?v=\d+\.\d+\.\d+)?$/,
        use: [
          {
            loader: 'url-loader',
            options: {
              name: '[hash].[ext]',
              limit: 10000
            }
          }
        ]
      },
      {
        test: /\.less$/,
        use: [
          { loader: 'style-loader' },
          { loader: 'css-loader?sourceMap=true' },
          { loader: 'less-loader?sourceMap=true' }
        ]
      },
      {
        test: /favicon\.ico$/,
        use: [
          {
            loader: 'file-loader',
            options: {
              name: '[name].[ext]'
            }
          }
        ]
      }
    ]
  },
  plugins: (() => {
    const plugins = [
      new webpack.HotModuleReplacementPlugin(),
      new webpack.NamedModulesPlugin(),
      new webpack.NoEmitOnErrorsPlugin(),
      new webpack.DefinePlugin({
        'process.env': {
          NODE_ENV: JSON.stringify(''),
          REPO_GIT_REV: JSON.stringify(process.env.REPO_GIT_REV),
          DB_VERSION: JSON.stringify(process.env.DB_VERSION),
          JS_SDK_VERSION: JSON.stringify(process.env.JS_SDK_VERSION),
          UI_VERSION: JSON.stringify(process.env.npm_package_version)
        }
      })
    ];
    if (env.build) {
      plugins.push([new UglifyJsPlugin({ sourceMap: true })]);
    }
    return plugins;
  })(),
  devServer: {
    host: '0.0.0.0',
    port: 8001,
    historyApiFallback: true,
    hot: true,
    progress: true,
    disableHostCheck: true
  }
});

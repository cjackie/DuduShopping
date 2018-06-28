const webpack = require('webpack');
const path = require('path');

module.exports = env => {
    return {
        entry: ['whatwg-fetch', './src/index.js'],
        output: {
            path: path.resolve(__dirname),
            filename: '../src/main/webapp/react-bundle.js',
            libraryTarget: 'umd',
            umdNamedDefine: true,
            library: 'DuduShopping'
        },
        module: {
            rules: [
                {
                    test: /\.(js|jsx)$/,
                    exclude: /node_modules/, // why this?
                    use: 'babel-loader'
                }
            ]
        },
        mode : env && env.production ? 'production' : 'development',
        devtool: env && env.production? false : 'source-map'
    };
};
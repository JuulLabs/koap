// webpack.config.js
module.exports = {
    mode: 'development',
    entry: './decoder.test.ts',
    output: {
      filename: 'main.js',
      publicPath: 'dist'
    },
    module: {
        rules: [
          {
            test: /\.js$/,
            exclude: /node_modules/,
            use: {
              loader: 'babel-loader',
              options: {
                presets: ['@babel/preset-env', '@babel/preset-typescript']
              }
            }
          },
          {
            test: /\.ts$/,
            exclude: /node_modules/,
            use: 'ts-loader'
          }
        ]
    }
};
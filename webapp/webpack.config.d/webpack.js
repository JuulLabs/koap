const webpack = require('webpack');

config.plugins = [
    new webpack.ProvidePlugin({
        process: 'process/browser',
    })
]

config.resolve.fallback = {
    "buffer": require.resolve("buffer/"),
    "process": require.resolve("process/browser"),
    "stream": require.resolve("stream-browserify"),
}

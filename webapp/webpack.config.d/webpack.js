const webpack = require('webpack');

config.plugins = [
    new webpack.ProvidePlugin({
        process: 'process/browser',
    })
]

config.resolve.fallback = {
    "stream": require.resolve("stream-browserify"),
    "util": require.resolve("util/"),
    "buffer": require.resolve("buffer/"),
    "os": require.resolve("os-browserify/browser"),
    "process": require.resolve("process/browser"),
    "path": require.resolve("path-browserify")
}

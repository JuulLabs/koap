config.resolve.fallback = {
    "stream": require.resolve("stream-browserify"),
    "util": require.resolve("util/"),
    "buffer": require.resolve("buffer/"),
    "os": require.resolve("os-browserify/browser"),
    "process": require.resolve("process/"),
    "path": require.resolve("path-browserify")
}

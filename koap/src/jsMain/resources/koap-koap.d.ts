
// export namespace kotlin {
//     class Long {
//         constructor(value: number)
//     }
// }

// export namespace kotlin.kotlin.collections {
//     class ArrayList {
//         constructor(items: [any])
//     }
// }

export namespace com.juul.koap {
    class Message {
    }

    function tcpMessage(
        method: com.juul.koap.Message.Code.Method,
        token: number,
        options: Array<com.juul.koap.Message.Option>,
        payload: Int8Array
    ): com.juul.koap.Message

    function encode(
        message: com.juul.koap.Message
    ): Int8Array
}

export namespace com.juul.koap.Message {
    class Code {
    }

    class Option {
    }

    class Tcp {
    }
}

export namespace com.juul.koap.Message.Code {
    class Method {
        static GET: Method
        static POST: Method
        static PUT: Method
        static DELETE: Method
    }
}

export namespace com.juul.koap.Message.Option {
    class UriPath {
        constructor(path: string)
    }

    class UriQuery {
        constructor(query: string)
    }
}

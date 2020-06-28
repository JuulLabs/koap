[![codecov](https://codecov.io/gh/JuulLabs/koap/branch/master/graph/badge.svg?token=EM9VA765J7)](https://codecov.io/gh/JuulLabs/koap)
[![Apache 2.0](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

# KoAP

Kotlin CoAP encoder/decoder that provides basic support for:
- CoAP UDP ([RFC 7252])
- CoAP TCP ([RFC 8323])
- CoAP Observe ([RFC 7641])

## Usage

### Encoding

`ByteArray`s of encoded CoAP messages can be created from `Message` objects (either `Message.Udp` or
`Message.Tcp` depending on the desired encoding).

To encode a `Message` as a CoAP UDP `ByteArray`:

```kotlin
import com.juul.koap.Message.Udp
import com.juul.koap.encode

val message = Message.Udp(/* ... */)
val encoded = message.encode()
```

Similarly, to encode a `Message` as a CoAP TCP `ByteArray`:

```kotlin
import com.juul.koap.Message.Tcp
import com.juul.koap.encode

val message = Message.Tcp(/* ... */)
val encoded = message.encode()
```

### Decoding

Encoded CoAP messages (in the form of `ByteArray`s) can be decoded to `Message` objects (either
`Message.Udp` or `Message.Tcp`, depending on the encoding).

To decode a `ByteArray` containing a message encoded as CoAP UDP:

```kotlin
import com.juul.koap.Message.Udp
import com.juul.koap.decode

val encoded: ByteArray // Encoded message that adheres to RFC 7252.
val message = encoded.decode<Udp>()
```

Similarly, to decode a `ByteArray` encoded as a CoAP TCP message:

```kotlin
import com.juul.koap.Message.Tcp
import com.juul.koap.decode

val encoded: ByteArray // Encoded message that adheres to RFC 8323.
val message = encoded.decode<Tcp>()
```

#### Header

If it's desirable to examine the header prior to decoding the entire encoded CoAP message, then
`ByteArray.decodeUdpHeader` or `ByteArray.decodeTcpHeader` extension functions are available. The
remaining encoded CoAP message can then be decoded by passing the header to the `ByteArray.decode`
extension function, for example:

```kotlin
val encoded: ByteArray // Encoded message that adheres to RFC 7252.
val header = encoded.decodeUdpHeader()

// Examine header and determine that message should be decoded:
val message = encoded.decode(header, skipHeader = true)
```

## Examples

### Encoding

```kotlin
val message = Message.Udp(
    type = Confirmable,
    code = GET,
    id = 0xFEED,
    token = 0xCAFE,
    options = listOf(
        UriPath("example")
    ),
    payload = byteArrayOf()
)
```

Encoding the above `Message` will produce the following encoded data:

```
42 01 FE ED CA FE B7 65 78 61 6D 70 6C 65
```

Encoded messages adhere to the format described in [Figure 7: Message Format] of [RFC 7252]:

```
 0                   1                   2                   3
 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|Ver| T |  TKL  |      Code     |          Message ID           |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|   Token (if any, TKL bytes) ...
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|   Options (if any) ...
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|1 1 1 1 1 1 1 1|    Payload (if any) ...
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
```

| Field(s)            | Hex                    | Description                                                       |
|---------------------|------------------------|-------------------------------------------------------------------|
| Ver, T, TKL         | `42`                   | `4` = Version: 1, Type: 0 (Confirmable)<br/>`2` = Token Length: 2 |
| Code                | `01`                   | Code: 0.01 (GET)                                                  |
| Message ID          | `FE ED`                |                                                                   |
| Token               | `CA FE`                |                                                                   |
| Option delta/length | `B7`                   | `B` = Delta option: 11 (Uri-Path)<br/>`7` = Delta length: 7       |
| Option value        | `65 78 61 6D 70 6C 65` | `"example"`                                                       |

### Decoding

Assuming an encoded message consists of the following CoAP UDP encoded data:

```
42 01 FE ED CA FE B5 2F 74 65 73 74 FF 48 65 6C 6C 6F 20 55 44 50 21
```

| Field(s)            | Hex                             | Description                                                       |
|---------------------|---------------------------------|-------------------------------------------------------------------|
| Ver, T, TKL         | `42`                            | `4` = Version: 1, Type: 0 (Confirmable)<br/>`2` = Token Length: 2 |
| Code                | `01`                            | Code: 0.01 (GET)                                                  |
| Message ID          | `FE ED`                         |                                                                   |
| Token               | `CA FE`                         |                                                                   |
| Option delta/length | `B5`                            | `B` = Delta option: 11 (Uri-Path)<br/>`5` = Delta length: 5       |
| Option value        | `2F 74 65 73 74`                | `"/test"`                                                         |
| Payload marker      | `FF`                            | Signifies the end of `Option`s and beginning of payload.          |
| Payload             | `48 65 6C 6C 6F 20 55 44 50 21` | `"Hello UDP!"`                                                    |

The above `encoded` UDP message can be decoded to a `Message.Udp` via `ByteArray.decode` extension
function:

```kotlin
val message = encoded.decode<Udp>()
```

The resulting `Message` will be equivalent to:

```kotlin
Message.Udp(
    type = Confirmable,
    code = GET,
    id = 0xFEED,
    token = 0xCAFE,
    options = listOf(
        UriPath("/test")
    ),
    payload = "Hello UDP!".toByteArray()
)
```

# Setup

## Gradle

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.juul.koap/koap/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.juul.koap/koap)

```groovy
repositories {
    jcenter() // or mavenCentral()
}

dependencies {
    implementation 'com.juul.koap:koap:$version'
}
```

# License

```
Copyright 2020 JUUL Labs, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```


[RFC 7252]: https://tools.ietf.org/html/rfc7252
[RFC 8323]: https://tools.ietf.org/html/rfc8323
[RFC 7641]: https://tools.ietf.org/html/rfc7641
[Figure 7: Message Format]: https://tools.ietf.org/html/rfc7252#section-3

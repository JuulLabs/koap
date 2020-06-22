[![codecov](https://codecov.io/gh/JuulLabs/koap/branch/master/graph/badge.svg?token=EM9VA765J7)](https://codecov.io/gh/JuulLabs/koap)

# KoAP

Kotlin CoAP encoder/decoder that supports CoAP UDP ([RFC 7252]) and CoAP TCP ([RFC 8323]).

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

## Examples

### Encoding

```kotlin
val message = Message.Udp(
    type = Confirmable,
    code = GET,
    id = 0xFE_ED,
    token = 0xCA_FE,
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

Artifacts are hosted on GitHub packages, which can be configured as follows:

```groovy
repositories {
    maven {
        url = "https://maven.pkg.github.com/juullabs/android-github-packages"
        credentials {
            username = findProperty('github.packages.username')
            password = findProperty('github.packages.password')
        }
    }
}

dependencies {
    implementation 'com.juul.koap:koap:$version'
}
```

_Replace_ `$version` _with the desired version (can be found on the [GitHub Packages page])._


[RFC 7252]: https://tools.ietf.org/html/rfc7252
[RFC 8323]: https://tools.ietf.org/html/rfc8323
[GitHub Packages page]: https://github.com/JuulLabs/android-github-packages/packages/273980
[Figure 7: Message Format]: https://tools.ietf.org/html/rfc7252#section-3

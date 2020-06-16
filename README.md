[![codecov](https://codecov.io/gh/JuulLabs/koap/branch/master/graph/badge.svg?token=EM9VA765J7)](https://codecov.io/gh/JuulLabs/koap)

# KoAP

Kotlin CoAP encoder/decoder that supports CoAP UDP ([RFC 7252]) and CoAP TCP ([RFC 8323]).

## API

### Encoding

CoAP `ByteArray` payloads can be created from `Message` objects (either `Message.Udp` or `Message.Tcp` dependending on the desired encoding).

To encode a `Message` as a UDP `ByteArray` payload:

```kotlin
import com.juul.koap.Message.Udp
import com.juul.koap.encode

val message = Message.Udp(/* ... */)
val payload = message.encode()
```

Similarly, to encode a `Message` as a TCP `ByteArray` payload:

```kotlin
import com.juul.koap.Message.Tcp
import com.juul.koap.encode

val message = Message.Tcp(/* ... */)
val payload = message.encode()
```

### Decoding

Payloads (in the form of `ByteArray`s) can be decoded to `Message` objects (either `Message.Udp` or
`Message.Tcp` dependending on the encoding).

To decode a UDP `ByteArray` payload:

```kotlin
import com.juul.koap.Message.Udp
import com.juul.koap.decode

val payload: ByteArray // Payload that adheres to RFC 7252.
val message = payload.decode<Udp>()
```

Similarly, to decode a TCP `ByteArray` payload:

```kotlin
import com.juul.koap.Message.Tcp
import com.juul.koap.decode

val payload: ByteArray // Payload that adheres to RFC 8323.
val message = payload.decode<Tcp>()
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
[GitHub Packages page]: TODO

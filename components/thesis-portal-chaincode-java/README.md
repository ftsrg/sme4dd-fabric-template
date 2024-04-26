# Hyperledger Fabric v2 TPC-C chaincode (Java)

## Overview

This project contains the TPC-C transaction profile implementations in the form of a Hyperledger Fabric v2 chaincode, written in Java, ported from the [JavaScript version](../../v1/javascript/README.md) by [KangogoDamarisKE](https://github.com/KangogoDamarisKE).

Please refer to the [README of the original JS version](../../v1/javascript/README.md) for more information.

## Build Instructions

This project uses [Gradle](https://gradle.org/) as its build system.
You are expected to build a JAR from the chaincode using the `shadowJar` task (→ it should appear in the `build/lib/` subdirectory as `chaincode.jar`).

However, note that normally, you do not need to locally build the chaincode.
The [test network](../../../../test-network/README.adoc) will build generate this JAR as part of a [dockerized](https://www.docker.com/) build process.
Build directly only if you know what you are doing.

## Development

You most likely do not want to build with OpenJML for local development, eg in your IDE.
To disable OpenJML, simply set the `withoutOpenJML` Gradle property to `true`.
For example, in `gradle.properties`:

```properties
withoutOpenJML = "true"
```


## License

The project uses the Apache License Version 2.0. For more information see [NOTICES.md](./../../NOTICES.md), [CONTRIBUTORS.md](./../../CONTRIBUTORS.md), and [LICENSE](./../../LICENSE).

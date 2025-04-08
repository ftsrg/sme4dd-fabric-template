# SPDX-License-Identifier: Apache-2.0
FROM gradle:8-jdk21-jammy AS build

WORKDIR /home/gradle/
COPY src/ src/
COPY build.gradle.kts ./ 

RUN gradle --no-daemon shadowJar


FROM eclipse-temurin:21-jre
ARG CC_SERVER_PORT=9999

ENV TINI_VERSION v0.19.0
ADD [ \
    "https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini", \
    "/tini" \
]
RUN chmod +x /tini

RUN addgroup --system javauser && useradd -g javauser javauser

COPY --chown=javauser:javauser --from=build [ \
    "/home/gradle/build/libs/chaincode.jar", \
    "/chaincode.jar" \
]
COPY --chown=javauser:javauser [ \
    "docker/docker-entrypoint", \
    "/docker-entrypoint" \
]

ENV PORT $CC_SERVER_PORT
EXPOSE $CC_SERVER_PORT

USER javauser
ENTRYPOINT [ "/tini", "--", "/docker-entrypoint" ]

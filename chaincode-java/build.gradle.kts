/* SPDX-License-Identifier: Apache-2.0 */
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
  application
  id("com.github.johnrengelman.shadow") version "7.1.2"
  id("com.diffplug.spotless") version "6.19.0"
  id("io.freefair.lombok") version "8.6"
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(11)) } }

group = "hu.bme.mit.ftsrg.chaincode.launchcodes"
version = "0.1.0"

repositories {
  mavenCentral()
  maven { url = uri("https://jitpack.io") }
}

val versions =
    mapOf(
        "tinylog" to "2.7.0",
        "mockito" to "5.11.0",
        "fabric" to "2.5.5",
        "gson" to "2.10.1",
        "assertj" to "3.24.2",
        "junit" to "5.10.0")

dependencies {
  implementation(
      "org.hyperledger.fabric-chaincode-java:fabric-chaincode-shim:${versions["fabric"]}")
  implementation("org.tinylog:tinylog-api:${versions["tinylog"]}")
  implementation("org.tinylog:tinylog-impl:${versions["tinylog"]}")
  implementation("com.google.code.gson:gson:${versions["gson"]}")

  testImplementation("org.assertj:assertj-core:${versions["assertj"]}")
  testImplementation("org.junit.jupiter:junit-jupiter:${versions["junit"]}")
  testImplementation("org.mockito:mockito-core:${versions["mockito"]}")
  testImplementation("org.mockito:mockito-junit-jupiter:${versions["mockito"]}")
}

application { mainClass.set("org.hyperledger.fabric.contract.ContractRouter") }

tasks.named<ShadowJar>("shadowJar") {
  mergeServiceFiles()
  archiveBaseName.set("chaincode")
  archiveClassifier.set("")
  archiveVersion.set("")
}

tasks.test {
  useJUnitPlatform()
  testLogging {
    showExceptions = true
    events = setOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
  }
}

tasks.named<JavaExec>("run") {
  environment(
      mapOf("CORE_CHAINCODE_ID_NAME" to "hw-launch-codes", "CORE_PEER_ADDRESS" to "127.0.0.1:7041"))
}

spotless {
  java {
    importOrder()
    removeUnusedImports()
    googleJavaFormat()
    formatAnnotations()
    licenseHeader("/* SPDX-License-Identifier: Apache-2.0 */")
  }
  kotlinGradle { ktfmt() }
}

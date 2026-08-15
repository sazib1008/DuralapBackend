plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":modules:auth"))
    implementation(project(":modules:user"))
    implementation(project(":modules:chat"))
    implementation(project(":modules:message"))
    implementation(project(":modules:presence"))
    implementation(project(":modules:notification"))
    implementation(project(":modules:media"))
    implementation(project(":modules:search"))
    implementation(project(":modules:analytics"))

    implementation(project(":shared:shared-kernel"))
    implementation(project(":shared:shared-security"))
    implementation(project(":shared:shared-mongo"))
    implementation(project(":shared:shared-redis"))
    implementation(project(":shared:shared-websocket"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
}

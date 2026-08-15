plugins {
    `java-library`
}

dependencies {
    api(project(":shared:shared-kernel"))
    implementation(project(":shared:shared-security"))
    implementation(project(":shared:shared-mongo"))
    implementation(project(":shared:shared-redis"))
    implementation(project(":shared:shared-websocket"))
    implementation(project(":modules:user"))
    implementation(project(":modules:chat"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
}

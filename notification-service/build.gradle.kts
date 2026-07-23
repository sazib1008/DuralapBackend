plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":common:common-dto"))
    implementation(project(":common:common-utils"))
    implementation(project(":common:common-security"))
    implementation(project(":common:common-mongo"))
    implementation(project(":common:common-redis"))
    implementation(project(":common:common-kafka"))
    
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
}

plugins {
    `java-library`
}

dependencies {
    api(project(":shared:shared-kernel"))
    implementation(project(":shared:shared-security"))
    implementation(project(":shared:shared-mongo"))
    implementation(project(":shared:shared-redis"))
    implementation(project(":modules:user"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
}

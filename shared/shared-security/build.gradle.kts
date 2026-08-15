plugins {
    `java-library`
}

dependencies {
    api(project(":shared:shared-kernel"))
    api(project(":shared:shared-redis"))
    api(project(":shared:shared-mongo"))
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-oauth2-client")
    api("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    api("org.bouncycastle:bcprov-jdk18on:1.78.1")
}

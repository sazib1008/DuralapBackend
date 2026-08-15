plugins {
    `java-library`
}

dependencies {
    api(project(":shared:shared-kernel"))
    api("org.springframework.boot:spring-boot-starter-data-redis")
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-security")
}

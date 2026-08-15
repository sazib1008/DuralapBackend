plugins {
    `java-library`
}

dependencies {
    api(project(":shared:shared-kernel"))
    api(project(":shared:shared-security"))
    api(project(":shared:shared-redis"))
    api("org.springframework.boot:spring-boot-starter-websocket")
}

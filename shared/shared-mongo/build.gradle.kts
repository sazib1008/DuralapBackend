plugins {
    `java-library`
}

dependencies {
    api(project(":shared:shared-kernel"))
    api("org.springframework.boot:spring-boot-starter-data-mongodb")
}

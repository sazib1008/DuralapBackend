dependencies {
    implementation(project(":common:common-dto"))
    implementation(project(":common:common-utils"))
    implementation(project(":common:common-mongo"))
    implementation(project(":common:common-redis"))
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.bouncycastle:bcprov-jdk18on:1.78.1")
}


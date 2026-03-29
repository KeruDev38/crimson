plugins {
    id("java-library")
}

dependencies {
    api(project(":core-transactions"))
    implementation("org.springframework.boot:spring-boot-starter-web")
}

plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":api-rest"))
    implementation(project(":notifications"))
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("org.postgresql:postgresql")
}

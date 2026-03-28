plugins {
    war
}

dependencies {
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    implementation("com.github.demidko:aot:2025.11.25")
}

tasks.named<War>("war") {
    archiveFileName.set("hw5_search_engine.war")
}

tasks.register("prepareKotlinBuildScriptModel") {
    doLast { }
}

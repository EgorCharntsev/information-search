plugins {
    application
}

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("com.github.demidko:aot:2025.11.25")
}

application {
    mainClass.set("ru.kpfu.itis.charntsev.tokenization.Main")
}

tasks.register("prepareKotlinBuildScriptModel") {
    doLast { }
}
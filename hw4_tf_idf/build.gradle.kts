plugins {
    application
}

dependencies {}

application {
    mainClass.set("ru.kpfu.itis.charntsev.tf.idf.Main")
}

tasks.withType<JavaExec>().configureEach {
    standardInput = System.`in`
    jvmArgs(
        "-Dfile.encoding=UTF-8",
        "-Dsun.stdout.encoding=UTF-8",
        "-Dsun.stderr.encoding=UTF-8"
    )
}

tasks.register("prepareKotlinBuildScriptModel") {
    doLast { }
}
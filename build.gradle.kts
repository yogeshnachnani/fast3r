import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile

val ktorVersion = "1.3.2"
val jgitVersion = "5.7.0.202003110725-r"
val logbackVersion = "1.2.3"
val jdbiVersion = "3.12.2"



plugins {
    java
    kotlin("multiplatform") version "1.3.72"
    kotlin("plugin.serialization") version "1.3.72"
}

group = "io.btc"
version = "1.0.0"

repositories {
    maven("https://kotlin.bintray.com/kotlin-js-wrappers/")
    mavenCentral()
    jcenter()
}

kotlin {
    js {
        browser {
            testTask {
                useKarma() {
                    useChromeHeadless()
                }
            }
            /**
             * see https://youtrack.jetbrains.com/issue/KT-36484. This is fixed in 1.4-M2 (we were/are at 1.3.71 while writing this)
             */
            dceTask {
                keep("ktor-ktor-io.\$\$importsForInline\$\$.ktor-ktor-io.io.ktor.utils.io")
            }
        }
        nodejs {
            useCommonJs()
        }
    }

    jvm {
        val main by compilations.getting {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
        tasks {
            register<Jar>("buildFatJar") {
                group = "application"
                manifest {
                    attributes["Main-Class"] = "io.btc.ApiServerBackend"
//                    attributes["Class-Path"] = main.compileDependencyFiles.map { it.name }.joinToString(separator =  " ")
                }
                archiveBaseName.set("${project.name}-fat")
                from(main.output.classesDirs, main.compileDependencyFiles)
//              from(main.compileDependencyFiles.asFileTree.map { if (it.isDirectory) it else zipTree(it) })
//                from (
//                    main.configuration
//                    main.compileDependencyFiles.map {
//                        if(it.isDirectory) {
//                            it
//                        } else {
//                            zipTree(it)
//                        }
//                    }
//                )
//                from(main.output.classesDirs, main.compileDependencyFiles)
                with(jar.get() as CopySpec)
            }
            register<JavaExec>("runLocally") {
                group = "application"
                setMain("AppKt")
                classpath = main.output.classesDirs
                classpath += main.compileDependencyFiles
            }
        }
        val test by compilations.getting {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    tasks.named("compileKotlinJs") {
        this as KotlinJsCompile
        kotlinOptions.moduleKind = "commonjs"
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-json:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:0.20.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jsMain by getting {
            dependencies {
                api(npm("text-encoding", "0.7.0"))
                api(npm("abort-controller", "3.0.0"))
                implementation(kotlin("stdlib-js"))
                implementation("io.ktor:ktor-client-serialization-js:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.20.0")
                implementation("io.ktor:ktor-client-js:$ktorVersion")
                implementation("io.ktor:ktor-client-json-js:$ktorVersion")
                api("org.jetbrains.kotlinx:kotlinx-html-js:0.7.1")

                //React, React DOM + Wrappers
                implementation("org.jetbrains:kotlin-react:16.13.0-pre.94-kotlin-1.3.70")
                implementation("org.jetbrains:kotlin-react-dom:16.13.0-pre.94-kotlin-1.3.70")
                implementation(npm("react", "16.13.1"))
                implementation(npm("react-dom", "16.13.1"))
                implementation("org.jetbrains:kotlin-styled:1.0.0-pre.94-kotlin-1.3.70")
                implementation(npm("styled-components"))
                implementation(npm("inline-style-prefixer"))
                // Components used only within React rendering (substituted out when using Atom)
                implementation(npm("react-ace", "8.1.0"))
                implementation(npm("ace-builds", "~1.4.10"))
                // Material UI
                implementation(npm("@material-ui/core"))

                /** Webpack */
                implementation(npm("file-loader", "~6.0.0"))

                /** Diff */
                implementation(npm("diff", "~4.0.2"))
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                /** Ktor and relatives */
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
                implementation("io.ktor:ktor-client-json-jvm:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization-jvm:$ktorVersion")
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")
                implementation("io.ktor:ktor-serialization:$ktorVersion")
                /** Jgit */
                implementation("org.eclipse.jgit:org.eclipse.jgit:$jgitVersion")
                /** Logs */
                implementation("ch.qos.logback:logback-classic:$logbackVersion")
                /** Db */
                implementation("org.xerial:sqlite-jdbc:3.30.1")
                implementation("org.jdbi:jdbi3-core:$jdbiVersion")
                implementation("org.jdbi:jdbi3-kotlin:$jdbiVersion")
                implementation("org.jdbi:jdbi3-kotlin-sqlobject:$jdbiVersion")
                implementation("org.jdbi:jdbi3-sqlite:$jdbiVersion")
                /** Other utils */
                implementation("commons-io:commons-io:2.6")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("org.eclipse.jgit:org.eclipse.jgit.junit:$jgitVersion")

                /** Ktor and relatives */
                implementation("io.ktor:ktor-server-test-host:$ktorVersion")
            }
        }

    }
}
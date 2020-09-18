import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile

val ktorVersion = "1.4.0"
val jgitVersion = "5.7.0.202003110725-r"
val logbackVersion = "1.2.3"
val jdbiVersion = "3.12.2"
val kotlinVersion = "1.4.0"
val reactVersion = "16.13.1"
val reactKotlinVersion = "$reactVersion-pre.110-kotlin-$kotlinVersion"
val reactRouterDomVersion = "5.1.2"
val serializationRuntimVersion = "1.0.0-RC"


plugins {
    java
    kotlin("multiplatform") version "1.4.0"
    kotlin("plugin.serialization") version "1.4.0"
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
            /** Run compilations + tests and copy all built artifacts into build/electron directory */
            register<Copy>("buildAllArtifacts") {
                dependsOn(
                    "clean",
                    "jvmJar",
                    "jsBrowserDistribution"
                )
                /** Copy electron files */
                from("$projectDir/electron_packaging")
                into("$buildDir/electron")

                /** Copy main jar file */
                getByName("jvmJar").outputs.files.map {
                    from(it.absolutePath) {
                        into("java")
                    }
                }
                /** Copy java dependencies */
                main.runtimeDependencyFiles.asFileTree.map {
                    from(it.absolutePath) {
                        into("java")
                    }
                }
                /** Copy javascript */
                from("$buildDir/distributions") {
                    into("js")
                }
            }
            register("electronPackage") {
                dependsOn("buildAllArtifacts")
                doLast {
                    val locationOfElectronPackager = "$projectDir/../electron"
                    println("Copying built artifacts from $buildDir/electron to $locationOfElectronPackager")
                    copy {
                        from("$buildDir/electron")
                        into("$locationOfElectronPackager/lib")
                    }
                    println("Will run electron packager now")
                    exec {
                        workingDir = File(locationOfElectronPackager)
                        commandLine("npm", "install")
                    }
                    exec {
                        workingDir = File(locationOfElectronPackager)
                        commandLine("node_modules/electron-packager/bin/electron-packager.js", ".", "--platform", "darwin")
                    }
                    exec {
                        workingDir = File(locationOfElectronPackager)
                        commandLine("node_modules/electron-packager/bin/electron-packager.js", ".", "--platform", "linux", "--arch", "x64")
                    }
                }
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
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationRuntimVersion")
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
                implementation(kotlin("stdlib"))
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.2")
                implementation("io.ktor:ktor-client-js:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization-js:$ktorVersion")

                //React, React DOM + Wrappers
                implementation("org.jetbrains:kotlin-react:$reactKotlinVersion")
                implementation("org.jetbrains:kotlin-react-dom:$reactKotlinVersion")
                implementation(npm("react", reactVersion))
                implementation(npm("react-dom", reactVersion))
                implementation("org.jetbrains:kotlin-styled:1.0.0-pre.115-kotlin-1.4.10")
                implementation(npm("styled-components", "5.2.0"))
                implementation(npm("inline-style-prefixer", "6.0.0" ))
                // Components used only within React rendering (substituted out when using Atom)
                implementation(npm("ace-builds", "~1.4.10"))
                // Material UI
                implementation(npm("@material-ui/core", "4.11.0"))
                implementation(npm("@material-ui/styles", "4.10.0"))
                implementation(npm("@material-ui/icons", "4.9.1"))

                /** Webpack */
                implementation(npm("file-loader", "~6.0.0"))

                /** Diff */
                implementation(npm("diff", "~4.0.2"))

                /** Library for drag-and-drop support. NOTE: Also imports redux! */
                implementation(npm("react-beautiful-dnd", "~13.0.0"))
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                /** Ktor and relatives */
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
                implementation("io.ktor:ktor-client-json-jvm:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization-jvm:$ktorVersion")
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
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
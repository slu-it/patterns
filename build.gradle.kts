import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES

plugins {
    id("org.springframework.boot") version "3.0.2" apply false
    id("io.spring.dependency-management") version "1.1.0" apply false
    kotlin("jvm") version "1.7.22" apply false
    kotlin("plugin.spring") version "1.7.22" apply false
}

subprojects {
    repositories { mavenCentral() }

    if (this.childProjects.isEmpty()) {
        apply {
            plugin("org.springframework.boot")
            plugin("io.spring.dependency-management")
            plugin("org.jetbrains.kotlin.jvm")
            plugin("org.jetbrains.kotlin.plugin.spring")
        }
        the<DependencyManagementExtension>().apply {
            imports {
                mavenBom(BOM_COORDINATES)
                mavenBom("org.jetbrains.kotlin:kotlin-bom:1.7.22")
                mavenBom("org.testcontainers:testcontainers-bom:1.17.6")
                mavenBom("io.github.logrecorder:logrecorder-bom:2.5.1")
            }
        }
        tasks {
            withType<JavaCompile> {
                sourceCompatibility = "17"
                targetCompatibility = "17"
            }
            withType<KotlinCompile> {
                kotlinOptions {
                    freeCompilerArgs = listOf("-Xjsr305=strict")
                    jvmTarget = "17"
                }
            }
            withType<Test> {
                useJUnitPlatform()
            }
        }
    }
}

import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.getByType

sourceSets {
    main {
        java.srcDirs("src/main")
    }

    dependencies {
        val junitVersion: String by project
        val jctoolsVersion: String by project

        implementation(project(":lincheck"))
        // Lincheck integration tests depend on `AbstractLincheckTest`
        // which is defined in lincheck module's 'test' source set
        implementation(project.project(":lincheck").extensions.getByType(SourceSetContainer::class)["test"].output)
        implementation("junit:junit:$junitVersion")
        implementation("org.jctools:jctools-core:$jctoolsVersion")
    }
}

tasks {
    // TODO: rename to match trace-recorder gradle task naming pattern to 'lincheckIntegrationTest'
    register<Test>("integrationTest") {
        configureJvmTestCommon(project)
        group = "verification"

        // We cannot put integration tests in the 'test' source set because they will
        // be run together with unit tests on the ':test' task, which we don't want.
        // Thus, all integration tests use 'main' source set instead and manually
        // set up test classes location.
        testClassesDirs = sourceSets["main"].output.classesDirs
        classpath = sourceSets["main"].runtimeClasspath

        enableAssertions = true
        testLogging.showStandardStreams = true
        outputs.upToDateWhen { false } // Always run tests when called
    }

    register<JavaExec>("loopEval") {
        group = "verification"
        description = "Runs the loop-evaluation benchmarks via AllBenchmarksRunner"
        classpath = sourceSets["main"].runtimeClasspath
        mainClass.set("org.jetbrains.lincheck_test.evaluation.common.AllBenchmarksRunner")
        workingDir = rootProject.projectDir

        val jdkToolchainVersion: String by project
        val javaToolchains = project.extensions.getByType<JavaToolchainService>()
        javaLauncher.set(
            javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(jdkToolchainVersion))
            }
        )

        listOf(
            "lincheck.loopEval.suite",
            "lincheck.loopEval.benchmarks"
        ).forEach { propertyName ->
            val value = System.getProperty(propertyName) ?: project.findProperty(propertyName)?.toString()
            if (value != null) {
                systemProperty(propertyName, value)
            }
        }
    }
}

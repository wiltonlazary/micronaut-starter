package io.micronaut.starter.feature.knative

import io.micronaut.starter.BeanContextSpec
import io.micronaut.starter.application.ApplicationType
import io.micronaut.starter.application.generator.GeneratorContext
import io.micronaut.starter.feature.build.gradle.templates.buildGradle
import io.micronaut.starter.feature.build.maven.templates.pom
import io.micronaut.starter.fixture.CommandOutputFixture
import io.micronaut.starter.options.Language
import spock.lang.Unroll

class KnativeSpec extends BeanContextSpec  implements CommandOutputFixture {

    void 'test readme.md with feature knative contains links to micronaut and knative docs'() {
        when:
        def output = generate(['knative'])
        def readme = output["README.md"]

        then:
        readme
        readme.contains("https://micronaut-projects.github.io/micronaut-kubernetes/latest/guide/index.html")
        readme.contains("https://knative.dev/")
    }

    void 'test knative configuration'() {
        when:
        GeneratorContext commandContext = buildGeneratorContext(['knative'])

        then:
        commandContext.templates.get('knativeYaml')
    }

}

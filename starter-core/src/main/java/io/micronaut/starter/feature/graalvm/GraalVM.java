/*
 * Copyright 2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.starter.feature.graalvm;

import com.fizzed.rocker.RockerModel;
import io.micronaut.starter.application.ApplicationType;
import io.micronaut.starter.application.generator.GeneratorContext;
import io.micronaut.starter.feature.Category;
import io.micronaut.starter.feature.Feature;
import io.micronaut.starter.feature.function.awslambda.AwsLambda;
import io.micronaut.starter.feature.graalvm.template.*;
import io.micronaut.starter.options.BuildTool;
import io.micronaut.starter.template.RockerTemplate;

import javax.inject.Singleton;

@Singleton
public class GraalVM implements Feature {

    @Override
    public String getName() {
        return "graalvm";
    }

    @Override
    public String getTitle() {
        return "GraalVM Native Image";
    }

    @Override
    public String getDescription() {
        return "Allows Building a GraalVM Native Image";
    }

    @Override
    public boolean supports(ApplicationType applicationType) {
        return true;
    }

    @Override
    public String getCategory() {
        return Category.PACKAGING;
    }

    @Override
    public void apply(GeneratorContext generatorContext) {
        // TODO: Delete Maven plugin is upgraded
        if (generatorContext.getBuildTool() == BuildTool.MAVEN) {

            ApplicationType applicationType = generatorContext.getApplicationType();
            RockerModel dockerfileRockerModel;
            String jarFile = generatorContext.getBuildTool()
                    .getShadeJarDirectoryPattern(generatorContext.getProject());
            if (nativeImageWillBeDeployedToAwsLambda(generatorContext)) {
                dockerfileRockerModel = lambdadockerfile.template(generatorContext.getProject(), generatorContext.getBuildTool(), jarFile, generatorContext.getJdkVersion());
                RockerModel deployshRockerModel = deploysh.template(generatorContext.getProject());
                generatorContext.addTemplate("deploysh", new RockerTemplate("deploy.sh", deployshRockerModel, true));

            } else {
                dockerfileRockerModel = dockerfile.template(generatorContext.getProject(), jarFile, generatorContext.getJdkVersion());
            }

            //overrides the template from the Docker feature
            generatorContext.addTemplate("dockerfile", new RockerTemplate("Dockerfile", dockerfileRockerModel));

            generatorContext.addTemplate("dockerBuildScript", new RockerTemplate("docker-build.sh", dockerBuildScript.template(generatorContext.getProject()), true));

            generatorContext.addTemplate("nativeImageProperties",
                    new RockerTemplate("src/main/resources/META-INF/native-image/{packageName}/{name}-application/native-image.properties", nativeImageProperties.template(
                            applicationType,
                            generatorContext.getProject(),
                            generatorContext.getFeatures())

                    )
            );
        }
    }

    private boolean nativeImageWillBeDeployedToAwsLambda(GeneratorContext generatorContext) {
        return generatorContext.getFeatures().getFeatures().stream().anyMatch(feature -> feature.getName().equals(AwsLambda.FEATURE_NAME_AWS_LAMBDA));
    }
}

/*
 * (c) Copyright 2019 Palantir Technologies Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.palantir.baseline.extensions;

import javax.inject.Inject;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

/**
 * Extension named {@code javaVersions} on the root project used to configure all java modules
 * with consistent java toolchains.
 */
public class BaselineJavaVersionsExtension {

    private final Property<JavaLanguageVersion> libraryTarget;
    private final Property<JavaLanguageVersion> distributionTarget;
    private final Property<JavaLanguageVersion> runtime;

    @Inject
    public BaselineJavaVersionsExtension(Project project) {
        this.libraryTarget = project.getObjects().property(JavaLanguageVersion.class);
        this.distributionTarget = project.getObjects().property(JavaLanguageVersion.class);
        this.runtime = project.getObjects().property(JavaLanguageVersion.class);
        // distribution defaults to the library value
        distributionTarget.convention(libraryTarget);
        // runtime defaults to the distribution value
        runtime.convention(distributionTarget);
    }

    /** Target {@link JavaLanguageVersion} for compilation of libraries that are published. */
    public final Property<JavaLanguageVersion> libraryTarget() {
        return libraryTarget;
    }

    public final void setLibraryTarget(int value) {
        libraryTarget.set(JavaLanguageVersion.of(value));
    }

    /**
     * Target {@link JavaLanguageVersion} for compilation of code used within distributions,
     * but not published externally.
     */
    public final Property<JavaLanguageVersion> distributionTarget() {
        return distributionTarget;
    }

    public final void setDistributionTarget(int value) {
        distributionTarget.set(JavaLanguageVersion.of(value));
    }

    /** Runtime {@link JavaLanguageVersion} for testing and packaging distributions. */
    public final Property<JavaLanguageVersion> runtime() {
        return runtime;
    }

    public final void setRuntime(int value) {
        runtime.set(JavaLanguageVersion.of(value));
    }
}

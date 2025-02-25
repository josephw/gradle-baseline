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

import com.google.common.collect.ImmutableSet;
import javax.inject.Inject;
import org.gradle.api.Project;
import org.gradle.api.provider.SetProperty;

/**
 * Extension to configure {@code --add-exports [VALUE]=ALL-UNNAMED} for the current module.
 */
public class BaselineModuleJvmArgsExtension {

    private final SetProperty<String> exports;
    private final SetProperty<String> opens;

    @Inject
    public BaselineModuleJvmArgsExtension(Project project) {
        exports = project.getObjects().setProperty(String.class);
        opens = project.getObjects().setProperty(String.class);
    }

    /**
     * Property describing all {@code --add-exports} values for this module.
     * Exports take the form {@code MODULE_NAME/PACKAGE_NAME}, so to represent
     * {@code --add-exports java.management/sun.management=ALL-UNNAMED} one would add the value
     * {@code java.management/sun.management}.
     */
    public final SetProperty<String> exports() {
        return exports;
    }

    public final void setExports(String... input) {
        ImmutableSet<String> immutableDeduplicatedCopy = ImmutableSet.copyOf(input);
        for (String export : immutableDeduplicatedCopy) {
            validateModulePackagePair(export);
        }
        exports.set(immutableDeduplicatedCopy);
    }

    /**
     * Property describing all {@code --add-opens} values for this module.
     * Opens take the form {@code MODULE_NAME/PACKAGE_NAME}, so to represent
     * {@code --add-opens java.management/sun.management=ALL-UNNAMED} one would add the value
     * {@code java.management/sun.management}.
     */
    public final SetProperty<String> opens() {
        return opens;
    }

    public final void setOpens(String... input) {
        ImmutableSet<String> immutableDeduplicatedCopy = ImmutableSet.copyOf(input);
        for (String export : immutableDeduplicatedCopy) {
            validateModulePackagePair(export);
        }
        opens.set(immutableDeduplicatedCopy);
    }

    private static void validateModulePackagePair(String moduleAndPackage) {
        if (moduleAndPackage.contains("=")) {
            throw new IllegalArgumentException(String.format(
                    "Value '%s' must not contain an '=', e.g. 'java.management/sun.management'. "
                            + "Each export implies a '=ALL-UNNAMED' suffix",
                    moduleAndPackage));
        }
        if (moduleAndPackage.contains(" ")) {
            throw new IllegalArgumentException(
                    String.format("Value '%s' must not contain whitespace", moduleAndPackage));
        }
        int firstSlash = moduleAndPackage.indexOf('/');
        int lastSlash = moduleAndPackage.lastIndexOf('/');
        if (firstSlash != lastSlash || firstSlash < 0) {
            throw new IllegalArgumentException(String.format(
                    "Value '%s' must contain both a module name and package "
                            + "name separated by a single slash, e.g. 'java.management/sun.management'",
                    moduleAndPackage));
        }
    }
}

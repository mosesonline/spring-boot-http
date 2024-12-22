package de.mosesonline.http;

import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.lang.syntax.elements.GivenClassesConjunction;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ArchitectureTests {

    private static final Pattern GREP_BACKEND_PACKAGE = Pattern.compile("de\\.mosesonline\\.http\\.adapter\\.backend\\.(?<baseBackendPackage>[^.]+).*");
    private static final GivenClassesConjunction BACKEND_CLASSES = ArchRuleDefinition.classes()
            .that()
            .resideInAPackage("de.mosesonline.http.adapter.backend.*..");

    @Test
    void backendClassesShouldNotImportOtherBackends() {
        final var backendDependenciesRule = BACKEND_CLASSES.should(haveNoDependenciesToOtherBackends());

        final var importedClasses = new ClassFileImporter().importPackages("de.mosesonline.http.adapter.backend");


        backendDependenciesRule.check(importedClasses);
    }

    @Test
    void backendClassesShouldNotImportOtherAdapters() {
        final var backendDependenciesRule = BACKEND_CLASSES.should(haveNoDependenciesToOtherAdapters());

        final var importedClasses = new ClassFileImporter().importPackages("de.mosesonline.http.adapter.backend");


        backendDependenciesRule.check(importedClasses);
    }

    @Test
    void backendServiceClassesShouldNotBePublic() {
        final var backendDependenciesRule = BACKEND_CLASSES.and().haveNameMatching(".*BackendService").should()
                .notBePublic();

        final var importedClasses = new ClassFileImporter().importPackages("de.mosesonline.http.adapter.backend");


        backendDependenciesRule.check(importedClasses);
    }

    ArchCondition<JavaClass> haveNoDependenciesToOtherBackends() {
        return new ArchCondition<>("only have dependencies to own backend") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                Matcher matcher = GREP_BACKEND_PACKAGE.matcher(javaClass.getPackageName());
                if (matcher.find()) {
                    String baseBackendPackage = matcher.group("baseBackendPackage");
                    javaClass.getDirectDependenciesFromSelf().stream()
                            .filter(d -> {
                                final var targetBasePackageMatcher = GREP_BACKEND_PACKAGE.matcher(d.getTargetClass().getPackageName());
                                return targetBasePackageMatcher.find() && !targetBasePackageMatcher.group("baseBackendPackage").equals(baseBackendPackage);
                            }).forEach(d -> events.add(SimpleConditionEvent.violated(d, d.getDescription())));
                }
            }
        };
    }

    ArchCondition<JavaClass> haveNoDependenciesToOtherAdapters() {
        return new ArchCondition<>("only have dependencies to own backend") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                javaClass.getDirectDependenciesFromSelf().stream()
                        .filter(ArchitectureTests::hasDependencyToNotBackendAdapter).forEach(d -> events.add(SimpleConditionEvent.violated(d, d.getDescription())));
            }
        };
    }

    private static boolean hasDependencyToNotBackendAdapter(Dependency d) {
        return d.getTargetClass().getPackageName().startsWith("de.mosesonline.http.adapter") &&
                !d.getTargetClass().getPackageName().startsWith("de.mosesonline.http.adapter.backend");
    }
}

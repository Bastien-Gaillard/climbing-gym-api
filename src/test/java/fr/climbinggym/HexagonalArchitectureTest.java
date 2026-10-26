package fr.climbinggym;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "fr.climbinggym", importOptions = ImportOption.DoNotIncludeTests.class)
class HexagonalArchitectureTest {

    @ArchTest
    static final ArchRule domain_is_framework_free = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "..application..", "..adapter..", "..config..", "org.springframework..", "jakarta..");

    @ArchTest
    static final ArchRule application_only_knows_domain = noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "..adapter..", "..config..", "org.springframework..", "jakarta..");

    @ArchTest
    static final ArchRule inbound_adapters_do_not_touch_persistence = noClasses()
            .that().resideInAPackage("..adapter.in..")
            .should().dependOnClassesThat().resideInAPackage("..adapter.out..");

    @ArchTest
    static final ArchRule inbound_adapters_use_input_ports = noClasses()
            .that().resideInAPackage("..adapter.in.web..")
            .should().dependOnClassesThat().resideInAPackage("..application.service..");

    @ArchTest
    static final ArchRule services_are_use_cases = classes()
            .that().resideInAPackage("..application.service..")
            .and().haveSimpleNameEndingWith("Service")
            .should().beAnnotatedWith(fr.climbinggym.application.UseCase.class);
}

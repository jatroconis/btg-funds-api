package com.btg.funds.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.btg.funds", importOptions = {ImportOption.DoNotIncludeTests.class})
public class DomainIsolationTest {

    @ArchTest
    public static final ArchRule domainShouldNotDependOnInfrastructureOrFrameworks = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..",
                    "software.amazon.awssdk.."
            );

    @ArchTest
    public static final ArchRule domainClassesShouldOnlyDependOnStandardJavaOrDomain = classes()
            .that().resideInAPackage("..domain..")
            .should().onlyDependOnClassesThat().resideInAnyPackage(
                    "java..",
                    "..domain.."
            );
}

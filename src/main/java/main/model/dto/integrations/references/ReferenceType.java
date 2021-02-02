package main.model.dto.integrations.references;

public class ReferenceType<T extends ReferenceDto> {

    public static final ReferenceType<TestReferenceDto> TEST = new ReferenceType<>(TestReferenceDto.class, "TEST");
    public static final ReferenceType<TestRunReferenceDto> TEST_RUN = new ReferenceType<>(TestRunReferenceDto.class, "TESTRUN");
    public static final ReferenceType<IssueReferenceDto> ISSUE = new ReferenceType<>(IssueReferenceDto.class, "ISSUE");

    private final Class<T> referenceDtoClass;
    private final String name;

    private ReferenceType(Class<T> referenceDtoClass, String name) {
        this.referenceDtoClass = referenceDtoClass;
        this.name = name;
    }

    public Class<T> getDtoClass() {
        return referenceDtoClass;
    }

    public String getName() {
        return name;
    }
}

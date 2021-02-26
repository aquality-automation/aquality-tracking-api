package main.model.dto.integrations.references;

import java.util.function.Supplier;

public class ReferenceType<DTO extends ReferenceDto> {

    public static final ReferenceType<TestReferenceDto> TEST = new ReferenceType<>(TestReferenceDto.class, "TEST", TestReferenceDto::new);
    public static final ReferenceType<TestRunReferenceDto> TEST_RUN = new ReferenceType<>(TestRunReferenceDto.class, "TESTRUN", TestRunReferenceDto::new);
    public static final ReferenceType<IssueReferenceDto> ISSUE = new ReferenceType<>(IssueReferenceDto.class, "ISSUE", IssueReferenceDto::new);

    private final Class<DTO> referenceDtoClass;
    private final String name;
    private final Supplier<DTO> createDto;

    private ReferenceType(Class<DTO> referenceDtoClass, String name, Supplier<DTO> createDto) {
        this.referenceDtoClass = referenceDtoClass;
        this.name = name;
        this.createDto = createDto;
    }

    public Class<DTO> getDtoClass() {
        return referenceDtoClass;
    }

    public String getName() {
        return name;
    }

    public DTO createDto() {
        return createDto.get();
    }
}

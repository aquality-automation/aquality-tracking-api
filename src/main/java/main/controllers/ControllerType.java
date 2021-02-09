package main.controllers;

import main.model.db.dao.DAO;
import main.model.db.dao.integrations.ReferenceDao;
import main.model.db.dao.integrations.SystemDao;
import main.model.db.dao.integrations.SystemTypeDao;
import main.model.db.dao.integrations.tts.TestTrackingStatusDao;
import main.model.db.dao.integrations.tts.TestTrackingTypeDao;
import main.model.dto.BaseDto;
import main.model.dto.integrations.references.IssueReferenceDto;
import main.model.dto.integrations.references.ReferenceType;
import main.model.dto.integrations.references.TestReferenceDto;
import main.model.dto.integrations.references.TestRunReferenceDto;
import main.model.dto.integrations.systems.SystemDto;
import main.model.dto.integrations.systems.SystemTypeDto;
import main.model.dto.integrations.tts.TestTrackingStatusDto;
import main.model.dto.integrations.tts.TestTrackingTypeDto;

import java.util.function.Supplier;

public class ControllerType<T extends BaseDto, D extends DAO<T>> {

    public static final ControllerType<TestReferenceDto, ReferenceDao<TestReferenceDto>> REF_TEST_CONTROLLER = new ControllerType<TestReferenceDto, ReferenceDao<TestReferenceDto>>(TestReferenceDto::new, () -> new ReferenceDao<>(ReferenceType.TEST), TestReferenceDto.class);
    public static final ControllerType<TestRunReferenceDto, ReferenceDao<TestRunReferenceDto>> REF_TESTRUN_CONTROLLER = new ControllerType<TestRunReferenceDto, ReferenceDao<TestRunReferenceDto>>(TestRunReferenceDto::new, () -> new ReferenceDao<>(ReferenceType.TEST_RUN), TestRunReferenceDto.class);
    public static final ControllerType<IssueReferenceDto, ReferenceDao<IssueReferenceDto>> REF_ISSUE_CONTROLLER = new ControllerType<IssueReferenceDto, ReferenceDao<IssueReferenceDto>>(IssueReferenceDto::new, () -> new ReferenceDao<>(ReferenceType.ISSUE), IssueReferenceDto.class);
    public static final ControllerType<TestTrackingStatusDto, TestTrackingStatusDao> TTS_STATUS_CONTROLLER = new ControllerType<>(TestTrackingStatusDto::new, TestTrackingStatusDao::new, TestTrackingStatusDto.class);
    public static final ControllerType<SystemDto, SystemDao> SYSTEM_CONTROLLER = new ControllerType<>(SystemDto::new, SystemDao::new, SystemDto.class);
    public static final ControllerType<SystemTypeDto, SystemTypeDao> SYSTEM_TYPE_CONTROLLER = new ControllerType<>(SystemTypeDto::new, SystemTypeDao::new, SystemTypeDto.class);
    public static final ControllerType<TestTrackingTypeDto, TestTrackingTypeDao> TTS_TYPE_CONTROLLER = new ControllerType<>(TestTrackingTypeDto::new, TestTrackingTypeDao::new, TestTrackingTypeDto.class);

    private final Supplier<T> dtoSupplier;
    private final Supplier<D> daoSupplier;
    private final Class<T> dtoClass;

    private ControllerType(Supplier<T> dtoSupplier, Supplier<D> daoSupplier, Class<T> dtoClass) {
        this.dtoSupplier = dtoSupplier;
        this.daoSupplier = daoSupplier;
        this.dtoClass = dtoClass;
    }

    D createDao() {
        return daoSupplier.get();
    }

    public T createDto() {
        return dtoSupplier.get();
    }

    public Class<T> getDtoClass() {
        return dtoClass;
    }
}

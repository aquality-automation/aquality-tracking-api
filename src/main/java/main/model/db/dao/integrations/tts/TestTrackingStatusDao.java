package main.model.db.dao.integrations.tts;

import main.model.db.dao.DAO;
import main.model.dto.integrations.tts.TestTrackingStatusDto;

public class TestTrackingStatusDao extends DAO<TestTrackingStatusDto> {

    public TestTrackingStatusDao() {
        super(TestTrackingStatusDto.class);
        select = "{call SELECT_INT_SYSTEM_TTS_STATUS(?,?,?,?,?,?,?,?)}";
        insert = "{call INSERT_INT_SYSTEM_TTS_STATUS(?,?,?,?,?,?,?,?)}";
        remove = "{call REMOVE_INT_SYSTEM_TTS_STATUS(?)}";
    }
}

package main.model.db.dao.integrations.tts;

import main.model.db.dao.DAO;
import main.model.dto.integrations.tts.TestTrackingTypeDto;

public class TestTrackingTypeDao extends DAO<TestTrackingTypeDto> {

    public TestTrackingTypeDao(){
        super(TestTrackingTypeDto.class);
        select = "{call SELECT_INT_TTS_TYPE()}";
    }
}

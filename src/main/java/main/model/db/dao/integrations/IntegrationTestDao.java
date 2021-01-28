package main.model.db.dao.integrations;

import main.model.db.dao.DAO;
import main.model.dto.integrations.IntegrationTestDto;

public class IntegrationTestDao extends DAO<IntegrationTestDto> {

    public IntegrationTestDao() {
        super(IntegrationTestDto.class);
        select = "{call SELECT_INTEGRATION_TEST(?,?,?,?,?)}";
        insert = "{call INSERT_INTEGRATION_TEST(?,?,?,?,?)}";
        // TODO: remove = "{call REMOVE_SUITE(?)}";
    }
}

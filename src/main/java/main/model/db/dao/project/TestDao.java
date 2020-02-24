package main.model.db.dao.project;

import com.mysql.cj.core.conf.url.ConnectionUrlParser;
import main.exceptions.AqualityException;
import main.model.db.dao.DAO;
import main.model.dto.DtoMapper;
import main.model.dto.LastResultColorsDto;
import main.model.dto.TestDto;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TestDao extends DAO<TestDto> {
    public TestDao() {
        super(TestDto.class);
        select = "{call SELECT_TEST(?,?,?,?,?,?)}";
        insert = "{call INSERT_TEST(?,?,?,?,?,?)}";
        remove = "{call REMOVE_TEST(?)}";
    }

    public LastResultColorsDto getLastColors(@NotNull Integer testId, @NotNull Integer limit) throws AqualityException {
        DtoMapper<LastResultColorsDto> lastResultColorsMapper = new DtoMapper<>(LastResultColorsDto.class);
        List<ConnectionUrlParser.Pair<String, String>> parameters = new ArrayList<>();
        parameters.add(new ConnectionUrlParser.Pair<>("request_test_id", testId.toString()));
        parameters.add(new ConnectionUrlParser.Pair<>("request_limit", limit.toString()));
        return lastResultColorsMapper.mapObjects(CallStoredProcedure("{call SELECT_LAST_RESULT_COLORS(?,?)}", parameters).toString()).get(0);
    }
}

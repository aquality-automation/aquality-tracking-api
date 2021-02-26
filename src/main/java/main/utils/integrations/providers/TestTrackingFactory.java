package main.utils.integrations.providers;

import main.model.dto.integrations.FixedType;
import main.model.dto.integrations.systems.SystemDto;
import main.model.dto.integrations.tts.TestTrackingType;
import main.utils.integrations.ITestTrackingApi;
import main.utils.integrations.atlassian.xray.XrayApi;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TestTrackingFactory {

    private TestTrackingFactory(){
        // factory method class
    }

    public static ITestTrackingApi getTestTracking(SystemDto system){
        int typeId = system.getInt_tts_type();
        FixedType type = FixedType.getType(Arrays.stream(TestTrackingType.values()).map(TestTrackingType::getType).collect(Collectors.toList()), typeId);
        if(type.getId() == TestTrackingType.XRAY.getType().getId()){
            String url = system.getUrl();
            String username = system.getUsername();
            String password = system.getPassword();
            return new XrayApi(url, username, password);
        }
        throw new IllegalArgumentException("Test Tracking Type Id " + typeId + " is not supported. Following type are supported: " + Arrays.toString(TestTrackingType.values()));
    }
}

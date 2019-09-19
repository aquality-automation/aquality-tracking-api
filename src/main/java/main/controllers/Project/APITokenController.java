package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.project.APITokenDao;
import main.model.dto.APITokenDto;
import main.model.dto.UserDto;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.util.NotImplemented;

import java.util.List;
import java.util.Objects;

public class APITokenController extends BaseController<APITokenDto> {
    private APITokenDao importTokenDao;
    public APITokenController(UserDto user) {
        super(user);
        importTokenDao = new APITokenDao();
    }

    @Override
    public List<APITokenDto> get(APITokenDto entity) throws AqualityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public APITokenDto create(APITokenDto template) throws AqualityException {
        if(baseUser.isAdmin() || baseUser.isManager() || baseUser.getProjectUser(template.getId()).isManager() || baseUser.getProjectUser(template.getId()).isAdmin()){
            return importTokenDao.create(template);
        }else{
            throw new AqualityPermissionsException("Account is not allowed to create API Token", baseUser);
        }
    }

    @Override @NotImplemented
    public boolean delete(APITokenDto entity) throws AqualityException {
        throw new UnsupportedOperationException();
    }

    public boolean isTokenValid(String token, Integer projectId) throws AqualityException {
        String actualHash = DigestUtils.md5Hex(token + "advbc1671-nlksdui-ff");
        APITokenDto tokenDTO = new APITokenDto();
        tokenDTO.setId(projectId);
        List<APITokenDto> importTokens = importTokenDao.searchAll(tokenDTO);

        if(importTokens.size() > 0){
            String expectedHash = (importTokens.get(0)).getApi_token();
            return Objects.equals(actualHash, expectedHash);
        }

        return false;
    }
}

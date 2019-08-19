package main.controllers.Project;

import main.controllers.BaseController;
import main.exceptions.AqualityException;
import main.exceptions.AqualityPermissionsException;
import main.model.db.dao.project.ImportTokenDao;
import main.model.dto.ImportTokenDto;
import main.model.dto.UserDto;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.util.NotImplemented;

import java.util.List;
import java.util.Objects;

public class ImportTokenController extends BaseController<ImportTokenDto> {
    private ImportTokenDao importTokenDao;
    public ImportTokenController(UserDto user) {
        super(user);
        importTokenDao = new ImportTokenDao();
    }

    @Override
    public List<ImportTokenDto> get(ImportTokenDto entity) throws AqualityException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImportTokenDto create(ImportTokenDto template) throws AqualityException {
        if(baseUser.isAdmin() || baseUser.isManager() || baseUser.getProjectUser(template.getId()).isManager() || baseUser.getProjectUser(template.getId()).isAdmin()){
            return importTokenDao.create(template);
        }else{
            throw new AqualityPermissionsException("Account is not allowed to create Import Token", baseUser);
        }
    }

    @Override @NotImplemented
    public boolean delete(ImportTokenDto entity) throws AqualityException {
        throw new UnsupportedOperationException();
    }

    public boolean isTokenValid(String token, Integer projectId) throws AqualityException {
        String actualHash = DigestUtils.md5Hex(token + "advbc1671-nlksdui-ff");
        ImportTokenDto tokenDTO = new ImportTokenDto();
        tokenDTO.setId(projectId);
        List<ImportTokenDto> importTokens = importTokenDao.searchAll(tokenDTO);

        if(importTokens.size() > 0){
            String expectedHash = (importTokens.get(0)).getImport_token();
            return Objects.equals(actualHash, expectedHash);
        }

        return false;
    }
}

package main.controllers;

import main.exceptions.AqualityException;
import main.model.db.dao.DAO;
import main.model.dto.BaseDto;
import main.model.dto.settings.UserDto;

import java.util.List;

public class ReadonlyController<T extends BaseDto, D extends DAO<T>> extends BaseController<T> {
    protected D dao;

    public ReadonlyController(UserDto user, D dao) {
        super(user);
        this.dao = dao;
    }

    @Override
    public List<T> get(T entity) throws AqualityException {
        return dao.searchAll(entity);
    }

    @Override
    public T create(T entity) throws AqualityException {
        throw getUnsupportedException("CREATE");
    }

    @Override
    public boolean delete(T entity) throws AqualityException {
        throw getUnsupportedException("DELETE");
    }

    private AqualityException getUnsupportedException(String operationName) {
        return new AqualityException("Operation " + operationName + " is not supported for integration_systems table. This table includes list of constants.");
    }
}

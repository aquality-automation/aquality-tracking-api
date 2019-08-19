package main.controllers;

import main.exceptions.AqualityException;
import main.model.dto.BaseDto;
import java.util.List;

public interface IController <T extends BaseDto> {

    /**
     * Get Complete Entity
     * @param entity search template
     * @return List of entities
     */
    List<T> get(T entity) throws AqualityException;

    /**
     * Create entity
     * @param entity to create
     * @return created entity
     */
    T create(T entity) throws AqualityException;

    /**
     * Remove entity
     * @param entity to remove
     * @return true if entity was removed
     */
    boolean delete(T entity) throws AqualityException;
}

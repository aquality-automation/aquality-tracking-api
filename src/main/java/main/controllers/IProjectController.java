package main.controllers;

import main.exceptions.AqualityException;
import main.model.dto.BaseDto;

import java.util.List;

public interface IProjectController<T extends BaseDto> {

    /**
     * Get Complete Entity
     *
     * @param entity    search template
     * @param projectId id of current project
     * @return List of entities
     */
    List<T> get(T entity, Integer projectId) throws AqualityException;

    /**
     * Create entity
     *
     * @param entity    to create
     * @param projectId id of current project
     * @return created entity
     */
    T create(T entity, Integer projectId) throws AqualityException;

    /**
     * Remove entity
     *
     * @param entity    to remove
     * @param projectId id of current project
     * @return true if entity was removed
     */
    boolean delete(T entity, Integer projectId) throws AqualityException;
}

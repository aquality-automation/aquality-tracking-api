package tests.workers.dao;

import main.exceptions.AqualityException;

public interface IDaoTest {

    void searchAllTest() throws AqualityException;

    void insertTest() throws AqualityException;

    void removeTest() throws AqualityException;
}

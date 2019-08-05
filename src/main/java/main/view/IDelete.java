package main.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IDelete {
    void doDelete(HttpServletRequest req, HttpServletResponse resp);
    void doOptions(HttpServletRequest req, HttpServletResponse resp);
}

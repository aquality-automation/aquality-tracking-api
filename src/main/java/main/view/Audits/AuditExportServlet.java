package main.view.Audits;

import main.Session;
import main.view.BaseServlet;
import main.view.IGet;
import main.utils.FileUtils;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/audit/export")
@MultipartConfig
public class AuditExportServlet extends BaseServlet implements IGet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp){
        setGetResponseHeaders(resp);
        String pathToExport = null;
        try {
            Session session = createSession(req);
            Boolean type = !req.getParameterMap().containsKey("type") || req.getParameter("type").equals("xls");
            Boolean all = !req.getParameterMap().containsKey("all") || req.getParameter("all").equals("true");
            pathToExport = session.getAuditController().create(all, type);
            processResponse(resp, pathToExport);
        }catch (Exception e) {
            handleException(resp, e);
        }finally {
            if(pathToExport != null){
                FileUtils fileUtils = new FileUtils();
                fileUtils.removeFile(pathToExport);
            }
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp){
        setOptionsResponseHeaders(resp);
    }
}

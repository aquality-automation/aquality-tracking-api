package main.view.publicApi;

import main.view.BaseServlet;
import main.view.IPost;
import main.view.Project.TestResultAttachmentServlet;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/public/test/result/attachment")
@MultipartConfig
public class PublicTestResultAttachment extends BaseServlet implements IPost {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        new TestResultAttachmentServlet().doPost(req, resp);
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp){
        setOptionsResponseHeaders(resp);
    }
}



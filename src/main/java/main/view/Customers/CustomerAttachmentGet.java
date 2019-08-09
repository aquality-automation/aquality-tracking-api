package main.view.Customers;

import main.Session;
import main.model.dto.CustomerAttachmentDto;
import main.utils.FileUtils;
import main.utils.PathUtils;
import main.view.BaseServlet;
import main.view.IDelete;
import main.view.IGet;
import main.view.IPost;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

@WebServlet("/customer/attachment")
@MultipartConfig
public class CustomerAttachmentGet extends BaseServlet implements IGet, IPost, IDelete {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp){
        setGetResponseHeaders(resp);
        try {
            Session session = createSession(req);
            if (req.getParameterMap().containsKey("id") && req.getParameterMap().containsKey("customer_id")) {
                CustomerAttachmentDto customerAttachmentDto = new CustomerAttachmentDto();
                customerAttachmentDto.setCustomer_id(Integer.parseInt(req.getParameter("customer_id")));
                customerAttachmentDto.setId(Integer.parseInt(req.getParameter("id")));

                customerAttachmentDto = session.getCustomerController().get(customerAttachmentDto).get(0);
                processResponse(resp, customerAttachmentDto.getPath());
            } else {
                resp.setStatus(400);
                setErrorHeader(resp, "You have no specify Attachment Or Customer ID");
            }
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        try {
            Session session = createSession(req);
            if (req.getParameterMap().containsKey("id") && req.getParameterMap().containsKey("customer_id")) {
                CustomerAttachmentDto customerAttachmentDto = new CustomerAttachmentDto();
                customerAttachmentDto.setCustomer_id(Integer.parseInt(req.getParameter("customer_id")));
                customerAttachmentDto.setId(Integer.parseInt(req.getParameter("id")));

                session.getCustomerController().delete(customerAttachmentDto);
            } else {
                resp.setStatus(400);
                setErrorHeader(resp, "You have no specify Attachment Or Customer ID!");
            }
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        setPostResponseHeaders(resp);
        try {
            Session session = createSession(req);
            if (req.getParameterMap().containsKey("customer_id")) {
                FileUtils fileUtils = new FileUtils();
                List<String> filePaths = fileUtils.doUpload(req, resp, PathUtils.createPathToBin(new String[]{"customers", getStringQueryParameter(req, "customer_id")}));
                for (String filePath : filePaths) {
                    CustomerAttachmentDto customerAttachmentDto = new CustomerAttachmentDto();
                    customerAttachmentDto.setPath(filePath);
                    customerAttachmentDto.setCustomer_id(Integer.parseInt(req.getParameter("customer_id")));
                    session.getCustomerController().create(customerAttachmentDto);
                }
            } else {
                resp.setStatus(400);
                setErrorHeader(resp, "You have no specify Customer ID!");
            }
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp){
        setOptionsResponseHeaders(resp);
    }
}

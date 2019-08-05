package main.view.Customers;

import main.Session;
import main.model.dto.CustomerDto;
import main.view.BaseServlet;
import main.view.IDelete;
import main.view.IGet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/customer")
public class CustomerGet extends BaseServlet implements IGet, IPost, IDelete {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp){
        setGetResponseHeaders(resp);
        setEncoding(resp);
        try {
            Session session = createSession(req);
            Integer customer_id = req.getParameterMap().containsKey("id") ?  Integer.parseInt(req.getParameter("id")) : null;
            boolean childs = req.getParameterMap().containsKey("withChildren") && Boolean.parseBoolean(req.getParameter("withChildren"));
            CustomerDto customerDto = new CustomerDto();
            customerDto.setId(customer_id);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(session.getCustomerController().get(customerDto, childs)));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp){
        setPostResponseHeaders(resp);
        setEncoding(resp);

        try {
            Session session = createSession(req);
            String requestedJson = getRequestJson(req);
            CustomerDto customer = mapper.mapObject(CustomerDto.class, requestedJson);
            setJSONContentType(resp);
            resp.getWriter().write(mapper.serialize(session.getCustomerController().create(customer)));
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp){
        setDeleteResponseHeaders(resp);

        try {
            Session session = createSession(req);
            CustomerDto customerDto = new CustomerDto();
            customerDto.setId(Integer.parseInt(req.getParameter("id")));
            session.getCustomerController().delete(customerDto);
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp){
        setOptionsResponseHeaders(resp);
    }
}

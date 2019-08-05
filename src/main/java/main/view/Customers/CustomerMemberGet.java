package main.view.Customers;

import main.Session;
import main.model.dto.CustomerMemberDto;
import main.model.dto.UserDto;
import main.view.BaseServlet;
import main.view.IGet;
import main.view.IPost;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/customer/member")
public class CustomerMemberGet extends BaseServlet implements IGet, IPost {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp){
        setGetResponseHeaders(resp);
        try {
            Session session = createSession(req);
            if (req.getParameterMap().containsKey("customer_id")) {
                CustomerMemberDto customerMemberDto = new CustomerMemberDto();
                customerMemberDto.setCustomer_id(Integer.parseInt(req.getParameter("customer_id")));
                List<CustomerMemberDto> members = session.getCustomerController().get(customerMemberDto);
                setJSONContentType(resp);
                resp.getWriter().write(mapper.serialize(members));
            } else {
                resp.setStatus(400);
                setErrorHeader(resp, "You have no specify Customer ID");
            }
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp){
        setPostResponseHeaders(resp);
        try {
            Session session = createSession(req);
            if (req.getParameterMap().containsKey("customer_id")) {
                String requestedJson = getRequestJson(req);
                List<UserDto> users = mapper.mapObjects(UserDto.class, requestedJson);
                List<CustomerMemberDto> members = new ArrayList<>();
                for (UserDto user :  users) {
                    CustomerMemberDto memberDto = (CustomerMemberDto) user;
                    memberDto.setCustomer_id(Integer.parseInt(req.getParameter("audit_id")));
                }
                session.getCustomerController().updateCustomerMember(members);
            } else {
                resp.setStatus(400);
                setErrorHeader(resp, "You have no specify Customer ID!");
            }
        }catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setOptionsResponseHeaders(resp);
    }
}

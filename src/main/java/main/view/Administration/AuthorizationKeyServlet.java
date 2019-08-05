package main.view.Administration;


import com.mysql.cj.core.conf.url.ConnectionUrlParser.Pair;
import main.view.BaseServlet;
import main.view.IGet;
import main.utils.RSA.RSAUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.KeyPair;
import java.util.Base64;
import java.util.Objects;

@WebServlet("/users/authToken")
public class AuthorizationKeyServlet extends BaseServlet implements IGet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        setGetResponseHeaders(resp);
        resp.addHeader("Access-Control-Expose-Headers", "pubKey");
        resp.addHeader("Access-Control-Allow-Headers", "pubKey");
        setEncoding(resp);

        String authString = req.getParameter("username");
        try {
            KeyPair keyPair = RSAUtil.buildKeyPair();
            resp.addHeader("pubKey","-----BEGIN PUBLIC KEY-----" + Base64.getMimeEncoder().encodeToString(keyPair.getPublic().getEncoded()) + "-----END PUBLIC KEY-----");
            RSAUtil.keystore.removeIf(x -> Objects.equals(x.left, authString));
            RSAUtil.keystore.add(new Pair<>(authString, keyPair.getPrivate()));
        } catch (Exception e) {
            handleException(resp, e);
        }
    }

    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setGetResponseHeaders(resp);
        resp.addHeader("Access-Control-Allow-Headers", "pubKey");
    }
}

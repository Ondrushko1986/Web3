package servlet;

import exception.DBException;
import model.BankClient;
import service.BankClientService;
import util.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegistrationServlet extends HttpServlet {
    BankClientService bankClientService = new BankClientService();

    @Override
    public void init() throws ServletException {
        try {
            new BankClientService().cleanUp();
        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println(PageGenerator.getInstance().getPage("registrationPage.html", new HashMap<>()));

        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String password = req.getParameter("password");
        Long money = Long.valueOf(req.getParameter("money"));

        String message = "";

        try {
            if (new BankClientService().addClient(new BankClient(name, password, money))) {
                message = "Add client successful";
            } else {
                message = "Client not add";
            }
        } catch (DBException e) {
            e.printStackTrace();
        }

        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("message", message);
        resp.getWriter().println(PageGenerator.getInstance().getPage("resultPage.html", pageVariables));
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}

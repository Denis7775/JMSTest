import javax.annotation.Resource;
import javax.jms.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet("/send")
public class MessageSender extends HttpServlet {

    @Resource(name = "jms/GlassFishBookConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(name = "jms/GlassFishBookQueue")
    private Destination queue;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            while (true) {
                Connection connection = connectionFactory.createConnection();
                connection.start();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageProducer producer = session.createProducer(queue);
                TextMessage message = session.createTextMessage();
                message.setText(req.getParameter("msg"));
                producer.send(message);
                session.close();
                connection.close();
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/index.jsp");
                requestDispatcher.forward(req, resp);
            }
        } catch (JMSException ex) {
            System.err.println("Sending message error");
            ex.printStackTrace();
        }
    }
}
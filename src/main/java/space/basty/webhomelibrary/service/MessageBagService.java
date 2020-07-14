package space.basty.webhomelibrary.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

@Service
public class MessageBagService {
    public static final String MSG_BAG_ATTR = "msgBag";

    public static final String PRIMARY = "primary";
    public static final String SECONDARY = "secondary";
    public static final String SUCCESS = "success";
    public static final String DANGER = "danger";
    public static final String WARNING = "warning";
    public static final String INFO = "info";
    public static final String LIGHT = "light";
    public static final String DARK = "dark";

    public void addMessageToSession(String level, String message) {
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
        if (session.getAttribute(MSG_BAG_ATTR) == null || !( session.getAttribute(MSG_BAG_ATTR) instanceof ArrayList<?>)) {
            session.setAttribute(MSG_BAG_ATTR, new ArrayList<Message>());
        }

        ((ArrayList<Message>) session.getAttribute(MSG_BAG_ATTR)).add(new Message(level, message));
    }

    public ArrayList<Message> emptyMessagBag() {
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
        ArrayList<Message> messages = (ArrayList<Message>) session.getAttribute(MSG_BAG_ATTR);
        session.removeAttribute(MSG_BAG_ATTR);

        return messages;
    }
}

@Getter
@AllArgsConstructor
class Message {
    private String level;
    private String message;
}

package hu.gerviba.webschop;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.HtmlUtils;

import hu.gerviba.webschop.model.UserEntity;

@Deprecated
public final class WebschopUtils {

    public static UserEntity getUser(HttpServletRequest request) {
        return (UserEntity) request.getSession().getAttribute("user");
    }
    
    @Deprecated
    public static String htmlEscape(String in) {
        return HtmlUtils.htmlEscape(in)
                .replace("Ű", "&#368;").replace("ű", "&#369;")
                .replace("Ő", "&#336;").replace("ő", "&#337;");
    }
    
}

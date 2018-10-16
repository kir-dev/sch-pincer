package hu.gerviba.webschop;

import javax.servlet.http.HttpServletRequest;

import hu.gerviba.webschop.model.UserEntity;

@Deprecated
public final class WebschopUtils {

    public static UserEntity getUser(HttpServletRequest request) {
        return (UserEntity) request.getSession().getAttribute("user");
    }
    
}

package hu.gerviba.webschop.web;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import hu.gerviba.webschop.model.UserEntity;

public class ControllerUtil {

    public ControllerUtil() {}
    
    @Value("${webschop.external}")
    private String uploadPath = "/etc/webschop/external/";
    
    public String uploadFile(String target, MultipartFile logo) {
        if (logo.isEmpty() || logo.getContentType() == null)
            return null;
        
        String path = !uploadPath.startsWith("/") ? System.getProperty("user.dir") + "/" + uploadPath : uploadPath;
        File dir = new File(path, target);
        dir.mkdirs();
        
        String fileName = new UUID(System.currentTimeMillis(), new Random().nextLong()).toString() 
                + logo.getOriginalFilename().substring(logo.getOriginalFilename().contains(".") 
                        ? logo.getOriginalFilename().lastIndexOf('.') : 0);
        path += (path.endsWith("/") ? "" : "/") + target + "/" + fileName;
        
        try {
            logo.transferTo(new File(path));
        } catch (IOException e) {
            return null;
        }
        
        return fileName;
    }
    
    public UserEntity getUser(HttpServletRequest request) {
        return (UserEntity) request.getSession().getAttribute("user");
    }
    
}

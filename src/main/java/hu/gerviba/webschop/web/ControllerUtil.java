package hu.gerviba.webschop.web;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import hu.gerviba.webschop.model.UserEntity;

public class ControllerUtil {

    public ControllerUtil() {}
    
    @Value("${schpincer.external}")
    String uploadPath = "/etc/schpincer/external/";
    
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

    public String sha256(String in) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return String.format("%064x", new BigInteger(1, digest.digest(in.getBytes(StandardCharsets.UTF_8))));
    }

    public boolean cannotEditCircle(Long circleId, HttpServletRequest request) {
        UserEntity user = getUser(request);
        return !((user.getPermissions() != null 
                && user.getPermissions().contains("ROLE_LEADER")
                && user.getPermissions().contains("CIRCLE_" + circleId)) || user.isSysadmin());
    }
    
    public boolean cannotEditCircleNoPR(Long circleId, HttpServletRequest request) {
        UserEntity user = getUser(request);
        return !((user.getPermissions() != null 
                && user.getPermissions().contains("ROLE_LEADER")
                && user.getPermissions().contains("CIRCLE_" + circleId)
                && !user.getPermissions().contains("PR_" + circleId)) || user.isSysadmin());
    }
    
    public boolean isPR(Long circleId, HttpServletRequest request) {
        UserEntity user = getUser(request);
        return user.getPermissions().contains("PR_" + circleId);
    }
    
    public Long parseDate(String in) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
        Date date;
        try {
            date = dateFormat.parse(in);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
        return date.getTime();
    }

    public String formatDate(Long date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm");
        return dateFormat.format(date);
    }

    public boolean isInInternalNetwork(HttpServletRequest request) {
        return request.getRemoteAddr().startsWith("152.66.") || request.getRemoteAddr().equals("127.0.0.1");
    }
    
}

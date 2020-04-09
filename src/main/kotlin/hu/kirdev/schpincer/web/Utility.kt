package hu.kirdev.schpincer.web

import hu.kirdev.schpincer.model.UserEntity
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.http.HttpServletRequest

fun MultipartFile.uploadFile(target: String, uploadPath: String): String? {
    if (this.isEmpty || this.contentType == null)
        return null
    var path = if (!uploadPath.startsWith("/")) System.getProperty("user.dir") + "/" + uploadPath else uploadPath
    val dir = File(path, target)
    dir.mkdirs()
    val originalFilename = this.originalFilename ?: ""
    val fileName = (UUID(System.currentTimeMillis(), Random().nextLong()).toString()

            + originalFilename.substring(if (originalFilename.contains(".")) originalFilename.lastIndexOf('.') else 0))
    path += (if (path.endsWith("/")) "" else "/") + "$target/$fileName"
    try {
        this.transferTo(File(path))
    } catch (e: IOException) {
        return null
    }
    return fileName
}

fun HttpServletRequest.getUser(): UserEntity {
    return this.session.getAttribute("user") as UserEntity
}

@Throws(NoSuchAlgorithmException::class)
fun String.sha256(): String {
    val digest = MessageDigest.getInstance("SHA-256")
    return String.format("%064x", BigInteger(1, digest.digest(this.toByteArray(StandardCharsets.UTF_8))))
}

fun cannotEditCircle(circleId: Long, request: HttpServletRequest): Boolean {
    val (_, _, _, _, sysadmin, _, permissions) = request.getUser()
    return !((permissions.contains("ROLE_LEADER") && permissions.contains("CIRCLE_$circleId")) || sysadmin)
}

fun cannotEditCircleNoPR(circleId: Long, request: HttpServletRequest): Boolean {
    val (_, _, _, _, sysadmin, _, permissions) = request.getUser()
    return !((permissions.contains("ROLE_LEADER") && permissions.contains("CIRCLE_$circleId") && !permissions.contains("PR_$circleId")) || sysadmin)
}

fun isPR(circleId: Long, request: HttpServletRequest): Boolean {
    val (_, _, _, _, _, _, permissions) = request.getUser()
    return permissions.contains("PR_$circleId")
}

val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm")

fun parseDate(dateToParse: String): Long {
    val date: Date
    date = try {
        dateFormat.parse(dateToParse)
    } catch (e: ParseException) {
        e.printStackTrace()
        return 0L
    }
    return date.time
}

fun formatDate(date: Long): String {
    val dateFormat: DateFormat = SimpleDateFormat("yyyy.MM.dd hh:mm")
    return dateFormat.format(date)
}

fun HttpServletRequest.isInInternalNetwork(): Boolean {
    return this.remoteAddr.startsWith("152.66.") || this.remoteAddr == "127.0.0.1"
}

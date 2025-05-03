package hu.kirdev.schpincer.web

import hu.kirdev.schpincer.dto.CircleMemberRole
import hu.kirdev.schpincer.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
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
import jakarta.servlet.http.HttpServletRequest

@Component
class DI {
    companion object {
        lateinit var instance: DI
    }

    init {
        instance = this
    }

    @Autowired
    lateinit var users: UserService
    @Value("\${schpincer.external:/etc/schpincer/external}")
    lateinit var uploadPath: String
}

fun MultipartFile.uploadFile(target: String): String? {
    if (this.isEmpty || this.contentType == null)
        return null
    var path = if (!DI.instance.uploadPath.startsWith("/")) System.getProperty("user.dir") + "/" + DI.instance.uploadPath
    else DI.instance.uploadPath
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

fun HttpServletRequest.hasUser() = this.session.getAttribute(USER_SESSION_ATTRIBUTE_NAME) != null

fun HttpServletRequest.getUser() = DI.instance.users.getById(this.session.getAttribute(USER_SESSION_ATTRIBUTE_NAME) as String)

fun HttpServletRequest.getUserIfPresent() = if (hasUser()) DI.instance.users.getByIdOrNull(this.session.getAttribute(USER_SESSION_ATTRIBUTE_NAME) as String) else null

fun HttpServletRequest.getUserId() = this.session.getAttribute(USER_SESSION_ATTRIBUTE_NAME) as String

fun HttpServletRequest.getOwnedCircles() = this.session.getAttribute(CIRCLE_OWNERSHIP_SESSION_ATTRIBUTE_NAME) as List<*>

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
    return request.getUser().permissions.contains("PR_$circleId")
}

fun isCircleOwner(circleId: Long, request: HttpServletRequest): Boolean {
    return request.getOwnedCircles().contains(circleId)
}

fun toReadableRole(permissions: Set<String>, circleID: Long): CircleMemberRole {
    val isLeader = permissions.contains("ROLE_LEADER")
    val isCircleOwner = permissions.contains("CIRCLE_${circleID}")
    val isPr = permissions.contains("PR_${circleID}")
    return if (!isLeader || !isCircleOwner) CircleMemberRole.NONE
    else if (isPr) CircleMemberRole.PR else CircleMemberRole.LEADER
}

val dateFormat: DateFormat by lazy { SimpleDateFormat("yyyy-MM-dd hh:mm") }

fun parseDate(dateToParse: String): Long {
    val date: Date = try {
        dateFormat.parse(dateToParse)
    } catch (e: ParseException) {
        e.printStackTrace()
        return 0L
    }
    return date.time
}

fun formatDate(date: Long): String {
    return dateFormat.format(date)
}

fun HttpServletRequest.isInInternalNetwork(): Boolean {
    return false
}

val nonAscii = Regex("[^\\x00-\\u0170]")
val controlChars = Regex("[\\p{Cntrl}&&[^\r\n\t]]")
val nonPrintable = Regex("\\p{C}")

fun String.removeNonPrintable() =
    this.replace(nonAscii, "")
        .replace(controlChars, "")
        .replace(nonPrintable, "")
        .trim()

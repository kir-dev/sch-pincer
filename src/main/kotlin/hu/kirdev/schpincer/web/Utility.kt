package hu.kirdev.schpincer.web

import hu.kirdev.schpincer.dto.CircleMemberRole
import hu.kirdev.schpincer.model.CircleMembership
import hu.kirdev.schpincer.model.SchPincerOidcUser
import hu.kirdev.schpincer.service.CircleService
import hu.kirdev.schpincer.service.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
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
import java.time.Instant
import java.util.*

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
    var path =
        if (!DI.instance.uploadPath.startsWith("/")) System.getProperty("user.dir") + "/" + DI.instance.uploadPath
        else DI.instance.uploadPath
    val dir = File(path, target)
    dir.mkdirs()
    val originalFilename = this.originalFilename ?: ""
    val fileName = (UUID(Instant.now().toEpochMilli(), Random().nextLong()).toString()
            + originalFilename.substring(if (originalFilename.contains(".")) originalFilename.lastIndexOf('.') else 0))
    path += (if (path.endsWith("/")) "" else "/") + "$target/$fileName"
    try {
        this.transferTo(File(path))
    } catch (e: IOException) {
        return null
    }
    return fileName
}

fun Authentication?.hasUser() = getUserId() != null

fun Authentication?.getUser() = DI.instance.users.getById(getUserId()!!)

fun Authentication?.getUserIfPresent() = if (hasUser()) getUser() else null

fun Authentication?.getUserId() = (this?.principal as? SchPincerOidcUser)?.internalId

fun Authentication?.getOwnedCircles(circleService: CircleService) =
    getOwnedCircleIds((this?.principal!! as SchPincerOidcUser).memberships, circleService)

fun getOwnedCircleIds(memberships: List<CircleMembership>, circleService: CircleService): List<Long> {
    return memberships
        .filter { it.title.any { it.lowercase().matches("^k[oö]rvezet[oöő]$".toRegex()) } }
        .mapNotNull { circleService.findByVirGroupId(it.id)?.id }
}


@Throws(NoSuchAlgorithmException::class)
fun String.sha256(): String {
    val digest = MessageDigest.getInstance("SHA-256")
    return String.format("%064x", BigInteger(1, digest.digest(this.toByteArray(StandardCharsets.UTF_8))))
}

fun cannotEditCircle(circleId: Long, auth: Authentication?): Boolean {
    val (_, _, _, _, sysadmin, _, permissions) = auth.getUser()
    return !((permissions.contains("ROLE_LEADER") && permissions.contains("CIRCLE_$circleId")) || sysadmin)
}

fun cannotEditCircleNoPR(circleId: Long, auth: Authentication?): Boolean {
    val (_, _, _, _, sysadmin, _, permissions) = auth.getUser()
    return !((permissions.contains("ROLE_LEADER") && permissions.contains("CIRCLE_$circleId") && !permissions.contains("PR_$circleId")) || sysadmin)
}

fun isPR(circleId: Long, auth: Authentication?): Boolean {
    return auth.getUser().permissions.contains("PR_$circleId")
}

fun isCircleOwner(circleId: Long, circleService: CircleService, auth: Authentication?): Boolean {
    return auth.getOwnedCircles(circleService).contains(circleId)
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

package hu.kirdev.schpincer.web

import hu.kirdev.schpincer.config.Role
import hu.kirdev.schpincer.dao.UserRepository
import hu.kirdev.schpincer.dto.CircleMemberRole
import hu.kirdev.schpincer.model.ExecutiveAt
import hu.kirdev.schpincer.model.SchPincerOidcUser
import hu.kirdev.schpincer.model.UserEntity
import hu.kirdev.schpincer.service.CircleService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.Authentication
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import kotlin.jvm.optionals.getOrNull

fun MultipartFile.uploadFile(uploadPath: String, target: String): String? {
    if (this.isEmpty || this.contentType == null)
        return null
    var path =
        if (uploadPath.startsWith("/")) System.getProperty("user.dir") + "/" + uploadPath
        else uploadPath
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

fun Authentication?.getUser(users: UserRepository): UserEntity? = getUserId()?.let { users.findById(it) }?.getOrNull()

fun Authentication?.getUserId() = (this?.principal as? SchPincerOidcUser)?.internalId

fun Authentication?.getOwnedCircles(circleService: CircleService) =
    getOwnedCircleIds((this?.principal!! as SchPincerOidcUser).executiveAtCircles, circleService)

fun getOwnedCircleIds(executiveAt: List<ExecutiveAt>, circleService: CircleService): List<Long> {
    return executiveAt.mapNotNull { circleService.findByVirGroupId(it.id)?.id }
}


fun cannotEditCircle(users: UserRepository, circleId: Long, auth: Authentication?): Boolean {
    val (_, _, _, _, sysadmin, _, _, permissions) = auth.getUser(users) ?: return true
    return !((permissions.contains("ROLE_${Role.LEADER.name}") && permissions.contains("CIRCLE_$circleId")) || sysadmin)
}

fun cannotEditCircleNoPR(users: UserRepository, circleId: Long, auth: Authentication?): Boolean {
    val (_, _, _, _, sysadmin, _, _, permissions) = auth.getUser(users) ?: return true
    return !((permissions.contains("ROLE_${Role.LEADER.name}") && permissions.contains("CIRCLE_$circleId") && !permissions.contains("PR_$circleId")) || sysadmin)
}

fun isPR(users: UserRepository, circleId: Long, auth: Authentication?): Boolean {
    return auth.getUser(users)?.permissions?.contains("PR_$circleId") ?: false
}

fun isCircleOwner(circleId: Long, circleService: CircleService, auth: Authentication?): Boolean {
    return auth.getOwnedCircles(circleService).contains(circleId)
}

fun toReadableRole(permissions: Set<String>, circleID: Long): CircleMemberRole {
    val isLeader = permissions.contains("ROLE_${Role.LEADER.name}")
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

val nonAscii = Regex("[^\\x00-\\u0170]")
val controlChars = Regex("[\\p{Cntrl}&&[^\r\n\t]]")
val nonPrintable = Regex("\\p{C}")

fun String.removeNonPrintable() =
    this.replace(nonAscii, "")
        .replace(controlChars, "")
        .replace(nonPrintable, "")
        .trim()

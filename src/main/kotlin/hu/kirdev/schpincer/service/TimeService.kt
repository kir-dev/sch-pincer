package hu.kirdev.schpincer.service

import org.springframework.stereotype.Service
import java.text.SimpleDateFormat

@Service
class TimeService {

    private val dateTimeByFormat = mutableMapOf<String, SimpleDateFormat>()

    fun format(time: Any?, format: String): String? {
        return if (time == null) null else dateTimeByFormat
                .computeIfAbsent(format, { SimpleDateFormat(it) })
                .format(time)
    }

}
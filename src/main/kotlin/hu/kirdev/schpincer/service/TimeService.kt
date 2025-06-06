package hu.kirdev.schpincer.service

import org.springframework.stereotype.Service
import java.text.SimpleDateFormat

@Service
class TimeService {

    fun format(time: Any?, format: String): String? {
        return if (time == null) null else SimpleDateFormat(format).format(time)
    }

}

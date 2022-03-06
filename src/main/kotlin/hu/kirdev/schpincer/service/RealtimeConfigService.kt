package hu.kirdev.schpincer.service

import org.springframework.stereotype.Service
import org.springframework.ui.Model

open class ConfigObject(
    var messageBoxType: String = "",
    var messageBoxMessage: String = ""
) {
    constructor(o: RealtimeConfigService) :
        this(messageBoxType = o.messageBoxType, messageBoxMessage = o.messageBoxMessage)
}

@Service
class RealtimeConfigService : ConfigObject() {

    fun injectPublicValues(model: Model) {
        model.addAttribute("messageBoxType", messageBoxType)
        model.addAttribute("messageBoxMessage", messageBoxMessage)
        model.addAttribute("appVersion", javaClass.getPackage().implementationVersion)
    }

    fun persist() {
        // Currently, no values stored in persistent storage
    }

}

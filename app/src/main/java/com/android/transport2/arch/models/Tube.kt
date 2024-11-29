package com.android.transport2.arch.models

import com.android.transport2.arch.managers.TubeManager
import java.io.Serializable

data class Tube(
    val id: TubeManager.TubeLine, //id for line
    val name: String, // name of line
    val colour: Int, // colour of line
    val mode: String, // the mode of line e.g. tube, DLR, overground
    //val disruptions: String?,
    val statusSeverityId: Int, // unsure how this changes 10 is good 9 is minor delays
    val status: String, // the status e.g. Good service, Minor Delays
    val reason: String, // a brief description of the reason for the delay
    val stops: List<TubeStop>? = null
):Serializable{
    companion object{
        fun fromTemplate(template: TubeTemplate) : Tube {
            val line = TubeManager.TubeLine.stringToTubeLine(template.id)
            val reason = if(template.lineStatuses[0].reason.isNullOrEmpty()) "Good service, No issues reported" else template.lineStatuses[0].reason.orEmpty()
            return Tube(
                line,
                template.name.let { if ("line" in it) it.dropLast(5) else it },
                line.color,
                template.modeName,
                template.lineStatuses[0].statusSeverity,
                template.lineStatuses[0].statusSeverityDescription,
                reason
            ) //template.disruptions.name
        }
    }
}
// disruptions currently has no use
data class TubeTemplate(
    val id: String,
    val name: String,
    val modeName: String,
    //val disruptions: Disruptions,
    val lineStatuses: List<LineStatus>
)

data class Disruptions(
    val name: String?
)

data class LineStatus(
    val id: Int,
    val statusSeverity: Int,
    val statusSeverityDescription: String,
    val reason: String?
)



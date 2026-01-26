package org.acme.mapper

import org.acme.domain.dto.*
import org.acme.domain.entity.*

object EntityMapper {
    
    fun toDto(user: User): UserDto = UserDto(
        id = user.id,
        email = user.email,
        name = user.name,
        avatarUrl = user.avatarUrl,
        isActive = user.isActive
    )
    
    fun toDto(meetingSeries: MeetingSeries): MeetingSeriesDto = MeetingSeriesDto(
        id = meetingSeries.id,
        name = meetingSeries.name,
        description = meetingSeries.description,
        createdBy = toDto(meetingSeries.createdBy!!),
        isActive = meetingSeries.isActive,
        createdAt = meetingSeries.createdAt,
        updatedAt = meetingSeries.updatedAt
    )
    
    fun toDto(member: MeetingSeriesMember): MeetingSeriesMemberDto = MeetingSeriesMemberDto(
        id = member.id,
        user = member.user?.let { toDto(it) },
        email = member.email,
        role = member.role,
        invitationSentAt = member.invitationSentAt,
        joinedAt = member.joinedAt
    )
    
    fun toDto(appointment: Appointment, plannedMembers: List<User>, attendedMembers: List<User>): AppointmentDto = AppointmentDto(
        id = appointment.id,
        meetingSeriesId = appointment.meetingSeries?.id!!,
        scheduledTime = appointment.scheduledTime,
        actualTime = appointment.actualTime,
        durationMinutes = appointment.durationMinutes,
        location = appointment.location,
        notes = appointment.notes,
        isCancelled = appointment.isCancelled,
        plannedMembers = plannedMembers.map { toDto(it) },
        attendedMembers = attendedMembers.map { toDto(it) }
    )
    
    fun toDto(area: Area): AreaDto = AreaDto(
        id = area.id,
        name = area.name,
        description = area.description,
        sortOrder = area.sortOrder
    )
    
    fun toDto(topic: Topic): TopicDto = TopicDto(
        id = topic.id,
        title = topic.title,
        description = topic.description,
        createdBy = toDto(topic.createdBy!!),
        sortOrder = topic.sortOrder,
        isResolved = topic.isResolved,
        createdAt = topic.createdAt
    )
    
    fun toDto(entry: Entry): EntryDto = EntryDto(
        id = entry.id,
        entryType = entry.entryType.name,
        content = entry.content,
        createdBy = toDto(entry.createdBy!!),
        assignedTo = entry.assignedTo?.let { toDto(it) },
        taskStatus = entry.taskStatus?.name,
        taskDueDate = entry.taskDueDate,
        createdAt = entry.createdAt
    )
}

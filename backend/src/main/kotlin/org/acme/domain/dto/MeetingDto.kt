package org.acme.domain.dto
import java.util.UUID

import org.acme.domain.entity.UserRole
import java.time.LocalDateTime

// Meeting Series DTOs
data class MeetingSeriesDto(
    val id: UUID?,
    val name: String,
    val description: String?,
    val createdBy: UserDto,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class CreateMeetingSeriesRequest(
    val name: String,
    val description: String?
)

data class UpdateMeetingSeriesRequest(
    val name: String?,
    val description: String?,
    val isActive: Boolean?
)

// Meeting Series Member DTOs
data class MeetingSeriesMemberDto(
    val id: UUID?,
    val user: UserDto?,
    val email: String?,
    val role: UserRole,
    val invitationSentAt: LocalDateTime?,
    val joinedAt: LocalDateTime?
)

data class InviteMemberRequest(
    val email: String,
    val role: UserRole
)

data class UpdateMemberRoleRequest(
    val role: UserRole
)

// Appointment DTOs
data class AppointmentDto(
    val id: UUID?,
    val meetingSeriesId: UUID,
    val scheduledTime: LocalDateTime,
    val actualTime: LocalDateTime?,
    val durationMinutes: Int?,
    val location: String?,
    val notes: String?,
    val isCancelled: Boolean,
    val plannedMembers: List<UserDto>,
    val attendedMembers: List<UserDto>
)

data class CreateAppointmentRequest(
    val scheduledTime: LocalDateTime,
    val durationMinutes: Int?,
    val location: String?,
    val notes: String?,
    val plannedMemberIds: List<UUID>
)

data class UpdateAppointmentRequest(
    val scheduledTime: LocalDateTime?,
    val actualTime: LocalDateTime?,
    val durationMinutes: Int?,
    val location: String?,
    val notes: String?,
    val isCancelled: Boolean?,
    val attendedMemberIds: List<UUID>?
)

// Area DTOs
data class AreaDto(
    val id: UUID?,
    val name: String,
    val description: String?,
    val sortOrder: Int
)

data class CreateAreaRequest(
    val name: String,
    val description: String?
)

data class UpdateAreaRequest(
    val name: String?,
    val description: String?,
    val sortOrder: Int?
)

// Topic DTOs
data class TopicDto(
    val id: UUID?,
    val title: String,
    val description: String?,
    val createdBy: UserDto,
    val sortOrder: Int,
    val isResolved: Boolean,
    val createdAt: LocalDateTime
)

data class CreateTopicRequest(
    val title: String,
    val description: String?
)

data class UpdateTopicRequest(
    val title: String?,
    val description: String?,
    val sortOrder: Int?,
    val isResolved: Boolean?
)

// Entry DTOs
data class EntryDto(
    val id: UUID?,
    val entryType: String,
    val content: String,
    val createdBy: UserDto,
    val assignedTo: UserDto?,
    val taskStatus: String?,
    val taskDueDate: LocalDateTime?,
    val createdAt: LocalDateTime
)

data class CreateEntryRequest(
    val topicId: UUID,
    val appointmentId: UUID,
    val entryType: String,
    val content: String,
    val assignedToId: UUID?,
    val taskDueDate: LocalDateTime?
)

data class UpdateEntryRequest(
    val content: String?,
    val taskStatus: String?,
    val taskDueDate: LocalDateTime?,
    val assignedToId: UUID?
)

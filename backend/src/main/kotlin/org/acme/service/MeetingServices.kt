package org.acme.service
import java.util.UUID

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.NotFoundException
import org.acme.domain.dto.*
import org.acme.domain.entity.*
import org.acme.mapper.EntityMapper

@ApplicationScoped
class AppointmentService {

    @Inject
    lateinit var authService: AuthService

    @Inject
    lateinit var meetingSeriesService: MeetingSeriesService

    @Transactional
    fun createAppointment(seriesId: UUID, request: CreateAppointmentRequest): AppointmentDto {
        val meetingSeries = MeetingSeries.findById(seriesId)
            ?: throw NotFoundException("Meeting series not found")

        checkAdminOrLeader(meetingSeries)

        val appointment = Appointment().apply {
            this.meetingSeries = meetingSeries
            scheduledTime = request.scheduledTime
            durationMinutes = request.durationMinutes
            location = request.location
            notes = request.notes
        }
        appointment.persist()

        // Add planned members
        request.plannedMemberIds.forEach { userId ->
            val user = User.findById(userId)
            if (user != null) {
                val member = AppointmentMember().apply {
                    this.appointment = appointment
                    this.user = user
                    isPlanned = true
                }
                member.persist()
            }
        }

        val plannedMembers = request.plannedMemberIds.mapNotNull { User.findById(it) }
        return EntityMapper.toDto(appointment, plannedMembers, emptyList())
    }

    fun getAppointment(appointmentId: UUID): AppointmentDto {
        val appointment = Appointment.findById(appointmentId)
            ?: throw NotFoundException("Appointment not found")

        val members = AppointmentMember.findByAppointment(appointmentId)
        val plannedMembers = members.filter { it.isPlanned }.mapNotNull { it.user }
        val attendedMembers = members.filter { it.attended }.mapNotNull { it.user }

        return EntityMapper.toDto(appointment, plannedMembers, attendedMembers)
    }

    fun listAppointments(seriesId: UUID): List<AppointmentDto> {
        val meetingSeries = MeetingSeries.findById(seriesId)
            ?: throw NotFoundException("Meeting series not found")

        return Appointment.findByMeetingSeries(seriesId).map { appointment ->
            val members = AppointmentMember.findByAppointment(appointment.id!!)
            val plannedMembers = members.filter { it.isPlanned }.mapNotNull { it.user }
            val attendedMembers = members.filter { it.attended }.mapNotNull { it.user }
            EntityMapper.toDto(appointment, plannedMembers, attendedMembers)
        }
    }

    @Transactional
    fun updateAppointment(appointmentId: UUID, request: UpdateAppointmentRequest): AppointmentDto {
        val appointment = Appointment.findById(appointmentId)
            ?: throw NotFoundException("Appointment not found")

        checkAdminOrLeader(appointment.meetingSeries!!)

        request.scheduledTime?.let { appointment.scheduledTime = it }
        request.actualTime?.let { appointment.actualTime = it }
        request.durationMinutes?.let { appointment.durationMinutes = it }
        request.location?.let { appointment.location = it }
        request.notes?.let { appointment.notes = it }
        request.isCancelled?.let { appointment.isCancelled = it }

        request.attendedMemberIds?.let { memberIds ->
            val existingMembers = AppointmentMember.findByAppointment(appointmentId)
            memberIds.forEach { userId ->
                val member = existingMembers.find { it.user?.id == userId }
                if (member != null) {
                    member.attended = true
                } else {
                    val user = User.findById(userId)
                    if (user != null) {
                        val newMember = AppointmentMember().apply {
                            this.appointment = appointment
                            this.user = user
                            isPlanned = false
                            attended = true
                        }
                        newMember.persist()
                    }
                }
            }
        }

        val members = AppointmentMember.findByAppointment(appointmentId)
        val plannedMembers = members.filter { it.isPlanned }.mapNotNull { it.user }
        val attendedMembers = members.filter { it.attended }.mapNotNull { it.user }

        return EntityMapper.toDto(appointment, plannedMembers, attendedMembers)
    }

    @Transactional
    fun deleteAppointment(appointmentId: UUID) {
        val appointment = Appointment.findById(appointmentId)
            ?: throw NotFoundException("Appointment not found")

        checkAdmin(appointment.meetingSeries!!)
        appointment.delete()
    }

    private fun checkAdmin(meetingSeries: MeetingSeries) {
        val currentUser = authService.getCurrentUser()
            ?: throw ForbiddenException("Not authenticated")

        if (!MeetingSeriesMember.hasRole(meetingSeries.id!!, currentUser.id!!, UserRole.ADMIN)) {
            throw ForbiddenException("Admin access required")
        }
    }

    private fun checkAdminOrLeader(meetingSeries: MeetingSeries) {
        val currentUser = authService.getCurrentUser()
            ?: throw ForbiddenException("Not authenticated")

        if (!MeetingSeriesMember.hasRole(meetingSeries.id!!, currentUser.id!!, UserRole.ADMIN, UserRole.MEETING_LEADER)) {
            throw ForbiddenException("Admin or Meeting Leader access required")
        }
    }
}

@ApplicationScoped
class AreaService {

    @Inject
    lateinit var authService: AuthService

    @Transactional
    fun createArea(seriesId: UUID, request: CreateAreaRequest): AreaDto {
        val meetingSeries = MeetingSeries.findById(seriesId)
            ?: throw NotFoundException("Meeting series not found")

        checkAdminOrLeader(meetingSeries)

        val area = Area().apply {
            this.meetingSeries = meetingSeries
            name = request.name
            description = request.description
        }
        area.persist()

        return EntityMapper.toDto(area)
    }

    fun listAreas(seriesId: UUID): List<AreaDto> {
        return Area.findByMeetingSeries(seriesId).map { EntityMapper.toDto(it) }
    }

    @Transactional
    fun updateArea(areaId: UUID, request: UpdateAreaRequest): AreaDto {
        val area = Area.findById(areaId) ?: throw NotFoundException("Area not found")
        checkAdminOrLeader(area.meetingSeries!!)

        request.name?.let { area.name = it }
        request.description?.let { area.description = it }
        request.sortOrder?.let { area.sortOrder = it }

        return EntityMapper.toDto(area)
    }

    @Transactional
    fun deleteArea(areaId: UUID) {
        val area = Area.findById(areaId) ?: throw NotFoundException("Area not found")
        checkAdminOrLeader(area.meetingSeries!!)
        area.delete()
    }

    private fun checkAdminOrLeader(meetingSeries: MeetingSeries) {
        val currentUser = authService.getCurrentUser()
            ?: throw ForbiddenException("Not authenticated")

        if (!MeetingSeriesMember.hasRole(meetingSeries.id!!, currentUser.id!!, UserRole.ADMIN, UserRole.MEETING_LEADER)) {
            throw ForbiddenException("Admin or Meeting Leader access required")
        }
    }
}

@ApplicationScoped
class TopicService {

    @Inject
    lateinit var authService: AuthService

    @Transactional
    fun createTopic(areaId: UUID, request: CreateTopicRequest): TopicDto {
        val area = Area.findById(areaId) ?: throw NotFoundException("Area not found")
        checkMemberAccess(area.meetingSeries!!)

        val currentUser = authService.getCurrentUser()!!
        val topic = Topic().apply {
            this.area = area
            title = request.title
            description = request.description
            createdBy = currentUser
        }
        topic.persist()

        return EntityMapper.toDto(topic)
    }

    fun listTopics(areaId: UUID): List<TopicDto> {
        return Topic.findByArea(areaId).map { EntityMapper.toDto(it) }
    }

    @Transactional
    fun updateTopic(topicId: UUID, request: UpdateTopicRequest): TopicDto {
        val topic = Topic.findById(topicId) ?: throw NotFoundException("Topic not found")
        checkMemberAccess(topic.area?.meetingSeries!!)

        request.title?.let { topic.title = it }
        request.description?.let { topic.description = it }
        request.sortOrder?.let { topic.sortOrder = it }
        request.isResolved?.let { topic.isResolved = it }

        return EntityMapper.toDto(topic)
    }

    @Transactional
    fun deleteTopic(topicId: UUID) {
        val topic = Topic.findById(topicId) ?: throw NotFoundException("Topic not found")
        val currentUser = authService.getCurrentUser()!!
        
        // Only creator, admin or meeting leader can delete
        if (topic.createdBy?.id != currentUser.id) {
            checkAdminOrLeader(topic.area?.meetingSeries!!)
        }
        
        topic.delete()
    }

    private fun checkMemberAccess(meetingSeries: MeetingSeries) {
        val currentUser = authService.getCurrentUser()
            ?: throw ForbiddenException("Not authenticated")

        if (!MeetingSeriesMember.hasRole(meetingSeries.id!!, currentUser.id!!, 
            UserRole.ADMIN, UserRole.MEETING_LEADER, UserRole.MEMBER)) {
            throw ForbiddenException("Member access required")
        }
    }

    private fun checkAdminOrLeader(meetingSeries: MeetingSeries) {
        val currentUser = authService.getCurrentUser()
            ?: throw ForbiddenException("Not authenticated")

        if (!MeetingSeriesMember.hasRole(meetingSeries.id!!, currentUser.id!!, UserRole.ADMIN, UserRole.MEETING_LEADER)) {
            throw ForbiddenException("Admin or Meeting Leader access required")
        }
    }
}

@ApplicationScoped
class EntryService {

    @Inject
    lateinit var authService: AuthService

    @Transactional
    fun createEntry(request: CreateEntryRequest): EntryDto {
        val topic = Topic.findById(request.topicId) ?: throw NotFoundException("Topic not found")
        val appointment = Appointment.findById(request.appointmentId) ?: throw NotFoundException("Appointment not found")
        
        checkMemberAccess(topic.area?.meetingSeries!!)

        val currentUser = authService.getCurrentUser()!!
        val entry = Entry().apply {
            this.topic = topic
            this.appointment = appointment
            entryType = EntryType.valueOf(request.entryType)
            content = request.content
            createdBy = currentUser
            assignedTo = request.assignedToId?.let { User.findById(it) }
            taskDueDate = request.taskDueDate
            if (entryType == EntryType.TASK) {
                taskStatus = TaskStatus.OPEN
            }
        }
        entry.persist()

        return EntityMapper.toDto(entry)
    }

    fun listEntriesByTopic(topicId: UUID): List<EntryDto> {
        return Entry.findByTopic(topicId).map { EntityMapper.toDto(it) }
    }

    fun listEntriesByAppointment(appointmentId: UUID): List<EntryDto> {
        return Entry.findByAppointment(appointmentId).map { EntityMapper.toDto(it) }
    }

    @Transactional
    fun updateEntry(entryId: UUID, request: UpdateEntryRequest): EntryDto {
        val entry = Entry.findById(entryId) ?: throw NotFoundException("Entry not found")
        checkMemberAccess(entry.topic?.area?.meetingSeries!!)

        request.content?.let { entry.content = it }
        request.taskStatus?.let { entry.taskStatus = TaskStatus.valueOf(it) }
        request.taskDueDate?.let { entry.taskDueDate = it }
        request.assignedToId?.let { entry.assignedTo = User.findById(it) }

        return EntityMapper.toDto(entry)
    }

    @Transactional
    fun deleteEntry(entryId: UUID) {
        val entry = Entry.findById(entryId) ?: throw NotFoundException("Entry not found")
        val currentUser = authService.getCurrentUser()!!
        
        if (entry.createdBy?.id != currentUser.id) {
            checkAdminOrLeader(entry.topic?.area?.meetingSeries!!)
        }
        
        entry.delete()
    }

    private fun checkMemberAccess(meetingSeries: MeetingSeries) {
        val currentUser = authService.getCurrentUser()
            ?: throw ForbiddenException("Not authenticated")

        if (!MeetingSeriesMember.hasRole(meetingSeries.id!!, currentUser.id!!, 
            UserRole.ADMIN, UserRole.MEETING_LEADER, UserRole.MEMBER)) {
            throw ForbiddenException("Member access required")
        }
    }

    private fun checkAdminOrLeader(meetingSeries: MeetingSeries) {
        val currentUser = authService.getCurrentUser()
            ?: throw ForbiddenException("Not authenticated")

        if (!MeetingSeriesMember.hasRole(meetingSeries.id!!, currentUser.id!!, UserRole.ADMIN, UserRole.MEETING_LEADER)) {
            throw ForbiddenException("Admin or Meeting Leader access required")
        }
    }
}

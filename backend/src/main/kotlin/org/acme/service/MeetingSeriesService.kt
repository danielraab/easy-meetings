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
import java.time.LocalDateTime

@ApplicationScoped
class MeetingSeriesService {

    @Inject
    lateinit var authService: AuthService

    @Inject
    lateinit var emailService: EmailService

    @Transactional
    fun createMeetingSeries(request: CreateMeetingSeriesRequest): MeetingSeriesDto {
        val currentUser = authService.getCurrentUser()
            ?: throw ForbiddenException("Not authenticated")

        val meetingSeries = MeetingSeries().apply {
            name = request.name
            description = request.description
            createdBy = currentUser
            isActive = true
        }
        meetingSeries.persist()

        // Add creator as admin
        val member = MeetingSeriesMember().apply {
            this.meetingSeries = meetingSeries
            user = currentUser
            role = UserRole.ADMIN
            joinedAt = LocalDateTime.now()
        }
        member.persist()

        return EntityMapper.toDto(meetingSeries)
    }

    fun getMeetingSeries(id: UUID): MeetingSeriesDto {
        val meetingSeries = MeetingSeries.findById(id)
            ?: throw NotFoundException("Meeting series not found")
        
        checkAccess(meetingSeries)
        return EntityMapper.toDto(meetingSeries)
    }

    fun listMeetingSeries(): List<MeetingSeriesDto> {
        val currentUser = authService.getCurrentUser()
            ?: throw ForbiddenException("Not authenticated")

        return MeetingSeries.findActiveByUser(currentUser.id!!)
            .map { EntityMapper.toDto(it) }
    }

    @Transactional
    fun updateMeetingSeries(id: UUID, request: UpdateMeetingSeriesRequest): MeetingSeriesDto {
        val meetingSeries = MeetingSeries.findById(id)
            ?: throw NotFoundException("Meeting series not found")

        checkAdminOrLeader(meetingSeries)

        request.name?.let { meetingSeries.name = it }
        request.description?.let { meetingSeries.description = it }
        request.isActive?.let { meetingSeries.isActive = it }

        return EntityMapper.toDto(meetingSeries)
    }

    @Transactional
    fun deleteMeetingSeries(id: UUID) {
        val meetingSeries = MeetingSeries.findById(id)
            ?: throw NotFoundException("Meeting series not found")

        checkAdmin(meetingSeries)
        meetingSeries.delete()
    }

    @Transactional
    fun inviteMember(seriesId: UUID, request: InviteMemberRequest): MeetingSeriesMemberDto {
        val meetingSeries = MeetingSeries.findById(seriesId)
            ?: throw NotFoundException("Meeting series not found")

        checkAdminOrLeader(meetingSeries)

        val existingUser = User.findByEmail(request.email)
        val currentUser = authService.getCurrentUser()!!

        val member = MeetingSeriesMember().apply {
            this.meetingSeries = meetingSeries
            user = existingUser
            email = if (existingUser == null) request.email else null
            role = request.role
            invitationSentAt = LocalDateTime.now()
            joinedAt = if (existingUser != null) LocalDateTime.now() else null
        }
        member.persist()

        // Send invitation email
        emailService.sendInvitation(
            request.email,
            meetingSeries.name,
            currentUser.name,
            request.role.name
        )

        return EntityMapper.toDto(member)
    }

    fun listMembers(seriesId: UUID): List<MeetingSeriesMemberDto> {
        val meetingSeries = MeetingSeries.findById(seriesId)
            ?: throw NotFoundException("Meeting series not found")

        checkAccess(meetingSeries)

        return MeetingSeriesMember.findByMeetingSeries(seriesId)
            .map { EntityMapper.toDto(it) }
    }

    @Transactional
    fun updateMemberRole(seriesId: UUID, memberId: UUID, request: UpdateMemberRoleRequest): MeetingSeriesMemberDto {
        val meetingSeries = MeetingSeries.findById(seriesId)
            ?: throw NotFoundException("Meeting series not found")

        checkAdmin(meetingSeries)

        val member = MeetingSeriesMember.findById(memberId)
            ?: throw NotFoundException("Member not found")

        member.role = request.role
        return EntityMapper.toDto(member)
    }

    @Transactional
    fun removeMember(seriesId: UUID, memberId: UUID) {
        val meetingSeries = MeetingSeries.findById(seriesId)
            ?: throw NotFoundException("Meeting series not found")

        checkAdmin(meetingSeries)

        val member = MeetingSeriesMember.findById(memberId)
            ?: throw NotFoundException("Member not found")

        member.delete()
    }

    private fun checkAccess(meetingSeries: MeetingSeries) {
        val currentUser = authService.getCurrentUser()
            ?: throw ForbiddenException("Not authenticated")

        val member = MeetingSeriesMember.findByMeetingSeriesAndUser(meetingSeries.id!!, currentUser.id!!)
            ?: throw ForbiddenException("Access denied")
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

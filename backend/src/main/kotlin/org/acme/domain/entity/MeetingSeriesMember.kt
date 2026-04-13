package org.acme.domain.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "meeting_series_members")
class MeetingSeriesMember : PanacheEntityBase {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    var id: UUID? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_series_id", nullable = false)
    var meetingSeries: MeetingSeries? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User? = null

    @Column
    var email: String? = null

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "user_role")
    var role: UserRole = UserRole.MEMBER

    @Column(name = "invitation_sent_at")
    var invitationSentAt: LocalDateTime? = null

    @Column(name = "joined_at")
    var joinedAt: LocalDateTime? = null

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()

    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }

    companion object : PanacheCompanion<MeetingSeriesMember> {
        fun findById(id: UUID): MeetingSeriesMember? = find("id", id).firstResult()
        
        fun findByMeetingSeriesAndUser(seriesId: UUID, userId: UUID): MeetingSeriesMember? = 
            find("meetingSeries.id = ?1 and user.id = ?2", seriesId, userId).firstResult()
        
        fun findByMeetingSeries(seriesId: UUID): List<MeetingSeriesMember> = 
            list("meetingSeries.id", seriesId)
        
        fun hasRole(seriesId: UUID, userId: UUID, vararg roles: UserRole): Boolean = 
            count("meetingSeries.id = ?1 and user.id = ?2 and role in ?3", seriesId, userId, roles.toList()) > 0
    }
}

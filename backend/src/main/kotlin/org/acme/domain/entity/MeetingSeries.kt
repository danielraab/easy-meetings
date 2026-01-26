package org.acme.domain.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "meeting_series")
class MeetingSeries : PanacheEntityBase {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    var id: UUID? = null

    @Column(nullable = false)
    var name: String = ""

    @Column(columnDefinition = "TEXT")
    var description: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    var createdBy: User? = null

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()

    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }

    companion object : PanacheCompanion<MeetingSeries> {
        fun findById(id: UUID): MeetingSeries? = find("id", id).firstResult()
        
        fun findByCreator(userId: UUID): List<MeetingSeries> = 
            list("createdBy.id = ?1 and isActive = true", userId)
        
        fun findActiveByUser(userId: UUID): List<MeetingSeries> = 
            list("from MeetingSeries ms where ms.isActive = true and exists (select 1 from MeetingSeriesMember msm where msm.meetingSeries = ms and msm.user.id = ?1)", userId)
    }
}

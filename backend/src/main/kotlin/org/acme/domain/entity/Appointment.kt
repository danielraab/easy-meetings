package org.acme.domain.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "appointments")
class Appointment : PanacheEntityBase {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    var id: UUID? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_series_id", nullable = false)
    var meetingSeries: MeetingSeries? = null

    @Column(name = "scheduled_time", nullable = false)
    var scheduledTime: LocalDateTime = LocalDateTime.now()

    @Column(name = "actual_time")
    var actualTime: LocalDateTime? = null

    @Column(name = "duration_minutes")
    var durationMinutes: Int? = null

    @Column
    var location: String? = null

    @Column(columnDefinition = "TEXT")
    var notes: String? = null

    @Column(name = "is_cancelled", nullable = false)
    var isCancelled: Boolean = false

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()

    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }

    companion object : PanacheCompanion<Appointment> {
        fun findById(id: UUID): Appointment? = find("id", id).firstResult()
        
        fun findByMeetingSeries(seriesId: UUID): List<Appointment> = 
            list("meetingSeries.id = ?1 and isCancelled = false order by scheduledTime", seriesId)
        
        fun findUpcoming(seriesId: UUID): List<Appointment> = 
            list("meetingSeries.id = ?1 and scheduledTime > ?2 and isCancelled = false order by scheduledTime", 
                seriesId, LocalDateTime.now())
    }
}

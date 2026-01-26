package org.acme.domain.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "areas")
class Area : PanacheEntityBase {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    var id: UUID? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_series_id", nullable = false)
    var meetingSeries: MeetingSeries? = null

    @Column(nullable = false)
    var name: String = ""

    @Column(columnDefinition = "TEXT")
    var description: String? = null

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()

    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }

    companion object : PanacheCompanion<Area> {
        fun findById(id: UUID): Area? = find("id", id).firstResult()
        
        fun findByMeetingSeries(seriesId: UUID): List<Area> = 
            list("meetingSeries.id = ?1 order by sortOrder", seriesId)
    }
}

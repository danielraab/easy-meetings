package org.acme.domain.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "topics")
class Topic : PanacheEntityBase {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    var id: UUID? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    var area: Area? = null

    @Column(nullable = false, length = 500)
    var title: String = ""

    @Column(columnDefinition = "TEXT")
    var description: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    var createdBy: User? = null

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0

    @Column(name = "is_resolved", nullable = false)
    var isResolved: Boolean = false

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()

    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }

    companion object : PanacheCompanion<Topic> {
        fun findById(id: UUID): Topic? = find("id", id).firstResult()
        
        fun findByArea(areaId: UUID): List<Topic> = 
            list("area.id = ?1 order by sortOrder", areaId)
    }
}

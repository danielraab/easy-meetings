package org.acme.domain.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "entries")
class Entry : PanacheEntityBase {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    var id: UUID? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    var topic: Topic? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    var appointment: Appointment? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false, columnDefinition = "entry_type")
    var entryType: EntryType = EntryType.COMMENT

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String = ""

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    var createdBy: User? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    var assignedTo: User? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "task_status", columnDefinition = "task_status")
    var taskStatus: TaskStatus? = null

    @Column(name = "task_due_date")
    var taskDueDate: LocalDateTime? = null

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()

    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }

    companion object {
        fun findByTopic(topicId: UUID): List<Entry> = 
            list("topic.id = ?1 order by createdAt", topicId)
        
        fun findByAppointment(appointmentId: UUID): List<Entry> = 
            list("appointment.id = ?1 order by createdAt", appointmentId)
        
        fun findTasksByAssignee(userId: UUID): List<Entry> = 
            list("entryType = ?1 and assignedTo.id = ?2 and taskStatus != ?3 order by taskDueDate", 
                EntryType.TASK, userId, TaskStatus.COMPLETED)
    }
}

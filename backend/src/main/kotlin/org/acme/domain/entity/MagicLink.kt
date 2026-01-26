package org.acme.domain.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "magic_links")
class MagicLink : PanacheEntityBase {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    var id: UUID? = null

    @Column(nullable = false)
    var email: String = ""

    @Column(nullable = false, unique = true)
    var token: String = ""

    @Column(name = "expires_at", nullable = false)
    var expiresAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "used_at")
    var usedAt: LocalDateTime? = null

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    fun isValid(): Boolean = usedAt == null && expiresAt.isAfter(LocalDateTime.now())

    companion object : PanacheCompanion<MagicLink> {
        fun findById(id: UUID): MagicLink? = find("id", id).firstResult()
        
        fun findByToken(token: String): MagicLink? = find("token", token).firstResult()
    }
}

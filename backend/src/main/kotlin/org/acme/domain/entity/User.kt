package org.acme.domain.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "users")
class User : PanacheEntityBase {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    var id: UUID? = null

    @Column(nullable = false, unique = true)
    var email: String = ""

    @Column(nullable = false)
    var name: String = ""

    @Column(name = "avatar_url")
    var avatarUrl: String? = null

    @Column(name = "oauth_provider", length = 50)
    var oauthProvider: String? = null

    @Column(name = "oauth_subject")
    var oauthSubject: String? = null

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

    companion object : PanacheCompanion<User> {
        fun findById(id: UUID): User? = find("id", id).firstResult()
        
        fun findByEmail(email: String): User? = find("email", email).firstResult()
        
        fun findByOAuth(provider: String, subject: String): User? = 
            find("oauthProvider = ?1 and oauthSubject = ?2", provider, subject).firstResult()
    }
}

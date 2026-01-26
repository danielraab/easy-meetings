package org.acme.service
import java.util.UUID

import io.quarkus.security.identity.SecurityIdentity
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import org.acme.domain.entity.MagicLink
import org.acme.domain.entity.User
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.security.SecureRandom
import java.time.LocalDateTime
import java.util.*

@ApplicationScoped
class AuthService {

    @Inject
    lateinit var emailService: EmailService

    @Inject
    lateinit var securityIdentity: SecurityIdentity

    @ConfigProperty(name = "app.magic-link.expiration-minutes")
    var expirationMinutes: Int = 15

    private val secureRandom = SecureRandom()

    @Transactional
    fun createMagicLink(email: String): String {
        // Generate a secure random token
        val token = generateSecureToken()
        
        val magicLink = MagicLink().apply {
            this.email = email.lowercase()
            this.token = token
            this.expiresAt = LocalDateTime.now().plusMinutes(expirationMinutes.toLong())
        }
        
        magicLink.persist()
        
        // Send email with magic link
        emailService.sendMagicLink(email, token)
        
        return "Magic link sent to $email"
    }

    @Transactional
    fun verifyMagicLink(token: String): User? {
        val magicLink = MagicLink.findByToken(token) ?: return null
        
        if (!magicLink.isValid()) {
            return null
        }
        
        // Mark magic link as used
        magicLink.usedAt = LocalDateTime.now()
        
        // Find or create user
        var user = User.findByEmail(magicLink.email)
        if (user == null) {
            user = User().apply {
                this.email = magicLink.email
                this.name = magicLink.email.substringBefore('@')
                this.isActive = true
            }
            user.persist()
        }
        
        return user
    }

    @Transactional
    fun handleOAuthLogin(email: String, name: String, provider: String, subject: String, avatarUrl: String?): User {
        var user = User.findByOAuth(provider, subject)
        
        if (user == null) {
            user = User.findByEmail(email)
        }
        
        if (user == null) {
            user = User().apply {
                this.email = email
                this.name = name
                this.oauthProvider = provider
                this.oauthSubject = subject
                this.avatarUrl = avatarUrl
                this.isActive = true
            }
        } else {
            user.oauthProvider = provider
            user.oauthSubject = subject
            user.name = name
            user.avatarUrl = avatarUrl
            user.updatedAt = LocalDateTime.now()
        }
        
        user.persist()
        return user
    }

    fun getCurrentUser(): User? {
        if (securityIdentity.isAnonymous) {
            return null
        }
        
        val email = securityIdentity.principal?.name ?: return null
        return User.findByEmail(email)
    }

    private fun generateSecureToken(): String {
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}

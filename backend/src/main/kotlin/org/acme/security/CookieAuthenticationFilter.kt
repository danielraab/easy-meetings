package org.acme.security

import jakarta.annotation.Priority
import jakarta.inject.Inject
import jakarta.ws.rs.Priorities
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.core.Cookie
import jakarta.ws.rs.ext.Provider
import java.util.UUID
import org.acme.domain.entity.User

@Provider
@Priority(Priorities.AUTHENTICATION)
class CookieAuthenticationFilter : ContainerRequestFilter {

    @Inject
    lateinit var cookieIdentityProvider: CookieIdentityProvider

    override fun filter(requestContext: ContainerRequestContext) {
        val sessionCookie: Cookie? = requestContext.cookies["user_session"]
        
        if (sessionCookie != null) {
            try {
                val userId = UUID.fromString(sessionCookie.value)
                val user = User.findById(userId)
                
                if (user != null && user.isActive) {
                    // Store user in request context for later retrieval
                    requestContext.setProperty("authenticated_user", user)
                }
            } catch (e: Exception) {
                // Invalid cookie format, ignore
            }
        }
    }
}

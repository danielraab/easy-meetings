package org.acme.resource
import java.util.UUID

import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.NewCookie
import jakarta.ws.rs.core.Response
import org.acme.domain.dto.*
import org.acme.mapper.EntityMapper
import org.acme.service.AuthService
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
class AuthResource {

    @Inject
    lateinit var authService: AuthService

    @POST
    @Path("/magic-link")
    @Operation(summary = "Request a magic link", description = "Sends a magic link to the provided email address")
    fun requestMagicLink(request: LoginRequest): Response {
        return try {
            val message = authService.createMagicLink(request.email)
            Response.ok(mapOf("message" to message)).build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Failed to send magic link"))
                .build()
        }
    }

    @POST
    @Path("/magic-link/verify")
    @Transactional
    @Operation(summary = "Verify magic link", description = "Verifies a magic link token and authenticates the user")
    fun verifyMagicLink(request: MagicLinkVerifyRequest): Response {
        val user = authService.verifyMagicLink(request.token)
            ?: return Response.status(Response.Status.UNAUTHORIZED)
                .entity(mapOf("error" to "Invalid or expired token"))
                .build()

        val userDto = EntityMapper.toDto(user)
        
        // Create a session cookie (in production, use proper session management)
        val cookie = NewCookie.Builder("user_session")
            .value(user.id.toString())
            .path("/")
            .maxAge(86400) // 24 hours
            .secure(false) // Set to true in production with HTTPS
            .httpOnly(true)
            .sameSite(NewCookie.SameSite.LAX)
            .build()

        return Response.ok(AuthResponse(userDto))
            .cookie(cookie)
            .build()
    }

    @GET
    @Path("/me")
    @Operation(summary = "Get current user", description = "Returns the currently authenticated user")
    fun getCurrentUser(): Response {
        val user = authService.getCurrentUser()
            ?: return Response.status(Response.Status.UNAUTHORIZED)
                .entity(mapOf("error" to "Not authenticated"))
                .build()

        val userDto = EntityMapper.toDto(user)
        return Response.ok(userDto).build()
    }

    @POST
    @Path("/logout")
    @Operation(summary = "Logout", description = "Logs out the current user")
    fun logout(): Response {
        val cookie = NewCookie.Builder("user_session")
            .value("")
            .path("/")
            .maxAge(0)
            .build()

        return Response.ok(mapOf("message" to "Logged out successfully"))
            .cookie(cookie)
            .build()
    }

    @GET
    @Path("/callback")
    @Operation(summary = "OAuth callback", description = "Handles OAuth provider callback")
    fun oauthCallback(): Response {
        // This endpoint is handled by Quarkus OIDC
        // It's here for OpenAPI documentation
        return Response.seeOther(java.net.URI.create("/")).build()
    }
}

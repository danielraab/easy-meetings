package org.acme.service

import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.InjectMock
import jakarta.inject.Inject
import org.acme.domain.entity.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

@QuarkusTest
class AuthServiceTest {

    @Inject
    lateinit var authService: AuthService

    @InjectMock
    lateinit var emailService: EmailService

    @Test
    fun testCreateMagicLinkSendsEmail() {
        val email = "test@example.com"
        
        val message = authService.createMagicLink(email)
        
        assertTrue(message.contains(email))
        verify(emailService).sendMagicLink("mail", "token")
    }
}

package org.acme.resource

import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.security.TestSecurity
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Test
import jakarta.ws.rs.core.MediaType

@QuarkusTest
class AuthResourceTest {

    @Test
    fun testMagicLinkRequest() {
        Given {
            contentType(MediaType.APPLICATION_JSON)
            body("""{"email": "test@example.com"}""")
        } When {
            post("/api/auth/magic-link")
        } Then {
            statusCode(200)
            body("message", notNullValue())
        }
    }

    @Test
    @TestSecurity(user = "test@example.com", roles = ["USER"])
    fun testGetCurrentUserAuthenticated() {
        When {
            get("/api/auth/me")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun testGetCurrentUserUnauthorized() {
        When {
            get("/api/auth/me")
        } Then {
            statusCode(401)
        }
    }
}

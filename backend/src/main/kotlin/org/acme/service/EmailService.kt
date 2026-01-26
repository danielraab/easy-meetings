package org.acme.service
import java.util.UUID

import io.quarkus.mailer.Mail
import io.quarkus.mailer.Mailer
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.eclipse.microprofile.config.inject.ConfigProperty

@ApplicationScoped
class EmailService {

    @Inject
    lateinit var mailer: Mailer

    @ConfigProperty(name = "app.magic-link.base-url")
    lateinit var baseUrl: String

    fun sendMagicLink(email: String, token: String) {
        val magicLink = "$baseUrl/auth/verify?token=$token"
        
        val html = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .button { 
                        display: inline-block; 
                        padding: 12px 24px; 
                        background-color: #4F46E5; 
                        color: white; 
                        text-decoration: none; 
                        border-radius: 6px; 
                        margin: 20px 0;
                    }
                    .footer { margin-top: 30px; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h2>Sign in to Easy Meetings</h2>
                    <p>Hello,</p>
                    <p>Click the button below to sign in to your Easy Meetings account. This link will expire in 15 minutes.</p>
                    <a href="$magicLink" class="button">Sign In</a>
                    <p>Or copy and paste this link into your browser:</p>
                    <p style="word-break: break-all; color: #4F46E5;">$magicLink</p>
                    <div class="footer">
                        <p>If you didn't request this email, you can safely ignore it.</p>
                        <p>© ${java.time.Year.now().value} Easy Meetings. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()

        mailer.send(
            Mail.withHtml(email, "Sign in to Easy Meetings", html)
        )
    }

    fun sendInvitation(email: String, meetingSeriesName: String, inviterName: String, role: String) {
        val signUpLink = "$baseUrl/auth/login?email=${java.net.URLEncoder.encode(email, "UTF-8")}"
        
        val html = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .button { 
                        display: inline-block; 
                        padding: 12px 24px; 
                        background-color: #4F46E5; 
                        color: white; 
                        text-decoration: none; 
                        border-radius: 6px; 
                        margin: 20px 0;
                    }
                    .info-box {
                        background-color: #F3F4F6;
                        padding: 15px;
                        border-radius: 6px;
                        margin: 20px 0;
                    }
                    .footer { margin-top: 30px; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h2>You've been invited to Easy Meetings</h2>
                    <p>Hello,</p>
                    <p>$inviterName has invited you to join the meeting series "<strong>$meetingSeriesName</strong>" as a <strong>$role</strong>.</p>
                    <div class="info-box">
                        <strong>Meeting Series:</strong> $meetingSeriesName<br>
                        <strong>Your Role:</strong> $role<br>
                        <strong>Invited by:</strong> $inviterName
                    </div>
                    <p>Click the button below to get started:</p>
                    <a href="$signUpLink" class="button">Accept Invitation</a>
                    <div class="footer">
                        <p>If you don't want to accept this invitation, you can safely ignore this email.</p>
                        <p>© ${java.time.Year.now().value} Easy Meetings. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()

        mailer.send(
            Mail.withHtml(email, "Invitation to $meetingSeriesName - Easy Meetings", html)
        )
    }
}

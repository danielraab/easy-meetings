package org.acme.resource

import jakarta.inject.Inject
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.acme.domain.dto.*
import org.acme.service.MeetingSeriesService
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import java.util.*

@Path("/api/meeting-series")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Meeting Series", description = "Meeting series management endpoints")
class MeetingSeriesResource {

    @Inject
    lateinit var meetingSeriesService: MeetingSeriesService

    @POST
    @Operation(summary = "Create meeting series", description = "Creates a new meeting series")
    fun create(@Valid request: CreateMeetingSeriesRequest): Response {
        val result = meetingSeriesService.createMeetingSeries(request)
        return Response.status(Response.Status.CREATED).entity(result).build()
    }

    @GET
    @Operation(summary = "List meeting series", description = "Lists all meeting series accessible to the current user")
    fun list(): Response {
        val result = meetingSeriesService.listMeetingSeries()
        return Response.ok(result).build()
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get meeting series", description = "Gets a specific meeting series by ID")
    fun get(@PathParam("id") id: UUID): Response {
        val result = meetingSeriesService.getMeetingSeries(id)
        return Response.ok(result).build()
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update meeting series", description = "Updates an existing meeting series")
    fun update(@PathParam("id") id: UUID, @Valid request: UpdateMeetingSeriesRequest): Response {
        val result = meetingSeriesService.updateMeetingSeries(id, request)
        return Response.ok(result).build()
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete meeting series", description = "Deletes a meeting series")
    fun delete(@PathParam("id") id: UUID): Response {
        meetingSeriesService.deleteMeetingSeries(id)
        return Response.noContent().build()
    }

    @POST
    @Path("/{id}/members")
    @Operation(summary = "Invite member", description = "Invites a new member to the meeting series")
    fun inviteMember(@PathParam("id") id: UUID, @Valid request: InviteMemberRequest): Response {
        val result = meetingSeriesService.inviteMember(id, request)
        return Response.status(Response.Status.CREATED).entity(result).build()
    }

    @GET
    @Path("/{id}/members")
    @Operation(summary = "List members", description = "Lists all members of a meeting series")
    fun listMembers(@PathParam("id") id: UUID): Response {
        val result = meetingSeriesService.listMembers(id)
        return Response.ok(result).build()
    }

    @PUT
    @Path("/{id}/members/{memberId}")
    @Operation(summary = "Update member role", description = "Updates a member's role in the meeting series")
    fun updateMemberRole(
        @PathParam("id") id: UUID,
        @PathParam("memberId") memberId: UUID,
        @Valid request: UpdateMemberRoleRequest
    ): Response {
        val result = meetingSeriesService.updateMemberRole(id, memberId, request)
        return Response.ok(result).build()
    }

    @DELETE
    @Path("/{id}/members/{memberId}")
    @Operation(summary = "Remove member", description = "Removes a member from the meeting series")
    fun removeMember(@PathParam("id") id: UUID, @PathParam("memberId") memberId: UUID): Response {
        meetingSeriesService.removeMember(id, memberId)
        return Response.noContent().build()
    }
}

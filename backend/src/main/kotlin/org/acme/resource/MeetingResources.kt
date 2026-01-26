package org.acme.resource

import jakarta.inject.Inject
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.acme.domain.dto.*
import org.acme.service.*
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import java.util.*

@Path("/api/meeting-series/{seriesId}/appointments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Appointments", description = "Appointment management endpoints")
class AppointmentResource {

    @Inject
    lateinit var appointmentService: AppointmentService

    @POST
    @Operation(summary = "Create appointment")
    fun create(@PathParam("seriesId") seriesId: UUID, @Valid request: CreateAppointmentRequest): Response {
        val result = appointmentService.createAppointment(seriesId, request)
        return Response.status(Response.Status.CREATED).entity(result).build()
    }

    @GET
    @Operation(summary = "List appointments")
    fun list(@PathParam("seriesId") seriesId: UUID): Response {
        val result = appointmentService.listAppointments(seriesId)
        return Response.ok(result).build()
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get appointment")
    fun get(@PathParam("id") id: UUID): Response {
        val result = appointmentService.getAppointment(id)
        return Response.ok(result).build()
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update appointment")
    fun update(@PathParam("id") id: UUID, @Valid request: UpdateAppointmentRequest): Response {
        val result = appointmentService.updateAppointment(id, request)
        return Response.ok(result).build()
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete appointment")
    fun delete(@PathParam("id") id: UUID): Response {
        appointmentService.deleteAppointment(id)
        return Response.noContent().build()
    }
}

@Path("/api/meeting-series/{seriesId}/areas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Areas", description = "Area management endpoints")
class AreaResource {

    @Inject
    lateinit var areaService: AreaService

    @POST
    @Operation(summary = "Create area")
    fun create(@PathParam("seriesId") seriesId: UUID, @Valid request: CreateAreaRequest): Response {
        val result = areaService.createArea(seriesId, request)
        return Response.status(Response.Status.CREATED).entity(result).build()
    }

    @GET
    @Operation(summary = "List areas")
    fun list(@PathParam("seriesId") seriesId: UUID): Response {
        val result = areaService.listAreas(seriesId)
        return Response.ok(result).build()
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update area")
    fun update(@PathParam("id") id: UUID, @Valid request: UpdateAreaRequest): Response {
        val result = areaService.updateArea(id, request)
        return Response.ok(result).build()
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete area")
    fun delete(@PathParam("id") id: UUID): Response {
        areaService.deleteArea(id)
        return Response.noContent().build()
    }
}

@Path("/api/areas/{areaId}/topics")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Topics", description = "Topic management endpoints")
class TopicResource {

    @Inject
    lateinit var topicService: TopicService

    @POST
    @Operation(summary = "Create topic")
    fun create(@PathParam("areaId") areaId: UUID, @Valid request: CreateTopicRequest): Response {
        val result = topicService.createTopic(areaId, request)
        return Response.status(Response.Status.CREATED).entity(result).build()
    }

    @GET
    @Operation(summary = "List topics")
    fun list(@PathParam("areaId") areaId: UUID): Response {
        val result = topicService.listTopics(areaId)
        return Response.ok(result).build()
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update topic")
    fun update(@PathParam("id") id: UUID, @Valid request: UpdateTopicRequest): Response {
        val result = topicService.updateTopic(id, request)
        return Response.ok(result).build()
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete topic")
    fun delete(@PathParam("id") id: UUID): Response {
        topicService.deleteTopic(id)
        return Response.noContent().build()
    }
}

@Path("/api/entries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Entries", description = "Entry (comments and tasks) management endpoints")
class EntryResource {

    @Inject
    lateinit var entryService: EntryService

    @POST
    @Operation(summary = "Create entry")
    fun create(@Valid request: CreateEntryRequest): Response {
        val result = entryService.createEntry(request)
        return Response.status(Response.Status.CREATED).entity(result).build()
    }

    @GET
    @Path("/topic/{topicId}")
    @Operation(summary = "List entries by topic")
    fun listByTopic(@PathParam("topicId") topicId: UUID): Response {
        val result = entryService.listEntriesByTopic(topicId)
        return Response.ok(result).build()
    }

    @GET
    @Path("/appointment/{appointmentId}")
    @Operation(summary = "List entries by appointment")
    fun listByAppointment(@PathParam("appointmentId") appointmentId: UUID): Response {
        val result = entryService.listEntriesByAppointment(appointmentId)
        return Response.ok(result).build()
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update entry")
    fun update(@PathParam("id") id: UUID, @Valid request: UpdateEntryRequest): Response {
        val result = entryService.updateEntry(id, request)
        return Response.ok(result).build()
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete entry")
    fun delete(@PathParam("id") id: UUID): Response {
        entryService.deleteEntry(id)
        return Response.noContent().build()
    }
}

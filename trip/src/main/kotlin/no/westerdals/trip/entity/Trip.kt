package no.westerdals.trip.entity

import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
class Trip(

    @get:Id
    var id: Long?,

    @get:NotBlank
    @get:Size(max = 32)
    var name : String?,

    @get:NotBlank
    var location: String?,

    @get:NotNull
    var price: Int?,

    @get:NotNull
    var soldOut: Boolean?
)
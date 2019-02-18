package no.westerdals.trip.DTO

import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class TripDTO (

    @ApiModelProperty("name of the company")
    @get:NotBlank
    var name: String? = null,

    @ApiModelProperty("location of the trip, i.e Rome, Italy.")
    @get:NotBlank
    var location: String? = null,

    @ApiModelProperty("price of the trip, in dollars")
    @get:NotNull
    var price: Int? = null,

    @ApiModelProperty("if it is sold out or not")
    @get:NotBlank
    var soldOut: Boolean? = null,

    @ApiModelProperty("id of the transaction")
    @get:NotBlank
    var id: String? = null
)
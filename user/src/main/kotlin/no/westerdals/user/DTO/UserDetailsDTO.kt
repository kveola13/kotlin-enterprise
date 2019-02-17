package no.westerdals.user.DTO

import io.swagger.annotations.ApiModelProperty
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class UserDetailsDTO(

    @ApiModelProperty("Username of user")
    @get:NotBlank
    var username: String? = null,

    @ApiModelProperty("Name of user")
    @get:NotBlank
    var name: String? = null,

    @ApiModelProperty("Last name of user")
    @get:NotBlank
    var surname: String? = null,

    @ApiModelProperty("Email of user")
    @get:NotBlank
    var email: String? = null,

    @ApiModelProperty("Age of user")
    @get:NotNull
    var age: Int? = null,

    @ApiModelProperty("Age of user")
    @get:NotNull
    var purchasedTickets: MutableList<Long>? = null
)
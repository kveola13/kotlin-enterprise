package no.westerdals.auth.dto


import io.swagger.annotations.ApiModelProperty
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class UserDTO(

        @ApiModelProperty("Username of user")
        @get:NotBlank
        var username: String? = null,

        @ApiModelProperty("Password of user")
        @get:NotBlank
        var password: String? = null,

        @ApiModelProperty("Role of user")
        @get:NotNull
        var roles: MutableSet<String>? = null
)
package no.westerdals.auth.db

import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name="USERS")
class UserEntity(

        @get:Id
        @get:NotBlank
        @get:Size(max = 32)
        var username: String?,

        @get:NotBlank
        @get:Size(max = 32)
        var password: String?,

        @get:ElementCollection(fetch = FetchType.EAGER)
        @get:NotNull
        var roles: MutableSet<String>? = mutableSetOf(),

        @get:NotNull
        var enabled: Boolean? = true
)
package no.westerdals.user.DTO

import no.westerdals.user.entity.User

class UserConverter{

    companion object {

        fun transform(userDetailsEntity: User): UserDTO {
            return UserDTO(

                username = userDetailsEntity.username,
                name = userDetailsEntity.name,
                surname = userDetailsEntity.surname,
                email = userDetailsEntity.email,
                age = userDetailsEntity.age,
                purchasedTrips = userDetailsEntity.purchasedTrips

            )
        }
        fun transform(users: Iterable<User>): List<UserDTO> {
            return users.map { transform(it) }
        }
    }
}
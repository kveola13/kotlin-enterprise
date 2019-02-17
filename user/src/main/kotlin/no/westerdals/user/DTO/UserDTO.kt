package no.westerdals.user.DTO

import no.westerdals.user.entity.User

class UserDetailsConverter{

    companion object {

        fun transform(userDetailsEntity: User): UserDetailsDTO {
            return UserDetailsDTO(

                username = userDetailsEntity.username,
                name = userDetailsEntity.name,
                surname = userDetailsEntity.surname,
                email = userDetailsEntity.email,
                age = userDetailsEntity.age,
                purchasedTickets = userDetailsEntity.purchasedTickets

            )
        }
        fun transform(users: Iterable<User>): List<UserDetailsDTO> {
            return users.map { transform(it) }
        }
    }
}
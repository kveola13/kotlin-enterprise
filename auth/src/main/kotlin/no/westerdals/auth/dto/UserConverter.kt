package no.westerdals.auth.dto

import no.westerdals.auth.db.UserEntity


class UserConverter{

    companion object {

        fun transform(userEntity: UserEntity): UserDTO {
            return UserDTO(
                    username = userEntity.username,
                    password = userEntity.password,
                    roles = userEntity.roles
            )
        }
        fun transform(users: Iterable<UserEntity>): List<UserDTO> {
            return users.map { transform(it) }
        }
    }
}
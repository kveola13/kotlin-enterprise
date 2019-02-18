package no.westerdals.auth.db

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.EntityManager

@Repository
interface UserRepository : CrudRepository<UserEntity, String>, UserRepositoryCustom {
}

@Transactional
interface UserRepositoryCustom {

    fun createUser(username: String, password: String): Boolean
    fun createAdmin(username: String, password: String): Boolean
}

@Repository
@Transactional
class UserRepositoryImpl : UserRepositoryCustom {

    @Autowired
    private lateinit var em: EntityManager
/*
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder
*/
    override fun createUser(username: String, password: String)
            : Boolean {

        //val hash = passwordEncoder.encode(password)

        if (em.find(UserEntity::class.java, username) != null) {
            //if we find a user with the existing username already, return false
            return false
        }
        //need to use hashed password instead of password when we add postgres
        val user = UserEntity(username, password, Collections.singleton("USER"), true)
        em.persist(user)
        return true
    }

    override fun createAdmin(username: String, password: String)
            : Boolean {

        //val hash = passwordEncoder.encode(password)

        if (em.find(UserEntity::class.java, username) != null) {
            //if we find a user with the existing username already, return false
            return false
        }
        //need to use hashed password instead of password when we add postgres
        val user = UserEntity(username, password, Collections.singleton("ADMIN"), true)
        em.persist(user)
        return true
    }
}
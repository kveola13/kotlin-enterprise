package no.westerdals.user.repo

import no.westerdals.user.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Repository
interface UserRepository : CrudRepository<User, String>, UserRepositoryCustom {
}

@Transactional
interface UserRepositoryCustom {

    fun createUserDetails(
        username: String,
        name: String,
        surname: String,
        email: String,
        age: Int,
        purchasedTickets: MutableList<Long>): Boolean

    fun updateEmail(username: String, email: String): Boolean
}

@Repository
@Transactional
class UserRepositoryImpl : UserRepositoryCustom {

    @Autowired
    private lateinit var em: EntityManager

    override fun createUserDetails(
        username: String,
        name: String,
        surname: String,
        email: String,
        age: Int,
        purchasedTickets: MutableList<Long>): Boolean {

        val userDetails = User(username, name, surname, email, age, purchasedTickets)
        em.persist(userDetails)
        return true
    }

    override fun updateEmail(username: String, email: String): Boolean{
        val userDetails = em.find(User::class.java, username) ?: return false
        userDetails.email = email
        return true
    }
}
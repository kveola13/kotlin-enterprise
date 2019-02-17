package no.westerdals.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Repository
interface UserDetailsRepository : CrudRepository<UserDetailsEntity, String>, UserDetailsRepositoryCustom {
}

@Transactional
interface UserDetailsRepositoryCustom {

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
class UserDetailsRepositoryImpl : UserDetailsRepositoryCustom {

    @Autowired
    private lateinit var em: EntityManager

    override fun createUserDetails(
            username: String,
            name: String,
            surname: String,
            email: String,
            age: Int,
            purchasedTickets: MutableList<Long>): Boolean {

        val userDetails = UserDetailsEntity(username, name, surname, email, age, purchasedTickets)
        em.persist(userDetails)
        return true
    }

    override fun updateEmail(username: String, email: String): Boolean{
        val userDetails = em.find(UserDetailsEntity::class.java, username) ?: return false
        userDetails.email = email
        return true
    }
}
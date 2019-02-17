package no.westerdals.trip.repo

import no.westerdals.trip.entity.Trip
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import javax.persistence.EntityManager

@Repository
interface TripRepo : CrudRepository<Trip, Long>, TripRepoCustom {
}

@Transactional
interface TripRepoCustom {

    fun createTrip(tripId: Long, name: String, location: String, price: Int, soldOut: Boolean): Long

    fun update(tripId: Long, name: String, location: String, price: Int, soldOut: Boolean): Boolean

    fun updatePrice(tripId: Long, price: Int) : Boolean
}

@Repository
@Transactional
class TicketRepositoryImpl : TripRepoCustom {

    @Autowired
    private lateinit var em: EntityManager

    override fun createTrip(tripId: Long, name: String, location: String, price: Int, soldOut: Boolean)
            : Long {

        val entity = Trip(tripId, name, location, price, soldOut)
        em.persist(entity)
        return entity.id!!
    }

    override fun update(tripId: Long, name: String, location: String, price: Int, soldOut: Boolean)
            : Boolean {

        val trip = em.find(Trip::class.java, tripId) ?: return false

        trip.id = tripId
        trip.name = name
        trip.location = location
        trip.price = price
        trip.soldOut = soldOut

        return true
    }

    override fun updatePrice(tripId: Long, price: Int)
            : Boolean {

        val trip = em.find(Trip::class.java, tripId) ?: return false

        trip.price = price

        return true
    }
}
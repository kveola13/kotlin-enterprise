package no.westerdals.trip.DTO

import no.westerdals.trip.entity.Trip

class TripConverter{

    companion object {

        fun transform(tripEntity: Trip): TripDTO {
            return TripDTO(

                id = tripEntity.id.toString(),
                name = tripEntity.name,
                location = tripEntity.location,
                price = tripEntity.price,
                soldOut = tripEntity.soldOut

            )
        }
        fun transform(users: Iterable<Trip>): List<TripDTO> {
            return users.map { transform(it) }
        }
    }
}
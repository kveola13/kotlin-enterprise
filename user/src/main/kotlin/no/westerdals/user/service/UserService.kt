package no.westerdals.user.service

import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.westerdals.user.DTO.UserConverter
import no.westerdals.user.DTO.UserDTO
import no.westerdals.user.entity.User
import no.westerdals.user.repo.UserRepository
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.beans.factory.annotation.Autowired

@RestController
@RequestMapping(path = ["/api"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
class UserService{

    @Autowired
    private lateinit var crud: UserRepository

    @ApiOperation("Get amount of users")
    @GetMapping(path = ["/userDetailsCount"],
        produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun getCount(): ResponseEntity<Long> {

        return ResponseEntity.ok(crud.count())
    }


    @ApiOperation("Get all user details")
    @GetMapping(path = ["/userDetails"],
        produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun getAll(): ResponseEntity<List<UserDTO>> {

        return ResponseEntity.ok(UserConverter.transform(crud.findAll()))
    }


    @ApiOperation("Get specific user details")
    @GetMapping(path = ["/userDetails/{id}"],
        produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun getById(
        @ApiParam("the username of a user")
        @PathVariable id: String)
            : ResponseEntity<UserDTO> {

        val entity = crud.findById(id).orElse(null)
            ?: return ResponseEntity.status(404).build()

        return ResponseEntity.ok(UserConverter.transform(entity))
    }



    @ApiOperation("Replace user details")
    @PutMapping(path = ["/userDetails/{id}"],
        consumes = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun replace(
        @ApiParam("the username of a user")
        @PathVariable id: String,
        @RequestBody dto: UserDTO)
            : ResponseEntity<Void> {

        if (id != dto.username) {
            return ResponseEntity.status(409).build()
        }

        val alreadyExists = crud.existsById(id)
        var code = if(alreadyExists) 204 else 201

        val entity = User(dto.username, dto.name, dto.surname, dto.email, dto.age, dto.purchasedTrips)

        try {
            crud.save(entity)
        } catch (e: Exception) {
            code = 400
        }

        return ResponseEntity.status(code).build()
    }

    @ApiOperation("Update the email")
    @PatchMapping(path = ["userDetails/{id}"], consumes = [(org.springframework.http.MediaType.APPLICATION_JSON_VALUE)])
    fun updateEmail(@ApiParam("the username of a user")
                    @PathVariable("id")
                    id: String?, @ApiParam("the new email") email: String): ResponseEntity<Any> {
        if (id == null) {
            return ResponseEntity.status(400).build()
        }
        if (!crud.existsById(id)) {
            return ResponseEntity.status(404).build()
        }
        try {
            crud.updateEmail(id, email)
        } catch (exception: Exception) {
            return ResponseEntity.status(400).build()
        }
        return ResponseEntity.status(204).build()
    }

    @ApiOperation("Delete a user with the given username")
    @DeleteMapping(path = ["/userDetails/{id}"])
    fun deleteByUsername(@ApiParam("The username of the user")
                         @PathVariable("id")
                         pathId: String?): ResponseEntity<Any> {

        if (!crud.existsById(pathId!!)) {
            return ResponseEntity.status(404).build()
        }

        crud.deleteById(pathId)
        return ResponseEntity.status(204).build()
    }
}
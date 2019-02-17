package no.westerdals.user.service

import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.kristiania.soj.groupexam.userservice.db.UserDetailsEntity
import no.kristiania.soj.groupexam.userservice.db.UserDetailsRepository
import no.kristiania.soj.groupexam.userservice.dto.UserDetailsConverter
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import no.kristiania.soj.groupexam.userservice.dto.UserDetailsDTO
import org.springframework.beans.factory.annotation.Autowired

@RestController
@RequestMapping(path = ["/api"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
class UserDetailsApi{

    @Autowired
    private lateinit var crud: UserDetailsRepository

    @ApiOperation("Get amount of users")
    @GetMapping(path = ["/userDetailsCount"],
        produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun getCount(): ResponseEntity<Long> {

        return ResponseEntity.ok(crud.count())
    }


    @ApiOperation("Get all user details")
    @GetMapping(path = ["/userDetails"],
        produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun getAll(): ResponseEntity<List<UserDetailsDTO>> {

        return ResponseEntity.ok(UserDetailsConverter.transform(crud.findAll()))
    }


    @ApiOperation("Get specific user details")
    @GetMapping(path = ["/userDetails/{id}"],
        produces = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun getById(
        @ApiParam("the username of a user")
        @PathVariable id: String)
            : ResponseEntity<UserDetailsDTO> {

        val entity = crud.findById(id).orElse(null)
            ?: return ResponseEntity.status(404).build()

        return ResponseEntity.ok(UserDetailsConverter.transform(entity))
    }



    @ApiOperation("Replace user details")
    @PutMapping(path = ["/userDetails/{id}"],
        consumes = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun replace(
        @ApiParam("the username of a user")
        @PathVariable id: String,
        @RequestBody dto: UserDetailsDTO)
            : ResponseEntity<Void> {

        if (id != dto.username) {
            return ResponseEntity.status(409).build()
        }

        val alreadyExists = crud.existsById(id)
        var code = if(alreadyExists) 204 else 201

        val entity = UserDetailsEntity(dto.username, dto.name, dto.surname, dto.email, dto.age, dto.purchasedTickets)

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
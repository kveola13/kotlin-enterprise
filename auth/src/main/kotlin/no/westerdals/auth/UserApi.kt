package no.westerdals.auth


import com.google.common.base.Throwables
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import no.westerdals.auth.db.UserRepository
import no.westerdals.auth.dto.UserConverter
import no.westerdals.auth.dto.UserDTO
import no.westerdals.auth.util.RestResponseFactory
import no.westerdals.auth.util.WrappedResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import javax.validation.ConstraintViolationException

@RestController
@RequestMapping(path = ["/api"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@Validated
class UserApi(
){

    @Autowired
    private lateinit var userCrud: UserRepository
/*
    @PostMapping(path = ["/signUp"],
            consumes = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun signIn(@RequestBody dto: UserDTO)
            : ResponseEntity<Void> {

        val username : String = dto.username!!
        val password : String = dto.password!!

        val registered = userCrud.createUser(username, password)

        if (!registered) {
            return ResponseEntity.status(400).build()
        }

        val userDetails = userDetailsService.loadUserByUsername(username)
        val token = UsernamePasswordAuthenticationToken(userDetails, password, userDetails.authorities)

        authenticationManager.authenticate(token)

        if (token.isAuthenticated) {
            SecurityContextHolder.getContext().authentication = token
        }

        return ResponseEntity.status(204).build()
    }

    @PostMapping(path = ["/login"],
            consumes = [(MediaType.APPLICATION_JSON_UTF8_VALUE)])
    fun login(@RequestBody dto: UserDTO)
            : ResponseEntity<Void> {

        val username : String = dto.username!!
        val password : String = dto.password!!

        val userDetails = try{
            userDetailsService.loadUserByUsername(username)
        } catch (e: UsernameNotFoundException){
            return ResponseEntity.status(400).build()
        }

        val token = UsernamePasswordAuthenticationToken(userDetails, password, userDetails.authorities)

        authenticationManager.authenticate(token)

        if (token.isAuthenticated) {
            SecurityContextHolder.getContext().authentication = token
            return ResponseEntity.status(204).build()
        }

        return ResponseEntity.status(400).build()
    }*/

    @ApiOperation("Simple user creation")
    @PostMapping(path = ["/users"],
            consumes = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @ApiResponse(code = 201, message = "The username of the created user")
    fun createUser(
            @ApiParam("Username and password. Do not specify role")
            @RequestBody
            dto: UserDTO

    ): ResponseEntity<WrappedResponse<Void>> {

        if (dto.username == null || dto.password == null) {
            return RestResponseFactory.userFailure("Username or password is missing")
        }

        try {
            userCrud.createUser(dto.username!!, dto.password!!)
        } catch (e: Exception) {
            if(Throwables.getRootCause(e) is ConstraintViolationException) {
                return RestResponseFactory.userFailure("Constraint violation")
            }
            throw e
        }

        return RestResponseFactory.created(URI.create("/api/users/" + dto.username))
    }

    @ApiOperation("Get all users")
    @GetMapping(path = ["/users"])
    fun getAll(

    ): ResponseEntity<WrappedResponse<List<UserDTO>>> {

        return ResponseEntity.status(200).body(
                WrappedResponse(
                        code = 200,
                        data = UserConverter.transform(userCrud.findAll()),
                        message = "Fetched all users"
                ).validated()
        )
    }

    @ApiOperation("Get a single user specified by username")
    @GetMapping(path = ["/users/{username}"])
    fun getByUsername(@ApiParam("The username of the user")
                @PathVariable("username")
                pathId: String?)
            : ResponseEntity<WrappedResponse<UserDTO>> {

        val user = userCrud.findById(pathId!!).orElse(null)
                ?: return RestResponseFactory.notFound(
                "The requested user with username '$pathId' is not in the database")

        return RestResponseFactory.payload(200, UserConverter.transform(user))
    }

    //private?
    @ApiOperation("Delete a user with the given username")
    @DeleteMapping(path = ["/users/{username}"])
    fun deleteByUsername(@ApiParam("The username of the user")
               @PathVariable("username")
               pathId: String?): ResponseEntity<WrappedResponse<Void>> {

        if (!userCrud.existsById(pathId!!)) {
            return RestResponseFactory.notFound(
                    "The requested user with username '$pathId' is not in the database")
        }

        userCrud.deleteById(pathId)
        return RestResponseFactory.noPayload(204)
    }

    //might just want it to be a PATCH
    @ApiOperation("Update a specific user")
    @PutMapping(path = ["/users/{username}"], consumes = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun updateByUsername(
            @ApiParam("The username of the user")
            @PathVariable("username")
            pathId: String,
            //
            @ApiParam("New data for updating the user")
            @RequestBody
            dto: UserDTO
    ): ResponseEntity<WrappedResponse<Void>> {

        if(dto.username == null){
            return RestResponseFactory.userFailure("Missing username JSON payload")
        }

        if(dto.username != pathId){
            return RestResponseFactory.userFailure("Inconsistent username between URL and JSON payload", 409)
        }

        val entity = userCrud.findById(pathId).orElse(null)
                ?: return RestResponseFactory.notFound(
                "The requested user with username '$pathId' is not in the database. " +
                        "This PUT operation will not create it.")

        //remember to hash the password when postgres is implemented
        entity.password = dto.password!!

        userCrud.save(entity)

        return RestResponseFactory.noPayload(204)
    }
}

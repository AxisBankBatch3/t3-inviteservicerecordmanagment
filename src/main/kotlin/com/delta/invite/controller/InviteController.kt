package com.delta.invite.controller

import com.delta.invite.jwt.JwtAuthTokenFilter
import com.delta.invite.model.User
import com.delta.invite.repository.UserRepository
import com.delta.invite.request.EmailRequestDto
import com.delta.invite.response.ResponseMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.util.*
import javax.validation.Valid


@RestController
@RequestMapping("/api/invite")
@CrossOrigin(origins = ["*"], maxAge = 3600)
class InviteController(@Autowired
                       var userRepository: UserRepository,

                       @Autowired
                       var encoder: PasswordEncoder,

                       @Autowired
                       var restTemplate: RestTemplate) {

    var email: EmailRequestDto? = null

    @PostMapping("/register")
    fun registerUser(@RequestBody signUpRequest: @Valid User?): ResponseEntity<*>? {
        if (userRepository.existsByUsername(signUpRequest?.username) == true) {
            return ResponseEntity<ResponseMessage>(
                ResponseMessage("Fail -> Username is already taken!"),
                HttpStatus.BAD_REQUEST
            )
        }
        val date = Date()
        val crunchifyUUID = UUID.randomUUID().toString()
        val user = User()
        user.fullName=signUpRequest?.fullName
        user.organization=signUpRequest?.organization
        user.username=signUpRequest?.username
        user.mobile=signUpRequest?.mobile
        user.password=encoder.encode(crunchifyUUID)
        user.role=signUpRequest?.role

        email = EmailRequestDto(
            "delta-Login Credentials",
            signUpRequest?.username,
            "username : " + signUpRequest?.username.toString() + "   password : " + crunchifyUUID
        )

        userRepository.save(user)
        val details: Optional<User?>? = userRepository.findByUsername(signUpRequest?.username)

        return ResponseEntity<ResponseMessage>(
            ResponseMessage(
                "User registered successfully!"
            ), HttpStatus.OK
        )
    }


    @PostMapping("/email")
    fun sendEmail(@RequestBody emailRequestDto: @Valid EmailRequestDto?): String? {
        emailRequestDto?.email=email?.email
        emailRequestDto?.body=email?.body
        emailRequestDto?.subject=email?.subject
        val headers = HttpHeaders()
        headers.setBearerAuth(JwtAuthTokenFilter.jwt.toString())
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        val entity: HttpEntity<EmailRequestDto> = HttpEntity<EmailRequestDto>(emailRequestDto, headers)
        val responseEntity = restTemplate.exchange(
            "http://localhost:8082/api/access/email",
            HttpMethod.POST, entity, String::class.java
        )
        return responseEntity.body
    }




    }
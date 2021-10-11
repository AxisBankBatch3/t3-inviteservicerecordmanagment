package com.delta.invite.service

import com.delta.invite.jwt.JwtAuthTokenFilter
import com.delta.invite.model.User
import com.delta.invite.repository.UserRepository
import com.delta.invite.request.EmailRequestDto
import com.delta.invite.response.ResponseMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.client.RestTemplate
import java.util.*


class InviteServiceImpl(
    @Autowired
    var userRepository: UserRepository,

    @Autowired
    var encoder: PasswordEncoder,

    @Autowired
    var restTemplate: RestTemplate

) : InviteService {

    lateinit var emailinvite: EmailRequestDto



    override fun registerUser(user: User): ResponseEntity<Any> {

        if(userRepository?.existsByUsername(user?.username) == true){
            return  ResponseEntity<Any>(
                ResponseMessage("fail-username already exist"),
                HttpStatus.BAD_REQUEST
            )
        }
            val crunchifyUUID = UUID.randomUUID().toString()

            var newUser =User(user.fullName,user.organization,user.username,user.mobile,encoder.encode(crunchifyUUID),user.role)

            emailinvite = EmailRequestDto("Delta-Login Credentials",
                                    user.username,
                                "username: "+user.username+"/n password: "+crunchifyUUID)

            userRepository.save(newUser)


        return ResponseEntity<Any>(
            ResponseMessage("User Registered Succesfully"),
            HttpStatus.OK
        )



    }

    override fun sendEmail(emailRequestDto: EmailRequestDto): String {
        emailRequestDto.email = emailinvite.email
        emailRequestDto.body = emailinvite.body
        emailRequestDto.subject =emailinvite.subject

        val headers = HttpHeaders()

        headers.setBearerAuth(JwtAuthTokenFilter.jwt.toString())
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        val entity: HttpEntity<EmailRequestDto> = HttpEntity<EmailRequestDto>(emailRequestDto, headers)

        val responseEntity = restTemplate.exchange(
            "http://localhost:8082/api/access/email",
            HttpMethod.POST, entity, String::class.java
        )

        return responseEntity.body!!
    }
}
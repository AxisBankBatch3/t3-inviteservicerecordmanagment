package com.delta.invite.service

import com.delta.invite.model.User
import com.delta.invite.request.EmailRequestDto
import org.springframework.http.ResponseEntity

interface InviteService {

    fun registerUser(user: User) : ResponseEntity<Any>

    fun sendEmail(emailRequestDto: EmailRequestDto) : String


}
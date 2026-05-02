package com.example.duralap.controller

import com.example.duralap.database.dto.*
import com.example.duralap.database.model.UserStatus
import com.example.duralap.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.NoSuchElementException

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = ["http://localhost:3000"])
class UserController(
    private val userService: UserService
) {



    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: String): ResponseEntity<UserResponse> {
        val user = userService.getUserById(id)
            ?: throw NoSuchElementException("User with ID \$id not found")
        return ResponseEntity.ok(user)
    }

    @GetMapping("/username/{username}")
    fun getUserByUsername(@PathVariable username: String): ResponseEntity<UserResponse> {
        val user = userService.getUserByUsername(username)
            ?: throw NoSuchElementException("User with username \$username not found")
        return ResponseEntity.ok(user)
    }

    @GetMapping("/email/{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<UserResponse> {
        val user = userService.getUserByEmail(email)
            ?: throw NoSuchElementException("User with email \$email not found")
        return ResponseEntity.ok(user)
    }

    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: String,
        @Valid @RequestBody request: UserUpdateRequest
    ): ResponseEntity<UserResponse> {
        val user = userService.updateUser(id, request)
        return ResponseEntity.ok(user)
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: String): ResponseEntity<Unit> {
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }




    @PatchMapping("/{id}/status")
    fun updateUserStatus(
        @PathVariable id: String,
        @RequestParam status: UserStatus
    ): ResponseEntity<UserResponse> {
        val user = userService.updateUserStatus(id, status)
        return ResponseEntity.ok(user)
    }

//    @PatchMapping("/{id}/call-status")
//    fun updateCallStatus(
//        @PathVariable id: String,
//        @RequestParam isInCall: Boolean,
//        @RequestParam(required = false) callId: String?
//    ): ResponseEntity<UserResponse> {
//        val user = userService.updateCallStatus(id, isInCall, callId)
//        return ResponseEntity.ok(user)
//    }



    @GetMapping("/check-username/{username}")
    fun checkUsername(@PathVariable username: String): ResponseEntity<Map<String, Boolean>> {
        val exists = userService.usernameExists(username)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }

    @GetMapping("/check-email/{email}")
    fun checkEmail(@PathVariable email: String): ResponseEntity<Map<String, Boolean>> {
        val exists = userService.emailExists(email)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }


}
package com.example.duralap.chat.domain.repository

import com.example.duralap.chat.domain.model.UserConversations
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserConversationsRepository : MongoRepository<UserConversations, String>

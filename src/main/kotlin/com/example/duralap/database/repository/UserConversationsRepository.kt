package com.example.duralap.database.repository

import com.example.duralap.database.model.UserConversations
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserConversationsRepository : MongoRepository<UserConversations, String>

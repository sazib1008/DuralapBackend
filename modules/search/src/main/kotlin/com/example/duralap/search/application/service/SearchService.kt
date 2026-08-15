package com.example.duralap.search.application.service

import com.example.duralap.chat.domain.model.Conversation
import com.example.duralap.user.domain.model.User
import com.example.duralap.user.domain.repository.UserRepository
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
class SearchService(
    private val userRepository: UserRepository,
    private val mongoTemplate: MongoTemplate
) {

    fun searchUsers(q: String, cursor: String?, limit: Int): Pair<List<User>, String?> {
        val query = Query()
        
        val criteriaList = mutableListOf(
            Criteria.where("username").regex(q, "i"),
            Criteria.where("fullName").regex(q, "i")
        )
        
        if (q.contains("@")) {
            criteriaList.add(Criteria.where("email").regex("^" + Pattern.quote(q) + "$", "i"))
        }
        
        query.addCriteria(Criteria().orOperator(*criteriaList.toTypedArray()))
        
        if (!cursor.isNullOrBlank()) {
            query.addCriteria(Criteria.where("id").gt(cursor))
        }
        
        query.with(Sort.by(Sort.Direction.ASC, "id"))
        query.limit(limit + 1)
        
        val users = mongoTemplate.find(query, User::class.java)
        
        val hasNext = users.size > limit
        val resultList = if (hasNext) users.subList(0, limit) else users
        val nextCursor = if (hasNext) resultList.last().id else null
        
        return Pair(resultList, nextCursor)
    }

    fun searchConversations(userId: String, q: String, cursor: String?, limit: Int): Pair<List<Conversation>, String?> {
        val query = Query()
        query.addCriteria(Criteria.where("participantIds").`in`(userId))
        
        if (!cursor.isNullOrBlank()) {
            query.addCriteria(Criteria.where("id").gt(cursor))
        }
        
        query.with(Sort.by(Sort.Direction.ASC, "id"))
        query.limit(limit * 5)
        
        val conversations = mongoTemplate.find(query, Conversation::class.java)
        
        val matchedConversations = mutableListOf<Conversation>()
        for (conv in conversations) {
            val otherParticipantIds = conv.participantIds.filter { it != userId }
            if (otherParticipantIds.isEmpty()) {
                val selfUser = userRepository.findById(userId).orElse(null)
                if (selfUser != null && (selfUser.username.contains(q, ignoreCase = true) || 
                    selfUser.fullName?.contains(q, ignoreCase = true) == true)) {
                    matchedConversations.add(conv)
                }
            } else {
                val otherUsers = userRepository.findAllById(otherParticipantIds)
                val match = otherUsers.any { 
                    it.username.contains(q, ignoreCase = true) || 
                    it.fullName?.contains(q, ignoreCase = true) == true
                }
                if (match) {
                    matchedConversations.add(conv)
                }
            }
            if (matchedConversations.size >= limit + 1) break
        }
        
        val hasNext = matchedConversations.size > limit
        val resultList = if (hasNext) matchedConversations.subList(0, limit) else matchedConversations
        val nextCursor = if (hasNext) resultList.last().id else null
        
        return Pair(resultList, nextCursor)
    }
}

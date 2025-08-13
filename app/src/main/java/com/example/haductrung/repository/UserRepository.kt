package com.example.haductrung.repository

import com.example.haductrung.database.dao.UserDao
import com.example.haductrung.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

class UserRepository (private val userDao: UserDao){
    suspend fun findUserByUsername(username:String):UserEntity?{
        return userDao.getUserByUsername(username)
    }
    suspend fun createUser(user:UserEntity){
        userDao.insertUser(user)
    }
    suspend fun updateUser(user: UserEntity){
        userDao.updateUser(user
        )
    }
     fun findUserById(userId: Int): Flow<UserEntity?>? {
        return userDao.findUserById(userId)
    }
}
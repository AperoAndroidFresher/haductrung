package com.example.haductrung.repository

import com.example.haductrung.database.DAO.UserDao
import com.example.haductrung.database.entity.UserEntity

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
    suspend fun findUserById(userId: Int): UserEntity? {
        return userDao.findUserById(userId)
    }
}
package com.example.haductrung.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.haductrung.database.entity.UserEntity

@Dao
interface UserDao{
    @Insert
    suspend fun insertUser(user: UserEntity)
    @Update
    suspend fun updateUser(user : UserEntity)
    @Query("""
        select *
        from users
        where username = :username LIMIT 1
    """)
    suspend fun getUserByUsername(username : String):UserEntity

    @Query("""
        SELECT * 
        FROM users 
        WHERE userId = :userId LIMIT 1
    """)
    suspend fun findUserById(userId: Int): UserEntity?
}
package database

import androidx.room.*
import database.MyData
import kotlinx.coroutines.flow.Flow

@Dao
interface MyDataBaseDao {
    @Insert
    suspend fun insert(user: MyData)

    @Update
    suspend fun update(user: MyData)

    @Query("SELECT * FROM my_data_table ORDER BY id DESC LIMIT 1")
    suspend fun getLastUser(): MyData?

    @Query("SELECT * FROM my_data_table WHERE id = :id")
    suspend fun getUserById(id: Int): MyData?
}
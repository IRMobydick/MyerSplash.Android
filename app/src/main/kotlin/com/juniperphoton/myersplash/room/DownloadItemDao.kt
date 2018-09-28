package com.juniperphoton.myersplash.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import io.reactivex.Flowable

@Dao
interface DownloadItemDao {
    @Query("SELECT * FROM DownloadItem WHERE id =:id")
    fun getDownloadItem(id: String?): DownloadItem?

    @Query("SELECT * FROM DownloadItem WHERE id =:id")
    fun getDownloadItemRx(id: String?): Flowable<DownloadItem>

    @Query("SELECT * FROM DownloadItem")
    fun getAllDownloadItems(): List<DownloadItem>

    @Query("SELECT * FROM DownloadItem")
    fun getAllDownloadItemsRx(): Flowable<DownloadItem>

    @Insert(onConflict = REPLACE)
    fun insert(vararg item: DownloadItem)

    @Delete
    fun delete(item: DownloadItem)

    @Query("UPDATE DownloadItem SET status=:status, progress=:progress WHERE id LIKE :itemId")
    fun update(itemId: String, status: Int, progress: Int)

    @Query("DELETE FROM DownloadItem WHERE status=:status")
    fun deleteAll(status: Int)
}
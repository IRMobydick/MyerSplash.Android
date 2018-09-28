package com.juniperphoton.myersplash.utils

import com.juniperphoton.myersplash.room.AppDatabase
import com.juniperphoton.myersplash.room.DownloadItem

/**
 * Helper class for updating download item status in realm.
 */
@Suppress("unused")
object DownloadItemTransactionUtil {
    /**
     * Delete a managed download [item].
     */
    fun delete(item: DownloadItem) {
        AppDatabase.instance.downloadItemDao()
                .delete(item)
    }

    /**
     * Update download [status] of a download [item].
     * @param status see [DownloadItem.DownloadStatus].
     */
    fun updateStatus(item: DownloadItem, @DownloadItem.DownloadStatus status: Int) {
        updateStatus(item.id, status, if (status == DownloadItem.DOWNLOAD_STATUS_FAILED) {
            0
        } else {
            item.progress
        })
    }

    /**
     * Update download [status] of given a [id].
     * @param status see [DownloadItem.DownloadStatus].
     */
    fun updateStatus(id: String, @DownloadItem.DownloadStatus status: Int, progress: Int) {
        AppDatabase.instance.downloadItemDao()
                .update(id, status, if (status == DownloadItem.DOWNLOAD_STATUS_FAILED) {
                    0
                } else {
                    progress
                })
    }
}
package com.juniperphoton.myersplash.room

import androidx.annotation.IntDef
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
class DownloadItem {
    companion object {
        const val DOWNLOAD_STATUS_INVALID = -1
        const val DOWNLOAD_STATUS_DOWNLOADING = 0
        const val DOWNLOAD_STATUS_FAILED = 1
        const val DOWNLOAD_STATUS_OK = 2

        const val DISPLAY_STATUS_NOT_SPECIFIED = -1

        const val ID_KEY = "id"
        const val DOWNLOAD_URL = "downloadUrl"
        const val STATUS_KEY = "status"
        const val POSITION_KEY = "position"
    }

    @IntDef(DOWNLOAD_STATUS_DOWNLOADING, DOWNLOAD_STATUS_OK, DOWNLOAD_STATUS_FAILED)
    @Retention(AnnotationRetention.SOURCE)
    annotation class DownloadStatus

    @PrimaryKey
    var id: String = ""

    var thumbUrl: String? = null

    var downloadUrl: String? = null

    var progress: Int = 0
        set(value) {
            field = value
            if (this.progress >= 100) {
                status = DOWNLOAD_STATUS_OK
            }
        }

    var color: Int = 0
    var status: Int = 0
    var filePath: String? = null
    var fileName: String? = null
    var position: Int = 0

    @Ignore
    var lastStatus = DISPLAY_STATUS_NOT_SPECIFIED

    constructor(id: String, thumbUrl: String, downloadUrl: String, fileName: String) {
        this.id = id
        this.thumbUrl = thumbUrl
        this.downloadUrl = downloadUrl
        this.status = DOWNLOAD_STATUS_DOWNLOADING
        this.fileName = fileName
    }

    open fun syncStatus() {
        lastStatus = status
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DownloadItem

        if (id != other.id) return false
        if (thumbUrl != other.thumbUrl) return false
        if (downloadUrl != other.downloadUrl) return false
        if (progress != other.progress) return false
        if (color != other.color) return false
        if (status != other.status) return false
        if (filePath != other.filePath) return false
        if (fileName != other.fileName) return false
        if (position != other.position) return false
        if (lastStatus != other.lastStatus) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (thumbUrl?.hashCode() ?: 0)
        result = 31 * result + (downloadUrl?.hashCode() ?: 0)
        result = 31 * result + progress
        result = 31 * result + color
        result = 31 * result + status
        result = 31 * result + (filePath?.hashCode() ?: 0)
        result = 31 * result + (fileName?.hashCode() ?: 0)
        result = 31 * result + position
        result = 31 * result + lastStatus
        return result
    }
}
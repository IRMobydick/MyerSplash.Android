package com.juniperphoton.myersplash.activity

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.juniperphoton.myersplash.R
import com.juniperphoton.myersplash.RealmCache
import com.juniperphoton.myersplash.adapter.DownloadsListAdapter
import com.juniperphoton.myersplash.room.AppDatabase
import com.juniperphoton.myersplash.room.DownloadItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Sort
import java.util.*

@Suppress("unused")
class ManageDownloadActivity : BaseActivity() {
    companion object {
        private const val TAG = "ManageDownloadActivity"
        const val ACTION = "action.downloads"
    }

    @BindView(R.id.downloads_list)
    lateinit var downloadsList: androidx.recyclerview.widget.RecyclerView

    @BindView(R.id.no_item_view)
    lateinit var noItemView: TextView

    @BindView(R.id.downloads_more_fab)
    lateinit var moreFab: FloatingActionButton

    private var adapter: DownloadsListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_download)
        ButterKnife.bind(this)

        initViews()
    }

    @OnClick(R.id.downloads_more_fab)
    internal fun onClickMore() {
        AlertDialog.Builder(this).setTitle(R.string.clear_options_title)
                .setItems(R.array.delete_options) { _, i ->
                    val deleteStatus = when (i) {
                        0 -> DownloadItem.DOWNLOAD_STATUS_DOWNLOADING
                        1 -> DownloadItem.DOWNLOAD_STATUS_OK
                        2 -> DownloadItem.DOWNLOAD_STATUS_FAILED
                        else -> DownloadItem.DOWNLOAD_STATUS_INVALID
                    }
                    deleteFromRealm(deleteStatus)
                }
                .setPositiveButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
    }

    private fun deleteFromRealm(status: Int) {
        AppDatabase.instance.downloadItemDao().deleteAll(status)
        initViews()
    }

    private fun updateNoItemVisibility() {
        if ((adapter?.data?.size ?: 0) > 0) {
            noItemView.visibility = View.GONE
        } else {
            noItemView.visibility = View.VISIBLE
        }
    }

    private fun initViews() {
        val downloadItems = ArrayList<DownloadItem>()

        AppDatabase.instance.downloadItemDao().getAllDownloadItemsRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    adapter.refreshItems(it)
                }

        RealmCache.getInstance()
                .where(DownloadItem::class.java)
                .findAllSorted(DownloadItem.POSITION_KEY, Sort.DESCENDING)
                .forEach {
                    downloadItems.add(it)
                }

        downloadItems.forEach {
            it.addChangeListener(itemStatusChangedListener)
        }

        val items = AppDatabase.instance.downloadItemDao()
                .getAllDownloadItems()

        if (adapter == null) {
            adapter = DownloadsListAdapter(this)
        }

        adapter!!.refreshItems(downloadItems)

        val layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2).apply {
            spanSizeLookup = object : androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position == adapter!!.itemCount - 1) 2 else 1
                }
            }
        }

        downloadsList.layoutManager = layoutManager
        downloadsList.adapter = adapter

        // We don't change the item animator so we cast it directly
        (downloadsList.itemAnimator as androidx.recyclerview.widget.SimpleItemAnimator).supportsChangeAnimations = false

        updateNoItemVisibility()
    }

    override fun onConfigNavigationBar(navigationBarHeight: Int) {
        if (navigationBarHeight > 0) {
            val params = moreFab.layoutParams as ViewGroup.MarginLayoutParams
            val previousMargin = params.bottomMargin
            params.setMargins(0, 0, previousMargin, previousMargin + navigationBarHeight)
            moreFab.layoutParams = params
        }
    }
}

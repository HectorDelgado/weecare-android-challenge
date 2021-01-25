package com.weemusic.android.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.picasso.Picasso
import com.weemusic.android.R
import com.weemusic.android.core.DaggerAppComponent
import com.weemusic.android.core.DaggerDomainComponent
import com.weemusic.android.core.DaggerNetworkComponent
import com.weemusic.android.domain.Album
import com.weemusic.android.domain.GetTopAlbumsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.threeten.bp.LocalDate
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var getTopAlbumsUseCase: GetTopAlbumsUseCase
    private lateinit var adapter: AlbumsAdapter
    private lateinit var topAlbumsDisposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val networkComponent = DaggerNetworkComponent.create()
        val domainComponent = DaggerDomainComponent
            .builder()
            .networkComponent(networkComponent)
            .build()

        DaggerAppComponent
            .builder()
            .domainComponent(domainComponent)
            .build()
            .inject(this)

        AndroidThreeTen.init(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
    }

    override fun onStart() {
        super.onStart()
        topAlbumsDisposable = getTopAlbumsUseCase
            .perform()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { response ->
                response.getAsJsonObject("feed")
                    .getAsJsonArray("entry")
                    .map { it.asJsonObject }
            }
            .subscribe(Consumer {
                adapter = AlbumsAdapter(it)
                rvFeed.adapter = adapter
                rvFeed.layoutManager =
                    GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_sort, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.sort_album -> {
                val sortedAlbum = adapter.albums.sortedBy { albums ->
                    albums.getAsJsonObject("im:name")
                        .getAsJsonPrimitive("label")
                        .asString
                }
                adapter = AlbumsAdapter(sortedAlbum)
                rvFeed.adapter = adapter
                true
            }
            R.id.sort_price -> {
                val sortedAlbums = adapter.albums.sortedBy { albums ->
                    albums.getAsJsonObject("im:price")
                        .getAsJsonObject("attributes")
                        .getAsJsonPrimitive("amount")
                        .asDouble
                }
                adapter = AlbumsAdapter(sortedAlbums)
                rvFeed.adapter = adapter
                true
            }
            R.id.sort_title -> {
                val sortedAlbums = adapter.albums.sortedBy { albums ->
                    albums.getAsJsonObject("title")
                        .getAsJsonPrimitive("label")
                        .asString
                }
                adapter = AlbumsAdapter(sortedAlbums)
                rvFeed.adapter = adapter
                true
            }
            R.id.sort_artist -> {
                val sortedAlbums = adapter.albums.sortedBy { albums ->
                    albums.getAsJsonObject("im:artist")
                        .getAsJsonPrimitive("label")
                        .asString
                }
                adapter = AlbumsAdapter(sortedAlbums)
                rvFeed.adapter = adapter
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    class AlbumsAdapter(val albums: List<JsonObject>) : RecyclerView.Adapter<AlbumsViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumsViewHolder {
            val itemView = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.album_view_holder, parent, false)

            return AlbumsViewHolder(itemView)
        }

        override fun getItemCount(): Int = albums.size

        override fun onBindViewHolder(holder: AlbumsViewHolder, position: Int) =
            holder.onBind(albums[position])
    }

    class AlbumsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun onBind(album: JsonObject) {
            val newAlbum = Album.from(album)

            val ivCover: ImageView = itemView.findViewById(R.id.ivCover)
            val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
            val tvArtist: TextView = itemView.findViewById(R.id.tvArtist)
            val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
            val tvNew: TextView = itemView.findViewById(R.id.tvNew)

            Picasso.with(itemView.context).load(newAlbum.images.last()).into(ivCover)
            tvTitle.text = newAlbum.title
            tvArtist.text = newAlbum.artist
            tvPrice.text = "$${newAlbum.price}"
            tvNew.visibility = if (LocalDate.now().toEpochDay() - newAlbum.releaseDate.toEpochDay() <= 60)
                View.VISIBLE else View.GONE
        }
    }
}

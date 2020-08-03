package com.mif50.filedownloader.ui.media

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.Observer
import com.mif50.filedownloader.R
import com.mif50.filedownloader.di.component.ActivityComponent
import com.mif50.filedownloader.ui.base.BaseActivity
import com.mif50.filedownloader.utils.common.LayoutRes
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_media_player.*

@LayoutRes(layout = R.layout.activity_media_player)
class MediaPlayerActivity : BaseActivity<MediaPlayerViewModel>() {

    private lateinit var mediaDataSourceFactory: DataSource.Factory
    lateinit var player: SimpleExoPlayer
    private var currentVolume = 0f
    private var seekVideo: Long = 0L
    private var videoMuted: Boolean = false


    companion object {
        private const val TAG = "MediaPlayerActivity"

    }

    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun setupView(savedInstanceState: Bundle?) {
        initExoPlayer()
        setListener()
    }

    override fun setupObservers() {
        super.setupObservers()
        observeVideoPath()
    }

    private fun observeVideoPath(){
        viewModel.videoPathLiveData.observe(this, Observer {
            it.getIfNotHandled()?.run {
                setupMediaSource(this)
            }
        })
    }

    private fun initExoPlayer(){
        player = SimpleExoPlayer.Builder(this).build()
        mediaDataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "mediaPlayerSample"))

        playerView.setShutterBackgroundColor(Color.TRANSPARENT)
        playerView.player = player
        playerView.requestFocus()

        currentVolume = player.volume
        if (videoMuted) player.volume = 0f
        imageViewVolume.isSelected = videoMuted
    }

    private fun setupMediaSource(videoPath: String){
        val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory)
            .createMediaSource(Uri.parse(videoPath))

        with(player) {
            prepare(mediaSource, false, false)
            player.seekTo(seekVideo)
            playWhenReady = true
        }
    }

    private fun setListener(){
        imageViewVolume.setOnClickListener { handleMute() }

        player.addListener(object :  Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playWhenReady && playbackState == Player.STATE_ENDED) {
                    player.seekTo(0)
                    player.playWhenReady
                }
            }
        })
    }

    private fun handleMute(){
        if (videoMuted) {
            player.volume = currentVolume
        } else {
            player.volume = 0f
        }
        videoMuted = !videoMuted

        imageViewVolume.isSelected = videoMuted
    }

    override fun onPause() {
        super.onPause()
        player.playWhenReady = false
    }

    override fun onResume() {
        super.onResume()
        player.playWhenReady = true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Util.SDK_INT > 23) player.release()
    }

}
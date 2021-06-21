package com.prayosof.yvideo.view.fragment.audiplayer

import denis.musicplayer.data.media.model.Album
import denis.musicplayer.data.media.model.Artist
import denis.musicplayer.data.media.model.Genre
import denis.musicplayer.data.media.model.Track

/**
 * Created by Yogesh Y. Nikam on 12/07/20.
 */
interface MediaManager {
    fun scanTracks(): ArrayList<Track>

    fun scanAlbums(): ArrayList<Album>
    fun scanAlbumTracks(albumID: Long): ArrayList<Track>
    fun getAlbumImagePath(albumID: Long): String?

    fun scanArtists(): ArrayList<Artist>
    fun scanArtistTracks(artistID: Long): ArrayList<Track>

    fun scanGenres(): ArrayList<Genre>
    fun scanGenreTracks(genreID: Long): ArrayList<Track>
}
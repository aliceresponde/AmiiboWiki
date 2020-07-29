/*
 * Copyright 2020 Oscar David Gallon Rosero
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package com.oscarg798.amiibowiki.core.network.models

import com.google.gson.annotations.SerializedName
import com.oscarg798.amiibowiki.core.models.CoverUrl
import com.oscarg798.amiibowiki.core.models.Game
import com.oscarg798.amiibowiki.core.models.GameCategory
import com.oscarg798.amiibowiki.core.models.Id
import com.oscarg798.amiibowiki.core.models.VideoId
import com.oscarg798.amiibowiki.core.models.WebSiteId
import com.oscarg798.amiibowiki.core.models.WebSiteUrl

data class APIGame(
    @SerializedName("id")
    val id: Int,
    @SerializedName("category")
    val category: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("cover")
    val coverId: Int?,
    @SerializedName("summary")
    val summary: String?,
    @SerializedName("websites")
    val webSiteIds: Set<Int>?,
    @SerializedName("videos")
    val videosId: Set<Int>?,
    @SerializedName("rating")
    val raiting: Double?
) {

    fun toGame(
        gameName: String,
        covers: Map<Id, CoverUrl>,
        webSites: Map<WebSiteId, WebSiteUrl>,
        videos: Map<Id, VideoId>
    ) = Game(
        id,
        name,
        GameCategory.createFromCategoryId(category),
        covers[coverId],
        gameName,
        summary,
        mapWebSites(webSites),
        mapVideos(videos),
        raiting
    )

    private fun mapVideos(
        videos: Map<Id, VideoId>
    ): List<VideoId>? {
        return if (videosId != null) {
            videosId.mapNotNull { videos[it] }
        } else {
            null
        }
    }

    private fun mapWebSites(
        webSites: Map<WebSiteId, WebSiteUrl>
    ): List<WebSiteUrl>? {
        return if (webSiteIds != null) {
            webSiteIds.mapNotNull { webSites[it] }
        } else {
            null
        }
    }
}
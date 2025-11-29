package com.pgitv

import com.lagradost.cloudstream3.LiveSearchResponse
import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.MovieSearchResponse
import com.lagradost.cloudstream3.SearchResponse
import com.lagradost.cloudstream3.TvType
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.newLiveSearchResponse
import com.lagradost.cloudstream3.newMovieSearchResponse
import org.jsoup.nodes.Element

class PgiTvProvider : MainAPI() {
    override var mainUrl = "https://www.pgitv.com/"
    override var name = "雪糕TV"
    override val supportedTypes = setOf(TvType.Movie)

    override var lang = "zh"

    // Enable this when your provider has a main page2
    override val hasMainPage = true

    // This function gets called when you search for something
    override suspend fun search(query: String): List<SearchResponse> {
        val document =
            app.get(
                "$mainUrl/vsearch/-------------.html",
                params = mapOf("wd" to query),
                referer = mainUrl
            ).document
        return document.select("ul.list-show-all li").map { it.toMovieSearchResponse() }
    }

    private fun Element.toMovieSearchResponse(): MovieSearchResponse {
        val anchor = this.select("a")
        val linkName = anchor.attr("href").substringAfterLast("/")
        val name = anchor.firstOrNull { it.text().isNotBlank() }?.text()
        val image = this.select("img").attr("src")
        return newMovieSearchResponse(
            name ?: "",
            linkName,
            TvType.Live,
            fix = false
        ) { posterUrl = image }
    }
}
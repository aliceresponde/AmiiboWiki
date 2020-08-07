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

package com.oscarg798.amiibowiki.amiibodetail.mvi

import com.oscarg798.amiibowiki.amiibodetail.errors.AmiiboDetailFailure
import com.oscarg798.amiibowiki.amiibodetail.models.ViewAmiiboDetails
import com.oscarg798.amiibowiki.core.mvi.ViewState

data class AmiiboDetailViewState(
    val loading: ViewState.LoadingState,
    val status: Status,
    val error: AmiiboDetailFailure? = null
) : ViewState<AmiiboDetailResult> {

    sealed class Status {
        object None : Status()
        data class ShowingAmiiboDetails(
            val amiiboDetails: ViewAmiiboDetails,
            val isRelatedGamesSectionEnabled: Boolean
        ) : Status()

        data class ShowingGameDetails(val gameId: Int, val gameSeries: String) : Status()
    }

    override fun reduce(result: AmiiboDetailResult): ViewState<AmiiboDetailResult> {
        return when (result) {
            is AmiiboDetailResult.DetailFetched -> copy(
                loading = ViewState.LoadingState.None,
                status = Status.ShowingAmiiboDetails(
                    result.amiibo,
                    result.isRelatedGamesSectionEnabled
                ),
                error = null
            )
            is AmiiboDetailResult.ShowGameDetails -> copy(
                loading = ViewState.LoadingState.None,
                status = Status.ShowingGameDetails(result.gameId, result.gameSeries),
                error = null
            )
            is AmiiboDetailResult.Loading -> copy(
                loading = ViewState.LoadingState.Loading,
                status = Status.None,
                error = null
            )
            is AmiiboDetailResult.Error -> copy(
                loading = ViewState.LoadingState.None,
                status = Status.None,
                error = result.error
            )
            is AmiiboDetailResult.None -> copy(
                loading = ViewState.LoadingState.None,
                status = Status.None,
                error = null
            )
        }
    }

    companion object {

        fun init() =
            AmiiboDetailViewState(
                ViewState.LoadingState.None,
                Status.None
            )
    }
}
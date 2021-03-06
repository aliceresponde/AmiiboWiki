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

package com.oscarg798.amiibowiki.amiibodetail

import com.oscarg798.amiibowiki.amiibodetail.logger.AmiiboDetailLogger
import com.oscarg798.amiibowiki.amiibodetail.models.ViewAmiiboDetails
import com.oscarg798.amiibowiki.amiibodetail.mvi.AmiiboDetailReducer
import com.oscarg798.amiibowiki.amiibodetail.mvi.AmiiboDetailViewState
import com.oscarg798.amiibowiki.amiibodetail.mvi.AmiiboDetailWish
import com.oscarg798.amiibowiki.amiibodetail.mvi.ShowingAmiiboDetailsParams
import com.oscarg798.amiibowiki.core.failures.AmiiboDetailFailure
import com.oscarg798.amiibowiki.core.featureflaghandler.AmiiboWikiFeatureFlag
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.models.AmiiboReleaseDate
import com.oscarg798.amiibowiki.core.models.GameSearchResult
import com.oscarg798.amiibowiki.core.usecases.GetAmiiboDetailUseCase
import com.oscarg798.amiibowiki.core.usecases.IsFeatureEnableUseCase
import com.oscarg798.amiibowiki.testutils.extensions.relaxedMockk
import com.oscarg798.amiibowiki.testutils.testrules.ViewModelTestRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class AmiiboDetailViewModelTest :
    ViewModelTestRule.ViewModelCreator<AmiiboDetailViewState, AmiiboDetailViewModel> {

    @get: Rule
    val viewModelTestTule = ViewModelTestRule<AmiiboDetailViewState, AmiiboDetailViewModel>(this)

    private val logger = relaxedMockk<AmiiboDetailLogger>()
    private val getAmiiboDetailUseCase = mockk<GetAmiiboDetailUseCase>()
    private val isFeatureFlagEnableUseCase = mockk<IsFeatureEnableUseCase>()
    private val reducer = spyk(AmiiboDetailReducer())

    @Before
    fun setup() {
        coEvery { getAmiiboDetailUseCase.execute(TAIL) } answers { AMIIBO }
        every { isFeatureFlagEnableUseCase.execute(AmiiboWikiFeatureFlag.ShowRelatedGames) } answers { false }
        every { isFeatureFlagEnableUseCase.execute(AmiiboWikiFeatureFlag.ShowGameDetail) } answers { false }
    }

    override fun create(): AmiiboDetailViewModel = AmiiboDetailViewModel(
        TAIL,
        getAmiiboDetailUseCase,
        logger,
        isFeatureFlagEnableUseCase,
        reducer,
        viewModelTestTule.coroutineContextProvider
    )

    @Test
    fun `given showrelated games FF is off and ShowDetail wish when view model process it then it should update the state with the amiibo result`() {
        viewModelTestTule.viewModel.onWish(AmiiboDetailWish.ShowAmiiboDetail)

        viewModelTestTule.testCollector wereValuesEmitted listOf(
            AmiiboDetailViewState(
                isIdling = true,
                isLoading = false,
                imageExpanded = null,
                amiiboDetails = null,
                error = null
            ),
            AmiiboDetailViewState(
                isIdling = false,
                isLoading = true,
                imageExpanded = null,
                amiiboDetails = null,
                error = null
            ),
            AmiiboDetailViewState(
                isIdling = false,
                isLoading = false,
                imageExpanded = null,
                amiiboDetails = ShowingAmiiboDetailsParams(VIEW_AMIIBO_DETAIL, false),
                error = null
            )
        )

        coVerify {
            getAmiiboDetailUseCase.execute(TAIL)
        }

        coVerify(exactly = 2) {
            reducer.reduce(any(), any())
        }
    }

    @Test
    fun `given show related games FF is on and ShowDetail wish when view model process it then it should update the state with the amiibo result`() {
        every { isFeatureFlagEnableUseCase.execute(AmiiboWikiFeatureFlag.ShowRelatedGames) } answers { true }
        viewModelTestTule.viewModel.onWish(AmiiboDetailWish.ShowAmiiboDetail)

        viewModelTestTule.testCollector wereValuesEmitted listOf(
            AmiiboDetailViewState(
                isIdling = true,
                isLoading = false,
                imageExpanded = null,
                amiiboDetails = null,
                error = null
            ),
            AmiiboDetailViewState(
                isIdling = false,
                isLoading = true,
                imageExpanded = null,
                amiiboDetails = null,
                error = null
            ),
            AmiiboDetailViewState(
                isIdling = false,
                isLoading = false,
                imageExpanded = null,
                amiiboDetails = ShowingAmiiboDetailsParams(VIEW_AMIIBO_DETAIL, true),
                error = null
            )
        )

        coVerify {
            getAmiiboDetailUseCase.execute(TAIL)
        }

        coVerify(exactly = 2) {
            reducer.reduce(any(), any())
        }
    }

    @Test
    fun `given show amiibo detail wish when view model process and there is an AmiiboNotFoundByTail failure it then it should update the state with the error`() {
        coEvery { getAmiiboDetailUseCase.execute(TAIL) } throws AmiiboDetailFailure.AmiiboNotFoundByTail(
            TAIL
        )

        viewModelTestTule.viewModel.onWish(AmiiboDetailWish.ShowAmiiboDetail)

        viewModelTestTule.testCollector wereValuesEmitted listOf(
            AmiiboDetailViewState(
                isIdling = true,
                isLoading = false,
                amiiboDetails = null,
                imageExpanded = null,
                error = null
            ),
            AmiiboDetailViewState(
                isIdling = false,
                isLoading = true,
                amiiboDetails = null,
                imageExpanded = null,
                error = null
            ),
            AmiiboDetailViewState(
                isIdling = false,
                isLoading = false,
                imageExpanded = null,
                amiiboDetails = null,
                error = AmiiboDetailFailure.AmiiboNotFoundByTail(TAIL)
            )
        )

        coVerify {
            getAmiiboDetailUseCase.execute(TAIL)
        }

        coVerify(exactly = 2) {
            reducer.reduce(any(), any())
        }
    }

    @Test
    fun `given expand image wish when its processed then it should return the expanded image`() {
        viewModelTestTule.viewModel.onWish(AmiiboDetailWish.ExpandAmiiboImage(AMIIBO_IMAGE_URL))

        viewModelTestTule.testCollector wereValuesEmitted listOf(
            AmiiboDetailViewState(
                isIdling = true,
                isLoading = false,
                amiiboDetails = null,
                imageExpanded = null,
                error = null
            ),
            AmiiboDetailViewState(
                isIdling = false,
                isLoading = false,
                amiiboDetails = null,
                imageExpanded = AMIIBO_IMAGE_URL,
                error = null
            )
        )
    }

    @Ignore("Crash is in the coroutine boundary, in the viewmodel scope")
    @Test(expected = NullPointerException::class)
    fun `given ShowDetail wish when view model process and there is an Exception failure it then it should throw it`() {
        coEvery { getAmiiboDetailUseCase.execute(TAIL) } throws NullPointerException()

        runBlockingTest {
            viewModelTestTule.viewModel.onWish(AmiiboDetailWish.ShowAmiiboDetail)
            viewModelTestTule.viewModel.state.launchIn(this).cancelAndJoin()
        }
    }

    @Test
    fun `when show amiibo details wish is emitted then it should track the view as shown with the properties`() {
        viewModelTestTule.viewModel.onWish(AmiiboDetailWish.ShowAmiiboDetail)

        verify {
            logger.trackScreenShown(
                mapOf(
                    TAIL_TRACKING_PROPERTY to AMIIBO.tail,
                    HEAD_TRACKING_PROPERTY to AMIIBO.head,
                    TYPE_TRACKING_PROPERTY to AMIIBO.type,
                    NAME_TRACKING_PROPERTY to AMIIBO.name,
                    GAME_SERIES_TRACKING_PROPERTY to AMIIBO.gameSeries
                )
            )
        }
    }
}

private const val AMIIBO_IMAGE_URL = "5"
private const val GAME_ID = 4
private const val GAME_SERIES = "22"
private const val TAIL_TRACKING_PROPERTY = "TAIL"
private const val HEAD_TRACKING_PROPERTY = "HEAD"
private const val TYPE_TRACKING_PROPERTY = "TYPE"
private const val NAME_TRACKING_PROPERTY = "NAME"
private const val GAME_SERIES_TRACKING_PROPERTY = "GAME_SERIES"
private val GAME_SEARCH_RESULTS = listOf(GameSearchResult(1, "2", "3", GAME_ID))
private val AMIIBO = Amiibo(
    amiiboSeries = "1",
    character = "2",
    gameSeries = GAME_SERIES,
    head = "4",
    image = AMIIBO_IMAGE_URL,
    type = "6",
    releaseDate = AmiiboReleaseDate("7", "8", "9", "10"),
    tail = "11", name = "12"
)
private val VIEW_AMIIBO_DETAIL = ViewAmiiboDetails(AMIIBO)
private const val TAIL = "1"

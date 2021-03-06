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

package com.oscarg798.amiibowiki.core

import com.oscarg798.amiibowiki.core.failures.FilterAmiiboFailure
import com.oscarg798.amiibowiki.core.failures.GetAmiibosFailure
import com.oscarg798.amiibowiki.core.models.Amiibo
import com.oscarg798.amiibowiki.core.models.AmiiboReleaseDate
import com.oscarg798.amiibowiki.core.models.AmiiboSearchQuery
import com.oscarg798.amiibowiki.core.network.models.APIAmiibo
import com.oscarg798.amiibowiki.core.network.models.APIAmiiboReleaseDate
import com.oscarg798.amiibowiki.core.network.models.GetAmiiboResponse
import com.oscarg798.amiibowiki.core.network.services.AmiiboService
import com.oscarg798.amiibowiki.core.persistence.dao.AmiiboDAO
import com.oscarg798.amiibowiki.core.persistence.models.DBAMiiboReleaseDate
import com.oscarg798.amiibowiki.core.persistence.models.DBAmiibo
import com.oscarg798.amiibowiki.core.repositories.AmiiboRepositoryImpl
import com.oscarg798.amiibowiki.network.exceptions.NetworkException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test

class AmiiboRepositoryTest {

    private val amiiboDAO = mockk<AmiiboDAO>()
    private val amiiboService = mockk<AmiiboService>()
    private lateinit var repository: AmiiboRepositoryImpl

    @Before
    fun before() {
        every { amiiboDAO.getAmiibos() } answers { flowOf(listOf(DB_AMIIBO)) }
        every { amiiboDAO.insert(any()) } answers { Unit }
        coEvery { amiiboDAO.getById("1") } answers { DB_AMIIBO }
        coEvery { amiiboService.get() } answers {
            GetAmiiboResponse(
                listOf(API_AMIIBO)
            )
        }
        coEvery { amiiboService.getAmiiboFilteredByType(any()) } answers {
            GetAmiiboResponse(
                listOf(
                    API_AMIIBO
                )
            )
        }
        repository = AmiiboRepositoryImpl(amiiboService, amiiboDAO)
    }

    @Test
    fun `when get amiibos is called then it should return the amiibos`() {
        val response = runBlocking { repository.getAmiibos().toList() }

        response.size shouldBeEqualTo 2

        listOf(
            Amiibo(
                "11", "12", "13", "14", "15", "16",
                AmiiboReleaseDate("19", "20", "21", "22"), "17", "18"
            )
        ) shouldBeEqualTo response[0]

        listOf(
            Amiibo(
                "11", "12", "13", "14", "15", "16",
                AmiiboReleaseDate("19", "20", "21", "22"), "17", "18"
            )
        ) shouldBeEqualTo response[1]

        coVerify {
            amiiboDAO.getAmiibos()
            amiiboService.get()
        }
    }

    @Test(expected = GetAmiibosFailure.ProblemInDataSource::class)
    fun `when there is an NetworkException_TimeOut updating amiibos then it should throw GetAmiibosFailure_ProblemInDataSource`() {
        coEvery { amiiboService.get() } throws NetworkException.TimeOut

        runBlocking { repository.getAmiibos().toList() }

        coVerify {
            amiiboDAO.getAmiibos()
            amiiboService.get()
        }
    }

    @Test(expected = GetAmiibosFailure.ProblemInDataSource::class)
    fun `when there is an NetworkException_UnknowHost updating amiibos then it should throw GetAmiibosFailure_ProblemInDataSource`() {
        coEvery { amiiboService.get() } throws NetworkException.UnknowHost("")
        runBlocking { repository.getAmiibos().toList() }

        coVerify {
            amiiboDAO.getAmiibos()
            amiiboService.get()
        }
    }

    @Test(expected = GetAmiibosFailure.ProblemInDataSource::class)
    fun `when there is an NetworkException_Connection updating amiibos then it should throw GetAmiibosFailure_ProblemInDataSource`() {
        coEvery { amiiboService.get() } throws NetworkException.Connection
        runBlocking { repository.getAmiibos().toList() }

        coVerify {
            amiiboDAO.getAmiibos()
            amiiboService.get()
        }
    }

    @Test(expected = GetAmiibosFailure.ProblemInDataSource::class)
    fun `when there is an exception updating amiibos then it should throw that exception`() {
        coEvery { amiiboService.get() } throws NetworkException.BadRequest("")

        runBlocking { repository.getAmiibos().toList() }

        coVerify {
            amiiboDAO.getAmiibos()
            amiiboService.get()
        }
    }

    @Test
    fun `given a name when filter amiibos is called then it shoudl return filtered amiibos`() {
        val result = runBlocking { repository.getAmiibosFilteredByTypeName("1") }
        listOf(AMIIBO) shouldBeEqualTo result

        coVerify {
            amiiboService.getAmiiboFilteredByType("1")
        }
    }

    @Test(expected = FilterAmiiboFailure.ErrorFilteringAmiibos::class)
    fun `when there is an NetworkException_TimeOut filtering amiibos by name then it should throw FilterAmiiboFailure_ErrorFilteringAmiibos`() {
        coEvery { amiiboService.getAmiiboFilteredByType("a") } throws NetworkException.TimeOut
        runBlocking { repository.getAmiibosFilteredByTypeName("a") }

        coVerify {
            amiiboDAO.getAmiibos()
            amiiboService.get()
        }
    }

    @Test(expected = FilterAmiiboFailure.ErrorFilteringAmiibos::class)
    fun `when there is an NetworkException_UnknowHost filtering amiibos then it should throw FilterAmiiboFailure_ErrorFilteringAmiibos`() {
        coEvery { amiiboService.getAmiiboFilteredByType("a") } throws NetworkException.UnknowHost("")
        runBlocking { repository.getAmiibosFilteredByTypeName("a") }

        coVerify {
            amiiboDAO.getAmiibos()
            amiiboService.get()
        }
    }

    @Test(expected = FilterAmiiboFailure.ErrorFilteringAmiibos::class)
    fun `when there is an NetworkException_Connection filtering amiibos then it should throw FilterAmiiboFailure_ErrorFilteringAmiibos`() {
        coEvery { amiiboService.getAmiiboFilteredByType("a") } throws NetworkException.Connection
        runBlocking { repository.getAmiibosFilteredByTypeName("a") }

        coVerify {
            amiiboDAO.getAmiibos()
            amiiboService.get()
        }
    }

    @Test(expected = FilterAmiiboFailure.ErrorFilteringAmiibos::class)
    fun `when there is an exception filtering amiibos then it should throw that exception`() {
        coEvery { amiiboService.getAmiiboFilteredByType("a") } throws NetworkException.BadRequest("")
        runBlocking { repository.getAmiibosFilteredByTypeName("a") }

        coVerify {
            amiiboDAO.getAmiibos()
            amiiboService.get()
        }
    }

    @Test
    fun `given an amiibo tail when get by id is called then it should return amiibo found`() {
        val result = runBlocking { repository.getAmiiboById("1") }
        result shouldBeEqualTo AMIIBO_MAPPED_FROM_DB
    }

    @Test(expected = IllegalArgumentException::class)
    fun `given a non existen tail id when get by id is called then it should return IllegalArgumentException`() {
        coEvery { amiiboDAO.getById("1") } answers { null }
        runBlocking { repository.getAmiiboById("1") }
    }

    @Test
    fun `given a name search query when search amiibos is executed then it should call the right method`() {
        every { amiiboDAO.searchByAmiiboName("%$MOCK_QUERY%") } answers { flowOf(listOf(DB_AMIIBO)) }

        runBlocking {
            repository.searchAmiibos(AmiiboSearchQuery.AmiiboName(MOCK_QUERY)).first()
        } shouldBeEqualTo listOf(AMIIBO_MAPPED_FROM_DB)

        verify {
            amiiboDAO.searchByAmiiboName("%$MOCK_QUERY%")
        }
    }

    @Test
    fun `given a character search query when search amiibos is executed then it should call the right method`() {
        every { amiiboDAO.searchByCharacter("%$MOCK_QUERY%") } answers {
            flowOf(
                listOf(
                    DB_AMIIBO.copy(
                        name = "other_name"
                    )
                )
            )
        }

        runBlocking {
            repository.searchAmiibos(AmiiboSearchQuery.Character(MOCK_QUERY)).first()
        } shouldBeEqualTo listOf(AMIIBO_MAPPED_FROM_DB.copy(name = "other_name"))

        verify {
            amiiboDAO.searchByCharacter("%$MOCK_QUERY%")
        }
    }

    @Test
    fun `given a game series search query when search amiibos is executed then it should call the right method`() {
        every { amiiboDAO.searchByGameSeries("%$MOCK_QUERY%") } answers {
            flowOf(
                listOf(
                    DB_AMIIBO.copy(
                        image = "other_image"
                    )
                )
            )
        }

        runBlocking {
            repository.searchAmiibos(AmiiboSearchQuery.GameSeries(MOCK_QUERY)).first()
        } shouldBeEqualTo listOf(AMIIBO_MAPPED_FROM_DB.copy(image = "other_image"))

        verify {
            amiiboDAO.searchByGameSeries("%$MOCK_QUERY%")
        }
    }

    @Test
    fun `given a amiibo series search query when search amiibos is executed then it should call the right method`() {
        every { amiiboDAO.searchByAmiiboSeries("%$MOCK_QUERY%") } answers {
            flowOf(
                listOf(
                    DB_AMIIBO.copy(
                        amiiboSeries = "seriees"
                    )
                )
            )
        }

        runBlocking {
            repository.searchAmiibos(AmiiboSearchQuery.AmiiboSeries(MOCK_QUERY)).first()
        } shouldBeEqualTo listOf(AMIIBO_MAPPED_FROM_DB.copy(amiiboSeries = "seriees"))

        verify {
            amiiboDAO.searchByAmiiboSeries("%$MOCK_QUERY%")
        }
    }
}

private const val MOCK_QUERY = "query"
private val AMIIBO_MAPPED_FROM_DB = Amiibo(
    "11", "12", "13", "14",
    "15", "16", AmiiboReleaseDate("19", "20", "21", "22"), "17", "18"
)
private val AMIIBO = Amiibo(
    "1",
    "2",
    "3",
    "4",
    "5",
    "6",
    AmiiboReleaseDate("7", "8", "10", "9"),
    "11", "12"
)
private val DB_AMIIBO = DBAmiibo(
    "11", "12", "13", "14", "15", "16", "17", "18", DBAMiiboReleaseDate("19", "20", "21", "22")
)
private val API_AMIIBO =
    APIAmiibo(
        "1", "2", "3", "4", "5", "6",
        APIAmiiboReleaseDate(
            "7",
            "8",
            "9",
            "10"
        ),
        "11", "12"
    )

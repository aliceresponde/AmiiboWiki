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

package com.oscarg798.amiibowiki.core.di

import android.content.Context
import androidx.room.Room
import com.oscarg798.amiibowiki.core.CoroutineContextProvider
import com.oscarg798.amiibowiki.core.models.Config
import com.oscarg798.amiibowiki.core.network.services.AmiiboService
import com.oscarg798.amiibowiki.core.network.services.AmiiboTypeService
import com.oscarg798.amiibowiki.core.persistence.dao.AmiiboDAO
import com.oscarg798.amiibowiki.core.persistence.dao.AmiiboTypeDAO
import com.oscarg798.amiibowiki.core.persistence.database.CoreAmiiboDatabase
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import java.util.*

@Module
object CoreModule {

    @CoreScope
    @Provides
    fun provideLocale(): Locale = Locale.getDefault()

    @CoreScope
    @Provides
    fun provideCoroutineContextProvider(): CoroutineContextProvider =
        CoroutineContextProvider(Dispatchers.Main, Dispatchers.IO)

    @CoreScope
    @Provides
    fun provideHousesService(retrofit: Retrofit): AmiiboService =
        retrofit.create(AmiiboService::class.java)

    @CoreScope
    @Provides
    fun provideBaseUrl(config: Config) = config.baseUrl

    @CoreScope
    @Provides
    fun provideAmiiboTypeService(retrofit: Retrofit) =
        retrofit.create(AmiiboTypeService::class.java)

    @CoreScope
    @Provides
    fun provideDatabase(context: Context): CoreAmiiboDatabase {
      return  Room.databaseBuilder(
            context,
            CoreAmiiboDatabase::class.java, "core_amiibo_database"
        ).build()
    }

    @CoreScope
    @Provides
    fun provideAmiiboTypeDao(database: CoreAmiiboDatabase): AmiiboTypeDAO = database.amiiboTypeDAO()

    @CoreScope
    @Provides
    fun provideAmiiboDAO(database: CoreAmiiboDatabase): AmiiboDAO = database.amiiboDAO()
}

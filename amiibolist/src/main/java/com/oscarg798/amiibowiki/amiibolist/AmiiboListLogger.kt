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

package com.oscarg798.amiibowiki.amiibolist

import com.oscarg798.amiibowiki.logger.annotations.LogEventProperties
import com.oscarg798.amiibowiki.logger.annotations.LogSources
import com.oscarg798.amiibowiki.logger.annotations.LoggerDecorator
import com.oscarg798.amiibowiki.logger.annotations.ScreenShown
import com.oscarg798.amiibowiki.logger.annotations.WidgetClicked
import com.oscarg798.amiibowiki.logger.events.BUTTON_TYPE_NAME
import com.oscarg798.amiibowiki.logger.events.IMAGE_VIEW_TYPE_NAME
import com.oscarg798.amiibowiki.logger.sources.CRASHLYTICS_LOG_SOURCE_NAME
import com.oscarg798.amiibowiki.logger.sources.FIREBASE_LOG_SOURCE_NAME
import com.oscarg798.lomeno.sources.NETWORK_LOG_SOURCE_NAME

@LoggerDecorator
interface AmiiboListLogger {

    @LogSources([FIREBASE_LOG_SOURCE_NAME, NETWORK_LOG_SOURCE_NAME])
    @ScreenShown(AMIIBO_LIST_SCREEN_NAME)
    fun trackScreenViewed(@LogEventProperties culito: Map<String, String>)

    @WidgetClicked("amibito", BUTTON_TYPE_NAME)
    fun trackAmiiboCLicked()

    @WidgetClicked("amibito", BUTTON_TYPE_NAME)
    fun trackAmiiboCLicked(@LogEventProperties culito2: Map<String, String>)

    @ScreenShown(AMIIBO_LIST_SCREEN_NAME)
    fun trackScreenViewed()
}

private const val AMIIBO_ITEM_CLICK_WIDGET_NAME = "AMIIBO_ITEM"
private const val AMIIBO_LIST_SCREEN_NAME = "AMIIBO_LIST"

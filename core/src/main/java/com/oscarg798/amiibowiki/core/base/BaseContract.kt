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

package com.oscarg798.amiibowiki.core.base

import androidx.lifecycle.ViewModel
import com.oscarg798.amiibowiki.core.mvi.ViewState
import com.oscarg798.amiibowiki.network.exceptions.NetworkException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import okio.IOException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import com.oscarg798.amiibowiki.core.mvi.Result as MVIResult
import com.oscarg798.amiibowiki.core.mvi.Wish as MVIWish

@ExperimentalCoroutinesApi
abstract class AbstractViewModel<Wish : MVIWish, Result : MVIResult> : ViewModel() {

    protected val wishProcessor = ConflatedBroadcastChannel<Wish>()

    public abstract fun process(): Flow<ViewState<Result>>
    protected abstract fun initState(): ViewState<Result>


    protected val _state: MutableStateFlow<ViewState<Result>> =
        MutableStateFlow(initState())

    val state: StateFlow<ViewState<Result>>
        get() = _state

    fun onWish(wish: Wish) {
        wishProcessor.offer(wish)
    }
}

sealed class Failure(message: String?, cause: Exception? = null) : Exception(message, cause) {
    open class Recoverable(override val message: String?, override val cause: Exception?) :
        Failure(message, cause)
}


fun <R> Result<R>.getOrTransformNetworkException(
    exceptionMapper: ((NetworkException) -> Failure) = { throw it }
): R = getOrElse {
    throw  when (it) {
        is NetworkException.TimeOut,
        NetworkException.UnknowHost(it.message),
        NetworkException.Connection -> exceptionMapper(it as NetworkException)
        else -> it
    }
}

public inline fun <T, R> T.runCatchingNetworkException(
    noinline exceptionHandler: ((NetworkException) -> Result<R>)? = null,
    block: T.() -> R
): Result<R> {
    return try {
        Result.success(block())
    } catch (e: NetworkException) {
        if (exceptionHandler != null) {
            exceptionHandler(e)
        } else {
            Result.failure(e)
        }

    }
}

public inline fun <T> Result<T>.onException(action: (exception: Exception) -> Unit): Result<T> {
    exceptionOrNull()?.let {
        if (it !is Exception) {
            throw it
        }

        action(it)
    }
    return this
}


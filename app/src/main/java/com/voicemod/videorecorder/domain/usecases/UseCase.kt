package com.voicemod.videorecorder.domain.usecases

import com.voicemod.videorecorder.common.Transformer
import io.reactivex.Observable

abstract class UseCase<T>(private val transformer: Transformer<T>) {

    companion object {
        const val PARAM_USE_CASE_ENTITY = "param:operationEntity"
    }

    abstract fun createObservable(data: Map<String, Any>? = null): Observable<T>

    fun observable(withData: Map<String, Any>? = null): Observable<T> {
        return createObservable(withData).compose(transformer)
    }

}
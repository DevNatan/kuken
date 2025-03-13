package gg.kuken.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlin.reflect.KClass

public interface EventDispatcher : CoroutineScope {

    public fun dispatch(event: Any)

    public fun <T : Any> listen(eventType: KClass<T>): Flow<T>
}

public inline fun <reified T : Any> EventDispatcher.listen(): Flow<T> {
    return listen(T::class)
}

internal class EventDispatcherImpl :
    EventDispatcher,
    CoroutineScope by CoroutineScope(Dispatchers.IO + SupervisorJob()) {

    private val publisher = MutableSharedFlow<Any>(extraBufferCapacity = 1)

    override fun <T : Any> listen(eventType: KClass<T>): Flow<T> =
        publisher.filterIsInstance(eventType)

    override fun dispatch(event: Any) {
        publisher.tryEmit(event)
    }
}

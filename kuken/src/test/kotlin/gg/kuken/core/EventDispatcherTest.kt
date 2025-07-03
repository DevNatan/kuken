package gg.kuken.core

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private data class TestEvent<T>(val value: T)

@ExperimentalCoroutinesApi
internal class EventsDispatcherTest {

    @Test
    fun `listen to a publication of a primitive type`() = runTest {
        val eventsDispatcher: EventDispatcher = EventDispatcherImpl()
        val received = mutableListOf<Int>()

        eventsDispatcher.listen<Int>()
            .onEach(received::add)
            .launchIn(TestScope(UnconfinedTestDispatcher()))

        assertTrue(received.isEmpty())
        eventsDispatcher.dispatch(event = 3)

        assertEquals(expected = listOf(3), actual = received)
    }

    @Test
    fun `listen to a publication of a data class`() = runTest {
        val eventsDispatcher: EventDispatcher = EventDispatcherImpl()
        val received = mutableListOf<TestEvent<String>>()

        eventsDispatcher.listen<TestEvent<String>>()
            .onEach(received::add)
            .launchIn(TestScope(UnconfinedTestDispatcher()))

        assertTrue(received.isEmpty())
        eventsDispatcher.dispatch(event = TestEvent("abc"))

        assertEquals(listOf(element = TestEvent("abc")), received)
    }

    @Test
    fun `ignore publication of non-listened type`() = runTest {
        val eventsDispatcher: EventDispatcher = EventDispatcherImpl()
        val received = mutableListOf<String>()

        eventsDispatcher.listen<String>()
            .onEach(received::add)
            .launchIn(TestScope(UnconfinedTestDispatcher()))

        assertTrue(received.isEmpty())
        eventsDispatcher.dispatch(event = TestEvent("abc"))

        assertTrue(received.isEmpty())
    }
}

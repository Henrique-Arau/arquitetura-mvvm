package com.everis.listadecontatos

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.everis.listadecontatos.feature.listacontatos.model.ContatosVO
import com.everis.listadecontatos.feature.listacontatos.repository.ListaDeContatosRepository
import com.everis.listadecontatos.feature.listacontatos.viewmodel.ListaDeContatosViewModel
import com.everis.listadecontatos.helpers.HelperDB
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ViewModelTest {

    lateinit var repository: ListaDeContatosRepository
    lateinit var viewModel: ListaDeContatosViewModel
    @Before
    fun setUp() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        repository = ListaDeContatosRepository(
            HelperDB(appContext)

        )
    }

    fun setupMock() {
        repository = Mockito.mock(ListaDeContatosRepository::class.java)
        Mockito.`when`(repository.requestListaDeContatos(
            Mockito.anyString(),
            Mockito.any(),
            Mockito.any()
        )).thenAnswer {
            val onSucesso = it.arguments[1] as? ((List<ContatosVO>)->Unit)
            val lista = mutableListOf<ContatosVO>()
            lista.add(ContatosVO(1, "Teste unitario", "123456"))
            onSucesso?.invoke(lista)
            ""
        }
    }


    //Teste repository
    @Test
    fun viewModelTest() {
        setupMock()
        viewModel = ListaDeContatosViewModel (
            repository
                )
        var lista: List<ContatosVO>? = null
        val lock = CountDownLatch(1)
        viewModel.getListaDeContatos(
            busca = "",
            onSucesso = { listaResult ->
                lista = listaResult
                lock.countDown()
            }, onErro = { ex ->
                fail("Não deveria ter retornado erro")
                lock.countDown()
            }
        )
        lock.await("0000".toLong(), TimeUnit.MICROSECONDS)
        assertNotNull(lista)
        lista?.let { assertTrue(it.isNotEmpty()) }
        assertEquals(2, lista?.size)
    }
}


package io.rm.mvi.presenter

import io.rm.mvi.view.DataSource

/**
 * Может содержать
 * @param data - данные
 * @param transient - переходное состояние, устанавливается в null после отправки потребителю
 * @param error - ошибка, которую видит пользователь. При такой ошибке любая фоновая работа прекращается. transient
 * устанавливается в null. Error устанавливается в null пользователем, когда он скрывает диалог с ошибкой. Модуль не
 * может изменить состояние, пока пользователь не примет решения по ошибке.
 *
 * Представление обрабатывает состояние в порядке - error, transient, data
 *
 * when {
 *      state.error != null ->
 *      state.transient != null ->
 *      else ->
 * }
 *
 * Пример со списком
 * 1. Первый показ экрана -         CombinedState(data, null, null)
 * 2. Загрузка -                    CombinedState(data, Pending, null) - не обновляем показ данных т.к. есть transient
 * состояние, обрабатываем его
 * 3a. Ошибка -                     CombinedState(data, null, Error) - не обновляем показ данных т.к. есть Error,
 * показываем ошибку
 * 3b. Загрузка окончена            CombinedState(data, PendingEnd, null) - скрываем загрузку, не обновляем показ данных
 *                                  т.к. есть transient
 *                                  CombinedState(data, null, null) - обновляем показ данных
 *
 * При отвязке презентера от представления состояние приводится к
 *      - CombinedState(Data, null, null) - если фоновая работа есть и она прекращается
 *      - CombinedState(Data, Transient, null) - если фоновая работа есть и она не прекращается
 *      - CombinedState(Data, null, Error) - если есть ошибка
 *
 * После привязки презентера к вью, презентер должен
 *      - из своего последнего состояния создать сначала временное CombinedState(Data, null, null) и отправить его
 *      в представление
 *      - отправить свое состояние
 */
data class CombinedState<TypeDataState : State.Data>(
    val data: TypeDataState,
    val transient: Transient? = null,
    val error: Error? = null
) : State()

sealed class State {

    open class Data : State()

    open class Transient : State()

    open class Error(val throwable: Throwable) : State()

    open class Refresh : Transient()

    open class Pending : Transient()

    open class PendingEnd : Transient()

    open class Routing : Transient()

    open class CollectionItems<TypeItemModel>(
        val items: MutableList<TypeItemModel>,
        var isEnd: Boolean
    ) : Data(), DataSource<TypeItemModel> {
        override val itemsCount: Int
            get() = this.items.size

        override fun getItemAtPosition(position: Int): TypeItemModel {
            return this.items[position]
        }
    }
}
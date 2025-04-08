package com.jbc.roomdb.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbc.roomdb.data.Contact
import com.jbc.roomdb.db.ContactDAO
import com.jbc.roomdb.data.ContactEvent
import com.jbc.roomdb.data.ContactState
import com.jbc.roomdb.data.SortType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ContactViewModel(
    private val dao: ContactDAO
): ViewModel() {

    private val _sortType = MutableStateFlow(SortType.FIRST_NAME)

    /*
        = Traduccion del ingles, puede no ser correcta =

        La razon por la que hacemos esto es, si miramos flatMapLatest, recibe un flow (nuestro
        sortType) y en el momento en el que este cambia (le damos a otro radio button para ordenar
        por lastName por ejemplo) la emision se transforma en un nuevo flow, y en este caso es uno de
        los flows que viene de nuestra db (dao.getContactsOrdered ...)

        De esta forma tan pronto como cambie nuestro tipo de sort, automaticamente cambiaremos la forma
        de donde obtenemos los contactos.
     */
    private val _contacts = _sortType
        .flatMapLatest { sortType ->
            when(sortType) {
                SortType.FIRST_NAME -> dao.getContactsOrderedByFirstName()
                SortType.LAST_NAME -> dao.getContactsOrderedByLastName()
                SortType.PHONE_NUMBER -> dao.getContactsOrderedByPhoneNumber()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(ContactState())

    /*
        = Traduccion del ingles, puede no ser correcta =

        Con combine, podemos combinar los tres Flows en solo uno. Cuando uno de esos Flows emite
        un nuevo valor, el codigo entre llaves se ejecuta (Si hay un cambio en el sortType, actualizamos
        el sortType de nuestro ContactState como dice abajo sortType = sortType. Lo mismo para los+
        contactos)
     */
    val state = combine(_state, _sortType, _contacts) { state, sortType, contacts ->
        state.copy(
            contacts = contacts,
            sortType = sortType,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ContactState())

    fun onEvent(event: ContactEvent) {
        when(event) {
            is ContactEvent.DeleteContact -> {
                viewModelScope.launch { dao.deleteContact(event.contact) }
            }

            ContactEvent.HideDialog -> {
                _state.update { it.copy(
                    isAddingContact = false
                ) }
            }

            ContactEvent.SaveContact -> {
                val firstName = state.value.firstName
                val lastName = state.value.lastName
                val phoneNumber = state.value.phoneNumber

                if(firstName.isBlank() || lastName.isBlank() || phoneNumber.isBlank()) { return }

                val contact = Contact(
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber
                )

                viewModelScope.launch { dao.upsertContact(contact) }

                _state.update { it.copy(
                    isAddingContact = false,
                    firstName = "",
                    lastName = "",
                    phoneNumber = "",
                ) }
            }

            is ContactEvent.SetFirstName -> {
                _state.update { it.copy(
                    firstName = event.firstName
                ) }
            }

            is ContactEvent.SetLastName -> {
                _state.update { it.copy(
                    lastName = event.lastName
                ) }
            }
            is ContactEvent.SetPhoneNumber -> {
                _state.update { it.copy(
                    phoneNumber = event.phoneNumber
                ) }
            }
            ContactEvent.ShowDialog -> {
                _state.update { it.copy(
                    isAddingContact = true
                ) }
            }
            is ContactEvent.SortContacts -> {
                _sortType.value = event.sortType
            }
        }
    }

}
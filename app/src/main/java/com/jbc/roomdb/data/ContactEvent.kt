package com.jbc.roomdb.data

/*
    Esta clase contiene varios eventos, interacciones del usuario con la app (ej, click en el
    floating action button, click en el boton de borrar). Las interacciones que sean posibles por
    parte del usuario en nuestra app estaran representadas por los eventos que esten en esta
    interfaz
 */

sealed interface ContactEvent {

    // Guardar un contacto con los datos que el usuario ha usado en los TextFields
    object SaveContact: ContactEvent

    data class SetFirstName(val firstName: String): ContactEvent
    data class SetLastName(val lastName: String): ContactEvent
    data class SetPhoneNumber(val phoneNumber: String): ContactEvent

    object ShowDialog: ContactEvent
    object HideDialog: ContactEvent

    // Ordenar los contactos en la lista
    data class SortContacts(val sortType: SortType): ContactEvent

    // Eliminar el contacto seleccionado
    data class DeleteContact(val contact: Contact): ContactEvent
}
package com.jbc.roomdb.data

/*
    Esta clase contiene los estados que tendra nuestra pantalla (la lista de contactos, los radio
    buttons para ordenar la lista, mostrar o esconder el dialog, ...)
 */
data class ContactState(
    val contacts: List<Contact> = emptyList(),
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val isAddingContact: Boolean = false,
    val sortType: SortType = SortType.FIRST_NAME

)

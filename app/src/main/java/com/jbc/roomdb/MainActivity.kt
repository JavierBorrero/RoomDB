package com.jbc.roomdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.jbc.roomdb.db.ContactDatabase
import com.jbc.roomdb.presentation.ContactScreen
import com.jbc.roomdb.presentation.ContactViewModel
import com.jbc.roomdb.ui.theme.RoomDBTheme

class MainActivity : ComponentActivity() {

    /*
        Lo correcto seria usar inyeccion de dependencias, al ser un proyecto pequeño solo para
        aprender Room no voy a usar inyeccion de dependencias, aunque esto no sea correcto.
     */
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            ContactDatabase::class.java,
            name="contacts.db"
        ).build()
    }

    private val viewModel by viewModels<ContactViewModel>(
        factoryProducer = {
            object: ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ContactViewModel(db.dao) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoomDBTheme {

                val state by viewModel.state.collectAsState()

                ContactScreen(
                    state = state,
                    onEvent = viewModel::onEvent
                )
            }
        }
    }
}
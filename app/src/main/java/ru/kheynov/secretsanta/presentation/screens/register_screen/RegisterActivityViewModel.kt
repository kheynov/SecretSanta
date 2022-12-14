package ru.kheynov.secretsanta.presentation.screens.register_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.kheynov.secretsanta.domain.entities.RegisterUser
import ru.kheynov.secretsanta.domain.use_cases.users.UsersUseCases
import ru.kheynov.secretsanta.utils.Resource
import ru.kheynov.secretsanta.utils.SantaException
import ru.kheynov.secretsanta.utils.UiText
import javax.inject.Inject

@HiltViewModel
class RegisterActivityViewModel @Inject constructor(
    private val useCases: UsersUseCases,
    firebaseAuth: FirebaseAuth,
) : ViewModel() {
    
    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state
    
    sealed interface State {
        object Idle : State
        object Loading : State
        data class Error(val error: Exception) : State
    }
    
    val username = firebaseAuth.currentUser?.displayName ?: firebaseAuth.currentUser?.email
    
    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()
    
    sealed interface Action {
        data class ShowError(val error: UiText) : Action
        object RouteToMain : Action
    }
    
    private val ioDispatcher = Dispatchers.IO
    
    fun registerUser(user: RegisterUser) {
        viewModelScope.launch {
            _state.value = State.Loading
            val res = withContext(ioDispatcher) {
                useCases.registerUserUseCase(user)
            }
            when (res) {
                is Resource.Failure -> {
                    if (res.exception is SantaException || res.exception is HttpException) {
                        _state.value = State.Error(res.exception)
                    } else {
                        _state.value = State.Idle
                        _actions.send(
                            Action.ShowError(
                                UiText.PlainText(
                                    res.exception.message.toString()
                                )
                            )
                        )
                    }
                }
                is Resource.Success -> {
                    _actions.send(Action.RouteToMain)
                }
            }
        }
    }
}
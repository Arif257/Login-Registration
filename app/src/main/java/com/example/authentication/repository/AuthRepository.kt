package com.example.authentication.repository

import com.example.authentication.data.LoginBody
import com.example.authentication.data.RegisterBody
import com.example.authentication.data.UniqueEmailValidationResponse
import com.example.authentication.data.ValidateEmailBody
import com.example.authentication.utils.APIConsumer
import com.example.authentication.utils.RequestStatus
import com.example.authentication.utils.SimplifiedMessage
import kotlinx.coroutines.flow.Flow

import kotlinx.coroutines.flow.flow

class AuthRepository(private val consumer: APIConsumer) {
    fun validateEmailAddress(body: ValidateEmailBody): Flow<RequestStatus<UniqueEmailValidationResponse>> =
        flow {

            emit(RequestStatus.Waiting)
            val response = consumer.validateEmailAddress(body)
            if (response.isSuccessful) {
                emit((RequestStatus.Success(response.body()!!)))
            } else {
                emit(
                    RequestStatus.Error(
                        SimplifiedMessage.get(
                            response.errorBody()!!.byteStream().reader().readText()
                        )
                    )
                )
            }

        }

    fun registerUser(body: RegisterBody) =
        flow {
            emit(RequestStatus.Waiting)
            val response = consumer.registerUser(body)
            if (response.isSuccessful) {
                emit((RequestStatus.Success(response.body()!!)))
            } else {
                emit(
                    RequestStatus.Error(
                        SimplifiedMessage.get(
                            response.errorBody()!!.byteStream().reader().readText()
                        )
                    )
                )
            }

        }
    fun loginUser(body: LoginBody) =
        flow {
            emit(RequestStatus.Waiting)
            val response = consumer.loginUser(body)
            if (response.isSuccessful) {
                emit((RequestStatus.Success(response.body()!!)))
            } else {
                emit(
                    RequestStatus.Error(
                        SimplifiedMessage.get(
                            response.errorBody()!!.byteStream().reader().readText()
                        )
                    )
                )
            }

        }
}
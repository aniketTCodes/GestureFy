package com.anikettcodes.gesturefy.data.datasource.local

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.anikettcodes.gesturefy.datastore.AuthorizationPreference
import com.google.protobuf.InvalidProtocolBufferException
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/*
Serializer for PROTO DATASTORE
 */

object AuthorizationPreferenceSerializer : Serializer<AuthorizationPreference> {
    override val defaultValue: AuthorizationPreference
        get() = AuthorizationPreference.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): AuthorizationPreference {
        try{
            return AuthorizationPreference.parseFrom(input)
        } catch (e: InvalidProtocolBufferException){
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: AuthorizationPreference, output: OutputStream) = t.writeTo(output)

}
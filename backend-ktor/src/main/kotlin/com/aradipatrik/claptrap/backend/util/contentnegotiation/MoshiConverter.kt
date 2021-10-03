package com.aradipatrik.claptrap.backend.util.contentnegotiation

import com.squareup.moshi.Moshi
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.ContentConverter
import io.ktor.features.ContentNegotiation
import io.ktor.features.suitableCharset
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import io.ktor.http.withCharset
import io.ktor.request.ApplicationReceiveRequest
import io.ktor.util.pipeline.PipelineContext
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.Okio
import kotlin.reflect.jvm.jvmErasure

/**
 * Moshi converter for [ContentNegotiation] feature
 */
@Suppress("BlockingMethodInNonBlockingContext")
class MoshiConverter(private val moshi: Moshi = Moshi.Builder().build()) : ContentConverter {
  override suspend fun convertForSend(
    context: PipelineContext<Any, ApplicationCall>,
    contentType: ContentType,
    value: Any
  ): Any? = TextContent(
    moshi.adapter(value.javaClass).toJson(value),
    contentType.withCharset(context.call.suitableCharset())
  )

  override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any? {
    val request = context.subject
    val channel = request.value as? ByteReadChannel ?: return null
    val type = request.typeInfo
    val javaType = type.jvmErasure

    return withContext(Dispatchers.IO) {
      val buffer = Okio.buffer(Okio.source(channel.toInputStream()))
      moshi.adapter(javaType.javaObjectType).fromJson(buffer)
    }
  }
}

/**
 * Register Moshi to [ContentNegotiation] feature
 */
fun ContentNegotiation.Configuration.moshi(
  contentType: ContentType = ContentType.Application.Json,
  block: Moshi.Builder.() -> Unit = {}
) {
  val builder = Moshi.Builder()
  builder.apply(block)
  val converter = MoshiConverter(builder.build())
  register(contentType, converter)
}

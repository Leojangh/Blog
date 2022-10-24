package com.genlz.blog

import com.genlz.blog.pojo.AuthorizedUser
import com.genlz.blog.pojo.Guest
import com.genlz.blog.pojo.User
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.GsonHttpMessageConverter
import org.springframework.stereotype.Component
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.time.Instant
import java.util.*
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.javaType


@Configuration
@ComponentScan
class GsonConfig {

    @Bean
    fun customConverters() = HttpMessageConverters(
        true,
        setOf(GsonHttpMessageConverter())
    )

    @Bean
    fun customGson(
        builder: GsonBuilder,
        @Qualifier("gsonAdapters")
        gsonAdapters: Collection<*>
    ): Gson = builder.apply {
        setExclusionStrategies(ExcludeStrategy)
        for (typeAdapter in gsonAdapters) typeAdapter?.let {
            val superclass = with(it.javaClass.genericSuperclass) {
                //TypeAdapter
                if (this is ParameterizedType) this
                //JsonSerializer/JsonDeserializer,the hard code is error-prone.
                else it.javaClass.kotlin.supertypes[/*hard code*/0].javaType
            }
            if (superclass is ParameterizedType) {
                val type = superclass.actualTypeArguments[0]
                registerTypeHierarchyAdapter(type as Class<*>, it)
            }
        }
        setPrettyPrinting()//for debugging
    }.create()

    @Bean
    @Qualifier("gsonAdapters")
    fun provideGsonTypeAdapters(
        context: ApplicationContext
    ): Collection<*> = context.getBeansWithAnnotation(GsonAdapter::class.java).values
}

@GsonAdapter
private class InstantTypeAdapter : TypeAdapter<Instant>() {
    override fun write(out: JsonWriter, value: Instant) {
        out.value("${value.epochSecond}#${value.nano}")
    }

    override fun read(reader: JsonReader): Instant = reader.nextString().split('#').run {
        Instant.ofEpochSecond(get(0).toLong(), get(1).toLong())
    }
}

/**
 * It's hard to scale.
 */
@GsonAdapter
private object GuestJsonDeserializer : JsonDeserializer<Guest> {
    override fun deserialize(
        json: JsonElement, typeOfT: Type, context: JsonDeserializationContext
    ) = with(json.asJsonObject) {
        Guest(get("username").asString, get("email").asString)
    }
}

/**
 * No scalable and error-prone.
 */
//@GsonAdapter
private object UserDeserializer : JsonDeserializer<User> {
    override fun deserialize(
        json: JsonElement, typeOfT: Type, context: JsonDeserializationContext
    ): User {
        @Suppress("UNCHECKED_CAST")
        val kClass = (typeOfT as Class<out User>).kotlin
        val fakeConstructor = kClass.companionObject?.memberFunctions?.first {
            it.name == "invoke" || it.name == kClass.simpleName
        } ?: error("Expected fake constructor named 'invoke' or '${kClass.simpleName}' not found!")
        val itr = fakeConstructor.parameters.listIterator(1)
        return with(json.asJsonObject) {
            val params = mutableListOf<Any>()
            itr.forEach/*map*/ {
                val paramType = it.type.javaType
                if ((paramType as? Class<*>)?.isPrimitive == true || paramType == String::class.java || paramType == Number::class.java) {
                    paramType as Class<*>
                    val m = "getAs${
                        paramType.simpleName.replaceFirstChar { c -> if (c.isLowerCase()) c.titlecase(Locale.getDefault()) else c.toString() }
                    }"
                    val asXX = JsonElement::class.java.getMethod(m)
                    params.add(/*get(it.name).asXX*/asXX(get(it.name)))
                } else {
                    //other reference type.
                    val jsonObject = getAsJsonObject(it.name) ?: getAsJsonObject("times")[it.name]
                    val obj = context.deserialize<Any>(jsonObject, paramType)
                    params.add(obj)
                }
            }
            fakeConstructor.call(kClass.companionObjectInstance, *params.toTypedArray()) as User
        }
    }
}

@GsonAdapter
private object AuthorizedUserJsonDeserializer : JsonDeserializer<AuthorizedUser> {
    override fun deserialize(
        json: JsonElement, typeOfT: Type, context: JsonDeserializationContext
    ) = with(json.asJsonObject) {
        val times = get("times").asJsonObject
        val id = get("id").asLong
        val username = get("username").asString
        val email = get("email").asString
        val createTime: Instant = requireNotNull(context.deserialize(times["createTime"], Instant::class.java))
        val modifyTime: MutableList<Instant> = requireNotNull(
            context.deserialize(
                times["modifyTime"].asJsonArray,
                object : TypeToken<MutableList<Instant>>() {}.type
            )
        )

        @Suppress("UNCHECKED_CAST")
        val kClass = (typeOfT as Class<out AuthorizedUser>).kotlin
        val fakeConstructor = kClass.companionObject?.memberFunctions?.first {
            it.name == "invoke" || it.name == kClass.simpleName
        } ?: error("Expected fake constructor named 'invoke' or '${kClass.simpleName}' not found!")
        fakeConstructor.call(
            kClass.companionObjectInstance,
            id,
            username,
            email,
            createTime,
            modifyTime
        ) as AuthorizedUser
    }
}

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class GsonExclude

@Component
@Target(AnnotationTarget.CLASS)
annotation class GsonAdapter

private object ExcludeStrategy : ExclusionStrategy {

    override fun shouldSkipField(f: FieldAttributes): Boolean {
        return f.getAnnotation(GsonExclude::class.java) != null
    }

    override fun shouldSkipClass(clazz: Class<*>?): Boolean = false
}


package ru.netology.lesson9

import java.lang.RuntimeException
import java.util.*

class Chat(
    val           chatId   : Int,
    val           userId1  : UserId,     // - id первого пользователя
    val           userId2  : UserId,     // - id второго пользователя
    private var autoIncId  : Int = 0, // - счетчик автоинкремента id
                             //контейнер для хранения сообщений чата
    private var chatData   : SortedMap<UserId, ChatMessage> = sortedMapOf<UserId,ChatMessage>()

) {
    private fun newId() = ++autoIncId

    /**
     * Добавляет сообщение в чат
     * возвращает true в случае успеха
     */
    fun add(senderId : UserId, text : String) : Boolean
    {
        val id  = newId()
        chatData[id] = ChatMessage(id = id, userId = senderId, text = text)
        return true
    }

    /**
     * Редактирование сообщения
     * возвращает true в случае успеха
     */
    fun edit(id : Int, newText : String) : Boolean
    {
        chatData[id].apply {
            this?.let {
                if (it.deletedState) throw CannotEditDeletedMessage(id)
                chatData.put(id, it.copy(text = newText, readState = false, deletedState = false))
            } ?: throw MessageNotFoundException(id)
        }
        return true
    }

    /**
     * удаление сообщений
     */
    fun delete(id : Int) : Boolean
    {
        chatData[id].apply {
            this?.let {
                chatData.put(id, it.copy(deletedState = true))
            } ?: throw MessageNotFoundException(id)
        }
        if (chatData.all { it.value.deletedState })
        {
            throw ChatIsEmptyException()
        }
        return true
    }

    /**
     * Функция помечает сообщения прочитанными
     * начаная с lastReadId, но не более  count
     */
    fun getMessages(lastReadId : Int, count : Int) : List<ChatMessage>
    {
        val maxRead = lastReadId + count
        val msgList = mutableListOf<ChatMessage>()
        chatData = chatData.mapValues{
            if (it.value.id in (lastReadId + 1) until maxRead) {
                val msg = it.value.copy(readState = true)
                msgList += msg
                msg
            } else {
                it.value
            }
        }.toSortedMap()
        return msgList
    }

    fun last() : ChatMessage?{
        return try{
            chatData.values.last()
        } catch (e : NoSuchElementException) {
            null
        }
    }
}

class MessageNotFoundException(id : Int) : RuntimeException("Сообщение с id $id отсутствует в чате")

class CannotEditDeletedMessage(id : Int) :
    RuntimeException("Невозможно отредактировать сообщение с id $id так как оно удалено")

class ChatIsEmptyException() : RuntimeException()


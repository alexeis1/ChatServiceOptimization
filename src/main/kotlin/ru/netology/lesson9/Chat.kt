package ru.netology.lesson9

import java.lang.RuntimeException

class Chat(
    val           userId1  : Int,     // - id первого пользователя
    val           userId2  : Int,     // - id второго пользователя
    private var autoIncId  : Int = 0, // - счетчик автоинкремента id
                             //контейнер для хранения сообщений чата
    private var chatData   : MutableMap<Int, ChatMessage> = mutableMapOf<Int,ChatMessage>()

) {
    private fun newId() = ++autoIncId

    /**
     * Добавляет сообщение в чат
     * возвращает true в случае успеха
     */
    fun add(senderId : Int, text : String) : Boolean
    {
        val id  = newId()
        chatData.put(id, ChatMessage(id = id, userId = senderId, text = text))
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
     * начаная с lastReadId, но не более
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
        }.toMutableMap()
        return msgList
    }
}

class MessageNotFoundException(id : Int) : RuntimeException("Сообщение с id $id отсутствует в чате")

class CannotEditDeletedMessage(id : Int) :
    RuntimeException("Невозможно отредактировать сообщение с id $id так как оно удалено")

class ChatIsEmptyException() : RuntimeException()


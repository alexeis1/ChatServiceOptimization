package ru.netology.lesson9

import java.lang.RuntimeException
import java.util.*

class Chat(
    val           chatId   : Int,
    val           userId1  : UserId,     // - id первого пользователя
    val           userId2  : UserId,     // - id второго пользователя
    private var autoIncId  : MsgId = 0, // - счетчик автоинкремента id
                             //контейнер для хранения сообщений чата
    private var chatData   : SortedMap<MsgId, ChatMessage> = sortedMapOf<MsgId, ChatMessage>()

) {
    private fun newId() = ++autoIncId

    /**
     * Добавляет сообщение в чат
     * возвращает сообщение
     */
    fun add(senderId : UserId, text : String) : ChatMessage
    {
        val id  = newId()
        val msg = ChatMessage(id = id, userId = senderId, text = text)
        chatData[id] = msg
        return msg
    }

    /**
     * Редактирование сообщения
     * возвращает true в случае успеха
     */
    fun edit(id : Int, newText : String) : Boolean
    {
        chatData[id]?.apply {
            if (this.deletedState) throw CannotEditDeletedMessage(id)
            chatData[id] = this.copy(text = newText, readState = false, deletedState = false)
        }?: throw MessageNotFoundException(id)
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
     * начаная с lastReadId, но не более count
     * если lastReadId будет равен нулю то просматривать будем все
     */
    fun getMessages(lastReadId : MsgId = 0, count : Int = autoIncId) : List<ChatMessage>
    {
        val maxRead = lastReadId + count + 1
        val msgList = mutableListOf<ChatMessage>()
        chatData = chatData.mapValues{
            //сообщение будет или прочитано или скопировано как есть
            //в новый мап
            if (it.key in (lastReadId + 1) until maxRead) {
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
        return chatData.values.findLast { !it.deletedState }
    }
}

class MessageNotFoundException(id : Int) : RuntimeException("Сообщение с id $id отсутствует в чате")

class CannotEditDeletedMessage(id : Int) :
    RuntimeException("Невозможно отредактировать сообщение с id $id так как оно удалено")

class ChatIsEmptyException() : RuntimeException()


package ru.netology.lesson10

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
     * Функция получает сообщения из чата помечает их прочитанными
     * начаная с lastReadId, но не более  count
     * если lastReadId будет равен нулю то просматривать будем все
     */
    fun getMessages(receiverId : UserId, lastReadId : MsgId = 0, count : Int = autoIncId)
        : List<ChatMessage>
    {
        val maxRead = lastReadId + count + 1
        val msgList = mutableListOf<ChatMessage>()
                           //помечаем некоторые сообщение прочитанными
        chatData = chatData.mapValues{
            //диапазон можно вычислить так как сообщения физически не удаляются
            //а возвращаем мы полный список сообщений, в том числе удаленных
            if (it.key in (lastReadId + 1) until maxRead) {
                // помечаем прочитанными только сообщения не от receiverId (т.е. для receiverId)
                val msg = if (it.value.userId != receiverId) {
                              it.value.copy(readState = true)
                          } else {
                              it.value
                          }
                msgList += msg  //заполняем результирующий список
                msg  //помещаем или прочтенное или исходное сообщение
            } else {  //значения вне диапазона
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


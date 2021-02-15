package ru.netology.lesson9

import java.lang.RuntimeException
import kotlin.math.*

/**
 * Класс для управления чатами
 */

class ChatService {
    //контейнер для всех чатов.
    private val chatMap     = mutableMapOf<UserId, MutableMap<UserId, Chat>>()
    private var idGenerator = 0 //счетчик автоинкремента id
    private fun genChatId(): Int {return ++idGenerator }

    //количество записей пользователей с чатами
    fun userRecordsCount() = chatMap.size

    //количество созданных записей для чатов (в 2 раза больше чем чатов)
    fun chatRecsCount() : Int
    {
        return chatMap.values.fold(0) { acc, mutableMap ->
            acc + mutableMap.values.fold(0){acc2, _ -> acc2 + 1}
        }
    }
    /**
     * создает новый пустой чат
     */
    private fun createChat(senderId : UserId, receiverId : UserId) : Chat
    {
        val chat = Chat(chatId = genChatId(), userId1 = senderId, userId2 = receiverId)
        //добавляем 2е записи с чатом для каждого из пользователей или предварительно
        //создаем аккаунты для каждого из пользователей и добавляем туда записии с чатом
        chatMap[senderId]?.apply{ this[receiverId] = chat } ?:
            chatMap.put(senderId, mutableMapOf(Pair(receiverId, chat)))
        chatMap[receiverId]?.apply{ this[senderId] = chat }?:
            chatMap.put(receiverId, mutableMapOf(Pair(senderId, chat)))
        return chat
    }

    /**
     * удаляем записи чата у обоих пользователей
     */
    private fun deleteChatPrivate(senderId : UserId, receiverId : UserId)
    {   //удаляем обе записи из мапы
        chatMap[senderId]?.apply  { this.remove(receiverId) } ?: throw UserNotFoundException(senderId)
        chatMap[receiverId]?.apply{ this.remove(senderId)   } ?: throw UserNotFoundException(senderId)
    }
    /**
     * Функция создает новое сообщение, или новый чат с сообщением
     */
    fun addNewMessage(senderId : UserId, receiverId : UserId, text : String) {
        //добавляем сообщение или в существующий чат или в новый
        chatMap[senderId]?.let{it[receiverId]?.add(senderId = senderId, text = text)} ?:
            createChat(senderId, receiverId).add(senderId = senderId, text = text)
    }

    /**
     * Получить информацию о количестве непрочитанных чатов
     * это количество чатов, в каждом из которых есть хотя бы одно непрочитанное сообщение
     */
    fun getUnreadChatsCount(receiverId : UserId) : Int
    {
        var result = 0
        chatMap[receiverId]?.forEach() {
            if (it.value.last()?.readState == false)
            {
                result++
            }
        }
        return result
    }

    /**
     * Споисок сообщений, по одному последнему из каждого чата
     */
    fun getChats(receiverId : UserId) : List<ChatPreview> {
        val chatMap = chatMap[receiverId]
        return chatMap?.map {      //конвертируем мап в список
            it.value.last()?.let { //если последнее сообщение есть делаем запись
                    it1 -> ChatPreview(chatId = it.value.chatId, lastMsg = it1)
            } ?:
            ChatPreview(           //пустая запись
                chatId = it.value.chatId,
                lastMsg = ChatMessage(id = 0, userId = receiverId, text = "пусто")
            ) } ?: emptyList()
    }

    /**
     * Функция помечает сообщения прочитанными
     * начаная с lastReadId, но не более  count
     */
    fun getMessages(receiverId : UserId, chatId : Int, lastReadId : Int, count : Int) : List<ChatMessage>
    {
        chatMap[receiverId]?.forEach{ chat ->
                if (chat.value.chatId == chatId) {
                    return chat.value.getMessages(lastReadId, count)
                }
            } ?: throw UserNotFoundException(receiverId)
        return emptyList()
    }

    /**
     * Функция удаляет чат
     */
    fun deleteChat(receiverId : UserId, chatId : Int)
    {
        chatMap[receiverId]?.forEach{ chat ->
            if (chat.value.chatId == chatId) {
                deleteChatPrivate(chat.value.userId1, chat.value.userId2)
            }
        } ?: throw UserNotFoundException(receiverId)
    }

    /**
     * функия удаляет сообщение. Если чат остается пустой то функция delete герерит
     * исключение ChatIsEmptyException
     */
    fun deleteMessage(receiverId : UserId, msgId : Int)
    {
        chatMap[receiverId]?.forEach{ chat ->
            try {
                chat.value.delete(msgId)
            } catch (e : ChatIsEmptyException) {
                deleteChatPrivate(chat.value.userId1, chat.value.userId2)
            }
        } ?: throw UserNotFoundException(receiverId)
    }
}

/**
 * запись для превью чата
 */
data class ChatPreview(
    val chatId : Int,
    val lastMsg : ChatMessage
)

class UserNotFoundException(userId : UserId) : RuntimeException("Пользователь с id=$userId не наден")
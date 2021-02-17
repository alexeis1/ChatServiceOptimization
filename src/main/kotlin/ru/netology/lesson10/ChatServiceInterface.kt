package ru.netology.lesson10

/**
 * интерфейс для управления чатами
 */

interface ChatServiceInterface{

    /**
     * Функция создает новое сообщение, или новый чат с сообщением
     */
    fun addNewMessage(senderId : UserId, receiverId : UserId, text : String)

    /**
     * Получить информацию о количестве непрочитанных чатов
     * это количество чатов, в каждом из которых есть хотя бы одно непрочитанное сообщение
     */
    fun getUnreadChatsCount(receiverId : UserId) : Int

    /**
     * Споисок сообщений, по одному последнему из каждого чата
     */
    fun getChats(receiverId : UserId) : List<ChatPreview>

    /**
     * Функция получает сообщения из чата помечает их прочитанными
     * начаная с lastReadId, но не более  count
     */
    fun getMessages(receiverId : UserId, chatId : Int, lastReadId : Int = 0, count : Int = 20)
            : List<ChatMessage>

    /**
     * Функция удаляет чат
     */
    fun deleteChat(receiverId : UserId, chatId : Int)

    /**
     * функия удаляет сообщение. Если чат остается пустой то функция deleteMessage
     * упдаляет целиком чат
     */
    fun deleteMessage(receiverId : UserId, chatId : Int, msgId : Int)
}

/**
 * запись для превью чата
 */
data class ChatPreview(
    val chatId : Int,
    val lastMsg : ChatMessage
)

class UserNotFoundException(userId : UserId) : RuntimeException("Пользователь с id=$userId не наден")

class ChatNotFoundException(chatId : Int) : RuntimeException("Чат с id=$chatId не наден")
package ru.netology.lesson10

class ChatService : ChatServiceInterface{
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
        chatMap[senderId]?.  remove(receiverId) ?: throw UserNotFoundException(senderId)
        chatMap[receiverId]?.remove(senderId)   ?: throw UserNotFoundException(senderId)
    }
    /**
     * Функция создает новое сообщение, или новый чат с сообщением
     */
    override fun addNewMessage(senderId : UserId, receiverId : UserId, text : String) {
        //добавляем сообщение или в существующий чат или в новый
        chatMap[senderId]?.let{it[receiverId]?.add(senderId = senderId, text = text)} ?:
            createChat(senderId, receiverId).add(senderId = senderId, text = text)
    }

    /**
     * Получить информацию о количестве непрочитанных чатов
     * это количество чатов, в каждом из которых есть хотя бы одно непрочитанное сообщение
     */
    override fun getUnreadChatsCount(receiverId : UserId) : Int
    {
        var result = 0
        chatMap[receiverId]?.forEach {
            //если последнее сообщение в чате не прочитанное
            if (it.value.last()?.readState == false &&
                //и оно не от меня, то оно не прочитанное
                it.value.last()?.userId    != receiverId)
            {
                result++
            }  //исключение если пользователь неверный
        } ?: throw UserNotFoundException(receiverId)
        return result
    }

    /**
     * Споисок сообщений, по одному последнему из каждого чата
     */
    override fun getChats(receiverId : UserId) : List<ChatPreview> {
        val chatMap = chatMap[receiverId]
        return chatMap?.map {      //конвертируем мап в список
            it.value.last()?.let { //если последнее сообщение есть, то добавляем результат
                    it1 -> ChatPreview(chatId = it.value.chatId, lastMsg = it1)
            } ?:
            ChatPreview(           //пустая запись
                chatId = it.value.chatId,
                lastMsg = ChatMessage(id = 0, userId = receiverId, text = "пусто")
            )  //исключение если пользователь неверный
        } ?: throw UserNotFoundException(receiverId)
    }

    /**
     * Функция получает сообщения из чата помечает их прочитанными
     * начаная с lastReadId, но не более  count
     */
    override fun getMessages(receiverId : UserId, chatId : Int, lastReadId : Int, count : Int)
        : List<ChatMessage>
    {
        chatMap[receiverId]?.forEach{ chat ->
                if (chat.value.chatId == chatId) {
                    return chat.value.getMessages(receiverId = receiverId, lastReadId = lastReadId, count = count)
                }   //исключение если пользователь неверный
            } ?: throw UserNotFoundException(receiverId)
        //исключение если чат не найден
        throw ChatNotFoundException(chatId)
    }

    /**
     * Функция удаляет чат
     */
    override fun deleteChat(receiverId : UserId, chatId : Int)
    {      //у пользователя находим чат по chatId
        chatMap[receiverId]?.let { userChats->
            userChats.values.firstOrNull { it.chatId == chatId }?.
            let {  chat->  //удаляем обе записи чата для обоих аккаунтов
                deleteChatPrivate(senderId = chat.userId1, receiverId = chat.userId2)
            } ?: throw ChatNotFoundException(chatId)
        } ?: throw UserNotFoundException(receiverId)
    }

    /**
     * функия удаляет сообщение. Если чат остается пустой то функция deleteMessage
     * упдаляет целиком чат
     */
    override fun deleteMessage(receiverId : UserId, chatId : Int, msgId : Int)
    {     //у пользователя находим чат по chatId
        chatMap[receiverId]?.let { userChats->
            userChats.values.firstOrNull { it.chatId == chatId }?.
            let {  chat->
                try{ //удаляем сообщение
                    chat.delete(id = msgId)
                } catch (e : ChatIsEmptyException) {//если сообщение посоеднее то и чат тоже
                    deleteChatPrivate(senderId = chat.userId1, receiverId = chat.userId2)
                }
            } ?: throw ChatNotFoundException(chatId)
        } ?: throw UserNotFoundException(receiverId)
    }
}

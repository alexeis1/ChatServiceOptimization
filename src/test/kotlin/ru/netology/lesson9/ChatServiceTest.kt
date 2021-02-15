package ru.netology.lesson9

import org.junit.Assert.*
import org.junit.Test

class ChatServiceTest {

    @Test
    fun addNewMessage_succeeded() {
        val service = ChatService()
        service.addNewMessage(senderId = 1, receiverId = 2, text = "message")
        assertEquals(service.userRecordsCount(), 2)
        assertEquals(service.chatRecsCount(), 2)
    }

    @Test
    fun addNewMessage_addInExistedChatSucceeded() {
        val service = ChatService()
        service.addNewMessage(senderId = 1, receiverId = 2, text = "message")
        service.addNewMessage(senderId = 1, receiverId = 2, text = "message 2")

        assertEquals(service.userRecordsCount(), 2)
        assertEquals(service.chatRecsCount(), 2)
    }

    @Test
    fun addNewMessage_addNewChatInExistedUserRecordSucceeded() {
        val service = ChatService()
        service.addNewMessage(senderId = 1, receiverId = 2, text = "message")
        service.addNewMessage(senderId = 1, receiverId = 3, text = "message 2")

        assertEquals(service.userRecordsCount(), 3)
        assertEquals(service.chatRecsCount(), 4)
    }

    @Test
    fun getUnreadChatsCount_2chats1MessageSucceeded() {
        val service = ChatService()
        service.addNewMessage(senderId = 2, receiverId = 1, text = "message")
        service.addNewMessage(senderId = 3, receiverId = 1, text = "message 2")

        assertEquals(service.getUnreadChatsCount(1), 2)
    }

    @Test
    fun getUnreadChatsCount_2chatsNoUnreadMessagesSucceeded() {
        val service = ChatService()
        service.addNewMessage(senderId = 1, receiverId = 2, text = "message")
        service.addNewMessage(senderId = 1, receiverId = 3, text = "message 2")

        assertEquals(service.getUnreadChatsCount(1), 0)
    }

    @Test(expected = UserNotFoundException::class)
    fun getUnreadChatsCount_wrongUserId() {
        val service = ChatService()
        service.addNewMessage(senderId = 1, receiverId = 2, text = "message")
        service.addNewMessage(senderId = 1, receiverId = 3, text = "message 2")

        service.getUnreadChatsCount(4)
    }

    @Test
    fun getChats_succeeded() {
        val service = ChatService()
        service.addNewMessage(senderId = 2, receiverId = 1, text = "message 21")
        service.addNewMessage(senderId = 1, receiverId = 2, text = "message 12")
        service.addNewMessage(senderId = 3, receiverId = 1, text = "message 31")
                     //получаем 2 чата
        val result = service.getChats(receiverId = 1)
        assertEquals(result.size, 2)
        //чат 1 <-> 2, два сообщение, последнее 12
        assertEquals(result[0].lastMsg.text,"message 12")
        //чат 3 <-> 1, одно сообщение оно же последнее 31
        assertEquals(result[1].lastMsg.text,"message 31")
    }

    @Test(expected = UserNotFoundException::class)
    fun getChats_wrongUserId() {
        val service = ChatService()
        service.addNewMessage(senderId = 2, receiverId = 1, text = "message")
        service.addNewMessage(senderId = 3, receiverId = 1, text = "message 2")

        service.getChats(receiverId = 4)
    }

    @Test
    fun getMessages_succeeded() {
        val service = ChatService()
        //создаем чат с id = 1
        service.addNewMessage(senderId = 2, receiverId = 1, text = "message 21")
        service.addNewMessage(senderId = 1, receiverId = 2, text = "message 12")
        //создаем чат с id = 2
        service.addNewMessage(senderId = 3, receiverId = 1, text = "message 31")

        //в чате id=1, одно сообщение исходящее, одно входящее итого 2
        val msgList = service.getMessages(receiverId = 1, chatId = 1)
        assertEquals(msgList.size, 2)
        //первое входящее сообщение должно получить статус прочитано
        assertEquals(msgList[0].readState, true)
        //второе исходящее должно остаться непрочитанным
        assertEquals(msgList[1].readState, false)
    }

    @Test(expected = UserNotFoundException::class)
    fun getMessages_wrongUserId() {
        val service = ChatService()
        //создаем чат с id = 1
        service.addNewMessage(senderId = 2, receiverId = 1, text = "message 21")
        service.addNewMessage(senderId = 1, receiverId = 2, text = "message 12")
        //создаем чат с id = 2
        service.addNewMessage(senderId = 3, receiverId = 1, text = "message 31")

        service.getMessages(receiverId = 4, chatId = 1)
    }

    @Test(expected = ChatNotFoundException::class)
    fun getMessages_wrongChatId() {
        val service = ChatService()
        //создаем чат с id = 1
        service.addNewMessage(senderId = 2, receiverId = 1, text = "message 21")
        service.addNewMessage(senderId = 1, receiverId = 2, text = "message 12")
        //создаем чат с id = 2
        service.addNewMessage(senderId = 3, receiverId = 1, text = "message 31")

        service.getMessages(receiverId = 1, chatId = 3)
    }

    @Test(expected = ChatNotFoundException::class)
    fun deleteChat_succeeded() {
        val service = ChatService()
        //создаем чат с id = 1
        service.addNewMessage(senderId = 2, receiverId = 1, text = "message 21")
        service.addNewMessage(senderId = 1, receiverId = 2, text = "message 12")
        //создаем чат с id = 2
        service.addNewMessage(senderId = 3, receiverId = 1, text = "message 31")

        service.deleteChat(receiverId = 1, chatId = 1)
        service.getMessages(receiverId = 1, chatId = 1)
    }

    @Test(expected = UserNotFoundException::class)
    fun deleteChat_wrongUserId() {
        val service = ChatService()
        //создаем чат с id = 1
        service.addNewMessage(senderId = 2, receiverId = 1, text = "message 21")
        service.addNewMessage(senderId = 1, receiverId = 2, text = "message 12")
        //создаем чат с id = 2
        service.addNewMessage(senderId = 3, receiverId = 1, text = "message 31")

        service.deleteChat(receiverId = 4, chatId = 1)
    }

    @Test(expected = ChatNotFoundException::class)
    fun deleteChat_wrongChatId() {
        val service = ChatService()
        //создаем чат с id = 1
        service.addNewMessage(senderId = 2, receiverId = 1, text = "message 21")
        service.addNewMessage(senderId = 1, receiverId = 2, text = "message 12")
        //создаем чат с id = 2
        service.addNewMessage(senderId = 3, receiverId = 1, text = "message 31")

        service.deleteChat(receiverId = 3, chatId = 3)
    }

    @Test
    fun deleteMessage_notLastMessage() {
        val service = ChatService()
        //создаем чат с id = 1
        service.addNewMessage(senderId = 2, receiverId = 1, text = "message 21")
        service.addNewMessage(senderId = 1, receiverId = 2, text = "message 12")
        //создаем чат с id = 2
        service.addNewMessage(senderId = 3, receiverId = 1, text = "message 31")

        val msgList  = service.getMessages(receiverId = 1, chatId = 1).filter{ !it.deletedState }
        service.deleteMessage(receiverId = 1, chatId = 1, msgId = msgList[0].id)
        val msgList2 = service.getMessages(receiverId = 1, chatId = 1).filter{ !it.deletedState }
        //в чате должно стать на 1 сообщение меньше
        assertEquals(msgList.size, msgList2.size + 1)
    }

    @Test(expected = ChatNotFoundException::class)
    fun deleteMessage_lastMessage() {
        val service = ChatService()
        //создаем чат с id = 1
        service.addNewMessage(senderId = 2, receiverId = 1, text = "message 21")
        service.addNewMessage(senderId = 1, receiverId = 2, text = "message 12")
        //создаем чат с id = 2
        service.addNewMessage(senderId = 3, receiverId = 1, text = "message 31")

        val msgList  = service.getMessages(receiverId = 1, chatId = 2).filter{ !it.deletedState }
        //удаляем последее сообдение
        service.deleteMessage(receiverId = 1, chatId = 2, msgId = msgList[0].id)

        //запрос должен завершится исключением ChatNotFoundException
        service.getMessages(receiverId = 1, chatId = 2)
    }

    @Test(expected = ChatNotFoundException::class)
    fun deleteMessage_wrongChatId() {
        val service = ChatService()
        //создаем чат с id = 1
        service.addNewMessage(senderId = 2, receiverId = 1, text = "message 21")
        service.addNewMessage(senderId = 1, receiverId = 2, text = "message 12")
        //создаем чат с id = 2
        service.addNewMessage(senderId = 3, receiverId = 1, text = "message 31")

        val msgList  = service.getMessages(receiverId = 1, chatId = 2).filter{ !it.deletedState }
        //удаляем последее сообдение
        service.deleteMessage(receiverId = 1, chatId = 3, msgId = msgList[0].id)
    }

    @Test(expected = UserNotFoundException::class)
    fun deleteMessage_wrongUserId() {
        val service = ChatService()
        //создаем чат с id = 1
        service.addNewMessage(senderId = 2, receiverId = 1, text = "message 21")
        service.addNewMessage(senderId = 1, receiverId = 2, text = "message 12")
        //создаем чат с id = 2
        service.addNewMessage(senderId = 3, receiverId = 1, text = "message 31")

        val msgList  = service.getMessages(receiverId = 1, chatId = 2).filter{ !it.deletedState }
        //удаляем последее сообдение
        service.deleteMessage(receiverId = 4, chatId = 1, msgId = msgList[0].id)
    }

}
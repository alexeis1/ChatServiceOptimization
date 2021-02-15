package ru.netology.lesson9

import org.junit.Assert.*
import org.junit.Test

class ChatServiceTest {

    @Test
    fun addNewMessage_succeeded() {
        val service = ChatService()
        service.addNewMessage(1, 2, "message")
        assertEquals(service.userRecordsCount(), 2)
        assertEquals(service.chatRecsCount(), 2)
    }

    @Test
    fun addNewMessage_addInExistedChatSucceeded() {
        val service = ChatService()
        service.addNewMessage(1, 2, "message")
        service.addNewMessage(1, 2, "message 2")

        assertEquals(service.userRecordsCount(), 2)
        assertEquals(service.chatRecsCount(), 2)
    }

    @Test
    fun addNewMessage_addNewChatInExistedUserRecordSucceeded() {
        val service = ChatService()
        service.addNewMessage(1, 2, "message")
        service.addNewMessage(1, 3, "message 2")

        assertEquals(service.userRecordsCount(), 3)
        assertEquals(service.chatRecsCount(), 4)
    }

    @Test
    fun getUnreadChatsCount() {
    }

    @Test
    fun getChats() {
    }

    @Test
    fun getMessages() {
    }

    @Test
    fun deleteChat() {
    }

    @Test
    fun deleteMessage() {
    }
}
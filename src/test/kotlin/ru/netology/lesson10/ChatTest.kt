package ru.netology.lesson10

import org.junit.Assert.*
import org.junit.Test

class ChatTest {

    @Test
    fun edit_succeeded() {
        val chat   = Chat(chatId = 1, userId1 = 1, userId2 = 2)
        val msg    = chat.add(senderId = 1, text = "message")
        val result = chat.edit(id = msg.id, newText = "new message")
        assertTrue(result && chat.last()!!.text == "new message")
    }

    @Test(expected = CannotEditDeletedMessage::class)
    fun edit_failedOnDeleted() {
        val chat = Chat(chatId = 1, userId1 = 1, userId2 = 2)
                    chat.add(senderId = 1, text = "message")
        val msg2  = chat.add(senderId = 1, text = "message")
        chat.delete(msg2.id)
        chat.edit(id = msg2.id, newText = "new message")
    }

    @Test(expected = MessageNotFoundException::class)
    fun edit_notMessageFound() {
        val chat   = Chat(chatId = 1, userId1 = 1, userId2 = 2)
        val msg    = chat.add(senderId = 1, text = "message")
        chat.edit(id = msg.id + 1, newText = "new message")
    }

    @Test
    fun delete_succeeded2Messages() {
        val chat = Chat(chatId = 1, userId1 = 1, userId2 = 2)
        val msg1 = chat.add(senderId = 1, text = "message")
                   chat.add(senderId = 1, text = "message")
        assertTrue(chat.delete(msg1.id))
    }

    @Test(expected = ChatIsEmptyException::class)
    fun delete_lastSucceeded() {
        val chat = Chat(chatId = 1, userId1 = 1, userId2 = 2)
        val msg1 = chat.add(senderId = 1, text = "message")
        chat.delete(msg1.id)
    }

    @Test(expected = MessageNotFoundException::class)
    fun delete_failedByMessageId() {
        val chat = Chat(chatId = 1, userId1 = 1, userId2 = 2)
        val msg1 = chat.add(senderId = 1, text = "message")
        chat.delete(msg1.id + 1)
    }

    @Test
    fun getMessages_succeeded() {
        val chat = Chat(chatId = 1, userId1 = 1, userId2 = 2)
        chat.add(senderId = 1, text = "message")
        chat.add(senderId = 1, text = "message")
        assertEquals(chat.getMessages(receiverId = 2, lastReadId = 0, count = 1).size, 1)
        assertEquals(chat.getMessages(receiverId = 2, lastReadId = 1, count = 1).size, 1)
    }

    @Test
    fun getMessages_default() {
        val chat = Chat(chatId = 1, userId1 = 1, userId2 = 2)
        chat.add(senderId = 1, text = "message")
        chat.add(senderId = 1, text = "message")
        assertEquals(chat.getMessages(2).size, 2)
    }

    @Test
    fun last_succeeded() {
        val chat = Chat(chatId = 1, userId1 = 1, userId2 = 2)
        chat.add(senderId = 1, text = "message")
        assertNotNull(chat.last())
        assertEquals(chat.last()!!.userId, 1 )
    }

    @Test
    fun last_failedEmpty() {
        val chat = Chat(chatId = 1, userId1 = 1, userId2 = 2)
        assertNull(chat.last())
    }

    @Test
    fun last_succeededWithDeleted() {
        val chat = Chat(chatId = 1, userId1 = 1, userId2 = 2)
        val msg1  = chat.add(senderId = 1, text = "message")
        val msg2  = chat.add(senderId = 1, text = "message")
        chat.delete(msg2.id)
        assertEquals(chat.last()!!.id, msg1.id)
    }
}
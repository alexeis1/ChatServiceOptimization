package ru.netology.lesson9

/**
 * Сообщение чата
 */

typealias UserId = Int
typealias MsgId  = Int

data class ChatMessage(
    val id           : MsgId,            // - сообщения
    val userId       : UserId,           // - создателя сообщения
    val text         : String,           // - тест сообщения
    val date         : Int = 0,          // - unix date
    val readState    : Boolean = false,  // - false - сообщение не прочитано / true - прочитано
    val deletedState : Boolean = false   // - false - сообщение не удалено / true сообщение удаено
)

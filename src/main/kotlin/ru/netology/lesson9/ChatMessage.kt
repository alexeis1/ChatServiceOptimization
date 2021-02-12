package ru.netology.lesson9

/**
 * Сообщение чата
 */
data class ChatMessage(
    val id           : Int,              // - сообщения
    val userId       : Int,              // - создателя сообщения
    val text         : String,           // - тест сообщения
    val date         : Int = 0,          // - unix date
    val readState    : Boolean = false,  // - false - сообщение не прочитано / true - прочитано
    val deletedState : Boolean = false   // - false - сообщение не удалено / true сообщение удаено
)

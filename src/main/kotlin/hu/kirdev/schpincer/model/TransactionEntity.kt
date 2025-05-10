package hu.kirdev.schpincer.model

import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

data class TransactionEntity(
        @Id
        @GeneratedValue
        @Column
        var id: Long = 0,

        @Column
        var accountId: Long = 0,

        @Column
        var amount: Long = 0,

        @Column
        var associatedCircleId: Long = 0,

        @Column
        var issuedOn: Long = 0,

        @Column
        var issuer: String = "",

        @Column
        var sericeComment: String = "",

        @Column
        var userComment: String = "",

        @Column
        var hash: String = ""
)

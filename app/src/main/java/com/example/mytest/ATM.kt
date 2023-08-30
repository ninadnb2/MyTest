package com.example.mytest

import java.util.Scanner

class ATM {
    private val availableDenominations = mutableMapOf(
        2000 to 0,
        500 to 0,
        200 to 0,
        100 to 0
    )


    fun showAvailableDenominations() {
        println("Current Available Denomination in Machine")
        val maxLength = availableDenominations.keys.maxOrNull()?.toString()?.length ?: 0

        for ((denomination, count) in availableDenominations) {
            val denominationStr = denomination.toString()
            val padding = " ".repeat(maxLength - denominationStr.length)
            println("%-${maxLength}s\t$count".format("$denominationStr$padding"))
        }

        val totalValue = availableDenominations.entries.sumBy { (denomination, count) -> denomination * count }
        val totalStr = "Total"
        val totalPadding = " ".repeat(maxLength - totalStr.length + 1)  // Adjusted padding here
        println("%-${maxLength}s\t$totalValue".format("$totalStr$totalPadding"))
    }


    fun deposit(denomination: Int, numNotes: Int): String {
        val validDenominations = setOf(100, 200, 500, 2000)

        if (denomination !in validDenominations) {
            return "Invalid denomination. Allowed denominations: ${validDenominations.joinToString(", ")}"
        }

        if (numNotes <= 0) {
            return "Invalid number of notes. Please enter a positive number."
        }

        val amount = denomination * numNotes

        if (amount % denomination != 0) {
            return "Invalid deposit amount. Amount should be a multiple of $denomination."
        }

        availableDenominations[denomination] = availableDenominations.getOrDefault(denomination, 0) + numNotes
        return "Deposit successful. Denomination $denomination updated."
    }

    fun getDenominationCount(): Map<Int, Int> {
        return availableDenominations.toMap()
    }


    fun withdraw(amount: Int) {
        var remainingAmount = amount
        val withdrawnDenominations = mutableMapOf<Int, Int>()

        for (denomination in availableDenominations.keys.sortedDescending()) {
            val noteCount = remainingAmount / denomination
            val availableNotes = availableDenominations.getOrDefault(denomination, 0)

            if (noteCount > 0 && availableNotes >= noteCount) {
                withdrawnDenominations[denomination] = noteCount
                availableDenominations[denomination] = availableNotes - noteCount
                remainingAmount -= noteCount * denomination
            }
        }

        if (remainingAmount == 0) {
            println("Withdrawn successfully:")
            for ((denomination, count) in withdrawnDenominations) {
                println("$denomination x $count")
            }
        } else {
            println("Withdrawal failed. Insufficient denominations available.")
        }
    }
}

fun main() {
    val atm = ATM()
    val scanner = Scanner(System.`in`)
    atm.showAvailableDenominations()
    while (true) {
        println("Select operation:")
        println("1. Deposit")
        println("2. Withdraw")
        println("3. Exit")
        print("Enter your choice: ")
        val choice = scanner.nextInt()

        when (choice) {
            1 -> {
                println("Enter deposit amount: ")
                val depositAmount = scanner.nextInt()
                atm.deposit(depositAmount,1)
                atm.showAvailableDenominations()
            }
            2 -> {
                println("Enter withdrawal amount: ")
                val withdrawalAmount = scanner.nextInt()
                atm.withdraw(withdrawalAmount)
                atm.showAvailableDenominations()
            }
            3 -> {
                println("Exiting...")
                return
            }
            else -> println("Invalid choice.")
        }
    }
}





package org.hildan.leboncoin.api

data class UserData(
    val userId: String,
    val personalData: PersonalData
)

data class PersonalData(
    val email: String,
    val lastname: String,
    val firstname: String,
    val addresses: Addresses,
    val phones: Phones,
    val pseudo: String
)

data class Addresses(
    val billing: Address
)

data class Address(
    val label: String,
    val regionCode: Int,
    val dptCode: Int,
    val address: String,
    val zipcode: String,
    val city: String
)

data class Phones(
    val main: Phone
)

data class Phone(
    val label: String,
    val number: String
)

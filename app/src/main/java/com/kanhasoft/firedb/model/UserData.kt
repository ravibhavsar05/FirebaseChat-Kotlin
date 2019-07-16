package com.kanhasoft.firedb.model

class UserData {
    var user_id: String? = null
    var uid: String? = null
    var display_name: String? = null
    var email: String? = null
    var sex: String? = null
    var phone: String? = null
    var birthdate: String? = null
    var about: String? = null
    var address: String? = null
    var state: Int? = 0
    var city: Int? = 0
    var zip: String? = null
    var profile_pic: String? = null
    var last_chat_with_admin: String? = null
    var last_chat_with_user: String? = null
    var token: String? = null

    constructor() {
    }

    override fun toString(): String {
        return "UserData(user_id=$user_id, uid=$uid, display_name=$display_name, email=$email, sex=$sex, phone=$phone, birthdate=$birthdate, about=$about, address=$address, state=$state, city=$city, zip=$zip, profile_pic=$profile_pic, last_chat_with_admin=$last_chat_with_admin, last_chat_with_user=$last_chat_with_user, token=$token)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserData

        if (user_id != other.user_id) return false
        if (uid != other.uid) return false
        if (display_name != other.display_name) return false
        if (email != other.email) return false
        if (sex != other.sex) return false
        if (phone != other.phone) return false
        if (birthdate != other.birthdate) return false
        if (about != other.about) return false
        if (address != other.address) return false
        if (state != other.state) return false
        if (city != other.city) return false
        if (zip != other.zip) return false
        if (profile_pic != other.profile_pic) return false
        if (last_chat_with_admin != other.last_chat_with_admin) return false
        if (last_chat_with_user != other.last_chat_with_user) return false
        if (token != other.token) return false

        return true
    }

    override fun hashCode(): Int {
        var result = user_id?.hashCode() ?: 0
        result = 31 * result + (uid?.hashCode() ?: 0)
        result = 31 * result + (display_name?.hashCode() ?: 0)
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (sex?.hashCode() ?: 0)
        result = 31 * result + (phone?.hashCode() ?: 0)
        result = 31 * result + (birthdate?.hashCode() ?: 0)
        result = 31 * result + (about?.hashCode() ?: 0)
        result = 31 * result + (address?.hashCode() ?: 0)
        result = 31 * result + (state ?: 0)
        result = 31 * result + (city ?: 0)
        result = 31 * result + (zip?.hashCode() ?: 0)
        result = 31 * result + (profile_pic?.hashCode() ?: 0)
        result = 31 * result + (last_chat_with_admin?.hashCode() ?: 0)
        result = 31 * result + (last_chat_with_user?.hashCode() ?: 0)
        result = 31 * result + (token?.hashCode() ?: 0)
        return result
    }


}

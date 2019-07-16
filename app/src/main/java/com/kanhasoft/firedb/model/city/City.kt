package com.kanhasoft.firedb.model.city

import com.kanhasoft.firedb.model.state.State

class City(val cityID: Int, val state: State, val cityName: String) : Comparable<City> {

    override fun toString(): String {
        return cityName
    }

    override fun compareTo(another: City): Int {
        return this.cityID - another.cityID//ascending order
        //            return another.getCityID() - this.cityID;//descending order
    }
}

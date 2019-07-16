package com.kanhasoft.firedb.model.state

class State(val stateID: Int, val stateName: String) : Comparable<State> {

    override fun toString(): String {
        return stateName
    }

    override fun compareTo(another: State): Int {
        return this.stateID - another.stateID//ascending order
        //            return another.getStateID()-this.getStateID();//descending order
    }
}

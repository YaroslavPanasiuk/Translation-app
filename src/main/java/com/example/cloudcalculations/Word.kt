package com.example.cloudcalculations

class Word{
    var index: Int = -1
    var unknownWord: String = ""
    var translation: String = ""
    var topicName: String = ""
    var state: Int = 1
    constructor(unknownWord:String, translation:String, topicName:String, state: Int){
        this.unknownWord = unknownWord
        this.translation = translation
        this.topicName = topicName
        this.state = state
    }
    constructor(unknownWord:String, translation:String){
        this.unknownWord = unknownWord
        this.translation = translation
    }
    constructor()
}
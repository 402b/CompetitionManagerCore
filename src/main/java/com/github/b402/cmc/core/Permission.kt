package com.github.b402.cmc.core

enum class Permission(
        vararg val ext: String
) {
    ANY(),
    USER("ANY"),
    VERIFIED("USER"),
    JUDGE("VERIFIED"),
    PROJECT_JUDGE("JUDGE"),
    MAIN_JUDGE("PROJECT_JUDGE"),
    ADMIN("MAIN_JUDGE");
    val extend = mutableListOf<Permission>()
    fun init() {
        for(t in ext){
            extend += Permission.valueOf(t)
        }
    }


    operator fun contains(pre: Permission):Boolean{
        if(this == pre){
            return true
        }
        for(t in this.extend){
            if(pre in t){
                return true
            }
        }
        return false
    }

    companion object{
        init{

            for(t in Permission.values()){
                t.init()
            }
        }
    }
}
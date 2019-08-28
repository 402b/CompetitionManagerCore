package com.github.b402.cmc.core

enum class Permission(
        vararg val ext: String
) {
    ANY(),
    USER("ANY"),
    JUDGE("USER"),
    PROJECT_JUDGE("JUDGE"),
    MAIN_JUDGE("PROJECT_JUDGE"),
    ADMIN("MAIN_JUDGE");
    val extend = mutableListOf<Permission>()
    fun init() {
        for(t in ext){
            extend += Permission.valueOf(t)
        }
    }

    private fun check(from: Permission):Boolean{
        if(from == this){
            return true
        }
        for(t in extend){
            if(t.check(from)){
                return true
            }
        }
        return false

    }
    operator fun contains(pre: Permission):Boolean{
//        checkInit()
        if(this == pre){
            return true
        }
        for(t in pre.extend){
            if(t.check(this)){
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
//        var init = false
//        fun checkInit(){
//            if(!init){
//                for(t in Permission.values()){
//                    t.init()
//                }
//                init = true
//            }
//        }
    }
}
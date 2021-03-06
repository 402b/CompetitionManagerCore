//引入部分依赖函数
//document.write('<script src="login.js" type="text/javascript" charset="utf-8"></script>');

//报名函数
/*signUp(competition){

    axios({
        params: {
            url:"/Data/",
            param: {
                token:getCookie("token"),
                Data:{
                    userName: userName,
                    password: password
                }
            }
        }
    }).then(



    );
}

applyJudge(){
    axios({
        params:{
            url:"/Data/judeg_apply",
            param:{
                token:getCookie("token"),
                Data:{
                    gameId:competition.id
                }
            }

        }
    }).then();

}


*/







var vm = new Vue({
    el: '#page',
    data: {
        userInfo:[],
        /*userInfo:{
            name:"smc",
            age:20,
            authority:"athlete"
        },*/
        messageNum:0,
        message:{
            //接收消息

        },
        gameToShowById:[],
        //pageIndex
        pageIndex:1,
        //储存得到的所有比赛项目的id
        gameIds:[1,2,3],
        //储存要展示的比赛项目的信息
        gameToShow:[],
        userPermission:0,
    },
    computed:{
        //获取个人信息
        computeUserInfo:function(){
            axios({
                method:"POST",
                url:"/Data/user_info",
                data:{

                    param:{
                        token:getCookie("token"),
                        Data:{

                        }
                    }
                }
            }).then(
                rep=>{console.log("请求个人信息成功");console.log(rep);
                console.log(rep["data"]);
                this.userInfo = rep["data"];
                return rep["data"];}
                ,
                rep=>{console.log("请求个人信息失败")}
            )
        },

        //计算用户权限
        computeUserPermission:function(){
            axios({
                method:'POST',
                url:"/Data/user_permission",
                data:{

                    param: {
                        token:getCookie("token"),
                        Data:{

                        },
                    }
                }
            }).then(
                rep=>{
                    console.log("获取用户权限成功");
                    //格式存疑
                    var permission = rep.data.permission;
                    console.log(rep);
                    switch(permission){
                        case "JUDGE": this.userPermission = 1; break;
                        case "PROJECT_JUDGE": this.userPermission = 2;break;
                        case "MAIN_JUDGE": this.userPermission = 3;break;
                        case "ADMIN": this.userPermission = 4;break;
                    }
                    if(this.userPermission>0)
                        document.getElementById("show1").style.display="block";
                    if(this.userPermission>1)
                        document.getElementById("show2").style.display="block";
                    if(this.userPermission>2)
                        document.getElementById("show3").style.display="block";
                    if(this.userPermission>3)
                        document.getElementById("show4").style.display="block";
                },
                rep=>{console.log("获取用户权限失败")}
            );
        },

        //获取所有可报名比赛的id
        /*computeGameIds: function(){
                axios({
                    method:"POST",
                    url:"/Data/game_list",
                    data:{

                        param: {
                            token:getCookie("token"),
                            Data:{
                                //有歧义 并非真正gameId 而是比赛在列表中的序号
                                gameId:this.gameToShowById
                            },
                        }
                    }
                }).then(
                    rep=>{
                        console.log("获取可报名的比赛id成功");
                        this.gameIds = rep.data;
                        //格式存疑
                        return rep;
                    },
                    rep=>{console.log("获取可报名的比赛id失败")}
                );
            },
        */
        //获取要展示的比赛的gameId
        /*computeGameToShowById: function () {
            var index = (this.pageIndex-1)*10;
            index = index>-1?index:0;
            var list = [];
            //if(!this.gameIds)
            //   return [];
            for(var i =0;i<10 && this.gameIds.gamelist[index+i];i++){
                list[i] = this.gameIds.gamelist[index+i];
            }
            this.gameToShowById = list;
            return list;
        }, */
        computeGameToShow:function(){
            //获取要展示的可报名的比赛的信息
            var index = (this.pageIndex-1)*10;
            index = index>-1?index:0;
            var list = [];
            //if(!this.gameIds)
            //   return []
            if(!this.gameIds.gamelist)
                return null;
            console.log("gamelist: "+ this.gameIds.gamelist)
            for(var i =0;i<10 && this.gameIds.gamelist[index+i];i++){
                list[i] = this.gameIds.gamelist[index+i];
            }
            this.gameToShowById = list;
            //vm.$set()
            axios({
                method:"POST",
                url:"/Data/game_info",
                data:{
                    param: {
                        token:getCookie("token"),
                        Data:{
                            gameId:list
                        },
                    }
                }
            }).then(
                rep=>{
                    console.log("获取要展示的比赛的信息成功");
                    this.gameToShow = rep.data;
                    console.log(rep);
                    //格式存疑
                    return rep;
                },
                rep=>{console.log("获取要展示的比赛的信息失败")}
            );
        },
        //获取未读消息id信息
        computeUnreadMessageId: function (){
            axios({
                method:"POST",
                url:"/Data/message_unread",
                data:{

                    param: {
                        token:getCookie("token"),
                        Data:{
                            uid:this.userInfo.uid,
                        },
                    }
                }
            }).then(
                rep=>{
                    console.log("获取未读消息id成功");
                    this.messageIds = rep.data.list;
                    if(rep.data.list)
                        this.messageNum = rep.data.list.length;
                    console.log(rep);
                    return rep;
                },
                rep=>{console.log("获取未读消息id失败")}
            );
        },
        //获取未读消息id信息
        computeUnreadMessageId: function (){
            axios({
                method:"POST",
                url:"/Data/message_unread",
                data:{

                    param: {
                        token:getCookie("token"),
                        Data:{
                            uid:this.userInfo.uid,
                        },
                    }
                }
            }).then(
                rep=>{
                    console.log("获取未读消息id成功");
                    this.messageIds = rep.data.list;
                    if(rep.data.list)
                        this.messageNum = rep.data.list.length;
                    console.log(rep);
                    return rep;
                },
                rep=>{console.log("获取未读消息id失败")}
            );
        },
    },
    methods:{
        //报名某个比赛
        signUp(competition){
            console.log(competition);
            axios({
                method:"POST",
                url:"/Data/game_join",
                data: {

                    param: {
                        token:getCookie("token"),
                        Data:{
                            uid:this.userInfo.uid,
                            gid:competition.gameID
                        }
                    }
                }
            }).then(
                rep=>{ console.log("报名比赛请求送出")
                    if(rep.data.status=="success")
                        alert("报名成功");
                },
                rep=>{console.log("报名比赛请求发送失败")}
            );
        },
        //申请成为某个比赛的裁判
        applyJudge(competition){
            console.log(competition);
            axios({
                method:"POST",
                url:"/Data/judge_apply",
                data: {

                    param: {
                        token:getCookie("token"),
                        Data:{
                            gid:competition.gameID
                        }
                    }
                }
            }).then(
                rep=>{ console.log("申请裁判请求发送成功");
                    if(rep.data.status=="success")
                        alert("申请发送成功，请等待审核");
                },
                rep=>{ console.log("申请裁判请求发送失败")}
            );
        },

        toLocalString: function(date) {
            return date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate()+"   "+date.getHours()
                +":"+date.getMinutes()+":"+date.getSeconds();
        },
    }
})




axios({
    method:"POST",
    url:"/Data/game_list",
    data:{

        param: {
            token:getCookie("token"),
            Data:{

            },
        }
    }
}).then(
    rep=>{
        console.log("获取可报名的比赛id成功");
        vm.gameIds = rep.data;
        //格式存疑
        return rep;
    },
    rep=>{console.log("获取可报名的比赛id失败")}
);
//console.log(vm.computeGameToShow);


    axios({
        method:"POST",
        url:"/Data/user_info",
        data:{

            param:{
                token:getCookie("token"),
                Data:{

                }
            }
        }
    }).then(
        rep=>{console.log("请求个人信息成功");console.log(rep);
            console.log(rep["data"]);
            vm.userInfo = rep["data"];
            return rep["data"];}
        ,
        rep=>{console.log("请求个人信息失败")}
    )

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
        userInfo:{name:"test3"},
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
        pageIndex:0,
        //储存得到的所有比赛项目的id
        gameIds:[1,2,3],
        //储存要展示的比赛项目的信息
        gameToShow:[]
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
        computeGameToShowById: function () {
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
        },
        computeGameToShow:function(){
            //获取要展示的可报名的比赛的信息
            axios({
                method:"POST",
                url:"/Data/game_info",
                data:{
                    param: {
                        token:getCookie("token"),
                        Data:{
                            gameId:this.gameToShowById
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
        }
    },
    methods:{
        //报名某个比赛
        signUp(competition){
            axios({
                method:"POST",
                url:"/Data/game_join",
                data: {

                    param: {
                        token:getCookie("token"),
                        Data:{
                            uid:this.userInfo.uid,
                            gid:competition.gameId
                        }
                    }
                }
            }).then(
                rep=>{ console.log("报名比赛请求送出")},
                rep=>{console.log("报名比赛请求发送失败")}
            );
        },
        //申请成为某个比赛的裁判
        applyJudge(competition){
            axios({
                method:"POST",
                url:"/Data/judge_apply",
                data: {

                    param: {
                        token:getCookie("token"),
                        Data:{
                            gameId:competition.gameId
                        }
                    }
                }
            }).then(
                rep=>{ console.log("申请裁判请求发送成功")},
                rep=>{ console.log("申请裁判请求发送失败")}
            );
        }
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
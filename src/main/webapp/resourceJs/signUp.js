//引入部分依赖函数
document.write('<script src="login.js" type="text/javascript" charset="utf-8"></script>');

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
        /*userInfo:{
            name:"smc",
            age:20,
            authority:"athlete"
        },*/
        messageNum:0,
        message:{
            //接收消息

        },
        //pageIndex
        pageIndex:0,
        //储存得到的所有比赛项目的id
        gameIds:[],
        //储存要展示的比赛项目的信息
        //gameToShow
    },
    computed:{
        userInfo:function(){
            axios({
                params:{
                    url:"/Data/user_info",
                    param:{
                        token:getCookie("token"),
                        Data:{

                        }
                    }
                }
            }).then(
                rep=>{console.log("请求个人信息成功");console.log(rep);return rep}
                ,
                rep=>{console.log("请求个人信息失败")}
            )
        },
        //获取所有可报名比赛的信息
        computeGameIds:{
            get:function(){
                return this.gameIds
            },
            set:function(){
                axios({
                    params:{
                        url:"/Data/game_info",
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
                        console.log("获取可报名的项目成功");
                        //格式存疑
                        return rep;
                    },
                    rep=>{console.log("获取可报名的项目失败")}
                );
            }
        },
        //获取要展示的比赛的gameId
        gameToShowById: function () {
            var index = (this.pageIndex-1)*10;
            index = index>-1?index:0;
            var list = [];
            for(var i =0;i<10 && index+i<this.gameIds.length;i++){
                list[i] = this.gameIds[index+i];
            }
            return list;
        },
        gameToShow:function(){
            //获取可报名的比赛项目
            var games = {};
            axios({
                params:{
                    url:"/Data/game_list",
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
                    console.log("获取可报名的项目成功");
                    //格式存疑
                    return rep;
                },
                rep=>{console.log("获取可报名的项目失败")}
            );
        }
    },
    methods:{
        //报名某个比赛
        signUp(competition){
            axios({
                params: {
                    url:"/Data/game_join",
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
                params: {
                    url:"/Data/judge_apply",
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
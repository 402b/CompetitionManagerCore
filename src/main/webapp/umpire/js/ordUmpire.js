//获取cookie函数
function getCookie(name){
    var aCookie = document.cookie.split("; ");
    for(var i =0;i<aCookie.length;i++){
        var keyValue = aCookie[i].split("=");
        if(name==keyValue[0]) {
            return keyValue[1];
        }
    }
    return null;
}
//设置cookie的函数
function setCookie(name,value){
    if (value != null) {
        document.cookie = name + "=" + value + "; ";
    }
}
//检查是否有token存在
var token = getCookie("token");
if(token!=null){
}
function formatNumber (n) {
    n = n.toString()
    return n[1] ? n : '0' + n;
}
function formatTime (number, format) {
    let time = new Date(number)
    let newArr = []
    let formatArr = ['Y', 'M', 'D', 'h', 'm', 's']
    newArr.push(time.getFullYear())
    newArr.push(formatNumber(time.getMonth() + 1))
    newArr.push(formatNumber(time.getDate()))

    newArr.push(formatNumber(time.getHours()))
    newArr.push(formatNumber(time.getMinutes()))
    newArr.push(formatNumber(time.getSeconds()))

    for (let i in newArr) {
        format = format.replace(formatArr[i], newArr[i])
    }
    return format;
}

var userInfo = new Vue({    //获取用户信息
    el: "#userInfo",
    data: {
        realName:"",
        gender:"",
        uid:"",
        id:""
    },
    created() {
        this.refresh();
    },
    methods: {
        refresh: function () {
            axios({
                url: '/Data/userinfo',
                params: {
                    param: {
                        token: getCookie("token"),
                        Data: {}
                    }
                }
            }).then(
                rep=>{
                    if(rep.data.status == "success")
                    {
                        setCookie("token", rep.data.token);
                        this.realName = rep.data.realName;
                        this.gender = rep.data.gender;
                        this.uid = rep.data.uid;
                        this.id = rep.data.id;
                    }
                    else
                    {
                        alert("获取用户信息失败！" + rep.data.reason)
                    }
                })
        }
    }
})

var enterScore = new Vue({    //录入成绩
    el: "#enterScore",
    data: {
        game: [
        ],   //某裁判负责的所有项目信息
        player: [
        ],
        uids:[],    //参赛选手的uid列表
        gameid: [
        ],  //用户从后端获取的所有比赛id
        score:[],       //提交成绩后传给后端比赛列表
        gameID: "", //选择录入的赛事ID
        gameName: "",   //选择录入赛事的名称
        show:false, //false展示赛事选择界面，true展示成绩录入界面
        pageNow: 1, //当前所在页面
        pageAmount: 0, //页面总数
        recordAmount: 0,
        pageEach: 10,   //每页显示的记录数
        enterNumber:0, //用户键盘输入的页码数
    },
    created() {
        this.start();
    },
    methods: {
        start: function() {
            axios({
                url: '/Data/judge_info',
                params: {
                    param: {
                        token: getCookie("token"),
                        Data: {
                        }
                    }
                }
            }).then(
                rep=>{
                    if(rep.data.status=="success"){
                        setCookie("token",rep.data.token);
                        this.gameid = [];
                        for (var info of rep.data.infos) {
                            this.gameid.push(info.gid);
                        }
                        this.recordAmount = this.gameid.length;
                        this.pageAmount = Math.ceil(this.recordAmount/this.pageEach);
                        this.changePage();
                    } else{
                        alert("获取裁判员负责的项目id列表失败!");
                    }
                })
        },
        prePage: function() {
            if (this.pageNow >= 2) {
                this.pageNow--;
                this.changePage();
            }
        },
        nextPage: function() {
            if (this.pageNow < this.pageAmount) {
                this.pageNow++;
                this.changePage();
            }
        },
        enter: function() {
            var patrn = /^\d+$/;
            if (this.enterNumber > this.pageAmount || this.enterNumber <= 0) {
                alert("你想前往的页面已超出上限!");
            }
            else if (!patrn.exec(this.enterNumber)){
                alert("请输入正确数字!")
            }
            else {
                this.pageNow = this.enterNumber;
                this.changePage();
            }
        },
        changePage: function () {
            if (this.show == false) {
                var start = (this.pageNow - 1) * this.pageEach;
                var end = Math.min(start + this.pageEach, this.recordAmount);
                var gameidX = [];    //需要请求的比赛id列表
                for (var i = start; i < end; i++) {
                    gameidX.push(this.gameid[i]);
                }
                axios({
                    url: '/Data/game_info',
                    method: 'POST',
                    data: {
                        param: {
                            token: getCookie("token"),
                            Data: {
                                gameId: gameidX,
                            }
                        }
                    }
                }).then(
                    rep => {
                        if (rep.data.status == "success") {
                            setCookie("token", rep.data.token);
                            this.game = rep.data.info;
                            for (var i = 0; i < this.game.length; i++) {
                                this.game[i].time = formatTime(this.game[i].time, 'Y-M-D h:m:s');
                            }
                        } else {
                            alert("获取用户数据表失败!");
                        }
                    })
            }
            else {
                for (var i = 0; i < this.uids.length; i++) {
                    var uidX = [];
                    uidX.push(this.uids[i]);
                    axios({     //向后端请求当前赛事参赛选手的详细信息
                        url: '/Data/user_info',
                        method: 'POST',
                        data: {
                            param: {
                                token: getCookie("token"),
                                Data: {
                                    uid: uidX,
                                }
                            }
                        }
                    }).then(
                        rep=>{
                            if(rep.data.status=="success"){
                                setCookie("token",rep.data.token);
                                this.player.push(rep.data.info[0]);
                            } else{
                                alert("获取参赛用户信息失败!"+rep.data.reason);
                                location.reload(true);
                            }
                        })
                }
            }
        }
        ,
        change: function(gameName, gameID) {     //进入成绩登记页面
            this.show = true;
            this.gameID = gameID;
            this.gameName = gameName;
            axios({     //向后端请求当前赛事参赛选手的id列表
                url: '/Data/game_joinInfo',
                method: 'POST',
                data: {
                    param: {
                        token: getCookie("token"),
                        Data: {
                            gameId: this.gameID,
                        }
                    }
                }
            }).then(
                rep=>{
                    if(rep.data.status=="success"){
                        setCookie("token",rep.data.token);
                        this.uids = rep.data.uids;
                        this.changePage();
                    } else{
                        alert("获取参赛用户列表失败!"+rep.data.reason);
                        location.reload(true);
                    }
                })
        },
        back: function(){
            this.show = false;
            this.gameID = "";
            this.gameName = "";
            this.scoreType = "";
            this.uids = [];
            this.player = [];
        },
        check: function () {
            this.score = [];
            var s = document.getElementsByName("score");
            for (var i = 0; i < s.length; i++) {
                var obj = {uid: this.player[i].uid, score: s[i].value};
                this.score.push(obj);
            }
            for (var i = 0; i < this.score.length; i++) {
                if (this.score[i].score != "") {
                    axios({
                        url: '/Data/score_upload',  //成绩录入
                        method: 'POST',
                        data: {
                            param: {
                                token: getCookie("token"),
                                Data: {
                                    gid: this.gameID,
                                    uid: this.score[i].uid,
                                    score: this.score[i].score,
                                }
                            }
                        }
                    }).then(
                        rep => {
                            if (rep.data.status == "success") {
                                setCookie("token", rep.data.token);
                                alert("成绩录入成功！");
                            } else {
                                alert("成绩录入失败！" + rep.data.reason);
                            }
                        }
                        ,
                        rep => {
                            alert("抱歉，网页当前不可用");
                            console.log(rep)
                        })
                }
            }
        }
    }
})
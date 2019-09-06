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
    document.cookie = name + "="+ value + "; ";
}
//检查是否有token存在
var token = getCookie("token");
if(token!=null){
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
        test: function() {
            this.realName = "Tony";
        },
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
                this.uid = rep.data.gender;
                this.id = rep.data.gender;
            }
        else
            {
                alert("获取用户信息失败！" + rep.data.reason)
            }
        })
        }
    }
})

var enterScore = new Vue({    //查看某裁判所负责的项目信息，并录入成绩
    el: "#enterScore",
    data: {
        game: [ //isReg表示是否已经登记成绩
            ],   //某裁判负责的所有项目信息
        player: [
            {uid:"001",realName:"张三"},
            {uid:"002",realName:"李四"},
            {uid:"003",realName:"Tony"},
            {uid:"004",realName:"Lisa"},
            ],
        gameid: [
        ],  //用户从后端获取的所有比赛id
        gameNow: [],    //当前应该展示的比赛信息
        score:[],       //提交成绩后传给后端比赛列表
        tests: [
            {gameName:"男子100米",gameID:"001",gameType:"预赛",isReg:false},
            {gameName:"女子100米",gameID:"002",gameType:"决赛",isReg:true},
            {gameName:"男子100米",gameID:"003",gameType:"预赛",isReg:true},
            {gameName:"男子1500米",gameID:"004",gameType:"预赛",isReg:false},
        ],
        gameID: "", //选择录入的赛事ID
        gameName: "",   //选择录入赛事的名称
        show:false, //false展示赛事选择界面，true展示成绩录入界面
        scoreType: "", //成绩类型
        pageNow: 1, //当前所在页面
        pageAmount: 0, //页面总数
        recordAmount: 0,
        pageEach: 1,   //每页显示的记录数
        enterNumber:0, //用户键盘输入的页码数
    },
    created() {
        this.start();
    },
    methods: {
        start: function() {
            //测试用代码
            // this.recordAmount = this.tests.length;
            // this.pageAmount = Math.ceil(this.recordAmount/this.pageEach);
            // this.changePage();

            axios({
                url: '/Data/judgeGame',
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
                        this.gameid = rep.data.gamelist;  //比赛id列表
                        this.recordAmount = this.gameid.length;
                        this.pageAmount = Math.ceil(this.recordAmount/this.pageEach);
                        this.changePage();
                    } else{
                        alert("获取裁判员负责的项目列表失败!");
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
                console.log(this.enterNumber);
                this.pageNow = this.enterNumber;
                this.changePage();
            }
        },
        changePage: function () {
            //测试用代码
            // this.game = [];
            // var start = (this.pageNow-1) * this.pageEach;
            // var end = Math.min(start+this.pageEach, this.recordAmount);
            // console.log(end);
            // for (var i = start; i < end; i++) {
            //     this.game.push(this.tests[i]);
            // }
            // console.log(this.game.length);

            var start = (this.pageNow-1) * this.pageEach;
            var end = Math.min(start+this.pageEach, this.recordAmount);
            var gameidX = [];    //需要请求的用户id列表
            for (var i = start; i < end; i++) {
                gameidX.push(this.gameid[i]);
            }
            axios({
                url: '/Data/gameinfo',
                params: {
                    param: {
                        token: getCookie("token"),
                        Data: {
                            gameId: gameidX,
                        }
                    }
                }
            }).then(
                rep=>{
                    if(rep.data.status=="success"){
                        setCookie("token",rep.data.token);
                        this.game = rep.data.info;
                    } else{
                        alert("获取用户数据表失败!");
                    }
                })
        }
        ,
        change: function(gameName, gameID, isReg) {
            if (isReg == true) {
                var con = confirm("你是否要强制修改"+gameName+"的成绩？");
                if (con == false)
                    return;
            }
            this.show = true;
            this.gameID = gameID;
            this.gameName = gameName;
        },
        back: function(){
            this.show = false;
            this.gameID = "";
            this.gameName = "";
            this.scoreType = "";
        },
        check: function () {
            if (this.scoreType == "")
                alert("你还未选择成绩的单位!");
            else {
                var s = document.getElementsByName("score");
                var f = document.getElementsByName("foul");
                for (var i = 0; i < s.length; i++) {
                    var x = (f[i].value == "true");
                    var obj = {uid: this.player[i].uid, foul: x, score: s[i].value};
                    this.score.push(obj);
                }
                var legal = true;
                for (var j = 0; j < this.score.length; j++) {
                    if (this.score[j].foul == true && this.score[j].score != ""){
                        legal = false;
                        alert("犯规记录无需录入成绩！");
                        break;
                    }
                    else if (this.score[j].foul == false && this.score[j].score == ""){
                        legal = false;
                        alert("有未犯规且空的记录，请检查！");
                        break;
                    }
                    else if (this.scoreType == "秒" || this.scoreType == "米" || this.scoreType == "分数") {
                        var patrn = /^[0-9]+(.[0-9]{1,3})?$/;
                        if (!patrn.exec(this.score[j].score)) {
                            legal = false;
                            alert("第"+(j+1) + "行成绩请输入正确的整数或1-3位小数！");
                            break;
                        }
                    }
                    else if (this.scoreType == "分秒") {
                        var patrn = /^[0-9]+(:)+[0-9]+(.[0-9]{1,3})?$/;
                        if (!patrn.exec(this.score[j].score)) {
                            legal = false;
                            alert("第"+(j+1) + "行成绩请输入正确的时间信息！");
                            break;
                        }
                    }
                    else if (this.scoreType == "个") {
                        var patrn = /^\d+$/;
                        if (!patrn.exec(this.score[j].score)) {
                            legal = false;
                            alert("第"+(j+1) + "行成绩请输入正确的非负整数！");
                            break;
                        }
                    }
                }
                if (legal == false)
                    this.score = [];
                else{
                    axios({
                        url: '/Data/scoreReg',  //成绩录入
                        params: {
                            param: {
                                token: getCookie("token"),
                                Data: {
                                    gameID:this.gameID,
                                    scoreType:this.scoreType,
                                    score:this.score
                                }
                            }
                        }
                    }).then(
                        rep=>{
                            if(rep.data.status == "success")
                            {
                                alert("成绩录入成功！");

                            }
                            else
                            {
                                alert("成绩录入失败！" + rep.data.reason)
                            }
                        }
                        ,
                        rep=>{
                            alert("抱歉，网页当前不可用");
                            console.log(rep)
                        })
                }
            }
        }
    }
})

var setUmpire = new Vue({   //任命裁判
    el: "#setUmpire",
    data: {
        umpireName: "",
        UID: "",
        gameName: "",
        type: "",
    },
    methods: {
        check:function () {
            console.log(this.umpireName);
            console.log(this.UID);
            console.log(this.gameName);
            console.log(this.type);
            if (this.type!="主裁判" && (this.UID=="" || this.gameName=="" || this.umpireName==""))
                alert("有空项目，请检查!");
            else if(this.type=="主裁判" && this.gameName!="")
                alert("主裁判无需指定项目！");
            else if(this.type=="主裁判" && (this.UID=="" || this.umpireName==""))
                alert("有空项目，请检查!");
            else if(this.type!="主裁判" && this.type!="项目裁判" && this.type!="普通裁判")
                alert("请填写正确的裁判类型");
            else {
                axios({
                    url:'/Data/setUmpire',
                    params: {
                        param: {
                            Data:{
                                umpireName: this.umpireName,
                                UID: this.UID,
                                gameName: this.gameName,
                                type: this.type,
                            }
                        }
                    }
                }).then(
                    rep=>{
                    if(rep.data.status=="success"){
                    setCookie("token",rep.data.token);
                    alert("任命成功！");
                    location.reload(true);
                } else{
                    alert("任命失败"+rep.reason);
                }
            }
                    ,
                    rep=>{
                        alert("抱歉，网页当前不可用");
                        console.log(rep)
                    })
            }
        }
    }
})

var UmpireInfo = new Vue({
    el: "#UmpireInfo",
    data: {
        tests:[
            {uid:"001",realName:"Tony",umpireType:"项目裁判",game:"男子100米",gameType:"预赛",gameID:"002"},
            {uid:"002",realName:"Linda",umpireType:"普通裁判",game:"女子800米",gameType:"决赛",gameID:"003"},
            {uid:"004",realName:"Linda",umpireType:"普通裁判",game:"女子800米",gameType:"决赛",gameID:"004"}
        ],
        umpire:[
        ],
        isCheckAll: false,
        checked:[
        ],
        asd:"",
    },
    created() {
        this.getUmpire();
    },
    methods: {
        getUmpire: function() {
            axios({
                url: '/Data/umpire',
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
                        this.umpire = rep.data.umpire;
                    } else{
                        alert("获取裁判列表失败!");
                    }
                })
        },
        checkAll: function() {
            this.isCheckAll = !this.isCheckAll;
            if(this.isCheckAll == true) {
                this.checked = [];
                for (var i = 0; i < this.umpire.length; i++) {
                    var obj = {uid:this.umpire[i].uid,gameID:this.umpire[i].gameID};
                    this.checked.push(obj);
                }
            }
            else {
                this.checked = [];
            }
            console.log(this.checked.length);
        },
        checkOne: function(uid, gameID) {
            console.log(uid + gameID);
            var obj = {uid:uid,gameID:gameID};
            var x = -1;
            for (var i = 0; i < this.checked.length; i++) {
                if (this.checked[i].uid == uid && this.checked[i].gameID == gameID) {
                    x = i;
                    break;
                }
            }
            if (x>-1) {
                this.checked.splice(x,1)
                console.log(this.checked.length);
            }
            else {
                this.checked.push(obj);
            }
        },
        check: function(uid, gameID) {
            this.isCheckAll = (this.checked.length == this.umpire.length);
            var obj = {uid:uid,gameID:gameID};
            var x = JSON.stringify(this.checked).indexOf(JSON.stringify(obj));
            console.log(uid+":"+x);
            console.log(this.checked.length);
            if (x>-1)
                return true;
            else
                return false;
        },
        send: function () {
            if (this.checked.length < 1)
                alert("你未选择任何项目！");
            else {
                axios({
                    url: '/Data/recUmpire',
                    params: {
                        param: {
                            token: getCookie("token"),
                            Data: {checked:this.checked,
                            }
                        }
                    }
                }).then(
                    rep=>{
                        if(rep.data.status=="success"){
                            setCookie("token",rep.data.token);
                            alert("取消裁判资格成功！");
                            location.reload(true);
                        } else{
                            alert("取消裁判资格失败!");
                        }
                    }
                    ,
                    rep=>{
                        alert("抱歉，网页当前不可用");
                        console.log(rep)
                    })
            }
        }
    }
})
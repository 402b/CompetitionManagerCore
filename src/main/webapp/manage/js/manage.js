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
                alert("获取用户信息失败！" + rep.reason)
            }
        })
        }
    }})
var createGame = new Vue({  //创建比赛
    el: "#createGame",
    data: {
        name: "",
        type: "",
        time: "",
        startDate: "",
        endDate: "",
        number: ""
    },
    methods: {
        check:function () {
            console.log(this.name);
            console.log(this.type);
            console.log(this.time);
            console.log(this.number);
            console.log(this.startDate);
            console.log(this.endDate);
            if (this.name=="" || this.type=="" || this.time=="" || this.number=="" || this.startDate=="" || this.endDate=="")
                alert("有空项目，请检查!");
            else if(this.type!="预赛" && this.type!="预决赛")
                alert("请填写正确的比赛类型");
            else if(this.startDate > this.endDate || this.time < this.endDate)
                alert("请填写正确的报名时间");
            else {
                axios({
                    url:'/Data/createGame',
                    params: {
                        param: {
                            tooken: getCookie("token"),
                            Data:{
                                name: this.name,
                                type: this.type,
                                time: this.time,
                                number: this.number,
                            }
                        }
                    }
                }).then(
                    rep=>{
                    if(rep.data.status=="success"){
                    window.open("index.html"/* 管理界面 */);
                    setCookie("token",rep.data.token);
                    alert("注册成功！");
                } else{
                    alert("注册失败"+rep.reason)
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
                    window.open("index.html"/* 管理界面 */);
                    setCookie("token",rep.data.token);
                    alert("任命成功！");
                } else{
                    alert("任命失败"+rep.reason);
                }
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
        umpire:[],
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
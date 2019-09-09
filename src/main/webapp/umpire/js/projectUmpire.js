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


var enterScore = new Vue({    //录入成绩
    el: "#enterScore",
    data: {
        game: [ //isReg表示是否已经登记成绩
        ],   //某裁判负责的所有项目信息
        player: [
        ],
        tests2: [
            {uid:"001",realName:"张三"},
            {uid:"002",realName:"李四"},
            {uid:"003",realName:"Tony"},
            {uid:"004",realName:"Lisa"},
        ],
        uids:[],    //参赛选手的uid列表
        gameid: [
        ],  //用户从后端获取的所有比赛id
        gameNow: [],    //当前应该展示的比赛信息
        score:[],       //提交成绩后传给后端比赛列表
        tests: [
            {gameName:"男子100米",gameID:"001"},
            {gameName:"女子100米",gameID:"002"},
            {gameName:"男子100米",gameID:"003"},
            {gameName:"男子1500米",gameID:"004"},
        ],
        gameID: "", //选择录入的赛事ID
        gameName: "",   //选择录入赛事的名称
        show:false, //false展示赛事选择界面，true展示成绩录入界面
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
                url: '/Data/game_info',
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
        change: function(gameName, gameID, isReg) {     //进入成绩登记页面
            this.show = true;
            this.gameID = gameID;
            this.gameName = gameName;
            axios({     //向后端请求当前赛事参赛选手的id列表
                url: '/Data/game_joinInfo',
                params: {
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
                    } else{
                        alert("获取参赛用户列表失败!");
                    }
                })
            if (this.uids.length != 0) {
                for (var i = 0; i < this.uids.length; i++) {
                    var uid = [];
                    uid.push(this.uids[i]);
                    axios({     //向后端请求当前赛事参赛选手详细信息
                        url: '/Data/user_info',
                        params: {
                            param: {
                                token: getCookie("token"),
                                Data: {
                                    uid: uid,
                                }
                            }
                        }
                    }).then(
                        rep=>{
                            if(rep.data.status=="success"){
                                setCookie("token",rep.data.token);
                                this.player.push(rep.data.info[0]);
                            } else{
                                alert("获取参赛用户详细信息失败!");
                            }
                        })
                }
            }
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
                var s = document.getElementsByName("score");
                for (var i = 0; i < s.length; i++) {
                    var obj = {uid: this.player[i].uid, score: s[i].value};
                    this.score.push(obj);
                }
                for (var i = 0; i < this.score.length; i++) {
                    axios({
                        url: '/Data/score_upload',  //成绩录入
                        params: {
                            param: {
                                token: getCookie("token"),
                                Data: {
                                    gid: this.gameID,
                                    uid: this.score[i].uid,
                                    score:this.score[i].score,
                                }
                            }
                        }
                    }).then(
                        rep=>{
                            if(rep.data.status == "success")
                            {
                                if (i == (this.score.length-1)) {
                                    alert("所有成绩录入成功！");
                                    location.reload(true);
                                }
                            }
                            else
                            {
                                alert("成绩录入失败！" + rep.data.reason);
                                location.reload(true);
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

var cancelUmpire = new Vue({    //某裁判所负责的项目信息，可以用于取消裁判资格
    el: "#cancelUmpire",
    data: {
        umpire:[],  //当前应该显示的用户信息
        game: [],   //当前应该显示的某裁判负责的项目信息
        gameid: [],  //用户从后端获取的所有比赛id
        gameNow: [],    //当前应该展示的比赛信息
        tests: [
            {gameName: "男子100米", gameID: "001"},
            {gameName: "女子100米", gameID: "002"},
            {gameName: "男子100米", gameID: "003"},
            {gameName: "男子1500米", gameID: "004"},
        ],
        tests2: [
            {uid:"001",id:"1243",realName:"Tony",gender:"F"},
            {uid:"002",id:"1243",realName:"Tony",gender:"M"},
            {uid:"003",id:"1243",realName:"Tony",gender:"F"},
            {uid:"004",id:"1243",realName:"Tony",gender:"M"},
            {uid:"005",id:"1243",realName:"Tony",gender:"M"},
            {uid:"006",id:"1243",realName:"Tony",gender:"M"},
            {uid:"007",id:"1243",realName:"Tony",gender:"M"},
        ],
        gameID: "", //选择取消裁判的赛事ID
        gameName: "",   //选择取消裁判的赛事名称
        users:[],   //所有该项目裁判的uid
        show: false, //false展示赛事选择界面，true展示取消裁判界面
        pageNow: 1, //当前所在页面
        pageAmount: 0, //页面总数
        recordAmount: 0,
        pageEach: 2,   //每页显示的记录数
        enterNumber: 0, //用户键盘输入的页码数
        isAgree: false,
        isCheckAll: false,
        checked: [], //选择的用户id
    },
    created() {
        this.start();
    },
    methods: {
        start: function () {
            //测试用代码
            // this.recordAmount = this.tests.length;
            // this.pageAmount = Math.ceil(this.recordAmount / this.pageEach);
            // this.changePage();

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
        prePage: function () {
            if (this.pageNow >= 2) {
                this.pageNow--;
                this.changePage();
            }
        },
        nextPage: function () {
            if (this.pageNow < this.pageAmount) {
                this.pageNow++;
                this.changePage();
            }
        },
        enter: function () {
            var patrn = /^\d+$/;
            if (this.enterNumber > this.pageAmount || this.enterNumber <= 0) {
                alert("你想前往的页面已超出上限!");
            } else if (!patrn.exec(this.enterNumber)) {
                alert("请输入正确数字!")
            } else {
                console.log(this.enterNumber);
                this.pageNow = this.enterNumber;
                this.changePage();
            }
        },
        changePage: function () {
            // 测试用代码
            // if (this.show == false) {
            //     this.game = [];
            //     var start = (this.pageNow - 1) * this.pageEach;
            //     var end = Math.min(start + this.pageEach, this.recordAmount);
            //     console.log(end);
            //     for (var i = start; i < end; i++) {
            //         this.game.push(this.tests[i]);
            //     }
            // }
            // else {
            //     this.checked = [];
            //     this.umpire = [];
            //     console.log(this.recordAmount);
            //     var start = (this.pageNow - 1) * this.pageEach;
            //     var end = Math.min(start + this.pageEach, this.recordAmount);
            //     for (var i = start; i < end; i++) {
            //         this.umpire.push(this.tests2[i]);
            //     }
            // }

            if (this.show == false) {
                this.game = [];
                var start = (this.pageNow - 1) * this.pageEach;
                var end = Math.min(start + this.pageEach, this.recordAmount);
                var gameidX = [];    //需要请求的比赛id列表
                for (var i = start; i < end; i++) {
                    gameidX.push(this.gameid[i]);
                }
                axios({
                    url: '/Data/game_info',
                    params: {
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
                        } else {
                            alert("获取赛事信息表失败!");
                        }
                    })
            }
            else {
                this.umpire = [];
                var start = (this.pageNow - 1) * this.pageEach;
                var end = Math.min(start + this.pageEach, this.recordAmount);
                var useridX = [];    //需要请求的用户id列表
                for (var i = start; i < end; i++) {
                    useridX.push(this.users[i]);
                }
                axios({
                    url: '/Data/user_info',
                    params: {
                        param: {
                            token: getCookie("token"),
                            Data: {
                                uid: useridX,
                            }
                        }
                    }
                }).then(
                    rep => {
                        if (rep.data.status == "success") {
                            setCookie("token", rep.data.token);
                            this.umpire = rep.data.info;
                        } else {
                            alert("获取用户信息表失败!");
                        }
                    })
            }
        }
        ,
        change: function (gameName, gameID) {   //进入审核裁判员页面
            this.show = true;
            this.gameID = gameID;
            this.gameName = gameName;
            axios({
                url: '/Data/judge_info',
                params: {
                    param: {
                        token: getCookie("token"),
                        Data: {
                            by: "game",
                            gid: this.gameID,
                            verify: false,
                        }
                    }
                }
            }).then(
                rep => {
                    if (rep.data.status == "success") {
                        setCookie("token", rep.data.token);
                        this.users = [];
                        for (info of rep.data.infos) {
                            this.users.push(info.uid);
                        }
                    } else {
                        alert("获取申请裁判的用户id列表失败!");
                    }
                })
            this.recordAmount = this.users.length;
            this.pageAmount = Math.ceil(this.recordAmount / this.pageEach);
            this.pageNow = 1;
            this.changePage();
        },
        back: function () {
            this.show = false;
            this.gameID = "";
            this.gameName = "";
            this.checked = [];
            this.umpire = [];
            this.pageNow = 1;
            this.recordAmount = this.gameid.length;
            this.pageAmount = Math.ceil(this.recordAmount / this.pageEach);
            this.changePage();
        },
        checkAll: function() {
            this.isCheckAll = !this.isCheckAll;
            if(this.isCheckAll == true) {
                this.checked = [];
                for (var i = 0; i < this.umpire.length; i++) {
                    this.checked.push(this.umpire[i].uid);
                }
            }
            else {
                this.checked = [];
            }
            console.log(this.checked.length);
        },
        checkOne: function(uid) {
            var x = this.checked.indexOf(uid);
            if (x>-1) {
                this.checked.splice(x,1)
                console.log(this.checked.length);
            }
            else {
                this.checked.push(uid);
            }
        },
        check: function(uid) {
            this.isCheckAll = (this.checked.length == this.umpire.length);
            var x = this.checked.indexOf(uid);
            console.log(uid+":"+x);
            console.log(this.checked.length);
            if (x>-1)
                return true;
            else
                return false;
        },
        agree: function () {
            if (this.checked.length < 1)
                alert("你未选择任何项目！");
            else {
                isAgree = true;
                axios({
                    url: '/Data/judge_verify',
                    params: {
                        param: {
                            token: getCookie("token"),
                            Data: {
                                gid:this.gameID,
                                checked:this.checked,
                                verify:true,
                            }
                        }
                    }
                }).then(
                    rep=>{
                        if(rep.data.status=="success"){
                            setCookie("token",rep.data.token);
                            alert("成功通过相应用户的申请！");
                            location.reload(true);
                        } else{
                            alert("审核失败!");
                        }
                    }
                    ,
                    rep=>{
                        alert("抱歉，网页当前不可用");
                        console.log(rep)
                    })
            }
        },
        disagree: function () {
            if (this.checked.length < 1)
                alert("你未选择任何项目！");
            else {
                isAgree = false;
                axios({
                    url: '/Data/verifyumpire',
                    params: {
                        param: {
                            token: getCookie("token"),
                            Data: {
                                gid:this.gameID,
                                checked:this.checked,
                                verify:false,
                            }
                        }
                    }
                }).then(
                    rep=>{
                        if(rep.data.status=="success"){
                            setCookie("token",rep.data.token);
                            alert("成功取消相应用户的裁判资格！！");
                            location.reload(true);
                        } else{
                            alert("取消失败!");
                            location.reload(true);
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

var checkUmpire = new Vue({    //某裁判所负责的项目信息，可以用于审核裁判申请
    el: "#checkUmpire",
    data: {
        umpire:[],  //当前应该显示的用户信息
        game: [],   //当前应该显示的某裁判负责的项目信息
        gameid: [],  //用户从后端获取的所有比赛id
        gameNow: [],    //当前应该展示的比赛信息
        tests: [
            {gameName: "男子100米", gameID: "001"},
            {gameName: "女子100米", gameID: "002"},
            {gameName: "男子100米", gameID: "003"},
            {gameName: "男子1500米", gameID: "004"},
        ],
        tests2: [
            {uid:"001",id:"1243",realName:"Tony",gender:"F"},
            {uid:"002",id:"1243",realName:"Tony",gender:"M"},
            {uid:"003",id:"1243",realName:"Tony",gender:"F"},
            {uid:"004",id:"1243",realName:"Tony",gender:"M"},
            {uid:"005",id:"1243",realName:"Tony",gender:"M"},
            {uid:"006",id:"1243",realName:"Tony",gender:"M"},
            {uid:"007",id:"1243",realName:"Tony",gender:"M"},
        ],
        gameID: "", //选择取消审核的赛事ID
        gameName: "",   //选择审核的赛事名称
        users:[],   //所有申请但未认证资格的用户id
        show: false, //false展示赛事选择界面，true展示审核裁判界面
        pageNow: 1, //当前所在页面
        pageAmount: 0, //页面总数
        recordAmount: 0,
        pageEach: 2,   //每页显示的记录数
        enterNumber: 0, //用户键盘输入的页码数
        isAgree: false,
        isCheckAll: false,
        checked: [], //选择的用户id
    },
    created() {
        this.start();
    },
    methods: {
        start: function () {
            //测试用代码
            // this.recordAmount = this.tests.length;
            // this.pageAmount = Math.ceil(this.recordAmount / this.pageEach);
            // this.changePage();

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
        prePage: function () {
            if (this.pageNow >= 2) {
                this.pageNow--;
                this.changePage();
            }
        },
        nextPage: function () {
            if (this.pageNow < this.pageAmount) {
                this.pageNow++;
                this.changePage();
            }
        },
        enter: function () {
            var patrn = /^\d+$/;
            if (this.enterNumber > this.pageAmount || this.enterNumber <= 0) {
                alert("你想前往的页面已超出上限!");
            } else if (!patrn.exec(this.enterNumber)) {
                alert("请输入正确数字!")
            } else {
                console.log(this.enterNumber);
                this.pageNow = this.enterNumber;
                this.changePage();
            }
        },
        changePage: function () {
            // 测试用代码
            // if (this.show == false) {
            //     this.game = [];
            //     var start = (this.pageNow - 1) * this.pageEach;
            //     var end = Math.min(start + this.pageEach, this.recordAmount);
            //     console.log(end);
            //     for (var i = start; i < end; i++) {
            //         this.game.push(this.tests[i]);
            //     }
            // }
            // else {
            //     this.checked = [];
            //     this.umpire = [];
            //     console.log(this.recordAmount);
            //     var start = (this.pageNow - 1) * this.pageEach;
            //     var end = Math.min(start + this.pageEach, this.recordAmount);
            //     for (var i = start; i < end; i++) {
            //         this.umpire.push(this.tests2[i]);
            //     }
            // }

            if (this.show == false) {
                this.game = [];
                var start = (this.pageNow - 1) * this.pageEach;
                var end = Math.min(start + this.pageEach, this.recordAmount);
                var gameidX = [];    //需要请求的比赛id列表
                for (var i = start; i < end; i++) {
                    gameidX.push(this.gameid[i]);
                }
                axios({
                    url: '/Data/game_info',
                    params: {
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
                        } else {
                            alert("获取赛事信息表失败!");
                        }
                    })
            }
            else {
                this.umpire = [];
                var start = (this.pageNow - 1) * this.pageEach;
                var end = Math.min(start + this.pageEach, this.recordAmount);
                var useridX = [];    //需要请求的用户id列表
                for (var i = start; i < end; i++) {
                    useridX.push(this.users[i]);
                }
                axios({
                    url: '/Data/user_info',
                    params: {
                        param: {
                            token: getCookie("token"),
                            Data: {
                                uid: useridX,
                            }
                        }
                    }
                }).then(
                    rep => {
                        if (rep.data.status == "success") {
                            setCookie("token", rep.data.token);
                            this.umpire = rep.data.info;
                        } else {
                            alert("获取用户信息表失败!");
                        }
                    })
            }
        }
        ,
        change: function (gameName, gameID) {   //进入审核裁判员页面
            this.show = true;
            this.gameID = gameID;
            this.gameName = gameName;
            axios({
                url: '/Data/judge_unverified',
                params: {
                    param: {
                        token: getCookie("token"),
                        Data: {
                            gid: this.gameID,
                        }
                    }
                }
            }).then(
                rep => {
                    if (rep.data.status == "success") {
                        setCookie("token", rep.data.token);
                        this.users = rep.data.uids;
                    } else {
                        alert("获取申请裁判的用户id列表失败!");
                    }
                })
            this.recordAmount = this.users.length;
            this.pageAmount = Math.ceil(this.recordAmount / this.pageEach);
            this.pageNow = 1;
            this.changePage();
        },
        back: function () {
            this.show = false;
            this.gameID = "";
            this.gameName = "";
            this.checked = [];
            this.umpire = [];
            this.pageNow = 1;
            this.recordAmount = this.gameid.length;
            this.pageAmount = Math.ceil(this.recordAmount / this.pageEach);
            this.changePage();
        },
        checkAll: function() {
            this.isCheckAll = !this.isCheckAll;
            if(this.isCheckAll == true) {
                this.checked = [];
                for (var i = 0; i < this.umpire.length; i++) {
                    this.checked.push(this.umpire[i].uid);
                }
            }
            else {
                this.checked = [];
            }
            console.log(this.checked.length);
        },
        checkOne: function(uid) {
            var x = this.checked.indexOf(uid);
            if (x>-1) {
                this.checked.splice(x,1)
                console.log(this.checked.length);
            }
            else {
                this.checked.push(uid);
            }
        },
        check: function(uid) {
            this.isCheckAll = (this.checked.length == this.umpire.length);
            var x = this.checked.indexOf(uid);
            console.log(uid+":"+x);
            console.log(this.checked.length);
            if (x>-1)
                return true;
            else
                return false;
        },
        agree: function () {
            if (this.checked.length < 1)
                alert("你未选择任何项目！");
            else {
                isAgree = true;
                axios({
                    url: '/Data/judge_verify',
                    params: {
                        param: {
                            token: getCookie("token"),
                            Data: {
                                gid:this.gameID,
                                checked:this.checked,
                                verify:true,
                            }
                        }
                    }
                }).then(
                    rep=>{
                        if(rep.data.status=="success"){
                            setCookie("token",rep.data.token);
                            alert("成功通过相应用户的申请！");
                            location.reload(true);
                        } else{
                            alert("审核失败!");
                            location.reload(true);
                        }
                    }
                    ,
                    rep=>{
                        alert("抱歉，网页当前不可用");
                        console.log(rep)
                    })
            }
        },
        disagree: function () {
            if (this.checked.length < 1)
                alert("你未选择任何项目！");
            else {
                isAgree = false;
                axios({
                    url: '/Data/judge_verify',
                    params: {
                        param: {
                            token: getCookie("token"),
                            Data: {
                                gid:this.gameID,
                                checked:this.checked,
                                verify:false,
                            }
                        }
                    }
                }).then(
                    rep=>{
                        if(rep.data.status=="success"){
                            setCookie("token",rep.data.token);
                            alert("成功拒绝相应用户的申请！");
                            location.reload(true);
                        } else{
                            alert("审核失败!");
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

var checkPlayer = new Vue({    //某裁判所负责的项目信息，可以用于审核运动员报名申请
    el: "#checkPlayer",
    data: {
        player:[],  //当前应该显示的运动员信息
        game: [],   //当前应该显示的某裁判负责的项目信息
        gameid: [],  //用户从后端获取的所有比赛id
        gameNow: [],    //当前应该展示的比赛信息
        tests: [
            {gameName: "男子100米", gameID: "001"},
            {gameName: "女子100米", gameID: "002"},
            {gameName: "男子100米", gameID: "003"},
            {gameName: "男子1500米", gameID: "004"},
        ],
        tests2: [
            {uid:"001",id:"1243",realName:"Tony",gender:"F"},
            {uid:"002",id:"1243",realName:"Tony",gender:"M"},
            {uid:"003",id:"1243",realName:"Tony",gender:"F"},
            {uid:"004",id:"1243",realName:"Tony",gender:"M"},
            {uid:"005",id:"1243",realName:"Tony",gender:"M"},
            {uid:"006",id:"1243",realName:"Tony",gender:"M"},
            {uid:"007",id:"1243",realName:"Tony",gender:"M"},
        ],
        gameID: "", //选择审核的赛事ID
        gameName: "",   //选择审核的赛事名称
        users:[],   //所有报名的用户id
        show: false, //false展示赛事选择界面，true展示审核运动员界面
        pageNow: 1, //当前所在页面
        pageAmount: 0, //页面总数
        recordAmount: 0,
        pageEach: 2,   //每页显示的记录数
        enterNumber: 0, //用户键盘输入的页码数
        isAgree: false,
        isCheckAll: false,
        checked: [], //选择的用户id
    },
    created() {
        this.start();
    },
    methods: {
        start: function () {
            //测试用代码
            // this.recordAmount = this.tests.length;
            // this.pageAmount = Math.ceil(this.recordAmount / this.pageEach);
            // this.changePage();

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
        prePage: function () {
            if (this.pageNow >= 2) {
                this.pageNow--;
                this.changePage();
            }
        },
        nextPage: function () {
            if (this.pageNow < this.pageAmount) {
                this.pageNow++;
                this.changePage();
            }
        },
        enter: function () {
            var patrn = /^\d+$/;
            if (this.enterNumber > this.pageAmount || this.enterNumber <= 0) {
                alert("你想前往的页面已超出上限!");
            } else if (!patrn.exec(this.enterNumber)) {
                alert("请输入正确数字!")
            } else {
                console.log(this.enterNumber);
                this.pageNow = this.enterNumber;
                this.changePage();
            }
        },
        changePage: function () {
            // 测试用代码
            // if (this.show == false) {
            //     this.game = [];
            //     var start = (this.pageNow - 1) * this.pageEach;
            //     var end = Math.min(start + this.pageEach, this.recordAmount);
            //     console.log(end);
            //     for (var i = start; i < end; i++) {
            //         this.game.push(this.tests[i]);
            //     }
            // }
            // else {
            //     this.checked = [];
            //     this.player = [];
            //     console.log(this.recordAmount);
            //     var start = (this.pageNow - 1) * this.pageEach;
            //     var end = Math.min(start + this.pageEach, this.recordAmount);
            //     for (var i = start; i < end; i++) {
            //         this.player.push(this.tests2[i]);
            //     }
            // }

            if (this.show == false) {
                this.game = [];
                var start = (this.pageNow - 1) * this.pageEach;
                var end = Math.min(start + this.pageEach, this.recordAmount);
                var gameidX = [];    //需要请求的比赛id列表
                for (var i = start; i < end; i++) {
                    gameidX.push(this.gameid[i]);
                }
                axios({
                    url: '/Data/game_info',
                    params: {
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
                        } else {
                            alert("获取赛事信息表失败!");
                        }
                    })
            }
            else {
                this.player = [];
                var start = (this.pageNow - 1) * this.pageEach;
                var end = Math.min(start + this.pageEach, this.recordAmount);
                var useridX = [];    //需要请求的用户id列表
                for (var i = start; i < end; i++) {
                    useridX.push(this.users[i]);
                }
                axios({
                    url: '/Data/user_info',
                    params: {
                        param: {
                            token: getCookie("token"),
                            Data: {
                                uid: useridX,
                            }
                        }
                    }
                }).then(
                    rep => {
                        if (rep.data.status == "success") {
                            setCookie("token", rep.data.token);
                            this.player = rep.data.info;
                        } else {
                            alert("获取用户信息表失败!");
                        }
                    })
            }
        }
        ,
        change: function (gameName, gameID) {   //进入审核裁判员页面
            this.show = true;
            this.gameID = gameID;
            this.gameName = gameName;
            axios({
                url: '/Data/judge_unverified',
                params: {
                    param: {
                        token: getCookie("token"),
                        Data: {
                            gid: this.gameID,
                        }
                    }
                }
            }).then(
                rep => {
                    if (rep.data.status == "success") {
                        setCookie("token", rep.data.token);
                        this.users = rep.data.uids;
                    } else {
                        alert("获取申请裁判的用户id列表失败!");
                    }
                })
            this.recordAmount = this.users.length;
            this.pageAmount = Math.ceil(this.recordAmount / this.pageEach);
            this.pageNow = 1;
            this.changePage();
        },
        back: function () {
            this.show = false;
            this.gameID = "";
            this.gameName = "";
            this.checked = [];
            this.player = [];
            this.pageNow = 1;
            this.recordAmount = this.gameid.length;
            this.pageAmount = Math.ceil(this.recordAmount / this.pageEach);
            this.changePage();
        },
        checkAll: function() {
            this.isCheckAll = !this.isCheckAll;
            if(this.isCheckAll == true) {
                this.checked = [];
                for (var i = 0; i < this.player.length; i++) {
                    this.checked.push(this.player[i].uid);
                }
            }
            else {
                this.checked = [];
            }
            console.log(this.checked.length);
        },
        checkOne: function(uid) {
            var x = this.checked.indexOf(uid);
            if (x>-1) {
                this.checked.splice(x,1)
                console.log(this.checked.length);
            }
            else {
                this.checked.push(uid);
            }
        },
        check: function(uid) {
            this.isCheckAll = (this.checked.length == this.player.length);
            var x = this.checked.indexOf(uid);
            console.log(uid+":"+x);
            console.log(this.checked.length);
            if (x>-1)
                return true;
            else
                return false;
        },
        agree: function () {
            if (this.checked.length < 1)
                alert("你未选择任何项目！");
            else {
                isAgree = true;
                axios({
                    url: '/Data/judge_verify',
                    params: {
                        param: {
                            token: getCookie("token"),
                            Data: {
                                gid:this.gameID,
                                checked:this.checked,
                                verify:true,
                            }
                        }
                    }
                }).then(
                    rep=>{
                        if(rep.data.status=="success"){
                            setCookie("token",rep.data.token);
                            alert("成功通过相应用户的申请！");
                            location.reload(true);
                        } else{
                            alert("审核失败!");
                            location.reload(true);
                        }
                    }
                    ,
                    rep=>{
                        alert("抱歉，网页当前不可用");
                        console.log(rep)
                    })
            }
        },
        disagree: function () {
            if (this.checked.length < 1)
                alert("你未选择任何项目！");
            else {
                isAgree = false;
                axios({
                    url: '/Data/verifyplayer',
                    params: {
                        param: {
                            token: getCookie("token"),
                            Data: {
                                gid:this.gameID,
                                checked:this.checked,
                                verify:false,
                            }
                        }
                    }
                }).then(
                    rep=>{
                        if(rep.data.status=="success"){
                            setCookie("token",rep.data.token);
                            alert("成功拒绝相应用户的申请！");
                            location.reload(true);
                        } else{
                            alert("审核失败!");
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

var checkScore = new Vue({    //审核成绩
    el: "#checkScore",
    data: {
        game: [ //isReg表示是否已经登记成绩//isCheck表示是否已经审核成绩
        ],   //某裁判负责的所有项目信息
        player: [
        ],
        tests2: [
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
            {gameName:"男子100米",gameID:"001",isReg:false,isCheck:false},
            {gameName:"女子100米",gameID:"002",isReg:true,isCheck:true},
            {gameName:"男子100米",gameID:"003",isReg:true,isCheck:true},
            {gameName:"男子1500米",gameID:"004",isReg:false,isCheck:false},
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
        change: function(gameName, gameID, isReg) {     //进入成绩登记页面
            if (isReg == true) {
                var con = confirm("你是否要强制修改"+gameName+"的成绩？");
                if (con == false)
                    return;
            }
            this.show = true;
            this.gameID = gameID;
            this.gameName = gameName;
            axios({     //向后端请求当前赛事参赛选手的信息
                url: '/Data/playerinfo',
                params: {
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
                        this.player = rep.data.player;
                    } else{
                        alert("获取用户数据表失败!");
                    }
                })
        },
        back: function(){
            this.show = false;
            this.gameID = "";
            this.gameName = "";
            this.scoreType = "";
        },
        agree: function () {
            axios({     //向后端请求当前赛事参赛选手的信息
                url: '/Data/？？',
                params: {
                    param: {
                        token: getCookie("token"),
                        Data: {
                            gameId: this.gameID,
                            verify: true,
                        }
                    }
                }
            }).then(
                rep=>{
                    if(rep.data.status=="success"){
                        setCookie("token",rep.data.token);
                        alert("成功通过成绩!");
                    } else{
                        alert("成绩通过失败!");
                    }
                })
        },
        disagree: function () {
            axios({     //向后端请求当前赛事参赛选手的信息
                url: '/Data/？？',
                params: {
                    param: {
                        token: getCookie("token"),
                        Data: {
                            gameId: this.gameID,
                            verify: false,
                        }
                    }
                }
            }).then(
                rep=>{
                    if(rep.data.status=="success"){
                        setCookie("token",rep.data.token);
                        alert("成功不通过成绩!");
                    } else{
                        alert("成绩不通过失败!");
                    }
                })
        }
    }
})

var showScore = new Vue({    //发布成绩
    el: "#showScore",
    data: {
        game: [ //isReg表示是否已经登记成绩//isCheck表示是否已经审核成绩//isShow表示是否已经发布成绩
        ],   //某裁判负责的所有项目信息
        player: [
        ],
        tests2: [
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
            {gameName:"男子100米",gameID:"001",isReg:false,isCheck:false},
            {gameName:"女子100米",gameID:"002",isReg:true,isCheck:true},
            {gameName:"男子100米",gameID:"003",isReg:true,isCheck:true},
            {gameName:"男子1500米",gameID:"004",isReg:false,isCheck:false},
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
        change: function(gameName, gameID, isReg) {     //进入成绩登记页面
            if (isReg == true) {
                var con = confirm("你是否要强制修改"+gameName+"的成绩？");
                if (con == false)
                    return;
            }
            this.show = true;
            this.gameID = gameID;
            this.gameName = gameName;
            axios({     //向后端请求当前赛事参赛选手的信息
                url: '/Data/playerinfo',
                params: {
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
                        this.player = rep.data.player;
                    } else{
                        alert("获取用户数据表失败!");
                    }
                })
        },
        back: function(){
            this.show = false;
            this.gameID = "";
            this.gameName = "";
            this.scoreType = "";
        },
        agree: function () {
            axios({     //向后端请求当前赛事参赛选手的信息
                url: '/Data/？？',
                params: {
                    param: {
                        token: getCookie("token"),
                        Data: {
                            gameId: this.gameID,
                            verify: true,
                        }
                    }
                }
            }).then(
                rep=>{
                    if(rep.data.status=="success"){
                        setCookie("token",rep.data.token);
                        alert("成功通过成绩!");
                    } else{
                        alert("成绩通过失败!");
                    }
                })
        },
        disagree: function () {
            axios({     //向后端请求当前赛事参赛选手的信息
                url: '/Data/？？',
                params: {
                    param: {
                        token: getCookie("token"),
                        Data: {
                            gameId: this.gameID,
                            verify: false,
                        }
                    }
                }
            }).then(
                rep=>{
                    if(rep.data.status=="success"){
                        setCookie("token",rep.data.token);
                        alert("成功不通过成绩!");
                    } else{
                        alert("成绩不通过失败!");
                    }
                })
        }
    }
})
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
        test: function() {
            this.realName = "Tony";
        },
        refresh: function () {
            console.log(window.location.pathname);
            axios({
                url: '/Data/user_info',
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
                if (rep.data.permission != "ADMIN") {
                    console.log(rep.data.permission);
                    alert("你无权进入此网页！");
                    window.location("home.html");
                }
            }
        else
            {
                alert("获取用户信息失败！" + rep.data.reason)
            }
        })
        }
    }
})

var createGame = new Vue({  //创建比赛
    el: "#createGame",
    data: {
        name: "",
        time: "",
        startDate: "",
        endDate: "",
        number: "",
        tests3: [
            {sortName:"123",display:"顺序"},
            {sortName:"321",display:"倒序"},
        ],
        sorts: [],   //从后端接收排序方式列表
        sortName: ""
    },
    created() {
        this.start();
    },
    methods: {
        start:function() {
            axios({
                url: '/Data/sort_list',
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
                        this.sorts = rep.data.sorts;
                    }
                    else
                    {
                        alert("获取排序方式列表失败！" + rep.data.reason)
                    }
                })
        },
        check:function () {
            console.log(this.name);
            var TIME = Date.parse(this.time);
            console.log(TIME);
            console.log(this.number);
            var STARTDATE = Date.parse(this.startDate);
            console.log(STARTDATE);
            var ENDDATE = Date.parse(this.endDate);
            console.log(ENDDATE);
            console.log(this.sortName);
            if (this.name=="" || this.time=="" || this.number=="" || this.startDate=="" || this.endDate=="" || this.sortName=="")
                alert("有空项目，请检查!");
            else if(isNaN(TIME)==true || isNaN(STARTDATE)==true || isNaN(ENDDATE) == true) {
                alert("请检查时间是否选择正确！")
            }
            else if(STARTDATE > ENDDATE || TIME < ENDDATE)
                alert("请填写正确的报名时间");
            else {
                axios({
                    url:'/Data/game_create',
                    params: {
                        param: {
                            token: getCookie("token"),
                            Data:{
                                name: this.name,
                                time: TIME,
                                number: this.number,
                                startDate: STARTDATE,
                                endDate: ENDDATE,
                                sortType: this.sortName,
                            }
                        }
                    }
                }).then(
                    rep=>{
                    if(rep.data.status=="success"){
                    setCookie("token",rep.data.token);
                    alert("创建比赛成功！");
                    location.reload(true);
                } else{
                    alert("创建比赛失败"+rep.data.reason)
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
        gameID: "",
        gameName: "",
        type: "",
    },
    methods: {
        check:function () {
            console.log(this.umpireName);
            console.log(this.UID);
            console.log(this.gameName);
            console.log(this.type);
            if (this.type!="主裁判" && (this.UID=="" || this.gameName=="" || this.umpireName=="" || this.gameID == ""))
                alert("有空项目，请检查!");
            else if(this.type=="主裁判" && this.gameID!="")
                alert("主裁判无需指定项目！");
            else if(this.type=="主裁判" && (this.UID=="" || this.umpireName==""))
                alert("有空项目，请检查!");
            else if(this.type!="主裁判" && this.type!="项目裁判")
                alert("请填写正确的裁判类型");
            else if (this.type == "主裁判"){
                axios({
                    url:'/Data/admin_appointMainJudge',
                    params: {
                        param: {
                            token: getCookie("token"),
                            Data:{
                                uid: this.UID,
                                appoint: true
                            }
                        }
                    }
                }).then(
                    rep=>{
                    if(rep.data.status=="success"){
                    setCookie("token",rep.data.token);
                    alert("任命主裁判成功！");
                    location.reload(true);
                } else{
                    alert("任命失败"+rep.data.reason);
                }
            }
                    ,
                    rep=>{
                        alert("抱歉，网页当前不可用");
                        console.log(rep)
                    })
            }
            else {
                axios({
                    url:'/Data/admin_appointProjectJudge',
                    params: {
                        param: {
                            token: getCookie("token"),
                            Data:{
                                gid: this.gameID,
                                uid: this.UID,
                                appoint: true,
                            }
                        }
                    }
                }).then(
                    rep=>{
                        if(rep.data.status=="success"){
                            setCookie("token",rep.data.token);
                            alert("任命项目裁判成功！");
                            location.reload(true);
                        } else{
                            alert("任命失败"+rep.data.reason);
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
                        // alert("获取裁判员负责的项目id列表失败!");
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
                var start = (this.pageNow-1) * this.pageEach;
                var end = Math.min(start+this.pageEach, this.recordAmount);
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
                    rep=>{
                        if(rep.data.status=="success"){
                            setCookie("token",rep.data.token);
                            this.game = rep.data.info;
                            for (var i = 0; i < this.game.length; i++) {
                                this.game[i].time = formatTime(this.game[i].time, 'Y-M-D h:m:s');
                            }
                        } else{
                            alert("获取比赛数据表失败!");
                        }
                    })
            }
            else {
                this.umpire = [];
                var start = (this.pageNow - 1) * this.pageEach;
                var end = Math.min(start + this.pageEach, this.recordAmount);
                var t;
                for (var i = start; i < end; i++) {
                    var useridX = [];    //需要请求的用户id列表
                    useridX.push(this.users[i].uid);
                    axios({
                        url: '/Data/user_info',
                        method: 'POST',
                        data: {
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
                                var obj = {id:rep.data.info[0].id, uid:rep.data.info[0].uid, realName:rep.data.info[0].realName, gender:rep.data.info[0].gender, type:""};
                                this.umpire.push(obj);
                                this.umpireType();
                            } else {
                                alert("获取用户信息表失败!");
                            }
                        })
                }
            }
        }
        ,
        umpireType: function () {
            for (var i = 0; i < this.umpire.length; i++) {
                for (var j = 0; j < this.umpire.length; j++) {
                    if (this.umpire[i].uid == this.users[j].uid) {
                        this.umpire[i].type = this.users[j].type;
                        break;
                    }
                }
            }
        }
        ,
        change: function (gameName, gameID) {   //进入取消裁判员页面
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
                            id: this.gameID,
                            verify: true,
                        }
                    }
                }
            }).then(
                rep => {
                    if (rep.data.status == "success") {
                        setCookie("token", rep.data.token);
                        this.users = [];
                        for (info of rep.data.infos) {
                            var obj = {uid:info.uid, type:info.type};
                            this.users.push(obj);
                        }
                        this.recordAmount = this.users.length;
                        this.pageAmount = Math.ceil(this.recordAmount / this.pageEach);
                        this.pageNow = 1;
                        this.changePage();
                    } else {
                        alert("获取当前项目裁判的id列表失败!");
                    }
                })
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
        disagree: function () {
            if (this.checked.length < 1)
                alert("你未选择任何项目！");
            else {
                isAgree = false;
                axios({
                    url: '/Data/judge_verify',
                    method: 'POST',
                    data: {
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

var checkUser = new Vue({ //审查用户资格
    el: "#checkUser",
    data: {
        player: [
            ],//当前应该展示的用户信息
        tests: [
            {uid:"001",id:"1243",realName:"Tony",gender:"F"},
            {uid:"002",id:"1243",realName:"Tony",gender:"M"},
            {uid:"003",id:"1243",realName:"Tony",gender:"F"},
            {uid:"004",id:"1243",realName:"Tony",gender:"M"},
            {uid:"005",id:"1243",realName:"Tony",gender:"M"},
            {uid:"006",id:"1243",realName:"Tony",gender:"M"},
            {uid:"007",id:"1243",realName:"Tony",gender:"M"},
        ],
        users:[],   //所有申请但未认证资格的用户id
        isCheckAll: false,
        checked: [],
        isAgree: false,
        pageNow: 1, //当前所在页面
        pageAmount: 0, //页面总数
        recordAmount: 0,
        pageEach: 10,   //每页显示的记录数
        enterNumber:0, //用户键盘输入的页码数
    },
    created(){
        this.start();
    },
    methods: {
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
        start: function() {
            //测试用代码
            // this.recordAmount = this.tests.length;
            // this.pageAmount = Math.ceil(this.recordAmount/this.pageEach);
            // this.changePage();
            axios({
                url: '/Data/admin_unverifiedUsers',
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
                        this.users = rep.data.users;
                        this.recordAmount = this.users.length;
                        this.pageAmount = Math.ceil(this.recordAmount/this.pageEach);
                        this.changePage();
                    } else{
                        alert("获取用户资格申请表失败!"+rep.data.reason);
                    }
                })
        },
        changePage: function() {
            //测试用代码
            // this.checked = [];
            // console.log("2222");
            // this.player = [];
            // var start = (this.pageNow-1) * this.pageEach;
            // var end = Math.min(start+this.pageEach, this.recordAmount);
            // console.log(end);
            // for (var i = start; i < end; i++) {
            //     this.player.push(this.tests[i]);
            // }
            // console.log(this.player.length);
            var start = (this.pageNow-1) * this.pageEach;
            var end = Math.min(start+this.pageEach, this.recordAmount);
            var usersX = [];    //需要请求的用户id列表
            for (var i = start; i < end; i++) {
                usersX.push(this.users[i]);
            }
            console.log(usersX);
            axios({
                url: '/Data/user_info',
                method: 'POST',
                data: {
                    param:{
                        token: getCookie("token"),
                        Data: {
                            uid: usersX,
                        }
                    }
                }
            }).then(
                rep=>{
                    if(rep.data.status=="success"){
                        setCookie("token",rep.data.token);
                        this.checked = [];
                        this.player = rep.data.info;
                        for (var i = 0; i < this.player.length; i++) {
                            if (this.player[i].gender == "F")
                                this.player[i].gender = "女";
                            else if (this.player[i].gender == "M")
                                this.player[i].gender = "男";
                        }
                    } else{
                        alert("获取用户请求表失败!" + rep.data.reason);
                    }
                })
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
                this.isAgree = true;
                axios({
                    url: '/Data/admin_verifyUser',
                    method: 'POST',
                    data: {
                        param: {
                            token: getCookie("token"),
                            Data: {
                                checked:this.checked,
                                isAgree:this.isAgree
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
                this.isAgree = false;
                axios({
                    url: '/Data/admin_verifyUser',
                    method: 'POST',
                    data: {
                        param: {
                            token: getCookie("token"),
                            Data: {
                                checked:this.checked,
                                isAgree:this.isAgree,
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

const signUpButton = document.getElementById('signUp');
const signInButton = document.getElementById('signIn');
const container = document.getElementById('container');


signUpButton.addEventListener('click', () => {
    container.classList.add("right-panel-active");
});


signInButton.addEventListener('click', () => {
    container.classList.remove("right-panel-active");
});

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
    //token非空则隐式登录
    //axios({
    //})
}

var login = new Vue({
    el: "#log",
    data: {
        userName: "",
        password: ""
    },
    methods: {
        check:function () {
            console.log(this.userName+this.password);
            axios({
                url:'/Data/user_login',
                params: {
                    param: {
                        Data:{
                            userName: this.userName,
                            password: this.password
                        }
                    }
                }
            }).then(
                rep=>{
                    if(rep.data.status=="success"){
                        setCookie("token",rep.data.token);
                        alert("登陆成功")
                        window.location.href="home.html";
                    }
                    else{
                        alert("登陆失败"+rep.data.reason)
                    }
                }
                ,
                rep=>{
                    alert("抱歉，网页当前不可用");
                })
        }
    }
})

var register = new Vue({
    el: "#reg",
    data: {
        realName: "",
        gender: "",
        id: "",
        userName: "",
        password: "",
        password2: "",
        isread:false
    },
    methods: {
        check:function () {
            // console.log(this.password.length);
            // console.log("33");
            // var now = Date();
            // var exitTime= now.getTime() + 3000;
            // while(true){
            //     var now = Date();
            //     if(now.getTime()>exitTime)
            //         break;
            // }
            var len = this.password.length;
            if (this.isread == false)
                alert("您还未同意相关服务条款和隐私政策！")
            else if(this.realName=="" || this.gender=="" || this.id=="" || this.userName=="" || this.password=="" || this.password2 == "")
                alert("有项目为空，请检查！")
            else if(len<6 || len>16)
                alert("密码长度不符！")
            else if(this.password != this.password2)
                alert("两次输入的密码不一致！")
            else {
                axios({
                    url:'/Data/user_register',
                    params: {
                        param: {
                            Data:{
                                realName: this.realName,
                                gender: this.gender,
                                id: this.id,
                                userName: this.userName,
                                password: this.password,
                            }
                        }
                    }
                }).then(
                    rep=>{
                        console.log(rep)
                        console.log(rep.data.status)
                        if(rep.data.status=="success"){
                            setCookie("token",rep.data.token);
                            alert("注册成功！");
                            window.location.href="home.html";
                        } else{
                            alert("注册失败"+rep.data.reason)
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
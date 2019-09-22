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
function getCookie(c_name)
{
    if (document.cookie.length>0)
    {
        c_start=document.cookie.indexOf(c_name + "=")
        if (c_start!=-1)
        {
            c_start=c_start + c_name.length+1
            c_end=document.cookie.indexOf(";",c_start)
            if (c_end==-1) c_end=document.cookie.length
            return unescape(document.cookie.substring(c_start,c_end))
        }
    }
    return ""
}

//设置cookie的函数
function setCookie (name, value)

{
    //设置名称为name,值为value的Cookie
    console.log('setCookie')
    var expdate = new Date();   //初始化时间
    expdate.setTime(expdate.getTime() + 30 * 60 * 1000);   //时间
    document.cookie = name+"="+value+";expires="+expdate.toGMTString()+";path=/";

    //即document.cookie= name+"="+value+";path=/";   时间可以不要，但路径(path)必须要填写，因为JS的默认路径是当前页，如果不填，此cookie只在当前页面生效！~
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
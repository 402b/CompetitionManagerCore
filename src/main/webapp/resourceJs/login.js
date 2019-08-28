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

//登录函数
function login(){
    var userName = document.getElementById("userName").value;
    var password = document.getElementById("password").value;
    axios({
        url:'/Data/login',
        params: {
            param: {
                Data:{
                    userName: userName,
                    password: password
                }
            }
        }
    }).then(
        rep=>{
            if(rep.status=="success"){
                setCookie("token",rep.token);
                window.open("/home"/* 主页面 */);
                alert("登录成功！");
            }
            else{
                alert("登陆失败"+rep.reason)
            }
        }
        ,
        rep=>{
            alert("抱歉，网页当前不可用");
        })
}

//注册函数
function register(){
    var userName = document.getElementById("userName").value;
    var password = document.getElementById("password").value;
    axios({
        url:'/Data/register',
        params:{
            param: {
                Data:{
                    userName: userName,
                    password: password
                }
            }
        }
    }).then(
        rep=>{
            alert(rep)
        })
}

//检查是否有token存在
var token = getCookie("token");
if(token!=null){

}



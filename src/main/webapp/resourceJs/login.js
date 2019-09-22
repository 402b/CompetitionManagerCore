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
    //window.open("./home.html")
}



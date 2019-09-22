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


function toLocalString(date) {
    return date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate()+"   "+date.getHours()
        +":"+date.getMinutes()+":"+date.getSeconds();
}

function getUserName(uid) {
    axios({
        method:'POST',
        url:"/Data/user_info",
        data:{

            param:{
                token:getCookie("token"),
                Data:{
                    uid:[uid],
                }
            }
        }
    }).then(
        rep=>{console.log("请求uid为 "+uid+"的人信息成功");//console.log(rep);
            console.log(rep.data);
            return rep}
        ,
        rep=>{console.log("请求个人信息失败")}
    )
}


function check(){
    if(!getCookie("token")){
        alert("请先登录");
        window.location.href = "loginX.html";
    }

}

check();





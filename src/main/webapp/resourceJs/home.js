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




